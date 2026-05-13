package com.inisteel.cim.yd.jsp.coiljsp.session;


//UTIL IMPORT
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.log.Logger;
import jspeed.base.property.PropertyService;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;
import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.CmUtil;
import com.inisteel.cim.hr.common.util.CmnUtil;
import com.inisteel.cim.yd.ccommon.util.CCommUtils;
import com.inisteel.cim.yd.common.dao.YdPlateCommDAO;
import com.inisteel.cim.yd.common.dao.ydCarFtmvMtlDao.YdCarFtmvMtlDao;
import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.dao.ydCarSpecDao.YdCarSpecDao;
import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
import com.inisteel.cim.yd.common.dao.ydCrnWrkMtlDao.YdCrnWrkMtlDao;
import com.inisteel.cim.yd.common.dao.ydEqpDao.YdEqpDao;
import com.inisteel.cim.yd.common.dao.ydPrepMtlDao.YdPrepMtlDao;
import com.inisteel.cim.yd.common.dao.ydPrepSchDao.YdPrepSchDao;
import com.inisteel.cim.yd.common.dao.ydSchRuleDao.YdSchRuleDao;
import com.inisteel.cim.yd.common.dao.ydStkBedDao.YdStkBedDao;
import com.inisteel.cim.yd.common.dao.ydStkColDao.YdStkColDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.dao.ydStrGtrDao.YdStrGtrDao;
import com.inisteel.cim.yd.common.dao.ydTcarFtmvMtlDao.YdTcarFtmvMtlDao;
import com.inisteel.cim.yd.common.dao.ydTcarSchDao.YdTcarSchDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookDao.YdWrkbookDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookMtlDao.YdWrkbookMtlDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.rule.GetBreRule6;
import com.inisteel.cim.yd.common.util.DaoManager;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdSlabUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.loc.CoilYdToLocDcsnUtil;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO;
import com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao;
import com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDAO;
import com.inisteel.cim.yd.jsp.common.YDComScript;
import com.inisteel.cim.yd.jsp.common.YDComUtil;
import com.inisteel.cim.yd.jsp.common.YDDataUtil;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;
import com.inisteel.cim.yd.ydStock.StockSpecReg.CoilSpecRegSeEJBBean;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;
 
/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="CoilGdsJspSeEJB" jndi-name="CoilGdsJspSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */ 

public class CoilGdsJspSeEJBBean extends BaseSessionBean {
	
	private YdUtils ydUtils = new YdUtils();
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();
	private Logger logger = new Logger("yd");
	private String szSessionName = getClass().getName();
	
	private YdSlabUtils slabUtils = new YdSlabUtils();
	
	private YdDelegate ydDelegate =new YdDelegate();
	
	private YdPICommDAO	   		ydPICommDAO   = new YdPICommDAO();

	private CCommUtils commUtils = new CCommUtils();	
	
	 YDDataUtil  yddatautil = new YDDataUtil();
	 YDComScript ydScript   = null;

	
	/**
	 * ejbCrate()
	 * 
	 * @throws javax.ejb.CreateException
	 */ 
	public void ejbCreate() throws javax.ejb.CreateException {
	}
	
	
	/**
	 *  코일제품창고 작업실적일품 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilGdsYdWrkRsltDdArtcl(JDTORecord inDto) throws DAOException {
		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
				
		String szMsg        = "";		
		String szMethodName = "getCoilGdsYdWrkRsltDdArtcl";			
		int    intRtnVal    = 0;

		try {
			recPara.setField("V_YD_GP",             inDto.getField("YD_GP"));
			recPara.setField("V_DATE_FROM",         inDto.getField("DATE_FROM"));
			recPara.setField("V_DATE_TO",           inDto.getField("DATE_TO"));				
			recPara.setField("V_YD_WRK_DUTY",       inDto.getField("YD_WRK_DUTY"));
			recPara.setField("V_YD_AID_WRK_YN",     inDto.getField("YD_AID_WRK_YN"));
							
			recPara.setField("V_YD_GNT_GP",         inDto.getField("YD_GNT_GP"));
			recPara.setField("V_STL_PROG_CD",       inDto.getField("CURR_PROG_CD"));
			recPara.setField("V_PAGE_CNT",          inDto.getField("PAGE_NO"));			
			recPara.setField("V_ROW_CNT",           inDto.getField("ROWCOUNT"));
//!AT			
			recPara.setField("V_YD_BAY_GP",     	inDto.getField("YD_BAY_GP"));
			recPara.setField("V_YD_BAY_GP1",     	inDto.getField("FROM_YD_BAY_GP"));
			recPara.setField("V_YD_BAY_GP2",     	inDto.getField("TO_YD_BAY_GP"));
			
			CoilGdsJspDao dao = new CoilGdsJspDao();
			
			outRecSet = dao.getCoilGdsYdWrkRsltDdArtcl(recPara);
			
			
			
			if (outRecSet == null || outRecSet.size() == 0) {
				szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			} 

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} 
		
		return outRecSet;
	}//end of getCoilGdsYdWrkRsltDdArtcl
	
	/**
	 *  열별 저장위치 조회 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilGdsYdColStkPosList(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		
		String szMsg        = "";		
		String szMethodName = "getCoilGdsYdColStkPosList";

		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao(); 
		
		int intRtnVal = 0;
		
		try {
					
			recPara.setField("YD_STK_COL_GP",     yddatautil.setDataDefault(inDto.getField("YD_GP"),         "") 
									           +  yddatautil.setDataDefault(inDto.getField("YD_BAY_GP"),     "")
									           +  yddatautil.setDataDefault(inDto.getField("YD_EQP_GP"),     "")
									           +  yddatautil.setDataDefault(inDto.getField("YD_STK_COL_NO"), ""));
			recPara.setField("YD_STK_COL_NO",     yddatautil.setDataDefault(inDto.getField("YD_STK_COL_NO"), ""));
			recPara.setField("STL_NO",            yddatautil.setDataDefault(inDto.getField("STL_NO"), ""));
			
			recPara.setField("PAGE_CNT1", inDto.getField("PAGE_NO" ));
			recPara.setField("PAGE_CNT2", inDto.getField("PAGE_NO" ));
			recPara.setField("ROW_CNT1",  inDto.getField("ROWCOUNT"));
			recPara.setField("ROW_CNT2",  inDto.getField("ROWCOUNT"));
//getYdStklyr21			
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 21);
		
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
			
			logger.println(LogLevel.DEBUG_TEXT, "getCoilGdsYdColStkPosList");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilGdsYdColStkPosList
	
	/**
	 * 열별 저장위치 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updCoilGdsYdColStkPosList(JDTORecord[] inDto) throws DAOException {
		int           intRtnVal     = 0;
		String        szMsg         = "";
		String        szMethodName  = "updCoilGdsYdColStkPosList";
		JDTORecord    recPara       = JDTORecordFactory.getInstance().create();		
		
		String    colGp        = "";
		
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		
		try {
			
			//열별 저장위치 수정
			for(int x=0;x<inDto.length;x++){
			
			//수정항목 세팅 
			colGp = yddatautil.setDataDefault(inDto[x].getField("YD_GP"),            "")
		          + yddatautil.setDataDefault(inDto[x].getField("YD_BAY_GP"),        "")
 		          + yddatautil.setDataDefault(inDto[x].getField("YD_EQP_GP"),        "")
		          + yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_NO"),    "");
		      
		    recPara.setField("YD_STK_COL_GP", colGp);
		    recPara.setField("STL_NO",        yddatautil.setDataDefault(inDto[x].getField("STL_NO"),           ""));
		    recPara.setField("YD_STK_BED_NO", yddatautil.setDataDefault(inDto[x].getField("YD_STK_BED_NO"),    ""));
		    recPara.setField("YD_STK_LYR_NO", yddatautil.setDataDefault(inDto[x].getField("YD_STK_LYR_NO"),    ""));
//updYdStklyr0		    
		    intRtnVal = ydStkLyrDao.updYdStklyr(recPara, 0);
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
	}	// end of updCoilGdsYdColStkPosList
	
	/**
	 * 열별 저장위치 등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void insCoilGdsYdColStkPosList(JDTORecord[] inDto) throws DAOException {

		int           intRtnVal     = 0;
		String        szMsg         = "";
		String        szMethodName  = "insCoilGdsYdColStkPosList";
		JDTORecord    recPara       = JDTORecordFactory.getInstance().create();		
		String        szQuery       = "";

		String    colGp        = "";
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		try {
			
			//열별 저장위치 등록
			for(int x=0;x<inDto.length;x++){
				
				//등록항목 세팅 
				colGp = yddatautil.setDataDefault(inDto[x].getField("YD_GP" ),            "")
				      + yddatautil.setDataDefault(inDto[x].getField("YD_BAY_GP" ),        "")
		          	  + yddatautil.setDataDefault(inDto[x].getField("YD_EQP_GP" ),        "")
		              + yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_NO" ),    "");
				
			    recPara.setField("YD_STK_COL_GP", colGp);
			    recPara.setField("STL_NO",        yddatautil.setDataDefault(inDto[x].getField("STL_NO"),           ""));
			    recPara.setField("YD_STK_BED_NO", yddatautil.setDataDefault(inDto[x].getField("YD_STK_BED_NO"),    ""));
			    recPara.setField("YD_STK_LYR_NO", yddatautil.setDataDefault(inDto[x].getField("YD_STK_LYR_NO"),    ""));
						
				intRtnVal = ydStkLyrDao.insYdStklyr(recPara);
				
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
	}	// end of insCoilGdsYdColStkPosList
	

	/**
	 *  코일제품창고 일품별재고조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdDdArtclStkRef(JDTORecord inDto) throws DAOException {
		
		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
				
		String szMsg        = "";		
		String szMethodName = "getCoilYdDdArtclStkRef";			
		int    intRtnVal    = 0;
		
		try {
			recPara.setField("YD_GP" ,         	ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));
			recPara.setField("YD_STK_COL_GP", 	ydDaoUtils.paraRecChkNull(inDto, "YD_STK_COL_GP"));
			recPara.setField("STL_NO" ,        	ydDaoUtils.paraRecChkNull(inDto, "STL_NO"));
			recPara.setField("YD_COIL_GP" ,  	ydDaoUtils.paraRecChkNull(inDto, "YD_COIL_GP"));
			recPara.setField("ORDERLINE" ,  	ydDaoUtils.paraRecChkNull(inDto, "ORDERLINE"));
			recPara.setField("ORD_GP" ,  		ydDaoUtils.paraRecChkNull(inDto, "ORD_GP"));
			recPara.setField("RECEIPT_DATE" ,  	ydDaoUtils.paraRecChkNull(inDto, "RECEIPT_DATE"));
			recPara.setField("PROG_CD" ,  		ydDaoUtils.paraRecChkNull(inDto, "PROG_CD"));
			recPara.setField("WRAP_METHOD_GP" , ydDaoUtils.paraRecChkNull(inDto, "WRAP_METHOD_GP"));
			recPara.setField("PAGE_CNT",        inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT",         inDto.getField("ROWCOUNT"));
			
			YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
//getYdStklyr33
//PIDEV_S :병행가동용:PI_YD
			recPara.setField("PI_YD",   ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));					
			/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdDdArtclStkRef_PIDEV*/
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 33);

			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg,YdConstant.ERROR);
				}
				return outRecSet;
			} // end of if			
			
			outRecSet.first();
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {

		}
		
		return outRecSet;
	}//end of getCoilYdDdArtclStkRef
	
	/**
	 * 코일야드 크레인작업범위등록 조회 (야드/동/설비구분/설비번호를 받을때)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdCrnStsSet(JDTORecord inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg="";
		String szPara=null;
		YdEqpDao ydEqpDao = new YdEqpDao();		
	
		String szMethodName="getCoilYdCrnStsSet";
		String tempLog = null;
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		
		try {
			//저장위치 좌표설정화면 BED 수정
				tempLog = inDto.toString();
				System.out.println(tempLog);				
				
				szPara = inDto.getFieldString("YD_GP")
					   +  inDto.getFieldString("YD_BAY_GP")
					   +  inDto.getFieldString("YD_EQP_GP")				
					   +  inDto.getFieldString("YD_EQP_NO");
				
				//설비ID 
				recPara.setField("YD_EQP_ID", szPara);

//getYdEqp2				
				intRtnVal = ydEqpDao.getYdEqp(recPara, outRecSet, 2);
					
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
				
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		return outRecSet;
	}	// end of getCoilYdCrnStsSet
	
	
	/**
	 *  저장위치별 재고 List
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdStkPosList(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String      szMsg        = "";
		String      szMethodName = "getCoilYdStkPosList";
		String 	szOperationName = "저장위치별 재고 List";
		YdStkBedDao YdStkBedDap  = new YdStkBedDao();
		String szYdDongGp        = "";
		String szYdBedGp         = "";
		
		int intRtnVal = 0;
		
		try {
		
			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			recPara.setField("YD_GP",    		    ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));
		    recPara.setField("YD_BAY_GP",    		ydDaoUtils.paraRecChkNull(inDto, "YD_DONG_GP"));
		    recPara.setField("YD_EQP_GP", 		    ydDaoUtils.paraRecChkNull(inDto, "YD_SPAN_GP"));
		    recPara.setField("YD_STK_COL_NO", 		ydDaoUtils.paraRecChkNull(inDto, "YD_COL_GP"));
		    recPara.setField("YD_STK_BED_NO",    	ydDaoUtils.paraRecChkNull(inDto, "YD_BED_GP"));
			recPara.setField("PAGE_CNT",            inDto.getField("PAGE_NO"));		
			recPara.setField("ROW_CNT",             inDto.getField("ROWCOUNT"));

//getYdStkbed32
//PIDEV_S :병행가동용:PI_YD
			recPara.setField("PI_YD",    	 ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));	
			intRtnVal = YdStkBedDap.getYdStkbed(recPara, outRecSet, 32);
			
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
	}//end of getCoilYdStkPosList
	

	/**
	 * 코일야드 크레인작업범위등록 조회 (설비 ID 를 받는경우)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdCrnStsSetID(JDTORecord inDto) throws DAOException {
		int    intRtnVal    = 0;
		String szMsg        = "";
		String szMethodName = "getCoilYdCrnStsSetID";

		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		
		YdEqpDao ydEqpDao = new YdEqpDao();
		try {
		
				//설비ID 
				recPara.setField("YD_EQP_ID",yddatautil.setDataDefault(inDto.getField("YD_EQP_ID"), ""));

//getYdEqp6				
				intRtnVal = ydEqpDao.getYdEqp(recPara, outRecSet, 6);
					
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
				
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		return outRecSet;
	}	// end of getCoilYdCrnStsSetID
	

	/**
	 * 코일야드 크레인 상태 설정 (ON_LINE, OFF_LINE)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updCoilYdCrnStsSetCrnMode(JDTORecord[] inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg= "";
	
		String szMethodName="updCoilYdCrnStsSetCrnMode";
		String tempLog = null;
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		YdEqpDao ydEqpDao = new YdEqpDao();		
		
		try {
			//저장위치 좌표설정화면 BED 수정
			for(int x=0;x<inDto.length;x++){
				tempLog = inDto[x].toString();
				System.out.println(tempLog);				
								
				//입출입  상태
				recPara.setField("YD_EQP_ID", yddatautil.setDataDefault(inDto[x].getField("YD_EQP_ID"), ""));
				recPara.setField("YD_EQP_WRK_MODE", yddatautil.setDataDefault(inDto[x].getField("YD_EQP_WRK_MODE"), ""));
				
//updYdEqp0				
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
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}	// end of updCoilYdCrnStsSetCrnMode
	
	
	/**
	 * 코일야드 크레인 상태 설정(고장/정상)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updCoilYdCrnStsSetCrnStat(JDTORecord[] inDto) throws DAOException {
		int    intRtnVal = 0;
		String szMsg     = "";
	
		String     szMethodName = "updCoilYdCrnStsSetCrnStat";
		String     tempLog      = null;
		JDTORecord recPara      = JDTORecordFactory.getInstance().create();
		YdEqpDao   ydEqpDao     = new YdEqpDao();		
		
		try {
			//저장위치 좌표설정화면 BED 수정
			for(int x=0;x<inDto.length;x++){
				tempLog = inDto[x].toString();
				System.out.println(tempLog);				
								
				//입출입  상태
				recPara.setField("YD_EQP_ID",   yddatautil.setDataDefault(inDto[x].getField("YD_EQP_ID"), ""));
				recPara.setField("YD_EQP_STAT", yddatautil.setDataDefault(inDto[x].getField("YD_EQP_STAT"), ""));
//updYdEqp0				
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
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}	// end of updCoilYdCrnStsSetCrnStat
	
	
	
	/**
	 * !A 이적작업진행 관리 > 동별 이적목록 조회 (로딩시 조회)
     * 작성자 : 박지열
     * 작성일 : 2010/03/19
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws xception
	 */
	public JDTORecordSet getCoilYdGdsMvWorkDongList(JDTORecord inDto) throws DAOException {
		int    intRtnVal    = 0;
		String szMsg        = "";
		String szMethodName = "getCoilYdGdsMvWorkDongList";

		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		YdPrepSchDao ydPrepSchDao = new YdPrepSchDao();
		
		try {
				//조회조건
				//없음
				//!A  szQueryIdGet400
				intRtnVal = ydPrepSchDao.getYdPrepsch(recPara, outRecSet, 400);
				
//getYdPrepsch2
				//intRtnVal = ydPrepSchDao.getYdPrepsch(recPara, outRecSet, 2);
				
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
				
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}	
	
	/**
	 * 이적작업진행 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdGdsMvWorkList(JDTORecord inDto) throws DAOException {
		int    intRtnVal    = 0;
		String szMsg        = "";
		String szMethodName = "getCoilYdGdsMvWorkList";

		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");

		YdPrepSchDao ydPrepSchDao = new YdPrepSchDao();

		try {
			//조회조건
			recPara.setField("YD_GP",          yddatautil.setDataDefault(inDto.getField("YD_GP"),         ""));
			recPara.setField("YD_BAY_GP",      yddatautil.setDataDefault(inDto.getField("YD_BAY_GP"),     ""));
			recPara.setField("YD_EQP_GP",      yddatautil.setDataDefault(inDto.getField("YD_EQP_GP"),     ""));
			recPara.setField("YD_STK_COL_NO",  yddatautil.setDataDefault(inDto.getField("YD_STK_COL_NO"), ""));
			recPara.setField("YD_STK_BED_NO",  yddatautil.setDataDefault(inDto.getField("YD_STK_BED_NO"), ""));
			recPara.setField("PAGE_CNT1",      inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT1",       inDto.getField("ROWCOUNT"));
			recPara.setField("PAGE_CNT2",      inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT2",       inDto.getField("ROWCOUNT"));

			//!A szQueryIdGet3
			intRtnVal = ydPrepSchDao.getYdPrepsch(recPara, outRecSet, 2);

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

			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}	// end of getCoilYdGdsMvWorkList
	
	/**
	 * 이적작업진행 상세조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdGdsMvWorkDtlList(JDTORecord inDto) throws DAOException {
		int    intRtnVal    = 0;
		String szMsg        = "";
		String szMethodName = "getCoilYdGdsMvWorkDtlList";

		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		YdPrepSchDao ydPrepSchDao = new YdPrepSchDao();
		
		try {
			//조회조건
			recPara.setField("YD_GP",          yddatautil.setDataDefault(inDto.getField("YD_GP"),         ""));
			recPara.setField("YD_BAY_GP",      yddatautil.setDataDefault(inDto.getField("YD_BAY_GP"),     ""));
			recPara.setField("YD_EQP_GP",      yddatautil.setDataDefault(inDto.getField("YD_EQP_GP"),     ""));
			recPara.setField("YD_STK_COL_NO",  yddatautil.setDataDefault(inDto.getField("YD_STK_COL_NO"), ""));
			recPara.setField("YD_STK_BED_NO",  yddatautil.setDataDefault(inDto.getField("YD_STK_BED_NO"), ""));
			recPara.setField("PAGE_CNT1",      inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT1",       inDto.getField("ROWCOUNT"));
			recPara.setField("PAGE_CNT2",      inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT2",       inDto.getField("ROWCOUNT"));
			

			if("Y".equals(inDto.getFieldString("EQUAL_GP"))) // 동내이적
				//!A szQueryIdGet401
				intRtnVal = ydPrepSchDao.getYdPrepsch(recPara, outRecSet, 401);
			else if("N".equals(inDto.getFieldString("EQUAL_GP"))) // 동간이적
				//!A szQueryIdGet402
				intRtnVal = ydPrepSchDao.getYdPrepsch(recPara, outRecSet, 402);
			
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
			
			ydUtils.putLog(szSessionName, szMethodName, szMsg,  YdConstant.DEBUG);
		
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}	// end of getCoilYdGdsMvWorkDtlList
	
	/**
	 * 이적작업진행관리 대상재 조회 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdGdsStockList(JDTORecord inDto) throws DAOException {
		int    intRtnVal    = 0;
		String szMsg        = "";
		String szMethodName = "getCoilYdGdsStockList";

		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		
		try {
				//조회조건
				recPara.setField("YD_GP",          yddatautil.setDataDefault(inDto.getField("YD_GP"),         ""));
				recPara.setField("YD_BAY_GP",      yddatautil.setDataDefault(inDto.getField("YD_BAY_GP"),     ""));
				recPara.setField("YD_EQP_GP",      yddatautil.setDataDefault(inDto.getField("YD_EQP_GP"),     ""));
				recPara.setField("YD_STK_COL_NO",  yddatautil.setDataDefault(inDto.getField("YD_STK_COL_NO"), ""));
				recPara.setField("YD_STK_BED_NO",  yddatautil.setDataDefault(inDto.getField("YD_STK_BED_NO"), ""));
				recPara.setField("PAGE_CNT1",      inDto.getField("PAGE_NO"));
				recPara.setField("PAGE_CNT2",      inDto.getField("PAGE_NO"));
				recPara.setField("ROW_CNT1",       inDto.getField("ROWCOUNT"));
				recPara.setField("ROW_CNT2",       inDto.getField("ROWCOUNT"));

//getYdStklyr40				
				intRtnVal  = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 40);
				
				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!	!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					//return outRecSet;
					return outRecSet;
				} // end of if
				
				ydUtils.putLog(szSessionName, szMethodName, szMsg,  YdConstant.DEBUG);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}	// end of getCoilYdGdsMvWorkDtlList
	
	/**
	 * 이적작업진행관리 예약위치 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updCoilYdToLocGuide(JDTORecord[] inDto) throws DAOException {
		int           intRtnVal    = 0;
		String        szMsg        = "";
		String        szMethodName = "updCoilYdToLocGuide";
		JDTORecord    recPara      = JDTORecordFactory.getInstance().create();		
		
		YdPrepSchDao ydPrepSchDao = new YdPrepSchDao();
		
		try {
			for(int x=0;x<inDto.length;x++){
				
			    recPara.setField("YD_PREP_SCH_ID",  yddatautil.setDataDefault(inDto[x].getField("YD_PREP_SCH_ID"),   ""));
			    recPara.setField("YD_TO_LOC_GUIDE", yddatautil.setDataDefault(inDto[x].getField("YD_TO_LOC_GUIDE"),  ""));
			    recPara.setField("YD_STK_COL_GP",   yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_GP"),    ""));
			    recPara.setField("YD_STK_BED_NO",   yddatautil.setDataDefault(inDto[x].getField("YD_STK_BED_NO"),    ""));
			    
			    System.out.println("recPara:::::======="+recPara);
			    intRtnVal = ydPrepSchDao.updYdPrepsch(recPara, 0);
			    
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
	}	// end of updCoilGdsYdColStkPosList
	
	/**
	 * 코일제품창고 군,열 상태별 재고 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilGdsYdLineSvLocMgt(JDTORecord inDto) throws DAOException {
		int    intRtnVal    = 0;
		String szMsg        = "";
		String szMethodName = "getCoilGdsYdLineSvLocMgt";

		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		YdStkColDao ydStkColDao = new YdStkColDao();
		
		try {
				//조회조건
				recPara.setField("YD_GP",          yddatautil.setDataDefault(inDto.getField("YD_GP"),         ""));
				recPara.setField("YD_BAY_GP",      yddatautil.setDataDefault(inDto.getField("YD_BAY_GP"),     ""));
				recPara.setField("YD_EQP_GP",      yddatautil.setDataDefault(inDto.getField("YD_EQP_GP"),     ""));
				
				recPara.setField("YD_COIL_OUTDIA_GRP_GP",      yddatautil.setDataDefault(inDto.getField("YD_COIL_OUTDIA_GRP_GP"),     ""));
				recPara.setField("YD_STK_COL_W_GP",      yddatautil.setDataDefault(inDto.getField("YD_STK_COL_W_GP"),     ""));

//getYdStkcol9
				intRtnVal  = ydStkColDao.getYdStkcol(recPara, outRecSet, 9);
				
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
				
				ydUtils.putLog(szSessionName, szMethodName, szMsg,  YdConstant.DEBUG);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}	// end of getCoilGdsYdLineSvLocMgt
	
	/**
	 * 코일제품창고 군,열 상태 및 SPAN별 재고 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilGdsYdLineSvLocMgtSpan(JDTORecord inDto) throws DAOException {
		int    intRtnVal    = 0;
		String szMsg        = "";
		String szMethodName = "getCoilGdsYdLineSvLocMgtSpan";

		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		YdStkColDao ydStkColDao = new YdStkColDao();
		
		try {
				//조회조건
				recPara.setField("YD_GP",          yddatautil.setDataDefault(inDto.getField("YD_GP"),         ""));
				recPara.setField("YD_BAY_GP",      yddatautil.setDataDefault(inDto.getField("YD_BAY_GP"),     ""));
				recPara.setField("YD_EQP_GP",      yddatautil.setDataDefault(inDto.getField("YD_EQP_GP"),     ""));
				//!A 열정보 조회 조건 추가 (박지열 추가 - 2010/03/23 )
				recPara.setField("YD_STK_COL_NO",  yddatautil.setDataDefault(inDto.getField("YD_STK_COL_NO"),     ""));
				
				recPara.setField("YD_COIL_OUTDIA_GRP_GP", ydDaoUtils.paraRecChkNull(inDto, "YD_COIL_OUTDIA_GRP_GP"));
				recPara.setField("YD_STK_COL_W_GP", ydDaoUtils.paraRecChkNull(inDto, "YD_STK_COL_W_GP"));
				
				
				//recPara.setField("YD_STK_COL_NO",  yddatautil.setDataDefault(inDto.getField("YD_STK_COL_NO"), ""));
//getYdStkcol10				
				intRtnVal  = ydStkColDao.getYdStkcol(recPara, outRecSet, 10);
				
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
				
				ydUtils.putLog(szSessionName, szMethodName, szMsg,  YdConstant.DEBUG);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}	// end of getCoilGdsYdLineSvLocMgtSpan
	
	
	/**
	 * 적치열정보 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilGdsYdStkCol(JDTORecord inDto) throws DAOException {
		int    intRtnVal    = 0;
		String szMsg        = "";
		String szMethodName = "getCoilGdsYdStkCol";

		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		YdStkColDao ydStkColDao = new YdStkColDao();
		
		try {
				//조회조건
				recPara.setField("YD_GP",          yddatautil.setDataDefault(inDto.getField("YD_GP"),         ""));
				recPara.setField("YD_BAY_GP",      yddatautil.setDataDefault(inDto.getField("YD_BAY_GP"),     ""));
				recPara.setField("YD_EQP_GP",      yddatautil.setDataDefault(inDto.getField("YD_EQP_GP"),     ""));
				recPara.setField("YD_STK_COL_NO",  yddatautil.setDataDefault(inDto.getField("YD_STK_COL_NO"), ""));
				
				/*
				recPara.setField("PAGE_CNT1",      inDto.getField("PAGE_NO"));
				recPara.setField("PAGE_CNT2",      inDto.getField("PAGE_NO"));
				recPara.setField("ROW_CNT1",       inDto.getField("ROWCOUNT"));
				recPara.setField("ROW_CNT2",       inDto.getField("ROWCOUNT"));
				*/

//getYdStkcol2				
				intRtnVal  = ydStkColDao.getYdStkcol(recPara, outRecSet, 2);
				
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
				
				ydUtils.putLog(szSessionName, szMethodName, szMsg,  YdConstant.DEBUG);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}	// end of getCoilGdsYdStkCol
	
	/**
	 * 적치Bed정보 조회 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilGdsYdStkBed(JDTORecord inDto) throws DAOException {
		int    intRtnVal    = 0;
		String szMsg        = "";
		String szMethodName = "getCoilGdsYdStkBed";

		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		YdStkBedDao ydStkBedDao = new YdStkBedDao();
		
		try {
				//조회조건
				recPara.setField("YD_STK_COL_GP",     yddatautil.setDataDefault(inDto.getField("YD_STK_COL_GP"),         ""));
				
//getYdStkbed1				
				intRtnVal  = ydStkBedDao.getYdStkbed(recPara, outRecSet, 1);
				
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
				
				ydUtils.putLog(szSessionName, szMethodName, szMsg,  YdConstant.DEBUG);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}	// end of getCoilGdsYdStkBed
	
	/**
	 * 저장집합 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilGdsYdStrGtr(JDTORecord inDto) throws DAOException {
		int    intRtnVal    = 0;
		String szMsg        = "";
		String szMethodName = "getCoilGdsYdStrGtr";

		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		YdStrGtrDao ydStrGtrDao = new YdStrGtrDao();
		
		try {
				//조회조건
				recPara.setField("YD_GP",          yddatautil.setDataDefault(inDto.getField("YD_GP"),         ""));
				recPara.setField("YD_BAY_GP",      yddatautil.setDataDefault(inDto.getField("YD_BAY_GP"),     ""));

//getYdStrgtr1				
				intRtnVal  = ydStrGtrDao.getYdStrgtr(recPara, outRecSet, 1);
				
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
				
				ydUtils.putLog(szSessionName, szMethodName, szMsg,  YdConstant.DEBUG);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}	// end of getCoilGdsYdStrGtr
	
	
	/**
	 * 적치열 정보 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updCoilGdsYdStkCol(JDTORecord[] inDto) throws DAOException {
		int           intRtnVal     = 0;
		String        szMsg         = "";
		String        szMethodName  = "updCoilGdsYdStkCol";
		JDTORecord    recPara       = JDTORecordFactory.getInstance().create();		
		
		YdStkColDao ydStkColDao = new YdStkColDao();
		
		try {
			
			//적치열정보  수정
			for(int x=0;x<inDto.length;x++){
				
				recPara.setField("YD_STK_COL_GP"              ,yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_GP"        ),    ""));
				recPara.setField("YD_GP"                      ,yddatautil.setDataDefault(inDto[x].getField("YD_GP"                ),    ""));
				recPara.setField("YD_BAY_GP"                  ,yddatautil.setDataDefault(inDto[x].getField("YD_BAY_GP"            ),    ""));
				recPara.setField("YD_EQP_GP"                  ,yddatautil.setDataDefault(inDto[x].getField("YD_EQP_GP"            ),    ""));
				recPara.setField("YD_STK_COL_NO"              ,yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_NO"        ),    ""));
				recPara.setField("YD_STK_COL_ACT_STAT"        ,yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_ACT_STAT"  ),    ""));
				recPara.setField("YD_STK_COL_RULE_XAXIS"      ,yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_RULE_XAXIS"),    ""));
				recPara.setField("YD_STK_COL_RULE_YAXIS"      ,yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_RULE_YAXIS"),    ""));
				recPara.setField("YD_STK_COL_W"               ,yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_W"         ),    ""));
				recPara.setField("YD_STK_COL_L"               ,yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_L"         ),    ""));
				recPara.setField("YD_CAR_USE_GP"              ,yddatautil.setDataDefault(inDto[x].getField("YD_CAR_USE_GP"        ),    ""));
				recPara.setField("TRN_EQP_CD"                 ,yddatautil.setDataDefault(inDto[x].getField("TRN_EQP_CD"           ),    ""));
				recPara.setField("CAR_NO"                     ,yddatautil.setDataDefault(inDto[x].getField("CAR_NO"               ),    ""));
				recPara.setField("CARD_NO"                    ,yddatautil.setDataDefault(inDto[x].getField("CARD_NO"              ),    ""));
				recPara.setField("WLOC_CD"                    ,yddatautil.setDataDefault(inDto[x].getField("WLOC_CD"              ),    ""));
				recPara.setField("YD_PNT_CD"                  ,yddatautil.setDataDefault(inDto[x].getField("YD_PNT_CD"            ),    ""));
				recPara.setField("YD_STK_COL_W_GP"            ,yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_W_GP"      ),    ""));
				recPara.setField("YD_STK_COL_H_MAX"           ,yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_H_MAX"     ),    ""));
				recPara.setField("YD_STK_COL_BED_L_TP"        ,yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_BED_L_TP"  ),    ""));
				recPara.setField("YD_COIL_OUTDIA_GRP_GP"      ,yddatautil.setDataDefault(inDto[x].getField("YD_COIL_OUTDIA_GRP_GP"),    ""));
				
			    intRtnVal = ydStkColDao.updYdStkcol(recPara, 0);
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
	}	// end of updCoilGdsYdStkCol
	
	/**
	 * 적치베드 정보 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updCoilGdsYdStkBed(JDTORecord[] inDto) throws DAOException {
		int           intRtnVal     = 0;
		String        szMsg         = "";
		String        szMethodName  = "updCoilGdsYdStkBed";
		JDTORecord    recPara       = JDTORecordFactory.getInstance().create();		
		
		YdStkBedDao ydStkBedDao = new YdStkBedDao();
		
		try {
			
			//열별 저장위치 수정
			for(int x=0;x<inDto.length;x++){
				
				//수정항목 세팅 
				
				recPara.setField("YD_STK_COL_GP"        	, inDto[x].getField("YD_STK_COL_GP"        ));
				recPara.setField("YD_STK_BED_NO"        	, inDto[x].getField("YD_STK_BED_NO"        ));
				recPara.setField("YD_STR_GTR_CD"        	, inDto[x].getField("YD_STR_GTR_CD"        ));
				recPara.setField("YD_STK_BED_TP"        	, inDto[x].getField("YD_STK_BED_TP"        ));
				recPara.setField("YD_STK_BED_L_GP"      	, inDto[x].getField("YD_STK_BED_L_GP"      ));
				recPara.setField("YD_STK_BED_W_GP"      	, inDto[x].getField("YD_STK_BED_W_GP"      ));
				recPara.setField("YD_STK_BED_DIR_GP"    	, inDto[x].getField("YD_STK_BED_DIR_GP"    ));
				recPara.setField("YD_STK_BED_ACT_STAT"  	, inDto[x].getField("YD_STK_BED_ACT_STAT"  ));
				recPara.setField("YD_STK_BED_WHIO_STAT" 	, inDto[x].getField("YD_STK_BED_WHIO_STAT" ));
				recPara.setField("YD_STK_BED_USG_GP"    	, inDto[x].getField("YD_STK_BED_USG_GP"    ));
				recPara.setField("YD_STK_BED_XAXIS"     	, inDto[x].getField("YD_STK_BED_XAXIS"     ));
				recPara.setField("YD_STK_BED_YAXIS"     	, inDto[x].getField("YD_STK_BED_YAXIS"     ));
				recPara.setField("YD_STK_BED_ZAXIS"     	, inDto[x].getField("YD_STK_BED_ZAXIS"     ));
				recPara.setField("YD_STK_BED_LYR_MAX"   	, inDto[x].getField("YD_STK_BED_LYR_MAX"   ));
				recPara.setField("YD_STK_BED_WT_MAX"    	, inDto[x].getField("YD_STK_BED_WT_MAX"    ));
				recPara.setField("YD_STK_BED_H_MAX"     	, inDto[x].getField("YD_STK_BED_H_MAX"     ));
				recPara.setField("YD_STK_BED_L_MAX"     	, inDto[x].getField("YD_STK_BED_L_MAX"     ));
				recPara.setField("YD_STK_BED_W_MAX"     	, inDto[x].getField("YD_STK_BED_W_MAX"     ));
				recPara.setField("YD_STK_BED_XAXIS_TOL" 	, inDto[x].getField("YD_STK_BED_XAXIS_TOL" ));
				recPara.setField("YD_STK_BED_YAXIS_TOL" 	, inDto[x].getField("YD_STK_BED_YAXIS_TOL" ));
				recPara.setField("YD_L_S_GRP_GP"        	, inDto[x].getField("YD_L_S_GRP_GP"        ));
				recPara.setField("YD_COIL_OUTDIA_GRP_GP"	, inDto[x].getField("YD_COIL_OUTDIA_GRP_GP"));
		    
		    intRtnVal = ydStkBedDao.updYdStkbed(recPara, 0);
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
	}	// end of updCoilGdsYdStkBed
	
	/**
	 * 저장집합 정보 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updCoilGdsYdStrGtr(JDTORecord[] inDto) throws DAOException {
		int           intRtnVal     = 0;
		String        szMsg         = "";
		String        szMethodName  = "updCoilGdsYdStrGtr";
		JDTORecord    recPara       = JDTORecordFactory.getInstance().create();		
		String        szQuery       = "";
		
		String    colGp        = "";
		String    bedNo        = "";
		String    lyrNo        = "";
		String    stlNo        = "";
		
		YdStrGtrDao ydStrGtrDao = new YdStrGtrDao();
		
		try {
			
			for(int x=0;x<inDto.length;x++){
		      
				recPara.setField("YD_STR_GTR_CD"		, yddatautil.setDataDefault(inDto[x].getField("YD_STR_GTR_CD"	  ),  ""));
				recPara.setField("YD_STR_GTR_NM"		, yddatautil.setDataDefault(inDto[x].getField("YD_STR_GTR_NM"	  ),  ""));
				recPara.setField("YD_STRCHAR_GRP_CD"	, yddatautil.setDataDefault(inDto[x].getField("YD_STRCHAR_GRP_CD" ),  ""));
				recPara.setField("YD_GP"			    , yddatautil.setDataDefault(inDto[x].getField("YD_GP"			  ),  ""));
				recPara.setField("YD_BAY_GP"			, yddatautil.setDataDefault(inDto[x].getField("YD_BAY_GP"		  ),  ""));
				recPara.setField("YD_SCH_RNG_CD"		, yddatautil.setDataDefault(inDto[x].getField("YD_SCH_RNG_CD"	  ),  ""));
				recPara.setField("YD_MTL_ITEM"			, yddatautil.setDataDefault(inDto[x].getField("YD_MTL_ITEM"		  ),  ""));
				recPara.setField("YD_AIM_RT_GP"			, yddatautil.setDataDefault(inDto[x].getField("YD_AIM_RT_GP"	  ),  ""));
				recPara.setField("YD_EQP_GP"			, yddatautil.setDataDefault(inDto[x].getField("YD_EQP_GP"		  ),  ""));
				recPara.setField("YD_BOOK_OUT_LOC"		, yddatautil.setDataDefault(inDto[x].getField("YD_BOOK_OUT_LOC"	  ),  ""));
				recPara.setField("YD_MTL_W_GP"			, yddatautil.setDataDefault(inDto[x].getField("YD_MTL_W_GP"		  ),  ""));
				recPara.setField("YD_MTL_L_GP"			, yddatautil.setDataDefault(inDto[x].getField("YD_MTL_L_GP"		  ),  ""));
				recPara.setField("YD_BAY_SRCH_PRIOR"	, yddatautil.setDataDefault(inDto[x].getField("YD_BAY_SRCH_PRIOR" ),  ""));
			    
			    intRtnVal = ydStrGtrDao.updYdStrgtr(recPara, 0);
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
	}	// end of updCoilGdsYdStrGtr
	
	/**
	 *  열별 저장위치 조회 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdColStkPosList(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		
		String szMsg        = "";		
		String szMethodName = "getCoilYdColStkPosList";
		String szOperationName = "열별 저장위치 조회";

		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao(); 
		
		int intRtnVal = 0;
		
		try {
				
			
			 recPara         = JDTORecordFactory.getInstance().create();
			  
			recPara.setField("YD_STK_COL_GP",     yddatautil.setDataDefault(inDto.getField("YD_GP"),         "") 
									           +  yddatautil.setDataDefault(inDto.getField("YD_BAY_GP"),     "")
									           +  yddatautil.setDataDefault(inDto.getField("YD_EQP_GP"),     "")
									           +  yddatautil.setDataDefault(inDto.getField("YD_STK_COL_NO"), ""));
			recPara.setField("YD_STK_COL_NO",     yddatautil.setDataDefault(inDto.getField("YD_STK_COL_NO"), ""));
			
			
			/*
			 * recPara.setField("STL_NO",            yddatautil.setDataDefault(inDto.getField("STL_NO"), ""));
			recPara.setField("PAGE_CNT1", inDto.getField("PAGE_NO" ));
			recPara.setField("PAGE_CNT2", inDto.getField("PAGE_NO" ));
			recPara.setField("ROW_CNT1",  inDto.getField("ROWCOUNT"));
			recPara.setField("ROW_CNT2",  inDto.getField("ROWCOUNT"));
			
			*/
			
			ydUtils.displayRecord(szOperationName, recPara);
//getYdStklyr61			
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 61);
		
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
			
			logger.println(LogLevel.DEBUG_TEXT, "getCoilYdColStkPosList");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilYdColStkPosList
	
	/**
	 *  SPAN별 저장위치 조회 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdSpanStkPosList(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		
		String szMsg        = "";		
		String szMethodName = "getCoilYdSpanStkPosList";

		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao(); 
		
		int intRtnVal = 0;
		
		try {
			recPara.setField("YD_GP",         yddatautil.setDataDefault(inDto.getField("YD_GP"), ""));
			recPara.setField("YD_BAY_GP",     yddatautil.setDataDefault(inDto.getField("YD_BAY_GP"), ""));
			recPara.setField("YD_EQP_GP",     yddatautil.setDataDefault(inDto.getField("YD_EQP_GP"), ""));
			recPara.setField("YD_STK_COL_NO", yddatautil.setDataDefault(inDto.getField("YD_STK_COL_NO"), ""));
			
			recPara.setField("PAGE_CNT1", inDto.getField("PAGE_NO" ));
			recPara.setField("PAGE_CNT2", inDto.getField("PAGE_NO" ));
			recPara.setField("ROW_CNT1",  inDto.getField("ROWCOUNT"));
			recPara.setField("ROW_CNT2",  inDto.getField("ROWCOUNT"));

//getYdStklyr42			
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 42);
		
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
			
			logger.println(LogLevel.DEBUG_TEXT, "getCoilYdSpanStkPosList");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilYdSpanStkPosList
	
	/**
	 * Span별 저장위치 정보 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updCoilYdSpanStkPosList(JDTORecord[] inDto) throws DAOException {
		int           intRtnVal     = 0;
		String        szMsg         = "";
		String        szMethodName  = "updCoilYdSpanStkPosList";
		JDTORecord    recPara       = JDTORecordFactory.getInstance().create();		
		
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		
		try {
			
			//열별 저장위치 수정
			for(int x=0;x<inDto.length;x++){
				//수정항목 세팅 
			    recPara.setField("YD_STK_COL_GP", yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_GP"),    ""));
			    recPara.setField("STL_NO",        yddatautil.setDataDefault(inDto[x].getField("STL_NO"),           ""));
			    recPara.setField("YD_STK_BED_NO", yddatautil.setDataDefault(inDto[x].getField("YD_STK_BED_NO"),    ""));
			    recPara.setField("YD_STK_LYR_NO", yddatautil.setDataDefault(inDto[x].getField("YD_STK_LYR_NO"),    ""));
			    
			    intRtnVal = ydStkLyrDao.updYdStklyr(recPara, 0);
				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} //return outRecSet
				} //end of if
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}	// end of updCoilYdSpanStkPosList
	
	
	/**
	 *  작업실적량 정보 조회 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdWrkRsltQty(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getCoilYdWrkRsltQty";

		YdCrnSchDao ydCrnSchDao = new YdCrnSchDao(); 
		
		int intRtnVal = 0;
		
		try {
			recPara.setField("YD_GP",           yddatautil.setDataDefault(inDto.getField("YD_GP"),         ""));
			recPara.setField("YD_WRK_HDS_DD",   yddatautil.setDataDefault(inDto.getField("YD_WRK_HDS_DD"), ""));
			recPara.setField("YD_WRK_DUTY",     yddatautil.setDataDefault(inDto.getField("YD_WRK_DUTY"),   ""));
			recPara.setField("YD_SCH_CD",       yddatautil.setDataDefault(inDto.getField("YD_SCH_CD"),     ""));

//getYdCrnsch25			
			intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, outRecSet, 25);
		
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
			
			logger.println(LogLevel.DEBUG_TEXT, "getCoilYdWrkRsltQty");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilYdWrkRsltQty
	
	
	/**
	 *  코일제품야드 스케줄 기동관리 (조회)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdSchStirMgt(JDTORecord inDto) throws DAOException {
		
		//Log Message 용 
		String szMsg="";		
		String szEdit =null;
		String szMethodName="getCoilYdSchStirMgt";	
		
		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecord    recEdit   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecordSet retRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
				
		YdSchRuleDao ydSchruleDao = new YdSchRuleDao();
		
		int intRtnVal = 0;
		
		try {
			
			//적치열 구분과 적치베드 번호로 분리 
			
			recPara.setField("YD_GP",     inDto.getField("YD_GP"));
			recPara.setField("YD_SCH_CD", inDto.getField("YD_SCH_CD"));
			recPara.setField("YD_BAY_GP", inDto.getField("YD_BAY_GP"));
			
			recPara.setField("PAGE_CNT1", inDto.getField("PAGE_NO"));
			recPara.setField("PAGE_CNT2", inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT1",  inDto.getField("ROWCOUNT"));
			recPara.setField("ROW_CNT2",  inDto.getField("ROWCOUNT"));

//getYdSchrule1			
			intRtnVal = ydSchruleDao.getYdSchrule(recPara, outRecSet, 1);
	
			
			if (intRtnVal <= 0) {
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
			do {
				//스케줄 코드를  설비구분을 분리하여 YD_EQP_GP 컬럼에 넣어준다.
				recEdit = outRecSet.getRecord();
				szEdit = recEdit.getFieldString("YD_SCH_CD").trim().substring(4, 6);
				recEdit.setField("YD_EQP_GP", szEdit);
				
				retRecSet.addRecord(recEdit);
				
			}while(outRecSet.next());
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return retRecSet;
	}//end of getCoilYdSchStirMgt
	
	
	/**
	 * 코일제품야드 스케줄 기동관리 (수정)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updCoilYdSchStirMgt(JDTORecord[] inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg= null;
		String szMethodName = null;
		String tempLog = null;
		
		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();		
		YdSchRuleDao ydSchRuleDao = new YdSchRuleDao();
		
		szMsg        = "";
		szMethodName = "updCoilYdSchStirMgt";
		
		try {
			//저장위치 좌표설정화면 BED 수정
			for(int x=0;x<inDto.length;x++){
				tempLog = inDto[x].toString();
				
				//스케줄 코드
				recPara.setField("YD_SCH_CD",            yddatautil.setDataDefault(inDto[x].getField("YD_SCH_CD"), ""));
				//기준활성상태
				//recPara.setField("YD_SCH_RULE_ACT_STAT", yddatautil.setDataDefault(inDto[x].getField("YD_SCH_RULE_ACT_STAT"), ""));
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
	}	// end of updCoilYdSchStirMgt
	
	/**
	 *  코일제품야드 동수주별재고조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdBayOrdInv1(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getCoilYdBayOrdInv1";

		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		
		int intRtnVal = 0;
		
		try {
			recPara.setField("YD_GP",         yddatautil.setDataDefault(inDto.getField("YD_GP"),       ""));
			//!A 조회조건 변경(재료구분-> 수주별 , 품명-> 고객사별)
			//!A 박지열 - 2010/03/23
			recPara.setField("ORD_GP",   yddatautil.setDataDefault(inDto.getField("ORD_GP"), ""));
			recPara.setField("CUST_CD",   yddatautil.setDataDefault(inDto.getField("CUST_CD"), ""));

//getYdStklyr44			
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 44);
		
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
			
			logger.println(LogLevel.DEBUG_TEXT, "getCoilYdBayOrdInv1");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilYdBayOrdInv1
	
	/**
	 *  코일제품야드 동수주별재고조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdBayOrdInv2(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getCoilYdBayOrdInv2";

		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		
		int intRtnVal = 0;
		
		try {
			recPara.setField("YD_GP",         yddatautil.setDataDefault(inDto.getField("YD_GP"),       ""));
			recPara.setField("YD_MTL_ITEM",   yddatautil.setDataDefault(inDto.getField("YD_MTL_ITEM"), ""));
			recPara.setField("ITEMNAME_CD",   yddatautil.setDataDefault(inDto.getField("ITEMNAME_CD"), ""));

//getYdStklyr45			
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 45);
		
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
			
			logger.println(LogLevel.DEBUG_TEXT, "getCoilYdBayOrdInv2");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilYdBayOrdInv2
	
	
	
	/**
	 *  코일제품야드 열적치용도 비율 및 예상적치 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdColStkUsageRtoExpStk(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getCoilYdColStkUsageRtoExpStk";

		YdStkBedDao ydStkBedDao = new YdStkBedDao();
		
		int intRtnVal = 0;
		
		try {
			recPara.setField("YD_GP1",       yddatautil.setDataDefault(inDto.getField("YD_GP"),     ""));
			recPara.setField("YD_BAY_GP1",   yddatautil.setDataDefault(inDto.getField("YD_BAY_GP"), ""));
//			recPara.setField("YD_GP2",       yddatautil.setDataDefault(inDto.getField("YD_GP"),     ""));
//			recPara.setField("YD_BAY_GP2",   yddatautil.setDataDefault(inDto.getField("YD_BAY_GP"), ""));
//			recPara.setField("YD_GP3",       yddatautil.setDataDefault(inDto.getField("YD_GP"),     ""));
//			recPara.setField("YD_BAY_GP3",   yddatautil.setDataDefault(inDto.getField("YD_BAY_GP"), ""));

//getYdStkbed16			
			intRtnVal = ydStkBedDao.getYdStkbed(recPara, outRecSet, 16);
		
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
			
			logger.println(LogLevel.DEBUG_TEXT, "getCoilYdColStkUsageRtoExpStk");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilYdColStkUsageRtoExpStk
	
	/**
	 *  코일제품야드 입고진행 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdInPlan(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getCoilYdInPlan";
		String sBranchCd    = "";
		YdStkBedDao ydStkBedDao = new YdStkBedDao();
		
		int intRtnVal = 0;
		
		try {
			recPara.setField("YD_GP",           "H");
			
			sBranchCd = yddatautil.setDataDefault(inDto.getField("BRANCH_CD"),     "");
		
			if("".equals(sBranchCd))
			{
				recPara.setField("BRANCH_CD1", "DFD");
				recPara.setField("BRANCH_CD2", "EDD");
				recPara.setField("BRANCH_CD3", "FFD");
				recPara.setField("BRANCH_CD4", "GFD");
				recPara.setField("BRANCH_CD5", "HKD");

//getYdStkbed17				
				intRtnVal = ydStkBedDao.getYdStkbed(recPara, outRecSet, 17);
				
			} else{
				recPara.setField("BRANCH_CD", sBranchCd);
				
//getYdStkbed18				
				intRtnVal = ydStkBedDao.getYdStkbed(recPara, outRecSet, 18);
				
			}

		
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
			
			logger.println(LogLevel.DEBUG_TEXT, "getCoilYdInPlan");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilYdInPlan
	
	
	/**
	 *  코일제품야드 입고진행 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdInPlan2(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getCoilYdInPlan2";

		
		String szEqpGp = null;
		String szOperationName = "코일제품야드 입고진행 조회";
		
		
		//DAO
		YdCrnWrkMtlDao ydCrnWrkMtlDao = new YdCrnWrkMtlDao();
		
		int intRtnVal = 0;
		
		//String sBranchCd    = "";
		//YdStkColDao ydStkcolDao = new YdStkColDao();
		
		
		try {
			
			/*
			sBranchCd = yddatautil.setDataDefault(inDto.getField("BRANCH_CD"),     "");
			System.out.println("sBranchCd===>>"+ sBranchCd);
			if(sBranchCd.equals("0"))
			{
				recPara.setField("BRANCH_CD1", "1");
				recPara.setField("BRANCH_CD2", "2");
				recPara.setField("BRANCH_CD3", "3");
				recPara.setField("BRANCH_CD4", "4");
				recPara.setField("BRANCH_CD5", "5");
			} else{
				recPara.setField("BRANCH_CD1", sBranchCd);
				recPara.setField("BRANCH_CD2", sBranchCd);
				recPara.setField("BRANCH_CD3", sBranchCd);
				recPara.setField("BRANCH_CD4", sBranchCd);
				recPara.setField("BRANCH_CD5", sBranchCd);
			}
			
			intRtnVal = ydStkcolDao.getYdStkcol(recPara, outRecSet, 17);
		
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
			
			logger.println(LogLevel.DEBUG_TEXT, "getCoilYdInPlan2");
			
			*/
			
			
			
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			szEqpGp = ydDaoUtils.paraRecChkNull(inDto, "EQP_GP");
			
			//------------------------------------------------------------------------------------
			// 코일 입고진행관리 조회 (2010.02.22) - 설비
			//------------------------------------------------------------------------------------
			
			if(!szEqpGp.equals("")){
				
				//------------------------------------------------------------------------------------
				// 코일 입고진행관리 조회 (2010.02.22) - 대차 
				//------------------------------------------------------------------------------------
				if(szEqpGp.equals("JXTC01") || szEqpGp.equals("JXTC02")){
					
					
					
					
				}
				
				//------------------------------------------------------------------------------------
				// 코일 입고진행관리 조회 (2010.02.22) - LINE 설비
				//------------------------------------------------------------------------------------
				else{
					
					recPara         = JDTORecordFactory.getInstance().create();
					recPara.setField("EQP_GP", szEqpGp);
//getYdCrnwrkmtl21					
					intRtnVal = ydCrnWrkMtlDao.getYdCrnwrkmtl(recPara, outRecSet, 21);
					
					if(intRtnVal < 0){
						szMsg = "[JSP Session : "+szOperationName+"]조회 ERROR ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						
					}else if(intRtnVal == 0){
						szMsg = "[JSP Session : "+szOperationName+"]조회된 DATA가 없습니다. ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
						
					}else{
						szMsg = "[JSP Session : "+szOperationName+"] 조회 성공";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
					
				}
		
			}
			
			//------------------------------------------------------------------------------------
			// 코일 입고진행관리 조회 (2010.02.22) - 전체를 선택한 경우  
			//------------------------------------------------------------------------------------
			
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilYdInPlan2
	
	/**
	 * 코일제품야드 입고진행 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updCoilYdInPlan(JDTORecord[] inDto) throws DAOException {
		int           intRtnVal     = 0;
		String        szMsg         = "";
		String        szMethodName  = "updCoilYdInPlan";
		JDTORecord    recPara       = JDTORecordFactory.getInstance().create();		
		
		String    colGp        = "";
		
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		
		try {
			
			//열별 저장위치 수정
			for(int x=0;x<inDto.length;x++){
			
			//수정항목 세팅 
			colGp = yddatautil.setDataDefault(inDto[x].getField("YD_GP"),            "")
		          + yddatautil.setDataDefault(inDto[x].getField("YD_BAY_GP"),        "")
 		          + yddatautil.setDataDefault(inDto[x].getField("YD_EQP_GP"),        "")
		          + yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_NO"),    "");
		      
		    recPara.setField("YD_STK_COL_GP", colGp);
		    recPara.setField("STL_NO",        yddatautil.setDataDefault(inDto[x].getField("STL_NO"),           ""));
		    recPara.setField("YD_STK_BED_NO", yddatautil.setDataDefault(inDto[x].getField("YD_STK_BED_NO"),    ""));
		    recPara.setField("YD_STK_LYR_NO", yddatautil.setDataDefault(inDto[x].getField("YD_STK_LYR_NO"),    ""));
		    
		    intRtnVal = ydStkLyrDao.updYdStklyr(recPara, 0);
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
	}	// end of updCoilYdInPlan
	
	/**
	 *  코일제품야드 입고진행 상세 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdInPlanDtl(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getCoilYdInPlanDtl";
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		
		int intRtnVal = 0;
		
		try {

			recPara.setField("EQP_GP", ydDaoUtils.paraRecChkNull(inDto,"GUBUN"));
			
//getYdStklyr46			
//PIDEV_S :병행가동용:PI_YD
			recPara.setField("PI_YD",    	 "J");			
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 46);
		
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
			
			logger.println(LogLevel.DEBUG_TEXT, "getCoilYdInPlanDtl");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilYdInPlanDtl
	
	/**
	 * 코일제품야드 입고진행 상세정보 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updCoilYdInPlanDtl(JDTORecord[] inDto) throws DAOException {
		int           intRtnVal     = 0;
		String        szMsg         = "";
		String        szMethodName  = "updCoilYdInPlanDtl";
		JDTORecord    recPara       = JDTORecordFactory.getInstance().create();		
		
		YdStockDao  ydStockDao = new YdStockDao();
		try {
			
			//열별 저장위치 수정
			for(int x=0;x<inDto.length;x++){
			    recPara.setField("STL_NO",           yddatautil.setDataDefault(inDto[x].getField("STL_NO"),           ""));
			    recPara.setField("YD_RCPT_STR_LOC",  yddatautil.setDataDefault(inDto[x].getField("YD_RCPT_STR_LOC"),  ""));
			    
			    intRtnVal = ydStockDao.updYdStock(recPara, 0);
			    
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
	}// end of updCoilYdInPlanDtl 
	
	
	
	/**
	 *  코일제품야드 제품단위 이적등록 조회
	 *  심명순 090518
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdGdsGdsUnitMvReg(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getCoilYdGdsGdsUnitMvReg";

		YdStockDao ydStockDao = new YdStockDao();
		
		int intRtnVal = 0;
		
		try {
			recPara.setField("YD_STK_COL_GP",     yddatautil.setDataDefault(inDto.getField("YD_GP"),         "") 
								           +  yddatautil.setDataDefault(inDto.getField("YD_BAY_GP"),     "")
								           +  yddatautil.setDataDefault(inDto.getField("YD_EQP_GP"),     "")
								           +  yddatautil.setDataDefault(inDto.getField("YD_STK_COL_NO"), ""));
			//recPara.setField("YD_STK_COL_NO",     yddatautil.setDataDefault(inDto.getField("YD_STK_COL_NO"), ""));
			recPara.setField("ORD_LINE",     inDto.getField("ORD_LINE"));			
			
			recPara.setField("PAGE_CNT1", inDto.getField("PAGE_NO"));
			recPara.setField("PAGE_CNT2", inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT1",  inDto.getField("ROWCOUNT"));
			recPara.setField("ROW_CNT2",  inDto.getField("ROWCOUNT"));
			
			System.out.println("recPara ===="+recPara);
			
//getYdStock82
//PIDEV_S :병행가동용:PI_YD
            recPara.setField("PI_YD",       yddatautil.setDataDefault(inDto.getField("YD_GP"),"") ); 
			intRtnVal  = ydStockDao.getYdStock(recPara, outRecSet, 82);
		
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
			
			logger.println(LogLevel.DEBUG_TEXT, "getCoilYdColStkUsageRtoExpStk");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilYdGdsGdsUnitMvReg
	
	/**
	 *  코일제품야드 차량모니터링 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdInBayCarList(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getCoilYdInBayCarList";
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		
		int intRtnVal = 0;
		
		try {
			recPara.setField("YD_GP",           yddatautil.setDataDefault(inDto.getField("YD_GP"),         ""));
			recPara.setField("YD_CAR_WRK_GP",   yddatautil.setDataDefault(inDto.getField("YD_CAR_WRK_GP"), ""));
			recPara.setField("YD_PNT_CD",       yddatautil.setDataDefault(inDto.getField("YD_PNT_CD"),     ""));
			recPara.setField("CAR_GP",          yddatautil.setDataDefault(inDto.getField("CAR_GP"),        ""));

//getYdStklyr46
//PIDEV_S :병행가동용:PI_YD
			recPara.setField("PI_YD",    	 yddatautil.setDataDefault(inDto.getField("YD_GP"),         ""));
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 46);
		
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
			
			logger.println(LogLevel.DEBUG_TEXT, "getCoilYdInBayCarList");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilYdInBayCarList
	
	
	/**
	 *  코일제품야드 입동대기차량 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdInBayRdCarList(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getCoilYdInBayRdCarList";
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		
		int intRtnVal = 0;
		
		try {
			recPara.setField("YD_GP",           yddatautil.setDataDefault(inDto.getField("YD_GP"),         ""));
			recPara.setField("YD_CAR_WRK_GP",   yddatautil.setDataDefault(inDto.getField("YD_CAR_WRK_GP"), ""));
			recPara.setField("YD_PNT_CD",       yddatautil.setDataDefault(inDto.getField("YD_PNT_CD"),     ""));
			recPara.setField("CAR_GP",          yddatautil.setDataDefault(inDto.getField("CAR_GP"),        ""));
//getYdStklyr46
//PIDEV_S :병행가동용:PI_YD
			recPara.setField("PI_YD",    	 yddatautil.setDataDefault(inDto.getField("YD_GP"),         ""));		
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 46);
		
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
			
			logger.println(LogLevel.DEBUG_TEXT, "getCoilYdInBayRdCarList");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilYdInBayRdCarList
	
	
	/**
	 *  코일제품야드 재료상세백업 저장위치 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdStlDtlBakcup1(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String      szMsg        = "";
		String      szMethodName = "getCoilYdStlDtlBakcup1";
		YdStockDao  ydStockDao  = new YdStockDao();
		
		int intRtnVal = 0;
		
		try {
			recPara.setField("STL_NO",   yddatautil.setDataDefault(inDto.getField("STL_NO"), ""));
			//recPara.setField("CAR_NO",   yddatautil.setDataDefault(inDto.getField("CAR_NO"), ""));
//getYdStock83
			intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 83);
		
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
			
			logger.println(LogLevel.DEBUG_TEXT, "getCoilYdStlDtlBakcup1");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilYdStlDtlBakcup1
	
	
	/**
	 *  코일제품야드 재료상세백업 스케줄정보 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdStlDtlBakcup2(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getCoilYdStlDtlBakcup2";
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		
		int intRtnVal = 0;
		
		try {
			recPara.setField("STL_NO",   yddatautil.setDataDefault(inDto.getField("STL_NO"), ""));
//getYdWrkbook13			
			intRtnVal = ydWrkbookDao.getYdWrkbook(recPara, outRecSet, 13);
		
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
			
			logger.println(LogLevel.DEBUG_TEXT, "getCoilYdStlDtlBakcup2");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilYdStlDtlBakcup2
	
	
	/**
	 *  코일제품야드 재료상세백업 재료지시정보 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdStlDtlBakcup3(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getCoilYdStlDtlBakcup3";
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		
		int intRtnVal = 0;
		
		try {
			recPara.setField("STL_NO",   yddatautil.setDataDefault(inDto.getField("STL_NO"), ""));
			recPara.setField("CAR_NO",   yddatautil.setDataDefault(inDto.getField("CAR_NO"), ""));

			//intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 46);
			intRtnVal = 0;
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
			
			logger.println(LogLevel.DEBUG_TEXT, "getCoilYdStlDtlBakcup3");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilYdStlDtlBakcup3
	
	
	
	/**
	 *  코일제품야드 재료상세백업 재료위치정보 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdStlDtlBakcup4(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getCoilYdStlDtlBakcup4";
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		
		int intRtnVal = 0;
		
		try {
			recPara.setField("STL_NO",   yddatautil.setDataDefault(inDto.getField("STL_NO"), ""));
			recPara.setField("CAR_NO",   yddatautil.setDataDefault(inDto.getField("CAR_NO"), ""));
//getYdStklyr24			
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 24);
		
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
			
			logger.println(LogLevel.DEBUG_TEXT, "getCoilYdStlDtlBakcup4");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilYdStlDtlBakcup4
	
	
	/**
	 *  코일제품야드 재료상세백업 재료위치정보 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdStlDtlBakcup5(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara    = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet  = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String      szMsg        = "";		
		String      szMethodName = "getCoilYdStlDtlBakcup5";
		YdCarSchDao ydCarSchDao  = new YdCarSchDao();
		
		int intRtnVal = 0;
		
		try {
			recPara.setField("STL_NO",   yddatautil.setDataDefault(inDto.getField("STL_NO"), ""));
			recPara.setField("CAR_NO",   yddatautil.setDataDefault(inDto.getField("CAR_NO"), ""));
			
//getYdCarsch14			
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, outRecSet, 14);
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
			
			logger.println(LogLevel.DEBUG_TEXT, "getCoilYdStlDtlBakcup5");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilYdStlDtlBakcup5
	
	/**
	 *  코일제품야드 차량모니터링 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdGdsCarMonitoring(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getCoilYdGdsCarMonitoring";

		YdCarSchDao ydCarSchDao = new YdCarSchDao();
		
		int intRtnVal = 0;
		
		try {
			recPara.setField("YD_GP",         yddatautil.setDataDefault(inDto.getField("YD_GP"),         ""));			
			recPara.setField("POINT_GP",      yddatautil.setDataDefault(inDto.getField("POINT_GP"),      ""));
			recPara.setField("YD_EQP_GP",     yddatautil.setDataDefault(inDto.getField("YD_EQP_GP"),     ""));
//getYdCarsch13			
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, outRecSet, 13);
			
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
			
			logger.println(LogLevel.DEBUG_TEXT, "getCoilYdGdsCarMonitoring");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilYdGdsCarMonitoring
	
	
	/**
	 *  코일제품창고 반납 크레인 조회 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdRetCrnReg(JDTORecord inDto) throws DAOException {
		
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
				
		String szMsg="";		
		String szMethodName="getCoilYdRetCrnReg";
		String szRetGp = "";
		
			
		int intRtnVal = 0;
		
		try {
			
			szRetGp = yddatautil.setDataDefault(inDto.getField("CRN_RET_TP"), "");
			//선택된 경우에만 값을 넣어준다. 
			if ("".equals(szRetGp))
			{
				recPara.setField("PROG_CD1"	,"J");
				recPara.setField("PROG_CD2"	,"G");
				recPara.setField("PROG_CD3"	,"H");
				recPara.setField("PROG_CD4"	,"F");
				recPara.setField("PROG_CD5"	,"B");
			} else if ("I2".equals(szRetGp)) {   //반송
				recPara.setField("PROG_CD1"	,"J");
				recPara.setField("PROG_CD2"	,"");
				recPara.setField("PROG_CD3"	,"");
				recPara.setField("PROG_CD4"	,"");
				recPara.setField("PROG_CD5"	,"");
			} else {
				recPara.setField("PROG_CD1"	,"G");
				recPara.setField("PROG_CD2"	,"H");
				recPara.setField("PROG_CD3"	,"F");
				recPara.setField("PROG_CD4"	,"B");
				recPara.setField("PROG_CD5"	,"");
			}	

						
			recPara.setField("YD_GP"	,yddatautil.setDataDefault(inDto.getField("YD_GP"), ""));
			recPara.setField("YD_BAY_GP",yddatautil.setDataDefault(inDto.getField("YD_BAY_GP"), ""));
			recPara.setField("YD_BAY_GP1",yddatautil.setDataDefault(inDto.getField("YD_BAY_GP1"), ""));
			
			recPara.setField("PAGE_CNT1",inDto.getField("PAGE_NO"));
			recPara.setField("PAGE_CNT2",inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT1",inDto.getField("ROWCOUNT"));
			recPara.setField("ROW_CNT2",inDto.getField("ROWCOUNT"));
					
			System.out.println("recPara ====="+ recPara);
			System.out.println("recPara ====="+ recPara);
			System.out.println("recPara ====="+ recPara);
			System.out.println("recPara ====="+ recPara);
			System.out.println("recPara ====="+ recPara);
			
			YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
			
//			if("".equals(szRetGp)){
//getYdStklyr59				
				/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdGdsRetCrnReg_PAGE*/
				intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 59);
//			} else{
//getYdStklyr60				
//				intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 60);
//			}
				
			
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
		
		return outRecSet;
	}//end of getCoilYdRetCrnReg
	/**
	 *  반납/반송 조회 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdRetCrnReg1(JDTORecord inDto) throws DAOException {
		
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
				
		String szMsg="";		
		String szMethodName="getCoilYdRetCrnReg1";
		String szRetGp = "";
		String sztemp = "";
			
		int intRtnVal = 0;
		
		try {
			
			szRetGp = yddatautil.setDataDefault(inDto.getField("CRN_RET_TP"), "");
			sztemp = yddatautil.setDataDefault(inDto.getField("COIL_NO"), "");
			recPara.setField("COIL_NO"		, sztemp);
			recPara.setField("YD_GP"		, yddatautil.setDataDefault(inDto.getField("YD_GP"), ""));
			recPara.setField("YD_BAY_GP"	, yddatautil.setDataDefault(inDto.getField("YD_BAY_GP"), ""));
			recPara.setField("YD_BAY_GP1"	, yddatautil.setDataDefault(inDto.getField("YD_BAY_GP1"), ""));
			recPara.setField("EMERGENCY"	, yddatautil.setDataDefault(inDto.getField("EMERGENCY"), ""));			
			recPara.setField("CHK_FLAG"		, szRetGp);
			
			recPara.setField("PAGE_CNT1"	, inDto.getField("PAGE_NO"));
			recPara.setField("PAGE_CNT2"	, inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT1"		, inDto.getField("ROWCOUNT"));
			recPara.setField("ROW_CNT2"		, inDto.getField("ROWCOUNT"));
					
			YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
			
			/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdRetCrnReg_PIDEV*/
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 604);
			
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
		
		return outRecSet;
	}//end of getCoilYdRetCrnReg1

	/**
	 *  지포장대상조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdSendGF(JDTORecord inDto) throws DAOException {
		
		JPlateYdCommDAO ydCommDao = new JPlateYdCommDAO();
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
				
		String szMsg="";		
		String szMethodName="getCoilYdSendGF";
		String szRetGp = "";
		String sztemp = "";
		String sQueryId = "";
		int intRtnVal = 0;
		
		try {
			
			szRetGp = yddatautil.setDataDefault(inDto.getField("CRN_RET_TP"), "");
			sztemp = yddatautil.setDataDefault(inDto.getField("COIL_NO"), "");
			recPara.setField("COIL_NO"		, sztemp);
			recPara.setField("YD_GP"		, "H");
			recPara.setField("YD_BAY_GP"	, yddatautil.setDataDefault(inDto.getField("YD_DONG_GP"), ""));
			recPara.setField("YD_EQP_GP"	, yddatautil.setDataDefault(inDto.getField("YD_SPAN_GP"), ""));
			recPara.setField("YD_STK_COL_NO"	, yddatautil.setDataDefault(inDto.getField("YD_COL_GP"), ""));
			recPara.setField("EMERGENCY"	, yddatautil.setDataDefault(inDto.getField("EMERGENCY"), ""));			
			recPara.setField("CHK_FLAG"		, szRetGp);
			
			recPara.setField("PAGE_CNT1"	, inDto.getField("PAGE_NO"));
			recPara.setField("PAGE_CNT2"	, inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT1"		, inDto.getField("ROWCOUNT"));
			recPara.setField("ROW_CNT2"		, inDto.getField("ROWCOUNT"));
			
			sQueryId = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdSendGF";
		    intRtnVal = ydCommDao.select(recPara, outRecSet, sQueryId);
		    
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
		
		return outRecSet;
	}//end of getCoilYdSendGF
	

	/**
	 * 지포장 보급 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord updCoilYdSendGF(JDTORecord inDto) {
		int       intRtnVal    = 0;
		String    szMsg        = "";
		String    szMethodName = "updCoilYdSendGF";
		String szOperationName = "지포장 보급";
		JDTORecord 			outRecord 		= JDTORecordFactory.getInstance().create(); 
		
		YdUtils ydUtils = new YdUtils();
				
		JDTORecord    recPara  = JDTORecordFactory.getInstance().create();	
		YdStockDao ydStockDao = new YdStockDao();
		
		
		szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		try {
			
			
			recPara.setField("STL_NO",   		ydDaoUtils.paraRecChkNull(inDto, "STL_NO"));
			recPara.setField("YD_AIM_BAY_GP",   ydDaoUtils.paraRecChkNull(inDto, "YD_AIM_BAY_GP"));
			recPara.setField("YD_AIM_YD_GP",    ydDaoUtils.paraRecChkNull(inDto, "YD_AIM_YD_GP"));
			recPara.setField("MODIFIER",   		ydDaoUtils.paraRecChkNull(inDto, "YD_USER_ID"));
			recPara.setField("NEXT_PROG",   	ydDaoUtils.paraRecChkNull(inDto, "WO_CAR_PLNT_PROC_CD"));
			recPara.setField("WO_CAR_PLNT_PROC_CD",   	ydDaoUtils.paraRecChkNull(inDto, "WO_CAR_PLNT_PROC_CD"));
			recPara.setField("YD_AIM_RT_GP",   	ydDaoUtils.paraRecChkNull(inDto, "YD_AIM_RT_GP"));
			
			ydUtils.displayRecord(szOperationName, recPara);
//updYdStock0				
	        intRtnVal = ydStockDao.updYdStock(recPara,0);
	        
			if (intRtnVal < 1) {
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, "목표동 수정시 Error 발생.");	
				return outRecord;
			} // end of if		
			
			
			intRtnVal = ydStockDao.updPtComm_PROG_CD(recPara, 4);
			if (intRtnVal < 1) {
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, "반납공정 수정시 Error 발생.");	
				return outRecord;
			} // end of if		

			outRecord.setField("RTN_CD" 	, "1");	
			return outRecord;
		
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

	}	// end of updCoilYdSendGF
	
	
	/**
	 *  코일제품창고 반송 조회 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdRetMgt(JDTORecord inDto) throws DAOException {
		
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
				
		String szMsg="";		
		String szMethodName="getCoilYdRetMgt";
		String szRetGp 		= "";
		String szLOC_CD 	= "";
		String szPROG_CD 	= "";
		String szCOIL_NO 	= "";
		String szSNDBK_GP 	= "";
		
			
		int intRtnVal = 0;
		
		try {
			
			szLOC_CD 	= yddatautil.setDataDefault(inDto.getField("LOC_CD"), "");
			szPROG_CD 	= yddatautil.setDataDefault(inDto.getField("PROG_CD"), "");
			szCOIL_NO 	= yddatautil.setDataDefault(inDto.getField("COIL_NO"), "");
			szSNDBK_GP 	= yddatautil.setDataDefault(inDto.getField("SNDBK_GP"), "");
			
			//선택된 경우에만 값을 넣어준다. 
			recPara.setField("LOC_CD"	,szLOC_CD);
			recPara.setField("PROG_CD"	,szPROG_CD);
			recPara.setField("COIL_NO"	,szCOIL_NO);
			recPara.setField("SNDBK_GP"	,szSNDBK_GP);
			
			recPara.setField("PAGE_CNT1",inDto.getField("PAGE_NO"));
			recPara.setField("PAGE_CNT2",inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT1",inDto.getField("ROWCOUNT"));
			recPara.setField("ROW_CNT2",inDto.getField("ROWCOUNT"));
			
			YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
			
			/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrCoilSndbk*/
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 603);
				
			
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
		
		return outRecSet;
	}//end of getCoilYdRetMgt
	
	
	
	/**
	 * 사유별 이적등록의 대차제품상세 조회/검색하는 메소드이다.코일 공통  항목을 조회한다.(검색어, 업무영역코드)
	 * 심명순(090713)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getBecauseMv(JDTORecord inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg="";
		String szMethodName="getYdStock";		
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecordSet rSetStock = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecord recPara = JDTORecordFactory.getInstance().create();

		YdStockDao ydStockDao = new YdStockDao();
	
		try {
			
			
			recPara.setField("YD_GP" , ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));
			recPara.setField("YD_BAY_GP" , ydDaoUtils.paraRecChkNull(inDto, "YD_BAY_GP"));
			recPara.setField("YD_EQP_GP" , ydDaoUtils.paraRecChkNull(inDto, "YD_EQP_GP"));
			recPara.setField("YD_STK_COL_NO" , ydDaoUtils.paraRecChkNull(inDto, "YD_STK_COL_NO"));
//			!AD recPara.setField("YD_COIL_GP" , ydDaoUtils.paraRecChkNull(inDto, "YD_COIL_GP"));
			recPara.setField("PAGE_NO" , ydDaoUtils.paraRecChkNull(inDto, "PAGE_NO"));
			recPara.setField("ROW_CNT" , ydDaoUtils.paraRecChkNull(inDto, "ROWCOUNT"));
			
			
//			!AD recPara.setField("ORD_GP" , ydDaoUtils.paraRecChkNull(inDto, "ORD_GP"));
//			!AD recPara.setField("DEST_CD" , ydDaoUtils.paraRecChkNull(inDto, "DEST_CD"));
//			!AD recPara.setField("DEMANDER_CD" , ydDaoUtils.paraRecChkNull(inDto, "DEMANDER_CD"));
//			!AD recPara.setField("ORD" , ydDaoUtils.paraRecChkNull(inDto, "ORD"));
//			!AD recPara.setField("PROG_CD" , ydDaoUtils.paraRecChkNull(inDto, "PROG_CD"));
			recPara.setField("YD_COIL_OUTDIA_GRP_GP" , ydDaoUtils.paraRecChkNull(inDto, "YD_COIL_OUTDIA_GRP_GP"));
			
			//야드 저장품 테이블에서 품목코드를 읽어온다
			
//getYdStock59		
//PIDEV_S :병행가동용:PI_YD
			recPara.setField("PI_YD",    	ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));			
			intRtnVal = ydStockDao.getYdStock(recPara, rSetStock, 59);
						
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
		return rSetStock;
	} // end of getBecauseMv
	
	/**
	 * 저장위치관리화면 조회
	 * 심명순(090713)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdStkPosSet(JDTORecord inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg="";
		String szMethodName="getCoilYdStkPosSet";
		
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		YdStkColDao ydStkcolDao = new YdStkColDao();
		
			
		try {
			if (inDto.getFieldString("YD_STK_COL_NO").trim().equals("ALL"))
			{				
				recPara.setField("YD_GP", 	inDto.getField("YD_GP"));
				recPara.setField("YD_BAY_GP", inDto.getField("YD_BAY_GP"));
				recPara.setField("YD_EQP_GP", inDto.getField("YD_EQP_GP"));
//getYdStkcol1				
				intRtnVal = ydStkcolDao.getYdStkcol(recPara,outRecSet,1);
			
			} else{				
				recPara.setField("YD_GP", 		inDto.getField("YD_GP"));
				recPara.setField("YD_BAY_GP", 	inDto.getField("YD_BAY_GP"));
				recPara.setField("YD_EQP_GP", 	inDto.getField("YD_EQP_GP"));
				recPara.setField("YD_STK_COL_NO", inDto.getField("YD_STK_COL_NO"));

//getYdStkcol13				
				intRtnVal = ydStkcolDao.getYdStkcol(recPara,outRecSet,13);	
				
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
						
		
			ydUtils.putLog(szSessionName, szMethodName, szMsg,  YdConstant.DEBUG);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		return outRecSet;
	}	// end of getCoilYdStkPosSet
	
	/**
	 *저장위치 좌표설정화면 베드 조회
	 * SJH
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdStkPosSetBed(JDTORecord inDto) throws DAOException {

		//for Log 
		String szMsg="";
		String szMethodName="getCoilYdStkPosSetBed";		
		int intRtnVal = 0;
		
		String szEditStkPos =null;
		String szEditStkCol =null;
		String szEditStkBed =null;
		
		
		
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JDTORecord recEdit = JDTORecordFactory.getInstance().create();
		JDTORecordSet retRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");		
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		YdStkBedDao ydStkbedDao = new YdStkBedDao();
		 
		try {
			
			   //적치열구분을 Parameter 로 Set
				recPara.setField("YD_STK_COL_GP", 	inDto.getField("YD_STK_COL_GP"));

//getYdStkbed1
//!AT				intRtnVal = ydStkbedDao.getYdStkbed(recPara,outRecSet,1);
				intRtnVal = ydStkbedDao.getYdStkbed(recPara,outRecSet,300);
				
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
			
			
			
			//JDTORecordSet 에 첫 위치로 이동시킨다. 
			outRecSet.first();
			
			do {
				
				//스케줄 코드를  설비구분을 분리하여 YD_EQP_GP 컬럼에 넣어준다.
				recEdit = outRecSet.getRecord();
				szEditStkCol = yddatautil.setDataDefault(recEdit.getField("YD_STK_COL_GP"), ""); 
				szEditStkBed = yddatautil.setDataDefault(recEdit.getField("YD_STK_BED_NO"), "");
					
				szEditStkPos = szEditStkCol + "-"+ szEditStkBed;
				recEdit.setField("YD_STK_POS", szEditStkPos);
				
				retRecSet.addRecord(recEdit);
				
			}while(outRecSet.next());
		
			ydUtils.putLog(szSessionName, szMethodName, szMsg,  YdConstant.DEBUG);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		return retRecSet;
	}	// end of getCoilYdStkPosSetBed
	
	
	/**
	 *저장위치 좌표설정화면 열 수정 
	 * 심명순(090713)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String  updCoilYdStkPosSet(JDTORecord [] inDto) throws DAOException {
		
		JPlateYdCommDAO ydCommDao = new JPlateYdCommDAO();
		
		int intRtnVal = 0;
		String szMsg="";
		String sQueryId = "";
		String szPassWord="";
		String szMethodName="updCoilYdStkPosSet";
		String szRtnMsg = YdConstant.RETN_CD_SUCCESS;
		
		String szFromXRule = "";
		String szFromYRule = "";
		String szFromZRule = "";
		String szToXRule = "";
		String szToYRule = "";
		String szToZRule = "";
		
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JDTORecord msgRecord = JDTORecordFactory.getInstance().create();
		JDTORecord inRecord = JDTORecordFactory.getInstance().create();
		JDTORecord recOutTemp = JDTORecordFactory.getInstance().create();
		JDTORecordSet rsResult = null;
		
		YdStkColDao ydStkcolDao = new YdStkColDao();
		 
		try {
			//메뉴 페이지 목록을 수정한다.
			for(int x=0;x<inDto.length;x++){
			
				
				//수정할 항목 SETTING
				
				recPara = inDto[x];
				
				recPara.setField("MODIFIER", recPara.getField("YD_USER_ID"));
				szFromXRule = recPara.getFieldString("YD_STK_COL_RULE_XAXIS_HIDDEN");
				szFromYRule = recPara.getFieldString("YD_STK_COL_RULE_YAXIS_HIDDEN");
				szFromZRule = recPara.getFieldString("YD_STK_COL_RULE_ZAXIS_HIDDEN");
				
				szToXRule = recPara.getFieldString("YD_STK_COL_RULE_XAXIS");
				szToYRule = recPara.getFieldString("YD_STK_COL_RULE_YAXIS");
				szToZRule = recPara.getFieldString("YD_STK_COL_RULE_ZAXIS");
				
				ydUtils.putLog(szSessionName, szMethodName, "★  야드저장위치좌표관리 체크 szFromXRule="+szFromXRule, YdConstant.INFO);
				ydUtils.putLog(szSessionName, szMethodName, "★  야드저장위치좌표관리 체크 szFromYRule="+szFromYRule, YdConstant.INFO);
				ydUtils.putLog(szSessionName, szMethodName, "★  야드저장위치좌표관리 체크 szFromZRule="+szFromZRule, YdConstant.INFO);
				ydUtils.putLog(szSessionName, szMethodName, "★  야드저장위치좌표관리 체크 szToXRule="+szToXRule, YdConstant.INFO);
				ydUtils.putLog(szSessionName, szMethodName, "★  야드저장위치좌표관리 체크 szToYRule="+szToYRule, YdConstant.INFO);
				ydUtils.putLog(szSessionName, szMethodName, "★  야드저장위치좌표관리 체크 szToZRule="+szToZRule, YdConstant.INFO);
				
//				151118 hun 야드저장위치좌표관리 PassWord 체크 X,Y,Z 축 수정시에만 체크함
				if(!szFromXRule.equals(szToXRule) || !szFromYRule.equals(szToYRule) || !szFromZRule.equals(szToZRule)){
				
//					151112 hun 야드저장위치좌표관리 PassWord 체크 start
					ydUtils.putLog(szSessionName, szMethodName, "★  야드저장위치좌표관리 PassWord 체크 start", YdConstant.INFO);
					szPassWord = ydDaoUtils.paraRecChkNull(recPara, "INPUT_PASS");
					
					rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
				    inRecord = JDTORecordFactory.getInstance().create();
				    sQueryId = "com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdLocByPassWrod";
				    intRtnVal = ydCommDao.select(inRecord, rsResult, sQueryId);
				    
				    rsResult.first();
				    recOutTemp = JDTORecordFactory.getInstance().create();
				    recOutTemp  = rsResult.getRecord();
				    
					if(!szPassWord.equals(recOutTemp.getFieldString("ITEM_VALUE1"))){
						return "PassWord를 정확히 입력하시기 바랍니다.";
					}
					ydUtils.putLog(szSessionName, szMethodName, "★  야드저장위치좌표관리 PassWord 체크 end", YdConstant.INFO);
				}
				//------------------------------------------------------------------------------
				// 적치열 정보중 외경정보 변경
				// ydStkColDao.updYdStkcol(recPara, 0);
				// ydStkBedDao.updYdStkbedYdStkColGp(recPara, 5);
				//------------------------------------------------------------------------------
				
				szRtnMsg = yddatautil.updYdCoilOutdiaGrpGpCol(recPara);
				if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
					throw new DAOException(getClass().getName()+ szRtnMsg);
				}
				
				
				//------------------------------------------------------------------------------
				// 적치열 정보중 폭구분 변경 
				//ydStkColDao.updYdStkcol(recPara, 0)
				//ydStkBedDao.updYdStkbedYdStkColGp(recPara, 6);
				//------------------------------------------------------------------------------
				
				szRtnMsg = yddatautil.updYdStkbedYdStkBedWGp(recPara);
				if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
					throw new DAOException(getClass().getName()+ szRtnMsg);
				}
				
				
				//------------------------------------------------------------------------------
				// 적치베드 정보중 저장집합코드 변경
				//ydStkBedDao.updYdStkbedYdStkColGp(recPara, 7)
				//------------------------------------------------------------------------------
				
				szRtnMsg = yddatautil.updYdStkbedStrGtrCd(recPara);
				if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
					throw new DAOException(getClass().getName()+ szRtnMsg);
				}
				
				
				//------------------------------------------------------------------------------
				// 적치베드 정보중 야드적치단활성상태
				//ydStkBedDao.updYdStkbedYdStkColGp(recPara, 7)
				//------------------------------------------------------------------------------
				
				szRtnMsg = yddatautil.updYdCoilLyeActStatCol(recPara);
				if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
					throw new DAOException(getClass().getName()+ szRtnMsg);
				}
				
				
				//------------------------------------------------------------------------------
				// 적치베드 정보중 야드적치단활성상태
				//ydStkBedDao.updYdStkbedYdStkColGp(recPara, 7)
				//------------------------------------------------------------------------------
				
				szRtnMsg = yddatautil.updYdCoilRuleXCol(recPara);
				if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
					throw new DAOException(getClass().getName()+ szRtnMsg);
				}
				
				
				
				//------------------------------------------------------------------------------
				// 적치열 정보 UPDATE 
				//------------------------------------------------------------------------------
				/* com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.updYdStkcol */
				intRtnVal = ydStkcolDao.updYdStkcol(recPara,11);
				
				
				
				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}					
				} // end of if
				
				ydUtils.putLog(szSessionName, szMethodName, szMsg,  YdConstant.DEBUG);
 				
				if(intRtnVal > 0){
					
					//------------------------------------------------------------------------------
					//L2저장위치제원정보 전송 YDY5L001
					//------------------------------------------------------------------------------
					String szYD_STK_COL_GP=ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");
					
					msgRecord = JDTORecordFactory.getInstance().create();
					msgRecord.setField("YD_INFO_SYNC_CD", "3");				//1:동,2:SPAN,3:열,4:BED
					msgRecord.setField("YD_GP", 			szYD_STK_COL_GP.substring(0, 1));
					msgRecord.setField("YD_STK_COL_GP", 	szYD_STK_COL_GP);
					
					YdCommonUtils.sndStrPosSpecToL2(msgRecord);
					
					szRtnMsg = YdConstant.RETN_CD_SUCCESS;
				}
			}
			
			
			return szRtnMsg ; 

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally{
		}		
	}	// end of updCoilYdStkPosSet	
	
	/**
	 *저장위치 좌표설정화면 BED 수정 
	 * SJH
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 */
	public JDTORecord updCoilYdStkPosSetBed(JDTORecord [] inDto)throws DAOException {
		
		YdStkLyrDao YdStkLyrDao = new YdStkLyrDao();
		JPlateYdCommDAO ydCommDao = new JPlateYdCommDAO();
		
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JDTORecord outRecord = JDTORecordFactory.getInstance().create(); // 
		JDTORecord inRecord = JDTORecordFactory.getInstance().create();
		JDTORecord recOutTemp = JDTORecordFactory.getInstance().create();
		JDTORecordSet rsResult = null;
		
		int intRtnVal = 0;
		String szMsg="";
		String szPassWord = "";
		String sQueryId = "";
		String szMethodName="updCoilYdStkPosSetBed";
		String szYD_STK_BED_NO2="";
		String szYD_STK_COL_GP2="";
		
		String szFromXRule = "";
		String szFromYRule = "";
		String szFromZRule = "";
		String szFromXRuleTol = "";
		String szFromYRuleTol = "";
		String szFromZRuleTol = "";
		String szToXRule = "";
		String szToYRule = "";
		String szToZRule = "";
		String szToXRuleTol = "";
		String szToYRuleTol = "";
		String szToZRuleTol = "";
		String szPassFlag = "";
		
		try {
			if(inDto.length==0){
				outRecord.setField("RTN_CD" 	, "1");	
				outRecord.setField("RTN_MSG" 	, "수정하실 항목을 선택하시기 바랍니다.");	
				return outRecord;
			}
			//저장위치 좌표설정화면 BED 수정 
			for(int x=0;x<inDto.length;x++){
				
				recPara = JDTORecordFactory.getInstance().create();
				
				recPara = inDto[x];
				
				szFromXRule = recPara.getFieldString("YD_STK_LYR_XAXIS_HIDDEN");
				szFromYRule = recPara.getFieldString("YD_STK_LYR_YAXIS_HIDDEN");
				szFromZRule = recPara.getFieldString("YD_STK_LYR_ZAXIS_HIDDEN");
				szFromXRuleTol = recPara.getFieldString("YD_STK_BED_XAXIS_TOL_HIDDEN");
				szFromYRuleTol = recPara.getFieldString("YD_STK_BED_YAXIS_TOL_HIDDEN");
				szFromZRuleTol = recPara.getFieldString("YD_STK_BED_ZAXIS_TOL_HIDDEN");
				
				szToXRule = recPara.getFieldString("YD_STK_LYR_XAXIS");
				szToYRule = recPara.getFieldString("YD_STK_LYR_YAXIS");
				szToZRule = recPara.getFieldString("YD_STK_LYR_ZAXIS");
				szToXRuleTol = recPara.getFieldString("YD_STK_BED_XAXIS_TOL");
				szToYRuleTol = recPara.getFieldString("YD_STK_BED_YAXIS_TOL");
				szToZRuleTol = recPara.getFieldString("YD_STK_BED_ZAXIS_TOL");
				szPassFlag = recPara.getFieldString("PASS_FLAG");
				
				ydUtils.putLog(szSessionName, szMethodName, "★  야드저장위치좌표관리 체크 szFromXRule="+szFromXRule, YdConstant.INFO);
				ydUtils.putLog(szSessionName, szMethodName, "★  야드저장위치좌표관리 체크 szFromYRule="+szFromYRule, YdConstant.INFO);
				ydUtils.putLog(szSessionName, szMethodName, "★  야드저장위치좌표관리 체크 szFromZRule="+szFromZRule, YdConstant.INFO);
				ydUtils.putLog(szSessionName, szMethodName, "★  야드저장위치좌표관리 체크 szToXRule="+szToXRule, YdConstant.INFO);
				ydUtils.putLog(szSessionName, szMethodName, "★  야드저장위치좌표관리 체크 szToYRule="+szToYRule, YdConstant.INFO);
				ydUtils.putLog(szSessionName, szMethodName, "★  야드저장위치좌표관리 체크 szToZRule="+szToZRule, YdConstant.INFO);
				ydUtils.putLog(szSessionName, szMethodName, "★  야드저장위치좌표관리 체크 szPassFlag="+szPassFlag, YdConstant.INFO);
				
//				151123 hun 군만 변경시 패스워드 체크 안함
				if(!"pass".equals(szPassFlag)){
//				151118 hun 야드저장위치좌표관리 PassWord 체크 X,Y,Z 축 수정시에만 체크함
					if(!szFromXRule.equals(szToXRule) || !szFromYRule.equals(szToYRule) || !szFromZRule.equals(szToZRule)
					  || !szFromXRuleTol.equals(szToXRuleTol) || !szFromYRuleTol.equals(szToYRuleTol) || !szFromZRuleTol.equals(szToZRuleTol)
					){					
					
//				151113 hun 야드저장위치좌표관리 PassWord 체크 start
						ydUtils.putLog(szSessionName, szMethodName, "★  야드저장위치좌표관리 PassWord 체크 start", YdConstant.INFO);
						szPassWord = ydDaoUtils.paraRecChkNull(recPara, "INPUT_PASS");
						
						rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
					    inRecord = JDTORecordFactory.getInstance().create();
					    sQueryId = "com.inisteel.cim.yd.dao.ydlocsrchrngdao.YdLocsrchrngDao.getYdLocByPassWrod";
					    intRtnVal = ydCommDao.select(inRecord, rsResult, sQueryId);
					    
					    rsResult.first();
					    recOutTemp = JDTORecordFactory.getInstance().create();
					    recOutTemp  = rsResult.getRecord();
					    
						if(!szPassWord.equals(recOutTemp.getFieldString("ITEM_VALUE1"))){
							outRecord.setField("RTN_CD" 	, "1");	
							outRecord.setField("RTN_MSG" 	, "PassWord를 정확히 입력하시기 바랍니다.");	
							return outRecord;
						}
						ydUtils.putLog(szSessionName, szMethodName, "★  야드저장위치좌표관리 PassWord 체크 end", YdConstant.INFO);
					}
				}
				
				//수정할 항목 SETTING
				
				//적치열구분 필수 
				
				
				/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrDan*/
				intRtnVal = YdStkLyrDao.updYdStklyrDan(recPara);
								
				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}					
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
				} // end of if
				
				
				/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrTol*/
				intRtnVal = YdStkLyrDao.updYdStklyrTol(recPara);
								
				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}	
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
				}
				
				ydUtils.putLog(szSessionName, szMethodName, szMsg,  YdConstant.DEBUG);			
			
			}
			
			
			for(int x=0;x<inDto.length;x++){
				//------------------------------------------------------------------------------
				//L2저장위치제원정보 전송 YDY5L001
				//------------------------------------------------------------------------------
				//적치열구분 필수 
				recPara = JDTORecordFactory.getInstance().create();
				
				recPara = inDto[x];
									
				String szYD_STK_COL_GP=ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");
				String szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
				
				szMsg ="@@@@@"+x+"단정보 :"+szYD_STK_COL_GP2;
				ydUtils.putLog(szSessionName, szMethodName,szMsg ,  YdConstant.DEBUG);	
				if((!szYD_STK_COL_GP2.equals(szYD_STK_COL_GP+szYD_STK_BED_NO) ) ||szYD_STK_COL_GP2.equals("")){
					
 
					szYD_STK_COL_GP2=szYD_STK_COL_GP+szYD_STK_BED_NO;
					
					JDTORecord msgRecord = JDTORecordFactory.getInstance().create();
					msgRecord.setField("YD_INFO_SYNC_CD", "4");				//1:동,2:SPAN,3:열,4:BED
					msgRecord.setField("YD_GP", 			szYD_STK_COL_GP.substring(0, 1));
					msgRecord.setField("YD_STK_COL_GP", 	szYD_STK_COL_GP);
					msgRecord.setField("YD_STK_BED_NO", 	ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO"));
					
					YdCommonUtils.sndStrPosSpecToL2(msgRecord);
				}
			}
			outRecord.setField("RTN_CD" 	, "1");	
			outRecord.setField("RTN_MSG" ,"정상적으로 수정 되었습니다.");	
			return outRecord;


		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
			
		} finally{
		}		
	}	// end of updCoilYdStkPosSetBed	
	
	
	/**
	 *저장위치 좌표설정화면 열 등록
	 * 심명순(090713)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void insCoilYdStkPosSet(JDTORecord [] inDto) throws DAOException {
		
		
		int intRtnVal = 0;
		String szMsg="";
		String szMethodName="insCoilYdStkPosSet";
		
		String szYdStkColGp = null;
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		YdStkColDao ydStkcolDao = new YdStkColDao();	
		
		try {
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
				
				ydUtils.putLog(szSessionName, szMethodName, szMsg,  YdConstant.DEBUG);				
			}

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally{
		}		
	}// end of updCoilYdStkPosSet
	
	/**
	 *저장위치 좌표설정화면 열 등록
	 * 심명순(090713)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void insCoilYdStkPosSetBed(JDTORecord [] inDto) throws DAOException {
		
		
		int intRtnVal = 0;
		String szMsg="";
		String szMethodName="insCoilYdStkPosSetBed";
		
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		YdStkBedDao ydStkbedDao = new YdStkBedDao();
		try {
			//저장위치 좌표설정화면 열 등록.
		
			for(int x=0;x<inDto.length;x++){
				
				//등록  항목 SETTING
				
				//적치열구분 필수 
				recPara.setField("YD_STK_COL_GP", inDto[x].getField("YD_STK_COL_GP").toString().trim());
				
				
				//적치열번호 필수
				recPara.setField("YD_STK_BED_NO", inDto[x].getField("YD_STK_BED_NO"));
				
				//야드저장집합코드 NOT NULL
				recPara.setField("YD_STR_GTR_CD","TESTYD");
				
				recPara.setField("YD_STK_BED_TP",	inDto[x].getField("YD_STK_BED_TP"));
				recPara.setField("YD_STK_BED_L_GP", inDto[x].getField("YD_STK_BED_L_GP"));
				recPara.setField("YD_STK_BED_W_GP", inDto[x].getField("YD_STK_BED_W_GP"));
				recPara.setField("YD_STK_BED_DIR_GP", inDto[x].getField("YD_STK_BED_DIR_GP"));
				recPara.setField("YD_STK_BED_ACT_STAT", inDto[x].getField("YD_STK_BED_ACT_STAT"));
				recPara.setField("YD_STK_BED_WHIO_STAT", inDto[x].getField("YD_STK_BED_WHIO_STAT"));
				recPara.setField("YD_STK_BED_XAXIS", inDto[x].getField("YD_STK_BED_XAXIS"));
				recPara.setField("YD_STK_BED_YAXIS", inDto[x].getField("YD_STK_BED_YAXIS"));
				recPara.setField("YD_STK_BED_ZAXIS", inDto[x].getField("YD_STK_BED_ZAXIS"));
				recPara.setField("YD_STK_BED_LYR_MAX", inDto[x].getField("YD_STK_BED_LYR_MAX"));
				recPara.setField("YD_STK_BED_WT_MAX", inDto[x].getField("YD_STK_BED_WT_MAX"));
				recPara.setField("YD_STK_BED_H_MAX", inDto[x].getField("YD_STK_BED_H_MAX"));
				recPara.setField("YD_STK_BED_L_MAX", inDto[x].getField("YD_STK_BED_L_MAX"));
				recPara.setField("YD_STK_BED_W_MAX", inDto[x].getField("YD_STK_BED_W_MAX"));
				
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
				
				ydUtils.putLog(szSessionName, szMethodName, szMsg,  YdConstant.DEBUG);				
			}

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally{
		}		
	}// end of insCoilYdStkPosSetBed
	
	
	/**
	 * 코일 제품상세정보 조회/검색하는 메소드이다.코일 공통  항목을 조회한다.(검색어, 업무영역코드)
	 * 심명순(090713)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getPtCoilCommInfoji(JDTORecord inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg="";
		String szMethodName="getPtCoilCommInfoji";		
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecordSet rSetStock = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecord recPara = JDTORecordFactory.getInstance().create();

		
	
		try {
			
			YdStockDao ydStockDao = new YdStockDao();		
			recPara.setField("STL_NO", 	inDto.getField("STL_NO"));
			
			//야드 저장품 테이블에서 품목코드를 읽어온다

//getYdStock53			
//PIDEV_S :병행가동용:PI_YD
            recPara.setField("PI_YD",       "J"); 
			intRtnVal = ydStockDao.getYdStock(recPara, rSetStock, 53);
						
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
		return rSetStock;
	} // end of getPtCoilCommInfoji
	
	/**
	 *  후판제품야드주문별재고조회
	 * 심명순(090713)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdOrdInfoStkRef(JDTORecord inDto) throws DAOException {
		
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
				
		String szMsg="";		
		String szMethodName="getCoilYdOrdInfoStkRef";
		
			
		int intRtnVal = 0;
		
		try {
			
			
			recPara.setField("YD_GP", yddatautil.setDataDefault(inDto.getField("YD_GP"), ""));
			recPara.setField("ORD_NO", yddatautil.setDataDefault(inDto.getField("ORD_NO"), ""));
			recPara.setField("ORD_DTL", yddatautil.setDataDefault(inDto.getField("ORD_DTL"), ""));
			recPara.setField("DEST_CD", yddatautil.setDataDefault(inDto.getField("DEST_CD"), ""));
			recPara.setField("YD_GP", yddatautil.setDataDefault(inDto.getField("YD_GP"), ""));
			recPara.setField("CUST_CD", yddatautil.setDataDefault(inDto.getField("CUST_CD"), ""));
			recPara.setField("PAGE_CNT1",inDto.getField("PAGE_NO"));
			recPara.setField("PAGE_CNT2",inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT1",inDto.getField("ROWCOUNT"));
			recPara.setField("ROW_CNT2",inDto.getField("ROWCOUNT"));
			
			
			/* com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdOrdInfoStkRef_PAGE */
			
					
			YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
			
//getYdStklyr62			
			intRtnVal =  ydStkLyrDao.getYdStklyr(recPara, outRecSet,62);
			
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
		
		return outRecSet;
	}//end of getCoilYdOrdInfoStkRef	
	
	
	/**
	 * 코일야드 저장위치별 정보 조회 
	 * 심명순(090713)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilGdsYdStkLocInfoList(JDTORecord inDto) throws DAOException {
		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");

		String szMsg            = "";
		String szMethodName     = "getCoilGdsYdStkLocInfoList";
		int intRtnVal = 0;

		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();

		try {
			//적치열 구분과 적치베드 번호로 분리
			recPara.setField("YD_STK_COL_GP",  yddatautil.setDataDefault(inDto.getField("STOCK_POS"), ""));
			recPara.setField("CURR_PROG_CD",      yddatautil.setDataDefault(inDto.getField("CURR_PROG_CD"), ""));

			/*
			recPara.setField("PAGE_CNT1",inDto.getField("PAGE_NO"));
			recPara.setField("PAGE_CNT2",inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT1",inDto.getField("ROWCOUNT"));
			recPara.setField("ROW_CNT2",inDto.getField("ROWCOUNT"));	
			*/
			
			recPara.setField("PAGE_CNT",inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT",inDto.getField("ROWCOUNT"));
			//intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 63);
			
//!AT		intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 95);
//getYdStklyr300			
//PIDEV_S :병행가동용:PI_YD
			recPara.setField("PI_YD",    	"J");				
			/*"com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrCoilGdsPos_n_PIDEV*/
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 300);
					
			
			if (intRtnVal <= 0) {
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
			ydUtils.putLog(szSessionName, szMethodName, szMsg,  YdConstant.DEBUG);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilGdsYdStkLocInfoList
	
	/**
	 *  차량별 상세 작업관리 조회(조회)
	 *  심명순(090713)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdCarWorkMgtlist(JDTORecord inDto) throws DAOException {
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		YdSchRuleDao ydSchRuleDao = new YdSchRuleDao();
		JDTORecordSet retRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		String     szMsg        = "";
		String     szMethodName = "getCoilYdCarWorkMgtlist";
		JDTORecord revRec       = JDTORecordFactory.getInstance().create();

		String chkWorkStat   = "";
		String carUseGp      = "";
		String out_plant     = "";
		String szCarProgStat = "";
		int intRtnVal = 0;
		
		try {
			recPara.setField("CAR_NO",				yddatautil.setDataDefault(inDto.getField("CAR_NO"), 		  ""));
			recPara.setField("YD_CARLD_STOP_LOC",	yddatautil.setDataDefault(inDto.getField("YD_GP"), 			  ""));
			recPara.setField("YD_CARUD_STOP_LOC",	yddatautil.setDataDefault(inDto.getField("YD_GP"), 			  ""));
			recPara.setField("YD_CAR_PROG_STAT",	yddatautil.setDataDefault(inDto.getField("YD_CAR_PROG_STAT"), ""));
			recPara.setField("PAGE_CNT1",inDto.getField("PAGE_NO"));
			recPara.setField("PAGE_CNT2",inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT1",inDto.getField("ROWCOUNT"));
			recPara.setField("ROW_CNT2",inDto.getField("ROWCOUNT"));
			
//getYdSchrule6		
//PIDEV_S :병행가동용:PI_YD
			recPara.setField("PI_YD",    	yddatautil.setDataDefault(inDto.getField("YD_GP"),""));						
			intRtnVal = ydSchRuleDao.getYdSchrule(recPara, outRecSet, 6);
			
			System.out.println("recPara :============> "+ recPara);
			System.out.println("outRecSet :==========>"+ outRecSet);
			System.out.println("intRtnVal :==========>"+ intRtnVal);
			
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
			do 
			{
				revRec = outRecSet.getRecord();
				
				//차량 사용구분에 따른 차량번호 사용 
				// L : 구내운송 , G : 출하차량 
				
				carUseGp =    yddatautil.setDataDefault(revRec.getField("YD_CAR_USE_GP"), "");
				out_plant =   yddatautil.setDataDefault(revRec.getField("YD_CARLD_STOP_LOC"), "");
				
				if(! out_plant.equals("")){
					out_plant  = out_plant.substring(0,1);
				}
				
				
				//불출공장 
				revRec.setField("OUT_PLANT",out_plant);
				
				
				if ("".equals(carUseGp)){
					revRec.setField("CAR_NO", "차량사용유무없음");
					
				}else if("L".equals(carUseGp)){
					//revRec.setField("CAR_NO", revRec.getField("TRN_EQP_CD"));
				}else if("G".equals(carUseGp)){
					//처리 하지않아도 된다.
					//revRec.setField("CAR_NO", revRec.getField("CAR_NO"));
				}
			
				
				chkWorkStat = yddatautil.setDataDefault(revRec.getField("YD_EQP_WRK_STAT"), "N");

				//상차 정보 SETTING(야드 설비작업상태  U )

				if (chkWorkStat.equals("L"))
					{
						revRec.setField("T_STOP_LOC",revRec.getField("YD_CARLD_STOP_LOC"));
						revRec.setField("T_LEV_DT",  revRec.getField("YD_CARLD_LEV_DT"));
						revRec.setField("T_ARR_DT",  revRec.getField("YD_CARLD_ARR_DT"));
						revRec.setField("T_ST_DT",   revRec.getField("YD_CARLD_ST_DT"));
						revRec.setField("T_CMPL_DT", revRec.getField("YD_CARLD_CMPL_DT"));
					
				} //하차 정보 세팅상차 정보 SETTING(야드 설비작업상태 D)
				else if(chkWorkStat.equals("U")) {
						revRec.setField("T_STOP_LOC", 	revRec.getField("YD_CARUD_STOP_LOC"));
						revRec.setField("T_LEV_DT",   	revRec.getField("YD_CARUD_LEV_DT"));
						revRec.setField("T_ARR_DT", 	revRec.getField("YD_CARUD_ARR_DT"));
						revRec.setField("T_ST_DT", 		revRec.getField("YD_CARUD_ST_DT"));
						revRec.setField("T_CMPL_DT",	revRec.getField("YD_CARUD_CMPL_DT"));
				}
				//else{
					// 야드 설비작업상태  맞지않을경우!!!
				//}
				//RECORDSET에 ADD 
				retRecSet.addRecord(revRec);

			}while(outRecSet.next());

			logger.println(LogLevel.DEBUG_TEXT, "getCoilYdCarWorkMgtlist");

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return retRecSet;
	} //end of getCoilYdCarWorkMgtlist
	
	/**
	 *  코일 야드 차량진행관리 조회 
	 * 심명순(090713)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdCarWorkList(JDTORecord inDto) throws DAOException {
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		YdCarSchDao ydCarschDao = new YdCarSchDao();
		JDTORecordSet retRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		String     szMsg        = "";
		String     szMethodName = "getCoilYdCarWorkList";
		JDTORecord revRec       = JDTORecordFactory.getInstance().create();

		String chkWorkStat   = "";
		String carUseGp      = "";
		String out_plant     = "";
		String szCarProgStat = "";
		int intRtnVal = 0;
		
		try {
			
			recPara.setField("YD_GP1",yddatautil.setDataDefault(inDto.getField("YD_GP"), ""));
			recPara.setField("YD_GP2",yddatautil.setDataDefault(inDto.getField("YD_GP"), ""));
			recPara.setField("YD_EQP_GP",yddatautil.setDataDefault(inDto.getField("CAR_GP"), ""));
			recPara.setField("PAGE_CNT1",inDto.getField("PAGE_NO"));
			recPara.setField("PAGE_CNT2",inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT1",inDto.getField("ROWCOUNT"));
			recPara.setField("ROW_CNT2",inDto.getField("ROWCOUNT"));
			
		
			intRtnVal = ydCarschDao.getYdCarsch(recPara, outRecSet, 12);
			
			System.out.println("recPara :============> "+ recPara);
			System.out.println("outRecSet :==========>"+ outRecSet);
			
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
			do 
			{
				revRec = outRecSet.getRecord();
				
				//차량 사용구분에 따른 차량번호 사용 
				// L : 구내운송 , G : 출하차량 
				
				carUseGp =    yddatautil.setDataDefault(revRec.getField("YD_CAR_USE_GP"), "");
				out_plant =   yddatautil.setDataDefault(revRec.getField("YD_CARLD_STOP_LOC"), "");
				szCarProgStat =  yddatautil.setDataDefault(revRec.getField("YD_CAR_PROG_STAT"), "");
				
				
				
				//불출공장 
				revRec.setField("OUT_PLANT",out_plant);
				
				if(! out_plant.equals("")){
					out_plant = out_plant.substring(0,1);
				}
				
				
				if ("".equals(carUseGp)){
					revRec.setField("CAR_NO", "차량사용유무없음");
					
				}else if("L".equals(carUseGp)){
					revRec.setField("CAR_NO", revRec.getField("TRN_EQP_CD"));
				}else if("G".equals(carUseGp)){
					//처리 하지않아도 된다.
					//revRec.setField("CAR_NO", revRec.getField("CAR_NO"));
				}
			
				
				chkWorkStat = yddatautil.setDataDefault(revRec.getField("YD_EQP_WRK_STAT"), "N");

				//상차 정보 SETTING(야드 설비작업상태  U )
				if (chkWorkStat.equals("L"))
					{
						revRec.setField("T_STOP_LOC",revRec.getField("YD_CARLD_STOP_LOC"));
						revRec.setField("T_LEV_DT",  revRec.getField("YD_CARLD_LEV_DT"));
						revRec.setField("T_ARR_DT",  revRec.getField("YD_CARLD_ARR_DT"));
						revRec.setField("T_ST_DT",   revRec.getField("YD_CARLD_ST_DT"));
						revRec.setField("T_CMPL_DT", revRec.getField("YD_CARLD_CMPL_DT"));
					
				} //하차 정보 세팅상차 정보 SETTING(야드 설비작업상태 D)
				else if(chkWorkStat.equals("U")) {
						revRec.setField("T_STOP_LOC", 	revRec.getField("YD_CARUD_STOP_LOC"));
						revRec.setField("T_LEV_DT",   	revRec.getField("YD_CARUD_LEV_DT"));
						revRec.setField("T_ARR_DT", 	revRec.getField("YD_CARUD_ARR_DT"));
						revRec.setField("T_ST_DT", 		revRec.getField("YD_CARUD_ST_DT"));
						revRec.setField("T_CMPL_DT",	revRec.getField("YD_CARUD_CMPL_DT"));
				}
				//else{
					// 야드 설비작업상태  맞지않을경우!!!
				//}
			
				//RECORDSET에 ADD 
				retRecSet.addRecord(revRec);

			}while(outRecSet.next());

			logger.println(LogLevel.DEBUG_TEXT, "getCoilYdCarWorkList");

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return retRecSet;
	} //end of getCoilYdCarWorkList
	
	/**
	 * 크레인 관리 조회
	 * 심명순(090713)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdCrnWorkMgt(JDTORecord inDto) throws DAOException {
		int         intRtnVal    = 0;
		String      szMsg        = "";
		YdCrnSchDao ydCrnschDao  = new YdCrnSchDao();
		String      szMethodName = "getCoilYdCrnWorkMgt";
		
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");

		JDTORecord recPara = JDTORecordFactory.getInstance().create();
			
		try {
			
			recPara.setField("YD_BAY_GP", 	inDto.getField("YD_BAY_GP"));		
			recPara.setField("YD_SCH_CD", 	inDto.getField("YD_SCH_CD"));							
			recPara.setField("YD_EQP_ID", 	inDto.getField("YD_EQP_ID"));
			recPara.setField("PAGE_CNT1",   inDto.getField("PAGE_NO"));
			recPara.setField("PAGE_CNT2",   inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT1",    inDto.getField("ROWCOUNT"));
			recPara.setField("ROW_CNT2",    inDto.getField("ROWCOUNT"));
//getYdCrnsch35			
			intRtnVal = ydCrnschDao.getYdCrnsch(recPara, outRecSet, 35);
				
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
						
		
			ydUtils.putLog(szSessionName, szMethodName, szMsg,  YdConstant.DEBUG);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		return outRecSet;
	}	// end of getCoilYdCrnWorkMgt
	
	/**
	 *  야드크레인 작업관리 (작업취소)
	 * 심명순(090713)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void cancleWorkCoilYdCrnWorkMgt(JDTORecord[] inDto) throws DAOException {
		String szMsg        = "";	
		String szMethodName = "cancleWorkCoilYdCrnWorkMgt";

		JDTORecord recPara = JDTORecordFactory.getInstance().create();
	
		YdDelegate ydDelegate = new YdDelegate();
		try {
			 
			for(int x=0;x<inDto.length;x++){			
				
				//TC CODE
				//현재 구현된 테스트용 내부전문 YDYD9003
				recPara.setField("JMS_TC_CD","YDYD9003");
				
				//스케줄 ID
				recPara.setField("YD_CRN_SCH_ID",yddatautil.setDataDefault(inDto[x].getField("YD_CRN_SCH_ID"), ""));

				//스케줄 CODE
				recPara.setField("YD_SCH_CD", yddatautil.setDataDefault(inDto[x].getField("YD_SCH_CD"), ""));
				
				//삭제유무 
				recPara.setField("DEL_YN",  "Y");
				
				//수정자 
				recPara.setField("MODIFIER",  "JSPUSER");				
				//ydDelegate.sendMsg(recPara);				
				EJBConnector ejbConn = null;
				
				ejbConn = new EJBConnector("default", this);
				ejbConn.trx("YdSimSeEJB", "wrkCncl", recPara);
			}
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}	// end of cancleWorkCoilYdCrnWorkMgt
	
	/**
	 * 대차작업관리 검색 하는 메소드이다.코일 공통  항목을 조회한다.(검색어, 업무영역코드)
	 * 심명순(090713)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getTcarSchMtlList(JDTORecord inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg="";
		String szMethodName="getYdStock";		
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecordSet rSetStock = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecord recPara = JDTORecordFactory.getInstance().create();

		
	
		try {
			
			YdStockDao ydStockDao = new YdStockDao();		
			//recPara.setField("STL_NO", 	inDto.getField("STL_NO"));
			
			//야드 저장품 테이블에서 품목코드를 읽어온다
//getYdStock57
			intRtnVal = ydStockDao.getYdStock(recPara, rSetStock, 57);
						
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
		return rSetStock;
	} // end of getTcarSchMtlList
	
	/**
	 * 대차작업관리의 대차제품상세 조회/검색하는 메소드이다.코일 공통  항목을 조회한다.(검색어, 업무영역코드)
	 * 심명순(090713)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getTcarWorkMtlList(JDTORecord inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg="";
		String szMethodName="getTcarWorkMtlList";		
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecordSet rSetStock = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecord recPara = JDTORecordFactory.getInstance().create();

		YdStockDao ydStockDao = new YdStockDao();
	
		try {
			recPara.setField("YD_EQP_NAME",yddatautil.setDataDefault(inDto.getField("YD_EQP_NAME"), ""));			
			recPara.setField("PAGE_CNT1",inDto.getField("PAGE_NO"));
			recPara.setField("PAGE_CNT2",inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT1",inDto.getField("ROWCOUNT"));
			recPara.setField("ROW_CNT2",inDto.getField("ROWCOUNT"));			
					
			
			//야드 저장품 테이블에서 품목코드를 읽어온다
//getYdStock58			
			intRtnVal = ydStockDao.getYdStock(recPara, rSetStock, 58);
						
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
		return rSetStock;
	} // end of getTcarWorkMtlList
	
	/**
	 *  열별 저장위치 조회 (단 별)
	 * 심명순 (090715)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdColStkPosLyrGpList(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getCoilYdColStkPosLyrGpList";
		String szOperationName = "열별 저장위치 조회 (단 별)";

		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao(); 
		
		int intRtnVal = 0;
		
		try {
			
			recPara.setField("YD_STK_COL_GP",     yddatautil.setDataDefault(inDto.getField("YD_GP"),         "") 
									           +  yddatautil.setDataDefault(inDto.getField("YD_BAY_GP"),     "")
									           +  yddatautil.setDataDefault(inDto.getField("YD_EQP_GP"),     "")
									           +  yddatautil.setDataDefault(inDto.getField("YD_STK_COL_NO"), ""));
			
			
			
			recPara.setField("YD_STK_LYR_NO",     yddatautil.setDataDefault(inDto.getField("YD_STK_LYR_NO"), ""));
			recPara.setField("DEST_CD1",     yddatautil.setDataDefault(inDto.getField("DEST_CD"), ""));
			recPara.setField("DEST_CD2",     yddatautil.setDataDefault(inDto.getField("DEST_CD"), ""));
		
			
	
			ydUtils.displayRecord(szOperationName, recPara);
//getYdStklyr64			
//PIDEV_S :병행가동용:PI_YD
			recPara.setField("PI_YD",    	yddatautil.setDataDefault(inDto.getField("YD_GP"),         ""));		
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 64);
		
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
			
			logger.println(LogLevel.DEBUG_TEXT, "getCoilYdColStkPosLyrGpList");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilYdColStkPosLyrGpList
	
	/**
	 *  차량작업관리 배차내역  조회 코일 외
	 * 심명순 (090723)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdGdsOutCar(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";
		//String szTemp       = "";
		String szWLOC_CD       = "";
		String szMethodName = "getCoilYdGdsOutCar";
		String szYD_CAR_STOP_LOC = "";
 
		YdCarSchDao ydCarSchDao = new YdCarSchDao(); 
		
		int intRtnVal = 0;
		
		try {
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
				
				/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarAsgnInfoByWlocCd*/
				intRtnVal = ydCarSchDao.getYdCarsch(recPara, outRecSet, 26);
			
				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "오류발생[1] - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}else if (intRtnVal == -2) {
						szMsg = "오류발생[2] - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}else{
						szMsg = "오류발생[3] - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					//return outRecSet;
					return outRecSet;
				}else if(intRtnVal == 0) {
					szMsg = "대상재가 존재하지 않습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg,  YdConstant.DEBUG);
				}// end of if
			}
					
			
			
			
			//레코드셋이 없을때까지 반복한다.
			//outRecSet.first();
			
			logger.println(LogLevel.DEBUG_TEXT, "getCoilYdGdsOutCar");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilYdGdsOutCar
	
	/**
	 *  차량작업관리 작업재료 조회 
	 * 심명순 (090723)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdGdsCarWork(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = null;
		JDTORecordSet    outRecSet       = null;
		JDTORecordSet    temp       = null;
		YdUtils ydUtils 				= new YdUtils();
		
		int intRtnVal 					= 0;
		String szMsg        			= "";		
		String szMethodName 			= "getCoilYdGdsCarWork";
		String szOperationName			= "차량작업재료조회";
		String szRtnMsg					= null;
		
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		YdStockDao		ydStockDao		= new YdStockDao();
		YdCarFtmvMtlDao ydCarFtmvMtlDao = new YdCarFtmvMtlDao();
		
		String szYD_CAR_SCH_ID			= null;
		String szYD_CAR_USE_GP			= null;
		String szTRN_EQP_CD				= null;
		String szCAR_NO					= null;
		String szCARD_NO				= null;
		String szYD_CAR_PROG_STAT		= null;
		String szDEL_YN					= null;
		String szSPOS_WLOC_CD			= null;
		String szYD_GP					= null;
		String szTRANS_ORD_DATE			= null;
		String szTRANS_ORD_SEQNO		= null;
		String szYD_CARLD_WRK_BOOK_ID = null;
		try {
			
			//-----------------------------------------------------------------------------------------------------------------
			// 파라미터 확인
			//-----------------------------------------------------------------------------------------------------------------
			szMsg = "[JSP Session - "+szOperationName+"] 메소드 시작 - 파라미터 확인";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			ydUtils.displayRecord(szOperationName, inDto);
			
			//-----------------------------------------------------------------------------------------------------------------
			
			
			//-----------------------------------------------------------------------------------------------------------------
			// 차량스케줄 조회
			//-----------------------------------------------------------------------------------------------------------------
			szYD_CAR_SCH_ID = yddatautil.setDataDefault(inDto.getField("YD_CAR_SCH_ID"), "");
			
			
			outRecSet       = JDTORecordFactory.getInstance().createRecordSet("");
			szRtnMsg = YdCommonUtils.getCarSchByCarSchId(szYD_CAR_SCH_ID, outRecSet);
			
			//-----------------------------------------------------------------------------------------------------------------
			
			
			//-----------------------------------------------------------------------------------------------------------------
			// 차량스케줄이 존재하면 차량작업내역 조회
			//-----------------------------------------------------------------------------------------------------------------
			if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				
				outRecSet.first();
				recPara = outRecSet.getRecord();
				
				szDEL_YN = yddatautil.setDataDefault(recPara.getField("DEL_YN"), "");
				
				
				//-----------------------------------------------------------------------------------------------------------------
				// 차량스케줄이 삭제되지 않은 경우에는 차량작업내역 조회
				//-----------------------------------------------------------------------------------------------------------------
				if( szDEL_YN.equals("N") ) {
					
					szSPOS_WLOC_CD			= yddatautil.setDataDefault(recPara.getField("SPOS_WLOC_CD"), "");
					szYD_CAR_USE_GP 		= yddatautil.setDataDefault(recPara.getField("YD_CAR_USE_GP"), "");
					szTRN_EQP_CD 			= yddatautil.setDataDefault(recPara.getField("TRN_EQP_CD"), "");
					szCAR_NO 				= yddatautil.setDataDefault(recPara.getField("CAR_NO"), "");
					szCARD_NO 				= yddatautil.setDataDefault(recPara.getField("CARD_NO"), "");
					szYD_CAR_PROG_STAT		= yddatautil.setDataDefault(recPara.getField("YD_CAR_PROG_STAT"), "");
					szTRANS_ORD_DATE		= yddatautil.setDataDefault(recPara.getField("TRANS_ORD_DATE"), "");
					szTRANS_ORD_SEQNO		= yddatautil.setDataDefault(recPara.getField("TRANS_ORD_SEQNO"), "");
					
					szYD_GP = YdCommonUtils.getYdFromWlocCd(szSPOS_WLOC_CD);
					
					szYD_CARLD_WRK_BOOK_ID = yddatautil.setDataDefault(recPara.getField("YD_CARLD_WRK_BOOK_ID"), "");
				
					recPara         = JDTORecordFactory.getInstance().create();
					outRecSet       = JDTORecordFactory.getInstance().createRecordSet("");
					
					if( szYD_CAR_PROG_STAT.equals(YdConstant.YD_CARLD_LEV) && szYD_CAR_USE_GP.equals(YdConstant.YD_CAR_USE_GP_DM)) {
						//-----------------------------------------------------------------------------------------------------------------
						//	출하차량이고 상차출발이면 운송지시일자와 운송지시순번으로 조회
						//-----------------------------------------------------------------------------------------------------------------
						szMsg = "[JSP Session - "+szOperationName+"] 출하차량이고 상차출발이면 운송지시일자와 운송지시순번으로 조회 시작";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						recPara.setField("YD_STK_COL_GP",		szYD_GP + "_");
						recPara.setField("TRANS_ORD_DATE",		szTRANS_ORD_DATE);
						recPara.setField("TRANS_ORD_SEQNO",		szTRANS_ORD_SEQNO);
						recPara.setField("CAR_NO",				szCAR_NO);		
						recPara.setField("CARD_NO",				szCARD_NO);
//getYdStock128						
//PIDEV_S :병행가동용:PI_YD
						recPara.setField("PI_YD",    	szYD_GP);		
						intRtnVal	= ydStockDao.getYdStock(recPara, outRecSet, 128);
						
						szMsg = "[JSP Session - "+szOperationName+"] 출하차량이고 상차출발이면 운송지시일자와 운송지시순번으로 조회 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}else if( szYD_CAR_PROG_STAT.equals(YdConstant.YD_CARLD_CMPL) ) {			
						//-----------------------------------------------------------------------------------------------------------------
						//	상차완료인 경우는 차량이송재료를 조회
						//	수정자 : 임춘수
						//	수정일 : 2010.02.01
						//-----------------------------------------------------------------------------------------------------------------
						szMsg = "[JSP Session - "+szOperationName+"] 상차완료인 차량스케줄["+szYD_CAR_SCH_ID+"]의 차량이송재료 조회 시작";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						recPara.setField("YD_CAR_SCH_ID",		szYD_CAR_SCH_ID);
//getYdCarftmvmtl12						
						intRtnVal	= ydCarFtmvMtlDao.getYdCarftmvmtl(recPara, outRecSet, 12);
						
						szMsg = "[JSP Session - "+szOperationName+"] 상차완료인 차량스케줄["+szYD_CAR_SCH_ID+"]의 차량이송재료 조회 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						//-----------------------------------------------------------------------------------------------------------------
					}else{
						szMsg = "[JSP Session - "+szOperationName+"] 대상재 조회 시작";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						recPara.setField("YD_CAR_USE_GP",		szYD_CAR_USE_GP);
						recPara.setField("TRN_EQP_CD",			szTRN_EQP_CD);
						recPara.setField("CAR_NO",				szCAR_NO);
						recPara.setField("CARD_NO",				szCARD_NO);
//getYdWrkbookmtl26		
						// 슬라브 야드의 경우 분리
						if(szSPOS_WLOC_CD.equals("DHY21")
						 ||szSPOS_WLOC_CD.equals("DJY25") //(비상야드추가)
						 ||szSPOS_WLOC_CD.equals("DYY15")
						 ||szSPOS_WLOC_CD.equals("BSY01")
						 ||szSPOS_WLOC_CD.equals("BSY02")
						 ||szSPOS_WLOC_CD.equals("BSY03")
						 ||szSPOS_WLOC_CD.equals("DKY23")
						 ||szSPOS_WLOC_CD.equals("DWY23")
						 ||szSPOS_WLOC_CD.equals("DKY21")
						 ||szSPOS_WLOC_CD.equals("DWY22")
						 ||szSPOS_WLOC_CD.equals("DJY24")){
							
							/*
							 * 2020.01.13 추가
							 * 이적상차인경우 기존 작업예약과 JOIN하여 보여주던 기능은 맞지 않는다.
							 * 이적을 두번에 나눠서 하면 한 상차 작업에 작업예약이 2개
							 * 따라서, 구내운송에 상차완료 전상태이고, 상차 작업예약이 없거나, 스케줄 코드 검색하여 이적상차인경우 차량이송재료 table 에서 조회.
							 * 
							 * */
							if("L".equals(szYD_CAR_USE_GP) && (//구내운송이고 상차인 경우 상차완료 전까지
									YdConstant.YD_CARLD_LEV.equals(szYD_CAR_PROG_STAT)
									|| YdConstant.YD_CARLD_ARR.equals(szYD_CAR_PROG_STAT)
									|| YdConstant.YD_CARLD_CHK.equals(szYD_CAR_PROG_STAT)
									|| YdConstant.YD_CARLD_ST.equals(szYD_CAR_PROG_STAT))){
								if("".equals(szYD_CARLD_WRK_BOOK_ID)){
									//차량작업재료로 조회
									recPara.setField("YD_CAR_SCH_ID",		szYD_CAR_SCH_ID);
									//getYdCarftmvmtl12						
									//intRtnVal	= ydCarFtmvMtlDao.getYdCarftmvmtl(recPara, outRecSet, 12);
									intRtnVal  = ydWrkbookMtlDao.getYdWrkbookmtl(recPara, outRecSet, 271);
								}
								else {
									//이적상차인지 검사
									temp       = JDTORecordFactory.getInstance().createRecordSet("");
									intRtnVal  = ydWrkbookMtlDao.getYdWrkbookmtl(recPara, temp, 272);
									if(intRtnVal>0){
										String manualYN = temp.getRecord(0).getFieldString("M_WRK_GP");
										szMsg = "[JSP Session - "+szOperationName+"] 이적상차 여부 : "+ manualYN ;
										ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
										
										if("Y".equals(manualYN)){
											recPara.setField("YD_CAR_SCH_ID",		szYD_CAR_SCH_ID);
											//getYdCarftmvmtl12						
											intRtnVal	= ydWrkbookMtlDao.getYdWrkbookmtl(recPara, outRecSet, 271);
										}
										else{
											intRtnVal  = ydWrkbookMtlDao.getYdWrkbookmtl(recPara, outRecSet, 26);
										}
									}
									
									
								}
							}else{
								intRtnVal  = ydWrkbookMtlDao.getYdWrkbookmtl(recPara, outRecSet, 26);
					
							}
							
							//intRtnVal  = ydWrkbookMtlDao.getYdWrkbookmtl(recPara, outRecSet, 26);
						}//end if slab yard
						else
						{
							intRtnVal  = ydWrkbookMtlDao.getYdWrkbookmtl(recPara, outRecSet, 400);							
						}
						
//						intRtnVal  = ydWrkbookMtlDao.getYdWrkbookmtl(recPara, outRecSet, 26);
						
											
						szMsg = "[JSP Session - "+szOperationName+"] 대상재 조회 완료";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
				
					if (intRtnVal <= 0) {
						if (intRtnVal == -1) {
							szMsg = "[JSP Session - "+szOperationName+"] 작업재료 조회 오류발생 1 : 반환값 - " + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						}else if (intRtnVal == 0) {
							szMsg = "[JSP Session - "+szOperationName+"] 작업재료 조회 대상재가 존재하지 않음 : 반환값 - " + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						} else {
							szMsg = "[JSP Session - "+szOperationName+"] 작업재료 조회 오류발생 2 : 반환값 - " + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						}
						//return outRecSet;
						return outRecSet;
					} // end of if
					
					szMsg = "[JSP Session - "+szOperationName+"] 작업재료 조회 성공 : 레코드 수 - " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}else{
					szMsg = "[JSP Session - "+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"]이 삭제되었습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					throw new DAOException(YdConstant.RETN_CD_NOTEXIST);
				}
				//-----------------------------------------------------------------------------------------------------------------
			}else{
				szMsg = "[JSP Session - "+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"] 조회 시 오류발생";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new DAOException(szRtnMsg);
			}
			//-----------------------------------------------------------------------------------------------------------------
			
			
			szMsg = "[JSP Session - "+szOperationName+"] 메소드 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(DAOException ex) {
			throw ex;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilYdGdsCarWork


	
	/**
	 *  차량작업관리 차량스케줄 조회 코일외
	 * 심명순 (090727)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdGdsCarSch(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		//String szTemp     ="";
		String szMethodName = "getCoilYdGdsCarSch";
		String szYdBay = null;
		String szYd = "";
		String szBay = "";
		String szWLOC_CD = "";
		String szWORK_GBN = "";
		String szUSER_ID = "";

		YdCarSchDao ydCarSchDao = new YdCarSchDao(); 
		
		int intRtnVal = 0;
		
		try {
					
			szWLOC_CD 	= yddatautil.setDataDefault(inDto.getField("WLOC_CD"), "");
			szYdBay   	= yddatautil.setDataDefault(inDto.getField("BAY"), "");
			szWORK_GBN  = yddatautil.setDataDefault(inDto.getField("WORK_GBN"), "");
			szUSER_ID  = yddatautil.setDataDefault(inDto.getField("YD_USER_ID"), "");
			if( szYdBay.length() > 1 ) {
				szYd = szYdBay.substring(0, 1);
				szBay = szYdBay.substring(1, 2);
			}else{
				szYd = szYdBay;
				szBay = "";
			}
			
			if(szWLOC_CD.equals("") || szWLOC_CD.length() != 5 ){
				return outRecSet ; 
			}
			
			recPara.setField("WLOC_CD",szWLOC_CD);
			recPara.setField("YD_GP", szYd);
			recPara.setField("YD_BAY_GP", szBay);
			recPara.setField("USER_ID", szUSER_ID);
			recPara.setField("WORK_GBN", szWORK_GBN);
			
			szMsg = "개소코드  : " + szWLOC_CD + ", 야드구분 : " + szYd + ", 동구분 : " + szBay+ ", 정렬순 : " + szWORK_GBN+ ", 유저아이디 : "+ szUSER_ID;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            /*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarSchByWlocCdCoil*/
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, outRecSet, 300);
		
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
			//outRecSet.first();
			
			logger.println(LogLevel.DEBUG_TEXT, "getCoilYdGdsCarSch");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilYdGdsCarSch
	
	/**
	 * 포인트개폐처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 */
	public void procCoilYdGdsPntUnitCL(JDTORecord [] inDto) {
		int       	intRtnVal    		= 0;
		String    	szMsg        		= null;
		String    	szMethodName 		= "procCoilYdGdsPntUnitCL";
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

		String szYD_STK_COL_GP			= null;
		String szYD_STK_COL_ACT_STAT	= null;
		String szYD_STK_COL_ACT_STAT_PARAM	= null;
		
		String szPI_YD	= null;
		String szPI_YD1	= null;
		
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
				
				//PIDEV_S :병행가동용:PI_YD
				szPI_YD = yddatautil.setDataDefault(inDto[x].getField("PI_YD"),     "*");
				
				recPara.setField("YD_STK_COL_GP",   		szYD_STK_COL_GP);
				
//PIDEV_S :병행가동용:PI_YD
				recPara.setField("PI_YD",   		szPI_YD);
				
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
					
					intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0048");	
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
	 * 입동순서 바꾸기
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 */
	public void procCoilYdGdsBayInWoSeqChang(JDTORecord [] inDto) {
		int       intRtnVal    = 0;
		String    szMsg        = null;
		String    syd_car_sch_id = "";
		String    szMethodName = "procCoilYdGdsBayInWoSeqChang";
		YdUtils ydUtils = new YdUtils();
				
		JDTORecord    recPara  = JDTORecordFactory.getInstance().create();	
		YdCarSchDao ydCarSchDao = new YdCarSchDao(); 
		
		szMsg        = "";

		ydUtils.putLog(szSessionName, szMethodName,  "procCoilYdGdsBayInWoSeqChang() IN", YdConstant.DEBUG);

		try {
			
				// 수정
			for(int x=0;x<inDto.length;x++){					
				for(int i=1;i<=15;i++){
					syd_car_sch_id = yddatautil.setDataDefault(inDto[x].getField("YD_CAR_SCH_ID"+i),     "");
					
					if(!syd_car_sch_id.equals("")){
					
						recPara.setField("YD_CAR_SCH_ID"	, yddatautil.setDataDefault(inDto[x].getField("YD_CAR_SCH_ID"+i),     ""));
						recPara.setField("YD_BAYIN_WO_SEQ"	, yddatautil.setDataDefault(inDto[x].getField("YD_BAYIN_WO_SEQ"+i),     ""));
						recPara.setField("MODIFIER"			, yddatautil.setDataDefault(inDto[x].getFieldString("YD_USER_ID"),""));
						/*com.inisteel.cim.yd.dao.ydcarschdao.ydCarSchDao.updYdCarschYdBayinWoSeq*/
				        //intRtnVal = ydCarSchDao.updYdCarsch(recPara,0);
						intRtnVal = ydCarSchDao.updYdCarschYdCarWrkBookId(recPara,303);
					}
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
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		ydUtils.putLog(szSessionName, szMethodName,  "procCoilYdGdsBayInWoSeqChang() OUT", YdConstant.DEBUG);
	}	// end of procCoilYdGdsBayInWoSeqChang
	
	
	
	/**
	 * 차량 작업 관리 화면 배차등록 - 차량스케줄 생성 후판에서 사용
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return 
	 * @return 
	 * @return
	 * @throws DAOException
	 */
	
	public String uptCarSch(JDTORecord [] inDto) throws DAOException {
		int       intRtnVal    = 0;
		JDTORecordSet    outRecSet       = null;
		JDTORecordSet    rsTemp       = null;
		JDTORecord recTemp = null;
		JDTORecord recPara = null;
		String szMsg        = "";		
		String szMethodName = "uptCarSch";
		String szCRUD = null;
		String szYD_CAR_USE_GP = null;
		String szCAR_NO = null;
		String szCARD_NO = null;
		String szTRN_EQP_CD = null;
		String szYD_CAR_SCH_ID = null;
		String szYD_CAR_PROG_STAT = null;
		String szCurrDate = null;
		String szRtnMsg = YdConstant.RETN_CD_SUCCESS;
		String szYD_CAR_STOP_LOC	= null;
		String szYD_PNT_CD			= null;
		
		YdStkColDao	ydStkColDao	= new YdStkColDao();
		YdCarSchDao ydCarSchDao = new YdCarSchDao();
		//YdCarSpecDao ydCarSpecDao = new YdCarSpecDao();
		
		try {
			szMsg = "[JSP Session - 배차등록] 메소드 시작 - 로우건수 : " + inDto.length;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			recTemp = JDTORecordFactory.getInstance().create();
			//여러건의 로우를 처리 - 등록 및 수정
			for(int i = 0; i < inDto.length; i++ ) {
				recPara         = JDTORecordFactory.getInstance().create();
				//항목 편집
				szYD_CAR_SCH_ID = yddatautil.setDataDefault(inDto[i].getField("YD_CAR_SCH_ID"), "");
				szYD_CAR_USE_GP = yddatautil.setDataDefault(inDto[i].getField("YD_CAR_USE_GP"), "");
				recPara.setField("YD_CAR_SCH_ID",szYD_CAR_SCH_ID);
				recPara.setField("YD_CAR_REG_SEQ",yddatautil.setDataDefault(inDto[i].getField("YD_CAR_REG_SEQ"), ""));
				recPara.setField("YD_EQP_ID",yddatautil.setDataDefault(inDto[i].getField("YD_EQP_ID"), ""));
				recPara.setField("YD_CAR_USE_GP",szYD_CAR_USE_GP);
				if( szYD_CAR_USE_GP.equals("L")) {
					szCAR_NO = "";
					szCARD_NO = yddatautil.setDataDefault(inDto[i].getField("CARD_NO"), "");
					szTRN_EQP_CD = yddatautil.setDataDefault(inDto[i].getField("TRN_EQP_CD"), "");
				}else if(szYD_CAR_USE_GP.equals("G")) {
					szCAR_NO = yddatautil.setDataDefault(inDto[i].getField("CAR_NO"), "");
					szCARD_NO = yddatautil.setDataDefault(inDto[i].getField("CARD_NO"), "");
					szTRN_EQP_CD = "";
				}
				
				if(szYD_CAR_USE_GP.equals("G")) {
					recPara.setField("CAR_KIND","TR");
				}else{
					recPara.setField("CAR_KIND","PT");
				}
				
				szYD_CAR_STOP_LOC = yddatautil.setDataDefault(inDto[i].getField("YD_CAR_STOP_LOC"), "");
				
				szMsg = "[JSP Session - 배차등록] 차량정지POINT["+szYD_CAR_STOP_LOC+"]를 POINT코드로 변경을 위해 조회 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				rsTemp = JDTORecordFactory.getInstance().createRecordSet("");
				recTemp.setField("YD_STK_COL_GP", szYD_CAR_STOP_LOC);
				
				//getYdStkcol0								
				intRtnVal = ydStkColDao.getYdStkcol(recTemp, rsTemp, 0);
				
				if( intRtnVal == 0 ) {
					szMsg = "[JSP Session - 배차등록] 차량정지POINT["+szYD_CAR_STOP_LOC+"]를 POINT코드로 변경을 위해 조회 시 존재하지 않음";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					return YdConstant.RETN_CD_NOTEXIST;
				}else if( intRtnVal < 0 ) {
					szMsg = "[JSP Session - 배차등록] 차량정지POINT["+szYD_CAR_STOP_LOC+"]를 POINT코드로 변경을 위해 조회 시 - 오류발생[반환값:"+intRtnVal+"]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					return YdConstant.RETN_CD_FAILURE;
				}
				
				rsTemp.first();
				recTemp = rsTemp.getRecord();
				
				szYD_PNT_CD = yddatautil.setDataDefault(recTemp.getField("YD_PNT_CD"), "");
				
				szMsg = "[JSP Session - 배차등록] 차량정지POINT["+szYD_CAR_STOP_LOC+"]를 POINT코드["+szYD_PNT_CD+"]로 변경을 위해 조회 성공";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				recPara.setField("CARD_NO",szCARD_NO);
				recPara.setField("CAR_NO",szCAR_NO);
				recPara.setField("TRN_EQP_CD",szTRN_EQP_CD);
				recPara.setField("YD_EQP_WRK_STAT", "U");
				String sYD_CAR_PROG_STAT = yddatautil.setDataDefault(inDto[i].getField("YD_CAR_PROG_STAT"), "");
				
				//상차진행 인 경우 에만 추가 한다.
				if(	sYD_CAR_PROG_STAT.equals("1")||
					sYD_CAR_PROG_STAT.equals("2")||
					sYD_CAR_PROG_STAT.equals("4")||
					sYD_CAR_PROG_STAT.equals("5")){
					recPara.setField("SPOS_WLOC_CD",yddatautil.setDataDefault(inDto[i].getField("WLOC_CD2"), ""));
				}
				recPara.setField("ARR_WLOC_CD",yddatautil.setDataDefault(inDto[i].getField("ARR_WLOC_CD"), ""));
				recPara.setField("DEST_TEL_NO",yddatautil.setDataDefault(inDto[i].getField("DEST_TEL_NO"), ""));
				recPara.setField("YD_CARLD_STOP_LOC", szYD_CAR_STOP_LOC);
				recPara.setField("YD_PNT_CD1", szYD_PNT_CD);
				szYD_CAR_PROG_STAT = yddatautil.setDataDefault(inDto[i].getField("YD_CAR_PROG_STAT"), "");
				szCurrDate = YdUtils.getCurDate("yyyyMMddHHmmss");
				recPara.setField("YD_CAR_PROG_STAT",yddatautil.setDataDefault(inDto[i].getField("YD_CAR_PROG_STAT"), ""));
				recPara.setField("YD_WRK_ALW_L",yddatautil.setDataDefault(inDto[i].getField("YD_WRK_ALW_L"), ""));
				recPara.setField("YD_WRK_ALW_W",yddatautil.setDataDefault(inDto[i].getField("YD_WRK_ALW_W"), ""));
				recPara.setField("YD_WRK_ALW_SKID_PITCH",yddatautil.setDataDefault(inDto[i].getField("YD_WRK_ALW_SKID_PITCH"), ""));
				recPara.setField("YD_WRK_ALW_SH",yddatautil.setDataDefault(inDto[i].getField("YD_WRK_ALW_SH"), ""));
				recPara.setField("YD_WRK_ALW_WT",yddatautil.setDataDefault(inDto[i].getField("YD_WRK_ALW_WT"), ""));
				
				szCRUD = yddatautil.setDataDefault(inDto[i].getField("CRUD"), "");
				//System.out.println("YdCarSpec_update_recPara==>>"+recPara);
				szMsg = "[JSP Session]배차등록 - 차량스케줄정보 : YD_EQP_ID - " + yddatautil.setDataDefault(inDto[i].getField("YD_EQP_ID"), "");
				szMsg += ", YD_CAR_SCH_ID - " + szYD_CAR_SCH_ID;
				szMsg += ", YD_CAR_USE_GP - " + yddatautil.setDataDefault(inDto[i].getField("YD_CAR_USE_GP"), "");
				szMsg += ", TRN_EQP_CD - " + yddatautil.setDataDefault(inDto[i].getField("TRN_EQP_CD"), "");
				szMsg += ", CRUD - " + szCRUD;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//차량스케줄 등록 시
				if( szCRUD.equals("C") ) {
					
					//차량스케줄 등록 시는 넘겨진 차량정보로 차량스케줄이 존재하는 지를 먼저 확인 필요
					szMsg = "[JSP Session]배차등록 - ("+(i+1)+") 차량스케줄 등록전 같은 차량 정보로 차량스케줄 존재하는 지 확인 전";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					outRecSet       = JDTORecordFactory.getInstance().createRecordSet("");
//PIDEV_S :병행가동용:PI_YD
					recPara.setField("PI_YD",    	szYD_CAR_STOP_LOC.substring(0,1));								
					intRtnVal = ydCarSchDao.getYdCarsch(recPara, outRecSet, 27);
					
					if( intRtnVal < 0 ) {
						szMsg = "[JSP Session]배차등록 - ("+(i+1)+") 차량스케줄 등록전 같은 차량 정보로 차량스케줄 존재하는 지 확인 시 오류발생 - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						szRtnMsg = YdConstant.RETN_CD_FAILURE;
						continue;
					}else if( intRtnVal > 0 ) {
						szMsg = "[JSP Session]배차등록 - ("+(i+1)+") 차량스케줄 등록전 같은 차량 정보로 차량스케줄 존재하므로 등록 불가 - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						szRtnMsg = YdConstant.RETN_CD_EXIST;
						continue;
					}
					
					recPara.setField("YD_CARLD_LEV_DT", szCurrDate);			//상차출발일시
					if( szYD_CAR_PROG_STAT.equals("2")) {						//차량상차도착
						recPara.setField("YD_CARLD_ARR_DT", szCurrDate);		//상차도착일시
					}
					
					//차량스케줄 등록
					szMsg = "[JSP Session]배차등록 - ("+(i+1)+") 차량스케줄 등록 가능";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					recPara.setField("REGISTER",yddatautil.setDataDefault(inDto[i].getField("YD_USER_ID"), ""));
					intRtnVal = ydCarSchDao.insYdCarsch(recPara);
					
					if (intRtnVal < 0) {
						if (intRtnVal == -1) {
							szMsg = "[JSP Session]배차등록 - 차량스케줄 등록 시 오류1 - 리턴값 : " + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						} else {
							szMsg = "[JSP Session]배차등록 - 차량스케줄 등록 시 오류2 - 리턴값 : " + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						}
						szRtnMsg = YdConstant.RETN_CD_FAILURE;
					}else{
						szMsg = "[JSP Session] 배차등록 - ("+(i+1)+")차량스케줄 등록 성공";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
					
				}
				//차량스케줄 수정 시
				else if( szCRUD.equals("U") ) {
					szMsg = "[JSP Session]배차등록 - ("+(i+1)+") 차량스케줄 수정 전";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					recPara.setField("MODIFIER",yddatautil.setDataDefault(inDto[i].getField("YD_USER_ID"), ""));
					//차량스케줄 수정
					intRtnVal = ydCarSchDao.updYdCarsch(recPara, 0);
					
					if (intRtnVal < 0) {
						if (intRtnVal == -1) {
							szMsg = "[JSP Session]배차등록 - 차량스케줄 수정 시 오류1 - 리턴값 : " + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						} else {
							szMsg = "[JSP Session]배차등록 - 차량스케줄 수정 시 오류2 - 리턴값 : " + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						}
						szRtnMsg = YdConstant.RETN_CD_FAILURE;
					}else{
						szMsg = "[JSP Session]배차등록 - ("+(i+1)+")차량스케줄 수정 성공";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
				}
				//intRtnVal = ydCarSpecDao.updYdCarspec(recPara,  0);				
				
				//System.out.println("inDto[0]에 값 =="+inDto[0]);
				//mkYardCarSch(inDto[0]);
			}
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return szRtnMsg;
	}//end of uptCarSch
	
	/**
	 * 차량 작업 관리 화면 배차등록 - 차량스케줄 생성 코일에서 사용
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return 
	 * @return 
	 * @return
	 * @throws DAOException
	 */
	
	public String uptCarSchCoil(JDTORecord [] inDto) throws DAOException {
		int       intRtnVal    = 0;
		JDTORecordSet    outRecSet       = null;
		JDTORecordSet    rsTemp       = null;
		JDTORecord recTemp = null;
		JDTORecord recPara = null;
		String szMsg        = "";		
		String szMethodName = "uptCarSchCoil";
		String szCRUD = null;
		String szYD_CAR_USE_GP = null;
		String szCAR_NO = null;
		String szCARD_NO = null;
		String szTRN_EQP_CD = null;
		String szYD_CAR_SCH_ID = null;
		String szYD_CAR_PROG_STAT = null;
		String szCurrDate = null;
		String szRtnMsg = YdConstant.RETN_CD_SUCCESS;
		String szYD_CAR_STOP_LOC	= null;
		String szYD_PNT_CD			= null;
		
		YdStkColDao	ydStkColDao	= new YdStkColDao();
		YdCarSchDao ydCarSchDao = new YdCarSchDao();
		//YdCarSpecDao ydCarSpecDao = new YdCarSpecDao();
		
		try {
			szMsg = "[JSP Session - 배차등록] 메소드 시작 - 로우건수 : " + inDto.length;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			recTemp = JDTORecordFactory.getInstance().create();
			//여러건의 로우를 처리 - 등록 및 수정
			for(int i = 0; i < inDto.length; i++ ) {
				recPara         = JDTORecordFactory.getInstance().create();
				//항목 편집
				szYD_CAR_SCH_ID = yddatautil.setDataDefault(inDto[i].getField("YD_CAR_SCH_ID"), "");
				szYD_CAR_USE_GP = yddatautil.setDataDefault(inDto[i].getField("YD_CAR_USE_GP"), "");
				recPara.setField("YD_CAR_SCH_ID",szYD_CAR_SCH_ID);
				recPara.setField("YD_CAR_REG_SEQ",yddatautil.setDataDefault(inDto[i].getField("YD_CAR_REG_SEQ"), ""));
				recPara.setField("YD_EQP_ID",yddatautil.setDataDefault(inDto[i].getField("YD_EQP_ID"), ""));
				recPara.setField("YD_CAR_USE_GP",szYD_CAR_USE_GP);
				
				if( szYD_CAR_USE_GP.equals("L")) {
					szCAR_NO = "";
					szCARD_NO = yddatautil.setDataDefault(inDto[i].getField("CARD_NO"), "");
					szTRN_EQP_CD = yddatautil.setDataDefault(inDto[i].getField("TRN_EQP_CD"), "");
				}else if(szYD_CAR_USE_GP.equals("G")) {
					szCAR_NO = yddatautil.setDataDefault(inDto[i].getField("CAR_NO"), "");
					szCARD_NO = yddatautil.setDataDefault(inDto[i].getField("CARD_NO"), "");
					szTRN_EQP_CD = "";
				}
				
				if(szYD_CAR_USE_GP.equals("G")) {
					recPara.setField("CAR_KIND","TR");
				}else{
					recPara.setField("CAR_KIND","PT");
				}
				
				szYD_CAR_STOP_LOC = yddatautil.setDataDefault(inDto[i].getField("YD_CAR_STOP_LOC"), "");
				
				szMsg = "[JSP Session - 배차등록] 차량정지POINT["+szYD_CAR_STOP_LOC+"]를 POINT코드로 변경을 위해 조회 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				rsTemp = JDTORecordFactory.getInstance().createRecordSet("");
				recTemp.setField("YD_STK_COL_GP", szYD_CAR_STOP_LOC);

//getYdStkcol0				
				intRtnVal = ydStkColDao.getYdStkcol(recTemp, rsTemp, 0);
				
				if( intRtnVal == 0 ) {
					szMsg = "[JSP Session - 배차등록] 차량정지POINT["+szYD_CAR_STOP_LOC+"]를 POINT코드로 변경을 위해 조회 시 존재하지 않음";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					return YdConstant.RETN_CD_NOTEXIST;
				}else if( intRtnVal < 0 ) {
					szMsg = "[JSP Session - 배차등록] 차량정지POINT["+szYD_CAR_STOP_LOC+"]를 POINT코드로 변경을 위해 조회 시 - 오류발생[반환값:"+intRtnVal+"]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					return YdConstant.RETN_CD_FAILURE;
				}
				
				rsTemp.first();
				recTemp = rsTemp.getRecord();
				
				szYD_PNT_CD = yddatautil.setDataDefault(recTemp.getField("YD_PNT_CD"), "");
				
				szMsg = "[JSP Session - 배차등록] 차량정지POINT["+szYD_CAR_STOP_LOC+"]를 POINT코드["+szYD_PNT_CD+"]로 변경을 위해 조회 성공";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				recPara.setField("CARD_NO",szCARD_NO);
				recPara.setField("CAR_NO",szCAR_NO);
				recPara.setField("TRN_EQP_CD",szTRN_EQP_CD);
				recPara.setField("YD_EQP_WRK_STAT", "U");
				recPara.setField("SPOS_WLOC_CD",yddatautil.setDataDefault(inDto[i].getField("WLOC_CD2"), ""));
				recPara.setField("ARR_WLOC_CD",yddatautil.setDataDefault(inDto[i].getField("ARR_WLOC_CD"), ""));
				recPara.setField("DEST_TEL_NO",yddatautil.setDataDefault(inDto[i].getField("DEST_TEL_NO"), ""));
				recPara.setField("YD_CARLD_STOP_LOC", szYD_CAR_STOP_LOC);
				recPara.setField("YD_PNT_CD1", szYD_PNT_CD);
				szYD_CAR_PROG_STAT = yddatautil.setDataDefault(inDto[i].getField("YD_CAR_PROG_STAT"), "");
				
				szCurrDate = YdUtils.getCurDate("yyyyMMddHHmmss");
				
				recPara.setField("YD_CAR_PROG_STAT",yddatautil.setDataDefault(inDto[i].getField("YD_CAR_PROG_STAT"), ""));
				recPara.setField("YD_WRK_ALW_L",yddatautil.setDataDefault(inDto[i].getField("YD_WRK_ALW_L"), ""));
				recPara.setField("YD_WRK_ALW_W",yddatautil.setDataDefault(inDto[i].getField("YD_WRK_ALW_W"), ""));
				recPara.setField("YD_WRK_ALW_SKID_PITCH",yddatautil.setDataDefault(inDto[i].getField("YD_WRK_ALW_SKID_PITCH"), ""));
				recPara.setField("YD_WRK_ALW_SH",yddatautil.setDataDefault(inDto[i].getField("YD_WRK_ALW_SH"), ""));
				recPara.setField("YD_WRK_ALW_WT",yddatautil.setDataDefault(inDto[i].getField("YD_WRK_ALW_WT"), ""));
				
				szCRUD = yddatautil.setDataDefault(inDto[i].getField("CRUD"), "");
				//System.out.println("YdCarSpec_update_recPara==>>"+recPara);
				szMsg = "[JSP Session]배차등록 - 차량스케줄정보 : YD_EQP_ID - " + yddatautil.setDataDefault(inDto[i].getField("YD_EQP_ID"), "");
				szMsg += ", YD_CAR_SCH_ID - " + szYD_CAR_SCH_ID;
				szMsg += ", YD_CAR_USE_GP - " + yddatautil.setDataDefault(inDto[i].getField("YD_CAR_USE_GP"), "");
				szMsg += ", TRN_EQP_CD - " + yddatautil.setDataDefault(inDto[i].getField("TRN_EQP_CD"), "");
				szMsg += ", CRUD - " + szCRUD;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//차량스케줄 등록 시
				if( szCRUD.equals("C") ) {
					
					//차량스케줄 등록 시는 넘겨진 차량정보로 차량스케줄이 존재하는 지를 먼저 확인 필요
					szMsg = "[JSP Session]배차등록 - ("+(i+1)+") 차량스케줄 등록전 같은 차량 정보로 차량스케줄 존재하는 지 확인 전";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					outRecSet       = JDTORecordFactory.getInstance().createRecordSet("");
//PIDEV_S :병행가동용:PI_YD
					recPara.setField("PI_YD",    	szYD_CAR_STOP_LOC.substring(0,1));	
					intRtnVal = ydCarSchDao.getYdCarsch(recPara, outRecSet, 27);
					
					if( intRtnVal < 0 ) {
						szMsg = "[JSP Session]배차등록 - ("+(i+1)+") 차량스케줄 등록전 같은 차량 정보로 차량스케줄 존재하는 지 확인 시 오류발생 - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						szRtnMsg = YdConstant.RETN_CD_FAILURE;
						continue;
					}else if( intRtnVal > 0 ) {
						szMsg = "[JSP Session]배차등록 - ("+(i+1)+") 차량스케줄 등록전 같은 차량 정보로 차량스케줄 존재하므로 등록 불가 - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						szRtnMsg = YdConstant.RETN_CD_EXIST;
						continue;
					}
					
					recPara.setField("YD_CARLD_LEV_DT", szCurrDate);			//상차출발일시
					if( szYD_CAR_PROG_STAT.equals("2")) {						//차량상차도착
						recPara.setField("YD_CARLD_ARR_DT", szCurrDate);		//상차도착일시
					}
					
					//차량스케줄 등록
					szMsg = "[JSP Session]배차등록 - ("+(i+1)+") 차량스케줄 등록 가능";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					recPara.setField("REGISTER",yddatautil.setDataDefault(inDto[i].getField("YD_USER_ID"), ""));
					intRtnVal = ydCarSchDao.insYdCarsch(recPara);
					
					if (intRtnVal < 0) {
						if (intRtnVal == -1) {
							szMsg = "[JSP Session]배차등록 - 차량스케줄 등록 시 오류1 - 리턴값 : " + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						} else {
							szMsg = "[JSP Session]배차등록 - 차량스케줄 등록 시 오류2 - 리턴값 : " + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						}
						szRtnMsg = YdConstant.RETN_CD_FAILURE;
					}else{
						szMsg = "[JSP Session] 배차등록 - ("+(i+1)+")차량스케줄 등록 성공";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
					
				}
				//차량스케줄 수정 시
				else if( szCRUD.equals("U") ) {
					szMsg = "[JSP Session]배차등록 - ("+(i+1)+") 차량스케줄 수정 전";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					recPara.setField("MODIFIER",yddatautil.setDataDefault(inDto[i].getField("YD_USER_ID"), ""));
					//차량스케줄 수정
					intRtnVal = ydCarSchDao.updYdCarsch(recPara, 0);
					
					if (intRtnVal < 0) {
						if (intRtnVal == -1) {
							szMsg = "[JSP Session]배차등록 - 차량스케줄 수정 시 오류1 - 리턴값 : " + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						} else {
							szMsg = "[JSP Session]배차등록 - 차량스케줄 수정 시 오류2 - 리턴값 : " + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						}
						szRtnMsg = YdConstant.RETN_CD_FAILURE;
					}else{
						szMsg = "[JSP Session]배차등록 - ("+(i+1)+")차량스케줄 수정 성공";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
				}
				//intRtnVal = ydCarSpecDao.updYdCarspec(recPara,  0);				
				
				//System.out.println("inDto[0]에 값 =="+inDto[0]);
				//mkYardCarSch(inDto[0]);
			}
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return szRtnMsg;
	}//end of uptCarSch
		
	/**
	 * 차량작업관리 상차LOT편성
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return 
	 * @return 
	 * @return
	 * @throws DAOException
	 */
	
	public String insCarLdLot(JDTORecord [] inDto) throws DAOException {
		/*
		 * 업무기준 : 1. 차량스케줄ID로 차량스케줄이 존재하는 조회
		 * 				1-1. 존재하지 않으면 오류처리
		 * 				1-2. 존재하면 기준적용/작업자지정인 지를 판단
		 * 					1-2-1. 작업자지정이면
		 * 						1-2-1-1. 스케줄기준 체크
		 * 						1-2-1-2. 기존작업예약이 존재하지 않으면 작업예약 등록
		 * 						1-2-1-3. 작업예약 재료 등록
		 * 						1-2-1-4. 통합야드인 경우의 준비스케줄을 이용하여 작업예약 등록인 경우에는 
		 * 								해당 준비스케줄의 작업예약ID와 DEL_YN 항목에 Y를 설정
		 * 					1-2-2. 기준적용이면
		 * 						1-2-2-1. 구내운송은 차량스펙을 조회하여 야드작업허용중량을 사용
		 * 						1-2-2-2. 이송대상재를 조회
		 * 						1-2-2-3. 스케줄기준 체크
		 * 						1-2-2-4. 작업예약 등록
		 * 						1-2-2-5. 작업예약재료 등록
		 * 					1-2-3. 상차출발인 경우 예약된 차량정지point를 삭제하고 차량정지point지시요구 모듈을 호출
		 * 수정자 : 임춘수
		 * 수정일 : 2009.10.12
		 */
		int       intRtnVal    = 0;
		JDTORecord       recTemp         = null;
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecordSet    rsResult       = null;
		String szMsg        = "";		
		String szMethodName = "insCarLdLot";
		String szOperationName		= "상차LOT편성";
		String szSTL_NO = null;
		String szRtnMsg = YdConstant.RETN_CD_SUCCESS;
		String szYD_CAR_SCH_ID = null;
		String szYD_CAR_USE_GP = null;
		String szTRN_EQP_CD 	= null;
		String szCAR_NO = null;
		String szCARD_NO = null;
		String szYD_STR_LOC = null;
		String szYD_GP = "";
		String szYD_BAY_GP = "";
		String szYD_SCH_CD = "";
		String szYD_SCH_PRIOR = "";
		String szYD_AIM_YD_GP = null;
		String szYD_AIM_BAY_GP = null;
		String szCRUD = null;
		String szUser = null;
		String szWLOC_CD = null;
		String szPREV_YD_STK_COL_GP = "";
		String szYD_STK_COL_GP = "";
		String szYD_STK_BED_NO = "";
		String szYD_STK_LYR_NO = "";
		String szYD_CAR_LOT_TYPE = "";
		String szCAR_POINT = null;
		String szBAY = null;
		String szYD_AIM_RT_GP = null;
		String szYD_PNT_CD1 = "";
		String szPREP_TYPE		= null;
		String szYD_PREP_SCH_ID	= null;
		String szYD_CAR_PROG_STAT	= null;
		String szCurDate		= YdUtils.getCurDate("yyyyMMddHHmmss");
		String szARR_YD_PNT_CD = "";
		int intYD_WRK_ALW_L = 0;				//야드작업허용길이
		int intYD_WRK_ALW_W = 0;				//야드작업허용폭
		int intYD_WRK_ALW_SKID_PITCH = 0;		//야드작업허용Skid간격
		int intYD_WRK_ALW_SH = 0;				//야드작업허용매수
		int intYD_WRK_ALW_WT = 0;				//야드작업허용중량
		
		int intYD_MTL_WT = 0;					//야드재료중량
		int intYD_MTL_WT_SUM = 0;
		int intYD_MTL_SH = 0;
		
		boolean bRtnVal = false;
		boolean bPointSendable = false;
		
		String szYD_WBOOK_ID = null;
		String szIS_EJB_CALL	= "";
		
		YdCarSchDao ydCarSchDao = new YdCarSchDao();
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		YdStockDao ydStockDao = new YdStockDao();
		YdCarSpecDao ydCarSpecDao = null;
		YdPrepSchDao ydPrepSchDao = null;
		YdPrepMtlDao ydPrepMtlDao = null;
		YdStkColDao ydStkColDao		= null;
		
		
		JDTORecordSet rsWrkMtl = JDTORecordFactory.getInstance().createRecordSet("stock");
		JDTORecordSet rsSort = JDTORecordFactory.getInstance().createRecordSet("stock");
		JDTORecord recSort = null;
		
		try {
			szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//발지개소코드
			szWLOC_CD = ydDaoUtils.paraRecChkNull(inDto[0], "WLOC_CD3");
			
			szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 - 발지개소코드["+szWLOC_CD+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//+++++++++++++++ 1. 차량스케줄을 먼저 조회해서 존재하는 지를 확인 ++++++++++++++++++++++
			szYD_CAR_SCH_ID = yddatautil.setDataDefault(inDto[0].getField("YD_CAR_SCH_ID"), "");
			szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 차량스케줄ID["+szYD_CAR_SCH_ID+"]로 조회 전";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
//getYdCarsch0			
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, outRecSet, 0);
			if( intRtnVal == 0 ) {
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 차량스케줄ID["+szYD_CAR_SCH_ID+"]이 존재하지 않습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NOTEXIST;
			}else if( intRtnVal < 0 ) {
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 차량스케줄ID["+szYD_CAR_SCH_ID+"]로 조회시 오류발생 : 반환값  - " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}
			outRecSet.first();
			recPara = outRecSet.getRecord();
			
			szYD_CAR_PROG_STAT = yddatautil.setDataDefault(recPara.getField("YD_CAR_PROG_STAT"), "");		//차량진행상태
			szYD_CAR_USE_GP = yddatautil.setDataDefault(recPara.getField("YD_CAR_USE_GP"), "");
			szTRN_EQP_CD = yddatautil.setDataDefault(recPara.getField("TRN_EQP_CD"), "");
			szCAR_NO = yddatautil.setDataDefault(recPara.getField("CAR_NO"), "");
			szCARD_NO = yddatautil.setDataDefault(recPara.getField("CARD_NO"), "");
			szARR_YD_PNT_CD = yddatautil.setDataDefault(recPara.getField("YD_PNT_CD1"), "");
			
			
			//C연주 스케줄 코드를 포인트 코드 기준으로 분리 하기
	    	/////////////////////////////////////////////////////////////////////////////////
	    	if(YdConstant.WLOC_CD_C_SLAB_YARD.equals(szWLOC_CD)  ||
	    	   YdConstant.WLOC_CD_PORT_SLAB_YARD.equals(szWLOC_CD)   //항만슬라브야드 기능추가 - 2015.12.31 LeeJY
	    	  ){  
 
	    		EJBConnector 	ejbConnC = new EJBConnector("default", "CarMvHdSeEJB", this);			
	    		szYD_SCH_CD = (String)ejbConnC.trx("CarPointSchCdChange", new Class[] { String.class,String.class,String.class,String.class }
			    								  , new Object[] { szWLOC_CD,szARR_YD_PNT_CD,szYD_WBOOK_ID,"E" });
	    	}
	    	/////////////////////////////////////////////////////////////////////////////////
	    	
			
			szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 차량스케줄ID["+szYD_CAR_SCH_ID+"]로 차량스케줄이 존재합니다 - 운송장비코드["+szTRN_EQP_CD+"], 차량번호["+szCAR_NO+"], 카드번호["+szCARD_NO+"], 차량사용구분["+szYD_CAR_USE_GP+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			
			//기준적용인 지 작업자지정인 지를 판단
			szYD_CAR_LOT_TYPE = ydDaoUtils.paraRecChkNull(inDto[0], "YD_CAR_LOT_TYPE");
			if( szYD_CAR_LOT_TYPE.equals("M") )	{
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 - 작업자지정";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				
				
				//+++++++++++++++++++++++ 2. 스케줄기준 확인 +++++++++++++++++++++++
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] : 스케줄기준 확인 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				szYD_STR_LOC = yddatautil.setDataDefault(inDto[0].getField("YD_STR_LOC"), "");
				if( !szYD_STR_LOC.equals("")) {
					szYD_GP = szYD_STR_LOC.substring(0, 1);
					szYD_BAY_GP = szYD_STR_LOC.substring(1, 2);
					
					if(!YdConstant.WLOC_CD_C_SLAB_YARD.equals(szWLOC_CD) ){ 
						szYD_SCH_CD = szYD_STR_LOC.substring(0, 2) + "PT01UM";
					}
					
					recPara = JDTORecordFactory.getInstance().create();
					intRtnVal = YdCommonUtils.getCrnInfoByCrnSchRule(szYD_SCH_CD, recPara);
					if( intRtnVal == -1 ) {
						szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] : 스케줄기준["+szYD_SCH_CD+"]이 금지상태입니다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return YdConstant.RETN_CRN_SCH_PROH;
					}else if( intRtnVal < 0 ) {
						szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] : 스케줄기준["+szYD_SCH_CD+"]조회 시 오류발생 : 반환값 - " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return YdConstant.RETN_CD_FAILURE;
					}
					
					szYD_SCH_PRIOR = yddatautil.setDataDefault(recPara.getField("YD_SCH_PRIOR"), "");
					
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] : 스케줄기준 확인 끝";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				
				//+++++++++++++++++++++++ 3. 작업예약 등록 +++++++++++++++++++++++
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] : 작업예약 등록 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				szYD_PREP_SCH_ID = yddatautil.setDataDefault(inDto[0].getField("YD_PREP_SCH_ID"), "");
				szPREP_TYPE = yddatautil.setDataDefault(inDto[0].getField("PREP_TYPE"), "");
				szYD_WBOOK_ID = yddatautil.setDataDefault(inDto[0].getField("YD_WBOOK_ID"), "");
				
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] - 기존의 준비스케줄을 작업예약에 등록할 지 유무 판단["+szPREP_TYPE+"] - 준비스케줄ID["+szYD_PREP_SCH_ID+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				if( szYD_WBOOK_ID.equals("") ) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] - 기존에 등록된 작업예약ID가 존재하지 않습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}else{
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] - 기존에 등록된 작업예약ID["+szYD_WBOOK_ID+"]가 존재합니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
				
				szYD_AIM_YD_GP = yddatautil.setDataDefault(inDto[0].getField("YD_AIM_YD_GP"), "");
				szYD_AIM_BAY_GP = yddatautil.setDataDefault(inDto[0].getField("YD_AIM_BAY_GP"), "");
				szUser = yddatautil.setDataDefault(inDto[0].getField("YD_USER_ID"), "");
				
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] - 첫번째 재료의 목표야드["+szYD_AIM_YD_GP+"], 목표동["+szYD_AIM_BAY_GP+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				if( szPREP_TYPE.equals("Y") && !szYD_PREP_SCH_ID.equals("")) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] - 준비스케줄ID["+szYD_PREP_SCH_ID+"]의 목표야드와 목표동을 사용하기 위해서 조회 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					/*
					 * 준비스케줄ID가 존재하는 경우는 이송LOT편성된 정보에서 목표야드와 목표동 구분을 조회해서 사용
					 * 수정자 : 임춘수
					 * 수정일 : 2009.10.26
					 */
					rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_PREP_SCH_ID",   szYD_PREP_SCH_ID);
					ydPrepSchDao = new YdPrepSchDao();
//getYdPrepsch0					
					intRtnVal = ydPrepSchDao.getYdPrepsch(recPara, rsResult, 0);
					if( intRtnVal <= 0) {
						szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] - 준비스케줄ID["+szYD_PREP_SCH_ID+"]로 조회 시 오류발생 - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}else{
						rsResult.first();
						recPara = rsResult.getRecord();
						
						//야드목표야드구분
						szYD_AIM_YD_GP  = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_YD_GP");
						//야드목표동구분
						szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_BAY_GP");
						//스케줄코드
						if(!YdConstant.WLOC_CD_C_SLAB_YARD.equals(szWLOC_CD) ){
							szYD_SCH_CD = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_CD");
						}
						szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] - 준비스케줄ID["+szYD_PREP_SCH_ID+"]로 조회 시 데이타가 존재합니다. - 목표야드["+szYD_AIM_YD_GP+"], 목표동["+szYD_AIM_BAY_GP+"], 스케줄코드["+szYD_SCH_CD+"]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
					}
					
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] - 준비스케줄ID["+szYD_PREP_SCH_ID+"]가 존재하므로 목표야드["+szYD_AIM_YD_GP+"]와 목표동["+szYD_AIM_BAY_GP+"], 스케줄코드["+szYD_SCH_CD+"]을 사용";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
				
				 
				
				if( szYD_WBOOK_ID.equals("")) {
					//신규작업예약 등록
					szYD_WBOOK_ID = ydWrkbookDao.getYdWrkbookId();
					
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
					recPara.setField("YD_GP", 		  szYD_GP);
					recPara.setField("YD_BAY_GP", 	  szYD_BAY_GP);
					recPara.setField("YD_SCH_PRIOR", 	szYD_SCH_PRIOR);
					recPara.setField("YD_SCH_CD", 	  szYD_SCH_CD);
					recPara.setField("REGISTER", 	  szUser);
					recPara.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
					recPara.setField("TRN_EQP_CD", 	  szTRN_EQP_CD);
					recPara.setField("YD_AIM_YD_GP",  szYD_AIM_YD_GP);
					recPara.setField("YD_AIM_BAY_GP", szYD_AIM_BAY_GP);
					
					//작업예약 INSERT
					intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
					if (intRtnVal < 1) {
						szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] : 작업예약 데이터 등록 중 에러";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return YdConstant.RETN_CD_FAILURE;
					}
					
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] : 신규작업예약 등록["+szYD_WBOOK_ID+"] 끝";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}else{
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] - 기존에 등록된 작업예약ID["+szYD_WBOOK_ID+"]로 작업예약 재료 등록 시 사용합니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				
				//+++++++++++++++++++++++ 4. 작업예약재료 등록 +++++++++++++++++++++++
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] : 작업예약재료 등록[작업예약ID : "+szYD_WBOOK_ID+"] 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//recPara         = JDTORecordFactory.getInstance().create();
				//recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
				//recPara.setField("REGISTER", 	  szUser);
				//여러건의 로우를 처리 - 등록 및 수정
				for(int i = 0; i < inDto.length; i++ ) {
					recPara         = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
					recPara.setField("REGISTER", 	  szUser);
					
					szCRUD = yddatautil.setDataDefault(inDto[i].getField("CRUD"), "");
					if( !szPREP_TYPE.equals("Y")) {
						if( !szCRUD.equals("C") ) continue;
					}
					//항목 편집
					szSTL_NO = yddatautil.setDataDefault(inDto[i].getField("STL_NO"), "");
					
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] - "+(i + 1)+"번째 재료번호["+szSTL_NO+"]로 적치단 조회 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					//[1].재료의 저장품테이블에 존재하는 지 확인
					
					//[2].재료의 적치단 상태를 확인 
					if( !szWLOC_CD.equals(YdConstant.WLOC_CD_A_PLATE_PLANT) && 
						!szWLOC_CD.equals(YdConstant.WLOC_CD_B_PLATE_PLANT) &&
						!szWLOC_CD.equals(YdConstant.WLOC_CD_C_HR_PLANT)) {
						rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
						bRtnVal = YdCommonUtils.chkGetStlStkLyr(szSTL_NO, "C", rsResult);
						if( !bRtnVal ) {
							szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] - "+(i + 1)+"번째 재료번호["+szSTL_NO+"]가 적치단에 존재하지 않습니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							throw new DAOException(YdConstant.RETN_CD_NOTEXIST);
						}
						rsResult.first();
						recTemp = rsResult.getRecord();
						szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_COL_GP");
						szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_NO");
						szYD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_NO");
						//[3]. 작업예약재료 등록
						szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] - "+(i + 1)+"번째 재료번호["+szSTL_NO+"]로 적치단 정보 ";
						szMsg += "- 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"], 적치단["+szYD_STK_LYR_NO+"]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}else{
						szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] - "+(i + 1)+"번째 재료번호["+szSTL_NO+"]로 적치단 정보가 없음 ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
					//재료번호
					recPara.setField("STL_NO", 		   szSTL_NO);
					//적치열구분
					recPara.setField("YD_STK_COL_GP",  szYD_STK_COL_GP);
					//적치BED번호
					recPara.setField("YD_STK_BED_NO",  szYD_STK_BED_NO);
					//적치단번호
					recPara.setField("YD_STK_LYR_NO",  szYD_STK_LYR_NO);
					//권상모음순서
					recPara.setField("YD_UP_COLL_SEQ", "" + (i + 1));
					
					rsWrkMtl.addRecord(recPara);
					
					// 작업예약재료 테이블에 등록한다.
//					intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
//					
//					if (intRtnVal < 1) {
//						szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] : 작업예약재료 데이터 등록 중 에러";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//						throw new DAOException(YdConstant.RETN_CD_FAILURE);
//					}
//					
//					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] - "+(i + 1)+"번째 재료번호["+szSTL_NO+"]를 작업예약재료[작업예약ID : "+szYD_WBOOK_ID+"]로 등록 성공 ";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
				}
				
				
				String szRtnVal = sortUpColSeq(rsWrkMtl, szYD_AIM_RT_GP, rsSort); 
				
				
				
				if (szRtnVal == YdConstant.RETN_CD_SUCCESS) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 작업예약재료 정렬 성공 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					for(int Loop_i = 1; Loop_i <= rsSort.size(); Loop_i++) {
						rsSort.absolute(Loop_i);
						recSort         = JDTORecordFactory.getInstance().create();
						recSort = rsSort.getRecord();
						
						recPara         = JDTORecordFactory.getInstance().create();
						recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
						recPara.setField("REGISTER", 	  szUser);
						//재료번호
						recPara.setField("STL_NO", 		   ydDaoUtils.paraRecChkNull(recSort, "STL_NO"));
						//적치열구분
						recPara.setField("YD_STK_COL_GP",  ydDaoUtils.paraRecChkNull(recSort, "YD_STK_COL_GP"));
						//적치BED번호
						recPara.setField("YD_STK_BED_NO",  ydDaoUtils.paraRecChkNull(recSort, "YD_STK_BED_NO"));
						//적치단번호
						recPara.setField("YD_STK_LYR_NO",  ydDaoUtils.paraRecChkNull(recSort, "YD_STK_LYR_NO"));
						//권상모음순서
						recPara.setField("YD_UP_COLL_SEQ", "" + Loop_i);
						
						// 작업예약재료 테이블에 등록한다.
						intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
						
						if (intRtnVal < 1) {
							szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] : 작업예약재료 데이터 등록 중 에러";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							throw new DAOException(YdConstant.RETN_CD_FAILURE);
						}
						
						szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] - "+Loop_i+"번째 재료번호["+ydDaoUtils.paraRecChkNull(recSort, "STL_NO")+"]를 작업예약재료[작업예약ID : "+szYD_WBOOK_ID+"]로 등록 성공 ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
					
				} else {
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 작업예약재료 정렬 실패 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					rsWrkMtl.first();
					
					for(int Loop_i = 0; Loop_i < rsWrkMtl.size(); Loop_i++) {
						recSort         = JDTORecordFactory.getInstance().create();
						recSort = rsWrkMtl.getRecord(Loop_i);
						
						recPara         = JDTORecordFactory.getInstance().create();
						recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
						recPara.setField("REGISTER", 	  szUser);
						//재료번호
						recPara.setField("STL_NO", 		   ydDaoUtils.paraRecChkNull(recSort, "STL_NO"));
						//적치열구분
						recPara.setField("YD_STK_COL_GP",  ydDaoUtils.paraRecChkNull(recSort, "YD_STK_COL_GP"));
						//적치BED번호
						recPara.setField("YD_STK_BED_NO",  ydDaoUtils.paraRecChkNull(recSort, "YD_STK_BED_NO"));
						//적치단번호
						recPara.setField("YD_STK_LYR_NO",  ydDaoUtils.paraRecChkNull(recSort, "YD_STK_LYR_NO"));
						//권상모음순서
						recPara.setField("YD_UP_COLL_SEQ", ydDaoUtils.paraRecChkNull(recSort, "YD_UP_COLL_SEQ"));
						
						// 작업예약재료 테이블에 등록한다.
						intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
						
						if (intRtnVal < 1) {
							szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] : 작업예약재료 데이터 등록 중 에러";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							throw new DAOException(YdConstant.RETN_CD_FAILURE);
						}
						
						szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] - "+Loop_i+"번째 재료번호["+ydDaoUtils.paraRecChkNull(recSort, "STL_NO")+"]를 작업예약재료[작업예약ID : "+szYD_WBOOK_ID+"]로 등록 성공 ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
					
				}
				
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] : 작업예약재료[작업예약ID : "+szYD_WBOOK_ID+"] 등록 끝";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				if( szPREP_TYPE.equals("Y") && !szYD_PREP_SCH_ID.equals("")) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] : 작업예약에 등록 후 준비스케줄["+szYD_PREP_SCH_ID+"] 삭제 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					/*
					recPara         = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_PREP_SCH_ID",   szYD_PREP_SCH_ID);
					//준비재료 삭제처리
					ydPrepMtlDao = new YdPrepMtlDao();
					intRtnVal = ydPrepMtlDao.delYdPrepmtlByPrepSchId(recPara);
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] : 준비재료["+szYD_PREP_SCH_ID+"] 삭제 성공 - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					//준비스케줄 삭제처리
					ydPrepSchDao = new YdPrepSchDao();
					intRtnVal =  ydPrepSchDao.delYdPrepsch(recPara);
					*/
					ydPrepMtlDao = new YdPrepMtlDao();
					
					recPara         = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_PREP_SCH_ID",   	szYD_PREP_SCH_ID);
					recPara.setField("DEL_YN",   			"Y");
					recPara.setField("MODIFIER",   			szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
					//준비재료 삭제처리
					intRtnVal = ydPrepMtlDao.uptDelYdPrepmtlByPrepSchId(recPara);
					
					//ydPrepSchDao = new YdPrepSchDao();
					
					//준비스케줄 삭제처리
					recPara.setField("YD_WBOOK_ID",   		szYD_WBOOK_ID);
					intRtnVal =  ydPrepSchDao.updDelYdPrepsch(recPara);
					
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] : 준비스케줄["+szYD_PREP_SCH_ID+"] 삭제 성공 - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					
				}
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			}else{
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 - 기준적용";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				
				//++++++++++++++++++++++++ 차량스펙 조회 시작 +++++++++++++++++++++++++++++++++
				
				//차량스펙을 조회해서 차량작업가능매수만큼 LOT편성
				ydCarSpecDao = new YdCarSpecDao();
				outRecSet       = JDTORecordFactory.getInstance().createRecordSet("carSpec");
				recPara         = JDTORecordFactory.getInstance().create();
				if( szYD_CAR_USE_GP.equals("L") ) {					//구내운송차량
					recPara.setField("TRN_EQP_CD", szTRN_EQP_CD);
					
//getYdCarspec2					
					intRtnVal = ydCarSpecDao.getYdCarspec(recPara, outRecSet, 2);
				}else if( szYD_CAR_USE_GP.equals("G") ) {			
					//recPara.setField("CAR_NO", szCAR_NO);
					//intRtnVal = ydCarSpecDao.getYdCarspec(recPara, outRecSet, 4);
					intRtnVal = 1;
				}else{
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] - 지원하지 않는 차량사용구분["+szYD_CAR_USE_GP+"]입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					throw new DAOException(YdConstant.RETN_CD_FAILURE);
				}
				/*
				 * 출하차량인 경우에는 차량스펙을 관리하지 않으므로 차후 로직 변경 필요.
				 * 수정자 : 임춘수
				 */
				
				if( intRtnVal == 0 ) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] - 해당차량에 대한 차량스펙이 존재하지 않습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					throw new DAOException(YdConstant.RETN_CD_FAILURE);
				}else if( intRtnVal < 0 ) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] - 해당차량에 대한 차량스펙 조회 시 오류발생 - 반환값 : " +intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					throw new DAOException(YdConstant.RETN_CD_FAILURE);
				}
				
				if( szYD_CAR_USE_GP.equals("L") ) {					//구내운송차량
					outRecSet.first();
					recPara = outRecSet.getRecord();
					/*
					int intYD_WRK_ALW_L = 0;				//야드작업허용길이
					int intYD_WRK_ALW_W = 0;				//야드작업허용폭
					int intYD_WRK_ALW_SKID_PITCH = 0;		//야드작업허용Skid간격
					int intYD_WRK_ALW_SH = 0;				//야드작업허용매수
					int intYD_WRK_ALW_WT = 0;				//야드작업허용중량
					*/
					
					intYD_WRK_ALW_L = ydDaoUtils.paraRecChkNullInt(recPara, "YD_WRK_ALW_L");
					intYD_WRK_ALW_W = ydDaoUtils.paraRecChkNullInt(recPara, "YD_WRK_ALW_W"); 
					intYD_WRK_ALW_SKID_PITCH  = ydDaoUtils.paraRecChkNullInt(recPara, "YD_WRK_ALW_SKID_PITCH");
					intYD_WRK_ALW_SH  = ydDaoUtils.paraRecChkNullInt(recPara, "YD_WRK_ALW_SH");
					intYD_WRK_ALW_WT  = ydDaoUtils.paraRecChkNullInt(recPara, "YD_WRK_ALW_WT");
					
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] - 차량사용구분["+szYD_CAR_USE_GP+"], 운송장비코드["+szTRN_EQP_CD+"], 차량번호["+szCAR_NO+"]에 대한 차량스펙 : ";
					szMsg += "야드작업허용길이["+intYD_WRK_ALW_L+"], 야드작업허용폭["+intYD_WRK_ALW_W+"], 야드작업허용Skid간격["+intYD_WRK_ALW_SKID_PITCH+"], 야드작업허용매수["+intYD_WRK_ALW_SH+"], 야드작업허용중량["+intYD_WRK_ALW_WT+"]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
				//++++++++++++++++++++++++ 차량스펙 조회 끝 +++++++++++++++++++++++++++++++++
				
				//++++++++++++++++++++++++ 대상재 조회 시작 +++++++++++++++++++++++++++++++++
				szCAR_POINT = ydDaoUtils.paraRecChkNull(inDto[0], "CAR_POINT3");
				szBAY = ydDaoUtils.paraRecChkNull(inDto[0], "BAY3");
				if( szCAR_POINT.equals("") ) {
					szCAR_POINT = szBAY;
				}else{
					szCAR_POINT = szCAR_POINT.substring(0, 2);
				}
				szUser = yddatautil.setDataDefault(inDto[0].getField("YD_USER_ID"), "");
				//szWLOC_CD = ydDaoUtils.paraRecChkNull(inDto[0], "WLOC_CD3");
				szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(inDto[0], "YD_AIM_RT_GP");
				outRecSet       = JDTORecordFactory.getInstance().createRecordSet("stock");
				recPara         = JDTORecordFactory.getInstance().create();
				recPara.setField("SPOS_WLOC_CD",	    szWLOC_CD);
				recPara.setField("YD_AIM_RT_GP",	    szYD_AIM_RT_GP);
				recPara.setField("YD_STK_COL_GP",	    szCAR_POINT);
				
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 - 기준적용 시 발지개소코드["+szWLOC_CD+"], 야드목표행선구분["+szYD_AIM_RT_GP+"], 적치열구분["+szCAR_POINT+"]로 대상재 조회 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//getYdStock122				
				intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 122);
				
				if( intRtnVal == 0 ) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 - 기준적용 시 발지개소코드["+szWLOC_CD+"], 야드목표행선구분["+szYD_AIM_RT_GP+"], 적치열구분["+szCAR_POINT+"]로 대상재가 존재하지 않습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}else if( intRtnVal < 0 ) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 - 기준적용 시 발지개소코드["+szWLOC_CD+"], 야드목표행선구분["+szYD_AIM_RT_GP+"], 적치열구분["+szCAR_POINT+"]로 대상재조회 시 오류발생 - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					throw new DAOException(YdConstant.RETN_CD_FAILURE);
				}
				
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 - 기준적용 시 발지개소코드["+szWLOC_CD+"], 야드목표행선구분["+szYD_AIM_RT_GP+"], 적치열구분["+szCAR_POINT+"]로 대상재 조회된 갯수 - " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				intYD_MTL_SH = outRecSet.size();
				
				//if( intYD_MTL_SH  > intYD_WRK_ALW_SH ) intYD_MTL_SH = intYD_WRK_ALW_SH;
				
				
				outRecSet.first();
				recPara = outRecSet.getRecord();
				szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_YD_GP");
				szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_BAY_GP");
				szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");
				szPREV_YD_STK_COL_GP = szYD_STK_COL_GP;
				szYD_GP = szYD_STK_COL_GP.substring(0, 1);
				szYD_BAY_GP = szYD_STK_COL_GP.substring(1, 2);
				
				if(!YdConstant.WLOC_CD_C_SLAB_YARD.equals(szWLOC_CD) ){
					szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT01UM";
				}
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] : 1번째 대상재의 적치열구분["+szYD_STK_COL_GP+"], 스케줄코드["+szYD_SCH_CD+"], 야드목표야드구분["+szYD_AIM_YD_GP+"], 야드목표동구분["+szYD_AIM_BAY_GP+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//+++++++++++++++++++++++ 스케줄기준 확인 시작 +++++++++++++++++++++++
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] : 스케줄기준 확인 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
				recPara = JDTORecordFactory.getInstance().create();
				intRtnVal = YdCommonUtils.getCrnInfoByCrnSchRule(szYD_SCH_CD, recPara);
				if( intRtnVal == -1 ) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] : 스케줄기준["+szYD_SCH_CD+"]이 금지상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CRN_SCH_PROH;
				}else if( intRtnVal < 0 ) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] : 스케줄기준["+szYD_SCH_CD+"]조회 시 오류발생 : 반환값 - " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;
				}
				
				szYD_SCH_PRIOR = yddatautil.setDataDefault(recPara.getField("YD_SCH_PRIOR"), "");
				
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] : 스케줄기준 확인 끝";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//++++++++++++++++++++++++++++++스케줄기준 확인 끝++++++++++++++++++++++++++++++++
				
				//------------------------------------------------------------------------------------------------------
				//	작업예약 등록 시작
				//------------------------------------------------------------------------------------------------------
				
				//신규작업예약 등록
				szYD_WBOOK_ID = ydWrkbookDao.getYdWrkbookId();
				
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
				recPara.setField("YD_GP", 		  szYD_GP);
				recPara.setField("YD_BAY_GP", 	  szYD_BAY_GP);
				recPara.setField("YD_SCH_PRIOR", 	szYD_SCH_PRIOR);
				recPara.setField("YD_SCH_CD", 	  szYD_SCH_CD);
				recPara.setField("REGISTER", 	  szUser);
				recPara.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
				recPara.setField("TRN_EQP_CD", 	  szTRN_EQP_CD);
				recPara.setField("YD_AIM_YD_GP",  szYD_AIM_YD_GP);
				recPara.setField("YD_AIM_BAY_GP", szYD_AIM_BAY_GP);
				
				//작업예약 INSERT
				intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
				if (intRtnVal < 1) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] : 작업예약 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;
				}
				
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] : 신규작업예약 등록["+szYD_WBOOK_ID+"] 끝";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//++++++++++++++++++++++++++++ 작업예약 등록 끝 ++++++++++++++++++++++++++++++++
				
				//------------------------------------------------------------------------------------------------------
				//	작업예약재료 등록 시작
				//------------------------------------------------------------------------------------------------------
				
				
				
				
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] - 대상재매수 : " + intYD_MTL_SH;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//recPara         = JDTORecordFactory.getInstance().create();
				//recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
				//recPara.setField("REGISTER", 	  szUser);
				for(int i = 1; i <= intYD_MTL_SH; i++) {
					recPara         = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
					recPara.setField("REGISTER", 	  szUser);
					
					outRecSet.absolute(i);
					recTemp = outRecSet.getRecord();
					szSTL_NO = ydDaoUtils.paraRecChkNull(recTemp, "STL_NO");
					szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_COL_GP");
					szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_NO");
					szYD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_NO");
					intYD_MTL_WT = ydDaoUtils.paraRecChkNullInt(recTemp, "YD_MTL_WT");
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] : ["+i+"]번째 대상재["+szSTL_NO+"]의 재료중량["+intYD_MTL_WT+"]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					intYD_MTL_WT_SUM += intYD_MTL_WT;
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] : 총재료중량["+intYD_MTL_WT_SUM+"] --- 차량작업가능중량["+intYD_WRK_ALW_WT+"]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					if( szYD_CAR_USE_GP.equals("L") ) {					//구내운송차량
						if( intYD_MTL_WT_SUM > intYD_WRK_ALW_WT ) break;
					}
					
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] : 1번째 대상재의 야드동["+szPREV_YD_STK_COL_GP+"] - ["+i+"]번째 대상재의 야드동["+szYD_STK_COL_GP+"]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					if( i > 1 ) {
						if( !szPREV_YD_STK_COL_GP.substring(0, 2).equals(szYD_STK_COL_GP.substring(0, 2)) ) {
							szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] : 1번째 대상재의 야드동["+szPREV_YD_STK_COL_GP+"] - ["+i+"]번째 대상재의 야드동["+szYD_STK_COL_GP+"]이 다르므로 작업예약재료에 더 이상 등록하지 않음.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							break;
						}
					}
					
					
					
					//재료번호
					recPara.setField("STL_NO", 		   szSTL_NO);
					//적치열구분
					recPara.setField("YD_STK_COL_GP",  szYD_STK_COL_GP);
					//적치BED번호
					recPara.setField("YD_STK_BED_NO",  szYD_STK_BED_NO);
					//적치단번호
					recPara.setField("YD_STK_LYR_NO",  szYD_STK_LYR_NO);
					//권상모음순서
					recPara.setField("YD_UP_COLL_SEQ", "" + (i + 1));
					
					rsWrkMtl.addRecord(recPara);
					
					// 작업예약재료 테이블에 등록한다.
//					intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
//					
//					if (intRtnVal < 1) {
//						szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] : 작업예약재료 데이터 등록 중 에러";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//						throw new DAOException(YdConstant.RETN_CD_FAILURE);
//					}
//					
//					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] - "+i+"번째 재료번호["+szSTL_NO+"]를 작업예약재료[작업예약ID : "+szYD_WBOOK_ID+"]로 등록 성공 ";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
				
				String szRtnVal = sortUpColSeq(rsWrkMtl, szYD_AIM_RT_GP, rsSort); 
				
				
				
				if (szRtnVal == YdConstant.RETN_CD_SUCCESS) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 작업예약재료 정렬 성공 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					for(int Loop_i = 1; Loop_i <= rsSort.size(); Loop_i++) {
						rsSort.absolute(Loop_i);
						recSort         = JDTORecordFactory.getInstance().create();
						recSort = rsSort.getRecord();
						
						recPara         = JDTORecordFactory.getInstance().create();
						recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
						recPara.setField("REGISTER", 	  szUser);
						//재료번호
						recPara.setField("STL_NO", 		   ydDaoUtils.paraRecChkNull(recSort, "STL_NO"));
						//적치열구분
						recPara.setField("YD_STK_COL_GP",  ydDaoUtils.paraRecChkNull(recSort, "YD_STK_COL_GP"));
						//적치BED번호
						recPara.setField("YD_STK_BED_NO",  ydDaoUtils.paraRecChkNull(recSort, "YD_STK_BED_NO"));
						//적치단번호
						recPara.setField("YD_STK_LYR_NO",  ydDaoUtils.paraRecChkNull(recSort, "YD_STK_LYR_NO"));
						//권상모음순서
						recPara.setField("YD_UP_COLL_SEQ", "" + Loop_i);
						
						// 작업예약재료 테이블에 등록한다.
						intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
						
						if (intRtnVal < 1) {
							szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] : 작업예약재료 데이터 등록 중 에러";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							throw new DAOException(YdConstant.RETN_CD_FAILURE);
						}
						
						szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] - "+Loop_i+"번째 재료번호["+ydDaoUtils.paraRecChkNull(recSort, "STL_NO")+"]를 작업예약재료[작업예약ID : "+szYD_WBOOK_ID+"]로 등록 성공 ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
					
				} else {
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 작업예약재료 정렬 실패 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					for(int Loop_i = 1; Loop_i <= rsWrkMtl.size(); Loop_i++) {
						rsWrkMtl.absolute(Loop_i);
						recSort         = JDTORecordFactory.getInstance().create();
						recSort = rsWrkMtl.getRecord();
						
						recPara         = JDTORecordFactory.getInstance().create();
						recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
						recPara.setField("REGISTER", 	  szUser);
						//재료번호
						recPara.setField("STL_NO", 		   ydDaoUtils.paraRecChkNull(recSort, "STL_NO"));
						//적치열구분
						recPara.setField("YD_STK_COL_GP",  ydDaoUtils.paraRecChkNull(recSort, "YD_STK_COL_GP"));
						//적치BED번호
						recPara.setField("YD_STK_BED_NO",  ydDaoUtils.paraRecChkNull(recSort, "YD_STK_BED_NO"));
						//적치단번호
						recPara.setField("YD_STK_LYR_NO",  ydDaoUtils.paraRecChkNull(recSort, "YD_STK_LYR_NO"));
						//권상모음순서
						recPara.setField("YD_UP_COLL_SEQ", ydDaoUtils.paraRecChkNull(recSort, "YD_UP_COLL_SEQ"));
						
						// 작업예약재료 테이블에 등록한다.
						intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
						
						if (intRtnVal < 1) {
							szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] : 작업예약재료 데이터 등록 중 에러";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							throw new DAOException(YdConstant.RETN_CD_FAILURE);
						}
						
						szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] - "+Loop_i+"번째 재료번호["+ydDaoUtils.paraRecChkNull(recSort, "STL_NO")+"]를 작업예약재료[작업예약ID : "+szYD_WBOOK_ID+"]로 등록 성공 ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
					
				}
				
				
				
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] : 작업예약재료[작업예약ID : "+szYD_WBOOK_ID+"] 등록 끝";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//------------------------------------------------------------------------------------------------------
				
				//++++++++++++++++++++++++++++ 작업예약재료 등록 끝 ++++++++++++++++++++++++++++++++
			}
			
			/*++++++++++++++++++++++++++ 차량정지Point 전송 시작 +++++++++++++++++++++++++++*/
			
			szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 차량정지Point 전송 전 개소코드["+szWLOC_CD+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			if( szWLOC_CD.equals(YdConstant.WLOC_CD_A_PLATE_PLANT) || 
				szWLOC_CD.equals(YdConstant.WLOC_CD_B_PLATE_PLANT) ||	
				szWLOC_CD.equals(YdConstant.WLOC_CD_C_HR_PLANT) ) {
				if ( szYD_CAR_PROG_STAT.equals(YdConstant.YD_CARLD_LEV) ) {
					
					//------------------------------------------------------------------------------------------------------
					//	후판sizing개소코드거나 재열재개소코드이고 상차출발이면 소재차량정지point요구 모듈 호출
					//------------------------------------------------------------------------------------------------------
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 야드에서관리되지 않는 개소코드["+szWLOC_CD+"]이므로 차량스케줄["+szYD_CAR_SCH_ID+"] 상차point코드 등록 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					if( szWLOC_CD.equals(YdConstant.WLOC_CD_A_PLATE_PLANT)||
						szWLOC_CD.equals(YdConstant.WLOC_CD_B_PLATE_PLANT)){
						szYD_PNT_CD1 = YdConstant.WLOC_CD_A_PLATE_PLANT_PNT_CD;
					}else if( szWLOC_CD.equals(YdConstant.WLOC_CD_C_HR_PLANT) ) {
						szYD_PNT_CD1 = YdConstant.WLOC_CD_C_HR_PLANT_PNT_CD;
					}
					
					recPara         = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
					recPara.setField("YD_CARLD_PNT_WO_DT", szCurDate);
					recPara.setField("YD_PNT_CD1", szYD_PNT_CD1);
					recPara.setField("MODIFIER", szUser);
					recPara.setField("YD_CARLD_WRK_BOOK_ID", szYD_WBOOK_ID);
					
					szIS_EJB_CALL = "Y";
					bPointSendable = true;
					
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 차량정지Point 전송 끝";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					//------------------------------------------------------------------------------------------------------
				}else{
					recPara         = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
					recPara.setField("MODIFIER", szUser);
					recPara.setField("YD_CARLD_WRK_BOOK_ID", szYD_WBOOK_ID);
					
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 차량도착 시 상차LOT편성이므로 차량정지POINT전송하지 않음";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
			}else{
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 야드에서관리되는 개소코드["+szWLOC_CD+"]이므로 차량스케줄["+szYD_CAR_SCH_ID+"] ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				szCAR_POINT = szYD_STK_COL_GP.substring(0, 2) + "PT";
				
				if( szYD_CAR_PROG_STAT.equals(YdConstant.YD_CARLD_LEV) ) {			//상차출발 시
					
					//------------------------------------------------------------------------------------------------------
					//	상차출발 시 POINT 전송되도록 처리
					//------------------------------------------------------------------------------------------------------
					
					szIS_EJB_CALL = "Y";
					bPointSendable = true;
					
					//------------------------------------------------------------------------------------------------------
					
				}else if( ( szYD_STK_COL_GP.substring(0, 1).equals(YdConstant.YD_GP_C_SLAB_YARD) 			/* C연주슬라브야드 */
						|| szYD_STK_COL_GP.substring(0, 1).equals(YdConstant.YD_GP_PORT_SLAB_YARD)		//항만슬라브야드 기능추가 - 2015.12.31 LeeJY
						|| szYD_STK_COL_GP.substring(0, 1).equals(YdConstant.YD_GP_A_PLATE_SLAB_YARD)		/* A후판슬라브야드 */
						|| szYD_STK_COL_GP.substring(0, 1).equals(YdConstant.YD_GP_INTGR_YARD)				/* 통합야드 */
						) 
						&& szYD_CAR_PROG_STAT.equals(YdConstant.YD_CARLD_ARR) ) {			//상차도착 시
					
					//------------------------------------------------------------------------------------------------------
					//	C연주슬라브야드, A후판슬라브야드, 통합야드이면서 상차도착 시는 크레인스케줄메인 모듈 호출
					//------------------------------------------------------------------------------------------------------
					
					szMsg="[JSP Session] 차량작업관리 화면 상차LOT편성 : 통합야드이고 상차도착 시 상차LOT편성 후 스케줄기준체크/크레인호출 모듈 호출 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("MSG_ID", 			"YDYDJ634");
					recPara.setField("YD_WBOOK_ID", 	szYD_WBOOK_ID);
					recPara.setField("YD_SCH_CD", 		szYD_SCH_CD);
					
					szMsg="[JSP Session] 차량작업관리 화면 상차LOT편성 : 스케줄기준체크/크레인호출 모듈 호출 전 전문내용 보기";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					ydUtils.displayRecord(szOperationName, recPara);
					
					YdDelegate ydDelegate           = new YdDelegate();
			    	//ydDelegate.sendMsg(recPara);
			    	ydDelegate.sendMsg(recPara);
			    	
			    	szMsg="[JSP Session] 차량작업관리 화면 상차LOT편성 : 스케줄기준체크/크레인호출 모듈 호출 성공";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					//------------------------------------------------------------------------------------------------------
				}
				
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
				recPara.setField("MODIFIER", szUser);
				recPara.setField("YD_CARLD_WRK_BOOK_ID", szYD_WBOOK_ID);
			}
			
			//------------------------------------------------------------------------------------------------------
			//	차량스케줄 수정 - 상차작업예약ID, 차량정지POINT 등
			//------------------------------------------------------------------------------------------------------
			
			intRtnVal = ydCarSchDao.updYdCarsch(recPara, 0);
			
			if (intRtnVal < 1) {
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 차량스케줄["+szYD_CAR_SCH_ID+"]의 수정 시 오류발생 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new DAOException(YdConstant.RETN_CD_FAILURE);
			}else if (intRtnVal == 0 ) {
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 차량스케줄["+szYD_CAR_SCH_ID+"]의 수정 시 차량스케줄이 존재하지 않음 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new DAOException(YdConstant.RETN_CD_NOTEXIST);
			}
			szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 차량스케줄["+szYD_CAR_SCH_ID+"]의 수정 성공";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			if( bPointSendable ) {
				
				//------------------------------------------------------------------------------------------------------
				/*
				 * 예약된 차량정지POINT 삭제처리
				 */
				//------------------------------------------------------------------------------------------------------
				ydStkColDao = new YdStkColDao();
				recPara = JDTORecordFactory.getInstance().create();
				//운송장비코드
				recPara.setField("TRN_EQP_CD",              szTRN_EQP_CD);
				intRtnVal = ydStkColDao.updYdStkcol1(recPara, 1);
				
				if( intRtnVal <= 0 ) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 운송장비코드["+szTRN_EQP_CD+"] - 예약된 차량정지point 삭제 시 오류발생 - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}else if( intRtnVal == 0 ) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 운송장비코드["+szTRN_EQP_CD+"] - 예약된 차량정지point 삭제 시 예약된 차량정지point가 존재하지 않습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
				
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 운송장비코드["+szTRN_EQP_CD+"] - 예약된 차량정지point 삭제 성공";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//------------------------------------------------------------------------------------------------------
				
				
				//------------------------------------------------------------------------------------------------------
				//	차량정지 Point 전송
				//------------------------------------------------------------------------------------------------------
				
				YdCommonUtils.callMatlCarArrPntReq(szTRN_EQP_CD, szWLOC_CD, szIS_EJB_CALL, this);
				
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 야드에서관리되는 개소코드["+szWLOC_CD+"] - 차량정지Point 전송";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//------------------------------------------------------------------------------------------------------
				
			}
			/*++++++++++++++++++++++++++ 차량정지Point 전송 끝 +++++++++++++++++++++++++++*/
			
			szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return szRtnMsg;
	}//end of insCarLdLot

	/**
	 * 차량작업관리 상차LOT편성 코일사용
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return 
	 * @return 
	 * @return
	 * @throws DAOException
	 */
	
	public String insCarLdLotCoil(JDTORecord [] inDto) throws DAOException {
		/*
		 * 업무기준 : 1. 차량스케줄ID로 차량스케줄이 존재하는 조회
		 * 				1-1. 존재하지 않으면 오류처리
		 * 				1-2. 존재하면 기준적용/작업자지정인 지를 판단
		 * 					1-2-1. 작업자지정이면
		 * 						1-2-1-1. 스케줄기준 체크
		 * 						1-2-1-2. 기존작업예약이 존재하지 않으면 작업예약 등록
		 * 						1-2-1-3. 작업예약 재료 등록
		 * 						1-2-1-4. 통합야드인 경우의 준비스케줄을 이용하여 작업예약 등록인 경우에는 
		 * 								해당 준비스케줄의 작업예약ID와 DEL_YN 항목에 Y를 설정
		 * 					1-2-2. 기준적용이면
		 * 						1-2-2-1. 구내운송은 차량스펙을 조회하여 야드작업허용중량을 사용
		 * 						1-2-2-2. 이송대상재를 조회
		 * 						1-2-2-3. 스케줄기준 체크
		 * 						1-2-2-4. 작업예약 등록
		 * 						1-2-2-5. 작업예약재료 등록
		 * 					1-2-3. 상차출발인 경우 예약된 차량정지point를 삭제하고 차량정지point지시요구 모듈을 호출
		 * 수정자 : 임춘수
		 * 수정일 : 2009.10.12
		 */
		int       intRtnVal    = 0;
		JDTORecord       recTemp         = null;
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecordSet    rsResult       = null;
		String szMsg        = "";		
		String szMethodName = "insCarLdLotCoil";
		String szOperationName		= "상차LOT편성";
		String szSTL_NO = null;
		String szRtnMsg = YdConstant.RETN_CD_SUCCESS;
		String szYD_CAR_SCH_ID = null;
		String szYD_CAR_USE_GP = null;
		String szTRN_EQP_CD 	= null;
		String szCAR_NO = null;
		String szCARD_NO = null;
		String szYD_STR_LOC = null;
		String szYD_GP = "";
		String szYD_BAY_GP = "";
		String szYD_SCH_CD = "";
		String szYD_SCH_PRIOR = "";
		String szYD_AIM_YD_GP = null;
		String szYD_AIM_BAY_GP = null;
		String szCRUD = null;
		String szUser = null;
		String szWLOC_CD = null;
		String szPREV_YD_STK_COL_GP = "";
		String szYD_STK_COL_GP = "";
		String szYD_STK_BED_NO = "";
		String szYD_STK_LYR_NO = "";
		String szYD_CAR_LOT_TYPE = "";
		String szCAR_POINT = null;
		String szBAY = null;
		String szYD_AIM_RT_GP = null;
		String szYD_PNT_CD1 = "";
		String szPREP_TYPE		= null;
		String szYD_PREP_SCH_ID	= null;
		String szYD_CAR_PROG_STAT	= null;
		String szCurDate		= YdUtils.getCurDate("yyyyMMddHHmmss");
		
		int intYD_WRK_ALW_L = 0;				//야드작업허용길이
		int intYD_WRK_ALW_W = 0;				//야드작업허용폭
		int intYD_WRK_ALW_SKID_PITCH = 0;		//야드작업허용Skid간격
		int intYD_WRK_ALW_SH = 0;				//야드작업허용매수
		int intYD_WRK_ALW_WT = 0;				//야드작업허용중량
		
		int intYD_MTL_WT = 0;					//야드재료중량
		int intYD_MTL_WT_SUM = 0;
		int intYD_MTL_SH = 0;
		
		boolean bRtnVal = false;
		boolean bPointSendable = false;
		
		String szYD_WBOOK_ID = null;
		String szIS_EJB_CALL	= "";
		
		YdCarSchDao ydCarSchDao = new YdCarSchDao();
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		YdStockDao ydStockDao = new YdStockDao();
		YdCarSpecDao ydCarSpecDao = null;
		YdPrepSchDao ydPrepSchDao = null;
		YdPrepMtlDao ydPrepMtlDao = null;
		YdStkColDao ydStkColDao		= null;
		
		
		JDTORecordSet rsWrkMtl = JDTORecordFactory.getInstance().createRecordSet("stock");
		JDTORecordSet rsSort = JDTORecordFactory.getInstance().createRecordSet("stock");
		JDTORecord recSort = null;
		
		try {
			szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//발지개소코드
			szWLOC_CD = ydDaoUtils.paraRecChkNull(inDto[0], "WLOC_CD3");
			
			szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 - 발지개소코드["+szWLOC_CD+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//+++++++++++++++ 1. 차량스케줄을 먼저 조회해서 존재하는 지를 확인 ++++++++++++++++++++++
			szYD_CAR_SCH_ID = yddatautil.setDataDefault(inDto[0].getField("YD_CAR_SCH_ID"), "");
			szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 차량스케줄ID["+szYD_CAR_SCH_ID+"]로 조회 전";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
//getYdCarsch0			
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, outRecSet, 0);
			if( intRtnVal == 0 ) {
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 차량스케줄ID["+szYD_CAR_SCH_ID+"]이 존재하지 않습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NOTEXIST;
			}else if( intRtnVal < 0 ) {
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 차량스케줄ID["+szYD_CAR_SCH_ID+"]로 조회시 오류발생 : 반환값  - " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}
			outRecSet.first();
			recPara = outRecSet.getRecord();
			
			szYD_CAR_PROG_STAT 	= yddatautil.setDataDefault(recPara.getField("YD_CAR_PROG_STAT"), "");		//차량진행상태
			szYD_CAR_USE_GP 	= yddatautil.setDataDefault(recPara.getField("YD_CAR_USE_GP"), "");
			szTRN_EQP_CD 		= yddatautil.setDataDefault(recPara.getField("TRN_EQP_CD"), "");
			szCAR_NO 			= yddatautil.setDataDefault(recPara.getField("CAR_NO"), "");
			szCARD_NO 			= yddatautil.setDataDefault(recPara.getField("CARD_NO"), "");
			
			szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 차량스케줄ID["+szYD_CAR_SCH_ID+"]로 차량스케줄이 존재합니다 - 운송장비코드["+szTRN_EQP_CD+"], 차량번호["+szCAR_NO+"], 카드번호["+szCARD_NO+"], 차량사용구분["+szYD_CAR_USE_GP+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			
			//기준적용인 지 작업자지정인 지를 판단
			szYD_CAR_LOT_TYPE = ydDaoUtils.paraRecChkNull(inDto[0], "YD_CAR_LOT_TYPE");
			if( szYD_CAR_LOT_TYPE.equals("M") )	{
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 - 작업자지정";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//+++++++++++++++++++++++ 2. 스케줄기준 확인 +++++++++++++++++++++++
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] : 스케줄기준 확인 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				szYD_STR_LOC = yddatautil.setDataDefault(inDto[0].getField("YD_STR_LOC"), "");
				if( !szYD_STR_LOC.equals("")) {
					szYD_GP 	= szYD_STR_LOC.substring(0, 1);
					szYD_BAY_GP = szYD_STR_LOC.substring(1, 2);
                 
					if(szYD_GP.equals("J")) {
						if(szYD_STR_LOC.substring(1, 2).equals("B")||szYD_STR_LOC.substring(1, 2).equals("C")){
							if(Integer.parseInt(szYD_STR_LOC.substring(2, 4)) < 31){
								szYD_SCH_CD = szYD_STR_LOC.substring(0, 2) + "PT52UM";  //제품이송
							} else {
								szYD_SCH_CD = szYD_STR_LOC.substring(0, 2) + "PT02UM";  //제품이송
							}
						} else {
							szYD_SCH_CD = szYD_STR_LOC.substring(0, 2) + "PT02UM";  //제품이송
						}
					} else {
						szYD_SCH_CD = szYD_STR_LOC.substring(0, 2) + "PT01UM";  //소재이송
					}
					
					recPara = JDTORecordFactory.getInstance().create();
					intRtnVal = YdCommonUtils.getCrnInfoByCrnSchRule(szYD_SCH_CD, recPara);
					if( intRtnVal == -1 ) {
						szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] : 스케줄기준["+szYD_SCH_CD+"]이 금지상태입니다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return YdConstant.RETN_CRN_SCH_PROH;
					}else if( intRtnVal < 0 ) {
						szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] : 스케줄기준["+szYD_SCH_CD+"]조회 시 오류발생 : 반환값 - " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return YdConstant.RETN_CD_FAILURE;
					}
					
					szYD_SCH_PRIOR = yddatautil.setDataDefault(recPara.getField("YD_SCH_PRIOR"), "");
					
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] : 스케줄기준 확인 끝";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				
				//+++++++++++++++++++++++ 3. 작업예약 등록 +++++++++++++++++++++++
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] : 작업예약 등록 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				szYD_PREP_SCH_ID 	= yddatautil.setDataDefault(inDto[0].getField("YD_PREP_SCH_ID"), "");
				szPREP_TYPE 		= yddatautil.setDataDefault(inDto[0].getField("PREP_TYPE"), "");
				szYD_WBOOK_ID 		= yddatautil.setDataDefault(inDto[0].getField("YD_WBOOK_ID"), "");
				
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] - 기존의 준비스케줄을 작업예약에 등록할 지 유무 판단["+szPREP_TYPE+"] - 준비스케줄ID["+szYD_PREP_SCH_ID+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				if( szYD_WBOOK_ID.equals("") ) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] - 기존에 등록된 작업예약ID가 존재하지 않습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}else{
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] - 기존에 등록된 작업예약ID["+szYD_WBOOK_ID+"]가 존재합니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
				
				szYD_AIM_YD_GP 	= yddatautil.setDataDefault(inDto[0].getField("YD_AIM_YD_GP"), "");
				szYD_AIM_BAY_GP = yddatautil.setDataDefault(inDto[0].getField("YD_AIM_BAY_GP"), "");
				szUser 			= yddatautil.setDataDefault(inDto[0].getField("YD_USER_ID"), "");
				
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] - 첫번째 재료의 목표야드["+szYD_AIM_YD_GP+"], 목표동["+szYD_AIM_BAY_GP+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				if( szPREP_TYPE.equals("Y") && !szYD_PREP_SCH_ID.equals("")) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] - 준비스케줄ID["+szYD_PREP_SCH_ID+"]의 목표야드와 목표동을 사용하기 위해서 조회 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					/*
					 * 준비스케줄ID가 존재하는 경우는 이송LOT편성된 정보에서 목표야드와 목표동 구분을 조회해서 사용
					 * 수정자 : 임춘수
					 * 수정일 : 2009.10.26
					 */
					rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_PREP_SCH_ID",   szYD_PREP_SCH_ID);
					ydPrepSchDao = new YdPrepSchDao();
//getYdPrepsch0					
					intRtnVal = ydPrepSchDao.getYdPrepsch(recPara, rsResult, 0);
					if( intRtnVal <= 0) {
						szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] - 준비스케줄ID["+szYD_PREP_SCH_ID+"]로 조회 시 오류발생 - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}else{
						rsResult.first();
						recPara = rsResult.getRecord();
						
						//야드목표야드구분
						szYD_AIM_YD_GP  = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_YD_GP");
						//야드목표동구분
						szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_BAY_GP");
						//스케줄코드
						szYD_SCH_CD = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_CD");
						
						szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] - 준비스케줄ID["+szYD_PREP_SCH_ID+"]로 조회 시 데이타가 존재합니다. - 목표야드["+szYD_AIM_YD_GP+"], 목표동["+szYD_AIM_BAY_GP+"], 스케줄코드["+szYD_SCH_CD+"]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
					}
					
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] - 준비스케줄ID["+szYD_PREP_SCH_ID+"]가 존재하므로 목표야드["+szYD_AIM_YD_GP+"]와 목표동["+szYD_AIM_BAY_GP+"], 스케줄코드["+szYD_SCH_CD+"]을 사용";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
				
				if( szYD_WBOOK_ID.equals("")) {
					//신규작업예약 등록
					szYD_WBOOK_ID = ydWrkbookDao.getYdWrkbookId();
					
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
					recPara.setField("YD_GP", 		  szYD_GP);
					recPara.setField("YD_BAY_GP", 	  szYD_BAY_GP);
					recPara.setField("YD_SCH_PRIOR",  szYD_SCH_PRIOR);
					recPara.setField("YD_SCH_CD", 	  szYD_SCH_CD);
					recPara.setField("REGISTER", 	  szUser);
					recPara.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
					recPara.setField("TRN_EQP_CD", 	  szTRN_EQP_CD);
					recPara.setField("YD_AIM_YD_GP",  szYD_AIM_YD_GP);
					recPara.setField("YD_AIM_BAY_GP", szYD_AIM_BAY_GP);
					
					//작업예약 INSERT
					intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
					if (intRtnVal < 1) {
						szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] : 작업예약 데이터 등록 중 에러";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						return YdConstant.RETN_CD_FAILURE;
					}
					
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] : 신규작업예약 등록["+szYD_WBOOK_ID+"] 끝";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}else{
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] - 기존에 등록된 작업예약ID["+szYD_WBOOK_ID+"]로 작업예약 재료 등록 시 사용합니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				
				//+++++++++++++++++++++++ 4. 작업예약재료 등록 +++++++++++++++++++++++
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] : 작업예약재료 등록[작업예약ID : "+szYD_WBOOK_ID+"] 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				//여러건의 로우를 처리 - 등록 및 수정
				for(int i = 0; i < inDto.length; i++ ) {
					recPara         = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
					recPara.setField("REGISTER", 	  szUser);
					
					szCRUD = yddatautil.setDataDefault(inDto[i].getField("CRUD"), "");
					if( !szPREP_TYPE.equals("Y")) {
						if( !szCRUD.equals("C") ) continue;
					}
					//항목 편집
					szSTL_NO = yddatautil.setDataDefault(inDto[i].getField("STL_NO"), "");
					
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] - "+(i + 1)+"번째 재료번호["+szSTL_NO+"]로 적치단 조회 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					//[1].재료의 저장품테이블에 존재하는 지 확인
					
					//[2].재료의 적치단 상태를 확인
					if( !szWLOC_CD.equals(YdConstant.WLOC_CD_A_PLATE_PLANT) && 
						!szWLOC_CD.equals(YdConstant.WLOC_CD_B_PLATE_PLANT) &&	
						!szWLOC_CD.equals(YdConstant.WLOC_CD_C_HR_PLANT)) {
						rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
						bRtnVal = YdCommonUtils.chkGetStlStkLyr(szSTL_NO, "C", rsResult);
						if( !bRtnVal ) {
							szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] - "+(i + 1)+"번째 재료번호["+szSTL_NO+"]가 적치단에 존재하지 않습니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							throw new DAOException(YdConstant.RETN_CD_NOTEXIST);
						}
						rsResult.first();
						recTemp = rsResult.getRecord();
						szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_COL_GP");
						szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_NO");
						szYD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_NO");
						//[3]. 작업예약재료 등록
						szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] - "+(i + 1)+"번째 재료번호["+szSTL_NO+"]로 적치단 정보 ";
						szMsg += "- 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"], 적치단["+szYD_STK_LYR_NO+"]";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}else{
						szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] - "+(i + 1)+"번째 재료번호["+szSTL_NO+"]로 적치단 정보가 없음 ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
					//재료번호
					recPara.setField("STL_NO", 		   szSTL_NO);
					//적치열구분
					recPara.setField("YD_STK_COL_GP",  szYD_STK_COL_GP);
					//적치BED번호
					recPara.setField("YD_STK_BED_NO",  szYD_STK_BED_NO);
					//적치단번호
					recPara.setField("YD_STK_LYR_NO",  szYD_STK_LYR_NO);
					//권상모음순서
					recPara.setField("YD_UP_COLL_SEQ", "" + (i + 1));
					
					rsWrkMtl.addRecord(recPara);
				}
				
				String szRtnVal =YdConstant.RETN_CD_SUCCESS;
				
				if(szYD_STK_COL_GP.substring(0, 1).equals("H") ||
						szYD_STK_COL_GP.substring(0, 1).equals("J")){
					
					JDTORecord recInRsSet = null;
					for( int Loop_i = 1; Loop_i <= rsWrkMtl.size(); Loop_i++) {
						rsWrkMtl.absolute(Loop_i);
						recInRsSet = JDTORecordFactory.getInstance().create();
						recInRsSet.setRecord(rsWrkMtl.getRecord());
						
						rsSort.addRecord(recInRsSet);
					
					}
				}else {
					szRtnVal = sortUpColSeqCoil(rsWrkMtl, szYD_AIM_RT_GP, rsSort); 
				
				}	
				//String szRtnVal = sortUpColSeqCoil(rsWrkMtl, szYD_AIM_RT_GP, rsSort); 
				
				ydUtils.putLog(szSessionName, szMethodName, "sortUpColSeq rtn-->" + rsSort.size() , YdConstant.DEBUG);
				
				if (szRtnVal == YdConstant.RETN_CD_SUCCESS) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 작업예약재료 정렬 성공 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					for(int Loop_i = 1; Loop_i <= rsSort.size(); Loop_i++) {
						rsSort.absolute(Loop_i);
						recSort         = JDTORecordFactory.getInstance().create();
						recSort 		= rsSort.getRecord();
						
						recPara         = JDTORecordFactory.getInstance().create();
						ydUtils.putLog(szSessionName, szMethodName, szYD_WBOOK_ID, YdConstant.DEBUG);
						recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
						recPara.setField("REGISTER", 	  szUser);
						//재료번호
						recPara.setField("STL_NO", 		   ydDaoUtils.paraRecChkNull(recSort, "STL_NO"));
						//적치열구분
						recPara.setField("YD_STK_COL_GP",  ydDaoUtils.paraRecChkNull(recSort, "YD_STK_COL_GP"));
						//적치BED번호
						recPara.setField("YD_STK_BED_NO",  ydDaoUtils.paraRecChkNull(recSort, "YD_STK_BED_NO"));
						//적치단번호
						recPara.setField("YD_STK_LYR_NO",  ydDaoUtils.paraRecChkNull(recSort, "YD_STK_LYR_NO"));
						//권상모음순서
						recPara.setField("YD_UP_COLL_SEQ", "" + Loop_i);
						
						ydUtils.putLog(szSessionName, szMethodName, "kkk", YdConstant.DEBUG);
						
						// 작업예약재료 테이블에 등록한다.
						intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
						
						if (intRtnVal < 1) {
							szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] : 작업예약재료 데이터 등록 중 에러";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							throw new DAOException(YdConstant.RETN_CD_FAILURE);
						}
						
						szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] - "+Loop_i+"번째 재료번호["+ydDaoUtils.paraRecChkNull(recSort, "STL_NO")+"]를 작업예약재료[작업예약ID : "+szYD_WBOOK_ID+"]로 등록 성공 ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
					
				} else {
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 작업예약재료 정렬 실패 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					rsWrkMtl.first();
					
					for(int Loop_i = 0; Loop_i < rsWrkMtl.size(); Loop_i++) {
						recSort         = JDTORecordFactory.getInstance().create();
						recSort = rsWrkMtl.getRecord(Loop_i);
						
						recPara         = JDTORecordFactory.getInstance().create();
						recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
						recPara.setField("REGISTER", 	  szUser);
						//재료번호
						recPara.setField("STL_NO", 		   ydDaoUtils.paraRecChkNull(recSort, "STL_NO"));
						//적치열구분
						recPara.setField("YD_STK_COL_GP",  ydDaoUtils.paraRecChkNull(recSort, "YD_STK_COL_GP"));
						//적치BED번호
						recPara.setField("YD_STK_BED_NO",  ydDaoUtils.paraRecChkNull(recSort, "YD_STK_BED_NO"));
						//적치단번호
						recPara.setField("YD_STK_LYR_NO",  ydDaoUtils.paraRecChkNull(recSort, "YD_STK_LYR_NO"));
						//권상모음순서
						recPara.setField("YD_UP_COLL_SEQ", ydDaoUtils.paraRecChkNull(recSort, "YD_UP_COLL_SEQ"));
						
						// 작업예약재료 테이블에 등록한다.
						intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
						
						if (intRtnVal < 1) {
							szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] : 작업예약재료 데이터 등록 중 에러";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							throw new DAOException(YdConstant.RETN_CD_FAILURE);
						}
						
						szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] - "+Loop_i+"번째 재료번호["+ydDaoUtils.paraRecChkNull(recSort, "STL_NO")+"]를 작업예약재료[작업예약ID : "+szYD_WBOOK_ID+"]로 등록 성공 ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
					
				}
				
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] : 작업예약재료[작업예약ID : "+szYD_WBOOK_ID+"] 등록 끝";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				if( szPREP_TYPE.equals("Y") && !szYD_PREP_SCH_ID.equals("")) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] : 작업예약에 등록 후 준비스케줄["+szYD_PREP_SCH_ID+"] 삭제 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

					ydPrepMtlDao = new YdPrepMtlDao();
					
					recPara         = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_PREP_SCH_ID",   	szYD_PREP_SCH_ID);
					recPara.setField("DEL_YN",   			"Y");
					recPara.setField("MODIFIER",   			szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
					//준비재료 삭제처리
					intRtnVal = ydPrepMtlDao.uptDelYdPrepmtlByPrepSchId(recPara);
					
					//ydPrepSchDao = new YdPrepSchDao();
					
					//준비스케줄 삭제처리
					recPara.setField("YD_WBOOK_ID",   		szYD_WBOOK_ID);
					intRtnVal =  ydPrepSchDao.updDelYdPrepsch(recPara);
					
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[작업자지정] : 준비스케줄["+szYD_PREP_SCH_ID+"] 삭제 성공 - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					
				}
				//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			}else{
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 - 기준적용";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				
				//++++++++++++++++++++++++ 차량스펙 조회 시작 +++++++++++++++++++++++++++++++++
				
				//차량스펙을 조회해서 차량작업가능매수만큼 LOT편성
				ydCarSpecDao = new YdCarSpecDao();
				outRecSet       = JDTORecordFactory.getInstance().createRecordSet("carSpec");
				recPara         = JDTORecordFactory.getInstance().create();
				if( szYD_CAR_USE_GP.equals("L") ) {					//구내운송차량
					recPara.setField("TRN_EQP_CD", szTRN_EQP_CD);
					
//getYdCarspec2					
					intRtnVal = ydCarSpecDao.getYdCarspec(recPara, outRecSet, 2);
				}else if( szYD_CAR_USE_GP.equals("G") ) {			
					//recPara.setField("CAR_NO", szCAR_NO);
					//intRtnVal = ydCarSpecDao.getYdCarspec(recPara, outRecSet, 4);
					intRtnVal = 1;
				}else{
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] - 지원하지 않는 차량사용구분["+szYD_CAR_USE_GP+"]입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					throw new DAOException(YdConstant.RETN_CD_FAILURE);
				}
				/*
				 * 출하차량인 경우에는 차량스펙을 관리하지 않으므로 차후 로직 변경 필요.
				 * 수정자 : 임춘수
				 */
				
				if( intRtnVal == 0 ) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] - 해당차량에 대한 차량스펙이 존재하지 않습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					throw new DAOException(YdConstant.RETN_CD_FAILURE);
				}else if( intRtnVal < 0 ) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] - 해당차량에 대한 차량스펙 조회 시 오류발생 - 반환값 : " +intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					throw new DAOException(YdConstant.RETN_CD_FAILURE);
				}
				
				if( szYD_CAR_USE_GP.equals("L") ) {					//구내운송차량
					outRecSet.first();
					recPara = outRecSet.getRecord();
					
					intYD_WRK_ALW_L = ydDaoUtils.paraRecChkNullInt(recPara, "YD_WRK_ALW_L");
					intYD_WRK_ALW_W = ydDaoUtils.paraRecChkNullInt(recPara, "YD_WRK_ALW_W"); 
					intYD_WRK_ALW_SKID_PITCH  = ydDaoUtils.paraRecChkNullInt(recPara, "YD_WRK_ALW_SKID_PITCH");
					intYD_WRK_ALW_SH  = ydDaoUtils.paraRecChkNullInt(recPara, "YD_WRK_ALW_SH");
					intYD_WRK_ALW_WT  = ydDaoUtils.paraRecChkNullInt(recPara, "YD_WRK_ALW_WT");
					
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] - 차량사용구분["+szYD_CAR_USE_GP+"], 운송장비코드["+szTRN_EQP_CD+"], 차량번호["+szCAR_NO+"]에 대한 차량스펙 : ";
					szMsg += "야드작업허용길이["+intYD_WRK_ALW_L+"], 야드작업허용폭["+intYD_WRK_ALW_W+"], 야드작업허용Skid간격["+intYD_WRK_ALW_SKID_PITCH+"], 야드작업허용매수["+intYD_WRK_ALW_SH+"], 야드작업허용중량["+intYD_WRK_ALW_WT+"]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
				//++++++++++++++++++++++++ 차량스펙 조회 끝 +++++++++++++++++++++++++++++++++
				
				//++++++++++++++++++++++++ 대상재 조회 시작 +++++++++++++++++++++++++++++++++
				szCAR_POINT = ydDaoUtils.paraRecChkNull(inDto[0], "CAR_POINT3");
				szBAY = ydDaoUtils.paraRecChkNull(inDto[0], "BAY3");
				if( szCAR_POINT.equals("") ) {
					szCAR_POINT = szBAY;
				}else{
					szCAR_POINT = szCAR_POINT.substring(0, 2);
				}
				szUser = yddatautil.setDataDefault(inDto[0].getField("YD_USER_ID"), "");

				szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(inDto[0], "YD_AIM_RT_GP");
				outRecSet       = JDTORecordFactory.getInstance().createRecordSet("stock");
				recPara         = JDTORecordFactory.getInstance().create();
				recPara.setField("SPOS_WLOC_CD",	    szWLOC_CD);
				recPara.setField("YD_AIM_RT_GP",	    szYD_AIM_RT_GP);
				recPara.setField("YD_STK_COL_GP",	    szCAR_POINT);
				
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 - 기준적용 시 발지개소코드["+szWLOC_CD+"], 야드목표행선구분["+szYD_AIM_RT_GP+"], 적치열구분["+szCAR_POINT+"]로 대상재 조회 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//getYdStock122				
				intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 122);
				
				if( intRtnVal == 0 ) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 - 기준적용 시 발지개소코드["+szWLOC_CD+"], 야드목표행선구분["+szYD_AIM_RT_GP+"], 적치열구분["+szCAR_POINT+"]로 대상재가 존재하지 않습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}else if( intRtnVal < 0 ) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 - 기준적용 시 발지개소코드["+szWLOC_CD+"], 야드목표행선구분["+szYD_AIM_RT_GP+"], 적치열구분["+szCAR_POINT+"]로 대상재조회 시 오류발생 - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					throw new DAOException(YdConstant.RETN_CD_FAILURE);
				}
				
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 - 기준적용 시 발지개소코드["+szWLOC_CD+"], 야드목표행선구분["+szYD_AIM_RT_GP+"], 적치열구분["+szCAR_POINT+"]로 대상재 조회된 갯수 - " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				intYD_MTL_SH = outRecSet.size();
				
				//if( intYD_MTL_SH  > intYD_WRK_ALW_SH ) intYD_MTL_SH = intYD_WRK_ALW_SH;
				
				
				outRecSet.first();
				recPara = outRecSet.getRecord();
				szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_YD_GP");
				szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_BAY_GP");
				szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");
				szPREV_YD_STK_COL_GP = szYD_STK_COL_GP;
				szYD_GP = szYD_STK_COL_GP.substring(0, 1);
				szYD_BAY_GP = szYD_STK_COL_GP.substring(1, 2);
				
//				szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT01UM";
				if(szYD_GP.equals("J")) {
					if(szYD_STK_COL_GP.substring(1, 2).equals("B")||szYD_STK_COL_GP.substring(1, 2).equals("C")){
						if(Integer.parseInt(szYD_STK_COL_GP.substring(2, 4)) < 31){
							szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT52UM";  //제품이송
						} else {
							szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT02UM";  //제품이송
						}
					} else {
						szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT02UM";  //제품이송
					}

				} else {
					szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT01UM";  //소재이송
				}
				
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] : 1번째 대상재의 적치열구분["+szYD_STK_COL_GP+"], 스케줄코드["+szYD_SCH_CD+"], 야드목표야드구분["+szYD_AIM_YD_GP+"], 야드목표동구분["+szYD_AIM_BAY_GP+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//+++++++++++++++++++++++ 스케줄기준 확인 시작 +++++++++++++++++++++++
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] : 스케줄기준 확인 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
				recPara = JDTORecordFactory.getInstance().create();
				intRtnVal = YdCommonUtils.getCrnInfoByCrnSchRule(szYD_SCH_CD, recPara);
				if( intRtnVal == -1 ) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] : 스케줄기준["+szYD_SCH_CD+"]이 금지상태입니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CRN_SCH_PROH;
				}else if( intRtnVal < 0 ) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] : 스케줄기준["+szYD_SCH_CD+"]조회 시 오류발생 : 반환값 - " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;
				}
				
				szYD_SCH_PRIOR = yddatautil.setDataDefault(recPara.getField("YD_SCH_PRIOR"), "");
				
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] : 스케줄기준 확인 끝";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//++++++++++++++++++++++++++++++스케줄기준 확인 끝++++++++++++++++++++++++++++++++
				
				//------------------------------------------------------------------------------------------------------
				//	작업예약 등록 시작
				//------------------------------------------------------------------------------------------------------
				
				//신규작업예약 등록
				szYD_WBOOK_ID = ydWrkbookDao.getYdWrkbookId();
				
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
				recPara.setField("YD_GP", 		  szYD_GP);
				recPara.setField("YD_BAY_GP", 	  szYD_BAY_GP);
				recPara.setField("YD_SCH_PRIOR", 	szYD_SCH_PRIOR);
				recPara.setField("YD_SCH_CD", 	  szYD_SCH_CD);
				recPara.setField("REGISTER", 	  szUser);
				recPara.setField("YD_CAR_USE_GP", szYD_CAR_USE_GP);
				recPara.setField("TRN_EQP_CD", 	  szTRN_EQP_CD);
				recPara.setField("YD_AIM_YD_GP",  szYD_AIM_YD_GP);
				recPara.setField("YD_AIM_BAY_GP", szYD_AIM_BAY_GP);
				
				//작업예약 INSERT
				intRtnVal = ydWrkbookDao.insYdWrkbook(recPara);
				if (intRtnVal < 1) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] : 작업예약 데이터 등록 중 에러";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;
				}
				
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] : 신규작업예약 등록["+szYD_WBOOK_ID+"] 끝";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//++++++++++++++++++++++++++++ 작업예약 등록 끝 ++++++++++++++++++++++++++++++++
				
				//------------------------------------------------------------------------------------------------------
				//	작업예약재료 등록 시작
				//------------------------------------------------------------------------------------------------------
				
				
				
				
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] - 대상재매수 : " + intYD_MTL_SH;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

				for(int i = 1; i <= intYD_MTL_SH; i++) {
					recPara         = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
					recPara.setField("REGISTER", 	  szUser);
					
					outRecSet.absolute(i);
					recTemp = outRecSet.getRecord();
					szSTL_NO = ydDaoUtils.paraRecChkNull(recTemp, "STL_NO");
					szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_COL_GP");
					szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_NO");
					szYD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_NO");
					intYD_MTL_WT = ydDaoUtils.paraRecChkNullInt(recTemp, "YD_MTL_WT");
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] : ["+i+"]번째 대상재["+szSTL_NO+"]의 재료중량["+intYD_MTL_WT+"]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					intYD_MTL_WT_SUM += intYD_MTL_WT;
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] : 총재료중량["+intYD_MTL_WT_SUM+"] --- 차량작업가능중량["+intYD_WRK_ALW_WT+"]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					if( szYD_CAR_USE_GP.equals("L") ) {					//구내운송차량
						if( intYD_MTL_WT_SUM > intYD_WRK_ALW_WT ) break;
					}
					
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] : 1번째 대상재의 야드동["+szPREV_YD_STK_COL_GP+"] - ["+i+"]번째 대상재의 야드동["+szYD_STK_COL_GP+"]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					if( i > 1 ) {
						if( !szPREV_YD_STK_COL_GP.substring(0, 2).equals(szYD_STK_COL_GP.substring(0, 2)) ) {
							szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] : 1번째 대상재의 야드동["+szPREV_YD_STK_COL_GP+"] - ["+i+"]번째 대상재의 야드동["+szYD_STK_COL_GP+"]이 다르므로 작업예약재료에 더 이상 등록하지 않음.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							break;
						}
					}
					
					
					
					//재료번호
					recPara.setField("STL_NO", 		   szSTL_NO);
					//적치열구분
					recPara.setField("YD_STK_COL_GP",  szYD_STK_COL_GP);
					//적치BED번호
					recPara.setField("YD_STK_BED_NO",  szYD_STK_BED_NO);
					//적치단번호
					recPara.setField("YD_STK_LYR_NO",  szYD_STK_LYR_NO);
					//권상모음순서
					recPara.setField("YD_UP_COLL_SEQ", "" + (i + 1));
					
					rsWrkMtl.addRecord(recPara);
					
				}
				
//				String szRtnVal = sortUpColSeqCoil(rsWrkMtl, szYD_AIM_RT_GP, rsSort); 
				String szRtnVal =YdConstant.RETN_CD_SUCCESS;
				
				if(szYD_STK_COL_GP.substring(0, 1).equals("H") ||
						szYD_STK_COL_GP.substring(0, 1).equals("J")){
					
					JDTORecord recInRsSet = null;
					for( int Loop_i = 1; Loop_i <= rsWrkMtl.size(); Loop_i++) {
						rsWrkMtl.absolute(Loop_i);
						recInRsSet = JDTORecordFactory.getInstance().create();
						recInRsSet.setRecord(rsWrkMtl.getRecord());
						
						rsSort.addRecord(recInRsSet);
					
					}
				}else {
					szRtnVal = sortUpColSeqCoil(rsWrkMtl, szYD_AIM_RT_GP, rsSort); 
				
				}	
				
				if (szRtnVal == YdConstant.RETN_CD_SUCCESS) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 작업예약재료 정렬 성공 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					for(int Loop_i = 1; Loop_i <= rsSort.size(); Loop_i++) {
						rsSort.absolute(Loop_i);
						recSort         = JDTORecordFactory.getInstance().create();
						recSort = rsSort.getRecord();
						
						recPara         = JDTORecordFactory.getInstance().create();
						recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
						recPara.setField("REGISTER", 	  szUser);
						//재료번호
						recPara.setField("STL_NO", 		   ydDaoUtils.paraRecChkNull(recSort, "STL_NO"));
						//적치열구분
						recPara.setField("YD_STK_COL_GP",  ydDaoUtils.paraRecChkNull(recSort, "YD_STK_COL_GP"));
						//적치BED번호
						recPara.setField("YD_STK_BED_NO",  ydDaoUtils.paraRecChkNull(recSort, "YD_STK_BED_NO"));
						//적치단번호
						recPara.setField("YD_STK_LYR_NO",  ydDaoUtils.paraRecChkNull(recSort, "YD_STK_LYR_NO"));
						//권상모음순서
						recPara.setField("YD_UP_COLL_SEQ", "" + Loop_i);
						
						// 작업예약재료 테이블에 등록한다.
						intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
						
						if (intRtnVal < 1) {
							szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] : 작업예약재료 데이터 등록 중 에러";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							throw new DAOException(YdConstant.RETN_CD_FAILURE);
						}
						
						szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] - "+Loop_i+"번째 재료번호["+ydDaoUtils.paraRecChkNull(recSort, "STL_NO")+"]를 작업예약재료[작업예약ID : "+szYD_WBOOK_ID+"]로 등록 성공 ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
					
				} else {
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 작업예약재료 정렬 실패 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					for(int Loop_i = 1; Loop_i <= rsWrkMtl.size(); Loop_i++) {
						rsWrkMtl.absolute(Loop_i);
						recSort         = JDTORecordFactory.getInstance().create();
						recSort = rsWrkMtl.getRecord();
						
						recPara         = JDTORecordFactory.getInstance().create();
						recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
						recPara.setField("REGISTER", 	  szUser);
						//재료번호
						recPara.setField("STL_NO", 		   ydDaoUtils.paraRecChkNull(recSort, "STL_NO"));
						//적치열구분
						recPara.setField("YD_STK_COL_GP",  ydDaoUtils.paraRecChkNull(recSort, "YD_STK_COL_GP"));
						//적치BED번호
						recPara.setField("YD_STK_BED_NO",  ydDaoUtils.paraRecChkNull(recSort, "YD_STK_BED_NO"));
						//적치단번호
						recPara.setField("YD_STK_LYR_NO",  ydDaoUtils.paraRecChkNull(recSort, "YD_STK_LYR_NO"));
						//권상모음순서
						recPara.setField("YD_UP_COLL_SEQ", ydDaoUtils.paraRecChkNull(recSort, "YD_UP_COLL_SEQ"));
						
						// 작업예약재료 테이블에 등록한다.
						intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
						
						if (intRtnVal < 1) {
							szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] : 작업예약재료 데이터 등록 중 에러";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							throw new DAOException(YdConstant.RETN_CD_FAILURE);
						}
						
						szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] - "+Loop_i+"번째 재료번호["+ydDaoUtils.paraRecChkNull(recSort, "STL_NO")+"]를 작업예약재료[작업예약ID : "+szYD_WBOOK_ID+"]로 등록 성공 ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
					
				}
				
				
				
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성[기준적용] : 작업예약재료[작업예약ID : "+szYD_WBOOK_ID+"] 등록 끝";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//------------------------------------------------------------------------------------------------------
				
				//++++++++++++++++++++++++++++ 작업예약재료 등록 끝 ++++++++++++++++++++++++++++++++
			}
			
			/*++++++++++++++++++++++++++ 차량정지Point 전송 시작 +++++++++++++++++++++++++++*/
			
			szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 차량정지Point 전송 전 개소코드["+szWLOC_CD+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 야드에서관리되는 개소코드["+szWLOC_CD+"]이므로 차량스케줄["+szYD_CAR_SCH_ID+"] ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			szCAR_POINT = szYD_STK_COL_GP.substring(0, 2) + "PT";
			
			if( szYD_CAR_PROG_STAT.equals(YdConstant.YD_CARLD_LEV) ) {			//상차출발 시
				
				//------------------------------------------------------------------------------------------------------
				//	상차출발 시 POINT 전송되도록 처리
				//------------------------------------------------------------------------------------------------------
				
				szIS_EJB_CALL = "Y";
				bPointSendable = true;
				
				//------------------------------------------------------------------------------------------------------
				
			}else if( szYD_CAR_PROG_STAT.equals(YdConstant.YD_CARLD_ARR) ) {			//상차도착 시
				
				//------------------------------------------------------------------------------------------------------
				//	상차도착 시는 크레인스케줄메인 모듈 호출
				//------------------------------------------------------------------------------------------------------
				
				szMsg="[JSP Session] 차량작업관리 화면 상차LOT편성 : 상차도착 시 상차LOT편성 후 스케줄기준체크/크레인호출 모듈 호출 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("MSG_ID", 			"YDYDJ634");
				recPara.setField("YD_WBOOK_ID", 	szYD_WBOOK_ID);
				recPara.setField("YD_SCH_CD", 		szYD_SCH_CD);
				
				szMsg="[JSP Session] 차량작업관리 화면 상차LOT편성 : 스케줄기준체크/크레인호출 모듈 호출 전 전문내용 보기";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				ydUtils.displayRecord(szOperationName, recPara);
				
				YdDelegate ydDelegate           = new YdDelegate();
		    	//ydDelegate.sendMsg(recPara);
		    	ydDelegate.sendMsg(recPara);
		    	
		    	szMsg="[JSP Session] 차량작업관리 화면 상차LOT편성 : 스케줄기준체크/크레인호출 모듈 호출 성공";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//------------------------------------------------------------------------------------------------------
			}
			
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
			recPara.setField("MODIFIER", szUser);
			recPara.setField("YD_CARLD_WRK_BOOK_ID", szYD_WBOOK_ID);

			
			//------------------------------------------------------------------------------------------------------
			//	차량스케줄 수정 - 상차작업예약ID, 차량정지POINT 등
			//------------------------------------------------------------------------------------------------------
			
			intRtnVal = ydCarSchDao.updYdCarsch(recPara, 0);
			
			if (intRtnVal < 1) {
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 차량스케줄["+szYD_CAR_SCH_ID+"]의 수정 시 오류발생 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new DAOException(YdConstant.RETN_CD_FAILURE);
			}else if (intRtnVal == 0 ) {
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 차량스케줄["+szYD_CAR_SCH_ID+"]의 수정 시 차량스케줄이 존재하지 않음 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new DAOException(YdConstant.RETN_CD_NOTEXIST);
			}
			szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 차량스케줄["+szYD_CAR_SCH_ID+"]의 수정 성공";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			if( bPointSendable ) {
				
				//------------------------------------------------------------------------------------------------------
				/*
				 * 예약된 차량정지POINT 삭제처리
				 */
				//------------------------------------------------------------------------------------------------------
				ydStkColDao = new YdStkColDao();
				recPara = JDTORecordFactory.getInstance().create();
				//운송장비코드
				recPara.setField("TRN_EQP_CD",              szTRN_EQP_CD);
				intRtnVal = ydStkColDao.updYdStkcol1(recPara, 1);
				
				if( intRtnVal <= 0 ) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 운송장비코드["+szTRN_EQP_CD+"] - 예약된 차량정지point 삭제 시 오류발생 - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}else if( intRtnVal == 0 ) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 운송장비코드["+szTRN_EQP_CD+"] - 예약된 차량정지point 삭제 시 예약된 차량정지point가 존재하지 않습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
				
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 운송장비코드["+szTRN_EQP_CD+"] - 예약된 차량정지point 삭제 성공";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//------------------------------------------------------------------------------------------------------
				
				
				//------------------------------------------------------------------------------------------------------
				//	차량정지 Point 전송
				//------------------------------------------------------------------------------------------------------
				
				YdCommonUtils.callMatlCarArrPntReq(szTRN_EQP_CD, szWLOC_CD, szIS_EJB_CALL, this);
				
				szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 : 야드에서관리되는 개소코드["+szWLOC_CD+"] - 차량정지Point 전송";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//------------------------------------------------------------------------------------------------------
				
			}
			/*++++++++++++++++++++++++++ 차량정지Point 전송 끝 +++++++++++++++++++++++++++*/
			
			szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return szRtnMsg;
	}//end of insCarLdLotCoil
	
	
	
	/**
	 * 후판 사용함
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return 
	 * @return 
	 * @return
	 * @throws DAOException
	 */
	
	public String sortUpColSeq(JDTORecordSet rsWrkBookMtl, String szYD_AIM_RT_GP, JDTORecordSet rsSort) throws JDTOException {
		
		JDTORecord recInRsSet = null;
		JDTORecordSet rsBedCnt = null;
		
		String szRtnVal = YdConstant.RETN_CD_SUCCESS;
		String szMethodName = "sortUpColSeq";
		String szOperationName = "권상모음순서 정리";
		String szLogMsg = null;
		String[] szArrYdEqpGp = null;
		String[] szArrStlNo = null;
		String[] szArrYdStkPos = null;
		
		String[] szSortStkPos = null;
		String[] szSortStlCnt = null;
		String   szTmpStkPos = null;
		String   szTmpStlCnt = null;
		
		Vector vBedCnt = null;
		String szBeforeYdStkPos = "";
		int    intStlCnt = 0;
		
				
		String szTmpYdEqpGp = "";
		String szTmpStlNo = null;
		
		boolean blnSaveEqpId = true;
		String  szYdEqpGp   = "";
		boolean blnBaseBed = false;
		
		int intArrSize = 0;
		
		try {
			
			szLogMsg = "[" + szOperationName + "] - sortUpColSeq]--------------------- 처리 시작 --------------------------";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLog(szSessionName, szMethodName, "rsWrkBookMtl.size()" + rsWrkBookMtl.size(), YdConstant.DEBUG);
			
			szArrYdEqpGp 	= new String[rsWrkBookMtl.size()];
			szArrStlNo      = new String[rsWrkBookMtl.size()];
			szArrYdStkPos   = new String[rsWrkBookMtl.size()];
			
						
			for(int Loop_i = 1; Loop_i <= rsWrkBookMtl.size(); Loop_i++) {
				rsWrkBookMtl.absolute(Loop_i);
				recInRsSet = JDTORecordFactory.getInstance().create();
				recInRsSet.setRecord(rsWrkBookMtl.getRecord());
				
				szArrYdEqpGp[Loop_i - 1] 	= ydDaoUtils.paraRecChkNull(recInRsSet, "YD_STK_COL_GP").substring(2,4);
				szArrStlNo[Loop_i - 1] 		= ydDaoUtils.paraRecChkNull(recInRsSet, "STL_NO");
				szArrYdStkPos[Loop_i - 1] 	= ydDaoUtils.paraRecChkNull(recInRsSet, "YD_STK_COL_GP") + ydDaoUtils.paraRecChkNull(recInRsSet, "YD_STK_BED_NO");
				ydUtils.putLog(szSessionName, szMethodName, "rsWrkBookMtl.size()1111 "+szArrYdStkPos.length, YdConstant.DEBUG);
								
			} //end of for
			
			// 동일스판의 재료들인지를 확인한다.
			for(int Loop_i = 0; Loop_i<szArrYdEqpGp.length; Loop_i++) {
				if (Loop_i == 0) {
					szYdEqpGp = szArrYdEqpGp[Loop_i];
				} else {
					if (!szArrYdEqpGp[Loop_i].equals(szYdEqpGp)){
						blnSaveEqpId = false;
						break;
					}
				}
			}
			
			//동일스판이 아닐경우
			if (blnSaveEqpId == false) {
				// 정렬 시작
				intArrSize = rsWrkBookMtl.size();
				
				szLogMsg = "----권상모음순서 Sorting 작업전 목표행선[" + szYD_AIM_RT_GP + "]---";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				for(int x = 0; x<intArrSize; x++ ) {
					szLogMsg = "스판번호[" + szArrYdEqpGp[x] + "] - 재료번호[" + szArrStlNo[x] + "]";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}
				
				
				for(int x = 0; x<intArrSize-1; x++ ) {
					for(int y = 0; y<intArrSize-x-1; y++) {
						if (szYD_AIM_RT_GP.startsWith("A") ||szYD_AIM_RT_GP.startsWith("Y")) { // 4스판쪽으로 정렬
							if(Integer.parseInt(szArrYdEqpGp[y]) < Integer.parseInt(szArrYdEqpGp[y+1])) {
								szTmpYdEqpGp = szArrYdEqpGp[y];
								szTmpStlNo = szArrStlNo[y];
								szArrYdEqpGp[y] = szArrYdEqpGp[y+1];
								szArrStlNo[y] = szArrStlNo[y+1];   
								szArrYdEqpGp[y+1] = szTmpYdEqpGp;
								szArrStlNo[y+1] = szTmpStlNo;
							}
							
						} else if (szYD_AIM_RT_GP.startsWith("E") ||szYD_AIM_RT_GP.startsWith("G")) { //1스판쪽으로 정렬
							if(Integer.parseInt(szArrYdEqpGp[y]) > Integer.parseInt(szArrYdEqpGp[y+1])) {
								szTmpYdEqpGp = szArrYdEqpGp[y];
								szTmpStlNo = szArrStlNo[y];
								szArrYdEqpGp[y] = szArrYdEqpGp[y+1];
								szArrStlNo[y] = szArrStlNo[y+1];   
								szArrYdEqpGp[y+1] = szTmpYdEqpGp;
								szArrStlNo[y+1] = szTmpStlNo;
							}
							
						} else if (szYD_AIM_RT_GP.startsWith("B") || szYD_AIM_RT_GP.startsWith("C")) { //2스판쪽으로 정렬
							if( "02".equals(szArrYdEqpGp[y+1]) ||
								Integer.parseInt(szArrYdEqpGp[y]) > Integer.parseInt(szArrYdEqpGp[y+1])) {
								szTmpYdEqpGp = szArrYdEqpGp[y];
								szTmpStlNo = szArrStlNo[y];
								szArrYdEqpGp[y] = szArrYdEqpGp[y+1];
								szArrStlNo[y] = szArrStlNo[y+1];   
								szArrYdEqpGp[y+1] = szTmpYdEqpGp;
								szArrStlNo[y+1] = szTmpStlNo;
							}
						}
					}
				}
				
				szLogMsg = "----권상모음순서 Sorting 작업후 목표행선[" + szYD_AIM_RT_GP + "]---";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				for(int x = 0; x<intArrSize; x++ ) {
					szLogMsg = "스판번호[" + szArrYdEqpGp[x] + "] - 재료번호[" + szArrStlNo[x] + "]";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}
				
				
				for(int x = 0; x<intArrSize; x++ ) {
				
					for( int Loop_i = 1; Loop_i <= rsWrkBookMtl.size(); Loop_i++) {
						rsWrkBookMtl.absolute(Loop_i);
						recInRsSet = JDTORecordFactory.getInstance().create();
						recInRsSet.setRecord(rsWrkBookMtl.getRecord());
						
						
						if (szArrStlNo[x].equals(ydDaoUtils.paraRecChkNull(recInRsSet, "STL_NO"))){
							rsSort.addRecord(recInRsSet);
						}
						
					} //end of for
				}
				
				if (rsSort.size() != rsWrkBookMtl.size()) {
					szRtnVal = YdConstant.RETN_CD_FAILURE;
					return szRtnVal;
				}
				
				szLogMsg = "[" + szOperationName + "] - sortUpColSeq]--------------------- 처리 종료 --------------------------";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				return szRtnVal;
				
			} else { // 동일스판일 경우 예약재료가 많은 BED를 권상모음순서 순위조정
				rsBedCnt = JDTORecordFactory.getInstance().createRecordSet("Temp");
				
				if (szArrYdStkPos.length > 1) {
					szBeforeYdStkPos = szArrYdStkPos[0];
					intStlCnt = 1;
					for(int Loop_i = 1; Loop_i<szArrYdStkPos.length; Loop_i++) {
						if (szArrYdStkPos[Loop_i].equals(szBeforeYdStkPos)){
							intStlCnt++;
						} else {
							
							recInRsSet = JDTORecordFactory.getInstance().create();
							recInRsSet.setField("YD_STK_POS", szBeforeYdStkPos);
							recInRsSet.setField("YD_STK_CNT", ""+intStlCnt);
							rsBedCnt.addRecord(recInRsSet);
							
							szBeforeYdStkPos = szArrYdStkPos[Loop_i];
							intStlCnt = 1;
							
							
						}
							
					} //end for
					
					recInRsSet = JDTORecordFactory.getInstance().create();
					recInRsSet.setField("YD_STK_POS", szBeforeYdStkPos);
					recInRsSet.setField("YD_STK_CNT", ""+intStlCnt);
					rsBedCnt.addRecord(recInRsSet);
					
					if(rsBedCnt.size() > 1) {
						szSortStkPos = new String[rsBedCnt.size()];
						szSortStlCnt = new String[rsBedCnt.size()];
						
						intArrSize = rsBedCnt.size();
						
						for(int Loop_i = 1; Loop_i <= rsBedCnt.size(); Loop_i++){
							rsBedCnt.absolute(Loop_i);
							recInRsSet = rsBedCnt.getRecord();
							
							szSortStkPos[Loop_i - 1] = ydDaoUtils.paraRecChkNull(recInRsSet, "YD_STK_POS");
							szSortStlCnt[Loop_i - 1] = ydDaoUtils.paraRecChkNull(recInRsSet, "YD_STK_CNT");
							
						} // end for
					
						
						for(int x = 0; x<intArrSize-1; x++ ) {
							for(int y = 0; y<intArrSize-x-1; y++) {
								if(Integer.parseInt(szSortStlCnt[y]) > Integer.parseInt(szSortStlCnt[y+1])) {
									szTmpStkPos = szSortStkPos[y];
									szTmpStlCnt = szSortStlCnt[y];
									szSortStkPos[y] = szSortStkPos[y+1];
									szSortStlCnt[y] = szSortStlCnt[y+1];   
									szSortStkPos[y+1] = szTmpStkPos;
									szSortStlCnt[y+1] = szTmpStlCnt;
								}
							}
						}
						
						szLogMsg = "[" + szOperationName + "] - sortUpColSeq]--BED_CNT[" + intArrSize + "]개 입니다.";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						
						for(int x = 0; x<intArrSize; x++ ) {
							szLogMsg = "[" + szOperationName + "] - sortUpColSeq]--정렬자료 POS[" + szSortStkPos[x] + "]와 재료수[" + szSortStlCnt[x] + "] 입니다.";
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						}
						
						szLogMsg = "[" + szOperationName + "] - sortUpColSeq]--정렬된 BED의 작업능력을 체크하여 가능여부를 판단";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						for(int x = 0; x<intArrSize; x++ ) {
							blnBaseBed = this.CheckBedSpec(szSortStkPos[intArrSize-1], rsWrkBookMtl);
							if(blnBaseBed == false){
								this.RotateSortStkPosCnt(szSortStkPos, szSortStlCnt);
							} else {
								break;
							}
						}
						
						
						for(int x = 0; x<intArrSize; x++ ) {
							
							for( int Loop_i = 1; Loop_i <= rsWrkBookMtl.size(); Loop_i++) {
								rsWrkBookMtl.absolute(Loop_i);
								recInRsSet = JDTORecordFactory.getInstance().create();
								recInRsSet.setRecord(rsWrkBookMtl.getRecord());
								
								
								if (szSortStkPos[x].equals(ydDaoUtils.paraRecChkNull(recInRsSet, "YD_STK_COL_GP") + ydDaoUtils.paraRecChkNull(recInRsSet, "YD_STK_BED_NO"))){
									rsSort.addRecord(recInRsSet);
								}
								
							} //end of for
						}
						
						if (rsSort.size() != rsWrkBookMtl.size()) {
							szLogMsg = "[" + szOperationName + "] - sortUpColSeq]--작업예약재료[" + rsWrkBookMtl.size() + "]와 정렬재료[" + rsSort.size() + "]의 수가 맞지 않습니다.";
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
							szRtnVal = YdConstant.RETN_CD_FAILURE;
							return szRtnVal;
						}
						
						szLogMsg = "[" + szOperationName + "] - sortUpColSeq]--------------------- 처리 종료 --------------------------";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					} else {
						szLogMsg = "[" + szOperationName + "] - sortUpColSeq]----대상BED가 1개입니다.";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						szRtnVal = YdConstant.RETN_CD_FAILURE;
						return szRtnVal;
					}
				}
				//한매인 경우 그대로 rsSort 에 addRecord 후 return
				else {
					rsWrkBookMtl.absolute(1);
					recInRsSet = JDTORecordFactory.getInstance().create();
					recInRsSet.setRecord(rsWrkBookMtl.getRecord());
					rsSort.addRecord(recInRsSet);
					
					szLogMsg = "[" + szOperationName + "] - sortUpColSeq]-대상재가 한개일땐 그대로 return-";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					
					szLogMsg = "[" + szOperationName + "] - sortUpColSeq]--------------------- 처리 종료 --------------------------";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}
				
				
				
				return szRtnVal;
			}
		} catch (Exception e) {
			szLogMsg = szOperationName + " ++++++++++++ 권상모음순서 정리 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			szRtnVal = YdConstant.RETN_CD_FAILURE;
			return szRtnVal;
		}
		
	} //end of sortUpColSeq

	/**
	 * 코일사용함
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return 
	 * @return 
	 * @return
	 * @throws DAOException
	 */
	
	public String sortUpColSeqCoil(JDTORecordSet rsWrkBookMtl, String szYD_AIM_RT_GP, JDTORecordSet rsSort) throws JDTOException {
		
		JDTORecord recInRsSet = null;
		JDTORecordSet rsBedCnt = null;
		
		String szRtnVal = YdConstant.RETN_CD_SUCCESS;
		String szMethodName = "sortUpColSeq";
		String szOperationName = "권상모음순서 정리";
		String szLogMsg = null;
		String[] szArrYdEqpGp = null;
		String[] szArrStlNo = null;
		String[] szArrYdStkPos = null;
		
		String[] szSortStkPos = null;
		String[] szSortStlCnt = null;
		String   szTmpStkPos = null;
		String   szTmpStlCnt = null;
		
		Vector vBedCnt = null;
		String szBeforeYdStkPos = "";
		int    intStlCnt = 0;
		
				
		String szTmpYdEqpGp = "";
		String szTmpStlNo = null;
		
		boolean blnSaveEqpId = true;
		String  szYdEqpGp   = "";
		boolean blnBaseBed = false;
		
		int intArrSize = 0;
		
		try {
			
			szLogMsg = "[" + szOperationName + "] - sortUpColSeq]--------------------- 처리 시작 --------------------------";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			ydUtils.putLog(szSessionName, szMethodName, "rsWrkBookMtl.size()" + rsWrkBookMtl.size(), YdConstant.DEBUG);
			
			szArrYdEqpGp 	= new String[rsWrkBookMtl.size()];
			szArrStlNo      = new String[rsWrkBookMtl.size()];
			szArrYdStkPos   = new String[rsWrkBookMtl.size()];
			
						
			for(int Loop_i = 1; Loop_i <= rsWrkBookMtl.size(); Loop_i++) {
				rsWrkBookMtl.absolute(Loop_i);
				recInRsSet = JDTORecordFactory.getInstance().create();
				recInRsSet.setRecord(rsWrkBookMtl.getRecord());
				
				szArrYdEqpGp[Loop_i - 1] 	= ydDaoUtils.paraRecChkNull(recInRsSet, "YD_STK_COL_GP").substring(2,4);
				szArrStlNo[Loop_i - 1] 		= ydDaoUtils.paraRecChkNull(recInRsSet, "STL_NO");
				szArrYdStkPos[Loop_i - 1] 	= ydDaoUtils.paraRecChkNull(recInRsSet, "YD_STK_COL_GP") + ydDaoUtils.paraRecChkNull(recInRsSet, "YD_STK_BED_NO");
				ydUtils.putLog(szSessionName, szMethodName, "rsWrkBookMtl.size()1111", YdConstant.DEBUG);
								
			} //end of for
			
			// 동일스판의 재료들인지를 확인한다.
			for(int Loop_i = 0; Loop_i<szArrYdEqpGp.length; Loop_i++) {
				if (Loop_i == 0) {
					szYdEqpGp = szArrYdEqpGp[Loop_i];
				} else {
					if (!szArrYdEqpGp[Loop_i].equals(szYdEqpGp)){
						blnSaveEqpId = false;
						break;
					}
				}
			}
			
			//동일스판이 아닐경우
			if (blnSaveEqpId == false) {
				// 정렬 시작
				intArrSize = rsWrkBookMtl.size();
				
				szLogMsg = "----권상모음순서 Sorting 작업전 목표행선[" + szYD_AIM_RT_GP + "]---";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				for(int x = 0; x<intArrSize; x++ ) {
					szLogMsg = "스판번호[" + szArrYdEqpGp[x] + "] - 재료번호[" + szArrStlNo[x] + "]";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}
				
				
				for(int x = 0; x<intArrSize-1; x++ ) {
					for(int y = 0; y<intArrSize-x-1; y++) {
						if (szYD_AIM_RT_GP.startsWith("A") ||szYD_AIM_RT_GP.startsWith("Y")) { // 4스판쪽으로 정렬
							if(Integer.parseInt(szArrYdEqpGp[y]) < Integer.parseInt(szArrYdEqpGp[y+1])) {
								szTmpYdEqpGp = szArrYdEqpGp[y];
								szTmpStlNo = szArrStlNo[y];
								szArrYdEqpGp[y] = szArrYdEqpGp[y+1];
								szArrStlNo[y] = szArrStlNo[y+1];   
								szArrYdEqpGp[y+1] = szTmpYdEqpGp;
								szArrStlNo[y+1] = szTmpStlNo;
							}
							
						} else if (szYD_AIM_RT_GP.startsWith("E") ||szYD_AIM_RT_GP.startsWith("G")) { //1스판쪽으로 정렬
							if(Integer.parseInt(szArrYdEqpGp[y]) > Integer.parseInt(szArrYdEqpGp[y+1])) {
								szTmpYdEqpGp = szArrYdEqpGp[y];
								szTmpStlNo = szArrStlNo[y];
								szArrYdEqpGp[y] = szArrYdEqpGp[y+1];
								szArrStlNo[y] = szArrStlNo[y+1];   
								szArrYdEqpGp[y+1] = szTmpYdEqpGp;
								szArrStlNo[y+1] = szTmpStlNo;
							}
							
						} else if (szYD_AIM_RT_GP.startsWith("B") || szYD_AIM_RT_GP.startsWith("C")) { //2스판쪽으로 정렬
							if( "02".equals(szArrYdEqpGp[y+1]) ||
								Integer.parseInt(szArrYdEqpGp[y]) > Integer.parseInt(szArrYdEqpGp[y+1])) {
								szTmpYdEqpGp = szArrYdEqpGp[y];
								szTmpStlNo = szArrStlNo[y];
								szArrYdEqpGp[y] = szArrYdEqpGp[y+1];
								szArrStlNo[y] = szArrStlNo[y+1];   
								szArrYdEqpGp[y+1] = szTmpYdEqpGp;
								szArrStlNo[y+1] = szTmpStlNo;
							}
						}
					}
				}
				
				szLogMsg = "----권상모음순서 Sorting 작업후 목표행선[" + szYD_AIM_RT_GP + "]---";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				for(int x = 0; x<intArrSize; x++ ) {
					szLogMsg = "스판번호[" + szArrYdEqpGp[x] + "] - 재료번호[" + szArrStlNo[x] + "]";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}
				
				
				for(int x = 0; x<intArrSize; x++ ) {
				
					for( int Loop_i = 1; Loop_i <= rsWrkBookMtl.size(); Loop_i++) {
						rsWrkBookMtl.absolute(Loop_i);
						recInRsSet = JDTORecordFactory.getInstance().create();
						recInRsSet.setRecord(rsWrkBookMtl.getRecord());
						
						
						if (szArrStlNo[x].equals(ydDaoUtils.paraRecChkNull(recInRsSet, "STL_NO"))){
							rsSort.addRecord(recInRsSet);
						}
						
					} //end of for
				}
				
				if (rsSort.size() != rsWrkBookMtl.size()) {
					szRtnVal = YdConstant.RETN_CD_FAILURE;
					return szRtnVal;
				}
				
				szLogMsg = "[" + szOperationName + "] - sortUpColSeq]--------------------- 처리 종료 --------------------------";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				return szRtnVal;
				
			} else { // 동일스판일 경우 예약재료가 많은 BED를 권상모음순서 순위조정
				rsBedCnt = JDTORecordFactory.getInstance().createRecordSet("Temp");
				
				ydUtils.putLog(szSessionName, szMethodName, "szArrYdStkPos.length" + szArrYdStkPos.length, YdConstant.DEBUG);
				
				if (szArrYdStkPos.length > 0) {
					szBeforeYdStkPos = szArrYdStkPos[0];
					intStlCnt = 1;
					for(int Loop_i = 1; Loop_i<szArrYdStkPos.length; Loop_i++) {
						if (szArrYdStkPos[Loop_i].equals(szBeforeYdStkPos)){
							intStlCnt++;
						} else {
							
							recInRsSet = JDTORecordFactory.getInstance().create();
							recInRsSet.setField("YD_STK_POS", szBeforeYdStkPos);
							recInRsSet.setField("YD_STK_CNT", ""+intStlCnt);
							rsBedCnt.addRecord(recInRsSet);
							
							szBeforeYdStkPos = szArrYdStkPos[Loop_i];
							intStlCnt = 1;
							
							
						}
							
					} //end for
					
					recInRsSet = JDTORecordFactory.getInstance().create();
					recInRsSet.setField("YD_STK_POS", szBeforeYdStkPos);
					recInRsSet.setField("YD_STK_CNT", ""+intStlCnt);
					rsBedCnt.addRecord(recInRsSet);
					
					if(rsBedCnt.size() > 0) {
						szSortStkPos = new String[rsBedCnt.size()];
						szSortStlCnt = new String[rsBedCnt.size()];
						
						intArrSize = rsBedCnt.size();
						
						for(int Loop_i = 1; Loop_i <= rsBedCnt.size(); Loop_i++){
							rsBedCnt.absolute(Loop_i);
							recInRsSet = rsBedCnt.getRecord();
							
							szSortStkPos[Loop_i - 1] = ydDaoUtils.paraRecChkNull(recInRsSet, "YD_STK_POS");
							szSortStlCnt[Loop_i - 1] = ydDaoUtils.paraRecChkNull(recInRsSet, "YD_STK_CNT");
							
						} // end for
					
						
						for(int x = 0; x<intArrSize-1; x++ ) {
							for(int y = 0; y<intArrSize-x-1; y++) {
								if(Integer.parseInt(szSortStlCnt[y]) > Integer.parseInt(szSortStlCnt[y+1])) {
									szTmpStkPos = szSortStkPos[y];
									szTmpStlCnt = szSortStlCnt[y];
									szSortStkPos[y] = szSortStkPos[y+1];
									szSortStlCnt[y] = szSortStlCnt[y+1];   
									szSortStkPos[y+1] = szTmpStkPos;
									szSortStlCnt[y+1] = szTmpStlCnt;
								}
							}
						}
						
						szLogMsg = "[" + szOperationName + "] - sortUpColSeq]--BED_CNT[" + intArrSize + "]개 입니다.";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						
						for(int x = 0; x<intArrSize; x++ ) {
							szLogMsg = "[" + szOperationName + "] - sortUpColSeq]--정렬자료 POS[" + szSortStkPos[x] + "]와 재료수[" + szSortStlCnt[x] + "] 입니다.";
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						}
						
						szLogMsg = "[" + szOperationName + "] - sortUpColSeq]--정렬된 BED의 작업능력을 체크하여 가능여부를 판단";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						for(int x = 0; x<intArrSize; x++ ) {
							blnBaseBed = this.CheckBedSpec(szSortStkPos[intArrSize-1], rsWrkBookMtl);
							if(blnBaseBed == false){
								this.RotateSortStkPosCnt(szSortStkPos, szSortStlCnt);
							} else {
								break;
							}
						}
						
						ydUtils.putLog(szSessionName, szMethodName, "intArrSize" + intArrSize, YdConstant.DEBUG);
						for(int x = 0; x<intArrSize; x++ ) {
							
							for( int Loop_i = 1; Loop_i <= rsWrkBookMtl.size(); Loop_i++) {
								rsWrkBookMtl.absolute(Loop_i);
								recInRsSet = JDTORecordFactory.getInstance().create();
								recInRsSet.setRecord(rsWrkBookMtl.getRecord());
								
								
								if (szSortStkPos[x].equals(ydDaoUtils.paraRecChkNull(recInRsSet, "YD_STK_COL_GP") + ydDaoUtils.paraRecChkNull(recInRsSet, "YD_STK_BED_NO"))){
									rsSort.addRecord(recInRsSet);
								}
								
							} //end of for
						}
						
						if (rsSort.size() != rsWrkBookMtl.size()) {
							szLogMsg = "[" + szOperationName + "] - sortUpColSeq]--작업예약재료[" + rsWrkBookMtl.size() + "]와 정렬재료[" + rsSort.size() + "]의 수가 맞지 않습니다.";
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
							szRtnVal = YdConstant.RETN_CD_FAILURE;
							return szRtnVal;
						}
						
						szLogMsg = "[" + szOperationName + "] - sortUpColSeq]--------------------- 처리 종료 --------------------------";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					} else {
						szLogMsg = "[" + szOperationName + "] - sortUpColSeq]----대상BED가 1개입니다.";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						szRtnVal = YdConstant.RETN_CD_FAILURE;
						return szRtnVal;
					}
				}
				
				
				
				return szRtnVal;
			}
		} catch (Exception e) {
			szLogMsg = szOperationName + " ++++++++++++ 권상모음순서 정리 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			szRtnVal = YdConstant.RETN_CD_FAILURE;
			return szRtnVal;
		}
		
	} //end of sortUpColSeq

	/**
	 * BED 능력체크.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param szSortStkPos, szSortStlCnt
	 * @return 
	 * @return 
	 * @return
	 * @throws DAOException
	 */
	
	public boolean CheckBedSpec(String  szSortStkPos, JDTORecordSet rsWrkBookMtl) throws DAOException {
		String szMsg        		= "";		
		String szMethodName 		= "CheckBedSpec";
		String szOperationName 		= "BED 능력체크";
		
		String szYD_STK_COL_GP    = "";
		String szYD_STK_BED_NO    = "";
		String szYD_STK_LYR_NO    = "";
		int    intYD_STK_LYR_NO   = 1000;
		
		
		JDTORecord     recWrkBookMtl = null;
		JDTORecordSet  rsTemp        = null;
		JDTORecord     recTemp       = null;
			
		
		boolean blnRtn = true;
		
		try {
			
			szYD_STK_COL_GP				= szSortStkPos.substring(0, 6);
			szYD_STK_BED_NO				= szSortStkPos.substring(6, 8);
			
			rsWrkBookMtl.first();
			for(int i=0; i<rsWrkBookMtl.size(); i++) {
				recWrkBookMtl = rsWrkBookMtl.getRecord(i);
				if(szYD_STK_COL_GP.equals(ydDaoUtils.paraRecChkNull(recWrkBookMtl,"YD_STK_COL_GP")) && szYD_STK_BED_NO.equals(ydDaoUtils.paraRecChkNull(recWrkBookMtl,"YD_STK_BED_NO"))) {
					if(Integer.parseInt(ydDaoUtils.paraRecChkNull(recWrkBookMtl,"YD_STK_LYR_NO")) < intYD_STK_LYR_NO ) {
						szYD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(recWrkBookMtl,"YD_STK_LYR_NO");
						intYD_STK_LYR_NO = Integer.parseInt(ydDaoUtils.paraRecChkNull(recWrkBookMtl,"YD_STK_LYR_NO"));
					}
				}
			}
					
			
			szMsg="[" + szOperationName + "] 권상모음[B], 모음작업완료[S] 시 BASE위치[적치열:"+szYD_STK_COL_GP+", 베드:"+szYD_STK_BED_NO+", 단:"+szYD_STK_LYR_NO+"]부터 아래단으로 적치중인 재료들 정보 조회 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			rsTemp			= JDTORecordFactory.getInstance().createRecordSet("");
			recTemp 		= JDTORecordFactory.getInstance().create();
			recTemp.setField("YD_STK_COL_GP", 		szYD_STK_COL_GP);
			recTemp.setField("YD_STK_BED_NO", 		szYD_STK_BED_NO);
			recTemp.setField("YD_STK_LYR_NO", 		szYD_STK_LYR_NO);
			
			String szRtnMsg				= DaoManager.getYdStklyr(recTemp, rsTemp, 93);
			
			int intStkLyrMax			= 0;
			long lngStkLyrWtMax			= 0;
			double dblStkLyrHMax		= 0;
			
			if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				
				rsTemp.first();
				
				recTemp				= rsTemp.getRecord();
				
				//적치단에적치중인 총매수 중량 높이
	    		intStkLyrMax     = ydDaoUtils.paraRecChkNullInt (recTemp,"SH_CNT");
	   			lngStkLyrWtMax   = ydDaoUtils.paraRecChkNullLong(recTemp,"SUM_MTL_WT");
	   			dblStkLyrHMax    = ydDaoUtils.paraRecChkNullDouble(recTemp,"SUM_MTL_T");
	   			
	   			szMsg="[" + szOperationName + "] 권상모음[B], 모음작업완료[S] 시 BASE위치[적치열:"+szYD_STK_COL_GP+", 베드:"+szYD_STK_BED_NO+", 단:"+szYD_STK_LYR_NO+"]부터 아래단으로 적치중인 재료의 총매수["+intStkLyrMax+"], 총중량["+lngStkLyrWtMax+"], 총높이["+dblStkLyrHMax+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
			}else if( szRtnMsg.equals(YdConstant.RETN_CD_NOTEXIST) ) {
				szMsg="[" + szOperationName + "] 권상모음[B], 모음작업완료[S] 시 BASE위치[적치열:"+szYD_STK_COL_GP+", 베드:"+szYD_STK_BED_NO+", 단:"+szYD_STK_LYR_NO+"]부터 아래단으로 적치중인 재료가 존재하지 않으므로 총매수["+intStkLyrMax+"], 총중량["+lngStkLyrWtMax+"], 총높이["+dblStkLyrHMax+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}else{
				szMsg="[" + szOperationName + "] 권상모음[B], 모음작업완료[S] 시 BASE위치[적치열:"+szYD_STK_COL_GP+", 베드:"+szYD_STK_BED_NO+", 단:"+szYD_STK_LYR_NO+"]부터 아래단으로 적치중인 재료들 정보 조회 시 오류발생 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}
			
			//---------------------------------------------------------------------------------------------------------
			//	현재의 권상모음순서보다 큰 재료들의 매수, 중량합, 두께합을 조회
			//---------------------------------------------------------------------------------------------------------
			
			szMsg="[" + szOperationName + "] 권상모음[B], 모음작업완료[S] 시 해당 작업예약재료들의 정보 조회 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							
			
			int intMTL_SH			= 0;
			long lngSUM_MTL_WT		= 0;
			double dblSUM_MTL_T		= 0;
			
			rsWrkBookMtl.first();
			for(int i=0; i<rsWrkBookMtl.size(); i++) {
				recWrkBookMtl = rsWrkBookMtl.getRecord(i);
				intMTL_SH = intMTL_SH + 1;
				lngSUM_MTL_WT = lngSUM_MTL_WT + ydDaoUtils.paraRecChkNullLong(recTemp,"YD_MTL_WT");
				dblSUM_MTL_T = dblSUM_MTL_T + ydDaoUtils.paraRecChkNullDouble(recTemp,"YD_MTL_T");
			}
				
	   		szMsg="[" + szOperationName + "] 권상모음[B], 모음작업완료[S] 시 작업예약재료들의 정보 조회 완료 - 총매수["+intMTL_SH+"], 총중량["+lngSUM_MTL_WT+"], 총높이["+dblSUM_MTL_T+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//---------------------------------------------------------------------------------------------------------
			
			
			//---------------------------------------------------------------------------------------------------------
			//	베드정보 조회 - MAX단, MAX중량, 높이 조회
			//---------------------------------------------------------------------------------------------------------
			
			szMsg="[" + szOperationName + "] 권상모음[B], 모음작업완료[S] 시 베드[적치열:"+szYD_STK_COL_GP+", 베드:"+szYD_STK_BED_NO+"] 정보 조회 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			int intYD_STK_BED_LYR_MAX			= 0;
			long lngYD_STK_BED_WT_MAX			= 0;
			double dblYD_STK_BED_H_MAX			= 0;
			
			rsTemp			= JDTORecordFactory.getInstance().createRecordSet("");
			recTemp 		= JDTORecordFactory.getInstance().create();
			recTemp.setField("YD_STK_COL_GP", 		szYD_STK_COL_GP);
			recTemp.setField("YD_STK_BED_NO", 		szYD_STK_BED_NO);
			
			szRtnMsg				= DaoManager.getYdStkbed(recTemp, rsTemp, 0);
			
			if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				
				rsTemp.first();
				
				recTemp				= rsTemp.getRecord();
	
				intYD_STK_BED_LYR_MAX 			= ydDaoUtils.paraRecChkNullInt(recTemp,"YD_STK_BED_LYR_MAX");
				lngYD_STK_BED_WT_MAX   			= ydDaoUtils.paraRecChkNullLong(recTemp,"YD_STK_BED_WT_MAX");
				dblYD_STK_BED_H_MAX				= ydDaoUtils.paraRecChkNullDouble(recTemp,"YD_STK_BED_H_MAX");
				
				szMsg="[" + szOperationName + "] 권상모음[B], 모음작업완료[S] 시 베드[적치열:"+szYD_STK_COL_GP+", 베드:"+szYD_STK_BED_NO+"] 정보 조회 완료 - 총매수["+intYD_STK_BED_LYR_MAX+"], 총중량["+lngYD_STK_BED_WT_MAX+"], 총높이["+dblYD_STK_BED_H_MAX+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}else{
				szMsg="[" + szOperationName + "] 권상모음[B], 모음작업완료[S] 시 베드[적치열:"+szYD_STK_COL_GP+", 베드:"+szYD_STK_BED_NO+"] 정보 조회 시 오류발생 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}
			
			boolean isShOver			= false;
			boolean isWtOver			= false;
			boolean isHOver				= false;
			
			//매수 비교
			if( intYD_STK_BED_LYR_MAX < intMTL_SH + intStkLyrMax ) {
				isShOver				= true;
				
				szMsg="[" + szOperationName + "] 권상모음[B], 모음작업완료[S] 시 BASE위치[적치열:"+szYD_STK_COL_GP+", 베드:"+szYD_STK_BED_NO+", 단:"+szYD_STK_LYR_NO+"]가 매수초과이므로 BASE이적 처리";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			//중량 비교
			if( lngYD_STK_BED_WT_MAX < lngSUM_MTL_WT + lngStkLyrWtMax ) {
				isWtOver				= true;
				
				szMsg="[" + szOperationName + "] 권상모음[B], 모음작업완료[S] 시 BASE위치[적치열:"+szYD_STK_COL_GP+", 베드:"+szYD_STK_BED_NO+", 단:"+szYD_STK_LYR_NO+"]가중량초과이므로 BASE이적 처리";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			//높이 비교
			if( dblYD_STK_BED_H_MAX < dblSUM_MTL_T + dblStkLyrHMax ) {
				isHOver					= true;
				szMsg="[" + szOperationName + "] 권상모음[B], 모음작업완료[S] 시 BASE위치[적치열:"+szYD_STK_COL_GP+", 베드:"+szYD_STK_BED_NO+", 단:"+szYD_STK_LYR_NO+"]가 높이초과이므로 BASE이적 처리";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
			if( isShOver || isWtOver || isHOver ) {
				blnRtn = false;
			}
			
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return blnRtn;
	} // end of CheckBedSpec

	/**
	 * Sorting된 위치를 회전변경한다.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param szSortStkPos, szSortStlCnt
	 * @return 
	 * @return 
	 * @return
	 * @throws DAOException
	 */
	
	public String RotateSortStkPosCnt(String [] szSortStkPos, String [] szSortStlCnt) throws DAOException {
		
		String szMsg        		= "";		
		String szMethodName 		= "RotateSortStkPosCnt";
		String szOperationName 		= "Sorting된 위치를 회전변경";
		String szRtnMsg 			= YdConstant.RETN_CD_SUCCESS;
		
		String tmpStkPos = "";
		String tmpStlCnt = "";
		
		try {
			
			szMsg = "[" + szOperationName + "] - RotateSortStkPosCnt]--------------------- 처리 시작 --------------------------";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			for(int x = 0; x<szSortStkPos.length; x++ ) {
				szMsg = "[" + szOperationName + "] - RotateSortStkPosCnt]--정렬전자료 POS[" + szSortStkPos[x] + "]와 재료수[" + szSortStlCnt[x] + "] 입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			} 
			
			tmpStkPos = szSortStkPos[szSortStkPos.length-1];
			tmpStlCnt = szSortStlCnt[szSortStkPos.length-1];
			
			for(int i = szSortStkPos.length-1; i >= 0; i--){
				szSortStkPos[i+1] = szSortStkPos[i];
				szSortStlCnt[i+1] = szSortStlCnt[i];
			}
			
			szSortStkPos[0] = tmpStkPos;
			szSortStlCnt[0] = tmpStlCnt;
			
			szMsg = "[" + szOperationName + "] - RotateSortStkPosCnt]--------------------- 처리 종료 --------------------------";
			for(int x = 0; x<szSortStkPos.length; x++ ) {
				szMsg = "[" + szOperationName + "] - RotateSortStkPosCnt]--정렬후자료 POS[" + szSortStkPos[x] + "]와 재료수[" + szSortStlCnt[x] + "] 입니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szRtnMsg = YdConstant.RETN_CD_SUCCESS;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return szRtnMsg;
	} // end of RotateSortStkPosCnt
	
	
	/**
	 * 차량작업관리 상차LOT편성 취소 : 후판
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return 
	 * @return 
	 * @return
	 * @throws DAOException
	 */
	
	public String cancelCarLdLot(JDTORecord [] inDto) throws DAOException {
		/*
		 * 업무기준 : 1. 그리드에 선택된 작업예약재료를 삭제처리
		 * 			 2. 작업예약재료가 모두 삭제되었으면
		 * 				2-1. 작업예약도 같이 삭제처리
		 * 				2-2. 차량스케줄의 상차작업예약ID를 NULL로 수정
		 * 			  	2-3. 상차작업예약ID와 연관된 준비스케줄과 준비재료의 DEL_YN항목을 N으로 설정
		 * 				2-4. 예약된 차량정지POINT를 삭제처리
		 * 수정자 : 임춘수
		 * 수정일 : 2009.10.12
		 */
		//JDTO 변수정의
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = null;
		//DAO 변수정의
		YdCarSchDao ydCarSchDao		= new YdCarSchDao();
		//기본 변수 정의
		String szMsg        		= "";		
		String szMethodName 		= "cancelCarLdLot";
		String szOperationName 		= "차량작업관리 - 상차LOT편성취소";
		int       intRtnVal			= 0;
		String szRtnMsg 			= YdConstant.RETN_CD_SUCCESS;
		//로컬변수 정의
		String szSTL_NO 			= null;
		String szCRUD 				= null;
		String szYD_WBOOK_ID 		= null;
		String szYD_CAR_SCH_ID		= null;
		String szYD_PREP_SCH_ID		= null;
		String szYD_CAR_STOP_LOC	= null;
		String szUser 				= null;
		
		int intRUDCnt 				= 0;
		
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		//YdCarSpecDao ydCarSpecDao = new YdCarSpecDao();
		
		try {
			szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(inDto[0], "YD_CAR_SCH_ID");
			
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 - 차량스케줄확인[" + szYD_CAR_SCH_ID + "]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			/*
			 * 1. 그리드에 선택된 작업예약재료를 삭제처리
			 */
			//여러건의 로우를 처리 - 등록 및 수정
			for(int i = 0; i < inDto.length; i++ ) {
				szCRUD = ydDaoUtils.paraRecChkNull(inDto[i], "CRUD");
				szYD_WBOOK_ID = ydDaoUtils.paraRecChkNull(inDto[i], "YD_WBOOK_ID");
				szSTL_NO = ydDaoUtils.paraRecChkNull(inDto[i], "STL_NO");
				szUser = ydDaoUtils.paraRecChkNull(inDto[i], "YD_USER_ID");
				szMsg = "[JSP Session : "+szOperationName+"] ["+( i + 1 )+"]재료번호 - CRUD["+szCRUD+"], YD_WBOOK_ID["+szYD_WBOOK_ID+"], STL_NO["+szSTL_NO+"] 삭제 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				if( !szCRUD.equals("C") ) {
					recPara.setField("STL_NO", szSTL_NO);
					recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
					recPara.setField("DEL_YN", "Y");
					recPara.setField("MODIFIER", szUser);
					intRtnVal = ydWrkbookMtlDao.updYdWrkbookmtl(recPara, 0);
					if( intRtnVal < 0 ) {
						szMsg = "[JSP Session : "+szOperationName+"] 작업예약재료["+szSTL_NO+"] 삭제 시 오류발생 : 반환값 - " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						throw new DAOException(YdConstant.RETN_CD_FAILURE);
					}else if( intRtnVal == 0 ) {
						szMsg = "[JSP Session : "+szOperationName+"] 작업예약재료["+szSTL_NO+"] 삭제 시 해당 재료가 존재하지 않음 - " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}else{
						szMsg = "[JSP Session : "+szOperationName+"] 작업예약재료["+szSTL_NO+"] 삭제 성공 ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
					intRUDCnt++;
				}
			}
			if( intRUDCnt > 0 ) {
				/*
				 * 2. 작업예약재료가 모두 삭제되었으면
				 */
				szMsg = "[JSP Session : "+szOperationName+"] 작업예약[" + szYD_WBOOK_ID + "] 삭제 시작 ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
				recPara         = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
	//getYdWrkbookmtl1				
				intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recPara, outRecSet, 1);
				
				if( intRtnVal == 0 ) {
					/*
					 * 2-1. 작업예약도 같이 삭제처리
					 */
					recPara.setField("DEL_YN", "Y");
					recPara.setField("MODIFIER", szUser);
					intRtnVal = ydWrkbookDao.updYdWrkbook(recPara, 0);
					if( intRtnVal < 0  ) {
						szMsg = "[JSP Session : "+szOperationName+"] 작업예약["+szYD_WBOOK_ID+"] 삭제 시 오류발생 : 반환값 - " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						throw new DAOException(YdConstant.RETN_CD_FAILURE);
					}else if( intRtnVal == 0  ) {
						szMsg = "[JSP Session : "+szOperationName+"] 작업예약["+szYD_WBOOK_ID+"] 삭제 시 작업예약이 존재하지 않음 ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						throw new DAOException(YdConstant.RETN_CD_FAILURE);
					}
					szMsg = "[JSP Session : "+szOperationName+"] 작업예약["+szYD_WBOOK_ID+"] 삭제 성공 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					/*
					 * 2-2. 차량스케줄의 상차작업예약ID를 NULL로 수정
					 */
					recPara         = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
					recPara.setField("YD_CARLD_WRK_BOOK_ID", "");
					recPara.setField("MODIFIER", szUser);
					
					intRtnVal = ydCarSchDao.updYdCarsch(recPara, 0);
					
					if( intRtnVal < 0  ) {
						szMsg = "[JSP Session : "+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"]의 상차작업예약ID를 NULL로 수정 시 오류발생 : 반환값 - " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						throw new DAOException(YdConstant.RETN_CD_FAILURE);
					}else if( intRtnVal == 0  ) {
						szMsg = "[JSP Session : "+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"]의 상차작업예약ID를 NULL로 수정 시 존재하지 않음 ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						throw new DAOException(YdConstant.RETN_CD_FAILURE);
					}
					
					/*
					 * 2.3. 상차작업예약ID와 연관된 준비스케줄과 준비재료의 DEL_YN항목을 N으로 설정
					 */
					szMsg = "[JSP Session : "+szOperationName+"] 작업예약ID[" + szYD_WBOOK_ID + "]와 연관된 준비스케줄 원복 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					szRtnMsg = YdCommonUtils.restorePrepSch(szYD_WBOOK_ID, szMethodName);
					
					szMsg = "[JSP Session : "+szOperationName+"] 작업예약ID[" + szYD_WBOOK_ID + "]와 연관된 준비스케줄 원복 성공 : " + szRtnMsg;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
	//				outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
	//				recPara         = JDTORecordFactory.getInstance().create();
	//				recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
	//				
	//				YdPrepSchDao ydPrepSchDao = new YdPrepSchDao();
	//				
	//				intRtnVal = ydPrepSchDao.getYdPrepsch(recPara, outRecSet, 8);
	//				
	//				if( intRtnVal < 0  ) {
	//					szMsg = "[JSP Session : "+szOperationName+"] 작업예약ID[" + szYD_WBOOK_ID + "]와 연관된 준비스케줄 조회 시 오류발생 : 반환값 - " + intRtnVal;
	//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	//				}else if( intRtnVal == 0  ) {
	//					szMsg = "[JSP Session : "+szOperationName+"] 작업예약ID[" + szYD_WBOOK_ID + "]와 연관된 준비스케줄 조회 시 존재하지 않음 ";
	//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	//				}else{
	//					
	//					outRecSet.first();
	//					recPara = outRecSet.getRecord();
	//					
	//					szYD_PREP_SCH_ID = ydDaoUtils.paraRecChkNull(recPara, "YD_PREP_SCH_ID");
	//					
	//					szMsg = "[JSP Session : "+szOperationName+"] 작업예약ID[" + szYD_WBOOK_ID + "]와 연관된 준비스케줄[" + szYD_PREP_SCH_ID + "]이 존재하므로 삭제 시작";
	//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	//					
	//					YdPrepMtlDao ydPrepMtlDao = new YdPrepMtlDao();
	//					
	//					recPara         = JDTORecordFactory.getInstance().create();
	//					recPara.setField("YD_PREP_SCH_ID",   	szYD_PREP_SCH_ID);
	//					recPara.setField("DEL_YN",   			"N");
	//					recPara.setField("MODIFIER",   			szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
	//					//준비재료 삭제처리
	//					intRtnVal = ydPrepMtlDao.uptDelYdPrepmtlByPrepSchId(recPara);
	//					
	//					//준비스케줄 삭제처리
	//					recPara.setField("YD_WBOOK_ID",   		"");
	//					intRtnVal =  ydPrepSchDao.updDelYdPrepsch(recPara);
	//					
	//					szMsg = "[JSP Session : "+szOperationName+"] 작업예약ID[" + szYD_WBOOK_ID + "]와 연관된 준비스케줄[" + szYD_PREP_SCH_ID + "]과 준비재료 삭제 성공";
	//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	//				}
					
					/*
					 * 2-4. 예약된 차량정지POINT를 삭제처리 --> 상차LOT편성 시 삭제 됨
					 */
	//				szYD_CAR_STOP_LOC = ydDaoUtils.paraRecChkNull(inDto[0], "CAR_POINT3");
	//				szMsg = "[JSP Session : "+szOperationName+"] 예약된 차량정지POINT["+szYD_CAR_STOP_LOC+"]";
	//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	//				if( !szYD_CAR_STOP_LOC.equals("")) {
	//					szMsg = "[JSP Session : "+szOperationName+"] 예약된 차량정지POINT["+szYD_CAR_STOP_LOC+"]가 존재하므로 삭제처리 시작";
	//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	//					recPara         = JDTORecordFactory.getInstance().create();
	//					recPara.setField("YD_STK_COL_GP",   	szYD_CAR_STOP_LOC);
	//					recPara.setField("YD_CAR_USE_GP",   	"");					//차량사용구분
	//					recPara.setField("TRN_EQP_CD",   		"");					//운송장비코드
	//					YdStkColDao ydStkColDao = new YdStkColDao();
	//					intRtnVal = ydStkColDao.updYdStkcol(recPara, 0);
	//					
	//					if( intRtnVal < 0 ) {
	//						szMsg = "[JSP Session : "+szOperationName+"] 예약된 차량정지POINT["+szYD_CAR_STOP_LOC+"]가 존재하므로 삭제처리 시 오류발생[1] - 반환값 : " +intRtnVal;
	//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	//					}else if( intRtnVal == 0 ) {
	//						szMsg = "[JSP Session : "+szOperationName+"] 예약된 차량정지POINT["+szYD_CAR_STOP_LOC+"]가 존재하므로 삭제처리 시 적치열이 존재하지 않습니다. - 반환값 : " +intRtnVal;
	//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	//					}
	//					
	//					szMsg = "[JSP Session : "+szOperationName+"] 예약된 차량정지POINT["+szYD_CAR_STOP_LOC+"]가 존재하므로 삭제처리 완료";
	//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	//				}
					
				}else if( intRtnVal > 0 ) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 취소  - 작업예약재료가 존재하므로 작업예약["+szYD_WBOOK_ID+"] 삭제 불가";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}else{
					szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 취소  - 작업예약ID["+szYD_WBOOK_ID+"]로 작업예약재료 조회시 오류발생 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					throw new DAOException(YdConstant.RETN_CD_FAILURE);
				}
			}
			szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 취소 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szRtnMsg = YdConstant.RETN_CD_SUCCESS;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return szRtnMsg;
	}//end of cancelCarLdLot

	/**
	 * 차량작업관리 상차LOT편성 취소 : 코일사용
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return 
	 * @return 
	 * @return
	 * @throws DAOException
	 */
	
	public String cancelCarLdLotCoil(JDTORecord [] inDto) throws DAOException {
		/*
		 * 업무기준 : 1. 그리드에 선택된 작업예약재료를 삭제처리
		 * 			 2. 작업예약재료가 모두 삭제되었으면
		 * 				2-1. 작업예약도 같이 삭제처리
		 * 				2-2. 차량스케줄의 상차작업예약ID를 NULL로 수정
		 * 			  	2-3. 상차작업예약ID와 연관된 준비스케줄과 준비재료의 DEL_YN항목을 N으로 설정
		 * 				2-4. 예약된 차량정지POINT를 삭제처리
		 * 수정자 : 임춘수
		 * 수정일 : 2009.10.12
		 */
		//JDTO 변수정의
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = null;
		//DAO 변수정의
		YdCarSchDao ydCarSchDao		= new YdCarSchDao();
		//기본 변수 정의
		String szMsg        		= "";		
		String szMethodName 		= "cancelCarLdLotCoil";
		String szOperationName 		= "차량작업관리 - 상차LOT편성취소";
		int       intRtnVal			= 0;
		String szRtnMsg 			= YdConstant.RETN_CD_SUCCESS;
		//로컬변수 정의
		String szSTL_NO 			= null;
		String szCRUD 				= null;
		String szYD_WBOOK_ID 		= null;
		String szYD_CAR_SCH_ID		= null;
		String szYD_PREP_SCH_ID		= null;
		String szYD_CAR_STOP_LOC	= null;
		String szUser 				= null;
		
		int intRUDCnt 				= 0;
		
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		//YdCarSpecDao ydCarSpecDao = new YdCarSpecDao();
		
		try {
			szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(inDto[0], "YD_CAR_SCH_ID");
			
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 - 차량스케줄확인[" + szYD_CAR_SCH_ID + "]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			/*
			 * 1. 그리드에 선택된 작업예약재료를 삭제처리
			 */
			//여러건의 로우를 처리 - 등록 및 수정
			for(int i = 0; i < inDto.length; i++ ) {
				szCRUD 			= ydDaoUtils.paraRecChkNull(inDto[i], "CRUD");
				szYD_WBOOK_ID 	= ydDaoUtils.paraRecChkNull(inDto[i], "YD_WBOOK_ID");
				szSTL_NO 		= ydDaoUtils.paraRecChkNull(inDto[i], "STL_NO");
				szUser 			= ydDaoUtils.paraRecChkNull(inDto[i], "YD_USER_ID");
				szMsg 			= "[JSP Session : "+szOperationName+"] ["+( i + 1 )+"]재료번호 - CRUD["+szCRUD+"], YD_WBOOK_ID["+szYD_WBOOK_ID+"], STL_NO["+szSTL_NO+"] 삭제 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				if( !szCRUD.equals("C") ) {
					recPara         = JDTORecordFactory.getInstance().create();
					recPara.setField("STL_NO", szSTL_NO);
					recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
					recPara.setField("DEL_YN", "Y");
					recPara.setField("MODIFIER", szUser);
					intRtnVal = ydWrkbookMtlDao.updYdWrkbookmtl(recPara, 0);
					if( intRtnVal < 0 ) {
						szMsg = "[JSP Session : "+szOperationName+"] 작업예약재료["+szSTL_NO+"] 삭제 시 오류발생 : 반환값 - " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						throw new DAOException(YdConstant.RETN_CD_FAILURE);
					}else if( intRtnVal == 0 ) {
						szMsg = "[JSP Session : "+szOperationName+"] 작업예약재료["+szSTL_NO+"] 삭제 시 해당 재료가 존재하지 않음 - " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}else{
						szMsg = "[JSP Session : "+szOperationName+"] 작업예약재료["+szSTL_NO+"] 삭제 성공 ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
					/* 
					 * 2-1. 작업예약도 같이 삭제처리
					 */
					recPara         = JDTORecordFactory.getInstance().create();
					recPara.setField("DEL_YN", "Y");
					recPara.setField("MODIFIER", szUser);
					intRtnVal = ydWrkbookDao.updYdWrkbook(recPara, 0);
					if( intRtnVal < 0  ) {
						szMsg = "[JSP Session : "+szOperationName+"] 작업예약["+szYD_WBOOK_ID+"] 삭제 시 오류발생 : 반환값 - " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						throw new DAOException(YdConstant.RETN_CD_FAILURE);
					}else if( intRtnVal == 0  ) {
						szMsg = "[JSP Session : "+szOperationName+"] 작업예약["+szYD_WBOOK_ID+"] 삭제 시 작업예약이 존재하지 않음 ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						throw new DAOException(YdConstant.RETN_CD_FAILURE);
					}
					szMsg = "[JSP Session : "+szOperationName+"] 작업예약["+szYD_WBOOK_ID+"] 삭제 성공 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

					intRUDCnt++;
				}
			}
					
			/*
			 * 2-2. 차량스케줄의 상차작업예약ID를 NULL로 수정
			 */
			recPara         = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
			recPara.setField("YD_CARLD_WRK_BOOK_ID", "");
			recPara.setField("MODIFIER", szUser);
			
			intRtnVal = ydCarSchDao.updYdCarsch(recPara, 0);
			
			if( intRtnVal < 0  ) {
				szMsg = "[JSP Session : "+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"]의 상차작업예약ID를 NULL로 수정 시 오류발생 : 반환값 - " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new DAOException(YdConstant.RETN_CD_FAILURE);
			}else if( intRtnVal == 0  ) {
				szMsg = "[JSP Session : "+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"]의 상차작업예약ID를 NULL로 수정 시 존재하지 않음 ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new DAOException(YdConstant.RETN_CD_FAILURE);
			}
			
			/*
			 * 2.3. 상차작업예약ID와 연관된 준비스케줄과 준비재료의 DEL_YN항목을 N으로 설정
			 */
			szMsg = "[JSP Session : "+szOperationName+"] 작업예약ID[" + szYD_WBOOK_ID + "]와 연관된 준비스케줄 원복 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szRtnMsg = YdCommonUtils.restorePrepSch(szYD_WBOOK_ID, szMethodName);
			
			szMsg = "[JSP Session : "+szOperationName+"] 작업예약ID[" + szYD_WBOOK_ID + "]와 연관된 준비스케줄 원복 성공 : " + szRtnMsg;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szMsg = "[JSP Session] 차량작업관리 화면 상차LOT편성 취소 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szRtnMsg = YdConstant.RETN_CD_SUCCESS;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return szRtnMsg;
	}//end of cancelCarLdLot


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
	
	public String complCarLdLot(JDTORecord [] inDto) throws DAOException {
		int       intRtnVal    = 0;
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecord       recTemp         = null;
		JDTORecord       outRec         = JDTORecordFactory.getInstance().create();
		JDTORecord       recLdStart      = null;
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		
		String szMsg        = "";		
		String szMethodName = "complCarLdLot";
		String szSTL_NO = null;
		String szRtnMsg = YdConstant.RETN_CD_SUCCESS;
		String szYD_STK_LYR_NO = null;
		String szYD_WBOOK_ID = null;
		String szYD_CAR_SCH_ID = null;
		String szUser = null;
		String szCurrDate = YdUtils.getCurDate("yyyyMMddHHmmss");
		String szARR_WLOC_CD = "";
		String szYD_AIM_YD_GP = null;
		String szTRN_EQP_CD = null;
		String szWLOC_CD = null;
		String szTcCode = null;
		String szWkGp = null;
		String szQueueName = null;
		String szYD_MTL_WT = null;
		String szYD_CAR_PROG_STAT = null;
		String szSPOS_YD_PNT_CD = "";
		String szHCR_GP = null;
		String szYD_MTL_ITEM = null;
		String szOperationName = "상차완료처리";
		PropertyService propertyService = null;
		//int intRUDCnt = 0;
		
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		YdCarSchDao ydCarSchDao = new YdCarSchDao();
		YdCarFtmvMtlDao ydCarFtmvMtlDao = new YdCarFtmvMtlDao();
		
		
		try {
			//------------------------------------------------------------------------------------------------------
			// 전문확인
			//------------------------------------------------------------------------------------------------------
			szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			szYD_CAR_SCH_ID = yddatautil.setDataDefault(inDto[0].getField("YD_CAR_SCH_ID"), "");
			szYD_WBOOK_ID = ydDaoUtils.paraRecChkNull(inDto[0], "YD_WBOOK_ID");
			szUser = ydDaoUtils.paraRecChkNull(inDto[0], "YD_USER_ID");
			szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(inDto[0], "YD_AIM_YD_GP");
			szWLOC_CD = ydDaoUtils.paraRecChkNull(inDto[0], "WLOC_CD3");
			szTRN_EQP_CD = ydDaoUtils.paraRecChkNull(inDto[0], "TRN_CAR_NO");
			szYD_MTL_ITEM = ydDaoUtils.paraRecChkNull(inDto[0], "YD_MTL_ITEM");
			szHCR_GP = ydDaoUtils.paraRecChkNull(inDto[0], "HCR_GP");
			szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  - 차량스케줄ID["+szYD_CAR_SCH_ID+"], 작업예약ID["+szYD_WBOOK_ID+"], 야드목표야드구분["+szYD_AIM_YD_GP+"], 운송장비코드["+szTRN_EQP_CD+"], 발지개소코드["+szWLOC_CD+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//------------------------------------------------------------------------------------------------------
			
			
			//------------------------------------------------------------------------------------------------------
			// 차량스케줄 조회 후 차량진행상태 확인 시작
			//------------------------------------------------------------------------------------------------------
			szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리 : 차량스케줄ID["+szYD_CAR_SCH_ID+"]로 조회 전";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//1. 차량스케줄을 먼저 조회해서 존재하는 지를 확인
			recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);

//getYdCarsch1			
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, outRecSet, 0);
			if( intRtnVal == 0 ) {
				szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리 : 차량스케줄ID["+szYD_CAR_SCH_ID+"]이 존재하지 않습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NOTEXIST;
			}else if( intRtnVal < 0 ) {
				szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리 : 차량스케줄ID["+szYD_CAR_SCH_ID+"]로 조회시 오류발생 : 반환값  - " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}
			outRecSet.first();
			recPara = outRecSet.getRecord();
			
			szYD_CAR_PROG_STAT = yddatautil.setDataDefault(recPara.getField("YD_CAR_PROG_STAT"), "");
			szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리 : 차량스케줄ID["+szYD_CAR_SCH_ID+"]로 야드차량진행상태["+szYD_CAR_PROG_STAT+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			if( szYD_CAR_PROG_STAT.equals("5")) {
				szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리 : 차량스케줄ID["+szYD_CAR_SCH_ID+"]로 야드차량진행상태["+szYD_CAR_PROG_STAT+"]가 이미 상차완료이므로 상차완료처리를 할 수 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return YdConstant.RETN_CD_EQ_STATUS;
			}
			szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리 : 차량스케줄ID["+szYD_CAR_SCH_ID+"]로 야드차량진행상태["+szYD_CAR_PROG_STAT+"]가 상차완료가 아니므로 상차완료처리를 합니다.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//------------------------------------------------------------------------------------------------------
			
			
			//------------------------------------------------------------------------------------------------------
			// 차량스케줄과 작업예약을 연결시킨다
			//------------------------------------------------------------------------------------------------------
			//야드구분을 개소코드로 변환
			szARR_WLOC_CD = YdCommonUtils.getWlocCd(szYD_AIM_YD_GP);
			
			if( szWLOC_CD.equals(YdConstant.WLOC_CD_A_PLATE_PLANT)){
				
				if("".equals(ydDaoUtils.paraRecChkNull(inDto[0], "STL_NO"))){
					szARR_WLOC_CD = YdConstant.WLOC_CD_A_PLATE_SLAB_YARD;
				}else{
					szARR_WLOC_CD = YdCommonUtils.getWlocCd3(ydDaoUtils.paraRecChkNull(inDto[0], "STL_NO"));
				}
				
			}else if( szWLOC_CD.equals(YdConstant.WLOC_CD_B_PLATE_PLANT)) {
				
				if("".equals(ydDaoUtils.paraRecChkNull(inDto[0], "STL_NO"))){
					szARR_WLOC_CD = YdConstant.WLOC_CD_2_PLATE_SLAB_YARD;
				}else{
					szARR_WLOC_CD = YdCommonUtils.getWlocCd3(ydDaoUtils.paraRecChkNull(inDto[0], "STL_NO"));
				}
			}
			//1. 차량스케줄과 작업예약을 연결시킨다 - 상차작업예약ID, 상차개시일시, 상차완료일시, 착지개소코드, 차량진행상태를 상차완료로 설정한다.
			recPara         = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);				//차량스케줄ID
			recPara.setField("YD_CARLD_WRK_BOOK_ID", szYD_WBOOK_ID);		//상차작업예약ID
			recPara.setField("YD_EQP_WRK_STAT", "L");						//야드설비작업상태
			recPara.setField("YD_CARLD_ST_DT", szCurrDate);					//상차개시일시
			recPara.setField("YD_CARLD_CMPL_DT", szCurrDate);				//상차완료일시
			recPara.setField("ARR_WLOC_CD", szARR_WLOC_CD);					//착지개소코드
			recPara.setField("YD_CAR_PROG_STAT", "5");						//차량진행상태 : 상차완료[5]
			recPara.setField("MODIFIER", szUser);							//수정자
			intRtnVal = ydCarSchDao.updYdCarsch(recPara, 0);
			
			if( intRtnVal == 0 ) {
				szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  - 차량스케줄에 상차개시일시, 상차완료일시, 차량진행상태[상차완료-5]를 업데이트시 차량스케줄이 존재하지 않습니다. : 반환값 - " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new DAOException(YdConstant.RETN_CD_FAILURE);
			}else if( intRtnVal < 0 ) {
				szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  - 차량스케줄에 상차개시일시, 상차완료일시, 차량진행상태[상차완료-5]를 업데이트시 오류발생 : 반환값 - " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new DAOException(YdConstant.RETN_CD_FAILURE);
			}
			
			//------------------------------------------------------------------------------------------------------
			
			
			//------------------------------------------------------------------------------------------------------
			// 상차개시를 구내운송으로 전송
			//------------------------------------------------------------------------------------------------------
			//개소코드와 포인트코드 판단
			if( szWLOC_CD.equals(YdConstant.WLOC_CD_A_PLATE_PLANT)||
				szWLOC_CD.equals(YdConstant.WLOC_CD_B_PLATE_PLANT)){
				szSPOS_YD_PNT_CD = YdConstant.WLOC_CD_A_PLATE_PLANT_PNT_CD;
			}else if( szWLOC_CD.equals(YdConstant.WLOC_CD_C_HR_PLANT) ) {
				szSPOS_YD_PNT_CD = YdConstant.WLOC_CD_C_HR_PLANT_PNT_CD;
			}
			
			recLdStart         = JDTORecordFactory.getInstance().create();
			szTcCode = "YDTSJ007";
			//2. 상차개시를 구내운송으로 전송
			recLdStart.setField("JMS_TC_CD", 			szTcCode);
			recLdStart.setField("JMS_TC_CREATE_DDTT",   YdUtils.getCurDate("yyyyMMddHHmmss"));
			// 운송장비코드 [운송장비코드]
			recLdStart.setField("TRN_EQP_CD", szTRN_EQP_CD);	
			// 발지개소코드 [발지개소코드]
			recLdStart.setField("SPOS_WLOC_CD", szWLOC_CD);
			// 발지 야드포인트코드 [상차정지위치 = 적치열구분] - 아직 미 결정
			recLdStart.setField("SPOS_YD_PNT_CD", szSPOS_YD_PNT_CD);
			// 착지개소코드 [착지개소코드]
			recLdStart.setField("ARR_WLOC_CD", szARR_WLOC_CD);
			// 운송작업시작일시 [상차개시일시]
			recLdStart.setField("TRN_WRK_ST_DT", szCurrDate);
			
//			szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  시 - 상차개시전문을 구내운송으로 전송 시작";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			ydUtils.displayRecord(szOperationName, recLdStart);
			
//			YdDeleComm deleComm = new YdDeleComm();
//			szWkGp =szTcCode.substring(2, 4);
//			propertyService = PropertyService.getInstance();
//			szQueueName =propertyService.getProperty("common.properties", "jms.queue."+szWkGp+"_MDB_QUEUE");
//			deleComm.jmsQSnder(szQueueName, recLdStart);
//			
//			szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  시 - 상차개시전문을 구내운송으로 전송 완료";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//------------------------------------------------------------------------------------------------------
			
			
			//------------------------------------------------------------------------------------------------------
			// 상차완료를 구내운송으로 전송, 차량이송재료 등록, 작업예약재료/작업예약 삭제 처리
			//------------------------------------------------------------------------------------------------------
			szTcCode = "YDTSJ008";
			outRec.setField("JMS_TC_CD", 			szTcCode);
			outRec.setField("JMS_TC_CREATE_DDTT",   YdUtils.getCurDate("yyyyMMddHHmmss"));
			// 운송장비코드 [운송장비코드]
			outRec.setField("TRN_EQP_CD", szTRN_EQP_CD);	
			// 발지개소코드 [발지개소코드]
			outRec.setField("SPOS_WLOC_CD", szWLOC_CD);
			// 발지 야드포인트코드 [상차정지위치 = 적치열구분] - 아직 미 결정
			outRec.setField("SPOS_YD_PNT_CD", szSPOS_YD_PNT_CD);
			// 착지개소코드 [착지개소코드]
			outRec.setField("ARR_WLOC_CD", szARR_WLOC_CD);
			//3. 작업예약에 등록된 재료를 차량이송재료로 등록 후 작업예약재료를 삭제처리한다.
			recTemp         = JDTORecordFactory.getInstance().create();
			recTemp.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			recTemp.setField("DEL_YN", "Y");
			recTemp.setField("MODIFIER", szUser);
			
			recPara         = JDTORecordFactory.getInstance().create();
			for(int i = inDto.length - 1; i >= 0; i-- ) {
				
				//------------------------------------------------------------------------------------------------------
				// 차량이송재료 등록
				//------------------------------------------------------------------------------------------------------
				szYD_CAR_SCH_ID = yddatautil.setDataDefault(inDto[i].getField("YD_CAR_SCH_ID"), "");
				szSTL_NO = ydDaoUtils.paraRecChkNull(inDto[i], "STL_NO");
				szYD_MTL_WT = ydDaoUtils.paraRecChkNull(inDto[i], "YD_MTL_WT");
				szUser = ydDaoUtils.paraRecChkNull(inDto[i], "YD_USER_ID");
				szYD_STK_LYR_NO = "00" +  (i + 1);
				szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리 - 차량이송재료 등록 : ["+( i+ 1)+"]재료번호 - 차량스케줄ID["+szYD_CAR_SCH_ID+"], STL_NO["+szSTL_NO+"] ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
				recPara.setField("STL_NO", szSTL_NO);
				recPara.setField("REGISTER", szUser);
				recPara.setField("YD_STK_BED_NO", "01");
				recPara.setField("YD_STK_LYR_NO", szYD_STK_LYR_NO.substring(szYD_STK_LYR_NO.length() - 3));
				
				intRtnVal = ydCarFtmvMtlDao.insYdCarftmvmtl(recPara);
				
				if( intRtnVal == 0 ) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  - 차량이송재료["+szSTL_NO+"] 등록 시 오류발생 1 : 반환값 - " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					throw new DAOException(YdConstant.RETN_CD_FAILURE);
				}else if( intRtnVal < 0 ) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리 - 차량이송재료["+szSTL_NO+"] 등록 시 오류발생 2 : 반환값 - " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					throw new DAOException(YdConstant.RETN_CD_FAILURE);
				}
				
				szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  - 차량이송재료["+szSTL_NO+"] 등록 성공 ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//------------------------------------------------------------------------------------------------------
				
				
				//------------------------------------------------------------------------------------------------------
				// 작업예약재료 삭제 처리
				//------------------------------------------------------------------------------------------------------
				outRec.setField("STL_NO" + (i + 1), szSTL_NO);
				outRec.setField("STL_WT" + (i + 1), szYD_MTL_WT);
				
				//작업예약재료 삭제처리
				
				recTemp.setField("STL_NO", szSTL_NO);
				intRtnVal = ydWrkbookMtlDao.updYdWrkbookmtl(recTemp, 0);
				if( intRtnVal < 0 ) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  - 작업예약재료 삭제 시 오류발생 : 반환값 - " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					//throw new DAOException(YdConstant.RETN_CD_FAILURE);
				}else if( intRtnVal == 0 ) {
					szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  - 작업예약재료 삭제 시 해당 재료가 존재하지 않음 - " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}else{
					szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  - 작업예약재료 삭제 성공 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
				//------------------------------------------------------------------------------------------------------
			}
			
			//------------------------------------------------------------------------------------------------------
			// 작업예약 삭제 처리
			//------------------------------------------------------------------------------------------------------
			
			szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  - 작업예약 삭제 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			recTemp         = JDTORecordFactory.getInstance().create();
			recTemp.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			recTemp.setField("DEL_YN", "Y");
			intRtnVal = ydWrkbookDao.updYdWrkbook(recTemp, 0);
			if( intRtnVal < 0  ) {
				szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  - 작업예약["+szYD_WBOOK_ID+"] 삭제 시 오류발생 : 반환값 - " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				//throw new DAOException(YdConstant.RETN_CD_FAILURE);
			}else if( intRtnVal == 0  ) {
				szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  - 작업예약["+szYD_WBOOK_ID+"] 삭제 시 작업예약이 존재하지 않음 ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				//throw new DAOException(YdConstant.RETN_CD_FAILURE);
			}
			szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  - 작업예약["+szYD_WBOOK_ID+"] 삭제 성공 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//------------------------------------------------------------------------------------------------------
			
			
			//------------------------------------------------------------------------------------------------------
			// 공통테이블 업데이트 처리
			//------------------------------------------------------------------------------------------------------
			szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  - 공통테이블 업데이트 처리 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			if( szWLOC_CD.equals(YdConstant.WLOC_CD_A_PLATE_PLANT)||
				szWLOC_CD.equals(YdConstant.WLOC_CD_B_PLATE_PLANT)	)	{
				szRtnMsg = YdCommonUtils.procCarLoadCmplForPlateGds(szYD_CAR_SCH_ID, YdConstant.YD_STR_LOC_CURR, szMethodName);
				if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
					szMsg="[JSP Session] 차량작업관리 화면 상차완료시 공통테이블 업데이트 처리 성공 - 현위치 개소코드["+szWLOC_CD+"]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}else{
					szMsg="[JSP Session] 차량작업관리 화면 상차완료시 공통테이블 업데이트 처리 실패 - 현위치 개소코드["+szWLOC_CD+"]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
			}else{
				intRtnVal = YdCommonUtils.procCarLoadCmpl(szYD_CAR_SCH_ID);
				if( intRtnVal <= 0 ) {
					//업무간 데이타가 공유되지 않아서 현재는 예외를 던지지 않음
					szMsg="[JSP Session] 차량작업관리 화면 상차완료시 공통테이블 업데이트 처리 실패";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}else{
					szMsg="[JSP Session] 차량작업관리 화면 상차완료시 공통테이블 업데이트 처리 성공";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
			}
			
			
			szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  - 공통테이블 업데이트 처리 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//------------------------------------------------------------------------------------------------------
			
			
			//------------------------------------------------------------------------------------------------------
			//4. 상차완료를 구내운송으로 전송
			//------------------------------------------------------------------------------------------------------
			outRec.setField("TRN_WRK_MTL_GP", (szYD_MTL_ITEM.equals("") ? "" : szYD_MTL_ITEM.substring(0, 1)));
			outRec.setField("MTL_UGNT_GP", "");
			outRec.setField("HCR_GP", szHCR_GP);
			outRec.setField("CARLD_SH", "" + inDto.length);
			outRec.setField("CARLD_CMPL_DT", szCurrDate);
			
			//ydUtils.displayRecord(szOperationName, outRec);
			//deleComm.jmsQSnder(szQueueName, outRec);
			//YdCommonUtils.sndLdStartNComplTc(recLdStart, outRec);
			
			YdDelegate ydDelegate = new YdDelegate();
			
			//------------------------------------------------------------------------------------------------------
			// 이송상차완료실적[YDYDJ770] 2후판정정야드 송신 
			//------------------------------------------------------------------------------------------------------	
			if(YdConstant.WLOC_CD_B_PLATE_PLANT.equals(szWLOC_CD)) {
				//2후판SIZING 개소코드(DWY23) 일 경우만 전문 송신
				int cnt = 0;
				
				szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  시 - 이송상차완료실적[YDYDJ770]을 정정야드로 전송 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				JDTORecord recLdCmpl1  = JDTORecordFactory.getInstance().create();
				szTcCode  = "YDYDJ770";
				recLdCmpl1.setField("JMS_TC_CD", 				szTcCode);
				recLdCmpl1.setField("YD_GP", 					"F"); //F:2후판 정정야드	
				
				for(int i = 0; i < inDto.length ;  i++ ) {
					szSTL_NO = ydDaoUtils.paraRecChkNull(inDto[i], "STL_NO");
					if(!"".equals(szSTL_NO)) {
						cnt++;
						if(cnt>10) {
							break;
						}						
					}					
					recLdCmpl1.setField("STL_NO" + (i + 1), szSTL_NO);
				}
				recLdCmpl1.setField("PL_WR_GDS_TOT_SH", Integer.toString(cnt));				
				
				ydDelegate.sendMsg(recLdCmpl1);
				
				szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  시 - 이송상차완료실적[YDYDJ770]을 정정야드로 전송 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				
			}
			//------------------------------------------------------------------------------------------------------
			
			//------------------------------------------------------------------------------------------------------
			// 상차개시전문 송신
			//------------------------------------------------------------------------------------------------------
			
			//열연재열재 개소코드 
			if(!YdConstant.WLOC_CD_C_HR_PLANT.equals(szWLOC_CD)) { 
				szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  시 - 상차개시전문을 구내운송으로 전송 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				recLdStart         = JDTORecordFactory.getInstance().create();
				szTcCode = "YDTSJ007";
				//2. 상차개시를 구내운송으로 전송
				recLdStart.setField("JMS_TC_CD", 			szTcCode);
				recLdStart.setField("YD_CAR_SCH_ID", 		szYD_CAR_SCH_ID);
				
				ydDelegate.sendMsg(recLdStart);
				
				szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  시 - 상차개시전문을 구내운송으로 전송 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			}
			//------------------------------------------------------------------------------------------------------
			
			
			//------------------------------------------------------------------------------------------------------
			// 상차완료전문 송신
			//------------------------------------------------------------------------------------------------------
			
			szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  시 - 상차완료전문을 구내운송으로 전송 시작(전문버퍼사용)";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			JDTORecord recLdCmpl         = JDTORecordFactory.getInstance().create();
			szTcCode = "YDTSJ008";
			recLdCmpl.setField("JMS_TC_CD", 				YdConstant.YDYDJ701);
			recLdCmpl.setField(YdConstant.BUFFER_TC_CD, 	szTcCode);
			recLdCmpl.setField("YD_CAR_SCH_ID", 			szYD_CAR_SCH_ID);
			
			//recPara         = JDTORecordFactory.getInstance().create();
			//recPara.setField("JMS_TC_CD", 					"YDYDJ701");
			//recPara.setField(YdConstant.TC_BODY, 			recLdCmpl);
			
			ydDelegate.sendMsg(recLdCmpl);
			
			szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리  시 - 상차완료전문을 구내운송으로 전송 완료(전문버퍼사용)";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//------------------------------------------------------------------------------------------------------
			
			szMsg = "[JSP Session] 차량작업관리 화면 상차완료처리 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return YdConstant.RETN_CD_SUCCESS;
	}//end of complCarLdLot
	
	/**
	 * 차량작업관리화면 하차완료처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return 
	 * @return 
	 * @return
	 * @throws DAOException
	 */
	
	public String complCarUd(JDTORecord [] inDto) throws DAOException {
		/*
		 * DESC : 후판SIZING개소와 열연재열재개소코드에서 하차완료를 백업처리 시 사용되는 모듈
		 * 업무기준 : 1. 차량스케줄ID로 차량스케줄이 존재하는 지 조회
		 * 				1-1. 존재하지 않으면 오류메세지 반환
		 * 				1-2. 존재하면 차량스케줄의 야드차량진행상태가 하차완료가능상태[하차도착(B), 하차검수(C)]인 지 체크
		 * 					1-2-1. 하차완료가능하지 않으면 오류메세지 반환
		 * 					1-2-2. 하차완료가능하면
								1-2-2-1. 차량스케줄을 하차완료로 설정을 하고 삭제처리는 하지 않는다.
		 * 						1-2-2-2. 이송지시테이블과 공통테이블의 재료진도 수정
		 * 						1-2-2-3. 차량이송재료 삭제처리
		 * 						1-2-2-4. 구내운송으로 하차개시와 하차완료 전문 송신
		 * 						1-2-2-5. 후판SIZING개소인 경우 후판조업으로 YDPRJ001 후판제품반납하차실적 전송 
		 * 
		 * 
		 * 파라미터 : 그리드의 선택된 레코드들[배열]
		 * 		YD_CAR_SCH_ID		: 차량스케줄ID
		 * 		TRN_EQP_CD			: 운송장비코드
		 * 		YD_WRK_BOOK_ID		: 작업예약ID
		 * 		ARR_WLOC_CD			: 착지개소코드
		 * 		YD_CAR_STOP_LOC		: 차량정지위치
		 * 		YD_USER_ID			: 사용자ID
		 * 
		 * 호출모듈 : 1. 차량작업관리화면메뉴 내 배차내역 탭의 하차완료 버튼
		 * 
		 * 수정자 : 임춘수
		 * 수정일시 : 2009.09.25
		 */
		//DAO 정의
		
		//JDTO 정의
		
		//기본변수 정의
		
		//로컬변수 정의
		
		int       intRtnVal    = 0;
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecord       recTemp         = null;
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		String szMsg        = "";		
		String szMethodName = "complCarUd";
		String szOperationName = "차량작업관리화면 하차완료처리";
		String szRtnMsg = YdConstant.RETN_CD_SUCCESS;
		String szYD_WBOOK_ID = null;
		String szYD_CAR_SCH_ID = null;
		String szUser = null;
		String szCurrDate = YdUtils.getCurDate("yyyyMMddHHmmss");
		String szARR_WLOC_CD = "";
		String szTRN_EQP_CD = null;
		String szWLOC_CD = null;
		String szTcCode = null;
		String szWkGp = null;
		String szQueueName = null;
		String szYD_CAR_PROG_STAT = null;
		String szARR_YD_PNT_CD = "";
		String szYD_CAR_STOP_LOC = null;
		String szYD_GP			= "";
		
		PropertyService propertyService = null;
		//int intRUDCnt = 0;
		
		YdCarSchDao ydCarSchDao = new YdCarSchDao();
		YdCarFtmvMtlDao ydCarFtmvMtlDao = new YdCarFtmvMtlDao();
		YdStkColDao ydStkColDao = new YdStkColDao();
		
		try {
			//------------------------------------------------------------------------------------------------------
			//	파라미터 확인
			//------------------------------------------------------------------------------------------------------
			szMsg = "[JSP Session] " + szOperationName + " 메소드시작 - 파라미터 확인";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			ydUtils.displayRecord(szOperationName, inDto[0]);
			
			szYD_CAR_SCH_ID 	= yddatautil.setDataDefault(inDto[0].getField("YD_CAR_SCH_ID"), "");
			szYD_WBOOK_ID 		= ydDaoUtils.paraRecChkNull(inDto[0], "YD_WRK_BOOK_ID");
			szUser 				= ydDaoUtils.paraRecChkNull(inDto[0], "YD_USER_ID");
			szARR_WLOC_CD 		= ydDaoUtils.paraRecChkNull(inDto[0], "ARR_WLOC_CD");
			szTRN_EQP_CD 		= ydDaoUtils.paraRecChkNull(inDto[0], "TRN_EQP_CD");
			szYD_CAR_STOP_LOC 	= ydDaoUtils.paraRecChkNull(inDto[0], "YD_CAR_STOP_LOC");
			if( !szYD_CAR_STOP_LOC.equals("") ) {
				szYD_GP = szYD_CAR_STOP_LOC.substring(0, 1);
			}
			
			szMsg = "[JSP Session] " + szOperationName + " - 차량스케줄ID["+szYD_CAR_SCH_ID+"], 작업예약ID["+szYD_WBOOK_ID+"], 운송장비코드["+szTRN_EQP_CD+"], 착지개소코드["+szARR_WLOC_CD+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//------------------------------------------------------------------------------------------------------
			
			
			
			//------------------------------------------------------------------------------------------------------
			//	차량스케줄 조회 후 차량진행상태 확인 시작 - 다른유저에 의해서 상태가 변경될 수 있으므로 먼저 상태를 확인 필요
			//	차량스케줄 조회
			//------------------------------------------------------------------------------------------------------
			szMsg = "[JSP Session] " + szOperationName + " : 차량스케줄ID["+szYD_CAR_SCH_ID+"]로 조회 전";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//1. 차량스케줄을 먼저 조회해서 존재하는 지를 확인
			recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
//getYdCarsch0			
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, outRecSet, 0);
			if( intRtnVal == 0 ) {
				szMsg = "[JSP Session] " + szOperationName + " : 차량스케줄ID["+szYD_CAR_SCH_ID+"]이 존재하지 않습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NOTEXIST;
			}else if( intRtnVal < 0 ) {
				szMsg = "[JSP Session] " + szOperationName + " : 차량스케줄ID["+szYD_CAR_SCH_ID+"]로 조회시 오류발생 : 반환값  - " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}
			outRecSet.first();
			recPara = outRecSet.getRecord();
			
			//------------------------------------------------------------------------------------------------------
			
			
			//------------------------------------------------------------------------------------------------------
			//	차량진행상태 확인
			//------------------------------------------------------------------------------------------------------
			szYD_CAR_PROG_STAT = yddatautil.setDataDefault(recPara.getField("YD_CAR_PROG_STAT"), "");
			szMsg = "[JSP Session] " + szOperationName + " : 차량스케줄ID["+szYD_CAR_SCH_ID+"]로 야드차량진행상태["+szYD_CAR_PROG_STAT+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			if( !szYD_CAR_PROG_STAT.equals("B") && !szYD_CAR_PROG_STAT.equals("C")) {
				szMsg = "[JSP Session] " + szOperationName + " : 차량스케줄["+szYD_CAR_SCH_ID+"]의 야드차량진행상태["+szYD_CAR_PROG_STAT+"]가 하차완료[하차완료가능상태 : 하차도착(B), 하차검수(C)]할 수 있는 상태가 아닙니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				return YdConstant.RETN_CD_NOTEQ_STATUS;
			}
			szMsg = "[JSP Session] " + szOperationName + " : 차량스케줄["+szYD_CAR_SCH_ID+"]의 야드차량진행상태["+szYD_CAR_PROG_STAT+"]가 하차완료처리가능한 상태입니다.";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//------------------------------------------------------------------------------------------------------
			
			
			//------------------------------------------------------------------------------------------------------
			// 차량스케줄의 차량진행상태를 하차완료로 변경 - 삭제처리를 하지 않음
			//------------------------------------------------------------------------------------------------------
			recPara         = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);				//차량스케줄ID
			recPara.setField("YD_EQP_WRK_STAT", "U");						//야드설비작업상태
			recPara.setField("YD_CARUD_ST_DT", szCurrDate);					//하차개시일시
			recPara.setField("YD_CARUD_CMPL_DT", szCurrDate);				//하차완료일시
			recPara.setField("YD_CAR_PROG_STAT", "E");						//차량진행상태 : 하차완료[E]
			recPara.setField("MODIFIER", szUser);							//수정자
			intRtnVal = ydCarSchDao.updYdCarsch(recPara, 0);
			
			if( intRtnVal == 0 ) {
				szMsg = "[JSP Session] " + szOperationName + " - 차량스케줄["+szYD_CAR_SCH_ID+"]에 하차개시일시, 하차완료일시, 차량진행상태[하차완료-E]를 업데이트시 차량스케줄이 존재하지 않습니다. : 반환값 - " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new DAOException(YdConstant.RETN_CD_FAILURE);
			}else if( intRtnVal < 0 ) {
				szMsg = "[JSP Session] " + szOperationName + " - 차량스케줄["+szYD_CAR_SCH_ID+"]에 하차개시일시, 하차완료일시, 차량진행상태[하차완료-E]를 업데이트시 오류발생 : 반환값 - " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				throw new DAOException(YdConstant.RETN_CD_FAILURE);
			}
			
			//------------------------------------------------------------------------------------------------------
			
			
			//------------------------------------------------------------------------------------------------------
			//	공통테이블 수정
			//------------------------------------------------------------------------------------------------------
			if( !szYD_GP.equals("") ) {
				if ( szYD_GP.equals(YdConstant.YD_GP_C_SLAB_YARD) 
						|| szYD_GP.equals(YdConstant.YD_GP_PORT_SLAB_YARD)   //항만슬라브야드 기능추가 - 2015.12.31 LeeJY
						|| szYD_GP.equals(YdConstant.YD_GP_A_PLATE_SLAB_YARD) 
						|| szYD_GP.equals(YdConstant.YD_GP_INTGR_YARD) )  {
			
					/*
					 * 공통테이블 업데이트 처리 - 아래모듈을 그냥 사용할 수 없음 만약 필요하면 다른 모듈을 적용 필요.
					 */
	//				intRtnVal = YdCommonUtils.procCarUnLoadCmpl(szYD_CAR_SCH_ID);
	//				if( intRtnVal <= 0 ) {
	//					//업무간 데이타가 공유되지 않아서 현재는 예외를 던지지 않음
	//					szMsg="[JSP Session] " + szOperationName + " - 공통테이블 업데이트 처리 실패";
	//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	//				}else{
	//					szMsg="[JSP Session] " + szOperationName + " - 공통테이블 업데이트 처리 성공";
	//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	//				}
				}else if ( szYD_GP.equals(YdConstant.YD_GP_PLATE_GDS_YARD) )  {				//후판제품창고야드
					
				}else if ( szYD_GP.equals(YdConstant.YD_GP_C_HR_COIL_MATL_YARD) 
						|| szYD_GP.equals(YdConstant.YD_GP_C_HR_COIL_GDS_YARD) )  {		//C열연코일소재야드 / C열연코일제품야드
					
				}

			}else{
				if( szARR_WLOC_CD.equals(YdConstant.WLOC_CD_A_PLATE_PLANT)||
					szARR_WLOC_CD.equals(YdConstant.WLOC_CD_B_PLATE_PLANT)){	//후판sizing개소코드
					
					//------------------------------------------------------------------------------------------------------
					//	이송지시테이블 수정
					//------------------------------------------------------------------------------------------------------
					
					
					//------------------------------------------------------------------------------------------------------
				}else if( szARR_WLOC_CD.equals(YdConstant.WLOC_CD_C_HR_PLANT) ) {//재열재 개소코드
					
				}
			}
			
			//------------------------------------------------------------------------------------------------------
			
			/*
			 *  차량이송재료 삭제처리 시작 
			 */
			//------------------------------------------------------------------------------------------------------
			// 1. 차량 이송재료를 조회 후 삭제처리
			//------------------------------------------------------------------------------------------------------
			outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
//getYdCarftmvmtl4			
			intRtnVal = ydCarFtmvMtlDao.getYdCarftmvmtl(recPara, outRecSet, 4);
			if(intRtnVal <= 0) {
				szMsg="[JSP Session] " + szOperationName + " 차량스케줄["+szYD_CAR_SCH_ID+"]에 이송재료 가 존재하지 않습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}else{
				String szSTL_NO				= "";
				String szYD_STK_LYR_NO		= "";
				recTemp = JDTORecordFactory.getInstance().create();
				for(int i = 1 ; i <= outRecSet.size(); i++ ) {
					outRecSet.absolute(i);
					recPara = outRecSet.getRecord();
					
					szSTL_NO	=	ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
					szYD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO");
					
					recTemp.setField("YD_CAR_SCH_ID", 		szYD_CAR_SCH_ID);
					recTemp.setField("STL_NO", 				szSTL_NO);
					recTemp.setField("DEL_YN", 				"Y");
					intRtnVal = ydCarFtmvMtlDao.updYdCarftmvmtl(recTemp, 0);
					if( intRtnVal == 0 ) {
						szMsg="[JSP Session] " + szOperationName + " : 차량스케줄["+szYD_CAR_SCH_ID+"]의 이송재료삭제 시 이송재료가 존재하지 않습니다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}else if( intRtnVal < 0 ) {
						szMsg="[JSP Session] " + szOperationName + " : 차량스케줄["+szYD_CAR_SCH_ID+"]의 이송재료삭제 시 오류발생 - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}else{
						szMsg="[JSP Session] " + szOperationName + " : 차량스케줄["+szYD_CAR_SCH_ID+"]의 이송재료삭제 성공";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
					
					//------------------------------------------------------------------------------------------------------
					//	Plate공통테이블 저장위치 수정
					//------------------------------------------------------------------------------------------------------
					if( szARR_WLOC_CD.equals(YdConstant.WLOC_CD_A_PLATE_PLANT)|| /* 후판sizing개소코드 */
						szARR_WLOC_CD.equals(YdConstant.WLOC_CD_B_PLATE_PLANT)){				
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("PLATE_NO", 			szSTL_NO);
						recPara.setField("YD_STK_COL_GP", 		YdConstant.KBRTPA);
						recPara.setField("YD_STK_BED_NO", 		"01");
						recPara.setField("YD_STK_LYR_NO", 		szYD_STK_LYR_NO);
						recPara.setField("YD_MTL_ITEM", 		"P");
						YdCommonUtils.setYdStrLocToPtComm(recPara, szMethodName);
					}
					//------------------------------------------------------------------------------------------------------
				}
			}
			
			if( szARR_WLOC_CD.equals(YdConstant.WLOC_CD_A_PLATE_PLANT)||
				szARR_WLOC_CD.equals(YdConstant.WLOC_CD_B_PLATE_PLANT)){//후판sizing개소코드
				//------------------------------------------------------------------------------------------------------
				//	이송지시테이블 수정
				//------------------------------------------------------------------------------------------------------
				YdCommonUtils.procCarUnLoadCmplForPlateGds(szYD_CAR_SCH_ID, YdConstant.YD_STR_LOC_CURR);
				//------------------------------------------------------------------------------------------------------
			}
			
			//------------------------------------------------------------------------------------------------------
			
			//+++++++++++++++++ 하차개시 전송 시작 ++++++++++++++++
			//		1	JMS_TC_CD			JMSTCCODE 			VARCHAR2(8)			YDTSJ009
			//		2	JMS_TC_CREATE_DDTT	JMSTC생성일시			DATE	(14)    	YYYYMMDDHHMMSS (24시간개념)	// 레코드 선언

			//		3.	TRN_EQP_CD			운송장비코드			VARCHAR2(8)
			//		4.	ARR_WLOC_CD         착지개소코드			VARCHAR2(5)
			//		5.	ARR_YD_PNT_CD       착지야드포인트코드		VARCHAR2(4)
			//		6.	TRN_WRK_ST_DT		운송작업시작일시		VARCHAR2(14)
			if( szARR_WLOC_CD.equals(YdConstant.WLOC_CD_A_PLATE_PLANT)||
				szARR_WLOC_CD.equals(YdConstant.WLOC_CD_B_PLATE_PLANT)){
				szARR_YD_PNT_CD = YdConstant.WLOC_CD_A_PLATE_PLANT_PNT_CD;
			}else if( szARR_WLOC_CD.equals(YdConstant.WLOC_CD_C_HR_PLANT) ) {
				szARR_YD_PNT_CD = YdConstant.WLOC_CD_C_HR_PLANT_PNT_CD;
			}else{
				outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
				recPara         = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_STK_COL_GP", 			szYD_CAR_STOP_LOC);
				intRtnVal = ydStkColDao.getYdStkcol(recPara, outRecSet, 0);
				if( intRtnVal <= 0 ) {
					szMsg="[JSP Session] " + szOperationName + " - 차량정지위치[" + szYD_CAR_STOP_LOC + "]에 대한 개소코드와 야드포인트코드가 존재하지 않습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}else{
					outRecSet.first();
					recPara = outRecSet.getRecord();
					szARR_WLOC_CD = ydDaoUtils.paraRecChkNull(recPara, "WLOC_CD");
					szARR_YD_PNT_CD = ydDaoUtils.paraRecChkNull(recPara, "YD_PNT_CD");
					szMsg="[JSP Session] " + szOperationName + " - 차량정지위치[" + szYD_CAR_STOP_LOC + "]에 대한 개소코드[" + szARR_WLOC_CD + "]와 야드포인트코드[" + szARR_YD_PNT_CD + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
			}
			
			recPara         = JDTORecordFactory.getInstance().create();
			szTcCode = "YDTSJ009";
			//2. 하차개시를 구내운송으로 전송
			recPara.setField("JMS_TC_CD", 			szTcCode);
			recPara.setField("YD_CAR_SCH_ID", 		szYD_CAR_SCH_ID);
//			recPara.setField("JMS_TC_CREATE_DDTT",  YdUtils.getCurDate("yyyyMMddHHmmss"));
//			// 운송장비코드
//			recPara.setField("TRN_EQP_CD", szTRN_EQP_CD);	
//			// 착지개소코드
//			recPara.setField("ARR_WLOC_CD", szARR_WLOC_CD);
//			// 착지야드포인트코드
//			recPara.setField("ARR_YD_PNT_CD", szARR_YD_PNT_CD);
//			// 운송작업시작일시 [하차개시일시]
//			recPara.setField("TRN_WRK_ST_DT", szCurrDate);
//			
//			YdDeleComm deleComm = new YdDeleComm();
//			szWkGp =szTcCode.substring(2, 4);
//			propertyService = PropertyService.getInstance();
//			szQueueName =propertyService.getProperty("common.properties", "jms.queue."+szWkGp+"_MDB_QUEUE");
						
			szMsg = "[JSP Session] " + szOperationName + " - 하차개시전문을 구내운송으로 전송 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			ydUtils.displayRecord(szOperationName, recPara);
			
			YdDelegate ydDelegate = new YdDelegate();
			
			ydDelegate.sendMsg(recPara);
			
			//deleComm.jmsQSnder(szQueueName, recPara);
			
			szMsg = "[JSP Session] " + szOperationName + " - 하차개시전문을 구내운송으로 전송 완료";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//+++++++++++++++++ 하차개시 전송 끝 ++++++++++++++++
			
			//+++++++++++++++++ 하차완료 전송 시작 ++++++++++++++++
			//		1	JMS_TC_CD			JMSTCCODE 			VARCHAR2(8)			YDTSJ010
			//		2	JMS_TC_CREATE_DDTT	JMSTC생성일시			DATE	(14)		YYYYMMDDHHMMSS (24시간개념)

			//		3	TRN_EQP_CD          운송장비코드			VARCHAR2(8)
			//		4	ARR_WLOC_CD         착지개소코드			VARCHAR2(5)
			//		5	ARR_YD_PNT_CD		착지야드포인트코드		VARCHAR2(4)
			//		6	CARUD_CMPL_DT       하차완료일시			VARCHAR2(14)
			
			recPara         = JDTORecordFactory.getInstance().create();
			szTcCode = "YDTSJ010";
			//2. 하차완료를 구내운송으로 전송
			recPara.setField("JMS_TC_CD", 					YdConstant.YDYDJ701);
			recPara.setField(YdConstant.BUFFER_TC_CD, 		szTcCode);
			recPara.setField("YD_CAR_SCH_ID", 				szYD_CAR_SCH_ID);
//			recPara.setField("JMS_TC_CREATE_DDTT",   YdUtils.getCurDate("yyyyMMddHHmmss"));
//			// 운송장비코드
//			recPara.setField("TRN_EQP_CD", szTRN_EQP_CD);	
//			// 착지개소코드
//			recPara.setField("ARR_WLOC_CD", szARR_WLOC_CD);
//			// 착지야드포인트코드
//			recPara.setField("ARR_YD_PNT_CD", szARR_YD_PNT_CD);
//			// 운송작업시작일시 [하차완료일시]
//			recPara.setField("CARUD_CMPL_DT", szCurrDate);
			
			szMsg = "[JSP Session] " + szOperationName + " - 하차완료전문을 구내운송으로 전송 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			ydUtils.displayRecord(szOperationName, recPara);
			
			ydDelegate.sendMsg(recPara);
			//deleComm.jmsQSnder(szQueueName, recPara);
			
			szMsg = "[JSP Session] " + szOperationName + " - 하차완료전문을 구내운송으로 전송 완료";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			//+++++++++++++++++ 하차완료 전송 끝 ++++++++++++++++
			
			//+++++++++++++++++ YDPRJ001 후판제품반납하차실적 전송 시작 ++++++++++++++++
			if( szARR_WLOC_CD.equals(YdConstant.WLOC_CD_A_PLATE_PLANT)||
				szARR_WLOC_CD.equals(YdConstant.WLOC_CD_B_PLATE_PLANT)){
				recPara         = JDTORecordFactory.getInstance().create();
				recPara.setField("JMS_TC_CD", 			"YDPRJ001");
				recPara.setField("YD_CAR_SCH_ID", 		szYD_CAR_SCH_ID);
				
				//YdDelegate      ydDelegate      = new YdDelegate();
				ydDelegate.sendMsg(recPara);
			}
			//+++++++++++++++++ YDPRJ001 후판제품반납하차실적 전송 끝++++++++++++++++
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return szRtnMsg;
	}//end of complCarUd
	
	/**
	 * 오퍼레이션명 : 차량스케줄생성
	 * 심명순 (090805)  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public void mkYardCarSch(JDTORecord msgRecord)throws JDTOException  {
		int       intRtnVal    = 0;
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		String aaa =  "";
		String trnEqpCd = "";
		String carNo  = "";
		String szMsg        = "";		
		String szMethodName = "mkYardCarSch";
		String szOperationName  = "차량스케줄생성";
		
		YdCarSchDao ydCarSchDao = new YdCarSchDao();
		
		try {
			ydUtils.putLog(szSessionName, szMethodName,  "mkYardCarSch() IN", YdConstant.DEBUG);

					
			if(aaa.equals("TEST")){// (현재 구분 없이 그냥 수행중) 출하나 구내운송을 구분할때 쓸 예정
				
			}else{
				System.out.println("msgRecord에 YD_CAR_STOP_LOC가 안들어와?"+ msgRecord);
				
				String carUseGp  = yddatautil.setDataDefault(msgRecord.getField("YD_CAR_USE_GP"), "");
				
				if(carUseGp.equals("L")){
					System.out.println("L구내운송!! 여기 안들어오는듯?");
					trnEqpCd  = yddatautil.setDataDefault(msgRecord.getField("TRN_EQP_CD"), "");
				}else if(carUseGp.equals("G")){
					System.out.println("G출하!! 여기 안들어오는듯?");
					carNo  = yddatautil.setDataDefault(msgRecord.getField("TRN_EQP_CD"), "");
				}
				
				recPara.setField("YD_CAR_SCH_ID",		yddatautil.setDataDefault(msgRecord.getField("YD_CAR_SCH_ID"), ""));
				recPara.setField("YD_CAR_USE_GP",		carUseGp);
				recPara.setField("TRN_EQP_CD",			trnEqpCd);
				recPara.setField("CAR_NO",				carNo);
				recPara.setField("YD_EQP_WRK_STAT",		"U");
				recPara.setField("YD_EQP_WRK_SH",		yddatautil.setDataDefault(msgRecord.getField("YD_WRK_ALW_SH"), ""));
				recPara.setField("YD_EQP_WRK_WT",		yddatautil.setDataDefault(msgRecord.getField("YD_WRK_ALW_WT"), ""));
				//recPara.setField("SPOS_WLOC_CD",		yddatautil.setDataDefault(msgRecord.getField("SPOS_WLOC_CD"), ""));
				recPara.setField("YD_CARLD_STOP_LOC",	yddatautil.setDataDefault(msgRecord.getField("YD_CAR_STOP_LOC"), ""));
				recPara.setField("YD_CAR_PROG_STAT",	yddatautil.setDataDefault(msgRecord.getField("YD_CAR_PROG_STAT"), ""));
				recPara.setField("DEST_TEL_NO",			yddatautil.setDataDefault(msgRecord.getField("DEST_TEL_NO"), ""));
				recPara.setField("CARD_NO",				yddatautil.setDataDefault(msgRecord.getField("CARD_NO"), ""));
				recPara.setField("REGISTER",			yddatautil.setDataDefault(msgRecord.getField("YD_USER_ID"), ""));
				recPara.setField("YD_BAYIN_WO_SEQ",		"9");
				
				if(carUseGp.equals("G")) {
					recPara.setField("CAR_KIND","TR");
				}else{
					recPara.setField("CAR_KIND","PT");
				}
				
				System.out.println("carUseGp 에 제대로 값이?"+ carUseGp);
				System.out.println("trnEqpCd 에 제대로 값이?"+ trnEqpCd);
				System.out.println("carNo 에 제대로 값이?"+ carNo);
				
				ydUtils.displayRecord(szOperationName, recPara);
				
				intRtnVal = ydCarSchDao.insYdCarsch(recPara);
				
				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
				}// end of if
				System.out.println("mkYardCarSch 끝 ");
			}// end of for	
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		ydUtils.putLog(szSessionName, szMethodName,  "mkYardCarSch() OUT", 3);
		
	} //end of mkYardCarSch()
	
		
		
	/**
	 * 차량 작업 관리 화면 배차등록 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet intCarSch(JDTORecord [] inDto) throws DAOException {
		int       intRtnVal    = 0;
		//JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		//YdCarFtmvMtlDao  ydCarftmvmtlDao = new YdCarFtmvMtlDao();
		JDTORecord recPara = null;
		String szMsg        = "";		
		String szMethodName = "intCarSch";
		String szOperationName = "배차등록";
		YdCarSchDao ydCarSchDao = new YdCarSchDao();
		YdCarSpecDao ydCarSpecDao = new YdCarSpecDao();
		
		try {	
			
			String strSchId = yddatautil.setDataDefault(inDto[0].getField("YD_CAR_SCH_ID"),"");
			String strTrnEqpCd = yddatautil.setDataDefault(inDto[0].getField("TRN_EQP_CD"),"");
			
			System.out.println("값이 뭐일까 궁금합니다=====  "+strSchId);
			
			if(strSchId.equals("TEST")){// 출하일때(현재 구분 없이 그냥 수행중)
				
			}else{
				recPara = inDto[0];
				
				ydUtils.displayRecord(szOperationName, recPara);
				
				intRtnVal = ydCarSpecDao.insYdCarspec(recPara);
				
				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
				} 					
			}// end of if
				
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of intCarSch
	

	/**
	 * 이송대상재 POP(조회)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */
	
	public JDTORecordSet getCoilYdRcptPlnMtl(JDTORecord inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg="";
		
		String szMethodName="getCoilYdRcptPlnMtl";		
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
	
		JDTORecord recPara = JDTORecordFactory.getInstance().create();

		YdStockDao ydStockDao = new YdStockDao();
		String szCAR_POINT = null;
		String szBAY = null;
		String szSPOS_WLOC_CD = null;
		
		try {
//			szCAR_POINT = inDto.getFieldString("CAR_POINT");
//			szBAY = inDto.getFieldString("BAY");
			szCAR_POINT = ydDaoUtils.paraRecChkNull(inDto, "CAR_POINT");
			szBAY = ydDaoUtils.paraRecChkNull(inDto, "BAY");
//			if( szCAR_POINT.equals("") ) {
//				szCAR_POINT = szBAY;
//			}else{
//				szCAR_POINT = szCAR_POINT.substring(0, 2);
//			}
			ydUtils.putLog(szSessionName, szMethodName , "getCoilYdRcptPlnMtl1" , YdConstant.ERROR);
			
			
			szSPOS_WLOC_CD = ydDaoUtils.paraRecChkNull(inDto, "WLOC_CD");
			
			recPara.setField("SPOS_WLOC_CD",	    ydDaoUtils.paraRecChkNull(inDto, "WLOC_CD"));
			recPara.setField("YD_AIM_RT_GP",	    ydDaoUtils.paraRecChkNull(inDto, "YD_AIM_RT_GP"));
			//recPara.setField("YD_STK_COL_GP",	    szCAR_POINT);
			recPara.setField("PAGE_CNT1", inDto.getFieldString("PAGE_NO"));
			recPara.setField("PAGE_CNT2", inDto.getFieldString("PAGE_NO"));
			recPara.setField("ROW_CNT1",  inDto.getFieldString("ROWCOUNT"));
			recPara.setField("ROW_CNT2",  inDto.getFieldString("ROWCOUNT"));

			ydUtils.putLog(szSessionName, szMethodName , "getCoilYdRcptPlnMtl2" , YdConstant.ERROR);
			
			
			intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 404);
			
			outRecSet.first();			
			
			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "[JSP Session] 이송대상재 POP(조회) - 리턴값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "[JSP Session] 이송대상재 POP(조회) - 리턴값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				return outRecSet;
			} // end of if	
			 			
		
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}	
	
	/**
	 * 이송대상재 저장품 수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 */
	public String updCoilFrtoMoveMtlToStock(JDTORecord [] inDto) {
		int       intRtnVal    = 0;
		int       intSh        = 0;
		String    szMsg        = null;
		String    szMethodName = "updCoilFrtoMoveMtlToStock";
		String    szOperationName = "이송대상재 저장품 수정";
		String 	  szRtnMsg 		= YdConstant.RETN_CD_SUCCESS;

		JDTORecord    recPara  = JDTORecordFactory.getInstance().create();	
		YdStockDao ydStockDao = new YdStockDao();
			
		String szSTL_NO = null;
		String szYD_AIM_YD_GP = null;
		String szYD_AIM_BAY_GP = null;
		String szYD_AIM_RT_GP = null;
		
		try {
			for(int x=0;x<inDto.length;x++){
				szSTL_NO = ydDaoUtils.paraRecChkNull(inDto[x], "STL_NO");
				szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(inDto[x], "YD_AIM_YD_GP");
				szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(inDto[x], "YD_AIM_BAY_GP");
				szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(inDto[x], "YD_AIM_RT_GP");
				szMsg = "[JSP Session : " + szOperationName + "] 재료번호[" + szSTL_NO + "], 목표야드[" + szYD_AIM_YD_GP + "], 목표동[" + szYD_AIM_BAY_GP + "], 목표행선[" + szYD_AIM_RT_GP + "]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				recPara.setField("STL_NO", 			szSTL_NO);
				recPara.setField("YD_AIM_YD_GP", 	szYD_AIM_YD_GP);
				recPara.setField("YD_AIM_BAY_GP", 	szYD_AIM_BAY_GP);
				recPara.setField("YD_AIM_RT_GP", 	szYD_AIM_RT_GP);
				intRtnVal = ydStockDao.updYdStock(recPara, 0);
				if( intRtnVal < 0 ) {
					szMsg = "[JSP Session : " + szOperationName + "] 재료번호[" + szSTL_NO + "], 목표야드[" + szYD_AIM_YD_GP + "], 목표동[" + szYD_AIM_BAY_GP + "], 목표행선[" + szYD_AIM_RT_GP + "] 수정시 오류발생 - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}else if( intRtnVal == 0 ) {
					szMsg = "[JSP Session : " + szOperationName + "] 재료번호[" + szSTL_NO + "], 목표야드[" + szYD_AIM_YD_GP + "], 목표동[" + szYD_AIM_BAY_GP + "], 목표행선[" + szYD_AIM_RT_GP + "] 수정시 재료가 존재하지 않습니다. - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}else{
					szMsg = "[JSP Session : " + szOperationName + "] 재료번호[" + szSTL_NO + "], 목표야드[" + szYD_AIM_YD_GP + "], 목표동[" + szYD_AIM_BAY_GP + "], 목표행선[" + szYD_AIM_RT_GP + "] 수정 성공";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
			}
			szMsg = "[JSP Session : " + szOperationName + "] 저장품 수정 성공";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			szMsg = "[JSP Session : " + szOperationName + "] 오류발생[1] : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(YdConstant.RETN_CD_FAILURE);
		} finally {
		}
		return szRtnMsg;
	}	// end of updFrtoMoveMtlToStock
		
	
	/**
	 *  차량작업관리 차량스펙정보 조회 
	 * 심명순 (090731)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCarSpecInfo(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szTemp     ="";
		String szMethodName = "getCarSpecInfo";

		YdCarSpecDao ydCarSpecDao = new YdCarSpecDao(); 
		
		int intRtnVal = 0;
		
		try {	
			ydUtils.putLog(szSessionName, szMethodName,  "getCarSpecInfo() IN", 3);
			
			recPara.setField("CAR_NO",  yddatautil.setDataDefault(inDto.getField("TRN_EQP_CD"), ""));
			
//			System.out.println("recPara====!!!!"+recPara);	
//			System.out.println("outRecSet===="+outRecSet);
//getYdCarspec6			
				intRtnVal = ydCarSpecDao.getYdCarspec(recPara, outRecSet, 6);
			//System.out.println("intRtnVal >>>>>>"+intRtnVal);
			
			if (intRtnVal <= 0) {
				if(intRtnVal == 0){
					outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
//getYdCarspec7					
					intRtnVal = ydCarSpecDao.getYdCarspec(recPara, outRecSet, 7);
					System.out.println("intRtnVal >>>>>>"+intRtnVal);
					if (intRtnVal < 0) {
						if (intRtnVal == -1) {
							szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						} else {
							szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						}
					}return outRecSet;
					
				}else if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	
				} // end of if
			
			
				//레코드셋이 없을때까지 반복한다.
				outRecSet.first();
				
				logger.println(LogLevel.DEBUG_TEXT, "getCarSpecInfo");
			
			}
				
			ydUtils.putLog(szSessionName, szMethodName,  "getCarSpecInfo() OUT", YdConstant.DEBUG);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCarSpecInfo
	
	/**
	 *  차량작업관리 차량스펙정보 조회 코일
	 * 심명순 (090731)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCarSpecInfoCoil(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szTemp     ="";
		String szMethodName = "getCarSpecInfo";

		YdCarSpecDao ydCarSpecDao = new YdCarSpecDao(); 
		
		int intRtnVal = 0;
		
		try {	
			ydUtils.putLog(szSessionName, szMethodName,  "getCarSpecInfoCoil() IN", 3);
			
			recPara.setField("CAR_NO",  yddatautil.setDataDefault(inDto.getField("TRN_EQP_CD"), ""));
			
//			System.out.println("recPara====!!!!"+recPara);	
//			System.out.println("outRecSet===="+outRecSet);
//getYdCarspec6			
				intRtnVal = ydCarSpecDao.getYdCarspec(recPara, outRecSet, 6);
			//System.out.println("intRtnVal >>>>>>"+intRtnVal);
			
			if (intRtnVal <= 0) {
				if(intRtnVal == 0){
					outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
//getYdCarspec7					
					intRtnVal = ydCarSpecDao.getYdCarspec(recPara, outRecSet, 7);
					System.out.println("intRtnVal >>>>>>"+intRtnVal);
					if (intRtnVal < 0) {
						if (intRtnVal == -1) {
							szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						} else {
							szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						}
					}return outRecSet;
					
				}else if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
	
				} // end of if
			
			
				//레코드셋이 없을때까지 반복한다.
				outRecSet.first();
				
				logger.println(LogLevel.DEBUG_TEXT, "getCarSpecInfoCoil");
			
			}
				
			ydUtils.putLog(szSessionName, szMethodName,  "getCarSpecInfoCoil() OUT", YdConstant.DEBUG);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCarSpecInfoCoil
	
	/**
	 * 차량작업관리 상차LOT편성  
	 * 심명순 (090723)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return 
	 * @return 
	 * @return
	 * @throws DAOException
	 */
	
	public JDTORecordSet insSangchaLot(JDTORecord inDto) throws DAOException {
		int       intRtnVal    = 0;
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecord recPara = null;
		String szMsg        = "";
		String szYdUserId        = "";
		String szMethodName = "insSangchaLot";
		String szOperationName = "상차LOT편성";
		
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		
		try {
			
			ydUtils.displayRecord(szOperationName, inDto);
			
			recPara         = JDTORecordFactory.getInstance().create();
			ydUtils.putLog(szSessionName, szMethodName,  "insSangchaLot() IN", YdConstant.DEBUG);
					
			
			recPara.setField("TRN_CAR_NO",yddatautil.setDataDefault(inDto.getField("TRN_CAR_NO"), ""));
			
			szYdUserId = yddatautil.setDataDefault(inDto.getField("YD_USER_ID"), "");
							
			System.out.println("YdCarSpec_update_recPara==>>"+recPara);
//getYdWrkbook16			
			intRtnVal = ydWrkbookDao.getYdWrkbook(recPara, outRecSet, 16);
			
			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
			} else if(intRtnVal == 0){
				outRecSet.first();
				recPara = outRecSet.getRecord();
				System.out.println(outRecSet.toString());
				// REGISTER 추가 받아 보내기 
				recPara.setField("REGISTER", szYdUserId);
				insWrkbook(recPara);
			}else if(intRtnVal > 0){
				
				outRecSet.first();
				recPara = outRecSet.getRecord();
				// 수정자 추가 받아 보내기 
				recPara.setField("MODIFIER", szYdUserId); 
				updWrkbook(recPara);
			}
			
			//mkYardCarSch(inDto[0]);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of insSangchaLot
	
	/**
	 * 차량작업관리 상차LOT편성 (정보 있을시 MODIFIER MOD_DATE 업데이트 처리) 
	 * 심명순 (090723)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return 
	 * @return 
	 * @return
	 * @throws DAOException
	 */
	
	public String  updWrkbook(JDTORecord   outRec) throws DAOException {
		int       intRtnVal    = 0;
		JDTORecord recPara = null;
		String szMsg        = "";		
		String szMethodName = "updWrkbook";
		
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		
		
		try {
			
			recPara         = JDTORecordFactory.getInstance().create();
			ydUtils.putLog(szSessionName, szMethodName,  "updWrkbook() IN",YdConstant.DEBUG);
			
			
			recPara.setField("YD_WBOOK_ID",yddatautil.setDataDefault(outRec.getField("YD_WBOOK_ID"), ""));
			recPara.setField("MODIFIER",yddatautil.setDataDefault(outRec.getField("MODIFIER"),""));
							
			intRtnVal = ydWrkbookDao.updYdWrkbook(recPara, 0);
			
			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
			} 		
						
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return YdConstant.RETN_CD_SUCCESS;
	}//end of updWrkbook
	
	
	/**
	 * 차량작업관리 상차LOT편성 (정보 없을시 INSERT 처리) 
	 * 심명순 (090723)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return 
	 * @return 
	 * @return
	 * @throws DAOException
	 */
	
	public String  insWrkbook(JDTORecord   outRec) throws DAOException {
		int       intRtnVal    = 0;
		JDTORecord recPara = null;
		String szMsg        = "";		
		String szMethodName = "insWrkbook";
		
		//		설비ID
		String szYD_EQP_ID            = null;
		//		스케줄코드
		String szYD_SCH_CD            = null;
		
		YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		
		
		try {
			
			recPara         = JDTORecordFactory.getInstance().create();
			ydUtils.putLog(szSessionName, szMethodName,  "insWrkbook() IN", YdConstant.DEBUG);
			szYD_EQP_ID = yddatautil.setDataDefault(outRec.getField("YD_EQP_ID"), "");
			//=========스케줄 코드 생성=======
			szYD_SCH_CD = szYD_EQP_ID.substring(0, 4) + "0" + szYD_EQP_ID.substring(5, 6) + "UM";
			//=================================================================================
			outRec.setField("YD_SCH_CD", szYD_SCH_CD);
			
			recPara.setField("YD_WBOOK_ID",yddatautil.setDataDefault(outRec.getField("YD_WBOOK_ID"), ""));
			recPara.setField("MODIFIER",yddatautil.setDataDefault(outRec.getField("MODIFIER"),""));
							
			intRtnVal = ydWrkbookDao.updYdWrkbook(recPara, 0);
			
			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
			} 		
						
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return YdConstant.RETN_CD_SUCCESS;
	}//end of insWrkbook
	
	
//	/**
//	 * 코일제품야드 입고진행관리 목표동 수정
//	 *
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param inDto
//	 * @return
//	 */
//	public String updCoilYdAimBayModify(JDTORecord [] inDto) {
//		int       intRtnVal    = 0;
//		String    szMsg        = "";
//		String    szMethodName = "updCoilYdAimBayModify";
//		String szOperationName = "목표동 수정";
//
//		
//		YdUtils ydUtils = new YdUtils();
//				
//		JDTORecord    recPara  = JDTORecordFactory.getInstance().create();	
//		YdStockDao ydStockDao = new YdStockDao();
//		
//		
//		ydUtils.putLog(szSessionName, szMethodName,  "updCoilYdAimBayModify() IN", YdConstant.DEBUG);
//
//		try {
//			
//			
//				// 수정
//			for(int x=0;x<inDto.length;x++){					
//	
//				recPara.setField("STL_NO",   		yddatautil.setDataDefault(inDto[x].getField("STL_NO"),     ""));
//				recPara.setField("YD_AIM_BAY_GP",    	yddatautil.setDataDefault(inDto[x].getField("YD_AIM_BAY_GP"),     ""));
//				//수정
//				recPara.setField("MODIFIER",   			 yddatautil.setDataDefault(inDto[x].getFieldString("YD_USER_ID"),""));
//				
//				
//				ydUtils.displayRecord(szOperationName, recPara);
//				
//		        intRtnVal = ydStockDao.updYdStock(recPara,0);
//		        
//				if (intRtnVal < 0) {
//					if (intRtnVal == -1) {
//						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//					} else {
//						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//					}
//				} // end of if				
//			}
//		} catch (Exception e) {
//			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
//			throw new DAOException(getClass().getName() + e.getMessage(),e);
//		} finally {
//		}
//
//		ydUtils.putLog(szSessionName, szMethodName,  "updCoilYdAimBayModify() OUT", YdConstant.DEBUG);
//		return YdConstant.RETN_CD_SUCCESS;
//	}	// end of updCoilYdAimBayModify
//	
	
	/**
	 * 코일제품야드 입고진행관리 추출요구 기능
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 */
	public String R3shearOutLineOffReq(JDTORecord [] inDto) {
		String    szMsg        = null;
		String    szMethodName = "R3shearOutLineOffReq";
		String szOperationName = "추출요구 기능";
		
		String  szEqpGp = null;
		String  szChgEqpGp = null;
		String  szTcGp = null;
		String  szTcCode = null;
		String sRTN_MSG	  = "";
		String sRTN_CD    = "";
		JDTORecord outRecord1    = JDTORecordFactory.getInstance().create();
		JDTORecord    recPara  =null;
		YdDelegate ydDelegate = new YdDelegate();
		
		szMsg        = "";

		ydUtils.putLog(szSessionName, szMethodName,  "R3shearOutLineOffReq() IN", YdConstant.DEBUG);

		try {
			
			for(int x=0;x<inDto.length;x++){					
				//	TC CCODE

				/*
					YDH2L003 (SPM1 정정 출측 LINE OFF 실적)
					YDH2L013 (HFL 정정 출측 LINE OFF 실적)
					YDH2L023 (SPM2 정정 출측 LINE OFF 실적)
				 */
				
				 recPara  = JDTORecordFactory.getInstance().create();	
				 
				 
				//설비 ID 정보로 적치열 + 베;드정보를 읽오는 Function
				 
				szMsg = "[JSP Session : "+szOperationName+"] 설비정보를 야드 적치정보로 Mapping 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
				szEqpGp = ydDaoUtils.paraRecChkNull(inDto[x], "EQP_GP");
				
		
				szChgEqpGp = (String)YdCommonUtils.h_hRvsstEqpGpMatch.get(szEqpGp); 
				
				
				szMsg = "[JSP Session : "+szOperationName+"] 설비정보를 야드 적치정보로 Mapping 끝";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				
				if(szChgEqpGp == null || szChgEqpGp.equals("")){
					szMsg = "설비[" + szEqpGp + "] 로 매칭되는 설비 구분값이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_NOTEXIST ;
				}
				
				szTcGp = szEqpGp.substring(0,3);
				
				if(szTcGp.equals("SDC")){
					// SPM1 정정출축 'SDC'
					szTcCode = "YDH2L003";
				} else if(szTcGp.equals("HDC") ||  szTcGp.equals("K2-") || szTcGp.equals("K3-")){
					// HFL 정정 출측 'HDC'
					szTcCode = "YDH2L013";
				} else if(szTcGp.equals("DDC")){
					// SPM2 정정출측 'DDC'
					szTcCode = "YDH2L023";
				} else {
					return "해당 설비는 전문전송할 필요가 없습니다."; ////
					
				}
					
				recPara.setField("JMS_TC_CD",		szTcCode);
				recPara.setField("STL_NO",   		 ydDaoUtils.paraRecChkNull(inDto[x], "COIL_NO"));
				recPara.setField("YD_EQP_ID",  		szChgEqpGp.substring(0,6));
				recPara.setField("YD_STK_BED_NO",   szChgEqpGp.substring(6,8));
				
				ydUtils.displayRecord(szOperationName, recPara);
			
				ydDelegate.sendMsg(recPara);				
			
				
//coilYdGdsInPlant.jsp 화면에서 백업처리함(H2YDL003) 기동				
				EJBConnector ejbConn = null;
				
				recPara  = JDTORecordFactory.getInstance().create();	
				recPara.setField("JMS_TC_CD",		"H2YDL003");
				recPara.setField("STL_NO",   		ydDaoUtils.paraRecChkNull(inDto[x], "COIL_NO"));
				recPara.setField("YD_EQP_ID",  		szChgEqpGp.substring(0,6));
				recPara.setField("YD_STK_BED_NO",   szChgEqpGp.substring(6,8));

				ejbConn = new EJBConnector("default", this);
				outRecord1 = (JDTORecord)ejbConn.trx("CoilRcptWrkDmdSeEJB", "procR3ShearOutLineOffReq", recPara);
				sRTN_CD	 = StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
				sRTN_MSG = StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
			}
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		ydUtils.putLog(szSessionName, szMethodName,  "R3shearOutLineOffReq() OUT", YdConstant.DEBUG);
		return YdConstant.RETN_CD_SUCCESS;
	}	// end of R3shearOutLineOffReq
	
	
	
	/**
	 * 코일제품창고 사유별이적조회 상단데이터조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 */
	public void getBecauseMvUpLyr(JDTORecord [] inDto) {
		int       intRtnVal    = 0;
		String    szMsg        = null;
		String    szMethodName = "getBecauseMvUpLyr";		
		YdUtils ydUtils = new YdUtils();
		
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecord    recPara  = JDTORecordFactory.getInstance().create();	
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao(); 
		
		szMsg        = "";

		ydUtils.putLog(szSessionName, szMethodName,  "getBecauseMvUpLyr() IN", YdConstant.DEBUG);

		try {
			
			
				
			for(int x=0;x<inDto.length;x++){					
	
				recPara.setField("FROMLOC",   		yddatautil.setDataDefault(inDto[x].getField("FROMLOC"),     ""));
//getYdStklyr70				
		        intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 70);
		        
		        System.out.println("OUTRECSET에는??? ===>>>"+outRecSet.toString());
		        
				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
				} // end of if				
			} // end of for 
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		ydUtils.putLog(szSessionName, szMethodName,  "getBecauseMvUpLyr() OUT", YdConstant.DEBUG);
	}	// end of getBecauseMvUpLyr
	
	
	
	
	
	/**
	 * 주문별 재고조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 */
	public JDTORecordSet getCoidGdsYdOrdInfoMtl(JDTORecord  inDto) {
		int       intRtnVal    = 0;
		String    szMsg        = null;
		String    szMethodName = "getCoidGdsYdOrdInfoMtl";		
		YdUtils   ydUtils = new YdUtils();
		
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecord    recPara  = JDTORecordFactory.getInstance().create();	
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao(); 
		
		szMsg        = "";

		ydUtils.putLog(szSessionName, szMethodName,  "getBecauseMvUpLyr() IN", YdConstant.DEBUG);

		try {
			
			recPara.setField("YD_GP",ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));
			recPara.setField("ORD_NO",ydDaoUtils.paraRecChkNull(inDto, "ORD_NO"));
			recPara.setField("ORD_DTL",ydDaoUtils.paraRecChkNull(inDto, "ORD_DTL"));
			recPara.setField("DEST_CD",ydDaoUtils.paraRecChkNull(inDto, "DEST_CD"));
			recPara.setField("CUST_CD",ydDaoUtils.paraRecChkNull(inDto, "CUST_CD"));
//getYdStklyr87			
//PIDEV_S :병행가동용:PI_YD
			recPara.setField("PI_YD",ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));						
		    intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 87);
		        
		    if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
			} // end of if		
				
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		ydUtils.putLog(szSessionName, szMethodName,  "getBecauseMvUpLyr() OUT", YdConstant.DEBUG);
		
		return outRecSet;
	}	// end of getCoidGdsYdOrdInfoMtl
	
	
	
	
	/**
	 * 수주별 , 고객사별 재고조회(재료정보)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 */
	public JDTORecordSet getCoidGdsYdOrdGpMtl(JDTORecord  inDto) {
		int       intRtnVal    = 0;
		String    szMsg        = null;
		String    szMethodName = "getCoidGdsYdOrdGpMtl";		
		YdUtils   ydUtils = new YdUtils();
		String szOperationName = "수주별 , 고객사별 재고조회(재료정보)";
		
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecord    recPara  = JDTORecordFactory.getInstance().create();	
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao(); 
		
		szMsg        = "";

		szMsg = "[Jsp-Session  - "+ szOperationName  + "] + 시작";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		try {
			
			recPara.setField("YD_GP"	,ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));
			recPara.setField("YD_BAY_GP",ydDaoUtils.paraRecChkNull(inDto, "YD_BAY_GP"));
//getYdStklyr88
//PIDEV_S :병행가동용:PI_YD
			recPara.setField("PI_YD", ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));	
		    intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 88);
		        
		    if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
			} // end of if		
				
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "[Jsp-Session  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return outRecSet;
	}	// end of getCoidGdsYdOrdGpMtl
	
	
	

	/**
	 *  Span별 적치사양조정
	 *  !A 저장위치기준설정 > 목록조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 */
	public JDTORecordSet getYdStkcolBaySpan(JDTORecord  inDto) {
		int       intRtnVal    = 0;
		String    szMsg        = null;
		String    szMethodName = "getYdStkcolBaySpan";		
		YdUtils   ydUtils = new YdUtils();
		String szOperationName = "Span별 적치사양조정";
		
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecord    recPara  = JDTORecordFactory.getInstance().create();	
		
		YdStkColDao ydStkColDao = new YdStkColDao();
		
		szMsg        = "";

		szMsg = "[Jsp-Session  - "+ szOperationName  + "] + 시작";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		try {
			
			recPara.setField("YD_GP",ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));
			recPara.setField("YD_BAY_GP",ydDaoUtils.paraRecChkNull(inDto, "YD_BAY_GP"));
			recPara.setField("YD_EQP_GP",ydDaoUtils.paraRecChkNull(inDto, "YD_EQP_GP"));
			//!A 야드기준관리 > 저위치좌표설정 화면 때문에 열번호 추가 (박지열 - 2010/03/25)
			recPara.setField("YD_STK_COL_NO",ydDaoUtils.paraRecChkNull(inDto, "YD_STK_COL_NO"));
//getYdStkcol21		    
		    intRtnVal =  ydStkColDao.getYdStkcol(recPara, outRecSet, 21);
		        
		    if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
			} // end of if		
				
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		szMsg = "[Jsp-Session  - "+ szOperationName  + "] + 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return outRecSet;
	}	// end of getYdStkcolBaySpan
	
	
	
	
	
	
	
	
	/**
	 * 코일제품창고 사유별이적조회 지시버튼
	 * 후판아니면 삭제처리해야 함
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 */
	public String upBecauseMv(JDTORecord [] inDto, JDTORecord paraRec) {
		
		int intSh             = 0;
		int intStlCnt         = 0;
		int x                 = 0;
		String szMsg          = "";
		String szRtnMsg       = YdConstant.RETN_CD_SUCCESS;
		String szMethodName   = "upBecauseMv";
		String szOperationName = "작업지시 생성";
		String [] strArrStlNo = null;
		String szYD_STK_COL_GP = "";
		String szYD_STK_BED_NO = "";
		String szYD_TO_LOC_GUIDE = "";
		String szSPAN_GP = "";
		String szYD_SCH_CD = "";
		String szJB_GP = "";
		String szJB_GP_old = "";
		JDTORecord recPara    = null;
		
		String szToBedNo = null;
		String szToColGp = null;
		String szTemp = null;
		String szTCar = null;
		
		try {
			
			
			szMsg = "JSP-SESSION [후판야드 메뉴얼 작업지시 편성] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			
								
					recPara = JDTORecordFactory.getInstance().create();
					
					
					//YD_STK_COL_GP				
					
					
					szTemp =  ydDaoUtils.paraRecChkNull(inDto[0], "FROMLOC");
					
					if(szTemp.equals("")){
						return "FROM 적치정보가 없습니다!!";
						
					}
					szYD_STK_COL_GP = szTemp.substring(0,6);
					
					
					szToColGp = paraRec.getFieldString("YD_GP") +  paraRec.getFieldString("TO_YD_BAY_GP") +
					paraRec.getFieldString("TO_YD_EQP_GP") +  paraRec.getFieldString("TO_YD_STK_COL_NO") ;
					szToBedNo = ydDaoUtils.paraRecChkNull(paraRec, "TO_YD_STK_BED_NO");
					
					
					
					szTCar = ydDaoUtils.paraRecChkNull(paraRec, "T_CAR");
					
					
					if(szTCar.equals("")){
						szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "YD";
						szSPAN_GP = szYD_STK_COL_GP.substring(2,4);
						
					
						
						if(szSPAN_GP.equals("01") || szSPAN_GP.equals("02")){
							szYD_SCH_CD = szYD_SCH_CD + "01MM";
						} else if(szSPAN_GP.equals("03") || szSPAN_GP.equals("04")){
							szYD_SCH_CD = szYD_SCH_CD + "02MM";
						}  else if(szSPAN_GP.equals("05") || szSPAN_GP.equals("06")){
							szYD_SCH_CD = szYD_SCH_CD + "03MM";
						}  else if(szSPAN_GP.equals("07") || szSPAN_GP.equals("08") || szSPAN_GP.equals("09") ){
							szYD_SCH_CD = szYD_SCH_CD + "04MM";
						} 
						
						//YD_SCH_CD
						
						
					}
					else{
						recPara.setField("YD_WRK_PLAN_TCAR",szTCar );
						szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + szTCar.substring(2,6) + "UM"; 
					}
					
					
					recPara.setField("YD_SCH_CD", szYD_SCH_CD);
					
					
					//YD_STK_COL_GP				
					recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
					
					//REGISTER
					recPara.setField("REGISTER", inDto[0].getFieldString("YD_USER_ID"));
									
					
					//YD_STK_BED_NO
					recPara.setField("YD_STK_BED_NO", szTemp.substring(6,8));
					
					//To 위치 가이드
					szYD_TO_LOC_GUIDE = szToColGp + szToBedNo;
					
					recPara.setField("YD_TO_LOC_GUIDE" , szYD_TO_LOC_GUIDE);
					
					intStlCnt = 0;
					
					
					for(int Loopi = 0; Loopi < inDto.length; Loopi++) {
						//재료번호
						//STL_NO []
						intStlCnt++;
						recPara.setField("STL_NO"+(intStlCnt), ydDaoUtils.paraRecChkNull(inDto[Loopi], "STL_NO"));
						
						//권상 모음순서 
						//YD_UP_COLL_SEQ []
						recPara.setField("YD_UP_COLL_SEQ"+(intStlCnt),""+(intStlCnt));
						
					}

					
					//YD_SH [매수]
					recPara.setField("SLAB_SH", String.valueOf(intStlCnt));
					
					
					ydUtils.displayRecord(szOperationName, recPara);
					
					//내부 Process 연결
					EJBConnector ejbConn = null;
					ejbConn = new EJBConnector("default", this);
					ejbConn.trx("IssueWrkDmdSeEJB", "ydManualReq", recPara);
			
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		
		szMsg = "JSP-SESSION [후판야드 메뉴얼 작업지시 편성] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		
		
		return szRtnMsg;
	}	// end of upBecauseMv

//	/**
//	 * 코일제품창고 사유별이적조회 지시버튼
//	 *
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param inDto
//	 * @return
//	 */
//	public String updCoilBecauseMv(JDTORecord [] inDto, JDTORecord paraRec) {
//		
//		int intSh             = 0;
//		int intStlCnt         = 0;
//		int x                 = 0;
//		String szMsg          = "";
//		String szRtnMsg       = YdConstant.RETN_CD_SUCCESS;
//		String szMethodName   = "updCoilBecauseMv";
//		String szOperationName = "작업지시 생성";
//		String [] strArrStlNo = null;
//		String szYD_STK_COL_GP = "";
//		String szYD_STK_BED_NO = "";
//		String szYD_TO_LOC_GUIDE = "";
//		String szSPAN_GP = "";
//		String szYD_SCH_CD = "";
//		String szJB_GP = "";
//		String szJB_GP_old = "";
//		JDTORecord recPara    = null;
//		
//		String szToBedNo = null;
//		String szToColGp = null;
//		String szTemp = null;
//		String szTCar = null;
//		
//		JDTORecord inRecord       = null;
//		EJBConnector ejbConn = null;
//		JDTORecord outRecord     = JDTORecordFactory.getInstance().create(); // 
//		
//		try {
//			
//			
//			szMsg = "JSP-SESSION [코일야드 메뉴얼 작업지시 편성] 시작";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//			
//			recPara = JDTORecordFactory.getInstance().create();
//			
//			
//			//YD_STK_COL_GP				
//			
//			
//			szTemp =  ydDaoUtils.paraRecChkNull(inDto[0], "FROMLOC");
//			
//			if(szTemp.equals("")){
//				return "FROM 적치정보가 없습니다!!";
//				
//			}
//			szYD_STK_COL_GP = szTemp.substring(0,6);
//			
//			
//			szToColGp = paraRec.getFieldString("YD_GP") +  paraRec.getFieldString("TO_YD_BAY_GP") +
//			paraRec.getFieldString("TO_YD_EQP_GP") +  paraRec.getFieldString("TO_YD_STK_COL_NO") ;
//			szToBedNo = ydDaoUtils.paraRecChkNull(paraRec, "TO_YD_STK_BED_NO");
//			
//			szTCar = ydDaoUtils.paraRecChkNull(paraRec, "T_CAR");
//			
//			if(szTCar.equals("")){
//				szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "YD";
//				szSPAN_GP = szYD_STK_COL_GP.substring(2,4);
//				
//				//동내이적 스케줄을 스판기준으로 나누는 방법
//				if(szSPAN_GP.equals("01") || 
//						szSPAN_GP.equals("02")||
//						szSPAN_GP.equals("03")||
//						szSPAN_GP.equals("04")||
//						szSPAN_GP.equals("05")||
//						szSPAN_GP.equals("06")||
//						szSPAN_GP.equals("07")||
//						szSPAN_GP.equals("08")){
//					szYD_SCH_CD = szYD_SCH_CD + "01MM"; //동내이적
//				} else { 
//					szYD_SCH_CD = szYD_SCH_CD + "02MM"; //동내이적2
//				}   
//				
//				//YD_SCH_CD
//			}
//			else{
//				recPara.setField("YD_WRK_PLAN_TCAR",szTCar );
//				szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + szTCar.substring(2,6) + "UM"; 
//			}
//			//REGISTER
////			recPara.setField("REGISTER", inDto[0].getFieldString("YD_USER_ID"));
//			//To 위치 가이드
//			szYD_TO_LOC_GUIDE = szToColGp + szToBedNo;
//			
//			
//			for(int Loopi = 0; Loopi < inDto.length; Loopi++) {
//				//재료번호
//				//STL_NO []
//				intStlCnt++;
//				recPara.setField("STL_NO"+(intStlCnt), ydDaoUtils.paraRecChkNull(inDto[Loopi], "STL_NO"));
//				
//				//권상 모음순서 
//				//YD_UP_COLL_SEQ []
//				recPara.setField("YD_UP_COLL_SEQ"+(intStlCnt),""+(intStlCnt));
//
//				//재료번호
//				//레코드 생성
//				inRecord = JDTORecordFactory.getInstance().create();
//
//				inRecord.setField("YD_SCH_CD",          		szYD_SCH_CD);//스케줄코드
//				inRecord.setField("STL_SH",      				"1");  //LINE_IN 재료매수
//				inRecord.setField("STL_NO1", 	  				ydDaoUtils.paraRecChkNull(inDto[Loopi], "STL_NO"));
//				inRecord.setField("YD_TO_LOC_DCSN_MTD",			"F");
//						
//				inRecord.setField("TO_YD_STK_BED_NO",			szYD_TO_LOC_GUIDE);
//				inRecord.setField("YD_UP_COLL_SEQ", 			"1");  //권상모음순서
//				//recOutPara.setField("YD_STK_COL_GP",      		szYD_EQP_ID); //적치열구분		
//				//recOutPara.setField("YD_STK_BED_NO",      		szYD_STK_BED_NO); //적치베드번호
//
//				// 작업예약 등록 호출
////대처						this.procWrkBook(recOutPara);
////YD_SCH_CD:스케줄코드,
////STL_SH: 재료매수,
////YD_TO_LOC_DCSN_MTD(TO위치 결정 방법)
////STL_NO(재료번호1,2,3,....)
////FR_YD_STK_BED_NO(적치배드)
////TO_YD_STK_BED_NO(가이드가 됨)
//
//				ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
//				outRecord = (JDTORecord)ejbConn.trx("WrkBookInsertProc", new Class[] { JDTORecord.class }, new Object[] { inRecord });
//				String sRTN_CD		= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
//				String sRTN_MSG		= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
//				if ("0".equals(sRTN_CD)) {
//					szMsg = "작업예약 등록시 ERROR";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//					outRecord.setField("RTN_CD" , "0");	
//					outRecord.setField("RTN_MSG", sRTN_MSG);	
////					return outRecord;
//				
//				}	
//				szMsg = "다수코일  작업예약  완료!";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);					
//				
//				
//				
//			}
//			
//			szMsg = "다수코일  작업예약  완료!";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);					
//			
//		} catch (Exception e) {
//			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
//			throw new DAOException(getClass().getName() + e.getMessage(),e);
//		} finally {
//		}
//		
//		
//		szMsg = "JSP-SESSION [코일야드 메뉴얼 작업지시 편성] 끝";
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//		
//		
//		
//		return szRtnMsg;
//	}	// end of upBecauseMv
//	
	
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
//getYdWrkbook1			
			intRtnVal = ydWrkbookDao.getYdWrkbook(recPara, rsResult, 1);
			//리턴값 메세지처리
			if (intRtnVal > 1) {
				
				szMsg = "작업예약ID 데이터가 중복되었습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == 1) {

				blnRtnVal = true;
				
			} else if (intRtnVal == 0) {
				
				szMsg = "작업예약ID를 구하지 못했습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == -2) {
				
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
	 * 코일제품야드 반납대상 LIST 수정(목표동)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord updCoilYdRetCrnReg(JDTORecord inDto) {
		int       intRtnVal    = 0;
		String    szMsg        = "";
		String    szMethodName = "updCoilYdRetCrnReg";
		String szOperationName = "목표동 수정";
		JDTORecord 			outRecord 		= JDTORecordFactory.getInstance().create(); 
		
		YdUtils ydUtils = new YdUtils();
				
		JDTORecord    recPara  = JDTORecordFactory.getInstance().create();	
		YdStockDao ydStockDao = new YdStockDao();
		
		
		szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

		try {
			
			
			recPara.setField("STL_NO",   		ydDaoUtils.paraRecChkNull(inDto, "STL_NO"));
			recPara.setField("YD_AIM_BAY_GP",   ydDaoUtils.paraRecChkNull(inDto, "YD_AIM_BAY_GP"));
			recPara.setField("YD_AIM_YD_GP",    ydDaoUtils.paraRecChkNull(inDto, "YD_AIM_YD_GP"));
			recPara.setField("MODIFIER",   		ydDaoUtils.paraRecChkNull(inDto, "YD_USER_ID"));
			recPara.setField("NEXT_PROG",   	ydDaoUtils.paraRecChkNull(inDto, "WO_CAR_PLNT_PROC_CD"));
			recPara.setField("WO_CAR_PLNT_PROC_CD",   	ydDaoUtils.paraRecChkNull(inDto, "WO_CAR_PLNT_PROC_CD"));
			recPara.setField("YD_AIM_RT_GP",   	ydDaoUtils.paraRecChkNull(inDto, "YD_AIM_RT_GP"));
			
			ydUtils.displayRecord(szOperationName, recPara);
//updYdStock0				
	        intRtnVal = ydStockDao.updYdStock(recPara,0);
	        
			if (intRtnVal < 1) {
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, "목표동 수정시 Error 발생.");	
				return outRecord;
			} // end of if		
			
			
			intRtnVal = ydStockDao.updPtComm_PROG_CD(recPara, 4);
			if (intRtnVal < 1) {
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, "반납공정 수정시 Error 발생.");	
				return outRecord;
			} // end of if		

			outRecord.setField("RTN_CD" 	, "1");	
			return outRecord;
		
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

	}	// end of updCoilYdRetCrnReg
	
	
	/**
	 * 반송수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord[]
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord updCoilYdRetMgt(JDTORecord[] inDto) throws DAOException {
		int intRtnVal 		= 0;
		String szMsg		= null;
		String szMethodName = null;
		JDTORecordSet 	retRdSet 	= JDTORecordFactory.getInstance().createRecordSet("YD");
		CoilGdsJspDao dao = new CoilGdsJspDao();
		JDTORecord recPara  = JDTORecordFactory.getInstance().create();
		JDTORecord outRec   = JDTORecordFactory.getInstance().create();
		JDTORecord retRrd   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");

		
		szMsg        = "";
		String szSTL_NO = "";
		String szSNDBK_RSN_CD 	= "";
		String szSNDBK_REGISTER = "";
		String szSNDBK_GP		= "";
		String szYD_AIM_BAY_GP		= "";
		String szSNDBK_GP_ETC= "";
		szMethodName = "updCoilYdRetMgt";
			
		
		try {
			
			
			szMsg = "JSP-SESSION [반송 (수정)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			//저장위치 좌표설정화면 BED 수정
			for(int x=0;x<inDto.length;x++){
				
				szSTL_NO 			= yddatautil.setDataDefault(inDto[x].getField("STL_NO"),"");
				szSNDBK_RSN_CD 		= yddatautil.setDataDefault(inDto[x].getField("UPD_SNDBK_RSN_CD"),"");
				szSNDBK_REGISTER 	= yddatautil.setDataDefault(inDto[x].getField("UPD_SNDBK_REGISTER"),"");
				szSNDBK_GP			= yddatautil.setDataDefault(inDto[x].getField("UPD_SNDBK_GP"),"");
				szYD_AIM_BAY_GP		= yddatautil.setDataDefault(inDto[x].getField("UPD_YD_AIM_BAY_GP"),"");
				szSNDBK_GP_ETC		= yddatautil.setDataDefault(inDto[x].getField("SNDBK_GP_ETC"),"");

				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("V_STL_NO"      	, szSTL_NO);
				recPara.setField("V_SNDBK_RSN_CD"   , szSNDBK_RSN_CD);
				recPara.setField("V_SNDBK_REGISTER" , szSNDBK_REGISTER);
				recPara.setField("V_SNDBK_GP"      	, szSNDBK_GP);
				recPara.setField("V_YD_AIM_BAY_GP" 	, szYD_AIM_BAY_GP);
				recPara.setField("V_SNDBK_GP_ETC" 	, szSNDBK_GP_ETC);

				/**com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getCoilYdRetMgt*/
				outRecSet = dao.getCoilYdRetMgt(recPara);
				if(outRecSet == null || outRecSet.size() < 1){
					retRrd.setField("RTN_MSG", "stock read 이상.");
					retRrd.setField("RTN_CD", "0");
					return retRrd;
				}
				outRecSet.first();
				outRec = outRecSet.getRecord();
				String sSNDBK_GP = outRec.getFieldString("SNDBK_GP");
//			    
				if(sSNDBK_GP.equals("")){
					/**com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.updCoilYdRetMgt*/
					intRtnVal = dao.updCoilYdRetMgt(recPara);
					if (intRtnVal <= 0) {
						szMsg = "반송등록시 ERROR 발생 ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						retRrd.setField("RTN_CD" 	, "0");	
						retRrd.setField("RTN_MSG" 	, szMsg);	
						return retRrd;
					}
				} else {
					szMsg = szSTL_NO + "코일번호 반송구분이상:" + sSNDBK_GP  ;
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
		
		szMsg = "JSP-SESSION [반송등록] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return retRrd;
		
	}	// end of updCoilYdRetMgt

	/**
	 * 반송수정기타사항
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord[]
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord updCoilYdRetMgtUpdate(JDTORecord[] inDto) throws DAOException {
		int intRtnVal 		= 0;
		String szMsg		= null;
		String szMethodName = null;
		CoilGdsJspDao dao = new CoilGdsJspDao();
		JDTORecord recPara  = JDTORecordFactory.getInstance().create();
		JDTORecord outRec   = JDTORecordFactory.getInstance().create();
		JDTORecord retRrd   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");

		
		szMsg        = "";
		String szSTL_NO = "";
		String szSNDBK_GP_ETC 	= "";
		
		szMethodName = "updCoilYdRetMgtUpdate";
			
		
		try {
			
			
			szMsg = "JSP-SESSION [반송 (수정)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			//저장위치 좌표설정화면 BED 수정
			for(int x=0;x<inDto.length;x++){
				
				szSTL_NO 			= yddatautil.setDataDefault(inDto[x].getField("STL_NO"),"");
				szSNDBK_GP_ETC 		= yddatautil.setDataDefault(inDto[x].getField("SNDBK_GP_ETC"),"");

				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("V_STL_NO"      	, szSTL_NO);
				recPara.setField("V_SNDBK_GP_ETC"   , szSNDBK_GP_ETC);

				/**com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getCoilYdRetMgt*/
				outRecSet = dao.getCoilYdRetMgt(recPara);
				if(outRecSet == null || outRecSet.size() < 1){
					retRrd.setField("RTN_MSG", "stock read 이상.");
					retRrd.setField("RTN_CD", "0");
					return retRrd;
				}

				/**com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.updCoilYdRetMgtUpdate*/
				intRtnVal = dao.updCoilYdRetMgtUpdate(recPara);
				if (intRtnVal <= 0) {
					szMsg = "반송수정시 ERROR 발생 ";
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
		
		szMsg = "JSP-SESSION [반송수정] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return retRrd;
		
	}	// end of updCoilYdRetMgtUpdate
	
	
	/**
	 * 반송취소
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord[]
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord updCancelCoilYdRetMgt(JDTORecord[] inDto) throws DAOException {
		int intRtnVal 		= 0;
		String szMsg		= null;
		String szMethodName = null;
		JDTORecordSet 	retRdSet 	= JDTORecordFactory.getInstance().createRecordSet("YD");
		CoilGdsJspDao dao = new CoilGdsJspDao();
		JDTORecord recPara  = JDTORecordFactory.getInstance().create();
		JDTORecord outRec   = JDTORecordFactory.getInstance().create();
		JDTORecord retRrd   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");

		
		szMsg        = "";
		String szSTL_NO = "";
		String szSNDBK_RSN_CD 	= "";
		String szSNDBK_REGISTER = "";
		String szSNDBK_GP		= "";
		String szYD_AIM_BAY_GP		= "";
		
		szMethodName = "updCancelCoilYdRetMgt";
			
		
		try {
			
			
			szMsg = "JSP-SESSION [반송 취소] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			//저장위치 좌표설정화면 BED 수정
			for(int x=0;x<inDto.length;x++){
				
				szSTL_NO 			= yddatautil.setDataDefault(inDto[x].getField("STL_NO"),"");
				szSNDBK_RSN_CD 		= yddatautil.setDataDefault(inDto[0].getField("UPD_SNDBK_RSN_CD"),"");
				szSNDBK_REGISTER 	= yddatautil.setDataDefault(inDto[0].getField("UPD_SNDBK_REGISTER"),"");
				szSNDBK_GP			= yddatautil.setDataDefault(inDto[0].getField("UPD_SNDBK_GP"),"");
				
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("V_STL_NO"      	, szSTL_NO);
				recPara.setField("V_SNDBK_RSN_CD"   , "");
				recPara.setField("V_SNDBK_REGISTER" , "");
				recPara.setField("V_SNDBK_GP"      	, "");
				
				/**com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getCoilYdRetMgt*/
				outRecSet = dao.getCoilYdRetMgt(recPara);
				if(outRecSet == null || outRecSet.size() < 1){
					retRrd.setField("RTN_MSG", "STOCK READ 이상.");
					retRrd.setField("RTN_CD", "0");
					return retRrd;
				}
				outRecSet.first();
				outRec = outRecSet.getRecord();
				String sSNDBK_GP = outRec.getFieldString("SNDBK_GP");
				szYD_AIM_BAY_GP  = outRec.getFieldString("YD_AIM_BAY_GP");
				
				recPara.setField("V_YD_AIM_BAY_GP" 	, szYD_AIM_BAY_GP);
//			    
//				if(sSNDBK_GP.equals("")){
					/**com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.updCoilYdRetMgt*/
					intRtnVal = dao.updCoilYdRetMgt(recPara);
					if (intRtnVal <= 0) {
						szMsg = "반송등록시 ERROR 발생 ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						retRrd.setField("RTN_CD" 	, "0");	
						retRrd.setField("RTN_MSG" 	, szMsg);	
						return retRrd;
					}
//				} else {
//					szMsg = szSTL_NO + "코일번호 반송구분이상:" + sSNDBK_GP  ;
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//					retRrd.setField("RTN_CD" 	, "0");	
//					retRrd.setField("RTN_MSG" 	, szMsg);	
//					return retRrd;
//				}			
			}	
			
			retRrd.setField("RTN_CD", "1");
			retRrd.setField("RTN_MSG", "정상 처리 되었습니다.");
			
			
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		szMsg = "JSP-SESSION [반송취소] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return retRrd;
		
	}	// end of updslabYdSchStdMgt
	/**
	 * 반송확정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord[]
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord updCoilYdRetMgt1(JDTORecord[] inDto) throws DAOException {
		int intRtnVal 		= 0;
		String szMsg		= null;
		String szMethodName = null;
		JDTORecordSet 	retRdSet 	= JDTORecordFactory.getInstance().createRecordSet("YD");
		CoilGdsJspDao dao = new CoilGdsJspDao();
		JDTORecord recPara  = JDTORecordFactory.getInstance().create();
		JDTORecord outRec   = JDTORecordFactory.getInstance().create();
		JDTORecord retRrd   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");

		
		szMsg        = "";
		String szSTL_NO = "";
		String szSNDBK_RSN_CD 	= "";
		String szSNDBK_REGISTER = "";
		String szSNDBK_GP		= "";
		String szYD_AIM_BAY_GP		= "";
		String sYD_USER_ID		= "";
		
		szMethodName = "updCoilYdRetMgt1";
			
		
		try {
			
			
			szMsg = "JSP-SESSION [반송 확정] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			//저장위치 좌표설정화면 BED 수정
			for(int x=0;x<inDto.length;x++){
				
				szSTL_NO 			= yddatautil.setDataDefault(inDto[x].getField("STL_NO"),"");
				sYD_USER_ID 		= yddatautil.setDataDefault(inDto[x].getField("YD_USER_ID"),"");
				szSNDBK_RSN_CD 		= yddatautil.setDataDefault(inDto[0].getField("UPD_SNDBK_RSN_CD"),"");
				szSNDBK_REGISTER 	= yddatautil.setDataDefault(inDto[0].getField("UPD_SNDBK_REGISTER"),"");
				szSNDBK_GP			= yddatautil.setDataDefault(inDto[0].getField("UPD_SNDBK_GP"),"");
				
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("V_STL_NO"      	, szSTL_NO);
				recPara.setField("V_SNDBK_GP"      	, "2");
				recPara.setField("V_MODIFIER"      	, sYD_USER_ID);
				
				szMsg = szSTL_NO + "V_MODIFIER:" + sYD_USER_ID  ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				/**com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getCoilYdRetMgt*/
				outRecSet = dao.getCoilYdRetMgt(recPara);
				if(outRecSet == null || outRecSet.size() < 1){
					retRrd.setField("RTN_MSG", "STOCK READ 이상.");
					retRrd.setField("RTN_CD", "0");
					return retRrd;
				}
				outRecSet.first();
				outRec = outRecSet.getRecord();
				String sSNDBK_GP = outRec.getFieldString("SNDBK_GP");
//			    
				if(sSNDBK_GP.equals("1")){
					/**com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.updCoilYdRetMgt1*/
					intRtnVal = dao.updCoilYdRetMgt1(recPara);
					if (intRtnVal <= 0) {
						szMsg = "반송확정시 ERROR 발생 ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						retRrd.setField("RTN_CD" 	, "0");	
						retRrd.setField("RTN_MSG" 	, szMsg);	
						return retRrd;
					}
				} else {
					szMsg = szSTL_NO + "코일번호 반송구분이상:" + sSNDBK_GP  ;
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
		
		szMsg = "JSP-SESSION [반송확정] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return retRrd;
		
	}	// end of updCoilYdRetMgt1
	
	
	/**
	 * 코일야드 크레인상태 조회 (설비 ID 를 받는경우)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilGdsYdCrnStsSetById2(JDTORecord inDto) throws DAOException {
		int intRtnVal      = 0;
		String szRtnMsg = null;
		String szLogMsg = null;
		String szMethodName= "getCoilGdsYdCrnStsSetById2";
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
	}	// end of getCoilGdsYdCrnStsSetById2
	
	
	
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
	public GridData getHrCoilDtlInq(GridData gdReq) throws DAOException {
		try {
			CoilJspDAO dao = new CoilJspDAO();
			JDTORecordSet jrResult = dao.getHrCoilDtlInq(gdReq);
			GridData gdReturn = OperateGridData.cloneResponseGridData(gdReq);

			//args[] - 1 : 리턴할 GridData, 2 : 디비 결과 List, 3 : JSP에서 받은 GridData
			//3번째 아규먼트가 있었을 경우 JSP에서 받은 파라미터를 리턴할 GridData에 그대로 세팅한다.
	        return CmnUtil.jdtoRecordToGridData(gdReturn, jrResult.toList(), gdReq);
	        
	        
		} catch(Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}
	
	/**
	 * 야드관리 > 코일소재야드 > 야드재공관리 > 재료상세정보조회 (PAGE명:열연Coil상세조회)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdStrlocIdInfo(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara    = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet  = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getCoilYdStrlocIdInfo";
	      
		YdStkColDao dao = new YdStkColDao();
		
		int intRtnVal = 0;
		
		try {
			szMsg = "JSP-SESSION [열연Coil상세조회 - 위치변경이력조회]시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
			
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
			
			intRtnVal = dao.getYdStkcol(recPara, outRecSet, 301);
			
			if (intRtnVal <= 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
				//return outRecSet;
				return outRecSet;
			} // end of if
			
			//레코드셋이 없을때까지 반복한다.
			outRecSet.first();			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		szMsg = "JSP-SESSION [열연Coil상세조회 - 위치변경이력조회]끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
		return outRecSet;
	}//end of getCoilYdStrlocIdInfo
	
	/**
	 * 코일제품야드 tracking 팝업 조회보급
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public JDTORecordSet getcoilGdsYdLineWrPp(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getcoilGdsYdLineWrPp";

		
		String szEqpGp 		= "";
		String sSTL_NO 		= "";
		String sWORK_STAT 	= "";
		String szOperationName = "코일제품야드 입고Tracking조회 팝업";
		
		
		//DAO
		YdCrnWrkMtlDao ydCrnWrkMtlDao = new YdCrnWrkMtlDao();
		
		int intRtnVal = 0;
		
		try {
			
			sSTL_NO	= ydDaoUtils.paraRecChkNull(inDto, "COIL_NO");
			
			//if(!sSTL_NO.equals("")){
					
				recPara         = JDTORecordFactory.getInstance().create();
				recPara.setField("LINE"		, inDto.getField("EQP_GP"));	/*동구분*/
				recPara.setField("COIL_NO"	, sSTL_NO);
				recPara.setField("PAGE_NO"	, inDto.getField("PAGE_NO"));
				recPara.setField("ROW_CNT"	, inDto.getField("ROWCOUNT"));
				
//com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getcoilGdsYdLineWrPp2
				intRtnVal = ydCrnWrkMtlDao.getYdCrnwrkmtl(recPara, outRecSet, 401);
				
				if(intRtnVal < 0){
					szMsg = "[JSP Session : "+szOperationName+"]조회 ERROR ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					
				}else if(intRtnVal == 0){
					szMsg = "[JSP Session : "+szOperationName+"]조회된 DATA가 없습니다. ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
					
				}else{
					szMsg = "[JSP Session : "+szOperationName+"] 조회 성공";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
			//}
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	
	}	

	/*---------------------------------------------------------------------------------------------------*/
	/*                                     2기       작업
	/*---------------------------------------------------------------------------------------------------*/
	
	/**
	 * 야드관리 > 코일제품창고 > 저장관리  <제품단위,열단위 이적화면 코일 Display 데이터 조회 >  
	 * 송정현
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public GridData getMtlUnitMvstkReg(GridData inDto) throws DAOException {
		GridData 		rtnGrd 		= new GridData();
		JDTORecord    	recPara   	= JDTORecordFactory.getInstance().create();
		JDTORecordSet 	outRecSet 	= JDTORecordFactory.getInstance().createRecordSet("YD");

		CoilGdsJspDao dao = new CoilGdsJspDao();

		try {
			
			recPara.setField("V_YD_STK_COL_GP",      inDto.getParam("YD_STK_COL_GP"));
			/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getMtlUnitMvstkReg*/
			outRecSet = dao.getMtlUnitMvstkReg(recPara);
			
			if(outRecSet != null){
				rtnGrd = CmUtil.genGridData(inDto, outRecSet);
			}else{
				rtnGrd.setStatus("false");
				rtnGrd.addParam("ret", "-1");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		return rtnGrd;
	}//end of getCoilYdStkLocInfoList		

	/**
	 * 일품단위이적등록1
	 * 송정현
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getMtlUnitMvstkReg1(JDTORecord inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg="";
		String szMethodName="getMtlUnitMvstkReg1";		
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecordSet rSetStock = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecord recPara = JDTORecordFactory.getInstance().create();

		YdStockDao ydStockDao = new YdStockDao();
		String sztemp = "";
		try {
			
			sztemp = yddatautil.setDataDefault(inDto.getField("COIL_NO"), "");
			recPara.setField("COIL_NO"		, sztemp);
			recPara.setField("YD_GP" 			, ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));
			recPara.setField("YD_BAY_GP" 		, ydDaoUtils.paraRecChkNull(inDto, "YD_BAY_GP"));
			recPara.setField("YD_EQP_GP" 		, ydDaoUtils.paraRecChkNull(inDto, "YD_EQP_GP"));
			recPara.setField("YD_STK_COL_NO" 	, ydDaoUtils.paraRecChkNull(inDto, "YD_STK_COL_NO"));
			recPara.setField("WORD_UNIT_NAME" 	, ydDaoUtils.paraRecChkNull(inDto, "WORD_UNIT_NAME"));
			recPara.setField("PAGE_NO" 			, ydDaoUtils.paraRecChkNull(inDto, "PAGE_NO"));
			recPara.setField("ROW_CNT" 			, ydDaoUtils.paraRecChkNull(inDto, "ROWCOUNT"));
			recPara.setField("YD_COIL_OUTDIA_GRP_GP" , ydDaoUtils.paraRecChkNull(inDto, "YD_COIL_OUTDIA_GRP_GP"));
			
			//야드 저장품 테이블에서 품목코드를 읽어온다
			
//getYdStock59	:com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getBecauseMv_PIDEV		
//PIDEV_S :병행가동용:PI_YD
			recPara.setField("PI_YD",   ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));		
			intRtnVal = ydStockDao.getYdStock(recPara, rSetStock, 59);
						
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
		return rSetStock;
	} // end of getBecauseMv
	
	/**
	 * 통합이적등록1
	 * 송정현
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getSpanUnitMvstkReg(JDTORecord inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg="";
		String szMethodName="getSpanUnitMvstkReg";		
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecordSet rSetStock = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecord recPara = JDTORecordFactory.getInstance().create();

		YdStockDao ydStockDao = new YdStockDao();
	
		try {
			
			
			recPara.setField("SEARCH_GP" 			, ydDaoUtils.paraRecChkNull(inDto, "SEARCH_GP"));
			recPara.setField("ORD_NO" 				, ydDaoUtils.paraRecChkNull(inDto, "ORD_NO"));
			recPara.setField("ORD_DTL" 				, ydDaoUtils.paraRecChkNull(inDto, "ORD_DTL"));
			recPara.setField("CUST_CD" 				, ydDaoUtils.paraRecChkNull(inDto, "CUST_CD"));
			recPara.setField("TRANS_ORD_SEQNO" 		, ydDaoUtils.paraRecChkNull(inDto, "TRANS_ORD_SEQNO"));
			recPara.setField("EXPORT_SHIP_SET_NO" 	, ydDaoUtils.paraRecChkNull(inDto, "EXPORT_SHIP_SET_NO"));
			recPara.setField("YD_GP" 				, ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));
			recPara.setField("YD_BAY_GP" 			, ydDaoUtils.paraRecChkNull(inDto, "YD_BAY_GP"));
			recPara.setField("YD_COIL_OUTDIA_GRP_GP", ydDaoUtils.paraRecChkNull(inDto, "YD_COIL_OUTDIA_GRP_GP"));
			recPara.setField("USAGE_CD"				, ydDaoUtils.paraRecChkNull(inDto, "USAGE_CD"));
			recPara.setField("MTL_CHK_FLAG"			, ydDaoUtils.paraRecChkNull(inDto, "MTL_CHK_FLAG"));
			recPara.setField("PO_CHK_FLAG"			, ydDaoUtils.paraRecChkNull(inDto, "PO_CHK_FLAG"));
			recPara.setField("CON_CHK_FLAG"			, ydDaoUtils.paraRecChkNull(inDto, "CON_CHK_FLAG"));
			recPara.setField("PAGE_NO" 				, ydDaoUtils.paraRecChkNull(inDto, "PAGE_NO"));
			recPara.setField("ROW_CNT" 				, ydDaoUtils.paraRecChkNull(inDto, "ROWCOUNT"));
			
			//야드 저장품 테이블에서 품목코드를 읽어온다
//PIDEV_S :병행가동용:PI_YD
			recPara.setField("PI_YD",    	ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));						
			/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getSpanUnitMvstkReg_PIDEV*/
			intRtnVal = ydStockDao.getYdStock(recPara, rSetStock, 500);
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
		return rSetStock;
	} // end of 
	
	
	/**
	 * 메뉴얼 코일 작업지시 편성(ABC)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecord updMtlUnitMvstkReg(JDTORecord[] inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg= null;
		String szMethodName = null;
		String szOperationName = "메뉴얼 코일 작업지시 편성";
		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecord    tempPara = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecordSet outRecSet2 = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecordSet outRecSet3 = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecordSet rsGetStkLyr = null;
		JDTORecord recInTemp      = null;
		JDTORecord recStkLyr		= null;	
		JDTORecord recGetCrnWrkMtl  = null;	
		JDTORecord jrecord = null;
		JDTORecord recBedSet = null;
		JDTORecord inRecord     = null;
		JDTORecord outRecord     = JDTORecordFactory.getInstance().create();
		JDTORecord outRecord1    = JDTORecordFactory.getInstance().create();
		CoilYdToLocDcsnUtil ydToLocDcsnUtil = new CoilYdToLocDcsnUtil();
		String szStkLyrMtlStat = null;
        long lngCoilOutDia         = 0;
        long lngCoilOutDiaR        = 0;
        
        long lngMtlWt              = 0;
        long lngMtlWtR             = 0;
        long lngCrnWrkMtlWt        = 0;
        long lngCrnWrkMtlOutDia    = 0;
        long lngRtnVal    = 0;
        
 
        String rtn_cd  = null;
        String sTAG_STL_NO = "";
        String sBED_STL_NO = "";
        String sBED1_STL_NO = "";
		String sRTN_CD		= "";
		String sRTN_MSG		= "";
		String sRTN_TO_BEDNO	= "XX010101";   
		String szUser = "";
        szMsg        = "";
		szMethodName = "updMtlUnitMvstkReg";
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		YdStkColDao ydStkColDao = new YdStkColDao();
		String sYD_COIL_OUTDIA_GRP_GP = "";
		String szBedNoR = "";
		
		try {
			ydUtils.putLog(szSessionName, szMethodName, "CHECK", YdConstant.DEBUG);
			//저장위치 좌표설정화면 BED 수정
			for(int x=0;x<inDto.length;x++){
				//TC 미정
				recPara   = JDTORecordFactory.getInstance().create();
				
				//YD_SCH_CD
				//YD_WRK_PLAN_TCAR :계획 대차
				
				sTAG_STL_NO = ydDaoUtils.paraRecChkNull(inDto[x], "STL_NO");
				szUser 		= ydDaoUtils.paraRecChkNull(inDto[x], "YD_USER_ID");
				
				String sTO_YD_STK_COL_GP = inDto[x].getFieldString("TO_YD_STK_COL_GP");
				
				recPara.setField("STL_NO", 			sTAG_STL_NO);
				recPara.setField("STL_NO1",       	sTAG_STL_NO);
				recPara.setField("YD_SCH_CD",     	ydDaoUtils.paraRecChkNull(inDto[x], "YD_SCH_CD"));
				recPara.setField("YD_STK_COL_GP", 	ydDaoUtils.paraRecChkNull(inDto[x], "TO_YD_STK_COL_GP"));
				recPara.setField("STL_SH", "1");
				recPara.setField("YD_WRK_PLAN_TCAR", ydDaoUtils.paraRecChkNull(inDto[x], "YD_WRK_PLAN_TCAR"));
				recPara.setField("TO_YD_STK_BED_NO", sTO_YD_STK_COL_GP.substring(1,6)+inDto[x].getField("TO_YD_STK_BED_NO"));
				recPara.setField("REGISTER",        inDto[x].getField("YD_USER_ID"));
				recPara.setField("BED_NO",          inDto[x].getField("TO_YD_STK_BED_NO"));
				recPara.setField("YD_STK_BED_NO",   inDto[x].getField("TO_YD_STK_BED_NO"));
				recPara.setField("YD_TO_LOC_DCSN_MTD",  "F");
								
				// 단에 재료번호가 있는지 조회
				YdStockDao ydStockDao = new YdStockDao();
				/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockCOILCOMM*/
				intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 7);
				if(intRtnVal <= 0) {
					if(intRtnVal == 0) {
						szMsg="이적 재료 정보가 없습니다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, szMsg);	
						return outRecord;

//						return YdConstant.RETN_CD_FAILURE;
					}else{
						szMsg="이적 재료 정보 확인중 Error";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, szMsg);	
						return outRecord;

//						return YdConstant.RETN_CD_FAILURE;
					}
				}
				
				List RecSetTempList0 = outRecSet.toList();
				jrecord = (JDTORecord)RecSetTempList0.get(0);
				
				//추가 데이터 
				//TO Guide 정보

				String szColGp  = ydDaoUtils.paraRecChkNull(inDto[x], "TO_YD_STK_COL_GP");
				String szBedNo  = ydDaoUtils.paraRecChkNull(inDto[x], "TO_YD_STK_BED_NO");
				
				String sYD_BAY_GP = szColGp.substring(1,2);
				//*****************************************************************************************				
// 군별로 외경치구분값 READ		
				intRtnVal  = ydStkColDao.getYdStkcol(recPara, outRecSet2, 0 );
				
				if(outRecSet2 != null && outRecSet2.size()>0){
					sYD_COIL_OUTDIA_GRP_GP = outRecSet2.getRecord(0).getFieldString("YD_COIL_OUTDIA_GRP_GP");
				} else {
					szMsg="적치 BED의 외경군이 없습니다. Error";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
					
				}
//ABC			
				if(sYD_BAY_GP.equals("A")||sYD_BAY_GP.equals("B")||sYD_BAY_GP.equals("C")){
					szBedNoR = YdUtils.fillSpZr(Integer.parseInt(szBedNo) + 1 + "", 2, 0);
				} else {
					
					if(sYD_COIL_OUTDIA_GRP_GP.equals("A")) {
	
						szBedNoR = YdUtils.fillSpZr(Integer.parseInt(szBedNo) + 2 + "", 2, 0);
						
					} else if(sYD_COIL_OUTDIA_GRP_GP.equals("B")){
						
						szBedNoR = YdUtils.fillSpZr(Integer.parseInt(szBedNo) + 3 + "", 2, 0);
						
					} else if(sYD_COIL_OUTDIA_GRP_GP.equals("C")){
						
						szBedNoR = YdUtils.fillSpZr(Integer.parseInt(szBedNo) + 4 + "", 2, 0);
						
					} else {
						
						
						szMsg="적치 BED의 외경군이 이상합니다. 외경군: " +  sYD_COIL_OUTDIA_GRP_GP;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, szMsg);	
						return outRecord;
					}
				
				}	
				//검색된 Bed의 1단 2단 정보와 검색된Bed의 오른쪽 Bed의 1단의 정보를 조회한다.
				rsGetStkLyr = JDTORecordFactory.getInstance().createRecordSet("Temp");
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_STK_COL_GP", szColGp);
				recInTemp.setField("YD_STK_BED_NO1", szBedNo);
				recInTemp.setField("YD_STK_LYR_NO1", "002");
				recInTemp.setField("YD_STK_BED_NO2", szBedNo);
				recInTemp.setField("YD_STK_BED_NO_R", szBedNoR);
				recInTemp.setField("YD_STK_LYR_NO2", "001");

				/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrYdStockColGpBedNoLyrNoIN*/
				intRtnVal = ydStkLyrDao.getYdStklyr(recInTemp, rsGetStkLyr, 31);
				if(intRtnVal <= 0) {
					if(intRtnVal == 0) {
						szMsg="조회된 적치단 정보가 없습니다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, szMsg);	
						return outRecord;

					}else{
						szMsg="적치단 정보 조회중 Error";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, szMsg);	
						return outRecord;

					}
				}
				
				ydUtils.putLog(szSessionName, szMethodName, ""+intRtnVal, YdConstant.INFO);
				if (intRtnVal == 3){
					//검색된 Bed의 1단의 적치단재료상태 Check
					rsGetStkLyr.absolute(2);
					recStkLyr = JDTORecordFactory.getInstance().create();
					recStkLyr.setRecord(rsGetStkLyr.getRecord());
					szStkLyrMtlStat = ydDaoUtils.paraRecChkNull(recStkLyr,"YD_STK_LYR_MTL_STAT");
					sBED_STL_NO 	= ydDaoUtils.paraRecChkNull(recStkLyr,"STL_NO");
					
					if((sBED_STL_NO.equals("")) && (szStkLyrMtlStat.equals("E"))){
						//1단 check
						inRecord = JDTORecordFactory.getInstance().create();
						inRecord.setField("YD_STK_COL_GP" 	, ydDaoUtils.paraRecChkNull(inDto[x], "TO_YD_STK_COL_GP"));	
						inRecord.setField("YD_STK_BED_NO" 	, ydDaoUtils.paraRecChkNull(inDto[x], "TO_YD_STK_BED_NO"));	
						inRecord.setField("YD_STK_LYR_NO" 	, "001");	
							
						outRecord1 = ydToLocDcsnUtil.CoilGdsLyrBaseCheck(sTAG_STL_NO, inRecord);
						sRTN_CD			= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
						sRTN_MSG		= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
						sRTN_TO_BEDNO	= StringHelper.evl(outRecord1.getFieldString("RTN_TO_BEDNO"), "");
						if (!("1".equals(sRTN_CD))) {
							outRecord.setField("RTN_CD" 	, "0");	
							outRecord.setField("RTN_MSG" 	, sRTN_MSG);	
							return outRecord;
						}
						
					} else {
						//2단 check
						rsGetStkLyr.absolute(1);
						recStkLyr = JDTORecordFactory.getInstance().create();
						recStkLyr.setRecord(rsGetStkLyr.getRecord());
						sBED1_STL_NO = ydDaoUtils.paraRecChkNull(recStkLyr, "STL_NO");
						String szStkLyrMtlStat1 = ydDaoUtils.paraRecChkNull(recStkLyr,"YD_STK_LYR_MTL_STAT");
						if((sBED1_STL_NO.equals("")) && (szStkLyrMtlStat1.equals("E"))){
							//2단 check
							inRecord = JDTORecordFactory.getInstance().create();
							inRecord.setField("YD_STK_COL_GP" 	, ydDaoUtils.paraRecChkNull(inDto[x], "TO_YD_STK_COL_GP"));	
							inRecord.setField("YD_STK_BED_NO" 	, ydDaoUtils.paraRecChkNull(inDto[x], "TO_YD_STK_BED_NO"));	
							inRecord.setField("YD_STK_LYR_NO" 	, "002");	
								
							outRecord1 = ydToLocDcsnUtil.CoilGdsLyrBaseCheck(sTAG_STL_NO, inRecord);
							sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
							sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
							sRTN_TO_BEDNO	= StringHelper.evl(outRecord1.getFieldString("RTN_TO_BEDNO"), "");
							if (!("1".equals(sRTN_CD))) {
								outRecord.setField("RTN_CD" 	, "0");	
								outRecord.setField("RTN_MSG" 	, sRTN_MSG);	
								return outRecord;
							}	
						}else {
							outRecord.setField("RTN_CD" 	, "0");	
							outRecord.setField("RTN_MSG" 	, "TO위치로 예정되어 있거나 제품이 존재하는 위치입니다.확인요망");	
							return outRecord;
						}
					}
				} else {
					//검색된 Bed의 1단의 적치단재료상태 Check
					rsGetStkLyr.absolute(1);
					recStkLyr = JDTORecordFactory.getInstance().create();
					recStkLyr.setRecord(rsGetStkLyr.getRecord());
					szStkLyrMtlStat = ydDaoUtils.paraRecChkNull(recStkLyr,"YD_STK_LYR_MTL_STAT");
					sBED_STL_NO = ydDaoUtils.paraRecChkNull(recStkLyr,"STL_NO");
					
					if((sBED_STL_NO.equals("")) && (szStkLyrMtlStat.equals("E"))){
						//1단 check
						inRecord = JDTORecordFactory.getInstance().create();
						inRecord.setField("YD_STK_COL_GP" 	, ydDaoUtils.paraRecChkNull(inDto[x], "TO_YD_STK_COL_GP"));	
						inRecord.setField("YD_STK_BED_NO" 	, ydDaoUtils.paraRecChkNull(inDto[x], "TO_YD_STK_BED_NO"));	
						inRecord.setField("YD_STK_LYR_NO" 	, "001");	
							
						outRecord1 = ydToLocDcsnUtil.CoilGdsLyrBaseCheck(sTAG_STL_NO, inRecord);
						sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
						sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
						sRTN_TO_BEDNO	= StringHelper.evl(outRecord1.getFieldString("RTN_TO_BEDNO"), "");
						
						if (!("1".equals(sRTN_CD))) {
							outRecord.setField("RTN_CD" 	, "0");	
							outRecord.setField("RTN_MSG" 	, sRTN_MSG);	
							return outRecord;
						}
					}else {
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, "TO위치로 예정되어 있거나 제품이 존재하는 위치입니다.확인요망");	
						return outRecord;
					}	
				}
				ydUtils.displayRecord(szOperationName, recPara);
				
			}
			
			recPara.setField("TO_YD_STK_BED_NO"	, sRTN_TO_BEDNO );
			recPara.setField("YD_USER_ID"		, szUser );
			recPara.setField("RTN_CD" 			, "1");	
			return recPara;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}	// end of updMtlUnitMvstkReg
	
	/**
	 * 야드관리 > 코일소재야드 > 크레인실적관리 > 스판단위이적등록 (이적가능 Count, 예약 Count조회)
	 * @ejb.interface-method
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @작성자 :박지열
	 * @작성일 : 2010.04.22
	 */
	public GridData getToDongUseCount(GridData inDto) throws DAOException {
		
		JDTORecordSet 	retRdSet 	= JDTORecordFactory.getInstance().createRecordSet("YD");
		CoilJspDAO 		dao 		= new CoilJspDAO();
		GridData 		retGrid 	= new GridData();
		JDTORecord 		recPara		= null;
		try {
			
			recPara		= JDTORecordFactory.getInstance().create(); 
			
			recPara.setField("V_YD_STK_COL_GP1"	, inDto.getParam("V_YD_STK_COL_GP"));
			recPara.setField("V_YD_STK_COL_GP2"	, inDto.getParam("V_YD_STK_COL_GP"));
			
			retRdSet = dao.getToDongUseCount(recPara);
						
			if (retRdSet != null) {
				retGrid = CmUtil.genGridData(inDto , retRdSet);
				retGrid.addParam("ret", "0");
				retGrid.setStatus("true");
			} else{
				retGrid.addParam("ret", "-1");
				retGrid.setStatus("false");
			}
					
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return retGrid;
	} // end of getBecauseMv
	
	
	
	/**
	 * 야드관리 > 코일제품창고 > 기준관리 > 스케줄기준관리 조회 (화면:스케줄기준관리)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.05.31
	 */
	public JDTORecordSet getSchRuleMgtList(JDTORecord inDto) throws DAOException {

		int    intRtnVal    = 0;
		String szMsg        = "";
		CoilGdsJspDao dao   = new CoilGdsJspDao();
		String szMethodName = "getSchRuleMgtList";		
		JDTORecordSet outRecSet = null;
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		
		try {
			szMsg = "JSP-SESSION [스케줄기준 조회 (화면:스케줄기준관리)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			recPara.setField("V_YD_GP",    		inDto.getField("YD_GP"));
			recPara.setField("V_YD_BAY_GP", 	inDto.getField("YD_BAY_GP"));
			recPara.setField("V_YD_SCH_CD", 	yddatautil.setDataDefault(inDto.getField("YD_SCH_CD"),   ""));
			recPara.setField("V_YD_EQP_ID", 	yddatautil.setDataDefault(inDto.getField("YD_EQP_ID"),   ""));
			//recPara.setField("V_YD_EQP_ID2", 	yddatautil.setDataDefault(inDto.getField("YD_EQP_ID"),   ""));
			recPara.setField("V_CD_CONTENTS", 	yddatautil.setDataDefault(inDto.getField("CD_CONTENTS"), ""));
			recPara.setField("V_PAGE_CNT1",   	inDto.getField("PAGE_NO"));
			recPara.setField("V_PAGE_CNT2",   	inDto.getField("PAGE_NO"));
			recPara.setField("V_ROW_CNT1",    	inDto.getField("PAGE_SIZE"));
			recPara.setField("V_ROW_CNT2",    	inDto.getField("PAGE_SIZE"));			
			
			outRecSet = dao.getSchRuleMgtList(recPara);
			
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
		szMsg = "JSP-SESSION [스케줄기준 조회 (화면:스케줄기준관리)] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return outRecSet;
	}
	
	
	/**
	 * 야드관리 > 코일제품창고 > 기준관리 > 스케줄기준관리 (수정)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord[]
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord updSchRuleMgt(JDTORecord[] inDto) throws DAOException {
		int intRtnVal 		= 0;
		String szMsg		= null;
		String szMethodName = null;
		CoilGdsJspDao dao   = new CoilGdsJspDao();
		JDTORecord recPara  = JDTORecordFactory.getInstance().create();
		JDTORecord outRec   = JDTORecordFactory.getInstance().create();
		JDTORecord retRrd   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = null;

		
		szMsg        = "";
		szMethodName = "updSchRuleMgt";
			
		
		try {
			
			
			szMsg = "JSP-SESSION [스케줄 기준관리 (수정)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			//저장위치 좌표설정화면 BED 수정
			for(int x=0;x<inDto.length;x++){
				
				
				recPara.setField("V_YD_SCH_CD", 	inDto[x].getField("YD_SCH_CD"));
				outRecSet = dao.getSchruleRuleInfo(recPara);
				
				if(outRecSet == null || outRecSet.size() < 1){
					retRrd.setField("RTN_MSG", "스케줄 기준관리 수정정보 조회시 오류가 발생 하였습니다.");
					retRrd.setField("RTN_CD", "0");
					return retRrd;
				}
				outRecSet.first();
				outRec = outRecSet.getRecord();
 
//			      
				recPara.setField("V_MODIFIER",				inDto[x].getFieldString("YD_USER_ID")); //수정자
				recPara.setField("V_YD_SCH_RNG_CD", 		StringHelper.evl(inDto[x].getFieldString("YD_SCH_RNG_CD"),outRec.getFieldString("YD_SCH_RNG_CD"))); //스케줄범위코드
				recPara.setField("V_YD_SCH_WHIO_GP", 		StringHelper.evl(inDto[x].getFieldString("YD_SCH_WHIO_GP"),outRec.getFieldString("YD_SCH_WHIO_GP"))); // 스케줄입출고구분
				recPara.setField("V_YD_SCH_DIV_GP", 		StringHelper.evl(inDto[x].getFieldString("YD_SCH_DIV_GP"),outRec.getFieldString("YD_SCH_DIV_GP"))); // 스케줄분할구분
				recPara.setField("V_YD_SCH_RULE_ACT_STAT", 	StringHelper.evl(inDto[x].getFieldString("YD_SCH_RULE_ACT_STAT"),outRec.getFieldString("YD_SCH_RULE_ACT_STAT"))); // 스케줄기준활성상태
				recPara.setField("V_YD_WRK_CRN", 			StringHelper.evl(inDto[x].getFieldString("YD_WRK_CRN"),outRec.getFieldString("YD_WRK_CRN"))); // 작업크레인
				recPara.setField("V_YD_WRK_CRN_PRIOR", 		StringHelper.evl(inDto[x].getFieldString("YD_WRK_CRN_PRIOR"),outRec.getFieldString("YD_WRK_CRN_PRIOR"))); //작업크레인우선순위
				recPara.setField("V_YD_ALT_CRN_YN", 		StringHelper.evl(inDto[x].getFieldString("YD_ALT_CRN_YN"),outRec.getFieldString("YD_ALT_CRN_YN"))); // 대체크레인유무
				recPara.setField("V_YD_ALT_CRN", 			StringHelper.evl(inDto[x].getFieldString("YD_ALT_CRN"),outRec.getFieldString("YD_ALT_CRN"))); // 야드대체크레인
				recPara.setField("V_YD_ALT_CRN_PRIOR", 		StringHelper.evl(inDto[x].getFieldString("YD_ALT_CRN_PRIOR"),outRec.getFieldString("YD_ALT_CRN_PRIOR"))); // 대체크레인우선순위
				recPara.setField("V_CD_CONTENTS", 			StringHelper.evl(inDto[x].getFieldString("CD_CONTENTS"),outRec.getFieldString("CD_CONTENTS"))); // 스케쥴명
				recPara.setField("V_YD_SCH_PROH_EXN", 		StringHelper.evl(inDto[x].getFieldString("YD_SCH_PROH_EXN"),outRec.getFieldString("YD_SCH_PROH_EXN"))); // 야드스케줄금지유무
				recPara.setField("V_YD_SCH_CD", 			inDto[x].getField("YD_SCH_CD"));  // 스케줄코드                    

				
				intRtnVal = dao.updSchruleRuleInfo(recPara);
				
				if (intRtnVal < 1) {
					retRrd.setField("RTN_MSG", "스케줄 기준관리 수정중 오류가 발생 하였습니다.");
					retRrd.setField("RTN_CD", "0");
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
		
		szMsg = "JSP-SESSION [스케줄 기준관리 (수정)] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return retRrd;
		
	}	// end of updslabYdSchStdMgt
	
	
	
	/**
	 * 야드관리 > 코일제품창고 > 설비관리 > 대차스케줄관리  --> 대차상태설정팝업조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일  : 2010.06.10
	 */
	public JDTORecordSet getCoilYdTcarStsSet(JDTORecord inDto) throws DAOException {

		String szRtnMsg = null;
		String szLogMsg = null;
		String szMethodName = "getCoilYdTcarStsSet";
		String szYD_EQP_ID = null;
		String szOperationName = "대차설비 조회";
		String szMsg ="";

		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");

		CoilGdsJspDao dao = new CoilGdsJspDao();

		try {
			
			szYD_EQP_ID = yddatautil.setDataDefault(inDto.getField("YD_EQP_ID"), "");

			szLogMsg = "[JSP Session : "+szOperationName+ "]대차 : " +  szYD_EQP_ID;
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);

			// 설비ID
			recPara.setField("V_YD_EQP_ID", szYD_EQP_ID);
			outRecSet = dao.getCoilYdTcarStsSet(recPara); 

			if (outRecSet == null || outRecSet.size() < 1) {
				
				szMsg = "Search Warning!!!, No Search Data " ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
				return outRecSet;
				
			 } // end of if
			
		} catch (Exception e) {
			e.printStackTrace();
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
			throw new DAOException(szRtnMsg);
		} 

		return outRecSet;
	} // end of getCoilYdTcarStsSet
	
	
	
	/**
	 * 야드관리 > 코일제품창고 > 설비관리 > 대차스케줄관리  --> 대차상태설정 수정(설비 고장/정상 설정)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord[]
	 * @return String[]
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.10
	 */
	public JDTORecord updCoilYdTcarStsSet(JDTORecord[] inDto) throws DAOException {
		int intRtnVal = 0;
		String szLogMsg = null;
		String[] szRtnMsg = null;
		JDTORecord 			outRecord 		= JDTORecordFactory.getInstance().create(); // 
		String szMethodName="updCoilYdTcarStsSet";
		String szYD_EQP_ID = null;
		String szYD_EQP_STAT = null;
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		
		JDTORecord recEqpInfo = JDTORecordFactory.getInstance().create();
		JDTORecordSet rsEqpInfo = JDTORecordFactory.getInstance().createRecordSet("");
		
		
		CoilGdsJspDao dao = new CoilGdsJspDao();	
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
				szRtnMsg[x] = YdConstant.RETN_CD_SUCCESS;
				if(szYD_EQP_ID.equals("")){
					szLogMsg = "[" + x + "] 설비ID값이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);	
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szLogMsg);	
					return outRecord;

				}
				
				szYD_EQP_STAT = yddatautil.setDataDefault(inDto[x].getField("YD_EQP_STAT"), "");
				if(szYD_EQP_STAT.equals("")){
					szLogMsg = "[" + x + "] 설비상태 값이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);					
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szLogMsg);	
					return outRecord;

				}
				
				recPara.setField("MSG_ID"   , "YD_JSP");
				recPara.setField("YD_EQP_ID", szYD_EQP_ID); //야드설비ID


				//야드설비휴지코드
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
			
				//intRtnVal = ydEqpDao.getYdEqp(recPara, rsEqpInfo, 0);
				//파라미터 설정 
				recEqpInfo.setField("V_YD_EQP_ID", szYD_EQP_ID);
				// 설비정보 조회
				rsEqpInfo = dao.getEqpInfo(recEqpInfo);
				
				
				if(rsEqpInfo == null ){
					szMsg = "[JSP Session : "+szOperationName+"] 해당 설비 조회시 ERROR";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					
					szRtnMsg[x] = "해당 설비정보 조회시 ERROR";
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;

					
					
				}else if(rsEqpInfo.size() == 0){
					szMsg = "[JSP Session : "+szOperationName+"] 해당 설비가 존재하지 않습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					
					szRtnMsg[x] = "해당 설비정보가 존재하지 않습니다.";
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
					
				}
				
				rsEqpInfo.first();
				recEqpInfo = rsEqpInfo.getRecord();
			
				szYD_EQP_STAT_Comp = ydDaoUtils.paraRecChkNull(recEqpInfo, "YD_EQP_STAT");
				
				
				if(szYD_EQP_STAT_Comp.equals(YdConstant.YD_EQP_STAT_BREAK) && szYD_EQP_STAT_Temp.equals(YdConstant.YD_EQP_STAT_BREAK)){
					szMsg = "[JSP Session : "+szOperationName+"] 변경된 내용이 없습니다. ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					szRtnMsg[x] = "변경된 내용이 없습니다.";
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
					
				}else if( (!szYD_EQP_STAT_Comp.equals(YdConstant.YD_EQP_STAT_BREAK) ) && ( !szYD_EQP_STAT_Temp.equals(YdConstant.YD_EQP_STAT_BREAK)) ){
					szMsg = "[JSP Session : "+szOperationName+"] 변경된 내용이 없습니다. ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					szRtnMsg[x] =  "변경된 내용이 없습니다.";
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
				}
				
				
				//------------------------------------------------------------------------------------------------
				
				
				recPara.setField("YD_EQP_STAT"        , szYD_EQP_STAT_Temp); // 야드설비상태
				recPara.setField("YD_EQP_TRBL_RCVR_DT", YdUtils.getCurDate("yyyyMMddHHmmss")); //야드설비고장복구일시
				

				//------------------------------------------------------------------------------------------------
				// 복구 리스케줄 호출
				// C열연 코일야드   L2 : Y5YDL004 rcvY5EqpTrblRcvrWr  EqpTrackingFaEJB
				//------------------------------------------------------------------------------------------------
				
				
				//야드구분
				szYdGp = szYD_EQP_ID.substring(0,1);
				
				
				szLogMsg = "[JSP SESSION -  (야드구분  :  " + szYdGp +") ] 입니다";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
//				if( szYdGp.equals(YdConstant.YD_GP_C_HR_COIL_GDS_YARD) || szYdGp.equals(YdConstant.YD_GP_C_HR_COIL_MATL_YARD)){
					szRcvTcCode  = "Y5YDL004";
					szEjbConName = "procY5EqpTrblRcvrWr";
					
					
					szLogMsg = "[JSP SESSION -  (복구리스케줄     "+ szEjbConName +")을 호출  시작";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);

					
					//해당 메소드는 void 형이라 리턴이 없습니다.
					
					recPara.setField("JMS_TC_CD", szRcvTcCode);
				   	ejbConn = new EJBConnector("default", this);				
				 	ejbConn.trx("EqpTrackingSeEJB", szEjbConName, recPara);
				
					
					szLogMsg = "[JSP SESSION - (복구리스케줄     "+ szEjbConName +")을 호출  끝";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);

			}//end for
			
			outRecord.setField("RTN_CD" 	, "1");	
			return outRecord;			
		} catch (Exception e) {
			e.printStackTrace();
			
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
		}
	}	// end of updCoilYdTcarStsSet
	
	
	/**
	 * 야드관리 > 코일제품창고 > 설비관리 > 대차스케줄관리  --> 입고대차 설정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord[]
	 * @return String[]
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.10
	 */
	public JDTORecord updCoilYdTcarStsSetRcpt(JDTORecord[] inDto) throws DAOException {
		int intRtnVal = 0;
		String szLogMsg = null;
		String[] szRtnMsg = null;
	
		String szMethodName="updCoilYdTcarStsSetRcpt";
		String szYD_EQP_ID = null;
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		
		JDTORecord recEqpInfo = JDTORecordFactory.getInstance().create();
		JDTORecordSet rsEqpInfo = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord 			outRecord 		= JDTORecordFactory.getInstance().create(); // 
		
		CoilGdsJspDao dao = new CoilGdsJspDao();	
		YdEqpDao ydEqpDao = new YdEqpDao();
		szRtnMsg = new String[inDto.length];
		
	
		String szOperationName = "입고대차 설정";
		String szMsg = "";
		
		String szRCPT_TCAR_USE_YN     = "";
		String szRCPT_TCAR_BAY 		= "";
		String szRCPT_TCAR_AIM_BAY_GP  = "";
		
		String szYD_GP				 	= "";
		String szYD_TCAR_WRK_ABLE_BAY1 	= "";
		String szYD_TCAR_WRK_ABLE_BAY2 	= "";
		String szYD_TCAR_WRK_ABLE_BAY3 	= "";
		String szYD_TCAR_WRK_ABLE_BAY4 	= "";
		String szYD_TCAR_WRK_ABLE_BAY5 	= "";
		String szYD_TCAR_WRK_ABLE_BAY6 	= "";
		String szYD_TCAR_WRK_ABLE_BAY7 	= "";
		String szYD_TCAR_WRK_ABLE_BAY8 	= "";
		String szUser 			= "";
		
		try {
			
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			//저장위치 
			for(int x=0;x<inDto.length;x++){
				szYD_EQP_ID = yddatautil.setDataDefault(inDto[x].getField("YD_EQP_ID"), "");
				szUser 		= ydDaoUtils.paraRecChkNull(inDto[x], "YD_USER_ID");
				
				if(szYD_EQP_ID.equals("")){
					szLogMsg = "설비ID값이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);	
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szLogMsg);	
					return outRecord;
				}
		
				szRCPT_TCAR_USE_YN      = yddatautil.setDataDefault(inDto[x].getField("RCPT_TCAR_USE_YN"), "");
				if(szRCPT_TCAR_USE_YN.equals("")){
					szLogMsg = "입고대차 지정여부 값이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);					
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szLogMsg);	
					return outRecord;
				}

				szRCPT_TCAR_BAY 		= yddatautil.setDataDefault(inDto[x].getField("RCPT_TCAR_BAY"), "");
				if(szRCPT_TCAR_BAY.equals("")){
					szLogMsg = "입고동 값이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);					
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szLogMsg);	
					return outRecord;
	
				}

				
				szRCPT_TCAR_AIM_BAY_GP  = yddatautil.setDataDefault(inDto[x].getField("RCPT_TCAR_AIM_BAY_GP"), "");
				if(szRCPT_TCAR_AIM_BAY_GP.equals("")){
					szLogMsg = "목적동 값이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);					
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szLogMsg);	
					return outRecord;
				}
				

				//파라미터 설정 
				recEqpInfo.setField("V_YD_EQP_ID", szYD_EQP_ID);
				// 설비정보 조회
				rsEqpInfo = dao.getEqpInfo(recEqpInfo);
				
				
				if(rsEqpInfo == null ){
					szMsg = "[JSP Session : "+szOperationName+"] 해당 설비 조회시 ERROR";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					
					szRtnMsg[x] = "해당 설비정보 조회시 ERROR";
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
				
					
				}else if(rsEqpInfo.size() == 0){
					szMsg = "[JSP Session : "+szOperationName+"] 해당 설비가 존재하지 않습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					
					szRtnMsg[x] = "해당 설비정보가 존재하지 않습니다.";
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
					
				}
				
				rsEqpInfo.first();
				recEqpInfo = rsEqpInfo.getRecord();
			
				szYD_GP				 = ydDaoUtils.paraRecChkNull(recEqpInfo, "YD_GP");
				szYD_TCAR_WRK_ABLE_BAY1 = ydDaoUtils.paraRecChkNull(recEqpInfo, "YD_TCAR_WRK_ABLE_BAY1");
				szYD_TCAR_WRK_ABLE_BAY2 = ydDaoUtils.paraRecChkNull(recEqpInfo, "YD_TCAR_WRK_ABLE_BAY2");
				szYD_TCAR_WRK_ABLE_BAY3 = ydDaoUtils.paraRecChkNull(recEqpInfo, "YD_TCAR_WRK_ABLE_BAY3");
				szYD_TCAR_WRK_ABLE_BAY4 = ydDaoUtils.paraRecChkNull(recEqpInfo, "YD_TCAR_WRK_ABLE_BAY4");
				szYD_TCAR_WRK_ABLE_BAY5 = ydDaoUtils.paraRecChkNull(recEqpInfo, "YD_TCAR_WRK_ABLE_BAY5");
				szYD_TCAR_WRK_ABLE_BAY6 = ydDaoUtils.paraRecChkNull(recEqpInfo, "YD_TCAR_WRK_ABLE_BAY6");
				szYD_TCAR_WRK_ABLE_BAY7 = ydDaoUtils.paraRecChkNull(recEqpInfo, "YD_TCAR_WRK_ABLE_BAY7");
				szYD_TCAR_WRK_ABLE_BAY8 = ydDaoUtils.paraRecChkNull(recEqpInfo, "YD_TCAR_WRK_ABLE_BAY8");
				if(szYD_GP.equals("H")){ 
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, "소재창고에서 사용중입니다.");	
					return outRecord;
				
				}

				if(szRCPT_TCAR_USE_YN.equals("Y")){ 
					if(szRCPT_TCAR_BAY.equals(szYD_TCAR_WRK_ABLE_BAY1) 
							 || szRCPT_TCAR_BAY.equals(szYD_TCAR_WRK_ABLE_BAY2) 
							 || szRCPT_TCAR_BAY.equals(szYD_TCAR_WRK_ABLE_BAY3) 
							 || szRCPT_TCAR_BAY.equals(szYD_TCAR_WRK_ABLE_BAY4) 
							 || szRCPT_TCAR_BAY.equals(szYD_TCAR_WRK_ABLE_BAY5)
							 || szRCPT_TCAR_BAY.equals(szYD_TCAR_WRK_ABLE_BAY6)
							 || szRCPT_TCAR_BAY.equals(szYD_TCAR_WRK_ABLE_BAY7)
							 || szRCPT_TCAR_BAY.equals(szYD_TCAR_WRK_ABLE_BAY8)
							 ){ 
						
					} else {
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, "입고동이 대차 영역이 아닙니다..");	
						return outRecord;
					}	
					if(szRCPT_TCAR_AIM_BAY_GP.equals(szYD_TCAR_WRK_ABLE_BAY1) 
							 || szRCPT_TCAR_AIM_BAY_GP.equals(szYD_TCAR_WRK_ABLE_BAY2) 
							 || szRCPT_TCAR_AIM_BAY_GP.equals(szYD_TCAR_WRK_ABLE_BAY3) 
							 || szRCPT_TCAR_AIM_BAY_GP.equals(szYD_TCAR_WRK_ABLE_BAY4) 
							 || szRCPT_TCAR_AIM_BAY_GP.equals(szYD_TCAR_WRK_ABLE_BAY5)
							 || szRCPT_TCAR_AIM_BAY_GP.equals(szYD_TCAR_WRK_ABLE_BAY6)
							 || szRCPT_TCAR_AIM_BAY_GP.equals(szYD_TCAR_WRK_ABLE_BAY7)
							 || szRCPT_TCAR_AIM_BAY_GP.equals(szYD_TCAR_WRK_ABLE_BAY8)
							 ){ 
						
					} else {
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, "목적동이 대차 영역이 아닙니다..");	
						return outRecord;
					}	
				} else {
					szRCPT_TCAR_BAY = "";
					szRCPT_TCAR_AIM_BAY_GP = "";
				}
				
				recPara.setField("YD_EQP_ID"			, szYD_EQP_ID);
				recPara.setField("RCPT_TCAR_USE_YN" 	, szRCPT_TCAR_USE_YN);	
				recPara.setField("RCPT_TCAR_BAY" 		, szRCPT_TCAR_BAY);	
				recPara.setField("RCPT_TCAR_AIM_BAY_GP" , szRCPT_TCAR_AIM_BAY_GP);	
				recPara.setField("MODIFIER" 			, szUser);	
				
				ydUtils.putLog(szSessionName, szMethodName, "szUser" + szUser, YdConstant.DEBUG);
				intRtnVal = ydEqpDao.updYdEqpDirect(recPara, 300);
				
				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szLogMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					} else {
						szLogMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					}
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, "장비 등록시 ERROR 발생하였습니다..");	
					return outRecord;
				} // end of if	
				
			}//end for

			outRecord.setField("RTN_CD" 	, "1");	
			return outRecord;
			
		} catch (Exception e) {
			e.printStackTrace();
			
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
		}
		
		

	}	// end of updCoilYdTcarStsSet
	

	/**
	 * 야드관리 > 코일제품창고 > 설비관리 > 대차스케줄관리  --> 대차 상태 초기화
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord[]
	 * @return String[]
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.10
	 */
	public JDTORecord updCoilYdTcarClear(JDTORecord[] inDto) throws DAOException {
		
		int intRtnVal = 0;
		String szLogMsg = null;
		String[] szRtnMsg = null;
	
		String szMethodName="updCoilYdTcarClear";
		String szYD_EQP_ID = null;
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		
		JDTORecord 		inRecord1 	= JDTORecordFactory.getInstance().create();
		JDTORecord 		outRecord1 	= JDTORecordFactory.getInstance().create();
		JDTORecordSet 	outRecordSet= JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord 		outRecord	= JDTORecordFactory.getInstance().create(); // 
		
		CoilGdsJspDao dao = new CoilGdsJspDao();
		YdTcarFtmvMtlDao ydTcarftmvmtlDao = new YdTcarFtmvMtlDao();
		YdTcarSchDao     ydTcarSchDao     = new YdTcarSchDao();
		YdEqpDao ydEqpDao   = new YdEqpDao();
		
		szRtnMsg = new String[inDto.length];
		
	
		String szOperationName = "대차상태 초기화";
		String szMsg = "";
		
		String szYD_GP				 	= "";
		String szYD_GP_DB 				= "";
		String szYD_CURR_BAY_GP_DB 		= "";
		String szYD_TCAR_SCH_ID 		= "";
		String szUser 					= "";
		String sYD_GP_CHK				= "";
		String sPROC_FLAG               = "N";
		String szYD_EQP_STAT            = "N";
		
		try {
			
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			
			//저장위치 
			for(int x=0;x<inDto.length;x++){
				szYD_EQP_ID 		= yddatautil.setDataDefault(inDto[x].getField("YD_EQP_ID"), "");
				szYD_GP				= yddatautil.setDataDefault(inDto[x].getField("YD_GP"), "");
				szUser 				= ydDaoUtils.paraRecChkNull(inDto[x],"YD_USER_ID");
				sYD_GP_CHK			= yddatautil.setDataDefault(inDto[x].getField("YD_GP_DB"), "");
				// sYD_GP_CHK 대차이동구간 화면에서  기동
				
				if(szYD_EQP_ID.equals("")){
					szLogMsg = "설비ID값이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);	
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szLogMsg);	
					return outRecord;
				}
//대차 현재동 CHECK		
				//파라미터 설정 
				outRecordSet 	= JDTORecordFactory.getInstance().createRecordSet("");
				inRecord1 	= JDTORecordFactory.getInstance().create();
				inRecord1.setField("V_YD_EQP_ID", szYD_EQP_ID);
				// 설비정보 조회
				/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getEqpInfo*/
				outRecordSet = dao.getEqpInfo(inRecord1);
				
				if((outRecordSet == null ) || (outRecordSet.size() == 0)){
					szMsg = "[JSP Session : "+szOperationName+"] 해당 설비 조회시 ERROR";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					
					szRtnMsg[x] = "해당 설비정보 조회시 ERROR";
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
				}
				
				outRecordSet.first();
				outRecord1 = outRecordSet.getRecord();
			
				szYD_GP_DB			= ydDaoUtils.paraRecChkNull(outRecord1, "YD_GP");
				szYD_CURR_BAY_GP_DB	= ydDaoUtils.paraRecChkNull(outRecord1, "YD_CURR_BAY_GP");
				szYD_TCAR_SCH_ID 	= ydDaoUtils.paraRecChkNull(outRecord1, "YD_TCAR_SCH_ID");
				szYD_EQP_STAT 		= ydDaoUtils.paraRecChkNull(outRecord1, "YD_EQP_STAT");
				
				ydUtils.putLog(szSessionName, szMethodName, "szYD_GP:"    + szYD_GP, YdConstant.DEBUG);
				ydUtils.putLog(szSessionName, szMethodName, "szYD_GP_DB:" + szYD_GP_DB, YdConstant.DEBUG);
				
				
//				if(szYD_EQP_ID.equals("JXTC01")||szYD_EQP_ID.equals("JXTC02")){
//					if(sYD_GP_CHK.equals("")){
//					} else {
//						if(szYD_EQP_STAT.equals("M")){ 
//							outRecord.setField("RTN_CD" 	, "0");	
//							outRecord.setField("RTN_MSG" 	, "대차가 이동중입니다.이동완료후 처리 하세요.");	
//							return outRecord;						
//						}
//					} 
//					
//					if(!szYD_GP.equals(szYD_GP_DB)){
//						//야드까지 clear 처리 해야 함
//						if(sYD_GP_CHK.equals("")){
//							outRecord.setField("RTN_CD" 	, "0");	
//							outRecord.setField("RTN_MSG" 	, "타창고에서 사용중입니다.");	
//							return outRecord;
//						} 
//					}
//				}
				
				
				if(sYD_GP_CHK.equals("")){
					sPROC_FLAG = "Y";
				} else {
					sPROC_FLAG = "N";
				}	
					
				ydUtils.putLog(szSessionName, szMethodName, "sPROC_FLAG:" + sPROC_FLAG, YdConstant.DEBUG);
				
//sch 편성여부  CHECK		
//C증설1
				if(!szYD_TCAR_SCH_ID.equals("")){
					//파라미터 설정 
					outRecordSet 	= JDTORecordFactory.getInstance().createRecordSet("");
					inRecord1 	= JDTORecordFactory.getInstance().create();
					inRecord1.setField("V_YD_TCAR_SCH_ID", szYD_TCAR_SCH_ID);
					// 설비정보 조회
					/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getTEqpInfo*/
					outRecordSet = dao.getTEqpInfo(inRecord1);
					
					
					if(outRecordSet.size() == 0){
						szMsg = "[JSP Session : "+szOperationName+"] 해당 대차SCH 조회시 ERROR";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, szMsg);	
						return outRecord;
					}
					//outRecordSet.first();
	
					for(int Loopi = 1; Loopi <= outRecordSet.size(); Loopi++) {
						outRecordSet.absolute(Loopi);
						outRecord1  = JDTORecordFactory.getInstance().create();
						outRecord1 = outRecordSet.getRecord();
						String szSTL_NO 	= ydDaoUtils.paraRecChkNull(outRecord1,"STL_NO");
						String szSCH_CNT 	= ydDaoUtils.paraRecChkNull(outRecord1,"SCH_CNT");
						
						//1,2번 대차인 경우에만 스케줄편성 여부 체크
//						if(szYD_EQP_ID.equals("JXTC01") || szYD_EQP_ID.equals("JXTC02")){
//							if(szSCH_CNT.equals("0") ){
//								
//							} else {
//								outRecord.setField("RTN_CD" 	, "0");	
//								outRecord.setField("RTN_MSG" 	, szSTL_NO+ "코일에 대해 스케쥴이 편성되어 있으니 삭제후 처리 하세요");	
//								return outRecord;
//							}
//						}
	//mtl 삭제처리
						if(sPROC_FLAG.equals("Y")){
		
							inRecord1 = JDTORecordFactory.getInstance().create();
		        			inRecord1.setField("YD_TCAR_SCH_ID"	, szYD_TCAR_SCH_ID);						
		        			inRecord1.setField("STL_NO"			, szSTL_NO);							
			    			intRtnVal = ydTcarftmvmtlDao.delYdTcarftmvmtl(inRecord1);
			    			if(intRtnVal > 1) {
								szMsg="[대차 정보 삭제시 이상 발생 Error!! Code : " + intRtnVal;
								outRecord.setField("RTN_CD" 	, "0");	
								outRecord.setField("RTN_MSG" 	, "대차 정보 삭제시 이상 발생 Error");	
								return outRecord;
			    			}
						}
					}
				}
				if(sPROC_FLAG.equals("Y")){
//대차sch update
					inRecord1 = JDTORecordFactory.getInstance().create();
					inRecord1.setField("YD_TCAR_SCH_ID"			, szYD_TCAR_SCH_ID);		
					inRecord1.setField("MODIFIER"				, szUser);		
					inRecord1.setField("YD_EQP_WRK_STAT"		, "U");		
					inRecord1.setField("YD_WRK_PROG_STAT"		, "");		
					inRecord1.setField("YD_EQP_WRK_SH"			, "");	
					inRecord1.setField("YD_EQP_WRK_WT"			, "");		
					inRecord1.setField("YD_STK_BED_TP"			, "");		
					inRecord1.setField("YD_CARLD_LEV_LOC"		, "");	
					inRecord1.setField("YD_CARLD_LEV_DT"		, "");	
					inRecord1.setField("YD_CARLD_ARR_DT"		, "");	
					inRecord1.setField("YD_CARLD_WRK_BOOK_ID"	,"");	
					inRecord1.setField("YD_CARLD_SCH_REQ_GP"	, "6");		
					inRecord1.setField("YD_CARLD_STOP_LOC"		, szYD_GP+szYD_CURR_BAY_GP_DB+szYD_EQP_ID.substring(2, 6));		
					inRecord1.setField("YD_CARLD_ST_DT"			, "");		
					inRecord1.setField("YD_CARLD_CMPL_DT"		, "");		
					inRecord1.setField("YD_CARLD_WRK_ACT_GP"	, "");	
					inRecord1.setField("YD_CARLD_WRK_CRN"		, "");		
					inRecord1.setField("YD_CARUD_WRK_ACT_GP"	, "");		
					inRecord1.setField("YD_CARUD_LEV_DT"		, "");		
					inRecord1.setField("YD_CARUD_ARR_DT"		, "");		
					inRecord1.setField("YD_CARUD_WRK_BOOK_ID"	, "");		
					inRecord1.setField("YD_CARUD_SCH_REQ_GP"	, "3");		
					inRecord1.setField("YD_CARUD_STOP_LOC"		, szYD_GP+szYD_CURR_BAY_GP_DB+szYD_EQP_ID.substring(2, 6));
					inRecord1.setField("YD_CARUD_ST_DT"			, "");	
					inRecord1.setField("YD_CARUD_CMPL_DT"		, "");		
					inRecord1.setField("YD_CARUD_WRK_CRN"		, "");		
					inRecord1.setField("YD_CAR_PROG_STAT"		, "0");		 
					inRecord1.setField("YD_TCAR_WRK_SEQ"		, "");						
					/*com.inisteel.cim.yd.dao.ydtcarschdao.YdTcarschDao.updYdTcarsch*/
	    			intRtnVal = ydTcarSchDao.updYdTcarsch(inRecord1, 0);
	    			if(intRtnVal <= 0) {
	    				szMsg="대차sch 수정시 error 발생";
		    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" , "0");	
						outRecord.setField("RTN_MSG", szMsg);	
						return outRecord;
	    			}				
				}
				recPara = JDTORecordFactory.getInstance().create();
				
				//-------------------------------------------------------------------------------------------------------------
				//-------------------------------------------------------------------------------------------------------------

				
				// 1. 배드상태 변경(사용불가 -> 대차점유권한이 없는 야드전체 )
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("V_MODIFIER",				szUser);             		// 수정자
				recPara.setField("V_YD_STK_BED_ACT_STAT",	"C"); 			            // 적치배드활성상태(비활성화)
				recPara.setField("V_YD_GP1", 				"H"); 				// 사용야드
				recPara.setField("V_YD_GP2", 				"J"); 				// 사용야드
				recPara.setField("V_YD_EQP_GP",				szYD_EQP_ID.substring(2)); 	// 대차
				recPara.setField("V_YD_BAY_GP1",			"A"); // 동
				recPara.setField("V_YD_BAY_GP2",			"B"); // 동
				recPara.setField("V_YD_BAY_GP3",			"C"); // 동
				recPara.setField("V_YD_BAY_GP4",			"D"); // 동
				recPara.setField("V_YD_BAY_GP5",			"E"); // 동
				recPara.setField("V_YD_BAY_GP6",			"F"); // 동
				recPara.setField("V_YD_BAY_GP7",			"G"); // 동
				recPara.setField("V_YD_BAY_GP8",			"H"); // 동
				/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.updTCarYdGpMgtBed*/
				intRtnVal = dao.updTCarYdGpMgtBed(recPara);					
				if (intRtnVal < 1) {
					outRecord.setField("RTN_CD" , "0");	
					outRecord.setField("RTN_MSG", "배드상태 변경(사용불가 무대차)중 오류가 발생하였습니다.");	
					return outRecord;
				} 
				
				// 2. 배드상태 변경(사용가능 -> 대차점유권을 가지는 야드)
				recPara.setField("V_MODIFIER",				szUser);		// 수정자
				recPara.setField("V_YD_STK_BED_ACT_STAT",	"L"); 										// 적치배드활성상태(적치가능)
				recPara.setField("V_YD_GP1",				szYD_GP); 			// 사용야드
				recPara.setField("V_YD_GP2",				szYD_GP); 			// 사용야드
				recPara.setField("V_YD_EQP_GP",				szYD_EQP_ID.substring(2)); 					// 대차
				recPara.setField("V_YD_BAY_GP1",			szYD_CURR_BAY_GP_DB); // 동
				recPara.setField("V_YD_BAY_GP2",			""); // 동
				recPara.setField("V_YD_BAY_GP3",			""); // 동
				recPara.setField("V_YD_BAY_GP4",			""); // 동
				recPara.setField("V_YD_BAY_GP5",			""); // 동
				recPara.setField("V_YD_BAY_GP6",			""); // 동
				recPara.setField("V_YD_BAY_GP7",			""); // 동
				recPara.setField("V_YD_BAY_GP8",			""); // 동
				
				/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.updTCarYdGpMgtBed*/
				intRtnVal = dao.updTCarYdGpMgtBed(recPara);					
				if (intRtnVal < 1) {
					outRecord.setField("RTN_CD" , "0");	
					outRecord.setField("RTN_MSG", "배드상태 변경(사용가능 무대차)중 오류가 발생하였습니다.");	
					return outRecord;
				} 


				//-------------------------------------------------------------------------------------------------------------
				//-------------------------------------------------------------------------------------------------------------
				// 1. 레이어상태 변경(사용불가 -> 대차점유권한이 없는 야드전체 )
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("V_YD_STK_LYR_ACT_STAT",	"C");		// 적치단활성상태
				recPara.setField("V_STL_NO",				"");		// 코일번호
				recPara.setField("V_YD_STK_LYR_MTL_STAT",	"E");		// 적치단재료상태
				recPara.setField("V_MODIFIER", 				szUser);        				// 수정자
				recPara.setField("V_YD_GP1", 				"H"); 							// 사용야드
				recPara.setField("V_YD_GP2", 				"J"); 							// 사용야드
				recPara.setField("V_YD_EQP_GP",				szYD_EQP_ID.substring(2)); 									// 대차
				recPara.setField("V_YD_BAY_GP1",			"A"); // 동
				recPara.setField("V_YD_BAY_GP2",			"B"); // 동
				recPara.setField("V_YD_BAY_GP3",			"C"); // 동
				recPara.setField("V_YD_BAY_GP4",			"D"); // 동
				recPara.setField("V_YD_BAY_GP5",			"E"); // 동
				recPara.setField("V_YD_BAY_GP6",			"F"); // 동
				recPara.setField("V_YD_BAY_GP7",			"G"); // 동
				recPara.setField("V_YD_BAY_GP8",			"H"); // 동

				/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.updTCarYdGpMgtLyr */
				intRtnVal = dao.updTCarYdGpMgtLyr(recPara);					
				if (intRtnVal < 1) {
					outRecord.setField("RTN_CD" , "0");	
					outRecord.setField("RTN_MSG", "레이어상태 변경(사용불가 무대차)중 오류가 발생하였습니다.");	
					return outRecord;

				}
				
				// 2. 레이어상태 변경(사용가능 -> 대차점유권을 가지는 야드)
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("V_YD_STK_LYR_ACT_STAT",	"E");		// 적치단활성상태
				recPara.setField("V_STL_NO",				"");		// 코일번호
				recPara.setField("V_YD_STK_LYR_MTL_STAT",	"E");		// 적치단재료상태
				recPara.setField("V_MODIFIER", 				szUser);        	// 수정자
				recPara.setField("V_YD_GP1",				szYD_GP); 				// 사용야드
				recPara.setField("V_YD_GP2",				szYD_GP); 				// 사용야드
				recPara.setField("V_YD_EQP_GP",				szYD_EQP_ID.substring(2)); 						// 대차
				recPara.setField("V_YD_BAY_GP1",			szYD_CURR_BAY_GP_DB); // 동
				recPara.setField("V_YD_BAY_GP2",			""); // 동
				recPara.setField("V_YD_BAY_GP3",			""); // 동
				recPara.setField("V_YD_BAY_GP4",			""); // 동
				recPara.setField("V_YD_BAY_GP5",			""); // 동
				recPara.setField("V_YD_BAY_GP6",			""); // 동
				recPara.setField("V_YD_BAY_GP7",			""); // 동
				recPara.setField("V_YD_BAY_GP8",			""); // 동
				
				/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.updTCarYdGpMgtLyr*/
				intRtnVal = dao.updTCarYdGpMgtLyr(recPara);					
				if (intRtnVal < 1) {
					outRecord.setField("RTN_CD" , "0");	
					outRecord.setField("RTN_MSG", "레이어상태 변경(사용가능)중 오류가 발생하였습니다.");	
					return outRecord;
				}

				//-------------------------------------------------------------------------------------------------------------
				//-------------------------------------------------------------------------------------------------------------
				if(!sYD_GP_CHK.equals("")){  // 대차이동구간 화면에서  기동
					// 이동구간 변경 
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("V_YD_EQP_ID",					inDto[x].getFieldString("YD_EQP_ID"));            
					recPara.setField("V_YD_GP",						inDto[x].getFieldString("YD_GP"));             
					recPara.setField("V_YD_TCAR_WRK_ABLE_BAY1",		inDto[x].getFieldString("YD_TCAR_WRK_ABLE_BAY1"));
					recPara.setField("V_YD_TCAR_WRK_ABLE_BAY2",		inDto[x].getFieldString("YD_TCAR_WRK_ABLE_BAY2"));
					recPara.setField("V_YD_TCAR_WRK_ABLE_BAY3",		inDto[x].getFieldString("YD_TCAR_WRK_ABLE_BAY3"));
					recPara.setField("V_YD_TCAR_WRK_ABLE_BAY4",		inDto[x].getFieldString("YD_TCAR_WRK_ABLE_BAY4"));
					recPara.setField("V_YD_TCAR_WRK_ABLE_BAY5",		inDto[x].getFieldString("YD_TCAR_WRK_ABLE_BAY5"));
					recPara.setField("V_YD_TCAR_WRK_ABLE_BAY6",		inDto[x].getFieldString("YD_TCAR_WRK_ABLE_BAY6"));
					recPara.setField("V_YD_TCAR_WRK_ABLE_BAY7",		inDto[x].getFieldString("YD_TCAR_WRK_ABLE_BAY7"));
					recPara.setField("V_YD_TCAR_WRK_ABLE_BAY8",		inDto[x].getFieldString("YD_TCAR_WRK_ABLE_BAY8"));
					recPara.setField("V_MODIFIER",					inDto[x].getFieldString("YD_USER_ID"));     
					recPara.setField("V_YD_EQP_ID",					inDto[x].getFieldString("YD_EQP_ID"));            
					
					/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.updTCarYdGpMgt*/
					intRtnVal = dao.updTCarYdGpMgt(recPara);
					
					if (intRtnVal < 1) {
						outRecord.setField("RTN_MSG", "대차 이동구간 변경 중 오류가 발생 하였습니다.");
						outRecord.setField("RTN_CD", "0");
						return outRecord;
					} 		
				} else {
					
					recPara = JDTORecordFactory.getInstance().create();
					//입출입  상태
					recPara.setField("YD_EQP_ID"		, inDto[x].getFieldString("YD_EQP_ID"));  // 야드 설비 ID
					recPara.setField("YD_EQP_STAT"  	, "A"); // 상태 를 도착으로 
					
					//com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.updYdEqp
					intRtnVal = ydEqpDao.updYdEqp(recPara, 0);
					
					if (intRtnVal < 1) {
						outRecord.setField("RTN_MSG", "대차 이동구간 변경 중 오류가 발생 하였습니다.");
						outRecord.setField("RTN_CD", "0");
						return outRecord;
					} 		
						
					
				}

	    			
			}//end for

			outRecord.setField("RTN_CD" 	, "1");	
			return outRecord;
			
		} catch (Exception e) {
			e.printStackTrace();
			
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
		}
		
		

	}	// end of updCoilYdTcarStsSet
		
	/**
	 * 야드관리 > 코일제품창고 > 설비관리 > 대차스케줄관리  --> 대차상태설정 수정 (설비 ON_LINE, OFF_LINE 설정)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return String[]
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.15 
	 */
	public JDTORecord updCoilYdTcarStsSetCrnMode(JDTORecord[] inDto) throws DAOException {
		int intRtnVal  = 0;
		
		String[] szRtnMsg = new String[inDto.length];
	
		String szLogMsg   = null;
		String szMethodName = "updCoilYdTcarStsSetCrnMode";
		String szYD_EQP_ID = null;
		String szYD_EQP_WRK_MODE = null;
		String szYdGp = null;
		String szEjbConName = "";
		String szRcvTcCode = "";
		String szOperationName = "설비 ON_LINE, OFF_LINE 설정";
		
		JDTORecord recPara  = JDTORecordFactory.getInstance().create();
		YdEqpDao ydEqpDao   = new YdEqpDao();		
		EJBConnector ejbConn = null;
		JDTORecord 			outRecord 		= JDTORecordFactory.getInstance().create(); // 
		
		
		try {
			//설비 ON_LINE, OFF_LINE 설정
			
			szLogMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			for(int x=0;x<inDto.length;x++){
				
				szRtnMsg[x] = YdConstant.RETN_CD_SUCCESS;
				szYD_EQP_ID 		= yddatautil.setDataDefault(inDto[x].getField("YD_EQP_ID"), "");
				szYD_EQP_WRK_MODE 	= yddatautil.setDataDefault(inDto[x].getField("YD_EQP_WRK_MODE"), "");
				//입출입  상태
				recPara.setField("YD_EQP_ID", szYD_EQP_ID);  // 야드 설비 ID
				recPara.setField("YD_EQP_WRK_MODE", szYD_EQP_WRK_MODE); // 야드설비작업Mode
				
				//com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.updYdEqp
				intRtnVal = ydEqpDao.updYdEqp(recPara, 0);
				
				if (intRtnVal <= 0) {
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, "장비 등록시 ERROR 발생하였습니다..");	
					return outRecord;
				} // end of if
			}
			
			/* 해당 설비 UPDATE 후 설비 운전모드 전환 호출 
			 *  C열연 코일야드   L2 : Y5YDL003 rcvY5EqpDrvMdTurnov  EqpTrackingFaEJB
			 */
			
			szYdGp = szYD_EQP_ID.substring(0,1);
			
			szLogMsg = "[JSP SESSION -  (야드구분  :  " + szYdGp +") ] 입니다";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			szRcvTcCode  = "Y5YDL003";
			szEjbConName = "procY5EqpDrvMdTurnov";
			

			szLogMsg = "[JSP SESSION -  (설비 운전모드 전환     "+ szEjbConName +")을 호출  시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);

			
			//해당 메소드는 void 형이라 리턴이 없습니다.
			
			recPara.setField("JMS_TC_CD", szRcvTcCode);
		   	ejbConn = new EJBConnector("default", this);				
		 	ejbConn.trx("EqpTrackingSeEJB", szEjbConName, recPara);
		
			
			szLogMsg = "[JSP SESSION - (설비 운전모드 전환     "+ szEjbConName +")을 호출  끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			

			outRecord.setField("RTN_CD" 	, "1");	
			return outRecord;
			
		} catch (Exception e) {
			e.printStackTrace();
			
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
		}

	}	// end of updCoilYdTcarStsSetCrnMode
	
	
	
	/**
	 * 공대차 스케줄 호출
	 * @ejb.interface-method
	 * @param JDTORecord[]
	 * @return String
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.10
	 */
	public JDTORecord CoilYdTcarStsSetTcarA(JDTORecord[] inDto) throws DAOException {
		JDTORecord 		recPara 			= null;			// 파라미터 레코드
		JDTORecord 		recInTemp 			= null;			// 파라미터 레코드
		JDTORecord 		outRecord 			= JDTORecordFactory.getInstance().create();
		String 			szYdGp 				= "";			// 야드구분
		String 			szMethodName 		= "CoilYdTcarStsSetTcarA";	// 메소드명
		Integer 		objRtnInt 			= null;			// 스케쥴 호출 리턴값 
		String 			szRtnMsg 			= null;			// 리턴메시지 
		String 			szLogMsg 			= null;			// 로그메시지
		String 			szJMS_TC_CD 		= null;			// JMS TC 코드
		EJBConnector 	ejbConn 			= null;			// ejb커넥터
		String 			szEjbMethod 		= null;			// ejb메소드명
		YdDelegate ydDelegate =new YdDelegate();
		try{

			szLogMsg = "JSP-SESSION  [공대차 스케줄 호출] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

			recPara   = JDTORecordFactory.getInstance().create();	
			//공대차 스케줄 호출

			szYdGp = yddatautil.setDataDefault(inDto[0].getField("YD_GP"),""); // 야드구분

			if ("".equals(szYdGp)){
				//공장 야드 구분이 없습니다 
				szLogMsg = "공대차 스케줄 호출 - 공장 야드 구분이 없습니다 ";
				ydUtils.putLog(szSessionName, szMethodName, "[JSP Session] "+szLogMsg, YdConstant.ERROR);
				
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szLogMsg);
				
				return outRecord;

			}
			
			szJMS_TC_CD = "YDYDJ521";
			szEjbMethod = "procY5TcarSch";
			
			recPara.setField("JMS_TC_CD", szJMS_TC_CD); // 메시지코드
			recPara.setField("YD_EQP_ID", inDto[0].getField("YD_EQP_ID"));
			recPara.setField("YD_LD_UD_GP", "");
			recPara.setField("YD_WBOOK_ID", "");
			recPara.setField("YD_TO_BAY",yddatautil.setDataDefault(inDto[0].getField("YD_TO_BAY"),""));


			ejbConn = new EJBConnector("default", "CoilTransEqpSchSeEJB", this);
			outRecord = (JDTORecord)ejbConn.trx( "procY5TcarSch" , new Class[] { JDTORecord.class }, new Object[] { recPara });
			String sRTN_CD			= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
			String sTCAR_MOVE_SND	= StringHelper.evl(outRecord.getFieldString("TCAR_MOVE_SND"), "N");
			String sYD_TCAR_SCH_ID	= StringHelper.evl(outRecord.getFieldString("YD_TCAR_SCH_ID"), "");
			String sYD_AIM_BAY_GP	= StringHelper.evl(outRecord.getFieldString("YD_AIM_BAY_GP"), "");
			String sRTN_MSG			= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
			
    			
   			if(sRTN_CD.equals("0")){ 
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
				szLogMsg = "[JSP Session]공대차 스케줄 호출 처리 실패";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);

				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szLogMsg);
				
				return outRecord;
   			} else {
   				
   				if(sTCAR_MOVE_SND.equals("Y")){
 	               	
		    		recInTemp = JDTORecordFactory.getInstance().create();
		    		recInTemp.setField("MSG_ID"			, "YDY5L006");
		    		recInTemp.setField("YD_GP"			, szYdGp);
		    		recInTemp.setField("YD_TCAR_SCH_ID"	, sYD_TCAR_SCH_ID);
		    		recInTemp.setField("YD_AIM_BAY_GP"	, sYD_AIM_BAY_GP);
		    		
		    		ydDelegate.sendMsg(recInTemp);
		    		
					ydUtils.putLog(szSessionName, szMethodName, "대차 출발지시 전송 완료", YdConstant.DEBUG);
              	
   				} else {
   				
   					szRtnMsg = YdConstant.RETN_CD_SUCCESS;
   					szLogMsg = "[JSP Session]공대차 스케줄 호출 처리 실패(";
   					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);

   					outRecord.setField("RTN_CD" 	, "1");	
   					//대차 자동 출발 가능 여부 체크에 걸림
   					outRecord.setField("RTN_MSG" 	, sRTN_MSG + ": 대차 확인후 수동으로 이동처리 해야 함");
   					
   					return outRecord;
   	   					
   					
   				}
				szRtnMsg = YdConstant.RETN_CD_SUCCESS;
				szLogMsg = "[JSP Session]공대차 스케줄 호출 처리 성공";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
   			}

			outRecord.setField("RTN_CD" 	, "1");	
			return outRecord;

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
		}
	}
	
	/**
	 *   출발 실적 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return String
	 * @throws DAOException
	 */
	public JDTORecord CoilYdTcarStsSetTcarB(JDTORecord[] inDto) throws DAOException {
		
		JDTORecord recPara = null;
		JDTORecord 		outRecord 			= JDTORecordFactory.getInstance().create();
		//YdDelegate ydDelegate = new YdDelegate();
		String szMethodName = "CoilYdTcarStsSetTcarB";
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
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szLogMsg);
				
				return outRecord;
  

			}else if("0".equals(szProgStat)){
				// 출발실적(공차)
//				szAimYdGp = yddatautil.setDataDefault(inDto[0].getField("YD_CARLD_STOP_LOC"),"      ").substring(1,2);			
				szAimYdGp  = StringHelper.evl(inDto[0].getFieldString("YD_CARLD_STOP_LOC"),"      ").substring(1,2); //스케줄범위코드
			}else if("5".equals(szProgStat)){	
				// 출발실적(영차)

				// 영차 출발일경우 - 도착실적(영차)
//				szAimYdGp = yddatautil.setDataDefault(inDto[0].getField("YD_CARUD_STOP_LOC"),"      ").substring(1,2);
				szAimYdGp  = StringHelper.evl(inDto[0].getFieldString("YD_CARUD_STOP_LOC"),"      ").substring(1,2); //스케줄범위코드
			}
			else{
				// 야드차량진행상태가 맞지 않습니다 
				szLogMsg = "[JSP Session]대차 출발 실적 호출 - 야드차량진행상태(YD_CAR_PROG_STAT[" + yddatautil.setDataDefault(inDto[0].getFieldString("YD_CAR_PROG_STAT"),"") + "])값이 맞지 않습니다. ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szLogMsg);
				
				return outRecord;
  
			}
			recPara   = JDTORecordFactory.getInstance().create();	

			szYdGp = StringHelper.evl(inDto[0].getFieldString("YD_GP"),"");
			if ("".equals(szYdGp)){
				//공장 야드 구분이 없습니다 
				szLogMsg = "[JSP Session]대차 출발 실적 호출 - 공장 야드 구분이 없습니다 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szLogMsg);
				
				return outRecord;
  
			}else if( YdConstant.YD_GP_C_HR_COIL_MATL_YARD.equals(szYdGp) || YdConstant.YD_GP_C_HR_COIL_GDS_YARD.equals(szYdGp) ){

				recPara.setField("JMS_TC_CD"		, "YDYDJ621");
				recPara.setField("YD_EQP_ID"		, yddatautil.setDataDefault(inDto[0].getField("YD_EQP_ID"),""));
				recPara.setField("YD_BAY_GP"		, yddatautil.setDataDefault(inDto[0].getField("NEW_YD_CURR_BAY_GP"),""));
				recPara.setField("YD_MOVE_GP"		, "S");
				recPara.setField("YD_TCAR_MOVE_DIR"	, "F");
				recPara.setField("YD_TCAR_CURR_BAY"	, yddatautil.setDataDefault(inDto[0].getField("NEW_YD_CURR_BAY_GP"),""));
				recPara.setField("YD_TCAR_AIM_BAY"	, szAimYdGp);
			}

			szLogMsg = "[JSP Session]대차 출발 실적 - " +  ydDaoUtils.paraRecChkNull(inDto[0],"YD_EQP_ID");
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);				

			ydUtils.displayRecord(szOperationName, recPara);

			ejbConn = new EJBConnector("default", "CoilTcarMvHdSeEJB", this);					
			objRtnInt = (Integer)ejbConn.trx( "procY5TcarMvWr" , new Class[] { JDTORecord.class }, new Object[] { recPara });

			if( objRtnInt.intValue() == YdConstant.RETN_INT_SUCCESS.intValue() ) {			//성공
				szRtnMsg = YdConstant.RETN_CD_SUCCESS;
				szLogMsg = "[JSP Session]대차 출발 실적 호출 처리 성공";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			}else{																			//실패
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
				szLogMsg = "[JSP Session]대차 출발 실적 호출 처리 실패";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szLogMsg);
				
				return outRecord;
  
			}
			
			outRecord.setField("RTN_CD" 	, "1");	
			return outRecord;

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
		}
	}
	
	
	/**
	 *   도착 실적 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return String
	 * @throws DAOException
	 */
	public JDTORecord CoilYdTcarStsSetTcarC(JDTORecord[] inDto) throws DAOException {
		
		JDTORecord recPara     = null;
		JDTORecord 		outRecord 			= JDTORecordFactory.getInstance().create();
		//YdDelegate ydDelegate = new YdDelegate();
		String szProgStat      = "";
		String szYdGp          = "";
		String szBayGp         = "";
		String szMethodName		= "CoilYdTcarStsSetTcarC";
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


			szProgStat = ydDaoUtils.paraRecChkNull(inDto[0], "YD_CAR_PROG_STAT"); 

			if("".equals(szProgStat)){
				//상태값이 들어 있지 않습니다. 
				szLogMsg = "[JSP Session]대차 도착 실적 호출 - 야드차량진행상태(YD_CAR_PROG_STAT)값이 존재하지 않습니다. ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szLogMsg);
				
				return outRecord;
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
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szLogMsg);
				
				return outRecord;
			}
			recPara   = JDTORecordFactory.getInstance().create();	

			szYdGp = ydDaoUtils.paraRecChkNull(inDto[0], "YD_GP");

			if ("".equals(szYdGp)){
				//공장 야드 구분이 없습니다 
				szLogMsg = "[JSP Session]대차 도착 실적 호출 - 공장 야드 구분이 없습니다 ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szLogMsg);
				
				return outRecord;
			}else if( YdConstant.YD_GP_C_HR_COIL_MATL_YARD.equals(szYdGp) || YdConstant.YD_GP_C_HR_COIL_GDS_YARD.equals(szYdGp) ){
				//C열연 소재 야드 
				szJMS_TC_CD = "YDYDJ621";
				szEjbMethod = "procY5TcarMvWr";

				recPara.setField("JMS_TC_CD", szJMS_TC_CD);
				recPara.setField("YD_EQP_ID", ydDaoUtils.paraRecChkNull(inDto[0], "YD_EQP_ID"));

				szBayTempGp = yddatautil.setDataDefault(inDto[0].getField("NEW_YD_CURR_BAY_GP"),"");
				
				recPara.setField("YD_BAY_GP"		, szBayTempGp);
				recPara.setField("YD_MOVE_GP"		, "E");
				recPara.setField("YD_TCAR_MOVE_DIR"	, "F");
				recPara.setField("YD_TCAR_CURR_BAY"	, szBayTempGp);
				recPara.setField("YD_TCAR_AIM_BAY"	, szBayGp);

			}

			ydUtils.displayRecord(szOperationName, recPara);

			ejbConn = new EJBConnector("default", "CoilTcarMvHdSeEJB", this);					
			objRtnInt = (Integer)ejbConn.trx( "procY5TcarMvWr" , new Class[] { JDTORecord.class }, new Object[] { recPara });

			if( objRtnInt.intValue() == YdConstant.RETN_INT_SUCCESS.intValue() ) {			//성공
				szRtnMsg = YdConstant.RETN_CD_SUCCESS;
				szLogMsg = "[JSP Session]대차 도착 실적 호출 처리 성공";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			}else{																			//실패
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
				szLogMsg = "[JSP Session]대차 도착 실적 호출 처리 실패";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szLogMsg);
				
				return outRecord;
			}
			//}
			outRecord.setField("RTN_CD" 	, "1");	
			return outRecord;

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
		}
	}

	
	/**
	 *   완료 실적 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecord CoilYdTcarStsSetTcarD(JDTORecord[] inDto) throws DAOException {
		
		JDTORecord recPara = null;
		JDTORecord recStopPos = null;
		JDTORecord recTemp = null;
		JDTORecord recInTemp = null;
		JDTORecord 		outRecord 			= JDTORecordFactory.getInstance().create();
		
		JDTORecordSet    outRecSet  = null;
		EJBConnector ejbConn = null;
		   
		 
		String sYdCrnSchId = "";
		String szProgStat = "";
		String szWbookId ="";
		String szYdGp = "";
		String szYdStkPos ="";
		String szMtlStat = "";
		String szYD_BAY_GP ="";
		String szEqpId = "";
		//String szCrnId =  "";
		String szMsg              		= "";
	    String szMethodName       		= "CoilYdTcarStsSetTcarD";
	    String szYD_WBOOK_ID            ="";
	    String szSchCd                  = ""; 
	    String szOperationName = "대차 완료실적";
	  
	    
		int nCount = 0;
	    int nMtlCnt =0;

	    String szLogMsg = null;


	    YdStkLyrDao ydStkLyrDao   = new YdStkLyrDao ();  
	    YdTcarFtmvMtlDao ydTcarFtmvMtlDao = new YdTcarFtmvMtlDao();
	    YdTcarSchDao ydTcarSchDao = new YdTcarSchDao();


	    YdDelegate ydDelegate = new YdDelegate();

	    try{

	    	szLogMsg = "JSP-SESSION [완료 실적]시작";
	    	ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	

	    	szProgStat = yddatautil.setDataDefault(inDto[0].getField("YD_CAR_PROG_STAT"),"");

	    	if("".equals(szProgStat)){
	    		//상태값이 들어 있지 않습니다. 
	    		szLogMsg = "[JSP Session]대차 완료 실적 호출 - 야드차량진행상태(YD_CAR_PROG_STAT)값이 존재하지 않습니다. ";
	    		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szLogMsg);
				
				return outRecord;

	    	}

	    	else if("4".equals(szProgStat)){


//	    		// 완료실적 (상차)
//	    		//1. 상차 완료 실적을 처리 할 수 있는지 체크 한다.
//	    		//   상차할 대차 베드 정보에 권하 정보가 있으면 완료실적을 처리 할수 없다고 처리 한다.
//	    		//  대차 위에 베드가 있으므로 추후에는 적치열로 조회해야함
//
//	    		szYdStkPos = yddatautil.setDataDefault(inDto[0].getField("YD_CARLD_STOP_LOC"),"        ");
//	    		recStopPos =  JDTORecordFactory.getInstance().create();
//	    		recStopPos.setField("YD_STK_COL_GP", szYdStkPos.substring(0,6));
//	    		recStopPos.setField("YD_STK_BED_NO", "01");
//
//	    		outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
//
//	    		ydStkLyrDao.getYdStklyr(recStopPos, outRecSet, 1);
//
//
//
//	    		/////////////////////////////////////////////////////////////////////
//	    		//해당 상차 위치에 베드를 검색하여 정보를 조회한다.
//	    		/////////////////////////////////////////////////////////////////////
//
//	    		if (outRecSet.size()  < 0){
//	    			//Dao Error
//	    			//return "Dao Error";
//					outRecord.setField("RTN_CD" 	, "0");	
//					outRecord.setField("RTN_MSG" 	, szLogMsg);
//					
//					return outRecord;
//
//	    		}else if(outRecSet.size() == 0 ){
//	    			//해당 대차 베드위에 정보가 하나도 없을경우
//	    			//return "해당 대차 베드위에 정보가 하나도 없을경우";
//	    			szLogMsg = "[JSP Session]대차 상차완료 실적 호출 - 대차 베드위에 정보가 없습니다!";
//	    			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
//					outRecord.setField("RTN_CD" 	, "0");	
//					outRecord.setField("RTN_MSG" 	, szLogMsg);
//					
//					return outRecord;
//
//	    		}else if(outRecSet.size() >  0 ){
//	    			// 정보 존재시
//	    			outRecSet.first();
//
//	    			do{
//	    				recTemp = outRecSet.getRecord();
//	    				szMtlStat = ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_MTL_STAT");
//
//	    				//권하 대기 위치로 설정된 것이 있는 지 확인 
//	    				if(YdConstant.YD_STK_LYR_MTL_STAT_DN_WAIT.equals(szMtlStat))   
//	    					nCount++;		
//
//	    			}while(outRecSet.next());						
//	    		}
//
//
//	    		if(nCount > 0){
//	    			//에러 처리를 한다.
//	    			szLogMsg = "[JSP Session]대차 상차완료 실적 호출 - 크레인 지시가 존재 함!!";
//	    			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
//	    			//return  "크레인 지시가 존재 함!!";
//					outRecord.setField("RTN_CD" 	, "0");	
//					outRecord.setField("RTN_MSG" 	, szLogMsg);
//					
//					return outRecord;
//
//	    		}
//
//	    		//2. 상차완료 처리를 한다. --> 확인	L2에 저장품 정보를 보내야 함 
//
//	    		szMsg="하차작업예약 생성 및 등록";
//	    		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);					
//
	    		recPara = JDTORecordFactory.getInstance().create();				
	    		recPara.setField("YD_TCAR_SCH_ID", ydDaoUtils.paraRecChkNull(inDto[0], "YD_TCAR_SCH_ID"));
	    		outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp"); 
	    		/*com.inisteel.cim.yd.dao.ydtcarftmvmtldao.YdTcarftmvmtlDao.getYdStockTcarSchIdAll*/
	    		nMtlCnt   = ydTcarFtmvMtlDao.getYdTcarftmvmtl(recPara, outRecSet, 5);

	    		if (nMtlCnt < 1) {
	    			szLogMsg = "[JSP Session]대차 상차완료 실적 호출 - 대차이송재료없음";
	    			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
	    			//return "재료없음";
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szLogMsg);
					
					return outRecord;

	    		}


	    		// 하차 작업 예약 정보를 만들기 위한 재료는 이송재료 정보에서 가지고 온다.

	    		recTemp  = JDTORecordFactory.getInstance().create();
	    		recTemp  = outRecSet.getRecord(0);



	    		szYD_BAY_GP  = ydDaoUtils.paraRecChkNull( recTemp, "YD_AIM_BAY_GP"); //목표동을 하차동으로 세팅
//	    		szEqpId      = ydDaoUtils.paraRecChkNull( inDto[0], "YD_EQP_ID");
//	    		szYdGp       = szEqpId.substring(0,1);					
//	    		szSchCd      = szYdGp + szYD_BAY_GP + szEqpId.substring(2,6) +"LM";
//
//
//
//	    		//작업 예약 등록 (대차 스케줄은 크레인 스케줄을 편성하지않는다)
//	    		recPara = JDTORecordFactory.getInstance().create();	
//	    		//YD_SCH_CD
//	    		recPara.setField("YD_SCH_CD",szSchCd);
//
//	    		//YD_STK_COL_GP				
//	    		recPara.setField("YD_STK_COL_GP", szYdGp + szYD_BAY_GP + szEqpId.substring(2,6));
//
//	    		//추후 베드정보가 나올경우는 수정해야한다.
//	    		//YD_STK_BED_NO
//	    		recPara.setField("YD_STK_BED_NO", "01");
//
//	    		//YD_SH [매수]
//	    		recPara.setField("SLAB_SH", ""+nMtlCnt);
//
//
//	    		outRecSet.first();
//
//	    		for(int Loopi=0 ; Loopi<nMtlCnt ;Loopi++){
//	    			//재료번호
//
//	    			recTemp  = JDTORecordFactory.getInstance().create();
//	    			recTemp  = outRecSet.getRecord(Loopi);
//
//	    			//STL_NO []
//	    			recPara.setField("STL_NO"+(Loopi+1), ydDaoUtils.paraRecChkNull(recTemp, "STL_NO"));
//
//	    			//권상 모음순서 
//	    			//YD_UP_COLL_SEQ []
//	    			recPara.setField("YD_UP_COLL_SEQ"+(Loopi+1),""+(Loopi+1));
//	    		}
//
//	    		//추가 데이터 			
//
//	    		recPara.setField("YD_WRK_PLAN_TCAR", szEqpId);
//	    		recPara.setField("REGISTER", ydDaoUtils.paraRecChkNull(inDto[0], "MODIFIER")); 
//
//
//	    		ydUtils.displayRecord(szOperationName, recPara);
//
//	    		szYD_WBOOK_ID = this.ydManualReq(recPara); // 하차 작업예약 ID는 다시 대차스케줄을 호출하지않으므로 새로 만들었음 
//
//	    		if( YdConstant.RETN_CD_FAILURE.equals(szYD_WBOOK_ID)){
//	    			szMsg="작업예약 생성 실패";
//	    			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//	    			//return "작업예약 생성 실패";
//					outRecord.setField("RTN_CD" 	, "0");	
//					outRecord.setField("RTN_MSG" 	, szLogMsg);
//					
//					return outRecord;
//
//	    		}


	    		// 대차 스케줄 에 하차작업 예약 ID UPDATE 및 영공차 상태 변경, 차량 진행상태 변경 해준다 .

	    		recInTemp = JDTORecordFactory.getInstance().create();
	    		recInTemp.setField("YD_TCAR_SCH_ID"		, ydDaoUtils.paraRecChkNull(inDto[0], "YD_TCAR_SCH_ID")); 
	    		recInTemp.setField("YD_CAR_PROG_STAT"	, ""+5);
	    		recInTemp.setField("YD_EQP_WRK_STAT"	,"L"); //영차 상태 변경
//	    		recInTemp.setField("YD_CARUD_WRK_BOOK_ID",szYD_WBOOK_ID);
	    		recInTemp.setField("MODIFIER",ydDaoUtils.paraRecChkNull(inDto[0], "MODIFIER")); 


	    		szMsg="================대차스케줄 UPDATE ======================";
	    		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		ydUtils.displayRecord(szOperationName, recInTemp);

	    		ydTcarSchDao.updYdTcarsch(recInTemp, 0);





	    		//영대차출발지시

	    		recInTemp = JDTORecordFactory.getInstance().create();
//
//	    		recInTemp.setField("MSG_ID"			, "YDY5L006");	
//	    		recInTemp.setField("YD_TCAR_SCH_ID"	, ydDaoUtils.paraRecChkNull(inDto[0], "YD_TCAR_SCH_ID"));
//	    		recInTemp.setField("YD_GP"			, szYdGp);
//	    		recInTemp.setField("YD_SCH_CD"		, szSchCd);

	    		
	            	
	    		recInTemp = JDTORecordFactory.getInstance().create();
	    		recInTemp.setField("MSG_ID"			, "YDY5L006");
	    		recInTemp.setField("YD_GP"			, szYdGp);
	    		recInTemp.setField("YD_SCH_CD"		, szSchCd);
	    		recInTemp.setField("YD_TCAR_SCH_ID"	, ydDaoUtils.paraRecChkNull(inDto[0], "YD_TCAR_SCH_ID"));
	    		recInTemp.setField("YD_AIM_BAY_GP"	, szYD_BAY_GP);
	    		//ydDelegate.sendMsg(recInTemp);
	    		
				szMsg="[영대차출발지시";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	            	
	 
	            
	    		szMsg="영대차출발지시!!  MSG_IG : " + ydDaoUtils.paraRecChkNull(recInTemp, "MSG_ID") + " 전송";					
	    		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	    		ydDelegate.sendMsg(recInTemp);

				outRecord.setField("RTN_CD" 	, "1");	
				outRecord.setField("RTN_MSG" 	, szLogMsg);

				sYdCrnSchId = yddatautil.setDataDefault(inDto[0].getField("YD_CRN_SCH_ID") , "");
				szSchCd = yddatautil.setDataDefault(inDto[0].getField("YD_SCH_CD") , "");
				// 151102 hun 대차 중량오버일때 다음 크레인 스케줄 취소
//				if ("".equals(sYdCrnSchId)) {
//					// 취소요청 크레인이 없습니다.
//					szLogMsg = "[JSP Session]대차 완료 실적 호출 - 취소요청 크레인스케줄이 없습니다. ";
//					ydUtils.putLog(szSessionName , szMethodName , szLogMsg , YdConstant.ERROR);
//				} else {
//					szLogMsg = "[JSP Session]대차 완료 실적 호출 - 취소요청 크레인스케줄 =" + sYdCrnSchId;
//					ydUtils.putLog(szSessionName , szMethodName , szLogMsg , YdConstant.ERROR);
//					
//					recPara = JDTORecordFactory.getInstance().create();
//					recPara.setField("YD_CRN_SCH_ID" , sYdCrnSchId);
//					recPara.setField("YD_SCH_CD" , szSchCd);
//					recPara.setField("DEL_YN" , "Y");
//					recPara.setField("MODIFIER" , yddatautil.setDataDefault(inDto[0].getField("MODIFIER") , ""));
//
//					ejbConn = new EJBConnector("default" , "CoilJspSeEJB" , this);
//					outRecord = (JDTORecord) ejbConn.trx("WrkCancel" , new Class[]{JDTORecord.class} , new Object[]{recPara});
//
//				}
			    
			    return outRecord;

			       //++++++++++++상차 완료 +++++++++++(End)+++++++++++++++++++++++++++++++++++++++++++++++++++
	    	}

	    	else if("E".equals(szProgStat)){

	    		// 완료실적(하차)
	    		szWbookId = yddatautil.setDataDefault(inDto[0].getField("YD_CARUD_WRK_BOOK_ID"),"");

	    	}else{
	    		// 야드차량진행상태가 맞지 않습니다 
	    		szLogMsg = "[JSP Session]대차 완료 실적 호출 - 야드차량진행상태(YD_CAR_PROG_STAT[" + yddatautil.setDataDefault(inDto[0].getFieldString("YD_CAR_PROG_STAT"),"") + "])값이 맞지 않습니다. ";
	    		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szLogMsg);
				
				return outRecord;

	    	}
	    	recPara   = JDTORecordFactory.getInstance().create();

	    	szYdGp = yddatautil.setDataDefault(inDto[0].getField("YD_GP"),"");
	    	if ("".equals(szYdGp)){
	    		//공장 야드 구분이 없습니다 
	    		szLogMsg = "[JSP Session]대차 완료 실적 호출 - 공장 야드 구분이 없습니다 ";
	    		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szLogMsg);
				
				return outRecord;

	    	}else if(YdConstant.YD_GP_C_HR_COIL_MATL_YARD.equals(szYdGp)|| YdConstant.YD_GP_C_HR_COIL_GDS_YARD.equals(szYdGp)){
	    		//C열연 소재 야드 
	    		recPara.setField("JMS_TC_CD", "YDYDJ521");
	    	}


	    	recPara.setField("YD_EQP_ID"	, yddatautil.setDataDefault(inDto[0].getField("YD_EQP_ID"),""));
	    	recPara.setField("YD_LD_UD_GP"	, yddatautil.setDataDefault(inDto[0].getField("YD_EQP_WRK_STAT"),""));
	    	recPara.setField("YD_WBOOK_ID"	, szWbookId );

	    	ydUtils.displayRecord(szOperationName, recPara);
	    	ydDelegate.sendMsg(recPara);


			outRecord.setField("RTN_CD" 	, "1");	
			return outRecord;

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
		}
	}
	

	/**
	 *   현위치 변경 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return String
	 * @throws DAOException
	 */
	public JDTORecord CoilYdTcarStsSetTcarE(JDTORecord[] inDto) throws DAOException {
		JDTORecord 		outRecord 			= JDTORecordFactory.getInstance().create();
		JDTORecord recPara = null;	
		JDTORecord recInTemp = null;	
		JDTORecord recInTemp1 = null;	
		JDTORecord recOutTemp = null;	
		
	
		String sYD_EQP_ID = "";
		String szCurrBayGp = "";
		String szNewCurrBayGp = "";


		
		//현재는 01번 BED정보만 OPEN /CLOSE 시킨다고 함
		String szStkBedNo = "01";

		YdStkBedDao ydStkBedDao  = new YdStkBedDao ();
		YdEqpDao ydEqpDao = new YdEqpDao();
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		String szMethodName = "CoilYdTcarStsSetTcarE";
		String szOperationName  = "현위치 변경";

		String szLogMsg = null;
		int intRtnVal          = 0;
		JDTORecordSet rsResult          = null;
		JDTORecordSet rsResult1          = null;
		
		String sYD_GP = "";
		String sYD_STK_COL_GP = "";
		try{

			szLogMsg = "JSP-SESSION [현위치 변경] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

			szCurrBayGp    = yddatautil.setDataDefault(inDto[0].getField("YD_CURR_BAY_GP"),"");
			szNewCurrBayGp = yddatautil.setDataDefault(inDto[0].getField("NEW_YD_CURR_BAY_GP"),"");
			sYD_EQP_ID     = yddatautil.setDataDefault(inDto[0].getField("YD_EQP_ID"),"");

			
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");

	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_EQP_ID", sYD_EQP_ID);
	    	/*com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqp*/
	    	intRtnVal = ydEqpDao.getYdEqp(recInTemp, rsResult, 0);
	    	if(intRtnVal <= 0) {	    	 
	    		szLogMsg = "설비ID 값이 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szLogMsg);
				return outRecord;
	    	} 

	    	rsResult.absolute(1);
	    	recOutTemp = JDTORecordFactory.getInstance().create();
	    	recOutTemp.setRecord(rsResult.getRecord());
	        
	    	sYD_GP 		= ydDaoUtils.paraRecChkNull(recOutTemp, "YD_GP");
	    	sYD_STK_COL_GP = sYD_GP + szCurrBayGp + sYD_EQP_ID.substring(2, 6); // 현재동			

	    	szLogMsg="[공차출발실적] szStkColGp = " + sYD_STK_COL_GP;
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_STK_COL_GP", sYD_STK_COL_GP);
			
			rsResult1 = JDTORecordFactory.getInstance().createRecordSet("");
			/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedBed*/
			intRtnVal = ydStkBedDao.getYdStkbed(recInTemp, rsResult1, 1);
			if(intRtnVal <= 0) {
				szLogMsg="bed 정보 이상";
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szLogMsg);
				return outRecord;
			}
			
			for(int Loop_i = 1; Loop_i <= rsResult1.size(); Loop_i++) {
				rsResult1.absolute(Loop_i);
				recInTemp1  = JDTORecordFactory.getInstance().create();
				recInTemp1.setRecord(rsResult1.getRecord());		
				
			 	szStkBedNo = ydDaoUtils.paraRecChkNull(recInTemp1, "YD_STK_BED_NO");
					
			 	recInTemp = JDTORecordFactory.getInstance().create();
			 	recInTemp.setField("YD_STK_COL_GP", sYD_STK_COL_GP);
			 	recInTemp.setField("YD_STK_BED_NO", szStkBedNo);
			 	recInTemp.setField("YD_STK_BED_ACT_STAT", "C");
		 	/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.updYdStkbed*///TB_YD_STKBED
			 	intRtnVal = ydStkBedDao.updYdStkbed(recInTemp, 0);
				if(intRtnVal <  0) {
					szLogMsg="bed 정보 수정시 error 발생";
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szLogMsg);
					return outRecord;
				}
				//적치단 비활성화
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_STK_COL_GP", sYD_STK_COL_GP);
				recInTemp.setField("YD_STK_BED_NO", szStkBedNo);
				recInTemp.setField("YD_STK_LYR_ACT_STAT", "C");
				recInTemp.setField("STL_NO", "");
				recInTemp.setField("YD_STK_LYR_MTL_STAT", "E");
		 	/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrYdStkColGpYdStkBedNo*///TB_YD_STKLYR
				intRtnVal = ydStkLyrDao.updYdStklyrYdStkColGpBedNo(recInTemp);
				if(intRtnVal <= 0) {
					szLogMsg = " 적치단 정보 활성화중 Error!! ";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szLogMsg);
					return outRecord;
				}
			
			}
			
			//목적동 활성화
			sYD_STK_COL_GP = sYD_GP + szNewCurrBayGp + sYD_EQP_ID.substring(2, 6); // 목적동		
			recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_STK_COL_GP", sYD_STK_COL_GP);
	    	
			rsResult1 = JDTORecordFactory.getInstance().createRecordSet("");
			/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedBed*/
			intRtnVal = ydStkBedDao.getYdStkbed(recInTemp, rsResult1, 1 );
			if(intRtnVal <= 0) {
				szLogMsg="bed 정보 이상";
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szLogMsg);
				return outRecord;

    		}
			
			for(int Loop_i = 1; Loop_i <= rsResult1.size(); Loop_i++) {
				rsResult1.absolute(Loop_i);
				recInTemp1  = JDTORecordFactory.getInstance().create();
	    		recInTemp1.setRecord(rsResult1.getRecord());		
				
	    		szStkBedNo = ydDaoUtils.paraRecChkNull(recInTemp1, "YD_STK_BED_NO");
    		
		    	recInTemp = JDTORecordFactory.getInstance().create();
		    	recInTemp.setField("YD_STK_COL_GP", sYD_STK_COL_GP);
		    	recInTemp.setField("YD_STK_BED_NO", szStkBedNo);
		    	recInTemp.setField("YD_STK_BED_ACT_STAT", "L");
		    	/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.updYdStkbed*///TB_YD_STKBED
		    	intRtnVal = ydStkBedDao.updYdStkbed(recInTemp, 0);
				if(intRtnVal <= 0) {
					szLogMsg="bed 정보 수정시 이상";
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szLogMsg);
					return outRecord;
	    		}
				
		    	//상차정지위치 단정보 Clear, 
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_STK_COL_GP", sYD_STK_COL_GP);
				recInTemp.setField("YD_STK_BED_NO", szStkBedNo);
				recInTemp.setField("STL_NO", "");
				recInTemp.setField("MODIFIER", "SYSTEM");
				recInTemp.setField("YD_STK_LYR_ACT_STAT", "E");
				recInTemp.setField("YD_STK_LYR_MTL_STAT", "E");
				/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrYdStkColGpYdStkBedNo*///TB_YD_STKLYR
				intRtnVal = ydStkLyrDao.updYdStklyrYdStkColGpBedNo(recInTemp);
				if(intRtnVal <= 0) {
					szLogMsg = " 적치단 정보 활성화중 Error!! ";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szLogMsg);
					return outRecord;
				}
			}	    	
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_EQP_ID"		, sYD_EQP_ID);
			recInTemp.setField("YD_CURR_BAY_GP"	, szNewCurrBayGp);
			intRtnVal = ydEqpDao.updYdEqp(recInTemp, 0);
    		if(intRtnVal <= 0) {
    			szLogMsg = " 설비현재동 수정 Error!! ";
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szLogMsg);
				return outRecord;
    		}

			outRecord.setField("RTN_CD" 	, "1");	
			return outRecord;

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
		}

	}
	
	/**
	 *   HOME 동 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return String
	 * @throws DAOException
	 */
	public JDTORecord CoilYdTcarStsSetTcarF(JDTORecord[] inDto) throws DAOException {
		String szLogMsg = null;
		JDTORecord recPara = null;	
		YdEqpDao ydEqpDao = new YdEqpDao();
		JDTORecord 		outRecord 			= JDTORecordFactory.getInstance().create();
		JDTORecord 		outRecord1 			= JDTORecordFactory.getInstance().create();
		String szMethodName = "CoilYdTcarStsSetTcarF";
		String szOperationName = "HOME 동 변경";
		int intRtnVal          = 0;
		String sRTN_CD	= "";
		String sRTN_MSG = "";

		try{
			
			szLogMsg = "JSP-SESSION [ HOME 동 변경] 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);

			 // @param JDTORecord : YD_GP :야드구분(J,H), 
			 //                     T_CAR_CD : 대차ID(JXTC01, JXTC02), 
			 //                     FROM_BAY_GP : 현재동(D,E,F,G,H), 
			 //                     TO_BAY_GP : 이적동(D,E,F,G,H), 
			recPara   = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_GP"		, yddatautil.setDataDefault(inDto[0].getField("YD_GP"),""));
			recPara.setField("T_CAR_CD"		, yddatautil.setDataDefault(inDto[0].getField("YD_EQP_ID"),""));
			recPara.setField("FROM_BAY_GP"	, yddatautil.setDataDefault(inDto[0].getField("NEW_YD_CURR_BAY_GP"),""));
			recPara.setField("TO_BAY_GP"	, yddatautil.setDataDefault(inDto[0].getField("YD_HOME_BAY_GP"),""));
			recPara.setField("HOME_CHK"		, "Y");
			
			outRecord1 	= (JDTORecord)this.getTCarBayChk(recPara); 
			sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
			sRTN_MSG 	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
			if (!("1".equals(sRTN_CD))) {
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, sRTN_MSG);	
				return outRecord;
			}

			recPara   = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_EQP_ID",     yddatautil.setDataDefault(inDto[0].getField("YD_EQP_ID"),""));
			recPara.setField("YD_HOME_BAY_GP",yddatautil.setDataDefault(inDto[0].getField("YD_HOME_BAY_GP"),""));
			ydUtils.displayRecord(szOperationName, recPara);
			intRtnVal = ydEqpDao.updYdEqp(recPara, 0);
    		if(intRtnVal <= 0) {
    			szLogMsg = " 설비현재동 수정 Error!! ";
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szLogMsg);
				return outRecord;
    		}

			outRecord.setField("RTN_CD" 	, "1");	
			return outRecord;

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
		}
	}
	
	
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
		//YdStockDao 		ydStockDao 		= new YdStockDao();
		
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils           = new YdDaoUtils();
		//공용 METHOD
		//YdUtils ydutils                 = new YdUtils();
		
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
					
				} else if(YdConstant.YD_GP_PORT_SLAB_YARD.equals(szYD_GP)){  //항만슬라브야드 기능추가 - 2015.12.31 LeeJY
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
		boolean blnRtnVal         = true;
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
			blnRtnVal = false;
			return blnRtnVal;
		}
		
		szMsg = "JSP-SESSION [스케줄기준 체크 및 데이터 반환]끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

		return blnRtnVal;
		
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
		    /*com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpDaoCarSchInfoCoil*/
		    intRtnVal = ydEqpDao.getYdEqp(recPara, outRecSet, 302);
						
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
	 * 야드현황조회1
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getYdMgtList1(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		
		String szMsg        	= "";
		String szMethodName 	= "getYdMgtList1";	
		String szOperationName 	= "야드현황조회1";
		String sDD_CHK 			= "";
		
		YdEqpDao ydEqpDao = new YdEqpDao();
		
		int intRtnVal = 0;
		
		try {		
				    
			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			sDD_CHK =  ydDaoUtils.paraRecChkNull(inDto, "DD_CHK");  // 기준일 CHECK
			
			if(sDD_CHK.equals("N")) {   							// 현재
				recPara         = JDTORecordFactory.getInstance().create();
			    recPara.setField("YD_GP",      ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));
			    /*com.inisteel.cim.yd.dao.ydeqpdao.getYdMgtList1*/
			    intRtnVal = ydEqpDao.getYdEqp(recPara, outRecSet, 801);
			} else {												// 기준일 
				recPara         = JDTORecordFactory.getInstance().create();
			    recPara.setField("YD_GP"		, ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));
			    recPara.setField("YD_INV_DATE"	, ydDaoUtils.paraRecChkNull(inDto, "YD_WRK_HDS_DD"));
			    /*com.inisteel.cim.yd.dao.ydeqpdao.getYdMgtList3*/
			    intRtnVal = ydEqpDao.getYdEqp(recPara, outRecSet, 901);
			}
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
	}//end of getYdMgtList1
	
	
	
	/**
	 * 야드현황조회1
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getYdTotalMgtList(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		
		String szMsg        	= "";
		String szMethodName 	= "getYdTotalMgtList";	
		String szOperationName 	= "야드현황조회1";
		String sDD_CHK 			= "";
		String sDD_CHK2 		= "";
		String sDD_CHK3 		= "";
		
		YdEqpDao ydEqpDao = new YdEqpDao();
		
		int intRtnVal = 0;
		
		try {		
				    
			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			sDD_CHK =  ydDaoUtils.paraRecChkNull(inDto, "DD_CHK");  // 기준일 CHECK
			sDD_CHK2 =  ydDaoUtils.paraRecChkNull(inDto, "DD_CHK2");  // 기준일 CHECK2
			sDD_CHK3 =  ydDaoUtils.paraRecChkNull(inDto, "DD_CHK3");  // 적치기준 CHECK3
			
			if(sDD_CHK.equals("N")) {   							// 현재
				recPara         = JDTORecordFactory.getInstance().create();
				recPara.setField("CHK"			, sDD_CHK3);
				if(sDD_CHK2.equals("N")) { 
//PIDEV_S :병행가동용:PI_YD
					recPara.setField("PI_YD",    	"J");						
				    /*com.inisteel.cim.yd.dao.ydeqpdao.getYdTotalMgtList_PIDEV*/
				    intRtnVal = ydEqpDao.getYdEqp(recPara, outRecSet, 904);
				}else{
//PIDEV_S :병행가동용:PI_YD
					recPara.setField("PI_YD",    	"J");					
					/*com.inisteel.cim.yd.dao.ydeqpdao.getYdTotalMgtList2_PIDEV*/
				    intRtnVal = ydEqpDao.getYdEqp(recPara, outRecSet, 906);
				}
			} else {												// 기준일 
				recPara         = JDTORecordFactory.getInstance().create();
				recPara.setField("CHK"			, sDD_CHK3);
			    recPara.setField("YD_INV_DATE"	, ydDaoUtils.paraRecChkNull(inDto, "YD_WRK_HDS_DD"));
			    
			    if(sDD_CHK2.equals("N")) { 
				    /*com.inisteel.cim.yd.dao.ydeqpdao.getYdTotalMgtListXL*/
				    intRtnVal = ydEqpDao.getYdEqp(recPara, outRecSet, 905);
			    }else{
			    	/*com.inisteel.cim.yd.dao.ydeqpdao.getYdTotalMgtListXL2*/
				    intRtnVal = ydEqpDao.getYdEqp(recPara, outRecSet, 907);
			    }
			}
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
	}//end of getYdTotalMgtList
	
	
	/**
	 * 야드현황조회2
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getYdMgtList2(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		
		String      szMsg        = "";
		String      szMethodName = "getYdMgtList2";	
		String szOperationName = "야드현황조회2";
	    YdEqpDao ydEqpDao = new YdEqpDao();
	    String sDD_CHK 			= "";
		int intRtnVal = 0;
		
		try {		
				    
			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			sDD_CHK =  ydDaoUtils.paraRecChkNull(inDto, "DD_CHK");
			
			if(sDD_CHK.equals("N")) {
				recPara         = JDTORecordFactory.getInstance().create();
			    recPara.setField("YD_GP",      ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));
			    /*com.inisteel.cim.yd.dao.ydeqpdao.getYdMgtList2*/
			    intRtnVal = ydEqpDao.getYdEqp(recPara, outRecSet, 802);
			} else {
				recPara         = JDTORecordFactory.getInstance().create();
			    recPara.setField("YD_GP"			, ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));
			    recPara.setField("YD_INVGRP_DATE"	, ydDaoUtils.paraRecChkNull(inDto, "YD_WRK_HDS_DD"));
			    /*com.inisteel.cim.yd.dao.ydeqpdao.getYdMgtList4*/
			    intRtnVal = ydEqpDao.getYdEqp(recPara, outRecSet, 902);
			}			
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
	}//end of getYdMgtList1
	
	/**
	 * 야드현황조회5
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getYdMgtList5(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		
		String szMsg        	= "";
		String szMethodName 	= "getYdMgtList5";	
		String szOperationName 	= "야드현황조회5";
	    YdEqpDao ydEqpDao = new YdEqpDao();
	    String sDD_CHK 			= "";
		int intRtnVal = 0;
		
		try {		
				    
			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			recPara         = JDTORecordFactory.getInstance().create();
		    recPara.setField("YD_GP",      "H");
		    /*com.inisteel.cim.yd.dao.ydeqpdao.getYdMgtList5*/
		    intRtnVal = ydEqpDao.getYdEqp(recPara, outRecSet, 903);

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
	}//end of getYdMgtList5
	
	
	
	/**
	 * 대차  작업 대기 현황
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getTCarWrkWaitListCoil(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		
		String      szMsg        = "";
		String      szMethodName = "getTCarWrkWaitListCoil";	  
		String szOperationName="대차  작업 대기 현황";
	    
	    YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
		
		int intRtnVal = 0;
		
		try {
			
			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
				    
			recPara.setField("YD_WRK_PLAN_TCAR"	, ydDaoUtils.paraRecChkNull(inDto, "YD_WRK_PLAN_TCAR"));
			recPara.setField("YD_GP"			, ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));
			/*com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getTCarWrkWaitListCoil*/
		    intRtnVal = ydWrkbookDao.getYdWrkbook(recPara, outRecSet, 302);
						
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
	
	
	
	/**
	 *  야드관리 > 코일제품창고 > 설비관리 > 대차스케줄관리  --> 대차이동구간변경 팝업 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getTCarYdGpMgt(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		
		String      szMethodName = "getTCarYdGpMgt";	  
		CoilGdsJspDao dao = new CoilGdsJspDao();
		
		try {
			outRecSet = dao.getTCarYdGpMgt(recPara);
			
			if (outRecSet != null && outRecSet.size() < 1) {
				
				ydUtils.putLog(szSessionName, szMethodName, "조회된 데이터가 없습니다.", YdConstant.INFO);
				
				return outRecSet;
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
		return outRecSet;
	}//end of getTCarSchWrkMtl
	
	
	
	
	/**
	 * 이적/이송 대상 대차상태 체크 
	 * @ejb.interface-method
	 * @param JDTORecord[]
	 * @return JDTORecord
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.25
	 */
	public JDTORecord getStkColTCarChk(JDTORecord []  inDto) throws DAOException{
	
		JDTORecord 			outRecord 		= JDTORecordFactory.getInstance().create(); // 
		JDTORecord 			recPara			= JDTORecordFactory.getInstance().create();
		JDTORecord 			recTmp			= JDTORecordFactory.getInstance().create();
		JDTORecordSet   	outRecSet  		= null;
		
		CoilGdsJspDao		dao 			= new CoilGdsJspDao();
		
		String				sFROM			= "";
		String				sTO				= "";
		String				sTcar			= "";
		String 				sFrombayGp 		= ""; // from 동구분
		String 				sTobayGp 		= ""; // to 동구분
		
		try{

			for(int i = 0; i < inDto.length; i++ ) {
				sFROM = ydDaoUtils.paraRecChkNull(inDto[i], "FROM_YD_STK_COL_GP");
				sTO = ydDaoUtils.paraRecChkNull(inDto[i], "TO_YD_STK_COL_GP");
				sTcar = ydDaoUtils.paraRecChkNull(inDto[i], "YD_WRK_PLAN_TCAR");
				sFrombayGp = sFROM.substring(1, 2);
				sTobayGp = sTO.substring(1, 2);
				
				if(!sFrombayGp.equals(sTobayGp)){ // 같은 동일때는 대차가 필요 없음
					// **** 선택된 대차로 체크 ****
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("V_YD_EQP_ID", sTcar);

					outRecSet = dao.getTCarInfo(recPara);

					if(outRecSet == null && outRecSet.size() == 0){
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, "대차상태 조회에 실패 하였습니다.");	
						return outRecord;
					}else{
						outRecSet.first();
						recTmp = outRecSet.getRecord(0);	

						if(!recTmp.getFieldString("YD_GP").equals(sFROM.substring(0, 1))){ //현재 대차가 위치한 야드 체크  
							// 현재 야드에 없을시 에러리턴
							outRecord.setField("RTN_CD" 	, "-1");	
							outRecord.setField("RTN_MSG" 	, "현재 야드에 대차가 존재 하지 않습니다.");	
							return outRecord;
						}
						// 대차 이동구간 체크 
						int fn = recTmp.getFieldString("YD_TCAR_WRK_ABLE_BAY").indexOf(sFrombayGp);
						int tn = recTmp.getFieldString("YD_TCAR_WRK_ABLE_BAY").indexOf(sTobayGp);
						if( fn < 0 || tn < 0){
							// from동과 To동이 대차 이동구간에 포함 되지 않을 경우 error 
							outRecord.setField("RTN_CD" 	, "-2");	
							outRecord.setField("RTN_MSG" 	, "설정된 대차 이동구간이 일치하지 않습니다.<br>이동가능구간 : "+recTmp.getFieldString("YD_TCAR_WRK_ABLE_BAY"));	
							return outRecord;
						}
						//recTmp.getFieldString("YD_CURR_BAY_GP"); // 대차의 현재 위치
					}
				}
			}
			outRecord.setField("RTN_CD" 	, "1");	
			outRecord.setField("RTN_MSG" 	, "");
			
		}catch (Exception e){
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		return outRecord;
		
	}// end getStkColTCarChk
	
	/**
	 * 이적/이송 대상 외경군, 폭구분 체크 
	 * @ejb.interface-method
	 * @param JDTORecord[]
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.25
	 */
	public JDTORecordSet getStkColWidthGp(JDTORecord  inDto) throws DAOException{
	
		JDTORecord 			recPara			= JDTORecordFactory.getInstance().create();
		JDTORecordSet   	outRecSet  		= null;
		
		CoilGdsJspDao		dao 			= new CoilGdsJspDao();

		
		try{


			//////////////////////////////////////////////////////////////////////////////////////////////////////
			// 폭구분, 외경군 체크 
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("V_YD_STK_COL_GP",		ydDaoUtils.paraRecChkNull(inDto, "V_YD_STK_COL_GP"));

			outRecSet = dao.getStkColWidthGp(recPara);
			
		
		}catch (Exception e){
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		return outRecSet;
		
	}// end getStkColWidthGp
	
	
	/**
	 *  위치검색 범위 조회 (화면:위치검색순서관리) 하단 왼쪽 그리드 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.07.05
	 */
	public GridData getSpanbyLowInfo(GridData inDto){
		GridData rtnGrd = new GridData();
		JDTORecordSet    outRecSet  = null;
				
		CoilGdsJspDao dao = new CoilGdsJspDao();
		JDTORecord 		recPara		= null;
		
		try {
			
			recPara		= JDTORecordFactory.getInstance().create(); 			
			recPara.setField("V_YD_STR_GTR_CD1"	, inDto.getParam("YD_STR_GTR_CD").trim());
			recPara.setField("V_YD_SCH_CD"	, 	  inDto.getParam("YD_SCH_CD").trim());
			recPara.setField("V_YD_ROUTE_GP"	, inDto.getParam("YD_ROUTE_GP").trim());
			recPara.setField("V_YD_STR_GTR_CD2"	, inDto.getParam("YD_STR_GTR_CD").trim());
			
			outRecSet = dao.getSpanbyLowInfo(recPara);
			
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
	}//end of getSpanbyLowInfo
	
	
	/**
	 * 야드관리 > 코일제품창고 > 기준관리 > 저장위치용도관리  목록조회
	 *
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.07.05
	 */
	public GridData getStrlocUsgSetList(GridData inDto) {
		GridData 		rtnGrd 		= new GridData();
		JDTORecordSet   outRecSet  	= null;
		
		CoilGdsJspDao 	dao 		= new CoilGdsJspDao();
		JDTORecord 		recPara		= null;
		
		try {
			
			// 파라미터 셋팅 
			recPara		= JDTORecordFactory.getInstance().create(); 			
			recPara.setField("V_YD_GP"		, inDto.getParam("YD_GP").trim());		/*야드구분*/
			recPara.setField("V_YD_BAY_GP"	, inDto.getParam("YD_BAY_GP").trim());	/*동구분*/

			// DAO 호출
			outRecSet = dao.getStrlocUsgSetList(recPara);
			
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
	}//end of getStrlocUsgSetList
	
	
	/**
	 * 야드관리 > 코일제품창고 > 기준관리 > 저장위치용도관리  등록
	 *
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.07.05
	 */
	public GridData updStrlocUsgSet(GridData inDto) {
		GridData 		rtnGrd 		= new GridData();
		int 			ret			= 0;
		int 			res			= 0;
		CoilGdsJspDao 	dao 		= new CoilGdsJspDao();
		JDTORecord 		recPara		= null;
		
		try {
			
			 
			String headerName = "";
			int iCellCnt = Integer.parseInt(StringHelper.evl(inDto.getParam("CELL_CNT"),"0"));
			for(int j=1; j<=iCellCnt; j++){ //cell loop
				for(int i=0; i<inDto.getHeader("CHECK").getRowCount(); i++){ // row loop
					headerName = "SPAN"+((j+"").length()==1 ? "0"+j:""+j);
					if(inDto.getHeader(headerName).getHiddenValue(i).equals("U")){
						//파라미터 셋팅 
						recPara	= JDTORecordFactory.getInstance().create();
						// 변경값이 'M'이거나 'W'일 경우 폭구분 동기화  
						if(inDto.getHeader(headerName).getValue(i).equals("M")
								|| inDto.getHeader(headerName).getValue(i).equals("W")){
							recPara.setField("V_YD_STKBED_USG_CD",	inDto.getHeader(headerName).getValue(i).trim()); 	// 야드적채대용도코드
							recPara.setField("YD_STK_COL_W_GP",		inDto.getHeader(headerName).getValue(i).trim()); 	// 폭구분
							recPara.setField("V_MODIFIER", 			inDto.getParam("YD_USER_ID").trim()); 				// 수정자
							recPara.setField("V_YD_GP", 			inDto.getHeader("YD_GP").getValue(i).trim());		// 야드
							recPara.setField("V_YD_BAY_GP", 		inDto.getHeader("YD_BAY_GP").getValue(i).trim());	// 동
							recPara.setField("V_YD_EQP_GP", 		((j+"").length()==1 ? "0"+j:""+j));					// 스판
							recPara.setField("V_YD_STK_COL_NO", 	inDto.getHeader("YD_STK_COL_NO").getValue(i).trim());	// 열
							// DAO 호출							
							ret = dao.updStrlocUsgSet2(recPara);
							
							if(ret > 0){
								res+= ret;
							}else{
								System.out.println("\n\n===============저장 실패 2 =============\n\n");
								rtnGrd.addParam("ret", "-2");
								rtnGrd.setStatus("false");
								rtnGrd.setMessage("저장위치대용도코드 등록에 실패 하였습니다");
								return rtnGrd;
							}
							
						}else{
							recPara.setField("V_YD_STKBED_USG_CD",	inDto.getHeader(headerName).getValue(i).trim()); 	// 야드적채대용도코드
							recPara.setField("V_MODIFIER", 			inDto.getParam("YD_USER_ID").trim()); 				// 수정자
							recPara.setField("V_YD_GP", 			inDto.getHeader("YD_GP").getValue(i).trim());		// 야드
							recPara.setField("V_YD_BAY_GP", 		inDto.getHeader("YD_BAY_GP").getValue(i).trim());	// 동
							recPara.setField("V_YD_EQP_GP", 		((j+"").length()==1 ? "0"+j:""+j));					// 스판
							recPara.setField("V_YD_STK_COL_NO", 	inDto.getHeader("YD_STK_COL_NO").getValue(i).trim());	// 열
							// DAO 호출
							ret = dao.updStrlocUsgSet1(recPara);
							
							if(ret > 0){
								res+= ret;
							}else{
								System.out.println("\n\n===============저장 실패 1 =============\n\n");
								rtnGrd.addParam("ret", "-1");
								rtnGrd.setStatus("false");
								rtnGrd.setMessage("저장위치대용도코드 등록에 실패 하였습니다");
								return rtnGrd;
							}
						}
					}

				}
			}

			
			rtnGrd.addParam("ret", ""+res);
			rtnGrd.setStatus("true");
			rtnGrd.setMessage(res+"건 정상 등록되었습니다.");
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return rtnGrd;
	}//end 
	
	
	
	/**
	 * 야드관리 > 코일제품창고 > 기준관리 > 위치검색순서관리   적치구분 콤보리스트 조회 
	 *
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.07.07
	 */
	public GridData getYDB700ComboList(GridData inDto) {
		GridData 		rtnGrd 		= new GridData();
		JDTORecordSet   outRecSet  	= null;
		
		CoilGdsJspDao 	dao 		= new CoilGdsJspDao();
		JDTORecord 		recPara		= null;
		
		try {
			
			// 파라미터 셋팅 
			recPara		= JDTORecordFactory.getInstance().create(); 	
			// DAO 호출
			outRecSet = dao.getYDB700ComboList(recPara);
			
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
	}//end of getYDB700ComboList
	
	
	/**
	 * 대차 이동 가능 여부 채크 
	 * @ejb.interface-method
	 * @param JDTORecord : YD_GP :야드구분(J,H), 
	 *                     T_CAR_CD : 대차ID(JXTC01, JXTC02), 
	 *                     FROM_BAY_GP : 현재동(D,E,F,G,H), 
	 *                     TO_BAY_GP : 이적동(D,E,F,G,H), 
	 * @return JDTORecord : RTN_CD (error : 0,-1,-2,-3),(정상 : 1), 
	 *                      RTN_MSG (error : 0 = 대차조회실패, -1 = 같은동일때, -2 = 현재야드에대차 없음, -3 = 이동구간 불일치)
	 * @작성자 : 박지열
	 * @작성일 : 2010.07.07
	 */
	public JDTORecord getTCarBayChk(JDTORecord inDto) {
		CoilJspDAO 		dao 			= new CoilJspDAO();
		JDTORecord 			outRecord 		= JDTORecordFactory.getInstance().create();  
		JDTORecord 			recPara			= JDTORecordFactory.getInstance().create();
		JDTORecord 			recTmp			= JDTORecordFactory.getInstance().create();
		JDTORecordSet   	outRecSet  		= null;
		
		String 				sTcar			= "";
		String 				sFrombayGp		= "";
		String 				sTobayGp		= "";
		String 				ydGp			= "";
		String 				sHOME_CHK			= "";
		
		try {
			
			ydGp 		= inDto.getFieldString("YD_GP");
			sTcar 		= inDto.getFieldString("T_CAR_CD");
			sFrombayGp 	= inDto.getFieldString("FROM_BAY_GP");
			sTobayGp 	= inDto.getFieldString("TO_BAY_GP");
			sHOME_CHK 	= inDto.getFieldString("HOME_CHK");
			if(sFrombayGp.equals(sTobayGp)){
				if(sHOME_CHK.equals("Y")){
					
				} else {
					outRecord.setField("RTN_CD" 	, "-1");	
					outRecord.setField("RTN_MSG" 	, "같은동에 이적시에는 대차를 사용하지않습니다.");	
					return outRecord;
				}	
			}
			
//			if(sFrombayGp.equals(sTobayGp)){
//				outRecord.setField("RTN_CD" 	, "-1");	
//				outRecord.setField("RTN_MSG" 	, "같은동에 이적시에는 대차를 사용하지않습니다.");	
//				return outRecord;
//			}
			
			// **** 선택된 대차로 체크 ****
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("V_YD_EQP_ID", sTcar);
			
			outRecSet = dao.getTCarInfo(recPara);
			
			if(outRecSet == null && outRecSet.size() == 0){
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, "대차상태 조회에 실패 하였습니다.");	
				return outRecord;
			}else{
				outRecSet.first();
				recTmp = outRecSet.getRecord(0);	

				if(!recTmp.getFieldString("YD_GP").equals(ydGp)){ //현재 대차가 위치한 야드 체크  
					// 현재 야드에 없을시 에러리턴
					outRecord.setField("RTN_CD" 	, "-1");	
					outRecord.setField("RTN_MSG" 	, "현재 야드에 대차가 존재 하지 않습니다.");	
					return outRecord;
				}
				// 대차 이동구간 체크 
				int fn = recTmp.getFieldString("YD_TCAR_WRK_ABLE_BAY").indexOf(sFrombayGp);
				int tn = recTmp.getFieldString("YD_TCAR_WRK_ABLE_BAY").indexOf(sTobayGp);
				if( fn < 0 || tn < 0){
					// from동과 To동이 대차 이동구간에 포함 되지 않을 경우 error 
					outRecord.setField("RTN_CD" 	, "-2");	
					outRecord.setField("RTN_MSG" 	, "설정된 대차 이동구간이 일치하지 않습니다.<br>이동가능구간 : "+recTmp.getFieldString("YD_TCAR_WRK_ABLE_BAY"));	
					return outRecord;
				}
				//recTmp.getFieldString("YD_CURR_BAY_GP"); // 대차의 현재 위치
			}
			
			outRecord.setField("RTN_CD" 	, "1");	
			outRecord.setField("RTN_MSG" 	, "설정가능한 대차 입니다.");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return outRecord;
		
	}
	
	/**
	 *  차량작업관리 배차내역  조회 코일 
	 * 심명순 (090723)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdGdsOutCarCoil(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";
		//String szTemp       = "";
		String szWLOC_CD       = "";
		String szMethodName = "getCoilYdGdsOutCarCoil";
		String szYD_CAR_STOP_LOC = "";

		YdCarSchDao ydCarSchDao = new YdCarSchDao(); 
		
		int intRtnVal = 0;
		
		try {

			szWLOC_CD = yddatautil.setDataDefault(inDto.getField("WLOC_CD2"), "");
			
			szYD_CAR_STOP_LOC = yddatautil.setDataDefault(inDto.getField("CAR_POINT"), "");
			if( szYD_CAR_STOP_LOC.equals("") ) {
				szYD_CAR_STOP_LOC = yddatautil.setDataDefault(inDto.getField("BAY2"), "");
				if( szYD_CAR_STOP_LOC.length() < 2 ) {
					szYD_CAR_STOP_LOC = "";
				}
			}
			recPara.setField("WLOC_CD", szWLOC_CD);
			recPara.setField("YD_CAR_STOP_LOC", szYD_CAR_STOP_LOC);
			
			/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarAsgnInfoByWlocCd2*/
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, outRecSet, 309);
		
			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "오류발생[1] - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}else if (intRtnVal == -2) {
					szMsg = "오류발생[2] - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}else{
					szMsg = "오류발생[3] - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			}else if(intRtnVal == 0) {
				szMsg = "대상재가 존재하지 않습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,  YdConstant.DEBUG);
			}// end of if
			
			logger.println(LogLevel.DEBUG_TEXT, "getCoilYdGdsOutCarCoil");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilYdGdsOutCar	
	
	/**
	 *  차량작업관리 배차내역  조회 코일 
	 * 심명순 (090723)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdGdsOutCarCoilNEW(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";
		//String szTemp       = "";
		String szWLOC_CD       = "";
		String szMethodName = "getCoilYdGdsOutCarCoilNEW";
		String szYD_CAR_STOP_LOC = "";

		YdCarSchDao ydCarSchDao = new YdCarSchDao(); 
		
		int intRtnVal = 0;
		
		try {

			szWLOC_CD = yddatautil.setDataDefault(inDto.getField("WLOC_CD2"), "");
			
			szYD_CAR_STOP_LOC = yddatautil.setDataDefault(inDto.getField("CAR_POINT"), "");
			if( szYD_CAR_STOP_LOC.equals("") ) {
				szYD_CAR_STOP_LOC = yddatautil.setDataDefault(inDto.getField("BAY2"), "");
				if( szYD_CAR_STOP_LOC.length() < 2 ) {
					szYD_CAR_STOP_LOC = "";
				}
			}
			recPara.setField("WLOC_CD", szWLOC_CD);
			recPara.setField("YD_CAR_STOP_LOC", szYD_CAR_STOP_LOC);
			
			/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getCoilYdGdsOutCarCoilNEW*/
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, outRecSet, 414);
		
			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "오류발생[1] - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}else if (intRtnVal == -2) {
					szMsg = "오류발생[2] - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}else{
					szMsg = "오류발생[3] - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			}else if(intRtnVal == 0) {
				szMsg = "대상재가 존재하지 않습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,  YdConstant.DEBUG);
			}// end of if
			
			logger.println(LogLevel.DEBUG_TEXT, "getCoilYdGdsOutCarCoilNEW");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilYdGdsOutCarCoilNEW
	
	/**
	 *  차량작업관리 배차내역  조회 코일 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdGdsOutCarCoilNEW2(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";
		//String szTemp       = "";
		String szMethodName = "getCoilYdGdsOutCarCoilNEW2";
		String szYD_GP       = "";
		String szBAY_GP      = "";		
		String szLDTRN_CAR_STAT_CD = "";
		String szORDERBY	 = "";

		YdCarSchDao ydCarSchDao = new YdCarSchDao(); 
		
		int intRtnVal = 0;
		
		try {

			szYD_GP = yddatautil.setDataDefault(inDto.getField("YD_GP"), "");
			szBAY_GP = yddatautil.setDataDefault(inDto.getField("BAY_GP"), "");
			szLDTRN_CAR_STAT_CD = yddatautil.setDataDefault(inDto.getField("LDTRN_CAR_STAT_CD"), "");
			szORDERBY = yddatautil.setDataDefault(inDto.getField("ORDERBY"), "");
 
			recPara.setField("YD_GP",  szYD_GP);
			recPara.setField("BAY_GP", szBAY_GP);
			recPara.setField("LDTRN_CAR_STAT_CD", szLDTRN_CAR_STAT_CD);
			recPara.setField("ORDERBY", szORDERBY);
			
			/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getCoilYdGdsOutCarCoilNEW2_PIDEV*/

//PIDEV_S :병행가동용:PI_YD
//			recPara.setField("PI_YD",    	szYD_GP);			
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, outRecSet, 426);
		
			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "오류발생[1] - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}else if (intRtnVal == -2) {
					szMsg = "오류발생[2] - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}else{
					szMsg = "오류발생[3] - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				//return outRecSet;
				return outRecSet;
			}else if(intRtnVal == 0) {
				szMsg = "대상재가 존재하지 않습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg,  YdConstant.DEBUG);
			}// end of if
			
			logger.println(LogLevel.DEBUG_TEXT, "getCoilYdGdsOutCarCoilNEW");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilYdGdsOutCarCoilNEW2
	
	/**
	 *  차량작업관리 차량스케줄 조회 코일
	 * 심명순 (090727)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdGdsCarSchCoil(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		//String szTemp     ="";
		String szMethodName = "getCoilYdGdsCarSchCoil";
		String szYdBay = null;
		String szYd = "";
		String szBay = "";
		String szWLOC_CD = "";

		YdCarSchDao ydCarSchDao = new YdCarSchDao(); 
		
		int intRtnVal = 0;
		
		try {
					
			szWLOC_CD = yddatautil.setDataDefault(inDto.getField("WLOC_CD"), "");
			szYdBay   = yddatautil.setDataDefault(inDto.getField("BAY"), "");
			
			if( szYdBay.length() > 2 ) {
				szYd = szYdBay.substring(0, 1);
				szBay = szYdBay.substring(1, 3);
			}else if( szYdBay.length() > 1 ) {
				szYd = szYdBay.substring(0, 1);
				szBay = szYdBay.substring(1, 2);
			}else{
				szYd = szYdBay;
				szBay = "";
			}
			
			recPara.setField("WLOC_CD",szWLOC_CD);
			recPara.setField("YD_GP", szYd);
			recPara.setField("YD_BAY_GP", szBay);
			
			szMsg = "개소코드  : " + szWLOC_CD + ", 야드구분 : " + szYd + ", 동구분 : " + szBay;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			if(szYd.equals("H")) {
	            /*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarSchByWlocCdCoil_H*/
				intRtnVal = ydCarSchDao.getYdCarsch(recPara, outRecSet, 301);
			} else {
	            /*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarSchByWlocCdCoil_J_PIDEV*/
//PIDEV_S :병행가동용:PI_YD
				recPara.setField("PI_YD",    	szYd);						
				intRtnVal = ydCarSchDao.getYdCarsch(recPara, outRecSet, 308);
			}	
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
			//outRecSet.first();
			
			logger.println(LogLevel.DEBUG_TEXT, "getCoilYdGdsCarSchCoil");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilYdGdsCarSch
	
	
	/**
	 *  차량작업관리 차량스케줄 조회 코일
	 * 심명순 (090727)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdGdsCarSchCoilNEW(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		//String szTemp     ="";
		String szMethodName = "getCoilYdGdsCarSchCoilNEW";
		String szYdBay = null;
		String szYd = "";
		String szBay = "";
		String szWLOC_CD = "";

		YdCarSchDao ydCarSchDao = new YdCarSchDao(); 
		
		int intRtnVal = 0;
		
		try {
					
			szWLOC_CD = yddatautil.setDataDefault(inDto.getField("WLOC_CD"), "");
			szYdBay   = yddatautil.setDataDefault(inDto.getField("BAY"), "");
			
			if( szYdBay.length() > 1 ) {
				if("1".equals(szYdBay.substring(1, 2)) || "2".equals(szYdBay.substring(1, 2))){
			 
					szBay = szYdBay;
				}else{
					//szYd = szYdBay.substring(0, 1);
					szBay = szYdBay.substring(1, 2);
				}
			}else{
				//수정
				//szYd = "";
				szBay = szYdBay;   
			}
			
			recPara.setField("WLOC_CD",szWLOC_CD);
			recPara.setField("YD_BAY_GP", szBay);
			
			szMsg = "개소코드  : " + szWLOC_CD + ", 동구분 : " + szBay;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

            /*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarSchByWlocCdCoil_NEW_PIDEV*/
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, outRecSet, 411);
		
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
			//outRecSet.first();
			
			logger.println(LogLevel.DEBUG_TEXT, "getCoilYdGdsCarSchCoil");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilYdGdsCarSch
		
	/**
	 * 코일야드 차량작업관리 - 차량작업상세내역
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilCarWorkCoil(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		YdUtils ydUtils = new YdUtils();
		
		String szMsg        = "";		
		String szMethodName = "getCoilCarWork";
		
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		
		int intRtnVal = 0;
		
		try {		
			recPara.setField("YD_CAR_USE_GP"	,yddatautil.setDataDefault(inDto.getField("YD_CAR_USE_GP"), ""));
			recPara.setField("TRN_EQP_CD"		,yddatautil.setDataDefault(inDto.getField("TRN_EQP_CD"), ""));
			recPara.setField("CAR_NO"			,yddatautil.setDataDefault(inDto.getField("CAR_NO"), ""));
			recPara.setField("CARD_NO"			,yddatautil.setDataDefault(inDto.getField("CARD_NO"), ""));
								
			intRtnVal  = ydWrkbookMtlDao.getYdWrkbookmtl(recPara, outRecSet, 403);
		
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
	 * 준비스케줄재료LIST - 상차LOT편성 시 사용 코일
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getYdPrepmtlNStockByPrepSchIdCoil(JDTORecord inDto) throws DAOException {
		//JDTO변수 정의
		JDTORecord       recPara        = null;
		JDTORecordSet    outRecSet      = null;
		//DAO 변수 정의
		YdPrepMtlDao  yPrepMtlDao  		= new YdPrepMtlDao();
		//기본변수 정의
		String      szMsg        		= "";
		String      szMethodName		= "getYdPrepmtlNStockByPrepSchIdCoil";
		String		szOperationName		= "준비스케줄재료LIST - 상차LOT편성";
		//로컬변수 정의
		String		szYD_PREP_SCH_ID			= null;
		
		int intRtnVal = 0;
		
		try {
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 : 파라미터 확인 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			ydUtils.displayRecord(szOperationName, inDto);
			
			szYD_PREP_SCH_ID   = ydDaoUtils.paraRecChkNull(inDto, "YD_PREP_SCH_ID");
			
			
			outRecSet      = JDTORecordFactory.getInstance().createRecordSet("YD");
			recPara        = JDTORecordFactory.getInstance().create();
		    recPara.setField("YD_PREP_SCH_ID",    	szYD_PREP_SCH_ID);
		    /*com.inisteel.cim.yd.dao.ydprepmtldao.YdPrepmtlDao.getYdPrepmtlNStockByPrepSchIdCoil*/
		    intRtnVal = yPrepMtlDao.getYdPrepmtl(recPara, outRecSet,300);
		
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
		
		szMsg = "JSP-SESSION [준비스케줄ID LIST] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return outRecSet;
	}//end of getYdPrepSchIdList			
	/**
	 * 포인트개폐처리 코일
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 */
	public void procCoilYdGdsPntUnitCLCoil(JDTORecord [] inDto) {
		int       	intRtnVal    		= 0;
		String    	szMsg        		= null;
		String    	szMethodName 		= "procCoilYdGdsPntUnitCLCoil";
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

		String szYD_STK_COL_GP			= null;
		String szYD_STK_COL_ACT_STAT	= null;
		String szYD_STK_COL_ACT_STAT_PARAM	= null;
		String szYD_STKBED_USG_CD_PARAM	= null;
		String szTRN_EQP_CD             = "";
		String szYD_CAR_USE_GP          = "";
		String szCAR_NO          		= "";
		String szCARD_NO          		= "";
		String szYD_GP					= "";
		String szTO_YD_STK_COL_GP ="";
		boolean isSendable				= true;
		JDTORecord	recInTemp			= JDTORecordFactory.getInstance().create();
		
		try {
			
			szMsg = "["+szOperationName+"] --------------- 메소드 시작 - 적치열건수["+inDto.length+"] ---------------";
			ydUtils.putLog(szSessionName, szMethodName,  szMsg, YdConstant.INFO);
			
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara  = JDTORecordFactory.getInstance().create();
			
			for(int x=0;x<inDto.length;x++){					
				//----------------------------------------------------------------------------------------------
				//	적치열 조회
				//----------------------------------------------------------------------------------------------
				
				szYD_STK_COL_GP 			= yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_GP"),"");
				szYD_STK_COL_ACT_STAT_PARAM = yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_ACT_STAT"),"");
				szYD_STKBED_USG_CD_PARAM 	= yddatautil.setDataDefault(inDto[x].getField("YD_STKBED_USG_CD"),"");
				szTRN_EQP_CD				= yddatautil.setDataDefault(inDto[x].getField("TRN_EQP_CD"),"");
				ydUtils.putLog(szSessionName, szMethodName,  "szYD_STKBED_USG_CD_PARAM" + szYD_STKBED_USG_CD_PARAM, YdConstant.INFO);
				
				
				recPara.setField("YD_STK_COL_GP",   		szYD_STK_COL_GP);
				
				szMsg = "["+szOperationName+"] 적치열["+szYD_STK_COL_GP+"] 조회 시작 ";
				ydUtils.putLog(szSessionName, szMethodName,  szMsg, YdConstant.DEBUG);
				
				szYD_GP = szYD_STK_COL_GP.substring(0 , 1);
				
				if(szYD_GP.equals("1")||szYD_GP.equals("3")){
					szRtnMsg = DaoManager.getYdStkcol(recPara, rsResult, 404);
				}else{
					szRtnMsg = DaoManager.getYdStkcol(recPara, rsResult, 0);
				}
				
				szMsg = "["+szOperationName+"] 적치열["+szYD_STK_COL_GP+"] 조회 완료 - 반환메세지 : " + szRtnMsg;
				ydUtils.putLog(szSessionName, szMethodName,  szMsg, YdConstant.DEBUG);
				
				if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
					continue;
				}
				
				rsResult.first();
				recTemp = rsResult.getRecord();
				szYD_STK_COL_ACT_STAT 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_COL_ACT_STAT");
				szCAR_NO 				= ydDaoUtils.paraRecChkNull(recTemp, "CAR_NO");
				szCARD_NO 				= ydDaoUtils.paraRecChkNull(recTemp, "CARD_NO");
				
				//----------------------------------------------------------------------------------------------
				//	적치열 수정
				//----------------------------------------------------------------------------------------------
				ydUtils.putLog(szSessionName, szMethodName,  "szYD_STKBED_USG_CD_PARAM:" + szYD_STKBED_USG_CD_PARAM, YdConstant.INFO);
				ydUtils.putLog(szSessionName, szMethodName,  "szTRN_EQP_CD:" + szTRN_EQP_CD, YdConstant.INFO);
				
				if("".equals(szTRN_EQP_CD)){ 
					//포인트 차량이 존재 안하는 경우 
					szTRN_EQP_CD  	= "";
					szYD_CAR_USE_GP = "";
					szCAR_NO 		= "";
					szCARD_NO 		= "";
					
				}else{				
					if(szTRN_EQP_CD.substring(0 , 1).equals("G") && !"TT".equals(szYD_STKBED_USG_CD_PARAM)){			 
						szCAR_NO 		= "";
						szCARD_NO 		= "";
					}else {
						szTRN_EQP_CD  ="";
						szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(recTemp, "YD_CAR_USE_GP");
						szCAR_NO 		= ydDaoUtils.paraRecChkNull(recTemp, "CAR_NO");
						szCARD_NO 		= ydDaoUtils.paraRecChkNull(recTemp, "CARD_NO");
					}
				}

//				if( szTRN_EQP_CD.equals("") ) {
//					szYD_CAR_USE_GP = "";
//					szCAR_NO 		= "";
//					szCARD_NO 		= "";
//				} else {
//					szYD_CAR_USE_GP = ydDaoUtils.paraRecChkNull(recTemp, "YD_CAR_USE_GP");
//					szCAR_NO 		= ydDaoUtils.paraRecChkNull(recTemp, "CAR_NO");
//					szCARD_NO 		= ydDaoUtils.paraRecChkNull(recTemp, "CARD_NO");
//				}		

				recPara.setField("YD_STK_COL_ACT_STAT"	, szYD_STK_COL_ACT_STAT_PARAM);
				recPara.setField("YD_STKBED_USG_CD"		, szYD_STKBED_USG_CD_PARAM);
				recPara.setField("MODIFIER"				, yddatautil.setDataDefault(inDto[x].getFieldString("YD_USER_ID"),""));
				recPara.setField("TRN_EQP_CD"			, szTRN_EQP_CD);
				recPara.setField("YD_CAR_USE_GP"		, szYD_CAR_USE_GP);
				recPara.setField("CAR_NO"				, szCAR_NO);
				recPara.setField("CARD_NO"				, szCARD_NO);
				
				if(szYD_GP.equals("1")||szYD_GP.equals("3")){
					
					recInTemp = JDTORecordFactory.getInstance().create();
			    	recInTemp.setField("STACK_COL_GP"      , szYD_STK_COL_GP);
			    	recInTemp.setField("STACK_COL_ACTIVE_STAT", "L");
			    	recInTemp.setField("YD_CAR_USE_GP"      , "G");
			    	recInTemp.setField("TRN_EQP_CD"         , szTRN_EQP_CD);
			    	recInTemp.setField("CAR_NO"             , szCAR_NO);
			    	recInTemp.setField("CARD_NO"            , szCARD_NO);
			    	
			    	intRtnVal = ydStkColDao.updYmStkcol(recInTemp, 0);
				}else{
				 
					intRtnVal = ydStkColDao.updYdStkcol(recPara,0);
				}
		        
				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "["+szOperationName+"] 적치열 수정, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "["+szOperationName+"] 적치열 수정 parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
				} // end of if
				
				//차량포인트 동기화
				/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.updydcarpoint*/
				
				if(szYD_GP.equals("1")||szYD_GP.equals("3")){
					intRtnVal = ydStkColDao.updydcarpoint(recPara, 0);
				}else{
					intRtnVal = ydStkColDao.updYdStkcol(recPara,1);
				}
		        
				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "["+szOperationName+"] 차량포인트 수정, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "["+szOperationName+"] 차량포인트 수정 parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
				} // end of if
				
				
				//사용불가 인경우 대기차량을 다른 포인트로 변경 한다. 2020.01.04
				if("N".equals(szYD_STK_COL_ACT_STAT_PARAM) && "J".equals(szYD_GP)){
					if("1".equals(szYD_STK_COL_GP.substring(5 , 6)) || "2".equals(szYD_STK_COL_GP.substring(5 , 6))){
						if("1".equals(szYD_STK_COL_GP.substring(5 , 6))){//1통로 인경우
							szTO_YD_STK_COL_GP =szYD_STK_COL_GP.substring(0 , 5)+"2";
						}else{
							szTO_YD_STK_COL_GP =szYD_STK_COL_GP.substring(0 , 5)+"1";
						}
					}else{
						if("4".equals(szYD_STK_COL_GP.substring(5 , 6))){ //2통로 인경우
							szTO_YD_STK_COL_GP =szYD_STK_COL_GP.substring(0 , 5)+"5";
						}else{
							szTO_YD_STK_COL_GP =szYD_STK_COL_GP.substring(0 , 5)+"4";
						}
					}
					
					YdPlateCommDAO commDao 	  = new YdPlateCommDAO();
					
					recInTemp = JDTORecordFactory.getInstance().create();
			    	recInTemp.setField("STACK_COL_GP"      , szYD_STK_COL_GP);			//FROM 차량위치
			    	recInTemp.setField("TO_YD_STK_COL_GP"      , szTO_YD_STK_COL_GP);//TO 차량위치
		 
			    	intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.yd.jsp.coiljsp.session.CarschUpdatePoint");
					
					szMsg= "차량포인트 변경 완료 :" +intRtnVal ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);		
				}
				//----------------------------------------------------------------------------------------------
		
				
				//----------------------------------------------------------------------------------------------
				//	포인트개폐 전송
				//----------------------------------------------------------------------------------------------
				//szTemp = szYD_STK_COL_GP;
				
				
				recPara  = JDTORecordFactory.getInstance().create();
				//TC CCODE
				recPara.setField("JMS_TC_CD"	, "YDTSJ012");
				//야드구분
				recPara.setField("YD_GP"		, szYD_STK_COL_GP.substring(0,1));
				//야드적치열 구분
				recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
				
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
	}		
	
	/**
	 * 입동순서 바꾸기
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 */
	public void procCoilYdGdsBayInWoSeqChangCoil(JDTORecord [] inDto) {
		int       intRtnVal    = 0;
		String    szMsg        = null;
		String    szMethodName = "procCoilYdGdsBayInWoSeqChangCoil";
		YdUtils ydUtils = new YdUtils();
		String  syd_car_sch_id ="";		
		JDTORecord    recPara  = JDTORecordFactory.getInstance().create();	
		YdCarSchDao ydCarSchDao = new YdCarSchDao(); 
		
		szMsg        = "";

		ydUtils.putLog(szSessionName, szMethodName,  "procCoilYdGdsBayInWoSeqChangCoil() IN", YdConstant.DEBUG);

		try {
			
			
				// 수정
			for(int x=0;x<inDto.length;x++){					
				for(int i=1;i<=20;i++){
					syd_car_sch_id = yddatautil.setDataDefault(inDto[x].getField("YD_CAR_SCH_ID"+i),     "");
					
					if(!syd_car_sch_id.equals("")){
						recPara.setField("YD_CAR_SCH_ID"	, yddatautil.setDataDefault(inDto[x].getField("YD_CAR_SCH_ID"+i),     ""));
						recPara.setField("YD_BAYIN_WO_SEQ"	, yddatautil.setDataDefault(inDto[x].getField("YD_BAYIN_WO_SEQ"+i),     ""));
						recPara.setField("MODIFIER"			, yddatautil.setDataDefault(inDto[x].getFieldString("YD_USER_ID"),""));
						
				   //     intRtnVal = ydCarSchDao.updYdCarsch(recPara,0);
						/*com.inisteel.cim.yd.dao.ydcarschdao.ydCarSchDao.updYdCarschYdBayinWoSeq*/
						intRtnVal = ydCarSchDao.updYdCarschYdCarWrkBookId(recPara,303);
					}
				}
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

		ydUtils.putLog(szSessionName, szMethodName,  "procCoilYdGdsBayInWoSeqChangCoil() OUT", YdConstant.DEBUG);
	}	// end of procCoilYdGdsBayInWoSeqChang
	
		
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
		JDTORecordSet    outRecSet	= null;
		JDTORecordSet    rsTemp     = null;
		JDTORecordSet    rsResult    = null;
		
		YdStkBedDao ydStkBedDao = new YdStkBedDao();
		YdStkColDao ydStkColDao = new YdStkColDao();
		YdCarSpecDao ydCarSpecDao = new YdCarSpecDao();
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		
		JDTORecord RecOutRec = null;
		JDTORecord recPara = null;
		String szMsg        = "";		
		String szMethodName = "updCarWrMgt";
		String szYD_CAR_SCH_ID = null;
		String sYD_USER_ID			= "";
		
		String sTRN_EQP_CD 			= "";
		String sYD_CAR_USE_GP 		= "";
		String sYD_CAR_SCH_ID 		= "";
		String sYD_CARLD_STOP_LOC	= "";
		String sYD_CARLD_YD_WBOOK_ID= "";
		String sYD_CARUD_YD_WBOOK_ID= "";
		String sYD_CAR_PROG_STAT 	= "";
		String sYD_CRN_SCH_ID 		= "";
		String sYD_PREP_SCH_ID 		= "";
		String szRtnMsg				= "";
		String sYD_CARUD_STOP_LOC	= "";
		String sYD_SCH_CD           = "";
		String sRTN_CD				= "";
		String sRTN_MSG				= "";

		String sCRANE_SND				= "";
		String sYD_WRK_PROG_STAT				= "";

		
		JDTORecord[] 	inRecordarr   		= null;			
		JDTORecord outRecord = JDTORecordFactory.getInstance().create(); // 
		JDTORecord inRecord = JDTORecordFactory.getInstance().create(); // 
		JDTORecord recInTemp = JDTORecordFactory.getInstance().create(); // 

		JDTORecord inRecord4 = JDTORecordFactory.getInstance().create(); // 
		JDTORecord outRecord5 = JDTORecordFactory.getInstance().create(); // 
		JDTORecord outRecord6 = JDTORecordFactory.getInstance().create(); // 
		JDTORecord recDelPara = JDTORecordFactory.getInstance().create(); // 
		
		EJBConnector ejbConn = null;
		
//		YdStkColDao	ydStkColDao	= new YdStkColDao();
//		YdCarSchDao ydCarSchDao = new YdCarSchDao();
		//YdCarSpecDao ydCarSpecDao = new YdCarSpecDao();
		CoilGdsJspDao dao = new CoilGdsJspDao();
		YdDelegate		ydDelegate 		= new YdDelegate();
		
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
		
			sTRN_EQP_CD 			= ydDaoUtils.paraRecChkNull(RecOutRec, "TRN_EQP_CD");
			sYD_CRN_SCH_ID 			= ydDaoUtils.paraRecChkNull(RecOutRec, "YD_CRN_SCH_ID");
			sYD_CARLD_YD_WBOOK_ID	= ydDaoUtils.paraRecChkNull(RecOutRec, "YD_CARLD_YD_WBOOK_ID");
			sYD_CARUD_YD_WBOOK_ID 	= ydDaoUtils.paraRecChkNull(RecOutRec, "YD_CARUD_YD_WBOOK_ID");
			sYD_CAR_SCH_ID 			= ydDaoUtils.paraRecChkNull(RecOutRec, "YD_CAR_SCH_ID");
			sYD_CARLD_STOP_LOC		= ydDaoUtils.paraRecChkNull(RecOutRec, "YD_CARLD_STOP_LOC");
			sYD_CARUD_STOP_LOC		= ydDaoUtils.paraRecChkNull(RecOutRec, "YD_CARUD_STOP_LOC");
			sYD_CAR_PROG_STAT 		= ydDaoUtils.paraRecChkNull(RecOutRec, "YD_CAR_PROG_STAT");
			sYD_PREP_SCH_ID 		= ydDaoUtils.paraRecChkNull(RecOutRec, "YD_PREP_SCH_ID");
			sYD_CAR_USE_GP 			= ydDaoUtils.paraRecChkNull(RecOutRec, "YD_CAR_USE_GP");
			sYD_SCH_CD				= ydDaoUtils.paraRecChkNull(RecOutRec, "YD_SCH_CD");

			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			ydUtils.putLog(szSessionName, szMethodName, "sTRN_EQP_CD 			"+ sTRN_EQP_CD 			  ,YdConstant.DEBUG);
		    ydUtils.putLog(szSessionName, szMethodName, "sYD_CRN_SCH_ID 		"+ sYD_CRN_SCH_ID 		  ,YdConstant.DEBUG);
		    ydUtils.putLog(szSessionName, szMethodName, "sYD_CARLD_YD_WBOOK_ID	"+ sYD_CARLD_YD_WBOOK_ID  ,YdConstant.DEBUG);
		    ydUtils.putLog(szSessionName, szMethodName, "sYD_CARUD_YD_WBOOK_ID  "+ sYD_CARUD_YD_WBOOK_ID  ,YdConstant.DEBUG);
		    ydUtils.putLog(szSessionName, szMethodName, "sYD_CAR_SCH_ID 		"+ sYD_CAR_SCH_ID 		  ,YdConstant.DEBUG);
		    ydUtils.putLog(szSessionName, szMethodName, "sYD_CARLD_STOP_LOC		"+ sYD_CARLD_STOP_LOC	  ,YdConstant.DEBUG);
		    ydUtils.putLog(szSessionName, szMethodName, "sYD_CARUD_STOP_LOC		"+ sYD_CARUD_STOP_LOC	  ,YdConstant.DEBUG);
		    ydUtils.putLog(szSessionName, szMethodName, "sYD_CAR_PROG_STAT 		"+ sYD_CAR_PROG_STAT 	  ,YdConstant.DEBUG);
		    ydUtils.putLog(szSessionName, szMethodName, "sYD_PREP_SCH_ID 		"+ sYD_PREP_SCH_ID 		  ,YdConstant.DEBUG);
		    ydUtils.putLog(szSessionName, szMethodName, "sYD_CAR_USE_GP 		"+ sYD_CAR_USE_GP 		  ,YdConstant.DEBUG);
		    ydUtils.putLog(szSessionName, szMethodName, "sYD_SCH_CD				"+ sYD_SCH_CD			  ,YdConstant.DEBUG);			
		      
			if(!sYD_CRN_SCH_ID.equals("")) {
				inRecord4   	= JDTORecordFactory.getInstance().create();
				inRecord4.setField("YD_CRN_SCH_ID"	,sYD_CRN_SCH_ID);
				inRecord4.setField("YD_SCH_CD"		,sYD_SCH_CD);
				inRecord4.setField("DEL_YN"			,"Y");
				inRecord4.setField("MODIFIER"		,sYD_USER_ID);
							
				ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);			
				outRecord5 = (JDTORecord)ejbConn.trx("WrkCancel", new Class[] { JDTORecord.class }, new Object[] { inRecord4 });
				sRTN_CD				= StringHelper.evl(outRecord5.getFieldString("RTN_CD"), "0");
				sRTN_MSG			= StringHelper.evl(outRecord5.getFieldString("RTN_MSG"), "");
				sCRANE_SND			= StringHelper.evl(outRecord5.getFieldString("CRANE_SND"), "");
				sYD_WRK_PROG_STAT	= StringHelper.evl(outRecord5.getFieldString("YD_WRK_PROG_STAT"), "");
				
				if ("0".equals(sRTN_CD)) {
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, sRTN_MSG);	
					return outRecord;
				}	
	//작업예약삭제
//				if(sYD_SCH_CD.substring(2, 8).equals("TR05MM")||
//					sYD_SCH_CD.substring(2, 8).equals("TR06MM")||
//					sYD_SCH_CD.substring(2, 8).equals("TR15MM")||
//					sYD_SCH_CD.substring(2, 8).equals("TR16MM")||
//					sYD_SCH_CD.substring(2, 8).equals("TR07MM")||
//					sYD_SCH_CD.substring(2, 8).equals("TR08MM")||
//					sYD_SCH_CD.substring(2, 8).equals("TR17MM")||
//					sYD_SCH_CD.substring(2, 8).equals("TR18MM")||
//					sYD_SCH_CD.substring(2, 8).equals("TR57MM")||
//					sYD_SCH_CD.substring(2, 8).equals("TR58MM")||
//					sYD_SCH_CD.substring(2, 8).equals("TR67MM")||
//					sYD_SCH_CD.substring(2, 8).equals("TR68MM")	){
					
				//차량동간이적
				if("TR".equals(sYD_SCH_CD.substring(2 , 4)) && "MM".equals(sYD_SCH_CD.substring(6 , 8))){
					ydUtils.putLog(szSessionName, szMethodName, "차량동간이적 시 작업예약 삭제 생략", YdConstant.DEBUG);
				}else{
					outRecord5.setField("MODIFIER"		,sYD_USER_ID);
					outRecord5.setField("YD_USER_ID"	,sYD_USER_ID);
					ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);			
					outRecord6 = (JDTORecord)ejbConn.trx("delWookBook", new Class[] { JDTORecord.class }, new Object[] { outRecord5 });
					sRTN_CD				= StringHelper.evl(outRecord6.getFieldString("RTN_CD"), "0");
					sRTN_MSG			= StringHelper.evl(outRecord6.getFieldString("RTN_MSG"), "");
					sYD_SCH_CD			= StringHelper.evl(outRecord6.getFieldString("YD_SCH_CD"), "");
					if ("0".equals(sRTN_CD)) {
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, sRTN_MSG);	
						return outRecord;
					}
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
			} else {   

				if(!sYD_CARLD_YD_WBOOK_ID.equals("")) {
					// 작업예약취소  호출	
					inRecordarr = new JDTORecord[1];
	
					inRecordarr[0] = JDTORecordFactory.getInstance().create();
					inRecordarr[0].setField("YD_WBOOK_ID"		, sYD_CARLD_YD_WBOOK_ID); 
					inRecordarr[0].setField("YD_USER_ID"	    , sYD_USER_ID); 
	
					if("TR".equals(sYD_SCH_CD.substring(2 , 4)) && "MM".equals(sYD_SCH_CD.substring(6 , 8))){
						ydUtils.putLog(szSessionName, szMethodName, "차량동간이적 시 작업예약 삭제 생략", YdConstant.DEBUG);
					}else{
						ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
						String rtnMsg = (String)ejbConn.trx("delYdWrkbook",
								new Class[] { JDTORecord[].class }, new Object[] { inRecordarr });
		
						if (!rtnMsg.equals(YdConstant.RETN_CD_SUCCESS)) {
							outRecord.setField("RTN_CD" 	, "0");	
							outRecord.setField("RTN_MSG" 	, sRTN_MSG);	
							return outRecord;
						}
					}
						
	
					
				}			
				if(!sYD_CARUD_YD_WBOOK_ID.equals("")) {
					inRecordarr = new JDTORecord[1];
	
					inRecordarr[0] = JDTORecordFactory.getInstance().create();
					inRecordarr[0].setField("YD_WBOOK_ID"		, sYD_CARUD_YD_WBOOK_ID); 
					inRecordarr[0].setField("YD_USER_ID"	    , sYD_USER_ID); 
	
					if("TR".equals(sYD_SCH_CD.substring(2 , 4)) && "MM".equals(sYD_SCH_CD.substring(6 , 8))){
						ydUtils.putLog(szSessionName, szMethodName, "차량동간이적 시 작업예약 삭제 생략", YdConstant.DEBUG);
					}else{
						ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
						String rtnMsg = (String)ejbConn.trx("delYdWrkbook",
								new Class[] { JDTORecord[].class }, new Object[] { inRecordarr });
		
						if (!rtnMsg.equals(YdConstant.RETN_CD_SUCCESS)) {
							outRecord.setField("RTN_CD" 	, "0");	
							outRecord.setField("RTN_MSG" 	, sRTN_MSG);	
							return outRecord;
						}	
					}
					
				}
			}	
			if(!sYD_CAR_SCH_ID.equals("")) {

				inRecord = JDTORecordFactory.getInstance().create(); // 
				inRecord.setField("V_MODIFIER"		, sYD_USER_ID);
				inRecord.setField("V_YD_CAR_SCH_ID"	, sYD_CAR_SCH_ID);
			    
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
				
				//대기차량인 경우 생략
				if(!sYD_CAR_PROG_STAT.equals("1")&&!sYD_CAR_PROG_STAT.equals("A")){
				
					if(sYD_CAR_PROG_STAT.equals("2")||sYD_CAR_PROG_STAT.equals("3")||sYD_CAR_PROG_STAT.equals("4")||sYD_CAR_PROG_STAT.equals("5")){
						//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
				        EJBConnector ejbConn2 = new EJBConnector("default","CarMvHdSeEJB",this);
						ejbConn2.trx("CarPointinforeg2", new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class},
					  	             new Object[]{"B","","",sYD_CARLD_STOP_LOC,"","","C"});
					}else{
						//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
				        EJBConnector ejbConn2 = new EJBConnector("default","CarMvHdSeEJB",this);
						ejbConn2.trx("CarPointinforeg2", new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class},
					  	             new Object[]{"B","","",sYD_CARUD_STOP_LOC,"","","C"});
					}
				}
				
			}
			
			szMsg = "업무기준 : 차량 출발 시 저장위치 제원 야드L2로 전송 분기 start !! sYD_CAR_PROG_STAT ="+sYD_CAR_PROG_STAT;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//상차지 하차지 구분
			if(sYD_CAR_PROG_STAT.equals("2")||sYD_CAR_PROG_STAT.equals("3")||sYD_CAR_PROG_STAT.equals("4")||sYD_CAR_PROG_STAT.equals("5")){
				
				if(!sYD_CARLD_STOP_LOC.equals("")) {
	
					inRecord = JDTORecordFactory.getInstance().create(); // 
					inRecord.setField("V_MODIFIER"		, sYD_USER_ID);
					inRecord.setField("V_YD_STK_COL_GP"	, sYD_CARLD_STOP_LOC);
				    
				    /* com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.updCarWrMgtStkcol */
				    intRtnVal = dao.updCarWrMgtStkcol(inRecord);
					
					
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
//						outRecord.setField("RTN_CD" 	, "0");	
//						outRecord.setField("RTN_MSG" 	, szMsg);	
//						return outRecord;

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
//						outRecord.setField("RTN_CD" 	, "0");	
//						outRecord.setField("RTN_MSG" 	, szMsg);	
//						return outRecord;

					}else if(intRtnVal < 0) {
						szMsg = "[" + szSessionName + "] 출발야드의 적치열["+sYD_CARLD_STOP_LOC+"]의 적치단을 비활성상태로 변경처리 시 오류발생 - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, szMsg);	
						return outRecord;
					}else if(intRtnVal > 1000) {
						szMsg = "[" + szSessionName + "] 출발야드의 적치열["+sYD_CARLD_STOP_LOC+"]의 적치단을 비활성상태로 변경처리 시 오류발생 - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//						outRecord.setField("RTN_CD" 	, "0");	
//						outRecord.setField("RTN_MSG" 	, szMsg);	
//						return outRecord;
					}
	
					szMsg= "[" + szSessionName + "] 출발야드의 적치열["+sYD_CARLD_STOP_LOC+"]의 적치단을 비활성상태로 변경처리 성공 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					
	
		    		// 업무기준 : 차량 출발 시 저장위치 제원 야드L2로 전송
			    	//=======================================================================
					szMsg = "업무기준 : 차량 출발 시 저장위치 제원 야드L2로 전송 sYD_CAR_PROG_STAT in 2,3,4,5";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
			    	recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_INFO_SYNC_CD", "3");							    // 1:동,2:SPAN,3:열,4:BED
					recInTemp.setField("YD_GP"          , sYD_CARLD_STOP_LOC.substring(0, 1));
					recInTemp.setField("YD_STK_COL_GP"  , sYD_CARLD_STOP_LOC);
					recInTemp.setField("YD_CAR_PROG_STAT", "S");
					recInTemp.setField("YD_EQP_WRK_STAT" , "L");
					recInTemp.setField("CAR_NO" , sTRN_EQP_CD);
					YdCommonUtils.sndStrPosSpecToL2(recInTemp);
					
				}
			} else if(sYD_CAR_PROG_STAT.equals("B")||sYD_CAR_PROG_STAT.equals("C")||sYD_CAR_PROG_STAT.equals("D")||sYD_CAR_PROG_STAT.equals("E")){	
				if(!sYD_CARUD_STOP_LOC.equals("")) {
	
					inRecord = JDTORecordFactory.getInstance().create(); // 
					inRecord.setField("V_MODIFIER"		, sYD_USER_ID);
					inRecord.setField("V_YD_STK_COL_GP"	, sYD_CARUD_STOP_LOC);
				    
				    /* com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.updCarWrMgtStkcol */
				    intRtnVal = dao.updCarWrMgtStkcol(inRecord);
					if (intRtnVal <= 0) {
						szMsg = "적치열  수정중 ERROR 발생 ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//						outRecord.setField("RTN_CD" 	, "0");	
//						outRecord.setField("RTN_MSG" 	, szMsg);	
//						return outRecord;
					} 
					
					
					
					/*
					 * 적치베드 상태비활성화등록
					 */
					szMsg= "[" + szSessionName + "] 도착야드의 적치열["+sYD_CARUD_STOP_LOC+"]의 베드를 비활성상태와 BED중량MAX기본값으로 변경처리 시작 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("MODIFIER", sYD_USER_ID);
					recInTemp.setField("YD_STK_COL_GP", sYD_CARUD_STOP_LOC);
					recInTemp.setField("YD_STK_BED_ACT_STAT", "C");
					
					intRtnVal = ydStkBedDao.updYdStkbedYdStkColGp(recInTemp, 0);
					if(intRtnVal == 0) {
						szMsg = "[" + szSessionName + "] 도착야드의 적치열["+sYD_CARUD_STOP_LOC+"]의 베드를 비활성상태와 BED중량MAX기본값으로 변경처리 시 적치베드가 존재하지 않습니다 - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}else if(intRtnVal < 0) {
						szMsg = "[" + szSessionName + "] 도착야드의 적치열["+sYD_CARUD_STOP_LOC+"]의 베드를 비활성상태와 BED중량MAX기본값으로 변경처리 시 오류발생 - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
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
//						outRecord.setField("RTN_CD" 	, "0");	
//						outRecord.setField("RTN_MSG" 	, szMsg);	
//						return outRecord;
					}else if(intRtnVal < 0) {
						szMsg = "[" + szSessionName + "] 도착야드의 적치열["+sYD_CARUD_STOP_LOC+"]의 적치단을 비활성상태로 변경처리 시 오류발생 - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, szMsg);	
						return outRecord;
					}else if(intRtnVal > 1000) {
						szMsg = "[" + szSessionName + "] 도착야드의 적치열["+sYD_CARUD_STOP_LOC+"]의 적치단을 비활성상태로 변경처리 시 오류발생 - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//						outRecord.setField("RTN_CD" 	, "0");	
//						outRecord.setField("RTN_MSG" 	, szMsg);	
//						return outRecord;
					}
	
					szMsg= "[" + szSessionName + "] 도착야드의 적치열["+sYD_CARUD_STOP_LOC+"]의 적치단을 비활성상태로 변경처리 성공 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					
	
		    		// 업무기준 : 차량 출발 시 저장위치 제원 야드L2로 전송
			    	//=======================================================================
					szMsg = "업무기준 : 차량 출발 시 저장위치 제원 야드L2로 전송 sYD_CAR_PROG_STAT in B,C,D,E";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
			    	recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("YD_INFO_SYNC_CD", "3");							    // 1:동,2:SPAN,3:열,4:BED
					recInTemp.setField("YD_GP"          , sYD_CARUD_STOP_LOC.substring(0, 1));
					recInTemp.setField("YD_STK_COL_GP"  , sYD_CARUD_STOP_LOC);
					recInTemp.setField("YD_CAR_PROG_STAT", "S");
					recInTemp.setField("YD_EQP_WRK_STAT" , "L");
					YdCommonUtils.sndStrPosSpecToL2(recInTemp);
					
				}
			}	
			if(!sYD_PREP_SCH_ID.equals("")) {

				inRecord = JDTORecordFactory.getInstance().create(); // 
				inRecord.setField("V_MODIFIER"			, sYD_USER_ID);
				inRecord.setField("V_YD_PREP_SCH_ID"	, sYD_PREP_SCH_ID);
			    
			    /*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.updCarWrMgtPrepSchMtl*/
			    intRtnVal = dao.updCarWrMgtPrepSchMtl(inRecord);
				if (intRtnVal <= 0) {
					szMsg = "준비작업 복원시 ERROR 발생 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//					outRecord.setField("RTN_CD" 	, "0");	
//					outRecord.setField("RTN_MSG" 	, szMsg);	
//					return outRecord;
				} 
			    
			    /* com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.updCarWrMgtPrepSch */
			    intRtnVal = dao.updCarWrMgtPrepSch(inRecord);
				if (intRtnVal <= 0) {
					szMsg = "준비작업 복원시 ERROR 발생 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
//					outRecord.setField("RTN_CD" 	, "0");	
//					outRecord.setField("RTN_MSG" 	, szMsg);	
//					return outRecord;
				} 
	 
			}
			
			/*
			 * hun 150820 야드구분=J , AutoCrn=true 일때 L2저장위치제원정보 S 전송
			 */
//			String szYD_STK_COL_GP=ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");
//			szMsg = "작업 취소시 야드 구분=J, Auto크레인 경우 정보 전송 start";
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//			if("J".equals(szYD_STK_COL_GP.substring(0, 1)) && ydEqpDao.chkAutoCrn(szEQP_ID) ){
//				// 150820 hun 테스트시 삭제후 차량 출발 발송 ( YDY5L001 )
//				//------------------------------------------------------------------------------
//				//L2저장위치제원정보 전송 YDY5L001
//				//------------------------------------------------------------------------------
//				JDTORecord msgRecord = JDTORecordFactory.getInstance().create();
//				msgRecord.setField("YD_INFO_SYNC_CD", "3");				//1:동,2:SPAN,3:열,4:BED
//				msgRecord.setField("YD_GP", 			szYD_STK_COL_GP.substring(0, 1));
//				msgRecord.setField("YD_STK_COL_GP", 	szYD_STK_COL_GP);
//				msgRecord.setField("YD_CAR_ARRSTRT_STAT", 	"S"); // 출발 flag
//				
//				YdCommonUtils.sndStrPosSpecToL2(msgRecord);
//		 
//				szRtnMsg = YdConstant.RETN_CD_SUCCESS;
//			}
			
			outRecord.setField("RTN_CD" 	, "1");	
			
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} 
		return outRecord;
		
	}//end of uptCarSch	
	
	/**
	 * 코일 번호로 저장위치 조회 하기 
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.11
	 */
	public GridData getCoilTolyr(GridData inDto) {
		GridData 		rtnGrd 		= new GridData();
		JDTORecordSet   outRecSet  	= null;
		
		CoilGdsJspDao 	dao 		= new CoilGdsJspDao();
		JDTORecord 		recPara		= null;
		
		try {
			
			// 파라미터 셋팅 
			recPara		= JDTORecordFactory.getInstance().create(); 			
			recPara.setField("V_STL_NO"		, inDto.getParam("COIL_NO").trim());		/*코일 번호*/

			// DAO 호출
			outRecSet = dao.getCoilTolyr(recPara);
			
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
	 * 제품야드 작업 실적 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilGdsWrkRsltQty(JDTORecord inDto) throws DAOException {
		int           intRtnVal    = 0;
		String        szMsg        = "";
		String        szMethodName = "getCoilGdsWrkRsltQty";
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		
		try {
			recPara.setField("YD_GP",			yddatautil.setDataDefault(inDto.getField("YD_GP"), 			""));
			recPara.setField("YD_GNT_GP",		yddatautil.setDataDefault(inDto.getField("YD_GNT_GP"), 		""));
			recPara.setField("YD_WRK_HDS_DD", 	yddatautil.setDataDefault(inDto.getField("YD_WRK_HDS_DD"), 	""));
			recPara.setField("YD_WRK_DUTY", 	yddatautil.setDataDefault(inDto.getField("YD_WRK_DUTY"), 	""));
			recPara.setField("STL_PROG_CD", 	yddatautil.setDataDefault(inDto.getField("CURR_PROG_CD"), 	""));
			
			YdCrnSchDao ydCrnschDao = new YdCrnSchDao();
//PIDEV_S :병행가동용:PI_YD
			recPara.setField("PI_YD",    	yddatautil.setDataDefault(inDto.getField("YD_GP"), 			""));						
			/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getCoilGdsYdWrkRsltQty_PIDEV*/
			intRtnVal = ydCrnschDao.getYdCrnsch(recPara, outRecSet, 503);
			
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
	}	// end of getCoilGdsWrkRsltQty
	
	/**
	 * 대차 권상가능 CHECK
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return 
	 * @return 
	 * @return
	 * @throws DAOException
	 */
	
	public JDTORecord getStkColTCarUpChk(JDTORecord inDto) throws DAOException {
		int       intRtnVal    = 0;
		JDTORecordSet    outRecSet	= null;
		JDTORecordSet    rsTemp     = null;
		JDTORecordSet    rsResult    = null;
		
		JDTORecord RecOutRec 	= null;
		JDTORecord recPara 		= null;
		String szMsg        	= "";		
		String szMethodName 	= "getStkColTCarUpChk";
		String sYD_SCH_CD 		= "";	
		String sYD_WBOOK_ID 	= "";	
		String sSTL_CNT 	= "";	
		String sSUM_WGT 	= "";	
		String sCURR_PROG_CD ="";
		String sTRANS_ORD_SEQNO ="";
		
		
		JDTORecord outRecord = JDTORecordFactory.getInstance().create(); // 
		JDTORecord inRecord = JDTORecordFactory.getInstance().create(); // 
		
		//YdCarSpecDao ydCarSpecDao = new YdCarSpecDao();
		CoilGdsJspDao dao = new CoilGdsJspDao();
		
		try {
			szMsg = "[] 대차 권상가능 CHECK ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			sYD_SCH_CD 		= yddatautil.setDataDefault(inDto.getField("YD_SCH_CD"), "");
			sYD_WBOOK_ID 	= yddatautil.setDataDefault(inDto.getField("YD_WBOOK_ID"), "");
			
			recPara         = JDTORecordFactory.getInstance().create();
			recPara.setField("V_YD_SCH_CD"		,sYD_SCH_CD);
			recPara.setField("V_YD_WBOOK_ID"	,sYD_WBOOK_ID);
			
			outRecSet = dao.getStkColTCarUpChk(recPara);
			
			if (outRecSet == null || outRecSet.size() == 0) {
				szMsg = "해당정보가 없습니다." + sYD_SCH_CD;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
				
			} 
			outRecSet.first();
			RecOutRec = outRecSet.getRecord();
		
			sSTL_CNT		= ydDaoUtils.paraRecChkNull(RecOutRec, "STL_CNT");
			sSUM_WGT 		= ydDaoUtils.paraRecChkNull(RecOutRec, "SUM_WGT");
			sCURR_PROG_CD	= ydDaoUtils.paraRecChkNull(RecOutRec, "CURR_PROG_CD");
			sTRANS_ORD_SEQNO	= ydDaoUtils.paraRecChkNull(RecOutRec, "TRANS_ORD_SEQNO");
			
			ydUtils.putLog(szSessionName, szMethodName, "sSTL_CNT:" + sSTL_CNT, YdConstant.DEBUG);
			ydUtils.putLog(szSessionName, szMethodName, "sSUM_WGT:" + sSUM_WGT, YdConstant.DEBUG);
			ydUtils.putLog(szSessionName, szMethodName, "sCURR_PROG_CD:" + sCURR_PROG_CD, YdConstant.DEBUG);
			ydUtils.putLog(szSessionName, szMethodName, "sTRANS_ORD_SEQNO:" + sTRANS_ORD_SEQNO, YdConstant.DEBUG);
			
			outRecord.setField("CURR_PROG_CD" 	, sCURR_PROG_CD);
			outRecord.setField("TRANS_ORD_SEQNO" 	, sTRANS_ORD_SEQNO);
			outRecord.setField("STL_CNT" 	, sSTL_CNT);	
			outRecord.setField("SUM_WGT" 	, sSUM_WGT);	
			outRecord.setField("RTN_CD" 	, "1");	
			return outRecord;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}//end of uptCarSch	
	
	
	
	/**
	 * 대차 권상가능 CHECK
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return 
	 * @return 
	 * @return
	 * @throws DAOException
	 */
	
	public JDTORecord getStkColTCarUpChk2(JDTORecord inDto) throws DAOException {
		int       intRtnVal    = 0;
		JDTORecordSet    outRecSet	= null;
		JDTORecordSet    rsTemp     = null;
		JDTORecordSet    rsResult    = null;
		
		JDTORecord RecOutRec 	= null;
		JDTORecord recPara 		= null;
		String szMsg        	= "";		
		String szMethodName 	= "getStkColTCarUpChk2";
		String sYD_SCH_CD 		= "";	
		String sYD_CRN_SCH_ID 	= "";	
		String sSTL_CNT 	= "";	
		String sSUM_WGT 	= "";	
		
		JDTORecord outRecord = JDTORecordFactory.getInstance().create(); // 
		JDTORecord inRecord = JDTORecordFactory.getInstance().create(); // 
		
		//YdCarSpecDao ydCarSpecDao = new YdCarSpecDao();
		CoilGdsJspDao dao = new CoilGdsJspDao();
		
		try {
			szMsg = "[] 대차 권상가능 CHECK ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			sYD_SCH_CD 		= yddatautil.setDataDefault(inDto.getField("YD_SCH_CD"), "");
			sYD_CRN_SCH_ID 	= yddatautil.setDataDefault(inDto.getField("YD_CRN_SCH_ID"), "");
			
			recPara         = JDTORecordFactory.getInstance().create();
			recPara.setField("V_YD_SCH_CD"		,sYD_SCH_CD);
			recPara.setField("V_YD_CRN_SCH_ID"	,sYD_CRN_SCH_ID);
			
			outRecSet = dao.getStkColTCarUpChk2(recPara);
			
			if (outRecSet == null || outRecSet.size() == 0) {
				szMsg = "해당정보가 없습니다." + sYD_SCH_CD;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
				
			} 
			outRecSet.first();
			RecOutRec = outRecSet.getRecord();
		
			sSTL_CNT		= ydDaoUtils.paraRecChkNull(RecOutRec, "STL_CNT");
			sSUM_WGT 		= ydDaoUtils.paraRecChkNull(RecOutRec, "SUM_WGT");
			
			ydUtils.putLog(szSessionName, szMethodName, "sSTL_CNT:" + sSTL_CNT, YdConstant.DEBUG);
			ydUtils.putLog(szSessionName, szMethodName, "sSUM_WGT:" + sSUM_WGT, YdConstant.DEBUG);
			
			outRecord.setField("STL_CNT" 	, sSTL_CNT);	
			outRecord.setField("SUM_WGT" 	, sSUM_WGT);	
			outRecord.setField("RTN_CD" 	, "1");	
			return outRecord;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}//end of getStkColTCarUpChk2	
	
	
	/**
	 * 대차 권상가능 CHECK
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return 
	 * @return 
	 * @return
	 * @throws DAOException
	 */
	
	public JDTORecord getStkColTCarUpChk3(JDTORecord inDto) throws DAOException {
		int       intRtnVal    = 0;
		JDTORecordSet    outRecSet	= null;
		JDTORecordSet    rsTemp     = null;
		JDTORecordSet    rsResult    = null;
		
		JDTORecord RecOutRec 	= null;
		JDTORecord recPara 		= null;
		String szMsg        	= "";		
		String szMethodName 	= "getStkColTCarUpChk3";
		String sYD_SCH_CD 		= "";	
		String sYD_WBOOK_ID 	= "";	
		String sSTL_CNT 	= "";	
		String sSUM_WGT 	= "";	
		String sCURR_PROG_CD ="";
		String sTRANS_ORD_SEQNO ="";
		
		
		JDTORecord outRecord = JDTORecordFactory.getInstance().create(); // 
		JDTORecord inRecord = JDTORecordFactory.getInstance().create(); // 
		
		//YdCarSpecDao ydCarSpecDao = new YdCarSpecDao();
		CoilGdsJspDao dao = new CoilGdsJspDao();
		
		try {
			szMsg = "[] 대차 권상가능 CHECK ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			sYD_SCH_CD 		= yddatautil.setDataDefault(inDto.getField("YD_SCH_CD"), "");
			sYD_WBOOK_ID 	= yddatautil.setDataDefault(inDto.getField("YD_WBOOK_ID"), "");
			
			recPara         = JDTORecordFactory.getInstance().create();
			recPara.setField("V_YD_SCH_CD"		,sYD_SCH_CD);
			recPara.setField("V_YD_WBOOK_ID"	,sYD_WBOOK_ID);
			
			outRecSet = dao.getStkColTCarUpChk3(recPara);
			
			if (outRecSet == null || outRecSet.size() == 0) {
				szMsg = "해당정보가 없습니다." + sYD_SCH_CD;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
				
			} 
			outRecSet.first();
			RecOutRec = outRecSet.getRecord();
		
			sSTL_CNT		= ydDaoUtils.paraRecChkNull(RecOutRec, "STL_CNT");
			sSUM_WGT 		= ydDaoUtils.paraRecChkNull(RecOutRec, "SUM_WGT");
			sCURR_PROG_CD	= ydDaoUtils.paraRecChkNull(RecOutRec, "CURR_PROG_CD");
			sTRANS_ORD_SEQNO	= ydDaoUtils.paraRecChkNull(RecOutRec, "TRANS_ORD_SEQNO");
			
			ydUtils.putLog(szSessionName, szMethodName, "sSTL_CNT:" + sSTL_CNT, YdConstant.DEBUG);
			ydUtils.putLog(szSessionName, szMethodName, "sSUM_WGT:" + sSUM_WGT, YdConstant.DEBUG);
			ydUtils.putLog(szSessionName, szMethodName, "sCURR_PROG_CD:" + sCURR_PROG_CD, YdConstant.DEBUG);
			ydUtils.putLog(szSessionName, szMethodName, "sTRANS_ORD_SEQNO:" + sTRANS_ORD_SEQNO, YdConstant.DEBUG);
			
			outRecord.setField("CURR_PROG_CD" 	, sCURR_PROG_CD);
			outRecord.setField("TRANS_ORD_SEQNO" 	, sTRANS_ORD_SEQNO);
			outRecord.setField("STL_CNT" 	, sSTL_CNT);	
			outRecord.setField("SUM_WGT" 	, sSUM_WGT);	
			outRecord.setField("RTN_CD" 	, "1");	
			return outRecord;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}//end of getStkColTCarUpChk3	
	
	
	/**
	 * 차량출발등록(PDA) 목록 조회
	 * @ejb.interface-method
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.28 
	 */
	public JDTORecordSet getCarStartMgtList(JDTORecord inDto) throws DAOException {

		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");

		CoilGdsJspDao dao = new CoilGdsJspDao();

		try {

			recPara.setField("V_YD_GP", inDto.getFieldString("YD_GP"));
			outRecSet = dao.getCarStartMgtList(recPara);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

		return outRecSet;
	}	
	
	/**
	 * 차량도착등록(PDA) 목록 조회 
	 * @ejb.interface-method
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.28 
	 */
	public JDTORecordSet getCarArrivalMgtList(JDTORecord inDto) throws DAOException {
		
		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		CoilGdsJspDao dao = new CoilGdsJspDao();
		
		try {
			
			recPara.setField("V_CARD_NO", inDto.getFieldString("CARD_NO"));
			recPara.setField("V_YD_BAY_GP", inDto.getFieldString("YD_BAY_GP"));
			outRecSet = dao.getCarArrivalMgtList(recPara);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
		return outRecSet;
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
	public JDTORecord getYdPointCdList(JDTORecord inDto) throws DAOException {
		
		JDTORecord    	recPara   	= JDTORecordFactory.getInstance().create();
		JDTORecord    	tmpPara   	= JDTORecordFactory.getInstance().create();
		JDTORecord    	rtnPara   	= JDTORecordFactory.getInstance().create();
		JDTORecordSet 	outRecSet 	= JDTORecordFactory.getInstance().createRecordSet("YD");
		
		CoilGdsJspDao 	dao 		= new CoilGdsJspDao();
		
		String 			szComboList = "";
		
		try {
			
			recPara.setField("V_YD_BAY_GP", inDto.getFieldString("YD_BAY_GP"));
			outRecSet = dao.getYdPointCdList(recPara);
			
			if(outRecSet != null && outRecSet.size() >0){
				for(int i=0; i<outRecSet.size(); i++){
					tmpPara = outRecSet.getRecord(i);
					szComboList += tmpPara.getFieldString("CODE").trim() +"||";
					szComboList += tmpPara.getFieldString("NAME").trim() +"**";
				}
			}
			if(szComboList.length() > 3){
				szComboList = szComboList.substring(0, szComboList.length()-2);
			}
			
			rtnPara.setField("COMBOLIST", szComboList);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
		return rtnPara;
	}	
	
	
	/**
	 * 차량출발처리 (C열연 공통)
	 * @ejb.interface-method
	 * @param JDTORecord (YD_CAR_SCH_ID)
	 * @return JDTORecord
	 * @throws JDTOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.29
	 */
	public JDTORecord procCarStart(JDTORecord inRecord) throws DAOException {
		int 			intRtnVal 		= 0;
		JDTORecord 		outRcd	  		= null;
		JDTORecord 		recResult  		= null;
		JDTORecord 		recInTemp  		= null;
		
		JDTORecordSet 	rsResult 		= null;
		
		YdCarSchDao 	ydCarSchDao 	= new YdCarSchDao();
		
		String 			szMsg 			= "";
		String 			szMethodName	= "procCarStart";
		
		try {
			
			// 차량 자동출발 처리 
			
			outRcd  	= JDTORecordFactory.getInstance().create();
			recInTemp  	= JDTORecordFactory.getInstance().create();
			rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
			recInTemp.setField("YD_CAR_SCH_ID", inRecord.getFieldString("YD_CAR_SCH_ID"));
			
			intRtnVal 	= ydCarSchDao.getYdCarsch(recInTemp, rsResult, 0);
			if(intRtnVal > 0){
		
				rsResult.first();
				recResult = rsResult.getRecord();
				String szCAR_NO = ydDaoUtils.paraRecChkNull(recResult,"CAR_NO");
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("TC_CODE",        		"DMYDR040");									//전문코드
				recInTemp.setField("CARD_NO", 				ydDaoUtils.paraRecChkNull(recResult,"CARD_NO"));
				recInTemp.setField("CAR_NO", 				szCAR_NO);			
				recInTemp.setField("SPOS_WLOC_CD", 			ydDaoUtils.paraRecChkNull(recResult,"SPOS_WLOC_CD"));
				recInTemp.setField("SPOS_YD_PNT_CD", 		ydDaoUtils.paraRecChkNull(recResult,"YD_PNT_CD1"));
				recInTemp.setField("TRANS_ORD_DT", 			ydDaoUtils.paraRecChkNull(recResult,"TRANS_ORD_DATE"));
				recInTemp.setField("TRANS_ORD_SEQNO", 		ydDaoUtils.paraRecChkNull(recResult,"TRANS_ORD_SEQNO"));
				//E/T Car 나 해송차량인 경우에는 차량출발처리를 자동으로 하지 않는다.
				String szCARD_NO = ydDaoUtils.paraRecChkNull(recResult,"CARD_NO");
				
//				if(szCAR_NO.matches("\\d\\d\\d\\d")) {				//E/T Car
				if(szCARD_NO.equals("")){
					szCARD_NO = "XXXXX";
				}	
//				if(szCARD_NO.substring(0, 1).equals("T")||
//						szCARD_NO.substring(0, 1).equals("P")||
//						szCARD_NO.substring(0, 1).equals("E")
//		 	 			){
//					szMsg= "[procCarStart] E/T Car[" + szCAR_NO + "]는 차량출발처리를 하지 않습니다.";
//					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//					
//					outRcd.setField("RTN_CD", "0");
//					outRcd.setField("RTN_MSG", "E/T Car는 차량출발처리를 하지 않습니다.");
//				}else{
					szMsg= "[procCarStart] 차량번호[" + szCAR_NO + "]는 자동차량출발";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					EJBConnector ejbConn = new EJBConnector("default", "CarMvHdFaEJB", this);
					ejbConn.trx("rcvCoilGdsdistCarLevWr", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
					
					outRcd.setField("RTN_CD", "1");
					outRcd.setField("RTN_MSG", "차량 출발 처리 완료");
//				}
			}
			
		} catch (Exception e) {
			this.m_ctx.setRollbackOnly();
			e.printStackTrace();
		}
		return outRcd;
	}
	
	/**
	 * 차량도착처리 (C열연 공통)
	 * @ejb.interface-method
	 * @param JDTORecord (YD_STK_COL_GP, YD_CAR_SCH_ID)
	 * @return JDTORecord
	 * @throws JDTOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.29
	 */
	public JDTORecord procCarArrival(JDTORecord inRecord) throws DAOException {
		JDTORecord 		outRcd	  		= JDTORecordFactory.getInstance().create();;
		JDTORecord 		recInTemp  		= null;
		
		YdDelegate		ydDelegate 		= new YdDelegate();
		
		String 			szMsg 			= "";
		String 			szMethodName	= "procCarStart";
		String 			szOperationName	= "차량도착처리(YDYDJ633)";
		String 			szYD_STK_COL_GP = "";
		String 			szYD_CAR_SCH_ID = "";
		try {
			
			// 입력 파라메터 설정 
			szYD_STK_COL_GP = inRecord.getFieldString("YD_STK_COL_GP");
			szYD_CAR_SCH_ID = inRecord.getFieldString("YD_CAR_SCH_ID");
			
			/*
			 * 6. 차량정지위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
			 */
			szMsg="[" + szOperationName + "] 차량정지위치[" + szYD_STK_COL_GP + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 호출 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("TC_CODE"				,"YDYDJ633");
			recInTemp.setField("TC_CREATE_DDTT"			,YdUtils.getCurDate("yyyyMMddHHmmss"));
			recInTemp.setField("YD_CAR_STOP_LOC"		,szYD_STK_COL_GP);
			recInTemp.setField("YD_CAR_SCH_ID"			,szYD_CAR_SCH_ID);
						
			ydDelegate.sendMsg(recInTemp);
			szMsg="[" + szOperationName + "] 차량정지위치[" + szYD_STK_COL_GP + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 호출 성공";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			outRcd.setField("RTN_CD", "1");
			outRcd.setField("RTN_MSG", szOperationName+" 전문전송 완료");
			
		} catch (Exception e) {
			this.m_ctx.setRollbackOnly();
			e.printStackTrace();
		}
		
		return outRcd;
	}
	
	/**
	 * 차량도착처리 (C열연 공통)
	 * @ejb.interface-method
	 * @param JDTORecord (YD_STK_COL_GP, YD_CAR_SCH_ID)
	 * @return JDTORecord
	 * @throws JDTOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.29
	 */
	public JDTORecord updCarArrival(JDTORecord[] inRecord) throws DAOException {
		JDTORecord 		outRcd	  		= JDTORecordFactory.getInstance().create();;
		JDTORecord 		recInTemp  		= null;
		
		YdDelegate		ydDelegate 		= new YdDelegate();
		
		String 			szMsg 			= "";
		String 			szMethodName	= "updCarArrival";
		String 			szOperationName	= "차량도착처리(YDYDJ633)";
		String 			szYD_STK_COL_GP = "";
		String 			szYD_CAR_SCH_ID = "";
		try {
			
			for(int x=0;x<inRecord.length;x++){
				// 입력 파라메터 설정 YD_STK_COL_GP
				szYD_STK_COL_GP = inRecord[x].getFieldString("YD_STK_COL_GP"); 
				szYD_CAR_SCH_ID = inRecord[x].getFieldString("YD_CAR_SCH_ID1"); 
				
				/*
				 * 6. 차량정지위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
				 */
				szMsg="[" + szOperationName + "] 차량정지위치[" + szYD_STK_COL_GP + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 호출 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("TC_CODE"				,"YDYDJ633");
				recInTemp.setField("TC_CREATE_DDTT"			,YdUtils.getCurDate("yyyyMMddHHmmss"));
				recInTemp.setField("YD_CAR_STOP_LOC"		,szYD_STK_COL_GP);
				recInTemp.setField("YD_CAR_SCH_ID"			,szYD_CAR_SCH_ID);
							
				ydDelegate.sendMsg(recInTemp);
				szMsg="[" + szOperationName + "] 차량정지위치[" + szYD_STK_COL_GP + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 호출 성공";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				outRcd.setField("RTN_CD", "1");
				outRcd.setField("RTN_MSG", szOperationName+" 전문전송 완료");
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return outRcd;
	}

	/**
	 * 차량도착처리 (C열연 공통)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord (YD_STK_COL_GP, YD_CAR_SCH_ID)
	 * @return JDTORecord
	 * @throws JDTOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.29
	 */
	public JDTORecord updCarArrival1(JDTORecord[] inRecord) throws DAOException {
		JDTORecord 		outRcd	  		= JDTORecordFactory.getInstance().create();;
		JDTORecord 		recInTemp  		= null;
		
		YdDelegate		ydDelegate 		= new YdDelegate();
		
		String 			szMsg 			= "";
		String 			szMethodName	= "updCarArrival1";
		String 			szOperationName	= "차량도착처리(YDYDJ633)";
		String 			szYD_STK_COL_GP = "";
		String 			szYD_CAR_SCH_ID = "";
		try {
			
			for(int x=0;x<inRecord.length;x++){
				// 입력 파라메터 설정 YD_STK_COL_GP
				szYD_STK_COL_GP = inRecord[x].getFieldString("YD_STK_COL_GP"); 
				szYD_CAR_SCH_ID = inRecord[x].getFieldString("YD_CAR_SCH_ID1"); 
				
				/*
				 * 6. 차량정지위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
				 */
				szMsg="[" + szOperationName + "] 차량정지위치[" + szYD_STK_COL_GP + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 호출 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("TC_CODE"				,"YDYDJ633");
				recInTemp.setField("TC_CREATE_DDTT"			,YdUtils.getCurDate("yyyyMMddHHmmss"));
				recInTemp.setField("YD_CAR_STOP_LOC"		,szYD_STK_COL_GP);
//				recInTemp.setField("YD_CAR_SCH_ID"			,szYD_CAR_SCH_ID);
				recInTemp.setField("YD_CAR_SCH_ID"			,"");
										
				ydDelegate.sendMsg(recInTemp);
				szMsg="[" + szOperationName + "] 차량정지위치[" + szYD_STK_COL_GP + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 호출 성공";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				outRcd.setField("RTN_CD", "1");
				outRcd.setField("RTN_MSG", szOperationName+" 전문전송 완료");
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return outRcd;
	}	
	
	/**
	 * 차량도착처리 (전체 공통)
	 * @ejb.interface-method
	 * @param JDTORecord (YD_STK_COL_GP, YD_CAR_SCH_ID)
	 * @return JDTORecord
	 * @throws JDTOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.29
	 */
	public JDTORecord CarArrivalNEW(JDTORecord[] inRecord) throws DAOException {
		JDTORecord 		outRcd	  		= JDTORecordFactory.getInstance().create();;
		JDTORecord 		recInTemp  		= null;
		
		YdDelegate		ydDelegate 		= new YdDelegate();
		
		String 			szMsg 			= "";
		String 			szMethodName	= "CarArrivalNEW";
		String 			szOperationName	= "차량도착처리NEW";
		String 			szYD_CARPNT_CD = "";
		String 			szYD_CAR_SCH_ID = "";
		try {
			
			for(int x=0;x<inRecord.length;x++){
				// 입력 파라메터 설정 YD_STK_COL_GP
				szYD_CARPNT_CD = inRecord[x].getFieldString("YD_CARPNT_CD"); 
				 
				
				szMsg="[" + szOperationName + "] 차량입동포인트[" + szYD_CARPNT_CD + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 호출 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	 
				
				recInTemp = JDTORecordFactory.getInstance().create(); 
				recInTemp.setField("JMS_TC_CD",  "YDYDJ662");
				recInTemp.setField("YD_CARPNT_CD",    szYD_CARPNT_CD);		//입동포인트
				recInTemp.setField("YD_CAR_SCH_ID",    szYD_CAR_SCH_ID);	//차량스케줄ID
				ydUtils.displayRecord(szOperationName, recInTemp);	 
				ydDelegate.sendMsg(recInTemp);
				
				//EJBConnector 	ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);			
			    //ejbConn.trx("procCarBayInOrdReqNEW", new Class[] { JDTORecord.class }, new Object[] { recInTemp });					
				
			   
				
				szMsg="[" + szOperationName + "] 차량입동포인트[" + szYD_CARPNT_CD + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 호출 성공";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				outRcd.setField("RTN_CD", "1");
				outRcd.setField("RTN_MSG", szOperationName+" 전문전송 완료");
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return outRcd;
	}
	
	/**
	 * 차량출발처리 (C열연 공통)
	 * @ejb.interface-method
	 * @param JDTORecord (YD_CAR_SCH_ID)
	 * @return JDTORecord
	 * @throws JDTOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.29
	 */
	public JDTORecord updCarStart(JDTORecord[] inRecord) throws DAOException {
		int 			intRtnVal 		= 0;
		JDTORecord 		outRcd	  		= JDTORecordFactory.getInstance().create();
		JDTORecord 		recResult  		= JDTORecordFactory.getInstance().create();
		JDTORecord 		recInTemp  		= JDTORecordFactory.getInstance().create();
		
		JDTORecordSet 	rsResult 		= null;
		
		YdCarSchDao 	ydCarSchDao 	= new YdCarSchDao();
		
		String 			szMsg 			= "";
		String 			szMethodName	= "updCarStart";
		
		try {
			
			// 차량 자동출발 처리 
			
			for(int x=0;x<inRecord.length;x++){
				recInTemp  	= JDTORecordFactory.getInstance().create();
				rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
				recInTemp.setField("YD_CAR_SCH_ID", inRecord[x].getFieldString("YD_CAR_SCH_ID")); 
				
				intRtnVal 	= ydCarSchDao.getYdCarsch(recInTemp, rsResult, 0);
				if(intRtnVal > 0){
			
					rsResult.first();
					recResult = rsResult.getRecord();
					String szCAR_NO = ydDaoUtils.paraRecChkNull(recResult,"CAR_NO");
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("TC_CODE",        		"DMYDR040");									//전문코드
					recInTemp.setField("CARD_NO", 				ydDaoUtils.paraRecChkNull(recResult,"CARD_NO"));
					recInTemp.setField("CAR_NO", 				szCAR_NO);			
					recInTemp.setField("SPOS_WLOC_CD", 			ydDaoUtils.paraRecChkNull(recResult,"SPOS_WLOC_CD"));
					recInTemp.setField("SPOS_YD_PNT_CD", 		ydDaoUtils.paraRecChkNull(recResult,"YD_PNT_CD1"));
					recInTemp.setField("TRANS_ORD_DT", 			ydDaoUtils.paraRecChkNull(recResult,"TRANS_ORD_DATE"));
					recInTemp.setField("TRANS_ORD_SEQNO", 		ydDaoUtils.paraRecChkNull(recResult,"TRANS_ORD_SEQNO"));
					//E/T Car 나 해송차량인 경우에는 차량출발처리를 자동으로 하지 않는다.
//					if(szCAR_NO.matches("\\d\\d\\d\\d")) {				//E/T Car
					String szCARD_NO = ydDaoUtils.paraRecChkNull(recResult,"CARD_NO");
					if(szCARD_NO.equals("")){
						szCARD_NO = "XXXXX";
					}					
//					if(szCARD_NO.substring(0, 1).equals("T")||
//							szCARD_NO.substring(0, 1).equals("P")||
//							szCARD_NO.substring(0, 1).equals("E")){
//
//						szMsg= "[updCarStart] E/T Car[" + szCAR_NO + "]는 차량출발처리를 하지 않습니다.";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//						
//						outRcd.setField("RTN_CD", "0");
//						outRcd.setField("RTN_MSG", "E/T Car는 차량출발처리를 하지 않습니다.");
//					}else{
						szMsg= "[updCarStart] 차량번호[" + szCAR_NO + "]는 자동차량출발";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						EJBConnector ejbConn = new EJBConnector("default", "CarMvHdFaEJB", this);
						ejbConn.trx("rcvCoilGdsdistCarLevWr", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
						

//					}
				}
			}	
			outRcd.setField("RTN_CD", "1");
			outRcd.setField("RTN_MSG", "차량 출발 처리 완료");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outRcd;
	}
	
	
	
	/**
	 *  동별 SCHDULE 정보
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getSchRuleList(JDTORecord inDto) throws DAOException {
		
		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
				
		String szMsg        = "";		
		String szMethodName = "getSchRuleList";			
		int    intRtnVal    = 0;
		
		try {
			recPara.setField("V_YD_BAY_GP" ,  ydDaoUtils.paraRecChkNull(inDto, "YD_BAY_GP"));
			
			CoilGdsJspDao dao = new CoilGdsJspDao();
			outRecSet = dao.getSchRuleList(recPara);
			
			if (outRecSet == null || outRecSet.size() == 0) {
				szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			} 
			
			outRecSet.first();
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {

		}
		
		return outRecSet;
	}//end of getSchRuleList
	
	
	/**
	 * 야드관리 > 코일제품창고 > 기준관리 > 적치위치관리  목록조회
	 *
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.07.05
	 */
	public GridData getStrlocChgSetList(GridData inDto) {
		GridData 		rtnGrd 		= new GridData();
		JDTORecordSet   outRecSet  	= null;
		
		CoilGdsJspDao 	dao 		= new CoilGdsJspDao();
		JDTORecord 		recPara		= null;
		
		try {
			
			// 파라미터 셋팅 
			recPara		= JDTORecordFactory.getInstance().create(); 			
			recPara.setField("V_YD_GP"		, inDto.getParam("YD_GP").trim());		/*야드구분*/
			recPara.setField("V_YD_BAY_GP"	, inDto.getParam("YD_BAY_GP").trim());	/*동구분*/

			// DAO 호출 
			/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getStrlocChgSetList*/
			outRecSet = dao.getStrlocChgSetList(recPara);
			
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
	}//end of getStrlocUsgSetList
	
	/**
	 * 야드관리 > 코일제품창고 > 기준관리 > 적치위치변경  등록
	 *
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.07.05
	 */
	public JDTORecord updStrlocChgSet(JDTORecord[] inRecord) throws DAOException {		
		GridData 		rtnGrd 		= new GridData();
		int 			intRtnVal	= 0;
		int 			res			= 0;
		CoilGdsJspDao 	dao 		= new CoilGdsJspDao();
		JDTORecord 		recPara		= null;
		JDTORecord 		outRecord	= JDTORecordFactory.getInstance().create();
		JDTORecord 		RecOutRec	= JDTORecordFactory.getInstance().create();
		JDTORecord 		RecOutRec1	= JDTORecordFactory.getInstance().create();
		
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecordSet outRecSet1 = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String sYD_GP         = "";
		String sYD_BAY_GP     = "";
		String sYD_EQP_GP     = "";
		String sYD_STK_COL_NO = "";
		String sYD_USER_ID    = "";
		String sYD_STK_COL_GP = "";
		String headerName     = "";
		String szMethodName	= "updStrlocChgSet";
		String sSTL_NO_CNT    = "0";
		String sYD_STK_COL_GP_CHG = "";
		
		try {
			
			for(int x=0;x<inRecord.length;x++){
				sYD_GP         = yddatautil.setDataDefault(inRecord[x].getFieldString("YD_GP"), "");
				sYD_BAY_GP     = yddatautil.setDataDefault(inRecord[x].getFieldString("YD_BAY_GP"), ""); //동
				sYD_STK_COL_NO = yddatautil.setDataDefault(inRecord[x].getFieldString("YD_STK_COL_NO"), ""); //열
				sYD_USER_ID	   = yddatautil.setDataDefault(inRecord[0].getField("YD_USER_ID"), ""); //수정자
				
				for(int j=1; j<=62; j++){ //cell loop	

					headerName = "H_SPAN"+j; 
					if(yddatautil.setDataDefault(inRecord[x].getFieldString(headerName), "").equals("U")){
						if (j < 10){
							sYD_EQP_GP = "0"+j ;  //SPAM
						} else {
							sYD_EQP_GP = "" +j;  //SPAM
						}	
						
						sYD_STK_COL_GP = sYD_GP + sYD_BAY_GP + sYD_EQP_GP+ sYD_STK_COL_NO;                //
						
						ydUtils.putLog(szSessionName, szMethodName, "sYD_STK_COL_GP:" + sYD_STK_COL_GP , YdConstant.DEBUG);
						ydUtils.putLog(szSessionName, szMethodName, "YD_USER_ID:" + sYD_USER_ID , YdConstant.DEBUG);
		
						
						recPara	= JDTORecordFactory.getInstance().create();
						recPara.setField("V_YD_STK_COL_GP",	    sYD_STK_COL_GP); 
						// DAO 호출				
						/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getStrlocChgSet*/
						outRecSet = dao.getStrlocChgSet(recPara);
						
						if(outRecSet == null || outRecSet.size() < 1){
							outRecord.setField("RTN_CD", "0");
							outRecord.setField("RTN_MSG", "해당 대상 정보가 없습니다..");
							return outRecord;
						}
		
						outRecSet.first();
						RecOutRec	= JDTORecordFactory.getInstance().create();
						RecOutRec = outRecSet.getRecord();
						sSTL_NO_CNT	= ydDaoUtils.paraRecChkNull(RecOutRec, "STL_NO_CNT");
						
						if (!sSTL_NO_CNT.equals("0")) {
							outRecord.setField("RTN_CD", "0");
							outRecord.setField("RTN_MSG", "해당 위치에 코일이 있습니다..");
							return outRecord;
						}
		
						recPara	= JDTORecordFactory.getInstance().create();
						recPara.setField("V_MODIFIER"		, sYD_USER_ID); 
						recPara.setField("V_DEL_YN"			, "Y"); 
						recPara.setField("V_YD_STK_COL_GP"	, sYD_STK_COL_GP); 
		// 삭제처리
						intRtnVal = dao.updStrlocChgColSet(recPara);
						if (intRtnVal <= 0) {
							outRecord.setField("RTN_CD", "0");
							outRecord.setField("RTN_MSG", "COL 삭제시 ERROR 발생..");
							return outRecord;
						} 
					    
						intRtnVal = dao.updStrlocChgBedSet(recPara);
						if (intRtnVal <= 0) {
							outRecord.setField("RTN_CD", "0");
							outRecord.setField("RTN_MSG", "BED 삭제시 ERROR 발생..");
							return outRecord;
						} 
			
						intRtnVal = dao.updStrlocChgLyrSet(recPara);
						if (intRtnVal <= 0) {
							outRecord.setField("RTN_CD", "0");
							outRecord.setField("RTN_MSG", "LYR 삭제시 ERROR 발생..");
							return outRecord;
						} 
						
						recPara	= JDTORecordFactory.getInstance().create();
						recPara.setField("V_YD_STK_COL_GP",	    sYD_STK_COL_GP); 
						recPara.setField("V_YD_GP",	    		sYD_GP); 
						// DAO 호출				
						/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.getYdEqpTcBreYDB011*/
						outRecSet1 = dao.getYdEqpTcBreYDB011(recPara);
						if(outRecSet1 == null || outRecSet1.size() < 1){
							outRecord.setField("RTN_CD", "0");
							outRecord.setField("RTN_MSG", "해당 BRE 정보가 없습니다..");
							return outRecord;
						}
		
						outRecSet1.first();
						RecOutRec1 = JDTORecordFactory.getInstance().create();
						RecOutRec1 = outRecSet1.getRecord();
						if(sYD_GP.equals("H")) {
							sYD_STK_COL_GP_CHG	= ydDaoUtils.paraRecChkNull(RecOutRec1, "J_COL_GP");
						} else {
							sYD_STK_COL_GP_CHG	= ydDaoUtils.paraRecChkNull(RecOutRec1, "H_COL_GP");
						}	
						if (sYD_STK_COL_GP_CHG.equals("")) {
							outRecord.setField("RTN_CD", "0");
							outRecord.setField("RTN_MSG", "BRE 확인.");
							return outRecord;
						}
		
						recPara	= JDTORecordFactory.getInstance().create();
						recPara.setField("V_MODIFIER"		, sYD_USER_ID); 
						recPara.setField("V_DEL_YN"			, "N"); 
						recPara.setField("V_YD_STK_COL_GP"	, sYD_STK_COL_GP_CHG); 
		// 삭제처리
						intRtnVal = dao.updStrlocChgColSet(recPara);
						if (intRtnVal <= 0) {
							outRecord.setField("RTN_CD", "0");
							outRecord.setField("RTN_MSG", "COL 등록시 ERROR 발생..");
							return outRecord;
						} 
					    
						intRtnVal = dao.updStrlocChgBedSet(recPara);
						if (intRtnVal <= 0) {
							outRecord.setField("RTN_CD", "0");
							outRecord.setField("RTN_MSG", "BED 등록시 ERROR 발생..");
							return outRecord;
						} 
			
						intRtnVal = dao.updStrlocChgLyrSet(recPara);
						if (intRtnVal <= 0) {
							outRecord.setField("RTN_CD", "0");
							outRecord.setField("RTN_MSG", "LYR 등록시 ERROR 발생..");
							return outRecord;
						} 
					}	
				}
			}

			outRecord.setField("RTN_CD", "1");
			outRecord.setField("RTN_MSG"," 정상 등록되었습니다.");
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		return outRecord;
	}//end 
	/**
	 * 야드관리 > 코일제품야드 > 야드재공관리 > 재료진도별 재공현황
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilMtlProgIdInlnStat(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara    = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet  = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getCoilMtlProgIdInlnStat";
	      
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao(); 
		
		int intRtnVal = 0;
		
		try {
			szMsg = "JSP-SESSION [재료진도별 재공현황]시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
			
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
			/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilGdsMtlProgIdInlnStat*/
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 307);
			
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
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		szMsg = "JSP-SESSION [재료진도별 재공현황]끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
		return outRecSet;
	}//end of getCoilYdColStkPosList
	
	/**
	 * 제품이송재료LIST
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getYdTransMtlList(JDTORecord inDto) throws DAOException {
		/*
		 * 업무기준 : 이송지시테이블, 저장품, 적치단, 코일공통, 준비스케줄, 준비재료, 작업예약, 작업예약재료
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
	    String      szYD_AIM_YD_GP		= "";
	    String      szYD_AIM_BAY_GP 	= "";
	    String 		szSPOS_WLOC_CD		= "";
		String		szARR_WLOC_CD		= "";
		String		szWO_STATE			= "";
		String		szIN_OUT_GP			= "";
		String		szYD_GP				= "";
		String		szYD_DONG_GP		= "";
		String		szYD_SPAN_GP		= "";
		String		szYD_COL_GP			= "";
		String		szYD_STK_COL_GP		= "";
		String		szYD_AIM_RT_GP		= "";
		String		szDATE_FROM			= "";
		String		szDATE_TO			= "";
		String		szYD_STK_LYR_NO		= "";
		
		int intRtnVal = 0;
		
		try {
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 : 파라미터 확인 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			ydUtils.displayRecord(szOperationName, inDto);
			
			szIN_OUT_GP 			= ydDaoUtils.paraRecChkNull(inDto, "IN_OUT_GP");			//입고/출고구분
			szYD_DONG_GP 			= ydDaoUtils.paraRecChkNull(inDto, "YD_DONG_GP");
			szYD_SPAN_GP 			= ydDaoUtils.paraRecChkNull(inDto, "YD_SPAN_GP");
			szYD_COL_GP 			= ydDaoUtils.paraRecChkNull(inDto, "YD_COL_GP");
			szYD_STK_LYR_NO			= ydDaoUtils.paraRecChkNull(inDto, "YD_STK_LYR_NO");
			if( szIN_OUT_GP.equals("1")) {					//입고
				szARR_WLOC_CD 		= ydDaoUtils.paraRecChkNull(inDto, "SPOS_WLOC_CD");	
				szSPOS_WLOC_CD 		= ydDaoUtils.paraRecChkNull(inDto, "ARR_WLOC_CD");
				
				szYD_AIM_YD_GP 		= ydDaoUtils.paraRecChkNull(inDto, "YD_AIM_YD_GP");
				szYD_STK_COL_GP 	= "";
				szYD_AIM_BAY_GP 	= ydDaoUtils.paraRecChkNull(inDto, "YD_AIM_BAY_GP");
			
			}else{											//출고
				szSPOS_WLOC_CD 		= ydDaoUtils.paraRecChkNull(inDto, "SPOS_WLOC_CD");
				szARR_WLOC_CD 		= ydDaoUtils.paraRecChkNull(inDto, "ARR_WLOC_CD");

				szYD_AIM_YD_GP 		= ydDaoUtils.paraRecChkNull(inDto, "YD_GP");
				if(szYD_DONG_GP.equals("")) {
					szYD_STK_COL_GP	= szYD_AIM_YD_GP;
					szYD_AIM_BAY_GP	= "";
					
				} else {
					
					szYD_STK_COL_GP	= szYD_AIM_YD_GP + szYD_DONG_GP.substring(1, 2)+ szYD_SPAN_GP + szYD_COL_GP;
					szYD_AIM_BAY_GP = szYD_DONG_GP;
					
				}
				
			}
			
			szYD_AIM_RT_GP 			= ydDaoUtils.paraRecChkNull(inDto, "YD_AIM_RT_GP");
			szDATE_FROM   			= ydDaoUtils.paraRecChkNull(inDto, "FR_DATE");
			szDATE_TO   			= ydDaoUtils.paraRecChkNull(inDto, "TO_DATE");
			szWO_STATE   			= ydDaoUtils.paraRecChkNull(inDto, "WO_STATE");
			szYD_GP 				= ydDaoUtils.paraRecChkNull(inDto, "YD_GP");
			
			//------------------------------------------------------------------------
			//	날짜를 전체로 조회할 것인 지를 판단하는 변수 추가
			//	수정자 : 임춘수
			//	수정일 : 2010.01.29
			//------------------------------------------------------------------------
//			szRD_DATE_ALL			= ydDaoUtils.paraRecChkNull(inDto, "RD_DATE_ALL");
			
			if( szWO_STATE.equals("1")) {
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
		    recPara.setField("FR_DATE",    		szDATE_FROM);
		    recPara.setField("TO_DATE",      	szDATE_TO);
		    recPara.setField("YD_STK_COL_GP",   szYD_STK_COL_GP);
		    recPara.setField("YD_AIM_RT_GP",    szYD_AIM_RT_GP);
		    recPara.setField("YD_AIM_YD_GP",    szYD_AIM_YD_GP);
		    recPara.setField("YD_AIM_BAY_GP",   szYD_AIM_BAY_GP);
		    recPara.setField("YD_PREP_WK_ST",   "L");
		    recPara.setField("YD_STK_LYR_NO",   szYD_STK_LYR_NO);
		    
			recPara.setField("PAGE_NO",      	inDto.getField("PAGE_NO"));		
			recPara.setField("ROW_CNT",      	inDto.getField("ROWCOUNT"));
			
			if( szWO_STATE.equals("1")) {										//지시
			    /*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCoilYdFrtoMoveOrdMtlListPage*/
				intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 600);
			}else if( szWO_STATE.equals("2")){									//완료
				/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCoilGdsYdTransMtlListPageForMoveCmpl*/
				intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 601);
			}else if( szWO_STATE.equals("3")){									//이송LOT편성
				/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCoilGdsYdFrtoMovePrepMtlListPage*/
				intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 602);
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
	 * 크레인 출하작업 현황
	 * @ejb.interface-method
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.28 
	 */
	public JDTORecordSet getCoilCraneCarWrkPDA(JDTORecord inDto) throws DAOException {
		
		JDTORecord    	recPara   	= JDTORecordFactory.getInstance().create();
		JDTORecord    	tmpPara   	= JDTORecordFactory.getInstance().create();
		JDTORecord    	rtnPara   	= JDTORecordFactory.getInstance().create();
		JDTORecordSet 	outRecSet 	= JDTORecordFactory.getInstance().createRecordSet("YD");
		
		CoilGdsJspDao 	dao 		= new CoilGdsJspDao();
		
		String 			szComboList = "";
		
		try {
			
			outRecSet = dao.getCoilCraneCarWrkPDA(inDto);
			return outRecSet;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

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
	public JDTORecordSet getCrnGp(JDTORecord inDto) throws DAOException {
		
		JDTORecord    	recPara   	= JDTORecordFactory.getInstance().create();
		JDTORecord    	tmpPara   	= JDTORecordFactory.getInstance().create();
		JDTORecord    	rtnPara   	= JDTORecordFactory.getInstance().create();
		JDTORecordSet 	outRecSet 	= JDTORecordFactory.getInstance().createRecordSet("YD");
		
		CoilGdsJspDao 	dao 		= new CoilGdsJspDao();
		
		String 			szComboList = "";
		
		try {
			
			outRecSet = dao.getCrnGp(inDto);
			return outRecSet;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

	}	
	
	/**
	 * C연주야드현황조회1
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCSlabYdMgtList(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		
		String szMsg        	= "";
		String szMethodName 	= "getCSlabYdMgtList";	
		String szOperationName 	= "C연주야드현황조회1";
		
		CoilGdsJspDao 	dao 		= new CoilGdsJspDao();
		
		int intRtnVal = 0;
		
		try {		
				    
			outRecSet = dao.getCSlabYdMgtList(inDto);
			return outRecSet;
				
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}//end of getCSlabYdMgtList
	
	/**
	 * 후판야드현황조회1
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getPSlabYdMgtList(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		
		String szMsg        	= "";
		String szMethodName 	= "getPSlabYdMgtList";	
		String szOperationName 	= "후판야드현황조회1";
		
		CoilGdsJspDao 	dao 		= new CoilGdsJspDao();
		
		int intRtnVal = 0;
		
		try {		
				    
			outRecSet = dao.getPSlabYdMgtList(inDto);
			return outRecSet;
				
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}//end of getCSlabYdMgtList
	
	/**
	 * B열연야드현황조회1
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getBSlabYdMgtList(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		
		String szMsg        	= "";
		String szMethodName 	= "getPSlabYdMgtList";	
		String szOperationName 	= "B열연야드현황조회1";
		
		CoilGdsJspDao 	dao 		= new CoilGdsJspDao();
		
		int intRtnVal = 0;
		
		try {		
				    
			outRecSet = dao.getBSlabYdMgtList(inDto);
			return outRecSet;
				
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}//end of getCSlabYdMgtList
	
	/**
	 * test용
	 * @ejb.interface-method
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.28 

	public String getTEST(JDTORecord inDto) throws DAOException {
		
		String szOperationName		= "TEST BreRule";
		String szMethodName			= "getTEST";
		String szCD_VAL             = "";
		
		boolean bBreRule			= false;
		JDTORecord jdtoRcd			= JDTORecordFactory.getInstance().create();
		
		try {
			String item1 	= ydDaoUtils.paraRecChkNull(inDto, "ITEM_GP1");			// 야드동구분
			int item2 	 	= ydDaoUtils.paraRecChkNullInt(inDto, "ITEM_GP2");		// 가동대수
			int item3 	 	= ydDaoUtils.paraRecChkNullInt(inDto, "ITEM_GP3");		// 입고대기건수
			String item4 	= ydDaoUtils.paraRecChkNull(inDto, "ITEM_GP4");			// 스케쥴마지막코드
			String item5 	= ydDaoUtils.paraRecChkNull(inDto, "ITEM_GP5");			// 1호기상태
			String item6 	= ydDaoUtils.paraRecChkNull(inDto, "ITEM_GP6");			// 2호기상태
			String item7 	= ydDaoUtils.paraRecChkNull(inDto, "ITEM_GP7");			// 3호기상태
		
			String LOC 	    = ydDaoUtils.paraRecChkNull(inDto, "LOC");				// 작업호기권하위치 
			String item8 	= LOC.substring(2, 4);	 								// 작업권하스판(02,03,04)
			String LOC2 	= ydDaoUtils.paraRecChkNull(inDto, "LOC2");				// 2호기권하위치 
			String item9 	= LOC2.substring(2, 4);		// 해당동 2호기권하SPAN(03,04)
			String item10 	= LOC2.substring(2, 5);									// 해당동 2호기권하SPAN+1(PTB)
			
			String LOC1 	= ydDaoUtils.paraRecChkNull(inDto, "LOC1");				// 1호기권하위치 
          
			ydUtils.putLog(szSessionName, szMethodName, "LOC2.compareTo(LOC)" +LOC2.compareTo(LOC), YdConstant.ERROR);
			double item11 	= LOC2.compareTo(LOC) ;

			ydUtils.putLog(szSessionName, szMethodName, "LOC1.compareTo(LOC)" +LOC1.compareTo(LOC), YdConstant.ERROR);
			double item12 	= LOC1.compareTo(LOC) ;
					
			String item13 	= "";
			
			bBreRule = GetBreRule6.getYDB650(item1, item2, item3, item4, item5, item6, item7, item8
					                        ,item9, item10,item11,item12,item13,jdtoRcd);
			
			ydUtils.putLog(szSessionName, szMethodName, "bBreRule" + bBreRule, YdConstant.ERROR);
			if( bBreRule ) {
				try {
	    			
	    			ydUtils.displayRecord(szOperationName, jdtoRcd);
	    			
	    			szCD_VAL		= ydDaoUtils.paraRecChkNull(jdtoRcd, "CD_VAL");
	    			
	    			if(szCD_VAL.equals("") || szCD_VAL.equals(null)){
	    				szCD_VAL 	= "NODATA";
	    			}
	    			
	    		}catch(JDTOException ex) {
	    			ydUtils.putLog(szSessionName, szMethodName, "확인", YdConstant.ERROR);
	    			szCD_VAL 	= "NODATA";
	    		}
	    		
			}else{
				szCD_VAL 	= "NODATA";

			}
			
			

			return szCD_VAL;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}

	}	
	 */	
	/**
	 * 반납대상 긴급재 지정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord[]
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord updCoilYdemergencyMgt(JDTORecord[] inDto) throws DAOException {
		int intRtnVal 		= 0;
		String szMsg		= null;
		String szMethodName = null;
		JDTORecordSet 	retRdSet 	= JDTORecordFactory.getInstance().createRecordSet("YD");
		CoilGdsJspDao dao = new CoilGdsJspDao();
		JDTORecord recPara  = JDTORecordFactory.getInstance().create();
		JDTORecord outRec   = JDTORecordFactory.getInstance().create();
		JDTORecord retRrd   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");

		
		szMsg        = "";
		String szSTL_NO = "";
		String szSNDBK_RSN_CD 	= "";
		String szSNDBK_REGISTER = "";
		String szSNDBK_GP		= "";
		String szYD_AIM_BAY_GP		= "";
		String sYD_USER_ID		= "";
		
		szMethodName = "updCoilYdemergencyMgt";
			
		
		try {
			
			
			szMsg = "JSP-SESSION [반송 긴급재 지정] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			for(int x=0;x<inDto.length;x++){
				
				szSTL_NO 			= yddatautil.setDataDefault(inDto[x].getField("STL_NO"),"");
				sYD_USER_ID 		= yddatautil.setDataDefault(inDto[x].getField("YD_USER_ID"),"");

				
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("V_STL_NO"      	, szSTL_NO);
				recPara.setField("V_MODIFIER"      	, sYD_USER_ID);
				
				szMsg = szSTL_NO + "V_MODIFIER:" + sYD_USER_ID  ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				/**com.inisteel.cim.yd.jsp.coiljsp.dao.CoilGdsJspDao.updCoilYdemergencyMgt*/
				intRtnVal = dao.updCoilYdemergencyMgt(recPara);
				if (intRtnVal <= 0) {
					szMsg = "반송긴급재 지정 시 ERROR 발생 ";
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
		
		szMsg = "JSP-SESSION [반송 긴급재 지정] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return retRrd;
		
	}	// end of updCoilYdemergencyMgt
	
////////////////////////////////////////////////////////////////////////
//C증설
	
	/**
	 * 코일제품야드 tracking 팝업 조회보급
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public JDTORecordSet getcoilGdsYdLineWrPpNew(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getcoilGdsYdLineWrPp";

		
		String szEqpGp 		= "";
		String sSTL_NO 		= "";
		String sWORK_STAT 	= "";
		String szOperationName = "코일제품야드 입고Tracking조회 팝업";
		
		
		//DAO
		YdCrnWrkMtlDao ydCrnWrkMtlDao = new YdCrnWrkMtlDao();
		
		int intRtnVal = 0;
		
		try {
			
			sSTL_NO	= ydDaoUtils.paraRecChkNull(inDto, "COIL_NO");
			
			//if(!sSTL_NO.equals("")){
					
			recPara         = JDTORecordFactory.getInstance().create();
			recPara.setField("LINE"		, inDto.getField("EQP_GP"));	
			recPara.setField("COIL_NO"	, sSTL_NO);
			recPara.setField("PAGE_NO"	, inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT"	, inDto.getField("ROWCOUNT"));
				
			/*com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getcoilGdsYdLineWrPpNew*/
			intRtnVal = ydCrnWrkMtlDao.getYdCrnwrkmtl(recPara, outRecSet, 403);
			
			if(intRtnVal < 0){
				szMsg = "[JSP Session : "+szOperationName+"]조회 ERROR ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
			}else if(intRtnVal == 0){
				szMsg = "[JSP Session : "+szOperationName+"]조회된 DATA가 없습니다. ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
				
			}else{
				szMsg = "[JSP Session : "+szOperationName+"] 조회 성공";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	
	}	
	

	/**
	 * 코일제품야드 tracking 팝업 조회보급
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public JDTORecordSet getcoilGdsYdLineWrPpGPack(JDTORecord inDto) throws DAOException {
		
		JPlateYdCommDAO ydCommDao = new JPlateYdCommDAO();
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getcoilGdsYdLineWrPpGPack";

		String sQueryId     = "";
		String szEqpGp 		= "";
		String sSTL_NO 		= "";
		String sWORK_STAT 	= "";
		String szOperationName = "코일제품야드 지포장 입고Tracking조회 팝업";
		
		
		//DAO
		YdCrnWrkMtlDao ydCrnWrkMtlDao = new YdCrnWrkMtlDao();
		
		int intRtnVal = 0;
		
		try {
			
			sSTL_NO	= ydDaoUtils.paraRecChkNull(inDto, "COIL_NO");
			
			//if(!sSTL_NO.equals("")){
					
			recPara         = JDTORecordFactory.getInstance().create();
			recPara.setField("LINE"		, inDto.getField("EQP_GP"));	
			recPara.setField("COIL_NO"	, sSTL_NO);
			recPara.setField("PAGE_NO"	, inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT"	, inDto.getField("ROWCOUNT"));
			
			sQueryId = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getcoilGdsYdLineWrPpGPack";
		    intRtnVal = ydCommDao.select(recPara, outRecSet, sQueryId);
			
			if(intRtnVal < 0){
				szMsg = "[JSP Session : "+szOperationName+"]조회 ERROR ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
			}else if(intRtnVal == 0){
				szMsg = "[JSP Session : "+szOperationName+"]조회된 DATA가 없습니다. ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
				
			}else{
				szMsg = "[JSP Session : "+szOperationName+"] 조회 성공";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	
	}	
	

	/**
	 * 코일제품야드 CHook 모니터링 팝업
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public JDTORecordSet getCHookcoilGdsYdPp(JDTORecord inDto) throws DAOException {
		
		JPlateYdCommDAO ydCommDao = new JPlateYdCommDAO();
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getcoilGdsYdLineWrPpGPack";

		String sQueryId     = "";
		String szEqpGp 		= "";
		String sSTL_NO 		= "";
		String szParaCode 	= "";
		String szOperationName = "코일제품야드 CHook 모니터링 조회 팝업";
		
		int intRtnVal = 0;
		
		try {
			
			szParaCode	= ydDaoUtils.paraRecChkNull(inDto, "PARACODE");
			szMsg = "[JSP Session : "+szOperationName+"] szParaCode ="+szParaCode;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			recPara         = JDTORecordFactory.getInstance().create();
			recPara.setField("PARACODE"	, szParaCode);
			
			sQueryId = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getMonitorChookCoilPop";
		    intRtnVal = ydCommDao.select(recPara, outRecSet, sQueryId);
			
			if(intRtnVal < 0){
				szMsg = "[JSP Session : "+szOperationName+"]조회 ERROR ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
			}else if(intRtnVal == 0){
				szMsg = "[JSP Session : "+szOperationName+"]조회된 DATA가 없습니다. ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
				
			}else{
				szMsg = "[JSP Session : "+szOperationName+"] 조회 성공";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	
	}	
	

	/**
	 * 코일제품야드 CHook 모니터링 팝업 insert
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public JDTORecordSet insCHookcoilGdsYdPp(JDTORecord inDto) throws DAOException {
		
		JPlateYdCommDAO ydCommDao = new JPlateYdCommDAO();
		ymCommonDAO dao = ymCommonDAO.getInstance();
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "insCHookcoilGdsYdPp";

		String sQueryId     = "";
		
		String szSTL_NO 	= "";
		String szREGISTER	= "";
		String szINDIA_CONTENTS = "";
		String szYD_EQP_TRBL_RCVR_DT_FR = "";
		String szOperationName = "코일제품야드 CHook 모니터링 입력";
		
		int intRtnVal = 0;
		
		try {
			
			szSTL_NO	= ydDaoUtils.paraRecChkNull(inDto, "PSTL_NO");
			szREGISTER	= ydDaoUtils.paraRecChkNull(inDto, "YD_USER_ID");
			szINDIA_CONTENTS = ydDaoUtils.paraRecChkNull(inDto, "INS_INDIA");
			szYD_EQP_TRBL_RCVR_DT_FR = ydDaoUtils.paraRecChkNull(inDto, "YD_EQP_TRBL_RCVR_DT_FR");
			
			szMsg = "[JSP Session : "+szOperationName+"] szSTL_NO ="+szSTL_NO+",szREGISTER="+szREGISTER+",szINDIA_CONTENTS="+szINDIA_CONTENTS+"szYD_EQP_TRBL_RCVR_DT_FR="+szYD_EQP_TRBL_RCVR_DT_FR;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			recPara         = JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO"	, szSTL_NO);
			recPara.setField("REGISTER"	, szREGISTER);
			recPara.setField("INDIA_CONTENTS"	, szINDIA_CONTENTS);
			recPara.setField("YD_EQP_TRBL_RCVR_DT_FR"	, szYD_EQP_TRBL_RCVR_DT_FR);
		      
			sQueryId = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.insCoilIndiaHist";
		    //intRtnVal = ydCommDao.select(recPara, outRecSet, sQueryId);
			intRtnVal = dao.insertData(sQueryId , new Object[]{szSTL_NO,szSTL_NO,szREGISTER,szINDIA_CONTENTS,szYD_EQP_TRBL_RCVR_DT_FR});
			
			if(intRtnVal < 0){
				szMsg = "[JSP Session : "+szOperationName+"]입력 ERROR ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
			}else if(intRtnVal == 0){
				szMsg = "[JSP Session : "+szOperationName+"]입력된 DATA가 없습니다. ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
				
			}else{
				szMsg = "[JSP Session : "+szOperationName+"] 입력 성공";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	
	}	
	
	/**
	 * 압연일자별 재공현황
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet YdNextprocList(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		
		String szMsg        	= "";
		String szMethodName 	= "YdNextprocList";	
		String szOperationName 	= "압연일자별 재공현황";
		String sDD_CHK 			= "";
		
		YdEqpDao ydEqpDao = new YdEqpDao();
		
		int intRtnVal = 0;
		
		try {		
				    
			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
 
			
			recPara         = JDTORecordFactory.getInstance().create();
		    recPara.setField("YD_GP",      ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));
		    /*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getCoilTotYdNextproctList*/
		    intRtnVal = ydEqpDao.getYdEqp(recPara, outRecSet, 803);
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
	}//end of getYdMgtList
	
	/**
	 * 공장별 재공현황
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet YdNextprocList2(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		
		String szMsg        	= "";
		String szMethodName 	= "YdNextprocList2";	
		String szOperationName 	= "공장별 재공현황";
		String sDD_CHK 			= "";
		
		YdEqpDao ydEqpDao = new YdEqpDao();
		
		int intRtnVal = 0;
		
		try {		
				    
			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
 
			
			recPara         = JDTORecordFactory.getInstance().create();
		    recPara.setField("YD_GP",      ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));
		    /*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getCoilTotYdNextproctList2*/
		    intRtnVal = ydEqpDao.getYdEqp(recPara, outRecSet, 804);
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
	}//end of getYdMgtList2
	
	
	/**
	 * 공장별 재공현황Pop
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet YdNextprocList2Pop(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		
		String szMsg        	= "";
		String szMethodName 	= "YdNextprocList2Pop";	
		String szOperationName 	= "동별 재공현황율";
		String sDD_CHK 			= "";
		
		YdEqpDao ydEqpDao = new YdEqpDao();
		
		int intRtnVal = 0;
		
		try {		
				    
			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
 
			
			recPara         = JDTORecordFactory.getInstance().create();
		    recPara.setField("YD_GP",      ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));
		    /*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getCoilTotYdNextprocList2Pop*/
		    intRtnVal = ydEqpDao.getYdEqp(recPara, outRecSet, 805);
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
	}//end of getYdMgtList2Pop
	
	
	
	/**
	 * 오퍼레이션명 : HOT COIL이용한 결로방지 시스템
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */          
	public GridData procHotcoilAuto(GridData inDto) throws DAOException  {
		// DAO객체 선언
		YdDaoUtils ydDaoUtils 			= new YdDaoUtils();
		
		EJBConnector ejbConn 		= null;		
		//레코드 선언
		JDTORecord inRecord       	=  JDTORecordFactory.getInstance().create(); 
		JDTORecord recPara       	=  JDTORecordFactory.getInstance().create();

		JDTORecord outRecord     	= JDTORecordFactory.getInstance().create(); // 
		JDTORecord outRecord2     	= JDTORecordFactory.getInstance().create(); // 
		GridData gdRes 				= null;
		
		JDTORecordSet outRecSet     = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecord[]  inRecordarr   = null;
		
		String szMsg           = "";
		String szMethodName    = "procHotcoilAuto";
		String szOperationName = "HOT COIL이용한 결로방지 시스템";

		
		
		int intRtnVal          = 0;

		//목표행선구분
//		//야드적치Bed입출고상태
//		String szYD_STK_BED_WHIO_STAT  = null;
		//스케줄코드
		String szYD_SCH_CD             	= "";		
		String FIRST_YD_SCH_CD			= "";
		
		//공정구분
		String FIRST_YD_WBOOK_ID	   	= "";
		String FIRST_YD_BAY_GP		   	= "";
		String sYD_WBOOK_ID			   	= "";
		String sRTN_CD					= "";
		String sRTN_MSG					= "";

	
		String sSTL_NO 					= "";
		String sBAY_GP 					= "";
		String sIN_BAY_GP 				= "";
		String sYD_STK_LYR_NO           = "";		
		String sYD_STK_BED_NO          	= "";
		String sYD_STK_COL_GP		    = "";
		String szYD_CHK_GP 				= "";
		String sYD_USER_ID 				= "";
		
		try{
			inRecord = CmUtil.genJDTORecord(inDto);
			szYD_CHK_GP 	= ydDaoUtils.paraRecChkNull(inRecord, "YD_CHK_GP"); //1:보급 ,2:추출
			sYD_USER_ID		= ydDaoUtils.paraRecChkNull(inRecord, "YD_USER_ID");
			sIN_BAY_GP		= ydDaoUtils.paraRecChkNull(inRecord, "YD_BAY_GP");
			
			gdRes = OperateGridData.cloneResponseGridData(inDto);		
			gdRes = CmUtil.copyGDParam(inDto, gdRes);
			
			YdEqpDao   ydEqpDao   = new YdEqpDao();
			JDTORecord 		inRecord9 	= JDTORecordFactory.getInstance().create();
	 
			
			
			if(szYD_CHK_GP.equals("1")){
				//////////////////////////////////////////////////////////////////////////////////////////////////////////
				//--------결로재 보급 작업 ----------
				//////////////////////////////////////////////////////////////////////////////////////////////////////////
				
				if("".equals(sIN_BAY_GP)){
					inRecord9.setField("BAY_GP", "");     
				}else{
					inRecord9.setField("BAY_GP", sIN_BAY_GP); 
				}
				
				//	결로적치장 기준으로 HOT COIL대상 가져 오기				
				/*com.inisteel.cim.yd.dao.ydstklyrdao.hotcoilautolist*/
				intRtnVal = ydEqpDao.getYdEqp(inRecord9, outRecSet, 403);
				if(intRtnVal <= 0) {
					szMsg = "결로적치 보급 대상이 존재 안 합니다.<br>";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		
					gdRes.setMessage(szMsg);	
					return gdRes;
				}
				szMsg="결로재 보급 대상 건수: " + intRtnVal ;
				ydUtils.putLog(szOperationName, szMethodName, szMsg, YdConstant.DEBUG);		
				
				for (int nLoop =0 ; nLoop<outRecSet.size();nLoop++ ){
					
					recPara =JDTORecordFactory.getInstance().create();
					recPara = outRecSet.getRecord(nLoop);
					
					
					sYD_STK_COL_GP 	=StringHelper.evl(recPara.getFieldString("YD_STK_COL_GP"), "");
					sYD_STK_BED_NO 	=StringHelper.evl(recPara.getFieldString("YD_STK_BED_NO"), "");
					sYD_STK_LYR_NO 	=StringHelper.evl(recPara.getFieldString("YD_STK_LYR_NO"), "");
					sBAY_GP 		=StringHelper.evl(recPara.getFieldString("BAY_GP"), "");
					sSTL_NO 		=StringHelper.evl(recPara.getFieldString("STL_NO"), "");
					

					//=================================================================================
					// 결로방지재 추출요구 스케줄 코드 편성
					String sSPAN_GP = sYD_STK_COL_GP.substring(2, 4);
					
					//B,C동 구분
					if(sBAY_GP.equals("B")|| sBAY_GP.equals("C")){
						if(Integer.parseInt(sSPAN_GP)<=16){
							szYD_SCH_CD = "H" +sBAY_GP +"HC02LM";
						}else{
							szYD_SCH_CD = "H" +sBAY_GP +"HC01LM";
						}
					}else{
						szYD_SCH_CD = "H" +sBAY_GP +"HC01LM";
					}
					//=================================================================================
					
					//재료번호
					//레코드 생성
					inRecord = JDTORecordFactory.getInstance().create();
	
					inRecord.setField("YD_SCH_CD",          		szYD_SCH_CD);//스케줄코드
					inRecord.setField("STL_SH",      				"1");  //LINE_IN 재료매수
					inRecord.setField("STL_NO1", 	  				sSTL_NO);
					inRecord.setField("YD_TO_LOC_DCSN_MTD",			"F");					
					inRecord.setField("YD_AIM_YD_GP",				"J");
					inRecord.setField("YD_AIM_BAY_GP",				sBAY_GP); //작업예약에 목표동 설정처리함
					//inRecord.setField("TO_YD_STK_BED_NO",			sYD_STK_COL_GP.substring(1, 6) +sYD_STK_LYR_NO.substring(2, 3)+ sYD_STK_BED_NO);
					inRecord.setField("TO_YD_STK_BED_NO",			sYD_STK_COL_GP.substring(0, 4) );
					inRecord.setField("YD_USER_ID",					sYD_USER_ID);
					
					// 작업예약 등록 호출
	
					//YD_SCH_CD:스케줄코드,
					//STL_SH: 재료매수,
					//YD_TO_LOC_DCSN_MTD(TO위치 결정 방법)
					//STL_NO(재료번호1,2,3,....)
					//FR_YD_STK_BED_NO(적치배드)
					//TO_YD_STK_BED_NO(가이드가 됨)
					outRecord 	= JDTORecordFactory.getInstance().create();
					ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
					outRecord = (JDTORecord)ejbConn.trx("WrkBookInsertProcTX", new Class[] { JDTORecord.class }, new Object[] { inRecord });
					sRTN_CD			= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
					sYD_WBOOK_ID	= StringHelper.evl(outRecord.getFieldString("YD_WBOOK_ID"), "");
					sRTN_MSG		= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
					if ("0".equals(sRTN_CD)) {
						szMsg = "작업예약 등록시 ERROR";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						gdRes.setMessage(szMsg);	
						return gdRes;				
					}
					
					
					
					//동별 첫번째 작업에약에 대한 스케줄 기동 작업 
					if(!FIRST_YD_BAY_GP.equals(sBAY_GP)){
						FIRST_YD_WBOOK_ID 	= sYD_WBOOK_ID;
						FIRST_YD_SCH_CD 	= szYD_SCH_CD;
		
						
						//스케줄 기동처리 
						if (!FIRST_YD_WBOOK_ID.equals("")) {
							inRecordarr = new JDTORecord[1];
							
							inRecordarr[0] = JDTORecordFactory.getInstance().create();
							inRecordarr[0].setField("YD_SCH_CD"		, FIRST_YD_SCH_CD); 
							inRecordarr[0].setField("YD_WBOOK_ID"	, FIRST_YD_WBOOK_ID); 
							ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
							outRecord2 = (JDTORecord) ejbConn.trx("trxRunSchedule", new Class[] { JDTORecord[].class },	new Object[] { inRecordarr });
				
							sRTN_CD		= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
							sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
							ydUtils.putLog(szSessionName, szMethodName, "스케줄 정상기동여부 : " + sRTN_MSG, YdConstant.DEBUG);
							if (!("1".equals(sRTN_CD))) {
								ydUtils.putLog(szSessionName, szMethodName, "스케줄 기동처리 오류 YD_WBOOK_ID:"+FIRST_YD_WBOOK_ID, YdConstant.INFO);
							} 	
							
							ydUtils.putLog(szSessionName, szMethodName, "스케줄 기동처리 완료", YdConstant.INFO);
						}
					}
					
					FIRST_YD_BAY_GP		= sBAY_GP;
					}
							
					
				szMsg = "결로재 보급 대상("+intRtnVal+"건)작업  완료!<br>";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
				
				gdRes.setMessage(szMsg);
			
				
				//////////////////////////////////////////////////////////////////////////////////////////////////////////				
			}else if(szYD_CHK_GP.equals("2")){
				//////////////////////////////////////////////////////////////////////////////////////////////////////////
				//--------결로재 추출 작업 ----------
				//////////////////////////////////////////////////////////////////////////////////////////////////////////
				
				if("".equals(sIN_BAY_GP)){
					inRecord9.setField("BAY_GP", "");    //전체동 가져 오기     
				}else{
					inRecord9.setField("BAY_GP", sIN_BAY_GP); 
				}
				
//				결로적치장 기준으로 추출대상 가져 오기				
				/*com.inisteel.cim.yd.dao.ydstklyrdao.hotcoilautolist2*/
				intRtnVal = ydEqpDao.getYdEqp(inRecord9, outRecSet, 404);
				if(intRtnVal <= 0) {
					szMsg = "결로적치 추출 대상이 존재 안 합니다.<br>";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					gdRes.setMessage(szMsg);	
					return gdRes;
				}
				szMsg="결로재 추출 대상 건수: " + intRtnVal ;
				ydUtils.putLog(szOperationName, szMethodName, szMsg, YdConstant.DEBUG);		
				
				for (int nLoop =0 ; nLoop<outRecSet.size();nLoop++ ){
					
					recPara =JDTORecordFactory.getInstance().create();
					recPara = outRecSet.getRecord(nLoop);
					
					
					sYD_STK_COL_GP 	=StringHelper.evl(recPara.getFieldString("YD_STK_COL_GP"), "");
					sYD_STK_BED_NO 	=StringHelper.evl(recPara.getFieldString("YD_STK_BED_NO"), "");
					sYD_STK_LYR_NO 	=StringHelper.evl(recPara.getFieldString("YD_STK_LYR_NO"), "");
					sBAY_GP 		=StringHelper.evl(recPara.getFieldString("BAY_GP"), "");
					sSTL_NO 		=StringHelper.evl(recPara.getFieldString("STL_NO"), "");
					
					
					//=================================================================================
					// 결로방지재 추출요구 스케줄 코드 편성
					String sSPAN_GP = sYD_STK_COL_GP.substring(2, 4);
					
					//B,C동 구분
					if(sBAY_GP.equals("B")|| sBAY_GP.equals("C")){
						if(Integer.parseInt(sSPAN_GP)<=16){
							szYD_SCH_CD = "H" +sBAY_GP +"HC02UM";
						}else{
							szYD_SCH_CD = "H" +sBAY_GP +"HC01UM";
						}
					}else{
						szYD_SCH_CD = "H" +sBAY_GP +"HC01UM";
					}
					//=================================================================================
					
					//재료번호
					//레코드 생성
					inRecord = JDTORecordFactory.getInstance().create();
	
					inRecord.setField("YD_SCH_CD",          		szYD_SCH_CD);//스케줄코드
					inRecord.setField("STL_SH",      				"1");  //LINE_IN 재료매수
					inRecord.setField("STL_NO1", 	  				sSTL_NO);
					inRecord.setField("YD_TO_LOC_DCSN_MTD",			"F");					
					inRecord.setField("YD_AIM_YD_GP",				"H");
					inRecord.setField("YD_AIM_BAY_GP",				sBAY_GP); //작업예약에 목표동 설정처리함
					inRecord.setField("TO_YD_STK_BED_NO",			sYD_STK_COL_GP.substring(1, 6) +sYD_STK_LYR_NO.substring(2, 3)+ sYD_STK_BED_NO);
					inRecord.setField("YD_USER_ID",					sYD_USER_ID);
					// 작업예약 등록 호출
	
					//YD_SCH_CD:스케줄코드,
					//STL_SH: 재료매수,
					//YD_TO_LOC_DCSN_MTD(TO위치 결정 방법)
					//STL_NO(재료번호1,2,3,....)
					//FR_YD_STK_BED_NO(적치배드)
					//TO_YD_STK_BED_NO(가이드가 됨)
					outRecord 	= JDTORecordFactory.getInstance().create();
					ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
					outRecord = (JDTORecord)ejbConn.trx("WrkBookInsertProcTX", new Class[] { JDTORecord.class }, new Object[] { inRecord });
					sRTN_CD			= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
					sYD_WBOOK_ID	= StringHelper.evl(outRecord.getFieldString("YD_WBOOK_ID"), "");
					sRTN_MSG		= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
					if ("0".equals(sRTN_CD)) {
						szMsg = "작업예약 등록시 ERROR";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						gdRes.setMessage(szMsg);	
						return gdRes;				
					}
					
					
					
					//동별 첫번째 작업에약에 대한 스케줄 기동 작업 
					if(!FIRST_YD_BAY_GP.equals(sBAY_GP)){
						FIRST_YD_WBOOK_ID 	= sYD_WBOOK_ID;
						FIRST_YD_SCH_CD 	= szYD_SCH_CD;
		
						
						//스케줄 기동처리 
						if (!FIRST_YD_WBOOK_ID.equals("")) {
							inRecordarr = new JDTORecord[1];
							
							inRecordarr[0] = JDTORecordFactory.getInstance().create();
							inRecordarr[0].setField("YD_SCH_CD"		, FIRST_YD_SCH_CD); 
							inRecordarr[0].setField("YD_WBOOK_ID"	, FIRST_YD_WBOOK_ID); 
							ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
							outRecord2 = (JDTORecord) ejbConn.trx("trxRunSchedule", new Class[] { JDTORecord[].class },	new Object[] { inRecordarr });
				
							sRTN_CD		= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
							sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
							ydUtils.putLog(szSessionName, szMethodName, "스케줄 정상기동여부 : " + sRTN_MSG, YdConstant.DEBUG);
							if (!("1".equals(sRTN_CD))) {
								ydUtils.putLog(szSessionName, szMethodName, "스케줄 기동처리 오류 YD_WBOOK_ID:"+FIRST_YD_WBOOK_ID, YdConstant.INFO);
							} 	
							
							ydUtils.putLog(szSessionName, szMethodName, "스케줄 기동처리 완료", YdConstant.INFO);
						}
					}
					
					FIRST_YD_BAY_GP		= sBAY_GP;
					}
							
					
				szMsg = "결로재 추출 대상("+intRtnVal+"건)작업  완료!<br>";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	

					
				gdRes.setMessage(szMsg);
				//////////////////////////////////////////////////////////////////////////////////////////////////////////
			}
	
			return gdRes;
			
		} catch(Exception e){
			szMsg = "C열연 결로재 보급 대상작업 편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			
			throw new DAOException(szMsg);
		}

	} //end of procHotcoilAuto
	
	
	/**
	 * 오퍼레이션명 : HOT COIL이용한 결로방지 선택추출보급
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */          
	public GridData procHotcoilchklist(GridData inDto) throws DAOException  {
		// DAO객체 선언
		YdDaoUtils ydDaoUtils 			= new YdDaoUtils();
		YDComUtil   ydComUtil = new YDComUtil();
		EJBConnector ejbConn 		= null;		
		//레코드 선언
		JDTORecord inRecord       	=  JDTORecordFactory.getInstance().create(); 
		JDTORecord recPara       	=  JDTORecordFactory.getInstance().create();

		JDTORecord outRecord     	= JDTORecordFactory.getInstance().create(); // 
		JDTORecord outRecord2     	= JDTORecordFactory.getInstance().create(); // 
		GridData gdRes 				= null;
		
		JDTORecordSet outRecSet     = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecord[]  inRecordarr   = null;
		
		String szMsg           = "";
		String szMethodName    = "procHotcoilchklist";
		String szOperationName = "HOT COIL이용한 결로방지 선택추출보급";

		
		
		int intRtnVal          = 0;

		//목표행선구분
//		//야드적치Bed입출고상태
//		String szYD_STK_BED_WHIO_STAT  = null;
		//스케줄코드
		String szYD_SCH_CD             	= "";		
		String FIRST_YD_SCH_CD			= "";
		
		//공정구분
		String FIRST_YD_WBOOK_ID	   	= "";
		String FIRST_YD_BAY_GP		   	= "";
		String sYD_WBOOK_ID			   	= "";
		String sRTN_CD					= "";
		String sRTN_MSG					= "";

	
		String sSTL_NO 					= "";
		String sBAY_GP 					= "";
		String sYD_STK_LYR_NO           = "";		
		String sYD_STK_BED_NO          	= "";
		String sYD_STK_COL_GP		    = "";
		String szYD_CHK_GP 				= "";
		String sYD_USER_ID 				= "";
		
		try{
			
			JDTORecord [] inRecordSet = ydComUtil.genGridToJDTORecordAll(inDto);
			gdRes = OperateGridData.cloneResponseGridData(inDto);
			gdRes = CmUtil.copyGDParam(inDto, gdRes);
			
			
			inRecord = CmUtil.genJDTORecord(inDto);
			szYD_CHK_GP 	= ydDaoUtils.paraRecChkNull(inRecord, "YD_CHK_GP"); //1:보급 ,2:추출
			sYD_USER_ID		= ydDaoUtils.paraRecChkNull(inRecord, "YD_USER_ID");
			
			
			gdRes = OperateGridData.cloneResponseGridData(inDto);		
			gdRes = CmUtil.copyGDParam(inDto, gdRes);
			
			YdEqpDao   ydEqpDao   = new YdEqpDao();
			JDTORecord 		inRecord9 	= JDTORecordFactory.getInstance().create();
		
			inRecord9.setField("BAY_GP", "");    //전체동 가져 오기
			
			
			if(szYD_CHK_GP.equals("1")){
				//////////////////////////////////////////////////////////////////////////////////////////////////////////
				//--------결로재 보급 작업 ----------
				//////////////////////////////////////////////////////////////////////////////////////////////////////////
				
				//	결로적치장 기준으로 HOT COIL대상 가져 오기				
				/*com.inisteel.cim.yd.dao.ydstklyrdao.hotcoilautolist*/
				intRtnVal = ydEqpDao.getYdEqp(inRecord9, outRecSet, 403);
				if(intRtnVal <= 0) {
					szMsg = "결로적치 보급 대상이 존재 안 합니다.<br>";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
		
					gdRes.setMessage(szMsg);	
					return gdRes;
				}
				szMsg="결로재 보급 대상 건수: " + intRtnVal ;
				ydUtils.putLog(szOperationName, szMethodName, szMsg, YdConstant.DEBUG);		
				
				for (int nLoop =0 ; nLoop<outRecSet.size();nLoop++ ){
					
					recPara =JDTORecordFactory.getInstance().create();
					recPara = outRecSet.getRecord(nLoop);
					
					
					sYD_STK_COL_GP 	=StringHelper.evl(recPara.getFieldString("YD_STK_COL_GP"), "");
					sYD_STK_BED_NO 	=StringHelper.evl(recPara.getFieldString("YD_STK_BED_NO"), "");
					sYD_STK_LYR_NO 	=StringHelper.evl(recPara.getFieldString("YD_STK_LYR_NO"), "");
					sBAY_GP 		=StringHelper.evl(recPara.getFieldString("BAY_GP"), "");
					sSTL_NO 		=StringHelper.evl(recPara.getFieldString("STL_NO"), "");
					

					//=================================================================================
					// 결로방지재 추출요구 스케줄 코드 편성
					String sSPAN_GP = sYD_STK_COL_GP.substring(2, 4);
					
					//B,C동 구분
					if(sBAY_GP.equals("B")|| sBAY_GP.equals("C")){
						if(Integer.parseInt(sSPAN_GP)<=16){
							szYD_SCH_CD = "J" +sBAY_GP +"HC02LM";
						}else{
							szYD_SCH_CD = "J" +sBAY_GP +"HC01LM";
						}
					}else{
						szYD_SCH_CD = "J" +sBAY_GP +"HC01LM";
					}
					//=================================================================================
					
					//재료번호
					//레코드 생성
					inRecord = JDTORecordFactory.getInstance().create();
	
					inRecord.setField("YD_SCH_CD",          		szYD_SCH_CD);//스케줄코드
					inRecord.setField("STL_SH",      				"1");  //LINE_IN 재료매수
					inRecord.setField("STL_NO1", 	  				sSTL_NO);
					inRecord.setField("YD_TO_LOC_DCSN_MTD",			"F");					
					inRecord.setField("YD_AIM_YD_GP",				"J");
					inRecord.setField("YD_AIM_BAY_GP",				sBAY_GP); //작업예약에 목표동 설정처리함
					inRecord.setField("TO_YD_STK_BED_NO",			sYD_STK_COL_GP.substring(1, 6) +sYD_STK_LYR_NO.substring(2, 3)+ sYD_STK_BED_NO);
					inRecord.setField("YD_USER_ID",					sYD_USER_ID);
					
					// 작업예약 등록 호출
	
					//YD_SCH_CD:스케줄코드,
					//STL_SH: 재료매수,
					//YD_TO_LOC_DCSN_MTD(TO위치 결정 방법)
					//STL_NO(재료번호1,2,3,....)
					//FR_YD_STK_BED_NO(적치배드)
					//TO_YD_STK_BED_NO(가이드가 됨)
					outRecord 	= JDTORecordFactory.getInstance().create();
					ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
					outRecord = (JDTORecord)ejbConn.trx("WrkBookInsertProcTX", new Class[] { JDTORecord.class }, new Object[] { inRecord });
					sRTN_CD			= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
					sYD_WBOOK_ID	= StringHelper.evl(outRecord.getFieldString("YD_WBOOK_ID"), "");
					sRTN_MSG		= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
					if ("0".equals(sRTN_CD)) {
						szMsg = "작업예약 등록시 ERROR";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						gdRes.setMessage(szMsg);	
						return gdRes;				
					}
					
					
					
					//동별 첫번째 작업에약에 대한 스케줄 기동 작업 
					if(!FIRST_YD_BAY_GP.equals(sBAY_GP)){
						FIRST_YD_WBOOK_ID 	= sYD_WBOOK_ID;
						FIRST_YD_SCH_CD 	= szYD_SCH_CD;
		
						
						//스케줄 기동처리 
						if (!FIRST_YD_WBOOK_ID.equals("")) {
							inRecordarr = new JDTORecord[1];
							
							inRecordarr[0] = JDTORecordFactory.getInstance().create();
							inRecordarr[0].setField("YD_SCH_CD"		, FIRST_YD_SCH_CD); 
							inRecordarr[0].setField("YD_WBOOK_ID"	, FIRST_YD_WBOOK_ID); 
							ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
							outRecord2 = (JDTORecord) ejbConn.trx("trxRunSchedule", new Class[] { JDTORecord[].class },	new Object[] { inRecordarr });
				
							sRTN_CD		= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
							sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
							ydUtils.putLog(szSessionName, szMethodName, "스케줄 정상기동여부 : " + sRTN_MSG, YdConstant.DEBUG);
							if (!("1".equals(sRTN_CD))) {
								ydUtils.putLog(szSessionName, szMethodName, "스케줄 기동처리 오류 YD_WBOOK_ID:"+FIRST_YD_WBOOK_ID, YdConstant.INFO);
							} 	
							
							ydUtils.putLog(szSessionName, szMethodName, "스케줄 기동처리 완료", YdConstant.INFO);
						}
					}
					
					FIRST_YD_BAY_GP		= sBAY_GP;
					}
							
					
				szMsg = "결로재 보급 대상("+intRtnVal+"건)작업  완료!<br>";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
				
				gdRes.setMessage(szMsg);
			
				
				//////////////////////////////////////////////////////////////////////////////////////////////////////////				
			}else if(szYD_CHK_GP.equals("2")){
				//////////////////////////////////////////////////////////////////////////////////////////////////////////
				//--------결로재 추출 작업 ----------
				//////////////////////////////////////////////////////////////////////////////////////////////////////////

				szMsg="결로재 추출 대상 건수: " + inRecordSet.length ;
				ydUtils.putLog(szOperationName, szMethodName, szMsg, YdConstant.DEBUG);		
				
				for (int nLoop =0 ; nLoop<inRecordSet.length;nLoop++ ){

					sBAY_GP 		=ydDaoUtils.paraRecChkNull(inRecordSet[nLoop], "BAY_GP");
					sSTL_NO 		=ydDaoUtils.paraRecChkNull(inRecordSet[nLoop], "STL_NO");
					
					
					inRecord9 	= JDTORecordFactory.getInstance().create();					
					inRecord9.setField("BAY_GP", sBAY_GP);    //전체동 가져 오기
					
					/*com.inisteel.cim.yd.dao.ydstklyrdao.hotcoilchklist*/
					intRtnVal = ydEqpDao.getYdEqp(inRecord9, outRecSet, 405);
					if(intRtnVal <= 0) {
						szMsg = "결로적치 추출저장위치가 존재 안 합니다.<br>";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						gdRes.setMessage(szMsg);	
						return gdRes;	
					}else{
						recPara =JDTORecordFactory.getInstance().create();
						recPara = outRecSet.getRecord(0);
						
						
						sYD_STK_COL_GP 	=StringHelper.evl(recPara.getFieldString("YD_STK_COL_GP"), "");
						sYD_STK_BED_NO 	=StringHelper.evl(recPara.getFieldString("YD_STK_BED_NO"), "");
						sYD_STK_LYR_NO 	=StringHelper.evl(recPara.getFieldString("YD_STK_LYR_NO"), "");
							
						
						//=================================================================================
						// 결로방지재 추출요구 스케줄 코드 편성
						String sSPAN_GP = sYD_STK_COL_GP.substring(2, 4);
						
						//B,C동 구분
						if(sBAY_GP.equals("B")|| sBAY_GP.equals("C")){
							if(Integer.parseInt(sSPAN_GP)<=16){
								szYD_SCH_CD = "H" +sBAY_GP +"HC02UM";
							}else{
								szYD_SCH_CD = "H" +sBAY_GP +"HC01UM";
							}
						}else{
							szYD_SCH_CD = "H" +sBAY_GP +"HC01UM";
						}
						//=================================================================================
						
						//재료번호
						//레코드 생성
						inRecord = JDTORecordFactory.getInstance().create();
		
						inRecord.setField("YD_SCH_CD",          		szYD_SCH_CD);//스케줄코드
						inRecord.setField("STL_SH",      				"1");  //LINE_IN 재료매수
						inRecord.setField("STL_NO1", 	  				sSTL_NO);
						inRecord.setField("YD_TO_LOC_DCSN_MTD",			"F");					
						inRecord.setField("YD_AIM_YD_GP",				"H");
						inRecord.setField("YD_AIM_BAY_GP",				sBAY_GP); //작업예약에 목표동 설정처리함
						inRecord.setField("TO_YD_STK_BED_NO",			sYD_STK_COL_GP.substring(1, 6) +sYD_STK_LYR_NO.substring(2, 3)+ sYD_STK_BED_NO);
						inRecord.setField("YD_USER_ID",					sYD_USER_ID);
						// 작업예약 등록 호출
		
						//YD_SCH_CD:스케줄코드,
						//STL_SH: 재료매수,
						//YD_TO_LOC_DCSN_MTD(TO위치 결정 방법)
						//STL_NO(재료번호1,2,3,....)
						//FR_YD_STK_BED_NO(적치배드)
						//TO_YD_STK_BED_NO(가이드가 됨)
						outRecord 	= JDTORecordFactory.getInstance().create();
						ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
						outRecord = (JDTORecord)ejbConn.trx("WrkBookInsertProcTX", new Class[] { JDTORecord.class }, new Object[] { inRecord });
						sRTN_CD			= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
						sYD_WBOOK_ID	= StringHelper.evl(outRecord.getFieldString("YD_WBOOK_ID"), "");
						sRTN_MSG		= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
						if ("0".equals(sRTN_CD)) {
							szMsg = "작업예약 등록시 ERROR";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							gdRes.setMessage(szMsg);	
							return gdRes;				
						}
						
						
						
						//동별 첫번째 작업에약에 대한 스케줄 기동 작업 
						if(!FIRST_YD_BAY_GP.equals(sBAY_GP)){
							FIRST_YD_WBOOK_ID 	= sYD_WBOOK_ID;
							FIRST_YD_SCH_CD 	= szYD_SCH_CD;
			
							
							//스케줄 기동처리 
							if (!FIRST_YD_WBOOK_ID.equals("")) {
								inRecordarr = new JDTORecord[1];
								
								inRecordarr[0] = JDTORecordFactory.getInstance().create();
								inRecordarr[0].setField("YD_SCH_CD"		, FIRST_YD_SCH_CD); 
								inRecordarr[0].setField("YD_WBOOK_ID"	, FIRST_YD_WBOOK_ID); 
								ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
								outRecord2 = (JDTORecord) ejbConn.trx("trxRunSchedule", new Class[] { JDTORecord[].class },	new Object[] { inRecordarr });
					
								sRTN_CD		= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
								sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
								ydUtils.putLog(szSessionName, szMethodName, "스케줄 정상기동여부 : " + sRTN_MSG, YdConstant.DEBUG);
								if (!("1".equals(sRTN_CD))) {
									ydUtils.putLog(szSessionName, szMethodName, "스케줄 기동처리 오류 YD_WBOOK_ID:"+FIRST_YD_WBOOK_ID, YdConstant.INFO);
								} 	
								
								ydUtils.putLog(szSessionName, szMethodName, "스케줄 기동처리 완료", YdConstant.INFO);
							}
						}
						
						FIRST_YD_BAY_GP		= sBAY_GP;
					}
					}
							
					
				szMsg = "결로재 추출 대상("+intRtnVal+"건)작업  완료!<br>";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	

					
				gdRes.setMessage(szMsg);
				//////////////////////////////////////////////////////////////////////////////////////////////////////////
			}
	
			return gdRes;
			
		} catch(Exception e){
			szMsg = "C열연 결로재 보급 대상작업 편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			
			throw new DAOException(szMsg);
		}

	} //end of procHotcoilchklist
	
	/**
	 *  결로재재고조회 
	 * 심명순(090713)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getHotcoilStrLocList(JDTORecord inDto) throws DAOException {
		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");

		String szMsg            = "";
		String szMethodName     = "getHotcoilStrLocList";
		int intRtnVal = 0;

		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();

		try {
			//적치열 구분과 적치베드 번호로 분리
			recPara.setField("YD_STK_COL_GP",  yddatautil.setDataDefault(inDto.getField("STOCK_POS"), ""));
			recPara.setField("CURR_PROG_CD",      yddatautil.setDataDefault(inDto.getField("CURR_PROG_CD"), ""));

			
			recPara.setField("PAGE_CNT",inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT",inDto.getField("ROWCOUNT"));
 		
 			
			/*"com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrCoilGdsPos_n_PIDEV*/

//PIDEV_S :병행가동용:PI_YD
			recPara.setField("PI_YD",    	"J");				
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 621);
					
			
			if (intRtnVal <= 0) {
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
			ydUtils.putLog(szSessionName, szMethodName, szMsg,  YdConstant.DEBUG);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getHotcoilStrLocList
	
	/**
	 *  대기장도착등록(운전자용)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getWaitLocArrBackupList(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getWaitLocArrBackupList";

		YdStkBedDao ydStkBedDao = new YdStkBedDao();
		
		int intRtnVal = 0;
		
		try {
			recPara.setField("YD_GP",       yddatautil.setDataDefault(inDto.getField("YD_GP"), ""));
//PIDEV_S :병행가동용:PI_YD
			recPara.setField("PI_YD",    	yddatautil.setDataDefault(inDto.getField("YD_GP"), ""));
			/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getWaitLocArrBackupList_PIDEV*/
			intRtnVal = ydStkBedDao.getYdStkbed(recPara, outRecSet, 316);
		
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
			
			logger.println(LogLevel.DEBUG_TEXT, "getWaitLocArrBackupList");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getWaitLocArrBackupList
	
	/**
	 * 전체코일야드현황조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getYdMgtTotalList(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		
		String szMsg        	= "";
		String szMethodName 	= "getYdMgtTotalList";	
		String szOperationName 	= "전체코일야드현황조회";
		String sDD_CHK 			= "";
		
		YdEqpDao ydEqpDao = new YdEqpDao();
		
		int intRtnVal = 0;
		
		try {		
				    
			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			 
			
			 /*com.inisteel.cim.yd.dao.ydeqpdao.getYdMgtTotalList*/
		    intRtnVal = ydEqpDao.getYdEqp(recPara, outRecSet, 806);
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
	}//end of getYdMgtTotalList
	
	
	
	/**
	 * 차량 작업 관리 화면 : 초기화 처리(C열연 )
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return 
	 * @return 
	 * @return
	 * @throws DAOException
	 */
	
	public JDTORecord CarinfoResetC(JDTORecord inDto) throws DAOException {
 
		String szMsg        	= "";		
		String szMethodName 	= "CarinfoResetC";
		
		String szYD_CAR_SCH_ID 	= null;
		String szYD_USER_ID		= "CarinfoRes";
		String szTRN_EQP_CD		="";
		
		JDTORecord[] 	inRecordarr   	= null;			
		JDTORecord 		outRecord 		= JDTORecordFactory.getInstance().create(); // 
 
 		EJBConnector 	ejbConn 		= null; 
 		ymCommonDAO dao = ymCommonDAO.getInstance();
 		
		try {
			szMsg = "["+szMethodName+"] 메소드 시작  "  ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			szTRN_EQP_CD = ydDaoUtils.paraRecChkNull(inDto, "TRN_EQP_CD");
			
			String QueryId 	= "ym.tsinfo.getListSposYNchk2";
		    List sposYNChklist = dao.getCommonList(QueryId, new Object[]{szTRN_EQP_CD});
		    if(sposYNChklist.size() > 0)
		    {
		    	JDTORecord AvaPointList = (JDTORecord)sposYNChklist.get(0);
		    	szYD_CAR_SCH_ID 	= StringHelper.evl(AvaPointList.getFieldString("YD_CAR_SCH_ID"),"");
		     
				// 차량스케줄취소  호출	
				inRecordarr = new JDTORecord[1]; 
				inRecordarr[0] = JDTORecordFactory.getInstance().create();
				inRecordarr[0].setField("YD_CAR_SCH_ID"		, szYD_CAR_SCH_ID); 
				inRecordarr[0].setField("YD_USER_ID"	    , szYD_USER_ID); 
	
				ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
				outRecord 	 = (JDTORecord)ejbConn.trx("updCarWrMgt",new Class[] { JDTORecord[].class }, new Object[] { inRecordarr });
	  
				outRecord.setField("RTN_CD" 	, "1");	
		    }else{
		    	outRecord.setField("RTN_CD" 	, "0");	
		    }
			
			
			return outRecord;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}//end of CarinfoResetC	
	
	/**
	 * 야드관리 > 통합슬라브야드 > Monitoring > 슬라브이송지연사유등록 (수정)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord[]
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord updSlabTotYdToMoveMgt(JDTORecord[] inDto) throws DAOException {
		int intRtnVal 		= 0;
		String szMsg		= null;
		String szMethodName = null;
		CoilGdsJspDao dao   = new CoilGdsJspDao();
		JDTORecord recPara  = JDTORecordFactory.getInstance().create();
		JDTORecord outRec   = JDTORecordFactory.getInstance().create();
		JDTORecord retRrd   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = null;

		
		szMsg        = "";
		szMethodName = "updSlabTotYdToMoveMgt";
			
		
		try {
			
			
			szMsg = "JSP-SESSION [슬라브이송지연사유등록 (수정)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			//저장위치 좌표설정화면 BED 수정
			for(int x=0;x<inDto.length;x++){
 
//			      
				recPara.setField("V_MODIFIER",				inDto[x].getFieldString("YD_USER_ID")); //수정자
				recPara.setField("V_SNDBK_GP_ETC", 			inDto[x].getField("SNDBK_GP_ETC"));  // 지연사유   
				recPara.setField("V_STL_NO", 				inDto[x].getField("STL_NO"));  // SLAB번호                    
 
				intRtnVal = dao.updSlabTotYdToMoveInfo(recPara);
				
				if (intRtnVal < 1) {
					retRrd.setField("RTN_MSG", "슬라브이송지연사유등록 수정중 오류가 발생 하였습니다.");
					retRrd.setField("RTN_CD", "0");
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
		
		szMsg = "JSP-SESSION [슬라브이송지연사유등록 (수정)] 끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		
		return retRrd;
		
	}	// end of updSlabTotYdToMoveMgt
	
	/**
	 *  하차작업등록 (반품,회송,부분하차)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */	
	public JDTORecord regCarUdWrk(GridData gdReq) throws DAOException {
		
		EJBConnector ejbConn 			= null;
		
		String methodNm = "[CoilGdsJspSeEjbBean.regCarUdWrk]";
		String logId = gdReq.getIPAddress();
		String szLogMsg = ""; 
		
		try {
			
			szLogMsg = "JSP-FACADE [ " + methodNm +"] 시작";
			ydUtils.putLog(szSessionName, methodNm, szLogMsg, YdConstant.INFO);			
			
			int intRtnVal 	= 0;
			
			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			CoilSpecRegSeEJBBean CoilSpecRegSeEJBBean=new CoilSpecRegSeEJBBean();
			YdCarFtmvMtlDao ydCarftmvmtlDao = new YdCarFtmvMtlDao();
			YdPlateCommDAO 	commDao 	= new YdPlateCommDAO(); //3기 후판제품공용dao
			YdCarSchDao ydCarSchDao	= new YdCarSchDao();
			YdStockDao ydStockDao = new YdStockDao();
			ymCommonDAO dao = ymCommonDAO.getInstance();
			
			YdDelegate		ydDelegate 		= new YdDelegate();
			
			JDTORecordSet 	rsResult	= null;
			JDTORecord		recParam	= null;
			JDTORecord		recTemp		= null;
			
			JDTORecord outRecord = JDTORecordFactory.getInstance().create();
			JDTORecord recEditColumn	= JDTORecordFactory.getInstance().create();
			JDTORecord recOutTemp	= JDTORecordFactory.getInstance().create();
			JDTORecord inRecord = null;
			String szMsg = null;
			
			String szStlNo = null;
			String szMsgContents = null;
			String szCAR_KIND = "";
			String szYD_STK_BED_NO = null;
			String szWLOC_CD = null;
			String szYD_STK_COL_GP = null;
			String szYD_PNT_CD = null;
			String szTRANS_ORD_DATE = null;
			String szTRANS_ORD_SEQNO = null;
			String szUNIQUE_ID	= null;
			String szYD_CAR_SCH_ID = null;
			String sQueryId = "";
			String[] rVal = new String[1];
			String szCoilWt = "";
			//------------------------------------------------------------------
			//화면으로 부터 전달 받은 정보
			String szCAR_NO 		= gdReq.getParam("CAR_NO").toUpperCase();
			String szRETN_WK_GP 	= gdReq.getParam("RETN_WK_GP");  //1:반품, 2:회송, 3:부분하차 , 4:소재반품
			String szYD_CARPNT_CD 	= gdReq.getParam("YD_CARPNT_CD").toUpperCase();
			String szTEL_NO			= StringHelper.evl(gdReq.getParam("TEL_NO"),"00000000000");
			String szCARD_NO		= StringHelper.evl(gdReq.getParam("CARD_NO"),"0000").toUpperCase(); //차량번호의 뒤의 4자리를 사용한다.
			String szUser			= gdReq.getParam("YD_USER_ID");
			//------------------------------------------------------------------
			
			//151117 hun 화면으로 부터 전달 받은 정보로 중복 등록 불가 체크
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {
				
				szStlNo = slabUtils.trim(gdReq.getHeader("STL_NO").getValue(ii));
			
				// 전달받은 재료번호와 차량 번호로 TB_YD_CARSCH 조회
				rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
				recTemp = JDTORecordFactory.getInstance().create();
				recTemp.setField("CAR_NO",           szCAR_NO);
				recTemp.setField("STL_NO",           szStlNo);
			    sQueryId = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarYdCarMtlDEL_YN";
			    intRtnVal = commDao.select(recTemp, rsResult, sQueryId);
			    
			    if(intRtnVal>0){
			    	szMsg="["+methodNm+"] 해당재료로 등록된 차량 스케줄이 있습니다. ";
					ydUtils.putLog(szSessionName, methodNm, szMsg, YdConstant.ERROR);
					jrRtn.setField("RTN_CD" , "0");	
					jrRtn.setField("RTN_MSG", szMsg);	
					return jrRtn;
							
			    }
			}
			
			//151117 hun 화면으로 부터 전달 받은 차량번호가 GT(TT카) 일때 초기화 호출
			// TT카이고 부분하차 일경우 강제로 빼고 다시 넣어야 함...
			if(szCAR_NO.startsWith("GT")){
				szCAR_KIND = "TT";
			}else{
				szCAR_KIND = "TR";
			}
			
			if("TT".equals(szCAR_KIND) && "3".equals(szRETN_WK_GP)){
				// 차량 초기화 호출
				
				JDTORecord[] inCarRecord = new JDTORecord[1];
				
				// 전달받은 차량 번호로 TB_YD_CARSCH 조회
				rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
				recTemp = JDTORecordFactory.getInstance().create();
				recTemp.setField("CAR_NO",           szCAR_NO);
				recTemp.setField("TRN_EQP_CD",           szCAR_NO);
			    sQueryId = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschDaoByYdCarNoOrTrnEqpCd";
			    intRtnVal = commDao.select(recTemp, rsResult, sQueryId);
			    
			    rsResult.first();
		        recOutTemp = JDTORecordFactory.getInstance().create();
		        recOutTemp = rsResult.getRecord();
		        szYD_CAR_SCH_ID = recOutTemp.getFieldString("YD_CAR_SCH_ID");
			    
				inCarRecord[0] = JDTORecordFactory.getInstance().create();
				inCarRecord[0].setField("YD_USER_ID", 					"YdSystem");
				inCarRecord[0].setField("YD_CAR_SCH_ID", 				szYD_CAR_SCH_ID);
				inCarRecord[0].setField("TRN_EQP_CD", 					szCAR_NO);
				
				ejbConn = new EJBConnector("default", "CoilGdsJspSeEJB", this);
				outRecord = (JDTORecord)ejbConn.trx("updCarWrMgt", new Class[] { JDTORecord[].class }, new Object[] { inCarRecord });
				
				
			}
			
			
			//------------------------------------------------------------------
			//YD_CARPNT_CD 로 WLOC_CD, YD_PNT_CD, 하차위치(YD_STK_COL_GP)를 조회한다.
			recParam = JDTORecordFactory.getInstance().create();
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			recParam.setField("YD_CARPNT_CD", szYD_CARPNT_CD);
			
			intRtnVal = commDao.select(recParam, rsResult, "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarPoint");
			
			if(intRtnVal<1) {
				szMsg="["+methodNm+"] 차량포인트 조회 실패!! - intRtnVal : " + intRtnVal;
				ydUtils.putLog(szSessionName, methodNm, szMsg, YdConstant.ERROR);
				throw new Exception(szMsg);
			}
			
			rsResult.first();
			recTemp		= rsResult.getRecord();
			
			szWLOC_CD    		= StringHelper.evl(recTemp.getFieldString("WLOC_CD"), "");
			szYD_STK_COL_GP    	= StringHelper.evl(recTemp.getFieldString("YD_STK_COL_GP"), "");		
			szYD_PNT_CD	    	= StringHelper.evl(recTemp.getFieldString("YD_PNT_CD"), "");	
			
			//------------------------------------------------------------------
			//운송지시일자, 순번 생성  (999001 처럼 앞에  999를 붙인다.)
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			intRtnVal = commDao.select(recParam, rsResult, "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getRetnTransOrdNo");
			
			if(intRtnVal<1) {
				szMsg="["+methodNm+"] 운송지시일자,순번  생성시  오류발생 실패!! - intRtnVal : " + intRtnVal;
				ydUtils.putLog(szSessionName, methodNm, szMsg, YdConstant.ERROR);
				throw new Exception(szMsg);
			}
			
			rsResult.first();
			recTemp		= rsResult.getRecord();
			
			szTRANS_ORD_DATE		= ydDaoUtils.paraRecChkNull(recTemp,"TRANS_ORD_DATE");
			szTRANS_ORD_SEQNO		= ydDaoUtils.paraRecChkNull(recTemp,"TRANS_ORD_SEQNO");
			
			//------------------------------------------------------------------------
			//차량스케줄 생성 전에 입동대기중인 차량들에서 IF_SEQ_NO MAX값을 읽어온다. 
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recParam.setField("WLOC_CD"	, szWLOC_CD);
			recParam.setField("YD_GP"		, szYD_STK_COL_GP.substring(0,1));
			recParam.setField("YD_BAY_GP"	, szYD_STK_COL_GP.substring(1,2));
			
			intRtnVal = commDao.select(recParam, rsResult, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0082");
			
			if(intRtnVal<1) {
				szMsg="["+methodNm+"] IF_SEQ_NO 조회시  오류발생 - intRtnVal : " + intRtnVal;
				ydUtils.putLog(szSessionName, methodNm, szMsg, YdConstant.ERROR);
				szUNIQUE_ID = "0";
			} else {
				rsResult.first();
				recTemp		= rsResult.getRecord();
				
				szUNIQUE_ID		= ydDaoUtils.paraRecChkNull(recTemp,"IF_SEQ_NO");
			}
			
			
		    //--------------------------------------------------------------------------
			//1. Stock 생성 및 수정
			
			recEditColumn.setField("TRANS_ORD_DATE", 		szTRANS_ORD_DATE);
			recEditColumn.setField("TRANS_ORD_SEQNO", 		szTRANS_ORD_SEQNO); 
			recEditColumn.setField("CARD_NO", 				szCARD_NO);
			recEditColumn.setField("CAR_NO", 				szCAR_NO);
			recEditColumn.setField("MODIFIER", 				szUser);
			recEditColumn.setField("STL_APPEAR_GP", 		"Y");
			
			//수정할 레코드 수
			//int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {
				
				szStlNo = slabUtils.trim(gdReq.getHeader("STL_NO").getValue(ii));
				szYD_STK_BED_NO = slabUtils.trim(gdReq.getHeader("YD_STK_BED_NO").getValue(ii));
				szMsgContents = slabUtils.trim(gdReq.getHeader("MSG_CONTENTS").getValue(ii));
				szCoilWt = slabUtils.trim(gdReq.getHeader("COIL_WT").getValue(ii));
				
	    		//C열연 코일 저장품 등록 
	    		CoilSpecRegSeEJBBean.stockProcCom(szStlNo,1);
	    		
	    		recEditColumn.setField("STL_NO", 				szStlNo);
	    		recEditColumn.setField("YD_CAR_UPP_LOC_CD", 	szYD_STK_BED_NO); //야드 차상위치코드
	    		
				//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
	    		recParam.setField("STL_NO", szStlNo);
				rVal= YdCommonUtils.getYdAimRtGp("C",recParam );		
				//recEditColumn.setField("YD_AIM_RT_GP", rVal[0]);
				
				if("4".equals(szRETN_WK_GP)){
					recEditColumn.setField("YD_AIM_RT_GP", "B3");
				}else{
					recEditColumn.setField("YD_AIM_RT_GP", "A1");
				}
				
				recEditColumn.setField("STL_PROG_CD", rVal[1]);
				recEditColumn.setField("DEL_YN", "N");
				recEditColumn.setField("MSG_CONTENTS",szMsgContents);
				recEditColumn.setField("SNDBK_REGISTER",szUser);
				recEditColumn.setField("YD_MTL_WT",szCoilWt);
				
	    		intRtnVal = ydStockDao.updYdStockReg(recEditColumn);
	    		
			}
			
		    //--------------------------------------------------------------------------
			//2. 차량스케줄 생성
			szYD_CAR_SCH_ID = ydCarSchDao.getYdCarschId();
			recParam = JDTORecordFactory.getInstance().create();
			recParam.setField("YD_CAR_SCH_ID",    		szYD_CAR_SCH_ID);
			recParam.setField("REGISTER",         		szUser);
			recParam.setField("YD_EQP_WRK_STAT",  		"L");									//야드설비작업상태(영차:하차해야함)
			recParam.setField("YD_EQP_ID",        		YdConstant.YD_DM_CAR_EQP_ID);			//야드설비ID
			recParam.setField("YD_CAR_USE_GP",    		YdConstant.YD_CAR_USE_GP_DM);			//차량사용구분
			recParam.setField("CAR_KIND", 				szCAR_KIND);
			recParam.setField("ARR_WLOC_CD",     		szWLOC_CD);								//착지개소코드
			recParam.setField("SPOS_WLOC_CD",     		szWLOC_CD);								//발지개소코드
			recParam.setField("TRANS_EQUIPMENT_TYPE",   "P");									//운송장비Type
			recParam.setField("YD_PNT_CD3",     		szYD_PNT_CD);							//야드포인트코드3
			recParam.setField("CAR_NO",           		szCAR_NO);								//차량번호
			recParam.setField("CARD_NO",          		szCARD_NO);								//카드번호
			recParam.setField("YD_CARUD_LEV_DT",  		YdUtils.getCurDate("yyyyMMddHHmmss"));	//하차출발일시
			recParam.setField("TRANS_ORD_DATE",   		szTRANS_ORD_DATE);						//운송지시일자
			recParam.setField("TRANS_ORD_SEQNO",  		szTRANS_ORD_SEQNO);						//운송지시순번
			recParam.setField("YD_CARUD_STOP_LOC",		szYD_STK_COL_GP);						//차량하차정지위치
			recParam.setField("YD_BAYIN_WO_SEQ",  		YdConstant.YD_BAYIN_WO_SEQ_DEFAULT);	//입동지시순번 --기본값으로 설정(9) **
			recParam.setField("YD_CAR_PROG_STAT", 		YdConstant.YD_CARUD_LEV);				//하차출발상태(A)
			recParam.setField("IF_SEQ_NO", 				szUNIQUE_ID);							// 운송지시 SEQ
			recParam.setField("TEL_NO", 				szTEL_NO);								// 전화번호
			recParam.setField("YD_CAR_WRK_GP", 			szRETN_WK_GP);							// 야드차량작업구분  
			
    		//차량스케줄 등록
	    	intRtnVal = ydCarSchDao.insYdCarsch(recParam);
    		if( intRtnVal <= 0 ){
				szMsg="[" + methodNm + "] 차량스케줄 생성 시 오류발생[반환값 : " + intRtnVal + "]";
				ydUtils.putLog(szSessionName, methodNm, szMsg, YdConstant.ERROR);
				throw new Exception(szMsg);
    		}
    		
    		szMsg="[" + methodNm + "] 차량스케줄 생성 완료";
			ydUtils.putLog(szSessionName, methodNm, szMsg, YdConstant.DEBUG);
			
		    //--------------------------------------------------------------------------
			//3.차량스케줄재료 생성
			for (int ii = 0; ii < rowCnt; ii++) {
				
				szStlNo = slabUtils.trim(gdReq.getHeader("STL_NO").getValue(ii));
				szYD_STK_BED_NO = slabUtils.trim(gdReq.getHeader("YD_STK_BED_NO").getValue(ii));
				
				recParam.setField("YD_CAR_SCH_ID",    	szYD_CAR_SCH_ID);
				recParam.setField("REGISTER",         	szUser);
				recParam.setField("STL_NO", 			szStlNo);	 
				recParam.setField("YD_STK_BED_NO", 		szYD_STK_BED_NO); //야드 차상위치코드
				recParam.setField("YD_STK_LYR_NO", 		"001");
				recParam.setField("DEL_YN", 			"N");
		 
				intRtnVal = ydCarftmvmtlDao.insYdCarftmvmtl(recParam);
	    		if(intRtnVal != 1) {
	    			szMsg="[" + methodNm + "] 차량스케줄재료 생성 시 오류발생[반환값 : " + intRtnVal + "]";
					ydUtils.putLog(szSessionName, methodNm, szMsg, YdConstant.INFO);
					throw new Exception(szMsg);
	    		}
			}
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				szStlNo = slabUtils.trim(gdReq.getHeader("STL_NO").getValue(ii));
				//L2저장품재원 정보 송신
				//======================================================
				// 저장품제원 : 코일야드L2로 송신(YDY5L002)
				//======================================================
				JDTORecord recResult = null;
				recResult = JDTORecordFactory.getInstance().create();
				recResult.setField("MSG_ID"         , "YDY5L002");
				recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
				recResult.setField("STL_NO"         , szStlNo);
				recResult.setField("YD_STK_COL_GP"  , "");
				recResult.setField("YD_STK_BED_NO"  , "");
				
				ydDelegate.sendMsg(recResult);
	
				szMsg = "코일야드L2로 응답전문 [YDY5L002] 전송완료";
				ydUtils.putLog(szSessionName, methodNm, szMsg, YdConstant.DEBUG);
			}
		
		    //--------------------------------------------------------------------------
			//4. 입동지시 요구
			szMsg="[" + methodNm + "] 차량입동포인트[" + szYD_STK_COL_GP + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 호출 시작";
			ydUtils.putLog(szSessionName, methodNm, szMsg, YdConstant.DEBUG);	 
			
			recParam = JDTORecordFactory.getInstance().create();
			recParam.setField("JMS_TC_CD",  		"YDYDJ662");
			recParam.setField("YD_CARPNT_CD",    	szYD_CARPNT_CD);	//입동포인트
			recParam.setField("YD_CAR_SCH_ID",    	szYD_CAR_SCH_ID);	//차량스케줄ID
			recParam.setField("CAR_NO", 			szCAR_NO);
			recParam.setField("CARD_NO", 			szCARD_NO);
			recParam.setField("CAR_KIND", 			szCAR_KIND);
			ydUtils.displayRecord(methodNm, recParam);	 
			ydDelegate.sendMsg(recParam);
			
//			ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);			
//		    ejbConn.trx("procCarBayInOrdReqNEW", new Class[] { JDTORecord.class }, new Object[] { recParam });					
			
			szMsg="[" + methodNm + "] 차량입동포인트[" + szYD_STK_COL_GP + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 호출 성공";
			ydUtils.putLog(szSessionName, methodNm, szMsg, YdConstant.DEBUG);
			
			
			//--------------------------------------------------------------------------
			//3.차량스케줄재료 생성
			
			
			
			//--------------------------------------------------------------------------
			szLogMsg = "JSP-FACADE [ " + methodNm +"] 끝";
			ydUtils.putLog(szSessionName, methodNm, szLogMsg, YdConstant.INFO);			
			
			jrRtn.setField("RTN_CD" , "0");	
			jrRtn.setField("RTN_MSG", "정상적으로 등록하였습니다.");	
			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	} //regCarUdWrk
	
	/**
	 *  차량예정정보 전송 백업 화면
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */	
	public JDTORecord regCarUdExplainInfo(JDTORecord[] inDto) throws DAOException {
		
		YdCarSchDao ydCarSchDao   = new YdCarSchDao();
		JDTORecordSet rsResult    = JDTORecordFactory.getInstance().createRecordSet("");
		
		EJBConnector ejbConn 			= null;
		JDTORecord  recOutTemp = null;
		JDTORecord  outRec = null;
		JDTORecord  getparamRecord  = JDTORecordFactory.getInstance().create();
		JDTORecord  recPara  = JDTORecordFactory.getInstance().create();
		//Return Value
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
		String methodNm = "[CoilGdsJspSeEjbBean.regCarUdExplainInfo]";
		String szLogMsg = ""; 
		String szYD_STL_NO = "";
		String szLOAD_LOC_CD = "";
		int intRtnVal 	= 0;
		
		String szMsg = "";
		String szMATL_NO 			= "";
		String szMAT_WGT 			= "";
		String szMAT_THK 			= "";
		String szMAT_WTH 			= "";
		String szMAT_LEN 			= "";
		String szMAT_ODIA 			= "";			
		String szMAT_IDIA 			= "";
		String szWORK_STATE 		= "";
		String szYD_CURR_BAY_GP 	= "";
		
		int nTcLen 					= 821;
		try {
			
			szLogMsg = "JSP-FACADE [ " + methodNm +"] 시작";
			ydUtils.putLog(szSessionName, methodNm, szLogMsg, YdConstant.INFO);			
			
			/*
			for(int x=0;x<inDto.length;x++){
				ydUtils.putLog(szSessionName, methodNm, "YD_STL_NO =["+ inDto[x].getFieldString("YD_STL_NO") +"]", YdConstant.INFO);
				ydUtils.putLog(szSessionName, methodNm, "YD_PT_LOAD_LOC =["+ inDto[x].getFieldString("YD_PT_LOAD_LOC") +"]", YdConstant.INFO);
				ydUtils.putLog(szSessionName, methodNm, "YD_CAR_NO =["+ inDto[x].getFieldString("YD_CAR_NO") +"]", YdConstant.INFO);
				ydUtils.putLog(szSessionName, methodNm, "PT_CLS =["+ inDto[x].getFieldString("YD_PT_CLS") +"]", YdConstant.INFO);
				ydUtils.putLog(szSessionName, methodNm, "WORK_CLS =["+ inDto[x].getFieldString("YD_WORK_CLS") +"]", YdConstant.INFO);
				ydUtils.putLog(szSessionName, methodNm, "WORK_COIL_MAX_CNT =["+ inDto.length +"]", YdConstant.INFO);
				ydUtils.putLog(szSessionName, methodNm, "YD_CAR_UPP_LOC_CD =["+ inDto[x].getFieldString("YD_CAR_UPP_LOC_CD") +"]", YdConstant.INFO);
			}
			*/
			
			if(inDto.length>0){
				
				getparamRecord.setField("PT_LOAD_LOC"      ,ydDaoUtils.paraRecChkNull(inDto[0] , "YD_CARPNT_CD"));
				intRtnVal = ydCarSchDao.getYdCarsch(getparamRecord, rsResult, 436);
		        rsResult.first();
				recPara = rsResult.getRecord();
				String szChkCarNum = ydDaoUtils.paraRecChkNull(recPara, "CAR_NO");
				
		        if(intRtnVal < 0 || "".equals(szChkCarNum.trim())){ 
					szMsg= "["+methodNm+"] TB_YD_CARSCH[해당 위치에 차량이 없습니다.]";
					ydUtils.putLog(szSessionName, methodNm, szMsg, YdConstant.ERROR);
					
					jrRtn.setField("RTN_CD" , "1");	
					jrRtn.setField("RTN_MSG", "해당 위치에 차량이 없습니다.");	
					return jrRtn;
				}
				if(!szChkCarNum.equals(ydDaoUtils.paraRecChkNull(inDto[0], "CAR_NO"))){
					szMsg= "["+methodNm+"] TB_YD_CARSCH[해당위치에 차량정보가 틀립니다.]";
					ydUtils.putLog(szSessionName, methodNm, szMsg, YdConstant.ERROR);
					
					jrRtn.setField("RTN_CD" , "1");	
					jrRtn.setField("RTN_MSG", "해당위치에 차량정보가 틀립니다.");	
					return jrRtn;
				}
				
				
				
				
				outRec = JDTORecordFactory.getInstance().create();
				//차량작업 예정정보 전문 data setup

				outRec.setField("MSG_ID", 				new String("YDY5L008BACKUP") );
				outRec.setField("DATE", 				new String(YdUtils.getCurDate("yyyy-MM-dd")) );
				outRec.setField("TIME", 				new String(YdUtils.getCurDate("HH-mm-ss")) );
				outRec.setField("MSG_GP", 				new String("R") );
				outRec.setField("MSG_LEN", 				new String(YdUtils.fillSpZr(""+nTcLen, 4, 0)) );
				outRec.setField("TEMP", 				new String(YdUtils.fillSpZr("", 29, 1)) );
				
				outRec.setField("PT_LOAD_LOC",       YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(inDto[0] , "YD_CARPNT_CD"), 6, 1)); // 상차도 위치
				outRec.setField("CAR_NO", 		 	 YdUtils.fillSpZr_KOR(ydDaoUtils.paraRecChkNull(inDto[0], "CAR_NO"), 15, 1)); // 차량번호
				outRec.setField("PT_CLS", 			 YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(inDto[0], "CAR_WK_GP"), 2, 1));
				
				outRec.setField("WORK_CLS"   ,   	 ydDaoUtils.paraRecChkNull(inDto[0], "RETN_WK_GP")); // 작업구분
				outRec.setField("WORK_COIL_MAX_CNT", YdUtils.fillSpZr(inDto.length+"", 2, 0)); // 작업총 수량
	        	
				intRtnVal = inDto.length;
				intRtnVal = (intRtnVal >= 15) ? 15 : intRtnVal;
				
				// Coil 정보 setting
				for(int i=0; i<15 ; i++){
					
					
					if(i < intRtnVal){
						for(int j=0; j<intRtnVal ; j++){
							szMATL_NO 			= ydDaoUtils.paraRecChkNull(inDto[j], "YD_STL_NO");
							szLOAD_LOC_CD		= ydDaoUtils.paraRecChkNull(inDto[j], "YD_CAR_UPP_LOC_CD");			
							szMAT_WGT 			= ydDaoUtils.paraRecChkNull(inDto[j], "YD_COIL_WT");
							szMAT_THK 			= ydDaoUtils.paraRecChkNull(inDto[j], "YD_COIL_T");
							szMAT_WTH 			= ydDaoUtils.paraRecChkNull(inDto[j], "YD_COIL_W");
							szMAT_LEN 			= ydDaoUtils.paraRecChkNull(inDto[j], "YD_COIL_LEN");
							szMAT_ODIA 			= ydDaoUtils.paraRecChkNull(inDto[j], "YD_COIL_OUTDIA");			
							szMAT_IDIA 			= ydDaoUtils.paraRecChkNull(inDto[j], "YD_COIL_INDIA");
							szWORK_STATE 		= ydDaoUtils.paraRecChkNull(inDto[j], "YD_WORK_STATE");
							szYD_CURR_BAY_GP 	= ydDaoUtils.paraRecChkNull(inDto[j], "YD_CARPNT_CD");
							
							outRec.setField("MATL_NO"+(j+1), 		YdUtils.fillSpZr(szMATL_NO, 11, 1));
							outRec.setField("LOAD_LOC_CD"+(j+1), 		YdUtils.fillSpZr(szLOAD_LOC_CD, 2, 0));
							outRec.setField("MAT_WGT"+(j+1), 		YdUtils.fillSpZr(szMAT_WGT, 5, 0));
							
							// 16-4.Coil 두께 [Coil 두께]
							szMAT_THK = YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(inDto[j], "YD_COIL_T"), 7, 1);
							outRec.setField("MAT_THK"+(j+1)			, ydUtils.FloatLRPAD(szMAT_THK, 6, 3, '0'));	

							// 16-5.Coil 폭 [Coil 폭]
							szMAT_WTH = YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(inDto[j], "YD_COIL_W"), 6, 1);
							outRec.setField("MAT_WTH"+(j+1)			, ydUtils.FloatLRPAD(szMAT_WTH, 5, 1, '0'));	
							
							outRec.setField("MAT_LEN"+(j+1), 		YdUtils.fillSpZr(szMAT_LEN, 7, 0));
							outRec.setField("MAT_ODIA"+(j+1), 		YdUtils.fillSpZr(szMAT_ODIA, 5, 0));
							
							// 16.Coil 내경 [Coil 내경]
							szMAT_IDIA = YdUtils.fillSpZr(ydDaoUtils.paraRecChkNull(inDto[j], "YD_COIL_INDIA"), 6, 1);
							outRec.setField("MAT_IDIA"+(j+1)			, ydUtils.FloatLRPAD(szMAT_IDIA, 5, 1, '0'));
							
							outRec.setField("WORK_STATE"+(j+1), 		YdUtils.fillSpZr(szWORK_STATE, 1, 1));
							outRec.setField("YD_CURR_BAY_GP"+(j+1), 		YdUtils.fillSpZr(szYD_CURR_BAY_GP, 6, 1));
							
						
						i =j +1  ;
						}
						
						outRec.setField("MATL_NO"+(i+1), 		YdUtils.fillSpZr("", 11, 1));
						outRec.setField("LOAD_LOC_CD"+(i+1), 		YdUtils.fillSpZr("", 2, 1));
						outRec.setField("MAT_WGT"+(i+1), 		YdUtils.fillSpZr("", 5, 1));
						outRec.setField("MAT_THK"+(i+1), 		YdUtils.fillSpZr("", 6, 1));
						outRec.setField("MAT_WTH"+(i+1), 		YdUtils.fillSpZr("", 5, 1));
						outRec.setField("MAT_LEN"+(i+1), 		YdUtils.fillSpZr("", 7, 1));
						outRec.setField("MAT_ODIA"+(i+1), 		YdUtils.fillSpZr("", 5, 1));
						outRec.setField("MAT_IDIA"+(i+1), 		YdUtils.fillSpZr("", 5, 1));
						outRec.setField("WORK_STATE"+(i+1), 		YdUtils.fillSpZr("", 1, 1));
						outRec.setField("YD_CURR_BAY_GP"+(i+1), 		YdUtils.fillSpZr("", 6, 1));
					}else {
						outRec.setField("MATL_NO"+(i+1), 		YdUtils.fillSpZr("", 11, 1));
						outRec.setField("LOAD_LOC_CD"+(i+1), 		YdUtils.fillSpZr("", 2, 1));
						outRec.setField("MAT_WGT"+(i+1), 		YdUtils.fillSpZr("", 5, 1));
						outRec.setField("MAT_THK"+(i+1), 		YdUtils.fillSpZr("", 6, 1));
						outRec.setField("MAT_WTH"+(i+1), 		YdUtils.fillSpZr("", 5, 1));
						outRec.setField("MAT_LEN"+(i+1), 		YdUtils.fillSpZr("", 7, 1));
						outRec.setField("MAT_ODIA"+(i+1), 		YdUtils.fillSpZr("", 5, 1));
						outRec.setField("MAT_IDIA"+(i+1), 		YdUtils.fillSpZr("", 5, 1));
						outRec.setField("WORK_STATE"+(i+1), 		YdUtils.fillSpZr("", 1, 1));
						outRec.setField("YD_CURR_BAY_GP"+(i+1), 		YdUtils.fillSpZr("", 6, 1));
					}
					
				}
			}
			
        	ydDelegate.sendMsg(outRec);
        	
        	szMsg = "["+methodNm+"] 코일야드 차량작업 예정정보 전송 완료";
			ydUtils.putLog(szSessionName, methodNm, szMsg, YdConstant.DEBUG);
			
			ydUtils.displayRecord(methodNm, outRec);
			
			
			
			
			//--------------------------------------------------------------------------
			szLogMsg = "JSP-FACADE [ " + methodNm +"] 끝";
			ydUtils.putLog(szSessionName, methodNm, szLogMsg, YdConstant.INFO);			
			
			jrRtn.setField("RTN_CD" , "0");	
			jrRtn.setField("RTN_MSG", "정상적으로 전송하였습니다.");	
			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog("", methodNm, e));
		}	
	} //regCarUdWrk
	
	
	/**
	 *  지포장재반입조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilGdsYdReSendGF(JDTORecord inDto) throws DAOException {
		
		JPlateYdCommDAO ydCommDao = new JPlateYdCommDAO();
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
				
		String szMsg="";		
		String szMethodName="getCoilGdsYdReSendGF";
		String szRetGp = "";
		String sztemp = "";
		String sQueryId = "";
		int intRtnVal = 0;
		
		try {
			
			szRetGp = yddatautil.setDataDefault(inDto.getField("CRN_RET_TP"), "");
			sztemp = yddatautil.setDataDefault(inDto.getField("COIL_NO"), "");
			recPara.setField("COIL_NO"		, sztemp);
			recPara.setField("YD_GP"		, "J");
			recPara.setField("YD_BAY_GP"	, yddatautil.setDataDefault(inDto.getField("YD_DONG_GP"), ""));
			recPara.setField("YD_EQP_GP"	, yddatautil.setDataDefault(inDto.getField("YD_SPAN_GP"), ""));
			recPara.setField("YD_STK_COL_NO"	, yddatautil.setDataDefault(inDto.getField("YD_COL_GP"), ""));
			recPara.setField("EMERGENCY"	, yddatautil.setDataDefault(inDto.getField("EMERGENCY"), ""));			
			recPara.setField("G_WRAP_CMPL_YN"	, yddatautil.setDataDefault(inDto.getField("G_WRAP_CMPL_YN"), ""));
			recPara.setField("CHK_FLAG"		, szRetGp);
			
			recPara.setField("PAGE_CNT1"	, inDto.getField("PAGE_NO"));
			recPara.setField("PAGE_CNT2"	, inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT1"		, inDto.getField("ROWCOUNT"));
			recPara.setField("ROW_CNT2"		, inDto.getField("ROWCOUNT"));
			
			sQueryId = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilGdsYdReSendGF";
		    intRtnVal = ydCommDao.select(recPara, outRecSet, sQueryId);
		    
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
		
		return outRecSet;
	}//end of getCoilGdsYdReSendGF
	
	
	/**
	 *  지포장재삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet updCoilGdsYdReSendGFDel(JDTORecord inDto) throws DAOException {
		
		YdPlateCommDAO	commDao 		= new YdPlateCommDAO();	
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
				
		String szMsg="";		
		String szMethodName="updCoilGdsYdReSendGFDel"; 
		String sQueryId = "";
		String[] rVal = new String[1];
		int intRtnVal = 0;
		
		try { 
			recPara.setField("STL_NO"	, inDto.getField("STL_NO")); 
			recPara.setField("MODIFIER"	, inDto.getField("YD_USER_ID"));
			
			//목표행선
			rVal= YdCommonUtils.getYdAimRtGp("C",recPara );	
 
			recPara.setField("YD_AIM_RT_GP"	, rVal[0]); 
			
			sQueryId = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updCoilGdsYdReSendGFDel";
		    intRtnVal = commDao.update(recPara,  sQueryId);
		    
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
		
		return outRecSet;
	}//end of updCoilGdsYdReSendGFDel
	
	
	/**
	 * 오퍼레이션명 : HOT COIL이용한 결로방지 시스템
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */ 
	public JDTORecord procHotcoilAuto2(JDTORecord inRecord) throws DAOException  {
		// DAO객체 선언
		YdDaoUtils ydDaoUtils 			= new YdDaoUtils();
		
		EJBConnector ejbConn 		= null;		
		//레코드 선언 
		JDTORecord recPara       	=  JDTORecordFactory.getInstance().create();

		JDTORecord outRecord     	= JDTORecordFactory.getInstance().create(); // 
		JDTORecord outRecord2     	= JDTORecordFactory.getInstance().create(); // 
		GridData gdRes 				= null;
		
		JDTORecordSet outRecSet     = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecord[]  inRecordarr   = null;
		
		String szMsg           = "";
		String szMethodName    = "procHotcoilAuto2";
		String szOperationName = "HOT COIL이용한 결로방지 시스템2";

		
		
		int intRtnVal          = 0;

		//목표행선구분
//		//야드적치Bed입출고상태
//		String szYD_STK_BED_WHIO_STAT  = null;
		//스케줄코드
		String szYD_SCH_CD             	= "";		
		String FIRST_YD_SCH_CD			= "";
		
		//공정구분
		String FIRST_YD_WBOOK_ID	   	= "";
		String FIRST_YD_BAY_GP		   	= "";
		String sYD_WBOOK_ID			   	= "";
		String sRTN_CD					= "";
		String sRTN_MSG					= "";

	
		String sSTL_NO 					= "";
		String sBAY_GP 					= ""; 
		String sYD_STK_LYR_NO           = "";		
		String sYD_STK_BED_NO          	= "";
		String sYD_STK_COL_GP		    = "";
		String szYD_CHK_GP 				= "";
		String sYD_USER_ID 				= "";
		
		try{ 
			szYD_CHK_GP 	= ydDaoUtils.paraRecChkNull(inRecord, "YD_CHK_GP"); //1:보급 ,2:추출
			sYD_USER_ID		= ydDaoUtils.paraRecChkNull(inRecord, "YD_USER_ID"); 
 
			
			YdEqpDao   ydEqpDao   = new YdEqpDao();
			JDTORecord 		inRecord9 	= JDTORecordFactory.getInstance().create();
		
			
			
			
			if(szYD_CHK_GP.equals("1")){
				//////////////////////////////////////////////////////////////////////////////////////////////////////////
				//--------결로재 보급 작업 ----------
				//////////////////////////////////////////////////////////////////////////////////////////////////////////
				
				inRecord9.setField("BAY_GP", "E");     
				
				//	결로적치장 기준으로 HOT COIL대상 가져 오기				
				/*com.inisteel.cim.yd.dao.ydstklyrdao.hotcoilautolist*/
				intRtnVal = ydEqpDao.getYdEqp(inRecord9, outRecSet, 403);
				if(intRtnVal <= 0) {
					szMsg = "결로적치 보급 대상이 존재 안 합니다.<br>";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return outRecord;
				}
				szMsg="결로재 보급 대상 건수: " + intRtnVal ;
				ydUtils.putLog(szOperationName, szMethodName, szMsg, YdConstant.DEBUG);		
				
				for (int nLoop =0 ; nLoop<outRecSet.size();nLoop++ ){
					
					recPara =JDTORecordFactory.getInstance().create();
					recPara = outRecSet.getRecord(nLoop);
					
					
					sYD_STK_COL_GP 	=StringHelper.evl(recPara.getFieldString("YD_STK_COL_GP"), "");
					sYD_STK_BED_NO 	=StringHelper.evl(recPara.getFieldString("YD_STK_BED_NO"), "");
					sYD_STK_LYR_NO 	=StringHelper.evl(recPara.getFieldString("YD_STK_LYR_NO"), "");
					sBAY_GP 		=StringHelper.evl(recPara.getFieldString("BAY_GP"), "");
					sSTL_NO 		=StringHelper.evl(recPara.getFieldString("STL_NO"), "");
					

					//=================================================================================
					// 결로방지재 추출요구 스케줄 코드 편성
					String sSPAN_GP = sYD_STK_COL_GP.substring(2, 4);
					
					//B,C동 구분
					if(sBAY_GP.equals("B")|| sBAY_GP.equals("C")){
						if(Integer.parseInt(sSPAN_GP)<=16){
							szYD_SCH_CD = "H" +sBAY_GP +"HC02LM";
						}else{
							szYD_SCH_CD = "H" +sBAY_GP +"HC01LM";
						}
					}else{
						szYD_SCH_CD = "H" +sBAY_GP +"HC01LM";
					}
					//=================================================================================
					
					//재료번호
					//레코드 생성
					inRecord = JDTORecordFactory.getInstance().create();
	
					inRecord.setField("YD_SCH_CD",          		szYD_SCH_CD);//스케줄코드
					inRecord.setField("STL_SH",      				"1");  //LINE_IN 재료매수
					inRecord.setField("STL_NO1", 	  				sSTL_NO);
					inRecord.setField("YD_TO_LOC_DCSN_MTD",			"F");					
					inRecord.setField("YD_AIM_YD_GP",				"J");
					inRecord.setField("YD_AIM_BAY_GP",				sBAY_GP); //작업예약에 목표동 설정처리함
					//inRecord.setField("TO_YD_STK_BED_NO",			sYD_STK_COL_GP.substring(1, 6) +sYD_STK_LYR_NO.substring(2, 3)+ sYD_STK_BED_NO);
					inRecord.setField("TO_YD_STK_BED_NO",			sYD_STK_COL_GP.substring(0, 4) );
					inRecord.setField("YD_USER_ID",					sYD_USER_ID);
					
					// 작업예약 등록 호출
	
					//YD_SCH_CD:스케줄코드,
					//STL_SH: 재료매수,
					//YD_TO_LOC_DCSN_MTD(TO위치 결정 방법)
					//STL_NO(재료번호1,2,3,....)
					//FR_YD_STK_BED_NO(적치배드)
					//TO_YD_STK_BED_NO(가이드가 됨)
					outRecord 	= JDTORecordFactory.getInstance().create();
					ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
					outRecord = (JDTORecord)ejbConn.trx("WrkBookInsertProcTX", new Class[] { JDTORecord.class }, new Object[] { inRecord });
					sRTN_CD			= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
					sYD_WBOOK_ID	= StringHelper.evl(outRecord.getFieldString("YD_WBOOK_ID"), "");
					sRTN_MSG		= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
					if ("0".equals(sRTN_CD)) {
						szMsg = "작업예약 등록시 ERROR";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
						return outRecord;				
					}
					
					
					
					//동별 첫번째 작업에약에 대한 스케줄 기동 작업 
					if(!FIRST_YD_BAY_GP.equals(sBAY_GP)){
						FIRST_YD_WBOOK_ID 	= sYD_WBOOK_ID;
						FIRST_YD_SCH_CD 	= szYD_SCH_CD;
		
						
						//스케줄 기동처리 
						if (!FIRST_YD_WBOOK_ID.equals("")) {
							inRecordarr = new JDTORecord[1];
							
							inRecordarr[0] = JDTORecordFactory.getInstance().create();
							inRecordarr[0].setField("YD_SCH_CD"		, FIRST_YD_SCH_CD); 
							inRecordarr[0].setField("YD_WBOOK_ID"	, FIRST_YD_WBOOK_ID); 
							ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
							outRecord2 = (JDTORecord) ejbConn.trx("trxRunSchedule", new Class[] { JDTORecord[].class },	new Object[] { inRecordarr });
				
							sRTN_CD		= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
							sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
							ydUtils.putLog(szSessionName, szMethodName, "스케줄 정상기동여부 : " + sRTN_MSG, YdConstant.DEBUG);
							if (!("1".equals(sRTN_CD))) {
								ydUtils.putLog(szSessionName, szMethodName, "스케줄 기동처리 오류 YD_WBOOK_ID:"+FIRST_YD_WBOOK_ID, YdConstant.INFO);
							} 	
							
							ydUtils.putLog(szSessionName, szMethodName, "스케줄 기동처리 완료", YdConstant.INFO);
						}
					}
					
					FIRST_YD_BAY_GP		= sBAY_GP;
					}
							
					
				szMsg = "결로재 보급 대상("+intRtnVal+"건)작업  완료!<br>";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
				
				//////////////////////////////////////////////////////////////////////////////////////////////////////////				
			}else if(szYD_CHK_GP.equals("2")){
				//////////////////////////////////////////////////////////////////////////////////////////////////////////
				//--------결로재 추출 작업 ----------
				//////////////////////////////////////////////////////////////////////////////////////////////////////////
				
				inRecord9.setField("BAY_GP", "");    //전체동 가져 오기     
								
				
//				결로적치장 기준으로 추출대상 가져 오기				
				/*com.inisteel.cim.yd.dao.ydstklyrdao.hotcoilautolist2*/
				intRtnVal = ydEqpDao.getYdEqp(inRecord9, outRecSet, 404);
				if(intRtnVal <= 0) {
					szMsg = "결로적치 추출 대상이 존재 안 합니다.<br>";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
					return outRecord;
				}
				szMsg="결로재 추출 대상 건수: " + intRtnVal ;
				ydUtils.putLog(szOperationName, szMethodName, szMsg, YdConstant.DEBUG);		
				
				for (int nLoop =0 ; nLoop<outRecSet.size();nLoop++ ){
					
					recPara =JDTORecordFactory.getInstance().create();
					recPara = outRecSet.getRecord(nLoop);
					
					
					sYD_STK_COL_GP 	=StringHelper.evl(recPara.getFieldString("YD_STK_COL_GP"), "");
					sYD_STK_BED_NO 	=StringHelper.evl(recPara.getFieldString("YD_STK_BED_NO"), "");
					sYD_STK_LYR_NO 	=StringHelper.evl(recPara.getFieldString("YD_STK_LYR_NO"), "");
					sBAY_GP 		=StringHelper.evl(recPara.getFieldString("BAY_GP"), "");
					sSTL_NO 		=StringHelper.evl(recPara.getFieldString("STL_NO"), "");
					
					
					//=================================================================================
					// 결로방지재 추출요구 스케줄 코드 편성
					String sSPAN_GP = sYD_STK_COL_GP.substring(2, 4);
					
					//B,C동 구분
					if(sBAY_GP.equals("B")|| sBAY_GP.equals("C")){
						if(Integer.parseInt(sSPAN_GP)<=16){
							szYD_SCH_CD = "H" +sBAY_GP +"HC02UM";
						}else{
							szYD_SCH_CD = "H" +sBAY_GP +"HC01UM";
						}
					}else{
						szYD_SCH_CD = "H" +sBAY_GP +"HC01UM";
					}
					//=================================================================================
					
					//재료번호
					//레코드 생성
					inRecord = JDTORecordFactory.getInstance().create();
	
					inRecord.setField("YD_SCH_CD",          		szYD_SCH_CD);//스케줄코드
					inRecord.setField("STL_SH",      				"1");  //LINE_IN 재료매수
					inRecord.setField("STL_NO1", 	  				sSTL_NO);
					inRecord.setField("YD_TO_LOC_DCSN_MTD",			"F");					
					inRecord.setField("YD_AIM_YD_GP",				"H");
					inRecord.setField("YD_AIM_BAY_GP",				sBAY_GP); //작업예약에 목표동 설정처리함
					inRecord.setField("TO_YD_STK_BED_NO",			sYD_STK_COL_GP.substring(1, 6) +sYD_STK_LYR_NO.substring(2, 3)+ sYD_STK_BED_NO);
					inRecord.setField("YD_USER_ID",					sYD_USER_ID);
					// 작업예약 등록 호출
	
					//YD_SCH_CD:스케줄코드,
					//STL_SH: 재료매수,
					//YD_TO_LOC_DCSN_MTD(TO위치 결정 방법)
					//STL_NO(재료번호1,2,3,....)
					//FR_YD_STK_BED_NO(적치배드)
					//TO_YD_STK_BED_NO(가이드가 됨)
					outRecord 	= JDTORecordFactory.getInstance().create();
					ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
					outRecord = (JDTORecord)ejbConn.trx("WrkBookInsertProcTX", new Class[] { JDTORecord.class }, new Object[] { inRecord });
					sRTN_CD			= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
					sYD_WBOOK_ID	= StringHelper.evl(outRecord.getFieldString("YD_WBOOK_ID"), "");
					sRTN_MSG		= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
					if ("0".equals(sRTN_CD)) {
						szMsg = "작업예약 등록시 ERROR";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);						
						return outRecord;				
					}
					
					
					
					//동별 첫번째 작업에약에 대한 스케줄 기동 작업 
					if(!FIRST_YD_BAY_GP.equals(sBAY_GP)){
						FIRST_YD_WBOOK_ID 	= sYD_WBOOK_ID;
						FIRST_YD_SCH_CD 	= szYD_SCH_CD;
		
						
						//스케줄 기동처리 
						if (!FIRST_YD_WBOOK_ID.equals("")) {
							inRecordarr = new JDTORecord[1];
							
							inRecordarr[0] = JDTORecordFactory.getInstance().create();
							inRecordarr[0].setField("YD_SCH_CD"		, FIRST_YD_SCH_CD); 
							inRecordarr[0].setField("YD_WBOOK_ID"	, FIRST_YD_WBOOK_ID); 
							ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
							outRecord2 = (JDTORecord) ejbConn.trx("trxRunSchedule", new Class[] { JDTORecord[].class },	new Object[] { inRecordarr });
				
							sRTN_CD		= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
							sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
							ydUtils.putLog(szSessionName, szMethodName, "스케줄 정상기동여부 : " + sRTN_MSG, YdConstant.DEBUG);
							if (!("1".equals(sRTN_CD))) {
								ydUtils.putLog(szSessionName, szMethodName, "스케줄 기동처리 오류 YD_WBOOK_ID:"+FIRST_YD_WBOOK_ID, YdConstant.INFO);
							} 	
							
							ydUtils.putLog(szSessionName, szMethodName, "스케줄 기동처리 완료", YdConstant.INFO);
						}
					}
					
					FIRST_YD_BAY_GP		= sBAY_GP;
					}
							
					
				szMsg = "결로재 추출 대상("+intRtnVal+"건)작업  완료!<br>";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	

					
				//////////////////////////////////////////////////////////////////////////////////////////////////////////
			}
	
			return outRecord;
			
		} catch(Exception e){
			szMsg = "C열연 결로재 보급 대상작업 편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			
			throw new DAOException(szMsg);
		}

	} //end of procHotcoilAuto2
	
	
	
	/**
	 *  전체입동제한 변경 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */	
	public JDTORecord getCoilCarPointYn(GridData gdReq) throws DAOException {
		 
		JDTORecord recEditColumn	= JDTORecordFactory.getInstance().create();
		JDTORecord jrRtn 			= JDTORecordFactory.getInstance().create();
		
		String methodNm = "[CoilGdsJspSeEjbBean.getCoilCarPointYn]";
		String logId = gdReq.getIPAddress();
		String szLogMsg = ""; 
		YdStockDao ydStockDao = new YdStockDao();
		int intRtnVal 	= 0;
		
		try {
			
			szLogMsg = "JSP-FACADE [ " + methodNm +"] 시작";
			ydUtils.putLog(szSessionName, methodNm, szLogMsg, YdConstant.INFO);			
			
			String szPRE_SUP_YN = gdReq.getParam("PRE_SUP_YN").toUpperCase();
			String szCD_GP = gdReq.getParam("CD_GP");
			
			/*if("".equals(szCD_GP) || null == szCD_GP) {
				szCD_GP = "*";
			}*/
			  
			recEditColumn.setField("PRE_SUP_YN",szPRE_SUP_YN);
			recEditColumn.setField("CD_GP" , szCD_GP);
			
    		intRtnVal = ydStockDao.updCoilCarPointYnReg(recEditColumn);
			//--------------------------------------------------------------------------
			szLogMsg = "JSP-FACADE [ " + methodNm +"] 끝";
			ydUtils.putLog(szSessionName, methodNm, szLogMsg, YdConstant.INFO);			
			
			jrRtn.setField("RTN_CD" , "0");	
			jrRtn.setField("RTN_MSG", "정상적으로 등록하였습니다.");	
			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	} //getCoilCarPointYn
	
	
	/**
	 *  제품이송우선순위 변경 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */	
	public JDTORecord getCoilCarMovYn(GridData gdReq) throws DAOException {
		 
		JDTORecord recEditColumn	= JDTORecordFactory.getInstance().create();
		JDTORecord jrRtn 			= JDTORecordFactory.getInstance().create();
		YdPlateCommDAO	commDao 		= new YdPlateCommDAO();
		
		String methodNm = "[CoilGdsJspSeEjbBean.getCoilCarMovYn]";
		String logId = gdReq.getIPAddress();
		String szLogMsg = ""; 
		YdStockDao ydStockDao = new YdStockDao();
		int intRtnVal 	= 0;
		
		try {
			
			szLogMsg = "JSP-FACADE [ " + methodNm +"] 시작";
			ydUtils.putLog(szSessionName, methodNm, szLogMsg, YdConstant.INFO);			
			
			String szMOV_SUP_YN = gdReq.getParam("MOV_SUP_YN").toUpperCase();
			String szYD_GP = gdReq.getParam("YD_GP");
	 
			recEditColumn.setField("MOV_SUP_YN",szMOV_SUP_YN);
			recEditColumn.setField("YD_GP" , szYD_GP);
			
			intRtnVal = commDao.update(recEditColumn, "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updCoilCarMovYnReg");
			if (intRtnVal < 1) {
				
				szLogMsg = "제품이송우선순위UPDATE 실패! ErrorCode:" + intRtnVal;
	 

				jrRtn.setField("RTN_CD"	, "0");
				jrRtn.setField("RTN_MSG", szLogMsg);
				return jrRtn;
			}
 
			
			jrRtn.setField("RTN_CD" , "1");	
			jrRtn.setField("RTN_MSG", "정상적으로 등록하였습니다.");	
			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	} //getCoilCarMovYn
		
	
	/**
	 * 오퍼레이션명 : 2열연 B/C동 HOT COIL이용한 결로방지 시스템
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws DAOException
	 */ 
	public JDTORecord procHotcoilAutoInOut(JDTORecord inRecord) throws DAOException  {
		// DAO객체 선언
		YdDaoUtils ydDaoUtils 			= new YdDaoUtils();
		
		EJBConnector ejbConn 		= null;		
		//레코드 선언 
		JDTORecord recPara       	=  JDTORecordFactory.getInstance().create();

		JDTORecord outRecord     	= JDTORecordFactory.getInstance().create(); // 
		JDTORecord outRecord2     	= JDTORecordFactory.getInstance().create(); // 
		GridData gdRes 				= null;
		
		JDTORecordSet outRecSet     = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecord[]  inRecordarr   = null;
		
		String szMsg           = "";
		String szMethodName    = "procHotcoilAutoInOut";
		String szOperationName = "2열연 B/C동 HOT COIL이용한 결로방지 시스템";

		
		
		int intRtnVal          = 0;

		//목표행선구분
//		//야드적치Bed입출고상태
//		String szYD_STK_BED_WHIO_STAT  = null;
		//스케줄코드
		String szYD_SCH_CD             	= "";		
		String FIRST_YD_SCH_CD			= "";
		
		//공정구분
		String FIRST_YD_WBOOK_ID	   	= "";
		String FIRST_YD_BAY_GP		   	= "";
		String sYD_WBOOK_ID			   	= "";
		String sRTN_CD					= "";
		String sRTN_MSG					= "";

	
		String sSTL_NO 					= "";
		String sBAY_GP 					= ""; 
		String sYD_STK_LYR_NO           = "";		
		String sYD_STK_BED_NO          	= "";
		String sYD_STK_COL_GP		    = "";
		String szYD_CHK_GP 				= "";
		String sYD_USER_ID 				= "";
		String sIN_BAY_GP				= "";
		
		try{ 
			szYD_CHK_GP 	= ydDaoUtils.paraRecChkNull(inRecord, "YD_CHK_GP"); //1:보급 ,2:추출
			sYD_USER_ID		= ydDaoUtils.paraRecChkNull(inRecord, "YD_USER_ID"); 
			sIN_BAY_GP		= ydDaoUtils.paraRecChkNull(inRecord, "YD_BAY_GP"); 
			
			YdEqpDao   ydEqpDao   = new YdEqpDao();
			JDTORecord 		inRecord9 	= JDTORecordFactory.getInstance().create();
		
			
			
			
			if(szYD_CHK_GP.equals("1")){
				//////////////////////////////////////////////////////////////////////////////////////////////////////////
				//--------결로재 보급 작업 ----------
				//////////////////////////////////////////////////////////////////////////////////////////////////////////
				
				if("".equals(sIN_BAY_GP)){
					inRecord9.setField("BAY_GP", "");
				}else {
					inRecord9.setField("BAY_GP", sIN_BAY_GP);
				}
				
				//	결로적치장 기준으로 HOT COIL대상 가져 오기				
				/*com.inisteel.cim.yd.dao.ydstklyrdao.hotcoilautolistIN*/
				intRtnVal = ydEqpDao.getYdEqp(inRecord9, outRecSet, 406);
				if(intRtnVal <= 0) {
					szMsg = "결로적치 보급 대상이 존재 안 합니다.<br>";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return outRecord;
				}
				szMsg="결로재 보급 대상 건수: " + intRtnVal ;
				ydUtils.putLog(szOperationName, szMethodName, szMsg, YdConstant.DEBUG);		
				
				for (int nLoop =0 ; nLoop<outRecSet.size();nLoop++ ){
					
					recPara =JDTORecordFactory.getInstance().create();
					recPara = outRecSet.getRecord(nLoop);
					
					
					sYD_STK_COL_GP 	=StringHelper.evl(recPara.getFieldString("YD_STK_COL_GP"), "");
					sYD_STK_BED_NO 	=StringHelper.evl(recPara.getFieldString("YD_STK_BED_NO"), "");
					sYD_STK_LYR_NO 	=StringHelper.evl(recPara.getFieldString("YD_STK_LYR_NO"), "");
					sBAY_GP 		=StringHelper.evl(recPara.getFieldString("BAY_GP"), "");
					sSTL_NO 		=StringHelper.evl(recPara.getFieldString("STL_NO"), "");
					

					//=================================================================================
					// 결로방지재 보급요구 스케줄 코드 편성
					String sSPAN_GP = sYD_STK_COL_GP.substring(2, 4);
					
					//B,C동 구분
					if(sBAY_GP.equals("B")|| sBAY_GP.equals("C")){
						if(Integer.parseInt(sSPAN_GP)<16){
							szYD_SCH_CD = "J" +sBAY_GP +"HC02LM";
						}else{
							szYD_SCH_CD = "J" +sBAY_GP +"HC01LM";
						}
					}else{
						szYD_SCH_CD = "J" +sBAY_GP +"HC01LM";
					}
					//=================================================================================
					
					//재료번호
					//레코드 생성
					inRecord = JDTORecordFactory.getInstance().create();
	
					inRecord.setField("YD_SCH_CD",          		szYD_SCH_CD);//스케줄코드
					inRecord.setField("STL_SH",      				"1");  //LINE_IN 재료매수
					inRecord.setField("STL_NO1", 	  				sSTL_NO);
					inRecord.setField("YD_TO_LOC_DCSN_MTD",			"F");					
					inRecord.setField("YD_AIM_YD_GP",				"J");
					inRecord.setField("YD_AIM_BAY_GP",				sBAY_GP); //작업예약에 목표동 설정처리함
					//inRecord.setField("TO_YD_STK_BED_NO",			sYD_STK_COL_GP.substring(1, 6) +sYD_STK_LYR_NO.substring(2, 3)+ sYD_STK_BED_NO);
					inRecord.setField("TO_YD_STK_BED_NO",			sYD_STK_COL_GP.substring(0, 4) );
					inRecord.setField("YD_USER_ID",					sYD_USER_ID);
					
					// 작업예약 등록 호출
	
					//YD_SCH_CD:스케줄코드,
					//STL_SH: 재료매수,
					//YD_TO_LOC_DCSN_MTD(TO위치 결정 방법)
					//STL_NO(재료번호1,2,3,....)
					//FR_YD_STK_BED_NO(적치배드)
					//TO_YD_STK_BED_NO(가이드가 됨)
					outRecord 	= JDTORecordFactory.getInstance().create();
					ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
					outRecord = (JDTORecord)ejbConn.trx("WrkBookInsertProcTX", new Class[] { JDTORecord.class }, new Object[] { inRecord });
					sRTN_CD			= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
					sYD_WBOOK_ID	= StringHelper.evl(outRecord.getFieldString("YD_WBOOK_ID"), "");
					sRTN_MSG		= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
					if ("0".equals(sRTN_CD)) {
						szMsg = "작업예약 등록시 ERROR";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
						return outRecord;				
					}
					
					
					
					//동별 첫번째 작업에약에 대한 스케줄 기동 작업 
					if(!FIRST_YD_BAY_GP.equals(sBAY_GP)){
						FIRST_YD_WBOOK_ID 	= sYD_WBOOK_ID;
						FIRST_YD_SCH_CD 	= szYD_SCH_CD;
		
						
						//스케줄 기동처리 
						if (!FIRST_YD_WBOOK_ID.equals("")) {
							inRecordarr = new JDTORecord[1];
							
							inRecordarr[0] = JDTORecordFactory.getInstance().create();
							inRecordarr[0].setField("YD_SCH_CD"		, FIRST_YD_SCH_CD); 
							inRecordarr[0].setField("YD_WBOOK_ID"	, FIRST_YD_WBOOK_ID); 
							ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
							outRecord2 = (JDTORecord) ejbConn.trx("trxRunSchedule", new Class[] { JDTORecord[].class },	new Object[] { inRecordarr });
				
							sRTN_CD		= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
							sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
							ydUtils.putLog(szSessionName, szMethodName, "스케줄 정상기동여부 : " + sRTN_MSG, YdConstant.DEBUG);
							if (!("1".equals(sRTN_CD))) {
								ydUtils.putLog(szSessionName, szMethodName, "스케줄 기동처리 오류 YD_WBOOK_ID:"+FIRST_YD_WBOOK_ID, YdConstant.INFO);
							} 	
							
							ydUtils.putLog(szSessionName, szMethodName, "스케줄 기동처리 완료", YdConstant.INFO);
						}
					}
					
					FIRST_YD_BAY_GP		= sBAY_GP;
					}
							
					
				szMsg = "결로재 보급 대상("+intRtnVal+"건)작업  완료!<br>";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
				
				//////////////////////////////////////////////////////////////////////////////////////////////////////////				
			}else if(szYD_CHK_GP.equals("2")){
				//////////////////////////////////////////////////////////////////////////////////////////////////////////
				//--------결로재 추출 작업 ----------
				//////////////////////////////////////////////////////////////////////////////////////////////////////////
				
				inRecord9.setField("BAY_GP", "");    //전체동 가져 오기     
								
				
//				결로적치장 기준으로 추출대상 가져 오기				
				/*com.inisteel.cim.yd.dao.ydstklyrdao.hotcoilautolistOUT*/
				intRtnVal = ydEqpDao.getYdEqp(inRecord9, outRecSet, 407);
				if(intRtnVal <= 0) {
					szMsg = "결로적치 추출 대상이 존재 안 합니다.<br>";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);					
					return outRecord;
				}
				szMsg="결로재 추출 대상 건수: " + intRtnVal ;
				ydUtils.putLog(szOperationName, szMethodName, szMsg, YdConstant.DEBUG);		
				
				for (int nLoop =0 ; nLoop<outRecSet.size();nLoop++ ){
					
					recPara =JDTORecordFactory.getInstance().create();
					recPara = outRecSet.getRecord(nLoop);
					
					
					sYD_STK_COL_GP 	=StringHelper.evl(recPara.getFieldString("YD_STK_COL_GP"), "");
					sYD_STK_BED_NO 	=StringHelper.evl(recPara.getFieldString("YD_STK_BED_NO"), "");
					sYD_STK_LYR_NO 	=StringHelper.evl(recPara.getFieldString("YD_STK_LYR_NO"), "");
					sBAY_GP 		=StringHelper.evl(recPara.getFieldString("BAY_GP"), "");
					sSTL_NO 		=StringHelper.evl(recPara.getFieldString("STL_NO"), "");
					
					
					//=================================================================================
					// 결로방지재 추출요구 스케줄 코드 편성
					String sSPAN_GP = sYD_STK_COL_GP.substring(2, 4);
					
					//B,C동 구분
					if(sBAY_GP.equals("B")|| sBAY_GP.equals("C")){
						if(Integer.parseInt(sSPAN_GP)<20){
							szYD_SCH_CD = "J" +sBAY_GP +"HC02UM";
						}else{
							szYD_SCH_CD = "J" +sBAY_GP +"HC01UM";
						}
					}else{
						szYD_SCH_CD = "J" +sBAY_GP +"HC01UM";
					}
					//=================================================================================
					
					//재료번호
					//레코드 생성
					inRecord = JDTORecordFactory.getInstance().create();
	
					inRecord.setField("YD_SCH_CD",          		szYD_SCH_CD);//스케줄코드
					inRecord.setField("STL_SH",      				"1");  //LINE_IN 재료매수
					inRecord.setField("STL_NO1", 	  				sSTL_NO);
					inRecord.setField("YD_TO_LOC_DCSN_MTD",			"F");					
					inRecord.setField("YD_AIM_YD_GP",				"H");
					inRecord.setField("YD_AIM_BAY_GP",				sBAY_GP); //작업예약에 목표동 설정처리함
					//inRecord.setField("TO_YD_STK_BED_NO",			sYD_STK_COL_GP.substring(1, 6) +sYD_STK_LYR_NO.substring(2, 3)+ sYD_STK_BED_NO);
					inRecord.setField("TO_YD_STK_BED_NO",			sYD_STK_COL_GP.substring(0, 4) );
					inRecord.setField("YD_USER_ID",					sYD_USER_ID);
					// 작업예약 등록 호출
	
					//YD_SCH_CD:스케줄코드,
					//STL_SH: 재료매수,
					//YD_TO_LOC_DCSN_MTD(TO위치 결정 방법)
					//STL_NO(재료번호1,2,3,....)
					//FR_YD_STK_BED_NO(적치배드)
					//TO_YD_STK_BED_NO(가이드가 됨)
					outRecord 	= JDTORecordFactory.getInstance().create();
					ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
					outRecord = (JDTORecord)ejbConn.trx("WrkBookInsertProcTX", new Class[] { JDTORecord.class }, new Object[] { inRecord });
					sRTN_CD			= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
					sYD_WBOOK_ID	= StringHelper.evl(outRecord.getFieldString("YD_WBOOK_ID"), "");
					sRTN_MSG		= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
					if ("0".equals(sRTN_CD)) {
						szMsg = "작업예약 등록시 ERROR";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);						
						return outRecord;				
					}
					
					
					
					//동별 첫번째 작업에약에 대한 스케줄 기동 작업 
					if(!FIRST_YD_BAY_GP.equals(sBAY_GP)){
						FIRST_YD_WBOOK_ID 	= sYD_WBOOK_ID;
						FIRST_YD_SCH_CD 	= szYD_SCH_CD;
		
						
						//스케줄 기동처리 
						if (!FIRST_YD_WBOOK_ID.equals("")) {
							inRecordarr = new JDTORecord[1];
							
							inRecordarr[0] = JDTORecordFactory.getInstance().create();
							inRecordarr[0].setField("YD_SCH_CD"		, FIRST_YD_SCH_CD); 
							inRecordarr[0].setField("YD_WBOOK_ID"	, FIRST_YD_WBOOK_ID); 
							ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
							outRecord2 = (JDTORecord) ejbConn.trx("trxRunSchedule", new Class[] { JDTORecord[].class },	new Object[] { inRecordarr });
				
							sRTN_CD		= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
							sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
							ydUtils.putLog(szSessionName, szMethodName, "스케줄 정상기동여부 : " + sRTN_MSG, YdConstant.DEBUG);
							if (!("1".equals(sRTN_CD))) {
								ydUtils.putLog(szSessionName, szMethodName, "스케줄 기동처리 오류 YD_WBOOK_ID:"+FIRST_YD_WBOOK_ID, YdConstant.INFO);
							} 	
							
							ydUtils.putLog(szSessionName, szMethodName, "스케줄 기동처리 완료", YdConstant.INFO);
						}
					}
					
					FIRST_YD_BAY_GP		= sBAY_GP;
					}
							
					
				szMsg = "결로재 추출 대상("+intRtnVal+"건)작업  완료!<br>";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	

					
				//////////////////////////////////////////////////////////////////////////////////////////////////////////
			}
	
			return outRecord;
			
		} catch(Exception e){
			szMsg = "2열연 결로재 보급 대상작업 편성 Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			
			throw new DAOException(szMsg);
		}

	} //end of procHotcoilAutoInOut
	
	
	/**
	 * 압연일자별 재공현황(PO)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet YdNextprocPOList(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		
		String szMsg        	= "";
		String szMethodName 	= "YdNextprocPOList";	
		String szOperationName 	= "압연일자별 재공현황(PO)";
		String sDD_CHK 			= "";
		
		YdEqpDao ydEqpDao = new YdEqpDao();
		
		int intRtnVal = 0;
		
		try {		
				    
			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
 
			
			recPara         = JDTORecordFactory.getInstance().create();
		    recPara.setField("YD_GP",      ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));
		    /*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getCoilTotYdNextproctPOList*/
		    intRtnVal = ydEqpDao.getYdEqp(recPara, outRecSet, 807);
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
	}//end of getYdMgtPOList
	
	
	/**
	 * 야드관리 > 코일제품창고 > 설비관리 > 대차스케줄관리  --> 입고대차 설정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord[]
	 * @return String[]
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.10
	 */
	public JDTORecord updCoilYdTcarStsSetCond(JDTORecord[] inDto) throws DAOException {
		int intRtnVal = 0;
		String szLogMsg = null; 
	
		String szMethodName="updCoilYdTcarStsSetCond";
		String szYD_EQP_ID = null;
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		 
		JDTORecord outRecord = JDTORecordFactory.getInstance().create(); // 
		 	
		YdEqpDao ydEqpDao = new YdEqpDao(); 
	
		String szOperationName = "결로엄격재 대차 설정";
		String szMsg = "";
		
		String szRCPT_TCAR_USE_YN     = ""; 
		String szUser 			= "";
		
		try {
			
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			//저장위치 
			for(int x=0;x<inDto.length;x++){
				szYD_EQP_ID = yddatautil.setDataDefault(inDto[x].getField("YD_EQP_ID"), "");
				szUser 		= ydDaoUtils.paraRecChkNull(inDto[x], "YD_USER_ID");
				
				if(szYD_EQP_ID.equals("")){
					szLogMsg = "설비ID값이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);	
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szLogMsg);	
					return outRecord;
				}
		
				szRCPT_TCAR_USE_YN      = yddatautil.setDataDefault(inDto[x].getField("COND_TCAR_USE_YN"), "");
				if(szRCPT_TCAR_USE_YN.equals("")){
					szLogMsg = "결로엄격재 지정여부 값이 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);					
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szLogMsg);	
					return outRecord;
				}
  
				
				recPara.setField("YD_EQP_ID"			, szYD_EQP_ID);
				recPara.setField("RCPT_TCAR_USE_YN" 	, szRCPT_TCAR_USE_YN);	 	
				recPara.setField("MODIFIER" 			, szUser);	
				
				/*com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.updYdEqpTCarCond*/
				ydUtils.putLog(szSessionName, szMethodName, "szUser" + szUser, YdConstant.DEBUG);
				intRtnVal = ydEqpDao.updYdEqpDirect(recPara, 400);
				
				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szLogMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					} else {
						szLogMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					}
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, "장비 등록시 ERROR 발생하였습니다..");	
					return outRecord;
				} // end of if	
				
			}//end for

			outRecord.setField("RTN_CD" 	, "1");	
			return outRecord;
			
		} catch (Exception e) {
			e.printStackTrace();
			
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
		}
		
		

	}	// end of updCoilYdTcarStsSetCond
	
	
	/**
	 * 야드관리 > 2열연 코일소재야드 > 설비입측관리 > 지포장보급관리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord[]
	 * @return String[]
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.10
	 */
	public JDTORecordSet getCoilYdGFList(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		
		String szMsg        	= "";
		String szMethodName 	= "getCoilYdGFList";	
		String szOperationName 	= "지포장보급대상LIST";
		String sDD_CHK 			= "";
		
		YdEqpDao ydEqpDao = new YdEqpDao();
		
		int intRtnVal = 0;
		
		try {		
				    
			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
 
			
			recPara         = JDTORecordFactory.getInstance().create();
		    recPara.setField("PAGE_NO",     "1");
		    recPara.setField("PAGE_SIZE",     "1000");
		    /*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdGFList*/
		    intRtnVal = ydEqpDao.getYdEqp(recPara, outRecSet, 808);
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
	}//end of getCoilYdGFList
}