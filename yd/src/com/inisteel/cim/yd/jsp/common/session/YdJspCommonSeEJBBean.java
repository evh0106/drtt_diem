/**
 * @(#)YdJspCommonSeEJBBean.java
 * 
 * @version			1.0
 * @author 			현대제철
 * @date			2012/11/14
 * 
 * @description		이클래스는공통코드조회 Session EJB클래스입니다.
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.01  2013/04/03   허철호      허철호      신규설비 추가
 */

package com.inisteel.cim.yd.jsp.common.session; 

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;
import kr.co.gtone.bre.extend.CommCodeMng;
import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.CmCode;
import com.inisteel.cim.common.util.CmUtil;
import com.inisteel.cim.common.util.StringUtils;
import com.inisteel.cim.yd.common.dao.YdPlateCommDAO;
import com.inisteel.cim.yd.common.dao.ptOsCommDao.PtOsCommDao;
import com.inisteel.cim.yd.common.dao.ptStlFrtoMoveDao.PtStlFrtoMoveDao;
import com.inisteel.cim.yd.common.dao.ydCarFtmvMtlDao.YdCarFtmvMtlDao;
import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.dao.ydCarSpecDao.YdCarSpecDao;
import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
import com.inisteel.cim.yd.common.dao.ydCrnWrkMtlDao.YdCrnWrkMtlDao;
import com.inisteel.cim.yd.common.dao.ydEqpDao.YdEqpDao;
import com.inisteel.cim.yd.common.dao.ydLocSrchBedDao.YdLocSrchBedDao;
import com.inisteel.cim.yd.common.dao.ydLocSrchRngDao.YdLocSrchRngDao;
import com.inisteel.cim.yd.common.dao.ydPrepSchDao.YdPrepSchDao;
import com.inisteel.cim.yd.common.dao.ydSchRuleDao.YdSchRuleDao;
import com.inisteel.cim.yd.common.dao.ydStkBedDao.YdStkBedDao;
import com.inisteel.cim.yd.common.dao.ydStkColDao.YdStkColDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.dao.ydStrCharDao.YdStrCharDao;
import com.inisteel.cim.yd.common.dao.ydStrGtrDao.YdStrGtrDao;
import com.inisteel.cim.yd.common.dao.ydTcarFtmvMtlDao.YdTcarFtmvMtlDao;
import com.inisteel.cim.yd.common.dao.ydTcarSchDao.YdTcarSchDao;
import com.inisteel.cim.yd.common.dao.ydWrkHistDao.YdWrkHistDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookDao.YdWrkbookDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookMtlDao.YdWrkbookMtlDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.DaoManager;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.loc.CoilYdToLocDcsnUtil;
import com.inisteel.cim.yd.common.util.loc.YdStkLocVO;
import com.inisteel.cim.yd.common.util.loc.YdToLocDcsnUtil;
import com.inisteel.cim.yd.common.util.plate.PlateGdsYdUtil;
import com.inisteel.cim.yd.jsp.common.CmnUtil;
import com.inisteel.cim.yd.jsp.common.YDDataUtil;
import com.inisteel.cim.yd.jsp.common.Dao.JspCommonDAO;
import com.inisteel.cim.yd.ydStock.StockSpecReg.StockSpecRegSeEJBBean;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;

/**
 * [A] 클래스명 : 공통코드 조회
 * 
 * @ejb.bean name="YdJspCommonSeEJB" jndi-name="YdJspCommonSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class YdJspCommonSeEJBBean extends BaseSessionBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3740695859960102299L;
	
	private static Logger logger  = new Logger("yd");
	private YdUtils ydUtils       = new YdUtils();
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();
	 
	private YDDataUtil  yddatautil = new YDDataUtil();
	private String szSessionName = getClass().getName();
	
	private YdPICommDAO   ydPICommDAO   = new YdPICommDAO();

	
	/**
	 * ejbCreate()
	 * 
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	/**
	 * 코드 조회 메소드(WISEGRID)
	 * @param xlib.cmc.GridData
	 * @return xlib.cmc.GridData
	 * @throws com.inisteel.cim.common.exception.DAOException
	 * @ejb.interface-method
	 */
    public GridData getListCode(GridData gdReq) throws DAOException {
    	GridData returnGrid = null;
    	JDTORecordSet resultData = null;    	
    	JDTORecordSet returnSet = JDTORecordFactory.getInstance().createRecordSet("CODE");
    	
    	
    	String szOperationName = "코드 조회 메소드(WISEGRID)";
		try {
			//BRE 사용하게 수정함.
			JDTORecord [] dtos = CommCodeMng.CMCode(CmnUtil.nvl(gdReq.getParam("v_CD_EN_ID"), ""), CmnUtil.nvl(gdReq.getParam("v_CD_CAT_ID"), "HS0000"));
			JDTORecord dataSet = null;
			
			for(int ii=0;ii<dtos.length;ii++) {
				
				if(dtos[ii].getFieldString("CD_VAL").equals("CM")) continue;
				
				dataSet = JDTORecordFactory.getInstance().create();
				dataSet.addField("CD_VAL", dtos[ii].getFieldString("CD_VAL"));
				dataSet.addField("CD_MNNG", dtos[ii].getFieldString("CD_MNNG"));
				returnSet.addRecord(dataSet);
				ydUtils.displayRecord(szOperationName, dataSet);
			}
			
			returnSet.first();
			returnGrid = OperateGridData.cloneResponseGridData(gdReq);
			
			//args[] - 1 : 리턴할 GridData, 2 : 디비 결과 List, 3 : JSP에서 받은 GridData
			//3번째 아규먼트가 있었을 경우 JSP에서 받은 파라미터를 리턴할 GridData에 그대로 세팅한다.			
			return CmnUtil.jdtoRecordToGridData(returnGrid, resultData, gdReq);
			
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {			
		}
    }
	/**
	 * 출하차량상차LOT조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getDmCarLiftLotList(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		YdUtils ydUtils = new YdUtils();
		
		String szMsg        = "";		
		String szMethodName = "getDmCarLiftLotList";
		String szOperationName = "출하차량상차LOT조회";
		
		String szCARD_NO = null;
		String szCAR_NO = null;
		String szTRANS_ORD_DATE = null;
		String szTRANS_ORD_SEQNO = null;
		String szTRANS_ORD_DATE_SEQNO = null;
		String szYD_STK_COL_GP = null;
		String szYD_GP = null;
		String szYD_BAY_GP = null;
		String szDEMANDER_CD =null;
		YdStockDao ydStockDao = new YdStockDao();
		
		int intRtnVal = 0;
		
		try {		
			
			szMsg = "JSP-SESSION [출하차량상차LOT조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			szCARD_NO = StringHelper.evl(inDto.getFieldString("CARD_NO"), "");
			szCAR_NO = StringHelper.evl(inDto.getFieldString("CAR_NO"), "");
			szTRANS_ORD_DATE_SEQNO = StringHelper.evl(inDto.getFieldString("TRANS_ORD_DATE_SEQNO"), "");
			if(!szTRANS_ORD_DATE_SEQNO.equals("")){
				String[] arrTemp = szTRANS_ORD_DATE_SEQNO.split("/");
				szTRANS_ORD_DATE = arrTemp[0];
				szTRANS_ORD_SEQNO = arrTemp[1];
			}
			inDto.setField("TRANS_ORD_DATE", szTRANS_ORD_DATE);
			inDto.setField("TRANS_ORD_SEQNO", szTRANS_ORD_SEQNO);
			//szTRANS_ORD_DATE = StringHelper.evl(inDto.getFieldString("TRANS_ORD_DATE"), "");
			//szTRANS_ORD_SEQNO = StringHelper.evl(inDto.getFieldString("TRANS_ORD_SEQNO"), "");
			szYD_GP = StringHelper.evl(inDto.getFieldString("YD_GP"), "");
			szYD_BAY_GP = StringHelper.evl(inDto.getFieldString("YD_BAY_GP"), "_");
			szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP;
			inDto.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			
			szDEMANDER_CD =StringHelper.evl(inDto.getFieldString("DEMANDER_CD"), "");
			inDto.setField("DEMANDER_CD", szDEMANDER_CD);
			
			szMsg = "[JSP Session] " + szOperationName + " 파라미터 확인 : 카드번호[" + szCARD_NO + "], 차량번호[" + szCAR_NO + "], 운송지시일자[" + szTRANS_ORD_DATE + "], 운송지시순번[" + szTRANS_ORD_SEQNO + "], 야드구분[" + szYD_GP + "], 동구분[" + szYD_BAY_GP + "]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			if( szYD_GP.equals(YdConstant.YD_GP_PLATE_GDS_YARD)) {					//후판제품창고
				
				//적치열 Asc, 적치베드 Asc, 적치단 Desc
//PIDEV_S :병행가동용:PI_YD
				inDto.setField("PI_YD",    	szYD_GP);						
				intRtnVal = ydStockDao.getYdStock(inDto, outRecSet, 128);
			}else if( szYD_GP.equals(YdConstant.YD_GP_INTGR_YARD)) {					//통합슬라브
				//적치열 Asc, 적치베드 Asc, 적치단 Desc
//PIDEV_S :병행가동용:PI_YD
				inDto.setField("PI_YD",    	szYD_GP);				
				intRtnVal = ydStockDao.getYdStock(inDto, outRecSet, 619);
			}else{
//PIDEV_S :병행가동용:PI_YD
				inDto.setField("PI_YD",    	szYD_GP);					
				//적치열 Desc, 적치베드 Asc, 적치단 Desc
				intRtnVal = ydStockDao.getYdStock(inDto, outRecSet, 126);
			}
			
			if (intRtnVal <= 0) {
				if (intRtnVal == -1) {
					szMsg = "[JSP Session] " + szOperationName + " 오류발생 1 : 반환값 - " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}else if (intRtnVal == 0) {
					szMsg = "[JSP Session] " + szOperationName + " 대상재가 존재하지 않음 : 반환값 - " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				} else {
					szMsg = "[JSP Session] " + szOperationName + " 오류발생 2 : 반환값 - " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if
			
			szMsg = "[JSP Session] " + szOperationName + " 성공 : 레코드 수 - " + intRtnVal;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szMsg = "JSP-SESSION [출하차량상차LOT조회] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getDmCarLiftLotList

    
	/**
	 *  공통 코드 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getComboCodeList(JDTORecord inDto) throws DAOException {
		
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		try {
			recPara.setField("CODE",     yddatautil.setDataDefault(inDto.getField("CODE"), ""));
			recPara.setField("CAT",      yddatautil.setDataDefault(inDto.getField("CAT"), ""));

			String code = recPara.getFieldString("CODE");
			String cat  = recPara.getFieldString("CAT");
			
			CmCode ccCmCode = new CmCode(code, cat);
			
			String sCode = ccCmCode.getCodes().trim();
			String sName = ccCmCode.getNames().trim();
			if ("".equals(sCode)) {
				return outRecSet;
			}
			
			String sArrayCode[] = sCode.split(";");
			String sArrayName[] = sName.split(";");
			
			for (int jj = 0; jj < sArrayCode.length; jj++) {
				JDTORecord jRecInDto = JDTORecordFactory.getInstance().create(); 
				jRecInDto.setField("CODE", sArrayCode[jj]) ; 
				jRecInDto.setField("NAME", sArrayName[jj]) ;
				outRecSet.addRecord(jRecInDto) ; 
			}		
			
			logger.println(LogLevel.DEBUG_TEXT, "getComboCodeList");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getComboCodeList
	
	
	/**
	 *  야드크레인 작업관리 POP_UP (권상실적 처리) -YDYDJ600
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return String
	 * @throws DAOException
	 */
	public String updCrnUpPrsBackUp(JDTORecord[] inDto) throws DAOException {
		String szLogMsg = null;
		String szRtnMsg = null;
		JDTORecord recPara = null;
		JDTORecordSet rsResult = null;
		//YdDelegate ydDelegate = new YdDelegate();
		String szYdGp = null;
		String szJMS_TC_CD = null;
		String szEjbMethod = null;
		EJBConnector ejbConn = null;
		String szMethodName = "updCrnUpPrsBackUp";
		String szYD_EQP_ID = null;
		int intRtnVal = YdConstant.RETN_INT_FAILURE.intValue();
		String szYD_EQP_STAT = null;
		try{
			//for(int x=0;x<inDto.length;x++){
				//TC CODE
				
				
			szLogMsg = "JSP-SESSION [야드크레인 작업관리 POP_UP (권상실적 처리) ]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
				 
				szYdGp = inDto[0].getFieldString("YD_GP");
				szYD_EQP_ID = inDto[0].getFieldString("YD_EQP_ID");
				
				/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				* 크레인의 야드설비상태 - YD_EQP_STAT
				* W : 스케줄수행대기, 1 : 권상지시, 2 : 권상완료, 3 : 권하지시, 4 : 권하완료
				+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
				//크레인의 야드설비상태를 먼저 확인
				rsResult = JDTORecordFactory.getInstance().createRecordSet("retTmp");
				intRtnVal = YdCommonUtils.getYdEqp(szYD_EQP_ID, rsResult);
				if( intRtnVal == 0 ) {
					szLogMsg = "[JSP Session]야드크레인 작업관리 권상실적 처리 - 크레인설비[" + szYD_EQP_ID + "]가 존재하지 않습니다!";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;
				}else if( intRtnVal < 0 ) {
					szLogMsg = "[JSP Session]야드크레인 작업관리 권상실적 처리 - 크레인설비[" + szYD_EQP_ID + "]조회시 오류 발생";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NOTEXIST;
				}
				
				rsResult.first();
				recPara = rsResult.getRecord();
				szYD_EQP_STAT = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_STAT");
				if( !szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_UP_WO) && !szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_IDLE) ) {
					//W : 스케줄수행대기, 1 : 권상지시 상태가 아닌 경우에는 에러메시지 반환
					szLogMsg = "[JSP Session]야드크레인 작업관리 권상실적 처리 - 크레인의 야드설비상태[" + szYD_EQP_STAT + "]가 권상실적처리 가능한 상태[W : 스케줄수행대기, 1 : 권상지시]여야합니다!";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					return YdConstant.RETN_CRN_STATUS_ERR;
				}
				
				/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
				
				recPara = JDTORecordFactory.getInstance().create();
				
				szLogMsg = "[JSP Session]야드크레인 작업관리 권상실적 처리  - 야드구분[" + szYdGp + "]";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				if (YdConstant.YD_GP_C_SLAB_YARD.equals(szYdGp)){							//C연주 슬라브 야드 [A]
					szJMS_TC_CD = "YDYDJ600";
					szEjbMethod = "procY1CrnLdWr";
				} else if(YdConstant.YD_GP_PORT_SLAB_YARD.equals(szYdGp)){				//항만슬라브야드 기능추가 - 2015.12.30 LeeJY
					szJMS_TC_CD = "YDYDJ600";
					szEjbMethod = "procY1CrnLdWr";
				} else if(YdConstant.YD_GP_A_PLATE_SLAB_YARD.equals(szYdGp)){				//A후판 슬라브야드[D]
					//recPara.setField("JMS_TC_CD","YDYDJ602");
					szJMS_TC_CD = "YDYDJ602";
					szEjbMethod = "procY3CrnLdWr";
				} else if(YdConstant.YD_GP_PLATE_GDS_YARD.equals(szYdGp)){					//후판 제품 야드[K]
					//recPara.setField("JMS_TC_CD","YDYDJ604");
					szJMS_TC_CD = "YDYDJ604";
					szEjbMethod = "procY4CrnLdWr";
				} else if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYdGp)){					//2후판 제품 야드[T] - 2013.01.04 추가 (3기)
					szJMS_TC_CD = "YDYDJ604";
					szEjbMethod = "procY4CrnLdWr";
				} else if(YdConstant.YD_GP_C_HR_COIL_GDS_YARD.equals(szYdGp)){				//코일 제품 야드[J]
					szJMS_TC_CD = "YDYDJ606";
					szEjbMethod = "procY5CrnLdWr";
					//recPara.setField("JMS_TC_CD","YDYDJ606");
				}  else if(YdConstant.YD_GP_C_HR_COIL_MATL_YARD.equals(szYdGp)){			//코일 소재 야드[H]
					szJMS_TC_CD = "YDYDJ606";
					szEjbMethod = "procY5CrnLdWr";
					//recPara.setField("JMS_TC_CD","YDYDJ606");
				}  else if(YdConstant.YD_GP_INTGR_YARD.equals(szYdGp)){						//통합  야드[S]
					szJMS_TC_CD = "YDYDJ608";
					szEjbMethod = "procY0CrnLdWr";
					//recPara.setField("JMS_TC_CD","YDYDJ600");
				}
				
				
				recPara.setField("JMS_TC_CD", szJMS_TC_CD);
				recPara.setField("YD_EQP_ID",         szYD_EQP_ID);
				
				//BACK_UP 용도
				//recPara.setField("YD_EQP_WRK_MODE",   inDto[0].getFieldString("YD_EQP_WRK_MODE"));
				recPara.setField("YD_EQP_WRK_MODE",  "9");
				recPara.setField("YD_WRK_PROG_STAT", "2");
				recPara.setField("YD_CRN_SCH_ID",     inDto[0].getFieldString("YD_CRN_SCH_ID"));
				recPara.setField("YD_SCH_CD",         inDto[0].getFieldString("YD_SCH_CD"));

				//권상지시정보를 권상실적정보에넣어준다.
				recPara.setField("YD_UP_WR_LOC",      inDto[0].getFieldString("YD_UP_WO_LOC"));  
				recPara.setField("YD_UP_WR_LAYER",    inDto[0].getFieldString("YD_UP_WO_LAYER"));
				//JMS Call
				//ydDelegate.sendMsg(recPara);
		
//sjhkim
				if( szEjbMethod.equals("procY5CrnLdWr") ) {
					ejbConn = new EJBConnector("default", "CoilCraneLdHdSeEJB", this);		
				} else{
					ejbConn = new EJBConnector("default", "CraneLdHdSeEJB", this);		
				}
				
				//EJB Method Call
//				ejbConn = new EJBConnector("default", "CraneLdHdSeEJB", this);			
				szRtnMsg = (String)ejbConn.trx(szEjbMethod, new Class[] { JDTORecord.class }, new Object[] { recPara });
				
				if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {													//성공
					szLogMsg = "[JSP Session]야드크레인 작업관리 권상실적 처리 성공";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}else if( szRtnMsg.equals(YdConstant.RETN_CD_NOTEXIST) ) {											//데이터가 존재하지 않음
					szLogMsg = "[JSP Session]야드크레인 작업관리 권상실적 처리 오류 - 데이터가 존재하지 않음";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}else if( szRtnMsg.equals(YdConstant.RETN_CD_TC_ERROR) ) {											//전문 에러
					szLogMsg = "[JSP Session]야드크레인 작업관리 권상실적 처리 오류 - 전문 에러";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}else if( szRtnMsg.equals(YdConstant.RETN_CD_DUPLICATE) ) {											//데이타 중복
					szLogMsg = "[JSP Session]야드크레인 작업관리 권상실적 처리 오류 - 데이타 중복";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}else if( szRtnMsg.equals(YdConstant.RETN_CD_NO_PARAM) ) {											//파라미터 오류
					szLogMsg = "[JSP Session]야드크레인 작업관리 권상실적 처리 오류 - 파라미터 오류";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}else if( szRtnMsg.equals(YdConstant.RETN_CD_FAILURE) ) {											//실패
					szLogMsg = "[JSP Session]야드크레인 작업관리 권상실적 처리 실패";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				}
			//}
		}catch(Exception e){
			e.printStackTrace();
			szLogMsg = "[JSP Session]야드크레인 작업관리 권상실적 처리 에러발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
		szLogMsg = "JSP-SESSION [야드크레인 작업관리 POP_UP (권상실적 처리) ]끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
		
		return szRtnMsg;
	}
	
	/**
	 *  야드크레인 작업관리 POP_UP (권하실적 처리)-YDYDJ601
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return String
	 * @throws DAOException
	 */
	
	
	public String updCrnDnPrsBackUp(JDTORecord[] inDto) throws DAOException {
		String szLogMsg = null;
		String szRtnMsg = null;
		JDTORecord recPara = null;		
		JDTORecordSet rsResult = null;
		//YdDelegate ydDelegate = new YdDelegate();
		String szYdGp = null;
		String szJMS_TC_CD = null;
		String szEjbMethod = null;
		String szEjbClassNm = "CraneUdHdSeEJB";
		EJBConnector ejbConn = null;
		String szMethodName = "updCrnUpPrsBackUp";
		String szOperationName = "POP_UP (권하실적 처리)";
		String szYD_EQP_ID = null;
		int intRtnVal = YdConstant.RETN_INT_FAILURE.intValue();
		String szYD_EQP_STAT = null;
		//EJB CALL or JMS CALL 판단변수정의 - 하위모듈에서 사용됨
		String szIS_EJB_CALL = null;
		try{
			//for(int x=0;x<inDto.length;x++){
				//TC CODE
				
			szLogMsg = "JSP-SESSION [야드크레인 작업관리 POP_UP (권하실적 처리) ]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
				szYdGp   = inDto[0].getFieldString("YD_GP");
				szYD_EQP_ID = inDto[0].getFieldString("YD_EQP_ID");
				
				/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				* 크레인의 야드설비상태 - YD_EQP_STAT
				* W : 스케줄수행대기, 1 : 권상지시, 2 : 권상완료, 3 : 권하지시, 4 : 권하완료
				+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
				//크레인의 야드설비상태를 먼저 확인
				rsResult = JDTORecordFactory.getInstance().createRecordSet("retTmp");
				intRtnVal = YdCommonUtils.getYdEqp(szYD_EQP_ID, rsResult);
				if( intRtnVal == 0 ) {
					szLogMsg = "[JSP Session]야드크레인 작업관리 권하실적 처리 - 크레인설비[" + szYD_EQP_ID + "]가 존재하지 않습니다!";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;
				}else if( intRtnVal < 0 ) {
					szLogMsg = "[JSP Session]야드크레인 작업관리 권하실적 처리 - 크레인설비[" + szYD_EQP_ID + "]조회시 오류 발생";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NOTEXIST;
				}
				
				rsResult.first();
				recPara = rsResult.getRecord();
				szYD_EQP_STAT = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_STAT");
				if( !szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_UP_CMPL) && !szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_DN_WO) ) {
					//3 : 권하지시 상태가 아닌 경우에는 에러메시지 반환
					szLogMsg = "[JSP Session]야드크레인 작업관리 권하실적 처리 - 크레인의 야드설비상태[" + szYD_EQP_STAT + "]가 권하실적처리 가능한 상태[2 : 권상완료, 3 : 권하지시]여야합니다!";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					//return YdConstant.RETN_CRN_STATUS_ERR;
				}
				
				/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
				
				recPara  = JDTORecordFactory.getInstance().create();
				
				szLogMsg = "[JSP Session]야드크레인 작업관리 권하실적 처리  - 야드구분[" + szYdGp + "]";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				if (YdConstant.YD_GP_C_SLAB_YARD.equals(szYdGp)){							//C연주 슬라브 야드 [A]
					//recPara.setField("JMS_TC_CD","YDYDJ601");
					szJMS_TC_CD = "YDYDJ601";
					szEjbMethod = "procY1CrnUdWr";
				} else if(YdConstant.YD_GP_PORT_SLAB_YARD.equals(szYdGp)){				//항만슬라브야드 기능추가 - 2015.12.30 LeeJY
					//recPara.setField("JMS_TC_CD","YDYDJ601");
					szJMS_TC_CD = "YDYDJ601";
					szEjbMethod = "procY1CrnUdWr";
				} else if(YdConstant.YD_GP_A_PLATE_SLAB_YARD.equals(szYdGp)){				//A후판 슬라브야드[D]
					//recPara.setField("JMS_TC_CD","YDYDJ603");
					szJMS_TC_CD = "YDYDJ603";
					szEjbMethod = "procY3CrnUdWr";
				} else if(YdConstant.YD_GP_PLATE_GDS_YARD.equals(szYdGp)){					//후판 제품 야드[K]
					//recPara.setField("JMS_TC_CD","YDYDJ605");
					szJMS_TC_CD = "YDYDJ605";
					szEjbMethod = "procY4CrnUdWr";
				} else if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYdGp)){					//2후판 제품 야드[T] - 2013.01.04 추가 (3기)
					szJMS_TC_CD = "YDYDJ605";
					szEjbMethod = "procY4CrnUdWr";
				} else if(YdConstant.YD_GP_C_HR_COIL_GDS_YARD.equals(szYdGp)){				//코일 제품 야드[J]
					//recPara.setField("JMS_TC_CD","YDYDJ607");
					szJMS_TC_CD = "YDYDJ607";
					szEjbMethod = "procY5CrnUdWr";
					szEjbClassNm ="CoilCraneUdHdSeEJB";
				}  else if(YdConstant.YD_GP_C_HR_COIL_MATL_YARD.equals(szYdGp)){			//코일 소재 야드[H]
					//recPara.setField("JMS_TC_CD","YDYDJ607");
					szJMS_TC_CD = "YDYDJ607";
					szEjbMethod = "procY5CrnUdWr";
					szEjbClassNm ="CoilCraneUdHdSeEJB";
				}  else if(YdConstant.YD_GP_INTGR_YARD.equals(szYdGp)){						//통합  야드[S]
					//recPara.setField("JMS_TC_CD","YDYDJ609");
					szJMS_TC_CD = "YDYDJ609";
					szEjbMethod = "procY0CrnUdWr";
				}
				recPara.setField("JMS_TC_CD", szJMS_TC_CD);
				recPara.setField("YD_EQP_ID",         szYD_EQP_ID);
				//BAK_UP ,용도로 9로 세팅하여 보냄 (2009.10.05)
				//recPara.setField("YD_EQP_WRK_MODE",   inDto[0].getFieldString("YD_EQP_WRK_MODE"));
				recPara.setField("YD_EQP_WRK_MODE",   "9");
				
				recPara.setField("YD_WRK_PROG_STAT",  "4");
				recPara.setField("YD_SCH_CD",         inDto[0].getFieldString("YD_SCH_CD"));
				recPara.setField("YD_CRN_SCH_ID",     inDto[0].getFieldString("YD_CRN_SCH_ID"));
				//권하 실적정보 를 권하 실적정보에넣어준다.
				recPara.setField("YD_DN_WR_LOC",      inDto[0].getFieldString("YD_DN_WR_LOC"));  
				recPara.setField("YD_DN_WR_LAYER",    inDto[0].getFieldString("YD_DN_WO_LAYER"));
				
				//EJB CALL or JMS CALL
				szIS_EJB_CALL = "Y";
				recPara.setField("IS_EJB_CALL",    szIS_EJB_CALL);
				
				ydUtils.displayRecord(szOperationName, recPara);
				//JMS Call
				//ydDelegate.sendMsg(recPara);	
				
				//EJB Method Call
				
				ejbConn = new EJBConnector("default", szEjbClassNm, this);			
				szRtnMsg = (String)ejbConn.trx(szEjbMethod, new Class[] { JDTORecord.class }, new Object[] { recPara });
				
				if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {													//성공
					szLogMsg = "[JSP Session]야드크레인 작업관리 권하실적 처리 성공";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}else if( szRtnMsg.equals(YdConstant.RETN_CD_NOTEXIST) ) {											//데이터가 존재하지 않음
					szLogMsg = "[JSP Session]야드크레인 작업관리 권하실적 처리 오류 - 데이터가 존재하지 않음";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}else if( szRtnMsg.equals(YdConstant.RETN_CD_TC_ERROR) ) {											//전문 에러
					szLogMsg = "[JSP Session]야드크레인 작업관리 권하실적 처리 오류 - 전문 에러";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}else if( szRtnMsg.equals(YdConstant.RETN_CD_DUPLICATE) ) {											//데이타 중복
					szLogMsg = "[JSP Session]야드크레인 작업관리 권하실적 처리 오류 - 데이타 중복";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}else if( szRtnMsg.equals(YdConstant.RETN_CD_NO_PARAM) ) {											//파라미터 오류
					szLogMsg = "[JSP Session]야드크레인 작업관리 권하실적 처리 오류 - 파라미터 오류";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}else if( szRtnMsg.equals(YdConstant.RETN_CD_FAILURE) ) {											//실패
					szLogMsg = "[JSP Session]야드크레인 작업관리 권하실적 처리 실패";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				}
			//}
		}catch(Exception e){
			szLogMsg = "[JSP Session]야드크레인 작업관리 권하실적 처리 에러발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
		szLogMsg = "JSP-SESSION [야드크레인 작업관리 POP_UP (권하실적 처리) ]끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
		
		return szRtnMsg;
	}
	
	/**
	 *  크레인 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCrnList(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		
		String szMsg        = "";		
		String szMethodName = "getCrnList";
		
		YdEqpDao ydEqpDao = new YdEqpDao();

		int intRtnVal = 0;
		
		try {
			
			szMsg = "JSP-SESSION [크레인 조회]시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
			
			String ydEqpId = yddatautil.setDataDefault(inDto.getField("YD_EQP_ID"), "");
			
			recPara.addField("YD_GP",      ydEqpId.substring(0, 1));
			recPara.addField("YD_BAY_GP",  ydEqpId.substring(1, 2));
			recPara.addField("YD_EQP_GP",  ydEqpId.substring(2, 4));
		
			intRtnVal = ydEqpDao.getYdEqp(recPara, outRecSet, 1);
		
			
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
		
		
		szMsg = "JSP-SESSION [크레인 조회]끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
		
		return outRecSet;
	}//end of getComboCodeList
	
	
	/**
	 *  크레인 스케줄 목록 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCrnSchList(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		
		String szMsg        = "";		
		String szMethodName = "getCrnSchList";
	
		YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
		int intRtnVal = 0;
		
		try {
			
			szMsg = "JSP-SESSION [크레인 스케줄 목록 조회]시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
			
			recPara.addField("YD_GP",      yddatautil.setDataDefault(inDto.getField("YD_GP"), ""));
			recPara.addField("YD_BAY_GP",  yddatautil.setDataDefault(inDto.getField("YD_BAY_GP"), ""));
		
			intRtnVal      =  ydCrnSchDao.getYdCrnsch(recPara, outRecSet, 14);
			
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
		
		szMsg = "JSP-SESSION [크레인 스케줄 목록 조회]끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return outRecSet;
	}//end of getComboCodeList
	
	
	/**
	 *  설비 목록 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getEqpList(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara    = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		
		String szMsg        = "";		
		String szMethodName = "getEqpList";
	
		YdEqpDao  ydEqpDao  = new YdEqpDao ();
		int intRtnVal = 0;
		
		try {
			
			szMsg = "JSP-SESSION [ 설비 목록 조회]시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
			
			recPara.setField("YD_GP",      yddatautil.setDataDefault(inDto.getField("YD_GP"), ""));
			recPara.setField("YD_BAY_GP",  yddatautil.setDataDefault(inDto.getField("YD_BAY_GP"), ""));
			recPara.setField("YD_EQP_GP",  yddatautil.setDataDefault(inDto.getField("YD_EQP_GP"), ""));
		
			intRtnVal      =  ydEqpDao.getYdEqp(recPara, outRecSet, 2);
			
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
		
		szMsg = "JSP-SESSION [ 설비 목록 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return outRecSet;
	}//end of getComboCodeList
	
	/**
	 *  스케줄 목록 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getSchRuleList(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara    = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		
		String szMsg        = "";		
		String szMethodName = "getSchRuleList";
	
	
		int intRtnVal = 0;
		
		try {
			
			szMsg = "JSP-SESSION [ 스케줄 목록 조회]시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
			
			recPara.setField("YD_GP",      yddatautil.setDataDefault(inDto.getField("YD_GP"), ""));
			recPara.setField("YD_BAY_GP",  yddatautil.setDataDefault(inDto.getField("YD_BAY_GP"), ""));
			
			YdSchRuleDao YdSchRuleDao = new YdSchRuleDao();
			
			intRtnVal      =  YdSchRuleDao.getYdSchrule(recPara, outRecSet, 4);
			
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
		
		szMsg = "JSP-SESSION [ 스케줄 목록 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return outRecSet;
	}//end of getSchRuleList
	
	/**
	 *  스케줄 목록 조회 (화면:스케줄기준관리)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getSchRuleList_New(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara    = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		
		String szMsg        = "";		
		String szMethodName = "getSchRuleList_New";
	
	
		int intRtnVal = 0;
		
		try {
			
			szMsg = "JSP-SESSION [스케줄 목록 조회 - 화면:스케줄기준관리]시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
			
			recPara.setField("YD_GP",      yddatautil.setDataDefault(inDto.getField("YD_GP"), ""));
			recPara.setField("YD_BAY_GP",  yddatautil.setDataDefault(inDto.getField("YD_BAY_GP"), ""));
			
			YdSchRuleDao YdSchRuleDao = new YdSchRuleDao();
			
			intRtnVal      =  YdSchRuleDao.getYdSchrule(recPara, outRecSet, 302);
			
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
		
		szMsg = "JSP-SESSION [스케줄 목록 조회 - 화면:스케줄기준관리] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return outRecSet;
	}//end of getSchRuleList
	
	/**
	 *  스케줄 목록 조회 - 크레인별
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getSchRuleList_Crane(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara    = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		
		String szMsg        = "";		
		String szMethodName = "getSchRuleList_Crane";
	
	
		int intRtnVal = 0;
		
		try {
			
			szMsg = "JSP-SESSION [ 스케줄 목록 조회 - 크레인별]시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
			
			recPara.setField("YD_GP",      yddatautil.setDataDefault(inDto.getField("YD_GP"), ""));
			recPara.setField("YD_BAY_GP",  yddatautil.setDataDefault(inDto.getField("YD_BAY_GP"), ""));
			
			//YdSchRuleDao YdSchRuleDao = new YdSchRuleDao();
			YdEqpDao ydEqp            = new YdEqpDao();
			
			intRtnVal      =  ydEqp.getYdEqp(recPara, outRecSet, 14);
			
			
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
		
		szMsg = "JSP-SESSION [ 스케줄 목록 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return outRecSet;
	}//end of getSchRuleList_Crane
	
	
	/**
	 *  크레인 작업재료 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCrnWrkMtlRef(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara    = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		int intGp = 0;
		
		String szYdGp  ="";
		String szMethodName="getCrnWrkMtlRef";		
		String szLogMsg = "";
		
		
		YdCrnWrkMtlDao ydCrnWrkMtlDao = new YdCrnWrkMtlDao();
		try {
			szLogMsg = "JSP-SESSION [크레인 작업재료 조회]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	

			
			szYdGp = ydDaoUtils.paraRecChkNull(inDto, "YD_GP");
			recPara.setField("YD_CRN_SCH_ID",      yddatautil.setDataDefault(inDto.getField("YD_CRN_SCH_ID"), ""));
			
			
			if(szYdGp.equals(YdConstant.YD_GP_A_PLATE_SLAB_YARD)){
				intGp =	ydCrnWrkMtlDao.getYdCrnwrkmtl(recPara, outRecSet, 11);
				
			} else if(szYdGp.equals(YdConstant.YD_GP_C_SLAB_YARD) 
					|| szYdGp.equals(YdConstant.YD_GP_PORT_SLAB_YARD)    //항만슬라브야드 기능추가 - 2015.12.30 LeeJY
					|| szYdGp.equals(YdConstant.YD_GP_PLATE_GDS_YARD) 
					|| szYdGp.equals(YdConstant.YD_GP_PLATE2_GDS_YARD) ){ //--2013.01.22 수정 (3기)
//PIDEV_S :병행가동용:PI_YD
				recPara.setField("PI_YD",    	szYdGp);					
				intGp =	ydCrnWrkMtlDao.getYdCrnwrkmtl(recPara, outRecSet, 14);
				
			}else{
				intGp =	ydCrnWrkMtlDao.getYdCrnwrkmtl(recPara, outRecSet, 1);
			}
			
			
			//Error Process
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		szLogMsg = "JSP-SESSION [크레인 작업재료 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
		
		return outRecSet;
	}//end of getCrnWrkMtlRef
	
	
	
	
	/**
	 *  후판정정야드 Remark조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getpPlateRemarkDtl(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara    = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		int intGp = 0;
		
		String szYdGp  ="";
		String szMethodName="getpPlateRemarkDtl";		
		String szLogMsg = "";
			
		YdCrnWrkMtlDao ydCrnWrkMtlDao = new YdCrnWrkMtlDao();
		
		try {
			szLogMsg = "JSP-SESSION [후판정정야드 Remark조회]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	

		
			recPara.setField("YD_EQP_GP",      yddatautil.setDataDefault(inDto.getField("YD_EQP_GP"), ""));
			

		    intGp =	ydCrnWrkMtlDao.getYdCrnwrkmtl(recPara, outRecSet, 22);
			

			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		szLogMsg = "JSP-SESSION [후판정정야드 Remark조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
		
		return outRecSet;
	}//end of getCrnWrkMtlRef
	
	
	
	/**
	 *  후판정정야드  Bookout기준조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getpPlateBookoutDtl(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara    = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		int intGp = 0;
		
		String szYdGp  ="";
		String szMethodName="getpPlateBookoutDtl";		
		String szLogMsg = "";
			
		YdCrnWrkMtlDao ydCrnWrkMtlDao = new YdCrnWrkMtlDao();
		
		try {
			szLogMsg = "JSP-SESSION [후판정정야드  Bookout기준조회]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	

		
			recPara.setField("YD_LOC_GP",      yddatautil.setDataDefault(inDto.getField("YD_LOC_GP"), ""));
			

		    intGp =	ydCrnWrkMtlDao.getYdCrnwrkmtl(recPara, outRecSet, 23);
			

			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		szLogMsg = "JSP-SESSION [후판정정야드  Bookout기준조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
		
		return outRecSet;
	}//end of getCrnWrkMtlRef
	
	
	
	
	/**
	 *  크레인 작업재료 조회 (화면:크레인작업관리)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCrnWrkMtlRefCoil(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara    = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		int intGp = 0;
		
		String szYdGp  		= "";
		String szMethodName	= "getCrnWrkMtlRefCoil";		
		String szLogMsg 	= "";
		
		
		YdCrnWrkMtlDao ydCrnWrkMtlDao = new YdCrnWrkMtlDao();
		try {
			szLogMsg = "JSP-SESSION [크레인 작업재료 조회 - 화면:크레인작업관리]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	

			
			szYdGp = ydDaoUtils.paraRecChkNull(inDto, "YD_GP");
			recPara.setField("YD_WBOOK_ID", yddatautil.setDataDefault(inDto.getField("YD_WBOOK_ID"), ""));

			intGp =	ydCrnWrkMtlDao.getYdCrnwrkmtl(recPara, outRecSet, 304);			
			
			//Error Process
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		szLogMsg = "JSP-SESSION [크레인 작업재료 조회 - 화면:크레인작업관리] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
		
		return outRecSet;
	}//end of getCrnWrkMtlRefCoil
	
	/**
	 *  구내운송 IDLE 차량 LIST
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getLIdelCar(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara    = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		int intGp = 0;
		

		String szMethodName="getLIdelCar";		
		String szLogMsg = "";
		
		
		YdCarSpecDao ydCarSpecDao = new YdCarSpecDao();
		try {
			
			szLogMsg = "JSP-SESSION [구내운송 IDLE 차량 LIST]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			recPara.setField("YD_CAR_USE_GP1",      yddatautil.setDataDefault(inDto.getField("YD_CAR_USE_GP"), ""));
			recPara.setField("YD_CAR_USE_GP2",      yddatautil.setDataDefault(inDto.getField("YD_CAR_USE_GP"), ""));
			
			intGp = ydCarSpecDao.getYdCarspec(recPara, outRecSet, 5);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		szLogMsg = "JSP-SESSION [구내운송 IDLE 차량 LIST]끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return outRecSet;
	}//end of getLIdelCar
	

	
	
	/**
	 *  크레인 보류처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public Boolean updCrnDelaySet(JDTORecord[] inDto) throws DAOException {
		JDTORecord 	     recPara = JDTORecordFactory.getInstance().create();	
		
		
		boolean bool = false;
		int nRtnVal  = 0;
		String szMethodName       = "updCrnDelaySet";
		String szOperationName = "";
		String szLogMsg           = "크레인 보류처리";
			
		//스케줄 기준 테이블
		YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
	
		try{
			
			szLogMsg = "JSP-SESSION [크레인 보류처리]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			for(int x=0;x<inDto.length;x++){
				
				//  ============================ 보류기능     =======================================  //
				
				// 해당 스케줄을 포함하는 작업예약 ID로 현재 작업상태가  'W' 인크레인 스케줄을 "C"로 UPDATE한다.
				
				 recPara.setField("YD_WBOOK_ID", inDto[x].getField("YD_WBOOK_ID"));
				 recPara.setField("MODIFIER"   , inDto[x].getField("YD_USER_ID"));
				 
				//보류작업 대상정보
				 
				 ydUtils.displayRecord(szOperationName, recPara);
				 
				 
				 nRtnVal = ydCrnSchDao.updYdCrnschDelay(recPara, 0);
				
				if(nRtnVal < 0 ) {					
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					throw new DAOException();
				}
			}
			
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
		bool = true;
		
		szLogMsg = "JSP-SESSION [크레인 보류처리] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
		
		return new Boolean(bool);
	} // end of updCrnDelaySet
	
	
	
	
	/**
	 *  크레인 보류 해제 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public Boolean updCrnDelayCancleSet(JDTORecord[] inDto) throws DAOException {
		JDTORecord recPara  = JDTORecordFactory.getInstance().create();	
		boolean bool        = false;
		int nRtnVal         = 0;
		String szMethodName = "";
		String szLogMsg     = "";
		
		//스케줄 기준 테이블
//		YdSchRuleDao ydSchRuleDao = new YdSchRuleDao();
		YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
	
		try{
			
			szLogMsg = "JSP-SESSION [크레인 보류 해제 ]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			for(int x=0;x<inDto.length;x++){


				//  ============================ 보류기능     =======================================  //
				
				// 해당 스케줄을 포함하는 작업예약 ID로 현재 작업상태가  'C' 인크레인 스케줄을 "W"로 UPDATE한다.
				
				 recPara.setField("YD_WBOOK_ID", inDto[x].getField("YD_WBOOK_ID"));
				 recPara.setField("MODIFIER"   , inDto[x].getField("YD_USER_ID"));
				 
				 nRtnVal = ydCrnSchDao.updYdCrnschDelay(recPara, 1);
				 
				 
			}
		}catch(Exception e){
			szLogMsg ="크레인보류해제 처리 중 Exception";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
		bool = true;
		
		szLogMsg = "JSP-SESSION [크레인 보류 해제 ]끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
		
		
		return new Boolean(bool);
	} // end of updCrnDelayCancleSet() 
	
	
	//차량 출발 처리
	
	/**
	 *  야드 차량 출발 BACKUP 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	
	
	public void updCarDefBackUp(JDTORecord[] inDto) throws DAOException {
		
		
		JDTORecord     recPara      = null;
		JDTORecord     recPosPara   = null;
		JDTORecord     recPos       = null;
		JDTORecord     recSchIdPara = null;
		JDTORecordSet  rsRtn  		= null;
		JDTORecordSet  rsRtnPos 	= null;
		
		YdDelegate     ydDelegate   = new YdDelegate();
		YdCarSchDao    ydCarSchDao  = new YdCarSchDao();
		YdStkColDao    ydStkColDao  = new YdStkColDao();
		
		//구내운송/출하구분
		String szcarUseGp = null;
		
		//영/공차 구분
		String szCargp = null;
		String szSchId = null;
		
		int intGp = 0;
		String szMethodName="updCarDefBackUp";	
		String szOperationName = "차량 출발 BACKUP";
		String szLogMsg = "";
	
		try{
			
			szLogMsg = "JSP-SESSION [야드 차량 출발 BACKUP ]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			for(int x=0;x<inDto.length;x++){
				rsRtnPos      = JDTORecordFactory.getInstance().createRecordSet("YD");
				rsRtn         = JDTORecordFactory.getInstance().createRecordSet("YD");
				
				recPos        = JDTORecordFactory.getInstance().create();
				recPosPara    = JDTORecordFactory.getInstance().create();
				recSchIdPara  = JDTORecordFactory.getInstance().create();
				recPara       = JDTORecordFactory.getInstance().create();		
				
				szSchId       = yddatautil.setDataDefault(inDto[x].getField("YD_CAR_SCH_ID"), "");
				szcarUseGp    = yddatautil.setDataDefault(inDto[x].getField("YD_CAR_USE_GP"), "");
				
				recSchIdPara.setField("YD_CAR_SCH_ID",szSchId );
				intGp = ydCarSchDao.getYdCarsch(recSchIdPara, rsRtn, 0);
				
				if (intGp == 0 ){
					ydUtils.putLog(szSessionName, szMethodName, "해당 차량스케줄에 대한 정보가 없습니다 ", YdConstant.ERROR);
					return;
				}
				
				rsRtn.first();
				recSchIdPara = rsRtn.getRecord();
				
				//차량 사용구분에 따른 차량번호 사용 
				// L : 구내운송 , G : 출하차량
				
				if ("".equals(szcarUseGp)){
					//차량 사용구분이 없을경우
					ydUtils.putLog(szSessionName, szMethodName, "차량 사용구분이 없습니다 ", YdConstant.ERROR);
					return ;
					
				}else if("L".equals(szcarUseGp)){
					
					//구내운송인경우
					recPara.setField("JMS_TC_CD","YDYDJ651");
					recPara.setField("TRN_EQP_CD", recSchIdPara.getField("TRN_EQP_CD"));
					
					szCargp = yddatautil.setDataDefault(inDto[x].getField("YD_EQP_WRK_STAT"), "");

					//공차 인경우					
					if ("U".equals(szCargp)){
						recPosPara.setField("YD_STK_COL_GP", recSchIdPara.getField("YD_CARLD_LEV_LOC"));
						
						intGp = ydStkColDao.getYdStkcol(recPosPara, rsRtnPos, 0);
						
						if(intGp ==0 ){
							ydUtils.putLog(szSessionName, szMethodName, "적치열정보에서 해당 위치 정보를 가져올수 없습니다!! ", YdConstant.ERROR);
							return ; 
						}
						
						rsRtnPos.first();
						recPos = rsRtnPos.getRecord();
						//개소코드
						recPara.setField("SPOS_WLOC_CD", recPos.getField("WLOC_CD"));
						//야드포인트코드 
						recPara.setField("SPOS_YD_PNT_CD", recPos.getField("YD_PNT_CD"));
						
						recPara.setField("ARR_WLOC_CD", recSchIdPara.getField("SPOS_WLOC_CD"));

						recPara.setField("ARR_YD_PNT_CD", "");

						recPara.setField("TRN_WRK_FULLVOID_GP", "E"); 
						
						
						
					} else 	if ("L".equals(szCargp)){ 
						//영차 인경우
						recPosPara.setField("YD_STK_COL_GP", recSchIdPara.getField("YD_CARLD_STOP_LOC"));
						
						intGp = ydStkColDao.getYdStkcol(recPosPara, rsRtnPos, 0);
						
						if(intGp ==0 ){
							ydUtils.putLog(szSessionName, szMethodName, "적치열정보에서 해당 위치 정보를 가져올수 없습니다!! ", YdConstant.ERROR);
							return ; 
						}
						
						
						rsRtnPos.first();
						recPos = rsRtnPos.getRecord();
						//개소코드
						recPara.setField("SPOS_WLOC_CD", recPos.getField("WLOC_CD"));
						
						//야드포인트코드 
						recPara.setField("SPOS_YD_PNT_CD", recPos.getField("YD_PNT_CD"));						
						recPara.setField("ARR_WLOC_CD", recSchIdPara.getField("ARR_WLOC_CD"));
						recPara.setField("ARR_YD_PNT_CD","");
						recPara.setField("TRN_WRK_FULLVOID_GP", "F"); 
						
					}
					
					
				}else if("G".equals(szcarUseGp)){
					
					//출하인 경우				
					recPara.setField("JMS_TC_CD","YDYDJ656");
					
					//영/공차 처리 FLOW가 같음
					
					recPosPara.setField("YD_STK_COL_GP", recSchIdPara.getField("YD_CARLD_STOP_LOC"));
					intGp = ydStkColDao.getYdStkcol(recPosPara, rsRtnPos, 0);
					
					if(intGp ==0 ){
						ydUtils.putLog(szSessionName, szMethodName, "적치열정보에서 해당 위치 정보를 가져올수 없습니다!! ", YdConstant.ERROR);
						return ; 
					}
					
					rsRtnPos.first();
					recPos = rsRtnPos.getRecord();
					
					recPara.setField("SPOS_WLOC_CD"  , recPos.getField("WLOC_CD"));
					recPara.setField("SPOS_YD_PNT_CD", recPos.getField("YD_PNT_CD"));
				}else
				{
					ydUtils.displayRecord(szOperationName, recPara);
					ydUtils.putLog(szSessionName, szMethodName, "차량사용구분이 없습니다", YdConstant.ERROR);
				}
				
				
				ydUtils.displayRecord(szOperationName, recPara);
				ydDelegate.sendMsg(recPara);
			}
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
		szLogMsg = "JSP-SESSION [야드 차량 출발 BACKUP ]끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
	}
	
	
	
	
	
	
	//차량 도착 처리
	
	
	/**
	 *  야드 차량 도착 BACKUP 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	
	
	public void updCarArrBackUp(JDTORecord[] inDto) throws DAOException {
		
		
		JDTORecord     recPara      = null;
		JDTORecord     recPosPara   = null;
		JDTORecord     recPos       = null;
		JDTORecord     recSchIdPara = null;
		JDTORecord 	   recRtnSpec 	= null;
		
		JDTORecordSet  rsRtn  		= null;
		JDTORecordSet  rsRtnPos 	= null;
		JDTORecordSet  rsRtnSpec 	= null;
		
		int intGp = 0 ;
		
		YdDelegate ydDelegate       = new YdDelegate();
		YdCarSchDao ydCarSchDao     = new YdCarSchDao();
		YdStkColDao ydStkColDao     = new YdStkColDao();
		YdCarSpecDao ydCarSpecDao   = new YdCarSpecDao();
		
		
		//구내운송/출하구분
		String szcarUseGp           = null;
		//영/공차 구분
		String szCargp              = null;
		String szSchId              = null;
		
	
		String szMethodName="updCarArrBackUp";		
		String szLogMsg = "";
		String szOperationName = " 차량 도착 BACKUP";
		
		try{
			
			szLogMsg = "JSP-SESSION [ 야드 차량 도착 BACKUP]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			for(int x=0;x<inDto.length;x++){
				rsRtnPos     = JDTORecordFactory.getInstance().createRecordSet("YD");
				rsRtn        = JDTORecordFactory.getInstance().createRecordSet("YD");
				rsRtnSpec    = JDTORecordFactory.getInstance().createRecordSet("YD");
				
				recPos       = JDTORecordFactory.getInstance().create();
				recPosPara   = JDTORecordFactory.getInstance().create();
				recSchIdPara = JDTORecordFactory.getInstance().create();
				recPara      = JDTORecordFactory.getInstance().create();
				recRtnSpec   = JDTORecordFactory.getInstance().create();
				
				
				szSchId      = yddatautil.setDataDefault(inDto[x].getField("YD_CAR_SCH_ID"), "");
				szcarUseGp   = yddatautil.setDataDefault(inDto[x].getField("YD_CAR_USE_GP"), "");
				recSchIdPara.setField("YD_CAR_SCH_ID",szSchId );
				
				intGp        = ydCarSchDao.getYdCarsch(recSchIdPara, rsRtn, 0);
				
				if(intGp==0){
					ydUtils.putLog(szSessionName, szMethodName, "해당차량 스케줄 아이디에대한 정보가 없습니다!! ", YdConstant.ERROR);
					return;
				}
				
				
				rsRtn.first();
				recSchIdPara = rsRtn.getRecord();
				
				//차량 사용구분에 따른 차량번호 사용 
				// L : 구내운송 , G : 출하차량
				if ("".equals(szcarUseGp)){
					//차량 사용구분이 없을경우
					
				}else if("L".equals(szcarUseGp)){
					
					//구내운송인경우
					recPara.setField("JMS_TC_CD","YDYDJ650");
					recPara.setField("TRN_EQP_CD", recSchIdPara.getField("TRN_EQP_CD"));
					szCargp = yddatautil.setDataDefault(inDto[x].getField("YD_EQP_WRK_STAT"), "");

					//공차 인경우					
					if ("U".equals(szCargp)){
						
						//상차정지위치에서 데이터를 가지고 온다.
						recPosPara.setField("YD_STK_COL_GP", recSchIdPara.getField("YD_CARLD_STOP_LOC"));
						
						intGp = ydStkColDao.getYdStkcol(recPosPara, rsRtnPos, 0);
						
						if(intGp==0){
							ydUtils.putLog(szSessionName, szMethodName, "해당 적치열정보에 위치정보가 없습니다 !!", YdConstant.ERROR);
							return;
						}
						
						rsRtnPos.first();
						recPos = rsRtnPos.getRecord();
						//개소코드
						recPara.setField("ARR_WLOC_CD", recPos.getField("WLOC_CD"));
						//야드포인트코드 
						recPara.setField("ARR_YD_PNT_CD", recPos.getField("YD_PNT_CD"));
						recPara.setField("TRN_WRK_FULLVOID_GP", "E");
						
						//TRN_EQP_CD 로 정보를 찾는다.						
						intGp = ydCarSpecDao.getYdCarspec(recPara, rsRtnSpec, 2);
						if(intGp==0){
							ydUtils.putLog(szSessionName, szMethodName, "해당 스펙정보가 없습니다 !!", YdConstant.ERROR);
							return;
						}
						
						rsRtnSpec.first();
						recRtnSpec = rsRtnSpec.getRecord();		
						recPara.setField("TRN_EQP_STK_CAPA", recRtnSpec.getField("YD_WRK_ALW_WT"));
						
						//CAR_ARR_DT
						recPara.setField("CAR_ARR_DT", recSchIdPara.getField("YD_CARLD_ARR_DT"));
						
						
					} else 	if ("L".equals(szCargp)){
						
						//하차정지위치에서 데이터를 가지고 온다.
						recPosPara.setField("YD_STK_COL_GP", recSchIdPara.getField("YD_CARUD_STOP_LOC"));						
						intGp = ydStkColDao.getYdStkcol(recPosPara, rsRtnPos, 0);
						
						if(intGp==0){
							ydUtils.putLog(szSessionName, szMethodName, "적치열정보에 해당 위치정보가 없습니다 !!", YdConstant.ERROR);
							return;
						}
						
						
						rsRtnPos.first();
						recPos = rsRtnPos.getRecord();

						//개소코드
						recPara.setField("ARR_WLOC_CD", recPos.getField("WLOC_CD"));
						//야드포인트코드 
						recPara.setField("ARR_YD_PNT_CD", recPos.getField("YD_PNT_CD"));
						
						recPara.setField("TRN_WRK_FULLVOID_GP", "F");
						

						//TRN_EQP_CD 로 정보를 찾는다.						
						intGp = ydCarSpecDao.getYdCarspec(recPara, rsRtnSpec, 2);
						
						if(intGp==0){
							ydUtils.putLog(szSessionName, szMethodName, "해당 스펙정보가 없습니다 !!", YdConstant.ERROR);
							return;
						}
						
						rsRtnSpec.first();
						recRtnSpec = rsRtnSpec.getRecord();						
						
						recPara.setField("TRN_EQP_STK_CAPA", recRtnSpec.getField("YD_WRK_ALW_WT"));
						
						//CAR_ARR_DT
						
						recPara.setField("CAR_ARR_DT", recSchIdPara.getField("YD_CARUD_ARR_DT"));

						
					}
					
					
				}else if("G".equals(szcarUseGp)){
					
					//출하인 경우				
					//외판슬라브 출하차량 도착실적  BACKUP
					//야드별로 TC CODE 변경 예정 
					
					recPara.setField("JMS_TC_CD",           "YDYDJ652");
					recPara.setField("TC_CREATE_DDTT",		ydUtils.getCurDate("yyyyMMddHHmmss"));
					recPara.setField("TRANCE_ORD_DT",		ydUtils.getCurDate("yyyyMMddHHmmss"));
					
					//운송지시번호 -  현재는 입력 받는곳이나 정해진것이 없으므로 1111 세팅 추가 사항이 나올경우 수정요청 
					recPara.setField("TRANCE_ORD_SEQNO",	"1111");
					
					//영/공차 처리 FLOW가 같음
					
					recPosPara.setField("YD_STK_COL_GP", recSchIdPara.getField("YD_CARLD_STOP_LOC"));
					intGp = ydStkColDao.getYdStkcol(recPosPara, rsRtnPos, 0);
					
					if(intGp==0){
						ydUtils.putLog(szSessionName, szMethodName, "적치열정보에 해당 위치정보가 없습니다 !!", YdConstant.ERROR);
						return;
					}
					
					
					
					rsRtnPos.first();
					recPos = rsRtnPos.getRecord();
					
					recPara.setField("CAR_NO",         recSchIdPara.getField("CAR_NO"));
					recPara.setField("CARD_NO",        recSchIdPara.getField("CARD_NO"));
					recPara.setField("SPOS_WLOC_CD",   recPos.getField("WLOC_CD"));
					recPara.setField("SPOS_YD_PNT_CD", recPos.getField("YD_PNT_CD"));
					recPara.setField("CARD_NO",        recSchIdPara.getField("CARD_NO"));
					
					recPara.setField("YD_EQP_GP","TR");
					
					//TRN_EQP_CD 로 정보를 찾는다.						
					intGp = ydCarSpecDao.getYdCarspec(recPara, rsRtnSpec, 2);
					
					if(intGp==0){
						ydUtils.putLog(szSessionName, szMethodName, "해당 스펙정보가 없습니다  !!", YdConstant.ERROR);
						return;
					}
					
					rsRtnSpec.first();
					recRtnSpec = rsRtnSpec.getRecord();		
					
					recPara.setField("YD_WRK_ALW_L", 			recRtnSpec.getField("YD_WRK_ALW_L"));
					recPara.setField("YD_WRK_ALW_W", 			recRtnSpec.getField("YD_WRK_ALW_W"));
					recPara.setField("YD_WRK_ALW_SKID_PITCH", 	recRtnSpec.getField("YD_WRK_ALW_SKID_PITCH"));
					recPara.setField("YD_WRK_ALW_SH", 			recRtnSpec.getField("YD_WRK_ALW_SH"));
					recPara.setField("YD_WRK_ALW_WT", 			recRtnSpec.getField("YD_WRK_ALW_WT"));
				}
				
				ydUtils.displayRecord(szOperationName, recPara);
				ydDelegate.sendMsg(recPara);
			}
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
		szLogMsg = "JSP-SESSION [ 야드 차량 도착 BACKUP] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
		

	}
	
	

	
	/**
	 *  위치검색 범위 조회 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getYdLocSrchRng(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara    = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		
		String szMsg        = "";		
		String szMethodName = "getYdLocSrchRng";
		int intRtnVal = 0;
	
		YdLocSrchRngDao ydLocSrchRngDao = new YdLocSrchRngDao();	
		try {
			
			//Top Grid
			
			szMsg = "JSP-SESSION [ 위치검색 범위 조회 ]시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
			
			
			recPara.setField("YD_GP",      yddatautil.setDataDefault(inDto.getField("YD_GP"), ""));
			recPara.setField("YD_BAY_GP",  yddatautil.setDataDefault(inDto.getField("YD_BAY_GP"), ""));
			recPara.setField("YD_SCH_CD",  yddatautil.setDataDefault(inDto.getField("YD_SCH_CD"), ""));
			recPara.setField("YD_ROUTE_GP",  yddatautil.setDataDefault(inDto.getField("YD_ROUTE_GP"), ""));
		
			intRtnVal      =  ydLocSrchRngDao.getYdLocsrchrng(recPara, outRecSet, 2);
			
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
			
			// 레코드셋을 앞으로 커서를 위치 시켜준다 .
			outRecSet.first();
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		szMsg = "JSP-SESSION [ 위치검색 범위 조회 ]끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return outRecSet;
	}//end of getYdLocSrchRng
	
	/**
	 *  위치검색 범위 조회 (화면:위치검색SPAN관리)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getYdLocSrchRngCoil(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara    = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		
		String szMsg        = "";		
		String szMethodName = "getYdLocSrchRngCoil";
		int intRtnVal = 0;
	
		YdLocSrchRngDao ydLocSrchRngDao = new YdLocSrchRngDao();	
		try {
			
			//Top Grid
			
			szMsg = "JSP-SESSION [ 위치검색 범위 조회 - 화면:위치검색SPAN관리]시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
			recPara.setField("YD_GP",      yddatautil.setDataDefault(inDto.getField("YD_GP"), ""));
			recPara.setField("YD_BAY_GP",  yddatautil.setDataDefault(inDto.getField("YD_BAY_GP"), ""));
			recPara.setField("YD_SCH_CD",  yddatautil.setDataDefault(inDto.getField("YD_SCH_CD"), ""));
			intRtnVal      =  ydLocSrchRngDao.getYdLocsrchrng(recPara, outRecSet, 301);
			
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
			
			// 레코드셋을 앞으로 커서를 위치 시켜준다 .
			outRecSet.first();
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		szMsg = "JSP-SESSION [ 위치검색 범위 조회 - 화면:위치검색SPAN관리]끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return outRecSet;
	}//end of getYdLocSrchRng
	
	/**
	 *  저장집합코드조회 (화면:위치검색SPAN관리)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getStrGtrCodeNew(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara    = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		
		String szMsg        = "";		
		String szMethodName = "getStrGtrCodeNew";
		int intRtnVal = 0;
	
		YdLocSrchRngDao ydLocSrchRngDao = new YdLocSrchRngDao();	
		try {
			
			//Top Grid
			
			szMsg = "JSP-SESSION [ 저장집합코드조회 - 화면:위치검색SPAN관리]시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
			recPara.setField("GBN",      yddatautil.setDataDefault(inDto.getField("GBN"), ""));
			
			intRtnVal      =  ydLocSrchRngDao.getYdLocsrchrng(recPara, outRecSet, 302);
			
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
			
			// 레코드셋을 앞으로 커서를 위치 시켜준다 .
			outRecSet.first();
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		szMsg = "JSP-SESSION [ 저장집합코드조회 - 화면:위치검색SPAN관리]끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return outRecSet;
	}//end of getStrGtrCodeNew
	
	
	/**
	 *  위치검색 범위 조회 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getYdStkBedByYdStrGtrCd(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara    = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		
		String szMsg        = "";		
		String szMethodName = "getYdStkBedByYdStrGtrCd";
		int intRtnVal = 0;
	
		YdStkBedDao ydStkBedDao = new YdStkBedDao();
			
		try {
			

			szMsg = "JSP-SESSION [ 위치검색 범위 조회 ]시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
					
			//LGrid	
			recPara.setField("YD_STR_GTR_CD",            yddatautil.setDataDefault(inDto.getField("YD_STR_GTR_CD"), ""));
			recPara.setField("YD_ROUTE_GP", 			 yddatautil.setDataDefault(inDto.getField("YD_ROUTE_GP"), ""));
			recPara.setField("YD_LOC_SRCH_RNG_REG_SNO",  yddatautil.setDataDefault(inDto.getField("YD_LOC_SRCH_RNG_REG_SNO"), ""));
			                  
		
			ydStkBedDao.getYdStkbed(recPara, outRecSet, 11);
			
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
			
			// 레코드셋을 앞으로 커서를 위치 시켜준다 .
			outRecSet.first();
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		szMsg = "JSP-SESSION [ 위치검색 범위 조회 ]끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return outRecSet;
	}//end of getYdStkBedByYdStrGtrCd
	
	/**
	 *  위치검색 범위 조회 (화면:위치검색SPAN관리)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getYdStkBedByYdStrGtrCdCoil(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara    = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		
		String szMsg        = "";		
		String szMethodName = "getYdStkBedByYdStrGtrCdCoil";
		int intRtnVal = 0;
	
		YdStkBedDao ydStkBedDao = new YdStkBedDao();
			
		try {
			

			szMsg = "JSP-SESSION [ 위치검색 범위 조회 (화면:위치검색SPAN관리) ]시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
					
			//LGrid	
			recPara.setField("YD_STR_GTR_CD",            yddatautil.setDataDefault(inDto.getField("YD_STR_GTR_CD"), ""));
			recPara.setField("YD_LOC_SRCH_RNG_REG_SNO",  yddatautil.setDataDefault(inDto.getField("YD_LOC_SRCH_RNG_REG_SNO"), ""));
			                  
		
			ydStkBedDao.getYdStkbed(recPara, outRecSet, 301);
			
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
			
			// 레코드셋을 앞으로 커서를 위치 시켜준다 .
			outRecSet.first();
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		szMsg = "JSP-SESSION [ 위치검색 범위 조회 (화면:위치검색SPAN관리) ]끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return outRecSet;
	}//end of getYdStkBedByYdStrGtrCdCoil
	
	/**
	 *  위치검색 베드 조회 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getYdLocSrchBed(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara    = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		
		String szMsg                = "";		
		String szMethodName         = "getYdLocSrchBed";
		int intRtnVal = 0;
		
		YdLocSrchBedDao ydLocSrchBedDao = new YdLocSrchBedDao();
			
		try {
			
			szMsg = "JSP-SESSION [ 위치검색 베드 조회  ]시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
			
			
			//RGrid
			recPara.setField("YD_LOC_SRCH_RNG_REG_SNO",  yddatautil.setDataDefault(inDto.getField("YD_LOC_SRCH_RNG_REG_SNO"), ""));
		                      
			ydLocSrchBedDao.getYdLocsrchbed(recPara, outRecSet, 1);
			
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
		
		szMsg = "JSP-SESSION [ 위치검색 베드 조회  ]끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
		
		return outRecSet;
	}//end of getYdLocSrchBed
	
	/**
	 *  위치검색 베드 조회 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getYdLocSrchBedCoil(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara    = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		
		String szMsg                = "";		
		String szMethodName         = "getYdLocSrchBedCoil";
		int intRtnVal = 0;
		
		YdLocSrchBedDao ydLocSrchBedDao = new YdLocSrchBedDao();
			
		try {
			
			szMsg = "JSP-SESSION [ 위치검색 베드 조회  ]시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
		
			String message = "\n";
			//form 에서 넘어온 모든 name을  field name값으로 설정과 value 값도 셋팅
			//jsp에서 변수명을 설정만 해주면 자동으로 셋팅됨
			//form name값과 field name을 다르게 설정할시 while 밑에 설정
			Iterator iter = inDto.iterateName();
			int cnt = 0;
			while(iter.hasNext()) {
				String frmNm 	= (String) iter.next();		//form name
				String fieldNm  = frmNm;					//셋팅할 field name
				//page 처리일 경우 setFiedname 설정 
				if(frmNm.equals("PAGE_SIZE")) {
					fieldNm = "ROW_CNT";
				}else if(frmNm.equals("PAGE_NO")) {
					fieldNm = "PAGE_CNT"; 
				}
				recPara.setField(fieldNm,	yddatautil.setDataDefault(inDto.getField(frmNm),""));
				message += "[count:"+cnt+"- name:"+fieldNm +" - value:"+yddatautil.setDataDefault(inDto.getField(frmNm),"")+"] \n";
				cnt++;
			}		
			ydUtils.putLog(szSessionName,"\nform 에 있는 모든 변수값 : ", message, YdConstant.INFO);
		                      
			ydLocSrchBedDao.getYdLocsrchbed(recPara, outRecSet, 301);
			
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
		
		szMsg = "JSP-SESSION [ 위치검색 베드 조회  ]끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
		
		return outRecSet;
	}//end of getYdLocSrchBed
		
	/**
	 *  슬라브 공통 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getPtSlabComm(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara    = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet  = JDTORecordFactory.getInstance().createRecordSet("retTmp");		
		String szMsg        = "";		
		String szMethodName = "getPtSlabComm";
		int intRtnVal = 0;
	
		YdStockDao ydStockDao = new YdStockDao();
			
		try {
			
			szMsg = "JSP-SESSION [ 슬라브 공통 조회  ]시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
			
			recPara.setField("SLAB_NO",	 yddatautil.setDataDefault(inDto.getField("STL_NO"), ""));
			ydStockDao.getYdStock(recPara, outRecSet, 43);
			
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
			
			// 레코드셋을 앞으로 커서를 위치 시켜준다 .
			outRecSet.first();
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		szMsg = "JSP-SESSION [ 슬라브 공통 조회  ]끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return outRecSet;
	}//end of getPtSlabComm
	
	
	/**
	 *  주편 공통 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getPtMSlabComm(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara    = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet  = JDTORecordFactory.getInstance().createRecordSet("retTmp");		
		String szMsg                = "";		
		String szMethodName         = "getPtMSlabComm";
		int intRtnVal               = 0;
	
		YdStockDao ydStockDao      = new YdStockDao();
			
		try {
			
			szMsg = "JSP-SESSION [주편 공통 조회 ]시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
	
			recPara.setField("MSLAB_NO",	 yddatautil.setDataDefault(inDto.getField("STL_NO"), ""));
			ydStockDao.getYdStock(recPara, outRecSet, 44);
			
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
			
			// 레코드셋을 앞으로 커서를 위치 시켜준다 .
			outRecSet.first();
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		szMsg = "JSP-SESSION [주편 공통 조회 ]끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return outRecSet;
	}//end of getPtMSlabComm
	
	
	/**
	 *  후판 공통 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getPtPlateComm(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara    = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet  = JDTORecordFactory.getInstance().createRecordSet("retTmp");		
		String szMsg                = "";		
		String szMethodName         = "getPtPlateComm";
		int intRtnVal = 0;
	
		YdStockDao ydStockDao = new YdStockDao();
			
		try {
			
			szMsg = "JSP-SESSION [후판 공통 조회 ] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			recPara.setField("PLATE_NO", yddatautil.setDataDefault(inDto.getField("STL_NO"), ""));
			ydStockDao.getYdStock(recPara, outRecSet, 45);
			
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
			
			// 레코드셋을 앞으로 커서를 위치 시켜준다 .
			outRecSet.first();
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		szMsg = "JSP-SESSION [후판 공통 조회 ] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return outRecSet;
	}//end of getPtPlateComm
	
	
	/**
	 *  코일 공통 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getPtCoilComm(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara    = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet  = JDTORecordFactory.getInstance().createRecordSet("retTmp");		
		String szMsg                = "";		
		String szMethodName         = "getPtCoilComm";
		int intRtnVal = 0;
	
		YdStockDao ydStockDao = new YdStockDao();
			
		try {
			
			szMsg = "JSP-SESSION [코일 공통 조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			recPara.setField("COIL_NO", yddatautil.setDataDefault(inDto.getField("STL_NO"), ""));
			ydStockDao.getYdStock(recPara, outRecSet, 46);
			
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
			
			// 레코드셋을 앞으로 커서를 위치 시켜준다 .
			outRecSet.first();
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		szMsg = "JSP-SESSION [코일 공통 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return outRecSet;
	}//end of getPtCoilComm
	
	
	/**
	 * 위치 검색 BED 수정 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */	
	public void updYdLocSrchBed(JDTORecord[] inDto) throws DAOException {
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		
		String szMethodName="updYdLocSrchBed";		
		String szLogMsg = "";
		
		try{
			
			szLogMsg = "JSP-SESSION [ 위치 검색 BED 수정] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			for(int x=0;x<inDto.length;x++){
				
				recPara.setField("YD_LOC_SRCH_RNG_REG_SNO",inDto[x].getField("YD_LOC_SRCH_RNG_REG_SNO"));
				recPara.setField("YD_LOC_SRCH_BED_REG_SNO",inDto[x].getField("YD_LOC_SRCH_BED_REG_SNO"));
				recPara.setField("YD_STK_BED_SRCH_SEQ",    inDto[x].getField("YD_STK_BED_SRCH_SEQ"));
				recPara.setField("YD_STK_COL_GP",          inDto[x].getField("YD_STK_COL_GP"));
				recPara.setField("YD_STK_BED_NO",          inDto[x].getField("YD_STK_BED_NO"));
				
				
				
				//야드위치검색Bed등록차수Check
				

				if((null == recPara.getField("YD_LOC_SRCH_BED_REG_SNO")) || "".equals(recPara.getFieldString("YD_LOC_SRCH_BED_REG_SNO").trim()))
				{
					
					//Insert 
					this.insYdLocSrchBed(recPara);
					
					
				} else {
					
					//Update
					this.updYdLocSrchBed(recPara);
					
					
				}
			}
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
		szLogMsg = "JSP-SESSION [ 위치 검색 BED 수정] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
	}
	
	/** 2010.04.05
	 * 위치 검색 BED 수정 (화면:위치검색SPAN관리)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */	
	public void updYdLocSrchBedCoil(JDTORecord[] inDto) throws DAOException {
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		
		String szMethodName="updYdLocSrchBedCoil";		
		String szLogMsg = "";
		
		try{
			
			szLogMsg = "JSP-SESSION [ 위치 검색 BED 수정 - 화면:위치검색SPAN관리] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			JDTORecord pjdto = inDto[0];
			//delete 후에 insert
			this.delYdLocSrchBedCoil_ALL(pjdto);
			
			for(int x=0;x<inDto.length;x++){
				String message = "\n";
				//form 에서 넘어온 모든 name을  field name값으로 설정과 value 값도 셋팅
				//jsp에서 변수명을 설정만 해주면 자동으로 셋팅됨
				//form name값과 field name을 다르게 설정할시 while 밑에 설정
				Iterator iter = inDto[x].iterateName();
				int cnt = 0;
				while(iter.hasNext()) {
					String frmNm 	= (String) iter.next();		//form name
					String fieldNm  = frmNm;					//셋팅할 field name
					//page 처리일 경우 setFiedname 설정 
					if(frmNm.equals("PAGE_SIZE")) {
						fieldNm = "ROW_CNT";
					}else if(frmNm.equals("PAGE_NO")) {
						fieldNm = "PAGE_CNT"; 
					}
					recPara.setField(fieldNm,	yddatautil.setDataDefault(inDto[x].getField(frmNm),""));
					message += "[row : "+x +" - count:"+cnt+"- name:"+fieldNm +" - value:"+yddatautil.setDataDefault(inDto[x].getField(frmNm),"")+"] \n";
					cnt++;
				}		
				ydUtils.putLog(szSessionName,"\nform 에 있는 모든 변수값 log : ", message, YdConstant.INFO);
				
				//Insert 
				this.insYdLocSrchBedCoil(recPara);
			}
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		szLogMsg = "JSP-SESSION [ 위치 검색 BED 수정 - 화면:위치검색SPAN관리] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
	}
	
	/**
	 * 위치검색베드  수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */	
	public void updYdLocSrchBed(JDTORecord recMsg) throws DAOException{
		
		YdLocSrchBedDao ydLocSrchBedDao = new YdLocSrchBedDao();
		String szMethodName="updYdLocSrchBed";		
		String szLogMsg = "";

		
		try{
			
			szLogMsg = "JSP-SESSION [위치검색베드  수정 JDTORecord]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			ydLocSrchBedDao.updYdLocsrchbed(recMsg, 0);
			
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}	
		
		szLogMsg = "JSP-SESSION [위치검색베드  수정 JDTORecord]끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
	}
	
	/** 2010.04.05
	 * 위치검색베드  수정 (화면:위치검색SPAN관리)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */	
	public void updYdLocSrchBedCoil(JDTORecord recMsg) throws DAOException{
		
		YdLocSrchBedDao ydLocSrchBedDao = new YdLocSrchBedDao();
		String szMethodName="updYdLocSrchBedCoil";		
		String szLogMsg = "";

		try{
			
			szLogMsg = "JSP-SESSION [위치검색베드  수정 JDTORecord]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			ydLocSrchBedDao.updYdLocsrchbed(recMsg, 300);			
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}	
		
		szLogMsg = "JSP-SESSION [위치검색베드  수정 JDTORecord]끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
	}
	
	
	/**
	 * 위치검색베드 추가 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */	
	public void insYdLocSrchBed(JDTORecord recMsg)throws DAOException{
		int intMax = 0;
		String szMethodName="insYdLocSrchBed";		
		String szLogMsg = "";
		
		try{
			
			szLogMsg = "JSP-SESSION [위치검색베드 추가]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			YdLocSrchBedDao ydLocSrchBedDao = new YdLocSrchBedDao();
			
			//MAX 차수 구하기
			intMax = this.maxYdLocSrchBed(recMsg);
			
			//MAX 차수 + 1값을 넣어준다.
			recMsg.setField("YD_LOC_SRCH_BED_REG_SNO", new Integer(intMax+1));
						
			//위치 검색 테이블 레코드 추가 
			ydLocSrchBedDao.insYdLocsrchbed(recMsg);
			
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}	
		
		szLogMsg = "JSP-SESSION [위치검색베드 추가] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
	}
	
	/** 2010.04.05
	 * 위치검색베드 추가 (화면:위치검색SPAN관리)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */	
	public int insYdLocSrchBedCoil(JDTORecord recMsg)throws DAOException{
		int returnInt = 0;
		String szMethodName="insYdLocSrchBedCoil";		
		String szLogMsg = "";
		
		try{
			
			szLogMsg = "JSP-SESSION [위치검색베드 추가 - 화면:위치검색SPAN관리]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			YdLocSrchBedDao ydLocSrchBedDao = new YdLocSrchBedDao();
			returnInt += ydLocSrchBedDao.insYdLocSrchBedCoil(recMsg);
			
			szLogMsg = "JSP-SESSION [위치검색베드 추가 - 화면:위치검색SPAN관리] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return returnInt;
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}	
		
		
		
	}
	
	
	/**
	 * 위치검색 테이블 삭제 기능
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */	
	public void delYdLocSrchBed(JDTORecord[] inDto)throws DAOException{
		JDTORecord recPara = null;

		String szMethodName="delYdLocSrchBed";		
		String szLogMsg = "";
		
		
		try{
			YdLocSrchBedDao ydLocSrchBedDao = new YdLocSrchBedDao();
			
			szLogMsg = "JSP-SESSION [위치검색 테이블 삭제]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			
			for(int x=0;x<inDto.length;x++){
				recPara = JDTORecordFactory.getInstance().create();
				
				recPara.setField("YD_SCH_CD",              inDto[x].getField("YD_SCH_CD"));
				recPara.setField("YD_ROUTE_GP",            inDto[x].getField("YD_ROUTE_GP"));				
				recPara.setField("YD_LOC_SRCH_RNG_REG_SNO",inDto[x].getField("YD_LOC_SRCH_RNG_REG_SNO"));
				recPara.setField("YD_LOC_SRCH_BED_REG_SNO",inDto[x].getField("YD_LOC_SRCH_BED_REG_SNO"));
				recPara.setField("DEL_YN",                 "Y");
				
				//위치 검색 테이블 레코드 삭제 (DEL_YN -> Y)
				ydLocSrchBedDao.updYdLocsrchbed(recPara, 0);
			}
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
		szLogMsg = "JSP-SESSION [위치검색 테이블 삭제]끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
	}
	
	/**
	 * 위치검색 테이블 삭제 기능 (화면:위치검색SPAN관리)
	 * 모든 DATA 삭제 후 INSERT	
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */	
	public void delYdLocSrchBedCoil_ALL(JDTORecord inDto)throws DAOException{
		JDTORecord recPara = JDTORecordFactory.getInstance().create();

		String szMethodName="delYdLocSrchBedCoil";		
		String szLogMsg = "";
		
		
		try{
			int delCnt = 0;
			YdLocSrchBedDao ydLocSrchBedDao = new YdLocSrchBedDao();
			
			szLogMsg = "JSP-SESSION [위치검색 테이블 삭제 - 화면:위치검색SPAN관리]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			/*	JDTORecord
			 *	넘어온 recOutPara 헤더를 키를 본다
			 */
			Iterator iter = inDto.iterateName();
			while(iter.hasNext()){
				String str = (String) iter.next();
				ydUtils.putLog("SESSION", "delYdLocSrchBedCoil_ALL", "NAME::"+str +" -- value::" +inDto.getFieldString(str), YdConstant.INFO);
			}
//			for(int x=0;x<inDto.length;x++){
//				recPara = JDTORecordFactory.getInstance().create();
//						
				recPara.setField("YD_LOC_SRCH_RNG_REG_SNO", inDto.getField("YD_LOC_SRCH_RNG_REG_SNO"));
				//YD_LOC_SRCH_RNG_REG_SNO 번호로 TB_YD_LOCSRCHBED 삭제
				delCnt = ydLocSrchBedDao.delYdLocSrchBedCoil(recPara, 300);
//			}
			ydUtils.putLog(szSessionName, "삭제 Count : ", delCnt+"", YdConstant.INFO);
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
		szLogMsg = "JSP-SESSION [위치검색 테이블 삭제 - 화면:위치검색SPAN관리]끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
	}
	
	/**
	 * 위치검색 테이블 삭제 기능 (화면:위치검색SPAN관리)
	 * check DATA 삭제	
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */	
	public void delYdLocSrchBedCoil_CHECK2(JDTORecord[] inDto)throws DAOException{
		JDTORecord recPara = JDTORecordFactory.getInstance().create();

		String szMethodName="delYdLocSrchBedCoil_CHECK2";		
		String szLogMsg = "";
		
		
		try{
			int delCnt = 0;
			YdLocSrchBedDao ydLocSrchBedDao = new YdLocSrchBedDao();
			YdLocSrchRngDao YdLocSrchRngDao = new YdLocSrchRngDao();
			
			
			szLogMsg = "JSP-SESSION [위치검색 테이블 삭제 - 화면:위치검색SPAN관리]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
//			그리드에 체크한 값들만 넘어온다.
			for(int x=0;x<inDto.length;x++){
				/*	JDTORecord
				 *	넘어온 recOutPara 헤더를 키를 본다
				 */
//				Iterator iter = inDto[x].iterateName();
//				while(iter.hasNext()){
//					String str = (String) iter.next();
//					ydUtils.putLog("SESSION", "delYdLocSrchBedCoil_CHECK", "NAME::"+str +" -- value::" +inDto[x].getFieldString(str), YdConstant.INFO);
//				}
				
				recPara = JDTORecordFactory.getInstance().create();
				
				recPara.setField("YD_LOC_SRCH_RNG_REG_SNO",	yddatautil.setDataDefault(inDto[x].getField("YD_LOC_SRCH_RNG_REG_SNO"),""));
						
				//YD_LOC_SRCH_RNG_REG_SNO 번호로 TB_YD_LOCSRCHBED 삭제
				delCnt = ydLocSrchBedDao.delYdLocSrchBedCoil(recPara, 302);	
						
				//YD_LOC_SRCH_RNG_REG_SNO 번호로 TB_YD_LOCSRCHRNG 삭제
				delCnt = YdLocSrchRngDao.delYdLocSrchRngCoil(recPara, 300);
	
			}
			

			
			
			ydUtils.putLog(szSessionName, "삭제 Count : ", delCnt+"", YdConstant.INFO);
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
		szLogMsg = "JSP-SESSION [위치검색 테이블 삭제 - 화면:위치검색SPAN관리]끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
	}
	
	
	/**
	 * 위치검색 테이블 삭제 기능 (화면:위치검색SPAN관리)
	 * check DATA 삭제	
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */	
	public void delYdLocSrchBedCoil_CHECK(JDTORecord[] inDto)throws DAOException{
		JDTORecord recPara = JDTORecordFactory.getInstance().create();

		String szMethodName="delYdLocSrchBedCoil_CHECK";		
		String szLogMsg = "";
		
		
		try{
			int delCnt = 0;
			YdLocSrchBedDao ydLocSrchBedDao = new YdLocSrchBedDao();
			YdLocSrchRngDao YdLocSrchRngDao = new YdLocSrchRngDao();
			
			
			szLogMsg = "JSP-SESSION [위치검색 테이블 삭제 - 화면:위치검색SPAN관리]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
//			그리드에 체크한 값들만 넘어온다.
			for(int x=0;x<inDto.length;x++){
				/*	JDTORecord
				 *	넘어온 recOutPara 헤더를 키를 본다
				 */
//				Iterator iter = inDto[x].iterateName();
//				while(iter.hasNext()){
//					String str = (String) iter.next();
//					ydUtils.putLog("SESSION", "delYdLocSrchBedCoil_CHECK", "NAME::"+str +" -- value::" +inDto[x].getFieldString(str), YdConstant.INFO);
//				}
				
				recPara = JDTORecordFactory.getInstance().create();
				
				recPara.setField("YD_LOC_SRCH_RNG_REG_SNO",	yddatautil.setDataDefault(inDto[x].getField("YD_LOC_SRCH_RNG_REG_SNO"),""));
				recPara.setField("YD_STK_COL_GP",			yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_GP"),""));	
						
				//YD_LOC_SRCH_RNG_REG_SNO 번호로 TB_YD_LOCSRCHBED 삭제
				delCnt = ydLocSrchBedDao.delYdLocSrchBedCoil(recPara, 301);
	
			}
			

			
			
			ydUtils.putLog(szSessionName, "삭제 Count : ", delCnt+"", YdConstant.INFO);
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
		szLogMsg = "JSP-SESSION [위치검색 테이블 삭제 - 화면:위치검색SPAN관리]끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
	}
	
	/**
	 * 위치검색 범위 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */	
	public void updYdLocSrchRng(JDTORecord[] inDto)throws DAOException{
		JDTORecord recPara = null;
		String szMethodName="updYdLocSrchRng";		
		String szLogMsg = "";
		
		
		try{
			
			szLogMsg = "JSP-SESSION [위치검색 범위 수정] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			YdLocSrchRngDao ydLocSrchRngDao = new YdLocSrchRngDao();
			
			for(int x=0;x<inDto.length;x++){
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_SCH_CD",				yddatautil.setDataDefault(inDto[x].getField("YD_SCH_CD"),""));
				recPara.setField("YD_STR_GTR_CD",			yddatautil.setDataDefault(inDto[x].getField("YD_STR_GTR_CD"),""));				
				recPara.setField("YD_ROUTE_GP",				yddatautil.setDataDefault(inDto[x].getField("YD_ROUTE_GP"),""));
				recPara.setField("YD_LOC_SRCH_RNG_SEQ",		yddatautil.setDataDefault(inDto[x].getField("YD_LOC_SRCH_RNG_SEQ"),""));
				recPara.setField("YD_LOC_SRCH_RNG_ACT_STAT",yddatautil.setDataDefault(inDto[x].getField("YD_LOC_SRCH_RNG_ACT_STAT"),""));
				recPara.setField("YD_STK_BED_SRCH_METHOD",	yddatautil.setDataDefault(inDto[x].getField("YD_STK_BED_SRCH_METHOD"),""));
				
				recPara.setField("YD_LOC_SRCH_RNG_REG_SNO", yddatautil.setDataDefault(inDto[x].getField("YD_LOC_SRCH_RNG_REG_SNO"),""));
				
				ydLocSrchRngDao.updYdLocsrchrng(recPara, 0);
				
			}
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}	
		
		szLogMsg = "JSP-SESSION [위치검색 범위 수정] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
	}
	
	/**
	 * 위치검색 범위 수정 (화면:위치검색SPAN관리)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */	
	public void updYdLocSrchRngCoil(JDTORecord[] inDto)throws DAOException{
		JDTORecord recPara = null;
		String szMethodName="updYdLocSrchRngCoil";		
		String szLogMsg = "";
		
		
		try{
			
			szLogMsg = "JSP-SESSION [위치검색 범위 수정 - 화면:위치검색SPAN관리] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			YdLocSrchRngDao ydLocSrchRngDao = new YdLocSrchRngDao();
			
			for(int x=0;x<inDto.length;x++){
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_SCH_CD",				yddatautil.setDataDefault(inDto[x].getField("YD_SCH_CD"),""));
				recPara.setField("YD_STR_GTR_CD",			yddatautil.setDataDefault(inDto[x].getField("YD_STR_GTR_CD"),""));				
				recPara.setField("YD_ROUTE_GP",				yddatautil.setDataDefault(inDto[x].getField("YD_ROUTE_GP"),""));
				recPara.setField("YD_LOC_SRCH_RNG_SEQ",		yddatautil.setDataDefault(inDto[x].getField("YD_LOC_SRCH_RNG_SEQ"),""));
				recPara.setField("YD_LOC_SRCH_RNG_ACT_STAT",yddatautil.setDataDefault(inDto[x].getField("YD_LOC_SRCH_RNG_ACT_STAT"),""));
				recPara.setField("YD_STK_BED_SRCH_METHOD",	yddatautil.setDataDefault(inDto[x].getField("YD_STK_BED_SRCH_METHOD"),""));
				recPara.setField("YD_LOC_SRCH_RNG_REG_SNO", yddatautil.setDataDefault(inDto[x].getField("YD_LOC_SRCH_RNG_REG_SNO"),""));
				
				ydLocSrchRngDao.updYdLocsrchrng(recPara, 300);
				
			}
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}	
		
		szLogMsg = "JSP-SESSION [위치검색 범위 수정 - 화면:위치검색SPAN관리] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
	}
	
	/**
	 * 위치검색 테이블 MAX차수 구하기 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */	
	public int maxYdLocSrchBed(JDTORecord recMsg)throws DAOException{
		int intMax =0;
		int intGp = 0;
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JDTORecord recRtn = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet  = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		YdLocSrchBedDao ydLocSrchBedDao = new YdLocSrchBedDao();
		
		String szMethodName="maxYdLocSrchBed";	
		String szOperationName = "위치검색 테이블 MAX차수";
		String szLogMsg = "";
		
		try{
			
			szLogMsg = "JSP-SESSION [위치검색 테이블 MAX차수 구하기 ]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			recPara.setField("YD_SCH_CD",              recMsg.getField("YD_SCH_CD"));
			recPara.setField("YD_ROUTE_GP",            recMsg.getField("YD_ROUTE_GP"));
			recPara.setField("YD_LOC_SRCH_RNG_REG_SNO",recMsg.getField("YD_LOC_SRCH_RNG_REG_SNO"));
			
			intGp = ydLocSrchBedDao.getYdLocsrchbed(recPara, outRecSet, 2);
			
			//반환된 레코드가 없을경우는 0을 리턴해준다.
			if(outRecSet.size() < 1 ){
				return 0;
			}
			//실제적으로 한번만 수행된다.
			outRecSet.first();
			do{
				recRtn = outRecSet.getRecord();
				ydUtils.displayRecord(szOperationName, recRtn);
				intMax = recRtn.getFieldInt("YD_LOC_SRCH_BED_REG_SNO");
				
			}while(outRecSet.next());
			
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}	
		
		szLogMsg = "JSP-SESSION [위치검색 테이블 MAX차수 구하기 ]끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return intMax;
	}
	
	

	/**
	 *  저장품 번호로 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getStock(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara    = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet  = JDTORecordFactory.getInstance().createRecordSet("retTmp");		
		String szMsg        = "";		
		String szMethodName = "getStock";
		int intRtnVal = 0;
	
		YdStockDao ydStockDao = new YdStockDao();
			
		try {
			
			szMsg = "JSP-SESSION [저장품 번호로 조회]시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	

			
			recPara.setField("STL_NO", yddatautil.setDataDefault(inDto.getField("STL_NO"), ""));
			ydStockDao.getYdStock(recPara, outRecSet, 0);
			
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
			
			// 레코드셋을 앞으로 커서를 위치 시켜준다 .
			outRecSet.first();
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		szMsg = "JSP-SESSION [저장품 번호로 조회]끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return outRecSet;
	} //end of getStock
	
	

	/**
	 * 저장품 정보 수정 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */	
	
	public void updYdStock(JDTORecord[] inDto) throws DAOException {
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		YdStockDao ydStockDao = new YdStockDao();
		
		String szMethodName="updYdStock";		
		String szLogMsg = "";
		
		try{
			
			szLogMsg = "JSP-SESSION [ 저장품 정보 수정 ]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			for(int x=0;x<inDto.length;x++){
				recPara.setField("STL_NO", 			yddatautil.setDataDefault(inDto[x].getField("STL_NO"),			""));
				recPara.setField("YD_MTL_ITEM", 	yddatautil.setDataDefault(inDto[x].getField("YD_MTL_ITEM"),		""));
				recPara.setField("YD_STK_LOT_TP", 	yddatautil.setDataDefault(inDto[x].getField("YD_STK_LOT_TP"),	""));
				recPara.setField("YD_STK_LOT_CD", 	yddatautil.setDataDefault(inDto[x].getField("YD_STK_LOT_CD"),	""));
				recPara.setField("YD_AIM_RT_GP", 	yddatautil.setDataDefault(inDto[x].getField("YD_AIM_RT_GP"),	""));
				recPara.setField("YD_AIM_YD_GP", 	yddatautil.setDataDefault(inDto[x].getField("YD_AIM_YD_GP"),	""));
				recPara.setField("YD_AIM_BAY_GP", 	yddatautil.setDataDefault(inDto[x].getField("YD_AIM_BAY_GP"),	""));
				
				//PIDEV_S :병행가동용:PI_YD
				recPara.setField("PI_YD", 			yddatautil.setDataDefault(inDto[x].getField("PI_YD"),			"*"));
				ydStockDao.updYdStock(recPara, 0);
			}
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
		szLogMsg = "JSP-SESSION [ 저장품 정보 수정 ]끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
	}
	
	/**
	 * 슬라브 공통 수정 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */	
	
	public void updPtSlabCommFix(JDTORecord[] inDto) throws DAOException {
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		YdStockDao ydStockDao = new YdStockDao();
		
		String szMethodName="updPtSlabCommFix";		
		String szLogMsg = "";
		
		
		
		try{
			
			szLogMsg = "JSP-SESSION [ 슬라브 공통 수정 ]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			for(int x=0;x<inDto.length;x++){
				//Key 
				recPara.setField("SLAB_NO", 			yddatautil.setDataDefault(inDto[x].getField("SLAB_NO"),		""));
				
				
				//Column
				recPara.setField("PLNT_PROC_CD", 		yddatautil.setDataDefault(inDto[x].getField("PLNT_PROC_CD"),		""));
				recPara.setField("CC_PLNT_GP", 			yddatautil.setDataDefault(inDto[x].getField("CC_PLNT_GP"),			""));
				recPara.setField("RECORD_PROG_STAT", 	yddatautil.setDataDefault(inDto[x].getField("RECORD_PROG_STAT"),	""));
				recPara.setField("CURR_PROG_CD", 		yddatautil.setDataDefault(inDto[x].getField("CURR_PROG_CD"),		""));
				recPara.setField("ORD_YEOJAE_GP", 		yddatautil.setDataDefault(inDto[x].getField("ORD_YEOJAE_GP"),		""));
				recPara.setField("ORD_NO", 				yddatautil.setDataDefault(inDto[x].getField("ORD_NO"),				""));
				recPara.setField("ORD_DTL", 			yddatautil.setDataDefault(inDto[x].getField("ORD_DTL"),				""));
				recPara.setField("SLAB_T", 				yddatautil.setDataDefault(inDto[x].getField("SLAB_T"),				"0"));
				recPara.setField("SLAB_W", 				yddatautil.setDataDefault(inDto[x].getField("SLAB_W"),				"0"));
				recPara.setField("SLAB_LEN", 			yddatautil.setDataDefault(inDto[x].getField("SLAB_LEN"),			"0"));
				recPara.setField("SLAB_WT", 			yddatautil.setDataDefault(inDto[x].getField("SLAB_WT"),				"0"));
				recPara.setField("SCARFING_YN", 		yddatautil.setDataDefault(inDto[x].getField("SCARFING_YN"),			""));
				recPara.setField("HCR_GP", 				yddatautil.setDataDefault(inDto[x].getField("HCR_GP"),				""));
				recPara.setField("CCM_NO", 				yddatautil.setDataDefault(inDto[x].getField("CCM_NO"),				""));
				recPara.setField("STL_APPEAR_GP", 		yddatautil.setDataDefault(inDto[x].getField("STL_APPEAR_GP"),		""));
				recPara.setField("SLAB_WO_RT_CD",	 	yddatautil.setDataDefault(inDto[x].getField("SLAB_WO_RT_CD"),		""));
				recPara.setField("STACK_LOT_NO", 		yddatautil.setDataDefault(inDto[x].getField("STACK_LOT_NO"),		""));
				
				ydStockDao.updPtComm_FIX(recPara, 0);
			}
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
		szLogMsg = "JSP-SESSION [ 슬라브 공통 수정 ]끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
		
	}
	

	/**
	 * 주편 공통 수정 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */	
	
	public void updPtMSlabCommFix(JDTORecord[] inDto) throws DAOException {
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		YdStockDao ydStockDao = new YdStockDao();
		String szMethodName="updPtMSlabCommFix";		
		String szLogMsg = "";
		
		try{
			
			szLogMsg = "JSP-SESSION [ 주편 공통 수정 ]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			for(int x=0;x<inDto.length;x++){
				//Key 
				recPara.setField("MSLAB_NO", 			yddatautil.setDataDefault(inDto[x].getField("MSLAB_NO"),		""));
				
				//Column
		        recPara.setField("PLNT_PROC_CD",    yddatautil.setDataDefault(inDto[x].getField("PLNT_PROC_CD"),     ""));
		        recPara.setField("CC_PLNT_GP",      yddatautil.setDataDefault(inDto[x].getField("CC_PLNT_GP"),       ""));
		        recPara.setField("STL_APPEAR_GP",   yddatautil.setDataDefault(inDto[x].getField("STL_APPEAR_GP"),    ""));
		        recPara.setField("RECORD_PROG_STAT",yddatautil.setDataDefault(inDto[x].getField("RECORD_PROG_STAT"), ""));
		        recPara.setField("CURR_PROG_CD",    yddatautil.setDataDefault(inDto[x].getField("CURR_PROG_CD"),     ""));
		        recPara.setField("ORD_YEOJAE_GP",   yddatautil.setDataDefault(inDto[x].getField("ORD_YEOJAE_GP"),    ""));
		        recPara.setField("ORD_NO",          yddatautil.setDataDefault(inDto[x].getField("ORD_NO"),           ""));
		        recPara.setField("ORD_DTL",         yddatautil.setDataDefault(inDto[x].getField("ORD_DTL"),          ""));
		        recPara.setField("MSLAB_T",         yddatautil.setDataDefault(inDto[x].getField("MSLAB_T"),          "0"));
		        recPara.setField("MSLAB_W",         yddatautil.setDataDefault(inDto[x].getField("MSLAB_W"),          "0"));
		        recPara.setField("MSLAB_L",         yddatautil.setDataDefault(inDto[x].getField("MSLAB_L"),          "0"));
		        recPara.setField("MSLAB_WT",        yddatautil.setDataDefault(inDto[x].getField("MSLAB_WT"),         "0"));
		        recPara.setField("CC_CCM_NO",       yddatautil.setDataDefault(inDto[x].getField("CC_CCM_NO"),        ""));
		        recPara.setField("SLAB_WO_RT_CD",   yddatautil.setDataDefault(inDto[x].getField("SLAB_WO_RT_CD"),    ""));
		        recPara.setField("HCR_GP",          yddatautil.setDataDefault(inDto[x].getField("HCR_GP"),           ""));
		        recPara.setField("SCARFING_YN",     yddatautil.setDataDefault(inDto[x].getField("SCARFING_YN"),      ""));
		        recPara.setField("STACK_LOT_NO",    yddatautil.setDataDefault(inDto[x].getField("STACK_LOT_NO"),     ""));
				
				ydStockDao.updPtComm_FIX(recPara, 1);
			}
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
		szLogMsg = "JSP-SESSION [ 주편 공통 수정 ]끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
		
	}
	
	
	
	/**
	 * 후판 공통 수정 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	
	public void updPtPlateComm(JDTORecord[] inDto) throws DAOException {
		
		JspCommonDAO jspCommonDAO = new JspCommonDAO();
		String szMethodName="updPtPlateComm";		
		String szLogMsg = "";
		try{
			szLogMsg = "JSP-SESSION [ 후판 공통 수정 ]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			
			for(int x=0;x<inDto.length;x++){
				jspCommonDAO.updPtPlateComm(inDto[x]);
			}
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
		szLogMsg = "JSP-SESSION [후판 공통 수정 ] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
		
	}
	
	/**
	 * 코일 공통 수정 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	
	public void updPtCoilComm(JDTORecord[] inDto) throws DAOException {
		
		JspCommonDAO jspCommonDAO = new JspCommonDAO();
		String szMethodName="";		
		String szLogMsg = "";
		
		
		try{
			
			szLogMsg = "JSP-SESSION [코일 공통 수정]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			for(int x=0;x<inDto.length;x++){
				jspCommonDAO.updPtCoilComm(inDto[x]);
			}
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
		szLogMsg = "JSP-SESSION [코일 공통 수정]끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
	}
	
	
	/**
	 * 크레인상태관리 - 명령선택기동
	 * 전문 ID : C연주 : YDYDJ640
	 *  
	 * Input  : YD_EQP_ID			: 설비 ID 
	 *          YD_WRK_PROG_STAT 	: 작업진행상태
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return String
	 * @throws DAOException
	 */
	public String updCmdSelStart(JDTORecord[] inDto) throws DAOException {
		JDTORecord recPara = null;
		String szYdGp = null;
		YdDelegate ydDelegate = new YdDelegate();
		String szJMS_TC_CD = null;
		String szEjbMethod = null;
		EJBConnector ejbConn = null;
		String szLogMsg = null;
		String szRtnMsg = null;
		String szMethodName = "updCmdSelStart";
		String szOperationName = "명령선택기동";
		String szYD_EQP_ID = null;
		try{
			
			//for(int x=0;x<inDto.length;x++){
				
			
			szLogMsg = "JSP-SESSION [크레인상태관리 - 명령선택기동]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			
			
				szYdGp = inDto[0].getFieldString("YD_GP");			
				recPara = JDTORecordFactory.getInstance().create();
				
				szLogMsg = "[JSP Session]명령선택기동  - 야드구분[" + szYdGp + "]";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				if (YdConstant.YD_GP_C_SLAB_YARD.equals(szYdGp) || YdConstant.YD_GP_INTGR_YARD.equals(szYdGp) ){						//C연주 슬라브 야드 [A], 통합야드[S]
					szJMS_TC_CD = "YDYDJ640";
					szEjbMethod = "procY1CrnWrkOrdReq";
				}else if(YdConstant.YD_GP_PORT_SLAB_YARD.equals(szYdGp)){			//항만슬라브야드 기능추가 - 2015.12.30 LeeJY
					szJMS_TC_CD = "YDYDJ640";
					szEjbMethod = "procY1CrnWrkOrdReq";
				}else if(YdConstant.YD_GP_A_PLATE_SLAB_YARD.equals(szYdGp)){			//A후판 슬라브야드[D]
					//recPara.setField("JMS_TC_CD","YDYDJ641");
					szJMS_TC_CD = "YDYDJ641";
					szEjbMethod = "procY3CrnWrkOrdReq";
				}else if(YdConstant.YD_GP_PLATE_GDS_YARD.equals(szYdGp)){				//후판제품야드 [K]
					//recPara.setField("JMS_TC_CD","YDYDJ642");
					szJMS_TC_CD = "YDYDJ642";
					szEjbMethod = "procY4CrnWrkOrdReq";
				}else if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYdGp)){				//2후판제품야드 [T] - 2013.01.04 추가 (3기)
					szJMS_TC_CD = "YDYDJ642";
					szEjbMethod = "procY4CrnWrkOrdReq";					
				}else if(YdConstant.YD_GP_C_HR_COIL_MATL_YARD.equals(szYdGp)){			//C열연 코일야드[H]
					//recPara.setField("JMS_TC_CD","YDYDJ643");
					szJMS_TC_CD = "YDYDJ643";
					szEjbMethod = "procY5CrnWrkOrdReq";
				}else if(YdConstant.YD_GP_C_HR_COIL_GDS_YARD.equals(szYdGp)){			//C열연 제품야드[J]
					//recPara.setField("JMS_TC_CD","YDYDJ643");
					szJMS_TC_CD = "YDYDJ643";
					szEjbMethod = "procY5CrnWrkOrdReq";
				}else if(YdConstant.YD_GP_INTGR_YARD.equals(szYdGp)){					//통합제품야드[S]
					//recPara.setField("JMS_TC_CD","YDYDJ643");
					szJMS_TC_CD = "YDYDJ644";
					szEjbMethod = "procY0CrnWrkOrdReq";
				} 
				
				szYD_EQP_ID = inDto[0].getFieldString("YD_EQP_ID");
				
				szLogMsg = "[JSP Session]명령선택기동  - 야드구분[" + szYdGp + "], 크레인 설비[" + szYD_EQP_ID + "]";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
//SJH03004				
				recPara.setField("JMS_TC_CD", 			szJMS_TC_CD);
				
				recPara.setField("YD_EQP_ID" , 			szYD_EQP_ID);
				recPara.setField("YD_WRK_PROG_STAT" , 	"W");
				
				     
				ydUtils.displayRecord(szOperationName, recPara);
				
				//JMS Call
				//ydDelegate.sendMsg(recPara);
				
				//EJB Method Call

				if( szEjbMethod.equals("procY5CrnWrkOrdReq") ) {
					ejbConn = new EJBConnector("default", "CoilCraneLdHdSeEJB", this);
					recPara.setField("YD_CMD_CHK" , 	"Y");
				} else{
					
					ejbConn = new EJBConnector("default", "CraneLdHdSeEJB", this);			
				}
				szRtnMsg = (String)ejbConn.trx(szEjbMethod, new Class[] { JDTORecord.class }, new Object[] { recPara });

//				ydDelegate.sendMsg(recPara);				
				
				if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {													//성공
					szLogMsg = "[JSP Session]명령선택기동 성공 - 크레인 작업지시";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}else if( szRtnMsg.equals(YdConstant.RETN_CRN_SCH_PROH) ) {											//스케줄금지 상태
					szLogMsg = "[JSP Session]명령선택기동 성공 - 크레인 작업지시 : 스케줄금지 상태";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}else if( szRtnMsg.equals(YdConstant.RETN_CD_TC_ERROR) ) {											//전문 에러
					szLogMsg = "[JSP Session]명령선택기동 성공 - 크레인 작업지시 : 전문 에러";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}else if( szRtnMsg.equals(YdConstant.RETN_CRN_NO_SCH) ) {											//크레인스케쥴이 존재하지 않음
					szLogMsg = "[JSP Session]명령선택기동 성공 - 크레인 작업지시 : 크레인스케쥴이 존재하지 않음";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}else if( szRtnMsg.equals(YdConstant.RETN_CRN_NO_WRK) ) {											//작업예약이 더 이상 존재하지 않음
					szLogMsg = "[JSP Session]명령선택기동 성공 - 크레인 작업지시 : 작업예약이 더 이상 존재하지 않음";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}else if( szRtnMsg.equals(YdConstant.RETN_CD_FAILURE) ) {											//실패
					szLogMsg = "[JSP Session]명령선택기동 실패 - 크레인 작업지시 실패";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				}
				
				
			//}
		}catch(Exception e){
			szLogMsg = "[JSP Session]명령선택기동 에러발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
		szLogMsg = "JSP-SESSION [크레인상태관리 - 명령선택기동] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
		
		return szRtnMsg;
	}
	
	
	/**
	 * 크레인 우선 순위 변경
	 * Input  : YD_CRN_SCH_ID : 스케줄 ID
	 *          CRN_PRIOR : 크레인 우선순위 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public Boolean crnChgSchPrior(JDTORecord [] recMsg) throws DAOException {
		JDTORecord recPara = null;
		JDTORecord recTemp = null;
		int intSchPrior = 0;
		boolean bool = false;
		
		String szMethodName="crnChgSchPrior";		
		String szLogMsg = "";
		
		
		
    	JDTORecordSet rsrstDataSch =  JDTORecordFactory.getInstance().createRecordSet("YD");
    	
    	int intGp = 0;

    	YdCrnSchDao  ydCrnSchDao  = new YdCrnSchDao ();
    	YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
    	
		try{
			szLogMsg = "JSP-SESSION [크레인 우선 순위 변경]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			
			for(int x=0 ; x < recMsg.length ;x++){
				recPara = JDTORecordFactory.getInstance().create();
	
				// 1.  작업예약 ID ,크레인 ID , 입력받은 스케줄 우선순위
				recPara.setField("YD_WBOOK_ID" 	, recMsg[x].getField("YD_WBOOK_ID"));
				recPara.setField("YD_SCH_PRIOR" , recMsg[x].getField("YD_SCH_PRIOR"));
				intSchPrior = recMsg[x].getFieldInt("YD_SCH_PRIOR");	
				
				// 2. 작업 예약 정보 변경
				ydWrkbookDao.updYdWrkbook(recPara, 0);
				
				// 3. 작업예약에 편성된 스케줄정보 조회	
				
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID" 	, recMsg[x].getField("YD_WBOOK_ID"));
				recPara.setField("YD_WRK_PROG_STAT" ,"W");					
				
				intGp = ydCrnSchDao.getYdCrnsch(recPara, rsrstDataSch, 23);
				
				if (intGp <1 ){					
					//해당 정보가 없을경우는 미처리 
					throw new DAOException();
				}	
				
				//크레인 스케줄 정보 변경
				rsrstDataSch.first();
				do
				{
					
					// 3. 스케쿨 ID 에  입력받은 크레인 우선순위를 편성시킨다.
					
					recTemp = JDTORecordFactory.getInstance().create();
					recPara = JDTORecordFactory.getInstance().create();
					
					recTemp = rsrstDataSch.getRecord();
					
					recPara.setField("YD_CRN_SCH_ID", recTemp.getField("YD_CRN_SCH_ID"));
					recPara.setField("YD_SCH_PRIOR", new Integer(intSchPrior));
					
					// 4. 스케줄 테이블에 UPDATE 
					ydCrnSchDao.updYdCrnsch(recPara, 0);
					
				}while(rsrstDataSch.next());		
				
		}
			
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
		bool = true;
		
		szLogMsg = "JSP-SESSION [크레인 우선 순위 변경] 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
		
		return new Boolean(bool);
		
	}
	
	
	/**
	 * (작업)크레인 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return Boolean
	 * @throws DAOException
	 */
	public Boolean wrkCrnChg(JDTORecord[] recMsg) throws DAOException {
		JDTORecord recPara = null;
		JDTORecord recTemp = null;	
		JDTORecord recTemp2 = null;
	 	JDTORecordSet rsrstDataSch =  null;
	 	boolean bool = false;
	 	int intGp = 0;

    	YdCrnSchDao  ydCrnSchDao 	= new YdCrnSchDao ();
    	YdWrkbookDao ydWrkbookDao	= new YdWrkbookDao();
    	YdSchRuleDao ydSchRuleDao 	= new YdSchRuleDao();
    	
    	
    	String szWbookId			= null;
    	String szWrkCrn 			= null;
    	String szAltCrn				= null;
    	String szChgCrn 			= null;
    	String szEqpId				= null;
    	
    	int    intWrkCrnPrior		= 0;
    	int    intAltCrnPrior 		= 0;
    	int    intCrnPrior			= 0;
    	
    	String szMethodName="";		
    	String szLogMsg = "";
    	
    	String szYD_GP				= "";
	 	
	 	
		try{

			szLogMsg = "JSP-SESSION [(작업)크레인 변경 ]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			recTemp2 = JDTORecordFactory.getInstance().create();
			
			for(int x=0 ; x < recMsg.length ;x++){
				recPara = JDTORecordFactory.getInstance().create();
				
				szYD_GP	= recMsg[x].getFieldString("YD_SCH_CD").substring(0, 1);
				
			// 1. 선택된 크레인 스케줄 ID로 편성된 스케줄 기준에서 대체 크레인 설비 ID 와 우선순위를 가지고 온다.
				recPara.setField("YD_SCH_CD", recMsg[x].getField("YD_SCH_CD"));
				szWbookId =  recMsg[x].getFieldString("YD_WBOOK_ID");
				szEqpId = recMsg[x].getFieldString("YD_EQP_ID");
				
				rsrstDataSch =  JDTORecordFactory.getInstance().createRecordSet("YD");
				
				intGp = ydSchRuleDao.getYdSchrule(recPara, rsrstDataSch, 0);
				
				if (intGp < 1 ){
					//선택된 스케줄 기준데이터 정보에 해당 스케줄 정보가 없을경우
					throw new DAOException("선택된 스케줄 기준데이터 정보에 해당 스케줄 정보가 없음");
					
				}
				
				
				// 데이터가 실제로 1건 존재함
				recTemp = JDTORecordFactory.getInstance().create();
				rsrstDataSch.first();
				do{					
					recTemp = rsrstDataSch.getRecord();					
					
				}while(rsrstDataSch.next());
				
				szWrkCrn	   = recTemp.getFieldString("YD_WRK_CRN");
				szAltCrn	   = recTemp.getFieldString("YD_ALT_CRN");
				intWrkCrnPrior = recTemp.getFieldInt("YD_WRK_CRN_PRIOR");
				intAltCrnPrior = recTemp.getFieldInt("YD_ALT_CRN_PRIOR");
				
				
			// 1-1 크레인 ID를 비교하여 주작업 크레인인 경우는 대체작업 크레인과 순위를
		    //     그렇지 않은 경우는 주작업 크레인과 순위를 변경 크레인 정보와 순위를 SETTING 한다.
				if (szEqpId.equals(szWrkCrn)){
					szChgCrn = szAltCrn;
					intCrnPrior = intAltCrnPrior;
					
				}else {
					szChgCrn = szWrkCrn;
					intCrnPrior = intWrkCrnPrior;
					
				}
				
				
			// 2. 선택된 크레인 스케줄 작업예약 ID에  대체 설비 ID 와 우선순위 를 UPDATE 한다.
			
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID",szWbookId);
				recPara.setField("YD_SCH_PRIOR", new Integer(intCrnPrior));
				recPara.setField("MODIFIER",recMsg[x].getField("MODIFIER"));
				intGp = ydWrkbookDao.updYdWrkbook(recPara, 0);
				if (intGp <1){
					throw new DAOException("선택된 크레인 스케줄 작업예약 ID : "+szWbookId+"에   우선순위 ("+intCrnPrior+") 를 UPDATE 중 ERROR");
				}
				
				
				
			// 3. 작업예약 ID 로 현재 편성된 스케줄 ID [] 를 구한다.
				
				recPara = JDTORecordFactory.getInstance().create();
				rsrstDataSch =  JDTORecordFactory.getInstance().createRecordSet("YD");
				
				recPara.setField("YD_WBOOK_ID",szWbookId);
				recPara.setField("YD_WRK_PROG_STAT","W");
				
				intGp = ydCrnSchDao.getYdCrnsch(recPara, rsrstDataSch, 23);
				if (intGp <1 ){					
					// 해당 작업 ID 에 편성된 스케줄 정보가 없을경우  
					throw new DAOException("해당작업 ID 에 편성된 스케줄 정보가 존재하지 않음");
				}	
				//크레인 스케줄 정보 변경
				rsrstDataSch.first();
				do
				{	
					// 4. 편성된 스케줄 ID [] 에  대체 크레인 설비 ID와 우선순위를 편성
					recTemp = JDTORecordFactory.getInstance().create();
					recPara = JDTORecordFactory.getInstance().create();
					
					recTemp = rsrstDataSch.getRecord();
					
					recPara.setField("YD_CRN_SCH_ID", recTemp.getField("YD_CRN_SCH_ID"));
					recPara.setField("YD_EQP_ID",szChgCrn);					
					recPara.setField("YD_SCH_PRIOR",  new Integer(intCrnPrior));
					recPara.setField("MODIFIER",recMsg[x].getField("MODIFIER"));
					
					// 5. 스케줄 테이블에 UPDATE 
					intGp = ydCrnSchDao.updYdCrnsch(recPara, 0);
					if (intGp <1){
						throw new DAOException("스케줄 테이블에 UPDATE 중 ERROR");
					}
					//throw new DAOException("스케줄 테이블에 UPDATE 강제 RollBack");
					
					//-------------------------------------------------------------------------------------------------------------
					//	크레인 허용 오차 및 크레인 X, Y좌표 계산 - 임춘수 2009.11.26
					//-------------------------------------------------------------------------------------------------------------
					if( szYD_GP.equals(YdConstant.YD_GP_PLATE_GDS_YARD) ) {
						szLogMsg ="크레인 변경 후 제원 위치정보 세팅 호출";
		        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		        		
		        		recTemp2.setField("YD_CRN_SCH_ID", recTemp.getField("YD_CRN_SCH_ID"));
						YdUtils.updYdCrnschBedData(recTemp2);
				
						szLogMsg ="크레인 스케줄 변경 후 제원 위치정보 세팅 완료";
		        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					}
					//-------------------------------------------------------------------------------------------------------------
					
				}while(rsrstDataSch.next());						
			}
		}catch(DAOException e){
			ydUtils.putLog("YdJspCommonSeEJB", "wrkCrnChg", e.getMessage(), YdConstant.ERROR);
			
			throw e;
			
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
			
		}
		
		bool = true;
		
		szLogMsg = "JSP-SESSION [(작업)크레인 변경 ]끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
		
		
		return new Boolean(bool);
		
		//추가적 로직적으로 실패 하는부분이 생기면 false 값을 리턴시켜준다 
		
	}
	
	
	/**
	 * (작업)크레인 변경 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return Boolean
	 * @throws DAOException
	 */
	public String  wrkCrnChange(JDTORecord[] recMsg) throws DAOException {
		
		JDTORecord recPara 			= null;
		JDTORecord recTemp 			= null;	
		JDTORecord recTemp2 		= null;
		JDTORecord recEqpInfo 		= null;
		JDTORecord recSchInfo 		= null;
		JDTORecord recDelPara 		= null;
		
	 	JDTORecordSet rsrstDataSch 	= null;
	 	JDTORecordSet rsEqpInfo 	= null;
	 	JDTORecordSet rsSchInfo 	= null;
	 	
    	YdCrnSchDao  ydCrnSchDao 	= new YdCrnSchDao ();
    	YdWrkbookDao ydWrkbookDao	= new YdWrkbookDao();
    	YdSchRuleDao ydSchRuleDao 	= new YdSchRuleDao();
    	YdEqpDao ydEqpDao 			= new YdEqpDao();
    	YdDelegate ydDelegate 		= new YdDelegate();
    	
    	String szWbookId			= null;
    	String szWrkCrn 			= null;
    	String szAltCrn				= null;
    	String szChgCrn 			= null;
    	String szEqpId				= null;
    	
    	int intGp 			= 0;
    	int intWrkCrnPrior	= 0;
    	int intAltCrnPrior 	= 0;
    	int intCrnPrior		= 0;
    	
    	String szMethodName		= "wrkCrnChange";		
    	String szOperationName 	= "(작업)크레인 변경";
    	String szLogMsg 		= "";
    	
    	String szYD_GP			= "";
    	String szRtnValue 		= YdConstant.RETN_CD_SUCCESS;
    	
    	String szJMS_TC_CD 		= null;
    	String szWrkProgStat 	= "";
    	
    	boolean sbSendFlag ;
    	
    	EJBConnector ejbConn = null;
	 	
		try{

			szLogMsg = "JSP-SESSION [" + szOperationName + " ]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			recTemp2 = JDTORecordFactory.getInstance().create();
			
			for(int x=0 ; x < recMsg.length ;x++){
				sbSendFlag = false; 
				
				szYD_GP	= recMsg[x].getFieldString("YD_SCH_CD").substring(0, 1);
				 
				//설비 ID
				szEqpId = recMsg[x].getFieldString("YD_EQP_ID");
				
		
				/*
				 * 1. 체크 선택 된 크레인 스케줄 정보를 가지고 온다.	
				 */
				rsrstDataSch =  JDTORecordFactory.getInstance().createRecordSet("YD");
				recTemp = JDTORecordFactory.getInstance().create();
				recPara = JDTORecordFactory.getInstance().create();
				
				recPara.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(recMsg[x], "YD_CRN_SCH_ID"));
				
				intGp = ydCrnSchDao.getYdCrnsch(recPara, rsrstDataSch, 0);
				
				if(intGp < 1 ){
					// 스케줄 정보가 존재 하지 않을 경우
					szRtnValue = "스케줄 [ " +ydDaoUtils.paraRecChkNull(recMsg[x], "YD_CRN_SCH_ID")  + "정보가 존재 하지 않습니다.";
					return szRtnValue ;
				}
				
				rsrstDataSch.first();
				recTemp = rsrstDataSch.getRecord();
				
				//설비 상태
				szWrkProgStat	= ydDaoUtils.paraRecChkNull(recTemp, "YD_WRK_PROG_STAT");
				//String szYD_DN_WO_LOC 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_DN_WO_LOC");
				//작업예약 ID
				szWbookId 		= ydDaoUtils.paraRecChkNull(recTemp, "YD_WBOOK_ID");
				
				if(szWrkProgStat.equals(YdConstant.YD_EQP_STAT_UP_CMPL)){
					szRtnValue = "스케줄 ["+  ydDaoUtils.paraRecChkNull(recMsg[x], "YD_CRN_SCH_ID") +"]가 권상완료 상태에서는 상태를 변경 할 수 없습니다.";
					return szRtnValue ;
				}else if(szWrkProgStat.equals(YdConstant.YD_EQP_STAT_DN_WO)){
					szRtnValue = "스케줄 ["+  ydDaoUtils.paraRecChkNull(recMsg[x], "YD_CRN_SCH_ID") +"]가 권하지시 상태에서는 상태를 변경 할 수 없습니다.";
					return szRtnValue ;
				}else if(szWrkProgStat.equals(YdConstant.YD_EQP_STAT_DN_CMPL)){
					szRtnValue = "스케줄 ["+  ydDaoUtils.paraRecChkNull(recMsg[x], "YD_CRN_SCH_ID") +"]가 권하완료 상태에서는 상태를 변경 할 수 없습니다.";
					return szRtnValue ;
				}
				
				/*
				 *  2. 선택된 크레인 스케줄 ID로 편성된 스케줄 기준에서 대체 크레인 설비 ID 와 우선순위를 가지고 온다.
				 */
				
				if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYD_GP)) {
					//1,2후판 제품창고일경우..
					//통합스케줄 사용일경우
					szChgCrn 		= recMsg[x].getFieldString("MOD_EQP_ID");
					intCrnPrior		= recMsg[x].getFieldInt("YD_SCH_PRIOR");
					
				} else {
					
					//기존  TB_YD_SCHRULE 테이블 조회
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_SCH_CD", recMsg[x].getField("YD_SCH_CD"));
						
					rsrstDataSch =  JDTORecordFactory.getInstance().createRecordSet("YD");
					
					intGp = ydSchRuleDao.getYdSchrule(recPara, rsrstDataSch, 0);
					
					if (intGp < 1 ){
						//선택된 스케줄 기준데이터 정보에 해당 스케줄 정보가 없을경우
						throw new DAOException("선택된 스케줄 기준데이터 정보에 해당 스케줄 정보가 없음");
					}
					
					// 데이터가 실제로 1건 존재함
					recTemp = JDTORecordFactory.getInstance().create();
					rsrstDataSch.first();
					
					do{					
						recTemp = rsrstDataSch.getRecord();					
						
					}while(rsrstDataSch.next());
					
					szWrkCrn	   = recTemp.getFieldString("YD_WRK_CRN");
					szAltCrn	   = recTemp.getFieldString("YD_ALT_CRN");
					intWrkCrnPrior = recTemp.getFieldInt("YD_WRK_CRN_PRIOR");
					intAltCrnPrior = recTemp.getFieldInt("YD_ALT_CRN_PRIOR");
					
					
					/*
					 * 2-1 크레인 ID를 비교하여 주작업 크레인인 경우는 대체작업 크레인과 순위를
					 *    그렇지 않은 경우는 주작업 크레인과 순위를 변경 크레인 정보와 순위를 SETTING 한다.
					 */    
					if (szEqpId.equals(szWrkCrn)){
						
						szChgCrn 	= szAltCrn;
						intCrnPrior = intAltCrnPrior;
						
					}else {
						
						szChgCrn 	= szWrkCrn;
						intCrnPrior = intWrkCrnPrior;
					}
					
				}
				
				/*
				 * 2-2 변경 할 크레인이 선택되어 있으면 설비 정보를 조회한 후 고장 또는 OFF-LINE 일 경우 변경 할 수 없다고 판단하고 RETURN 한다.
				 */
				recEqpInfo = JDTORecordFactory.getInstance().create();
				rsEqpInfo = JDTORecordFactory.getInstance().createRecordSet("YD");
				recEqpInfo.setField("YD_EQP_ID", szChgCrn);
				
				//해당 설비  szChgCrn 로 설비 정보 조회 
				intGp = ydEqpDao.getYdEqp(recEqpInfo, rsEqpInfo, 0);
				
				if(intGp > 0 ){
				
					rsEqpInfo.first();
					recEqpInfo = rsEqpInfo.getRecord();
					
					if(ydDaoUtils.paraRecChkNull(recEqpInfo, "YD_EQP_STAT").equals(YdConstant.YD_EQP_STAT_BREAK)){
						// 설비 상태가 고장일 경우 
						szRtnValue = "변경 설비["+ szChgCrn+"]가 고장 상태여서 상태를 변경 할 수 없습니다.";
						return szRtnValue ;
					}
					
					if(ydDaoUtils.paraRecChkNull(recEqpInfo, "YD_EQP_WRK_MODE").equals(YdConstant.YD_EQP_WRK_MODE_OFF_LINE)){
						// 설비 상태가 OFF_LINE 일 경우 
						szRtnValue = "변경 설비["+ szChgCrn+"]가 OFF_LINE 이기때문에 상태를 변경 할 수 없습니다.";
						return szRtnValue ;
					}
					
					if( ydDaoUtils.paraRecChkNull(recEqpInfo, "YD_EQP_STAT").equals(YdConstant.YD_EQP_STAT_UP_WO)|| 
						ydDaoUtils.paraRecChkNull(recEqpInfo, "YD_EQP_STAT").equals(YdConstant.YD_EQP_STAT_UP_CMPL)|| 
						ydDaoUtils.paraRecChkNull(recEqpInfo, "YD_EQP_STAT").equals(YdConstant.YD_EQP_STAT_DN_WO)|| 
						ydDaoUtils.paraRecChkNull(recEqpInfo, "YD_EQP_STAT").equals(YdConstant.YD_EQP_STAT_DN_CMPL)){
						szRtnValue = "변경 설비["+ szChgCrn+"]가 작업지시기 내려가 있기때문에 상태를 변경 할 수 없습니다.";						
						return szRtnValue ;
					}
					
					// 2009.12.07 [작업지시 취소 전문은 발생하지 않는다 - 작업재지시 전문만 발생시켜준다]
					// 선택된 정보가 선택된 정보일 경우 작업지시 취소 정보를 송신한다.
					recSchInfo =  JDTORecordFactory.getInstance().create();
					rsSchInfo = JDTORecordFactory.getInstance().createRecordSet("YD");
					
					recSchInfo.setField("YD_CRN_SCH_ID", recMsg[x].getField("YD_CRN_SCH_ID") );
					
					intGp = ydCrnSchDao.getYdCrnsch(recSchInfo, rsSchInfo, 0);
					
					if(intGp > 0 ){
						
						rsSchInfo.first();
						recSchInfo = rsSchInfo.getRecord();
						
						// 현 작업 상태가 선택인 경우는 작업재지시 전문을 전송하기위채 체크해놓는다.
						if(ydDaoUtils.paraRecChkNull(recSchInfo, "YD_WRK_PROG_STAT").equals(YdConstant.YD_EQP_STAT_UP_WO)){
								//작업재지시 전문을 전송하기 위하여 Flag Setting
								sbSendFlag = true;
							}
						}
					
				}else{
					//해당 설비가 존재 하지 않습니다.
					szRtnValue = "해당 설비["+ szChgCrn+"]가 존재 하지 않습니다";
					return szRtnValue;
				}
				
				
				/*
				 * 3. 선택된 크레인 스케줄 작업예약 ID에  대체 설비 ID 와 우선순위 를 UPDATE 한다.
				 */
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID",szWbookId);
				recPara.setField("YD_SCH_PRIOR", new Integer(intCrnPrior));
				recPara.setField("MODIFIER",recMsg[x].getField("MODIFIER"));
				intGp = ydWrkbookDao.updYdWrkbook(recPara, 0);
				if (intGp <1){
					throw new DAOException("선택된 크레인 스케줄 작업예약 ID : "+szWbookId+"에   우선순위 ("+intCrnPrior+") 를 UPDATE 중 ERROR");
				}
				
				/*
				 * 4. 작업예약 ID 로 현재 편성된 스케줄 ID [] 를 구한다.
				 */
				
				recPara = JDTORecordFactory.getInstance().create();
				rsrstDataSch =  JDTORecordFactory.getInstance().createRecordSet("YD");
				
				recPara.setField("YD_WBOOK_ID",szWbookId);
				recPara.setField("YD_WRK_PROG_STAT","W");
				
				// 기존쿼리는 W 이상태만 체크하였으나 지금은 1,W 상태를 조회한다.
				intGp = ydCrnSchDao.getYdCrnsch(recPara, rsrstDataSch, 23);
				
				if (intGp <1 ){					
					// 해당 작업 ID 에 편성된 스케줄 정보가 없을경우  
					throw new DAOException("해당작업 ID 에 편성된 스케줄 정보가 존재하지 않음");
				}	
				
				//크레인 스케줄 정보 변경
				rsrstDataSch.first();
				
				do
				{	
					/*
					 * 5. 편성된 스케줄 ID [] 에  대체 크레인 설비 ID와 우선순위를 편성
					 */
					recTemp = JDTORecordFactory.getInstance().create();
					recPara = JDTORecordFactory.getInstance().create();
					
					recTemp = rsrstDataSch.getRecord();
					
					recPara.setField("YD_CRN_SCH_ID", recTemp.getField("YD_CRN_SCH_ID"));
					recPara.setField("YD_EQP_ID",szChgCrn);					
					recPara.setField("YD_SCH_PRIOR",  new Integer(intCrnPrior));
					recPara.setField("MODIFIER",recMsg[x].getField("MODIFIER"));
					
					// 5. 스케줄 테이블에 UPDATE 
					intGp = ydCrnSchDao.updYdCrnsch(recPara, 0);
					if (intGp <1){
						throw new DAOException("스케줄 테이블에 UPDATE 중 ERROR");
					}
					
					//-------------------------------------------------------------------------------------------------------------
					//	크레인 허용 오차 및 크레인 X, Y좌표 계산 - 임춘수 2009.11.26
					//-------------------------------------------------------------------------------------------------------------
					if( YdConstant.YD_GP_PLATE_GDS_YARD.equals(szYD_GP) 
							|| YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYD_GP)) { //- 2012.12.28 수정 (3기)
						
						szLogMsg ="크레인 변경 후 제원 위치정보 세팅 호출";
		        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		        		
		        		recTemp2.setField("YD_CRN_SCH_ID", recTemp.getField("YD_CRN_SCH_ID"));
						YdUtils.updYdCrnschBedData(recTemp2);
				
						szLogMsg ="크레인 스케줄 변경 후 제원 위치정보 세팅 완료";
		        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					}
					//-------------------------------------------------------------------------------------------------------------
					
				}while(rsrstDataSch.next());			
				
				/*
				 * 6. 작업 재지시 정보를 호출하여준다.
				 */
				
				//sbSendFlag 가 True 라는것은 변경전 스케줄 상태가 작업선택상태임을 나타낸다.
				if(sbSendFlag){
					
					//작업 취소전문이 발생 후 크레인 작업지시 (2009.10.28 요청사항)
					//다음작업이 선택될 수 있도록 하기 위함
					/*  YDYDJ640 procY1CrnWrkOrdReq - C연주 슬라브야드
						YDYDJ641 procY3CrnWrkOrdReq - A후판 슬라브야드 
						YDYDJ642 procY4CrnWrkOrdReq - 제품창고  
						YDYDJ643 procY5CrnWrkOrdReq - C열연 
						YDYDJ644 procY0CrnWrkOrdReq - 통합슬라브 야드
					 */
				 	//변경할 크레인 원 크레인 정보는 선택상태로 세팅을 바꾸어준다.
					
					szLogMsg = "JSP-SESSION [" + szOperationName + " ] 선택된 크레인이 변경될 경우 크레인 변경될 크레인 상태를  선택상태로 변경한다.";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);	
					
				 	recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_EQP_ID", szChgCrn);
					recPara.setField("YD_EQP_STAT", YdConstant.YD_EQP_STAT_UP_WO);
					recPara.setField("MODIFIER",recMsg[x].getField("MODIFIER"));
					
					szLogMsg = "JSP-SESSION [" + szOperationName + " ] 설비 정보 UPDATE";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);	
					
					intGp = ydEqpDao.updYdEqp(recPara, 0);
										
					String szEjbConName = "";
					
					szLogMsg = "[JSP Session] "+szOperationName +": 크레인 작업지시   : 야드구분[" + szYD_GP + "]";  // 야드구분은 스케줄 코드앞자리에서 발생되었다. 
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					if (YdConstant.YD_GP_C_SLAB_YARD.equals(szYD_GP)  ){						//C연주 슬라브 야드 [A]
						szJMS_TC_CD = "YDYDJ640";
						szEjbConName = "procY1CrnWrkOrdReq";
						
					}else if(YdConstant.YD_GP_PORT_SLAB_YARD.equals(szYD_GP)){				//항만슬라브야드 기능추가 - 2015.12.30 LeeJY
						szJMS_TC_CD = "YDYDJ640";
						szEjbConName = "procY1CrnWrkOrdReq";
					}else if(YdConstant.YD_GP_A_PLATE_SLAB_YARD.equals(szYD_GP)){				//A후판 슬라브야드[D]
						
						szJMS_TC_CD = "YDYDJ641";
						szEjbConName = "procY3CrnWrkOrdReq";
					
					}else if(YdConstant.YD_GP_PLATE_GDS_YARD.equals(szYD_GP) 
							|| YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYD_GP)){				//후판제품야드 [K],2후판제품야드[T] - 2012.12.28 수정 (3기)
						
						szJMS_TC_CD = "YDYDJ642";
						szEjbConName = "procY4CrnWrkOrdReq";
						
					}else if(YdConstant.YD_GP_C_HR_COIL_MATL_YARD.equals(szYD_GP)){				//C열연 코일야드[H]
					
						szJMS_TC_CD = "YDYDJ643";
						szEjbConName = "procY5CrnWrkOrdReq";
						
						
					}else if(YdConstant.YD_GP_C_HR_COIL_GDS_YARD.equals(szYD_GP)){				//C열연 제품야드[J]
						
						szJMS_TC_CD = "YDYDJ643";
						szEjbConName = "procY5CrnWrkOrdReq";
						
					} 	else if(YdConstant.YD_GP_INTGR_YARD.equals(szYD_GP)){					//통합제품야드[S]
						
						szJMS_TC_CD = "YDYDJ644";
						szEjbConName = "procY0CrnWrkOrdReq";
					} 
					
					
					//JMS => EJB CALL 형식으로 수정요청 
					
					
					recDelPara   = JDTORecordFactory.getInstance().create();
//SJH03004					
					recDelPara.setField("MSG_ID",       szJMS_TC_CD        );
					recDelPara.setField("YD_EQP_ID",    szChgCrn            );
					recDelPara.setField("YD_WRK_PROG_STAT",YdConstant.YD_EQP_STAT_UP_WO );
					recDelPara.setField("YD_SCH_CD", recMsg[x].getField("YD_SCH_CD") );
										
					ejbConn = new EJBConnector("default", this);	
					if( szEjbConName.equals("procY5CrnWrkOrdReq") ) {
						ejbConn.trx("CoilCraneLdHdSeEJB", szEjbConName, recDelPara);		
					} else{
						ejbConn.trx("CraneLdHdSeEJB", szEjbConName, recDelPara);		
					}
				 	
					//ydDelegate.sendMsg(recDelPara);
					
				 	//크레인 변경 후 원 크레인 정보는 작업대기 상태로 세팅을 바꾸어준다.
				 	szLogMsg = "JSP-SESSION [" + szOperationName + " ] 크레인 변경 후 전 크레인 정보 설비상태는 작업대기 상태로 바꾸어준다.";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);	
					
				 	recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_EQP_ID",szEqpId);
					recPara.setField("YD_EQP_STAT", YdConstant.YD_EQP_STAT_IDLE);
					recPara.setField("MODIFIER",recMsg[x].getField("MODIFIER"));
					
					intGp = ydEqpDao.updYdEqp(recPara, 0);
					
					//원크레인 정보에대하여 작업실적 응답전문을 전송하여 준다.(2009.12.15)
					szLogMsg = "[JSP Session] 크레인작업실적 응답전송   : 야드구분[" + szYD_GP + "]";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					szJMS_TC_CD = "";
					if (YdConstant.YD_GP_C_SLAB_YARD.equals(szYD_GP)  ){					//C연주 슬라브 야드 [A]
						szJMS_TC_CD = "YDY1L005";
						
					}else if(YdConstant.YD_GP_PORT_SLAB_YARD.equals(szYD_GP)){			//항만슬라브야드 기능추가 - 2015.12.30 LeeJY
						szJMS_TC_CD = "YDE7L005";
						
					}else if(YdConstant.YD_GP_A_PLATE_SLAB_YARD.equals(szYD_GP)){			//A후판 슬라브야드[D]
						
						szJMS_TC_CD = "YDY3L005";
					
					}else if(YdConstant.YD_GP_PLATE_GDS_YARD.equals(szYD_GP)){				//후판제품야드 [K]
						
						szJMS_TC_CD = "YDY4L005";
						
					}else if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYD_GP)){				//2후판제품야드 [T] - 2012.12.28 추가 (3기)
						
						szJMS_TC_CD = "YDY8L005";
						
						
					}else if(YdConstant.YD_GP_C_HR_COIL_MATL_YARD.equals(szYD_GP)){			//C열연 코일야드[H]
					
						szJMS_TC_CD = "YDY5L005";
						
					}else if(YdConstant.YD_GP_C_HR_COIL_GDS_YARD.equals(szYD_GP)){			//C열연 제품야드[J]
						
						szJMS_TC_CD = "YDY5L005";
						
					} 

					if(!szJMS_TC_CD.equals("")){
						recPara   = JDTORecordFactory.getInstance().create();
						recPara.setField("MSG_ID",           szJMS_TC_CD       );					
						recPara.setField("MSG_GP",           "I"               );
						recPara.setField("YD_EQP_ID",        szEqpId);
						recPara.setField("YD_WRK_PROG_STAT", ""                );
						recPara.setField("YD_SCH_CD",        ""                );
						recPara.setField("YD_CRN_SCH_ID",    ""                );
						recPara.setField("YD_L2_WR_GP",      "J"               );
						recPara.setField("YD_L3_HD_RS_CD",   "9999"            );
						recPara.setField("YD_L3_MSG",        ""                );
						
						ydDelegate.sendMsg(recPara);
						
					}
				}
			}
		}catch(DAOException e){
			ydUtils.putLog("YdJspCommonSeEJB", "wrkCrnChange", e.getMessage(), YdConstant.ERROR);
			
			throw e;
			
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
			
		}
		
		szLogMsg = "JSP-SESSION [(작업)크레인 변경 ]끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
		
		
		return szRtnValue ;
		
		
	}
	
	
	
	
	/**
	 * 크레인 작업 구분 지정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String crnWrkGPartSet(JDTORecord[] recMsg) throws DAOException {
    	String szRtnMsg = null;
    	String szLogMsg = null;
    	String szMethodName = "crnWrkGPartSet";
    	String szOperationName = "크레인 작업 구분 지정";
		JDTORecord recTemp = null;
		JDTORecord recPara = null;
		JDTORecordSet rsrstData =  null;
		int nRtnVal =0;
		
		YdSchRuleDao ydSchRuleDao = new YdSchRuleDao();
		
		
		try{
			
			szLogMsg = "JSP-SESSION [크레인 작업 구분 지정 ]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			
			//for(int x=0 ; x < recMsg.length ;x++){
				
				
			//1. 크레인 스케줄에서  현재 선택된 스케줄과 , 주설비 크레인으로 선택된 정보이외의
		    // 정보를 전부 스케줄정보를 구한다
			recPara = JDTORecordFactory.getInstance().create();
			rsrstData =JDTORecordFactory.getInstance().createRecordSet("YD");
			recPara.setField("YD_EQP_ID", recMsg[0].getField("YD_EQP_ID"));
			recPara.setField("YD_SCH_CD", recMsg[0].getField("WRK_YD_SCH_CD"));
			
			ydUtils.displayRecord(szOperationName, recPara);
			
			nRtnVal = ydSchRuleDao.getYdSchrule(recPara, rsrstData, 5);
			
			if(nRtnVal<1){
				//ERR : 변경할 스케줄 정보가 없습니다.
				return YdConstant.RETN_CD_NOTEXIST;
			}
				
				
			//2. 스케줄 정보에 모두 스케줄 금지상태로 세팅한다.
			rsrstData.first();
			
			do{
				recTemp = JDTORecordFactory.getInstance().create();
				recPara = JDTORecordFactory.getInstance().create();
				
				recTemp = rsrstData.getRecord();
				
				recPara.setField("YD_SCH_CD", recTemp.getField("YD_SCH_CD"));
				//Y : 기동금지 , N : 기동가능
				recPara.setField("YD_SCH_PROH_EXN", "Y"); 
				
				
				
				ydUtils.displayRecord(szOperationName, recPara);
				nRtnVal =ydSchRuleDao.updYdSchrule(recPara, 0);
				
				if (nRtnVal < 0)
				{
					//ERR : 해당 스케줄 기준을 변경 할 수 없습니다.					
					continue;
				}
			}while(rsrstData.next());
			
			szRtnMsg = YdConstant.RETN_CD_SUCCESS;
		//}
	
		}catch(Exception e){
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
			szLogMsg = "[JSP Session]크레인 작업 구분 지정 에러발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			//throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
		szLogMsg = "JSP-SESSION [크레인 작업 구분 지정 ]끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
		
		return szRtnMsg;
	}
	
	
//	public void crnWrkGPartSet(JDTORecord[] recMsg) throws DAOException {
//		
//		JDTORecord recPara = null;
//		JDTORecord recTemp = null;				
//	 	JDTORecordSet rsrstDataSch =  null;
//	 	
//	 	int intGp = 0;
//	 	
//	 	
//	 	YdCrnSchDao  ydCrnSchDao 	= new YdCrnSchDao ();
//    	YdWrkbookDao ydWrkbookDao	= new YdWrkbookDao();
//    	YdSchRuleDao ydSchRuleDao 	= new YdSchRuleDao();
//	 	
//	 	
//		try{
//			
//			for(int x=0 ; x < recMsg.length ;x++){
//				
//				
//				// 1. 스케줄 CD
//					recPara = JDTORecordFactory.getInstance().create();
//					recPara.setField("YD_SCH_CD", recMsg[x].getField("YD_SCH_CD"));
//					recPara.setField("YD_WRK_CRN_PRIOR", new Integer(0));
//					recPara.setField("MODIFIER", recMsg[x].getField("MODIFIER"));
//				
//				// 2. 스케줄 기준에 해당 스케줄 코드 주작업  우선순위 '0' 처리한다.
//					intGp =  	ydSchRuleDao.updYdSchrule(recPara, 0);
//					
//				// 3. 작업예약 중 완료 또는 삭제 되지 않은 작업에서 해당 스케줄 CD로 세팅된 작업예약 ID[]검색					
//					recPara = JDTORecordFactory.getInstance().create();
//					rsrstDataSch =  JDTORecordFactory.getInstance().createRecordSet("YD");
//					recPara.setField("YD_SCH_CD", recMsg[x].getField("YD_SCH_CD"));
//					
//					intGp = ydWrkbookDao.getYdWrkbook(recPara, rsrstDataSch, 8);					
//					
//				// 4. 작업예약 ID 우선순위를 '0' 처리
//					
//					if (intGp < 1)
//					{
//						//해당 스케줄 코드 
//						return;
//					}
//					
//					rsrstDataSch.first();
//					
//					do
//					{
//						recTemp = JDTORecordFactory.getInstance().create();
//						recPara = JDTORecordFactory.getInstance().create();
//						recTemp = rsrstDataSch.getRecord();						
//						
//						recPara.setField("YD_WBOOK_ID", recTemp.getField("YD_WBOOK_ID"));
//						recPara.setField("YD_SCH_PRIOR", new Integer(0));
//						
//						ydWrkbookDao.updYdWrkbook(recPara, 0);
//						
//					}while(rsrstDataSch.next());
//					
//					
//				// 5. 크레인 스케줄에  삭제 처리 되지않고 작업진행상태가 'W'인것 조회					
//					recPara = JDTORecordFactory.getInstance().create();				
//					recPara.setField("YD_SCH_CD", recMsg[x].getField("YD_SCH_CD"));
//					recPara.setField("YD_WRK_PROG_STAT", "W");
//					
//					rsrstDataSch =  JDTORecordFactory.getInstance().createRecordSet("YD");
//					
//					intGp = ydCrnSchDao.getYdCrnsch(recPara, rsrstDataSch, 24);
//					
//					if(intGp < 1 )
//					{
//						return;
//					}
//					
//					rsrstDataSch.first();
//					
//					do
//					{	
//						recTemp = JDTORecordFactory.getInstance().create();
//						recPara = JDTORecordFactory.getInstance().create();
//						recTemp = rsrstDataSch.getRecord();						
//						
//						recPara.setField("YD_CRN_SCH_ID", recTemp.getField("YD_CRN_SCH_ID"));
//						recPara.setField("YD_SCH_PRIOR", new Integer(0));
//						
//						// 6. 크레인 스케줄 우선순위 '0' 처리한다.
//						ydCrnSchDao.updYdCrnsch(recPara, 0);
//						
//					}while(rsrstDataSch.next());
//			}
//	
//		}catch(Exception e){
//			throw new DAOException(getClass().getName() + e.getMessage(),e);
//		}
//	}
	
	/**
	 * 크레인 작업 구분 해제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return String
	 * @throws DAOException
	 */
	public String crnWrkGpCalcle(JDTORecord[] recMsg) throws DAOException {
    	String szRtnMsg = null;
    	String szLogMsg = null;
    	String szMethodName = "crnWrkGpCalcle";
		JDTORecord recTemp = null;
		JDTORecord recPara = null;
		JDTORecordSet rsrstData =  null;
		int nRtnVal =0;
		
		YdSchRuleDao ydSchRuleDao = new YdSchRuleDao();
		
		
		try{
			
			
			szLogMsg = "JSP-SESSION [크레인 작업 구분 해제 ]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			
			//for(int x=0 ; x < recMsg.length ;x++){
				
				
			//1. 크레인 스케줄에서  현재 선택된 스케줄과 , 주설비 크레인으로 선택된 정보이외의
		    // 정보를 전부 스케줄정보를 구한다
			recPara = JDTORecordFactory.getInstance().create();
			rsrstData =JDTORecordFactory.getInstance().createRecordSet("YD");
			recPara.setField("YD_EQP_ID", recMsg[0].getField("YD_EQP_ID"));
			recPara.setField("YD_SCH_CD", "        ");
			
			nRtnVal = ydSchRuleDao.getYdSchrule(recPara, rsrstData, 5);
			
			if(nRtnVal<1){
				//ERR : 변경할 스케줄 정보가 없습니다.
				return YdConstant.RETN_CD_NOTEXIST;
			}
				
				
			//2. 스케줄 정보에 모두 스케줄 가능 상태로 세팅한다.
			rsrstData.first();
			recTemp = JDTORecordFactory.getInstance().create();
			recPara = JDTORecordFactory.getInstance().create();
			do{
				recTemp = rsrstData.getRecord();
				
				recPara.setField("YD_SCH_CD", recTemp.getField("YD_SCH_CD"));
				//Y : 기동금지 , N : 기동가능
				recPara.setField("YD_SCH_PROH_EXN", "N"); 
				
				nRtnVal =ydSchRuleDao.updYdSchrule(recPara, 0);
				
				if (nRtnVal < 0)
				{
					//ERR : 해당 스케줄 기준을 변경 할 수 없습니다.					
					continue;
				}
			}while(rsrstData.next());	
		//}
			szRtnMsg = YdConstant.RETN_CD_SUCCESS;
		}catch(Exception e){
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
			szLogMsg = "[JSP Session]크레인 작업 구분 지정 에러발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			//throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		szLogMsg = "JSP-SESSION [크레인 작업 구분 해제 ] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
		return szRtnMsg;
	}
	
	
	/**
	 * 권상 취소
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param recMsg
	 * @return String
	 * @throws DAOException
	 */
	
	
	public String crnUpCancle(JDTORecord[] recMsg) throws DAOException {
		JDTORecord recPara = null;
		JDTORecord recTemp = null;				
	 	JDTORecordSet rsrstDataSch =  null;
	 	JDTORecordSet rsResult =  null;
	 	YdDelegate ydDelegate 	= new YdDelegate();
	 	
	 	int intRtnVal = -1;
	 	int intGp = 0;
	 	int intLyrNo = 0;
	 	String szMethodName = "crnUpCancle";
	 	String szOperationName = "권상취소";
	 	String szMsg = "";
	 	
	 	YdCrnSchDao 	ydCrnSchDao  	= new YdCrnSchDao();
	 	YdStkLyrDao  	ydStkLyrDao 	= new YdStkLyrDao(); 
	 	YdCrnWrkMtlDao 	ydCrnWrkMtlDao	= new YdCrnWrkMtlDao();	
    	
    	//FROM POSITION
    	String szYdUpWoLoc = null;
    	
    	//권상 지시단 
    	String szYdUpWolayer = null;
    	
    	//적치열 구분
    	String szYdStkColGp = null;
    	
    	//적치 베드 번호
    	String szYdStkBedNo = null;
    	
    	String szJMS_TC_CD = null;
    	String szEjbMethod = null;
	 	String szYD_EQP_ID = null;
	 	String szLogMsg = null;
	 	String szRtnMsg = null;
	 	String szYD_EQP_STAT = null;
	 	
	 	EJBConnector ejbConn = null;
		try{
			
			
			szLogMsg = "JSP-SESSION [권상 취소] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			//for(int x=0 ; x < recMsg.length ;x++){
			
			/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			* 크레인의 야드설비상태 - YD_EQP_STAT
			* W : 스케줄수행대기, 1 : 권상지시, 2 : 권상완료, 3 : 권하지시, 4 : 권하완료
			+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			//크레인의 야드설비상태를 먼저 확인
			rsResult = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			intRtnVal = YdCommonUtils.getYdEqp(szYD_EQP_ID, rsResult);
			if( intRtnVal == 0 ) {
				szLogMsg = "[JSP Session]야드크레인 작업관리 권상 취소 처리 - 크레인설비[" + szYD_EQP_ID + "]가 존재하지 않습니다!";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}else if( intRtnVal < 0 ) {
				szLogMsg = "[JSP Session]야드크레인 작업관리 권상 취소 처리 - 크레인설비[" + szYD_EQP_ID + "]조회시 오류 발생";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NOTEXIST;
			}
			
			rsResult.first();
			recPara = rsResult.getRecord();
			szYD_EQP_STAT = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_STAT");
			if( !szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_UP_WO) ) {
				//1 : 권상지시 상태가 아닌 경우에는 에러메시지 반환
				szLogMsg = "[JSP Session]야드크레인 작업관리 권상 취소 처리 - 크레인의 야드설비상태[" + szYD_EQP_STAT + "]가 권상 취소처리 가능한 상태[1 : 권상지시]여야합니다!";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				return YdConstant.RETN_CRN_STATUS_ERR;
			}
			
			/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
			
			
			//1. 현 크레인 스케줄에 물려 있는 권상 지시 위치,  권상 지시 단 정보
				
				szYdUpWoLoc 	= yddatautil.setDataDefault(recMsg[0].getField("YD_UP_WO_LOC"), ""); 
				szYdUpWolayer	= yddatautil.setDataDefault(recMsg[0].getField("YD_UP_WO_LAYER"), "");
				
				if ("".equals(szYdUpWoLoc)){
					//권상  지시 위치 가 존재 하지 않습니다.
					szMsg ="[JSP Session]권상  지시 위치 가 존재 하지 않습니다.";
            		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;
				}
				
				if ("".equals(szYdUpWolayer)){
					//권상 지시 단 정보가 존재 하지 않습니다.
					szMsg ="[JSP Session]권상 지시 단 정보가 존재 하지 않습니다.";
            		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;
				}
				
				szYdStkColGp = szYdUpWoLoc.substring(0, 6);
				szYdStkBedNo = szYdUpWoLoc.substring(6, 8);
				
				
			//2. 현 크레인 스케줄 ID 에 물려 있는 재료정보를  낮은 단 부터 가지고 온다.
				rsrstDataSch = JDTORecordFactory.getInstance().createRecordSet("YD");
				recPara = JDTORecordFactory.getInstance().create();
				
				recPara.setField("YD_CRN_SCH_ID", recMsg[0].getField("YD_CRN_SCH_ID"));
				
				
				intGp = ydCrnWrkMtlDao.getYdCrnwrkmtl(recPara, rsrstDataSch, 7);
				
				
				if(intGp < 1) {
					//현 스케줄 작업재료가 존재하지 않습니다.
					szMsg ="현 스케줄"+recMsg[0].getField("YD_CRN_SCH_ID")+" 작업재료가 존재하지 않습니다.";
            		ydUtils.putLog("YdJspCommonSeEJBBean",szMethodName,szMsg  , YdConstant.ERROR);
					return YdConstant.RETN_CD_NOTEXIST;
				}
				
			//3. 재료정보 최하단부터 적치단 정보의  최상단 +1 위치 부터 다시 SETTING 한다.
				
				rsrstDataSch.first();
				intLyrNo = Integer.parseInt(szYdUpWolayer);
				do{
					recTemp = JDTORecordFactory.getInstance().create();
					recPara = JDTORecordFactory.getInstance().create();
					
					recTemp = rsrstDataSch.getRecord();
					
					recPara.setField("YD_STK_COL_GP", szYdStkColGp);  
					recPara.setField("YD_STK_BED_NO", szYdStkBedNo);					
					recPara.setField("YD_STK_LYR_NO", YdUtils.fillSpZr(Integer.toString(intLyrNo), 3, 0));
					

					recPara.setField("MODIFIER", recTemp.getField("MODIFIER"));

					recPara.setField("DEL_YN","N");
					recPara.setField("STL_NO", recTemp.getField("STL_NO"));
					
					//Default 값 재확인 
					recPara.setField("YD_STK_LYR_ACT_STAT", "E");
					recPara.setField("YD_STK_LYR_MTL_STAT", "U");
					
					
					
					intGp = ydStkLyrDao.updYdStklyr(recPara, 0);
					
					if(intGp < 1) {
						szMsg ="적치단 정보 UPDATE 실패!";
	            		ydUtils.putLog("YdJspCommonSeEJBBean",szMethodName,szMsg  , YdConstant.ERROR);
						return YdConstant.RETN_CD_FAILURE;
					}
					
					
					intLyrNo++;
					
				}while(rsrstDataSch.next());
				
				
			//4. 현 크레인 스케줄  작업 진행 상태를 'W' 로 세팅한다.
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID",recMsg[0].getField("YD_CRN_SCH_ID"));
				recPara.setField("YD_WRK_PROG_STAT", "W");
				recPara.setField("YD_UP_WR_LOC",    "");
				recPara.setField("YD_UP_WR_LAYER",  "");
				recPara.setField("YD_UP_WRK_ACT_GP", "");
				
				intGp = ydCrnSchDao.updYdCrnsch(recPara, 0);
				
				
				
				
				
				
				
		
				
				
				
			//5. 권상 취소 전문 생성 / 전송 [차상국으로 갈 전문 편집]=> 
				// 작업 재지시 전송 전문 전송
				
				
				//권하위치 변경  후  작업 재지시 
				String szYdGp ="";
				//YdDelegate ydDelegate = new YdDelegate();
				recPara = JDTORecordFactory.getInstance().create();
				
				szYdGp = recMsg[0].getFieldString("YD_GP");			
			
				if (YdConstant.YD_GP_C_SLAB_YARD.equals(szYdGp)){						//C연주 슬라브 야드[A]
					//recPara.setField("JMS_TC_CD","YDYDJ640");
					szJMS_TC_CD = "YDYDJ640";
					szEjbMethod = "procY1CrnWrkOrdReq";
				}else if(YdConstant.YD_GP_PORT_SLAB_YARD.equals(szYdGp)){			//항만슬라브야드 기능추가 - 2015.12.30 LeeJY
					szJMS_TC_CD = "YDYDJ640";
					szEjbMethod = "procY1CrnWrkOrdReq";
				}else if(YdConstant.YD_GP_A_PLATE_SLAB_YARD.equals(szYdGp)){			//A후판 슬라브야드[D]
					//recPara.setField("JMS_TC_CD","YDYDJ641");
					szJMS_TC_CD = "YDYDJ641";
					szEjbMethod = "procY3CrnWrkOrdReq";
				}else if(YdConstant.YD_GP_PLATE_GDS_YARD.equals(szYdGp)){				//후판제품야드 [K]
					//recPara.setField("JMS_TC_CD","YDYDJ642");
					szJMS_TC_CD = "YDYDJ642";
					szEjbMethod = "procY4CrnWrkOrdReq";
				}else if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYdGp)){				//2후판제품야드 [T] - 2013.01.04 추가 (3기)
					szJMS_TC_CD = "YDYDJ642";
					szEjbMethod = "procY4CrnWrkOrdReq";
				}else if(YdConstant.YD_GP_C_HR_COIL_MATL_YARD.equals(szYdGp)){			//C열연 코일야드[H]
					//recPara.setField("JMS_TC_CD","YDYDJ643");
					szJMS_TC_CD = "YDYDJ643";
					szEjbMethod = "procY5CrnWrkOrdReq";
				}else if(YdConstant.YD_GP_C_HR_COIL_GDS_YARD.equals(szYdGp)){			//C열연 제품야드[J]
					//recPara.setField("JMS_TC_CD","YDYDJ643");
					szJMS_TC_CD = "YDYDJ643";
					szEjbMethod = "procY5CrnWrkOrdReq";
				} else if(YdConstant.YD_GP_INTGR_YARD.equals(szYdGp)){					//통합제품야드[S]
					//recPara.setField("JMS_TC_CD","YDYDJ643");
					szJMS_TC_CD = "YDYDJ644";
					szEjbMethod = "procY0CrnWrkOrdReq";
				}
				
				szYD_EQP_ID = recMsg[0].getFieldString("YD_EQP_ID");
				
				szLogMsg = "[JSP Session]권상 취소  - 야드구분[" + szYdGp + "], 크레인 설비[" + szYD_EQP_ID + "]";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);

//SJH03004				
				recPara.setField("JMS_TC_CD", 			szJMS_TC_CD);
				recPara.setField("YD_EQP_ID" , 			szYD_EQP_ID);
				recPara.setField("YD_WRK_PROG_STAT" , 	"W");
				                   
				ydUtils.displayRecord(szOperationName, recPara);
				//JMS Call
				//ydDelegate.sendMsg(recPara);
				
				//EJB Method Call
//				sjhkim
				if( szEjbMethod.equals("procY5CrnWrkOrdReq") ) {
					ejbConn = new EJBConnector("default", "CoilCraneLdHdSeEJB", this);		
				} else{
					ejbConn = new EJBConnector("default", "CraneLdHdSeEJB", this);	
				}

				szRtnMsg = (String)ejbConn.trx(szEjbMethod, new Class[] { JDTORecord.class }, new Object[] { recPara });
				
				if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {													//성공
					szLogMsg = "[JSP Session]권상 취소 성공 - 크레인 작업지시";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}else if( szRtnMsg.equals(YdConstant.RETN_CRN_SCH_PROH) ) {											//스케줄금지 상태
					szLogMsg = "[JSP Session]권상 취소 성공 - 크레인 작업지시 : 스케줄금지 상태";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}else if( szRtnMsg.equals(YdConstant.RETN_CD_TC_ERROR) ) {											//전문 에러
					szLogMsg = "[JSP Session]권상 취소 성공 - 크레인 작업지시 : 전문 에러";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}else if( szRtnMsg.equals(YdConstant.RETN_CRN_NO_SCH) ) {											//크레인스케쥴이 존재하지 않음
					szLogMsg = "[JSP Session]권상 취소 성공 - 크레인 작업지시 : 크레인스케쥴이 존재하지 않음";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}else if( szRtnMsg.equals(YdConstant.RETN_CRN_NO_WRK) ) {											//작업예약이 더 이상 존재하지 않음
					szLogMsg = "[JSP Session]권상 취소 성공 - 크레인 작업지시 : 작업예약이 더 이상 존재하지 않음";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}else if( szRtnMsg.equals(YdConstant.RETN_CD_FAILURE) ) {											//실패
					szLogMsg = "[JSP Session]권상 취소 실패 - 크레인 작업지시 실패";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				}
				
				
			//}
	
		}catch(Exception e){
			szLogMsg = "[JSP Session]권상 취소 에러발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
		szLogMsg = "JSP-SESSION [권상 취소] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return szRtnMsg;
	}
	
	
	/**
	 * 메뉴얼 작업지시 편성
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void ydManualReq(JDTORecord[] inDto) throws DAOException {
		int       intRtnVal    = 0;
		int       intSh        = 0;
		String    szMsg        = null;
		String    szMethodName = null;
		String [] strArrStlNo  = null;
		JDTORecord    recPara  = JDTORecordFactory.getInstance().create();
		
		
		szMsg        = "";
		szMethodName = "ydManualReq";
		String szOperationName = "메뉴얼 작업지시 편성";
			
		try {
			
			szMsg = "JSP-SESSION [메뉴얼 작업지시 편성] 시작";
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
				
				//추가 데이터 
				//TO Guide 정보
				recPara.setField("YD_TO_LOC_GUIDE", inDto[x].getFieldString("TO_BED"));
				recPara.setField("REGISTER", inDto[x].getField("REGISTER"));
				//YD_WRK_PLAN_TCAR :계획 대차
				recPara.setField("YD_WRK_PLAN_TCAR", inDto[x].getField("YD_WRK_PLAN_TCAR"));
				
				
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
		
		szMsg = "JSP-SESSION [메뉴얼 작업지시 편성] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
	}	// end of ydManualReq
	
	
	/**
	 *  저장집합 목록 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getYdStrGtrCd(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara    = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet  = JDTORecordFactory.getInstance().createRecordSet("retTmp");		
		String szMsg        = "";		
		String szMethodName = "getYdStrGtrCd";
		int intRtnVal = 0;
	
		YdStrGtrDao ydStrGtrDao = new YdStrGtrDao();
			
		try {
			
			szMsg = "JSP-SESSION [저장집합 목록 조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			recPara.setField("YD_GP",     yddatautil.setDataDefault(inDto.getField("YD_GP"), ""));
			recPara.setField("YD_BAY_GP", yddatautil.setDataDefault(inDto.getField("YD_BAY_GP"), ""));
			
			ydStrGtrDao.getYdStrgtr(recPara, outRecSet, 1);
			
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
			
			// 레코드셋을 앞으로 커서를 위치 시켜준다 .
			outRecSet.first();
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		szMsg = "JSP-SESSION [저장집합 목록 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return outRecSet;
	} //end of getStock
	
	
	
	/**
	 * 권하위치 변경 (크레인작업관리 화면)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public Boolean updToPosFix(JDTORecord[] inDto) throws DAOException {
		
		int intRtnVal          = 0;				
		String szLogMsg           = null;
		String szMethodName    = "updToPosFix";		
		String szOperationName = "권하위치 변경 (크레인작업관리 화면)";
		
		String szStkPos        = null;
		String szStkColGp      = null;
		String szStkBedNo      = null;
		String szStkLyrNo      = null;
		
		JDTORecord    recPara  = null;
		JDTORecord    recInPara  = null;
		JDTORecord    recTemp  = null;
		JDTORecord    recSet   = null;
		JDTORecord    recSetTmp= null;
		JDTORecord    recInTemp= null;
		
		String szOldStkPos     = null;
		String szOldStkColGp   = null;
		String szOldStkBedNo   = null;
		String szOldStkLyrNo   = null;
		
		boolean bool = false;
			
		YdStkLyrDao ydStkLyrDao        = new YdStkLyrDao();
		YdCrnSchDao ydCrnSchDao        = new YdCrnSchDao();
		YdCrnWrkMtlDao  ydCrnWrkMtlDao = new YdCrnWrkMtlDao();
		YdDelegate ydDelegate 	= new YdDelegate();
		YdStkColDao ydStkColDao     = new YdStkColDao();
		JDTORecordSet    outRecSet  = null;
		JDTORecordSet    outRecSetTmp = null;
		
		String szYdWrkProgStat 		="";		
		String szSendYdWrkProgStat 	="";
		String szYdGp  				="";
		String szYdSchCd 			="";
	
		String szJMS_TC_CD 			="";
		String szEjbMethod 			="";
	    String szYD_EQP_ID 			="";
	    String szRtnMsg 			="";
	    String szYdSchId 			="";
	    
	    String szYdGpTemp 			="";
	    String szEqpGp 				=""; // 변경 설비구분 
	    String szEqpGpBefo 			=""; // 기존 설비구분 
	    String szYdWbookId 			=""; //작업예약 ID
	    String szRtnMsg1			= null;
	    
	    EJBConnector ejbConn = null;
		
		try {
			szLogMsg = "JSP-SESSION [권하위치 변경 (크레인작업관리 화면)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			for(int x=0;x<inDto.length;x++)
			{

				// 1. 기존 To 위치 정보 와 변경 To 위치 정보 
				recPara   	= JDTORecordFactory.getInstance().create();
				outRecSet  	= JDTORecordFactory.getInstance().createRecordSet("YD");
				
				szYdSchId =  yddatautil.setDataDefault(inDto[x].getField("YD_CRN_SCH_ID"), "");
				recPara.setField("YD_CRN_SCH_ID",szYdSchId);		
				
				szLogMsg = "[JSP Session] " + szOperationName + " 크레인스케줄 ID :["+szYdSchId+"]로 스케줄 조회" ;
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
        		
				intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, outRecSet, 0);
				
				if(intRtnVal < 0 )
				{
					szLogMsg = "[JSP Session] " + szOperationName + "해당 크레인스케줄 ID  정보: ["+ szYdSchId +" ]조회시 ERROR발생";            		
            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
            		throw new DAOException(szLogMsg);
				}else if(intRtnVal == 0 ){
					szLogMsg = "[JSP Session] " + szOperationName + "해당 스케줄크레인스케줄 ID  정보: ["+ szYdSchId + "] 가 존재하지않습니다";            		
            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
            		throw new DAOException(szLogMsg);
            		
				}
				
				szLogMsg = "[JSP Session] " + szOperationName + " 크레인스케줄 ID :["+szYdSchId+"]로 스케줄 조회 성공" ;
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				outRecSet.first();
				
				recTemp   = JDTORecordFactory.getInstance().create();			
				recTemp = outRecSet.getRecord();
				
				szOldStkPos   = yddatautil.setDataDefault(recTemp.getFieldString("YD_DN_WO_LOC"),"");					
				szOldStkLyrNo = yddatautil.setDataDefault(recTemp.getFieldString("YD_DN_WO_LAYER"),"");
				
				szYdWbookId = ydDaoUtils.paraRecChkNull(recTemp, "YD_WBOOK_ID");
				
				//현 스케줄 작업 진행상태(DB) 
				szSendYdWrkProgStat =  ydDaoUtils.paraRecChkNull(recTemp, "YD_WRK_PROG_STAT");
				
				szLogMsg = "[JSP Session] " + szOperationName + "현 스케줄 작업 진행상태(DB)[ " + szSendYdWrkProgStat  +" ]"  ;
        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
        		
				szLogMsg = "[JSP Session] " + szOperationName + "DB 권하지시위치 [ " + szOldStkPos + "-" + szOldStkLyrNo +"]"  ;
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
				//통합야드의 경우는 8-3자리로 들어옴 (12자리)
				//앞에 8자리만 사용해준다 (2009.10.12 이현성)
				//권상지시위치 (야드구분을 얻어내기 위함)
				
				szYdGpTemp = yddatautil.setDataDefault(inDto[x].getField("YD_GP"), "");
				
				szLogMsg = "[JSP Session] " + szOperationName + "야드구분 [ " + szYdGpTemp +"]" ;
        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
        		
				if(szYdGpTemp.equals(YdConstant.YD_GP_INTGR_YARD)){
					
					szStkPos =  yddatautil.setDataDefault(inDto[x].getField("YD_DN_WO_LOCLYR"), "");
					
            		szLogMsg = "[JSP Session] " + szOperationName + "통합슬라브야드 권하지시위치8-3 자리에서 데이터 새로 가지고옴  .["+ szStkPos +"]";
            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
            		
					if ("".equals(szStkPos)){				
						
						szLogMsg = "[JSP Session] " + szOperationName + "통합슬라브야드 변경 권하지시위치 정보가 없습니다."  ;
	            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
	            		throw new DAOException(szLogMsg);
					}
					
					if(szOldStkPos.equals(szStkPos)){
						szLogMsg = "[JSP Session] " + szOperationName + "입력된 권하위치가 기존 권하위치와 같습니다."  ;
	            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
	            		throw new DAOException(szLogMsg);
					}
					
				}else{
					
					szStkPos = yddatautil.setDataDefault(inDto[x].getField("YD_DN_WO_LOC"), "");
					
					szLogMsg = "[JSP Session] " + szOperationName + "입력받은 권하지시위치 [ " + szStkPos + "]"  ;
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					if ("".equals(szStkPos)){		
						szLogMsg = "[JSP Session] " + szOperationName + "변경 권하지시위치 정보가 없습니다."  ;
	            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
	            		throw new DAOException(szLogMsg);
					}
					
					if(szOldStkPos.equals(szStkPos)){
						szLogMsg = "[JSP Session] " + szOperationName + "입력된 권하위치가 기존 권하위치와 같습니다."  ;
	            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
	            		throw new DAOException(szLogMsg);
					}
				}
				
				szStkLyrNo = yddatautil.setDataDefault(inDto[x].getField("YD_DN_WO_LAYER"), "");
				
				if ("".equals(szStkLyrNo)){
					szLogMsg = "[JSP Session] " + szOperationName +  "변경 권하지시단 정보가 없으나 재계산하여줍니다.";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}
				
				if(szStkPos.length() >=8)
				{
					szStkColGp 	= szStkPos.substring(0, 6); 
					szStkBedNo 	= szStkPos.substring(6, 8);
					szEqpGp 	= szStkColGp.substring(2,4);
				}else{
					szLogMsg = "[JSP Session] " + szOperationName + "변경 권하지시위치 정보가 맞지 않습니다"  ;
            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
            		throw new DAOException(szLogMsg);
				}
				//-----------------------------------------------------------------------
				/*
				 * 2014.07.24 윤재광
				 * 후판제품창고일 경우 현대3사제품과 타고객사 제품 혼합저장방지 체크
				 * 1. 현대 3사(A14469,A14478,A11119)위에 타 고객사 제품 저장시
				 * 2. 현대 3사외 고객사 제품위에 현대3사 제품 저장시
				 */
				if(szStkColGp.startsWith("T")){ 
					
					String sIsAvailabe = "0";
					
					outRecSetTmp= JDTORecordFactory.getInstance().createRecordSet("YD");
					recInPara	= JDTORecordFactory.getInstance().create();
					recInPara.setField("YD_CRN_SCH_ID", szYdSchId);
					recInPara.setField("YD_STK_COL_GP", szStkColGp);
					recInPara.setField("YD_STK_BED_NO", szStkBedNo);
					
					intRtnVal = ydCrnSchDao.getYdCrnsch(recInPara, outRecSetTmp, 509);
					
					if(intRtnVal > 0){
						outRecSetTmp.first(); 
						
						recInPara 	= outRecSetTmp.getRecord();
						
						sIsAvailabe = yddatautil.setDataDefault(recInPara.getFieldString("IS_AVAILABLE"),"");
					}
					
					if( "1".equals(sIsAvailabe) ) {
						
						bool = false;
						
						szLogMsg = "현대3사 제품위에 타고객사 제품을 적치할 수 없습니다."; 
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
						
						return new Boolean(bool);
						
					}else if( "2".equals(sIsAvailabe) ) {
						
						bool = false;
						
						szLogMsg = "현대3사 제품을 타고객사 제품위에 적치할 수 없습니다.";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
						
						return new Boolean(bool);
					}
					
				}
				//-----------------------------------------------------------------------
				//	권하지시위치 변경 시 베드의 TO위치 정합성 판단.(추가: 2010.02.10 이현성)
				//-----------------------------------------------------------------------
				YdStkLocVO	ydStkLocVO	= new YdStkLocVO();
				recInPara	= JDTORecordFactory.getInstance().create();
				
				/* 파라미터정의:	1) YD_STK_COL_GP	- 적치열
				 * 				2) YD_STK_BED_NO	- 적치베드
				 * 				3) YD_EQP_WRK_SH	- 작업총매수
				 * 				4) YD_EQP_WRK_WT	- 작업총중량
				 * 				5) YD_EQP_WRK_T		- 작업총두께
				 * 				6) YD_SCH_CD		- 스케줄코드
				 */
				recInPara.setField("YD_STK_COL_GP", szStkColGp);
				recInPara.setField("YD_STK_BED_NO", szStkBedNo);
				recInPara.setField("YD_EQP_WRK_SH", ydDaoUtils.paraRecChkNull(recTemp, "YD_EQP_WRK_SH"));
				recInPara.setField("YD_EQP_WRK_WT", ydDaoUtils.paraRecChkNull(recTemp, "YD_EQP_WRK_WT"));
				recInPara.setField("YD_EQP_WRK_T" , ydDaoUtils.paraRecChkNull(recTemp, "YD_EQP_WRK_T"));
				recInPara.setField("YD_SCH_CD"	  , ydDaoUtils.paraRecChkNull(recTemp, "YD_SCH_CD"));
				 				
				if (szStkColGp.substring(0,1).equals("H") || szStkColGp.substring(0,1).equals("J")){
					szRtnMsg1 = CoilYdToLocDcsnUtil.procBedStackable(recInPara, ydStkLocVO, szMethodName);
				} else {
					szRtnMsg1 = YdToLocDcsnUtil.procBedStackable(recInPara, ydStkLocVO, szMethodName);
				}
				
				szLogMsg = "[JSP Session- " + szOperationName + "] 권하위치 Chekc Returen Value "+szRtnMsg1 ;
        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
        		
        		int intERR_CD = 0;
        		StringBuffer szSTATUS		= new StringBuffer();
        		
				
				if( !szRtnMsg1.equals(YdConstant.RETN_CD_SUCCESS) ) {
					if( szRtnMsg1.equals(YdConstant.RETN_CD_NOTEXIST) ) {
						
						intERR_CD = ydStkLocVO.getYdBedErrCd();
						
						if( intERR_CD >= YdConstant.YD_BED_ERR_CD_H_OVER ) {
							//해당하는 적치베드에 적치가능높이 OVER
							intERR_CD	-= YdConstant.YD_BED_ERR_CD_H_OVER;
							
							szSTATUS.append("적치가능높이 OVER");
						}
						
						if( intERR_CD >= YdConstant.YD_BED_ERR_CD_WT_OVER ) {
							//해당하는 적치베드에 적치가능중량 OVER
							intERR_CD	-= YdConstant.YD_BED_ERR_CD_WT_OVER;
							
							if( szSTATUS.length() > 0 ) szSTATUS.append(", ");
							
							szSTATUS.append("적치가능중량 OVER");
						}
						
						if( intERR_CD == YdConstant.YD_BED_ERR_CD_SH_OVER ) {
							//해당하는 적치베드에 적치가능매수 OVER
							
							if( szSTATUS.length() > 0 ) szSTATUS.append(", ");
							
							szSTATUS.append("적치가능매수 OVER");
						}
						
						szLogMsg = "해당크레인스케줄["+szYdSchId+"]의 권하지시적치열["+szStkColGp+"], 권하지시베드["+szStkBedNo+"]에 적치불가능합니다 - " + szSTATUS.toString();
						
					}else{
						
					}
					throw new DAOException(szLogMsg);
				}
				
				if( ydStkLocVO.getYdBedErrCd() != YdConstant.YD_BED_STACKABLE) {
					throw new DAOException(szLogMsg);
				}
				//-----------------------------------------------------------------------
				
				//-----------------------------------------------------------------------
				//	권하위치 변경할시에 작업지시 취소전문 발생 (데이터 변경전정보)
				//-----------------------------------------------------------------------
				szYdWrkProgStat =  ydDaoUtils.paraRecChkNull(recTemp, "YD_WRK_PROG_STAT");
				szYdSchCd       = ydDaoUtils.paraRecChkNull(recTemp, "YD_SCH_CD");
				
				// 신규 위치 적치단 정보
				szLogMsg = "[JSP Session] " + szOperationName +  "변경 권하지시단 정보 계산";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				recPara   	= JDTORecordFactory.getInstance().create();
				recTemp 	= outRecSet.getRecord();
				outRecSet  	= JDTORecordFactory.getInstance().createRecordSet("YD");
				
				recPara.setField("YD_STK_COL_GP", szStkColGp);
				recPara.setField("YD_STK_BED_NO", szStkBedNo);
				
				szLogMsg = "[JSP Session] " + szOperationName +  "적치단 정보조회";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 29);
				
				if (intRtnVal == 0){
					szStkLyrNo ="001";
				}
				else if ( intRtnVal > 0 )
				{
					outRecSet.last();
					recTemp 	= outRecSet.getRecord();
					szStkLyrNo 	= ydDaoUtils.stringPlusInt(recTemp.getFieldString("YD_STK_LYR_NO"),1);					
				}
				
				szLogMsg =  "[JSP Session] " + szOperationName +  "신규위치정보 :"+ szStkColGp  + "-"+ szStkBedNo +":" +szStkLyrNo;
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				recPara   = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID", yddatautil.setDataDefault(inDto[x].getField("YD_CRN_SCH_ID"), ""));
				
				outRecSet  = JDTORecordFactory.getInstance().createRecordSet("YD");
				
				szLogMsg = "[JSP Session] " + szOperationName +  "크레인 작업재료 조회";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				intRtnVal = ydCrnWrkMtlDao.getYdCrnwrkmtl(recPara, outRecSet, 1);
				
				szLogMsg = "[JSP Session] " + szOperationName +  "크레인 작업재료 조회 리턴값 :[ "+  intRtnVal +" ]";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				if (intRtnVal == 0)
				{
					szLogMsg =  "[JSP Session] " + szOperationName + "해당 스케줄에 해당되는 크레인 작업 재료가 없습니다.";
            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
            		throw new DAOException( szLogMsg );
				} else if (intRtnVal < 0){
					
					szLogMsg = "[JSP Session] " + szOperationName + "해당 스케줄에 해당되는 재료 조회시 ERROR";
            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
            		throw new DAOException( szLogMsg );
				}
				
//SJH05001				
				//기존 산적위치 정보가 올바르지 않거나 없을 경우는 기존산적위치를 삭제할 수없으므로 건너띈다.
				if( szOldStkPos.length() == 8 && (!szOldStkPos.equals("XX010101")) && (!szOldStkPos.equals("XXYY0101")))
				{	
					szOldStkColGp = szOldStkPos.substring(0, 6); 
					szOldStkBedNo = szOldStkPos.substring(6, 8);
					
					szLogMsg = "[JSP Session] " + szOperationName +  "기존위치정보 :"+ szOldStkColGp  + "-"+ szOldStkBedNo +":" +szOldStkLyrNo;				
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
            		
					//실제로는 크레인작업재료의 개수만 필요함				
					outRecSet.first();
					for (int nLoop =0 ; nLoop<outRecSet.size();nLoop++ ){
						
						recTemp =JDTORecordFactory.getInstance().create();
						recTemp = outRecSet.getRecord(nLoop);					
						
						// 기존 지시위치 에 쌓여 있는 정보 Clear
						recSet = JDTORecordFactory.getInstance().create();
		                recSet.setField("YD_STK_COL_GP",       szOldStkColGp);    
		                recSet.setField("YD_STK_BED_NO",       szOldStkBedNo); 
		                /*
		                 * 2011.03.02 슬라브 권상모음 스케쥴 삭제시 문제발생 
		                 * 아래 파라미터 막음.
		                 */
		                //recSet.setField("YD_STK_LYR_MTL_STAT", "D");
		                recSet.setField("STL_NO",              yddatautil.setDataDefault(recTemp.getField("STL_NO"),""));
		                
		                szLogMsg = "[JSP Session] " + szOperationName +   "기존 지시위치 에 쌓여 있는 정보 Clear ";
	            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
	            		
		                intRtnVal = ydStkLyrDao.updYdStklyrWithColStockStat(recSet);
		                
		                szLogMsg = "[JSP Session] " + szOperationName +   "기존 지시위치 에 쌓여 있는 정보 Clear 성공 [ " + intRtnVal + " ] ";
		            	ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		            	
		                //차상위치에서 변경 시 목표동 셋팅 초기화 작업
		                if(szOldStkColGp.substring(2, 4).equals("PT") && 
		                		(szOldStkColGp.substring(0,1).equals("A") ||
		                		 szOldStkColGp.substring(0,1).equals("M"))){  //항만슬라브야드 기능추가 - 2015.12.30 LeeJY
		                	
		                	recSet.setField("YD_STKBED_USG_CD",       ""); 
		                	intRtnVal = ydStkColDao.updYdStkcol(recSet,0);	
		                	
		                	szLogMsg = "[JSP Session] " + szOperationName +   "기존 지시위치 에 차상위치에서 변경 시 목표동 셋팅 초기화 작업  성공 [ " + intRtnVal + " ] ";
			            	ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		                }
		                
		            	
		            	
		            }		
					/*
					 * 2016.07.20 윤재광
					 * 이호현 주임 요청사항으로 아래기능 막음
					 */
					/*
					szLogMsg="권하위치 변경으로 스케줄의 기존 TO위치베드를 완산베드에서 해제 시작";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					recInTemp = JDTORecordFactory.getInstance().create();
		        	recInTemp.setField("YD_STK_COL_GP", 			szOldStkPos.substring(0, 6));
		        	recInTemp.setField("YD_STK_BED_NO", 			szOldStkPos.substring(6, 8));
		        	recInTemp.setField("YD_STK_BED_WHIO_STAT", 		"E");
		        	recInTemp.setField("MODIFIER", 					"CHGLOC");
		        	szLogMsg  = DaoManager.updYdStkbed(recInTemp, 0);
		        	*/
				}else{
					szLogMsg = "[JSP Session] " + szOperationName +  "기존 산적위치 정보가 올바르지 않거나 없을 경우는 기존산적위치를 삭제할 수없으므로 건너띈다";				
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}
				
				outRecSet.first();
				
				//작업재료는 밑에것부터 부터 위로 올라와있는 상태이므로 레코드셋을 역순으로 정렬한다
				outRecSet.reverseOrder();
				
				for (int nLoop =0 ; nLoop<outRecSet.size(); nLoop++ ){
					
					// 신규위치에 정보를 Setting
					recSet = JDTORecordFactory.getInstance().create();
					recTemp =JDTORecordFactory.getInstance().create();
					
					recTemp = outRecSet.getRecord(nLoop);					
					recSet.setField("YD_STK_COL_GP",       szStkColGp);    
		            recSet.setField("YD_STK_BED_NO",       szStkBedNo);   
		            recSet.setField("YD_STK_LYR_NO",       ydDaoUtils.stringPlusInt(szStkLyrNo, nLoop)) ;
		            recSet.setField("YD_STK_LYR_MTL_STAT", "D");
	                recSet.setField("STL_NO",              recTemp.getField("STL_NO"));
	                
	            	szLogMsg = "[JSP Session] " + szOperationName +   "신규위치에 정보를 UPDATE ";
	            	ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
	            	
	                intRtnVal = ydStkLyrDao.updYdStklyr(recSet, 0);
	                
	            	if (intRtnVal < 1)
					{
						//신규위치에 정보를 Setting 실패
	            		szLogMsg = "[JSP Session] " + szOperationName + "신규위치에 정보를 Setting 실패 [ " + intRtnVal + " ]"  ;
	            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
	            		throw new DAOException(szLogMsg);
					}
	            	
	        		//신규위치에 정보를 Setting 실패
            		szLogMsg = "[JSP Session] " + szOperationName + "신규위치에 정보를 Setting 성공 [ " + intRtnVal + " ]"  ;
            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
	            	
				}
		
				// 권하위치 정보 스케줄 정보에서 변경
				recPara   = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID", yddatautil.setDataDefault(inDto[x].getField("YD_CRN_SCH_ID"), ""));
				recPara.setField("YD_DN_WO_LOC", szStkColGp+szStkBedNo);				
				recPara.setField("YD_DN_WO_LAYER", szStkLyrNo);
				
				szLogMsg = "[JSP Session] " + szOperationName + " 권하위치 정보 스케줄 정보에서 변경(UPDATE)"  ;
        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				intRtnVal = ydCrnSchDao.updYdCrnsch(recPara, 0);
				
				if (intRtnVal < 1)
				{	
					szLogMsg = "[JSP Session] " + szOperationName +  "권하위치 스케줄 정보 변경 실패 [" +intRtnVal  + " ] " ;
            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
            		throw new DAOException(szLogMsg);
				}
				
				szLogMsg = "[JSP Session] " + szOperationName + " 권하위치 정보 스케줄 정보 UPDATE 성공 "  ;
        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
        		/*
        		 * 2010.09.08 윤재광 
        		 * 권하위치 변경시 권하분리(X)인 스케쥴의 권상위치 정보도 변경한다.
        		 * 이유 : 메인스케쥴 권하위치 변경 후 이후 권하분리 작업취소시 재료정보가 야드에 중복발생.
        		 */
        		{
        			szLogMsg = "[JSP Session] " + szOperationName + " 권하분리(X) 권상위치 정보 스케줄 정보에서 변경(UPDATE)"  ;
            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
    				
    				intRtnVal = ydCrnSchDao.updYdCrnXSchFromLoc(recPara);
    				
    				szLogMsg = "[JSP Session] " + szOperationName + " 권하분리(X) 권상위치 정보 스케줄 정보에서 변경(UPDATE) 성공 [" +intRtnVal  + " ] " ;
            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
        		}
        		
				// 스케줄 변경 후 제원 위치정보를 맞춰준다.
				szLogMsg =  "[JSP Session] " + szOperationName +  "스케줄 변경 후 제원 위치정보 세팅 호출";
        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
        		
        		boolean lb_updYdCrnBed = false;        		
        		lb_updYdCrnBed = YdUtils.updYdCrnschBedData(recPara);
        		
        		if(!lb_updYdCrnBed){
        			szLogMsg =  "[JSP Session] " + szOperationName +  "스케줄 변경 후 제원 위치정보 세팅 실패";
            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
        			
        		}
		
				szLogMsg =  "[JSP Session] " + szOperationName +  "스케줄 변경 후 제원 위치정보 세팅 완료";
        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				//권하위치 변경후 작업지시정보를  재전송 (Manual 작업지시보내는 방법으로  선택 인경우만 전송한다)
				if( szSendYdWrkProgStat.equals(YdConstant.YD_EQP_STAT_UP_WO) || 
					szSendYdWrkProgStat.equals(YdConstant.YD_EQP_STAT_UP_CMPL)){
					
					szLogMsg =  "[JSP Session] " + szOperationName +  "권하위치 변경후 작업지시정보를  재전송";
	        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
	        		
					if(szYdSchCd.length() > 0 ){
						
						szYdGp = szYdSchCd.substring(0,1);
					}else{
						szLogMsg = "[JSP Session] " + szOperationName + " 스케줄코드의 야드구분이 올바르지 않습니다";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					}
					
					szLogMsg =  "[JSP Session] " + szOperationName + "   - 야드구분[" + szYdGp + "]";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					szLogMsg =  "[JSP Session] " + szOperationName + "   - 스케줄 진행상태 ["  + szSendYdWrkProgStat +" ]";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					szJMS_TC_CD = "";
					
					if (YdConstant.YD_GP_C_SLAB_YARD.equals(szYdGp)  ){						//C연주 슬라브 야드 [A]
						szJMS_TC_CD = "YDYDJ640";
						szEjbMethod = "procY1CrnWrkOrdReq";
					}else if(YdConstant.YD_GP_A_PLATE_SLAB_YARD.equals(szYdGp)){			//A후판 슬라브야드[D]
						
						szJMS_TC_CD = "YDYDJ641";
						szEjbMethod = "procY3CrnWrkOrdReq";
					}else if(YdConstant.YD_GP_PLATE_GDS_YARD.equals(szYdGp) 
							|| YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYdGp) ){			//후판제품야드 [K],2후판제품야드 [T] - 2012.12.28 수정 (3기)
						
						szJMS_TC_CD = "YDYDJ642";
						szEjbMethod = "procY4CrnWrkOrdReq";
					}else if(YdConstant.YD_GP_C_HR_COIL_MATL_YARD.equals(szYdGp)){			//C열연 코일야드[H]
					
						szJMS_TC_CD = "YDYDJ643";
						szEjbMethod = "procY5CrnWrkOrdReq";
					}else if(YdConstant.YD_GP_C_HR_COIL_GDS_YARD.equals(szYdGp)){			//C열연 제품야드[J]
						
						szJMS_TC_CD = "YDYDJ643";
						szEjbMethod = "procY5CrnWrkOrdReq";
					}else if(YdConstant.YD_GP_INTGR_YARD.equals(szYdGp)){					//통합제품야드[S]
						
						szJMS_TC_CD = "YDYDJ644";
						szEjbMethod = "procY0CrnWrkOrdReq";
					} 
					
					szYD_EQP_ID = inDto[x].getFieldString("YD_EQP_ID");
					
					szLogMsg = "[JSP Session] " + szOperationName +  "- 야드구분[" + szYdGp + "], 크레인 설비[" + szYD_EQP_ID + "]";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
//SJH03004					
					recPara.setField("JMS_TC_CD", 			szJMS_TC_CD);
					
					recPara.setField("YD_EQP_ID" , 			szYD_EQP_ID);
					recPara.setField("YD_WRK_PROG_STAT" , 	szSendYdWrkProgStat);
					
					if (!szJMS_TC_CD.equals("")){
						//EJB Method Call
						if( szEjbMethod.equals("procY5CrnWrkOrdReq") ) {
							ejbConn = new EJBConnector("default", "CoilCraneLdHdSeEJB", this);			
						} else{
							ejbConn = new EJBConnector("default", "CraneLdHdSeEJB", this);	
						}
						szRtnMsg = (String)ejbConn.trx(szEjbMethod, new Class[] { JDTORecord.class }, new Object[] { recPara });	

//						ydDelegate.sendMsg(recPara);
						
						
					} else{
						szLogMsg = "[JSP Session] " + szOperationName  + " 작업재지시 전문을 전송하지 않습니다(작업TC ID 없음)";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					}
					
					szLogMsg = "[JSP Session] " + szOperationName + " 작업재지시  - 리턴메세지 : [" + szRtnMsg +"] ";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}
        		
        		//------------------------------------------------------------------------
        		// 본래 권하위치가 차량 또는 대차에서 일반야드로 권하위치를 변경하는경우
        		//------------------------------------------------------------------------
        		// szOldStkPos 기존 권하위치 
        		if(szOldStkPos.length() >= 6){
        			szEqpGpBefo = szOldStkPos.substring(2,4);
        		}
        		
        		// 기존 설비구분이 차량/대차이고 , 신규 설비 구분이 그 차량/대차 작업이 아닌경우  
        		// 작업예약 ID를 Clear  한다.
        		if(szEqpGpBefo.equals(YdConstant.YD_EQP_GP_TCAR)||
        		   szEqpGpBefo.equals(YdConstant.YD_EQP_GP_PALLET)||
        		   szEqpGpBefo.equals(YdConstant.YD_EQP_GP_TRAILER)){
        			
        			if(!szEqpGpBefo.equals(szEqpGp)){
        				
        				//szYdWbookId - 현 스케줄의 작업예약 ID
        				//delWBookBefoCarOrTCar
        				recPara   = JDTORecordFactory.getInstance().create();
        				recPara.setField("YD_WBOOK_ID", szYdWbookId);
        				recPara.setField("YD_EQP_GP", szEqpGpBefo);
        				yddatautil.delWBookBefoCarOrTCar(recPara);
        			}
        		}
			}		
					
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);			
		}finally { }
		
		bool = true;
		
		szLogMsg = "[JSP Session] " + szOperationName  + " 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return new Boolean(bool);
	}	// end of updToPosFix
	
	
	/**
	 * 권하위치 변경 (크레인 상태관리화면)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return String
	 * @throws DAOException
	 */
	public String updCrnDnPrsFix(JDTORecord[] inDto) throws DAOException {
		
		int intRtnVal = 0;				
		String szMsg= null;
		String szMethodName = "updCrnDnPrsFix";		
		String szOperationName = "권하위치 변경";
		JDTORecord    recPara = null;
		JDTORecord    recTemp = null;
		JDTORecord    recSet = null;
		JDTORecord recStkLyr = null;
		
		String szStkPos   = null;
		String szStkColGp = null;
		String szStkBedNo = null;
		String szStkLyrNo = null;
		
		String szOldStkPos   = null;
		String szOldStkColGp = null;
		String szOldStkBedNo = null;
		String szOldStkLyrNo = null;
		
			
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
		YdCrnWrkMtlDao  ydCrnWrkMtlDao = new YdCrnWrkMtlDao();
		YdDelegate ydDelegate 	= new YdDelegate();
		
		JDTORecordSet    outRecSet  = null;
		JDTORecordSet    rsStkLyr  = null;
		
		String szJMS_TC_CD = null;
		String szEjbMethod = null;
		String szLogMsg = null;
		String szRtnMsg = null;
		String szYD_EQP_ID = null;
		EJBConnector ejbConn = null;
		
		try {
			
			szLogMsg = "JSP-SESSION [ 권하위치 변경 (크레인 상태관리화면)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
				// 1. 기존 To 위치 정보 와 변경 To 위치 정보 
				recPara   = JDTORecordFactory.getInstance().create();
				outRecSet  = JDTORecordFactory.getInstance().createRecordSet("YD");
				
				recPara.setField("YD_CRN_SCH_ID", yddatautil.setDataDefault(inDto[0].getField("YD_CRN_SCH_ID"), ""));				
				intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, outRecSet, 0);
				
				if(intRtnVal <1 )
				{
					szMsg ="[JSP Session]권하위치 변경 - 해당 스케줄 정보: "+inDto[0].getField("YD_CRN_SCH_ID")+"가 존재하지않습니다";
            		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NOTEXIST;
				}
				outRecSet.first();
				
				recTemp   = JDTORecordFactory.getInstance().create();				

				//1건 조회됨
				do{				
					recTemp = outRecSet.getRecord();
					
					szOldStkPos   = yddatautil.setDataDefault(recTemp.getFieldString("YD_DN_WO_LOC"),"");					
					szOldStkLyrNo = yddatautil.setDataDefault(recTemp.getFieldString("YD_DN_WO_LAYER"),"");
					
				}while(outRecSet.next());
								
				szStkPos = yddatautil.setDataDefault(inDto[0].getField("YD_DN_WO_LOC"), "");
				
				if ("".equals(szStkPos)){					
					//권하지시위치 정보가 없습니다.
					szMsg ="[JSP Session]권하위치 변경 - 변경 권하지시위치 정보가 없습니다.";
            		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            		return YdConstant.RETN_CD_FAILURE;
				}
				
				
				szStkLyrNo = yddatautil.setDataDefault(inDto[0].getField("YD_DN_WO_LAYER"), "");
				
				if ("".equals(szStkLyrNo)){
					
					//권하지시단 정보가 없습니다.
					szMsg ="[JSP Session]권하위치 변경 - 변경 권하지시단 정보가 없습니다.";
            		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					//return;
				}
				
				if(szStkPos.length() ==8)
				{
					szStkColGp = szStkPos.substring(0, 6); 
					szStkBedNo = szStkPos.substring(6, 8);
				}else{
					//변경 권하지시위치 정보의 길이가 맞지 않습니다 
					return YdConstant.RETN_CD_FAILURE;
				}
				
				
				if(szStkPos.equals(szOldStkPos)){
					szMsg ="[JSP Session]권하위치 지시위치가 변경되지 않았습니다.";
            		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					
            		return "지시위치가 변경되지 않았습니다";
					
				}
				
				
				
				// 신규 위치 적치단 정보
				
				recPara    = JDTORecordFactory.getInstance().create();
				recTemp    = outRecSet.getRecord();
				outRecSet  = JDTORecordFactory.getInstance().createRecordSet("YD");
				
				recPara.setField("YD_STK_COL_GP", szStkColGp);
				recPara.setField("YD_STK_BED_NO", szStkBedNo);
				
				intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 29);
				
				if (intRtnVal == 0){
					szStkLyrNo ="001";
				}
				else if ( intRtnVal >0 )
				{
					outRecSet.last();
					recTemp = outRecSet.getRecord();
					
					szStkLyrNo =ydDaoUtils.stringPlusInt(recTemp.getFieldString("YD_STK_LYR_NO"),1);
					
					
				}
					
				
				szMsg = "[JSP Session]권하위치 변경 - 신규위치정보 :"+ szStkColGp  + "-"+ szStkBedNo +":" +szStkLyrNo;				
				ydUtils.putLog(szSessionName,szMethodName,szMsg  , YdConstant.DEBUG);
				
				
				recPara   = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID", yddatautil.setDataDefault(inDto[0].getField("YD_CRN_SCH_ID"), ""));
				
				outRecSet  = JDTORecordFactory.getInstance().createRecordSet("YD");
				intRtnVal = ydCrnWrkMtlDao.getYdCrnwrkmtl(recPara, outRecSet, 1);
				
				if (intRtnVal < 1)
				{
					//해당 스케줄에 해당되는 재료가 없습니다.
					szMsg ="[JSP Session]권하위치 변경 - 해당 스케줄에 해당되는 재료가 없습니다.";
            		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NOTEXIST;
				}
				
				

				
				//기존 산적위치 정보가 올바르지 않거나 없을 경우는 기존산적위치를 삭제할 수없으므로 건너띈다.
				
				if( szOldStkPos.length() == 8)
				{	
					szOldStkColGp = szOldStkPos.substring(0, 6); 
					szOldStkBedNo = szOldStkPos.substring(6, 8);
					szMsg = "[JSP Session]권하위치 변경 - 기존위치정보 :"+ szOldStkColGp  + "-"+ szOldStkBedNo +":" +szOldStkLyrNo;				
					ydUtils.putLog(szSessionName,szMethodName,szMsg  , YdConstant.DEBUG);
					
					
					//실제로는 크레인작업재료의 개수만 필요함				
					outRecSet.first();
					for (int nLoop =0 ; nLoop<outRecSet.size();nLoop++ ){
						
						// 기존 지시위치 에 쌓여 있는 정보 Clear
						recSet = JDTORecordFactory.getInstance().create();
		                recSet.setField("YD_STK_COL_GP",       szOldStkColGp);    
		                recSet.setField("YD_STK_BED_NO",       szOldStkBedNo);   
		                //적치단 설정
		                recSet.setField("YD_STK_LYR_NO",       ydDaoUtils.stringPlusInt(szOldStkLyrNo, nLoop)) ;	                
		                recSet.setField("YD_STK_LYR_MTL_STAT", "E");
		                recSet.setField("STL_NO",              "");
		                intRtnVal = ydStkLyrDao.updYdStklyr(recSet, 0);
		            	if (intRtnVal < 1)
						{
		            		szMsg ="[JSP Session]권하위치 변경 - 기존 지시위치 에 쌓여 있는 정보 Clear 실패";
		            		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							return YdConstant.RETN_CD_FAILURE;
						}
					}		
				}
				
							
				outRecSet.first();
				for (int nLoop =0 ; nLoop<outRecSet.size(); nLoop++ ){
					
					// 신규위치에 정보를 Setting
					recSet = JDTORecordFactory.getInstance().create();
					recTemp =JDTORecordFactory.getInstance().create();
					
					recTemp = outRecSet.getRecord(nLoop);					
					recSet.setField("YD_STK_COL_GP",       szStkColGp);    
		            recSet.setField("YD_STK_BED_NO",       szStkBedNo);   
		            recSet.setField("YD_STK_LYR_NO",       ydDaoUtils.stringPlusInt(szStkLyrNo, nLoop)) ;
		            recSet.setField("YD_STK_LYR_MTL_STAT", "D");
	                recSet.setField("STL_NO",              recTemp.getField("STL_NO"));
	                
	                intRtnVal = ydStkLyrDao.updYdStklyr(recSet, 0);
	            	if (intRtnVal < 1)
					{
						//신규위치에 정보를 Setting 실패
	            		szMsg ="[JSP Session]권하위치 변경 - 신규위치에 정보를 Setting 실패";
	            		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            		return YdConstant.RETN_CD_FAILURE;
					}
				}
				
				
				// 권하지시 위치의 적치열/적치베드 / 적치단 정보로 해당 적치단의 위치값을 읽어온다.
				recPara   = JDTORecordFactory.getInstance().create();
				
				recPara.setField("YD_STK_COL_GP",       szStkColGp);    
				recPara.setField("YD_STK_BED_NO",       szStkBedNo);   
				recPara.setField("YD_STK_LYR_NO",       szStkLyrNo) ;
	            
	            recStkLyr = JDTORecordFactory.getInstance().create();
	            rsStkLyr  = JDTORecordFactory.getInstance().createRecordSet("YD");
	       
	            
	            intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsStkLyr, 0);
	            
	            
	            
	            boolean bTest = false;  //적치단 좌표 조회가능 유무
	            if(intRtnVal < 0 ){
	            	
	            	szMsg ="[JSP Session - " + szOperationName +"[ - 적치단 정보 조회 ERROR";
            		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	            	
	            } 
	            
	            else if (intRtnVal == 0){
	            	szMsg ="[JSP Session - " + szOperationName +"[ - 적치단 정보가 없습니다.";
            		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	            }
				
	            else{ 
	            	
	            	szMsg ="[JSP Session - " + szOperationName +"[ - 적치단 조회 완료(좌표)";
            		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            		
	            	bTest = true;
	            	rsStkLyr.first();
	            	recStkLyr = rsStkLyr.getRecord();
	            }
	            
		
				// 권하위치 정보 스케줄 정보에서 변경
				recPara   = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID", yddatautil.setDataDefault(inDto[0].getField("YD_CRN_SCH_ID"), ""));
				recPara.setField("YD_DN_WO_LOC", yddatautil.setDataDefault(inDto[0].getField("YD_DN_WO_LOC"), ""));
				recPara.setField("YD_DN_WO_LAYER", szStkLyrNo);
				
				if(bTest){
					
					recPara.setField("YD_DN_WO_LOC_XAXIS", ydDaoUtils.paraRecChkNull(recStkLyr, "YD_STK_LYR_XAXIS"));
					recPara.setField("YD_DN_WO_LOC_YAXIS", ydDaoUtils.paraRecChkNull(recStkLyr, "YD_STK_LYR_YAXIS"));
					recPara.setField("YD_DN_WO_LOC_ZAXIS", ydDaoUtils.paraRecChkNull(recStkLyr, "YD_STK_LYR_ZAXIS"));
				}
				
				ydUtils.displayRecord(szOperationName, recPara);
				
				intRtnVal = ydCrnSchDao.updYdCrnsch(recPara, 0);
				
				if (intRtnVal < 1)
				{
					//권하위치 스케줄 정보 변경 실패
					szMsg ="[JSP Session]권하위치 변경 - 권하위치 스케줄 정보 변경 실패";
            		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
            		return YdConstant.RETN_CD_FAILURE;
				}
				 
				// 스케줄 변경 후 제원 위치정보를 맞춰준다.
				szLogMsg =  "[JSP Session] " + szOperationName +  "스케줄 변경 후 제원 위치정보 세팅 호출";
        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
        		
        		boolean lb_updYdCrnBed = false;        		
        		lb_updYdCrnBed = YdUtils.updYdCrnschBedData(recPara);
        		
        		if(!lb_updYdCrnBed){
        			szLogMsg =  "[JSP Session] " + szOperationName +  "스케줄 변경 후 제원 위치정보 세팅 실패";
            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
        		}
        		
				//권하위치 변경  후  작업 재지시 
				String szYdGp ="";
			
				
				szYdGp = inDto[0].getFieldString("YD_GP");			
				
				recPara = JDTORecordFactory.getInstance().create();
				
				if (YdConstant.YD_GP_C_SLAB_YARD.equals(szYdGp)){						//C연주 슬라브 야드[A]
					//recPara.setField("JMS_TC_CD","YDYDJ640");
					szJMS_TC_CD = "YDYDJ640";
					szEjbMethod = "procY1CrnWrkOrdReq";
				}else if(YdConstant.YD_GP_PORT_SLAB_YARD.equals(szYdGp)){			//항만슬라브야드 기능추가 - 2015.12.30 LeeJY
					szJMS_TC_CD = "YDYDJ640";
					szEjbMethod = "procY1CrnWrkOrdReq";
				}else if(YdConstant.YD_GP_A_PLATE_SLAB_YARD.equals(szYdGp)){			//A후판 슬라브야드[D]
					//recPara.setField("JMS_TC_CD","YDYDJ641");
					szJMS_TC_CD = "YDYDJ641";
					szEjbMethod = "procY3CrnWrkOrdReq";
				}else if(YdConstant.YD_GP_PLATE_GDS_YARD.equals(szYdGp)){				//후판제품야드 [K]
					//recPara.setField("JMS_TC_CD","YDYDJ642");
					szJMS_TC_CD = "YDYDJ642";
					szEjbMethod = "procY4CrnWrkOrdReq";
				}else if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYdGp)){				//2후판제품야드 [T] - 2013.01.04 추가 (3기)
					szJMS_TC_CD = "YDYDJ642";
					szEjbMethod = "procY4CrnWrkOrdReq";
				}else if(YdConstant.YD_GP_C_HR_COIL_MATL_YARD.equals(szYdGp)){			//C열연 코일야드[H]
					//recPara.setField("JMS_TC_CD","YDYDJ643");
					szJMS_TC_CD = "YDYDJ643";
					szEjbMethod = "procY5CrnWrkOrdReq";
				}else if(YdConstant.YD_GP_C_HR_COIL_GDS_YARD.equals(szYdGp)){			//C열연 제품야드[J]
					//recPara.setField("JMS_TC_CD","YDYDJ643");
					szJMS_TC_CD = "YDYDJ643";
					szEjbMethod = "procY5CrnWrkOrdReq";
				}else if(YdConstant.YD_GP_INTGR_YARD.equals(szYdGp)){					//통합제품야드[S]
					//recPara.setField("JMS_TC_CD","YDYDJ643");
					szJMS_TC_CD = "YDYDJ644";
					szEjbMethod = "procY0CrnWrkOrdReq";
				}
			
				szYD_EQP_ID = inDto[0].getFieldString("YD_EQP_ID");
				
				szLogMsg = "[JSP Session]권하위치 변경  - 야드구분[" + szYdGp + "], 크레인 설비[" + szYD_EQP_ID + "]";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
//SJH03004				
				recPara.setField("JMS_TC_CD", 			szJMS_TC_CD);
				recPara.setField("YD_EQP_ID" , 			szYD_EQP_ID);
				recPara.setField("YD_WRK_PROG_STAT" , 	inDto[0].getFieldString("YD_WRK_PROG_STAT"));
				                   
				ydUtils.displayRecord(szOperationName, recPara);
				//JMS Call
				//ydDelegate.sendMsg(recPara);
				
				//EJB Method Call
//sjhkim
				if( szEjbMethod.equals("procY5CrnWrkOrdReq") ) {
					ejbConn = new EJBConnector("default", "CoilCraneLdHdSeEJB", this);					
				} else{
					ejbConn = new EJBConnector("default", "CraneLdHdSeEJB", this);		
				}
				
				szRtnMsg = (String)ejbConn.trx(szEjbMethod, new Class[] { JDTORecord.class }, new Object[] { recPara });
	
				if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {													//성공
					szLogMsg = "[JSP Session]권하위치 변경 성공 - 크레인 작업지시";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}else if( szRtnMsg.equals(YdConstant.RETN_CRN_SCH_PROH) ) {											//스케줄금지 상태
					szLogMsg = "[JSP Session]권하위치 변경 성공 - 크레인 작업지시 : 스케줄금지 상태";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}else if( szRtnMsg.equals(YdConstant.RETN_CD_TC_ERROR) ) {											//전문 에러
					szLogMsg = "[JSP Session]권하위치 변경 성공 - 크레인 작업지시 : 전문 에러";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}else if( szRtnMsg.equals(YdConstant.RETN_CRN_NO_SCH) ) {											//크레인스케쥴이 존재하지 않음
					szLogMsg = "[JSP Session]권하위치 변경 성공 - 크레인 작업지시 : 크레인스케쥴이 존재하지 않음";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}else if( szRtnMsg.equals(YdConstant.RETN_CRN_NO_WRK) ) {											//작업예약이 더 이상 존재하지 않음
					szLogMsg = "[JSP Session]권하위치 변경 성공 - 크레인 작업지시 : 작업예약이 더 이상 존재하지 않음";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}else if( szRtnMsg.equals(YdConstant.RETN_CD_FAILURE) ) {											//실패
					szLogMsg = "[JSP Session]권하위치 변경 실패 - 크레인 작업지시 실패";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				}
				
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.			
			szLogMsg = "[JSP Session]권하위치 변경 에러발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		szLogMsg = "JSP-SESSION [ 권하위치 변경 (크레인 상태관리화면)] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return szRtnMsg;
	}	// end of updCrnDnPrsFix
	
	
	
	
	
	
	/**
	 *  스케줄 기동 (스케줄기동관리 화면)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	
	
	public void schStart(JDTORecord[] inDto) throws DAOException {
		JDTORecord recPara    = null;
		YdDelegate ydDelegate = new YdDelegate();
		String szYdGp         = null;
		

		String szMethodName   = "schStart";		
		String szLogMsg       = "";
		String szTemp         = "";
		
		try{
			
			szLogMsg = "JSP-SESSION [스케줄 기동 (스케줄기동관리 화면)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			for(int x=0;x<inDto.length;x++){
				//TC CODE
				
				 
//				szYdGp = inDto[x].getFieldString("YD_GP");		
				szTemp = inDto[x].getFieldString("YD_SCH_CD");	
				if(szTemp != null && !szTemp.equals("")){
					
					szYdGp = szTemp.substring(0, 1);
					
					szLogMsg = "===================================================================================";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					szLogMsg = "스케쥴 기동 x(" + x + ") : YD_SCH_CD(" + szTemp + ") YD_GP(" + szYdGp + ")";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					ydUtils.displayRecord(szMethodName, inDto[x]);
					szLogMsg = "===================================================================================";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					
					recPara = JDTORecordFactory.getInstance().create();
					if (YdConstant.YD_GP_C_SLAB_YARD.equals(szYdGp)){
						//C연주 슬라브 야드 
						recPara.setField("JMS_TC_CD","YDYDJ500");
						
					} else if(YdConstant.YD_GP_PORT_SLAB_YARD.equals(szYdGp)){  //항만슬라브야드 기능추가 - 2015.12.30 LeeJY
						//항만 슬라브야드
						recPara.setField("JMS_TC_CD","YDYDJ500");
						
					} else if(YdConstant.YD_GP_A_PLATE_SLAB_YARD.equals(szYdGp)){
						//A후판 슬라브야드
						recPara.setField("JMS_TC_CD","YDYDJ503");
						
					} else if(YdConstant.YD_GP_PLATE_GDS_YARD.equals(szYdGp)){
						// 후판제품 야드
						recPara.setField("JMS_TC_CD","YDYDJ506");
					} else if(YdConstant.YD_GP_C_HR_COIL_GDS_YARD.equals(szYdGp)){
						// 코일제품 야드
						recPara.setField("JMS_TC_CD","YDYDJ509");
					}  else if(YdConstant.YD_GP_C_HR_COIL_MATL_YARD.equals(szYdGp)){
						// 코일소재야드
						recPara.setField("JMS_TC_CD","YDYDJ509");
					} else if(YdConstant.YD_GP_INTGR_YARD.equals(szYdGp)){
						// 통합야드
						recPara.setField("JMS_TC_CD","YDYDJ512");
						//procY0CrnSchMain
					}
									
					recPara.setField("YD_SCH_CD", inDto[x].getField("YD_SCH_CD"));
					//작업크레인 정보를 설비에 넣어준다. 
					
					if(!ydDaoUtils.paraRecChkNull(inDto[x],"YD_EQP_ID").equals("")){
						recPara.setField("YD_EQP_ID", inDto[x].getField("YD_EQP_ID"));
					}else if(!ydDaoUtils.paraRecChkNull(inDto[x],"YD_WRK_CRN").equals("")) {
						recPara.setField("YD_EQP_ID", inDto[x].getField("YD_WRK_CRN"));
					}
					
					ydUtils.displayRecord(szMethodName, recPara);
					szLogMsg = "===================================================================================";
									
					ydDelegate.sendMsg(recPara);
				}
			}
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
		szLogMsg = "JSP-SESSION [스케줄 기동 (스케줄기동관리 화면)] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
	}
	
	
	/**
	 *  차량번호 LIST
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCarNoList(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara    = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		int intGp = 0;
		

		String szMethodName="getCarNoList";		
		String szLogMsg = "";
		
		YdCarSchDao ydCarSchDao = new YdCarSchDao();
		try {
			
			szLogMsg = "JSP-SESSION [차량번호 LIST]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			recPara.setField("YD_GP",  yddatautil.setDataDefault(inDto.getField("YD_GP"), ""));
		     
			/* com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarNoList */

			//select distinct(A.CAR_NO)
			// from USRYDA.TB_YD_CARSCH  A,
			//      USRYDA.TB_YD_WRKBOOK B
			//where A.CAR_NO = B.CAR_NO
			//  and B.YD_GP  = :V_YD_GP
			//order BY A.CAR_NO
//PIDEV_S :병행가동용:PI_YD			
			recPara.setField("PI_YD",  yddatautil.setDataDefault(inDto.getField("YD_GP"), ""));
			   
			intGp = ydCarSchDao.getYdCarsch(recPara, outRecSet, 33);
			

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		szLogMsg = "JSP-SESSION [차량번호 LIST]끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return outRecSet;
	}//end of getCarNoList
	
	
	
	/**
	 *  차량 상차 정보 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCarLiftInfo(JDTORecord inDto) throws DAOException {
		
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecord       recTemp         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");	
		JDTORecord       recCarProgStat  = null; 
		
		int nRtnVal = 0;
		String szMsg = "";
		String szMethodName = "getCarLiftInfo";
			
		
		String szCarProgStat = null;
		try {
			
			szMsg = "JSP-SESSION [차량 상차 정보 조회]시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
			
			recPara.setField("CAR_NO",    	 yddatautil.setDataDefault(inDto.getField("CAR_NO"), ""));
			recPara.setField("TRN_EQP_CD",   yddatautil.setDataDefault(inDto.getField("CAR_NO"), ""));
			
			//차량 번호로 차량 스케줄 및 차량 진행 상태 코드 값을 읽어온다. 
			
			
			YdCarSchDao ydCarSchDao = new YdCarSchDao();
			nRtnVal = ydCarSchDao.getYdCarsch(recPara, outRecSet, 10);
			
			if (nRtnVal < 0){
				//조회 ERROR 
				szMsg ="차량스케줄에서 조회시 에러";
        		ydUtils.putLog("YdJspCommonSeEJBBean",szMethodName,szMsg  , YdConstant.ERROR);
				return outRecSet;
			}else if(nRtnVal == 0){				
				// 조회된 정보가 없을 경우 
				szMsg ="차량스케줄에서 조회된 정보가 없습니다!!";
        		ydUtils.putLog("YdJspCommonSeEJBBean",szMethodName,szMsg  , YdConstant.INFO);				
				return outRecSet;
			}
			
			outRecSet.first();			
		    recTemp = outRecSet.getRecord();
			
			
			// 차량 진행 상태 코드 값이 '1','2',(상차출발, 상차도착) 인 경우
		    
			
			szCarProgStat  = recTemp.getFieldString("YD_CAR_PROG_STAT");
			
			//******************************
			// 2개의 쿼리는 컬럼명을 동일하게 하여 읽어올수 있도록 한다.
			//******************************
			szMsg ="차량 진행상태는 : "+ szCarProgStat +" 입니다 ";
    		ydUtils.putLog("YdJspCommonSeEJBBean",szMethodName,szMsg  , YdConstant.DEBUG);
			
			
    		recPara         = JDTORecordFactory.getInstance().create();
			outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
			
			if ("1".equals(szCarProgStat) || "2".equals(szCarProgStat)){
				YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
				
				//차량 스케줄에 상차 작업예약 ID 로 작업예약 재료 정보 조회를 한다.
				
				if(ydDaoUtils.paraRecChkNull(recTemp,"YD_CARLD_WRK_BOOK_ID").trim().equals("")){
					
					szMsg ="작업예약 ID가 없습니다( 차량진도코드가 : 1, 2 경우) ";
	        		ydUtils.putLog("YdJspCommonSeEJBBean",szMethodName,szMsg  , YdConstant.DEBUG);
	        	
	        		return outRecSet; 
				}
				
				recPara.setField("YD_WBOOK_ID", ydDaoUtils.paraRecChkNull(recTemp,"YD_CARLD_WRK_BOOK_ID"));					
				recPara.setField("PAGE_CNT1", inDto.getField("PAGE_NO"));
				recPara.setField("PAGE_CNT2", inDto.getField("PAGE_NO"));
				recPara.setField("ROW_CNT1",  inDto.getField("ROWCOUNT"));
				recPara.setField("ROW_CNT2",  inDto.getField("ROWCOUNT"));
				
				nRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recPara, outRecSet, 16);
				
			} else {		
				YdCarFtmvMtlDao ydCarFtmvMtlDao = new YdCarFtmvMtlDao();
				
				// 차량 진행 상태 코드값이  그 이외인 경우 는 차량  이송재료 정보를 읽어온다.
				recPara.setField("YD_CAR_SCH_ID", ydDaoUtils.paraRecChkNull(recTemp,"YD_CAR_SCH_ID"));						
				recPara.setField("PAGE_CNT1", inDto.getField("PAGE_NO"));
				recPara.setField("PAGE_CNT2", inDto.getField("PAGE_NO"));
				recPara.setField("ROW_CNT1",  inDto.getField("ROWCOUNT"));
				recPara.setField("ROW_CNT2",  inDto.getField("ROWCOUNT"));
				
				nRtnVal = ydCarFtmvMtlDao.getYdCarftmvmtl(recPara, outRecSet, 6);				
			}
			
			if (nRtnVal <= 0 ){
				//ERROR 처리 
				szMsg ="상차 조회된 정보가 없습니다!!";
        		ydUtils.putLog("YdJspCommonSeEJBBean",szMethodName,szMsg  , YdConstant.ERROR);
        		return outRecSet;
				
			}	
			
			
			// 데이터 존재시 첫번째 레코드 위치에 차량진도코드를 보내준다.
			
			outRecSet.first();
			recCarProgStat = outRecSet.getRecord(0);
			recCarProgStat.setField("CAR_PROG_STAT", szCarProgStat);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		szMsg = "JSP-SESSION [차량 상차 정보 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
		
		
		return outRecSet;
	}//end of getCarLiftInfo
	
	

	/**
	 *  차량 상차 정보 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCarLiftInfo_plateGds(JDTORecord inDto) throws DAOException {
		
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecord       recTemp         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");	
		JDTORecord       recCarProgStat  = null; 
		
		int nRtnVal = 0;
		String szMsg = "";
		String szMethodName = "getCarLiftInfo_plateGds";
			
		
		String szCarProgStat = null;
		try {
			
			szMsg = "JSP-SESSION [차량 상차 정보 조회]시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
			
			recPara.setField("CAR_NO",    	 yddatautil.setDataDefault(inDto.getField("CAR_NO"), ""));
			recPara.setField("TRN_EQP_CD",   yddatautil.setDataDefault(inDto.getField("CAR_NO"), ""));
			
			//차량 번호로 차량 스케줄 및 차량 진행 상태 코드 값을 읽어온다. 
			
			
			YdCarSchDao ydCarSchDao = new YdCarSchDao();
			nRtnVal = ydCarSchDao.getYdCarsch(recPara, outRecSet, 10);
			
			if (nRtnVal < 0){
				//조회 ERROR 
				szMsg ="차량스케줄에서 조회시 에러";
        		ydUtils.putLog("YdJspCommonSeEJBBean",szMethodName,szMsg  , YdConstant.ERROR);
				return outRecSet;
			}else if(nRtnVal == 0){				
				// 조회된 정보가 없을 경우 
				szMsg ="차량스케줄에서 조회된 정보가 없습니다!!";
        		ydUtils.putLog("YdJspCommonSeEJBBean",szMethodName,szMsg  , YdConstant.INFO);				
				return outRecSet;
			}
			
			outRecSet.first();			
		    recTemp = outRecSet.getRecord();
			
			
			// 차량 진행 상태 코드 값이 '1','2',(상차출발, 상차도착) 인 경우
		    
			
			szCarProgStat  = recTemp.getFieldString("YD_CAR_PROG_STAT");
			
			//******************************
			// 2개의 쿼리는 컬럼명을 동일하게 하여 읽어올수 있도록 한다.
			//******************************
			szMsg ="차량 진행상태는 : "+ szCarProgStat +" 입니다 ";
    		ydUtils.putLog("YdJspCommonSeEJBBean",szMethodName,szMsg  , YdConstant.DEBUG);
			
			
    		recPara         = JDTORecordFactory.getInstance().create();
			outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
			
			if ("1".equals(szCarProgStat) || "2".equals(szCarProgStat)){
				YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
				
				//차량 스케줄에 상차 작업예약 ID 로 작업예약 재료 정보 조회를 한다.
				
				if(ydDaoUtils.paraRecChkNull(recTemp,"YD_CARLD_WRK_BOOK_ID").trim().equals("")){
					
					szMsg ="작업예약 ID가 없습니다( 차량진도코드가 : 1, 2 경우) ";
	        		ydUtils.putLog("YdJspCommonSeEJBBean",szMethodName,szMsg  , YdConstant.DEBUG);
	        		
	        		if(ydDaoUtils.paraRecChkNull(recTemp,"YD_CAR_USE_GP").trim().equals("G")&&
	        		   ydDaoUtils.paraRecChkNull(recTemp,"SPOS_WLOC_CD").trim().equals("DKY30")){
						
	        			
						szMsg ="차량진도코드가 : 1, 2 경우 그리고 후판제품 출하차량이면 출하지시일자/순번으로 가져오기 ";
		        		ydUtils.putLog("YdJspCommonSeEJBBean",szMethodName,szMsg  , YdConstant.DEBUG);
		        		
		        		recPara.setField("TRANS_ORD_DATE",  ydDaoUtils.paraRecChkNull(recTemp,"TRANS_ORD_DATE").trim());
		        		recPara.setField("TRANS_ORD_SEQNO", ydDaoUtils.paraRecChkNull(recTemp,"TRANS_ORD_SEQNO").trim());
		        		recPara.setField("PAGE_CNT1", inDto.getField("PAGE_NO"));
		        		recPara.setField("PAGE_CNT2", inDto.getField("PAGE_NO"));
						recPara.setField("ROW_CNT1",  inDto.getField("ROWCOUNT"));
						recPara.setField("ROW_CNT2",  inDto.getField("ROWCOUNT"));
						
//PIDEV_S :병행가동용:PI_YD
		                recPara.setField("PI_YD",       "T"); 
						nRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recPara, outRecSet, 45);
								        		
	        		}else{
	    				return outRecSet;
	        		}
				}else{
				
					recPara.setField("YD_WBOOK_ID", ydDaoUtils.paraRecChkNull(recTemp,"YD_CARLD_WRK_BOOK_ID"));					
					recPara.setField("PAGE_CNT1", inDto.getField("PAGE_NO"));
					recPara.setField("PAGE_CNT2", inDto.getField("PAGE_NO"));
					recPara.setField("ROW_CNT1",  inDto.getField("ROWCOUNT"));
					recPara.setField("ROW_CNT2",  inDto.getField("ROWCOUNT"));
					
//PIDEV_S :병행가동용:PI_YD
                    recPara.setField("PI_YD",       "T"); 
					nRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recPara, outRecSet, 41);
				}
			} else {		
				YdCarFtmvMtlDao ydCarFtmvMtlDao = new YdCarFtmvMtlDao();
				
				// 차량 진행 상태 코드값이  그 이외인 경우 는 차량  이송재료 정보를 읽어온다.
				recPara.setField("YD_CAR_SCH_ID", ydDaoUtils.paraRecChkNull(recTemp,"YD_CAR_SCH_ID"));						
				recPara.setField("PAGE_CNT1", inDto.getField("PAGE_NO"));
				recPara.setField("PAGE_CNT2", inDto.getField("PAGE_NO"));
				recPara.setField("ROW_CNT1",  inDto.getField("ROWCOUNT"));
				recPara.setField("ROW_CNT2",  inDto.getField("ROWCOUNT"));
				
				//PIDEV_S :병행가동용:PI_YD
                recPara.setField("PI_YD",       "T"); 
                nRtnVal = ydCarFtmvMtlDao.getYdCarftmvmtl(recPara, outRecSet, 11);				
			}
			
			if (nRtnVal <= 0 ){
				//ERROR 처리 
				szMsg ="상차 조회된 정보가 없습니다!!";
        		ydUtils.putLog("YdJspCommonSeEJBBean",szMethodName,szMsg  , YdConstant.ERROR);
        		return outRecSet;
				
			}	
			
			
			// 데이터 존재시 첫번째 레코드 위치에 차량진도코드를 보내준다.
			
			outRecSet.first();
			recCarProgStat = outRecSet.getRecord(0);
			recCarProgStat.setField("CAR_PROG_STAT", szCarProgStat);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		szMsg = "JSP-SESSION [차량 상차 정보 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
		
		
		return outRecSet;
	}//end of getCarLiftInfo_plateGds
	
	
	
	/**
	 *  차량 상차 정보 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCarLiftInfo_BCoil(JDTORecord inDto) throws DAOException {
		
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecord       recTemp         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");		
		int nRtnVal = 0;
		String szMsg = "";
		String szMethodName = "getCarLiftInfo_BCoil";
			
		
		String szCarProgStat = null;
		try {
			
			szMsg = "JSP-SESSION [차량 상차 정보 조회]시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
			
			recPara.setField("CAR_NO",    	 yddatautil.setDataDefault(inDto.getField("CAR_NO"), ""));
			recPara.setField("TRN_EQP_CD",   yddatautil.setDataDefault(inDto.getField("CAR_NO"), ""));  
			
			//차량 번호로 차량 스케줄 및 차량 진행 상태 코드 값을 읽어온다. 
			
			
			YdCarSchDao ydCarSchDao = new YdCarSchDao();
			nRtnVal = ydCarSchDao.getYdCarsch(recPara, outRecSet, 10);
			
			
			if (nRtnVal < 1){
				//조회된 정보가 없을 경우 
				szMsg ="차량스케줄에서 조회시 에러 발생!!!!!!";
        		ydUtils.putLog("YdJspCommonSeEJBBean",szMethodName,szMsg  , YdConstant.ERROR);				
				return outRecSet;
			
			} else if (nRtnVal == 0){
				//조회된 정보가 없을 경우 
				szMsg ="차량스케줄에서 조회된 정보가 없습니다!!";
        		ydUtils.putLog("YdJspCommonSeEJBBean",szMethodName,szMsg  , YdConstant.INFO);				
				return outRecSet;
				
			}
			
			outRecSet.first();			
		    
			recTemp = outRecSet.getRecord();
			
						
			
			
			// 차량 진행 상태 코드 값이 '1','2','3' 인 경우
			
			szCarProgStat  = recTemp.getFieldString("YD_CAR_PROG_STAT");
			
			//******************************
			// 2개의 쿼리는 컬럼명을 동일하게 하여 읽어올수 있도록 한다.
			//******************************
			szMsg ="차량 진행상태는 : "+ szCarProgStat +" 입니다 ";
    		ydUtils.putLog("YdJspCommonSeEJBBean",szMethodName,szMsg  , YdConstant.DEBUG);
			
			
    		recPara         = JDTORecordFactory.getInstance().create();
			outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
			
		/*	if ("1".equals(szCarProgStat) || "2".equals(szCarProgStat)||"3".equals(szCarProgStat)){
				YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
				
				//차량 스케줄에 상차 작업예약 ID 로 작업예약 재료 정보 조회를 한다.
			
			
				recPara.setField("YD_WBOOK_ID", ydDaoUtils.paraRecChkNull(recTemp,"YD_CARLD_WRK_BOOK_ID"));
				recPara.setField("PAGE_CNT1", inDto.getField("PAGE_NO"));
				recPara.setField("PAGE_CNT2", inDto.getField("PAGE_NO"));
				recPara.setField("ROW_CNT1",  inDto.getField("ROWCOUNT"));
				recPara.setField("ROW_CNT2",  inDto.getField("ROWCOUNT"));
				
				nRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recPara, outRecSet, 20);   //////////////////////////////
				
			} else { */
			 
				YdCarFtmvMtlDao ydCarFtmvMtlDao = new YdCarFtmvMtlDao();
				
				// 차량 진행 상태 코드값이  그 이외인 경우 는 차량  이송재료 정보를 읽어온다.
				recPara.setField("YD_CAR_SCH_ID", ydDaoUtils.paraRecChkNull(recTemp,"YD_CAR_SCH_ID"));				
				recPara.setField("PAGE_CNT1", inDto.getField("PAGE_NO"));
				recPara.setField("PAGE_CNT2", inDto.getField("PAGE_NO"));
				recPara.setField("ROW_CNT1",  inDto.getField("ROWCOUNT"));
				recPara.setField("ROW_CNT2",  inDto.getField("ROWCOUNT"));
				
				nRtnVal = ydCarFtmvMtlDao.getYdCarftmvmtl(recPara, outRecSet, 8);				
		//	}
			
				if (nRtnVal < 0 ){
					//ERROR 처리 
					szMsg ="상차 조회된 정보가 없습니다!!";
	        		ydUtils.putLog("YdJspCommonSeEJBBean",szMethodName,szMsg  , YdConstant.ERROR);
					
				}
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		szMsg = "JSP-SESSION [차량 상차 정보 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
		
		return outRecSet;
	}//end of getCarLiftInfo_BCoil
	
	/**
	 *  차량 상차 정보 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCarLiftInfo_BSlab(JDTORecord inDto) throws DAOException {
		
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecord       recTemp         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");		
		int nRtnVal = 0;
		String szMsg = "";
		String szMethodName = "getCarLiftInfo_BSlab";
			
		
		String szCarProgStat = null;
		try {
			
			szMsg = "JSP-SESSION [차량 상차 정보 조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
			
			recPara.setField("CAR_NO",       ydDaoUtils.paraRecChkNull(inDto, "CAR_NO"));  
			recPara.setField("TRN_EQP_CD",   ydDaoUtils.paraRecChkNull(inDto, "CAR_NO"));    
			
			//차량 번호로 차량 스케줄 및 차량 진행 상태 코드 값을 읽어온다. 
			
			
			YdCarSchDao ydCarSchDao = new YdCarSchDao();
			
			nRtnVal = ydCarSchDao.getYdCarsch(recPara, outRecSet, 10);
			
			
			if (nRtnVal < 1){				 
				szMsg ="차량스케줄에서 조회시 에러 발생!!!!!!!!!!!!!!";
        		ydUtils.putLog("YdJspCommonSeEJBBean",szMethodName,szMsg  , YdConstant.ERROR);				
				return outRecSet;
			}else if (nRtnVal == 0){				 
				szMsg ="차량스케줄에서 조회된 데이터가 없습니다";
        		ydUtils.putLog("YdJspCommonSeEJBBean",szMethodName,szMsg  , YdConstant.INFO);				
				return outRecSet;
			}
			
			outRecSet.first();
			
			recTemp = outRecSet.getRecord();
			
			// 차량 진행 상태 코드 값이 '1','2','3' 인 경우
			
			szCarProgStat  = recTemp.getFieldString("YD_CAR_PROG_STAT");
			
			//******************************
			// 2개의 쿼리는 컬럼명을 동일하게 하여 읽어올수 있도록 한다.
			//******************************
			szMsg ="차량 진행상태는 : "+ szCarProgStat +" 입니다 ";
    		ydUtils.putLog("YdJspCommonSeEJBBean",szMethodName,szMsg  , YdConstant.DEBUG);
    		
    		recPara         = JDTORecordFactory.getInstance().create();
			outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
			
		/*	if ("1".equals(szCarProgStat) || "2".equals(szCarProgStat)||"3".equals(szCarProgStat)){
				YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
				
				//차량 스케줄에 상차 작업예약 ID 로 작업예약 재료 정보 조회를 한다.
			
			
				recPara.setField("YD_WBOOK_ID", recTemp.getFieldString("YD_CARLD_WRK_BOOK_ID"));
				                     
				
				recPara.setField("PAGE_CNT1", inDto.getField("PAGE_NO"));
				recPara.setField("PAGE_CNT2", inDto.getField("PAGE_NO"));
				recPara.setField("ROW_CNT1",  inDto.getField("ROWCOUNT"));
				recPara.setField("ROW_CNT2",  inDto.getField("ROWCOUNT"));
				
				nRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recPara, outRecSet, 21); 
				
			} else { */		
				YdCarFtmvMtlDao ydCarFtmvMtlDao = new YdCarFtmvMtlDao();
				
		 		// 차량 진행 상태 코드값이  그 이외인 경우 는 차량  이송재료 정보를 읽어온다.
				recPara.setField("YD_CAR_SCH_ID", recTemp.getFieldString("YD_CAR_SCH_ID"));
				recPara.setField("PAGE_CNT1", inDto.getField("PAGE_NO"));
				recPara.setField("PAGE_CNT2", inDto.getField("PAGE_NO"));
				recPara.setField("ROW_CNT1",  inDto.getField("ROWCOUNT"));
				recPara.setField("ROW_CNT2",  inDto.getField("ROWCOUNT"));
				
				nRtnVal = ydCarFtmvMtlDao.getYdCarftmvmtl(recPara, outRecSet, 9);				
			//}
			
			if (nRtnVal < 0 ){
				//ERROR 처리 
				szMsg ="상차 조회된 정보가 없습니다!!";
        		ydUtils.putLog("YdJspCommonSeEJBBean",szMethodName,szMsg  , YdConstant.ERROR);
				
			}	
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		szMsg = "JSP-SESSION [차량 상차 정보 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return outRecSet;
	}//end of getCarLiftInfo_BSlab
	
	
	
	/**
	 *  차량 상차 정보 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCarLiftInfo_ASlab(JDTORecord inDto) throws DAOException {
		
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecord       recTemp         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");		
		int nRtnVal = 0;
		String szMsg = "";
		String szMethodName = "getCarLiftInfo_ASlab";
			
		
		String szCarProgStat = null;
		try {
			
			szMsg = "JSP-SESSION [차량 상차 정보 조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			recPara.setField("CAR_NO",		 ydDaoUtils.paraRecChkNull(inDto, "CAR_NO"));
			recPara.setField("TRN_EQP_CD",   ydDaoUtils.paraRecChkNull(inDto, "CAR_NO"));    	 
			
			//차량 번호로 차량 스케줄 및 차량 진행 상태 코드 값을 읽어온다. 
			
			
			YdCarSchDao ydCarSchDao = new YdCarSchDao();
			
			nRtnVal = ydCarSchDao.getYdCarsch(recPara, outRecSet, 10);
			
			
			if (nRtnVal < 1){
				//조회시 에러 발생
				szMsg ="차량스케줄에서 조회시 ERROR !!!!!!";
        		ydUtils.putLog("YdJspCommonSeEJBBean",szMethodName,szMsg  , YdConstant.ERROR);				
				return outRecSet;
			} else if (nRtnVal == 0){
				//조회시 에러 발생
				szMsg ="차량스케줄에서 조회된 정보가 없습니다!!";
        		ydUtils.putLog("YdJspCommonSeEJBBean",szMethodName,szMsg  , YdConstant.INFO);				
				return outRecSet;
			} 
			
			outRecSet.first();
			recTemp = outRecSet.getRecord();
			
			
			
			// 차량 진행 상태 코드 값이 '1','2','3' 인 경우
			
			szCarProgStat  = recTemp.getFieldString("YD_CAR_PROG_STAT");
			
			//******************************
			// 2개의 쿼리는 컬럼명을 동일하게 하여 읽어올수 있도록 한다.
			//******************************
			szMsg ="차량 진행상태는 : "+ szCarProgStat +" 입니다 ";
    		ydUtils.putLog("YdJspCommonSeEJBBean",szMethodName,szMsg  , YdConstant.DEBUG);
			
			
    		recPara         = JDTORecordFactory.getInstance().create();
			outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
			
		/*	if ("1".equals(szCarProgStat) || "2".equals(szCarProgStat)||"3".equals(szCarProgStat)){
				YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
				
				//차량 스케줄에 상차 작업예약 ID 로 작업예약 재료 정보 조회를 한다.
			
			
				recPara.setField("YD_WBOOK_ID", ydDaoUtils.paraRecChkNull(recTemp, "YD_CARLD_WRK_BOOK_ID"));
				recPara.setField("PAGE_CNT1",   inDto.getField("PAGE_NO"));
				recPara.setField("PAGE_CNT2",   inDto.getField("PAGE_NO"));
				recPara.setField("ROW_CNT1",    inDto.getField("ROWCOUNT"));
				recPara.setField("ROW_CNT2",    inDto.getField("ROWCOUNT"));
				
				nRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recPara, outRecSet, 22);   //////////////////////////////
				
			} else {	*/	
				YdCarFtmvMtlDao ydCarFtmvMtlDao = new YdCarFtmvMtlDao();
				
				// 차량 진행 상태 코드값이  그 이외인 경우 는 차량  이송재료 정보를 읽어온다.
				recPara.setField("YD_CAR_SCH_ID", ydDaoUtils.paraRecChkNull(recTemp, "YD_CAR_SCH_ID"));						
				recPara.setField("PAGE_CNT1", inDto.getField("PAGE_NO"));
				recPara.setField("PAGE_CNT2", inDto.getField("PAGE_NO"));
				recPara.setField("ROW_CNT1",  inDto.getField("ROWCOUNT"));
				recPara.setField("ROW_CNT2",  inDto.getField("ROWCOUNT"));
				
				nRtnVal = ydCarFtmvMtlDao.getYdCarftmvmtl(recPara, outRecSet, 9);				
		//	}
			
			if (nRtnVal < 0 ){
				//ERROR 처리 
				szMsg ="상차 조회된 정보가 없습니다!!";
        		ydUtils.putLog("YdJspCommonSeEJBBean",szMethodName,szMsg  , YdConstant.DEBUG);				
			}	
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		szMsg = "JSP-SESSION [차량 상차 정보 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return outRecSet;
	}//end of getCarLiftInfo_ASlab
	
	
	
	/**
	 *  차량 상차 정보 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCarLiftInfo_ACoil(JDTORecord inDto) throws DAOException {
		
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecord       recTemp         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");		
		int nRtnVal = 0;
		String szMsg = "";
		String szMethodName = "getCarLiftInfo_ACoil";
			
		
		String szCarProgStat = null;
		try {
			
			szMsg = "JSP-SESSION [차량 상차 정보 조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			recPara.setField("CAR_NO",    	 yddatautil.setDataDefault(inDto.getField("CAR_NO"), ""));
			recPara.setField("TRN_EQP_CD",   yddatautil.setDataDefault(inDto.getField("CAR_NO"), ""));
			
			//차량 번호로 차량 스케줄 및 차량 진행 상태 코드 값을 읽어온다. 
			
			
			YdCarSchDao ydCarSchDao = new YdCarSchDao();
			
			nRtnVal = ydCarSchDao.getYdCarsch(recPara, outRecSet, 10);
			
			
			if (nRtnVal < 1){
				//조회된 정보가 없을 경우 
				szMsg ="차량스케줄에서 조회시 ERROR !!";
        		ydUtils.putLog("YdJspCommonSeEJBBean",szMethodName,szMsg  , YdConstant.ERROR);
				
				return outRecSet;
			} else if (nRtnVal == 0){
				//조회된 정보가 없을 경우 
				szMsg ="차량스케줄에서 조회된 정보가 없습니다!!";
        		ydUtils.putLog("YdJspCommonSeEJBBean",szMethodName,szMsg  , YdConstant.DEBUG);
				
				return outRecSet;
			}
			
			outRecSet.first();
			
			recTemp = outRecSet.getRecord();
			
			
			// 차량 진행 상태 코드 값이 '1','2','3' 인 경우
			
			szCarProgStat  = recTemp.getFieldString("YD_CAR_PROG_STAT");
			
			//******************************
			// 2개의 쿼리는 컬럼명을 동일하게 하여 읽어올수 있도록 한다.
			//******************************
			szMsg ="차량 진행상태는 : "+ szCarProgStat +" 입니다 ";
    		ydUtils.putLog("YdJspCommonSeEJBBean",szMethodName,szMsg  , YdConstant.DEBUG);
			
			
    		recPara         = JDTORecordFactory.getInstance().create();
			outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
			
		/*	if (       "1".equals(szCarProgStat) 
					|| "2".equals(szCarProgStat)
					|| "3".equals(szCarProgStat)){
				YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
				
				//차량 스케줄에 상차 작업예약 ID 로 작업예약 재료 정보 조회를 한다.
			
			
				recPara.setField("YD_WBOOK_ID", ydDaoUtils.paraRecChkNull(recTemp, "YD_CARLD_WRK_BOOK_ID")); 
				recPara.setField("PAGE_CNT1", inDto.getField("PAGE_NO"));
				recPara.setField("PAGE_CNT2", inDto.getField("PAGE_NO"));
				recPara.setField("ROW_CNT1",  inDto.getField("ROWCOUNT"));
				recPara.setField("ROW_CNT2",  inDto.getField("ROWCOUNT"));
				
				nRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recPara, outRecSet, 23);   
				
			} else {	*/
			
				YdCarFtmvMtlDao ydCarFtmvMtlDao = new YdCarFtmvMtlDao();
				
				// 차량 진행 상태 코드값이  그 이외인 경우 는 차량  이송재료 정보를 읽어온다.
				recPara.setField("YD_CAR_SCH_ID", ydDaoUtils.paraRecChkNull(recTemp, "YD_CAR_SCH_ID"));
				recPara.setField("PAGE_CNT1", inDto.getField("PAGE_NO"));
				recPara.setField("PAGE_CNT2", inDto.getField("PAGE_NO"));
				recPara.setField("ROW_CNT1",  inDto.getField("ROWCOUNT"));
				recPara.setField("ROW_CNT2",  inDto.getField("ROWCOUNT"));
				
				nRtnVal = ydCarFtmvMtlDao.getYdCarftmvmtl(recPara, outRecSet, 8);				
		//	}
			
			if (nRtnVal < 0 ){
				//ERROR 처리 
				szMsg ="상차 조회된 정보가 없습니다!!";
        		ydUtils.putLog("YdJspCommonSeEJBBean",szMethodName,szMsg  , YdConstant.ERROR);
				
			}	
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		szMsg = "JSP-SESSION [차량 상차 정보 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return outRecSet;
	}//end of getCarLiftInfo_ACoil
	
	
	
	/**
	 *  적치단 - 열 구분으로 조회 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getStkLyrByStkColGp(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara    = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet  = JDTORecordFactory.getInstance().createRecordSet("yd");		
		String szMsg        = "";		
		String szMethodName = "getStkLyrByStkColGp";
		int intRtnVal = 0;
	
		
		YdStkLyrDao ydStkLyrDao  = new YdStkLyrDao ();
		
			
		try {
			
			szMsg = "JSP-SESSION [적치단 - 열 구분으로 조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			recPara.setField("YD_STK_COL_GP",ydDaoUtils.paraRecChkNull(inDto, "YD_STK_COL_GP"));
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 32);
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
		
		szMsg = "JSP-SESSION [적치단 - 열 구분으로 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return outRecSet;
	} //end of getStkLyrByStkColGp
	
	
	
	
	
	/**
	 * 메뉴얼 코일 작업지시 편성
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void ydCoilManualReq(JDTORecord[] inDto) throws DAOException {
		
		int intRtnVal = 0;
		int intSh = 0;	
		
		String szMsg= null;
		
		
		String [] strArrStlNo = null;
		String [] strArrBedNo = null;
		
		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		
		szMsg        = "";
		String szMethodName = "ydCoilManualReq";
		String szOperationName = "메뉴얼 코일 작업지시 편성";
			
		
		try {
			
			szMsg = "JSP-SESSION  [메뉴얼 코일 작업지시 편성] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			//저장위치 좌표설정화면 BED 수정
			for(int x=0;x<inDto.length;x++){
				//TC 미정
				strArrStlNo = inDto[x].getFieldString("STL_NO").split(";");				
				intSh = inDto[x].getFieldInt("SLAB_SH");
				strArrBedNo = inDto[x].getFieldString("BED_NO").split(";");
				
		
					recPara   = JDTORecordFactory.getInstance().create();
					//YD_SCH_CD
					recPara.setField("YD_SCH_CD",     ydDaoUtils.paraRecChkNull(inDto[x], "YD_SCH_CD"));
					
					//YD_STK_COL_GP				
					recPara.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(inDto[x], "FROM_BED"));
							
					//YD_SH [매수]
					recPara.setField("SLAB_SH",       ydDaoUtils.paraRecChkNull(inDto[x],"SLAB_SH"));
					
					
				for(int Loopi=0 ; Loopi<intSh ;Loopi++){	
					//YD_STK_BED_NO
					recPara.setField("YD_STK_BED_NO", strArrBedNo[Loopi]);
					
					//YD_SH [매수] ->위쪽으로 이동
			
					//재료번호
					//STL_NO []
					recPara.setField("STL_NO"+(Loopi+1), strArrStlNo[Loopi]);
					
					//권상 모음순서  
					//YD_UP_COLL_SEQ []
					recPara.setField("YD_UP_COLL_SEQ"+(Loopi+1),""+(Loopi+1));
				}
					
					
					//추가 데이터 
					//TO Guide 정보
					recPara.setField("YD_TO_LOC_GUIDE",  inDto[x].getField("TO_BED"));
					recPara.setField("REGISTER",         inDto[x].getField("REGISTER"));					
					//YD_WRK_PLAN_TCAR :계획 대차
					recPara.setField("YD_WRK_PLAN_TCAR", inDto[x].getField("YD_WRK_PLAN_TCAR"));
					
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
		
		szMsg = "JSP-SESSION  [메뉴얼 코일 작업지시 편성] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
	}	// end of ydCoilManualReq
	
	
	



	/**
	 *   공대차 스케줄 호출 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return String
	 * @throws DAOException
	 */
	public String ydTcarA(JDTORecord[] inDto) throws DAOException {
		JDTORecord recPara = null;
		//YdDelegate ydDelegate = new YdDelegate();
		String szYdGp = "";
		String szMethodName = "ydTcarA";
		Integer objRtnInt = null;
		String szRtnMsg = null;
		String szLogMsg = null;
		String szJMS_TC_CD = null;
		EJBConnector ejbConn = null;
		String szEjbMethod = null;
		JDTORecord outRecord1  			= JDTORecordFactory.getInstance().create(); 
		try{
			//for(int x=0;x<inDto.length;x++){
			
			szLogMsg = "JSP-SESSION  [공대차 스케줄 호출] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
				recPara   = JDTORecordFactory.getInstance().create();	
				//공대차 스케줄 호출
				
				
				szYdGp = yddatautil.setDataDefault(inDto[0].getField("YD_GP"),"");
				
				
				if ("".equals(szYdGp)){
					//공장 야드 구분이 없습니다 
					szLogMsg = "[JSP Session]공대차 스케줄 호출 - 공장 야드 구분이 없습니다 ";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;
				}else if(YdConstant.YD_GP_C_SLAB_YARD.equals(szYdGp)){
					//C연주 슬라브야드
					szJMS_TC_CD = "YDYDJ520";
//					ejbConn = new EJBConnector("default", "TransEqpSchSeEJB", this);
					szEjbMethod = "procY1TcarSch";
				}else if(YdConstant.YD_GP_A_PLATE_SLAB_YARD.equals(szYdGp)){
					//후판 슬라브야드
					szJMS_TC_CD = "YDYDJ522";
//					ejbConn = new EJBConnector("default", "TransEqpSchSeEJB", this);
					szEjbMethod = "procY3TcarSch";
				}else if(YdConstant.YD_GP_C_HR_COIL_MATL_YARD.equals(szYdGp)|| YdConstant.YD_GP_C_HR_COIL_GDS_YARD.equals(szYdGp)){
					//C열연 소재 야드
					szJMS_TC_CD = "YDYDJ521";
//					ejbConn = new EJBConnector("default", "CoilTransEqpSchSeEJB", this);
					szEjbMethod = "procY5TcarSch";
				}
				recPara.setField("JMS_TC_CD", szJMS_TC_CD);
				recPara.setField("YD_EQP_ID", inDto[0].getField("YD_EQP_ID"));
				recPara.setField("YD_LD_UD_GP", "");
				recPara.setField("YD_WBOOK_ID", "");
				recPara.setField("YD_TO_BAY",yddatautil.setDataDefault(inDto[0].getField("YD_TO_BAY"),""));
				
	
				//전문송신
				//ydDelegate.sendMsg(recPara);
				//EJB Method Call
//sjhkim				ejbConn = new EJBConnector("default", "TransEqpSchSeEJB", this);
//				sjhkim
				if( szEjbMethod.equals("procY5TcarSch") ) {
					ejbConn = new EJBConnector("default", "CoilTransEqpSchSeEJB", this);			
					outRecord1 = (JDTORecord)ejbConn.trx( "procY5TcarSch" , new Class[] { JDTORecord.class }, new Object[] { recPara });
	       			String sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
	       			if(sRTN_CD.equals("0")){ 
						szRtnMsg = YdConstant.RETN_CD_FAILURE;
						szLogMsg = "[JSP Session]공대차 스케줄 호출 처리 실패";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
	       			} else {
						szRtnMsg = YdConstant.RETN_CD_SUCCESS;
						szLogMsg = "[JSP Session]공대차 스케줄 호출 처리 성공";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
	       			}
 	       			
				} else{
					ejbConn = new EJBConnector("default", "TransEqpSchSeEJB", this);		
					objRtnInt = (Integer)ejbConn.trx( szEjbMethod , new Class[] { JDTORecord.class }, new Object[] { recPara });
					
					if( objRtnInt.intValue() == YdConstant.RETN_INT_SUCCESS.intValue() ) {			//성공
						szRtnMsg = YdConstant.RETN_CD_SUCCESS;
						szLogMsg = "[JSP Session]공대차 스케줄 호출 처리 성공";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					}else{																			//실패
						szRtnMsg = YdConstant.RETN_CD_FAILURE;
						szLogMsg = "[JSP Session]공대차 스케줄 호출 처리 실패";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					}
				}
				
//				objRtnInt = (Integer)ejbConn.trx( szEjbMethod , new Class[] { JDTORecord.class }, new Object[] { recPara });
//				
//				if( objRtnInt.intValue() == YdConstant.RETN_INT_SUCCESS.intValue() ) {			//성공
//					szRtnMsg = YdConstant.RETN_CD_SUCCESS;
//					szLogMsg = "[JSP Session]공대차 스케줄 호출 처리 성공";
//					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
//				}else{																			//실패
//					szRtnMsg = YdConstant.RETN_CD_FAILURE;
//					szLogMsg = "[JSP Session]공대차 스케줄 호출 처리 실패";
//					ydUtils.putLog(szSessionName,  szMethodName, szLogMsg, YdConstant.ERROR);
//				}
			//}
		}catch(Exception e){
			//throw new DAOException(getClass().getName() + e.getMessage(),e);
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
			szLogMsg = "[JSP Session]공대차 스케줄 호출 처리 실패 - 예외발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
		}
		
		szLogMsg = "JSP-SESSION  [공대차 스케줄 호출] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		
		return szRtnMsg;
	}
	
	/**
	 *   출발 실적 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return String
	 * @throws DAOException
	 */
	public String ydTcarB(JDTORecord[] inDto) throws DAOException {
		
		JDTORecord recPara = null;
		//YdDelegate ydDelegate = new YdDelegate();
		String szMethodName = "ydTcarB";
		String szOperationName = "출발 실적";
		
		String szProgStat = "";
		String szYdGp = "";
		String szAimYdGp = "";
		Integer objRtnInt = null;
		String szRtnMsg = null;
		String szLogMsg = null;
		String szJMS_TC_CD = null;
		EJBConnector ejbConn = null;
		String szEjbMethod = null;
		try{

				
			szLogMsg = "JSP-SESSION  [출발 실적 ] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
				szProgStat = ydDaoUtils.paraRecChkNull(inDto[0], "YD_CAR_PROG_STAT");
				if("".equals(szProgStat)){
					//상태값이 들어 있지 않습니다. 
					szLogMsg = "[JSP Session]대차 출발 실적 호출 - 야드차량진행상태(YD_CAR_PROG_STAT)값이 존재하지 않습니다. ";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;
				
				}else if("0".equals(szProgStat)){
					// 출발실적(공차)
					szAimYdGp = yddatautil.setDataDefault(inDto[0].getField("YD_CARLD_STOP_LOC"),"      ").substring(1,2);			
					
				}else if("5".equals(szProgStat)){	
					// 출발실적(영차)

					// 영차 출발일경우 - 도착실적(영차)
					szAimYdGp = yddatautil.setDataDefault(inDto[0].getField("YD_CARUD_STOP_LOC"),"      ").substring(1,2);
				}
				else{
					// 야드차량진행상태가 맞지 않습니다 
					szLogMsg = "[JSP Session]대차 출발 실적 호출 - 야드차량진행상태(YD_CAR_PROG_STAT[" + yddatautil.setDataDefault(inDto[0].getFieldString("YD_CAR_PROG_STAT"),"") + "])값이 맞지 않습니다. ";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;
				}
				recPara   = JDTORecordFactory.getInstance().create();	
				
				szYdGp = yddatautil.setDataDefault(inDto[0].getFieldString("YD_GP"),"");
				
				
				if ("".equals(szYdGp)){
					//공장 야드 구분이 없습니다 
					szLogMsg = "[JSP Session]대차 출발 실적 호출 - 공장 야드 구분이 없습니다 ";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;
				}else if( YdConstant.YD_GP_C_SLAB_YARD.equals(szYdGp) ){
					//C연주 슬라브야드
					szJMS_TC_CD = "YDYDJ620";
					szEjbMethod = "procC3TcarMvWr";
					
					
					
					recPara.setField("JMS_TC_CD", szJMS_TC_CD);
					recPara.setField("YD_EQP_ID", ydDaoUtils.paraRecChkNull(inDto[0], "YD_EQP_ID"));	
					
					//추가
					recPara.setField("YD_TCAR_MOVE_GP", "S");
					recPara.setField("YD_TCAR_MOVE_DIR", "F");
					recPara.setField("YD_BAY_GP1",yddatautil.setDataDefault(inDto[0].getField("NEW_YD_CURR_BAY_GP"),""));
					recPara.setField("YD_BAY_GP2",szAimYdGp);
					
				}else if( YdConstant.YD_GP_A_PLATE_SLAB_YARD.equals(szYdGp) ){
					//후판 슬라브야드
					szJMS_TC_CD = "YDYDJ622";
					szEjbMethod = "procY3TcarMvWr";
					
					
					
					recPara.setField("JMS_TC_CD", szJMS_TC_CD);
					recPara.setField("YD_EQP_ID", ydDaoUtils.paraRecChkNull(inDto[0], "YD_EQP_ID"));	
					
					//추가
					recPara.setField("YD_TCAR_MOVE_GP", "S");
					recPara.setField("YD_TCAR_MOVE_DIR", "F");
					recPara.setField("YD_BAY_GP1",yddatautil.setDataDefault(inDto[0].getField("NEW_YD_CURR_BAY_GP"),""));
					recPara.setField("YD_BAY_GP2",szAimYdGp);
					
					
				}else if( YdConstant.YD_GP_C_HR_COIL_MATL_YARD.equals(szYdGp) || YdConstant.YD_GP_C_HR_COIL_GDS_YARD.equals(szYdGp) ){
					//C열연 소재 야드 
					szJMS_TC_CD = "YDYDJ621";
					szEjbMethod = "procY5TcarMvWr";
					
					recPara.setField("JMS_TC_CD", szJMS_TC_CD);
					recPara.setField("YD_EQP_ID", ydDaoUtils.paraRecChkNull(inDto[0], "YD_EQP_ID"));
					recPara.setField("YD_BAY_GP",yddatautil.setDataDefault(inDto[0].getField("NEW_YD_CURR_BAY_GP"),""));
					
					//추가
					recPara.setField("YD_MOVE_GP", "S");
					recPara.setField("YD_TCAR_MOVE_DIR", "F");
					recPara.setField("YD_TCAR_CURR_BAY",yddatautil.setDataDefault(inDto[0].getField("NEW_YD_CURR_BAY_GP"),""));
					recPara.setField("YD_TCAR_AIM_BAY",szAimYdGp);
					
					
					
				}
				
				szLogMsg = "[JSP Session]대차 출발 실적 - " + ydDaoUtils.paraRecChkNull(inDto[0], "YD_CURR_BAY_GP");
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
//				recPara.setField("JMS_TC_CD", szJMS_TC_CD);
//				recPara.setField("YD_EQP_ID", ydDaoUtils.paraRecChkNull(inDto[0], "YD_EQP_ID"));	
//				
//				//추가
//				recPara.setField("YD_TCAR_MOVE_GP", "S");
//				recPara.setField("YD_TCAR_MOVE_DIR", "F");
//				recPara.setField("YD_BAY_GP1",yddatautil.setDataDefault(inDto[0].getField("NEW_YD_CURR_BAY_GP"),""));
//				recPara.setField("YD_BAY_GP2",szAimYdGp);
		
				
				
				ydUtils.displayRecord(szOperationName, recPara);
				//JMS Call
				//ydDelegate.sendMsg(recPara);
				//EJB Method Call
//sjhkim
				if( szEjbMethod.equals("procY5TcarMvWr") ) {
					ejbConn = new EJBConnector("default", "CoilTcarMvHdSeEJB", this);					
				} else{
					ejbConn = new EJBConnector("default", "TcarMvHdSeEJB", this);		
				}
				
//				ejbConn = new EJBConnector("default", "TcarMvHdSeEJB", this);
				objRtnInt = (Integer)ejbConn.trx( szEjbMethod , new Class[] { JDTORecord.class }, new Object[] { recPara });
				
				if( objRtnInt.intValue() == YdConstant.RETN_INT_SUCCESS.intValue() ) {			//성공
					szRtnMsg = YdConstant.RETN_CD_SUCCESS;
					szLogMsg = "[JSP Session]대차 출발 실적 호출 처리 성공";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}else{																			//실패
					szRtnMsg = YdConstant.RETN_CD_FAILURE;
					szLogMsg = "[JSP Session]대차 출발 실적 호출 처리 실패";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				}
			//}
		}catch(Exception e){
			//throw new DAOException(getClass().getName() + e.getMessage(),e);
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
			szLogMsg = "[JSP Session]대차 출발 실적 호출 처리 실패 - 예외발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
		}
		
		
		szLogMsg = "JSP-SESSION  [출발 실적 ] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return szRtnMsg;
	}
	
	
	/**
	 *   도착 실적 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return String
	 * @throws DAOException
	 */
	public String ydTcarC(JDTORecord[] inDto) throws DAOException {
		
		JDTORecord recPara     = null;
		//YdDelegate ydDelegate = new YdDelegate();
		String szProgStat      = "";
		String szYdGp          = "";
		String szBayGp         = "";
		String szMethodName		= "ydTcarC";
		String szOperationName = "대차 도착 실적";
		Integer objRtnInt = null;
		String szRtnMsg = null;
		String szLogMsg = null;
		String szJMS_TC_CD = null;
		EJBConnector ejbConn = null;
		String szEjbMethod = null;
		
		String szBayTempGp = null;
		try{
			
			
			szLogMsg = "JSP-SESSION  [도착 실적 ] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			//for(int x=0;x<inDto.length;x++){
				szProgStat = ydDaoUtils.paraRecChkNull(inDto[0], "YD_CAR_PROG_STAT"); 
					//yddatautil.setDataDefault(inDto[0].getField("YD_CAR_PROG_STAT"),"");
				
				if("".equals(szProgStat)){
					//상태값이 들어 있지 않습니다. 
					szLogMsg = "[JSP Session]대차 도착 실적 호출 - 야드차량진행상태(YD_CAR_PROG_STAT)값이 존재하지 않습니다. ";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;
				}else if("1".equals(szProgStat)){
					// 상차/공차 출발일경우 - 도착실적(공차) 
					
					szBayGp = yddatautil.setDataDefault(inDto[0].getField("YD_CARLD_STOP_LOC"),"      ").substring(1,2);
					
				}else if("A".equals(szProgStat)){
					
					// 영차 출발일경우 - 도착실적(영차)
					szBayGp = yddatautil.setDataDefault(inDto[0].getField("YD_CARUD_STOP_LOC"),"      ").substring(1,2);
					
				}else{
					// 야드차량진행상태가 맞지 않습니다 
					szLogMsg = "[JSP Session]대차 도착 실적 호출 - 야드차량진행상태(YD_CAR_PROG_STAT[" + yddatautil.setDataDefault(inDto[0].getFieldString("YD_CAR_PROG_STAT"),"") + "])값이 맞지 않습니다. ";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;
				}
				recPara   = JDTORecordFactory.getInstance().create();	
				
				szYdGp = ydDaoUtils.paraRecChkNull(inDto[0], "YD_GP");
				
				if ("".equals(szYdGp)){
					//공장 야드 구분이 없습니다 
					szLogMsg = "[JSP Session]대차 도착 실적 호출 - 공장 야드 구분이 없습니다 ";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;
				}else if( YdConstant.YD_GP_C_SLAB_YARD.equals(szYdGp) ){
					//C연주 슬라브야드
					szJMS_TC_CD = "YDYDJ620";
					szEjbMethod = "procC3TcarMvWr";
					
					recPara.setField("JMS_TC_CD", szJMS_TC_CD);
					recPara.setField("YD_EQP_ID", yddatautil.setDataDefault(inDto[0].getField("YD_EQP_ID"),""));

					
					//추가
					// 도착처리시에 상/하차 정지 위치가 존재 하지 않을경우는
					// 입력된값으로 처리 해준다.
					
					//if((szBayGp.trim()).equals("")){
						szBayTempGp = yddatautil.setDataDefault(inDto[0].getField("NEW_YD_CURR_BAY_GP"),"");
						
						szLogMsg = "[JSP Session]대차도착 처리동 :[ " + szBayTempGp +"]";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						
					//}
					
					
					recPara.setField("YD_TCAR_MOVE_GP", "E");
					recPara.setField("YD_TCAR_MOVE_DIR", "F");
					recPara.setField("YD_BAY_GP1",szBayTempGp);
					recPara.setField("YD_BAY_GP2",szBayTempGp);
					
				}else if( YdConstant.YD_GP_A_PLATE_SLAB_YARD.equals(szYdGp) ){
					//후판 슬라브야드
					szJMS_TC_CD = "YDYDJ622";
					szEjbMethod = "procY3TcarMvWr";
					
					recPara.setField("JMS_TC_CD", szJMS_TC_CD);
					recPara.setField("YD_EQP_ID", yddatautil.setDataDefault(inDto[0].getField("YD_EQP_ID"),""));

					
					//추가
					// 도착처리시에 상/하차 정지 위치가 존재 하지 않을경우는
					// 입력된값으로 처리 해준다.
					
					//if((szBayGp.trim()).equals("")){
						szBayTempGp = yddatautil.setDataDefault(inDto[0].getField("NEW_YD_CURR_BAY_GP"),"");
						
						szLogMsg = "[JSP Session]대차도착 처리동 :[ " + szBayTempGp +"]";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						
					//}
					
					
					recPara.setField("YD_TCAR_MOVE_GP", "E");
					recPara.setField("YD_TCAR_MOVE_DIR", "F");
					recPara.setField("YD_BAY_GP1",szBayTempGp);
					recPara.setField("YD_BAY_GP2",szBayTempGp);
					
				}else if( YdConstant.YD_GP_C_HR_COIL_MATL_YARD.equals(szYdGp) || YdConstant.YD_GP_C_HR_COIL_GDS_YARD.equals(szYdGp) ){
					//C열연 소재 야드 
					szJMS_TC_CD = "YDYDJ621";
					szEjbMethod = "procY5TcarMvWr";
					
					recPara.setField("JMS_TC_CD", szJMS_TC_CD);
					recPara.setField("YD_EQP_ID", ydDaoUtils.paraRecChkNull(inDto[0], "YD_EQP_ID"));
					
					
					//추가
					// 도착처리시에 상/하차 정지 위치가 존재 하지 않을경우는
					// 입력된값으로 처리 해준다.
					
					if(szBayGp.trim().equals("")){
						szBayTempGp = yddatautil.setDataDefault(inDto[0].getField("NEW_YD_CURR_BAY_GP"),"");
					}
					
					recPara.setField("YD_BAY_GP",szBayTempGp);
					recPara.setField("YD_MOVE_GP", "E");
					recPara.setField("YD_TCAR_MOVE_DIR", "F");
					recPara.setField("YD_TCAR_CURR_BAY",szBayTempGp);
					recPara.setField("YD_TCAR_AIM_BAY",szBayGp);
						
				}
				
				ydUtils.displayRecord(szOperationName, recPara);
				
				//JMS Call
				//ydDelegate.sendMsg(recPara);
				//EJB Method Call
//sjhkim
				if( szEjbMethod.equals("procY5TcarMvWr") ) {
					ejbConn = new EJBConnector("default", "CoilTcarMvHdSeEJB", this);					
				} else{
					ejbConn = new EJBConnector("default", "TcarMvHdSeEJB", this);
				}
				
//				ejbConn = new EJBConnector("default", "TcarMvHdSeEJB", this);
				objRtnInt = (Integer)ejbConn.trx( szEjbMethod , new Class[] { JDTORecord.class }, new Object[] { recPara });
				
				if( objRtnInt.intValue() == YdConstant.RETN_INT_SUCCESS.intValue() ) {			//성공
					szRtnMsg = YdConstant.RETN_CD_SUCCESS;
					szLogMsg = "[JSP Session]대차 도착 실적 호출 처리 성공";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}else{																			//실패
					szRtnMsg = YdConstant.RETN_CD_FAILURE;
					szLogMsg = "[JSP Session]대차 도착 실적 호출 처리 실패";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				}
			//}
		}catch(Exception e){
			//throw new DAOException(getClass().getName() + e.getMessage(),e);
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
			szLogMsg = "[JSP Session]대차 도착 실적 호출 처리 실패 - 예외발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg,YdConstant.ERROR);
		}
		
		szLogMsg = "JSP-SESSION  [도착 실적 ] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return szRtnMsg;
	}
	
	
	/**
	 *   완료 실적 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String ydTcarD(JDTORecord[] inDto) throws DAOException {
		
		JDTORecord recPara = null;
		JDTORecord recStopPos = null;
		JDTORecord recTemp = null;
		JDTORecord recInTemp = null;
		//JDTORecord recResult = null;
		
		
		JDTORecordSet    outRecSet  = null;
			
		String szProgStat = "";
		String szWbookId ="";
		String szYdGp = "";
		String szYdStkPos ="";
		String szMtlStat = "";
		String szYD_BAY_GP ="";
		String szEqpId = "";
		//String szCrnId =  "";
		String szMsg              		= "";
	    String szMethodName       		= "ydTcarD";
	    String szYD_WBOOK_ID            ="";
	    String szSchCd                  = ""; 
	    String szOperationName = "대차 완료실적";
	  
	    
		int nCount = 0;
	    int nMtlCnt =0;

		String szLogMsg = null;

	
		
		
		YdStkLyrDao ydStkLyrDao   = new YdStkLyrDao ();  
		//YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		YdTcarFtmvMtlDao ydTcarFtmvMtlDao = new YdTcarFtmvMtlDao();
		YdTcarSchDao ydTcarSchDao = new YdTcarSchDao();
		
		
		YdDelegate ydDelegate = new YdDelegate();

		try{
			
			szLogMsg = "JSP-SESSION [완료 실적]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			
			//for(int x=0;x<inDto.length;x++){
				szProgStat = yddatautil.setDataDefault(inDto[0].getField("YD_CAR_PROG_STAT"),"");
				szYdGp = ydDaoUtils.paraRecChkNull(inDto[0], "YD_GP");
				
				if("".equals(szProgStat)){
					//상태값이 들어 있지 않습니다. 
					szLogMsg = "[JSP Session]대차 완료 실적 호출 - 야드차량진행상태(YD_CAR_PROG_STAT)값이 존재하지 않습니다. ";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NO_PARAM;
				}
				
				else if("4".equals(szProgStat)){
		
						
			// 완료실적 (상차)
			//1. 상차 완료 실적을 처리 할 수 있는지 체크 한다.
			//   상차할 대차 베드 정보에 권하 정보가 있으면 완료실적을 처리 할수 없다고 처리 한다.
			//  대차 위에 베드가 있으므로 추후에는 적치열로 조회해야함
		
						szYdStkPos = yddatautil.setDataDefault(inDto[0].getField("YD_CARLD_STOP_LOC"),"        ");
						recStopPos =  JDTORecordFactory.getInstance().create();
						recStopPos.setField("YD_STK_COL_GP", szYdStkPos.substring(0,6));
						recStopPos.setField("YD_STK_BED_NO", "01");
						
						outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
						
						ydStkLyrDao.getYdStklyr(recStopPos, outRecSet, 1);
						
					
						
						/////////////////////////////////////////////////////////////////////
	                    //해당 상차 위치에 베드를 검색하여 정보를 조회한다.
						/////////////////////////////////////////////////////////////////////
						
						if (outRecSet.size()  < 0){
							//Dao Error
							//return "Dao Error";
							return YdConstant.RETN_CD_FAILURE;
						}else if(outRecSet.size() == 0 ){
							//해당 대차 베드위에 정보가 하나도 없을경우
							//return "해당 대차 베드위에 정보가 하나도 없을경우";
							szLogMsg = "[JSP Session]대차 상차완료 실적 호출 - 대차 베드위에 정보가 없습니다!";
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
							return YdConstant.RETN_CD_FAILURE;
						}else if(outRecSet.size() >  0 ){
							// 정보 존재시
							outRecSet.first();
							
							do{
								recTemp = outRecSet.getRecord();
								szMtlStat = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_MTL_STAT");
								
								//권하 대기 위치로 설정된 것이 있는 지 확인 
								if(YdConstant.YD_STK_LYR_MTL_STAT_DN_WAIT.equals(szMtlStat))   
									nCount++;		
								
							}while(outRecSet.next());						
						}
					
						
						if(nCount > 0){
							//에러 처리를 한다.
							szLogMsg = "[JSP Session]대차 상차완료 실적 호출 - 크레인 지시가 존재 함!!";
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
							//return  "크레인 지시가 존재 함!!";
							return YdConstant.RETN_CD_FAILURE;
						}
						
					//2. 상차완료 처리를 한다.
						 
					/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			         * 			연주정정Level2 대차작업실적 전송  - YDC3L007		      
			         +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
					if( YdConstant.YD_GP_C_SLAB_YARD.equals(szYdGp) ){
				    	recInTemp = JDTORecordFactory.getInstance().create();
						recInTemp.setField("MSG_ID",        "YDC3L007");
						recInTemp.setField("YD_TCAR_SCH_ID", ydDaoUtils.paraRecChkNull(inDto[0], "YD_TCAR_SCH_ID"));
						ydDelegate.sendMsg(recInTemp);
						szMsg = "[대차스케줄]대차 상차 시 - 대차작업실적 [YDC3L007] 전송 완료" ;
			            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
			    	/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
		            
		            
		            szMsg="하차작업예약 생성 및 등록";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);					
				
					recPara = JDTORecordFactory.getInstance().create();				
					recPara.setField("YD_TCAR_SCH_ID", ydDaoUtils.paraRecChkNull(inDto[0], "YD_TCAR_SCH_ID"));
					outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
					nMtlCnt   = ydTcarFtmvMtlDao.getYdTcarftmvmtl(recPara, outRecSet, 5);
					
					if (nMtlCnt < 1) {
						szLogMsg = "[JSP Session]대차 상차완료 실적 호출 - 대차이송재료없음";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						//return "재료없음";
						return YdConstant.RETN_CD_FAILURE;
					}
					
					
					// 하차 작업 예약 정보를 만들기 위한 재료는 이송재료 정보에서 가지고 온다.
					
					recTemp   = JDTORecordFactory.getInstance().create();
					recTemp  = outRecSet.getRecord(0);
					
					
					
					szYD_BAY_GP  = ydDaoUtils.paraRecChkNull( recTemp, "YD_AIM_BAY_GP"); //목표동을 하차동으로 세팅
					szEqpId      = ydDaoUtils.paraRecChkNull( inDto[0], "YD_EQP_ID");
					szYdGp       = szEqpId.substring(0,1);					
					szSchCd      = szYdGp + szYD_BAY_GP + szEqpId.substring(2,6) +"LM";
					
					
					
					//작업 예약 등록 (대차 스케줄은 크레인 스케줄을 편성하지않는다)
					recPara = JDTORecordFactory.getInstance().create();	
					//YD_SCH_CD
					recPara.setField("YD_SCH_CD",szSchCd);
					
					//YD_STK_COL_GP				
					recPara.setField("YD_STK_COL_GP", szYdGp + szYD_BAY_GP + szEqpId.substring(2,6));
									
					//추후 베드정보가 나올경우는 수정해야한다.
					//YD_STK_BED_NO
					recPara.setField("YD_STK_BED_NO", "01");
					
					//YD_SH [매수]
					recPara.setField("SLAB_SH", ""+nMtlCnt);
					
					
					outRecSet.first();
					
					for(int Loopi=0 ; Loopi<nMtlCnt ;Loopi++){
						//재료번호
						
						recTemp   = JDTORecordFactory.getInstance().create();
						recTemp  = outRecSet.getRecord(Loopi);
						
						//STL_NO []
						recPara.setField("STL_NO"+(Loopi+1), ydDaoUtils.paraRecChkNull(recTemp, "STL_NO"));
						
						//권상 모음순서 
						//YD_UP_COLL_SEQ []
						recPara.setField("YD_UP_COLL_SEQ"+(Loopi+1),""+(Loopi+1));
					}
					
					//추가 데이터 			
					
					recPara.setField("YD_WRK_PLAN_TCAR", szEqpId);
					recPara.setField("REGISTER", ydDaoUtils.paraRecChkNull(inDto[0], "MODIFIER")); 
					
					
					ydUtils.displayRecord(szOperationName, recPara);
								    	
					szYD_WBOOK_ID = this.ydManualReq(recPara); // 하차 작업예약 ID는 다시 대차스케줄을 호출하지않으므로 새로 만들었음 
					
					if( YdConstant.RETN_CD_FAILURE.equals(szYD_WBOOK_ID)){
						szMsg="작업예약 생성 실패";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						//return "작업예약 생성 실패";
						return YdConstant.RETN_CD_FAILURE;
					}
					
					
					// 대차 스케줄 에 하차작업 예약 ID UPDATE 및 영공차 상태 변경, 차량 진행상태 변경 해준다 .
					
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_TCAR_SCH_ID", ydDaoUtils.paraRecChkNull(inDto[0], "YD_TCAR_SCH_ID")); 
					recInTemp.setField("YD_CAR_PROG_STAT", ""+5);
					recInTemp.setField("YD_EQP_WRK_STAT","L"); //영차 상태 변경
					recInTemp.setField("YD_CARUD_WRK_BOOK_ID",szYD_WBOOK_ID);
					recInTemp.setField("MODIFIER",ydDaoUtils.paraRecChkNull(inDto[0], "MODIFIER")); 
					
					
					szMsg="================대차스케줄 UPDATE ======================";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					ydUtils.displayRecord(szOperationName, recInTemp);
					
					ydTcarSchDao.updYdTcarsch(recInTemp, 0);
					
					
					
					
					
					//영대차출발지시
							
					recInTemp = JDTORecordFactory.getInstance().create();
					if( YdConstant.YD_GP_C_SLAB_YARD.equals(szYdGp) ){
						recInTemp.setField("MSG_ID", "YDC3L006");	
					} else {
						recInTemp.setField("MSG_ID", "YDY3L006");	
					}
		    		recInTemp.setField("YD_TCAR_SCH_ID", ydDaoUtils.paraRecChkNull(inDto[0], "YD_TCAR_SCH_ID"));
		    		recInTemp.setField("YD_GP",          szYdGp);
		    		recInTemp.setField("YD_SCH_CD",      szSchCd);
		    		
					szMsg="영대차출발지시!!  MSG_IG : " + ydDaoUtils.paraRecChkNull(recInTemp, "MSG_ID") + " 전송";					
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		    		ydDelegate.sendMsg(recInTemp);
		    		
		    		return YdConstant.RETN_CD_SUCCESS;
		    		
		    		//++++++++++++상차 완료 +++++++++++(End)+++++++++++++++++++++++++++++++++++++++++++++++++++
			}
				
				else if("E".equals(szProgStat)){
					
					// 완료실적(하차)
					szWbookId = yddatautil.setDataDefault(inDto[0].getField("YD_CARUD_WRK_BOOK_ID"),"");
					
				}else{
					// 야드차량진행상태가 맞지 않습니다 
					szLogMsg = "[JSP Session]대차 완료 실적 호출 - 야드차량진행상태(YD_CAR_PROG_STAT[" + yddatautil.setDataDefault(inDto[0].getFieldString("YD_CAR_PROG_STAT"),"") + "])값이 맞지 않습니다. ";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;
				}
				recPara   = JDTORecordFactory.getInstance().create();
				
				szYdGp = yddatautil.setDataDefault(inDto[0].getField("YD_GP"),"");
				if ("".equals(szYdGp)){
					//공장 야드 구분이 없습니다 
					szLogMsg = "[JSP Session]대차 완료 실적 호출 - 공장 야드 구분이 없습니다 ";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;
				}else if(YdConstant.YD_GP_C_SLAB_YARD.equals(szYdGp)){
					//C연주 슬라브야드
					recPara.setField("JMS_TC_CD", "YDYDJ520");
					
				}else if(YdConstant.YD_GP_A_PLATE_SLAB_YARD.equals(szYdGp)){
					//후판 슬라브야드
					recPara.setField("JMS_TC_CD", "YDYDJ522");
					
				}else if(YdConstant.YD_GP_C_HR_COIL_MATL_YARD.equals(szYdGp)|| YdConstant.YD_GP_C_HR_COIL_GDS_YARD.equals(szYdGp)){
					//C열연 소재 야드 
					recPara.setField("JMS_TC_CD", "YDYDJ521");
				}
				
		
				recPara.setField("YD_EQP_ID", yddatautil.setDataDefault(inDto[0].getField("YD_EQP_ID"),""));
				recPara.setField("YD_LD_UD_GP", yddatautil.setDataDefault(inDto[0].getField("YD_EQP_WRK_STAT"),""));
				recPara.setField("YD_WBOOK_ID",szWbookId );
				
				ydUtils.displayRecord(szOperationName, recPara);
				ydDelegate.sendMsg(recPara);
				
				
				
			//}
		}catch(Exception e){
			//throw new DAOException(getClass().getName() + e.getMessage(),e);
			szLogMsg = "[JSP Session]대차 완료 실적 처리 실패 - 예외발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_FAILURE;
		}
		
		szLogMsg = "JSP-SESSION [완료 실적] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
		
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	

	/**
	 *   현위치 변경 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return String
	 * @throws DAOException
	 */
	public String ydTcarE(JDTORecord[] inDto) throws DAOException {
		
		JDTORecord recPara = null;	
		String szEqpId = "";
		String szCurrBayGp = "";
		String szNewCurrBayGp = "";
		String szStkColGp ="";
		
		//현재는 01번 BED정보만 OPEN /CLOSE 시킨다고 함
		String szStkBedNo = "01";
		
		YdStkBedDao ydStkBedDao  = new YdStkBedDao ();
		YdEqpDao ydEqpDao = new YdEqpDao();
		
		String szMethodName = "ydTcarE";
		String szOperationName  = "현위치 변경";
		
		String szLogMsg = null;
		
		
		try{
			
			szLogMsg = "JSP-SESSION [현위치 변경] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			//for(int x=0;x<inDto.length;x++){
				
				szCurrBayGp    = yddatautil.setDataDefault(inDto[0].getField("YD_CURR_BAY_GP"),"");
				szNewCurrBayGp = yddatautil.setDataDefault(inDto[0].getField("NEW_YD_CURR_BAY_GP"),"");
				szEqpId        = yddatautil.setDataDefault(inDto[0].getField("YD_EQP_ID"),"");
				
			
				//설비 ID + 현재동 구분
				szStkColGp = szEqpId;
				szStkColGp = szStkColGp.replaceAll("X", szCurrBayGp);
				
				
				//MAP 상태 CLOSE
				recPara   = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_STK_COL_GP", szStkColGp);
				recPara.setField("YD_STK_BED_NO", szStkBedNo);
				recPara.setField("YD_STK_BED_ACT_STAT","C" );
				
				ydUtils.displayRecord(szOperationName, recPara);
				ydStkBedDao.updYdStkbed(recPara, 0);
				
				
				//설비 ID + 변경 현재동 구분
				szStkColGp = szEqpId;
				szStkColGp = szStkColGp.replaceAll("X", szNewCurrBayGp);
				
				//MAP 상태 OPEN
				recPara   = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_STK_COL_GP", szStkColGp);
				recPara.setField("YD_STK_BED_NO", szStkBedNo);
				recPara.setField("YD_STK_BED_ACT_STAT","L" );
				ydUtils.displayRecord(szOperationName, recPara);
				ydStkBedDao.updYdStkbed(recPara, 0);
				
				//설비 TABLE 정보 에 현위치 정보 변경
				recPara   = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_EQP_ID", szEqpId);
				recPara.setField("YD_CURR_BAY_GP", szNewCurrBayGp);
				
				ydUtils.displayRecord(szOperationName, recPara);
				ydEqpDao.updYdEqp(recPara, 0);
			//}
		}catch(Exception e){
			//throw new DAOException(getClass().getName() + e.getMessage(),e);
			szLogMsg = "[JSP Session]현위치 변경 처리 실패 - 예외발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_FAILURE;
		}
		
		szLogMsg = "JSP-SESSION [현위치 변경] 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	/**
	 *   HOME 동 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return String
	 * @throws DAOException
	 */
	public String ydTcarF(JDTORecord[] inDto) throws DAOException {
		String szLogMsg = null;
		JDTORecord recPara = null;	
		YdEqpDao ydEqpDao = new YdEqpDao();
		String szMethodName = "ydTcarF";
		String szOperationName = "HOME 동 변경";
		try{
			
			szLogMsg = "JSP-SESSION [ HOME 동 변경] 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			//for(int x=0;x<inDto.length;x++){
				recPara   = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_EQP_ID",     yddatautil.setDataDefault(inDto[0].getField("YD_EQP_ID"),""));
				recPara.setField("YD_HOME_BAY_GP",yddatautil.setDataDefault(inDto[0].getField("YD_HOME_BAY_GP"),""));
				ydUtils.displayRecord(szOperationName, recPara);
				ydEqpDao.updYdEqp(recPara, 0);
			//}
		}catch(Exception e){
			//throw new DAOException(getClass().getName() + e.getMessage(),e);
			szLogMsg = "[JSP Session]HOME 동 변경 처리 실패 - 예외발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_FAILURE;
		}
		
		szLogMsg = "JSP-SESSION [ HOME 동 변경] 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	
	/**
	 *  컨베어 목록 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getYdConveyorCodeName(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		
		String szMsg        = "";		
		String szMethodName = "getYdConveyorCodeName";
	
		YdStrGtrDao ydStrGtrDao = new YdStrGtrDao();
		
		int intRtnVal = 0;
		
		try {
			
			szMsg = "JSP-SESSION [ 컨베어 목록 조회] 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			//String ydEqpId = yddatautil.setDataDefault(inDto.getField("YD_EQP_ID"), "");
			
			recPara.addField("YD_GP",      yddatautil.setDataDefault(inDto.getField("YD_GP"), ""));
		
			intRtnVal  =  ydStrGtrDao.getYdStrgtr(recPara, outRecSet, 3);
			
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
		
		szMsg = "JSP-SESSION [ 컨베어 목록 조회] 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return outRecSet;
	}//end of getYdConveyorCodeName
	
	
	
	/**
	 * 분기 컨베어 목록 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getYdDivConveyorCodeName(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		
		String szMsg        = "";		
		String szMethodName = "getYdDivConveyorCodeName";
	
		
		YdStkColDao ydStkColDao = new YdStkColDao();
		
		int intRtnVal = 0;
		
		try {
			
			szMsg = "JSP-SESSION [분기 컨베어 목록 조회] 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			
			recPara.addField("YD_GP",      yddatautil.setDataDefault(inDto.getField("YD_GP"), ""));
			
			//com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdDivConveyorCodeName 
			ydStkColDao.getYdStkcol(recPara, outRecSet, 18);
			
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
			
			//레코드셋이 없을때까지 반복한다.
			outRecSet.first();
			
			logger.println(LogLevel.DEBUG_TEXT, "getYdDivConveyorCodeName");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		szMsg = "JSP-SESSION [분기 컨베어 목록 조회] 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return outRecSet;
	}//end of getYdDivConveyorCodeName
	
	
	
	
	
	
	
	
	
	/**
	 * 저장품 (코일) 항목 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	
	public void updStockCoilComm(JDTORecord[] inDto) throws DAOException {
		
		JspCommonDAO jspCommonDAO = new JspCommonDAO();

		String szMethodName="updStockCoilComm";		
		String szLogMsg = "";
		
		
		try{
			
			szLogMsg = "JSP-SESSION [저장품 (코일) 항목 수정]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			for(int x=0;x<inDto.length;x++){
				jspCommonDAO.updStockCoilComm(inDto[x]);
			}
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
		szLogMsg = "JSP-SESSION [저장품 (코일) 항목 수정]끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
	}
	
	/**
	 * 저장품 (후판) 항목 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */	
	
	public void updStockPlateComm(JDTORecord[] inDto) throws DAOException {
		
		JspCommonDAO jspCommonDAO = new JspCommonDAO();
		String szMethodName="updStockPlateComm";		
		String szLogMsg = "";
		
		try{
			szLogMsg = "JSP-SESSION [저장품 (후판) 항목 수정]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			for(int x=0;x<inDto.length;x++){
				jspCommonDAO.updStockPlateComm(inDto[x]);
			}
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
		szLogMsg = "JSP-SESSION [저장품 (후판) 항목 수정]끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
	}
	
	
	/**
	 * 야드별 위치검색베범위 등록 차수 Max 구하기
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */	
	
	public int getLocSrchRngMax(JDTORecord recMsg) throws DAOException {
		
		int nMax = 0;
		int nRtnVal =0;
		String strChec =""; 
		YdLocSrchRngDao  ydLocSrchRngDao = new YdLocSrchRngDao();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMethodName="getLocSrchRngMax";		
		String szLogMsg = "";
		

		JDTORecord recPara = null;	
		try{
			
			szLogMsg = "JSP-SESSION [야드별 위치검색베범위 등록 차수 Max 구하기]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			recPara = JDTORecordFactory.getInstance().create();
			
			recPara.setField("YD_GP",recMsg.getField("YD_GP"));
			
			nRtnVal = ydLocSrchRngDao.getYdLocsrchrng(recPara, outRecSet, 600);
			
			if (nRtnVal <= 0){
				return nMax;
			}
			
			
			recPara = JDTORecordFactory.getInstance().create();
			
			outRecSet.first();
			recPara =outRecSet.getRecord();
			strChec = yddatautil.setDataDefault( recPara.getField("MAX_YD_LOC_SRCH_RNG_REG_SNO"),"");
			
			// 데이터가 없는 경우
			if ("".equals(strChec)){
				return nMax;
			}
			
			nMax =  recPara.getFieldInt("MAX_YD_LOC_SRCH_RNG_REG_SNO");
			
			
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
		
		szLogMsg = "JSP-SESSION [야드별 위치검색베범위 등록 차수 Max 구하기] 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
		
		return nMax;
	}
	
	
	
	
	
	/**
	 * 위치검색베범위 등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */	
	
	public void insLocSrchRng(JDTORecord[] inDto) throws DAOException {
		
		int nRegMaxNo = 0;
		int nRtnVal = 0;
		YdLocSrchRngDao  ydLocSrchRngDao = new YdLocSrchRngDao();
		String szSchCd = "";
		JDTORecord recPara = null;
		
		String szMethodName="위치검색베범위 등록";		
		String szLogMsg = "";

		
		
		try{
			
			szLogMsg = "JSP-SESSION [위치검색베범위 등록]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			for(int x=0;x<inDto.length;x++){
				recPara = JDTORecordFactory.getInstance().create();
				
				szSchCd = yddatautil.setDataDefault( inDto[x].getField("YD_SCH_CD"),"");
				
				if ("".equals(szSchCd)){
					//스케줄 코드가 없을 경우는 작업불가능 
					return ;
				}
				
				recPara.setField("YD_GP",szSchCd.substring(0, 1));
				nRegMaxNo =this.getLocSrchRngMax(recPara);
				
				inDto[x].setField("YD_LOC_SRCH_RNG_REG_SNO", new Integer(nRegMaxNo));
				inDto[x].setField("DEL_YN", "N");
				
				
				nRtnVal = ydLocSrchRngDao.insYdLocsrchrng(inDto[x]);
				
				
			}
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		szLogMsg = "JSP-SESSION [위치검색베범위 등록]끝";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
		
	}
	
	
	/**
	 * 공차배차 전송 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return String
	 * @throws DAOException
	 */	
	
	public String setLIdelCar(JDTORecord[] inDto) throws DAOException {
		String szLogMsg = null;
		String szRtnMsg = null;
		String szMethodName = "setLIdelCar";
		String szOperationName = "공차배차 전송";
		String szWlocCd = "";
		String szMsgId  = "";
		String szYdCarUseGp = "";
		String szYdGp = "";
		String szYdAutoLot = "";
		JDTORecord recPara = null;
		
		String szEjbJndiName = null;
		String szEjbMethod = null;
		EJBConnector ejbConn = null;
		boolean isEjbCall = true;
		
		String szChkCarNo  ="";		
		String szChkTrnEqpCd ="";
		
		int   nRtnVal = 0;
		JDTORecordSet    outRecSet  = null;
		
		YdCarSchDao ydCarShcDao  = new YdCarSchDao (); 
		
		try{
			
			szLogMsg = "JSP-SESSION [공차배차 전송]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			//for(int x=0;x<inDto.length;x++){
				//개소 코드 확인
				 
				szYdCarUseGp = yddatautil.setDataDefault(inDto[0].getField("YD_CAR_USE_GP"),"");
				szYdGp = yddatautil.setDataDefault(inDto[0].getField("YD_GP"),"");
				szYdAutoLot = yddatautil.setDataDefault(inDto[0].getField("YD_AUTO_LOT"),"");
				
				szLogMsg = "[JSP Session]공차배차 전송 호출 - 공장 야드 구분[" + szYdGp + "], 차량사용구분[" + szYdCarUseGp + "], 자동LOT[Y]/수동LOT[N]편성[" + szYdAutoLot + "]";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
				if("L".equals(szYdCarUseGp)) {
					szMsgId = "YDYDJ651";
					if( YdConstant.YD_GP_C_SLAB_YARD.equals(szYdGp) ){							//C연주 소재[A]
						
						szWlocCd = "DHY21";
					}
					
					else if(YdConstant.YD_GP_PORT_SLAB_YARD.equals(szYdGp)){				//항만슬라브야드 기능추가 - 2015.12.30 LeeJY
						
						szWlocCd = "C3S01";
					}
					
					else if(YdConstant.YD_GP_C_HR_COIL_MATL_YARD.equals(szYdGp)){				//C열연 소재[H]
						
						szWlocCd = "DJ000";
					}
					
					else if( YdConstant.YD_GP_C_HR_COIL_GDS_YARD.equals(szYdGp) ){				//C열연 코일제품창고[J]
						
						szWlocCd = "DJY30";
					}
					
					else if( YdConstant.YD_GP_A_PLATE_SLAB_YARD.equals(szYdGp)){				//A후판  슬라브[D]
					
						szWlocCd = "DKY21";
					}
					
					else if( YdConstant.YD_GP_PLATE_GDS_YARD.equals(szYdGp)){					//후판 제품창고[K]
						
						szWlocCd = "DKY30";
					}else if( YdConstant.YD_GP_INTGR_YARD.equals(szYdGp)){						//통합야드[S]
						
						szWlocCd = "DJY25"; //(비상야드추가)
					}
					else{
						//맞는 개소포인트가 없는경우도 
						szLogMsg = "[JSP Session]구내운송 공차배차 전송 호출 - 공장 야드 구분이 없습니다 ";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						return YdConstant.RETN_CD_NO_PARAM;
					}
			
					
					/*****************************************************************************
					 * 차량 스케줄에서 차량이 중복되어 있는지 다시 체크 한다.(차량스케줄에 2대가 잡힐경우가 발생)
					 *******************************( START )************************************/
					// 쿼리 등록(차량스케줄이 존재(삭제되지않은것) 한 것중에 같은 차량번호가 있는지 체크한다.
					
					// 차량번호가 존재 한다면 더이상 진행시키지 않고 리턴한다.
					
					szChkTrnEqpCd  = yddatautil.setDataDefault(inDto[0].getField("TRN_EQP_CD"),"");			
					szChkCarNo     = yddatautil.setDataDefault(inDto[0].getField("CAR_NO"),"");
					

				
					recPara = JDTORecordFactory.getInstance().create();					
					recPara.setField("TRN_EQP_CD",    yddatautil.setDataDefault(inDto[0].getField("TRN_EQP_CD"),""));
					recPara.setField("CAR_NO",        yddatautil.setDataDefault(inDto[0].getField("CAR_NO"),""));					
					outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
					
					nRtnVal = ydCarShcDao.getYdCarsch(recPara, outRecSet, 21);
					
					if (nRtnVal > 0){
						//데이터가 존재하는 경우는 리턴한다.
						szLogMsg = "[JSP Session] " + "[" + szChkTrnEqpCd + "]"+ "[" +szChkCarNo+ "]" + "차량은 스케줄에 중복하여 넣을수 없습니다" ;
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						
						return YdConstant.RETN_CD_DUPLICATE;
					}
					
					
					 /*********************************( END )***********************************/
					
					// 구내운송 전문
					
					//JMS_TC_CD	
					//TRN_EQP_CD	
					//SPOS_WLOC_CD	
					//SPOS_YD_PNT_CD	
					//ARR_WLOC_CD	
					//ARR_YD_PNT_CD	
					//TRN_WRK_FULLVOID_GP
					
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("MSG_ID",szMsgId);
					recPara.setField("TRN_EQP_CD",    yddatautil.setDataDefault(inDto[0].getField("TRN_EQP_CD"),""));				
					recPara.setField("SPOS_WLOC_CD","");
					recPara.setField("SPOS_YD_PNT_CD","");
					recPara.setField("ARR_WLOC_CD",       szWlocCd);
					recPara.setField("ARR_YD_PNT_CD","");
					recPara.setField("TRN_WRK_FULLVOID_GP","E");		
					//2009.05.11 임춘수 추가 - LOT편성 호출 시 화면에서 공차배차버튼을 통해서 호출되는 상태값을 정의
					//아래의 항목이 없는 경우는 구내운송으로 통해서 호출될 시는 값이 존재하지 않음
					//야드의 차량진행관리화면으로부터 호출되는 경우 값을 설정함
					//자동LOT편성 - Y, 수동LOT편성(사용자가 화면에서 LOT편성업무처리) - N
					//현재 통합야드의 차량진행관리화면에서만 사용됨
					
					/*
					 * 화면에서 공차 배차 시 자동 LOT편성과 수동 LOT편성을 구분하는 구분자를 설정 - 모든 야드로 적용
					 * (통합야드와 C연주슬라브야드만 적용시킴)
					 * 자동LOT편성 - Y, 수동LOT편성(사용자가 화면에서 LOT편성업무처리) - N
					 * ====> 직상차 구분자로 변경시킴 : 직상차[LOT편성하지 않음] - C, 야드상차[LOT편성] - Y
					 * 수정자 : 임춘수
					 * 일자 : 2009.07.09
					 */
					if( YdConstant.YD_GP_INTGR_YARD.equals(szYdGp)){									//통합야드[S]
						recPara.setField("YD_DIRECT_CARLD_GP", szYdAutoLot.equals("Y") ? "Y" : "C");			//기존의 직상차의 상태변수로 변환
					}else if( YdConstant.YD_GP_C_SLAB_YARD.equals(szYdGp) ){							//C연주 소재[A]
						//자동LOT편성 - Y, 수동LOT편성(사용자가 화면에서 LOT편성업무처리) - N
						recPara.setField("YD_AUTO_LOT", szYdAutoLot);
					}else if( YdConstant.YD_GP_PORT_SLAB_YARD.equals(szYdGp) ){		//항만슬라브야드 기능추가 - 2015.12.30 LeeJY
						//자동LOT편성 - Y, 수동LOT편성(사용자가 화면에서 LOT편성업무처리) - N
						recPara.setField("YD_AUTO_LOT", szYdAutoLot);
					}
					//EJB JNDI NAME or EJB METHOD NAME
					szEjbJndiName = "CarMvHdSeEJB";
					szEjbMethod = "procMatlCarLev";
					
					szLogMsg = "[JSP Session]구내운송 공차배차 전송 호출 - 공장 야드 구분[" + szYdGp + "]";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}
				
				
				
				
				
				else if("G".equals(szYdCarUseGp)){
					//출하 (이송) 전문 전송 
					
					
					if("".equals(szYdGp)){
						szLogMsg = "[JSP Session]출하 공차배차 전송 호출 - 공장 야드 구분이 없습니다 ";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						return YdConstant.RETN_CD_NO_PARAM;
					}
					
					//외판 슬라브 출하차량 도착실적 BackUp
					if( YdConstant.YD_GP_C_SLAB_YARD.equals(szYdGp) ||				//C연주 소재[A]
						YdConstant.YD_GP_PORT_SLAB_YARD.equals(szYdGp) ){			//항만슬라브야드 기능추가 - 2015.12.30 LeeJY
						recPara = JDTORecordFactory.getInstance().create();
						
						recPara.setField("MSG_ID",					"YDYDJ652");					
						recPara.setField("TRANS_ORD_DT",			yddatautil.setDataDefault(inDto[0].getField("TRANS_ORD_DT"),"")); 
						recPara.setField("TRANS_ORD_SEQNO", 		yddatautil.setDataDefault(inDto[0].getField("TRANS_ORD_SEQNO"),"")); 
						recPara.setField("CARD_NO", 				yddatautil.setDataDefault(inDto[0].getField("CARD_NO"),"")); 
						recPara.setField("CAR_NO", 					yddatautil.setDataDefault(inDto[0].getField("CAR_NO"),"")); 
						recPara.setField("YD_CAR_USE_GP", 			yddatautil.setDataDefault(inDto[0].getField("YD_CAR_USE_GP"),"")); 
						recPara.setField("SPOS_WLOC_CD", 			"DHY21"); 
						recPara.setField("SPOS_YD_PNT_CD", 			yddatautil.setDataDefault(inDto[0].getField("SPOS_YD_PNT_CD"),"")); 
						recPara.setField("YD_EQP_GP", 				yddatautil.setDataDefault(inDto[0].getField("YD_EQP_GP"),"")); 
						recPara.setField("YD_WRK_ALW_L",			yddatautil.setDataDefault(inDto[0].getField("YD_WRK_ALW_L"),"")); 
						recPara.setField("YD_WRK_ALW_W", 			yddatautil.setDataDefault(inDto[0].getField("YD_WRK_ALW_SKID_PITCH"),"")); 
						recPara.setField("YD_WRK_ALW_SKID_PITCH", 	yddatautil.setDataDefault(inDto[0].getField("YD_WRK_ALW_SKID_PITCH"),"")); 
						recPara.setField("YD_WRK_ALW_SH",			yddatautil.setDataDefault(inDto[0].getField("YD_WRK_ALW_SH"),"")); 
						recPara.setField("YD_WRK_ALW_WT", 			yddatautil.setDataDefault(inDto[0].getField("YD_WRK_ALW_WT"),"")); 
						
						szEjbJndiName = "CarMvHdSeEJB";
						szEjbMethod = "procOutplSlabDistCarArrWr";
					}

					//코일제품출하차량도착실적 BackUp
					
					else if( YdConstant.YD_GP_C_HR_COIL_GDS_YARD.equals(szYdGp) ){				//C열연 코일제품창고[J]
						recPara = JDTORecordFactory.getInstance().create();						
						recPara.setField("MSG_ID",				"YDYDJ653");	
						recPara.setField("TRANS_ORD_DT",		yddatautil.setDataDefault(inDto[0].getField("TRANS_ORD_DT"),"")); 
						recPara.setField("TRANS_ORD_SEQNO", 	yddatautil.setDataDefault(inDto[0].getField("TRANS_ORD_SEQNO"),"")); 
						recPara.setField("CAR_NO", 				yddatautil.setDataDefault(inDto[0].getField("CAR_NO"),""));
						recPara.setField("CARD_NO", 			yddatautil.setDataDefault(inDto[0].getField("CARD_NO"),"")); 
						recPara.setField("CAR_NO", 				yddatautil.setDataDefault(inDto[0].getField("CAR_NO"),""));				
						recPara.setField("SPOS_WLOC_CD", 		"DJY30"); 
						recPara.setField("SPOS_YD_PNT_CD", 		yddatautil.setDataDefault(inDto[0].getField("SPOS_YD_PNT_CD"),"")); 
						
						szEjbJndiName = "CarMvHdSeEJB";
						szEjbMethod = "procCoilGdsDistCarArrWr";
						
					} 
					
					//코일임가공차량도착실적 BackUp					
					else if(YdConstant.YD_GP_C_HR_COIL_MATL_YARD.equals(szYdGp)){				//C열연 소재[H]
						recPara = JDTORecordFactory.getInstance().create();						
						recPara.setField("MSG_ID",					"YDYDJ654");						
						recPara.setField("TRANS_ORD_DT",			yddatautil.setDataDefault(inDto[0].getField("TRANS_ORD_DT"),"")); 
						recPara.setField("TRANS_ORD_SEQNO", 		yddatautil.setDataDefault(inDto[0].getField("TRANS_ORD_SEQNO"),""));
						recPara.setField("CAR_NO", 					yddatautil.setDataDefault(inDto[0].getField("CAR_NO"),""));
						recPara.setField("CARD_NO", 				yddatautil.setDataDefault(inDto[0].getField("CARD_NO"),""));						
						recPara.setField("SPOS_WLOC_CD", 			"DHY21"); 
						recPara.setField("SPOS_YD_PNT_CD", 			yddatautil.setDataDefault(inDto[0].getField("SPOS_YD_PNT_CD"),"")); 
						recPara.setField("RENTPROC_CD", 			yddatautil.setDataDefault(inDto[0].getField("RENTPROC_CD"),""));
						recPara.setField("YD_EQP_GP", 				yddatautil.setDataDefault(inDto[0].getField("YD_EQP_GP"),"")); 
						recPara.setField("YD_WRK_ALW_L",			yddatautil.setDataDefault(inDto[0].getField("YD_WRK_ALW_L"),"")); 
						recPara.setField("YD_WRK_ALW_W", 			yddatautil.setDataDefault(inDto[0].getField("YD_WRK_ALW_SKID_PITCH"),"")); 
						recPara.setField("YD_WRK_ALW_SKID_PITCH", 	yddatautil.setDataDefault(inDto[0].getField("YD_WRK_ALW_SKID_PITCH"),"")); 
						recPara.setField("YD_WRK_ALW_SH",			yddatautil.setDataDefault(inDto[0].getField("YD_WRK_ALW_SH"),"")); 
						recPara.setField("YD_WRK_ALW_WT", 			yddatautil.setDataDefault(inDto[0].getField("YD_WRK_ALW_WT"),""));
						
						szEjbJndiName = "CarMvHdSeEJB";
						szEjbMethod = "procCoilRentprocCarArrWr";
					}
					

					//후판제품출하차량도착실적 BackUp		
					else if( YdConstant.YD_GP_PLATE_GDS_YARD.equals(szYdGp)){					//후판 제품창고[K]
						recPara = JDTORecordFactory.getInstance().create();						
						recPara.setField("MSG_ID",				"YDYDJ655");
						recPara.setField("TRANS_ORD_DT",		yddatautil.setDataDefault(inDto[0].getField("TRANS_ORD_DT"),"")); 
						recPara.setField("TRANS_ORD_SEQNO", 	yddatautil.setDataDefault(inDto[0].getField("TRANS_ORD_SEQNO"),"")); 
						recPara.setField("CAR_NO", 				yddatautil.setDataDefault(inDto[0].getField("CAR_NO"),""));
						recPara.setField("CARD_NO", 			yddatautil.setDataDefault(inDto[0].getField("CARD_NO"),"")); 
						recPara.setField("CAR_NO", 				yddatautil.setDataDefault(inDto[0].getField("CAR_NO"),""));				
						recPara.setField("SPOS_WLOC_CD", 		"DKY30"); 
						recPara.setField("SPOS_YD_PNT_CD", 		yddatautil.setDataDefault(inDto[0].getField("SPOS_YD_PNT_CD"),""));
						
						szEjbJndiName = "CarMvHdSeEJB";
						szEjbMethod = "procPlGdsDistCarArrWr";
					}
					

					//외판 슬라브 출하차량 도착실적 BackUp (통합야드 같은 전문 사용)
					else if( YdConstant.YD_GP_INTGR_YARD.equals(szYdGp)){						//통합야드[S]
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("MSG_ID",					"YDYDJ652");					
						recPara.setField("TRANS_ORD_DT",			yddatautil.setDataDefault(inDto[0].getField("TRANS_ORD_DT"),"")); 
						recPara.setField("TRANS_ORD_SEQNO", 		yddatautil.setDataDefault(inDto[0].getField("TRANS_ORD_SEQNO"),"")); 
						recPara.setField("CARD_NO", 				yddatautil.setDataDefault(inDto[0].getField("CARD_NO"),"")); 
						recPara.setField("CAR_NO", 					yddatautil.setDataDefault(inDto[0].getField("CAR_NO"),"")); 
						recPara.setField("YD_CAR_USE_GP", 			yddatautil.setDataDefault(inDto[0].getField("YD_CAR_USE_GP"),"")); 
						recPara.setField("SPOS_WLOC_CD", 			"DJY25");  //(비상야드추가)
						recPara.setField("SPOS_YD_PNT_CD", 			yddatautil.setDataDefault(inDto[0].getField("SPOS_YD_PNT_CD"),"")); 
						recPara.setField("YD_EQP_GP", 				yddatautil.setDataDefault(inDto[0].getField("YD_EQP_GP"),""));
						recPara.setField("YD_WRK_ALW_L",			yddatautil.setDataDefault(inDto[0].getField("YD_WRK_ALW_L"),"")); 
						recPara.setField("YD_WRK_ALW_W", 			yddatautil.setDataDefault(inDto[0].getField("YD_WRK_ALW_SKID_PITCH"),"")); 
						recPara.setField("YD_WRK_ALW_SKID_PITCH", 	yddatautil.setDataDefault(inDto[0].getField("YD_WRK_ALW_SKID_PITCH"),"")); 
						recPara.setField("YD_WRK_ALW_SH",			yddatautil.setDataDefault(inDto[0].getField("YD_WRK_ALW_SH"),"")); 
						recPara.setField("YD_WRK_ALW_WT", 			yddatautil.setDataDefault(inDto[0].getField("YD_WRK_ALW_WT"),"")); 
						
						szEjbJndiName = "CarMvHdSeEJB";
						szEjbMethod = "procOutplSlabDistCarArrWr";
					}
					
					szLogMsg = "[JSP Session]출하 공차배차 전송 호출 - 공장 야드 구분[" + szYdGp + "]";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}
				
				//하위 모듈들을 EJB Call or JMS Call 로 할 것인 지를 판단하는 변수 설정
				recPara.setField("IS_EJB_CALL", "Y");
				
				ydUtils.displayRecord(szOperationName, recPara);
				
				if( isEjbCall ) {
					//EJB Method Call
					szLogMsg = "[JSP Session]공차배차 전송 호출 성공 - EJB Call[" + szEjbJndiName + "." + szEjbMethod + "] 시작";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					ejbConn = new EJBConnector("default", szEjbJndiName, this);
					szRtnMsg = (String)ejbConn.trx(szEjbMethod, new Class[] { JDTORecord.class }, new Object[] { recPara });
					
					if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
						szLogMsg = "[JSP Session]공차배차 전송 호출 성공 - EJB Call[" + szEjbJndiName + "." + szEjbMethod + "]";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					}else{
						szLogMsg = "[JSP Session]공차배차 전송 호출 오류발생 - EJB Call[" + szEjbJndiName + "." + szEjbMethod + "]";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					}
					
					szLogMsg = "[JSP Session]공차배차 전송 호출 - EJB Call[" + szEjbJndiName + "." + szEjbMethod + "]";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}else{
					//JMS Call
					//전문 내용 전송 
					YdDelegate ydDelegate = new YdDelegate();
					ydDelegate.sendMsg(recPara);
					
					szLogMsg = "[JSP Session]공차배차 전송 호출 - JMS Call";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					szRtnMsg = YdConstant.RETN_CD_SUCCESS;
				}
				
				
				
			//}
		}catch(Exception e){
			//throw new DAOException(getClass().getName() + e.getMessage(),e);
			szLogMsg = "[JSP Session]공차배차 전송 호출 - 예외발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_FAILURE;
		}
		
		
		szLogMsg = "JSP-SESSION [공차배차 전송] 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
		
		return szRtnMsg;
	}
	
	/**
	 * 상차LOT편성 - 차량진행관리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return String
	 * @throws DAOException
	 */
	public String mkUpLoadCarLot(JDTORecord inDto)  throws DAOException {
		//변수 선언
		JDTORecord recInTemp = null;
		//야드구분
		String szYD_GP	= null;
		//Ejb Conn 객체
		EJBConnector ejbConn = null;
		//로그메세지 객체
		String szLogMsg = null;
		//리턴메세지 객체
		String szRtnMsg = null;
		//메소드명
		String szMethodName = "mkUpLoadCarLot";
		//EJB JNDI NAME
		String szEjbJndiName = null;
		//EJB METHOD NAME
		String szEjbMethod = null;
		
		String szOperationName = "상차LOT편성 - 차량진행관리";
		try {
			
			szLogMsg = "JSP-SESSION [상차LOT편성 - 차량진행관리] 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			//업무처리
			szYD_GP	= ydDaoUtils.paraRecChkNull(inDto, "YD_GP");
			
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("MSG_ID", "YDYDJ288");
    		//2009.05.11 임춘수 추가 - 차량진행관리 화면에서 LOT편성구분
    		recInTemp.setField("YD_DIRECT_CARLD_GP", "Y");				//	C - 직상차 작업요구 없음, Y - 일반야드 대상재 작업요구
			recInTemp.setField("YD_CAR_USE_GP",           ydDaoUtils.paraRecChkNull(inDto, "YD_CAR_USE_GP"));
	    	recInTemp.setField("YD_EQP_ID",               ydDaoUtils.paraRecChkNull(inDto, "YD_EQP_ID"));
	    	recInTemp.setField("TRN_EQP_CD",              ydDaoUtils.paraRecChkNull(inDto, "TRN_EQP_CD"));
	    	recInTemp.setField("WLOC_CD",                 ydDaoUtils.paraRecChkNull(inDto, "WLOC_CD"));				//차량상차정지위치코드
	    	recInTemp.setField("SP_TRUCK_LOADING_LOC_TP", "S");
	    	recInTemp.setField("PNT_DMD_DT",              YdUtils.getCurDate("yyyyMMddHHmmss"));
	    	recInTemp.setField("SPOS_YD_PNT_CD",          ydDaoUtils.paraRecChkNull(inDto, "SPOS_YD_PNT_CD"));		//차량출발포인터코드
	    	recInTemp.setField("SPOS_WLOC_CD",            ydDaoUtils.paraRecChkNull(inDto, "SPOS_WLOC_CD"));		//차량출발위치코드
	    	//차량스케줄 생성유무  - 2009.05.11 임춘수 추가
	    	recInTemp.setField("YD_CAR_SCH_YN", "N");
	    	//EJB Call[Y값 설정] or JMS Call[값이 없거나 N값을 설정] 유무 설정
	    	recInTemp.setField("IS_EJB_CALL", "Y");
	    	
	    	szLogMsg = "[JSP Session]상차LOT편성 -  야드구분[" + szYD_GP + "] 차량진행관리 전문편집 후 보기 - 야드구분[" + szYD_GP + "]";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);	
	    	
	    	ydUtils.displayRecord(szOperationName, recInTemp);
	    	
	    	ejbConn = new EJBConnector("default", this);
	    	if( szYD_GP.equals(YdConstant.YD_GP_C_SLAB_YARD) ||		//C연주슬라브야드[A]
		    	szYD_GP.equals(YdConstant.YD_GP_PORT_SLAB_YARD)) {	//항만슬라브야드 기능추가 - 2015.12.30 LeeJY	
	    		szEjbJndiName = "IssueWrkDmdSeEJB";
	    		szEjbMethod = "procCCsMatlFtmvCarLdLotComp";
	    		szLogMsg = "[JSP Session]상차LOT편성 - 야드구분[" + szYD_GP + "] 차량진행관리 - EJB Call[" + szEjbJndiName + "." + szEjbMethod + "] 시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				szRtnMsg = (String)ejbConn.trx(szEjbJndiName, szEjbMethod, recInTemp);
	    		
	    		if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
	    			szLogMsg = "[JSP Session]상차LOT편성 - 야드구분[" + szYD_GP + "] 차량진행관리 성공 - EJB Call[" + szEjbJndiName + "." + szEjbMethod + "] 성공";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}else{
					szLogMsg = "[JSP Session]상차LOT편성 - 야드구분[" + szYD_GP + "] 차량진행관리 오류발생 - EJB Call[" + szEjbJndiName + "." + szEjbMethod + "]";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				}
	    		
	    	}else if( szYD_GP.equals(YdConstant.YD_GP_INTGR_YARD)) {						//통합야드[S]
	    		szEjbJndiName = "IssueWrkDmdSeEJB";
	    		szEjbMethod = "procSUnMatlFtmvCarLdLotComp";
	    		szLogMsg = "[JSP Session]상차LOT편성 - 야드구분[" + szYD_GP + "] 차량진행관리 - EJB Call[" + szEjbJndiName + "." + szEjbMethod + "] 시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				szRtnMsg = (String)ejbConn.trx(szEjbJndiName, szEjbMethod, recInTemp);
	    		
	    		if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
	    			szLogMsg = "[JSP Session]상차LOT편성 - 야드구분[" + szYD_GP + "] 차량진행관리 성공 - EJB Call[" + szEjbJndiName + "." + szEjbMethod + "] 성공";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}else{
					szLogMsg = "[JSP Session]상차LOT편성 - 야드구분[" + szYD_GP + "] 차량진행관리 오류발생 - EJB Call[" + szEjbJndiName + "." + szEjbMethod + "]";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				}
	    	}else{
	    		return YdConstant.RETN_CD_FAILURE;
	    	}
	    	
	    	szLogMsg = "[JSP Session]상차LOT편성 - 야드구분[" + szYD_GP + "] 차량진행관리  성공";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);	
		}catch(Exception e){
			//예외처리
			szLogMsg = "[JSP Session]상차LOT편성 - 야드구분[" + szYD_GP + "] 차량진행관리 : 예외발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_FAILURE;
			//throw new DAOException(getClass().getName() + e.getMessage(),e);			
		}
		
		szLogMsg = "JSP-SESSION [상차LOT편성 - 차량진행관리] 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
		
		return szRtnMsg;
	}
	
	
	
	/**
	 *  차량진행관리 BACKUP - 구내운송 - 공차출발
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return String
	 * @throws DAOException
	 */	
	
	public String ydCarLUDep(JDTORecord inDto) throws DAOException {
		JDTORecord     recPara      = null;
		JDTORecord     recPosPara   = null;
		JDTORecord     recPos       = null;
		JDTORecord     recSchIdPara = null;
		JDTORecordSet  rsRtn  		= null;
		JDTORecordSet  rsRtnPos 	= null;
		
		YdDelegate ydDelegate       = null;
		YdCarSchDao ydCarSchDao     = new YdCarSchDao();
		YdStkColDao ydStkColDao     = new YdStkColDao();
		
		String szSchId              = null;
		String szLogMsg             = "";
		String szMethodName			= "ydCarLUDep";
		String szOperationName = "구내운송 - 공차출발";
		//하위모듈을 EJB CALL or JMS CALL 판단 변수 정의
		String szIS_EJB_CALL		= null;
		EJBConnector ejbConn		= null;
		String szEjbJndiName		= null;
		String szEjbMethod			= null;
		String szRtnMsg				= YdConstant.RETN_CD_FAILURE;
		int intGp = 0;
		
		try{
			
			szLogMsg = "JSP-SESSION [차량진행관리 BACKUP - 구내운송 - 공차출발] 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			recPara   		= JDTORecordFactory.getInstance().create();		
			recPosPara 		= JDTORecordFactory.getInstance().create();
			recSchIdPara 	= JDTORecordFactory.getInstance().create();
			recPos          = JDTORecordFactory.getInstance().create();
			szSchId = yddatautil.setDataDefault(inDto.getField("YD_CAR_SCH_ID"), "");
			
			
			recSchIdPara.setField("YD_CAR_SCH_ID",szSchId );
			
			rsRtn = JDTORecordFactory.getInstance().createRecordSet("YD");
			intGp = ydCarSchDao.getYdCarsch(recSchIdPara, rsRtn, 0);
			
			if (intGp <= 0 ){
				szLogMsg = "[JSP Session]차량진행관리 구내운송 -공차출발 : 해당 차량스케줄[" + szSchId + "]에 대한 정보가 없습니다";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);					
				return YdConstant.RETN_CD_NOTEXIST;
				
				
			}
			
			rsRtn.first();
			recSchIdPara = rsRtn.getRecord();
			
			
			recPara.setField("JMS_TC_CD","YDYDJ651");			
			recPara.setField("TRN_EQP_CD", recSchIdPara.getField("TRN_EQP_CD"));
			
			
			recPosPara.setField("YD_STK_COL_GP", recSchIdPara.getField("YD_CARLD_LEV_LOC"));
			
			rsRtnPos = JDTORecordFactory.getInstance().createRecordSet("YD");
			intGp = ydStkColDao.getYdStkcol(recPosPara, rsRtnPos, 0);
			
			if(intGp <=0 ){				
				szLogMsg = "[JSP Session]차량진행관리 구내운송 -공차출발 : 적치열정보[" + recSchIdPara.getField("YD_CARLD_LEV_LOC") + "]에서 해당 위치 정보를 가져올수 없습니다!!";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);					
				return YdConstant.RETN_CD_NOTEXIST;			 
			}
			
			rsRtnPos.first();
			recPos = rsRtnPos.getRecord();
			
			//개소코드
			recPara.setField("SPOS_WLOC_CD", recPos.getField("WLOC_CD"));			
			
			//야드포인트코드 
			recPara.setField("SPOS_YD_PNT_CD", recPos.getField("YD_PNT_CD"));			
			
			recPara.setField("ARR_WLOC_CD", recSchIdPara.getField("SPOS_WLOC_CD"));

			recPara.setField("ARR_YD_PNT_CD", "");

			recPara.setField("TRN_WRK_FULLVOID_GP", "E");
			
			ydUtils.displayRecord(szOperationName, recPara);
			//하위모듈을 EJB CALL or JMS CALL 판단하는 값 읽어오기
			szIS_EJB_CALL = ydDaoUtils.paraRecChkNull(inDto, "IS_EJB_CALL");
			
			if( szIS_EJB_CALL.equals("Y") ) {
				//하위모듈을 EJB CALL or JMS CALL 판단 값 설정
				recPara.setField("IS_EJB_CALL", szIS_EJB_CALL);
				//EJB Call
				szEjbJndiName = "CarMvHdSeEJB";
				szEjbMethod = "procMatlCarLev";
				
				szLogMsg = "[JSP Session]차량진행관리 구내운송 -공차출발 EJB CALL[" + szEjbJndiName + "." + szEjbMethod + "] 시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				ejbConn = new EJBConnector("default", szEjbJndiName, this);
				szRtnMsg = (String)ejbConn.trx(szEjbMethod, new Class[] { JDTORecord.class }, new Object[] { recPara });
				
				szLogMsg = "[JSP Session]차량진행관리 구내운송 -공차출발 EJB CALL[" + szEjbJndiName + "." + szEjbMethod + "] 성공";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			}else{
				//JMS Call
				ydDelegate       = new YdDelegate();
				ydDelegate.sendMsg(recPara);
				szRtnMsg = YdConstant.RETN_CD_SUCCESS;
				szLogMsg = "[JSP Session]차량진행관리 구내운송 -공차출발 JMS CALL[" + szEjbJndiName + "." + szEjbMethod + "] 성공";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			}
			
		}catch(Exception e){
			szLogMsg = "[JSP Session]차량진행관리 구내운송 -공차출발 오류발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			//예외를 던질 것인 지 리턴값만 반환할 것인 지 고민 중....
			//다른 경우는 리턴값을 던지고 이 경우에는 예외를 던지기로 결정 - 임춘수 2009.07.10
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		
		szLogMsg = "JSP-SESSION [차량진행관리 BACKUP - 구내운송 - 공차출발] 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
		
		return szRtnMsg;
	}
	
	
	
	
	/**
	 *  차량진행관리 BACKUP - 구내운송 - 영차출발
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */	
	
	public String ydCarLLDep(JDTORecord inDto) throws DAOException {
		JDTORecord     recPara      = null;
		JDTORecord     recPosPara   = null;
		JDTORecord     recPos       = null;
		JDTORecord     recSchIdPara = null;
		JDTORecordSet  rsRtn  		= null;
		JDTORecordSet  rsRtnPos 	= null;
		
		YdDelegate  ydDelegate      = null;
		YdCarSchDao ydCarSchDao     = new YdCarSchDao();
		YdStkColDao ydStkColDao     = new YdStkColDao();
		
		String szSchId = "";
		String szLogMsg = "";
		//하위모듈을 EJB CALL or JMS CALL 판단 변수 정의
		String szIS_EJB_CALL		= null;
		EJBConnector ejbConn		= null;
		String szEjbJndiName		= null;
		String szEjbMethod			= null;
		String szRtnMsg				= YdConstant.RETN_CD_FAILURE;
		String szMethodName			= "ydCarLLDep";
		String szOperationName = "구내운송 - 영차출발";
		
		int intGp = 0;
		
		try{
			
			szLogMsg = "JSP-SESSION [차량진행관리 BACKUP - 구내운송 - 영차출발] 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			recPara  		= JDTORecordFactory.getInstance().create();		
			recSchIdPara 	= JDTORecordFactory.getInstance().create();
			recPosPara 		= JDTORecordFactory.getInstance().create();
			recPos			= JDTORecordFactory.getInstance().create();
			szSchId = yddatautil.setDataDefault(inDto.getField("YD_CAR_SCH_ID"), "");
			
			rsRtn = JDTORecordFactory.getInstance().createRecordSet("YD");
			recSchIdPara.setField("YD_CAR_SCH_ID",szSchId );
			intGp = ydCarSchDao.getYdCarsch(recSchIdPara, rsRtn, 0);
			
			if (intGp <= 0 ){
				szLogMsg = "[JSP Session]차량진행관리 BACKUP 구내운송 -영차출발 : 해당 차량스케줄["+szSchId+"]에 대한 정보가 없습니다";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);					
				return YdConstant.RETN_CD_NOTEXIST;
			}
			
			rsRtn.first();
			recSchIdPara = rsRtn.getRecord();
			
			
			recPara.setField("JMS_TC_CD","YDYDJ651");			
			recPara.setField("TRN_EQP_CD", recSchIdPara.getField("TRN_EQP_CD"));
			
			rsRtnPos = JDTORecordFactory.getInstance().createRecordSet("YD");
			recPosPara.setField("YD_STK_COL_GP", recSchIdPara.getField("YD_CARLD_STOP_LOC"));			
			intGp = ydStkColDao.getYdStkcol(recPosPara, rsRtnPos, 0);
			
			if(intGp <=0 ){
				szLogMsg = "[JSP Session]차량진행관리 BACKUP 구내운송 -영차출발 : 적치열정보["+recSchIdPara.getFieldString("YD_CARLD_STOP_LOC")+"]에서 해당 위치 정보를 가져올수 없습니다!!";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}
			
			rsRtnPos.first();
			recPos = rsRtnPos.getRecord();
			
			//개소코드
			recPara.setField("SPOS_WLOC_CD", recPos.getField("WLOC_CD"));
			//야드포인트코드 
			recPara.setField("SPOS_YD_PNT_CD", recPos.getField("YD_PNT_CD"));
			
			recPara.setField("ARR_WLOC_CD", recSchIdPara.getField("ARR_WLOC_CD"));

			recPara.setField("ARR_YD_PNT_CD","");

			recPara.setField("TRN_WRK_FULLVOID_GP", "F"); 
			
			
			ydUtils.displayRecord(szOperationName, recPara);
			
			//하위모듈을 EJB CALL or JMS CALL 판단하는 값 읽어오기
			szIS_EJB_CALL = ydDaoUtils.paraRecChkNull(inDto, "IS_EJB_CALL");
			
			if( szIS_EJB_CALL.equals("Y") ) {
				//하위모듈을 EJB CALL or JMS CALL 판단 값 설정
				recPara.setField("IS_EJB_CALL", szIS_EJB_CALL);
				//EJB Call
				szEjbJndiName = "CarMvHdSeEJB";
				szEjbMethod = "procMatlCarLev";
				
				szLogMsg = "[JSP Session]차량진행관리 BACKUP 구내운송 -영차출발 EJB CALL[" + szEjbJndiName + "." + szEjbMethod + "] 시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				ejbConn = new EJBConnector("default", szEjbJndiName, this);
				szRtnMsg = (String)ejbConn.trx(szEjbMethod, new Class[] { JDTORecord.class }, new Object[] { recPara });
				
				szLogMsg = "[JSP Session]차량진행관리 BACKUP 구내운송 -영차출발 EJB CALL[" + szEjbJndiName + "." + szEjbMethod + "] 성공";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			}else{
				//JMS Call
				ydDelegate       = new YdDelegate();
				ydDelegate.sendMsg(recPara);
				szRtnMsg = YdConstant.RETN_CD_SUCCESS;
				szLogMsg = "[JSP Session]차량진행관리 BACKUP 구내운송 -영차출발 JMS CALL[" + szEjbJndiName + "." + szEjbMethod + "] 성공";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			}		
			
		}catch(Exception e){
			szLogMsg = "[JSP Session]차량진행관리 BACKUP 구내운송 -영차출발 오류발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(),e);			
		}
		
		szLogMsg = "JSP-SESSION [차량진행관리 BACKUP - 구내운송 - 영차출발] 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
		
		return szRtnMsg;
	}
	
	/**
	 *  차량진행관리 BACKUP - 이송(출하) - 공차출발
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */	
	
	public void ydCarGUDep(JDTORecord inDto) throws DAOException {
		JDTORecord     recPara      = null;
		JDTORecord     recPosPara   = null;
		JDTORecord     recPos       = null;
		JDTORecord     recSchIdPara = null;	
		JDTORecordSet  rsRtnPos 	= null;
		
		YdDelegate ydDelegate = new YdDelegate();
		
		YdStkColDao ydStkColDao = new YdStkColDao();		
		
		int intGp = 0;
		
		String ydGp = "";
		String szLogMsg ="";

		String szMethodName="ydCarGUDep";	
		String szOperationName = "이송(출하) - 공차출발";
		
		try{
			
			szLogMsg = "JSP-SESSION [차량진행관리 BACKUP - 이송(출하) - 공차출발] 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			//야드 구분 CHECK
			
			ydGp = yddatautil.setDataDefault(inDto.getField("YD_GP"),"");
			
			
			if ("".equals(ydGp))
			{
				//야드구분 미입력
				szLogMsg = "야드구분이 존재하지 않습니다";
				ydUtils.putLog(szSessionName, "ydCarGUDep", szLogMsg, YdConstant.ERROR);
				return;
			}
			
			// C연주 슬라브 야드[외판슬라브야드 전문 편집]
			//JMS_TC_CD	
			//SPOS_WLOC_CD	
			//SPOS_YD_PNT_CD			
			
			else if("A".equals(ydGp) ||
					"M".equals(ydGp)      //항만슬라브야드 기능추가 - 2015.12.30 LeeJY
					){
				//현재 외판슬라브 출하차량에만 적용되어 있으므로 추후 야드별 전문 적용해야함
				recPara  		= JDTORecordFactory.getInstance().create();		
				recSchIdPara 	= JDTORecordFactory.getInstance().create();
				recPosPara 		= JDTORecordFactory.getInstance().create();
				recPos			= JDTORecordFactory.getInstance().create();	
				
				//출하인 경우				
				recPara.setField("JMS_TC_CD","YDYDJ656");
				
				//영/공차 처리 FLOW가 같음
				
				recPosPara.setField("YD_STK_COL_GP", inDto.getField("T_STOP_LOC"));
				
				
				//적치열에서 해당 개소코드 정보와 포인트코드를 가지고 온다.
				rsRtnPos = JDTORecordFactory.getInstance().createRecordSet("YD");
				intGp = ydStkColDao.getYdStkcol(recPosPara, rsRtnPos, 0);
				
				if(intGp <=0 ){
					szLogMsg = "적치열정보에서 해당 위치 정보를 가져올수 없습니다!!";
					ydUtils.putLog(szSessionName, "ydCarGUDep", szLogMsg, YdConstant.ERROR);					
					return ; 
				}
				
				rsRtnPos.first();
				recPos = rsRtnPos.getRecord();			
				
				recPara.setField("SPOS_WLOC_CD", recPos.getField("WLOC_CD"));
				recPara.setField("SPOS_YD_PNT_CD", recPos.getField("YD_PNT_CD"));
				
				
			}
			//C열연 코일제품야드 "J"
			//JMS_TC_CD	
			//TRANS_ORD_DT	
			//TRANS_ORD_SEQNO	
			//CAR_NO	
			//CARD_NO	
			//SPOS_WLOC_CD	
			//SPOS_YD_PNT_CD

			else if("J".equals(ydGp)){
								
				//현재 외판슬라브 출하차량에만 적용되어 있으므로 추후 야드별 전문 적용해야함
				recPara  		= JDTORecordFactory.getInstance().create();		
				recSchIdPara 	= JDTORecordFactory.getInstance().create();
				recPosPara 		= JDTORecordFactory.getInstance().create();
				recPos			= JDTORecordFactory.getInstance().create();	
				
				//출하인 경우				
				recPara.setField("JMS_TC_CD","YDYDJ657");
				
				//화면정보바로 사용
				recPara.setField("TRANS_ORD_DT", 	inDto.getField("TRANS_ORD_DATE"));
				recPara.setField("TRANS_ORD_SEQNO", inDto.getField("TRANS_ORD_SEQNO"));
				recPara.setField("CAR_NO", 			inDto.getField("CAR_NO"));
				recPara.setField("CARD_NO",			inDto.getField("CARD_NO"));
				
				
				//적치열에서 해당 개소코드 정보와 포인트코드를 가지고 온다.
				recPosPara.setField("YD_STK_COL_GP", inDto.getField("T_STOP_LOC"));
				rsRtnPos = JDTORecordFactory.getInstance().createRecordSet("YD");
				intGp = ydStkColDao.getYdStkcol(recPosPara, rsRtnPos, 0);
				
				if(intGp <=0 ){
					szLogMsg = "적치열정보에서 해당 위치 정보를 가져올수 없습니다!!";
					ydUtils.putLog(szSessionName, "ydCarGUDep", szLogMsg, YdConstant.ERROR);					
					return ; 
				}
				
				rsRtnPos.first();
				recPos = rsRtnPos.getRecord();			
				
				recPara.setField("SPOS_WLOC_CD", recPos.getField("WLOC_CD"));
				recPara.setField("SPOS_YD_PNT_CD", recPos.getField("YD_PNT_CD"));
				
			}
			
			//코일임가공 -소재
			//JMS_TC_CD	
			//TRANS_ORD_DT	
			//TRANS_ORD_SEQNO	
			//CAR_NO	
			//CARD_NO	
			//SPOS_WLOC_CD	
			//SPOS_YD_PNT_CD

			else if("H".equals(ydGp)){
								
				//현재 외판슬라브 출하차량에만 적용되어 있으므로 추후 야드별 전문 적용해야함
				recPara  		= JDTORecordFactory.getInstance().create();		
				recSchIdPara 	= JDTORecordFactory.getInstance().create();
				recPosPara 		= JDTORecordFactory.getInstance().create();
				recPos			= JDTORecordFactory.getInstance().create();	
				
				//출하인 경우				
				recPara.setField("JMS_TC_CD","YDYDJ658");
				
				//화면정보바로 사용
				recPara.setField("TRANS_ORD_DT", 	inDto.getField("TRANS_ORD_DATE"));
				recPara.setField("TRANS_ORD_SEQNO", inDto.getField("TRANS_ORD_SEQNO"));
				recPara.setField("CAR_NO", 			inDto.getField("CAR_NO"));
				recPara.setField("CARD_NO",			inDto.getField("CARD_NO"));
				
				
				//적치열에서 해당 개소코드 정보와 포인트코드를 가지고 온다.
				recPosPara.setField("YD_STK_COL_GP", inDto.getField("T_STOP_LOC"));
				rsRtnPos = JDTORecordFactory.getInstance().createRecordSet("YD");
				intGp = ydStkColDao.getYdStkcol(recPosPara, rsRtnPos, 0);
				
				if(intGp <=0 ){
					szLogMsg = "적치열정보에서 해당 위치 정보를 가져올수 없습니다!!";
					ydUtils.putLog(szSessionName, "ydCarGUDep", szLogMsg, YdConstant.ERROR);					
					return ; 
				}
				
				rsRtnPos.first();
				recPos = rsRtnPos.getRecord();			
				
				recPara.setField("SPOS_WLOC_CD", recPos.getField("WLOC_CD"));
				recPara.setField("SPOS_YD_PNT_CD", recPos.getField("YD_PNT_CD"));
				
			}
			

			//후판 제품 
			//JMS_TC_CD	
			//TRANS_ORD_DT	
			//TRANS_ORD_SEQNO	
			//CAR_NO	
			//CARD_NO	
			//SPOS_WLOC_CD	
			//SPOS_YD_PNT_CD

			else if("K".equals(ydGp)){
								
				//현재 외판슬라브 출하차량에만 적용되어 있으므로 추후 야드별 전문 적용해야함
				recPara  		= JDTORecordFactory.getInstance().create();		
				recSchIdPara 	= JDTORecordFactory.getInstance().create();
				recPosPara 		= JDTORecordFactory.getInstance().create();
				recPos			= JDTORecordFactory.getInstance().create();	
				
				//출하인 경우				
				recPara.setField("JMS_TC_CD","YDYDJ659");
				
				//화면정보바로 사용
				recPara.setField("TRANS_ORD_DT", 	inDto.getField("TRANS_ORD_DATE"));
				recPara.setField("TRANS_ORD_SEQNO", inDto.getField("TRANS_ORD_SEQNO"));
				recPara.setField("CAR_NO", 			inDto.getField("CAR_NO"));
				recPara.setField("CARD_NO",			inDto.getField("CARD_NO"));
				
				
				//적치열에서 해당 개소코드 정보와 포인트코드를 가지고 온다.
				recPosPara.setField("YD_STK_COL_GP", inDto.getField("T_STOP_LOC"));
				rsRtnPos = JDTORecordFactory.getInstance().createRecordSet("YD");
				intGp = ydStkColDao.getYdStkcol(recPosPara, rsRtnPos, 0);
				
				if(intGp <=0 ){
					szLogMsg = "적치열정보에서 해당 위치 정보를 가져올수 없습니다!!";
					ydUtils.putLog(szSessionName, "ydCarGUDep", szLogMsg, YdConstant.ERROR);					
					return ; 
				}
				
				rsRtnPos.first();
				recPos = rsRtnPos.getRecord();			
				
				recPara.setField("SPOS_WLOC_CD", recPos.getField("WLOC_CD"));
				recPara.setField("SPOS_YD_PNT_CD", recPos.getField("YD_PNT_CD"));
				
			}
			
			
			
			
			ydUtils.displayRecord(szOperationName, recPara);
			ydDelegate.sendMsg(recPara);
		
			
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);			
		}
		
		szLogMsg = "JSP-SESSION [차량진행관리 BACKUP - 이송(출하) - 공차출발] 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
		
	}
	
	
	/**
	 *  차량진행관리 BACKUP - 이송(출하) - 영차출발
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */	
	
	public void ydCarGLDep(JDTORecord inDto) throws DAOException {
		JDTORecord     recPara      = null;
		JDTORecord     recPosPara   = null;
		JDTORecord     recPos       = null;
		JDTORecord     recSchIdPara = null;	
		JDTORecordSet  rsRtnPos 	= null;
		
		YdDelegate ydDelegate = new YdDelegate();
		
		YdStkColDao ydStkColDao = new YdStkColDao();		
		
		int intGp = 0;
		
		String ydGp = "";
		String szLogMsg ="";
		String szMethodName="ydCarGLDep";	
		String szOperationName = "이송(출하) - 영차출발";
		
		try{
			
			szLogMsg = "JSP-SESSION [ 차량진행관리 BACKUP - 이송(출하) - 영차출발] 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			//야드 구분 CHECK
			
			ydGp = yddatautil.setDataDefault(inDto.getField("YD_GP"),"");
			
			
			if ("".equals(ydGp))
			{
				//야드구분 미입력
				szLogMsg = "야드구분이 존재하지 않습니다";
				ydUtils.putLog(szSessionName, "ydCarGLDep", szLogMsg,YdConstant.ERROR);
				return;
			}
			
			// C연주 슬라브 야드[외판슬라브야드 전문 편집]
			//JMS_TC_CD	
			//SPOS_WLOC_CD	
			//SPOS_YD_PNT_CD			
			
			else if("A".equals(ydGp) ||
					"M".equals(ydGp)    //항만슬라브야드 기능추가 - 2015.12.30 LeeJY
					){
				
				recPara  		= JDTORecordFactory.getInstance().create();		
				recSchIdPara 	= JDTORecordFactory.getInstance().create();
				recPosPara 		= JDTORecordFactory.getInstance().create();
				recPos			= JDTORecordFactory.getInstance().create();	
				
				//출하인 경우				
				recPara.setField("JMS_TC_CD","YDYDJ656");
				
				//영/공차 처리 FLOW가 같음
				
				recPosPara.setField("YD_STK_COL_GP", inDto.getField("T_STOP_LOC"));
				
				
				//적치열에서 해당 개소코드 정보와 포인트코드를 가지고 온다.
				rsRtnPos = JDTORecordFactory.getInstance().createRecordSet("YD");
				intGp = ydStkColDao.getYdStkcol(recPosPara, rsRtnPos, 0);
				
				if(intGp <=0 ){
					szLogMsg = "적치열정보에서 해당 위치 정보를 가져올수 없습니다!!";
					ydUtils.putLog(szSessionName, "ydCarGLDep", szLogMsg, YdConstant.ERROR);					
					return ; 
				}
				
				rsRtnPos.first();
				recPos = rsRtnPos.getRecord();			
				
				recPara.setField("SPOS_WLOC_CD", recPos.getField("WLOC_CD"));
				recPara.setField("SPOS_YD_PNT_CD", recPos.getField("YD_PNT_CD"));
				
				
			}
			//C열연 코일제품야드 "J"
			//JMS_TC_CD	
			//TRANS_ORD_DT	
			//TRANS_ORD_SEQNO	
			//CAR_NO	
			//CARD_NO	
			//SPOS_WLOC_CD	
			//SPOS_YD_PNT_CD

			else if("J".equals(ydGp)){
				
				recPara  		= JDTORecordFactory.getInstance().create();		
				recSchIdPara 	= JDTORecordFactory.getInstance().create();
				recPosPara 		= JDTORecordFactory.getInstance().create();
				recPos			= JDTORecordFactory.getInstance().create();	
				
				//출하인 경우				
				recPara.setField("JMS_TC_CD","YDYDJ657");
				
				//화면정보바로 사용
				recPara.setField("TRANS_ORD_DT", 	inDto.getField("TRANS_ORD_DATE"));
				recPara.setField("TRANS_ORD_SEQNO", inDto.getField("TRANS_ORD_SEQNO"));
				recPara.setField("CAR_NO", 			inDto.getField("CAR_NO"));
				recPara.setField("CARD_NO",			inDto.getField("CARD_NO"));
				
				
				//적치열에서 해당 개소코드 정보와 포인트코드를 가지고 온다.
				recPosPara.setField("YD_STK_COL_GP", inDto.getField("T_STOP_LOC"));
				rsRtnPos = JDTORecordFactory.getInstance().createRecordSet("YD");
				intGp = ydStkColDao.getYdStkcol(recPosPara, rsRtnPos, 0);
				
				if(intGp <=0 ){
					szLogMsg = "적치열정보에서 해당 위치 정보를 가져올수 없습니다!!";
					ydUtils.putLog(szSessionName, "ydCarGLDep", szLogMsg, YdConstant.ERROR);					
					return ; 
				}
				
				rsRtnPos.first();
				recPos = rsRtnPos.getRecord();			
				
				recPara.setField("SPOS_WLOC_CD", recPos.getField("WLOC_CD"));
				recPara.setField("SPOS_YD_PNT_CD", recPos.getField("YD_PNT_CD"));
				
			}
			
			//코일임가공 -소재
			//JMS_TC_CD	
			//TRANS_ORD_DT	
			//TRANS_ORD_SEQNO	
			//CAR_NO	
			//CARD_NO	
			//SPOS_WLOC_CD	
			//SPOS_YD_PNT_CD

			else if("H".equals(ydGp)){
								
			
				recPara  		= JDTORecordFactory.getInstance().create();		
				recSchIdPara 	= JDTORecordFactory.getInstance().create();
				recPosPara 		= JDTORecordFactory.getInstance().create();
				recPos			= JDTORecordFactory.getInstance().create();	
				
				//출하인 경우				
				recPara.setField("JMS_TC_CD","YDYDJ658");
				
				//화면정보바로 사용
				recPara.setField("TRANS_ORD_DT", 	inDto.getField("TRANS_ORD_DATE"));
				recPara.setField("TRANS_ORD_SEQNO", inDto.getField("TRANS_ORD_SEQNO"));
				recPara.setField("CAR_NO", 			inDto.getField("CAR_NO"));
				recPara.setField("CARD_NO",			inDto.getField("CARD_NO"));
				
				
				//적치열에서 해당 개소코드 정보와 포인트코드를 가지고 온다.
				recPosPara.setField("YD_STK_COL_GP", inDto.getField("T_STOP_LOC"));
				rsRtnPos = JDTORecordFactory.getInstance().createRecordSet("YD");
				intGp = ydStkColDao.getYdStkcol(recPosPara, rsRtnPos, 0);
				
				if(intGp <=0 ){
					szLogMsg = "적치열정보에서 해당 위치 정보를 가져올수 없습니다!!";
					ydUtils.putLog(szSessionName, "ydCarGUDep", szLogMsg, YdConstant.ERROR);					
					return ; 
				}
				
				rsRtnPos.first();
				recPos = rsRtnPos.getRecord();			
				
				recPara.setField("SPOS_WLOC_CD", recPos.getField("WLOC_CD"));
				recPara.setField("SPOS_YD_PNT_CD", recPos.getField("YD_PNT_CD"));
				
			}
			

			//후판 제품 
			//JMS_TC_CD	
			//TRANS_ORD_DT	
			//TRANS_ORD_SEQNO	
			//CAR_NO	
			//CARD_NO	
			//SPOS_WLOC_CD	
			//SPOS_YD_PNT_CD

			else if("K".equals(ydGp)){
								
				//현재 외판슬라브 출하차량에만 적용되어 있으므로 추후 야드별 전문 적용해야함
				recPara  		= JDTORecordFactory.getInstance().create();		
				recSchIdPara 	= JDTORecordFactory.getInstance().create();
				recPosPara 		= JDTORecordFactory.getInstance().create();
				recPos			= JDTORecordFactory.getInstance().create();	
				
				//출하인 경우				
				recPara.setField("JMS_TC_CD","YDYDJ659");
				
				//화면정보바로 사용
				recPara.setField("TRANS_ORD_DT", 	inDto.getField("TRANS_ORD_DATE"));
				recPara.setField("TRANS_ORD_SEQNO", inDto.getField("TRANS_ORD_SEQNO"));
				recPara.setField("CAR_NO", 			inDto.getField("CAR_NO"));
				recPara.setField("CARD_NO",			inDto.getField("CARD_NO"));
				
				
				//적치열에서 해당 개소코드 정보와 포인트코드를 가지고 온다.
				recPosPara.setField("YD_STK_COL_GP", inDto.getField("T_STOP_LOC"));
				rsRtnPos = JDTORecordFactory.getInstance().createRecordSet("YD");
				intGp = ydStkColDao.getYdStkcol(recPosPara, rsRtnPos, 0);
				
				if(intGp <=0 ){
					szLogMsg = "적치열정보에서 해당 위치 정보를 가져올수 없습니다!!";
					ydUtils.putLog(szSessionName, "ydCarGUDep", szLogMsg, YdConstant.ERROR);					
					return ; 
				}
				
				rsRtnPos.first();
				recPos = rsRtnPos.getRecord();			
				
				recPara.setField("SPOS_WLOC_CD", recPos.getField("WLOC_CD"));
				recPara.setField("SPOS_YD_PNT_CD", recPos.getField("YD_PNT_CD"));
				
			}
			
			ydUtils.displayRecord(szOperationName, recPara);
			ydDelegate.sendMsg(recPara);
		
			
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);			
		}
		
		
		szLogMsg = "JSP-SESSION [ 차량진행관리 BACKUP - 이송(출하) - 영차출발] 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
		
	}
	
	
	

	/**
	 *  차량진행관리 BACKUP - 구내운송 - 공차도착
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return String
	 * @throws DAOException
	 */	
	
	public String ydCarLUArr(JDTORecord inDto) throws DAOException {
		JDTORecord     recPara      = null;
		JDTORecord     recPosPara   = null;
		JDTORecord     recPos       = null;
		JDTORecord     recSchIdPara = null;
		JDTORecord 	   recRtnSpec 	= null;
		
		JDTORecordSet  rsRtn  		= null;
		JDTORecordSet  rsRtnPos 	= null;
		JDTORecordSet  rsRtnSpec 	= null;
		
		YdDelegate ydDelegate       = null;
		YdCarSchDao ydCarSchDao     = new YdCarSchDao();
		YdStkColDao ydStkColDao     = new YdStkColDao();
		YdCarSpecDao ydCarSpecDao   = new YdCarSpecDao();
		
		String szMethodName = "ydCarLUArr";
		String szOperationName = "구내운송 - 공차도착";
		String szSchId = "";
		String szLogMsg = "";
		String szRtnMsg = YdConstant.RETN_CD_FAILURE;
		String szEjbJndiName = null;
		String szEjbMethod = null;
		String szIS_EJB_CALL = null;
		
		EJBConnector ejbConn = null;
		
		int intGp = 0;
		
		
		try{
			
			szLogMsg = "JSP-SESSION [ 차량진행관리 BACKUP - 구내운송 - 공차도착] 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			ydUtils.displayRecord("차량진행관리 BACKUP - 구내운송 - 공차도착", inDto);
			
			
			recPara  		= JDTORecordFactory.getInstance().create();		
			recSchIdPara 	= JDTORecordFactory.getInstance().create();
			recPosPara 		= JDTORecordFactory.getInstance().create();
			recPos    		= JDTORecordFactory.getInstance().create();
			recRtnSpec   	= JDTORecordFactory.getInstance().create();
			szSchId = yddatautil.setDataDefault(inDto.getField("YD_CAR_SCH_ID"), "");
			
			
			rsRtn  = JDTORecordFactory.getInstance().createRecordSet("YD");
			recSchIdPara.setField("YD_CAR_SCH_ID",szSchId );
			intGp = ydCarSchDao.getYdCarsch(recSchIdPara, rsRtn, 0);
			
			if (intGp == 0 ){
				
				szLogMsg = "[JSP Session]차량진행관리 BACKUP - 구내운송 공차도착 : 해당 차량스케줄["+szSchId+"]에 대한 정보가 없습니다!";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);					
				return YdConstant.RETN_CD_NOTEXIST; 
				
			}
			
			rsRtn.first();
			recSchIdPara = rsRtn.getRecord();
			
			
			recPara.setField("JMS_TC_CD","YDYDJ650");	
			recPara.setField("TRN_EQP_CD", recSchIdPara.getField("TRN_EQP_CD"));
			
			
			//상차정지위치에서 데이터를 가지고 온다.
			recPosPara.setField("YD_STK_COL_GP", recSchIdPara.getField("YD_CARLD_STOP_LOC"));
			
			rsRtnPos =  JDTORecordFactory.getInstance().createRecordSet("YD");
			intGp = ydStkColDao.getYdStkcol(recPosPara, rsRtnPos, 0);
			
			if(intGp <=0 ){
				szLogMsg = "[JSP Session]차량진행관리 BACKUP - 구내운송 공차도착 : 적치열정보["+recSchIdPara.getField("YD_CARLD_STOP_LOC")+"]에서 해당 위치 정보를 가져올수 없습니다!";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);					
				return YdConstant.RETN_CD_FAILURE; 
				
			}
			
			rsRtnPos.first();
			recPos = rsRtnPos.getRecord();
			
			
			//개소코드
			recPara.setField("ARR_WLOC_CD", recPos.getField("WLOC_CD"));
			//야드포인트코드 
			recPara.setField("ARR_YD_PNT_CD", recPos.getField("YD_PNT_CD"));
			
			recPara.setField("TRN_WRK_FULLVOID_GP", "E");
			rsRtnSpec  = JDTORecordFactory.getInstance().createRecordSet("YD");


			//TRN_EQP_CD 로 정보를 찾는다.						
			intGp = ydCarSpecDao.getYdCarspec(recPara, rsRtnSpec, 2);
			
			if( intGp <= 0 ){
				szLogMsg = "[JSP Session]차량진행관리 BACKUP - 구내운송 공차도착 : 해당 스펙정보[" + recSchIdPara.getField("TRN_EQP_CD") + "]가 없습니다 !!";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);					
				return YdConstant.RETN_CD_FAILURE; 
				
			}			
			
			rsRtnSpec.first();
			recRtnSpec = rsRtnSpec.getRecord();
	
			recPara.setField("TRN_EQP_STK_CAPA", recRtnSpec.getField("YD_WRK_ALW_WT"));
			
			//CAR_ARR_DT			
			recPara.setField("CAR_ARR_DT", recSchIdPara.getField("YD_CARLD_ARR_DT"));
			
			
			szLogMsg = "------------------------편집 전문--------------------------";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);		
			
			ydUtils.displayRecord(szOperationName, recPara);
			
			//하위모듈을 EJB CALL or JMS CALL 판단하는 값 읽어오기
			szIS_EJB_CALL = ydDaoUtils.paraRecChkNull(inDto, "IS_EJB_CALL");
			
			if( szIS_EJB_CALL.equals("Y") ) {
				//하위모듈을 EJB CALL or JMS CALL 판단 값 설정
				recPara.setField("IS_EJB_CALL", szIS_EJB_CALL);
				//EJB Call
				szEjbJndiName = "CarMvHdSeEJB";
				szEjbMethod = "procMatlCarArr";
				
				szLogMsg = "[JSP Session]차량진행관리 BACKUP - 구내운송 공차도착 EJB CALL[" + szEjbJndiName + "." + szEjbMethod + "] 시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				ejbConn = new EJBConnector("default", szEjbJndiName, this);
				szRtnMsg = (String)ejbConn.trx(szEjbMethod, new Class[] { JDTORecord.class }, new Object[] { recPara });
				
				szLogMsg = "[JSP Session]차량진행관리 BACKUP - 구내운송 공차도착 EJB CALL[" + szEjbJndiName + "." + szEjbMethod + "] 성공";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			}else{
				ydDelegate       = new YdDelegate();
				ydDelegate.sendMsg(recPara);	
				szLogMsg = "[JSP Session]차량진행관리 BACKUP - 구내운송 공차도착 JMS CALL[" + szEjbJndiName + "." + szEjbMethod + "] 성공";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				szRtnMsg = YdConstant.RETN_CD_SUCCESS;
			}
			
		}catch(Exception e){
			szLogMsg = "[JSP Session]차량진행관리 BACKUP - 구내운송 공차도착 오류발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(),e);			
		}
		
		szLogMsg = "JSP-SESSION [ 차량진행관리 BACKUP - 구내운송 - 공차도착] 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
		
		return szRtnMsg;
	}
	
	
	
	
	
	
	/**
	 *  차량진행관리 BACKUP - 구내운송 - 영차도착(하차도착)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return String
	 * @throws DAOException
	 */	
	
	public String ydCarLLArr(JDTORecord inDto) throws DAOException {
		JDTORecord     recPara      = null;
		JDTORecord     recPosPara   = null;
		JDTORecord     recPos       = null;
		JDTORecord     recSchIdPara = null;
		JDTORecordSet  rsRtn  		= null;
		JDTORecordSet  rsRtnPos 	= null;
		JDTORecord 	   recRtnSpec 	= null;
		JDTORecordSet  rsRtnSpec 	= null;
		
		
		YdDelegate   ydDelegate    = null;
		YdCarSchDao  ydCarSchDao   = new YdCarSchDao();
		YdStkColDao  ydStkColDao   = new YdStkColDao();
		YdCarSpecDao ydCarSpecDao  = new YdCarSpecDao();
		
		String szMethodName = "ydCarLLArr";
		String szOperationName = "구내운송 - 영차도착(하차도착)";
		String szSchId = "";
		String szLogMsg = "";
		String szRtnMsg = YdConstant.RETN_CD_FAILURE;
		String szEjbJndiName = null;
		String szEjbMethod = null;
		String szIS_EJB_CALL = null;
		
		EJBConnector ejbConn = null;
		int intGp = 0;
		
		try{
			
			
			
			szLogMsg = "JSP-SESSION [ 차량진행관리 BACKUP - 구내운송 - 영차도착(하차도착)] 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			recPara   		= JDTORecordFactory.getInstance().create();
			recSchIdPara	= JDTORecordFactory.getInstance().create();
			recPosPara		= JDTORecordFactory.getInstance().create();
			recPos			= JDTORecordFactory.getInstance().create();
			recRtnSpec		= JDTORecordFactory.getInstance().create();
			
			szSchId = yddatautil.setDataDefault(inDto.getField("YD_CAR_SCH_ID"), "");
			
			rsRtn  = JDTORecordFactory.getInstance().createRecordSet("YD");
			
			recSchIdPara.setField("YD_CAR_SCH_ID",szSchId );
			intGp = ydCarSchDao.getYdCarsch(recSchIdPara, rsRtn, 0);
			
			if (intGp <= 0 ){
				
				szLogMsg = "[JSP Session]차량진행관리 BACKUP - 구내운송 영차도착(하차도착) : 해당 차량스케줄["+szSchId+"]에 대한 정보가 없습니다!!";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);					
				return YdConstant.RETN_CD_NOTEXIST; 
				
			}
			
			rsRtn.first();
			recSchIdPara = rsRtn.getRecord();
			
			
			recPara.setField("JMS_TC_CD","YDYDJ650");	
			recPara.setField("TRN_EQP_CD", recSchIdPara.getField("TRN_EQP_CD"));
			
			
			//하차정지위치에서 데이터를 가지고 온다.
			rsRtnPos  = JDTORecordFactory.getInstance().createRecordSet("YD");
			recPosPara.setField("YD_STK_COL_GP", recSchIdPara.getField("YD_CARUD_STOP_LOC"));						
			intGp = ydStkColDao.getYdStkcol(recPosPara, rsRtnPos, 0);
				
			
			if(intGp <=0 ){
				szLogMsg = "[JSP Session]차량진행관리 BACKUP - 구내운송 영차도착(하차도착) : 적치열정보["+ recSchIdPara.getFieldString("YD_CARUD_STOP_LOC") +"]에서 해당 위치 정보를 가져올수 없습니다!";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);					
				return YdConstant.RETN_CD_FAILURE; 
				
			}
			
			rsRtnPos.first();
			recPos = rsRtnPos.getRecord();
			

			//개소코드
			recPara.setField("ARR_WLOC_CD", recPos.getField("WLOC_CD"));
			//야드포인트코드 
			recPara.setField("ARR_YD_PNT_CD", recPos.getField("YD_PNT_CD"));
			
			recPara.setField("TRN_WRK_FULLVOID_GP", "F");
			
			rsRtnSpec  = JDTORecordFactory.getInstance().createRecordSet("YD");
			
			//TRN_EQP_CD 로 정보를 찾는다.						
			intGp = ydCarSpecDao.getYdCarspec(recPara, rsRtnSpec, 2);
			if(intGp<=0){
				szLogMsg = "[JSP Session]차량진행관리 BACKUP - 구내운송 영차도착(하차도착) : 해당 스펙정보["+recSchIdPara.getFieldString("TRN_EQP_CD")+"]가 없습니다 !";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);					
				return YdConstant.RETN_CD_FAILURE; 
			}			
			
			rsRtnSpec.first();
			recRtnSpec = rsRtnSpec.getRecord();						
			recPara.setField("TRN_EQP_STK_CAPA", recRtnSpec.getField("YD_WRK_ALW_WT"));
			
			//CAR_ARR_DT			
			recPara.setField("CAR_ARR_DT", recSchIdPara.getField("YD_CARUD_ARR_DT"));

			ydUtils.displayRecord(szOperationName, recPara);
			
			//하위모듈을 EJB CALL or JMS CALL 판단하는 값 읽어오기
			szIS_EJB_CALL = ydDaoUtils.paraRecChkNull(inDto, "IS_EJB_CALL");
			
			if( szIS_EJB_CALL.equals("Y") ) {
				//하위모듈을 EJB CALL or JMS CALL 판단 값 설정
				recPara.setField("IS_EJB_CALL", szIS_EJB_CALL);
				//EJB Call
				szEjbJndiName = "CarMvHdSeEJB";
				szEjbMethod = "procMatlCarArr";
				
				szLogMsg = "[JSP Session]차량진행관리 BACKUP - 구내운송 영차도착(하차도착) EJB CALL[" + szEjbJndiName + "." + szEjbMethod + "] 시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				ejbConn = new EJBConnector("default", szEjbJndiName, this);
				szRtnMsg = (String)ejbConn.trx(szEjbMethod, new Class[] { JDTORecord.class }, new Object[] { recPara });
				
				szLogMsg = "[JSP Session]차량진행관리 BACKUP - 구내운송 영차도착(하차도착) EJB CALL[" + szEjbJndiName + "." + szEjbMethod + "] 성공";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			}else{
				ydDelegate       = new YdDelegate();
				ydDelegate.sendMsg(recPara);	
				szLogMsg = "[JSP Session]차량진행관리 BACKUP - 구내운송 영차도착(하차도착) JMS CALL[" + szEjbJndiName + "." + szEjbMethod + "] 성공";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				szRtnMsg = YdConstant.RETN_CD_SUCCESS;
			}	
			
		}catch(Exception e){
			szLogMsg = "[JSP Session]차량진행관리 BACKUP - 구내운송 영차도착(하차도착) 오류발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(),e);			
		}
		
		szLogMsg = "JSP-SESSION [ 차량진행관리 BACKUP - 구내운송 - 영차도착(하차도착)] 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return szRtnMsg;
	}
	
	
	
	
	
	
	
	
	/**
	 *  차량진행관리 BACKUP - 구내운송 - 상차 완료
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */	
	
	public String ydCarLLdCmpl(JDTORecord inDto) throws DAOException {
		JDTORecord     recPara      = null;
		JDTORecord     recStopPos      = null;
		
		YdDelegate   ydDelegate    = new YdDelegate();
		YdCarSchDao  ydCarSchDao   = new YdCarSchDao();
		YdStkLyrDao  ydStkLyrDao = new YdStkLyrDao();
		
		String szMsg = "";
		String szYD_CAR_SCH_ID = "";
		String szYdStkPos = "";
		String szMethodName ="ydCarLLdCmpl";
		String szYdGp ="";
		String szMtlStat  = "";
		JDTORecordSet  outRecSet 	= null;
		
		
		int intGp = 0;
		int nCount = 0;
		
		
		
		try{
			
			szMsg = "JSP-SESSION [차량진행관리 BACKUP - 구내운송 - 상차 완료] 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			recPara   		= JDTORecordFactory.getInstance().create();
			
			szYD_CAR_SCH_ID = yddatautil.setDataDefault(inDto.getField("YD_CAR_SCH_ID"), "");
			szYdGp = yddatautil.setDataDefault(inDto.getField("YD_GP"), "");
			
			
			szYdStkPos = yddatautil.setDataDefault(inDto.getFieldString("T_STOP_LOC"),"        ");
			recStopPos =  JDTORecordFactory.getInstance().create();
			recStopPos.setField("YD_STK_COL_GP", szYdStkPos.substring(0,6));
			recStopPos.setField("YD_STK_BED_NO", szYdStkPos.substring(6,8));
			
			
			outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");		
			ydStkLyrDao.getYdStklyr(recStopPos, outRecSet, 1);
			
			/////////////////////////////////////////////////////////////////////
            //해당 상차 위치에 베드를 검색하여 정보를 조회한다.
			//////////////////////////////////////////////////////////////////////
			
			
			
			if (outRecSet.size()  < 0){
				//Dao Error
				
				//업무간 데이타가 공유되지 않아서 현재는 예외를 던지지 않음
				szMsg="차량베드 테이블 조회 오류";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
				return YdConstant.RETN_CD_FAILURE;
				
			}else if(outRecSet.size() == 0 ){
				//해당 대차 베드위에 정보가 하나도 없을경우
				szMsg="해당 대차 베드위에 정보가  하나도 존재하지 않습니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return YdConstant.RETN_CD_FAILURE;
				
				
				
				
			}else if(outRecSet.size() >  0 ){
				// 정보 존재시
				
				outRecSet.first();
				
				recPara   		= JDTORecordFactory.getInstance().create();
				
				do{
					recPara = outRecSet.getRecord();
					szMtlStat = yddatautil.setDataDefault(inDto.getFieldString("YD_STK_LYR_MTL_STAT"),"");
					if("D".equals(szMtlStat))
						nCount++;		
					
				}while(outRecSet.next());						
			}
		
			
			if(nCount > 0){
				
				//
				szMsg="크레인 작업지시가 있으므로 작업을 할 수 없습니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return YdConstant.RETN_CD_FAILURE;
				
			}
			
			
			//////////////////
			
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CAR_PROG_STAT", "5");
			recPara.setField("YD_EQP_WRK_STAT", "L");
			recPara.setField("YD_CARLD_CMPL_DT", 	ydUtils.getCurDate("yyyyMMddHHmmss"));
			recPara.setField("YD_CAR_SCH_ID",szYD_CAR_SCH_ID);
			
			
			
			intGp = ydCarSchDao.updYdCarsch(recPara, 0);
			
			if(intGp <= 0) {
				szMsg="차량스케줄에 상차 완료 등록시 Error!! Code : " + intGp;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			recPara = JDTORecordFactory.getInstance().create();
			
			intGp = YdCommonUtils.procCarLoadCmpl(szYD_CAR_SCH_ID);
			
			if( intGp <= 0 ) {
				//업무간 데이타가 공유되지 않아서 현재는 예외를 던지지 않음
				szMsg="상차완료시 공통테이블 업데이트 처리 실패";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}else{
				szMsg="상차완료시 공통테이블 업데이트 처리 성공";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}
			//--------------- 상차완료시 공통테이블 업데이트 처리 끝 ----------------------
			
			//상차작업완료 송신 YDTSJ008 (구내운송 상차완료)
			recPara.setField("MSG_ID",        "YDTSJ008");
			szMsg="구내운송 상차작업완료 송신 : YDTSJ008";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			// 스케줄 코드는 내부 프로세스 처리를 위하여 추가 된부분이므로 없어도 상관이 없음
			// 추후 필요할 경우는 상차 작업예약 ID 에 물려있는 스케줄 코드를 조회하여 보내주도록 한다.
			
			recPara.setField("YD_SCH_CD",     "");
			recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
			recPara.setField("YD_GP",         szYdGp);
			
			ydDelegate.sendMsg(recPara);
			
			szMsg="상차작업완료 송신 완료";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);			
		}
		
		szMsg = "JSP-SESSION [차량진행관리 BACKUP - 구내운송 - 상차 완료] 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	
	
	/**
	 *  차량진행관리 BACKUP - 이송(출하) - 공차도착
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */	
	
	public void ydCarGUArr(JDTORecord inDto) throws DAOException {
		JDTORecord     recPara      = null;	
		JDTORecord     recPos       = null;
		JDTORecord     recSchIdPara = null;
		JDTORecordSet  rsRtn  		= null;
		JDTORecordSet  rsRtnPos 	= null;
		JDTORecord 	   recRtnSpec 	= null;
		JDTORecordSet  rsRtnSpec 	= null;
		JDTORecord     recPosPara   = null;
		
		YdDelegate     ydDelegate   = new YdDelegate();
		YdCarSchDao    ydCarSchDao  = new YdCarSchDao();	
		YdCarSpecDao   ydCarSpecDao = new YdCarSpecDao();
		YdStkColDao    ydStkColDao  = new YdStkColDao();
		
		String szSchId              = null;
		String szYdGp               = "";
		String szLogMsg             = "";
		int intGp = 0;
		String szMethodName="ydCarGUArr";	
		String szOperationName = "이송(출하) - 공차도착";
		
		
		try{
			
			szLogMsg = "JSP-SESSION [차량진행관리 BACKUP - 이송(출하) - 공차도착] 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			// 현재는 외판슬라브출하차량도착실적 BackUp 만적용 추후 야드구분에 따른 데이터 편집 추가해야함
			
			
			szYdGp = yddatautil.setDataDefault(inDto.getField("YD_GP"), "");
			
			
			if ("".equals(szYdGp)){
				szLogMsg = "야드구분이 존재하지 않습니다";
				ydUtils.putLog(szSessionName, "ydCarGUArr", szLogMsg, YdConstant.ERROR);
				return;
			}
			
			
			
			
			
			//C연주 슬라브야드 [외판슬라브야드]
			//JMS_TC_CD	
			//TRANS_ORD_DT	
			//TRANS_ORD_SEQNO	
			//CARD_NO	
			//CAR_NO	
			//SPOS_WLOC_CD	
			//SPOS_YD_PNT_CD	
			//YD_EQP_GP	
			//YD_WRK_ALW_L	
			//YD_WRK_ALW_W	
			//YD_WRK_ALW_SKID_PITCH	
			//YD_WRK_ALW_SH	
			//YD_WRK_ALW_WT

			if("A".equals(szYdGp) ||
			   "M".equals(szYdGp)    //항만슬라브야드 기능추가 - 2015.12.30 LeeJY
			   ){
				recPara   		= JDTORecordFactory.getInstance().create();		
				recPosPara   	= JDTORecordFactory.getInstance().create();
				recSchIdPara 	= JDTORecordFactory.getInstance().create();
				recPos			= JDTORecordFactory.getInstance().create();
				recRtnSpec		= JDTORecordFactory.getInstance().create();
				
				recPara.setField("JMS_TC_CD","YDYDJ652");
				recPara.setField("TRANS_ORD_DT",		YdUtils.getCurDate("yyyyMMddHHmmss"));
			    
				szSchId = yddatautil.setDataDefault(inDto.getField("YD_CAR_SCH_ID"), "");
				
				rsRtn  = JDTORecordFactory.getInstance().createRecordSet("YD");
				recSchIdPara.setField("YD_CAR_SCH_ID",szSchId );
				intGp = ydCarSchDao.getYdCarsch(recSchIdPara, rsRtn, 0);
				
				if (intGp <= 0 ){
					szLogMsg = "해당 차량스케줄에 대한 정보가 없습니다";
					ydUtils.putLog(szSessionName, "ydCarGUArr", szLogMsg, YdConstant.ERROR);
					return;
					
				}
				
				rsRtn.first();
				recSchIdPara = rsRtn.getRecord();
				
				recPara.setField("TRANCE_ORD_SEQNO",	recSchIdPara.getField("TRANCE_ORD_SEQNO"));
				
				recPosPara.setField("YD_STK_COL_GP", recSchIdPara.getField("YD_CARLD_STOP_LOC"));
				
				rsRtnPos  = JDTORecordFactory.getInstance().createRecordSet("YD");
				intGp = ydStkColDao.getYdStkcol(recPosPara, rsRtnPos, 0);
				
				if(intGp <=0 ){
					szLogMsg = "적치열정보에서 해당 위치 정보를 가져올수 없습니다!";
					ydUtils.putLog(szSessionName, "ydCarGUArr", szLogMsg, YdConstant.ERROR);
					return;
					
				}
				
			
				rsRtnPos.first();
				recPos = rsRtnPos.getRecord();
				
				recPara.setField("CARD_NO",        recSchIdPara.getField("CARD_NO"));
				recPara.setField("CAR_NO",         recSchIdPara.getField("CAR_NO"));
				
				recPara.setField("SPOS_WLOC_CD",   recPos.getField("WLOC_CD"));
				recPara.setField("SPOS_YD_PNT_CD", recPos.getField("YD_PNT_CD"));		
				recPara.setField("YD_EQP_GP",	   recSchIdPara.getField("YD_EQP_GP"));
				
				rsRtnSpec  = JDTORecordFactory.getInstance().createRecordSet("YD");				

				//TRN_EQP_CD 로 정보를 찾는다.						
				intGp = ydCarSpecDao.getYdCarspec(recPara, rsRtnSpec, 2);
					
				

				if(intGp <=0 ){
					szLogMsg = "해당 스펙정보가 없습니다 !!";
					ydUtils.putLog(szSessionName, "ydCarGUArr", szLogMsg, YdConstant.ERROR);
					return;
					
				}
				
				
				rsRtnSpec.first();
				recRtnSpec = rsRtnSpec.getRecord();			
				
				recPara.setField("YD_WRK_ALW_L", 			recRtnSpec.getField("YD_WRK_ALW_L"));
				recPara.setField("YD_WRK_ALW_W", 			recRtnSpec.getField("YD_WRK_ALW_W"));
				recPara.setField("YD_WRK_ALW_SKID_PITCH", 	recRtnSpec.getField("YD_WRK_ALW_SKID_PITCH"));
				recPara.setField("YD_WRK_ALW_SH", 			recRtnSpec.getField("YD_WRK_ALW_SH"));
				recPara.setField("YD_WRK_ALW_WT", 			recRtnSpec.getField("YD_WRK_ALW_WT"));
			}
			
			//코일제품야드 
			//JMS_TC_CD	
			//TRANS_ORD_DT	
			//TRANS_ORD_SEQNO	
			//CAR_NO	
			//CARD_NO	
			//SPOS_WLOC_CD	
			//SPOS_YD_PNT_CD

			else if("J".equals(szYdGp)){
				
				recPara   		= JDTORecordFactory.getInstance().create();		
				recPosPara   	= JDTORecordFactory.getInstance().create();
				recSchIdPara 	= JDTORecordFactory.getInstance().create();
				recPos			= JDTORecordFactory.getInstance().create();
				recRtnSpec		= JDTORecordFactory.getInstance().create();
				
				recPara.setField("JMS_TC_CD","YDYDJ653");
				recPara.setField("TRANS_ORD_DT",		YdUtils.getCurDate("yyyyMMddHHmmss"));
				//recPara.setField("TRANS_ORD_DT",		inDto.getField("TRANS_ORD_DATE"));
				
				szSchId = yddatautil.setDataDefault(inDto.getField("YD_CAR_SCH_ID"), "");
				
				rsRtn  = JDTORecordFactory.getInstance().createRecordSet("YD");
				recSchIdPara.setField("YD_CAR_SCH_ID",szSchId );
				intGp = ydCarSchDao.getYdCarsch(recSchIdPara, rsRtn, 0);
				
			

				if(intGp <=0 ){
					szLogMsg = "해당 차량스케줄에 대한 정보가 없습니다!";
					ydUtils.putLog(szSessionName, "ydCarGUArr", szLogMsg, YdConstant.ERROR);
					return;
					
				}
				
				
				rsRtn.first();
				recSchIdPara = rsRtn.getRecord();
				
				recPara.setField("TRANCE_ORD_SEQNO",	recSchIdPara.getField("TRANCE_ORD_SEQNO"));
				
				recPosPara.setField("YD_STK_COL_GP", recSchIdPara.getField("YD_CARLD_STOP_LOC"));
				
				rsRtnPos  = JDTORecordFactory.getInstance().createRecordSet("YD");
				intGp = ydStkColDao.getYdStkcol(recPosPara, rsRtnPos, 0);
				
		
				if(intGp <=0 ){
					szLogMsg = "적치열정보에서 해당 위치 정보를 가져올수 없습니다!";
					ydUtils.putLog(szSessionName, "ydCarGUArr", szLogMsg, YdConstant.ERROR);
					return;
				}
				
			
				rsRtnPos.first();
				recPos = rsRtnPos.getRecord();
				
				recPara.setField("CARD_NO",        recSchIdPara.getField("CARD_NO"));
				recPara.setField("CAR_NO",         recSchIdPara.getField("CAR_NO"));
				
				recPara.setField("SPOS_WLOC_CD",   recPos.getField("WLOC_CD"));
				recPara.setField("SPOS_YD_PNT_CD", recPos.getField("YD_PNT_CD"));				
			}
			
			//코일 임가공 (소재 ) 
			//JMS_TC_CD	
			//TRANS_ORD_DT	
			//TRANS_ORD_SEQNO	
			//CAR_NO	
			//CARD_NO	
			//SPOS_WLOC_CD	
			//SPOS_YD_PNT_CD	
			//RENTPROC_CD	
			//YD_EQP_GP	
			//YD_WRK_ALW_L	
			//YD_WRK_ALW_W	
			//YD_WRK_ALW_SKID_PITCH	
			//YD_WRK_ALW_SH
			//YD_WRK_ALW_WT


			else if("H".equals(szYdGp)){
				
				recPara   		= JDTORecordFactory.getInstance().create();		
				recPosPara   	= JDTORecordFactory.getInstance().create();
				recSchIdPara 	= JDTORecordFactory.getInstance().create();
				recPos			= JDTORecordFactory.getInstance().create();
				recRtnSpec		= JDTORecordFactory.getInstance().create();
				
				recPara.setField("JMS_TC_CD","YDYDJ654");
				recPara.setField("TRANS_ORD_DT",		YdUtils.getCurDate("yyyyMMddHHmmss"));
				//recPara.setField("TRANS_ORD_DT",		inDto.getField("TRANS_ORD_DATE"));
				
				szSchId = yddatautil.setDataDefault(inDto.getField("YD_CAR_SCH_ID"), "");
				
				rsRtn  = JDTORecordFactory.getInstance().createRecordSet("YD");
				recSchIdPara.setField("YD_CAR_SCH_ID",szSchId );
				intGp = ydCarSchDao.getYdCarsch(recSchIdPara, rsRtn, 0);
				
				
				
				if(intGp <=0 ){
					szLogMsg = "해당 차량스케줄에 대한 정보가 없습니다!";
					ydUtils.putLog(szSessionName, "ydCarGUArr", szLogMsg, YdConstant.ERROR);
					return;
				}
				
				rsRtn.first();
				recSchIdPara = rsRtn.getRecord();
				
				recPara.setField("TRANCE_ORD_SEQNO",	recSchIdPara.getField("TRANCE_ORD_SEQNO"));
				
				recPosPara.setField("YD_STK_COL_GP", recSchIdPara.getField("YD_CARLD_STOP_LOC"));
				
				rsRtnPos  = JDTORecordFactory.getInstance().createRecordSet("YD");
				intGp = ydStkColDao.getYdStkcol(recPosPara, rsRtnPos, 0);
				
				if(intGp <=0 ){
					szLogMsg = "적치열정보에서 해당 위치 정보를 가져올수 없습니다!";
					ydUtils.putLog(szSessionName, "ydCarGUArr", szLogMsg, YdConstant.ERROR);
					return;
				}
				
				
			
				rsRtnPos.first();
				recPos = rsRtnPos.getRecord();
				
				recPara.setField("CARD_NO",        recSchIdPara.getField("CARD_NO"));
				recPara.setField("CAR_NO",         recSchIdPara.getField("CAR_NO"));
				
				recPara.setField("SPOS_WLOC_CD",   recPos.getField("WLOC_CD"));
				recPara.setField("SPOS_YD_PNT_CD", recPos.getField("YD_PNT_CD"));				
				recPara.setField("RENTPROC_CD",	   recSchIdPara.getField("RENTPROC_CD"));				
				recPara.setField("YD_EQP_GP",	   recSchIdPara.getField("YD_EQP_GP"));
				
				rsRtnSpec  = JDTORecordFactory.getInstance().createRecordSet("YD");				

				//TRN_EQP_CD 로 정보를 찾는다.						
				intGp = ydCarSpecDao.getYdCarspec(recPara, rsRtnSpec, 2);
					
				
				if(intGp <=0 ){
					szLogMsg = "해당 스펙정보가 없습니다 !";
					ydUtils.putLog(szSessionName, "ydCarGUArr", szLogMsg, YdConstant.ERROR);
					return;
				}
				
				rsRtnSpec.first();
				recRtnSpec = rsRtnSpec.getRecord();			
				
				recPara.setField("YD_WRK_ALW_L", 			recRtnSpec.getField("YD_WRK_ALW_L"));
				recPara.setField("YD_WRK_ALW_W", 			recRtnSpec.getField("YD_WRK_ALW_W"));
				recPara.setField("YD_WRK_ALW_SKID_PITCH", 	recRtnSpec.getField("YD_WRK_ALW_SKID_PITCH"));
				recPara.setField("YD_WRK_ALW_SH", 			recRtnSpec.getField("YD_WRK_ALW_SH"));
				recPara.setField("YD_WRK_ALW_WT", 			recRtnSpec.getField("YD_WRK_ALW_WT"));
			}
			
			//후판제품야드
			//JMS_TC_CD	
			//TRANS_ORD_DT	
			//TRANS_ORD_SEQNO	
			//CAR_NO	
			//CARD_NO	
			//SPOS_WLOC_CD	
			//SPOS_YD_PNT_CD

			else if("K".equals(szYdGp)){
				
				recPara   		= JDTORecordFactory.getInstance().create();		
				recPosPara   	= JDTORecordFactory.getInstance().create();
				recSchIdPara 	= JDTORecordFactory.getInstance().create();
				recPos			= JDTORecordFactory.getInstance().create();
				recRtnSpec		= JDTORecordFactory.getInstance().create();
				
				recPara.setField("JMS_TC_CD","YDYDJ655");
				recPara.setField("TRANS_ORD_DT",		YdUtils.getCurDate("yyyyMMddHHmmss"));
			
				szSchId = yddatautil.setDataDefault(inDto.getField("YD_CAR_SCH_ID"), "");
				
				rsRtn  = JDTORecordFactory.getInstance().createRecordSet("YD");
				recSchIdPara.setField("YD_CAR_SCH_ID",szSchId );
				intGp = ydCarSchDao.getYdCarsch(recSchIdPara, rsRtn, 0);
				
			
				if(intGp <=0 ){
					szLogMsg = "해당 차량스케줄에 대한 정보가 없습니다!";
					ydUtils.putLog(szSessionName, "ydCarGUArr", szLogMsg, YdConstant.ERROR);
					return;
				}
				
				
				
				rsRtn.first();
				recSchIdPara = rsRtn.getRecord();
				
				recPara.setField("TRANCE_ORD_SEQNO",	recSchIdPara.getField("TRANCE_ORD_SEQNO"));				
				recPosPara.setField("YD_STK_COL_GP", recSchIdPara.getField("YD_CARLD_STOP_LOC"));				
				rsRtnPos  = JDTORecordFactory.getInstance().createRecordSet("YD");
				
				intGp = ydStkColDao.getYdStkcol(recPosPara, rsRtnPos, 0);
				
				if(intGp <=0 ){
					szLogMsg = "적치열정보에서 해당 위치 정보를 가져올수 없습니다!";
					ydUtils.putLog(szSessionName, "ydCarGUArr", szLogMsg, YdConstant.ERROR);
					return;
				}
				
				rsRtnPos.first();
				recPos = rsRtnPos.getRecord();
				
				recPara.setField("CARD_NO",        recSchIdPara.getField("CARD_NO"));
				recPara.setField("CAR_NO",         recSchIdPara.getField("CAR_NO"));
				
				recPara.setField("SPOS_WLOC_CD",   recPos.getField("WLOC_CD"));
				recPara.setField("SPOS_YD_PNT_CD", recPos.getField("YD_PNT_CD"));				
			}
			
			
			ydUtils.displayRecord(szOperationName, recPara);
			ydDelegate.sendMsg(recPara);		
			
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);			
		}
		
		
		szLogMsg = "JSP-SESSION [차량진행관리 BACKUP - 이송(출하) - 공차도착] 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
	}
	
	
	
	
	
	/**
	 *  차량진행관리 BACKUP - 이송(출하) - 영차도착
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */	
	
	public void ydCarGLArr(JDTORecord inDto) throws DAOException {
		JDTORecord     recPara      = null;	
		JDTORecord     recPos       = null;
		JDTORecord     recSchIdPara = null;
		JDTORecordSet  rsRtn  		= null;
		JDTORecordSet  rsRtnPos 	= null;
		JDTORecord 	   recRtnSpec 	= null;
		JDTORecordSet  rsRtnSpec 	= null;
		JDTORecord     recPosPara   = null;
		
		YdDelegate     ydDelegate   = new YdDelegate();
		YdCarSchDao    ydCarSchDao  = new YdCarSchDao();	
		YdCarSpecDao   ydCarSpecDao = new YdCarSpecDao();
		YdStkColDao    ydStkColDao  = new YdStkColDao();
		
		String szSchId = null;
		String szYdGp = "";
		String szLogMsg = "";
		int intGp = 0;
		String szMethodName="ydCarGLArr";	
		String szOperationName = "이송(출하) - 영차도착";
		
		
		try{
			
			szLogMsg = "JSP-SESSION [차량진행관리 BACKUP - 이송(출하) - 영차도착]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			
			// 현재는 외판슬라브출하차량도착실적 BackUp 만적용 추후 야드구분에 따른 데이터 편집 추가해야함
			
			
			szYdGp = yddatautil.setDataDefault(inDto.getField("YD_GP"), "");
			
			
			if ("".equals(szYdGp)){
				szLogMsg = "야드구분이 존재하지 않습니다";
				ydUtils.putLog(szSessionName, "ydCarGLArr", szLogMsg, YdConstant.ERROR);
				return;
			}
			
			
			
			
			
			//C연주 슬라브야드 [외판슬라브야드]
			//JMS_TC_CD	
			//TRANS_ORD_DT	
			//TRANS_ORD_SEQNO	
			//CARD_NO	
			//CAR_NO	
			//SPOS_WLOC_CD	
			//SPOS_YD_PNT_CD	
			//YD_EQP_GP	
			//YD_WRK_ALW_L	
			//YD_WRK_ALW_W	
			//YD_WRK_ALW_SKID_PITCH	
			//YD_WRK_ALW_SH	
			//YD_WRK_ALW_WT

			if("A".equals(szYdGp) ||
			   "M".equals(szYdGp)     //항만슬라브야드 기능추가 - 2015.12.30 LeeJY
				){
				recPara   		= JDTORecordFactory.getInstance().create();		
				recPosPara   	= JDTORecordFactory.getInstance().create();
				recSchIdPara 	= JDTORecordFactory.getInstance().create();
				recPos			= JDTORecordFactory.getInstance().create();
				recRtnSpec		= JDTORecordFactory.getInstance().create();
				
				recPara.setField("JMS_TC_CD","YDYDJ652");
				recPara.setField("TRANS_ORD_DT",		YdUtils.getCurDate("yyyyMMddHHmmss"));
				//recPara.setField("TRANS_ORD_DT",		inDto.getField("TRANS_ORD_DATE"));
				
				szSchId = yddatautil.setDataDefault(inDto.getField("YD_CAR_SCH_ID"), "");
				
				rsRtn  = JDTORecordFactory.getInstance().createRecordSet("YD");
				recSchIdPara.setField("YD_CAR_SCH_ID",szSchId );
				intGp = ydCarSchDao.getYdCarsch(recSchIdPara, rsRtn, 0);
				
				if (intGp <= 0 ){
					szLogMsg = "해당 차량스케줄에 대한 정보가 없습니다";
					ydUtils.putLog(szSessionName, "ydCarGLArr", szLogMsg, YdConstant.ERROR);					
					return;
				}
				
				rsRtn.first();
				recSchIdPara = rsRtn.getRecord();
				
				recPara.setField("TRANCE_ORD_SEQNO",	recSchIdPara.getField("TRANCE_ORD_SEQNO"));
				
				recPosPara.setField("YD_STK_COL_GP", recSchIdPara.getField("YD_CARLD_STOP_LOC"));
				
				rsRtnPos  = JDTORecordFactory.getInstance().createRecordSet("YD");
				intGp = ydStkColDao.getYdStkcol(recPosPara, rsRtnPos, 0);
				
			
				
				if (intGp <= 0 ){
					szLogMsg = "적치열정보에서 해당 위치 정보를 가져올수 없습니다";
					ydUtils.putLog(szSessionName, "ydCarGLArr", szLogMsg, YdConstant.ERROR);					
					return;
				}
				
			
				rsRtnPos.first();
				recPos = rsRtnPos.getRecord();
				
				recPara.setField("CARD_NO",        recSchIdPara.getField("CARD_NO"));
				recPara.setField("CAR_NO",         recSchIdPara.getField("CAR_NO"));
				
				recPara.setField("SPOS_WLOC_CD",   recPos.getField("WLOC_CD"));
				recPara.setField("SPOS_YD_PNT_CD", recPos.getField("YD_PNT_CD"));		
				recPara.setField("YD_EQP_GP",	   recSchIdPara.getField("YD_EQP_GP"));
				
				rsRtnSpec  = JDTORecordFactory.getInstance().createRecordSet("YD");				

				//TRN_EQP_CD 로 정보를 찾는다.						
				intGp = ydCarSpecDao.getYdCarspec(recPara, rsRtnSpec, 2);
				
				if (intGp <= 0 ){
					szLogMsg = "해당 스펙정보가 없습니다 !!";
					ydUtils.putLog(szSessionName, "ydCarGLArr", szLogMsg, YdConstant.ERROR);					
					return;
				}
				
				
				rsRtnSpec.first();
				recRtnSpec = rsRtnSpec.getRecord();			
				
				recPara.setField("YD_WRK_ALW_L", 			recRtnSpec.getField("YD_WRK_ALW_L"));
				recPara.setField("YD_WRK_ALW_W", 			recRtnSpec.getField("YD_WRK_ALW_W"));
				recPara.setField("YD_WRK_ALW_SKID_PITCH", 	recRtnSpec.getField("YD_WRK_ALW_SKID_PITCH"));
				recPara.setField("YD_WRK_ALW_SH", 			recRtnSpec.getField("YD_WRK_ALW_SH"));
				recPara.setField("YD_WRK_ALW_WT", 			recRtnSpec.getField("YD_WRK_ALW_WT"));
			}
			
			//코일제품야드 
			//JMS_TC_CD	
			//TRANS_ORD_DT	
			//TRANS_ORD_SEQNO	
			//CAR_NO	
			//CARD_NO	
			//SPOS_WLOC_CD	
			//SPOS_YD_PNT_CD

			else if("J".equals(szYdGp)){
				
				recPara   		= JDTORecordFactory.getInstance().create();		
				recPosPara   	= JDTORecordFactory.getInstance().create();
				recSchIdPara 	= JDTORecordFactory.getInstance().create();
				recPos			= JDTORecordFactory.getInstance().create();
				recRtnSpec		= JDTORecordFactory.getInstance().create();
				
				recPara.setField("JMS_TC_CD","YDYDJ653");
				recPara.setField("TRANS_ORD_DT",		YdUtils.getCurDate("yyyyMMddHHmmss"));
			
				szSchId = yddatautil.setDataDefault(inDto.getField("YD_CAR_SCH_ID"), "");
				
				rsRtn  = JDTORecordFactory.getInstance().createRecordSet("YD");
				recSchIdPara.setField("YD_CAR_SCH_ID",szSchId );
				intGp = ydCarSchDao.getYdCarsch(recSchIdPara, rsRtn, 0);
				
				if (intGp <= 0 ){
					szLogMsg = "해당 차량스케줄에 대한 정보가 없습니다!";
					ydUtils.putLog(szSessionName, "ydCarGLArr", szLogMsg, YdConstant.ERROR);					
					return;
				}
				
				
				
				rsRtn.first();
				recSchIdPara = rsRtn.getRecord();
				
				recPara.setField("TRANCE_ORD_SEQNO",	recSchIdPara.getField("TRANCE_ORD_SEQNO"));
				recPosPara.setField("YD_STK_COL_GP", recSchIdPara.getField("YD_CARLD_STOP_LOC"));
				
				rsRtnPos  = JDTORecordFactory.getInstance().createRecordSet("YD");
				intGp = ydStkColDao.getYdStkcol(recPosPara, rsRtnPos, 0);
				
			
				if (intGp <= 0 ){
					szLogMsg = "적치열정보에서 해당 위치 정보를 가져올수 없습니다!";
					ydUtils.putLog(szSessionName, "ydCarGLArr", szLogMsg, YdConstant.ERROR);					
					return;
				}
				
			
				rsRtnPos.first();
				recPos = rsRtnPos.getRecord();
				
				recPara.setField("CARD_NO",        recSchIdPara.getField("CARD_NO"));
				recPara.setField("CAR_NO",         recSchIdPara.getField("CAR_NO"));
				
				recPara.setField("SPOS_WLOC_CD",   recPos.getField("WLOC_CD"));
				recPara.setField("SPOS_YD_PNT_CD", recPos.getField("YD_PNT_CD"));				
			}
			
			//코일 임가공 (소재 ) 
			//JMS_TC_CD	
			//TRANS_ORD_DT	
			//TRANS_ORD_SEQNO	
			//CAR_NO	
			//CARD_NO	
			//SPOS_WLOC_CD	
			//SPOS_YD_PNT_CD	
			//RENTPROC_CD	
			//YD_EQP_GP	
			//YD_WRK_ALW_L	
			//YD_WRK_ALW_W	
			//YD_WRK_ALW_SKID_PITCH	
			//YD_WRK_ALW_SH
			//YD_WRK_ALW_WT


			else if("H".equals(szYdGp)){
				
				recPara   		= JDTORecordFactory.getInstance().create();		
				recPosPara   	= JDTORecordFactory.getInstance().create();
				recSchIdPara 	= JDTORecordFactory.getInstance().create();
				recPos			= JDTORecordFactory.getInstance().create();
				recRtnSpec		= JDTORecordFactory.getInstance().create();
				
				recPara.setField("JMS_TC_CD","YDYDJ654");
				recPara.setField("TRANS_ORD_DT",		YdUtils.getCurDate("yyyyMMddHHmmss"));
				szSchId = yddatautil.setDataDefault(inDto.getField("YD_CAR_SCH_ID"), "");
				
				rsRtn  = JDTORecordFactory.getInstance().createRecordSet("YD");
				recSchIdPara.setField("YD_CAR_SCH_ID",szSchId );
				intGp = ydCarSchDao.getYdCarsch(recSchIdPara, rsRtn, 0);
				
			
				
				if (intGp <= 0 ){
					szLogMsg = "해당 차량스케줄에 대한 정보가 없습니다 ";
					ydUtils.putLog(szSessionName, "ydCarGLArr", szLogMsg, YdConstant.ERROR);					
					return;
				}
				
				
				rsRtn.first();
				recSchIdPara = rsRtn.getRecord();
				
				recPara.setField("TRANCE_ORD_SEQNO",	recSchIdPara.getField("TRANCE_ORD_SEQNO"));
				
				recPosPara.setField("YD_STK_COL_GP", recSchIdPara.getField("YD_CARLD_STOP_LOC"));
				
				rsRtnPos  = JDTORecordFactory.getInstance().createRecordSet("YD");
				intGp     = ydStkColDao.getYdStkcol(recPosPara, rsRtnPos, 0);
				
				
				if (intGp <= 0 ){
					szLogMsg = "적치열정보에서 해당 위치 정보를 가져올수 없습니다!  ";
					ydUtils.putLog(szSessionName, "ydCarGLArr", szLogMsg, YdConstant.ERROR);					
					return;
				}
				
			
				rsRtnPos.first();
				recPos = rsRtnPos.getRecord();
				
				recPara.setField("CARD_NO",        recSchIdPara.getField("CARD_NO"));
				recPara.setField("CAR_NO",         recSchIdPara.getField("CAR_NO"));
				
				recPara.setField("SPOS_WLOC_CD",   recPos.getField("WLOC_CD"));
				recPara.setField("SPOS_YD_PNT_CD", recPos.getField("YD_PNT_CD"));				
				recPara.setField("RENTPROC_CD",	   recSchIdPara.getField("RENTPROC_CD"));				
				recPara.setField("YD_EQP_GP",	   recSchIdPara.getField("YD_EQP_GP"));
				
				rsRtnSpec  = JDTORecordFactory.getInstance().createRecordSet("YD");				

				//TRN_EQP_CD 로 정보를 찾는다.						
				intGp = ydCarSpecDao.getYdCarspec(recPara, rsRtnSpec, 2);
			
				if (intGp <= 0 ){
					szLogMsg = "해당 스펙정보가 없습니다  ";
					ydUtils.putLog(szSessionName, "ydCarGLArr", szLogMsg, YdConstant.ERROR);					
					return;
				}
				
				
				rsRtnSpec.first();
				recRtnSpec = rsRtnSpec.getRecord();			
				
				recPara.setField("YD_WRK_ALW_L", 			recRtnSpec.getField("YD_WRK_ALW_L"));
				recPara.setField("YD_WRK_ALW_W", 			recRtnSpec.getField("YD_WRK_ALW_W"));
				recPara.setField("YD_WRK_ALW_SKID_PITCH", 	recRtnSpec.getField("YD_WRK_ALW_SKID_PITCH"));
				recPara.setField("YD_WRK_ALW_SH", 			recRtnSpec.getField("YD_WRK_ALW_SH"));
				recPara.setField("YD_WRK_ALW_WT", 			recRtnSpec.getField("YD_WRK_ALW_WT"));
			}
			
			//후판제품야드
			//JMS_TC_CD	
			//TRANS_ORD_DT	
			//TRANS_ORD_SEQNO	
			//CAR_NO	
			//CARD_NO	
			//SPOS_WLOC_CD	
			//SPOS_YD_PNT_CD

			else if("K".equals(szYdGp)){
				
				recPara   		= JDTORecordFactory.getInstance().create();		
				recPosPara   	= JDTORecordFactory.getInstance().create();
				recSchIdPara 	= JDTORecordFactory.getInstance().create();
				recPos			= JDTORecordFactory.getInstance().create();
				recRtnSpec		= JDTORecordFactory.getInstance().create();
				
				recPara.setField("JMS_TC_CD","YDYDJ655");
				recPara.setField("TRANS_ORD_DT",		YdUtils.getCurDate("yyyyMMddHHmmss"));
				//recPara.setField("TRANS_ORD_DT",		inDto.getField("TRANS_ORD_DATE"));
				
				szSchId = yddatautil.setDataDefault(inDto.getField("YD_CAR_SCH_ID"), "");
				
				rsRtn  = JDTORecordFactory.getInstance().createRecordSet("YD");
				recSchIdPara.setField("YD_CAR_SCH_ID",szSchId );
				intGp = ydCarSchDao.getYdCarsch(recSchIdPara, rsRtn, 0);
				
				if (intGp <= 0 ){
					szLogMsg = "해당 차량스케줄에 대한 정보가 없습니다 ";
					ydUtils.putLog(szSessionName, "ydCarGLArr", szLogMsg,YdConstant.ERROR);					
					return;
				}
				
				rsRtn.first();
				recSchIdPara = rsRtn.getRecord();
				
				recPara.setField("TRANCE_ORD_SEQNO",	recSchIdPara.getField("TRANCE_ORD_SEQNO"));				
				recPosPara.setField("YD_STK_COL_GP", recSchIdPara.getField("YD_CARLD_STOP_LOC"));
				
				rsRtnPos  = JDTORecordFactory.getInstance().createRecordSet("YD");
				intGp     = ydStkColDao.getYdStkcol(recPosPara, rsRtnPos, 0);
			
				if (intGp <= 0 ){
					szLogMsg = "적치열정보에서 해당 위치 정보를 가져올수 없습니다!";
					ydUtils.putLog(szSessionName, "ydCarGLArr", szLogMsg, YdConstant.ERROR);					
					return;
				}
				
			
				rsRtnPos.first();
				recPos = rsRtnPos.getRecord();
				
				recPara.setField("CARD_NO",        recSchIdPara.getField("CARD_NO"));
				recPara.setField("CAR_NO",         recSchIdPara.getField("CAR_NO"));
				
				recPara.setField("SPOS_WLOC_CD",   recPos.getField("WLOC_CD"));
				recPara.setField("SPOS_YD_PNT_CD", recPos.getField("YD_PNT_CD"));				
			}
			
			ydUtils.displayRecord(szOperationName, recPara);
			ydDelegate.sendMsg(recPara);		
			
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);			
		}
		
		szLogMsg = "JSP-SESSION [차량진행관리 BACKUP - 이송(출하) - 영차도착] 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
		
	}
	
	
	
	/**
	 *  차량진행관리 - 상태 값 변경 Main
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return String
	 * @throws DAOException
	 */	
	
	public String updCarProgMgtBk(JDTORecord [] inDto) throws DAOException {
		String szRtnMsg = YdConstant.RETN_CD_FAILURE;
		String szLogMsg = null;
		String szMethodName = "updCarProgMgtBk";
		String szOldPosition = "차량 상태 값 변경 Main";
		String szYdGp = "";
		String szCarUseGp = "";
		String szCarProgStat = "";
		JDTORecord     recPara      = null;
		String szOperationName = "";
		
		
		String szNewPosition = "";
		
		
		try{
		
			
			szLogMsg = "JSP-SESSION [ 차량진행관리 - 상태 값 변경 Main] 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			for (int nLoop = 0 ; nLoop< inDto.length ; nLoop++)
			{
				// 야드구분
				szYdGp =  yddatautil.setDataDefault(inDto[nLoop].getField("T_STOP_LOC"), "");

				szLogMsg = "[JSP Session]차량진행관리 - 상태 값 변경, 야드구분[" + szYdGp + "]";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				
				//T_STOP_LOC
				
				//야드구분을  같이 정보에 넣어서 보내준다 
				recPara =  JDTORecordFactory.getInstance().create();					
				recPara.setRecord( inDto[nLoop]) ;			
			
				
				recPara.setField("YD_GP",szYdGp );
				
				ydUtils.displayRecord(szOperationName, recPara);
				
				
				
				
				//
				// TO 위치 변경 되었을시에  차량 정지 위치 (상차,하차 정보는 맞는 정보를 변경)(START)
				//
				
				
				szNewPosition = yddatautil.setDataDefault(inDto[nLoop].getField("T_STOP_LOC"), "");
				
				
				
				
				
				
				
				
				/////////////////////////////////////////////////////////////////////
				// --TO 위치 변경 되었을시에  차량 정지 위치 (상차,하차 정보는 맞는 정보를 변경)(END)
				/////////////////////////////////////////////////////////////////////
				
				
				//구내운송 , 출하 데이터를 확인 
				
				szCarUseGp = yddatautil.setDataDefault(recPara.getField("YD_CAR_USE_GP"), "");
				szCarProgStat  =yddatautil.setDataDefault(recPara.getField("YD_CAR_PROG_STAT"), "");
				
				//하위모듈을 EJB CALL[Y값으로 설정] or JMS CALL[값이 없거나 N값으로 설정]로 실행할 것인 지를 설정
				recPara.setField("IS_EJB_CALL", "Y");
				
				if("".equals(szCarUseGp))
				{
					szLogMsg = "[JSP Session]차량진행관리 - 상태 값 변경 : 차량사용구분 값이 없습니다!!";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					return YdConstant.RETN_CD_FAILURE;
					// 구내 운송 
				} else if("L".equals(szCarUseGp)){
					
					if("1".equals(szCarProgStat))			//상차출발 처리				
						szRtnMsg = this.ydCarLUDep(recPara);		
					else if("2".equals(szCarProgStat))		//상차도착  처리 
						szRtnMsg = this.ydCarLUArr(recPara);		
					else if("A".equals(szCarProgStat))		//하차출발  처리 
						szRtnMsg = this.ydCarLLDep(recPara);
					else if("B".equals(szCarProgStat))		//하차도착  처리 
						szRtnMsg = this.ydCarLLArr(recPara);
					else if("5".equals(szCarProgStat))		//상차 완료 처리 
					{
						szRtnMsg = this.ydCarLLdCmpl(recPara);
					}
					else if("E".equals(szCarProgStat))
					{
						//this.ydCarLUdCmpl(recPara);		//하차 완료 처리
					}
					
					// 출하(이송)
				} else if("G".equals(szCarUseGp)){
					if("1".equals(szCarProgStat))			//상차출발 처리					
						this.ydCarGUDep(recPara);
					else if("2".equals(szCarProgStat))		//상차도착  처리 
						this.ydCarGUArr(recPara);
					else if("A".equals(szCarProgStat))		//하차출발  처리 
						this.ydCarGLDep(recPara);
					else if("B".equals(szCarProgStat))		//하차도착  처리 
						this.ydCarGLArr(recPara);
					else if("5".equals(szCarProgStat))		//상차 완료 처리 
					{
						//this.ydCarGLdCmpl(recPara);		
					}
					else if("E".equals(szCarProgStat))		//하차 완료 처리
					{
						//this.ydCarGUdCmpl(recPara);		
					}
				}				
			}
		
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);			
		}
		
		szLogMsg = "JSP-SESSION [ 차량진행관리 - 상태 값 변경 Main] 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
		
		return szRtnMsg;
	}
	
	
	/**
	 * 준비작업스케줄 편성
	 * @param xlib.cmc.GridDat
	 * @return xlib.cmc.GridData
	 * @throws com.inisteel.cim.common.exception.DAOException
	 * @ejb.interface-method
	 * 
	 */
	 
   public JDTORecordSet getReadySch(JDTORecord inDto) throws DAOException {
	    JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		
		YdStkBedDao ydStkBedDao = new YdStkBedDao();
		
		String szMsg        = "";		
    	String szMethodName = "getReadySch";
		
    	String szYdStkColGp = "";
		String szYdGp       = "";
		String szYdBayGp    = "";
		String szYdEqpGp    = "";

		
		int intRtnVal = 0;
		
		try {
			szMsg = "JSP-SESSION [ 준비작업스케줄 편성] 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
			
			szYdGp    =   yddatautil.setDataDefault(inDto.getField("YD_GP"), "_");
			szYdBayGp =   yddatautil.setDataDefault(inDto.getField("YD_BAY_GP"), "_");
			szYdEqpGp =   yddatautil.setDataDefault(inDto.getField("YD_EQP_GP"), "__");
			
			szYdStkColGp = szYdGp+szYdBayGp+szYdEqpGp;
			
			recPara.setField("YD_STK_COL_GP1", szYdStkColGp );
			recPara.setField("YD_STK_COL_GP2", szYdStkColGp);
			recPara.setField("YD_STK_COL_GP3", szYdStkColGp);
			recPara.setField("YD_STK_COL_GP4", szYdStkColGp);
			recPara.setField("YD_STK_COL_GP5", szYdStkColGp);
			recPara.setField("YD_STK_COL_GP6", szYdStkColGp);
			
			recPara.setField("YD_AIM_RT_GP1",  yddatautil.setDataDefault(inDto.getField("YD_AIM_RT_GP"), ""));
			recPara.setField("YD_AIM_RT_GP2",  yddatautil.setDataDefault(inDto.getField("YD_AIM_RT_GP"), ""));
			recPara.setField("YD_AIM_RT_GP3",  yddatautil.setDataDefault(inDto.getField("YD_AIM_RT_GP"), ""));
			recPara.setField("YD_AIM_RT_GP4",  yddatautil.setDataDefault(inDto.getField("YD_AIM_RT_GP"), ""));
			recPara.setField("YD_AIM_RT_GP5",  yddatautil.setDataDefault(inDto.getField("YD_AIM_RT_GP"), ""));
			
			
			recPara.setField("PAGE_CNT1",	inDto.getField("PAGE_NO"));
			recPara.setField("PAGE_CNT2",	inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT1",	inDto.getField("ROWCOUNT"));
			recPara.setField("ROW_CNT2",	inDto.getField("ROWCOUNT"));
		
			
			
		
			intRtnVal      =  ydStkBedDao.getYdStkbed(recPara, outRecSet, 15);
			
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
			
			
					
			logger.println(LogLevel.DEBUG_TEXT, "getCrnList");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		szMsg = "JSP-SESSION [ 준비작업스케줄 편성] 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return outRecSet;
   }
	
   /**
	 * 준비작업스케줄 편성
	 * @param xlib.cmc.GridDat
	 * @return xlib.cmc.GridData
	 * @throws com.inisteel.cim.common.exception.DAOException
	 * @ejb.interface-method
	 * 
	 */
	 
	public JDTORecordSet getReadySchPage(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara        = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet      = JDTORecordFactory.getInstance().createRecordSet("retTmp");
	
		YdStkBedDao ydStkBedDao 		= new YdStkBedDao();
		
		String szMsg        			= "";		
		String szMethodName 			= "getReadySchPage";
		String szOperationName			= "준비작업스케줄 편성";
			
		String szYdStkColGp 			= "";
		String szYdGp       			= "";
		String szYdBayGp    			= "";
		String szYdEqpGp    			= "";
		String szYD_AIM_RT_GP			= null;
		String szSPOS_WLOC_CD			= null;								//발지개소코드
		String szARR_WLOC_CD			= null;								//착지개소코드
		String szMAIN_WRK_SEARCH_GP		= null;
		String szYD_AIM_BAY_GP          = null;
		
		int intRtnVal = 0;
		
		try {
			szMsg = "[Jsp Session : " + szOperationName + "] ----------------- 메소드 시작 -----------------";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
			
			szYdGp    				= yddatautil.setDataDefault(inDto.getField("YD_GP"), "_");
			szYdBayGp 				= yddatautil.setDataDefault(inDto.getField("YD_BAY_GP"), "_");
			szYdEqpGp 				= yddatautil.setDataDefault(inDto.getField("YD_EQP_GP"), "__");
			
			szYD_AIM_RT_GP			= yddatautil.setDataDefault(inDto.getField("YD_AIM_RT_GP"), "");
			szARR_WLOC_CD			= yddatautil.setDataDefault(inDto.getField("ARR_WLOC_CD"), "");
			
			szSPOS_WLOC_CD			= YdCommonUtils.getWlocCd(szYdGp);
			
			szMAIN_WRK_SEARCH_GP	= yddatautil.setDataDefault(inDto.getField("MAIN_WRK_SEARCH_GP"), "");
			
			szYD_AIM_BAY_GP			= yddatautil.setDataDefault(inDto.getField("YD_AIM_BAY_GP"), "");
			
			szYdStkColGp = szYdGp+szYdBayGp+szYdEqpGp;
			
			recPara.setField("YD_STK_COL_GP", 			szYdStkColGp );
			recPara.setField("YD_AIM_RT_GP",  			szYD_AIM_RT_GP);
			recPara.setField("MAIN_WRK_SEARCH_GP", 		szMAIN_WRK_SEARCH_GP);
			recPara.setField("YD_AIM_BAY_GP",  			szYD_AIM_BAY_GP);
			recPara.setField("PAGE_NO",					inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT",					inDto.getField("ROWCOUNT"));
			
			//------------------------------------------------------------------------------------
			//	이송대기인 목표행선에 대해서는 이송지시테이블과 조인해서 대상재 조회하는 쿼리 사용
			//	등록자 : 임춘수
			//	등록일 : 2010.02.05
			//------------------------------------------------------------------------------------
			if( szYD_AIM_RT_GP.startsWith("E") ) {
				
				recPara.setField("SPOS_WLOC_CD", 			szSPOS_WLOC_CD);					//발지개소코드
				recPara.setField("ARR_WLOC_CD",  			szARR_WLOC_CD);						//착지개소코드
				
//				if( szMAIN_WRK_SEARCH_GP.equals("1") ) {
//					intRtnVal      =  ydStkBedDao.getYdStkbed(recPara, outRecSet, 33);
//				}else{
					intRtnVal      =  ydStkBedDao.getYdStkbed(recPara, outRecSet, 34);
				//}
				
			}else{
				intRtnVal      =  ydStkBedDao.getYdStkbed(recPara, outRecSet, 31);
			}
			//------------------------------------------------------------------------------------
			
			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "[Jsp Session : " + szOperationName + "] 조회시 오류발생1 ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "[Jsp Session : " + szOperationName + "] 조회시 오류발생2 parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				return outRecSet;
			} // end of if
	
			
		} catch (Exception e) {
			szMsg = "[Jsp Session : " + szOperationName + "] 예외발생 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	
		szMsg = "[Jsp Session : " + szOperationName + "] ----------------- 메소드 끝 -----------------";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return outRecSet;
	}
	
   
   /**
	 *  작업예약등록(이적)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String insMvWBookId(JDTORecord[] inDto) throws DAOException {
		JDTORecord recPara = JDTORecordFactory.getInstance().create();		
		
		String szRtnMsg = YdConstant.RETN_CD_SUCCESS;
		String szMethodName="insMvWBookId";		
		String szOperationName = "작업예약등록(이적)";
		String szLogMsg = "";
		
	
		try{
			
			szLogMsg = "JSP-SESSION [ 작업예약등록(이적)] 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			for(int x=0;x<inDto.length;x++){
				
				//해당 스케줄 기준정보에 야드 스케줄 금지유무를 "Y" Setting 한다.
				
				recPara.setField("YD_GP", 	          yddatautil.setDataDefault(inDto[x].getField("YD_GP"),""));
				recPara.setField("YD_BAY_GP", 	      yddatautil.setDataDefault(inDto[x].getField("YD_BAY_GP"),""));
				recPara.setField("YD_SPAN_GP", 	      yddatautil.setDataDefault(inDto[x].getField("YD_SPAN_GP"),""));
				recPara.setField("YD_AIM_RT_GP", 	  yddatautil.setDataDefault(inDto[x].getField("YD_AIM_RT_GP"),""));
				recPara.setField("YD_MAIN_WRK_GP", 	  yddatautil.setDataDefault(inDto[x].getField("YD_MAIN_WRK_GP"),""));
				recPara.setField("YD_TO_LOC_GUIDE_GP",yddatautil.setDataDefault(inDto[x].getField("YD_TO_LOC_GUIDE_GP"),""));
				recPara.setField("YD_AIM_YD_GP", 	  yddatautil.setDataDefault(inDto[x].getField("YD_AIM_YD_GP"),""));
				recPara.setField("YD_AIM_BAY_GP", 	  yddatautil.setDataDefault(inDto[x].getField("YD_AIM_BAY_GP"),""));
				recPara.setField("YD_AIM_SPAN_GP", 	  yddatautil.setDataDefault(inDto[x].getField("YD_AIM_SPAN_GP"),""));
				recPara.setField("YD_AIM_COL_GP", 	  yddatautil.setDataDefault(inDto[x].getField("YD_AIM_COL_GP"),""));
				
				recPara.setField("YD_AIM_BED_NO", 	  yddatautil.setDataDefault(inDto[x].getField("YD_AIM_BED_NO"),""));
				
				//2009.06.25 임춘수 추가
				recPara.setField("YD_STK_COL_GP", 	  yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_GPS"),""));
				recPara.setField("YD_STK_BED_NO", 	  yddatautil.setDataDefault(inDto[x].getField("YD_STK_BED_NOS"),""));
				
				recPara.setField("YD_TC_GP", 	  		yddatautil.setDataDefault(inDto[x].getField("YD_TC_GP"),""));
				
				//---------------------------------------------------------------------------------------------
				//	C연주슬라브야드의 준비스케줄 등록 시 지정한 작업매수의 값을 전달하여 해당하는 매수만큼 작업예약에 등록
				//	등록자 : 임춘수
				//	등록일 : 2010.01.20
				//---------------------------------------------------------------------------------------------
				
				recPara.setField("YD_EQP_WRK_SH", 	  yddatautil.setDataDefault(inDto[x].getField("YD_EQP_WRK_SH"),""));
				
				//---------------------------------------------------------------------------------------------
				
				
				//---------------------------------------------------------------------------------------------
				//	이송대기인 경우 착지개소코드도 파라미터로 전달됨
				//	등록자 : 임춘수
				//	등록일 : 2010.02.05
				//---------------------------------------------------------------------------------------------
				recPara.setField("ARR_WLOC_CD", 	          yddatautil.setDataDefault(inDto[x].getField("ARR_WLOC_CD"),""));
				//---------------------------------------------------------------------------------------------
				
				//---------------------------------------------------------------------------------------------
				//	작업재료리스트와 JMS_TC_CD
				//	등록자 : 석창화
				//	등록일 : 2010.03.15
				//---------------------------------------------------------------------------------------------
				recPara.setField("STL_LIST", 	          yddatautil.setDataDefault(inDto[x].getField("STL_LIST"),""));
				recPara.setField("JMS_TC_CD", 	          yddatautil.setDataDefault(inDto[x].getField("JMS_TC_CD"),""));
				//---------------------------------------------------------------------------------------------
				
				ydUtils.displayRecord(szOperationName, recPara);
				
			
				//내부 Process 연결
				EJBConnector ejbConn = null;
				ejbConn = new EJBConnector("default", this);
				//szRtnMsg = (String)ejbConn.trx("MvStkWrkDmdSeEJB", "procCCPrepLotComp", recPara); 		// 아래모듈로 변경 - 임춘수 2009.07.04
				szRtnMsg = (String)ejbConn.trx("MvStkWrkDmdSeEJB", "procCCPrepLotCompByCapa", recPara);

			}
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
		szLogMsg = "JSP-SESSION [ 작업예약등록(이적)] 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		
		return szRtnMsg;
	} // end of insMvWBookId
	
	
	 /**
	 *  BED 에 적치된 단정보 정리 기능( 베드 중간에 공베드가 발생하는 데이터 이상시에 데이터 치환)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto ( YD_STK_COL_GP, YD_STK_BED_NO)
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String bedSortMgt(JDTORecord inDto) throws DAOException {
		JDTORecord recPara = null;
		JDTORecord tempPara = null;
		int oldListSize = 0;
		int newListSize = 0;
		JDTORecordSet returnSet = JDTORecordFactory.getInstance().createRecordSet("CODE");
		JDTORecordSet resNewSet = JDTORecordFactory.getInstance().createRecordSet("CODE");
		List lst = null;
		String mtlStat = "";
	
		String szMethodName="bedSortMgt";		
		String szOperationName = "단정보 정리 기능";
		String szLogMsg = "";
		
		
		YdStkLyrDao  ydStkLyrDao  = new YdStkLyrDao ();
		try{
			
			szLogMsg = "JSP-SESSION [ BED 에 적치된 단정보 정리 기능]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	

			
			//베드정보 가져오기
			ydStkLyrDao.getYdStklyr(inDto, returnSet, 1);
								
			///////////////////////////////////////////////////////////////////
			
			
			
			// 적치중이지 않은  레코드 삭제
			
			returnSet.first();
			oldListSize = returnSet.size();
			
			if(returnSet.size() < 1){
				//수행할 작업이 없을경우 
				return "Success";
			}
			
			
			for(int i = 0 ; i < oldListSize;  i++ ){
				
				recPara = JDTORecordFactory.getInstance().create();
				
				recPara =  returnSet.getRecord(i);
				
				mtlStat = yddatautil.setDataDefault(recPara.getField("YD_STK_LYR_MTL_STAT"),"");
				
				if(mtlStat.equals("") || mtlStat.equals("E") ){
				
					
				}else{
					
					resNewSet.addRecord(recPara);
					
				}
					
			}
			
			// 정리된 레코드 형태로 DB UPDATE 
			returnSet.first();
			newListSize = resNewSet.size();
			for (int i  =0; i < oldListSize ;i++){
				recPara = JDTORecordFactory.getInstance().create();
				tempPara = JDTORecordFactory.getInstance().create();
				
				
				
				//정리된 리스트 사이즈는 차례대로 없데이트 한다.
				if( i < newListSize){
					
					tempPara = resNewSet.getRecord(i);
					
	
					ydUtils.displayRecord(szOperationName, tempPara);
					
					recPara.setField("YD_STK_COL_GP" , inDto.getField("YD_STK_COL_GP"));
					recPara.setField("YD_STK_BED_NO" , inDto.getField("YD_STK_BED_NO"));
					recPara.setField("YD_STK_LYR_NO" , ydUtils.fillSpZr(""+(i+1), 3, 0)) ;
					
					recPara.setField("STL_NO"        			  ,  tempPara.getField("STL_NO"));
					recPara.setField("YD_STK_LYR_ACT_STAT"        ,  tempPara.getField("YD_STK_LYR_ACT_STAT"));
					recPara.setField("YD_STK_LYR_MTL_STAT"        ,  tempPara.getField("YD_STK_LYR_MTL_STAT"));
					
					
					ydUtils.displayRecord(szOperationName, recPara);
					
					ydStkLyrDao.updYdStklyr(recPara, 0);
					
				}
				
				
				// 나머지 부분은 빈적치단 상태로 UPDATE 한다 
				else{
					ydUtils.putLog("화면공통", "베드정기기능", "베드정리기능 공베드 "+i+" : 수행" +newListSize+"크기", YdConstant.DEBUG);
					recPara.setField("YD_STK_COL_GP" , inDto.getField("YD_STK_COL_GP"));
					recPara.setField("YD_STK_BED_NO" , inDto.getField("YD_STK_BED_NO"));
					recPara.setField("YD_STK_LYR_NO" , ydUtils.fillSpZr(""+(i+1), 3, 0)) ;
					recPara.setField("STL_NO"        , "");
					recPara.setField("YD_STK_LYR_ACT_STAT"        , "E");
					recPara.setField("YD_STK_LYR_MTL_STAT"        , "E");		
					
					ydStkLyrDao.updYdStklyr(recPara, 0);
				}
				
			}
			
			
			// 정리단 높이 정리 작업 실시 [저장위치 수정 화면에 반영해 놓은것 호출만 하면된다.
			// 필요할시 연결해준다.
//			EJBConnector ejbConn = null;
//			ejbConn = new EJBConnector("default", this);
//			ejbConn.trx("SlabJspSeEJB", "updStkBedZPosFix", inDto);
			
			
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
		szLogMsg = "JSP-SESSION [ BED 에 적치된 단정보 정리 기능] 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
		
		
		return "Success";
	} // end of bedSortMgt
	
	
	
	
	/**
	 * 오퍼레이션명 : 작업 예약 생성(대차이외작업은 크레인 스케줄 호출함)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return (작업예약 ID)
	 * @throws JDTOException
	 */
	public String ydManualReq(JDTORecord msgRecord)throws JDTOException  {
		
		//스케줄기준 DAO
		YdWrkbookDao 	ydWrkbookDao    = new YdWrkbookDao();
		//작업예약 재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		//저장품 DAO
		YdStockDao 		ydStockDao 		= new YdStockDao();
		
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils           = new YdDaoUtils();
		//공용 METHOD
		YdUtils ydutils                 = new YdUtils();
		
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
		JDTORecord recPara     = null;
		JDTORecord recStkPara  = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		
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
		
		YdDelegate ydDelegate = new YdDelegate();
		
		try {
			
			szMsg = "JSP-SESSION [작업 예약 생성]시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
			
		
			//받은 전문 편집
			//스케줄코드
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			if (szYD_SCH_CD.equals("")) {
				
				szMsg = "[데이터 이상] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
				
			}
			//적치열구분
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
			if (szYD_STK_COL_GP.equals("")) {
				
				szMsg = "[데이터 이상] 적치열구분이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
				
			}
			//적치BED번호
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
			if (szYD_STK_BED_NO.equals("")) {
				
				szMsg = "[데이터 이상] 적치BED번호가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
				
			}
			//재료매수
			intMtlCnt = ydDaoUtils.paraRecChkNullInt(msgRecord, "SLAB_SH");
			if (intMtlCnt == 0) {
				
				szMsg = "[데이터 이상] 재료매수가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
				
			}
			
			//재료번호
			szSTL_NO          = new String[intMtlCnt + 1];
			//권상모음순서
			szYD_UP_COLL_SEQ  = new String[intMtlCnt + 1];

			//재료번호, 권상모음순서
			for(int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//재료번호
				szSTL_NO[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"STL_NO" + Loop_i);
				if (szSTL_NO[Loop_i].equals("")) {
					
					szMsg = "[전문 이상] " + Loop_i + "번째 재료 번호가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;
					
				}
				//권상모음순서
				szYD_UP_COLL_SEQ[Loop_i] = ydDaoUtils.paraRecChkNull(msgRecord,"YD_UP_COLL_SEQ" + Loop_i);
				if (szYD_UP_COLL_SEQ[Loop_i].equals("")) {
					
					szMsg = "[전문 이상] 재료번호(" + szYD_UP_COLL_SEQ[Loop_i] + ")에 대한 권상모음순서가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;
					
				}
			}

			//리턴 recordSet 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//스케줄 기준 체크
			blnRtnVal = this.chkGetSchRule(szYD_SCH_CD, rsResult);
			if (!blnRtnVal) return YdConstant.RETN_CD_FAILURE;
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//스케줄CD 체크
			//스케줄 금지 유무
			szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");
			//작업크레인
			szYD_WRK_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
			//대체크레인유무
			szYD_ALT_CRN_YN   = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");
			//대체크레인
			szYD_ALT_CRN      = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
			
			
			//20090325 김진욱 추가////////////////////////////////////////////////////////
			//스케줄우선순위
			szYD_SCH_PRIOR    = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN_PRIOR");
			////////////////////////////////////////////////////////////////////////////
			
			
			//스케줄 금지 유무가  "Y"이면 처리를 중지하고 유스케이스를 종료한다.
			if (szYD_SCH_PROH_EXN.equals("Y")) {
				
				szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
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
					return YdConstant.RETN_CD_FAILURE;
				}
				//대체크레인이 있으면 대체크레인 설비 상태 체크
				blnRtnVal = this.eqpStatCheck(szYD_ALT_CRN);
				//대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
				if (!blnRtnVal) {
					
					szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;
					
				} else {
					//대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
					szCrn = szYD_ALT_CRN;
					
				}
			} else {
				//작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
				szCrn = szYD_WRK_CRN;
				
			}

			//재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다.
			//작업예약재료 등록 여부를 체크한다.
			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//다른 작업예약에 재료가 등록되어있는지 체크한다.
				blnRtnVal = this.chkYdWrkBookMtl(szSTL_NO[Loop_i]);
				if (!blnRtnVal)return YdConstant.RETN_CD_FAILURE;
				
			}	
			
			
			// 리턴 RecordSet 생성
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			
			
			
			//20090325 김진욱 추가//////////////////////////////////////////////////////
			// 저장품테이블 조회
			blnRtnVal = this.chkGetStock(szSTL_NO[1], rsResult);
			if(!blnRtnVal) return YdConstant.RETN_CD_FAILURE;
			
			// 레코드추출
			rsResult.first();
			
			recPara = rsResult.getRecord();
			
			// 야드목표야드구분
			szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_YD_GP");
			
			// 야드목표동구분
			szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_BAY_GP");
			//////////////////////////////////////////////////////////////////////////
			
			
			
			//리턴 recordSet 생성
			rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
			
			//작업예약ID 생성
			szYD_WBOOK_ID = ydWrkbookDao.getYdWrkbookId();
		
			
			//INSERT 항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			//야드구분
			String szYD_GP       = szYD_SCH_CD.substring(0, 1);
			//동구분
			String szYD_BAY_GP   = szYD_SCH_CD.substring(1,2);
			

			
			//INSERT할 항목 SET (더 추가할항목이 있다고함 // 김진욱에게 재확인할것 )
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
			recPara.setField("YD_GP", 		  szYD_GP);
			recPara.setField("YD_BAY_GP", 	  szYD_BAY_GP);
			recPara.setField("YD_SCH_CD", 	  szYD_SCH_CD);
			recPara.setField("REGISTER", 	  szUser);
			//20090325 김진욱 추가
			recPara.setField("YD_AIM_YD_GP",  szYD_AIM_YD_GP);
			recPara.setField("YD_AIM_BAY_GP", szYD_AIM_BAY_GP);
			recPara.setField("YD_SCH_PRIOR",  szYD_SCH_PRIOR);
			
			//20090330 이현성 추가 
			//To위치 정보  To Guide 위치 및 To 위치 결정방법 'F': 지정위치 추가			
			
			recPara.setField("YD_TO_LOC_GUIDE",  ydDaoUtils.paraRecChkNull(msgRecord,"YD_TO_LOC_GUIDE"));
			if(!"".equals(ydDaoUtils.paraRecChkNull(msgRecord,"YD_TO_LOC_GUIDE")))
				recPara.setField("YD_TO_LOC_DCSN_MTD",  "F");
			
			//계획대차 
			recPara.setField("YD_WRK_PLAN_TCAR",  ydDaoUtils.paraRecChkNull(msgRecord,"YD_WRK_PLAN_TCAR"));
			
			
			
			//작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
			if (intRtnVal < 1) {
				szMsg = "작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}
			
			
			//조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
			recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			recPara.setField("REGISTER", 	  szUser);

			for (int Loop_i = 1; Loop_i <= intMtlCnt; Loop_i++) {
				
				//리턴 recordSet 생성
				rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
				//재료번호에 해당하는 적치중('C')인 적치단 데이터를 가져온다.
				blnRtnVal = this.chkGetStlStkLyr(szSTL_NO[Loop_i], YdConstant.YD_STK_LYR_MTL_STAT_STK , rsResult);
				if (!blnRtnVal) return YdConstant.RETN_CD_FAILURE;
				
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
				
				if (intRtnVal < 1) {
					szMsg = "작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;
				}
			}				
				
				// 대차 작업일 경우는 스케줄 기동을 시키지않는다.
				if("TC".equals(szYD_SCH_CD.substring(2,4))){
					return szYD_WBOOK_ID;
				}
				
				
				
				//작업 예약 편성 후 스케줄 기동 
				
				recPara = JDTORecordFactory.getInstance().create();
				
				
				if (YdConstant.YD_GP_C_SLAB_YARD.equals(szYD_GP)){
					//C연주 슬라브 야드 
					recPara.setField("JMS_TC_CD","YDYDJ500");
					
				} else if(YdConstant.YD_GP_PORT_SLAB_YARD.equals(szYD_GP)){  //항만슬라브야드 기능추가 - 2015.12.30 LeeJY
					//항만 슬라브야드
					recPara.setField("JMS_TC_CD","YDYDJ500");
					
				} else if(YdConstant.YD_GP_A_PLATE_SLAB_YARD.equals(szYD_GP)){
					//A후판 슬라브야드
					recPara.setField("JMS_TC_CD","YDYDJ503");
					
				} else if(YdConstant.YD_GP_PLATE_GDS_YARD.equals(szYD_GP)){
					//후판 제품야드
					recPara.setField("JMS_TC_CD","YDYDJ506");
				} else if(YdConstant.YD_GP_C_HR_COIL_GDS_YARD.equals(szYdGp)){
					//코일 제품야드
					recPara.setField("JMS_TC_CD","YDYDJ509");
				}  else if(YdConstant.YD_GP_C_HR_COIL_MATL_YARD.equals(szYD_GP)){
					
					recPara.setField("JMS_TC_CD","YDYDJ509");
				} else if(YdConstant.YD_GP_INTGR_YARD.equals(szYD_GP)){
					//통합야드
					recPara.setField("JMS_TC_CD","YDYDJ512");
				}
				
				recPara.setField("YD_SCH_CD", szYD_SCH_CD);
				//작업크레인 정보를 설비에 넣어준다. 
				recPara.setField("YD_EQP_ID",szCrn );
					
				
				//jms Send Method
				ydDelegate.sendMsg(recPara);
			
			} catch(Exception e) {
				szMsg = "메뉴얼 작업지시 요구 처리중 Error : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}
		
			
			szMsg = "JSP-SESSION [작업 예약 생성]끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
			
		return szYD_WBOOK_ID;

	
	} // end of ydManualReq()
	
	
	

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
			
			szMsg = "JSP-SESSION [스케줄기준 체크 및 데이터 반환]시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
			
			//조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			//스케줄코드
			recPara.setField("YD_SCH_CD", szSchCd);

			//스케줄코드로 스케줄기준 Table 조회
			intRtnVal = ydSchRuleDao.getYdSchrule(recPara, rsResult, 0);

			//리턴값 메세지처리
			if (intRtnVal > 1) {
				
				szMsg = "스케줄코드(" + szSchCd + ")에 대한 스케줄기준 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == 1) {

				blnRtnVal = true;
				
			} else if (intRtnVal == 0) {
				
				szMsg = "스케줄코드(" + szSchCd + ")에 대한 스케줄기준 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == -2) {
				
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
		
		szMsg = "JSP-SESSION [스케줄기준 체크 및 데이터 반환]끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return blnRtnVal = true;
		
	} //end of chkGetSchRule
	
	
	
	
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
			
			szMsg = "JSP-SESSION [설비상태 체크]시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			//레코드 생성
			recPara = JDTORecordFactory.getInstance().create();
			//레코드셋 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//설비ID를 작업크레인으로 설정
			recPara.setField("YD_EQP_ID", szEqpId);
			
			//설비 체크 및 데이터 조회
			blnRtnVal = this.chkGetEqp(szEqpId, rsResult);
			if (!blnRtnVal) return blnRtnVal;
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//설비상태
			szYD_EQP_STAT = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_STAT");
			
			//크레인의 상태가 'T'이면 false 리턴.
			//상수 값으로 변경 [2009.12.03 이현성]
			if (szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_BREAK)) {
				
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
		
		szMsg = "JSP-SESSION [설비상태 체크]끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return blnRtnVal;
		
	} //end of eqpStatCheck
	
	
	
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
			
			
			szMsg = "JSP-SESSION [설비 유무체크 및 조회결과 데이터 반환 ]시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
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
		
		szMsg = "JSP-SESSION [설비 유무체크 및 조회결과 데이터 반환 ]끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return blnRtnVal;
	} //end of chkGetEqp
	
	
	
	/**
	 * 오퍼레이션명 : 재료번호, 적치단재료상태에 대한 적치단 정보 유무체크 및 조회결과 데이터 반환
	 *  
	 * @param  String        szStlNo   재료번호
	 *         String        szMtlStat 적치단재료상태
	 *         JDTORecordSet rsResult  결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkGetStlStkLyr(String szStlNo, String szMtlStat, JDTORecordSet rsResult)throws JDTOException  {
		
		//적치단 DAO
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();

		String szMsg        = null;
		String szMethodName = "chkGetStlStkLyr";
		int intRtnVal       = 0;
		boolean blnRtnVal   = false;
		JDTORecord recPara  = null;
		
		try {
			
			szMsg = "JSP-SESSION [ 재료번호, 적치단재료상태에 대한 적치단 정보 유무체크 및 조회결과 데이터 반환 ]시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			//조회 항목  record 생성
			recPara = JDTORecordFactory.getInstance().create();

			//조회 파라미터 레코드 set
			recPara.setField("STL_NO",              szStlNo);
			recPara.setField("YD_STK_LYR_MTL_STAT", szMtlStat);
			
			//적치단정보 조회
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsResult, 3);

			//리턴값 메세지처리
			if (intRtnVal > 1) {
				
				szMsg = "재료번호("      + szStlNo   + ")," +
				        "적치단재료상태(" + szMtlStat + ")," +
				        " 에 대한 적치단 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == 1) {
				
				blnRtnVal = true;
				
			} else if (intRtnVal == 0) {
				szMsg = "재료번호("      + szStlNo   + ")," +
		                "적치단재료상태(" + szMtlStat + ")," +
						" 에 대한 적치단 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == -2) {
				szMsg = "재료번호("      + szStlNo   + ")," +
                        "적치단재료상태(" + szMtlStat + ")," +
				        "로 적치단 조회중 parameter error 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				szMsg = "재료번호("      + szStlNo   + ")," +
                        "적치단재료상태(" + szMtlStat + ")," +
				        " 로 적치단 조회중 오류 발생!";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e) {
			szMsg = "재료번호, 적치단재료상태에 대한 적치단 정보 유무체크 및 조회결과 데이터 반환 중 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		
		szMsg = "JSP-SESSION [ 재료번호, 적치단재료상태에 대한 적치단 정보 유무체크 및 조회결과 데이터 반환 ]끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return blnRtnVal;
	} //end of chkGetStlStkLyr
	
	
	
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
			
			szMsg = "JSP-SESSION [ 저장품유무체크 및 조회결과 데이터 반환 ]시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();
			
			//재료번호
			recPara.setField("STL_NO", szStlNo);
			
			//저장품 테이블 조회
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 0);
			
			//리턴값 메세지처리
			if (intRtnVal > 1) {
				
				szMsg = "재료번호(" + szStlNo + ")에 대한 저장품 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == 1) {

				blnRtnVal = true;
				
			} else if (intRtnVal == 0) {
				
				szMsg = "재료번호(" + szStlNo + ")에 대한 저장품 데이터가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == -2) {
				
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
		
		szMsg = "JSP-SESSION [ 저장품유무체크 및 조회결과 데이터 반환 ]끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
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
			
			szMsg = "JSP-SESSION [작업예약재료 등록여부 체크 ]시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();
			//레코드셋 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//재료번호
			recPara.setField("STL_NO", szStlNo);
			
			//재료번호로 작업예약재료 테이블을 읽어온다.
			intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recPara, rsResult, 2);
			
			//리턴값 메세지처리
			if (intRtnVal > 0) {
				
				szMsg = "재료번호(" + szStlNo + ")에 대한 작업예약재료 데이터가 이미 등록되어 있습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == 0) {
				
				szMsg = "재료번호(" + szStlNo + ")에 대한 작업예약재료 데이터가 등록 가능합니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = true;
				
			} else if (intRtnVal == -2) {
				
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
		
		szMsg = "JSP-SESSION [작업예약재료 등록여부 체크 ]끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return blnRtnVal;
		
	} //end of chkYdWrkBookMtl
	
	
	
	
	
	/**
	 *  예약 베드 조회 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getBedPlanPos(JDTORecord inDto) throws DAOException {
		
		JDTORecord       recPara    = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet  = JDTORecordFactory.getInstance().createRecordSet("retTmp");		
		
		JDTORecordSet    rtnRecSet  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		
		
		String szMsg                = "";		
		String szMethodName         = "getBedPlanPos";
		
		int intRtnVal = 0;
		
		String szYD_STK_LOT_TP     = "";
		String szCrnWrkMtlStkLotCd = "";
	
		YdStrCharDao ydStrCharDao = new YdStrCharDao();
		try {
			
			szMsg = "JSP-SESSION [예약 베드 조회 ]시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			
			recPara.setField("YD_SCH_CD",   yddatautil.setDataDefault(inDto.getField("SCH_CODE"), ""));
			recPara.setField("YD_ROUTE_GP", yddatautil.setDataDefault(inDto.getField("YD_AIM_RT_GP"), ""));	
			
			intRtnVal = ydStrCharDao.getYdStrchar(recPara, outRecSet, 2);

			//대상 건수 LOG 
			
			szMsg = "대상건수:" + intRtnVal;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				return outRecSet;
			}  // end of if
			
			
			
			szYD_STK_LOT_TP     = yddatautil.setDataDefault(inDto.getField("YD_STK_LOT_TP"), "");
			szCrnWrkMtlStkLotCd = yddatautil.setDataDefault(inDto.getField("YD_STK_LOT_CD"), "");

			
			//전달인자
			
			szMsg = "LOT TP:" + szYD_STK_LOT_TP+"\n";
			szMsg = "LOT CD:" + szCrnWrkMtlStkLotCd+"\n";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			outRecSet.first();
			
			intRtnVal= this.stkBedGradeSelect(szYD_STK_LOT_TP,szCrnWrkMtlStkLotCd,outRecSet,rtnRecSet);
			
			
		
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		szMsg = "JSP-SESSION [예약 베드 조회 ]끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return rtnRecSet;
	}//end of getBedPlanPos
	
	
	
	
	
	
	
	
	
	
	/**
	 * 오퍼레이션명 : 적치베드 저장등급 조회
	 *  
	 * @param   String szYD_STK_LOT_TP, szCrnWrkMtlStkLotCd, rsStkBed, rsResultBed
	 * @return int 1(등급부여 성공), -1(등급부여실패)
	 * @throws JDTOException
	 */
	public int stkBedGradeSelect(String szYD_STK_LOT_TP, String szCrnWrkMtlStkLotCd, JDTORecordSet rsStkBed, JDTORecordSet rsResultBed)throws JDTOException  {
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		
		//메세지
		String szMsg              = null;
		//메소드명
		String szMethodName       = "StkBedGradeSelect";
		String szOperationName = "적치베드 저장등급 조회";
		//리턴값(int)
		int intRtnVal = 0;
		//등급
		int intGrade  = 101;
		
		//레코드 선언
		JDTORecord recGetCrnWrkMtl = null;
		JDTORecord recGetRsSet     = null;
		JDTORecord recInRsSet      = null;
		
		JDTORecordSet rsGetStkLyr  = null;
		
		
		
		/*
		 * 
		 * 
		 * */
		
		YdStkBedDao ydStkBedDao=  new YdStkBedDao();
		
		
		
			
		try {	
			
			szMsg = "JSP-SESSION [적치베드 저장등급 조회]시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			
			//재료의 산적LOT정보를 셋팅한다.
			recGetCrnWrkMtl = JDTORecordFactory.getInstance().create();
			recGetCrnWrkMtl.setField("YD_STK_LOT_TP", szYD_STK_LOT_TP);
			recGetCrnWrkMtl.setField("YD_STK_LOT_CD", szCrnWrkMtlStkLotCd);
			
			//적치 베드수만큼 루프
			for(int Loop_i = 1; Loop_i <= rsStkBed.size(); Loop_i++) {
				rsStkBed.absolute(Loop_i);
				recInRsSet = JDTORecordFactory.getInstance().create();
				recInRsSet.setRecord(rsStkBed.getRecord());
				
				
				rsGetStkLyr = JDTORecordFactory.getInstance().createRecordSet("Temp");
				
				//이현성 수정 - 적치단 최상단 정보가 없더라도 베드정보까지 가져고에 만들기
				//intRtnVal = ydStkLyrDao.getYdStklyr(recInRsSet, rsGetStkLyr, 6);
				intRtnVal = ydStkBedDao.getYdStkbed(recInRsSet, rsGetStkLyr, 19);
				
				
				
				recGetRsSet = JDTORecordFactory.getInstance().create();
				if(intRtnVal <= 0) {
					if(intRtnVal == 0) {
						//베드가 존재 하지 않을경우
						
						
						szMsg="베드가 존재 하지 않음!!!!!!";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);	
						
						recInRsSet.setField("YD_STK_LOT_TP", ydDaoUtils.paraRecChkNull(recGetRsSet, "YD_STK_LOT_TP"));
						recInRsSet.setField("YD_STK_LOT_CD", ydDaoUtils.paraRecChkNull(recGetRsSet, "YD_STK_LOT_CD"));
						recInRsSet.setField("YD_STK_BED_GRADE", ""+102);						
						rsResultBed.addRecord(recInRsSet);
						continue;
						
						
						
					}else if(intRtnVal == -2) {
						szMsg="[StkBedGradeSelect] getYdStklyr parameter error";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					
					recInRsSet.setField("YD_STK_LOT_TP", ydDaoUtils.paraRecChkNull(recGetRsSet, "YD_STK_LOT_TP"));
					recInRsSet.setField("YD_STK_LOT_CD", ydDaoUtils.paraRecChkNull(recGetRsSet, "YD_STK_LOT_CD"));
					recInRsSet.setField("YD_STK_BED_GRADE", ""+109);						
					rsResultBed.addRecord(recInRsSet);
					continue;
					
				}
				rsGetStkLyr.absolute(1);
				recGetRsSet.setRecord(rsGetStkLyr.getRecord());
				
				
				//공베드는 베드는 있으나 재료정보가 없는 베드임
				
				if ("".equals(ydDaoUtils.paraRecChkNull(recGetRsSet, "STL_NO"))){
					//공베드인 경우
					recGetRsSet.setField("YD_STK_BED_NULL_GP", "Y");
					recGetRsSet.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(recInRsSet, "YD_STK_COL_GP"));
					recGetRsSet.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(recInRsSet, "YD_STK_BED_NO"));
				}
				
				
				
				szMsg="[LOT TP : ]" + szYD_STK_LOT_TP+"  :   " + Loop_i + "  :   szCrnWrkMtlStkLotCd " + szCrnWrkMtlStkLotCd;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
   				szMsg="===============크레인 하단재료정보============";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
       			ydUtils.displayRecord(szOperationName, recGetCrnWrkMtl);
       			
   				szMsg="===============적치베드 상단재료정보===============";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
       			ydUtils.displayRecord(szOperationName, recGetRsSet);
       			
				
				if( szYD_STK_LOT_TP.equals("SO") ) {

	   				//지시대기
	   				intGrade = this.Y1GradeTest1(recGetCrnWrkMtl, recGetRsSet);
	   				
	   			}else if ( szYD_STK_LOT_TP.equals("SY")  ) {
	   				
	   				//여재
	   				intGrade = this.Y1GradeTest2(recGetCrnWrkMtl, recGetRsSet);

	   			}else if ( szYD_STK_LOT_TP.equals("SL") ) {
	   				
	   				//장입LOT
	   				intGrade = this.Y1GradeTest3(recGetCrnWrkMtl, recGetRsSet);
	   			
	   			}else if ( szYD_STK_LOT_TP.equals("SH") ) {
	   				
	   				//장입순번
	   				intGrade = this.Y1GradeTest4(recGetCrnWrkMtl, recGetRsSet);
	   			
	   			}else if ( szYD_STK_LOT_TP.equals("SS") ) {
	   				
	   				//스카핑재
	   				intGrade = this.Y1GradeTest5(recGetCrnWrkMtl, recGetRsSet);
	   			
	   			}else if ( szYD_STK_LOT_TP.equals("ST") ) {
	   				
	   				//후판정정재
	   				intGrade = this.Y1GradeTest6(recGetCrnWrkMtl, recGetRsSet);
	   			
	   			}else{
	   				szMsg = "정의되지 않은 산적LotType입니다. 산적LotType : " + szYD_STK_LOT_TP;
	   				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	   				intGrade = 101;
	   			}
	   				
				
				recInRsSet.setField("YD_STK_LOT_TP", ydDaoUtils.paraRecChkNull(recGetRsSet, "YD_STK_LOT_TP"));
				recInRsSet.setField("YD_STK_LOT_CD", ydDaoUtils.paraRecChkNull(recGetRsSet, "YD_STK_LOT_CD"));
				recInRsSet.setField("YD_STK_BED_GRADE", ""+intGrade);
				
				rsResultBed.addRecord(recInRsSet);
			}
			
			
			
		} catch(Exception e) {
			szMsg = "작업예약재료 등록여부 체크 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return intRtnVal = -1;
		}
		
		szMsg = "JSP-SESSION [적치베드 저장등급 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return intRtnVal = 1;
		
	} //end of StkBedGradeSelect
	
	
    /**
     * 오퍼레이션명 : 등급부여 (지시대)
     *  
     * @param  rsResultCrnwrkmtl, rsBed
     * @return int 성공:1, 실패:-1
     * @throws 
     */
    public int Y1GradeTest1 (JDTORecord recGetCrnWrkMtl, JDTORecord recGetStkLyr){

    	int intRtnVal = 100;
    	
        //크레인작업재료의 최하단 산적LotType,산적Lot코드,폭
        String szCrnWrkMtlStkLotTp  = "";
        String szCrnWrkMtlStkLotCd  = "";
        
        //적치단의 최상단 산적LotType,산적Lot코드,폭
        String szStkLyrStkLotTp     = "";
        String szStkLyrStkLotCd     = "";
        String szYD_STK_BED_NULL_GP = "";
        String szYD_STK_COL_GP      = "";
        
        String szMsg = "";
        String szMethodName = "Y1GradeTest1";
    	
        try{
        	
        	szMsg = "JSP-SESSION [등급부여 (지시대)]시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
        	szCrnWrkMtlStkLotTp  = ydDaoUtils.paraRecChkNull(recGetCrnWrkMtl,"YD_STK_LOT_TP");
        	szCrnWrkMtlStkLotCd  = ydDaoUtils.paraRecChkNull(recGetCrnWrkMtl,"YD_STK_LOT_CD");
        	szStkLyrStkLotTp     = ydDaoUtils.paraRecChkNull(recGetStkLyr,   "YD_STK_LOT_TP");
        	szStkLyrStkLotCd     = ydDaoUtils.paraRecChkNull(recGetStkLyr,   "YD_STK_LOT_CD");
        	szYD_STK_BED_NULL_GP = ydDaoUtils.paraRecChkNull(recGetStkLyr,   "YD_STK_BED_NULL_GP");
        	szYD_STK_COL_GP      = ydDaoUtils.paraRecChkNull(recGetStkLyr,   "YD_STK_COL_GP");

        	//재료가 있으나 산적LotType와 코드가 없는경우
        	if((szStkLyrStkLotTp.equals("") || szStkLyrStkLotCd.equals(""))&& (!szYD_STK_BED_NULL_GP.equals("Y"))) {
        		intRtnVal = 10;
				szMsg="저장위치 등급 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        		return intRtnVal;
        	}
        	
        	//To위치가 공베드 = 4등급
        	if(szYD_STK_BED_NULL_GP.equals("Y")) {
        		if(szYD_STK_COL_GP.substring(2,4).equals("TC") || szYD_STK_COL_GP.substring(2,4).equals("PT") 
        			|| szYD_STK_COL_GP.substring(2,4).equals("TR") || szYD_STK_COL_GP.substring(2,4).equals("PU")
        			|| szYD_STK_COL_GP.substring(2,4).equals("DP") ){
        				intRtnVal = 1;
        			}else{
        				intRtnVal = 2;
        			}
        		
				szMsg="저장위치 등급 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        		return intRtnVal;
        	}
        	

        	if(szStkLyrStkLotCd.equals("") && szCrnWrkMtlStkLotCd.equals("")){
        		intRtnVal = 99;
				ydUtils.putLog(szSessionName, szMethodName, "양쪽 값이 존재 하지 않음  : 저장위치 등급 : " + intRtnVal, YdConstant.ERROR);
    			return intRtnVal;
        	}
        	
        	
        	szCrnWrkMtlStkLotCd = ydUtils.fillSpZr(szCrnWrkMtlStkLotCd, 14, 1);
        	szStkLyrStkLotCd    = ydUtils.fillSpZr(szStkLyrStkLotCd, 14, 1);
			
        	
        	
    		//산적LotType와 Code가 같은경우 1등급
    		if(szStkLyrStkLotCd.equals(szCrnWrkMtlStkLotCd) ) {
    			intRtnVal = 1;
				szMsg="저장위치 등급 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			return intRtnVal;
    		
    		//코드가 틀린 경우 2등급
    		}else if( (szStkLyrStkLotCd.substring(0,10)).equals( szCrnWrkMtlStkLotCd.substring(0,10) ) ) {
    			intRtnVal = 3;
				szMsg="저장위치 등급 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			return intRtnVal;
    		}else if(szStkLyrStkLotCd.substring(0,7).equals(szCrnWrkMtlStkLotCd.substring(0,7)) ) {
    			intRtnVal = 4;
				szMsg="저장위치 등급 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			return intRtnVal;
    		}else if(szStkLyrStkLotCd.substring(0,5).equals(szCrnWrkMtlStkLotCd.substring(0,5)) ) {
    			intRtnVal = 5;
				szMsg="저장위치 등급 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			return intRtnVal;
    		}else if(szStkLyrStkLotCd.substring(0,3).equals(szCrnWrkMtlStkLotCd.substring(0,3)) ) {
    			intRtnVal = 6;
				szMsg="저장위치 등급 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			return intRtnVal;
    		}else if(szStkLyrStkLotCd.substring(0,2).equals(szCrnWrkMtlStkLotCd.substring(0,2)) ) {
    			intRtnVal = 7;
				szMsg="저장위치 등급 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			return intRtnVal;
    			
    		}
    		
    		
    		szMsg = "JSP-SESSION [등급부여 (지시대)]끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
        	return intRtnVal;
        }catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "<Y1GradeTest> Exception Error :"+ e.getLocalizedMessage(), YdConstant.ERROR);
			return intRtnVal = 100;
        }//end of try~catch
    	
    }//end of Y1GradeTest1()
    
    /**
     * 오퍼레이션명 : 등급부여 (여재)
     *  
     * @param  rsResultCrnwrkmtl, rsBed
     * @return int 성공:1, 실패:-1
     * @throws 
     */
    public int Y1GradeTest2 (JDTORecord recGetCrnWrkMtl, JDTORecord recGetStkLyr){
    	//상위 Method : chkLocsrchbedCrnMtl
		//┏━┓
		//┃ 저장위치의 등급을 구한다.
		//┗━┛
    	
    	int intRtnVal = 100;
    	
        //크레인작업재료의 최하단 산적LotType,산적Lot코드,폭
        String szCrnWrkMtlStkLotTp = "";
        String szCrnWrkMtlStkLotCd = "";
        
        //적치단의 최상단 산적LotType,산적Lot코드,폭
        String szStkLyrStkLotTp     = "";
        String szStkLyrStkLotCd     = "";
        String szYD_STK_BED_NULL_GP = "";
        String szYD_STK_COL_GP      = "";
        
        String szMsg = "";
        String szMethodName = "Y1GradeTest2";
    	
        try{
        	
        	szMsg = "JSP-SESSION [ 등급부여 (여재)]시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
        	szCrnWrkMtlStkLotTp  = ydDaoUtils.paraRecChkNull(recGetCrnWrkMtl,"YD_STK_LOT_TP");
        	szCrnWrkMtlStkLotCd  = ydDaoUtils.paraRecChkNull(recGetCrnWrkMtl,"YD_STK_LOT_CD");
        	szStkLyrStkLotTp     = ydDaoUtils.paraRecChkNull(recGetStkLyr,   "YD_STK_LOT_TP");
        	szStkLyrStkLotCd     = ydDaoUtils.paraRecChkNull(recGetStkLyr,   "YD_STK_LOT_CD");
        	szYD_STK_BED_NULL_GP = ydDaoUtils.paraRecChkNull(recGetStkLyr,   "YD_STK_BED_NULL_GP");
        	szYD_STK_COL_GP      = ydDaoUtils.paraRecChkNull(recGetStkLyr,   "YD_STK_COL_GP");

        	//재료가 있으나 산적LotType와 코드가 없는경우
        	if((szStkLyrStkLotTp.equals("") || szStkLyrStkLotCd.equals(""))&& (!szYD_STK_BED_NULL_GP.equals("Y"))) {
        		intRtnVal = 10;
				szMsg="저장위치 등급 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        		return intRtnVal;
        	}
        	
        	
        	//To위치가 공베드 = 4등급
        	if(szYD_STK_BED_NULL_GP.equals("Y")) {
        		if(szYD_STK_COL_GP.substring(2,4).equals("TC") || szYD_STK_COL_GP.substring(2,4).equals("PT") 
        			|| szYD_STK_COL_GP.substring(2,4).equals("TR") || szYD_STK_COL_GP.substring(2,4).equals("PU")
        			|| szYD_STK_COL_GP.substring(2,4).equals("DP") ){
        				intRtnVal = 1;
        			}else{
        				intRtnVal = 2;
        			}
        		
				szMsg="저장위치 등급 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        		return intRtnVal;
        	}
        	

        	if(szStkLyrStkLotCd.equals("") && szCrnWrkMtlStkLotCd.equals("")){
        		intRtnVal = 99;
    			ydUtils.putLog(szSessionName, szMethodName, "양쪽 값이 존재 하지 않음  : 저장위치 등급 : " + intRtnVal, YdConstant.ERROR);
    			return intRtnVal;
        	}
        	
        	
        	szCrnWrkMtlStkLotCd = ydUtils.fillSpZr(szCrnWrkMtlStkLotCd, 14, 1);
        	szStkLyrStkLotCd    = ydUtils.fillSpZr(szStkLyrStkLotCd, 14, 1);
			
        	
        	szMsg = "JSP-SESSION [ 등급부여 (여재)] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
        
        	
    		//산적LotType와 Code가 같은경우 1등급
    		if(szStkLyrStkLotCd.equals(szCrnWrkMtlStkLotCd) ) {
    			intRtnVal = 1;
        		ydUtils.putLog(szSessionName, szMethodName, "저장위치 등급 : " + intRtnVal, YdConstant.ERROR);
    			ydUtils.putLog(szSessionName, szMethodName, "저장위치 등급 : " + intRtnVal, YdConstant.ERROR);
    			return intRtnVal;
    		
    		//코드가 틀린 경우 2등급
    		}else if(szStkLyrStkLotCd.substring(0,6).equals(szCrnWrkMtlStkLotCd.substring(0,6)) ) {
    			intRtnVal = 3;
        		ydUtils.putLog(szSessionName, szMethodName, "저장위치 등급 : " + intRtnVal, YdConstant.ERROR);
    			return intRtnVal;
    		}else if(szStkLyrStkLotCd.substring(0,5).equals(szCrnWrkMtlStkLotCd.substring(0,5)) ) {
    			intRtnVal = 4;
        		ydUtils.putLog(szSessionName, szMethodName, "저장위치 등급 : " + intRtnVal, YdConstant.ERROR);
    			return intRtnVal;
    		}else if(szStkLyrStkLotCd.substring(0,4).equals(szCrnWrkMtlStkLotCd.substring(0,4)) ) {
    			intRtnVal = 5;
        		ydUtils.putLog(szSessionName, szMethodName, "저장위치 등급 : " + intRtnVal, YdConstant.ERROR);
    			return intRtnVal;
    		}else if(szStkLyrStkLotCd.substring(0,2).equals(szCrnWrkMtlStkLotCd.substring(0,2)) ) {
    			intRtnVal = 6;
        		ydUtils.putLog(szSessionName, szMethodName, "저장위치 등급 : " + intRtnVal, YdConstant.ERROR);
    			return intRtnVal;
    			
    		}else{
    			intRtnVal = 10;
        		ydUtils.putLog(szSessionName, szMethodName, "저장위치 등급 : " + intRtnVal, YdConstant.ERROR);
    			return intRtnVal;
    		}
    		

    	
    		 
        }catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "<Y1GradeTest> Exception Error :"+ e.getLocalizedMessage(), YdConstant.ERROR);
			return intRtnVal = 100;
        }//end of try~catch
        
    }//end of Y1GradeTest2()
    
    /**
     * 오퍼레이션명 : 등급부여 (장입LOT)
     *  
     * @param  rsResultCrnwrkmtl, rsBed
     * @return int 성공:1, 실패:-1
     * @throws 
     */
    public int Y1GradeTest3 (JDTORecord recGetCrnWrkMtl, JDTORecord recGetStkLyr){
    	//상위 Method : chkLocsrchbedCrnMtl
		//┏━┓
		//┃ 저장위치의 등급을 구한다.
		//┗━┛
    	
    	int intRtnVal = 100;
    	
        //크레인작업재료의 최하단 산적LotType,산적Lot코드,폭
        String szCrnWrkMtlStkLotTp = "";
        String szCrnWrkMtlStkLotCd = "";
        
        //적치단의 최상단 산적LotType,산적Lot코드,폭
        String szStkLyrStkLotTp     = "";
        String szStkLyrStkLotCd     = "";
        String szYD_STK_BED_NULL_GP = "";
        String szYD_STK_COL_GP      = "";
        
        String szMsg = "";
        String szMethodName = "Y1GradeTest3";
    	
        try{
        	
        	szMsg = "JSP-SESSION [  등급부여 (장입LOT)]시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
        	szCrnWrkMtlStkLotTp  = ydDaoUtils.paraRecChkNull(recGetCrnWrkMtl,"YD_STK_LOT_TP");
        	szCrnWrkMtlStkLotCd  = ydDaoUtils.paraRecChkNull(recGetCrnWrkMtl,"YD_STK_LOT_CD");
        	szStkLyrStkLotTp     = ydDaoUtils.paraRecChkNull(recGetStkLyr,   "YD_STK_LOT_TP");
        	szStkLyrStkLotCd     = ydDaoUtils.paraRecChkNull(recGetStkLyr,   "YD_STK_LOT_CD");
        	szYD_STK_BED_NULL_GP = ydDaoUtils.paraRecChkNull(recGetStkLyr,   "YD_STK_BED_NULL_GP");
        	szYD_STK_COL_GP      = ydDaoUtils.paraRecChkNull(recGetStkLyr,   "YD_STK_COL_GP");
        	
        	//재료가 있으나 산적LotType와 코드가 없는경우
        	if((szStkLyrStkLotTp.equals("") || szStkLyrStkLotCd.equals(""))&& (!szYD_STK_BED_NULL_GP.equals("Y"))) {
        		intRtnVal = 10;
				szMsg="저장위치 등급 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        		return intRtnVal;
        	}
        	
        	//To위치가 공베드 = 4등급
        	if(szYD_STK_BED_NULL_GP.equals("Y")) {
        		if(szYD_STK_COL_GP.substring(2,4).equals("TC") || szYD_STK_COL_GP.substring(2,4).equals("PT") 
        			|| szYD_STK_COL_GP.substring(2,4).equals("TR") || szYD_STK_COL_GP.substring(2,4).equals("PU")
        			|| szYD_STK_COL_GP.substring(2,4).equals("DP") ){
        				intRtnVal = 1;
        			}else{
        				intRtnVal = 2;
        			}
        		
				szMsg="저장위치 등급 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        		return intRtnVal;
        	}
        	

        	if(szStkLyrStkLotCd.equals("") && szCrnWrkMtlStkLotCd.equals("")){
        		intRtnVal = 99;
    			ydUtils.putLog(szSessionName, szMethodName, "양쪽 값이 존재 하지 않음  : 저장위치 등급 : " + intRtnVal, YdConstant.ERROR);
    			return intRtnVal;
        	}
        	
        	
        	szCrnWrkMtlStkLotCd = ydUtils.fillSpZr(szCrnWrkMtlStkLotCd, 14, 1);
        	szStkLyrStkLotCd    = ydUtils.fillSpZr(szStkLyrStkLotCd, 14, 1);
			
        	
        	
        	szMsg = "JSP-SESSION [  등급부여 (장입LOT)] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
	    	
	    		
			//산적LotType와 Code가 같은경우 1등급
			if(szStkLyrStkLotCd.equals(szCrnWrkMtlStkLotCd) ) {
				intRtnVal = 1;
				szMsg="저장위치 등급 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return intRtnVal;
			
			//코드가 틀린 경우 3등급
			}else if( (szStkLyrStkLotCd.substring(0,7)).equals( szCrnWrkMtlStkLotCd.substring(0,7) ) ) {
				intRtnVal = 3;
				szMsg="저장위치 등급 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return intRtnVal;
				
			}else{
    			intRtnVal = 10;
        		ydUtils.putLog(szSessionName, szMethodName, "저장위치 등급 : " + intRtnVal, YdConstant.ERROR);
    			return intRtnVal;
    		}
	
    	}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "<Y1GradeTest> Exception Error :"+ e.getLocalizedMessage(), YdConstant.ERROR);
			return intRtnVal = 100;
        }//end of try~catch
    	
    }//end of Y1GradeTest3()
    
    /**
     * 오퍼레이션명 : 등급부여 (장입순번)
     *  
     * @param  rsResultCrnwrkmtl, rsBed
     * @return int 성공:1, 실패:-1
     * @throws 
     */
    public int Y1GradeTest4 (JDTORecord recGetCrnWrkMtl, JDTORecord recGetStkLyr){
    	//상위 Method : chkLocsrchbedCrnMtl
		//┏━┓
		//┃ 저장위치의 등급을 구한다.
		//┗━┛
    	
    	int intRtnVal = 100;
    	
        //크레인작업재료의 최하단 산적LotType,산적Lot코드,폭
        String szCrnWrkMtlStkLotTp = "";
        String szCrnWrkMtlStkLotCd = "";
        
        //적치단의 최상단 산적LotType,산적Lot코드,폭
        String szStkLyrStkLotTp     = "";
        String szStkLyrStkLotCd     = "";
        String szYD_STK_BED_NULL_GP = "";
        String szYD_STK_COL_GP      = "";
        
        String szMsg = "";
        String szMethodName = "Y1GradeTest4";
    	
        try{
        	
        	szMsg = "JSP-SESSION [등급부여 (장입순번)]시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
        	szCrnWrkMtlStkLotTp  = ydDaoUtils.paraRecChkNull(recGetCrnWrkMtl,"YD_STK_LOT_TP");
        	szCrnWrkMtlStkLotCd  = ydDaoUtils.paraRecChkNull(recGetCrnWrkMtl,"YD_STK_LOT_CD");
        	szStkLyrStkLotTp     = ydDaoUtils.paraRecChkNull(recGetStkLyr,   "YD_STK_LOT_TP");
        	szStkLyrStkLotCd     = ydDaoUtils.paraRecChkNull(recGetStkLyr,   "YD_STK_LOT_CD");
        	szYD_STK_BED_NULL_GP = ydDaoUtils.paraRecChkNull(recGetStkLyr,   "YD_STK_BED_NULL_GP");
        	szYD_STK_COL_GP      = ydDaoUtils.paraRecChkNull(recGetStkLyr,   "YD_STK_COL_GP");

        	//재료가 있으나 산적LotType와 코드가 없는경우
        	if((szStkLyrStkLotTp.equals("") || szStkLyrStkLotCd.equals(""))&& (!szYD_STK_BED_NULL_GP.equals("Y"))) {
        		intRtnVal = 10;
				szMsg="저장위치 등급 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        		return intRtnVal;
        	}
        	
        
        	//To위치가 공베드 = 4등급
        	if(szYD_STK_BED_NULL_GP.equals("Y")) {
        		if(szYD_STK_COL_GP.substring(2,4).equals("TC") || szYD_STK_COL_GP.substring(2,4).equals("PT") 
        			|| szYD_STK_COL_GP.substring(2,4).equals("TR") || szYD_STK_COL_GP.substring(2,4).equals("PU")
        			|| szYD_STK_COL_GP.substring(2,4).equals("DP") ){
        				intRtnVal = 1;
        			}else{
        				intRtnVal = 2;
        			}
        		
				szMsg="저장위치 등급 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        		return intRtnVal;
        	}
        	

        	if(szStkLyrStkLotCd.equals("") && szCrnWrkMtlStkLotCd.equals("")){
        		intRtnVal = 99;
    			ydUtils.putLog(szSessionName, szMethodName, "양쪽 값이 존재 하지 않음  : 저장위치 등급 : " + intRtnVal, YdConstant.ERROR);
    			return intRtnVal;
        	}
        	
        	
        	szCrnWrkMtlStkLotCd = ydUtils.fillSpZr(szCrnWrkMtlStkLotCd, 14, 1);
        	szStkLyrStkLotCd    = ydUtils.fillSpZr(szStkLyrStkLotCd, 14, 1);
			
        	
    	
        	
        	szMsg = "JSP-SESSION [등급부여 (장입순번)] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
    		
			//산적LotType와 Code가 같은경우 1등급
			if(szStkLyrStkLotCd.equals(szCrnWrkMtlStkLotCd) ) {
				intRtnVal = 1;
				szMsg="저장위치 등급 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return intRtnVal;
			
			//코드가 틀린 경우 3등급
			}else if( (szStkLyrStkLotCd.substring(0,2)).equals( szCrnWrkMtlStkLotCd.substring(0,2) ) ) {
				intRtnVal = 3;
				szMsg="저장위치 등급 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return intRtnVal;

			}else{
    			intRtnVal = 10;
        		ydUtils.putLog(szSessionName, szMethodName, "저장위치 등급 : " + intRtnVal, YdConstant.ERROR);
    			return intRtnVal;
    		}
			
    	}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "<Y1GradeTest> Exception Error :"+ e.getLocalizedMessage(), YdConstant.ERROR);
			return intRtnVal = 100;
        }//end of try~catch
    	
    }//end of Y1GradeTest4()
    
    /**
     * 오퍼레이션명 : 등급부여 (스카핑재)
     *  
     * @param  rsResultCrnwrkmtl, rsBed
     * @return int 성공:1, 실패:-1
     * @throws 
     */
    public int Y1GradeTest5 (JDTORecord recGetCrnWrkMtl, JDTORecord recGetStkLyr){
    	//상위 Method : chkLocsrchbedCrnMtl
		//┏━┓
		//┃ 저장위치의 등급을 구한다.
		//┗━┛
    	
    	int intRtnVal = 100;
    	
        //크레인작업재료의 최하단 산적LotType,산적Lot코드,폭
        String szCrnWrkMtlStkLotTp = "";
        String szCrnWrkMtlStkLotCd = "";
        
        //적치단의 최상단 산적LotType,산적Lot코드,폭
        String szStkLyrStkLotTp     = "";
        String szStkLyrStkLotCd     = "";
        String szYD_STK_BED_NULL_GP = "";
        String szYD_STK_COL_GP      = "";
        
        String szMsg = "";
        String szMethodName = "Y1GradeTest5";
    	
        try{
        	
        	szMsg = "JSP-SESSION [등급부여 (스카핑재)]시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
        	szCrnWrkMtlStkLotTp  = ydDaoUtils.paraRecChkNull(recGetCrnWrkMtl,"YD_STK_LOT_TP");
        	szCrnWrkMtlStkLotCd  = ydDaoUtils.paraRecChkNull(recGetCrnWrkMtl,"YD_STK_LOT_CD");
        	szStkLyrStkLotTp     = ydDaoUtils.paraRecChkNull(recGetStkLyr,   "YD_STK_LOT_TP");
        	szStkLyrStkLotCd     = ydDaoUtils.paraRecChkNull(recGetStkLyr,   "YD_STK_LOT_CD");
        	szYD_STK_BED_NULL_GP = ydDaoUtils.paraRecChkNull(recGetStkLyr,   "YD_STK_BED_NULL_GP");
        	szYD_STK_COL_GP      = ydDaoUtils.paraRecChkNull(recGetStkLyr,   "YD_STK_COL_GP");

        	//재료가 있으나 산적LotType와 코드가 없는경우
        	if((szStkLyrStkLotTp.equals("") || szStkLyrStkLotCd.equals(""))&& (!szYD_STK_BED_NULL_GP.equals("Y"))) {
        		intRtnVal = 10;
				szMsg="저장위치 등급 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        		return intRtnVal;
        	}
        
			
        	//To위치가 공베드 = 4등급
        	if(szYD_STK_BED_NULL_GP.equals("Y")) {
        		if(szYD_STK_COL_GP.substring(2,4).equals("TC") || szYD_STK_COL_GP.substring(2,4).equals("PT") 
        			|| szYD_STK_COL_GP.substring(2,4).equals("TR") || szYD_STK_COL_GP.substring(2,4).equals("PU")
        			|| szYD_STK_COL_GP.substring(2,4).equals("DP") ){
        				intRtnVal = 1;
        			}else{
        				intRtnVal = 2;
        			}
        		
				szMsg="저장위치 등급 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        		return intRtnVal;
        	}
    	
        	

        	if(szStkLyrStkLotCd.equals("") && szCrnWrkMtlStkLotCd.equals("")){
        		intRtnVal = 99;
    			ydUtils.putLog(szSessionName, szMethodName, "양쪽 값이 존재 하지 않음  : 저장위치 등급 : " + intRtnVal, YdConstant.ERROR);
    			return intRtnVal;
        	}
        	
        	
        	szCrnWrkMtlStkLotCd = ydUtils.fillSpZr(szCrnWrkMtlStkLotCd, 14, 1);
        	szStkLyrStkLotCd    = ydUtils.fillSpZr(szStkLyrStkLotCd, 14, 1);
			
        	
	    		
        	
        	szMsg = "JSP-SESSION [등급부여 (스카핑재)] 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			
			//산적LotType와 Code가 같은경우 1등급
			if(szStkLyrStkLotCd.equals(szCrnWrkMtlStkLotCd) ) {
				intRtnVal = 1;
				szMsg="저장위치 등급 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg,YdConstant.DEBUG);
				return intRtnVal;
			
			//코드가 틀린 경우 2등급
			}else if( (szStkLyrStkLotCd.substring(0,10)).equals( szCrnWrkMtlStkLotCd.substring(0,10) ) ) {
				intRtnVal = 3;
				szMsg="저장위치 등급 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return intRtnVal;
			}else if(szStkLyrStkLotCd.substring(0,9).equals(szCrnWrkMtlStkLotCd.substring(0,9)) ) {
				intRtnVal = 4;
				szMsg="저장위치 등급 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return intRtnVal;
			}else if(szStkLyrStkLotCd.substring(0,3).equals(szCrnWrkMtlStkLotCd.substring(0,3)) ) {
				intRtnVal = 5;
				szMsg="저장위치 등급 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return intRtnVal;
			}else if(szStkLyrStkLotCd.substring(0,2).equals(szCrnWrkMtlStkLotCd.substring(0,2)) ) {
				intRtnVal = 6;
				szMsg="저장위치 등급 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return intRtnVal;

			}else{
    			intRtnVal = 10;
        		ydUtils.putLog(szSessionName, szMethodName, "저장위치 등급 : " + intRtnVal, YdConstant.ERROR);
    			return intRtnVal;
    		}
			
    	}catch(Exception e){
			ydUtils.putLog(szSessionName, szMethodName, "<Y1GradeTest> Exception Error :"+ e.getLocalizedMessage(), YdConstant.ERROR);
			return intRtnVal = 100;
        }//end of try~catch
    	
    }//end of Y1GradeTest5()
    
    /**
     * 오퍼레이션명 : 등급부여 (후판정정재)
     *  
     * @param  rsResultCrnwrkmtl, rsBed
     * @return int 성공:1, 실패:-1
     * @throws 
     */
    public int Y1GradeTest6 (JDTORecord recGetCrnWrkMtl, JDTORecord recGetStkLyr){
    	//상위 Method : chkLocsrchbedCrnMtl
		//┏━┓
		//┃ 저장위치의 등급을 구한다.
		//┗━┛
    	
    	int intRtnVal = 100;
    	
        //크레인작업재료의 최하단 산적LotType,산적Lot코드,폭
        String szCrnWrkMtlStkLotTp = "";
        String szCrnWrkMtlStkLotCd = "";
        
        //적치단의 최상단 산적LotType,산적Lot코드,폭
        String szStkLyrStkLotTp     = "";
        String szStkLyrStkLotCd     = "";
        String szYD_STK_BED_NULL_GP = "";
        String szYD_STK_COL_GP      = "";
        
        String szMsg = "";
        String szMethodName = "Y1GradeTest6";
    	
        try{
        	
        	szMsg = "JSP-SESSION [ 등급부여 (후판정정재)]시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
        	szCrnWrkMtlStkLotTp  = ydDaoUtils.paraRecChkNull(recGetCrnWrkMtl,"YD_STK_LOT_TP");
        	szCrnWrkMtlStkLotCd  = ydDaoUtils.paraRecChkNull(recGetCrnWrkMtl,"YD_STK_LOT_CD");
        	szStkLyrStkLotTp     = ydDaoUtils.paraRecChkNull(recGetStkLyr,   "YD_STK_LOT_TP");
        	szStkLyrStkLotCd     = ydDaoUtils.paraRecChkNull(recGetStkLyr,   "YD_STK_LOT_CD");
        	szYD_STK_BED_NULL_GP = ydDaoUtils.paraRecChkNull(recGetStkLyr,   "YD_STK_BED_NULL_GP");
        	szYD_STK_COL_GP      = ydDaoUtils.paraRecChkNull(recGetStkLyr,   "YD_STK_COL_GP");

        	//재료가 있으나 산적LotType와 코드가 없는경우
        	if((szStkLyrStkLotTp.equals("") || szStkLyrStkLotCd.equals(""))&& (!szYD_STK_BED_NULL_GP.equals("Y"))) {
        		intRtnVal = 10;
				szMsg="저장위치 등급 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        		return intRtnVal;
        	}
        			
        	//To위치가 공베드 = 4등급
        	if(szYD_STK_BED_NULL_GP.equals("Y")) {
        		if(szYD_STK_COL_GP.substring(2,4).equals("TC") || szYD_STK_COL_GP.substring(2,4).equals("PT") 
        			|| szYD_STK_COL_GP.substring(2,4).equals("TR") || szYD_STK_COL_GP.substring(2,4).equals("PU")
        			|| szYD_STK_COL_GP.substring(2,4).equals("DP") ){
        				intRtnVal = 1;
        			}else{
        				intRtnVal = 2;
        			}
        		
				szMsg="저장위치 등급 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
        		return intRtnVal;
        	}
    	
        	

        	if(szStkLyrStkLotCd.equals("") && szCrnWrkMtlStkLotCd.equals("")){
        		intRtnVal = 99;
    			ydUtils.putLog(szSessionName, szMethodName, "양쪽 값이 존재 하지 않음  : 저장위치 등급 : " + intRtnVal, YdConstant.ERROR);
    			return intRtnVal;
        	}
        	
        	
        	szCrnWrkMtlStkLotCd = ydUtils.fillSpZr(szCrnWrkMtlStkLotCd, 14, 1);
        	szStkLyrStkLotCd    = ydUtils.fillSpZr(szStkLyrStkLotCd, 14, 1);
			
        	
        	
        	szMsg = "JSP-SESSION [ 등급부여 (후판정정재)] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
    		
			//산적LotType와 Code가 같은경우 1등급
			if(szStkLyrStkLotCd.equals(szCrnWrkMtlStkLotCd) ) {
				intRtnVal = 1;
				szMsg="저장위치 등급 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return intRtnVal;
			
			//코드가 틀린 경우 2등급
			}else if(szStkLyrStkLotCd.substring(0,13).equals(szCrnWrkMtlStkLotCd.substring(0,13)) ) {
				intRtnVal = 3;
				szMsg="저장위치 등급 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return intRtnVal;
			}else if(szStkLyrStkLotCd.substring(0,9).equals(szCrnWrkMtlStkLotCd.substring(0,9)) ) {
				intRtnVal = 4;
				szMsg="저장위치 등급 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return intRtnVal;
			}else if(szStkLyrStkLotCd.substring(0,3).equals(szCrnWrkMtlStkLotCd.substring(0,3)) ) {
				intRtnVal = 5;
				szMsg="저장위치 등급 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return intRtnVal;
			}else if(szStkLyrStkLotCd.substring(0,2).equals(szCrnWrkMtlStkLotCd.substring(0,2)) ) {
				intRtnVal = 6;
				szMsg="저장위치 등급 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return intRtnVal;
    		}else{
				intRtnVal = 10;
				szMsg="저장위치 등급 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    		}
			
	
	    	return intRtnVal;
    	}catch(Exception e){
    		ydUtils.putLog(szSessionName, szMethodName, "<Y1GradeTest> Exception Error :"+ e.getLocalizedMessage(), YdConstant.ERROR);
			return intRtnVal = 100;
        }//end of try~catch
    	
    }//end of Y1GradeTest6()
	
	
	/**
	 *  야드 주편공통, 슬라브공통 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getPtCommStock(JDTORecord inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg  = "";
		String szMethodName="getPtCommStock";
		String szOperationName = "주편공통, 슬라브공통 조회";
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecord    outSet    = JDTORecordFactory.getInstance().create();
		try {
			
			szMsg = "JSP-SESSION [ 야드 주편공통, 슬라브공통 조회]시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			recPara.setField("STL_NO", yddatautil.setDataDefault(inDto.getField("STL_NO"), ""));
			
			//YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
			//intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, outRecSet, 33);
			String sResult = YdCommonUtils.getPtCommStock(recPara.getFieldString("STL_NO"), outRecSet);
			StockSpecRegSeEJBBean stockSpecReg = new StockSpecRegSeEJBBean();
			outSet = outRecSet.getRecord(0);
			// SLAB일 경우
			if("S".equals(outSet.getFieldString("PT_TB_COMM"))) {
				outSet.setField("STL_NO", outSet.getFieldString("SLAB_NO"));
			}
			// MSLAB일 경우
			else if("B".equals(outSet.getFieldString("PT_TB_COMM"))) {
				outSet.setField("STL_NO", outSet.getFieldString("MSLAB_NO"));
			}
			ydUtils.displayRecord(szOperationName, outSet);
			intRtnVal = stockSpecReg.SetYD_MTL_ITEM_SLAB(outSet);
			/*
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
			*/
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);			
		
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		szMsg = "JSP-SESSION [ 야드 주편공통, 슬라브공통 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return outRecSet;
	}//end of getPtCommStock
	
	
	
	
	
	/**
	 *  검수 완료 기능
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return String
	 * @throws DAOException
	 */
	public String inspectionComplete(JDTORecord inDto) throws DAOException {
	
		int intRtnVal = 0;
		String szMsg            = "";
		String szRcvMsg         = "";
		String szMethodName     ="inspectionComplete";
		String szOperationName = "검수 완료";
		
		
		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecord    recSend   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		
		String sz_CAR_NO        = "";
		String sz_MODIFIER          = "";
		
		
		
		
		
		
		/*
		 * 필요 DAO
		 */
		YdCarSchDao ydCarSchDao = new YdCarSchDao();
		
		
		try {
			
			szMsg = "JSP-SESSION [ 검수 완료]시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			
			//정보 유무 CHECK
			
			/*
			 * - Check 해야할 정보 - 
			 * 
			 * 차량스케줄 ID
			 * 차량번호
			 * CARD NO
			 * 운송지시일자
			 * 운송지시순번  
			 */
			
			sz_CAR_NO             = ydDaoUtils.paraRecChkNull(inDto,"CAR_NO");
			
			if(sz_CAR_NO.equals("")){
				 szRcvMsg = "CAR_NO 존재 하지 않습니다";				 
				 ydUtils.putLog(szSessionName, szMethodName, szRcvMsg, YdConstant.ERROR);
				 return szRcvMsg ;				
			}
			
			recPara.setField("CAR_NO", sz_CAR_NO);
			recPara.setField("TRN_EQP_CD", sz_CAR_NO);
			
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, outRecSet, 21);
			
			if (intRtnVal < 0 ) {
				 szRcvMsg = "차량 스케줄 조회시 ERROR";				 
				 ydUtils.putLog(szSessionName, szMethodName, szRcvMsg, YdConstant.ERROR);
				 return szRcvMsg ;	
			
			}
			
			
			else if (intRtnVal == 0 ) {
				 szRcvMsg = "조회된 차량 스케줄이 없습니다.";			 
				 ydUtils.putLog(szSessionName, szMethodName, szRcvMsg, YdConstant.WARNING);
				 return szRcvMsg ;	
			}
			
			outRecSet.first();
			recPara   = JDTORecordFactory.getInstance().create();
			
			recPara   = outRecSet.getRecord();
			sz_MODIFIER           = ydDaoUtils.paraRecChkNull(inDto,"YD_USER_ID");
			
			
			// 차량 스케줄 ID정보에 진행상태를 검수완료로 UPDATE
			
			 recPara.setField("YD_CAR_PROG_STAT", "5"); //3: 상차검수 , 5: 상차완료 둘중에 맞는걸로 Setting 할것
			 recPara.setField("MODIFIER",  sz_MODIFIER);
			 recPara.setField("YD_CARLD_CMPL_DT", 	ydUtils.getCurDate("yyyyMMddHHmmss"));
			 intRtnVal = ydCarSchDao.updYdCarsch(recPara, 0);
			 
			 if (intRtnVal < 0){
				 szRcvMsg = "차량 스케줄 ID정보에 진행상태를 검수완료로 UPDATE 할수 없습니다";
				 ydUtils.putLog(szSessionName, szMethodName, szRcvMsg, YdConstant.ERROR);
				 return szRcvMsg;
			 }
			 
			
			// 출하로 검수완료 전문 전송
			 recSend   = JDTORecordFactory.getInstance().create();
			 recSend.setField("TC_CODE", 			"YDDMR027");                 //검수완료 실적
			 recSend.setField("CARD_NO", 			ydDaoUtils.paraRecChkNull(recPara, "CARD_NO"));
			 recSend.setField("CAR_NO",  			ydDaoUtils.paraRecChkNull(recPara, "CAR_NO"));
			 recSend.setField("ISSUE_CHK_WORKER",   sz_MODIFIER);                //20090805_출하야드회의시 접속자정보를 전송해달라고 함
			 recSend.setField("TRANS_ORD_DATE",     ydDaoUtils.paraRecChkNull(recPara, "TRANS_ORD_DATE"));
			 recSend.setField("TRANS_ORD_SEQNO",    ydDaoUtils.paraRecChkNull(recPara, "TRANS_ORD_SEQNO"));
			 

			 szMsg = "YDDMR027 검수완료 실적 화면에서 전송";
			 ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			 
			 
			 ydUtils.displayRecord(szOperationName, recSend);
			 
			 YdDelegate ydDelegate = new YdDelegate();
			 ydDelegate.sendMsg(recSend);
			 
			szMsg = "JSP-SESSION [ 검수 완료] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return YdConstant.RETN_CD_SUCCESS;
	}//end of inspectionComplete
	

	/**
	 *  한 건의 차량스케줄 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCarSchById(JDTORecord inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg  = "";
		String szMethodName="getCarSchById";
		YdCarSchDao ydCarSchDao = new YdCarSchDao();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		String szYD_CAR_SCH_ID = null;
		try {
			
			szMsg = "JSP-SESSION [차량스케줄 조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(inDto, "YD_CAR_SCH_ID");
			recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, outRecSet, 0);
			
			if( intRtnVal == 0 ) {
				szMsg = "[JSP Session] 차량스케줄조회 - 차량스케줄["+szYD_CAR_SCH_ID+"]이 존재하지 않습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				throw new DAOException(YdConstant.RETN_CD_NOTEXIST);
			}else if( intRtnVal < 0 ) {
				szMsg = "[JSP Session] 차량스케줄조회 - 차량스케줄["+szYD_CAR_SCH_ID+"] 조회 시 오류발생 : 반환값 - " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new DAOException(YdConstant.RETN_CD_FAILURE);
			}
			szMsg = "[JSP Session] 차량스케줄조회 - 차량스케줄["+szYD_CAR_SCH_ID+"]이 존재합니다. = " + intRtnVal;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		
		} catch (JDTOException e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		szMsg = "JSP-SESSION [차량스케줄 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return outRecSet;
	}//end of getCarSchById
	
	/**
	 * 차량스케줄/차량Point삭제
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String delCarSchNCarPoint(JDTORecord[] inDto) throws DAOException {
		String szRtnMsg				= null;
		String szMsg 				= "";
		String szMethodName			= "delCarSchNCarPoint";
		String szOperationName 		= "차량스케줄/차량Point삭제";
		
		String szYD_CAR_SCH_ID		= null;
		
		try {
			szMsg = "[Jsp Session : "+szOperationName+"] --------------------- 메소드 시작 ---------------------";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			for(int i = 0; i < inDto.length; i++ ) {
				
				szYD_CAR_SCH_ID		=	ydDaoUtils.paraRecChkNull(inDto[i], "YD_CAR_SCH_ID");
			
				szMsg = "[Jsp Session : "+szOperationName+"] ["+(i + 1)+"]차량스케줄["+szYD_CAR_SCH_ID+"] 삭제 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				
				szRtnMsg		= YdCommonUtils.delCarSchNCarPointByCarSchId(inDto[i], szMethodName);
			
				szMsg = "[Jsp Session : "+szOperationName+"] ["+(i + 1)+"]차량스케줄["+szYD_CAR_SCH_ID+"] 삭제 완료 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			}
			
			szMsg = "[Jsp Session : "+szOperationName+"] --------------------- 메소드 끝 ---------------------";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		}catch(Exception ex) {
			szMsg = "[Jsp Session : "+szOperationName+"] 오류발생 : " + ex.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	/**
	 * 차량출발처리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String procLeaveCar(JDTORecord[] inDto) throws DAOException {
		String szRtnMsg				= null;
		String szMsg 				= "";
		String szMethodName			= "procLeaveCar";
		String szOperationName 		= "차량출발처리";
		
		JDTORecordSet	rsResult	= null;
		JDTORecord		recInTemp	= null;
		JDTORecord		recTemp		= null;
		
		String szYD_CAR_SCH_ID		= null;
		String szYD_CAR_PROG_STAT	= null;
		String szCAR_NO				= null;
		String szCARD_NO			= null;
		String szSPOS_WLOC_CD		= null;
		String szYD_PNT_CD			= null;
		String szTRANS_ORD_DATE		= null;
		String szTRANS_ORD_SEQNO	= null;
		
		YdDelegate ydDelegate = new YdDelegate();
		
		try {
			szMsg = "[Jsp Session : "+szOperationName+"] --------------------- 메소드 시작 ---------------------";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			recInTemp = JDTORecordFactory.getInstance().create();
			
			for(int i = 0; i < inDto.length; i++ ) {
				
				szYD_CAR_SCH_ID		=	ydDaoUtils.paraRecChkNull(inDto[i], "YD_CAR_SCH_ID");
				
				
				//--------------------------------------------------------------------------------
				//	차량스케줄ID로 차량스케줄 조회
				//--------------------------------------------------------------------------------
				
				szMsg = "["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"] 조회 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				rsResult		= JDTORecordFactory.getInstance().createRecordSet("");
				recTemp			= JDTORecordFactory.getInstance().create();
				
				recTemp.setField("YD_CAR_SCH_ID", 			szYD_CAR_SCH_ID);
				
				szRtnMsg		= DaoManager.getYdCarsch(recTemp, rsResult, 0);
				
				if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
					szMsg = "["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"] 조회 시 오류발생 - 메세지 : " + szRtnMsg;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					//return szRtnMsg;
					continue;
				}
				
				rsResult.first();
				recTemp		= rsResult.getRecord();
				
				szYD_CAR_PROG_STAT		= ydDaoUtils.paraRecChkNull(recTemp, "YD_CAR_PROG_STAT");
				
				if( !szYD_CAR_PROG_STAT.equals(YdConstant.YD_CARLD_CMPL)) {
					szMsg = "["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"]의 차량진행상태["+szYD_CAR_PROG_STAT+"]가 상차완료가 아니므로 SKIP시킴";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					continue;
				}
				
				szCAR_NO				= ydDaoUtils.paraRecChkNull(recTemp, "CAR_NO");
				szCARD_NO				= ydDaoUtils.paraRecChkNull(recTemp, "CARD_NO");
				szSPOS_WLOC_CD			= ydDaoUtils.paraRecChkNull(recTemp, "SPOS_WLOC_CD");
				szYD_PNT_CD				= ydDaoUtils.paraRecChkNull(recTemp, "YD_PNT_CD1");
				szTRANS_ORD_DATE		= ydDaoUtils.paraRecChkNull(recTemp, "TRANS_ORD_DATE");
				szTRANS_ORD_SEQNO		= ydDaoUtils.paraRecChkNull(recTemp, "TRANS_ORD_SEQNO");
//PIDEV				
				String sYD_GP		    = ydDaoUtils.paraRecChkNull(recTemp, "YD_GP");
				
				szMsg = "["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"] 조회 완료 - 차량진행상태["+szYD_CAR_PROG_STAT+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				
				
				//--------------------------------------------------------------------------------
			
				
				//--------------------------------------------------------------------------------
				//
				//--------------------------------------------------------------------------------
				
				szMsg = "[Jsp Session : "+szOperationName+"] ["+(i + 1)+"]차량스케줄["+szYD_CAR_SCH_ID+"]의 차량출발 처리 EJB 호출 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				
				
				recInTemp.setField("TC_CODE",        		"YDYDJ659");									//전문코드
				recInTemp.setField("CARD_NO", 				szCARD_NO);
				recInTemp.setField("CAR_NO", 				szCAR_NO);			
				recInTemp.setField("SPOS_WLOC_CD", 			szSPOS_WLOC_CD);
				recInTemp.setField("SPOS_YD_PNT_CD", 		szYD_PNT_CD);
				recInTemp.setField("TRANS_ORD_DT", 			szTRANS_ORD_DATE);
				recInTemp.setField("TRANS_ORD_SEQNO", 		szTRANS_ORD_SEQNO);
				//PIDEV
				recInTemp.setField("PI_YD", 		        sYD_GP);
				
				// 2021. 06. 22 차량선별입동지시시점[T00171] "D"일경우 입동지시 전문이 날아가지 않는 문제 수정
				recInTemp.setField("CALL_PGM", "SANGCHA"); // 입동지시 전문을 보내기 위함
				
				EJBConnector ejbConn = new EJBConnector("default", "CarMvHdFaEJB", this);
				ejbConn.trx("rcvPlGdsDistCarLevWr", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
				 
				//ydDelegate.sendMsg(recInTemp);
			
				szMsg = "[Jsp Session : "+szOperationName+"] ["+(i + 1)+"]차량스케줄["+szYD_CAR_SCH_ID+"]의 차량출발 처리 EJB 호출 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				
				//--------------------------------------------------------------------------------
			}
			
			szMsg = "[Jsp Session : "+szOperationName+"] --------------------- 메소드 끝 ---------------------";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		}catch(Exception ex) {
			szMsg = "[Jsp Session : "+szOperationName+"] 오류발생 : " + ex.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	/**
	 * 입동지시
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String procBayInWo(JDTORecord[] inDto) throws DAOException {
		String szRtnMsg				= null;
		String szMsg 				= "";
		String szMethodName			= "procBayInWo";
		String szOperationName 		= "입동지시";
		
		JDTORecord		recInTemp	= null;
		JDTORecord inRec 				= null;
		JDTORecord outRec 				= null;
		
		
		String szYD_STK_COL_GP		= null;
		JDTORecordSet outRecSet 		= null;
		
		int intRtnVal = 0;
		YdDelegate ydDelegate = new YdDelegate();
		YdCarSchDao ydCarSchDao = new YdCarSchDao();		//차량스케줄DAO
		YdStkColDao ydStkColDao = new YdStkColDao();		//적치열DAO
	
		String szYD_CAR_USE_GP 		= "";
		String szCAR_NO 			= "";
		String szCARD_NO 			= "";
		String szTRANS_ORD_DATE 	= "";
		String szTRANS_ORD_SEQNO 	= "";
		String szTRN_EQP_CD 		= "";
		String szWLOC_CD 			= "";
		String szYD_PNT_CD 			= ""; 
		String szYD_CARPNT_CD2      = ""; 
		
		
		try {
			szMsg = "[Jsp Session : "+szOperationName+"] --------------------- 메소드 시작 ---------------------";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("TC_CODE"				,"YDYDJ633");
			recInTemp.setField("TC_CREATE_DDTT"			,YdUtils.getCurDate("yyyyMMddHHmmss"));
			
			for(int i = 0; i < inDto.length; i++ ) {
				
				szYD_STK_COL_GP		=	ydDaoUtils.paraRecChkNull(inDto[i], "YD_STK_COL_GP");
			
				szMsg = "[Jsp Session : "+szOperationName+"] ["+(i + 1)+"] 차량Point["+szYD_STK_COL_GP+"]에 대한 입동지시 모듈 JMS 호출 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				
				recInTemp.setField("YD_CAR_STOP_LOC"		,szYD_STK_COL_GP);
				recInTemp.setField("CALL_PGM"			    ,"SANGCHA");
				
				ydUtils.displayRecord(szOperationName, recInTemp);
				
				ydDelegate.sendMsg(recInTemp);
				
				szMsg = "[Jsp Session : "+szOperationName+"] ["+(i + 1)+"] 차량Point["+szYD_STK_COL_GP+"]에 대한 입동지시 모듈 JMS 호출 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
	
//SJH03001				
				if(YdConstant.YD_GP_PLATE_GDS_YARD.equals(szYD_STK_COL_GP.substring(0,1)) 
					|| YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYD_STK_COL_GP.substring(0,1))){ // - 2013.01.18 수정 (3기)
					
					outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
					inRec = JDTORecordFactory.getInstance().create();
					inRec.setField("YD_CAR_STOP_LOC", szYD_STK_COL_GP);
					/* com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdCarSchByInSeqCheck */
					intRtnVal = ydCarSchDao.getYdCarsch(inRec, outRecSet, 310);
					
					if( intRtnVal == 0 ) {
						szMsg= "["+ szMethodName +"] 입동지시할 차량스케줄이 존재하지 않습니다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						return YdConstant.RETN_CD_NOTEXIST;
					}else if( intRtnVal < 0 ) {
						szMsg= "["+ szMethodName +"] 입동지시순서 목록 조회 시 오류발생 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						return YdConstant.RETN_CD_NOTEXIST;
					}
					outRecSet.first();
					outRec = outRecSet.getRecord();
					
					szYD_CAR_USE_GP 	= StringHelper.evl(outRec.getFieldString("YD_CAR_USE_GP"), "");
					szCAR_NO 			= StringHelper.evl(outRec.getFieldString("CAR_NO"), "");
					szCARD_NO 			= StringHelper.evl(outRec.getFieldString("CARD_NO"), "");
					szTRANS_ORD_DATE 	= StringHelper.evl(outRec.getFieldString("TRANS_ORD_DATE"), "");
					szTRANS_ORD_SEQNO 	= StringHelper.evl(outRec.getFieldString("TRANS_ORD_SEQNO"), "");
					szWLOC_CD 			= StringHelper.evl(outRec.getFieldString("WLOC_CD"), ""); 
	    			szYD_PNT_CD 		= StringHelper.evl(outRec.getFieldString("YD_PNT_CD"), ""); 
	    			szYD_CARPNT_CD2		= StringHelper.evl(outRec.getFieldString("YD_CARPNT_CD"), ""); 
	    			
					if( szYD_CAR_USE_GP.equals(YdConstant.YD_CAR_USE_GP_DM)) {
						szMsg= "["+ szOperationName +"] 입동순서가 가장빠른 차량이 출하차량[차량번호:" + szCAR_NO + ", 카드번호:" + szCARD_NO + ", 운송지시일자:" + szTRANS_ORD_DATE + ", 운송지시순번:" + szTRANS_ORD_SEQNO + "]이므로 입동지시 전문을 전송한다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
						//------------------------------------------------------------------------------------------------------------
						//	신 상차처리 적용여부
			            //  권상시 입동 지시로 
						//------------------------------------------------------------------------------------------------------------
						YdEqpDao   ydEqpDao   = new YdEqpDao();
						JDTORecordSet 	outResult  	= JDTORecordFactory.getInstance().createRecordSet("");
						JDTORecord 		inRecord 	= JDTORecordFactory.getInstance().create();
						JDTORecord 		outRecord  	= JDTORecordFactory.getInstance().create();
						
						String szAPPLY_YN9  = "";
						//inRecord.setField("REPR_CD_GP", "K00171");	
						if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYD_STK_COL_GP.substring(0,1))) { //-2013.01.18 수정 (3기)
							inRecord.setField("REPR_CD_GP", "T00171"); //2후판 제품창고야드 기준 
						} else {
							inRecord.setField("REPR_CD_GP", "K00171"); //1후판 제품창고야드 기준
						}
						
						inRecord.setField("CD_GP", szYD_STK_COL_GP.substring(4,5));  // 통로
						inRecord.setField("ITEM", szYD_STK_COL_GP.substring(1,2));	 // 동
						
						/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getPlateYdRuleMgtYN*/
						intRtnVal = ydEqpDao.getYdEqp(inRecord, outResult, 999);
						if(intRtnVal > 0) {
							outResult.first();
							outRecord  = outResult.getRecord();
							szAPPLY_YN9 = outRecord.getFieldString("ITEM1");				
						}						
						
						szMsg ="신 입동지시 적용여부 " + szAPPLY_YN9 ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						if(szAPPLY_YN9.equals("S")) {	
							szMsg ="신 입동지시 여부 " + szAPPLY_YN9 + " 이므로 송신 안함" ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						} else{	
							outRec = JDTORecordFactory.getInstance().create();
							outRec.setField("TC_CODE"				,"YDDMR028");
							outRec.setField("TRANS_WORD_DATE"		,szTRANS_ORD_DATE);
							outRec.setField("TRANS_WORD_SEQNO" 		,szTRANS_ORD_SEQNO);
							outRec.setField("CARD_NO"				,szCARD_NO);
							outRec.setField("CAR_NO"				,szCAR_NO);
							outRec.setField("WLOC_CD"				,szWLOC_CD);
							outRec.setField("YD_PNT_CD"				,szYD_PNT_CD);	
							outRec.setField("YD_CARPNT_CD"			,szYD_CARPNT_CD2);	
							outRec.setField("LOAN_PULLOUT_ABLE_YN","Y");
							ydUtils.displayRecord(szOperationName, outRec);
							ydDelegate.sendMsg(outRec);
						}
					}	
				}
			}
			
			szMsg = "[Jsp Session : "+szOperationName+"] --------------------- 메소드 끝 ---------------------";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		}catch(Exception ex) {
			szMsg = "[Jsp Session : "+szOperationName+"] 오류발생 : " + ex.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		}
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	/**
	 * 운송지시일자,순번,차량번호,카드번호 그룹핑
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getDmCarTrnsOrdDatSeqNoGroup(JDTORecord inDto) throws DAOException {
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		YdUtils ydUtils = new YdUtils();
		
		String szMsg        = "";		
		String szMethodName = "getDmCarTrnsOrdDatSeqNoGroup";
		String szOperationName = "운송지시일자,순번,차량번호,카드번호 그룹핑";
		
		
		String szYD_GP = null;
		
		YdStockDao ydStockDao = new YdStockDao();
		
		int intRtnVal = 0;
		
		try {		
			
			szMsg = "JSP-SESSION [운송지시일자,순번,차량번호,카드번호 그룹핑] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			
			szYD_GP = StringHelper.evl(inDto.getFieldString("YD_GP"), "");
			
			szMsg = "[JSP Session] " + szOperationName + " 파라미터 확인 : 야드구분[" + szYD_GP + "]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			if(szYD_GP.equals("S")){
//PIDEV_S :병행가동용:PI_YD
				inDto.setField("PI_YD",    	szYD_GP);	
				intRtnVal  = ydStockDao.getYdStock(inDto, outRecSet, 603);
			}else{
				intRtnVal  = ydStockDao.getYdStock(inDto, outRecSet, 127);
			}
			
			if (intRtnVal <= 0) {
				if (intRtnVal == -1) {
					szMsg = "[JSP Session] " + szOperationName + " 오류발생 1 : 반환값 - " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}else if (intRtnVal == 0) {
					szMsg = "[JSP Session] " + szOperationName + " 대상재가 존재하지 않음 : 반환값 - " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				} else {
					szMsg = "[JSP Session] " + szOperationName + " 오류발생 2 : 반환값 - " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if
			
			szMsg = "[JSP Session] " + szOperationName + " 성공 : 레코드 수 - " + intRtnVal;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			

			szMsg = "JSP-SESSION [운송지시일자,순번,차량번호,카드번호 그룹핑] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getDmCarTrnsOrdDatSeqNoGroup
	
	
	/**
	 * 운송지시일자,순번,차량번호,카드번호 그룹핑
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getDmCarTrnsOrdDatSeqNoGroup2(JDTORecord inDto) throws DAOException {
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		YdUtils ydUtils = new YdUtils();
		
		String szMsg        = "";		
		String szMethodName = "getDmCarTrnsOrdDatSeqNoGroup2";
		String szOperationName = "차량포인트";
		
		
		String szYD_GP = null;
		
		YdStockDao ydStockDao = new YdStockDao();
		
		int intRtnVal = 0;
		
		try {		
			
			szMsg = "JSP-SESSION [차량포인트] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			
			szYD_GP = StringHelper.evl(inDto.getFieldString("YD_BAY_GP"), "");
			
			szMsg = "[JSP Session] " + szOperationName + " 파라미터 확인 : 야드구분[" + szYD_GP + "]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			intRtnVal  = ydStockDao.getYdStock(inDto, outRecSet, 605);
			
			if (intRtnVal <= 0) {
				if (intRtnVal == -1) {
					szMsg = "[JSP Session] " + szOperationName + " 오류발생 1 : 반환값 - " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}else if (intRtnVal == 0) {
					szMsg = "[JSP Session] " + szOperationName + " 대상재가 존재하지 않음 : 반환값 - " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				} else {
					szMsg = "[JSP Session] " + szOperationName + " 오류발생 2 : 반환값 - " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if
			
			szMsg = "[JSP Session] " + szOperationName + " 성공 : 레코드 수 - " + intRtnVal;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			

			szMsg = "JSP-SESSION [차량포인트] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getDmCarTrnsOrdDatSeqNoGroup2
	
	/**
	 * 출하차량상차LOT조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData getDmCarLotList(GridData inDto) throws DAOException {
		GridData 		rtnGrd 		= new GridData();
		JDTORecordSet   outRecSet  	= null;
		
		JspCommonDAO 	dao 		= new JspCommonDAO();
		JDTORecord 		recPara		= null;
		
		try {
			
			// 파라미터 셋팅 
			recPara		= JDTORecordFactory.getInstance().create(); 			
			recPara.setField("V_YD_GP", 		inDto.getParam("YD_GP").trim());			/*야드구분*/
			recPara.setField("V_FORM_DATE", 	inDto.getParam("DATE_FROM").trim());		/*from_date*/
			recPara.setField("V_TO_DATE", 		inDto.getParam("DATE_TO").trim());		/*to_date*/
			recPara.setField("V_HISCO_GP", 		StringHelper.evl(inDto.getParam("HISCO_GP").trim(),"0"));		/*하이스코구분*/
			recPara.setField("V_YD_BAY_GP", 	inDto.getParam("YD_BAY_GP").trim());		/*동*/
			recPara.setField("V_CAR_NO", 		inDto.getParam("CAR_NO").trim());		/*차량번호*/
			recPara.setField("V_CARD_NO", 		inDto.getParam("CARD_NO").trim());		/*카드번호*/
			recPara.setField("V_CUST_CD", 		inDto.getParam("HISCO_CUST").trim());		/*하이스코*/
			recPara.setField("V_CURR_PROG_CD", 	inDto.getParam("YD_GNT_GP").trim());		/*상태*/
			
			
			// DAO 호출
			outRecSet = dao.getDmCarLotList(recPara);
			
			if(outRecSet != null || outRecSet.size() == 0){
				rtnGrd = CmUtil.genGridData(inDto, outRecSet);
			}else{
				rtnGrd.setMessage("조회된 데이터가 없습니다.");
				rtnGrd.addParam("ret", "-1");
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return rtnGrd;
	}//end of getDmCarLotList
	
	
	
	/**
	 * 출하차량도착처리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String procDmCarArr(JDTORecord inDto) throws DAOException {
		/*
		 * 업무기준 : 
		 * 수정자 : 임춘수
		 * 수정일 : 2009.09.11
		 */
		int       intRtnVal    = 0;
		JDTORecord       recPara         = null;
		JDTORecord       recTemp         = null;
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecordSet    outRecSetTemp       = null;
		String szMsg        = "";		
		String szMethodName = "procDmCarArr";
		String szOperationName = "출하차량도착처리";
		String szRtnMsg = YdConstant.RETN_CD_SUCCESS;
		String szCARD_NO = null;
		String szCAR_NO = null;
		String szTRANS_ORD_DATE = null;
		String szTRANS_ORD_SEQNO = null;
		String szTRANS_ORD_DATE_SEQNO = null;
		String szYD_STK_COL_GP = null;
		String szPREV_YD_STK_COL_GP = "";
		String szYD_GP = null;
		String szYD_BAY_GP = null;
		String szYD_EQP_GP = null;
		String szYD_CAR_STOP_LOC = null;
		String szYD_STK_COL_ACT_STAT = null;
		String szTRN_EQP_STK_CAPA = null;
		String szYD_SCH_CD = null;
		String szYD_SCH_CD_FOR_CRN = null;
		String szYD_WBOOK_ID = null;
		String szYD_WBOOK_ID_FOR_CRN = null;
		String szYD_CRN_ID = null;
		String szYD_SCH_PRIOR = null;
		String szYD_CAR_USE_GP = null;
		String szYD_AIM_YD_GP = null;
		String szYD_AIM_BAY_GP = null;
		String szSTL_NO = null;
		String[] szYD_CRN_TC_CODE = null;
		String szYD_EQP_ID = null;
		String szSPOS_WLOC_CD = null;
		String szIS_EJB_CALL = null;
		EJBConnector ejbConn = null;
		String szYD_CAR_SCH_ID 	= null;
		String szYD_PNT_CD1 = "";
		
		String szUser = null;
		Vector vGroup = new Vector();
		
		YdDelegate ydDelegate = new YdDelegate();
		
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		YdCarSchDao ydCarSchDao = new YdCarSchDao();
//		YdCarFtmvMtlDao ydCarFtmvMtlDao = new YdCarFtmvMtlDao();
		YdStkColDao ydStkColDao = new YdStkColDao();
		YdStockDao ydStockDao = new YdStockDao();
		
		try {
			
			szMsg = "JSP-SESSION [출하차량도착처리] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			szMsg = "[JSP Session] " + szOperationName + " - 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			/*
			 * 넘어온 파라미터 확인
			 */
			szCARD_NO = StringHelper.evl(inDto.getFieldString("CARD_NO"), "");
			szCAR_NO = StringHelper.evl(inDto.getFieldString("CAR_NO"), "");
			szTRANS_ORD_DATE_SEQNO = StringHelper.evl(inDto.getFieldString("TRANS_ORD_DATE_SEQNO"), "");
			String[] arrTemp = szTRANS_ORD_DATE_SEQNO.split("/");
			szTRANS_ORD_DATE = arrTemp[0];
			szTRANS_ORD_SEQNO = arrTemp[1];
			//szTRANS_ORD_DATE = StringHelper.evl(inDto.getFieldString("TRANS_ORD_DATE"), "");
			//szTRANS_ORD_SEQNO = StringHelper.evl(inDto.getFieldString("TRANS_ORD_SEQNO"), "");
			inDto.setField("TRANS_ORD_DATE", szTRANS_ORD_DATE);
			inDto.setField("TRANS_ORD_SEQNO", szTRANS_ORD_SEQNO);
			szYD_GP = StringHelper.evl(inDto.getFieldString("YD_GP"), "");
			szYD_BAY_GP = StringHelper.evl(inDto.getFieldString("YD_BAY_GP"), "_");
			szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP;
			szYD_CAR_STOP_LOC = StringHelper.evl(inDto.getFieldString("YD_CAR_STOP_LOC"), "");
			inDto.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			szUser = StringHelper.evl(inDto.getFieldString("YD_USER_ID"), "");
			szYD_CAR_USE_GP = YdConstant.YD_CAR_USE_GP_DM;
			
			szMsg = "[JSP Session] " + szOperationName + " 파라미터 확인 : 카드번호[" + szCARD_NO + "], 차량번호[" + szCAR_NO + "], 운송지시일자[" + szTRANS_ORD_DATE + "], 운송지시순번[" + szTRANS_ORD_SEQNO + "], 야드구분[" + szYD_GP + "], 동구분[" + szYD_BAY_GP + "]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//저장품 검색
			//조회조건 : CARD_NO, TRANS_ORD_DATE, TRANS_ORD_SEQNO, YD_STK_COL_GP(LIKE)
			if( szYD_GP.equals(YdConstant.YD_GP_PLATE_GDS_YARD)) {				//후판제품창고
				//적치열 Asc, 적치베드 Asc, 적치단 Desc
//PIDEV_S :병행가동용:PI_YD
				inDto.setField("PI_YD",    	szYD_GP);					
				intRtnVal = ydStockDao.getYdStock(inDto, outRecSet, 128);
			}else{
//PIDEV_S :병행가동용:PI_YD
				inDto.setField("PI_YD",    	szYD_GP);					
				//적치열 Desc, 적치베드 Asc, 적치단 Desc
				intRtnVal = ydStockDao.getYdStock(inDto, outRecSet, 126);
			}
		
			if (intRtnVal <= 0) {
				if (intRtnVal == -1) {
					szMsg = "[JSP Session] " + szOperationName + " 오류발생 1 : 반환값 - " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;
				}else if (intRtnVal == 0) {
					szMsg = "[JSP Session] " + szOperationName + " 대상재가 존재하지 않음 : 반환값 - " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					return YdConstant.RETN_CD_NOTEXIST;
				} else {
					szMsg = "[JSP Session] " + szOperationName + " 오류발생 2 : 반환값 - " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;
				}
				//return outRecSet;
				
			} // end of if
			
			szMsg = "[JSP Session] " + szOperationName + " 성공 : 레코드 수 - " + intRtnVal;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			/*
			 * 동별로 대상재를 그룹LOT편성
			 */
			szMsg = "[JSP Session] " + szOperationName + " - 동별로 그룹LOT편성 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			outRecSetTemp = JDTORecordFactory.getInstance().createRecordSet("");
			vGroup.add(outRecSetTemp);
			for(int i = 1; i <= outRecSet.size(); i++) {
				outRecSet.absolute(i);
				recPara = outRecSet.getRecord();
				
				szYD_STK_COL_GP = StringHelper.evl(recPara.getFieldString("YD_STK_COL_GP"), "");
				if( i == 1) {
					szTRN_EQP_STK_CAPA = StringHelper.evl(recPara.getFieldString("YD_MTL_WT_SUM"), "");
					szPREV_YD_STK_COL_GP = szYD_STK_COL_GP;
				}else{
					if( szYD_STK_COL_GP.substring(0, 2).equals(szPREV_YD_STK_COL_GP.substring(0, 2)) ) {
						szMsg = "[JSP Session] " + szOperationName + " - 같은 동이므로 같은 그룹으로 LOT편성";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}else{
						szMsg = "[JSP Session] " + szOperationName + " - 다른 동이므로 다른 그룹으로 LOT편성";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						outRecSetTemp = JDTORecordFactory.getInstance().createRecordSet("");
						vGroup.add(outRecSetTemp);
					}
				}
				outRecSetTemp.addRecord(recPara);
			}
			szMsg = "[JSP Session] " + szOperationName + " - 동별로 그룹LOT편성 끝 : 그룹 개수 --> " + vGroup.size();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			/*
			 * 동별로 그룹으로 편성된 LOT를 작업예약으로 등록
			 */
			for(int i = 0; i < vGroup.size(); i++ ) {
				//그룹을 하나 씩 추출
				outRecSetTemp = (JDTORecordSet)vGroup.get(i);
				for(int j = 1; j <= outRecSetTemp.size(); j++ ) {
					outRecSetTemp.absolute(j);
					recTemp = outRecSetTemp.getRecord();
					szYD_STK_COL_GP = StringHelper.evl(recTemp.getFieldString("YD_STK_COL_GP"), "");
					szSTL_NO = StringHelper.evl(recTemp.getFieldString("STL_NO"), "");
					if( j == 1 ) {
						//스케줄기준 체크
						
						szYD_GP = szYD_STK_COL_GP.substring(0, 1);
						szYD_BAY_GP = szYD_STK_COL_GP.substring(1, 2);
						if( szYD_GP.equals(YdConstant.YD_GP_PLATE_GDS_YARD)) {				//후판제품창고
							/*
							 * 후판제품창고는 대상재가04, 05, 06스판이면 A통로, 07스판이면 B통로
							 * 스케줄코드는 A통로이면 K_PT01UM, B통로이면 K_PT02UM으로 처리한다.
							 */
							szYD_EQP_GP = szYD_STK_COL_GP.substring(2, 4);
							if( szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_04) || 
								szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_05) || 
								szYD_EQP_GP.equals(YdConstant.SPAN_ORDER_NEW_06) ) {
								szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT01UM";
							}else{
								szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT02UM";
							} 
						}else{
							szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT01UM";
						}
						recPara = JDTORecordFactory.getInstance().create();
						intRtnVal = YdCommonUtils.getCrnInfoByCrnSchRule(szYD_SCH_CD, recPara);
						if( intRtnVal <= 0) {
							szMsg = "[JSP Session] " + szOperationName + " - 해당스케줄코드["+szYD_SCH_CD+"]로 작업예약을 등록할 수 있는 상태가 아니므로 해당 그룹은 등록처리를 하지 않습니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							break;
						}
						
						
						szYD_AIM_YD_GP  = StringHelper.evl(recTemp.getFieldString("YD_AIM_YD_GP"), "");
						szYD_AIM_BAY_GP  = StringHelper.evl(recTemp.getFieldString("YD_AIM_BAY_GP"), "");
						szYD_WBOOK_ID = ydWrkbookDao.getYdWrkbookId();
						szYD_SCH_PRIOR = StringHelper.evl(recPara.getFieldString("YD_SCH_PRIOR"), "");
						
						if( szYD_STK_COL_GP.substring(0, 2).equals(szYD_CAR_STOP_LOC.substring(0, 2))) {
							szYD_SCH_CD_FOR_CRN = szYD_SCH_CD;
							szYD_WBOOK_ID_FOR_CRN = szYD_WBOOK_ID;
							szYD_CRN_TC_CODE = YdCommonUtils.getCrnSchTCByYD(szYD_GP);
							szYD_CRN_ID = StringHelper.evl(recPara.getFieldString("YD_WRK_CRN"), "");
						}
						
						//작업예약 등록
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
						recPara.setField("YD_GP", 		  szYD_GP);
						recPara.setField("YD_BAY_GP", 	  szYD_BAY_GP);
						recPara.setField("YD_SCH_CD", 	  szYD_SCH_CD);
						recPara.setField("YD_SCH_PRIOR",  szYD_SCH_PRIOR);
						recPara.setField("REGISTER", 	  szUser);
						recPara.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP); 
						recPara.setField("CAR_NO",	      szCAR_NO);
						recPara.setField("CARD_NO", 	  szCARD_NO); 
						recPara.setField("YD_AIM_YD_GP",  szYD_AIM_YD_GP);  
						recPara.setField("YD_AIM_BAY_GP", szYD_AIM_BAY_GP);
						
						intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
						if(intRtnVal < 1){
							szMsg = "[JSP Session] " + szOperationName + " - 작업예약 등록 중 에러";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							//return YdConstant.RETN_CD_FAILURE;
							throw new DAOException(YdConstant.RETN_CD_FAILURE);
						}
					}
					//작업예약 재료 등록
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
					recPara.setField("REGISTER", 	  szUser);
					//재료번호
					recPara.setField("STL_NO"       , szSTL_NO);
					//적치열구분
					recPara.setField("YD_STK_COL_GP",  szYD_STK_COL_GP);
					//적치BED번호
					recPara.setField("YD_STK_BED_NO",  StringHelper.evl(recTemp.getFieldString("YD_STK_BED_NO"), ""));
					//적치단번호
					recPara.setField("YD_STK_LYR_NO",  StringHelper.evl(recTemp.getFieldString("YD_STK_LYR_NO"), ""));
					//권상모음순서
					recPara.setField("YD_UP_COLL_SEQ", "" + j);
					
					
					intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
					if(intRtnVal < 1){
						szMsg = "[JSP Session] " + szOperationName + " - 작업예약재료["+szSTL_NO+"] 등록 중 에러";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						// 예외를 발생시켜 롤백 시킴
						throw new DAOException(szSessionName + " : " + szMethodName + " - " + szMsg);
						// return;
					}
				}
			}
			
			/*
			 * 차량정지위치[적치열]상태 확인
			 */
			szMsg = "[JSP Session] " + szOperationName + " - 차량정지위치[" + szYD_CAR_STOP_LOC + "]를 활성처리 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			outRecSet       = JDTORecordFactory.getInstance().createRecordSet("stkcol");
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szYD_CAR_STOP_LOC);
			intRtnVal = ydStkColDao.getYdStkcol(recPara, outRecSet, 0);
			if( intRtnVal <= 0 ) {
				szMsg = "[JSP Session] " + szOperationName + " - 적치열(차량정지위치)["+szYD_CAR_STOP_LOC+"] 조회 시 오류발생  : 반환값 - " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				//return YdConstant.RETN_CD_NOTEXIST;
				throw new DAOException(YdConstant.RETN_CD_FAILURE);
			}
			outRecSet.first();
			recTemp = outRecSet.getRecord();
			szYD_STK_COL_ACT_STAT = StringHelper.evl(recTemp.getFieldString("YD_STK_COL_ACT_STAT"), "");
			szSPOS_WLOC_CD = StringHelper.evl(recTemp.getFieldString("WLOC_CD"), "");
			if( !szYD_STK_COL_ACT_STAT.equals(YdConstant.YD_STK_COL_INACTIVE)) {
				szMsg = "[JSP Session] " + szOperationName + " - 적치열(차량정지위치)["+szYD_CAR_STOP_LOC+"]가 활성화할 수 있는 상태["+szYD_STK_COL_ACT_STAT+"]가 아닙니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				//return YdConstant.RETN_CD_FAILURE;
				throw new DAOException(YdConstant.RETN_CD_FAILURE);
			}
			
			/*
			 * 차량스케줄이 존재하는 지 먼저 확인
			 */
			szMsg="[JSP Session] " + szOperationName + " - 차량스케줄[차량번호:" + szCAR_NO + ", 카드번호:" + szCARD_NO + "]이 존재하는 지를 조회 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			recPara  = JDTORecordFactory.getInstance().create();
			outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
			recPara.setField("CAR_NO" , szCAR_NO);
			recPara.setField("CARD_NO", szCARD_NO);
//PIDEV_S :병행가동용:PI_YD
			recPara.setField("PI_YD",    	szYD_GP);			
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, outRecSet, 11);
			
			if( intRtnVal == 0 ) {
				/*
				 * 차량스케줄 생성
				 */
				szMsg="[JSP Session] " + szOperationName + " - 차량스케줄[차량번호:" + szCAR_NO + ", 카드번호:" + szCARD_NO + "]이 존재하지 않으므로 차량스케줄을 새로 생성 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				szYD_EQP_ID = YdConstant.YD_DM_CAR_EQP_ID;
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("REGISTER",         szUser);
				//recPara.setField("DEL_YN",           "N");
				recPara.setField("YD_EQP_WRK_STAT",  "U");
				recPara.setField("YD_EQP_ID",        szYD_EQP_ID);
				recPara.setField("YD_PNT_CD1",        szYD_PNT_CD1);
				recPara.setField("YD_CAR_USE_GP",    szYD_CAR_USE_GP);
				recPara.setField("SPOS_WLOC_CD",     szSPOS_WLOC_CD);
		    	recPara.setField("YD_CARLD_WRK_BOOK_ID", szYD_WBOOK_ID);
				recPara.setField("CAR_NO",           szCAR_NO);
				recPara.setField("CARD_NO",          szCARD_NO);
				recPara.setField("YD_CARLD_LEV_DT", YdUtils.getCurDate("yyyyMMddHHmmss"));//상차출발일시
				recPara.setField("TRANS_ORD_DATE",           szTRANS_ORD_DATE);
				recPara.setField("TRANS_ORD_SEQNO",          szTRANS_ORD_SEQNO);
		    	//차량상차정지위치를 등록한다.
				recPara.setField("YD_CARLD_STOP_LOC", szYD_CAR_STOP_LOC);
				recPara.setField("YD_CARLD_ARR_DT", YdUtils.getCurDate("yyyyMMddHHmmss"));
				recPara.setField("YD_CAR_PROG_STAT", "2");								//상차도착상태
	    		
				if(szYD_CAR_USE_GP.equals("G")) {
					recPara.setField("CAR_KIND","TR");
				}else{
					recPara.setField("CAR_KIND","PT");
				}
				
	    		//차량스케줄 등록
		    	intRtnVal = ydCarSchDao.insYdCarsch(recPara);
	    		if( intRtnVal <= 0 ){
					szMsg="[JSP Session] " + szOperationName + " - 차량스케줄[차량번호:" + szCAR_NO + ", 카드번호:" + szCARD_NO + "] 생성 시 오류발생[반환값 : " + intRtnVal + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					throw new DAOException(YdConstant.RETN_CD_FAILURE);
	    		}
	    		
	    		szMsg="[JSP Session] " + szOperationName + " - 차량스케줄[차량번호:" + szCAR_NO + ", 카드번호:" + szCARD_NO + "] 생성 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}else if( intRtnVal == 1 ){
				/*
				 * 차량스케줄 수정 - 상차도착
				 */
				outRecSet.first();
				recTemp = outRecSet.getRecord();
				szYD_CAR_SCH_ID = StringHelper.evl(recTemp.getFieldString("YD_CAR_SCH_ID"), "");
				szMsg="[JSP Session] " + szOperationName + " - 차량스케줄[" + szYD_CAR_SCH_ID + "] 변경 시작 - 상차도착 처리";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CAR_SCH_ID",    szYD_CAR_SCH_ID);
				recPara.setField("MODIFIER",         szUser);
				recPara.setField("YD_CARLD_WRK_BOOK_ID", szYD_WBOOK_ID);
				recPara.setField("YD_CARLD_STOP_LOC", szYD_CAR_STOP_LOC);
				recPara.setField("YD_CARLD_ARR_DT", YdUtils.getCurDate("yyyyMMddHHmmss"));
				recPara.setField("YD_CAR_PROG_STAT", "2");								//상차도착상태
				//차량스케줄 변경
		    	intRtnVal = ydCarSchDao.updYdCarsch(recPara, 0);
	    		if( intRtnVal <= 0 ){
					szMsg="[JSP Session] " + szOperationName + " - 차량스케줄[" + szYD_CAR_SCH_ID + "] 변경 시 오류발생[반환값 : " + intRtnVal + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					throw new DAOException(YdConstant.RETN_CD_FAILURE);
	    		}
				
				szMsg="[JSP Session] " + szOperationName + " - 차량스케줄[" + szYD_CAR_SCH_ID + "] 변경 완료 - 상차도착 처리";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}else{
				szMsg="[JSP Session] " + szOperationName + " - 차량스케줄[차량번호:" + szCAR_NO + ", 카드번호:" + szCARD_NO + "]이 존재하는 지를 조회 시 오류발생 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new DAOException(YdConstant.RETN_CD_FAILURE);
			}
			
			
			
			/*
			 * 차량정지위치를 활성처리
			 */
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szYD_CAR_STOP_LOC);
			recPara.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
			recPara.setField("CAR_NO", szCAR_NO);
			recPara.setField("CARD_NO", szCARD_NO);
			recPara.setField("YD_STK_COL_ACT_STAT", YdConstant.YD_STK_COL_ACTIVE);
			recPara.setField("TRN_EQP_STK_CAPA", szTRN_EQP_STK_CAPA);
			
			String szRtnCd = YdCommonUtils.procCarPosActiveOrInActive(recPara);
			
			if( !szRtnCd.equals(YdConstant.RETN_CD_SUCCESS)) {
				//return YdConstant.RETN_CD_FAILURE;
				throw new DAOException(YdConstant.RETN_CD_FAILURE);
			}
			

			
			/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    		 * 업무기준 : 출하 공차 도착 시 저장위치 제원 야드L2로 전송
    		 * 수정자 : 임춘수
    		 * 수정일자 : 2009.09.14
    		 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
			szMsg="["+szOperationName+"] 저장위치 제원 야드L2로 전송 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_INFO_SYNC_CD", "3");							//1:동,2:SPAN,3:열,4:BED
			recPara.setField("YD_GP", szYD_CAR_STOP_LOC.substring(0, 1));
			recPara.setField("YD_STK_COL_GP", szYD_CAR_STOP_LOC);
			//recPara.setField("YD_STK_BED_NO", szYD_CARLD_STOP_LOC);
			recPara.setField("YD_CAR_PROG_STAT", "2");
			recPara.setField("YD_EQP_WRK_STAT", "U");
			
			YdCommonUtils.sndStrPosSpecToL2(recPara);
			
			szMsg="["+szOperationName+"] 저장위치 제원 야드L2로 전송 완료";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			
			/*
			 * 크레인스케줄메인 호출처리
			 */
			szMsg = "[JSP Session] " + szOperationName + " - 크레인스케줄메인["+szYD_CRN_TC_CODE+"] 호출처리 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("JMS_TC_CD", szYD_CRN_TC_CODE[0]);
			//스케줄코드
			recPara.setField("YD_SCH_CD", szYD_SCH_CD_FOR_CRN);
    		//설비ID
			recPara.setField("YD_EQP_ID", szYD_CRN_ID);
    		recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID_FOR_CRN);
    		ydUtils.displayRecord(szOperationName, recPara);

    		
//sjh
   
//			
    		szIS_EJB_CALL = "N";
    		
    		if( szIS_EJB_CALL.equals("Y") ) {
    			szMsg = "[JSP Session] " + szOperationName + " - 크레인스케줄메인["+szYD_CRN_TC_CODE+"] EJB CALL 시작";
    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
				ejbConn.trx("CrnSchSeEJB", szYD_CRN_TC_CODE[1], recPara);
    		}else{
    			szMsg = "[JSP Session] " + szOperationName + " - 크레인스케줄메인["+szYD_CRN_TC_CODE+"] 전문 송신";
    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    			ydDelegate.sendMsg(recPara);
    		}
    		szMsg = "[JSP Session] " + szOperationName + " - 크레인스케줄메인["+szYD_CRN_TC_CODE+"] 호출처리 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
			
			szMsg = "[JSP Session] " + szOperationName + "  끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		}catch( DAOException e ) {
			szMsg = "[JSP Session] " + szOperationName + " 예외발생1 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw e;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			szMsg = "[JSP Session] " + szOperationName + " 예외발생2 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		szMsg = "JSP-SESSION [출하차량도착처리] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return szRtnMsg;
	}//end of procDmCarArr
	
	
	/**
	 * 출하차량도착처리(통합슬라브 외판처리)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String procDmCarArrS(JDTORecord inDto) throws DAOException {
		/*
		 * 업무기준 : 
		 * 수정자 : 임춘수
		 * 수정일 : 2009.09.11
		 */
		int       intRtnVal    = 0;
		JDTORecord       recPara         = null;
		JDTORecord       recTemp         = null;
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecordSet    outRecSetTemp       = null;
		String szMsg        = "";		
		String szMethodName = "procDmCarArrS";
		String szOperationName = "출하차량도착처리";
		String szRtnMsg = YdConstant.RETN_CD_SUCCESS;
		String szCARD_NO = null;
		String szCAR_NO = null;
		String szTRANS_ORD_DATE = null;
		String szTRANS_ORD_SEQNO = null;
		String szTRANS_ORD_DATE_SEQNO = null;
		String szYD_STK_COL_GP = null;
		String szPREV_YD_STK_COL_GP = "";
		String szYD_GP = null;
		String szYD_BAY_GP = null;
		String szYD_EQP_GP = null;
		String szYD_CAR_STOP_LOC = null;
		String szYD_STK_COL_ACT_STAT = null;
		String szTRN_EQP_STK_CAPA = null;
		String szYD_SCH_CD = null;
		String szYD_SCH_CD_FOR_CRN = null;
		String szYD_WBOOK_ID = null;
		String szYD_WBOOK_ID_FOR_CRN = null;
		String szYD_CRN_ID = null;
		String szYD_SCH_PRIOR = null;
		String szYD_CAR_USE_GP = null;
		String szYD_AIM_YD_GP = null;
		String szYD_AIM_BAY_GP = null;
		String szSTL_NO = null;
		String[] szYD_CRN_TC_CODE = null;
		String szYD_EQP_ID = null;
		String szSPOS_WLOC_CD = null;
		String szIS_EJB_CALL = null;
		EJBConnector ejbConn = null;
		String szYD_CAR_SCH_ID 	= null;
		String szYD_PNT_CD1 = "";
		
		String szUser = null;
		Vector vGroup = new Vector();
		
		YdDelegate ydDelegate = new YdDelegate();
		
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		YdCarSchDao ydCarSchDao = new YdCarSchDao();
//		YdCarFtmvMtlDao ydCarFtmvMtlDao = new YdCarFtmvMtlDao();
		YdStkColDao ydStkColDao = new YdStkColDao();
		YdStockDao ydStockDao = new YdStockDao();
		
		try {
			
			szMsg = "JSP-SESSION [출하차량도착처리] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			szMsg = "[JSP Session] " + szOperationName + " - 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			/*
			 * 넘어온 파라미터 확인
			 */
			szCARD_NO = StringHelper.evl(inDto.getFieldString("CARD_NO"), "");
			szCAR_NO = StringHelper.evl(inDto.getFieldString("CAR_NO"), "");
			szTRANS_ORD_DATE_SEQNO = StringHelper.evl(inDto.getFieldString("TRANS_ORD_DATE_SEQNO"), "");
			String[] arrTemp = szTRANS_ORD_DATE_SEQNO.split("/");
			szTRANS_ORD_DATE = arrTemp[0];
			szTRANS_ORD_SEQNO = arrTemp[1];
			//szTRANS_ORD_DATE = StringHelper.evl(inDto.getFieldString("TRANS_ORD_DATE"), "");
			//szTRANS_ORD_SEQNO = StringHelper.evl(inDto.getFieldString("TRANS_ORD_SEQNO"), "");
			inDto.setField("TRANS_ORD_DATE", szTRANS_ORD_DATE);
			inDto.setField("TRANS_ORD_SEQNO", szTRANS_ORD_SEQNO);
			szYD_GP = StringHelper.evl(inDto.getFieldString("YD_GP"), "");
			szYD_BAY_GP = StringHelper.evl(inDto.getFieldString("YD_BAY_GP"), "_");
			szYD_STK_COL_GP = szYD_GP + szYD_BAY_GP;
			szYD_CAR_STOP_LOC = StringHelper.evl(inDto.getFieldString("YD_CAR_STOP_LOC"), "");
			inDto.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			szUser = StringHelper.evl(inDto.getFieldString("YD_USER_ID"), "");
			szYD_CAR_USE_GP = YdConstant.YD_CAR_USE_GP_DM;
			
			szMsg = "[JSP Session] " + szOperationName + " 파라미터 확인 : 카드번호[" + szCARD_NO + "], 차량번호[" + szCAR_NO + "], 운송지시일자[" + szTRANS_ORD_DATE + "], 운송지시순번[" + szTRANS_ORD_SEQNO + "], 야드구분[" + szYD_GP + "], 동구분[" + szYD_BAY_GP + "]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//저장품 검색
			//조회조건 : CARD_NO, TRANS_ORD_DATE, TRANS_ORD_SEQNO, YD_STK_COL_GP(LIKE)
			if( szYD_GP.equals(YdConstant.YD_GP_PLATE_GDS_YARD)) {				//후판제품창고
				//적치열 Asc, 적치베드 Asc, 적치단 Desc
//PIDEV_S :병행가동용:PI_YD
				inDto.setField("PI_YD",    	szYD_GP);						
				intRtnVal = ydStockDao.getYdStock(inDto, outRecSet, 128);
			}else{
//PIDEV_S :병행가동용:PI_YD
				inDto.setField("PI_YD",    	szYD_GP);					
				//적치열 Desc, 적치베드 Asc, 적치단 Desc
				intRtnVal = ydStockDao.getYdStock(inDto, outRecSet, 126);
			}
		
			if (intRtnVal <= 0) {
				if (intRtnVal == -1) {
					szMsg = "[JSP Session] " + szOperationName + " 오류발생 1 : 반환값 - " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;
				}else if (intRtnVal == 0) {
					szMsg = "[JSP Session] " + szOperationName + " 대상재가 존재하지 않음 : 반환값 - " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					return YdConstant.RETN_CD_NOTEXIST;
				} else {
					szMsg = "[JSP Session] " + szOperationName + " 오류발생 2 : 반환값 - " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;
				}
				//return outRecSet;
				
			} // end of if
			 
			/*
			 * 차량정지위치[적치열]상태 확인
			 */
			szMsg = "[JSP Session] " + szOperationName + " - 차량정지위치[" + szYD_CAR_STOP_LOC + "]를 활성처리 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			outRecSet       = JDTORecordFactory.getInstance().createRecordSet("stkcol");
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szYD_CAR_STOP_LOC);
			intRtnVal = ydStkColDao.getYdStkcol(recPara, outRecSet, 0);
			if( intRtnVal <= 0 ) {
				szMsg = "[JSP Session] " + szOperationName + " - 적치열(차량정지위치)["+szYD_CAR_STOP_LOC+"] 조회 시 오류발생  : 반환값 - " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				//return YdConstant.RETN_CD_NOTEXIST;
				throw new DAOException(YdConstant.RETN_CD_FAILURE);
			}
			outRecSet.first();
			recTemp = outRecSet.getRecord();
			szYD_STK_COL_ACT_STAT = StringHelper.evl(recTemp.getFieldString("YD_STK_COL_ACT_STAT"), "");
			szYD_PNT_CD1 = StringHelper.evl(recTemp.getFieldString("YD_PNT_CD"), "");
			if( !szYD_STK_COL_ACT_STAT.equals(YdConstant.YD_STK_COL_INACTIVE)) {
				szMsg = "[JSP Session] " + szOperationName + " - 적치열(차량정지위치)["+szYD_CAR_STOP_LOC+"]가 활성화할 수 있는 상태["+szYD_STK_COL_ACT_STAT+"]가 아닙니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				//return YdConstant.RETN_CD_FAILURE;
				throw new DAOException(YdConstant.RETN_CD_FAILURE);
			}
			
			/*
			 * 차량스케줄이 존재하는 지 먼저 확인
			 */
			szMsg="[JSP Session] " + szOperationName + " - 차량스케줄[차량번호:" + szCAR_NO + ", 카드번호:" + szCARD_NO + "]이 존재하는 지를 조회 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			recPara  = JDTORecordFactory.getInstance().create();
			outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
			recPara.setField("CAR_NO" , szCAR_NO);
			recPara.setField("CARD_NO", szCARD_NO);
			recPara.setField("TRANS_ORD_DATE", szTRANS_ORD_DATE);
			recPara.setField("TRANS_ORD_SEQNO", szTRANS_ORD_SEQNO);
//PIDEV_S :병행가동용:PI_YD
			recPara.setField("PI_YD",    	szYD_GP);							
			/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschCarNoCardNoTransNo_PIDEV*/
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, outRecSet, 431);
			
			if( intRtnVal == 0 ) {
				/*
				 * 차량스케줄 생성
				 */
				szMsg="[JSP Session] " + szOperationName + " - 차량스케줄[차량번호:" + szCAR_NO + ", 카드번호:" + szCARD_NO + "]이 존재하지 않으므로 차량스케줄을 새로 생성 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				szYD_EQP_ID = YdConstant.YD_DM_CAR_EQP_ID;
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("REGISTER",         szUser);
				//recPara.setField("DEL_YN",           "N");
				recPara.setField("YD_EQP_WRK_STAT",  "U");
				recPara.setField("YD_EQP_ID",        szYD_EQP_ID);
				recPara.setField("YD_PNT_CD1",        szYD_PNT_CD1);
				recPara.setField("YD_CAR_USE_GP",    szYD_CAR_USE_GP);
				recPara.setField("SPOS_WLOC_CD",     "DJY25"); //(비상야드추가)
		    	recPara.setField("YD_CARLD_WRK_BOOK_ID", szYD_WBOOK_ID);
				recPara.setField("CAR_NO",           szCAR_NO);
				recPara.setField("CARD_NO",          szCARD_NO);
				recPara.setField("YD_CARLD_LEV_DT", YdUtils.getCurDate("yyyyMMddHHmmss"));//상차출발일시
				recPara.setField("TRANS_ORD_DATE",           szTRANS_ORD_DATE);
				recPara.setField("TRANS_ORD_SEQNO",          szTRANS_ORD_SEQNO);
		    	//차량상차정지위치를 등록한다.
				recPara.setField("YD_CARLD_STOP_LOC", szYD_CAR_STOP_LOC);
				recPara.setField("YD_CARLD_ARR_DT", YdUtils.getCurDate("yyyyMMddHHmmss"));
				recPara.setField("YD_CAR_PROG_STAT", "2");								//상차도착상태
	    		
				if(szYD_CAR_USE_GP.equals("G")) {
					recPara.setField("CAR_KIND","TR");
				}else{
					recPara.setField("CAR_KIND","PT");
				}
				
	    		//차량스케줄 등록
		    	intRtnVal = ydCarSchDao.insYdCarsch(recPara);
	    		if( intRtnVal <= 0 ){
					szMsg="[JSP Session] " + szOperationName + " - 차량스케줄[차량번호:" + szCAR_NO + ", 카드번호:" + szCARD_NO + "] 생성 시 오류발생[반환값 : " + intRtnVal + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					throw new DAOException(YdConstant.RETN_CD_FAILURE);
	    		}
	    		
	    		szMsg="[JSP Session] " + szOperationName + " - 차량스케줄[차량번호:" + szCAR_NO + ", 카드번호:" + szCARD_NO + "] 생성 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}else if( intRtnVal == 1 ){
				/*
				 * 차량스케줄 수정 - 상차도착
				 */
				outRecSet.first();
				recTemp = outRecSet.getRecord();
				szYD_CAR_SCH_ID = StringHelper.evl(recTemp.getFieldString("YD_CAR_SCH_ID"), "");
				szMsg="[JSP Session] " + szOperationName + " - 차량스케줄[" + szYD_CAR_SCH_ID + "] 변경 시작 - 상차도착 처리";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CAR_SCH_ID",    szYD_CAR_SCH_ID);
				recPara.setField("MODIFIER",         szUser);
				recPara.setField("YD_CARLD_WRK_BOOK_ID", szYD_WBOOK_ID);
				recPara.setField("YD_CARLD_STOP_LOC", szYD_CAR_STOP_LOC);
				recPara.setField("YD_CARLD_ARR_DT", YdUtils.getCurDate("yyyyMMddHHmmss"));
				recPara.setField("TRANS_ORD_DATE",           szTRANS_ORD_DATE);
				recPara.setField("TRANS_ORD_SEQNO",          szTRANS_ORD_SEQNO);
				recPara.setField("YD_CAR_PROG_STAT", "2");								//상차도착상태
				//차량스케줄 변경
		    	intRtnVal = ydCarSchDao.updYdCarsch(recPara, 0);
	    		if( intRtnVal <= 0 ){
					szMsg="[JSP Session] " + szOperationName + " - 차량스케줄[" + szYD_CAR_SCH_ID + "] 변경 시 오류발생[반환값 : " + intRtnVal + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					throw new DAOException(YdConstant.RETN_CD_FAILURE);
	    		}
				
				szMsg="[JSP Session] " + szOperationName + " - 차량스케줄[" + szYD_CAR_SCH_ID + "] 변경 완료 - 상차도착 처리";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}else{
				szMsg="[JSP Session] " + szOperationName + " - 차량스케줄[차량번호:" + szCAR_NO + ", 카드번호:" + szCARD_NO + "]이 존재하는 지를 조회 시 오류발생 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new DAOException(YdConstant.RETN_CD_FAILURE);
			}
			
			
			
			/*
			 * 차량정지위치를 활성처리
			 */
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szYD_CAR_STOP_LOC);
			recPara.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
			recPara.setField("CAR_NO", szCAR_NO);
			recPara.setField("CARD_NO", szCARD_NO);
			recPara.setField("YD_STK_COL_ACT_STAT", YdConstant.YD_STK_COL_ACTIVE);
			recPara.setField("TRN_EQP_STK_CAPA", szTRN_EQP_STK_CAPA);
			
			String szRtnCd = YdCommonUtils.procCarPosActiveOrInActive(recPara);
			
			if( !szRtnCd.equals(YdConstant.RETN_CD_SUCCESS)) {
				//return YdConstant.RETN_CD_FAILURE;
				throw new DAOException(YdConstant.RETN_CD_FAILURE);
			}
			
//			/*
//			 * 크레인스케줄메인 호출처리
//			 */
//			szMsg = "[JSP Session] " + szOperationName + " - 크레인스케줄메인["+szYD_CRN_TC_CODE+"] 호출처리 시작";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//			recPara = JDTORecordFactory.getInstance().create();
//			recPara.setField("JMS_TC_CD", szYD_CRN_TC_CODE[0]);
//			//스케줄코드
//			recPara.setField("YD_SCH_CD", szYD_SCH_CD_FOR_CRN);
//    		//설비ID
//			recPara.setField("YD_EQP_ID", szYD_CRN_ID);
//    		recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID_FOR_CRN);
//    		ydUtils.displayRecord(szOperationName, recPara);
//    		
//    		szIS_EJB_CALL = "Y";
//    		
//    		if( szIS_EJB_CALL.equals("Y") ) {
//    			szMsg = "[JSP Session] " + szOperationName + " - 크레인스케줄메인["+szYD_CRN_TC_CODE+"] EJB CALL 시작";
//    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//    			ejbConn = new EJBConnector("default", "YdJspCommonSeEJB", this);
//				ejbConn.trx("CrnSchSeEJB", szYD_CRN_TC_CODE[1], recPara);
//    		}else{
//    			szMsg = "[JSP Session] " + szOperationName + " - 크레인스케줄메인["+szYD_CRN_TC_CODE+"] 전문 송신";
//    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//    			ydDelegate.sendMsg(recPara);
//    		}
//    		szMsg = "[JSP Session] " + szOperationName + " - 크레인스케줄메인["+szYD_CRN_TC_CODE+"] 호출처리 끝";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//			
//			/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//    		 * 업무기준 : 출하 공차 도착 시 저장위치 제원 야드L2로 전송
//    		 * 수정자 : 임춘수
//    		 * 수정일자 : 2009.09.14
//    		 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
//			szMsg="["+szOperationName+"] 저장위치 제원 야드L2로 전송 시작";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//			recPara = JDTORecordFactory.getInstance().create();
//			recPara.setField("YD_INFO_SYNC_CD", "3");							//1:동,2:SPAN,3:열,4:BED
//			recPara.setField("YD_GP", szYD_CAR_STOP_LOC.substring(0, 1));
//			recPara.setField("YD_STK_COL_GP", szYD_CAR_STOP_LOC);
//			//recPara.setField("YD_STK_BED_NO", szYD_CARLD_STOP_LOC);
//			recPara.setField("YD_CAR_PROG_STAT", "2");
//			recPara.setField("YD_EQP_WRK_STAT", "U");
//			
//			YdCommonUtils.sndStrPosSpecToL2(recPara);
//			
//			szMsg="["+szOperationName+"] 저장위치 제원 야드L2로 전송 완료";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//			
//			/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
			
			szMsg = "[JSP Session] " + szOperationName + "  끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch( DAOException e ) {
			szMsg = "[JSP Session] " + szOperationName + " 예외발생1 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw e;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			szMsg = "[JSP Session] " + szOperationName + " 예외발생2 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		szMsg = "JSP-SESSION [출하차량도착처리] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return szRtnMsg;
	}//end of procDmCarArrS
	
	/**
	 * 재료이력정보 조회
	 * @param xlib.cmc.GridDat
	 * @return xlib.cmc.GridData
	 * @throws com.inisteel.cim.common.exception.DAOException
	 * @ejb.interface-method
	 * 
	 */
	 
   public JDTORecordSet getYdWrkHistDaoStlNo(JDTORecord inDto) throws DAOException {
	    JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		
	    
		
		String szMsg        = "";		
    	String szMethodName = "getYdWrkHistDaoStlNo";
    	YdWrkHistDao ydWrkHistDao = new YdWrkHistDao();
    
		
		int intRtnVal = 0;
		
		try {
			
			szMsg = "JSP-SESSION [재료이력정보 조회] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
			recPara.setField("STL_NO",	inDto.getField("V_SLAB_NO"));		
			
			intRtnVal = ydWrkHistDao.getYdWrkHist(recPara, outRecSet, 9);
			
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
		
		szMsg = "JSP-SESSION [재료이력정보 조회] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return outRecSet;
   }

   /**
    * 적치열의 동정보 or 스판정보 or 열정보를 조회하는 메소드
    * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
    * @param inDto
    * @return
    * @throws DAOException
    */
   public JDTORecordSet getCodeForStkCol(JDTORecord inDto) throws DAOException {
	   /*
	    * 업무기준 : 1. 동조회이면 START_POS = 1
	    * 			2. 스판조회이면 START_POS = 3
	    * 			3. 열조회이면 START_POS = 5
	    * 			4. 반환되는 레코드셋은 CODE[동 OR 스판 OR 열]항목만 가지고 있음
	    */
	    JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		
		YdStkColDao ydStkColDao			= new YdStkColDao();
		String szOperationName	= "동정보/스판정보/열조회";
		String szMethodName		= "getCodeForStkCol";
		String szMsg       	 	= "";		
		
		String szCOL_SEARCH_GP	= null;
		String szSTART_POS		= null;
		
		int intRtnVal = 0;
		
		try {
			
			szMsg = "[JSP Session : " + szOperationName + "] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szCOL_SEARCH_GP = ydDaoUtils.paraRecChkNull(inDto,"COL_SEARCH_GP");
			
			if( szCOL_SEARCH_GP.equals("D")) {				//동정보
				szSTART_POS = "1";
			}else if( szCOL_SEARCH_GP.equals("S")) {		//스판정보
				szSTART_POS = "3";
			}else if( szCOL_SEARCH_GP.equals("C")) {		//열정보
				szSTART_POS = "5";
			}
			
			recPara.setField("START_POS",		szSTART_POS);
			recPara.setField("YD_STK_COL_GP",	inDto.getField("YD_STK_COL_GP"));		
			
			intRtnVal = ydStkColDao.getYdStkcol(recPara, outRecSet, 20);
			
			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "[JSP Session : " + szOperationName + "] 오류발생[1] - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "[JSP Session : " + szOperationName + "] parameter error - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if
			
			szMsg = "[JSP Session : " + szOperationName + "] 메소드 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
  }
   
   
   /**
    * 적치열의 동정보 or 스판정보 or 열정보를 조회하는 메소드
    * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
    * @param inDto
    * @return
    * @throws DAOException
    */
   public JDTORecordSet getCodeForStkCol2(JDTORecord inDto) throws DAOException {
	   /*
	    * 업무기준 : 1. 동조회이면 START_POS = 1
	    * 			2. 스판조회이면 START_POS = 3
	    * 			3. 열조회이면 START_POS = 5
	    * 			4. 반환되는 레코드셋은 CODE[동 OR 스판 OR 열]항목만 가지고 있음
	    */
	    JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		
		YdStkColDao ydStkColDao			= new YdStkColDao();
		String szOperationName	= "동정보/스판정보/열조회";
		String szMethodName		= "getCodeForStkCol2";
		String szMsg       	 	= "";		
		
		String szCOL_SEARCH_GP	= null;
		String szSTART_POS		= null;
		
		int intRtnVal = 0;
		
		try {
			
			szMsg = "[JSP Session : " + szOperationName + "] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szCOL_SEARCH_GP = ydDaoUtils.paraRecChkNull(inDto,"COL_SEARCH_GP");
			
			if( szCOL_SEARCH_GP.equals("D")) {				//동정보
				szSTART_POS = "1";
			}else if( szCOL_SEARCH_GP.equals("S")) {		//스판정보
				szSTART_POS = "3";
			}else if( szCOL_SEARCH_GP.equals("C")) {		//열정보
				szSTART_POS = "5";
			}
			
			recPara.setField("START_POS",		szSTART_POS);
			recPara.setField("YD_STK_COL_GP",	inDto.getField("YD_STK_COL_GP"));		
			
			/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcolForCode2*/
			intRtnVal = ydStkColDao.getYdStkcol(recPara, outRecSet, 402);
			
			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "[JSP Session : " + szOperationName + "] 오류발생[1] - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "[JSP Session : " + szOperationName + "] parameter error - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if
			
			szMsg = "[JSP Session : " + szOperationName + "] 메소드 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
  }
   
   /**
    * 완료 처리(작업완료처리)
    * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
    * @param inDto
    * @return
    * @throws DAOException
    */
   public String procAllWrkCmp(JDTORecord inDto) throws DAOException {
	   /*
	    * 업무기준 :
	    * 
	    * 
	    * 
	    * 
	    * # 야드 차량진행상태 
				- 1: 상차출발
				- 2: 상차도착
				- 3: 상차검수
			 	- 4: 상차개시
			 	- 5: 상차완료
 
		1. 상차완료
			- 차량 정보로 차량스케줄 정보를 얻는다
			- 차량 스케줄이 1,2 ,4 인경우
				- 차량 스케줄에서 상차 작업 예약 ID 정보를 읽는다
				- 상차 작업예약 ID 정보로 작업예약 정보를 얻어온다.
				- 작업예약에 해당되는 크레인 스케줄 정보 (1,W) 를 스케줄 번호가 작은 순부터 읽어온다.
				- 얻어온 스케줄 정보에 대해서 권상 , 권하 처리 Backup 처리를 한다.
				
			- 차량 스케줄 상태가 1,2, 4 가 아닌 경우는 처리 할수 없다.
			 
	    * 
	    */
	    JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
	    JDTORecord       recCarSch       = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecord []    recArr          = new JDTORecord[1];
		
		String szOperationName	= "작업완료처리";
		String szMethodName		= "procAllWrkCmp";
		String szMsg       	 	= "";
		String szSubRtnMsg      = "";
		
		
		String szWbookId        = "";
		
		int intRtnVal = 0;
		
		
		
		YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
		
		
		try {
			
			szMsg = "[JSP Session : " + szOperationName + "] 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			
			
			szWbookId = ydDaoUtils.paraRecChkNull(inDto, "YD_WBOOK_ID"); 
			
			
			if (szWbookId.equals("")){
				
				szMsg =  "작업예약 정보가 존재하지 않습니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return szMsg;
				
			}
						
			
			// 작업예약 ID 정보를 가지고 있고 삭제되지 않은 크레인
			// 스케줄 정보를 가지고 온다.(스케줄이 작은 순서부터 큰순으로)
			//com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschByWrkId (크레인스케줄, IF 에서도 사용중인 쿼리)			
			
			 outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			 recPara         = JDTORecordFactory.getInstance().create();
			 recPara.setField("YD_WBOOK_ID", szWbookId);
			 
			 intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, outRecSet, 28);
			
			 if(intRtnVal <= 0){
					szMsg =  "["+ szWbookId +"] 작업예약 정보는   크레인 스케줄 정보를 가지고있지 않습니다";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return szMsg;
			 
			 }
			 
			 
			 for(int nLoop = 0 ; nLoop < outRecSet.size();nLoop++){
				 
				 recPara   = outRecSet.getRecord(nLoop);
				 recArr[0] = recPara;
				 
				 
				 //권상실적 처리 
				 
				 //updCrnUpPrsBackUp
				 
				 
				 
				 szSubRtnMsg = this.updCrnUpPrsBackUp(recArr);				 
				 
				 ydUtils.putLog(szSessionName, szMethodName, szSubRtnMsg, YdConstant.DEBUG);
				
				 
				 
				 //권하실적처리
				 //updCrnDnPrsBackUp
				 
				 ydUtils.displayRecord(szOperationName, recPara);
				 
				 recPara.setField("YD_DN_WR_LOC",    ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WO_LOC"));  
			     recPara.setField("YD_DN_WR_LAYER",  ydDaoUtils.paraRecChkNull(recPara, "YD_DN_WO_LAYER"));  
					
					
					
				 
				 szSubRtnMsg = this.updCrnDnPrsBackUp(recArr);
				 ydUtils.putLog(szSessionName, szMethodName, szSubRtnMsg, YdConstant.DEBUG);
				 
			 }
			
			
			
			szMsg = "[JSP Session : " + szOperationName + "] 메소드 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			
			return szSubRtnMsg;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
  }
   
   
   
   
   
   
   
   
   
   /**
	 *  상차위치 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String carLiftPosSet(JDTORecord[] inDto) throws DAOException {
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JDTORecord recInputPara = null;
		JDTORecord recInputPara2 = null;
		JDTORecord recInputPara3 = null;
		

		int nRtnVal  = 0;
		String szMethodName       = "carLiftPosSet";
		String szLogMsg           = "";
		
		String szRtnMsg = "";
			
		String lsz_StlNo = "";
		String lsz_YdStkBedNo = "";
		String lsz_YdStkLyrNo = "";
		String lsz_YdCarSchId = "";
		String szOperationName = "상차위치 수정";
		String lsz_YdCarstopLoc = "";
	
	
		
		YdCarFtmvMtlDao ydCarFtmvMtlDao  = new YdCarFtmvMtlDao();
		
		try{
			
			szLogMsg = "JSP-SESSION [상차위치 수정] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			for(int x=0;x<inDto.length;x++){
				
				
				recPara = inDto[x];

			//재료번호
				lsz_StlNo =    ydDaoUtils.paraRecChkNull(recPara, "STL_NO");

			//차량스케줄 
				lsz_YdCarSchId = ydDaoUtils.paraRecChkNull(recPara, "YD_CAR_SCH_ID");
			  
			//단
				lsz_YdStkBedNo =  ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
			
			//차상위치 
				lsz_YdStkLyrNo =  ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO");
				
			//차량정지위치	
				lsz_YdCarstopLoc =  ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_STOP_LOC");
				
				
				
				if(lsz_YdCarSchId.equals("")){
					szRtnMsg = "차량 스케줄 재료정보가 존재하지 않습니다";
					ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
					return szRtnMsg;
					
				}
		
				
			
			//단 / 저장위치 정보를 Check 한다.
				
				if(lsz_YdStkBedNo.equals("") || lsz_YdStkLyrNo.equals("")){
					szRtnMsg = "단 / 저장위치 정보가 올바르지 않습니다";
					ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
					return szRtnMsg;
					
				}
				recInputPara =  JDTORecordFactory.getInstance().create();
				
				recInputPara.setField("YD_CAR_SCH_ID", lsz_YdCarSchId);
				recInputPara.setField("STL_NO", lsz_StlNo);
				recInputPara.setField("YD_STK_BED_NO",  ydDaoUtils.stringPlusInt2(lsz_YdStkBedNo, 0));
				recInputPara.setField("YD_STK_LYR_NO",  ydDaoUtils.stringPlusInt(lsz_YdStkLyrNo, 0));
				recInputPara.setField("MODIFIER", ydDaoUtils.paraRecChkNull(recPara, "YD_USER_ID"));
				
				ydUtils.displayRecord(szOperationName, recInputPara);
				
				
				nRtnVal = ydCarFtmvMtlDao.updYdCarftmvmtl(recInputPara, 0);
				
				
				if(nRtnVal<0){
					szRtnMsg = "YD_차량이송재료 UPDATE 실패";
					ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
					return szRtnMsg;
					
				}else if(nRtnVal ==0){
					szRtnMsg = "YD_차량이송재료 UPDATE 된 항목이 없습니다";
					ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
					return szRtnMsg;
					
				}
				
				szRtnMsg = "YD_차량이송재료 UPDATE 성공  [ " + lsz_YdCarSchId +" ][" + lsz_StlNo +"][" +ydDaoUtils.stringPlusInt2(lsz_YdStkBedNo, 0) +"]["+ ydDaoUtils.stringPlusInt(lsz_YdStkLyrNo, 0) +"]" ;
				ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
				
				
				
				
				//기존위치 삭제
				
                recInputPara3 =  JDTORecordFactory.getInstance().create();
				
				recInputPara3.setField("STL_NO", lsz_StlNo);
			
	
				ydUtils.displayRecord(szOperationName, recInputPara3);
				
				nRtnVal = ydCarFtmvMtlDao.updYdCarftmvmtl(recInputPara3, 5);
					
				
				szRtnMsg = "YD_차상위치 단정보 삭제 성공  [ " + lsz_YdCarSchId +" ][" + lsz_StlNo +"]";
				ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.DEBUG);	
				
				
				
				
		
				//야드맵정보 수정
					
					recInputPara2 =  JDTORecordFactory.getInstance().create();
					
					recInputPara2.setField("STL_NO", lsz_StlNo);
					recInputPara2.setField("YD_CARLD_STOP_LOC", lsz_YdCarstopLoc);
					recInputPara2.setField("YD_STK_BED_NO",  ydDaoUtils.stringPlusInt2(lsz_YdStkBedNo, 0));
					recInputPara2.setField("YD_STK_LYR_NO",  ydDaoUtils.stringPlusInt(lsz_YdStkLyrNo, 0)); 
				
					ydUtils.displayRecord(szOperationName, recInputPara2);
					
					
					nRtnVal = ydCarFtmvMtlDao.updYdCarftmvmtl(recInputPara2, 6);
					
					if(nRtnVal<0){
						szRtnMsg = "YD_차량 야드맵 UPDATE 실패";
						ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
						return szRtnMsg;
						
					}else if(nRtnVal ==0){
						szRtnMsg = "YD_차량야드맵 UPDATE 된 항목이 없습니다";
						ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
						return szRtnMsg;
						
					}
					
					szRtnMsg = "YD_차량야드맵 UPDATE 성공  [ " + lsz_YdCarSchId +" ][" + lsz_StlNo +"][" +ydDaoUtils.stringPlusInt2(lsz_YdStkBedNo, 0) +"]["+ ydDaoUtils.stringPlusInt(lsz_YdStkLyrNo, 0) +"]" ;
					ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
				
					
			}
			
			szLogMsg = "JSP-SESSION [상차위치 수정] 끝 ";
			
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			return YdConstant.RETN_CD_SUCCESS;
			
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
	} // end of carLiftPosSet
	
	
	
	
	
	
	  /**
	 *  상차초기화1
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return 
	 * @throws DAOException
	 */
	 public void carLiftStatInit0(JDTORecord inDto) throws DAOException {
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JDTORecordSet resPtCommInfo = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecord recInputPara = null;
		JDTORecord recInputPara2 = null;
		JDTORecord recInputPara3 = null;
		JDTORecord recInputPara4 = null;
		JDTORecord recTemp = JDTORecordFactory.getInstance().create();
		YdCarSchDao  ydCarSchDao = new YdCarSchDao();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		

		int nRtnVal  = 0;
		String szMethodName       = "carLiftStatInit1";
		String szOperationName = "상차초기화";
		String szLogMsg           = "";
		
		String szRtnMsg = "";
			
		String lsz_StlNo = "";

		String lsz_YdCarSchId = "";
		
		String szMsg = "";
	
	
		
		YdCarFtmvMtlDao ydCarFtmvMtlDao  = new YdCarFtmvMtlDao();
		PtStlFrtoMoveDao ptStlFrtoMoveDao = new PtStlFrtoMoveDao();
		
		try{
			
			szLogMsg = "JSP-SESSION [상차초기화] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			
			szMsg = "JSP-SESSION [차량 상차 정보 조회]시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
			
			recPara.setField("CAR_NO",    	 yddatautil.setDataDefault(inDto.getField("CAR_NO"), ""));
			recPara.setField("TRN_EQP_CD",   yddatautil.setDataDefault(inDto.getField("CAR_NO"), ""));
			
			//차량 번호로 차량 스케줄 및 차량 진행 상태 코드 값을 읽어온다. 
			
			
			nRtnVal = ydCarSchDao.getYdCarsch(recPara, outRecSet, 10);
			
			if (nRtnVal < 0){
				//조회 ERROR 
				szMsg ="차량스케줄에서 조회시 에러";
        		ydUtils.putLog("YdJspCommonSeEJBBean",szMethodName,szMsg  , YdConstant.ERROR);

			}else if(nRtnVal == 0){				
				// 조회된 정보가 없을 경우 
				szMsg ="차량스케줄에서 조회된 정보가 없습니다!!";
        		ydUtils.putLog("YdJspCommonSeEJBBean",szMethodName,szMsg  , YdConstant.INFO);				
			}
			
			outRecSet.first();			
		    recTemp = outRecSet.getRecord();

						
			lsz_YdCarSchId = recTemp.getFieldString("YD_CAR_SCH_ID");
			
//			차량스케쥴 정보 초기화
			
			recInputPara =  JDTORecordFactory.getInstance().create();
			
			recInputPara.setField("YD_CAR_SCH_ID", lsz_YdCarSchId);
						
			
			ydUtils.displayRecord(szOperationName, recInputPara);
			
			nRtnVal = ydCarFtmvMtlDao.updYdCarftmvmtl(recInputPara, 4);
			
			
			if(nRtnVal<0){
				szRtnMsg = "YD_차량스케쥴 업데이트 실패";
				ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
				
				
			}else if(nRtnVal ==0){
				szRtnMsg = "YD_차량스케쥴 업데이트할  레코드가 없습니다";
				ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
				
			}
			
			
			
//			차량스케쥴 재료정보 삭제
			
			recInputPara2 =  JDTORecordFactory.getInstance().create();
			
			recInputPara2.setField("YD_CAR_SCH_ID", lsz_YdCarSchId);
						
			ydUtils.displayRecord(szOperationName, recInputPara2);
			
			nRtnVal = ydCarFtmvMtlDao.updYdCarftmvmtl(recInputPara2, 3);
			
			
			if(nRtnVal<0){
				szRtnMsg = "YD_차량이송재료 재료 삭제 실패";
				ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);					
				
			}else if(nRtnVal ==0){
				szRtnMsg = "YD_차량이송재료 재료 삭제할 레코드가 없습니다";
				ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
				
			}
			
			
			szRtnMsg = "YD_차량이송재료 재료 삭제 성공  [ " + lsz_YdCarSchId +" ][" + lsz_StlNo +"]";
			ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.DEBUG);	
			
			
			
			
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
	} // end of carLiftStatInit
	
	

	  /**
	 *  상차초기화
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String carLiftStatInit(JDTORecord[] inDto) throws DAOException {
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JDTORecordSet resPtCommInfo = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecord recInputPara = null;
		JDTORecord recInputPara2 = null;
		JDTORecord recInputPara3 = null;
		JDTORecord recInputPara4 = null;
		JDTORecord recTemp = JDTORecordFactory.getInstance().create();
		YdCarSchDao  ydCarSchDao = new YdCarSchDao();
		

		int nRtnVal  = 0;
		String szMethodName       = "carLiftStatInit";
		String szOperationName = "상차초기화";
		String szLogMsg           = "";
		
		String szRtnMsg = "";
			
		String lsz_StlNo = "";

		String lsz_YdCarSchId = "";
	
	
		
		YdCarFtmvMtlDao ydCarFtmvMtlDao  = new YdCarFtmvMtlDao();
		PtStlFrtoMoveDao ptStlFrtoMoveDao = new PtStlFrtoMoveDao();
		
		try{
			
			szLogMsg = "JSP-SESSION [상차초기화] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
		
			for(int x=0;x<inDto.length;x++){
				
				
				recPara = inDto[x];
				
             //				재료번호
				lsz_StlNo =    ydDaoUtils.paraRecChkNull(recPara, "STL_NO");

			//차량스케줄 
				lsz_YdCarSchId = ydDaoUtils.paraRecChkNull(recPara, "YD_CAR_SCH_ID");
			  
		
				
			
				if(lsz_YdCarSchId.equals("")){
					szRtnMsg = "차량 스케줄 재료정보가 존재하지 않습니다";
					ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
					return szRtnMsg;
					
				}
				
								
				//차량스케쥴 정보 초기화
			
			/*	recInputPara2 =  JDTORecordFactory.getInstance().create();
				
				recInputPara2.setField("YD_CAR_SCH_ID", lsz_YdCarSchId);
							
				
				ydUtils.displayRecord(szOperationName, recInputPara2);
				
				nRtnVal = ydCarFtmvMtlDao.updYdCarftmvmtl(recInputPara2, 4);
				
				
				if(nRtnVal<0){
					szRtnMsg = "YD_차량스케쥴 업데이트 실패";
					ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
					
					return szRtnMsg;
					
				}else if(nRtnVal ==0){
					szRtnMsg = "YD_차량스케쥴 업데이트할  레코드가 없습니다";
					ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
					return szRtnMsg;
					
				}
				
				
				szRtnMsg = "YD_차량스케쥴 업데이트 성공  [ " + lsz_YdCarSchId +" ][" + lsz_StlNo +"]";
				ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.DEBUG);	
				
				*/

			
				
                recInputPara3 =  JDTORecordFactory.getInstance().create();
				
				recInputPara3.setField("STL_NO", lsz_StlNo);
			
	
				ydUtils.displayRecord(szOperationName, recInputPara3);
				
				nRtnVal = ydCarFtmvMtlDao.updYdCarftmvmtl(recInputPara3, 5);
				
				
				if(nRtnVal<0){
					szRtnMsg = "YD_차상위치 단정보 삭제 실패";
					ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
					
					return szRtnMsg;
					
				}else if(nRtnVal ==0){
					szRtnMsg = "YD_차상위치 단정보 삭제 삭제할 레코드가 없습니다";
					ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
					return szRtnMsg;
					
				}
				
				
				szRtnMsg = "YD_차상위치 단정보 삭제 성공  [ " + lsz_YdCarSchId +" ][" + lsz_StlNo +"]";
				ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.DEBUG);	
				
				
				
				// 해당 재료 번호에 이송정보 공통 TABLE에 있는 재료의 MAX차수의 상차완료 일자 //상차 예정위치정보를 삭제해주어야한다.
				
				recInputPara4 =  JDTORecordFactory.getInstance().create();
				recInputPara4.setField("FRTOMOVE_CARLOAD_DATE", "");
				recInputPara4.setField("YD_MTL_PLN_STR_FR_LOC_CD", "");
				recInputPara4.setField("MODIFIER", ydDaoUtils.paraRecChkNull(recPara, "YD_USER_ID"));
				recInputPara4.setField("STL_NO", lsz_StlNo);
				
				nRtnVal = ptStlFrtoMoveDao.updPtStlFrtoMove(recInputPara4, 2);
				
				if(nRtnVal<0){
					szRtnMsg = "PT_소재이송지시 이송삭제일자 삭제 실패 ";
					ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
					return szRtnMsg;
					
				}else if(nRtnVal ==0){
					szRtnMsg = "YD_차량이송재료 이송삭제일자 삭제할 레코드가 없습니다";
					ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
					return szRtnMsg;
					
				}						
				
				szRtnMsg = "YD_차량이송재료 이송삭제일자 삭제 성공  [ " + lsz_StlNo +"]";
				ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.DEBUG);
				
							
			}
			
			
			szLogMsg = "JSP-SESSION [상차초기화 삭제] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return YdConstant.RETN_CD_SUCCESS;
			
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
	} // end of carLiftStatInit
	
	
	
	
	
	
   
   
	  /**
	 *  상차재료 삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String carLiftStlDelete(JDTORecord[] inDto) throws DAOException {
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JDTORecordSet resPtCommInfo = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecord recInputPara = null;
		JDTORecord recInputPara2 = null;
		JDTORecord recTemp = JDTORecordFactory.getInstance().create();
		YdCarSchDao  ydCarSchDao = new YdCarSchDao();
		

		int nRtnVal  = 0;
		String szMethodName       = "carLiftStlDelete";
		String szOperationName = "상차재료 삭제";
		String szLogMsg           = "";
		
		String szRtnMsg = "";
			
		String lsz_StlNo = "";

		String lsz_YdCarSchId = "";
	
	
		
		YdCarFtmvMtlDao ydCarFtmvMtlDao  = new YdCarFtmvMtlDao();
		PtStlFrtoMoveDao ptStlFrtoMoveDao = new PtStlFrtoMoveDao();
		
		try{
			
			szLogMsg = "JSP-SESSION [상차재료 삭제] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			for(int x=0;x<inDto.length;x++){
				
				
				recPara = inDto[x];
				

			//재료번호
				lsz_StlNo =    ydDaoUtils.paraRecChkNull(recPara, "STL_NO");

			//차량스케줄 
				lsz_YdCarSchId = ydDaoUtils.paraRecChkNull(recPara, "YD_CAR_SCH_ID");
			  
		
				
			
				if(lsz_YdCarSchId.equals("")){
					szRtnMsg = "차량 스케줄 재료정보가 존재하지 않습니다";
					ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
					return szRtnMsg;
					
				}
				
			
			
				recInputPara =  JDTORecordFactory.getInstance().create();
				
				recInputPara.setField("YD_CAR_SCH_ID", lsz_YdCarSchId);
				recInputPara.setField("STL_NO", lsz_StlNo);
				recInputPara.setField("DEL_YN", "Y");
				recInputPara.setField("MODIFIER", ydDaoUtils.paraRecChkNull(recPara, "YD_USER_ID"));
			
				
				
				
				ydUtils.displayRecord(szOperationName, recInputPara);
				
				/*com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.deleteYdCarftmvmtl*/
				nRtnVal = ydCarFtmvMtlDao.updYdCarftmvmtl(recInputPara, 10);
				
				
				if(nRtnVal<0){
					szRtnMsg = "YD_차량이송재료 재료 삭제 실패";
					ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
					
					return szRtnMsg;
					
				}else if(nRtnVal ==0){
					szRtnMsg = "YD_차량이송재료 재료 삭제할 레코드가 없습니다";
					ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
					return szRtnMsg;
					
				}
				
				
				szRtnMsg = "YD_차량이송재료 재료 삭제 성공  [ " + lsz_YdCarSchId +" ][" + lsz_StlNo +"]";
				ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.DEBUG);	
				
				
				
				
				//차상위치 단정보 삭제
				
                recInputPara2 =  JDTORecordFactory.getInstance().create();
				
				recInputPara2.setField("STL_NO", lsz_StlNo);
			
	
				ydUtils.displayRecord(szOperationName, recInputPara2);
				
				nRtnVal = ydCarFtmvMtlDao.updYdCarftmvmtl(recInputPara2, 5);
				
				
				if(nRtnVal<0){
					szRtnMsg = "YD_차상위치 단정보 삭제 실패";
					ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
					
					return szRtnMsg;
					
				}else if(nRtnVal ==0){
					szRtnMsg = "YD_차상위치 단정보 삭제 삭제할 레코드가 없습니다";
					ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
					return szRtnMsg;
					
				}
				
				
				szRtnMsg = "YD_차상위치 단정보 삭제 성공  [ " + lsz_YdCarSchId +" ][" + lsz_StlNo +"]";
				ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.DEBUG);	
				
				
				
				
				
				
				// 해당 재료 번호에 이송정보 공통 TABLE에 있는 재료의 MAX차수의 상차완료 일자 //상차 예정위치정보를 삭제해주어야한다.
				
				recInputPara =  JDTORecordFactory.getInstance().create();
				recInputPara.setField("FRTOMOVE_CARLOAD_DATE", "");
				recInputPara.setField("YD_MTL_PLN_STR_FR_LOC_CD", "");
				recInputPara.setField("MODIFIER", ydDaoUtils.paraRecChkNull(recPara, "YD_USER_ID"));
				recInputPara.setField("STL_NO", lsz_StlNo);
				
				nRtnVal = ptStlFrtoMoveDao.updPtStlFrtoMove(recInputPara, 2);
				
				if(nRtnVal<0){
					szRtnMsg = "PT_소재이송지시 이송삭제일자 삭제 실패 ";
					ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
					return szRtnMsg;
					
				}else if(nRtnVal ==0){
					szRtnMsg = "YD_차량이송재료 이송삭제일자 삭제할 레코드가 없습니다";
					ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
					return szRtnMsg;
					
				}						
				
				szRtnMsg = "YD_차량이송재료 이송삭제일자 삭제 성공  [ " + lsz_StlNo +"]";
				ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.DEBUG);
				
									
			}
			
//			야드작업매수 UPDATE
			
			recInputPara =  JDTORecordFactory.getInstance().create();
			recInputPara.setField("YD_CAR_SCH_ID",  lsz_YdCarSchId);
			
			resPtCommInfo = JDTORecordFactory.getInstance().createRecordSet("YD");
			
			nRtnVal = ydCarSchDao.getYdCarsch(recInputPara, resPtCommInfo, 0);
			
			if(nRtnVal < 1){
				szRtnMsg = "차량스케줄 ["+ lsz_YdCarSchId  +"] 가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.DEBUG);
				return szRtnMsg;
				
			}
			
			recTemp = JDTORecordFactory.getInstance().create();
			
			resPtCommInfo.first();
			
			recTemp =  resPtCommInfo.getRecord();
			
			int intYD_EQP_WRK_SH = Integer.parseInt(ydDaoUtils.paraRecChkNull(recTemp, "YD_EQP_WRK_SH"));
			
			intYD_EQP_WRK_SH = intYD_EQP_WRK_SH - inDto.length;
			
			recInputPara.setField("YD_EQP_WRK_SH",  "" + intYD_EQP_WRK_SH);
			
			nRtnVal = ydCarSchDao.updYdCarsch(recInputPara, 0);
			
			if(nRtnVal < 1){
				szRtnMsg = "차량스케줄 ["+ lsz_YdCarSchId  +"] 을 UPDATE 할 수 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.DEBUG);
				return szRtnMsg;
				
			}
			
			
			
			szLogMsg = "JSP-SESSION [상차재료 삭제] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return YdConstant.RETN_CD_SUCCESS;
			
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
	} // end of carLiftStlDelete
	
	
	
	

	  /**
	 *  상차재료 등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String carLiftStlInsert(JDTORecord[] inDto) throws DAOException {
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JDTORecord recTemp = JDTORecordFactory.getInstance().create();
		JDTORecord recStockInfo = null;
		JDTORecord recInputPara = null;
		JDTORecordSet returnSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecordSet resStockInfo = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecordSet resPtCommInfo = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		/* 
		 * 1. 입력된 차량스케줄 ID 와 재료번호 정보로 차량스케줄 재료정보에 등록되어 있는지 확인
		 * 	1.1 등록된경우 - 차량스케줄 작업재료의 삭제유무를 N으로 UPDATE해준다.
		 * 				   
		 *  1.2 등록되지않은 경우 - 차량스케줄 작업재료 정보를 신규로 INSERT
		 *  
		 *  
		 * 2.공통 - 소재이송테이블 정보 MAX차수에 상차완료일시와 상차예정위치 정보를 현정보로 UPDATE 해준다.
		 * 
		 * 3. To위치로 Update
		 * 
		 * 4. From위치 삭제
		 * 
		 * 5. 작업이력 등록
		 */

			
		
		int    nRtnVal  = 0;
		int    Loop_i = 0;
		String szMethodName       = "";
		String szLogMsg           = "";
		
		String szRtnMsg = YdConstant.RETN_CD_SUCCESS;			
		String lsz_StlNo = "";
		String lsz_YdCarSchId = "";
		String lsz_YdStkColGp = "";
		String lsz_YdStkBedNo = "";
		String lsz_YdStkLyrNo = "";
		
		String szYD_STK_COL_GP = "";
		String szYD_STK_BED_NO = "";
		String szYD_STK_LYR_NO = "";
		
		String szOperationName = "carLiftStlInsert";
	
		
		YdCarFtmvMtlDao ydCarFtmvMtlDao  = new YdCarFtmvMtlDao();
		PtStlFrtoMoveDao ptStlFrtoMoveDao = new PtStlFrtoMoveDao();
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		YdStockDao ydStockDao = new YdStockDao();
		YdWrkHistDao ydWrkHistDao = new YdWrkHistDao();
		YdSchRuleDao ydSchRuleDao = new YdSchRuleDao();
		YdCarSchDao  ydCarSchDao = new YdCarSchDao();
		
		try{
			
			szLogMsg = "JSP-SESSION [상차재료 등록] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			for(int x=0;x<inDto.length;x++){
				
				
				recPara = inDto[x];
				

			//재료번호
				lsz_StlNo =    ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
			//차량스케줄 
				lsz_YdCarSchId = ydDaoUtils.paraRecChkNull(recPara, "YD_CAR_SCH_ID");	
			// 적치열	
				lsz_YdStkColGp =  ydDaoUtils.paraRecChkNull(recPara, "YD_CARLD_STOP_LOC");
			//차상위치
				lsz_YdStkBedNo =  ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");			
			//단 
				lsz_YdStkLyrNo =  ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO");
				
			// 작업재료정보에 재료가 존재하는 확인
				if(lsz_StlNo.equals("") ){					
					szRtnMsg = "재료번호가 존재하지 않습니다";
					ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
					return szRtnMsg;
					
				}
				
				
				if(lsz_YdCarSchId.equals("")){
					szRtnMsg = "차량스케줄 ID가  존재하지 않습니다";
					ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
					return szRtnMsg;
				}
				
				//	단 / 저장위치 정보를 Check 한다.
				
				if(lsz_YdStkBedNo.equals("") || lsz_YdStkLyrNo.equals("")){
					szRtnMsg = "단 / 저장위치 정보가 올바르지 않습니다";
					ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
					return szRtnMsg;
					
				}
				recInputPara =  JDTORecordFactory.getInstance().create();
				recInputPara.setField("STL_NO", lsz_StlNo);
				recInputPara.setField("YD_CAR_SCH_ID", lsz_YdCarSchId);
				
				
				// 작업재료 정보조회 
				returnSet = JDTORecordFactory.getInstance().createRecordSet("YD");
				nRtnVal = ydCarFtmvMtlDao.getYdCarftmvmtl(recInputPara, returnSet, 301); //쿼리는 수정해야함 삭제유무 'Y'인것도 조회
				
				if (nRtnVal <= 0 ){
					//Insert Logic
					
					/* 
					 * YD_CAR_SCH_ID	VARCHAR2(18)	Not Null	야드차량스케쥴ID
						STL_NO	VARCHAR2(11)	Not Null	재료번호
						REGISTER	VARCHAR2(10)		등록자
						REG_DDTT	DATE		등록일시
						MODIFIER	VARCHAR2(10)		수정자
						MOD_DDTT	DATE		수정일시
						DEL_YN	VARCHAR2(1)		삭제유무
						YD_CAR_UPP_LOC_CD	VARCHAR2(2)		야드차상위치코드 ???????
						YD_STK_BED_NO	VARCHAR2(2)		야드적치Bed번호
						YD_STK_LYR_NO	VARCHAR2(3)		야드적치단번호
						HCR_GP	VARCHAR2(1)		HCR구분
						STL_PROG_CD	VARCHAR2(1)		재료진도코드
						YD_MTL_ITEM	VARCHAR2(2)		야드재료품목
						YD_ROUTE_GP	VARCHAR2(2)		야드행선구분
						
						
					 */
					
					
					szRtnMsg = "기존 차량작업재료가 없는정보이므로 신규정보로 등록한다.";
					ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.DEBUG);
					
					
					
					recInputPara =  JDTORecordFactory.getInstance().create();
					recInputPara.setField("YD_CAR_SCH_ID", lsz_YdCarSchId);
					recInputPara.setField("STL_NO", lsz_StlNo);
					recInputPara.setField("REGISTER",  inDto[x].getField("YD_USER_ID"));
					
					
					/*
					 * 필드값 넣는위치 확인할것 
					 */
					recInputPara.setField("YD_CAR_UPP_LOC_CD",""); //현재 이필드는 사용하지 않는다고 함 
					recInputPara.setField("YD_STK_BED_NO",  ydDaoUtils.stringPlusInt2(lsz_YdStkBedNo, 0));
					recInputPara.setField("YD_STK_LYR_NO",  ydDaoUtils.stringPlusInt(lsz_YdStkLyrNo, 0));
					
					//저장품 재료정보에서 가지고와서 SETTING 
			        //PIDEV_S :병행가동용:PI_YD
					recInputPara.setField("PI_YD",yddatautil.setDataDefault(inDto[x].getField("PI_YD"), "*")); 
					nRtnVal = ydStockDao.getYdStock(recInputPara, resStockInfo, 0);
					
					if(nRtnVal > 0){
						szRtnMsg = "저장품 DATA 조회된 경우";
						ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.DEBUG);
						
						resStockInfo.first();
						recStockInfo = JDTORecordFactory.getInstance().create();
						recStockInfo = resStockInfo.getRecord();
						
						recInputPara.setField("HCR_GP", ydDaoUtils.paraRecChkNull(recStockInfo, "HCR_GP"));
						recInputPara.setField("STL_PROG_CD", ydDaoUtils.paraRecChkNull(recStockInfo, "STL_PROG_CD"));
						recInputPara.setField("YD_MTL_ITEM", ydDaoUtils.paraRecChkNull(recStockInfo, "YD_MTL_ITEM"));
						recInputPara.setField("YD_ROUTE_GP", ydDaoUtils.paraRecChkNull(recStockInfo, "YD_ROUTE_GP"));
						
					}else{
						// 저장품에 DATA가 없어서 하위데이터 정보 세팅 불가능
						szRtnMsg = "저장품에 DATA가 없어서 하위데이터[HCR_GP,STL_PROG_CD,YD_MTL_ITEM,YD_ROUTE_GP] 정보 세팅 불가능";
						ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
						
					}
					
					
					
					nRtnVal = ydCarFtmvMtlDao.insYdCarftmvmtl(recInputPara);
					
						
				}else  {
					//Update Logic
					
					//기능처리를 위한 쿼리를 새로 등록 한다.
					
					szRtnMsg = "기존삭제된 정보가 있어서 그정보를 다시 복원한다";
					ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.DEBUG);
					

					recInputPara =  JDTORecordFactory.getInstance().create();
					recInputPara.setField("STL_NO", lsz_StlNo);
					recInputPara.setField("YD_CAR_SCH_ID", lsz_YdCarSchId);					
					recInputPara.setField("DEL_YN", "N");
					recInputPara.setField("MODIFIER"   , inDto[x].getField("YD_USER_ID"));
					
					
					
					nRtnVal = ydCarFtmvMtlDao.updYdCarftmvmtl(recInputPara, 8);
					
					
					if(nRtnVal <=0 ){
						szRtnMsg = "차량스케줄 작업재료정보의 삭제유무를 'N'으로 작업하는데 실패하였습니다 ["+ lsz_StlNo +"]";
						ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);						
						
					}else{
						szRtnMsg = "차량스케줄 작업재료정보를 갱신하였습니다 ["+ lsz_StlNo +"]";
						ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.DEBUG);
					}
				}
				
				// 적치단 수정
				
				recInputPara =  JDTORecordFactory.getInstance().create();
				recInputPara.setField("STL_NO", lsz_StlNo);
				recInputPara.setField("YD_STK_LYR_MTL_STAT", "");
				
				resPtCommInfo = JDTORecordFactory.getInstance().createRecordSet("YD");
				
				nRtnVal = ydStkLyrDao.getYdStklyr(recInputPara, resPtCommInfo, 3);
				
				if(nRtnVal < 1){
					szRtnMsg = "적치된  해당 재료 ["+ lsz_StlNo  +"]가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.DEBUG);
					
				}else{
				
					resPtCommInfo.first();
					recStockInfo = JDTORecordFactory.getInstance().create();
					recStockInfo = resPtCommInfo.getRecord();
					
					
					szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recStockInfo, "YD_STK_COL_GP");
					szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recStockInfo, "YD_STK_BED_NO");
					szYD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(recStockInfo, "YD_STK_LYR_NO");
					
					
					
					recInputPara =  JDTORecordFactory.getInstance().create();
					recInputPara.setField("MODIFIER"   , inDto[x].getField("YD_USER_ID"));
					recInputPara.setField("DEL_YN", "N");
					recInputPara.setField("STL_NO", "");
					recInputPara.setField("YD_STK_LYR_MTL_STAT", YdConstant.YD_STK_LYR_ACTIVE);  //적치가능
					recInputPara.setField("YD_STK_COL_GP", ydDaoUtils.paraRecChkNull(recStockInfo, "YD_STK_COL_GP"));
					recInputPara.setField("YD_STK_BED_NO", ydDaoUtils.paraRecChkNull(recStockInfo, "YD_STK_BED_NO"));
					recInputPara.setField("YD_STK_LYR_NO", ydDaoUtils.paraRecChkNull(recStockInfo, "YD_STK_LYR_NO"));
					
					nRtnVal = ydStkLyrDao.updYdStklyr(recInputPara, 0);
					
					if (nRtnVal < 1) {
						szRtnMsg = "재료 ["+ lsz_StlNo  +"]의 야드적치단 수정을 실패하였습니다.";
						ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.DEBUG);
						return szRtnMsg;
					}
				
				}
				//차상위치 저장품 등록 
				recInputPara =  JDTORecordFactory.getInstance().create();
				recInputPara.setField("MODIFIER"   , inDto[x].getField("YD_USER_ID"));
				recInputPara.setField("DEL_YN", "N");
				recInputPara.setField("STL_NO", lsz_StlNo);
				recInputPara.setField("YD_STK_LYR_MTL_STAT", YdConstant.YD_STK_LYR_INACTIVE);  //
				recInputPara.setField("YD_STK_COL_GP", lsz_YdStkColGp);
				recInputPara.setField("YD_STK_BED_NO", lsz_YdStkBedNo);
				recInputPara.setField("YD_STK_LYR_NO", lsz_YdStkLyrNo);
				
				nRtnVal = ydStkLyrDao.updYdStklyr(recInputPara, 0);
				
				if (nRtnVal < 1) {
					szRtnMsg = "재료 ["+ lsz_StlNo  +"]의 차량적치단 수정을 실패하였습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.DEBUG);
					return szRtnMsg;
				}
				
				
				recInputPara =  JDTORecordFactory.getInstance().create();
				recInputPara.setField("STL_NO", lsz_StlNo);
				recInputPara.setField("YD_STK_LYR_MTL_STAT", "");
				
				resPtCommInfo = JDTORecordFactory.getInstance().createRecordSet("YD");
				
				nRtnVal = ydStkLyrDao.getYdStklyr(recInputPara, resPtCommInfo, 3);
				
				if(nRtnVal < 1){
					szRtnMsg = "적치된  해당 재료 ["+ lsz_StlNo  +"]가 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.DEBUG);
					
				}else{
				
					resPtCommInfo.first();
					recStockInfo = JDTORecordFactory.getInstance().create();
					recStockInfo = resPtCommInfo.getRecord();
					
					
					szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recStockInfo, "YD_STK_COL_GP");
					szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recStockInfo, "YD_STK_BED_NO");
					szYD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(recStockInfo, "YD_STK_LYR_NO");
					
				}
				
				
				szRtnMsg = "차량스케줄 작업재료 적치단정보를 갱신하였습니다 ["+ lsz_StlNo +"]";
				ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.DEBUG);
				
				// 작업이력 생성...
				
				/*
				 *  작업 이력정보에 이력을 남긴다.
				 */
					
				
					
					
				szRtnMsg = "[JSP-SESSION] [ "  + szOperationName +  "]   이력정보추가 시작 ";
				ydUtils.putLog("SlabJspSeEJB", szMethodName, szRtnMsg, YdConstant.DEBUG);
				
									
				//초기화
				
				JDTORecord recOldPos = 	JDTORecordFactory.getInstance().create();				
				
				JDTORecord recWrkHistPara = JDTORecordFactory.getInstance().create();
				JDTORecord recWrkHistInfo = JDTORecordFactory.getInstance().create();
				
				recStockInfo = JDTORecordFactory.getInstance().create();
				JDTORecordSet outRecSet = null;
				JDTORecordSet rsDelInfo = null;
				JDTORecordSet rsStockInfo = null;
				
				rsDelInfo		=	JDTORecordFactory.getInstance().createRecordSet("rsdelInfo");
				String lszYdSchCd = "";

				recOldPos.setField("STL_NO", lsz_StlNo);
				recOldPos.setField("YD_STK_LYR_MTL_STAT", "C");
				
				
									
				/*
				 *  저장품(STOCK) 에 데이터가 있는지 체크한다.
				 */
					
		        //PIDEV_S :병행가동용:PI_YD
				recOldPos.setField("PI_YD",yddatautil.setDataDefault(inDto[x].getField("PI_YD"), "*")); 
				
				rsStockInfo = JDTORecordFactory.getInstance().createRecordSet("rsStockInfo");
				nRtnVal = ydStockDao.getYdStock(recOldPos, rsStockInfo, 0);
				
				
				// 재료 정보 이력정보에 추가 (2009.11.09)
				if(nRtnVal > 0){
					//STOCK 정보가 존재할 경우
					szRtnMsg = "[JSP-SESSION] [ "  + szOperationName +  "]  저장품 정보가 존재하므로 이력정보를 추가 가능합니다 ";
					ydUtils.putLog("SlabJspSeEJB", szMethodName, szRtnMsg, YdConstant.DEBUG);
					
					rsStockInfo.first();
					recStockInfo= rsStockInfo.getRecord();
					
				} else{
					
					szRtnMsg = "[JSP-SESSION] [ "  + szOperationName +  "]  저장품 정보가 없어 이력정보를 생성하지 않습니다 ";
					ydUtils.putLog("SlabJspSeEJB", szMethodName, szRtnMsg, YdConstant.ERROR);
					
					break;
					
				}
				
				/*
				 *  차량스케줄 정보를 copy한다.
				 */
				recOldPos = 	JDTORecordFactory.getInstance().create();	
				recOldPos.setField("YD_CAR_SCH_ID", lsz_YdCarSchId);
				
				rsStockInfo = JDTORecordFactory.getInstance().createRecordSet("rsStockInfo");
				nRtnVal = ydCarSchDao.getYdCarsch(recOldPos, rsStockInfo, 0);
				
				
				// 차량스케줄정보 이력정보에 추가 (2010.02.18)
				if(nRtnVal > 0){
					//STOCK 정보가 존재할 경우
					szRtnMsg = "[JSP-SESSION] [ "  + szOperationName +  "]  차량스케줄 정보가 존재하므로 이력정보를 추가 가능합니다 ";
					ydUtils.putLog("SlabJspSeEJB", szMethodName, szRtnMsg, YdConstant.DEBUG);
					
					rsStockInfo.first();
					recStockInfo= rsStockInfo.getRecord();
					
				} else{
					
					szRtnMsg = "[JSP-SESSION] [ "  + szOperationName +  "]  차량스케줄 정보가 없어 이력정보를 생성하지 않습니다 ";
					ydUtils.putLog("SlabJspSeEJB", szMethodName, szRtnMsg, YdConstant.ERROR);
					
					break;
					
				}
				
				
				
				//작업이력 - 권상정보 관련 입력
				
				recStockInfo.setField("YD_UP_WR_LOC", szYD_STK_COL_GP +  szYD_STK_BED_NO);
				recStockInfo.setField("YD_UP_WR_LAYER", szYD_STK_LYR_NO );
				//권상완료일시 --현재시간 구하는 정보 
				recStockInfo.setField("YD_UP_CMPL_DT",YdUtils.getCurDate("yyyyMMddHHmmss") );
									
				szRtnMsg = "[JSP-SESSION] [ "  + szOperationName +  "]  기존 저장위치 정보가 있을경우는 FROM(권상)정보를 추가합니다 ";
				ydUtils.putLog("SlabJspSeEJB", szMethodName, szRtnMsg, YdConstant.DEBUG);
				
				
				//야드구분은 적치열 정보에서 가지고 온다.
				recStockInfo.setField("YD_GP", szYD_STK_COL_GP.substring(0, 1));	
				recStockInfo.setField("STL_NO", lsz_StlNo);

				// 작업이력 - 권하정보 입력 
				recStockInfo.setField("YD_DN_WR_LOC",    lsz_YdStkColGp +  lsz_YdStkBedNo );
				recStockInfo.setField("YD_DN_WR_LAYER",  lsz_YdStkLyrNo);
				//권하완료일시 --현재시간 구하는 정보 
				recStockInfo.setField("YD_DN_CMPL_DT", YdUtils.getCurDate("yyyyMMddHHmmss"));
				
				//이적 작업 스케줄 코드를 얻는다.
				// STOCK 정보의 입고일자가 없을경우 입고스케줄로 편성
				lszYdSchCd = lsz_YdStkColGp + "UM";
				
				szRtnMsg = "JSP-SESSION [저장위치[ "  + szOperationName +  "] 해당 이적작업 스케줄 구하기.["+ lszYdSchCd +"]" ; 
				ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.INFO);
				
							
				recStockInfo.setField("YD_SCH_CD", lszYdSchCd ); // 야드 스케줄 코드 - YD_SCH_CD	
				recStockInfo.setField("YD_AID_WRK_YN" , "N");    // 야드보조작업여부 - YD_AID_WRK_YN		
					
				outRecSet = JDTORecordFactory.getInstance().createRecordSet("outRecSet");
				//해당 스케줄 정보로 주작업 크레인 정보를 가지고 온다.
				
				szRtnMsg = "JSP-SESSION [ "  + szOperationName +  "] 해당 스케줄 코드에 대한  기준정보 조회 ["+ lszYdSchCd +"]" ; 
				ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.INFO);		
				
				recWrkHistPara.setField("YD_SCH_CD", lszYdSchCd);
				nRtnVal = ydSchRuleDao.getYdSchrule(recWrkHistPara, outRecSet, 0);
				
				if(nRtnVal > 0){
					outRecSet.first();
					recWrkHistInfo = outRecSet.getRecord();
					
				}else {
					szRtnMsg = "JSP-SESSION [ "  + szOperationName +  "] 해당 스케줄에 대한 기준정보가 존재 하지 않습니다.["+ lszYdSchCd +"]" ; 
					ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);						
				}
				
				//스케줄 기준의 작업 크레인 정보를 넣어준다.				
				recStockInfo.setField("YD_EQP_ID" , ydDaoUtils.paraRecChkNull(recWrkHistInfo, "YD_WRK_CRN")); 
			
				// 야드수불구분 - YD_GNT_GP
				
				// 입고 : L , 이적 : M , 출고 : U
				String szYdGntGp  = "";
				
				if(lszYdSchCd.length() > 0){
					recStockInfo.setField("YD_GNT_GP", lszYdSchCd.substring(6, 7) );
					
				}else{
					recStockInfo.setField("YD_GNT_GP", "U");
					
				}
				
				recStockInfo.setField("YD_CAR_SCH_ID", lsz_YdCarSchId);
				
		
				recStockInfo.setField("YD_SCH_ST_GP", "B");		// 야드스케줄 기동 구분 "B" 로 넣어준다.			
				
				//이력정보 남기기
				
				ydUtils.displayRecord(szOperationName, recStockInfo);
				
				nRtnVal = ydWrkHistDao.insYdWrkHistCarSch(recStockInfo);
				
				if(nRtnVal > 0){
					szRtnMsg = "JSP-SESSION [ "  + szOperationName +  "] 이력정보를 로깅하였습니다." + nRtnVal ; 
					ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.INFO);	
				}else{
					szRtnMsg = "JSP-SESSION [ "  + szOperationName +  "] 이력정보를 로깅 실패 하였습니다" + nRtnVal ; 
					ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
				}
			
				
				
				
				// 공통 이송재료 테이블 UPDATE
				
//				recInputPara =  JDTORecordFactory.getInstance().create();
//				recInputPara.setField("FRTOMOVE_CARLOAD_DATE",  ydDaoUtils.paraRecChkNull(recPara, "FRTOMOVE_CARLOAD_DATE"));
//				
//				
//				//상차 위치 입력 - 이미 차량에 상차완료된 정보이므로 적치단에 정보가 없으므로 
//				//공통 정보에있는 저장위치이력 1번 데이터를 읽어서 넣어준다.
//				
//				
//				szRtnMsg = "적치 정보에있는 저장위치 읽어서 넣어준다";
//				ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.DEBUG);
//				
//				
//				//재료가 있는 저장위치를 찾는다.
//				recTemp = JDTORecordFactory.getInstance().create();
//				resPtCommInfo = JDTORecordFactory.getInstance().createRecordSet("YD");
//				recTemp.setField("STL_NO", lsz_StlNo);
//				recTemp.setField("YD_STK_LYR_MTL_STAT", "C");
//				
//				
//				nRtnVal = ydStkLyrDao.getYdStklyr(recTemp, resPtCommInfo, 3);
//				
//				if(nRtnVal < 1){
//					szRtnMsg = "적치된 (C) 해당 재료 ["+ lsz_StlNo  +"가 없습니다.";
//					ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.DEBUG);
//					return szRtnMsg;
//					
//				}
//				recTemp = JDTORecordFactory.getInstance().create();
//				
//				resPtCommInfo.first();
//				
//				recTemp =  resPtCommInfo.getRecord();
//				String szStkLyrNo = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_NO");
//				
//				
//				if(szStkLyrNo.equals("") || szStkLyrNo.length() < 3){
//					szRtnMsg = "적치단 정보가 올바르지 않습니다.";
//					ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
//					return szRtnMsg;
//					
//				}
//				
//				
//				recInputPara.setField("YD_MTL_PLN_STR_FR_LOC_CD", ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_COL_GP")
//						                                         + ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_NO")
//						                                         +  szStkLyrNo.substring(1,3) );
//				
//				
//				
//				recInputPara.setField("MODIFIER", ydDaoUtils.paraRecChkNull(recPara, "YD_USER_ID"));
//				recInputPara.setField("STL_NO", lsz_StlNo);				
//				
//				nRtnVal = ptStlFrtoMoveDao.updPtStlFrtoMove(recInputPara, 2);
//				
//				
//				if(nRtnVal<1){
//					szRtnMsg = "이송재료정보 UPDATE할수 없습니다";
//					ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.ERROR);
//					throw new DAOException(szRtnMsg);
//				}
				
				
			}
			
			//야드작업매수 UPDATE
			
			recInputPara =  JDTORecordFactory.getInstance().create();
			recInputPara.setField("YD_CAR_SCH_ID",  lsz_YdCarSchId);
			
			resPtCommInfo = JDTORecordFactory.getInstance().createRecordSet("YD");
			
			nRtnVal = ydCarSchDao.getYdCarsch(recInputPara, resPtCommInfo, 0);
			
			if(nRtnVal < 1){
				szRtnMsg = "차량스케줄 ["+ lsz_YdCarSchId  +"] 가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.DEBUG);
				return szRtnMsg;
				
			}
			
			recTemp = JDTORecordFactory.getInstance().create();
			
			resPtCommInfo.first();
			
			recTemp =  resPtCommInfo.getRecord();
			
			int intYD_EQP_WRK_SH = Integer.parseInt(ydDaoUtils.paraRecChkNull(recTemp, "YD_EQP_WRK_SH"));
			
			intYD_EQP_WRK_SH = intYD_EQP_WRK_SH + inDto.length;
			
			recInputPara.setField("YD_EQP_WRK_SH",  "" + intYD_EQP_WRK_SH);
			
			nRtnVal = ydCarSchDao.updYdCarsch(recInputPara, 0);
			
			if(nRtnVal < 1){
				szRtnMsg = "차량스케줄 ["+ lsz_YdCarSchId  +"] 을 UPDATE 할 수 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szRtnMsg, YdConstant.DEBUG);
				return szRtnMsg;
				
			}
			
			
			szLogMsg = "JSP-SESSION [상차재료 등록] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			return YdConstant.RETN_CD_SUCCESS;
			
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
	} // end of carLiftStlDelete
	
	
   
		/**
	    * 이송재료에 등록된 재료정보 조회
	    * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	    * @param inDto
	    * @return
	    * @throws DAOException
	    */
	   public JDTORecordSet getStlInfobyMoveStl(JDTORecord inDto) throws DAOException {
		
		    JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
			JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			
			YdStockDao ydStockDao = new YdStockDao();
			String szOperationName	= "이송재료에 등록된 재료정보 조회";
			String szMethodName		= "getStlInfobyMoveStl";
			String szMsg       	 	= "";		
			
		
			
			int intRtnVal = 0;
			
			try {
				
				szMsg = "[JSP Session : " + szOperationName + "] 메소드 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				
				recPara.setField("STL_NO",	        inDto.getField("STL_NO"));
						
				intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 144);
				
				
				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "[JSP Session : " + szOperationName + "] 오류발생[1] - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "[JSP Session : " + szOperationName + "] parameter error - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					//return outRecSet;
					return outRecSet;
				} // end of if
				
				szMsg = "[JSP Session : " + szOperationName + "] 메소드 끝";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				
			} catch (Exception e) {
				// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
				throw new DAOException(getClass().getName() + e.getMessage(),e);
			} finally {
			}
			return outRecSet;
	  }
   
   

	   
	   /**
	    * 적치열정보로 BED정보를 조회하는 메소드
	    * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	    * @param inDto
	    * @return
	    * @throws DAOException
	    */
	   public JDTORecordSet getBedNoByStkCol(JDTORecord inDto) throws DAOException {
		   		   
		    JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
			JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			
			YdStkBedDao ydStkBedDao			= new YdStkBedDao();
			String szOperationName	= "BED조회";
			String szMethodName		= "getBedNoByStkCol";
			String szMsg       	 	= "";		
			
			String szYdDongGp	= null;
			String szYdSpanGp	= null;
			String szYdColGp	= null;
			String szYdStkColGp = null;
			
			int intRtnVal = 0;
			
			try {
				
				szMsg = "[JSP Session : " + szOperationName + "] 메소드 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				szYdDongGp = ydDaoUtils.paraRecChkNull(inDto,"YD_DONG_GP");
				szYdSpanGp = ydDaoUtils.paraRecChkNull(inDto,"YD_SPAN_GP");
				szYdColGp = ydDaoUtils.paraRecChkNull(inDto,"YD_COL_GP");
				
				szYdStkColGp = szYdDongGp + szYdSpanGp + szYdColGp;
				
				
				recPara.setField("YD_STK_COL_GP",	szYdStkColGp);		
				
				intRtnVal = ydStkBedDao.getYdStkbed(recPara, outRecSet, 22);
				
				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "[JSP Session : " + szOperationName + "] 오류발생[1] - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "[JSP Session : " + szOperationName + "] parameter error - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					//return outRecSet;
					return outRecSet;
				} // end of if
				
				szMsg = "[JSP Session : " + szOperationName + "] 메소드 끝";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				
			} catch (Exception e) {
				// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
				throw new DAOException(getClass().getName() + e.getMessage(),e);
			} finally {
			}
			return outRecSet;
	  }
	   
	   /**
		 * BED정보로 주작업구분들을 조회하는 메소드
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws DAOException
		 */
	   public JDTORecordSet getBedInfoByGrpNm(JDTORecord inDto) throws DAOException {
		   		   
		    JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
			JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			
			YdPrepSchDao ydPrepSchDao			= new YdPrepSchDao();
			String szOperationName	= "주작업구분BED조회";
			String szMethodName		= "getBedInfoByGrpNm";
			String szMsg       	 	= "";		
			
			String szYdGp		= null;
			String szYdDongGp	= null;
			String szYdSpanGp	= null;
			String szYdColGp	= null;
			String szYdStkColGp = null;
			String szYdStkBedNo = null;
			String szGrpNm = null;
			
			int intRtnVal = 0;
			
			try {
				
				szMsg = "[JSP Session : " + szOperationName + "] 메소드 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				szYdGp = ydDaoUtils.paraRecChkNull(inDto,"YD_GP");
				szYdDongGp = ydDaoUtils.paraRecChkNull(inDto,"YD_DONG_GP");
				szYdSpanGp = ydDaoUtils.paraRecChkNull(inDto,"YD_SPAN_GP");
				szYdColGp = ydDaoUtils.paraRecChkNull(inDto,"YD_COL_GP");
				szYdStkBedNo = ydDaoUtils.paraRecChkNull(inDto,"YD_BED_GP");
				szGrpNm = ydDaoUtils.paraRecChkNull(inDto,"GRP_NM");
				
				szYdStkColGp = szYdGp + szYdDongGp + szYdSpanGp + szYdColGp;
								
				recPara.setField("YD_STK_COL_GP",	szYdStkColGp);
				recPara.setField("YD_STK_BED_NO",	szYdStkBedNo);
				recPara.setField("GRP_NM",	szGrpNm);
//PIDEV_S :병행가동용:PI_YD
				recPara.setField("PI_YD",    	szYdGp);					
				intRtnVal = ydPrepSchDao.getYdPrepsch(recPara, outRecSet, 20);
				
				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "[JSP Session : " + szOperationName + "] 오류발생[1] - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "[JSP Session : " + szOperationName + "] parameter error - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					//return outRecSet;
					return outRecSet;
				} // end of if
				
				szMsg = "[JSP Session : " + szOperationName + "] 메소드 끝";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				
			} catch (Exception e) {
				// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
				throw new DAOException(getClass().getName() + e.getMessage(),e);
			} finally {
			}
			return outRecSet;
	  }
	   
	   /**
	    * 적치열정보로 BED정보를 조회하는 메소드
	    * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	    * @param inDto
	    * @return
	    * @throws DAOException
	    */
	   public JDTORecordSet getBedNoByStkColNo(JDTORecord inDto) throws DAOException {
		   		   
		    JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
			JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			
			YdStkBedDao ydStkBedDao			= new YdStkBedDao();
			String szOperationName	= "BED조회";
			String szMethodName		= "getBedNoByStkColNo";
			String szMsg       	 	= "";		
			
			String szYdDongGp	= null;
			String szYdSpanGp	= null;
			String szYdColGp	= null;
			String szYdStkColGp = null;
			
			int intRtnVal = 0;
			
			try {
				
				szMsg = "[JSP Session : " + szOperationName + "] 메소드 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				szYdDongGp = ydDaoUtils.paraRecChkNull(inDto,"YD_BAY_GP");
				szYdSpanGp = ydDaoUtils.paraRecChkNull(inDto,"YD_EQP_GP");
				szYdColGp = ydDaoUtils.paraRecChkNull(inDto,"YD_STK_COL_NO");
				
				szYdStkColGp = szYdDongGp + szYdSpanGp + szYdColGp;
				
				
				recPara.setField("YD_STK_COL_GP",	szYdStkColGp);		
				
				intRtnVal = ydStkBedDao.getYdStkbed(recPara, outRecSet, 22);
				
				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "[JSP Session : " + szOperationName + "] 오류발생[1] - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "[JSP Session : " + szOperationName + "] parameter error - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					//return outRecSet;
					return outRecSet;
				} // end of if
				
				szMsg = "[JSP Session : " + szOperationName + "] 메소드 끝";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				
			} catch (Exception e) {
				// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
				throw new DAOException(getClass().getName() + e.getMessage(),e);
			} finally {
			}
			return outRecSet;
	  }
	   
	   
	   
	   /**
		 *  후판제품야드 적치열 폭정보 변경 LOGIC (2009.11.11)
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inParaRec{YD_STK_COL_GP,YD_STK_COL_W_GP,YD_USER_ID}
		 * @return String
		 * @throws DAOException
		 */
		public String updplateGdsYdStkColFix(JDTORecord inParaRec) throws DAOException {
			 
			
			
			
			/*
			 * 
			   1.넘겨온 적치 열 폭 정보와 현재 DB에 있는 폭 정보를 비교한다.
						정보가 같을 경우의 데이터는 변경될 정보가 없으므로 6번으로 
						정보가 다른 경우는 2번으로
			   2.변경될 정보가 소폭인지 중폭,광폭 인지 판단한다.
						중폭,광폭 일 경우에 홀수 열이 아닐 경우는 변경 할 수 없다. PASS한다.(다음 내용을 수행 할 경우 6번으로)
						소폭인 경우는 3번으로
						중폭인 경우 4번으로 
						광폭인 경우 5번으로 
			   3.소폭인 경우
						열 정보의 폭 구분을 소폭으로 변경한다.
						베드정보의 폭 길이를 소폭 값으로 변경한다.
						베드정보의 기준 X 좌표를 열 기준 값으로 덮어 쓴다.
							(+)L2 로 적치열 수정된 정보를 내려보내준다.
						 6번으로 
				4.중폭인 경우 다음의 상태를 체크한다.
						1열이거나 -1 열이 사용가능인 경우
							열 정보의 폭 구분을 중폭으로 변경한다.
							베드의 폭 값을 중폭으로 변경한다.
								(+)L2 로 적치열 수정된 정보를 내려보내준다.
							+ 1 열은 사용불가 정보로 변경한다.
								(+)L2 로 적치열 수정된 정보를 내려보내준다.
							6번으로
						- 1열이 사용 불가능인 경우
							열 정보의 폭 구분을 중폭으로 변경하고 베드정보의 폭 길이 를 중폭으로 늘린다.
							베드정보의 기준 X 좌표를 열 X 기준에서 - 중폭기준 값 만큼 이동시킨다.
								(+)L2 로 적치열 수정된 정보를 내려보내준다.
							6번으로
							이 외의 경우 6번으로
				5.광폭인 경우 
						열 정보를 광폭으로 변경한다.
						베드의 폭 값을 광폭으로 변경한다
							(+)L2 로 적치열 수정된 정보를 내려보내준다.
						+ 1 열의 정보를 사용불가로 변경한다.
							(+)L2 로 적치열 수정된 정보를 내려보내준다.
						6번으로 
				6.END

			 */
			String szRtnMsg = YdConstant.RETN_CD_SUCCESS;
			String szLogMsg = "";
			String szMethodName = "updplateGdsYdStkColFix";
			String szOperationName = "후판제품야드 적치열 폭정보 변경 LOGIC";
			String szYdStkColGp = "";
			String szYdStkColGpNext = "";
			String szYdStkColGpBefo = "";
			String szYdStkColWGp = "";
			String szYdStkColNo = "";
			
			String szActStat = "";
			String szColWGp = "";
			
			JDTORecord recStkColInfo = null;
			JDTORecord resStkColNextInfo = null;
			JDTORecord recStkColBefoInfo = null;
			JDTORecord recPara = null;
			JDTORecord recColPara = null;
			JDTORecord recTempPara = null;
			JDTORecordSet rsetStkColInfo = null;
			JDTORecordSet rsetStkColNextInfo = null;
			JDTORecordSet rsetStkColBefoInfo = null;
			
			YdStkColDao ydStkColDao  = new YdStkColDao();
			YdStkBedDao ydStkBedDao = new YdStkBedDao();
			YdDelegate ydDelegate = new YdDelegate();
			
			int nRtnVal = 0;
		
			
			
			try{
				szLogMsg = "[Jsp-Session "+szOperationName+" ]시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
				
				szYdStkColGp = ydDaoUtils.paraRecChkNull(inParaRec, "YD_STK_COL_GP");
				szYdStkColWGp = ydDaoUtils.paraRecChkNull(inParaRec, "YD_STK_COL_W_GP");
				
				
			   
				 // 1. 넘겨온 적치 열 폭 정보와 현재 DB에 있는 폭 정보를 비교한다.
				  
				
				
				if(szYdStkColGp.equals("") || szYdStkColGp.length()!=6){
					//적치열 정보가 올바르지 않을 경우!!
					szLogMsg = "[Jsp-Session "+szOperationName+" ] 적치열 정보가 맞지 않습니다 :" + szYdStkColGp;
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);	
					
					szRtnMsg ="적치열 정보가 올바르지 않습니다" ;
					return szRtnMsg;
					
				}
				
				if( !(szYdStkColWGp.equals("L") || szYdStkColWGp.equals("M") || szYdStkColWGp.equals("S"))){
					//변경 할 폭정보가 없습니다.!!
					szLogMsg = "[Jsp-Session "+szOperationName+" ] 변경 할 폭정보가 없습니다.!!" + szYdStkColWGp ;
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					szRtnMsg =" 변경 할 폭정보가 없습니다.!!" ;
					return szRtnMsg;
					
					
				}
				
				
				
				// 레코드 초기화 
				 recStkColInfo = JDTORecordFactory.getInstance().create();
				 rsetStkColInfo       = JDTORecordFactory.getInstance().createRecordSet("rsetStkColInfo");
				
				 	// 적치열 정보 조회 
				 nRtnVal = ydStkColDao.getYdStkcol(inParaRec, rsetStkColInfo, 0);
				 
				 if(nRtnVal <=0){
						//적치열 폭구분 정보가 올바르지 않을 경우!!
						szLogMsg = "[Jsp-Session "+szOperationName+" ] 적치열 정보 조회시 데이터가 존재 하지 않습니다." + szYdStkColWGp ;
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						szRtnMsg ="적치열 정보 조회시 데이터가 존재 하지 않습니다." ;
						return szRtnMsg;
					 
				 }
				 
				 rsetStkColInfo.first();
				 recStkColInfo = rsetStkColInfo.getRecord();
				 
				 
				 //if(ydDaoUtils.paraRecChkNull(recStkColInfo, "YD_STK_COL_W_GP").equals(szYdStkColWGp)){
				//	 //입력 정보와 DB 정보가 같은 경우 
				//	 szLogMsg = "[Jsp-Session "+szOperationName+" ] 입력정보와 같아 변경할 필요가 없습니다" + szYdStkColWGp ;
				//	 ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				//	 szRtnMsg = YdConstant.RETN_CD_SUCCESS;
				//	 return szRtnMsg;					 
				// }
				 
				 // 2.변경될 정보가 소폭인지 중폭,광폭 인지 판단한다.
				 	// 중폭,광폭 일 경우에 홀수 열이 아닐 경우는 변경 할 수 없다. PASS한다.[다음 내용을 수행 할 경우 6번으로]
				 
				 szYdStkColNo = szYdStkColGp.substring(4, 6);
				 
				 szLogMsg = "[Jsp-Session "+szOperationName+" ] 적치열 번호 "+ szYdStkColNo ;
				 ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				 
				 
				 //다음 적치열 정보
				 
				 szYdStkColGpNext = szYdStkColGp.substring(0,4) + ydDaoUtils.stringPlusInt2(szYdStkColNo, 1);					 
				 
				 //이전 적치열 정보
				 
				 szYdStkColGpBefo = szYdStkColGp.substring(0,4) + ydDaoUtils.stringPlusInt2(szYdStkColNo, -1);
				 
				 
				 // 중폭 또는 광폭인 경우
				 if(szYdStkColWGp.equals("L") || szYdStkColWGp.equals("M")){
					 
					 int nColNo = Integer.parseInt(szYdStkColNo);
					 
					 // 해당열이 짝수인경우
					 /*
					 if(nColNo % 2 == 0){
						    //적치열 폭구분 정보가 올바르지 않을 경우!!
							 recTempPara = JDTORecordFactory.getInstance().create();
							 rsetStkColBefoInfo = JDTORecordFactory.getInstance().createRecordSet("rsetStkColBefoInfo");
							 recStkColBefoInfo = JDTORecordFactory.getInstance().create();
							
							 
							 
							 recTempPara.setField("YD_STK_COL_GP", szYdStkColGpBefo);
							 nRtnVal = ydStkColDao.getYdStkcol(recTempPara, rsetStkColBefoInfo, 0);
							 
							 if(nRtnVal<= 0){
								 //기존 베드가 존재 하지 않는경우 (00베드 또는 없는 베드일경우는 >> 증가 방향)
								szLogMsg = "[Jsp-Session "+szOperationName+" ] 이전열정보 SELECT중  ERROR 발생." + szYdStkColGpBefo ;
								ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
								throw new DAOException("이전열정보 SELECT중  ERROR 발생.");
							 }else{
								 rsetStkColBefoInfo.first();
								 recStkColBefoInfo = rsetStkColBefoInfo.getRecord();
								 
								 szActStat = ydDaoUtils.paraRecChkNull(recStkColBefoInfo, "YD_STK_COL_ACT_STAT");
								 szColWGp =  ydDaoUtils.paraRecChkNull(recStkColBefoInfo, "YD_STK_COL_W_GP");
								 
								 if(szActStat.equals("N")||szColWGp.equals("L") || szColWGp.equals("M")){
									szLogMsg = "[Jsp-Session "+szOperationName+" ] 중폭/광폭 변경은 홀수 열만 변환할 수 있습니다 ." + szYdStkColWGp ;
									ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
									szRtnMsg ="중폭/광폭 변경은 홀수 열만 변환할 수 있습니다 ." ;
									return szRtnMsg;
								 } 
							 }
					 }*/
					 
					 if(szYdStkColWGp.equals("L") ){
						 //5. 광폭인 경우 
						 szLogMsg = "[Jsp-Session "+szOperationName+" ] 광폭정보로 변경합니다. ." + szYdStkColWGp ;
						 ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						 
						//열정보를 광폭으로 변경한다.
						 
						 //초기화 
						 recPara = JDTORecordFactory.getInstance().create();
						 recPara.setField("YD_STK_COL_GP", szYdStkColGp);
						 recPara.setField("MODIFIER", ydDaoUtils.paraRecChkNull(inParaRec, "YD_USER_ID"));
						 recPara.setField("YD_STK_COL_W_GP", szYdStkColWGp);	 
						 recPara.setField("YD_STK_COL_H_MAX",new Integer(YdConstant.STKBED_H_GP_L));
						 
						 
						 nRtnVal =  ydStkColDao.updYdStkcol(recPara, 0);
						 
						 if(nRtnVal <=0){
							 // 열 정보 변경 실패시 
							szLogMsg = "[Jsp-Session "+szOperationName+" ] 광폭 변경 UPDATE 중 ERROR 발생." + szYdStkColWGp ;
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
							throw new DAOException("광폭 변경 UPDATE 중 ERROR 발생.");
						 }
						 	
						 
						// 해당 열에 포함된 베드 정보의 폭값을 광폭으로 변경한다.
						 
						 szLogMsg = "[Jsp-Session "+szOperationName+" ] 베드 3개정보 광폭 변경" + szYdStkColWGp ;
						 ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
							
						 
						 recPara.setField("YD_STK_BED_NO", "01");
						 recPara.setField("YD_STK_BED_W_GP", szYdStkColWGp);
						 recPara.setField("YD_STK_BED_W_MAX", new Integer(YdConstant.STKBED_W_GP_L));
						 recPara.setField("YD_STK_BED_H_MAX", new Integer(YdConstant.STKBED_H_GP_L));
						 
						 ydUtils.displayRecord(szOperationName, recPara);
						 nRtnVal = ydStkBedDao.updYdStkbed(recPara, 0);
						 
						 
						 recPara.setField("YD_STK_BED_NO", "02");
						 recPara.setField("YD_STK_BED_W_GP", szYdStkColWGp);
						 recPara.setField("YD_STK_BED_W_MAX", new Integer(YdConstant.STKBED_W_GP_L));
						 recPara.setField("YD_STK_BED_H_MAX", new Integer(YdConstant.STKBED_H_GP_L));
						 ydUtils.displayRecord(szOperationName, recPara);
						 nRtnVal = ydStkBedDao.updYdStkbed(recPara, 0);
						 
//DONG_INSERT						 
						 recPara.setField("YD_STK_BED_NO", "03");
						 recPara.setField("YD_STK_BED_W_GP", szYdStkColWGp);
						 recPara.setField("YD_STK_BED_W_MAX", new Integer(YdConstant.STKBED_W_GP_L));
						 recPara.setField("YD_STK_BED_H_MAX", new Integer(YdConstant.STKBED_H_GP_L));
						 ydUtils.displayRecord(szOperationName, recPara);
						 nRtnVal = ydStkBedDao.updYdStkbed(recPara, 0);
						 
						 //(+)L2 로 적치열 수정된 정보를 내려보내준다.
						 recColPara =  JDTORecordFactory.getInstance().create();
						 recColPara.setField("MSG_ID", "YDY4L001");
						 recColPara.setField("YD_INFO_SYNC_CD", "3");
						 recColPara.setField("YD_GP", YdConstant.YD_GP_PLATE_GDS_YARD);
						 recColPara.setField("YD_STK_COL_GP", szYdStkColGp);
						
						 szLogMsg = "[Jsp-Session "+szOperationName+" ] L2 로 적치열 수정된 정보 송신 : " ;
						 ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						 
						 ydUtils.displayRecord(szOperationName, recColPara);
						 ydDelegate.sendMsg(recColPara);
						 
						 
						 
						 
						// + 1 열의 정보를 사용불가로 변경한다.
						 
						 recColPara =  JDTORecordFactory.getInstance().create();
						 recColPara.setField("YD_STK_COL_GP", szYdStkColGpNext);	
						 recColPara.setField("YD_STK_COL_ACT_STAT", "N");
						 recColPara.setField("MODIFIER",ydDaoUtils.paraRecChkNull(inParaRec, "YD_USER_ID") );
						 
						 
						 nRtnVal =  ydStkColDao.updYdStkcol(recColPara, 0);
						 
						 if(nRtnVal <= 0){
							szLogMsg = "[Jsp-Session "+szOperationName+" ] 해당 열을 비활성로 변경 할 수  없습니다.:" + szYdStkColGpNext ;
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						 }
							
						 
						 
						 //(+)L2 로 적치열 수정된 정보를 내려보내준다.
						 recColPara =  JDTORecordFactory.getInstance().create();
						 recColPara.setField("MSG_ID", "YDY4L001");
						 recColPara.setField("YD_INFO_SYNC_CD", "3");
						 recColPara.setField("YD_GP", YdConstant.YD_GP_PLATE_GDS_YARD);
						 recColPara.setField("YD_STK_COL_GP", szYdStkColGpNext);
						
						 szLogMsg = "[Jsp-Session "+szOperationName+" ] L2 로 적치열 수정된 정보 송신 : " ;
						 ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						 
						 ydUtils.displayRecord(szOperationName, recColPara);
						 ydDelegate.sendMsg(recColPara);
						 
						
							
						 
					 } else if(szYdStkColWGp.equals("M") ){
						 //5. 중폭인 경우
						 
						 String GrowFlag = "";
						 
							
						 
						 	if(szYdStkColNo.equals("01")  ){
						 		 GrowFlag = "R";
						 		
						 	}else{
							 	//초기화 
								 recTempPara = JDTORecordFactory.getInstance().create();
								 rsetStkColBefoInfo = JDTORecordFactory.getInstance().createRecordSet("rsetStkColBefoInfo");
								 recStkColBefoInfo = JDTORecordFactory.getInstance().create();
								 
								 
								 recTempPara.setField("YD_STK_COL_GP", szYdStkColGpBefo);
								 nRtnVal = ydStkColDao.getYdStkcol(recTempPara, rsetStkColBefoInfo, 0);
								 
								 if(nRtnVal<= 0){
									 //기존 베드가 존재 하지 않는경우 (00베드 또는 없는 베드일경우는 >> 증가 방향)
									 GrowFlag = "R";
								 }else{
									 rsetStkColBefoInfo.first();
									 recStkColBefoInfo = rsetStkColBefoInfo.getRecord();
									 
									 if(ydDaoUtils.paraRecChkNull(recStkColBefoInfo, "YD_STK_COL_ACT_STAT").equals("L")){
										 GrowFlag = "R";
									 } else{
										 GrowFlag = "L";
										 
									 }
								 }
							 
						 	}
						 	
						 	szLogMsg = "[Jsp-Session "+szOperationName+" ] 선택된 방향 구분." + GrowFlag ;
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						 	
						 	
						 	if(GrowFlag.equals("R")){
						 		 // 1열이거나 -1 열이 사용가능인 경우 
						 		
							 	// 열 정보의 폭구분을 중폭으로 변경한다.
						 			//초기화 
								 recPara = JDTORecordFactory.getInstance().create();
								 recPara.setField("YD_STK_COL_GP", szYdStkColGp);
								 recPara.setField("MODIFIER", ydDaoUtils.paraRecChkNull(inParaRec, "YD_USER_ID"));
								 recPara.setField("YD_STK_COL_W_GP", szYdStkColWGp);	
								 recPara.setField("YD_STK_COL_H_MAX",new Integer(YdConstant.STKBED_H_GP_M));
								 
								 
								 nRtnVal =  ydStkColDao.updYdStkcol(recPara, 0);
								 
								 if(nRtnVal <=0){
									 // 열 정보 변경 실패시 
									szLogMsg = "[Jsp-Session "+szOperationName+" ] 중폭 변경 UPDATE 중 ERROR 발생." + szYdStkColWGp ;
									ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
									throw new DAOException("광폭 변경 UPDATE 중 ERROR 발생.");
								 }
								 	
								 
								// 베드의 폭 값을 중폭으로 세팅한다.	
								 
								 szLogMsg = "[Jsp-Session "+szOperationName+" ] 베드 3개정보 중폭 변경" + szYdStkColWGp ;
								 ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
									
								 
								 recPara.setField("YD_STK_BED_NO", "01");
								 recPara.setField("YD_STK_BED_W_GP", szYdStkColWGp);
								 recPara.setField("YD_STK_BED_W_MAX", new Integer(YdConstant.STKBED_W_GP_M));
								 recPara.setField("YD_STK_BED_H_MAX", new Integer(YdConstant.STKBED_H_GP_M));
								 
								 ydUtils.displayRecord(szOperationName, recPara);
								 nRtnVal = ydStkBedDao.updYdStkbed(recPara, 0);
								 
								 
								 recPara.setField("YD_STK_BED_NO", "02");
								 recPara.setField("YD_STK_BED_W_GP", szYdStkColWGp);
								 recPara.setField("YD_STK_BED_W_MAX", new Integer(YdConstant.STKBED_W_GP_M));
								 recPara.setField("YD_STK_BED_H_MAX", new Integer(YdConstant.STKBED_H_GP_M));
								 ydUtils.displayRecord(szOperationName, recPara);
								 nRtnVal = ydStkBedDao.updYdStkbed(recPara, 0);
								 
								 
								 recPara.setField("YD_STK_BED_NO", "03");
								 recPara.setField("YD_STK_BED_W_GP", szYdStkColWGp);
								 recPara.setField("YD_STK_BED_W_MAX", new Integer(YdConstant.STKBED_W_GP_M));
								 recPara.setField("YD_STK_BED_H_MAX", new Integer(YdConstant.STKBED_H_GP_M));
								 ydUtils.displayRecord(szOperationName, recPara);
								 nRtnVal = ydStkBedDao.updYdStkbed(recPara, 0);
						 		
								 
								 //(+)L2 로 적치열 수정된 정보를 내려보내준다.
								 
								 recColPara =  JDTORecordFactory.getInstance().create();
								 recColPara.setField("MSG_ID", "YDY4L001");
								 recColPara.setField("YD_INFO_SYNC_CD", "3");
								 recColPara.setField("YD_GP", YdConstant.YD_GP_PLATE_GDS_YARD);
								 recColPara.setField("YD_STK_COL_GP", szYdStkColGp);
								
								 szLogMsg = "[Jsp-Session "+szOperationName+" ] L2 로 적치열 수정된 정보 송신 : " ;
								 ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
								 
								 ydUtils.displayRecord(szOperationName, recColPara);
								 ydDelegate.sendMsg(recColPara);
								 
								 
						 		
							 	
								// + 1 열의 정보를 사용불가로 변경한다.
								 recColPara =  JDTORecordFactory.getInstance().create();
								 recColPara.setField("YD_STK_COL_GP", szYdStkColGpNext);	
								 recColPara.setField("YD_STK_COL_ACT_STAT", "N");
								 recColPara.setField("MODIFIER",ydDaoUtils.paraRecChkNull(inParaRec, "YD_USER_ID") );
								 
								 
								 nRtnVal =  ydStkColDao.updYdStkcol(recColPara, 0);
								 
								 if(nRtnVal <= 0){
									szLogMsg = "[Jsp-Session "+szOperationName+" ] 해당 열을 비활성로 변경 할 수  없습니다.:" + szYdStkColGpNext ;
									ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
								 }
								 
								 //(+)L2 로 적치열 수정된 정보를 내려보내준다.
								 recColPara =  JDTORecordFactory.getInstance().create();
								 recColPara.setField("MSG_ID", "YDY4L001");
								 recColPara.setField("YD_INFO_SYNC_CD", "3");
								 recColPara.setField("YD_GP", YdConstant.YD_GP_PLATE_GDS_YARD);
								 recColPara.setField("YD_STK_COL_GP", szYdStkColGpNext);
								
								 szLogMsg = "[Jsp-Session "+szOperationName+" ] L2 로 적치열 수정된 정보 송신 : " ;
								 ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
								 
								 ydUtils.displayRecord(szOperationName, recColPara);
								 ydDelegate.sendMsg(recColPara);
								 
								 
								 
							 		// END
								 return szRtnMsg;
								 
						 	}else if(GrowFlag.equals("L")){
					 			// 열 정보의 폭구분을 중폭으로 변경한다.
					 			//초기화 
								 recPara = JDTORecordFactory.getInstance().create();
								 recPara.setField("YD_STK_COL_GP", szYdStkColGp);
								 recPara.setField("MODIFIER", ydDaoUtils.paraRecChkNull(inParaRec, "YD_USER_ID"));
								 recPara.setField("YD_STK_COL_W_GP", szYdStkColWGp);	 
								 recPara.setField("YD_STK_COL_H_MAX",new Integer(YdConstant.STKBED_H_GP_L));
								 
								 
								 nRtnVal =  ydStkColDao.updYdStkcol(recPara, 0);
								 
								 if(nRtnVal <=0){
									 // 열 정보 변경 실패시 
									szLogMsg = "[Jsp-Session "+szOperationName+" ] 중폭 변경 UPDATE 중 ERROR 발생." + szYdStkColWGp ;
									ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
									throw new DAOException("광폭 변경 UPDATE 중 ERROR 발생.");
								 }
								 	
								 
								// 베드의 폭값 및 기준 X 좌표를 열 X 기준에서 - 중폭 기준 값 만큼 이동 시킨다.	
							    // 열 기준 X - (중폭-소폭)	
							
								 szLogMsg = "[Jsp-Session "+szOperationName+" ] 베드 3개정보 중폭 변경" + szYdStkColWGp ;
								 ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
									
								 
								 recPara.setField("YD_STK_BED_NO", "01");
								 recPara.setField("YD_STK_BED_W_GP", szYdStkColWGp);
								 recPara.setField("YD_STK_BED_W_MAX", new Integer(YdConstant.STKBED_W_GP_M));
								 recPara.setField("YD_STK_BED_XAXIS", 
										 new Integer(Integer.parseInt(ydDaoUtils.paraRecChkNull(recStkColInfo, "YD_STK_COL_RULE_XAXIS") ) -(YdConstant.STKBED_W_GP_M - YdConstant.STKBED_W_GP_S)));
								 
								 recPara.setField("YD_STK_BED_H_MAX", new Integer(YdConstant.STKBED_H_GP_M));
										 
								 ydUtils.displayRecord(szOperationName, recPara);
								 nRtnVal = ydStkBedDao.updYdStkbed(recPara, 0);
								 
								 
								 recPara.setField("YD_STK_BED_NO", "02");
								 recPara.setField("YD_STK_BED_W_GP", szYdStkColWGp);
								 recPara.setField("YD_STK_BED_W_MAX", new Integer(YdConstant.STKBED_W_GP_M));
								 recPara.setField("YD_STK_BED_XAXIS", 
										 new Integer(Integer.parseInt(ydDaoUtils.paraRecChkNull(recStkColInfo, "YD_STK_COL_RULE_XAXIS") ) -(YdConstant.STKBED_W_GP_M - YdConstant.STKBED_W_GP_S)));
								
								 ydUtils.displayRecord(szOperationName, recPara);
								 recPara.setField("YD_STK_BED_H_MAX", new Integer(YdConstant.STKBED_H_GP_M));
								 nRtnVal = ydStkBedDao.updYdStkbed(recPara, 0);
								 
								 
								 recPara.setField("YD_STK_BED_NO", "03");
								 recPara.setField("YD_STK_BED_W_GP", szYdStkColWGp);
								 recPara.setField("YD_STK_BED_W_MAX", new Integer(YdConstant.STKBED_W_GP_M));
								 recPara.setField("YD_STK_BED_XAXIS", 
										 new Integer(Integer.parseInt(ydDaoUtils.paraRecChkNull(recStkColInfo, "YD_STK_COL_RULE_XAXIS") ) -(YdConstant.STKBED_W_GP_M - YdConstant.STKBED_W_GP_S)));
								 recPara.setField("YD_STK_BED_H_MAX", new Integer(YdConstant.STKBED_H_GP_M));
								
								 ydUtils.displayRecord(szOperationName, recPara);
								 nRtnVal = ydStkBedDao.updYdStkbed(recPara, 0);
								 
								 
								 //(+)L2 로 적치열 수정된 정보를 내려보내준다.
								 recColPara =  JDTORecordFactory.getInstance().create();
								 recColPara.setField("MSG_ID", "YDY4L001");
								 recColPara.setField("YD_INFO_SYNC_CD", "3");
								 recColPara.setField("YD_GP", YdConstant.YD_GP_PLATE_GDS_YARD);
								 recColPara.setField("YD_STK_COL_GP", szYdStkColGp);
								
								 szLogMsg = "[Jsp-Session "+szOperationName+" ] L2 로 적치열 수정된 정보 송신 : " ;
								 ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
								 
								 ydUtils.displayRecord(szOperationName, recColPara);
								 ydDelegate.sendMsg(recColPara);
								 
							 	
						 	}
					 }
					 
					 szRtnMsg = YdConstant.RETN_CD_SUCCESS;
					 return szRtnMsg;
				
				 
				 }
				 // 3. 소폭으로 변경
				 
				 else if(szYdStkColWGp.equals("S")){
					 // 열 정보의 폭 구분을 소폭으로 변경한다.
					
					 
					 recPara = JDTORecordFactory.getInstance().create();
					 recPara.setField("YD_STK_COL_GP", szYdStkColGp);
					 recPara.setField("MODIFIER", ydDaoUtils.paraRecChkNull(inParaRec, "YD_USER_ID"));
					 recPara.setField("YD_STK_COL_W_GP", szYdStkColWGp);
					 recPara.setField("YD_STK_COL_H_MAX",new Integer(YdConstant.STKBED_H_GP_S));
					 
					 
					 nRtnVal =  ydStkColDao.updYdStkcol(recPara, 0);
					 
					 if(nRtnVal <=0){
						 // 열 정보 변경 실패시 
						szLogMsg = "[Jsp-Session "+szOperationName+" ] 소폭 변경 UPDATE 중 ERROR 발생." + szYdStkColWGp ;
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						throw new DAOException("소폭 변경 UPDATE 중 ERROR 발생.");
					 }
					 	
						
					// 베드정보의 폭 길이를 소폭 값으로 변경한다.
					 // 베드 정보의 기준 X좌표를 열 기준값으로 덮어 쓴다.
					 szLogMsg = "[Jsp-Session "+szOperationName+" ] 베드 3개정보 중폭 변경" + szYdStkColWGp ;
					 ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						
					 
					 recPara.setField("YD_STK_BED_NO", "01");
					 recPara.setField("YD_STK_BED_W_GP", szYdStkColWGp);
					 recPara.setField("YD_STK_BED_W_MAX", new Integer(YdConstant.STKBED_W_GP_S));
					 recPara.setField("YD_STK_BED_XAXIS", ydDaoUtils.paraRecChkNull(recStkColInfo, "YD_STK_COL_RULE_XAXIS") );
					 recPara.setField("YD_STK_BED_H_MAX", new Integer(YdConstant.STKBED_H_GP_S));
							 
					 
					 ydUtils.displayRecord(szOperationName, recPara);
					 nRtnVal = ydStkBedDao.updYdStkbed(recPara, 0);
					 
					 
					 recPara.setField("YD_STK_BED_NO", "02");
					 recPara.setField("YD_STK_BED_W_GP", szYdStkColWGp);
					 recPara.setField("YD_STK_BED_W_MAX", new Integer(YdConstant.STKBED_W_GP_S));
					 recPara.setField("YD_STK_BED_XAXIS", ydDaoUtils.paraRecChkNull(recStkColInfo, "YD_STK_COL_RULE_XAXIS") );
					 recPara.setField("YD_STK_BED_H_MAX", new Integer(YdConstant.STKBED_H_GP_S));
					 ydUtils.displayRecord(szOperationName, recPara);
					 nRtnVal = ydStkBedDao.updYdStkbed(recPara, 0);
					 
					 
					 recPara.setField("YD_STK_BED_NO", "03");
					 recPara.setField("YD_STK_BED_W_GP", szYdStkColWGp);
					 recPara.setField("YD_STK_BED_W_MAX", new Integer(YdConstant.STKBED_W_GP_S));
					 recPara.setField("YD_STK_BED_XAXIS", ydDaoUtils.paraRecChkNull(recStkColInfo, "YD_STK_COL_RULE_XAXIS") );
					 recPara.setField("YD_STK_BED_H_MAX", new Integer(YdConstant.STKBED_H_GP_S));
					 ydUtils.displayRecord(szOperationName, recPara);
					 nRtnVal = ydStkBedDao.updYdStkbed(recPara, 0);
					 

					 //(+)L2 로 적치열 수정된 정보를 내려보내준다.
					 recColPara =  JDTORecordFactory.getInstance().create();
					 recColPara.setField("MSG_ID", "YDY4L001");
					 recColPara.setField("YD_INFO_SYNC_CD", "3");
					 recColPara.setField("YD_GP", YdConstant.YD_GP_PLATE_GDS_YARD);
					 recColPara.setField("YD_STK_COL_GP", szYdStkColGp);
					
					 szLogMsg = "[Jsp-Session "+szOperationName+" ] L2 로 적치열 수정된 정보 송신 : " ;
					 ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					 
					 ydUtils.displayRecord(szOperationName, recColPara);
					 ydDelegate.sendMsg(recColPara);
					 
					 
					 return szRtnMsg;
					 
				 }
				
				
			}catch(Exception e){
				szLogMsg = "[JSP Session "+ szOperationName + "] :"+ e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				throw new DAOException(getClass().getName() + e.getMessage(),e);
			}
			
			szLogMsg = "[Jsp-Session "+szOperationName+" ] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			return szRtnMsg;
		}
		

		
		
		/**
		 *  대차 작업 예약 정보를 조회
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return JDTORecordSet
		 * @throws DAOException
		 */
		public JDTORecordSet getWorkBookMtlByTcarSchWBookID(JDTORecord inDto) throws DAOException {
			// DAO 및 UTIL 객체
			
			YdWrkbookMtlDao  ydWrkbookMtlDao = new YdWrkbookMtlDao(); 

			// 레코드 선언
			JDTORecord recPara      = null;
			JDTORecordSet outRecSet = null;
			
			// 변수 선언
			String szMethodName     = "getWorkBookMtlByTcarSchWBookID";
			String szOperationName  = "대차 작업 예약 정보를 조회";
			String szMsg            = "";		
			int nRet                = 0;
			
			try {
								
				szMsg = "JSP-SESSION  ["+szOperationName+"]시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				

				// 레코드 생성
				recPara   = JDTORecordFactory.getInstance().create();
				outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
				
				//
				recPara.setField("YD_WBOOK_ID", yddatautil.setDataDefault(inDto.getField("YD_WBOOK_ID"), ""));
				
				nRet = ydWrkbookMtlDao.getYdWrkbookmtl(recPara, outRecSet, 39);
				if(nRet <= 0) {
					if(nRet == -1) {
						szMsg = "routine error!!!, ErrorCode:" + nRet;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + nRet;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					return outRecSet;
				} 
				
			} catch(Exception e) {
				// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
				throw new DAOException(getClass().getName() + e.getMessage(),e);
			} finally {
			}
			szMsg = "JSP-SESSION  ["+szOperationName+"] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			
			return outRecSet;
		}//end of getComboCodeList

		
		/**
		 *  대차 작업 예약 정보를 조회(COIL)
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return JDTORecordSet
		 * @throws DAOException
		 */
		public JDTORecordSet getWorkBookMtlByTcarSchWBookID2(JDTORecord inDto) throws DAOException {
			// DAO 및 UTIL 객체
			
			YdWrkbookMtlDao  ydWrkbookMtlDao = new YdWrkbookMtlDao(); 

			// 레코드 선언
			JDTORecord recPara      = null;
			JDTORecordSet outRecSet = null;
			
			// 변수 선언
			String szMethodName     = "getWorkBookMtlByTcarSchWBookID2";
			String szOperationName  = "대차 작업 예약 정보를 조회";
			String szMsg            = "";		
			int nRet                = 0;
			
			try {
								
				szMsg = "JSP-SESSION  ["+szOperationName+"]시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				

				// 레코드 생성
				recPara   = JDTORecordFactory.getInstance().create();
				outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
				
				//
				recPara.setField("EQP_CHK", yddatautil.setDataDefault(inDto.getField("EQP_CHK"), ""));
				/*com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookMtlByTcarSchWBookID2*/
				nRet = ydWrkbookMtlDao.getYdWrkbookmtl(recPara, outRecSet, 307);
				if(nRet <= 0) {
					if(nRet == -1) {
						szMsg = "routine error!!!, ErrorCode:" + nRet;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + nRet;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					return outRecSet;
				} 
				
			} catch(Exception e) {
				// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
				throw new DAOException(getClass().getName() + e.getMessage(),e);
			} finally {
			}
			szMsg = "JSP-SESSION  ["+szOperationName+"] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			
			return outRecSet;
		}//end of getWorkBookMtlByTcarSchWBookID2
		
		
		/**
		 *  야드값(YD_GP1, YD_GP2)으로 설비ID와   설비명 조회  
		 *  
		 *  권오창
		 *  2009.11.11
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return JDTORecordSet
		 * @throws DAOException
		 */
		public JDTORecordSet getEqpIDEqpNameList(JDTORecord inDto) throws DAOException {
			// DAO 및 UTIL 객체
			YdEqpDao ydEqpDao       = new YdEqpDao();

			// 레코드 선언
			JDTORecord recPara      = null;
			JDTORecordSet outRecSet = null;
			
			// 변수 선언
			String szMethodName     = "getEqpIDEqpNameList";
			String szMsg            = "";		
			int nRet                = 0;
			
			try {
				szMsg = "JSP-SESSION [ 설비 목록 조회]시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	

				// 레코드 생성
				recPara   = JDTORecordFactory.getInstance().create();
				outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
				
				//
				recPara.setField("YD_GP1", yddatautil.setDataDefault(inDto.getField("YD_GP1"), ""));
				recPara.setField("YD_GP2", yddatautil.setDataDefault(inDto.getField("YD_GP2"), ""));
				nRet = ydEqpDao.getYdEqp(recPara, outRecSet, 13);
				if(nRet <= 0) {
					if(nRet == -1) {
						szMsg = "routine error!!!, ErrorCode:" + nRet;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + nRet;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					return outRecSet;
				} 
				
			} catch(Exception e) {
				// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
				throw new DAOException(getClass().getName() + e.getMessage(),e);
			} finally {
			}
			
			szMsg = "JSP-SESSION [ 설비 목록 조회] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			return outRecSet;
		}
		
		
		

		
		/**
		 *  야드구분(YD_GP1, YD_GP2)과 동구분(YD_BAY_GP)으로 설비ID와   설비명 조회  
		 *  
		 *  권오창
		 *  2009.11.18
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return JDTORecordSet
		 * @throws DAOException
		 */
		public JDTORecordSet getEqpIDEqpNameListYdGpYdBayGp(JDTORecord inDto) throws DAOException {
			// DAO 및 UTIL 객체
			YdEqpDao ydEqpDao       = new YdEqpDao();

			// 레코드 선언
			JDTORecord recPara      = null;
			JDTORecordSet outRecSet = null;
			
			// 변수 선언
			String szMethodName     = "getEqpIDEqpNameListYdGpYdBayGp";
			String szMsg            = "";		
			int nRet                = 0;
			
			try {
				szMsg = "JSP-SESSION [ 설비 목록 조회]시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	

				// 레코드 생성
				recPara   = JDTORecordFactory.getInstance().create();
				outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
				
				//
				recPara.setField("YD_GP1"   , yddatautil.setDataDefault(inDto.getField("YD_GP1"), ""));
				recPara.setField("YD_BAY_GP", yddatautil.setDataDefault(inDto.getField("YD_BAY_GP"), ""));
				recPara.setField("YD_GP2"   , yddatautil.setDataDefault(inDto.getField("YD_GP2"), ""));
				nRet = ydEqpDao.getYdEqp(recPara, outRecSet, 15);
				if(nRet <= 0) {
					if(nRet == -1) {
						szMsg = "routine error!!!, ErrorCode:" + nRet;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + nRet;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					return outRecSet;
				} 
				
			} catch(Exception e) {
				// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
				throw new DAOException(getClass().getName() + e.getMessage(),e);
			} finally {
			}
			
			szMsg = "JSP-SESSION [ 설비 목록 조회] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			return outRecSet;
		}

		
		
		
		
		/**
		 *  대차 작업 예약 순서 변경
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return JDTORecordSet
		 * @throws DAOException
		 */
		public String tCarSchChangeSequence(JDTORecord[] inDto) throws DAOException {
			
			
			int nRtnVal  = 0;
			String szMethodName       = "tCarSchChangeSequence";
			String szOperationName = " 대차 작업 예약 순서 변경";
			String szLogMsg           = "";
			String szRtnMsg = YdConstant.RETN_CD_SUCCESS;
			
			JDTORecord recGetPara  = null;
			JDTORecord recPara      = null;
				
			//작업예약 TABLE
			YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		
			try{
				
				szLogMsg = "[Jsp-Session] "+szOperationName + "시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
				
				for(int x=0;x<inDto.length;x++){
					
					//  대차 작업 예약 순서 변경
					
					recPara   = JDTORecordFactory.getInstance().create();
					recGetPara = JDTORecordFactory.getInstance().create();
					
					recGetPara =  inDto[x];
					 
					recPara.setField("YD_WBOOK_ID", ydDaoUtils.paraRecChkNull(recGetPara, "YD_WBOOK_ID") );
					recPara.setField("YD_SCH_PRIOR", ydDaoUtils.paraRecChkNull(recGetPara, "YD_SCH_PRIOR") );
					recPara.setField("MODIFIER", ydDaoUtils.paraRecChkNull(recGetPara, "YD_USER_ID") );
					 
					//UPDATE					
					ydUtils.displayRecord(szOperationName, recPara);
					 
					 
					 nRtnVal = ydWrkbookDao.updYdWrkbook(recPara, 0);
					
					if(nRtnVal < 0 ) {					
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						throw new DAOException(YdConstant.RETN_CD_FAILURE);
					}
				}
				
			}catch(DAOException e){
				szLogMsg = "[Jsp-Session] "+szOperationName + "UPDATE 실패!!";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);	
				
				throw e;
				
			}catch(Exception e){
				throw new DAOException(getClass().getName() + e.getMessage(),e);
			}
			
			
			
			szLogMsg = "[Jsp-Session] "+szOperationName + "시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			return szRtnMsg;
		} // end of tCarSchChangeSequence
		
		
		
		
		/**
		 *  대차 작업 취소 
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return JDTORecordSet
		 * @throws DAOException
		 */
		public String tCarCancleWBook(JDTORecord[] inDto) throws DAOException {
			/*
			 * 1. 해당 대차작업의  작업예약 정보가 크레인 스케줄에 편성되어 있는지 판단한다.
			 * 2. 크레인 스케줄 정보에 편성되어 있으면 작업취소할수 없다고 판단하고 Exception (작업취소 불가) 라고 RETURN 한다.
			 * 3. 크레인 스케줄 정보에 편성되지 않았을 경우에는 작업예약 및 작업 예약재료를 삭제 (DEL_YN = 'Y' SETTING) 한다.
			 * 4. 작업완료 유무를 RETURN 한다.
			 */
			
			int nRtnVal = 0;
			String szMethodName = "tCarCancleWBook";
			String szOperationName = " 대차 작업 취소";
			String szLogMsg = "";
			String szRtnMsg = YdConstant.RETN_CD_SUCCESS;
			
			String szWBookId = null;
			String szYdSchCd = null;
			
			
			JDTORecord recGetPara = null;
			JDTORecord recPara = null;
			JDTORecord recTcarInfo = null;
			JDTORecordSet outRecSet = null;
			
			
				
		
			YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
			YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
			YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
			YdTcarSchDao ydTcarSchDao  = new YdTcarSchDao();
			
			try{
				
				szLogMsg = "[Jsp-Session] "+szOperationName + "시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
				
				for(int x=0;x<inDto.length;x++){
					
				//  1. 해당 대차작업의  작업예약 정보가 크레인 스케줄에 편성되어 있는지 판단한다.
					recGetPara = inDto[x];
					szWBookId = ydDaoUtils.paraRecChkNull(recGetPara, "YD_WBOOK_ID");
					
					if(szWBookId.trim().equals("")){
						//대차 작업 의 예약 정보가 올바르지 않을경우
						
						
						szLogMsg = "[Jsp-Session] "+szOperationName + ": 작업예약정보가 맞지않습니다.";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						
						
						szRtnMsg = YdConstant.RETN_CD_NO_PARAM;
						
						//DAO Exception 발생해야할지 판단해야함
						
						
						return szRtnMsg;
					}
					
					// 2010.01.08  
				
					// A. 해당 작업 스케줄 코드가 상차작업인지 하차작업인지 판단한다.
					
					szYdSchCd = ydDaoUtils.paraRecChkNull(recGetPara, "YD_SCH_CD");
					
					szLogMsg = "[Jsp-Session] "+szOperationName + ": 스케줄 코드. [" + szYdSchCd +"]";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					
					if(szYdSchCd.equals("") || szYdSchCd.length() != 8){
						szLogMsg = "[Jsp-Session] "+szOperationName + ": 스케줄 코드가 올바르지 않습니다. [" + szYdSchCd +"]";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						
						szRtnMsg = "스케줄 코드가 올바르지 않습니다 .";
						return szRtnMsg;
					}
					
					
					
					
					
					recPara   = JDTORecordFactory.getInstance().create();
					recTcarInfo = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_EQP_ID", ydDaoUtils.paraRecChkNull(recGetPara, "YD_WRK_PLAN_TCAR"));
					outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
					
					nRtnVal = ydTcarSchDao.getYdTcarsch(recPara, outRecSet, 4);
					
				
					
					if(nRtnVal < 0 ){
						szLogMsg = "[Jsp-Session] "+szOperationName + "해당 대차 스케줄 조회시 ERROR.";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						
						szRtnMsg = "해당 대차 스케줄 조회시 ERROR.";
						//DAO Exception 발생해야할지 판단해야함
						
						return szRtnMsg;
					}else if( nRtnVal == 0 ){
						szLogMsg = "[Jsp-Session] "+szOperationName + "대차 스케줄 조회된 데이터가 없습니다.";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.WARNING);
												
					}else {
						
						szLogMsg = "[Jsp-Session] "+szOperationName + "대차 스케줄 조회 성공.";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
						
						outRecSet.first();
						recTcarInfo = outRecSet.getRecord();
						
					}
					
					
					
					// A-1  상차 작업인경우 대차스케줄에 상차 작업예약이 같은것이 편성되어있는지 확인한다.
					
					if(szYdSchCd.substring(6, 7).equals("U") ){
										
						if( szWBookId.equals(ydDaoUtils.paraRecChkNull(recTcarInfo, "YD_CARLD_WRK_BOOK_ID"))) {
							szLogMsg = "[Jsp-Session] "+szOperationName + " 상차작업예약 되어 있어 삭제 할수 없습니다.";
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
							
							szRtnMsg = ":상차작업예약 되어 있어 삭제 할수 없습니다.";
							return szRtnMsg;
							
						}
						
					}
					
					// A-2  하차 작업인경우 대차스케줄에 하차 작업예약이 같은것이 편성되어 있는지 확인한다.
					
					if(szYdSchCd.substring(6, 7).equals("L") ){
						if( szWBookId.equals(ydDaoUtils.paraRecChkNull(recTcarInfo, "YD_CARUD_WRK_BOOK_ID"))) {
							szLogMsg = "[Jsp-Session] "+szOperationName + " 하차작업예약 되어 있어 삭제 할수 없습니다.";
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);

							szRtnMsg = "하차 작업예약 되어 있어 삭제 할수 없습니다.";
							return szRtnMsg;
						}				
						
					}

					
					
					// 2. 크레인 스케줄 정보에 편성되어 있으면 작업취소할수 없다고 판단하고 Exception (작업취소 불가) 라고 RETURN 한다.
					recPara   = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_WBOOK_ID", szWBookId);
					
					outRecSet = JDTORecordFactory.getInstance().createRecordSet("Yd");
					nRtnVal = ydCrnSchDao.getYdCrnsch(recPara, outRecSet, 28);
					
					if(nRtnVal > 0 ){
						szLogMsg = "[Jsp-Session] "+szOperationName + ":크레인 스케줄이 생성되어 삭제 할수 없습니다.!!";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						
						szRtnMsg = YdConstant.RETN_CD_EXIST;
						//DAO Exception 발생해야할지 판단해야함
						
						return szRtnMsg;
						
					}else if (nRtnVal < 0 ){
						
						szLogMsg = "[Jsp-Session] "+szOperationName + ":크레인 스케줄 작업예약 ID로 조회시 ERROR!!";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						
						szRtnMsg = YdConstant.RETN_CD_FAILURE;
						//DAO Exception 발생해야할지 판단해야함
						
						return szRtnMsg;
						
						
					}
					
					
					// 3. 크레인 스케줄 정보에 편성되지 않았을 경우에는 작업예약 및 작업 예약재료를 삭제 (DEL_YN = 'Y' SETTING) 한다.
					
					
					//작업예약 재료 삭제
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_WBOOK_ID", szWBookId);
					//recPara.setField("MODIFIER",ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER"));
					recPara.setField("MODIFIER", recGetPara.getField("YD_USER_ID"));
					recPara.setField("DEL_YN","Y");		
					nRtnVal = ydWrkbookMtlDao.updYdWrkbookmtl1(recPara);
					
					if(nRtnVal < 0){
						szLogMsg = "작업예약재료  삭제시 오류 발생 ";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						
						
						szRtnMsg = szLogMsg;
						new DAOException(szRtnMsg);
						return szLogMsg;	
					}
					
					
					//작업예약 삭제
					
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_WBOOK_ID", szWBookId);
					recPara.setField("MODIFIER", recGetPara.getFieldString("YD_USER_ID") );
					recPara.setField("DEL_YN","Y");		
					nRtnVal = ydWrkbookDao.updYdWrkbook(recPara, 0);
					
					if(nRtnVal < 0){
						szLogMsg ="작업예약  삭제시 오류 발생 ";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg , YdConstant.ERROR);
						
						szRtnMsg = szLogMsg;
						new DAOException(szRtnMsg);
						return szRtnMsg;	
					}
					
					
				}
				
			}catch(DAOException e){
				szLogMsg = "[Jsp-Session] "+szOperationName + "UPDATE 실패!!";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);	
				
				throw e;
				
			}catch(Exception e){
				throw new DAOException(getClass().getName() + e.getMessage(),e);
			}
			
			// 4. 작업완료 유무를 RETURN 한다.
			
			szLogMsg = "[Jsp-Session] "+szOperationName + "시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			return szRtnMsg;
		} // end of tCarSchChangeSequence
		
		
		/**
		 * 대차스케줄삭제 - 대차초기화
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return JDTORecordSet
		 * @throws DAOException
		 */
		public String delTcarSch(JDTORecord[] inDto) throws DAOException {
			String szMethodName 			= "delTcarSch";
			String szOperationName 			= "대차스케줄삭제:대차초기화";
			String szLogMsg 				= "";
			String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
			int	intRtnVal					= -100;
			
			YdTcarSchDao	ydTcarSchDao	= new YdTcarSchDao();
			YdStkBedDao		ydStkBedDao		= new YdStkBedDao();
			YdStkLyrDao		ydStkLyrDao		= new YdStkLyrDao();
			
			String szYD_EQP_ID				= null;
			String szRTN_MSG				= null;
			String szYD_USER_ID				= null;
			String szYD_STK_BED_ACT_STAT	= null;
			String szYD_CARLD_STOP_LOC		= null;
			String szYD_CURR_BAY_GP			= null;
			String szEQP_YD_CURR_BAY_GP		= null;
			String szYD_TCAR_SCH_ID			= null;
			
			JDTORecord			recPara		= null;
			JDTORecord			recTemp		= null;
			JDTORecord			recInTemp	= null;
			JDTORecordSet		rsResult	= null;
			
			try{
				
				szLogMsg = "[Jsp-Session : "+szOperationName + "] ------------------ 메소드 시작 ------------------";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				recPara				= JDTORecordFactory.getInstance().create();
				
				for(int x=0;x<inDto.length;x++){
					szYD_EQP_ID					= ydDaoUtils.paraRecChkNull(inDto[x], "YD_EQP_ID");
					szYD_USER_ID				= ydDaoUtils.paraRecChkNull(inDto[x], "YD_USER_ID");
					szYD_CURR_BAY_GP			= ydDaoUtils.paraRecChkNull(inDto[x], "YD_CURR_BAY_GP");
					
					
					//--------------------------------------------------------------------------------------------------------
					//	대차설비로 대차스케줄 조회
					//--------------------------------------------------------------------------------------------------------
					
					szLogMsg = "[Jsp-Session : "+szOperationName + "] 대차설비ID["+szYD_EQP_ID+"]에 해당하는 대차스케줄 조회 시작";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					rsResult			= JDTORecordFactory.getInstance().createRecordSet("");
					
					recPara.setField("YD_EQP_ID", 			szYD_EQP_ID);
					
					szRTN_MSG			= DaoManager.getYdTcarsch(recPara, rsResult, 4);
					
					if( !szRTN_MSG.equals(YdConstant.RETN_CD_SUCCESS) ) {
						szLogMsg = "[Jsp-Session : "+szOperationName + "] 대차설비ID["+szYD_EQP_ID+"]에 해당하는 대차스케줄 조회 시 오류발생 - 루프반복";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						continue;
					}
					
					szLogMsg = "[Jsp-Session : "+szOperationName + "] 대차설비ID["+szYD_EQP_ID+"]에 해당하는 대차스케줄 조회 완료 - 대상재건수["+rsResult.size()+"]";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					rsResult.first();
					recTemp						= rsResult.getRecord();
					
					szYD_TCAR_SCH_ID			= ydDaoUtils.paraRecChkNull(recTemp, "YD_TCAR_SCH_ID");
					
					//--------------------------------------------------------------------------------------------------------
					
					
					//--------------------------------------------------------------------------------------------------------
					//	대차설비정보조회
					//--------------------------------------------------------------------------------------------------------
					
					szLogMsg = "[Jsp-Session : "+szOperationName + "] 대차설비ID["+szYD_EQP_ID+"]로 설비TABLE 조회 시작";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					rsResult			= JDTORecordFactory.getInstance().createRecordSet("");
					szRTN_MSG			= DaoManager.getYdEqp(recPara, rsResult, 0);
					
					if( !szRTN_MSG.equals(YdConstant.RETN_CD_SUCCESS) ) {
						szLogMsg = "[Jsp-Session : "+szOperationName + "] 대차설비ID["+szYD_EQP_ID+"]로 설비TABLE 조회 시 오류발생";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						throw new DAOException(szLogMsg);
					}
					
					szLogMsg = "[Jsp-Session : "+szOperationName + "] 대차설비ID["+szYD_EQP_ID+"]로 설비TABLE 조회 완료 - 대상재건수["+rsResult.size()+"]";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					rsResult.first();
					recTemp						= rsResult.getRecord();
					
					szEQP_YD_CURR_BAY_GP			= ydDaoUtils.paraRecChkNull(recTemp, "YD_CURR_BAY_GP");
					
					//--------------------------------------------------------------------------------------------------------
					
					
					//--------------------------------------------------------------------------------------------------------
					//	대차스케줄이 존재하면 대차이송재료 삭제
					//--------------------------------------------------------------------------------------------------------
					
					szLogMsg = "[Jsp-Session : "+szOperationName + "] 대차설비ID["+szYD_EQP_ID+"]에 해당하는 대차스케줄["+szYD_TCAR_SCH_ID+"]의 대차이송재료 조회 시작";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					rsResult			= JDTORecordFactory.getInstance().createRecordSet("");
					recTemp				= JDTORecordFactory.getInstance().create();
					recTemp.setField("YD_TCAR_SCH_ID", 			szYD_TCAR_SCH_ID);
					
					szRTN_MSG			= DaoManager.getYdTcarftmvmtl(recTemp, rsResult, 1);
					
					if( szRTN_MSG.equals(YdConstant.RETN_CD_SUCCESS) ) {
						
						szLogMsg = "[Jsp-Session : "+szOperationName + "] 대차설비ID["+szYD_EQP_ID+"]에 해당하는 대차스케줄["+szYD_TCAR_SCH_ID+"]의 대차이송재료 조회 완료 - 대상재건수["+rsResult.size()+"]";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						
						for(int i = 1; i <= rsResult.size(); i++ ) {
							rsResult.absolute(i);
							recTemp			= rsResult.getRecord();
							
							szLogMsg = "[Jsp-Session : "+szOperationName + "] 대차설비ID["+szYD_EQP_ID+"]에 해당하는 대차스케줄["+szYD_TCAR_SCH_ID+"]의 대차이송재료["+ydDaoUtils.paraRecChkNull(recTemp, "STL_NO")+"] 삭제 시작";
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
							
							recTemp.setField("DEL_YN", "Y");
							
							szRTN_MSG			= DaoManager.updYdTcarftmvmtl(recTemp, 0);
							
							if( !szRTN_MSG.equals(YdConstant.RETN_CD_SUCCESS) ) {
								szLogMsg = "[Jsp-Session : "+szOperationName + "] 대차설비ID["+szYD_EQP_ID+"]에 해당하는 대차스케줄["+szYD_TCAR_SCH_ID+"]의 대차이송재료["+ydDaoUtils.paraRecChkNull(recTemp, "STL_NO")+"] 삭제 시 오류발생";
								ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
								throw new DAOException(szLogMsg);
							}
							
							szLogMsg = "[Jsp-Session : "+szOperationName + "] 대차설비ID["+szYD_EQP_ID+"]에 해당하는 대차스케줄["+szYD_TCAR_SCH_ID+"]의 대차이송재료["+ydDaoUtils.paraRecChkNull(recTemp, "STL_NO")+"] 삭제 성공";
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						}
						
					}
					
					szLogMsg = "[Jsp-Session : "+szOperationName + "] 대차설비ID["+szYD_EQP_ID+"]에 해당하는 대차스케줄["+szYD_TCAR_SCH_ID+"]의 대차이송재료 조회 완료 - 메세지 : " + szRTN_MSG;
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					//--------------------------------------------------------------------------------------------------------
					
					//--------------------------------------------------------------------------------------------------------
					//	대차스케줄 삭제
					//--------------------------------------------------------------------------------------------------------
					
					szLogMsg = "[Jsp-Session : "+szOperationName + "] 대차설비ID["+szYD_EQP_ID+"]와 관련된 대차스케줄 삭제 시작";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					recPara.setField("YD_EQP_ID", 			szYD_EQP_ID);
					recPara.setField("MODIFIER", 			szYD_USER_ID);
					
					szRTN_MSG			= DaoManager.updYdTCarschDir(recPara, 2);
					
					szLogMsg = "[Jsp-Session : "+szOperationName + "] 대차설비ID["+szYD_EQP_ID+"]와 관련된 대차스케줄 삭제 완료 - 메세지 : " + szRTN_MSG;
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					//--------------------------------------------------------------------------------------------------------
					
					
					//--------------------------------------------------------------------------------------------------------
					//	사용자가 지정한 현재동정보를 사용해서 대차스케줄 초기값으로 생성
					//--------------------------------------------------------------------------------------------------------
					
					szLogMsg = "[Jsp-Session : "+szOperationName + "] 대차설비ID["+szYD_EQP_ID+"]의 사용자지정 현재동 정보[" + szYD_CURR_BAY_GP + "]로 대차스케줄 초기생성 시작";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					szYD_CARLD_STOP_LOC			= szYD_EQP_ID.substring(0, 1) + szYD_CURR_BAY_GP + szYD_EQP_ID.substring(2);
					
					szYD_TCAR_SCH_ID			= ydTcarSchDao.getYdTcarschId();
					
					recTemp						= JDTORecordFactory.getInstance().create();
					recTemp.setField("YD_TCAR_SCH_ID", 			szYD_TCAR_SCH_ID);					//대차스케줄ID
					recTemp.setField("REGISTER", 				szYD_USER_ID);						//등록자
					recTemp.setField("YD_CAR_PROG_STAT", 		"0");								//차량진행상태
					recTemp.setField("YD_CARLD_SCH_REQ_GP", 	"6");								//상차스케줄요청구분
					recTemp.setField("YD_CARUD_SCH_REQ_GP", 	"3");								//하차스케줄요청구분
					recTemp.setField("YD_EQP_WRK_STAT", 		"U");								//설비작업상태 - 공차
					recTemp.setField("YD_EQP_ID", 				szYD_EQP_ID);						//야드설비ID
					recTemp.setField("YD_CARLD_STOP_LOC",		szYD_CARLD_STOP_LOC);				//야드상차정지위치
					
					intRtnVal					= ydTcarSchDao.insYdTcarsch(recTemp);
					
					if( intRtnVal <= 0 ) {
						szLogMsg = "[Jsp-Session : "+szOperationName + "] 대차설비ID["+szYD_EQP_ID+"]의 사용자지정 현재동 정보[" + szYD_CURR_BAY_GP + "]로 대차스케줄["+szYD_TCAR_SCH_ID+"] 초기생성 시 오류발생";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						throw new DAOException(szLogMsg);
					}
					
					szLogMsg = "[Jsp-Session : "+szOperationName + "] 대차설비ID["+szYD_EQP_ID+"]의 사용자지정 현재동 정보[" + szYD_CURR_BAY_GP + "]로 대차스케줄["+szYD_TCAR_SCH_ID+"] 초기생성 완료";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					//--------------------------------------------------------------------------------------------------------
					
					
					//--------------------------------------------------------------------------------------------------------
					//	사용자가 지정한 현재동정보를 사용해서 대차설비의 현재동으로 설정
					//--------------------------------------------------------------------------------------------------------
					
					szLogMsg = "[Jsp-Session : "+szOperationName + "] 대차설비ID["+szYD_EQP_ID+"]의 사용자지정 현재동 정보[" + szYD_CURR_BAY_GP + "]를 수정 시작";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					recTemp				= JDTORecordFactory.getInstance().create();
					recTemp.setField("YD_EQP_ID", 				szYD_EQP_ID);
					recTemp.setField("YD_CURR_BAY_GP", 			szYD_CURR_BAY_GP);
					recTemp.setField("MODIFIER", 				szYD_USER_ID);
					
					
					szRTN_MSG			= DaoManager.updYdEqp(recTemp, 0);
					
					if( !szRTN_MSG.equals(YdConstant.RETN_CD_SUCCESS) ) {
						szLogMsg = "[Jsp-Session : "+szOperationName + "] 대차설비ID["+szYD_EQP_ID+"]의 사용자지정 현재동 정보[" + szYD_CURR_BAY_GP + "]를 수정 시 오류발생";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						throw new DAOException(szLogMsg);
					}
					
					szLogMsg = "[Jsp-Session : "+szOperationName + "] 대차설비ID["+szYD_EQP_ID+"]의 사용자지정 현재동 정보[" + szYD_CURR_BAY_GP + "]를 수정 완료";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					//--------------------------------------------------------------------------------------------------------
					
					//--------------------------------------------------------------------------------------------------------
					//	대차설비의 현재동과 사용자가 넘겨준 현재동이 다른 경우에는 
					//	대차설비의 현재동의 저장위치 맵을 비활성화 처리 후 저장위치제원정보 전송
					//--------------------------------------------------------------------------------------------------------
					
					if( !szEQP_YD_CURR_BAY_GP.equals("") ) {
						if( !szEQP_YD_CURR_BAY_GP.equals(szYD_CURR_BAY_GP) ) {
							//--------------------------------------------------------------------------------------------------------
							//	대차설비의 현재동에 대한 저장위치베드 정보 조회 후 맵이 활성상태이면 비활성화 처리 후 저장위치제원 정보 전송
							//--------------------------------------------------------------------------------------------------------
							String szBEFORE_STOP_LOC		= szYD_EQP_ID.substring(0, 1) + szEQP_YD_CURR_BAY_GP + szYD_EQP_ID.substring(2);
							
							szLogMsg = "[Jsp-Session : "+szOperationName + "] 대차설비ID["+szYD_EQP_ID+"]의 현재동 정보[" + szEQP_YD_CURR_BAY_GP + "]의 저장위치["+szBEFORE_STOP_LOC+", 01] 정보 조회 시작";
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
							
							rsResult			= JDTORecordFactory.getInstance().createRecordSet("");
							recTemp				= JDTORecordFactory.getInstance().create();
							recTemp.setField("YD_STK_COL_GP", 			szBEFORE_STOP_LOC);
							recTemp.setField("YD_STK_BED_NO", 			"01");
							
							szRTN_MSG				= DaoManager.getYdStkbed(recTemp, rsResult, 0);
							
							if( !szRTN_MSG.equals(YdConstant.RETN_CD_SUCCESS) ) {
								szLogMsg = "[Jsp-Session : "+szOperationName + "] 대차설비ID["+szYD_EQP_ID+"]의 현재동 정보[" + szEQP_YD_CURR_BAY_GP + "]의 저장위치["+szBEFORE_STOP_LOC+", 01] 정보 조회 시 오류발생";
								ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
								throw new DAOException(szLogMsg);
							}
							
							
							
							rsResult.first();
							
							recTemp				= rsResult.getRecord();
							
							szYD_STK_BED_ACT_STAT		= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_ACT_STAT");
							
							szLogMsg = "[Jsp-Session : "+szOperationName + "] 대차설비ID["+szYD_EQP_ID+"]의 현재동 정보[" + szEQP_YD_CURR_BAY_GP + "]의 저장위치["+szBEFORE_STOP_LOC+", 01] 정보 조회 완료 - 야드적치Bed활성상태["+szYD_STK_BED_ACT_STAT+"]";
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
							
							if( szYD_STK_BED_ACT_STAT.equals(YdConstant.YD_STK_BED_ACTIVE) ) {
								//--------------------------------------------------------------------------------------------------------
								//	베드 맵 활성화 처리
								//--------------------------------------------------------------------------------------------------------
								
								//--------------------------------------------------------------------------------------------
					    		//	상차정지위치 베드 활성화
					    		//--------------------------------------------------------------------------------------------
								
								String szYD_STK_BED_NO			= "01";
								
								szLogMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szBEFORE_STOP_LOC+", 적치베드:"+szYD_STK_BED_NO+"] 비활성화 시작";
								ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					    		
					    		recInTemp = JDTORecordFactory.getInstance().create();
						    	recInTemp.setField("YD_STK_COL_GP", 			szBEFORE_STOP_LOC);
						    	recInTemp.setField("YD_STK_BED_NO", 			szYD_STK_BED_NO);
						    	recInTemp.setField("YD_STK_BED_ACT_STAT", 		"C");
						    	intRtnVal = ydStkBedDao.updYdStkbed(recInTemp, 0);
								if(intRtnVal <= 0) {
					    			if(intRtnVal == 0) {
					    				szLogMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szBEFORE_STOP_LOC+", 적치베드:"+szYD_STK_BED_NO+"] 비활성화 시 data not found";
					    				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					    			}else if(intRtnVal == -1) {
					    				szLogMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szBEFORE_STOP_LOC+", 적치베드:"+szYD_STK_BED_NO+"] 비활성화 시 duplicate data,";
					    				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);	
					    			}else if(intRtnVal == -2) {
					    				szLogMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szBEFORE_STOP_LOC+", 적치베드:"+szYD_STK_BED_NO+"] 비활성화 시 parameter error";
					    				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					    			}else if(intRtnVal == -3){
					    				szLogMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szBEFORE_STOP_LOC+", 적치베드:"+szYD_STK_BED_NO+"] 비활성화 시 execution failed";
					    				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					    			}
					    			//return intRtnVal = -1;
					    			throw new DAOException(szLogMsg);
					    		}
								
								szLogMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szBEFORE_STOP_LOC+", 적치베드:"+szYD_STK_BED_NO+"] 비활성화 완료 - 반환값 : " + intRtnVal;
								ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
								
								//--------------------------------------------------------------------------------------------------
						    	//	상차정지위치 단정보 활성화
								//--------------------------------------------------------------------------------------------------
								
								szLogMsg="["+szOperationName+"] 상차정지위치단[적치열:"+szBEFORE_STOP_LOC+", 적치베드:"+szYD_STK_BED_NO+"] 활성화 시작";
								ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
								
								recInTemp = JDTORecordFactory.getInstance().create();
								recInTemp.setField("YD_STK_COL_GP", 			szBEFORE_STOP_LOC);
								recInTemp.setField("YD_STK_BED_NO", 			szYD_STK_BED_NO);
								recInTemp.setField("STL_NO", 					"");
								//recInTemp.setField("MODIFIER", 					"SYSTEM");
								recInTemp.setField("YD_STK_LYR_ACT_STAT", 		"C");
								recInTemp.setField("YD_STK_LYR_MTL_STAT", 		"E");
		                  
						    	
								intRtnVal = ydStkLyrDao.updYdStklyrYdStkColGpBedNo2(recInTemp);
								if(intRtnVal <= 0) {
									szLogMsg = "["+szOperationName+"] 상차정지위치단[적치열:"+szBEFORE_STOP_LOC+", 적치베드:"+szYD_STK_BED_NO+"] 활성화 시 Error!!  - 반환값 : " + intRtnVal;
									ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
									throw new DAOException(szLogMsg);
								}
								
								szLogMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szBEFORE_STOP_LOC+", 적치베드:"+szYD_STK_BED_NO+"] 활성화 완료 - 반환값 : " + intRtnVal;
								ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
								
								//--------------------------------------------------------------------------------------------------
								
								//--------------------------------------------------------------------------------------------
					    		//	적치대제원 전문을 L2로 전송
					    		//--------------------------------------------------------------------------------------------
								
								szLogMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szYD_CARLD_STOP_LOC+", 적치베드:"+szYD_STK_BED_NO+"]에 대한 저장위치제원 전송 시작";
								ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
								
								recInTemp = JDTORecordFactory.getInstance().create();
								recInTemp.setField("YD_INFO_SYNC_CD" , 			"3");						          // 1:동,2:SPAN,3:열,4:BED
								recInTemp.setField("YD_GP"           , 			szYD_EQP_ID.substring(0, 1));
								recInTemp.setField("YD_STK_COL_GP"   , 			szBEFORE_STOP_LOC);
								// 전문에 "S"면 출발이므로 "1" or "A" 를 넣어서 전문 호출하면 전문편집시 출발인 "S"로 송신
								// 전문에 "E"면 도착이므로 "2" or "B" 를 넣어서 전문 호출하면 전문편집시 도착인 "A"로 송신
								recInTemp.setField("YD_CAR_PROG_STAT", 			"1");						     
								recInTemp.setField("YD_EQP_WRK_STAT" , 			YdUtils.fillSpZr("U", 1, 1)); 
								YdCommonUtils.sndStrPosSpecToL2(recInTemp);
								
								szLogMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szBEFORE_STOP_LOC+", 적치베드:"+szYD_STK_BED_NO+"]에 대한 저장위치제원 전송 완료";
								ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
								
								//--------------------------------------------------------------------------------------------------------
							}else if( szYD_STK_BED_ACT_STAT.equals(YdConstant.YD_STK_BED_INACTIVE) ) {
								szLogMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szBEFORE_STOP_LOC+"]에 대한 저장위치가 이미 비활성화 상태이므로 맵비활성화와 저장위치제원 전송 필요없음";
								ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
							}else if( szYD_STK_BED_ACT_STAT.equals(YdConstant.YD_STK_BED_NOUSE) ) {
								szLogMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szBEFORE_STOP_LOC+"]에 대한 저장위치가 이미 사용불가 상태임";
								ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
							}else{
								szLogMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szBEFORE_STOP_LOC+"]의 야드적치Bed활성상태값중 지원하지 않는 값["+szYD_STK_BED_ACT_STAT+"]입니다.";
								ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
							}
							//--------------------------------------------------------------------------------------------------------
						}
					}
					
					//--------------------------------------------------------------------------------------------------------
					
					//--------------------------------------------------------------------------------------------------------
					//	사용자가 넘겨준 현재동으로 맵정보 조회 후 비활성상태이면 맵정보 활성화, L2로 저장위치제원 정보 전송
					//--------------------------------------------------------------------------------------------------------
					
					szLogMsg = "[Jsp-Session : "+szOperationName + "] 대차설비ID["+szYD_EQP_ID+"]의 사용자지정 현재동 정보[" + szYD_CURR_BAY_GP + "]의 저장위치["+szYD_CARLD_STOP_LOC+", 01] 정보 조회 시작";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					rsResult			= JDTORecordFactory.getInstance().createRecordSet("");
					recTemp				= JDTORecordFactory.getInstance().create();
					recTemp.setField("YD_STK_COL_GP", 			szYD_CARLD_STOP_LOC);
					recTemp.setField("YD_STK_BED_NO", 			"01");
					
					szRTN_MSG				= DaoManager.getYdStkbed(recTemp, rsResult, 0);
					
					if( !szRTN_MSG.equals(YdConstant.RETN_CD_SUCCESS) ) {
						szLogMsg = "[Jsp-Session : "+szOperationName + "] 대차설비ID["+szYD_EQP_ID+"]의 사용자지정 현재동 정보[" + szYD_CURR_BAY_GP + "]의 저장위치["+szYD_CARLD_STOP_LOC+", 01] 정보 조회 시 오류발생";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						throw new DAOException(szLogMsg);
					}
					
					
					
					rsResult.first();
					
					recTemp				= rsResult.getRecord();
					
					szYD_STK_BED_ACT_STAT		= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_ACT_STAT");
					
					szLogMsg = "[Jsp-Session : "+szOperationName + "] 대차설비ID["+szYD_EQP_ID+"]의 사용자지정 현재동 정보[" + szYD_CURR_BAY_GP + "]의 저장위치["+szYD_CARLD_STOP_LOC+", 01] 정보 조회 완료 - 야드적치Bed활성상태["+szYD_STK_BED_ACT_STAT+"]";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					if( szYD_STK_BED_ACT_STAT.equals(YdConstant.YD_STK_BED_INACTIVE) ) {
						//--------------------------------------------------------------------------------------------------------
						//	베드 맵 활성화 처리
						//--------------------------------------------------------------------------------------------------------
						
						//--------------------------------------------------------------------------------------------
			    		//	상차정지위치 베드 활성화
			    		//--------------------------------------------------------------------------------------------
						
						String szYD_STK_BED_NO			= "01";
						
						szLogMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szYD_CARLD_STOP_LOC+", 적치베드:"+szYD_STK_BED_NO+"] 활성화 시작";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			    		
			    		recInTemp = JDTORecordFactory.getInstance().create();
				    	recInTemp.setField("YD_STK_COL_GP", 			szYD_CARLD_STOP_LOC);
				    	recInTemp.setField("YD_STK_BED_NO", 			szYD_STK_BED_NO);
				    	recInTemp.setField("YD_STK_BED_ACT_STAT", 		"L");
				    	intRtnVal = ydStkBedDao.updYdStkbed(recInTemp, 0);
						if(intRtnVal <= 0) {
			    			if(intRtnVal == 0) {
			    				szLogMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szYD_CARLD_STOP_LOC+", 적치베드:"+szYD_STK_BED_NO+"] 활성화 시 data not found";
			    				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			    			}else if(intRtnVal == -1) {
			    				szLogMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szYD_CARLD_STOP_LOC+", 적치베드:"+szYD_STK_BED_NO+"] 활성화 시 duplicate data,";
			    				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);	
			    			}else if(intRtnVal == -2) {
			    				szLogMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szYD_CARLD_STOP_LOC+", 적치베드:"+szYD_STK_BED_NO+"] 활성화 시 parameter error";
			    				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			    			}else if(intRtnVal == -3){
			    				szLogMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szYD_CARLD_STOP_LOC+", 적치베드:"+szYD_STK_BED_NO+"] 활성화 시 execution failed";
			    				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			    			}
			    			//return intRtnVal = -1;
			    			throw new DAOException(szLogMsg);
			    		}
						
						szLogMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szYD_CARLD_STOP_LOC+", 적치베드:"+szYD_STK_BED_NO+"] 활성화 완료 - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						
						//--------------------------------------------------------------------------------------------------
				    	//	상차정지위치 단정보 활성화
						//--------------------------------------------------------------------------------------------------
						
						szLogMsg="["+szOperationName+"] 상차정지위치단[적치열:"+szYD_CARLD_STOP_LOC+", 적치베드:"+szYD_STK_BED_NO+"] 활성화 시작";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						
						recInTemp = JDTORecordFactory.getInstance().create();
						recInTemp.setField("YD_STK_COL_GP", 			szYD_CARLD_STOP_LOC);
						recInTemp.setField("YD_STK_BED_NO", 			szYD_STK_BED_NO);
						recInTemp.setField("STL_NO", 					"");
						//recInTemp.setField("MODIFIER", 					"SYSTEM");
						recInTemp.setField("YD_STK_LYR_ACT_STAT", 		"E");
						recInTemp.setField("YD_STK_LYR_MTL_STAT", 		"E");
                  
				    	
						intRtnVal = ydStkLyrDao.updYdStklyrYdStkColGpBedNo2(recInTemp);
						if(intRtnVal <= 0) {
							szLogMsg = "["+szOperationName+"] 상차정지위치단[적치열:"+szYD_CARLD_STOP_LOC+", 적치베드:"+szYD_STK_BED_NO+"] 활성화 시 Error!!  - 반환값 : " + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
							throw new DAOException(szLogMsg);
						}
						
						szLogMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szYD_CARLD_STOP_LOC+", 적치베드:"+szYD_STK_BED_NO+"] 활성화 완료 - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						
						//--------------------------------------------------------------------------------------------------
						
						//--------------------------------------------------------------------------------------------
			    		//	적치대제원 전문을 L2로 전송
			    		//--------------------------------------------------------------------------------------------
						
						szLogMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szYD_CARLD_STOP_LOC+", 적치베드:"+szYD_STK_BED_NO+"]에 대한 저장위치제원 전송 시작";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						
						recInTemp = JDTORecordFactory.getInstance().create();
						recInTemp.setField("YD_INFO_SYNC_CD" , 			"3");						          // 1:동,2:SPAN,3:열,4:BED
						recInTemp.setField("YD_GP"           , 			szYD_EQP_ID.substring(0, 1));
						recInTemp.setField("YD_STK_COL_GP"   , 			szYD_CARLD_STOP_LOC);
						// 전문에 "S"면 출발이므로 "1" or "A" 를 넣어서 전문 호출하면 전문편집시 출발인 "S"로 송신
						// 전문에 "E"면 도착이므로 "2" or "B" 를 넣어서 전문 호출하면 전문편집시 도착인 "A"로 송신
						recInTemp.setField("YD_CAR_PROG_STAT", 			"2");						     
						recInTemp.setField("YD_EQP_WRK_STAT" , 			YdUtils.fillSpZr("U", 1, 1)); 
						YdCommonUtils.sndStrPosSpecToL2(recInTemp);
						
						szLogMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szYD_CARLD_STOP_LOC+", 적치베드:"+szYD_STK_BED_NO+"]에 대한 저장위치제원 전송 완료";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						
						//--------------------------------------------------------------------------------------------------------
					}else if( szYD_STK_BED_ACT_STAT.equals(YdConstant.YD_STK_BED_ACTIVE) ) {
						szLogMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szYD_CARLD_STOP_LOC+"]에 대한 저장위치가 이미 활성화 상태이므로 맵활성화와 저장위치제원 전송 필요없음";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					}else if( szYD_STK_BED_ACT_STAT.equals(YdConstant.YD_STK_BED_NOUSE) ) {
						szLogMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szYD_CARLD_STOP_LOC+"]에 대한 저장위치가 이미 사용불가 상태임";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					}else{
						szLogMsg="["+szOperationName+"] 상차정지위치베드[적치열:"+szYD_CARLD_STOP_LOC+"]의 야드적치Bed활성상태값중 지원하지 않는 값["+szYD_STK_BED_ACT_STAT+"]입니다.";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					}
					//--------------------------------------------------------------------------------------------------------
				}
				
			}catch(DAOException e){
				szLogMsg = "[Jsp-Session : "+szOperationName + "] 예외발생[1] : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);	
				throw e;
				
			}catch(Exception e){
				szLogMsg = "[Jsp-Session : "+szOperationName + "] 예외발생[2] : " + e.getMessage();
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				throw new DAOException(szLogMsg);
			}
			
			// 4. 작업완료 유무를 RETURN 한다.
			
			szLogMsg = "[Jsp-Session : "+szOperationName + "] ------------------ 메소드 끝 ------------------";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			return szRtnMsg;
		} // end of delTcarSch
		
		/**
		 * 수요가코드 조회
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws DAOException
		 */
		public JDTORecordSet getDemanderCdList(JDTORecord inDto) throws DAOException {
			int           intRtnVal    = 0;
			String        szMsg        = "";
			String		  szGp         = "";
			String        szMethodName = "getDemanderCdList";
			String 	      szSearchGp   = "";
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			
			try {
				
				szMsg = "radioBox : " + yddatautil.setDataDefault(inDto.getField("RADIOBOX"),		"");
				szMsg += ", INPUT_TEXT : " + yddatautil.setDataDefault(inDto.getField("INPUT_TEXT"),    "");
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				szGp = yddatautil.setDataDefault(inDto.getField("RADIOBOX"),		"");
				
				if ("1".equals(szGp)) {
					recPara.setField("CUST_CD",    	yddatautil.setDataDefault(inDto.getField("INPUT_TEXT"),		""));
					szMsg = "CUST_CD : " + yddatautil.setDataDefault(recPara.getField("CUST_CD"),		"");
				} else if("0".equals(szGp)) {
					recPara.setField("CUST_KO_NAME",    	yddatautil.setDataDefault(inDto.getField("INPUT_TEXT"),		""));
					szMsg = "CUST_KO_NAME : " + yddatautil.setDataDefault(recPara.getField("CUST_KO_NAME"),		"");
				} else if("2".equals(szGp)) {
					recPara.setField("COMREGNO",    	yddatautil.setDataDefault(inDto.getField("INPUT_TEXT"),		""));
					szMsg = "COMREGNO : " + yddatautil.setDataDefault(recPara.getField("COMREGNO"),		"");
				}
				
				recPara.setField("PAGE_CNT1", 	  	inDto.getField("PAGE_NO"));
				recPara.setField("PAGE_CNT2", 	  	inDto.getField("PAGE_NO"));
				recPara.setField("ROW_CNT1",  	  	inDto.getField("ROWCOUNT"));
				recPara.setField("ROW_CNT2",  	  	inDto.getField("ROWCOUNT"));

				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				PtOsCommDao ptOsCommDao = new PtOsCommDao();
				intRtnVal = ptOsCommDao.getPtOsComm(recPara, outRecSet, 15);
				
				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					return outRecSet;
				}
			} catch (Exception e) {
				// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
				throw new DAOException(getClass().getName() + e.getMessage(),e);
			} finally {
			}
			return outRecSet;
		}
		
		/**
		 * 목적지코드 조회
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws DAOException
		 */
		public JDTORecordSet getDestCdList(JDTORecord inDto) throws DAOException {
			int           intRtnVal    = 0;
			String        szMsg        = "";
			String		  szGp         = "";
			String        szMethodName = "getDestCdList";
			String 	      szSearchGp   = "";
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			
			try {
				
				szMsg = "radioBox : " + yddatautil.setDataDefault(inDto.getField("RADIOBOX"),		"");
				szMsg += ", INPUT_TEXT : " + yddatautil.setDataDefault(inDto.getField("INPUT_TEXT"),    "");
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				szGp = yddatautil.setDataDefault(inDto.getField("RADIOBOX"),		"");
				
				if ("1".equals(szGp)) {
					recPara.setField("DEST_CD",    	yddatautil.setDataDefault(inDto.getField("INPUT_TEXT"),		""));
					szMsg = "DEST_CD : " + yddatautil.setDataDefault(recPara.getField("DEST_CD"),		"");
				} else if("0".equals(szGp)) {
					recPara.setField("DEST_NAME",    	yddatautil.setDataDefault(inDto.getField("INPUT_TEXT"),		""));
					szMsg = "DEST_NAME : " + yddatautil.setDataDefault(recPara.getField("DEST_NAME"),		"");
				}
				
				recPara.setField("DEST_GP", 	  	inDto.getField("DEST_GP"));
				recPara.setField("DEST_AREA_GP", 	  	inDto.getField("DEST_AREA_GP"));
				
				recPara.setField("PAGE_CNT1", 	  	inDto.getField("PAGE_NO"));
				recPara.setField("PAGE_CNT2", 	  	inDto.getField("PAGE_NO"));
				recPara.setField("ROW_CNT1",  	  	inDto.getField("ROWCOUNT"));
				recPara.setField("ROW_CNT2",  	  	inDto.getField("ROWCOUNT"));

				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				PtOsCommDao ptOsCommDao = new PtOsCommDao();
				intRtnVal = ptOsCommDao.getPtOsComm(recPara, outRecSet, 16);
				
				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					return outRecSet;
				}
			} catch (Exception e) {
				// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
				throw new DAOException(getClass().getName() + e.getMessage(),e);
			} finally {
			}
			return outRecSet;
		}
		
		/**
		 * 고객사코드 조회
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws DAOException
		 */
		public JDTORecordSet getCustCdList(JDTORecord inDto) throws DAOException {
			int           intRtnVal    = 0;
			String        szMsg        = "";
			String		  szGp         = "";
			String        szMethodName = "getCustCdList";
			String 	      szSearchGp   = "";
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			
			try {
				
				szMsg = "radioBox : " + yddatautil.setDataDefault(inDto.getField("RADIOBOX"),		"");
				szMsg += ", INPUT_TEXT : " + yddatautil.setDataDefault(inDto.getField("INPUT_TEXT"),    "");
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				szGp = yddatautil.setDataDefault(inDto.getField("RADIOBOX"),		"");
				
				if ("1".equals(szGp)) {
					recPara.setField("CUST_CD",    	yddatautil.setDataDefault(inDto.getField("INPUT_TEXT"),		""));
					szMsg = "CUST_CD : " + yddatautil.setDataDefault(recPara.getField("CUST_CD"),		"");
				} else if("0".equals(szGp)) {
					recPara.setField("CUST_KO_NAME",    	yddatautil.setDataDefault(inDto.getField("INPUT_TEXT"),		""));
					szMsg = "CUST_KO_NAME : " + yddatautil.setDataDefault(recPara.getField("CUST_KO_NAME"),		"");
				} else if("2".equals(szGp)) {
					recPara.setField("COMREGNO",    	yddatautil.setDataDefault(inDto.getField("INPUT_TEXT"),		""));
					szMsg = "COMREGNO : " + yddatautil.setDataDefault(recPara.getField("COMREGNO"),		"");
				}
				
				recPara.setField("PAGE_CNT1", 	  	inDto.getField("PAGE_NO"));
				recPara.setField("PAGE_CNT2", 	  	inDto.getField("PAGE_NO"));
				recPara.setField("ROW_CNT1",  	  	inDto.getField("ROWCOUNT"));
				recPara.setField("ROW_CNT2",  	  	inDto.getField("ROWCOUNT"));

				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				PtOsCommDao ptOsCommDao = new PtOsCommDao();
				intRtnVal = ptOsCommDao.getPtOsComm(recPara, outRecSet, 15);
				
				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					return outRecSet;
				}
			} catch (Exception e) {
				// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
				throw new DAOException(getClass().getName() + e.getMessage(),e);
			} finally {
			}
			return outRecSet;
		}
		
		/**
		 * 선박코드 조회
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return
		 * @throws DAOException
		 */
		public JDTORecordSet getShipCdList(JDTORecord inDto) throws DAOException {
			int           intRtnVal    = 0;
			String        szMsg        = "";
			String		  szGp         = "";
			String        szMethodName = "getShipCdList";
			String 	      szSearchGp   = "";
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			
			try {
				
				szMsg = "radioBox : " + yddatautil.setDataDefault(inDto.getField("RADIOBOX"),		"");
				szMsg += ", INPUT_TEXT : " + yddatautil.setDataDefault(inDto.getField("INPUT_TEXT"),    "");
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				szGp = yddatautil.setDataDefault(inDto.getField("RADIOBOX"),		"");
				
				if ("0".equals(szGp)) {
					recPara.setField("SHIP_NAME",    	yddatautil.setDataDefault(inDto.getField("INPUT_TEXT"),		""));
					szMsg = "SHIP_NAME : " + yddatautil.setDataDefault(recPara.getField("SHIP_NAME"),		"");
				} else if("1".equals(szGp)) {
					recPara.setField("SHIP_CD",    	yddatautil.setDataDefault(inDto.getField("INPUT_TEXT"),		""));
					szMsg = "SHIP_CD : " + yddatautil.setDataDefault(recPara.getField("SHIP_CD"),		"");
				} else if("2".equals(szGp)) {
					recPara.setField("SHPAS_REQ_NO",    	yddatautil.setDataDefault(inDto.getField("INPUT_TEXT"),		""));
					szMsg = "SHPAS_REQ_NO : " + yddatautil.setDataDefault(recPara.getField("SHPAS_REQ_NO"),		"");
				}
				
				recPara.setField("PAGE_CNT1", 	  	inDto.getField("PAGE_NO"));
				recPara.setField("PAGE_CNT2", 	  	inDto.getField("PAGE_NO"));
				recPara.setField("ROW_CNT1",  	  	inDto.getField("ROWCOUNT"));
				recPara.setField("ROW_CNT2",  	  	inDto.getField("ROWCOUNT"));

				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				PtOsCommDao ptOsCommDao = new PtOsCommDao();
				intRtnVal = ptOsCommDao.getPtOsComm(recPara, outRecSet, 20);
				
				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					return outRecSet;
				}
			} catch (Exception e) {
				// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
				throw new DAOException(getClass().getName() + e.getMessage(),e);
			} finally {
			}
			return outRecSet;
		}		
		
		
		
		
		
		/**
		 * 가동율 분석
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return JDTORecordSet
		 * @throws DAOException
		 */
		public JDTORecordSet getYdWrkAnalysis(JDTORecord inDto) throws DAOException {
			// DAO 및 UTIL 객체
			
			YdWrkHistDao ydWrkHistDao = new YdWrkHistDao();

			// 레코드 선언
			JDTORecord recPara      = null;
			JDTORecordSet outRecSet = null;
			
			// 변수 선언
			String szMethodName     = "getYdWrkAnalysis";
			String szOperationName  = "가동율 분석";
			String szMsg            = "";		
			int nRet                = 0;
			
			try {
								
				szMsg = "JSP-SESSION  ["+szOperationName+"]시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				

				// 레코드 생성
				recPara   = JDTORecordFactory.getInstance().create();
				outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
				
				//
				recPara.setField("YD_GP", ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));
				recPara.setField("YD_BAY_GP", ydDaoUtils.paraRecChkNull(inDto, "YD_BAY_GP"));
				recPara.setField("YD_WRK_FR_DD", ydDaoUtils.paraRecChkNull(inDto, "DATE_FROM"));
				recPara.setField("YD_WRK_TO_DD", ydDaoUtils.paraRecChkNull(inDto, "DATE_TO"));
				
				nRet = ydWrkHistDao.getYdWrkHist(recPara, outRecSet, 13);
				
				if(nRet <= 0) {
					if(nRet == -1) {
						szMsg = "routine error!!!, ErrorCode:" + nRet;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + nRet;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					return outRecSet;
				} 
				
			} catch(Exception e) {
				// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
				throw new DAOException(getClass().getName() + e.getMessage(),e);
			} finally {
			}
			szMsg = "JSP-SESSION  ["+szOperationName+"] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			
			return outRecSet;
		}//end of getYdWrkAnalysis
		
		/**
		 * 가동율 분석
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return JDTORecordSet
		 * @throws DAOException
		 */
		public JDTORecordSet getYdWrkAnalysisForPlate(JDTORecord inDto) throws DAOException {
			// DAO 및 UTIL 객체
			
			YdWrkHistDao ydWrkHistDao = new YdWrkHistDao();

			// 레코드 선언
			JDTORecord recPara      = null;
			JDTORecordSet outRecSet = null;
			
			// 변수 선언
			String szMethodName     = "getYdWrkAnalysisForPlate";
			String szOperationName  = "가동율 분석";
			String szMsg            = "";		
			int nRet                = 0;
			
			try {
								
				szMsg = "JSP-SESSION  ["+szOperationName+"]시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				

				// 레코드 생성
				recPara   = JDTORecordFactory.getInstance().create();
				outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
				
				//
				recPara.setField("YD_GP", ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));
				recPara.setField("YD_BAY_GP", ydDaoUtils.paraRecChkNull(inDto, "YD_BAY_GP"));
				recPara.setField("YD_WRK_FR_DD", ydDaoUtils.paraRecChkNull(inDto, "DATE_FROM"));
				recPara.setField("YD_WRK_TO_DD", ydDaoUtils.paraRecChkNull(inDto, "DATE_TO"));
				
				recPara.setField("YD_EQP_ID", ydDaoUtils.paraRecChkNull(inDto, "YD_EQP_ID"));  //설비 필터 추가 REQ202309487427
				
				nRet = ydWrkHistDao.getYdWrkHist(recPara, outRecSet, 17);
				
				if(nRet <= 0) {
					if(nRet == -1) {
						szMsg = "routine error!!!, ErrorCode:" + nRet;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + nRet;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					return outRecSet;
				} 
				
			} catch(Exception e) {
				// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
				throw new DAOException(getClass().getName() + e.getMessage(),e);
			} finally {
			}
			szMsg = "JSP-SESSION  ["+szOperationName+"] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			
			return outRecSet;
		}//end of getYdWrkAnalysisForPlate
		
		
		/**
		 * 스케줄 분석
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return JDTORecordSet
		 * @throws DAOException
		 */
		public JDTORecordSet getYdWrkAnalysisBySchCd(JDTORecord inDto) throws DAOException {
			// DAO 및 UTIL 객체
			
			YdWrkHistDao ydWrkHistDao = new YdWrkHistDao();

			// 레코드 선언
			JDTORecord recPara      = null;
			JDTORecordSet outRecSet = null;
			
			// 변수 선언
			String szMethodName     = "getYdWrkAnalysisBySchCd";
			String szOperationName  = "스케줄 분석";
			String szMsg            = "";		
			int nRet                = 0;
			String szYdGp           = "";
			
			try {
								
				szMsg = "JSP-SESSION  ["+szOperationName+"]시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				

				// 레코드 생성
				recPara   = JDTORecordFactory.getInstance().create();
				outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
				
				//
				szYdGp = ydDaoUtils.paraRecChkNull(inDto, "YD_GP");
				recPara.setField("YD_GP", ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));
				recPara.setField("YD_BAY_GP", ydDaoUtils.paraRecChkNull(inDto, "YD_BAY_GP"));
				recPara.setField("YD_WRK_FR_DD", ydDaoUtils.paraRecChkNull(inDto, "DATE_FROM"));
				recPara.setField("YD_WRK_TO_DD", ydDaoUtils.paraRecChkNull(inDto, "DATE_TO"));
				
				if (szYdGp.equals(YdConstant.YD_GP_PLATE_GDS_YARD) ||
						szYdGp.equals(YdConstant.YD_GP_C_HR_COIL_GDS_YARD)	) { // 후판제품창고, 코일제품창고
					// com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getYdWrkAnalysisBySchCdPlateGds
					nRet = ydWrkHistDao.getYdWrkHist(recPara, outRecSet, 15);
				} else { // 나머지
					// com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getYdWrkAnalysisBySchCd
					nRet = ydWrkHistDao.getYdWrkHist(recPara, outRecSet, 14);
				}
				
				if(nRet <= 0) {
					if(nRet == -1) {
						szMsg = "routine error!!!, ErrorCode:" + nRet;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + nRet;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					return outRecSet;
				} 
				
			} catch(Exception e) {
				// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
				throw new DAOException(getClass().getName() + e.getMessage(),e);
			} finally {
			}
			szMsg = "JSP-SESSION  ["+szOperationName+"] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			
			return outRecSet;
		}//end of getYdWrkAnalysisBySchCd
		
		/**
		 * 후판슬라브 이상재 이력관리
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return JDTORecordSet
		 * @throws DAOException
		 */
		public JDTORecordSet getYdWrkAnalysisByAbSlab(JDTORecord inDto) throws DAOException {
			// DAO 및 UTIL 객체
			
			YdWrkHistDao ydWrkHistDao = new YdWrkHistDao();

			// 레코드 선언
			JDTORecord recPara      = null;
			JDTORecordSet outRecSet = null;
			
			// 변수 선언
			String szMethodName     = "getYdWrkAnalysisByAbSlab";
			String szOperationName  = "후판슬라브 이상재 이력관리";
			String szMsg            = "";		
			int nRet                = 0;
			String szYdGp           = "";
			
			try {
								
				szMsg = "JSP-SESSION  ["+szOperationName+"]시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				

				// 레코드 생성
				recPara   = JDTORecordFactory.getInstance().create();
				outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
				
				//
				szYdGp = ydDaoUtils.paraRecChkNull(inDto, "YD_GP");
				recPara.setField("YD_GP", ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));
				recPara.setField("YD_BAY_GP", ydDaoUtils.paraRecChkNull(inDto, "YD_BAY_GP"));
				recPara.setField("WORK_GBN", ydDaoUtils.paraRecChkNull(inDto, "WORK_GBN"));
				recPara.setField("YD_WRK_FR_DD", ydDaoUtils.paraRecChkNull(inDto, "DATE_FROM"));
				recPara.setField("YD_WRK_TO_DD", ydDaoUtils.paraRecChkNull(inDto, "DATE_TO"));
				recPara.setField("WD_GP", ydDaoUtils.paraRecChkNull(inDto, "WD_GP"));
				
				// com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getYdWrkAnalysisByAbSlab
				nRet = ydWrkHistDao.getYdWrkHist(recPara, outRecSet, 18);
				
				if(nRet <= 0) {
					if(nRet == -1) {
						szMsg = "routine error!!!, ErrorCode:" + nRet;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + nRet;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					return outRecSet;
				} 
				
			} catch(Exception e) {
				// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
				throw new DAOException(getClass().getName() + e.getMessage(),e);
			} finally {
			}
			szMsg = "JSP-SESSION  ["+szOperationName+"] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			
			return outRecSet;
		}//end of getYdWrkAnalysisByAbSlab
		
		/**
		 * 크레인작업실적현황(야드관리 > 코일소재야드 > 크레인실적관리 > 크레인 작업실적현황)
		 *
		 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		 * @param inDto
		 * @return JDTORecordSet
		 * @throws DAOException
		 */
		public JDTORecordSet getCrnWrkWrStat(JDTORecord inDto) throws DAOException {
			// DAO 및 UTIL 객체
			YdWrkHistDao ydWrkHistDao = new YdWrkHistDao();

			// 레코드 선언
			JDTORecord recPara      = null;
			JDTORecordSet outRecSet = null;
			
			// 변수 선언
			String szMethodName     = "getCrnWrkWrStat";
			String szOperationName  = "크레인작업실적현황";
			String szMsg            = "";		
			int nRet                = 0;
			String szYdGp           = "";
			
			try {
								
				szMsg = "JSP-SESSION  ["+szOperationName+"]시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				

				// 레코드 생성
				recPara   = JDTORecordFactory.getInstance().create();
				outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
				
				String message = "";
				//form 에서 넘어온 모든 name을  field name값으로 설정과 value 값도 셋팅
				//form name값과 field name을 다르게 설정할시 while 밑에 설정
				Iterator iter = inDto.iterateName();
				int cnt = 0;
				while(iter.hasNext()) {
					String frmNm 	= (String) iter.next();		//form name
					String fieldNm  = frmNm;					//셋팅할 field name
					//page 처리일 경우 setFiedname 설정 
					if(frmNm.equals("PAGE_SIZE")) {
						fieldNm = "ROW_CNT";
					}else if(frmNm.equals("PAGE_NO")) {
						fieldNm = "PAGE_CNT"; 
					}
					recPara.setField(fieldNm,	yddatautil.setDataDefault(inDto.getField(frmNm),""));
					message += "[count:"+cnt+"- name:"+fieldNm +" - value:"+yddatautil.setDataDefault(inDto.getField(frmNm),"")+"] \n";
					cnt++;
				}		
				ydUtils.putLog(szSessionName,"\nform 에 있는 모든 변수값 : ", message, YdConstant.INFO);
				
				nRet = ydWrkHistDao.getYdWrkHist(recPara, outRecSet, 300);
				
				if(nRet <= 0) {
					if(nRet == -1) {
						szMsg = "routine error!!!, ErrorCode:" + nRet;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + nRet;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					return outRecSet;
				} 
				
			} catch(Exception e) {
				// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
				throw new DAOException(getClass().getName() + e.getMessage(),e);
			} finally {
			}
			szMsg = "JSP-SESSION  ["+szOperationName+"] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			
			return outRecSet;
		}//end of getYdWrkAnalysisBySchCd
		
		
		
		/**
		 * 적치 가능 번지 리스트 조회 (select box용  소재,제품 공통 )
		 * @ejb.interface-method
		 * @param GridData
		 * @return GridData
		 * @작성자 : 박지열
		 * @작성일 : 2010.07.14
		 */
		public GridData getUsableBedList(GridData inDto) {
			GridData 		rtnGrd 		= new GridData();
			JDTORecordSet   outRecSet  	= null;
			
			JspCommonDAO 	dao 		= new JspCommonDAO();
			JDTORecord 		recPara		= null;
			
			try {
				
				// 파라미터 셋팅 
				recPara		= JDTORecordFactory.getInstance().create(); 			
				recPara.setField("V_YD_STK_COL_GP", 	inDto.getParam("V_YD_STK_COL_GP").trim());	/*동구분*/
				recPara.setField("V_YD_STK_LYR_NO", 	inDto.getParam("V_YD_STK_LYR_NO").trim());
				recPara.setField("V_STL_NO", 			inDto.getParam("V_STL_NO").trim());
				
				// DAO 호출
				outRecSet = dao.getUsableBedList(recPara);
				
				if(outRecSet != null){
					rtnGrd = CmUtil.genGridData(inDto, outRecSet);
				}else{
					rtnGrd.setStatus("false");
					rtnGrd.addParam("ret", "-1");
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return rtnGrd;
		}
		
		
		/**
		 * 후판정정야드 북아웃 대상재조회 (select box용)
		 * @ejb.interface-method
		 * @param GridData
		 * @return GridData
		 * @작성자 : 
		 * @작성일 : 
		 */
		public GridData getpPlateYdBookoutSltList(GridData inDto) {
			GridData 		rtnGrd 		= new GridData();
			JDTORecordSet   outRecSet  	= null;
			
			JspCommonDAO 	dao 		= new JspCommonDAO();
			JDTORecord 		recPara		= null;
			
			try {
				
				// 파라미터 셋팅 
				recPara		= JDTORecordFactory.getInstance().create(); 			
				recPara.setField("YD_LOC_GP", 	inDto.getParam("YD_LOC_GP").trim());
				
				// DAO 호출
				outRecSet = dao.getpPlateYdBookoutStlList(recPara);
				
				if(outRecSet != null){
					rtnGrd = CmUtil.genGridData(inDto, outRecSet);
				}else{
					rtnGrd.setStatus("false");
					rtnGrd.addParam("ret", "-1");
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return rtnGrd;
		}
		
		
		
		
		/**
		 * 후판정정야드 Bed조회 (select box용)
		 * @ejb.interface-method
		 * @param GridData
		 * @return GridData
		 * @작성자 : 
		 * @작성일 : 
		 */
		public GridData getpPlateYdBedList(GridData inDto) {
			GridData 		rtnGrd 		= new GridData();
			JDTORecordSet   outRecSet  	= null;
			
			JspCommonDAO 	dao 		= new JspCommonDAO();
			JDTORecord 		recPara		= null;
			String s_YD_EQP_GP = "";
			
			try {
				
				
				s_YD_EQP_GP = getYdeqpGp_CODE(inDto.getParam("YD_EQP_GP").trim());
				
				// 파라미터 셋팅 
				recPara		= JDTORecordFactory.getInstance().create(); 			
				recPara.setField("YD_EQP_GP", s_YD_EQP_GP);
				
				// DAO 호출
				outRecSet = dao.getpPlateYdBedList(recPara);
				
				if(outRecSet != null){
					rtnGrd = CmUtil.genGridData(inDto, outRecSet);
				}else{
					rtnGrd.setStatus("false");
					rtnGrd.addParam("ret", "-1");
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return rtnGrd;
		}
		
		
		
		/**
		 * 후판정정야드 Bed조회 (select box용)
		 * @ejb.interface-method
		 * @param GridData
		 * @return GridData
		 * @작성자 : 
		 * @작성일 : 
		 */
		public GridData getpPlateYdBedList99(GridData inDto) {
			GridData 		rtnGrd 		= new GridData();
			JDTORecordSet   outRecSet  	= null;
			
			JspCommonDAO 	dao 		= new JspCommonDAO();
			JDTORecord 		recPara		= null;
			String s_YD_EQP_GP = "";
			
			try {
				
				
				s_YD_EQP_GP = getYdeqpGp_CODE(inDto.getParam("YD_EQP_GP").trim());
				
				// 파라미터 셋팅 
				recPara		= JDTORecordFactory.getInstance().create(); 			
				recPara.setField("YD_EQP_GP", s_YD_EQP_GP);
				
				// DAO 호출
				outRecSet = dao.getpPlateYdBedList99(recPara);
				
				if(outRecSet != null){
					rtnGrd = CmUtil.genGridData(inDto, outRecSet);
				}else{
					rtnGrd.setStatus("false");
					rtnGrd.addParam("ret", "-1");
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return rtnGrd;
		}
		
		
		/**
		 * 후판정정야드 Bed조회 (select box용)
		 * @ejb.interface-method
		 * @param GridData
		 * @return GridData
		 * @작성자 : 
		 * @작성일 : 
		 */
		public GridData getpPlateYdBedList2(GridData inDto) { 
			GridData 		rtnGrd 		= new GridData();
			JDTORecordSet   outRecSet  	= null;
			
			JspCommonDAO 	dao 		= new JspCommonDAO();
			JDTORecord 		recPara		= null;
			String s_YD_EQP_GP = "";
			
			try {
				
				
				s_YD_EQP_GP = getYdeqpGp_CODE(inDto.getParam("YD_EQP_GP").trim());
				
				// 파라미터 셋팅 
				recPara		= JDTORecordFactory.getInstance().create(); 			
				recPara.setField("YD_EQP_GP", s_YD_EQP_GP);
				
				// DAO 호출
				outRecSet = dao.getpPlateYdBedList(recPara);
				
				if(outRecSet != null){
					rtnGrd = CmUtil.genGridData(inDto, outRecSet);
					//rtnGrd = copyGDParam(inDto, rtnGrd);
				}else{
					rtnGrd.setStatus("false");
					rtnGrd.addParam("ret", "-1");
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return rtnGrd;
		}
		
		
		
		/**
		 * 후판정정야드 Bed조회 (select box용)
		 * @ejb.interface-method
		 * @param GridData
		 * @return GridData
		 * @작성자 : 
		 * @작성일 : 
		 */
		public GridData getpPlateYdBedList3(GridData inDto) { 
			GridData 		rtnGrd 		= new GridData();
			JDTORecordSet   outRecSet  	= null;
			
			JspCommonDAO 	dao 		= new JspCommonDAO();
			JDTORecord 		recPara		= null;
			String s_YD_EQP_GP = "";
			
			try {
				
				
				s_YD_EQP_GP = getYdeqpGp_CODE(inDto.getParam("YD_EQP_GP").trim());
				
				// 파라미터 셋팅 
				recPara		= JDTORecordFactory.getInstance().create(); 			
				recPara.setField("YD_EQP_GP", s_YD_EQP_GP);
				
				// DAO 호출
				outRecSet = dao.getpPlateYdBedList2(recPara);
				
				if(outRecSet != null){
					rtnGrd = CmUtil.genGridData(inDto, outRecSet);
				}else{
					rtnGrd.setStatus("false");
					rtnGrd.addParam("ret", "-1");
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return rtnGrd;
		}
		
		
		
		
		
		/**
		 * 후판정정야드 Layer조회 (select box용)
		 * @ejb.interface-method
		 * @param GridData
		 * @return GridData
		 * @작성자 : 
		 * @작성일 : 
		 */
		public GridData getpPlateYdLayerList(GridData inDto) {
			GridData 		rtnGrd 		= new GridData();
			JDTORecordSet   outRecSet  	= null;
			
			JspCommonDAO 	dao 		= new JspCommonDAO();
			JDTORecord 		recPara		= null;
			String s_YD_EQP_GP = "";
			
			try {
				
				s_YD_EQP_GP = getYdeqpGp_CODE(inDto.getParam("YD_EQP_GP").trim());
				
				// 파라미터 셋팅 
				recPara		= JDTORecordFactory.getInstance().create(); 			
									
				recPara.setField("YD_STK_COL_GP", 	s_YD_EQP_GP+inDto.getParam("YD_STK_BED_NO").trim());
				
				// DAO 호출
				outRecSet = dao.getpPlateYdLayerList(recPara);
				
				if(outRecSet != null){
					rtnGrd = CmUtil.genGridData(inDto, outRecSet);
				}else{
					rtnGrd.setStatus("false");
					rtnGrd.addParam("ret", "-1");
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return rtnGrd;
		}
		
		
		
		/**
		 * 후판정정야드 Layer조회 (select box용)
		 * @ejb.interface-method
		 * @param GridData
		 * @return GridData
		 * @작성자 : 
		 * @작성일 : 
		 */
		public GridData getpPlateYdLayerList_L(GridData inDto) {
			GridData 		rtnGrd 		= new GridData();
			JDTORecordSet   outRecSet  	= null;
			
			JspCommonDAO 	dao 		= new JspCommonDAO();
			JDTORecord 		recPara		= null;
			String s_YD_EQP_GP = "";
			
			try {
				
				s_YD_EQP_GP = getYdeqpGp_CODE(inDto.getParam("YD_EQP_GP").trim());
				
				// 파라미터 셋팅 
				recPara		= JDTORecordFactory.getInstance().create(); 			
									
				recPara.setField("YD_STK_COL_GP", 	s_YD_EQP_GP+inDto.getParam("YD_STK_BED_NO").trim());
				
				// DAO 호출
				outRecSet = dao.getpPlateYdLayerList_L(recPara);
				
				if(outRecSet != null){
					rtnGrd = CmUtil.genGridData(inDto, outRecSet);
				}else{
					rtnGrd.setStatus("false");
					rtnGrd.addParam("ret", "-1");
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return rtnGrd;
		}
		
		
		
		
		/**
		 * 후판정정야드 Location조회 (select box용)
		 * @ejb.interface-method
		 * @param GridData
		 * @return GridData
		 * @작성자 : 
		 * @작성일 : 
		 */
		public GridData getpPlateYdLocationList(GridData inDto) {
			GridData 		rtnGrd 		= new GridData();
			JDTORecordSet   outRecSet  	= null;
			
			JspCommonDAO 	dao 		= new JspCommonDAO();
			JDTORecord 		recPara		= null;
			String s_YD_EQP_GP = "";
			
			try {
				
				s_YD_EQP_GP = getYdeqpGp_CODE(inDto.getParam("YD_EQP_GP").trim());
				
				// 파라미터 셋팅 
				recPara		= JDTORecordFactory.getInstance().create(); 			
									
				recPara.setField("YD_STK_COL_GP", 	s_YD_EQP_GP+inDto.getParam("YD_STK_BED_NO").trim());
				recPara.setField("YD_STK_LYR_NO", 	inDto.getParam("YD_STK_LYR_NO").trim());
				
				// DAO 호출
				outRecSet = dao.getpPlateYdLocationList(recPara);
				
				if(outRecSet != null){
					rtnGrd = CmUtil.genGridData(inDto, outRecSet);
					//rtnGrd = copyGDParam(inDto, rtnGrd);
				}else{
					rtnGrd.setStatus("false");
					rtnGrd.addParam("ret", "-1");
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return rtnGrd;
		}
		
		
		
		
		/**
		 * 후판정정야드 Layer조회 (select box용)
		 * @ejb.interface-method
		 * @param GridData
		 * @return GridData
		 * @작성자 : 
		 * @작성일 : 
		 */
		public GridData getpPlateYdLayerList2(GridData inDto) {
			GridData 		rtnGrd 		= new GridData();
			JDTORecordSet   outRecSet  	= null;
			
			JspCommonDAO 	dao 		= new JspCommonDAO();
			JDTORecord 		recPara		= null;
			String s_YD_EQP_GP = "";
			
			try {
				
				s_YD_EQP_GP = getYdeqpGp_CODE(inDto.getParam("YD_EQP_GP").trim());
				
				// 파라미터 셋팅 
				recPara		= JDTORecordFactory.getInstance().create(); 			
									
				recPara.setField("YD_STK_COL_GP", 	s_YD_EQP_GP+inDto.getParam("YD_STK_BED_NO").trim());
				
				// DAO 호출
				outRecSet = dao.getpPlateYdLayerList(recPara);
				
				if(outRecSet != null){
					rtnGrd = CmUtil.genGridData(inDto, outRecSet);
					//rtnGrd = copyGDParam(inDto, rtnGrd);
				}else{
					rtnGrd.setStatus("false");
					rtnGrd.addParam("ret", "-1");
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return rtnGrd;
		}
		
		
		
		
		/**
		 * 후판정정야드 북아웃 대상재조회 (select box용)
		 * @ejb.interface-method
		 * @param GridData
		 * @return GridData
		 * @작성자 : 
		 * @작성일 : 
		 */
		public GridData getpPlateYdBookoutSltDtlList(GridData inDto) {
			GridData 		rtnGrd 		= new GridData();
			JDTORecordSet   outRecSet  	= null;
			
			JspCommonDAO 	dao 		= new JspCommonDAO();
			JDTORecord 		recPara		= null;
			
			try {
				
				// 파라미터 셋팅 
				recPara		= JDTORecordFactory.getInstance().create(); 			
				recPara.setField("STL_NO", 	inDto.getParam("STL_NO").trim());
				
				// DAO 호출
				outRecSet = dao.getpPlateYdBookoutStlDtlList(recPara);
				
				if(outRecSet != null){
					rtnGrd = CmUtil.genGridData(inDto, outRecSet);
				}else{
					rtnGrd.setStatus("false");
					rtnGrd.addParam("ret", "-1");
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return rtnGrd;
		}
		
		
		
		/**
		 * 후판정정야드  후판정정야드 적치현황 상세조회
		 * @ejb.interface-method
		 * @param GridData
		 * @return GridData
		 * @작성자 : 
		 * @작성일 : 
		 */
		public GridData getpPlateYdStlList(GridData inDto) {
			GridData 		rtnGrd 		= new GridData();
			JDTORecordSet   outRecSet  	= null;
			
			JspCommonDAO 	dao 		= new JspCommonDAO();
			JDTORecord 		recPara		= null;
			
			try {
				
				String yd_Gp = getYdeqpGp_CODE(inDto.getParam("YD_EQP_GP").trim());
				
				// 파라미터 셋팅 
				recPara		= JDTORecordFactory.getInstance().create(); 			
				recPara.setField("YD_EQP_GP", 	yd_Gp);
				recPara.setField("YD_BED_GP", 	inDto.getParam("YD_BED_GP").trim());
				
				// DAO 호출
				outRecSet = dao.getpPlateYdStlList(recPara);
				
				if(outRecSet != null){
					rtnGrd = CmUtil.genGridData(inDto, outRecSet);
				}else{
					rtnGrd.setStatus("false");
					rtnGrd.addParam("ret", "-1");
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return rtnGrd;
		}
		
		
		
		/**
		 * 후판정정야드 북아웃 야드조회 (select box용)
		 * @ejb.interface-method
		 * @param GridData
		 * @return GridData
		 * @작성자 : 
		 * @작성일 : 
		 */
		public GridData getpPlateYdBookoutYdList(GridData inDto) {
			GridData 		rtnGrd 		= new GridData();
			JDTORecordSet   outRecSet  	= null;
			
			JspCommonDAO 	dao 		= new JspCommonDAO();
			JDTORecord 		recPara		= null;
			
			try {
				
				// 파라미터 셋팅 
				recPara		= JDTORecordFactory.getInstance().create(); 			
				recPara.setField("YD_LOC_GP", 	inDto.getParam("YD_LOC_GP").trim());
				
				// DAO 호출
				outRecSet = dao.getpPlateYdBookoutYdList(recPara);
				
				if(outRecSet != null){
					rtnGrd = CmUtil.genGridData(inDto, outRecSet);
				}else{
					rtnGrd.setStatus("false");
					rtnGrd.addParam("ret", "-1");
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return rtnGrd;
		}
		
		
		
		
		/**
		 * 야드관리 > 코일소재야드 / 코일제품창고 > 저장관리 > 이적작업진행관리(목록리스트 조회 -> 대상재 )
		 * @ejb.interface-method
		 * @param GridData
		 * @return GridData
		 * @작성자 : 박지열
		 * @작성일 : 2010.07.19
		 */
		public GridData getMvstkProgMgtList(GridData inDto) {
			GridData 		rtnGrd 		= new GridData();
			JDTORecordSet   outRecSet  	= null;
			
			JspCommonDAO 	dao 		= new JspCommonDAO();
			JDTORecord 		recPara		= null;
			
			try {
				
				// 파라미터 셋팅 
				recPara		= JDTORecordFactory.getInstance().create(); 			
				recPara.setField("V_YD_GP", 			inDto.getParam("YD_GP").trim());		/*야드구분*/
				recPara.setField("GUBUN", 				inDto.getParam("GUBUN").trim());		/*조회구분*/
				recPara.setField("V_YD_BAY_GP", 		inDto.getParam("YD_BAY_GP").trim());		/*동*/
				recPara.setField("V_YD_EQP_GP", 		inDto.getParam("YD_EQP_GP").trim());		/*스판*/
				recPara.setField("V_YD_STK_COL_NO", 	inDto.getParam("YD_STK_COL_NO").trim());		/*열번호*/
				recPara.setField("PAGENO", 				inDto.getParam("page_no").trim());
				recPara.setField("ROWCOUNT", 			inDto.getParam("rowCount").trim());
				
				
				// DAO 호출
				outRecSet = dao.getMvstkProgMgtList(recPara);
				
				if(outRecSet != null || outRecSet.size() == 0){
					rtnGrd = CmUtil.genGridData(inDto, outRecSet);
				}else{
					rtnGrd.setMessage("조회된 데이터가 없습니다.");
					rtnGrd.addParam("ret", "-1");
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return rtnGrd;
		}
		
		/**
		 * 야드관리 > 코일소재야드 / 코일제품창고 > 저장관리 > 이적작업진행관리(목록리스트 조회 -> 작업진행분 )
		 * @ejb.interface-method
		 * @param GridData
		 * @return GridData
		 * @작성자 : 박지열
		 * @작성일 : 2010.07.19
		 */
		public GridData getMvstkProgMgtWorkList(GridData inDto) {
			GridData 		rtnGrd 		= new GridData();
			JDTORecordSet   outRecSet  	= null;
			
			JspCommonDAO 	dao 		= new JspCommonDAO();
			JDTORecord 		recPara		= null;
			
			try {
				
				// 파라미터 셋팅 
				recPara		= JDTORecordFactory.getInstance().create(); 			
				recPara.setField("V_YD_GP", 			inDto.getParam("YD_GP").trim());		/*야드구분*/
				recPara.setField("GUBUN", 				inDto.getParam("GUBUN").trim());		/*조회구분*/
				recPara.setField("V_YD_BAY_GP", 		inDto.getParam("YD_BAY_GP").trim());		/*동*/
				recPara.setField("V_YD_EQP_GP", 		inDto.getParam("YD_EQP_GP").trim());		/*스판*/
				recPara.setField("V_YD_STK_COL_NO", 	inDto.getParam("YD_STK_COL_NO").trim());		/*열번호*/
				recPara.setField("PAGENO", 				inDto.getParam("page_no").trim());
				recPara.setField("ROWCOUNT", 			inDto.getParam("rowCount").trim());
				
				
				// DAO 호출
				outRecSet = dao.getMvstkProgMgtWorkList(recPara);
				
				if(outRecSet != null || outRecSet.size() == 0){
					rtnGrd = CmUtil.genGridData(inDto, outRecSet);
				}else{
					rtnGrd.setMessage("조회된 데이터가 없습니다.");
					rtnGrd.addParam("ret", "-1");
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return rtnGrd;
		}
		
		
		/**
		 * 야드관리 > 코일소재야드 / 코일제품창고 > 저장관리 > 이적작업진행관리(동별 이적/이송건수 조회 -> 대상재)
		 * @ejb.interface-method
		 * @param GridData
		 * @return GridData
		 * @작성자 : 박지열
		 * @작성일 : 2010.07.19
		 */
		public GridData getMvstkProgMgtBayCnt(GridData inDto) {
			GridData 		rtnGrd 		= new GridData();
			JDTORecordSet   outRecSet  	= null;
			
			JspCommonDAO 	dao 		= new JspCommonDAO();
			JDTORecord 		recPara		= null;
			
			try {
				
				// 파라미터 셋팅 
				recPara		= JDTORecordFactory.getInstance().create(); 			
				recPara.setField("V_YD_GP", 			inDto.getParam("YD_GP").trim());		/*야드구분*/
				recPara.setField("V_YD_BAY_GP", 		inDto.getParam("YD_BAY_GP").trim());		/*동*/
				
				
				// DAO 호출
				outRecSet = dao.getMvstkProgMgtBayCnt(recPara);
				
				if(outRecSet != null || outRecSet.size() == 0){
					rtnGrd = CmUtil.genGridData(inDto, outRecSet);
				}else{
					rtnGrd.setMessage("조회된 데이터가 없습니다.");
					rtnGrd.addParam("ret", "-1");
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return rtnGrd;
		}
		
		
		/**
		 * 야드관리 > 코일소재야드 / 코일제품창고 > 저장관리 > 이적작업진행관리(동별 이적/이송건수 조회 -> 작업진행분)
		 * @ejb.interface-method
		 * @param GridData
		 * @return GridData
		 * @작성자 : 박지열
		 * @작성일 : 2010.07.19
		 */
		public GridData getMvstkProgMgtWorkBayCnt(GridData inDto) {
			GridData 		rtnGrd 		= new GridData();
			JDTORecordSet   outRecSet  	= null;
			
			JspCommonDAO 	dao 		= new JspCommonDAO();
			JDTORecord 		recPara		= null;
			
			try {
				
				// 파라미터 셋팅 
				recPara		= JDTORecordFactory.getInstance().create(); 			
				recPara.setField("V_YD_GP", 			inDto.getParam("YD_GP").trim());		/*야드구분*/
				recPara.setField("V_YD_BAY_GP", 		inDto.getParam("YD_BAY_GP").trim());		/*동*/
				
				
				// DAO 호출
				outRecSet = dao.getMvstkProgMgtWorkBayCnt(recPara);
				
				if(outRecSet != null || outRecSet.size() == 0){
					rtnGrd = CmUtil.genGridData(inDto, outRecSet);
				}else{
					rtnGrd.setMessage("조회된 데이터가 없습니다.");
					rtnGrd.addParam("ret", "-1");
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return rtnGrd;
		}
		
		
		private String getYdeqpGp_CODE(String YdeqpGp){
			
			String YdeqpGp_CD	= "";
			
			if(YdeqpGp.equals("극후물")){ 
				YdeqpGp_CD = "01";
			}else if(YdeqpGp.equals("냉각대출측")){
				YdeqpGp_CD = "02";
			}else if(YdeqpGp.equals("#1GAS")){
				YdeqpGp_CD = "03";
			}else if(YdeqpGp.equals("#1전단")){
				YdeqpGp_CD = "04";
			}else if(YdeqpGp.equals("#2전단")){
				YdeqpGp_CD = "05";
			}else if(YdeqpGp.equals("#2GAS")){
				YdeqpGp_CD = "06";
			}else if(YdeqpGp.equals("극후물GAS")){
				YdeqpGp_CD = "07";
			}else if(YdeqpGp.equals("전단GAS")){
				YdeqpGp_CD = "08";
			}else if(YdeqpGp.equals("냉간교정야드")){
				YdeqpGp_CD = "09";
			}else if(YdeqpGp.equals("보수장")){
				YdeqpGp_CD = "10";
			}else if(YdeqpGp.equals("보수장GAS")){
				YdeqpGp_CD = "11";
			}else if(YdeqpGp.equals("열처리")){ 
				YdeqpGp_CD = "12";
			}else if(YdeqpGp.equals("ShotBlast")){
				YdeqpGp_CD = "13";
			}else if(YdeqpGp.equals("열처리GAS")){
				YdeqpGp_CD = "14";
			}else if(YdeqpGp.equals("제품창고#1GAS")){
				YdeqpGp_CD = "15";
			}else if(YdeqpGp.equals("제품창고#2GAS")){
				YdeqpGp_CD = "16";
			}else if(YdeqpGp.equals("제품창고#3GAS")){
				YdeqpGp_CD = "17";
			}else if(YdeqpGp.equals("제품창고#4GAS")){
				YdeqpGp_CD = "18";
			}
			
			
			return YdeqpGp_CD;
		}
		
		 /**
		    * LOT ID 조회하는 메소드
		    * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
		    * @param inDto
		    * @return
		    * @throws DAOException
		    */
		   public JDTORecordSet getCodeForLot(JDTORecord inDto) throws DAOException {

			    JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
				JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
				
				YdStkColDao ydStkColDao			= new YdStkColDao();
				String szOperationName	= "LOT ID 조회";
				String szMethodName		= "getCodeForLot";
				String szMsg       	 	= "";		
				
				int intRtnVal = 0;
				
				try {
					
					szMsg = "[JSP Session : " + szOperationName + "] 메소드 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					recPara.setField("CHK",	inDto.getField("CHK"));
					recPara.setField("TRN_EQP_CD",	inDto.getField("TRN_EQP_CD"));		
					recPara.setField("DONG",	inDto.getField("DONG"));		
					/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkLotID*/
					intRtnVal = ydStkColDao.getYdStkcol(recPara, outRecSet, 308);
					
					if (intRtnVal < 0) {
						if (intRtnVal == -1) {
							szMsg = "[JSP Session : " + szOperationName + "] 오류발생[1] - 반환값 : " + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						} else {
							szMsg = "[JSP Session : " + szOperationName + "] parameter error - 반환값 : " + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						}
						//return outRecSet;
						return outRecSet;
					} // end of if
					
					szMsg = "[JSP Session : " + szOperationName + "] 메소드 끝";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					
				} catch (Exception e) {
					// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
					throw new DAOException(getClass().getName() + e.getMessage(),e);
				} finally {
				}
				return outRecSet;
		  }
		
		   /**
			 * [A] 오퍼레이션명 : Machine Scarfint 실적 조회
			 *
			 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
			 * @param inRecord
			 * @return
			 * @throws DAOException
			 */
			public GridData getMachineScarfingWr(GridData inDto) throws DAOException {
				GridData 		rtnGrd 		= new GridData();
				JDTORecordSet   outRecSet  	= null;
				
				JspCommonDAO 	dao 		= new JspCommonDAO();
				JDTORecord 		recPara		= null;
				
				try {
					
					// 파라미터 셋팅 
					recPara		= JDTORecordFactory.getInstance().create(); 			
					recPara.setField("V_FR_DD", 			inDto.getParam("V_FR_DD").trim());	 
					recPara.setField("V_DD_GP", 			inDto.getParam("V_DD_GP").trim());	 
					recPara.setField("V_TO_DD", 			inDto.getParam("V_TO_DD").trim());	 
					recPara.setField("V_DD_GP", 			inDto.getParam("V_DD_GP").trim());	 
					recPara.setField("V_SLAB_WO_RT_CD", 	inDto.getParam("V_SLAB_WO_RT_CD").trim());		 
					recPara.setField("V_PAGENO", 			inDto.getParam("page_no").trim());
					recPara.setField("V_ROWCOUNT", 			inDto.getParam("rowCount").trim());
					
					// DAO 호출
					outRecSet = dao.getMachineScarfingWr(recPara);
					
					if(outRecSet != null || outRecSet.size() == 0){
						rtnGrd = CmUtil.genGridData(inDto, outRecSet);
					}else{
						rtnGrd.setMessage("조회된 데이터가 없습니다.");
						rtnGrd.addParam("ret", "-1");
					}
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return rtnGrd;
			}//end of getMachineScarfingWr
			
			/**
			 * [A] 오퍼레이션명 : Machine Scarfint 실적 요약 조회
			 *
			 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
			 * @param inRecord
			 * @return
			 * @throws DAOException
			 */
			public GridData getMachineScarfingSummary(GridData inDto) throws DAOException {
				GridData 		rtnGrd 		= new GridData();
				JDTORecordSet   outRecSet  	= null;
				
				JspCommonDAO 	dao 		= new JspCommonDAO();
				JDTORecord 		recPara		= null;
				
				try {
					
					// 파라미터 셋팅 
					recPara		= JDTORecordFactory.getInstance().create(); 			
					recPara.setField("V_FR_DD", 			inDto.getParam("V_FR_DD").trim());	 
					recPara.setField("V_DD_GP", 			inDto.getParam("V_DD_GP").trim());	 
					recPara.setField("V_TO_DD", 			inDto.getParam("V_TO_DD").trim());	 
					recPara.setField("V_DD_GP", 			inDto.getParam("V_DD_GP").trim());	 
					recPara.setField("V_SLAB_WO_RT_CD", 	inDto.getParam("V_SLAB_WO_RT_CD").trim());		  

					// DAO 호출
					outRecSet = dao.getMachineScarfingSummary(recPara);
					
					if(outRecSet != null || outRecSet.size() == 0){
						rtnGrd = CmUtil.genGridData(inDto, outRecSet);
					}else{
						rtnGrd.setMessage("조회된 데이터가 없습니다.");
						rtnGrd.addParam("ret", "-1");
					}
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return rtnGrd;
			}//end of getMachineScarfingSummary
			

			/**
			 * 수요가 그룹핑
			 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
			 * @param inDto
			 * @return
			 * @throws DAOException
			 */
			public JDTORecordSet getDemenderNoGroup(JDTORecord inDto) throws DAOException {
				JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
				
				YdUtils ydUtils = new YdUtils();
				
				String szMsg        = "";		
				String szMethodName = "getDemenderNoGroup";
				String szOperationName = "수요가 그룹핑";
				
				
				String szYD_GP = null;
				
				YdStockDao ydStockDao = new YdStockDao();
				
				int intRtnVal = 0;
				
				try {		
					
					szMsg = "JSP-SESSION [수요가 그룹핑] 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
					
					
					szYD_GP = StringHelper.evl(inDto.getFieldString("YD_GP"), "");
					
					szMsg = "[JSP Session] " + szOperationName + " 파라미터 확인 : 야드구분[" + szYD_GP + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					if(szYD_GP.equals("S")){
						/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getDemenderNoGroup_PIDEV*/
						intRtnVal  = ydStockDao.getYdStock(inDto, outRecSet, 618);
					}else{
						intRtnVal  = ydStockDao.getYdStock(inDto, outRecSet, 127);
					}
					
					if (intRtnVal <= 0) {
						if (intRtnVal == -1) {
							szMsg = "[JSP Session] " + szOperationName + " 오류발생 1 : 반환값 - " + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						}else if (intRtnVal == 0) {
							szMsg = "[JSP Session] " + szOperationName + " 대상재가 존재하지 않음 : 반환값 - " + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						} else {
							szMsg = "[JSP Session] " + szOperationName + " 오류발생 2 : 반환값 - " + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						}
						//return outRecSet;
						return outRecSet;
					} // end of if
					
					szMsg = "[JSP Session] " + szOperationName + " 성공 : 레코드 수 - " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					

					szMsg = "JSP-SESSION [수요가 그룹핑] 끝";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
					
				} catch (Exception e) {
					// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
					throw new DAOException(getClass().getName() + e.getMessage(),e);
				} finally {
				}
				return outRecSet;
			}//end of getDemenderNoGroup
			
			
			/**
			 * 운송장비코드 그룹핑
			 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
			 * @param inDto
			 * @return
			 * @throws DAOException
			 */
			public JDTORecordSet getDemenderNoGroup2(JDTORecord inDto) throws DAOException {
				JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
				
				YdUtils ydUtils = new YdUtils();
				
				String szMsg        = "";		
				String szMethodName = "getDemenderNoGroup2";
				String szOperationName = "운송장비코드 그룹핑";
				
				
				String szYD_GP = null;
				
				YdStockDao ydStockDao = new YdStockDao();
				
				int intRtnVal = 0;
				
				try {		
					
					szMsg = "JSP-SESSION [운송장비코드 그룹핑] 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
					
					
					szYD_GP = StringHelper.evl(inDto.getFieldString("YD_GP"), "");
					
					szMsg = "[JSP Session] " + szOperationName + " 파라미터 확인 : 야드구분[" + szYD_GP + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
//PIDEV_S :병행가동용:PI_YD
					inDto.setField("PI_YD",    	szYD_GP);		
					/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getDemenderNoGroup2_PIDEV*/
					intRtnVal  = ydStockDao.getYdStock(inDto, outRecSet, 723);
					
					if (intRtnVal <= 0) {
						if (intRtnVal == -1) {
							szMsg = "[JSP Session] " + szOperationName + " 오류발생 1 : 반환값 - " + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						}else if (intRtnVal == 0) {
							szMsg = "[JSP Session] " + szOperationName + " 대상재가 존재하지 않음 : 반환값 - " + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						} else {
							szMsg = "[JSP Session] " + szOperationName + " 오류발생 2 : 반환값 - " + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						}
						//return outRecSet;
						return outRecSet;
					} // end of if
					
					szMsg = "[JSP Session] " + szOperationName + " 성공 : 레코드 수 - " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					

					szMsg = "JSP-SESSION [수요가 그룹핑] 끝";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
					
				} catch (Exception e) {
					// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
					throw new DAOException(getClass().getName() + e.getMessage(),e);
				} finally {
				}
				return outRecSet;
			}//end of getDemenderNoGroup2
			
			
			/**
			 *  차량 공통 정보 조회
			 *
			 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
			 * @param inDto
			 * @return JDTORecordSet
			 * @throws DAOException
			 */
			public JDTORecordSet getCarInfo(JDTORecord inDto) throws DAOException {
				
				JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
				JDTORecord       recTemp         = JDTORecordFactory.getInstance().create();
				JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");	
				JDTORecord       recCarProgStat  = null; 
				
				int nRtnVal = 0;
				String szMsg = "";
				String szMethodName = "getCarInfo";
				String szOperationName = "차량 공통 정보 조회";	
				YdStockDao ydStockDao = new YdStockDao();
				
				int intRtnVal = 0;
				String szCarProgStat = null;
				try {
					
					szMsg = "JSP-SESSION [" + szOperationName + "]시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
					
					recPara.setField("CAR_NO",    	 yddatautil.setDataDefault(inDto.getField("CAR_NO"), ""));
					//PIDEV_S :병행가동용:PI_YD
					recPara.setField("PI_YD",    	 yddatautil.setDataDefault(inDto.getField("PI_YD"), "*")); 
					
					/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCarInfo_PIDEV*/
					intRtnVal  = ydStockDao.getYdStock(recPara, outRecSet, 728);
					
					if (intRtnVal <= 0) {
						if (intRtnVal == -1) {
							szMsg = "[JSP Session] " + szOperationName + " 오류발생 1 : 반환값 - " + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						}else if (intRtnVal == 0) {
							szMsg = "[JSP Session] " + szOperationName + " 대상재가 존재하지 않음 : 반환값 - " + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						} else {
							szMsg = "[JSP Session] " + szOperationName + " 오류발생 2 : 반환값 - " + intRtnVal;
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
				
				szMsg = "JSP-SESSION [" + szOperationName + "] 끝";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
				
				
				return outRecSet;
			}//end of getCarInfo
			
			
			/**
			 *  스케쥴코드조회 (화면:크레인작업시지 작성)
			 *
			 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
			 * @param inDto
			 * @return JDTORecordSet
			 * @throws DAOException
			 */
			public JDTORecordSet getSchCodeNew(JDTORecord inDto) throws DAOException {
				JDTORecord       recPara    = JDTORecordFactory.getInstance().create();
				JDTORecordSet    outRecSet  = JDTORecordFactory.getInstance().createRecordSet("retTmp");
				
				String szMsg        = "";		
				String szMethodName = "getSchCodeNew";
				int intRtnVal = 0;
			
				YdLocSrchRngDao ydLocSrchRngDao = new YdLocSrchRngDao();	
				try {
					
					//Top Grid
					
					szMsg = "JSP-SESSION [ 스케쥴코드조회 (화면:크레인작업시지 작성)]시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
					recPara.setField("YD_BAY_GP",      yddatautil.setDataDefault(inDto.getField("GBN"), ""));
					
					/*com.inisteel.cim.yd.dao.ydlocsrchbeddao.YdLocsrchbedDao.getSchCodeNew*/
					intRtnVal      =  ydLocSrchRngDao.getYdLocsrchrng(recPara, outRecSet, 304);
					
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
					
					// 레코드셋을 앞으로 커서를 위치 시켜준다 .
					outRecSet.first();
					
				} catch (Exception e) {
					// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
					throw new DAOException(getClass().getName() + e.getMessage(),e);
				} finally {
				}
				
				szMsg = "JSP-SESSION [ 스케쥴코드조회 (화면:크레인작업시지 작성)]끝";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				
				return outRecSet;
			}//end of getSchCodeNew
			
			
			
			/**
			 * 마킹최종고객사코드
			 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
			 * @param inDto
			 * @return
			 * @throws DAOException
			 */
			public JDTORecordSet getMarkingDemenderNo(JDTORecord inDto) throws DAOException {
				JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
				
				YdUtils ydUtils = new YdUtils();
				
				String szMsg        = "";		
				String szMethodName = "getMarkingDemenderNo";
				String szOperationName = "마킹최종고객사코드";
				
				
				String szYD_GP = null;
				
				YdStockDao ydStockDao = new YdStockDao();
				
				int intRtnVal = 0;
				
				try {		
					
					szMsg = "JSP-SESSION [마킹최종고객사코드] 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
					
					
					szYD_GP = StringHelper.evl(inDto.getFieldString("YD_GP"), "");
					
					szMsg = "[JSP Session] " + szOperationName + " 파라미터 확인 : 야드구분[" + szYD_GP + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
//PIDEV_S :병행가동용:PI_YD
					inDto.setField("PI_YD",    	szYD_GP);			
					/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getMarkingDemenderNo_PIDEV*/
					intRtnVal  = ydStockDao.getYdStock(inDto, outRecSet, 732);
					
					if (intRtnVal <= 0) {
						if (intRtnVal == -1) {
							szMsg = "[JSP Session] " + szOperationName + " 오류발생 1 : 반환값 - " + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						}else if (intRtnVal == 0) {
							szMsg = "[JSP Session] " + szOperationName + " 대상재가 존재하지 않음 : 반환값 - " + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						} else {
							szMsg = "[JSP Session] " + szOperationName + " 오류발생 2 : 반환값 - " + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						}
						//return outRecSet;
						return outRecSet;
					} // end of if
					
					szMsg = "[JSP Session] " + szOperationName + " 성공 : 레코드 수 - " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					

					szMsg = "JSP-SESSION [마킹최종고객사코드] 끝";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
					
				} catch (Exception e) {
					// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
					throw new DAOException(getClass().getName() + e.getMessage(),e);
				} finally {
				}
				return outRecSet;
			}//end of getMarkingDemenderNo
			
	///////////////////////////////////////////////////////////////////////////////
	///                          YdJspCoommonSeEJB                              ///
	///////////////////////////////////////////////////////////////////////////////

			
			///////////////////////////////////////////////////////////////////////////////
			///                          전사물류개선 프로젝트 2021.1.6                  ///
			///////////////////////////////////////////////////////////////////////////////
			/**
			 * 크레인작업Type 변경 유인/무인/리모컨/..
			 *
			 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
			 * @param inDto
			 * @return Boolean
			 * @throws DAOException
			 */
			public String[] updCrnJobType(JDTORecord[] recMsg) throws DAOException {
				
				String[] szRtnMsg = new String[recMsg.length];

				String szLogMsg   = "";
				String szMethodName = "updCrnJobType";
				String szYD_EQP_ID = "";
				String szYD_EQP_WRK_MODE2 = "";
				String szYD_EQP_WRK_MODE = "";
				String szYD_CRN_SCH_ID = "";
				String szYdGp = "";
				String szEjbConName = "";
				String szOperationName = "설비 유인, 무인, 리모컨 설정";

				JDTORecord recPara  = JDTORecordFactory.getInstance().create();
				EJBConnector ejbConn = null;

				try {
					//설비 ON_LINE, OFF_LINE 설정

					szLogMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

					for(int x=0;x<recMsg.length;x++){
						szYD_EQP_ID = yddatautil.setDataDefault(recMsg[x].getField("YD_EQP_ID"), "");
						szYD_EQP_WRK_MODE2 = yddatautil.setDataDefault(recMsg[x].getField("YD_EQP_WRK_MODE2"), "");
						szYD_EQP_WRK_MODE = yddatautil.setDataDefault(recMsg[x].getField("YD_EQP_WRK_MODE"), "");
						szYD_EQP_WRK_MODE = yddatautil.setDataDefault(recMsg[x].getField("YD_EQP_WRK_MODE"), "");
						szYD_CRN_SCH_ID  = yddatautil.setDataDefault(recMsg[x].getField("YD_CRN_SCH_ID"), "");
						//입출입  상태
						recPara.setField("YD_EQP_ID", szYD_EQP_ID);
						recPara.setField("YD_EQP_WRK_MODE2", szYD_EQP_WRK_MODE2);
					}

					szYdGp = szYD_EQP_ID.substring(0,1);
					szLogMsg = "[JSP SESSION -  (야드구분  :  " + szYdGp +") ] 입니다";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
 
					//통합슬라브의 경우는 복구 리스케줄 정보강없음 L2와 전문전송을 하지 않는다.
					szLogMsg = "[JSP SESSION -  (설비 운전모드 전환     "+ szEjbConName +")을 호출  시작";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);

					//해당 메소드는 void 형이라 리턴이 없습니다.
					//운전모드 변경
					JDTORecord jrParam  = JDTORecordFactory.getInstance().create();
					jrParam.setField("MSG_ID"      , "Y9YDL003"); //운전모드전환
					jrParam.setField("YD_CRN_SCH_ID", ""); // 크레인스케줄ID
					jrParam.setField("YD_EQP_ID" , szYD_EQP_ID); 
					jrParam.setField("YD_EQP_WRK_MODE" , szYD_EQP_WRK_MODE); //야드설비작업Mode(1:On-Line, 2:Off-Line)
					jrParam.setField("YD_EQP_WRK_MODE2", szYD_EQP_WRK_MODE2); //A:무인, R:리모컨, E:정비, M:유인
					jrParam.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);  
					ejbConn = new EJBConnector("default", "PlateYdRcvL2SeEJB", this);
					ejbConn.trx("rcvY9YDL003", new Class[] { JDTORecord.class }, new Object[] { jrParam });


					szLogMsg = "[JSP SESSION - (설비 운전모드 전환     "+ szEjbConName +")을 호출  끝";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG); 

					szLogMsg = "[JSP Session : "+szOperationName+"] 메소드 끝 ";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

				} catch (Exception e) {
					// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
					throw new DAOException(getClass().getName() + e.getMessage(),e);
				} finally {
				}

				return szRtnMsg;
			}
			
			/**
			 * 차량스케줄/차량Point삭제
			 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
			 * @param inDto
			 * @return
			 * @throws DAOException
			 */
			public String delCarSchNCarPoint4G(JDTORecord[] inDto) throws DAOException {
				String szRtnMsg				= null;
				String szMsg 				= "";
				String szMethodName			= "delCarSchNCarPoint4G";
				String szOperationName 		= "차량스케줄/차량Point삭제";
				
				String szYD_CAR_SCH_ID		= null;
				
				try {
					szMsg = "[Jsp Session : "+szOperationName+"] --------------------- 메소드 시작 ---------------------";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
					
					for(int i = 0; i < inDto.length; i++ ) {
						
						szYD_CAR_SCH_ID		=	ydDaoUtils.paraRecChkNull(inDto[i], "YD_CAR_SCH_ID");
					
						szMsg = "[Jsp Session : "+szOperationName+"] ["+(i + 1)+"]차량스케줄["+szYD_CAR_SCH_ID+"] 삭제 시작";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
						// 전사물류개선 배차취소관련 신규모듈 적용여부 2021. 1. 6
						szRtnMsg		= YdCommonUtils.delCarSchNCarPointByCarSchIdNew(inDto[i], szMethodName);					
					
						szMsg = "[Jsp Session : "+szOperationName+"] ["+(i + 1)+"]차량스케줄["+szYD_CAR_SCH_ID+"] 삭제 완료 - 메세지 : " + szRtnMsg;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
					}
					
					szMsg = "[Jsp Session : "+szOperationName+"] --------------------- 메소드 끝 ---------------------";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				}catch(Exception ex) {
					szMsg = "[Jsp Session : "+szOperationName+"] 오류발생 : " + ex.getMessage();
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				
				return YdConstant.RETN_CD_SUCCESS;
			}
			
			/**
			 * 포인트개폐처리
			 *
			 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
			 * @param inDto
			 * @return
			 */
			public void procCoilYdGdsPntUnitCL4G(JDTORecord [] inDto) {
				int       	intRtnVal    		= 0;
				String    	szMsg        		= null;
				String    	szMethodName 		= "procCoilYdGdsPntUnitCL4G";
				String	 	szOperationName		= "포인트개폐처리";
				String		szRtnMsg			= null;
				
				YdUtils ydUtils = new YdUtils();
						
				//Delegate
				YdDelegate ydDelegate           = new YdDelegate();
				
				JDTORecord    recPara  			= null;
				JDTORecord    recTemp  			= null;
				JDTORecordSet	rsResult		= null;
				
				YdStkColDao ydStkColDao 		= new YdStkColDao();
				
				szMsg        					= "";
				//String szTemp       			= "";

				String szYD_STK_COL_GP			 = "";
				String szYD_STK_COL_ACT_STAT	 = "";
				String szYD_STK_COL_ACT_STAT_PARAM	 = "";
				String szYD_FRM_YN = ""; 
				
				boolean isSendable				= true;

				try {
					
					szMsg = "["+szOperationName+"] --------------- 메소드 시작 - 적치열건수["+inDto.length+"] ---------------";
					ydUtils.putLog(szSessionName, szMethodName,  szMsg, YdConstant.INFO);
					
					rsResult = JDTORecordFactory.getInstance().createRecordSet("");
					recPara  = JDTORecordFactory.getInstance().create();
					
					for(int x=0;x<inDto.length;x++){					
						//----------------------------------------------------------------------------------------------
						//	적치열 조회
						//----------------------------------------------------------------------------------------------
						
						szYD_STK_COL_GP = yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_GP"),     "");
						szYD_STK_COL_ACT_STAT_PARAM = yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_ACT_STAT"),     "");
						szYD_FRM_YN = yddatautil.setDataDefault(inDto[x].getField("YD_FRM_YN"),     "");
						
						recPara.setField("YD_STK_COL_GP",   		szYD_STK_COL_GP);
						
						szMsg = "["+szOperationName+"] 적치열["+szYD_STK_COL_GP+"] 조회 시작 ";
						ydUtils.putLog(szSessionName, szMethodName,  szMsg, YdConstant.DEBUG);
						
						szRtnMsg = DaoManager.getYdStkcol(recPara, rsResult, 0);
						
						szMsg = "["+szOperationName+"] 적치열["+szYD_STK_COL_GP+"] 조회 완료 - 반환메세지 : " + szRtnMsg;
						ydUtils.putLog(szSessionName, szMethodName,  szMsg, YdConstant.DEBUG);
						
						if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
							continue;
						}
						
						rsResult.first();
						recTemp = rsResult.getRecord();
						szYD_STK_COL_ACT_STAT = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_COL_ACT_STAT");
										
						//----------------------------------------------------------------------------------------------
						
						
						//----------------------------------------------------------------------------------------------
						//	적치열 수정
						//----------------------------------------------------------------------------------------------
						
						recPara.setField("YD_STK_COL_ACT_STAT",    	szYD_STK_COL_ACT_STAT_PARAM);
						//수정
						recPara.setField("MODIFIER",   			 yddatautil.setDataDefault(inDto[x].getFieldString("YD_USER_ID"),""));
						
				        intRtnVal = ydStkColDao.updYdStkcol(recPara,0);
				        
						if (intRtnVal < 0) {
							if (intRtnVal == -1) {
								szMsg = "["+szOperationName+"] 적치열 수정, ErrorCode:" + intRtnVal;
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							} else {
								szMsg = "["+szOperationName+"] 적치열 수정 parameter error!!!, ErrorCode:" + intRtnVal;
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							}
						} // end of if
						
						//----------------------------------------------------------------------------------------------
						//후판일 경우에만 실행
						if("T".equals(szYD_STK_COL_GP.substring(0,1))){
							
							YdPlateCommDAO	commDao 		= new YdPlateCommDAO();
							
							recPara = JDTORecordFactory.getInstance().create();
							
							recPara.setField("YD_CARPNT_CD"			, szYD_STK_COL_GP.substring(0,1)+szYD_STK_COL_GP.substring(4,5)+szYD_STK_COL_GP.substring(1,2)+szYD_STK_COL_GP.substring(5,6));
							recPara.setField("YD_STK_COL_ACT_STAT"	, szYD_STK_COL_ACT_STAT_PARAM);					
							// 전사물류개선 형상유무도 변경가능하도록 수정조치함
							recPara.setField("YD_FRM_YN"	, szYD_FRM_YN);					
							
							intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateCarPointByStatusCarFrmYn");	
						}
						
						//----------------------------------------------------------------------------------------------
						//	포인트개폐 전송
						//----------------------------------------------------------------------------------------------
						//szTemp = szYD_STK_COL_GP;
						
						
						recPara  = JDTORecordFactory.getInstance().create();
						//TC CCODE
						recPara.setField("JMS_TC_CD",		"YDTSJ012");
						
						//야드구분
						recPara.setField("YD_GP", 				szYD_STK_COL_GP.substring(0,1));
						//야드적치열 구분
						recPara.setField("YD_STK_COL_GP", 		szYD_STK_COL_GP);
						
						if(szYD_STK_COL_ACT_STAT_PARAM.equals ("C") 
								|| szYD_STK_COL_ACT_STAT_PARAM.equals("L")
								|| szYD_STK_COL_ACT_STAT_PARAM.equals("R")){
							if( szYD_STK_COL_ACT_STAT.equals("N")) {			//사용불가
								szMsg = "["+szOperationName+"] 적치열["+szYD_STK_COL_GP+"] - 변경된 야드적치열활성상태["+szYD_STK_COL_ACT_STAT_PARAM+"]에 대한 포인트 OPEN 처리 전문 송신 ";
								ydUtils.putLog(szSessionName, szMethodName,  szMsg, YdConstant.DEBUG);
								
								recPara.setField("PNT_UNIT_CL_GP",	"Y");
							}else{
								isSendable = false;
							}
						}else if(szYD_STK_COL_ACT_STAT_PARAM.equals ("N")){
							
							szMsg = "["+szOperationName+"] 적치열["+szYD_STK_COL_GP+"]에 대한 사용불가(포인트 CLOSE) 처리 전문 송신 ";
							ydUtils.putLog(szSessionName, szMethodName,  szMsg, YdConstant.DEBUG);
							
							recPara.setField("PNT_UNIT_CL_GP",		"N");
						}else{
							szMsg = "["+szOperationName+"] 포인트 개폐구분의 값이 없습니다 !!!";
							ydUtils.putLog(szSessionName, szMethodName,  szMsg, YdConstant.ERROR);
						}
						
						if( isSendable ) {

							ydUtils.displayRecord(szOperationName, recPara);
						
							//Delegate 연결
							//ydDelegate.sendMsg(recPara);
							ydDelegate.sendMsg(recPara);
							
							szMsg = "["+szOperationName+"] 적치열["+szYD_STK_COL_GP+"]에 대한 포인트개폐 전문 송신 완료 ";
							ydUtils.putLog(szSessionName, szMethodName,  szMsg, YdConstant.DEBUG);
							
							recPara = JDTORecordFactory.getInstance().create();
							recPara.setField("YD_INFO_SYNC_CD", "3");							//1:동,2:SPAN,3:열,4:BED
							recPara.setField("YD_GP", 			szYD_STK_COL_GP.substring(0, 1));
							recPara.setField("YD_STK_COL_GP", 	szYD_STK_COL_GP);
							
							YdCommonUtils.sndStrPosSpecToL2(recPara);
							
							szMsg = "["+szOperationName+"] 적치열["+szYD_STK_COL_GP+"]에 대한 야드저장위치제원 전문 송신 완료 ";
							ydUtils.putLog(szSessionName, szMethodName,  szMsg, YdConstant.DEBUG);
						}
						//----------------------------------------------------------------------------------------------
					}
					
					szMsg = "["+szOperationName+"] --------------- 메소드 끝 --------------- ";
					ydUtils.putLog(szSessionName, szMethodName,  szMsg, YdConstant.INFO);
					
				} catch (Exception e) {
					// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
					szMsg = "["+szOperationName+"] 예외 발생 - " + e.getMessage();
					ydUtils.putLog(szSessionName, szMethodName,  szMsg, YdConstant.ERROR);
					throw new DAOException(getClass().getName() + e.getMessage(),e);
				} finally {
				}
			}	// end of procCoilYdGdsPntUnitCL
			
			
			
			/**
			 * 권하위치 변경 (크레인작업관리 화면)
			 *
			 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
			 * @param inDto
			 * @return
			 * @throws DAOException
			 */
			public JDTORecord updToPosFix4G(JDTORecord[] inDto) throws DAOException {
				
				int intRtnVal          = 0;				
				String szLogMsg           = null;
				String szMethodName    = "updToPosFix4G";		
				String szOperationName = "권하위치 변경 (크레인작업관리 화면)";
				
				String szStkPos        = null;
				String szStkColGp      = null;
				String szStkBedNo      = null;
				String szStkLyrNo      = null;
				
				JDTORecord    recPara  = null;
				JDTORecord    recInPara  = null;
				JDTORecord    recTemp  = null;
				JDTORecord    recSet   = null;
				JDTORecord    recSetTmp= null;
				JDTORecord    recInTemp= null;
				
				String szOldStkPos     = null;
				String szOldStkColGp   = null;
				String szOldStkBedNo   = null;
				String szOldStkLyrNo   = null;
				
					
				YdStkLyrDao ydStkLyrDao        = new YdStkLyrDao();
				YdCrnSchDao ydCrnSchDao        = new YdCrnSchDao();
				YdCrnWrkMtlDao  ydCrnWrkMtlDao = new YdCrnWrkMtlDao();
				YdDelegate ydDelegate 	= new YdDelegate();
				YdStkColDao ydStkColDao     = new YdStkColDao();
				JDTORecordSet    outRecSet  = null;
				JDTORecordSet    outRecSetTmp = null;
				
				String szYdWrkProgStat 		="";		
				String szSendYdWrkProgStat 	="";
				String szYdGp  				="";
				String szYdSchCd 			="";
			
				String szJMS_TC_CD 			="";
				String szEjbMethod 			="";
			    String szYD_EQP_ID 			="";
			    String szRtnMsg 			="";
			    String szYdSchId 			="";
			    
			    String szYdGpTemp 			="";
			    String szEqpGp 				=""; // 변경 설비구분 
			    String szEqpGpBefo 			=""; // 기존 설비구분 
			    String szYdWbookId 			=""; //작업예약 ID
			    String szRtnMsg1			= null;
			    String szUserExceptionId = "";
			    EJBConnector ejbConn = null;
				
			    // 전사물류개선 2021.1.6 L9시스템 여부
			    boolean isSendToEaiY9 = false;
			    JDTORecord    rtnJto  = JDTORecordFactory.getInstance().create();
			    boolean isAutoCrnSendYn = false; // 권하위치변경 수정가능 여부 판단
			    
			    

			    
				try {
					szLogMsg = "JSP-SESSION [권하위치 변경 (크레인작업관리 화면)] 시작";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
					

					// 2021. 12. 09
					// 크레인스케쥴 에러가 났을 경우
				    List aErrorCrnSch = this.isDnCangeLoc(inDto);
				    if(aErrorCrnSch != null && aErrorCrnSch.size() > 0){
				    	int nErrorCrnSch = aErrorCrnSch.size();
				    	String sTmp_ErrorYdCrnSchId = ""; 
				    	String sSum_ErrorYdCrnSchId = "";
				    	for( int i=0; i < nErrorCrnSch; i++){
				    		sTmp_ErrorYdCrnSchId= (String)aErrorCrnSch.get(i);
				    		if( !"".equals(sTmp_ErrorYdCrnSchId)){
				    			if(!"".equals(sSum_ErrorYdCrnSchId)){
				    				sSum_ErrorYdCrnSchId += "★" + sTmp_ErrorYdCrnSchId;
				    			}else{
				    				sSum_ErrorYdCrnSchId += sTmp_ErrorYdCrnSchId;
				    			}
				    		}
				    	}
						szLogMsg = "JSP-SESSION [권하위치 변경 (크레인작업관리 화면)] 오류검증에 걸려 종료처리";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						
						sSum_ErrorYdCrnSchId = StringUtils.remove(sSum_ErrorYdCrnSchId,"[JSP Session] 권하위치 변경 (크레인작업관리 화면) 사전검증");
				    	rtnJto.setField("STATUS", "false");
						rtnJto.setField("MESSAGE", "[USER_EXCEPTION]"+sSum_ErrorYdCrnSchId);
				    	return rtnJto;
				    }
				    
				    
				    rtnJto.setField("STATUS", "true");
				    rtnJto.setField("MESSAGE", "권하위치 변경 성공");
				    
					for(int x=0;x<inDto.length;x++)
					{
						
						// 자동화크레인의 경우 응답정보를 전달받아 처리 하기때문에 다건의 경우 Flag를 주어 제어하도록 조치함
						isAutoCrnSendYn = false;
						
						// 1. 기존 To 위치 정보 와 변경 To 위치 정보 
						recPara   	= JDTORecordFactory.getInstance().create();
						outRecSet  	= JDTORecordFactory.getInstance().createRecordSet("YD");
						
						szYdSchId =  yddatautil.setDataDefault(inDto[x].getField("YD_CRN_SCH_ID"), "");
						szUserExceptionId = "[USER_EXCEPTION]" + szYdSchId+"★";

						recPara.setField("YD_CRN_SCH_ID",szYdSchId);		
						
						szLogMsg = "[JSP Session] " + szOperationName + " 크레인스케줄 ID :["+szYdSchId+"]로 스케줄 조회" ;
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		        		
						intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, outRecSet, 0);
						
						szLogMsg = "[JSP Session] " + szOperationName + " 크레인스케줄 ID :["+szYdSchId+"]로 스케줄 조회 성공" ;
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						
						outRecSet.first();
						
						recTemp   = JDTORecordFactory.getInstance().create();			
						recTemp = outRecSet.getRecord();
						
						szOldStkPos   = yddatautil.setDataDefault(recTemp.getFieldString("YD_DN_WO_LOC"),"");					
						szOldStkLyrNo = yddatautil.setDataDefault(recTemp.getFieldString("YD_DN_WO_LAYER"),"");

						szYD_EQP_ID = yddatautil.setDataDefault(recTemp.getFieldString("YD_EQP_ID"),"");
						szYdWbookId = ydDaoUtils.paraRecChkNull(recTemp, "YD_WBOOK_ID");
						
						//현 스케줄 작업 진행상태(DB) 
						szSendYdWrkProgStat =  ydDaoUtils.paraRecChkNull(recTemp, "YD_WRK_PROG_STAT");
						
						szLogMsg = "[JSP Session] " + szOperationName + "현 스케줄 작업 진행상태(DB)[ " + szSendYdWrkProgStat  +" ]"  ;
		        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		        		
						szLogMsg = "[JSP Session] " + szOperationName + "DB 권하지시위치 [ " + szOldStkPos + "-" + szOldStkLyrNo +"]"  ;
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
							
						//통합야드의 경우는 8-3자리로 들어옴 (12자리)
						//앞에 8자리만 사용해준다 (2009.10.12 이현성)
						//권상지시위치 (야드구분을 얻어내기 위함)
						
						szYdGpTemp = yddatautil.setDataDefault(inDto[x].getField("YD_GP"), "");
						
						szLogMsg = "[JSP Session] " + szOperationName + "야드구분 [ " + szYdGpTemp +"]" ;
		        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
							
						szStkPos = yddatautil.setDataDefault(inDto[x].getField("YD_DN_WO_LOC"), "");
						szLogMsg = "[JSP Session] " + szOperationName + "입력받은 권하지시위치 [ " + szStkPos + "]"  ;
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						
						szStkLyrNo = yddatautil.setDataDefault(inDto[x].getField("YD_DN_WO_LAYER"), "");
						szStkColGp 	= szStkPos.substring(0, 6); 
						szStkBedNo 	= szStkPos.substring(6, 8);
						szEqpGp 	= szStkColGp.substring(2,4);
					 
						//-----------------------------------------------------------------------
						
						//-----------------------------------------------------------------------
						//	권하위치 변경할시에 작업지시 취소전문 발생 (데이터 변경전정보)
						//-----------------------------------------------------------------------
						szYdWrkProgStat =  ydDaoUtils.paraRecChkNull(recTemp, "YD_WRK_PROG_STAT");
						szYdSchCd       = ydDaoUtils.paraRecChkNull(recTemp, "YD_SCH_CD");
						
						// 신규 위치 적치단 정보
						szLogMsg = "[JSP Session] " + szOperationName +  "변경 권하지시단 정보 계산";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						
						recPara   	= JDTORecordFactory.getInstance().create();
						recTemp 	= outRecSet.getRecord();
						outRecSet  	= JDTORecordFactory.getInstance().createRecordSet("YD");
						
						recPara.setField("YD_STK_COL_GP", szStkColGp);
						recPara.setField("YD_STK_BED_NO", szStkBedNo);
						
						szLogMsg = "[JSP Session] " + szOperationName +  "적치단 정보조회";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 29);
						
						if (intRtnVal == 0){
							szStkLyrNo ="001";
						}
						else if ( intRtnVal > 0 )
						{
							outRecSet.last();
							recTemp 	= outRecSet.getRecord();
							szStkLyrNo 	= ydDaoUtils.stringPlusInt(recTemp.getFieldString("YD_STK_LYR_NO"),1);					
						}
						
						szLogMsg =  "[JSP Session] " + szOperationName +  "신규위치정보 :"+ szStkColGp  + "-"+ szStkBedNo +":" +szStkLyrNo;
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
						
						recPara   = JDTORecordFactory.getInstance().create();
						recPara.setField("YD_CRN_SCH_ID", yddatautil.setDataDefault(inDto[x].getField("YD_CRN_SCH_ID"), ""));
						
						outRecSet  = JDTORecordFactory.getInstance().createRecordSet("YD");
						
						szLogMsg = "[JSP Session] " + szOperationName +  "크레인 작업재료 조회";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						
						intRtnVal = ydCrnWrkMtlDao.getYdCrnwrkmtl(recPara, outRecSet, 1);
						
						szLogMsg = "[JSP Session] " + szOperationName +  "크레인 작업재료 조회 리턴값 :[ "+  intRtnVal +" ]";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						
						
						YdPlateCommDAO	commDao2 		= new YdPlateCommDAO();
						JDTORecordSet rsResult 		    = JDTORecordFactory.getInstance().createRecordSet("temp");
			    	    recInTemp = JDTORecordFactory.getInstance().create();
			    	    recInTemp.setField("YD_EQP_ID"    , szYD_EQP_ID);
			    	    //recInTemp.setField("OLD_STK_POS"    , szOldStkPos);
			    	    recInTemp.setField("YD_CRN_SCH_ID"    , szYdSchId);
			    	    recInTemp.setField("STK_POS"       , szStkPos);
						//if(1==1){
						if (commDao2.select(recInTemp, rsResult,"com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getIsChgBBedType") > 0) {
							// 레코드 추출
							rsResult.first();
							
							//sCRN_WRK_PROC_STAT = ydSlabUtils.trim(rsResult.getRecord(0).getFieldString("YD_EQP_STAT")); 
							String IS_CHG_B_BED_TYPE=rsResult.getRecord(0).getFieldString("IS_CHG_B_BED_TYPE");	
							
							if(IS_CHG_B_BED_TYPE.equals("Y")){
								rtnJto.setField("STATUS", "false");
								rtnJto.setField("MESSAGE", "1후판 B동은, 초장적 제품이 포함된 작업을 장척베드에 권하할 수 없습니다.");
								return  rtnJto;
							}
						}
						//}
						
						// 2021.01.06 전사물류개선 자동화크레인 Y9시스템관련  Start 
						isSendToEaiY9 = PlateGdsYdUtil.isSendToEaiY9_ydEqpId(szYD_EQP_ID);
						if(isSendToEaiY9){
							// Y9YDL015에서 던져주는 값
							String szCallPgm =  yddatautil.setDataDefault(inDto[x].getField("CALL_PGM"), "");
							if(!"Y9YL015_AUTO".equals(szCallPgm)){

								JDTORecordSet rsEqpInfo = JDTORecordFactory.getInstance().createRecordSet("temp");
								if( YdCommonUtils.getYdEqp(szYD_EQP_ID, rsEqpInfo) > 0 ) {
									String szydEqpStat		= rsEqpInfo.getRecord(0).getFieldString("YD_EQP_STAT");   // 설비 상태
									String szEqpAutoCrnMode= rsEqpInfo.getRecord(0).getFieldString("YD_EQP_AUTO_CRN_MODE"); // AutoCrn 상태
									String szEqpAutoCrnYN 	= rsEqpInfo.getRecord(0).getFieldString("YD_EQP_WRK_MODE2");   // AutoCrn 여부	
									
									//   Auto크레인시에 일시정지(4) 상태만 가능 ( 대기) 일경우 응답결과를 받고 처리하는걸로 수정조치함
//									if (("A".equals(szEqpAutoCrnYN) || "R".equals(szEqpAutoCrnYN))) {
									if ("A".equals(szEqpAutoCrnYN)) {
//										if (!"4".equals(szEqpAutoCrnMode) 
//												&& !("W".equals(szSendYdWrkProgStat)||"S".equals(szSendYdWrkProgStat)||"1".equals(szSendYdWrkProgStat)) 
//												&& !"B".equals(szydEqpStat) ) {
										if ( !("4".equals(szEqpAutoCrnMode) || "5".equals(szEqpAutoCrnMode)) && !"W".equals(szSendYdWrkProgStat)) {
											szLogMsg = "[JSP Session] " + szOperationName + "L2에 지시가 내려간 무인크레인 [" + szYD_EQP_ID + "]이 일시정지(4)이거나, 비상정지(5) 상태가 아니면 변경 할 수 없습니다.";
											ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
											rtnJto.setField("STATUS", "false");
											rtnJto.setField("MESSAGE", "L2에 지시가 내려간 자동화 크레인[" + szYD_EQP_ID + "] 일시정지(4)이거나, 비상정지(5) 상태가 아니면 변경 할 수 없습니다.");
											return  rtnJto;
										}
										
										// 지시가 내려간 Case
										if(!"W".equals(szSendYdWrkProgStat)){
							    	    	szLogMsg = "[JSP Session] " + szOperationName + "무인크레인 [" + szYD_EQP_ID +"]  YD_DN_WO_LOC_TO["+ szStkColGp +"-"+ szStkBedNo +"]";
							    	    	ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
							    	    	
							    	    	YdPlateCommDAO	commDao 		= new YdPlateCommDAO();
							    	    	recInTemp = JDTORecordFactory.getInstance().create();
							    	    	recInTemp.setField("YD_DN_WO_LOC_TO"    , szStkColGp + szStkBedNo );
							    	    	recInTemp.setField("STL_NO_TEMP"        , "");
							    	    	recInTemp.setField("STK_LYR_NO_TEMP"    , szStkLyrNo);
							    	    	recInTemp.setField("YD_L2_REQUEST_STAT" , "5");
							    	    	recInTemp.setField("YD_CRN_SCH_ID"      , szYdSchId);
							    	    	
							    	    	intRtnVal = commDao.update(recInTemp,"com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.upYdCrnSchLocStat1");
							    	    	
							    	    	if(intRtnVal>0){
								    	    	JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
								    	    	jrYdMsg.setField("JMS_TC_CD"       , "YDYDJ642");	//크레인작업지시요구
								    	    	jrYdMsg.setField("YD_EQP_ID"       , szYD_EQP_ID       );	//야드설비ID
								    	    	jrYdMsg.setField("YD_WRK_PROG_STAT", "5");	//야드작업진행상태(권하위치변경 요구상태)	    					
								    	    	jrYdMsg.setField("YD_SCH_CD"       , szYdSchCd      );	//야드스케쥴코드
								    	    	jrYdMsg.setField("YD_CRN_SCH_ID"   , szYdSchId   );	//야드크레인스케쥴ID
								    	    	
								    	    	EJBConnector ydEjbCon = new EJBConnector("default", "CraneLdHdSeEJB", this); 
								    	    	ydEjbCon.trx("procY4CrnWrkOrdReq",   new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
								    	    	
								    	    	isAutoCrnSendYn = true;
							    	    	}
										}
									}
								}
							}
							else{
								// L2관련 초기화한다.
				    	    	YdPlateCommDAO	commDao 		= new YdPlateCommDAO();
				    	    	recInTemp = JDTORecordFactory.getInstance().create();
				    	    	recInTemp.setField("YD_DN_WO_LOC_TO"    , "");
				    	    	recInTemp.setField("STL_NO_TEMP"        , "");
				    	    	recInTemp.setField("STK_LYR_NO_TEMP"    , "");
				    	    	recInTemp.setField("YD_L2_REQUEST_STAT" , "");
				    	    	recInTemp.setField("YD_CRN_SCH_ID"      , szYdSchId);
				    	    	intRtnVal = commDao.update(recInTemp,"com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.upYdCrnSchLocStat1");
							}
						}
						 
						// 자동화크레인의 Flag가 Ture일 경우 다음 대상을 변경처리한다.
						if(!isAutoCrnSendYn){
							//SJH05001				
							//기존 산적위치 정보가 올바르지 않거나 없을 경우는 기존산적위치를 삭제할 수없으므로 건너띈다.
							if( szOldStkPos.length() == 8 && (!szOldStkPos.equals("XX010101")) && (!szOldStkPos.equals("XXYY0101")))
							{	
								szOldStkColGp = szOldStkPos.substring(0, 6); 
								szOldStkBedNo = szOldStkPos.substring(6, 8);
								
								szLogMsg = "[JSP Session] " + szOperationName +  "기존위치정보 :"+ szOldStkColGp  + "-"+ szOldStkBedNo +":" +szOldStkLyrNo;				
								ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			            		
								//실제로는 크레인작업재료의 개수만 필요함				
								outRecSet.first();
								for (int nLoop =0 ; nLoop<outRecSet.size();nLoop++ ){
									
									recTemp =JDTORecordFactory.getInstance().create();
									recTemp = outRecSet.getRecord(nLoop);					
									
									// 기존 지시위치 에 쌓여 있는 정보 Clear
									recSet = JDTORecordFactory.getInstance().create();
					                recSet.setField("YD_STK_COL_GP",       szOldStkColGp);    
					                recSet.setField("YD_STK_BED_NO",       szOldStkBedNo); 
					                /*
					                 * 2011.03.02 슬라브 권상모음 스케쥴 삭제시 문제발생 
					                 * 아래 파라미터 막음.
					                 */
					                //recSet.setField("YD_STK_LYR_MTL_STAT", "D");
					                recSet.setField("STL_NO",              yddatautil.setDataDefault(recTemp.getField("STL_NO"),""));
					                
					                szLogMsg = "[JSP Session] " + szOperationName +   "기존 지시위치 에 쌓여 있는 정보 Clear ";
				            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				            		
					                intRtnVal = ydStkLyrDao.updYdStklyrWithColStockStat(recSet);
					                
					                szLogMsg = "[JSP Session] " + szOperationName +   "기존 지시위치 에 쌓여 있는 정보 Clear 성공 [ " + intRtnVal + " ] ";
					            	ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					            	
					                //차상위치에서 변경 시 목표동 셋팅 초기화 작업
					                if(szOldStkColGp.substring(2, 4).equals("PT") && 
					                		(szOldStkColGp.substring(0,1).equals("A") ||
					                		 szOldStkColGp.substring(0,1).equals("M"))){  //항만슬라브야드 기능추가 - 2015.12.30 LeeJY
					                	
					                	recSet.setField("YD_STKBED_USG_CD",       ""); 
					                	intRtnVal = ydStkColDao.updYdStkcol(recSet,0);	
					                	
					                	szLogMsg = "[JSP Session] " + szOperationName +   "기존 지시위치 에 차상위치에서 변경 시 목표동 셋팅 초기화 작업  성공 [ " + intRtnVal + " ] ";
						            	ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					                }
					            }		
								/*
								 * 2016.07.20 윤재광
								 * 이호현 주임 요청사항으로 아래기능 막음
								 */
								/*
								szLogMsg="권하위치 변경으로 스케줄의 기존 TO위치베드를 완산베드에서 해제 시작";
								ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
								
								recInTemp = JDTORecordFactory.getInstance().create();
					        	recInTemp.setField("YD_STK_COL_GP", 			szOldStkPos.substring(0, 6));
					        	recInTemp.setField("YD_STK_BED_NO", 			szOldStkPos.substring(6, 8));
					        	recInTemp.setField("YD_STK_BED_WHIO_STAT", 		"E");
					        	recInTemp.setField("MODIFIER", 					"CHGLOC");
					        	szLogMsg  = DaoManager.updYdStkbed(recInTemp, 0);
					        	*/
							}else{
								szLogMsg = "[JSP Session] " + szOperationName +  "기존 산적위치 정보가 올바르지 않거나 없을 경우는 기존산적위치를 삭제할 수없으므로 건너띈다";				
								ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
							} 
							
							outRecSet.first();
							
							//작업재료는 밑에것부터 부터 위로 올라와있는 상태이므로 레코드셋을 역순으로 정렬한다
							outRecSet.reverseOrder();
							
							for (int nLoop =0 ; nLoop<outRecSet.size(); nLoop++ ){
								
								// 신규위치에 정보를 Setting
								recSet = JDTORecordFactory.getInstance().create();
								recTemp =JDTORecordFactory.getInstance().create();
								
								recTemp = outRecSet.getRecord(nLoop);					
								recSet.setField("YD_STK_COL_GP",       szStkColGp);    
					            recSet.setField("YD_STK_BED_NO",       szStkBedNo);   
					            recSet.setField("YD_STK_LYR_NO",       ydDaoUtils.stringPlusInt(szStkLyrNo, nLoop)) ;
					            recSet.setField("YD_STK_LYR_MTL_STAT", "D");
				                recSet.setField("STL_NO",              recTemp.getField("STL_NO"));
				                
				            	szLogMsg = "[JSP Session] " + szOperationName +   "신규위치에 정보를 UPDATE ";
				            	ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				            	
				                intRtnVal = ydStkLyrDao.updYdStklyr(recSet, 0);
				                
				            	if (intRtnVal < 1)
								{
									//신규위치에 정보를 Setting 실패
				            		szLogMsg = "[JSP Session] " + szOperationName + "신규위치에 정보를 Setting 실패 [ " + intRtnVal + " ]"  ;
				            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				            		throw new Exception(szUserExceptionId+szLogMsg);
								}
				            	
				        		//신규위치에 정보를 Setting 실패
			            		szLogMsg = "[JSP Session] " + szOperationName + "신규위치에 정보를 Setting 성공 [ " + intRtnVal + " ]"  ;
			            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				            	
							}
					
							// 권하위치 정보 스케줄 정보에서 변경
							recPara   = JDTORecordFactory.getInstance().create();
							recPara.setField("YD_CRN_SCH_ID", yddatautil.setDataDefault(inDto[x].getField("YD_CRN_SCH_ID"), ""));
							recPara.setField("YD_DN_WO_LOC", szStkColGp+szStkBedNo);				
							recPara.setField("YD_DN_WO_LAYER", szStkLyrNo);
							
							szLogMsg = "[JSP Session] " + szOperationName + " 권하위치 정보 스케줄 정보에서 변경(UPDATE)"  ;
			        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
							
							intRtnVal = ydCrnSchDao.updYdCrnsch(recPara, 0);
							
							if (intRtnVal < 1)
							{	
								szLogMsg = "[JSP Session] " + szOperationName +  "권하위치 스케줄 정보 변경 실패 [" +intRtnVal  + " ] " ;
			            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			            		throw new Exception(szUserExceptionId+szLogMsg);
							}
							
							szLogMsg = "[JSP Session] " + szOperationName + " 권하위치 정보 스케줄 정보 UPDATE 성공 "  ;
			        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
							
			        		/*
			        		 * 2010.09.08 윤재광 
			        		 * 권하위치 변경시 권하분리(X)인 스케쥴의 권상위치 정보도 변경한다.
			        		 * 이유 : 메인스케쥴 권하위치 변경 후 이후 권하분리 작업취소시 재료정보가 야드에 중복발생.
			        		 */
			        		{
			        			szLogMsg = "[JSP Session] " + szOperationName + " 권하분리(X) 권상위치 정보 스케줄 정보에서 변경(UPDATE)"  ;
			            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			    				
			    				intRtnVal = ydCrnSchDao.updYdCrnXSchFromLoc(recPara);
			    				
			    				szLogMsg = "[JSP Session] " + szOperationName + " 권하분리(X) 권상위치 정보 스케줄 정보에서 변경(UPDATE) 성공 [" +intRtnVal  + " ] " ;
			            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			        		}
			        		
							// 스케줄 변경 후 제원 위치정보를 맞춰준다.
							szLogMsg =  "[JSP Session] " + szOperationName +  "스케줄 변경 후 제원 위치정보 세팅 호출";
			        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			        		
			        		boolean lb_updYdCrnBed = false;        		
			        		lb_updYdCrnBed = YdUtils.updYdCrnschBedData(recPara);
			        		
			        		if(!lb_updYdCrnBed){
			        			szLogMsg =  "[JSP Session] " + szOperationName +  "스케줄 변경 후 제원 위치정보 세팅 실패";
			            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			        			
			        		}
					
							szLogMsg =  "[JSP Session] " + szOperationName +  "스케줄 변경 후 제원 위치정보 세팅 완료";
			        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
							
							//권하위치 변경후 작업지시정보를  재전송 (Manual 작업지시보내는 방법으로  선택 인경우만 전송한다)
							if( szSendYdWrkProgStat.equals(YdConstant.YD_EQP_STAT_UP_WO) || 
								szSendYdWrkProgStat.equals(YdConstant.YD_EQP_STAT_UP_CMPL)){
								
								szLogMsg =  "[JSP Session] " + szOperationName +  "권하위치 변경후 작업지시정보를  재전송";
				        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				        		
								if(szYdSchCd.length() > 0 ){
									
									szYdGp = szYdSchCd.substring(0,1);
								}else{
									szLogMsg = "[JSP Session] " + szOperationName + " 스케줄코드의 야드구분이 올바르지 않습니다";
									ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
								}
								
								szLogMsg =  "[JSP Session] " + szOperationName + "   - 야드구분[" + szYdGp + "]";
								ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
								
								szLogMsg =  "[JSP Session] " + szOperationName + "   - 스케줄 진행상태 ["  + szSendYdWrkProgStat +" ]";
								ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
								
								szJMS_TC_CD = "";
								
								if (YdConstant.YD_GP_C_SLAB_YARD.equals(szYdGp)  ){						//C연주 슬라브 야드 [A]
									szJMS_TC_CD = "YDYDJ640";
									szEjbMethod = "procY1CrnWrkOrdReq";
								}else if(YdConstant.YD_GP_A_PLATE_SLAB_YARD.equals(szYdGp)){			//A후판 슬라브야드[D]
									
									szJMS_TC_CD = "YDYDJ641";
									szEjbMethod = "procY3CrnWrkOrdReq";
								}else if(YdConstant.YD_GP_PLATE_GDS_YARD.equals(szYdGp) 
										|| YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYdGp) ){			//후판제품야드 [K],2후판제품야드 [T] - 2012.12.28 수정 (3기)
									
									szJMS_TC_CD = "YDYDJ642";
									szEjbMethod = "procY4CrnWrkOrdReq";
								}else if(YdConstant.YD_GP_C_HR_COIL_MATL_YARD.equals(szYdGp)){			//C열연 코일야드[H]
								
									szJMS_TC_CD = "YDYDJ643";
									szEjbMethod = "procY5CrnWrkOrdReq";
								}else if(YdConstant.YD_GP_C_HR_COIL_GDS_YARD.equals(szYdGp)){			//C열연 제품야드[J]
									
									szJMS_TC_CD = "YDYDJ643";
									szEjbMethod = "procY5CrnWrkOrdReq";
								}else if(YdConstant.YD_GP_INTGR_YARD.equals(szYdGp)){					//통합제품야드[S]
									
									szJMS_TC_CD = "YDYDJ644";
									szEjbMethod = "procY0CrnWrkOrdReq";
								} 
								
								szYD_EQP_ID = inDto[x].getFieldString("YD_EQP_ID");
								
								szLogMsg = "[JSP Session] " + szOperationName +  "- 야드구분[" + szYdGp + "], 크레인 설비[" + szYD_EQP_ID + "]";
								ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			//SJH03004					
								recPara.setField("JMS_TC_CD", 			szJMS_TC_CD);
								
								recPara.setField("YD_EQP_ID" , 			szYD_EQP_ID);
								recPara.setField("YD_WRK_PROG_STAT" , 	szSendYdWrkProgStat);
								
								if (!szJMS_TC_CD.equals("")){
									//EJB Method Call
									if( szEjbMethod.equals("procY5CrnWrkOrdReq") ) {
										ejbConn = new EJBConnector("default", "CoilCraneLdHdSeEJB", this);			
									} else{
										ejbConn = new EJBConnector("default", "CraneLdHdSeEJB", this);	
									}
									szRtnMsg = (String)ejbConn.trx(szEjbMethod, new Class[] { JDTORecord.class }, new Object[] { recPara });	
	
	//								ydDelegate.sendMsg(recPara);
									
									
								} else{
									szLogMsg = "[JSP Session] " + szOperationName  + " 작업재지시 전문을 전송하지 않습니다(작업TC ID 없음)";
									ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
								}
								
								szLogMsg = "[JSP Session] " + szOperationName + " 작업재지시  - 리턴메세지 : [" + szRtnMsg +"] ";
								ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
							}
			        		
			        		//------------------------------------------------------------------------
			        		// 본래 권하위치가 차량 또는 대차에서 일반야드로 권하위치를 변경하는경우
			        		//------------------------------------------------------------------------
			        		// szOldStkPos 기존 권하위치 
			        		if(szOldStkPos.length() >= 6){
			        			szEqpGpBefo = szOldStkPos.substring(2,4);
			        		}
			        		
			        		// 기존 설비구분이 차량/대차이고 , 신규 설비 구분이 그 차량/대차 작업이 아닌경우  
			        		// 작업예약 ID를 Clear  한다.
			        		if(szEqpGpBefo.equals(YdConstant.YD_EQP_GP_TCAR)||
			        		   szEqpGpBefo.equals(YdConstant.YD_EQP_GP_PALLET)||
			        		   szEqpGpBefo.equals(YdConstant.YD_EQP_GP_TRAILER)){
			        			
			        			if(!szEqpGpBefo.equals(szEqpGp)){
			        				
			        				//szYdWbookId - 현 스케줄의 작업예약 ID
			        				//delWBookBefoCarOrTCar
			        				recPara   = JDTORecordFactory.getInstance().create();
			        				recPara.setField("YD_WBOOK_ID", szYdWbookId);
			        				recPara.setField("YD_EQP_GP", szEqpGpBefo);
			        				yddatautil.delWBookBefoCarOrTCar(recPara);
			        			}
			        		}
						}		
					}
				}catch(DAOException e) {
					throw e;
				}catch(Exception e){
					throw new DAOException(e.getMessage());			
				}finally { }
				
				
				szLogMsg = "[JSP Session] " + szOperationName  + " 끝";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				return rtnJto;
			}	// end of updToPosFix
			
			/**
			 * 권하위치변경전 Check
			 * @param inDto
			 * @return
			 */
			private List isDnCangeLoc(JDTORecord[] inDto){
				

				YdStkLyrDao ydStkLyrDao        = new YdStkLyrDao();
				YdCrnSchDao ydCrnSchDao        = new YdCrnSchDao();
				YdCrnWrkMtlDao  ydCrnWrkMtlDao = new YdCrnWrkMtlDao();
				
				int intRtnVal = 0;
				ArrayList rtnList_Error_CrnSchID = null;
				
				
				String szLogMsg = "";
				String szOperationName = "권하위치 변경 (크레인작업관리 화면) 사전검증";
				String szMethodName = "YdJspCommonSeEJBBean.isDnCangeLoc";
				String szSendYdWrkProgStat 	="";
			
			    String szYdSchId 			="";
			    
			    String szYdGpTemp 			="";
			    String szRtnMsg1			= "";
				String szOldStkPos     = "";
				String szOldStkLyrNo   = "";
				String szStkPos = "";
				String szStkColGp ="";
				String szStkBedNo = "";
				String szStkLyrNo = "";
				
				String forceDownYn = "";
				
				JDTORecord    recPara  = null;
				JDTORecord    recInPara = null;
				JDTORecord    recTemp  = null;
				JDTORecordSet    outRecSet  = null;
				JDTORecordSet    outRecSetTmp = null;
				
				try{
					
					szLogMsg = "[JSP Session] " + szOperationName  ;
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					rtnList_Error_CrnSchID = new ArrayList();
					for(int x=0;x<inDto.length;x++)
					{
						
						
						// 1. 기존 To 위치 정보 와 변경 To 위치 정보 
						recPara   	= JDTORecordFactory.getInstance().create();
						outRecSet  	= JDTORecordFactory.getInstance().createRecordSet("YD");
						
						szYdSchId =  yddatautil.setDataDefault(inDto[x].getField("YD_CRN_SCH_ID"), "");
						
						forceDownYn = yddatautil.setDataDefault(inDto[x].getField("FORCE_DOWN_YN"), "");
						
						recPara.setField("YD_CRN_SCH_ID",szYdSchId);		
						
						szLogMsg = "[JSP Session] " + szOperationName + " 크레인스케줄 ID :["+szYdSchId+"]로 스케줄 조회" ;
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		        		
						intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, outRecSet, 0);
						
						if(intRtnVal < 0 )
						{
							szLogMsg = "[JSP Session] " + szOperationName + "해당 크레인스케줄 ID  정보: ["+ szYdSchId +" ]조회시 ERROR발생";            		
		            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
		            		rtnList_Error_CrnSchID.add(szYdSchId + "■" + szLogMsg);
		            		
						}else if(intRtnVal == 0 ){
							szLogMsg = "[JSP Session] " + szOperationName + "해당 스케줄크레인스케줄 ID  정보: ["+ szYdSchId + "] 가 존재하지않습니다";            		
		            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
		            		rtnList_Error_CrnSchID.add(szYdSchId + "■" + szLogMsg);
		            		
						}
						
						szLogMsg = "[JSP Session] " + szOperationName + " 크레인스케줄 ID :["+szYdSchId+"]로 스케줄 조회 성공" ;
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						
						outRecSet.first();
						
						recTemp   = JDTORecordFactory.getInstance().create();			
						recTemp = outRecSet.getRecord();
						
						szOldStkPos   = yddatautil.setDataDefault(recTemp.getFieldString("YD_DN_WO_LOC"),"");					
						szOldStkLyrNo = yddatautil.setDataDefault(recTemp.getFieldString("YD_DN_WO_LAYER"),"");
	
						
						//현 스케줄 작업 진행상태(DB) 
						szSendYdWrkProgStat =  ydDaoUtils.paraRecChkNull(recTemp, "YD_WRK_PROG_STAT");
						
						szLogMsg = "[JSP Session] " + szOperationName + "현 스케줄 작업 진행상태(DB)[ " + szSendYdWrkProgStat  +" ]"  ;
		        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		        		
						szLogMsg = "[JSP Session] " + szOperationName + "DB 권하지시위치 [ " + szOldStkPos + "-" + szOldStkLyrNo +"]"  ;
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
							
						//통합야드의 경우는 8-3자리로 들어옴 (12자리)
						//앞에 8자리만 사용해준다 (2009.10.12 이현성)
						//권상지시위치 (야드구분을 얻어내기 위함)
						
						szYdGpTemp = yddatautil.setDataDefault(inDto[x].getField("YD_GP"), "");
						
						szLogMsg = "[JSP Session] " + szOperationName + "야드구분 [ " + szYdGpTemp +"]" ;
		        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		        		
							
						szStkPos = yddatautil.setDataDefault(inDto[x].getField("YD_DN_WO_LOC"), "");
						szLogMsg = "[JSP Session] " + szOperationName + "입력받은 권하지시위치 [ " + szStkPos + "]"  ;
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						
						if ("".equals(szStkPos)){		
							szLogMsg = "[JSP Session] " + szOperationName + "변경 권하지시위치 정보가 없습니다."  ;
		            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
		            		rtnList_Error_CrnSchID.add(szYdSchId + "■" + szLogMsg);
						}
						
						if(szOldStkPos.equals(szStkPos)){
							szLogMsg = "[JSP Session] " + szOperationName + "입력된 권하위치가 기존 권하위치와 같습니다."  ;
		            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
		            		rtnList_Error_CrnSchID.add(szYdSchId + "■" + szLogMsg);
						}
						
						szStkLyrNo = yddatautil.setDataDefault(inDto[x].getField("YD_DN_WO_LAYER"), "");
						if ("".equals(szStkLyrNo)){
							szLogMsg = "[JSP Session] " + szOperationName +  "변경 권하지시단 정보가 없으나 재계산하여줍니다.";
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						}
						
						if(szStkPos.length() >=8)
						{
							szStkColGp 	= szStkPos.substring(0, 6); 
							szStkBedNo 	= szStkPos.substring(6, 8);
						}else{
							szLogMsg = "[JSP Session] " + szOperationName + "변경 권하지시위치 정보가 맞지 않습니다"  ;
		            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
		            		rtnList_Error_CrnSchID.add(szYdSchId + "■" + szLogMsg);
						}
						
						
						String sIsAvailabe = "0";
						outRecSetTmp= JDTORecordFactory.getInstance().createRecordSet("YD");
						recInPara	= JDTORecordFactory.getInstance().create();
						recInPara.setField("YD_CRN_SCH_ID", szYdSchId);
						recInPara.setField("YD_STK_COL_GP", szStkColGp);
						recInPara.setField("YD_STK_BED_NO", szStkBedNo);
						
						intRtnVal = ydCrnSchDao.getYdCrnsch(recInPara, outRecSetTmp, 509);
						
						if(intRtnVal > 0){
							outRecSetTmp.first(); 
							recInPara 	= outRecSetTmp.getRecord();
							sIsAvailabe = yddatautil.setDataDefault(recInPara.getFieldString("IS_AVAILABLE"),"");
						}
						
						if( "1".equals(sIsAvailabe) ) {
							szLogMsg = "현대3사 제품위에 타고객사 제품을 적치할 수 없습니다."; 
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
							rtnList_Error_CrnSchID.add(szYdSchId + "■" + szLogMsg);
						}else if( "2".equals(sIsAvailabe) ) {
							szLogMsg = "현대3사 제품을 타고객사 제품위에 적치할 수 없습니다.";
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
							rtnList_Error_CrnSchID.add(szYdSchId + "■" + szLogMsg); 
						}
						
						
						//-----------------------------------------------------------------------
						//	권하지시위치 변경 시 베드의 TO위치 정합성 판단.(추가: 2010.02.10 이현성)
						//-----------------------------------------------------------------------
						YdStkLocVO	ydStkLocVO	= new YdStkLocVO();
						recInPara	= JDTORecordFactory.getInstance().create();
						
						/* 파라미터정의:	1) YD_STK_COL_GP	- 적치열
						 * 				2) YD_STK_BED_NO	- 적치베드
						 * 				3) YD_EQP_WRK_SH	- 작업총매수
						 * 				4) YD_EQP_WRK_WT	- 작업총중량
						 * 				5) YD_EQP_WRK_T		- 작업총두께
						 * 				6) YD_SCH_CD		- 스케줄코드
						 */
						recInPara.setField("YD_STK_COL_GP", szStkColGp);
						recInPara.setField("YD_STK_BED_NO", szStkBedNo);
						recInPara.setField("YD_EQP_WRK_SH", ydDaoUtils.paraRecChkNull(recTemp, "YD_EQP_WRK_SH"));
						recInPara.setField("YD_EQP_WRK_WT", ydDaoUtils.paraRecChkNull(recTemp, "YD_EQP_WRK_WT"));
						recInPara.setField("YD_EQP_WRK_T" , ydDaoUtils.paraRecChkNull(recTemp, "YD_EQP_WRK_T"));
						recInPara.setField("YD_SCH_CD"	  , ydDaoUtils.paraRecChkNull(recTemp, "YD_SCH_CD"));
						recInPara.setField("FORCE_DOWN_YN",forceDownYn);
						
						 
						szRtnMsg1 = YdToLocDcsnUtil.procBedStackable(recInPara, ydStkLocVO, szMethodName);
						
						
						szLogMsg = "[JSP Session] " + szOperationName +" 권하위치 Chekc Returen Value "+szRtnMsg1 ;
		        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		        		
		        		int intERR_CD = 0;
		        		StringBuffer szSTATUS		= new StringBuffer();
		        		
						
						if( !szRtnMsg1.equals(YdConstant.RETN_CD_SUCCESS) ) {
							if( szRtnMsg1.equals(YdConstant.RETN_CD_NOTEXIST) ) {
								
								intERR_CD = ydStkLocVO.getYdBedErrCd();
								
								if( intERR_CD >= YdConstant.YD_BED_ERR_CD_H_OVER ) {
									//해당하는 적치베드에 적치가능높이 OVER
									intERR_CD	-= YdConstant.YD_BED_ERR_CD_H_OVER;
									
									szSTATUS.append("적치가능높이 OVER");
								}
								
								if( intERR_CD >= YdConstant.YD_BED_ERR_CD_WT_OVER ) {
									//해당하는 적치베드에 적치가능중량 OVER
									intERR_CD	-= YdConstant.YD_BED_ERR_CD_WT_OVER;
									
									if( szSTATUS.length() > 0 ) szSTATUS.append(", ");
									
									szSTATUS.append("적치가능중량 OVER");
								}
								
								if( intERR_CD == YdConstant.YD_BED_ERR_CD_SH_OVER ) {
									//해당하는 적치베드에 적치가능매수 OVER
									
									if( szSTATUS.length() > 0 ) szSTATUS.append(", ");
									
									szSTATUS.append("적치가능매수 OVER");
								}
								
								szLogMsg = "해당크레인스케줄["+szYdSchId+"]의 권하지시적치열["+szStkColGp+"], 권하지시베드["+szStkBedNo+"]에 적치불가능합니다 - " + szSTATUS.toString();
								
							}else{
								if(szRtnMsg1.equals(Integer.toString(YdConstant.YD_BED_ERR_CD_H_OVER)) ) {
									//해당하는 적치베드에 적치가능높이 OVER
									intERR_CD	-= YdConstant.YD_BED_ERR_CD_H_OVER;
									
									szSTATUS.append("적치가능높이 OVER");
									
									szLogMsg = "해당크레인스케줄["+szYdSchId+"]의 권하지시적치열["+szStkColGp+"], 권하지시베드["+szStkBedNo+"]에 적치불가능합니다 - " + szSTATUS.toString();
									
									szLogMsg = "[JSP Session] " + szOperationName +"높이제한으로 적치 불가" ;
					        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
								}
							}
							rtnList_Error_CrnSchID.add(szYdSchId + "■" + szLogMsg); 
						}
						
						if( ydStkLocVO.getYdBedErrCd() != YdConstant.YD_BED_STACKABLE) {
							rtnList_Error_CrnSchID.add(szYdSchId + "■" + "적치가능유무확인중 확인중 오류(코드:" +ydStkLocVO.getYdBedErrCd()+") 발생[적치가능매수(1) or 적치가능중량(3) or 적치가능높이(5)] "); 
						}
						//-----------------------------------------------------------------------
						
						//-----------------------------------------------------------------------
						//	권하위치 변경할시에 작업지시 취소전문 발생 (데이터 변경전정보)
						//-----------------------------------------------------------------------
						
						// 신규 위치 적치단 정보
						szLogMsg = "[JSP Session] " + szOperationName +  "변경 권하지시단 정보 계산";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						
						recPara   	= JDTORecordFactory.getInstance().create();
						recTemp 	= outRecSet.getRecord();
						outRecSet  	= JDTORecordFactory.getInstance().createRecordSet("YD");
						
						recPara.setField("YD_STK_COL_GP", szStkColGp);
						recPara.setField("YD_STK_BED_NO", szStkBedNo);
						
						szLogMsg = "[JSP Session] " + szOperationName +  "적치단 정보조회";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 29);
						
						if (intRtnVal == 0){
							szStkLyrNo ="001";
						}
						else if ( intRtnVal > 0 )
						{
							outRecSet.last();
							recTemp 	= outRecSet.getRecord();
							szStkLyrNo 	= ydDaoUtils.stringPlusInt(recTemp.getFieldString("YD_STK_LYR_NO"),1);					
						}
						
						szLogMsg =  "[JSP Session] " + szOperationName +  "신규위치정보 :"+ szStkColGp  + "-"+ szStkBedNo +":" +szStkLyrNo;
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
						
						recPara   = JDTORecordFactory.getInstance().create();
						recPara.setField("YD_CRN_SCH_ID", yddatautil.setDataDefault(inDto[x].getField("YD_CRN_SCH_ID"), ""));
						
						outRecSet  = JDTORecordFactory.getInstance().createRecordSet("YD");
						
						szLogMsg = "[JSP Session] " + szOperationName +  "크레인 작업재료 조회";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						
						intRtnVal = ydCrnWrkMtlDao.getYdCrnwrkmtl(recPara, outRecSet, 1);
						
						szLogMsg = "[JSP Session] " + szOperationName +  "크레인 작업재료 조회 리턴값 :[ "+  intRtnVal +" ]";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						
						if (intRtnVal == 0)
						{
							szLogMsg =  "[JSP Session] " + szOperationName + "해당 스케줄에 해당되는 크레인 작업 재료가 없습니다.";
		            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
		            		rtnList_Error_CrnSchID.add(szYdSchId + "■" + szLogMsg);
						} else if (intRtnVal < 0){
							
							szLogMsg = "[JSP Session] " + szOperationName + "해당 스케줄에 해당되는 재료 조회시 ERROR";
		            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
		            		rtnList_Error_CrnSchID.add(szYdSchId + "■" + szLogMsg);
						}
					}
					
				}catch(Exception e){
					
				}
				
				return rtnList_Error_CrnSchID;
			}
			/**
			 * 권하위치 변경 (크레인 상태관리화면)
			 * - 전사물류개선 자동화모듈 관련 분리처리함
			 * 
			 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
			 * @param inDto
			 * @return String
			 * @throws DAOException
			 */
			public String updCrnDnPrsFix4G(JDTORecord[] inDto) throws DAOException {
				
				int intRtnVal = 0;				
				String szMsg= null;
				String szMethodName = "updCrnDnPrsFix4G";		
				String szOperationName = "권하위치 변경";
				JDTORecord    recPara = null;
				JDTORecord    recTemp = null;
				JDTORecord    recSet = null;
				JDTORecord recStkLyr = null;
				
				String szStkPos   = null;
				String szStkColGp = null;
				String szStkBedNo = null;
				String szStkLyrNo = null;
				
				String szOldStkPos   = null;
				String szOldStkColGp = null;
				String szOldStkBedNo = null;
				String szOldStkLyrNo = null;
				
					
				YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
				YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
				YdCrnWrkMtlDao  ydCrnWrkMtlDao = new YdCrnWrkMtlDao();
				
				JDTORecordSet    outRecSet  = null;
				JDTORecordSet    rsStkLyr  = null;
				
				String szJMS_TC_CD = null;
				String szEjbMethod = null;
				String szLogMsg = null;
				String szRtnMsg = null;
				String szYD_EQP_ID = null;
				EJBConnector ejbConn = null;
				

			    // 전사물류개선 2021.1.6 L9시스템 여부
			    boolean isSendToEaiY9 = false;
				try {
					
					szLogMsg = "JSP-SESSION [ 권하위치 변경 (크레인 상태관리화면)] 시작";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
					
					
						// 1. 기존 To 위치 정보 와 변경 To 위치 정보 
						recPara   = JDTORecordFactory.getInstance().create();
						outRecSet  = JDTORecordFactory.getInstance().createRecordSet("YD");
						
						recPara.setField("YD_CRN_SCH_ID", yddatautil.setDataDefault(inDto[0].getField("YD_CRN_SCH_ID"), ""));				
						intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, outRecSet, 0);
						
						if(intRtnVal <1 )
						{
							szMsg ="[JSP Session]권하위치 변경 - 해당 스케줄 정보: "+inDto[0].getField("YD_CRN_SCH_ID")+"가 존재하지않습니다";
		            		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							return YdConstant.RETN_CD_NOTEXIST;
						}
						outRecSet.first();
						
						recTemp   = JDTORecordFactory.getInstance().create();				

						//1건 조회됨
						do{				
							recTemp = outRecSet.getRecord();
							
							szOldStkPos   = yddatautil.setDataDefault(recTemp.getFieldString("YD_DN_WO_LOC"),"");					
							szOldStkLyrNo = yddatautil.setDataDefault(recTemp.getFieldString("YD_DN_WO_LAYER"),"");
							
						}while(outRecSet.next());
										
						szStkPos = yddatautil.setDataDefault(inDto[0].getField("YD_DN_WO_LOC"), "");
						
						if ("".equals(szStkPos)){					
							//권하지시위치 정보가 없습니다.
							szMsg ="[JSP Session]권하위치 변경 - 변경 권하지시위치 정보가 없습니다.";
		            		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		            		return YdConstant.RETN_CD_FAILURE;
						}
						
						
						szStkLyrNo = yddatautil.setDataDefault(inDto[0].getField("YD_DN_WO_LAYER"), "");
						
						// 2021. 11. 30
						// 후판제품엔 단정보 체크하지 않는다.
						if ( !szOldStkPos.startsWith(YdConstant.YD_GP_PLATE2_GDS_YARD)){
							if("".equals(szStkLyrNo)){
								//권하지시단 정보가 없습니다.
								szMsg ="[JSP Session]권하위치 변경 - 변경 권하지시단 정보가 없습니다.";
			            		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							}
							//return;
						}
						
						if(szStkPos.length() ==8)
						{
							szStkColGp = szStkPos.substring(0, 6); 
							szStkBedNo = szStkPos.substring(6, 8);
						}else{
							//변경 권하지시위치 정보의 길이가 맞지 않습니다 
							return YdConstant.RETN_CD_FAILURE;
						}
						
						
						if(szStkPos.equals(szOldStkPos)){
							szMsg ="[JSP Session]권하위치 지시위치가 변경되지 않았습니다."; 
		            		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							
		            		return "지시위치가 변경되지 않았습니다";
							
						}
						
						//1후판B동대상: 초장적제품이 포함된 작업을 장척베드에 내려놓으려고 할때 에러처리  이현우 책임 요청 2023.10.23 REQ202310501735
							YdPlateCommDAO	commDao 		= new YdPlateCommDAO();
							JDTORecordSet rsResult 		    = JDTORecordFactory.getInstance().createRecordSet("temp");
							recPara = JDTORecordFactory.getInstance().create();
							recPara.setField("YD_EQP_ID"    , inDto[0].getFieldString("YD_EQP_ID")); //비어있음.
			    	    	//recInTemp.setField("OLD_STK_POS"    , szOldStkPos);
							recPara.setField("YD_CRN_SCH_ID"    , yddatautil.setDataDefault(inDto[0].getField("YD_CRN_SCH_ID"), ""));
							recPara.setField("STK_POS"       , szStkPos);  //신규 권하지
							//if(1==1){
							if (commDao.select(recPara, rsResult,"com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getIsChgBBedType") > 0) {
								// 레코드 추출
								rsResult.first();
								
								//sCRN_WRK_PROC_STAT = ydSlabUtils.trim(rsResult.getRecord(0).getFieldString("YD_EQP_STAT")); 
								String IS_CHG_B_BED_TYPE=rsResult.getRecord(0).getFieldString("IS_CHG_B_BED_TYPE");	
								
								if(IS_CHG_B_BED_TYPE.equals("Y")){
									szMsg ="1후판 B동은, 초장적 제품이 포함된 작업을 장척베드에 권하할 수 없습니다.";
				            		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				            		
									return "1후판 B동은, 초장적 제품이 포함된 작업을 장척베드에 권하할 수 없습니다.";
								}
							}					
						
						// 신규 위치 적치단 정보
						
						recPara    = JDTORecordFactory.getInstance().create();
						recTemp    = outRecSet.getRecord();
						outRecSet  = JDTORecordFactory.getInstance().createRecordSet("YD");
						
						recPara.setField("YD_STK_COL_GP", szStkColGp);
						recPara.setField("YD_STK_BED_NO", szStkBedNo);
						
						intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 29);
						
						if (intRtnVal == 0){
							szStkLyrNo ="001";
						}
						else if ( intRtnVal >0 )
						{
							outRecSet.last();
							recTemp = outRecSet.getRecord();
							
							szStkLyrNo =ydDaoUtils.stringPlusInt(recTemp.getFieldString("YD_STK_LYR_NO"),1);
							
							
						}
							
						
						szMsg = "[JSP Session]권하위치 변경 - 신규위치정보 :"+ szStkColGp  + "-"+ szStkBedNo +":" +szStkLyrNo;				
						ydUtils.putLog(szSessionName,szMethodName,szMsg  , YdConstant.DEBUG);
						
						
						recPara   = JDTORecordFactory.getInstance().create();
						recPara.setField("YD_CRN_SCH_ID", yddatautil.setDataDefault(inDto[0].getField("YD_CRN_SCH_ID"), ""));
						
						outRecSet  = JDTORecordFactory.getInstance().createRecordSet("YD");
						intRtnVal = ydCrnWrkMtlDao.getYdCrnwrkmtl(recPara, outRecSet, 1);
						
						if (intRtnVal < 1)
						{
							//해당 스케줄에 해당되는 재료가 없습니다.
							szMsg ="[JSP Session]권하위치 변경 - 해당 스케줄에 해당되는 재료가 없습니다.";
		            		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							return YdConstant.RETN_CD_NOTEXIST;
						}
						
						
						// 2021.01.06 전사물류개선 자동화크레인 Y9시스템관련  Start 
						szYD_EQP_ID = outRecSet.getRecord(0).getFieldString("YD_EQP_ID");
						isSendToEaiY9 = PlateGdsYdUtil.isSendToEaiY9_ydEqpId(szYD_EQP_ID);
						
						if(isSendToEaiY9){
							// Y9YDL015에서 던져주는 값

							JDTORecordSet rsEqpInfo = JDTORecordFactory.getInstance().createRecordSet("temp");
							if( YdCommonUtils.getYdEqp(szYD_EQP_ID, rsEqpInfo) > 0 ) {
								String szydEqpStat		= rsEqpInfo.getRecord(0).getFieldString("YD_EQP_STAT");   // 설비 상태
								String szEqpAutoCrnMode= rsEqpInfo.getRecord(0).getFieldString("YD_EQP_AUTO_CRN_MODE"); // AutoCrn 상태
								String szEqpAutoCrnYN 	= rsEqpInfo.getRecord(0).getFieldString("YD_EQP_WRK_MODE2");   // AutoCrn 여부	
								String szSendYdWrkProgStat =  rsEqpInfo.getRecord(0).getFieldString("YD_WRK_PROG_STAT"); // 현재 DB의 크레인스케쥴 상태
								
								//   Auto크레인시에 일시정지(4) 상태만 가능 ( 대기, 선택, 권상지 ) 일경우 응답결과를 받고 처리하는걸로 수정조치함
//								if (("A".equals(szEqpAutoCrnYN) || "R".equals(szEqpAutoCrnYN))) {
								if ("A".equals(szEqpAutoCrnYN)) {
//										if (!"4".equals(szEqpAutoCrnMode) && !("W".equals(szSendYdWrkProgStat) || "S".equals(szSendYdWrkProgStat)  || "1".equals(szSendYdWrkProgStat)) && !"B".equals(szydEqpStat)) {
									if ( !("4".equals(szEqpAutoCrnMode) || "5".equals(szEqpAutoCrnMode)) && !"W".equals(szSendYdWrkProgStat)) {
										szLogMsg = "[JSP Session] " + szOperationName + "L2에 지시가 내려간 무인크레인 [" + szYD_EQP_ID + "]이 일시정지(4)이거나, 비상정지(5) 상태가 아니면 변경 할 수 없습니다.";
										
										ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
										return  "L2에 지시가 내려간 자동화 크레인[" + szYD_EQP_ID + "] 일시정지(4)이거나, 비상정지(5) 상태가 아니면 변경 할 수 없습니다.";
									}
									
									// 지시가 내려간 Case
									if(!"W".equals(szSendYdWrkProgStat)){
						    	    	szLogMsg = "[JSP Session] " + szOperationName + "무인크레인 [" + szYD_EQP_ID +"]  YD_DN_WO_LOC_TO["+ szStkColGp +"-"+ szStkBedNo +"]";
						    	    	ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						    	    	
						    	    	//YdPlateCommDAO	commDao 		= new YdPlateCommDAO();
						    	    	JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
						    	    	recInTemp.setField("YD_DN_WO_LOC_TO"    , szStkColGp + szStkBedNo );
						    	    	recInTemp.setField("STL_NO_TEMP"        , "");
						    	    	recInTemp.setField("STK_LYR_NO_TEMP"    , szStkLyrNo);
						    	    	recInTemp.setField("YD_L2_REQUEST_STAT" , "5");
						    	    	recInTemp.setField("YD_CRN_SCH_ID"      , inDto[0].getField("YD_CRN_SCH_ID"));
						    	    	
						    	    	intRtnVal = commDao.update(recInTemp,"com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.upYdCrnSchLocStat1");
						    	    	
						    	    	if(intRtnVal>0){
							    	    	JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
							    	    	jrYdMsg.setField("JMS_TC_CD"       , "YDYDJ642");	//크레인작업지시요구
							    	    	jrYdMsg.setField("YD_EQP_ID"       , szYD_EQP_ID       );	//야드설비ID
							    	    	jrYdMsg.setField("YD_WRK_PROG_STAT", "5");	//야드작업진행상태(권하위치변경 요구상태)	    					
							    	    	jrYdMsg.setField("YD_SCH_CD"       , inDto[0].getField("YD_SCH_CD")      );	//야드스케쥴코드
							    	    	jrYdMsg.setField("YD_CRN_SCH_ID"   , inDto[0].getField("YD_CRN_SCH_ID")   );	//야드크레인스케쥴ID
							    	    	
							    	    	EJBConnector ydEjbCon = new EJBConnector("default", "CraneLdHdSeEJB", this); 
							    	    	ydEjbCon.trx("procY4CrnWrkOrdReq",   new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
							    	    	
							    	    	return YdConstant.RETN_CD_SUCCESS;
						    	    	}
									}
								}
							}
						}
						
						//기존 산적위치 정보가 올바르지 않거나 없을 경우는 기존산적위치를 삭제할 수없으므로 건너띈다.
						
						if( szOldStkPos.length() == 8)
						{	
							szOldStkColGp = szOldStkPos.substring(0, 6); 
							szOldStkBedNo = szOldStkPos.substring(6, 8);
							szMsg = "[JSP Session]권하위치 변경 - 기존위치정보 :"+ szOldStkColGp  + "-"+ szOldStkBedNo +":" +szOldStkLyrNo;				
							ydUtils.putLog(szSessionName,szMethodName,szMsg  , YdConstant.DEBUG);
							
							
							//실제로는 크레인작업재료의 개수만 필요함				
							outRecSet.first();
							JDTORecord outRec = null;
							for (int nLoop =0 ; nLoop<outRecSet.size();nLoop++ ){
								
								/*
								 * 2021. 11. 30 주석처리
								 * 권하위치 초기화시
								 * 재료번호 조회조건 추가로직으로 변경처리 
								// 기존 지시위치 에 쌓여 있는 정보 Clear
								recSet = JDTORecordFactory.getInstance().create();
				                recSet.setField("YD_STK_COL_GP",       szOldStkColGp);    
				                recSet.setField("YD_STK_BED_NO",       szOldStkBedNo);   
				                //적치단 설정
				                recSet.setField("YD_STK_LYR_NO",       ydDaoUtils.stringPlusInt(szOldStkLyrNo, nLoop)) ;	                
				                recSet.setField("YD_STK_LYR_MTL_STAT", "E");
				                recSet.setField("STL_NO",              "");
				                intRtnVal = ydStkLyrDao.updYdStklyr(recSet, 0);
				                */
				                
//								2021. 11. 30 
//								권하위치 초기화시
//								재료번호 조회조건 추가로직으로 변경처리 
								

				                szLogMsg = "[JSP Session] " + szOperationName +   "기존 지시위치 에 쌓여 있는 정보 Clear ";
			            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			            		
								outRec = outRecSet.getRecord(nLoop);	
								recSet = JDTORecordFactory.getInstance().create();
				                recSet.setField("YD_STK_COL_GP",       szOldStkColGp);    
				                recSet.setField("YD_STK_BED_NO",       szOldStkBedNo);   
				                recSet.setField("STL_NO", yddatautil.setDataDefault(outRec.getField("STL_NO"),""));
			            		intRtnVal = ydStkLyrDao.updYdStklyrWithColStockStat(recSet);
				                
			            		szLogMsg = "[JSP Session] " + szOperationName +   "기존 지시위치 에 쌓여 있는 정보 Clear 성공 [ " + intRtnVal + " ] ";
				            	ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);

//				            	초기화시 재료가 없다는 건 실제 'D'로 된 것이 없어서 해당 체크안함
//				            	if (intRtnVal < 1)
//								{
//				            		szMsg ="[JSP Session]권하위치 변경 - 기존 지시위치 에 쌓여 있는 정보 Clear 실패";
//				            		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//									return YdConstant.RETN_CD_FAILURE;
//								}
							}		
						}
						
									
						outRecSet.first();
						for (int nLoop =0 ; nLoop<outRecSet.size(); nLoop++ ){
							
							// 신규위치에 정보를 Setting
							recSet = JDTORecordFactory.getInstance().create();
							recTemp =JDTORecordFactory.getInstance().create();
							
							recTemp = outRecSet.getRecord(nLoop);					
							recSet.setField("YD_STK_COL_GP",       szStkColGp);    
				            recSet.setField("YD_STK_BED_NO",       szStkBedNo);   
				            recSet.setField("YD_STK_LYR_NO",       ydDaoUtils.stringPlusInt(szStkLyrNo, nLoop)) ;
				            recSet.setField("YD_STK_LYR_MTL_STAT", "D");
			                recSet.setField("STL_NO",              recTemp.getField("STL_NO"));
			                
			                intRtnVal = ydStkLyrDao.updYdStklyr(recSet, 0);
			            	if (intRtnVal < 1)
							{
								//신규위치에 정보를 Setting 실패
			            		szMsg ="[JSP Session]권하위치 변경 - 신규위치에 정보를 Setting 실패";
			            		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			            		return YdConstant.RETN_CD_FAILURE;
							}
						}
						
						
						// 권하지시 위치의 적치열/적치베드 / 적치단 정보로 해당 적치단의 위치값을 읽어온다.
						recPara   = JDTORecordFactory.getInstance().create();
						
						recPara.setField("YD_STK_COL_GP",       szStkColGp);    
						recPara.setField("YD_STK_BED_NO",       szStkBedNo);   
						recPara.setField("YD_STK_LYR_NO",       szStkLyrNo) ;
			            
			            recStkLyr = JDTORecordFactory.getInstance().create();
			            rsStkLyr  = JDTORecordFactory.getInstance().createRecordSet("YD");
			       
			            
			            intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsStkLyr, 0);
			            
			            
			            
			            boolean bTest = false;  //적치단 좌표 조회가능 유무
			            if(intRtnVal < 0 ){
			            	
			            	szMsg ="[JSP Session - " + szOperationName +"[ - 적치단 정보 조회 ERROR";
		            		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			            	
			            } 
			            
			            else if (intRtnVal == 0){
			            	szMsg ="[JSP Session - " + szOperationName +"[ - 적치단 정보가 없습니다.";
		            		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			            }
						
			            else{ 
			            	
			            	szMsg ="[JSP Session - " + szOperationName +"[ - 적치단 조회 완료(좌표)";
		            		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		            		
			            	bTest = true;
			            	rsStkLyr.first();
			            	recStkLyr = rsStkLyr.getRecord();
			            }
			            
				
						// 권하위치 정보 스케줄 정보에서 변경
						recPara   = JDTORecordFactory.getInstance().create();
						recPara.setField("YD_CRN_SCH_ID", yddatautil.setDataDefault(inDto[0].getField("YD_CRN_SCH_ID"), ""));
						recPara.setField("YD_DN_WO_LOC", yddatautil.setDataDefault(inDto[0].getField("YD_DN_WO_LOC"), ""));
						recPara.setField("YD_DN_WO_LAYER", szStkLyrNo);
						
						if(bTest){
							
							recPara.setField("YD_DN_WO_LOC_XAXIS", ydDaoUtils.paraRecChkNull(recStkLyr, "YD_STK_LYR_XAXIS"));
							recPara.setField("YD_DN_WO_LOC_YAXIS", ydDaoUtils.paraRecChkNull(recStkLyr, "YD_STK_LYR_YAXIS"));
							recPara.setField("YD_DN_WO_LOC_ZAXIS", ydDaoUtils.paraRecChkNull(recStkLyr, "YD_STK_LYR_ZAXIS"));
						}
						
						ydUtils.displayRecord(szOperationName, recPara);
						
						intRtnVal = ydCrnSchDao.updYdCrnsch(recPara, 0);
						
						if (intRtnVal < 1)
						{
							//권하위치 스케줄 정보 변경 실패
							szMsg ="[JSP Session]권하위치 변경 - 권하위치 스케줄 정보 변경 실패";
		            		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		            		return YdConstant.RETN_CD_FAILURE;
						}
						 
						// 스케줄 변경 후 제원 위치정보를 맞춰준다.
						szLogMsg =  "[JSP Session] " + szOperationName +  "스케줄 변경 후 제원 위치정보 세팅 호출";
		        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		        		
		        		boolean lb_updYdCrnBed = false;        		
		        		lb_updYdCrnBed = YdUtils.updYdCrnschBedData(recPara);
		        		
		        		if(!lb_updYdCrnBed){
		        			szLogMsg =  "[JSP Session] " + szOperationName +  "스케줄 변경 후 제원 위치정보 세팅 실패";
		            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		        		}
		        		
						//권하위치 변경  후  작업 재지시 
						String szYdGp ="";
					
						
						szYdGp = inDto[0].getFieldString("YD_GP");			
						
						recPara = JDTORecordFactory.getInstance().create();
						
						if (YdConstant.YD_GP_C_SLAB_YARD.equals(szYdGp)){						//C연주 슬라브 야드[A]
							//recPara.setField("JMS_TC_CD","YDYDJ640");
							szJMS_TC_CD = "YDYDJ640";
							szEjbMethod = "procY1CrnWrkOrdReq";
						}else if(YdConstant.YD_GP_PORT_SLAB_YARD.equals(szYdGp)){			//항만슬라브야드 기능추가 - 2015.12.30 LeeJY
							szJMS_TC_CD = "YDYDJ640";
							szEjbMethod = "procY1CrnWrkOrdReq";
						}else if(YdConstant.YD_GP_A_PLATE_SLAB_YARD.equals(szYdGp)){			//A후판 슬라브야드[D]
							//recPara.setField("JMS_TC_CD","YDYDJ641");
							szJMS_TC_CD = "YDYDJ641";
							szEjbMethod = "procY3CrnWrkOrdReq";
						}else if(YdConstant.YD_GP_PLATE_GDS_YARD.equals(szYdGp)){				//후판제품야드 [K]
							//recPara.setField("JMS_TC_CD","YDYDJ642");
							szJMS_TC_CD = "YDYDJ642";
							szEjbMethod = "procY4CrnWrkOrdReq";
						}else if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYdGp)){				//2후판제품야드 [T] - 2013.01.04 추가 (3기)
							szJMS_TC_CD = "YDYDJ642";
							szEjbMethod = "procY4CrnWrkOrdReq";
						}else if(YdConstant.YD_GP_C_HR_COIL_MATL_YARD.equals(szYdGp)){			//C열연 코일야드[H]
							//recPara.setField("JMS_TC_CD","YDYDJ643");
							szJMS_TC_CD = "YDYDJ643";
							szEjbMethod = "procY5CrnWrkOrdReq";
						}else if(YdConstant.YD_GP_C_HR_COIL_GDS_YARD.equals(szYdGp)){			//C열연 제품야드[J]
							//recPara.setField("JMS_TC_CD","YDYDJ643");
							szJMS_TC_CD = "YDYDJ643";
							szEjbMethod = "procY5CrnWrkOrdReq";
						}else if(YdConstant.YD_GP_INTGR_YARD.equals(szYdGp)){					//통합제품야드[S]
							//recPara.setField("JMS_TC_CD","YDYDJ643");
							szJMS_TC_CD = "YDYDJ644";
							szEjbMethod = "procY0CrnWrkOrdReq";
						}
					
						szYD_EQP_ID = inDto[0].getFieldString("YD_EQP_ID");
						
						szLogMsg = "[JSP Session]권하위치 변경  - 야드구분[" + szYdGp + "], 크레인 설비[" + szYD_EQP_ID + "]";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		//SJH03004				
						recPara.setField("JMS_TC_CD", 			szJMS_TC_CD);
						recPara.setField("YD_EQP_ID" , 			szYD_EQP_ID);
						recPara.setField("YD_WRK_PROG_STAT" , 	inDto[0].getFieldString("YD_WRK_PROG_STAT"));
						                   
						ydUtils.displayRecord(szOperationName, recPara);
						//JMS Call
						//ydDelegate.sendMsg(recPara);
						
						//EJB Method Call
		//sjhkim
						if( szEjbMethod.equals("procY5CrnWrkOrdReq") ) {
							ejbConn = new EJBConnector("default", "CoilCraneLdHdSeEJB", this);					
						} else{
							ejbConn = new EJBConnector("default", "CraneLdHdSeEJB", this);		
						}
						
						szRtnMsg = (String)ejbConn.trx(szEjbMethod, new Class[] { JDTORecord.class }, new Object[] { recPara });
			
						if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {													//성공
							szLogMsg = "[JSP Session]권하위치 변경 성공 - 크레인 작업지시";
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						}else if( szRtnMsg.equals(YdConstant.RETN_CRN_SCH_PROH) ) {											//스케줄금지 상태
							szLogMsg = "[JSP Session]권하위치 변경 성공 - 크레인 작업지시 : 스케줄금지 상태";
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						}else if( szRtnMsg.equals(YdConstant.RETN_CD_TC_ERROR) ) {											//전문 에러
							szLogMsg = "[JSP Session]권하위치 변경 성공 - 크레인 작업지시 : 전문 에러";
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						}else if( szRtnMsg.equals(YdConstant.RETN_CRN_NO_SCH) ) {											//크레인스케쥴이 존재하지 않음
							szLogMsg = "[JSP Session]권하위치 변경 성공 - 크레인 작업지시 : 크레인스케쥴이 존재하지 않음";
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						}else if( szRtnMsg.equals(YdConstant.RETN_CRN_NO_WRK) ) {											//작업예약이 더 이상 존재하지 않음
							szLogMsg = "[JSP Session]권하위치 변경 성공 - 크레인 작업지시 : 작업예약이 더 이상 존재하지 않음";
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						}else if( szRtnMsg.equals(YdConstant.RETN_CD_FAILURE) ) {											//실패
							szLogMsg = "[JSP Session]권하위치 변경 실패 - 크레인 작업지시 실패";
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						}
						
				} catch (Exception e) {
					// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.			
					szLogMsg = "[JSP Session]권하위치 변경 에러발생 : " + e.getMessage();
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					throw new DAOException(getClass().getName() + e.getMessage(),e);
				} finally {
				}
				
				szLogMsg = "JSP-SESSION [ 권하위치 변경 (크레인 상태관리화면)] 끝";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				return szRtnMsg;
			}	// end of updCrnDnPrsFix4G
			
			
			/**
			 * 설비 고장/정상설정
			 *
			 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
			 * @param inDto
			 * @return String[]
			 * @throws DAOException
			 */
			public String[] updSlabYdCrnStsSetCrnStat4G(JDTORecord[] inDto) throws DAOException {
				int intRtnVal = 0;
				String szLogMsg = null;
				String[] szRtnMsg = null;

				String szMethodName="updSlabYdCrnStsSetCrnStat4G";
				String szYD_EQP_ID = null;
				String szYD_EQP_STAT = null;
				JDTORecord recPara = JDTORecordFactory.getInstance().create();

				JDTORecord recEqpInfo = JDTORecordFactory.getInstance().create();
				JDTORecordSet rsEqpInfo = JDTORecordFactory.getInstance().createRecordSet("rsEqpInfo");


				YdEqpDao ydEqpDao = new YdEqpDao();
				szRtnMsg = new String[inDto.length];

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
  
						recPara.setField("MSG_ID", "Y9YDL004");
					   	ejbConn = new EJBConnector("default", this);
					   	ejbConn.trx("PlateYdRcvL2SeEJB", "rcvY9YDL004", recPara);


						szLogMsg = "[JSP SESSION - (복구리스케줄     "+ szEjbConName +")을 호출  끝";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						
						if(inDto == null || inDto.length > 1 ){
							szRtnMsg = new String[1];
							szRtnMsg[0] =  "정상적으로 처리되었습니다.";
						}else{
							szRtnMsg[0] =  "정상적으로 처리되었습니다.";
						}

						//------------------------------------------------------------------------------------------------
					}
				} catch (Exception e) {
					// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
					throw new DAOException(getClass().getName() + e.getMessage(), e);
				} finally {
				}


				szMsg = "[JSP Session : "+szOperationName+"] 메소드 끝 ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				return szRtnMsg;
			}	// end of updSlabYdCrnStsSetCrnStat4G


			/**
			 * 설비 ON_LINE, OFF_LINE 설정
			 *
			 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
			 * @param inDto
			 * @return String[]
			 * @throws DAOException
			 */
			public String[] updSlabYdCrnStsSetCrnMode4G(JDTORecord[] inDto) throws DAOException {

				String[] szRtnMsg = new String[inDto.length];
				String szLogMsg   = null;
				String szMethodName = "updSlabYdCrnStsSetCrnMode4G";
				String szYD_EQP_ID = "";
				String szYD_EQP_WRK_MODE = "";
				String szYD_EQP_WRK_MODE2 = "";
				String szEjbConName = "";
				String szRcvTcCode = "";
				String szOperationName = "설비 ON_LINE, OFF_LINE 설정";

				JDTORecord recPara  = JDTORecordFactory.getInstance().create();
				EJBConnector ejbConn = null;

				try {
					//설비 ON_LINE, OFF_LINE 설정

					szLogMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

					for(int x=0;x<inDto.length;x++){
						szYD_EQP_ID = yddatautil.setDataDefault(inDto[x].getField("YD_EQP_ID"), "");
						szYD_EQP_WRK_MODE = yddatautil.setDataDefault(inDto[x].getField("YD_EQP_WRK_MODE"), "");
						szYD_EQP_WRK_MODE2 = yddatautil.setDataDefault(inDto[x].getField("YD_EQP_WRK_MODE2"), "");
						//입출입  상태
						recPara.setField("YD_EQP_ID", szYD_EQP_ID);
						recPara.setField("YD_EQP_WRK_MODE", szYD_EQP_WRK_MODE);
						recPara.setField("YD_EQP_WRK_MODE2", szYD_EQP_WRK_MODE2);
 
					}

					recPara.setField("MSG_ID", "Y9YDL003");
				   	ejbConn = new EJBConnector("default", this);
				 	ejbConn.trx("PlateYdRcvL2SeEJB", "rcvY9YDL003", recPara);

					szLogMsg = "[JSP SESSION - (설비 운전모드 전환     "+ szEjbConName +")을 호출  끝";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);

					szLogMsg = "[JSP Session : "+szOperationName+"] 메소드 끝 ";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
					
					if(inDto == null || inDto.length > 1 ){
						szRtnMsg = new String[1];
						szRtnMsg[0] =  "정상적으로 처리되었습니다.";
					}else{
						szRtnMsg[0] =  "정상적으로 처리되었습니다.";
					}

				} catch (Exception e) {
					// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
					throw new DAOException(getClass().getName() + e.getMessage(),e);
				} finally {
				}

				return szRtnMsg;
			}	// end of updSlabYdCrnStsSetCrnMode4G
			
			
			/**
			 * 크레인스케쥴 응답백업처리
			 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
			 * @param inDto
			 * @return
			 * @throws DAOException
			 */
			public JDTORecord  updbtCrnStsSetPp(JDTORecord[] inDto) throws DAOException {
				String szLogMsg   = null;
				String szMethodName = "updbtCrnStsSetPp";
				String szYD_EQP_ID = "";
				String szOperationName = "응답백업처리";

				JDTORecord recPara  = null;
				JDTORecordSet outRecSet  = null;
				JDTORecord jrParam  = null; 
				EJBConnector ejbConn = null;
				
			    JDTORecord    rtnJto  = JDTORecordFactory.getInstance().create();
			    String szYD_WRK_PROG_STAT = "";
			    String szYD_CRN_SCH_ID    = "";
			    String szYD_SCH_CD = "";
			    String szYD_WBOOK_ID = "";
			    
			    YdPlateCommDAO	commDao  = null;
			    
				try {
					//설비 ON_LINE, OFF_LINE 설정
					commDao 		= new YdPlateCommDAO();
					szLogMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
					
					rtnJto.setField("STATUS", YdConstant.RETN_CD_SUCCESS);
					rtnJto.setField("MESSAGE", "응답백업처리 완료");
					
					for(int x=0;x<inDto.length;x++){
						
						szYD_EQP_ID = yddatautil.setDataDefault(inDto[x].getField("YD_EQP_ID"), "");
						szYD_SCH_CD  = yddatautil.setDataDefault(inDto[x].getField("YD_SCH_CD"), "");
						szYD_WBOOK_ID = yddatautil.setDataDefault(inDto[x].getField("YD_WBOOK_ID"), "");
						// 1. 기존 To 위치 정보 와 변경 To 위치 정보 
						recPara   = JDTORecordFactory.getInstance().create();
						outRecSet  = JDTORecordFactory.getInstance().createRecordSet("YD");
						recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
						
						if(commDao.select(recPara, outRecSet, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.getYdCrnschByWbookId") > 0){
							if(PlateGdsYdUtil.isSendToEaiY9_ydEqpId(szYD_EQP_ID)){
								szYD_WRK_PROG_STAT = outRecSet.getRecord(0).getFieldString("YD_WRK_PROG_STAT");
								szYD_CRN_SCH_ID = outRecSet.getRecord(0).getFieldString("YD_CRN_SCH_ID");
								
								if(!"S".equals(szYD_WRK_PROG_STAT)){
									rtnJto.setField("STATUS", YdConstant.RETN_CD_FAILURE);
									rtnJto.setField("MESSAGE", "["+szYD_EQP_ID+"]는 응답대기 상태가 아닙니다. 야드진행상태["+szYD_WRK_PROG_STAT+"]");
									return rtnJto;
//									throw new Exception("["+szYD_EQP_ID+"]는 응답대기 상태가 아닙니다. 야드진행상태["+szYD_WRK_PROG_STAT+"]");
								}
								
								jrParam   = JDTORecordFactory.getInstance().create();
								jrParam.setField("MSG_ID"	        , "Y9YDL015" );
								jrParam.setField("MSG_GP"		    , "I" );
								jrParam.setField("YD_EQP_ID"		, szYD_EQP_ID );
								jrParam.setField("YD_WRK_PROG_STAT"	, "1" );
								jrParam.setField("YD_SCH_CD"		, szYD_SCH_CD );
								jrParam.setField("YD_CRN_SCH_ID"	, szYD_CRN_SCH_ID );
								jrParam.setField("REQ_YN"	        , "Y" );
								ejbConn = new EJBConnector("default", "PlateYdRcvL2SeEJB", this);
								ejbConn.trx("rcvY9YDL015", new Class[] { JDTORecord.class }, new Object[] { jrParam });
							} 	
						}
						else{
							rtnJto.setField("STATUS", YdConstant.RETN_CD_FAILURE);
							rtnJto.setField("MESSAGE", "["+szYD_EQP_ID+"]는 응답대기 상태가 아닙니다.");
							return rtnJto;
						}
					} 

					szLogMsg = "[JSP Session : "+szOperationName+"] 메소드 끝 ";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
					 
				} catch (Exception e) {
					// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
					throw new DAOException(getClass().getName() + e.getMessage(),e);
				} finally {
				}

				return rtnJto;
			}
			
			
			/**
			 * (작업)크레인 변경 
			 *
			 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
			 * @param inDto
			 * @return Boolean
			 * @throws DAOException
			 */
			public JDTORecord  wrkCrnChange4G(JDTORecord[] recMsg) throws DAOException {
				
				JDTORecord recPara 			= null;
				JDTORecord recTemp 			= null;	
				JDTORecord recTemp2 		= null;
				JDTORecord recEqpInfo 		= null;
				JDTORecord recSchInfo 		= null;
				JDTORecord recDelPara 		= null;
				
			 	JDTORecordSet rsrstDataSch 	= null;
			 	JDTORecordSet rsEqpInfo 	= null;
			 	JDTORecordSet rsSchInfo 	= null;
			 	
		    	YdCrnSchDao  ydCrnSchDao 	= new YdCrnSchDao ();
		    	YdWrkbookDao ydWrkbookDao	= new YdWrkbookDao();
		    	YdSchRuleDao ydSchRuleDao 	= new YdSchRuleDao();
		    	YdEqpDao ydEqpDao 			= new YdEqpDao();
		    	YdDelegate ydDelegate 		= new YdDelegate();
		    	
		    	String szWbookId			= null;
		    	String szWrkCrn 			= null;
		    	String szAltCrn				= null;
		    	String szChgCrn 			= null;
		    	String szEqpId				= null;
		    	String szSchCd              = null;
		    	
		    	int intGp 			= 0;
		    	int intWrkCrnPrior	= 0;
		    	int intAltCrnPrior 	= 0;
		    	int intCrnPrior		= 0;
		    	
		    	String szMethodName		= "wrkCrnChange4G";		
		    	String szOperationName 	= "(작업)크레인 변경";
		    	String szLogMsg 		= "";
		    	
		    	String szYD_GP			= "";
		    	String szRtnValue 		= YdConstant.RETN_CD_SUCCESS;
		    	
		    	String szJMS_TC_CD 		= null;
		    	String szWrkProgStat 	= "";
		    	
		    	boolean sbSendFlag ;
		    	
		    	EJBConnector ejbConn = null;
		    	
			 	boolean isSendToEaiY9 = false;
			 	
			 	
			 	JDTORecord rtnJDTORecord = null;
			 	List aSendMsg = null;
			 	
				try{

					szLogMsg = "JSP-SESSION [" + szOperationName + " ]시작";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
					
					recTemp2 = JDTORecordFactory.getInstance().create();
					rtnJDTORecord = JDTORecordFactory.getInstance().create();
					aSendMsg = new ArrayList();
					rtnJDTORecord.setField("ERROR_MSG", szRtnValue);
					
					for(int x=0 ; x < recMsg.length ;x++){
						sbSendFlag = false; 
						
						szYD_GP	= recMsg[x].getFieldString("YD_SCH_CD").substring(0, 1);
						 
						//설비 ID
						szEqpId = recMsg[x].getFieldString("YD_EQP_ID");
						
						
						szSchCd=  recMsg[x].getFieldString("YD_SCH_CD");
				
						/*
						 * 1. 체크 선택 된 크레인 스케줄 정보를 가지고 온다.	
						 */
						rsrstDataSch =  JDTORecordFactory.getInstance().createRecordSet("YD");
						recTemp = JDTORecordFactory.getInstance().create();
						recPara = JDTORecordFactory.getInstance().create();
						
						recPara.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(recMsg[x], "YD_CRN_SCH_ID"));
						
						intGp = ydCrnSchDao.getYdCrnsch(recPara, rsrstDataSch, 0);
						
						if(intGp < 1 ){
							// 스케줄 정보가 존재 하지 않을 경우
							szRtnValue = "스케줄 [ " +ydDaoUtils.paraRecChkNull(recMsg[x], "YD_CRN_SCH_ID")  + "정보가 존재 하지 않습니다.";
							rtnJDTORecord.setField("ERROR_MSG", szRtnValue);
							return  rtnJDTORecord;
						}
						
						rsrstDataSch.first();
						recTemp = rsrstDataSch.getRecord();
						
						//설비 상태
						szWrkProgStat	= ydDaoUtils.paraRecChkNull(recTemp, "YD_WRK_PROG_STAT");
						//String szYD_DN_WO_LOC 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_DN_WO_LOC");
						//작업예약 ID
						szWbookId 		= ydDaoUtils.paraRecChkNull(recTemp, "YD_WBOOK_ID");
						
						if(szWrkProgStat.equals(YdConstant.YD_EQP_STAT_UP_CMPL)){
							szRtnValue = "스케줄 ["+  ydDaoUtils.paraRecChkNull(recMsg[x], "YD_CRN_SCH_ID") +"]가 권상완료 상태에서는 상태를 변경 할 수 없습니다.";
							rtnJDTORecord.setField("ERROR_MSG", szRtnValue);
							return  rtnJDTORecord;
						}else if(szWrkProgStat.equals(YdConstant.YD_EQP_STAT_DN_WO)){
							szRtnValue = "스케줄 ["+  ydDaoUtils.paraRecChkNull(recMsg[x], "YD_CRN_SCH_ID") +"]가 권하지시 상태에서는 상태를 변경 할 수 없습니다.";
							rtnJDTORecord.setField("ERROR_MSG", szRtnValue);
							return  rtnJDTORecord;
						}else if(szWrkProgStat.equals(YdConstant.YD_EQP_STAT_DN_CMPL)){
							szRtnValue = "스케줄 ["+  ydDaoUtils.paraRecChkNull(recMsg[x], "YD_CRN_SCH_ID") +"]가 권하완료 상태에서는 상태를 변경 할 수 없습니다.";
							rtnJDTORecord.setField("ERROR_MSG", szRtnValue);
							return  rtnJDTORecord;
						}
						
						/*
						 *  2. 선택된 크레인 스케줄 ID로 편성된 스케줄 기준에서 대체 크레인 설비 ID 와 우선순위를 가지고 온다.
						 */
						
						//1,2후판 제품창고일경우..
						//통합스케줄 사용일경우
						szChgCrn 		= recMsg[x].getFieldString("MOD_EQP_ID");
						intCrnPrior		= recMsg[x].getFieldInt("YD_SCH_PRIOR");
						
						/*
						 * 2-2 변경 할 크레인이 선택되어 있으면 설비 정보를 조회한 후 고장 또는 OFF-LINE 일 경우 변경 할 수 없다고 판단하고 RETURN 한다.
						 */
						recEqpInfo = JDTORecordFactory.getInstance().create();
						rsEqpInfo = JDTORecordFactory.getInstance().createRecordSet("YD");
						recEqpInfo.setField("YD_EQP_ID", szChgCrn);
						
						//해당 설비  szChgCrn 로 설비 정보 조회 
						intGp = ydEqpDao.getYdEqp(recEqpInfo, rsEqpInfo, 0);
						
						if(intGp > 0 ){
						
							rsEqpInfo.first();
							recEqpInfo = rsEqpInfo.getRecord();
							
							if(ydDaoUtils.paraRecChkNull(recEqpInfo, "YD_EQP_STAT").equals(YdConstant.YD_EQP_STAT_BREAK)){
								// 설비 상태가 고장일 경우 
								szRtnValue = "변경 설비["+ szChgCrn+"]가 고장 상태여서 상태를 변경 할 수 없습니다.";
								rtnJDTORecord.setField("ERROR_MSG", szRtnValue);
								return  rtnJDTORecord;
							}
							
							if(ydDaoUtils.paraRecChkNull(recEqpInfo, "YD_EQP_WRK_MODE").equals(YdConstant.YD_EQP_WRK_MODE_OFF_LINE)){
								// 설비 상태가 OFF_LINE 일 경우 
								szRtnValue = "변경 설비["+ szChgCrn+"]가 OFF_LINE 이기때문에 상태를 변경 할 수 없습니다.";
								rtnJDTORecord.setField("ERROR_MSG", szRtnValue);
								return  rtnJDTORecord;
							}
							
							/*2025.04.16 월례회의 내용 작업을 진행하고 있는 크레인에게도 크레인 변경 가능하도록 수정 --임진후 기사 요청*/
							/*if( ydDaoUtils.paraRecChkNull(recEqpInfo, "YD_EQP_STAT").equals(YdConstant.YD_EQP_STAT_UP_WO)|| 
								ydDaoUtils.paraRecChkNull(recEqpInfo, "YD_EQP_STAT").equals(YdConstant.YD_EQP_STAT_UP_CMPL)|| 
								ydDaoUtils.paraRecChkNull(recEqpInfo, "YD_EQP_STAT").equals(YdConstant.YD_EQP_STAT_DN_WO)|| 
								ydDaoUtils.paraRecChkNull(recEqpInfo, "YD_EQP_STAT").equals(YdConstant.YD_EQP_STAT_DN_CMPL)){
								szRtnValue = "변경 설비["+ szChgCrn+"]가 작업지시기 내려가 있기때문에 상태를 변경 할 수 없습니다.";						
								rtnJDTORecord.setField("ERROR_MSG", szRtnValue);
								return  rtnJDTORecord;
							}*/
							
							
							// 2009.12.07 [작업지시 취소 전문은 발생하지 않는다 - 작업재지시 전문만 발생시켜준다]
							// 선택된 정보가 선택된 정보일 경우 작업지시 취소 정보를 송신한다.
							recSchInfo =  JDTORecordFactory.getInstance().create();
							rsSchInfo = JDTORecordFactory.getInstance().createRecordSet("YD");
							
							recSchInfo.setField("YD_CRN_SCH_ID", recMsg[x].getField("YD_CRN_SCH_ID") );
							
							intGp = ydCrnSchDao.getYdCrnsch(recSchInfo, rsSchInfo, 0);
							
							if(intGp > 0 ){
								
								rsSchInfo.first();
								recSchInfo = rsSchInfo.getRecord();
								
								
								// 전사물류개선 2021. 1.6 자동화크레인의 경우 
								// 일시정지상태에서만 변경가능하므로 아래와 같이 로직을 추가한다.
								// 변경전 크레인이 자동화대상 크레인일 경우
								// 야드진행상태가 대기상태가 아닐 경우
								boolean isSendY9MSG = false;
								isSendToEaiY9 = PlateGdsYdUtil.isSendToEaiY9_ydEqpId(szEqpId);
								String sOld_YD_EQP_STAT = "";
								String sOld_YD_EQP_WRK_MODE = "";
								String sYD_L2_REQUEST_STAT = "";
								if(!YdConstant.YD_EQP_STAT_IDLE.equals(ydDaoUtils.paraRecChkNull(recSchInfo, "YD_WRK_PROG_STAT"))){
									if(isSendToEaiY9){
										recEqpInfo = JDTORecordFactory.getInstance().create();
										rsEqpInfo = JDTORecordFactory.getInstance().createRecordSet("YD");
										recEqpInfo.setField("YD_EQP_ID", szEqpId);
										
										//해당 설비  szChgCrn 로 설비 정보 조회 
										intGp = ydEqpDao.getYdEqp(recEqpInfo, rsEqpInfo, 0);
										if(intGp > 0){
											// 자동화 크레인일 경우 상태를 체크하자
											if(YdConstant.YD_EQP_WRK_MODE2_A.equals(rsEqpInfo.getRecord(0).getFieldString("YD_EQP_WRK_MODE2"))){
												if( !( "4".equals(rsEqpInfo.getRecord(0).getFieldString("YD_EQP_AUTO_CRN_MODE")) 
													 || "5".equals(rsEqpInfo.getRecord(0).getFieldString("YD_EQP_AUTO_CRN_MODE")))
												){
													szRtnValue = "자동화크레인의 경우 [일시정지:Crance Mode(4,5)] 또는 [대기(W)] 경우에만 변경 가능합니다.";						
													rtnJDTORecord.setField("ERROR_MSG", szRtnValue);
													return  rtnJDTORecord;
												}
												isSendY9MSG = true;
											}
											//------------------------------------------------------------------------------------------------------------
											//	전사물류개선
											//------------------------------------------------------------------------------------------------------------
											else if (YdConstant.YD_EQP_WRK_MODE2_M.equals(rsEqpInfo.getRecord(0).getFieldString("YD_EQP_WRK_MODE2")))
											{// 20220627  박성열 크레인 유무인 체크 후 유인일 경우 false
												isSendY9MSG = false;
											}
											else
											{
												isSendY9MSG = true;
											}

											sOld_YD_EQP_STAT = ydDaoUtils.paraRecChkNull(recEqpInfo, "YD_EQP_STAT");
											sOld_YD_EQP_WRK_MODE  = ydDaoUtils.paraRecChkNull(recEqpInfo, "YD_EQP_WRK_MODE");
											//isSendY9MSG = true;
										}
									}
								}
								
								// 현 작업 상태가 선택인 경우는 작업재지시 전문을 전송하기위채 체크해놓는다.
								// 권상지시 이거나 Y9시스템에 응답대기 상태인 것들
								if( YdConstant.YD_EQP_STAT_UP_WO.equals(ydDaoUtils.paraRecChkNull(recSchInfo, "YD_WRK_PROG_STAT"))
										|| "1".equals(sYD_L2_REQUEST_STAT)){
									//작업재지시 전문을 전송하기 위하여 Flag Setting
									sbSendFlag = true;
									
									// 취소된 크레인에 대해서 삭제 작업지시 "D"으로 전송
									if(isSendY9MSG){
										JDTORecord jdtoYDY9L004   = JDTORecordFactory.getInstance().create();
										jdtoYDY9L004.setField("MSG_ID",       "YDY9L004"        );
										jdtoYDY9L004.setField("MSG_GP"       , "D"       ); //전문구분(취소)
										jdtoYDY9L004.setField("YD_EQP_ID",    szEqpId            );
										jdtoYDY9L004.setField("YD_CRN_SCH_ID",    ydDaoUtils.paraRecChkNull(recMsg[x], "YD_CRN_SCH_ID") );
										jdtoYDY9L004.setField("YD_WRK_PROG_STAT",YdConstant.YD_EQP_STAT_UP_WO ); 
//										ydDelegate.sendMsg(jdtoYDY9L004);
										aSendMsg.add(jdtoYDY9L004);
										
//										// 이전크레인의 설비상태를 W로 변경처리한다.
//										JDTORecord jtoEqp = JDTORecordFactory.getInstance().create();
//										jtoEqp.setField("YD_EQP_ID", szEqpId);
//										jtoEqp.setField("YD_EQP_STAT", YdConstant.YD_EQP_STAT_IDLE);
//										recPara.setField("MODIFIER",recMsg[x].getField("MODIFIER"));
//										szLogMsg = "JSP-SESSION [" + szOperationName + " ] 설비 정보 UPDATE";
//										ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
//										ydEqpDao.updYdEqp(jtoEqp, 0);
										
										JDTORecord jdtoYDY9L005 = JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용
										jdtoYDY9L005.setResultCode(ydUtils.getLogId());	//Log ID
										jdtoYDY9L005.setField("MSG_ID",           "YDY9L005"       );					
										jdtoYDY9L005.setField("MSG_GP",           "I"               );
										jdtoYDY9L005.setField("YD_EQP_ID"     , szEqpId); //야드설비ID
										jdtoYDY9L005.setField("YD_L2_WR_GP"   , "J"    ); //야드L2실적구분(지시요구)
										jdtoYDY9L005.setField("YD_L3_HD_RS_CD", "9999" ); //야드L3처리결과코드(Error)
										jdtoYDY9L005.setField("YD_L3_MSG"     , "크레인변경[" + szChgCrn + "]" ); //야드L3MESSAGE
//										ydDelegate.sendMsg(jdtoYDY9L005);
										aSendMsg.add(jdtoYDY9L005);
										
										// 변경된 크레인에 대해서 작업지시를 내린다.
										// W상태로 초기화를 했기 때문
										// 고장만 아니라면 
//										if(YdConstant.YD_EQP_STAT_BREAK.equals(sOld_YD_EQP_STAT)
//											&& YdConstant.YD_EQP_WRK_MODE_ON_LINE.equals(sOld_YD_EQP_WRK_MODE)
//										){
//											JDTORecord jdtoY9YDL007   = JDTORecordFactory.getInstance().create();
//											//SJH03004					
//											jdtoY9YDL007.setField("MSG_ID",       szJMS_TC_CD);
//											jdtoY9YDL007.setField("YD_EQP_ID",    szEqpId);
//											jdtoY9YDL007.setField("YD_EQP_WRK_MODE",    sOld_YD_EQP_WRK_MODE);
//											jdtoY9YDL007.setField("YD_WRK_PROG_STAT",YdConstant.YD_EQP_STAT_IDLE );
//											jdtoY9YDL007.setField("YD_SCH_CD", "");
//											jdtoY9YDL007.setField("YD_CRN_SCH_ID", "");
//											aSendMsg.add(jdtoY9YDL007);
//										}
									}
								}
							}
							
						}else{
							//해당 설비가 존재 하지 않습니다.
							szRtnValue = "해당 설비["+ szChgCrn+"]가 존재 하지 않습니다";
							rtnJDTORecord.setField("ERROR_MSG", szRtnValue);
							return  rtnJDTORecord;
						}
						
						
						/*
						 * 3. 선택된 크레인 스케줄 작업예약 ID에  대체 설비 ID 와 우선순위 를 UPDATE 한다.
						 */
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("YD_WBOOK_ID",szWbookId);
						recPara.setField("YD_SCH_PRIOR", new Integer(intCrnPrior));
						recPara.setField("MODIFIER",recMsg[x].getField("MODIFIER"));
						intGp = ydWrkbookDao.updYdWrkbook(recPara, 0);
						if (intGp <1){
							throw new DAOException("선택된 크레인 스케줄 작업예약 ID : "+szWbookId+"에   우선순위 ("+intCrnPrior+") 를 UPDATE 중 ERROR");
						}
						
						/*
						 * 4. 작업예약 ID 로 현재 편성된 스케줄 ID [] 를 구한다.
						 */
						
						recPara = JDTORecordFactory.getInstance().create();
						rsrstDataSch =  JDTORecordFactory.getInstance().createRecordSet("YD");
						
						recPara.setField("YD_WBOOK_ID",szWbookId);
						recPara.setField("YD_WRK_PROG_STAT","W");
						
						// 기존쿼리는 W 이상태만 체크하였으나 지금은 1,W 상태를 조회한다.
						intGp = ydCrnSchDao.getYdCrnsch(recPara, rsrstDataSch, 23);
						
						if (intGp <1 ){					
							// 해당 작업 ID 에 편성된 스케줄 정보가 없을경우  
							throw new DAOException("해당작업 ID 에 편성된 스케줄 정보가 존재하지 않음");
						}	
						
						//크레인 스케줄 정보 변경
						rsrstDataSch.first();
						
						do
						{	
							/*
							 * 5. 편성된 스케줄 ID [] 에  대체 크레인 설비 ID와 우선순위를 편성
							 */
							recTemp = JDTORecordFactory.getInstance().create();
							recPara = JDTORecordFactory.getInstance().create();
							
							recTemp = rsrstDataSch.getRecord();
							
							recPara.setField("YD_CRN_SCH_ID", recTemp.getField("YD_CRN_SCH_ID"));
							recPara.setField("YD_EQP_ID",szChgCrn);					
							recPara.setField("YD_SCH_PRIOR",  new Integer(intCrnPrior));
							recPara.setField("MODIFIER",recMsg[x].getField("MODIFIER"));
							recPara.setField("YD_WRK_PROG_STAT","W");//선택한 스케줄의 작업진행상태를 대기로 변경
							
							// 5. 스케줄 테이블에 UPDATE 
							intGp = ydCrnSchDao.updYdCrnsch(recPara, 0);
							if (intGp <1){
								throw new DAOException("스케줄 테이블에 UPDATE 중 ERROR");
							}
							
							//-------------------------------------------------------------------------------------------------------------
							//	크레인 허용 오차 및 크레인 X, Y좌표 계산 - 임춘수 2009.11.26
							//-------------------------------------------------------------------------------------------------------------
							if( YdConstant.YD_GP_PLATE_GDS_YARD.equals(szYD_GP) 
									|| YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYD_GP)) { //- 2012.12.28 수정 (3기)
								
								szLogMsg ="크레인 변경 후 제원 위치정보 세팅 호출";
				        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				        		
				        		recTemp2.setField("YD_CRN_SCH_ID", recTemp.getField("YD_CRN_SCH_ID"));
								YdUtils.updYdCrnschBedData(recTemp2);
						
								szLogMsg ="크레인 스케줄 변경 후 제원 위치정보 세팅 완료";
				        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
							}
							//-------------------------------------------------------------------------------------------------------------
							
						}while(rsrstDataSch.next());			
						
						/*
						 * 6. 작업 재지시 정보를 호출하여준다.
						 */
						
						//sbSendFlag 가 True 라는것은 변경전 스케줄 상태가 작업선택상태임을 나타낸다.
						if(sbSendFlag){
							
							//작업 취소전문이 발생 후 크레인 작업지시 (2009.10.28 요청사항)
							//다음작업이 선택될 수 있도록 하기 위함
							/*  YDYDJ640 procY1CrnWrkOrdReq - C연주 슬라브야드
								YDYDJ641 procY3CrnWrkOrdReq - A후판 슬라브야드 
								YDYDJ642 procY4CrnWrkOrdReq - 제품창고  
								YDYDJ643 procY5CrnWrkOrdReq - C열연 
								YDYDJ644 procY0CrnWrkOrdReq - 통합슬라브 야드
							 */
						 	//변경할 크레인 원 크레인 정보는 선택상태로 세팅을 바꾸어준다.
							
							szLogMsg = "JSP-SESSION [" + szOperationName + " ] 선택된 크레인이 변경될 경우 크레인 변경될 크레인 상태를  선택상태로 변경한다.";
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);	
							
						 	recPara = JDTORecordFactory.getInstance().create();
							recPara.setField("YD_EQP_ID", szChgCrn);
							recPara.setField("YD_EQP_STAT", YdConstant.YD_EQP_STAT_UP_WO);
							recPara.setField("MODIFIER",recMsg[x].getField("MODIFIER"));
							
							szLogMsg = "JSP-SESSION [" + szOperationName + " ] 설비 정보 UPDATE";
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);	
							
							intGp = ydEqpDao.updYdEqp(recPara, 0);
												
							String szEjbConName = "";
							
							szLogMsg = "[JSP Session] "+szOperationName +": 크레인 작업지시   : 야드구분[" + szYD_GP + "]";  // 야드구분은 스케줄 코드앞자리에서 발생되었다. 
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
							
							if (YdConstant.YD_GP_C_SLAB_YARD.equals(szYD_GP)  ){						//C연주 슬라브 야드 [A]
								szJMS_TC_CD = "YDYDJ640";
								szEjbConName = "procY1CrnWrkOrdReq";
								
							}else if(YdConstant.YD_GP_PORT_SLAB_YARD.equals(szYD_GP)){				//항만슬라브야드 기능추가 - 2015.12.30 LeeJY
								szJMS_TC_CD = "YDYDJ640";
								szEjbConName = "procY1CrnWrkOrdReq";
							}else if(YdConstant.YD_GP_A_PLATE_SLAB_YARD.equals(szYD_GP)){				//A후판 슬라브야드[D]
								
								szJMS_TC_CD = "YDYDJ641";
								szEjbConName = "procY3CrnWrkOrdReq";
							
							}else if(YdConstant.YD_GP_PLATE_GDS_YARD.equals(szYD_GP) 
									|| YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYD_GP)){				//후판제품야드 [K],2후판제품야드[T] - 2012.12.28 수정 (3기)
								
								szJMS_TC_CD = "YDYDJ642";
								szEjbConName = "procY4CrnWrkOrdReq";
								
							}else if(YdConstant.YD_GP_C_HR_COIL_MATL_YARD.equals(szYD_GP)){				//C열연 코일야드[H]
							
								szJMS_TC_CD = "YDYDJ643";
								szEjbConName = "procY5CrnWrkOrdReq";
								
								
							}else if(YdConstant.YD_GP_C_HR_COIL_GDS_YARD.equals(szYD_GP)){				//C열연 제품야드[J]
								
								szJMS_TC_CD = "YDYDJ643";
								szEjbConName = "procY5CrnWrkOrdReq";
								
							} 	else if(YdConstant.YD_GP_INTGR_YARD.equals(szYD_GP)){					//통합제품야드[S]
								
								szJMS_TC_CD = "YDYDJ644";
								szEjbConName = "procY0CrnWrkOrdReq";
							} 
							
							
							//JMS => EJB CALL 형식으로 수정요청 
							
							
							recDelPara   = JDTORecordFactory.getInstance().create();
		//SJH03004					
							recDelPara.setField("MSG_ID",       szJMS_TC_CD        );
							recDelPara.setField("YD_EQP_ID",    szChgCrn            );
							recDelPara.setField("YD_WRK_PROG_STAT",YdConstant.YD_EQP_STAT_UP_WO );
							recDelPara.setField("YD_SCH_CD", recMsg[x].getField("YD_SCH_CD") );
												
							ejbConn = new EJBConnector("default", this);	
							if( szEjbConName.equals("procY5CrnWrkOrdReq") ) {
								ejbConn.trx("CoilCraneLdHdSeEJB", szEjbConName, recDelPara);		
							} else{
								ejbConn.trx("CraneLdHdSeEJB", szEjbConName, recDelPara);		
							}
						 	
							//ydDelegate.sendMsg(recDelPara);
							
						 	//크레인 변경 후 원 크레인 정보는 작업대기 상태로 세팅을 바꾸어준다.
						 	szLogMsg = "JSP-SESSION [" + szOperationName + " ] 크레인 변경 후 전 크레인 정보 설비상태는 작업대기 상태로 바꾸어준다.";
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);	
							
						 	recPara = JDTORecordFactory.getInstance().create();
							recPara.setField("YD_EQP_ID",szEqpId);
							recPara.setField("YD_EQP_STAT", YdConstant.YD_EQP_STAT_IDLE);
							recPara.setField("MODIFIER",recMsg[x].getField("MODIFIER"));
							
							intGp = ydEqpDao.updYdEqp(recPara, 0);
							
							//원크레인 정보에대하여 작업실적 응답전문을 전송하여 준다.(2009.12.15)
							szLogMsg = "[JSP Session] 크레인작업실적 응답전송   : 야드구분[" + szYD_GP + "]";
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
							
							szJMS_TC_CD = "";
							if (YdConstant.YD_GP_C_SLAB_YARD.equals(szYD_GP)  ){					//C연주 슬라브 야드 [A]
								szJMS_TC_CD = "YDY1L005";
								
							}else if(YdConstant.YD_GP_PORT_SLAB_YARD.equals(szYD_GP)){			//항만슬라브야드 기능추가 - 2015.12.30 LeeJY
								szJMS_TC_CD = "YDE7L005";
								
							}else if(YdConstant.YD_GP_A_PLATE_SLAB_YARD.equals(szYD_GP)){			//A후판 슬라브야드[D]
								
								szJMS_TC_CD = "YDY3L005";
							
							}else if(YdConstant.YD_GP_PLATE_GDS_YARD.equals(szYD_GP)){				//후판제품야드 [K]
								
								szJMS_TC_CD = "YDY4L005";
								
							}else if(YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYD_GP)){				//2후판제품야드 [T] - 2012.12.28 추가 (3기)
								
								szJMS_TC_CD = "YDY8L005";
								
								// 2021.01.06 전사물류개선 자동화크레인 Y9시스템관련 여부
								if (PlateGdsYdUtil.isSendToEaiY9_ydEqpId(szChgCrn)){
									szJMS_TC_CD = "YDY9L005";
								}
							}else if(YdConstant.YD_GP_C_HR_COIL_MATL_YARD.equals(szYD_GP)){			//C열연 코일야드[H]
							
								szJMS_TC_CD = "YDY5L005";
								
							}else if(YdConstant.YD_GP_C_HR_COIL_GDS_YARD.equals(szYD_GP)){			//C열연 제품야드[J]
								
								szJMS_TC_CD = "YDY5L005";
								
							} 

							// 전사물류개선 Y9는 위에서 이전 장비에 대한 처리를 이미 완료함
							if(!isSendToEaiY9){
								if(!szJMS_TC_CD.equals("")){
									recPara   = JDTORecordFactory.getInstance().create();
									recPara.setField("MSG_ID",           szJMS_TC_CD       );					
									recPara.setField("MSG_GP",           "I"               );
									recPara.setField("YD_EQP_ID",        szEqpId);
									recPara.setField("YD_WRK_PROG_STAT", ""                );
									recPara.setField("YD_SCH_CD",        ""                );
									recPara.setField("YD_CRN_SCH_ID",    ""                );
									recPara.setField("YD_L2_WR_GP",      "J"               );
									recPara.setField("YD_L3_HD_RS_CD",   "9999"            );
									recPara.setField("YD_L3_MSG",        ""                );
									
									aSendMsg.add(recPara);
								}
							}
						}
					}

					

					//여기서 이전 설비 입고 FLAG:N, 변경 설비 입고 FLAG:Y 처리
					//이전설비:szEqpId, 변경설비:szChgCrn
					/*
					 * 				
				if(szSchCd.substring(2,5).equals("RTR") && szSchCd.substring(6,7).equals("L")){
				JDTORecord recSchPara = null;
				recSchPara=JDTORecordFactory.getInstance().create();
				recSchPara.setField("MSG_ID", "YDY8L009");
				recSchPara.setField("EQP_CD", szEqpId);
				recSchPara.setField("YD_BAY", szSchCd.substring(1,2));
				recSchPara.setField("RT", szSchCd.substring(5,6));
				recSchPara.setField("FLAG_YN", "Y");
				ydDelegate.sendMsg(recSchPara);
					 * 
					 */
						if(szSchCd.substring(2,5).equals("RTR") && szSchCd.substring(6,7).equals("L")){  //입고작업일 경우만
							JDTORecord jdtoYDY8L009_OLD = JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용
							jdtoYDY8L009_OLD.setField("MSG_ID",    "YDY8L009");
							jdtoYDY8L009_OLD.setField("EQP_CD",    szEqpId);
							jdtoYDY8L009_OLD.setField("YD_BAY",    szSchCd.substring(1,2));
							jdtoYDY8L009_OLD.setField("RT",    szSchCd.substring(5,6));
							jdtoYDY8L009_OLD.setField("FLAG_YN", "N");   //이전설비는 FLAG:N으로
							
							aSendMsg.add(jdtoYDY8L009_OLD);
							
							JDTORecord jdtoYDY8L009_NEW = JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용
							jdtoYDY8L009_NEW.setField("MSG_ID",    "YDY8L009");
							jdtoYDY8L009_NEW.setField("EQP_CD",    szChgCrn);
							jdtoYDY8L009_NEW.setField("YD_BAY",    szSchCd.substring(1,2));
							jdtoYDY8L009_NEW.setField("RT",    szSchCd.substring(5,6));
							jdtoYDY8L009_NEW.setField("FLAG_YN", "Y");   //변경설비는 FLAG:Y로							
							
							aSendMsg.add(jdtoYDY8L009_NEW);
						}
					//}					
					
					rtnJDTORecord.addField("SEND_DATA", aSendMsg);
					
				}catch(DAOException e){
					ydUtils.putLog("YdJspCommonSeEJB", "wrkCrnChange4G", e.getMessage(), YdConstant.ERROR);
					
					throw e;
					
				}catch(Exception e){
					throw new DAOException(getClass().getName() + e.getMessage(),e);
					
				}
				
				szLogMsg = "JSP-SESSION [(작업)크레인 변경 ]끝";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
				
			 	
				return rtnJDTORecord ;
				
				
			}
			///////////////////////////////////////////////////////////////////////////////
			///                          전사물류개선 프로젝트 2021.1.6                  ///
			///////////////////////////////////////////////////////////////////////////////
} // End of Class



