package com.inisteel.cim.yd.jsp.coiljsp.session;


//UTIL IMPORT
import java.util.Iterator;
import java.util.List;

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

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.jms.JmsQueueSender;
import com.inisteel.cim.common.util.CmUtil;
import com.inisteel.cim.yd.common.dao.ydCarFtmvMtlDao.YdCarFtmvMtlDao;
import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
import com.inisteel.cim.yd.common.dao.ydCrnSpecDao.YdCrnSpecDao;
import com.inisteel.cim.yd.common.dao.ydCrnWrkMtlDao.YdCrnWrkMtlDao;
import com.inisteel.cim.yd.common.dao.ydCrnWrkMtlHistDao.YdCrnWrkMtlHistDao;
import com.inisteel.cim.yd.common.dao.ydEqpDao.YdEqpDao;
import com.inisteel.cim.yd.common.dao.ydEqpPauseDao.YdEqpPauseDao;
import com.inisteel.cim.yd.common.dao.ydPrepMtlDao.YdPrepMtlDao;
import com.inisteel.cim.yd.common.dao.ydPrepSchDao.YdPrepSchDao;
import com.inisteel.cim.yd.common.dao.ydSchRuleDao.YdSchRuleDao;
import com.inisteel.cim.yd.common.dao.ydStkBedDao.YdStkBedDao;
import com.inisteel.cim.yd.common.dao.ydStkColDao.YdStkColDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.dao.ydTcarFtmvMtlDao.YdTcarFtmvMtlDao;
import com.inisteel.cim.yd.common.dao.ydWrkHistDao.YdWrkHistDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookDao.YdWrkbookDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookMtlDao.YdWrkbookMtlDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.DaoManager;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant; 
import com.inisteel.cim.yd.common.util.YdDBAssist;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.loc.CoilYdToLocDcsnUtil;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO;
import com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDAO;
import com.inisteel.cim.yd.jsp.common.YDComScript;
import com.inisteel.cim.yd.jsp.common.YDComUtil;
import com.inisteel.cim.yd.jsp.common.YDDataUtil;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;






/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 * 클래스명 : 소재야드 Session Class
 *
 * @ejb.bean name="CoilJspSeEJB" jndi-name="CoilJspSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required" 
 */

public class CoilJspSeEJBBean extends BaseSessionBean {
	
	private YdUtils ydUtils = new YdUtils();
	private Logger  logger = new Logger("yd");
	private String  szSessionName = getClass().getName();
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();
	
	YDDataUtil  yddatautil = new YDDataUtil();
	YDComScript ydScript   = null;
	YdDelegate ydDelegate = new YdDelegate();

	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}
	
	
	/**
	 *  코일 야드 스케줄 기동 (조회)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdSchStirMgt(JDTORecord inDto) throws DAOException {
		
		//Log Message 용 
		String szMsg         = "";		
		String szEdit        = null;
		String szMethodName  = "getCoilYdSchStirMgt";	
		
		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecord    recEdit   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecordSet retRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
				
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
				szEdit  = recEdit.getFieldString("YD_SCH_CD").trim().substring(4, 6);
				recEdit.setField("YD_EQP_GP", szEdit);
				
				retRecSet.addRecord(recEdit);
				
			}while(outRecSet.next());
			
			
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return retRecSet;
	}//end of getCoilYdSchStirMgt

	
	/**
	 *  설비사양설정 (조회)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdEqpSetSpec(JDTORecord inDto) throws DAOException {
		
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		YdCrnSpecDao ydCrnspecDao = new YdCrnSpecDao();
		
		String szMsg="";
		String szMethodName="getCoilYdEqpSetSpec";
		
			
		int intRtnVal = 0;
		
		try {
			
			//야드 구분, 설비 ID, 페이지 설정 
			recPara.setField("YD_GP",     yddatautil.setDataDefault(inDto.getField("YD_GP"), ""));
			recPara.setField("YD_EQP_ID", yddatautil.setDataDefault(inDto.getField("YD_EQP_ID"), "%"));			
			recPara.setField("PAGE_CNT1", inDto.getField("PAGE_NO"));
			recPara.setField("PAGE_CNT2", inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT1",  inDto.getField("ROWCOUNT"));
			recPara.setField("ROW_CNT2",  inDto.getField("ROWCOUNT"));

			// 야드구분과 설비번호를 전달인자로 넘겨준다.
			intRtnVal = ydCrnspecDao.getYdCrnspec(recPara, outRecSet, 1);

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
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

		return outRecSet;
	}//end of getCoilYdEqpSetSpec
	
	
	/**
	 * 설비사양설정 (수정)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updCoilYdEqpSetSpec(JDTORecord[] inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg = "";
		String szMethodName = "updCoilYdEqpSetSpec";
		String szOperationName = "설비사양설정";
		
		JDTORecord   recPara      = JDTORecordFactory.getInstance().create();		
		YdCrnSpecDao ydCrnspecDao = new YdCrnSpecDao();
		YdEqpDao 	 ydEqpDao     = new YdEqpDao();
		
		
		try {
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
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}	// end of updCoilYdEqpSetSpec
	
	
	/**
	 * 설비사양설정 (등록)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void insCoilYdEqpSetSpec(JDTORecord[] inDto) throws DAOException {
		
		int intRtnVal = 0;
		
		String szMsg=  "";
		String szMethodName = "insCoilYdEqpSetSpec";

		String szydEqpId = null;
		JDTORecord   recPara   = JDTORecordFactory.getInstance().create();		
		YdCrnSpecDao ydCrnspecDao = new YdCrnSpecDao();
		YdEqpDao 	 ydEqpDao     = new YdEqpDao();
		

		try {
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
				recPara.setField("YD_GP", szydEqpId.substring(0, 1));
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
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
				
				//전달 인자 LOG출력 (DEBUG 용) 
				szMsg = recPara.toString();
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
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
				
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				
			}			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}	// end of insCoilYdEqpSetSpec	
	
	
	/**
	 * 설비사양설정 (삭제)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void delCoilYdEqpSetSpec(JDTORecord[] inDto) throws DAOException {
	
		int intRtnVal = 0;
		String szMsg= "";
		String szMethodName = "delCoilYdEqpSetSpec";		
		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();		
		
		//DAO 셍성 
		YdEqpDao 	 ydEqpDao     = new YdEqpDao();
		YdCrnSpecDao ydCrnspecDao = new YdCrnSpecDao();					
		
		try {
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
				
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				
			}			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}	// end of delCoilYdEqpSetSpec
	
	/**
	 * 코일 야드 스케줄 기동관리 (수정)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updCoilYdSchStirMgt(JDTORecord[] inDto) throws DAOException {
		int    intRtnVal    = 0;
		String szMsg        = null;
		String szMethodName = null;
		String tempLog      = null;
		
		JDTORecord    recPara      = JDTORecordFactory.getInstance().create();		
		YdSchRuleDao  ydSchRuleDao = new YdSchRuleDao();
		
		szMsg        = "";
		szMethodName = "updCoilYdSchStirMgt";
			
		
		try {
			//저장위치 좌표설정화면 BED 수정
			for(int x=0;x<inDto.length;x++){
				tempLog = inDto[x].toString();
								
				//스케줄 코드
				recPara.setField("YD_SCH_CD"		, yddatautil.setDataDefault(inDto[x].getField("YD_SCH_CD"), ""));
				//금지/해제 
				recPara.setField("YD_SCH_PROH_EXN"	, yddatautil.setDataDefault(inDto[x].getField("YD_SCH_PROH_EXN"), "N"));
				
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
				
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				
			}			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}	// end of updCoilYdSchStirMgt

	
	/**
	 *  코일 야드 차량진행관리 조회 
	 *
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
			
			recPara.setField("YD_GP1"	, yddatautil.setDataDefault(inDto.getField("YD_GP"), ""));
			recPara.setField("YD_GP2"	, yddatautil.setDataDefault(inDto.getField("YD_GP"), ""));
			recPara.setField("YD_EQP_GP", yddatautil.setDataDefault(inDto.getField("CAR_GP"), ""));
			recPara.setField("PAGE_CNT1", inDto.getField("PAGE_NO"));
			recPara.setField("PAGE_CNT2", inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT1"	, inDto.getField("ROWCOUNT"));
			recPara.setField("ROW_CNT2"	, inDto.getField("ROWCOUNT"));
		
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
				
				carUseGp 		= yddatautil.setDataDefault(revRec.getField("YD_CAR_USE_GP"), "");
				out_plant 		= yddatautil.setDataDefault(revRec.getField("YD_CARLD_STOP_LOC"), "");
				szCarProgStat 	= yddatautil.setDataDefault(revRec.getField("YD_CAR_PROG_STAT"), "");
				
				revRec.setField("OUT_PLANT",out_plant);
				
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
				if (chkWorkStat.equals("L")) {
						revRec.setField("T_STOP_LOC",	revRec.getField("YD_CARLD_STOP_LOC"));
						revRec.setField("T_LEV_DT",  	revRec.getField("YD_CARLD_LEV_DT"));
						revRec.setField("T_ARR_DT",  	revRec.getField("YD_CARLD_ARR_DT"));
						revRec.setField("T_ST_DT",   	revRec.getField("YD_CARLD_ST_DT"));
						revRec.setField("T_CMPL_DT", 	revRec.getField("YD_CARLD_CMPL_DT"));
					
				} //하차 정보 세팅상차 정보 SETTING(야드 설비작업상태 D)
				else if(chkWorkStat.equals("U")) {
						revRec.setField("T_STOP_LOC", 	revRec.getField("YD_CARUD_STOP_LOC"));
						revRec.setField("T_LEV_DT",   	revRec.getField("YD_CARUD_LEV_DT"));
						revRec.setField("T_ARR_DT", 	revRec.getField("YD_CARUD_ARR_DT"));
						revRec.setField("T_ST_DT", 		revRec.getField("YD_CARUD_ST_DT"));
						revRec.setField("T_CMPL_DT",	revRec.getField("YD_CARUD_CMPL_DT"));
				}
			
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

		int intRtnVal 		= 0;
		String szMsg		= "";
		String szMethodName	= "getCoilYdStkPosSet";		
		JDTORecord recPara 	= JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		try {
			
			recPara.setField("STL_NO", 	inDto.getField("STL_NO"));
			
			YdStockDao ydStockDao = new YdStockDao();
			
			intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 1);
			
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
		
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}
		

	/**
	 * 코일 공통  항목을 조회한다.(검색어, 업무영역코드)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getPtCoilComm(JDTORecord inDto) throws DAOException {
		int intRtnVal 		= 0;
		String szMsg		= "";
		String szMethodName	= "getYdStock";		
		JDTORecord recPara  = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		try {
			
			recPara.setField("STL_NO", 	inDto.getField("STL_NO"));
			
			YdStockDao ydStockDao = new YdStockDao();
			
			intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 7);
			
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
						
		
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);			
		
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}
	

	/**
	 * 코일 공통  항목을 조회한다.(검색어, 업무영역코드)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getPtCoilComm_backup(JDTORecord inDto) throws DAOException {
			
		int intRtnVal = 0;
		String szMsg="";
		String szMethodName="getPtCoilComm_backup";		
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		try {
			
			recPara.setField("STL_NO", 	inDto.getField("STL_NO"));
			
			YdStockDao ydStockDao = new YdStockDao();
			
			intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 1);
			
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
						
		
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);			
		
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
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
				
				intRtnVal = ydStkcolDao.getYdStkcol(recPara,outRecSet,1);
			
			} else{				
				recPara.setField("YD_GP", 		inDto.getField("YD_GP"));
				recPara.setField("YD_BAY_GP", 	inDto.getField("YD_BAY_GP"));
				recPara.setField("YD_EQP_GP", 	inDto.getField("YD_EQP_GP"));
				recPara.setField("YD_STK_COL_NO", inDto.getField("YD_STK_COL_NO"));
				
				intRtnVal = ydStkcolDao.getYdStkcol(recPara,outRecSet,2);	
				
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
						
		
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		return outRecSet;
	}	// end of getCoilYdStkPosSet
	

	
	
	/**
	 *저장위치 좌표설정화면 열 수정 
	 * 심명순(090713)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String  updCoilYdStkPosSet(JDTORecord [] inDto) throws DAOException {
		
		
		int intRtnVal = 0;
		String szMsg="";
		String szMethodName="updCoilYdStkPosSet";
		String szRtnMsg = YdConstant.RETN_CD_SUCCESS;
		
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		YdStkColDao ydStkcolDao = new YdStkColDao();
		 
		try {
			//메뉴 페이지 목록을 수정한다.
			for(int x=0;x<inDto.length;x++){
			
				
				//수정할 항목 SETTING
				
				recPara = inDto[x];
				
				recPara.setField("MODIFIER", recPara.getField("YD_USER_ID"));
				
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
				
				ydUtils.putLog(szSessionName, szMethodName, szMsg,  YdConstant.DEBUG);
				
				if(intRtnVal > 0){
					
					//------------------------------------------------------------------------------
					//L2저장위치제원정보 전송 YDY5L001
					//------------------------------------------------------------------------------
					String szYD_STK_COL_GP=ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");
					
					JDTORecord msgRecord = JDTORecordFactory.getInstance().create();
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
	}	

	
	/**
	 *저장위치 좌표설정화면 열 등록
	 *
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
				
				recPara.setField("YD_STK_COL_NO"		, inDto[x].getField("YD_STK_COL_NO")); //적치열번호 필수 
				recPara.setField("YD_GP"				, inDto[x].getField("YD_GP")); 	//야드구분
				recPara.setField("YD_BAY_GP"			, inDto[x].getField("YD_BAY_GP")); //야드동구분
				recPara.setField("YD_EQP_GP"			, inDto[x].getField("YD_EQP_GP")); //야드설비구분
				recPara.setField("YD_STK_COL_ACT_STAT"	, inDto[x].getField("YD_STK_COL_ACT_STAT")); //활성상태 
				recPara.setField("YD_STK_COL_RULE_XAXIS", inDto[x].getField("YD_STK_COL_RULE_XAXIS")); //기준 X축
				recPara.setField("YD_STK_COL_RULE_YAXIS", inDto[x].getField("YD_STK_COL_RULE_YAXIS")); //기준Y축
				recPara.setField("YD_STK_COL_W"			, inDto[x].getField("YD_STK_COL_W"));  //폭 
				recPara.setField("YD_STK_COL_L"			, inDto[x].getField("YD_STK_COL_L"));  //길이
				
				//적치열 구분 * 필수 
				szYdStkColGp = inDto[x].getFieldString("YD_GP")
							+  inDto[x].getFieldString("YD_BAY_GP")
							+  inDto[x].getFieldString("YD_EQP_GP")				
							+  inDto[x].getFieldString("YD_STK_COL_NO");
				recPara.setField("YD_STK_COL_GP"		, szYdStkColGp.trim());
				
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
				
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				
			}

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally{
		}		
	}// end of updCoilYdStkPosSet
	

	
	/**
	 *저장위치 좌표설정화면 열 삭제 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void delCoilYdStkPosSet(JDTORecord [] inDto) throws DAOException {
		
		
		int intRtnVal 		= 0;
		String szMsg		= "";
		String szMethodName	= "delCoilYdStkPosSet";
		
		JDTORecord recPara 		= JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		
		YdStkColDao ydStkcolDao = new YdStkColDao();
		 
		try {
			//메뉴 페이지 목록을 수정한다.
			for(int x=0;x<inDto.length;x++){
				//삭제 KEY SETTING
				recPara.setField("YD_STK_COL_NO", 	inDto[x].getField("YD_STK_COL_NO"));  //적치열번호
				recPara.setField("YD_GP", 			inDto[x].getField("YD_GP")); //야드 구분
				recPara.setField("YD_BAY_GP", 		inDto[x].getField("YD_BAY_GP"));//동구분
				recPara.setField("YD_EQP_GP", 		inDto[x].getField("YD_EQP_GP")); //설비구분 

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
					
					intRtnVal = ydStkcolDao.updYdStkcol(recPara,0);
					
				}
				
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				
			}

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally{
		}		
	}	// end of delCoilYdStkPosSet	
	
	
	
	
	/**
	 *저장위치 좌표설정화면 열 등록
	 *
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
				recPara.setField("YD_STR_GTR_CD"		, "TESTYD");
				
				recPara.setField("YD_STK_BED_TP"		, inDto[x].getField("YD_STK_BED_TP"));
				recPara.setField("YD_STK_BED_L_GP"		, inDto[x].getField("YD_STK_BED_L_GP"));
				recPara.setField("YD_STK_BED_W_GP"		, inDto[x].getField("YD_STK_BED_W_GP"));
				recPara.setField("YD_STK_BED_DIR_GP"	, inDto[x].getField("YD_STK_BED_DIR_GP"));
				recPara.setField("YD_STK_BED_ACT_STAT"	, inDto[x].getField("YD_STK_BED_ACT_STAT"));
				recPara.setField("YD_STK_BED_WHIO_STAT"	, inDto[x].getField("YD_STK_BED_WHIO_STAT"));
				recPara.setField("YD_STK_BED_XAXIS"		, inDto[x].getField("YD_STK_BED_XAXIS"));
				recPara.setField("YD_STK_BED_YAXIS"		, inDto[x].getField("YD_STK_BED_YAXIS"));
				recPara.setField("YD_STK_BED_ZAXIS"		, inDto[x].getField("YD_STK_BED_ZAXIS"));
				recPara.setField("YD_STK_BED_LYR_MAX"	, inDto[x].getField("YD_STK_BED_LYR_MAX"));
				recPara.setField("YD_STK_BED_WT_MAX"	, inDto[x].getField("YD_STK_BED_WT_MAX"));
				recPara.setField("YD_STK_BED_H_MAX"		, inDto[x].getField("YD_STK_BED_H_MAX"));
				recPara.setField("YD_STK_BED_L_MAX"		, inDto[x].getField("YD_STK_BED_L_MAX"));
				recPara.setField("YD_STK_BED_W_MAX"		, inDto[x].getField("YD_STK_BED_W_MAX"));
				
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
				
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				
			}

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally{
		}		
	}// end of insCoilYdStkPosSetBed
	
	
	

	/**
	 *저장위치 좌표설정화면 BED 수정 
	 * SJH
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 */
	public JDTORecord updCoilYdStkPosSetBed(JDTORecord [] inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg="";
		String sERR_FLAG="N";
		String szMethodName="updCoilYdStkPosSetBed";
		JDTORecord outRecord = JDTORecordFactory.getInstance().create(); // 
		
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
	
		YdStkLyrDao YdStkLyrDao = new YdStkLyrDao();
		try {
			//저장위치 좌표설정화면 BED 수정 
			for(int x=0;x<inDto.length;x++){
				//수정할 항목 SETTING
				
				//적치열구분 필수 
				recPara = JDTORecordFactory.getInstance().create();
				recPara = inDto[x];
				
//				intRtnVal = ydStkbedDao.updYdStkbed(recPara,0);
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
				}
	
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
				
				szMsg ="@@@@@"+x%2;
				ydUtils.putLog(szSessionName, szMethodName,szMsg ,  YdConstant.DEBUG);	
				if((x%2 == 0)){
					JDTORecord msgRecord = JDTORecordFactory.getInstance().create();
					msgRecord.setField("YD_INFO_SYNC_CD", "4");				//1:동,2:SPAN,3:열,4:BED
					msgRecord.setField("YD_GP", 			szYD_STK_COL_GP.substring(0, 1));
					msgRecord.setField("YD_STK_COL_GP", 	szYD_STK_COL_GP);
					msgRecord.setField("YD_STK_BED_NO", 	ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO"));
					
					YdCommonUtils.sndStrPosSpecToL2(msgRecord);
				}
			}
			
			outRecord.setField("RTN_CD" 	, "1");	
			return outRecord;

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
			
		} 		
		
	}	// end of updCoilYdStkPosSetBed
	
	/**
	 * 크레인작업실적LIST (야드관리 > 코일소재야드 > 크레인실적관리 > 크레인작업실적LIST조회)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilCrnWrkWrList(JDTORecord inDto) throws DAOException {
		int         intRtnVal    = 0;
		String      szMsg        = "";
		String      szMethodName = "getCoilCrnWrkWrList";
		YdCrnSchDao ydCrnschDao  = new YdCrnSchDao();
		
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecord recPara 		= JDTORecordFactory.getInstance().create();
			
		try {
			ydUtils.putLog(szSessionName, szMethodName, "JSP-SESSION [크레인작업실적LIST] 시작", YdConstant.INFO);
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
			/*밑에 두 field 는 조회조건에 없지만 기존소스가 이렇게 되어있었음.그래서 추가해줌*/
			recPara.setField("YD_AIM_RT_GP"	, yddatautil.setDataDefault(inDto.getField("YD_AIM_RT_GP"),""));
			recPara.setField("STL_PROG_CD"	, yddatautil.setDataDefault(inDto.getField("STL_PROG_CD"),""));
			recPara.setField("PARTY"		, yddatautil.setDataDefault(inDto.getField("YD_WRK_PARTY"),""));
			
			ydUtils.putLog(szSessionName,"form 에 있는 모든 변수값 : ", message, YdConstant.INFO);
		
			/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getCoilCrnWrkWrList_PIDEV*/
			intRtnVal = ydCrnschDao.getYdCrnsch(recPara, outRecSet, 302);
							
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
						
			
		
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		ydUtils.putLog(szSessionName, szMethodName, "JSP-SESSION [크레인작업실적LIST] 끝", YdConstant.INFO);
		return outRecSet;
	}	// end of getCoilCrnWrkWrList
	
	/**
	 * 설비 정비이력 조회 (야드관리 > 코일소재야드 > 크레인실적관리 > 설비 정비이력 관리)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getEqpMaintHist(JDTORecord inDto) throws DAOException {
		int         intRtnVal    = 0;
		String      szMsg        = "";
		String      szMethodName = "getEqpMaintHist";
		YdEqpPauseDao  ydEqpPauseDao  = new YdEqpPauseDao();
		
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecord recPara 		= JDTORecordFactory.getInstance().create();
			
		try {
			ydUtils.putLog(szSessionName, szMethodName, "JSP-SESSION [설비 정비이력 조회] 시작", YdConstant.INFO);
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
			
			ydUtils.putLog(szSessionName,"form 에 있는 모든 변수값 : ", message, YdConstant.INFO);
		
			intRtnVal = ydEqpPauseDao.getYdEqppause(recPara, outRecSet, 300);
							
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
						
			
		
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		ydUtils.putLog(szSessionName, szMethodName, "JSP-SESSION [설비 정비이력 조회] 끝", YdConstant.INFO);
		
		return outRecSet;
	}	// end of getCoilCrnWrkWrList
	
	/**
	 * 크레인 관리 조회
	 *
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
//SJH			intRtnVal = ydCrnschDao.getYdCrnsch(recPara, outRecSet, 20);
			intRtnVal = ydCrnschDao.getYdCrnsch(recPara, outRecSet, 300);
							
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
						
			
		
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		return outRecSet;
	}	// end of getCoilYdCrnWorkMgt
	
	/**
	 * 크레인 관리 조회 (화면:크레인작업관리)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdCrnWorkMgt_New(JDTORecord inDto) throws DAOException {
		int         intRtnVal    = 0;
		String      szMsg        = "";
		YdCrnSchDao ydCrnschDao  = new YdCrnSchDao();
		String      szMethodName = "getCoilYdCrnWorkMgt_New";
		
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");

		JDTORecord recPara = JDTORecordFactory.getInstance().create();
			
		try {
			szMsg = "JSP-SESSION [크레인작업관리 조회 - 화면:크레인작업관리] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			recPara.setField("YD_GP", 		inDto.getField("YD_GP"));		
			recPara.setField("YD_BAY_GP", 	inDto.getField("YD_BAY_GP"));		
			recPara.setField("YD_SCH_CD", 	inDto.getField("YD_SCH_CD"));							
			recPara.setField("YD_EQP_ID", 	inDto.getField("YD_EQP_ID"));
			recPara.setField("PAGE_NO",   	inDto.getField("PAGE_NO"));
			recPara.setField("ROWCOUNT",    inDto.getField("ROWCOUNT"));
			
			intRtnVal = ydCrnschDao.getYdCrnsch(recPara, outRecSet, 301);
							
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
						
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szMsg = "JSP-SESSION [크레인작업관리 조회 - 화면:크레인작업관리] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		return outRecSet;
	}	// end of getCoilYdCrnWorkMgt
	
	/**
	
	/**
	 * 적치열 베드 금지 조회 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdBedBanCnc(JDTORecord inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg="";
		String szYdStkColGp=null;	
		String szMethodName="getCoilYdBedBanCnc";
		
		
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");	
		
		YdStkBedDao ydStkbedDao = new YdStkBedDao();
		
		try {
			//적치열 구분 * 필수 
			szYdStkColGp = inDto.getFieldString("YD_GP")
						+  inDto.getFieldString("YD_BAY_GP")
						+  inDto.getFieldString("YD_EQP_GP")				
						+  inDto.getFieldString("YD_STK_COL_NO");
			
			//적치열구분 필수 
			recPara.setField("YD_STK_COL_GP",szYdStkColGp);
			
			
			//적치 베드 
			recPara.setField("YD_STK_BED_NO", yddatautil.setDataDefault(inDto.getField("YD_STK_BED_NO"), ""));
			
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
						
		
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		return outRecSet;
	}	// end of getCoilYdBedBanCnc
	
	
	

	/**
	 * 적치열 베드 금지 조회 [수정 - 화면에서 야드/동/설비/베드 정보 인자 제거시]
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto (저장위치 =  적치열구분(야드,동,스판)+베드번호)
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdBedBanCnc1(JDTORecord inDto) throws DAOException {
		int           intRtnVal    = 0;
		String        szMsg        = "";
		String        szStkPos     = null;	
		String        szYdStkColGp = null;	
		String        szYdStkBedNo = null;
		String        szMethodName = "getCoilYdBedBanCnc1";		
		JDTORecordSet outRecSet    = JDTORecordFactory.getInstance().createRecordSet("YD");		
	
		
			
		JDTORecord  recPara     = JDTORecordFactory.getInstance().create();
		YdStkBedDao ydStkbedDao = new YdStkBedDao();
		 
		try {
			//적치열 구분 * 필수
			szStkPos = yddatautil.setDataDefault(inDto.getField("STKPOS"), "");
			
			//추가 - 전체길이 체크
			if (szStkPos==null || "".equals(szStkPos) ||szStkPos.length()!=8)
			{
				System.out.println("데이터 길이가 올바르지 않습니다.");
				return outRecSet ;
			}
			
			//추가 - 서브 길이 체크 (적치열구분 6자리, 적치베드 2자리)s			
			
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
						
		
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		return outRecSet;
	}	// end of getCoilYdBedBanCnc
	
	
	
	
	/**
	 * 적치열 베드 금지 수정  
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updCoilYdBedBanCnc(JDTORecord[] inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg="";	
		String szMethodName="updCoilYdBedBanCnc";
		
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		YdStkBedDao ydStkbedDao = new YdStkBedDao();
		
		try {
			//저장위치 좌표설정화면 BED 수정
			for(int x=0;x<inDto.length;x++){
			
				
				recPara.setField("YD_STK_COL_GP"		, yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_GP"), "")); //적치열구분 필수 
				recPara.setField("YD_STK_BED_NO"		, yddatautil.setDataDefault(inDto[x].getField("YD_STK_BED_NO"), "")); //적치 베드 
				recPara.setField("YD_STK_BED_ACT_STAT"	, yddatautil.setDataDefault(inDto[x].getField("YD_STK_BED_ACT_STAT"), "")); //활성상태
				recPara.setField("YD_STK_BED_WHIO_STAT"	, yddatautil.setDataDefault(inDto[x].getField("YD_STK_BED_WHIO_STAT"), ""));//입출입  상태
				
				ydStkbedDao.updYdStkbed(recPara,0);
					
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
	}	// end of updCoilYdBedBanCnc
	
	
	
	
	/**
	 * 코일야드 크레인작업범위등록 조회 (야드/동/설비구분/설비번호를 받을때)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdCrnStsSet(JDTORecord inDto) throws DAOException {
		
		
		int intRtnVal = 0;
		String szRtnMsg = null;
		String szLogMsg = null;
		String szPara = null;
		YdEqpDao ydEqpDao = new YdEqpDao();		
	
		String szMethodName="getCoilYdCrnStsSet";
	
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		
		try {
			//저장위치 좌표설정화면 BED 조회
			
     		szPara = inDto.getFieldString("YD_GP")
							+  inDto.getFieldString("YD_BAY_GP")
							+  inDto.getFieldString("YD_EQP_GP")				
							+  inDto.getFieldString("YD_EQP_NO");
     		
     		szLogMsg = "[JSP Session]대차설비 조회 : " + szPara;
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			//설비ID 
			recPara.setField("YD_EQP_ID",szPara);
			
			intRtnVal = ydEqpDao.getYdEqp(recPara, outRecSet, 2);
			
			//에러체크
			if( intRtnVal == 0 ) {				//설비가 없는 경우
				szRtnMsg = YdConstant.RETN_CD_NOTEXIST;
				szLogMsg = "[JSP Session]해당 대차설비[" + szPara + "]가 존재하지 않습니다!";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				throw new DAOException(szRtnMsg);
			}else if( intRtnVal < 0 ) {			//조회시 에러가 발생한 경우
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
				szLogMsg = "[JSP Session]해당 대차설비[" + szPara + "]조회시 에러가 발생";;
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				throw new DAOException(szRtnMsg);
			}
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
			szLogMsg = "[JSP Session]" + getClass().getName() + " : 조회시 에러가 발생 - " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
			throw new DAOException(szRtnMsg);
		} finally {
			
		}
		
		return outRecSet;
		
	
	}	// end of getCoilYdCrnStsSet
	
	
	/**
	 * 코일야드 크레인작업범위등록 조회 (설비 ID 를 받는경우)
	 *
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
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		YdEqpDao ydEqpDao = new YdEqpDao();		
		try {
		
				//설비ID 
				recPara.setField("YD_EQP_ID",yddatautil.setDataDefault(inDto.getField("YD_EQP_ID"), ""));
				
				intRtnVal = ydEqpDao.getYdEqp(recPara, outRecSet, 3);
					
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
	 * 코일야드 차량진행관리 전체 조회 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdCarWorkListAll(JDTORecord inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg="";	
		YdCarSchDao ydCarschDao = new YdCarSchDao();	
		String szMethodName="getCoilYdCarWorkListAll";
		String chkWorkStat = null;
		String carGp = null;
		String compCarGp = null;
		
		
		int totalCount =0;
		int rownum = 10;
		int pagenum =1;
		int maxRows=0;
		
		
		
		JDTORecord    recPara    = JDTORecordFactory.getInstance().create();
		JDTORecord    revRec     = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet  = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecordSet retRecSet  = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecordSet pageRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		
		try {
			
				//설비ID 
				recPara.setField("YD_GP",yddatautil.setDataDefault(inDto.getField("YD_GP"), ""));				

				//차량구분				
				carGp = yddatautil.setDataDefault(inDto.getField("CAR_GP"), "");
				
				intRtnVal = ydCarschDao.getYdCarsch(recPara, outRecSet, 1);
				
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
			
			
				
				// 데이터 편집
				
				//레코드셋을 제일 처음으로 보낸다.
				outRecSet.first();
				
			
				//레코드셋이 없을때까지 반복한다.
				do 
				{
					revRec = outRecSet.getRecord();
				
					
					//차량구분 =		T(트레일러),C(코일카), P(팔레트)					
					//설비구분 =		TR(트레일러),PT(팔레트)
					
					//좌측 한자리만 비교한다. 
					
					compCarGp =   yddatautil.setDataDefault(revRec.getField("YD_EQP_GP"), "A").trim().substring(0, 1);
					
					//차량정보에 따른 체크
					if ( !carGp.equals("A") && !carGp.equals(compCarGp)) 
						continue;
														
					chkWorkStat = yddatautil.setDataDefault(revRec.getField("YD_EQP_WRK_STAT"), "N");
					
					//상차 정보 SETTING(야드 설비작업상태  U )
					if (chkWorkStat.equals("U"))
						{
							revRec.setField("T_STOP_LOC",revRec.getField("YD_CARLD_STOP_LOC"));
							revRec.setField("T_LEV_DT",  revRec.getField("YD_CARLD_LEV_DT"));
							revRec.setField("T_ARR_DT",  revRec.getField("YD_CARLD_ARR_DT"));
							revRec.setField("T_ST_DT",   revRec.getField("YD_CARLD_ST_DT"));
							revRec.setField("T_CMPL_DT", revRec.getField("YD_CARLD_CMPL_DT"));
						
					} //하차 정보 세팅상차 정보 SETTING(야드 설비작업상태 D)
					else if(chkWorkStat.equals("D")) {
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
				
				// 페이징 처리 
				
				//Total Count Setting				
				totalCount = retRecSet.size();
				
								
				//revRec 초기화 
				revRec =null;
				retRecSet.first();
				
				pagenum = inDto.getFieldInt("STARTS");
				rownum= inDto.getFieldInt("ROWCOUNT");
				
				
				if ((pagenum+rownum-1) >= totalCount)
					maxRows = totalCount;
				else
					maxRows = pagenum+rownum-1;
				
				for(int i = pagenum-1 ; i<maxRows;i++)
				{				
					revRec = retRecSet.getRecord(i);					
					revRec.setField("TOTAL", Integer.toString(totalCount));					
					pageRecSet.addRecord(revRec);					
				}				
				
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		return pageRecSet;
	}	// end of getCoilYdCarWorkListAll
	
	
	
	
	
	
	/**
	 * 코일야드 크레인 상태 설정(고장/정상)
	 *
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
				recPara.setField("YD_EQP_ID"	, yddatautil.setDataDefault(inDto[x].getField("YD_EQP_ID"), ""));
				recPara.setField("YD_EQP_STAT"	, yddatautil.setDataDefault(inDto[x].getField("YD_EQP_STAT"), ""));
				
				
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
	 * 코일야드 크레인 상태 설정 (ON_LINE, OFF_LINE)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updCoilYdCrnStsSetCrnMode(JDTORecord[] inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg="";
	
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
	 * 코일야드 크레인 상태 설정 (ON_LINE, OFF_LINE)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updCoilYdCrnStsSetCrnMode2(JDTORecord[] inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg="";
	
		String szMethodName="updCoilYdCrnStsSetCrnMode2";
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
				recPara.setField("YD_EQP_WRK_MODE2", yddatautil.setDataDefault(inDto[x].getField("YD_EQP_WRK_MODE2"), ""));
				
				ydUtils.putLog(szSessionName, szMethodName, "hun "+yddatautil.setDataDefault(inDto[x].getField("YD_EQP_WRK_MODE2"), ""), YdConstant.DEBUG);
				
				
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
	 * 코일야드 크레인 작업실적 응답 백업
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void sendCoilYdCrnAnswer(JDTORecord[] inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg="";
	
		String szMethodName="sendCoilYdCrnAnswer";
		String tempLog = null;
		String szYD_EQP_ID = "";
		String szYD_SCH_CD = "";
		String szYD_CRN_SCH_ID = "";
		String szCRN_ANSWER = "";
		JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
		
		try {
			//저장위치 좌표설정화면 BED 수정
			for(int x=0;x<inDto.length;x++){
				tempLog = inDto[x].toString();
				System.out.println(tempLog);				
								
				//입출입  상태
				szYD_EQP_ID 	= yddatautil.setDataDefault(inDto[x].getField("YD_EQP_ID"), "");
				szYD_SCH_CD 	= yddatautil.setDataDefault(inDto[x].getField("YD_SCH_CD"), "");
				szYD_CRN_SCH_ID = yddatautil.setDataDefault(inDto[x].getField("YD_CRN_SCH_ID"), "");
				szCRN_ANSWER 	= yddatautil.setDataDefault(inDto[x].getField("YD_CRN_ANSWER"), "");
				
				ydUtils.putLog(szSessionName, szMethodName, "hun szCRN_ANSWER ="+szCRN_ANSWER, YdConstant.DEBUG);
				
				if(YdConstant.CRN_WRK_RE_LD_WR.equals(szCRN_ANSWER)){
					
					szMsg = "==== 크레인 권상 실적응답 백업 start====";
					ydUtils.putLog(szSessionName, szMethodName, szMsg,YdConstant.DEBUG);
					
			        /*
			         * C열연코일L2 크레인작업실적응답 전송  - YDY5L005 권상완료
			         */
			        recInTemp = JDTORecordFactory.getInstance().create(); 
			        recInTemp.setField("MSG_ID"          , "YDY5L005");
			        recInTemp.setField("YD_EQP_ID"       , szYD_EQP_ID);			        	//야드설비ID
			        recInTemp.setField("YD_WRK_PROG_STAT", YdConstant.YD_EQP_STAT_UP_CMPL);		//야드작업진행상태
			        recInTemp.setField("YD_SCH_CD"       , szYD_SCH_CD);				        //야드스케줄코드
			        recInTemp.setField("YD_CRN_SCH_ID"   , szYD_CRN_SCH_ID);		            //야드크레인스케줄ID
			        recInTemp.setField("YD_L2_WR_GP"     , YdConstant.CRN_WRK_RE_LD_WR);		//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
			        recInTemp.setField("YD_L3_HD_RS_CD"  , YdConstant.CRN_WRK_RE_CD_NORMAL_HD);	//야드L3처리결과코드
					ydDelegate.sendMsg(recInTemp);
					szMsg = "[권상실적처리]C열연코일L2 크레인작업실적응답[YDY5L005] 전송 완료" ;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	                
	                szMsg = "==== 크레인 권상 실적응답 백업 end====";
					ydUtils.putLog(szSessionName, szMethodName, szMsg,YdConstant.DEBUG);
				}else{
					
					szMsg = "==== 크레인 권하 실적응답 백업 start====";
					ydUtils.putLog(szSessionName, szMethodName, szMsg,YdConstant.DEBUG);
					
			        /*
			         * C열연코일L2 크레인작업실적응답 전송  - YDY5L005 권상완료
			         */
			        recInTemp = JDTORecordFactory.getInstance().create(); 
			        recInTemp.setField("MSG_ID"          , "YDY5L005");
			        recInTemp.setField("YD_EQP_ID"       , szYD_EQP_ID);			        	//야드설비ID
			        recInTemp.setField("YD_WRK_PROG_STAT", YdConstant.YD_EQP_STAT_DN_CMPL);		//야드작업진행상태
			        recInTemp.setField("YD_SCH_CD"       , szYD_SCH_CD);				        //야드스케줄코드
			        recInTemp.setField("YD_CRN_SCH_ID"   , szYD_CRN_SCH_ID);		            //야드크레인스케줄ID
			        recInTemp.setField("YD_L2_WR_GP"     , YdConstant.CRN_WRK_RE_DN_WR);		//야드L2실적구분 - U:권상실적,D:권하실적,E:비상조업실적,R:고장,M:모드변경,J:지시요구,F:강제권하
			        recInTemp.setField("YD_L3_HD_RS_CD"  , YdConstant.CRN_WRK_RE_CD_NORMAL_HD);	//야드L3처리결과코드
					ydDelegate.sendMsg(recInTemp);
					szMsg = "[권상실적처리]C열연코일L2 크레인작업실적응답[YDY5L005] 전송 완료" ;
	                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	                
	                szMsg = "==== 크레인 권하 실적응답 백업 end====";
					ydUtils.putLog(szSessionName, szMethodName, szMsg,YdConstant.DEBUG);
					
				}
				
			}			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}	// end of updCoilYdCrnStsSetCrnMode
	
	
	/**
	 * 코일야드 대차 상태 설정(고장/정상)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updCoilYdTcarStsSetStat(JDTORecord[] inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg="";
	
		String szMethodName="updCoilYdTcarStsSetStat";
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
					//return outRecSet;
				} // end of if
							
				
				System.out.println("updCoilYdTcarStsSetStat");
				System.out.println(recPara.toString());
			
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}	// end of updCoilYdTcarStsSetStat
	
	
	/**
	 * 코일야드 대차 모드  설정 (ON_LINE, OFF_LINE)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updCoilYdTcarStsSetMode(JDTORecord[] inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg="";
	
		String szMethodName="updCoilYdTcarStsSetMode";
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
							
				
				System.out.println("updCoilYdTcarStsSetMode");
				System.out.println(recPara.toString());
			
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}	// end of updCoilYdTcarStsSetMode
	

	/**
	 * 코일야드 차량정지위치 상태등록  조회 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto (차량정지위치:적치열구분)
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdCarStopLocStsReg(JDTORecord inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg="";
	
		YdStkColDao ydStkcolDao = new YdStkColDao();		
	
		String szMethodName="getCoilYdCarStopLocStsReg";
		String tempLog = null;
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		try {
			//저장위치 좌표설정화면 BED 수정
		
				tempLog = inDto.toString();
				System.out.println(tempLog);				
				
				//설비ID 
				recPara.setField("YD_PNT_CD",yddatautil.setDataDefault(inDto.getField("YD_PNT_CD"), ""));
				recPara.setField("YD_GP",yddatautil.setDataDefault(inDto.getField("YD_GP"), ""));
				intRtnVal = ydStkcolDao.getYdStkcol(recPara, outRecSet, 15);
					
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
	}	// end of getCoilYdCarStopLocStsReg
	
	
	
	/**
	 * 코일야드 차량정지위치 상태 등록 수정 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updCoilYdCarStopLocStsReg(JDTORecord[] inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg="";

		String szMethodName="updCoilYdCarStopLocStsReg";
		String tempLog = null;
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		YdStkColDao ydStkcolDao = new YdStkColDao();		
		
		try {
			//저장위치 좌표설정화면 BED 수정
			for(int x=0;x<inDto.length;x++){
				tempLog = inDto[x].toString();
				System.out.println(tempLog);				
								
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
				
				System.out.println("updCoilYdTcarStsSetMode");
				System.out.println(recPara.toString());
			
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}	// end of updCoilYdCarStopLocStsReg
	
	
	
	
	/**
	 * SPAN별 저장위치관리  
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdStkLocInfoList(JDTORecord inDto) throws DAOException {
		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");

		String szMsg            = "";
		String szMethodName     = "getCoilYdStkLocInfoList";
		int intRtnVal = 0;

		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();

		try {
			//적치열 구분과 적치베드 번호로 분리
			recPara.setField("YD_STK_COL_GP",      yddatautil.setDataDefault(inDto.getField("YD_STK_COL_GP"), ""));
			
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 20);
			if (intRtnVal <0) {
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
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilYdStkLocInfoList		
	
	
	

	
	/**
	 *  EVENT별 작업재료 조회 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdEventWorkMatRef(JDTORecord inDto) throws DAOException {
		
		// Log Message 
		String szMsg        = ""; 		
		String szMethodName = "getCoilYdEventWorkMatRef";	
		
		String szEditStkPos =null;
		String szEditStkCol =null;
		String szEditStkBed =null;
		
		// ERROR CHECK
		int intRtnVal = 0;
		
		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecord    recEdit   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("yd");
		JDTORecordSet retRecSet = JDTORecordFactory.getInstance().createRecordSet("yd");
		
		YdCrnWrkMtlDao ydCrnWrkMtlDao = new YdCrnWrkMtlDao();
		
		try {
			//작업계상일
			recPara.setField("YD_WRK_HDS_DD",      yddatautil.setDataDefault(inDto.getField("YD_WRK_HDS_DD"), ""));
			//작업근무조
			recPara.setField("YD_WRK_DUTY",        yddatautil.setDataDefault(inDto.getField("YD_WRK_DUTY"), ""));
			
			//입출입구분을 DAO에서 SCH_CD 로 처리하기 때문에 변경해서 넣어준다.
			recPara.setField("YD_SCH_CD",          yddatautil.setDataDefault(inDto.getField("YD_SCH_WHIO_GP"), ""));
			
			//주여구분
			//recPara.setField("HRSHR_WO_ORDRMN_GP", inDto.getField("HRSHR_WO_ORDRMN_GP"));
			recPara.setField("ORD_YEOJAE_GP",   yddatautil.setDataDefault(inDto.getField("HRSHR_WO_ORDRMN_GP"), ""));
			
			//페이징설정 
			recPara.setField("PAGE_CNT1",          inDto.getField("PAGE_NO"));
			recPara.setField("PAGE_CNT2",          inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT1",           inDto.getField("ROWCOUNT"));
			recPara.setField("ROW_CNT2",           inDto.getField("ROWCOUNT"));
						
			intRtnVal = ydCrnWrkMtlDao.getYdCrnwrkmtl(recPara, outRecSet, 5);
			
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
			
			//레코드 위치를 가장 앞으로 돌려준다.
			outRecSet.first();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			do {
				
				//스케줄 코드를  설비구분을 분리하여 YD_EQP_GP 컬럼에 넣어준다.
				recEdit = outRecSet.getRecord();
				szEditStkCol = yddatautil.setDataDefault(recEdit.getField("YD_STK_COL_GP"), ""); 
				szEditStkBed = yddatautil.setDataDefault(recEdit.getField("YD_STK_BED_NO"), "");
					
				szEditStkPos = szEditStkCol + "-"+ szEditStkBed;
				recEdit.setField("YD_STK_POS", szEditStkPos);
				
				retRecSet.addRecord(recEdit);
				
			}while(outRecSet.next());
	
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return retRecSet;
	}//end of getCoilYdEventWorkMatRef	
	
	
	
	/**
	 * 압연지시관리 (조회)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdRollCmdRef(JDTORecord inDto) throws DAOException {

		// ERROR CHECK
		int intRtnVal = 0;

		// Log Message 
		String szMsg ="";		
		String szMethodName="getCoilYdRollCmdRef";
		
		String szEditStkPos =null;
		String szEditStkCol =null;
		String szEditStkBed =null;
		
		
		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecord    recEdit   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecordSet retRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
					
		YdStockDao ydStockDao = new YdStockDao();
		
		
		try {
			
			//압연공장구분
			recPara.setField("MILL_PLNT_GP",      inDto.getField("MILL_PLNT_GP"));
			
			//페이징설정 
			recPara.setField("PAGE_CNT1",          inDto.getField("PAGE_NO"));
			recPara.setField("PAGE_CNT2",          inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT1",           inDto.getField("ROWCOUNT"));
			recPara.setField("ROW_CNT2",           inDto.getField("ROWCOUNT"));
			
			intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 9);
			
			if (intRtnVal <= 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR); 
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR );
				}
				return outRecSet;
			} // end of if
			
			//레코드 위치를 가장 앞으로 돌려준다.
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
			
	
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return retRecSet;
	}//end of getCoilYdRollCmdRef
	
		
	/**
	 * 수불구 변경등록 (조회)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto (저장위치 =  적치열구분(야드,동,스판)+베드번호)
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdBedBanCnc2(JDTORecord inDto) throws DAOException {
		
		int intRtnVal = 0;
		String szStkPos=null;	
		String szYdStkColGp = null;	
		String szYdStkBedNo = null;
		
		String szMsg="";	
		String szMethodName="getCoilYdBedBanCnc2";
		
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");	
		
		
		//DAO 생성 
		YdStkBedDao ydStkbedDao = new YdStkBedDao();
			
		try {
			//적치열 구분 * 필수
			szStkPos = yddatautil.setDataDefault(inDto.getField("STKPOS"), "");
			
			//추가 - 전체길이 체크
			if (szStkPos==null || "".equals(szStkPos) ||szStkPos.length()!=8)
			{
				System.out.println("데이터 길이가 올바르지 않습니다.");
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
						
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		return outRecSet;
	}	// end of getCoilYdBedBanCnc2
	
	
	/**
	 * 수불구 변경등록 (수정) 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updCoilYdBedBanCnc2(JDTORecord[] inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg="";	
		String szMethodName="updCoilYdBedBanCnc2";
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		
		
		YdStkBedDao ydStkbedDao = new YdStkBedDao();
		try {
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
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}	// end of updCoilYdBedBanCnc2
	
	
	/**
	 *  야드크레인 작업관리 (작업취소)
	 *
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
	 *  야드관리 > 코일소재야드 > 설비/차량관리 > 이송대상재관리   (이송대상목록 조회)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData getCoilYdMvMtlList(GridData inDto) throws DAOException {
		GridData 		rtnGrd 		= new GridData();
		JDTORecordSet   outRecSet  	= null;
		
		CoilJspDAO 		dao 		= new CoilJspDAO();
		JDTORecord 		recPara		= null;
		
		try {
			
			// 파라미터 셋팅 
			recPara		= JDTORecordFactory.getInstance().create(); 			
			recPara.setField("V_YD_GP", 	inDto.getParam("YD_GP").trim());	/*야드구분*/
			recPara.setField("V_YD_BAY_GP", inDto.getParam("YD_BAY_GP"));
			recPara.setField("V_PLANT_GP", 	inDto.getParam("FRTOMOVE_PLANT_GP"));
			recPara.setField("V_STL_NO", 	inDto.getParam("STL_NO"));
			recPara.setField("V_FROM_DATE", inDto.getParam("DATE_FROM"));
			recPara.setField("V_TO_DATE", 	inDto.getParam("DATE_TO"));

			// DAO 호출
			outRecSet = dao.getCoilYdMvMtlList(recPara);
			
			if(outRecSet == null){
				rtnGrd.setMessage("조회된 데이터가 없습니다.");
				rtnGrd.addParam("ret", "-1");
			}else{
				rtnGrd = CmUtil.genGridData(inDto, outRecSet);
				
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return rtnGrd;
	}//end of getCoilYdMvMtlList

	
	/**
	 *  스케줄코드별기준 조회 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdSchCdInfoList(JDTORecord inDto) throws DAOException {
		
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		YdSchRuleDao     ydSchRuleDao    = new YdSchRuleDao();
		
		String szMsg        = "";		
		String szMethodName = "getCoilYdSchCdInfoList";
	
		int intRtnVal = 0;
		
		try {
			
			recPara.setField("YD_GP",     yddatautil.setDataDefault(inDto.getField("YD_SCH_CD").toString().substring(0, 1), ""));
			recPara.setField("YD_BAY_GP", yddatautil.setDataDefault(inDto.getField("YD_SCH_CD").toString().substring(1, 2), ""));
			recPara.setField("YD_SCH_CD", yddatautil.setDataDefault(inDto.getField("YD_SCH_CD"), ""));
			
			recPara.setField("PAGE_CNT1", inDto.getField("PAGE_NO" ));
			recPara.setField("PAGE_CNT2", inDto.getField("PAGE_NO" ));
			recPara.setField("ROW_CNT1",  inDto.getField("ROWCOUNT"));
			recPara.setField("ROW_CNT2",  inDto.getField("ROWCOUNT"));
			
			
			int pgNo1 = recPara.getFieldInt("PAGE_CNT1");
			int pgNo2 = recPara.getFieldInt("PAGE_CNT2");
			int rCnt1 = recPara.getFieldInt("ROW_CNT1");
			int rCnt2 = recPara.getFieldInt("ROW_CNT2");
			
		
			intRtnVal = ydSchRuleDao.getYdSchrule(recPara, outRecSet, 1);
			
			if (intRtnVal <= 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
				}
				
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				//return outRecSet;
				return outRecSet;
			} // end of if
			
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//레코드셋이 없을때까지 반복한다.
			outRecSet.first();
			
			logger.println(LogLevel.DEBUG_TEXT, "getCoilYdSchCdInfoList");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilYdSchCdInfoList	
	
	/**
	 * 스케줄코드별기준 등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void insCoilYdSchCdInfoList(JDTORecord[] inDto) throws DAOException {
		
		int intRtnVal = 0;
		
		String szMsg        = "";
		String szMethodName = "insCoilYdSchCdInfoList";

		JDTORecord    recPara       = JDTORecordFactory.getInstance().create();		
		YdSchRuleDao  ydSchRuleDao  = new YdSchRuleDao();

		try {
			//저장위치 좌표설정화면 BED 수정
			for(int x=0;x<inDto.length;x++){
			
				// 스케줄기준 정보 설정
				recPara.setField("YD_GP", inDto[x].getField("YD_GP"));
				recPara.setField("YD_BAY_GP", inDto[x].getField("YD_BAY_GP"));
				recPara.setField("YD_SCH_WHIO_GP", inDto[x].getField("YD_SCH_WHIO_GP"));
				recPara.setField("YD_SCH_DIV_GP", inDto[x].getField("YD_SCH_DIV_GP"));
				
				recPara.setField("YD_SCH_RULE_ACT_STAT",  yddatautil.setDataDefault(inDto[x].getField("YD_SCH_RULE_ACT_STAT"),  ""));
				recPara.setField("YD_WRK_CRN",            yddatautil.setDataDefault(inDto[x].getField("YD_WRK_CRN"),            ""));
				recPara.setField("YD_WRK_CRN_PRIOR",      yddatautil.setDataDefault(inDto[x].getField("YD_WRK_CRN_PRIOR"),      ""));
				recPara.setField("YD_ALT_CRN_YN",         yddatautil.setDataDefault(inDto[x].getField("YD_ALT_CRN_YN"),         ""));
				recPara.setField("YD_ALT_CRN",            yddatautil.setDataDefault(inDto[x].getField("YD_ALT_CRN"),            ""));
				recPara.setField("YD_ALT_CRN_PRIOR",      yddatautil.setDataDefault(inDto[x].getField("YD_ALT_CRN_PRIOR"),      ""));
				recPara.setField("CD_CONTENTS",           yddatautil.setDataDefault(inDto[x].getField("CD_CONTENTS"),           ""));
				recPara.setField("YD_SCH_PROH_EXN",       yddatautil.setDataDefault(inDto[x].getField("YD_SCH_PROH_EXN"),       ""));
				
				recPara.setField("YD_SCH_RNG_CD",   inDto[x].getField("YD_MTL_ITEM").toString() + inDto[x].getField("YD_EQP_GP").toString());
				recPara.setField("YD_SCH_CD",       recPara.getFieldString("YD_GP") 
						                          + recPara.getFieldString("YD_BAY_GP") 
						                          + recPara.getFieldString("YD_SCH_RNG_CD")
						                          + recPara.getFieldString("YD_SCH_WHIO_GP")
						                          + recPara.getFieldString("YD_SCH_DIV_GP"));
				// 스케줄기준 정보 INSERT 				
				intRtnVal  = ydSchRuleDao.insYdSchrule(recPara);
				
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
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
				
				//전달 인자 LOG출력 (DEBUG 용) 
				szMsg = recPara.toString();
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}	// end of insCoilYdSchCdInfoList
	
	/**
	 * 스케줄코드별기준 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updCoilYdSchCdInfoList(JDTORecord[] inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg= "";
		String szMethodName = "updCoilYdSchCdInfoList";
		String szOperationName = "스케줄코드별기준 수정";
		
		JDTORecord    recPara     = JDTORecordFactory.getInstance().create();		
		YdSchRuleDao  ydSchRuleDao  = new YdSchRuleDao();
		
		
		try {
			//저장위치 좌표설정화면 BED 수정
			for(int x=0;x<inDto.length;x++){
													
				
				//설비 ID 및 수정항목 세팅 
				recPara.setField("YD_SCH_CD",             yddatautil.setDataDefault(inDto[x].getField("YD_SCH_CD"           ),           ""));
				recPara.setField("YD_GP",                 yddatautil.setDataDefault(inDto[x].getField("YD_GP"               ),           ""));
				recPara.setField("YD_BAY_GP",             yddatautil.setDataDefault(inDto[x].getField("YD_BAY_GP"           ),           ""));
				recPara.setField("YD_MTL_ITEM",           yddatautil.setDataDefault(inDto[x].getField("YD_MTL_ITEM"         ),           ""));
				recPara.setField("YD_EQP_GP",             yddatautil.setDataDefault(inDto[x].getField("YD_EQP_GP"           ),           ""));
				recPara.setField("YD_SCH_RNG_CD",         yddatautil.setDataDefault(inDto[x].getField("YD_SCH_RNG_CD"       ),           ""));
				recPara.setField("YD_SCH_WHIO_GP",        yddatautil.setDataDefault(inDto[x].getField("YD_SCH_WHIO_GP"      ),           ""));
				recPara.setField("YD_SCH_DIV_GP",         yddatautil.setDataDefault(inDto[x].getField("YD_SCH_DIV_GP"       ),           ""));
				recPara.setField("YD_SCH_RULE_ACT_STAT",  yddatautil.setDataDefault(inDto[x].getField("YD_SCH_RULE_ACT_STAT"),           ""));
				recPara.setField("YD_WRK_CRN",            yddatautil.setDataDefault(inDto[x].getField("YD_WRK_CRN"          ),           ""));
				recPara.setField("YD_WRK_CRN_PRIOR",      yddatautil.setDataDefault(inDto[x].getField("YD_WRK_CRN_PRIOR"    ),           ""));
				recPara.setField("YD_ALT_CRN_YN",         yddatautil.setDataDefault(inDto[x].getField("YD_ALT_CRN_YN"       ),           ""));
				recPara.setField("YD_ALT_CRN",            yddatautil.setDataDefault(inDto[x].getField("YD_ALT_CRN"          ),           ""));
				recPara.setField("YD_ALT_CRN_PRIOR",      yddatautil.setDataDefault(inDto[x].getField("YD_ALT_CRN_PRIOR"    ),           ""));
				recPara.setField("CD_CONTENTS",           yddatautil.setDataDefault(inDto[x].getField("CD_CONTENTS"         ),           ""));
				recPara.setField("YD_SCH_PROH_EXN",       yddatautil.setDataDefault(inDto[x].getField("YD_SCH_PROH_EXN"     ),           ""));
				
				//fix 20081230 수정자 부분추가 
				//recPara.setField("MODIFIER",             yddatautil.setDataDefault(inDto[x].getField("SZUSERID"),            ""));
				
				ydUtils.displayRecord(szOperationName, recPara);
				
				// 스케줄기준 정보 UPDATE
				intRtnVal  = ydSchRuleDao.updYdSchrule(recPara, 0);
				
				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						
					}
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} // end of if
						
			}			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}	// end of updCoilYdEqpSetSpec
	
	
	/**
	 *  야드동정보 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdBaySetList(JDTORecord inDto) throws DAOException {
		
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		//YdCarFtmvMtlDao  ydCarftmvmtlDao = new YdCarFtmvMtlDao();
		
		String szMsg        = "";		
		String szMethodName = "getCoilYdBaySetList";
		
		YdStkColDao ydStkColDao = new YdStkColDao();  
	       
		int intRtnVal = 0;
		
		try {
			recPara.setField("YD_GP",     yddatautil.setDataDefault(inDto.getField("YD_GP"), ""));
			
			recPara.setField("PAGE_CNT1", inDto.getField("PAGE_NO" ));
			recPara.setField("PAGE_CNT2", inDto.getField("PAGE_NO" ));
			recPara.setField("ROW_CNT1",  inDto.getField("ROWCOUNT"));
			recPara.setField("ROW_CNT2",  inDto.getField("ROWCOUNT"));
	
			intRtnVal = ydStkColDao.getYdStkcol(recPara, outRecSet, 5);
			
			
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
			
			logger.println(LogLevel.DEBUG_TEXT, "getCoilYdMvMtlList");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilYdBaySetList

	
	/**
	 *  야드적치열정보 조회 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdColStsSetInfo(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getCoilYdColStsSetInfo";
		
		YdStkColDao ydStkColDao = new YdStkColDao();
		
		int intRtnVal = 0;
		
		try {
			recPara.setField("YD_STK_COL_GP",     yddatautil.setDataDefault(inDto.getField("YD_STK_COL_GP"), ""));
			
			intRtnVal = ydStkColDao.getYdStkcol(recPara, outRecSet, 6);
			
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
			
			logger.println(LogLevel.DEBUG_TEXT, "getCoilYdColStsSetInfo");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilYdBaySetList

	
	/**
	 * 야드적치열정보 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updCoilYdColStsSetInfo(JDTORecord[] inDto) throws DAOException {

		int           intRtnVal     = 0;
		String        szMsg         = "";
		String        szMethodName  = "updCoilYdColStsSetInfo";
		JDTORecord    recPara       = JDTORecordFactory.getInstance().create();
		YdStkColDao   ydStkColDao   = new YdStkColDao();
		
		try {
			//야드적치열정보 수정
			for(int x=0;x<inDto.length;x++){
				
				//설비 ID 및 수정항목 세팅 
				recPara.setField("YD_STK_COL_GP",         yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_GP"       ),    ""));
				recPara.setField("YD_STK_COL_ACT_STAT",   yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_ACT_STAT" ),    ""));
				
				intRtnVal = ydStkColDao.updYdStkcol(recPara, 0);
						
				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						
					}
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} // end of if
			}				
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}	// end of updCoilYdColStsSetInfo
	
	
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
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getCoilYdColStkPosList";

		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao(); 
		
		int intRtnVal = 0;
		
		try {
					
			recPara.setField("YD_STK_COL_GP",     yddatautil.setDataDefault(inDto.getField("YD_GP"),         "") 
									           +  yddatautil.setDataDefault(inDto.getField("YD_BAY_GP"),     "")
									           +  yddatautil.setDataDefault(inDto.getField("YD_EQP_GP"),     "")
									           +  yddatautil.setDataDefault(inDto.getField("YD_STK_COL_NO"), ""));
			recPara.setField("YD_STK_COL_NO",     yddatautil.setDataDefault(inDto.getField("YD_STK_COL_NO"), ""));
			
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
			
			logger.println(LogLevel.DEBUG_TEXT, "getCoilYdColStkPosList");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilYdColStkPosList
	
	/**
	 *  열별 저장위치 조회 (단 별)
	 *
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
				
			recPara         = JDTORecordFactory.getInstance().create();
			
			recPara.setField("YD_STK_COL_GP",     yddatautil.setDataDefault(inDto.getField("YD_GP"),         "") 
									           +  yddatautil.setDataDefault(inDto.getField("YD_BAY_GP"),     "")
									           +  yddatautil.setDataDefault(inDto.getField("YD_EQP_GP"),     "")
									           +  yddatautil.setDataDefault(inDto.getField("YD_STK_COL_NO"), ""));
			recPara.setField("YD_STK_COL_NO",     yddatautil.setDataDefault(inDto.getField("YD_STK_COL_NO"), ""));
			recPara.setField("YD_STK_LYR_NO",     yddatautil.setDataDefault(inDto.getField("YD_STK_LYR_NO"), ""));
			
			
			
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 51);
		
			
			ydUtils.displayRecord(szOperationName, recPara);
			
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
	 * 열별 저장위치 등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void insCoilYdColStkPosList(JDTORecord[] inDto) throws DAOException {
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
	}	// end of insCoilYdColStkPosList
	
	/**
	 * 열별 저장위치 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updCoilYdColStkPosList(JDTORecord[] inDto) throws DAOException {

		int           intRtnVal     = 0;
		String        szMsg         = "";
		String        szMethodName  = "updCoilYdColStkPosList";
		JDTORecord    recPara       = JDTORecordFactory.getInstance().create();		
		String        szQuery       = "";
		
		String    colGp        = "";
		String    lyrNo        = "";
		
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		YDComUtil   ydUtil      = new YDComUtil();
		try {
			
			//열별 저장위치 수정
			for(int x=0;x<inDto.length;x++){
				
				//수정항목 세팅 
				colGp = yddatautil.setDataDefault(inDto[x].getField("YD_GP"),            "")
			          + yddatautil.setDataDefault(inDto[x].getField("YD_BAY_GP"),        "")
	 		          + yddatautil.setDataDefault(inDto[x].getField("YD_EQP_GP"),        "")
			          + yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_NO"),    "");
			    
				lyrNo = yddatautil.setDataDefault(inDto[x].getField("YD_STK_LYR_NO"),    "");
				
			    recPara.setField("YD_STK_COL_GP", colGp);
			    recPara.setField("STL_NO",        yddatautil.setDataDefault(inDto[x].getField("STL_NO"),           ""));
			    recPara.setField("YD_STK_BED_NO", yddatautil.setDataDefault(inDto[x].getField("YD_STK_BED_NO"),    ""));
			    recPara.setField("YD_STK_LYR_NO", ydUtil.filler(lyrNo, 3, "N"));
			    
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
	}	// end of updCoilYdColStkPosList
	
	/**
	 *  야드스케쥴코드와 코드설명 목록 조회 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdSchCdList(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getCoilYdSchCdList";
	
		//test_parameter
		String ydGp       = "";
	       
		int intRtnVal = 0;
		YdSchRuleDao ydSchruleDao = new YdSchRuleDao();
		
		try {
			ydGp = yddatautil.setDataDefault(inDto.getField("YD_EQP_ID"), "").toString().substring(0, 1)
			     + yddatautil.setDataDefault(inDto.getField("YD_EQP_ID"), "").toString().substring(1, 2);
			recPara.setField("YD_SCH_CD", ydGp);
			
			intRtnVal = ydSchruleDao.getYdSchrule(recPara, outRecSet, 2);
			
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
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilYdSchCdList
	
	
		
	/**
	 * 코일야드 야드동별재고현황 목록 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdBayInvList(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara    = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet  = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getCoilYdBayInvList";
	      
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao(); 
		
		int intRtnVal = 0;
		
		try {
			recPara.setField("YD_GP",    yddatautil.setDataDefault(inDto.getField("YD_GP"),     ""));
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 22);
			
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
			
			logger.println(LogLevel.DEBUG_TEXT, "getCoilYdBayInvList");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilYdColStkPosList
	
	/**
	 * 야드관리 > 코일소재야드 > 야드재공관리 > 재료진도별 재공현황
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
			/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilMtlProgIdInlnStat*/
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 306);
			
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
	 * 콘베어정보목록 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdConveyorMgt(JDTORecord inDto) throws DAOException {
		JDTORecord     recPara    = JDTORecordFactory.getInstance().create();
		JDTORecordSet  outRecSet  = JDTORecordFactory.getInstance().createRecordSet("yd");
		String szMsg              = "";
		String szMethodName       = "getCoilYdConveyorMgt";
		YdStkLyrDao ydStkLyrDao   = new YdStkLyrDao(); 
		int intRtnVal = 0;
		String sConv  = "";
		
		try {
			
			sConv = yddatautil.setDataDefault(inDto.getField("YD_CONVEYOR_BRANCH_CD"), "");
			
			//
			recPara.setField("YD_STK_COL_GP1",  yddatautil.setDataDefault(inDto.getField("YD_STK_COL_GP1"), ""));
			recPara.setField("YD_STK_COL_GP2",  yddatautil.setDataDefault(inDto.getField("YD_STK_COL_GP2"), ""));
		
			/*
			recPara.setField("PAGE_CNT1", inDto.getField("PAGE_NO" ));
			recPara.setField("PAGE_CNT2", inDto.getField("PAGE_NO" ));
			recPara.setField("ROW_CNT1",  inDto.getField("ROWCOUNT"));
			recPara.setField("ROW_CNT2",  inDto.getField("ROWCOUNT"));
			*/
			
			/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdConveyorMgt_PAGE2*/
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 37);
			
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
			
			logger.println(LogLevel.DEBUG_TEXT, "getCoilYdConveyorMgt");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}
	/**
	 * 콘베어정보목록img 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdConveyorMgtImg(JDTORecord inDto) throws DAOException {
		JDTORecord     recPara    = JDTORecordFactory.getInstance().create();
		JDTORecordSet  outRecSet  = JDTORecordFactory.getInstance().createRecordSet("yd");
		String szMsg              = "";
		String szMethodName       = "getCoilYdConveyorMgtImg";
		YdStkLyrDao ydStkLyrDao   = new YdStkLyrDao(); 
		int intRtnVal = 0;
		String sConv  = "";
		
		try {
			
			//
			recPara.setField("V_TEMP", "A");
			
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 606);
			
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
			
			logger.println(LogLevel.DEBUG_TEXT, "getCoilYdConveyorMgtImg");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}
	
	
	/**
	 *  저장품 저장위치 조회 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdStkPosInfo(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getCoilYdStkPosInfo";
	
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao(); 
		
		int intRtnVal = 0;
		
		try {
			recPara.setField("STL_NO",     yddatautil.setDataDefault(inDto.getField("STL_NO"), ""));
			
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
			
			logger.println(LogLevel.DEBUG_TEXT, "getCoilYdStkPosInfo");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilYdStkPosInfo
	
	
	/**
	 * 저장위치 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updCoilYdStkPosInfo(JDTORecord[] inDto) throws DAOException {
		int           intRtnVal     = 0;
		int           intRtnVal2    = 0;
		String        szMsg         = "";
		String        szMethodName  = "updCoilYdStkPosInfo";
		JDTORecord    recPara       = JDTORecordFactory.getInstance().create();
		JDTORecord    tmpPara       = JDTORecordFactory.getInstance().create();
		
		JDTORecord    updPara1      = JDTORecordFactory.getInstance().create();
		JDTORecord    updPara2      = JDTORecordFactory.getInstance().create();
		
		String        updQuery1     = ""; // 현재위치의 재료번호를 NULL로 수정하는 쿼리
		String        updQuery2     = ""; // 수정위치의 재료번호를 수정하는 쿼리
		
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao(); 
		YdStockDao  ydStockDao  = new YdStockDao();
		
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		//STL_NO, STK_POS, STK_LYR, UPD_POS, UPD_LYR
//		String stlNo   = "";
//		String stkPos  = "";
//		String stkLyr  = "";
//		String updPos  = "";
//		String updLyr  = "";
		
		YdDBAssist ydb = new YdDBAssist();
		
		try {
			
			recPara.setField("STL_NO",      yddatautil.setDataDefault(inDto[0].getField("STL_NO"),         ""));
			recPara.setField("STK_POS",     yddatautil.setDataDefault(inDto[0].getField("YD_STK_POS"),     ""));
			recPara.setField("STK_LYR",     yddatautil.setDataDefault(inDto[0].getField("YD_STK_LYR_NO"),  ""));
			recPara.setField("UPD_POS",     yddatautil.setDataDefault(inDto[0].getField("UPD_STK_POS"),    ""));
			recPara.setField("UPD_LYR",     yddatautil.setDataDefault(inDto[0].getField("UPD_STK_LYR_NO"), ""));
			
			//stlNo  = recPara.getFieldString("STL_NO");
			//stkPos = recPara.getFieldString("STK_POS");
			//stkLyr = recPara.getFieldString("STK_LYR");
			//updPos = recPara.getFieldString("UPD_POS");
			//updLyr = recPara.getFieldString("UPD_LYR");
			
			tmpPara.setField("UPD_POS",       yddatautil.setDataDefault(inDto[0].getField("UPD_STK_POS"),    ""));
			tmpPara.setField("UPD_LYR",       yddatautil.setDataDefault(inDto[0].getField("UPD_STK_LYR_NO"), ""));			
			tmpPara.setField("UPD_POS",       yddatautil.setDataDefault(inDto[0].getField("UPD_STK_POS"),    ""));
			tmpPara.setField("YD_STK_BED_NO", tmpPara.getFieldString("UPD_POS").substring(6, 8));
			tmpPara.setField("YD_STK_LYR_NO", tmpPara.getFieldString("UPD_LYR"));
			
			// 1) 수정위치의 재료번호 등록 여부를 조회 -----------------------------------------------------
			
			intRtnVal2 = ydStkLyrDao.getYdStklyr(tmpPara, outRecSet, 25);
			
			// 0일 경우 저장위치에 재료번호 없음
			//if(outRecSet.getRecord(0).getFieldInt("STL_NO") == 0) {
			if(intRtnVal2 == 0) {
				
				// 2) 현재위치의 재료번호를 Null로  수정 --------------------------------------------------------
				
				updPara1.setField("STL_NO", "");
				updPara1.setField("YD_STK_COL_GP", recPara.getFieldString("STK_POS").substring(0, 7));
				updPara1.setField("YD_STK_BED_NO", recPara.getFieldString("STK_POS").substring(6, 8));
				updPara1.setField("YD_STK_LYR_NO", recPara.getFieldString("STK_LYR"));
				updPara1.setField("YD_STK_LYR_MTL_STAT", "E");
				
		        intRtnVal = ydStkLyrDao.updYdStklyr(updPara1, 0); 
		        ydUtils.putLog(szSessionName, szMethodName, updQuery1, YdConstant.DEBUG);
		        
				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						
					}
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} // end of if
				
				// 3) 수정위치의 재료번호를 등록(UPDATE) ----------------------------------------------------------------
				updPara2.setField("STL_NO",        recPara.getFieldString("STL_NO"));
				updPara2.setField("YD_STK_COL_GP", recPara.getFieldString("UPD_POS").substring(0, 7));
				updPara2.setField("YD_STK_BED_NO", recPara.getFieldString("UPD_POS").substring(6, 8));
				updPara2.setField("YD_STK_LYR_NO", recPara.getFieldString("UPD_LYR"));
				updPara2.setField("YD_STK_LYR_MTL_STAT", "C");
				
		        intRtnVal = ydStkLyrDao.updYdStklyr(updPara2, 0);
		        
		        ydUtils.putLog(szSessionName, szMethodName, updQuery2, YdConstant.DEBUG);
		        
		        
		        // 4) 코일 공통 테이블의 저장위치 수정(UPDATE) ----------------------------------------------------------------
		        // 코일 공통 테이블을 먼저 조회 후 정보가 있을 경우에만 수정
		        JDTORecord    paraRec      = JDTORecordFactory.getInstance().create(); // 조회용도
		        JDTORecord    coilComRec   = JDTORecordFactory.getInstance().create();
		        JDTORecordSet coilComRslt  = JDTORecordFactory.getInstance().createRecordSet("YD");
		        
		        paraRec.setField("COIL_NO",         updPara2.getFieldString("STL_NO"));
		        
		        //코일 공통 조회
		        /* com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCOILCOMM */
		        intRtnVal = ydStockDao.getYdStock(paraRec, coilComRslt, 8);
		        
		        System.out.println("코일공통 조회 결과 : " + intRtnVal);
		        
		        System.out.println("COIL_NO : " + updPara2.getFieldString("STL_NO"));
		        
				//에러처리 추가
				//공통정보 없을경우는 처리하지않는다.
				//확인하고 이부분없을시 에러처리 추가 필요 
				
				if (intRtnVal >0 )
				{
					coilComRslt.first();
					coilComRec = coilComRslt.getRecord();
			 
					
					//변경 전 코일 공통 피수 값 출력
					System.out.println("@@@@@@코일공통 조회 결과COIL_NO : " + yddatautil.setDataDefault(coilComRec.getField("COIL_NO"), ""));
					System.out.println("@@@@@@코일공통 조회 결과SKINPASS_YN : " + yddatautil.setDataDefault(coilComRec.getField("SKINPASS_YN"), ""));
					System.out.println("@@@@@@코일공통 조회 결과NEXT_PROC : " + yddatautil.setDataDefault(coilComRec.getField("NEXT_PROC"), ""));
					System.out.println("@@@@@@코일공통 조회 결과MID_INSPECT_DEFECT_CD1 : " + yddatautil.setDataDefault(coilComRec.getField("MID_INSPECT_DEFECT_CD1"), ""));
					System.out.println("@@@@@@코일공통 조회 결과MID_INSPECT_DEFECT_CD2 : " + yddatautil.setDataDefault(coilComRec.getField("MID_INSPECT_DEFECT_CD2"), ""));
					System.out.println("@@@@@@코일공통 조회 결과MID_INSPECT_DEFECT_CD3 : " + yddatautil.setDataDefault(coilComRec.getField("MID_INSPECT_DEFECT_CD3"), ""));
					System.out.println("@@@@@@코일공통 조회 결과MID_INSPECT_DEFECT_CD4 : " + yddatautil.setDataDefault(coilComRec.getField("MID_INSPECT_DEFECT_CD4"), ""));
					System.out.println("@@@@@@코일공통 조회 결과MID_INSPECT_DEFECT_CD5 : " + yddatautil.setDataDefault(coilComRec.getField("MID_INSPECT_DEFECT_CD5"), ""));
					
					
					
					JDTORecord coilComPara  = JDTORecordFactory.getInstance().create();
					
			        coilComPara.setField("YD_GP",           updPara2.getFieldString("YD_STK_COL_GP").substring(0, 1));
			        coilComPara.setField("YD_BAY_GP",       updPara2.getFieldString("YD_STK_COL_GP").substring(1, 2));
			        coilComPara.setField("YD_EQP_GP",       updPara2.getFieldString("YD_STK_COL_GP").substring(2, 4));
			        coilComPara.setField("YD_STK_COL_NO",   updPara2.getFieldString("YD_STK_COL_GP").substring(4, 6));
			        coilComPara.setField("YD_STK_BED_NO",   updPara2.getFieldString("YD_STK_BED_NO"));
			        coilComPara.setField("YD_STK_LYR_NO",   updPara2.getFieldString("YD_STK_LYR_NO"));
			        coilComPara.setField("YD_STR_LOC",      updPara2.getFieldString("YD_STK_COL_GP")+ updPara2.getFieldString("YD_STK_BED_NO"));
			        //coilComPara.setField("YD_STR_LOC_HIS1", coilComRec.getFieldString("YD_STR_LOC"));
			        //coilComPara.setField("YD_STR_LOC_HIS2", coilComRec.getFieldString("YD_STR_LOC_HIS1"));
			        coilComPara.setField("FNL_REG_PGM",     "updCoilYdStkPosInfo" );
			        coilComPara.setField("MODIFIER",        yddatautil.setDataDefault( inDto[0].getField("MODIFIER"),"YD"));
					
			        coilComPara.setField("COIL_NO",         updPara2.getFieldString("STL_NO"));
		    
			        /* com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updPtCoilcommLOC */
			        
			        intRtnVal = ydStockDao.updPtComm_LOC(coilComPara, 3);
			        
			        if (intRtnVal < 0) {
						if (intRtnVal == -1) {
							szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
						} else {
							szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
							
						}
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} // end of if
				}
			}else {
				szMsg = "수정위치에 재료번호가 등록되어 있습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}	// end of updCoilYdStkPosInfo
	
	/**
	 * 저장위치 삭제(현재위치의 재료번호를 NULL로 수정)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void delCoilYdStkPosInfo(JDTORecord[] inDto) throws DAOException {

		int           intRtnVal     = 0;
		String        szMsg         = "";
		String        szMethodName  = "delCoilYdStkPosInfo";
		JDTORecord    recPara       = JDTORecordFactory.getInstance().create();		
		YdStkLyrDao ydStkLyrDao     = new YdStkLyrDao();		
		YdStockDao  ydStockDao  = new YdStockDao();
		try {
			recPara.setField("YD_STK_POS",     yddatautil.setDataDefault(inDto[0].getField("YD_STK_POS"),     ""));
			recPara.setField("YD_STK_LYR_NO",  yddatautil.setDataDefault(inDto[0].getField("YD_STK_LYR_NO"),  ""));
			recPara.setField("YD_STK_COL_GP",  recPara.getFieldString("YD_STK_POS").substring(0, 6));
			recPara.setField("YD_STK_BED_NO",  recPara.getFieldString("YD_STK_POS").substring(6));
			recPara.setField("STL_NO",         "");
			recPara.setField("YD_STK_LYR_MTL_STAT",         "E");
	        
	        intRtnVal = ydStkLyrDao.updYdStklyr(recPara,0);
	        
			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
				}
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			} // end of if
			
			
			
			//코일공통을 초기화 
			JDTORecord coilComPara  = JDTORecordFactory.getInstance().create();
			
	        coilComPara.setField("YD_GP",           "X");
	        coilComPara.setField("YD_BAY_GP",       "H");
	        coilComPara.setField("YD_EQP_GP",       "01");
	        coilComPara.setField("YD_STK_COL_NO",   "01");
	        coilComPara.setField("YD_STK_BED_NO",   "01");
	        coilComPara.setField("YD_STK_LYR_NO",   "001");
	        coilComPara.setField("YD_STR_LOC",      "XH01010101");
	        coilComPara.setField("FNL_REG_PGM",     "delCoilYdStkPosInfo" );
	        coilComPara.setField("MODIFIER",        yddatautil.setDataDefault( inDto[0].getField("MODIFIER"),"YD"));			
	        coilComPara.setField("COIL_NO",         inDto[0].getField("STL_NO"));
    
	        /* com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updPtCoilcommLOC */	        
	        intRtnVal = ydStockDao.updPtComm_LOC(coilComPara, 3);
	        
	        if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					
				}
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			} // end of if
	        
	        
	        
			//L2저장품재원 정보 송신
			//======================================================
			// 저장품제원 : 코일야드L2로 송신(YDY5L002)
			//======================================================
			JDTORecord recResult = null;
			recResult = JDTORecordFactory.getInstance().create();
			recResult.setField("MSG_ID"         , "YDY5L002");
			recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
			recResult.setField("STL_NO"         , inDto[0].getField("STL_NO"));
			recResult.setField("YD_STK_COL_GP"  , "");
			recResult.setField("YD_STK_BED_NO"  , "");
			
			ydDelegate.sendMsg(recResult);

			szMsg = "코일야드L2로 응답전문 [YDY5L002] 전송완료";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}	// end of delCoilYdStkPosInfo
	
	/**
	 * 콘베어정보 등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void insCoilYdConveyorMgt(JDTORecord[] inDto) throws DAOException {
		int intRtnVal = 0;
		
		String szMsg        = "";
		String szMethodName = "insCoilYdConveyorMgt";

		String       szydEqpId    = null;
		JDTORecord   recPara      = JDTORecordFactory.getInstance().create();		
		YdCrnSpecDao ydCrnspecDao = new YdCrnSpecDao();
		YdEqpDao 	 ydEqpDao     = new YdEqpDao();

		try {
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
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);	
				
				//전달 인자 LOG출력 (DEBUG 용) 
				szMsg = recPara.toString();
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
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
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}	// end of insCoilYdConveyorMgt	
	
	/**
	 * 콘베어정보 삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String  delCoilYdConveyorMgt(JDTORecord[] inDto) throws DAOException {
	
		int        intRtnVal    = 0;
		String     szMsg        = "";
		String     szMethodName = "delCoilYdConveyorMgt";		
		String	   rtnMsg = YdConstant.RETN_CD_SUCCESS;
		String	   rcvMsg = YdConstant.RETN_CD_SUCCESS;
		JDTORecord recPara      = JDTORecordFactory.getInstance().create();		
		JDTORecordSet rsTemp  = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		YdStockDao ydStockDao = new YdStockDao();
		YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		
		String szOperationName = "콘베어정보 삭제";
		EJBConnector ejbConn = null;
		
		
		
		
		try {
			
			for(int x=0;x<inDto.length;x++){
				
				
				//초기화
				rsTemp  = JDTORecordFactory.getInstance().createRecordSet("YD");
				recPara      = JDTORecordFactory.getInstance().create();
				
				//---------------------------------------------------------------------------------
				// 1. 해당 재료로 작업스케줄이 편성되어있는지 확인
				//	1.1 존재시 작업 취소로 연결하여 스케줄 및 작업예약 삭제 함.
				// 2. 해당 재료로 작업예약이 편성되어있는지 확인
				//  2.1 존재시 작업예약 삭제
				// 3. 해당 위치 정보 삭제
				//---------------------------------------------------------------------------------

				
				// 1. 스케줄 존재여부 체크 
				recPara.setField("STL_NO", ydDaoUtils.paraRecChkNull(inDto[x], "STL_NO"));
				intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, rsTemp, 52);
				
				if(intRtnVal < 0 ){
					szMsg = "[JSP Session : "+szOperationName + "] 크레인 스케줄 존재 여부 조회시 ERROR ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					//DAO Exceptiopn
					
				} else if(intRtnVal == 0 ){
					// PASS
					
				} else {
					
					 

					rsTemp.first();
					for(int i = 0 ; i <rsTemp.size(); i++ ){
						
						recPara      = JDTORecordFactory.getInstance().create();
						recPara = rsTemp.getRecord(i);

						//해당 스케줄만 삭제하고자 하는경우 
						recPara.setField("CANCLE_GP", "1");
						
						ejbConn = new EJBConnector("default", this);				
						rcvMsg = (String)ejbConn.trx("SlabJspSeEJB", "procSchCancle", recPara);
					}
					
					
				}
				
				
				szMsg = "[JSP Session : "+szOperationName + "] 크레인 스케줄 존재 여부 체크 END ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				
				//---------------------------------------------------------------------------------
				//---------------------------------------------------------------------------------
				
				//2. 작업예약 존재여부 체크
				rsTemp  = JDTORecordFactory.getInstance().createRecordSet("YD");
				recPara      = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", ydDaoUtils.paraRecChkNull(inDto[x], "STL_NO"));

				
				intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recPara, rsTemp, 2);
				
				if(intRtnVal < 0 ){
					szMsg = "[JSP Session : "+szOperationName + "] 작업예약 정보 존재 조회시 ERROR ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					//DAO Exceptiopn
					
				} else if(intRtnVal == 0 ){
					// PASS
					
				} else {

					rsTemp.first();
					for(int i = 0 ; i <rsTemp.size(); i++ ){
						
						recPara      = JDTORecordFactory.getInstance().create();
						recPara = rsTemp.getRecord(i);
						
						recPara.setField("YD_USER_ID",inDto[x].getField("YD_USER_ID"));
						
						
						ejbConn = new EJBConnector("default", this);				
						rcvMsg = (String)ejbConn.trx("SlabJspSeEJB", "delYdWrkbook", recPara);
						
					}
				}
				
				szMsg = "[JSP Session : "+szOperationName + "] 작업예약 정보 존재여부 체크 END ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				
				//---------------------------------------------------------------------------------
				
				
				
				//---------------------------------------------------------------------------------
				//3. 해당위치 정보 삭제
				//---------------------------------------------------------------------------------
				recPara      = JDTORecordFactory.getInstance().create();		
				recPara.setField("STL_NO",         "");
				recPara.setField("YD_STK_LYR_NO",  inDto[x].getField("YD_STK_LYR_NO"));
				recPara.setField("YD_STK_COL_GP",  inDto[x].getField("YD_STK_COL_GP"));
				recPara.setField("YD_STK_BED_NO",  inDto[x].getField("YD_STK_BED_NO"));
				recPara.setField("YD_STK_LYR_MTL_STAT", "E");
				recPara.setField("MODIFIER",    inDto[x].getField("YD_USER_ID"));
				
				intRtnVal = ydStkLyrDao.updYdStklyr(recPara, 0);
			    
				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					}
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} // end of if
				
				
				
				szMsg = "[JSP Session : "+szOperationName + "] 해당위치 정보 삭제 END ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//---------------------------------------------------------------------------------
				//4. 공통 저장위치 삭제
				//---------------------------------------------------------------------------------
				
				recPara      = JDTORecordFactory.getInstance().create();	
				recPara.setField("YD_GP",    "");
				recPara.setField("YD_BAY_GP",    "");
				recPara.setField("YD_EQP_GP",    "");
				recPara.setField("YD_STK_COL_NO",    "");
				recPara.setField("YD_STK_BED_NO",    "");
				recPara.setField("YD_STK_LYR_NO",    "");
				recPara.setField("YD_STR_LOC",    "");
				recPara.setField("MODIFIER",    inDto[x].getField("YD_USER_ID"));
				recPara.setField("FNL_REG_PGM",   szMethodName);
				recPara.setField("COIL_NO",    inDto[x].getField("STL_NO"));
				intRtnVal = ydStockDao.updPtComm_LOC(recPara, 3);

				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					}
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} // end of if
			
				szMsg = "[JSP Session : "+szOperationName + "] 공통 저장위치 삭제 END ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			return rtnMsg;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}	// end of delCoilYdConveyorMgt
	
	
	/**
	 * 야드 동 정보 설정(수정)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updCoilYdBaySetInfo(JDTORecord[] inDto) throws DAOException {

		int           intRtnVal     = 0;
		String        szMsg         = "";
		String        szMethodName  = "updCoilYdBaySetInfo";
		JDTORecord    recPara       = JDTORecordFactory.getInstance().create();		
		YdStkColDao   ydStkColDao   = new YdStkColDao();
		try {

			for(int x=0;x<inDto.length;x++){
				recPara.setField("YD_STK_COL_GP",       yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_GP"), ""));
				recPara.setField("YD_GP",               yddatautil.setDataDefault(inDto[x].getField("YD_GP"), ""));
				recPara.setField("YD_BAY_GP",           yddatautil.setDataDefault(inDto[x].getField("YD_BAY_GP"), ""));
				recPara.setField("YD_EQP_GP",           yddatautil.setDataDefault(inDto[x].getField("YD_EQP_GP"), ""));
				recPara.setField("YD_STK_COL_NO",       yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_NO"), ""));
				recPara.setField("YD_STK_COL_ACT_STAT", yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_ACT_STAT"), ""));
				recPara.setField("YD_STK_COL_L",        yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_L"), ""));
				recPara.setField("YD_STK_COL_W",        yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_W"), ""));
				
				intRtnVal = ydStkColDao.updYdStkcol(recPara,0);
			    
				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					}
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} // end of if
			}
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}	// end of updCoilYdBaySetInfo

	/**
	 * 야드 동 정보 삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void delCoilYdBaySetInfo(JDTORecord[] inDto) throws DAOException {
		int           intRtnVal     = 0;
		String        szMsg         = "";
		String        szMethodName  = "delCoilYdBaySetInfo";
		JDTORecord    recPara       = JDTORecordFactory.getInstance().create();		
		YdStkColDao   ydStkColDao   = new YdStkColDao();
		
		try {
			for(int x=0;x<inDto.length;x++){			
				recPara.setField("YD_GP",               yddatautil.setDataDefault(inDto[x].getField("YD_GP"), ""));
				recPara.setField("YD_BAY_GP",           yddatautil.setDataDefault(inDto[x].getField("YD_BAY_GP"), ""));
				recPara.setField("YD_EQP_GP",           yddatautil.setDataDefault(inDto[x].getField("YD_EQP_GP"), ""));
				recPara.setField("YD_STK_COL_NO",       yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_NO"), ""));
				recPara.setField("DEL_YN",              "Y");
				
				intRtnVal = ydStkColDao.updYdStkcol(recPara,0);
				
				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					}
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} // end of if
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}	// end of delCoilYdBaySetInfo
	
	/**
	 * 야드 동 정보 등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void insCoilYdBaySetInfo(JDTORecord[] inDto) throws DAOException {

		int           intRtnVal     = 0;
		String        szMsg         = "";
		String        szMethodName  = "insCoilYdBaySetInfo";
		JDTORecord    recPara       = JDTORecordFactory.getInstance().create();		
		YdStkColDao   ydStkColDao   = new YdStkColDao();
		
		String        updQuery1     = ""; // 현재위치의 재료번호를 NULL로 수정하는 쿼리
			
		try {
			for(int x=0;x<inDto.length;x++){
				recPara.setField("YD_GP",               yddatautil.setDataDefault(inDto[x].getField("YD_GP"), ""));
				recPara.setField("YD_BAY_GP",           yddatautil.setDataDefault(inDto[x].getField("YD_BAY_GP"), ""));
				recPara.setField("YD_EQP_GP",           yddatautil.setDataDefault(inDto[x].getField("YD_EQP_GP"), ""));
				recPara.setField("YD_STK_COL_NO",       yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_NO"), ""));
				recPara.setField("YD_STK_COL_ACT_STAT", yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_ACT_STAT"), ""));
				recPara.setField("YD_STK_COL_L",        yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_L"), ""));
				recPara.setField("YD_STK_COL_W",        yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_W"), ""));
				recPara.setField("YD_STK_COL_GP",       recPara.getFieldString("YD_GP") + recPara.getFieldString("YD_BAY_GP")
						                              + recPara.getFieldString("YD_EQP_GP") + recPara.getFieldString("YD_STK_COL_NO"));
				
				intRtnVal = ydStkColDao.updYdStkcol(recPara,0);
				
				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
						
					}
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} // end of if
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}	// end of insCoilYdBaySetInfo
	
	
	/**
	 * 콘베어정보 수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updCoilYdConveyorMgt(JDTORecord[] inDto) throws DAOException {

		int           intRtnVal     = 0;
		String        szMsg         = "";
		String        szMethodName  = "updCoilYdConveyorMgt";
		JDTORecord    recPara       = JDTORecordFactory.getInstance().create();		
		YdStkLyrDao   ydStkLyrDao   = new YdStkLyrDao();
		try {
			for(int x=0;x<inDto.length;x++){
				/*
				recPara.setField("YD_STK_POS",           yddatautil.setDataDefault(inDto[x].getField("YD_STK_POS"), ""));
				recPara.setField("STL_NO",               yddatautil.setDataDefault(inDto[x].getField("STL_NO"), ""));
				recPara.setField("YD_STK_LYR_NO",        yddatautil.setDataDefault(inDto[x].getField("YD_STK_LYR_NO"), ""));
				recPara.setField("YD_STK_COL_GP",        recPara.getFieldString("YD_STK_POS").substring(0, 5));
				recPara.setField("YD_STK_BED_NO",        recPara.getFieldString("YD_STK_POS").substring(6));
				*/
				
				recPara.setField("YD_STK_COL_GP",        yddatautil.setDataDefault(inDto[x].getField("YD_STK_COL_GP"), ""));
				recPara.setField("YD_STK_BED_NO",        yddatautil.setDataDefault(inDto[x].getField("YD_STK_BED_NO"), ""));
				recPara.setField("YD_STK_LYR_NO",        yddatautil.setDataDefault(inDto[x].getField("YD_STK_LYR_NO"), ""));
				recPara.setField("STL_NO",               yddatautil.setDataDefault(inDto[x].getField("STL_NO"), ""));
				recPara.setField("YD_STK_LYR_ACT_STAT",  "C");
				
				intRtnVal = ydStkLyrDao.updYdStklyr(recPara,0);
				
				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					}
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} // end of if
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}	// end of updCoilYdConveyorMgt
	

//	/**
//	 * 반납 크레인 수정 
//	 *
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param inDto
//	 * @return
//	 * @throws DAOException
//	 */
//	public void updCoilYdRetCrnReg(JDTORecord[] inDto) throws DAOException {
//		int        intRtnVal    = 0;
//		String     szMsg        = "";
//		String     szMethodName = "updCoilYdRetCrnReg";
//		String szOperationName = "반납 크레인 수정";
//			
//		YdStockDao ydStockDao   = new YdStockDao();
//		try {
//			//저장위치 좌표설정화면 BED 수정
//			for(int x=0;x<inDto.length;x++){
//				//설비 ID 및 수정항목 세팅 
//				JDTORecord recPara      = JDTORecordFactory.getInstance().create();	
//				recPara.setField("STL_NO",          yddatautil.setDataDefault(inDto[x].getField("STL_NO"),       ""));
//				recPara.setField("PLNT_PROC_CD",    yddatautil.setDataDefault(inDto[x].getField("PLNT_PROC_CD"), ""));
//				recPara.setField("YD_GP",      		"H");
//				recPara.setField("YD_AIM_YD_GP",    "H");
//				recPara.setField("MODIFIER",      	yddatautil.setDataDefault(inDto[x].getField("SZUSERID"),     ""));
//				
//				ydUtils.displayRecord(szOperationName, recPara);
//				
//				//크레인 SPEC UPDATE
//				intRtnVal = ydStockDao.updYdStock(recPara, 0);
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
//	}	// end of updCoilYdRetCrnReg
//	
	
	/**
	 * 코일야드 크레인작업범위등록 조회 (설비 ID 를 받는경우)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdCrnStsSetById(JDTORecord inDto) throws DAOException {
		int    intRtnVal    = 0;
		String szMsg        = "";
		String szMethodName = "getCoilYdCrnStsSetById";

		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		YdEqpDao ydEqpDao = new YdEqpDao();		
		try {
				//설비ID 
				recPara.setField("YD_EQP_ID",yddatautil.setDataDefault(inDto.getField("YD_EQP_ID"), ""));
				intRtnVal = ydEqpDao.getYdEqp(recPara, outRecSet, 6);
				
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		return outRecSet;
	}	// end of getCoilYdCrnStsSetById
	
	/**
	 * 코일야드 대차 이동실적 BACKUP 처리 (출발지시)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updCoilYdCrnStsSetTcarOrd(JDTORecord[] inDto) throws DAOException {
		String     szMsg         = "이동실적 BACKUP 처리";
		String     szMethodName  = "updCoilYdCrnStsSetTcarOrd";
		String szOperationName = "";
		JDTORecord recPara       = null;
		JDTORecord recPara2      = null;
		YdDelegate ydDelegate    = new YdDelegate();
		String 	   ydEqpWrkStat  = "";
		int        intRtnVal     = 0;
		String     wrkBook1      = "";
		String     wrkBook2      = "";
		YdWrkbookDao ydWrkBookDao = new YdWrkbookDao();
		JDTORecordSet tmpRecSet = JDTORecordFactory.getInstance().createRecordSet("yd");
		try {
			//저장위치 좌표설정화면 BED 수정
			for(int x=0;x<inDto.length;x++){							
								
				recPara  = JDTORecordFactory.getInstance().create();
				recPara2 = JDTORecordFactory.getInstance().create();
				// 이동실적 BACKUP 처리 
				// TC CODE
				// YDYDJ620
				recPara.setField("JMS_TC_CD","YDC3L006");				
				recPara.setField("YD_GP",           yddatautil.setDataDefault(inDto[x].getField("YD_GP"), ""));
				recPara.setField("YD_TCAR_SCH_ID",  yddatautil.setDataDefault(inDto[x].getField("YD_TCAR_SCH_ID"), ""));
				recPara.setField("YD_EQP_WRK_STAT", yddatautil.setDataDefault(inDto[x].getField("YD_EQP_WRK_STAT"), ""));
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
				// 작업 스케줄 코드 조회 끝 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ 
				
				// 2) 출발지시 처리 
				
				//Delegate
				//코일야드 대차 이동실적 BACKUP 전송  
				//ydDelegate.sendMsg(recPara);
				ydDelegate.sendMsg(recPara);
				
				ydUtils.displayRecord(szOperationName, recPara);
			     
			}			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}	// end of updCoilYdCrnStsSetTcarOrd
	
	/**
	 * 코일 야드 대차 이동실적 BACKUP 처리 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updCoilYdCrnStsSetTcarMove(JDTORecord[] inDto) throws DAOException {
		String szMsg = "";
		String szMethodName = "updCoilYdCrnStsSetTcarMove";
		String szOperationName = "대차 이동실적 BACKUP 처리";
		JDTORecord recPara = null;
		YdDelegate ydDelegate = new YdDelegate();
		
		try {
			//저장위치 좌표설정화면 BED 수정
			for(int x=0;x<inDto.length;x++){							
								
				recPara = JDTORecordFactory.getInstance().create();
				
				// 이동실적 BACKUP 처리 
				// TC CODE
				// YDYDJ620
				recPara.setField("JMS_TC_CD",  "YDYDJ620");				
				recPara.setField("YD_EQP_ID",  yddatautil.setDataDefault(inDto[x].getField("YD_EQP_ID"), ""));
				recPara.setField("YD_BAY_GP",  yddatautil.setDataDefault(inDto[x].getField("YD_CURR_BAY_GP"), ""));
				recPara.setField("YD_MOVE_GP", yddatautil.setDataDefault(inDto[x].getField("YD_MOVE_GP"), ""));
				
				//Delegate
				//슬라브야드 대차 이동실적 BACKUP 전송  
				
				ydUtils.displayRecord(szOperationName, recPara);
				//ydDelegate.sendMsg(recPara);
				ydDelegate.sendMsg(recPara);
			     
			}			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}	// end of updCoilYdCrnStsSetTcarMove
	
	/**
	 *  야드크레인 작업관리 (스케줄 취소)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void cancleSchCoilYdCrnWorkMgt(JDTORecord[] inDto) throws DAOException {
		String szMsg        = "";	
		String szMethodName = "cancleSchCoilYdCrnWorkMgt";

		JDTORecord recPara = JDTORecordFactory.getInstance().create();
	
		YdDelegate ydDelegate = new YdDelegate();
		try {
			String szLogMsg = "JSP-SESSION [야드크레인 작업관리 - 스케줄 취소]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			for(int x=0;x<inDto.length;x++){			
				
				//TC CODE
				//현재 구현된 테스트용 내부전문 YDYD9004
				recPara.setField("JMS_TC_CD","YDYD9004");
				
				//스케줄 ID
				recPara.setField("YD_CRN_SCH_ID",yddatautil.setDataDefault(inDto[x].getField("YD_CRN_SCH_ID"), ""));

				//스케줄 CODE
				recPara.setField("YD_SCH_CD", yddatautil.setDataDefault(inDto[x].getField("YD_SCH_CD"), ""));
				
				//삭제유무 
				recPara.setField("DEL_YN",  "Y");
				
				//수정자 
				recPara.setField("MODIFIER",  "JSPUSER");	
				
				
				//전문 EJB 쪽  작업이 완료 될 경우 TC_CODE수정하고  QSender

				//ydDelegate.sendMsg(recPara);
				
				EJBConnector ejbConn = null;
							
				ejbConn = new EJBConnector("default", this);
				ejbConn.trx("CoilJspSeEJB", "schCncl", recPara);
			
			}
			szLogMsg = "JSP-SESSION [야드크레인 작업관리 - 스케줄 취소] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		
		
		}
	}	// end of cancleSchCoilYdCrnWorkMgt
	
	/**
	 * 코일 제품상세정보 조회/검색하는 메소드이다.코일 공통  항목을 조회한다.(검색어, 업무영역코드)
	 *
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
	}

	/**
	 *  차량별 상세 작업관리 조회(조회)
	 *
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
			
			recPara.setField("CAR_NO",yddatautil.setDataDefault(inDto.getField("CAR_NO"), ""));
			recPara.setField("YD_CARLD_STOP_LOC",yddatautil.setDataDefault(inDto.getField("YD_GP"), ""));
			recPara.setField("YD_CARUD_STOP_LOC",yddatautil.setDataDefault(inDto.getField("YD_GP"), ""));
			recPara.setField("PAGE_CNT1",inDto.getField("PAGE_NO"));
			recPara.setField("PAGE_CNT2",inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT1",inDto.getField("ROWCOUNT"));
			recPara.setField("ROW_CNT2",inDto.getField("ROWCOUNT"));
			
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
				
				if(!out_plant.equals("")){
					
					out_plant = out_plant.substring(0,1);
					
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
	}
	
	
	/**
	 * 대차작업관리 검색 하는 메소드이다.코일 공통  항목을 조회한다.(검색어, 업무영역코드)
	 *
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
	}
	
	
	/**
	 * 대차작업관리의 대차제품상세 조회/검색하는 메소드이다.코일 공통  항목을 조회한다.(검색어, 업무영역코드)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getTcarWorkMtlList(JDTORecord inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg="";
		String szMethodName="getYdStock";		
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
	}
	
	/**
	 * 코일야드 재료상세정보 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdMtlDtl(JDTORecord inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg  = "";
		String szMethodName="getCoilYdMtlDtl";
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		try {
			
			recPara.setField("STL_NO", 	inDto.getField("STL_NO"));
			
			YdStockDao ydStockDao = new YdStockDao();
			
			intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 60);
			
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
						
		
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);			
		
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}
	
	/**
	 * 대차 상차정보 조회 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getYdTcarLiftFtmvMtl(JDTORecord inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg  = "";
		String szMethodName="getCoilYdMtlDtl";
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		try {
			recPara.setField("YD_EQP_ID", 	inDto.getField("YD_EQP_ID"));
			
			YdTcarFtmvMtlDao ydTcarFtmvMtlDao = new YdTcarFtmvMtlDao();

			intRtnVal = ydTcarFtmvMtlDao.getYdTcarftmvmtl(recPara, outRecSet, 3);

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
						
		
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);			
		
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}
	
	/**
	 * 사유별 이적등록의 대차제품상세 조회/검색하는 메소드이다.코일 공통  항목을 조회한다.(검색어, 업무영역코드)
	 *
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
			recPara.setField("YD_COIL_GP",yddatautil.setDataDefault(inDto.getField("YD_COIL_GP"), ""));
			recPara.setField("SELECT_INPUT",yddatautil.setDataDefault(inDto.getField("SELECT_INPUT"), ""));			
			recPara.setField("PAGE_CNT1",inDto.getField("PAGE_NO"));
			recPara.setField("PAGE_CNT2",inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT1",inDto.getField("ROWCOUNT"));
			recPara.setField("ROW_CNT2",inDto.getField("ROWCOUNT"));			
					
			
			//야드 저장품 테이블에서 품목코드를 읽어온다
//PIDEV_S :병행가동용:PI_YD
			recPara.setField("PI_YD",    	"J");			
			intRtnVal = ydStockDao.getYdStock(recPara, rSetStock, 59);
						
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
	}
	
	/**
	 * 입고 Backup처리 조회/검색하는 메소드이다.코일 공통  항목을 조회한다.(검색어, 업무영역코드)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getYdBackupWork(JDTORecord inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg="";
		String szMethodName="getYdStock";		
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecordSet rSetStock = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecord recPara = JDTORecordFactory.getInstance().create();

		YdStockDao ydStockDao = new YdStockDao();
	
		try {
			recPara.setField("YD_GP",yddatautil.setDataDefault(inDto.getField("YD_GP"), ""));
			recPara.setField("PAGE_CNT1",inDto.getField("PAGE_NO"));
			recPara.setField("PAGE_CNT2",inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT1",inDto.getField("ROWCOUNT"));
			recPara.setField("ROW_CNT2",inDto.getField("ROWCOUNT"));			
					
			
			//야드 저장품 테이블에서 품목코드를 읽어온다
			
			intRtnVal = ydStockDao.getYdStock(recPara, rSetStock, 61);
						
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
	}
	
	/**
	 * 야드관리 > 코일소재야드 > 설비/차량관리 > 이송대상재관리   (이송공장별 이송량 조회)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData getCoilYdFrtMoveGpList(GridData inDto) throws DAOException {
		GridData 		rtnGrd 		= new GridData();
		JDTORecordSet   outRecSet  	= null;
		
		CoilJspDAO 		dao 		= new CoilJspDAO();
		JDTORecord 		recPara		= null;
		
		try {
			
			// 파라미터 셋팅 
			recPara		= JDTORecordFactory.getInstance().create(); 			
			recPara.setField("V_YD_GP", 	inDto.getParam("YD_GP").trim());	/*야드구분*/
			recPara.setField("V_YD_BAY_GP", inDto.getParam("YD_BAY_GP"));
			recPara.setField("V_PLANT_GP", 	inDto.getParam("FRTOMOVE_PLANT_GP"));
			recPara.setField("V_STL_NO", 	inDto.getParam("STL_NO"));
			recPara.setField("V_FROM_DATE", inDto.getParam("DATE_FROM"));
			recPara.setField("V_TO_DATE", 	inDto.getParam("DATE_TO"));

			// DAO 호출
			outRecSet = dao.getCoilYdFrtMoveGpList(recPara);
			
			if(outRecSet == null){
				rtnGrd.setMessage("조회된 데이터가 없습니다.");
				rtnGrd.addParam("ret", "-1");
			}else{
				rtnGrd = CmUtil.genGridData(inDto, outRecSet);
				
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return rtnGrd;
	}
	
	/**
	 * 코일저장위치 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdCoilStkPos(JDTORecord inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg  = "";
		String szMethodName="getCoilYdCoilStkPos";
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		try {
			recPara.setField("YD_STK_COL_GP", yddatautil.setDataDefault(inDto.getField("YD_STK_COL_GP"),   ""));
			YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 52);

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
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);			
		
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}
	
	/**
	 * 코일저장위치 조회(크레인작성지시 화면)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdCoilStkPos2(JDTORecord inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg  = "";
		String szMethodName="getCoilYdCoilStkPos2";
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		try {
			recPara.setField("YD_STK_COL_GP", yddatautil.setDataDefault(inDto.getField("YD_STK_COL_GP"),   ""));
			YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 52);
			
			// 코일저장위치에 재료번호가 없을경우 해당 열의 bed수를 계산한다.
			if(intRtnVal == 0) {
				JDTORecordSet tmpRecSet  = JDTORecordFactory.getInstance().createRecordSet("YD"); //Bed수를 조회하는 RecordSet
				JDTORecord    tmpRec     = JDTORecordFactory.getInstance().create();              //임시 Record
				intRtnVal = ydStkLyrDao.getYdStklyr(recPara, tmpRecSet, 56); //Bed수 조회
				System.out.println("intRtnVal : " + intRtnVal);

				tmpRec.addField("YD_STK_COL_GP",     recPara.getFieldString("YD_STK_COL_GP"));
				tmpRec.addField("YD_STK_BED_NO",     "");
				tmpRec.addField("YD_STK_LYR_NO",     "");
				tmpRec.addField("STL_NO",            "");
				tmpRec.addField("YD_STK_BED_CNT_NO", tmpRecSet.getRecord(0).getFieldString("YD_STK_BED_CNT_NO"));
				
				outRecSet.addRecord(tmpRec);
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
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);			
		
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}
	
	/**
	 * 코일야드 크레인작업지시작성
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updCoilYdCrnWorkCmd(JDTORecord[] inDto) throws DAOException {
		int           intRtnVal     = 0;
		String        szMsg         = "";
		String        szMethodName  = "updCoilYdCrnWorkCmd";
		JDTORecord    recPara       = JDTORecordFactory.getInstance().create();
		YdStkLyrDao   ydStkLyrDao   = new YdStkLyrDao();
		YdStockDao    ydStockDao    = new YdStockDao();
		JDTORecordSet outRecSet     = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		JDTORecord    tmpPara1       = JDTORecordFactory.getInstance().create();
		JDTORecordSet tmpRecSet1     = JDTORecordFactory.getInstance().createRecordSet("YD");
		int           tmpRslt1       = 0;
		JDTORecord    tmpPara2       = JDTORecordFactory.getInstance().create();
		JDTORecordSet tmpRecSet2     = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecordSet tmpRecSet3     = JDTORecordFactory.getInstance().createRecordSet("YD");
		int           tmpRslt2       = 0;
		int           tmpRslt3       = 0;
		try {
			recPara.setField("FROM_YD_STK_COL_GP" ,yddatautil.setDataDefault(inDto[0].getField("FROM_YD_STK_COL_GP"),""));
			recPara.setField("FROM_YD_STK_BED_NO" ,yddatautil.setDataDefault(inDto[0].getField("FROM_YD_STK_BED_NO"),""));
			recPara.setField("TO_YD_STK_COL_GP"   ,yddatautil.setDataDefault(inDto[0].getField("TO_YD_STK_COL_GP")  ,""));
			recPara.setField("TO_YD_STK_BED_NO"   ,yddatautil.setDataDefault(inDto[0].getField("TO_YD_STK_BED_NO")  ,""));
			recPara.setField("STL_NO"             ,yddatautil.setDataDefault(inDto[0].getField("STL_NO")            ,""));
			
			tmpPara1.setField("YD_STK_COL_GP", recPara.getFieldString("TO_YD_STK_COL_GP"));

			
			
			
			
			// 1)Bed번호가 없을 경우 Bed를 구함
			if("".equals(recPara.getFieldString("TO_YD_STK_BED_NO"))) {
				// 1-1) 적치중인 재료가 없는 가장 빠른 번호의 Bed를 구함
				/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdDdArtclStkRef_PIDEV*/
				tmpRslt1 = ydStkLyrDao.getYdStklyr(tmpPara1, tmpRecSet1, 33);
			}
			
			// 2)적치단 구하기
				/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdDdArtclStkRef_PIDEV*/
				tmpRslt2 = ydStkLyrDao.getYdStklyr(tmpPara1, tmpRecSet2, 33);
				String lyrNo = tmpRecSet2.getRecord(0).getFieldString("YD_STK_LYR_NO");
			    // 2-1) "001"단 일 경우 
				if("001".equals(lyrNo)) {
					// 단정보에 "001"입력
					tmpPara1.setField("YD_STK_LYR_NO", lyrNo);
				}
				// 2-2) "002"단 일 경우
				else if("002".equals(lyrNo)) {
					// 2-2-1) 다음 Bed의 "001"단 적치중 체크(베드, 단, 적치상태, 재료번호)
					tmpRslt3 = ydStkLyrDao.getYdStklyr(tmpPara1, tmpRecSet3, 33);
					String stlNo = tmpRecSet3.getRecord(0).getFieldString("STL_NO");
					// 2-2-2) 적치중일 경우 
					   // 단정보에 "002"입력
						if(!"".equals(stlNo)) {
							tmpPara1.setField("YD_STK_LYR_NO", lyrNo);
						}
					// 2-2-3) 미적치중일 경우
					   // 해당 Bed의 "001"입력
						else if("".equals(stlNo)) {
							tmpPara1.setField("YD_STK_BED_NO", tmpRecSet3.getRecord(0).getFieldString("YD_STK_BED_NO"));
							tmpPara1.setField("YD_STK_LYR_NO", tmpRecSet3.getRecord(0).getFieldString("YD_STK_LYR_NO"));
						}
				}
				
				//update 단정보
				
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}	// end of updCoilYdCrnWorkCmd
	
	/**
	 * 코일야드 크레인스케줄 재료
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdCrnWrkMtl(JDTORecord inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg  = "";
		String szMethodName="getCoilYdCrnWrkMtl";
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		try {
			recPara.setField("YD_STK_COL_GP",   yddatautil.setDataDefault(inDto.getField("YD_STK_COL_GP"),   ""));
			
			YdCrnSchDao ydCrnSchDao = new YdCrnSchDao();
			intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, outRecSet, 33);

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
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);			
		
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}
	

	/**
	 * LINE재공 LIST조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdBayInlnList(JDTORecord inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg  = "";
		String szMethodName="getCoilYdBayInlnList";
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		try {
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
			
			YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
//PIDEV_S :병행가동용:PI_YD
			recPara.setField("PI_YD",    	"J");			
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 67);

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
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);			
		
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}
	
	
	/**
	 * LINE재공 LIST조회2
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdBayInlnList2(JDTORecord inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg  = "";
		String szMethodName="getCoilYdBayInlnList2";
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		try {
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
			
			YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
			/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getCoilTotYdNextproctListDetail_PIDEV*/
//PIDEV_S :병행가동용:PI_YD
//			recPara.setField("PI_YD",    	"J");
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 308);

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
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);			
		
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}
	
	/**
	 * 다음공정별 재공현황팝업 (화면:LINE재공LIST)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdBayInlnList_Pp(JDTORecord inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg  = "";
		String szMethodName="getCoilYdBayInlnList_Pp";
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		try {
			szMsg = "[Jsp Facade : "+szMethodName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
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
			
			YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 305);

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
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);			
		
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		szMsg = "[Jsp Facade : "+szMethodName+"] 메소드 끝 ";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		return outRecSet;
	}
	
	/**
	 * LINE별 재공상세현황 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdBayInlnDtlList(JDTORecord inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg  = "";
		String szMethodName="getCoilYdBayInlnDtlList";
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		try {
			recPara.setField("YD_GP", yddatautil.setDataDefault(inDto.getField("YD_GP"),   ""));
			
			YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 66);

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
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);			
		
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}
	
	/**
	 * 여재보유현황 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdRmnPoss(JDTORecord inDto) throws DAOException {
		int intRtnVal = 0;
		String szMsg  = "";
		String szMethodName="getCoilYdRmnPoss";
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		try {
			recPara.setField("YD_GP", yddatautil.setDataDefault(inDto.getField("YD_GP"),   ""));
			
			YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 52);

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
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);			
		
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}
	
	/**
	 * 코일야드 여재보유현황 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdRmnPossList(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara    = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet  = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getCoilYdRmnPossList";

		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		
		int intRtnVal = 0;
		
		try {
			recPara.setField("YD_GP",    yddatautil.setDataDefault(inDto.getField("YD_GP"),     ""));
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 65);
			
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
			
			logger.println(LogLevel.DEBUG_TEXT, "getCoilYdRmnPossList");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilYdRmnPossList
	
	
	/**
	 * 코일야드 분기 CONV 대상 재료 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getDivConvTargetMtl_Popup(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara    = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet  = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getDivConvTargetMtl_Popup";

		
		YdStockDao ydStockDao = new YdStockDao();
		
		int intRtnVal = 0;
		 
		try {
			recPara.setField("NEXT_PROC",    yddatautil.setDataDefault(inDto.getField("NEXT_PROC"),     ""));
			recPara.setField("PAGE_CNT",     yddatautil.setDataDefault(inDto.getField("PAGE_NO"),      ""));
			recPara.setField("ROW_CNT",      yddatautil.setDataDefault(inDto.getField("ROWCOUNT"),     ""));
			
			
			
			
			intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 119);
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
		return outRecSet;
	}//end of getDivConvTargetMtl_Popup
	
	
	/**
	 * 코일야드 분기 CONV 대상 재료 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getDivConvTargetMtl_NextProc(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara    = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet  = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getDivConvTargetMtl_NextProc";

		
		YdStockDao ydStockDao = new YdStockDao();
		
		int intRtnVal = 0;
		
		try {
			recPara.setField("NEXT_PROC",    yddatautil.setDataDefault(inDto.getField("NEXT_PROC"),     ""));
			recPara.setField("COIL_NO",      yddatautil.setDataDefault(inDto.getField("STL_NO"),     ""));
			
			
			if( !ydDaoUtils.paraRecChkNull(recPara, "COIL_NO").equals("")){
				//코일번호에 입력정보가 존재할경우 다음공정 정보가 들어올수 있는 모든 상황에 대하여 조회조건을 충족시킨다.
				
				recPara.setField("NEXT_PROC", "");
			}
			
			
			
			intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 177);
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
		return outRecSet;
	}//end of getDivConvTargetMtl_NextProc
	
	
	
	/**
	 * Line-Off 분기 Conv
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public String updDivConvLineOff(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara    = JDTORecordFactory.getInstance().create();
		JDTORecord       outRecord1    = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet  = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "updDivConvLineOff";
		String rtnMsg = "";
		
		YdStockDao ydStockDao = new YdStockDao();
		
		int intRtnVal = 0;
		
		try {
			
			recPara.setField("MSG_ID",         "H1YDL001");
			recPara.setField("YD_EQP_ID",      yddatautil.setDataDefault(inDto.getField("YD_EQP_ID"),     ""));
			recPara.setField("STL_NO",         yddatautil.setDataDefault(inDto.getField("STL_NO"),      ""));
			recPara.setField("YD_DSTR_GP",     yddatautil.setDataDefault(inDto.getField("YD_DSTR_GP"),     ""));
			recPara.setField("YD_STK_BED_NO",  yddatautil.setDataDefault(inDto.getField("YD_STK_BED_NO"),     ""));
			
			EJBConnector ejbConn = null;
			
			//추후 로직단에서 리턴해주면 메세지를 뿌려줄수 잇도록 갱신한다 
			//ejbConn = new EJBConnector("default", this);
			ejbConn = new EJBConnector("default", "CoilRcptWrkDmdSeEJB", this);
			
			
			//ejbConn.trx("RcptWrkDmdSeEJB", "procR2MillBrLineOffReq", recPara);			
			outRecord1=(JDTORecord)ejbConn.trx("procR2MillBrLineOffReq", new Class[] { JDTORecord.class }, new Object[] { recPara });
			String sRTN_CD	= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
			String sRTN_MSG		= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
			ydUtils.putLog(szSessionName, szMethodName, "///" + sRTN_MSG, YdConstant.DEBUG);
			if ("0".equals(sRTN_CD)) {
				return sRTN_MSG;
			}	
			
		}
		catch (DAOException e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			szMsg = "updDivConvLineOff DAOException - " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw e;
			
			
		}catch(Exception e) {
			szMsg = "updDivConvLineOff Exception - " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(e.getMessage());
		}
		
		return YdConstant.RETN_CD_SUCCESS;
	}//end of updDivConvLineOff
	
	
	
	
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
		
		JDTORecordSet outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
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
	 * 작업예약 조회(코일소재야드))
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
		String szOperationName = "작업예약 조회(코일소재야드)";
				
		
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
			szDATE_TO		= ydDaoUtils.paraRecChkNull(inDto, "DATE_TO");
				    
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
			
		    //intRtnVal = ydWrkbookDao.getYdWrkbook(recPara, outRecSet, 26);
		    intRtnVal = ydWrkbookDao.getYdWrkbook(recPara, outRecSet, 300);
						
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
	 * 작업예약 재료 조회
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
		String szOperationName = "작업예약 재료 조회(코일소재야드)";
		
		
		int intRtnVal = 0;
		
		try {		
				    
			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			
		    recPara.setField("YD_WBOOK_ID",        ydDaoUtils.paraRecChkNull(inDto, "YD_WBOOK_ID"));
		   
		    
		    szYdGp = ydDaoUtils.paraRecChkNull(inDto, "YD_GP");
		    
		    
		    
		    YdWrkbookMtlDao ydWrkbookmtlDao = new YdWrkbookMtlDao();
		    
		    
//		    if(szYdGp.equals(YdConstant.YD_GP_A_PLATE_SLAB_YARD)  || szYdGp.equals(YdConstant.YD_GP_INTGR_YARD) ||  szYdGp.equals(YdConstant.YD_GP_C_SLAB_YARD)){
//		    	ydWrkbookmtlDao.getYdWrkbookmtl(inDto, outRecSet, 29);
//		    	
//		    }else{
//		    	ydWrkbookmtlDao.getYdWrkbookmtl(inDto, outRecSet, 28);
//		    }
			/*com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookMtlStockByWBookID*/			
		    ydWrkbookmtlDao.getYdWrkbookmtl(inDto, outRecSet, 40);			
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
	 * 코일야드 스케줄 기동
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecord trxRunSchedule(JDTORecord[] inDto) throws DAOException {
		int 			intRtnVal 			= 0;
		String 			szMsg				= "";
		String 			szMethodName 		= "trxRunSchedule";
		String 			szYD_SCH_PRIOR 		= "";
		String 			szOperationName 	= "코일야드 스케줄 기동";
		String 			szYD_GP 			= "";
		String 			szYD_SCH_CD			= null;
		String 			szTC_CD 			= "";
		String 			szCRN_SCH_INS_TYPE	= "";
		String 			szYD_WBOOK_ID 		= "";
		String 			sRTN_MSG 			="";
		String 			szYD_CHK 			="";
		
		JDTORecord 		recPara 			= null;
		EJBConnector 	ejbConn 			= null;
		JDTORecord 		outRecord    		= JDTORecordFactory.getInstance().create();
		JDTORecord 		outRecord1			= JDTORecordFactory.getInstance().create();
		JDTORecord		recPara2			= null;
		
		//DAO 
		
		YdWrkbookDao 	ydWrkbookDao 		= new YdWrkbookDao(); 
		YdWrkbookMtlDao ydWrkbookMtlDao 	= new YdWrkbookMtlDao();
		try {
			
			szMsg = "JSP-SESSION [코일야드 스케줄 기동 ] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			szYD_CHK	=ydDaoUtils.paraRecChkNull(inDto[0], "YD_CHK") ;
			
			//저장위치 좌표설정화면 BED 수정
			for(int x=0;x<inDto.length;x++){
				szYD_GP = inDto[x].getFieldString("YD_GP");
				szYD_SCH_CD  = inDto[x].getFieldString("YD_SCH_CD");
				
				szTC_CD = "YDYDJ509";
				
				inDto[x].setField("JMS_TC_CD", szTC_CD);
				
				szMsg = "JSP-SESSION [코일야드 스케줄 기동 ] 차량하차작업인 지 판단 변수의 값["+szCRN_SCH_INS_TYPE+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				if( !szCRN_SCH_INS_TYPE.equals("")) {
					inDto[x].setField("CRN_SCH_INS_TYPE", szCRN_SCH_INS_TYPE);
					szMsg = "JSP-SESSION [코일야드 스케줄 기동 ] 차량하차작업인 경우";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				}
				ydUtils.displayRecord(szOperationName, inDto[x]);
				
				
				//------------------------------------------------------------------------------
				// 스케줄 기동전 스케줄 우선순위 값이 존재하는경우 우선순위 값을 넣어서 UPDATE 한다
				//------------------------------------------------------------------------------
				
				szYD_SCH_PRIOR = ydDaoUtils.paraRecChkNull(inDto[x], "YD_SCH_PRIOR");
				szYD_WBOOK_ID = ydDaoUtils.paraRecChkNull(inDto[x], "YD_WBOOK_ID");
				
				if(!szYD_SCH_PRIOR.equals("")){
					// 작업 예약 정보 우선순위 변경
					recPara		= JDTORecordFactory.getInstance().create();
					
					recPara.setField("JMS_TC_CD", szTC_CD);
					recPara.setField("YD_SCH_CD", szYD_SCH_CD);
					recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
					recPara.setField("YD_SCH_PRIOR", szYD_SCH_PRIOR);	
					recPara.setField("MODIFIER", ydDaoUtils.paraRecChkNull(inDto[x], "YD_USER_ID"));
					
					
					ydUtils.displayRecord(szOperationName, recPara); 
					
					/*com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.updYdWrkbook*/
					intRtnVal =  ydWrkbookDao.updYdWrkbook(recPara, 0);
					
					
					if(intRtnVal< 0){
						szMsg = "[Jsp Session : "+szOperationName+"] 작업예약 UPDATE 실패!!";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" , "0");	
						outRecord.setField("RTN_MSG", szMsg);	
						m_ctx.setRollbackOnly();
						return outRecord;
						
					}else if(intRtnVal == 0 ){
						
						szMsg = "[Jsp Session : "+szOperationName+"] 작업예약 UPDATE 할 항목이 없습니다!!";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
					}else{
						szMsg = "[Jsp Session : "+szOperationName+"] 작업예약 우선순위 UPDATE 성공!!!";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
						
					}
				
				} else {
					
					recPara		= JDTORecordFactory.getInstance().create();
					
					recPara.setField("JMS_TC_CD", szTC_CD);
					recPara.setField("YD_SCH_CD", szYD_SCH_CD);
					recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
					recPara.setField("YD_SCH_PRIOR", szYD_SCH_PRIOR);	
					recPara.setField("MODIFIER", ydDaoUtils.paraRecChkNull(inDto[x], "YD_USER_ID"));
				}
				
				if(szYD_CHK.equals("")){
					JDTORecord inRec =null;
					String szCurrProgCd = null;
					JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("temRs");
					recPara2         = JDTORecordFactory.getInstance().create();
					recPara2.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
					//getYdWrkbookmtl1				
					intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recPara2, outRecSet, 1);
					
					if(intRtnVal> 0){
						outRecSet.first();
						inRec = JDTORecordFactory.getInstance().create();
						inRec = outRecSet.getRecord();					
						
						szCurrProgCd = ydDaoUtils.paraRecChkNull(inRec, "CURR_PROG_CD");
					}
					
					//대착이적 중 운송대기 인 경우 스케줄 기동여부를 작업자에게 판단 하기 
//					if(szCurrProgCd.equals("L") 
//						&& szYD_SCH_CD.substring(2,4).equals("TC")
//						&& szYD_SCH_CD.substring(0,1).equals("J")
//						){
//						szMsg = "스케줄작업자판단 필요";
//						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
//						
//						outRecord.setField("RTN_CD" 	, "0");	
//						outRecord.setField("RTN_MSG" 	, szMsg);
//						return outRecord;
//					}
				}
				
				{	
					// 대차 상차작업 목적동 체크(동일 목적동 인 경우에만 작업 할 수 있음)//////////////////////////////////////
					if (szYD_SCH_CD.substring(2 , 4).equals("TC") && szYD_SCH_CD.substring(6 , 7).equals("U")) {

						JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("temRs");
						recPara2 = JDTORecordFactory.getInstance().create();
						recPara2.setField("YD_WBOOK_ID" , szYD_WBOOK_ID);

						/* com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookTCcarChk */
						intRtnVal = ydWrkbookDao.getYdWrkbook(recPara2 , outRecSet ,508);
						if (intRtnVal <= 0) {
							szMsg = "목적동이 틀린 대차상차작업예약 입니다.";
							ydUtils.putLog(szSessionName , szMethodName , szMsg , YdConstant.INFO);

							outRecord.setField("RTN_CD" , "0");
							outRecord.setField("RTN_MSG" , szMsg);
							return outRecord;
						}
					}
					////////////////////////////////////////////////////////////////////////////////////////////
					ymCommonDAO dao = ymCommonDAO.getInstance();
					List chkList = null;
					String QueryId 	= "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getMsgYDB999";
					chkList = dao.getCommonList(QueryId, new Object[]{"1"});

				    JDTORecord unloadPointrec = (JDTORecord)chkList.get(0);
			    	String CHK   = StringHelper.evl(unloadPointrec.getFieldString("DEL_YN"), "");	    	
			    	ydUtils.putLog(szSessionName, szMethodName, "◑◑◑◑◑   C열연 대차스케쥴 JMS분리(YDB999):"+CHK, YdConstant.INFO);
								    	
					//스케줄 기동					
			    	if("Y".equals(CHK)){
				    	recPara.setField("JMS_TC_CD","YDYDJ599");
						//jms Send Method
						ydDelegate.sendMsg(recPara);
			    	}else {					
						ejbConn = new EJBConnector("default", "CoilCrnSchSeEJB", this);
						outRecord1 = (JDTORecord)ejbConn.trx("procY5CrnSchMainB", new Class[] { JDTORecord.class },	new Object[] { recPara });
						String sRTN_CD	= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
						sRTN_MSG		= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
						ydUtils.putLog(szSessionName, szMethodName, "///" + sRTN_MSG, YdConstant.DEBUG);
						if ("0".equals(sRTN_CD)) {
							outRecord.setField("RTN_CD" , "0");	
							outRecord.setField("RTN_MSG", sRTN_MSG);	
							//m_ctx.setRollbackOnly();
							return outRecord;
						}
			    	}
				}
				 
			}		
			
			szMsg = "JSP-SESSION [코일야드 스케줄 기동 YDYDJ599] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			outRecord.setField("RTN_CD" 	, "1");	
			outRecord.setField("RTN_MSG" 	, sRTN_MSG);	
			
			ydUtils.putLog(szSessionName, szMethodName, sRTN_MSG, YdConstant.INFO);

			return outRecord;
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
	}	// end of trxRunSchedule
	/**
	 * 코일야드 소재 크레인상태 조회 (설비 ID 를 받는경우)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdCrnStsSetById2(JDTORecord inDto) throws DAOException {
		int intRtnVal      = 0;
		String szRtnMsg = null;
		String szLogMsg = null;
		String szMethodName= "getCoilYdCrnStsSetById2";
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
	}	// end of getCoilYdCrnStsSetById2
	
	/*---------------------------------------------------------------------------------------------------*/
	/*                                     2기작업
	/*---------------------------------------------------------------------------------------------------*/
	
	/**
	 * 일품단위이적등록  
	 * 송정현
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getMtlUnitMvstkReg(JDTORecord inDto) throws DAOException {
		JDTORecord    recPara   = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");

		String szMsg            = "";
		String szMethodName     = "getMtlUnitMvstkReg";
		int intRtnVal = 0;

		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();

		try {
			//적치열 구분과 적치베드 번호로 분리
			recPara.setField("YD_STK_COL_GP",      yddatautil.setDataDefault(inDto.getField("YD_STK_COL_GP"), ""));
			/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdStkLocInfoList_PAGE*/
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 20);
			if (intRtnVal <0) {
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
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
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
			recPara.setField("STL_PROG_CD"		, ydDaoUtils.paraRecChkNull(inDto, "STL_PROG_CD"));
			recPara.setField("NEXT_PROC" 		, ydDaoUtils.paraRecChkNull(inDto, "NEXT_PROC"));
			recPara.setField("YD_GP" 			, ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));
			recPara.setField("YD_BAY_GP" 		, ydDaoUtils.paraRecChkNull(inDto, "YD_BAY_GP"));
			recPara.setField("YD_EQP_GP" 		, ydDaoUtils.paraRecChkNull(inDto, "YD_EQP_GP"));
			recPara.setField("YD_STK_COL_NO" 	, ydDaoUtils.paraRecChkNull(inDto, "YD_STK_COL_NO"));
			recPara.setField("WORD_UNIT_NAME" 	, ydDaoUtils.paraRecChkNull(inDto, "WORD_UNIT_NAME"));
			recPara.setField("PAGE_NO" 			, ydDaoUtils.paraRecChkNull(inDto, "PAGE_NO"));
			recPara.setField("ROW_CNT" 			, ydDaoUtils.paraRecChkNull(inDto, "ROWCOUNT"));
			recPara.setField("YD_COIL_OUTDIA_GRP_GP" , ydDaoUtils.paraRecChkNull(inDto, "YD_COIL_OUTDIA_GRP_GP"));
			
			recPara.setField("YD_LAYER_GP" 		, ydDaoUtils.paraRecChkNull(inDto, "YD_LAYER_GP"));
			recPara.setField("YD_CHK" 			, ydDaoUtils.paraRecChkNull(inDto, "YD_CHK"));
			recPara.setField("USAGE_CD"  		, ydDaoUtils.paraRecChkNull(inDto, "USAGE_CD"));
			recPara.setField("CHK_FROM_FLAG" 	, ydDaoUtils.paraRecChkNull(inDto, "CHK_FROM_FLAG"));
			recPara.setField("CON_CHK_FLAG" 	, ydDaoUtils.paraRecChkNull(inDto, "CON_CHK_FLAG"));
			recPara.setField("PO_GP" 			, ydDaoUtils.paraRecChkNull(inDto, "PO_GP")); 
			recPara.setField("YD_MILLTIME" 			, ydDaoUtils.paraRecChkNull(inDto, "YD_MILLTIME")); 
			
			//야드 저장품 테이블에서 품목코드를 읽어온다
			String yd_chk = ydDaoUtils.paraRecChkNull(inDto, "YD_CHK");
			
			if(yd_chk.equals("") || yd_chk.equals("1")){
			//getYdStock59			
//PIDEV_S :병행가동용:PI_YD
				recPara.setField("PI_YD",    	ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));						
				intRtnVal = ydStockDao.getYdStock(recPara, rSetStock, 59);
			}else if(yd_chk.equals("2")){
//PIDEV_S :병행가동용:PI_YD
				recPara.setField("PI_YD",    	ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));					
				intRtnVal = ydStockDao.getYdStock(recPara, rSetStock, 616);	
			}else if(yd_chk.equals("3")){
				intRtnVal = ydStockDao.getYdStock(recPara, rSetStock, 617);
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
		return rSetStock;
	} // end of getMtlUnitMvstkReg1
	
	
	/**
	 * 메뉴얼 코일 작업지시 편성
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecord updMtlUnitMvstkReg(JDTORecord[] inDto) throws DAOException {
		int intRtnVal 				= 0;
		String szMsg				= null;
		String szMethodName 		= null;
		String szOperationName 		= "메뉴얼 코일 작업지시 편성";
		JDTORecord    recPara   	= JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet 	= JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecordSet rsGetStkLyr 	= null;
		JDTORecord recInTemp      	= null;
		JDTORecord recStkLyr		= null;	
		JDTORecord inRecord     	= null;
		JDTORecord outRecord     	= JDTORecordFactory.getInstance().create();
		JDTORecord outRecord1    	= JDTORecordFactory.getInstance().create();
		String szStkLyrMtlStat 		= null;
		CoilYdToLocDcsnUtil ydToLocDcsnUtil = new CoilYdToLocDcsnUtil();

        String sTAG_STL_NO 		= "";
        String sBED_STL_NO 		= "";
        String sBED1_STL_NO 	= "";
		String sRTN_CD			= "";
		String sRTN_MSG			= "";
		String sRTN_TO_BEDNO	= "XX010101";   

        szMsg        = "";
		szMethodName = "updMtlUnitMvstkReg";
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		try {
			
			//저장위치 좌표설정화면 BED 수정
			for(int x=0;x<inDto.length;x++){
				//TC 미정
				recPara   = JDTORecordFactory.getInstance().create();
				
				//YD_SCH_CD
				//YD_WRK_PLAN_TCAR :계획 대차
				
				sTAG_STL_NO = ydDaoUtils.paraRecChkNull(inDto[x], "STL_NO");
				
				String sTO_YD_STK_COL_GP = inDto[x].getFieldString("TO_YD_STK_COL_GP");
				
				recPara.setField("STL_NO", 			sTAG_STL_NO);
				recPara.setField("STL_NO1",       	sTAG_STL_NO);
				recPara.setField("YD_SCH_CD",     	ydDaoUtils.paraRecChkNull(inDto[x], "YD_SCH_CD"));
				recPara.setField("YD_STK_COL_GP", 	ydDaoUtils.paraRecChkNull(inDto[x], "TO_YD_STK_COL_GP"));
				recPara.setField("STL_SH"		, 	"1");
				recPara.setField("YD_WRK_PLAN_TCAR", ydDaoUtils.paraRecChkNull(inDto[x], "YD_WRK_PLAN_TCAR"));
				recPara.setField("TO_YD_STK_BED_NO", sTO_YD_STK_COL_GP.substring(1,6)+inDto[x].getField("TO_YD_STK_BED_NO"));
				recPara.setField("REGISTER",        inDto[x].getField("REGISTER"));
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
					}else{
						szMsg="이적 재료 정보 확인중 Error";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, szMsg);	
						return outRecord;
					}
				}
				
				List RecSetTempList0 = outRecSet.toList();
//				jrecord = (JDTORecord)RecSetTempList0.get(0);
				
				//추가 데이터 
				//TO Guide 정보

				String szColGp  = ydDaoUtils.paraRecChkNull(inDto[x], "TO_YD_STK_COL_GP");
				String szBedNo  = ydDaoUtils.paraRecChkNull(inDto[x], "TO_YD_STK_BED_NO"); 
				String szBedNoR = YdUtils.fillSpZr(Integer.parseInt(szBedNo) + 1 + "", 2, 0);

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
					sBED_STL_NO = ydDaoUtils.paraRecChkNull(recStkLyr,"STL_NO");
					
					if((sBED_STL_NO.equals("")) && (szStkLyrMtlStat.equals("E"))){
						//1단 check
						inRecord = JDTORecordFactory.getInstance().create();
						inRecord.setField("YD_STK_COL_GP" 	, ydDaoUtils.paraRecChkNull(inDto[x], "TO_YD_STK_COL_GP"));	
						inRecord.setField("YD_STK_BED_NO" 	, ydDaoUtils.paraRecChkNull(inDto[x], "TO_YD_STK_BED_NO"));	
						inRecord.setField("YD_STK_LYR_NO" 	, "001");	
							
						outRecord1 = ydToLocDcsnUtil.CoilLyrBaseCheck(sTAG_STL_NO, inRecord);
						sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
						sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
						sRTN_TO_BEDNO	= StringHelper.evl(outRecord1.getFieldString("RTN_TO_BEDNO"), "");
						
						if (!("1".equals(sRTN_CD))) {
							outRecord.setField("RTN_CD" 	, "0");	
							outRecord.setField("RTN_MSG" 	, sRTN_MSG);	
							return outRecord;
						}

					} else {
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
								
							outRecord1 = ydToLocDcsnUtil.CoilLyrBaseCheck(sTAG_STL_NO, inRecord);
							sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
							sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
							sRTN_TO_BEDNO	= StringHelper.evl(outRecord1.getFieldString("RTN_TO_BEDNO"), "");
							if (!("1".equals(sRTN_CD))) {
								outRecord.setField("RTN_CD" 	, "0");	
								outRecord.setField("RTN_MSG" 	, sRTN_MSG);	
								return outRecord;
							}	
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
							
						outRecord1 = ydToLocDcsnUtil.CoilLyrBaseCheck(sTAG_STL_NO, inRecord);
						sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
						sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
						sRTN_TO_BEDNO	= StringHelper.evl(outRecord1.getFieldString("RTN_TO_BEDNO"), "");
						
						if (!("1".equals(sRTN_CD))) {
							outRecord.setField("RTN_CD" 	, "0");	
							outRecord.setField("RTN_MSG" 	, sRTN_MSG);	
							return outRecord;
						}
					}	
				}
				ydUtils.displayRecord(szOperationName, recPara);
				
			}
			
			recPara.setField("TO_YD_STK_BED_NO", sRTN_TO_BEDNO );
			recPara.setField("RTN_CD" 	, "1");	
			return recPara;
			
		} catch (Exception e) {
			e.printStackTrace();
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
	} // end of getToDongUseCount
	
	
	/**
	 *  위치검색 범위 조회 (화면:위치검색SPAN관리) 하단 왼쪽 그리드 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public GridData getSpanbyLowInfo(GridData inDto){
		GridData rtnGrd = new GridData();
		JDTORecordSet    outRecSet  = null;
				
		CoilJspDAO dao = new CoilJspDAO();
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
	 * 야드관리 > 코일소재야드 > 기준관리 > 저장위치용도관리  목록조회
	 *
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.04.26
	 */
	public GridData getStrlocUsgSetList(GridData inDto) {
		GridData 		rtnGrd 		= new GridData();
		JDTORecordSet   outRecSet  	= null;
		
		CoilJspDAO 		dao 		= new CoilJspDAO();
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
	 * 야드관리 > 코일소재야드 > 기준관리 > 저장위치용도관리  등록
	 *
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.04.26
	 */
	public GridData updStrlocUsgSet(GridData inDto) {
		GridData 		rtnGrd 		= new GridData();
		int 			ret			= 0;
		int 			res			= 0;
		CoilJspDAO 		dao 		= new CoilJspDAO();
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
//!AT			/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedBedDan*/
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
	 * 코일소재야드 tracking 조회
     * 송
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdHrTracking(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getCoilYdHrTracking";

		
		String szEqpGp = null;
		String szOperationName = "코일제품야드 입고Tracking 조회";
		
		
		//DAO
		YdCrnWrkMtlDao ydCrnWrkMtlDao = new YdCrnWrkMtlDao();
		
		int intRtnVal = 0;
		
		try {
			
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			szEqpGp = ydDaoUtils.paraRecChkNull(inDto, "EQP_GP");
			
			//------------------------------------------------------------------------------------
			// 코일 입고진행관리 조회 (2010.02.22) - 설비
			//------------------------------------------------------------------------------------
			
			if(!szEqpGp.equals("")){
					
				recPara         = JDTORecordFactory.getInstance().create();
				recPara.setField("EQP_CD", szEqpGp);
//				/*com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getCoilYdHrTracking*/		
				intRtnVal = ydCrnWrkMtlDao.getYdCrnwrkmtl(recPara, outRecSet, 300);
				
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
			
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilYdHrTracking

	/**
	 * 코일소재야드 tracking 조회백업용
     * 송
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdHrTrackingBackUp(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getCoilYdHrTrackingBackUp";

		
		String szEqpGp = null;
		String szOperationName = "코일제품야드 입고Tracking조회백업용";
		
		
		//DAO
		YdCrnWrkMtlDao ydCrnWrkMtlDao = new YdCrnWrkMtlDao();
		
		int intRtnVal = 0;
		
		try {
			
			szEqpGp = ydDaoUtils.paraRecChkNull(inDto, "EQP_GP");
			
			if(!szEqpGp.equals("")){
					
				recPara         = JDTORecordFactory.getInstance().create();
				recPara.setField("EQP_CD", szEqpGp);
//				/*com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getCoilYdHrTrackingBackUp*/			
				intRtnVal = ydCrnWrkMtlDao.getYdCrnwrkmtl(recPara, outRecSet, 301);
				
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
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilYdHrTracking

	
	
	/**
	 * 코일소재야드 tracking 상세조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public JDTORecordSet getCoilYdHrTrackingDtl(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getCoilYdHrTrackingDtl";
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		
		int intRtnVal = 0;
		
		try {

			recPara.setField("EQP_CD", ydDaoUtils.paraRecChkNull(inDto,"GUBUN"));
			
//getYdStklyr302	
//PIDEV_S :병행가동용:PI_YD
			recPara.setField("PI_YD",    	"J");						
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 302);
		
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
		return outRecSet;
	
	}	
	/**
	 * 코일소재야드 tracking 백업상세조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public JDTORecordSet getCoilYdHrTrackingBackUpDtl(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getCoilYdHrTrackingBackUpDtl";
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		
		int intRtnVal = 0;
		
		try {

			recPara.setField("EQP_CD", ydDaoUtils.paraRecChkNull(inDto,"GUBUN"));
			
//getYdStklyr302
//PIDEV_S :병행가동용:PI_YD
			recPara.setField("PI_YD",    	"J");					
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 303);
		
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
		return outRecSet;
	
	}	
	/**
	 * 코일소재야드 tracking 팝업 조회보급
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public JDTORecordSet getcoilYdLineWrPp(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getcoilYdLineWrPp";

		
		String sSTL_NO 		= "";
		String sWORK_STAT 	= null;
		String szOperationName = "코일제품야드 입고Tracking조회 팝업";
		
		
		//DAO
		YdCrnWrkMtlDao ydCrnWrkMtlDao = new YdCrnWrkMtlDao();
		
		int intRtnVal = 0;
		
		try {
			
			sSTL_NO	= ydDaoUtils.paraRecChkNull(inDto, "COIL_NO");
			
//			if(!sSTL_NO.equals("")){
					
				recPara         = JDTORecordFactory.getInstance().create();
				recPara.setField("LINE"		, inDto.getField("EQP_GP"));	/*동구분*/
				recPara.setField("COIL_NO"	, sSTL_NO);
				recPara.setField("PAGE_NO"	, inDto.getField("PAGE_NO"));
				recPara.setField("ROW_CNT"	, inDto.getField("ROWCOUNT"));

//getYdCrnwrkmtl300					
				/*com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getcoilYdLineWrPp*/
				intRtnVal = ydCrnWrkMtlDao.getYdCrnwrkmtl(recPara, outRecSet, 302);
				
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
//			}
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	
	}	
	/**
	 * 코일소재야드 tracking 팝업 조회보급
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public JDTORecordSet getcoilYdLineWrCodePp(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getcoilYdLineWrCodePp";

		
		String sINPUT_GB 	= null;
		String szOperationName = "코일제품야드 입고Tracking조회 팝업";
		
		
		//DAO
		YdCrnWrkMtlDao ydCrnWrkMtlDao = new YdCrnWrkMtlDao();
		
		int intRtnVal = 0;
		
		try {
			
			sINPUT_GB 	= ydDaoUtils.paraRecChkNull(inDto, "PARA_INPUT_GB");
			
//			if(!sINPUT_GB.equals("")){
					
				recPara         = JDTORecordFactory.getInstance().create();
				recPara.setField("PARA_INPUT_GB"	, sINPUT_GB);

//getYdCrnwrkmtl300					
				intRtnVal = ydCrnWrkMtlDao.getYdCrnwrkmtl(recPara, outRecSet, 303);
				
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
//			}
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	
	}	
	
	
	/**
	 * 오퍼레이션명 : 작업 취소
	 * 스케쥴 취소
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord WrkCancel(JDTORecord msgRecord)throws JDTOException  {
		
		YdCrnSchDao  ydCrnSchDao        = new  YdCrnSchDao();
		YdEqpDao     ydEqpDao     = new YdEqpDao();
		ymCommonDAO dao = ymCommonDAO.getInstance();
		
		//리턴레코드셋
		JDTORecordSet rsRtnVal = JDTORecordFactory.getInstance().createRecordSet("temRs");
		//파라미터 레코드 생성
		JDTORecord recPara  	= JDTORecordFactory.getInstance().create();
		JDTORecord recCheck  	= JDTORecordFactory.getInstance().create();
		JDTORecord outRecord 	= JDTORecordFactory.getInstance().create(); // 
		JDTORecord outRecord1   = JDTORecordFactory.getInstance().create(); // 
		JDTORecord recInPara = null;
		//파라미터 스크링 변수
		String szV_YD_CRN_SCH_ID  = null;
		String szV_YD_SCH_CD      = null;
		String szV_DEL_YN         = null;
		String szV_MODIFIER       = null;
		String szDEL_FLAG		  = "N";
		String sRTN_CD	="";
		String sRTN_MSG ="";	
		String szYD_L2_REQUEST_STAT = "";
		String sQueryId = "";
		
		//리턴값
		int intRtnVal = 0;
		
		//체크 값
		String szC_YD_WRK_PROG_STAT= null;
		String szC_YD_EQP_ID= null;
		String szYD_SCH_CD = null;
		
		String szMsg="";
		String szMethodName="WrkCancel";
		
		szMsg = "[Jsp Session : "+szMethodName+"] 메소드 시작   ";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
		try {
			//크레인스케줄 ID
			szV_YD_CRN_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");
			szDEL_FLAG	      = ydDaoUtils.paraRecChkNull(msgRecord, "DEL_FLAG");
			szC_YD_EQP_ID 	  = ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID");
			szYD_SCH_CD 	  = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			
			if (szV_YD_CRN_SCH_ID.equals("")) {
				
				szMsg="스케줄 취소 처리("+szMethodName+") 실패, YD_CRN_SCH_ID값이 없음";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;
			}
			
			//파라미터 레코드 필수항목 null 체크 및 스트링 편집
	
			szV_YD_SCH_CD     = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");  //스케줄 코드
			szV_DEL_YN        = ydDaoUtils.paraRecChkNull(msgRecord, "DEL_YN");  //삭제유무
			szV_MODIFIER      = ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER");  //수정자
			
			//파라미터 레코드 setting
			recPara  = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CRN_SCH_ID", szV_YD_CRN_SCH_ID);
			recPara.setField("YD_SCH_CD",     szV_YD_SCH_CD);
			recPara.setField("DEL_YN",        szV_DEL_YN);		
			recPara.setField("MODIFIER",      szV_MODIFIER);
	
			/*
			 * 크레인 스케줄에서 선택된 스케줄 ID로 작업예약을 조회한 후
			 * 삭제되지 않는 작업예약에 포함된 크레인 스케줄중 첫번째 값이
			 * 2,3 인 경우 후처리를 하지않기 위해  Check Logic 반영 
			 */
			
			// com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getCheckYdCrnSchId
			
			rsRtnVal = JDTORecordFactory.getInstance().createRecordSet("temRs");
			/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getCheckYdCrnSchId*/
			intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, rsRtnVal, 36);
			
			if (intRtnVal < 1){
				szMsg = "대상 스케줄이 존재 하지않습니다";
	//			szMsg = "취소 작업을 완료 하였습니다.";
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;
			}
			
			rsRtnVal.first();		
			recCheck = rsRtnVal.getRecord();
			
			szC_YD_WRK_PROG_STAT = ydDaoUtils.paraRecChkNull(recCheck, "YD_WRK_PROG_STAT"); 
			
			
	//		1 권상작업지시 권상작업지시  
	//		2 권상중 권상중              
	//		3 권하지시 권하지시          
	//		4 권하완료 권하완료          
			
			if(szDEL_FLAG.equals("N") || szDEL_FLAG.equals("")){
				
				szMsg = "[Jsp Session :hun szC_YD_WRK_PROG_STAT ="+szC_YD_WRK_PROG_STAT+"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//2,3, 인 경우 와 혹시모를 4이면서도 스케줄 삭제가 되지 않은 경우
				if(szC_YD_WRK_PROG_STAT.equals("2") || szC_YD_WRK_PROG_STAT.equals("3") || szC_YD_WRK_PROG_STAT.equals("4")){
					szMsg = "크레인 작업이 완료되지 않았습니다!!";
					outRecord.setField("RTN_CD" , "0");	
					outRecord.setField("RTN_MSG", szMsg);	
					return outRecord;
				}
			}
			
			/*
			 * 2,3이 아닌 경우 스케줄 취소기능에  첫번째 크레인 스케줄 ID 정보를 전송
			 */		
			
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(recCheck, "YD_CRN_SCH_ID"));
			recPara.setField("YD_SCH_CD",     ydDaoUtils.paraRecChkNull(recCheck, "YD_SCH_CD"));
			recPara.setField("DEL_YN",        ydDaoUtils.paraRecChkNull(recCheck, "DEL_YN"));		
			recPara.setField("MODIFIER",      szV_MODIFIER);
			recPara.setField("YD_L2_RETURN_FLAG",      ydDaoUtils.paraRecChkNull(msgRecord, "YD_L2_RETURN_FLAG"));
			recPara.setField("IS_LAST_SELECTED",      ydDaoUtils.paraRecChkNull(msgRecord, "IS_LAST_SELECTED"));
			
			outRecord1 = (JDTORecord)this.ScheduleCancel(recPara);
			
			sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
			sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
			
			if ("0".equals(sRTN_CD)) {
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, sRTN_MSG);	
				return outRecord;
			}	
			outRecord1.setField("RTN_CD" , "1");	
			outRecord1.setField("RTN_MSG", szMsg);	
			return outRecord1;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
			

	}// end of wrkCncl()
	/**
	 * 오퍼레이션명 : 저장위치 변경에서 작업 취소
	 * 스케쥴 취소
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord WrkCancelloc(JDTORecord msgRecord)throws JDTOException  {
		
		YdCrnSchDao  ydCrnSchDao        = new  YdCrnSchDao();
		
		//리턴레코드셋
		JDTORecordSet rsRtnVal = JDTORecordFactory.getInstance().createRecordSet("temRs");
		//파라미터 레코드 생성
		JDTORecord recPara  	= JDTORecordFactory.getInstance().create();
		JDTORecord recCheck  	= JDTORecordFactory.getInstance().create();
		JDTORecord outRecord 	= JDTORecordFactory.getInstance().create(); // 
		JDTORecord outRecord1   = JDTORecordFactory.getInstance().create(); // 
		//파라미터 스크링 변수
		String szV_YD_CRN_SCH_ID  = null;
		String szV_YD_SCH_CD      = null;
		String szV_DEL_YN         = null;
		String szV_MODIFIER       = null;
		String szDEL_FLAG		  = "N";
		//리턴값
		int intRtnVal = 0;
		
		//체크 값
		String szC_YD_WRK_PROG_STAT= null;
		
		String szMsg="";
		String szMethodName="WrkCancelloc";
		
		szMsg = "[Jsp Session : "+szMethodName+"] 메소드 시작   ";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
		try {
			//크레인스케줄 ID
			szV_YD_CRN_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");
			szDEL_FLAG	      = ydDaoUtils.paraRecChkNull(msgRecord, "DEL_FLAG");
			
			if (szV_YD_CRN_SCH_ID.equals("")) {
				
				szMsg="스케줄 취소 처리("+szMethodName+") 실패, YD_CRN_SCH_ID값이 없음";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;
			}
			
			//파라미터 레코드 필수항목 null 체크 및 스트링 편집
	
			szV_YD_SCH_CD     = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");  //스케줄 코드
			szV_DEL_YN        = ydDaoUtils.paraRecChkNull(msgRecord, "DEL_YN");  //삭제유무
			szV_MODIFIER      = ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER");  //수정자
			
			//파라미터 레코드 setting
			recPara  = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CRN_SCH_ID", szV_YD_CRN_SCH_ID);
			recPara.setField("YD_SCH_CD",     szV_YD_SCH_CD);
			recPara.setField("DEL_YN",        szV_DEL_YN);		
			recPara.setField("MODIFIER",      szV_MODIFIER);
	
			/*
			 * 크레인 스케줄에서 선택된 스케줄 ID로 작업예약을 조회한 후
			 * 삭제되지 않는 작업예약에 포함된 크레인 스케줄중 첫번째 값이
			 * 2,3 인 경우 후처리를 하지않기 위해  Check Logic 반영 
			 */
			
			// com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getCheckYdCrnSchId
			
			rsRtnVal = JDTORecordFactory.getInstance().createRecordSet("temRs");
			/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getCheckYdCrnSchId*/
			intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, rsRtnVal, 36);
			
			if (intRtnVal < 1){
				szMsg = "대상 스케줄이 존재 하지않습니다";
	//			szMsg = "취소 작업을 완료 하였습니다.";
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szMsg);	
				return outRecord;
			}
			
			rsRtnVal.first();		
			recCheck = rsRtnVal.getRecord();
			
			szC_YD_WRK_PROG_STAT = ydDaoUtils.paraRecChkNull(recCheck, "YD_WRK_PROG_STAT"); 
			
			/*
			 * 2,3이 아닌 경우 스케줄 취소기능에  첫번째 크레인 스케줄 ID 정보를 전송
			 */		
			
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CRN_SCH_ID", ydDaoUtils.paraRecChkNull(recCheck, "YD_CRN_SCH_ID"));
			recPara.setField("YD_SCH_CD",     ydDaoUtils.paraRecChkNull(recCheck, "YD_SCH_CD"));
			recPara.setField("DEL_YN",        ydDaoUtils.paraRecChkNull(recCheck, "DEL_YN"));		
			recPara.setField("MODIFIER",      szV_MODIFIER);
			
			
			//크레인스케줄 ID보다 이상인 ID 삭제  업데이트 실행(적치단정보까지 CLEAR)
			//스케줄취소
			outRecord1 = (JDTORecord)this.ScheduleCancel(recPara);
			
			String sRTN_CD	= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
			String sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
			if ("0".equals(sRTN_CD)) {
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, sRTN_MSG);	
				return outRecord;
			}	
			outRecord1.setField("RTN_CD" , "1");	
			outRecord1.setField("RTN_MSG", szMsg);	
			return outRecord1;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
			

	}// end of wrkCncl()
	
	/**
	 * 오퍼레이션명 : 스케줄 삭제
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord ScheduleCancel(JDTORecord msgRecord)throws JDTOException  {
		YdCrnSchDao ydCrnSchDao	= new  YdCrnSchDao();
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		YdStockDao ydStockDao 	= new YdStockDao();
		YdEqpDao ydEqpDao 		= new YdEqpDao();
		YdStkBedDao ydStkBedDao = new YdStkBedDao();
		YdCrnWrkMtlDao ydCrnWrkMtlDao = new YdCrnWrkMtlDao();
		
		//wan
		//파라미터 레코드 생성
		JDTORecord recPara   	= JDTORecordFactory.getInstance().create();
		JDTORecord recParaStock = JDTORecordFactory.getInstance().create();
		JDTORecord recEqpPara   = JDTORecordFactory.getInstance().create();
		JDTORecord outRecord 	= JDTORecordFactory.getInstance().create(); // 
		JDTORecord recDelPara 	= JDTORecordFactory.getInstance().create(); // 
		JDTORecord recGetCrnSch = JDTORecordFactory.getInstance().create(); //
		
		JDTORecordSet rsGetCrnMtl 	= JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecordSet rsGetBedInfo 	= JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecordSet rsGetCrnSch 	= JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecordSet outRecSet 	= JDTORecordFactory.getInstance().createRecordSet("YD");
		JDTORecordSet rsCrnSchInfo 	= null;
		
		
		
		//크레인작업재료 레코드
		JDTORecord recGetCrnMtl		= null;
		JDTORecord recSetStkLyr 	= JDTORecordFactory.getInstance().create();
		JDTORecord recSetStkBed 	= JDTORecordFactory.getInstance().create();
		
		
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
		String szYD_L2_RETURN_FLAG = null;
		
		String szMsg			= "";
		String szMethodName		= "ScheduleCancel";
		
		String szYdSchId 		= "";
		String szYdWrkProgStat  = "";
		String szEqpId 			= "";
		String szOperationName 	= "스케줄 삭제";

		
		String szUpdEqpstat 	= "";
		String szWbookId 		= "";
		String schk 			= "Y";
		String supdYdStklyrchk	= "Y";
		szMsg = "[Jsp Session : "+szOperationName+"] 메소드 시작   ";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
		//========크레인스케줄 삭제==========//
		try {
			
			//파라미터 null 체크
			szV_YD_CRN_SCH_ID 	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");
			szV_YD_SCH_CD     	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			szV_DEL_YN        	= ydDaoUtils.paraRecChkNull(msgRecord, "DEL_YN");
			szV_MODIFIER      	= ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER");
			szYD_L2_RETURN_FLAG = ydDaoUtils.paraRecChkNull(msgRecord, "YD_L2_RETURN_FLAG");
			
			//파라미터 레코드 편집
			recPara.setField("YD_CRN_SCH_ID", szV_YD_CRN_SCH_ID);
			recPara.setField("YD_SCH_CD",     szV_YD_SCH_CD);
			recPara.setField("DEL_YN",        szV_DEL_YN);
			recPara.setField("MODIFIER",      szV_MODIFIER);
			
			//스케줄코드가 동일하고 크레인스케줄 ID이상인 크레인스케줄ID를 SELECT ( 추가 : 스케줄 ID에 포함된 같은 작업예약정보에서만 추출)
			
			rsGetCrnSch = JDTORecordFactory.getInstance().createRecordSet("YD");
			
			szMsg = "[Jsp Session : "+szOperationName+"] 스케줄코드가 동일하고 크레인스케줄 ID이상인 크레인스케줄ID를 SELECT";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschCrnIdOVERID*/
			intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, rsGetCrnSch, 5);
			
			//더 이상 삭제 작업이 없는경우
			if (intRtnVal < 1) {
				szMsg = "[Jsp Session : "+szOperationName+"] 삭제 작업이 완료되었습니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				outRecord.setField("RTN_CD" 	, "1");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
			}
			
			//레코드셋을 역순으로
			
			szMsg = "[Jsp Session : "+szOperationName+"] 레코드셋을 역순으로 정렬 - reverseOrder";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			rsGetCrnSch.reverseOrder();
			//레코드셋의 커서를 처음으로
			
			szMsg = "[Jsp Session : "+szOperationName+"] 레코드셋처음으로 이동";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			rsGetCrnSch.first();
			
			szMsg = "[Jsp Session : "+szOperationName+"] 선택된 건수 :" + rsGetCrnSch.size()  ;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
	
			//크레인스케줄 데이터 만큼 루프를 돌아서 크레인스케줄ID에 편성된 재료를 찾아 적치단을 CLEAR한다.
			for (int Loop_i = 0; Loop_i < rsGetCrnSch.size(); Loop_i++) {
				
				//크레인스케줄 데이터의 레코드를 추출(작업상태 체크를 위해 미리 추출)
				
				//ADD
				recGetCrnSch = JDTORecordFactory.getInstance().create();
				recGetCrnSch = rsGetCrnSch.getRecord(Loop_i);
				
				recPara = JDTORecordFactory.getInstance().create();
			
				szYdSchId 		= ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_CRN_SCH_ID");
				szWbookId 		= ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_WBOOK_ID");
				szYdWrkProgStat = ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_WRK_PROG_STAT");
				
				recPara.setField("YD_CRN_SCH_ID",szYdSchId);
				
				// 설비번호를 얻는다.(YD_EQP_ID) => 설비상태를  'W'(대기)상태로 만들어주기 위함
				szEqpId =  ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_EQP_ID");
				
				szMsg = "[Jsp Session : "+szOperationName+"] 스케줄에 편성된 설비번호: " + szEqpId  ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	
				
				szV_YD_UP_WO_LOC   = ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_UP_WO_LOC");  //권상 지시위치
				szV_YD_UP_WO_LAYER = ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_UP_WO_LAYER"); //권상 지시단
				szV_YD_DN_WO_LOC   = ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_DN_WO_LOC");   //권하 지시위치
				szV_YD_DN_WO_LAYER = ydDaoUtils.paraRecChkNull(recGetCrnSch, "YD_DN_WO_LAYER");  //권하 지시단
				
				//해당 크레인스케줄ID로 크레인작업재료를 SELECT
				
				szMsg = "[Jsp Session : "+szOperationName+"] 해당 크레인스케줄ID로 크레인작업재료를 SELECT "  ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				
				rsGetCrnMtl = JDTORecordFactory.getInstance().createRecordSet("temp");
				/* com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdCrnwrkmtlCoilComm */
				//intRtnVal = ydCrnSchDao.getYdCrnsch(recGetCrnSch, rsGetCrnMtl, 3);
				intRtnVal = ydCrnSchDao.getYdCrnsch(recGetCrnSch, rsGetCrnMtl, 400);
				
				//에러리턴
				if (intRtnVal < 0) {
					
					szMsg = "[Jsp Session : "+szOperationName+"] 실패! 해당 작업재료 조회 ERROR :" +  intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
				}
				
				//------------------------------------------------------------------------------------------------
				// 권상지시 상태(작업지시가 내려간경우) - 취소 처리 
				//------------------------------------------------------------------------------------------------
				
				if( szYdWrkProgStat.equals(YdConstant.YD_EQP_STAT_UP_WO) ){
					
					//------------------------------------------------------------------------------------------------
					//  작업지시 취소 전문 : YD_CRN_SCH_ID,YD_WRK_PROG_STAT, MSG_GP = 'D'
//					//------------------------------------------------------------------------------------------------
//					outRecord.setField("CRANE_SND" 			, "Y");	
//					outRecord.setField("YD_CRN_SCH_ID" 		, szYdSchId);	
//					outRecord.setField("YD_WRK_PROG_STAT" 	, szYdWrkProgStat);	
//					outRecord.setField("MSG_GP" 			, "D");	
//					ydUtils.putLog(szSessionName, szMethodName, "취소전문 확인" , YdConstant.DEBUG);	
					
//					150902 hun 자동크레인 응답(YDY5L015) 왔을때는 전송 안함...
					if(!"Y".equals(szYD_L2_RETURN_FLAG) ){
						recDelPara   = JDTORecordFactory.getInstance().create();
						recDelPara.setField("MSG_ID",           "YDY5L004"        );
						recDelPara.setField("YD_CRN_SCH_ID",    szYdSchId          ); 
						recDelPara.setField("YD_WRK_PROG_STAT", szYdWrkProgStat    );   // 이모듈을 탈려면 항상 '1'의값이 들어옴
						recDelPara.setField("MSG_GP",           "D"                );
	//					recDelPara.setField("MSG_DEL_FLAG",     "Y"                );
						
						YdDelegate ydDelegate = new YdDelegate();					
						ydUtils.displayRecord(szOperationName, recDelPara);					
						ydDelegate.sendMsg(recDelPara);
					}
					
				}
				//------------------------------------------------------------------------------------------------
				// 권상/ 권하 위치 Log
				//------------------------------------------------------------------------------------------------
				
				szMsg="권상지시위치 "+szV_YD_UP_WO_LOC+"\n";
				szMsg+="권상 지시단 "+szV_YD_UP_WO_LAYER+"\n";
				szMsg+="권하 지시위치 "+szV_YD_DN_WO_LOC+"\n";
				szMsg+="권하 지시단 "+szV_YD_DN_WO_LAYER+"\n";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//------------------------------------------------------------------------------------------------
				
				//------------------------------------------------------------------------------------------------
				// 권하위치 원복
				//
				// - 2009.10.07 스케줄/작업 취소시 권하위치가 XX010101 BED로 잡혀있는경우나
				//   권하지시위치 정보가 올바르게 들어있지않는 정보는 돌려줄 수 없다.
				//------------------------------------------------------------------------------------------------
				
				
				if (!( szV_YD_DN_WO_LOC.equals("") || szV_YD_DN_WO_LAYER.equals("")  || szV_YD_DN_WO_LOC.equals("XX010101") )  ){
	
					//레코드의 커서를 처음으로
					
					szMsg = "[Jsp Session : "+szOperationName+"] 권하지시위치 " + szV_YD_DN_WO_LOC + "-" + szV_YD_DN_WO_LAYER ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					szMsg = "[Jsp Session : "+szOperationName+"] 크레인 작업재료 매수  " + rsGetCrnMtl.size() ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
					rsGetCrnMtl.first();
					
									
					//레코드 갯수를 구한다.
					intRsGetCrnMtlSize = rsGetCrnMtl.size();
					
					//크레인스케줄의 작업 재료 만큼 루프를 돌아 권상권하 대기 정보를 초기화한다.
					for (int Loop_j = 0; Loop_j < intRsGetCrnMtlSize; Loop_j++) {
						
						//크레인작업재료 데이터의 레코드를 추출
						
						recGetCrnMtl = JDTORecordFactory.getInstance().create();					
						recGetCrnMtl = rsGetCrnMtl.getRecord(Loop_j);
						
						
						ydUtils.putLog(szSessionName, szMethodName, "recGetCrnMtl",4);
						ydUtils.displayRecord(szOperationName, recGetCrnMtl);
						
						recSetStkLyr.setField("YD_STK_COL_GP",       szV_YD_DN_WO_LOC.substring(0, 6));
						recSetStkLyr.setField("YD_STK_BED_NO",       szV_YD_DN_WO_LOC.substring(6, 8));
						szStkLyrPlus = ydDaoUtils.stringPlusInt(szV_YD_DN_WO_LAYER, Loop_j);
						recSetStkLyr.setField("YD_STK_LYR_NO",       szStkLyrPlus);
						recSetStkLyr.setField("STL_NO",              "");
						recSetStkLyr.setField("YD_STK_LYR_MTL_STAT", "E");

						if(szV_YD_DN_WO_LOC.substring(6, 8).equals("00")){
							
							
							// TODO.. 권하시의처리가 필요 한가...? 없어도 될듯 
							
							
						} else {

							szMsg = "[Jsp Session : "+szOperationName+"] : 권하 재료 정보 복원";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							
							//적치단 테이블에 권하지시 CLEAR 업데이트
							
							szMsg = "[Jsp Session : "+szOperationName+"] : 권하 지시 위치 CLEAR";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							
							ydUtils.displayRecord(szOperationName, recSetStkLyr);
							
							intRtnVal = ydStkLyrDao.updYdStklyr(recSetStkLyr, 0);
							
							//에러리턴
							if (intRtnVal < 1) {
								szMsg = "[Jsp Session : "+szOperationName+"] : 권하 지시 위치 CLEAR 실패";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
								outRecord.setField("RTN_CD" 	, "0");	
								outRecord.setField("RTN_MSG" 	, szMsg);	
								return outRecord;
	
							}
							
							szMsg = "[Jsp Session : "+szOperationName+"] : 권하 지시 위치 CLEAR 성공";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						}	
											
						szMsg = "[Jsp Session : "+szOperationName+"] : 권하위치 Bed 정보 조회";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						rsGetBedInfo = JDTORecordFactory.getInstance().createRecordSet("YD");
						intRtnVal =  ydStkBedDao.getYdStkbed(recSetStkLyr, rsGetBedInfo, 0);
						
						if(intRtnVal > 0){
						
							//------------------------------------------------------------------------------------------------
							// 야드적치BED입출고상태 변경
							// 권하위치 정보의 BED정보를 읽는다 -> 
							// 완산베드 상태를 적치가능상태로 바꾸어준다.
							//------------------------------------------------------------------------------------------------
							
							rsGetBedInfo.first();
							recSetStkBed = rsGetBedInfo.getRecord();
							
							if(YdConstant.YD_STK_BED_WHIO_FULL.equals(ydDaoUtils.paraRecChkNull(recSetStkBed, "YD_STK_BED_WHIO_STAT")) ){
								recSetStkBed.setField("YD_STK_BED_WHIO_STAT", YdConstant.YD_STK_BED_WHIO_ENABLE);
								recSetStkBed.setField("MODIFIER", szV_MODIFIER);
								
								
								szMsg = "[Jsp Session : "+szOperationName+"] : 완산베드 상태를 적치가능상태로 변경한다.";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								
								intRtnVal = ydStkBedDao.updYdStkbed(recSetStkBed, 0);
								
								if(intRtnVal < 0 ){
									szMsg = "[Jsp Session : "+szOperationName+"] : 야드적치Bed입출고상태 변경 UPDATE ERROR 발생 .";
									ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
									outRecord.setField("RTN_CD" 	, "0");	
									outRecord.setField("RTN_MSG" 	, szMsg);	
									return outRecord;
								}else{
									szMsg = "[Jsp Session : "+szOperationName+"] : 야드적치Bed입출고상태 변경 UPDATE 완료.";
									ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
									
								}
							}
						}else{
							//해당 베드의 정보가 존재 하지 않습니다.
												
							szMsg = "[Jsp Session : "+szOperationName+"] : 해당 베드의 정보가 존재 하지 않습니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
						}
					}
						
				}else{
					//권하위치가 올바르게 잡혀있지 않을때 에러처리를 원한다면 RollBack 을 시킬수 있다.				
					szMsg = "[Jsp Session : "+szOperationName+"] : 권하위치가 올바른형식이 아니라 원복시킬수 없습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);				
				}
				
				//------------------------------------------------------------------------------------------------
				//	권상위치 원복 
				//------------------------------------------------------------------------------------------------
				
				if (!( szV_YD_UP_WO_LOC.equals("") || szV_YD_UP_WO_LAYER.equals("") )){
	
					//레코드의 커서를 처음으로
					
					szMsg = "[Jsp Session : "+szOperationName+"] : 권상지시 정보  :" + szV_YD_UP_WO_LOC + "-" + szV_YD_UP_WO_LAYER;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
					
					rsGetCrnMtl.first();
					
					//레코드 갯수를 구한다.
					intRsGetCrnMtlSize = rsGetCrnMtl.size();
					
					
					//------------------------------------------------------------------------------------------------
					//	크레인스케줄의 작업 재료 만큼 루프를 돌아 권상대기 정보를 초기화한다. 
					//------------------------------------------------------------------------------------------------
					
					
					for (int Loop_j = 0; Loop_j < intRsGetCrnMtlSize; Loop_j++) {
						
						//크레인작업재료 데이터의 레코드를 추출
						String eqpCd  = szV_YD_UP_WO_LOC.substring(2, 4);
						String eqpCd1 = szV_YD_UP_WO_LOC.substring(2, 6);

						recGetCrnMtl = JDTORecordFactory.getInstance().create();					
						recGetCrnMtl = rsGetCrnMtl.getRecord(Loop_j);
						recSetStkLyr.setField("YD_STK_COL_GP"		, szV_YD_UP_WO_LOC.substring(0, 6));
						recSetStkLyr.setField("YD_STK_BED_NO"		, szV_YD_UP_WO_LOC.substring(6, 8));
						szStkLyrPlus = ydDaoUtils.stringPlusInt(szV_YD_UP_WO_LAYER, Loop_j);
						recSetStkLyr.setField("YD_STK_LYR_NO"		, szStkLyrPlus);
					
						szMsg = "[Jsp Session : "+szOperationName+"] : szV_YD_SCH_CD:" + szV_YD_SCH_CD;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//C증설						
						/*take out 일 경우*/
						if( szV_YD_SCH_CD.equals("HBKE03LM")|| 
							szV_YD_SCH_CD.equals("HAKE03LM")|| 
							szV_YD_SCH_CD.equals("HBFE03LM")||
							szV_YD_SCH_CD.equals("HCKE03LM")|| 
							szV_YD_SCH_CD.equals("HCFE03LM")||
							szV_YD_SCH_CD.equals("HDFE03LM")||
							szV_YD_SCH_CD.equals("HEDE03LM")||
							szV_YD_SCH_CD.equals("HFFE03LM")||
							szV_YD_SCH_CD.equals("HGFE03LM")||
							szV_YD_SCH_CD.equals("HHKE03LM")||
							//추출
							szV_YD_SCH_CD.equals("HBKD03LM")|| 
							szV_YD_SCH_CD.equals("HAKD03LM")||  
							szV_YD_SCH_CD.equals("HCKD03LM")||   
							szV_YD_SCH_CD.equals("HEDD03LM")||  
							szV_YD_SCH_CD.equals("HHKD03LM")
							
							) {
							
							recSetStkLyr.setField("STL_NO"				, recGetCrnMtl.getField("STL_NO"));
							recSetStkLyr.setField("YD_STK_LYR_MTL_STAT"	, "C");
						} else {
									
							// 설비일때..
							if (eqpCd.equals("KD") || eqpCd.equals("KE") || 
							    eqpCd.equals("FD") || eqpCd.equals("DD") || eqpCd.equals("DE") || 
								eqpCd.equals("CV") || eqpCd1.equals("FE01")|| eqpCd1.equals("FE04")) { 
								
								recSetStkLyr.setField("STL_NO"				, ""); 
								recSetStkLyr.setField("YD_STK_LYR_MTL_STAT"	, "E");
								
								schk ="N";
								
							} else {
								
								recSetStkLyr.setField("STL_NO"				, recGetCrnMtl.getField("STL_NO"));
								recSetStkLyr.setField("YD_STK_LYR_MTL_STAT"	, "C");
							}

							
						}	

						szMsg = "[Jsp Session : "+szOperationName+"] : 권상지시 정보  복원하기 전에 재료번호 저장위치 CLEAR:" +recGetCrnMtl.getField("STL_NO") ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
 
						
						if(schk.equals("N")){
							recSetStkLyr.setField("STL_NO"				, recGetCrnMtl.getField("STL_NO"));
			 
						}
						
						//적치단 테이블에 권상지시 CLEAR 업데이트 
						intRtnVal = ydStkLyrDao.updYdStklyr(recSetStkLyr, 2);
						//에러리턴
						if (intRtnVal < 1) {
							szMsg = "[Jsp Session : "+szOperationName+"] : 적치단 테이블에 권상지시 CLEAR 업데이트 실패" ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);				 

						}else {
							szMsg = "[Jsp Session : "+szOperationName+"] : 적치단 테이블에 권상지시 CLEAR 업데이트 성공" ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						}
						
						if(schk.equals("N")){
							recSetStkLyr.setField("STL_NO"				, "");
						}

 
						//적치단 테이블에 권상지시  업데이트 
						intRtnVal = ydStkLyrDao.updYdStklyr(recSetStkLyr, 0);
						//에러리턴
						if (intRtnVal < 1) {
							szMsg = "[Jsp Session : "+szOperationName+"] : 적치단 테이블에 권상지시  업데이트 실패" ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							outRecord.setField("RTN_CD" 	, "0");	
							outRecord.setField("RTN_MSG" 	, szMsg);	
							//return outRecord;

						}else{
						
							szMsg = "[Jsp Session : "+szOperationName+"] : 적치단 테이블에 권상지시  업데이트 성공" ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						}
						
						
						//다음 스케줄 작업 재료 레코드로가기 전 작업한 스케줄 재료를 삭제 처리 해준다.
											
						szMsg = "[Jsp Session : "+szOperationName+"] : 크레인스케줄 작업 재료 삭제처리" ;
						
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						recPara.setField("YD_CRN_SCH_ID"	, szYdSchId);
						recPara.setField("DEL_YN"			, "Y");
						recPara.setField("MODIFIER"			, szV_MODIFIER);
						recPara.setField("STL_NO"			, recGetCrnMtl.getField("STL_NO"));	
						
						intRtnVal = ydCrnWrkMtlDao.updYdCrnwrkmtl(recPara, 0);
						if(intRtnVal < 0){
							szMsg = "[Jsp Session : "+szOperationName+"] : 크레인스케줄 작업 재료 삭제처리시 ERROR" ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							outRecord.setField("RTN_CD" 	, "0");	
							outRecord.setField("RTN_MSG" 	, szMsg);	
							return outRecord;

						}else if(intRtnVal == 0 ){
							szMsg = "[Jsp Session : "+szOperationName+"] : 크레인스케줄 작업 재료 삭제처리 대상 없음" ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
						}else{
							szMsg = "[Jsp Session : "+szOperationName+"] : 크레인스케줄 작업 재료 삭제처리 성공" ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						}
						
						
						//------------------------------------------------------------------------------------------------
						// 스케줄  작업 재료 삭제시 저장품에 등록된 작업예약 ID, 스케줄 CD Clear   
						//------------------------------------------------------------------------------------------------
						
						szMsg = "[Jsp Session : "+szOperationName+"] : 저장품  작업예약 ID, 스케줄코드 CLEAR" ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
											
						recParaStock.setField("STL_NO"		, recGetCrnMtl.getField("STL_NO"));	
						recParaStock.setField("MODIFIER"	, szV_MODIFIER);
						recParaStock.setField("YD_WBOOK_ID"	, "" );
						recParaStock.setField("YD_SCH_CD"	, "" );
						
						ydUtils.displayRecord(szOperationName, recParaStock);
						
						intRtnVal = ydStockDao.updYdStock(recParaStock, 0);
						
						if(intRtnVal < 0){
							szMsg = "[Jsp Session : "+szOperationName+"] : 저장품  작업예약 ID, 스케줄코드 CLEAR ERROR" ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							outRecord.setField("RTN_CD" 	, "0");	
							outRecord.setField("RTN_MSG" 	, szMsg);	
							return outRecord;

						}else if(intRtnVal == 0 ){
							szMsg = "[Jsp Session : "+szOperationName+"] : 저장품  작업예약 ID, 스케줄코드 CLEAR 대상 없음" ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
						}else{
							szMsg = "[Jsp Session : "+szOperationName+"] : 저장품  작업예약 ID, 스케줄코드 CLEAR 성공" ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						}
					}
	
					//------------------------------------------------------------------------------------------------
					//	크레인스케줄 삭제처리 
					//------------------------------------------------------------------------------------------------
					
					szMsg = "[Jsp Session : "+szOperationName+"] : 크레인스케줄  삭제처리" ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					recPara.setField("DEL_YN"	, "Y");
					recPara.setField("MODIFIER"	, szV_MODIFIER);		
					
					ydUtils.displayRecord(szOperationName, recPara);

					intRtnVal = ydCrnSchDao.updYdCrnsch(recPara, 0);
					if (intRtnVal > 0) {
						szMsg = "[Jsp Session : "+szOperationName+"] : 크레인스케줄  삭제처리 완료" ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
					} else {					
						szMsg = "[Jsp Session : "+szOperationName+"] : 크레인스케줄  삭제처리 실패" ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, szMsg);	
						return outRecord;
					}
					//------------------------------------------------------------------------------------------------
					// 2009.12.14  (이현성)
					// 설비 상태를 진행상태에 맞도록 변경 시킨다. 
					// 해당 작업 예약 ID으로 스케줄 정보 조회시에 하나도 존재 하지 않을경우에
					// - 해당 스케줄 코드로 전체 스케줄 조회시 남은스케줄 첫번째 진행상태 정보로 UPDATE 
					// - 해당 스케줄 코드로 전체 스케줄 조회시 남아있는것이 없을경우는 대기상태로 UPDAT 해준다.
					//------------------------------------------------------------------------------------------------
					
					recEqpPara   = JDTORecordFactory.getInstance().create();
					rsCrnSchInfo = JDTORecordFactory.getInstance().createRecordSet("YD");
					recEqpPara.setField("YD_WBOOK_ID", szWbookId);
					
					intRtnVal = ydCrnSchDao.getYdCrnsch(recEqpPara, rsCrnSchInfo, 28);
					
					//설비 상태 UPDATE 유무 체크 FLAG 
					boolean lb_updEqpFlag  = false;
					boolean lb_updEqpFlag1  = false;
					
					if(intRtnVal < 0 ){
						
						szMsg = "[Jsp Session : "+szOperationName+"] : 해당 작업예약 정보에  남은 스케줄 조회시 ERROR" ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						
						
						lb_updEqpFlag  = false;
						
					} else if (intRtnVal ==0){
						
						szMsg = "[Jsp Session : "+szOperationName+"] : 해당 작업예약 정보에  남은 스케줄 정보가 없으므로 작업대기 상태로 UPDATE 합니다." ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						//해당 스케줄 코드로 같은 편성된 스케줄이 없는지 확인한다.(다른작업예약 ID가 편성되었을경우)
						recEqpPara   = JDTORecordFactory.getInstance().create();
						rsCrnSchInfo = JDTORecordFactory.getInstance().createRecordSet("YD");
//						recEqpPara.setField("YD_SCH_CD", szV_YD_SCH_CD);
						recEqpPara.setField("YD_EQP_ID", szEqpId);
						
	
						szMsg = "[Jsp Session : "+szOperationName+"] : 해당 설비로 작업중인 스케줄이 있는지 확인한다." ;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
//						intRtnVal = ydCrnSchDao.getYdCrnsch(recEqpPara, rsCrnSchInfo, 6);
						intRtnVal = ydCrnSchDao.getYdCrnsch(recEqpPara, rsCrnSchInfo, 16);
						if(intRtnVal < 0 ){
							szMsg="[Jsp Session : "+szOperationName+"] :남은 스케줄코드로 스케줄 조회시 ERROR";				
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							lb_updEqpFlag  = false;
						
						}  else if (intRtnVal == 0){
							szMsg="[Jsp Session : "+szOperationName+"] :남은 스케줄 정보가 없으므로 작업대기 상태로 UPDATE 합니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							szUpdEqpstat = YdConstant.YD_EQP_STAT_IDLE;
							lb_updEqpFlag   = true;
							lb_updEqpFlag1  = true;
						} else{
							szMsg="[Jsp Session : "+szOperationName+"] :해당 스케줄 코드에 스케줄 정보가 남아 있어 설비정보는 남은스케줄 첫번째 진행상태 정보로 UPDATE 합니다.";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							recEqpPara   = JDTORecordFactory.getInstance().create(); 
							rsCrnSchInfo.first();
							recEqpPara = rsCrnSchInfo.getRecord();
							szUpdEqpstat = ydDaoUtils.paraRecChkNull(recEqpPara, "YD_WRK_PROG_STAT");
							lb_updEqpFlag  	= true;
							lb_updEqpFlag1  = true;
						}
					
					} else{
						
						szMsg="[Jsp Session : "+szOperationName+"] 해당 작업예약 정보에 스케줄 정보가 남아 있어 설비정보는 UPDATE 하지 않습니다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
						lb_updEqpFlag  = false;
						
					}
					
					
					if(lb_updEqpFlag){
						
						recEqpPara   = JDTORecordFactory.getInstance().create();
						recEqpPara.setField("YD_EQP_ID", szEqpId);
						recEqpPara.setField("YD_EQP_STAT", szUpdEqpstat);
						recEqpPara.setField("MODIFIER",szV_MODIFIER);
						
						String szRtnMsg = "";
						szRtnMsg = YdCommonUtils.checkCrnStat(szEqpId);
						
						if(szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS)){

							szMsg="[Jsp Session : "+szOperationName+"] 해당 스케줄 크레인("+ szEqpId +") 설비상태 [" + szUpdEqpstat +"]로 변경 ";
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
							
							
							ydUtils.displayRecord(szOperationName, recEqpPara);
				
							intRtnVal = ydEqpDao.updYdEqp(recEqpPara, 0);
							
							
							if(intRtnVal < 0 ){
								szMsg="[Jsp Session : "+szOperationName+"] 해당 스케줄 크레인("+ szEqpId +") 설비상태 [" + szUpdEqpstat +"]로 변경시 ERROR ";
								ydUtils.putLog(szSessionName, szMethodName, szMsg,YdConstant.ERROR);
								outRecord.setField("RTN_CD" 	, "0");	
								outRecord.setField("RTN_MSG" 	, szMsg);	
								return outRecord;

								
							}else if(intRtnVal == 0){
								szMsg="[Jsp Session : "+szOperationName+"] 해당 스케줄 크레인("+ szEqpId +") 설비상태 [" + szUpdEqpstat +"]로 변경대상이 없습니다 ";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.WARNING);
								
							}else {
								szMsg="[Jsp Session : "+szOperationName+"] 해당 스케줄 크레인("+ szEqpId +") 설비상태 [" + szUpdEqpstat +"]로 변경 성공 ";
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
								
							}
						}
						if(lb_updEqpFlag1){
// 취소처리 후 작업지시 재 전송	
//							150904 hun 스케쥴 여러건 삭제시 작업지시는 마지막 한건만 나가게 수정 IS_LAST_SELECTED 값은 Fa에서 넘겨줌
							if("1".equals(ydDaoUtils.paraRecChkNull(msgRecord, "IS_LAST_SELECTED"))){
								if(szUpdEqpstat.equals(YdConstant.YD_EQP_STAT_IDLE)){
									recEqpPara   = JDTORecordFactory.getInstance().create();
									recEqpPara.setField("MSG_ID",           "YDYDJ643");
	//SJH03004								recEqpPara.setField("JMS_TC_CD"            	, "YDYDJ643");
	
									recEqpPara.setField("YD_EQP_ID",        szEqpId);
									recEqpPara.setField("YD_WRK_PROG_STAT", szUpdEqpstat);
									EJBConnector ydEjbCon = new EJBConnector("default", this);
									ydEjbCon.trx("CoilCraneLdHdSeEJB", "procY5CrnWrkOrdReq", recEqpPara);
	//								ydDelegate.sendMsg(recEqpPara);	
								
								}
							}
						}
					}
				}
				
			}
			
			
			
	//		 작업 예약 /재료 삭제
			// 크레인 작업 재지시를 위하여  설비 아이디 , 스케줄 코드를 넘겨준다.
			
//			msgRecord.setField("YD_EQP_ID", szEqpId);
//			msgRecord.setField("YD_SCH_CD", szV_YD_SCH_CD);
			
			outRecord.setField("YD_CRN_SCH_ID" 	, szV_YD_CRN_SCH_ID);	
			outRecord.setField("YD_EQP_ID" 		, szEqpId);	
			outRecord.setField("YD_SCH_CD" 		, szV_YD_SCH_CD);	
			outRecord.setField("MODIFIER"		, szV_MODIFIER);
				
			
			szMsg="[Jsp Session : "+szOperationName+"] :작업예약 삭제 호출완료";				
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
//			szMsg = this.delWBook(msgRecord);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		outRecord.setField("RTN_CD" 	, "1");	
		outRecord.setField("RTN_MSG" 	, szMsg);	
		return outRecord;
		

	
	}// end of ()
	
	/**
	 * 오퍼레이션명 : 작업예약 삭제
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord (YD_CRN_SCH_ID)
	 * @return
	 * @throws JDTOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord delWookBook(JDTORecord msgRecord)throws JDTOException  {
		
		
		YdCrnSchDao  ydCrnSchDao	= new  YdCrnSchDao();
		
		//리턴레코드셋
		JDTORecordSet rsRtnVal 	= JDTORecordFactory.getInstance().createRecordSet("temRs");
		JDTORecordSet rsResult1	= JDTORecordFactory.getInstance().createRecordSet("temRs");
		
		JDTORecord recPara1   	= JDTORecordFactory.getInstance().create();
		JDTORecord recPara   	= JDTORecordFactory.getInstance().create();
		JDTORecord recCheck  	= JDTORecordFactory.getInstance().create();
		JDTORecord inRec  	 	= JDTORecordFactory.getInstance().create();
		JDTORecord inRecord	 	= JDTORecordFactory.getInstance().create();
		JDTORecord outRecord 	= JDTORecordFactory.getInstance().create(); // 
		JDTORecord outRecord1 	= JDTORecordFactory.getInstance().create(); // 

		//파라미터 스크링 변수
		String szV_YD_CRN_SCH_ID    = null;	
		String szV_YD_WBOOK_ID     	= null;
		String szOperationName		= "작업예약 삭제";
		
		//리턴값
		int intRtnVal 				= 0;
		String szMsg				= "";
		String szMethodName			= "delWookBook";
		
		// 크레인 작업 지시 EJB Call 시 필요한 변수
		String szEqpId 				= "";
		String szV_YD_SCH_CD 		= "";
		String szYD_USER_ID 		= "";
		String szYD_SCH_CD 			= ""; 
		String szSTL_NO 			= "";

		YdWrkbookDao ydWrkbookDao	= new YdWrkbookDao();
		YdStkLyrDao ydStkLyrDao    	= new YdStkLyrDao();		
		//들어온데이타  display 
		
		szMsg="작업예약 삭제 처리 기능 시작";
	    ydUtils.putLog(szSessionName, szMethodName, szMsg,YdConstant.DEBUG);
		
		ydUtils.displayRecord("작업예약 삭제 처리 기능 IN-PARA", msgRecord);

		szYD_USER_ID = ydDaoUtils.paraRecChkNull(msgRecord, "MODIFIER");
		
		try{		
		
			if (ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID").equals("")) {		

				szMsg="스케줄 ID 정보가 없어서 작업예약 삭제처리를 하지 못하였습니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);

				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
			}
		
			szV_YD_CRN_SCH_ID  	= ydDaoUtils.paraRecChkNull(msgRecord, "YD_CRN_SCH_ID");
			szEqpId 			= ydDaoUtils.paraRecChkNull(msgRecord, "YD_EQP_ID"); 
			szV_YD_SCH_CD 		= ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			
			
			//파라미터 레코드 setting
			recPara.setField("YD_CRN_SCH_ID", szV_YD_CRN_SCH_ID);
			
			rsRtnVal = JDTORecordFactory.getInstance().createRecordSet("temRs");
			intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, rsRtnVal, 0);
			
			if (intRtnVal < 1 ){
				szMsg="해당크레인 스케줄이 존재하지않습니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
			}
			
			rsRtnVal.first();
			recCheck = rsRtnVal.getRecord();
			szV_YD_WBOOK_ID = ydDaoUtils.paraRecChkNull(recCheck, "YD_WBOOK_ID");
		
			
			if (szV_YD_WBOOK_ID.equals("")){
				szMsg="해당크레인 스케줄에 작업예약 정보가 존재하지않습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
			}
			
			rsRtnVal = JDTORecordFactory.getInstance().createRecordSet("temRs");
			intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, rsRtnVal, 36);
			
			if (intRtnVal < 0){
				szMsg = YdConstant.RETN_CD_FAILURE;
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
			} else  if (intRtnVal > 0){
				szMsg = "스케줄 정보가 남아 있습니다.";
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
			}
			
//C증설		
			if( szV_YD_SCH_CD.equals("HBKE03LM")||
				szV_YD_SCH_CD.equals("HAKE03LM")||
				szV_YD_SCH_CD.equals("HBFE03LM")||			
				szV_YD_SCH_CD.equals("HCKE03LM")||
				szV_YD_SCH_CD.equals("HCFE03LM")||
				szV_YD_SCH_CD.equals("HDFE03LM")||
				szV_YD_SCH_CD.equals("HEDE03LM")||
				szV_YD_SCH_CD.equals("HFFE03LM")||
				szV_YD_SCH_CD.equals("HGFE03LM")||
				szV_YD_SCH_CD.equals("HHKE03LM")||
				
				//추출
				szV_YD_SCH_CD.equals("HBKD03LM")|| 
				szV_YD_SCH_CD.equals("HAKD03LM")||  
				szV_YD_SCH_CD.equals("HCKD03LM")||   
				szV_YD_SCH_CD.equals("HEDD03LM")||  
				szV_YD_SCH_CD.equals("HHKD03LM")
				) {
				
				//take out 일 경우 from 저장위치 clear
				recPara  = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID", 		szV_YD_WBOOK_ID);
				
				rsResult1 = JDTORecordFactory.getInstance().createRecordSet("temRs");
				/*com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookYdWrkbookMtlByWrkbookID*/
				intRtnVal = ydWrkbookDao.getYdWrkbook(recPara, rsResult1, 17);
				if(intRtnVal > 0 ){
					rsResult1.first();
					recPara1 = JDTORecordFactory.getInstance().create();
					recPara1 = rsResult1.getRecord();
					szSTL_NO = ydDaoUtils.paraRecChkNull(recPara1, "STL_NO"); 
					
					JDTORecordSet   outRecSet1   = JDTORecordFactory.getInstance().createRecordSet("YD");
					inRecord = JDTORecordFactory.getInstance().create();
					inRecord.setField("STL_NO",   szSTL_NO);
		//getYdStklyr24
					/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdStkPosInfo_PIDEV*/
					intRtnVal = ydStkLyrDao.getYdStklyr(inRecord, outRecSet1, 24);
					if (intRtnVal > 0) {
						//적치되어 있는 정보 삭제처리
						outRecSet1.first();
						outRecord1 = outRecSet1.getRecord();
						recPara = JDTORecordFactory.getInstance().create();
						
						//적치단 재료상태가 적치 가능이면 재료 등록
						//적치단 테이블 업데이트
						//적치열구분 = 설비ID
						recPara.setField("YD_STK_COL_GP", 	    ydDaoUtils.paraRecChkNull(outRecord1, "YD_STK_COL_GP"));
						recPara.setField("YD_STK_BED_NO", 	    ydDaoUtils.paraRecChkNull(outRecord1, "YD_STK_BED_NO"));
						recPara.setField("YD_STK_LYR_NO", 	    ydDaoUtils.paraRecChkNull(outRecord1, "YD_STK_LYR_NO"));
						recPara.setField("MODIFIER", 		    szYD_USER_ID);
						recPara.setField("YD_STK_LYR_MTL_STAT", "E");
						recPara.setField("STL_NO", 			    "");
						
						//업데이트 실행
						intRtnVal = ydStkLyrDao.updYdStklyr(recPara, 0);
						//에러리턴
						if (intRtnVal < 1) {
							szMsg = "[Jsp Session : "+szOperationName+"] : 적치단 테이블에 권상지시 CLEAR 업데이트 실패" ;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						}
					} 
				}
				
			}
					
			
			//------------------------------------------------------------------------------------------------
			//	차량 / 대차 작업과 관계있는 작업 Clear 
			//------------------------------------------------------------------------------------------------
			
			String szRtnMsg = "";
			// 차량 또는 대차 스케줄에 있는 작업예약 ID를 Clear
			inRec    = JDTORecordFactory.getInstance().create();
			inRec.setField("MODIFIER"	, szYD_USER_ID);
			inRec.setField("YD_WBOOK_ID", szV_YD_WBOOK_ID);
			
			
			szRtnMsg = yddatautil.delWBookBefoCarOrTCar(inRec);
			
			if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				szMsg = "[Jsp Session : "+ szOperationName +"] 작업예약["+szV_YD_WBOOK_ID+"] 대차/차량 스케줄 Clear성공 ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
			}else if(szRtnMsg.equals(YdConstant.RETN_CD_FAILURE)){
				szMsg = "[Jsp Session : "+ szOperationName +"] 작업예약["+szV_YD_WBOOK_ID+"] 대차/차량 스케줄 Clear 실패 ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
			}else{
				szMsg = "[Jsp Session : "+ szOperationName +"] 작업예약["+szV_YD_WBOOK_ID+"]  " + szRtnMsg ;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}
	
			//------------------------------------------------------------------------------------------------
			//	작업예약/재료 삭제
			//------------------------------------------------------------------------------------------------
			
			szRtnMsg = YdCommonUtils.delYdWrkbookNMtl(szV_YD_WBOOK_ID, szYD_USER_ID);
			
			if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				szMsg = "[Jsp Session : "+ szOperationName +"] 작업예약["+szV_YD_WBOOK_ID+"]삭제 성공";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}else{
				szMsg = "[Jsp Session : "+ szOperationName +"] 작업예약["+szV_YD_WBOOK_ID+"]삭제 실패 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
			}
			
	
			String szYD_WBOOK_ID 	= szV_YD_WBOOK_ID;
			JDTORecordSet outRecSet = null;
			//준비스케줄 원복 
			szMsg = "[JSP Session : "+szOperationName+"] 삭제된 작업예약ID[" + szYD_WBOOK_ID + "]와 연관된 준비스케줄 조회 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			outRecSet       = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			recPara         = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
			
			YdPrepSchDao ydPrepSchDao = new YdPrepSchDao();
			
			intRtnVal = ydPrepSchDao.getYdPrepsch(recPara, outRecSet, 8);
			
			if( intRtnVal < 0  ) {
				szMsg = "[JSP Session : "+szOperationName+"] 삭제된 작업예약ID[" + szYD_WBOOK_ID + "]와 연관된 준비스케줄 조회 시 오류발생 : 반환값 - " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}else if( intRtnVal == 0  ) {
				szMsg = "[JSP Session : "+szOperationName+"] 삭제된 작업예약ID[" + szYD_WBOOK_ID + "]와 연관된 준비스케줄 조회 시 존재하지 않음 ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			}else{
				
				outRecSet.first();
				recPara = outRecSet.getRecord();
				
				String szYD_PREP_SCH_ID = ydDaoUtils.paraRecChkNull(recPara, "YD_PREP_SCH_ID");
				
				szMsg = "[JSP Session : "+szOperationName+"] 삭제된 작업예약ID[" + szYD_WBOOK_ID + "]와 연관된 준비스케줄[" + szYD_PREP_SCH_ID + "]이 존재하므로 DEL_YN => N으로 설정 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				YdPrepMtlDao ydPrepMtlDao = new YdPrepMtlDao();
				
				recPara	= JDTORecordFactory.getInstance().create();
				recPara.setField("YD_PREP_SCH_ID",   	szYD_PREP_SCH_ID);
				recPara.setField("DEL_YN",   			"N");
				recPara.setField("MODIFIER",   			szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
				//준비재료
				intRtnVal = ydPrepMtlDao.uptDelYdPrepmtlByPrepSchId(recPara);
				
				//준비스케줄
				recPara.setField("YD_WBOOK_ID",   		"");
				intRtnVal =  ydPrepSchDao.updDelYdPrepsch(recPara);
				
				szMsg = "[JSP Session : "+szOperationName+"] 삭제된 작업예약ID[" + szYD_WBOOK_ID + "]와 연관된 준비스케줄[" + szYD_PREP_SCH_ID + "]과 준비재료 DEL_YN => N으로 설정 성공";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
		

			
		//설비 ID 정보와 스케줄 코드가 들어왔을때만 실행한다.
			if (   szEqpId.equals("")  || szV_YD_SCH_CD.equals("")) {
				
				szMsg = "[JSP Session : "+szOperationName+"] 설비 ID 정보" + szEqpId + "스케줄 코드 [" + szV_YD_SCH_CD + "]" 
				+ "중 누락된 정보가 발생하여 해당 크레인 작업지시를 호출하지 않고 마칩니다";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				outRecord.setField("CRANE_SND_YN" 	, "N");	
				outRecord.setField("RTN_CD" 		, "1");	
				outRecord.setField("RTN_MSG" 		, szMsg);	
				return outRecord;
			
			} else {
				
				outRecord.setField("CRANE_WR_SND_YN" 	, "Y");	
				outRecord.setField("MSG_ID" 			, "YDYDJ643");	
				outRecord.setField("YD_EQP_ID" 			, szEqpId);	
				outRecord.setField("YD_WRK_PROG_STAT"	, "W");	
				outRecord.setField("YD_SCH_CD" 			, szV_YD_SCH_CD);	
				outRecord.setField("RTN_CD" 			, "1");	
				outRecord.setField("RTN_MSG" 			, szMsg);	
				return outRecord;			
			}
		
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	
	}
 	
	/**
	 *  크레인작업예약관리 - 작업예약 삭제(예약번호로 삭제처리 함)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public String delYdWrkbook(JDTORecord [] inDto) throws DAOException {
		String 		szOperationName			= "작업예약삭제";
		String		szMethodName			= "delYdWrkbook";
		YdWrkbookDao ydWrkbookDao			= new YdWrkbookDao();
		YdStkLyrDao ydStkLyrDao    			= new YdStkLyrDao();
		String		szMsg					= null;
		String		szRtnMsg				= null;
		
		JDTORecord	inRecord 				= JDTORecordFactory.getInstance().create();
		JDTORecord	outRecord1 				= JDTORecordFactory.getInstance().create();
		
		JDTORecord	recPara					= null;
		JDTORecord	recPara1				= null;
		JDTORecordSet rsResult				= null;
		JDTORecordSet rsResult1				= null;
		
		String		szYD_WBOOK_ID			= null;
		String		szYD_USER_ID			= null;
		String      szYD_SCH_CD 			= "";
		String      szSTL_NO 				= "";

		int       	intRtnVal				= 0;
		
		try {
			szMsg = "[Jsp Session : "+ szOperationName +"] 메소드 시작 - 삭제대상재 건수["+inDto.length+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			recPara  = JDTORecordFactory.getInstance().create();
			for( int i = 0 ; i < inDto.length; i++ ) {
				//------------------------------------------------------------------------------------------------
				//	크레인스케줄이 존재하는 지 먼저 확인
				//------------------------------------------------------------------------------------------------
				szYD_WBOOK_ID = ydDaoUtils.paraRecChkNull(inDto[i], "YD_WBOOK_ID");
				szYD_USER_ID  = ydDaoUtils.paraRecChkNull(inDto[i], "YD_USER_ID");
				
				recPara.setField("YD_WBOOK_ID", 		szYD_WBOOK_ID);
				
				rsResult	= JDTORecordFactory.getInstance().createRecordSet("");
				
				szRtnMsg 	= DaoManager.getYdCrnsch(recPara, rsResult, 28);
				
				if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) && !szRtnMsg.equals(YdConstant.RETN_CD_NOTEXIST) ) {
					szMsg = "[Jsp Session : "+ szOperationName +"] 작업예약["+szYD_WBOOK_ID+"]으로 크레인스케줄 조회 시 오류발생 : " + szRtnMsg;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					continue;
				}else{
					if( rsResult.size() > 0 ) {
						szMsg = "[Jsp Session : "+ szOperationName +"] 작업예약["+szYD_WBOOK_ID+"]으로 크레인스케줄이 존재하므로 작업예약을 삭제하지 않습니다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						throw new DAOException(szMsg);						 
					}else{
						szMsg = "[Jsp Session : "+ szOperationName +"] 작업예약["+szYD_WBOOK_ID+"]으로 크레인스케줄이 존재하지 않으므로 작업예약 삭제 가능합니다.";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
				}
				//------------------------------------------------------------------------------------------------

//				sjh 추가			
				//take out 일 경우 from 저장위치 clear
				recPara  = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID", 		szYD_WBOOK_ID);
				
				rsResult1 = JDTORecordFactory.getInstance().createRecordSet("rsYdWBook");
				/*com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookYdWrkbookMtlByWrkbookID*/
				intRtnVal = ydWrkbookDao.getYdWrkbook(recPara, rsResult1, 17);
				if(intRtnVal > 0 ){
					rsResult1.first();
					recPara1 = JDTORecordFactory.getInstance().create();
					recPara1 = rsResult1.getRecord();
					szYD_SCH_CD	= ydDaoUtils.paraRecChkNull(recPara1, "YD_SCH_CD"); 
					szSTL_NO 	= ydDaoUtils.paraRecChkNull(recPara1, "STL_NO"); 
									
//C증설					
					if( szYD_SCH_CD.equals("HBKE03LM")||
						szYD_SCH_CD.equals("HAKE03LM")||
						szYD_SCH_CD.equals("HBFE03LM")||
						szYD_SCH_CD.equals("HCKE03LM")||
						szYD_SCH_CD.equals("HCFE03LM")||
						szYD_SCH_CD.equals("HDFE03LM")||
						szYD_SCH_CD.equals("HEDE03LM")||
						szYD_SCH_CD.equals("HFFE03LM")||
						szYD_SCH_CD.equals("HGFE03LM")||
						szYD_SCH_CD.equals("HHKE03LM")||
						//추출
						szYD_SCH_CD.equals("HBKD03LM")|| 
						szYD_SCH_CD.equals("HAKD03LM")||  
						szYD_SCH_CD.equals("HCKD03LM")||   
						szYD_SCH_CD.equals("HEDD03LM")||  
						szYD_SCH_CD.equals("HHKD03LM")
						
					  ) {
						
						JDTORecordSet   outRecSet   = JDTORecordFactory.getInstance().createRecordSet("YD");
						inRecord = JDTORecordFactory.getInstance().create();
						inRecord.setField("STL_NO",   szSTL_NO);
			//getYdStklyr24
						/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdStkPosInfo_PIDEV*/
						intRtnVal = ydStkLyrDao.getYdStklyr(inRecord, outRecSet, 24);
						if (intRtnVal > 0) {
							//적치되어 있는 정보 삭제처리
							outRecSet.first();
							outRecord1 = outRecSet.getRecord();
							recPara = JDTORecordFactory.getInstance().create();
							
							//적치단 재료상태가 적치 가능이면 재료 등록
							//적치단 테이블 업데이트
							//적치열구분 = 설비ID
							recPara.setField("YD_STK_COL_GP", 	    ydDaoUtils.paraRecChkNull(outRecord1, "YD_STK_COL_GP"));
							recPara.setField("YD_STK_BED_NO", 	    ydDaoUtils.paraRecChkNull(outRecord1, "YD_STK_BED_NO"));
							recPara.setField("YD_STK_LYR_NO", 	    ydDaoUtils.paraRecChkNull(outRecord1, "YD_STK_LYR_NO"));
							recPara.setField("MODIFIER", 		    szYD_USER_ID);
							recPara.setField("YD_STK_LYR_MTL_STAT", "E");
							recPara.setField("STL_NO", 			    "");
							
							//업데이트 실행
							intRtnVal = ydStkLyrDao.updYdStklyr(recPara, 0);
							//에러리턴
							if (intRtnVal < 1) {
								szMsg = "[Jsp Session : "+szOperationName+"] : 적치단 테이블에 권상지시 CLEAR 업데이트 실패" ;
								ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							}
						} 
					}
				}
		
				
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
	 *  보급취소등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecord updcoilYdLineWrCancelPp(JDTORecord inRecord) throws DAOException {
		String 		szOperationName			= "보급취소등록";
		String		szMethodName			= "updcoilYdLineWrCancelPp";
		String		szMsg					= null;
		int intRtnVal						= 0;
		JDTORecord	recPara					= null;
		//JDTORecord	recPara					= null;
		JDTORecordSet rsResult1				= null;
		JDTORecordSet rsResult2				= null;
		JDTORecord outRecord = JDTORecordFactory.getInstance().create(); // 
		String		sYD_CRN_SCH_ID			= null;
		String		sYD_WBOOK_ID			= null;
		String sYD_SCH_CD					= "";
		YdCrnSchDao		ydCrnSchDao		= new YdCrnSchDao();
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		
		try {
			szMsg = "[Jsp Session : "+ szOperationName +"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			rsResult1 = JDTORecordFactory.getInstance().createRecordSet("");
			/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getCrnSchByStlNo*/
			intRtnVal = ydCrnSchDao.getYdCrnsch(inRecord, rsResult1, 52);
			if( intRtnVal  > 0 ) {
				rsResult1.first();
				recPara = rsResult1.getRecord();
				sYD_CRN_SCH_ID 	= ydDaoUtils.paraRecChkNull(recPara, "YD_CRN_SCH_ID");
				sYD_SCH_CD 		= ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_CD");

			}	
			rsResult2 = JDTORecordFactory.getInstance().createRecordSet("");
			/* com.inisteel.cim.yd.dao.ydwrkbookmtldao.YdWrkbookmtlDao.getYdWrkbookmtlSTLNO */
			intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(inRecord, rsResult2, 2);
			if( intRtnVal  > 0 ) {
				rsResult2.first();
				recPara = rsResult2.getRecord();
				sYD_WBOOK_ID = ydDaoUtils.paraRecChkNull(recPara, "YD_WBOOK_ID");
			}	

			szMsg = "[Jsp Session : "+ szOperationName +"] 메소드 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			outRecord.setField("YD_CRN_SCH_ID" 	, sYD_CRN_SCH_ID);	
			outRecord.setField("YD_WBOOK_ID" 	, sYD_WBOOK_ID);	
			outRecord.setField("YD_SCH_CD" 		, sYD_SCH_CD);	
			outRecord.setField("RTN_CD" 		, "1");	
			return outRecord;			
			
		} catch (Exception e) {
			szMsg = "[Jsp Session : "+ szOperationName +"] 예외발생 - 메세지 : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(YdConstant.RETN_CD_FAILURE);
		}
	}
	
	/**
	 * (작업)크레인 변경 
	 * 송정현
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return Boolean
	 * @throws DAOException
	 */
	public JDTORecord  wrkCrnChange(JDTORecord[] recMsg) throws DAOException {
		JDTORecord recPara = null;
		JDTORecord recTemp = null;	
		JDTORecord recTemp2 = null;
		JDTORecord recEqpInfo = null;
		JDTORecord recSchInfo = null;
		JDTORecord recDelPara = null;
		 
	 	JDTORecordSet rsrstDataSch = null;
	 	JDTORecordSet rsEqpInfo = null;
	 	JDTORecordSet rsSchInfo = null;
	 	JDTORecord outRecord = JDTORecordFactory.getInstance().create(); // 
	 	int intGp = 0;

    	YdCrnSchDao  ydCrnSchDao 	= new YdCrnSchDao ();
    	YdWrkbookDao ydWrkbookDao	= new YdWrkbookDao();
    	YdSchRuleDao ydSchRuleDao 	= new YdSchRuleDao();
    	YdEqpDao ydEqpDao = new YdEqpDao();
    	
    	
    	
    	String szWbookId			= null;
    	String szWrkCrn 			= null;
    	String szAltCrn				= null;
    	String szChgCrn 			= null;
    	String szEqpId				= null;
    	String szydEqpStat			= null;
    	String szWrkProgStat2		= null;
    	int    intWrkCrnPrior		= 0;
    	int    intAltCrnPrior 		= 0;
    	int    intCrnPrior			= 0;
    	
    	String szMethodName="wrkCrnChange";		
    	String szOperationName = "(작업)크레인 변경";
    	String szLogMsg = "";
    	
    	String szYD_GP				= "";
    	String szRtnValue = YdConstant.RETN_CD_SUCCESS;
    	
    	String szJMS_TC_CD = null;
    	String szWrkProgStat = "";
    	String szMODIFIER 	="";
    	String szYD_SCH_CD  ="";
    	String szchgydEqpStat  ="";
    	String szchkWrkProgStat  ="W";
    	String szEqpAutoCrnMode = "";
    	String szEqpAutoCrnYN = "";
    	
    	boolean sbSendFlag  = false; 
    	int icnt =0;
    	
    	EJBConnector ejbConn = null;
	 	
	 	
		try{

			szLogMsg = "JSP-SESSION [" + szOperationName + " ]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			szEqpId = recMsg[0].getFieldString("YD_EQP_ID");
			szWrkProgStat = recMsg[0].getFieldString("YD_WRK_PROG_STAT_CD");
			//설비 상태 가져 오기********************************************************
			recEqpInfo = JDTORecordFactory.getInstance().create();
			rsEqpInfo = JDTORecordFactory.getInstance().createRecordSet("YD");
			recEqpInfo.setField("YD_EQP_ID" , szEqpId);
			// 해당 설비 szChgCrn 로 설비 정보 조회
			intGp = ydEqpDao.getYdEqp(recEqpInfo , rsEqpInfo , 0);

			if (intGp > 0) {
				rsEqpInfo.first();
				recEqpInfo = rsEqpInfo.getRecord();
				//설비 상태
				szydEqpStat= ydDaoUtils.paraRecChkNull(recEqpInfo, "YD_EQP_STAT");
//				AutoCrn 상태
				szEqpAutoCrnMode = ydDaoUtils.paraRecChkNull(recEqpInfo, "YD_EQP_AUTO_CRN_MODE");
//				AutoCrn 여부
				szEqpAutoCrnYN   = ydDaoUtils.paraRecChkNull(recEqpInfo, "YD_EQP_WRK_MODE2");
				
				if(YdConstant.YD_EQP_STAT_UP_CMPL.equals(szWrkProgStat)){
					szRtnValue = "크레인 ["+  szEqpId +"]가 권상완료 상태에서는 상태를 변경 할 수 없습니다.";
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szRtnValue);	
					return outRecord;
					
				}else if(YdConstant.YD_EQP_STAT_DN_WO.equals(szWrkProgStat)){
					szRtnValue = "크레인 ["+  szEqpId +"]가 권하지시 상태에서는 상태를 변경 할 수 없습니다.";
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szRtnValue);	
					return outRecord;
					
				}else if(YdConstant.YD_EQP_STAT_DN_CMPL.equals(szWrkProgStat)){
					szRtnValue = "크레인 ["+  szEqpId +"]가 권하완료 상태에서는 상태를 변경 할 수 없습니다.";
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szRtnValue);	
					return outRecord;
//				150918 hun Auto크레인시에 일시정지(4) 상태만 가능
				}else if ("A".equals(szEqpAutoCrnYN) || "R".equals(szEqpAutoCrnYN)) {
					if (!"W".equals(szWrkProgStat) && !"S".equals(szWrkProgStat) ) {
						if (!"4".equals(szEqpAutoCrnMode) && !"B".equals(szydEqpStat) ) {
							szRtnValue = "무인크레인 [" + szEqpId + "]이 일시정지이거나 고장상태가 아니면 상태를 변경 할 수 없습니다.";
							outRecord.setField("RTN_CD" , "0");
							outRecord.setField("RTN_MSG" , szRtnValue);
							return outRecord;
						}
					}
				}
			}
			//*************************************************************************
			
			
			
			//변경 크레인을 가져 온다********************************************************
			rsrstDataSch =  JDTORecordFactory.getInstance().createRecordSet("YD");
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_SCH_CD",recMsg[0].getFieldString("YD_SCH_CD")); 
			intGp = ydSchRuleDao.getYdSchrule(recPara, rsrstDataSch, 0);
			
			if (intGp < 1 ){
				//선택된 스케줄 기준데이터 정보에 해당 스케줄 정보가 없을경우
 				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, "선택된 스케줄 기준데이터 정보에 해당 스케줄 정보가 없음");	
				return outRecord;				
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
			//****************************************************************************

			
			
			//변경 크레인 설비상태 체크*********************************************************
			// 1-2 변경 할 크레인이 선택되어 있으면 설비 정보를 조회한 후 고장 또는 OFF-LINE 일 경우 변경 할 수 없다고 판단하고 RETURN 한다.
			
			recEqpInfo = JDTORecordFactory.getInstance().create();
			rsEqpInfo = JDTORecordFactory.getInstance().createRecordSet("YD");
			recEqpInfo.setField("YD_EQP_ID", szChgCrn);
			
			//해당 설비  szChgCrn 로 설비 정보 조회 			
			intGp = ydEqpDao.getYdEqp(recEqpInfo, rsEqpInfo, 0);
			
			if(intGp > 0 ){
				rsEqpInfo.first();
				recEqpInfo = rsEqpInfo.getRecord();
				szchgydEqpStat = ydDaoUtils.paraRecChkNull(recEqpInfo, "YD_EQP_STAT");
				if(szchgydEqpStat.equals(YdConstant.YD_EQP_STAT_BREAK)){
					// 설비 상태가 고장일 경우 
					szRtnValue = "변경 설비["+ szChgCrn+"]가 고장 상태여서 상태를 변경 할 수 없습니다.";
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szRtnValue);	
					return outRecord;
				}
				
				if(ydDaoUtils.paraRecChkNull(recEqpInfo, "YD_EQP_WRK_MODE").equals(YdConstant.YD_EQP_WRK_MODE_OFF_LINE)){
					// 설비 상태가 OFF_LINE 일 경우 
					szRtnValue = "변경 설비["+ szChgCrn+"]가 OFF_LINE 이기때문에 상태를 변경 할 수 없습니다.";
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szRtnValue);	
					return outRecord;
				}
				
				//권하이후 부터 크레인 변경이 가능 합니다.
				if(szchgydEqpStat.equals(YdConstant.YD_EQP_STAT_DN_WO)
					||szchgydEqpStat.equals(YdConstant.YD_EQP_STAT_DN_CMPL)	){						
					szRtnValue = "변경 설비["+ szChgCrn+"]가 권하중인 상태라 변경 할 수 없습니다.";						
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szRtnValue);	
					return outRecord;					
				}	
				
			}else{
				//해당 설비가 존재 하지 않습니다.
				
				szRtnValue = "해당 설비["+ szChgCrn+"]가 존재 하지 않습니다";
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szRtnValue);	
				return outRecord;
				
			}
			//****************************************************************************
			
			
			//##############################################################################################	
			for(int x=0 ; x < recMsg.length ;x++){
				sbSendFlag = false;
				szYD_GP = recMsg[x].getFieldString("YD_SCH_CD").substring(0 , 1);				
				szMODIFIER = recMsg[x].getFieldString("MODIFIER");

				
				// 크레인스케줄 변경 가능 상태 체크 **************************************************
				rsrstDataSch = JDTORecordFactory.getInstance().createRecordSet("YD");				
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID" , ydDaoUtils.paraRecChkNull(recMsg[x] , "YD_CRN_SCH_ID"));
				intGp = ydCrnSchDao.getYdCrnsch(recPara , rsrstDataSch , 0);

				if (intGp < 1) {
					// 스케줄 정보가 존재 하지 않을 경우
					szRtnValue = "스케줄 [ " + ydDaoUtils.paraRecChkNull(recMsg[x] , "YD_CRN_SCH_ID") + "정보가 존재 하지 않습니다.";
					outRecord.setField("RTN_CD" , "0");
					outRecord.setField("RTN_MSG" , szRtnValue);
					return outRecord;
					// return szRtnValue ;

				}
				
				rsrstDataSch.first();
				recTemp = JDTORecordFactory.getInstance().create();
				recTemp = rsrstDataSch.getRecord();
				
				szWbookId = ydDaoUtils.paraRecChkNull(recTemp , "YD_WBOOK_ID"); // 작업예약 ID				
				szWrkProgStat = ydDaoUtils.paraRecChkNull(recTemp , "YD_WRK_PROG_STAT"); // 설비 상태
				
				if(szWrkProgStat.equals(YdConstant.YD_EQP_STAT_UP_CMPL)){
					szRtnValue = "스케줄 ["+  ydDaoUtils.paraRecChkNull(recMsg[x], "YD_CRN_SCH_ID") +"]가 권상완료 상태에서는 상태를 변경 할 수 없습니다.";
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szRtnValue);	
					return outRecord;
					
				}else if(szWrkProgStat.equals(YdConstant.YD_EQP_STAT_DN_WO)){
					szRtnValue = "스케줄 ["+  ydDaoUtils.paraRecChkNull(recMsg[x], "YD_CRN_SCH_ID") +"]가 권하지시 상태에서는 상태를 변경 할 수 없습니다.";
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szRtnValue);	
					return outRecord;
				}else if(szWrkProgStat.equals(YdConstant.YD_EQP_STAT_DN_CMPL)){
					szRtnValue = "스케줄 ["+  ydDaoUtils.paraRecChkNull(recMsg[x], "YD_CRN_SCH_ID") +"]가 권하완료 상태에서는 상태를 변경 할 수 없습니다.";
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szRtnValue);	
					return outRecord;
				}else if(szWrkProgStat.equals(YdConstant.YD_EQP_STAT_UP_WO)){
					szchkWrkProgStat =szWrkProgStat;
				}
				//***************************************************************************
				
				

				//작업예약 수정*****************************************************************
				// 2. 선택된 크레인 스케줄 작업예약 ID에  대체 설비 ID 와 우선순위 를 UPDATE 한다.			
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID",szWbookId);
				recPara.setField("YD_SCH_PRIOR", new Integer(intCrnPrior));
				recPara.setField("MODIFIER",szMODIFIER);
				intGp = ydWrkbookDao.updYdWrkbook(recPara, 0);
				if (intGp <1){
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, "선택된 크레인 스케줄 작업예약 ID : "+szWbookId+"에   우선순위 ("+intCrnPrior+") 를 UPDATE 중 ERROR");	
					return outRecord;
				}
				//****************************************************************************
				
				
				
				// 3. 작업예약 ID 로 현재 편성된 스케줄 ID [] 를 구한다.				
				recPara = JDTORecordFactory.getInstance().create();
				rsrstDataSch =  JDTORecordFactory.getInstance().createRecordSet("YD");
				
				recPara.setField("YD_WBOOK_ID",szWbookId);
				recPara.setField("YD_WRK_PROG_STAT","W");
				
				// 기존쿼리는 W 이상태만 체크하였으나 지금은 1,W 상태를 조회한다.
				intGp = ydCrnSchDao.getYdCrnsch(recPara, rsrstDataSch, 23);
				
				
				if (intGp <1 ){					
					// 해당 작업 ID 에 편성된 스케줄 정보가 없을경우  
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, "해당작업 ID 에 편성된 스케줄 정보가 존재하지 않음");	
					return outRecord;
				}					
				
				//크레인 스케줄 정보 변경
				rsrstDataSch.first();
				
				do
				{	
					// 4. 편성된 스케줄 ID [] 에  대체 크레인 설비 ID와 우선순위를 편성
					recTemp = JDTORecordFactory.getInstance().create();
					recPara = JDTORecordFactory.getInstance().create();					
					recTemp = rsrstDataSch.getRecord();
					szWrkProgStat = ydDaoUtils.paraRecChkNull(recTemp , "YD_WRK_PROG_STAT"); // 설비 상태
					
					//*************************************************************
					// 150911 hun Auto크레인 경우 작업지시 취소 필수요청 ( 유무인 동시 적용 )
					//크레인 변경 후 원 크레인 정보는 작업대기 상태로 세팅하기전 현재 선택 작업지시 취소 전문 발송
					//*************************************************************
					
					// 화면에 check된 스케쥴이 여러건일때 기존 선택된 스케쥴만 전송  
					szLogMsg = "JSP-SESSION [" + szOperationName + " ] 크레인이 변경될 경우 기존크레인 작업취소 전문발송 szWrkProgStat=["+szWrkProgStat+"]";
					ydUtils.putLog(szSessionName , szMethodName , szLogMsg , YdConstant.DEBUG);
					
					if("1".equals(szWrkProgStat)){
						recDelPara   = JDTORecordFactory.getInstance().create();
						recDelPara.setField("MSG_ID",           "YDY5L004"        );
						recDelPara.setField("YD_CRN_SCH_ID",    recTemp.getField("YD_CRN_SCH_ID")          ); 
						recPara.setField("YD_WRK_PROG_STAT","W");
						recDelPara.setField("MSG_GP",           "D"                );
						
						YdDelegate ydDelegate = new YdDelegate();					
						ydUtils.displayRecord(szOperationName, recDelPara);					
						ydDelegate.sendMsg(recDelPara);
					}
					//현작업지시 취소 전문 발송 end
					//*************************************************************
					
					
					if(szWrkProgStat.equals(YdConstant.YD_EQP_STAT_UP_WO)){
						szchkWrkProgStat =szWrkProgStat;
					}
					
					recPara.setField("YD_CRN_SCH_ID", recTemp.getField("YD_CRN_SCH_ID"));
					recPara.setField("YD_EQP_ID",szChgCrn);					
					recPara.setField("YD_SCH_PRIOR",  new Integer(intCrnPrior));
					recPara.setField("MODIFIER",szMODIFIER);
					recPara.setField("YD_WRK_PROG_STAT","W");
					recPara.setField("YD_WORD_DT","");
					
					// 5. 스케줄 테이블에 UPDATE 
					intGp = ydCrnSchDao.updYdCrnsch(recPara, 0);
					if (intGp <1){
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, "스케줄 테이블에 UPDATE 중 ERROR");	
						return outRecord;
					}
										
				}while(rsrstDataSch.next());
				
			} //for 문
			//##############################################################################################
				
 
				// *************************************************************
				// 변경 크레인 상태가  대기인 경우 작업대기 상태로 세팅을 바꾸어준다.
				// *************************************************************

				// 변경 크레인의 상태가 대기 상태인 경우
				if (szchgydEqpStat.equals(YdConstant.YD_EQP_STAT_IDLE)) {

					szLogMsg = "JSP-SESSION [" + szOperationName + " ] 선택된 크레인이 변경될 경우 크레인 변경될 크레인 상태를  선택상태로 변경한다.";
					ydUtils.putLog(szSessionName , szMethodName , szLogMsg , YdConstant.DEBUG);


					szLogMsg = "[JSP Session] " + szOperationName + ": 크레인 작업지시   : 야드구분[" + szYD_GP + "]"; // 야드구분은 스케줄 코드앞자리에서 발생되었다.
					ydUtils.putLog(szSessionName , szMethodName , szLogMsg , YdConstant.DEBUG);

					// JMS => EJB CALL 형식으로 수정요청
					recDelPara = JDTORecordFactory.getInstance().create();
					szJMS_TC_CD = "YDYDJ643";

					recDelPara.setField("MSG_ID" , szJMS_TC_CD);
					recDelPara.setField("YD_EQP_ID" , szChgCrn);
					recDelPara.setField("YD_WRK_PROG_STAT" , YdConstant.YD_EQP_STAT_IDLE);
					recDelPara.setField("YD_SCH_CD" , szYD_SCH_CD);
					ydUtils.displayRecord(szOperationName , recDelPara);

					ejbConn = new EJBConnector("default" , this);
					ejbConn.trx("CoilCraneLdHdSeEJB" , "procY5CrnWrkOrdReq" , recDelPara);

				}
				
				//*************************************************************
				//크레인 변경 후 원 크레인 정보는 작업대기 상태로 세팅을 바꾸어준다.
				//*************************************************************
				
				if (szydEqpStat.equals(YdConstant.YD_EQP_STAT_IDLE) || szydEqpStat.equals(YdConstant.YD_EQP_STAT_UP_WO)){ 	
						
					szLogMsg = "JSP-SESSION [" + szOperationName + " ] 크레인 변경 후 전 크레인 정보 설비상태는 작업대기 상태로 바꾸어준다." + szchkWrkProgStat + ",eqp:" + szydEqpStat;
					ydUtils.putLog(szSessionName , szMethodName , szLogMsg , YdConstant.DEBUG);
	
					// 변경 크레인 상태와 변경 전 설비 상태 비교
					if (szchkWrkProgStat.equals(YdConstant.YD_EQP_STAT_UP_WO) && szydEqpStat.equals(YdConstant.YD_EQP_STAT_UP_WO)) {
						szWrkProgStat2 = YdConstant.YD_EQP_STAT_IDLE;
					} else if (szchkWrkProgStat.equals(YdConstant.YD_EQP_STAT_IDLE) && szydEqpStat.equals(YdConstant.YD_EQP_STAT_UP_WO)) {
						szWrkProgStat2 = YdConstant.YD_EQP_STAT_UP_WO;
					} else if (szchkWrkProgStat.equals(YdConstant.YD_EQP_STAT_IDLE) && szydEqpStat.equals(YdConstant.YD_EQP_STAT_IDLE)) {
						szWrkProgStat2 = YdConstant.YD_EQP_STAT_IDLE;
					}
	
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_EQP_ID" , szEqpId);
					recPara.setField("YD_EQP_STAT" , szWrkProgStat2);
					recPara.setField("MODIFIER" , szMODIFIER);
	
					intGp = ydEqpDao.updYdEqp(recPara , 0);
					if (intGp < 1) {
						outRecord.setField("RTN_CD" , "0");
						outRecord.setField("RTN_MSG" , "설비에 UPDATE 중 ERROR");
						return outRecord;
					}
	
					if ((szchkWrkProgStat.equals(YdConstant.YD_EQP_STAT_UP_WO) && szydEqpStat.equals(YdConstant.YD_EQP_STAT_UP_WO))
							|| (szchkWrkProgStat.equals(YdConstant.YD_EQP_STAT_IDLE) && szydEqpStat.equals(YdConstant.YD_EQP_STAT_IDLE))) {
	
						// 작업지시 요구
						recDelPara = JDTORecordFactory.getInstance().create();
						szJMS_TC_CD = "YDYDJ643";
	
						recDelPara.setField("MSG_ID" , szJMS_TC_CD);
						recDelPara.setField("YD_EQP_ID" , szEqpId);
						recDelPara.setField("YD_WRK_PROG_STAT" , szWrkProgStat2);
						recDelPara.setField("YD_SCH_CD" , szYD_SCH_CD);	
						ydUtils.displayRecord(szOperationName , recDelPara);
	
						ejbConn = new EJBConnector("default" , this);
						ejbConn.trx("CoilCraneLdHdSeEJB" , "procY5CrnWrkOrdReq" , recDelPara);

	
					}
	
				}
 			
			outRecord.setField("RTN_CD" 		, "1");	
			
		}catch(DAOException e){
			ydUtils.putLog("YdJspCommonSeEJB", "wrkCrnChange", e.getMessage(), YdConstant.ERROR);
			throw new DAOException(getClass().getName() + e.getMessage(),e);
			
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
			
		}
		
 		return outRecord ;
		
		
	}
	
	
	/**
	 * 야드관리 > 코일소재야드 > 제공관리 > 저장위치관리 조회 (현재는 테스트에 들어 잇음 2010.05.11)
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.05.11
	 */
	public GridData getCoilYdStrlocModMgt(GridData inDto) {
		GridData 		rtnGrd 		= new GridData();
		JDTORecordSet   outRecSet  	= null;
		
		CoilJspDAO 		dao 		= new CoilJspDAO();
		JDTORecord 		recPara		= null;
		
		try {
			
			// 파라미터 셋팅 
			recPara		= JDTORecordFactory.getInstance().create(); 			
			recPara.setField("V_STL_NO"		, inDto.getParam("STL_NO").trim());	/*코일번호*/
			//recPara.setField("V_GONG_GP"	, inDto.getParam("YD_GP").trim());	/*야드구분*/

			// DAO 호출
			outRecSet = dao.getCoilYdStrlocModMgt(recPara);
			
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
	}//end of getCoilYdStrlocModMgt
	
	
	
	/**
	 * 저장위치변경관리 (YD저장위치수정)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord updYDStrlocModMgt(JDTORecord[] recMsg, GridData gdReq) throws DAOException {
		int           intRtnVal     = 0;
		JDTORecord    tmpPara       = JDTORecordFactory.getInstance().create();
		
		JDTORecord    updPara1      = JDTORecordFactory.getInstance().create();
		JDTORecord    updPara2      = JDTORecordFactory.getInstance().create();
		JDTORecord    updPara3      = JDTORecordFactory.getInstance().create();
		JDTORecord    updPara4      = JDTORecordFactory.getInstance().create();
		JDTORecord    inRecord      = JDTORecordFactory.getInstance().create();
		JDTORecord    inRecord1     = JDTORecordFactory.getInstance().create();
		JDTORecord    outRecord1      = JDTORecordFactory.getInstance().create();
		
		String szMethodName = "updYDStrlocModMgt";
		GridData 		rtnGrd 		= new GridData();
		CoilJspDAO 		dao 		= new CoilJspDAO();
		YdStkLyrDao ydStkLyrDao    		= new YdStkLyrDao();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String sFR_YD_STK_COL_GP 	= ""; 
		String sFR_YD_STK_BED_NO 	= ""; 
		String sFR_YD_STK_LYR_NO 	= ""; 

		String sTO_YD_STK_COL_GP 	= ""; 
		String sTO_YD_STK_BED_NO 	= ""; 
		String sTO_YD_STK_LYR_NO 	= ""; 

		String sTO_STKLOC 			= ""; 
		String sYD_USER_ID 			= "";
		String sSTL_NO              = "";
		String sSND_FLAG			= "";
		String sSND_COMM_FLAG		= "Y";
		String sYD_AIM_RT_GP		= "";
		String sYD_AIM_YD_GP		= "";
		String sYD_AIM_BAY_GP		= "";
		String sMTL_UPD_FLAG		= "";
		String sLOC_UPD_FLAG		= "";
		
		CoilYdToLocDcsnUtil ydToLocDcsnUtil = new CoilYdToLocDcsnUtil();
		
		JDTORecord outRecord = JDTORecordFactory.getInstance().create(); // 
		
//SJH		
		try {
			
			sFR_YD_STK_COL_GP	= recMsg[0].getFieldString("YD_STK_COL_GP"); // 야드적치열
			sFR_YD_STK_BED_NO	= recMsg[0].getFieldString("YD_STK_BED_NO"); // 
			sFR_YD_STK_LYR_NO	= recMsg[0].getFieldString("YD_STK_LYR_NO"); // 
			
			
			sTO_YD_STK_COL_GP	= recMsg[0].getFieldString("UPD_STK_POS_2"); // 야드적치열
			sTO_YD_STK_BED_NO	= recMsg[0].getFieldString("UPD_STK_BED_NO"); // 
			sTO_YD_STK_LYR_NO	= recMsg[0].getFieldString("UPD_STK_LYR_NO"); //
			sTO_STKLOC 			= recMsg[0].getFieldString("UPD_STK_POS");

			sYD_USER_ID			= recMsg[0].getFieldString("YD_USER_ID"); // 
			
			sSTL_NO				= recMsg[0].getFieldString("STL_NO"); // 
			sSND_FLAG			= gdReq.getParam("SND_FLAG");		// 송신 여부

			sYD_AIM_RT_GP		= recMsg[0].getFieldString("YD_AIM_RT_GP"); // 
			sYD_AIM_YD_GP		= recMsg[0].getFieldString("YD_AIM_YD_GP"); // 
			sYD_AIM_BAY_GP		= recMsg[0].getFieldString("YD_AIM_BAY_GP"); // 
			
			sMTL_UPD_FLAG		= recMsg[0].getFieldString("MTL_UPD_FLAG"); // gdReq.getHeader("MTL_UPD_FLAG").getValue(0);		// 재료 속성 변경
			sLOC_UPD_FLAG		= recMsg[0].getFieldString("LOC_UPD_FLAG"); // gdReq.getHeader("LOC_UPD_FLAG").getValue(0);		// 저장위치변경
			
			
			tmpPara.setField("V_YD_STK_BED_NO", sTO_STKLOC); 
			tmpPara.setField("V_YD_STK_LYR_NO", sTO_YD_STK_LYR_NO); 
			
			// 1) 수정위치의 재료번호 등록 여부를 조회 -----------------------------------------------------
			/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilYDDao.getStrlocModMgtChk*/
			outRecSet = dao.getStrlocModMgtChk(tmpPara);
			
			if(outRecSet != null && outRecSet.size()>0){
				// 이미 저장위치에 코일이 존재 한다면 리턴 :count
				if(!outRecSet.getRecord(0).getField("YD_STK_LYR_MTL_STAT").equals("E")){
					if(!outRecSet.getRecord(0).getField("STL_NO").equals(sSTL_NO)){
							
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, "선택 하신 수정위치는 이미 코일이 존재하거나 금지 bed 입니다.");	
						return outRecord;
					} else {
						sSND_COMM_FLAG			= "N";
					}
					
				}
			}	

			//=============================================================================================
			// 레이어 정보 수정 
			//=============================================================================================
			//2) 현재위치의 재료번호를 조회후  Null로  수정 --------------------------------------------------------
			/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdStkPosInfo_PIDEV*/
			JDTORecordSet   outRecSet1   = JDTORecordFactory.getInstance().createRecordSet("YD");
			inRecord1 = JDTORecordFactory.getInstance().create();
			inRecord1.setField("STL_NO",   sSTL_NO);
			intRtnVal = ydStkLyrDao.getYdStklyr(inRecord1, outRecSet1, 24);
			if (intRtnVal > 0) {
				//적치되어 있는 정보 삭제처리
				outRecSet1.first();
				outRecord1 = outRecSet1.getRecord();
				//적치단 재료상태가 적치 가능이면 재료 등록
				//적치단 테이블 업데이트
				//적치열구분 = 설비ID
				sFR_YD_STK_COL_GP = ydDaoUtils.paraRecChkNull(outRecord1, "YD_STK_COL_GP");
				sFR_YD_STK_BED_NO = ydDaoUtils.paraRecChkNull(outRecord1, "YD_STK_BED_NO");
				sFR_YD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(outRecord1, "YD_STK_LYR_NO");
			}
			
			updPara1.setField("V_YD_STK_COL_GP", sFR_YD_STK_COL_GP);// 야드적치열
			updPara1.setField("V_YD_STK_BED_NO", sFR_YD_STK_BED_NO);// 야드적치배드
			updPara1.setField("V_YD_STK_LYR_NO", sFR_YD_STK_LYR_NO);// 적치단      
			
			
			if(!sSND_FLAG.equals("STOCK")){
				if(sSND_COMM_FLAG.equals("Y")){
					// from 위치 최신정보로 조회
					/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilYDDao.getStrlocModMgtStlInfo*/
					outRecSet = dao.getStrlocModMgtStlInfo(updPara1);
					
					if(outRecSet != null && outRecSet.size()>0){
						// from 위치 수정 
						updPara1.setField("V_MODIFIER", sYD_USER_ID);
						updPara1.setField("V_STL_NO", ""); // 재료번호
						updPara1.setField("V_YD_STK_LYR_MTL_STAT", "E"); // 적치단재료상태
						updPara1.setField("V_YD_STK_COL_GP", sFR_YD_STK_COL_GP); // 야드적치열
						updPara1.setField("V_YD_STK_BED_NO", sFR_YD_STK_BED_NO);  // 야드적치배드
						updPara1.setField("V_YD_STK_LYR_NO", sFR_YD_STK_LYR_NO);  // 적치단               
						
						/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDAO.updStrlocModMgtFromAndTo*/
						intRtnVal = dao.updStrlocModMgtFromAndTo(updPara1);
						
						if(intRtnVal != 1){
							// TODO.. 오류처리
							outRecord.setField("RTN_CD" 	, "0");	
							outRecord.setField("RTN_MSG" 	, "현재위치정보 수정에 실패 하였습니다");	
							return outRecord;
							
						}
//					}else{
//						// TODO.. 오류처리 
//						outRecord.setField("RTN_CD" 	, "0");	
//						outRecord.setField("RTN_MSG" 	, "현재 위치정보 조회에 실패 하였습니다");	
//						return outRecord;
					}
				}
			}
			if(sSND_COMM_FLAG.equals("Y")){
				//3) 수정위치의 재료번호를 등록(UPDATE) ----------------------------------------------------------------
				updPara2.setField("V_YD_STK_COL_GP", sTO_YD_STK_COL_GP);// 야드적치열
				updPara2.setField("V_YD_STK_BED_NO", sTO_YD_STK_BED_NO);// 야드적치배드
				updPara2.setField("V_YD_STK_LYR_NO", sTO_YD_STK_LYR_NO);// 적치단      
				
				
				// To 위치 최신정보로 조회
				/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilYDDao.getStrlocModMgtStlInfo*/
				outRecSet = dao.getStrlocModMgtStlInfo(updPara2);
				
				if(outRecSet != null && outRecSet.size()>0){
					
					if(sTO_YD_STK_COL_GP.substring(0, 1).equals("J")){
						//1단 check
						inRecord = JDTORecordFactory.getInstance().create();
						inRecord.setField("YD_STK_COL_GP" 	, sTO_YD_STK_COL_GP);	
						inRecord.setField("YD_STK_BED_NO" 	, sTO_YD_STK_BED_NO);	
						inRecord.setField("YD_STK_LYR_NO" 	, sTO_YD_STK_LYR_NO);
						//inRecord.setField("BRE_CHK1_SKIP_YN", "N");  //폭간섭 여부 SKIP
						ydUtils.putLog(szSessionName, szMethodName, "CHK???"+sTO_YD_STK_COL_GP.substring(1, 2), YdConstant.DEBUG);
						if(  sTO_YD_STK_COL_GP.substring(2, 4).equals("80")
							    || sTO_YD_STK_COL_GP.substring(2, 4).equals("70")
								|| sTO_YD_STK_COL_GP.substring(1, 2).equals("L")
								|| sTO_YD_STK_COL_GP.substring(1, 2).equals("M")
								|| sTO_YD_STK_COL_GP.substring(1, 2).equals("B")
								|| sTO_YD_STK_COL_GP.substring(1, 2).equals("A")
							) {  // 가상 bed임
							
						} else {
	
							outRecord1 = ydToLocDcsnUtil.CoilGdsLyrBaseCheck(sSTL_NO, inRecord);
							String sRTN_CD	= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
							String sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
							
							if (!("1".equals(sRTN_CD))) {
								outRecord.setField("RTN_CD" 	, "0");	
								outRecord.setField("RTN_MSG" 	, sRTN_MSG);	
								return outRecord;
							}
						}
					}	

					
					// To위치 수정 
					updPara2.setField("V_MODIFIER", sYD_USER_ID);
					updPara2.setField("V_STL_NO", sSTL_NO); // 재료번호
					updPara2.setField("V_YD_STK_LYR_MTL_STAT", "C"); // 적치단재료상태
					updPara2.setField("V_YD_STK_COL_GP", sTO_YD_STK_COL_GP); // 야드적치열
					updPara2.setField("V_YD_STK_BED_NO", sTO_YD_STK_BED_NO);  // 야드적치배드
					updPara2.setField("V_YD_STK_LYR_NO", sTO_YD_STK_LYR_NO);  // 적치단               
	
					intRtnVal = dao.updStrlocModMgtFromAndTo(updPara2);
					
					if(intRtnVal != 1){
						// TODO.. 수정 오류처리
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, "변경위치정보 수정에 실패 하였습니다");	
						return outRecord;
						
					}
				}else{
					// TODO.. 조회된 데이터 없을시 오류처리 
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, "변경 위치정보 조회에 실패 하였습니다");	
					return outRecord;
				}
			}	
			
			//=============================================================================================
			// 야드저장품 수정 
			//=============================================================================================
			
			updPara3.setField("V_YD_STK_COL_GP"	, sTO_YD_STK_COL_GP);	// 야드적치열구분
			updPara3.setField("V_YD_STK_BED_NO"	, sTO_YD_STK_BED_NO);	// 야드적치배드
			updPara3.setField("V_YD_STK_LYR_NO"	, sTO_YD_STK_LYR_NO);	// 야드단
			updPara3.setField("V_MODIFIER"		, sYD_USER_ID);	// 수정자
			updPara3.setField("V_STL_NO"		, sSTL_NO);	// 코일번호
			updPara3.setField("V_YD_AIM_YD_GP"	, sYD_AIM_YD_GP);	// 목표야드
			updPara3.setField("V_YD_AIM_BAY_GP" , sYD_AIM_BAY_GP);	// 목적동
			updPara3.setField("V_YD_AIM_RT_GP"	, sYD_AIM_RT_GP);	// 목표행선
			
			ydUtils.putLog(szSessionName, szMethodName, "sYD_AIM_YD_GP:"+ sYD_AIM_YD_GP, YdConstant.DEBUG);
			ydUtils.putLog(szSessionName, szMethodName, "sYD_AIM_BAY_GP:"+ sYD_AIM_BAY_GP, YdConstant.DEBUG);


			
			/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDAO.updStrlocModMgtStock*/
			intRtnVal = dao.updStrlocModMgtStock(updPara3);
			
			if(intRtnVal != 1){
				// TODO.. 수정 오류처리
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, "야드저장품 정보 수정에 실패 하였습니다");	
				return outRecord;

			}
				
			// 정상 처리 후
			outRecord.setField("SND_COMM_FLAG" 	, sSND_COMM_FLAG);	
			outRecord.setField("RTN_CD" 		, "1");	
			return outRecord;
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} 
	}	// end of updStrlocModMgt
	/**
	 * 저장위치변경관리 (YD저장위치수정)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord updYDStrlocModMgt1(JDTORecord[] recMsg, GridData gdReq) throws DAOException {
		int           intRtnVal     = 0;
		JDTORecord    tmpPara       = JDTORecordFactory.getInstance().create();
		
		JDTORecord    updPara1      = JDTORecordFactory.getInstance().create();
		JDTORecord    updPara2      = JDTORecordFactory.getInstance().create();
		JDTORecord    updPara3      = JDTORecordFactory.getInstance().create();
		JDTORecord    updPara4      = JDTORecordFactory.getInstance().create();
		JDTORecord    inRecord      = JDTORecordFactory.getInstance().create();
		JDTORecord    outRecord1      = JDTORecordFactory.getInstance().create();
		
		String szMethodName = "updYDStrlocModMgt1";
		GridData 		rtnGrd 		= new GridData();
		CoilJspDAO 		dao 		= new CoilJspDAO();
		
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String sFR_YD_STK_COL_GP 	= ""; 
		String sFR_YD_STK_BED_NO 	= ""; 
		String sFR_YD_STK_LYR_NO 	= ""; 

		String sTO_YD_STK_COL_GP 	= ""; 
		String sTO_YD_STK_BED_NO 	= ""; 
		String sTO_YD_STK_LYR_NO 	= ""; 

		String sTO_STKLOC 			= ""; 
		String sYD_USER_ID 			= "";
		String sSTL_NO              = "";
		String sSND_FLAG			= "";
		String sSND_COMM_FLAG		= "Y";
		String sYD_AIM_RT_GP		= "";
		String sYD_AIM_YD_GP		= "";
		String sYD_AIM_BAY_GP		= "";
		String sMTL_UPD_FLAG		= "";
		String sLOC_UPD_FLAG		= "";
		
		CoilYdToLocDcsnUtil ydToLocDcsnUtil = new CoilYdToLocDcsnUtil();
		
		JDTORecord outRecord = JDTORecordFactory.getInstance().create(); // 
		
//SJH		
		try {
			
			sFR_YD_STK_COL_GP	= recMsg[0].getFieldString("YD_STK_COL_GP"); // 야드적치열
			sFR_YD_STK_BED_NO	= recMsg[0].getFieldString("YD_STK_BED_NO"); // 
			sFR_YD_STK_LYR_NO	= recMsg[0].getFieldString("YD_STK_LYR_NO"); // 
			
			
			sTO_YD_STK_COL_GP	= recMsg[0].getFieldString("UPD_STK_POS_2"); // 야드적치열
			sTO_YD_STK_BED_NO	= recMsg[0].getFieldString("UPD_STK_BED_NO"); // 
			sTO_YD_STK_LYR_NO	= recMsg[0].getFieldString("UPD_STK_LYR_NO"); //
			sTO_STKLOC 			= recMsg[0].getFieldString("UPD_STK_POS");

			sYD_USER_ID			= recMsg[0].getFieldString("YD_USER_ID"); // 
			
			sSTL_NO				= recMsg[0].getFieldString("STL_NO"); // 
			sSND_FLAG			= gdReq.getParam("SND_FLAG");		// 송신 여부

			sYD_AIM_RT_GP		= recMsg[0].getFieldString("YD_AIM_RT_GP"); // 
			sYD_AIM_YD_GP		= recMsg[0].getFieldString("YD_AIM_YD_GP"); // 
			sYD_AIM_BAY_GP		= recMsg[0].getFieldString("YD_AIM_BAY_GP"); // 
			
			sMTL_UPD_FLAG		= recMsg[0].getFieldString("MTL_UPD_FLAG"); // gdReq.getHeader("MTL_UPD_FLAG").getValue(0);		// 재료 속성 변경
			sLOC_UPD_FLAG		= recMsg[0].getFieldString("LOC_UPD_FLAG"); // gdReq.getHeader("LOC_UPD_FLAG").getValue(0);		// 저장위치변경
			
			updPara3.setField("V_YD_STK_COL_GP"	, sFR_YD_STK_COL_GP);	// 야드적치열구분
			updPara3.setField("V_YD_STK_BED_NO"	, sFR_YD_STK_BED_NO);	// 야드적치배드
			updPara3.setField("V_YD_STK_LYR_NO"	, sFR_YD_STK_LYR_NO);	// 야드단
			updPara3.setField("V_MODIFIER"		, sYD_USER_ID);	// 수정자
			updPara3.setField("V_STL_NO"		, sSTL_NO);	// 코일번호
			updPara3.setField("V_YD_AIM_YD_GP"	, sYD_AIM_YD_GP);	// 목표야드
			updPara3.setField("V_YD_AIM_BAY_GP" , sYD_AIM_BAY_GP);	// 목적동
			updPara3.setField("V_YD_AIM_RT_GP"	, sYD_AIM_RT_GP);	// 목표행선
			
			ydUtils.putLog(szSessionName, szMethodName, "sYD_AIM_YD_GP:"+ sYD_AIM_YD_GP, YdConstant.DEBUG);
			ydUtils.putLog(szSessionName, szMethodName, "sYD_AIM_BAY_GP:"+ sYD_AIM_BAY_GP, YdConstant.DEBUG);


			
			/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDAO.updStrlocModMgtStock*/
			intRtnVal = dao.updStrlocModMgtStock(updPara3);
			
			if(intRtnVal != 1){
				// TODO.. 수정 오류처리
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, "야드저장품 정보 수정에 실패 하였습니다");	
				return outRecord;

			}
				
			// 정상 처리 후
			outRecord.setField("SND_COMM_FLAG" 	, "N");	
			outRecord.setField("RTN_CD" 		, "1");	
			return outRecord;
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} 
	}	// end of updStrlocModMgt1


	/**
	 * 저장위치변경관리 (YD저장위치수정)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord updYDStrlocModMgtLoc(JDTORecord[] recMsg, GridData gdReq) throws DAOException {
		int           intRtnVal     = 0;
		JDTORecord    tmpPara       = JDTORecordFactory.getInstance().create();
		
		JDTORecord    updPara1      = JDTORecordFactory.getInstance().create();
		JDTORecord    updPara2      = JDTORecordFactory.getInstance().create();
		JDTORecord    updPara3      = JDTORecordFactory.getInstance().create();
		JDTORecord    updPara4      = JDTORecordFactory.getInstance().create();
		JDTORecord    inRecord      = JDTORecordFactory.getInstance().create();
		JDTORecord    outRecord1      = JDTORecordFactory.getInstance().create();
		
		String szMethodName = "updYDStrlocModMgtLoc";
		GridData 		rtnGrd 		= new GridData();
		CoilJspDAO 		dao 		= new CoilJspDAO();
		
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String sFR_YD_STK_COL_GP 	= ""; 
		String sFR_YD_STK_BED_NO 	= ""; 
		String sFR_YD_STK_LYR_NO 	= ""; 

		String sTO_YD_STK_COL_GP 	= ""; 
		String sTO_YD_STK_BED_NO 	= ""; 
		String sTO_YD_STK_LYR_NO 	= ""; 

		String sTO_STKLOC 			= ""; 
		String sYD_USER_ID 			= "";
		String sSTL_NO              = "";
		String sSND_FLAG			= "";
		String sSND_COMM_FLAG		= "Y";
		String sYD_AIM_RT_GP		= "";
		String sYD_AIM_YD_GP		= "";
		String sYD_AIM_BAY_GP		= "";
		
		CoilYdToLocDcsnUtil ydToLocDcsnUtil = new CoilYdToLocDcsnUtil();
		
		JDTORecord outRecord = JDTORecordFactory.getInstance().create(); // 
		
//SJH		
		try {
			
			sFR_YD_STK_COL_GP	= recMsg[0].getFieldString("YD_STK_COL_GP"); // 야드적치열
			sFR_YD_STK_BED_NO	= recMsg[0].getFieldString("YD_STK_BED_NO"); // 
			sFR_YD_STK_LYR_NO	= recMsg[0].getFieldString("YD_STK_LYR_NO"); // 
			
			
			sTO_YD_STK_COL_GP	= recMsg[0].getFieldString("UPD_STK_POS_2"); // 야드적치열
			sTO_YD_STK_BED_NO	= recMsg[0].getFieldString("UPD_STK_BED_NO"); // 
			sTO_YD_STK_LYR_NO	= recMsg[0].getFieldString("UPD_STK_LYR_NO"); //
			sTO_STKLOC 			= recMsg[0].getFieldString("UPD_STK_POS");

			sYD_USER_ID			= recMsg[0].getFieldString("YD_USER_ID"); // 
			
			sSTL_NO				= recMsg[0].getFieldString("STL_NO"); // 
			sSND_FLAG			= gdReq.getParam("SND_FLAG");		// 송신 여부

			sYD_AIM_RT_GP		= recMsg[0].getFieldString("YD_AIM_RT_GP"); // 
			sYD_AIM_YD_GP		= recMsg[0].getFieldString("YD_AIM_YD_GP"); // 
			sYD_AIM_BAY_GP		= recMsg[0].getFieldString("YD_AIM_BAY_GP"); // 
			
			
			tmpPara.setField("V_YD_STK_BED_NO", sTO_STKLOC); 
			tmpPara.setField("V_YD_STK_LYR_NO", sTO_YD_STK_LYR_NO); 
			
			// 1) 수정위치의 재료번호 등록 여부를 조회 -----------------------------------------------------
			/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilYDDao.getStrlocModMgtChk*/
			outRecSet = dao.getStrlocModMgtChk(tmpPara);
			
			if(outRecSet != null && outRecSet.size()>0){
				// 이미 저장위치에 코일이 존재 한다면 리턴 :count
				if(!outRecSet.getRecord(0).getField("YD_STK_LYR_MTL_STAT").equals("E")){
					if(!outRecSet.getRecord(0).getField("STL_NO").equals(sSTL_NO)){
							
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, "선택 하신 수정위치는 이미 코일이 존재하거나 금지 bed 입니다.");	
						return outRecord;
					} else {
						sSND_COMM_FLAG			= "N";
					}
					
				}
			}	

			//=============================================================================================
			// 레이어 정보 수정 
			//=============================================================================================
			//2) 현재위치의 재료번호를 조회후  Null로  수정 --------------------------------------------------------
			updPara1.setField("V_YD_STK_COL_GP", sFR_YD_STK_COL_GP);// 야드적치열
			updPara1.setField("V_YD_STK_BED_NO", sFR_YD_STK_BED_NO);// 야드적치배드
			updPara1.setField("V_YD_STK_LYR_NO", sFR_YD_STK_LYR_NO);// 적치단      
			
			
			if(!sSND_FLAG.equals("STOCK")){
				if(sSND_COMM_FLAG.equals("Y")){
					// from 위치 최신정보로 조회
					/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilYDDao.getStrlocModMgtStlInfo*/
					outRecSet = dao.getStrlocModMgtStlInfo(updPara1);
					
					if(outRecSet != null && outRecSet.size()>0){
						// from 위치 수정 
						updPara1.setField("V_MODIFIER", sYD_USER_ID);
						updPara1.setField("V_STL_NO", ""); // 재료번호
						updPara1.setField("V_YD_STK_LYR_MTL_STAT", "E"); // 적치단재료상태
						updPara1.setField("V_YD_STK_COL_GP", sFR_YD_STK_COL_GP); // 야드적치열
						updPara1.setField("V_YD_STK_BED_NO", sFR_YD_STK_BED_NO);  // 야드적치배드
						updPara1.setField("V_YD_STK_LYR_NO", sFR_YD_STK_LYR_NO);  // 적치단               
						
						/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDAO.updStrlocModMgtFromAndTo*/
						intRtnVal = dao.updStrlocModMgtFromAndTo(updPara1);
						
						if(intRtnVal != 1){
							// TODO.. 오류처리
							outRecord.setField("RTN_CD" 	, "0");	
							outRecord.setField("RTN_MSG" 	, "현재위치정보 수정에 실패 하였습니다");	
							return outRecord;
							
						}
//					}else{
//						// TODO.. 오류처리 
//						outRecord.setField("RTN_CD" 	, "0");	
//						outRecord.setField("RTN_MSG" 	, "현재 위치정보 조회에 실패 하였습니다");	
//						return outRecord;
					}
				}
			}
			
			if(sSND_COMM_FLAG.equals("Y")){
				//3) 수정위치의 재료번호를 등록(UPDATE) ----------------------------------------------------------------
				updPara2.setField("V_YD_STK_COL_GP", sTO_YD_STK_COL_GP);// 야드적치열
				updPara2.setField("V_YD_STK_BED_NO", sTO_YD_STK_BED_NO);// 야드적치배드
				updPara2.setField("V_YD_STK_LYR_NO", sTO_YD_STK_LYR_NO);// 적치단      
				
				
				// To 위치 최신정보로 조회
				/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilYDDao.getStrlocModMgtStlInfo*/
				outRecSet = dao.getStrlocModMgtStlInfo(updPara2);
				
				if(outRecSet != null && outRecSet.size()>0){
					
					// To위치 수정 
					updPara2.setField("V_MODIFIER", sYD_USER_ID);
					updPara2.setField("V_STL_NO", sSTL_NO); // 재료번호
					updPara2.setField("V_YD_STK_LYR_MTL_STAT", "C"); // 적치단재료상태
					updPara2.setField("V_YD_STK_COL_GP", sTO_YD_STK_COL_GP); // 야드적치열
					updPara2.setField("V_YD_STK_BED_NO", sTO_YD_STK_BED_NO);  // 야드적치배드
					updPara2.setField("V_YD_STK_LYR_NO", sTO_YD_STK_LYR_NO);  // 적치단               
	
					intRtnVal = dao.updStrlocModMgtFromAndTo(updPara2);
					
					if(intRtnVal != 1){
						// TODO.. 수정 오류처리
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, "변경위치정보 수정에 실패 하였습니다");	
						return outRecord;
						
					}
				}else{
					// TODO.. 조회된 데이터 없을시 오류처리 
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, "변경 위치정보 조회에 실패 하였습니다");	
					return outRecord;
				}
			}	
			//=============================================================================================
			// 야드저장품 수정 
			//=============================================================================================
			
			updPara3.setField("V_YD_STK_COL_GP"	, sTO_YD_STK_COL_GP);	// 야드적치열구분
			updPara3.setField("V_YD_STK_BED_NO"	, sTO_YD_STK_BED_NO);	// 야드적치배드
			updPara3.setField("V_YD_STK_LYR_NO"	, sTO_YD_STK_LYR_NO);	// 야드단
			updPara3.setField("V_MODIFIER"		, sYD_USER_ID);	// 수정자
			updPara3.setField("V_STL_NO"		, sSTL_NO);	// 코일번호
			updPara3.setField("V_YD_AIM_YD_GP"	, sYD_AIM_YD_GP);	// 목표야드
			updPara3.setField("V_YD_AIM_BAY_GP" , sYD_AIM_BAY_GP);	// 목적동
			updPara3.setField("V_YD_AIM_RT_GP"	, sYD_AIM_RT_GP);	// 목표행선
			
			ydUtils.putLog(szSessionName, szMethodName, "sYD_AIM_YD_GP:"+ sYD_AIM_YD_GP, YdConstant.DEBUG);
			ydUtils.putLog(szSessionName, szMethodName, "sYD_AIM_BAY_GP:"+ sYD_AIM_BAY_GP, YdConstant.DEBUG);


			
			/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDAO.updStrlocModMgtStock*/
			intRtnVal = dao.updStrlocModMgtStock(updPara3);
			
			if(intRtnVal != 1){
				// TODO.. 수정 오류처리
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, "야드저장품 정보 수정에 실패 하였습니다");	
				return outRecord;

			}
				
			// 정상 처리 후
			outRecord.setField("SND_COMM_FLAG" 	, sSND_COMM_FLAG);	
			outRecord.setField("RTN_CD" 		, "1");	
			return outRecord;
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} 
	}	// end of updStrlocModMgt

	
	/**
	 * 저장위치변경관리 (COMM저장위치수정)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord updCOMMStrlocModMgt(JDTORecord recMsg) throws DAOException {
		int           intRtnVal     = 0;
		
		JDTORecord    updPara4      = JDTORecordFactory.getInstance().create();
		
		CoilJspDAO 		dao 		= new CoilJspDAO();
		

		String sTO_YD_STK_COL_GP 	= ""; 
		String sTO_YD_STK_BED_NO 	= ""; 
		String sTO_YD_STK_LYR_NO 	= ""; 

		String sYD_USER_ID 			= "";
		String sSTL_NO              = "";
		JDTORecord outRecord = JDTORecordFactory.getInstance().create(); // 
		
		
		try {
			
			sTO_YD_STK_COL_GP	= recMsg.getFieldString("UPD_STK_POS_2"); // 야드적치열
			sTO_YD_STK_BED_NO	= recMsg.getFieldString("UPD_STK_BED_NO"); // 
			sTO_YD_STK_LYR_NO	= recMsg.getFieldString("UPD_STK_LYR_NO"); //
			sYD_USER_ID			= recMsg.getFieldString("YD_USER_ID"); // 
			
			sSTL_NO				= recMsg.getFieldString("STL_NO"); // 
			

				
			//=============================================================================================
			// 코일 공통 수정 
			//=============================================================================================
			//4) 코일 공통 테이블의 저장위치 수정(UPDATE) ----------------------------------------------------------------
			updPara4.setField("V_YD_GP"			, sTO_YD_STK_COL_GP.substring(0, 1));	// 야드구분
			updPara4.setField("V_YD_BAY_GP"		, sTO_YD_STK_COL_GP.substring(1, 2));	// 야드동구분
			updPara4.setField("V_YD_EQP_GP"		, sTO_YD_STK_COL_GP.substring(2, 4));	// 야드설비구분
			updPara4.setField("V_YD_STK_COL_NO"	, sTO_YD_STK_COL_GP.substring(4, 6));	// 야드적치열번호
			updPara4.setField("V_YD_STK_BED_NO"	, sTO_YD_STK_BED_NO);	// 야드적치배드번호
			updPara4.setField("V_YD_STK_LYR_NO"	, sTO_YD_STK_LYR_NO);	// 야드적치단번호    
			updPara4.setField("V_YD_STR_LOC"	, sTO_YD_STK_COL_GP.substring(0, 1)+
												  sTO_YD_STK_COL_GP.substring(1, 2)+
												  sTO_YD_STK_COL_GP.substring(2, 4)+
												  sTO_YD_STK_COL_GP.substring(4, 6)+ 
												  sTO_YD_STK_BED_NO                +
												  sTO_YD_STK_LYR_NO.substring(1, 3));	// 야드저장위치
			updPara4.setField("V_MODIFIER"		, sYD_USER_ID);	// 수정자
			updPara4.setField("V_FNL_REG_PGM"	, "updCoilYdStkPosInfo");	// 최종등록 프로그램
			updPara4.setField("V_COIL_NO"		, sSTL_NO);	// 코일번호          
			/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDAO.updStrlocModMgtColComm*/
			intRtnVal = dao.updStrlocModMgtColComm(updPara4);
			
			if(intRtnVal != 1){
				// TODO.. 수정 오류처리
	
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, "코일공통 저장위치 정보 수정에 실패 하였습니다");	
				return outRecord;
				
			}
				
			// 정상 처리 후
			outRecord.setField("RTN_CD" 	, "1");	
			return outRecord;
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} 
	}	// end of updStrlocModMgt
	
	
	
	/**
	 * 크레인작업관리 > InterLock 팝업
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @작성자 : 박지열
	 * 작성일 : 2010.05.20
	 */
	public GridData interLock(GridData inDto) throws DAOException{
		
		GridData rtnGrd = new GridData(); 
		String szMethodName = "interLock";
		String SchCdList = "";
		JDTORecordSet   outRecSet  	= null;

		JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
		CoilJspDAO 		dao 		= new CoilJspDAO();
		JDTORecord 		recPara		= JDTORecordFactory.getInstance().create();

		try{

			for(int i=0; i<inDto.getHeader("CHECK").getRowCount(); i++){
				
				recPara.setField("V_YD_SCH_CD", inDto.getHeader("YD_SCH_CD").getValue(i));
				//DAO 호출
				outRecSet = dao.getInterLockList(recPara);
				
				if(outRecSet == null || outRecSet.size() < 1){
					//TODO..
					SchCdList += inDto.getHeader("YD_SCH_CD").getValue(i)+"; ";
				}else{
					if(outRecSet.getRecord(0).getFieldString("YD_WRK_PROG_STAT").equals("E") || outRecSet.getRecord(0).getFieldString("YD_WRK_PROG_STAT").equals("D")){

						//크레인 작업지시 호출
//SJH03004
						recInTemp.setField("MSG_ID",           "Y5YDL007");
						recInTemp.setField("YD_EQP_ID",        outRecSet.getRecord(0).getFieldString("YD_EQP_ID"));
						recInTemp.setField("YD_EQP_WRK_MODE",  outRecSet.getRecord(0).getFieldString("YD_EQP_WRK_MODE"));
						recInTemp.setField("YD_WRK_PROG_STAT", outRecSet.getRecord(0).getFieldString("YD_WRK_PROG_STAT"));
						recInTemp.setField("YD_SCH_CD",        outRecSet.getRecord(0).getFieldString("YD_SCH_CD")); // 스케쥴 코드
						recInTemp.setField("YD_CRN_SCH_ID",    outRecSet.getRecord(0).getFieldString("YD_CRN_SCH_ID"));
						recInTemp.setField("YD_CRN_XAXIS",     "");
						recInTemp.setField("YD_CRN_YAXIS",     "");

						String szEjbJndiName = "CoilCraneLdHdSeEJB";
						String szEjbMethod = "procY5CrnWrkOrdReq";

						String szLogMsg = "[C열연권하실적처리]크레인 작업지시 호출["+szEjbJndiName+"."+szEjbMethod+"] - 메소드 콜 시작";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
//SJH03004
						EJBConnector ejbConn = new EJBConnector("default", "CoilCraneLdHdSeEJB", this);			
						String szRtnMsg = (String)ejbConn.trx("procY5CrnWrkOrdReq", new Class[] { JDTORecord.class }, new Object[] { recInTemp });

//						ydDelegate.sendMsg(recInTemp);
						
//						String szRtnMsg = YdConstant.RETN_CD_SUCCESS;

						if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) || szRtnMsg.equals(YdConstant.RETN_CRN_NO_SCH) || szRtnMsg.equals(YdConstant.RETN_CRN_NO_WRK) ) {
							szLogMsg = "[C열연권하실적처리]크레인 작업지시 호출["+szEjbJndiName+"."+szEjbMethod+"] - 메소드 콜 완료";
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						}else{
							szLogMsg = "[C열연권하실적처리]크레인 작업지시 호출["+szEjbJndiName+"."+szEjbMethod+"] - 메소드 콜 오류발생";
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
							//리턴값이 오류일지라도 롤백 등 후속작업을 하지 않음...

							rtnGrd = CmUtil.copyGDParam(inDto, rtnGrd);
							rtnGrd.setStatus("false");
							rtnGrd.addParam("msg", szLogMsg);
							return rtnGrd;
						}
					}else{
						SchCdList += inDto.getHeader("YD_SCH_CD").getValue(i)+"; ";
					}
				}
				
			}
			
			if(!SchCdList.equals("")){
				m_ctx.setRollbackOnly();
				rtnGrd = CmUtil.copyGDParam(inDto, rtnGrd);
				rtnGrd.setStatus("false");
				rtnGrd.addParam("msg", "Inter-Lock을 설정 할 수 없는 스케줄 입니다. <br>"+"스케줄번호 : "+SchCdList);
				return rtnGrd;
			}else{
			
				rtnGrd = CmUtil.copyGDParam(inDto, rtnGrd);
				rtnGrd.setStatus("true");
				rtnGrd.addParam("msg", "정상 처리되었습니다.");
			}
			
		}catch (Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
		
		return rtnGrd;

	}
	
	/**
	 * 크레인작업관리 > InterLock 팝업
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @작성자 : 박지열
	 * 작성일 : 2010.05.20
	 */
	public JDTORecord getInterLock(JDTORecord []  inDto) throws DAOException{
		
		GridData rtnGrd = new GridData(); 
		String szMethodName = "getInterLock";
		String SchCdList = "";
		JDTORecordSet   outRecSet  	= null;
		JDTORecord outRecord = JDTORecordFactory.getInstance().create(); // 
		JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
		CoilJspDAO 		dao 		= new CoilJspDAO();
		YdCrnSchDao ydCrnSchDao        = new YdCrnSchDao();
		YdEqpDao     ydEqpDao     = new YdEqpDao();
		JDTORecord 		recPara		= JDTORecordFactory.getInstance().create();
		String szLogMsg 		=  "";
		
		String sYD_EQP_ID		=  "";
		String sYD_EQP_WRK_MODE	=  "";
		String sYD_WRK_PROG_STAT=  "";
		String sYD_SCH_CD		=  "";
		String sYD_CRN_SCH_ID	=  "";
		String szEjbJndiName = "CoilCraneLdHdSeEJB";
		String szEjbMethod = "procY5CrnWrkOrdReq";
		String szRtnMsg = "";
		int intRtnVal  = 0;
		try{

			for(int i = 0; i < inDto.length; i++ ) {

				sYD_EQP_ID 			= ydDaoUtils.paraRecChkNull(inDto[i],"YD_EQP_ID");
				sYD_WRK_PROG_STAT	= ydDaoUtils.paraRecChkNull(inDto[i],"YD_EQP_WRK_MODE");
				sYD_SCH_CD			= ydDaoUtils.paraRecChkNull(inDto[i],"YD_SCH_CD"); // 스케쥴 코드
				sYD_CRN_SCH_ID		= ydDaoUtils.paraRecChkNull(inDto[i],"YD_CRN_SCH_ID");
				
				
				szLogMsg = "[JSP Session] 무인 크레인 체크 szYD_EQP_ID ="+sYD_EQP_ID+ "ydEqpDao.chkAutoCrn(szYD_EQP_ID) ="+ydEqpDao.chkAutoCrn(sYD_EQP_ID);
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				if(ydEqpDao.chkAutoCrn(sYD_EQP_ID) ){
					szLogMsg = "[JSP Session] " + "해당 크레인ID  정보: ["+ sYD_EQP_ID +"] 무인크레인은 interLock을 전송할 수 없습니다.";            		
            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szLogMsg);	
					return outRecord;
					
				}

				outRecSet  = JDTORecordFactory.getInstance().createRecordSet("YD");
				recPara   = JDTORecordFactory.getInstance().create();
				recPara.setField("V_YD_CRN_SCH_ID",sYD_CRN_SCH_ID);		
				outRecSet = dao.getInterLockList1(recPara);
				if(outRecSet == null || outRecSet.size() < 1){
					szLogMsg = "[JSP Session] " + "해당 크레인스케줄 ID  정보: ["+ sYD_CRN_SCH_ID +"]는 종료 되었거나 interLock이 발생었습니다.";            		
            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szLogMsg);	
					return outRecord;
				} else {
					
					recInTemp = JDTORecordFactory.getInstance().create();
//SJH03004
					recInTemp.setField("MSG_ID",           "Y5YDL007");
//					recInTemp.setField("JMS_TC_CD" 				, "Y5YDL007");
					recInTemp.setField("YD_EQP_ID",        sYD_EQP_ID);
					recInTemp.setField("YD_WRK_PROG_STAT", sYD_WRK_PROG_STAT);
					recInTemp.setField("YD_SCH_CD",        sYD_SCH_CD); // 스케쥴 코드
					recInTemp.setField("YD_CRN_SCH_ID",    sYD_CRN_SCH_ID);
					
					szLogMsg = "[YD_EQP_ID:" + i +"]"+ sYD_EQP_ID;
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					szLogMsg = "[YD_WRK_PROG_STAT:" + i +"]"+ sYD_WRK_PROG_STAT;
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					szLogMsg = "[YD_SCH_CD:" + i +"]"+ sYD_SCH_CD;
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					szLogMsg = "[YD_CRN_SCH_ID:" + i +"]"+ sYD_CRN_SCH_ID;
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
	
	
					EJBConnector ejbConn = new EJBConnector("default", "CoilCraneLdHdSeEJB", this);			
					szRtnMsg = (String)ejbConn.trx("procY5CrnWrkOrdReq", new Class[] { JDTORecord.class }, new Object[] { recInTemp });

					szLogMsg = "크레인 작업지시 호출["+szEjbJndiName+"."+szEjbMethod+"]szRtnMsg:" + szRtnMsg;
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
	
					if(szRtnMsg != "SUCCESS"){
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, "크레인 작업지시 호출 실패 하였습니다");	
						return outRecord;
					}
	        		
				}
				

			}
			
			outRecord.setField("RTN_CD" 	, "1");	
			return outRecord;
			
		}catch (Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
	}
	
	
	
	
	/**
	 * 야드관리 > 코일소재야드 > 설비/차량관리 > 야드보급순서  목록조회
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.03
	 */
	public GridData getSupplyInOrderList(GridData inDto) {
		GridData 		rtnGrd 		= new GridData();
		JDTORecordSet   outRecSet  	= null;
		
		CoilJspDAO 		dao 		= new CoilJspDAO();
		JDTORecord 		recPara		= null;
		
		try {
			
			
			ydUtils.putLog(szSessionName, "", inDto.getParam("EQP_GP"), YdConstant.DEBUG);
		
			// 파라미터 셋팅 
			recPara		= JDTORecordFactory.getInstance().create(); 			
			recPara.setField("V_LINE"		, inDto.getParam("EQP_GP"));	/*동구분*/
			recPara.setField("V_WORK_STAT"	, inDto.getParam("WORK_STAT"));	/*동구분*/
			recPara.setField("V_NEXT_PROC"	, "ALL");	/*동구분*/
			recPara.setField("V_PAGE_NO1"	, inDto.getParam("page_no"));
			recPara.setField("V_ROWCOUNT1"	, inDto.getParam("rowCount"));
			recPara.setField("V_PAGE_NO2"	, inDto.getParam("page_no"));
			recPara.setField("V_ROWCOUNT2"	, inDto.getParam("rowCount"));

			// DAO 호출
			/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilYDDao.getSupplyInOrderList*/
				outRecSet = dao.getSupplyInOrderList(recPara);
			
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
	}//end of getCoilYdStrlocModMgt
	
	
	
	/**
	 * 저장품 생성(사용화면 -> 저장위치수정)
	 * @ejb.interface-method
	 * @param JDTORecord
	 * @return JDTORecord
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.07
	 */
	public JDTORecord insYdStockInfo(JDTORecord inDto) throws DAOException {
		
		JDTORecord outRecord 	= JDTORecordFactory.getInstance().create();
		
		String szMethodName		= "insYdStockInfo";
		String szMsg			= "";
		String sSTL_NO			= "";
		String sSTATUS_FLAG     = "";
		String sDEL_YN          = "";
		YdStockDao ydStockDao   = new YdStockDao();
		int nRet                = 0;
		int nRet1               = 0;
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		
		try{
			
			sSTL_NO	= ydDaoUtils.paraRecChkNull(inDto,"STL_NO");

			// 코일STOCK 조회
			recPara = JDTORecordFactory.getInstance().create();
			JDTORecordSet rsResult1 = JDTORecordFactory.getInstance().createRecordSet("");
			recPara.setField("STL_NO", sSTL_NO);
			
			nRet1 = ydStockDao.getYdStock(recPara, rsResult1, 0);
			if(nRet1 <= 0){
				sSTATUS_FLAG = "I";
			} else {
				rsResult1.first();
				JDTORecord recGetComm1 = JDTORecordFactory.getInstance().create();
				recGetComm1 = rsResult1.getRecord();
				sDEL_YN = ydDaoUtils.paraRecChkNull(recGetComm1,"DEL_YN");
				
				if(sDEL_YN.equals("Y")){
					sSTATUS_FLAG = "U";
				} else {
					szMsg = "저장품에 있습니다. Error :: [" + nRet1 + "] (" + sSTL_NO + ") PARAMETER ERROR" ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					outRecord.setField("RTN_CD", "-1");
					outRecord.setField("RTN_MSG", szMsg);
					return outRecord;
					
				}
			}
			
			if(sSTATUS_FLAG.equals("I")){
				// 코일공통 조회
				recPara  = JDTORecordFactory.getInstance().create();
				JDTORecordSet rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				recPara.setField("COIL_NO", sSTL_NO);
				
				nRet = ydStockDao.getYdStock(recPara, rsResult, 8);
				if(nRet <= 0){
					szMsg = "COILCOMM[코일공통] Error :: [" + nRet1 + "] (" + sSTL_NO + ") PARAMETER ERROR" ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					outRecord.setField("RTN_CD", "-1");
					outRecord.setField("RTN_MSG", szMsg);
					return outRecord;
					//throw new DAOException(getClass().getName());
				}
				
				rsResult.first();
				JDTORecord recGetComm = JDTORecordFactory.getInstance().create();
				recGetComm = rsResult.getRecord();
				
				//=====================================================
				// 항목편집
				//=====================================================
				JDTORecord recEditRec  = JDTORecordFactory.getInstance().create();
				 
				nRet = this.editCHrShearWrkWr(recGetComm, recEditRec);
				if(nRet < 0){
					szMsg = "항목편집 Error [" + nRet + "]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					outRecord.setField("RTN_CD", "-2");
					outRecord.setField("RTN_MSG", szMsg);
					return outRecord;
					//throw new DAOException(getClass().getName());
				}
	
				szMsg = "항목편집 SUCCESSFULL";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);				
	
				//=====================================================
				// 저장품 생성
				//=====================================================
				recEditRec.setField("REGISTER", "HRYDJ007");
				nRet = ydStockDao.insYdStock(recEditRec);
				if(nRet < 0){
					szMsg = "YD_STOCK[저장품] INSERT ERROR :: [" + nRet + "]" ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					outRecord.setField("RTN_CD", "-3");
					outRecord.setField("RTN_MSG", szMsg);
					return outRecord;
					//throw new DAOException(getClass().getName());
				}
	
				szMsg = "YD_STOCK[저장품] INSERT SUCCESSFULL (" + ydDaoUtils.paraRecChkNull(recEditRec, "STL_NO") + ")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);							
	
			} else if(sSTATUS_FLAG.equals("U")){
				recPara  = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", sSTL_NO);
				recPara.setField("DEL_YN", "N");
				    
				nRet = ydStockDao.updYdStock(recPara, 0);
			    
				if (nRet < 0) {
					if (nRet == -1) {
						szMsg = "routine error!!!, ErrorCode:" + nRet;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					} else {
						szMsg = "parameter error!!!, ErrorCode:" + nRet;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					}
					//return outRecSet;
				}				
				
				
			}

			
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
			
			outRecord.setField("RTN_CD", "");
			outRecord.setField("RTN_MSG", "저장품 등록 후 " + szMsg);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return outRecord;
	}
	
	/**
	 * [C열연정정작업실적수신] 항목 편집
	 * return  1: 항목 편집 성공
	 * 		  -1: 항목 편집 실패	
	 * @param inRecord
	 * @param outRec
	 * @throws JDTOException
	 */
	public int editCHrShearWrkWr(JDTORecord inRecord, JDTORecord recEditRec) throws JDTOException {

		String szMethodName	=	"editCHrShearWrkWr";
		String szMsg="";
		String szSTL_APPEAR_GP 	="";
		String szYD_MTL_ITEM	=""; 
		String szSTL_PROG_CD	=""; 
		String szYD_AIM_YD_GP	="";
		String szBRANCH_CD		="";
		String szNEXT_PROC		="";
		String szYD_AIM_RT_GP	="";
		String sYD_AIM_BAY_GP	="";
		double  iW_GP              = 0; 
		double  iOUTDIA            = 0;
		String sW_GP            ="";
		String sOUTDIA          ="";
		try{
			// 야드재료품목
			szSTL_APPEAR_GP = ydDaoUtils.paraRecChkNull(inRecord,"STL_APPEAR_GP");
			if(szSTL_APPEAR_GP.equals("E")){
				szYD_MTL_ITEM = "CM";
				szYD_AIM_YD_GP= "H";
			}else if(szSTL_APPEAR_GP.equals("Y")){
				szYD_MTL_ITEM = "CG";
				szYD_AIM_YD_GP= "J";
			}
			// 야드목표행선구분
			szNEXT_PROC   = ydDaoUtils.paraRecChkNull(inRecord,"NEXT_PROC");
			szBRANCH_CD   = ydDaoUtils.paraRecChkNull(inRecord,"BRANCH_CD");
			szSTL_PROG_CD = ydDaoUtils.paraRecChkNull(inRecord,"CURR_PROG_CD");
			
			if(!szNEXT_PROC.equals("")){
				sYD_AIM_BAY_GP = szNEXT_PROC.substring(0, 1);
			}
			if(szSTL_PROG_CD.equals("G")){
				szYD_AIM_RT_GP = "G2";
			}else if(szSTL_PROG_CD.equals("H")){
				szYD_AIM_RT_GP = "H2";
			}else if(szSTL_PROG_CD.equals("K")){
				szYD_AIM_RT_GP = "K2";
			}else if(szSTL_PROG_CD.equals("C")){
				
				if(!szNEXT_PROC.equals("")){
					if(szNEXT_PROC.equals("HH")){
						szYD_AIM_RT_GP = "CF";
					}
				}else{
					if(szBRANCH_CD.equals("3S")||szBRANCH_CD.equals("HS")){
						szYD_AIM_RT_GP = "CF";						
					}else if(szBRANCH_CD.equals("GH")){
						szYD_AIM_RT_GP = "CE";	
					}else if(szBRANCH_CD.equals("EH")){
						szYD_AIM_RT_GP = "CG";	
					}else if(szBRANCH_CD.equals("FH")){
						szYD_AIM_RT_GP = "CH";	
					}else if(szBRANCH_CD.equals("DH")){
						szYD_AIM_RT_GP = "CI";	
					}
				}
			}
			
			recEditRec.setField("STL_NO"			 , ydDaoUtils.paraRecChkNull(inRecord,"COIL_NO")); 					
			recEditRec.setField("YD_MTL_T"			 , ydDaoUtils.paraRecChkNull(inRecord,"SHEAR_T")); 		
			recEditRec.setField("YD_MTL_W"			 , ydDaoUtils.paraRecChkNull(inRecord,"SHEAR_W")); 		    
			recEditRec.setField("YD_MTL_L"			 , ydDaoUtils.paraRecChkNull(inRecord,"COIL_LEN"));		
			recEditRec.setField("YD_MTL_WT"			 , ydDaoUtils.paraRecChkNull(inRecord,"COIL_WT"));	
			recEditRec.setField("COIL_INDIA"		 , ydDaoUtils.paraRecChkNull(inRecord,"COIL_INDIA"));	
			recEditRec.setField("COIL_OUTDIA"		 , ydDaoUtils.paraRecChkNull(inRecord,"COIL_OUTDIA"));	
			recEditRec.setField("PLNT_PROC_CD"		 , ydDaoUtils.paraRecChkNull(inRecord,"PLNT_PROC_CD"));				
			recEditRec.setField("ORD_YEOJAE_GP"		 , ydDaoUtils.paraRecChkNull(inRecord,"ORD_YEOJAE_GP"));			
			recEditRec.setField("ORD_NO"			 , ydDaoUtils.paraRecChkNull(inRecord,"ORD_NO")); 			
			recEditRec.setField("ORD_DTL"			 , ydDaoUtils.paraRecChkNull(inRecord,"ORD_DTL")); 
			recEditRec.setField("CUST_CD"			 , ydDaoUtils.paraRecChkNull(inRecord,"CUST_CD")); 			
			recEditRec.setField("DEMANDER_CD"		 , ydDaoUtils.paraRecChkNull(inRecord,"DEMANDER_CD"));
			recEditRec.setField("SPEC_ABBSYM"		 , ydDaoUtils.paraRecChkNull(inRecord,"SPEC_ABBSYM"));
			recEditRec.setField("HYSCO_TRANS_GP"	 , ydDaoUtils.paraRecChkNull(inRecord,"HYSCO_TRANS_GP"));		
		  
			recEditRec.setField("HCR_GP"		 	 , ydDaoUtils.paraRecChkNull(inRecord,"HCR_GP"));	
			recEditRec.setField("COOL_METHOD"		 , ydDaoUtils.paraRecChkNull(inRecord,"COOL_METHOD"));
			recEditRec.setField("COOL_DONE_GP"		 , ydDaoUtils.paraRecChkNull(inRecord,"COOL_DONE_GP"));
			recEditRec.setField("YD_CONVEYOR_BRANCH_CD" , szBRANCH_CD);
			recEditRec.setField("PTOP_PLNT_GP"		 , ydDaoUtils.paraRecChkNull(inRecord,"PTOP_PLNT_GP"));				
			recEditRec.setField("OVERALL_STAMP_GRADE", ydDaoUtils.paraRecChkNull(inRecord,"OVERALL_STAMP_GRADE"));
			recEditRec.setField("STL_PROG_CD"		 , szSTL_PROG_CD);
			recEditRec.setField("STL_APPEAR_GP"		 , szSTL_APPEAR_GP);
			recEditRec.setField("YD_MTL_ITEM"		 , szYD_MTL_ITEM);
			recEditRec.setField("YD_AIM_YD_GP"		 , szYD_AIM_YD_GP);
			recEditRec.setField("YD_AIM_RT_GP"		 , szYD_AIM_RT_GP);
			recEditRec.setField("YD_AIM_BAY_GP"		 , sYD_AIM_BAY_GP);
			
//			폭 구분 
//			iW_GP = ydDaoUtils.paraRecChkNullInt(inRecord,"SHEAR_W");
			iW_GP = ydDaoUtils.paraRecChkNullDouble(inRecord,"SHEAR_W");
			
			if (iW_GP < 1601) {
				sW_GP = "M";
			} else {
				sW_GP = "L";
			}
			recEditRec.setField("YD_MTL_W_GP"		 , sW_GP);
			
//외경그룹			
//			iOUTDIA = Integer.parseInt(ydDaoUtils.paraRecChkNull(inRecord,"COIL_OUTDIA"));
			iOUTDIA = ydDaoUtils.paraRecChkNullDouble(inRecord,"COIL_OUTDIA");
			
			if (iOUTDIA <= 1280) {
				sOUTDIA = "A";
			} else if (( iOUTDIA > 1280 )&&( iOUTDIA <= 1930)) { 
				sOUTDIA = "B";
			} else if ( iOUTDIA > 1930 ) {
				sOUTDIA = "C";
			}
			
			recEditRec.setField("YD_COIL_OUTDIA_GRP_GP"		 , sOUTDIA);
			
			
			
		} catch(Exception e){
			return -1;
		}
		return 1;
	} //end of editCHrShearWrkWr()
	
	
	
	
	/**
	 * 이적/이송 대상 폭구분 체크 
	 * @ejb.interface-method
	 * @param JDTORecord[]
	 * @return JDTORecord
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.25
	 */
	public JDTORecord getStkColWidthGp(JDTORecord []  inDto) throws DAOException{
	
		JDTORecord 			outRecord 		= JDTORecordFactory.getInstance().create(); // 
		JDTORecord 			recPara			= JDTORecordFactory.getInstance().create();
		JDTORecord 			recTmp			= JDTORecordFactory.getInstance().create();
		JDTORecordSet   	outRecSet  		= null;
		
		CoilJspDAO 			dao 			= new CoilJspDAO();
		
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
				
				//////////////////////////////////////////////////////////////////////////////////////////////////////
				// 폭구분 체크 	
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("FROM_STK_COL_GP",		sFROM);
				recPara.setField("TO_STK_COL_GP",		sTO);
				
				outRecSet = dao.getStkColWidthGp(recPara);

				if(outRecSet == null && outRecSet.size() == 0){
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, "폭구분 조회에 실패 하였습니다.");	
					return outRecord;
				}else{
					outRecSet.first();
					recTmp = outRecSet.getRecord(0);
//					if(recTmp.getFieldString("GRP_GP_CHK").equals("0")){ // 이적불가(외경군 불일치 )
//						outRecord.setField("RTN_CD" 	, "-1");	
//						outRecord.setField("RTN_MSG" 	, "외경군이 일치하지 않아 이적이 불가 합니다.");	
//						return outRecord;
//					}
					if(recTmp.getFieldString("STAT_CHK").equals("0")){ // 이적불가
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, "폭구분이 일치하지 않아 이적이 불가 합니다.");	
						return outRecord;
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
		
	}// end getStkColWidthGp
	
	

	/**
	 * 오퍼레이션명 : 스케쥴 재전송 (크레인작업관리 화면)
	 * 스케쥴 취소
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord reSendSchCoilYdCrnWorkMgt(JDTORecord msgRecord)throws JDTOException  {
		int intRtnVal          		= 0;		
		int intRtnVal3          	= 0;		
		
		String szLogMsg         	= null;
		String szMethodName    		= "reSendSchCoilYdCrnWorkMgt";		
		String szOperationName 		= "스케쥴 재전송 (크레인작업관리 화면)";
		
		String szStkPos        		= null;
		String szStkColGp      		= null;
		String szStkBedNo      		= null;
		String szStkLyrNo      		= null;
		
		JDTORecord    recPara  		= null;
		JDTORecord    recInPara  	= null;
		JDTORecord    recTemp  		= null;
		JDTORecord    recTemp3  	= null;
		
		JDTORecord    recSet   		= null;
		JDTORecord    inRecord   	= JDTORecordFactory.getInstance().create(); // 
		JDTORecord 	  outRecord 	= JDTORecordFactory.getInstance().create(); // 
		JDTORecord 	  outRecord1 	= JDTORecordFactory.getInstance().create(); // 
		JDTORecord 	  outRecord2 	= JDTORecordFactory.getInstance().create(); // 
		JDTORecord 	  outRecord3 	= JDTORecordFactory.getInstance().create(); // 
		JDTORecord    inRecord3   	= JDTORecordFactory.getInstance().create(); // 
		
		String szOldStkPos     = null;
		String szOldStkColGp   = null;
		String szOldStkBedNo   = null;
		String szOldStkLyrNo   = null;
		boolean bool = false;
		YdStkBedDao ydStkBedDao = new YdStkBedDao();	
		YdStkLyrDao ydStkLyrDao        = new YdStkLyrDao();
		YdCrnSchDao ydCrnSchDao        = new YdCrnSchDao();
		YdCrnWrkMtlDao  ydCrnWrkMtlDao = new YdCrnWrkMtlDao();
		CoilYdToLocDcsnUtil ydToLocDcsnUtil = new CoilYdToLocDcsnUtil();
		ymCommonDAO dao = ymCommonDAO.getInstance();
		
		JDTORecordSet    outRecSet  = null;
		JDTORecordSet    outRecSet1  = null;
		JDTORecordSet    outRecSet2  = null;
		JDTORecordSet    outRecSet3  = null;
		
		String szYdWrkProgStat 		= "";		
		String szSendYdWrkProgStat 	= "";
		String szYdGp  				= "";
		String szYdSchCd 			= "";
	
		String szJMS_TC_CD 			= "";
		String szEjbMethod	 		= "";
	    String szYD_EQP_ID 			= "";
	    String szRtnMsg 			= "";
	    String szYdSchId 			= "";
	    EJBConnector ejbConn 		= null;
	    String szYdGpTemp 			= "";
	    String szEqpGp 				= "";     // 변경 설비구분 
	    String szEqpGpBefo 			= ""; // 기존 설비구분 
	    String szYdWbookId 			= ""; //작업예약 ID
	    String szRtnMsg1			= null;
	    String szLayer    			= "";
	    String sTAG_STL_NO 			= "";
	    String sRTN_CD				= "";
	    String sRTN_MSG				= "";
	    String sYD_GP               = "";
        String szYdSchcd 			= "";
        String szYD_CRN_SCH_ID      = "";
        String sDN_YD_STK_LYR_XAXIS 		= "";
        String sDN_YD_STK_BED_XAXIS_TOL 	= "";
        String sDN_YD_STK_LYR_YAXIS 		= "";
        String sDN_YD_STK_BED_YAXIS_TOL 	= "";
        String sQueryId ="";
        // 150619 hun 추가 무인화작업
        String sDN_YD_STK_LYR_ZAXIS 		= "";
        String sDN_YD_STK_BED_ZAXIS_TOL 	= "";
        
		
		try {
			
			szLogMsg = "JSP-SESSION [스케쥴 재전송 (크레인작업관리 화면)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
//			sTAG_STL_NO	= yddatautil.setDataDefault(msgRecord.getField("STL_NO"), "");
			szYdSchId 	= yddatautil.setDataDefault(msgRecord.getField("YD_CRN_SCH_ID"), "");
//			szStkPos 	= yddatautil.setDataDefault(msgRecord.getField("YD_DN_WO_LOC"), "");
			
			
			// 1. 기존 To 위치 정보 와 변경 To 위치 정보 
			outRecSet  = JDTORecordFactory.getInstance().createRecordSet("YD");
			
			recPara   = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CRN_SCH_ID",szYdSchId);		
			
			intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, outRecSet, 0);
			
			if(intRtnVal < 0 )
			{
				szLogMsg = "[JSP Session] " + szOperationName + "해당 크레인스케줄 ID  정보: ["+ szYdSchId +" ]조회시 ERROR발생";            		
        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szLogMsg);	
				return outRecord;
			}else if(intRtnVal == 0 ){
				szLogMsg = "[JSP Session] " + szOperationName + "해당 스케줄크레인스케줄 ID  정보: ["+ szYdSchId + "] 가 존재하지않습니다";            		
        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szLogMsg);	
				return outRecord;
        		
			}
			
			
			outRecSet.first();
			
			recTemp   = JDTORecordFactory.getInstance().create();			
			recTemp = outRecSet.getRecord();
			//recTemp : 스케줄 정보
			
			szYdWrkProgStat = ydDaoUtils.paraRecChkNull(recTemp, "YD_WRK_PROG_STAT");
			szYdSchCd       = ydDaoUtils.paraRecChkNull(recTemp, "YD_SCH_CD");

			recPara   = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CRN_SCH_ID", yddatautil.setDataDefault(msgRecord.getField("YD_CRN_SCH_ID"), ""));
			
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
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szLogMsg);	
				return outRecord;
			} else if (intRtnVal < 0){
				
				szLogMsg = "[JSP Session] " + szOperationName + "해당 스케줄에 해당되는 재료 조회시 ERROR";
        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szLogMsg);	
				return outRecord;
			}
			
			//크레인 스케줄의 값을 다시 재전송한다. 
        	
			szYD_CRN_SCH_ID			= ydDaoUtils.paraRecChkNull(recTemp, "YD_CRN_SCH_ID");
			String szYD_WRK_PROG_STAT =	ydDaoUtils.paraRecChkNull(recTemp, "YD_L2_REQUEST_STAT");
			
			recInPara = JDTORecordFactory.getInstance().create();
    		//작업지시 전문 전송 data setup
			recInPara.setField("MSG_ID", 			"YDY5L004");
        	recInPara.setField("YD_CRN_SCH_ID",    	szYD_CRN_SCH_ID);
        	recInPara.setField("YD_WRK_PROG_STAT", 	szYD_WRK_PROG_STAT);
        	recInPara.setField("YD_SCH_CD",        	ydDaoUtils.paraRecChkNull(recTemp, "YD_SCH_CD"));
        	recInPara.setField("YD_GP",            	ydDaoUtils.paraRecChkNull(recTemp, "YD_GP"));
        	recInPara.setField("MODIFIER", 			"YDSYSTEM");
        	recInPara.setField("MSG_GP", 			"R");
        	ydDelegate.sendMsg(recInPara);
        	szLogMsg = "["+szOperationName+"] ["+szYD_WRK_PROG_STAT+"]인 스케줄["+szYD_CRN_SCH_ID+"]을 재전송";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			outRecord.setField("RTN_CD" 	, "1");	
			return outRecord;

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
			
		} 		
	}	// end of updToPosFix
	
	


	/**
	 * 권하위치 변경 (크레인작업관리 화면)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecord updToPosFixCoilHold(JDTORecord inDto) throws DAOException {
		
		int intRtnVal          		= 0;		
		int intRtnVal3          	= 0;		
		
		String szLogMsg         	= null;
		String szMethodName    		= "updToPosFixCoilHold";		
		String szOperationName 		= "권하위치 변경 (크레인작업관리 화면Hold)";
		
		String szStkPos        		= null;
		String szStkColGp      		= null;
		String szStkBedNo      		= null;
		String szStkLyrNo      		= null;
		
		JDTORecord    recPara  		= null;
		JDTORecord    recInPara  	= null;
		JDTORecord    recTemp  		= null;
		JDTORecord    recTemp3  	= null;
		
		JDTORecord    recSet   		= null;
		JDTORecord    inRecord   	= JDTORecordFactory.getInstance().create(); // 
		JDTORecord 	  outRecord 	= JDTORecordFactory.getInstance().create(); // 
		JDTORecord 	  outRecord1 	= JDTORecordFactory.getInstance().create(); // 
		JDTORecord 	  outRecord2 	= JDTORecordFactory.getInstance().create(); // 
		JDTORecord 	  outRecord3 	= JDTORecordFactory.getInstance().create(); // 
		JDTORecord    inRecord3   	= JDTORecordFactory.getInstance().create(); // 
		
		String szOldStkPos     = null;
		String szOldStkColGp   = null;
		String szOldStkBedNo   = null;
		String szOldStkLyrNo   = null;
		boolean bool = false;
		YdStkBedDao ydStkBedDao = new YdStkBedDao();	
		YdStkLyrDao ydStkLyrDao        = new YdStkLyrDao();
		YdCrnSchDao ydCrnSchDao        = new YdCrnSchDao();
		YdCrnWrkMtlDao  ydCrnWrkMtlDao = new YdCrnWrkMtlDao();
		CoilYdToLocDcsnUtil ydToLocDcsnUtil = new CoilYdToLocDcsnUtil();
		ymCommonDAO dao = ymCommonDAO.getInstance();
		
		JDTORecordSet    outRecSet  = null;
		JDTORecordSet    outRecSet1  = null;
		JDTORecordSet    outRecSet2  = null;
		JDTORecordSet    outRecSet3  = null;
		
		String szYdWrkProgStat 		= "";		
		String szSendYdWrkProgStat 	= "";
		String szYdGp  				= "";
		String szYdSchCd 			= "";
	
		String szJMS_TC_CD 			= "";
		String szEjbMethod	 		= "";
	    String szYD_EQP_ID 			= "";
	    String szRtnMsg 			= "";
	    String szYdSchId 			= "";
	    EJBConnector ejbConn 		= null;
	    String szYdGpTemp 			= "";
	    String szEqpGp 				= "";     // 변경 설비구분 
	    String szEqpGpBefo 			= ""; // 기존 설비구분 
	    String szYdWbookId 			= ""; //작업예약 ID
	    String szRtnMsg1			= null;
	    String szLayer    			= "";
	    String sTAG_STL_NO 			= "";
	    String sRTN_CD				= "";
	    String sRTN_MSG				= "";
	    String sYD_GP               = "";
        String szYdSchcd 			= "";
        String szYD_CRN_SCH_ID      = "";
        String sDN_YD_STK_LYR_XAXIS 		= "";
        String sDN_YD_STK_BED_XAXIS_TOL 	= "";
        String sDN_YD_STK_LYR_YAXIS 		= "";
        String sDN_YD_STK_BED_YAXIS_TOL 	= "";
        String sQueryId ="";
        // 150619 hun 추가 무인화작업
        String sDN_YD_STK_LYR_ZAXIS 		= "";
        String sDN_YD_STK_BED_ZAXIS_TOL 	= "";
        
		
		try {
			
			
			szLogMsg = "JSP-SESSION [권하위치 변경 (크레인작업관리 화면)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			sTAG_STL_NO	= yddatautil.setDataDefault(inDto.getField("STL_NO"), "");
			szYdSchId 	= yddatautil.setDataDefault(inDto.getField("YD_CRN_SCH_ID"), "");
			szStkPos 	= yddatautil.setDataDefault(inDto.getField("YD_DN_WO_LOC"), "");
			szStkLyrNo 	= yddatautil.setDataDefault(inDto.getField("YD_DN_WO_LAYER"), "");
			sYD_GP 		= szStkPos.substring(0, 1);
			szYD_EQP_ID = yddatautil.setDataDefault(inDto.getField("YD_EQP_ID"), "");
			
			//
			// 1. 기존 To 위치 정보 와 변경 To 위치 정보 
			outRecSet  = JDTORecordFactory.getInstance().createRecordSet("YD");
			
			recPara   = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CRN_SCH_ID",szYdSchId);		
			
			intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, outRecSet, 0);
			
			if(intRtnVal < 0 )
			{
				szLogMsg = "[JSP Session] " + szOperationName + "해당 크레인스케줄 ID  정보: ["+ szYdSchId +" ]조회시 ERROR발생";            		
        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szLogMsg);	
				return outRecord;
			}else if(intRtnVal == 0 ){
				szLogMsg = "[JSP Session] " + szOperationName + "해당 스케줄크레인스케줄 ID  정보: ["+ szYdSchId + "] 가 존재하지않습니다";            		
        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szLogMsg);	
				return outRecord;
        		
			}
			
			
			outRecSet.first();
			
			recTemp   = JDTORecordFactory.getInstance().create();			
			recTemp = outRecSet.getRecord();
			
			szOldStkPos   		= yddatautil.setDataDefault(recTemp.getFieldString("YD_DN_WO_LOC"),"");					
			szOldStkLyrNo 		= yddatautil.setDataDefault(recTemp.getFieldString("YD_DN_WO_LAYER"),"");
			szYdWbookId 		= ydDaoUtils.paraRecChkNull(recTemp, "YD_WBOOK_ID");
			szSendYdWrkProgStat = ydDaoUtils.paraRecChkNull(recTemp, "YD_WRK_PROG_STAT"); //현 스케줄 작업 진행상태(DB)
			szYdSchcd 			= ydDaoUtils.paraRecChkNull(recTemp, "YD_SCH_CD");
			
			szLogMsg = "[JSP Session] " + szOperationName + "현 스케줄 작업 진행상태(DB)[ " + szSendYdWrkProgStat  +" ]"  ;
    		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
    		
			
			szLogMsg = "[JSP Session] " + szOperationName + "DB 권하지시위치 [ " + szOldStkPos + "-" + szOldStkLyrNo +"]"  ;
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			
			
			szLogMsg = "[JSP Session] " + szOperationName + "입력받은 권하지시위치 [" + szStkPos + "]"  ;
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			
			if ("".equals(szStkPos)){		
				szLogMsg = "[JSP Session] " + szOperationName + "변경 권하지시위치 정보가 없습니다."  ;
        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szLogMsg);	
				return outRecord;
			}
			
			
			if(szOldStkPos.equals(szStkPos)){
				szLogMsg = "[JSP Session] " + szOperationName + "입력된 권하위치가 기존 권하위치와 같습니다."  ;
        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szLogMsg);	
				return outRecord;
				
			}
				
			
			if(szStkPos.length() >=8)
			{
				szStkColGp = szStkPos.substring(0, 6); 
				szStkBedNo = szStkPos.substring(6, 8);
				szEqpGp    = szStkColGp.substring(2,4);
			}else{
				szLogMsg = "[JSP Session] " + szOperationName + "변경 권하지시위치 정보가 맞지 않습니다"  ;
        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szLogMsg);	
				return outRecord;
			}
			
			//-----------------------------------------------------------------------
			
			inRecord = JDTORecordFactory.getInstance().create();
			inRecord.setField("YD_STK_COL_GP" 	, szStkColGp);	
			inRecord.setField("YD_STK_BED_NO" 	, szStkBedNo);	
			inRecord.setField("YD_STK_LYR_NO" 	, szStkLyrNo);	

			
			outRecSet  	= JDTORecordFactory.getInstance().createRecordSet("YD");
			szLogMsg = "[JSP Session] " + szOperationName +  "입력한 적치단 정보조회";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			intRtnVal = ydStkLyrDao.getYdStklyr(inRecord, outRecSet, 0);
			if (intRtnVal == 0){
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, "저장위치 이상");	
				return outRecord;
			} else if ( intRtnVal > 0 )	{
				recTemp   	= JDTORecordFactory.getInstance().create();
				outRecSet.first();
				recTemp 	= outRecSet.getRecord();
				
				String sYD_STK_LYR_MTL_STAT	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_MTL_STAT"); 
				String sYD_STK_LYR_ACT_STAT	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_ACT_STAT"); 
				
				ydUtils.putLog(szSessionName, szMethodName, "sYD_STK_LYR_MTL_STAT:" + sYD_STK_LYR_MTL_STAT, YdConstant.DEBUG);
				ydUtils.putLog(szSessionName, szMethodName, "sYD_STK_LYR_MTL_STAT:" + sYD_STK_LYR_MTL_STAT, YdConstant.DEBUG);
				ydUtils.putLog(szSessionName, szMethodName, "sYD_STK_LYR_ACT_STAT:" + sYD_STK_LYR_ACT_STAT, YdConstant.DEBUG);
				
				
				if(sYD_GP.equals("J")&& szYdSchcd.substring(2, 4).equals("HC")){
					//결로재 적치위치
					if ((sYD_STK_LYR_MTL_STAT.equals("E")) && (sYD_STK_LYR_ACT_STAT.equals("S"))) {
						
					} else {	
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, "제품이 있거나 결로재 적치위치가 아닙니다.");	
						return outRecord;
					}
				}else{
					//일반재 적치위치
					if ((sYD_STK_LYR_MTL_STAT.equals("E")) && (sYD_STK_LYR_ACT_STAT.equals("E"))) {
					
					} else {	
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, "제품이 있거나 예약상태/금지  입니다.");	
						return outRecord;
					}
				}
				
			}
			if (sYD_GP.equals("H")){
				outRecord1 = ydToLocDcsnUtil.CoilLyrBaseCheck(sTAG_STL_NO, inRecord);
				sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
				sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
				
				if (!("1".equals(sRTN_CD))) {
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, sRTN_MSG);	
					return outRecord;
				}

			} else {
				//권하위치 기본 체크 예외동 처리 (A,B,C,L,M,60,70) 결로재 적치 포함 
//				if( szStkColGp.substring(2, 4).equals("80")
//						||szStkColGp.substring(2, 4).equals("70")
//						|| szStkColGp.substring(1, 2).equals("L")
//						|| szStkColGp.substring(1, 2).equals("M")
//						|| szStkColGp.substring(1, 2).equals("C")
//						|| szStkColGp.substring(1, 2).equals("B")
//						|| szStkColGp.substring(1, 2).equals("A")
//						||(sYD_GP.equals("J")&& szYdSchcd.substring(2, 4).equals("HC"))
//					) {  // 가상 bed임
//					
//				} else {
//					outRecord1 = ydToLocDcsnUtil.CoilGdsLyrBaseCheck(sTAG_STL_NO, inRecord);
//					sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
//					sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
//					
//					if (!("1".equals(sRTN_CD))) {
//						outRecord.setField("RTN_CD" 	, "0");	
//						outRecord.setField("RTN_MSG" 	, sRTN_MSG);	
//						return outRecord;
//					}
//				}	
				
				szLogMsg = "[JSP Session] " + szOperationName +  "권하위치변경 시 BASECHECK를 생략 하기로 함(출하팀과 협의)";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			}
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			//-----------------------------------------------------------------------
			
			//-----------------------------------------------------------------------
			//	권하위치 변경할시에 작업지시 취소전문 발생 (데이터 변경전정보)
			//-----------------------------------------------------------------------

			//recTemp : 스케줄 정보
			
			szYdWrkProgStat = ydDaoUtils.paraRecChkNull(recTemp, "YD_WRK_PROG_STAT");
			szYdSchCd       = ydDaoUtils.paraRecChkNull(recTemp, "YD_SCH_CD");

			recPara   = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CRN_SCH_ID", yddatautil.setDataDefault(inDto.getField("YD_CRN_SCH_ID"), ""));
			
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
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szLogMsg);	
				return outRecord;
			} else if (intRtnVal < 0){
				
				szLogMsg = "[JSP Session] " + szOperationName + "해당 스케줄에 해당되는 재료 조회시 ERROR";
        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szLogMsg);	
				return outRecord;
			}
		
			//기존 산적위치 정보가 올바르지 않거나 없을 경우는 기존산적위치를 삭제할 수없으므로 건너띈다.
			szLogMsg = "[szOldStkPos] " + szOldStkPos + "해당 스케줄에 해당되는 재료 조회";
			if( szOldStkPos.length() == 8 && (!szOldStkPos.equals("XX010101")))
			{	
				szOldStkColGp = szOldStkPos.substring(0, 6); 
				szOldStkBedNo = szOldStkPos.substring(6, 8);
				szLogMsg = "[JSP Session] " + szOperationName +  "기존위치정보 :"+ szOldStkColGp  + "-"+ szOldStkBedNo +":" +szOldStkLyrNo;				
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
        	
					//실제로는 크레인작업재료의 개수만 필요함				
//				outRecSet.first();
//				for (int nLoop =0 ; nLoop<outRecSet.size();nLoop++ ){
					 
				inRecord3 = JDTORecordFactory.getInstance().create();
				inRecord3.setField("YD_STK_COL_GP" 	, szOldStkColGp);	
				inRecord3.setField("YD_STK_BED_NO" 	, szOldStkBedNo);	
				inRecord3.setField("YD_STK_LYR_NO" 	, szOldStkLyrNo);	

				outRecSet3  	= JDTORecordFactory.getInstance().createRecordSet("YD");
				szLogMsg = "[JSP Session] " + szOperationName +  "권하적치단 정보조회";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				intRtnVal3 = ydStkLyrDao.getYdStklyr(inRecord3, outRecSet3, 0);
				if (intRtnVal3 == 0){
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, "저장위치 이상");	
					return outRecord;
				} else if ( intRtnVal3 > 0 )	{
					
					szLogMsg = "[JSP Session] " + szOperationName +  "권하적치단 정보조회 정상pass";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				}
			}
			
			//권하지시베드조회
			outRecSet1 = JDTORecordFactory.getInstance().createRecordSet("");
			outRecord2 = JDTORecordFactory.getInstance().create();
			outRecord2.setField("YD_STK_COL_GP", szStkColGp);
			outRecord2.setField("YD_STK_BED_NO", szStkBedNo );
			outRecord2.setField("YD_STK_LYR_NO", szStkLyrNo);    
			/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedLyr*/
			intRtnVal = ydStkBedDao.getYdStkbed(outRecord2, outRecSet1, 304);
			if(intRtnVal <= 0){
				szLogMsg="Y5UpdGradStkLyr getYdStkbed : execution failed";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" , "0");	
				outRecord.setField("RTN_MSG", szLogMsg);	
				return outRecord;

			}
			
			
			
			//-----------------------------------------------------------------------
			// 150713 hun 크레인 무인화 TB_YD_CRNSCH 변경위치 임시 저장
			szYdWrkProgStat = "5";
			szYD_CRN_SCH_ID = yddatautil.setDataDefault(inDto.getField("YD_CRN_SCH_ID"), "");
			szYdWbookId 		= ydDaoUtils.paraRecChkNull(recTemp, "YD_WBOOK_ID");
            
            sQueryId = "com.inisteel.cim.ym.dao.ydCarPointdao.ydCrnSchDao.upYdCrnSchLocStat";
//			intRtnVal = dao.updateData(sQueryId,new Object[]{ szStkPos, sTAG_STL_NO, szStkLyrNo, "R", szYD_CRN_SCH_ID  });
			intRtnVal = dao.updateData(sQueryId,new Object[]{ szStkPos, sTAG_STL_NO, szStkLyrNo, szYdWrkProgStat, szYD_CRN_SCH_ID  });
			
			
        	if (intRtnVal < 1)
			{
        		szLogMsg = "[JSP Session] " + szOperationName +   "TB_YD_CRNSCH 저장 실패 [ " + intRtnVal +" ] ";
        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szLogMsg);	
				return outRecord;
			}else{
        	
            	szLogMsg = "[JSP Session] " + szOperationName +   "TB_YD_CRNSCH 저장 성공 [ " + intRtnVal + " ] ";
            	ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
            	
            	
            	
            	szJMS_TC_CD = "YDY5L004";
            	recPara.setField("JMS_TC_CD", 			szJMS_TC_CD);
				recPara.setField("YD_EQP_ID" , 			szYD_EQP_ID);
				recPara.setField("YD_WRK_PROG_STAT" , 	"5"); //  권하위치변경 요구상태
				
				ydUtils.displayRecord(szOperationName, recPara);
				
				//EJB Method Call
				ejbConn = new EJBConnector("default", "CoilCraneLdHdSeEJB", this);		
				szRtnMsg = (String)ejbConn.trx("procY5CrnWrkOrdReq", new Class[] { JDTORecord.class }, new Object[] { recPara });	
				
				szLogMsg = "[JSP Session] " + szOperationName + " 크레인 무인화(state=5) 작업재지시  - 리턴메세지 : [" + szRtnMsg +"] ";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
            	
            	
        		
			}
				
			outRecord.setField("RTN_CD" 	, "1");	
			return outRecord;

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
			
		} 		
	}	// end of updToPosFix
	
	

	/**
	 * 권하위치 변경 (EAI요청 Y5YDL015 권하위치 변경가능 응답시 호출)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecord updToPosFixCoilProc(JDTORecord inDto) throws DAOException {
		
		int intRtnVal          		= 0;		
		int intRtnVal3          	= 0;		
		
		String szLogMsg         	= null;
		String szMethodName    		= "updToPosFixCoilProc";		
		String szOperationName 		= "크레인 작업지시 변경 (EAI요청 Y5YDL015 자동크레인 작업지시 응답)";
		
		String szStkPos        		= null;
		String szStkColGp      		= null;
		String szStkBedNo      		= null;
		String szStkLyrNo      		= null;
		
		JDTORecord    recPara  		= null;
		JDTORecord    recInPara  	= null;
		JDTORecord    recTemp  		= null;
		JDTORecord    recTemp3  	= null;
		
		JDTORecord    recSet   		= null;
		JDTORecordSet  rsResult= JDTORecordFactory.getInstance().createRecordSet("temp");
		JDTORecordSet  rsGetRequestStat= JDTORecordFactory.getInstance().createRecordSet("temp");
		JDTORecord    inRecord   	= JDTORecordFactory.getInstance().create(); // 
		JDTORecord 	  outRecord 	= JDTORecordFactory.getInstance().create(); // 
		JDTORecord 	  outRecord1 	= JDTORecordFactory.getInstance().create(); // 
		JDTORecord 	  outRecord2 	= JDTORecordFactory.getInstance().create(); // 
		JDTORecord 	  outRecord3 	= JDTORecordFactory.getInstance().create(); // 
		JDTORecord    inRecord3   	= JDTORecordFactory.getInstance().create(); //
		JDTORecord recDelPara   = null;
		
		String szOldStkPos     = null;
		String szOldStkColGp   = null;
		String szOldStkBedNo   = null;
		String szOldStkLyrNo   = null;
		boolean bool = false;
		YdStkBedDao ydStkBedDao = new YdStkBedDao();	
		YdStkLyrDao ydStkLyrDao        = new YdStkLyrDao();
		YdCrnSchDao ydCrnSchDao        = new YdCrnSchDao();
		YdCrnWrkMtlDao  ydCrnWrkMtlDao = new YdCrnWrkMtlDao();
		CoilYdToLocDcsnUtil ydToLocDcsnUtil = new CoilYdToLocDcsnUtil();
		JPlateYdCommDAO ydCommDao = new JPlateYdCommDAO();
		ymCommonDAO dao = ymCommonDAO.getInstance();
		YdEqpDao     ydEqpDao     = new YdEqpDao();
		
		JDTORecordSet    outRecSet  = null;
		JDTORecordSet    outRecSet1  = null;
		JDTORecordSet    outRecSet2  = null;
		JDTORecordSet    outRecSet3  = null;
		
		String szYdWrkProgStat 		= "";		
		String szSendYdWrkProgStat 	= "";
		String szYdGp  				= "";
		String szYdSchCd 			= "";
	
		String szJMS_TC_CD 			= "";
		String szEjbMethod	 		= "";
	    String szYD_EQP_ID 			= "";
	    String szRtnMsg 			= "";
	    String szYdSchId 			= "";
	    EJBConnector ejbConn 		= null;
	    String szYdGpTemp 			= "";
	    String szEqpGp 				= "";     // 변경 설비구분 
	    String szEqpGpBefo 			= ""; // 기존 설비구분 
	    String szYdWbookId 			= ""; //작업예약 ID
	    String szRtnMsg1			= null;
	    String szLayer    			= "";
	    String sTAG_STL_NO 			= "";
	    String sRTN_CD				= "";
	    String sRTN_MSG				= "";
	    String sYD_GP               = "";
        String sDN_YD_STK_LYR_XAXIS 		= "";
        String sDN_YD_STK_BED_XAXIS_TOL 	= "";
        String sDN_YD_STK_LYR_YAXIS 		= "";
        String sDN_YD_STK_BED_YAXIS_TOL 	= "";
        String sQueryId ="";
        // 150619 hun 추가 무인화작업
        String sDN_YD_STK_LYR_ZAXIS 		= "";
        String sDN_YD_STK_BED_ZAXIS_TOL 	= "";
        String szYdSchcd 			= "";
        String szYD_WRK_PROG_REQ_MSG = "";
        String szCrnSchID = "";
        String L2Request = "";
		
		try {
			szLogMsg = "JSP-SESSION [Thread.sleep(500)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			Thread.sleep(500);		//야드 스케쥴 시간 대기.(0.5초 여유)
			
			szLogMsg = "JSP-SESSION [Thread.sleep(500)] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			szLogMsg = "JSP-SESSION [권하위치 변경 (EAI요청 Y5YDL015 권하위치 변경가능 응답시 호출)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			szLogMsg = "JSP-SESSION [권하위치 변경 ] inDto= "+ydUtils.disyRec( inDto);
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			szYD_EQP_ID = yddatautil.setDataDefault(inDto.getField("YD_EQP_ID"), "");
			
			szLogMsg = "[JSP Session] 무인 크레인 체크 szYD_EQP_ID ="+szYD_EQP_ID+ "ydEqpDao.chkAutoCrn(szYD_EQP_ID) ="+ydEqpDao.chkAutoCrn(szYD_EQP_ID);
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			if(ydEqpDao.chkAutoCrn(szYD_EQP_ID) ){
			
			
				// 권하위치 변경일 경우 
				if("5".equals(ydDaoUtils.paraRecChkNull(inDto,"YD_WRK_PROG_STAT"))){
					
					szLogMsg = "JSP-SESSION [권하위치 변경 ] REQ_YN = "+ydDaoUtils.paraRecChkNull(inDto,"REQ_YN");
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
					// 응답전문 N 일때(작업 불가메세지)
					if("N".equals(ydDaoUtils.paraRecChkNull(inDto,"REQ_YN"))){
						
						szLogMsg = "JSP-SESSION [권하위치 변경 (EAI요청 Y5YDL015 권하위치 변경가능 N 응답시 호출)] 시작";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
						
	//					권하위치 변경 N응답시 메세지 update
						szCrnSchID = yddatautil.setDataDefault(inDto.getField("YD_CRN_SCH_ID"), "");
						szYD_WRK_PROG_REQ_MSG = ydDaoUtils.paraRecChkNull(inDto,"YD_WRK_PROG_REQ_MSG");
						
						sQueryId = "com.inisteel.cim.yd.dao.ydeqpdao.YdCrnSchDao.updYdCrnSchProgStatOnlyMsg";
						intRtnVal = dao.updateData(sQueryId,new Object[]{ szYD_WRK_PROG_REQ_MSG, szCrnSchID });
						
						
						if(intRtnVal <= 0){
							szLogMsg = "JSP-SESSION [권하위치 변경가능 N 응답 작업 불가메세지 수신 data가 없습니다]";
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
							
							szLogMsg = "[JSP Session] " + szOperationName + "Y5YDL015 작업 불가메세지 수신 data가 없습니다.";
							outRecord.setField("RTN_CD" 	, "0");	
							outRecord.setField("RTN_MSG" 	, szLogMsg);	
							return outRecord;
						}else{
							szLogMsg = "JSP-SESSION [권하위치 변경가능 N 응답 작업 update 완료]";
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
						
							szLogMsg = "[JSP Session] " + szOperationName + "Y5YDL015 작업 불가메세지 수신";
							outRecord.setField("RTN_CD" 	, "1");	
							outRecord.setField("RTN_MSG" 	, szLogMsg);	
							return outRecord;
						}
					}
					
					
					// 기존 저장했던 Temp Table select
					szLogMsg = "JSP-SESSION [szYdSchId="+inDto.getField("YD_CRN_SCH_ID")+",szYD_EQP_ID="+inDto.getField("YD_EQP_ID")+"] 시작";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
					
					rsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
					sQueryId = "com.inisteel.cim.yd.dao.ydeqpdao.YdCrnSchDao.YdCrnSchLocLog";
					intRtnVal = ydCommDao.select(inDto, rsResult, sQueryId);
					
					
					if(intRtnVal < 0 )
					{
						szLogMsg = "[JSP Session] " + szOperationName + "해당 크레인스케줄 ID  정보: ["+ szYdSchId +" ]조회시 ERROR발생";            		
		        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, szLogMsg);	
						return outRecord;
					}
					
					rsResult.absolute(1);
					inDto = JDTORecordFactory.getInstance().create();
					// Temp Data inDto에 다시 세팅 
					inDto.setRecord(rsResult.getRecord());
					
					
					sTAG_STL_NO		= yddatautil.setDataDefault(inDto.getField("STL_NO_TEMP"), "");
					szYdSchId 		= yddatautil.setDataDefault(inDto.getField("YD_CRN_SCH_ID"), "");
					szStkPos 		= yddatautil.setDataDefault(inDto.getField("YD_DN_WO_LOC_TO"), "");
					szStkLyrNo 		= yddatautil.setDataDefault(inDto.getField("STK_LYR_NO_TEMP"), "");
					sYD_GP 			= szStkPos.substring(0, 1);
					
					szLogMsg = "[JSP Session] szStkLyrNo=" + szStkLyrNo + ", szYdSchId =["+ szYdSchId +" ] sTAG_STL_NO="+sTAG_STL_NO;            		
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
					
					//
					// 1. 기존 To 위치 정보 와 변경 To 위치 정보 
					outRecSet  = JDTORecordFactory.getInstance().createRecordSet("YD");
					
					recPara   = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_CRN_SCH_ID",szYdSchId);		
					
					intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, outRecSet, 0);
					
					if(intRtnVal < 0 )
					{
						szLogMsg = "[JSP Session] " + szOperationName + "해당 크레인스케줄 ID  정보: ["+ szYdSchId +" ]조회시 ERROR발생";            		
		        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, szLogMsg);	
						return outRecord;
					}else if(intRtnVal == 0 ){
						szLogMsg = "[JSP Session] " + szOperationName + "해당 스케줄크레인스케줄 ID  정보: ["+ szYdSchId + "] 가 존재하지않습니다";            		
		        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, szLogMsg);	
						return outRecord;
		        		
					}
					
					
					outRecSet.first();
					
					recTemp   = JDTORecordFactory.getInstance().create();			
					recTemp = outRecSet.getRecord();
					
					szOldStkPos   		= yddatautil.setDataDefault(recTemp.getFieldString("YD_DN_WO_LOC"),"");					
					szOldStkLyrNo 		= yddatautil.setDataDefault(recTemp.getFieldString("YD_DN_WO_LAYER"),"");
					szYdWbookId 		= ydDaoUtils.paraRecChkNull(recTemp, "YD_WBOOK_ID");
					szSendYdWrkProgStat = ydDaoUtils.paraRecChkNull(recTemp, "YD_WRK_PROG_STAT"); //현 스케줄 작업 진행상태(DB)
					szYdSchcd 			= ydDaoUtils.paraRecChkNull(recTemp, "YD_SCH_CD");
					
					szLogMsg = "[JSP Session] " + szOperationName + "현 스케줄 작업 진행상태(DB)[ " + szSendYdWrkProgStat  +" ]"  ;
		    		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		    		
					
					szLogMsg = "[JSP Session] " + szOperationName + "DB 권하지시위치 [ " + szOldStkPos + "-" + szOldStkLyrNo +"]"  ;
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					
					
					szLogMsg = "[JSP Session] " + szOperationName + "입력받은 권하지시위치 [" + szStkPos + "]"  ;
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					
					szStkColGp = szStkPos.substring(0, 6); 
					szStkBedNo = szStkPos.substring(6, 8);
					szEqpGp    = szStkColGp.substring(2,4);
					
					
					inRecord = JDTORecordFactory.getInstance().create();
					inRecord.setField("YD_STK_COL_GP" 	, szStkColGp);	
					inRecord.setField("YD_STK_BED_NO" 	, szStkBedNo);	
					inRecord.setField("YD_STK_LYR_NO" 	, szStkLyrNo);	
		
					
					outRecSet  	= JDTORecordFactory.getInstance().createRecordSet("YD");
					szLogMsg = "[JSP Session] " + szOperationName +  "입력한 적치단 정보조회";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					intRtnVal = ydStkLyrDao.getYdStklyr(inRecord, outRecSet, 0);
					if (intRtnVal == 0){
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, "저장위치 이상");	
						return outRecord;
					} else if ( intRtnVal > 0 )	{
						recTemp   	= JDTORecordFactory.getInstance().create();
						outRecSet.first();
						recTemp 	= outRecSet.getRecord();
						
						String sYD_STK_LYR_MTL_STAT	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_MTL_STAT"); 
						String sYD_STK_LYR_ACT_STAT	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_ACT_STAT"); 
						
						ydUtils.putLog(szSessionName, szMethodName, "sYD_STK_LYR_MTL_STAT:" + sYD_STK_LYR_MTL_STAT, YdConstant.DEBUG);
						ydUtils.putLog(szSessionName, szMethodName, "sYD_STK_LYR_ACT_STAT:" + sYD_STK_LYR_ACT_STAT, YdConstant.DEBUG);
						ydUtils.putLog(szSessionName, szMethodName, "sYD_GP:" + sYD_GP, YdConstant.DEBUG);
						ydUtils.putLog(szSessionName, szMethodName, "szYdSchcd:" + szYdSchcd, YdConstant.DEBUG);
						ydUtils.putLog(szSessionName, szMethodName, "szYdSchcd.substring(2, 4):" + szYdSchcd.substring(2, 4), YdConstant.DEBUG);
						
						// hun 아래 로직은 Hold 메서드에서 처리
						/*
						if(sYD_GP.equals("J")&& szYdSchcd.substring(2, 4).equals("HC")){
							//결로재 적치위치
							if ((sYD_STK_LYR_MTL_STAT.equals("E")) && (sYD_STK_LYR_ACT_STAT.equals("S"))) {
								
							} else {	
								outRecord.setField("RTN_CD" 	, "0");	
								outRecord.setField("RTN_MSG" 	, "제품이 있거나 결로재 적치위치가 아닙니다.");	
								return outRecord;
							}
						}else{
							ydUtils.putLog(szSessionName, szMethodName, "sYD_GP else check:" + sYD_GP, YdConstant.DEBUG);
							//일반재 적치위치
							if ((sYD_STK_LYR_MTL_STAT.equals("E")) && (sYD_STK_LYR_ACT_STAT.equals("E"))) {
							
							} else {	
								outRecord.setField("RTN_CD" 	, "0");	
								outRecord.setField("RTN_MSG" 	, "제품이 있거나 예약상태/금지  입니다.");	
								return outRecord;
							}
						}
						*/
					}
					
					ydUtils.putLog(szSessionName, szMethodName, "sYD_GP check:" + sYD_GP, YdConstant.DEBUG);
					
					if (sYD_GP.equals("H")){
						outRecord1 = ydToLocDcsnUtil.CoilLyrBaseCheck(sTAG_STL_NO, inRecord);
						sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
						sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
						
						if (!("1".equals(sRTN_CD))) {
							outRecord.setField("RTN_CD" 	, "0");	
							outRecord.setField("RTN_MSG" 	, sRTN_MSG);	
							return outRecord;
						}
		
					} else {
//						ydUtils.putLog(szSessionName, szMethodName, "sYD_GP check else:" + sYD_GP, YdConstant.DEBUG);
//						//권하위치 기본 체크 예외동 처리 (A,B,C,L,M,60,70) 결로재 적치 포함 
//						if(szStkColGp.substring(2, 4).equals("80")
//								||szStkColGp.substring(2, 4).equals("70")
//								|| szStkColGp.substring(1, 2).equals("L")
//								|| szStkColGp.substring(1, 2).equals("M")
//								|| szStkColGp.substring(1, 2).equals("C")
//								|| szStkColGp.substring(1, 2).equals("B")
//								|| szStkColGp.substring(1, 2).equals("A")
//								||(sYD_GP.equals("J")&& szYdSchcd.substring(2, 4).equals("HC"))
//							) {  // 가상 bed임
//							
//						} else {
//							outRecord1 = ydToLocDcsnUtil.CoilGdsLyrBaseCheck(sTAG_STL_NO, inRecord);
//							sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
//							sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
//							
//							ydUtils.putLog(szSessionName, szMethodName, "가상Bed else sRTN_CD:" + sRTN_CD, YdConstant.DEBUG);
//							if (!("1".equals(sRTN_CD))) {
//								outRecord.setField("RTN_CD" 	, "0");	
//								outRecord.setField("RTN_MSG" 	, sRTN_MSG);	
//								return outRecord;
//							}
//						}	
						szLogMsg = "[JSP Session] " + szOperationName +  "권하위치변경 시 BASECHECK를 생략 하기로 함(출하팀과 협의)";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					}	
					
					
					//-----------------------------------------------------------------------
					
					//-----------------------------------------------------------------------
					//	권하위치 변경할시에 작업지시 취소전문 발생 (데이터 변경전정보)
					//-----------------------------------------------------------------------
		
					//recTemp : 스케줄 정보
					
					szYdWrkProgStat = ydDaoUtils.paraRecChkNull(recTemp, "YD_WRK_PROG_STAT");
					szYdSchCd       = ydDaoUtils.paraRecChkNull(recTemp, "YD_SCH_CD");
		
					recPara   = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_CRN_SCH_ID", yddatautil.setDataDefault(inDto.getField("YD_CRN_SCH_ID"), ""));
					
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
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, szLogMsg);	
						return outRecord;
					} else if (intRtnVal < 0){
						
						szLogMsg = "[JSP Session] " + szOperationName + "해당 스케줄에 해당되는 재료 조회시 ERROR";
		        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, szLogMsg);	
						return outRecord;
					}
				
					//기존 산적위치 정보가 올바르지 않거나 없을 경우는 기존산적위치를 삭제할 수없으므로 건너띈다.
					szLogMsg = "[szOldStkPos] " + szOldStkPos + "해당 스케줄에 해당되는 재료 조회";
					if( szOldStkPos.length() == 8 && (!szOldStkPos.equals("XX010101")))
					{	
						szOldStkColGp = szOldStkPos.substring(0, 6); 
						szOldStkBedNo = szOldStkPos.substring(6, 8);
						szLogMsg = "[JSP Session] " + szOperationName +  "기존위치정보 :"+ szOldStkColGp  + "-"+ szOldStkBedNo +":" +szOldStkLyrNo;				
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
		        	
						//실제로는 크레인작업재료의 개수만 필요함				
		//					outRecSet.first();
		//					for (int nLoop =0 ; nLoop<outRecSet.size();nLoop++ ){
							 
						inRecord3 = JDTORecordFactory.getInstance().create();
						inRecord3.setField("YD_STK_COL_GP" 	, szOldStkColGp);	
						inRecord3.setField("YD_STK_BED_NO" 	, szOldStkBedNo);	
						inRecord3.setField("YD_STK_LYR_NO" 	, szOldStkLyrNo);	
		
						outRecSet3  	= JDTORecordFactory.getInstance().createRecordSet("YD");
						szLogMsg = "[JSP Session] " + szOperationName +  "권하적치단 정보조회";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						intRtnVal3 = ydStkLyrDao.getYdStklyr(inRecord3, outRecSet3, 0);
						if (intRtnVal3 == 0){
							outRecord.setField("RTN_CD" 	, "0");	
							outRecord.setField("RTN_MSG" 	, "저장위치 이상");	
							return outRecord;
						} else if ( intRtnVal3 > 0 )	{
							recTemp3   	= JDTORecordFactory.getInstance().create();
							outRecSet3.first();
							recTemp3 	= outRecSet3.getRecord();
							
							String sSTL_NO3	= ydDaoUtils.paraRecChkNull(recTemp3, "STL_NO"); 
							
							ydUtils.putLog(szSessionName, szMethodName, "비교-->sSTL_NO3:" + sSTL_NO3+ "sTAG_STL_NO:" + sTAG_STL_NO, YdConstant.DEBUG);
							if (sTAG_STL_NO.equals(sSTL_NO3)) {
						
								// 기존 지시위치 에 쌓여 있는 정보 Clear
								recSet = JDTORecordFactory.getInstance().create();
				                recSet.setField("YD_STK_COL_GP",       szOldStkColGp);    
				                recSet.setField("YD_STK_BED_NO",       szOldStkBedNo);   
				                //적치단 설정
				                recSet.setField("YD_STK_LYR_NO",       szOldStkLyrNo) ;	                
				                recSet.setField("YD_STK_LYR_MTL_STAT", "E");
				                recSet.setField("STL_NO",              "");
				                
				                szLogMsg = "[JSP Session] " + szOperationName +   "기존 지시위치 에 쌓여 있는 정보 Clear ";
			            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			            		
				                intRtnVal = ydStkLyrDao.updYdStklyr(recSet, 0);
				            	if (intRtnVal < 1)
								{
				            		szLogMsg = "[JSP Session] " + szOperationName +   "기존 지시위치 에 쌓여 있는 정보 Clear 실패 [ " + intRtnVal +" ] ";
				            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
									outRecord.setField("RTN_CD" 	, "0");	
									outRecord.setField("RTN_MSG" 	, szLogMsg);	
									return outRecord;
								}
				            	
				            	szLogMsg = "[JSP Session] " + szOperationName +   "기존 지시위치 에 쌓여 있는 정보 Clear 성공 [ " + intRtnVal + " ] ";
				            	ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
							}
						}
					}
					
								
					// 신규위치에 정보를 Setting
					recSet = JDTORecordFactory.getInstance().create();
					recTemp =JDTORecordFactory.getInstance().create();
					
					recSet.setField("YD_STK_COL_GP",       szStkColGp);    
		            recSet.setField("YD_STK_BED_NO",       szStkBedNo);   
		            recSet.setField("YD_STK_LYR_NO",       szStkLyrNo) ;
		            recSet.setField("YD_STK_LYR_MTL_STAT", "D");
		            recSet.setField("STL_NO",              sTAG_STL_NO);
		            
		            
		
		        	szLogMsg = "[JSP Session] " + szOperationName +   "신규위치에 정보를 UPDATE ";
		        	ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		        	
		        	ydUtils.displayRecord(szOperationName, recSet);
		        	
		        	
		            intRtnVal = ydStkLyrDao.updYdStklyr(recSet, 0);
		            
		        	if (intRtnVal < 1)
					{
						//신규위치에 정보를 Setting 실패
		        		szLogMsg = "[JSP Session] " + szOperationName + "신규위치에 정보를 Setting 실패 [ " + intRtnVal + " ]"  ;
		        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, szLogMsg);	
						return outRecord;
					}
		        	
		    		//신규위치에 정보를 Setting 실패
		    		szLogMsg = "[JSP Session] " + szOperationName + "신규위치에 정보를 Setting 성공 [ " + intRtnVal + " ]"  ;
		    		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		            	
					// 권하위치 정보 스케줄 정보에서 변경
					
					//권하지시베드조회
					outRecSet1 = JDTORecordFactory.getInstance().createRecordSet("");
					outRecord2 = JDTORecordFactory.getInstance().create();
					outRecord2.setField("YD_STK_COL_GP", szStkColGp);
					outRecord2.setField("YD_STK_BED_NO", szStkBedNo );
					outRecord2.setField("YD_STK_LYR_NO", szStkLyrNo);    
					/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedLyr*/
					intRtnVal = ydStkBedDao.getYdStkbed(outRecord2, outRecSet1, 304);
					if(intRtnVal <= 0){
						szLogMsg="Y5UpdGradStkLyr getYdStkbed : execution failed";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" , "0");	
						outRecord.setField("RTN_MSG", szLogMsg);	
						return outRecord;
		
					}
					outRecSet1.absolute(1);
					outRecord3 = JDTORecordFactory.getInstance().create();
					outRecord3.setRecord(outRecSet1.getRecord());
					
					sDN_YD_STK_LYR_XAXIS 		= ydDaoUtils.paraRecChkNull(outRecord3, "YD_STK_LYR_XAXIS");
					sDN_YD_STK_BED_XAXIS_TOL 	= ydDaoUtils.paraRecChkNull(outRecord3, "YD_STK_BED_XAXIS_TOL");
					sDN_YD_STK_LYR_YAXIS 		= ydDaoUtils.paraRecChkNull(outRecord3, "YD_STK_LYR_YAXIS");
					sDN_YD_STK_BED_YAXIS_TOL 	= ydDaoUtils.paraRecChkNull(outRecord3, "YD_STK_BED_YAXIS_TOL");
					// 150619 hun 추가 -- 무인화작업
					sDN_YD_STK_LYR_ZAXIS 		= ydDaoUtils.paraRecChkNull(outRecord3, "YD_STK_LYR_ZAXIS");
					sDN_YD_STK_BED_ZAXIS_TOL 	= ydDaoUtils.paraRecChkNull(outRecord3, "YD_STK_BED_ZAXIS_TOL");
					
					
					recPara   = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_CRN_SCH_ID"	, yddatautil.setDataDefault(inDto.getField("YD_CRN_SCH_ID"), ""));
					recPara.setField("YD_DN_WO_LOC"		, szStkColGp+szStkBedNo);	
					recPara.setField("YD_DN_WO_LAYER"	, szStkLyrNo);
					recPara.setField("YD_DN_WO_LOC_XAXIS"		, sDN_YD_STK_LYR_XAXIS);
					recPara.setField("YD_DN_WO_XAXIS_GAP_MAX"	, sDN_YD_STK_BED_XAXIS_TOL);
					recPara.setField("YD_DN_WO_XAXIS_GAP_MIN"	, sDN_YD_STK_BED_XAXIS_TOL);
					recPara.setField("YD_DN_WO_LOC_YAXIS"		, sDN_YD_STK_LYR_YAXIS);
					recPara.setField("YD_DN_WO_YAXIS_GAP_MAX"	, sDN_YD_STK_BED_YAXIS_TOL);
					recPara.setField("YD_DN_WO_YAXIS_GAP_MIN"	, sDN_YD_STK_BED_YAXIS_TOL);
					// 150619 hun 추가 -- 무인화작업
					recPara.setField("YD_DN_WO_LOC_ZAXIS"		, sDN_YD_STK_LYR_ZAXIS);
					recPara.setField("YD_DN_WO_ZAXIS_GAP_MAX"	, sDN_YD_STK_BED_ZAXIS_TOL);
					recPara.setField("YD_DN_WO_ZAXIS_GAP_MIN"	, sDN_YD_STK_BED_ZAXIS_TOL);
					recPara.setField("YD_L2_REQUEST_STAT"	, "");  // hun 크레인무인화 요청 완료 Flag
					recPara.setField("YD_DN_WO_LOC_TO"		, "");  //hun 크레인무인화 백업 삭제
					
					if("".equals(yddatautil.setDataDefault(inDto.getField("YD_UP_WR_LOC"), ""))){
						recPara.setField("YD_WRK_PROG_STAT"	, "1");
					}else{
						recPara.setField("YD_WRK_PROG_STAT"	, "2");
					}
					
					szLogMsg = "[JSP Session] " + szOperationName + " 권하위치 정보 스케줄 정보에서 변경(UPDATE)"  ;
		    		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					ydUtils.displayRecord(szOperationName, recPara);
					
					
					intRtnVal = ydCrnSchDao.updYdCrnsch(recPara, 0);
					
					if (intRtnVal < 1)
					{	
						szLogMsg = "[JSP Session] " + szOperationName +  "권하위치 스케줄 정보 변경 실패 [" +intRtnVal  + " ] " ;
		        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, szLogMsg);	
						return outRecord;
					}
					
					szLogMsg = "[JSP Session] " + szOperationName + " 권하위치 정보 스케줄 정보 UPDATE 성공 "  ;
		    		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
		
					//권하위치 변경후 작업지시정보를  재전송 (Manual 작업지시보내는 방법으로  선택 인경우만 전송한다)
								 
		    		
		    		if( szSendYdWrkProgStat.equals(YdConstant.YD_EQP_STAT_UP_WO) || szSendYdWrkProgStat.equals(YdConstant.YD_EQP_STAT_UP_CMPL)){
						
						szLogMsg =  "[JSP Session] " + szOperationName +  "권하위치 변경후 작업지시정보를  재전송";
		        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		        		
						szLogMsg =  "[JSP Session] " + szOperationName + "   - 스케줄 진행상태 ["  + szSendYdWrkProgStat +" ]";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						
						
						szJMS_TC_CD = "Y5YDL007";
						
				
						szYD_EQP_ID = inDto.getFieldString("YD_EQP_ID");
						
						szLogMsg = "[JSP Session] " + szOperationName +  "- 야드구분[" + szYdGp + "], 크레인 설비[" + szYD_EQP_ID + "]";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						
						recPara.setField("JMS_TC_CD", 			szJMS_TC_CD);
		//					recPara.setField("JMS_TC_CD"            	, szJMS_TC_CD);
						recPara.setField("YD_EQP_ID" , 			szYD_EQP_ID);
						recPara.setField("YD_WRK_PROG_STAT" , 	szSendYdWrkProgStat);
						
						ydUtils.displayRecord(szOperationName, recPara);
						
		//SJH03004								
						//EJB Method Call
							//EJB Method Call
						ejbConn = new EJBConnector("default", "CoilCraneLdHdSeEJB", this);		
						szRtnMsg = (String)ejbConn.trx("procY5CrnWrkOrdReq", new Class[] { JDTORecord.class }, new Object[] { recPara });	
		
		//					ydDelegate.sendMsg(recPara);
						
						
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
		    		if(szEqpGpBefo.equals(YdConstant.YD_EQP_GP_TCAR) 
		    				||szEqpGpBefo.equals(YdConstant.YD_EQP_GP_PALLET) 
		    				||szEqpGpBefo.equals(YdConstant.YD_EQP_GP_TRAILER) 
		    				
		    		){
		    			if(!szEqpGpBefo.equals(szEqpGp)){
		    				
		    				//szYdWbookId - 현 스케줄의 작업예약 ID
		    				//delWBookBefoCarOrTCar
		    				
		    				recPara   = JDTORecordFactory.getInstance().create();
		    				recPara.setField("YD_WBOOK_ID", szYdWbookId);
		    				
		    				yddatautil.delWBookBefoCarOrTCar(recPara);
		    				
		    			}
		    		}
					
							
					outRecord.setField("RTN_CD" 	, "1");	
					return outRecord;
				}else{
	//				권하위치 변경이 아닐경우
					// 작업대기상태 update
					String szYD_WRK_PROG_STAT_S = ydDaoUtils.paraRecChkNull(inDto,"YD_WRK_PROG_STAT");
	//				String szCrnSchID = ydDaoUtils.paraRecChkNull(inDto,"YD_CRN_SCH_ID");
					szCrnSchID = yddatautil.setDataDefault(inDto.getField("YD_CRN_SCH_ID"), "");
					szYD_WRK_PROG_REQ_MSG = ydDaoUtils.paraRecChkNull(inDto,"YD_WRK_PROG_REQ_MSG");
					szYdSchCd = yddatautil.setDataDefault(inDto.getField("YD_SCH_CD"), "");
					String szREQ_YN = yddatautil.setDataDefault(inDto.getField("REQ_YN"), "");
					String szMSG_GP = yddatautil.setDataDefault(inDto.getField("MSG_GP"), "");
					szYD_EQP_ID = yddatautil.setDataDefault(inDto.getField("YD_EQP_ID"), "");
					
					szLogMsg = "[JSP Session] 기타 응답 메세지일경우 param " + szOperationName + " szYD_WRK_PROG_STAT_S ="+ szYD_WRK_PROG_STAT_S +"szCrnSchID ="+szCrnSchID+" szYD_WRK_PROG_REQ_MSG ="+szYD_WRK_PROG_REQ_MSG;
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					if("Y".equals(szREQ_YN) ){
						szLogMsg = "[JSP Session] 기타 응답 메세지일경우 Y 응답 szREQ_YN ="+ szREQ_YN ;
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						
						sQueryId = "com.inisteel.cim.yd.dao.ydeqpdao.YdCrnSchDao.updYdCrnSchProgStatMsg";
						intRtnVal = dao.updateData(sQueryId,new Object[]{szYD_WRK_PROG_STAT_S, szYD_WRK_PROG_REQ_MSG, szCrnSchID });
						
						// 취소요청 Y 응답 
						if("Y".equals(szREQ_YN) && "D".equals(szMSG_GP)  ){
							szLogMsg = "[JSP Session] 취소요청 Y 응답 스케쥴 취소 실행 szREQ_YN ="+ szREQ_YN ;
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
							
////////////////////////////스케쥴 취소 start							
							sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
							sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
							
							recPara   	= JDTORecordFactory.getInstance().create();
							recPara.setField("YD_CRN_SCH_ID"		,szCrnSchID);
							recPara.setField("YD_SCH_CD"			,szYdSchCd);
							recPara.setField("DEL_YN"				,szREQ_YN);
							recPara.setField("MODIFIER"				,"YDSYSTEM");
							recPara.setField("YD_EQP_ID"			,szYD_EQP_ID);
							recPara.setField("YD_L2_RETURN_FLAG"	,"Y");
							recPara.setField("IS_LAST_SELECTED"	,"1");
							
							ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);
							outRecord = (JDTORecord)ejbConn.trx("WrkCancel", new Class[] { JDTORecord.class }, new Object[] { recPara });
							
							sRTN_CD				= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0");
							sRTN_MSG			= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");
////////////////////////////////스케쥴 취소 end
							
							
////////////////////////////////작업 취소 start
							// Crn스케쥴ID 로 Auto 설비 check 
							recPara   	= JDTORecordFactory.getInstance().create();
							recPara.setField("YD_CRN_SCH_ID"		,szCrnSchID);
							
							rsGetRequestStat = JDTORecordFactory.getInstance().createRecordSet("Temp");
							sQueryId = "com.inisteel.cim.yd.dao.ydeqpdao.YdCrnSchDao.YdGetL2RequestByYdCrnSchId";
							intRtnVal = ydCommDao.select(recPara, rsGetRequestStat, sQueryId);
							rsGetRequestStat.absolute(1);
							recTemp = JDTORecordFactory.getInstance().create();
							// Temp Data inDto에 다시 세팅 
							recTemp.setRecord(rsGetRequestStat.getRecord());
				    		
							L2Request = yddatautil.setDataDefault(recTemp.getField("YD_L2_REQUEST_STAT"), "");
							
							if("X".equals(L2Request)){
								szLogMsg = "[JSP Session] 취소요청 Y 응답 스케쥴 취소 실행후 삭제 실행 L2Request ="+ L2Request ;
								ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
									
								ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);			
								outRecord1 = (JDTORecord)ejbConn.trx("delWookBook", new Class[] { JDTORecord.class }, new Object[] { outRecord });
								
								String sCRANE_WR_SND_YN	= StringHelper.evl(outRecord1.getFieldString("CRANE_WR_SND_YN"), "");
								String sYD_EQP_ID		= StringHelper.evl(outRecord1.getFieldString("YD_EQP_ID"), "");
								String sYD_SCH_CD		= StringHelper.evl(outRecord1.getFieldString("YD_SCH_CD"), "");
							
								//스케줄기동 전문 송신
								if ("Y".equals(sCRANE_WR_SND_YN)) {
									
									recDelPara   = JDTORecordFactory.getInstance().create();
									recDelPara.setField("MSG_ID",      "YDYDJ643"        );
									recDelPara.setField("YD_EQP_ID",    sYD_EQP_ID            );					   
									recDelPara.setField("YD_WRK_PROG_STAT", "4" );
									recDelPara.setField("YD_SCH_CD", 	sYD_SCH_CD);  
				
									ejbConn = new EJBConnector("default", this);	
									ejbConn.trx("CoilCraneLdHdSeEJB", "procY5CrnWrkOrdReq", recDelPara);
								}	
							
							}
///////////////////////////////작업 취소 end
							
						}
						
					}else if("N".equals(szREQ_YN)){
						szLogMsg = "[JSP Session] 권하위치 변경이 아닐경우 N 응답 szREQ_YN ="+ szREQ_YN ;
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						
						sQueryId = "com.inisteel.cim.yd.dao.ydeqpdao.YdCrnSchDao.updYdCrnSchProgStatOnlyMsg";
						intRtnVal = dao.updateData(sQueryId,new Object[]{ szYD_WRK_PROG_REQ_MSG, szCrnSchID });						

						if(intRtnVal <= 0){
							szLogMsg = "JSP-SESSION [Y5YDL015 N 응답 작업 불가메세지 수신 data가 없습니다]";
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
							
							szLogMsg = "[JSP Session] " + szOperationName + "Y5YDL015 작업 불가메세지 수신 data가 없습니다.";
							outRecord.setField("RTN_CD" 	, "0");	
							outRecord.setField("RTN_MSG" 	, szLogMsg);	
							return outRecord;
						}else{
							szLogMsg = "JSP-SESSION [Y5YDL015 변경가능 N 응답 작업 update 완료]";
							ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
						
							szLogMsg = "[JSP Session] " + szOperationName + "Y5YDL015 작업 불가메세지 수신";
							outRecord.setField("RTN_CD" 	, "1");	
							outRecord.setField("RTN_MSG" 	, szLogMsg);	
							return outRecord;
						}
						
					}
					outRecord.setField("RTN_CD" 	, "1");	
					return outRecord;
					
				} // 권하위치 변경 아닐경우 else
			}else{ // auto 크레인 아닐경우
				outRecord.setField("RTN_CD" 	, "0");	
				return outRecord;
			}
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
			
		}
	}	// end of updToPosFix
	
	
	
	/**
	 * 권하위치 변경 (크레인작업관리 화면)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecord updToPosFixCoil(JDTORecord[] inDto) throws DAOException {
		
		int intRtnVal          		= 0;		
		int intRtnVal3          	= 0;		
		
		String szLogMsg         	= null;
		String szMethodName    		= "updToPosFixCoil";		
		String szOperationName 		= "권하위치 변경 (크레인작업관리 화면)";
		
		String szStkPos        		= null;
		String szStkColGp      		= null; 
		String szStkBedNo      		= null;
		String szStkLyrNo      		= null;
		
		JDTORecord    recPara  		= null;
		JDTORecord    recInPara  	= null;
		JDTORecord    recTemp  		= null;
		JDTORecord    recTemp3  	= null;
		JDTORecord    recEqpInfo  	= null;
		
		JDTORecord    recSet   		= null;
		JDTORecord    inRecord   	= JDTORecordFactory.getInstance().create(); // 
		JDTORecord 	  outRecord 	= JDTORecordFactory.getInstance().create(); // 
		JDTORecord 	  outRecord1 	= JDTORecordFactory.getInstance().create(); // 
		JDTORecord 	  outRecord2 	= JDTORecordFactory.getInstance().create(); // 
		JDTORecord 	  outRecord3 	= JDTORecordFactory.getInstance().create(); // 
		JDTORecord    inRecord3   	= JDTORecordFactory.getInstance().create(); // 
		
		String szOldStkPos     = null;
		String szOldStkColGp   = null;
		String szOldStkBedNo   = null;
		String szOldStkLyrNo   = null;
		boolean bool = false;
		YdStkBedDao ydStkBedDao = new YdStkBedDao();	
		YdStkLyrDao ydStkLyrDao        = new YdStkLyrDao();
		YdCrnSchDao ydCrnSchDao        = new YdCrnSchDao();
		YdCrnWrkMtlDao  ydCrnWrkMtlDao = new YdCrnWrkMtlDao();
		YdEqpDao ydEqpDao        = new YdEqpDao();
		CoilYdToLocDcsnUtil ydToLocDcsnUtil = new CoilYdToLocDcsnUtil();
		
		JDTORecordSet    outRecSet  = null;
		JDTORecordSet    outRecSet1  = null;
		JDTORecordSet    outRecSet2  = null;
		JDTORecordSet    outRecSet3  = null;
		JDTORecordSet    rsEqpInfo  = null;
		
		String szYdWrkProgStat 		= "";		
		String szSendYdWrkProgStat 	= "";
		String szYdGp  				= "";
		String szYdSchCd 			= "";
	
		String szJMS_TC_CD 			= "";
		String szEjbMethod	 		= "";
	    String szYD_EQP_ID 			= "";
	    String szRtnMsg 			= "";
	    String szYdSchId 			= "";
	    EJBConnector ejbConn 		= null;
	    String szYdGpTemp 			= "";
	    String szEqpGp 				= "";     // 변경 설비구분 
	    String szEqpGpBefo 			= ""; // 기존 설비구분 
	    String szYdWbookId 			= ""; //작업예약 ID
	    String szRtnMsg1			= null;
	    String szLayer    			= "";
	    String sTAG_STL_NO 			= "";
	    String sRTN_CD				= "";
	    String sRTN_MSG				= "";
	    String sYD_GP               = "";
        String sDN_YD_STK_LYR_XAXIS 		= "";
        String sDN_YD_STK_BED_XAXIS_TOL 	= "";
        String sDN_YD_STK_LYR_YAXIS 		= "";
        String sDN_YD_STK_BED_YAXIS_TOL 	= "";
        // 150619 hun 추가 무인화작업
        String sDN_YD_STK_LYR_ZAXIS 		= "";
        String sDN_YD_STK_BED_ZAXIS_TOL 	= "";
        String szYdSchcd 			= "";
        String szYdUPWorkMode2		= "";
        String szYdDNWorkMode2		= "";
//      151002 hun 설비상태 체크하여 Auto일경우 일시정지 상태에서만 가능하게 수정
        String szEqpId			= "";
        String szydEqpStat		= "";
        String szEqpAutoCrnMode	= "";
        String szEqpAutoCrnYN	= "";
        String szRtnValue		= "";
        int intGp = 0;
		
		try {
			
			
			szLogMsg = "JSP-SESSION [권하위치 변경 (크레인작업관리 화면)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			
			for(int x=0;x<inDto.length;x++){
				
				sTAG_STL_NO	= yddatautil.setDataDefault(inDto[x].getField("STL_NO"), "");
				szYdSchId 	= yddatautil.setDataDefault(inDto[x].getField("YD_CRN_SCH_ID"), "");
				szStkPos 	= yddatautil.setDataDefault(inDto[x].getField("YD_DN_WO_LOC"), "");
				szStkLyrNo 	= yddatautil.setDataDefault(inDto[x].getField("YD_DN_WO_LAYER"), "");
				sYD_GP 		= szStkPos.substring(0, 1);
				
				
				//
				// 1. 기존 To 위치 정보 와 변경 To 위치 정보 
				outRecSet  = JDTORecordFactory.getInstance().createRecordSet("YD");
				
				recPara   = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID",szYdSchId);		
				
				intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, outRecSet, 0);
				
				if(intRtnVal < 0 )
				{
					szLogMsg = "[JSP Session] " + szOperationName + "해당 크레인스케줄 ID  정보: ["+ szYdSchId +" ]조회시 ERROR발생";            		
            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szLogMsg);	
					return outRecord;
				}else if(intRtnVal == 0 ){
					szLogMsg = "[JSP Session] " + szOperationName + "해당 스케줄크레인스케줄 ID  정보: ["+ szYdSchId + "] 가 존재하지않습니다";            		
            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szLogMsg);	
					return outRecord;
	        		
				}
				
				
				outRecSet.first();
				
				recTemp   = JDTORecordFactory.getInstance().create();			
				recTemp = outRecSet.getRecord();
				
				szOldStkPos   		= yddatautil.setDataDefault(recTemp.getFieldString("YD_DN_WO_LOC"),"");					
				szOldStkLyrNo 		= yddatautil.setDataDefault(recTemp.getFieldString("YD_DN_WO_LAYER"),"");
				szYdWbookId 		= ydDaoUtils.paraRecChkNull(recTemp, "YD_WBOOK_ID");
				szSendYdWrkProgStat = ydDaoUtils.paraRecChkNull(recTemp, "YD_WRK_PROG_STAT"); //현 스케줄 작업 진행상태(DB)
				szYdSchcd 			= ydDaoUtils.paraRecChkNull(recTemp, "YD_SCH_CD");
				szYdUPWorkMode2	= ydDaoUtils.paraRecChkNull(recTemp, "YD_UP_WRK_MODE2");
				szYdDNWorkMode2	= ydDaoUtils.paraRecChkNull(recTemp, "YD_DN_WRK_MODE2");
				
				
				szLogMsg = "[JSP Session] " + szOperationName + "현 스케줄 작업 진행상태(DB)[ " + szSendYdWrkProgStat  +" ]"  ;
        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
        		
				szLogMsg = "[JSP Session] " + szOperationName + "DB 권하지시위치 [ " + szOldStkPos + "-" + szOldStkLyrNo +"]"  ;
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				szLogMsg = "[JSP Session] " + szOperationName + "입력받은 권하지시위치 [" + szStkPos + "]"  ;
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
//		      	151002 hun 설비상태 체크하여 Auto일경우 일시정지 상태에서만 가능하게 수정
				szEqpId = inDto[x].getFieldString("YD_EQP_ID");
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

					if (szSendYdWrkProgStat.equals(YdConstant.YD_EQP_STAT_DN_CMPL)) {
						szRtnValue = "크레인 [" + szEqpId + "]가 권하완료 상태에서는 변경 할 수 없습니다.";
						outRecord.setField("RTN_CD" , "0");
						outRecord.setField("RTN_MSG" , szRtnValue);
						return outRecord;
						// 150918 hun Auto크레인시에 일시정지(4) 상태만 가능
					} else if (("A".equals(szEqpAutoCrnYN) || "R".equals(szEqpAutoCrnYN))) {

						if (!"4".equals(szEqpAutoCrnMode) && !"W".equals(szSendYdWrkProgStat) && !"B".equals(szydEqpStat)) {
							szRtnValue = "무인크레인 [" + szEqpId + "]이 일시정지이거나, 고장 상태가 아니면 변경 할 수 없습니다.";
							outRecord.setField("RTN_CD" , "0");
							outRecord.setField("RTN_MSG" , szRtnValue);
							return outRecord;
						}
					}
				}
			   //*************************************************************************
				
				//-----------------------------------------------------------------------------------------------//
				// 150710 hun 크레인상태(YD_EQP_WRK_MODE2)=A 일때 자동메서드 분리 
				szYD_EQP_ID = inDto[x].getFieldString("YD_EQP_ID");
				szLogMsg = "[JSP Session] " + szOperationName + "입력받은 설비모드 [" + szYD_EQP_ID + "]"+ydEqpDao.chkAutoCrn(szYD_EQP_ID) +"szSendYdWrkProgStat="+szSendYdWrkProgStat ;
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				
				if (ydEqpDao.chkAutoCrn(szYD_EQP_ID) && !"W".equals(szSendYdWrkProgStat) ){
					szLogMsg = "[JSP Session] " + szSendYdWrkProgStat + "자동크레인메서드 호출로 분기"  ;
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					
					//outRecord = this.updToPosFixCoilHold(inDto[x]);
					ejbConn = new EJBConnector("default", this);				
					outRecord = (JDTORecord)ejbConn.trx("CoilJspSeEJB", "updToPosFixCoilHold", inDto[x]);
					
					sRTN_CD	= StringHelper.evl(outRecord.getFieldString("RTN_CD"), "0"); 
					sRTN_MSG	= StringHelper.evl(outRecord.getFieldString("RTN_MSG"), "");	
					
					ydUtils.putLog(szSessionName, "", "sRTN_CD.--> " +sRTN_CD, YdConstant.DEBUG);
					
					return outRecord;
					
				//-----------------------------------------------------------------------------------------------//
				}else{
					
					if ("".equals(szStkPos)){		
						szLogMsg = "[JSP Session] " + szOperationName + "변경 권하지시위치 정보가 없습니다."  ;
	            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, szLogMsg);	
						return outRecord;
					}
					
					
					if(szOldStkPos.equals(szStkPos)){
						szLogMsg = "[JSP Session] " + szOperationName + "입력된 권하위치가 기존 권하위치와 같습니다."  ;
	            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, szLogMsg);	
						return outRecord;
						
					}
						
					
					if(szStkPos.length() >=8)
					{
						szStkColGp = szStkPos.substring(0, 6); 
						szStkBedNo = szStkPos.substring(6, 8);
						szEqpGp    = szStkColGp.substring(2,4);
					}else{
						szLogMsg = "[JSP Session] " + szOperationName + "변경 권하지시위치 정보가 맞지 않습니다"  ;
	            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, szLogMsg);	
						return outRecord;
					}
					//-----------------------------------------------------------------------
					
					inRecord = JDTORecordFactory.getInstance().create();
					inRecord.setField("YD_STK_COL_GP" 	, szStkColGp);	
					inRecord.setField("YD_STK_BED_NO" 	, szStkBedNo);	
					inRecord.setField("YD_STK_LYR_NO" 	, szStkLyrNo);	
	
					
					outRecSet  	= JDTORecordFactory.getInstance().createRecordSet("YD");
					szLogMsg = "[JSP Session] " + szOperationName +  "입력한 적치단 정보조회";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					intRtnVal = ydStkLyrDao.getYdStklyr(inRecord, outRecSet, 0);
					if (intRtnVal == 0){
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, "저장위치 이상");	
						return outRecord;
					} else if ( intRtnVal > 0 )	{
						recTemp   	= JDTORecordFactory.getInstance().create();
						outRecSet.first();
						recTemp 	= outRecSet.getRecord();
						
						String sYD_STK_LYR_MTL_STAT	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_MTL_STAT"); 
						String sYD_STK_LYR_ACT_STAT	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_ACT_STAT"); 
						
						ydUtils.putLog(szSessionName, szMethodName, "sYD_STK_LYR_MTL_STAT:" + sYD_STK_LYR_MTL_STAT, YdConstant.DEBUG);
						ydUtils.putLog(szSessionName, szMethodName, "sYD_STK_LYR_ACT_STAT:" + sYD_STK_LYR_ACT_STAT, YdConstant.DEBUG);
						
						
						if(sYD_GP.equals("J")&& szYdSchcd.substring(2, 4).equals("HC")){
							//결로재 적치위치
							if ((sYD_STK_LYR_MTL_STAT.equals("E")) && (sYD_STK_LYR_ACT_STAT.equals("S"))) {
								
							} else {	
								outRecord.setField("RTN_CD" 	, "0");	
								outRecord.setField("RTN_MSG" 	, "제품이 있거나 결로재 적치위치가 아닙니다.");	
								return outRecord;
							}
						}else{
							//일반재 적치위치
							if ((sYD_STK_LYR_MTL_STAT.equals("E")) && (sYD_STK_LYR_ACT_STAT.equals("E"))) {
							
							} else {	
								outRecord.setField("RTN_CD" 	, "0");	
								outRecord.setField("RTN_MSG" 	, "제품이 있거나 예약상태/금지  입니다.");	
								return outRecord;
							}
						}
						
					}
					if (sYD_GP.equals("H")){
						outRecord1 = ydToLocDcsnUtil.CoilLyrBaseCheck(sTAG_STL_NO, inRecord);
						sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
						sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
						
						if (!("1".equals(sRTN_CD))) {
							outRecord.setField("RTN_CD" 	, "0");	
							outRecord.setField("RTN_MSG" 	, sRTN_MSG);	
							return outRecord;
						}
		
					} else {				
						//권하위치 기본 체크 예외동 처리 (A,B,C,L,M,80,70) 결로재 적치 포함 
//						if(szStkColGp.substring(2, 4).equals("80")
//								||szStkColGp.substring(2, 4).equals("70")
//								|| szStkColGp.substring(1, 2).equals("L")
//								|| szStkColGp.substring(1, 2).equals("M")
//								|| szStkColGp.substring(1, 2).equals("C")
//								|| szStkColGp.substring(1, 2).equals("B")
//								|| szStkColGp.substring(1, 2).equals("A")
//								||(sYD_GP.equals("J")&& szYdSchcd.substring(2, 4).equals("HC"))
//							) {  // 가상 bed임
//							
//						} else {
//							outRecord1 = ydToLocDcsnUtil.CoilGdsLyrBaseCheck(sTAG_STL_NO, inRecord);
//							sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
//							sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
//							
//							if (!("1".equals(sRTN_CD))) {
//								outRecord.setField("RTN_CD" 	, "0");	
//								outRecord.setField("RTN_MSG" 	, sRTN_MSG);	
//								return outRecord;
//							}
//						}	
						
						szLogMsg = "[JSP Session] " + szOperationName +  "권하위치변경 시 BASECHECK를 생략 하기로 함(출하팀과 협의)";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					}	
					
					
					//-----------------------------------------------------------------------
					
					//-----------------------------------------------------------------------
					//	권하위치 변경할시에 작업지시 취소전문 발생 (데이터 변경전정보)
					//-----------------------------------------------------------------------
	
					//recTemp : 스케줄 정보
					
					szYdWrkProgStat = ydDaoUtils.paraRecChkNull(recTemp, "YD_WRK_PROG_STAT");
					szYdSchCd       = ydDaoUtils.paraRecChkNull(recTemp, "YD_SCH_CD");
	
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
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, szLogMsg);	
						return outRecord;
					} else if (intRtnVal < 0){
						
						szLogMsg = "[JSP Session] " + szOperationName + "해당 스케줄에 해당되는 재료 조회시 ERROR";
	            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, szLogMsg);	
						return outRecord;
					}
				
					//기존 산적위치 정보가 올바르지 않거나 없을 경우는 기존산적위치를 삭제할 수없으므로 건너띈다.
					szLogMsg = "[szOldStkPos] " + szOldStkPos + "해당 스케줄에 해당되는 재료 조회";
					if( szOldStkPos.length() == 8 && (!szOldStkPos.equals("XX010101")))
					{	
						szOldStkColGp = szOldStkPos.substring(0, 6); 
						szOldStkBedNo = szOldStkPos.substring(6, 8);
						szLogMsg = "[JSP Session] " + szOperationName +  "기존위치정보 :"+ szOldStkColGp  + "-"+ szOldStkBedNo +":" +szOldStkLyrNo;				
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
	            	
	 					//실제로는 크레인작업재료의 개수만 필요함				
	//					outRecSet.first();
	//					for (int nLoop =0 ; nLoop<outRecSet.size();nLoop++ ){
							 
						inRecord3 = JDTORecordFactory.getInstance().create();
						inRecord3.setField("YD_STK_COL_GP" 	, szOldStkColGp);	
						inRecord3.setField("YD_STK_BED_NO" 	, szOldStkBedNo);	
						inRecord3.setField("YD_STK_LYR_NO" 	, szOldStkLyrNo);	
	
						outRecSet3  	= JDTORecordFactory.getInstance().createRecordSet("YD");
						szLogMsg = "[JSP Session] " + szOperationName +  "권하적치단 정보조회";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						intRtnVal3 = ydStkLyrDao.getYdStklyr(inRecord3, outRecSet3, 0);
						if (intRtnVal3 == 0){
							outRecord.setField("RTN_CD" 	, "0");	
							outRecord.setField("RTN_MSG" 	, "저장위치 이상");	
							return outRecord;
						} else if ( intRtnVal3 > 0 )	{
							recTemp3   	= JDTORecordFactory.getInstance().create();
							outRecSet3.first();
							recTemp3 	= outRecSet3.getRecord();
							
							String sSTL_NO3	= ydDaoUtils.paraRecChkNull(recTemp3, "STL_NO"); 
							
							ydUtils.putLog(szSessionName, szMethodName, "비교-->sSTL_NO3:" + sSTL_NO3+ "sTAG_STL_NO:" + sTAG_STL_NO, YdConstant.DEBUG);
							if (sTAG_STL_NO.equals(sSTL_NO3)) {
						
								// 기존 지시위치 에 쌓여 있는 정보 Clear
								recSet = JDTORecordFactory.getInstance().create();
				                recSet.setField("YD_STK_COL_GP",       szOldStkColGp);    
				                recSet.setField("YD_STK_BED_NO",       szOldStkBedNo);   
				                //적치단 설정
				                recSet.setField("YD_STK_LYR_NO",       szOldStkLyrNo) ;	                
				                recSet.setField("YD_STK_LYR_MTL_STAT", "E");
				                recSet.setField("STL_NO",              "");
				                
				                szLogMsg = "[JSP Session] " + szOperationName +   "기존 지시위치 에 쌓여 있는 정보 Clear ";
			            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
			            		
				                intRtnVal = ydStkLyrDao.updYdStklyr(recSet, 0);
				            	if (intRtnVal < 1)
								{
				            		szLogMsg = "[JSP Session] " + szOperationName +   "기존 지시위치 에 쌓여 있는 정보 Clear 실패 [ " + intRtnVal +" ] ";
				            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
									outRecord.setField("RTN_CD" 	, "0");	
									outRecord.setField("RTN_MSG" 	, szLogMsg);	
									return outRecord;
								}
				            	
				            	szLogMsg = "[JSP Session] " + szOperationName +   "기존 지시위치 에 쌓여 있는 정보 Clear 성공 [ " + intRtnVal + " ] ";
				            	ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
							}
						}
					}
					
								
					// 신규위치에 정보를 Setting
					recSet = JDTORecordFactory.getInstance().create();
					recTemp =JDTORecordFactory.getInstance().create();
					
					recSet.setField("YD_STK_COL_GP",       szStkColGp);    
		            recSet.setField("YD_STK_BED_NO",       szStkBedNo);   
		            recSet.setField("YD_STK_LYR_NO",       szStkLyrNo) ;
		            recSet.setField("YD_STK_LYR_MTL_STAT", "D");
	                recSet.setField("STL_NO",              sTAG_STL_NO);
	                
	                
	
	            	szLogMsg = "[JSP Session] " + szOperationName +   "신규위치에 정보를 UPDATE ";
	            	ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
	            	
	            	ydUtils.displayRecord(szOperationName, recSet);
	            	
	            	
	                intRtnVal = ydStkLyrDao.updYdStklyr(recSet, 0);
	                
	            	if (intRtnVal < 1)
					{
						//신규위치에 정보를 Setting 실패
	            		szLogMsg = "[JSP Session] " + szOperationName + "신규위치에 정보를 Setting 실패 [ " + intRtnVal + " ]"  ;
	            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, szLogMsg);	
						return outRecord;
					}
	            	
	        		//신규위치에 정보를 Setting 실패
	        		szLogMsg = "[JSP Session] " + szOperationName + "신규위치에 정보를 Setting 성공 [ " + intRtnVal + " ]"  ;
	        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		            	
					// 권하위치 정보 스케줄 정보에서 변경
					
					//권하지시베드조회
					outRecSet1 = JDTORecordFactory.getInstance().createRecordSet("");
					outRecord2 = JDTORecordFactory.getInstance().create();
					outRecord2.setField("YD_STK_COL_GP", szStkColGp);
					outRecord2.setField("YD_STK_BED_NO", szStkBedNo );
					outRecord2.setField("YD_STK_LYR_NO", szStkLyrNo);    
					/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedLyr*/
					intRtnVal = ydStkBedDao.getYdStkbed(outRecord2, outRecSet1, 304);
					if(intRtnVal <= 0){
						szLogMsg="Y5UpdGradStkLyr getYdStkbed : execution failed";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" , "0");	
						outRecord.setField("RTN_MSG", szLogMsg);	
						return outRecord;
		
					}
					outRecSet1.absolute(1);
					outRecord3 = JDTORecordFactory.getInstance().create();
					outRecord3.setRecord(outRecSet1.getRecord());
					
					sDN_YD_STK_LYR_XAXIS 		= ydDaoUtils.paraRecChkNull(outRecord3, "YD_STK_LYR_XAXIS");
					sDN_YD_STK_BED_XAXIS_TOL 	= ydDaoUtils.paraRecChkNull(outRecord3, "YD_STK_BED_XAXIS_TOL");
					sDN_YD_STK_LYR_YAXIS 		= ydDaoUtils.paraRecChkNull(outRecord3, "YD_STK_LYR_YAXIS");
					sDN_YD_STK_BED_YAXIS_TOL 	= ydDaoUtils.paraRecChkNull(outRecord3, "YD_STK_BED_YAXIS_TOL");
					// 150619 hun 추가 -- 무인화작업
					sDN_YD_STK_LYR_ZAXIS 		= ydDaoUtils.paraRecChkNull(outRecord3, "YD_STK_LYR_ZAXIS");
					sDN_YD_STK_BED_ZAXIS_TOL 	= ydDaoUtils.paraRecChkNull(outRecord3, "YD_STK_BED_ZAXIS_TOL");
					
					
					recPara   = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_CRN_SCH_ID"	, yddatautil.setDataDefault(inDto[x].getField("YD_CRN_SCH_ID"), ""));
					recPara.setField("YD_DN_WO_LOC"		, szStkColGp+szStkBedNo);				
					recPara.setField("YD_DN_WO_LAYER"	, szStkLyrNo);
					recPara.setField("YD_DN_WO_LOC_XAXIS"		, sDN_YD_STK_LYR_XAXIS);
					recPara.setField("YD_DN_WO_XAXIS_GAP_MAX"	, sDN_YD_STK_BED_XAXIS_TOL);
					recPara.setField("YD_DN_WO_XAXIS_GAP_MIN"	, sDN_YD_STK_BED_XAXIS_TOL);
					recPara.setField("YD_DN_WO_LOC_YAXIS"		, sDN_YD_STK_LYR_YAXIS);
					recPara.setField("YD_DN_WO_YAXIS_GAP_MAX"	, sDN_YD_STK_BED_YAXIS_TOL);
					recPara.setField("YD_DN_WO_YAXIS_GAP_MIN"	, sDN_YD_STK_BED_YAXIS_TOL);
					// 150619 hun 추가 -- 무인화작업
					recPara.setField("YD_DN_WO_LOC_ZAXIS"		, sDN_YD_STK_LYR_ZAXIS);
					recPara.setField("YD_DN_WO_ZAXIS_GAP_MAX"	, sDN_YD_STK_BED_ZAXIS_TOL);
					recPara.setField("YD_DN_WO_ZAXIS_GAP_MIN"	, sDN_YD_STK_BED_ZAXIS_TOL);
					
					szLogMsg = "[JSP Session] " + szOperationName + " 권하위치 정보 스케줄 정보에서 변경(UPDATE)"  ;
	        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					ydUtils.displayRecord(szOperationName, recPara);
					
					
					intRtnVal = ydCrnSchDao.updYdCrnsch(recPara, 0);
					
					if (intRtnVal < 1)
					{	
						szLogMsg = "[JSP Session] " + szOperationName +  "권하위치 스케줄 정보 변경 실패 [" +intRtnVal  + " ] " ;
	            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, szLogMsg);	
						return outRecord;
					}
					
					szLogMsg = "[JSP Session] " + szOperationName + " 권하위치 정보 스케줄 정보 UPDATE 성공 "  ;
	        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
	
					//권하위치 변경후 작업지시정보를  재전송 (Manual 작업지시보내는 방법으로  선택 인경우만 전송한다)
								 
	        		
	        		if( szSendYdWrkProgStat.equals(YdConstant.YD_EQP_STAT_UP_WO) || szSendYdWrkProgStat.equals(YdConstant.YD_EQP_STAT_UP_CMPL)){
						
						szLogMsg =  "[JSP Session] " + szOperationName +  "권하위치 변경후 작업지시정보를  재전송";
		        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		        		
						szLogMsg =  "[JSP Session] " + szOperationName + "   - 스케줄 진행상태 ["  + szSendYdWrkProgStat +" ]";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						
						
						szJMS_TC_CD = "Y5YDL007";
						
				
						szYD_EQP_ID = inDto[x].getFieldString("YD_EQP_ID");
						
						szLogMsg = "[JSP Session] " + szOperationName +  "- 야드구분[" + szYdGp + "], 크레인 설비[" + szYD_EQP_ID + "]";
						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
						
						recPara.setField("JMS_TC_CD", 			szJMS_TC_CD);
	//					recPara.setField("JMS_TC_CD"            	, szJMS_TC_CD);
						recPara.setField("YD_EQP_ID" , 			szYD_EQP_ID);
						recPara.setField("YD_WRK_PROG_STAT" , 	szSendYdWrkProgStat);
						
						ydUtils.displayRecord(szOperationName, recPara);
						
	//SJH03004								
						//EJB Method Call
							//EJB Method Call
						ejbConn = new EJBConnector("default", "CoilCraneLdHdSeEJB", this);		
						szRtnMsg = (String)ejbConn.trx("procY5CrnWrkOrdReq", new Class[] { JDTORecord.class }, new Object[] { recPara });	
	
	//					ydDelegate.sendMsg(recPara);
						
						
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
	        		if(szEqpGpBefo.equals(YdConstant.YD_EQP_GP_TCAR) 
	        				||szEqpGpBefo.equals(YdConstant.YD_EQP_GP_PALLET) 
	        				||szEqpGpBefo.equals(YdConstant.YD_EQP_GP_TRAILER) 
	        				
	        		){
	        			if(!szEqpGpBefo.equals(szEqpGp)){
	        				
	        				//szYdWbookId - 현 스케줄의 작업예약 ID
	        				//delWBookBefoCarOrTCar
	        				
	        				recPara   = JDTORecordFactory.getInstance().create();
	        				recPara.setField("YD_WBOOK_ID", szYdWbookId);
	        				
	        				yddatautil.delWBookBefoCarOrTCar(recPara);
	        				
	        			}
	        		}
				}
			}		
					
			outRecord.setField("RTN_CD" 	, "1");	
			return outRecord;

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
			
		} 		
	}	// end of updToPosFix
	
	
	
	/**
	 * 권하위치 변경 (크레인작업관리 화면)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecord updToPosFixCoil_BACK(JDTORecord[] inDto) throws DAOException {
		
		int intRtnVal          = 0;				
		String szLogMsg           = null;
		String szMethodName    = "updToPosFixCoil_BACK";		
		String szOperationName = "권하위치 변경 (크레인작업관리 화면BACK)";
		
		String szStkPos        = null;
		String szStkColGp      = null;
		String szStkBedNo      = null;
		String szStkLyrNo      = null;
		
		JDTORecord    recPara  = null;
		JDTORecord    recInPara  = null;
		JDTORecord    recTemp  = null;
		JDTORecord    recSet   = null;
		JDTORecord    inRecord   = JDTORecordFactory.getInstance().create(); // 
		JDTORecord 	  outRecord 		= JDTORecordFactory.getInstance().create(); // 
		JDTORecord 	  outRecord1 		= JDTORecordFactory.getInstance().create(); // 
		JDTORecord 	  outRecord2 		= JDTORecordFactory.getInstance().create(); // 
		JDTORecord 	  outRecord3 		= JDTORecordFactory.getInstance().create(); // 
		
		String szOldStkPos     = null;
		String szOldStkColGp   = null;
		String szOldStkBedNo   = null;
		String szOldStkLyrNo   = null;
		boolean bool = false;
		YdStkBedDao ydStkBedDao = new YdStkBedDao();	
		YdStkLyrDao ydStkLyrDao        = new YdStkLyrDao();
		YdCrnSchDao ydCrnSchDao        = new YdCrnSchDao();
		YdCrnWrkMtlDao  ydCrnWrkMtlDao = new YdCrnWrkMtlDao();
		CoilYdToLocDcsnUtil ydToLocDcsnUtil = new CoilYdToLocDcsnUtil();
		
		JDTORecordSet    outRecSet  = null;
		JDTORecordSet    outRecSet1  = null;
		
		
		String szYdWrkProgStat 		= "";		
		String szSendYdWrkProgStat 	= "";
		String szYdGp  				= "";
		String szYdSchCd 			= "";
	
		String szJMS_TC_CD 			= "";
		String szEjbMethod	 		= "";
	    String szYD_EQP_ID 			= "";
	    String szRtnMsg 			= "";
	    String szYdSchId 			= "";
	    EJBConnector ejbConn 		= null;
	    String szYdGpTemp 			= "";
	    String szEqpGp 				= "";     // 변경 설비구분 
	    String szEqpGpBefo 			= ""; // 기존 설비구분 
	    String szYdWbookId 			= ""; //작업예약 ID
	    String szRtnMsg1			= null;
	    String szLayer    			= "";
	    String sTAG_STL_NO 			= "";
	    String sRTN_CD				= "";
	    String sRTN_MSG				= "";
	    String sYD_GP               = "";
        String sDN_YD_STK_LYR_XAXIS 		= "";
        String sDN_YD_STK_BED_XAXIS_TOL 	= "";
        String sDN_YD_STK_LYR_YAXIS 		= "";
        String sDN_YD_STK_BED_YAXIS_TOL 	= "";
        // 150619 hun 추가 무인화작업
        String sDN_YD_STK_LYR_ZAXIS 		= "";
        String sDN_YD_STK_BED_ZAXIS_TOL 	= "";
        
        String szYdSchcd 			= "";
        
		
		try {
			
			
			szLogMsg = "JSP-SESSION [권하위치 변경 (크레인작업관리 화면)] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			
			
			for(int x=0;x<inDto.length;x++){
				
				sTAG_STL_NO	= yddatautil.setDataDefault(inDto[x].getField("STL_NO"), "");
				szYdSchId 	= yddatautil.setDataDefault(inDto[x].getField("YD_CRN_SCH_ID"), "");
				szStkPos 	= yddatautil.setDataDefault(inDto[x].getField("YD_DN_WO_LOC"), "");
				szStkLyrNo 	= yddatautil.setDataDefault(inDto[x].getField("YD_DN_WO_LAYER"), "");
				sYD_GP 		= szStkPos.substring(0, 1);
				
				
				//
				// 1. 기존 To 위치 정보 와 변경 To 위치 정보 
				outRecSet  = JDTORecordFactory.getInstance().createRecordSet("YD");
				
				recPara   = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID",szYdSchId);		
				
				intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, outRecSet, 0);
				
				if(intRtnVal < 0 )
				{
					szLogMsg = "[JSP Session] " + szOperationName + "해당 크레인스케줄 ID  정보: ["+ szYdSchId +" ]조회시 ERROR발생";            		
            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szLogMsg);	
					return outRecord;
				}else if(intRtnVal == 0 ){
					szLogMsg = "[JSP Session] " + szOperationName + "해당 스케줄크레인스케줄 ID  정보: ["+ szYdSchId + "] 가 존재하지않습니다";            		
            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szLogMsg);	
					return outRecord;
	        		
				}
				
				
				outRecSet.first();
				
				recTemp   = JDTORecordFactory.getInstance().create();			
				recTemp = outRecSet.getRecord();
				
				szOldStkPos   		= yddatautil.setDataDefault(recTemp.getFieldString("YD_DN_WO_LOC"),"");					
				szOldStkLyrNo 		= yddatautil.setDataDefault(recTemp.getFieldString("YD_DN_WO_LAYER"),"");
				szYdWbookId 		= ydDaoUtils.paraRecChkNull(recTemp, "YD_WBOOK_ID");
				szSendYdWrkProgStat = ydDaoUtils.paraRecChkNull(recTemp, "YD_WRK_PROG_STAT"); //현 스케줄 작업 진행상태(DB)
				szYdSchcd 			= ydDaoUtils.paraRecChkNull(recTemp, "YD_SCH_CD");
				
				szLogMsg = "[JSP Session] " + szOperationName + "현 스케줄 작업 진행상태(DB)[ " + szSendYdWrkProgStat  +" ]"  ;
        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
        		
				
				szLogMsg = "[JSP Session] " + szOperationName + "DB 권하지시위치 [ " + szOldStkPos + "-" + szOldStkLyrNo +"]"  ;
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				
				
				szLogMsg = "[JSP Session] " + szOperationName + "입력받은 권하지시위치 [" + szStkPos + "]"  ;
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				
				if ("".equals(szStkPos)){		
					szLogMsg = "[JSP Session] " + szOperationName + "변경 권하지시위치 정보가 없습니다."  ;
            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szLogMsg);	
					return outRecord;
				}
				
				
				if(szOldStkPos.equals(szStkPos)){
					szLogMsg = "[JSP Session] " + szOperationName + "입력된 권하위치가 기존 권하위치와 같습니다."  ;
            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szLogMsg);	
					return outRecord;
					
				}
					
				
				if(szStkPos.length() >=8)
				{
					szStkColGp = szStkPos.substring(0, 6); 
					szStkBedNo = szStkPos.substring(6, 8);
					szEqpGp    = szStkColGp.substring(2,4);
				}else{
					szLogMsg = "[JSP Session] " + szOperationName + "변경 권하지시위치 정보가 맞지 않습니다"  ;
            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szLogMsg);	
					return outRecord;
				}
				//-----------------------------------------------------------------------
				
				inRecord = JDTORecordFactory.getInstance().create();
				inRecord.setField("YD_STK_COL_GP" 	, szStkColGp);	
				inRecord.setField("YD_STK_BED_NO" 	, szStkBedNo);	
				inRecord.setField("YD_STK_LYR_NO" 	, szStkLyrNo);	

				
				outRecSet  	= JDTORecordFactory.getInstance().createRecordSet("YD");
				szLogMsg = "[JSP Session] " + szOperationName +  "적치단 정보조회";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				intRtnVal = ydStkLyrDao.getYdStklyr(inRecord, outRecSet, 0);
				if (intRtnVal == 0){
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, "저장위치 이상");	
					return outRecord;
				} else if ( intRtnVal > 0 )	{
					recTemp   	= JDTORecordFactory.getInstance().create();
					outRecSet.first();
					recTemp 	= outRecSet.getRecord();
					
					String sYD_STK_LYR_MTL_STAT	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_MTL_STAT"); 
					String sYD_STK_LYR_ACT_STAT	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_ACT_STAT"); 
					
					ydUtils.putLog(szSessionName, szMethodName, "sYD_STK_LYR_MTL_STAT:" + sYD_STK_LYR_MTL_STAT, YdConstant.DEBUG);
					if(sYD_GP.equals("J")&& szYdSchcd.substring(2, 4).equals("HC")){
						//결로재 적치위치
						if ( (sYD_STK_LYR_MTL_STAT.equals("E")) && (sYD_STK_LYR_ACT_STAT.equals("S"))) {
							
						} else {	
							outRecord.setField("RTN_CD" 	, "0");	
							outRecord.setField("RTN_MSG" 	, "제품이 있거나 결로재 적치위치가 아닙니다.");	
							return outRecord;
						}
					}else{
						//일반재 적치위치
						if ((sYD_STK_LYR_MTL_STAT.equals("E")) && (sYD_STK_LYR_ACT_STAT.equals("E"))) {
						
						} else {	
							outRecord.setField("RTN_CD" 	, "0");	
							outRecord.setField("RTN_MSG" 	, "제품이 있거나 예약상태/금지  입니다.");	
							return outRecord;
						}
					}
					
				}
//				if (sYD_GP.equals("H")){	
//					outRecord1 = ydToLocDcsnUtil.CoilLyrBaseCheck(sTAG_STL_NO, inRecord);
//				} else {
//					outRecord1 = ydToLocDcsnUtil.CoilGdsLyrBaseCheck(sTAG_STL_NO, inRecord);
//				}	
//				sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
//				sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
//				
//				if (!("1".equals(sRTN_CD))) {
//					outRecord.setField("RTN_CD" 	, "0");	
//					outRecord.setField("RTN_MSG" 	, sRTN_MSG);	
//					return outRecord;
//				}
				
				if (sYD_GP.equals("H")){	
					outRecord1 = ydToLocDcsnUtil.CoilLyrBaseCheck(sTAG_STL_NO, inRecord);
					sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
					sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
					
					if (!("1".equals(sRTN_CD))) {
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, sRTN_MSG);	
						return outRecord;
					}
	
				} else {					
					//권하위치 기본 체크 예외동 처리 (A,B,C,L,M,80,70) 결로재 적치 포함 
					if(szStkColGp.substring(2, 4).equals("80")
							||szStkColGp.substring(2, 4).equals("70")
							|| szStkColGp.substring(1, 2).equals("L")
							|| szStkColGp.substring(1, 2).equals("M")
							|| szStkColGp.substring(1, 2).equals("C")
							|| szStkColGp.substring(1, 2).equals("B")
							|| szStkColGp.substring(1, 2).equals("A")
							||(sYD_GP.equals("J")&& szYdSchcd.substring(2, 4).equals("HC"))
						) {  // 가상 bed임
						
					} else {
						outRecord1 = ydToLocDcsnUtil.CoilGdsLyrBaseCheck(sTAG_STL_NO, inRecord);
						sRTN_CD		= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
						sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
						
						if (!("1".equals(sRTN_CD))) {
							outRecord.setField("RTN_CD" 	, "0");	
							outRecord.setField("RTN_MSG" 	, sRTN_MSG);	
							return outRecord;
						}
					}	
				}
				//-----------------------------------------------------------------------
				
				//-----------------------------------------------------------------------
				//	권하위치 변경할시에 작업지시 취소전문 발생 (데이터 변경전정보)
				//-----------------------------------------------------------------------

				//recTemp : 스케줄 정보
				
				szYdWrkProgStat = ydDaoUtils.paraRecChkNull(recTemp, "YD_WRK_PROG_STAT");
				szYdSchCd       = ydDaoUtils.paraRecChkNull(recTemp, "YD_SCH_CD");

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
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szLogMsg);	
					return outRecord;
				} else if (intRtnVal < 0){
					
					szLogMsg = "[JSP Session] " + szOperationName + "해당 스케줄에 해당되는 재료 조회시 ERROR";
            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szLogMsg);	
					return outRecord;
				}
			
				
				//기존 산적위치 정보가 올바르지 않거나 없을 경우는 기존산적위치를 삭제할 수없으므로 건너띈다.
				szLogMsg = "[szOldStkPos] " + szOldStkPos + "해당 스케줄에 해당되는 재료 조회";
				if( szOldStkPos.length() == 8 && (!szOldStkPos.equals("XX010101")))
				{	
					szOldStkColGp = szOldStkPos.substring(0, 6); 
					szOldStkBedNo = szOldStkPos.substring(6, 8);
					szLogMsg = "[JSP Session] " + szOperationName +  "기존위치정보 :"+ szOldStkColGp  + "-"+ szOldStkBedNo +":" +szOldStkLyrNo;				
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
            	
            		
					
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
		                
		                szLogMsg = "[JSP Session] " + szOperationName +   "기존 지시위치 에 쌓여 있는 정보 Clear ";
	            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
	            		
		                intRtnVal = ydStkLyrDao.updYdStklyr(recSet, 0);
		            	if (intRtnVal < 1)
						{
		            		szLogMsg = "[JSP Session] " + szOperationName +   "기존 지시위치 에 쌓여 있는 정보 Clear 실패 [ " + intRtnVal +" ] ";
		            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
							outRecord.setField("RTN_CD" 	, "0");	
							outRecord.setField("RTN_MSG" 	, szLogMsg);	
							return outRecord;
						}
		            	
		            	szLogMsg = "[JSP Session] " + szOperationName +   "기존 지시위치 에 쌓여 있는 정보 Clear 성공 [ " + intRtnVal + " ] ";
		            	ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
		            		
					}		
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
	            	
	            	ydUtils.displayRecord(szOperationName, recSet);
	            	
	            	
	                intRtnVal = ydStkLyrDao.updYdStklyr(recSet, 0);
	                
	            	if (intRtnVal < 1)
					{
						//신규위치에 정보를 Setting 실패
	            		szLogMsg = "[JSP Session] " + szOperationName + "신규위치에 정보를 Setting 실패 [ " + intRtnVal + " ]"  ;
	            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, szLogMsg);	
						return outRecord;
					}
	            	
	        		//신규위치에 정보를 Setting 실패
            		szLogMsg = "[JSP Session] " + szOperationName + "신규위치에 정보를 Setting 성공 [ " + intRtnVal + " ]"  ;
            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
	            	
				}
				
		
				// 권하위치 정보 스케줄 정보에서 변경
				
				//권하지시베드조회
				outRecSet1 = JDTORecordFactory.getInstance().createRecordSet("");
				outRecord2 = JDTORecordFactory.getInstance().create();
				outRecord2.setField("YD_STK_COL_GP", szStkColGp);
				outRecord2.setField("YD_STK_BED_NO", szStkBedNo );
				outRecord2.setField("YD_STK_LYR_NO", szStkLyrNo);    
				/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedLyr*/
				intRtnVal = ydStkBedDao.getYdStkbed(outRecord2, outRecSet1, 304);
				if(intRtnVal <= 0){
					szLogMsg="Y5UpdGradStkLyr getYdStkbed : execution failed";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					outRecord.setField("RTN_CD" , "0");	
					outRecord.setField("RTN_MSG", szLogMsg);	
					return outRecord;
	
				}
				outRecSet1.absolute(1);
				outRecord3 = JDTORecordFactory.getInstance().create();
				outRecord3.setRecord(outRecSet1.getRecord());
				
				sDN_YD_STK_LYR_XAXIS 		= ydDaoUtils.paraRecChkNull(outRecord3, "YD_STK_LYR_XAXIS");
				sDN_YD_STK_BED_XAXIS_TOL 	= ydDaoUtils.paraRecChkNull(outRecord3, "YD_STK_BED_XAXIS_TOL");
				sDN_YD_STK_LYR_YAXIS 		= ydDaoUtils.paraRecChkNull(outRecord3, "YD_STK_LYR_YAXIS");
				sDN_YD_STK_BED_YAXIS_TOL 	= ydDaoUtils.paraRecChkNull(outRecord3, "YD_STK_BED_YAXIS_TOL");
				// 150619 hun 추가 -- 무인화작업
				sDN_YD_STK_LYR_ZAXIS 		= ydDaoUtils.paraRecChkNull(outRecord3, "YD_STK_LYR_ZAXIS");
				sDN_YD_STK_BED_ZAXIS_TOL 	= ydDaoUtils.paraRecChkNull(outRecord3, "YD_STK_BED_ZAXIS_TOL");
				
				
				
				recPara   = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID"	, yddatautil.setDataDefault(inDto[x].getField("YD_CRN_SCH_ID"), ""));
				recPara.setField("YD_DN_WO_LOC"		, szStkColGp+szStkBedNo);				
				recPara.setField("YD_DN_WO_LAYER"	, szStkLyrNo);
				recPara.setField("YD_DN_WO_LOC_XAXIS"		, sDN_YD_STK_LYR_XAXIS);
				recPara.setField("YD_DN_WO_XAXIS_GAP_MAX"	, sDN_YD_STK_BED_XAXIS_TOL);
				recPara.setField("YD_DN_WO_XAXIS_GAP_MIN"	, sDN_YD_STK_BED_XAXIS_TOL);
				recPara.setField("YD_DN_WO_LOC_YAXIS"		, sDN_YD_STK_LYR_YAXIS);
				recPara.setField("YD_DN_WO_YAXIS_GAP_MAX"	, sDN_YD_STK_BED_YAXIS_TOL);
				recPara.setField("YD_DN_WO_YAXIS_GAP_MIN"	, sDN_YD_STK_BED_YAXIS_TOL);
				recPara.setField("YD_DN_WO_LOC_ZAXIS"		, sDN_YD_STK_LYR_ZAXIS);
				recPara.setField("YD_DN_WO_ZAXIS_GAP_MAX"	, sDN_YD_STK_BED_ZAXIS_TOL);
				recPara.setField("YD_DN_WO_ZAXIS_GAP_MIN"	, sDN_YD_STK_BED_ZAXIS_TOL);
				
				
				szLogMsg = "[JSP Session] " + szOperationName + " 권하위치 정보 스케줄 정보에서 변경(UPDATE)"  ;
        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				ydUtils.displayRecord(szOperationName, recPara);
				
				
				intRtnVal = ydCrnSchDao.updYdCrnsch(recPara, 0);
				
				if (intRtnVal < 1)
				{	
					szLogMsg = "[JSP Session] " + szOperationName +  "권하위치 스케줄 정보 변경 실패 [" +intRtnVal  + " ] " ;
            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szLogMsg);	
					return outRecord;
				}
				
				szLogMsg = "[JSP Session] " + szOperationName + " 권하위치 정보 스케줄 정보 UPDATE 성공 "  ;
        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				// 스케줄 변경 후 제원 위치정보를 맞춰준다.
				
//				szLogMsg =  "[JSP Session] " + szOperationName +  "스케줄 변경 후 제원 위치정보 세팅 호출";
//        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
//        		
//        		boolean lb_updYdCrnBed = false;        		
//        		lb_updYdCrnBed = YdUtils.updYdCrnschBedData(recPara);
//        		
//        		if(!lb_updYdCrnBed){
//        			szLogMsg =  "[JSP Session] " + szOperationName +  "스케줄 변경 후 제원 위치정보 세팅 실패";
//            		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
//        			
//        		}
//		
//				szLogMsg =  "[JSP Session] " + szOperationName +  "스케줄 변경 후 제원 위치정보 세팅 완료";
//        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
//				     
				
				//권하위치 변경후 작업지시정보를  재전송 (Manual 작업지시보내는 방법으로  선택 인경우만 전송한다)
							 
        		
        		if( szSendYdWrkProgStat.equals(YdConstant.YD_EQP_STAT_UP_WO) || szSendYdWrkProgStat.equals(YdConstant.YD_EQP_STAT_UP_CMPL)){
					
					szLogMsg =  "[JSP Session] " + szOperationName +  "권하위치 변경후 작업지시정보를  재전송";
	        		ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
	        		
					
//					if(szYdSchCd.length() > 0 ){
//						
//						szYdGp = szYdSchCd.substring(0,1);
//					}else{
//						szLogMsg = "[JSP Session] " + szOperationName + " 스케줄코드의 야드구분이 올바르지 않습니다";
//						ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.ERROR);
//					}
//					
//					
//					szLogMsg =  "[JSP Session] " + szOperationName + "   - 야드구분[" + szYdGp + "]";
//					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
//					
					szLogMsg =  "[JSP Session] " + szOperationName + "   - 스케줄 진행상태 ["  + szSendYdWrkProgStat +" ]";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					
					szJMS_TC_CD = "Y5YDL007";
					
			
					szYD_EQP_ID = inDto[x].getFieldString("YD_EQP_ID");
					
					szLogMsg = "[JSP Session] " + szOperationName +  "- 야드구분[" + szYdGp + "], 크레인 설비[" + szYD_EQP_ID + "]";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
//SJH03004			
					recPara.setField("JMS_TC_CD", 			szJMS_TC_CD);
//					recPara.setField("JMS_TC_CD"               	, szJMS_TC_CD);

					recPara.setField("YD_EQP_ID" 				, szYD_EQP_ID);
					recPara.setField("YD_WRK_PROG_STAT" 		, szSendYdWrkProgStat);
					
					ydUtils.displayRecord(szOperationName, recPara);
					
					ejbConn = new EJBConnector("default", "CoilCraneLdHdSeEJB", this);		
					szRtnMsg = (String)ejbConn.trx("procY5CrnWrkOrdReq", new Class[] { JDTORecord.class }, new Object[] { recPara });	

//					ydDelegate.sendMsg(recPara);
					
					
					
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
        		
        		if(szEqpGpBefo.equals(YdConstant.YD_EQP_GP_TCAR) 
        				||szEqpGpBefo.equals(YdConstant.YD_EQP_GP_PALLET) 
        				||szEqpGpBefo.equals(YdConstant.YD_EQP_GP_TRAILER) 
        				
        		){
        			if(!szEqpGpBefo.equals(szEqpGp)){
        				
        				//szYdWbookId - 현 스케줄의 작업예약 ID
        				//delWBookBefoCarOrTCar
        				
        				recPara   = JDTORecordFactory.getInstance().create();
        				recPara.setField("YD_WBOOK_ID", szYdWbookId);
        				
        				yddatautil.delWBookBefoCarOrTCar(recPara);
        				
        			}
        		}
			}		
					
			outRecord.setField("RTN_CD" 	, "1");	
			return outRecord;

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
			
		} 		
	}	// end of updToPosFix
	
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
		JDTORecord 			outRecord 		= JDTORecordFactory.getInstance().create(); // 
		JDTORecord 			recPara			= JDTORecordFactory.getInstance().create();
		JDTORecord 			recTmp			= JDTORecordFactory.getInstance().create();
		JDTORecordSet   	outRecSet  		= null;
		
		String 				sTcar			= "";
		String 				sFrombayGp		= "";
		String 				sTobayGp		= "";
		String 				ydGp			= "";
		
		
		try {
			
			ydGp = inDto.getFieldString("YD_GP");
			sTcar = inDto.getFieldString("T_CAR_CD");
			sFrombayGp = inDto.getFieldString("FROM_BAY_GP");
			sTobayGp = inDto.getFieldString("TO_BAY_GP");
			if(sFrombayGp.equals(sTobayGp)){
				outRecord.setField("RTN_CD" 	, "-1");	
				outRecord.setField("RTN_MSG" 	, "같은동에 이적시에는 대차를 사용하지않습니다.");	
				return outRecord;
			}
			
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
					outRecord.setField("RTN_CD" 	, "-2");	
					outRecord.setField("RTN_MSG" 	, "현재 야드에 대차가 존재 하지 않습니다.");	
					return outRecord;
				}
				// 대차 이동구간 체크 
				int fn = recTmp.getFieldString("YD_TCAR_WRK_ABLE_BAY").indexOf(sFrombayGp);
				int tn = recTmp.getFieldString("YD_TCAR_WRK_ABLE_BAY").indexOf(sTobayGp);
				if( fn < 0 || tn < 0){
					// from동과 To동이 대차 이동구간에 포함 되지 않을 경우 error 
					outRecord.setField("RTN_CD" 	, "-3");	
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
	 * 이송재료LIST
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
		int intRtnVal = 0;
		
		try {
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 : 파라미터 확인 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			ydUtils.displayRecord(szOperationName, inDto);
			
			szIN_OUT_GP 			= ydDaoUtils.paraRecChkNull(inDto, "IN_OUT_GP");			//입고/출고구분
			szYD_DONG_GP 			= ydDaoUtils.paraRecChkNull(inDto, "YD_DONG_GP");
			szYD_SPAN_GP 			= ydDaoUtils.paraRecChkNull(inDto, "YD_SPAN_GP");
			szYD_COL_GP 			= ydDaoUtils.paraRecChkNull(inDto, "YD_COL_GP");
			if( szIN_OUT_GP.equals("1")) {					//입고
				szARR_WLOC_CD 			= ydDaoUtils.paraRecChkNull(inDto, "SPOS_WLOC_CD");	
				szSPOS_WLOC_CD 			= ydDaoUtils.paraRecChkNull(inDto, "ARR_WLOC_CD");
				szYD_STK_COL_GP 		= "";
			}else{											//출고
				szSPOS_WLOC_CD 			= ydDaoUtils.paraRecChkNull(inDto, "SPOS_WLOC_CD");
				szARR_WLOC_CD 			= ydDaoUtils.paraRecChkNull(inDto, "ARR_WLOC_CD");
				szYD_STK_COL_GP 		= szYD_DONG_GP + szYD_SPAN_GP + szYD_COL_GP;
			}
			
//			szDATE_FROM   			= ydDaoUtils.paraRecChkNull(inDto, "DATE_FROM");
//			szDATE_TO   			= ydDaoUtils.paraRecChkNull(inDto, "DATE_TO");
//			szWO_STATE   			= ydDaoUtils.paraRecChkNull(inDto, "WO_STATE");
			szYD_AIM_RT_GP 			= ydDaoUtils.paraRecChkNull(inDto, "YD_AIM_RT_GP");
			szYD_AIM_YD_GP 			= ydDaoUtils.paraRecChkNull(inDto, "YD_AIM_YD_GP");
			szYD_AIM_BAY_GP 		= ydDaoUtils.paraRecChkNull(inDto, "YD_AIM_BAY_GP");
//			szMAKER_NAME 			= ydDaoUtils.paraRecChkNull(inDto, "MAKER_NAME");
			szYD_GP 				= ydDaoUtils.paraRecChkNull(inDto, "YD_GP");
			
			//------------------------------------------------------------------------
			//	날짜를 전체로 조회할 것인 지를 판단하는 변수 추가
			//	수정자 : 임춘수
			//	수정일 : 2010.01.29
			//------------------------------------------------------------------------
//			szRD_DATE_ALL			= ydDaoUtils.paraRecChkNull(inDto, "RD_DATE_ALL");
			
//			if( szRD_DATE_ALL.equals("Y")) {
//				szDATE_FROM				= "00000000";
//				szDATE_TO				= "99999999";
//			}
			
			//------------------------------------------------------------------------
			
			outRecSet      = JDTORecordFactory.getInstance().createRecordSet("YD");
			recPara        = JDTORecordFactory.getInstance().create();
		    recPara.setField("WO_STATE",     	szWO_STATE);
		    recPara.setField("SPOS_WLOC_CD", 	szSPOS_WLOC_CD);
		    recPara.setField("ARR_WLOC_CD",  	szARR_WLOC_CD);
		    recPara.setField("YD_GP",  			szYD_GP);
//		    recPara.setField("DATE_FROM",    	szDATE_FROM);
//		    recPara.setField("DATE_TO",      	szDATE_TO);
		    recPara.setField("YD_STK_COL_GP",    szYD_STK_COL_GP);
		    recPara.setField("YD_AIM_RT_GP",    szYD_AIM_RT_GP);
		    recPara.setField("YD_AIM_YD_GP",    szYD_AIM_YD_GP);
		    recPara.setField("YD_AIM_BAY_GP",   szYD_AIM_BAY_GP);
		    //recPara.setField("MAKER_NAME",     	szMAKER_NAME);
		    recPara.setField("YD_PREP_WK_ST",   "L");
			recPara.setField("PAGE_NO",      	inDto.getField("PAGE_NO"));		
			recPara.setField("ROW_CNT",      	inDto.getField("ROWCOUNT"));
			if( szIN_OUT_GP.equals("1")) {					//입고
				intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 612);
			}else {	
				intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 200);
			}
//			}else if( szWO_STATE.equals("2")){									//완료
//				intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 201);
//			}else if( szWO_STATE.equals("3")){									//이송LOT편성
//				intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 202);
//			}
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
	 * 목표행선/목표야드 /  목표동 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecord updCoilYdTransMtlList(JDTORecord [] inDto) throws DAOException {
		
		
		int intRtnVal = 0;
		String szMsg="";
		String szMethodName="updCoilYdTransMtlList";
		String szRcvMsg = "";
		
		
		JDTORecord recPara = JDTORecordFactory.getInstance().create();		
		YdStockDao ydStockDao = new YdStockDao();
		JDTORecord 			outRecord 		= JDTORecordFactory.getInstance().create(); // 
		 
		try {
			//메뉴 페이지 목록을 수정한다.
			for(int x=0;x<inDto.length;x++){
			
				
				//수정할 항목 SETTING
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("STL_NO", yddatautil.setDataDefault(inDto[x].getField("STL_NO"), ""));
				recPara.setField("YD_AIM_RT_GP",  yddatautil.setDataDefault(inDto[x].getField("YD_AIM_RT_GP"),  "").toUpperCase());
				recPara.setField("YD_AIM_BAY_GP", yddatautil.setDataDefault(inDto[x].getField("YD_AIM_BAY_GP"), "").toUpperCase());	
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
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szRcvMsg);	
					return outRecord;

					
				} // end of if
				
			}
			outRecord.setField("RTN_CD" 	, "1");	
			return outRecord;
			
			
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally{
		}		
	}	// end of updCoilYdTransMtlList	
	
	/**
	 * 이송대상재를 준비스케줄에 등록 - 자동, 크레인 등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecord insYdPrepSchNCrn(JDTORecord inDto) throws DAOException {
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
		JDTORecord 			outRecord 		= JDTORecordFactory.getInstance().create(); // 
		
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
//			szYD_AIM_RT_GP 			= ydDaoUtils.paraRecChkNull(inDto, "YD_AIM_RT_GP");
//			szDATE_FROM   			= ydDaoUtils.paraRecChkNull(inDto, "DATE_FROM");
//			szDATE_TO   			= ydDaoUtils.paraRecChkNull(inDto, "DATE_TO");
			
			szYD_AIM_YD_GP 			= ydDaoUtils.paraRecChkNull(inDto, "YD_AIM_YD_GP");
			szYD_AIM_BAY_GP 		= ydDaoUtils.paraRecChkNull(inDto, "YD_AIM_BAY_GP");
			//야드구분
			szYD_GP 				= ydDaoUtils.paraRecChkNull(inDto, "YD_GP");
			
			outRecSet      = JDTORecordFactory.getInstance().createRecordSet("YD");
			recPara        = JDTORecordFactory.getInstance().create();
		    recPara.setField("SPOS_WLOC_CD", 	szSPOS_WLOC_CD);
		    recPara.setField("ARR_WLOC_CD",  	szARR_WLOC_CD);
		    recPara.setField("YD_GP",  			szYD_GP);
		    recPara.setField("YD_STK_COL_GP",   szYD_STK_COL_GP);
//		    recPara.setField("YD_AIM_RT_GP",    szYD_AIM_RT_GP);
		    recPara.setField("YD_AIM_YD_GP",    szYD_AIM_YD_GP);
		    recPara.setField("YD_AIM_BAY_GP",   szYD_AIM_BAY_GP);
		    recPara.setField("YD_PREP_WK_ST",   "L");
		    recPara.setField("PAGE_NO",      	"1");		
			recPara.setField("ROW_CNT",      	"1000");
			
			//com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdFrtoMoveOrdMtlList
		    //intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 158);
		    intRtnVal = ydStockDao.getYdStock(recPara, outRecSet, 200);
		    if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "[JSP Session : "+szOperationName+"] 조회 시 오류발생[1] - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				} else {
					szMsg = "[JSP Session : "+szOperationName+"] 조회 시 오류발생[2] - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				}
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;

			} // end of if
			
			if( intRtnVal == 0 ) {
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
			}
			jdtoRcd = JDTORecordFactory.getInstance().create();
			/*
			 * 1-1-0. BRE Rule에서 Pallet/Trailer에 따른 야드설비작업매수를 조회
			 */
			if( szCAR_GP.equals("P") ) {
		    		intLOT_SH = 6;
			}else if( szCAR_GP.equals("T") ) {
		    		intLOT_SH = 3;
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
					szSTL_NO  		= ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
					szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");
					szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
					szYD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO");
					szYD_GP 		= szYD_STK_COL_GP.substring(0, 1);
					szYD_AIM_YD_GP 	= ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_YD_GP");
					szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_BAY_GP");
					szARR_WLOC_CD 	= ydDaoUtils.paraRecChkNull(recPara, "ARR_WLOC_CD");
					lngYD_MTL_WT 	= ydDaoUtils.paraRecChkNullLong(recPara, "YD_MTL_WT");
					
					szYD_AIM_YD_GP = YdCommonUtils.getYdFromWlocCd(szARR_WLOC_CD);
					
					lngYD_MTL_WT_SUM += lngYD_MTL_WT;
					//szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT01UM";
					
					if(szYD_GP.equals("H")){
						if(szSPOS_WLOC_CD.equals("DJY1E")) {
							szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT02UM";//제품통로
						} else {
							szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT01UM";//소재통로 
						}
					} else {
						if(szYD_STK_COL_GP.substring(1, 2).equals("B")||szYD_STK_COL_GP.substring(1, 2).equals("C")){
							if(Integer.parseInt(szYD_STK_COL_GP.substring(2, 4)) < 31){
								szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT52UM";  //제품이송
							} else {
								szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT02UM";  //제품이송
							}
						} else {
							szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT02UM";  //제품이송
						}
		
//						szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT02UM";//제품창고
					}	
					
					if( j == 1 ) {
						szYD_PREP_SCH_ID = ydPrepSchDao.getYdPrepschId();
						recTemp = JDTORecordFactory.getInstance().create();
						//준비스케줄 등록
						recTemp.setField("YD_PREP_SCH_ID"	, szYD_PREP_SCH_ID);
						recTemp.setField("YD_SCH_CD"		, szYD_SCH_CD);
						recTemp.setField("REGISTER"			, szUserId);
						recTemp.setField("YD_GP"			, szYD_GP);
						recTemp.setField("YD_PREP_WK_ST"	, "L");
						recTemp.setField("ARR_WLOC_CD"		, szARR_WLOC_CD);
						recTemp.setField("YD_AIM_YD_GP"		, szYD_AIM_YD_GP);
						recTemp.setField("YD_AIM_BAY_GP"	, szYD_AIM_BAY_GP);
						recTemp.setField("YD_CARASGN_SEQ"	, YdConstant.YD_CARASGN_SEQ_AUTO_DEFAULT);
						//recTemp.setField("YD_EQP_WRK_SH", "" + intLOT_SH);
						recTemp.setField("YD_WRK_PLAN_CRN"	, szYD_EQP_ID);
						recTemp.setField("CAR_GP", szCAR_GP);
						
						intRtnVal = ydPrepSchDao.insYdPrepsch(recTemp);
						
						if( intRtnVal <= 0 ) {
							szMsg = "[JSP Session : "+szOperationName+"] ["+i+"번]준비스케줄["+szYD_PREP_SCH_ID+"] 등록 시 오류발생[2] - 반환값 : " + intRtnVal;
							ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
							outRecord.setField("RTN_CD" 	, "0");	
							outRecord.setField("RTN_MSG" 	, szMsg);	
							return outRecord;

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
					recTemp.setField("STL_NO"			, szSTL_NO);
					recTemp.setField("YD_PREP_SCH_ID"	, szYD_PREP_SCH_ID);
					recTemp.setField("REGISTER"			, szUserId);
					recTemp.setField("YD_STK_COL_GP"	, szYD_STK_COL_GP);
					recTemp.setField("YD_STK_BED_NO"	, szYD_STK_BED_NO);
					recTemp.setField("YD_STK_LYR_NO"	, szYD_STK_LYR_NO);
					
					intRtnVal = ydPrepMtlDao.insYdPrepmtl(recTemp);
					
					if( intRtnVal <= 0 ) {
						szMsg = "[JSP Session : "+szOperationName+"] ["+i+"번]준비스케줄["+szYD_PREP_SCH_ID+"]의 ["+j+"]번재료["+szSTL_NO+"] 등록 시 오류발생[1] - 반환값 : " + intRtnVal;
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, szMsg);	
						return outRecord;
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
					if( intRtnVal <= 0 ) {
						szMsg = "[JSP Session : "+szOperationName+"] ["+i+"번]준비스케줄재료["+szSTL_NO+"] 등록 시 오류발생[1] - 반환값 : " + intRtnVal;
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, szMsg);	
						return outRecord;
					}else{
						szMsg = "[JSP Session : "+szOperationName+"] ["+i+"번]준비스케줄번재료["+szSTL_NO+"] 등록 성공 ";
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
					
				}
				
				if( !bIsLoopable ) break;
				
			}

			szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄 등록 성공 - 메소드 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			outRecord.setField("RTN_CD" 	, "1");	
			return outRecord;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

	}//end of insYdPrepSchNCrn
	
	/**
	 * 이송대상재를 준비스케줄에 등록 - 수동, 크레인 등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecord insYdPrepSchNCrnByManual(JDTORecord[] inDto) throws DAOException {
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
		long		lngYD_MTL_WT		= 0;
		long		lngYD_MTL_WT_SUM	= 0;
		String		szSPOS_WLOC_CD			= null;
		JDTORecord 			outRecord 		= JDTORecordFactory.getInstance().create(); // 
		int intRtnVal = 0;
		
		try {
			intLOT_SH = inDto.length;
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 - 대상재건수["+intLOT_SH+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szUserId 		= ydDaoUtils.paraRecChkNull(inDto[0], "YD_USER_ID");
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(inDto[0], "YD_STK_COL_GP");
			szSPOS_WLOC_CD 	= ydDaoUtils.paraRecChkNull(inDto[0], "SPOS_WLOC_CD");
			szARR_WLOC_CD 	= ydDaoUtils.paraRecChkNull(inDto[0], "ARR_WLOC_CD");
			szYD_AIM_BAY_GP = ydDaoUtils.paraRecChkNull(inDto[0], "YD_AIM_BAY_GP");
			szYD_AIM_YD_GP 	= ydDaoUtils.paraRecChkNull(inDto[0], "YD_AIM_YD_GP");
			szYD_EQP_ID 	= ydDaoUtils.paraRecChkNull(inDto[0], "YD_EQP_ID");
			szCAR_GP		= ydDaoUtils.paraRecChkNull(inDto[0], "CAR_GP");				//차량구분
			
			szMsg = "[JSP Session : "+szOperationName+"] 설비ID(크레인ID)["+szYD_EQP_ID+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szYD_GP = szYD_STK_COL_GP.substring(0, 1);
			if(szYD_GP.equals("H")){
				if(szSPOS_WLOC_CD.equals("DJY1E")) {
					szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT02UM";//제품통로
				} else {
					szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT01UM";//소재통로 
				}
			} else {
				if(szYD_STK_COL_GP.substring(1, 2).equals("B")||szYD_STK_COL_GP.substring(1, 2).equals("C")){
					if(Integer.parseInt(szYD_STK_COL_GP.substring(2, 4)) < 31){
						szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT52UM";  //제품이송
					} else {
						szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT02UM";  //제품이송
					}
				} else {
					szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT02UM";  //제품이송
				}

//				szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT02UM";//제품창고
			}	
			//준비스케줄 등록
			szYD_PREP_SCH_ID = ydPrepSchDao.getYdPrepschId();
			
			recTemp = JDTORecordFactory.getInstance().create();
			recTemp.setField("YD_PREP_SCH_ID"	, szYD_PREP_SCH_ID);
			recTemp.setField("YD_SCH_CD"		, szYD_SCH_CD);
			recTemp.setField("REGISTER"			, szUserId);
			recTemp.setField("YD_GP"			, szYD_GP);
			recTemp.setField("YD_PREP_WK_ST"	, "L");
			recTemp.setField("ARR_WLOC_CD"		, szARR_WLOC_CD);
			recTemp.setField("YD_AIM_YD_GP"		, szYD_AIM_YD_GP);
//SJH			recTemp.setField("YD_AIM_BAY_GP"	, szYD_AIM_BAY_GP);
			recTemp.setField("YD_AIM_BAY_GP"	, szYD_EQP_ID.substring(1,2));
			recTemp.setField("YD_CARASGN_SEQ"	, YdConstant.YD_CARASGN_SEQ_MAN_DEFAULT);
			recTemp.setField("YD_EQP_WRK_SH"	, "" + intLOT_SH);
			recTemp.setField("YD_WRK_PLAN_CRN"	, szYD_EQP_ID);
			recTemp.setField("CAR_GP"			, szCAR_GP);
			
			intRtnVal = ydPrepSchDao.insYdPrepsch(recTemp);
			
			if( intRtnVal <= 0 ) {
				szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"] 등록 시 오류발생[1] - 반환값 : " + intRtnVal;
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
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
				recTemp.setField("STL_NO"			, szSTL_NO);
				recTemp.setField("YD_PREP_SCH_ID"	, szYD_PREP_SCH_ID);
				recTemp.setField("REGISTER"			, szUserId);
				recTemp.setField("YD_STK_COL_GP"	, szYD_STK_COL_GP);
				recTemp.setField("YD_STK_BED_NO"	, szYD_STK_BED_NO);
				recTemp.setField("YD_STK_LYR_NO"	, szYD_STK_LYR_NO);
				
				intRtnVal = ydPrepMtlDao.insYdPrepmtl(recTemp);
				
				if( intRtnVal <= 0 ) {
					szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"]의 ["+(i + 1)+"]번재료["+szSTL_NO+"] 등록 시 오류발생[1] - 반환값 : " + intRtnVal;
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
				}else{
					szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"]의 ["+(i + 1)+"]번재료["+szSTL_NO+"] 등록 성공 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
			}
			
			recTemp = JDTORecordFactory.getInstance().create();
			//준비스케줄수정
			recTemp.setField("YD_PREP_SCH_ID"		, szYD_PREP_SCH_ID);
			recTemp.setField("YD_INV_SUM_WT"		, "" + lngYD_MTL_WT_SUM);
			intRtnVal = ydPrepSchDao.updYdPrepsch(recTemp, 0);
			
			szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄 등록 성공 - 메소드 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			outRecord.setField("RTN_CD" 	, "1");	
			return outRecord;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

	}//end of insYdPrepSchNCrnByManual
	
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
		String		szSPOS_WLOC_CD1		= null;
		
		int intRtnVal = 0;
		
		try {
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 : 파라미터 확인 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			ydUtils.displayRecord(szOperationName, inDto);
			
			szYD_GP 			= ydDaoUtils.paraRecChkNull(inDto, "YD_GP");
			szYD_DONG_GP 		= ydDaoUtils.paraRecChkNull(inDto, "YD_DONG_GP");
			szYD_WRK_PLAN_CRN 	= ydDaoUtils.paraRecChkNull(inDto, "YD_WRK_PLAN_CRN");
			szCAR_GP 			= ydDaoUtils.paraRecChkNull(inDto, "CAR_GP");
			szYD_PREP_WK_ST		= "L";
			szSPOS_WLOC_CD 		= ydDaoUtils.paraRecChkNull(inDto, "SPOS_WLOC_CD");
			
			if(szSPOS_WLOC_CD.equals("DJY21")) {
				szSPOS_WLOC_CD1 		= "DJY22";
			} else if(szSPOS_WLOC_CD.equals("DJY22")) {
				szSPOS_WLOC_CD1 		= "DJY21";
			} else  {
				szSPOS_WLOC_CD1 		= "";
			}
			outRecSet      = JDTORecordFactory.getInstance().createRecordSet("YD");
			recPara        = JDTORecordFactory.getInstance().create();
			recPara.setField("SPOS_WLOC_CD",   	szSPOS_WLOC_CD);
			recPara.setField("SPOS_WLOC_CD1",  	szSPOS_WLOC_CD1);
		    recPara.setField("YD_GP",     		szYD_GP);
		    recPara.setField("YD_SCH_CD",  		szYD_DONG_GP);
		    recPara.setField("YD_WRK_PLAN_CRN", szYD_WRK_PLAN_CRN);
		    recPara.setField("YD_PREP_WK_ST",   szYD_PREP_WK_ST);
		    recPara.setField("CAR_GP",   		szCAR_GP);
			recPara.setField("PAGE_NO",      	inDto.getField("PAGE_NO"));		
			recPara.setField("ROW_CNT",      	inDto.getField("ROWCOUNT"));
		
			//com.inisteel.cim.yd.dao.ydprepschdao.YdPrepschDao.getYdPrepschNWordCancelListByCrnPageCoil
			intRtnVal = ydPrepSchDao.getYdPrepsch(recPara, outRecSet, 403);
		
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
		String		szYD_PREP_SCH_ID			= null;
		String		szSPOS_WLOC_CD		= null;
		String		szQueryType			= null;
		
		int intRtnVal = 0;
		
		try {
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 : 파라미터 확인 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			ydUtils.displayRecord(szOperationName, inDto);
			
			szYD_PREP_SCH_ID	= ydDaoUtils.paraRecChkNull(inDto, "YD_PREP_SCH_ID");
			szSPOS_WLOC_CD   	= ydDaoUtils.paraRecChkNull(inDto, "SPOS_WLOC_CD");
			szQueryType 		= ydDaoUtils.paraRecChkNull(inDto, "QUERY_TYPE");
			
			outRecSet      = JDTORecordFactory.getInstance().createRecordSet("YD");
			recPara        = JDTORecordFactory.getInstance().create();
			recPara.setField("SPOS_WLOC_CD",    	szSPOS_WLOC_CD);
		    recPara.setField("YD_PREP_SCH_ID",    	szYD_PREP_SCH_ID);
		    //intRtnVal = yPrepMtlDao.getYdPrepmtl(recPara, outRecSet, 1);
///		    if( szQueryType.equals("SUPPLY")) {
//		    	//intRtnVal = yPrepMtlDao.getYdPrepmtl(recPara, outRecSet, 4);		//보급 이송LOT재료
//		    	intRtnVal = yPrepMtlDao.getYdPrepmtl(recPara, outRecSet, 6);		//스카핑/정정보급LOT재료
//		    }else{
		    	intRtnVal = yPrepMtlDao.getYdPrepmtl(recPara, outRecSet, 3);		//통합야드 이송LOT재료
//		    }
		    
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
	 * 준비재료삭제
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecord delYdPrepMtl(JDTORecord[] inDto) throws DAOException {
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
		//로컬변수 정의
	    int			intLOT_SH			= 0;
		String		szYD_PREP_SCH_ID	= null;
		String		szSTL_NO			= null;
		int			intYD_EQP_WRK_SH	= 0;
		int			intYD_INV_SUM_WT	= 0;
		int			intYD_MTL_WT		= 0;
		String		szYD_USER_ID		= null;
		int intRtnVal = 0;
		JDTORecord 			outRecord 		= JDTORecordFactory.getInstance().create(); // 
		try {
			intLOT_SH = inDto.length;
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 - 삭제할 준비스케줄 건수["+intLOT_SH+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szYD_PREP_SCH_ID= ydDaoUtils.paraRecChkNull(inDto[0], "YD_PREP_SCH_ID");
			szYD_USER_ID  	= ydDaoUtils.paraRecChkNull(inDto[0], "YD_USER_ID");
			
			outRecSet 		= JDTORecordFactory.getInstance().createRecordSet("");
			recPara         = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_PREP_SCH_ID",   szYD_PREP_SCH_ID);
			
			intRtnVal = ydPrepSchDao.getYdPrepsch(recPara, outRecSet, 0);
			
			if( intRtnVal <= 0 ) {
				szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"] 조회 시 오류발생 - 반환값 : " + intRtnVal;
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szMsg);	
				return outRecord;
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
				
				if( intRtnVal <= 0 ) {
					szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"]의 준비재료["+szSTL_NO+"]를 저장품 조회 시 오류발생 - 반환값 : " + intRtnVal;
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
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
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;

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
			
			outRecord.setField("RTN_CD" 	, "1");	
			outRecord.setField("RTN_MSG" 	, szMsg);	
			return outRecord;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}

	}//end of delYdPrepMtl
	
	/**
	 * 준비스케줄수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecord uptYdPrepSch(JDTORecord[] inDto) throws DAOException {
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
		JDTORecord 			outRecord 		= JDTORecordFactory.getInstance().create(); // 
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
				szUserId  				= ydDaoUtils.paraRecChkNull(inDto[i], "YD_USER_ID");
				szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"] 수정 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				recPara         = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_PREP_SCH_ID",   	szYD_PREP_SCH_ID);
//				if( szQueryType.equals("SUPPLY") ) {
//					recPara.setField("YD_CARASGN_SEQ",   	szYD_CARASGN_SEQ);
//				}else{
					
					if( !szYD_WRK_PLAN_CRN.equals("") )	{
						//recPara.setField("YD_SCH_CD",   		szYD_SCH_CD.substring(0, 5) + szYD_WRK_PLAN_CRN.substring(5) + szYD_SCH_CD.substring(6));
						recPara.setField("YD_WRK_PLAN_CRN",   	szYD_WRK_PLAN_CRN);
					}
					
					recPara.setField("YD_AIM_BAY_GP",   	szYD_AIM_BAY_GP);
					recPara.setField("YD_CARASGN_SEQ",   	szYD_CARASGN_SEQ);
					
//				}
				recPara.setField("MODIFIER",   			szUserId);
				//준비스케줄 수정
				intRtnVal =  ydPrepSchDao.updYdPrepsch(recPara, 0);
				if( intRtnVal <= 0 ) {
					szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"] 수정 시 오류발생 - 반환값 : " + intRtnVal;
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
				}	
				
				szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"] 수정 성공 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			szMsg = "[JSP Session : "+szOperationName+"] 준비스케줄 수정 성공 - 메소드 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			outRecord.setField("RTN_CD" 	, "1");	
			outRecord.setField("RTN_MSG" 	, szMsg);	
			return outRecord;			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}//end of uptYdPrepSch
		
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
	 * 차량 상차완료 처리 (구내운송:코일에서 사용함)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public JDTORecordSet getCoilYdCarUpEndPp(JDTORecord inDto) throws DAOException {
		//JDTO변수 정의
		JDTORecord       recPara        = null;
		JDTORecordSet    outRecSet      = null;
		//DAO 변수 정의
		CoilJspDAO 		dao 		= new CoilJspDAO();
		//기본변수 정의
		String      szMsg        		= "";
		String      szMethodName		= "getCoilYdCarUpEndPp";
		String		szOperationName		= "차량 상차완료LIST";
			
		try {
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 : 파라미터 확인 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			ydUtils.displayRecord(szOperationName, inDto);
			
			outRecSet      = JDTORecordFactory.getInstance().createRecordSet("YD");
			recPara        = JDTORecordFactory.getInstance().create();
			recPara.setField("V_WLOC_CD", 	ydDaoUtils.paraRecChkNull(inDto, "WLOC_CD"));
			recPara.setField("V_PAGE_NO",   ydDaoUtils.paraRecChkNull(inDto, "PAGE_NO"));		
			recPara.setField("V_ROW_CNT",   ydDaoUtils.paraRecChkNull(inDto, "ROWCOUNT"));

			outRecSet = dao.getcoilYdCarUpEndPp(recPara);
			
			if (outRecSet == null || outRecSet.size() == 0) {
				return outRecSet;
			} 
			
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
	 * 차량 상차완료 처리 (구내운송:코일에서 사용함)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecord updCoilYdCarUpEndPp(JDTORecord[] inDto) throws DAOException {
		/*
		 */
		//JDTO변수 정의
		JDTORecord  	recPara     	= JDTORecordFactory.getInstance().create();
		JDTORecord  	recTemp     	= JDTORecordFactory.getInstance().create();
		JDTORecord  	updPara1     	= JDTORecordFactory.getInstance().create();
		JDTORecord  	updPara2     	= JDTORecordFactory.getInstance().create();
		JDTORecord  	recOutTemp     	= JDTORecordFactory.getInstance().create();
		
		JDTORecordSet	outRecSet		= null;
		//DAO 변수 정의
		CoilJspDAO 		dao 		= new CoilJspDAO();
		YdStkColDao ydStkColDao = new YdStkColDao();
		//기본변수 정의
		String      szMsg        		= "";
		String      szMethodName		= "updCoilYdCarUpEndPp";
		String		szOperationName		= "차량 상차완료";
		//로컬변수 정의
		String		szYD_PREP_SCH_ID	= "";
		String		szUserId			= "";
		String		sSTL_NO				= "";
		String		sYD_STK_COL_GP 		= "";
		String		sYD_STK_BED_NO 		= "";
		String		sYD_STK_LYR_NO 		= "";
		String		sTO_YD_STK_COL_GP 	= "";
		String		sTO_YD_STK_BED_NO 	= "";
		String		sCRN_SCH_YN 		= "";
		String		sWRKBOOK_YN 		= "";
		String		szYD_CAR_SCH_ID		= "";
		String		sTRN_EQP_CD 		= "";
		String		sWLOC_CD 			= "";
		String		sPNT_CD 			= "";
		String		sYD_GP 				= "";
		String		sSPOS_WLOC_CD		= "";
		String		sARR_WLOC_CD 		= "";
		String		sFIRST_SPOS_WLOC_CD	= "";
		String		sFIRST_ARR_WLOC_CD 	= "";
			
		YdCarSchDao ydCarSchDao = new YdCarSchDao();
		YdCarFtmvMtlDao ydCarftmvmtlDao = new YdCarFtmvMtlDao();

		JDTORecord 	outRecord 			= JDTORecordFactory.getInstance().create(); // 
		int intRtnVal = 0;
		
		try {
			
			szMsg = "차량 상차완료 메소드 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);

			for(int i = 0; i < inDto.length; i++ ) {
				sSTL_NO				= ydDaoUtils.paraRecChkNull(inDto[i], "STL_NO");
				szUserId  			= ydDaoUtils.paraRecChkNull(inDto[i], "YD_USER_ID");
				sTO_YD_STK_BED_NO	= "0" + ydDaoUtils.paraRecChkNull(inDto[i], "RNUM");
				sTO_YD_STK_COL_GP	= ydDaoUtils.paraRecChkNull(inDto[i], "H_STK_COL_GP");
				sTRN_EQP_CD			= ydDaoUtils.paraRecChkNull(inDto[i], "H_CAR_NO");
				sWLOC_CD			= ydDaoUtils.paraRecChkNull(inDto[i], "H_WLOC_CD");
				sPNT_CD				= ydDaoUtils.paraRecChkNull(inDto[i], "H_PNT_CD");
				sYD_GP				= ydDaoUtils.paraRecChkNull(inDto[i], "H_YD_GP");
				
				ydUtils.putLog(szSessionName, szMethodName, "sSTL_NO-->" + sSTL_NO, YdConstant.DEBUG);
				recPara		= JDTORecordFactory.getInstance().create();
				recPara.setField("V_STL_NO"	, sSTL_NO);
				recPara.setField("V_YD_GP"	, sYD_GP);
				
				outRecSet 	= JDTORecordFactory.getInstance().createRecordSet("");
				/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDAO.getYdStockYdCoilCommLyr*/
				outRecSet  	= dao.getYdStockYdCoilCommLyr(recPara);
				if(outRecSet == null || outRecSet.size() <= 0 ) {
					szMsg = "해당 재료정보가 없습니다.--> 저장위치를 먼저 변경해주세요.. 재료번호:" + sSTL_NO;
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
				}
				if( outRecSet.size() > 1 ) {
					szMsg = "저장품 조회 시 오류발생(작업예약확인) 재료번호:" + sSTL_NO;
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
				}

				outRecSet.first();
				recTemp = outRecSet.getRecord();
				sSTL_NO 		= ydDaoUtils.paraRecChkNull(recTemp, "STL_NO");
				sYD_STK_COL_GP 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_COL_GP");
				sYD_STK_BED_NO 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_NO");
				sYD_STK_LYR_NO 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_NO");
				sCRN_SCH_YN 	= ydDaoUtils.paraRecChkNull(recTemp, "CRN_SCH_YN");
				sWRKBOOK_YN 	= ydDaoUtils.paraRecChkNull(recTemp, "WRKBOOK_YN");
				sSPOS_WLOC_CD	= ydDaoUtils.paraRecChkNull(recTemp, "SPOS_WLOC_CD");
				sARR_WLOC_CD 	= ydDaoUtils.paraRecChkNull(recTemp, "ARR_WLOC_CD");

				if(i == 0) {
					sFIRST_SPOS_WLOC_CD = sSPOS_WLOC_CD;	
					sFIRST_ARR_WLOC_CD 	= sARR_WLOC_CD;
				} else {
					if( !sFIRST_SPOS_WLOC_CD.equals(sSPOS_WLOC_CD) ) {
						szMsg = "상차지가 서로 틀립니다. 확인하세요:" + sSTL_NO;
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, szMsg);	
						return outRecord;
					}		
					if( !sFIRST_ARR_WLOC_CD.equals(sARR_WLOC_CD) ) {
						szMsg = "하차지가 서로 틀립니다. 확인하세요:" + sSTL_NO;
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, szMsg);	
						return outRecord;
					}		
					
				}	
				if( sCRN_SCH_YN.equals("Y") ) {
					szMsg = "크레인 스케쥴 편성되어 있습니다. 삭제후 처리 하세요.  재료번호:" + sSTL_NO;
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
				}
				
				if( sWRKBOOK_YN.equals("Y") ) {
					szMsg = "작업예약이 편성되어 있습니다. 삭제후 처리 하세요.  재료번호:" + sSTL_NO;
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;
				}
				
				if(!sYD_STK_BED_NO.equals("PT")) {
					updPara1     	= JDTORecordFactory.getInstance().create();
					updPara1.setField("V_MODIFIER"		, szUserId);
					updPara1.setField("V_STL_NO"		, ""); // 재료번호
					updPara1.setField("V_YD_STK_LYR_MTL_STAT", "E"); // 적치단재료상태
					updPara1.setField("V_YD_STK_COL_GP"	, sYD_STK_COL_GP); // 야드적치열
					updPara1.setField("V_YD_STK_BED_NO"	, sYD_STK_BED_NO);  // 야드적치배드
					updPara1.setField("V_YD_STK_LYR_NO"	, sYD_STK_LYR_NO);  // 적치단               
					
					/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDAO.updStrlocModMgtFromAndTo*/
					intRtnVal = dao.updStrlocModMgtFromAndTo(updPara1);
					
					if(intRtnVal != 1){
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, "현재위치정보 수정에 실패 하였습니다");	
						return outRecord;
					}
				
					updPara2     	= JDTORecordFactory.getInstance().create();
					updPara2.setField("V_YD_STK_COL_GP"	, sTO_YD_STK_COL_GP);// 야드적치열
					updPara2.setField("V_YD_STK_BED_NO"	, sTO_YD_STK_BED_NO);// 야드적치배드
					updPara2.setField("V_YD_STK_LYR_NO"	, "001");// 적치단      
					
					// To 위치 최신정보로 조회
					outRecSet 	= JDTORecordFactory.getInstance().createRecordSet("");
					/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilYDDao.getStrlocModMgtStlInfo*/
					outRecSet = dao.getStrlocModMgtStlInfo(updPara2);
					
					if(outRecSet != null && outRecSet.size()>0){
						// To위치 수정 
						updPara2.setField("V_MODIFIER"				, szUserId);
						updPara2.setField("V_STL_NO"				, sSTL_NO); 			// 재료번호
						updPara2.setField("V_YD_STK_LYR_MTL_STAT"	, "C"); 				// 적치단재료상태
						updPara2.setField("V_YD_STK_COL_GP"			, sTO_YD_STK_COL_GP); 	// 야드적치열
						updPara2.setField("V_YD_STK_BED_NO"			, sTO_YD_STK_BED_NO);  	// 야드적치배드
						updPara2.setField("V_YD_STK_LYR_NO"			, "001");  // 적치단               
						/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDAO.updStrlocModMgtFromAndTo*/
						intRtnVal = dao.updStrlocModMgtFromAndTo(updPara2);
						
						if(intRtnVal != 1){
							outRecord.setField("RTN_CD" 	, "0");	
							outRecord.setField("RTN_MSG" 	, "변경위치정보 수정에 실패 하였습니다");	
							return outRecord;
							
						}
					}else{
						outRecord.setField("RTN_CD" 		, "0");	
						outRecord.setField("RTN_MSG" 		, "변경 위치정보 조회에 실패 하였습니다");	
						return outRecord;
					}				
					szMsg = "[JSP Session : "+szOperationName+"] 차량상차작업["+szYD_PREP_SCH_ID+"] 수정 성공 - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}	
/////////////////////////////////
				if(i == 0) {
					
			    	szYD_CAR_SCH_ID = ydCarSchDao.getYdCarschId();
			    	
			    	szMsg="차량스케줄ID 생성 시작 전";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			    	
			    	//차량스케줄INSERT 항목
					recOutTemp = JDTORecordFactory.getInstance().create();
					recOutTemp.setField("YD_CAR_SCH_ID"			, szYD_CAR_SCH_ID);
			    	recOutTemp.setField("REGISTER"				, szUserId);
			    	recOutTemp.setField("YD_EQP_WRK_STAT"		, "L");
			    	recOutTemp.setField("YD_EQP_ID"				, "XX"+ sTO_YD_STK_COL_GP.substring(2,6));
			    	recOutTemp.setField("TRN_EQP_CD"			, sTRN_EQP_CD);
			    	recOutTemp.setField("YD_CAR_USE_GP"			, "L");
			    	recOutTemp.setField("CAR_KIND"				, "PT");
			    	recOutTemp.setField("SPOS_WLOC_CD"			, sWLOC_CD);
			    	recOutTemp.setField("ARR_WLOC_CD"			, sARR_WLOC_CD);
			    	recOutTemp.setField("YD_BAYIN_WO_SEQ"		, YdConstant.YD_BAYIN_WO_SEQ_DEFAULT);
			    	recOutTemp.setField("YD_CARLD_WRK_BOOK_ID"	, "");
			    	recOutTemp.setField("YD_CAR_PROG_STAT"		, "5");//상차완료상태
			    	recOutTemp.setField("YD_CARLD_CMPL_DT", 	ydUtils.getCurDate("yyyyMMddHHmmss"));
			    	recOutTemp.setField("YD_CARLD_LEV_DT"		, YdUtils.getCurDate("yyyyMMddHHmmss"));//상차출발일시
			    	recOutTemp.setField("YD_CARLD_LEV_LOC"		, sTO_YD_STK_COL_GP);
			    	recOutTemp.setField("YD_CARLD_STOP_LOC"		, sTO_YD_STK_COL_GP);
					recOutTemp.setField("YD_PNT_CD1"			, sPNT_CD);
					recOutTemp.setField("YD_CARLD_PNT_WO_DT"	, YdUtils.getCurDate("yyyyMMddHHmmss"));
					recOutTemp.setField("DEL_YN",       		"N");
					 
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			    	
			    	//차량스케줄 등록
			    	intRtnVal = ydCarSchDao.insYdCarsch(recOutTemp);
		    		if(intRtnVal != 1){
						outRecord.setField("RTN_CD" 		, "0");	
						outRecord.setField("RTN_MSG" 		, "차량스케줄 등록에 실패 하였습니다");	
						return outRecord;
		    		}
		    		
		    		szMsg="차량스케줄ID 생성 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					recOutTemp = JDTORecordFactory.getInstance().create();
					recOutTemp.setField("YD_STK_COL_GP" 		, sTO_YD_STK_COL_GP);
					recOutTemp.setField("TRN_EQP_CD"        	, sTRN_EQP_CD);
					recOutTemp.setField("YD_CAR_USE_GP"     	, "L");
					recOutTemp.setField("YD_STK_COL_ACT_STAT"	, "L");
					recOutTemp.setField("MODIFIER"				, szUserId);
					intRtnVal = ydStkColDao.updYdStkcol(recOutTemp, 0);
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

				recOutTemp = JDTORecordFactory.getInstance().create();
				recOutTemp.setField("YD_CAR_SCH_ID"		, szYD_CAR_SCH_ID);
				recOutTemp.setField("STL_NO"			, sSTL_NO); 
				recOutTemp.setField("YD_STK_BED_NO"		, sTO_YD_STK_BED_NO);
				recOutTemp.setField("REGISTER"			, szUserId);
				recOutTemp.setField("YD_STK_LYR_NO"		, "001") ;
				recOutTemp.setField("DEL_YN"			, "N");
				intRtnVal = ydCarftmvmtlDao.insYdCarftmvmtl(recOutTemp);
	    		if(intRtnVal != 1) {
					outRecord.setField("RTN_CD" 		, "0");	
					outRecord.setField("RTN_MSG" 		, "차량스케줄 재료등록에 실패 하였습니다");	
					return outRecord;
	    		}
			}
			
			szMsg = "[JSP Session : "+szOperationName+"] 차량상차작업  수정 성공 - 메소드 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			outRecord.setField("RTN_CD" 		, "1");	
			outRecord.setField("RTN_MSG" 		, szMsg);	
			outRecord.setField("YD_CAR_SCH_ID" 	, szYD_CAR_SCH_ID);	
			return outRecord;			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
	}//end of uptYdPrepSch	
	/**
	 * 크레인 관리 조회 (화면:크레인작업관리)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdCrnWoWorkMgt(JDTORecord inDto) throws DAOException {
		int         intRtnVal    = 0;
		String      szMsg        = "";
		YdCrnSchDao ydCrnschDao  = new YdCrnSchDao();
		String      szMethodName = "getCoilYdCrnWoWorkMgt";
		
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");

		JDTORecord recPara = JDTORecordFactory.getInstance().create();
			
		try {
			szMsg = "JSP-SESSION [크레인작업관리 조회 - 화면:크레인작업관리] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			recPara.setField("YD_GP", 		inDto.getField("YD_GP"));		
			recPara.setField("YD_BAY_GP", 	inDto.getField("YD_BAY_GP"));		
			recPara.setField("YD_SCH_CD", 	inDto.getField("YD_SCH_CD"));							
			recPara.setField("YD_EQP_ID", 	inDto.getField("YD_EQP_ID"));
			recPara.setField("PAGE_NO",   	inDto.getField("PAGE_NO"));
			recPara.setField("ROWCOUNT",    inDto.getField("ROWCOUNT"));
			/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getCoilYdCrnWoWorkMgt*/
			intRtnVal = ydCrnschDao.getYdCrnsch(recPara, outRecSet, 309);
							
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
						
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szMsg = "JSP-SESSION [크레인작업관리 조회 - 화면:크레인작업관리] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		return outRecSet;
	}	// end of getCoilYdCrnWoWorkMgt
	/**
	 * 크레인 관리 조회 (화면:크레인작업관리)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdCrnWoWorkMgtDtl(JDTORecord inDto) throws DAOException {
		int         intRtnVal    = 0;
		String      szMsg        = "";
		YdCrnSchDao ydCrnschDao  = new YdCrnSchDao();
		String      szMethodName = "getCoilYdCrnWoWorkMgtDtl";
		
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");

		JDTORecord recPara = JDTORecordFactory.getInstance().create();
			
		try {
			szMsg = "JSP-SESSION [크레인작업관리 조회 - 화면:크레인작업관리] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			recPara.setField("YD_SCH_CD", 		inDto.getField("PARA_YD_SCH_CD"));							
			recPara.setField("YD_SCH_PRIOR", 	inDto.getField("PARA_YD_SCH_PRIOR"));
			recPara.setField("PAGE_NO",   		inDto.getField("PAGE_NO"));
			recPara.setField("ROWCOUNT",    	inDto.getField("ROWCOUNT"));
			/*com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getCoilYdCrnWoWorkMgtDtl*/
			intRtnVal = ydCrnschDao.getYdCrnsch(recPara, outRecSet, 311);
							
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
						
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szMsg = "JSP-SESSION [크레인작업관리 조회 - 화면:크레인작업관리] 끝";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
		return outRecSet;
	}	// end of getCoilYdCrnWoWorkMgtDtl

	
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
	public JDTORecord crnChgSchPriorCoil(JDTORecord [] recMsg) throws DAOException {
		JDTORecord recPara = null;
		JDTORecord recTemp = null;
		int intSchPrior = 0;
		boolean bool = false;
		JDTORecord 	outRecord 			= JDTORecordFactory.getInstance().create(); // 
	
		String szMethodName="crnChgSchPriorCoil";		
		String szLogMsg = "";
		String szMsg    = "";
		
		
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
					szMsg = "해당정보는 대기 상태가 아닙니다.";
					outRecord.setField("RTN_CD" 		, "0");	
					outRecord.setField("RTN_MSG" 		, szMsg);	
					return outRecord;			
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

			
			
			szLogMsg = "JSP-SESSION [크레인 우선 순위 변경] 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	

			outRecord.setField("RTN_CD" 		, "1");	
			return outRecord;			

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
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
	public JDTORecord proccrnChgSchPriorCoilRecord(JDTORecord recMsg) throws DAOException {
		JDTORecord recPara = null;
		JDTORecord recTemp = null;
		int intSchPrior = 0;
		boolean bool = false;
		JDTORecord 	outRecord 			= JDTORecordFactory.getInstance().create(); // 
	
		String szMethodName="crnChgSchPriorCoil";		
		String szLogMsg = "";
		String szMsg    = "";
		
		
    	JDTORecordSet rsrstDataSch =  JDTORecordFactory.getInstance().createRecordSet("YD");
    	
    	int intGp = 0;

    	YdCrnSchDao  ydCrnSchDao  = new YdCrnSchDao ();
    	YdWrkbookDao ydWrkbookDao = new YdWrkbookDao();
    	
		try{
			szLogMsg = "JSP-SESSION [크레인 우선 순위 변경]시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	
			
			
			//for(int x=0 ; x < recMsg.size() ;x++){
				recPara = JDTORecordFactory.getInstance().create();
	
				// 1.  작업예약 ID ,크레인 ID , 입력받은 스케줄 우선순위
				recPara.setField("YD_WBOOK_ID" 	, recMsg.getField("YD_WBOOK_ID"));
				recPara.setField("YD_SCH_PRIOR" , recMsg.getField("YD_SCH_PRIOR"));
				intSchPrior = recMsg.getFieldInt("YD_SCH_PRIOR");	
				
				// 2. 작업 예약 정보 변경
				ydWrkbookDao.updYdWrkbook(recPara, 0);
				
				// 3. 작업예약에 편성된 스케줄정보 조회	
				
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID" 	, recMsg.getField("YD_WBOOK_ID"));
				recPara.setField("YD_WRK_PROG_STAT" ,"W");					
				
				intGp = ydCrnSchDao.getYdCrnsch(recPara, rsrstDataSch, 23);
				
				if (intGp <1 ){					
					szMsg = "해당정보는 대기 상태가 아닙니다.";
					outRecord.setField("RTN_CD" 		, "0");	
					outRecord.setField("RTN_MSG" 		, szMsg);	
					return outRecord;			
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
			//}	

			
			
			szLogMsg = "JSP-SESSION [크레인 우선 순위 변경] 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);	

			outRecord.setField("RTN_CD" 		, "1");	
			return outRecord;			

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		
	}
	
	
	/**
	 * 야드관리 > C열연 코일소재야드 > 야드현황관리 > 재료진도별재공현황  (저장물품목록)
	 * @ejb.interface-method
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @작성자 :박지열
	 * @작성일 : 2010.10.05
	 */
	public GridData getMtlProgStlList(GridData inDto) throws DAOException {
		
		JDTORecordSet 	retRdSet 	= JDTORecordFactory.getInstance().createRecordSet("YD");
		CoilJspDAO 		dao 		= new CoilJspDAO();
		GridData 		retGrid 	= new GridData();
		JDTORecord 		recPara		= null;
		try {
			
			recPara		= JDTORecordFactory.getInstance().create(); 
			
			recPara.setField("V_PROG_CD", inDto.getParam("prog_cd"));
			recPara.setField("V_YD_GP"	, inDto.getParam("YD_GP"));
			recPara.setField("V_PAGE_NO", inDto.getParam("page_no"));		
			recPara.setField("V_ROW_CNT", inDto.getParam("rowCount"));
			
			retRdSet = dao.getMtlProgStlList(recPara);
						
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
	 * 야드관리 > C열연 코일소재야드 > 산적LOT관리 > 반입현황조회  (A,B열연 반입카운트 조회 )
	 * @ejb.interface-method
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @작성자 :박지열
	 * @작성일 : 2010.10.11
	 */
	public GridData getCarryBayCnt(GridData inDto) throws DAOException {
		
		JDTORecordSet 	retRdSet 	= JDTORecordFactory.getInstance().createRecordSet("YD");
		CoilJspDAO 		dao 		= new CoilJspDAO();
		GridData 		retGrid 	= new GridData();
		JDTORecord 		recPara		= null;
		try {
			
			recPara		= JDTORecordFactory.getInstance().create(); 
			
			recPara.setField("V_YD_GP"	, inDto.getParam("YD_GP"));
			recPara.setField("V_DATE_FROM", StringHelper.replaceStr(inDto.getParam("DATE_FROM"), ".", ""));		
			recPara.setField("V_DATE_TO", StringHelper.replaceStr(inDto.getParam("DATE_TO"), ".", ""));	
			
			retRdSet = dao.getCarryBayCnt(recPara);
			
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
	 * 야드관리 > C열연 코일소재야드 > 산적LOT관리 > 반입현황조회  (대상재목록 조회 )
	 * @ejb.interface-method
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @작성자 :박지열
	 * @작성일 : 2010.10.11
	 */
	public GridData getCarryList(GridData inDto) throws DAOException {
		
		JDTORecordSet 	retRdSet 	= JDTORecordFactory.getInstance().createRecordSet("YD");
		CoilJspDAO 		dao 		= new CoilJspDAO();
		GridData 		retGrid 	= new GridData();
		JDTORecord 		recPara		= null;
		try {
			
			recPara		= JDTORecordFactory.getInstance().create(); 
			
			recPara.setField("V_YD_GP"	, inDto.getParam("YD_GP"));
			recPara.setField("V_DATE_FROM", StringHelper.replaceStr(inDto.getParam("DATE_FROM"), ".", ""));		
			recPara.setField("V_DATE_TO", StringHelper.replaceStr(inDto.getParam("DATE_TO"), ".", ""));	
			recPara.setField("V_YD_BAY_GP"	, inDto.getParam("YD_BAY_GP"));		
			recPara.setField("V_GUBUN"	, inDto.getParam("GUBUN"));
			recPara.setField("V_PAGE_CNT", inDto.getParam("page_no"));		
			recPara.setField("V_ROW_CNT", inDto.getParam("rowCount"));
			
			retRdSet = dao.getCarryList(recPara);
			
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
			recPara.setField("PARTY"		, yddatautil.setDataDefault(inDto.getField("YD_WRK_PARTY"),""));
			ydUtils.putLog(szSessionName,"\nform 에 있는 모든 변수값 : ", message, YdConstant.INFO);
			
			/*com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getCrnWrkWrStatNew*/
			nRet = ydWrkHistDao.getYdWrkHist(recPara, outRecSet, 301);
			
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
	 * 이상재현황
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public GridData getMtlErrorList(GridData inDto) throws DAOException {
		/*
		 */
		//JDTO변수 정의
		JDTORecord       recPara        = null;
		JDTORecordSet    outRecSet      = null;
		GridData 		 retGrid 	= new GridData();
		//DAO 변수 정의
		CoilJspDAO 		dao 			= new CoilJspDAO();
		//기본변수 정의
		String      szMsg        		= "";
		String      szMethodName		= "getMtlErrorList";
		String		szOperationName		= "이상재LIST";
		//로컬변수 정의
	    String      szSEARCH_LIST_GP	= "";
	    String      szYD_BAY_GP 		= "";
		String		szYD_GP				= "";
		int intRtnVal = 0;
		
		try {
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 : 파라미터 확인 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szYD_GP 				= inDto.getParam("YD_GP");
			szSEARCH_LIST_GP		= inDto.getParam("SEARCH_LIST_GP");
			szYD_BAY_GP				= inDto.getParam("YD_BAY_GP");
			
			outRecSet      = JDTORecordFactory.getInstance().createRecordSet("YD");
			recPara        = JDTORecordFactory.getInstance().create();
		    recPara.setField("V_YD_GP",     	szYD_GP);
		   
			if( szSEARCH_LIST_GP.equals("A")) {										
			    /*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDAO.getMtlErrorListA*/
				outRecSet = dao.getMtlErrorListA(recPara);     //1단 코일 없이 2단 적치가 되어 있는 코일
			}else if( szSEARCH_LIST_GP.equals("B")){									
				/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDAO.getMtlErrorListB*/
				outRecSet = dao.getMtlErrorListB(recPara);     //코일공통에만 존재하는 코일
			}else if( szSEARCH_LIST_GP.equals("C")){									
				/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDAO.getMtlErrorListC*/
				outRecSet = dao.getMtlErrorListC(recPara);     //저장위치만 존재하고 코일공통은 종료된 코일
			}else if( szSEARCH_LIST_GP.equals("D")){									
				/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDAO.getMtlErrorListD*/
				outRecSet = dao.getMtlErrorListD(recPara);     //3일전 예정분 코일 중 저장위치가 존재 안 하는 코일
			}else if( szSEARCH_LIST_GP.equals("E")){									
				/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDAO.getMtlErrorListE*/
				outRecSet = dao.getMtlErrorListE(recPara);     //코일번호가 2개이상 저장되어 있는 경우
			}
			
			if (outRecSet != null) {
				retGrid = CmUtil.genGridData(inDto , outRecSet);
				retGrid.addParam("ret", "0");
				retGrid.setStatus("true");
			} else{
				retGrid.addParam("ret", "-1");
				retGrid.setStatus("false");
			}
			
		
			szMsg = "[JSP Session : "+szOperationName+"] 조회 성공 : 대상재건수[" + intRtnVal + "]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return retGrid;
	}//end of getMtlErrorList
	
	
	/**
	 *  입출고 현황(C열연소재야드)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilTotYdInOutList(JDTORecord inDto) throws DAOException {
		
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		
		String      szMsg        = "";
		String      szMethodName = "getCoilTotYdInOutList";
		String	szOperationName = "입출고 현황(C열연소재야드)";		
		
		YdCarSchDao ydCarSchDao = new YdCarSchDao();
		
		int intRtnVal = 0;
		
		try {		
			
			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				    
			  recPara.setField("DATE_FROM",    		ydDaoUtils.paraRecChkNull(inDto, "DATE_FROM"));
			  recPara.setField("DATE_TO", 		    ydDaoUtils.paraRecChkNull(inDto, "DATE_TO"));
			  recPara.setField("YD_WRK_DUTY", 	    ydDaoUtils.paraRecChkNull(inDto, "YD_WRK_DUTY"));
			  
			  //com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getCoilTotYdInOutList
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, outRecSet, 404);
						
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
		
		
		
		
		
		
	}//end of getCoilTotYdInOutList
	
	/**
	 *  입출고 현황(B열연소재야드)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilTotYdInOutListB(JDTORecord inDto) throws DAOException {
		
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		
		String      szMsg        = "";
		String      szMethodName = "getCoilTotYdInOutListB";
		String	szOperationName = "입출고 현황(B열연소재야드)";		
		
		YdCarSchDao ydCarSchDao = new YdCarSchDao();
		
		int intRtnVal = 0;
		
		try {		
			
			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				    
			  recPara.setField("DATE_FROM",    		ydDaoUtils.paraRecChkNull(inDto, "DATE_FROM"));
			  recPara.setField("DATE_TO", 		    ydDaoUtils.paraRecChkNull(inDto, "DATE_TO"));
			  recPara.setField("YD_WRK_DUTY", 	    ydDaoUtils.paraRecChkNull(inDto, "YD_WRK_DUTY"));
			  
			  //com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getCoilTotYdInOutListAB
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, outRecSet, 416);
						
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
		
		
		
		
		
		
	}//end of getCoilTotYdInOutListB
	
	/**
	 *  입출고 현황(C열연소재야드)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilTotYdInOutListDong(JDTORecord inDto) throws DAOException {
		
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		
		String      szMsg        = "";
		String      szMethodName = "getCoilTotYdInOutListDong";
		String	szOperationName = "입출고 현황(C열연소재야드)";		
		
		YdCarSchDao ydCarSchDao = new YdCarSchDao();
		
		int intRtnVal = 0;
		
		try {		
			
			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				    
			  recPara.setField("DATE_FROM",    		ydDaoUtils.paraRecChkNull(inDto, "DATE_FROM"));
			  recPara.setField("DATE_TO", 		    ydDaoUtils.paraRecChkNull(inDto, "DATE_TO"));
			  recPara.setField("YD_WRK_DUTY", 	    ydDaoUtils.paraRecChkNull(inDto, "YD_WRK_DUTY"));
			  
			  //com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getCoilTotYdInOutListDong
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, outRecSet, 405);
						
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
		
		
		
		
		
		
	}//end of getCoilTotYdInOutListDong
	
	/**
	 *  입출고 현황(B열연소재야드)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilTotYdInOutListDongB(JDTORecord inDto) throws DAOException {
		
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		
		String      szMsg        = "";
		String      szMethodName = "getCoilTotYdInOutListDongB";
		String	szOperationName = "입출고 현황(B열연소재야드)";		
		
		YdCarSchDao ydCarSchDao = new YdCarSchDao();
		
		int intRtnVal = 0;
		
		try {		
			
			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				    
			  recPara.setField("DATE_FROM",    		ydDaoUtils.paraRecChkNull(inDto, "DATE_FROM"));
			  recPara.setField("DATE_TO", 		    ydDaoUtils.paraRecChkNull(inDto, "DATE_TO"));
			  recPara.setField("YD_WRK_DUTY", 	    ydDaoUtils.paraRecChkNull(inDto, "YD_WRK_DUTY"));
			  
			  //com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getCoilTotYdInOutListDongB
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, outRecSet, 417);
						
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
		
		
		
		
		
		
	}//end of getCoilTotYdInOutListDongB
	/**
	 * 저장위치변경관리 (YD저장위치수정)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord updYDStrlocModMgtPDA(JDTORecord recMsg ) throws DAOException {
		int           intRtnVal     = 0;
		JDTORecord    tmpPara       = JDTORecordFactory.getInstance().create();
		
		JDTORecord    updPara1      = JDTORecordFactory.getInstance().create();
		JDTORecord    updPara2      = JDTORecordFactory.getInstance().create();
		JDTORecord    updPara3      = JDTORecordFactory.getInstance().create();
		JDTORecord    updPara4      = JDTORecordFactory.getInstance().create();
		JDTORecord    inRecord      = JDTORecordFactory.getInstance().create();
		JDTORecord    inRecord1     = JDTORecordFactory.getInstance().create();
		JDTORecord    outRecord1      = JDTORecordFactory.getInstance().create();
		
		String szMethodName = "updYDStrlocModMgt";
		GridData 		rtnGrd 		= new GridData();
		CoilJspDAO 		dao 		= new CoilJspDAO();
		YdStkLyrDao ydStkLyrDao    		= new YdStkLyrDao();
		YdCarSchDao ydCarSchDao 			= new YdCarSchDao();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String sFR_YD_STK_COL_GP 	= ""; 
		String sFR_YD_STK_BED_NO 	= ""; 
		String sFR_YD_STK_LYR_NO 	= ""; 

		String sTO_YD_STK_COL_GP 	= ""; 
		String sTO_YD_STK_BED_NO 	= ""; 
		String sTO_YD_STK_LYR_NO 	= ""; 

		String sTO_STKLOC 			= ""; 
		String sYD_USER_ID 			= "";
		String sSTL_NO              = "";
		String sSND_FLAG			= "";
		String sSND_COMM_FLAG		= "Y";
		String sYD_AIM_RT_GP		= "";
		String sYD_AIM_YD_GP		= "";
		String sYD_AIM_BAY_GP		= "";
		String sMTL_UPD_FLAG		= "";
		String sLOC_UPD_FLAG		= "";
		String szMsg	="";
		String sRTN_CD ="";
		String sRTN_MSG ="";
		String sCURR_PROG_CD ="";
		
		CoilYdToLocDcsnUtil ydToLocDcsnUtil = new CoilYdToLocDcsnUtil();
		
		JDTORecord outRecord = JDTORecordFactory.getInstance().create(); // 
		JDTORecord inRecord2  				= JDTORecordFactory.getInstance().create();			
//SJH		
		try {
			
			sFR_YD_STK_COL_GP	= ydDaoUtils.paraRecChkNull(recMsg, "YD_STK_COL_GP"); 
			sFR_YD_STK_BED_NO	= ydDaoUtils.paraRecChkNull(recMsg, "YD_STK_BED_NO"); 
			sFR_YD_STK_LYR_NO	= ydDaoUtils.paraRecChkNull(recMsg, "YD_STK_LYR_NO");
			
			
			sTO_YD_STK_COL_GP	= ydDaoUtils.paraRecChkNull(recMsg, "UPD_STK_POS_2"); 
			sTO_YD_STK_BED_NO	= ydDaoUtils.paraRecChkNull(recMsg, "UPD_STK_BED_NO"); 
			sTO_YD_STK_LYR_NO	= ydDaoUtils.paraRecChkNull(recMsg, "UPD_STK_LYR_NO");
			sTO_STKLOC 			= ydDaoUtils.paraRecChkNull(recMsg, "UPD_STK_POS");

			sYD_USER_ID			= ydDaoUtils.paraRecChkNull(recMsg, "YD_USER_ID"); 
			
			sSTL_NO				= ydDaoUtils.paraRecChkNull(recMsg, "STL_NO"); // 

			sYD_AIM_RT_GP		= ydDaoUtils.paraRecChkNull(recMsg, "YD_AIM_RT_GP");// 
			sYD_AIM_YD_GP		= ydDaoUtils.paraRecChkNull(recMsg, "YD_AIM_YD_GP"); // 
			sYD_AIM_BAY_GP		= ydDaoUtils.paraRecChkNull(recMsg, "YD_AIM_BAY_GP"); // 
			
			sMTL_UPD_FLAG		= ydDaoUtils.paraRecChkNull(recMsg, "MTL_UPD_FLAG"); // 재료 속성 변경
			sLOC_UPD_FLAG		= ydDaoUtils.paraRecChkNull(recMsg, "LOC_UPD_FLAG"); // 저장위치변경
			sCURR_PROG_CD		= ydDaoUtils.paraRecChkNull(recMsg, "CURR_PROG_CD"); //
			
			tmpPara.setField("V_YD_STK_BED_NO", sTO_STKLOC); 
			tmpPara.setField("V_YD_STK_LYR_NO", sTO_YD_STK_LYR_NO); 
			
			// 1) 수정위치의 재료번호 등록 여부를 조회 -----------------------------------------------------
			/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilYDDao.getStrlocModMgtChk*/
			outRecSet = dao.getStrlocModMgtChk(tmpPara);
			
			if(outRecSet != null && outRecSet.size()>0){
				// 이미 저장위치에 코일이 존재 한다면 리턴 :count
				if(!outRecSet.getRecord(0).getField("YD_STK_LYR_MTL_STAT").equals("E")){
					if(!outRecSet.getRecord(0).getField("STL_NO").equals(sSTL_NO)){
							
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, "선택 하신 수정위치는 이미 코일이 존재하거나 금지 bed 입니다.");	
						return outRecord;
					} else {
						sSND_COMM_FLAG			= "N";
					}
					
				}
			}	

			//=============================================================================================
			// 레이어 정보 수정 
			//=============================================================================================
			//2) 현재위치의 재료번호를 조회후  Null로  수정 --------------------------------------------------------
			/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdStkPosInfo_PIDEV*/
			JDTORecordSet   outRecSet1   = JDTORecordFactory.getInstance().createRecordSet("YD");
			inRecord1 = JDTORecordFactory.getInstance().create();
			inRecord1.setField("STL_NO",   sSTL_NO);
			intRtnVal = ydStkLyrDao.getYdStklyr(inRecord1, outRecSet1, 24);
			if (intRtnVal > 0) {
				//적치되어 있는 정보 삭제처리
				outRecSet1.first();
				outRecord1 = outRecSet1.getRecord();
				//적치단 재료상태가 적치 가능이면 재료 등록
				//적치단 테이블 업데이트
				//적치열구분 = 설비ID
				sFR_YD_STK_COL_GP = ydDaoUtils.paraRecChkNull(outRecord1, "YD_STK_COL_GP");
				sFR_YD_STK_BED_NO = ydDaoUtils.paraRecChkNull(outRecord1, "YD_STK_BED_NO");
				sFR_YD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(outRecord1, "YD_STK_LYR_NO");
			}
			
			updPara1.setField("V_YD_STK_COL_GP", sFR_YD_STK_COL_GP);// 야드적치열
			updPara1.setField("V_YD_STK_BED_NO", sFR_YD_STK_BED_NO);// 야드적치배드
			updPara1.setField("V_YD_STK_LYR_NO", sFR_YD_STK_LYR_NO);// 적치단      
			
			

				if(sSND_COMM_FLAG.equals("Y")){
					// from 위치 최신정보로 조회
					/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilYDDao.getStrlocModMgtStlInfo*/
					outRecSet = dao.getStrlocModMgtStlInfo(updPara1);
					
					if(outRecSet != null && outRecSet.size()>0){
						// from 위치 수정 
						updPara1.setField("V_MODIFIER", sYD_USER_ID);
						updPara1.setField("V_STL_NO", ""); // 재료번호
						updPara1.setField("V_YD_STK_LYR_MTL_STAT", "E"); // 적치단재료상태
						updPara1.setField("V_YD_STK_COL_GP", sFR_YD_STK_COL_GP); // 야드적치열
						updPara1.setField("V_YD_STK_BED_NO", sFR_YD_STK_BED_NO);  // 야드적치배드
						updPara1.setField("V_YD_STK_LYR_NO", sFR_YD_STK_LYR_NO);  // 적치단               
						
						/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDAO.updStrlocModMgtFromAndTo*/
						intRtnVal = dao.updStrlocModMgtFromAndTo(updPara1);
						
						if(intRtnVal != 1){
							// TODO.. 오류처리
							outRecord.setField("RTN_CD" 	, "0");	
							outRecord.setField("RTN_MSG" 	, "현재위치정보 수정에 실패 하였습니다");	
							return outRecord;
							
						}
					}
				}
			if(sSND_COMM_FLAG.equals("Y")){
				//3) 수정위치의 재료번호를 등록(UPDATE) ----------------------------------------------------------------
				updPara2.setField("V_YD_STK_COL_GP", sTO_YD_STK_COL_GP);// 야드적치열
				updPara2.setField("V_YD_STK_BED_NO", sTO_YD_STK_BED_NO);// 야드적치배드
				updPara2.setField("V_YD_STK_LYR_NO", sTO_YD_STK_LYR_NO);// 적치단      
				
				
				// To 위치 최신정보로 조회
				/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilYDDao.getStrlocModMgtStlInfo*/
				outRecSet = dao.getStrlocModMgtStlInfo(updPara2);
				
				if(outRecSet != null && outRecSet.size()>0){
					
					if(sTO_YD_STK_COL_GP.substring(0, 1).equals("J")){
						//1단 check
						inRecord = JDTORecordFactory.getInstance().create();
						inRecord.setField("YD_STK_COL_GP" 	, sTO_YD_STK_COL_GP);	
						inRecord.setField("YD_STK_BED_NO" 	, sTO_YD_STK_BED_NO);	
						inRecord.setField("YD_STK_LYR_NO" 	, sTO_YD_STK_LYR_NO);
						//inRecord.setField("BRE_CHK1_SKIP_YN", "N");  //폭간섭 여부 SKIP
						ydUtils.putLog(szSessionName, szMethodName, "CHK???"+sTO_YD_STK_COL_GP.substring(1, 2), YdConstant.DEBUG);
						if(sTO_YD_STK_COL_GP.substring(2, 4).equals("80")
								||sTO_YD_STK_COL_GP.substring(2, 4).equals("70")
								|| sTO_YD_STK_COL_GP.substring(1, 2).equals("L")
								|| sTO_YD_STK_COL_GP.substring(1, 2).equals("M")
								|| sTO_YD_STK_COL_GP.substring(1, 2).equals("B")
								|| sTO_YD_STK_COL_GP.substring(1, 2).equals("A")
							) {  // 가상 bed임
							
						} else {
	
							outRecord1 = ydToLocDcsnUtil.CoilGdsLyrBaseCheck(sSTL_NO, inRecord);
							 sRTN_CD	= StringHelper.evl(outRecord1.getFieldString("RTN_CD"), "0");
							 sRTN_MSG	= StringHelper.evl(outRecord1.getFieldString("RTN_MSG"), "");
							
							if (!("1".equals(sRTN_CD))) {
								outRecord.setField("RTN_CD" 	, "0");	
								outRecord.setField("RTN_MSG" 	, sRTN_MSG);	
								return outRecord;
							}
						}
					}	

					
					// To위치 수정 
					updPara2.setField("V_MODIFIER", sYD_USER_ID);
					updPara2.setField("V_STL_NO", sSTL_NO); // 재료번호
					updPara2.setField("V_YD_STK_LYR_MTL_STAT", "C"); // 적치단재료상태
					updPara2.setField("V_YD_STK_COL_GP", sTO_YD_STK_COL_GP); // 야드적치열
					updPara2.setField("V_YD_STK_BED_NO", sTO_YD_STK_BED_NO);  // 야드적치배드
					updPara2.setField("V_YD_STK_LYR_NO", sTO_YD_STK_LYR_NO);  // 적치단               
	
					intRtnVal = dao.updStrlocModMgtFromAndTo(updPara2);
					
					if(intRtnVal != 1){
						// TODO.. 수정 오류처리
						outRecord.setField("RTN_CD" 	, "0");	
						outRecord.setField("RTN_MSG" 	, "변경위치정보 수정에 실패 하였습니다");	
						return outRecord;
						
					}
				}else{
					// TODO.. 조회된 데이터 없을시 오류처리 
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, "변경 위치정보 조회에 실패 하였습니다");	
					return outRecord;
				}
			}	
			
			//=============================================================================================
			// 야드저장품 수정 
			//=============================================================================================
			
			updPara3.setField("V_YD_STK_COL_GP"	, sTO_YD_STK_COL_GP);	// 야드적치열구분
			updPara3.setField("V_YD_STK_BED_NO"	, sTO_YD_STK_BED_NO);	// 야드적치배드
			updPara3.setField("V_YD_STK_LYR_NO"	, sTO_YD_STK_LYR_NO);	// 야드단
			updPara3.setField("V_MODIFIER"		, sYD_USER_ID);	// 수정자
			updPara3.setField("V_STL_NO"		, sSTL_NO);	// 코일번호
			updPara3.setField("V_YD_AIM_YD_GP"	, sYD_AIM_YD_GP);	// 목표야드
			updPara3.setField("V_YD_AIM_BAY_GP" , sYD_AIM_BAY_GP);	// 목적동
			updPara3.setField("V_YD_AIM_RT_GP"	, sYD_AIM_RT_GP);	// 목표행선
			
			ydUtils.putLog(szSessionName, szMethodName, "sYD_AIM_YD_GP:"+ sYD_AIM_YD_GP, YdConstant.DEBUG);
			ydUtils.putLog(szSessionName, szMethodName, "sYD_AIM_BAY_GP:"+ sYD_AIM_BAY_GP, YdConstant.DEBUG);


			
			/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDAO.updStrlocModMgtStock*/
			intRtnVal = dao.updStrlocModMgtStock(updPara3);
			
			if(intRtnVal != 1){
				// TODO.. 수정 오류처리
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, "야드저장품 정보 수정에 실패 하였습니다");	
				return outRecord;

			}
			
			
			if(sSND_COMM_FLAG.equals("Y")){
				
				//저장위치 변경이력 등록
				JDTORecord recCrnStock = null;
				YdWrkHistDao ydWrkHistDao = new YdWrkHistDao();			
				 
				recCrnStock = JDTORecordFactory.getInstance().create(); 
				recCrnStock.setField("STL_NO"			, sSTL_NO);
				recCrnStock.setField("MODIFIER"			, sYD_USER_ID);
				recCrnStock.setField("YD_UP_WR_LOC"		, sFR_YD_STK_COL_GP+sFR_YD_STK_BED_NO);
				recCrnStock.setField("YD_UP_WR_LAYER"	, sFR_YD_STK_LYR_NO); //FROM 단
				recCrnStock.setField("YD_DN_WR_LOC"		, sTO_YD_STK_COL_GP+sTO_YD_STK_BED_NO);
				recCrnStock.setField("YD_DN_WR_LAYER"	, sTO_YD_STK_LYR_NO); //TO 단
				recCrnStock.setField("YD_GP"			, sTO_YD_STK_COL_GP.substring(0, 1));
				recCrnStock.setField("SPEC_ABBSYM"		, sSND_FLAG);
				recCrnStock.setField("YD_GP"			, sTO_YD_STK_COL_GP.substring(0, 1));
				
				recCrnStock.setField("YD_SCH_CD"		, sTO_YD_STK_COL_GP.substring(0, 2)+"YD01MM");
				//------------------------------------------------------------------------------------
				
				// 이력테이블에 INSERT
				intRtnVal = ydWrkHistDao.insYdCoilWrkHist(recCrnStock);
				
				if(intRtnVal<=0) {
					szMsg = "재료번호(" + sSTL_NO + ")에 대한 INSERT가 실패하였습니다.";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szMsg);	
					return outRecord;	
				}
			}	
			if(sSND_COMM_FLAG.equals("Y")){
				
				//코일 공통  정보 수정				
				inRecord = JDTORecordFactory.getInstance().create();
				inRecord.setField("UPD_STK_POS_2"	, sTO_YD_STK_COL_GP);
				inRecord.setField("UPD_STK_BED_NO"	, sTO_YD_STK_BED_NO);
				inRecord.setField("UPD_STK_LYR_NO"	, sTO_YD_STK_LYR_NO);
				inRecord.setField("YD_USER_ID"		, sYD_USER_ID);
				inRecord.setField("STL_NO"			, sSTL_NO);
				
				
				EJBConnector ejbConn = new EJBConnector("default", "CoilJspSeEJB", this);			
				JDTORecord outRecord2 	= (JDTORecord)ejbConn.trx("updCOMMStrlocModMgt", new Class[] { JDTORecord.class }, new Object[] { inRecord });
				sRTN_CD		= StringHelper.evl(outRecord2.getFieldString("RTN_CD"), "0");
				sRTN_MSG	= StringHelper.evl(outRecord2.getFieldString("RTN_MSG"), "");
		
				if ("0".equals(sRTN_CD)) {
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, sRTN_MSG);	
					return outRecord;		
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
				if(sTO_YD_STK_COL_GP.substring(0, 1).equals("J")){
					//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
					//코일제품이적작업실적
					JDTORecord tcRecordDM = null;
					tcRecordDM = JDTORecordFactory.getInstance().create(); 
					tcRecordDM.setField("GOODS_NO"		, sSTL_NO);
//					tcRecordDM.setField("BEFO_STORE_LOC", sFR_YD_STK_COL_GP+sFR_YD_STK_BED_NO+sFR_YD_STK_LYR_NO);
//					tcRecordDM.setField("TO_STORE_LOC"	, sTO_YD_STK_COL_GP+sTO_YD_STK_BED_NO+sTO_YD_STK_LYR_NO);
	
					tcRecordDM.setField("BEFO_STORE_LOC",ydUtils.ParsingStkColGpBedLyr(sFR_YD_STK_COL_GP, sFR_YD_STK_BED_NO, sFR_YD_STK_LYR_NO));
					tcRecordDM.setField("TO_STORE_LOC",ydUtils.ParsingStkColGpBedLyr(sTO_YD_STK_COL_GP, sTO_YD_STK_BED_NO, sTO_YD_STK_LYR_NO));
					
					//인터페이스 전문 호출
					ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
					ejbConn.trx("getYDDMR004",new Class[]{JDTORecord.class}, new Object[]{tcRecordDM}); 
	
					szMsg = "코일야드에서 출하L3로 응답전문 [YDDMR004] 전송완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				}
				
				
				//반납 실적처리 등록 (backup)
				if(sTO_YD_STK_COL_GP.substring(0, 1).equals("H") && sCURR_PROG_CD.equals("J")){
					//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

					JDTORecord getRecord = null;
					getRecord = JDTORecordFactory.getInstance().create(); 
					getRecord.setField("YD_MTL_ITEM"	, "CM");
					getRecord.setField("STL_NO"			, sSTL_NO);
					 //트렌젝션 분리
        	        ejbConn = new EJBConnector("default", "CoilCraneUdHdSeEJB", this);
        	        ejbConn.trx("Y5SetProgCodeCoil", new Class[] { JDTORecord.class }, new Object[] { getRecord });
        	        
		            
					//반납실적정보 전송 YDDMR034
					JDTORecord tcRecordDM = null;
					tcRecordDM = JDTORecordFactory.getInstance().create(); 				        	
					tcRecordDM.setField("MSG_ID",        "YDDMR034");			//반납확정정보:전문코드
					tcRecordDM.setField("STL_NO",     	 sSTL_NO);				//제품번호
	    			
	    			ydDelegate.sendMsg(tcRecordDM);
	    			
					szMsg="출하관리 코일반납작업실적전송 송신 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
						
	

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
				}
			}

			// 정상 처리 후
			outRecord.setField("SND_COMM_FLAG" 	, sSND_COMM_FLAG);	
			outRecord.setField("RTN_CD" 		, "1");	
			return outRecord;
			
			
		} catch (Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} 
	}	// end of updStrlocModMgtPDA
	
	
	
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
	
///////////////////////////////////////////////////////////////////////////////	
//C증설		
	
	/**
	 * 소재 SPM/HFL입측관리
     * 송
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdHrTrackingNew(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getCoilYdHrTrackingNew";

		String	szYDGp = null;
		String szEqpGp = null;
		String szOperationName = "코일소재야드 입고Tracking 조회";
		
		
		//DAO
		YdCrnWrkMtlDao ydCrnWrkMtlDao = new YdCrnWrkMtlDao();
		
		int intRtnVal = 0;
		
		try {
			
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			szEqpGp = ydDaoUtils.paraRecChkNull(inDto, "EQP_GP");
			szYDGp = ydDaoUtils.paraRecChkNull(inDto, "YD_GP");
			//------------------------------------------------------------------------------------
			// 코일 입고진행관리 조회 (2010.02.22) - 설비
			//------------------------------------------------------------------------------------
			
			if(!szEqpGp.equals("")){
					
				recPara         = JDTORecordFactory.getInstance().create();
				recPara.setField("EQP_CD", szEqpGp);
				recPara.setField("YD_GP", szYDGp);
				/*com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getCoilYdHrTrackingNew*/		
				intRtnVal = ydCrnWrkMtlDao.getYdCrnwrkmtl(recPara, outRecSet, 402);
				
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
			
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilYdHrTrackingNew
	

	/**
	 * 제품 지포장 입측관리
     * 송
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdHrTrackingGPack(JDTORecord inDto) throws DAOException {
		
		JPlateYdCommDAO ydCommDao = new JPlateYdCommDAO();
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getCoilYdHrTrackingGPack";

		String sQueryId = "";
		String	szYDGp = null;
		String szEqpGp = null;
		String szOperationName = "코일소재야드 입고Tracking 조회";
		
		
		//DAO
		YdCrnWrkMtlDao ydCrnWrkMtlDao = new YdCrnWrkMtlDao();
		
		int intRtnVal = 0;
		
		try {
			
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			szEqpGp = ydDaoUtils.paraRecChkNull(inDto, "EQP_GP");
			szYDGp = ydDaoUtils.paraRecChkNull(inDto, "YD_GP");
			//------------------------------------------------------------------------------------
			// 지포장 입고진행관리 조회 
			//------------------------------------------------------------------------------------
			
			if(!szEqpGp.equals("")){
					
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("EQP_CD", szEqpGp);
				recPara.setField("YD_GP", szYDGp);
		        sQueryId = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getCoilYdHrTrackingGPack";
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
			}
			
			szMsg = "[JSP Session : "+szOperationName+"] 메소드 끝 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getCoilYdHrTrackingGPack
	
	/**
	 * 소재 SPM/HFL입측관리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public JDTORecordSet getCoilYdHrTrackingBackUpNew(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getCoilYdHrTrackingBackUpNew";
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		
		int intRtnVal = 0;
		
		try {

			recPara.setField("EQP_CD", ydDaoUtils.paraRecChkNull(inDto,"EQP_GP"));
			

			/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdHrTrackingBackUpNew*/
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 393);
		
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
		return outRecSet;
	
	}	


	/**
	 * 제품 지포장 입측관리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public JDTORecordSet getCoilYdHrTrackingBackUpGPack(JDTORecord inDto) throws DAOException {
		
		JPlateYdCommDAO ydCommDao = new JPlateYdCommDAO();
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getCoilYdHrTrackingBackUpGPack";
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		
		String sQueryId = "";
		String	szYDGp = null;
		String szEqpGp = null;
		int intRtnVal = 0;
		
		try {

			szEqpGp = ydDaoUtils.paraRecChkNull(inDto, "EQP_GP");
			szYDGp = ydDaoUtils.paraRecChkNull(inDto, "YD_GP");

			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("EQP_CD", szEqpGp);
			recPara.setField("YD_GP", szYDGp);
	        sQueryId = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdHrTrackingBackUpGPack";
	        intRtnVal = ydCommDao.select(recPara, outRecSet, sQueryId);
		
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
		return outRecSet;
	
	}	
	

	/**
	 * 구코일 관리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public JDTORecordSet getMonitorChookCoilGdsYard(JDTORecord inDto) throws DAOException {
		
		JPlateYdCommDAO ydCommDao = new JPlateYdCommDAO();
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getMonitorChookCoilGdsYard";
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		
		String sQueryId = "";
		String	szYDGp = null;
		String szEqpGp = null;
		int intRtnVal = 0;
		
		try {

			szEqpGp = ydDaoUtils.paraRecChkNull(inDto, "EQP_GP");
			szYDGp = ydDaoUtils.paraRecChkNull(inDto, "YD_GP");

			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("EQP_CD", szEqpGp);
			recPara.setField("YD_GP", szYDGp);
	        sQueryId = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getMonitorChookCoilGdsYard";
	        intRtnVal = ydCommDao.select(recPara, outRecSet, sQueryId);
		
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
		return outRecSet;
	
	}	

	
	/**
	 * 소재 SPM/HFL입측관리 POP
	 * @ejb.interface-method
	 * @param GridData
	 * @return GridData
	 * @작성자 : 박지열
	 * @작성일 : 2010.06.03
	 */
	public GridData getSupplyInOrderListNew(GridData inDto) {
		GridData 		rtnGrd 		= new GridData();
		JDTORecordSet   outRecSet  	= null;
		
		CoilJspDAO 		dao 		= new CoilJspDAO();
		JDTORecord 		recPara		= null;
		
		try {
			
			
			ydUtils.putLog(szSessionName, "", inDto.getParam("EQP_GP"), YdConstant.DEBUG);
		
			// 파라미터 셋팅 
			recPara		= JDTORecordFactory.getInstance().create(); 			
			recPara.setField("V_LINE"		, inDto.getParam("EQP_GP"));	/*동구분*/
			recPara.setField("V_WORK_STAT"	, inDto.getParam("WORK_STAT"));	/*동구분*/
			recPara.setField("V_NEXT_PROC"	, "ALL");	/*동구분*/
			recPara.setField("V_PAGE_NO1"	, inDto.getParam("page_no"));
			recPara.setField("V_ROWCOUNT1"	, inDto.getParam("rowCount"));
			recPara.setField("V_PAGE_NO2"	, inDto.getParam("page_no"));
			recPara.setField("V_ROWCOUNT2"	, inDto.getParam("rowCount"));

			// DAO 호출
			/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilYDDao.getTrackingList*/
			outRecSet = dao.getTrackingList(recPara);
			
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
	}//end of getCoilYdStrlocModMgt
	
	/**
	 * 소재 SPM/HFL입측관리 POP
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return GridData
	 * @throws DAOException
	 * @throws JDTOException
	 */	
	public JDTORecordSet getcoilYdLineWrPpNew(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "";		
		String szMethodName = "getcoilYdLineWrPpNew";

		
		String sSTL_NO 		= "";
		String sWORK_STAT 	= null;
		String szOperationName = "코일제품야드 입고Tracking조회 팝업";
		
		
		//DAO
		YdCrnWrkMtlDao ydCrnWrkMtlDao = new YdCrnWrkMtlDao();
		
		int intRtnVal = 0;
		
		try {
			
			sSTL_NO	= ydDaoUtils.paraRecChkNull(inDto, "COIL_NO");

			recPara         = JDTORecordFactory.getInstance().create();
			recPara.setField("LINE"		, inDto.getField("EQP_GP"));	/*동구분*/
			recPara.setField("COIL_NO"	, sSTL_NO);
			recPara.setField("PAGE_NO"	, inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT"	, inDto.getField("ROWCOUNT"));

	
			/*com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getcoilYdLineWrPpNew*/
			intRtnVal = ydCrnWrkMtlDao.getYdCrnwrkmtl(recPara, outRecSet, 308);
			
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
	 * 야드관리 > 코일소재야드 > 크레인실적관리 > 스판단위이적등록 (작업가능 대차)
	 * @ejb.interface-method
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @작성자 :박지열
	 * @작성일 : 2010.04.22
	 */
	public GridData getToDongTcarUse(GridData inDto) throws DAOException {
		
		JDTORecordSet 	retRdSet 	= JDTORecordFactory.getInstance().createRecordSet("YD");
		CoilJspDAO 		dao 		= new CoilJspDAO();
		GridData 		retGrid 	= new GridData();
		JDTORecord 		recPara		= null;
		try {
			
			recPara		= JDTORecordFactory.getInstance().create(); 
			
			recPara.setField("V_FROM_YD_STK_COL_GP"	, inDto.getParam("FROM_YD_STK_COL_GP"));
			recPara.setField("V_TO_YD_STK_COL_GP"	, inDto.getParam("TO_YD_STK_COL_GP"));
			recPara.setField("V_YD_GP"				, inDto.getParam("TCAR_YD_GP"));
			
			retRdSet = dao.getToDongTcarUse(recPara);
						
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
	} // end of getToDongUseCount
	
	/**
	 * 콘베어정보목록img 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdConveyorMgtImgNew(JDTORecord inDto) throws DAOException {
		JDTORecord     recPara    = JDTORecordFactory.getInstance().create();
		JDTORecordSet  outRecSet  = JDTORecordFactory.getInstance().createRecordSet("yd");
		String szMsg              = "";
		String szMethodName       = "getCoilYdConveyorMgtImgNew";
		YdStkLyrDao ydStkLyrDao   = new YdStkLyrDao(); 
		int intRtnVal = 0;
		String sConv  = "";
		
		try {
			
			//
			recPara.setField("V_TEMP", "A");
			
			/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdConveyorMgtImgNew*/
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 619);
			
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
			
			logger.println(LogLevel.DEBUG_TEXT, "getCoilYdConveyorMgtImg");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}
	
	
	/**
	 * 야드관리 > 코일소재야드 >  
	 * @ejb.interface-method
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @작성자 :박지열
	 * @작성일 : 2010.04.22
	 */
	public GridData getHrShrWoUnitCmtUnit(GridData inDto) throws DAOException {
		
		JDTORecordSet 	retRdSet 	= JDTORecordFactory.getInstance().createRecordSet("YD");
		CoilJspDAO 		dao 		= new CoilJspDAO();
		GridData 		retGrid 	= new GridData();
		JDTORecord 		recPara		= null;
		try {
			
			recPara		= JDTORecordFactory.getInstance().create(); 
 
			recPara.setField("V_PTOP_PLNT_GP"	, inDto.getParam("V_PTOP_PLNT_GP"));
			recPara.setField("V_LINE_GP"	, inDto.getParam("V_LINE_GP"));
			recPara.setField("V_WORK_STAT"				, inDto.getParam("V_WORK_STAT"));
			
			retRdSet = dao.getHrShrWoUnitCmtUnit(recPara);
						
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
	} // end of getHrShrWoUnitCmtUnit
	
	/**
	 * 야드관리 > 코일소재야드 > 야드재공관리 > 스판별재공현황
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getSpanStockList(JDTORecord inDto) throws DAOException {
		JDTORecord       recPara    = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet  = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		String szMsg        = "스판별재공현황";		
		String szMethodName = "getSpanStockList";
	      
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao(); 
		
		int intRtnVal = 0;
		
		try {
			szMsg = "JSP-SESSION [스판별재공현황]시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
			
			String message = "";
	 
			recPara.setField("DONG",		yddatautil.setDataDefault(inDto.getField("DONG"),""));
			recPara.setField("FROM_SPAN",	yddatautil.setDataDefault(inDto.getField("FROM_SPAN"),""));
			recPara.setField("TO_SPAN",		yddatautil.setDataDefault(inDto.getField("TO_SPAN"),""));
 
			ydUtils.putLog(szSessionName,"\nform 에 있는 모든 변수값 : ", message, YdConstant.INFO);
			/*ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.getSpanStockList*/
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 309);
			
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
		szMsg = "JSP-SESSION [스판별재공현황]끝";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);	
		return outRecSet;
	}//end of getCoilYdColStkPosList
	
	
	/**
	 *  코일이송실적조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdMoveResultList(JDTORecord inDto) throws DAOException {
		
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		
		String      szMsg        = "";
		String      szMethodName = "getCoilYdMoveResultList";
		String	szOperationName = "코일이송실적조회";		
		
		YdCarSchDao ydCarSchDao = new YdCarSchDao();
		
		int intRtnVal = 0;
		
		try {		
			
			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				    
			  recPara.setField("FROM_DATE",    		ydDaoUtils.paraRecChkNull(inDto, "DATE_FROM"));
			  recPara.setField("TO_DATE", 		    ydDaoUtils.paraRecChkNull(inDto, "DATE_TO"));
			  recPara.setField("YD_WRK_DUTY", 	    ydDaoUtils.paraRecChkNull(inDto, "YD_WRK_DUTY"));
			  
			  //ym.facilitystatus.facilityinquiry.dao.YardFicilityDAO.getCoilMoveResult
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, outRecSet, 427);
						
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
 
	}//end of getCoilYdMoveResultList
	
	
	/**
	 *  코일이송실적조회상세
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdMoveResultListSub(JDTORecord inDto) throws DAOException {
		
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		
		String      szMsg        = "";
		String      szMethodName = "getCoilYdMoveResultListSub";
		String	szOperationName = "코일이송실적조회상세";		
		
		YdCarSchDao ydCarSchDao = new YdCarSchDao();
		
		int intRtnVal = 0;
		
		try {		
			
			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				    
			  recPara.setField("FROM_DATE",    		ydDaoUtils.paraRecChkNull(inDto, "DATE_FROM"));
			  recPara.setField("TO_DATE", 		    ydDaoUtils.paraRecChkNull(inDto, "DATE_TO"));
			  recPara.setField("SPOS_WLOC_CD", 	    ydDaoUtils.paraRecChkNull(inDto, "SPOS_WLOC_CD"));
			  recPara.setField("ARR_WLOC_CD", 	    ydDaoUtils.paraRecChkNull(inDto, "ARR_WLOC_CD"));
			  
			  //ym.facilitystatus.facilityinquiry.dao.YardFicilityDAO.getCoilMoveResultListA
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, outRecSet, 428);
						
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
 
	}//end of getCoilYdMoveResultListSub
	
	
	/**
	 *  코일야드 차량진행관리 조회
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getCoilYdCarWorkingList(JDTORecord inDto) throws DAOException {

		JDTORecord recPara      = JDTORecordFactory.getInstance().create();
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		YdCarSchDao ydCarschDao = new YdCarSchDao();
		JDTORecordSet retRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");


		String szMsg         = "";
		String szMethodName  = "getCoilYdCarWorkingList";
		String szOperationName = "코일야드 차량진행관리 조회";

		JDTORecord revRec    = JDTORecordFactory.getInstance().create();

		String chkWorkStat   = null;
		String carUseGp      = null;
		String out_plant     = null;
		String arr_plant     = null;


		int intRtnVal = 0;

		try {

			szMsg = "[JSP Session : "+szOperationName+"] 메소드 시작 ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);

			recPara.setField("YD_GP1",				yddatautil.setDataDefault(inDto.getField("YD_GP"), ""));
			recPara.setField("YD_GP2",				yddatautil.setDataDefault(inDto.getField("YD_GP"), ""));
			recPara.setField("YD_EQP_GP",			yddatautil.setDataDefault(inDto.getField("CAR_GP"), ""));
			recPara.setField("PAGE_CNT1",            inDto.getField("PAGE_NO"));
			recPara.setField("PAGE_CNT2",            inDto.getField("PAGE_NO"));
			recPara.setField("ROW_CNT1",             inDto.getField("ROWCOUNT"));
			recPara.setField("ROW_CNT2",             inDto.getField("ROWCOUNT"));

			/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getCoilYdCarWorkingList*/
			intRtnVal = ydCarschDao.getYdCarsch(recPara, outRecSet, 429);

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
				if(out_plant.equals("")){
					out_plant =yddatautil.setDataDefault(inDto.getField("YD_GP"), "");
				}
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
	 * B열연 Coil 야드 결로 위치 지정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord updCondensationAsgnInfo(JDTORecord inDto) throws DAOException {
		int intRtnVal     			= 0;
		String szLogMsg         	= null;
		String szMethodName    		= "updCondensationAsgnInfo";		
		String szOperationName 		= "B열연 Coil 야드 결로 위치 지정";
		
		String sYD_GP				= "";
		String sYD_BAY_GP			= "";
		String sYD_EQP_GP			= "";
		String sCONTENTS			= "";
		String sMODIFIER 			= "";
		
		JDTORecord outRecord 		= JDTORecordFactory.getInstance().create();
		CoilJspDAO 		dao 		= new CoilJspDAO();
		
		try {
			
			szLogMsg = "JSP-SESSION [B열연 Coil 야드 결로 위치 지정] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			szLogMsg = "JSP-SESSION [B열연 Coil 야드 결로 위치 지정] inDto= "+ydUtils.disyRec( inDto);
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			sYD_GP = ydDaoUtils.paraRecChkNull(inDto, "YD_GP");
			sYD_BAY_GP = ydDaoUtils.paraRecChkNull(inDto, "YD_BAY_GP");
			sYD_EQP_GP = ydDaoUtils.paraRecChkNull(inDto, "YD_EQP_GP");
			sCONTENTS = ydDaoUtils.paraRecChkNull(inDto, "CONTENTS");
			sMODIFIER = ydDaoUtils.paraRecChkNull(inDto, "YD_USER_ID");
			
			JDTORecord    tmpPara       = JDTORecordFactory.getInstance().create();
			tmpPara.setField("V_YD_GP" , sYD_GP);
			tmpPara.setField("V_YD_BAY_GP" , sYD_BAY_GP);
			tmpPara.setField("V_YD_EQP_GP" , sYD_EQP_GP);
			tmpPara.setField("V_CONTENTS" , sCONTENTS);
			tmpPara.setField("V_USERID" , sMODIFIER);
			
			/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDAO.updCondensationAsgnInfo */
			intRtnVal = dao.updCondensationAsgnInfo(tmpPara);
			
			
			if(intRtnVal != 1){
				szLogMsg = "JSP-SESSION [결로 위치 지정 실패]";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szLogMsg);	
				return outRecord;
			}else{
				szLogMsg = "JSP-SESSION [결로 위치 지정 완료]";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
				outRecord.setField("RTN_CD" 	, "1");	
				outRecord.setField("RTN_MSG" 	, szLogMsg);
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
		return outRecord;
	}
	
	
	
	
	
	/**
	 * B열연 Coil 야드 결로 위치 해제
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord updCondensationRelInfo(JDTORecord inDto) throws DAOException {
		int intRtnVal     			= 0;
		String szLogMsg         	= null;
		String szMethodName    		= "updCondensationRelInfo";		
		String szOperationName 		= "B열연 Coil 야드 결로 위치 해제";
		
		String sYD_GP				= "";
		String sYD_BAY_GP			= "";
		String sYD_EQP_GP			= "";
		String sMODIFIER 			= "";
		
		JDTORecord outRecord 		= JDTORecordFactory.getInstance().create();
		CoilJspDAO 		dao 		= new CoilJspDAO();
		
		try {
			
			szLogMsg = "JSP-SESSION [B열연 Coil 야드 결로 위치 해제] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			szLogMsg = "JSP-SESSION [B열연 Coil 야드 결로 위치 해제] inDto= "+ydUtils.disyRec( inDto);
			ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
			sYD_GP = ydDaoUtils.paraRecChkNull(inDto, "YD_GP");
			sYD_BAY_GP = ydDaoUtils.paraRecChkNull(inDto, "YD_BAY_GP");
			sYD_EQP_GP = ydDaoUtils.paraRecChkNull(inDto, "YD_EQP_GP");
			sMODIFIER = ydDaoUtils.paraRecChkNull(inDto, "YD_USER_ID");
			
			JDTORecord    tmpPara       = JDTORecordFactory.getInstance().create();
			tmpPara.setField("V_YD_GP" , sYD_GP);
			tmpPara.setField("V_YD_BAY_GP" , sYD_BAY_GP);
			tmpPara.setField("V_YD_EQP_GP" , sYD_EQP_GP);
			tmpPara.setField("V_USERID" , sMODIFIER);
			
			/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDAO.updCondensationRelInfo */
			intRtnVal = dao.updCondensationRelInfo(tmpPara);
			
			
			if(intRtnVal != 1){
				szLogMsg = "JSP-SESSION [결로 위치 해제 실패]";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				outRecord.setField("RTN_CD" 	, "0");	
				outRecord.setField("RTN_MSG" 	, szLogMsg);	
				return outRecord;
			}else{
				szLogMsg = "JSP-SESSION [결로 위치 해제 완료]";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
			
				//insert
				szLogMsg = "JSP-SESSION [B열연 Coil 야드 결로 위치 정보 신규 등록] 시작";
				ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				
				/*com.inisteel.cim.yd.jsp.coiljsp.dao.CoilJspDAO.insNewCondensationInfo */
				intRtnVal = dao.insCondensationInfo(tmpPara);
				
				if(intRtnVal != 1) {
					szLogMsg = "JSP-SESSION [결로 위치 정보 신규 등록 실패]";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.DEBUG);
					
					outRecord.setField("RTN_CD" 	, "0");	
					outRecord.setField("RTN_MSG" 	, szLogMsg);	
					return outRecord;
				} else {
					szLogMsg = "JSP-SESSION [결로 위치 정보 신규 등록 완료]";
					ydUtils.putLog(szSessionName, szMethodName, szLogMsg, YdConstant.INFO);
				}
				
				outRecord.setField("RTN_CD" 	, "1");	
				outRecord.setField("RTN_MSG" 	, szLogMsg);
				
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
		return outRecord;
	}
	
	
	/**
	 *  2열연 결로ON/OFF 조회 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getConResultList(JDTORecord inDto) throws DAOException {
		
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YD");
		
		
		String      szMsg        = "";
		String      szMethodName = "getConResultList";
		String	szOperationName = "2열연 결로ON/OFF 조회";		
		
		YdCarSchDao ydCarSchDao = new YdCarSchDao();
		
		int intRtnVal = 0;
		
		try {		
			
			szMsg = "JSP-SESSION ["+ szOperationName +"] 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
				    
			  //recPara.setField("FROM_DATE",    		ydDaoUtils.paraRecChkNull(inDto, "DATE_FROM")); 
			  
			  //ym.facilitystatus.facilityinquiry.dao.YardFicilityDAO.getConResultList
			intRtnVal = ydCarSchDao.getYdCarsch(recPara, outRecSet, 441);
						
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
 
	}//end of getConResultList
	
	/**
	 * 결로ON  버튼 클릭시 실행
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
public GridData updConOffResultList(GridData inDto) throws DAOException {
		 
		CoilJspDAO 		dao 		= new CoilJspDAO();
		GridData 		retGrid 	= new GridData();
		JDTORecord 		recPara		= null;
		int intRtnVal     			= 0;
		try {
			
			recPara		= JDTORecordFactory.getInstance().create(); 
			
			recPara.setField("V_MODE_GP"	, inDto.getParam("MODE")); 
			recPara.setField("V_YD_USER_ID"	, inDto.getParam("YD_USER_ID")); 
			
			intRtnVal = dao.updConOffResultYN(recPara);
						
 	
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return retGrid;
	} // end of updConOffResultList

/**
 * 결로HOT코일이적
 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
 * @param inDto
 * @return JDTORecordSet
 * @throws DAOException
 */
public JDTORecordSet getCondenMvstkReg(JDTORecord inDto) throws DAOException {
	int intRtnVal = 0;
	String szMsg="";
	String szMethodName="getCondenMvstkReg";		
	JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("YD");
	JDTORecordSet rSetStock = JDTORecordFactory.getInstance().createRecordSet("YD");
	JDTORecord recPara = JDTORecordFactory.getInstance().create();

	YdStockDao ydStockDao = new YdStockDao(); 
	try {
		recPara.setField("YD_GP" 			, ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));
		recPara.setField("YD_BAY_GP" 		, ydDaoUtils.paraRecChkNull(inDto, "YD_BAY_GP"));
		recPara.setField("YD_EQP_GP" 		, ydDaoUtils.paraRecChkNull(inDto, "YD_EQP_GP"));
		recPara.setField("YD_STK_COL_NO" 	, ydDaoUtils.paraRecChkNull(inDto, "YD_STK_COL_NO")); 
		recPara.setField("YD_MIL_SEQ" 		, ydDaoUtils.paraRecChkNull(inDto, "YD_MIL_SEQ")); 
		recPara.setField("YD_TIME" 			, ydDaoUtils.paraRecChkNull(inDto, "YD_TIME")); 
		recPara.setField("PAGE_NO" 			, ydDaoUtils.paraRecChkNull(inDto, "PAGE_NO"));
		recPara.setField("ROW_CNT" 			, ydDaoUtils.paraRecChkNull(inDto, "ROWCOUNT")); 
		
		
//PIDEV_S :병행가동용:PI_YD
		recPara.setField("PI_YD",   ydDaoUtils.paraRecChkNull(inDto, "YD_GP"));
		 //com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCondenMvstkReg_PIDEV
		intRtnVal = ydStockDao.getYdStock(recPara, rSetStock, 733);
					
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
} // end of getCondenMvstkReg
}