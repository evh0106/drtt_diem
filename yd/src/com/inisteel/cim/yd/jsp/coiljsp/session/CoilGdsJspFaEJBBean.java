package com.inisteel.cim.yd.jsp.coiljsp.session;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.log.LogService;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;
import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.common.util.CmUtil;
import com.inisteel.cim.yd.common.dao.ydTcarSchDao.YdTcarSchDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookDao.YdWrkbookDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdSlabUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO;
import com.inisteel.cim.yd.jsp.common.CmnUtil;
import com.inisteel.cim.yd.jsp.common.YDComUtil;
import com.inisteel.cim.yd.jsp.common.YDDataUtil;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;



/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Facade Session EJB클래스입니다.
 *
 * @ejb.bean name="CoilGdsJspFaEJB" jndi-name="CoilGdsJspFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class CoilGdsJspFaEJBBean extends BaseSessionBean {

	
	private YdUtils ydUtils = new YdUtils();
	
	YDComUtil   ydComUtil = new YDComUtil();
	private String szSessionName = getClass().getName();
	private Logger log = LogService.getInstance().getLogger("CoilYdTcarStsSet");
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();	
	private YdSlabUtils slabUtils = new YdSlabUtils();
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	
	/**
	 * 코일제품창고 작업실적일품 조회 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilGdsYdWrkRsltDdArtcl(GridData inDto) throws DAOException {
					
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		String szMethodName = "getCoilGdsYdWrkRsltDdArtcl";
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			
			ejbConn = new EJBConnector("default", this);

			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilGdsYdWrkRsltDdArtcl", inRecord);
			                                                          
			gdRes = CmUtil.genGridData(inDto , recordSet);

		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilGdsYdWrkRsltDdArtcl
	
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
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdColStkPosList", inRecord);
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
	 * 저장위치별 재고 List 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdStkPosList(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdStkPosList";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdStkPosList", inRecord);
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
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			ejbConn.trx("updCoilYdColStkPosList", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
			
		}		
		return gdRes;
	}  //end of updCoilGdsYdColStkPosList
	
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
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			ejbConn.trx("insCoilYdColStkPosList", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
			
		}
		return gdRes;
	}  //end of insCoilGdsYdColStkPosList
	
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

			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdRetCrnReg", inRecord);
			                                                          
			gdRes = CmUtil.genGridData(inDto , recordSet);

		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilYdRetCrnReg

	/**
	 * 반납크레인대상조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	
	public GridData getCoilYdRetCrnReg1(GridData inDto) throws DAOException {
					
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		String szMethodName = "getCoilYdRetCrnReg1";
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			
			ejbConn = new EJBConnector("default", this);

			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdRetCrnReg1", inRecord);
			                                                          
			gdRes = CmUtil.genGridData(inDto , recordSet);

		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilYdRetCrnReg1


	/**
	 * 지포장대상조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	
	public GridData getCoilYdSendGF(GridData inDto) throws DAOException {
					
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		String szMethodName = "getCoilYdSendGF";
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			
			ejbConn = new EJBConnector("default", this);

			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdSendGF", inRecord);
			                                                          
			gdRes = CmUtil.genGridData(inDto , recordSet);

		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilYdSendGF


	/**
	 * 지포장 등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilYdSendGF(GridData gdReq) throws DAOException {
		//LOG
		JPlateYdCommDAO ydCommDao = new JPlateYdCommDAO();
		
		String szMsg="";
		String szMethodName = "updCoilYdSendGF";
		JDTORecord outRecord2   	= JDTORecordFactory.getInstance().create();			
		JDTORecord inRecord   		= JDTORecordFactory.getInstance().create();			
		JDTORecord inRecord1   		= JDTORecordFactory.getInstance().create();			
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet rsResult     		= null;
		String sQueryId			= "";
		String sYD_WBOOK_ID 	= "";
		String sYD_SCH_CD 		= "";
		String sYD_USER_ID 		= "";
		String sYD_STK_COL_GP 	= "";
		String sSTL_NO 			= "";
		String sYD_AIM_BAY_GP 	= "";
		String sFirstYD_AIM_BAY_GP = "";
		String sFirstYD_STK_COL_GP = "";
		String sFirstYD_SCH_CD ="";
		String sFirstYD_WBOOK_ID = "";
		String sRTN_CD	= "";
		String sRTN_MSG	= "";
		
		
		int intRtnVal 		= 0;
		int intStlSchCnt 	= 0;
		
		try{
			JDTORecord [] inRecordSet = ydComUtil.genGridToJDTORecordAll(gdReq);
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			
			for(int Loopi = 0; Loopi < inRecordSet.length; Loopi++) {
				
				sYD_USER_ID 	= ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "YD_USER_ID");
				sYD_STK_COL_GP	= ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "YD_STK_COL_GP");
				sSTL_NO			= ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "STL_NO");
				sYD_AIM_BAY_GP  = ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "YD_AIM_BAY_GP");   // 입력동
				
				inRecord1 = JDTORecordFactory.getInstance().create();
				inRecord1.setField("STL_SH"						, "1");  
				inRecord1.setField("STL_NO1"					, sSTL_NO);
				inRecord1.setField("YD_UP_COLL_SEQ1"			, "1");  
				inRecord1.setField("YD_USER_ID"					, sYD_USER_ID);  
				inRecord1.setField("YD_AIM_BAY_GP"				, sYD_AIM_BAY_GP); 
				inRecord1.setField("YD_AIM_YD_GP"				, "H");
				inRecord1.setField("YD_TO_LOC_DCSN_MTD",		"S");
				inRecord1.setField("TO_YD_STK_BED_NO",			"");
				
				if(sYD_STK_COL_GP.substring(1, 2).equals("B")||sYD_STK_COL_GP.substring(1, 2).equals("C")
					|| sYD_STK_COL_GP.substring(1, 2).equals("E")||sYD_STK_COL_GP.substring(1, 2).equals("H") ){
					
					sYD_SCH_CD = "H" + sYD_STK_COL_GP.substring(1, 2) + "GF01UM";  //지포장 SCH_CD
						
				} else {
					gdRes.setMessage("B,C,E,H동 이외의 동은 동간이적후 지포장을 선택해 주시기 바랍니다.");		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}
				inRecord1.setField("YD_SCH_CD"					, sYD_SCH_CD);
				
				ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
				outRecord2 = (JDTORecord)ejbConn.trx("WrkBookInsertProc", new Class[] { JDTORecord.class }, new Object[] { inRecord1 });
				sRTN_CD		= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
				sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
				sYD_WBOOK_ID= StringHelper.evl(outRecord2.getFieldString("YD_WBOOK_ID"), "");
				sYD_SCH_CD	= StringHelper.evl(outRecord2.getFieldString("YD_SCH_CD"), "");
				if (!("1".equals(sRTN_CD))) {
					gdRes.setMessage(sRTN_MSG);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				} else {
					
					intStlSchCnt++;
					if(sFirstYD_WBOOK_ID.equals("")) { 
						
						sFirstYD_WBOOK_ID = sYD_WBOOK_ID;
						sFirstYD_SCH_CD   = sYD_SCH_CD;
						sFirstYD_AIM_BAY_GP = sYD_STK_COL_GP.substring(1, 2);
						sFirstYD_STK_COL_GP = sYD_STK_COL_GP;
					}	
					gdRes.setMessage(sRTN_MSG + " <br> 정상적으로 작업예약까지 등록했습니다.");		
		
				} 	
				szMsg = "스케줄 코드 :"+ sFirstYD_SCH_CD;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
			}	
			
			
			if(!sFirstYD_WBOOK_ID.equals("")) {
				

//				151215 hun 지포장장 To위치 가능 여부 체크 start
				ydUtils.putLog(szSessionName, szMethodName, "★ 지포장장 To위치 가능 여부 체크 start★", YdConstant.INFO);
				rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
			    inRecord = JDTORecordFactory.getInstance().create();
			    inRecord.setField("YD_BAY_GP",           sFirstYD_AIM_BAY_GP);
			    inRecord.setField("YD_STK_COL_GP",           sFirstYD_STK_COL_GP);
			    sQueryId = "com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdLocByGFplace";
			    intRtnVal = ydCommDao.select(inRecord, rsResult, sQueryId);
			    
			    if (intRtnVal == 0) {
					gdRes.setMessage("지포장장 적재 불가능. 지포장장을 비워주시기 바랍니다.");
					return gdRes;	
				}	
			    ydUtils.putLog(szSessionName, szMethodName, "★ 지포장장 To위치 가능 여부 체크 end★", YdConstant.INFO);
				

//				151217 hun 지포장 스케줄기동 여부 체크 start
				ydUtils.putLog(szSessionName, szMethodName, "★ 지포장 스케줄기동 여부 체크 start★", YdConstant.INFO);
				rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
			    inRecord = JDTORecordFactory.getInstance().create();
			    inRecord.setField("YD_BAY_GP",           sFirstYD_AIM_BAY_GP);
			    sQueryId = "com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdLocByGFExSCH";
			    intRtnVal = ydCommDao.select(inRecord, rsResult, sQueryId);
			    
			    if (intRtnVal > 0) {
					gdRes.setMessage("지포장 보급 스케줄이 존재하므로 작업예약만 등록합니다.");
					return gdRes;	
				}	
			    ydUtils.putLog(szSessionName, szMethodName, "★ 지포장 스케줄기동 여부 체크 end★", YdConstant.INFO);
				
			    
				JDTORecord[] inRecordarr   	= null;
				inRecordarr = new JDTORecord[1];
				
				inRecordarr[0] = JDTORecordFactory.getInstance().create();
				inRecordarr[0].setField("YD_SCH_CD"		, sFirstYD_SCH_CD); 
				inRecordarr[0].setField("YD_WBOOK_ID"	, sFirstYD_WBOOK_ID); 
				ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
				outRecord2 = (JDTORecord) ejbConn.trx("trxRunSchedule", new Class[] { JDTORecord[].class },	new Object[] { inRecordarr });
	
				sRTN_CD		= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "");
				sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
				ydUtils.putLog(szSessionName, szMethodName, "마지막 RETURN:" + sRTN_MSG, YdConstant.DEBUG);
				if (!("1".equals(sRTN_CD))) {
					gdRes.setMessage(sRTN_MSG);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				} else {
					gdRes.setMessage(sRTN_MSG + " <br> 정상적으로 스케쥴까지 등록했습니다.");		
				}
			}
			
			gdRes.setMessage("정상적으로 지포장보급 등록했습니다");		
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		return gdRes;
	}  //end of updCoilYdSendGF


	/**
	 * 지포장 등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilYdSendGFEmergency(GridData gdReq) throws DAOException {
		//LOG
		JPlateYdCommDAO ydCommDao = new JPlateYdCommDAO();
		
		String szMsg="";
		String szMethodName = "updCoilYdSendGFEmergency";
		JDTORecord outRecord2   	= JDTORecordFactory.getInstance().create();			
		JDTORecord inRecord   		= JDTORecordFactory.getInstance().create();			
		JDTORecord inRecord1   		= JDTORecordFactory.getInstance().create();			
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet rsResult     		= null;
		String sQueryId			= "";
		String sYD_WBOOK_ID 	= "";
		String sYD_SCH_CD 		= "";
		String sYD_USER_ID 		= "";
		String sYD_STK_COL_GP 	= "";
		String sSTL_NO 			= "";
		String sYD_AIM_BAY_GP 	= "";
		String sFirstYD_WBOOK_ID = "";
		String sFirstYD_AIM_BAY_GP = "";
		String sFirstYD_STK_COL_GP = "";
		String sFirstYD_SCH_CD ="";
		String sRTN_CD	= "";
		String sRTN_MSG	= "";
		
		int intRtnVal 		= 0;
		int intStlSchCnt 	= 0;
		
		try{
			
			
			JDTORecord [] inRecordSet =  ydComUtil.genGridToJDTORecordAll(gdReq);
			
			
			szMsg = "[JSP Facade] 반송긴급재  시작  ==>";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			outRecord2 = (JDTORecord)ejbConn.trx("updCoilYdemergencyMgt", new Class[] { JDTORecord[].class }, new Object[] { inRecordSet });

			sRTN_CD	= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
			sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
			if ("0".equals(sRTN_CD)) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
				return gdRes;	
			}	

			
			//JDTORecord [] inRecordSet = ydComUtil.genGridToJDTORecordAll(gdReq);
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			
			for(int Loopi = 0; Loopi < inRecordSet.length; Loopi++) {
				
				sYD_USER_ID 	= ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "YD_USER_ID");
				sYD_STK_COL_GP	= ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "YD_STK_COL_GP");
				sSTL_NO			= ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "STL_NO");
				sYD_AIM_BAY_GP  = ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "YD_AIM_BAY_GP");   // 입력동
				
				inRecord1 = JDTORecordFactory.getInstance().create();
				inRecord1.setField("STL_SH"						, "1");  
				inRecord1.setField("STL_NO1"					, sSTL_NO);
				inRecord1.setField("YD_UP_COLL_SEQ1"			, "1");  
				inRecord1.setField("YD_USER_ID"					, sYD_USER_ID);  
				inRecord1.setField("YD_AIM_BAY_GP"				, sYD_AIM_BAY_GP); 
				inRecord1.setField("YD_AIM_YD_GP"				, "H");
				inRecord1.setField("YD_TO_LOC_DCSN_MTD",		"S");
				inRecord1.setField("TO_YD_STK_BED_NO",			"");
				
				if(sYD_STK_COL_GP.substring(1, 2).equals("B")||sYD_STK_COL_GP.substring(1, 2).equals("C")
					|| sYD_STK_COL_GP.substring(1, 2).equals("E")||sYD_STK_COL_GP.substring(1, 2).equals("H") ){
					
					sYD_SCH_CD = "H" + sYD_STK_COL_GP.substring(1, 2) + "GF01UM";  //지포장 SCH_CD
						
				} else {
					gdRes.setMessage("B,C,E,H동 이외의 동은 동간이적후 지포장을 선택해 주시기 바랍니다.");		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}
				inRecord1.setField("YD_SCH_CD"					, sYD_SCH_CD);
				
				ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
				outRecord2 = (JDTORecord)ejbConn.trx("WrkBookInsertProc", new Class[] { JDTORecord.class }, new Object[] { inRecord1 });
				sRTN_CD		= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
				sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
				sYD_WBOOK_ID= StringHelper.evl(outRecord2.getFieldString("YD_WBOOK_ID"), "");
				sYD_SCH_CD	= StringHelper.evl(outRecord2.getFieldString("YD_SCH_CD"), "");
				if (!("1".equals(sRTN_CD))) {
					gdRes.setMessage(sRTN_MSG);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				} else {
					
					intStlSchCnt++;
					if(sFirstYD_WBOOK_ID.equals("")) { 
						
						sFirstYD_WBOOK_ID = sYD_WBOOK_ID;
						sFirstYD_SCH_CD   = sYD_SCH_CD;
						sFirstYD_AIM_BAY_GP = sYD_STK_COL_GP.substring(1, 2);
						sFirstYD_STK_COL_GP = sYD_STK_COL_GP;
					}	
					gdRes.setMessage(sRTN_MSG + " <br> 정상적으로 작업예약까지 등록했습니다.");		
		
				} 	
				szMsg = "스케줄 코드 :"+ sFirstYD_SCH_CD;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
			}	
			
			
			if(!sFirstYD_WBOOK_ID.equals("")) {
				
//				151215 hun 지포장장 To위치 가능 여부 체크 start
				ydUtils.putLog(szSessionName, szMethodName, "★ 지포장장 To위치 가능 여부 체크 start★", YdConstant.INFO);
				rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
			    inRecord = JDTORecordFactory.getInstance().create();
			    inRecord.setField("YD_BAY_GP",           sFirstYD_AIM_BAY_GP);
			    inRecord.setField("YD_STK_COL_GP",           sFirstYD_STK_COL_GP);
			    sQueryId = "com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdLocByGFplace";
			    intRtnVal = ydCommDao.select(inRecord, rsResult, sQueryId);
			    
			    if (intRtnVal == 0) {
					gdRes.setMessage("지포장장 적재 불가능. 지포장장을 비워주시기 바랍니다.");
					return gdRes;	
				}	
			    ydUtils.putLog(szSessionName, szMethodName, "★ 지포장장 To위치 가능 여부 체크 end★", YdConstant.INFO);
				

//				151217 hun 지포장 스케줄기동 여부 체크 start
				ydUtils.putLog(szSessionName, szMethodName, "★ 지포장 스케줄기동 여부 체크 start★", YdConstant.INFO);
				rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
			    inRecord = JDTORecordFactory.getInstance().create();
			    inRecord.setField("YD_BAY_GP",           sFirstYD_AIM_BAY_GP);
			    sQueryId = "com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdLocByGFExSCH";
			    intRtnVal = ydCommDao.select(inRecord, rsResult, sQueryId);
			    
			    if (intRtnVal > 0) {
					gdRes.setMessage("지포장 보급 스케줄이 존재하므로 작업예약만 등록합니다.");
					return gdRes;	
				}	
			    ydUtils.putLog(szSessionName, szMethodName, "★ 지포장 스케줄기동 여부 체크 end★", YdConstant.INFO);
				
				JDTORecord[] inRecordarr   	= null;
				inRecordarr = new JDTORecord[1];
				
				inRecordarr[0] = JDTORecordFactory.getInstance().create();
				inRecordarr[0].setField("YD_SCH_CD"		, sFirstYD_SCH_CD); 
				inRecordarr[0].setField("YD_WBOOK_ID"	, sFirstYD_WBOOK_ID); 
				ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
				outRecord2 = (JDTORecord) ejbConn.trx("trxRunSchedule", new Class[] { JDTORecord[].class },	new Object[] { inRecordarr });
	
				sRTN_CD		= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "");
				sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
				ydUtils.putLog(szSessionName, szMethodName, "마지막 RETURN:" + sRTN_MSG, YdConstant.DEBUG);
				if (!("1".equals(sRTN_CD))) {
					gdRes.setMessage(sRTN_MSG);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				} else {
					gdRes.setMessage(sRTN_MSG + " <br> 정상적으로 스케쥴까지 등록했습니다.");		
				}
			}
			
			gdRes.setMessage("정상적으로 지포장보급 등록했습니다");		
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		return gdRes;
	}  //end of updCoilYdSendGFEmergency
	
	/**
	 * 반납크레인 수정[공장공정코드]
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilYdRetCrnReg(GridData gdReq) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName = "updCoilYdRetCrnReg";
		JDTORecord outRecord2   	= JDTORecordFactory.getInstance().create();			
		JDTORecord inRecord   		= JDTORecordFactory.getInstance().create();			
		JDTORecord inRecord1   		= JDTORecordFactory.getInstance().create();			
		JDTORecord inRecord2   		= JDTORecordFactory.getInstance().create();			
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		YdTcarSchDao ydTcarSchDao  = new YdTcarSchDao();
		
		String sPROG_CD 		= "";
		String sYD_WBOOK_ID 	= "";
		String sYD_SCH_CD 		= "";
		String sYD_USER_ID 		= "";
		String sYD_STK_COL_GP 	= "";
		String sSTL_NO 			= "";
		String sYD_AIM_BAY_GP 	= "";
		String szTCar         	= "";
		String sFirstYD_WBOOK_ID = "";
		String sYD_CURR_BAY_GP  = "";
		JDTORecordSet rsGetStock1     = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecord recGetVal1     	= null;
		String sRTN_CD	= "";
		String sRTN_MSG	= "";

		int intStlCnt 		= 0;
		int intRtnVal 		= 0;
		int intStlSchCnt 	= 0;
		int iLOC_CNT     	= 0;
		
		try{
			JDTORecord [] inRecordSet = ydComUtil.genGridToJDTORecordAll(gdReq);
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);

			sPROG_CD	=  ydDaoUtils.paraRecChkNull(inRecordSet[0], "RETURN_GP");
			
			for(int Loopi = 0; Loopi < inRecordSet.length; Loopi++) {
				
				sYD_USER_ID 	= ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "YD_USER_ID");
				sYD_STK_COL_GP	= ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "YD_STK_COL_GP");
				sSTL_NO			= ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "STL_NO");
				sPROG_CD		= ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "RETURN_GP");
				sYD_AIM_BAY_GP  = ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "YD_AIM_BAY_GP");
				
				inRecord	= JDTORecordFactory.getInstance().create();
				inRecord.setField("STL_NO"			, sSTL_NO);
				inRecord.setField("YD_AIM_BAY_GP"	, sYD_AIM_BAY_GP); 
				inRecord.setField("YD_AIM_YD_GP"	, "H"); 
				inRecord.setField("YD_USER_ID"		, sYD_USER_ID);
				
				// 목표동 수정
				ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
				outRecord2 = (JDTORecord)ejbConn.trx("updCoilYdRetCrnReg",
							new Class[] { JDTORecord.class }, new Object[] { inRecord });
				sRTN_CD	= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
				sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
				if ("0".equals(sRTN_CD)) {
					gdRes.setMessage(sRTN_MSG);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}	
				
			
//					sYD_SCH_CD = "H" + sYD_STK_COL_GP.substring(1, 2) + "YD03UM";
					
				inRecord1 = JDTORecordFactory.getInstance().create();
//					inRecord1.setField("YD_SCH_CD"					, sYD_SCH_CD);
				inRecord1.setField("STL_SH"						, "1");  
				inRecord1.setField("STL_NO1"					, sSTL_NO);
				inRecord1.setField("YD_UP_COLL_SEQ1"			, "1");  
				inRecord1.setField("YD_USER_ID"					, sYD_USER_ID);  
				inRecord1.setField("YD_AIM_BAY_GP"				, sYD_AIM_BAY_GP); 
				inRecord1.setField("YD_AIM_YD_GP"				, "H");
				
				if(sYD_STK_COL_GP.substring(1, 2).equals(sYD_AIM_BAY_GP)) { 
					inRecord1.setField("YD_TO_LOC_DCSN_MTD",		"S");
					inRecord1.setField("TO_YD_STK_BED_NO",			"");
					
					if(sPROG_CD.equals("J")||sPROG_CD.equals("5") ) {  // 반납
						sYD_SCH_CD = "H" + sYD_STK_COL_GP.substring(1, 2) + "YD03UM";  //반납SCH_CD
					} else {
						sYD_SCH_CD = "H" + sYD_STK_COL_GP.substring(1, 2) + "YD04UM";  //반송SCH_CD
					}	
					inRecord1.setField("YD_SCH_CD"					, sYD_SCH_CD);
					
				} else {
					
					rsGetStock1 = JDTORecordFactory.getInstance().createRecordSet("");
					inRecord2 = JDTORecordFactory.getInstance().create();

					inRecord2.setField("FR_YD_BAY_GP",       sYD_STK_COL_GP.substring(1, 2));
					inRecord2.setField("TO_YD_BAY_GP",       sYD_AIM_BAY_GP);  
					
					/*com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.getYdTcarStat*/
					intRtnVal = ydTcarSchDao.getYdTcarsch(inRecord2, rsGetStock1, 202);
					
					if(intRtnVal <= 0){
						szMsg = "사용가능한 대차가 없습니다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						gdRes.setMessage(szMsg);		
						m_ctx.setRollbackOnly();
						return gdRes;	
					} else {
						// 커서 처음으로
						rsGetStock1.first();
						recGetVal1 = rsGetStock1.getRecord();	
						szTCar 			= ydDaoUtils.paraRecChkNull(recGetVal1,"YD_EQP_ID");
						iLOC_CNT 		= ydDaoUtils.paraRecChkNullInt(recGetVal1, "LOC_CNT");  // 적치가능 
						sYD_CURR_BAY_GP	= ydDaoUtils.paraRecChkNull(recGetVal1, "YD_CURR_BAY_GP");  // 대차현위치
						ydUtils.putLog(szSessionName, szMethodName, "szTCar:" + szTCar +  "iLOC_CNT:" + ""+ iLOC_CNT, YdConstant.DEBUG);
					}	
					
					if(sPROG_CD.equals("J")||sPROG_CD.equals("5")) {  // 반납
						sYD_SCH_CD = "H" + sYD_STK_COL_GP.substring(1, 2) +  szTCar.substring(2,6) + "UJ";  //반납SCH_CD
					} else {
						sYD_SCH_CD = "H" + sYD_STK_COL_GP.substring(1, 2) +  szTCar.substring(2,6) + "UB";  //반송SCH_CD
					}	
					inRecord1.setField("YD_SCH_CD",	 				sYD_SCH_CD);
					inRecord1.setField("YD_TO_LOC_DCSN_MTD",		"F");
					inRecord1.setField("TO_YD_STK_BED_NO",			"H" + sYD_STK_COL_GP.substring(1, 2) + szTCar.substring(2,6));
					inRecord1.setField("YD_WRK_PLAN_TCAR", 			szTCar);  //권상모음순서
				}	

				ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
				outRecord2 = (JDTORecord)ejbConn.trx("WrkBookInsertProc", new Class[] { JDTORecord.class }, new Object[] { inRecord1 });
				sRTN_CD		= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
				sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
				sYD_WBOOK_ID= StringHelper.evl(outRecord2.getFieldString("YD_WBOOK_ID"), "");
				sYD_SCH_CD	= StringHelper.evl(outRecord2.getFieldString("YD_SCH_CD"), "");
				if (!("1".equals(sRTN_CD))) {
					gdRes.setMessage(sRTN_MSG);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				} else {
					
					intStlSchCnt++;
//					if(intStlSchCnt == 1) { 
					if(sFirstYD_WBOOK_ID.equals("")) { 
						if(!sYD_SCH_CD.substring(2, 4).equals("TC")) {
							sFirstYD_WBOOK_ID = sYD_WBOOK_ID;
						} else if (sYD_SCH_CD.substring(2, 4).equals("TC") && (iLOC_CNT > 0) && (sYD_STK_COL_GP.substring(1, 2).equals(sYD_CURR_BAY_GP))) {
							sFirstYD_WBOOK_ID = sYD_WBOOK_ID;
						}
					}	
					gdRes.setMessage(sRTN_MSG + " <br> 정상적으로 작업예약까지 등록했습니다.");		
		
				} 	
				szMsg = "스케줄 코드 :"+ sYD_SCH_CD;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
			}	
			
			if(!sFirstYD_WBOOK_ID.equals("")) {
				
				JDTORecord[] inRecordarr   	= null;
				inRecordarr = new JDTORecord[1];
				
				inRecordarr[0] = JDTORecordFactory.getInstance().create();
				inRecordarr[0].setField("YD_SCH_CD"		, sYD_SCH_CD); 
				inRecordarr[0].setField("YD_WBOOK_ID"	, sFirstYD_WBOOK_ID); 
				ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
				outRecord2 = (JDTORecord) ejbConn.trx("trxRunSchedule", new Class[] { JDTORecord[].class },	new Object[] { inRecordarr });
	
				sRTN_CD		= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "");
				sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
				ydUtils.putLog(szSessionName, szMethodName, "마지막 RETURN:" + sRTN_MSG, YdConstant.DEBUG);
				if (!("1".equals(sRTN_CD))) {
					gdRes.setMessage(sRTN_MSG);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				} else {
					gdRes.setMessage(sRTN_MSG + " <br> 정상적으로 스케쥴까지 등록했습니다.");		
				}
			}
			
			gdRes.setMessage("정상적으로 반납/반송처리 등록했습니다");		
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		return gdRes;
	}  //end of updCoilYdRetCrnReg

	/**
	 * 반납반송정보[공장공정코드]
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilYdRetCrnReg1(GridData gdReq) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName = "updCoilYdRetCrnReg1";
		JDTORecord outRecord2   	= JDTORecordFactory.getInstance().create();			
		JDTORecord inRecord   		= JDTORecordFactory.getInstance().create();			
		JDTORecord inRecord1   		= JDTORecordFactory.getInstance().create();			
		JDTORecord inRecord2   		= JDTORecordFactory.getInstance().create();			
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		YdTcarSchDao ydTcarSchDao  = new YdTcarSchDao();
		JPlateYdCommDAO ydCommDao = new JPlateYdCommDAO();
		
		JDTORecordSet rsResult     		= null;
		String sQueryId			= "";
		String sPROG_CD 		= "";
		String sYD_WBOOK_ID 	= "";
		String sYD_SCH_CD 		= "";
		String sYD_USER_ID 		= "";
		String sYD_STK_COL_GP 	= "";
		String sSTL_NO 			= "";
		String sYD_AIM_BAY_GP 	= "";
		String szTCar         	= "";
		String sFirstYD_WBOOK_ID = "";
		String sYD_CURR_BAY_GP  = "";
		JDTORecordSet rsGetStock1     = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecord recGetVal1     	= null;
		String sRTN_CD	= "";
		String sRTN_MSG	= "";
		String sWO_CAR_PLNT_PROC_CD ="";
		String sFirstYD_SCH_CD ="";
		
		int intStlCnt 		= 0;
		int intRtnVal 		= 0;
		int intStlSchCnt 	= 0;
		int iLOC_CNT     	= 0;
		
		try{
			JDTORecord [] inRecordSet = ydComUtil.genGridToJDTORecordAll(gdReq);
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);

			sPROG_CD	=  ydDaoUtils.paraRecChkNull(inRecordSet[0], "CURR_PROG_CD");
			
			for(int Loopi = 0; Loopi < inRecordSet.length; Loopi++) {
				
				sYD_USER_ID 	= ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "YD_USER_ID");
				sYD_STK_COL_GP	= ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "YD_STK_COL_GP");
				sSTL_NO			= ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "STL_NO");
				sPROG_CD		= ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "CURR_PROG_CD"); 
				sYD_AIM_BAY_GP  = ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "YD_AIM_BAY_GP");   // 입력동
				sWO_CAR_PLNT_PROC_CD  = ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "WO_CAR_PLNT_PROC_CD");
				
				
//				151112 hun 반납장 To위치 가능 여부 체크 start
				ydUtils.putLog(szSessionName, szMethodName, "★ 반납장 To위치 가능 여부 체크 start★", YdConstant.INFO);
				rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
			    inRecord = JDTORecordFactory.getInstance().create();
			    inRecord.setField("YD_BAY_GP",           sYD_AIM_BAY_GP);
			    inRecord.setField("YD_STK_COL_GP",           sYD_STK_COL_GP);
			    sQueryId = "com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdLocByReturnGood";
			    intRtnVal = ydCommDao.select(inRecord, rsResult, sQueryId);
			    
			    if (intRtnVal == 0) {
					gdRes.setMessage("반납장 적재 불가능. 반납장을 비워주시기 바랍니다.");		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}	
			    ydUtils.putLog(szSessionName, szMethodName, "★ 반납장 To위치 가능 여부 체크 end★", YdConstant.INFO);
				
				inRecord	= JDTORecordFactory.getInstance().create();
				inRecord.setField("STL_NO"			, sSTL_NO);
				inRecord.setField("YD_AIM_BAY_GP"	, sYD_AIM_BAY_GP); 
				inRecord.setField("YD_AIM_YD_GP"	, "H"); 
				inRecord.setField("YD_USER_ID"		, sYD_USER_ID);
				inRecord.setField("WO_CAR_PLNT_PROC_CD"	, sWO_CAR_PLNT_PROC_CD); 
//				151112 hun 반납/반송 등록시 행선코드 B3 단일화
				inRecord.setField("YD_AIM_RT_GP"		, "B3");
				
				// 목표동 수정
				ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
				outRecord2 = (JDTORecord)ejbConn.trx("updCoilYdRetCrnReg",
							new Class[] { JDTORecord.class }, new Object[] { inRecord });
				sRTN_CD	= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
				sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
				if ("0".equals(sRTN_CD)) {
					gdRes.setMessage(sRTN_MSG);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}	
				
					
				inRecord1 = JDTORecordFactory.getInstance().create();
				inRecord1.setField("STL_SH"						, "1");  
				inRecord1.setField("STL_NO1"					, sSTL_NO);
				inRecord1.setField("YD_UP_COLL_SEQ1"			, "1");  
				inRecord1.setField("YD_USER_ID"					, sYD_USER_ID);  
				inRecord1.setField("YD_AIM_BAY_GP"				, sYD_AIM_BAY_GP); 
				inRecord1.setField("YD_AIM_YD_GP"				, "H");
				
				if(sYD_STK_COL_GP.substring(1, 2).equals(sYD_AIM_BAY_GP)) { 
					inRecord1.setField("YD_TO_LOC_DCSN_MTD",		"S");
					inRecord1.setField("TO_YD_STK_BED_NO",			"");
					
					if(sPROG_CD.equals("J")||sPROG_CD.equals("5")) {  // 반납
						if(sYD_STK_COL_GP.substring(1, 2).equals("B")||sYD_STK_COL_GP.substring(1, 2).equals("C")){
							if(Integer.parseInt(sYD_STK_COL_GP.substring(2, 4)) < 31){
								sYD_SCH_CD = "H" + sYD_STK_COL_GP.substring(1, 2) + "YD53UM";  //반납SCH_CD
							} else {
								sYD_SCH_CD = "H" + sYD_STK_COL_GP.substring(1, 2) + "YD03UM";  //반납SCH_CD
							}	
						} else {
							sYD_SCH_CD = "H" + sYD_STK_COL_GP.substring(1, 2) + "YD03UM";  //반납SCH_CD
						}	
					} else {
						if(sYD_STK_COL_GP.substring(1, 2).equals("B")||sYD_STK_COL_GP.substring(1, 2).equals("C")){
							if(Integer.parseInt(sYD_STK_COL_GP.substring(2, 4)) < 31){
								sYD_SCH_CD = "H" + sYD_STK_COL_GP.substring(1, 2) + "YD54UM";  //반송SCH_CD
							} else {
								sYD_SCH_CD = "H" + sYD_STK_COL_GP.substring(1, 2) + "YD04UM";  //반송SCH_CD
							}	
						} else {
							sYD_SCH_CD = "H" + sYD_STK_COL_GP.substring(1, 2) + "YD04UM";  //반송SCH_CD
							
						}	
					}	
					inRecord1.setField("YD_SCH_CD"					, sYD_SCH_CD);
					
				} else {
					
					rsGetStock1 = JDTORecordFactory.getInstance().createRecordSet("");
					inRecord2 = JDTORecordFactory.getInstance().create();

					inRecord2.setField("FR_YD_BAY_GP",       sYD_STK_COL_GP.substring(1, 2));
					inRecord2.setField("TO_YD_BAY_GP",       sYD_AIM_BAY_GP);  
					
					/*com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.getYdTcarStat*/
					intRtnVal = ydTcarSchDao.getYdTcarsch(inRecord2, rsGetStock1, 202);
					
					if(intRtnVal <= 0){
						szMsg = "사용가능한 대차가 없습니다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						gdRes.setMessage(szMsg);		
						m_ctx.setRollbackOnly();
						return gdRes;	
					} else {
						// 커서 처음으로
						rsGetStock1.first();
						recGetVal1 = rsGetStock1.getRecord();	
						szTCar 			= ydDaoUtils.paraRecChkNull(recGetVal1,"YD_EQP_ID");
						iLOC_CNT 		= ydDaoUtils.paraRecChkNullInt(recGetVal1, "LOC_CNT");  // 적치가능 
						sYD_CURR_BAY_GP	= ydDaoUtils.paraRecChkNull(recGetVal1, "YD_CURR_BAY_GP");  // 대차현위치
						ydUtils.putLog(szSessionName, szMethodName, "szTCar:" + szTCar +  "iLOC_CNT:" + ""+ iLOC_CNT, YdConstant.DEBUG);
					}	
					
					if(sPROG_CD.equals("J")||sPROG_CD.equals("5")) {  // 반납
						sYD_SCH_CD = "H" + sYD_STK_COL_GP.substring(1, 2) +  szTCar.substring(2,6) + "UJ";  //반납SCH_CD
					} else {
						sYD_SCH_CD = "H" + sYD_STK_COL_GP.substring(1, 2) +  szTCar.substring(2,6) + "UB";  //반송SCH_CD
					}	
					inRecord1.setField("YD_SCH_CD",	 				sYD_SCH_CD);
					inRecord1.setField("YD_TO_LOC_DCSN_MTD",		"F");
					inRecord1.setField("TO_YD_STK_BED_NO",			"H" + sYD_STK_COL_GP.substring(1, 2) + szTCar.substring(2,6));
					inRecord1.setField("YD_WRK_PLAN_TCAR", 			szTCar);  //권상모음순서
				}	

				ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
				outRecord2 = (JDTORecord)ejbConn.trx("WrkBookInsertProc", new Class[] { JDTORecord.class }, new Object[] { inRecord1 });
				sRTN_CD		= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
				sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
				sYD_WBOOK_ID= StringHelper.evl(outRecord2.getFieldString("YD_WBOOK_ID"), "");
				sYD_SCH_CD	= StringHelper.evl(outRecord2.getFieldString("YD_SCH_CD"), "");
				if (!("1".equals(sRTN_CD))) {
					gdRes.setMessage(sRTN_MSG);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				} else {
					
					intStlSchCnt++;
					if(sFirstYD_WBOOK_ID.equals("")) { 
						if(!sYD_SCH_CD.substring(2, 4).equals("TC")) {
							sFirstYD_WBOOK_ID = sYD_WBOOK_ID;
						} else if (sYD_SCH_CD.substring(2, 4).equals("TC") && (iLOC_CNT > 0) && (sYD_STK_COL_GP.substring(1, 2).equals(sYD_CURR_BAY_GP))) {
							sFirstYD_WBOOK_ID = sYD_WBOOK_ID;
						}
						
						sFirstYD_SCH_CD   = sYD_SCH_CD;
					}	
					gdRes.setMessage(sRTN_MSG + " <br> 정상적으로 작업예약까지 등록했습니다.");		
		
				} 	
				szMsg = "스케줄 코드 :"+ sFirstYD_SCH_CD;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
			}	
			
			
			if(!sFirstYD_WBOOK_ID.equals("")) {
				
				JDTORecord[] inRecordarr   	= null;
				inRecordarr = new JDTORecord[1];
				
				inRecordarr[0] = JDTORecordFactory.getInstance().create();
				inRecordarr[0].setField("YD_SCH_CD"		, sFirstYD_SCH_CD); 
				inRecordarr[0].setField("YD_WBOOK_ID"	, sFirstYD_WBOOK_ID); 
				ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
				outRecord2 = (JDTORecord) ejbConn.trx("trxRunSchedule", new Class[] { JDTORecord[].class },	new Object[] { inRecordarr });
	
				sRTN_CD		= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "");
				sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
				ydUtils.putLog(szSessionName, szMethodName, "마지막 RETURN:" + sRTN_MSG, YdConstant.DEBUG);
				if (!("1".equals(sRTN_CD))) {
					gdRes.setMessage(sRTN_MSG);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				} else {
					gdRes.setMessage(sRTN_MSG + " <br> 정상적으로 스케쥴까지 등록했습니다.");		
				}
			}
			
			gdRes.setMessage("정상적으로 반납/반송처리 등록했습니다");		
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		return gdRes;
	}  //end of updCoilYdRetCrnReg1

	
	
	/**
	 * 반송대상조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	
	public GridData getCoilYdRetMgt(GridData inDto) throws DAOException {
					
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		String szMethodName = "getCoilYdRetMgt";
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			
			ejbConn = new EJBConnector("default", this);

			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdRetMgt", inRecord);
			                                                          
			gdRes = CmUtil.genGridData(inDto , recordSet);

		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilYdRetCrnReg

	
	/**
	 * 반송수정[공장공정코드]
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilYdRetMgt(GridData inDto) throws DAOException {
		//		LOG
		String szMsg = "";
		String szMethodName="updCoilYdRetMgt";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		String szRtnMsg = "";
		JDTORecord outRecord2   	= JDTORecordFactory.getInstance().create();		
		
		try{
			JDTORecord [] inRecord =  ydComUtil.genGridToJDTORecordAll(inDto);
			
			
			szMsg = "[JSP Facade] 반송등록  시작  ==>";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);
			
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			outRecord2 = (JDTORecord)ejbConn.trx("updCoilYdRetMgt", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

			String sRTN_CD	= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
			String sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
			if ("0".equals(sRTN_CD)) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
				return gdRes;	
			}	
					
			
			szMsg = "[JSP Facade] 반송등록처리 ===> 끝";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes.setMessage("정상적으로 반송등록 처리 되었습니다.");
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
			
		}
		return gdRes;
	}  //end of updCoilYdRetMgt
	
	/**
	 * 반송수정[기타사항 등록]
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilYdRetMgtUpdate(GridData inDto) throws DAOException {
		//		LOG
		String szMsg = "";
		String szMethodName="updCoilYdRetMgtUpdate";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		String szRtnMsg = "";
		JDTORecord outRecord2   	= JDTORecordFactory.getInstance().create();		
		
		try{
			JDTORecord [] inRecord =  ydComUtil.genGridToJDTORecordAll(inDto);
			
			
			szMsg = "[JSP Facade] 반송수정시작  ==>";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);
			
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			outRecord2 = (JDTORecord)ejbConn.trx("updCoilYdRetMgtUpdate", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

			String sRTN_CD	= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
			String sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
			if ("0".equals(sRTN_CD)) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
				return gdRes;	
			}	
					
			
			szMsg = "[JSP Facade] 반송수정처리 ===> 끝";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes.setMessage("정상적으로 수정 처리 되었습니다.");
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
			
		}
		return gdRes;
	}  //end of updCoilYdRetMgt
	
	
	/**
	 * 반송취소수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCancelCoilYdRetMgt(GridData inDto) throws DAOException {
		//		LOG
		String szMsg = "";
		String szMethodName="updCancelCoilYdRetMgt";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		String szRtnMsg = "";
		JDTORecord outRecord2   	= JDTORecordFactory.getInstance().create();		
		
		try{
			JDTORecord [] inRecord =  ydComUtil.genGridToJDTORecordAll(inDto);
			
			
			szMsg = "[JSP Facade] 반송등록  시작  ==>";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);
			
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			outRecord2 = (JDTORecord)ejbConn.trx("updCancelCoilYdRetMgt", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

			String sRTN_CD	= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
			String sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
			if ("0".equals(sRTN_CD)) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
				return gdRes;	
			}	
					
			
			szMsg = "[JSP Facade] 반송취소처리 ===> 끝";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes.setMessage("정상적으로 반송취소 처리 되었습니다.");
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
			
		}
		return gdRes;
	}  //end of updCoilYdRetMgt
	
	/**
	 * 반송확정수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilYdRetMgt1(GridData inDto) throws DAOException {
		//		LOG
		String szMsg = "";
		String szMethodName="updCoilYdRetMgt1";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		String szRtnMsg = "";
		JDTORecord outRecord2   	= JDTORecordFactory.getInstance().create();		
		
		try{
			JDTORecord [] inRecord =  ydComUtil.genGridToJDTORecordAll(inDto);
			
			
			szMsg = "[JSP Facade] 반송확정  시작  ==>";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);
			
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			outRecord2 = (JDTORecord)ejbConn.trx("updCoilYdRetMgt1", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

			String sRTN_CD	= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
			String sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
			if ("0".equals(sRTN_CD)) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
				return gdRes;	
			}	
					
			
			szMsg = "[JSP Facade] 반송확정처리 ===> 끝";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes.setMessage("정상적으로 반송확정 처리 되었습니다.");
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
			
		}
		return gdRes;
	}  //end of updCoilYdRetMgt1
	
	/**
	 * 코일제품창고 일품별재고조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdDdArtclStkRef(GridData inDto) throws DAOException {
					
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		String szMethodName="getCoilYdDdArtclStkRef";
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdDdArtclStkRef", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);

		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilYdDdArtclStkRef
	
	/**
	 *  코일 야드 크레인 설비내용 조회 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdCrnStsSet(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdCrnStsSet";	
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdCrnStsSetID", inRecord);
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
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
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
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
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
	 *  코일제품 야드 크레인 설비내용 조회 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	
	
	public GridData getCoilYdCrnStsSetById(GridData inDto) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName="getCoilYdCrnStsSetById";	
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdCrnStsSetById", inRecord);
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
	 *  코일 야드 크레인 설비내용 조회 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	
	
	public GridData getCoilGdsYdCrnStsSetById2(GridData inDto) throws DAOException {
		//LOG
		String szLogMsg = null;
		String szRtnMsg = null;
		String szMethodName="getCoilGdsYdCrnStsSetById2";	
		String szOperationName	= "코일 야드 크레인 설비내용 조회";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilGdsYdCrnStsSetById2", inRecord);
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
	 * !A 이적작업진행 관리 > 동별 이적목록 조회 (로딩시 조회)
	 * 작성자 : 박지열
     * 작성일 : 2010/03/19
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdGdsMvWorkDongList(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdGdsMvWorkDongList";	
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdGdsMvWorkDongList", inRecord);
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
	 *  이적작업진행 조회 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdGdsMvWorkList(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdGdsMvWorkList";	
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdGdsMvWorkList", inRecord);
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
	 *  이적작업진행상세 조회 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdGdsMvWorkDtlList(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdGdsMvWorkDtlList";	
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdGdsMvWorkDtlList", inRecord);
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
	 *  이적작업진행관리 대상재 조회 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdGdsStockList(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdGdsStockList";	
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdGdsStockList", inRecord);
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
	 * 이적작업진행관리 예약위치 수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilYdToLocGuide(GridData gdReq) throws DAOException {
		//LOG
		String       szMsg        = "";
		String       szMethodName = "updCoilYdToLocGuide";
		GridData     gdRes        = null;
		EJBConnector ejbConn      = null;
		try{
			//JDTORecord [] inRecord = CmUtil.genJDTORecordSet(gdReq);
			//fix 20081230 수정 - Param 정보도 추가로 가져오기 위함 
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			ejbConn.trx("updCoilYdToLocGuide", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		return gdRes;
	}  //end of updCoilYdToLocGuide
	
	/**
	 *  코일제품창고 군,열 상태별 재고 조회 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilGdsYdLineSvLocMgt(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilGdsYdLineSvLocMgt";	
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilGdsYdLineSvLocMgt", inRecord);
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
	 *  코일제품창고 군,열 상태 및 SPAN별 재고 조회 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilGdsYdLineSvLocMgtSpan(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilGdsYdLineSvLocMgtSpan";	
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilGdsYdLineSvLocMgtSpan", inRecord);
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
	 * 적치열정보 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilGdsYdStkCol(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilGdsYdStkCol";	
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn   = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilGdsYdStkCol", inRecord);
			gdRes     = CmUtil.genGridData(inDto , recordSet);
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
	 * 적치Bed정보 조회 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilGdsYdStkBed(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilGdsYdStkBed";	
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilGdsYdStkBed", inRecord);
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
	 * 저장집합 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilGdsYdStrGtr(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilGdsYdStrGtr";	
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn   = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilGdsYdStrGtr", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg,YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	}
	
	
	/**
	 * 적치열 정보 수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilGdsYdStkCol(GridData gdReq) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName="updCoilGdsYdStkCol";
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			//JDTORecord [] inRecord = CmUtil.genJDTORecordSet(gdReq);
			//fix 20081230 수정 - Param 정보도 추가로 가져오기 위함 
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			ejbConn.trx("updCoilGdsYdStkCol", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		return gdRes;
	}  //end of updCoilGdsYdStkCol


	/**
	 * 적치베드 정보 수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilGdsYdStkBed(GridData gdReq) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName="updCoilGdsYdStkBed";
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			//JDTORecord [] inRecord = CmUtil.genJDTORecordSet(gdReq);
			//fix 20081230 수정 - Param 정보도 추가로 가져오기 위함 
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			ejbConn.trx("updCoilGdsYdStkBed", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		return gdRes;
	}  //end of updCoilGdsYdStkBed
	
	/**
	 * 저장집합 정보 수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilGdsYdStrGtr(GridData gdReq) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName="updCoilGdsYdStrGtr";
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			//JDTORecord [] inRecord = CmUtil.genJDTORecordSet(gdReq);
			//fix 20081230 수정 - Param 정보도 추가로 가져오기 위함 
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			ejbConn.trx("updCoilGdsYdStrGtr", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		return gdRes;
	}  //end of updCoilGdsYdStrGtr
	
	/**
	 * SPAN별 저장위치 정보 조회 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdSpanStkPosList(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdSpanStkPosList";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdSpanStkPosList", inRecord);
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
	 * Span별 저장위치 정보 수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilYdSpanStkPosList(GridData gdReq) throws DAOException {
		//LOG
		String       szMsg        = "";
		String       szMethodName = "updCoilYdSpanStkPosList";
		GridData     gdRes        = null;
		EJBConnector ejbConn      = null;
		try{
			//JDTORecord [] inRecord = CmUtil.genJDTORecordSet(gdReq);
			//fix 20081230 수정 - Param 정보도 추가로 가져오기 위함 
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			ejbConn.trx("updCoilYdSpanStkPosList", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		return gdRes;
	}  //end of updCoilYdSpanStkPosList

	
	/**
	 * 작업실적량 정보 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdWrkRsltQty(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdWrkRsltQty";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdWrkRsltQty", inRecord);
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
	 * 코일제품야드 스케줄 기동관리 (조회)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData getCoilYdSchStirMgt(GridData inDto) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName="getCoilYdSchStirMgt";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdSchStirMgt", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilYdSchStirMgt
	
	/**
	 * 코일제품야드 스케줄 기동관리 (수정)
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
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			ejbConn.trx("updCoilYdSchStirMgt", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return gdRes;
	}  //end of updslabYdSchStirMgt
	
	/**
	 * 코일제품야드 동수주별재고조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData getCoilYdBayOrdInv1(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdBayOrdInv1";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdBayOrdInv1", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilYdBayOrdInv1
	
	/**
	 * 코일제품야드 동수주별재고조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData getCoilYdBayOrdInv2(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdBayOrdInv2";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdBayOrdInv2", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilYdBayOrdInv2
	
	/**
	 * 코일제품야드 열적치용도 비율 및 예상적치 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData getCoilYdColStkUsageRtoExpStk(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdColStkUsageRtoExpStk";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdColStkUsageRtoExpStk", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilYdColStkUsageRtoExpStk
	
	
	/**
	 * 코일제품야드 입고진행 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData getCoilYdInPlan(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdInPlan";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdInPlan", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilYdInPlan
	
	/**
	 * 코일제품야드 입고진행 재료상세 조회(090819)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData getCoilYdInPlan2(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdInPlan2";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdInPlan2", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilYdInPlan2

	/**
	 * 코일제품야드 입고진행 수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilYdInPlan(GridData gdReq) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName="updCoilYdInPlan";
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			//JDTORecord [] inRecord = CmUtil.genJDTORecordSet(gdReq);
			//fix 20081230 수정 - Param 정보도 추가로 가져오기 위함 
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			ejbConn.trx("updCoilYdInPlan", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		return gdRes;
	}  //end of updCoilYdInPlan
	
	
	/**
	 * 코일제품야드 입고진행 상세 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData getCoilYdInPlanDtl(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdInPlanDtl";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdInPlanDtl", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilYdInPlanDtl
	
//	/**
//	 * 코일제품야드 입고진행관리 목표동 수정
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param inDto
//	 * @return GridData
//	 * @throws DAOException
//	 * @throws JDTOException
//	 */
//	public GridData updCoilYdAimBayModify(GridData gdReq) throws DAOException {
//		//LOG
//		String szMsg="";
//		String szMethodName="updCoilYdAimBayModify";
//		GridData gdRes = null;
//		EJBConnector ejbConn = null;
//		try{
//			//JDTORecord [] inRecord = CmUtil.genJDTORecordSet(gdReq);
//			//fix 20081230 수정 - Param 정보도 추가로 가져오기 위함 
//			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);
//			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
//			ejbConn.trx("updCoilYdAimBayModify", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
//			gdRes = OperateGridData.cloneResponseGridData(gdReq);
//			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		}catch(Exception e){
//			throw new DAOException(getClass().getName() + e.getMessage(),e);
//		}
//		return gdRes;
//	}  //end of updCoilYdAimBayModify	
	
	/**
	 * 코일제품야드 입고진행 상세정보 수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilYdInPlanDtl(GridData gdReq) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName="updCoilYdInPlanDtl";
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			//JDTORecord [] inRecord = CmUtil.genJDTORecordSet(gdReq);
			//fix 20081230 수정 - Param 정보도 추가로 가져오기 위함 
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			ejbConn.trx("updCoilYdInPlanDtl", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return gdRes;
	}  //end of updCoilYdInPlanDtl	
	
	/**
	 * 코일제품야드 제품단위 이적등록 조회 
	 * 심명순 090518
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData getCoilYdGdsGdsUnitMvReg(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdGdsGdsUnitMvReg";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdGdsGdsUnitMvReg", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilYdGdsGdsUnitMvReg
	
	
	/**
	 * 코일제품야드 차량모니터링 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData getCoilYdInBayCarList(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdInBayCarList";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdInBayCarList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilYdInBayCarList
	
	
	/**
	 * 코일제품야드 입동대기차량 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData getCoilYdInBayRdCarList(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdInBayRdCarList";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdInBayRdCarList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilYdInBayRdCarList
	

	
	/**
	 * 코일제품야드 재료상세백업 저장위치 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData getCoilYdStlDtlBakcup1(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdStlDtlBakcup1";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdStlDtlBakcup1", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilYdStlDtlBakcup1
	
	/**
	 * 코일제품야드 재료상세백업 스케줄정보 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData getCoilYdStlDtlBakcup2(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdStlDtlBakcup2";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdStlDtlBakcup2", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilYdStlDtlBakcup2
	
	/**
	 * 코일제품야드 재료상세백업 재료지시정보 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData getCoilYdStlDtlBakcup3(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdStlDtlBakcup3";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdStlDtlBakcup3", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilYdStlDtlBakcup3
	
	/**
	 * 코일제품야드 재료상세백업 재료위치정보 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData getCoilYdStlDtlBakcup4(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdStlDtlBakcup4";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdStlDtlBakcup4", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilYdStlDtlBakcup4
	
	/**
	 * 코일제품야드 재료상세백업 차량정보 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData getCoilYdStlDtlBakcup5(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdStlDtlBakcup5";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdStlDtlBakcup5", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilYdStlDtlBakcup5
	
	/**
	 * 코일제품야드 차량모니터링 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData getCoilYdGdsCarMonitoring(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdGdsCarMonitoring";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdGdsCarMonitoring", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilYdGdsCarMonitoring

	
	/**
	 * 제품상세정보조회
	 * @ejb.interface-method
	 * @author 	윤혁상
	 * @date 	2009.07.14
	 */
	public JDTORecordSet getGoodsDetailInfo(JDTORecord paramRec) throws DAOException {
		log.println(LogLevel.DEBUG, "==[ EJB LOGIC ]=========================");
		log.println(LogLevel.DEBUG, "getGoodsDetailInfo()");
		String szMethodName = "getGoodsDetailInfo";
		try {
			//EJB호출및 결과값 반환
			EJBConnector ejbConn = new EJBConnector("inisteelApp", "JNDIInvStatusSeEJB", this);
			
			JDTORecordSet rtnRecordSet = (JDTORecordSet) ejbConn.trx("getGoodsDetailInfo", 
					                      new Class[] { JDTORecord.class }, 
					                      new Object[] { paramRec });
		
			return rtnRecordSet;
		} catch(Exception e){			
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:", YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}
	
//	/**
//	 * 사유별 이적등록   getMtlUnitMvstkReg1
//	 * 심명순(090713)
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param inDto
//	 * @return
//	 * @throws DAOException
//	 * @throws JDTOException
//	 */
//	public GridData getBecauseMv(GridData inDto) throws DAOException {
//		//LOG
//		String szMsg        = "";
//		String szMethodName = "getBauseMv";
//		
//		GridData      gdRes     = null;
//		EJBConnector  ejbConn   = null;
//		JDTORecordSet recordSet = null;
//		try{
//			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
//			ejbConn = new EJBConnector("default", this);
//			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getBecauseMv", inRecord);
//			gdRes = CmUtil.genGridData(inDto , recordSet);
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//		}catch(Exception e){
//			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
//		}
//		gdRes.setStatus("true");
//		gdRes.setMessage("Success");
//		return gdRes;
//	} // end of getBecauseMv
	
	/**
	 * span별 저장위치관리화면 조회
	 * 심명순(090713)
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
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdStkPosSet", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);			
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);	
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} // end of getCoilYdStkPosSet
	
	/**
	 * 저장위치 좌표설정화면 베드  조회
	 * 심명순(090713)
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
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdStkPosSetBed", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} // end of getCoilYdStkPosSetBed
	
	
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
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);

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
	 * 저장위치 좌표설정화면 BED 수정 
	 * 심명순(090713)
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
			
			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			
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
		
		return gdRes;
	} // end of updCoilYdStkPosSetBed
	
	
	/**
	 * 저장위치 좌표설정화면 열 등록 
	 * 심명순(090713)
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
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
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
	} // end of insCoilYdStkPosSet
	
	/**
	 * 저장위치 좌표설정화면 BED  등록 
	 * 심명순(090713)
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
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
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
	} // end of insCoilYdStkPosSetBed
	
	
	
	/**
	 * 코일 제품상세정보 조회/검색하는 메소드이다.(검색어, 업무영역코드)
	 * 심명순(090713)
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

			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);

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
	} // end of getPtCoilCommInfoji
	
	/**
	 * 후판제품야드주문별 재고조회
	 * 심명순(090713)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	
	public GridData getCoilYdOrdInfoStkRef(GridData inDto) throws DAOException {
					
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		String szMethodName="getCoilYdOrdInfoStkRef";
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			
			ejbConn = new EJBConnector("default", this);

			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdOrdInfoStkRef", inRecord);
			                                                          
			gdRes = CmUtil.genGridData(inDto , recordSet);

		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilYdOrdInfoStkRef
	
	/**
	 *  코일 저장위치 별 정보 조회 
	 *  심명순(090713)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	
	public GridData getCoilGdsYdStkLocInfoList(GridData inDto) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName="getCoilYdStkLocInfoList";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilGdsYdStkLocInfoList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilGdsYdStkLocInfoList
	
	/**
	 * 차량별 상세 작업관리 조회 
	 * 심명순(090713)
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
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdCarWorkMgtlist", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilYdCarWorkMgtlist
	
	/**
	 *  코일 야드 차량진행관리 조회
	 * 심명순(090713)
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
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdCarWorkList", inRecord);				

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
	} //end of getCoilYdCarWorkList
	
	/**
	 *  크레인 작업관리 조회
	 * 심명순(090713)
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
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdCrnWorkMgt", inRecord);
			gdRes     = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilYdCrnWorkMgt
	
	
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
		String szMsg        = "";
		String szMethodName = "cancleWorkCoilYdCrnWorkMgt";
		
		GridData     gdRes   = null;
		EJBConnector ejbConn = null;

		try{
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);		
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);			
			ejbConn.trx("cancleWorkCoilYdCrnWorkMgt", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(gdReq);		
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		return gdRes;	
	} //end of cancleWorkCoilYdCrnWorkMgt
	
	/**
	 * 대차 작업관리 조회 
	 * 심명순(090713)
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
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getTcarSchMtlList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getTcarSchMtlList
	
	/**
	 * 대차 작업관리재료정보 조회 
	 * 심명순(090713)
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
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getTcarWorkMtlList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getTcarWorkMtlList
	
	/**
	 * 열별 저장위치 정보 조회(단 별) 
	 * 심명순 (090715)
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
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdColStkPosLyrGpList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilYdColStkPosLyrGpList
	
	/**
	 * 차량작업관리 배차내역  조회 코일 외
	 * 심명순 (090723)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdGdsOutCar(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdGdsOutCar";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdGdsOutCar", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilYdGdsOutCar
	
	/**
	 * 차량작업관리 배차내역  조회 코일 
	 * 심명순 (090723)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdGdsOutCarCoil(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdGdsOutCarCoil";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdGdsOutCarCoil", inRecord);
			//gdRes = CmUtil.genGridData(inDto , recordSet);
			gdRes     = CmUtil.genResGridData(inDto , recordSet);

		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilYdGdsOutCar
	
	
	/**
	 * 차량작업관리 배차내역  조회 코일 
	 * 심명순 (090723)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdGdsOutCarCoilNEW(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdGdsOutCarCoilNEW";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdGdsOutCarCoilNEW", inRecord);
			//gdRes = CmUtil.genGridData(inDto , recordSet);
			gdRes     = CmUtil.genResGridData(inDto , recordSet);

		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilYdGdsOutCarCoilNEW
	
	
	/**
	 * 차량작업관리 배차내역  조회 코일 
	 * 심명순 (090723)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdGdsOutCarCoilNEW2(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdGdsOutCarCoilNEW2";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdGdsOutCarCoilNEW2", inRecord);
			//gdRes = CmUtil.genGridData(inDto , recordSet);
			gdRes     = CmUtil.genResGridData(inDto , recordSet);

		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilYdGdsOutCarCoilNEW2
	
	/**
	 * 차량작업관리 작업재료조회 
	 * 심명순 (090723)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */ 
	public GridData getCoilYdGdsCarWork(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = null;
		String szMethodName = "getCoilYdGdsCarWork";
		String szOperationName = "차량작업관리 작업재료조회";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szMsg = "[JSP Facede] " + szOperationName + " 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdGdsCarWork", inRecord);
			
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
			gdRes.setMessage(YdConstant.RETN_CD_SUCCESS);
			
			szMsg = "[JSP Facede] " + szOperationName + " 완료 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		
		return gdRes;
	} //end of getCoilYdGdsCarWork


	/**
	 * 차량작업관리 차량스케줄 조회 코일외
	 * 심명순 (090727)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdGdsCarSch(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdGdsCarSch";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdGdsCarSch", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilYdGdsCarSch
	
	
	/**
	 *  코일제품창고 포인트개폐 처리 코일 외
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData procCoilYdGdsPntUnitCL(GridData inDto) throws DAOException {
		//LOG
		String szMsg = "";
		String szMethodName="procCoilYdGdsPntUnitCL";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		
		try{
			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(inDto);
			
			
			szMsg = "포인트개폐 전송처리 시작 ==>";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			ejbConn.trx("procCoilYdGdsPntUnitCL", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);
			
			
			szMsg = " 포인트개폐 전송처리 ===> 끝";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return gdRes;
	}  //end of procCoilYdGdsPntUnitCL
	
	/**
	 *  입동순서 변경
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData procCoilYdGdsBayInWoSeqChang(GridData inDto) throws DAOException {
		//LOG
		String szMsg = "";
		String szMethodName="procCoilYdGdsBayInWoSeqChang";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		
		try{
			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(inDto);
			
			
			szMsg = "차량작업관리 차량 POINT작업현황 입동순서 변경 작업 시작 ==>";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			ejbConn.trx("procCoilYdGdsBayInWoSeqChang", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);
			
			
			szMsg = "차량작업관리 차량 POINT작업현황 입동순서 변경 작업  끝";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return gdRes;
	}  //end of procCoilYdGdsBayInWoSeqChang
	
	/**
	 * 차량 작업 관리 화면 코일에서 사용
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public GridData uptCarSchCoil(GridData inDto) throws DAOException {
		//		LOG
		String szMsg = "";
		String szMethodName="uptCarSchCoil";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		String szRtnMsg = "";
		
		try{
			JDTORecord [] inRecord =  ydComUtil.genGridToJDTORecordAll(inDto);
			
			
			szMsg = "[JSP Facade] 차량작업관리 배차내역 배차등록버튼 수정  전송처리 시작  ==>";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			szRtnMsg = (String)ejbConn.trx("uptCarSchCoil", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);
			
			
			szMsg = "[JSP Facade] 차량작업관리 배차내역 배차등록버튼 수정  전송처리 ===> 끝";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes.setMessage(szRtnMsg);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
			
		}
		return gdRes;
	}  //end of uptCarSch

	/**
	 * 차량 작업 관리 화면: 후판에서 사용
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public GridData uptCarSch(GridData inDto) throws DAOException {
		//		LOG
		String szMsg = "";
		String szMethodName="uptCarSch";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		String szRtnMsg = "";
		
		try{
			JDTORecord [] inRecord =  ydComUtil.genGridToJDTORecordAll(inDto);
			
			
			szMsg = "[JSP Facade] 차량작업관리 배차내역 배차등록버튼 수정  전송처리 시작  ==>";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			szRtnMsg = (String)ejbConn.trx("uptCarSch", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);
			
			
			szMsg = "[JSP Facade] 차량작업관리 배차내역 배차등록버튼 수정  전송처리 ===> 끝";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes.setMessage(szRtnMsg);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
			
		}
		return gdRes;
	}  //end of uptCarSch
		
	/**
	 * 차량작업관리 화면 상차LOT편성 후판사용
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public GridData insCarLdLot(GridData inDto) throws DAOException {
		//		LOG
		String szMsg = "";
		String szMethodName="insCarLdLot";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		String szRtnMsg = "";
		String szYD_CAR_LOT_TYPE = "";
		JDTORecord [] inRecord =  null;
		try{

			szMsg = "[JSP Facade] 차량작업관리 화면 상차LOT편성 시작  ==>";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			szYD_CAR_LOT_TYPE = inDto.getParam("YD_CAR_LOT_TYPE");
			if( szYD_CAR_LOT_TYPE.equals("M") ) {
				inRecord =  ydComUtil.genGridToJDTORecordAll(inDto);
				szMsg = "[JSP Facade] 차량작업관리 화면 상차LOT편성 - 작업자 지정";
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			}else{
				inRecord = new JDTORecord[1];
				inRecord[0] = ydComUtil.genParamToJDTORecord(inDto);
				szMsg = "[JSP Facade] 차량작업관리 화면 상차LOT편성 시작   - 기준 적용";
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			}
			
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			szRtnMsg = (String)ejbConn.trx("insCarLdLot", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);
			
			
			szMsg = "[JSP Facade] 차량작업관리 화면 상차LOT편성 끝";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes.setMessage(szRtnMsg);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
			
		}
		return gdRes;
	}  //end of insCarLdLot

	/**
	 * 차량작업관리 화면 상차LOT편성 코일 사용
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public GridData insCarLdLotCoil(GridData inDto) throws DAOException {
		//		LOG
		String szMsg = "";
		String szMethodName="insCarLdLotCoil";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		String szRtnMsg = "";
		String szYD_CAR_LOT_TYPE = "";
		JDTORecord [] inRecord =  null;
		try{

			szMsg = "[JSP Facade] 차량작업관리 화면 상차LOT편성 시작  ==>";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			szYD_CAR_LOT_TYPE = inDto.getParam("YD_CAR_LOT_TYPE");
			if( szYD_CAR_LOT_TYPE.equals("M") ) {
				inRecord =  ydComUtil.genGridToJDTORecordAll(inDto);
				szMsg = "[JSP Facade] 차량작업관리 화면 상차LOT편성 - 작업자 지정";
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			}else{
				inRecord = new JDTORecord[1];
				inRecord[0] = ydComUtil.genParamToJDTORecord(inDto);
				szMsg = "[JSP Facade] 차량작업관리 화면 상차LOT편성 시작   - 기준 적용";
				ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			}
			
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			szRtnMsg = (String)ejbConn.trx("insCarLdLotCoil", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);
			
			
			szMsg = "[JSP Facade] 차량작업관리 화면 상차LOT편성 끝";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes.setMessage(szRtnMsg);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return gdRes;
	}  //end of insCarLdLot
		
	/**
	 * 차량작업관리 화면 상차LOT편성 취소
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public GridData cancelCarLdLot(GridData inDto) throws DAOException {
		//		LOG
		String szMsg = "";
		String szMethodName="cancelCarLdLot";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		String szRtnMsg = "";
		
		try{
			JDTORecord [] inRecord =  ydComUtil.genGridToJDTORecordAll(inDto);
			
			
			szMsg = "[JSP Facade] 차량작업관리 화면 상차LOT편성  취소 시작  ==>";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			szRtnMsg = (String)ejbConn.trx("cancelCarLdLot", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);
			
			
			szMsg = "[JSP Facade] 차량작업관리 화면 상차LOT편성  취소 끝";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes.setMessage(szRtnMsg);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return gdRes;
	}  //end of cancelCarLdLot
	
	/**
	 * 차량작업관리 화면 상차LOT편성 취소 :코일 사용
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public GridData cancelCarLdLotCoil(GridData inDto) throws DAOException {
		//		LOG
		String szMsg = "";
		String szMethodName="cancelCarLdLotCoil";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		String szRtnMsg = "";
		
		try{
			JDTORecord [] inRecord =  ydComUtil.genGridToJDTORecordAll(inDto);
			
			
			szMsg = "[JSP Facade] 차량작업관리 화면 상차LOT편성  취소 시작  ==>";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			szRtnMsg = (String)ejbConn.trx("cancelCarLdLotCoil", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);
			
			
			szMsg = "[JSP Facade] 차량작업관리 화면 상차LOT편성  취소 끝";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes.setMessage(szRtnMsg);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return gdRes;
	}  //end of cancelCarLdLot
		
	/**
	 * 차량작업관리화면 상차완료처리
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public GridData complCarLdLot(GridData inDto) throws DAOException {
		//		LOG
		String szMsg = "";
		String szMethodName="complCarLdLot";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		String szRtnMsg = "";
		
		try{
			JDTORecord [] inRecord =  ydComUtil.genGridToJDTORecordAll(inDto);
			
			
			szMsg = "[JSP Facade] 차량작업관리 화면 상차완료처리 시작  ==>";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			szRtnMsg = (String)ejbConn.trx("complCarLdLot", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);
			
			
			szMsg = "[JSP Facade] 차량작업관리 화면 상차완료처리 끝";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes.setMessage(szRtnMsg);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return gdRes;
	}  //end of complCarLdLot
	
	/**
	 * 차량작업관리화면 하차완료처리 사용안
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public GridData complCarUd(GridData inDto) throws DAOException {
		//		LOG
		String szMsg = "";
		String szMethodName="complCarUd";
		String szOperationName = "차량작업관리화면 하차완료처리";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		String szRtnMsg = "";
		
		try{
			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(inDto);
			
			
			szMsg = "[JSP Facade] " + szOperationName + " 시작  ==>";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			szRtnMsg = (String)ejbConn.trx("complCarUd", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);
			
			
			szMsg = "[JSP Facade] " + szOperationName + " 끝";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes.setMessage(szRtnMsg);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return gdRes;
	}  //end of complCarUd
	
	
	/**
	 * 차량 작업 관리 화면 배차등록 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public GridData intCarSch(GridData inDto) throws DAOException {
		//		LOG
		String szMsg = "";
		String szMethodName="intCarSch";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		
		try {
			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(inDto);
			
			szMsg = "후판제품창고 차량작업관리 배차내역 배차등록버튼 수정  전송처리 시작";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			ejbConn.trx("intCarSch", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);
			
			szMsg = "후판제품창고 차량작업관리 배차내역 배차등록버튼 수정  전송처리 ===> 끝";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
		} catch (Exception e) {
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		return gdRes;
	} //end of intCarSch

	/**
	 * 이송대상재 POP(조회)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData getCoilYdRcptPlnMtl(GridData inDto) throws JDTOException {
		GridData		gdRes = null;
		EJBConnector 	ejbConn = null;
		JDTORecordSet	recordSet = null;
		String 			szMethodName  = "getCoilYdRcptPlnMtl";
		String 			szMsg         = "";
		
		try{

			ydUtils.putLog(szSessionName, szMethodName , "getCoilYdRcptPlnMtl" , YdConstant.DEBUG);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);

			recordSet = (JDTORecordSet) ejbConn.trx("getCoilYdRcptPlnMtl",
					new Class[] { JDTORecord.class }, new Object[] { inRecord });

			gdRes = CmUtil.genGridData(inDto , recordSet);
			
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			return gdRes;
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
	}
	
	/**
	 *  이송대상재 저장품 수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 */
	public GridData updCoilFrtoMoveMtlToStock(GridData inDto) throws JDTOException {
		//LOG
		String szMsg = "";
		String szMethodName="updCoilFrtoMoveMtlToStock";
		String szOperationName = "이송대상재 저장품 수정";
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		
		try{
			JDTORecord [] inRecord =  ydComUtil.genGridToJDTORecordAll(inDto);
			
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			String szRtnMsg = (String)ejbConn.trx("updCoilFrtoMoveMtlToStock", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);
			gdRes.setStatus("true");
			gdRes.setMessage(szRtnMsg);
			szMsg = "[JSP Facade : " + szOperationName + "] 반환메세지 - " + szRtnMsg;
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			return gdRes;
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
	}  //end of updFrtoMoveMtlToStock
	
	/**
	 * 차량작업관리 차량스펙정보 조회  후판
	 * 심명순 (090731)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCarSpecInfo(GridData inDto) throws DAOException {
//		LOG
		String szMsg        = "";
		String szMethodName = "getCarSpecInfo";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCarSpecInfo", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCarSpecInfo
	
	/**
	 * 차량작업관리 차량스펙정보 조회  코일
	 * 심명순 (090731)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCarSpecInfoCoil(GridData inDto) throws DAOException {
//		LOG
		String szMsg        = "";
		String szMethodName = "getCarSpecInfoCoil";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCarSpecInfoCoil", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCarSpecInfo
	
	
	/**
	 * 차량작업관리 상차LOT편성  
	 * 심명순 (090723)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData insSangchaLot(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "insSangchaLot";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "insSangchaLot", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg,YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of insSangchaLot
	
	/**
	 * 코일제품야드 입고진행관리 추출요구 기능
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData R3shearOutLineOffReq(GridData gdReq) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName="R3shearOutLineOffReq";
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		String sRTN_MSG	  = "";
		String sRTN_CD    = "";
    	JDTORecord 		outRecord1 	= JDTORecordFactory.getInstance().create();
		try{
			//JDTORecord [] inRecord = CmUtil.genJDTORecordSet(gdReq);
			//fix 20081230 수정 - Param 정보도 추가로 가져오기 위함 
			
			JDTORecord [] inRecord = ydComUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			outRecord1 = (JDTORecord)ejbConn.trx("R3shearOutLineOffReq", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
			sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			
			gdRes.setMessage(sRTN_MSG);		
			
			
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return gdRes;
	}  //end of R3shearOutLineOffReq

	/**
	 * 코일제품창고 사유별이적조회 상단데이터조회
	 * 심명순(090713)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getBecauseMvUpLyr(GridData inDto) throws DAOException {
		
		//LOG
		String szMsg="";
		String szMethodName="getBecauseMvUpLyr";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);

			recordSet = (JDTORecordSet) ejbConn.trx("getBecauseMvUpLyr",
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
	} // end of getBecauseMvUpLyr
	
//	/**
//	 * 코일제품창고 사유별이적조회 지시버튼  updColUnitMvstkReg
//	 * 심명순(090713)
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param inDto
//	 * @return
//	 * @throws DAOException
//	 * @throws JDTOException
//	 */
//	public GridData updBecauseMv(GridData gdReq) throws DAOException {
//		
//		//LOG
//		String szMsg="";
//		String szMethodName="updBecauseMv";
//		String szOperationName	= "코일제품 사유별 이적 지시 등록";
//		String szRet = YdConstant.RETN_CD_SUCCESS;
//		
//		GridData gdRes = null;
//		EJBConnector ejbConn = null;
//		JDTORecordSet recordSet = null;
//		String sRTN_CD	= "";
//		String sRTN_MSG	= "";
//		String szTemp   = "";
//		String szYD_STK_COL_GP = "";
//		String szToColGp = "";
//		String szToBedNo = "";
//		String szTCar = "";
//		String szYD_SCH_CD = "";
//		String szSPAN_GP = "";
//		String szYD_TO_LOC_GUIDE  = "";
//		int intStlCnt         = 0;
//		JDTORecord outRecord    = JDTORecordFactory.getInstance().create();
//		JDTORecord outRecord1   = JDTORecordFactory.getInstance().create();
//		JDTORecord outRecord2   = JDTORecordFactory.getInstance().create();
//		JDTORecord inRecord1 	= JDTORecordFactory.getInstance().create();
//		JDTORecord inRecord     = JDTORecordFactory.getInstance().create();
//		
//		try{
//			szMsg = "[JSP-FACADE  " + szOperationName +"] 시작";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//			
//			
//			inRecord1 = CmUtil.genJDTORecord(gdReq);
//			JDTORecord [] inRecordSet =  ydComUtil.genGridToJDTORecordAll(gdReq);
//			
//			gdRes = OperateGridData.cloneResponseGridData(gdReq);
//			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
//			
//			szTemp =  ydDaoUtils.paraRecChkNull(inRecordSet[0], "FROMLOC");
//			
//			if(szTemp.equals("")){
//				gdRes.setMessage("FROM 적치정보가 없습니다!!");		
//				m_ctx.setRollbackOnly();
//				
//			} else { 
//				szYD_STK_COL_GP = szTemp.substring(0,6);
//				szToColGp = inRecord1.getFieldString("YD_GP") +  inRecord1.getFieldString("TO_YD_BAY_GP") +
//							inRecord1.getFieldString("TO_YD_EQP_GP") +  inRecord1.getFieldString("TO_YD_STK_COL_NO") ;
//				szToBedNo = ydDaoUtils.paraRecChkNull(inRecord, "TO_YD_STK_BED_NO");
//				
//				szTCar = ydDaoUtils.paraRecChkNull(inRecord1, "T_CAR");
//				
//				if(szTCar.equals("")){
//					szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "YD";
//					szSPAN_GP = szYD_STK_COL_GP.substring(2,4);
//					
//					//동내이적 스케줄을 스판기준으로 나누는 방법
//					if(szSPAN_GP.equals("01") || 
//							szSPAN_GP.equals("02")||
//							szSPAN_GP.equals("03")||
//							szSPAN_GP.equals("04")||
//							szSPAN_GP.equals("05")||
//							szSPAN_GP.equals("06")||
//							szSPAN_GP.equals("07")||
//							szSPAN_GP.equals("08")){
//						szYD_SCH_CD = szYD_SCH_CD + "01MM"; //동내이적
//					} else { 
//						szYD_SCH_CD = szYD_SCH_CD + "02MM"; //동내이적2
//					}   
//					
//					//YD_SCH_CD
//				}
//				else{
//					inRecord.setField("YD_WRK_PLAN_TCAR",szTCar );
//					szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + szTCar.substring(2,6) + "UM"; 
//				}
//				//REGISTER
//	//			recPara.setField("REGISTER", inDto[0].getFieldString("YD_USER_ID"));
//				//To 위치 가이드
//				szYD_TO_LOC_GUIDE = szToColGp + szToBedNo;
//				
//				
//				for(int Loopi = 0; Loopi < inRecordSet.length; Loopi++) {
//					//재료번호
//					//STL_NO []
//					intStlCnt++;
//					inRecord = JDTORecordFactory.getInstance().create();
//					inRecord.setField("STL_NO"+(intStlCnt), ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "STL_NO"));
//					
//					//권상 모음순서 
//					//YD_UP_COLL_SEQ []
//					inRecord.setField("YD_UP_COLL_SEQ"+(intStlCnt),""+(intStlCnt));
//	
//					//재료번호
//					//레코드 생성
//	
//					inRecord.setField("YD_SCH_CD",          		szYD_SCH_CD);//스케줄코드
//					inRecord.setField("STL_SH",      				"1");  //LINE_IN 재료매수
//					inRecord.setField("STL_NO1", 	  				ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "STL_NO"));
//					inRecord.setField("YD_TO_LOC_DCSN_MTD",			"F");
//							
//					inRecord.setField("TO_YD_STK_BED_NO",			szYD_TO_LOC_GUIDE);
//					inRecord.setField("YD_UP_COLL_SEQ", 			"1");  //권상모음순서
//					//recOutPara.setField("YD_STK_COL_GP",      		szYD_EQP_ID); //적치열구분		
//					//recOutPara.setField("YD_STK_BED_NO",      		szYD_STK_BED_NO); //적치베드번호
//	
//					// 작업예약 등록 호출
//	//대처						this.procWrkBook(recOutPara);
//	//YD_SCH_CD:스케줄코드,
//	//STL_SH: 재료매수,
//	//YD_TO_LOC_DCSN_MTD(TO위치 결정 방법)
//	//STL_NO(재료번호1,2,3,....)
//	//FR_YD_STK_BED_NO(적치배드)
//	//TO_YD_STK_BED_NO(가이드가 됨)
//	
//					ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
//					outRecord = (JDTORecord)ejbConn.trx("WrkBookInsertProc", new Class[] { JDTORecord.class }, new Object[] { inRecord });
//					sRTN_CD		= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
//					sRTN_MSG	= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
//					if ("0".equals(sRTN_CD)) {
//						szMsg = "작업예약 등록시 ERROR";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//						gdRes.setMessage(sRTN_MSG);		
//						m_ctx.setRollbackOnly();
//					} else {
//						
//						gdRes.setMessage("정상적으로 작업예약까지 등록했습니다.");			
//					}	
//				}
//			}
//			szMsg = "다수코일  작업예약  완료!";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);					
//			
//		}catch(Exception e){		
//			gdRes = gdReq;
//			gdRes.setMessage(e.getMessage());			//e.getMessage() - 에러메세지코드를 변환할 필요가 있음
//			ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
//		}
//		
//		return gdRes;
//	} // end of upBecauseMv
//	
	
	

	/**
	 * 주문별 재고조회
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoidGdsYdOrdInfoMtl(GridData inDto) throws DAOException {

		String szMethodName="getCoidGdsYdOrdInfoMtl";		
		String szLogMsg = "";
		String szOperationName	= "주문별 재고조회";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);

			recordSet = (JDTORecordSet) ejbConn.trx("getCoidGdsYdOrdInfoMtl",
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
	} // end of getCoidGdsYdOrdInfoMtl
	
	
	
	
	/**
	 * 수주별 , 고객사별 재고조회(재료정보)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoidGdsYdOrdGpMtl(GridData inDto) throws DAOException {

		String szMethodName="getCoidGdsYdOrdGpMtl";		
		String szLogMsg = "";
		String szOperationName	= "수주별 , 고객사별 재고조회(재료정보)";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);

			recordSet = (JDTORecordSet) ejbConn.trx("getCoidGdsYdOrdGpMtl",
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
	} // end of getCoidGdsYdOrdGpMtl
	
	
	
	
	/**
	 * Span별 적치사양조정
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
	 * [A] 오퍼레이션명 : 열연Coil상세조회
	 *      
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
	 * @param GridData gdReq
	 * @return GridData
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.04.09
	*/
	public GridData mgtHrCoilDtlInq(GridData gdReq) throws DAOException {
		try {			
			EJBConnector ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			return (GridData)ejbConn.trx("getHrCoilDtlInq", new Class[] { GridData.class }, new Object[] { gdReq });
		} catch(Exception e) {
			e.printStackTrace();
			ydUtils.putLog(szSessionName, "CoilGdsJspFaEJBBean.mgtHrCoilDtlInq()", e.getMessage(), YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}
	
	/**
	 * 야드관리 > 코일소재야드 > 야드재공관리 > 재료상세정보조회 (PAGE명:열연Coil상세조회)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdStrlocIdInfo(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdStrlocIdInfo";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szMsg = "JSP-FACADE [열연Coil상세조회 - 위치변경이력조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdStrlocIdInfo", inRecord);
			
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		szMsg = "JSP-FACADE [열연Coil상세조회 - 위치변경이력조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		return gdRes;
	}
	
	
	/**
	 * 코일제품야드 tracking 입고팝업 조회보급
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData getcoilGdsYdLineWrPp(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getcoilGdsYdLineWrPp";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getcoilGdsYdLineWrPp", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return gdRes;
	} //end of getcoilGdsYdLineWrPp
	

	
	
	/**
	 * 코일제품야드 tracking 팝업 조회보급등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData inscoilGdsYdLineWrPp(GridData gdReq) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName="inscoilGdsYdLineWrPp";
		JDTORecord   inRecord   	= JDTORecordFactory.getInstance().create();
		JDTORecord   outRecord   	= JDTORecordFactory.getInstance().create();
		JDTORecord[] inRecordarr   	= null;
		String sYD_SCH_CD 	= "";
		String sYD_EQP 		= "";
		String sYD_BAY 		= "";
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			JDTORecord paramRec = CmUtil.genJDTORecord(gdReq);
			
//			 * TREAT_GP	처리구분	C	1	Y	1:보급, 2:보급취소, 3:추출, 4:Take-Out, 5:Take-In				
//			 * STL_NO	재료번호	C	11	Y					
//           * EQP_GP	설비구분	C	6		보급, Take-In 요구시 Coil 위치			
			
			sYD_BAY = paramRec.getFieldString("PARA_YD_EQP_ID1");
			sYD_BAY = paramRec.getFieldString("PARA_YD_EQP_ID1").substring(1, 2);
			sYD_EQP = paramRec.getFieldString("PARA_YD_EQP_ID1")+paramRec.getFieldString("PARA_YD_STK_BED_NO");
			
			ydUtils.putLog(szSessionName, szMethodName, sYD_EQP, YdConstant.INFO);
			
			if((sYD_BAY.equals("D"))||(sYD_BAY.equals("F"))){
				inRecord.setField("JMS_TC_CD"		, "HRYDJ009"); 
				inRecord.setField("EQP_GP"			, (String)YdCommonUtils.h_hstEqpGpMatch.get(sYD_EQP)); 
				inRecord.setField("TREAT_GP"		, "3"); 
//C증설					
			} else if(sYD_BAY.equals("A")){
				if(sYD_EQP.substring(2,3).equals("K")){	
					inRecord.setField("JMS_TC_CD"	, "H2YDL073");
				} else{	
					inRecord.setField("JMS_TC_CD"	, "H2YDL063");
				}
			} else if(sYD_BAY.equals("B")){
				if(sYD_EQP.substring(2,3).equals("K")){	
					inRecord.setField("JMS_TC_CD"	, "H2YDL043");
				} else{	
					inRecord.setField("JMS_TC_CD"	, "H2YDL063");
				}
			} else if(sYD_BAY.equals("C")){
				if(sYD_EQP.substring(2,3).equals("K")){	
					inRecord.setField("JMS_TC_CD"	, "H2YDL033");
				} else{	
					inRecord.setField("JMS_TC_CD"	, "H2YDL053");
				}
			} else if(sYD_BAY.equals("E")){
				inRecord.setField("JMS_TC_CD"		, "H2YDL023"); 
			} else if(sYD_BAY.equals("G")){
				inRecord.setField("JMS_TC_CD"		, "H2YDL013"); 
			} else if(sYD_BAY.equals("H")){
				inRecord.setField("JMS_TC_CD"		, "H2YDL003"); 
			}
			inRecord.setField("STL_NO"			, paramRec.getFieldString("PARA_STL_NO")); 
			inRecord.setField("YD_EQP_ID"		, paramRec.getFieldString("PARA_YD_EQP_ID1")); 
			inRecord.setField("YD_STK_BED_NO"	, paramRec.getFieldString("PARA_YD_STK_BED_NO")); 
			
			
			ejbConn = new EJBConnector("default", "CoilRcptWrkDmdSeEJB", this);
			outRecord = (JDTORecord)ejbConn.trx("procR3ShearOutLineOffReq", new Class[] { JDTORecord.class }, new Object[] { inRecord });

			
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);

//			ydUtils.putLog(szSessionName, szMethodName, "스케줄 기동", YdConstant.INFO);
			
//			스케줄 기동 			
			String sRTN_CD		= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
			String sRTN_MSG		= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
			sYD_SCH_CD 			= StringHelper.evl(outRecord.getFieldString("YD_SCH_CD"), "");
			
			if (!("1".equals(sRTN_CD))) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
			} else {

				gdRes.setMessage("정상적으로 기동하였습니다." +sRTN_MSG);		
	
			} 	
			
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return gdRes;
	}  //end of inscoilGdsYdLineWrPp	
	
	

	
	/**
	 * 코일제품야드 tracking 팝업 조회보급등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData inscoilGdsYdLineWrPpGPack(GridData gdReq) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName="inscoilGdsYdLineWrPp";
		JDTORecord   inRecord   	= JDTORecordFactory.getInstance().create();
		JDTORecord   outRecord   	= JDTORecordFactory.getInstance().create();
		JDTORecord[] inRecordarr   	= null;
		String sYD_SCH_CD 	= "";
		String sYD_EQP 		= "";
		String sYD_BAY 		= "";
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			JDTORecord paramRec = CmUtil.genJDTORecord(gdReq);
			
			sYD_BAY = paramRec.getFieldString("PARA_YD_EQP_ID1").substring(1, 2);
			sYD_EQP = paramRec.getFieldString("PARA_YD_EQP_ID1")+paramRec.getFieldString("PARA_YD_STK_BED_NO");
			
			ydUtils.putLog(szSessionName, szMethodName, sYD_EQP, YdConstant.INFO);
			
			
			inRecord.setField("JMS_TC_CD"		, "HRYDJ009"); 
			inRecord.setField("EQP_GP"			, (String)YdCommonUtils.h_hstEqpGpMatch.get(sYD_EQP)); 
			inRecord.setField("TREAT_GP"		, "3"); 

			inRecord.setField("STL_NO"			, paramRec.getFieldString("PARA_STL_NO")); 
			inRecord.setField("YD_EQP_ID"		, paramRec.getFieldString("PARA_YD_EQP_ID1")); 
			inRecord.setField("YD_STK_BED_NO"	, paramRec.getFieldString("PARA_YD_STK_BED_NO")); 
			
			
			ejbConn = new EJBConnector("default", "CoilRcptWrkDmdSeEJB", this);
			outRecord = (JDTORecord)ejbConn.trx("procR3ShearOutLineOffReq", new Class[] { JDTORecord.class }, new Object[] { inRecord });

			
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);

//			ydUtils.putLog(szSessionName, szMethodName, "스케줄 기동", YdConstant.INFO);
			
//			스케줄 기동 			
			String sRTN_CD		= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
			String sRTN_MSG		= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
			sYD_SCH_CD 			= StringHelper.evl(outRecord.getFieldString("YD_SCH_CD"), "");
			
			if (!("1".equals(sRTN_CD))) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
			} else {

				gdRes.setMessage("정상적으로 기동하였습니다." +sRTN_MSG);		
	
			} 	
			
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return gdRes;
	}  //end of inscoilGdsYdLineWrPp	


	/*---------------------------------------------------------------------------------------------------*/
	/*                                     2기       작업
	/*---------------------------------------------------------------------------------------------------*/
	/**
	 *  야드관리 > 코일제품창고 > 저장관리  <제품단위,열단위 이적화면 코일 Display 데이터 조회 > 
	 *  송정현
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 * @수정자 : 박지열
	 * @수정일 : 2010.07.07
	 */
	
	public GridData getMtlUnitMvstkReg(GridData inDto) throws DAOException {
	
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		String szMethodName		= "getMtlUnitMvstkReg";
		try{
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			gdRes = (GridData)ejbConn.trx("getMtlUnitMvstkReg", new Class[] { GridData.class }, new Object[] { inDto });
			
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		
		return gdRes;
	} //end of getMtlUnitMvstkReg
	
	
	
	/**
	 * 일품단위이적등록/열단위이적등록
	 * 송정
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
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getMtlUnitMvstkReg1", inRecord);
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
	 * span단위이적등록
	 * 송정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getSpanUnitMvstkReg(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getSpanUnitMvstkReg";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getSpanUnitMvstkReg", inRecord);
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
	 *  야드관리 > 코일제품창고 > 저장관리 > 제품단위이적등록  (이적지시)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updMtlUnitMvstkReg(GridData gdReq) throws DAOException {
		YdDelegate ydDelegate 	= new YdDelegate();
		YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();//작업예약 DAO
		
		String szMsg="";
		String szMethodName="updMtlUnitMvstkReg";
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		String sRTN_CD	= "";
		String sRTN_MSG	= "";
		String sYD_WBOOK_ID = "";
		String sYD_SCH_CD = "";
		String sSTL_CNT = "";
		String sSUM_WGT = "";
		String sSND_FLAG = "N";
		String sCHK_FLAG = "N";

		JDTORecordSet rsResult2 = null; 
		JDTORecord inRecord1    = JDTORecordFactory.getInstance().create();
		JDTORecord inRecord2    = JDTORecordFactory.getInstance().create();
		JDTORecord outRecord1   = JDTORecordFactory.getInstance().create();
		JDTORecord outRecord2   = JDTORecordFactory.getInstance().create();
		JDTORecord outRecord4   = JDTORecordFactory.getInstance().create();
		JDTORecord recInTemp    = JDTORecordFactory.getInstance().create();
		
		String sYD_GP			= "";
		String sYD_AIM_BAY_GP	= "";
		String sYD_TCAR_SCH_ID  = "";
		String sCURR_PROG_CD  = "";
		String sTRANS_ORD_SEQNO  = "";
		int intRtnVal = 0;

		try{

			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			
			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(gdReq);
			
			//------------------------------------------------------------------------------------------------
			// 2010.06.25 추가
			//  대차상태  체크
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			outRecord1 = (JDTORecord)ejbConn.trx("getStkColTCarChk", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
			sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
			if (!sRTN_CD.equals("1")) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
				return gdRes;
			} 
			//------------------------------------------------------------------------------------------------
			
			
			// 메뉴얼 코일 작업지시 편성
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			outRecord1 = (JDTORecord)ejbConn.trx("updMtlUnitMvstkReg", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			sRTN_CD	= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
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
	
			} 	
			szMsg = "스케줄 코드 :"+ sYD_SCH_CD.substring(2, 4);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			if(sYD_SCH_CD.substring(2, 4).equals("TC")){
			    // 설비 check 하여 		

//				대차작업 송신 여부		
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
				// 상차 가능 CHECK
				inRecord2 = JDTORecordFactory.getInstance().create();
				inRecord2.setField("YD_SCH_CD"		, sYD_SCH_CD);//스케줄코드
				inRecord2.setField("YD_WBOOK_ID"	, sYD_WBOOK_ID);  //
				ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
				outRecord4 = (JDTORecord)ejbConn.trx("getStkColTCarUpChk3", new Class[] { JDTORecord.class }, new Object[] { inRecord2 });
				sRTN_CD		= StringHelper.evl(outRecord4.getFieldString("RTN_CD"), "0");
				sRTN_MSG	= StringHelper.evl(outRecord4.getFieldString("RTN_MSG"), "");
				sSTL_CNT	= StringHelper.evl(outRecord4.getFieldString("STL_CNT"), "0");
				sSUM_WGT	= StringHelper.evl(outRecord4.getFieldString("SUM_WGT"), "0");
				
				sCURR_PROG_CD	= StringHelper.evl(outRecord4.getFieldString("CURR_PROG_CD"), "0");
				sTRANS_ORD_SEQNO	= StringHelper.evl(outRecord4.getFieldString("TRANS_ORD_SEQNO"), "0");

				
				if(!sTRANS_ORD_SEQNO.equals("0") && sCURR_PROG_CD.equals("M")){
					szMsg = " <br> lot편성된 보관매출재는 동간 이적이 불가능 합니다. ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					gdRes.setMessage(szMsg);		
					m_ctx.setRollbackOnly();
					return gdRes;					
				}
				
				if ("0".equals(sRTN_CD)) {
					szMsg = "상차 가능 CHECK시  ERROR";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					gdRes.setMessage(szMsg);		
					m_ctx.setRollbackOnly();
					return gdRes;
				} else {
					gdRes.setMessage(sRTN_MSG + " <br> 정상적으로 작업예약까지 등록했습니다.");		
					//gdRes.setMessage("정상적으로 작업예약까지 등록했습니다.");			
				}	
				
				double dSTL_CNT = Integer.parseInt(sSTL_CNT);
				double dSUM_WGT = Integer.parseInt(sSUM_WGT);
				if (dSTL_CNT <= 3) {
					if (dSUM_WGT < YdConstant.YD_COIL_TC_WEIGH_MAX) {
						sCHK_FLAG = "Y";
					}
				} 
			} else {
				sCHK_FLAG = "Y";
			}	
	//			스케쥴등록
			if( sCHK_FLAG.equals("Y")) {
				
				ydUtils.putLog(szSessionName, szMethodName, "151027 hun 제품단위 이적시 스케줄존재 체크후 기동", YdConstant.DEBUG);
				
				rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
				inRecord2 = JDTORecordFactory.getInstance().create();
				inRecord2.setField("YD_SCH_CD",          	sYD_SCH_CD);//스케줄코드
				/*com.inisteel.cim.yd.dao.ydWrkbookDao.getWorkTbRefNONESchCd*/
				intRtnVal = ydWrkbookDao.getYdWrkbook(inRecord2, rsResult2, 502);
				ydUtils.putLog(szSessionName, szMethodName, "151027 hun 제품단위 이적시 스케줄존재 체크후 기동"+intRtnVal, YdConstant.DEBUG);
//         		151029 체크 안함으로 원복
//				if(intRtnVal <= 0) { 
					
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
		
						gdRes.setMessage(sRTN_MSG + " <br> 정상적으로 스케쥴까지 등록했습니다.");		
			
					} 	
//				}	
			ydUtils.putLog(szSessionName, szMethodName, sRTN_MSG, YdConstant.DEBUG);
			}
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return gdRes;
	}  //end of updMtlUnitMvstkReg
	
	/**
	 * 야드관리 > 코일제품창고 > 저장관리 > 열단위이적등록/스판단위이적등록  (이적지시)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updColUnitMvstkReg(GridData gdReq) throws DAOException {
		
		YdDelegate ydDelegate 	= new YdDelegate();
		YdWrkbookDao ydWrkbookDao       = new YdWrkbookDao();//작업예약 DAO
		String szMsg			= "";
		String szMethodName		= "updColUnitMvstkReg";
		String szOperationName	= "제품창고 열단위 이적 처리";
		
		GridData gdRes 			= null;
		EJBConnector ejbConn 	= null;
		String sRTN_CD			= "";
		String sRTN_MSG			= "";
		String szTemp   		= "";
		String szToColGp 		= "";
		String szTCar 			= "";
		String szYD_SCH_CD 		= "";
		String szYD_TO_LOC_GUIDE  = "";
		String sYD_USER_ID  	= "";
		int intStlCnt         	= 0;
		int intStlSchCnt        = 0;
		int intRtnVal        	= 0;
		
		String sUP_POS 			= "";
		JDTORecord outRecord    = JDTORecordFactory.getInstance().create();
		JDTORecord outRecord1   = JDTORecordFactory.getInstance().create();
		JDTORecord outRecord2   = JDTORecordFactory.getInstance().create();
		JDTORecord outRecord21  = JDTORecordFactory.getInstance().create();
		JDTORecord outRecord22  = JDTORecordFactory.getInstance().create();
		JDTORecord recInTemp    = JDTORecordFactory.getInstance().create();
		JDTORecord inRecord2 	= JDTORecordFactory.getInstance().create();
		JDTORecord inRecord1 	= JDTORecordFactory.getInstance().create();
		JDTORecord inRecord     = JDTORecordFactory.getInstance().create();
		JDTORecord outRecord4   = JDTORecordFactory.getInstance().create();
		
		JDTORecordSet rsResult2 = null; 
		String sYD_GP			= "";
		String sYD_TCAR_SCH_ID  = "";
		String sYD_AIM_BAY_GP	= "";
		String sSND_FLAG		= "N";
		String sYD_WBOOK_ID     = "";
		String sFirstYD_WBOOK_ID     = "";
		String sCURR_PROG_CD     = "";
		String sTRANS_ORD_SEQNO     = "";
		
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
				JDTORecord [] inRcd =  new JDTORecord[1];
				inRcd[0] = JDTORecordFactory.getInstance().create();
				
				inRcd[0].setField("FROM_YD_STK_COL_GP", szTemp.substring(0, 6));
				inRcd[0].setField("TO_YD_STK_COL_GP", szToColGp);
				inRcd[0].setField("YD_WRK_PLAN_TCAR", szTCar); // 대차
				
				ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
				outRecord1 = (JDTORecord)ejbConn.trx("getStkColTCarChk", new Class[] { JDTORecord[].class }, new Object[] { inRcd });
				sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
				sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
				if (!sRTN_CD.equals("1")) {
					gdRes.setMessage(sRTN_MSG);		
					m_ctx.setRollbackOnly();
					return gdRes;
				} 

				
				//------------------------------------------------------------------------------------------------
				
				
				//To 위치 가이드
				szYD_TO_LOC_GUIDE = szToColGp ;
//2단  처리				
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
						ydUtils.putLog(szSessionName, szMethodName, "작업예약:" +sYD_WBOOK_ID , YdConstant.DEBUG);
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
							
							// 상차 가능 CHECK
							inRecord2 = JDTORecordFactory.getInstance().create();
							inRecord2.setField("YD_SCH_CD"		, szYD_SCH_CD);//스케줄코드
							inRecord2.setField("YD_WBOOK_ID"	, sYD_WBOOK_ID);  //
							ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
							outRecord4 = (JDTORecord)ejbConn.trx("getStkColTCarUpChk3", new Class[] { JDTORecord.class }, new Object[] { inRecord2 });
							
							sCURR_PROG_CD	= StringHelper.evl(outRecord4.getFieldString("CURR_PROG_CD"), "0");
							sTRANS_ORD_SEQNO	= StringHelper.evl(outRecord4.getFieldString("TRANS_ORD_SEQNO"), "0");

							
							if(!sTRANS_ORD_SEQNO.equals("0") && sCURR_PROG_CD.equals("M")){
								szMsg = "lot편성된 보관매출재는 동간 이적이 불가능 합니다. ";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
								gdRes.setMessage(szMsg);		
								m_ctx.setRollbackOnly();
								return gdRes;					
							}
							
							
//							 설비 check 하여 	
//							 대차작업 송신 여부
							inRecord2 = JDTORecordFactory.getInstance().create();
							inRecord2.setField("YD_WBOOK_ID"			, sYD_WBOOK_ID);

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
//1단  처리				
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
		
						ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
						outRecord = (JDTORecord)ejbConn.trx("WrkBookInsertProc", new Class[] { JDTORecord.class }, new Object[] { inRecord });
						sRTN_CD		= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
						sRTN_MSG	= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
						sYD_WBOOK_ID= StringHelper.evl(outRecord.getFieldString("YD_WBOOK_ID"), "");
						
						ydUtils.putLog(szSessionName, szMethodName, "작업예약:" +sYD_WBOOK_ID , YdConstant.DEBUG);
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
						ydUtils.putLog(szSessionName, szMethodName, "첫작업예약:" +sFirstYD_WBOOK_ID , YdConstant.DEBUG);	
						if((szYD_SCH_CD.substring(2, 4).equals("TC")) && (sSND_FLAG.equals("N"))){
//							 설비 check 하여 	
//							 대차작업 송신 여부
							inRecord2 = JDTORecordFactory.getInstance().create();
							inRecord2.setField("YD_WBOOK_ID"			, sYD_WBOOK_ID);

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
	 * 
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
		String szMethodName		= "getToDongUseCount";
		try{
			
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			gdRes = (GridData)ejbConn.trx("getToDongUseCount", new Class[] { GridData.class }, new Object[] { inDto });

			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		return gdRes;
	} // end of getSpanMvList
	
	
	
	/**
	 * 야드관리 > 코일제품창고 > 기준관리 > 스케줄기준관리 조회 (화면:스케줄기준관리)
	 *
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @throws JDTOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.05.31
	 */
	public GridData getSchRuleMgtList(GridData inDto) throws JDTOException {
		
	
		String szMethodName     = "getSchRuleMgtList";
		String szLogMsg = "";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;

		try{
			
			szLogMsg = "JSP-FACADE [스케줄기준 조회 (화면:스케줄기준관리)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);

			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);

			recordSet = (JDTORecordSet) ejbConn.trx("getSchRuleMgtList",
					new Class[] { JDTORecord.class }, new Object[] { inRecord });
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "JSP-FACADE [스케줄기준 조회 (화면:스케줄기준관리)] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		

		return gdRes;
	}  // End Of getSchRuleMgtList
	
	
	/**
	 *  야드관리 > 코일제품창고 > 기준관리 > 스케줄기준관리 (수정)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws JDTOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.05.31
	 */
	public GridData updSchRuleMgt(GridData inDto) throws JDTOException {
	
		String szMethodName="updSchRuleMgt";
		String szLogMsg = ""; 
		String szOperationName	= "스케줄 기준관리 (수정)";
		JDTORecord outRecord   	= JDTORecordFactory.getInstance().create();
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			
			szLogMsg = "JSP-FACADE [ " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);			
			
			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			outRecord = (JDTORecord) ejbConn.trx("updSchRuleMgt", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			
			String sRTN_CD	= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
			String sRTN_MSG	= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
			if ("0".equals(sRTN_CD)) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
				return gdRes;	
			}	
			gdRes.setMessage("정상적으로 스케줄 기준관리 되었습니다.");		
			
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		szLogMsg = "JSP-FACADE [ " + szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}  //end of updSchRuleMgt
	
	
	/**
	 * 야드관리 > 코일제품창고 > 설비관리 > 대차스케줄관리  --> 대차상태설정팝업 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws JDTOException
	 * @작성자 : 박지열
	 * @작성일  : 2010.06.10
	 */
	public GridData getCoilYdTcarStsSet(GridData inDto) throws JDTOException {
		//LOG
		String szLogMsg = null;
		String szRtnMsg = null;
		String szMethodName="getCoilYdTcarStsSet";	
		String szOperationName	= "대차설비조회";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdTcarStsSet", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			//조회성공메세지를 설정
			gdRes.setMessage(YdConstant.RETN_CD_SUCCESS);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		
		
		return gdRes;
	}
	
	/**
	 *  야드관리 > 코일제품창고 > 설비관리 > 대차스케줄관리  --> 대차상태설정 수정(설비 고장/정상 설정)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws JDTOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.10
	 */
	public GridData updCoilYdTcarStsSet(GridData inDto) throws JDTOException {
	 
		String szMethodName="updCoilYdTcarStsSet";
		String[] szRtnMsg = null;
		String rtnMsg = YdConstant.RETN_CD_SUCCESS;
		String szLogMsg = "";
		String szOperationName	= "설비 고장/정상 설정";
		JDTORecord outRecord   	= JDTORecordFactory.getInstance().create();
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);

			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);		
			
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			outRecord = (JDTORecord) ejbConn.trx("updCoilYdTcarStsSet", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

			String sRTN_CD	= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
			String sRTN_MSG	= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
			if ("0".equals(sRTN_CD)) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
				return gdRes;	
			}	
			gdRes.setMessage("정상적으로 설비고장/정상 설정 되었습니다.");		
			
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		
		return gdRes;	
	}
	
	/**
	 *  야드관리 > 코일제품창고 > 설비관리 > 대차스케줄관리  --> 입고대차 지정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws JDTOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.10
	 */
	public GridData updCoilYdTcarStsSetRcpt(GridData inDto) throws JDTOException {
	 
		String szMethodName="updCoilYdTcarStsSetRcpt";
		String[] szRtnMsg = null;
		String rtnMsg = YdConstant.RETN_CD_SUCCESS;
		String szLogMsg = "";
		String szOperationName	= "입고대차 설정";
		JDTORecord outRecord   	= JDTORecordFactory.getInstance().create();
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			 
			
			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);

			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);		
			
			outRecord = (JDTORecord)ejbConn.trx("updCoilYdTcarStsSetRcpt",
						new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			String sRTN_CD	= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
			String sRTN_MSG	= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
			if ("0".equals(sRTN_CD)) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
				return gdRes;	
			}	
			gdRes.setMessage("정상적으로 수정되었습니다.");		
			
			
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		gdRes.setStatus("true");
		
		return gdRes;	
	}
	
	
	/**
	 *  야드관리 > 코일제품창고 > 설비관리 > 대차스케줄관리  --> 대차 clear
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws JDTOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.10
	 */
	public GridData updCoilYdTcarClear(GridData inDto) throws JDTOException {
	 
		String szMethodName="updCoilYdTcarClear";
		String[] szRtnMsg = null;
		String rtnMsg = YdConstant.RETN_CD_SUCCESS;
		String szLogMsg = "";
		String szOperationName	= "대차 상태 초기화";
		JDTORecord outRecord   	= JDTORecordFactory.getInstance().create();
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			 
			
			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);

			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);		
			
			outRecord = (JDTORecord)ejbConn.trx("updCoilYdTcarClear",
						new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			String sRTN_CD	= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
			String sRTN_MSG	= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
			if ("0".equals(sRTN_CD)) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
				return gdRes;	
			}	
			gdRes.setMessage("정상적으로 수정되었습니다.");		
			
			
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		gdRes.setStatus("true");
		
		return gdRes;	
	}
	
	
	/**
	 *  야드관리 > 코일제품창고 > 설비관리 > 대차스케줄관리  --> 대차상태설정 수정 (설비 ON_LINE, OFF_LINE 설정)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.15 
	 */
	public GridData updCoilYdTcarStsSetCrnMode(GridData inDto) throws JDTOException {
		//LOG
		String[] szRtnMsg = null;
		String rtnMsg = YdConstant.RETN_CD_SUCCESS;
		String szMethodName="updCoilYdTcarStsSetCrnMode";
		String szLogMsg = "";
		String szOperationName	= "설비 On-Line/Off-Line 설정";
		JDTORecord outRecord   	= JDTORecordFactory.getInstance().create();
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			
			szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);		
			
			JDTORecord [] inRecord = CmUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			outRecord = (JDTORecord)ejbConn.trx("updCoilYdTcarStsSetCrnMode", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			String sRTN_CD	= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
			String sRTN_MSG	= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
			if ("0".equals(sRTN_CD)) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
				return gdRes;	
			}	
			gdRes.setMessage("정상적으로 설비MODE 수정되었습니다.");		
			
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		
		szLogMsg = "[JSP-FACADE  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		
		return gdRes;	
	}
	
		
	/**
	 *  공대차 스케줄 호출 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	public GridData CoilYdTcarStsSetTcarA(GridData gdReq) throws JDTOException {
		//LOG
		String szLogMsg = null;
		String szRtnMsg = null;
		String szMethodName="CoilYdTcarStsSetTcarA";
		JDTORecord outRecord   	= JDTORecordFactory.getInstance().create();
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{

			szLogMsg = "JSP-FACADE [ 공대차 스케줄 호출  ] 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
	
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);			
			
			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			outRecord = (JDTORecord)ejbConn.trx("CoilYdTcarStsSetTcarA", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			String sRTN_CD	= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
			String sRTN_MSG	= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
			if ("0".equals(sRTN_CD)) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
				return gdRes;	
			}	
			gdRes.setMessage(sRTN_MSG  + "<br> 정상적으로 공대차 스케줄 호출되었습니다.");		

			szLogMsg = "[JSP Facade]공대차 스케줄 호출 처리 : " + szRtnMsg;
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}

		szLogMsg = "JSP-FACADE [ 공대차 스케줄 호출  ] 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}  //end of CoilYdTcarStsSetTcarA
	
	
	
	
	/**
	 *  출발 실적
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	public GridData CoilYdTcarStsSetTcarB(GridData gdReq) throws JDTOException {
		//LOG
		String szLogMsg = null;
		String szRtnMsg = null;
		String szMethodName="CoilYdTcarStsSetTcarB";
		JDTORecord outRecord   	= JDTORecordFactory.getInstance().create();
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			
			szLogMsg = "JSP-FACADE [ 출발 실적 ] 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);

			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			outRecord = (JDTORecord)ejbConn.trx("CoilYdTcarStsSetTcarB", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			String sRTN_CD	= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
			String sRTN_MSG	= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
			if ("0".equals(sRTN_CD)) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
				return gdRes;	
			}	
			gdRes.setMessage("정상적으로 대차 출발 실적 호출되었습니다.");		

			szLogMsg = "[JSP Facade]대차 출발 실적 호출 처리 : " + szRtnMsg;
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		szLogMsg = "JSP-FACADE [  출발 실적 ] 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}  //end of CoilYdTcarStsSetTcarB
	
	
	/**
	 *  도착실적
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	public GridData CoilYdTcarStsSetTcarC(GridData gdReq) throws JDTOException {
		//LOG
		String szLogMsg = null;
		String szRtnMsg = null;
		String szMethodName="CoilYdTcarStsSetTcarC";
		JDTORecord outRecord   	= JDTORecordFactory.getInstance().create();
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			
			szLogMsg = "JSP-FACADE [  도착실적  ] 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			
			
			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			outRecord = (JDTORecord)ejbConn.trx("CoilYdTcarStsSetTcarC", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			String sRTN_CD	= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
			String sRTN_MSG	= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
			if ("0".equals(sRTN_CD)) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
				return gdRes;	
			}	
			gdRes.setMessage("정상적으로 대차 도착실적 호출  호출되었습니다.");		

			szLogMsg = "[JSP Facade]대차 도착실적 호출 처리 : " + szRtnMsg;
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		szLogMsg = "JSP-FACADE [  도착실적  ] 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}  //end of CoilYdTcarStsSetTcarC
	
	
	/**
	 *  완료 실적
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	public GridData CoilYdTcarStsSetTcarD(GridData gdReq) throws JDTOException {
		//LOG
		String szLogMsg = null;
		String szRtnMsg = null;
		String szMethodName="CoilYdTcarStsSetTcarD";
		JDTORecord outRecord   	= JDTORecordFactory.getInstance().create();
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			
			szLogMsg = "JSP-FACADE [  완료 실적 ] 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);

			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			outRecord = (JDTORecord)ejbConn.trx("CoilYdTcarStsSetTcarD", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			String sRTN_CD	= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
			String sRTN_MSG	= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
			if ("0".equals(sRTN_CD)) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
				return gdRes;	
			}	
			gdRes.setMessage("정상적으로대차 완료 실적 호출되었습니다.");		

			szLogMsg = "[JSP Facade]대차 완료 실적 호출 처리 : " + szRtnMsg;
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		szLogMsg = "JSP-FACADE [  완료 실적 ] 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}  //end of CoilYdTcarStsSetTcarD
	
	

	/**
	 *  현재동 변경
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	public GridData CoilYdTcarStsSetTcarE(GridData gdReq) throws JDTOException {
		//LOG
		String szLogMsg = null;
		String szRtnMsg = null;
		String szMethodName="CoilYdTcarStsSetTcarE";
		JDTORecord outRecord   	= JDTORecordFactory.getInstance().create();
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			
			szLogMsg = "JSP-FACADE [  현재동 변경 ] 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			
			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			outRecord = (JDTORecord)ejbConn.trx("CoilYdTcarStsSetTcarE", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			String sRTN_CD	= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
			String sRTN_MSG	= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
			if ("0".equals(sRTN_CD)) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
				return gdRes;	
			}	
			gdRes.setMessage("정상적으로 현재동 변경 되었습니다.");		

			
			
			szLogMsg = "[JSP Facade]현재동 변경 처리 : " + szRtnMsg;
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		szLogMsg = "JSP-FACADE [  현재동 변경 ] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}  //end of CoilYdTcarStsSetTcarE
	
	
	/**
	 * HOME 동 변경
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	
	public GridData CoilYdTcarStsSetTcarF(GridData gdReq) throws JDTOException {
		//LOG
		String szLogMsg = null;
		String szRtnMsg = null;
		String szMethodName="CoilYdTcarStsSetTcarF";
		JDTORecord outRecord   	= JDTORecordFactory.getInstance().create();
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			
			szLogMsg = "JSP-FACADE [ HOME 동 변경]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);

			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(gdReq);
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			outRecord = (JDTORecord)ejbConn.trx("CoilYdTcarStsSetTcarF", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			String sRTN_CD	= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
			String sRTN_MSG	= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
			if ("0".equals(sRTN_CD)) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
				return gdRes;	
			}	
			gdRes.setMessage("정상적으로 HOME 동  변경 되었습니다.");		


			szLogMsg = "[JSP Facade]HOME 동 변경 처리 : " + szRtnMsg;
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		szLogMsg = "JSP-FACADE [HOME 동 변경]끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}  //end of CoilYdTcarStsSetTcarF

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
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getEqpTCarSchInfo", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	} //end of getEqpTCarSchInfo

	/**
	 *  야드현황 조회1
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getYdMgtList1(GridData inDto) throws JDTOException {
		 
		String szMethodName = "getYdMgtList1";
		String szLogMsg = "";
		String szOperationName = "야드현황 조회1";
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getYdMgtList1", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	} //end of getYdMgtList1
	
	
	/**
	 *  야드현황 조회1
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getYdTotalMgtList(GridData inDto) throws JDTOException {
		 
		String szMethodName = "getYdTotalMgtList";
		String szLogMsg = "";
		String szOperationName = "야드현황 조회1";
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getYdTotalMgtList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	} //end of getYdTotalMgtList
	
	/**
	 *  야드현황 조회1
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getYdMgtList2(GridData inDto) throws JDTOException {
		 
		String szMethodName = "getYdMgtList2";
		String szLogMsg = "";
		String szOperationName = "야드현황 조회2";
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getYdMgtList2", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	} //end of getYdMgtList2
	
	/**
	 *  야드현황 조회5
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getYdMgtList5(GridData inDto) throws JDTOException {
		 
		String szMethodName = "getYdMgtList5";
		String szLogMsg = "";
		String szOperationName = "야드현황 조회5";
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getYdMgtList5", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	} //end of getYdMgtList5
	
	
	/**
	 *  대차 작업 대기 현황
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getTCarWrkWaitListCoil(GridData inDto) throws JDTOException {
		 
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
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getTCarWrkWaitListCoil", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
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
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getTCarSchWrkMtl", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	} //end of getTCarSchWrkMtl
	
	
	/**
	 *  야드관리 > 코일제품창고 > 설비관리 > 대차스케줄관리  --> 대차이동구간변경 팝업 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getTCarYdGpMgt(GridData inDto) throws JDTOException {
		
		String szMethodName = "getTCarYdGpMgt";
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
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getTCarYdGpMgt", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	} //end of getTCarSchWrkMtl
	
	
	
	
	/**
	 *  야드관리 > 코일제품창고 > 설비관리 > 대차스케줄관리  --> 대차이동구간변경 팝업 (이동구간 수정)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws JDTOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.05.31
	 */
	public GridData updTCarYdGpMgt(GridData inDto) throws JDTOException {
	
		JDTORecord outRecod = null;
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		String szMethodName = "updTCarYdGpMgt";
		try{
			
			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			outRecod = (JDTORecord) ejbConn.trx("updTCarYdGpMgt", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			
			
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);
			
			if(!outRecod.getFieldString("RTN_CD").equals("1")){	
				ydUtils.putLog(szSessionName, "updTCarYdGpMgt", outRecod.getFieldString("RTN_MSG"), YdConstant.INFO);
				//오류발생일 경우 Rollback 처리 
				m_ctx.setRollbackOnly();
				
			}
			gdRes.setMessage(outRecod.getFieldString("RTN_MSG"));
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		return gdRes;
	}  //end of updTCarYdGpMgt
	
	
	
	/**
	 * 적치베드 조회 (저장집합코드) 화면:위치검색순서관리 하단 좌측 그리드 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.07.05
	 */
	public GridData getSpanbyLowInfo(GridData inDto){
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		String szMethodName = "getSpanbyLowInfo";
		try{
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			gdRes = (GridData)ejbConn.trx("getSpanbyLowInfo", new Class[] { GridData.class }, new Object[] { inDto });

			//gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}


		return gdRes;
	} // end of getSpanbyLowInfo
	
	
	/**
	 * 야드관리 > 코일제품창고 > 기준관리 > 저장위치용도관리  목록조회
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
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			gdRes = (GridData)ejbConn.trx("getStrlocUsgSetList", new Class[] { GridData.class }, new Object[] { inDto });
			
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		
		return gdRes;
	} // end of getSpanbyLowInfo
	
	
	/**
	 * 야드관리 > 코일제품창고 > 기준관리 > 저장위치용도관리  등록
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
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			gdRes = (GridData)ejbConn.trx("updStrlocUsgSet", new Class[] { GridData.class }, new Object[] { inDto });
			
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		
		return gdRes;
	} // end of getSpanbyLowInfo
	
	
	/**
	 * 야드관리 > 코일제품창고 > 기준관리 > 위치검색순서관리   적치구분 콤보리스트 조회 
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.07.07
	 */
	public GridData getYDB700ComboList(GridData inDto){
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		String szMethodName = "getYDB700ComboList";
		try{
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			gdRes = (GridData)ejbConn.trx("getYDB700ComboList", new Class[] { GridData.class }, new Object[] { inDto });
			
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		
		return gdRes;
	} // end of getYDB700ComboList
	
	/**
	 * 야드관리 > 코일제품창고 > 저장관리 > 통합이적지시  외경군, 폭구분 체크.
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.07.07
	 */
	public GridData getStkColWidthGp(GridData inDto){
		GridData      		gdRes     		= null;
		EJBConnector 		ejbConn   		= null;
		
		JDTORecordSet   	outRecSet  		= null;
		String szMethodName = "getStkColWidthGp";
		try{
			
			JDTORecord inRecord =  ydComUtil.genParamToJDTORecord(inDto);
			
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			outRecSet = (JDTORecordSet)ejbConn.trx("getStkColWidthGp", new Class[] { JDTORecord.class }, new Object[] { inRecord });
			
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);
			
			gdRes = CmUtil.genGridData(inDto, outRecSet);
			
			
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		
		return gdRes;
	} // end of getStkColWidthGp
	

	/**
	 * 차량작업관리 차량스케줄 조회 코일
	 * 심명순 (090727)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdGdsCarSchCoil(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdGdsCarSchCoil";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdGdsCarSchCoil", inRecord);
			//gdRes = CmUtil.genGridData(inDto , recordSet);
			gdRes     = CmUtil.genResGridData(inDto , recordSet);

		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilYdGdsCarSch
	
	
	/**
	 * 차량작업관리 차량스케줄 조회 코일
	 * 심명순 (090727)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData getCoilYdGdsCarSchCoilNEW(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCoilYdGdsCarSchCoilNEW";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdGdsCarSchCoilNEW", inRecord);
			//gdRes = CmUtil.genGridData(inDto , recordSet);
			gdRes     = CmUtil.genResGridData(inDto , recordSet);

		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilYdGdsCarSchCoilNEW
	
	/**
	 * 코일야드 차량작업관리 - 차량작업상세내역 코일
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public GridData getCoilCarWorkCoil(GridData inDto) throws JDTOException {
		//LOG
		String szMsg        = null;
		String szMethodName = "getCoilCarWorkCoil";
		String szOperationName = "차량작업관리 - 차량작업상세내역";
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szMsg = "[JSP Facade] " + szOperationName + " 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilCarWorkCoil", inRecord);
			//gdRes = CmUtil.genGridData(inDto , recordSet);
			gdRes     = CmUtil.genResGridData(inDto , recordSet);

		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getSlabTotCarWork

	/**
	 * 준비스케줄재료LIST - 상차LOT편성 시 사용
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 */
	public GridData getYdPrepmtlNStockByPrepSchIdCoil(GridData inDto) throws JDTOException {
		/*
		 * 업무기준 : 준비스케줄테이블에서 준비스케줄ID ASC 를 조회
		 * 수정자 : 임춘수
		 * 수정일 : 2009.09.28
		 */
		String szMethodName = "getYdPrepmtlNStockByPrepSchIdCoil";
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
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getYdPrepmtlNStockByPrepSchIdCoil", inRecord);
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
	} //end of getYdPrepmtlNStockByPrepSchId
	/**	
	 * 준비스케줄ID LIST - 상차LOT편성 시 선택박스 표시 코일
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
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getYdPrepSchIdList", inRecord);
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
	} //end of getYdPrepSchIdList
	/**
	 *  코일제품창고 포인트개폐 처리 코일 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData procCoilYdGdsPntUnitCLCoil(GridData inDto) throws DAOException {
		//LOG
		String szMsg = "";
		String szMethodName="procCoilYdGdsPntUnitCLCoil";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		
		try{
			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(inDto);
			
			
			szMsg = "포인트개폐 전송처리 시작 ==>";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			ejbConn.trx("procCoilYdGdsPntUnitCLCoil", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);
			
			
			szMsg = " 포인트개폐 전송처리 ===> 끝";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return gdRes;
	}  //end of procCoilYdGdsPntUnitCL
	/**
	 *  입동순서 변경
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData procCoilYdGdsBayInWoSeqChangCoil(GridData inDto) throws DAOException {
		//LOG
		String szMsg = "";
		String szMethodName="procCoilYdGdsBayInWoSeqChangCoil";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		
		try{
			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(inDto);
			
			
			szMsg = "차량작업관리 차량 POINT작업현황 입동순서 변경 작업 시작 ==>";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			ejbConn.trx("procCoilYdGdsBayInWoSeqChangCoil", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);
			
			
			szMsg = "차량작업관리 차량 POINT작업현황 입동순서 변경 작업  끝";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return gdRes;
	}  //end of procCoilYdGdsBayInWoSeqChang
	
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
		YDDataUtil  yddatautil = new YDDataUtil();
		try{
			JDTORecord [] inRecord =  ydComUtil.genGridToJDTORecordAll(inDto);
			
			
			szMsg = "[JSP Facade] 차량작업관리 초기화  전송처리 시작  ==>";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);
	 
			String sYdGp = yddatautil.setDataDefault(inRecord[0].getField("YD_GP"), "");
			
			if(sYdGp.equals("1")||sYdGp.equals("3")){ 
				//A,B열연 취소처리
				
				JDTORecord recPara = JDTORecordFactory.getInstance().create();  
								
				recPara.setField("YD_GP", 					sYdGp);
				recPara.setField("JMS_TC_CD", 				"DMYDR060");
				recPara.setField("CURR_PROG_CD", 			"N");
				recPara.setField("TRANS_ORD_DT", 			yddatautil.setDataDefault(inRecord[0].getField("TRANS_ORD_DT"), ""));
				recPara.setField("TRANS_ORD_SEQNO", 		yddatautil.setDataDefault(inRecord[0].getField("TRANS_ORD_SEQNO"), ""));
				recPara.setField("CARD_NO", 				yddatautil.setDataDefault(inRecord[0].getField("CARD_NO"), ""));
		 	 
				ejbConn = new EJBConnector("default", "JNDIInternal", this);
				ejbConn.trx("receiveCancel", new Class[] { JDTORecord.class }, new Object[] { recPara });
				
			}else{
				//C열연 취소처리
				ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
				outRecord2 = (JDTORecord)ejbConn.trx("updCarWrMgt", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			
				String sRTN_CD	= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
				String sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
				if ("0".equals(sRTN_CD)) {
					gdRes.setMessage(sRTN_MSG);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}	
			}
					
			
			szMsg = "[JSP Facade] 차량작업관리 초기화  수정  전송처리 ===> 끝";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes.setMessage("정상적으로 초기화 처리 되었습니다.");
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return gdRes;
	}  //end of uptCarSch

	/**
	 * 코일 번호로 저장위치 조회 하기 
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.11
	 */
	public GridData getCoilTolyr(GridData inDto){
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		String szMethodName="getCoilTolyr";
		try{
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			gdRes = (GridData)ejbConn.trx("getCoilTolyr", new Class[] { GridData.class }, new Object[] { inDto });
			
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		
		return gdRes;
	} // end of getSpanbyLowInfo
	
	/**
	 * 제품야드 작업 실적 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 */
	
	public GridData getCoilGdsWrkRsltQty(GridData inDto) throws JDTOException {
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		String 			szMsg 			= "";
		String 			szMethodName	="getCoilGdsWrkRsltQty";
		
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilGdsWrkRsltQty", inRecord);			                                                          
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			return gdRes;
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
	}	// end of getCoilGdsWrkRsltQty
	
	/**
	 * 차량출발등록(PDA) 목록 조회
	 * @ejb.interface-method
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.28 
	 */
	public JDTORecordSet getCarStartMgtList(JDTORecord inRecord) throws JDTOException {
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		String 			szMethodName	="getCarStartMgtList";
		try{
			
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCarStartMgtList", inRecord);			                                                          
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:", YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		return recordSet;
		
	}
	
	/**
	 * 차량출발등록(PDA) 목록 조회
	 * @ejb.interface-method
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.28 
	 */
	public JDTORecordSet getCarArrivalMgtList(JDTORecord inRecord) throws JDTOException {
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		String 			szMethodName	="getCarArrivalMgtList";
		try{
			
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCarArrivalMgtList", inRecord);			                                                          
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:", YdConstant.ERROR);
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		
		return recordSet;
		
	}	
	/**
	 * 동별 야드포인트코드(PDA) 조회(selectBox 용)
	 * @ejb.interface-method
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.28 
	 */
	public JDTORecord getYdPointCdList(JDTORecord inRecord) throws JDTOException {
		EJBConnector  	ejbConn   	= null;
		JDTORecord 		outRcd 		= null;
		
		try{
			
			ejbConn = new EJBConnector("default", this);
			outRcd = (JDTORecord) ejbConn.trx("CoilGdsJspSeEJB", "getYdPointCdList", inRecord);			                                                          
			
		}catch(Exception e){
			e.printStackTrace();
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		
		return outRcd;
		
	}	
	
	
	/**
	 * 차량출발등록(PDA) 출발등록
	 * @ejb.interface-method
	 * @param JDTORecord
	 * @return JDTORecord
	 * @throws JDTOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.29
	 */
	public JDTORecord updCarStartMgt(JDTORecord inRecord) throws JDTOException {
		EJBConnector  	ejbConn   	= null;
		JDTORecord 		outRcd	  	= null;
		
		String 			sRTN_CD		= "";
		String 			sHdn_chk	= "";
		
		try {
			
			sHdn_chk = inRecord.getFieldString("hdn_chk");
			
			JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CAR_SCH_ID", inRecord.getFieldString("hdn_sch_id_"+sHdn_chk));
			
			ejbConn = new EJBConnector("default", this);
			outRcd = (JDTORecord) ejbConn.trx("CoilGdsJspSeEJB", "procCarStart", recPara);	
			
			sRTN_CD		= StringHelper.evl(outRcd.getFieldString("RTN_CD"), "0");
			
			if(!sRTN_CD.equals("1")){
				this.m_ctx.setRollbackOnly();
				outRcd.setField("RTN_CD","-1");
				outRcd.setField("RTN_MSG","처리중 오류 발생");
				return outRcd;
			}
			
		} catch (Exception e) {
			this.m_ctx.setRollbackOnly();
			e.printStackTrace();
		}
		
		return outRcd;
	}
	
	/**
	 * 차량도착등록(PDA) 도착등록
	 * @ejb.interface-method
	 * @param JDTORecord
	 * @return JDTORecord
	 * @throws JDTOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.29
	 */
	public JDTORecord updCarArrivalMgt(JDTORecord inRecord) throws JDTOException {
		EJBConnector  	ejbConn   	= null;
		JDTORecord 		outRcd	  	= null;
		
		String 			sHdn_chk	= "";
		
		try {

			/*
			Z5RecvDeEJBBean
			recvZ5DML001
			
			USER_ID
			CARD_NO
			FNL_MOD_PGM_ID
			TC_CODE : Z5DML001
			*/
			
			
			
			sHdn_chk = inRecord.getFieldString("hdn_chk");
			
			JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
			recPara.setField("CARD_NO", inRecord.getFieldString("hdn_card_no_"+sHdn_chk));
			recPara.setField("USER_ID", inRecord.getFieldString("YD_USER_ID"));
			recPara.setField("TC_CODE", "Z5DML001");
			
			// 출하 메소드 호출 
			ejbConn = new EJBConnector("inisteelApp","JNDIZ5RecvDeEJB",this);
			//ejbConn = new EJBConnector("default", this);
			ejbConn.trx("recvZ5DML001", recPara);	
			
			
			outRcd   = JDTORecordFactory.getInstance().create();
			
			outRcd.setField("RTN_CD","1");
			outRcd.setField("RTN_MSG","차량도착처리 전문전송 완료");
			
		} catch (Exception e) {
			//this.m_ctx.setRollbackOnly();
			e.printStackTrace();
		}
		
		return outRcd;
	}
	
	
	/**
	 * 차량출발처리 (C열연 공통)
	 * @ejb.interface-method
	 * @param JDTORecord   (YD_CAR_SCH_ID)
	 * @return JDTORecord
	 * @throws JDTOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.29
	 */
	public JDTORecord procCarStart(JDTORecord inRecord) throws JDTOException {
		EJBConnector  	ejbConn   	= null;
		JDTORecord 		outRcd	  	= null;
		
		try {
			//YD_CAR_SCH_ID
			ejbConn = new EJBConnector("default", this);
			outRcd = (JDTORecord) ejbConn.trx("CoilGdsJspSeEJB", "procCarStart", inRecord);	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outRcd;
	}
	
	/**
	 * 차량도착처리 (C열연 공통)
	 * @ejb.interface-method
	 * @param JDTORecord  (YD_CAR_SCH_ID, YD_STK_COL_GP)
	 * @return JDTORecord
	 * @throws JDTOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.29
	 */
	public JDTORecord procCarArrival(JDTORecord inRecord) throws JDTOException {
		EJBConnector  	ejbConn   	= null;
		JDTORecord 		outRcd	  	= null;
		
		try {
			//YD_STK_COL_GP
			//YD_CAR_SCH_ID
			ejbConn = new EJBConnector("default", this);
			outRcd = (JDTORecord) ejbConn.trx("CoilGdsJspSeEJB", "procCarArrival", inRecord);	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outRcd;
	}
	
	/**
	 * 차량 작업 관리 화면 :차량도착처리
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public GridData updCarArrival(GridData inDto) throws DAOException {
		//		LOG
		String szMsg = "";
		String szMethodName="updCarArrival";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		String szRtnMsg = "";
		JDTORecord outRecord2   	= JDTORecordFactory.getInstance().create();		
		
		try{
			JDTORecord [] inRecord =  ydComUtil.genGridToJDTORecordAll(inDto);
			
			
			szMsg = "[JSP Facade] 차량작업관리 차량도착처리  전송처리 시작  ==>";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);

			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			outRecord2 = (JDTORecord)ejbConn.trx("updCarArrival1", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

			String sRTN_CD	= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
			String sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
			if ("0".equals(sRTN_CD)) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
				return gdRes;	
			}	
					
			
			szMsg = "[JSP Facade] 차량작업관리 차량도착처리 전송처리 ===> 끝";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes.setMessage("정상적으로 차량도착처리 되었습니다.");
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
			
		}
		return gdRes;
	}  //end of uptCarSch
	
	/**
	 * 차량 작업 관리 화면 :차량도착처리
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public GridData CarArrivalNEW(GridData inDto) throws DAOException {
		//		LOG
		String szMsg = "";
		String szMethodName="CarArrivalNEW";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		String szRtnMsg = "";
		JDTORecord outRecord2   	= JDTORecordFactory.getInstance().create();		
		
		try{
			JDTORecord [] inRecord =  ydComUtil.genGridToJDTORecordAll(inDto);
			
			
			szMsg = "[JSP Facade] 차량작업관리 차량도착처리  전송처리 시작  ==>";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);

			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			outRecord2 = (JDTORecord)ejbConn.trx("CarArrivalNEW", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

			String sRTN_CD	= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
			String sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
			if ("0".equals(sRTN_CD)) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
				return gdRes;	
			}	
					
			
			szMsg = "[JSP Facade] 차량작업관리 차량도착처리 전송처리 ===> 끝";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes.setMessage("정상적으로 차량도착처리 되었습니다.");
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
			
		}
		return gdRes;
	}  //end of CarArrivalNEW
	
	
	
	/**
	 * 차량 작업 관리 화면 :차량출발처리
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public GridData updCarStart(GridData inDto) throws DAOException {
		//		LOG
		String szMsg 			= "";
		String szMethodName		= "updCarStart";
		
		GridData gdRes 			= null;
		EJBConnector ejbConn 	= null;
		String szRtnMsg 		= "";
		JDTORecord outRecord2  	= JDTORecordFactory.getInstance().create();		
		
		try{
			JDTORecord [] inRecord =  ydComUtil.genGridToJDTORecordAll(inDto);
			
			szMsg = "[JSP Facade] 차량작업관리 차량출발처리  전송처리 시작  ==>";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);

			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			outRecord2 = (JDTORecord)ejbConn.trx("updCarStart", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

			String sRTN_CD	= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
			String sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
			if ("0".equals(sRTN_CD)) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
				return gdRes;	
			}	
					
			
			szMsg = "[JSP Facade] 차량작업관리 차량출발처리 전송처리 ===> 끝";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes.setMessage("정상적으로 차량출발처리 되었습니다.");
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
			
		}
		return gdRes;
	}  //end of uptCarSch
	
	
	/**
	 * 동별 SCHDULE 정보 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 */
	
	public GridData getSchRuleList(GridData inDto) throws JDTOException {
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		String 			szMsg 			= "";
		String 			szMethodName	="getSchRuleList";
		
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getSchRuleList", inRecord);			                                                          
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			return gdRes;
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
	}	// end of getCoilGdsWrkRsltQty
	
	/**
	 * 야드관리 > 코일제품창고 > 기준관리 > 적치위치변경관리  목록조회
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.04.26
	 */
	public GridData getStrlocChgSetList(GridData inDto){
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		String szMethodName		= "getStrlocChgSetList";
		try{
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			gdRes = (GridData)ejbConn.trx("getStrlocChgSetList", new Class[] { GridData.class }, new Object[] { inDto });
			
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		
		return gdRes;
	} // end of getSpanbyLowInfo
	
	
	/**
	 * 야드관리 > 코일제품창고 > 기준관리 > 적치위치변경관리  등록
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.04.27
	 */
	public GridData updStrlocChgSet(GridData inDto){
		String szMsg 			= "";
		String szMethodName		= "updStrlocChgSet";
		
		GridData gdRes 			= null;
		EJBConnector ejbConn 	= null;
		String szRtnMsg 		= "";
		JDTORecord outRecord2  	= JDTORecordFactory.getInstance().create();		
		
		
		try{
			JDTORecord [] inRecord =  ydComUtil.genGridToJDTORecordAll(inDto);
			
			
			szMsg = "[JSP Facade] 적치열 변경 시작  ==>";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);

			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			outRecord2 = (JDTORecord)ejbConn.trx("updStrlocChgSet", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

			String sRTN_CD	= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
			String sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
			
			if ("0".equals(sRTN_CD)) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
				return gdRes;	
			}	
					
			
			szMsg = "[JSP Facade] 적치열 변경처리 ===> 끝";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes.setMessage("정상적으로 적치열 변경 되었습니다.");
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
			
		}
		return gdRes;

	} // end of getSpanbyLowInfo
	/**
	 * 야드관리 > 코일제품야드 > 야드재공관리 > 재료진도별 재공현황  
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
			
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilMtlProgIdInlnStat", inRecord);
			
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
	 * 제품 이송재료LIST
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
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getYdTransMtlList", inRecord);
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
	 * 크레인 출하작업 현황(PDA)
	 * @ejb.interface-method
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.28 
	 */
	public JDTORecordSet getCoilCraneCarWrkPDA(JDTORecord inRecord) throws JDTOException {
		EJBConnector  	ejbConn   	= null;
		JDTORecordSet 	outRcd 		= null;
		
		try{
			
			ejbConn = new EJBConnector("default", this);
			outRcd = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilCraneCarWrkPDA", inRecord);			                                                          
			
		}catch(Exception e){
			e.printStackTrace();
			throw new JDTOException(getClass().getName() + e.getMessage(), e);
		}
		
		return outRcd;
		
	}	
	/**
	 * 공장별 크레인 정보
	 * @ejb.interface-method
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.28 
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

			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);

			recordSet = (JDTORecordSet) ejbConn.trx("getCrnGp", new Class[] { JDTORecord.class }, new Object[] { inRecord });
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "JSP-FACADE [크레인구분 조회 (화면:스케줄기준관리] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		

		return gdRes;
	}  // End Of getCrnGp
	
	/**
	 *  C연주야드현황 조회1
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getCSlabYdMgtList(GridData inDto) throws JDTOException {
		 
		String szMethodName = "getCSlabYdMgtList";
		String szLogMsg = "";
		String szOperationName = "C연주야드현황 조회1";
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCSlabYdMgtList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	} //end of getCSlabYdMgtList
	
	/**
	 *  B열연야드현황 조회1
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getBSlabYdMgtList(GridData inDto) throws JDTOException {
		 
		String szMethodName = "getBSlabYdMgtList";
		String szLogMsg = "";
		String szOperationName = "B열연야드현황 조회1";
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getBSlabYdMgtList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	} //end of getBSlabYdMgtList
	
	/**
	 *  후판야드현황 조회1
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getPSlabYdMgtList(GridData inDto) throws JDTOException {
		 
		String szMethodName = "getㅔSlabYdMgtList";
		String szLogMsg = "";
		String szOperationName = "후판야드현황 조회1";
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getPSlabYdMgtList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	} //end of getPSlabYdMgtList
	
	/**
	 * 반납대상 긴급재 지정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilYdemergencyMgt(GridData inDto) throws DAOException {
		//		LOG
		String szMsg = "";
		String szMethodName="updCoilYdemergencyMgt";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		String szRtnMsg = "";
		JDTORecord outRecord2   	= JDTORecordFactory.getInstance().create();		
		
		try{
			JDTORecord [] inRecord =  ydComUtil.genGridToJDTORecordAll(inDto);
			
			
			szMsg = "[JSP Facade] 반송긴급재  시작  ==>";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);
			
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			outRecord2 = (JDTORecord)ejbConn.trx("updCoilYdemergencyMgt", new Class[] { JDTORecord[].class }, new Object[] { inRecord });

			String sRTN_CD	= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
			String sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
			if ("0".equals(sRTN_CD)) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
				return gdRes;	
			}	
					
			
			szMsg = "[JSP Facade] 반송긴급재 처리 ===> 끝";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes.setMessage("정상적으로 반송긴급재 처리 되었습니다.");
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
			
		}
		return gdRes;
	}  //end of updCoilYdemergencyMgt
	
////////////////////////////////////////////////////////////////////////
//C증설
	/**
	 * 코일제품야드 tracking 입고팝업 조회보급
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData getcoilGdsYdLineWrPpNew(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getcoilGdsYdLineWrPpNew";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getcoilGdsYdLineWrPpNew", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return gdRes;
	} //end of getcoilGdsYdLineWrPp
	
	/**
	 * 코일제품야드 tracking 입고팝업 조회보급
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData getcoilGdsYdLineWrPpGPack(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getcoilGdsYdLineWrPpGPack";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getcoilGdsYdLineWrPpGPack", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return gdRes;
	} //end of getcoilGdsYdLineWrPpGPack
	

	/**
	 * 코일제품야드 CHook 모니터링 팝업
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData getCHookcoilGdsYdPp(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getCHookcoilGdsYdPp";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCHookcoilGdsYdPp", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return gdRes;
	} //end of getCHookcoilGdsYdPp
	

	/**
	 * 코일제품야드 CHook 모니터링 팝업 insert
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData insCHookcoilGdsYdPp(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "insCHookcoilGdsYdPp";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "insCHookcoilGdsYdPp", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return gdRes;
	} //end of insCHookcoilGdsYdPp
	
	/**
	 *  압연일자별 재공현황
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData YdNextprocList(GridData inDto) throws JDTOException {
		 
		String szMethodName = "YdNextprocList";
		String szLogMsg = "";
		String szOperationName = "압연일자별 재공현황";
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "YdNextprocList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	} //end of getYdMgtList
	
	
	/**
	 *  공장별 재공현황
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData YdNextprocList2(GridData inDto) throws JDTOException {
		 
		String szMethodName = "YdNextprocList2";
		String szLogMsg = "";
		String szOperationName = "공장별 재공현황";
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "YdNextprocList2", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	} //end of getYdMgtList2
	
	
	/**
	 *  공장별 재공현황Pop
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData YdNextprocList2Pop(GridData inDto) throws JDTOException {
		 
		String szMethodName = "YdNextprocList2Pop";
		String szLogMsg = "";
		String szOperationName = "동별 재공현황율";
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "YdNextprocList2Pop", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	} //end of getYdMgtList2Pop
	
	
	/**
	 *  결로재재고조회  
	 *  정종균
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	
	public GridData getHotcoilStrLocList(GridData inDto) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName="getHotcoilStrLocList";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getHotcoilStrLocList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getHotcoilStrLocList
	
	
	/**
	 * 차량 작업 관리 화면 :대기장도착처리
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public GridData getStandByYdArrive(GridData inDto) throws DAOException {
		//		LOG
		String szMsg 			= "";
		String szMethodName		= "getStandByYdArrive";
		
		GridData gdRes 			= null;
		EJBConnector ejbConn 	= null;
		String szRtnMsg 		= "";
		JDTORecord outRecord2  	= JDTORecordFactory.getInstance().create();		
		JDTORecord inRecord2  	= JDTORecordFactory.getInstance().create();	
		try{
			JDTORecord [] inRecord =  ydComUtil.genGridToJDTORecordAll(inDto);
			
			szMsg = "[JSP Facade] 차량작업관리 대기장도착처리  전송처리 시작  ==>";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);

			for(int x=0;x<inRecord.length;x++){
				inRecord2 	= JDTORecordFactory.getInstance().create();
				inRecord2.setField("YD_GP"				, inRecord[x].getFieldString("YD_GP"));
				inRecord2.setField("CMBN_CARLD_YN"		, inRecord[x].getFieldString("CMBN_CARLD_YN"));
				inRecord2.setField("WORK_GP"			, inRecord[x].getFieldString("WORK_GP"));
				inRecord2.setField("TEL_NO"				, inRecord[x].getFieldString("TEL_NUMBER"));
				inRecord2.setField("TRANS_ORD_DT"		, inRecord[x].getFieldString("TRANS_ORD_DT"));
				inRecord2.setField("TRANS_ORD_SEQNO"	, inRecord[x].getFieldString("TRANS_ORD_SEQNO"));
				inRecord2.setField("CAR_NO"				, inRecord[x].getFieldString("CAR_NO"));
				inRecord2.setField("CARD_NO"			, inRecord[x].getFieldString("CARD_NO"));
				inRecord2.setField("WAIT_ARR_DDTT"		, inRecord[x].getFieldString("WAIT_ARR_DDTT"));
				inRecord2.setField("WAIT_ARR_GP"		, inRecord[x].getFieldString("WAIT_ARR_GP"));
				inRecord2.setField("DRIVER_NAME"		, inRecord[x].getFieldString("DRIVER_NAME"));
			
				ejbConn = new EJBConnector("default", "RtModRegSeEJB", this);
				ejbConn.trx("procStandByYdArrive", new Class[] { JDTORecord.class }, new Object[] { inRecord2 });
	
			}
			
			szMsg = "[JSP Facade] 차량작업관리 대기장도착처리 전송처리 ===> 끝";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes.setMessage("정상적으로 대기장도착처리 되었습니다.");
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			 
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
			
		}
		return gdRes;
	}  //end of getStandByYdArrive
	
	
	/**
	 * 차량 작업 관리 화면 :대기장도착BACKUP화면(출하호출)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public GridData updYdWaitLocArrBackup(GridData inDto) throws DAOException {
		//		LOG
		String szMsg 			= "";
		String szMethodName		= "updYdWaitLocArrBackup";
		String szYD_USER_ID		= "";
		GridData gdRes 			= null;
		EJBConnector ejbConn 	= null;
		String szRtnMsg 		= "";
		JDTORecord outRecord2  	= JDTORecordFactory.getInstance().create();		
		JDTORecord inRecord2  	= JDTORecordFactory.getInstance().create();		
		try{
			JDTORecord [] inRecord =  ydComUtil.genGridToJDTORecordAll(inDto);
			
			szMsg = "[JSP Facade] 차량작업관리 대기장도착처리(출하호출)  전송처리 시작  ==>";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);

			for(int x=0;x<inRecord.length;x++){
				inRecord2 	= JDTORecordFactory.getInstance().create();
				inRecord2.setField("YD_GP"				, inRecord[x].getFieldString("YD_GP"));
				inRecord2.setField("CMBN_CARLD_YN"		, inRecord[x].getFieldString("CMBN_CARLD_YN"));
				inRecord2.setField("WORK_GP"			, inRecord[x].getFieldString("WORK_GP"));
				inRecord2.setField("TEL_NO"				, inRecord[x].getFieldString("TEL_NO"));
				inRecord2.setField("TRANS_ORD_DT"		, inRecord[x].getFieldString("TRANS_WORD_DATE"));
				inRecord2.setField("TRANS_ORD_SEQNO"	, inRecord[x].getFieldString("TRANS_WORD_SEQNO"));
				inRecord2.setField("CAR_NO"				, inRecord[x].getFieldString("CAR_NO"));
				inRecord2.setField("CARD_NO"			, inRecord[x].getFieldString("CARD_NO"));
				inRecord2.setField("WAIT_ARR_DDTT"		, inRecord[x].getFieldString("WAIT_ARR_DDTT"));
				inRecord2.setField("WAIT_ARR_GP"		, inRecord[x].getFieldString("WAIT_ARR_GP"));
				inRecord2.setField("USER_ID"			, inRecord[x].getFieldString("USER_ID"));
			
				//출하쪽 메소드 호출
				ejbConn = new EJBConnector("inisteelApp", "JNDILdtrnLotFaEJB", this);
				outRecord2 = (JDTORecord)ejbConn.trx("updYdWaitLocArrBackup", new Class[] { JDTORecord.class }, new Object[] { inRecord2 });

				String sRTN_CD	= StringHelper.evl(outRecord2.getFieldString("ERR_YN"), "0");
				String sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("rtnCode"), "");
				if ("Y".equals(sRTN_CD)) {
					gdRes.setMessage(sRTN_MSG);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}	
			}
					
			
			szMsg = "[JSP Facade] 차량작업관리 대기장도착처리(출하호출) 전송처리 ===> 끝";
			ydUtils.putLog(szSessionName, szMethodName , szMsg , YdConstant.DEBUG);
			
			gdRes.setMessage("정상적으로 대기장도착처리(출하호출) 되었습니다.");
			gdRes.setStatus("true");
			gdRes.setMessage("Success");
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
			
		}
		return gdRes;
	}  //end of updYdWaitLocArrBackup
	
	
	/**
	 * 대기장도착등록(운전자용)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData getWaitLocArrBackupList(GridData inDto) throws DAOException {
		//LOG
		String szMsg        = "";
		String szMethodName = "getWaitLocArrBackupList";
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getWaitLocArrBackupList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getWaitLocArrBackupList
	
	
	/**
	 * 재공율 조정작업
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updYdNextprocList2Pop(GridData gdReq) throws DAOException {
		//LOG
		String szMsg="";
		String szMethodName = "updYdNextprocList2Pop";
 		 
		GridData gdRes = null; 
		String sEQP_GP 			= "";
		String sDONG 			= "";
		String sRATE 			= "";
		String sYD_USER_ID 		= ""; 
	 
		String sRTN_CD	= "";
		String sRTN_MSG	= "";
  
		String sQueryId  = "";
		
		try{
			JDTORecord [] inRecordSet = ydComUtil.genGridToJDTORecordAll(gdReq);
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes); 
			
			for(int Loopi = 0; Loopi < inRecordSet.length; Loopi++) { 
				sYD_USER_ID 	= ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "YD_USER_ID");
				sEQP_GP			= ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "EQP_GP");
				sDONG			= ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "DONG");
				sRATE 			= ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "재공율조정");
 
				ymCommonDAO dao = ymCommonDAO.getInstance();
				sQueryId  = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updYdNextprocList2Pop";
				dao.updateData(sQueryId,new Object[]{sRATE,sYD_USER_ID,sDONG,sEQP_GP});
  	
				szMsg = "재공율 조정작업 :"+ sRATE;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
			}	
	 
			gdRes.setMessage("재공율 조정작업처리 등록했습니다");		
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		return gdRes;
	}  //end of updCoilYdRetCrnReg1
	
	
	/**
	 *  전체코일야드현황조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData getYdMgtTotalList(GridData inDto) throws JDTOException {
		 
		String szMethodName = "getYdMgtTotalList";
		String szLogMsg = "";
		String szOperationName = "전체코일야드현황조회";
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getYdMgtTotalList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	} //end of getYdMgtTotalList
	
	
	/**
	 *  야드관리 > 통합슬라브야드 > Monitoring > 슬라브이송지연사유등록 (수정)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws JDTOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.05.31
	 */
	public GridData updSlabTotYdToMoveMgt(GridData inDto) throws JDTOException {
	
		String szMethodName="updSlabTotYdToMoveMgt";
		String szLogMsg = ""; 
		String szOperationName	= "슬라브이송지연사유등록";
		JDTORecord outRecord   	= JDTORecordFactory.getInstance().create();
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			
			szLogMsg = "JSP-FACADE [ " + szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);			
			
			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			outRecord = (JDTORecord) ejbConn.trx("updSlabTotYdToMoveMgt", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			
			String sRTN_CD	= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
			String sRTN_MSG	= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
			if ("0".equals(sRTN_CD)) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
				return gdRes;	
			}	
			gdRes.setMessage("정상적으로 적용 되었습니다.");		
			
			
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		szLogMsg = "JSP-FACADE [ " + szOperationName +"] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	}  //end of updSlabTotYdToMoveMgt
	
	/**
	 * 하차작업등록 (반품,회송,부분하차)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData regCarUdWrk(GridData gdReq) throws DAOException {
		//LOG
		JDTORecord outRecord   	= JDTORecordFactory.getInstance().create();
		String sRTN_CD	="";
		String sRTN_MSG ="";
		String szMethodName = "[CoilGdsJspFaEjbBean.regCarUdWrk]";
		String logId = slabUtils.getLogId();
		String szLogMsg = ""; 
		
		try{
			szLogMsg = "JSP-FACADE [ " + szMethodName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			EJBConnector ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			outRecord 	= (JDTORecord)ejbConn.trx("regCarUdWrk", new Class[] { GridData.class }, new Object[] { gdReq });
			sRTN_CD		= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
			sRTN_MSG	= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
			
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);

			szLogMsg = "JSP-FACADE [ " + szMethodName +"] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			gdRes.setMessage(sRTN_MSG);
			return gdRes;
			
		} catch(DAOException e) {
			throw e;			
		}catch(Exception e){
			throw new DAOException(slabUtils.makeErrorLog(logId, szMethodName, e));		
		}

	} //end of regCarUdWrk	
	

	/**
	 * 차량예정정보 발송
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData regCarUdExplainInfo(GridData gdReq) throws DAOException {
		//LOG
		JDTORecord outRecord   	= JDTORecordFactory.getInstance().create();
		String sRTN_CD	="";
		String sRTN_MSG ="";
		String szMethodName = "[CoilGdsJspFaEjbBean.regCarUdExplainInfo]";
		String logId = slabUtils.getLogId();
		String szLogMsg = ""; 
		
		try{
			szLogMsg = "JSP-FACADE [ " + szMethodName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord [] inRecordSet = ydComUtil.genGridToJDTORecordAll(gdReq);
			
			EJBConnector ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			outRecord   = (JDTORecord)ejbConn.trx("regCarUdExplainInfo", new Class[] { JDTORecord[].class }, new Object[] { inRecordSet });
			sRTN_CD		= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
			sRTN_MSG	= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
			
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);

			szLogMsg = "JSP-FACADE [ " + szMethodName +"] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			gdRes.setMessage(sRTN_MSG);
			return gdRes;
			
		} catch(DAOException e) {
			throw e;			
		}catch(Exception e){
			throw new DAOException(slabUtils.makeErrorLog(logId, szMethodName, e));		
		}

	} //end of regCarUdExplainInfo	
	
	
	/**
	 * 공냉재 입고 등록(권하 등록 시)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */
	public JDTORecord updCoilYdSendAirCl(JDTORecord inRecord) throws DAOException  { 
		//LOG 
		
		String szMsg="";
		String szMethodName = "updCoilYdSendAirCl";
		JDTORecord outRecord2   	= JDTORecordFactory.getInstance().create();		 	
		JDTORecord inRecord1   		= JDTORecordFactory.getInstance().create();	 
		 
		EJBConnector ejbConn = null;  
		String sYD_WBOOK_ID 	= "";
		String sYD_SCH_CD 		= "";
		String sYD_USER_ID 		= ""; 
		String sSTL_NO 				= "";
		String sYD_AIM_BAY_GP 	= ""; 
		String sFirstYD_SCH_CD 	= "";
		String sFirstYD_WBOOK_ID= "";
		String sRTN_CD				= "";
		String sRTN_MSG			= "";
		 
		int intStlSchCnt 	= 0;
		
		try{
  
			sYD_USER_ID 		= ydDaoUtils.paraRecChkNull(inRecord, "YD_USER_ID"); 
			sSTL_NO				= ydDaoUtils.paraRecChkNull(inRecord, "STL_NO");
			sYD_AIM_BAY_GP  	= ydDaoUtils.paraRecChkNull(inRecord, "YD_AIM_BAY_GP");   // 입력동 
			
			//크레인 스케쥴 코드 생성
			sYD_SCH_CD = "J" + sYD_AIM_BAY_GP + "YD03LM";  // 공냉재이적
			
			inRecord1 = JDTORecordFactory.getInstance().create();
			inRecord1.setField("STL_SH"						, "1");  
			inRecord1.setField("STL_NO1"						, sSTL_NO);
			inRecord1.setField("YD_UP_COLL_SEQ1"			, "1");  
			inRecord1.setField("YD_USER_ID"					, sYD_USER_ID);  
			inRecord1.setField("YD_AIM_BAY_GP"			, sYD_AIM_BAY_GP); 
			inRecord1.setField("YD_AIM_YD_GP"				, "J");
			inRecord1.setField("YD_TO_LOC_DCSN_MTD"	, "S");
			inRecord1.setField("TO_YD_STK_BED_NO"		,  "");		
			inRecord1.setField("YD_SCH_CD"					, sYD_SCH_CD);
			
			ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
			outRecord2 = (JDTORecord)ejbConn.trx("WrkBookInsertProc", new Class[] { JDTORecord.class }, new Object[] { inRecord1 });
			sRTN_CD		= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
			sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
			sYD_WBOOK_ID= StringHelper.evl(outRecord2.getFieldString("YD_WBOOK_ID"), "");
			sYD_SCH_CD	= StringHelper.evl(outRecord2.getFieldString("YD_SCH_CD"), "");
			if (!("1".equals(sRTN_CD))) { 
				ydUtils.putLog(szSessionName, szMethodName, sRTN_MSG, YdConstant.ERROR);
				return outRecord2;	
			} else {
				
				intStlSchCnt++;
				if(sFirstYD_WBOOK_ID.equals("")) { 
					
					sFirstYD_WBOOK_ID = sYD_WBOOK_ID;
					sFirstYD_SCH_CD   = sYD_SCH_CD; 
				}	 
				
				szMsg = "정상적으로 작업예약까지 등록했습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	
			} 	
			szMsg = "스케줄 코드 :"+ sFirstYD_SCH_CD;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
	 
			
			if(!sFirstYD_WBOOK_ID.equals("")) {				
 
				JDTORecord[] inRecordarr   	= null;
				inRecordarr = new JDTORecord[1];
				
				inRecordarr[0] = JDTORecordFactory.getInstance().create();
				inRecordarr[0].setField("YD_SCH_CD"		, sFirstYD_SCH_CD); 
				inRecordarr[0].setField("YD_WBOOK_ID"	, sFirstYD_WBOOK_ID); 
				ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
				outRecord2 = (JDTORecord) ejbConn.trx("trxRunSchedule", new Class[] { JDTORecord[].class },	new Object[] { inRecordarr });
	
				sRTN_CD		= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "");
				sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
				ydUtils.putLog(szSessionName, szMethodName, "마지막 RETURN:" + sRTN_MSG, YdConstant.DEBUG);
				if (!("1".equals(sRTN_CD))) {
					ydUtils.putLog(szSessionName, szMethodName, sRTN_MSG, YdConstant.ERROR);
					return outRecord2;	
				} else {
					szMsg = "정상적으로 스케쥴등록까지 등록했습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
			}
			
			return outRecord2;
			 
		} catch(Exception e){
			szMsg = "2열연 공냉재 입고 대상작업 편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			
			throw new DAOException(szMsg);
		}
	}  //end of updCoilYdSendAirCl
	
	/**
	 * 지포장(공냉재) 입고 등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilYdSendGF2(GridData gdReq) throws DAOException {
		//LOG
		JPlateYdCommDAO ydCommDao = new JPlateYdCommDAO();
		
		String szMsg="";
		String szMethodName = "updCoilYdSendGF2";
		JDTORecord outRecord2   	= JDTORecordFactory.getInstance().create();			
		JDTORecord inRecord   		= JDTORecordFactory.getInstance().create();			
		JDTORecord inRecord1   		= JDTORecordFactory.getInstance().create();			
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet rsResult     		= null;
		String sQueryId			= "";
		String sYD_WBOOK_ID 	= "";
		String sYD_SCH_CD 		= "";
		String sYD_USER_ID 		= "";
		String sYD_STK_COL_GP 	= "";
		String sSTL_NO 			= "";
		String sYD_AIM_BAY_GP 	= "";
		String sFirstYD_AIM_BAY_GP = "";
		String sFirstYD_STK_COL_GP = "";
		String sFirstYD_SCH_CD ="";
		String sFirstYD_WBOOK_ID = "";
		String sRTN_CD	= "";
		String sRTN_MSG	= "";
		
		
		int intRtnVal 		= 0;
		int intStlSchCnt 	= 0;
		
		try{
			JDTORecord [] inRecordSet = ydComUtil.genGridToJDTORecordAll(gdReq);
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			
			for(int Loopi = 0; Loopi < inRecordSet.length; Loopi++) {
				
				sYD_USER_ID 	= ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "YD_USER_ID");
				sYD_STK_COL_GP	= ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "YD_STK_COL_GP");
				sSTL_NO			= ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "STL_NO");
				sYD_AIM_BAY_GP  = ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "YD_AIM_BAY_GP");   // 입력동
				sYD_SCH_CD 	= ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "FRM_YD_SCH_CD");  //공냉재 스케쥴 코드
				
				inRecord1 = JDTORecordFactory.getInstance().create();
				inRecord1.setField("STL_SH"						, "1");  
				inRecord1.setField("STL_NO1"					, sSTL_NO);
				inRecord1.setField("YD_UP_COLL_SEQ1"			, "1");  
				inRecord1.setField("YD_USER_ID"					, sYD_USER_ID);  
				inRecord1.setField("YD_AIM_BAY_GP"				, sYD_AIM_BAY_GP); 
				inRecord1.setField("YD_AIM_YD_GP"				, "J");
				inRecord1.setField("YD_TO_LOC_DCSN_MTD",		"S");
				inRecord1.setField("TO_YD_STK_BED_NO",			"");
				
//				if(sYD_STK_COL_GP.substring(1, 2).equals("B")||sYD_STK_COL_GP.substring(1, 2).equals("C")
//					|| sYD_STK_COL_GP.substring(1, 2).equals("E")||sYD_STK_COL_GP.substring(1, 2).equals("H") ){
//					
//					sYD_SCH_CD = "J" + sYD_STK_COL_GP.substring(1, 2) + "GF02LM";  //지포장 SCH_CD
//						
//				} else {
//					gdRes.setMessage("B,C,E,H동 이외의 동은 동간이적후 지포장을 선택해 주시기 바랍니다.");		
//					m_ctx.setRollbackOnly();
//					return gdRes;	
//				}
				
				if("".equals(sYD_SCH_CD)){
					sYD_SCH_CD = "J" + sYD_STK_COL_GP.substring(1, 2) + "GF02LM";  //지포장 SCH_CD
				}
				
				inRecord1.setField("YD_SCH_CD"					, sYD_SCH_CD);
				
				ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
				outRecord2 = (JDTORecord)ejbConn.trx("WrkBookInsertProc", new Class[] { JDTORecord.class }, new Object[] { inRecord1 });
				sRTN_CD		= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
				sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
				sYD_WBOOK_ID= StringHelper.evl(outRecord2.getFieldString("YD_WBOOK_ID"), "");
				sYD_SCH_CD	= StringHelper.evl(outRecord2.getFieldString("YD_SCH_CD"), "");
				if (!("1".equals(sRTN_CD))) {
					gdRes.setMessage(sRTN_MSG);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				} else {
					
					intStlSchCnt++;
					if(sFirstYD_WBOOK_ID.equals("")) { 
						
						sFirstYD_WBOOK_ID = sYD_WBOOK_ID;
						sFirstYD_SCH_CD   = sYD_SCH_CD;
						sFirstYD_AIM_BAY_GP = sYD_STK_COL_GP.substring(1, 2);
						sFirstYD_STK_COL_GP = sYD_STK_COL_GP;
					}	
					gdRes.setMessage(sRTN_MSG + " <br> 정상적으로 작업예약까지 등록했습니다.");		
		
				} 	
				szMsg = "스케줄 코드 :"+ sFirstYD_SCH_CD;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
			}	
			
			
			if(!sFirstYD_WBOOK_ID.equals("")) {				
 
				JDTORecord[] inRecordarr   	= null;
				inRecordarr = new JDTORecord[1];
				
				inRecordarr[0] = JDTORecordFactory.getInstance().create();
				inRecordarr[0].setField("YD_SCH_CD"		, sFirstYD_SCH_CD); 
				inRecordarr[0].setField("YD_WBOOK_ID"	, sFirstYD_WBOOK_ID); 
				ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
				outRecord2 = (JDTORecord) ejbConn.trx("trxRunSchedule", new Class[] { JDTORecord[].class },	new Object[] { inRecordarr });
	
				sRTN_CD		= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "");
				sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
				ydUtils.putLog(szSessionName, szMethodName, "마지막 RETURN:" + sRTN_MSG, YdConstant.DEBUG);
				if (!("1".equals(sRTN_CD))) {
					gdRes.setMessage(sRTN_MSG);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				} else {
					gdRes.setMessage(sRTN_MSG + " <br> 정상적으로 스케쥴까지 등록했습니다.");		
				}
			}
			
			gdRes.setMessage("정상적으로 지포장입고 등록했습니다");		
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		return gdRes;
	}  //end of updCoilYdSendGF2
	
	/**
	 * 지포장재반입조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	
	public GridData getCoilGdsYdReSendGF(GridData inDto) throws DAOException {
					
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet recordSet = null;
		String szMethodName = "getCoilGdsYdReSendGF";
		try{
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			
			ejbConn = new EJBConnector("default", this);

			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilGdsYdReSendGF", inRecord);
			                                                          
			gdRes = CmUtil.genGridData(inDto , recordSet);

		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		return gdRes;
	} //end of getCoilGdsYdReSendGF
	
	
	/**
	 * 지포장재삭제
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	
	public GridData updCoilGdsYdReSendGFDel(GridData gdReq) throws DAOException {
 
		String szMsg="";
		String szMethodName = "updCoilGdsYdReSendGFDel"; 		
		JDTORecord inRecord1   		= JDTORecordFactory.getInstance().create();			
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
 
		String sSTL_NO 			= "";
		String sYD_USER_ID		= "";
		
		try{
			JDTORecord [] inRecordSet = ydComUtil.genGridToJDTORecordAll(gdReq);
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			
			for(int Loopi = 0; Loopi < inRecordSet.length; Loopi++) {
				sYD_USER_ID 	= ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "YD_USER_ID");
				sSTL_NO			= ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "STL_NO");
				 
				
				inRecord1 = JDTORecordFactory.getInstance().create();   
				inRecord1.setField("STL_NO"					, sSTL_NO);
				inRecord1.setField("YD_USER_ID"				, sYD_USER_ID);
				
				ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
				ejbConn.trx("updCoilGdsYdReSendGFDel", new Class[] { JDTORecord.class }, new Object[] { inRecord1 });
 
			}	
			 
			gdRes.setMessage("정상적으로 지포장삭제 등록했습니다");		
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			gdRes.setStatus("true");
			gdRes.setMessage("정상적으로 지포장삭제 등록했습니다");
			return gdRes;
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		 
	}  //end of updCoilGdsYdReSendGFDel
	
	
	
	
	/**
	 * 지포장재반입 등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public GridData updCoilGdsYdReSendGF(GridData gdReq) throws DAOException {
		//LOG
		JPlateYdCommDAO ydCommDao = new JPlateYdCommDAO();
		
		String szMsg="";
		String szMethodName = "updCoilGdsYdReSendGF";
		JDTORecord outRecord2   	= JDTORecordFactory.getInstance().create();			
		JDTORecord inRecord   		= JDTORecordFactory.getInstance().create();			
		JDTORecord inRecord1   		= JDTORecordFactory.getInstance().create();			
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		JDTORecordSet rsResult     		= null;
		String sQueryId			= "";
		String sYD_WBOOK_ID 	= "";
		String sYD_SCH_CD 		= "";
		String sYD_USER_ID 		= "";
		String sYD_STK_COL_GP 	= "";
		String sSTL_NO 			= "";
		String sYD_AIM_BAY_GP 	= "";
		String sFirstYD_AIM_BAY_GP = "";
		String sFirstYD_STK_COL_GP = "";
		String sFirstYD_SCH_CD ="";
		String sFirstYD_WBOOK_ID = "";
		String sRTN_CD	= "";
		String sRTN_MSG	= "";
		
		
		int intRtnVal 		= 0;
		int intStlSchCnt 	= 0;
		
		try{
			JDTORecord [] inRecordSet = ydComUtil.genGridToJDTORecordAll(gdReq);
			gdRes = OperateGridData.cloneResponseGridData(gdReq);
			gdRes = CmUtil.copyGDParam(gdReq, gdRes);
			
			for(int Loopi = 0; Loopi < inRecordSet.length; Loopi++) {
				
				sYD_USER_ID 	= ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "YD_USER_ID");
				sYD_STK_COL_GP	= ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "YD_STK_COL_GP");
				sSTL_NO			= ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "STL_NO");
				sYD_AIM_BAY_GP  = ydDaoUtils.paraRecChkNull(inRecordSet[Loopi], "YD_AIM_BAY_GP");   // 입력동
				
				inRecord1 = JDTORecordFactory.getInstance().create();
				inRecord1.setField("STL_SH"						, "1");  
				inRecord1.setField("STL_NO1"					, sSTL_NO);
				inRecord1.setField("YD_UP_COLL_SEQ1"			, "1");  
				inRecord1.setField("YD_USER_ID"					, sYD_USER_ID);  
				inRecord1.setField("YD_AIM_BAY_GP"				, sYD_AIM_BAY_GP); 
				inRecord1.setField("YD_AIM_YD_GP"				, "H");
				inRecord1.setField("YD_TO_LOC_DCSN_MTD",		"S");
				inRecord1.setField("TO_YD_STK_BED_NO",			"");
				
				if(sYD_STK_COL_GP.substring(1, 2).equals("B")||sYD_STK_COL_GP.substring(1, 2).equals("C")
					|| sYD_STK_COL_GP.substring(1, 2).equals("E")||sYD_STK_COL_GP.substring(1, 2).equals("H") ){
					
					sYD_SCH_CD = "H" + sYD_STK_COL_GP.substring(1, 2) + "GF01LM";  //지포장 SCH_CD
						
				} else {
					gdRes.setMessage("B,C,E,H동 이외의 동은 동간이적후 지포장을 선택해 주시기 바랍니다.");		
					m_ctx.setRollbackOnly();
					return gdRes;	
				}
				inRecord1.setField("YD_SCH_CD"					, sYD_SCH_CD);
				
				ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
				outRecord2 = (JDTORecord)ejbConn.trx("WrkBookInsertProc", new Class[] { JDTORecord.class }, new Object[] { inRecord1 });
				sRTN_CD		= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
				sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
				sYD_WBOOK_ID= StringHelper.evl(outRecord2.getFieldString("YD_WBOOK_ID"), "");
				sYD_SCH_CD	= StringHelper.evl(outRecord2.getFieldString("YD_SCH_CD"), "");
				if (!("1".equals(sRTN_CD))) {
					gdRes.setMessage(sRTN_MSG);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				} else {
					
					intStlSchCnt++;
					if(sFirstYD_WBOOK_ID.equals("")) { 
						
						sFirstYD_WBOOK_ID = sYD_WBOOK_ID;
						sFirstYD_SCH_CD   = sYD_SCH_CD;
						sFirstYD_AIM_BAY_GP = sYD_STK_COL_GP.substring(1, 2);
						sFirstYD_STK_COL_GP = sYD_STK_COL_GP;
					}	
					gdRes.setMessage(sRTN_MSG + " <br> 정상적으로 작업예약까지 등록했습니다.");		
		
				} 	
				szMsg = "스케줄 코드 :"+ sFirstYD_SCH_CD;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
			}	
			
			
			if(!sFirstYD_WBOOK_ID.equals("")) {				
 
				JDTORecord[] inRecordarr   	= null;
				inRecordarr = new JDTORecord[1];
				
				inRecordarr[0] = JDTORecordFactory.getInstance().create();
				inRecordarr[0].setField("YD_SCH_CD"		, sFirstYD_SCH_CD); 
				inRecordarr[0].setField("YD_WBOOK_ID"	, sFirstYD_WBOOK_ID); 
				ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
				outRecord2 = (JDTORecord) ejbConn.trx("trxRunSchedule", new Class[] { JDTORecord[].class },	new Object[] { inRecordarr });
	
				sRTN_CD		= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "");
				sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
				ydUtils.putLog(szSessionName, szMethodName, "마지막 RETURN:" + sRTN_MSG, YdConstant.DEBUG);
				if (!("1".equals(sRTN_CD))) {
					gdRes.setMessage(sRTN_MSG);		
					m_ctx.setRollbackOnly();
					return gdRes;	
				} else {
					gdRes.setMessage(sRTN_MSG + " <br> 정상적으로 스케쥴까지 등록했습니다.");		
				}
			}
			
			gdRes.setMessage("정상적으로 지포장재반입 등록했습니다");		
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		return gdRes;
	}  //end of updCoilGdsYdReSendGF
	
	
	 
	
	/**
	 * 전체입동제한 변경 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData getCoilCarPointYn(GridData gdReq) throws DAOException {
		//LOG
		JDTORecord outRecord   	= JDTORecordFactory.getInstance().create();
		String sRTN_CD	="";
		String sRTN_MSG ="";
		String szMethodName = "[CoilGdsJspFaEjbBean.getCoilCarPointYn]";
		String logId = slabUtils.getLogId();
		String szLogMsg = ""; 
		
		try{
			szLogMsg = "JSP-FACADE [ " + szMethodName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			EJBConnector ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			outRecord 	= (JDTORecord)ejbConn.trx("getCoilCarPointYn", new Class[] { GridData.class }, new Object[] { gdReq });
			sRTN_CD		= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
			sRTN_MSG	= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
			
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);

			szLogMsg = "JSP-FACADE [ " + szMethodName +"] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			gdRes.setMessage(sRTN_MSG);
			return gdRes;
			
		} catch(DAOException e) {
			throw e;			
		}catch(Exception e){
			throw new DAOException(slabUtils.makeErrorLog(logId, szMethodName, e));		
		}

	} //end of getCoilCarPointYn	
	
	
	/**
	 * 제품이송우선순위 변경 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public GridData getCoilCarMovYn(GridData gdReq) throws DAOException {
		//LOG
		JDTORecord outRecord   	= JDTORecordFactory.getInstance().create();
		String sRTN_CD	="";
		String sRTN_MSG ="";
		String szMethodName = "[CoilGdsJspFaEjbBean.getCoilCarMovYn]";
		String logId = slabUtils.getLogId();
		String szLogMsg = ""; 
		
		try{
			szLogMsg = "JSP-FACADE [ " + szMethodName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			EJBConnector ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
			outRecord 	= (JDTORecord)ejbConn.trx("getCoilCarMovYn", new Class[] { GridData.class }, new Object[] { gdReq });
			sRTN_CD		= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
			sRTN_MSG	= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
			
			GridData gdRes = OperateGridData.cloneResponseGridData(gdReq);

			szLogMsg = "JSP-FACADE [ " + szMethodName +"] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			gdRes.setMessage(sRTN_MSG);
			return gdRes;
			
		} catch(DAOException e) {
			throw e;			
		}catch(Exception e){
			throw new DAOException(slabUtils.makeErrorLog(logId, szMethodName, e));		
		}

	} //end of getCoilCarMovYn
	
	/**
	 *  압연일자별 재공현황(PO)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws JDTOException
	 * @throws JDTOException
	 */	
	public GridData YdNextprocPOList(GridData inDto) throws JDTOException {
		 
		String szMethodName = "YdNextprocPOList";
		String szLogMsg = "";
		String szOperationName = "압연일자별 재공현황(PO)";
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "YdNextprocPOList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes;
	} //end of getYdMgtPOList
	
	
	/**
	 *  야드관리 > 코일제품창고 > 설비관리 > 대차스케줄관리  --> 입고대차 지정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws JDTOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.10
	 */
	public GridData updCoilYdTcarStsSetCond(GridData inDto) throws JDTOException {
	 
		String szMethodName="updCoilYdTcarStsSetCond";
		String[] szRtnMsg = null;
		String rtnMsg = YdConstant.RETN_CD_SUCCESS;
		String szLogMsg = "";
		String szOperationName	= "결로엄격재 대차 설정";
		JDTORecord outRecord   	= JDTORecordFactory.getInstance().create();
		
		GridData gdRes = null;
		EJBConnector ejbConn = null;
		try{
			 
			
			JDTORecord [] inRecord =  ydComUtil.genJDTORecordSet(inDto);
			ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);

			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);		
			
			outRecord = (JDTORecord)ejbConn.trx("updCoilYdTcarStsSetCond", new Class[] { JDTORecord[].class }, new Object[] { inRecord });
			String sRTN_CD	= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
			String sRTN_MSG	= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
			if ("0".equals(sRTN_CD)) {
				gdRes.setMessage(sRTN_MSG);		
				m_ctx.setRollbackOnly();
				return gdRes;	
			}	
			gdRes.setMessage("정상적으로 수정되었습니다.");		
			
			
		}catch(Exception e){		
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}		
		gdRes.setStatus("true");
		
		return gdRes;	
	}
	
	
	/**
	 *  야드관리 > 2열연 코일소재야드 > 설비입측관리 > 지포장보급관리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws JDTOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.10
	 */
	public GridData getCoilYdGFList(GridData inDto) throws JDTOException {
	 
		String szMethodName="getCoilYdGFList";
		String[] szRtnMsg = null;
		String rtnMsg = YdConstant.RETN_CD_SUCCESS;
		String szLogMsg = "";
		String szOperationName	= "지포장보급대상LIST";
		JDTORecord outRecord   	= JDTORecordFactory.getInstance().create();
		
		
		GridData      gdRes     = null;
		EJBConnector  ejbConn   = null;
		JDTORecordSet recordSet = null;
		try{
			szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(inDto);
			ejbConn = new EJBConnector("default", this);
			recordSet = (JDTORecordSet) ejbConn.trx("CoilGdsJspSeEJB", "getCoilYdGFList", inRecord);
			gdRes = CmUtil.genGridData(inDto , recordSet);
		}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "다운현상grid로그:"+gdRes, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		gdRes.setStatus("true");
		gdRes.setMessage("Success");
		
		szLogMsg = "[Jsp Facade - "+szOperationName+"] 메소드 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return gdRes; 
		 
	}
}
