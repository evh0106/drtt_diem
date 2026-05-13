package com.inisteel.cim.yd.jsp.coiljsp.session;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.property.PropertyService;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;
import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.jms.JmsQueueSender;
import com.inisteel.cim.common.util.CmUtil;
import com.inisteel.cim.yd.common.dao.ydCarFtmvMtlDao.YdCarFtmvMtlDao;
import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
import com.inisteel.cim.yd.common.dao.ydEqpDao.YdEqpDao;
import com.inisteel.cim.yd.common.dao.ydSchRuleDao.YdSchRuleDao;
import com.inisteel.cim.yd.common.dao.ydStkColDao.YdStkColDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.dao.ydTcarFtmvMtlDao.YdTcarFtmvMtlDao;
import com.inisteel.cim.yd.common.dao.ydTcarSchDao.YdTcarSchDao;
import com.inisteel.cim.yd.common.dao.ydWrkHistDao.YdWrkHistDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookDao.YdWrkbookDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookMtlDao.YdWrkbookMtlDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdConst;
import com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDAO;
import com.inisteel.cim.yd.jsp.common.CmnUtil;
import com.inisteel.cim.yd.jsp.common.YDComUtil;
import com.inisteel.cim.yd.jsp.common.YDDataUtil;
import com.inisteel.cim.yd.common.dao.ptStlFrtoMoveDao.PtStlFrtoMoveDao;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;

 
/**
 * 이 클래스는 업무 화면의 메뉴를 관리하기 위한 Facade Session EJB클래스입니다.
 * 클래스명 : 소재야드 Facade Class
 *
 * @ejb.bean name="CoilJspFaEJB" jndi-name="CoilJspFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class CoilJspFaEJBBean extends BaseSessionBean {

	private YdUtils log = new YdUtils();
	private YdUtils ydUtils = new YdUtils();
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();	
	YDComUtil   ydComUtil = new YDComUtil();
	private String szSessionName = getClass().getName();
	private EJBConnector ydEjbCon = new EJBConnector("default", this);
	
		

	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	
	/**
	 * 설비사양설정 (조회)x
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	
	public GridData getCoilYdEqpSetSpec(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdEqpSetSpec";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);			
			ejbConn   = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdEqpSetSpec", inRecord);
			gdRes     = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilYdEqpSetSpec
	
	 
	/**
	 * 설비사양설정 (수정)x
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilYdEqpSetSpec(GridData gdReq) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName="updCoilYdEqpSetSpec";
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			
			
			//JDTORecord [] inRecord = CmUtil.genJDTORecordSet(gdReq);
			//fix 20081230 수정 - Param 정보도 추가로 가져오기 위함 
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);

			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			ejbConn.trx("updCoilYdEqpSetSpec", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		return gdRes;
	}  //end of updCoilYdEqpSetSpec
	
	
	/**
	 * 설비사양설정 (삭제)x
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData delCoilYdEqpSetSpec(GridData gdReq) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName="delCoilYdEqpSetSpec";
					
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			ejbConn.trx("delCoilYdEqpSetSpec", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		return gdRes;
	}  //end of delCoilYdEqpSetSpec
	
	
	

	/**
	 * 설비사양설정 (등록)x
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData insCoilYdEqpSetSpec(GridData gdReq) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName="insCoilYdEqpSetSpec";
					
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			ejbConn.trx("insCoilYdEqpSetSpec", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return gdRes;
	}  //end of insCoilYdEqpSetSpec	
	
	
	/**
	 * 코일야드 스케줄 기동 (조회)x
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	
	public GridData getCoilYdSchStirMgt(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdSchStirMgt";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);			
			ejbConn   = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdSchStirMgt", inRecord);
			gdRes     = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilYdEqpSetSpec
	
	/**
	 * 코일 야드 스케줄 기동관리 (수정)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilYdSchStirMgt(GridData gdReq) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName="updCoilYdSchStirMgt";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			ejbConn.trx("updCoilYdSchStirMgt", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		return gdRes;
	}  //end of updCoilYdSchStirMgt

	/**
	 *  코일 야드 슬라브야드 차량진행관리 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdCarWorkList(GridData gdReq) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdCarWorkList";	
		
		GridData      returnGrid = null;	
		EJBConnector  ejbConn    = null;
		JDTORecordSet recordSet  = null;
		GridData      gdRes      = null;
		
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
			returnGrid = OperateGridData.cloneResponseGridData(gdReq);			
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdCarWorkList", inRecord);				

			//공통 PM 에서 날짜를 처리하기 위하여  필요한 UTil
		     gdRes = CmnUtil.jdtoRecordToGridData(returnGrid, recordSet, gdReq);		     
		     ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	
	/**
	 * 코일 야드차량 상차정보 조회 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	
	
	public GridData getCoilYdCarLiftInfo(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdCarLiftInfo";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdCarLiftInfo", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	
	/**
	 * 저장위치 좌표설정화면 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	
	public GridData getCoilYdStkPosSet(GridData inDto) throws DAOException {
		//LOG
		String szMsg         = "";
		String szMethodName  = "getCoilYdStkPosSet";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
			
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdStkPosSet", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);			
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	
	/**
	 * 저장품 데이터를 조회/검색하는 메소드이다.(검색어, 업무영역코드)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getYdStock(GridData inDto) throws DAOException {
		
		//LOG 
		String szMsg="";
		String szMethodName="getYdStock";		

		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);

			recordSet = (JDTORecordSet) ejbConn.trx("getYdStock",
					new Class[] { JDTORecord.class }, new Object[] { inRecord });
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		}catch(Exception e){			
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");

		return gdRes;
	}
	
	/**
	 * 코일 공통을 조회/검색하는 메소드이다.(검색어, 업무영역코드)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getPtCoilComm(GridData inDto) throws DAOException {
		
		//LOG
		String szMsg="";
		String szMethodName="getPtCoilComm";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);

			recordSet = (JDTORecordSet) ejbConn.trx("getPtCoilComm",
					new Class[] { JDTORecord.class }, new Object[] { inRecord });

			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");

		return gdRes;
	}
		
	/**
	 * 저장위치 조회  메소드.()
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	
	public GridData getCoilYdStkLocInfo(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdStkLocInfo";	
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			recordSet = (JDTORecordSet) ejbConn.trx("getCoilYdStkLocInfo",
					new Class[] { JDTORecord.class }, new Object[] { inRecord });
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){			
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);	
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	
	
	
	/**
	 * 저장위치 좌표설정화면 베드  조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData getCoilYdStkPosSetBed(GridData inDto) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName="getCoilYdStkPosSetBed";	
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdStkPosSetBed", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	
	
	/**
	 * 저장위치 좌표설정화면 열 수정 
	 * SJH 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	
	public GridData updCoilYdStkPosSet(GridData inDto) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName="updCoilYdStkPosSet";
		String szRtnMsg = "";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
			
		try{
			//JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);

			szRtnMsg = (String)ejbConn.trx("updCoilYdStkPosSet",
						new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage(szRtnMsg);
		return gdRes;
	} // end of updCoilYdStkPosSet
	
	
	
	/**
	 * 저장위치 좌표설정화면 열 등록 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	
	public GridData insCoilYdStkPosSet(GridData inDto) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName="insCoilYdStkPosSet";	
		
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		
		try{
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			ejbConn.trx("insCoilYdStkPosSet", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	
	
	
	/**
	 * 저장위치 좌표설정화면 열 삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	
	public GridData delCoilYdStkPosSet(GridData inDto) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName="delCoilYdStkPosSet";	
		
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;		
		try{
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			ejbConn.trx("insCoilYdStkPosSet",new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} // end of delCoilYdStkPosSet	
	
	
	/**
	 * 저장위치 좌표설정화면 BED  등록 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData insCoilYdStkPosSetBed(GridData inDto) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName="insCoilYdStkPosSetBed";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		
		try{
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			ejbConn.trx("insCoilYdStkPosSetBed", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}				
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	
	
	/**
	 * 저장위치 좌표설정화면 BED 수정 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData updCoilYdStkPosSetBed(GridData inDto) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName="updCoilYdStkPosSetBed";
		JDTORecord outRecord   	= JDTORecordFactory.getInstance().create();
		GridData gdRes = null;
		EJBConnector ejbConn = null; 
		try{
			//JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);

			gdRes = OperateGridData.cloneResponseGridData(inDto);
			
			outRecord = (JDTORecord)ejbConn.trx("updCoilYdStkPosSetBed",
						new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			String sRTN_CD	= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
			String sRTN_MSG	= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
			if ("0".equals(sRTN_CD)) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
				return gdRes;	
			}	
			gdRes.setMessage(sRTN_MSG);		
			
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	
	/**
	 *  크레인작업실적LIST (야드관리 > 코일소재야드 > 크레인실적관리 > 크레인작업실적LIST조회)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilCrnWrkWrList(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilCrnWrkWrList";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szMsg = "JSP-FACADE [크레인작업실적LIST조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn   = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilCrnWrkWrList", inRecord);
			gdRes     = CmUtil.genGridData(inDto , recordSet);
			
			szMsg = "JSP-FACADE [크레인작업실적LIST조회] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	
	/**
	 *  설비 정비이력 조회 (야드관리 > 코일소재야드 > 크레인실적관리 > 설비 정비이력 관리)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getEqpMaintHist(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getEqpMaintHist";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szMsg = "JSP-FACADE [설비 정비이력 조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn   = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getEqpMaintHist", inRecord);
			gdRes     = CmUtil.genGridData(inDto , recordSet);
			
			szMsg = "JSP-FACADE [설비 정비이력 조회] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	
	/**
	 *  크레인 작업관리 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdCrnWorkMgt(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdCrnWorkMgt";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn   = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdCrnWorkMgt", inRecord);
			gdRes     = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	
	/**
	 *  크레인 작업관리 조회 (화면:크레인작업관리)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdCrnWorkMgt_New(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdCrnWorkMgt_New";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szMsg = "JSP-FACADE [크레인작업관리 조회 - 화면:크레인작업관리] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn   = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdCrnWorkMgt_New", inRecord);
			//gdRes     = CmUtil.genGridData(inDto , recordSet);
			gdRes     = CmUtil.genResGridData(inDto , recordSet);

			szMsg = "JSP-FACADE [크레인작업관리 조회 - 화면:크레인작업관리] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}

		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	
	
	
	
	/**
	 *  코일 야드 베드금지 / 해제 조회 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	
	
	public GridData getCoilYdBedBanCnc(GridData inDto) throws DAOException {
		//LOG
		String szMsg          = "";
		String szMethodName   = "getCoilYdBedBanCnc";		
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdBedBanCnc1", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}	
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	
	

	/**
	 *  코일 야드 베드금지 / 해제 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilYdBedBanCnc(GridData inDto) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName="updCoilYdBedBanCnc";	
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			ejbConn.trx("updCoilYdBedBanCnc", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	
	
	/**
	 *  코일 야드 크레인 설비내용 조회 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	
	
	public GridData getCoilYdCrnStsSet(GridData inDto) throws DAOException {

		//LOG
		String szLogMsg = null;
		String szRtnMsg = null;
		String szMethodName="getCoilYdCrnStsSet";	
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("SlabJspSeEJB", "getslabYdCrnStsSetID", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			//조회성공메세지를 설정
			gdRes.setMessage(YdConstant.RETN_CD_SUCCESS);
		}catch(DAOException e) {
			//에러메시지를 설정
			gdRes.setMessage(e.getMessage());
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		return gdRes;
		
		
	} //getCoilYdCrnStsSet
	
	/**
	 *  코일 야드 코일야드 차량진행관리 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdCarWorkListAll(GridData inDto) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName="getCoilYdCarWorkListAll";	
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdCarWorkListAll", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	
	
	
	/**
	 *  코일 야드 크레인 상태 수정(UPDATE) 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilYdCrnStsSetCrnStat(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "updCoilYdCrnStsSetCrnStat";	
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			ejbConn.trx("updCoilYdCrnStsSetCrnStat", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;	
	}
	

	/**
	 *  코일 야드 크레인 운전모드 수정(UPDATE) 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilYdCrnStsSetCrnMode(GridData inDto) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName="updCoilYdCrnStsSetCrnMode";
		
		GridData     gdRes   = null;
		EJBConnector ejbConn = null;
		try{
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			ejbConn.trx("updCoilYdCrnStsSetCrnMode", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);			
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;	
	}
	
	/**
	 *  코일 야드 크레인 작업모드 수정(UPDATE) 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilYdCrnStsSetCrnMode2(GridData inDto) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName="updCoilYdCrnStsSetCrnMode2";
		
		GridData     gdRes   = null;
		EJBConnector ejbConn = null;
		try{
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			ejbConn.trx("updCoilYdCrnStsSetCrnMode2", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);			
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;	
	}
	

	/**
	 *  코일 야드 크레인 작업실적 응답 (SEND) 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData sendCoilYdCrnAnswer(GridData inDto) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName="sendCoilYdCrnAnswer";
		
		GridData     gdRes   = null;
		EJBConnector ejbConn = null;
		try{
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			ejbConn.trx("sendCoilYdCrnAnswer", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);			
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;	
	}
	
	/**
	 *  코일 야드 대차 상태 수정(UPDATE) 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilYdTcarStsSetStat(GridData inDto) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName="updCoilYdTcarStsSetStat";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			ejbConn.trx("updCoilYdTcarStsSetStat", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	
	}
	

	/**
	 *  코일 야드 대차  운전모드 수정(UPDATE) 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilYdTcarStsSetMode(GridData inDto) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName="updCoilYdTcarStsSetMode";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			ejbConn.trx("updCoilYdTcarStsSetMode", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	
	}
	
	
	
	/**
	 *  코일 야드 차량정지위치상태등록 조회 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdCarStopLocStsReg(GridData inDto) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName="getCoilYdCarStopLocStsReg";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdCarStopLocStsReg", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	
	
	
	/**
	 *  코일 야드 차량정지상태 등록 수정 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilYdCarStopLocStsReg(GridData inDto) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName="updCoilYdCarStopLocStsReg";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			ejbConn.trx("updCoilYdCarStopLocStsReg", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	
	}

	
	
//	/**
//	 *  SPAN별 저장위치관리  getMtlUnitMvstkReg
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param inDto
//	 * @return
//	 * @throws DAOException
//	 * @throws JDTOException
//	 */
//	
//	public GridData getCoilYdStkLocInfoList(GridData inDto) throws DAOException {
//		//LOG
//		String szMsg="";
//		String szMethodName="getCoilYdStkLocInfoList";
//		
//		GridData gdRes = null;
//		EJBConnector ejbConn = null;
//		JDTORecordSet recordSet = null;
//		try{
//			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
//			ejbConn = new EJBConnector("default", this);
//			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdStkLocInfoList", inRecord);
//			gdRes = CmUtil.genGridData(inDto , recordSet);
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//
//		}catch(Exception e){
//			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
//		}
//		gdRes.setStatus("true");
//		gdRes.setMessage("Success");
//		return gdRes;
//	} //end of getCoilYdStkLocInfoList
//	

	/**
	 * EVENT별 작업재료 조회 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData getCoilYdEventWorkMatRef(GridData inDto) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName="getCoilYdEventWorkMatRef";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdEventWorkMatRef", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilYdEventWorkMatRef
	
	
	/**
	 * 압연지시관리 (조회)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	
	public GridData getCoilYdRollCmdRef(GridData inDto) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName="getCoilYdRollCmdRef";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdRollCmdRef", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilYdRollCmdRef
	
	
	/**
	 * 수불구변경등록 조회 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	
	
	public GridData getCoilYdBedBanCnc2(GridData inDto) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName="getCoilYdBedBanCnc2";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdBedBanCnc2", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}	
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}

	/**
	 *  수불구변경등록 (수정) 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilYdBedBanCnc2(GridData inDto) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName="updCoilYdBedBanCnc2";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			ejbConn.trx("updCoilYdBedBanCnc2", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	
	
	/**
	 * 야드관리 > 코일소재야드 > 설비/차량관리 > 이송대상재관리   (이송대상목록 조회)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdMvMtlList(GridData inDto) throws DAOException {
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		String szMethodName="getCoilYdMvMtlList";
		
		try{
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			gdRes = (GridData)ejbConn.trx("getCoilYdMvMtlList", new Class[] { GridData.class }, new Object[] { inDto });
			
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		
		return gdRes;
	}
	
	/**
	 * 스케줄코드별기준 조회 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdSchCdInfoList(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdSchCdInfoList";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdSchCdInfoList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	
	/**
	 * 스케줄코드별기준 등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData insCoilYdSchCdInfoList(GridData gdReq) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "insCoilYdSchCdInfoList";
					
		GridData     gdRes   = null;
		EJBConnector ejbConn = null;
		try{
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			ejbConn.trx("insCoilYdSchCdInfoList", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return gdRes;
	}  //end of insCoilYdSchCdInfoList
	
	/**
	 * 스케줄코드별기준 수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilYdSchCdInfoList(GridData gdReq) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName="updCoilYdSchCdInfoList";
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		
		try{
			//JDTORecord [] inRecord = CmUtil.genJDTORecordSet(gdReq);
			//fix 20081230 수정 - Param 정보도 추가로 가져오기 위함 
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);
			
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			ejbConn.trx("updCoilYdSchCdInfoList", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		return gdRes;
	}  //end of updCoilYdSchCdInfoList

	
	
	/**
	 * 야드동정보 조회 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdBaySetList(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdBaySetList";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdBaySetList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	
	/**
	 * 야드적치열정보 조회 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdColStsSetInfo(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdColStsSetInfo";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdColStsSetInfo", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}	
	
	
	/**
	 * 야드적치열정보 수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilYdColStsSetInfo(GridData gdReq) throws DAOException {
		//LOG
		String szMsg         = "";
		String szMethodName  = "updCoilYdColStsSetInfo";
		GridData gdRes       = null;
		EJBConnector ejbConn = null;
		try{
			//JDTORecord [] inRecord = CmUtil.genJDTORecordSet(gdReq);
			//fix 20081230 수정 - Param 정보도 추가로 가져오기 위함 
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);

			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			ejbConn.trx("updCoilYdColStsSetInfo", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		return gdRes;
	}  //end of updCoilYdSchCdInfoList
	
	/**
	 * 열별 저장위치 정보 조회 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdColStkPosList(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdColStkPosList";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdColStkPosList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	
	/**
	 * 열별 저장위치 정보 조회(단 별) 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdColStkPosLyrGpList(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdColStkPosLyrGpList";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdColStkPosLyrGpList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	
	/**
	 * 열별 저장위치 정보 등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData insCoilYdColStkPosList(GridData gdReq) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "insCoilYdColStkPosList";
					
		GridData     gdRes   = null;
		EJBConnector ejbConn = null;
		try{
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			ejbConn.trx("insCoilYdColStkPosList", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return gdRes;
	}  //end of insCoilYdColStkPosList
	
	/**
	 * 열별 저장위치 정보 수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilYdColStkPosList(GridData gdReq) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName="updCoilYdColStkPosList";
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{

			//JDTORecord [] inRecord = CmUtil.genJDTORecordSet(gdReq);
			//fix 20081230 수정 - Param 정보도 추가로 가져오기 위함 
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);

			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			ejbConn.trx("updCoilYdColStkPosList", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		return gdRes;
	}  //end of updCoilYdColStkPosList

	/**
	 * 야드스케쥴코드와 코드설명 목록 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdSchCdList(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdSchCdList";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdSchCdList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}

	/**
	 * 코일야드 야드동별재고현황 목록 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdBayInvList(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdBayInvList";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdBayInvList", inRecord);
			
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		return gdRes;
	}
	
	/**
	 * 야드관리 > 코일소재야드 > 야드재공관리 > 재료진도별 재공현황  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilMtlProgIdInlnStat(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilMtlProgIdInlnStat";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szMsg = "JSP-FACADE [재료진도별 재공현황 ] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilMtlProgIdInlnStat", inRecord);
			
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		szMsg = "JSP-FACADE [재료진도별 재공현황 ] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		return gdRes;
	}
	/**
	 * 콘베어정보목록 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdConveyorMgt(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdConveyorMgt";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdConveyorMgt", inRecord);
			
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	/**
	 * 콘베어정보목록img 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdConveyorMgtImg(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdConveyorMgtImg";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdConveyorMgtImg", inRecord);
			
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	
	/**
	 * 저장품 저장위치 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdStkPosInfo(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdStkPosInfo";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdStkPosInfo", inRecord);
			
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	
	/**
	 * 저장위치 수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilYdStkPosInfo(GridData gdReq) throws DAOException {
		//LOG
		String szMsg         = "";
		String szMethodName  = "updCoilYdStkPosInfo";
		GridData gdRes       = null;
		EJBConnector ejbConn = null;
		try{
			//JDTORecord [] inRecord = CmUtil.genJDTORecordSet(gdReq);
			//fix 20081230 수정 - Param 정보도 추가로 가져오기 위함 
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			ejbConn.trx("updCoilYdStkPosInfo", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		return gdRes;
	}  //end of updCoilYdStkPosInfo
	
	/**
	 * 저장위치 삭제(현재위치의 재료번호를 NULL로 수정, 스케줄/작업예약 삭제, 차량/대차스케줄 삭제)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData delCoilYdStkPosInfo(GridData gdReq) throws DAOException {
		//LOG
		String 			szMsg         		= "";
		String 			szMethodName 		= "delCoilYdStkPosInfo";
		GridData 		gdRes       		= null;
		EJBConnector 	ejbConn 			= null;
		
		CoilJspDAO		dao					= new CoilJspDAO();
		YdCrnSchDao ydCrnschDao  			= new YdCrnSchDao();
		YdWrkbookMtlDao ydWrkbookMtlDao 	= new YdWrkbookMtlDao();
		JDTORecord 		inRecord   			= JDTORecordFactory.getInstance().create();
		JDTORecord 		outRecord   		= JDTORecordFactory.getInstance().create();
		JDTORecord 		outRecord1  		= JDTORecordFactory.getInstance().create();
		JDTORecord 		inRecord1  			= JDTORecordFactory.getInstance().create();
		
		JDTORecord 		outRecord9   		= JDTORecordFactory.getInstance().create();
		JDTORecord 		inRecord4   		= JDTORecordFactory.getInstance().create();
		JDTORecord 		outRecord5  		= JDTORecordFactory.getInstance().create();
		JDTORecord 		outRecord6  		= JDTORecordFactory.getInstance().create();
		JDTORecord 		outRecord0  		= JDTORecordFactory.getInstance().create();
		
	
		JDTORecord 		recDelPara   		= null;
		JDTORecordSet 	outRdSet	 		= JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecordSet 	rsResult1	 		= JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecordSet 	rsResult2	 		= JDTORecordFactory.getInstance().createRecordSet("YD");
		
		JDTORecord[] 	inRecordarr   		= null;			
		String 			sRTN_CD				= "";
		String 			sRTN_MSG			= "";
		
		String 			sCRANE_SND			= "";
		String 			sYD_CRN_SCH_ID		= "";
		String 			sYD_WRK_PROG_STAT	= "";
		String 			sYD_EQP_ID			= "";
		String 			sYD_SCH_CD			= "";
		String 			sCRANE_WR_SND_YN 	= "";
		
		String 			szCrnSchId 			= "";  // 스케줄 ID
		String 			szSchCd 			= "";  // 스케줄코드
		String          szYD_WBOOK_ID		= "";
		String 			sYD_USER_ID			= "";
		int             intRtnVal = 0;
		String          sYD_WBOOK_ID        = "";
		String sYD_DONG = "";
		try{
			
			JDTORecord [] inRecordReq = ydComUtil.genJDTORecordSet(gdReq);
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			
			//===================================================================================
			// 스케줄 조회 
			recDelPara   = JDTORecordFactory.getInstance().create();
			recDelPara.setField("V_YD_GP", 	gdReq.getParam("YD_GP"));
			recDelPara.setField("V_STL_NO", gdReq.getParam("STL_NO"));
			sYD_USER_ID = gdReq.getParam("YD_USER_ID");
			sYD_DONG = gdReq.getParam("YD_GP");
			
			ydUtils.putLog(szSessionName, szMethodName, "sYD_DONG-->" + sYD_DONG + sYD_USER_ID, YdConstant.DEBUG);
			
			outRdSet = dao.getCoilYdSchAtCoilNo(recDelPara);
			
			if(outRdSet == null || outRdSet.size() == 0){
				// 저장위치만 삭제 
				//JDTORecord [] inRecord = CmUtil.genJDTORecordSet(gdReq);
				if(!sYD_DONG.equals("X")) {
					ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
					ejbConn.trx("delCoilYdStkPosInfo", new Class[] { JDTORecord[].class }, new Object[] { inRecordReq });
					szMsg = "저장위치 삭제 성공";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}	
				// 메시지 담기 
				gdRes.setMessage("저장위치 삭제가 정상 처리되었습니다.");
				 
			}else{

				rsResult1 = JDTORecordFactory.getInstance().createRecordSet("");
				inRecord1 = JDTORecordFactory.getInstance().create();			
				inRecord1.setField("STL_NO",      gdReq.getParam("STL_NO"));
				/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getCrnSchByStlNo*/
				intRtnVal = ydCrnschDao.getYdCrnsch(inRecord1, rsResult1, 52);
				if( intRtnVal  > 0 ) {
//					gdRes.setMessage("스케쥴이 편성되어 있습니다. 취소처리후 작업하세요");		
//					m_ctx.setRollbackOnly();
//					return gdRes;	

					rsResult1.first();
					outRecord9 = rsResult1.getRecord();
					sYD_CRN_SCH_ID 	= ydDaoUtils.paraRecChkNull(outRecord9, "YD_CRN_SCH_ID");
					sYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(outRecord9, "YD_SCH_CD");
					inRecord4   	= JDTORecordFactory.getInstance().create();
					inRecord4.setField("YD_CRN_SCH_ID"	,sYD_CRN_SCH_ID);
					inRecord4.setField("YD_SCH_CD"		,sYD_SCH_CD);
					inRecord4.setField("DEL_YN"			,"Y");
					inRecord4.setField("MODIFIER"		,sYD_USER_ID);
								
					ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);			
					outRecord5 = (JDTORecord)ejbConn.trx("WrkCancelloc", new Class[] { JDTORecord.class }, new Object[] { inRecord4 });
					sRTN_CD				= StringHelper.evl(outRecord5.getFieldString("RTN_CD"), "0");
					sRTN_MSG			= StringHelper.evl(outRecord5.getFieldString("RTN_MSG"), "");
					sCRANE_SND			= StringHelper.evl(outRecord5.getFieldString("CRANE_SND"), "");
					sYD_WRK_PROG_STAT	= StringHelper.evl(outRecord5.getFieldString("YD_WRK_PROG_STAT"), "");
					
					if ("0".equals(sRTN_CD)) {
						gdRes.setMessage(sRTN_MSG);		
						m_ctx.setRollbackOnly();
						return gdRes;	
					}	
		//작업예약삭제
					outRecord5.setField("MODIFIER"		,sYD_USER_ID);
					outRecord5.setField("YD_USER_ID"	,sYD_USER_ID);
					ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);			
					outRecord6 = (JDTORecord)ejbConn.trx("delWookBook", new Class[] { JDTORecord.class }, new Object[] { outRecord5 });
					sRTN_CD				= StringHelper.evl(outRecord6.getFieldString("RTN_CD"), "0");
					sRTN_MSG			= StringHelper.evl(outRecord6.getFieldString("RTN_MSG"), "");
					sYD_SCH_CD			= StringHelper.evl(outRecord6.getFieldString("YD_SCH_CD"), "");
						if ("0".equals(sRTN_CD)) {
						gdRes.setMessage(sRTN_MSG);		
						m_ctx.setRollbackOnly();
						return gdRes;
					}	
		
		//작업취소전문 송신
					if ("Y".equals(sCRANE_SND)) {
						recDelPara   = JDTORecordFactory.getInstance().create();
						recDelPara.setField("MSG_ID",           "YDY5L004"        );
						recDelPara.setField("YD_CRN_SCH_ID",    sYD_CRN_SCH_ID          ); 
						recDelPara.setField("YD_WRK_PROG_STAT", sYD_WRK_PROG_STAT    );   // 이모듈을 탈려면 항상 '1'의값이 들어옴
						recDelPara.setField("MSG_GP",           "D"                );
						YdDelegate ydDelegate = new YdDelegate();				
						ydDelegate.sendMsg(recDelPara);
					}	
				} else {	
	
					rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
					/* com.inisteel.cim.yd.dao.ydwrkbookmtldao.YdWrkbookmtlDao.getYdWrkbookmtlSTLNO */
					intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(inRecord1, rsResult2, 2);
					if( intRtnVal  > 0 ) {
						rsResult2.first();
						outRecord0 = rsResult2.getRecord();
						sYD_WBOOK_ID 	= ydDaoUtils.paraRecChkNull(outRecord0, "YD_WBOOK_ID");
						sYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(outRecord0, "YD_SCH_CD");
					
					}	
		
					if(!(sYD_WBOOK_ID.equals(""))){
						// 작업예약취소  호출	
						inRecordarr = new JDTORecord[1];
	
						inRecordarr[0] = JDTORecordFactory.getInstance().create();
						inRecordarr[0].setField("YD_WBOOK_ID"		, sYD_WBOOK_ID); 
						inRecordarr[0].setField("YD_USER_ID"	    , sYD_USER_ID); 
	
						ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
						String rtnMsg = (String)ejbConn.trx("delYdWrkbook",
								new Class[] { JDTORecord[].class }, new Object[] { inRecordarr });
	
						if (!rtnMsg.equals(YdConstant.RETN_CD_SUCCESS)) {
							gdRes.setMessage(rtnMsg);		
							m_ctx.setRollbackOnly();
							return gdRes;
						}	
	
					}
				
				}			
				
				//=====================================================================================
				// 저장위치 삭제
				//JDTORecord [] inRecord = CmUtil.genJDTORecordSet(gdReq);
				//fix 20081230 수정 - Param 정보도 추가로 가져오기 위함
				if(!sYD_DONG.equals("X")) {
					ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
					ejbConn.trx("delCoilYdStkPosInfo", new Class[] { JDTORecord[].class }, new Object[] { inRecordReq });
					szMsg = "저장위치 삭제 성공";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}	
				gdRes.setMessage("작업취소, 저장위치 삭제가 정상 처리되었습니다.");
			}
			
				
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		return gdRes;
	}  //end of delCoilYdStkPosInfo	
	
	
	/**
	 * 콘베어정보 등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData insCoilYdConveyorMgt(GridData gdReq) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "insCoilYdConveyorMgt";
					
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			ejbConn.trx("insCoilYdConveyorMgt", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return gdRes;
	}  //end of insCoilYdConveyorMgt
	
	/**
	 * 콘베어정보 삭제
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData delCoilYdConveyorMgt(GridData gdReq) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName="delCoilYdConveyorMgt";
					
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			ejbConn.trx("delCoilYdConveyorMgt", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		return gdRes;
	}  //end of delCoilYdConveyorMgt
	
	
	/**
	 * 야드 동 정보 설정(수정)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilYdBaySetInfo(GridData gdReq) throws DAOException {
		//LOG
		String szMsg         = "";
		String szMethodName  = "updCoilYdBaySetInfo";
		GridData gdRes       = null;
		EJBConnector ejbConn = null;
		try{
			//JDTORecord [] inRecord = CmUtil.genJDTORecordSet(gdReq);
			//fix 20081230 수정 - Param 정보도 추가로 가져오기 위함 
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);

			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			ejbConn.trx("updCoilYdBaySetInfo", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		return gdRes;
	}  //end of updCoilYdStkPosInfo
	
	/**
	 * 야드 동 정보 삭제
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData delCoilYdBaySetInfo(GridData gdReq) throws DAOException {
		//LOG
		String szMsg         = "";
		String szMethodName  = "delCoilYdBaySetInfo";
		GridData gdRes       = null;
		EJBConnector ejbConn = null;
		try{
			//JDTORecord [] inRecord = CmUtil.genJDTORecordSet(gdReq);
			//fix 20081230 수정 - Param 정보도 추가로 가져오기 위함 
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);

			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			ejbConn.trx("delCoilYdBaySetInfo", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		return gdRes;
	}  //end of delCoilYdBaySetInfo
	
	/**
	 * 야드 동 정보 등록 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData insCoilYdBaySetInfo(GridData gdReq) throws DAOException {
		//LOG
		String szMsg         = "";
		String szMethodName  = "insCoilYdBaySetInfo";
		GridData gdRes       = null;
		EJBConnector ejbConn = null;
		try{
			//JDTORecord [] inRecord = CmUtil.genJDTORecordSet(gdReq);
			//fix 20081230 수정 - Param 정보도 추가로 가져오기 위함 
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);

			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			ejbConn.trx("insCoilYdBaySetInfo", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return gdRes;
	}  //end of updCoilYdStkPosInfo
	
	
	/**
	 * 콘베어정보 수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilYdConveyorMgt(GridData gdReq) throws DAOException {
		//LOG
		String szMsg         = "";
		String szMethodName  = "updCoilYdConveyorMgt";
		GridData gdRes       = null;
		EJBConnector ejbConn = null;
		try{
			//JDTORecord [] inRecord = CmUtil.genJDTORecordSet(gdReq);
			//fix 20081230 수정 - Param 정보도 추가로 가져오기 위함 
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);

			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			ejbConn.trx("updCoilYdConveyorMgt", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		return gdRes;
	}  //end of updCoilYdStkPosInfo
	
	/**
	 * 반납크레인대상조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	
	public GridData getCoilYdRetCrnReg(GridData inDto) throws DAOException {
					
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		String szMethodName = "getCoilYdRetCrnReg";	
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			
			ejbConn = new EJBConnector("default", this);

			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdRetCrnReg", inRecord);
			                                                          
			gdRes = CmUtil.genGridData(inDto , recordSet);

		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilYdRetCrnReg

//	/**
//	 * 반납크레인 수정[공장공정코드]
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param inDto
//	 * @return GridData
//	 * @throws DAOException
//	 * @throws JDTOException
//	 */
//	public GridData updCoilYdRetCrnReg(GridData gdReq) throws DAOException {
//		//LOG
//		String szMsg="";
//		String szMethodName="updCoilYdRetCrnReg";
//					
//		GridData gdRes = null;
//		EJBConnector ejbConn = null;
//		try{
//			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);
//					
//			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
//			ejbConn.trx("updCoilYdRetCrnReg",
//						new Class[] { JDTORecord[].class }, new Object[] { inRecord });
//			gdRes = OperateGridData.cloneResponseGridData(gdReq);
//			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		}catch(Exception e){
//			throw new DAOException(getClass().getName() + e.getMessage(),e);
//			//ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
//		}		
//		return gdRes;
//	}  //end of updCoilYdRetCrnReg
//	

	
	/**
	 *  코일 야드 크레인 설비내용 조회 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdCrnStsSetById(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdCrnStsSetById";	
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdCrnStsSetById", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	/**
	 *  코일 소재야드 크레인 설비내용 조회 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	
	
	public GridData getCoilYdCrnStsSetById2(GridData inDto) throws DAOException {
		//LOG
		String szLogMsg = null;
		String szRtnMsg = null;
		String szMethodName="getCoilYdCrnStsSetById2";	
		String szOperationName	= "코일 야드 크레인 설비내용 조회";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdCrnStsSetById2", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			//조회성공메세지를 설정
			gdRes.setMessage(YdConstant.RETN_CD_SUCCESS);
		}catch(DAOException e) {
			//에러메시지를 설정
			gdRes.setMessage(e.getMessage());
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}
	
	
	
	/**
	 *  코일 야드 대차  출발지시 수정(UPDATE) 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilYdCrnStsSetTcarOrd(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "updCoilYdCrnStsSetTcarOrd";
		
		GridData     gdRes   = null;
		EJBConnector ejbConn = null;
		try{
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			ejbConn.trx( szMethodName , new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
				
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	
	/**
	 *  코일 야드 대차  운전모드 수정(UPDATE) 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilYdCrnStsSetTcarMove(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "updCoilYdCrnStsSetTcarMove";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			ejbConn.trx( szMethodName , new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
				
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	
	
	/**
	 * 코일 제품상세정보 조회/검색하는 메소드이다.(검색어, 업무영역코드)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getPtCoilCommInfoji(GridData inDto) throws DAOException {
		
		//LOG
		String szMsg="";
		String szMethodName="getPtCoilCommInfoji";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);

			recordSet = (JDTORecordSet) ejbConn.trx("getPtCoilCommInfoji",
					new Class[] { JDTORecord.class }, new Object[] { inRecord });			
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");

		return gdRes;
	}
	
	
	/**
	 * 차량별 상세 작업관리 조회 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdCarWorkMgtlist(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdCarWorkMgtlist";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdCarWorkMgtlist", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	
	/**
	 * 대차 작업관리 조회 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getTcarSchMtlList(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getTcarSchMtlList";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getTcarSchMtlList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}

	/**
	 * 대차 작업관리재료정보 조회 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getTcarWorkMtlList(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getTcarWorkMtlList";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getTcarWorkMtlList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	
	/**
	 * 코일야드 재료상세정보 조회 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdMtlDtl(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdMtlDtl";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdMtlDtl", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	
	/**
	 * 대차 상차정보 조회 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getYdTcarLiftFtmvMtl(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getYdTcarLiftFtmvMtl";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getYdTcarLiftFtmvMtl", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}

	/**
	 * 입고 Backup처리  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getYdBackupWork(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getYdBackupWork";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getYdBackupWork", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}

	/**
	 * 야드관리 > 코일소재야드 > 설비/차량관리 > 이송대상재관리   (이송공장별 이송량 조회)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdFrtMoveGpList(GridData inDto) throws DAOException {
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		String szMethodName = "getCoilYdFrtMoveGpList";	
		try{
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			gdRes = (GridData)ejbConn.trx("getCoilYdFrtMoveGpList", new Class[] { GridData.class }, new Object[] { inDto });
			
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		
		return gdRes;
	}

	/**
	 * 코일저장위치 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdCoilStkPos(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdCoilStkPos";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdCoilStkPos", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	
	/**
	 * 코일저장위치 조회(크레인작성지시 화면)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdCoilStkPos2(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdCoilStkPos2";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdCoilStkPos2", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	
	/**
	 * 코일야드 크레인작업지시작성
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilYdCrnWorkCmd(GridData gdReq) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName="updCoilYdCrnWorkCmd";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			ejbConn.trx("updCoilYdCrnWorkCmd", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		return gdRes;
	}  //end of updCoilYdCrnWorkCmd
	
	/**
	 * 코일야드 크레인스케줄 재료 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdCrnWrkMtl(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdCrnWrkMtl";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdCrnWrkMtl", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	
//	/**
//	 * 소재코일야드 메뉴얼 작업지시 편성
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param inDto
//	 * @return GridData
//	 * @throws DAOException
//	 * @throws JDTOException
//	 */
//	public GridData ydCoilManualReq(GridData gdReq) throws DAOException {
//		//LOG
//		String szMsg="";
//		String szMethodName="ydCoilManualReq";
//		String rtnMsg = "";		
//		GridData gdRes = null;
//		EJBConnector ejbConn = null;
//		String sRTN_CD	= "";
//		String sRTN_MSG	= "";
//
//		JDTORecord outRecord1    = JDTORecordFactory.getInstance().create();
//		JDTORecord outRecord2    = JDTORecordFactory.getInstance().create();
//	
//		try{
//
//			gdRes = OperateGridData.cloneResponseGridData(gdReq);
//			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
//			
//			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(gdReq);
//			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
//			outRecord1 = (JDTORecord)ejbConn.trx("ydCoilManualReq", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
//			sRTN_CD	= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
//			sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
//			if (!("1".equals(sRTN_CD))) {
//				gdRes.setMessage(sRTN_MSG);		
//				m_ctx.setRollbackOnly();
//			} else {
//
//				gdRes.setMessage(sRTN_MSG);		
//	
//			} 	
////작업예약등록			
//			ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
//			outRecord2 = (JDTORecord)ejbConn.trx("WrkBookInsertProc", new Class[] { JDTORecord.class }, new Object[] { outRecord1 });
//			sRTN_CD		= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
//			sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
//			if (!("1".equals(sRTN_CD))) {
//				gdRes.setMessage(sRTN_MSG);		
//				m_ctx.setRollbackOnly();
//			} else {
//
//				gdRes.setMessage("정상적으로 작업예약까지 등록했습니다.");		
//	
//			} 	
//			
////			//내부 Process 연결
////			EJBConnector ejbConn = null;
////			ejbConn = new EJBConnector("default", this);
////			ejbConn.trx("IssueWrkDmdFaEJB", "ydManualReqCoil", recPara);
//			
//			ydUtils.putLog(szSessionName, szMethodName, sRTN_MSG, YdConstant.DEBUG);
//			
//		}catch(Exception e){
//			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
//		}
//		return gdRes;
//	}  //end of ydCoilManualReq
//	
	/**
	 * LINE재공 LIST조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdBayInlnList(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdBayInlnList";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdBayInlnList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	
	
	/**
	 * LINE재공 LIST조회2
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdBayInlnList2(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdBayInlnList2";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdBayInlnList2", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	
	/**
	 * 다음공정별 재공현황팝업 (화면:LINE재공LIST)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdBayInlnList_Pp(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdBayInlnList_Pp";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szMsg = "[Jsp Facade : "+szMethodName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdBayInlnList_Pp", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		szMsg = "[Jsp Facade : "+szMethodName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		return gdRes;
	}
	
	/**
	 * LINE별 재공상세현황 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdBayInlnDtlList(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdBayInlnDtlList";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdBayInlnDtlList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	
	/**
	 * 여재보유현황 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdRmnPoss(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdRmnPoss";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdRmnPoss", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	
	/**
	 * 코일야드 여재보유현황 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdRmnPossList(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdRmnPossList";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdRmnPossList", inRecord);
			
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		return gdRes;
	}
	
	
	/**
	 * 코일야드 분기 CONV 대상 재료 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getDivConvTargetMtl_Popup(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getDivConvTargetMtl_Popup";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getDivConvTargetMtl_Popup", inRecord);
			
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		return gdRes;
	}
	
	
	/**
	 * 코일야드 분기 CONV 대상 재료 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getDivConvTargetMtl_NextProc(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getDivConvTargetMtl_NextProc";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getDivConvTargetMtl_NextProc", inRecord);
			
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		return gdRes;
	}
	
	
	/**
	 * Line-Off 분기 Conv
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updDivConvLineOff(GridData gdReq) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName="updDivConvLineOff";
		String rtnMsg = "";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			
			rtnMsg = (String)ejbConn.trx("updDivConvLineOff", new Class[] { JDTORecord.class }, new Object[] { inRecord });
			gdRes.setMessage(rtnMsg);
			
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		return gdRes;
	}  //end of updDivConvLineOff
	
	/**
	 * 코일야드 저장위치 좌표 관리
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getYdStkcolBaySpan(GridData inDto) throws DAOException {

		String szMethodName="getYdStkcolBaySpan";		
		String szLogMsg = "";
		String szOperationName	= "Span별 적치사양조정";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);

			recordSet = (JDTORecordSet) ejbConn.trx("getYdStkcolBaySpan",
					new Class[] { JDTORecord.class }, new Object[] { inRecord });
			gdRes = CmUtil.genGridData(inDto , recordSet);
		
		}catch(Exception e){			
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		

		return gdRes;
	} // end of getYdStkcolBaySpan
	
	/**
	 * 크레인별 배차기준수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData uptCarAsgnStdByCrn(GridData inDto) throws DAOException {
		/*
		 * 업무기준 : 그리드로 넘겨진 배차기준 정보를 설비테이블에 수정 처리
		 * 수정자 : 임춘수/SJH
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
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);

			szRtnMsg = (String)ejbConn.trx("uptCarAsgnStdByCrn",
						new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage(szRtnMsg);
		return gdRes;
	} //end of uptCarAsgnStdByCrn

	
	/**
	 *  작업예약 조회(코일 소재야드)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData getYdWrkbook_page(GridData inDto) throws DAOException {
	 
		String szMethodName = "getYdWrkbook_page";
		String szLogMsg = "";
		String szOperationName = "작업예약 조회(코일소재)";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getYdWrkbook_page", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
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
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData getYdWrkbook_dtl_page(GridData inDto) throws DAOException {
		 
		String szMethodName = "getYdWrkbook_dtl_page";
		String szLogMsg = "";
		String szOperationName = "작업예약 재료 조회(코일소재)";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getYdWrkbook_dtl_page", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return gdRes;
	} //end of getYdWrkbook_dtl_page
	
	
	
	/*---------------------------------------------------------------------------------------------------*/
	/*                                     2기 작업
	/*---------------------------------------------------------------------------------------------------*/
	/**
	 *  일품단위이적등록   getMtlUnitMvstkReg
	 *  송정현
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	
	public GridData getMtlUnitMvstkReg(GridData inDto) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName="getMtlUnitMvstkReg";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getMtlUnitMvstkReg", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getMtlUnitMvstkReg
	
	/**
	 * 일품단위이적등록
	 * 송정현
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getMtlUnitMvstkReg1(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getMtlUnitMvstkReg1";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getMtlUnitMvstkReg1", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} // end of getBecauseMv


	/**
	 * 소재코일야드 메뉴얼 작업지시 편성
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updMtlUnitMvstkReg(GridData gdReq) throws DAOException {
		//LOG
		YdDelegate ydDelegate 	= new YdDelegate();
		String szMsg		= "";
		String szMethodName	= "updMtlUnitMvstkReg";
		GridData gdRes 		= null;
		EJBConnector ejbConn = null;
		String sRTN_CD		= "";
		String sRTN_MSG		= "";
		String sYD_WBOOK_ID = "";
		String sYD_SCH_CD 	= "";
		String sSND_FLAG 	= "N";
		JDTORecord inRecord1   = JDTORecordFactory.getInstance().create();
		JDTORecord outRecord1   = JDTORecordFactory.getInstance().create();
		JDTORecord outRecord2   = JDTORecordFactory.getInstance().create();
		JDTORecord recInTemp    = JDTORecordFactory.getInstance().create();
	
		String sYD_GP			= "";
		String sYD_AIM_BAY_GP	= "";
		String sYD_TCAR_SCH_ID  = "";
		
		try{

			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			
			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(gdReq);
			
			//------------------------------------------------------------------------------------------------
			// 2010.06.25 추가
			// 대상 위치 폭구분, 대차상태  체크
			// TO위치의 폭구분이 다를경우 이동 불가
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			outRecord1 = (JDTORecord)ejbConn.trx("getStkColWidthGp", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
			sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
			if (!sRTN_CD.equals("1")) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
				return gdRes;
			} 
			//------------------------------------------------------------------------------------------------
			
			
			// 메뉴얼 코일 작업지시 편성
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			outRecord1 = (JDTORecord)ejbConn.trx("updMtlUnitMvstkReg", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
			sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
			if (!("1".equals(sRTN_CD))) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
				return gdRes;
			} else {

				gdRes.setMessage(sRTN_MSG);		
				
			} 	
//작업예약등록			
			ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
			outRecord2 = (JDTORecord)ejbConn.trx("WrkBookInsertProc", new Class[] { JDTORecord.class }, new Object[] { outRecord1 });
			sRTN_CD		= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
			sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
			sYD_WBOOK_ID= StringHelper.evl(outRecord2.getFieldString("YD_WBOOK_ID"), "");
			sYD_SCH_CD	= StringHelper.evl(outRecord2.getFieldString("YD_SCH_CD"), "");
			if (!("1".equals(sRTN_CD))) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
				return gdRes;
			} else {
				
				gdRes.setMessage(sRTN_MSG + " <br> 정상적으로 작업예약까지 등록했습니다.");		
			//	gdRes.setMessage("정상적으로 작업예약까지 등록했습니다.");		
	
			} 	
			szMsg = "스케줄 코드 :"+ sYD_SCH_CD.substring(2, 4);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			if(sYD_SCH_CD.substring(2, 4).equals("TC")){
		    // 설비 check 하여 		

//대차작업 송신 여부		
				inRecord1 = JDTORecordFactory.getInstance().create();
				inRecord1.setField("YD_WBOOK_ID"			, sYD_WBOOK_ID);
	    		
				ejbConn = new EJBConnector("default", "CoilTransEqpSchSeEJB", this);
				outRecord2 = (JDTORecord)ejbConn.trx("TCarMoveWrkBook", new Class[] { JDTORecord.class }, new Object[] { inRecord1 });
				sRTN_CD			= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
				sRTN_MSG		= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
				sYD_GP			= StringHelper.evl(outRecord2.getFieldString("YD_GP"), "");
				sYD_TCAR_SCH_ID = StringHelper.evl(outRecord2.getFieldString("YD_TCAR_SCH_ID"), "");
				sYD_AIM_BAY_GP	= StringHelper.evl(outRecord2.getFieldString("YD_AIM_BAY_GP"), "");
				sSND_FLAG		= StringHelper.evl(outRecord2.getFieldString("SND_FLAG"), "");
				
				if( sRTN_CD.equals("0")) {
					gdRes.setMessage(sRTN_MSG);		
					m_ctx.setRollbackOnly();
					return gdRes;
				} else {	
					ydUtils.putLog(szSessionName, szMethodName, "대차작업 송신 여부:" +sSND_FLAG , YdConstant.DEBUG);
					if( sSND_FLAG.equals("Y")) {
						recInTemp = JDTORecordFactory.getInstance().create();
			    		recInTemp.setField("MSG_ID"			, "YDY5L006");
			    		recInTemp.setField("YD_GP"			, sYD_GP);
			    		recInTemp.setField("YD_SCH_CD"		, sYD_SCH_CD);
			    		recInTemp.setField("YD_TCAR_SCH_ID"	, sYD_TCAR_SCH_ID);
			    		recInTemp.setField("YD_AIM_BAY_GP"	, sYD_AIM_BAY_GP);
			    		ydDelegate.sendMsg(recInTemp);
					}
				}	
				
			} else {
	//스케쥴등록	
				JDTORecord[] inRecordarr   	= null;
				inRecordarr = new JDTORecord[1];
				
				inRecordarr[0] = JDTORecordFactory.getInstance().create();
				inRecordarr[0].setField("YD_SCH_CD"		, sYD_SCH_CD); 
				inRecordarr[0].setField("YD_WBOOK_ID"	, sYD_WBOOK_ID); 
				ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
				outRecord2 = (JDTORecord) ejbConn.trx("trxRunSchedule", new Class[] { JDTORecord[].class },	new Object[] { inRecordarr });
	
				sRTN_CD		= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
				sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
				ydUtils.putLog(szSessionName, szMethodName, "//" + sRTN_MSG, YdConstant.DEBUG);
				if (!("1".equals(sRTN_CD))) {
					gdRes.setMessage(sRTN_MSG);		
					m_ctx.setRollbackOnly();
					return gdRes;
				} else {
	
					gdRes.setMessage(sRTN_MSG + "<br> 스케쥴까지 등록했습니다.");		
		
				} 	
				
			}
			
			ydUtils.putLog(szSessionName, szMethodName, sRTN_MSG, YdConstant.DEBUG);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return gdRes;
	}  //end of updMtlUnitMvstkReg
	
	
	/**
	 *  야드관리 > 코일소재야드 > 재공관리 > 열단위이적등록/스판단위이적등록  (이적지시)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updColUnitMvstkReg(GridData gdReq) throws DAOException {
		
		YdDelegate ydDelegate 	= new YdDelegate();
		YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();//작업예약 DAO
		String szMsg="";
		String szMethodName="updColUnitMvstkReg";
		String szOperationName	= "소재창고 열단위 이적 처리";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		String sRTN_CD		= "";
		String sRTN_MSG		= "";
		String szTemp   	= "";
		String szToColGp 	= "";
		String szTCar 		= "";
		String szYD_SCH_CD 	= "";
		String szYD_TO_LOC_GUIDE  = "";
		String sYD_USER_ID  	= "";
		String sUP_POS 			= "";
		String sYD_WBOOK_ID 	= "";
		String sFirstYD_WBOOK_ID     = "";
		int intStlSchCnt        = 0;
		int intStlCnt         	= 0;
		JDTORecord outRecord    = JDTORecordFactory.getInstance().create();
		JDTORecord outRecord1   = JDTORecordFactory.getInstance().create();
		JDTORecord outRecord2   = JDTORecordFactory.getInstance().create();
		JDTORecord outRecord21  = JDTORecordFactory.getInstance().create();
		JDTORecord recInTemp   	= JDTORecordFactory.getInstance().create();
		JDTORecord inRecord2 	= JDTORecordFactory.getInstance().create();
		JDTORecord inRecord1 	= JDTORecordFactory.getInstance().create();
		JDTORecord inRecord     = JDTORecordFactory.getInstance().create();
		JDTORecordSet rsResult2 = null; 
		String sYD_GP			= "";
		String sYD_TCAR_SCH_ID  = "";
		String sYD_AIM_BAY_GP	= "";
		String sSND_FLAG		= "N";
		
		int intRtnVal        	= 0;
		try{
			szMsg = "[JSP-FACADE  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			inRecord1 = CmUtil.genJDTORecord(gdReq);
			JDTORecord [] inRecordSet =  ydComUtil.genGridToJDTORecordAll(gdReq);
			
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			 
			szTemp 		=  ydDaoUtils.paraRecChkNull(inRecordSet[0], "FROMLOC");
			sYD_USER_ID =  ydDaoUtils.paraRecChkNull(inRecord1, "YD_USER_ID"); 
			szYD_SCH_CD =  ydDaoUtils.paraRecChkNull(inRecord1, "FRM_YD_SCH_CD"); 
			
			if(szTemp.equals("")){
				gdRes.setMessage("FROM 적치정보가 없습니다!!");		
				m_ctx.setRollbackOnly();
				
			} else { 
				szToColGp = inRecord1.getFieldString("YD_GP") +  inRecord1.getFieldString("TO_YD_BAY_GP") +
							inRecord1.getFieldString("TO_YD_EQP_GP") +  inRecord1.getFieldString("TO_YD_STK_COL_NO") ;
				szTCar = ydDaoUtils.paraRecChkNull(inRecord1, "T_CAR");
				
				//------------------------------------------------------------------------------------------------
				// 2010.06.25 추가
				// 대상 위치 폭구분 체크
				// TO위치의 폭구분이 다를경우 이동 불가
				JDTORecord [] inRcd =  new JDTORecord[1];
				inRcd[0] = JDTORecordFactory.getInstance().create();
				
				inRcd[0].setField("FROM_YD_STK_COL_GP", szTemp.substring(0, 6));
				inRcd[0].setField("TO_YD_STK_COL_GP", szToColGp);
				inRcd[0].setField("YD_WRK_PLAN_TCAR", szTCar); // 대차
				
				ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
				outRecord1 = (JDTORecord)ejbConn.trx("getStkColWidthGp", new Class[] { JDTORecord[].class }, new Object[] { inRcd });
				sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
				sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
				if (!sRTN_CD.equals("1")) {
					gdRes.setMessage(sRTN_MSG);		
					m_ctx.setRollbackOnly();
					return gdRes;
				} 
				//------------------------------------------------------------------------------------------------
				
				
				szYD_TO_LOC_GUIDE = szToColGp ;
				
//				2단  처리				
				for(int Loopi = 0; Loopi < inRecordSet.length; Loopi++) {
					//재료번호
					//STL_NO []
					sUP_POS =  ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "UP_POS");
					if(sUP_POS.substring(5, 6).equals("2")) {
						intStlCnt++;
						inRecord = JDTORecordFactory.getInstance().create();
						inRecord.setField("YD_SCH_CD",          		szYD_SCH_CD);//스케줄코드
						inRecord.setField("STL_SH",      				"1");  //LINE_IN 재료매수
						inRecord.setField("STL_NO1", 	  				ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "STL_NO"));
						inRecord.setField("YD_TO_LOC_DCSN_MTD",			"F");
								
						inRecord.setField("TO_YD_STK_BED_NO",			szYD_TO_LOC_GUIDE);
						inRecord.setField("YD_UP_COLL_SEQ1", 			"1");  //권상모음순서
						inRecord.setField("YD_WRK_PLAN_TCAR", 			szTCar);  //권상모음순서
						inRecord.setField("YD_USER_ID", 				sYD_USER_ID);  //권상모음순서
		
// 작업예약 등록 호출
//대처						this.procWrkBook(recOutPara);
//YD_SCH_CD:스케줄코드,
//STL_SH: 재료매수,
//YD_TO_LOC_DCSN_MTD(TO위치 결정 방법)
//STL_NO(재료번호1,2,3,....)
//FR_YD_STK_BED_NO(적치배드)
//TO_YD_STK_BED_NO(가이드가 됨)
		
						ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
						outRecord = (JDTORecord)ejbConn.trx("WrkBookInsertProc", new Class[] { JDTORecord.class }, new Object[] { inRecord });
						sRTN_CD		= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
						sRTN_MSG	= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
						sYD_WBOOK_ID= StringHelper.evl(outRecord.getFieldString("YD_WBOOK_ID"), "");
						
						
						if ("0".equals(sRTN_CD)) {
							szMsg = "작업예약 등록시 ERROR";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							gdRes.setMessage(sRTN_MSG);		
							m_ctx.setRollbackOnly();
							return gdRes;
						} else {
							intStlSchCnt++;
							
							if((intStlSchCnt == 1) && (!szYD_SCH_CD.substring(2, 4).equals("TC"))) {
								sFirstYD_WBOOK_ID = sYD_WBOOK_ID;
							}

							gdRes.setMessage(sRTN_MSG + " <br> 정상적으로 작업예약까지 등록했습니다.");		
							//gdRes.setMessage("정상적으로 작업예약까지 등록했습니다.");			
						}	
						if((szYD_SCH_CD.substring(2, 4).equals("TC")) && (sSND_FLAG.equals("N"))){
// 설비 check 하여 	
// 대차작업 송신 여부
							inRecord2 = JDTORecordFactory.getInstance().create();
							inRecord2.setField("YD_WBOOK_ID"		, sYD_WBOOK_ID);
							inRecord2.setField("MODIFIER"			, sYD_USER_ID);  /*TODO.. 2010.07.29 - 박지열(수정자 추가)*/

							ejbConn = new EJBConnector("default", "CoilTransEqpSchSeEJB", this);
							outRecord2 = (JDTORecord)ejbConn.trx("TCarMoveWrkBook", new Class[] { JDTORecord.class }, new Object[] { inRecord2 });
							sRTN_CD			= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
							sRTN_MSG		= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
							sYD_GP			= StringHelper.evl(outRecord2.getFieldString("YD_GP"), "");
							sYD_TCAR_SCH_ID = StringHelper.evl(outRecord2.getFieldString("YD_TCAR_SCH_ID"), "");
							sYD_AIM_BAY_GP	= StringHelper.evl(outRecord2.getFieldString("YD_AIM_BAY_GP"), "");
							sSND_FLAG		= StringHelper.evl(outRecord2.getFieldString("SND_FLAG"), "");
							
							if( sRTN_CD.equals("0")) {
								gdRes.setMessage(sRTN_MSG);		
								m_ctx.setRollbackOnly();
							} else {	
								ydUtils.putLog(szSessionName, szMethodName, "대차작업 송신 여부:" +sSND_FLAG , YdConstant.DEBUG);
								if( sSND_FLAG.equals("Y")) {
									recInTemp = JDTORecordFactory.getInstance().create();
						    		recInTemp.setField("MSG_ID"			, "YDY5L006");
						    		recInTemp.setField("YD_GP"			, sYD_GP);
						    		recInTemp.setField("YD_SCH_CD"		, szYD_SCH_CD);
						    		recInTemp.setField("YD_TCAR_SCH_ID"	, sYD_TCAR_SCH_ID);
						    		recInTemp.setField("YD_AIM_BAY_GP"	, sYD_AIM_BAY_GP);
						    		ydDelegate.sendMsg(recInTemp);
								}
							}	
						}
					}
				}
//				1단  처리				
				for(int Loopi = 0; Loopi < inRecordSet.length; Loopi++) {
					//재료번호
					//STL_NO []
					sUP_POS =  ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "UP_POS");
					if(sUP_POS.substring(5, 6).equals("1")) {
						intStlCnt++;
						inRecord = JDTORecordFactory.getInstance().create();
						inRecord.setField("YD_SCH_CD",          		szYD_SCH_CD);//스케줄코드
						inRecord.setField("STL_SH",      				"1");  //LINE_IN 재료매수
						inRecord.setField("STL_NO1", 	  				ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "STL_NO"));
						inRecord.setField("YD_TO_LOC_DCSN_MTD",			"F");
								
						inRecord.setField("TO_YD_STK_BED_NO",			szYD_TO_LOC_GUIDE);
						inRecord.setField("YD_UP_COLL_SEQ1", 			"1");  //권상모음순서
						inRecord.setField("YD_WRK_PLAN_TCAR", 			szTCar);  //권상모음순서
						inRecord.setField("YD_USER_ID", 				sYD_USER_ID);  //권상모음순서
		
//				 작업예약 등록 호출
		//YD_SCH_CD:스케줄코드,
		//STL_SH: 재료매수,
		//YD_TO_LOC_DCSN_MTD(TO위치 결정 방법)
		//STL_NO(재료번호1,2,3,....)
		//FR_YD_STK_BED_NO(적치배드)
		//TO_YD_STK_BED_NO(가이드가 됨)
		
						ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
						outRecord = (JDTORecord)ejbConn.trx("WrkBookInsertProc", new Class[] { JDTORecord.class }, new Object[] { inRecord });
						sRTN_CD		= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
						sRTN_MSG	= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
						sYD_WBOOK_ID= StringHelper.evl(outRecord.getFieldString("YD_WBOOK_ID"), "");
						if ("0".equals(sRTN_CD)) {
							szMsg = "작업예약 등록시 ERROR";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							gdRes.setMessage(sRTN_MSG);		
							m_ctx.setRollbackOnly();
							return gdRes;
						} else {
							
							intStlSchCnt++;
							
							if((intStlSchCnt == 1) && (!szYD_SCH_CD.substring(2, 4).equals("TC"))) {
								sFirstYD_WBOOK_ID = sYD_WBOOK_ID;
							}

							gdRes.setMessage(sRTN_MSG + " <br> 정상적으로 작업예약까지 등록했습니다.");		
							//gdRes.setMessage("정상적으로 작업예약까지 등록했습니다.");			
						}	

						if((szYD_SCH_CD.substring(2, 4).equals("TC")) && (sSND_FLAG.equals("N"))){
//							 설비 check 하여 	
//							 대차작업 송신 여부
							inRecord2 = JDTORecordFactory.getInstance().create();
							inRecord2.setField("YD_WBOOK_ID"			, sYD_WBOOK_ID);
							inRecord2.setField("MODIFIER"			, sYD_USER_ID);  /*TODO.. 2010.07.29 - 박지열(수정자 추가)*/

							ejbConn = new EJBConnector("default", "CoilTransEqpSchSeEJB", this);
							outRecord2 = (JDTORecord)ejbConn.trx("TCarMoveWrkBook", new Class[] { JDTORecord.class }, new Object[] { inRecord2 });
							sRTN_CD			= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
							sRTN_MSG		= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
							sYD_GP			= StringHelper.evl(outRecord2.getFieldString("YD_GP"), "");
							sYD_TCAR_SCH_ID = StringHelper.evl(outRecord2.getFieldString("YD_TCAR_SCH_ID"), "");
							sYD_AIM_BAY_GP	= StringHelper.evl(outRecord2.getFieldString("YD_AIM_BAY_GP"), "");
							sSND_FLAG		= StringHelper.evl(outRecord2.getFieldString("SND_FLAG"), "");
							
							if( sRTN_CD.equals("0")) {
								gdRes.setMessage(sRTN_MSG);		
								m_ctx.setRollbackOnly();
							} else {	
								ydUtils.putLog(szSessionName, szMethodName, "대차작업 송신 여부:" +sSND_FLAG , YdConstant.DEBUG);
								if( sSND_FLAG.equals("Y")) {
									recInTemp = JDTORecordFactory.getInstance().create();
						    		recInTemp.setField("MSG_ID"			, "YDY5L006");
						    		recInTemp.setField("YD_GP"			, sYD_GP);
						    		recInTemp.setField("YD_SCH_CD"		, szYD_SCH_CD);
						    		recInTemp.setField("YD_TCAR_SCH_ID"	, sYD_TCAR_SCH_ID);
						    		recInTemp.setField("YD_AIM_BAY_GP"	, sYD_AIM_BAY_GP);
						    		ydDelegate.sendMsg(recInTemp);
								}
							}	
						}
					}	
				}
			}
				
			if(!sFirstYD_WBOOK_ID.equals("")) {
				//재료번호
				rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
				inRecord = JDTORecordFactory.getInstance().create();
				inRecord.setField("YD_SCH_CD",          	szYD_SCH_CD);//스케줄코드
				/*com.inisteel.cim.yd.dao.ydWrkbookDao.getWorkTbRefNONESchCd*/
				intRtnVal = ydWrkbookDao.getYdWrkbook(inRecord, rsResult2, 502);
		    	if(intRtnVal <= 0) {
					JDTORecord[] inRecordarr   	= null;
					inRecordarr = new JDTORecord[1];
					
					inRecordarr[0] = JDTORecordFactory.getInstance().create();
					inRecordarr[0].setField("YD_SCH_CD"		, szYD_SCH_CD); 
					inRecordarr[0].setField("YD_WBOOK_ID"	, sFirstYD_WBOOK_ID); 
					
					ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
					outRecord21 = (JDTORecord) ejbConn.trx("trxRunSchedule", new Class[] { JDTORecord[].class },	new Object[] { inRecordarr });
		
					sRTN_CD		= StringHelper.evl(outRecord21.getFieldString("RTN_CD"), "0");
					sRTN_MSG	= StringHelper.evl(outRecord21.getFieldString("RTN_MSG"), "");
					ydUtils.putLog(szSessionName, szMethodName, "//" + sRTN_MSG, YdConstant.DEBUG);
					if (!("1".equals(sRTN_CD))) {
						gdRes.setMessage(sRTN_MSG);		
						m_ctx.setRollbackOnly();
					} else {
		
						gdRes.setMessage(sRTN_MSG + " <br> 정상적으로 스케쥴까지 등록했습니다.");		
			
					} 	
		    	}	
			}
				
				
			szMsg = "다수코일  작업예약  완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);					
			
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		return gdRes;
	} // end of updColUnitMvstkReg
	
	
	
	/**
	 * 야드관리 > 코일소재야드 > 크레인실적관리 > 스판단위이적등록 (이적가능 Count, 예약 Count조회)
	 * @ejb.interface-method
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @작성자 :박지열
	 * @작성일 : 2010.04.22
	 */
	public GridData getToDongUseCount(GridData inDto) throws DAOException {
		//LOG
	
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		String szMethodName = "getToDongUseCount";	
		try{
			
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			gdRes = (GridData)ejbConn.trx("getToDongUseCount", new Class[] { GridData.class }, new Object[] { inDto });

			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		return gdRes;
	} // end of getSpanMvList
	
	
	
	/**
	 * 적치베드 조회 (저장집합코드) 화면:위치검색SPAN관리 하단 좌측 그리드 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getSpanbyLowInfo(GridData inDto){
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		String szMethodName = "getSpanbyLowInfo";	
		try{
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			gdRes = (GridData)ejbConn.trx("getSpanbyLowInfo", new Class[] { GridData.class }, new Object[] { inDto });

			//gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}


		return gdRes;
	} // end of getSpanbyLowInfo

	
	/**
	 * 야드관리 > 코일소재야드 > 기준관리 > 저장위치용도관리  목록조회
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.04.26
	 */
	public GridData getStrlocUsgSetList(GridData inDto){
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		String szMethodName = "getStrlocUsgSetList";	
		try{
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			gdRes = (GridData)ejbConn.trx("getStrlocUsgSetList", new Class[] { GridData.class }, new Object[] { inDto });
			
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		
		return gdRes;
	} // end of getSpanbyLowInfo
	
	
	/**
	 * 야드관리 > 코일소재야드 > 기준관리 > 저장위치용도관리  등록
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.04.27
	 */
	public GridData updStrlocUsgSet(GridData inDto){
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		String szMethodName = "updStrlocUsgSet";	
		try{
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			gdRes = (GridData)ejbConn.trx("updStrlocUsgSet", new Class[] { GridData.class }, new Object[] { inDto });
			
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		
		return gdRes;
	} // end of getSpanbyLowInfo
	
	/**
	 * 코일야드  스케줄 기동
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * 송정
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData trxRunSchedule(GridData gdReq) throws DAOException {
		// LOG
		String szMsg = "";
		String szMethodName = "trxRunSchedule";
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		YdEqpDao	ydEqpDao 	= new YdEqpDao();
		String szYD_SCH_CD = "";
		JDTORecordSet 	rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord   	recPara   	= JDTORecordFactory.getInstance().create();
		JDTORecord   	outRecord1  = JDTORecordFactory.getInstance().create();
		int intRtnVal = 0;

		try {
			
			szMsg = "JSP-FACADE [코일야드 스케줄 기동 ] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			//JDTORecord[] inRecord = CmUtil.genJDTORecordSet(gdReq);
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			
			JDTORecord[] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
			szMsg = "JSP-FACADE [그리드 데이터 =>JDTORecord[] 변환 완료  ]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			//저장위치 좌표설정화면 BED 수정
			for(int x=0;x<inRecord.length;x++){
				szYD_SCH_CD = inRecord[x].getFieldString("YD_SCH_CD");
				rsResult1 	= JDTORecordFactory.getInstance().createRecordSet("");
				recPara 	= JDTORecordFactory.getInstance().create();			
				recPara.setField("YD_SCH_CD",      szYD_SCH_CD);
				// DAO 호출				
				/*com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpTcBreYDB521*/
				intRtnVal = ydEqpDao.getYdEqp(recPara, rsResult1, 521);
				if(intRtnVal == 0) {
				} else {	
					gdRes.setMessage("기동할수 없는 스케쥴입니다." + szYD_SCH_CD + "<br> (업무기준에서 변경:코일제품야드>YDB521 에서 삭제유무 Y로 변경)");		
					m_ctx.setRollbackOnly();
					return gdRes;
				}	
			}
			
			
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			outRecord1 = (JDTORecord)ejbConn.trx("trxRunSchedule", new Class[] { JDTORecord[].class },	new Object[] { inRecord });
			String sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
			String sRTN_MSG		= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
			if (!("1".equals(sRTN_CD))) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
			} else {

				if ("".equals(sRTN_MSG)) {
					gdRes.setMessage("코일야드 스케줄 기동 성공");	
				} else {
					gdRes.setMessage(sRTN_MSG);	
				}
	
			} 	
		} catch (Exception e) {
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}

		return gdRes;
	} // end of trxRunSchedule

	/**
	 * 코일소재야드 tracking 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * 송
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData getCoilYdHrTracking(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdHrTracking";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdHrTracking", inRecord);
			//gdRes = CmUtil.genGridData(inDto , recordSet);
			gdRes     = CmUtil.genResGridData(inDto , recordSet);

		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilYdHrTracking


	/**
	 * 코일소재야드 tracking 상세조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData getCoilYdHrTrackingDtl(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdHrTrackingDtl";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdHrTrackingDtl", inRecord);
			//gdRes = CmUtil.genGridData(inDto , recordSet);
			gdRes     = CmUtil.genResGridData(inDto , recordSet);

		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilYdInPlanDtl
	
	/**
	 * 코일소재야드 tracking 조회백업
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * 송
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData getCoilYdHrTrackingBackUp(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdHrTrackingBackUp";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdHrTrackingBackUp", inRecord);
			//gdRes = CmUtil.genGridData(inDto , recordSet);
			gdRes     = CmUtil.genResGridData(inDto , recordSet);

		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilYdHrTracking

	/**
	 * 코일소재야드 tracking 백업 상세조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData getCoilYdHrTrackingBackUpDtl(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdHrTrackingBackUpDtl";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdHrTrackingBackUpDtl", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilYdInPlanDtl
	

	/**
	 * 코일소재야드 tracking 팝업 조회보급
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData getcoilYdLineWrPp(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getcoilYdLineWrPp";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getcoilYdLineWrPp", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getcoilYdLineWrPp
	
	/**
	 * 코일소재야드 tracking 팝업 코드조회보급
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData getcoilYdLineWrCodePp(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getcoilYdLineWrCodePp";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getcoilYdLineWrCodePp", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getcoilYdLineWrPp
	
	/**
	 * 코일소재야드 tracking 팝업 조회보급등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData inscoilYdLineWrPp(GridData gdReq) throws DAOException {
		//LOG
		String szMsg="";
		String szEqp_id="";
		String szMethodName="inscoilYdLineWrPp";
		JDTORecord   inRecord   	= JDTORecordFactory.getInstance().create();
		JDTORecord   outRecord   	= JDTORecordFactory.getInstance().create();
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		
		try{
			//JDTORecord paramRec = CmUtil.genJDTORecord(gdReq);
			JDTORecord [] inRecordArr =  ydComUtil.genJDTORecordSet(gdReq);
			
//			 * TREAT_GP	처리구분	C	1	Y	1:보급, 2:보급취소, 3:추출, 4:Take-Out, 5:Take-In				
//			 * STL_NO	재료번호	C	11	Y					
//           * EQP_GP	설비구분	C	6		보급, Take-In 요구시 Coil 위치			
			for(int i=0; i<inRecordArr.length; i++){
				inRecord.setField("TREAT_GP"		, "1"); 
				szEqp_id = inRecordArr[i].getFieldString("PARA_YD_EQP_ID");
				
				//결속대 BACKUP인 경우 
				if(szEqp_id.equals("HFFE02") || szEqp_id.equals("HDFE03")|| szEqp_id.equals("HBFE05")){
					
					inRecord.setField("JMS_TC_CD"		, "HRYDJ008"); 
					if(szEqp_id.equals("HFFE02") ){
						inRecord.setField("WORD_PROC"		, "FH"); 
					}else if(szEqp_id.equals("HDFE03")){
						inRecord.setField("WORD_PROC"		, "DH");
					}else if(szEqp_id.equals("HBFE05")){
						inRecord.setField("WORD_PROC"		, "BH");
					}
					
				}else{
					inRecord.setField("JMS_TC_CD"		, "H2YDL001"); 
				}
				inRecord.setField("STL_NO"			, inRecordArr[i].getFieldString("COIL_NO")); 
				inRecord.setField("YD_EQP_ID"		, inRecordArr[i].getFieldString("PARA_YD_EQP_ID")); 
				//inRecord.setField("YD_STK_BED_NO"	, inRecordArr[i].getFieldString("PARA_YD_STK_BED_NO")); 
				
				
				ejbConn = new EJBConnector("default", "CoilIssueWrkDmdSeEJB", this);
				outRecord = (JDTORecord)ejbConn.trx("procCHrShearInSupLotComp", new Class[] { JDTORecord.class }, new Object[] { inRecord });
	
				
				gdRes = OperateGridData.cloneResponseGridData(gdReq);
				gdRes = CmUtil.copyGDParam(gdReq, gdRes);
	
				ydUtils.putLog(szSessionName, szMethodName, "스케줄 기동", YdConstant.INFO);
				
	//			스케줄 기동 			
				String sRTN_CD		= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
				String sRTN_MSG		= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
				//sYD_SCH_CD 			= StringHelper.evl(outRecord.getFieldString("YD_SCH_CD"), "");
				//String sYD_WBOOK_ID	= StringHelper.evl(outRecord.getFieldString("YD_WBOOK_ID"), "");
				
				ydUtils.putLog(szSessionName, szMethodName, "/" + sRTN_MSG, YdConstant.DEBUG);
				
				if ("1".equals(sRTN_CD)) {
				
					gdRes.setMessage("작업예약등록을 하였습니다..");			
					ydUtils.putLog(szSessionName, szMethodName, "스케줄 기동처리 완료", YdConstant.INFO);
					gdRes.setMessage("스케쥴 기동 처리하였습니다.");				
					
				}else {
					
					gdRes.setMessage(sRTN_MSG);		
					m_ctx.setRollbackOnly();
					return gdRes;
	
				}	
			}
			
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return gdRes;
	}  //end of inscoilYdLineWrPp	
	
	/**
	 * 코일소재야드 tracking 팝업 조회보급취소등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updcoilYdLineWrCancelPp(GridData gdReq) throws DAOException {
		//LOG

		String szMsg				= "";
		String szMethodName			= "updcoilYdLineWrCancelPp";
		JDTORecord inRecord			= JDTORecordFactory.getInstance().create();

		JDTORecord outRecord   		= JDTORecordFactory.getInstance().create();
		JDTORecord outRecord1   	= JDTORecordFactory.getInstance().create();
		JDTORecord[] inRecordarr   	= null;
		String sYD_SCH_CD 			= "";
		String szSTL_NO 			= "";
		//String szEQP_GP 			= "";
		String sYD_CRN_SCH_ID   	= "";
		String sRTN_CD          	= "";
		String sCRANE_WR_SND_YN 	= "";
		String sMODIFIER 			= "";
		String sYD_EQP_ID 			= "";
		String sYD_WBOOK_ID			= null;		
		GridData gdRes 				= null;
		EJBConnector ejbConn 		= null;

		YdDelegate ydDelegate 	= new YdDelegate();

		String rtnMsg				= "";
		String sRTN_MSG				= "";
		String sCRANE_SND			= "";
		String sYD_WRK_PROG_STAT	= "";

		try{

			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			//JDTORecord paramRec = CmUtil.genJDTORecord(gdReq);
			JDTORecord [] inRecordArr =  ydComUtil.genJDTORecordSet(gdReq);
			for(int i=0; i< inRecordArr.length; i++){

				
				szSTL_NO 	= inRecordArr[i].getFieldString("COIL_NO");
				//szEQP_GP 	= inRecordArr[i].getFieldString("PARA_EQP_GP");
				sMODIFIER 	= inRecordArr[i].getFieldString("YD_USER_ID");

				inRecord = JDTORecordFactory.getInstance().create();
				inRecord.setField("STL_NO",     	szSTL_NO);								//재료번호

				ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
				outRecord = (JDTORecord)ejbConn.trx("updcoilYdLineWrCancelPp", new Class[] { JDTORecord.class }, new Object[] { inRecord });
				sRTN_CD			= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
				sYD_CRN_SCH_ID 	= StringHelper.evl(outRecord.getFieldString("YD_CRN_SCH_ID"), "");
				sYD_SCH_CD 		= StringHelper.evl(outRecord.getFieldString("YD_SCH_CD"), "");
				sYD_WBOOK_ID 	= StringHelper.evl(outRecord.getFieldString("YD_WBOOK_ID"), "");
				if ("0".equals(sRTN_CD)) {
					szMsg = "작업예약 등록시 ERROR";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					gdRes.setMessage("보급취소 실패");		
					return gdRes;
				} 
				ydUtils.putLog(szSessionName, szMethodName, "sYD_CRN_SCH_ID:"+sYD_CRN_SCH_ID, YdConstant.DEBUG);
				ydUtils.putLog(szSessionName, szMethodName, "sYD_WBOOK_ID:"+sYD_WBOOK_ID, YdConstant.DEBUG);
			//
				if (!("".equals(sYD_CRN_SCH_ID))) {
					// 작업취소 호출
					inRecord   	= JDTORecordFactory.getInstance().create();
					inRecord.setField("YD_CRN_SCH_ID"	,sYD_CRN_SCH_ID);
					inRecord.setField("YD_SCH_CD"		,sYD_SCH_CD);
					inRecord.setField("DEL_YN"			,"N");
					inRecord.setField("MODIFIER"		,sMODIFIER);

					ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);			
					outRecord = (JDTORecord)ejbConn.trx("WrkCancel", new Class[] { JDTORecord.class }, new Object[] { inRecord });
					sRTN_CD				= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
					sRTN_MSG			= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
					sCRANE_SND			= StringHelper.evl(outRecord.getFieldString("CRANE_SND"), "");
					sYD_CRN_SCH_ID		= StringHelper.evl(outRecord.getFieldString("YD_CRN_SCH_ID"), "");
					sYD_WRK_PROG_STAT	= StringHelper.evl(outRecord.getFieldString("YD_WRK_PROG_STAT"), "");

					if ("0".equals(sRTN_CD)) {
						gdRes.setMessage(sRTN_MSG);		
						m_ctx.setRollbackOnly();
						return gdRes;	
					}	
					//작업예약삭제
					ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);			
					outRecord1 = (JDTORecord)ejbConn.trx("delWookBook", new Class[] { JDTORecord.class }, new Object[] { outRecord });
					sRTN_CD				= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
					sRTN_MSG			= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
					sYD_EQP_ID			= StringHelper.evl(outRecord1.getFieldString("YD_EQP_ID"), "");
					sYD_SCH_CD			= StringHelper.evl(outRecord1.getFieldString("YD_SCH_CD"), "");
					sCRANE_WR_SND_YN	= StringHelper.evl(outRecord1.getFieldString("CRANE_WR_SND_YN"), "");
					if ("0".equals(sRTN_CD)) {
						gdRes.setMessage(sRTN_MSG);		
						m_ctx.setRollbackOnly();
						return gdRes;
					}	

					//작업취소전문 송신
					if ("Y".equals(sCRANE_SND)) {
						inRecord   = JDTORecordFactory.getInstance().create();
						inRecord.setField("MSG_ID",           "YDY5L004"        );
						inRecord.setField("YD_CRN_SCH_ID",    sYD_CRN_SCH_ID          ); 
						inRecord.setField("YD_WRK_PROG_STAT", sYD_WRK_PROG_STAT    );   // 이모듈을 탈려면 항상 '1'의값이 들어옴
						inRecord.setField("MSG_GP",           "D"                );

						ydDelegate.sendMsg(inRecord);
					}	

					//스케줄기동 전문 송신
					if ("Y".equals(sCRANE_WR_SND_YN)) {

						inRecord   = JDTORecordFactory.getInstance().create();
						inRecord.setField("MSG_ID",      		"YDYDJ643" );
						inRecord.setField("YD_EQP_ID",    		sYD_EQP_ID );					   
						inRecord.setField("YD_WRK_PROG_STAT", 	"4" );
						inRecord.setField("YD_SCH_CD", 			sYD_SCH_CD);  

						ejbConn = new EJBConnector("default", this);	
						ejbConn.trx("CoilCraneLdHdSeEJB", "procY5CrnWrkOrdReq", inRecord);
//						ydDelegate.sendMsg(inRecord);

					}	

				} else if (!("".equals(sYD_WBOOK_ID))) {
					// 작업예약취소  호출	
					inRecordarr = new JDTORecord[1];

					inRecordarr[0] = JDTORecordFactory.getInstance().create();
					inRecordarr[0].setField("YD_WBOOK_ID"		, sYD_WBOOK_ID); 
					inRecordarr[0].setField("YD_USER_ID"	    , sMODIFIER); 

					ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
					rtnMsg = (String)ejbConn.trx("delYdWrkbook",
							new Class[] { JDTORecord[].class }, new Object[] { inRecordarr });

				}

				inRecord = JDTORecordFactory.getInstance().create();
				inRecord.setField("MSG_ID",      	"YDHRJ001"); 							//TAKEOUT
				inRecord.setField("STL_NO",     	szSTL_NO);								//재료번호
				
				inRecord.setField("YD_EQP_ID",     	"HGFE01");								//의미없음
				inRecord.setField("YD_STK_BED_NO", 	"06");							  	    //의미없음

				inRecord.setField("TREAT_GP", 		"2");    // 취소		

//SJH10.29				ejbConn = new EJBConnector("default", "CoilIssueWrkDmdSeEJB", this);
//SJH10.29				outRecord = (JDTORecord)ejbConn.trx("procCHrShearInSupLotComp", new Class[] { JDTORecord.class }, new Object[] { inRecord });

				ydDelegate.sendMsg(inRecord);

				szMsg="열연조업 L3 정정보급취소 전송 송신 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				


				gdRes.setMessage("열연조업 L3 정정보급취소 실적 전송 송신 완료..");	
			}


		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}

		return gdRes;

	}  //end of updcoilYdLineWrCancelPp	
	
	
	/**
	 * 코일소재야드 tracking 팝업 조회보급취소등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecord
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public JDTORecord updcoilYdLineWrCancelPpHR(JDTORecord inRecord2) throws DAOException {
		//LOG

		String szMsg				= "";
		String szMethodName			= "updcoilYdLineWrCancelPpHR";
		JDTORecord inRecord			= JDTORecordFactory.getInstance().create();

		JDTORecord outRecord   		= JDTORecordFactory.getInstance().create();
		JDTORecord outRecord1   	= JDTORecordFactory.getInstance().create();
		JDTORecord[] inRecordarr   	= null;
		String sYD_SCH_CD 			= "";
		String szSTL_NO 			= "";
		//String szEQP_GP 			= "";
		String sYD_CRN_SCH_ID   	= "";
		String sRTN_CD          	= "";
		String sCRANE_WR_SND_YN 	= "";
		String sMODIFIER 			= "";
		String sYD_EQP_ID 			= "";
		String sYD_WBOOK_ID			= null;		
		JDTORecord gdRes 			=  JDTORecordFactory.getInstance().create();
		EJBConnector ejbConn 		= null;

		YdDelegate ydDelegate 	= new YdDelegate();

		String rtnMsg				= "";
		String sRTN_MSG				= "";
		String sCRANE_SND			= "";
		String sYD_WRK_PROG_STAT	= "";

		try{
 
				szSTL_NO 	= StringHelper.evl(inRecord2.getFieldString("COIL_NO"), "");
				sMODIFIER 	= StringHelper.evl(inRecord2.getFieldString("YD_USER_ID"), "");

				inRecord = JDTORecordFactory.getInstance().create();
				inRecord.setField("STL_NO",     	szSTL_NO);								//재료번호

				ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
				outRecord = (JDTORecord)ejbConn.trx("updcoilYdLineWrCancelPp", new Class[] { JDTORecord.class }, new Object[] { inRecord });
				sRTN_CD			= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
				sYD_CRN_SCH_ID 	= StringHelper.evl(outRecord.getFieldString("YD_CRN_SCH_ID"), "");
				sYD_SCH_CD 		= StringHelper.evl(outRecord.getFieldString("YD_SCH_CD"), "");
				sYD_WBOOK_ID 	= StringHelper.evl(outRecord.getFieldString("YD_WBOOK_ID"), "");
				if ("0".equals(sRTN_CD)) {
					szMsg = "작업예약 등록시 ERROR";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					gdRes.setField("RTN_CD","보급취소 실패");		
					return gdRes;
				} 
				ydUtils.putLog(szSessionName, szMethodName, "sYD_CRN_SCH_ID:"+sYD_CRN_SCH_ID, YdConstant.DEBUG);
				ydUtils.putLog(szSessionName, szMethodName, "sYD_WBOOK_ID:"+sYD_WBOOK_ID, YdConstant.DEBUG);
			//
				if (!("".equals(sYD_CRN_SCH_ID))) {
					// 작업취소 호출
					inRecord   	= JDTORecordFactory.getInstance().create();
					inRecord.setField("YD_CRN_SCH_ID"	,sYD_CRN_SCH_ID);
					inRecord.setField("YD_SCH_CD"		,sYD_SCH_CD);
					inRecord.setField("DEL_YN"			,"N");
					inRecord.setField("MODIFIER"		,sMODIFIER);

					ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);			
					outRecord = (JDTORecord)ejbConn.trx("WrkCancel", new Class[] { JDTORecord.class }, new Object[] { inRecord });
					sRTN_CD				= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
					sRTN_MSG			= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
					sCRANE_SND			= StringHelper.evl(outRecord.getFieldString("CRANE_SND"), "");
					sYD_CRN_SCH_ID		= StringHelper.evl(outRecord.getFieldString("YD_CRN_SCH_ID"), "");
					sYD_WRK_PROG_STAT	= StringHelper.evl(outRecord.getFieldString("YD_WRK_PROG_STAT"), "");

					if ("0".equals(sRTN_CD)) {
						gdRes.setField("RTN_CD",sRTN_MSG);		
						m_ctx.setRollbackOnly();
						return gdRes;	
					}	
					//작업예약삭제
					ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);			
					outRecord1 = (JDTORecord)ejbConn.trx("delWookBook", new Class[] { JDTORecord.class }, new Object[] { outRecord });
					sRTN_CD				= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
					sRTN_MSG			= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
					sYD_EQP_ID			= StringHelper.evl(outRecord1.getFieldString("YD_EQP_ID"), "");
					sYD_SCH_CD			= StringHelper.evl(outRecord1.getFieldString("YD_SCH_CD"), "");
					sCRANE_WR_SND_YN	= StringHelper.evl(outRecord1.getFieldString("CRANE_WR_SND_YN"), "");
					if ("0".equals(sRTN_CD)) {
						gdRes.setField("RTN_CD",sRTN_MSG);		
						m_ctx.setRollbackOnly();
						return gdRes;
					}	

					//작업취소전문 송신
					if ("Y".equals(sCRANE_SND)) {
						inRecord   = JDTORecordFactory.getInstance().create();
						inRecord.setField("MSG_ID",           "YDY5L004"        );
						inRecord.setField("YD_CRN_SCH_ID",    sYD_CRN_SCH_ID          ); 
						inRecord.setField("YD_WRK_PROG_STAT", sYD_WRK_PROG_STAT    );   // 이모듈을 탈려면 항상 '1'의값이 들어옴
						inRecord.setField("MSG_GP",           "D"                );

						ydDelegate.sendMsg(inRecord);
					}	

					//스케줄기동 전문 송신
					if ("Y".equals(sCRANE_WR_SND_YN)) {

						inRecord   = JDTORecordFactory.getInstance().create();
						inRecord.setField("MSG_ID",      		"YDYDJ643" );
						inRecord.setField("YD_EQP_ID",    		sYD_EQP_ID );					   
						inRecord.setField("YD_WRK_PROG_STAT", 	"4" );
						inRecord.setField("YD_SCH_CD", 			sYD_SCH_CD);  

						ejbConn = new EJBConnector("default", this);	
						ejbConn.trx("CoilCraneLdHdSeEJB", "procY5CrnWrkOrdReq", inRecord);
//						ydDelegate.sendMsg(inRecord);

					}	

				} else if (!("".equals(sYD_WBOOK_ID))) {
					// 작업예약취소  호출	
					inRecordarr = new JDTORecord[1];

					inRecordarr[0] = JDTORecordFactory.getInstance().create();
					inRecordarr[0].setField("YD_WBOOK_ID"		, sYD_WBOOK_ID); 
					inRecordarr[0].setField("YD_USER_ID"	    , sMODIFIER); 

					ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
					rtnMsg = (String)ejbConn.trx("delYdWrkbook",
							new Class[] { JDTORecord[].class }, new Object[] { inRecordarr });

				}

				inRecord = JDTORecordFactory.getInstance().create();
				inRecord.setField("MSG_ID",      	"YDHRJ001"); 							//TAKEOUT
				inRecord.setField("STL_NO",     	szSTL_NO);								//재료번호
				
				inRecord.setField("YD_EQP_ID",     	"HGFE01");								//의미없음
				inRecord.setField("YD_STK_BED_NO", 	"06");							  	    //의미없음

				inRecord.setField("TREAT_GP", 		"2");    // 취소		
				ydDelegate.sendMsg(inRecord);

				szMsg="열연조업 L3 정정보급취소 전송 송신 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				gdRes.setField("RTN_CD","열연조업 L3 정정보급취소 실적 전송 송신 완료..");	
 
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}

		return gdRes;

	}  //end of updcoilYdLineWrCancelPpHR	
	
	/**
	 * 코일소재야드 tracking 팝업 TakeIn 등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updcoilYdLineWrTakeInPp(GridData gdReq) throws DAOException {
		//LOG

		String szMsg="";
		String szMethodName="updcoilYdLineWrTakeInPp";
		JDTORecord inRecord		= JDTORecordFactory.getInstance().create();
		JDTORecord outRecord   	= JDTORecordFactory.getInstance().create();
		String szSTL_NO 		= "";
		String szEQP_GP 		= "";
		String sYD_EQP_ID 		= "";
		String sBED   			= "";
		String sBED2   			= "";


		GridData gdRes = null;
		EJBConnector ejbConn 	= null;

		try{

			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);

			//JDTORecord paramRec = CmUtil.genJDTORecord(gdReq);
			JDTORecord [] inRecordArr =  ydComUtil.genJDTORecordSet(gdReq);
			
			for(int i=0; i< inRecordArr.length; i++){
				szSTL_NO 	= inRecordArr[i].getFieldString("COIL_NO");
				szEQP_GP 	= inRecordArr[i].getFieldString("PARA_EQP_GP");
				sYD_EQP_ID 	= inRecordArr[i].getFieldString("PARA_YD_EQP_ID");
				sBED 		= inRecordArr[i].getFieldString("YD_STK_BED_NO");
				
				ydUtils.putLog(szSessionName, szMethodName, sBED, YdConstant.DEBUG);
				
				inRecord		= JDTORecordFactory.getInstance().create();
				inRecord.setField("MSG_ID"			, "H2YDL004"); 							//열연조업 take_in  전문코드
				inRecord.setField("STL_NO"			, szSTL_NO);								//재료번호
				inRecord.setField("YD_EQP_ID"		, sYD_EQP_ID); 
				inRecord.setField("YD_STK_BED_NO"	, sBED);							  	   
//				크레인 및 작업 예약 확인
				ejbConn = new EJBConnector("default", "CoilIssueWrkDmdSeEJB", this);
				outRecord = (JDTORecord)ejbConn.trx("procCHrShearInSupLotComp", new Class[] { JDTORecord.class }, new Object[] { inRecord });


				szMsg="열연조업 TAKE_IN 전송 송신 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				gdRes.setMessage("열연조업 L3 TAKE_IN 실적 전송 송신 완료..");	
			}


		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return gdRes;

	}  //end of updcoilYdLineWrTakeInPp	
	

	/**
	 * 코일소재야드 tracking 팝업 TakeOut 등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updcoilYdLineWrTakeOutPp(GridData gdReq) throws DAOException {
		//LOG
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao(); 
		String szMsg="";
		String szMethodName="updcoilYdLineWrTakeOutPp";

		JDTORecordSet rsResult1 = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord inRecord		= JDTORecordFactory.getInstance().create();
		JDTORecord inRecord1	= JDTORecordFactory.getInstance().create();
		JDTORecord outRecord   	= JDTORecordFactory.getInstance().create();
		JDTORecord recCarSch   	= JDTORecordFactory.getInstance().create();
		
		String szSTL_NO 		= "";
		String szEQP_GP 		= "";
		String sYD_EQP_ID 		= "";
		String sBED   			= "";
		int intRtnVal           = 0;
		String sYD_STK_BED_NO   = "";
		GridData gdRes = null;
		EJBConnector ejbConn 	= null;

		try{

			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);

			//JDTORecord paramRec = CmUtil.genJDTORecord(gdReq);
			JDTORecord [] inRecordArr =  ydComUtil.genJDTORecordSet(gdReq);

			for(int i=0; i< inRecordArr.length; i++){
				szSTL_NO 	= inRecordArr[i].getFieldString("COIL_NO");
				szEQP_GP 	= inRecordArr[i].getFieldString("PARA_EQP_GP");
				sYD_EQP_ID 	= inRecordArr[i].getFieldString("PARA_YD_EQP_ID");
				
				inRecord		= JDTORecordFactory.getInstance().create();
				inRecord.setField("MSG_ID"			, "H2YDL005"); 							//열연조업 take_out  전문코드
				inRecord.setField("STL_NO"			, szSTL_NO);								//재료번호
				inRecord.setField("YD_EQP_ID"		, sYD_EQP_ID); 

				inRecord.setField("ISPTOR"		, gdReq.getParam("ISPTOR").trim()); 
				inRecord.setField("TAKE_OUT_DT"		, ydUtils.getCurDate("yyyyMMddHHmmss"));
				inRecord.setField("TAKE_OUT_CD"		, gdReq.getParam("TAKE_OUT_CD").trim()); 
 
				
				
				if(sYD_EQP_ID.substring(3,4).equals("D")){ 
					inRecord.setField("YD_STK_BED_NO"	, "02"); //추출존
				}else{
//					inRecord.setField("YD_STK_BED_NO"	, "01"); //입측존
					
					rsResult1 = JDTORecordFactory.getInstance().createRecordSet("");
					inRecord1 = JDTORecordFactory.getInstance().create();			
					inRecord1.setField("YD_STK_COL_GP"	, sYD_EQP_ID);
					inRecord1.setField("STL_NO"			, szSTL_NO);
					/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdConvStlNo*/
					intRtnVal = ydStkLyrDao.getYdStklyr(inRecord1, rsResult1, 607);
					if( intRtnVal  > 0 ) {
						rsResult1.absolute(1);
						recCarSch = rsResult1.getRecord();
						sYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recCarSch,"YD_STK_BED_NO");
						inRecord.setField("YD_STK_BED_NO"	, sYD_STK_BED_NO);
					} else {
						inRecord.setField("YD_STK_BED_NO"	, "01"); //입측 강제 set
					}	
				}
				
				ejbConn = new EJBConnector("default", "CoilRcptWrkDmdSeEJB", this);
				outRecord = (JDTORecord)ejbConn.trx("procR3ShearOutLineOffReq", new Class[] { JDTORecord.class }, new Object[] { inRecord });
				String sRTN_CD		= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
				String sRTN_MSG		= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");

				if ("0".equals(sRTN_CD)) {
					gdRes.setMessage(sRTN_MSG);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}	

				szMsg="열연조업 TAKE_OUT 전송 송신 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				gdRes.setMessage("열연조업 L3 TAKE_OUT 실적 완료..");		
			}


		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}

		return gdRes;

	}  //end of insCoilYdEqpSetSpec	
	
	/**
	 *  야드크레인 작업관리 (작업취소) 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData cancleWorkCoilYdCrnWorkMgt(GridData gdReq) throws DAOException {
		//LOG
		YDDataUtil  yddatautil = new YDDataUtil();
		YdDelegate ydDelegate = new YdDelegate();	
		YdEqpDao     ydEqpDao     = new YdEqpDao();
		YdCrnSchDao ydCrnSchDao        = new YdCrnSchDao();
		ymCommonDAO dao = ymCommonDAO.getInstance();
		
		String szMsg        = "";
		String szMethodName = "cancleWorkCoilYdCrnWorkMgt";
		String szOperationName 		= "작업취소 (크레인작업관리 화면)";
		
		GridData     gdRes   = null;
		EJBConnector ejbConn = null;
		JDTORecord inRecord   	= JDTORecordFactory.getInstance().create();
		JDTORecord outRecord   	= JDTORecordFactory.getInstance().create();
		JDTORecord outRecord1  	= JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet  = null;
		JDTORecord    recPara  		= null;
		JDTORecord recDelPara   = null;
		JDTORecord    recTemp  		= null;
		String sRTN_CD		= "";
		String sRTN_MSG		= "";
		
		String sCRANE_SND	= "";
		String sYD_CRN_SCH_ID	= "";
		String sYD_WRK_PROG_STAT	= "";
		String sYD_EQP_ID		= "";
		String sYD_SCH_CD		= "";
		String sCRANE_WR_SND_YN = "";
		int intRtnVal;
		String szLogMsg         	= "";
		String szYdWrkProgStat = "";
		String szYdEqpId = "";
		JDTORecord recEqpInfo = null;
		JDTORecordSet rsEqpInfo = null;
		int intGp;
		String szydEqpStat = "";
		String szEqpAutoCrnMode = "";
		String szEqpAutoCrnYN = "";
		String szRtnValue = "";
		
		try{
			gdRes = OperateGridData.cloneResponseGridData(gdReq);		
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);

//스케쥴 삭제	
			
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			JDTORecord [] inRecordArr =  ydComUtil.genJDTORecordSet(gdReq);
			
			for(int x=0;x<inRecordArr.length;x++){	
				
//				150917 hun 화면 param 대신 DB값을 읽어와서 체크로 변경 ( 사용자 리프레쉬 안함 발생 )
				sYD_CRN_SCH_ID = yddatautil.setDataDefault(inRecordArr[x].getField("YD_CRN_SCH_ID"), "");
				
				outRecSet  = JDTORecordFactory.getInstance().createRecordSet("YD");
				
				recPara   = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID",sYD_CRN_SCH_ID);		
				
				intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, outRecSet, 0);
				
				if(intRtnVal <= 0 )
				{
					szLogMsg = "[JSP Session] " + szOperationName + "해당 크레인스케줄 ID  정보: ["+ sYD_CRN_SCH_ID +" ]조회시 ERROR발생";            		
	        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
	        		gdRes.setMessage("삭제할 스케줄이 없습니다.");
				}else{
				
					outRecSet.first();
					
					recTemp   = JDTORecordFactory.getInstance().create();			
					recTemp = outRecSet.getRecord();
					//recTemp : 스케줄 정보
					
					szYdWrkProgStat = ydDaoUtils.paraRecChkNull(recTemp, "YD_WRK_PROG_STAT");
					szYdEqpId = ydDaoUtils.paraRecChkNull(recTemp, "YD_EQP_ID");
				}
				
				szMsg = "화면의 상태값 ProgStat="+yddatautil.setDataDefault(inRecordArr[x].getField("YD_WRK_PROG_STAT_CD"), "");
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				szMsg = "DB의 상태값 ProgStat="+szYdWrkProgStat;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				// 150812 hun 크레인무인화 AutoCrn 인 경우 작업지시 대기상태 변경후 응답대기
				if(ydEqpDao.chkAutoCrn(szYdEqpId) && !"W".equals(szYdWrkProgStat)){
					szMsg = "chkAutoCrn() true or szC_YD_WRK_PROG_STAT = W  아닌경우";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
					 
					// 작업대기상태 update
//					String szYD_WRK_PROG_STAT_S    = "S";
//					szYD_L2_REQUEST_STAT = szC_YD_WRK_PROG_STAT;
//					
//					sQueryId = "com.inisteel.cim.yd.dao.ydeqpdao.YdCrnSchDao.updYdCrnSchProgStat";
//					intRtnVal = dao.updateData(sQueryId,new Object[]{szYD_WRK_PROG_STAT_S, szYD_L2_REQUEST_STAT, ydDaoUtils.paraRecChkNull(recCheck, "YD_CRN_SCH_ID") });

//			        151002 hun 설비상태 체크하여 Auto일경우 일시정지 상태에서만 가능하게 수정
					//설비 상태 가져 오기********************************************************
					recEqpInfo = JDTORecordFactory.getInstance().create();
					rsEqpInfo = JDTORecordFactory.getInstance().createRecordSet("YD");
					recEqpInfo.setField("YD_EQP_ID" , szYdEqpId);
					// 해당 설비 szChgCrn 로 설비 정보 조회
					intGp = ydEqpDao.getYdEqp(recEqpInfo , rsEqpInfo , 0);
					if (intGp > 0) {
						rsEqpInfo.first();
						recEqpInfo = rsEqpInfo.getRecord();
						// 설비 상태
						szydEqpStat = ydDaoUtils.paraRecChkNull(recEqpInfo , "YD_EQP_STAT");
						// AutoCrn 상태
						szEqpAutoCrnMode = ydDaoUtils.paraRecChkNull(recEqpInfo , "YD_EQP_AUTO_CRN_MODE");
						// AutoCrn 여부
						szEqpAutoCrnYN = ydDaoUtils.paraRecChkNull(recEqpInfo , "YD_EQP_WRK_MODE2");

						if ("A".equals(szEqpAutoCrnYN) || "R".equals(szEqpAutoCrnYN)) {
							if(!"4".equals(szEqpAutoCrnMode) && !"B".equals(szydEqpStat)) {

								szRtnValue = "무인크레인 [" + szYdEqpId + "]이 일시정지나 고장상태가 아니면 취소 할 수 없습니다.";

								gdRes.setMessage(szRtnValue);
								m_ctx.setRollbackOnly();
								return gdRes;
							}
						}
					}
			        //*************************************************************************

					
					//	크레인 스케줄의 취소 전문 전송
					inRecord   	= JDTORecordFactory.getInstance().create();
		    		//작업지시 전문 전송 data setup
					inRecord.setField("YD_EQP_ID", yddatautil.setDataDefault(inRecordArr[x].getField("YD_EQP_ID"), ""));
					inRecord.setField("YD_CRN_SCH_ID", yddatautil.setDataDefault(inRecordArr[x].getField("YD_CRN_SCH_ID"), ""));
					inRecord.setField("MSG_ID", 			"YDY5L004");
					inRecord.setField("YD_WRK_PROG_STAT", 	StringHelper.evl(outRecord.getFieldString("YD_WRK_PROG_STAT_CD"), ""));
					inRecord.setField("YD_SCH_CD",        	yddatautil.setDataDefault(inRecordArr[x].getField("YD_SCH_CD"), ""));
					inRecord.setField("YD_GP",            	"J");
					inRecord.setField("MODIFIER", 			"YDSYSTEM");
					inRecord.setField("MSG_GP", 			"D");
		        	ydDelegate.sendMsg(inRecord);
		        	
		        	szMsg = "["+szSessionName+"] ["+StringHelper.evl(outRecord.getFieldString("YD_WRK_PROG_STAT"), "")+"]인 스케줄["+yddatautil.setDataDefault(inRecordArr[x].getField("YD_CRN_SCH_ID"), "")+"]을 작업 삭제 요청 전송";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					
					sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
					sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
					
					// 작업대기상태 update : 스케쥴 취소와 구분되게 X 로 상태 없데이트 함...
					String szYD_WRK_PROG_STAT_S    = "S";
					String szYD_L2_REQUEST_STAT = "X";
					String szYD_CRN_SCH_ID = yddatautil.setDataDefault(inRecordArr[x].getField("YD_CRN_SCH_ID"), "");
					String sQueryId = "com.inisteel.cim.yd.dao.ydeqpdao.YdCrnSchDao.updYdCrnSchProgStat";
					intRtnVal = dao.updateData(sQueryId,new Object[]{szYD_WRK_PROG_STAT_S, szYD_L2_REQUEST_STAT, szYD_CRN_SCH_ID });
					
					sRTN_CD = String.valueOf(intRtnVal); 
					sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
				}else{
				
					inRecord   	= JDTORecordFactory.getInstance().create();
					inRecord.setField("YD_CRN_SCH_ID"	,yddatautil.setDataDefault(inRecordArr[x].getField("YD_CRN_SCH_ID"), ""));
					inRecord.setField("YD_SCH_CD"		,yddatautil.setDataDefault(inRecordArr[x].getField("YD_SCH_CD"), ""));
					inRecord.setField("DEL_YN"			,yddatautil.setDataDefault(inRecordArr[x].getField("DEL_YN"), ""));
					inRecord.setField("MODIFIER"		,yddatautil.setDataDefault(inRecordArr[x].getField("MODIFIER"), ""));
					inRecord.setField("YD_EQP_ID"		,yddatautil.setDataDefault(inRecordArr[x].getField("YD_EQP_ID"), ""));
								
					ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);			
					outRecord = (JDTORecord)ejbConn.trx("WrkCancel", new Class[] { JDTORecord.class }, new Object[] { inRecord });
					sRTN_CD				= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
					sRTN_MSG			= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
					sCRANE_SND			= StringHelper.evl(outRecord.getFieldString("CRANE_SND"), "");
					sYD_CRN_SCH_ID		= StringHelper.evl(outRecord.getFieldString("YD_CRN_SCH_ID"), "");
					sYD_WRK_PROG_STAT	= StringHelper.evl(outRecord.getFieldString("YD_WRK_PROG_STAT"), "");
		
					if ("0".equals(sRTN_CD)) {
						gdRes.setMessage(sRTN_MSG);		
						m_ctx.setRollbackOnly();
						return gdRes;	
					}	
		//작업예약삭제
					ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);			
					outRecord1 = (JDTORecord)ejbConn.trx("delWookBook", new Class[] { JDTORecord.class }, new Object[] { outRecord });
					sRTN_CD				= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
					sRTN_MSG			= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
					sYD_EQP_ID			= StringHelper.evl(outRecord1.getFieldString("YD_EQP_ID"), "");
					sYD_SCH_CD			= StringHelper.evl(outRecord1.getFieldString("YD_SCH_CD"), "");
					sCRANE_WR_SND_YN	= StringHelper.evl(outRecord1.getFieldString("CRANE_WR_SND_YN"), "");
					if ("0".equals(sRTN_CD)) {
						gdRes.setMessage(sRTN_MSG);		
						m_ctx.setRollbackOnly();
						return gdRes;
					}	
		
		//작업취소전문 송신
					if ("Y".equals(sCRANE_SND)) {
						recDelPara   = JDTORecordFactory.getInstance().create();
						recDelPara.setField("MSG_ID",           "YDY5L004"        );
						recDelPara.setField("YD_CRN_SCH_ID",    sYD_CRN_SCH_ID          ); 
						recDelPara.setField("YD_WRK_PROG_STAT", sYD_WRK_PROG_STAT    );   // 이모듈을 탈려면 항상 '1'의값이 들어옴
						recDelPara.setField("MSG_GP",           "D"                );
						
						ydDelegate.sendMsg(recDelPara);
					}	
		
		//스케줄기동 전문 송신
//					150917 hun 오토이고 W 일때 스케줄 기동 필요없음...( 스케줄 재전송시 기존 선택된놈과 꼬일 소지 있음.. )
					if(ydEqpDao.chkAutoCrn(szYdEqpId) && "W".equals(szYdWrkProgStat)){
					}else{
						szMsg = "chkAutoCrn() true and szC_YD_WRK_PROG_STAT = W  경우 스케줄기동 안함 pass!!";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						if ("Y".equals(sCRANE_WR_SND_YN)) {
		//SJH03004	
							recDelPara   = JDTORecordFactory.getInstance().create();
							recDelPara.setField("MSG_ID",      "YDYDJ643"        );
		//					recDelPara.setField("JMS_TC_CD"            	,  "YDYDJ643");
		
							recDelPara.setField("YD_EQP_ID",    sYD_EQP_ID            );					   
							recDelPara.setField("YD_WRK_PROG_STAT", "4" );
							recDelPara.setField("YD_SCH_CD", 	sYD_SCH_CD);  
		
							ejbConn = new EJBConnector("default", this);	
							ejbConn.trx("CoilCraneLdHdSeEJB", "procY5CrnWrkOrdReq", recDelPara);
		//					ydDelegate.sendMsg(recDelPara);
						}
					}
				}
			}	// end for
			gdRes.setMessage("정상적으로 삭제처리 되었습니다.");		
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		return gdRes;	
	}
	
	/**
	 *  야드크레인 작업관리 (스케줄 취소)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData cancleSchCoilYdCrnWorkMgt(GridData gdReq) throws DAOException {
		//LOG
		YDDataUtil  yddatautil = new YDDataUtil();
		YdEqpDao     ydEqpDao     = new YdEqpDao();
		ymCommonDAO dao = ymCommonDAO.getInstance();
		String szMsg="";
		String szMethodName="cancleSchCoilYdCrnWorkMgt";
		YdDelegate ydDelegate = new YdDelegate();
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecord inRecord   	= JDTORecordFactory.getInstance().create();
		JDTORecord outRecord   	= JDTORecordFactory.getInstance().create();
		JDTORecord outRecord1  	= JDTORecordFactory.getInstance().create();
		String sRTN_CD		= "";
		String sRTN_MSG		= "";
		String szYD_WRK_PROG_STAT = "";
		String isLastSelected = "0";
		
		int intGp;
		String szydEqpStat = "";
		String szEqpAutoCrnMode = "";
		String szEqpId = "";
		String szEqpAutoCrnYN = "";
		JDTORecord recEqpInfo  	= null;
		JDTORecordSet rsEqpInfo  = null;
		String szRtnValue = "";
		boolean autoFlag = false;
		
		try{
			gdRes = OperateGridData.cloneResponseGridData(gdReq);		
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
	
	//스케쥴 삭제		
			JDTORecord [] inRecordArr =  ydComUtil.genJDTORecordSet(gdReq);
			
			for(int x=0;x<inRecordArr.length;x++){	
				
				autoFlag = ydEqpDao.chkAutoCrn(yddatautil.setDataDefault(inRecordArr[x].getField("YD_EQP_ID"), ""));
				szMsg = "chkAutoCrn() =["+autoFlag+"] or szC_YD_WRK_PROG_STAT = "+yddatautil.setDataDefault(inRecordArr[x].getField("YD_WRK_PROG_STAT_CD"), "");
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				if(autoFlag && !"W".equals(yddatautil.setDataDefault(inRecordArr[x].getField("YD_WRK_PROG_STAT_CD"), "")) && 
						!"S".equals(yddatautil.setDataDefault(inRecordArr[x].getField("YD_WRK_PROG_STAT_CD"), "")) ){
					szMsg = "chkAutoCrn() true or szC_YD_WRK_PROG_STAT = W  경우";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
					
//			        151002 hun 설비상태 체크하여 Auto일경우 일시정지 상태에서만 가능하게 수정
					szEqpId = inRecordArr[x].getFieldString("YD_EQP_ID");
					//설비 상태 가져 오기********************************************************
					recEqpInfo = JDTORecordFactory.getInstance().create();
					rsEqpInfo = JDTORecordFactory.getInstance().createRecordSet("YD");
					recEqpInfo.setField("YD_EQP_ID" , szEqpId);
					// 해당 설비 szChgCrn 로 설비 정보 조회
					intGp = ydEqpDao.getYdEqp(recEqpInfo , rsEqpInfo , 0);
					if (intGp > 0) {
						rsEqpInfo.first();
						recEqpInfo = rsEqpInfo.getRecord();
						// 설비 상태
						szydEqpStat = ydDaoUtils.paraRecChkNull(recEqpInfo , "YD_EQP_STAT");
						// AutoCrn 상태
						szEqpAutoCrnMode = ydDaoUtils.paraRecChkNull(recEqpInfo , "YD_EQP_AUTO_CRN_MODE");
						// AutoCrn 여부
						szEqpAutoCrnYN = ydDaoUtils.paraRecChkNull(recEqpInfo , "YD_EQP_WRK_MODE2");

						if ("A".equals(szEqpAutoCrnYN) || "R".equals(szEqpAutoCrnYN)) {
							if (!"4".equals(szEqpAutoCrnMode) && !"B".equals(szydEqpStat)) {

								szRtnValue = "무인크레인 [" + szEqpId + "]이 일시정지이거나 고장상태가 아니면 취소 할 수 없습니다.";

								gdRes.setMessage(szRtnValue);
								m_ctx.setRollbackOnly();
								return gdRes;
							}
						}
					}
			        //*************************************************************************

					
					//	크레인 스케줄의 취소 전문 전송
					inRecord = JDTORecordFactory.getInstance().create();
		    		//작업지시 전문 전송 data setup
					inRecord.setField("YD_EQP_ID", yddatautil.setDataDefault(inRecordArr[x].getField("YD_EQP_ID"), ""));
					inRecord.setField("YD_CRN_SCH_ID", yddatautil.setDataDefault(inRecordArr[x].getField("YD_CRN_SCH_ID"), ""));
					inRecord.setField("MSG_ID", 			"YDY5L004");
					inRecord.setField("YD_WRK_PROG_STAT", 	yddatautil.setDataDefault(inRecordArr[x].getField("YD_WRK_PROG_STAT_CD"), ""));
					inRecord.setField("YD_SCH_CD",        	yddatautil.setDataDefault(inRecordArr[x].getField("YD_SCH_CD"), ""));
					inRecord.setField("YD_GP",            	"J");
					inRecord.setField("MODIFIER", 			"YDSYSTEM");
					inRecord.setField("MSG_GP", 			"D");
		        	ydDelegate.sendMsg(inRecord);
		        	
		        	szMsg = "["+szSessionName+"] 스케줄["+yddatautil.setDataDefault(inRecordArr[x].getField("YD_CRN_SCH_ID"), "")+"]을 스케쥴 취소 요청 전송";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					szMsg = "["+szSessionName+"] ["+StringHelper.evl(outRecord.getFieldString("YD_WRK_PROG_STAT"), "")+"]인 스케줄["+yddatautil.setDataDefault(inRecordArr[x].getField("YD_CRN_SCH_ID"), "")+"]을 작업 삭제 요청 전송";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					
					sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
					sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
					
					// 작업대기상태 update : 작업취소와 구분되게 D 로 상태 없데이트 함...
					String szYD_WRK_PROG_STAT_S    = "S";
					String szYD_L2_REQUEST_STAT = "D";
					String szYD_CRN_SCH_ID = yddatautil.setDataDefault(inRecordArr[x].getField("YD_CRN_SCH_ID"), "");
					String sQueryId = "com.inisteel.cim.yd.dao.ydeqpdao.YdCrnSchDao.updYdCrnSchProgStat";
					int intRtnVal = dao.updateData(sQueryId,new Object[]{szYD_WRK_PROG_STAT_S, szYD_L2_REQUEST_STAT, szYD_CRN_SCH_ID });
					
					sRTN_CD = String.valueOf(intRtnVal); 
					sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
					
				}else{
					szMsg = "chkAutoCrn() true or szC_YD_WRK_PROG_STAT = W  아닌경우= 유인 or 선택중";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
					
					inRecord = JDTORecordFactory.getInstance().create();
					inRecord.setField("YD_CRN_SCH_ID"	,yddatautil.setDataDefault(inRecordArr[x].getField("YD_CRN_SCH_ID"), ""));
					inRecord.setField("YD_SCH_CD"		,yddatautil.setDataDefault(inRecordArr[x].getField("YD_SCH_CD"), ""));
					inRecord.setField("DEL_YN"			,yddatautil.setDataDefault(inRecordArr[x].getField("DEL_YN"), ""));
					inRecord.setField("MODIFIER"		,yddatautil.setDataDefault(inRecordArr[x].getField("MODIFIER"), ""));
					inRecord.setField("YD_EQP_ID"		,yddatautil.setDataDefault(inRecordArr[x].getField("YD_EQP_ID"), ""));
					
					if((x+1)==inRecordArr.length){
						isLastSelected = "1";
					}
					inRecord.setField("IS_LAST_SELECTED" ,isLastSelected);
					
					ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);			
					outRecord 	= (JDTORecord)ejbConn.trx("WrkCancel", new Class[] { JDTORecord.class }, new Object[] { inRecord });
					sRTN_CD		= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
					sRTN_MSG	= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
				}

				if ("0".equals(sRTN_CD)) {
					gdRes.setMessage(sRTN_MSG);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}
				
			}
			gdRes.setMessage("정상적으로 스케줄 취소처리 되었습니다.");	
		
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		
		szMsg = "JSP-FACADE [야드크레인 작업관리 - 스케줄 취소]끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
		return gdRes;	
	}	
	

	/**
	 *  야드크레인 작업관리 (스케줄 재전송)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData reSendSchCoilYdCrnWorkMgt(GridData gdReq) throws DAOException {
		//LOG
		YDDataUtil  yddatautil = new YDDataUtil();
		String szMsg="";
		String szMethodName="reSendSchCoilYdCrnWorkMgt";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecord inRecord   	= JDTORecordFactory.getInstance().create();
		JDTORecord outRecord   	= JDTORecordFactory.getInstance().create();
		String sRTN_CD		= "";
		String sRTN_MSG		= "";
		

		
		try{
			gdRes = OperateGridData.cloneResponseGridData(gdReq);		
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
	
	//스케쥴 재전송
			JDTORecord [] inRecordArr =  ydComUtil.genJDTORecordSet(gdReq);
			
			for(int x=0;x<inRecordArr.length;x++){	
				
				inRecord = JDTORecordFactory.getInstance().create();
				inRecord.setField("YD_CRN_SCH_ID"	,yddatautil.setDataDefault(inRecordArr[x].getField("YD_CRN_SCH_ID"), ""));
				inRecord.setField("YD_SCH_CD"		,yddatautil.setDataDefault(inRecordArr[x].getField("YD_SCH_CD"), ""));
				inRecord.setField("DEL_YN"			,yddatautil.setDataDefault(inRecordArr[x].getField("DEL_YN"), ""));
				inRecord.setField("MODIFIER"		,yddatautil.setDataDefault(inRecordArr[x].getField("MODIFIER"), ""));
				
				ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);			
				outRecord 	= (JDTORecord)ejbConn.trx("reSendSchCoilYdCrnWorkMgt", new Class[] { JDTORecord.class }, new Object[] { inRecord });
				sRTN_CD		= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
				sRTN_MSG	= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
		
				if ("0".equals(sRTN_CD)) {
					gdRes.setMessage(sRTN_MSG);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}	
			}	
			gdRes.setMessage("정상적으로 스케줄 재전송 되었습니다.");		
		
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		
		szMsg = "JSP-FACADE [야드크레인 작업관리 - 스케줄 재전송]끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
		return gdRes;	
	}	
	
	
	/**
	 * 크레인작업예약관리 - 작업예약 삭제(예약번호로 삭제처리 함)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	
	public GridData delYdWrkbook(GridData inDto) throws DAOException {
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
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			rtnMsg = (String)ejbConn.trx("delYdWrkbook",
						new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			
			gdRes.setMessage(rtnMsg);
			
			szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 끝 - 반환메세지 : " + rtnMsg;
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		
		return gdRes;
	}

	/**
	 * 크레인상태관리 - 크레인 변경[신규작성중]
	 * 송정현
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	
	public GridData wrkCrnChange(GridData gdReq) throws JDTOException {						
		GridData gdRes            = null;
		EJBConnector ejbConn      = null;
	
		
		JDTORecord outRecord   	= JDTORecordFactory.getInstance().create();			
		String szMethodName       = "wrkCrnChange";
		String szLogMsg           = "";
		String szOperationName = " 크레인상태관리 - 크레인 변경";
		String szRtnValue =  YdConstant.RETN_CD_SUCCESS;

		try{
			
			szLogMsg = "JSP-FACADE  [ "+ szOperationName +" ]시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			gdRes = OperateGridData.cloneResponseGridData(gdReq);		
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			
			
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);			
			outRecord = (JDTORecord)ejbConn.trx("wrkCrnChange", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			String sRTN_CD	= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
			String sRTN_MSG	= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
	
			if ("0".equals(sRTN_CD)) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
				return gdRes;	
			}	
			
			gdRes.setMessage("크레인 변경 성공");
	
			
		}catch(JDTOException de) {
			
			gdRes.setMessage(szRtnValue);
			
			//Log 
			szLogMsg = "크레인 변경 실패 - DAO Exception ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
		} catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}			
		
		szLogMsg = "JSP-FACADE  [ 크레인상태관리 - 크레인 변경 ] 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	} // end of wrkCrnChange()
	
	
	/**
	 * 야드관리 > 코일소재야드 > 제공관리 > 저장위치관리 조회 (현재는 테스트에 들어 잇음 2010.05.11)
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.05.11
	 */
	public GridData getCoilYdStrlocModMgt(GridData inDto){
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		String szMethodName = "getCoilYdStrlocModMgt";	
		try{
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			gdRes = (GridData)ejbConn.trx("getCoilYdStrlocModMgt", new Class[] { GridData.class }, new Object[] { inDto });
			
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		
		return gdRes;
	} // end of getCoilYdStrlocModMgt
	
	
	/**
	 * 저장위치변경관리 (저장위치수정)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updStrlocModMgt(GridData gdReq) throws DAOException {

		YdCrnSchDao ydCrnschDao  			= new YdCrnSchDao();
		YdWrkbookDao ydWrkbookDao 			= new YdWrkbookDao();
		YdWrkbookMtlDao ydWrkbookMtlDao 	= new YdWrkbookMtlDao();
		YdCarSchDao ydCarSchDao 			= new YdCarSchDao();
		YdStkColDao ydStkColDao 			= new YdStkColDao();
		YdDelegate ydDelegate 				= new YdDelegate();
		YdTcarFtmvMtlDao ydTcarftmvmtlDao 	= new YdTcarFtmvMtlDao();
		YdCarFtmvMtlDao ydCarFtmvMtlDao 	= new YdCarFtmvMtlDao();
		
		String szMsg         				= "";
		String szMethodName  				= "updStrlocModMgt";
		GridData gdRes       				= null;
		EJBConnector ejbConn 				= null;
		String szLogMsg      				= "";
		JDTORecord inRecord1  				= JDTORecordFactory.getInstance().create();			
		JDTORecord inRecord2  				= JDTORecordFactory.getInstance().create();			
		JDTORecord inRecord9  				= JDTORecordFactory.getInstance().create();			
		JDTORecord outRecord0 				= JDTORecordFactory.getInstance().create();			
		JDTORecord outRecord1 				= JDTORecordFactory.getInstance().create();			
		JDTORecord outRecord2 				= JDTORecordFactory.getInstance().create();			
		JDTORecordSet rsResult1				= null;
		JDTORecordSet rsResult2				= null;

		Calendar today						= Calendar.getInstance();
		SimpleDateFormat sdf 				= new SimpleDateFormat("yyyyMMddHHmmss");
		String sRTN_CD				= "";
		String sRTN_MSG				= "";
		
		String sFR_YD_STK_COL_GP	= "";
		String sFR_YD_STK_BED_NO	= "";
		String sFR_YD_EQP_GP		= "";
		
		String sTO_YD_STK_COL_GP	= "";
		String sTO_YD_STK_BED_NO	= "";
		String sTO_YD_EQP_GP		= "";
		String sSTL_NO				= "";
		String sYD_WBOOK_ID         = "";
		String sYD_SCH_CD           = "";
		String sDB_DATE             = sdf.format(today.getTime());
		String sYD_USER_ID          = "";
		String sYD_AIM_YD_GP 		= "";
		String sSND_FLAG			= ""; 
		String sUPD_STK_LYR_NO		= "";
		String sFR_STK_LYR_NO		= "";
		String sUpdToPos			= "";
		String sUpdSend				= "";
		String szYD_EQP_WRK_STAT	= "";
		
		
		int    intRtnVal 			= 0;
		
		try{
	
			szLogMsg = "JSP-FACADE  [ "+ szMethodName +" ]시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);
			
			gdRes = OperateGridData.cloneResponseGridData(gdReq);		
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);

			
			sFR_YD_STK_COL_GP	= gdReq.getHeader("YD_STK_COL_GP").getValue(0); // 야드적치열
			sFR_STK_LYR_NO		= gdReq.getHeader("CURR_STK_LYR_NO").getValue(0); // 적치단
			sFR_YD_STK_BED_NO	= gdReq.getHeader("YD_STK_BED_NO").getValue(0); // 번지
			
			
			sFR_YD_EQP_GP  		= sFR_YD_STK_COL_GP.substring(2, 4);			// fr야드설비구분     
			
			sTO_YD_STK_COL_GP	= gdReq.getHeader("UPD_STK_POS_2").getValue(0); // 야드적치열
			sUPD_STK_LYR_NO		= gdReq.getHeader("UPD_STK_LYR_NO").getValue(0); // 적치단
			sTO_YD_STK_BED_NO	= gdReq.getHeader("UPD_STK_BED_NO").getValue(0);// 
			sTO_YD_EQP_GP  		= sTO_YD_STK_COL_GP.substring(2, 4);			// to야드설비구분     
			sSTL_NO				= gdReq.getHeader("STL_NO").getValue(0);
			sYD_USER_ID			= gdReq.getHeader("YD_USER_ID").getValue(0);					// 사용자ID
			sYD_AIM_YD_GP		= gdReq.getHeader("YD_AIM_YD_GP").getValue(0);					// 사용자ID
			sSND_FLAG			= gdReq.getParam("SND_FLAG");		// 송신 여부
			sUpdToPos			= gdReq.getParam("UPD_TOPOS"); // 저장위치 수정 여부
			sUpdSend			= gdReq.getParam("UPD_SEND"); // 전문송신 여부
			
			ydUtils.putLog(szSessionName, szMethodName, "sFR_YD_STK_COL_GP:"	+ sFR_YD_STK_COL_GP, YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, "sUPD_STK_LYR_NO:"		+ sUPD_STK_LYR_NO, YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, "sFR_YD_STK_BED_NO:"	+ sFR_YD_STK_BED_NO, YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, "sFR_YD_EQP_GP:"		+ sFR_YD_EQP_GP, YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, "sTO_YD_STK_BED_NO:"	+ sTO_YD_STK_BED_NO, YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, "sTO_YD_EQP_GP:"		+ sTO_YD_EQP_GP, YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, "sSTL_NO:"				+ sSTL_NO, YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, "sSND_FLAG:"			+ sSND_FLAG, YdConstant.INFO);
				
			
			///////////////////////////////////////////////////////////////////////
			//저장위치 수정일 경우 	
			///////////////////////////////////////////////////////////////////////
			if(sUpdToPos.equals("TOPOS")){ 
				//======================================================================================
				//YARD 정보 수정:UPDATE TB_YD_STKLYR, TB_YD_STOCK
				ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);			
				outRecord1 	= (JDTORecord)ejbConn.trx("updYDStrlocModMgt", new Class[] { JDTORecord[].class,GridData.class }, new Object[] { inRecord,gdReq });
				sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
				sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");

				if ("0".equals(sRTN_CD) && !sSND_FLAG.equals("STOCKED")) {
					gdRes.setMessage(sRTN_MSG);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}	
				
				rsResult1 = JDTORecordFactory.getInstance().createRecordSet("");
				inRecord1 = JDTORecordFactory.getInstance().create();			
				inRecord1.setField("STL_NO",      sSTL_NO);
				/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getCrnSchByStlNo*/
				intRtnVal = ydCrnschDao.getYdCrnsch(inRecord1, rsResult1, 52);
				if( intRtnVal  > 0 ) {
					gdRes.setMessage("스케쥴이 편성되어 있습니다. 취소처리후 작업하세요");		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}	

				rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
				/* com.inisteel.cim.yd.dao.ydwrkbookmtldao.YdWrkbookmtlDao.getYdWrkbookmtlSTLNO */
				intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(inRecord1, rsResult2, 2);
				if( intRtnVal  > 0 ) {
					rsResult2.first();
					outRecord0 = rsResult2.getRecord();
					sYD_WBOOK_ID 	= ydDaoUtils.paraRecChkNull(outRecord0, "YD_WBOOK_ID");
					sYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(outRecord0, "YD_SCH_CD");
					sDB_DATE 		= ydDaoUtils.paraRecChkNull(outRecord0, "DB_DATE");

				}	

				if(!(sYD_WBOOK_ID.equals(""))){
//					작업 예약 삭제
					inRecord1 = JDTORecordFactory.getInstance().create();
					inRecord1.setField("YD_WBOOK_ID", sYD_WBOOK_ID);
					inRecord1.setField("DEL_YN", "Y");
					inRecord1.setField("STL_NO", sSTL_NO);
					inRecord1.setField("MODIFIER", sYD_USER_ID);
					intRtnVal =  ydWrkbookDao.updYdWrkbook(inRecord1, 0);
					if( intRtnVal < 1 ) {
						szMsg = "[JSP Session : "+szMethodName+"] 작업예약 삭제 시 오류발생 : 반환값 - " + intRtnVal;
						gdRes.setMessage("작업예약재료 삭제 시 오류발생");		
						m_ctx.setRollbackOnly();
						return gdRes;	
					}

					inRecord1 = JDTORecordFactory.getInstance().create();
					inRecord1.setField("YD_WBOOK_ID", sYD_WBOOK_ID);
					inRecord1.setField("DEL_YN", "Y");
					inRecord1.setField("STL_NO", sSTL_NO);
					inRecord1.setField("MODIFIER", sYD_USER_ID);
					intRtnVal = ydWrkbookMtlDao.updYdWrkbookmtl(inRecord1, 0);
					if( intRtnVal < 1 ) {
						szMsg = "[JSP Session : "+szMethodName+"] 작업예약재료 삭제 시 오류발생 : 반환값 - " + intRtnVal;
						gdRes.setMessage("작업예약재료 삭제 시 오류발생");		
						m_ctx.setRollbackOnly();
						return gdRes;	
					}
				}
				
				
				//===============================================================================================================
				// 대차에서 내릴 경우 
				if( sFR_YD_EQP_GP.equals("TC") ){
					if( (sTO_YD_EQP_GP.substring(0,1).equals("0")) ||(sTO_YD_EQP_GP.substring(0,1).equals("1"))){

						inRecord1 = JDTORecordFactory.getInstance().create();
						inRecord1.setField("STL_NO", sSTL_NO);
						rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
						/*com.inisteel.cim.yd.dao.ydtcarftmvmtldao.YdTcarftmvmtlDao.getYdStockTcarSTL_NO*/
						intRtnVal = ydTcarftmvmtlDao.getYdTcarftmvmtl(inRecord1, rsResult2, 302);
						if( intRtnVal <= 0 ) {
							gdRes.setMessage("대차위에  정보가 존재하지 않습니다.");		
						}else{
							//조회된 차량정지위치에서 운송장비코드를 가져온다.
							rsResult2.first();
							outRecord0 = rsResult2.getRecord();
							//운송장비코드
							String szYD_TCAR_SCH_ID	= ydDaoUtils.paraRecChkNull(outRecord0, "YD_TCAR_SCH_ID");

							inRecord1 = JDTORecordFactory.getInstance().create();
							inRecord1.setField("YD_TCAR_SCH_ID"	, szYD_TCAR_SCH_ID);						
							inRecord1.setField("STL_NO"			, sSTL_NO);							
							intRtnVal = ydTcarftmvmtlDao.delYdTcarftmvmtl(inRecord1);
							if(intRtnVal < 1) {
								szMsg="[대차 정보 삭제시 이상 발생 Error!! Code : " + intRtnVal;
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								gdRes.setMessage(szMsg);		
								m_ctx.setRollbackOnly();
								return gdRes;	
							}
						}
					}
				}
				//===============================================================================================================
				// 대차에 올릴경우  
				if( sTO_YD_EQP_GP.equals("TC") ){
					if( (sFR_YD_EQP_GP.substring(0,1).equals("0")) ||(sFR_YD_EQP_GP.substring(0,1).equals("1"))){
						
					}	
				}
				
				inRecord9 = JDTORecordFactory.getInstance().create();
				inRecord9.setField("UPD_STK_POS_2"	, sTO_YD_STK_COL_GP);
				inRecord9.setField("UPD_STK_BED_NO"	, sTO_YD_STK_BED_NO);
				inRecord9.setField("UPD_STK_LYR_NO"	, sUPD_STK_LYR_NO);
				inRecord9.setField("YD_USER_ID"		, sYD_USER_ID);
				inRecord9.setField("STL_NO"			, sSTL_NO);
				
				
				//코일 공통  정보 수정			
				ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);			
				outRecord2 	= (JDTORecord)ejbConn.trx("updCOMMStrlocModMgt", new Class[] { JDTORecord.class }, new Object[] { inRecord9 });
				sRTN_CD		= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
				sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
		
				if ("0".equals(sRTN_CD)) {
					gdRes.setMessage(sRTN_MSG);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}	
				
			}
			
			///////////////////////////////////////////////////////////////////////
			// 전문 송신
			///////////////////////////////////////////////////////////////////////
			if(sUpdSend.equals("SEND")){
				//===============================================================================================================
				// 저장품 생성
				if(sSND_FLAG.equals("STOCK")){ // 저장품()
					inRecord1 = JDTORecordFactory.getInstance().create();
					inRecord1.setField("STL_NO",     	sSTL_NO);								//재료번호

					ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);			
					outRecord1 	= (JDTORecord)ejbConn.trx("insYdStockInfo", new Class[] { JDTORecord.class }, new Object[] { inRecord1 });

					sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
					sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");

					if (!sRTN_CD.equals("0")) {
						gdRes.setMessage(sRTN_MSG);		
						m_ctx.setRollbackOnly();
						return gdRes;	
					}

				}

				//===============================================================================================================
				// LINE IN
				if(sSND_FLAG.equals("LINEIN")){ 

					inRecord1 = JDTORecordFactory.getInstance().create();
					inRecord1.setField("MSG_ID",        "YDHRJ001"); 							//열연조업 L3 정정보급완료 실적  전문코드
					inRecord1.setField("STL_NO",     	sSTL_NO);								//재료번호
					inRecord1.setField("YD_EQP_ID",     sTO_YD_STK_COL_GP);						//야드설비id
					inRecord1.setField("YD_STK_BED_NO", sTO_YD_STK_BED_NO);						//야드적치베드번호
					//inRecord1.setField("YD_DN_CMPL_DT", sDB_DATE);								//야드권하완료일시
					inRecord1.setField("YD_DN_RSLT_DT", sDB_DATE);								//야드권하완료일시
					inRecord1.setField("TREAT_GP", "1");									//1:보급완료, 2:보급취소, 5:Take-In
					ydDelegate.sendMsg(inRecord1);
					szMsg="열연조업 L3 정정보급완료 실적 전송 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

					
					
					//품질 열연정정입측보급실적----------------------------------------------
					inRecord1 = JDTORecordFactory.getInstance().create();
					inRecord1.setField("MSG_ID",      	"YDQMJ002"); 							 
					inRecord1.setField("STL_NO",     	sSTL_NO);	  			//재료번호				
					ydDelegate.sendMsg(inRecord1);

					szMsg="품질 L3 열연정정입측보급실적 전송 송신 완료1";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
					//-------------------------------------------------------------------
					
					
					inRecord2 = JDTORecordFactory.getInstance().create();
					if(( sTO_YD_STK_COL_GP.startsWith("HHKE01")) ||
							( sTO_YD_STK_COL_GP.startsWith("HHKD01")) ){    	/* 코일소재[H]야드 H동 SPM1 보급 */
						inRecord2.setField("MSG_ID",        "YDH2L001");							//전문코드
						inRecord2.setField("STL_NO",     	sSTL_NO);								//재료번호
						inRecord2.setField("YD_EQP_ID",     sTO_YD_STK_COL_GP);			//야드설비ID
						inRecord2.setField("YD_STK_BED_NO", sTO_YD_STK_BED_NO);			//야드적치Bed번호 
						ydDelegate.sendMsg(inRecord2);

						szMsg="열연조업 L2  정정 SPM1 보급완료 실적  전송 송신 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);			        	
					}

					if( sTO_YD_STK_COL_GP.startsWith("HGFE01")){		/* 코일소재[H]야드 G동 HFL 보급 */
						inRecord2.setField("MSG_ID",        "YDH2L011");							//전문코드
						inRecord2.setField("STL_NO",     	sSTL_NO);								//재료번호
						inRecord2.setField("YD_EQP_ID",     sTO_YD_STK_COL_GP);			//야드설비ID
						inRecord2.setField("YD_STK_BED_NO", sTO_YD_STK_BED_NO);			//야드적치Bed번호 
						ydDelegate.sendMsg(inRecord2);

						szMsg="열연조업 L2  정정 HFL 보급완료 실적  전송 송신 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				        	
					}

					if(( sTO_YD_STK_COL_GP.startsWith("HEDE01"))||
							( sTO_YD_STK_COL_GP.startsWith("HEDD01"))){		/* 코일소재[H]야드 E동 SPM2 보급 */
						inRecord2.setField("MSG_ID",        "YDH2L021");							//전문코드
						inRecord2.setField("STL_NO",     	sSTL_NO);								//재료번호
						inRecord2.setField("YD_EQP_ID",     sTO_YD_STK_COL_GP);			//야드설비ID
						inRecord2.setField("YD_STK_BED_NO", sTO_YD_STK_BED_NO);			//야드적치Bed번호 
						ydDelegate.sendMsg(inRecord2);

						szMsg="열연조업 L2  정정 SPM2 보급완료 실적  전송 송신 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				        	
					}	
//C증설	
					if(( sTO_YD_STK_COL_GP.startsWith("HCKE03")) ||
							( sTO_YD_STK_COL_GP.startsWith("HCKD03")) ){    	
						inRecord2.setField("MSG_ID",        "YDH2L031");		
						inRecord2.setField("STL_NO",     	sSTL_NO);								//재료번호
						inRecord2.setField("YD_EQP_ID",     sTO_YD_STK_COL_GP);			//야드설비ID
						inRecord2.setField("YD_STK_BED_NO", sTO_YD_STK_BED_NO);			//야드적치Bed번호 
						ydDelegate.sendMsg(inRecord2);

						szMsg="열연조업 L2  정정 SPM3 보급완료 실적  전송 송신 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);			        	
					}

					if(( sTO_YD_STK_COL_GP.startsWith("HBKE04")) ||
							( sTO_YD_STK_COL_GP.startsWith("HBKD04")) ){    	
						inRecord2.setField("MSG_ID",        "YDH2L041");							//전문코드
						inRecord2.setField("STL_NO",     	sSTL_NO);								//재료번호
						inRecord2.setField("YD_EQP_ID",     sTO_YD_STK_COL_GP);			//야드설비ID
						inRecord2.setField("YD_STK_BED_NO", sTO_YD_STK_BED_NO);			//야드적치Bed번호 
						ydDelegate.sendMsg(inRecord2);

						szMsg="열연조업 L2  정정 SPM4 보급완료 실적  전송 송신 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);			        	
					}
					
					if(( sTO_YD_STK_COL_GP.startsWith("HAKE05")) ||
							( sTO_YD_STK_COL_GP.startsWith("HAKD05")) ){    	
						inRecord2.setField("MSG_ID",        "YDH2L071");							//전문코드
						inRecord2.setField("STL_NO",     	sSTL_NO);								//재료번호
						inRecord2.setField("YD_EQP_ID",     sTO_YD_STK_COL_GP);			//야드설비ID
						inRecord2.setField("YD_STK_BED_NO", sTO_YD_STK_BED_NO);			//야드적치Bed번호 
						ydDelegate.sendMsg(inRecord2);

						szMsg="열연조업 L2  정정 SPM5 보급완료 실적  전송 송신 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);			        	
					}
					
					if( sTO_YD_STK_COL_GP.startsWith("HCFE04")){		
						inRecord2.setField("MSG_ID",        "YDH2L051");							//전문코드
						inRecord2.setField("STL_NO",     	sSTL_NO);								//재료번호
						inRecord2.setField("YD_EQP_ID",     sTO_YD_STK_COL_GP);			//야드설비ID
						inRecord2.setField("YD_STK_BED_NO", sTO_YD_STK_BED_NO);			//야드적치Bed번호 
						ydDelegate.sendMsg(inRecord2);

						szMsg="열연조업 L2  정정 HFL4 보급완료 실적  전송 송신 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				        	
					}
	
					if( sTO_YD_STK_COL_GP.startsWith("HBFE05")){		
						inRecord2.setField("MSG_ID",        "YDH2L061");							//전문코드
						inRecord2.setField("STL_NO",     	sSTL_NO);								//재료번호
						inRecord2.setField("YD_EQP_ID",     sTO_YD_STK_COL_GP);			//야드설비ID
						inRecord2.setField("YD_STK_BED_NO", sTO_YD_STK_BED_NO);			//야드적치Bed번호 
						ydDelegate.sendMsg(inRecord2);

						szMsg="열연조업 L2  정정 HFL5 보급완료 실적  전송 송신 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				        	
					}
					
					
				}	 	

				//===============================================================================================================
				// LINE OFF
				if(sSND_FLAG.equals("LINEOFF")){ 

					inRecord1 = JDTORecordFactory.getInstance().create();
					inRecord1.setField("MSG_ID"       , "YDHRJ002");			//전문코드
					inRecord1.setField("STL_NO"       , sSTL_NO);				//재료번호
					inRecord1.setField("TREAT_GP"       , "3");					//재료번호
					inRecord1.setField("YD_UP_CMPL_DT", sDB_DATE);				//크레인스케줄ID
					ydDelegate.sendMsg(inRecord1);
					szMsg = "열연조업 L3 정정추출완료실적 전문 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

					inRecord2 = JDTORecordFactory.getInstance().create();        	
					if( sFR_YD_STK_COL_GP.startsWith("HHKD01") ){    	
						inRecord2.setField("MSG_ID"       , "YDH2L003");			// SPM1 전문코드
						inRecord2.setField("YD_EQP_ID"    , sFR_YD_STK_COL_GP);		// 야드설비ID
						inRecord2.setField("YD_STK_BED_NO", sFR_YD_STK_BED_NO);		// 야드적치Bed번호 
						inRecord2.setField("STL_NO"       , sSTL_NO);				// 재료번호
						ydDelegate.sendMsg(inRecord2);
						szMsg = "C열연 SPM1 정정출측Line-Off실적 전문(YDH2L003) 송신 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
					}
					if( sFR_YD_STK_COL_GP.startsWith("HGFD01")){
						inRecord2.setField("MSG_ID"       , "YDH2L013");			// HFL 전문코드
						inRecord2.setField("YD_EQP_ID"    , sFR_YD_STK_COL_GP);		// 야드설비ID
						inRecord2.setField("YD_STK_BED_NO", sFR_YD_STK_BED_NO);		// 야드적치Bed번호 
						inRecord2.setField("STL_NO"       , sSTL_NO);				// 재료번호
						ydDelegate.sendMsg(inRecord2);
						szMsg = "C열연 HFL 정정출측Line-Off실적 전문(YDH2L013) 송신 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
					}	
					if( sFR_YD_STK_COL_GP.startsWith("HEDD01")){
						inRecord2.setField("MSG_ID"       , "YDH2L023");			// SPM2 전문코드
						inRecord2.setField("YD_EQP_ID"    , sFR_YD_STK_COL_GP);		// 야드설비ID
						inRecord2.setField("YD_STK_BED_NO", sFR_YD_STK_BED_NO);		// 야드적치Bed번호 
						inRecord2.setField("STL_NO"       , sSTL_NO);				// 재료번호
						ydDelegate.sendMsg(inRecord2);
						szMsg = "C열연 SPM2 정정출측Line-Off실적 전문(YDH2L023) 송신 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
					}
					
//C증설						
					if( sFR_YD_STK_COL_GP.startsWith("HCKD03") ){    	
						inRecord2.setField("MSG_ID"       , "YDH2L033");			// SPM3 전문코드
						inRecord2.setField("YD_EQP_ID"    , sFR_YD_STK_COL_GP);		// 야드설비ID
						inRecord2.setField("YD_STK_BED_NO", sFR_YD_STK_BED_NO);		// 야드적치Bed번호 
						inRecord2.setField("STL_NO"       , sSTL_NO);				// 재료번호
						ydDelegate.sendMsg(inRecord2);
						szMsg = "C열연 SPM3 정정출측Line-Off실적 전문(YDH2L003) 송신 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
					}

					if( sFR_YD_STK_COL_GP.startsWith("HBKD04") ){    	
						inRecord2.setField("MSG_ID"       , "YDH2L043");			// SPM4 전문코드
						inRecord2.setField("YD_EQP_ID"    , sFR_YD_STK_COL_GP);		// 야드설비ID
						inRecord2.setField("YD_STK_BED_NO", sFR_YD_STK_BED_NO);		// 야드적치Bed번호 
						inRecord2.setField("STL_NO"       , sSTL_NO);				// 재료번호
						ydDelegate.sendMsg(inRecord2);
						szMsg = "C열연 SPM4 정정출측Line-Off실적 전문(YDH2L043) 송신 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
					}

					if( sFR_YD_STK_COL_GP.startsWith("HAKD05") ){    	
						inRecord2.setField("MSG_ID"       , "YDH2L073");			// SPM5 전문코드
						inRecord2.setField("YD_EQP_ID"    , sFR_YD_STK_COL_GP);		// 야드설비ID
						inRecord2.setField("YD_STK_BED_NO", sFR_YD_STK_BED_NO);		// 야드적치Bed번호 
						inRecord2.setField("STL_NO"       , sSTL_NO);				// 재료번호
						ydDelegate.sendMsg(inRecord2);
						szMsg = "C열연 SPM5 정정출측Line-Off실적 전문(YDH2L073) 송신 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
					}
					
					if( sFR_YD_STK_COL_GP.startsWith("HCFD04")){
						inRecord2.setField("MSG_ID"       , "YDH2L053");			// HFL4 전문코드
						inRecord2.setField("YD_EQP_ID"    , sFR_YD_STK_COL_GP);		// 야드설비ID
						inRecord2.setField("YD_STK_BED_NO", sFR_YD_STK_BED_NO);		// 야드적치Bed번호 
						inRecord2.setField("STL_NO"       , sSTL_NO);				// 재료번호
						ydDelegate.sendMsg(inRecord2);
						szMsg = "C열연 HFL4 정정출측Line-Off실적 전문(YDH2L013) 송신 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
					}	
					
					if( sFR_YD_STK_COL_GP.startsWith("HBFD05")){
						inRecord2.setField("MSG_ID"       , "YDH2L063");			// HFL5 전문코드
						inRecord2.setField("YD_EQP_ID"    , sFR_YD_STK_COL_GP);		// 야드설비ID
						inRecord2.setField("YD_STK_BED_NO", sFR_YD_STK_BED_NO);		// 야드적치Bed번호 
						inRecord2.setField("STL_NO"       , sSTL_NO);				// 재료번호
						ydDelegate.sendMsg(inRecord2);
						szMsg = "C열연 HFL5 정정출측Line-Off실적 전문(YDH2L013) 송신 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
					}	

					
					
					if(sFR_YD_STK_COL_GP.substring(0, 1).equals("J")) {

//						inRecord1 = JDTORecordFactory.getInstance().create();
//						inRecord1.setField("MSG_ID",        "YDDMR001");									//출하관리 코일제품입고작업실적전송:전문코드
//						inRecord1.setField("YD_GP",     	sFR_YD_STK_COL_GP.substring(0, 1));				//입고야드구분
//						inRecord1.setField("YD_CRN_SCH_ID", szCrnSchId);									//크레인스케줄ID

//						ydDelegate.sendMsg(inRecord1);

						szMsg="출하관리 코일입고작업실적전송 송신 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}	
				}	 

				//===============================================================================================================
				// TAKE IN
				if(sSND_FLAG.equals("TAKEIN")){ 
					inRecord1 = JDTORecordFactory.getInstance().create();
					inRecord1.setField("MSG_ID",        "YDHRJ001"); 							//열연조업 L3 정정보급완료 실적  전문코드
					inRecord1.setField("STL_NO",     	sSTL_NO);								//재료번호
					inRecord1.setField("YD_EQP_ID",     sTO_YD_STK_COL_GP);						//야드설비id
					inRecord1.setField("YD_STK_BED_NO", sTO_YD_STK_BED_NO);						//야드적치베드번호
					inRecord1.setField("YD_DN_CMPL_DT", sDB_DATE);								//야드권하완료일시
					inRecord1.setField("TREAT_GP"		, "5");									//1:보급완료, 2:보급취소, 5:Take-In
					ydDelegate.sendMsg(inRecord1);
					szMsg="열연조업 L3 TAKE-IN 완료 실적 전송 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

					//품질 열연정정입측보급실적----------------------------------------------
					inRecord1 = JDTORecordFactory.getInstance().create();
					inRecord1.setField("MSG_ID",      	"YDQMJ002"); 							 
					inRecord1.setField("STL_NO",     	sSTL_NO);	  			//재료번호				
					ydDelegate.sendMsg(inRecord1);

					szMsg="품질 L3 열연정정입측보급실적 전송 송신 완료2";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
					//-------------------------------------------------------------------
					
					inRecord2 = JDTORecordFactory.getInstance().create();

					if(( sTO_YD_STK_COL_GP.startsWith("HHKE01")) ||
							( sTO_YD_STK_COL_GP.startsWith("HHKD01")) ){    	
						inRecord2.setField("MSG_ID",        "YDH2L001");							//전문코드
						inRecord2.setField("STL_NO",     	sSTL_NO);								//재료번호
						inRecord2.setField("YD_EQP_ID",     sTO_YD_STK_COL_GP);			//야드설비ID
						inRecord2.setField("YD_STK_BED_NO", sTO_YD_STK_BED_NO);			//야드적치Bed번호 
						ydDelegate.sendMsg(inRecord2);

						szMsg="열연조업 L2  정정 SPM1 TAKE-IN 실적  전송 송신 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);			        	
					}

					if( sTO_YD_STK_COL_GP.startsWith("HGFE01")){		
						inRecord2.setField("MSG_ID",        "YDH2L011");							//전문코드
						inRecord2.setField("STL_NO",     	sSTL_NO);								//재료번호
						inRecord2.setField("YD_EQP_ID",     sTO_YD_STK_COL_GP);			//야드설비ID
						inRecord2.setField("YD_STK_BED_NO", sTO_YD_STK_BED_NO);			//야드적치Bed번호 
						ydDelegate.sendMsg(inRecord2);

						szMsg="열연조업 L2  정정 HFL TAKE-IN 실적  전송 송신 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				        	
					}

					if(( sTO_YD_STK_COL_GP.startsWith("HEDE01"))||
							( sTO_YD_STK_COL_GP.startsWith("HEDD01"))){		
						inRecord2.setField("MSG_ID",        "YDH2L021");							//전문코드
						inRecord2.setField("STL_NO",     	sSTL_NO);								//재료번호
						inRecord2.setField("YD_EQP_ID",     sTO_YD_STK_COL_GP);			//야드설비ID
						inRecord2.setField("YD_STK_BED_NO", sTO_YD_STK_BED_NO);			//야드적치Bed번호 
						ydDelegate.sendMsg(inRecord2);

						szMsg="열연조업 L2  정정 SPM2 TAKE-IN 실적  전송 송신 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				        	
					}	
//C증설	

					if(( sTO_YD_STK_COL_GP.startsWith("HCKE03")) ||
							( sTO_YD_STK_COL_GP.startsWith("HCKD03")) ){    	
						inRecord2.setField("MSG_ID",        "YDH2L031");							//전문코드
						inRecord2.setField("STL_NO",     	sSTL_NO);								//재료번호
						inRecord2.setField("YD_EQP_ID",     sTO_YD_STK_COL_GP);			//야드설비ID
						inRecord2.setField("YD_STK_BED_NO", sTO_YD_STK_BED_NO);			//야드적치Bed번호 
						ydDelegate.sendMsg(inRecord2);

						szMsg="열연조업 L2  정정 SPM3 TAKE-IN 실적  전송 송신 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);			        	
					}
					
					if(( sTO_YD_STK_COL_GP.startsWith("HBKE04")) ||
							( sTO_YD_STK_COL_GP.startsWith("HBKD04")) ){    	
						inRecord2.setField("MSG_ID",        "YDH2L041");							//전문코드
						inRecord2.setField("STL_NO",     	sSTL_NO);								//재료번호
						inRecord2.setField("YD_EQP_ID",     sTO_YD_STK_COL_GP);			//야드설비ID
						inRecord2.setField("YD_STK_BED_NO", sTO_YD_STK_BED_NO);			//야드적치Bed번호 
						ydDelegate.sendMsg(inRecord2);

						szMsg="열연조업 L2  정정 SPM4 TAKE-IN 실적  전송 송신 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);			        	
					}

					if(( sTO_YD_STK_COL_GP.startsWith("HAKE05")) ||
							( sTO_YD_STK_COL_GP.startsWith("HAKD05")) ){    	
						inRecord2.setField("MSG_ID",        "YDH2L071");							//전문코드
						inRecord2.setField("STL_NO",     	sSTL_NO);								//재료번호
						inRecord2.setField("YD_EQP_ID",     sTO_YD_STK_COL_GP);			//야드설비ID
						inRecord2.setField("YD_STK_BED_NO", sTO_YD_STK_BED_NO);			//야드적치Bed번호 
						ydDelegate.sendMsg(inRecord2);

						szMsg="열연조업 L2  정정 SPM5 TAKE-IN 실적  전송 송신 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);			        	
					}
					
					if( sTO_YD_STK_COL_GP.startsWith("HCFE04")){		
						inRecord2.setField("MSG_ID",        "YDH2L051");							//전문코드
						inRecord2.setField("STL_NO",     	sSTL_NO);								//재료번호
						inRecord2.setField("YD_EQP_ID",     sTO_YD_STK_COL_GP);			//야드설비ID
						inRecord2.setField("YD_STK_BED_NO", sTO_YD_STK_BED_NO);			//야드적치Bed번호 
						ydDelegate.sendMsg(inRecord2);

						szMsg="열연조업 L2  정정 HFL4 TAKE-IN 실적  전송 송신 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				        	
					}

					if( sTO_YD_STK_COL_GP.startsWith("HBFE05")){		
						inRecord2.setField("MSG_ID",        "YDH2L061");							//전문코드
						inRecord2.setField("STL_NO",     	sSTL_NO);								//재료번호
						inRecord2.setField("YD_EQP_ID",     sTO_YD_STK_COL_GP);			//야드설비ID
						inRecord2.setField("YD_STK_BED_NO", sTO_YD_STK_BED_NO);			//야드적치Bed번호 
						ydDelegate.sendMsg(inRecord2);

						szMsg="열연조업 L2  정정 HFL5 TAKE-IN 실적  전송 송신 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				        	
					}
					
					
				}	 
				
				//===============================================================================================================
				// TAKE OUT
				if(sSND_FLAG.equals("TAKEOUT")){ 
					//입측 //TAKEOUT
					inRecord1 = JDTORecordFactory.getInstance().create();
					inRecord1.setField("MSG_ID"       , "YDHRJ002");			//전문코드
					inRecord1.setField("STL_NO"       , sSTL_NO);				//재료번호
					inRecord1.setField("TREAT_GP"     , "4");					//TAKEOUT
					inRecord1.setField("YD_UP_CMPL_DT", sDB_DATE);				//크레인스케줄ID
					ydDelegate.sendMsg(inRecord1);
					szMsg = "열연조업 L3 정정추출완료실적 전문 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

					// 조업 정정 L2 전문
					inRecord2 = JDTORecordFactory.getInstance().create();
					if( (sFR_YD_STK_COL_GP.startsWith("HHKE01") && sYD_SCH_CD.equals("HHKE03LM")) || (sFR_YD_STK_COL_GP.startsWith("HHKD01") && sYD_SCH_CD.equals("HHKD03LM")) ){
						inRecord2.setField("MSG_ID"       , "YDH2L004");			// SPM1 전문코드
						inRecord2.setField("YD_EQP_ID"    , sFR_YD_STK_COL_GP);		// 야드설비ID
						inRecord2.setField("YD_STK_BED_NO", sFR_YD_STK_BED_NO);		// 야드적치Bed번호 
						inRecord2.setField("STL_NO"       , sSTL_NO);				// 재료번호
						ydDelegate.sendMsg(inRecord2);
						szMsg = "C열연 SPM1 정정입측TAKE-OUT실적 전문(YDH2L004) 송신 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
					}
					if( sFR_YD_STK_COL_GP.startsWith("HGFE01") && sYD_SCH_CD.equals("HGFE03LM") ){
						inRecord2.setField("MSG_ID"       , "YDH2L014");			// HFL 전문코드
						inRecord2.setField("YD_EQP_ID"    , sFR_YD_STK_COL_GP);		// 야드설비ID
						inRecord2.setField("YD_STK_BED_NO", sFR_YD_STK_BED_NO);		// 야드적치Bed번호 
						inRecord2.setField("STL_NO"       , sSTL_NO);				// 재료번호
						ydDelegate.sendMsg(inRecord2);
						szMsg = "C열연 HFL 정정입측TAKE-OUT실적 전문(YDH2L014) 송신 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
					}	
					if( (sFR_YD_STK_COL_GP.startsWith("HEDE01") && sYD_SCH_CD.equals("HEDE03LM")) ||  (sFR_YD_STK_COL_GP.startsWith("HEDD01") && sYD_SCH_CD.equals("HEDD03LM")) ){
						inRecord2.setField("MSG_ID"       , "YDH2L024");			// SPM2 전문코드
						inRecord2.setField("YD_EQP_ID"    , sFR_YD_STK_COL_GP);		// 야드설비ID
						inRecord2.setField("YD_STK_BED_NO", sFR_YD_STK_BED_NO);		// 야드적치Bed번호 
						inRecord2.setField("STL_NO"       , sSTL_NO);				// 재료번호
						ydDelegate.sendMsg(inRecord2);
						szMsg = "C열연 SPM2 정정입측TAKE-OUT실적 전문(YDH2L024) 송신 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
					}
//C증설				
					if( (sFR_YD_STK_COL_GP.startsWith("HCKE03") && sYD_SCH_CD.equals("HCKE03LM")) ||  (sFR_YD_STK_COL_GP.startsWith("HCKD03") && sYD_SCH_CD.equals("HCKD03LM"))){
						inRecord2.setField("MSG_ID"       , "YDH2L034");			// SPM3 전문코드
						inRecord2.setField("YD_EQP_ID"    , sFR_YD_STK_COL_GP);		// 야드설비ID
						inRecord2.setField("YD_STK_BED_NO", sFR_YD_STK_BED_NO);		// 야드적치Bed번호 
						inRecord2.setField("STL_NO"       , sSTL_NO);				// 재료번호
						ydDelegate.sendMsg(inRecord2);
						szMsg = "C열연 SPM3 정정입측TAKE-OUT실적 전문(YDH2L034) 송신 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
					}
				
					if( (sFR_YD_STK_COL_GP.startsWith("HBKE04") && sYD_SCH_CD.equals("HBKE03LM")) ||  (sFR_YD_STK_COL_GP.startsWith("HBKD04") && sYD_SCH_CD.equals("HBKD03LM")) ){
						inRecord2.setField("MSG_ID"       , "YDH2L044");			// SPM4 전문코드
						inRecord2.setField("YD_EQP_ID"    , sFR_YD_STK_COL_GP);		// 야드설비ID
						inRecord2.setField("YD_STK_BED_NO", sFR_YD_STK_BED_NO);		// 야드적치Bed번호 
						inRecord2.setField("STL_NO"       , sSTL_NO);				// 재료번호
						ydDelegate.sendMsg(inRecord2);
						szMsg = "C열연 SPM4 정정입측TAKE-OUT실적 전문(YDH2L044) 송신 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
					}

					if( (sFR_YD_STK_COL_GP.startsWith("HAKE05") && sYD_SCH_CD.equals("HAKE03LM")) ||  (sFR_YD_STK_COL_GP.startsWith("HAKD05") && sYD_SCH_CD.equals("HAKD03LM")) ){
						inRecord2.setField("MSG_ID"       , "YDH2L074");			// SPM5 전문코드
						inRecord2.setField("YD_EQP_ID"    , sFR_YD_STK_COL_GP);		// 야드설비ID
						inRecord2.setField("YD_STK_BED_NO", sFR_YD_STK_BED_NO);		// 야드적치Bed번호 
						inRecord2.setField("STL_NO"       , sSTL_NO);				// 재료번호
						ydDelegate.sendMsg(inRecord2);
						szMsg = "C열연 SPM5 정정입측TAKE-OUT실적 전문(YDH2L074) 송신 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
					}
				
					if( sFR_YD_STK_COL_GP.startsWith("HCFE04") && sYD_SCH_CD.equals("HCFE03LM") ){
						inRecord2.setField("MSG_ID"       , "YDH2L054");			// HFL4 전문코드
						inRecord2.setField("YD_EQP_ID"    , sFR_YD_STK_COL_GP);		// 야드설비ID
						inRecord2.setField("YD_STK_BED_NO", sFR_YD_STK_BED_NO);		// 야드적치Bed번호 
						inRecord2.setField("STL_NO"       , sSTL_NO);				// 재료번호
						ydDelegate.sendMsg(inRecord2);
						szMsg = "C열연 HFL4 정정입측TAKE-OUT실적 전문(YDH2L054) 송신 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
					}	

					if( sFR_YD_STK_COL_GP.startsWith("HBFE05") && sYD_SCH_CD.equals("HBFE03LM") ){
						inRecord2.setField("MSG_ID"       , "YDH2L064");			// HFL5 전문코드
						inRecord2.setField("YD_EQP_ID"    , sFR_YD_STK_COL_GP);		// 야드설비ID
						inRecord2.setField("YD_STK_BED_NO", sFR_YD_STK_BED_NO);		// 야드적치Bed번호 
						inRecord2.setField("STL_NO"       , sSTL_NO);				// 재료번호
						ydDelegate.sendMsg(inRecord2);
						szMsg = "C열연 HFL5 정정입측TAKE-OUT실적 전문(YDH2L064) 송신 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
					}	
					
				
				
				
				
				}
				
				//===============================================================================================================
				//컨베어
				if(sSND_FLAG.equals("H2LINE")){ 
					inRecord1 = JDTORecordFactory.getInstance().create();
					inRecord1.setField("MSG_ID"       	, "YDH1L001");						//전문코드YD_EQP_ID
					inRecord1.setField("YD_EQP_ID"    	, sFR_YD_STK_COL_GP);				//재료번호
					inRecord1.setField("STL_NO"       	, sSTL_NO);							//재료번호
					inRecord1.setField("YD_STK_BED_NO"	, sFR_YD_STK_BED_NO);				//재료번호
					ydDelegate.sendMsg(inRecord1);
					szMsg = "열연 압연 L2 전문 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				}

				//===============================================================================================================
				// 차량상차개시 SANGSTA
				if(sSND_FLAG.equals("SANGSTA")){  	//권하지시위치로 적치열을 조회하여 차량정지위치 정보를 가져온다.
					inRecord1 = JDTORecordFactory.getInstance().create();
					inRecord1.setField("YD_STK_COL_GP", sFR_YD_STK_COL_GP);
					rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
					/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcol_PIDEV*/
					intRtnVal = ydStkColDao.getYdStkcol(inRecord1, rsResult2, 0);
					if( intRtnVal <= 0 ) {
						gdRes.setMessage("차량정지위치[" + sFR_YD_STK_COL_GP + "] 정보가 존재하지 않습니다.");	
						m_ctx.setRollbackOnly();
						return gdRes;
					}else{
						//조회된 차량정지위치에서 운송장비코드를 가져온다.
						rsResult2.first();
						outRecord0 = rsResult2.getRecord();
						//운송장비코드
						String szTRN_EQP_CD		= ydDaoUtils.paraRecChkNull(outRecord0, "TRN_EQP_CD");
						String szYD_CAR_USE_GP 	= ydDaoUtils.paraRecChkNull(outRecord0, "YD_CAR_USE_GP");
						String szCAR_NO 		= ydDaoUtils.paraRecChkNull(outRecord0, "CAR_NO");
						String szCARD_NO 		= ydDaoUtils.paraRecChkNull(outRecord0, "CARD_NO");
						//String szARR_WLOC_CD	= ydDaoUtils.paraRecChkNull(outRecord0, "WLOC_CD"); // 착지개소코드 
						szMsg = "차량정지위치[" + sFR_YD_STK_COL_GP + "] 정보가 존재하므로 차량사용구분["+szYD_CAR_USE_GP+"], 운송장비코드[" + szTRN_EQP_CD + "], 차량번호["+szCAR_NO+"], 카드번호["+szCARD_NO+"]로 차량스케줄 조회" ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						//운송장비코드로 차량스케줄조회
						inRecord1 = JDTORecordFactory.getInstance().create();
						inRecord1.setField("YD_CAR_USE_GP"	, szYD_CAR_USE_GP);
						inRecord1.setField("TRN_EQP_CD"		, szTRN_EQP_CD);
						inRecord1.setField("STL_NO"			, sSTL_NO); 
						
						rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
						/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschByStlNoCarID*/
						intRtnVal = ydCarSchDao.getYdCarsch(inRecord1, rsResult2, 433);
						if( intRtnVal <= 0 ) {
							szMsg = "차량사용구분["+szYD_CAR_USE_GP+"], 운송장비코드[" + szTRN_EQP_CD + "], 차량번호["+szCAR_NO+"], 카드번호["+szCARD_NO+"]에 해당하는 차량스케줄이 존재하지 않습니다." ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							gdRes.setMessage("차량스케쥴 정보가 존재하지 않습니다.");
							m_ctx.setRollbackOnly();
							return gdRes;
						}else{
							//차량진행상태를 파악하여 상차검수이거나 상차도착일 때만 상차개시 전문을 송신한다.
							rsResult2.absolute(1);
							outRecord0 = rsResult2.getRecord();
							String szYD_CAR_SCH_ID 		= ydDaoUtils.paraRecChkNull(outRecord0, "YD_CAR_SCH_ID"); //차량스케줄ID
							String szYD_CAR_PROG_STAT 	= ydDaoUtils.paraRecChkNull(outRecord0, "YD_CAR_PROG_STAT"); //야드차량진행상태
							szYD_CAR_USE_GP 			= ydDaoUtils.paraRecChkNull(outRecord0, "YD_CAR_USE_GP");	//차량사용구분
							szYD_EQP_WRK_STAT			= ydDaoUtils.paraRecChkNull(outRecord0, "YD_EQP_WRK_STAT");
							
							szMsg = "차량스케줄 조회 후 차량스케줄ID[" + szYD_CAR_SCH_ID + "], 야드차량진행상태[" + szYD_CAR_PROG_STAT + "], 차량사용구분[" + szYD_CAR_USE_GP + "]" ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

							//상차검수이거나 상차도착일 때 상차개시 전문 송신
							//if( szYD_CAR_PROG_STAT.equals("3") || szYD_CAR_PROG_STAT.equals("2") ) {
							//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
							//야드구분에 따른 개소코드 반환
							//String szARR_WLOC_CD = YdCommonUtils.getWlocCd(sYD_AIM_YD_GP); //WLOC_CD
							//차량스케줄 업데이트 - 상차개시
							inRecord1 = JDTORecordFactory.getInstance().create();
							inRecord1.setField("YD_CAR_SCH_ID", 	szYD_CAR_SCH_ID);		//차량스케줄ID
							inRecord1.setField("YD_CARLD_ST_DT", 	sDB_DATE);				//상차개시일시
							inRecord1.setField("MODIFIER", 			sYD_USER_ID);			//수정자
							inRecord1.setField("MOD_DDTT", 			sDB_DATE);				//수정일
							
							if(szYD_EQP_WRK_STAT.equals("L")){
								inRecord1.setField("YD_CAR_PROG_STAT", 	"D");					//차량진행상태
							}else{
								inRecord1.setField("YD_CAR_PROG_STAT", 	"4");					//차량진행상태
							}
							//TODO .....
							// 차량 스케줄이 공차 출발과 상차지 출발이 각각 만들어 지는지 한개의 스케줄로 구성 되는지???
							//if( szYD_CAR_USE_GP.equals("L") ) {					//구내운송
							//	inRecord1.setField("ARR_WLOC_CD", szARR_WLOC_CD); //착지개소코드
							//}
							intRtnVal = ydCarSchDao.updYdCarsch(inRecord1, 0);

							if(intRtnVal <= 0) {
								szMsg="차량스케줄에 상차개시일시 등록시 Error!! Code : " + intRtnVal;
								gdRes.setMessage(szMsg);		
								m_ctx.setRollbackOnly();
								return gdRes;	
							}

							inRecord1 = JDTORecordFactory.getInstance().create();
							if( szYD_CAR_USE_GP.equals("L") ) {					//구내운송
								inRecord1.setField("MSG_ID",        "YDTSJ007");//상차작업개시 송신 YDTSJ007 (구내운송 상차개시)
								inRecord1.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
								//------------------------------------------------------------------------
								szMsg="상차작업개시 송신 YDTSJ007 (구내운송 상차개시) 송신 시작";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

								ydDelegate.sendMsg(inRecord1);

								szMsg="상차작업개시 송신 YDTSJ007 (구내운송 상차개시) 송신 완료";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

								//------------------------------------------------------------------------
								szMsg="상차작업개시 송신 YDDMR019 (출하 상차개시) 송신 시작";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

								inRecord1.setField("MSG_ID",        "YDDMR019");//상차작업개시 송신 YDDMR019 (출하 상차개시)
								ydDelegate.sendMsg(inRecord1);

								szMsg="상차작업개시 송신 YDDMR019 (출하 상차개시) 송신 완료";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);


							}else if( szYD_CAR_USE_GP.equals("G") ){			//출하차량

								inRecord1.setField("MSG_ID",        "YDDMR007");
								inRecord1.setField("YD_SCH_CD",     sYD_SCH_CD);
								inRecord1.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
								inRecord1.setField("YD_GP",         ydDaoUtils.paraRecChkNull(outRecord0, "YD_GP"));

								ydDelegate.sendMsg(inRecord1);

								szMsg="상차작업개시 송신 YDDMR007 (코일출하상차개시) 송신 시작";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							}


							szMsg="상차작업개시 송신 완료";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						}
						//}
					}
				}
				
				//===============================================================================================================
				// 차량상차완료 SANGEND
				
				/*
				 * ******* 제품일 경우 ********  
				 * 상차	┏  구내운송	┏ 상차개시TC전송	YDTSJ007  
				 *      ┃              	┗ 상차완료TC전송	YDTSJ008
				 *    	┗  출하		┏ 상차개시TC전송	YDDMR019	제품일때만 전송 U
				 *  				┣ 상차완료TC전송 	YDDMR021	제품일때만 전송 U
				 *  				┣ 	 			YDDMR007	
				 *  				┗ 				YDDMR015	
				 *  
				 * 하차	┏ 구내운송	┏ 하차개시TC전송 	YDTSJ009
				 * 		┃			┗ 하차완료TC존송	YDTSJ010
				 * 		┗ 출하		┏ 하차개시TC전송	YDDMR019	제품일때만 전송 D
				 * 					┗ 하차완료TC전송	YDDMR021	제품일때만 전송 D
				 *  
				 * */
				if(sSND_FLAG.equals("SANGEND")){  	
					inRecord1 = JDTORecordFactory.getInstance().create();
					inRecord1.setField("YD_STK_COL_GP", sFR_YD_STK_COL_GP);
					rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
					/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcol_PIDEV*/
					intRtnVal = ydStkColDao.getYdStkcol(inRecord1, rsResult2, 0);
					if( intRtnVal <= 0 ) {
						gdRes.setMessage("차량정지위치[" + sTO_YD_STK_COL_GP + "] 정보가 존재하지 않습니다.");
						m_ctx.setRollbackOnly();
						return gdRes;
					}else{
						//조회된 차량정지위치에서 운송장비코드를 가져온다.
						rsResult2.first();
						outRecord0 = rsResult2.getRecord();
						//운송장비코드
						String szTRN_EQP_CD		= ydDaoUtils.paraRecChkNull(outRecord0, "TRN_EQP_CD");
						String szYD_CAR_USE_GP 	= ydDaoUtils.paraRecChkNull(outRecord0, "YD_CAR_USE_GP");
						String szCAR_NO 		= ydDaoUtils.paraRecChkNull(outRecord0, "CAR_NO");
						String szCARD_NO 		= ydDaoUtils.paraRecChkNull(outRecord0, "CARD_NO");
						String szYD_PNT_CD		= ydDaoUtils.paraRecChkNull(outRecord0, "YD_PNT_CD");
						//String szARR_WLOC_CD	= ydDaoUtils.paraRecChkNull(outRecord0, "WLOC_CD"); // 착지개소코드 
						
						szMsg = "차량정지위치[" + sTO_YD_STK_COL_GP + "] 정보가 존재하므로 차량사용구분["+szYD_CAR_USE_GP+"], 운송장비코드[" + szTRN_EQP_CD + "], 차량번호["+szCAR_NO+"], 카드번호["+szCARD_NO+"]로 차량스케줄 조회" ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						//운송장비코드로 차량스케줄조회
						inRecord1 = JDTORecordFactory.getInstance().create();
						inRecord1.setField("YD_CAR_USE_GP"	, szYD_CAR_USE_GP);
						inRecord1.setField("TRN_EQP_CD"		, szTRN_EQP_CD);
						inRecord1.setField("STL_NO"			, sSTL_NO);
						 
						rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
						/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschByStlNoCarID*/
						intRtnVal = ydCarSchDao.getYdCarsch(inRecord1, rsResult2, 433);
						if( intRtnVal <= 0 ) {
							szMsg = "차량사용구분["+szYD_CAR_USE_GP+"], 운송장비코드[" + szTRN_EQP_CD + "], 차량번호["+szCAR_NO+"], 카드번호["+szCARD_NO+"]에 해당하는 차량스케줄이 존재하지 않습니다." ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							gdRes.setMessage(szMsg);
							m_ctx.setRollbackOnly();
							return gdRes;
						}else{
							
							rsResult2.absolute(1);
							outRecord0 = rsResult2.getRecord();
							String szYD_CAR_SCH_ID 		= ydDaoUtils.paraRecChkNull(outRecord0, "YD_CAR_SCH_ID"); //차량스케줄ID
							String szYD_CAR_PROG_STAT 	= ydDaoUtils.paraRecChkNull(outRecord0, "YD_CAR_PROG_STAT"); //야드차량진행상태
							szYD_CAR_USE_GP 			= ydDaoUtils.paraRecChkNull(outRecord0, "YD_CAR_USE_GP");	//차량사용구분
							szYD_EQP_WRK_STAT 			= ydDaoUtils.paraRecChkNull(outRecord0, "YD_EQP_WRK_STAT");
							// 차량 상태 
//							1 상차출발, 2 상차도착, 3 상차검수, 4 상차개시, 5 상차완료   
//							A 하차출발, B 하차도착, C 하차검수, D 하차개시, E 하차완료 


							szMsg = "차량스케줄 조회 후 차량스케줄ID[" + szYD_CAR_SCH_ID + "], 야드차량진행상태[" + szYD_CAR_PROG_STAT + "], 차량사용구분[" + szYD_CAR_USE_GP + "]" ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

							//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
							//야드구분에 따른 개소코드 반환
							//String szARR_WLOC_CD = YdCommonUtils.getWlocCd(sYD_AIM_YD_GP);


							//차량스케줄 업데이트 - 상차완료
							inRecord1 = JDTORecordFactory.getInstance().create();
							inRecord1.setField("YD_CAR_SCH_ID", 	szYD_CAR_SCH_ID);		//차량스케줄ID
							inRecord1.setField("YD_PNT_CD3", 		szYD_PNT_CD);			//차량스케줄ID							
							inRecord1.setField("YD_CARLD_CMPL_DT", 	sDB_DATE);				//상차완료일시
							inRecord1.setField("MODIFIER", 			sYD_USER_ID);			//수정자
							inRecord1.setField("MOD_DDTT", 			sDB_DATE);				//수정일
							
							
							if(szYD_EQP_WRK_STAT.equals("L")){
								inRecord1.setField("YD_CAR_PROG_STAT", "E");									//하차완료상태 
							}else{
								inRecord1.setField("YD_CAR_PROG_STAT", "5");									//상차완료상태
							}
							
							
							intRtnVal = ydCarSchDao.updYdCarsch(inRecord1, 0);

							if(intRtnVal <= 0) {
								szMsg="차량스케줄에 상차 완료 등록시 Error!! Code : " + intRtnVal;
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								gdRes.setMessage(szMsg);		
								m_ctx.setRollbackOnly();
								return gdRes;	
							}

							if( szYD_CAR_USE_GP.equals("L") ) {					
								//구내운송
								inRecord1.setField("MSG_ID",        "YDTSJ008");// 상차완료TC전송	YDTSJ008 
								//inRecord1.setField("ARR_WLOC_CD", szARR_WLOC_CD);//착지개소코드
								inRecord1.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);

								//------------------------------------------------------------------
								szMsg="상차완료 송신 YDTSJ008 (구내운송 상차완료) 송신 시작";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

								ydDelegate.sendMsg(inRecord1); //구내운송 - 상차완료TC전송	YDTSJ008
								
								szMsg="상차완료 송신 YDTSJ008 (구내운송 상차완료) 송신완료";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								//------------------------------------------------------------------
								
								
								//------------------------------------------------------------------
								szMsg="상차완료 송신 YDDMR021 (출하 상차완료) 송신 시작";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								
								inRecord1.setField("MSG_ID",        "YDDMR021"); // 출하 - 상차완료 YDDMR021
								ydDelegate.sendMsg(inRecord1); //출하 - 상차완료 YDDMR021
								
								szMsg="상차완료 송신 YDTSJ008 (출하 상차완료) 송신완료";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								//------------------------------------------------------------------
								
								
							}else if( szYD_CAR_USE_GP.equals("G") ){	//출하차량

								szMsg="상차완료 송신 YDDMR015 (코일출하상차완료) 송신 시작";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								
								inRecord1.setField("MSG_ID",        "YDDMR015");
								inRecord1.setField("YD_SCH_CD",     sYD_SCH_CD);
								inRecord1.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
								inRecord1.setField("YD_GP",         ydDaoUtils.paraRecChkNull(outRecord0, "YD_GP"));

								ydDelegate.sendMsg(inRecord1);
								
								szMsg="상차완료 송신 YDDMR015 (코일출하상차완료) 송신 완료";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							}
							

							szMsg="상차완료 송신 완료";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						}
					}

				}

				//===============================================================================================================
				// 차량하차개시
				if(sSND_FLAG.equals("HASTA")){  	
					//권하지시위치로 적치열을 조회하여 차량정지위치 정보를 가져온다.
					inRecord1 = JDTORecordFactory.getInstance().create();
					inRecord1.setField("YD_STK_COL_GP", sFR_YD_STK_COL_GP);
					rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
					/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcol_PIDEV*/
					intRtnVal = ydStkColDao.getYdStkcol(inRecord1, rsResult2, 0);
					if( intRtnVal <= 0 ) {
						gdRes.setMessage("차량정지위치[" + sFR_YD_STK_COL_GP + "] 정보가 존재하지 않습니다.");		
					}else{
						//조회된 차량정지위치에서 운송장비코드를 가져온다.
						rsResult2.first();
						outRecord0 = rsResult2.getRecord();
						//운송장비코드
						String szTRN_EQP_CD		= ydDaoUtils.paraRecChkNull(outRecord0, "TRN_EQP_CD");
						String szYD_CAR_USE_GP 	= ydDaoUtils.paraRecChkNull(outRecord0, "YD_CAR_USE_GP");
						String szCAR_NO 		= ydDaoUtils.paraRecChkNull(outRecord0, "CAR_NO");
						String szCARD_NO 		= ydDaoUtils.paraRecChkNull(outRecord0, "CARD_NO");
						szMsg = "차량정지위치[" + sTO_YD_STK_COL_GP + "] 정보가 존재하므로 차량사용구분["+szYD_CAR_USE_GP+"], 운송장비코드[" + szTRN_EQP_CD + "], 차량번호["+szCAR_NO+"], 카드번호["+szCARD_NO+"]로 차량스케줄 조회" ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

						//운송장비코드로 차량스케줄조회
						inRecord1 = JDTORecordFactory.getInstance().create();
						inRecord1.setField("YD_CAR_USE_GP"	, szYD_CAR_USE_GP);
						inRecord1.setField("TRN_EQP_CD"		, szTRN_EQP_CD);
						inRecord1.setField("STL_NO"			, sSTL_NO); 
						rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
						/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschByStlNoCarID*/
						intRtnVal = ydCarSchDao.getYdCarsch(inRecord1, rsResult2, 433);
						if( intRtnVal <= 0 ) {
							szMsg = "차량사용구분["+szYD_CAR_USE_GP+"], 운송장비코드[" + szTRN_EQP_CD + "], 차량번호["+szCAR_NO+"], 카드번호["+szCARD_NO+"]에 해당하는 차량스케줄이 존재하지 않습니다." ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

						}else{
							//차량진행상태를 파악하여 상차검수이거나 상차도착일 때만 상차개시 전문을 송신한다.
							rsResult2.absolute(1);
							outRecord0 = rsResult2.getRecord();
							String szYD_CAR_SCH_ID 		= ydDaoUtils.paraRecChkNull(outRecord0, "YD_CAR_SCH_ID"); //차량스케줄ID
							String szYD_CAR_PROG_STAT 	= ydDaoUtils.paraRecChkNull(outRecord0, "YD_CAR_PROG_STAT"); //야드차량진행상태
							//String szYD_SCH_CD 			= ydDaoUtils.paraRecChkNull(outRecord0, "YD_SCH_CD"); //야드차량진행상태
							szYD_CAR_USE_GP 			= ydDaoUtils.paraRecChkNull(outRecord0, "YD_CAR_USE_GP");	//차량사용구분

							szMsg = "차량스케줄 조회 후 차량스케줄ID[" + szYD_CAR_SCH_ID + "], 야드차량진행상태[" + szYD_CAR_PROG_STAT + "], 차량사용구분[" + szYD_CAR_USE_GP + "]" ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

							//하차검수이거나 하차도착일 때 하차개시 전문 송신.???
							inRecord1 = JDTORecordFactory.getInstance().create();
							inRecord1.setField("YD_CAR_SCH_ID", 	szYD_CAR_SCH_ID);	//차량스케줄ID
							inRecord1.setField("YD_CAR_PROG_STAT", 	"D");				//차량진행상태
							inRecord1.setField("YD_CARUD_ST_DT", 	sDB_DATE);			//하차개시일시
							inRecord1.setField("MODIFIER", 			sYD_USER_ID);		//수정자
							inRecord1.setField("MOD_DDTT", 			sDB_DATE);			//수정일

							intRtnVal = ydCarSchDao.updYdCarsch(inRecord1, 0);

							if(intRtnVal <= 0) {
								szMsg="[저장위치수정]차량스케줄에 하차 개시 등록시 Error!! Code : " + intRtnVal;
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								gdRes.setMessage(szMsg);		
								m_ctx.setRollbackOnly();
								return gdRes;	
							}

							if( szYD_CAR_USE_GP.equals("L") ) { // 구내운송일때 
								inRecord1 = JDTORecordFactory.getInstance().create();
								// TC_CODE설정 
								inRecord1.setField("MSG_ID",        "YDTSJ009");
								inRecord1.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);

								//----------------------------------------------------------------------
								szMsg="[저장위치수정]하차작업개시[YDTSJ009] 송신 시작";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

								ydDelegate.sendMsg(inRecord1);

								szMsg="[저장위치수정]하차작업개시[YDTSJ009] 송신 완료";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

								//----------------------------------------------------------------------
								szMsg="[저장위치수정]하차작업개시[YDDMR019] 송신 시작";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

								// TC_CODE설정 
								inRecord1.setField("MSG_ID",        "YDDMR019");
								ydDelegate.sendMsg(inRecord1);

								szMsg="[저장위치수정]하차작업개시[YDDMR019] 송신 완료";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

							}
						}
					}
				}
				
				
				//===============================================================================================================
				// 차량하차완료
				if(sSND_FLAG.equals("HAEND")){  	
					//권하지시위치로 적치열을 조회하여 차량정지위치 정보를 가져온다.
					inRecord1 = JDTORecordFactory.getInstance().create();
					inRecord1.setField("YD_STK_COL_GP", sFR_YD_STK_COL_GP);
					rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
					/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcol_PIDEV*/
					intRtnVal = ydStkColDao.getYdStkcol(inRecord1, rsResult2, 0);
					if( intRtnVal <= 0 ) {
						gdRes.setMessage("차량정지위치[" + sFR_YD_STK_COL_GP + "] 정보가 존재하지 않습니다.");		
					}else{
						//조회된 차량정지위치에서 운송장비코드를 가져온다.
						rsResult2.first();
						outRecord0 = rsResult2.getRecord();
						//운송장비코드
						String szTRN_EQP_CD		= ydDaoUtils.paraRecChkNull(outRecord0, "TRN_EQP_CD");
						String szYD_CAR_USE_GP 	= ydDaoUtils.paraRecChkNull(outRecord0, "YD_CAR_USE_GP");
						String szCAR_NO 		= ydDaoUtils.paraRecChkNull(outRecord0, "CAR_NO");
						String szCARD_NO 		= ydDaoUtils.paraRecChkNull(outRecord0, "CARD_NO");
						szMsg = "차량정지위치[" + sTO_YD_STK_COL_GP + "] 정보가 존재하므로 차량사용구분["+szYD_CAR_USE_GP+"], 운송장비코드[" + szTRN_EQP_CD + "], 차량번호["+szCAR_NO+"], 카드번호["+szCARD_NO+"]로 차량스케줄 조회" ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						//운송장비코드로 차량스케줄조회
						inRecord1 = JDTORecordFactory.getInstance().create();
						inRecord1.setField("YD_CAR_USE_GP"	, szYD_CAR_USE_GP);
						inRecord1.setField("TRN_EQP_CD"		, szTRN_EQP_CD);
						inRecord1.setField("CAR_NO"			, szCAR_NO);
						inRecord1.setField("STL_NO"			, sSTL_NO);
						rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
						/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschByStlNoCarID*/
						intRtnVal = ydCarSchDao.getYdCarsch(inRecord1, rsResult2, 433);
						if( intRtnVal <= 0 ) {
							szMsg = "차량사용구분["+szYD_CAR_USE_GP+"], 운송장비코드[" + szTRN_EQP_CD + "], 차량번호["+szCAR_NO+"], 카드번호["+szCARD_NO+"]에 해당하는 차량스케줄이 존재하지 않습니다." ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							
						}else{
							//차량진행상태를 파악하여 상차검수이거나 상차도착일 때만 상차개시 전문을 송신한다.
							rsResult2.absolute(1);
							outRecord0 = rsResult2.getRecord();
							String szYD_CAR_SCH_ID 		= ydDaoUtils.paraRecChkNull(outRecord0, "YD_CAR_SCH_ID"); //차량스케줄ID
							String szYD_CAR_PROG_STAT 	= ydDaoUtils.paraRecChkNull(outRecord0, "YD_CAR_PROG_STAT"); //야드차량진행상태
							String szYD_SCH_CD 			= ydDaoUtils.paraRecChkNull(outRecord0, "YD_SCH_CD"); //야드차량진행상태
							szYD_CAR_USE_GP 			= ydDaoUtils.paraRecChkNull(outRecord0, "YD_CAR_USE_GP");	//차량사용구분
							
							szMsg = "차량스케줄 조회 후 차량스케줄ID[" + szYD_CAR_SCH_ID + "], 야드차량진행상태[" + szYD_CAR_PROG_STAT + "], 차량사용구분[" + szYD_CAR_USE_GP + "]" ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							
							/**
							 * 1)차량 스케줄 하차완료 처리 - 스케줄 삭제 하지 않음 
							 */
							inRecord1 = JDTORecordFactory.getInstance().create();
							inRecord1.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);		//차량스케줄ID
							inRecord1.setField("YD_CAR_PROG_STAT", "E");				//차량진행상태 : 하차완료[E]
							inRecord1.setField("YD_CARUD_CMPL_DT", sDB_DATE);			//하차완료일시
							inRecord1.setField("MODIFIER", sYD_USER_ID);				//수정자
							inRecord1.setField("MOD_DDTT", sDB_DATE);					//수정일
							intRtnVal = ydCarSchDao.updYdCarsch(inRecord1, 0);
							if(intRtnVal <= 0) {
								szMsg="[저장위치수정 - 하차완료]차량스케줄에 하차 개시 등록시 Error!! Code : " + intRtnVal;
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								gdRes.setMessage(szMsg);		
								m_ctx.setRollbackOnly();
								return gdRes;	
							}

							/**
							 * 2)차량 이송재료를 조회 후 삭제처리
							 */
							JDTORecord recTemp = null;
							JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
							inRecord1 = JDTORecordFactory.getInstance().create();
							inRecord1.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
							
							/* com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.getYdCarftmvmtlBySchId */		
							intRtnVal = ydCarFtmvMtlDao.getYdCarftmvmtl(inRecord1, outRecSet, 4);
							if(intRtnVal <= 0) {
								szMsg="[저장위치수정] - 하차완료 - 차량스케줄["+szYD_CAR_SCH_ID+"]에 이송재료 가 존재하지 않습니다.";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
								gdRes.setMessage(szMsg);
								m_ctx.setRollbackOnly();
								return gdRes;
							}else{
								recTemp = JDTORecordFactory.getInstance().create();
								for(int i = 1 ; i <= outRecSet.size(); i++ ) {
									outRecSet.absolute(i);
									inRecord1 = outRecSet.getRecord();
									
									recTemp.setField("YD_CAR_SCH_ID", 		szYD_CAR_SCH_ID);
									recTemp.setField("STL_NO", 				ydDaoUtils.paraRecChkNull(inRecord1, "STL_NO"));
									recTemp.setField("DEL_YN", 				"Y");
									
									/* com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.updYdCarftmvmtl */
									intRtnVal = ydCarFtmvMtlDao.updYdCarftmvmtl(recTemp, 0);
									
									if( intRtnVal == 0 ) {
										szMsg="[저장위치수정] - 하차완료 - 차량스케줄["+szYD_CAR_SCH_ID+"]의 이송재료삭제 시 이송재료가 존재하지 않습니다.";
										ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
										gdRes.setMessage(szMsg);
										m_ctx.setRollbackOnly();
										return gdRes;
									}else if( intRtnVal < 0 ) {
										szMsg="[저장위치수정] - 하차완료 - 차량스케줄["+szYD_CAR_SCH_ID+"]의 이송재료삭제 시 오류발생 - 반환값 : " + intRtnVal;
										ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
										gdRes.setMessage(szMsg);
										m_ctx.setRollbackOnly();
										return gdRes;
									}else{
										szMsg="[저장위치수정] - 하차완료 - 차량스케줄["+szYD_CAR_SCH_ID+"]의 이송재료삭제 성공";
										ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
									}
								}
							}
							
							
							//하차완료 전문 송신
							if( szYD_CAR_USE_GP.equals("L") ) { // 구내운송일때
								//하차작업개시 송신 YDTSJ010
								inRecord1 = JDTORecordFactory.getInstance().create();
								
								inRecord1.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
								
								//--------------------------------------------------------------------
								szMsg="[저장위치수정]하차작업완료[YDTSJ009] 송신 시작";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								
								inRecord1.setField("MSG_ID",        "YDTSJ010");
								ydDelegate.sendMsg(inRecord1);

								szMsg="[저장위치수정]하차작업완료[YDTSJ009] 송신 완료";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								
								//--------------------------------------------------------------------
								szMsg="[저장위치수정]하차작업완료[YDDBR021] 송신 시작";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								
								inRecord1.setField("MSG_ID",        "YDDBR021");
								ydDelegate.sendMsg(inRecord1);
								
								szMsg="[저장위치수정]하차작업완료[YDDBR021] 송신 완료";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

							}
						}
					}
				}


				

				//===============================================================================================================
				// 입고(확인 필요)
				if(sSND_FLAG.equals("STOCKED")){ 
					//코일입고작업실적
					JDTORecord tcRecordDM = null;
					tcRecordDM = JDTORecordFactory.getInstance().create(); 
					tcRecordDM.setField("GOODS_NO",sSTL_NO);
					tcRecordDM.setField("YD_GP",sFR_YD_STK_COL_GP.substring(0, 1));
					tcRecordDM.setField("STORE_LOC",sTO_YD_STK_COL_GP+sUPD_STK_LYR_NO.substring(2, 3)+sTO_YD_STK_BED_NO);

					//인터페이스 전문 호출
					ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
					ejbConn.trx("getYDDMR001",new Class[]{JDTORecord.class}, new Object[]{tcRecordDM}); 

					szMsg="내부IF호출=== 일관제철 코일입고작업실적.===";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
			}
			
			
			//저장위치 변경이력 등록
			JDTORecord recCrnStock = null;
			YdWrkHistDao ydWrkHistDao = new YdWrkHistDao();			
			 
			 recCrnStock = JDTORecordFactory.getInstance().create(); 
			 recCrnStock.setField("STL_NO", sSTL_NO);
			 recCrnStock.setField("MODIFIER", sYD_USER_ID);
			 recCrnStock.setField("YD_UP_WR_LOC", sFR_YD_STK_COL_GP+sFR_YD_STK_BED_NO);
			 recCrnStock.setField("YD_UP_WR_LAYER", "00"+sFR_STK_LYR_NO); //FROM 단
			 recCrnStock.setField("YD_DN_WR_LOC", sTO_YD_STK_COL_GP+sTO_YD_STK_BED_NO);
			 recCrnStock.setField("YD_DN_WR_LAYER", sUPD_STK_LYR_NO); //TO 단
			 recCrnStock.setField("YD_GP", sFR_YD_STK_COL_GP.substring(0, 1));
			 recCrnStock.setField("SPEC_ABBSYM", sSND_FLAG);
			//------------------------------------------------------------------------------------
			
			// 이력테이블에 INSERT
			intRtnVal = ydWrkHistDao.insYdCoilWrkHist(recCrnStock);
			
			if(intRtnVal<=0) {
				szMsg = "재료번호(" + sSTL_NO + ")에 대한 INSERT가 실패하였습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				gdRes.setMessage(szMsg);		
				m_ctx.setRollbackOnly();
				return gdRes;	
			}
			
			
			
			//L2저장품재원 정보 송신
			//======================================================
			// 저장품제원 : 코일야드L2로 송신(YDY5L002)
			//======================================================
			JDTORecord recResult = null;
			recResult = JDTORecordFactory.getInstance().create();
			recResult.setField("MSG_ID"         , "YDY5L002");
			recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
			recResult.setField("STL_NO"         , sSTL_NO);
			recResult.setField("YD_STK_COL_GP"  , "");
			recResult.setField("YD_STK_BED_NO"  , "");
			
			ydDelegate.sendMsg(recResult);

			szMsg = "코일야드L2로 응답전문 [YDY5L002] 전송완료";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			//제품장인 경우 출하에 저장위치변경 통보
			if(sFR_YD_STK_COL_GP.substring(0, 1).equals("J")){
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				//코일제품이적작업실적
				JDTORecord tcRecordDM = null;
				tcRecordDM = JDTORecordFactory.getInstance().create(); 
				tcRecordDM.setField("GOODS_NO",sSTL_NO);
//				tcRecordDM.setField("BEFO_STORE_LOC",sFR_YD_STK_COL_GP+sFR_YD_STK_BED_NO+"00"+sFR_STK_LYR_NO);
//				tcRecordDM.setField("TO_STORE_LOC",sTO_YD_STK_COL_GP+sTO_YD_STK_BED_NO+sUPD_STK_LYR_NO);

				tcRecordDM.setField("BEFO_STORE_LOC",ydUtils.ParsingStkColGpBedLyr(sFR_YD_STK_COL_GP, sFR_YD_STK_BED_NO, "00"+sFR_STK_LYR_NO));
				tcRecordDM.setField("TO_STORE_LOC",ydUtils.ParsingStkColGpBedLyr(sTO_YD_STK_COL_GP, sTO_YD_STK_BED_NO, sUPD_STK_LYR_NO));
				
				//인터페이스 전문 호출
				ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
				ejbConn.trx("getYDDMR004",new Class[]{JDTORecord.class},
						new Object[]{tcRecordDM}); 

				szMsg = "코일야드에서 출하L3로 응답전문 [YDDMR004] 전송완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			}
            
			
			gdRes.setMessage("저장위치 변경 성공");
		
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		return gdRes;
	}  //end of updStrlocModMgt
	
	
	/**
	 * 크레인작업관리 > InterLock 스케쥴 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @작성자 : 박지열
	 * @작성일 : 2010.05.19
	 */
	public GridData interLock(GridData inDto){
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		String szMethodName = "interLock";	
		try{
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			gdRes = (GridData)ejbConn.trx("interLock", new Class[] { GridData.class }, new Object[] { inDto });
			
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		
		return gdRes;
	} 

	/**
	 * 크레인작업관리 > InterLock 스케쥴 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @작성자 : 박지열
	 * @작성일 : 2010.05.19
	 */
	public GridData getInterLock(GridData inDto) throws DAOException {
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecord outRecord = JDTORecordFactory.getInstance().create();		
		String szMethodName = "getInterLock";	
		try{
//			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
//			gdRes = (GridData)ejbConn.trx("interLock", new Class[] { GridData.class }, new Object[] { inDto });

			gdRes = OperateGridData.cloneResponseGridData(inDto);		
			gdRes = CmUtil.copyGDParam(inDto, gdRes);
			
			
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);		
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);			
			outRecord = (JDTORecord)ejbConn.trx("getInterLock", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			String sRTN_CD	= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0"); 
			String sRTN_MSG	= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
	
			ydUtils.putLog(szSessionName, "", "sRTN_CD.--> " +sRTN_CD, YdConstant.DEBUG);
	
			if ("0".equals(sRTN_CD)) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
				return gdRes;	
			}	
			gdRes.setMessage("InterLock 기동 성공");
			
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		
		return gdRes;
	} 
	
	
	/**
	 * 야드관리 > 코일소재야드 > 설비/차량관리 > 야드보급순서  목록조회
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.03
	 */
	public GridData getSupplyInOrderList(GridData inDto){
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		String szMethodName = "getSupplyInOrderList";	
		try{
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			gdRes = (GridData)ejbConn.trx("getSupplyInOrderList", new Class[] { GridData.class }, new Object[] { inDto });
			
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		
		return gdRes;
	} 
	
	/**
	 *  권하위치 변경 (크레인작업관리 화면)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData updToPosFixCoil(GridData gdReq) throws JDTOException {
		
		String szLogMsg           = "";
		String szMethodName       = "";
		boolean bool              = false;
		Boolean boolWrapper       = null;

		GridData gdRes            = null;
		EJBConnector ejbConn      = null;
		JDTORecord outRecord = JDTORecordFactory.getInstance().create();		
		
		try{
			
			szLogMsg = "JSP-FACADE [권하위치 변경 (크레인작업관리 화면)] 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			
			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			outRecord = (JDTORecord)ejbConn.trx("updToPosFixCoil", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			
			String sRTN_CD	= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0"); 
			String sRTN_MSG	= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
	
			ydUtils.putLog(szSessionName, "", "sRTN_CD.--> " +sRTN_CD, YdConstant.DEBUG);
	
			if ("0".equals(sRTN_CD)) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
				return gdRes;	
			}	
			gdRes.setMessage("권하위치 변경 성공");
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		szLogMsg = "JSP-FACADE [권하위치 변경 (크레인작업관리 화면)] 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}  //end of updToPosFix
	

	/**
	 *  권하위치 변경 ( )
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public void updToPosFixCoilProc(JDTORecord inRecord) throws JDTOException  {
		
		String szMsg="";
		String szLogMsg           = "";
		String szMethodName="updToPosFixCoilProc";
		JDTORecord 	  outRecord 	= JDTORecordFactory.getInstance().create();
		EJBConnector  ejbConn   = null;
		GridData gdRes            = null;
		
		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){
			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
		}
		
		try {
			szLogMsg = "JSP-FACADE [권하위치 변경 (크레인작업관리 화면)] 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			// 권하위치 변경 Proc
			ejbConn = new EJBConnector("default", this);
			outRecord = (JDTORecord) ejbConn.trx("CoilJspSeEJB", "updToPosFixCoilProc", inRecord);

			String sRTN_CD	= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0"); 
			String sRTN_MSG	= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
	
			ydUtils.putLog(szSessionName, "", "sRTN_CD.--> " +sRTN_CD, YdConstant.DEBUG);
	
			gdRes.setMessage("권하위치 변경 성공");

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch

		
		szMsg="Y0크레인권하실적 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
	 } // end of updToPosFixCoilProc()
	
	/**
	 * 이송재료LIST
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData getYdTransMtlList(GridData inDto) throws DAOException {
		/*
		 * 업무기준 : 이송지시테이블, 저장품, 적치단, 코일공통, 준비스케줄 테이블을 조인해서 이송대상재를 조회
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
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getYdTransMtlList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return gdRes;
	} //end of getYdTransMtlList

	/**
	 * 목표행선/목표야드/목표동 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	public GridData updCoilYdTransMtlList(GridData inDto) throws JDTOException {
		 
		String szMethodName="updCoilYdTransMtlList";
		String szRcvMsg = "";
		String szLogMsg = "";
		String szOperationName = "목표행선/목표야드/목표동 수정";
		
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecord outRecord = JDTORecordFactory.getInstance().create();		

			
		try{
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);

			gdRes = OperateGridData.cloneResponseGridData(inDto);
			
			outRecord = (JDTORecord)ejbConn.trx("updCoilYdTransMtlList",
						new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			
			String sRTN_CD	= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0"); 
			String sRTN_MSG	= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
			ydUtils.putLog(szSessionName, "", "sRTN_CD.--> " +sRTN_CD, YdConstant.DEBUG);
	
			if ("0".equals(sRTN_CD)) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
				return gdRes;	
			}	

			gdRes.setStatus("true");
			gdRes.setMessage(szRcvMsg);
			
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("updSlabTotYdTransMtlList");
		gdRes.setMessage("정상적으로 처리되었습니다.");
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return gdRes;
	}
	
	/**
	 * 이송대상재를 준비스케줄에 등록 - 크레인설비 등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData insYdPrepSchNCrn(GridData inDto) throws JDTOException {
		/*
		 * 업무기준 :
		 * 			 테이블을 조인해서 이송대상재를 조회해서 준비스케줄에 등록
		 * 수정자 : 임춘수
		 * 수정일 : 2009.09.28
		 */
		String szMethodName = "insYdPrepSchNCrn";
		String szRtnMsg = null;
		String szLogMsg = "";
		String szOperationName = "크레인설비 등록";
		JDTORecord outRecord = JDTORecordFactory.getInstance().create();		

		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		try{
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			
			gdRes = OperateGridData.cloneResponseGridData(inDto);

			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", this);
			outRecord = (JDTORecord)ejbConn.trx("CoilJspSeEJB", "insYdPrepSchNCrn", inRecord);
			
			String sRTN_CD	= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0"); 
			String sRTN_MSG	= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
			ydUtils.putLog(szSessionName, "", "sRTN_CD.--> " +sRTN_CD, YdConstant.DEBUG);
	
			if ("0".equals(sRTN_CD)) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
				return gdRes;	
			}	

		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("정상적으로 처리되었습니다.");
		
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
		
		JDTORecord outRecord = JDTORecordFactory.getInstance().create();		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		try{
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			
			JDTORecord[] inRecord = ydComUtil.genGridToJDTORecordAll(inDto);
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);

			outRecord = (JDTORecord)ejbConn.trx("insYdPrepSchNCrnByManual",
						new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			String sRTN_CD	= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0"); 
			String sRTN_MSG	= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
			ydUtils.putLog(szSessionName, "", "sRTN_CD.--> " +sRTN_CD, YdConstant.DEBUG);
	
			if ("0".equals(sRTN_CD)) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
				return gdRes;	
			}	
			

		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("정상적으로 처리되었습니다.");
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return gdRes;
	} //end of insYdPrepSchNCrnByManual	

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
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getYdPrepSchListByCrn", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		return gdRes;
	}	
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
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getYdPrepSchMtlList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return gdRes;
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
		String szRtnMsg		= null;
		String szLogMsg = "";
		String szOperationName = "준비재료삭제";
		JDTORecord outRecord = JDTORecordFactory.getInstance().create();		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		try{
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			JDTORecord[] inRecord = ydComUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);

			outRecord = (JDTORecord)ejbConn.trx("delYdPrepMtl",
						new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			String sRTN_CD	= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0"); 
			String sRTN_MSG	= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
			ydUtils.putLog(szSessionName, "", "sRTN_CD.--> " +sRTN_CD, YdConstant.DEBUG);
	
			if ("0".equals(sRTN_CD)) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
				return gdRes;	
			}	
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		
		gdRes.setStatus("true");
		gdRes.setMessage("정상적으로 처리되었습니다.");
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
		JDTORecord outRecord = JDTORecordFactory.getInstance().create();		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		try{
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			
			JDTORecord[] inRecord = ydComUtil.genGridToJDTORecordAll(inDto);
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);

			outRecord = (JDTORecord)ejbConn.trx("uptYdPrepSch",
						new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			String sRTN_CD	= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0"); 
			String sRTN_MSG	= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
			ydUtils.putLog(szSessionName, "", "sRTN_CD.--> " +sRTN_CD, YdConstant.DEBUG);
	
			if ("0".equals(sRTN_CD)) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
				return gdRes;	
			}	
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("정상적으로 처리되었습니다.");
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return gdRes;
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
		String szOperationName = "준비스케줄과 준비재료삭제";
		
		String szRtnMsg		= null;
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		try{
			
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord[] inRecord = ydComUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);

			szRtnMsg = (String)ejbConn.trx("delYdPrepSch",
						new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage(szRtnMsg);
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return gdRes;
	} //end of delYdPrepSch
		
	/**
	 * 차량 상차완료 처리 (구내운송:코일에서 사용함)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData getCoilYdCarUpEndPp(GridData inDto) throws JDTOException {
		String szMethodName = "getYdPrepSchMtlList";
		String szLogMsg = "";
		String szOperationName = "차량 상차완료 처리";
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdCarUpEndPp", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return gdRes;
	} //end of getcoilYdCarUpEndPp
	
	/**
	 * 차량 상차완료 처리 (구내운송:코일에서 사용함)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData updCoilYdCarUpEndPp(GridData inDto) throws JDTOException {
		String szMethodName = "updCoilYdCarUpEndPp";
		String szRtnMsg		= null;
		String szLogMsg = "";
		String szOperationName = "차량 상차완료 처리";
		JDTORecord inRecord1 = JDTORecordFactory.getInstance().create();		
		JDTORecord inRecord9 = JDTORecordFactory.getInstance().create();		
		JDTORecord outRecord = JDTORecordFactory.getInstance().create();		
		JDTORecord outRecord2 = JDTORecordFactory.getInstance().create();		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;

		
		String sSTL_NO				 = "";
		String szUserId  			 = "";
		String sTO_YD_STK_COL_GP	 = "";
		String sTO_YD_STK_BED_NO	 = "";
		String szYD_CAR_SCH_ID		 = "";
		String sYD_GP                = "";
		String szMsg             	 = "";
		String sRTN_CD1	         	 = "";
		String sRTN_MSG1         	 = "";
		String sRTN_CD2	         	 = "";
		String sRTN_MSG2         	 = "";
		String sRTN_CD3	         	 = "";
		String sRTN_MSG3         	 = "";

		
		YdDelegate ydDelegate = new YdDelegate();	
		
		try{
			
			szMsg = "[JSP Facade] 차량작업관리차량 상차완료 처리 시작  ==>";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			
			gdRes = OperateGridData.cloneResponseGridData(inDto);

			JDTORecord [] inRecord =  ydComUtil.genGridToJDTORecordAll(inDto);
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			outRecord2 = (JDTORecord)ejbConn.trx("updCarWrMgt", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

			sRTN_CD1	= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
			sRTN_MSG1	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
			if ("0".equals(sRTN_CD1)) {
				gdRes.setMessage(sRTN_MSG1);		
//				m_ctx.setRollbackOnly();
//				return gdRes;	
			}	
			
		
			szMsg = "[JSP Facade] 차량작업관리 차량 상차완료 처리  시작  ==>";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);

			outRecord = (JDTORecord)ejbConn.trx("updCoilYdCarUpEndPp",
						new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			
			sRTN_CD2		= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0"); 
			sRTN_MSG2		= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
			szYD_CAR_SCH_ID = StringHelper.evl(outRecord.getFieldString("YD_CAR_SCH_ID"), "");
			ydUtils.putLog(szSessionName, "", "sRTN_CD.--> " +sRTN_CD2, YdConstant.DEBUG);
	
			if ("0".equals(sRTN_CD2)) {
				gdRes.setMessage(sRTN_MSG2);		
				m_ctx.setRollbackOnly();
				return gdRes;	
			}	
			sYD_GP	= ydDaoUtils.paraRecChkNull(inRecord[0], "YD_GP");
			
			inRecord1 = JDTORecordFactory.getInstance().create();		
			
			//구내운송
			inRecord1.setField("MSG_ID",        "YDTSJ008");// 상차완료TC전송	YDTSJ008 
			inRecord1.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);

			//------------------------------------------------------------------
			szMsg="상차완료 송신 YDTSJ008 (구내운송 상차완료) 송신 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			ydDelegate.sendMsg(inRecord1); //구내운송 - 상차완료TC전송	YDTSJ008
			
			szMsg="상차완료 송신 YDTSJ008 (구내운송 상차완료) 송신완료";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//------------------------------------------------------------------
				
			if( sYD_GP.equals("J") ) {					
				
				//------------------------------------------------------------------
				szMsg="상차완료 송신 YDDMR021 (출하 상차완료) 송신 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				inRecord1.setField("MSG_ID",        "YDDMR021"); // 출하 - 상차완료 YDDMR021
				ydDelegate.sendMsg(inRecord1); //출하 - 상차완료 YDDMR021
				
				szMsg="상차완료 송신 YDTSJ008 (출하 상차완료) 송신완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//------------------------------------------------------------------

			}	
			//이송일 경우 YM 등록 여부 CHECK 함	
			ejbConn = new EJBConnector("default", "CoilCraneUdHdSeEJB", this);
			ejbConn.trx("Y5insYmStockCoil", new Class[] { JDTORecord.class }, new Object[] { inRecord1 });
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("정상적으로 처리되었습니다.");
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return gdRes;
	} //end of uptYdPrepSch
	
	/**
	 * 저장위치변경관리 (저장위치수정)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updStrlocMod1(GridData gdReq) throws DAOException {

		YdCrnSchDao ydCrnschDao  			= new YdCrnSchDao();
		YdWrkbookDao ydWrkbookDao 			= new YdWrkbookDao();
		YdWrkbookMtlDao ydWrkbookMtlDao 	= new YdWrkbookMtlDao();
		YdCarSchDao ydCarSchDao 			= new YdCarSchDao();
		YdStkColDao ydStkColDao 			= new YdStkColDao();
		YdDelegate ydDelegate 				= new YdDelegate();
		YdTcarFtmvMtlDao ydTcarftmvmtlDao 	= new YdTcarFtmvMtlDao();
		YdCarFtmvMtlDao ydCarFtmvMtlDao 	= new YdCarFtmvMtlDao();

		YdTcarSchDao ydTcarSchDao = new YdTcarSchDao();
		
		String szMsg         				= "";
		String szMethodName  				= "updStrlocMod1";
		GridData gdRes       				= null;
		EJBConnector ejbConn 				= null;
		String szLogMsg      				= "";
		JDTORecord inRecord1  				= JDTORecordFactory.getInstance().create();			
		JDTORecord inRecord2  				= JDTORecordFactory.getInstance().create();			
		JDTORecord inRecord3  				= JDTORecordFactory.getInstance().create();			
		JDTORecord inRecord9  				= JDTORecordFactory.getInstance().create();			
		JDTORecord outRecord0 				= JDTORecordFactory.getInstance().create();			
		JDTORecord outRecord1 				= JDTORecordFactory.getInstance().create();			
		JDTORecord outRecord2 				= JDTORecordFactory.getInstance().create();			
		JDTORecord outRecord3 				= JDTORecordFactory.getInstance().create();			
		JDTORecordSet rsResult1				= null;
		JDTORecordSet rsResult2				= null;
		JDTORecordSet rsResult3				= null;

		Calendar today						= Calendar.getInstance();
		SimpleDateFormat sdf 				= new SimpleDateFormat("yyyyMMddHHmmss");
		String sRTN_CD				= "";
		String sRTN_MSG				= "";
		
		String sFR_YD_STK_COL_GP	= "";
		String sFR_YD_STK_BED_NO	= "";
		String sFR_YD_EQP_GP		= "";
		
		String sTO_YD_STK_COL_GP	= "";
		String sTO_YD_STK_BED_NO	= "";
		String sTO_YD_EQP_GP		= "";
		String sSTL_NO				= "";
		String sYD_WBOOK_ID         = "";
		String sYD_SCH_CD           = "";
		String sDB_DATE             = sdf.format(today.getTime());
		String sYD_USER_ID          = "";
		String sYD_AIM_YD_GP 		= "";
		String sSND_FLAG			= ""; 
		String sTO_YD_STK_LYR_NO	= "";
		String sFR_YD_STK_LYR_NO	= "";
		String sSND_COMM_FLAG		= "";
//		String sUpdToPos			= "";
//		String sUpdSend				= "";
		
		int    intRtnVal 			= 0;
		int    intRtnVal3 			= 0;
			
		try{
	
			szLogMsg = "JSP-FACADE  [ "+ szMethodName +" ]시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);
			
			gdRes = OperateGridData.cloneResponseGridData(gdReq);		
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);

			
			sFR_YD_STK_COL_GP	= gdReq.getHeader("YD_STK_COL_GP").getValue(0); // 야드적치열
			sFR_YD_STK_BED_NO	= gdReq.getHeader("YD_STK_BED_NO").getValue(0); // 번지
			sFR_YD_STK_LYR_NO	= gdReq.getHeader("CURR_STK_LYR_NO").getValue(0); // 적치단
			sFR_YD_EQP_GP  		= sFR_YD_STK_COL_GP.substring(2, 4);			// fr야드설비구분     
			
			sTO_YD_STK_COL_GP	= gdReq.getHeader("UPD_STK_POS_2").getValue(0); // 야드적치열
			sTO_YD_STK_BED_NO	= gdReq.getHeader("UPD_STK_BED_NO").getValue(0);// 
			sTO_YD_STK_LYR_NO	= gdReq.getHeader("UPD_STK_LYR_NO").getValue(0); // 적치단
			sTO_YD_EQP_GP  		= sTO_YD_STK_COL_GP.substring(2, 4);			// to야드설비구분     
			
			sSTL_NO				= gdReq.getHeader("STL_NO").getValue(0);
			sYD_USER_ID			= gdReq.getHeader("YD_USER_ID").getValue(0);					// 사용자ID
			sYD_AIM_YD_GP		= gdReq.getHeader("YD_AIM_YD_GP").getValue(0);					// 사용자ID
			sSND_FLAG			= gdReq.getParam("SND_FLAG");		// 송신 여부
//			sUpdToPos			= gdReq.getParam("UPD_TOPOS"); // 저장위치 수정 여부
//			sUpdSend			= gdReq.getParam("UPD_SEND"); // 전문송신 여부
			
			ydUtils.putLog(szSessionName, szMethodName, "sFR_YD_STK_COL_GP:"	+ sFR_YD_STK_COL_GP, YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, "sFR_YD_STK_BED_NO:"	+ sFR_YD_STK_BED_NO, YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, "sFR_YD_EQP_GP:"		+ sFR_YD_EQP_GP, YdConstant.INFO);
			
			ydUtils.putLog(szSessionName, szMethodName, "sTO_YD_STK_COL_GP:"	+ sTO_YD_STK_COL_GP, YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, "sTO_YD_STK_BED_NO:"	+ sTO_YD_STK_BED_NO, YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, "sTO_YD_STK_LYR_NO:"	+ sTO_YD_STK_LYR_NO, YdConstant.INFO);
			
			ydUtils.putLog(szSessionName, szMethodName, "sTO_YD_EQP_GP:"		+ sTO_YD_EQP_GP, YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, "sSTL_NO:"				+ sSTL_NO, YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, "sSND_FLAG:"			+ sSND_FLAG, YdConstant.INFO);
				
			
//			///////////////////////////////////////////////////////////////////////
//			//저장위치 수정일 경우 	
//			///////////////////////////////////////////////////////////////////////
//			//======================================================================================
//			//YARD 정보 수정:UPDATE TB_YD_STKLYR, TB_YD_STOCK
//			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);			
//			outRecord1 		= (JDTORecord)ejbConn.trx("updYDStrlocModMgt", new Class[] { JDTORecord[].class,GridData.class }, new Object[] { inRecord,gdReq });
//			sRTN_CD			= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
//			sRTN_MSG		= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
//			sSND_COMM_FLAG	= StringHelper.evl(outRecord1.getFieldString("SND_COMM_FLAG"), "");
//		
//			if ("0".equals(sRTN_CD) && !sSND_FLAG.equals("STOCKED")) {
//				gdRes.setMessage(sRTN_MSG);		
//				m_ctx.setRollbackOnly();
//				return gdRes;	
//			}	

			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);			
			outRecord1 		= (JDTORecord)ejbConn.trx("updYDStrlocModMgtLoc", new Class[] { JDTORecord[].class,GridData.class }, new Object[] { inRecord,gdReq });
			sRTN_CD			= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
			sRTN_MSG		= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
			sSND_COMM_FLAG	= StringHelper.evl(outRecord1.getFieldString("SND_COMM_FLAG"), "");
		
			if ("0".equals(sRTN_CD) && !sSND_FLAG.equals("STOCKED")) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
				return gdRes;	
			}	
			
			rsResult1 = JDTORecordFactory.getInstance().createRecordSet("");
			inRecord1 = JDTORecordFactory.getInstance().create();			
			inRecord1.setField("STL_NO",      sSTL_NO);
			/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getCrnSchByStlNo*/
			intRtnVal = ydCrnschDao.getYdCrnsch(inRecord1, rsResult1, 52);
			if( intRtnVal  > 0 ) {
				gdRes.setMessage("스케쥴이 편성되어 있습니다. 취소처리후 작업하세요");		
				m_ctx.setRollbackOnly();
				return gdRes;	
			}	

			rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
			/* com.inisteel.cim.yd.dao.ydwrkbookmtldao.YdWrkbookmtlDao.getYdWrkbookmtlSTLNO */
			intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(inRecord1, rsResult2, 2);
			if( intRtnVal  > 0 ) {
				rsResult2.first();
				outRecord0 = rsResult2.getRecord();
				sYD_WBOOK_ID 	= ydDaoUtils.paraRecChkNull(outRecord0, "YD_WBOOK_ID");
				sYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(outRecord0, "YD_SCH_CD");
				sDB_DATE 		= ydDaoUtils.paraRecChkNull(outRecord0, "DB_DATE");

			}	

			if(!(sYD_WBOOK_ID.equals(""))){
//					작업 예약 삭제
				inRecord1 = JDTORecordFactory.getInstance().create();
				inRecord1.setField("YD_WBOOK_ID", sYD_WBOOK_ID);
				inRecord1.setField("DEL_YN", "Y");
				inRecord1.setField("STL_NO", sSTL_NO);
				inRecord1.setField("MODIFIER", sYD_USER_ID);
				intRtnVal =  ydWrkbookDao.updYdWrkbook(inRecord1, 0);
				if( intRtnVal < 1 ) {
					szMsg = "[JSP Session : "+szMethodName+"] 작업예약 삭제 시 오류발생 : 반환값 - " + intRtnVal;
					gdRes.setMessage("작업예약재료 삭제 시 오류발생");		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}

				inRecord1 = JDTORecordFactory.getInstance().create();
				inRecord1.setField("YD_WBOOK_ID", sYD_WBOOK_ID);
				inRecord1.setField("DEL_YN", "Y");
				inRecord1.setField("STL_NO", sSTL_NO);
				inRecord1.setField("MODIFIER", sYD_USER_ID);
				intRtnVal = ydWrkbookMtlDao.updYdWrkbookmtl(inRecord1, 0);
				if( intRtnVal < 1 ) {
					szMsg = "[JSP Session : "+szMethodName+"] 작업예약재료 삭제 시 오류발생 : 반환값 - " + intRtnVal;
					gdRes.setMessage("작업예약재료 삭제 시 오류발생");		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}
			}
			
			
			//===============================================================================================================
			// 대차에서 내릴 경우 
			if( sFR_YD_EQP_GP.equals("TC") ){
				if( (sTO_YD_EQP_GP.substring(0,1).equals("0")) ||(sTO_YD_EQP_GP.substring(0,1).equals("1"))){

					inRecord1 = JDTORecordFactory.getInstance().create();
					inRecord1.setField("STL_NO", sSTL_NO);
					rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
					/*com.inisteel.cim.yd.dao.ydtcarftmvmtldao.YdTcarftmvmtlDao.getYdStockTcarSTL_NO*/
					intRtnVal = ydTcarftmvmtlDao.getYdTcarftmvmtl(inRecord1, rsResult2, 302);
					if( intRtnVal <= 0 ) {
						gdRes.setMessage("대차위에  정보가 존재하지 않습니다.");		
					}else{
						//조회된 차량정지위치에서 운송장비코드를 가져온다.
						rsResult2.first();
						outRecord0 = rsResult2.getRecord();
						
						String szYD_TCAR_SCH_ID	= ydDaoUtils.paraRecChkNull(outRecord0, "YD_TCAR_SCH_ID");//운송장비코드

						inRecord1 = JDTORecordFactory.getInstance().create();
						inRecord1.setField("YD_TCAR_SCH_ID"	, szYD_TCAR_SCH_ID);						
						inRecord1.setField("STL_NO"			, sSTL_NO);							
						intRtnVal = ydTcarftmvmtlDao.delYdTcarftmvmtl(inRecord1);
						if(intRtnVal < 1) {
							szMsg="[대차 정보 삭제시 이상 발생 Error!! Code : " + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							gdRes.setMessage(szMsg);		
							m_ctx.setRollbackOnly();
							return gdRes;	
						}
					}
				}
			}
			//===============================================================================================================
			// 대차에 올릴경우  
			if( sTO_YD_EQP_GP.equals("TC") ){
				if( (sFR_YD_EQP_GP.substring(0,1).equals("0")) ||(sFR_YD_EQP_GP.substring(0,1).equals("1"))){

					rsResult2 = JDTORecordFactory.getInstance().createRecordSet("Temp");
					inRecord1 = JDTORecordFactory.getInstance().create();
				
					inRecord1.setField("YD_EQP_ID", "JXTC"+sTO_YD_STK_BED_NO);                   //JXTC01
			    	/*com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.getYdTcarschByYdEqpId*/
			    	intRtnVal = ydTcarSchDao.getYdTcarsch(inRecord1, rsResult2, 4);
					
					if( intRtnVal <= 0 ) {
						gdRes.setMessage("대차스케쥴  정보가 존재하지 않습니다.");		
					}else{
						//조회된 차량정지위치에서 운송장비코드를 가져온다.
						rsResult2.first();
						outRecord0 = rsResult2.getRecord();
						
						String szYD_TCAR_SCH_ID	= ydDaoUtils.paraRecChkNull(outRecord0, "YD_TCAR_SCH_ID");//운송장비코드

						inRecord1 = JDTORecordFactory.getInstance().create();
						inRecord1.setField("YD_TCAR_SCH_ID"	, szYD_TCAR_SCH_ID);						
						inRecord1.setField("STL_NO"			, sSTL_NO);	
						
						inRecord1.setField("REGISTER"		, sYD_USER_ID);
						inRecord1.setField("YD_STK_BED_NO"	, sTO_YD_STK_BED_NO) ;
						inRecord1.setField("YD_STK_LYR_NO"	, "001") ;
						
						intRtnVal = ydTcarftmvmtlDao.insYdTcarftmvmtl(inRecord1);
						if(intRtnVal < 1) {
							szMsg="[대차 재료 정보추가시  이상 발생 Error!! Code : " + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							gdRes.setMessage(szMsg);		
							m_ctx.setRollbackOnly();
							return gdRes;	
						}
					}
				}	
			}
			
			//===============================================================================================================
			// 차량에서 내릴 경우 
			if( sFR_YD_EQP_GP.equals("PT") ){
				if( (sTO_YD_EQP_GP.substring(0,1).equals("0")) ||(sTO_YD_EQP_GP.substring(0,1).equals("1"))){

					inRecord1 = JDTORecordFactory.getInstance().create();
					inRecord1.setField("YD_STK_COL_GP", sTO_YD_STK_COL_GP.substring(0, 6));
					rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
					
					intRtnVal = ydStkColDao.getYdStkcol(inRecord1, rsResult2, 0);
					if( intRtnVal <= 0 ) {
						szMsg="[차량정지위치 정보가 존재하지 않습니다" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						gdRes.setMessage(szMsg);		
						m_ctx.setRollbackOnly();
						return gdRes;	
						
					}else{
						//조회된 차량정지위치에서 운송장비코드를 가져온다.
						rsResult2.first();
						outRecord0 = rsResult2.getRecord();
						
						//운송장비코드
		        		String szTRN_EQP_CD1 	= ydDaoUtils.paraRecChkNull(outRecord0, "TRN_EQP_CD");
		        		String szYD_CAR_USE_GP1	= ydDaoUtils.paraRecChkNull(outRecord0, "YD_CAR_USE_GP");
		        		String szCAR_NO1 		= ydDaoUtils.paraRecChkNull(outRecord0, "CAR_NO");
		        		String szCARD_NO1 		= ydDaoUtils.paraRecChkNull(outRecord0, "CARD_NO");
		        	
		        		//운송장비코드로 차량스케줄조회
		        		inRecord2 = JDTORecordFactory.getInstance().create();
		        		inRecord2.setField("YD_CAR_USE_GP"	, szYD_CAR_USE_GP1);
		        		inRecord2.setField("TRN_EQP_CD"		, szTRN_EQP_CD1);
		        		inRecord2.setField("STL_NO"			, sSTL_NO); 
		        		rsResult3 = JDTORecordFactory.getInstance().createRecordSet("");
		        		/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschByStlNoCarID*/
		    			intRtnVal = ydCarSchDao.getYdCarsch(inRecord2, rsResult3, 433);						
		    			if( intRtnVal <= 0 ) {
		    				szMsg="[운송장비코드 정보가 존재하지 않습니다" + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							gdRes.setMessage(szMsg);		
							m_ctx.setRollbackOnly();
							return gdRes;	
			                
		            	}else{
		            		
		            		rsResult3.absolute(1);
		            		outRecord3 = rsResult3.getRecord();
		            		String szYD_CAR_SCH_ID1 		= ydDaoUtils.paraRecChkNull(outRecord3, "YD_CAR_SCH_ID"); //차량스케줄ID
		            		
		            		inRecord3 = JDTORecordFactory.getInstance().create();
		            		inRecord3.setField("YD_CAR_SCH_ID"	, szYD_CAR_SCH_ID1);
		            		inRecord3.setField("STL_NO"			, sSTL_NO); 
		            		inRecord3.setField("MODIFIER"		, sYD_USER_ID);
		            		inRecord3.setField("DEL_YN"			, "Y");
					    		
	
		                	//차량 작업 진행관리 호출(하차)
		                	intRtnVal3 = ydCarFtmvMtlDao.updYdCarftmvmtl(inRecord3, 0);
		                	if(intRtnVal3 < 1) {
								szMsg="[차량 재료 정보삭제시  이상 발생 Error!! Code : " + intRtnVal;
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								gdRes.setMessage(szMsg);		
								m_ctx.setRollbackOnly();
								return gdRes;	
		                	}
		            	}
					}
				}
			}
			//===============================================================================================================
			// 차량에 올릴경우  
			if( sTO_YD_EQP_GP.equals("PT") ){
				if( (sFR_YD_EQP_GP.substring(0,1).equals("0")) ||(sFR_YD_EQP_GP.substring(0,1).equals("1"))){

					inRecord1 = JDTORecordFactory.getInstance().create();
					inRecord1.setField("YD_STK_COL_GP", sTO_YD_STK_COL_GP.substring(0, 6));
					rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
					
					intRtnVal = ydStkColDao.getYdStkcol(inRecord1, rsResult2, 0);
					if( intRtnVal <= 0 ) {
						szMsg="[차량정지위치 정보가 존재하지 않습니다" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						gdRes.setMessage(szMsg);		
						m_ctx.setRollbackOnly();
						return gdRes;	
						
					}else{
						//조회된 차량정지위치에서 운송장비코드를 가져온다.
						rsResult2.first();
						outRecord0 = rsResult2.getRecord();
						
						//운송장비코드
		        		String szTRN_EQP_CD1 	= ydDaoUtils.paraRecChkNull(outRecord0, "TRN_EQP_CD");
		        		String szYD_CAR_USE_GP1	= ydDaoUtils.paraRecChkNull(outRecord0, "YD_CAR_USE_GP");
		        		String szCAR_NO1 		= ydDaoUtils.paraRecChkNull(outRecord0, "CAR_NO");
		        		String szCARD_NO1 		= ydDaoUtils.paraRecChkNull(outRecord0, "CARD_NO");
		        	
		        		//운송장비코드로 차량스케줄조회
		        		inRecord2 = JDTORecordFactory.getInstance().create();
		        		inRecord2.setField("YD_CAR_USE_GP"	, szYD_CAR_USE_GP1);
		        		inRecord2.setField("TRN_EQP_CD"		, szTRN_EQP_CD1);
		        		inRecord2.setField("STL_NO"			, sSTL_NO); 
		        		rsResult3 = JDTORecordFactory.getInstance().createRecordSet("");
		        		/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschByStlNoCarID*/
		    			intRtnVal = ydCarSchDao.getYdCarsch(inRecord2, rsResult3, 433);						
		    			if( intRtnVal <= 0 ) {
		    				szMsg="[차량SCH 운송장비 정보가 존재하지 않습니다" + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							gdRes.setMessage(szMsg);		
							m_ctx.setRollbackOnly();
							return gdRes;	
			                
		            	}else{
		            		
		            		rsResult3.absolute(1);
		            		outRecord3 = rsResult3.getRecord();
		            		String szYD_CAR_SCH_ID1 		= ydDaoUtils.paraRecChkNull(outRecord3, "YD_CAR_SCH_ID"); //차량스케줄ID
		            		
		            		inRecord3 = JDTORecordFactory.getInstance().create();
		            		inRecord3.setField("YD_TCAR_SCH_ID"	, szYD_CAR_SCH_ID1);						
		            		inRecord3.setField("STL_NO"			, sSTL_NO);	
							
		            		inRecord3.setField("REGISTER"		, sYD_USER_ID);
		            		inRecord3.setField("YD_STK_BED_NO"	, sTO_YD_STK_BED_NO) ;
		            		inRecord3.setField("YD_STK_LYR_NO"	, "001") ;
							
							intRtnVal = ydCarFtmvMtlDao.insYdCarftmvmtl(inRecord3);
							if(intRtnVal < 1) {
								szMsg="[차량 재료 정보추가시  이상 발생 Error!! Code : " + intRtnVal;
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								gdRes.setMessage(szMsg);		
								m_ctx.setRollbackOnly();
								return gdRes;	
							}
		            	}
					}
				}
			}
			if(sSND_COMM_FLAG.equals("Y")){
				//저장위치 변경이력 등록
				JDTORecord recCrnStock = null;
				YdWrkHistDao ydWrkHistDao = new YdWrkHistDao();			
				 
				recCrnStock = JDTORecordFactory.getInstance().create(); 
				recCrnStock.setField("STL_NO"			, sSTL_NO);
				recCrnStock.setField("MODIFIER"			, sYD_USER_ID);
				recCrnStock.setField("YD_UP_WR_LOC"		, sFR_YD_STK_COL_GP+sFR_YD_STK_BED_NO);
				recCrnStock.setField("YD_UP_WR_LAYER"	, "00"+sFR_YD_STK_LYR_NO); //FROM 단
				recCrnStock.setField("YD_DN_WR_LOC"		, sTO_YD_STK_COL_GP+sTO_YD_STK_BED_NO);
				recCrnStock.setField("YD_DN_WR_LAYER"	, sTO_YD_STK_LYR_NO); //TO 단
				recCrnStock.setField("YD_GP"			, sTO_YD_STK_COL_GP.substring(0, 1));
				recCrnStock.setField("SPEC_ABBSYM"		, sSND_FLAG);
				//------------------------------------------------------------------------------------
				
				// 이력테이블에 INSERT
				intRtnVal = ydWrkHistDao.insYdCoilWrkHist(recCrnStock);
				
				if(intRtnVal<=0) {
					szMsg = "재료번호(" + sSTL_NO + ")에 대한 INSERT가 실패하였습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					gdRes.setMessage(szMsg);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}
	
				//코일 공통  정보 수정				
				inRecord9 = JDTORecordFactory.getInstance().create();
				inRecord9.setField("UPD_STK_POS_2"	, sTO_YD_STK_COL_GP);
				inRecord9.setField("UPD_STK_BED_NO"	, sTO_YD_STK_BED_NO);
				inRecord9.setField("UPD_STK_LYR_NO"	, sTO_YD_STK_LYR_NO);
				inRecord9.setField("YD_USER_ID"		, sYD_USER_ID);
				inRecord9.setField("STL_NO"			, sSTL_NO);
				
				ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);			
				outRecord2 	= (JDTORecord)ejbConn.trx("updCOMMStrlocModMgt", new Class[] { JDTORecord.class }, new Object[] { inRecord9 });
				sRTN_CD		= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
				sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
		
				if ("0".equals(sRTN_CD)) {
					gdRes.setMessage(sRTN_MSG);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}	
				
				//L2저장품제원 정보 송신
				//======================================================
				// 저장품제원 : 코일야드L2로 송신(YDY5L002)
				//======================================================
				JDTORecord recResult = null;
				recResult = JDTORecordFactory.getInstance().create();
				recResult.setField("MSG_ID"         , "YDY5L002");
				recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
				recResult.setField("STL_NO"         , sSTL_NO);
				recResult.setField("YD_STK_COL_GP"  , "");
				recResult.setField("YD_STK_BED_NO"  , "");
				
				ydDelegate.sendMsg(recResult);
	
				szMsg = "코일야드L2로 응답전문 [YDY5L002] 전송완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				
				//제품장인 경우 출하에 저장위치변경 통보
				if(sFR_YD_STK_COL_GP.substring(0, 1).equals("J")){
					//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
					//코일제품이적작업실적
					JDTORecord tcRecordDM = null;
					tcRecordDM = JDTORecordFactory.getInstance().create(); 
					tcRecordDM.setField("GOODS_NO"		, sSTL_NO);
//					tcRecordDM.setField("BEFO_STORE_LOC", sFR_YD_STK_COL_GP+sFR_YD_STK_BED_NO+"00"+sFR_YD_STK_LYR_NO);
//					tcRecordDM.setField("TO_STORE_LOC"	, sTO_YD_STK_COL_GP+sTO_YD_STK_BED_NO+sTO_YD_STK_LYR_NO);
	
					tcRecordDM.setField("BEFO_STORE_LOC",ydUtils.ParsingStkColGpBedLyr(sFR_YD_STK_COL_GP, sFR_YD_STK_BED_NO, "00"+sFR_YD_STK_LYR_NO));
					tcRecordDM.setField("TO_STORE_LOC",ydUtils.ParsingStkColGpBedLyr(sTO_YD_STK_COL_GP, sTO_YD_STK_BED_NO, sTO_YD_STK_LYR_NO));
					
					
					//인터페이스 전문 호출
					ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
					ejbConn.trx("getYDDMR004",new Class[]{JDTORecord.class}, new Object[]{tcRecordDM}); 
	
					szMsg = "코일야드에서 출하L3로 응답전문 [YDDMR004] 전송완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				}
			}	
			gdRes.setMessage("저장위치 변경 성공");
		
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		return gdRes;
	}  //end of updStrlocMod1

	/**
	 * 저장위치변경관리 (저장위치수정:소재)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updStrlocMod3(GridData gdReq) throws DAOException {

		YdCrnSchDao ydCrnschDao  			= new YdCrnSchDao();
		YdWrkbookDao ydWrkbookDao 			= new YdWrkbookDao();
		YdWrkbookMtlDao ydWrkbookMtlDao 	= new YdWrkbookMtlDao();
		YdCarSchDao ydCarSchDao 			= new YdCarSchDao();
		YdStkColDao ydStkColDao 			= new YdStkColDao();
		YdDelegate ydDelegate 				= new YdDelegate();
		YdTcarFtmvMtlDao ydTcarftmvmtlDao 	= new YdTcarFtmvMtlDao();
		YdCarFtmvMtlDao ydCarFtmvMtlDao 	= new YdCarFtmvMtlDao();
		JDTORecord recDelPara   	= null;
		YdTcarSchDao ydTcarSchDao = new YdTcarSchDao();
		
		String szMsg         				= "";
		String szMethodName  				= "updStrlocMod3";
		GridData gdRes       				= null;
		EJBConnector ejbConn 				= null;
		String szLogMsg      				= "";
		JDTORecord inRecord1  				= JDTORecordFactory.getInstance().create();			
		JDTORecord inRecord2  				= JDTORecordFactory.getInstance().create();			
		JDTORecord inRecord3  				= JDTORecordFactory.getInstance().create();			
		JDTORecord inRecord4  				= JDTORecordFactory.getInstance().create();			
		JDTORecord inRecord8  				= JDTORecordFactory.getInstance().create();			
		JDTORecord inRecord9  				= JDTORecordFactory.getInstance().create();			
		JDTORecord outRecord0 				= JDTORecordFactory.getInstance().create();			
		JDTORecord outRecord1 				= JDTORecordFactory.getInstance().create();			
		JDTORecord outRecord2 				= JDTORecordFactory.getInstance().create();			
		JDTORecord outRecord3 				= JDTORecordFactory.getInstance().create();			
		JDTORecord outRecord4 				= JDTORecordFactory.getInstance().create();			
		JDTORecord outRecord5 				= JDTORecordFactory.getInstance().create();			
		JDTORecord outRecord6 				= JDTORecordFactory.getInstance().create();			
		JDTORecord outRecord9 				= JDTORecordFactory.getInstance().create();			
		JDTORecordSet rsResult1				= null;
		JDTORecordSet rsResult2				= null;
		JDTORecordSet rsResult3				= null;
		Calendar today						= Calendar.getInstance();
		SimpleDateFormat sdf 				= new SimpleDateFormat("yyyyMMddHHmmss");
		String sRTN_CD				= "";
		String sRTN_MSG				= "";
		
		String sFR_YD_STK_COL_GP	= "";
		String sFR_YD_STK_BED_NO	= "";
		String sFR_YD_EQP_GP		= "";
		
		String sTO_YD_STK_COL_GP	= "";
		String sTO_YD_STK_BED_NO	= "";
		String sTO_YD_EQP_GP		= "";
		String sSTL_NO				= "";
		String sYD_WBOOK_ID         = "";
		String sYD_SCH_CD           = "";
		String sYD_SCH_CD1          = "";
		String sYD_USER_ID          = "";
		String sYD_AIM_YD_GP 		= "";
		String sSND_FLAG			= ""; 
		String sTO_YD_STK_LYR_NO	= "";
		String sFR_YD_STK_LYR_NO	= "";
		String sSND_COMM_FLAG		= "";

		String sMTL_UPD_FLAG		= "";
		String sMTL_MOV_UPD_FLAG	= "";
		String sYD_CRN_SCH_ID       = "";
		String sCRANE_SND			= "";
		String sYD_WRK_PROG_STAT	= "";
		String sYD_GP				= "";
		String sYD_AIM_BAY_GP       = "";
		String sCURR_PROG_CD		= "";
		JDTORecord[] 	inRecordarr = null;			
		int    intRtnVal 			= 0;
		int    intRtnVal3 			= 0;
			
		try{
	
			szLogMsg = "JSP-FACADE  [ "+ szMethodName +" ]시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);
			
			gdRes = OperateGridData.cloneResponseGridData(gdReq);		
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);

			sCURR_PROG_CD  		= gdReq.getHeader("CURR_PROG_CD").getValue(0); // 진도코드
			sFR_YD_STK_COL_GP	= gdReq.getHeader("YD_STK_COL_GP").getValue(0); // 야드적치열
			sFR_YD_STK_BED_NO	= gdReq.getHeader("YD_STK_BED_NO").getValue(0); // 번지
			sFR_YD_STK_LYR_NO	= gdReq.getHeader("CURR_STK_LYR_NO").getValue(0); // 적치단
			sFR_YD_EQP_GP  		= sFR_YD_STK_COL_GP.substring(2, 4);			// fr야드설비구분     
			
			sTO_YD_STK_COL_GP	= gdReq.getHeader("UPD_STK_POS_2").getValue(0); // 야드적치열
			sTO_YD_STK_BED_NO	= gdReq.getHeader("UPD_STK_BED_NO").getValue(0);// 
			sTO_YD_STK_LYR_NO	= gdReq.getHeader("UPD_STK_LYR_NO").getValue(0); // 적치단
			sTO_YD_EQP_GP  		= sTO_YD_STK_COL_GP.substring(2, 4);			// to야드설비구분     
			
			sSTL_NO				= gdReq.getHeader("STL_NO").getValue(0);
			sYD_USER_ID			= gdReq.getHeader("YD_USER_ID").getValue(0);					// 사용자ID
			sYD_AIM_YD_GP		= gdReq.getHeader("YD_AIM_YD_GP").getValue(0);					// 사용자ID
			sYD_AIM_BAY_GP		= gdReq.getHeader("YD_AIM_BAY_GP").getValue(0); // 
			sSND_FLAG			= gdReq.getParam("SND_FLAG");		// 송신 여부

			sMTL_UPD_FLAG		= gdReq.getParam("MTL_UPD_FLAG");		// 재료 속성 변경
			sYD_SCH_CD1			= StringHelper.evl(gdReq.getParam("YD_SCH_CD"), "");			// 재료 속성 변경
			sYD_GP				= gdReq.getParam("YD_GP");									// 송신 여부
//SJH
			sMTL_MOV_UPD_FLAG	= StringHelper.evl(gdReq.getParam("MTL_MOV_UPD_FLAG"), "N");	// 이송하차인경우			

			ydUtils.putLog(szSessionName, szMethodName, "sCURR_PROG_CD:"	+ sCURR_PROG_CD, YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, "sFR_YD_STK_COL_GP:"	+ sFR_YD_STK_COL_GP, YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, "sFR_YD_STK_BED_NO:"	+ sFR_YD_STK_BED_NO, YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, "sFR_YD_EQP_GP:"		+ sFR_YD_EQP_GP, YdConstant.INFO);
			
			ydUtils.putLog(szSessionName, szMethodName, "sTO_YD_STK_COL_GP:"	+ sTO_YD_STK_COL_GP, YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, "sTO_YD_STK_BED_NO:"	+ sTO_YD_STK_BED_NO, YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, "sTO_YD_STK_LYR_NO:"	+ sTO_YD_STK_LYR_NO, YdConstant.INFO);
			
			ydUtils.putLog(szSessionName, szMethodName, "sTO_YD_EQP_GP:"		+ sTO_YD_EQP_GP, YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, "sSTL_NO:"				+ sSTL_NO, YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, "sSND_FLAG:"			+ sSND_FLAG, YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, "sMTL_UPD_FLAG:"		+ sMTL_UPD_FLAG, YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, "sYD_SCH_CD1:"		    + sYD_SCH_CD1, YdConstant.INFO);
			
			ydUtils.putLog(szSessionName, szMethodName, "MTL_MOV_UPD_FLAG:"	    + sMTL_MOV_UPD_FLAG, YdConstant.INFO);
			///////////////////////////////////////////////////////////////////////
			//저장위치 수정일 경우 	
			///////////////////////////////////////////////////////////////////////
			//======================================================================================
			if(sMTL_UPD_FLAG.equals("Y")) {  //속성만 변경
				//YARD 정보 수정:UPDATE TB_YD_STKLYR, TB_YD_STOCK
				ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);			
				outRecord1 		= (JDTORecord)ejbConn.trx("updYDStrlocModMgt1", new Class[] { JDTORecord[].class,GridData.class }, new Object[] { inRecord,gdReq });
				sRTN_CD			= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
				sRTN_MSG		= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
				sSND_COMM_FLAG	= StringHelper.evl(outRecord1.getFieldString("SND_COMM_FLAG"), "");
	
				if ("0".equals(sRTN_CD)) {
					gdRes.setMessage(sRTN_MSG);		
					m_ctx.setRollbackOnly();
					return gdRes;	 
				}	
			} else {
	
				rsResult1 = JDTORecordFactory.getInstance().createRecordSet("");
				inRecord1 = JDTORecordFactory.getInstance().create();			
				inRecord1.setField("STL_NO",      sSTL_NO);
				/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getCrnSchByStlNo*/
				intRtnVal = ydCrnschDao.getYdCrnsch(inRecord1, rsResult1, 52);
				if( intRtnVal  > 0 ) {
//					gdRes.setMessage("스케쥴이 편성되어 있습니다. 삭제처리후 작업하세요");		
//					m_ctx.setRollbackOnly();
//					return gdRes;	

					rsResult1.first();
					outRecord9 = rsResult1.getRecord();
					sYD_CRN_SCH_ID 	= ydDaoUtils.paraRecChkNull(outRecord9, "YD_CRN_SCH_ID");
					sYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(outRecord9, "YD_SCH_CD");
					inRecord4   	= JDTORecordFactory.getInstance().create();
					inRecord4.setField("YD_CRN_SCH_ID"	,sYD_CRN_SCH_ID);
					inRecord4.setField("YD_SCH_CD"		,sYD_SCH_CD);
					inRecord4.setField("DEL_YN"			,"Y");
					inRecord4.setField("MODIFIER"		,sYD_USER_ID);
					//스케줄 취소			
					ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);			
					outRecord5 = (JDTORecord)ejbConn.trx("WrkCancelloc", new Class[] { JDTORecord.class }, new Object[] { inRecord4 });
					sRTN_CD				= StringHelper.evl(outRecord5.getFieldString("RTN_CD"), "0");
					sRTN_MSG			= StringHelper.evl(outRecord5.getFieldString("RTN_MSG"), "");
					sCRANE_SND			= StringHelper.evl(outRecord5.getFieldString("CRANE_SND"), "");
					sYD_WRK_PROG_STAT	= StringHelper.evl(outRecord5.getFieldString("YD_WRK_PROG_STAT"), "");
					
					if ("0".equals(sRTN_CD)) {
						gdRes.setMessage(sRTN_MSG);		
						m_ctx.setRollbackOnly();
						return gdRes;	
					}	
					//작업예약삭제
					outRecord5.setField("MODIFIER"		,sYD_USER_ID);
					outRecord5.setField("YD_USER_ID"	,sYD_USER_ID);
					ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);			
					outRecord6 = (JDTORecord)ejbConn.trx("delWookBook", new Class[] { JDTORecord.class }, new Object[] { outRecord5 });
					sRTN_CD				= StringHelper.evl(outRecord6.getFieldString("RTN_CD"), "0");
					sRTN_MSG			= StringHelper.evl(outRecord6.getFieldString("RTN_MSG"), "");
					sYD_SCH_CD			= StringHelper.evl(outRecord6.getFieldString("YD_SCH_CD"), "");
						if ("0".equals(sRTN_CD)) {
						gdRes.setMessage(sRTN_MSG);		
						m_ctx.setRollbackOnly();
						return gdRes;
					}	
		
						//작업취소전문 송신
					if ("Y".equals(sCRANE_SND)) {
						recDelPara   = JDTORecordFactory.getInstance().create();
						recDelPara.setField("MSG_ID",           "YDY5L004"        );
						recDelPara.setField("YD_CRN_SCH_ID",    sYD_CRN_SCH_ID          ); 
						recDelPara.setField("YD_WRK_PROG_STAT", sYD_WRK_PROG_STAT    );   // 이모듈을 탈려면 항상 '1'의값이 들어옴
						recDelPara.setField("MSG_GP",           "D"                );
						ydDelegate.sendMsg(recDelPara);
					}	
				} 
				else {	


					rsResult2 = JDTORecordFactory.getInstance().createRecordSet("Temp");
					inRecord1 = JDTORecordFactory.getInstance().create();
					
					inRecord1.setField("YD_BAY_GP"	, sTO_YD_STK_COL_GP.substring(1,2));                  
					inRecord1.setField("STL_NO"		, sSTL_NO);                
			    	/*com.inisteel.cim.yd.dao.ydwrkbookmtldao.YdWrkbookmtlDao.getYdWrkbookmtlYdbaygpSTLNO*/
			    	intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(inRecord1, rsResult2, 405);
					
					if( intRtnVal > 0 ) {
						rsResult2.first();
						outRecord0 = rsResult2.getRecord();
						sYD_WBOOK_ID 	= ydDaoUtils.paraRecChkNull(outRecord0, "YD_WBOOK_ID");
						sYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(outRecord0, "YD_SCH_CD");
					}
						
					if(!(sYD_WBOOK_ID.equals(""))){
						// 작업예약취소  호출	
						inRecordarr = new JDTORecord[1];
	
						inRecordarr[0] = JDTORecordFactory.getInstance().create();
						inRecordarr[0].setField("YD_WBOOK_ID"		, sYD_WBOOK_ID); 
						inRecordarr[0].setField("YD_USER_ID"	    , sYD_USER_ID); 
	
						ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
						String rtnMsg = (String)ejbConn.trx("delYdWrkbook",
								new Class[] { JDTORecord[].class }, new Object[] { inRecordarr });
	
						if (!rtnMsg.equals(YdConstant.RETN_CD_SUCCESS)) {
							gdRes.setMessage(rtnMsg);		
							m_ctx.setRollbackOnly();
							return gdRes;
						}	
	
					}
				
				}
				
				//YARD 정보 수정:UPDATE TB_YD_STKLYR, TB_YD_STOCK
				ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);			
				outRecord1 		= (JDTORecord)ejbConn.trx("updYDStrlocModMgt", new Class[] { JDTORecord[].class,GridData.class }, new Object[] { inRecord,gdReq });
				sRTN_CD			= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
				sRTN_MSG		= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
				sSND_COMM_FLAG	= StringHelper.evl(outRecord1.getFieldString("SND_COMM_FLAG"), "");
	
				if ("0".equals(sRTN_CD) && !sSND_FLAG.equals("STOCKED")) {
					gdRes.setMessage(sRTN_MSG);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}	
				
				//===============================================================================================================
				// 대차에서 내릴 경우 
				if( sFR_YD_EQP_GP.equals("TC") ){
					if( (sTO_YD_EQP_GP.substring(0,1).equals("0")) ||(sTO_YD_EQP_GP.substring(0,1).equals("1"))){
	
						inRecord1 = JDTORecordFactory.getInstance().create();
						inRecord1.setField("STL_NO", sSTL_NO);
						rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
						/*com.inisteel.cim.yd.dao.ydtcarftmvmtldao.YdTcarftmvmtlDao.getYdStockTcarSTL_NO*/
						intRtnVal = ydTcarftmvmtlDao.getYdTcarftmvmtl(inRecord1, rsResult2, 302);
						if( intRtnVal <= 0 ) {
							gdRes.setMessage("대차위에  정보가 존재하지 않습니다.");		
						}else{
							//조회된 차량정지위치에서 운송장비코드를 가져온다.
							rsResult2.first();
							outRecord0 = rsResult2.getRecord();
							
							String szYD_TCAR_SCH_ID	= ydDaoUtils.paraRecChkNull(outRecord0, "YD_TCAR_SCH_ID");//운송장비코드
	
							inRecord1 = JDTORecordFactory.getInstance().create();
							inRecord1.setField("YD_TCAR_SCH_ID"	, szYD_TCAR_SCH_ID);						
							inRecord1.setField("STL_NO"			, sSTL_NO);							
							intRtnVal = ydTcarftmvmtlDao.delYdTcarftmvmtl(inRecord1);
							if(intRtnVal < 1) {
								szMsg="[대차 정보 삭제시 이상 발생 Error!! Code : " + intRtnVal;
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								gdRes.setMessage(szMsg);		
								m_ctx.setRollbackOnly();
								return gdRes;	
							}
						}
					}
				}
				//===============================================================================================================
				// 대차에 올릴경우  
				if( sTO_YD_EQP_GP.equals("TC") ){
					if( (sFR_YD_EQP_GP.substring(0,1).equals("0")) ||(sFR_YD_EQP_GP.substring(0,1).equals("1"))){
	
						rsResult2 = JDTORecordFactory.getInstance().createRecordSet("Temp");
						inRecord1 = JDTORecordFactory.getInstance().create();
						
						inRecord1.setField("YD_EQP_ID", "JXTC"+sTO_YD_STK_COL_GP.substring(4,6));                   //JXTC01
				    	/*com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.getYdTcarschByYdEqpId*/
				    	intRtnVal = ydTcarSchDao.getYdTcarsch(inRecord1, rsResult2, 4);
						
						if( intRtnVal <= 0 ) {
							gdRes.setMessage("대차스케쥴  정보가 존재하지 않습니다.");		
						}else{
							
							rsResult2.first();
							outRecord0 = rsResult2.getRecord();
							
							String szYD_TCAR_SCH_ID	= ydDaoUtils.paraRecChkNull(outRecord0, "YD_TCAR_SCH_ID");
							String szYD_EQP_WRK_STAT= ydDaoUtils.paraRecChkNull(outRecord0, "YD_EQP_WRK_STAT");
						
							rsResult2 = JDTORecordFactory.getInstance().createRecordSet("Temp");
							inRecord1 = JDTORecordFactory.getInstance().create();
							
							inRecord1.setField("EQP_ID", "TC"+sTO_YD_STK_COL_GP.substring(4,6));                  
							inRecord1.setField("STL_NO", sSTL_NO);                
					    	/*com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.getYdTcarWrkBookId*/
					    	intRtnVal = ydTcarSchDao.getYdTcarsch(inRecord1, rsResult2, 203);
							
							if( intRtnVal <= 0 ) {
								szMsg="하차 스케쥴이 없는 경우에는 대차위치로 저장위치변경할수 없습니다.!!";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								gdRes.setMessage(szMsg);		
								m_ctx.setRollbackOnly();
								return gdRes;	
	
							}
								
							
							inRecord1 = JDTORecordFactory.getInstance().create();
							inRecord1.setField("YD_TCAR_SCH_ID"	, szYD_TCAR_SCH_ID);						
							inRecord1.setField("STL_NO"			, sSTL_NO);	
							inRecord1.setField("REGISTER"		, sYD_USER_ID);
							inRecord1.setField("YD_STK_BED_NO"	, sTO_YD_STK_BED_NO) ;
							inRecord1.setField("YD_STK_LYR_NO"	, "001") ;
							
							intRtnVal = ydTcarftmvmtlDao.insYdTcarftmvmtl(inRecord1);
							if(intRtnVal < 1) {
								szMsg="[대차 재료 정보추가시  이상 발생 Error!! Code : " + intRtnVal;
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								gdRes.setMessage(szMsg);		
								m_ctx.setRollbackOnly();
								return gdRes;	
							}
							
							inRecord1 = JDTORecordFactory.getInstance().create();
							inRecord1.setField("YD_TCAR_SCH_ID"		, szYD_TCAR_SCH_ID);							
							inRecord1.setField("YD_CARUD_STOP_LOC"  , sYD_GP+sYD_AIM_BAY_GP+"TC"+sTO_YD_STK_COL_GP.substring(4,6) ); 
							
							inRecord1.setField("YD_CAR_PROG_STAT", "4");
				 
				    		intRtnVal = ydTcarSchDao.updYdTcarsch(inRecord1, 0);
							if(intRtnVal <= 0) {
								szMsg="[대차 스케줄 상태변경시  발생 Error!! Code : " + intRtnVal;
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								gdRes.setMessage(szMsg);		
								m_ctx.setRollbackOnly();
								return gdRes;	
							}	
						}
					}	
				}
				
				//===============================================================================================================
				// 차량에서 내릴 경우 
				if( sFR_YD_EQP_GP.equals("PT") ){
					if( (sTO_YD_EQP_GP.substring(0,1).equals("0")) ||(sTO_YD_EQP_GP.substring(0,1).equals("1"))){
	
						inRecord1 = JDTORecordFactory.getInstance().create();
						inRecord1.setField("YD_STK_COL_GP", sTO_YD_STK_COL_GP.substring(0, 6));
						rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
						
						intRtnVal = ydStkColDao.getYdStkcol(inRecord1, rsResult2, 0);
						if( intRtnVal <= 0 ) {
							szMsg="[차량정지위치 정보가 존재하지 않습니다" + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							gdRes.setMessage(szMsg);		
							m_ctx.setRollbackOnly();
							return gdRes;	
							
						}else{
							//조회된 차량정지위치에서 운송장비코드를 가져온다.
							rsResult2.first();
							outRecord0 = rsResult2.getRecord();
							
							//운송장비코드
			        		String szTRN_EQP_CD1 	= ydDaoUtils.paraRecChkNull(outRecord0, "TRN_EQP_CD");
			        		String szYD_CAR_USE_GP1	= ydDaoUtils.paraRecChkNull(outRecord0, "YD_CAR_USE_GP");
			        		String szCAR_NO1 		= ydDaoUtils.paraRecChkNull(outRecord0, "CAR_NO");
			        		String szCARD_NO1 		= ydDaoUtils.paraRecChkNull(outRecord0, "CARD_NO");
			        	
			        		//운송장비코드로 차량스케줄조회
			        		inRecord2 = JDTORecordFactory.getInstance().create();
			        		inRecord2.setField("YD_CAR_USE_GP"	, szYD_CAR_USE_GP1);
			        		inRecord2.setField("TRN_EQP_CD"		, szTRN_EQP_CD1);
			        		inRecord2.setField("CAR_NO"			, szCAR_NO1);
			        		inRecord2.setField("CARD_NO"		, szCARD_NO1);
			        		rsResult3 = JDTORecordFactory.getInstance().createRecordSet("");
//PIDEV_S :병행가동용:PI_YD
			        		inRecord2.setField("PI_YD",    	sYD_GP);	
			    			intRtnVal = ydCarSchDao.getYdCarsch(inRecord2, rsResult3, 27);						
			    			if( intRtnVal <= 0 ) {
			    				szMsg="[운송장비코드 정보가 존재하지 않습니다" + intRtnVal;
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								gdRes.setMessage(szMsg);		
								m_ctx.setRollbackOnly();
								return gdRes;	
				                
			            	}else{
			            		
			            		rsResult3.absolute(1);
			            		outRecord3 = rsResult3.getRecord();
			            		String szYD_CAR_SCH_ID1 		= ydDaoUtils.paraRecChkNull(outRecord3, "YD_CAR_SCH_ID"); //차량스케줄ID
			            		
			            		inRecord3 = JDTORecordFactory.getInstance().create();
			            		inRecord3.setField("YD_CAR_SCH_ID"	, szYD_CAR_SCH_ID1);
			            		inRecord3.setField("STL_NO"			, sSTL_NO); 
			            		inRecord3.setField("MODIFIER"		, sYD_USER_ID);
			            		inRecord3.setField("DEL_YN"			, "Y");
						    		
		
			                	//차량 작업 진행관리 호출(하차)
			                	intRtnVal3 = ydCarFtmvMtlDao.updYdCarftmvmtl(inRecord3, 0);
			                	if(intRtnVal3 < 1) {
									szMsg="[차량 재료 정보삭제시  이상 발생 Error!! Code : " + intRtnVal;
									ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
									gdRes.setMessage(szMsg);		
									m_ctx.setRollbackOnly();
									return gdRes;	
			                	}
			            	}
						}
					}
				}
				//===============================================================================================================
				// 차량에 올릴경우  
				if( sTO_YD_EQP_GP.equals("PT") ){
					if( (sFR_YD_EQP_GP.substring(0,1).equals("0")) ||(sFR_YD_EQP_GP.substring(0,1).equals("1"))){
	
						inRecord1 = JDTORecordFactory.getInstance().create();
						inRecord1.setField("YD_STK_COL_GP", sTO_YD_STK_COL_GP.substring(0, 6));
						rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
						
						intRtnVal = ydStkColDao.getYdStkcol(inRecord1, rsResult2, 0);
						if( intRtnVal <= 0 ) {
							szMsg="[차량정지위치 정보가 존재하지 않습니다" + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							gdRes.setMessage(szMsg);		
							m_ctx.setRollbackOnly();
							return gdRes;	
							
						}else{
							//조회된 차량정지위치에서 운송장비코드를 가져온다.
							rsResult2.first();
							outRecord0 = rsResult2.getRecord();
							
							//운송장비코드
			        		String szTRN_EQP_CD1 	= ydDaoUtils.paraRecChkNull(outRecord0, "TRN_EQP_CD");
			        		String szYD_CAR_USE_GP1	= ydDaoUtils.paraRecChkNull(outRecord0, "YD_CAR_USE_GP");
			        		String szCAR_NO1 		= ydDaoUtils.paraRecChkNull(outRecord0, "CAR_NO");
			        		String szCARD_NO1 		= ydDaoUtils.paraRecChkNull(outRecord0, "CARD_NO");
			        	
			        		//운송장비코드로 차량스케줄조회
			        		inRecord2 = JDTORecordFactory.getInstance().create();
			        		inRecord2.setField("YD_CAR_USE_GP"	, szYD_CAR_USE_GP1);
			        		inRecord2.setField("TRN_EQP_CD"		, szTRN_EQP_CD1);
			        		inRecord2.setField("STL_NO"			, sSTL_NO);
			        		 
			        		rsResult3 = JDTORecordFactory.getInstance().createRecordSet("");
			        		/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschByStlNoCarID*/
			    			intRtnVal = ydCarSchDao.getYdCarsch(inRecord2, rsResult3, 433);						
			    			if( intRtnVal <= 0 ) {
			    				szMsg="[차량SCH 운송장비 정보가 존재하지 않습니다" + intRtnVal;
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								gdRes.setMessage(szMsg);		
								m_ctx.setRollbackOnly();
								return gdRes;	
				                
			            	}else{
			            		
			            		rsResult3.absolute(1);
			            		outRecord3 = rsResult3.getRecord();
			            		String szYD_CAR_SCH_ID1 		= ydDaoUtils.paraRecChkNull(outRecord3, "YD_CAR_SCH_ID"); //차량스케줄ID
			            		
			            		inRecord3 = JDTORecordFactory.getInstance().create();
			            		inRecord3.setField("YD_TCAR_SCH_ID"	, szYD_CAR_SCH_ID1);						
			            		inRecord3.setField("STL_NO"			, sSTL_NO);	
								
			            		inRecord3.setField("REGISTER"		, sYD_USER_ID);
			            		inRecord3.setField("YD_STK_BED_NO"	, sTO_YD_STK_BED_NO) ;
			            		inRecord3.setField("YD_STK_LYR_NO"	, "001") ;
								
								intRtnVal = ydCarFtmvMtlDao.insYdCarftmvmtl(inRecord3);
								if(intRtnVal < 1) {
									szMsg="[차량 재료 정보추가시  이상 발생 Error!! Code : " + intRtnVal;
									ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
									gdRes.setMessage(szMsg);		
									m_ctx.setRollbackOnly();
									return gdRes;	
								}
			            	}
						}
					}
				}
				if(sSND_COMM_FLAG.equals("Y")){
				
					//저장위치 변경이력 등록
					JDTORecord recCrnStock = null;
					YdWrkHistDao ydWrkHistDao = new YdWrkHistDao();			
					 
					recCrnStock = JDTORecordFactory.getInstance().create(); 
					recCrnStock.setField("STL_NO"			, sSTL_NO);
					recCrnStock.setField("MODIFIER"			, sYD_USER_ID);
					recCrnStock.setField("YD_UP_WR_LOC"		, sFR_YD_STK_COL_GP+sFR_YD_STK_BED_NO);
					recCrnStock.setField("YD_UP_WR_LAYER"	, "00"+sFR_YD_STK_LYR_NO); //FROM 단
					recCrnStock.setField("YD_DN_WR_LOC"		, sTO_YD_STK_COL_GP+sTO_YD_STK_BED_NO);
					recCrnStock.setField("YD_DN_WR_LAYER"	, sTO_YD_STK_LYR_NO); //TO 단
					recCrnStock.setField("YD_GP"			, sTO_YD_STK_COL_GP.substring(0, 1));
					recCrnStock.setField("SPEC_ABBSYM"		, sSND_FLAG);
					recCrnStock.setField("YD_GP"			, sTO_YD_STK_COL_GP.substring(0, 1));
					if(sYD_SCH_CD1.equals("")) {
					} else {	
						recCrnStock.setField("YD_SCH_CD"	, sYD_SCH_CD1);
					}
					//------------------------------------------------------------------------------------
					
					// 이력테이블에 INSERT
					intRtnVal = ydWrkHistDao.insYdCoilWrkHist(recCrnStock);
					
					if(intRtnVal<=0) {
						szMsg = "재료번호(" + sSTL_NO + ")에 대한 INSERT가 실패하였습니다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						gdRes.setMessage(szMsg);		
						m_ctx.setRollbackOnly();
						return gdRes;	
					}
				}	
				if(sSND_COMM_FLAG.equals("Y")){
					
					//코일 공통  정보 수정				
					inRecord9 = JDTORecordFactory.getInstance().create();
					inRecord9.setField("UPD_STK_POS_2"	, sTO_YD_STK_COL_GP);
					inRecord9.setField("UPD_STK_BED_NO"	, sTO_YD_STK_BED_NO);
					inRecord9.setField("UPD_STK_LYR_NO"	, sTO_YD_STK_LYR_NO);
					inRecord9.setField("YD_USER_ID"		, sYD_USER_ID);
					inRecord9.setField("STL_NO"			, sSTL_NO);
					
					
					ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);			
					outRecord2 	= (JDTORecord)ejbConn.trx("updCOMMStrlocModMgt", new Class[] { JDTORecord.class }, new Object[] { inRecord9 });
					sRTN_CD		= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
					sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
			
					if ("0".equals(sRTN_CD)) {
						gdRes.setMessage(sRTN_MSG);		
						m_ctx.setRollbackOnly();
						return gdRes;	
					}	
					
					//L2저장품재원 정보 송신
					//======================================================
					// 저장품제원 : 코일야드L2로 송신(YDY5L002)
					//======================================================
					JDTORecord recResult = null;
					recResult = JDTORecordFactory.getInstance().create();
					recResult.setField("MSG_ID"         , "YDY5L002");
					recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
					recResult.setField("STL_NO"         , sSTL_NO);
					recResult.setField("YD_STK_COL_GP"  , "");
					recResult.setField("YD_STK_BED_NO"  , "");
					
					ydDelegate.sendMsg(recResult);
		
					szMsg = "코일야드L2로 응답전문 [YDY5L002] 전송완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					
					//제품장인 경우 출하에 저장위치변경 통보
					if(sFR_YD_STK_COL_GP.substring(0, 1).equals("J")){
						//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
						//코일제품이적작업실적
						JDTORecord tcRecordDM = null;
						tcRecordDM = JDTORecordFactory.getInstance().create(); 
						tcRecordDM.setField("GOODS_NO"		, sSTL_NO);
//						tcRecordDM.setField("BEFO_STORE_LOC", sFR_YD_STK_COL_GP+sFR_YD_STK_BED_NO+"00"+sFR_YD_STK_LYR_NO);
//						tcRecordDM.setField("TO_STORE_LOC"	, sTO_YD_STK_COL_GP+sTO_YD_STK_BED_NO+sTO_YD_STK_LYR_NO);
		
						tcRecordDM.setField("BEFO_STORE_LOC",ydUtils.ParsingStkColGpBedLyr(sFR_YD_STK_COL_GP, sFR_YD_STK_BED_NO, "00"+sFR_YD_STK_LYR_NO));
						tcRecordDM.setField("TO_STORE_LOC",ydUtils.ParsingStkColGpBedLyr(sTO_YD_STK_COL_GP, sTO_YD_STK_BED_NO, sTO_YD_STK_LYR_NO));
						
						//인터페이스 전문 호출
						ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
						ejbConn.trx("getYDDMR004",new Class[]{JDTORecord.class}, new Object[]{tcRecordDM}); 
		
						szMsg = "코일야드에서 출하L3로 응답전문 [YDDMR004] 전송완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
					}
					
					
					//반납 실적처리 등록 (backup)
					if((sFR_YD_STK_COL_GP.substring(0, 1).equals("H") && sCURR_PROG_CD.equals("J"))
						|| (sMTL_MOV_UPD_FLAG.equals("Y") && sCURR_PROG_CD.equals("E"))){
						//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

						JDTORecord getRecord = null;
						getRecord = JDTORecordFactory.getInstance().create(); 
						getRecord.setField("YD_MTL_ITEM"	, "CM");
						getRecord.setField("STL_NO"			, sSTL_NO);
						 //트렌젝션 분리
	        	        ejbConn = new EJBConnector("default", "CoilCraneUdHdSeEJB", this);
	        	        ejbConn.trx("Y5SetProgCodeCoil", new Class[] { JDTORecord.class }, new Object[] { getRecord });
	        	        
	        	        if(sFR_YD_STK_COL_GP.substring(0, 1).equals("H") && sCURR_PROG_CD.equals("J")){
							//반납실적정보 전송 YDDMR034
							JDTORecord tcRecordDM = null;
							tcRecordDM = JDTORecordFactory.getInstance().create(); 				        	
							tcRecordDM.setField("MSG_ID",        "YDDMR034");			//반납확정정보:전문코드
							tcRecordDM.setField("STL_NO",     	 sSTL_NO);				//제품번호
			    			
			    			ydDelegate.sendMsg(tcRecordDM);
			    			
							szMsg="출하관리 코일반납작업실적전송 송신 완료";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        	        }	

						///진행에 송신 :sndJMSInfo	YDPTJ002
						szMsg="진행관리 실적전송 송신 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						JDTORecordSet getRecSet1 			= JDTORecordFactory.getInstance().createRecordSet("temp");
						getRecSet1 = JDTORecordFactory.getInstance().createRecordSet("");
						inRecord2 = JDTORecordFactory.getInstance().create();			
						inRecord2.setField("STL_NO", sSTL_NO);
						/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschYdCarMtlCoilComm*/
						intRtnVal = ydCarSchDao.getYdCarsch(inRecord2, getRecSet1, 305);	
						if(intRtnVal < 0) {
							szMsg = "COIL공통 테이블 조회오류  [Ret : " + intRtnVal + "]";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
						} else if(intRtnVal == 0) {
							szMsg = "COIL공통 테이블 조회건수 없음  [Ret : " + intRtnVal + "]";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
						}				
						
						getRecSet1.first();
						JDTORecord recGetVal = null;
						recGetVal = getRecSet1.getRecord(0);	
						JDTORecord recInTemp = null;
						recInTemp 	= JDTORecordFactory.getInstance().create();
			
						recInTemp.setField("JMS_TC_CD"				, "YDPTJ002");
						recInTemp.setField("JMS_TC_CREATE_DDTT"		, YdUtils.getCurDate("yyyyMMddHHmmss"));				
						recInTemp.setField("STL_NO"					, sSTL_NO.trim()); // 재료번호
						recInTemp.setField("ORD_NO"					, ydDaoUtils.paraRecChkNull(recGetVal, "ORD_NO")); // 주문번호
						recInTemp.setField("ORD_DTL"				, ydDaoUtils.paraRecChkNull(recGetVal, "ORD_DTL"));  // 주문행번
						recInTemp.setField("PLNT_PROC_CD"			, ydDaoUtils.paraRecChkNull(recGetVal, "PLNT_PROC_CD")); // 공장공정코드
						recInTemp.setField("STL_APPEAR_GP"			, ydDaoUtils.paraRecChkNull(recGetVal, "STL_APPEAR_GP"));  // 재료외형구분
						recInTemp.setField("CURR_PROG_CD"			, ydDaoUtils.paraRecChkNull(recGetVal, "CURR_PROG_CD"));   // 현재진도코드
						recInTemp.setField("ORD_YEOJAE_GP"			, ydDaoUtils.paraRecChkNull(recGetVal, "ORD_YEOJAE_GP"));  // 주문여재구분
						recInTemp.setField("STL_WT"					, ydDaoUtils.paraRecChkNull(recGetVal, "COIL_WT"));   // 재료중량 (COIL중량) 
						recInTemp.setField("DS_MTL_WT"				, "");		// 설계재료중량
						recInTemp.setField("MTL_STAT_GP"			, ydDaoUtils.paraRecChkNull(recGetVal, "MTL_STAT_GP")); // 재료상태구분
						recInTemp.setField("RECORD_END_GP"			, ydDaoUtils.paraRecChkNull(recGetVal, "RECORD_END_GP")); // Record 종료구분
						recInTemp.setField("RECORD_END_GP1"			, "");
						recInTemp.setField("BEFO_PROG_CD"			, ydDaoUtils.paraRecChkNull(recGetVal, "BEFO_PROG_CD")); // 전진도 코드
						recInTemp.setField("BEF_ORD_NO"				, ydDaoUtils.paraRecChkNull(recGetVal, "BEF_ORD_NO"));	// 전주문 번호
						recInTemp.setField("BEF_ORD_DTL"			, ydDaoUtils.paraRecChkNull(recGetVal, "BEF_ORD_DTL"));	// 전주문 행번
						recInTemp.setField("MMATL_FEE_NO"			, ydDaoUtils.paraRecChkNull(recGetVal, "MMATL_FEE_NO"));	// 모재료번호   
						recInTemp.setField("ORDERTRANS_MATCH_GP"	, ydDaoUtils.paraRecChkNull(recGetVal, "MATCH_ORDERTRANS_GP"));	// 목전충당구분
						
						this.sndJMSInfo(recInTemp);
							
						//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
						if(sMTL_MOV_UPD_FLAG.equals("Y") && sCURR_PROG_CD.equals("E")) {
		    			//----------------------------------------------------------------------------------------------------------
		    			// 이송지시테이블 업데이트 - 이송완료일자, 이송계상일자, 이송상태코드, 야드재료예정저장To위치코드
		    			//----------------------------------------------------------------------------------------------------------
							PtStlFrtoMoveDao ptStlFrtoMoveDao 	= new PtStlFrtoMoveDao();
							
							inRecord8 = JDTORecordFactory.getInstance().create();
			    			inRecord8.setField("STL_NO",        sSTL_NO);
			    			
			    			JDTORecordSet rsResultTemp = JDTORecordFactory.getInstance().createRecordSet("");
			    			intRtnVal = ptStlFrtoMoveDao.getPtStlFrtoMove(inRecord8, rsResultTemp, 0);
							if( intRtnVal <= 0 ) {
								szMsg="[이송지시] 진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]에 재료[" + sSTL_NO + "]가 존재하지 않습니다.";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							} else {
							
							
								rsResultTemp.first();
								JDTORecord recOutTemp = JDTORecordFactory.getInstance().create();
				    			recOutTemp.setRecord(rsResultTemp.getRecord());
				    			recOutTemp.setField("YD_MTL_PLN_STR_TO_LOC_CD", sTO_YD_STK_COL_GP);
				    			recOutTemp.setField("FRTOMOVE_STAT_CD", "*");
				    			
				    			szMsg="[이송지시]  진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]- 코일소재재료번호["
				    			+ sSTL_NO + "], 이송지시차수[" 
				    			+ ydDaoUtils.paraRecChkNull(recOutTemp, "TRANSWORD_SEQNO") + "]에  이송완료일자, 이송계상일자, 이송상태코드, 야드재료예정저장To위치코드 업데이트 시작";
				    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				    			
				    			intRtnVal = ptStlFrtoMoveDao.updPtStlFrtoMove(recOutTemp, 1);
				    			if( intRtnVal <= 0 ) {
									gdRes.setMessage("소재이송지시TABLE UPDATE 실패");		
				    			}
							}
						}		
					
					}
					
					
				}	
			}
			gdRes.setMessage("저장위치 변경 성공");
		
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		return gdRes;
	}  //end of updStrlocMod3
	
	/**
	 * 저장위치변경관리 (저장위치수정: 송신처리)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updStrlocMod2(GridData gdReq) throws DAOException {

		YdStkColDao ydStkColDao 			= new YdStkColDao();
		YdDelegate ydDelegate 				= new YdDelegate();
		YdStockDao ydStockDao 				= new YdStockDao();
		String szMsg         				= "";
		String szMethodName  				= "updStrlocMod2";
		GridData gdRes       				= null;
		EJBConnector ejbConn 				= null;
		String szLogMsg      				= "";
		JDTORecord inRecord1  				= JDTORecordFactory.getInstance().create();			
		JDTORecord inRecord2  				= JDTORecordFactory.getInstance().create();			
		JDTORecord inRecord8  				= JDTORecordFactory.getInstance().create();			
		JDTORecord inRecord9  				= JDTORecordFactory.getInstance().create();			
		JDTORecord outRecord0 				= JDTORecordFactory.getInstance().create();			
		JDTORecord outRecord1 				= JDTORecordFactory.getInstance().create();			
		JDTORecord outRecord2 				= JDTORecordFactory.getInstance().create();			
		JDTORecordSet rsResult1				= null;
		JDTORecordSet rsResult2				= null;

		Calendar today						= Calendar.getInstance();
		SimpleDateFormat sdf 				= new SimpleDateFormat("yyyyMMddHHmmss");
		String sRTN_CD				= "";
		String sRTN_MSG				= "";
		
		String sFR_YD_STK_COL_GP	= "";
		String sFR_YD_STK_BED_NO	= "";
		String sFR_YD_EQP_GP		= "";
		
		String sTO_YD_STK_COL_GP	= "";
		String sTO_YD_STK_BED_NO	= "";
		String sTO_YD_EQP_GP		= "";
		String sSTL_NO				= "";
		String sYD_WBOOK_ID         = "";
		String sYD_SCH_CD           = "";
		String sDB_DATE             = sdf.format(today.getTime());
		String sYD_USER_ID          = "";
		String sYD_AIM_YD_GP 		= "";
		String sSND_FLAG			= ""; 
		String sTO_YD_STK_LYR_NO	= "";
		String sFR_YD_STK_LYR_NO	= "";
//		String sUpdToPos			= "";
		String sYD_GP				= "";
		String sTRANS_ORD_DATE		= "";
		String sTRANS_ORD_SEQNO	 	= "";
		String sINPUT_DATA1			= "";
		String sINPUT_DATA2			= "";
		String sINPUT_DATA3			= "";
		String sINPUT_DATA4			= "";
		String sINPUT_DATA5			= "";
		String sCURR_PROG_CD		= "";
		int    intRtnVal 			= 0;
		
		try{
	
			szLogMsg = "JSP-FACADE  [ "+ szMethodName +" ]시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);
			
			gdRes = OperateGridData.cloneResponseGridData(gdReq);		
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);

			
			sFR_YD_STK_COL_GP	= gdReq.getHeader("YD_STK_COL_GP").getValue(0); 				// 야드적치열
			sFR_YD_STK_BED_NO	= gdReq.getHeader("YD_STK_BED_NO").getValue(0); 				// 번지
			sFR_YD_STK_LYR_NO	= gdReq.getHeader("CURR_STK_LYR_NO").getValue(0); 				// 적치단(1자리)
			sFR_YD_EQP_GP  		= sFR_YD_STK_COL_GP.substring(2, 4);							// fr야드설비구분     
			
			sTO_YD_STK_COL_GP	= gdReq.getHeader("UPD_STK_POS_2").getValue(0); 				// 야드적치열
			sTO_YD_STK_BED_NO	= gdReq.getHeader("UPD_STK_BED_NO").getValue(0);				// 
			sTO_YD_STK_LYR_NO	= gdReq.getHeader("UPD_STK_LYR_NO").getValue(0); 				// 적치단(3자리)
			sTO_YD_EQP_GP  		= sTO_YD_STK_COL_GP.substring(2, 4);							// to야드설비구분     

			sSTL_NO				= gdReq.getHeader("STL_NO").getValue(0);
			sYD_USER_ID			= gdReq.getHeader("YD_USER_ID").getValue(0);					// 사용자ID
			
			sTRANS_ORD_DATE		= gdReq.getHeader("TRANS_ORD_DATE").getValue(0);				// 운송지시 번호
			sTRANS_ORD_SEQNO	= gdReq.getHeader("TRANS_ORD_SEQNO").getValue(0);				// 운송지시 순번
			
			sYD_AIM_YD_GP		= gdReq.getHeader("YD_AIM_YD_GP").getValue(0);					// 
			
			sSND_FLAG			= gdReq.getParam("SND_FLAG");									// 송신 여부
			sYD_GP				= gdReq.getParam("YD_GP");									// 송신 여부
			sCURR_PROG_CD		= gdReq.getHeader("CURR_PROG_CD").getValue(0);
//			sYD_GP				= sFR_YD_STK_COL_GP.substring(0, 1);
			
			
			ydUtils.putLog(szSessionName, szMethodName, "sFR_YD_STK_COL_GP:"	+ sFR_YD_STK_COL_GP, YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, "sFR_YD_STK_BED_NO:"	+ sFR_YD_STK_BED_NO, YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, "sFR_YD_EQP_GP:"		+ sFR_YD_EQP_GP, YdConstant.INFO);
			
			ydUtils.putLog(szSessionName, szMethodName, "sTO_YD_STK_COL_GP:"	+ sTO_YD_STK_COL_GP, YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, "sTO_YD_STK_BED_NO:"	+ sTO_YD_STK_BED_NO, YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, "sTO_YD_STK_LYR_NO:"	+ sTO_YD_STK_LYR_NO, YdConstant.INFO);
			
			ydUtils.putLog(szSessionName, szMethodName, "sTO_YD_EQP_GP:"		+ sTO_YD_EQP_GP, YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, "sSTL_NO:"				+ sSTL_NO, YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, "sSND_FLAG:"			+ sSND_FLAG, YdConstant.INFO);
			ydUtils.putLog(szSessionName, szMethodName, "sYD_GP:"				+ sYD_GP, YdConstant.INFO);	
			ydUtils.putLog(szSessionName, szMethodName, "sCURR_PROG_CD:"		+ sCURR_PROG_CD, YdConstant.INFO);
			///////////////////////////////////////////////////////////////////////
			// 전문 송신
			///////////////////////////////////////////////////////////////////////

			//===============================================================================================================
			// 저장품 생성
			if(sSND_FLAG.equals("STOCK")){ // 저장품()
				inRecord1 = JDTORecordFactory.getInstance().create();
				inRecord1.setField("STL_NO",     	sSTL_NO);								//재료번호

				ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);			
				outRecord1 	= (JDTORecord)ejbConn.trx("insYdStockInfo", new Class[] { JDTORecord.class }, new Object[] { inRecord1 });

				sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
				sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");

				if (!sRTN_CD.equals("0")) {
					gdRes.setMessage(sRTN_MSG);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}

			}

			//===============================================================================================================
			// LINE IN
			if(sSND_FLAG.equals("LINEIN")){ 
				sINPUT_DATA1		= gdReq.getParam("PARA_INPUT_DATA1"); 							// 카드번호
				sINPUT_DATA2		= gdReq.getParam("PARA_INPUT_DATA2"); 							// 차량번호

				if ((sINPUT_DATA1.equals(""))||(sINPUT_DATA1.length() != 6)) {
					szMsg = "설비ID가 이상합니다.(6자리)";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		
					gdRes.setMessage(szMsg);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}
				if ((sINPUT_DATA2.equals(""))||(sINPUT_DATA2.length() != 2)) {
					szMsg = "적치베드번호가 이상합니다..";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		
					gdRes.setMessage(szMsg);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}

				inRecord1 = JDTORecordFactory.getInstance().create();
				inRecord1.setField("MSG_ID",        "YDHRJ001"); 							//열연조업 L3 정정보급완료 실적  전문코드
				inRecord1.setField("STL_NO",     	sSTL_NO);								//재료번호
				inRecord1.setField("YD_EQP_ID",     sINPUT_DATA1);						//야드설비id
				inRecord1.setField("YD_STK_BED_NO", sINPUT_DATA2);						//야드적치베드번호
				inRecord1.setField("YD_DN_RSLT_DT", sDB_DATE);								//야드권하완료일시
				inRecord1.setField("TREAT_GP", "1");									//1:보급완료, 2:보급취소, 5:Take-In
				
				ydDelegate.sendMsg(inRecord1);
				
				szMsg="열연조업 L3 정정보급완료 실적 전송 송신 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				//품질 열연정정입측보급실적----------------------------------------------
				inRecord1 = JDTORecordFactory.getInstance().create();
				inRecord1.setField("MSG_ID",      	"YDQMJ002"); 							 
				inRecord1.setField("STL_NO",     	sSTL_NO);	  			//재료번호				
				ydDelegate.sendMsg(inRecord1);

				szMsg="품질 L3 열연정정입측보급실적 전송 송신 완료3";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				//-------------------------------------------------------------------
				
				inRecord2 = JDTORecordFactory.getInstance().create();
				if(( sINPUT_DATA1.startsWith("HHKE01")) ||
						( sINPUT_DATA1.startsWith("HHKD01")) ){    	/* 코일소재[H]야드 H동 SPM1 보급 */
					inRecord2.setField("MSG_ID",        "YDH2L001");							//전문코드
					inRecord2.setField("STL_NO",     	sSTL_NO);								//재료번호
					inRecord2.setField("YD_EQP_ID",     sINPUT_DATA1);			//야드설비ID
					inRecord2.setField("YD_STK_BED_NO", sINPUT_DATA2);			//야드적치Bed번호 
					ydDelegate.sendMsg(inRecord2);

					szMsg="열연조업 L2  정정 SPM1 보급완료 실적  전송 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);			        	
				}

				if( sINPUT_DATA1.startsWith("HGFE01")){		/* 코일소재[H]야드 G동 HFL 보급 */
					inRecord2.setField("MSG_ID",        "YDH2L011");							//전문코드
					inRecord2.setField("STL_NO",     	sSTL_NO);								//재료번호
					inRecord2.setField("YD_EQP_ID",     sINPUT_DATA1);			//야드설비ID
					inRecord2.setField("YD_STK_BED_NO", sINPUT_DATA2);			//야드적치Bed번호 
					ydDelegate.sendMsg(inRecord2);

					szMsg="열연조업 L2  정정 HFL 보급완료 실적  전송 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				        	
				}

				if(( sINPUT_DATA1.startsWith("HEDE01"))||
						( sINPUT_DATA1.startsWith("HEDD01"))){		/* 코일소재[H]야드 E동 SPM2 보급 */
					inRecord2.setField("MSG_ID",        "YDH2L021");							//전문코드
					inRecord2.setField("STL_NO",     	sSTL_NO);								//재료번호
					inRecord2.setField("YD_EQP_ID",     sINPUT_DATA1);			//야드설비ID
					inRecord2.setField("YD_STK_BED_NO", sINPUT_DATA2);			//야드적치Bed번호 
					ydDelegate.sendMsg(inRecord2);

					szMsg="열연조업 L2  정정 SPM2 보급완료 실적  전송 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				        	
				}	
//C증설 
				if(( sINPUT_DATA1.startsWith("HCKE03")) ||
						( sINPUT_DATA1.startsWith("HCKD03")) ){    	
					inRecord2.setField("MSG_ID",        "YDH2L031");							//전문코드
					inRecord2.setField("STL_NO",     	sSTL_NO);								//재료번호
					inRecord2.setField("YD_EQP_ID",     sINPUT_DATA1);			//야드설비ID
					inRecord2.setField("YD_STK_BED_NO", sINPUT_DATA2);			//야드적치Bed번호 
					ydDelegate.sendMsg(inRecord2);

					szMsg="열연조업 L2  정정 SPM3 보급완료 실적  전송 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);			        	
				}
				if(( sINPUT_DATA1.startsWith("HBKE04")) ||
						( sINPUT_DATA1.startsWith("HBKD04")) ){    	
					inRecord2.setField("MSG_ID",        "YDH2L041");							//전문코드
					inRecord2.setField("STL_NO",     	sSTL_NO);								//재료번호
					inRecord2.setField("YD_EQP_ID",     sINPUT_DATA1);			//야드설비ID
					inRecord2.setField("YD_STK_BED_NO", sINPUT_DATA2);			//야드적치Bed번호 
					ydDelegate.sendMsg(inRecord2);

					szMsg="열연조업 L2  정정 SPM4 보급완료 실적  전송 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);			        	
				}
				if(( sINPUT_DATA1.startsWith("HAKE05")) ||
						( sINPUT_DATA1.startsWith("HAKD05")) ){    	
					inRecord2.setField("MSG_ID",        "YDH2L071");							//전문코드
					inRecord2.setField("STL_NO",     	sSTL_NO);								//재료번호
					inRecord2.setField("YD_EQP_ID",     sINPUT_DATA1);			//야드설비ID
					inRecord2.setField("YD_STK_BED_NO", sINPUT_DATA2);			//야드적치Bed번호 
					ydDelegate.sendMsg(inRecord2);

					szMsg="열연조업 L2  정정 SPM5 보급완료 실적  전송 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);			        	
				}
				if( sINPUT_DATA1.startsWith("HCFE04")){		
					inRecord2.setField("MSG_ID",        "YDH2L051");							//전문코드
					inRecord2.setField("STL_NO",     	sSTL_NO);								//재료번호
					inRecord2.setField("YD_EQP_ID",     sINPUT_DATA1);			//야드설비ID
					inRecord2.setField("YD_STK_BED_NO", sINPUT_DATA2);			//야드적치Bed번호 
					ydDelegate.sendMsg(inRecord2);

					szMsg="열연조업 L2  정정 HFL4 보급완료 실적  전송 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				        	
				}
				if( sINPUT_DATA1.startsWith("HBFE05")){		
					inRecord2.setField("MSG_ID",        "YDH2L061");							//전문코드
					inRecord2.setField("STL_NO",     	sSTL_NO);								//재료번호
					inRecord2.setField("YD_EQP_ID",     sINPUT_DATA1);			//야드설비ID
					inRecord2.setField("YD_STK_BED_NO", sINPUT_DATA2);			//야드적치Bed번호 
					ydDelegate.sendMsg(inRecord2);

					szMsg="열연조업 L2  정정 HFL5 보급완료 실적  전송 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				        	
				}
			
			
			}	 	

			//===============================================================================================================
			// LINE OFF
			if(sSND_FLAG.equals("LINEOFF")){ 
				sINPUT_DATA1		= gdReq.getParam("PARA_INPUT_DATA1"); 							// 카드번호
				sINPUT_DATA2		= gdReq.getParam("PARA_INPUT_DATA2"); 							// 차량번호

				if ((sINPUT_DATA1.equals(""))||(sINPUT_DATA1.length() != 6)) {
					szMsg = "설비ID가 이상합니다.(6자리)";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		
					gdRes.setMessage(szMsg);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}
				if ((sINPUT_DATA2.equals(""))||(sINPUT_DATA2.length() != 2)) {
					szMsg = "적치베드번호가 이상합니다..";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		
					gdRes.setMessage(szMsg);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}

				inRecord1 = JDTORecordFactory.getInstance().create();
				inRecord1.setField("MSG_ID"       	, "YDHRJ002");			//전문코드
				inRecord1.setField("STL_NO"       	, sSTL_NO);				//재료번호
				inRecord1.setField("TREAT_GP"       , "3");					//재료번호
				inRecord1.setField("YD_UP_CMPL_DT"	, sDB_DATE);				//크레인스케줄ID
				ydDelegate.sendMsg(inRecord1);
				szMsg = "열연조업 L3 정정추출완료실적 전문 송신 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				inRecord2 = JDTORecordFactory.getInstance().create();        	
				if( sINPUT_DATA1.startsWith("HHKD01") ){    	/* 코일소재[H]야드 H동 SPM1 보급 */
					inRecord2.setField("MSG_ID"       , "YDH2L003");			// SPM1 전문코드
					inRecord2.setField("YD_EQP_ID"    , sINPUT_DATA1);		// 야드설비ID
					inRecord2.setField("YD_STK_BED_NO", sINPUT_DATA2);		// 야드적치Bed번호 
					inRecord2.setField("STL_NO"       , sSTL_NO);				// 재료번호
					ydDelegate.sendMsg(inRecord2);
					szMsg = "C열연 SPM1 정정출측Line-Off실적 전문(YDH2L003) 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
				}
				if( sINPUT_DATA1.startsWith("HGFD01")){
					inRecord2.setField("MSG_ID"       , "YDH2L013");			// HFL 전문코드
					inRecord2.setField("YD_EQP_ID"    , sINPUT_DATA1);		// 야드설비ID
					inRecord2.setField("YD_STK_BED_NO", sINPUT_DATA2);		// 야드적치Bed번호 
					inRecord2.setField("STL_NO"       , sSTL_NO);				// 재료번호
					ydDelegate.sendMsg(inRecord2);
					szMsg = "C열연 HFL 정정출측Line-Off실적 전문(YDH2L013) 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
				}	
				if( sINPUT_DATA1.startsWith("HEDD01")){
					inRecord2.setField("MSG_ID"       , "YDH2L023");			// SPM2 전문코드
					inRecord2.setField("YD_EQP_ID"    , sINPUT_DATA1);		// 야드설비ID
					inRecord2.setField("YD_STK_BED_NO", sINPUT_DATA2);		// 야드적치Bed번호 
					inRecord2.setField("STL_NO"       , sSTL_NO);				// 재료번호
					ydDelegate.sendMsg(inRecord2);
					szMsg = "C열연 SPM2 정정출측Line-Off실적 전문(YDH2L023) 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
				}
//C증설						
				if( sFR_YD_STK_COL_GP.startsWith("HCKD03") ){    	
					inRecord2.setField("MSG_ID"       , "YDH2L033");			// SPM3 전문코드
					inRecord2.setField("YD_EQP_ID"    , sFR_YD_STK_COL_GP);		// 야드설비ID
					inRecord2.setField("YD_STK_BED_NO", sFR_YD_STK_BED_NO);		// 야드적치Bed번호 
					inRecord2.setField("STL_NO"       , sSTL_NO);				// 재료번호
					ydDelegate.sendMsg(inRecord2);
					szMsg = "C열연 SPM3 정정출측Line-Off실적 전문(YDH2L003) 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
				}

				if( sFR_YD_STK_COL_GP.startsWith("HBKD04") ){    	
					inRecord2.setField("MSG_ID"       , "YDH2L043");			// SPM4 전문코드
					inRecord2.setField("YD_EQP_ID"    , sFR_YD_STK_COL_GP);		// 야드설비ID
					inRecord2.setField("YD_STK_BED_NO", sFR_YD_STK_BED_NO);		// 야드적치Bed번호 
					inRecord2.setField("STL_NO"       , sSTL_NO);				// 재료번호
					ydDelegate.sendMsg(inRecord2);
					szMsg = "C열연 SPM4 정정출측Line-Off실적 전문(YDH2L043) 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
				}

				if( sFR_YD_STK_COL_GP.startsWith("HAKD05") ){    	
					inRecord2.setField("MSG_ID"       , "YDH2L073");			// SPM5 전문코드
					inRecord2.setField("YD_EQP_ID"    , sFR_YD_STK_COL_GP);		// 야드설비ID
					inRecord2.setField("YD_STK_BED_NO", sFR_YD_STK_BED_NO);		// 야드적치Bed번호 
					inRecord2.setField("STL_NO"       , sSTL_NO);				// 재료번호
					ydDelegate.sendMsg(inRecord2);
					szMsg = "C열연 SPM5 정정출측Line-Off실적 전문(YDH2L073) 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
				}
				
				if( sFR_YD_STK_COL_GP.startsWith("HCFD04")){
					inRecord2.setField("MSG_ID"       , "YDH2L053");			// HFL 전문코드
					inRecord2.setField("YD_EQP_ID"    , sFR_YD_STK_COL_GP);		// 야드설비ID
					inRecord2.setField("YD_STK_BED_NO", sFR_YD_STK_BED_NO);		// 야드적치Bed번호 
					inRecord2.setField("STL_NO"       , sSTL_NO);				// 재료번호
					ydDelegate.sendMsg(inRecord2);
					szMsg = "C열연 HFL4 정정출측Line-Off실적 전문(YDH2L013) 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
				}	
				
				if( sFR_YD_STK_COL_GP.startsWith("HBFD05")){
					inRecord2.setField("MSG_ID"       , "YDH2L063");			// HFL 전문코드
					inRecord2.setField("YD_EQP_ID"    , sFR_YD_STK_COL_GP);		// 야드설비ID
					inRecord2.setField("YD_STK_BED_NO", sFR_YD_STK_BED_NO);		// 야드적치Bed번호 
					inRecord2.setField("STL_NO"       , sSTL_NO);				// 재료번호
					ydDelegate.sendMsg(inRecord2);
					szMsg = "C열연 HFL5 정정출측Line-Off실적 전문(YDH2L013) 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
				}	

			}	 

			//===============================================================================================================
			// TAKE IN
			if(sSND_FLAG.equals("TAKEIN")){ 
				sINPUT_DATA1		= gdReq.getParam("PARA_INPUT_DATA1"); 							// 카드번호
				sINPUT_DATA2		= gdReq.getParam("PARA_INPUT_DATA2"); 							// 차량번호

				if ((sINPUT_DATA1.equals(""))||(sINPUT_DATA1.length() != 6)) {
					szMsg = "설비ID가 이상합니다.(6자리)";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		
					gdRes.setMessage(szMsg);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}
				if ((sINPUT_DATA2.equals(""))||(sINPUT_DATA2.length() != 2)) {
					szMsg = "적치베드번호가 이상합니다..";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		
					gdRes.setMessage(szMsg);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}

				inRecord1 = JDTORecordFactory.getInstance().create();
				inRecord1.setField("MSG_ID",        "YDHRJ001"); 							//열연조업 L3 정정보급완료 실적  전문코드
				inRecord1.setField("STL_NO",     	sSTL_NO);								//재료번호
				inRecord1.setField("YD_EQP_ID",     sINPUT_DATA1);						//야드설비id
				inRecord1.setField("YD_STK_BED_NO", sINPUT_DATA2);						//야드적치베드번호
				inRecord1.setField("YD_DN_CMPL_DT", sDB_DATE);								//야드권하완료일시
				inRecord1.setField("TREAT_GP"		, "5");									//1:보급완료, 2:보급취소, 5:Take-In
				ydDelegate.sendMsg(inRecord1);
				szMsg="열연조업 L3 TAKE-IN 완료 실적 전송 송신 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				//품질 열연정정입측보급실적----------------------------------------------
				inRecord1 = JDTORecordFactory.getInstance().create();
				inRecord1.setField("MSG_ID",      	"YDQMJ002"); 							 
				inRecord1.setField("STL_NO",     	sSTL_NO);	  			//재료번호				
				ydDelegate.sendMsg(inRecord1);

				szMsg="품질 L3 열연정정입측보급실적 전송 송신 완료4";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				//-------------------------------------------------------------------
				
				inRecord2 = JDTORecordFactory.getInstance().create();
				if(( sINPUT_DATA1.startsWith("HHKE01")) ||
						( sINPUT_DATA1.startsWith("HHKD01")) ){    	/* 코일소재[H]야드 H동 SPM1 보급 */
					inRecord2.setField("MSG_ID",        "YDH2L001");							//전문코드
					inRecord2.setField("STL_NO",     	sSTL_NO);								//재료번호
					inRecord2.setField("YD_EQP_ID",     sINPUT_DATA1);			//야드설비ID
					inRecord2.setField("YD_STK_BED_NO", sINPUT_DATA2);			//야드적치Bed번호 
					ydDelegate.sendMsg(inRecord2);

					szMsg="열연조업 L2  정정 SPM1 TAKE-IN 실적  전송 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);			        	
				}

				if( sINPUT_DATA1.startsWith("HGFE01")){		/* 코일소재[H]야드 G동 HFL 보급 */
					inRecord2.setField("MSG_ID",        "YDH2L011");							//전문코드
					inRecord2.setField("STL_NO",     	sSTL_NO);								//재료번호
					inRecord2.setField("YD_EQP_ID",     sINPUT_DATA1);			//야드설비ID
					inRecord2.setField("YD_STK_BED_NO", sINPUT_DATA2);			//야드적치Bed번호 
					ydDelegate.sendMsg(inRecord2);

					szMsg="열연조업 L2  정정 HFL TAKE-IN 실적  전송 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				        	
				}

				if(( sINPUT_DATA1.startsWith("HEDE01"))||
						( sINPUT_DATA1.startsWith("HEDD01"))){		/* 코일소재[H]야드 E동 SPM2 보급 */
					inRecord2.setField("MSG_ID",        "YDH2L021");							//전문코드
					inRecord2.setField("STL_NO",     	sSTL_NO);								//재료번호
					inRecord2.setField("YD_EQP_ID",     sINPUT_DATA1);			//야드설비ID
					inRecord2.setField("YD_STK_BED_NO", sINPUT_DATA2);			//야드적치Bed번호 
					ydDelegate.sendMsg(inRecord2);

					szMsg="열연조업 L2  정정 SPM2 TAKE-IN 실적  전송 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				        	
				}	
//C증설	

				if(( sTO_YD_STK_COL_GP.startsWith("HCKE03")) ||
						( sTO_YD_STK_COL_GP.startsWith("HCKD03")) ){    
					inRecord2.setField("MSG_ID",        "YDH2L031");							//전문코드
					inRecord2.setField("STL_NO",     	sSTL_NO);								//재료번호
					inRecord2.setField("YD_EQP_ID",     sTO_YD_STK_COL_GP);			//야드설비ID
					inRecord2.setField("YD_STK_BED_NO", sTO_YD_STK_BED_NO);			//야드적치Bed번호 
					ydDelegate.sendMsg(inRecord2);

					szMsg="열연조업 L2  정정 SPM3 TAKE-IN 실적  전송 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);			        	
				}
				
				if(( sTO_YD_STK_COL_GP.startsWith("HBKE04")) ||
						( sTO_YD_STK_COL_GP.startsWith("HBKD04")) ){   
					inRecord2.setField("MSG_ID",        "YDH2L041");							//전문코드
					inRecord2.setField("STL_NO",     	sSTL_NO);								//재료번호
					inRecord2.setField("YD_EQP_ID",     sTO_YD_STK_COL_GP);			//야드설비ID
					inRecord2.setField("YD_STK_BED_NO", sTO_YD_STK_BED_NO);			//야드적치Bed번호 
					ydDelegate.sendMsg(inRecord2);

					szMsg="열연조업 L2  정정 SPM4 TAKE-IN 실적  전송 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);			        	
				}
				
				if(( sTO_YD_STK_COL_GP.startsWith("HAKE05")) ||
						( sTO_YD_STK_COL_GP.startsWith("HAKD05")) ){   
					inRecord2.setField("MSG_ID",        "YDH2L071");							//전문코드
					inRecord2.setField("STL_NO",     	sSTL_NO);								//재료번호
					inRecord2.setField("YD_EQP_ID",     sTO_YD_STK_COL_GP);			//야드설비ID
					inRecord2.setField("YD_STK_BED_NO", sTO_YD_STK_BED_NO);			//야드적치Bed번호 
					ydDelegate.sendMsg(inRecord2);

					szMsg="열연조업 L2  정정 SPM5 TAKE-IN 실적  전송 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);			        	
				}

				if( sTO_YD_STK_COL_GP.startsWith("HCFE04")){		
					inRecord2.setField("MSG_ID",        "YDH2L051");							//전문코드
					inRecord2.setField("STL_NO",     	sSTL_NO);								//재료번호
					inRecord2.setField("YD_EQP_ID",     sTO_YD_STK_COL_GP);			//야드설비ID
					inRecord2.setField("YD_STK_BED_NO", sTO_YD_STK_BED_NO);			//야드적치Bed번호 
					ydDelegate.sendMsg(inRecord2);

					szMsg="열연조업 L2  정정 HFL4 TAKE-IN 실적  전송 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				        	
				}

				if( sTO_YD_STK_COL_GP.startsWith("HBFE05")){		
					inRecord2.setField("MSG_ID",        "YDH2L061");							//전문코드
					inRecord2.setField("STL_NO",     	sSTL_NO);								//재료번호
					inRecord2.setField("YD_EQP_ID",     sTO_YD_STK_COL_GP);			//야드설비ID
					inRecord2.setField("YD_STK_BED_NO", sTO_YD_STK_BED_NO);			//야드적치Bed번호 
					ydDelegate.sendMsg(inRecord2);

					szMsg="열연조업 L2  정정 HFL5 TAKE-IN 실적  전송 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				        	
				}
				

			
			
			}	 
			
			//===============================================================================================================
			// TAKE OUT
			if(sSND_FLAG.equals("TAKEOUT")){ 
				sINPUT_DATA1		= gdReq.getParam("PARA_INPUT_DATA1"); 							// 카드번호
				sINPUT_DATA2		= gdReq.getParam("PARA_INPUT_DATA2"); 							// 차량번호

				if ((sINPUT_DATA1.equals(""))||(sINPUT_DATA1.length() != 6)) {
					szMsg = "설비ID가 이상합니다.(6자리)";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		
					gdRes.setMessage(szMsg);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}
				if ((sINPUT_DATA2.equals(""))||(sINPUT_DATA2.length() != 2)) {
					szMsg = "적치베드번호가 이상합니다..";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		
					gdRes.setMessage(szMsg);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}
				//입측 //TAKEOUT
				inRecord1 = JDTORecordFactory.getInstance().create();
				inRecord1.setField("MSG_ID"       , "YDHRJ002");			//전문코드
				inRecord1.setField("STL_NO"       , sSTL_NO);				//재료번호
				inRecord1.setField("TREAT_GP"     , "4");					//TAKEOUT
				inRecord1.setField("YD_UP_CMPL_DT", sDB_DATE);				//크레인스케줄ID
				ydDelegate.sendMsg(inRecord1);
				szMsg = "열연조업 L3 정정추출완료실적 전문 송신 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				// 조업 정정 L2 전문
				inRecord2 = JDTORecordFactory.getInstance().create();
				if( sINPUT_DATA1.startsWith("HHKE01") || sINPUT_DATA1.startsWith("HHKD01")){
					inRecord2.setField("MSG_ID"       , "YDH2L004");			// SPM1 전문코드
					inRecord2.setField("YD_EQP_ID"    , sINPUT_DATA1);		// 야드설비ID
					inRecord2.setField("YD_STK_BED_NO", sINPUT_DATA2);		// 야드적치Bed번호 
					inRecord2.setField("STL_NO"       , sSTL_NO);				// 재료번호
					ydDelegate.sendMsg(inRecord2);
					szMsg = "C열연 SPM1 정정입측TAKE-OUT실적 전문(YDH2L004) 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
				}
				if( sINPUT_DATA1.startsWith("HGFE01")){
					inRecord2.setField("MSG_ID"       , "YDH2L014");			// HFL 전문코드
					inRecord2.setField("YD_EQP_ID"    , sINPUT_DATA1);		// 야드설비ID
					inRecord2.setField("YD_STK_BED_NO", sINPUT_DATA2);		// 야드적치Bed번호 
					inRecord2.setField("STL_NO"       , sSTL_NO);				// 재료번호
					ydDelegate.sendMsg(inRecord2);
					szMsg = "C열연 HFL 정정입측TAKE-OUT실적 전문(YDH2L014) 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
				}	
				if( sINPUT_DATA1.startsWith("HEDE01") || sINPUT_DATA1.startsWith("HEDD01")){
					inRecord2.setField("MSG_ID"       , "YDH2L024");			// SPM2 전문코드
					inRecord2.setField("YD_EQP_ID"    , sINPUT_DATA1);		// 야드설비ID
					inRecord2.setField("YD_STK_BED_NO", sINPUT_DATA2);		// 야드적치Bed번호 
					inRecord2.setField("STL_NO"       , sSTL_NO);				// 재료번호
					ydDelegate.sendMsg(inRecord2);
					szMsg = "C열연 SPM2 정정입측TAKE-OUT실적 전문(YDH2L024) 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
				}

//C증설				
				if( (sFR_YD_STK_COL_GP.startsWith("HCKE03") && sYD_SCH_CD.equals("HCKE03LM")) || (sFR_YD_STK_COL_GP.startsWith("HCKD03") && sYD_SCH_CD.equals("HCKD03LM")) ){
					inRecord2.setField("MSG_ID"       , "YDH2L034");			// SPM3 전문코드
					inRecord2.setField("YD_EQP_ID"    , sFR_YD_STK_COL_GP);		// 야드설비ID
					inRecord2.setField("YD_STK_BED_NO", sFR_YD_STK_BED_NO);		// 야드적치Bed번호 
					inRecord2.setField("STL_NO"       , sSTL_NO);				// 재료번호
					ydDelegate.sendMsg(inRecord2);
					szMsg = "C열연 SPM3 정정입측TAKE-OUT실적 전문(YDH2L034) 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
				}
			
				if( (sFR_YD_STK_COL_GP.startsWith("HBKE04") && sYD_SCH_CD.equals("HBKE03LM")) ||  (sFR_YD_STK_COL_GP.startsWith("HBKD04") && sYD_SCH_CD.equals("HBKD03LM"))){
					inRecord2.setField("MSG_ID"       , "YDH2L044");			// SPM4 전문코드
					inRecord2.setField("YD_EQP_ID"    , sFR_YD_STK_COL_GP);		// 야드설비ID
					inRecord2.setField("YD_STK_BED_NO", sFR_YD_STK_BED_NO);		// 야드적치Bed번호 
					inRecord2.setField("STL_NO"       , sSTL_NO);				// 재료번호
					ydDelegate.sendMsg(inRecord2);
					szMsg = "C열연 SPM4 정정입측TAKE-OUT실적 전문(YDH2L044) 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
				}

				if( (sFR_YD_STK_COL_GP.startsWith("HAKE05") && sYD_SCH_CD.equals("HAKE03LM")) ||  (sFR_YD_STK_COL_GP.startsWith("HAKD05") && sYD_SCH_CD.equals("HAKD03LM"))){
					inRecord2.setField("MSG_ID"       , "YDH2L074");			// SPM5 전문코드
					inRecord2.setField("YD_EQP_ID"    , sFR_YD_STK_COL_GP);		// 야드설비ID
					inRecord2.setField("YD_STK_BED_NO", sFR_YD_STK_BED_NO);		// 야드적치Bed번호 
					inRecord2.setField("STL_NO"       , sSTL_NO);				// 재료번호
					ydDelegate.sendMsg(inRecord2);
					szMsg = "C열연 SPM5 정정입측TAKE-OUT실적 전문(YDH2L074) 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
				}
			
				if( sFR_YD_STK_COL_GP.startsWith("HCFE04") && sYD_SCH_CD.equals("HCFE03LM") ){
					inRecord2.setField("MSG_ID"       , "YDH2L054");			// HFL4 전문코드
					inRecord2.setField("YD_EQP_ID"    , sFR_YD_STK_COL_GP);		// 야드설비ID
					inRecord2.setField("YD_STK_BED_NO", sFR_YD_STK_BED_NO);		// 야드적치Bed번호 
					inRecord2.setField("STL_NO"       , sSTL_NO);				// 재료번호
					ydDelegate.sendMsg(inRecord2);
					szMsg = "C열연 HFL4 정정입측TAKE-OUT실적 전문(YDH2L054) 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
				}	

				if( sFR_YD_STK_COL_GP.startsWith("HBFE05") && sYD_SCH_CD.equals("HBFE03LM") ){
					inRecord2.setField("MSG_ID"       , "YDH2L064");			// HFL5 전문코드
					inRecord2.setField("YD_EQP_ID"    , sFR_YD_STK_COL_GP);		// 야드설비ID
					inRecord2.setField("YD_STK_BED_NO", sFR_YD_STK_BED_NO);		// 야드적치Bed번호 
					inRecord2.setField("STL_NO"       , sSTL_NO);				// 재료번호
					ydDelegate.sendMsg(inRecord2);
					szMsg = "C열연 HFL5 정정입측TAKE-OUT실적 전문(YDH2L064) 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);						
				}	
				
			
			
			}
			
			
			//===============================================================================================================
			//컨베어 수입완료
			if(sSND_FLAG.equals("H2LINE")){ 
				sINPUT_DATA1		= gdReq.getParam("PARA_INPUT_DATA1"); 							// 카드번호
				sINPUT_DATA2		= gdReq.getParam("PARA_INPUT_DATA2"); 							// 차량번호

				if ((sINPUT_DATA1.equals(""))||(sINPUT_DATA1.length() != 6)) {
					szMsg = "설비ID가 이상합니다.(6자리)";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		
					gdRes.setMessage(szMsg);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}
				if ((sINPUT_DATA2.equals(""))||(sINPUT_DATA2.length() != 2)) {
					szMsg = "인출위치가 이상합니다..";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		
					gdRes.setMessage(szMsg);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}
				
				inRecord1 = JDTORecordFactory.getInstance().create();
				inRecord1.setField("MSG_ID"       	, "YDH1L001");						//전문코드YD_EQP_ID
				inRecord1.setField("STL_NO"       	, sSTL_NO);							//재료번호
				inRecord1.setField("YD_EQP_ID"    	, sINPUT_DATA1);					//재료번호
				inRecord1.setField("YD_STK_BED_NO"	, sINPUT_DATA2);					//재료번호
				ydDelegate.sendMsg(inRecord1);
				szMsg = "열연 압연 L2 전문 송신 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			}
			//===============================================================================================================

			
			
			// 출하 차량상차개시 SANGSTA 
			if(sSND_FLAG.equals("G_SANGSTA")){  	//권하지시위치로 적치열을 조회하여 차량정지위치 정보를 가져온다.
				sTRANS_ORD_DATE		= gdReq.getHeader("TRANS_ORD_DATE").getValue(0);				// 운송지시 번호
				sTRANS_ORD_SEQNO	= gdReq.getHeader("TRANS_ORD_SEQNO").getValue(0);				// 운송지시 순번
				sINPUT_DATA1		= gdReq.getParam("PARA_INPUT_DATA1"); 							// 카드번호
				sINPUT_DATA2		= gdReq.getParam("PARA_INPUT_DATA2"); 							// 차량번호
				
				inRecord1 = JDTORecordFactory.getInstance().create();
				inRecord1.setField("CARD_NO", sINPUT_DATA1);
				rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
				
				/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getCoilYdStkColYDCard*/
				intRtnVal = ydStkColDao.getYdStkcol(inRecord1, rsResult2, 303);
				if( intRtnVal <= 0 ) {
					szMsg="차량정보가 없습니다." + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					gdRes.setMessage(szMsg);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}
				
				if (sTRANS_ORD_DATE.equals("")) {
					szMsg = "운송지시 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		
					gdRes.setMessage(szMsg);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}
				if (sTRANS_ORD_SEQNO.equals("")) {
					szMsg = "운송지시 순번가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		
					gdRes.setMessage(szMsg);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}
				
				
				//코일출하상차개시
				inRecord1 	= JDTORecordFactory.getInstance().create();
				inRecord1.setField("JMS_TC_CD"           	, new String("YDDMR007"));
				inRecord1.setField("JMS_TC_CREATE_DDTT"  	, new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
				inRecord1.setField("CARD_NO"           		, sINPUT_DATA1);  		// 카드번호
				inRecord1.setField("CAR_NO"            		, sINPUT_DATA2);   		// 차량번호
				inRecord1.setField("YD_GP"             		, sYD_GP);
				inRecord1.setField("CARLOAD_START_DATE"		, new String(YdUtils.getCurDate("yyyyMMdd")));
				inRecord1.setField("CARLOAD_START_TIME"		, new String(YdUtils.getCurDate("HHmmss")));
				inRecord1.setField("TC_CODE"				, "YDDMR007");
				inRecord1.setField("TRANS_WORD_DATE"   		, sTRANS_ORD_DATE);
				inRecord1.setField("TRANS_WORD_SEQNO"  		, sTRANS_ORD_SEQNO);
				
				this.sndJMSInfo(inRecord1);
			}
				
			
			/*
			 * ******* 제품일 경우 ********  
			 * 상차	┏  구내운송	┏ 상차개시TC전송	YDTSJ007  
			 *      ┃              	┗ 상차완료TC전송	YDTSJ008
			 *    	┗  출하		┏ 상차개시TC전송	YDDMR019	코일제품고간이송상하차개시
			 *  				┣ 상차완료TC전송 	YDDMR021	코일제품고간이송상하차완료
			 *  				┣ 	 			YDDMR007	코일출하상차개시
			 *  				┗ 				YDDMR015	코일출하상차완료
			 *  
			 * 하차	┏ 구내운송	┏ 하차개시TC전송 	YDTSJ009
			 * 		┃			┗ 하차완료TC존송	YDTSJ010
			 * 		┗ 출하		┏ 하차개시TC전송	YDDMR019	제품일때만 전송 D
			 * 					┗ 하차완료TC전송	YDDMR021	제품일때만 전송 D
			 *  
			 * */
			//===============================================================================================================
			//출하 차량상차완료 SANGEND//YDDMR015
			if(sSND_FLAG.equals("G_SANGEND")){  	
				sTRANS_ORD_DATE		= gdReq.getHeader("TRANS_ORD_DATE").getValue(0);				// 운송지시 번호
				sTRANS_ORD_SEQNO	= gdReq.getHeader("TRANS_ORD_SEQNO").getValue(0);				// 운송지시 순번
				sINPUT_DATA1		= gdReq.getParam("PARA_INPUT_DATA1"); 							// 카드번호
				sINPUT_DATA2		= gdReq.getParam("PARA_INPUT_DATA2"); 							// 차량번호
				
				inRecord1 = JDTORecordFactory.getInstance().create();
				inRecord1.setField("CARD_NO", sINPUT_DATA1);
				rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
				
				/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getCoilYdStkColYDCard*/
				intRtnVal = ydStkColDao.getYdStkcol(inRecord1, rsResult2, 303);
				if( intRtnVal <= 0 ) {
					szMsg="차량정보가 없습니다." + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					gdRes.setMessage(szMsg);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}
				
				if (sTRANS_ORD_DATE.equals("")) {
					szMsg = "운송지시 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		
					gdRes.setMessage(szMsg);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}
				if (sTRANS_ORD_SEQNO.equals("")) {
					szMsg = "운송지시 순번가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		
					gdRes.setMessage(szMsg);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}
				
				
				//코일출하상차완료
				inRecord1 	= JDTORecordFactory.getInstance().create();
				inRecord1.setField("JMS_TC_CD"           	, new String("YDDMR015"));
				inRecord1.setField("JMS_TC_CREATE_DDTT"  	, new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
				inRecord1.setField("CARD_NO"           		, sINPUT_DATA1);  		// 카드번호
				inRecord1.setField("CAR_NO"            		, sINPUT_DATA2);   		// 차량번호
				inRecord1.setField("YD_GP"             		, sYD_GP);
				inRecord1.setField("CARLOAD_START_DATE"		, new String(YdUtils.getCurDate("yyyyMMdd")));
				inRecord1.setField("CARLOAD_START_TIME"		, new String(YdUtils.getCurDate("HHmmss")));
				inRecord1.setField("TC_CODE"				, "YDDMR015");
				inRecord1.setField("TRANS_WORD_DATE"   		, sTRANS_ORD_DATE);
				inRecord1.setField("TRANS_WORD_SEQNO"  		, sTRANS_ORD_SEQNO);
				
				this.sndJMSInfo(inRecord1);
				
			}


			//===============================================================================================================
			// 구내운송 고간 상차개시:출하만:YDDMR019
				
			if(sSND_FLAG.equals("L_SANGSTA")){  
				if(sYD_GP.equals("J")){  
					sTRANS_ORD_DATE		= gdReq.getHeader("TRANS_ORD_DATE").getValue(0);				// 운송지시 번호
					sTRANS_ORD_SEQNO	= gdReq.getHeader("TRANS_ORD_SEQNO").getValue(0);				// 운송지시 순번
					sINPUT_DATA1		= gdReq.getParam("PARA_INPUT_DATA1"); 							// 코일번호2
					sINPUT_DATA2		= gdReq.getParam("PARA_INPUT_DATA2"); 							// 코일번호3
					sINPUT_DATA3		= gdReq.getParam("PARA_INPUT_DATA3"); 							// 운송장비코드
					sINPUT_DATA4		= gdReq.getParam("PARA_INPUT_DATA4"); 							// 개소코드
					sINPUT_DATA5		= gdReq.getParam("PARA_INPUT_DATA5"); 							// 포인트
					
					if (sTRANS_ORD_DATE.equals("")) {
						szMsg = "운송지시 번호가 없습니다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		
						gdRes.setMessage(szMsg);		
						m_ctx.setRollbackOnly();
						return gdRes;	
					}
					if (sTRANS_ORD_SEQNO.equals("")) {
						szMsg = "운송지시 순번가 없습니다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		
						gdRes.setMessage(szMsg);		
						m_ctx.setRollbackOnly();
						return gdRes;	
					}
					
					
					//고간상차개시
					inRecord1 	= JDTORecordFactory.getInstance().create();
					inRecord1.setField("JMS_TC_CD"           	, new String("YDDMR019"));
					inRecord1.setField("JMS_TC_CREATE_DDTT"  	, new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
					inRecord1.setField("UPCARUNLOAD_GP"    		, "U");  		// U:상차,D:하차
					inRecord1.setField("CARD_NO"           		, "");  		// 카드번호
					inRecord1.setField("CAR_NO"            		, sINPUT_DATA3);   		// 차량번호
					inRecord1.setField("YD_GP"             		, sYD_GP);
					inRecord1.setField("CARLOAD_START_DATE"		, new String(YdUtils.getCurDate("yyyyMMdd")));
					inRecord1.setField("CARLOAD_START_TIME"		, new String(YdUtils.getCurDate("HHmmss")));
					inRecord1.setField("GOODS_NO1"				, sSTL_NO);
					inRecord1.setField("TRANS_WORD_DATE1"   	, sTRANS_ORD_DATE);
					inRecord1.setField("TRANS_WORD_SEQNO1"  	, sTRANS_ORD_SEQNO);
										
					if(!sINPUT_DATA1.equals("")){	
	
						inRecord8 = JDTORecordFactory.getInstance().create();
						inRecord8.setField("STL_NO", sINPUT_DATA1);
						rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
						
						/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockCOILCOMM*/
						intRtnVal  = ydStockDao.getYdStock(inRecord8, rsResult2, 7);
						
						if( intRtnVal <= 0 ) {
							szMsg = "코일번호2(" + sINPUT_DATA1 + ") 저장품에 없습니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							gdRes.setMessage(szMsg);		
							m_ctx.setRollbackOnly();
							return gdRes;	
						}
						rsResult2.first();
						inRecord9 = rsResult2.getRecord();
	
						inRecord1.setField("GOODS_NO2"				, sINPUT_DATA1);
						inRecord1.setField("TRANS_WORD_DATE2"   	, ydDaoUtils.paraRecChkNull(inRecord9, "TRANS_ORD_DATE"));
						inRecord1.setField("TRANS_WORD_SEQNO2"  	, ydDaoUtils.paraRecChkNull(inRecord9, "TRANS_ORD_SEQNO"));
					}
					
					if(!sINPUT_DATA2.equals("")){	
						
						inRecord8 = JDTORecordFactory.getInstance().create();
						inRecord8.setField("STL_NO", sINPUT_DATA2);
						rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
						
						/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockCOILCOMM*/
						intRtnVal  = ydStockDao.getYdStock(inRecord8, rsResult2, 7);
						
						if( intRtnVal <= 0 ) {
							szMsg="코일번호3(" + sINPUT_DATA2 + ") 저장품에 없습니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							gdRes.setMessage(szMsg);		
							m_ctx.setRollbackOnly();
							return gdRes;	
						}
						rsResult2.first();
						inRecord9 = rsResult2.getRecord();
	
						inRecord1.setField("GOODS_NO3"				, sINPUT_DATA2);
						inRecord1.setField("TRANS_WORD_DATE3"   	, ydDaoUtils.paraRecChkNull(inRecord9, "TRANS_ORD_DATE"));
						inRecord1.setField("TRANS_WORD_SEQNO3"  	, ydDaoUtils.paraRecChkNull(inRecord9, "TRANS_ORD_SEQNO"));
					}		
					
					inRecord1.setField("TC_CODE"				, "YDDMR019");
					
					this.sndJMSInfo(inRecord1);
				}
				
			}
			
			//===============================================================================================================
			// 구내운송 하차개시:YDTSJ009 고간 하차개시:YDDMR019
				
			if(sSND_FLAG.equals("L_HASTA")){  	
				if(sYD_GP.equals("J")){  // 구내운송 고간 하차개시:YDDMR019
					
					sTRANS_ORD_DATE		= gdReq.getHeader("TRANS_ORD_DATE").getValue(0);				// 운송지시 번호
					sTRANS_ORD_SEQNO	= gdReq.getHeader("TRANS_ORD_SEQNO").getValue(0);				// 운송지시 순번
					sINPUT_DATA1		= gdReq.getParam("PARA_INPUT_DATA1"); 							// 코일번호2
					sINPUT_DATA2		= gdReq.getParam("PARA_INPUT_DATA2"); 							// 코일번호3
					sINPUT_DATA3		= gdReq.getParam("PARA_INPUT_DATA3"); 							// 운송장비코드
					sINPUT_DATA4		= gdReq.getParam("PARA_INPUT_DATA4"); 							// 개소코드
					sINPUT_DATA5		= gdReq.getParam("PARA_INPUT_DATA5"); 							// 포인트
					
					if (sTRANS_ORD_DATE.equals("")) {
						szMsg = "운송지시 번호가 없습니다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		
						gdRes.setMessage(szMsg);		
						m_ctx.setRollbackOnly();
						return gdRes;	
					}
					if (sTRANS_ORD_SEQNO.equals("")) {
						szMsg = "운송지시 순번가 없습니다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		
						gdRes.setMessage(szMsg);		
						m_ctx.setRollbackOnly();
						return gdRes;	
					}
					
					//고간하차개시
					inRecord1 	= JDTORecordFactory.getInstance().create();
					inRecord1.setField("JMS_TC_CD"           	, new String("YDDMR019"));
					inRecord1.setField("JMS_TC_CREATE_DDTT"  	, new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
					inRecord1.setField("UPCARUNLOAD_GP"    		, "D");  		// U:상차,D:하차
					inRecord1.setField("CARD_NO"           		, "");  		// 카드번호
					inRecord1.setField("CAR_NO"            		, sINPUT_DATA3);   		// 차량번호
					inRecord1.setField("YD_GP"             		, sYD_GP);
					inRecord1.setField("CARLOAD_START_DATE"		, new String(YdUtils.getCurDate("yyyyMMdd")));
					inRecord1.setField("CARLOAD_START_TIME"		, new String(YdUtils.getCurDate("HHmmss")));
					inRecord1.setField("GOODS_NO1"				, sSTL_NO);
					inRecord1.setField("TRANS_WORD_DATE1"   	, sTRANS_ORD_DATE);
					inRecord1.setField("TRANS_WORD_SEQNO1"  	, sTRANS_ORD_SEQNO);
					
					if(!sINPUT_DATA1.equals("")){	
						
						inRecord8 = JDTORecordFactory.getInstance().create();
						inRecord8.setField("STL_NO", sINPUT_DATA1);
						rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
						
						/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockCOILCOMM*/
						intRtnVal  = ydStockDao.getYdStock(inRecord8, rsResult2, 7);
						
						if( intRtnVal <= 0 ) {
							szMsg = "코일번호2(" + sINPUT_DATA1 + ") 저장품에 없습니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							gdRes.setMessage(szMsg);		
							m_ctx.setRollbackOnly();
							return gdRes;	
						}
						rsResult2.first();
						inRecord9 = rsResult2.getRecord();
	
						inRecord1.setField("GOODS_NO2"				, sINPUT_DATA1);
						inRecord1.setField("TRANS_WORD_DATE2"   	, ydDaoUtils.paraRecChkNull(inRecord9, "TRANS_ORD_DATE"));
						inRecord1.setField("TRANS_WORD_SEQNO2"  	, ydDaoUtils.paraRecChkNull(inRecord9, "TRANS_ORD_SEQNO"));
					}
					
					if(!sINPUT_DATA2.equals("")){	
						
						inRecord8 = JDTORecordFactory.getInstance().create();
						inRecord8.setField("STL_NO", sINPUT_DATA2);
						rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
						
						/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockCOILCOMM*/
						intRtnVal  = ydStockDao.getYdStock(inRecord8, rsResult2, 7);
						
						if( intRtnVal <= 0 ) {
							szMsg="코일번호3(" + sINPUT_DATA2 + ") 저장품에 없습니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							gdRes.setMessage(szMsg);		
							m_ctx.setRollbackOnly();
							return gdRes;	
						}
						rsResult2.first();
						inRecord9 = rsResult2.getRecord();
	
						inRecord1.setField("GOODS_NO3"				, sINPUT_DATA2);
						inRecord1.setField("TRANS_WORD_DATE3"   	, ydDaoUtils.paraRecChkNull(inRecord9, "TRANS_ORD_DATE"));
						inRecord1.setField("TRANS_WORD_SEQNO3"  	, ydDaoUtils.paraRecChkNull(inRecord9, "TRANS_ORD_SEQNO"));
					}		
					
					inRecord1.setField("TC_CODE"				, "YDDMR019");

					this.sndJMSInfo(inRecord1);
				
				}	

				
				
				
				
				sINPUT_DATA3		= gdReq.getParam("PARA_INPUT_DATA3"); 							// 차량번호
				sINPUT_DATA4		= gdReq.getParam("PARA_INPUT_DATA4"); 							// 착지개소
				sINPUT_DATA5		= gdReq.getParam("PARA_INPUT_DATA5"); 							// 착지포인트
								
				if (sINPUT_DATA3.equals("") || sINPUT_DATA3.length() != 8) {
					szMsg = "운송장비코드가 이상합니다..";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		
					gdRes.setMessage(szMsg);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}
				if (sINPUT_DATA4.equals("") || sINPUT_DATA4.length() != 5) {
					szMsg = "착지개소가 이상합니다..";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		
					gdRes.setMessage(szMsg);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}
				if (sINPUT_DATA5.equals("") || sINPUT_DATA5.length() != 4 ) {
					szMsg = "착지포인트가 이상합니다..";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		
					gdRes.setMessage(szMsg);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}
				
				
				//구내운송 하차개시
				inRecord1 	= JDTORecordFactory.getInstance().create();
				inRecord1.setField("JMS_TC_CD"           	, new String("YDTSJ009"));
				inRecord1.setField("JMS_TC_CREATE_DDTT"  	, new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
				inRecord1.setField("TRN_EQP_CD"           	, sINPUT_DATA3);  		// 차량번호
				inRecord1.setField("ARR_WLOC_CD"            , sINPUT_DATA4);   		// 착지개소
				inRecord1.setField("ARR_YD_PNT_CD"          , sINPUT_DATA5);        // 착지포인트
				inRecord1.setField("TRN_WRK_ST_DT"			, new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
				
				this.sndJMSInfo(inRecord1);
				
			}
			
			
			//===============================================================================================================
			// 구내운송하차완료:YDTSJ010
			if(sSND_FLAG.equals("L_HAEND")){  	
				if(sYD_GP.equals("J")){  // 구내운송 고간 상하차완료:YDDMR021
					
					
					
					sTRANS_ORD_DATE		= gdReq.getHeader("TRANS_ORD_DATE").getValue(0);				// 운송지시 번호
					sTRANS_ORD_SEQNO	= gdReq.getHeader("TRANS_ORD_SEQNO").getValue(0);				// 운송지시 순번
					sINPUT_DATA1		= gdReq.getParam("PARA_INPUT_DATA1"); 							// 코일번호2
					sINPUT_DATA2		= gdReq.getParam("PARA_INPUT_DATA2"); 							// 코일번호3
					sINPUT_DATA3		= gdReq.getParam("PARA_INPUT_DATA3"); 							// 운송장비코드
					sINPUT_DATA4		= gdReq.getParam("PARA_INPUT_DATA4"); 							// 개소코드
					sINPUT_DATA5		= gdReq.getParam("PARA_INPUT_DATA5"); 							// 포인트
					
					if (sTRANS_ORD_DATE.equals("")) {
						szMsg = "운송지시 번호가 없습니다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		
						gdRes.setMessage(szMsg);		
						m_ctx.setRollbackOnly();
						return gdRes;	
					}
					if (sTRANS_ORD_SEQNO.equals("")) {
						szMsg = "운송지시 순번가 없습니다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		
						gdRes.setMessage(szMsg);		
						m_ctx.setRollbackOnly();
						return gdRes;	
					}
					if(!sFR_YD_STK_COL_GP.substring(0, 1).equals("J")) {
						szMsg = "저장위치를 확인하세요";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		
						gdRes.setMessage(szMsg);		
						m_ctx.setRollbackOnly();
						return gdRes;	
						
					}
					
					//고간하차완료
					inRecord1 	= JDTORecordFactory.getInstance().create();
					inRecord1.setField("JMS_TC_CD"           	, new String("YDDMR021"));
					inRecord1.setField("JMS_TC_CREATE_DDTT"  	, new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
					inRecord1.setField("UPCARUNLOAD_GP"    		, "D");  		// U:상차,D:하차
					inRecord1.setField("TREAT_EA"          		, "1");   // 처리갯수
					inRecord1.setField("CARD_NO"           		, "");  		// 카드번호
					inRecord1.setField("CAR_NO"            		, sINPUT_DATA3);   		// 차량번호
					inRecord1.setField("ARR_YD_PNT_CD"          , "");  
					inRecord1.setField("ISSUE_DDTT"				, new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
					inRecord1.setField("GOODS_NO1"				, sSTL_NO);
					inRecord1.setField("TRANS_WORD_DATE1"   	, sTRANS_ORD_DATE);
					inRecord1.setField("TRANS_WORD_SEQNO1"  	, sTRANS_ORD_SEQNO);
					inRecord1.setField("STORE_LOC_CD1"          , sFR_YD_STK_COL_GP + sFR_YD_STK_BED_NO +"0"+ sFR_YD_STK_LYR_NO );  //1F0306 1002		
					inRecord1.setField("YD_GP1"           		, sFR_YD_STK_COL_GP.substring(0, 1));  		
					inRecord1.setField("BAY_GP1"           		, sFR_YD_STK_COL_GP.substring(1, 2));  //동  		
					inRecord1.setField("SPAN1"           		, sFR_YD_STK_COL_GP.substring(2, 4));  		
					inRecord1.setField("STK_LYR1"           	, "0"+ sFR_YD_STK_LYR_NO);  		

					if(!sINPUT_DATA1.equals("")){	
						
						inRecord8 = JDTORecordFactory.getInstance().create();
						inRecord8.setField("STL_NO", sINPUT_DATA1);
						rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
						
						/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrYdStkBedStl_PIDEV*/
						intRtnVal  = ydStockDao.getYdStock(inRecord8, rsResult2, 26);
						
						if( intRtnVal <= 0 ) {
							szMsg = "코일번호2(" + sINPUT_DATA1 + ") 저장품에 없습니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							gdRes.setMessage(szMsg);		
							m_ctx.setRollbackOnly();
							return gdRes;	
						}
						rsResult2.first();
						inRecord9 = rsResult2.getRecord();
						inRecord1.setField("TREAT_EA"          		, "2");   // 처리갯수
						inRecord1.setField("GOODS_NO2"				, sINPUT_DATA1);
						inRecord1.setField("TRANS_WORD_DATE2"   	, ydDaoUtils.paraRecChkNull(inRecord9, "TRANS_ORD_DATE"));
						inRecord1.setField("TRANS_WORD_SEQNO2"  	, ydDaoUtils.paraRecChkNull(inRecord9, "TRANS_ORD_SEQNO"));
						
						String sFR_YD_STK_COL_GP_TMP = ydDaoUtils.paraRecChkNull(inRecord9, "YD_STK_COL_GP");
						String sFR_YD_STK_BED_NO_TMP = ydDaoUtils.paraRecChkNull(inRecord9, "YD_STK_BED_NO");
						String sFR_YD_STK_LYR_NO_TMP = ydDaoUtils.paraRecChkNull(inRecord9, "YD_STK_LYR_NO");
						
						inRecord1.setField("STORE_LOC_CD2"          , sFR_YD_STK_COL_GP_TMP + sFR_YD_STK_BED_NO_TMP + sFR_YD_STK_LYR_NO_TMP.substring(1, 3) );  //1F0306 1002		
						inRecord1.setField("YD_GP2"           		, sFR_YD_STK_COL_GP_TMP.substring(0, 1));  		
						inRecord1.setField("BAY_GP2"           		, sFR_YD_STK_COL_GP_TMP.substring(1, 2));  //동  		
						inRecord1.setField("SPAN2"           		, sFR_YD_STK_COL_GP_TMP.substring(2, 4));  		
						inRecord1.setField("STK_LYR2"           	, sFR_YD_STK_LYR_NO_TMP.substring(1, 3));  		
					}

					if(!sINPUT_DATA2.equals("")){	
						
						inRecord8 = JDTORecordFactory.getInstance().create();
						inRecord8.setField("STL_NO", sINPUT_DATA2);
						rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
						
						/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrYdStkBedStl_PIDEV*/
						intRtnVal  = ydStockDao.getYdStock(inRecord8, rsResult2, 26);
						
						if( intRtnVal <= 0 ) {
							szMsg = "코일번호2(" + sINPUT_DATA2 + ") 저장품에 없습니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							gdRes.setMessage(szMsg);		
							m_ctx.setRollbackOnly();
							return gdRes;	
						}
						rsResult2.first();
						inRecord9 = rsResult2.getRecord();
						inRecord1.setField("TREAT_EA"          		, "3");   // 처리갯수
						inRecord1.setField("GOODS_NO3"				, sINPUT_DATA2);
						inRecord1.setField("TRANS_WORD_DATE3"   	, ydDaoUtils.paraRecChkNull(inRecord9, "TRANS_ORD_DATE"));
						inRecord1.setField("TRANS_WORD_SEQNO3"  	, ydDaoUtils.paraRecChkNull(inRecord9, "TRANS_ORD_SEQNO"));
						
						String sFR_YD_STK_COL_GP_TMP = ydDaoUtils.paraRecChkNull(inRecord9, "YD_STK_COL_GP");
						String sFR_YD_STK_BED_NO_TMP = ydDaoUtils.paraRecChkNull(inRecord9, "YD_STK_BED_NO");
						String sFR_YD_STK_LYR_NO_TMP = ydDaoUtils.paraRecChkNull(inRecord9, "YD_STK_LYR_NO");
						
						inRecord1.setField("STORE_LOC_CD3"          , sFR_YD_STK_COL_GP_TMP + sFR_YD_STK_BED_NO_TMP + sFR_YD_STK_LYR_NO_TMP.substring(1, 3) );  //1F0306 1002		
						inRecord1.setField("YD_GP3"           		, sFR_YD_STK_COL_GP_TMP.substring(0, 1));  		
						inRecord1.setField("BAY_GP3"           		, sFR_YD_STK_COL_GP_TMP.substring(1, 2));  //동  		
						inRecord1.setField("SPAN3"           		, sFR_YD_STK_COL_GP_TMP.substring(2, 4));  		
						inRecord1.setField("STK_LYR3"           	, sFR_YD_STK_LYR_NO_TMP.substring(1, 3));  		
					
					}
					
					inRecord1.setField("TC_CODE"				, "YDDMR021");

					this.sndJMSInfo(inRecord1);					
				
				}	

				sINPUT_DATA3		= gdReq.getParam("PARA_INPUT_DATA3"); 							// 차량번호
				sINPUT_DATA4		= gdReq.getParam("PARA_INPUT_DATA4"); 							// 착지개소
				sINPUT_DATA5		= gdReq.getParam("PARA_INPUT_DATA5"); 							// 착지포인트
								
				if (sINPUT_DATA3.equals("") || sINPUT_DATA3.length() != 8) {
					szMsg = "운송장비코드가 이상합니다..";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		
					gdRes.setMessage(szMsg);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}
				if (sINPUT_DATA4.equals("") || sINPUT_DATA4.length() != 5) {
					szMsg = "착지개소가 이상합니다..";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		
					gdRes.setMessage(szMsg);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}
				if (sINPUT_DATA5.equals("") || sINPUT_DATA5.length() != 4 ) {
					szMsg = "착지포인트가 이상합니다..";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);		
					gdRes.setMessage(szMsg);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}
				
				
				//구내운송 하차완료
				inRecord1 	= JDTORecordFactory.getInstance().create();
				inRecord1.setField("JMS_TC_CD"           	, new String("YDTSJ010"));
				inRecord1.setField("JMS_TC_CREATE_DDTT"  	, new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
				inRecord1.setField("TRN_EQP_CD"           	, sINPUT_DATA3);  		// 차량번호
				inRecord1.setField("ARR_WLOC_CD"            , sINPUT_DATA4);   		// 착지개소
				inRecord1.setField("ARR_YD_PNT_CD"          , sINPUT_DATA5);        // 착지포인트
				inRecord1.setField("CARUD_CMPL_DT"			, new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
				
				this.sndJMSInfo(inRecord1);
			}
			
			//===============================================================================================================
			// 입고//YDDMR001
			if(sSND_FLAG.equals("STOCKED")){ 
				inRecord1 	= JDTORecordFactory.getInstance().create();
				inRecord1.setField("JMS_TC_CD"           	, "YDDMR001");
				inRecord1.setField("JMS_TC_CREATE_DDTT"  	, new String(YdUtils.getCurDate("yyyyMMddHHmmss")));
				inRecord1.setField("GOODS_NO"           	, sSTL_NO);  		// 차량번호
				inRecord1.setField("RECEIPT_DATE"           , new String(YdUtils.getCurDate("yyyyMMdd")));
				inRecord1.setField("RECEIPT_TIME"          	, new String(YdUtils.getCurDate("HHmmss")));
				inRecord1.setField("YD_GP"					, sYD_GP);
				inRecord1.setField("TC_CODE"				, "YDDMR001");
				inRecord1.setField("STORE_LOC"				, sFR_YD_STK_COL_GP+sFR_YD_STK_BED_NO+"0"+sFR_YD_STK_LYR_NO);
				inRecord1.setField("PROD_ITEM_CODE"			, "");
				inRecord1.setField("CURR_PROG_CD"			, sCURR_PROG_CD);
				
							
				this.sndJMSInfo(inRecord1);

			}
			
			gdRes.setMessage("전문송신 성공");
		
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		return gdRes;
	}  //end of updStrlocMod	
	/**
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
	 * [A] 오퍼레이션명 : (JMS :JDTORecord 송신처리)
	 * 
	 */
	public void sndJMSInfo (JDTORecord param) throws DAOException {	
		
		JmsQueueSender sender = null;
		String queueName = null;
		JDTORecord insRecord = null; 			
		PropertyService propertyService=null;	
		
		
		try {	
			
			StringBuffer sbf = new StringBuffer();			
			
			// 프로퍼티 서비스 인스턴스를 취득합니다.
			propertyService = PropertyService.getInstance();
			
			ydUtils.displayRecord("송신확인", param);
			
			// JDTORecord인스턴스 객체 취득
			insRecord = JDTORecordFactory.getInstance().create();			
					
			String JMS_TC_CD	    	 = StringHelper.evl(param.getFieldString("JMS_TC_CD"), "");				//JMS전문 ID		8
			String Message = "";
			String szWkGp  = JMS_TC_CD.substring(2,4);
//			출하http ->jms 
			// 큐 명칭을 프로퍼티로부터 취득합니다.
			queueName = propertyService.getProperty("common.properties","jms.queue."+szWkGp+"_MDB_QUEUE");	

			
	
			sender = new JmsQueueSender();			
			sender.initQueueService(queueName);		
	
			sender.send(param);
		
		
		}catch (Exception e) {
		}finally {
			try {
				sender.closeAll();
			} catch (Exception e) {
			}
		}
	}
	/**
	 *  크레인 작업관리 조회 (화면:크레인작업관리)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdCrnWoWorkMgt(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdCrnWoWorkMgt";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szMsg = "JSP-FACADE [크레인작업관리 조회 - 화면:크레인작업관리] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn   = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdCrnWoWorkMgt", inRecord);
			gdRes     = CmUtil.genGridData(inDto , recordSet);
			
			szMsg = "JSP-FACADE [크레인작업관리 조회 - 화면:크레인작업관리] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	
	/**
	 *  크레인 작업관리 조회 (화면:크레인작업관리)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdCrnWoWorkMgtDtl(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdCrnWoWorkMgtDtl";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szMsg = "JSP-FACADE [크레인작업관리 조회 - 화면:크레인작업관리] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn   = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdCrnWoWorkMgtDtl", inRecord);
			gdRes     = CmUtil.genGridData(inDto , recordSet);
			
			szMsg = "JSP-FACADE [크레인작업관리 조회 - 화면:크레인작업관리] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	
	/**
	 * 크레인상태관리 - 긴급작업 변경
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	
	public GridData crnChgSchPriorCoil(GridData gdReq) throws JDTOException {						
		GridData gdRes            	= null;
		EJBConnector ejbConn      	= null;
		String szMethodName       	= "crnChgSchPriorCoil";
		String szLogMsg           	= "";
		JDTORecord outRecord   		= JDTORecordFactory.getInstance().create();
		JDTORecord recDelPara   	= null;
		String 	sYD_EQP_ID		 	= "";
		String 	sYD_SCH_CD		 	= "";
		YdEqpDao    ydEqpDao    	= new YdEqpDao();
		JDTORecordSet rsResult 		= null;
		int intRtnVal         		= 0;
		JDTORecord recInTemp   		= null;
		JDTORecord recOutTemp   	= null;
		
		String szYD_EQP_STAT        = "";
		String sYD_CRN_SCH_ID       = "";
		String sYD_WRK_PROG_STAT    = "";
		YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
		YdDelegate ydDelegate = new YdDelegate();
		String szMsg                = "";
		try{
	
			szLogMsg = "JSP-FACADE  [ 크레인상태관리 - 긴급작업 변경 ] 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			gdRes = OperateGridData.cloneResponseGridData(gdReq);		
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);

			sYD_EQP_ID		= gdReq.getHeader("YD_EQP_ID").getValue(0);
			sYD_SCH_CD		= gdReq.getHeader("YD_SCH_CD").getValue(0);					
			
			
			// 150827 긴급작업시 크레인 우선순위 변경 호출
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);			
			outRecord = (JDTORecord)ejbConn.trx("crnChgSchPriorCoil", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			String sRTN_CD	= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
			String sRTN_MSG	= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
			if ("0".equals(sRTN_CD)) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
				return gdRes;	
			}	
			
			try{
	
				rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
				recInTemp 	= JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_EQP_ID"           	,sYD_EQP_ID);
				
				intRtnVal = ydEqpDao.getYdEqp(recInTemp, rsResult, 0);
				if(intRtnVal <= 0) {
					if(intRtnVal == 0) {
						sRTN_MSG = szMethodName + " getYdEqp data not found";
						ydUtils.putLog(szSessionName, szMethodName, sRTN_MSG, YdConstant.INFO);
					}else if(intRtnVal == -2) {
						sRTN_MSG = szMethodName + " getYdEqp parameter error";
						ydUtils.putLog(szSessionName, szMethodName, sRTN_MSG, YdConstant.ERROR);
					}
					gdRes.setMessage(sRTN_MSG);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}
				
				recOutTemp = JDTORecordFactory.getInstance().create();
				recOutTemp.setRecord(rsResult.getRecord(0));
				szYD_EQP_STAT = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_STAT");
				
//				150827 hun 크레인 긴급작업시 Auto크레인 우선순위 변경후 아무것도 안함( 자동화에서 작업지시 다시내려오면 error )
				if(ydEqpDao.chkAutoCrn(sYD_EQP_ID)){
					szMsg = "[Jsp Session : "+szMethodName+"] : 긴급작업 Auto크레인 우선순위 변경후 아무것도 안함( 자동화에서 작업지시 다시내려오면 문제발생!! )" ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					outRecord.setField("RTN_CD", "1");	
					
				}else{
					if((szYD_EQP_STAT.equals("W"))||szYD_EQP_STAT.equals("1")) {
						if(szYD_EQP_STAT.equals("1")) {
	
							
							szMsg = "[Jsp Session : "+szMethodName+"] : 전작업지시  상태변경처리" ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							
							rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
							recInTemp = JDTORecordFactory.getInstance().create();
							recInTemp.setField("YD_EQP_ID"           	,sYD_EQP_ID);
							
							// 150826 hun 긴급작업시 이전작업 상태변경 값S(대기) 추가로 쿼리 변경
							/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnSchEqpIdWrkProgStatPlusS*/
							intRtnVal = ydCrnSchDao.getYdCrnsch(recInTemp, rsResult, 603);
							if(intRtnVal > 0) {
								
								recOutTemp = JDTORecordFactory.getInstance().create();
								recOutTemp.setRecord(rsResult.getRecord(0));
								
								sYD_CRN_SCH_ID 		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CRN_SCH_ID");
								sYD_WRK_PROG_STAT 	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WRK_PROG_STAT");
								
								// 150826 hun 긴급작업시 이전작업 상태변경 값S(대기) 추가
								if (sYD_WRK_PROG_STAT.equals("1") || sYD_WRK_PROG_STAT.equals("S")){
									recInTemp = JDTORecordFactory.getInstance().create();
									recInTemp.setField("YD_WRK_PROG_STAT" , "W");		
									recInTemp.setField("YD_CRN_SCH_ID"    , sYD_CRN_SCH_ID);		
									
									intRtnVal = ydCrnSchDao.updYdCrnsch(recInTemp, 0);
									if (intRtnVal > 0) {
										szMsg = "[Jsp Session : "+szMethodName+"] : 전작업지시  상태변경 완료" ;
										ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
										
									} else {					
										szMsg = "[Jsp Session : "+szMethodName+"] : 전작업지시  상태 실패" ;
										ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
										gdRes.setMessage(sRTN_MSG);		
										m_ctx.setRollbackOnly();
										return gdRes;	
									}
								}	
							}
						}	
						
						
						//if(szYD_EQP_STAT.equals("W")) {
							recInTemp = JDTORecordFactory.getInstance().create();
		//SJH03004					
							recInTemp.setField("MSG_ID",           "YDYDJ643");						
							recInTemp.setField("YD_EQP_ID",        sYD_EQP_ID);
							recInTemp.setField("YD_WRK_PROG_STAT", "W");
							recInTemp.setField("YD_CMD_CHK", 	   "Y");
							
							
							EJBConnector ydEjbCon = new EJBConnector("default", this);
							sRTN_MSG = (String)ydEjbCon.trx("CoilCraneLdHdSeEJB", "procY5CrnWrkOrdReq", recInTemp);
							
							if (sRTN_MSG.equals(YdConstant.RETN_CD_SUCCESS)){
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								outRecord.setField("RTN_CD", "1");						
							}
						//}
	 				}
				}
			}catch(JDTOException de) {
				gdRes.setMessage("긴급작업 변경 성공");		
			}catch(Exception e){
				gdRes.setMessage("긴급작업 변경 성공");		
			}			
				
			
			gdRes.setMessage("긴급작업 변경 성공");		
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}			
		
		
		szLogMsg = "JSP-FACADE  [ 크레인상태관리 - 긴급작업 변경 ] 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		
		return gdRes;
	} // end of crnChgSchPriorCoil()
	
	
	/**
	 * 크레인상태관리 - 긴급작업 변경
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	
	
	public JDTORecord crnChgSchPriorCoilRecord(JDTORecord gdReq) throws JDTOException {						
		GridData gdRes            	= null;
		EJBConnector ejbConn      	= null;
		String szMethodName       	= "crnChgSchPriorCoilRecord";
		String szLogMsg           	= "";
		JDTORecord outRecord   		= JDTORecordFactory.getInstance().create();
		JDTORecord recDelPara   	= null;
		String 	sYD_EQP_ID		 	= "";
		String 	sYD_SCH_CD		 	= "";
		YdEqpDao    ydEqpDao    	= new YdEqpDao();
		JDTORecordSet rsResult 		= null;
		int intRtnVal         		= 0;
		JDTORecord recInTemp   		= null;
		JDTORecord recOutTemp   	= null;
		
		String szYD_EQP_STAT        = "";
		String sYD_CRN_SCH_ID       = "";
		String sYD_WRK_PROG_STAT    = "";
		YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
		YdDelegate ydDelegate = new YdDelegate();
		String szMsg                = "";
		try{
	
			szLogMsg = "JSP-FACADE  [ 크레인상태관리 - 긴급작업 변경 ] 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			

			sYD_EQP_ID		= ydDaoUtils.paraRecChkNull(gdReq, "YD_EQP_ID");

			
	
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);			
			outRecord = (JDTORecord)ejbConn.trx("proccrnChgSchPriorCoilRecord", new Class[] { JDTORecord.class }, new Object[] { gdReq });
			String sRTN_CD	= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
			String sRTN_MSG	= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
			if ("0".equals(sRTN_CD)) {
				m_ctx.setRollbackOnly();
				szMsg = "해당정보는 대기 상태가 아닙니다.";
				outRecord.setField("RTN_CD" 		, "0");	
				outRecord.setField("RTN_MSG" 		, szMsg);	
				return outRecord;
			}	
			
	
				rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
				recInTemp 	= JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_EQP_ID"           	,sYD_EQP_ID);
				
				intRtnVal = ydEqpDao.getYdEqp(recInTemp, rsResult, 0);
				if(intRtnVal <= 0) {
					if(intRtnVal == 0) {
						sRTN_MSG = szMethodName + " getYdEqp data not found";
						ydUtils.putLog(szSessionName, szMethodName, sRTN_MSG, YdConstant.INFO);
					}else if(intRtnVal == -2) {
						sRTN_MSG = szMethodName + " getYdEqp parameter error";
						ydUtils.putLog(szSessionName, szMethodName, sRTN_MSG, YdConstant.ERROR);
					}

					m_ctx.setRollbackOnly();
					szMsg = "해당정보는 대기 상태가 아닙니다.";
					outRecord.setField("RTN_CD" 		, "0");	
					outRecord.setField("RTN_MSG" 		, szMsg);	
					return outRecord;
				}
				
				recOutTemp = JDTORecordFactory.getInstance().create();
				recOutTemp.setRecord(rsResult.getRecord(0));
				szYD_EQP_STAT = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_EQP_STAT");
				
				if((szYD_EQP_STAT.equals("W"))||szYD_EQP_STAT.equals("1")) {
					if(szYD_EQP_STAT.equals("1")) {

						
						szMsg = "[Jsp Session : "+szMethodName+"] : 전작업지시  상태변경처리" ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
						recInTemp = JDTORecordFactory.getInstance().create();
						recInTemp.setField("YD_EQP_ID"           	,sYD_EQP_ID);
						
						/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnSchEqpIdWrkProgStat*/
						intRtnVal = ydCrnSchDao.getYdCrnsch(recInTemp, rsResult, 16);
						if(intRtnVal > 0) {
							
							recOutTemp = JDTORecordFactory.getInstance().create();
							recOutTemp.setRecord(rsResult.getRecord(0));
							
							sYD_CRN_SCH_ID 		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_CRN_SCH_ID");
							sYD_WRK_PROG_STAT 	= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_WRK_PROG_STAT");
							if (sYD_WRK_PROG_STAT.equals("1")){
								recInTemp = JDTORecordFactory.getInstance().create();
								recInTemp.setField("YD_WRK_PROG_STAT" , "W");		
								recInTemp.setField("YD_CRN_SCH_ID"    , sYD_CRN_SCH_ID);		
								
								intRtnVal = ydCrnSchDao.updYdCrnsch(recInTemp, 0);
								if (intRtnVal > 0) {
									szMsg = "[Jsp Session : "+szMethodName+"] : 전작업지시  상태변경 완료" ;
									ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
									
								} else {					
									szMsg = "[Jsp Session : "+szMethodName+"] : 전작업지시  상태 실패" ;
									ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					
									m_ctx.setRollbackOnly();
									outRecord.setField("RTN_CD" 		, "0");	
									outRecord.setField("RTN_MSG" 		, szMsg);	
									return outRecord;
								}
								
								
								//설비Table의 상태 변경 (권상완료상태로 변경)
								recInTemp = JDTORecordFactory.getInstance().create();
								recInTemp.setField("YD_EQP_ID",			 sYD_EQP_ID);
								recInTemp.setField("YD_EQP_STAT",        JPlateYdConst.YD_EQP_STAT_IDLE);			 
								 						 
						        intRtnVal = ydEqpDao.updYdEqp(recInTemp, 0);						     
						 
							}	
						}
					}	
					recInTemp = JDTORecordFactory.getInstance().create();
//SJH03004					
					recInTemp.setField("MSG_ID",           "YDYDJ643");
					
					recInTemp.setField("YD_EQP_ID",        sYD_EQP_ID);
					recInTemp.setField("YD_WRK_PROG_STAT", "W");
					//recInTemp.setField("YD_CRN_SCH_ID_RE", sYD_CRN_SCH_ID);
					EJBConnector ydEjbCon = new EJBConnector("default", this);
					sRTN_MSG = (String)ydEjbCon.trx("CoilCraneLdHdSeEJB", "procY5CrnWrkOrdReq", recInTemp);
					
					if (sRTN_MSG.equals(YdConstant.RETN_CD_SUCCESS)){
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						outRecord.setField("RTN_CD", "1");						
					}
				}

				
		szMsg = "JSP-FACADE  [ 크레인상태관리 - 긴급작업 변경 ] 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		outRecord.setField("RTN_CD" 		, "1");	
		outRecord.setField("RTN_MSG" 		, szMsg);	
		return outRecord;
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}			
		
		

		
	} // end of crnChgSchPriorCoilRecord()

	
	
	
	/**
	 * 야드관리 > C열연 코일소재야드 > 야드현황관리 > 재료진도별재공현황  (저장물품목록)
	 * @ejb.interface-method
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @작성자 :박지열
	 * @작성일 : 2010.10.05
	 */
	public GridData getMtlProgStlList(GridData inDto) throws DAOException {
		//LOG
	
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		String szMethodName = "getMtlProgStlList";
		try{
			
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			gdRes = (GridData)ejbConn.trx("getMtlProgStlList", new Class[] { GridData.class }, new Object[] { inDto });

			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		return gdRes;
	} // end of getSpanMvList
	
	
	/**
	 * 야드관리 > C열연 코일소재야드 > 산적LOT관리 > 반입현황조회  (A,B열연 반입카운트 조회 )
	 * @ejb.interface-method
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @작성자 :박지열
	 * @작성일 : 2010.10.11
	 */
	public GridData getCarryBayCnt(GridData inDto) throws DAOException {
		//LOG
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		String szMethodName = "getCarryBayCnt";
		try{
			
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			gdRes = (GridData)ejbConn.trx("getCarryBayCnt", new Class[] { GridData.class }, new Object[] { inDto });
			
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		return gdRes;
	} // end of getSpanMvList
	
	
	/**
	 * 야드관리 > C열연 코일소재야드 > 산적LOT관리 > 반입현황조회  (대상재목록 조회 )
	 * @ejb.interface-method
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @작성자 :박지열
	 * @작성일 : 2010.10.11
	 */
	public GridData getCarryList(GridData inDto) throws DAOException {
		//LOG
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		String szMethodName = "getCarryList";
		try{
			
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			gdRes = (GridData)ejbConn.trx("getCarryList", new Class[] { GridData.class }, new Object[] { inDto });
			
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		return gdRes;
	} // end of getSpanMvList
	
	
	/**
	 * 크레인작업실적현황(야드관리 > 코일소재야드 > 크레인실적관리 > 크레인 작업실적현황)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCrnWrkWrStat(GridData inDto) throws JDTOException {
		String szOperationName	= "크레인작업실적현황";
		String szMethodName		= "getCrnWrkWrStat";
		String szMsg			= null;
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			
			szMsg = "JSP-FACADE ["+szOperationName+"]시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCrnWrkWrStat", inRecord);
			
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage(YdConstant.RETN_CD_SUCCESS);		
		
		szMsg = "JSP-FACADE ["+szOperationName+"]끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return gdRes;
	}
	
	/**
	 * 야드관리 > C열연 코일소재야드 > 이상재
	 * @ejb.interface-method
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @작성자 :박지열
	 * @작성일 : 2010.10.05
	 */
	public GridData getMtlErrorList(GridData inDto) throws DAOException {
		//LOG
	
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		String szMethodName		= "getMtlErrorList";
		try{
			
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			gdRes = (GridData)ejbConn.trx("getMtlErrorList", new Class[] { GridData.class }, new Object[] { inDto });

			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		return gdRes;
	} // end of getMtlErrorList
	
	
	
	/**
	 *  입출고 현황(C열연소재야드)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getCoilTotYdInOutList(GridData inDto) throws JDTOException {
	 
		String szMethodName = "getCoilTotYdInOutList";
		String szLogMsg = "";
		String szOperationName = "입출고 현황(C열연소재야드)";
		
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szLogMsg = "[Jsp Facade : " + szOperationName + "] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilTotYdInOutList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return gdRes;
	} //end of getCoilTotYdInOutList
	
	
	/**
	 *  입출고 현황(B열연소재야드)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getCoilTotYdInOutListB(GridData inDto) throws JDTOException {
	 
		String szMethodName = "getCoilTotYdInOutList";
		String szLogMsg = "";
		String szOperationName = "입출고 현황(B열연소재야드)";
		
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szLogMsg = "[Jsp Facade : " + szOperationName + "] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilTotYdInOutListB", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return gdRes;
	} //end of getCoilTotYdInOutListB
	
	/**
	 *  입출고 현황(C열연소재야드)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getCoilTotYdInOutListDong(GridData inDto) throws JDTOException {
	 
		String szMethodName = "getCoilTotYdInOutListDong";
		String szLogMsg = "";
		String szOperationName = "입출고 현황(C열연소재야드)";
		
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szLogMsg = "[Jsp Facade : " + szOperationName + "] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilTotYdInOutListDong", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return gdRes;
	} //end of getCoilTotYdInOutListDong
	
	/**
	 *  입출고 현황(B열연소재야드)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getCoilTotYdInOutListDongB(GridData inDto) throws JDTOException {
	 
		String szMethodName = "getCoilTotYdInOutListDongB";
		String szLogMsg = "";
		String szOperationName = "입출고 현황(B열연소재야드)";
		
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szLogMsg = "[Jsp Facade : " + szOperationName + "] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilTotYdInOutListDongB", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return gdRes;
	} //end of getCoilTotYdInOutListDongB
	
	
	/**
	 *  야드관리 > 코일소재야드 > 재공관리 > 열단위이적등록/차량이적등록  (이적지시)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updColUnitCarMvstkReg(GridData gdReq) throws DAOException {
		
		YdStockDao ydStockDao = new YdStockDao();
		YdDelegate ydDelegate 	= new YdDelegate();
		YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();//작업예약 DAO
		String szMsg="";
		String szMethodName="updColUnitCarMvstkReg";
		String szOperationName	= "소재창고 차량이적등록";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		String sRTN_CD		= "";
		String sRTN_MSG		= "";
		String szTemp   	= "";
		String szToBayGp 	= "";
		String szTCar 		= "";
		String szYD_SCH_CD 	= "";
		String szYD_TO_LOC_GUIDE  = "";
		String sYD_USER_ID  	= "";
		String sUP_POS 			= "";
		String sYD_WBOOK_ID 	= "";
		String sFirstYD_WBOOK_ID     = "";
		int intStlSchCnt        = 0;
		int intStlCnt         	= 0;
		JDTORecord outRecord    = JDTORecordFactory.getInstance().create();
		JDTORecord outRecord1   = JDTORecordFactory.getInstance().create();
		JDTORecord outRecord2   = JDTORecordFactory.getInstance().create();
		JDTORecord outRecord21  = JDTORecordFactory.getInstance().create();
		JDTORecord recInTemp   	= JDTORecordFactory.getInstance().create();
		JDTORecord inRecord2 	= JDTORecordFactory.getInstance().create();
		JDTORecord inRecord1 	= JDTORecordFactory.getInstance().create();
		JDTORecord inRecord     = JDTORecordFactory.getInstance().create();
		JDTORecordSet rsResult2 = null; 
		String sYD_GP			= "";
		String sYD_TCAR_SCH_ID  = "";
		String sYD_AIM_BAY_GP	= "";
		String sSND_FLAG		= "N";
		String sCAR_NO			= "";
		String szCarCnt			= "";
		Calendar today			= Calendar.getInstance();
		Date	date			= today.getTime();
		SimpleDateFormat sdf	= new SimpleDateFormat("yyyyMMdd");
		String sDate	= sdf.format(date);
		String sSecondYD_WBOOK_ID ="";
		String sThirdYD_WBOOK_ID ="";
		int intRtnVal        	= 0;
		int Loopi = 0;
		try{
			szMsg = "[JSP-FACADE  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			inRecord1 = CmUtil.genJDTORecord(gdReq);
			JDTORecord [] inRecordSet =  ydComUtil.genGridToJDTORecordAll(gdReq);
			
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			 
			szTemp 		=  ydDaoUtils.paraRecChkNull(inRecordSet[0], "FROMLOC");
			sYD_USER_ID =  ydDaoUtils.paraRecChkNull(inRecord1, "YD_USER_ID"); 
			szYD_SCH_CD =  ydDaoUtils.paraRecChkNull(inRecord1, "FRM_YD_SCH_CD"); 
			szToBayGp	=  ydDaoUtils.paraRecChkNull(inRecord1, "TO_YD_BAY_GP"); 
			szCarCnt	=  ydDaoUtils.paraRecChkNull(inRecord1, "T_CNT");
			
			//차량번호 할당
			if(szYD_SCH_CD.substring(5, 6).equals("5")){
				sCAR_NO ="5555";
			}else if(szYD_SCH_CD.substring(5, 6).equals("6")){
				sCAR_NO ="6666";
			}
			
			if(szTemp.equals("")){
				gdRes.setMessage("FROM 적치정보가 없습니다!!");		
				m_ctx.setRollbackOnly();
				
			} else { 
				szTCar = ydDaoUtils.paraRecChkNull(inRecord1, "CAR_NO");
			}	
				//------------------------------------------------------------------------------------------------
//				// 2010.06.25 추가
//				// 대상 위치 폭구분 체크
//				// TO위치의 폭구분이 다를경우 이동 불가
//				JDTORecord [] inRcd =  new JDTORecord[1];
//				inRcd[0] = JDTORecordFactory.getInstance().create();
//				
//				inRcd[0].setField("FROM_YD_STK_COL_GP", szTemp.substring(0, 6));
//				inRcd[0].setField("TO_YD_STK_COL_GP", szToColGp);
//				inRcd[0].setField("YD_WRK_PLAN_TCAR", szTCar); // 대차
//				
//				ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
//				outRecord1 = (JDTORecord)ejbConn.trx("getStkColWidthGp", new Class[] { JDTORecord[].class }, new Object[] { inRcd });
//				sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
//				sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
//				if (!sRTN_CD.equals("1")) {
//					gdRes.setMessage(sRTN_MSG);		
//					m_ctx.setRollbackOnly();
//					return gdRes;
//				} 
				//------------------------------------------------------------------------------------------------
				
				
				//szYD_TO_LOC_GUIDE = szToColGp ;
				
//				2단  처리				
				for( Loopi = 0; Loopi < inRecordSet.length; Loopi++) {
					//재료번호
					//STL_NO []
					sUP_POS =  ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "UP_POS");
					if(sUP_POS.substring(5, 6).equals("2")) {
						intStlCnt++;
						inRecord = JDTORecordFactory.getInstance().create();
						inRecord.setField("YD_SCH_CD",          		szYD_SCH_CD);//스케줄코드
						inRecord.setField("STL_SH",      				"1");  //LINE_IN 재료매수
						inRecord.setField("STL_NO1", 	  				ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "STL_NO"));
						inRecord.setField("YD_TO_LOC_DCSN_MTD",			"S");
								
						inRecord.setField("TO_YD_STK_BED_NO",			szYD_TO_LOC_GUIDE);
						inRecord.setField("YD_UP_COLL_SEQ1", 			"1");  //권상모음순서
						inRecord.setField("YD_WRK_PLAN_TCAR", 			szTCar);  
						inRecord.setField("YD_USER_ID", 				sYD_USER_ID);   
						inRecord.setField("CARD_NO", 					sCAR_NO);  //차량번호
						inRecord.setField("YD_AIM_BAY_GP", 				szToBayGp);
		
// 작업예약 등록 호출
//대처						this.procWrkBook(recOutPara);
//YD_SCH_CD:스케줄코드,
//STL_SH: 재료매수,
//YD_TO_LOC_DCSN_MTD(TO위치 결정 방법)
//STL_NO(재료번호1,2,3,....)
//FR_YD_STK_BED_NO(적치배드)
//TO_YD_STK_BED_NO(가이드가 됨)
		
						ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
						outRecord = (JDTORecord)ejbConn.trx("WrkBookInsertProc", new Class[] { JDTORecord.class }, new Object[] { inRecord });
						sRTN_CD		= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
						sRTN_MSG	= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
						sYD_WBOOK_ID= StringHelper.evl(outRecord.getFieldString("YD_WBOOK_ID"), "");
						
						
						if ("0".equals(sRTN_CD)) {
							szMsg = "작업예약 등록시 ERROR";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							gdRes.setMessage(sRTN_MSG);		
							return gdRes;
						} else {
							intStlSchCnt++;
							
							if((intStlSchCnt == 1) && sFirstYD_WBOOK_ID.equals("")) {
								sFirstYD_WBOOK_ID = sYD_WBOOK_ID;
							}							
							
							if((intStlSchCnt == 2) && sSecondYD_WBOOK_ID.equals("") && (szCarCnt.equals("2")||szCarCnt.equals("3"))) {
								sSecondYD_WBOOK_ID = sYD_WBOOK_ID;
							}
							
							if((intStlSchCnt == 3) && sThirdYD_WBOOK_ID.equals("")  && szCarCnt.equals("3")) {
								sThirdYD_WBOOK_ID = sYD_WBOOK_ID;
							}

							gdRes.setMessage(sRTN_MSG + " <br> 정상적으로 작업예약까지 등록했습니다.");		
							//gdRes.setMessage("정상적으로 작업예약까지 등록했습니다.");			
						}	
						

						
						inRecord = JDTORecordFactory.getInstance().create();
						inRecord.setField("STL_NO", ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "STL_NO"));
						inRecord.setField("YD_AIM_YD_GP",  szYD_SCH_CD.substring(0, 1));
						inRecord.setField("YD_AIM_BAY_GP", szToBayGp);
						inRecord.setField("YD_AIM_RT_GP", "B3");
						inRecord.setField("TRANS_ORD_DATE", sDate);
						inRecord.setField("TRANS_ORD_SEQNO", sCAR_NO);
						inRecord.setField("CARD_NO", sCAR_NO);
						inRecord.setField("SAILNO", szCarCnt);
						
						intRtnVal = ydStockDao.updYdStock(inRecord, 0);
						
						if (intRtnVal < 0) {
							if (intRtnVal == -1) {
								szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							} else {
								szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							}
						}
					}
					}
					
//					1단  처리				
					for( Loopi = 0; Loopi < inRecordSet.length; Loopi++) {
						//재료번호
						//STL_NO []
						sUP_POS =  ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "UP_POS"); 
					 
						if(sUP_POS.substring(5, 6).equals("1")) {
						intStlCnt++;
						inRecord = JDTORecordFactory.getInstance().create();
						inRecord.setField("YD_SCH_CD",          		szYD_SCH_CD);//스케줄코드
						inRecord.setField("STL_SH",      				"1");  //LINE_IN 재료매수
						inRecord.setField("STL_NO1", 	  				ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "STL_NO"));
						inRecord.setField("YD_TO_LOC_DCSN_MTD",			"S");
								
						inRecord.setField("TO_YD_STK_BED_NO",			szYD_TO_LOC_GUIDE);
						inRecord.setField("YD_UP_COLL_SEQ1", 			"1");  //권상모음순서
						inRecord.setField("YD_WRK_PLAN_TCAR", 			szTCar);
						inRecord.setField("YD_USER_ID", 				sYD_USER_ID);  //사용자
						inRecord.setField("CARD_NO", 					sCAR_NO);  //차량번호
						inRecord.setField("YD_AIM_BAY_GP", 				szToBayGp);
//				 작업예약 등록 호출
		//YD_SCH_CD:스케줄코드,
		//STL_SH: 재료매수,
		//YD_TO_LOC_DCSN_MTD(TO위치 결정 방법)
		//STL_NO(재료번호1,2,3,....)
		//FR_YD_STK_BED_NO(적치배드)
		//TO_YD_STK_BED_NO(가이드가 됨)
		
						ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
						outRecord = (JDTORecord)ejbConn.trx("WrkBookInsertProc", new Class[] { JDTORecord.class }, new Object[] { inRecord });
						sRTN_CD		= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
						sRTN_MSG	= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
						sYD_WBOOK_ID= StringHelper.evl(outRecord.getFieldString("YD_WBOOK_ID"), "");
						if ("0".equals(sRTN_CD)) {
							szMsg = "작업예약 등록시 ERROR";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							gdRes.setMessage(sRTN_MSG);		
							return gdRes;
						} else {
							
							intStlSchCnt++;
							
							if((intStlSchCnt == 1) && sFirstYD_WBOOK_ID.equals("")) {
								sFirstYD_WBOOK_ID = sYD_WBOOK_ID;
							}							
							
							if((intStlSchCnt == 2) && sSecondYD_WBOOK_ID.equals("") && (szCarCnt.equals("2")||szCarCnt.equals("3"))) {
								sSecondYD_WBOOK_ID = sYD_WBOOK_ID;
							}
							
							if((intStlSchCnt == 3) && sThirdYD_WBOOK_ID.equals("")  && szCarCnt.equals("3")) {
								sThirdYD_WBOOK_ID = sYD_WBOOK_ID;
							}
							
							

							gdRes.setMessage(sRTN_MSG + " <br> 정상적으로 작업예약까지 등록했습니다.");		
							//gdRes.setMessage("정상적으로 작업예약까지 등록했습니다.");			
						}	
						
						inRecord = JDTORecordFactory.getInstance().create();
						inRecord.setField("STL_NO", ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "STL_NO"));
						inRecord.setField("YD_AIM_YD_GP",  szYD_SCH_CD.substring(0, 1));
						inRecord.setField("YD_AIM_RT_GP", "B3");	
						inRecord.setField("YD_AIM_BAY_GP", szToBayGp);
						inRecord.setField("TRANS_ORD_DATE", sDate);
						inRecord.setField("TRANS_ORD_SEQNO", sCAR_NO);
						inRecord.setField("CARD_NO", sCAR_NO);
						inRecord.setField("SAILNO", szCarCnt);
						intRtnVal = ydStockDao.updYdStock(inRecord, 0);
						
						if (intRtnVal < 0) {
							if (intRtnVal == -1) {
								szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							} else {
								szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							}
						}
						}
				}
		
				
			if(!sFirstYD_WBOOK_ID.equals("")) {
				//재료번호
				rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
				inRecord = JDTORecordFactory.getInstance().create();
				inRecord.setField("YD_SCH_CD",          	szYD_SCH_CD);//스케줄코드
				/*com.inisteel.cim.yd.dao.ydWrkbookDao.getWorkTbRefNONESchCd*/
				intRtnVal = ydWrkbookDao.getYdWrkbook(inRecord, rsResult2, 502);
		    	if(intRtnVal <= 0) {
					JDTORecord[] inRecordarr   	= null;
					inRecordarr = new JDTORecord[1];
					
					inRecordarr[0] = JDTORecordFactory.getInstance().create();
					inRecordarr[0].setField("YD_SCH_CD"		, szYD_SCH_CD); 
					inRecordarr[0].setField("YD_WBOOK_ID"	, sFirstYD_WBOOK_ID); 
					
					ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
					outRecord21 = (JDTORecord) ejbConn.trx("trxRunSchedule", new Class[] { JDTORecord[].class },	new Object[] { inRecordarr });
		
					sRTN_CD		= StringHelper.evl(outRecord21.getFieldString("RTN_CD"), "0");
					sRTN_MSG	= StringHelper.evl(outRecord21.getFieldString("RTN_MSG"), "");
					ydUtils.putLog(szSessionName, szMethodName, "//" + sRTN_MSG, YdConstant.DEBUG);
					if (!("1".equals(sRTN_CD))) {
						gdRes.setMessage(sRTN_MSG);		
					} else {
		
						gdRes.setMessage(sRTN_MSG + " <br> 정상적으로 스케쥴까지 등록했습니다.");		
			
					} 	
		    	}	
			}
			

			if(!sSecondYD_WBOOK_ID.equals("")) {

					JDTORecord[] inRecordarr   	= null;
					inRecordarr = new JDTORecord[1];
					
					inRecordarr[0] = JDTORecordFactory.getInstance().create();
					inRecordarr[0].setField("YD_SCH_CD"		, szYD_SCH_CD); 
					inRecordarr[0].setField("YD_WBOOK_ID"	, sSecondYD_WBOOK_ID); 
					
					ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
					outRecord21 = (JDTORecord) ejbConn.trx("trxRunSchedule", new Class[] { JDTORecord[].class },	new Object[] { inRecordarr });
		
					sRTN_CD		= StringHelper.evl(outRecord21.getFieldString("RTN_CD"), "0");
					sRTN_MSG	= StringHelper.evl(outRecord21.getFieldString("RTN_MSG"), "");
					ydUtils.putLog(szSessionName, szMethodName, "//" + sRTN_MSG, YdConstant.DEBUG);
					if (!("1".equals(sRTN_CD))) {
						gdRes.setMessage(sRTN_MSG);		
					} else {
		
						gdRes.setMessage(sRTN_MSG + " <br> 정상적으로 스케쥴까지 등록했습니다.");		
			
					} 	

			}
			
			
			if(!sThirdYD_WBOOK_ID.equals("")) {

				JDTORecord[] inRecordarr   	= null;
				inRecordarr = new JDTORecord[1];
				
				inRecordarr[0] = JDTORecordFactory.getInstance().create();
				inRecordarr[0].setField("YD_SCH_CD"		, szYD_SCH_CD); 
				inRecordarr[0].setField("YD_WBOOK_ID"	, sThirdYD_WBOOK_ID); 
				
				ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
				outRecord21 = (JDTORecord) ejbConn.trx("trxRunSchedule", new Class[] { JDTORecord[].class },	new Object[] { inRecordarr });
	
				sRTN_CD		= StringHelper.evl(outRecord21.getFieldString("RTN_CD"), "0");
				sRTN_MSG	= StringHelper.evl(outRecord21.getFieldString("RTN_MSG"), "");
				ydUtils.putLog(szSessionName, szMethodName, "//" + sRTN_MSG, YdConstant.DEBUG);
				if (!("1".equals(sRTN_CD))) {
					gdRes.setMessage(sRTN_MSG);		
				} else {
	
					gdRes.setMessage(sRTN_MSG + " <br> 정상적으로 스케쥴까지 등록했습니다.");		
		
				} 	

		}
			
			
				
				
			szMsg = "다수코일  작업예약  완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);					
			
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		return gdRes;
	} // end of updColUnitCarMvstkReg

	
	
   /**
	 *  야드관리 > 코일소재야드 > 재공관리 > 열단위이적등록/차량이적등록  (제품도이적지시)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updColUnitCarMvstkRegNew(GridData gdReq) throws DAOException {
		
		YdStockDao ydStockDao = new YdStockDao();
		YdDelegate ydDelegate 	= new YdDelegate();
		YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();//작업예약 DAO
		YdSchRuleDao ydSchRuleDao 		= new YdSchRuleDao();
		String szMsg="";
		String szMethodName="updColUnitCarMvstkRegNew";
		String szOperationName	= "소재/제품창고 차량이적등록";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		String sRTN_CD		= "";
		String sRTN_MSG		= "";
		String szTemp   	= "";
		String szToBayGp 	= "";
		String szTCar 		= "";
		String szYD_SCH_CD 	= "";
		String szYD_TO_LOC_GUIDE  = "";
		String sYD_USER_ID  	= "";
		String sUP_POS 			= "";
		String sYD_WBOOK_ID 	= "";
		String sFirstYD_WBOOK_ID     = "";
		int intStlSchCnt        = 0;
		int intStlCnt         	= 0;
		JDTORecord outRecord    = JDTORecordFactory.getInstance().create();
		JDTORecord outRecord1   = JDTORecordFactory.getInstance().create();
		JDTORecord outRecord2   = JDTORecordFactory.getInstance().create();
		JDTORecord outRecord7   = JDTORecordFactory.getInstance().create();
		JDTORecord outRecord21  = JDTORecordFactory.getInstance().create();
		JDTORecord recInTemp   	= JDTORecordFactory.getInstance().create();
		JDTORecord inRecord2 	= JDTORecordFactory.getInstance().create();
		JDTORecord inRecord1 	= JDTORecordFactory.getInstance().create();
		JDTORecord inRecord     = JDTORecordFactory.getInstance().create();
		JDTORecord recPara     = JDTORecordFactory.getInstance().create();
		
		JDTORecordSet rsResult = null;
		JDTORecordSet rsResult2 = null; 
		String sYD_GP			= "";
		String sYD_TCAR_SCH_ID  = "";
		String sYD_AIM_BAY_GP	= "";
		String sSND_FLAG		= "N";
		String sCAR_NO			= "";
		String szCarCnt			= "";
		String szWBOOK_CNT_YN   = "";
		Calendar today			= Calendar.getInstance();
		Date	date			= today.getTime();
		SimpleDateFormat sdf	= new SimpleDateFormat("yyyyMMdd");
		String sDate	= sdf.format(date);
		String sSecondYD_WBOOK_ID 	= "";
		String sThirdYD_WBOOK_ID	= "";
		String szToDir        	= "";
		String szYD_WRK_CRN   	= "";
		
		int intRtnVal        	= 0;
		int Loopi = 0;
		try{
			szMsg = "[JSP-FACADE  " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			inRecord1 = CmUtil.genJDTORecord(gdReq);
			JDTORecord [] inRecordSet =  ydComUtil.genGridToJDTORecordAll(gdReq);
			
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			 
			szTemp 		=  ydDaoUtils.paraRecChkNull(inRecordSet[0], "FROMLOC");
			sYD_USER_ID =  ydDaoUtils.paraRecChkNull(inRecord1, "YD_USER_ID"); 
			szYD_SCH_CD =  ydDaoUtils.paraRecChkNull(inRecord1, "FRM_YD_SCH_CD"); 
			szToBayGp	=  ydDaoUtils.paraRecChkNull(inRecord1, "TO_YD_BAY_GP"); 
			szCarCnt	=  ydDaoUtils.paraRecChkNull(inRecord1, "T_CNT");
			szToDir		=  ydDaoUtils.paraRecChkNull(inRecord1, "TO_DIR");
			
			//차량번호 할당
//			if(szYD_SCH_CD.substring(5, 6).equals("5")||szYD_SCH_CD.substring(5, 6).equals("7")){
//				
//				if(szYD_SCH_CD.substring(5, 6).equals("7")){
//					sCAR_NO ="7777";
//				}else{
//					sCAR_NO ="5555";
//				}
//			}else if(szYD_SCH_CD.substring(5, 6).equals("6")||szYD_SCH_CD.substring(5, 6).equals("8")){
//				
//				if(szYD_SCH_CD.substring(5, 6).equals("8")){
//					sCAR_NO ="8888";
//				}else{
//					sCAR_NO ="6666";
//				}
//			}
			
			sCAR_NO = szYD_SCH_CD.substring(5, 6)+szYD_SCH_CD.substring(5, 6)+szYD_SCH_CD.substring(5, 6)+szYD_SCH_CD.substring(5, 6);
			
			if(szTemp.equals("")){
				gdRes.setMessage("FROM 적치정보가 없습니다!!");		
				m_ctx.setRollbackOnly();
				return gdRes;
			} else { 
				szTCar = ydDaoUtils.paraRecChkNull(inRecord1, "CAR_NO");
			}	
			//------------------------------------------------------------------------------------------------------------
			//	신 차량이적 적용
			//------------------------------------------------------------------------------------------------------------
			JDTORecordSet 	outResult9  = JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord 		inRecord9 	= JDTORecordFactory.getInstance().create();
			JDTORecord 		outRecord8  = JDTORecordFactory.getInstance().create();
			YdEqpDao ydEqpDao     = new YdEqpDao();
			String szAPPLY_YN9  = "N";
			inRecord9.setField("REPR_CD_GP", "H00020");
			
			/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN*/
			intRtnVal = ydEqpDao.getYdEqp(inRecord9, outResult9, 999);
			if(intRtnVal > 0) {
				outResult9.first();
				outRecord8  = outResult9.getRecord();
				szAPPLY_YN9 = outRecord8.getFieldString("ITEM1");				
			}
			ydUtils.putLog(szSessionName, szMethodName, "신 차량이적 처리 적용여부 " + szAPPLY_YN9, YdConstant.DEBUG);

			if(szAPPLY_YN9.equals("Y")) {
				rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
				inRecord = JDTORecordFactory.getInstance().create();
				inRecord.setField("CARD_NO",          	sCAR_NO);		//차량번호
				inRecord.setField("YD_SCH_CD",         	szYD_SCH_CD);	//스케줄코드
				inRecord.setField("YD_AIM_BAY_GP",     	szToBayGp);		//목적동
				
				/*com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookCntCarCoil*/
				intRtnVal = ydWrkbookDao.getYdWrkbook(inRecord, rsResult2, 506);
		    	if(intRtnVal <= 0) {
	
		    		gdRes.setMessage("작업예약 read error");		
					m_ctx.setRollbackOnly();
					return gdRes;
		    	} else {
		    		
		    		rsResult2.first();
					outRecord7  = rsResult2.getRecord();
					szWBOOK_CNT_YN = outRecord7.getFieldString("WBOOK_CNT_YN");				
					if(szWBOOK_CNT_YN.equals("N")) {
					} else {	
			    		gdRes.setMessage("해당차량으로 타동(" + szWBOOK_CNT_YN+")으로  편성된 작업예약정보가 있습니다.<br> " +
			    				"작업완료후 처리하세요");		
						m_ctx.setRollbackOnly();
						return gdRes;
					}
		    	}
			}	
			
//2단  처리				
			for( Loopi = 0; Loopi < inRecordSet.length; Loopi++) {
				//재료번호
				//STL_NO []
				sUP_POS =  ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "UP_POS");
				if(sUP_POS.substring(5, 6).equals("2")) {
					intStlCnt++;
					inRecord = JDTORecordFactory.getInstance().create();
					inRecord.setField("YD_SCH_CD",          		szYD_SCH_CD);//스케줄코드
					inRecord.setField("STL_SH",      				"1");  //LINE_IN 재료매수
					inRecord.setField("STL_NO1", 	  				ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "STL_NO"));
					inRecord.setField("YD_TO_LOC_DCSN_MTD",			"S");
							
					inRecord.setField("TO_YD_STK_BED_NO",			szYD_TO_LOC_GUIDE);
					inRecord.setField("YD_UP_COLL_SEQ1", 			"1");  //권상모음순서
					inRecord.setField("YD_WRK_PLAN_TCAR", 			szTCar);  
					inRecord.setField("YD_USER_ID", 				sYD_USER_ID);   
					inRecord.setField("CARD_NO", 					sCAR_NO);  //차량번호
					inRecord.setField("CAR_NO", 					sCAR_NO);  //차량번호
					inRecord.setField("YD_AIM_BAY_GP", 				szToBayGp);  
					inRecord.setField("YD_CAR_USE_GP", 				YdConstant.YD_CAR_USE_GP_DM);
					inRecord.setField("DIST_SHIPASSIGN_GP", 		szToDir);  
					
					ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
					outRecord = (JDTORecord)ejbConn.trx("WrkBookInsertProc", new Class[] { JDTORecord.class }, new Object[] { inRecord });
					sRTN_CD		= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
					sRTN_MSG	= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
					sYD_WBOOK_ID= StringHelper.evl(outRecord.getFieldString("YD_WBOOK_ID"), "");
					
					if ("0".equals(sRTN_CD)) {
						szMsg = "작업예약 등록시 ERROR";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						gdRes.setMessage(sRTN_MSG);		
						return gdRes;
					} else {
						intStlSchCnt++;
						
						if((intStlSchCnt == 1) && sFirstYD_WBOOK_ID.equals("")) {
							sFirstYD_WBOOK_ID = sYD_WBOOK_ID;
						}							
						
						if((intStlSchCnt == 2) && sSecondYD_WBOOK_ID.equals("") && (szCarCnt.equals("2")||szCarCnt.equals("3"))) {
							sSecondYD_WBOOK_ID = sYD_WBOOK_ID;
						}
						
						if((intStlSchCnt == 3) && sThirdYD_WBOOK_ID.equals("")  && szCarCnt.equals("3")) {
							sThirdYD_WBOOK_ID = sYD_WBOOK_ID;
						}

						gdRes.setMessage(sRTN_MSG + " <br> 정상적으로 작업예약까지 등록했습니다.");		
						//gdRes.setMessage("정상적으로 작업예약까지 등록했습니다.");			
					}	
					

					if(szAPPLY_YN9.equals("Y")){
						
						inRecord = JDTORecordFactory.getInstance().create();
						inRecord.setField("STL_NO"			, ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "STL_NO"));
						inRecord.setField("YD_AIM_YD_GP"	, szYD_SCH_CD.substring(0, 1));
						inRecord.setField("YD_AIM_RT_GP"	, "B3");	
						inRecord.setField("YD_AIM_BAY_GP"	, szToBayGp);
						inRecord.setField("COIL_CAR_LOTID"	, "Y");
						inRecord.setField("COIL_CAR_NO"		, sCAR_NO);
						inRecord.setField("SAILNO"			, szCarCnt);
						
						/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updYdStockCoilCarLotId*/
						intRtnVal = ydStockDao.updYdStock_COIL_LOTID(inRecord);
	
					} else {
						inRecord = JDTORecordFactory.getInstance().create();
						inRecord.setField("STL_NO", ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "STL_NO"));
						inRecord.setField("YD_AIM_YD_GP"	, szYD_SCH_CD.substring(0, 1));
						inRecord.setField("YD_AIM_RT_GP"	, "B3");	
						inRecord.setField("YD_AIM_BAY_GP"	, szToBayGp);
						inRecord.setField("TRANS_ORD_DATE"	, sDate);
						inRecord.setField("TRANS_ORD_SEQNO"	, sCAR_NO);
						inRecord.setField("CARD_NO"			, sCAR_NO);
						inRecord.setField("SAILNO"			, szCarCnt);
						intRtnVal = ydStockDao.updYdStock(inRecord, 0);
					}	
					
					if (intRtnVal < 0) {
						if (intRtnVal == -1) {
							szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						} else {
							szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						}
					}
				}
			}
					
//	1단  처리				
			for( Loopi = 0; Loopi < inRecordSet.length; Loopi++) {
				//재료번호
				//STL_NO []
				sUP_POS =  ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "UP_POS"); 
			 
				if(sUP_POS.substring(5, 6).equals("1")) {
					intStlCnt++;
					inRecord = JDTORecordFactory.getInstance().create();
					inRecord.setField("YD_SCH_CD",          		szYD_SCH_CD);//스케줄코드
					inRecord.setField("STL_SH",      				"1");  //LINE_IN 재료매수
					inRecord.setField("STL_NO1", 	  				ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "STL_NO"));
					inRecord.setField("YD_TO_LOC_DCSN_MTD",			"S");
							
					inRecord.setField("TO_YD_STK_BED_NO",			szYD_TO_LOC_GUIDE);
					inRecord.setField("YD_UP_COLL_SEQ1", 			"1");  //권상모음순서
					inRecord.setField("YD_WRK_PLAN_TCAR", 			szTCar);
					inRecord.setField("YD_USER_ID", 				sYD_USER_ID);  //사용자
					inRecord.setField("CARD_NO", 					sCAR_NO);  //차량번호
					inRecord.setField("CAR_NO", 					sCAR_NO);  //차량번호
					inRecord.setField("YD_AIM_BAY_GP", 				szToBayGp);  
					inRecord.setField("YD_CAR_USE_GP", 				YdConstant.YD_CAR_USE_GP_DM);
					inRecord.setField("DIST_SHIPASSIGN_GP", 		szToDir);   
	
					ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
					outRecord = (JDTORecord)ejbConn.trx("WrkBookInsertProc", new Class[] { JDTORecord.class }, new Object[] { inRecord });
					sRTN_CD		= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
					sRTN_MSG	= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
					sYD_WBOOK_ID= StringHelper.evl(outRecord.getFieldString("YD_WBOOK_ID"), "");
					if ("0".equals(sRTN_CD)) {
						szMsg = "작업예약 등록시 ERROR";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						gdRes.setMessage(sRTN_MSG);		
						return gdRes;
					} else {
						
						intStlSchCnt++;
						
						if((intStlSchCnt == 1) && sFirstYD_WBOOK_ID.equals("")) {
							sFirstYD_WBOOK_ID = sYD_WBOOK_ID;
						}							
						
						if((intStlSchCnt == 2) && sSecondYD_WBOOK_ID.equals("") && (szCarCnt.equals("2")||szCarCnt.equals("3"))) {
							sSecondYD_WBOOK_ID = sYD_WBOOK_ID;
						}
						
						if((intStlSchCnt == 3) && sThirdYD_WBOOK_ID.equals("")  && szCarCnt.equals("3")) {
							sThirdYD_WBOOK_ID = sYD_WBOOK_ID;
						}
						
						gdRes.setMessage(sRTN_MSG + " <br> 정상적으로 작업예약까지 등록했습니다.");		
						//gdRes.setMessage("정상적으로 작업예약까지 등록했습니다.");			
					}	
	
					
					if(szAPPLY_YN9.equals("Y")){
						
						inRecord = JDTORecordFactory.getInstance().create();
						inRecord.setField("STL_NO"			, ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "STL_NO"));
						inRecord.setField("YD_AIM_YD_GP"	, szYD_SCH_CD.substring(0, 1));
						inRecord.setField("YD_AIM_RT_GP"	, "B3");	
						inRecord.setField("YD_AIM_BAY_GP"	, szToBayGp);
						inRecord.setField("COIL_CAR_LOTID"	, "Y");
						inRecord.setField("COIL_CAR_NO"		, sCAR_NO);
						inRecord.setField("SAILNO"			, szCarCnt);
						
						/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updYdStockCoilCarLotId*/
						intRtnVal = ydStockDao.updYdStock_COIL_LOTID(inRecord);
	
					} else {
						inRecord = JDTORecordFactory.getInstance().create();
						inRecord.setField("STL_NO", ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "STL_NO"));
						inRecord.setField("YD_AIM_YD_GP"	, szYD_SCH_CD.substring(0, 1));
						inRecord.setField("YD_AIM_RT_GP"	, "B3");	
						inRecord.setField("YD_AIM_BAY_GP"	, szToBayGp);
						inRecord.setField("TRANS_ORD_DATE"	, sDate);
						inRecord.setField("TRANS_ORD_SEQNO"	, sCAR_NO);
						inRecord.setField("CARD_NO"			, sCAR_NO);
						inRecord.setField("SAILNO"			, szCarCnt);
						intRtnVal = ydStockDao.updYdStock(inRecord, 0);
					}	
					
					if (intRtnVal < 0) {
						if (intRtnVal == -1) {
							szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						} else {
							szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						}
					}
				}
			}
				

//			151022 hun 차량 동간 이적 무인화 적용
			
			//스케줄코드로 스케줄기준 Table 조회해서 작업 크레인ID SELECT
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			inRecord = JDTORecordFactory.getInstance().create();
			inRecord.setField("YD_SCH_CD"	, szYD_SCH_CD);
			intRtnVal = ydSchRuleDao.getYdSchrule(inRecord, rsResult, 0);

			//리턴값 메세지처리
			if(intRtnVal != 1) {
				szMsg = "스케줄코드(" + szYD_SCH_CD + ")에 대한 스케줄기준 데이터가 이상합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}	
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			//스케줄CD 체크
			szYD_WRK_CRN       = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");   	//작업크레인
			ydUtils.putLog(szSessionName, szMethodName, "작업 진행 EQP_ID =" + szYD_WRK_CRN, YdConstant.DEBUG);
				
			// 151028 hun 무인화 적용 제품야드만 해당
//			if( false ){
			if( "J".equals(szYD_SCH_CD.substring(0, 1)) ){
				ydUtils.putLog(szSessionName, szMethodName, "제품야드 경우 작업예약편성 후 차량도착 TC올때까지 대기" + szYD_WRK_CRN, YdConstant.DEBUG);
					
			}else {
			
				//스케줄 호출 (상차 가능 매수 만큼 스케줄 기능)
				//재료번호
				rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
				inRecord = JDTORecordFactory.getInstance().create();
				inRecord.setField("YD_SCH_CD",          	szYD_SCH_CD);//스케줄코드
				/*com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookBySchCd*/
				intRtnVal = ydWrkbookDao.getYdWrkbook(inRecord, rsResult2, 8);
				
				
				if(rsResult2.size()>0){
				
					// 상차 가능 매수  수만큼 루프
					for(int Loop_i = 1; Loop_i <= Integer.parseInt(szCarCnt) ; Loop_i++) {
						rsResult2.absolute(Loop_i);
						inRecord = rsResult2.getRecord();
						
						JDTORecord[] inRecordarr   	= null;
						inRecordarr = new JDTORecord[1];
						
						inRecordarr[0] = JDTORecordFactory.getInstance().create();
						inRecordarr[0].setField("YD_SCH_CD"		, szYD_SCH_CD); 
						inRecordarr[0].setField("YD_WBOOK_ID"	, ydDaoUtils.paraRecChkNull(inRecord,"YD_WBOOK_ID")); 
						
						ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
						outRecord21 = (JDTORecord) ejbConn.trx("trxRunSchedule", new Class[] { JDTORecord[].class },	new Object[] { inRecordarr });
			
						sRTN_CD		= StringHelper.evl(outRecord21.getFieldString("RTN_CD"), "0");
						sRTN_MSG	= StringHelper.evl(outRecord21.getFieldString("RTN_MSG"), "");
						ydUtils.putLog(szSessionName, szMethodName, "//" + sRTN_MSG, YdConstant.DEBUG);
						if (!("1".equals(sRTN_CD))) {
							gdRes.setMessage(sRTN_MSG);		
						} else {
			
							gdRes.setMessage(sRTN_MSG + " <br> 정상적으로 스케쥴까지 등록했습니다.");		
				
						} 
					}
				}
			}
			
				
//			if(!sFirstYD_WBOOK_ID.equals("")) {
//				//재료번호
//				rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
//				inRecord = JDTORecordFactory.getInstance().create();
//				inRecord.setField("YD_SCH_CD",          	szYD_SCH_CD);//스케줄코드
//				/*com.inisteel.cim.yd.dao.ydWrkbookDao.getWorkTbRefNONESchCd*/
//				intRtnVal = ydWrkbookDao.getYdWrkbook(inRecord, rsResult2, 502);
//		    	if(intRtnVal <= 0) {
//					JDTORecord[] inRecordarr   	= null;
//					inRecordarr = new JDTORecord[1];
//					
//					inRecordarr[0] = JDTORecordFactory.getInstance().create();
//					inRecordarr[0].setField("YD_SCH_CD"		, szYD_SCH_CD); 
//					inRecordarr[0].setField("YD_WBOOK_ID"	, sFirstYD_WBOOK_ID); 
//					
//					ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
//					outRecord21 = (JDTORecord) ejbConn.trx("trxRunSchedule", new Class[] { JDTORecord[].class },	new Object[] { inRecordarr });
//		
//					sRTN_CD		= StringHelper.evl(outRecord21.getFieldString("RTN_CD"), "0");
//					sRTN_MSG	= StringHelper.evl(outRecord21.getFieldString("RTN_MSG"), "");
//					ydUtils.putLog(szSessionName, szMethodName, "//" + sRTN_MSG, YdConstant.DEBUG);
//					if (!("1".equals(sRTN_CD))) {
//						gdRes.setMessage(sRTN_MSG);		
//					} else {
//		
//						gdRes.setMessage(sRTN_MSG + " <br> 정상적으로 스케쥴까지 등록했습니다.");		
//			
//					} 	
//		    	}	
//			}
//
//			if(!sSecondYD_WBOOK_ID.equals("")) {
//
//				JDTORecord[] inRecordarr   	= null;
//				inRecordarr = new JDTORecord[1];
//				
//				inRecordarr[0] = JDTORecordFactory.getInstance().create();
//				inRecordarr[0].setField("YD_SCH_CD"		, szYD_SCH_CD); 
//				inRecordarr[0].setField("YD_WBOOK_ID"	, sSecondYD_WBOOK_ID); 
//				
//				ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
//				outRecord21 = (JDTORecord) ejbConn.trx("trxRunSchedule", new Class[] { JDTORecord[].class },	new Object[] { inRecordarr });
//	
//				sRTN_CD		= StringHelper.evl(outRecord21.getFieldString("RTN_CD"), "0");
//				sRTN_MSG	= StringHelper.evl(outRecord21.getFieldString("RTN_MSG"), "");
//				ydUtils.putLog(szSessionName, szMethodName, "//" + sRTN_MSG, YdConstant.DEBUG);
//				if (!("1".equals(sRTN_CD))) {
//
//					gdRes.setMessage(sRTN_MSG);		
//
//				} else {
//	
//					gdRes.setMessage(sRTN_MSG + " <br> 정상적으로 스케쥴까지 등록했습니다.");		
//
//				} 	
//			}
//			
//			
//			if(!sThirdYD_WBOOK_ID.equals("")) {
//
//				JDTORecord[] inRecordarr   	= null;
//				inRecordarr = new JDTORecord[1];
//				
//				inRecordarr[0] = JDTORecordFactory.getInstance().create();
//				inRecordarr[0].setField("YD_SCH_CD"		, szYD_SCH_CD); 
//				inRecordarr[0].setField("YD_WBOOK_ID"	, sThirdYD_WBOOK_ID); 
//				
//				ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
//				outRecord21 = (JDTORecord) ejbConn.trx("trxRunSchedule", new Class[] { JDTORecord[].class },	new Object[] { inRecordarr });
//	
//				sRTN_CD		= StringHelper.evl(outRecord21.getFieldString("RTN_CD"), "0");
//				sRTN_MSG	= StringHelper.evl(outRecord21.getFieldString("RTN_MSG"), "");
//				ydUtils.putLog(szSessionName, szMethodName, "//" + sRTN_MSG, YdConstant.DEBUG);
//				if (!("1".equals(sRTN_CD))) {
//					gdRes.setMessage(sRTN_MSG);		
//				} else {
//	
//					gdRes.setMessage(sRTN_MSG + " <br> 정상적으로 스케쥴까지 등록했습니다.");		
//		
//				} 	
//			}
				
			szMsg = "다수코일  작업예약  완료!";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);					
			
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		return gdRes;
	} // end of updColUnitCarMvstkReg
	
	
////////////////////////////////////////////////////////////////////////	
//C증설	
	/**
	 * 소재 SPM/HFL입측관리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * 송
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData getCoilYdHrTrackingNew(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdHrTrackingNew";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdHrTrackingNew", inRecord);

			gdRes     = CmUtil.genResGridData(inDto , recordSet);

		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //	
	
	/**
	 * 제품 지포장 입측관리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * 송
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData getCoilYdHrTrackingGPack(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdHrTrackingNew";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdHrTrackingGPack", inRecord);

			gdRes     = CmUtil.genResGridData(inDto , recordSet);

		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //	
	
	/**
	 * 소재 SPM/HFL입측관리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData getCoilYdHrTrackingBackUpNew(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdHrTrackingBackUpNew";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdHrTrackingBackUpNew", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //
	

	/**
	 * 소재 SPM/HFL입측관리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData getCoilYdHrTrackingBackUpGPack(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdHrTrackingBackUpNew";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdHrTrackingBackUpGPack", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //
	

	/**
	 * 짱구코일 관리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData getMonitorChookCoilGdsYard(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getMonitorChookCoilGdsYard";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getMonitorChookCoilGdsYard", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //
	
	
	/**
	 * 소재 SPM/HFL입측관리 POP
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.03
	 */
	public GridData getSupplyInOrderListNew(GridData inDto){
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		String szMethodName = "getSupplyInOrderList";	
		try{
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			gdRes = (GridData)ejbConn.trx("getSupplyInOrderListNew", new Class[] { GridData.class }, new Object[] { inDto });
			
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		
		return gdRes;
	} 
	/**
	 * 소재 SPM/HFL입측관리 POP
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData getcoilYdLineWrPpNew(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getcoilYdLineWrPpNew";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getcoilYdLineWrPpNew", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //
	
	/**
	 * 야드관리 > 코일소재야드 > 크레인실적관리 > 스판단위이적등록 (작업가능 대차)
	 * @ejb.interface-method
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @작성자 :박지열
	 * @작성일 : 2010.04.22
	 */
	public GridData getToDongTcarUse(GridData inDto) throws DAOException {
		//LOG
	
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		String szMethodName = "getToDongTcarUse";	
		try{
			
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			gdRes = (GridData)ejbConn.trx("getToDongTcarUse", new Class[] { GridData.class }, new Object[] { inDto });

			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		return gdRes;
	} // end of getSpanMvList
	
	/**
	 * 콘베어정보목록img 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdConveyorMgtImgNew(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdConveyorMgtImgNew";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdConveyorMgtImgNew", inRecord);
			
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	
	/**
	 * 야드관리 > 
	 * @ejb.interface-method
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @작성자 :박지열
	 * @작성일 : 2010.04.22
	 */
	public GridData getHrShrWoUnitCmtUnit(GridData inDto) throws DAOException {
		//LOG
	
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		String szMethodName = "getHrShrWoUnitCmtUnit";	
		try{
			
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			gdRes = (GridData)ejbConn.trx("getHrShrWoUnitCmtUnit", new Class[] { GridData.class }, new Object[] { inDto });

			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		return gdRes;
	} // end of getHrShrWoUnitCmtUnit
	
	
	/**
	 * 야드관리 > 코일소재야드 > 야드재공관리 > 스판별재공현황 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getSpanStockList(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "스판별재공현황";
		String szMethodName = "getSpanStockList";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szMsg = "JSP-FACADE [스판별재공현황 ] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getSpanStockList", inRecord);
			
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		szMsg = "JSP-FACADE [스판별재공현황 ] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		return gdRes;
	}
	
	/**
	 *  코일이송실적조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getCoilYdMoveResultList(GridData inDto) throws JDTOException {
	 
		String szMethodName = "getCoilYdMoveResultList";
		String szLogMsg = "";
		String szOperationName = "코일이송실적조회";
		
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szLogMsg = "[Jsp Facade : " + szOperationName + "] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdMoveResultList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return gdRes;
	} //end of getCoilYdMoveResultList
	
	/**
	 *  코일이송실적조회상세
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getCoilYdMoveResultListSub(GridData inDto) throws JDTOException {
	 
		String szMethodName = "getCoilYdMoveResultListSub";
		String szLogMsg = "";
		String szOperationName = "코일이송실적조회상세";
		
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szLogMsg = "[Jsp Facade : " + szOperationName + "] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdMoveResultListSub", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return gdRes;
	} //end of getCoilYdMoveResultListSub
	
	/**
	 *  코일야드 차량진행관리 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdCarWorkingList(GridData gdReq) throws JDTOException {
 
		String szMethodName="getCoilYdCarWorkingList";
		String szLogMsg = "";
		String szOperationName	= "코일야드 차량진행관리 조회";
		
		
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
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCoilYdCarWorkingList", inRecord);				

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
	 * B열연 Coil 야드 결로 위치 지정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecordSet
	 * @return void
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public void updCondensationAsgnInfo(JDTORecord[] inRecordSet) throws DAOException {
		//LOG

		String szMsg				= "";
		String szMethodName			= "updCondensationAsgnInfo";
		JDTORecord inRecord			= JDTORecordFactory.getInstance().create();
		JDTORecord inRecord2		= JDTORecordFactory.getInstance().create();

		String sYD_GP				= "";
		String sYD_BAY_GP			= "";
		String sYD_EQP_GP			= "";
		String sCONTENTS			= "";
		String sMODIFIER 			= "";
		
		JDTORecord outRecord   		= JDTORecordFactory.getInstance().create();
		
		EJBConnector ejbConn = null;

		try{
 
			for(int nLoop = 0; nLoop < inRecordSet.length; nLoop++) {
				
				inRecord = JDTORecordFactory.getInstance().create();
				inRecord2 = JDTORecordFactory.getInstance().create();
				
				inRecord = inRecordSet[nLoop];
				
				sYD_GP			= StringHelper.evl(inRecord.getFieldString("YD_GP"), "");
				sYD_BAY_GP		= StringHelper.evl(inRecord.getFieldString("YD_BAY_GP"), "");
				sYD_EQP_GP		= StringHelper.evl(inRecord.getFieldString("YD_EQP_GP"), "");
				sCONTENTS		= StringHelper.evl(inRecord.getFieldString("CONTENTS"), "");
				sMODIFIER 		= StringHelper.evl(inRecord.getFieldString("YD_USER_ID"), "");
				
				inRecord2.setField("YD_GP",     	sYD_GP);								//야드구분
				inRecord2.setField("YD_BAY_GP",     sYD_BAY_GP);							//야드동
				inRecord2.setField("YD_EQP_GP",     sYD_EQP_GP);							//스판번호
				inRecord2.setField("CONTENTS",     	sCONTENTS);								//내용
				inRecord2.setField("YD_USER_ID",    sMODIFIER);								//사용자ID
				
				ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
				outRecord = (JDTORecord)ejbConn.trx("updCondensationAsgnInfo", new Class[] { JDTORecord.class }, new Object[] { inRecord2 });
				
				ydUtils.putLog(szSessionName, szMethodName, "결로 위치 지정 성공", YdConstant.DEBUG);
				
			}
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "결로 위치 지정 실패", YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}

	}  //end of updCondensationAsgnInfo
	
	
	
	
	/**
	 * B열연 Coil 야드 결로 위치 해제
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecordSet
	 * @return void
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public void updCondensationRelInfo(JDTORecord[] inRecordSet) throws DAOException {
		//LOG

		String szMsg				= "";
		String szMethodName			= "updCondensationRelInfo";
		JDTORecord inRecord			= JDTORecordFactory.getInstance().create();
		JDTORecord inRecord2		= JDTORecordFactory.getInstance().create();

		String sYD_GP				= "";
		String sYD_BAY_GP			= "";
		String sYD_EQP_GP			= "";
		String sMODIFIER 			= "";
		
		JDTORecord outRecord   		= JDTORecordFactory.getInstance().create();
		
		EJBConnector ejbConn = null;

		try{
 
			for(int nLoop = 0; nLoop < inRecordSet.length; nLoop++) {
				
				inRecord = JDTORecordFactory.getInstance().create();
				inRecord2 = JDTORecordFactory.getInstance().create();
				
				inRecord = inRecordSet[nLoop];
				
				sYD_GP			= StringHelper.evl(inRecord.getFieldString("YD_GP"), "");
				sYD_BAY_GP		= StringHelper.evl(inRecord.getFieldString("YD_BAY_GP"), "");
				sYD_EQP_GP		= StringHelper.evl(inRecord.getFieldString("YD_EQP_GP"), "");
				sMODIFIER 		= StringHelper.evl(inRecord.getFieldString("YD_USER_ID"), "");
				
				inRecord2.setField("YD_GP",     	sYD_GP);								//야드구분
				inRecord2.setField("YD_BAY_GP",     sYD_BAY_GP);							//야드동
				inRecord2.setField("YD_EQP_GP",     sYD_EQP_GP);							//스판번호
				inRecord2.setField("YD_USER_ID",    sMODIFIER);								//사용자ID
				
				ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
				outRecord = (JDTORecord)ejbConn.trx("updCondensationRelInfo", new Class[] { JDTORecord.class }, new Object[] { inRecord2 });
				
				ydUtils.putLog(szSessionName, szMethodName, "결로 위치 해제 성공", YdConstant.DEBUG);
				
			}
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "결로 위치 해제 실패", YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}

	}  //end of updCondensationRelInfo
	
	
	/**
	 *  2열연 결로ON/OFF 조회 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getConResultList(GridData inDto) throws JDTOException {
	 
		String szMethodName = "getConResultList";
		String szLogMsg = "";
		String szOperationName = "2열연 결로ON/OFF 조회";
		
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szLogMsg = "[Jsp Facade : " + szOperationName + "] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getConResultList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade : "+szOperationName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return gdRes;
	} //end of getConResultList
	
	/**
	 * 결로ON  버튼 클릭시 실행
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updConOffResultList(GridData inDto) throws DAOException {
		//LOG
	
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		String szMethodName = "updConOffResultList";	
		try{
			
			ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
			gdRes = (GridData)ejbConn.trx("updConOffResultList", new Class[] { GridData.class }, new Object[] { inDto });

			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		return gdRes;
	} // end of updConOffResultList
	
	/**
	 * 결로HOT코일이적
	 * 송정현
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCondenMvstkReg(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCondenMvstkReg";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilJspSeEJB", "getCondenMvstkReg", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} // end of getCondenMvstkReg
} 