package com.inisteel.cim.yd.jjyd.session;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;
import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;

import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookMtlDao.YdWrkbookMtlDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO;
import com.inisteel.cim.yd.jplateyd.session.JPlateYdJspFaEJBBean;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdConst;
import com.inisteel.cim.yd.jsp.common.YDComUtil;
import com.inisteel.cim.yd.jsp.common.YDDataUtil;
import com.inisteel.cim.yd.common.util.YdUtils;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.CmUtil;


/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Facade Session EJB클래스입니다.
 * 
 * @ejb.bean name="PlateReviseFaEJB" jndi-name="PlateReviseFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class PlateReviseFaEJBBean extends BaseSessionBean {

	private String szSessionName = getClass().getName(); 
	private YdUtils ydUtils = new YdUtils();
	YDComUtil   ydComUtil = new YDComUtil();
	private YdDaoUtils ydDaoUtils =new YdDaoUtils();
	
	private EJBConnector ydEjbCon = new EJBConnector("default", this);

	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	
	

	/**
	 * 스판과 배드에 할당된 단정보 가져오기
	 * @ejb.interface-method
	 * @param GridData
	 * @return JDTORecord
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.17
	 */
	public JDTORecord getpPlateYdCrnDownListPDADanList(JDTORecord inDto){
		JDTORecord      	outRd     = null;
		EJBConnector  		ejbConn   = null;
		
		try{
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			outRd = (JDTORecord)ejbConn.trx("getpPlateYdCrnDownListPDADanList", new Class[] { JDTORecord.class }, new Object[] { inDto });
			
		}catch(Exception e){		
			e.printStackTrace();
		}
		
		
		return outRd;
	} 

	/**
	 * 후판정정야드 관리자 적치확인 처리화면 (PDA) LIST조회
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.17
	 */
	public JDTORecordSet getpPlateYdCrnDownListPDA(JDTORecord inDto){
		JDTORecordSet      	outRdSet     = null;
		EJBConnector  		ejbConn   = null;
		
		try{
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			outRdSet = (JDTORecordSet)ejbConn.trx("getpPlateYdCrnDownListPDA", new Class[] { JDTORecord.class }, new Object[] { inDto });
			
		}catch(Exception e){		
			e.printStackTrace();
		}
		
		
		return outRdSet;
	} 
	
	/**
	 * 임가공입고실적등록 (조회)
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.17
	 */
	public GridData getCoilFromToResultList(GridData inDto){
		GridData			gdRds		= new GridData();
		JDTORecordSet      	outRdSet    = null;
		EJBConnector  		ejbConn   	= null;
		
		try{
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			outRdSet = (JDTORecordSet)ejbConn.trx("getCoilFromToResultList", new Class[] { GridData.class }, new Object[] { inDto });
			
			if(outRdSet != null || outRdSet.size() == 0){
				gdRds = CmUtil.genGridData(inDto, outRdSet);
			}else{
				gdRds.setMessage("조회된 데이터가 없습니다.");
				gdRds.addParam("ret", "-1");
			}
			
			
		}catch(Exception e){		
			e.printStackTrace();
		}
		
		
		return gdRds;
	} 
	
	
	/**
	 * 후판정정야드 크레인스케줄(BookIn/이적)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @작성자 : 윤혁상
	 * @작성일 : 2010.08.19
	 */
	public GridData pPlateCrnSchBookInMm(GridData inDto){
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		
		String szMethodName="pPlateCrnSchBookInMm";
		String szR_msg ="";
		String szLogMsg = "";
		String szOperationName	= "크레인스케줄(BookIn/이적)";

		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);	
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			szR_msg = (String)ejbConn.trx("pPlateCrnSchBookInMm", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);		
			gdRes = CmUtil.copyGDParam(inDto, gdRes);
			
			gdRes.setMessage(szR_msg);
			ydUtils.putLog(szSessionName, szMethodName, szR_msg, YdConstant.INFO);
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

		}catch(Exception e){		
			e.printStackTrace();
		}


		return gdRes;
	} 
	
	
	
	/**
	 * 후판정정야드 스케쥴조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @작성자 : 
	 * @작성일 : 
	 */
	public GridData getpPlateYdSchList(GridData inDto){
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;

		try{
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			gdRes = (GridData)ejbConn.trx("getpPlateYdSchList", new Class[] { GridData.class }, new Object[] { inDto });

		}catch(Exception e){		
			e.printStackTrace();
		}


		return gdRes;
	} 
	
	
	
	/**
	 * 후판정정야드 모니터링 총량조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @작성자 : 
	 * @작성일 : 
	 */
	public GridData getpPlateMonitoring_Tot(GridData inDto){
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;

		try{
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			gdRes = (GridData)ejbConn.trx("getpPlateMonitoring_Tot", new Class[] { GridData.class }, new Object[] { inDto });

		}catch(Exception e){		
			e.printStackTrace();
		}


		return gdRes;
	} 
	
	
	
	/**
	 * 후판정정야드 작업대상조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @작성자 : 
	 * @작성일 : 
	 */
	public GridData getpPlateYdCrnWorkList(GridData inDto){
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;

		try{
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			gdRes = (GridData)ejbConn.trx("getpPlateYdCrnWorkList", new Class[] { GridData.class }, new Object[] { inDto });

		}catch(Exception e){		
			e.printStackTrace();
		}


		return gdRes;
	} 
	
	
	
	/**
	 * 후판정정야드 스케쥴정보수정 (크레인)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData updatepPlateYdSchCrn(GridData gdReq) throws JDTOException {
	
		String szMethodName="updatepPlateYdSchCrn";
		String szLogMsg = ""; 
		String szOperationName	= "후판정정야드 스케쥴정보수정 (크레인)";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			
			szLogMsg = "JSP-FACADE [ " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			ejbConn.trx("updatepPlateYdSchCrn", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
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
	 * 후판정정야드 스케쥴삭제
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData deletepPlateYdSchCrn(GridData gdReq) throws JDTOException {
	
		String szMethodName="deletepPlateYdSchCrn";
		String szLogMsg = ""; 
		String szOperationName	= "후판정정야드 스케쥴삭제";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			
			szLogMsg = "JSP-FACADE [ " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			ejbConn.trx("deletepPlateYdSchCrn", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
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
	 * 크레인작업실적등록(차상국) - 크레인번호로 판번호(재료번호) 조회 
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.23
	 */
	public GridData getCrnNoAndStlNo(GridData inDto){
		GridData			gdRds		= new GridData();
		JDTORecordSet      	outRdSet    = null;
		EJBConnector  		ejbConn   	= null;
		
		try{
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			outRdSet = (JDTORecordSet)ejbConn.trx("getCrnNoAndStlNo", new Class[] { GridData.class }, new Object[] { inDto });
			
			if(outRdSet != null || outRdSet.size() == 0){
				gdRds = CmUtil.genGridData(inDto, outRdSet);
			}else{
				gdRds.setMessage("조회된 데이터가 없습니다.");
				gdRds.addParam("ret", "-1");
			}
			
			
		}catch(Exception e){		
			e.printStackTrace();
		}
		
		
		return gdRds;
	} 
	
	/**
	 * 크레인작업실적등록(차상국) - 권상/권하위치 조회
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.23
	 */
	public GridData getCrnUpDownLocList(GridData inDto){
		GridData			gdRds		= new GridData();
		JDTORecordSet      	outRdSet    = null;
		EJBConnector  		ejbConn   	= null;
		
		try{
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			outRdSet = (JDTORecordSet)ejbConn.trx("getCrnUpDownLocList", new Class[] { GridData.class }, new Object[] { inDto });
			
			if(outRdSet != null || outRdSet.size() == 0){
				gdRds = CmUtil.genGridData(inDto, outRdSet);
			}else{
				gdRds.setMessage("조회된 데이터가 없습니다.");
				gdRds.addParam("ret", "-1");
			}
			
			
		}catch(Exception e){		
			e.printStackTrace();
		}
		
		
		return gdRds;
	} 
	
	
	
	/**
	 * 크레인작업실적등록(차상국) - 권상/권하위치 조회
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.23
	 */
	public GridData getCrnUpDownBedList(GridData inDto){
		GridData			gdRds		= new GridData();
		JDTORecordSet      	outRdSet    = null;
		EJBConnector  		ejbConn   	= null;
		
		try{
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			outRdSet = (JDTORecordSet)ejbConn.trx("getCrnUpDownBedList", new Class[] { GridData.class }, new Object[] { inDto });
			
			if(outRdSet != null || outRdSet.size() == 0){
				gdRds = CmUtil.genGridData(inDto, outRdSet);
			}else{
				gdRds.setMessage("조회된 데이터가 없습니다.");
				gdRds.addParam("ret", "-1");
			}
			
			
		}catch(Exception e){		
			e.printStackTrace();
		}
		
		
		return gdRds;
	} 
	
	
	
	/**
	 * 크레인작업실적등록(차상국) - 권상/권하위치 조회
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.23
	 */
	public GridData getCrnUpDownBedList2(GridData inDto){
		GridData			gdRds		= new GridData();
		JDTORecordSet      	outRdSet    = null;
		EJBConnector  		ejbConn   	= null;
		
		try{
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			outRdSet = (JDTORecordSet)ejbConn.trx("getCrnUpDownBedList2", new Class[] { GridData.class }, new Object[] { inDto });
			
			if(outRdSet != null || outRdSet.size() == 0){
				gdRds = CmUtil.genGridData(inDto, outRdSet);
			}else{
				gdRds.setMessage("조회된 데이터가 없습니다.");
				gdRds.addParam("ret", "-1");
			}
			
			
		}catch(Exception e){		
			e.printStackTrace();
		}
		
		
		return gdRds;
	} 
	
	/**
	 * 크레인작업실적등록(차상국) - 권상/권하위치 조회
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.23
	 */
	public GridData getCrnUpDownList(GridData inDto){
		GridData			gdRds		= new GridData();
		JDTORecordSet      	outRdSet    = null;
		EJBConnector  		ejbConn   	= null;
		
		try{
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			outRdSet = (JDTORecordSet)ejbConn.trx("getCrnUpDownList", new Class[] { GridData.class }, new Object[] { inDto });
			
			if(outRdSet != null || outRdSet.size() == 0){
				gdRds = CmUtil.genGridData(inDto, outRdSet);
			}else{
				gdRds.setMessage("조회된 데이터가 없습니다.");
				gdRds.addParam("ret", "-1");
			}
			
			
		}catch(Exception e){		
			e.printStackTrace();
		}
		
		
		return gdRds;
	} 
	
	
	 
	/**
	 * 크레인작업실적등록(차상국) - 권상위치 조회 
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @작성자 : 윤혁상
	 * @작성일 : 2010.08.26
	 */
	public GridData getCrnUpLocList(GridData inDto){
		GridData			gdRds		= new GridData();
		JDTORecordSet      	outRdSet    = null;
		EJBConnector  		ejbConn   	= null;
		
		try{
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			outRdSet = (JDTORecordSet)ejbConn.trx("getCrnUpLocList", new Class[] { GridData.class }, new Object[] { inDto });
			
			if(outRdSet != null || outRdSet.size() == 0){
				gdRds = CmUtil.genGridData(inDto, outRdSet);
			}else{
				gdRds.setMessage("조회된 데이터가 없습니다.");
				gdRds.addParam("ret", "-1");
			}
			
			
		}catch(Exception e){		
			e.printStackTrace();
		}
		
		
		return gdRds;
	} 
	
	
	/**
	 * 크레인작업실적등록(차상국) - 권하위치 조회
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @작성자 : 윤혁상
	 * @작성일 : 2010.08.26
	 */
	public GridData getCrnDownLocList(GridData inDto){
		GridData			gdRds		= new GridData();
		JDTORecordSet      	outRdSet    = null;
		EJBConnector  		ejbConn   	= null;
		
		try{
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			outRdSet = (JDTORecordSet)ejbConn.trx("getCrnDownLocList", new Class[] { GridData.class }, new Object[] { inDto });
			
			if(outRdSet != null || outRdSet.size() == 0){
				gdRds = CmUtil.genGridData(inDto, outRdSet);
			}else{
				gdRds.setMessage("조회된 데이터가 없습니다.");
				gdRds.addParam("ret", "-1");
			}
			
			
		}catch(Exception e){		
			e.printStackTrace();
		}
		
		
		return gdRds;
	} 
	
	
	
	/**
	 * 후판정정야드 별 소재현황 1 - 목록조회 
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.24
	 */
	public GridData getPlateYdlocList_1(GridData inDto){
		GridData			gdRds		= new GridData();
		JDTORecordSet      	outRdSet    = null;
		EJBConnector  		ejbConn   	= null;
		
		try{
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			outRdSet = (JDTORecordSet)ejbConn.trx("getPlateYdlocList_1", new Class[] { GridData.class }, new Object[] { inDto });
			
			if(outRdSet != null || outRdSet.size() == 0){
				gdRds = CmUtil.genGridData(inDto, outRdSet);
			}else{
				gdRds.setMessage("조회된 데이터가 없습니다.");
				gdRds.addParam("ret", "-1");
			}
			
			
		}catch(Exception e){		
			e.printStackTrace();
		}
		
		
		return gdRds;
	} 
	
	/**
	 * 후판정정야드 별 소재현황 2 - 그래픽 표현 목록조회 
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.24
	 */
	public GridData getPlateYdlocList_2(GridData inDto){
		GridData			gdRds		= new GridData();
		JDTORecordSet      	outRdSet    = null;
		EJBConnector  		ejbConn   	= null;
		
		try{
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			outRdSet = (JDTORecordSet)ejbConn.trx("getPlateYdlocList_2", new Class[] { GridData.class }, new Object[] { inDto });
			
			if(outRdSet != null || outRdSet.size() == 0){
				gdRds = CmUtil.genGridData(inDto, outRdSet);
			}else{
				gdRds.setMessage("조회된 데이터가 없습니다.");
				gdRds.addParam("ret", "-1");
			}
			
			
		}catch(Exception e){		
			e.printStackTrace();
		}
		
		
		return gdRds;
	} 
	
	
	/**
	 * 후판정정야드 북아웃코드 (조회)
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @작성자 : 윤혁상
	 * @작성일 : 2010.08.25
	 */
	public GridData getpPlateYdBookoutCodeList(GridData inDto){
		GridData			gdRds		= new GridData();
		JDTORecordSet      	outRdSet    = null;
		EJBConnector  		ejbConn   	= null;
		
		try{
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			outRdSet = (JDTORecordSet)ejbConn.trx("getpPlateYdBookoutCodeList", new Class[] { GridData.class }, new Object[] { inDto });
			
			if(outRdSet != null || outRdSet.size() == 0){
				gdRds = CmUtil.genGridData(inDto, outRdSet);
			}else{
				gdRds.setMessage("조회된 데이터가 없습니다.");
				gdRds.addParam("ret", "-1");
			}
			
			
		}catch(Exception e){		
			e.printStackTrace();
		}
		
		
		return gdRds;
	} 
	
	
	
	/**
	 * 후판정정야드 북아웃코드 삭제
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData delYdBookoutCode(GridData gdReq) throws JDTOException {
	
		String szMethodName="delYdBookoutCode";
		String szLogMsg = ""; 
		String szOperationName	= "후판정정야드 북아웃코드 삭제";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			
			szLogMsg = "JSP-FACADE [ " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			ejbConn.trx("delYdBookoutCode", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
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
	 * 후판정정야드 북아웃코드 수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData updateYdBookoutCode(GridData gdReq) throws JDTOException {
	
		String szMethodName="updateYdBookoutCode";
		String szLogMsg = ""; 
		String szOperationName	= "후판정정야드 북아웃코드 수정";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			
			szLogMsg = "JSP-FACADE [ " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
	
			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			ejbConn.trx("updateYdBookoutCode", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
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
	 * 후판정정야드 북아웃코드 등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData inspPlateYdBookoutCode(GridData gdReq) throws JDTOException {
	
		String szMethodName="updateYdBookoutCode";
		String szLogMsg = ""; 
		String szOperationName	= "후판정정야드 북아웃코드 등록";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			
			szLogMsg = "JSP-FACADE [ " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
	
			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			ejbConn.trx("inspPlateYdBookoutCode", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
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
	 * 후판정정야드 권상실적처리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @작성자 : 윤혁상
	 * @작성일 : 2010.08.19
	 */
	public GridData pPlateYdCrnUpWrk(GridData inDto){
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		
		String szMethodName="pPlateYdCrnUpWrk";
		String szR_msg ="";
		String szLogMsg = "";
		String szOperationName	= "후판정정야드 권상실적처리";

		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);	
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			szR_msg = (String)ejbConn.trx("pPlateYdCrnUpWrk", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);		
			gdRes = CmUtil.copyGDParam(inDto, gdRes);
			
			gdRes.setMessage(szR_msg);
			ydUtils.putLog(szSessionName, szMethodName, szR_msg, YdConstant.INFO);
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

		}catch(Exception e){		
			e.printStackTrace();
		}


		return gdRes;
	} 
	
	
	
	/**
	 * 후판정정야드 권하실적처리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @작성자 : 윤혁상
	 * @작성일 : 2010.08.19
	 */
	public GridData pPlateYdCrnDownWrk(GridData inDto){


		GridData			gdRds		= new GridData();
		JDTORecordSet      	outRdSet    = null;
		EJBConnector  		ejbConn   	= null;
		
		try{
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			outRdSet = (JDTORecordSet)ejbConn.trx("pPlateYdCrnDownWrk", new Class[] { GridData.class }, new Object[] { inDto });
			
		/*	if(outRdSet != null || outRdSet.size() == 0){
				gdRds = CmUtil.genGridData(inDto, outRdSet);
			}else{
				gdRds.setMessage("조회된 데이터가 없습니다.");
				gdRds.addParam("ret", "-1");
			}*/
			
			
		}catch(Exception e){		
			e.printStackTrace();
		}
		
		
		return gdRds;
		
	} 
	
	
	
	/**
	 * 저장위치수정 팝업 조회 (화면)
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.02
	 */
	public GridData getJjydPlateLocMgt(GridData inDto){
		GridData			gdRds		= new GridData();
		JDTORecordSet      	outRdSet    = null;
		EJBConnector  		ejbConn   	= null;
		
		try{
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			outRdSet = (JDTORecordSet)ejbConn.trx("getJjydPlateLocMgt", new Class[] { GridData.class }, new Object[] { inDto });
			
			if(outRdSet != null || outRdSet.size() == 0){
				gdRds = CmUtil.genGridData(inDto, outRdSet);
			}else{
				gdRds.setMessage("조회된 데이터가 없습니다.");
				gdRds.addParam("ret", "-1");
			}
			
			
		}catch(Exception e){		
			e.printStackTrace();
		}
		
		
		return gdRds;
	} 
	
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 저장위치수정 조회 (PDA)
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.02
	 */
	public JDTORecordSet getPDA_pPlateLocMgt(JDTORecord inDto){
		JDTORecordSet      	outRdSet    = null;
		EJBConnector  		ejbConn   	= null;
		
		try{
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			outRdSet = (JDTORecordSet)ejbConn.trx("getPDA_pPlateLocMgt", new Class[] { JDTORecord.class }, new Object[] { inDto });
			
		}catch(Exception e){		
			e.printStackTrace();
		}
		
		return outRdSet;
	} 
	
	
	/**
	 * 저장위치수정 (TO위치 조회)
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.07
	 */
	public GridData getJjydPlateToLoc(GridData inDto){
		GridData			gdRds		= new GridData();
		JDTORecordSet      	outRdSet    = null;
		EJBConnector  		ejbConn   	= null;
		JDTORecord 			recPara		= null;
		
		try{
			
			recPara	= JDTORecordFactory.getInstance().create(); 			
			recPara.setField("V_YD_EQP_GP", 		inDto.getParam("T_YD_EQP_GP").trim());	    /*스판(야드)*/
			recPara.setField("V_YD_BED_GP", 		inDto.getParam("T_YD_BED_GP").trim());	    /*배드*/
			recPara.setField("V_YD_STK_LYR_NO", 	inDto.getParam("T_YD_STK_LYR_NO").trim());	/*단*/
			recPara.setField("V_YD_STK_BED_NO", 	inDto.getParam("T_YD_FEVER_GP").trim());	/*열*/
			
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			outRdSet = (JDTORecordSet)ejbConn.trx("getJjydPlateToLoc", new Class[] { JDTORecord.class }, new Object[] { recPara });
			
			if(outRdSet != null || outRdSet.size() == 0){
				gdRds = CmUtil.genGridData(inDto, outRdSet);
			}else{
				gdRds.setMessage("조회된 데이터가 없습니다.");
				gdRds.addParam("ret", "-1");
			}
			
			
		}catch(Exception e){		
			e.printStackTrace();
		}
		
		
		return gdRds;
	} 
	
	/**
	 * 저장위치수정 (TO위치 조회)- PDA
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.07
	 */
	public JDTORecordSet getJjydPlateToLoc(JDTORecord inDto){
		JDTORecordSet      	outRdSet    = null;
		EJBConnector  		ejbConn   	= null;
		JDTORecord 			recPara		= null;
		
		try{

			recPara	= JDTORecordFactory.getInstance().create(); 	
			
			recPara.setField("V_YD_EQP_GP", 		inDto.getFieldString("T_YD_EQP_GP").trim());	    /*스판(야드)*/
			recPara.setField("V_YD_BED_GP", 		inDto.getFieldString("T_YD_BED_GP").trim());	    /*배드*/
			recPara.setField("V_YD_STK_LYR_NO", 	inDto.getFieldString("T_YD_STK_LYR_NO").trim());	/*단*/
			recPara.setField("V_YD_STK_BED_NO", 	inDto.getFieldString("T_YD_FEVER_GP").trim());	/*열*/
			
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			outRdSet = (JDTORecordSet)ejbConn.trx("getJjydPlateToLoc", new Class[] { JDTORecord.class }, new Object[] { recPara });
			
			
			
		}catch(Exception e){		
			e.printStackTrace();
		}
		
		
		return outRdSet;
	} 
	
	/**
	 * 저장위치수정 수정
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.17
	 */
	public GridData updJjydPlateLocMgt(GridData inDto){
		GridData			gdRds		= new GridData();
		JDTORecord      	outRcd       = null;
		EJBConnector  		ejbConn   	= null;
		try{
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			outRcd = (JDTORecord)ejbConn.trx("updJjydPlateLocMgt", new Class[] { GridData.class }, new Object[] { inDto });
			
			gdRds = CmUtil.copyGDParam(inDto, gdRds);
			if(outRcd != null && outRcd.size()>0){
				gdRds.addParam("RTN_CD", outRcd.getFieldString("RTN_CD"));
				gdRds.setMessage(outRcd.getFieldString("RTN_MSG"));
			}else{
				gdRds.addParam("RTN_CD", "-1");
				gdRds.setMessage("저장위치 수정 실패");
			}
			
		}catch(Exception e){		
			e.printStackTrace();
		}
		
		
		return gdRds;
	} 
	
	/**
	 * 저장위치수정 수정(PDA)
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.17
	 */
	public JDTORecord updPDA_pPlateLocMgt(JDTORecord inDto){
		JDTORecord      	outRcd       = null;
		EJBConnector  		ejbConn   	= null;
		try{
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			outRcd = (JDTORecord)ejbConn.trx("updPDA_pPlateLocMgt", new Class[] { JDTORecord.class }, new Object[] { inDto });
						
		}catch(Exception e){		
			e.printStackTrace();
		}
		
		
		return outRcd;
	} 
	
	
	/**
	 * 저장위치수정 삭제/반입(PDA)
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.17
	 */
	public JDTORecord delPDA_pPlateLocMgt(JDTORecord inDto){
		JDTORecord      	outRcd       = null;
		EJBConnector  		ejbConn   	= null;
		try{
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			outRcd = (JDTORecord)ejbConn.trx("delPDA_pPlateLocMgt", new Class[] { JDTORecord.class }, new Object[] { inDto });
			
		}catch(Exception e){		
			e.printStackTrace();
		}
		
		
		return outRcd;
	} 
	
	
	
	/**
	 * 저장위치수정 BookIn(PDA)
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.17
	 */
	public GridData deleteBookoutStl(GridData inDto){
		GridData      gdRes     = null;
		EJBConnector  		ejbConn   	= null;
		try{
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			gdRes = (GridData)ejbConn.trx("deleteBookoutStl", new Class[] { GridData.class }, new Object[] { inDto });
			
		}catch(Exception e){		
			e.printStackTrace();
		}
		
		
		return gdRes;
	} 
	
	
	
	
	
	
	/**
	 * 북아웃정보삭제
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.17
	 */
	public JDTORecord bookinPDA_pPlateLocMgt(JDTORecord inDto){
		JDTORecord      	outRcd       = null;
		EJBConnector  		ejbConn   	= null;
		try{
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			outRcd = (JDTORecord)ejbConn.trx("bookinPDA_pPlateLocMgt", new Class[] { JDTORecord.class }, new Object[] { inDto });
			
		}catch(Exception e){		
			e.printStackTrace();
		}
		
		
		return outRcd;
	} 
	
	
	/**
	 * 저장위치수정 등록(PDA)
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.17
	 */
	public JDTORecord insPDA_pPlateLocMgt(JDTORecord inDto){
		JDTORecord      	outRcd       = null;
		EJBConnector  		ejbConn   	= null;
		try{
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			outRcd = (JDTORecord)ejbConn.trx("insPDA_pPlateLocMgt", new Class[] { JDTORecord.class }, new Object[] { inDto });
			
		}catch(Exception e){		
			e.printStackTrace();
		}
		
		
		return outRcd;
	} 
	
	

	/**
	 * 오퍼레이션명 : 후판정정야드가스절단실적
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvpPlateYdGascutresult(JDTORecord inRecord) throws JDTOException {
		//
		// YD-UC-???? 후판정정야드가스절단실적
		//
		//  
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvpPlateYdGascutresult";
		String szWRK_GP = "";

		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
			
		}
	
 
		try {
			
			szWRK_GP = ydDaoUtils.paraRecChkNull(inRecord, "WRK_GP");
			
			if(szWRK_GP.equals("1") ||szWRK_GP.equals("3"))
			{
				ydEjbCon.trx("PlateReviseSeEJB", "rcvpPlateYdSetoutStl", inRecord);				
			}
			
			else if(szWRK_GP.equals("2"))
			{
				ydEjbCon.trx("PlateReviseSeEJB", "rcvpPlateYdGascutresult", inRecord);
			}


		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);

		} // end of try catch


		
		szMsg="C연주크레인스케줄Main 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvY1CrnSchMain()
	
	
	
	
	/**
	 * 위치별적치현황- 야드별 방침 조회 
	 * @ejb.interface-method
	 * @param JDTORecord
	 * @return JDTORecord
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.20
	 */
	public GridData getPlateYdlocList_REMARK(GridData inDto){
		JDTORecordSet      	outRdSet    = null;
		EJBConnector  		ejbConn   	= null;
		GridData			gdRds		= new GridData();
		try{	
			
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			outRdSet = (JDTORecordSet)ejbConn.trx("getPlateYdlocList_REMARK", new Class[] { GridData.class }, new Object[] { inDto });
			if(outRdSet != null || outRdSet.size() == 0){
				gdRds = CmUtil.genGridData(inDto, outRdSet);
			}else{
				gdRds.setMessage("조회된 데이터가 없습니다.");
				gdRds.addParam("ret", "-1");
			}
			
			
		}catch(Exception e){		
			e.printStackTrace();
		}
		
		
		return gdRds;
	} 
	
	
	/**
	 * 스판과 배드에 할당된 단정보 가져오기
	 * @ejb.interface-method
	 * @param GridData
	 * @return JDTORecord
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.17
	 */
	public JDTORecord getLocMgtCodeLayerList_L(JDTORecord inDto){
		JDTORecord      	outRd     = null;
		EJBConnector  		ejbConn   = null;
		
		try{
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			outRd = (JDTORecord)ejbConn.trx("getLocMgtCodeLayerList_L", new Class[] { JDTORecord.class }, new Object[] { inDto });
			
		}catch(Exception e){		
			e.printStackTrace();
		}
		
		
		return outRd;
	} 
	/**
	 * 스판과 배드에 할당된 단정보 가져오기
	 * @ejb.interface-method
	 * @param GridData
	 * @return JDTORecord
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.17
	 */
	public JDTORecord getLocMgtCodeLayerList(JDTORecord inDto){
		JDTORecord      	outRd     = null;
		EJBConnector  		ejbConn   = null;
		
		try{
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			outRd = (JDTORecord)ejbConn.trx("getLocMgtCodeLayerList", new Class[] { JDTORecord.class }, new Object[] { inDto });
			
		}catch(Exception e){		
			e.printStackTrace();
		}
		
		
		return outRd;
	} 
	
	
	/**
	 * 스판과 배드에 할당된 단정보 가져오기
	 * @ejb.interface-method
	 * @param GridData
	 * @return JDTORecord
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.17
	 */
	public JDTORecord getpPlateYdLocationList(JDTORecord inDto){
		JDTORecord      	outRd     = null;
		EJBConnector  		ejbConn   = null;
		
		try{
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			outRd = (JDTORecord)ejbConn.trx("getpPlateYdLocationList", new Class[] { JDTORecord.class }, new Object[] { inDto });
			
		}catch(Exception e){		
			e.printStackTrace();
		}
		
		
		return outRd;
	} 
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 위치별적치현황 LIST (PDA)
	 * @ejb.interface-method
	 * @param JDTORecord
	 * @return JDTORecord
	 * @작성자 : 윤재광
	 * @작성일 : 2013.09.20
	 */
	public JDTORecordSet getPDApPlateYdLocList(JDTORecord inDto){
		
		JDTORecordSet      	outRdSet    = null;
		EJBConnector  		ejbConn   	= null;
		
		try{	
			
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			outRdSet = (JDTORecordSet)ejbConn.trx("getPDApPlateYdLocList", new Class[] { JDTORecord.class }, new Object[] { inDto });
		}catch(Exception e){		
			e.printStackTrace();
		}
		return outRdSet;
	} 
	/**
	 * 북인작업 대상재 정보 가져오기(PDA)
	 * @ejb.interface-method
	 * @param GridData
	 * @return JDTORecord
	 * @작성자 : 윤재광
	 * @작성일 : 2013.08.17
	 */
	public JDTORecord getpPlateYdBookoutStlList(JDTORecord inDto){
		JDTORecord      	outRd     = null;
		EJBConnector  		ejbConn   = null;
		
		try{
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			outRd = (JDTORecord)ejbConn.trx("getpPlateYdBookoutStlList", new Class[] { JDTORecord.class }, new Object[] { inDto });
			
		}catch(Exception e){		
			e.printStackTrace();
		}
		return outRd;
	} 
	
	/**
	 * 이적대상재 등록 (PDA)
	 * @ejb.interface-method
	 * @param GridData
	 * @return JDTORecord
	 * @작성자 : 윤재광
	 * @작성일 : 2010.08.17 
	 */
	public JDTORecord updpPlateYdCrnDownListPDA(JDTORecord inDto){
		EJBConnector  	ejbConn   	= null;
		JDTORecord		outRcd		= null;
		try{
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			outRcd = (JDTORecord)ejbConn.trx("updpPlateYdCrnDownListPDA", new Class[] { JDTORecord.class }, new Object[] { inDto });
			
		}catch(Exception e){		
			e.printStackTrace();
		}
		
		
		return outRcd;
	} 
	
	/**
	 * 저장위치 [산적 LOT 수정] 조회 (1후판정정야드)
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
		
		String 			szMsg 			= "";
		String 			szMethodName	="getPlateYdStkPosFix";
		
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("PlateReviseSeEJB", "getPlateYdStkPosFix", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			 
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			return gdRes;
			
		}catch(Exception e){
			szMsg = e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}	
		 
	}	// end of getPlateYdStkPosFix
	
	/**
	 * 1후판정정야드 PM45 저장위치 수정  
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */	
	public GridData updPlateYdStkPosFix(GridData inDto) throws DAOException {
		
		GridData gdRes 			= null;
		EJBConnector ejbConn 	= null;
		
		String szMsg 			= "";
		String szMethodName		= "updPlateYdStkPosFix";
		try{
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);
			
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
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
	 * 1후판정정야드 저장위치 수정  
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */	
	public GridData updpPlateYdStkPosFix(GridData inDto) throws DAOException {
		
		GridData gdRes 			= null;
		EJBConnector ejbConn 	= null;
		
		String szMsg 			= "";
		String szRtnMsg 		= "";
		String szMethodName		= "updpPlateYdStkPosFix";
		JDTORecord recTemp 		= null;
		JDTORecord recPara 		= null;
		
		JDTORecordSet recordSet1 = null;
		JDTORecordSet recordSet2 = null;
		JDTORecordSet recordSet3 = null;
		
		String szTemp 			 = null;
		
		int nRtnVal= 0;
		
		JDTORecordSet rsStock	= null;
		
		JPlateYdStockDAO ydDao 			= new JPlateYdStockDAO();
		YdStkLyrDao ydStkLyrDao 		= new YdStkLyrDao();
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		
		try{
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(inDto);
			
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
				nRtnVal  = ydDao.getYdStock(recPara, rsStock);
				
				if(nRtnVal <=0){
					
					szMsg = szTemp+" = 저장품에 데이터 신규생성";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					
					nRtnVal = ydDao.insYdStockBookOut(recPara);
				}
				
				//작업예약 재료확인
				recordSet1 = JDTORecordFactory.getInstance().createRecordSet("Yd");
				nRtnVal    = ydWrkbookMtlDao.getYdWrkbookmtl(recTemp, recordSet1, 2);
				
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
			}
			
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			ejbConn.trx("updpPlateYdStkPosFix", new Class[] { JDTORecord[].class }, 
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
	 * 저장위치 [PM45] 재료정보 요구 (1후판정정야드)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	
	public GridData sendPlateYdStkPosFix(GridData inDto) throws JDTOException {
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		
		String 			szMsg 			= "";
		String 			szMethodName	="sendPlateYdStkPosFix";
		
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			ejbConn.trx("PlateReviseSeEJB", "sendPlateYdStkPosFix", inRecord);

			gdRes = OperateGridData.cloneResponseGridData(inDto);
			
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			return gdRes;
			
		}catch(Exception e){
			szMsg = e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}	
		 
	}	// end of sendPlateYdStkPosFix
	
	/**
	 * 오퍼레이션명 : 1후판정정야드 I/F 수신
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws JDTOException
	 */
	public void rcvY9Interface(JDTORecord inRecord) throws JDTOException {
		//
		// 1후판정정야드 I/F 수신
		/*
			Y9YDL004	설비고장복구실적
			Y9YDL007	크레인작업지시요구 - YDYDJ720
			Y9YDL008	크레인권상실적
			Y9YDL009	크레인권하실적
			Y9YDL001	Book-In/Book-Out 실적
			Y9YDL002	저장품제원
			Y9YDL003	저장품제원요구
		*/
		//
		String szMsg="";
		String szMethodName="rcvY9Interface";
		
		/*
		 * SMS 전문은 ID가 29812/29814로 들어오기 때문에 유효성검사시 에러발생. 따라서 SKIP 
		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){

			szMsg=szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			return;
		}
		*/

		try {
			String szTcCode = ydDaoUtils.paraRecChkNull(inRecord, "MSG_ID");
			
			if("Y9YDL004".equals(szTcCode)){
				ydEjbCon.trx("PlateReviseSeEJB", "procY9EqpTrblRcvrWr", 	inRecord);
			}else if("Y9YDL007".equals(szTcCode)){
				ydEjbCon.trx("PlateReviseSeEJB", "procY9CrnWrkOrdReq", 		inRecord);
			}else if("Y9YDL008".equals(szTcCode)){
				ydEjbCon.trx("PlateReviseSeEJB", "procY9CrnLdWr", 			inRecord);
			}else if("Y9YDL009".equals(szTcCode)){
				ydEjbCon.trx("PlateReviseSeEJB", "procY9CrnUdWr", 			inRecord);
			}else if("Y9YDL001".equals(szTcCode)){
				ydEjbCon.trx("PlateReviseSeEJB", "procPm45BookInOutWslt", 	inRecord);
			}else if("Y9YDL002".equals(szTcCode)){
				ydEjbCon.trx("PlateReviseSeEJB", "procPm45LocInfo", 		inRecord);
			}else if("Y9YDL003".equals(szTcCode)){
				ydEjbCon.trx("PlateReviseSeEJB", "procPm45StlInfo", 		inRecord);	
			}else if("E5YDL001".equals(szTcCode)||"29812".equals(szTcCode)){ 
				ydEjbCon.trx("PlateReviseSeEJB", "procBookInReqInfo", 		inRecord);	
			}else if("E5YDL002".equals(szTcCode)||"29814".equals(szTcCode)){
				ydEjbCon.trx("PlateReviseSeEJB", "procBookOutReqInfo", 		inRecord);	
			} 
			 
		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

			throw new JDTOException(szMsg);
		} // end of try catch
		
		szMsg="1후판정정야드 I/F 수신 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	} // end of rcvY9Interface()
	
	/**
	 * 오퍼레이션명 : 1후판정정야드크레인스케줄Main
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inRecord
	 * @return
	 * @throws DAOException
	 * @weblogic.transaction-descriptor trans-timeout-seconds="180"
	 */
	public void rcvY9CrnSchMain(JDTORecord inRecord) throws DAOException {
		//
		// 1후판정정야드크레인스케줄Main
		// TC : YDYDJ710
		//
		//┏━┓
		//┃
		//┗━┛
		
		String szMsg="";
		String szMethodName="rcvY9CrnSchMain";
		
		if( !ydUtils.rcvMsgChk(inRecord, szSessionName, szMethodName)){			
			szMsg= szMethodName+"() 실행 실패";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
 
		try {
			ydEjbCon.trx("PlateReviseSeEJB", "procY9CrnSchMain", inRecord);

		} catch (Exception e) {			
			szMsg =szMethodName + "() " +e.getMessage(); 
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szMsg);
		} // end of try catch
		
		szMsg="1후판정정야드크레인스케줄Main 처리("+szMethodName+") 완료";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

	} // end of rcvY9CrnSchMain()
	
	/**
	 *  권하위치 변경 (크레인작업관리 화면)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData rcvY9CrnSchMain(GridData gdReq) throws JDTOException {
		
		String szLogMsg           = "";
		String szMethodName       = "rcvY9CrnSchMain";
		boolean bool              = false;
		Boolean boolWrapper       = null;

		GridData gdRes            = null;
		EJBConnector ejbConn      = null;
		
		try{
			
			szLogMsg = "JSP-FACADE [1후판정정야드크레인스케줄Main (크레인작업예약 화면)] 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			
			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(gdReq);
			
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			
			for(int idx = 0; idx < inRecord.length; idx++){			
				
				ejbConn.trx("procY9CrnSchMain", new Class[] { JDTORecord.class }, 
				                               new Object[] { inRecord[idx] });
			 
			}
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			szLogMsg = "크레인스케줄 등록 성공";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		}catch(Exception e){
			gdRes.setMessage("Failure");
			szLogMsg = "크레인스케줄 등록 실패";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		
		szLogMsg = "JSP-FACADE [1후판정정야드크레인스케줄Main (크레인작업예약 화면)] 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}  //end of rcvY9CrnSchMain
	
	/**
	 * 크레인상태관리 - 명령선택기동
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData updCmdSelStart(GridData gdReq) throws JDTOException {	
		String szLogMsg 		= null;
		String szRtnMsg 		= null;
		GridData gdRes 			= null;
		EJBConnector ejbConn 	= null;
		String szMethodName 	= "updCmdSelStart";
		
		try{
			szLogMsg = "JSP-FACADE [ 크레인상태관리 - 명령선택기동] 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);			
			szRtnMsg = (String)ejbConn.trx("updCmdSelStart", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
		
			gdRes = OperateGridData.cloneResponseGridData(gdReq);		
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			gdRes.setMessage(szRtnMsg);
			
		}catch(Exception e){
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
			gdRes.setMessage(szRtnMsg);
			szLogMsg = "[JSP Facade]명령선택기동 에러발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(),e);
		}
		
		szLogMsg = "JSP-FACADE [크레인상태관리 - 명령선택기동] ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
		
		gdRes.setStatus("true");
		return gdRes;
	} // end of updCmdSelStart()
	
	/**
	 *  야드크레인 작업관리 POP_UP (권상실적 처리)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData updCrnUpPrsBackUp(GridData gdReq) throws JDTOException {
	
		String szMethodName 	= "updCrnUpPrsBackUp";
		String szLogMsg 		= null;
		String szRtnMsg 		= null;
		GridData gdRes 			= null;
		EJBConnector ejbConn 	= null;

		try{
			
			szLogMsg = "JSP-FACADE [야드크레인 작업관리 POP_UP (권상실적 처리)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);			
			szRtnMsg = (String)ejbConn.trx("updCrnUpPrsBackUp", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			
			gdRes = OperateGridData.cloneResponseGridData(gdReq);		
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			gdRes.setMessage(szRtnMsg);
		}catch(Exception e){
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
			gdRes.setMessage(szRtnMsg);
			szLogMsg = "[JSP Facade]야드크레인작업관리 권상실적 처리 에러발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(),e);
		}
		gdRes.setStatus("true");
		
		szLogMsg = "JSP-FACADE [야드크레인 작업관리 POP_UP (권상실적 처리)] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}
	
	/**
	 *  야드크레인 작업관리 POP_UP (권하실적 처리)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData updCrnDnPrsBackUp(GridData gdReq) throws DAOException {
		
		String szMethodName 	= "updCrnDnPrsBackUp";
		String szLogMsg 		= null;
		String szRtnMsg 		= null;
		GridData gdRes 			= null;
		EJBConnector ejbConn 	= null;

		try{
			szLogMsg = "JSP-FACADE [야드크레인 작업관리 POP_UP (권하실적 처리)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);			
			szRtnMsg = (String)ejbConn.trx("updCrnDnPrsBackUp", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			
			gdRes = OperateGridData.cloneResponseGridData(gdReq);		
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			gdRes.setMessage(szRtnMsg);
		}catch(Exception e){
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
			gdRes.setMessage(szRtnMsg);
			szLogMsg = "[JSP Facade]야드크레인작업관리 권하실적 처리 에러발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		gdRes.setStatus("true");
		
		szLogMsg = "JSP-FACADE [야드크레인 작업관리 POP_UP (권하실적 처리)] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
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
	public GridData updToPosFix(GridData gdReq) throws JDTOException {
		
		String szLogMsg           = "";
		String szMethodName       = "updToPosFix";
		boolean bool              = false;
		Boolean boolWrapper       = null;

		GridData gdRes            = null;
		EJBConnector ejbConn      = null;
		
		try{
			
			szLogMsg = "JSP-FACADE [권하위치 변경 (크레인작업관리 화면)] 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			
			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(gdReq);
			
			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			boolWrapper = (Boolean)ejbConn.trx("updToPosFix", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			
			bool = boolWrapper.booleanValue();
			
			if(bool){
				gdRes.setStatus("true");
				gdRes.setMessage("Success");
				szLogMsg = "권하위치 변경 성공";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
			}else{
				gdRes.setStatus("true");
				gdRes.setMessage("Failure");
				szLogMsg = "권하위치 변경 실패";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.WARNING);
			}
			
		}catch(Exception e){
			gdRes.setMessage("Failure");
			szLogMsg = "권하위치 변경 실패";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		
		szLogMsg = "JSP-FACADE [권하위치 변경 (크레인작업관리 화면)] 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}  //end of updToPosFix
	

	/**
	 *  야드크레인 작업관리 (작업취소) 
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
		String szOperationName	  = "작업관리 (작업취소)";
		
		String sYD_CRN_SCH_ID  	= "";
		String sYD_SCH_CD 		= "";
		String sYD_USER_ID 		= "";
		JDTORecordSet rsRtnVal 	= JDTORecordFactory.getInstance().createRecordSet("temRs");
		JDTORecord recPara 		= JDTORecordFactory.getInstance().create();
		JDTORecord recCheck 	= JDTORecordFactory.getInstance().create();
		JDTORecord outRecord1 	= JDTORecordFactory.getInstance().create();
		JDTORecord recDelPara 	= JDTORecordFactory.getInstance().create();
		
		String sRTN_CD	= "";
		String sRTN_MSG	= "";
		
		String szJMS_TC_CD			= "";
		String szYD_EQP_ID			= "";
		String szYD_WRK_PROG_STAT 	= "";
		String szYD_SCH_CD  		= "";
		String szRTN_SND    		= "N";
		String sCANCEL_SEND 		= "N";
		
		YDDataUtil  yddatautil 		= new YDDataUtil();
		YdCrnSchDao  ydCrnSchDao    = new  YdCrnSchDao();
		
		GridData gdRes 				= null;
		EJBConnector ejbConn 		= null;
		
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
				sYD_SCH_CD 		= yddatautil.setDataDefault(inRecord[x].getField("YD_SCH_CD"), "");
				sYD_USER_ID 	= yddatautil.setDataDefault(inRecord[x].getField("YD_USER_ID"), "");
				
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
				rsRtnVal 	= JDTORecordFactory.getInstance().createRecordSet("temRs");
				// com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getCheckYdCrnSchId
				intRtnVal 	= ydCrnSchDao.getYdCrnsch(recPara, rsRtnVal, 36);
				
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
				
				szMsg = "스케쥴 취소 시작!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
				ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);			
				outRecord1 	= (JDTORecord)ejbConn.trx("PlateSchCncl", new Class[] { JDTORecord.class }, new Object[] { recPara });

				sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
				sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
				sCANCEL_SEND= StringHelper.evl(outRecord1.getFieldString("CANCEL_SEND"), "N");
				if ("0".equals(sRTN_CD)) {
					gdRes.setMessage(sRTN_MSG);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}		

				szMsg = "스케쥴 취소 종료!! 작업예약 취소 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);			
				outRecord1 	= (JDTORecord)ejbConn.trx("PlateDelWBook", new Class[] { JDTORecord.class }, new Object[] { outRecord1 });

				szMsg = "작업예약 취소 종료!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				sRTN_CD				= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
				szYD_EQP_ID			= StringHelper.evl(outRecord1.getFieldString("YD_EQP_ID"), "");
				szYD_WRK_PROG_STAT	= StringHelper.evl(outRecord1.getFieldString("YD_WRK_PROG_STAT"), "");
				szYD_SCH_CD			= StringHelper.evl(outRecord1.getFieldString("YD_SCH_CD"), "");
				szRTN_SND			= StringHelper.evl(outRecord1.getFieldString("RTN_SND"), "N");

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
					recDelPara.setField("JMS_TC_CD",        	 "YDYDJ720");
					recDelPara.setField("MSG_ID"				, "Y9YDL007"        );
					recDelPara.setField("YD_EQP_ID"				, szYD_EQP_ID            );					   
					recDelPara.setField("YD_WRK_PROG_STAT"		, szYD_WRK_PROG_STAT);
					recDelPara.setField("YD_SCH_CD"				, szYD_SCH_CD );  
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
	 *  야드크레인 작업관리 (스케줄 취소)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * 
	 */
	public GridData cancleSchPlateYdCrnWorkMgt(GridData gdReq) throws JDTOException {
	
		String szMethodName		= "cancleSchPlateYdCrnWorkMgt";
		String szR_msg 			= "";
		String szLogMsg 		= "";
		String szOperationName	= "작업관리 (스케줄 취소)";
		
		//파라미터 스크링 변수
		
		String sYD_CRN_SCH_ID  	= "";
		String sYD_SCH_CD 		= "";
		String sYD_USER_ID 		= "";
		JDTORecordSet rsRtnVal 	= JDTORecordFactory.getInstance().createRecordSet("temRs");
		JDTORecord recPara 		= JDTORecordFactory.getInstance().create();
		JDTORecord recCheck 	= JDTORecordFactory.getInstance().create();
		JDTORecord outRecord1 	= JDTORecordFactory.getInstance().create();
		JDTORecord recEqpPara 	= JDTORecordFactory.getInstance().create();
		
		String sRTN_CD			= "";
		String sRTN_MSG			= "";
		
		String szRtnMsg			= "";
		String szYD_EQP_ID		= "";
		String sCANCEL_SEND 	= "N";
		YDDataUtil  yddatautil 	= new YDDataUtil();
		YdCrnSchDao  ydCrnSchDao= new  YdCrnSchDao();
		GridData gdRes 			= null;
		EJBConnector ejbConn 	= null;
		int intRtnVal	 		= 0;
		String szMsg 			= "";
		String szC_YD_WRK_PROG_STAT = "";
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			gdRes = OperateGridData.cloneResponseGridData(gdReq);		
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);
			
			for(int x=0;x<inRecord.length;x++){			
				
				sYD_CRN_SCH_ID  = yddatautil.setDataDefault(inRecord[x].getField("YD_CRN_SCH_ID"), "");
				sYD_SCH_CD 		= yddatautil.setDataDefault(inRecord[x].getField("YD_SCH_CD"), "");
				sYD_USER_ID 	= yddatautil.setDataDefault(inRecord[x].getField("YD_USER_ID"), "");

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
				rsRtnVal 	= JDTORecordFactory.getInstance().createRecordSet("temRs");
				// com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getCheckYdCrnSchId
				intRtnVal 	= ydCrnSchDao.getYdCrnsch(recPara, rsRtnVal, 36);
				
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
				
				szMsg = "스케쥴 취소 시작!!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
				ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);			
				outRecord1 	= (JDTORecord)ejbConn.trx("PlateSchCncl", new Class[] { JDTORecord.class }, new Object[] { recPara });

				sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
				sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
				sCANCEL_SEND= StringHelper.evl(outRecord1.getFieldString("CANCEL_SEND"), "N");
				szYD_EQP_ID	= StringHelper.evl(outRecord1.getFieldString("YD_EQP_ID"), "");
							
				
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
						recEqpPara.setField("YD_EQP_ID"		, szYD_EQP_ID);
						recEqpPara.setField("YD_EQP_STAT"	, YdConstant.YD_EQP_STAT_IDLE);
						recEqpPara.setField("MODIFIER"		,sYD_USER_ID);
						
						szMsg="[Jsp-Session " + szOperationName+ " ] 크레인("+ szYD_EQP_ID +") 설비상태 [" + YdConstant.YD_EQP_STAT_IDLE +"]로 변경 ------------------";				
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
					    EJBConnector ejbConn2 = new EJBConnector("default","PlateReviseSeEJB",this);
					    Boolean isSuccess = (Boolean)ejbConn2.trx("RequiresUpdYdEqp",new Class[]{JDTORecord.class}, new Object[]{recEqpPara});
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
	 * [1후판정정야드] 이적대상재 등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData insMvWBookId(GridData inDto) throws DAOException {
		//LOG
		String rtnMsg 			= JPlateYdConst.RETN_CD_SUCCESS;
		String szMethodName		= "insMvWBookId";
		String szLogMsg 		= "";
		String szOperationName	= "이적대상재 등록 처리";

		GridData     gdRes 		= null;
		EJBConnector ejbConn 	= null;
		
		// Session Name
		String SZ_SESSION_NAME = PlateReviseFaEJB.class.getName();

		try {

			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			JDTORecord[] inRecord = ydComUtil.genGridToJDTORecordAll(inDto);

			ejbConn = new EJBConnector("default", "PlateReviseSeEJB", this);
			rtnMsg  = (String)ejbConn.trx("insMvWBookId", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

			gdRes 	= OperateGridData.cloneResponseGridData(inDto);
			gdRes   = CmUtil.copyGDParam(inDto, gdRes);
			gdRes.setMessage(rtnMsg);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(rtnMsg)) {
				szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] 오류발생 >>>> " + rtnMsg;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
				m_ctx.setRollbackOnly();
				gdRes.setStatus("false");
				return gdRes;
			}

		} catch(Exception e) {
			rtnMsg = JPlateYdConst.RETN_CD_FAILURE;
			gdRes.setMessage(rtnMsg);
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, "[JSP-FACADE  - "+ szOperationName  + "]" + e.getMessage(), JPlateYdConst.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");

		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝 >>>> " + rtnMsg;
		ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return gdRes;
	}

}//end Class






