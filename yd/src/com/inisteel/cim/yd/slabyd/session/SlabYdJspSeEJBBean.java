/**
 * @(#)SlabYdJspSeEJBBean
 *
 * @version          V1.00
 * @author           허철호
 * @date             2013/03/04
 *
 * @description      Slab야드 화면 처리
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2013/03/04   허철호      허철호      최초 등록
 */
package com.inisteel.cim.yd.slabyd.session;
   
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.dao.YdPlateCommDAO;
import com.inisteel.cim.yd.common.dao.ydPrepSchDao.YdPrepSchDao;
import com.inisteel.cim.yd.common.dao.ydStkColDao.YdStkColDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.util.DaoManager;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdSlabUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.pSlabYd.dao.PSlabYdCommDAO;
import com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO;
import com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO;
import com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO;
import com.inisteel.cim.yd.slabyd.dao.SlabYdSchDAO;  
import com.inisteel.cim.ydPI.dao.YdPICommDAO;

/**
 *      [A] 클래스명 : Slab야드 화면 처리
 *
 * @ejb.bean name="SlabYdJspSeEJB" jndi-name="SlabYdJspSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300" 
 * @ejb.transaction type="Required"
*/
public class SlabYdJspSeEJBBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;
	private YdSlabUtils slabUtils = new YdSlabUtils();
	private SlabYdComm   slabComm = new SlabYdComm();
	private SlabYdCommDAO commDao = new SlabYdCommDAO();
	YdPlateCommDAO 	commDao2 = new YdPlateCommDAO();
	private SlabYdJspDAO   jspDao = new SlabYdJspDAO();
	private SlabYdSchDAO   schDao = new SlabYdSchDAO(); 
	private YdStockDao ydStockDao = new YdStockDao();
	
	private PSlabYdCommDAO PcommDao = new PSlabYdCommDAO();
	private YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();
	private YdUtils ydUtils = new YdUtils();
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}
	
	/***************************************************************************
	 * 크레인작업관리
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 크레인작업관리 조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getCrnWrkMgt(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리 조회[SlabYdJspSeEJB.getCrnWrkMgt] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			JDTORecordSet jrRst = jspDao.getCrnWrkMgt(gdReq);
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			return slabUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업관리 크레인변경
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtCrnWrkMgtCM(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리 크레인변경[SlabYdJspSeEJB.trtCrnWrkMgtCM] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			String ydCrnSchId      = ""; //야드크레인스케쥴ID
			String ydWbookId       = ""; //야드작업예약ID
			String ydWrkProgStat   = ""; //야드작업진행상태
			String ydSchCd         = ""; //야드스케쥴코드
			String ydEqpId         = ""; //야드설비ID(크레인)
			String chgYdEqpId      = ""; //변경 야드설비ID(크레인)
			String chgYdSchPrior   = ""; //변경 야드스케쥴우선순위
			String chgYdEqpStat    = ""; //변경 야드설비상태
			String chgYdEqpWrkMode = ""; //변경 야드설비작업Mode
			String modifier = slabUtils.trim(gdReq.getParam("userid")); //수정자
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, modifier);
			
			//처리완료한 야드작업예약ID
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String[] arrYdWbookId = new String[rowCnt];
			
			for (int ii = 0; ii < rowCnt; ii++) {
				ydWbookId  = slabUtils.trim(gdReq.getHeader("YD_WBOOK_ID"  ).getValue(ii));
				ydCrnSchId = slabUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID").getValue(ii));

			    //작업할  야드작업예약ID가 작업 완료한 야드작업예약ID에 있으면 이전에 모두 처리되었으므로 Skip
				if (slabUtils.chkExist(arrYdWbookId, ydWbookId)) { continue; }
				arrYdWbookId[ii] = ydWbookId;

				/**********************************************************
				* 1. 크레인스케줄, 스케줄기준, 설비정보 Check
				* 1.1 크레인스케줄의 스케줄ID 및 설비상태 Check
				* 1.2 크레인스케줄 설비ID로 스케줄기준의 주 및 대체 크레인설비ID와 비교하여 변경 크레인설비ID와 순위를 Set
				* 1.3 변경 할 크레인 정보를 Check
				**********************************************************/
				jrParam.setField("V_YD_WBOOK_ID"  , ydWbookId );
				jrParam.setField("V_YD_CRN_SCH_ID", ydCrnSchId);
				
				//기본정보조회
				JDTORecordSet jsCrn = jspDao.getCrnWrkMgt("CMSch", jrParam);

			    if (jsCrn == null || jsCrn.size() <= 0) {
					throw new Exception("크레인스케쥴ID[" + ydCrnSchId + "]의 크레인스케줄 정보가 존재하지 않습니다.");
			    }
				
			    JDTORecord jrCrn = jsCrn.getRecord(0);
				
			    ydWrkProgStat   = slabUtils.trim(jrCrn.getFieldString("YD_WRK_PROG_STAT"   )); //야드작업진행상태
				ydSchCd         = slabUtils.trim(jrCrn.getFieldString("YD_SCH_CD"          )); //야드스케쥴코드
				ydEqpId         = slabUtils.trim(jrCrn.getFieldString("YD_EQP_ID"          )); //야드설비ID
				chgYdEqpId      = slabUtils.trim(jrCrn.getFieldString("CHG_YD_EQP_ID"      )); //변경 야드설비ID
				chgYdSchPrior   = slabUtils.trim(jrCrn.getFieldString("CHG_YD_SCH_PRIOR"   )); //변경 야드스케쥴우선순위
				chgYdEqpStat    = slabUtils.trim(jrCrn.getFieldString("CHG_YD_EQP_STAT"    )); //변경 야드설비상태
				chgYdEqpWrkMode = slabUtils.trim(jrCrn.getFieldString("CHG_YD_EQP_WRK_MODE")); //변경 야드설비작업Mode

				if ("2".equals(ydWrkProgStat)) {
					throw new Exception("크레인스케줄 [" + ydCrnSchId + "]의 작업진행상태가 [2:권상완료]이므로 변경하실 수 없습니다.");
				} else if ("3".equals(ydWrkProgStat)) {
					throw new Exception("크레인스케줄 [" + ydCrnSchId + "]의 작업진행상태가 [3:권하지시]이므로 변경하실 수 없습니다.");
				} else if ("4".equals(ydWrkProgStat)) {
					throw new Exception("크레인스케줄 [" + ydCrnSchId + "]의 작업진행상태가 [4:권하완료]이므로 변경하실 수 없습니다.");
				} else if ("".equals(chgYdEqpId)) {
					throw new Exception("변경 크레인 [" + chgYdEqpId + "]의 정보가 존재하지 않습니다.");
				} else if ("B".equals(chgYdEqpStat)) {
					throw new Exception("변경 크레인 [" + chgYdEqpId + "]의 설비상태가 [B:고장]이므로 변경하실 수 없습니다.");
				} else if (!"1".equals(chgYdEqpWrkMode)) {
					throw new Exception("변경 크레인 [" + chgYdEqpId + "]의 설비작업Mode가 [Off-Line]이므로 변경하실 수 없습니다.");
				} else if ("1".equals(chgYdEqpStat) || "2".equals(chgYdEqpStat) || "3".equals(chgYdEqpStat)) {
					throw new Exception("변경 크레인 [" + chgYdEqpId + "]의 작업지시가 이미 내려진 상태이므로 변경하실 수 없습니다.");
				}

				slabUtils.printLog(logId, "크레인변경 [ " + ydWbookId + " : " + ydEqpId + " >> " + chgYdEqpId + " ]", "SL");

				/**********************************************************
				* 2. 작업예약 및 크레인스케줄 Table에  대체 크레인ID와 우선순위를 Update
				**********************************************************/
				jrParam.setField("V_YD_SCH_PRIOR", chgYdSchPrior);
				jrParam.setField("V_YD_EQP_ID"   , chgYdEqpId   );
				
				//작업예약 Table 우선순위 Update
				commDao.updSlabYd("WbPrior", jrParam);
			
				//크레인스케줄 Table 크레인ID, 우선순위 Update
				jspDao.updCrnWrkMgt("CMCrnSch", jrParam);
			
				/**********************************************************
				* 3. 현 작업상태가 권상지시[1]인 경우
				**********************************************************/
				if ("1".equals(ydWrkProgStat)) {
					/**********************************************************
					* 3.1 변경 크레인의 설비 Table 상태정보를 Update
					**********************************************************/
					jrParam.setField("V_YD_EQP_STAT", "1"); //야드설비상태 : 권상작업지시

					commDao.updStat("Eqp", jrParam);

					/**********************************************************
					* 3.2 변경 크레인의 크레인작업지시요구 처리
					**********************************************************/
					//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
					JDTORecord jrYdMsg = slabUtils.getParam(logId, methodNm, modifier);

					jrYdMsg.setField("JMS_TC_CD"       , "Y1YDL007");	//크레인작업지시요구
					jrYdMsg.setField("YD_EQP_ID"       , chgYdEqpId);	//야드설비ID
					jrYdMsg.setField("YD_WRK_PROG_STAT", "1"       );	//야드작업진행상태(권상작업지시)
					jrYdMsg.setField("YD_SCH_CD"       , ydSchCd   );	//야드스케쥴코드
					jrYdMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId);	//야드크레인스케쥴ID

					EJBConnector sndConn = new EJBConnector("default", "SlabYdL2RcvSeEJB", this);
					JDTORecord jrSnd = (JDTORecord)sndConn.trx("rcvY1YDL007", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });

					jrRtn = slabUtils.addSndData(jrRtn, jrSnd);

					/**********************************************************
					* 3.3 이전 크레인의 설비 Table 상태정보를 Update
					**********************************************************/
					jrParam.setField("V_YD_EQP_ID"  , ydEqpId);
					jrParam.setField("V_YD_EQP_STAT", "W"    ); //야드설비상태 : 권상작업지시

					commDao.updStat("Eqp", jrParam);

					/**********************************************************
					* 3.4 이전 크레인의 작업실적응답 전문을 전송
					**********************************************************/
					JDTORecord resMsg = JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용

					resMsg.setResultCode(logId);	//Log ID
					resMsg.setResultMsg(methodNm);	//Log Method Name
					resMsg.setField("YD_EQP_ID"     , ydEqpId); //야드설비ID
					resMsg.setField("YD_L2_WR_GP"   , "J"    ); //야드L2실적구분(지시요구)
					resMsg.setField("YD_L3_HD_RS_CD", "9999" ); //야드L3처리결과코드(Error)
					resMsg.setField("YD_L3_MSG"     , "크레인변경[" + chgYdEqpId + "]" ); //야드L3MESSAGE

					jrRtn = slabUtils.addSndData(jrRtn, slabComm.getYDY1L005(resMsg));
				}
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}

	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업관리 순위변경
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtCrnWrkMgtPM(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리 순위변경[SlabYdJspSeEJB.trtCrnWrkMgtPM] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			String ydWbookId  = ""; //야드작업예약ID
			String ydSchPrior = ""; //야드스케쥴우선순위
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));

			//처리완료한 야드작업예약ID
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String[] arrYdWbookId = new String[rowCnt];
			
			for (int ii = 0; ii < rowCnt; ii++) {
				/**********************************************************
				* 1. 작업예약ID Check
				**********************************************************/
			    ydWbookId  = slabUtils.trim(gdReq.getHeader("YD_WBOOK_ID" ).getValue(ii)); //야드작업예약ID
			    ydSchPrior = slabUtils.trim(gdReq.getHeader("YD_SCH_PRIOR").getValue(ii)); //야드스케쥴우선순위

			    //작업할  야드작업예약ID가 작업 완료한 야드작업예약ID에 있으면 이전에 모두 처리되었으므로 Skip
				if (slabUtils.chkExist(arrYdWbookId, ydWbookId)) { continue; }
				arrYdWbookId[ii] = ydWbookId;

				slabUtils.printLog(logId, "우선순위변경 [ " + ydWbookId + " >> " + ydSchPrior + " ]", "SL");

				/**********************************************************
				* 2. 작업예약 및 크레인스케줄 Table에  우선순위를 Update
				**********************************************************/
				jrParam.setField("V_YD_WBOOK_ID" , ydWbookId );
				jrParam.setField("V_YD_SCH_PRIOR", ydSchPrior);
				
				//작업예약 Table 우선순위 Update
				commDao.updSlabYd("WbPrior", jrParam);
			
				//크레인스케줄 Table 크레인ID, 우선순위 Update
				jspDao.updCrnWrkMgt("CMCrnSch", jrParam);
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}

	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업관리 권하위치변경
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtCrnWrkMgtDM(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리 권하위치변경[SlabYdJspSeEJB.trtCrnWrkMgtDM] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String crnSchId = "";
		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//EJB Call을 위한 Message 생성 - Log ID, Method, 수정자 Set
			JDTORecord jrYdMsg = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));
			
			//권하위치변경 대상 스케줄
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {
				crnSchId = slabUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID"   ).getValue(ii));				
				//화면단 변수가 아닌 크레인 스케줄로 스케줄 조회하여 정보 get
				if("".equals(crnSchId))	continue;		
				else {
					slabUtils.printLog(logId, methodNm, crnSchId+"에 대해 크레인 스케줄 조회 시작", gdReq);
					JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));
					jrParam.setField("V_YD_CRN_SCH_ID"       , crnSchId);
					JDTORecordSet jsCrnSch = jspDao.getCrnWrkMgt("SI", jrParam);

					if (jsCrnSch != null && jsCrnSch.size() > 0) {
						JDTORecord jrCrnSch = jsCrnSch.getRecord(0);
						
						String szMsg = "장비: "+slabUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"))+
									   "|스케줄ID: "+slabUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"))+
									   "|신규권하위치: "+slabUtils.trim(gdReq.getHeader("YD_DN_WO_LOC"    ).getValue(ii))+
									   "|이전권하위치: "+slabUtils.trim(jrCrnSch.getFieldString("YD_DN_WO_LOC"  ))+
									   " 에 대해 권하위치 변경\n";
						slabUtils.printLog(logId, methodNm, szMsg, gdReq);
						
				    	jrYdMsg.setField("YD_EQP_ID"       , slabUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"  ))); //야드설비ID(크레인)
						jrYdMsg.setField("YD_SCH_CD"       , slabUtils.trim(jrCrnSch.getFieldString("YD_SCH_CD"  ))); //야드스케쥴코드
						jrYdMsg.setField("YD_CRN_SCH_ID"   , slabUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"  )));  //야드크레인스케쥴ID
						jrYdMsg.setField("YD_WBOOK_ID"     , slabUtils.trim(jrCrnSch.getFieldString("YD_WBOOK_ID"  )));  //야드작업예약ID
						jrYdMsg.setField("YD_DN_WO_LOC"    , slabUtils.trim(gdReq.getHeader("YD_DN_WO_LOC"    ).getValue(ii))); //야드권하지시위치(신규)
						jrYdMsg.setField("YD_WRK_PROG_STAT", slabUtils.trim(jrCrnSch.getFieldString("YD_WRK_PROG_STAT"  ))); //야드작업진행상태
						jrYdMsg.setField("YD_UP_WO_LOC"	   , slabUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LOC"  ))); //야드권상위치
						jrYdMsg.setField("YD_DN_WO_LOC_ORG", slabUtils.trim(jrCrnSch.getFieldString("YD_DN_WO_LOC"  ))); //야드이전권하위치
					
						//권하지시위치 변경
						jrRtn = slabUtils.addSndData(jrRtn, this.updCrnSchDnWoLoc(jrYdMsg));
					}
				}
				
				/*jrYdMsg.setField("YD_EQP_ID"       , slabUtils.trim(gdReq.getHeader("YD_EQP_ID"       ).getValue(ii))); //야드설비ID(크레인)
				jrYdMsg.setField("YD_SCH_CD"       , slabUtils.trim(gdReq.getHeader("YD_SCH_CD"       ).getValue(ii))); //야드스케쥴코드
				jrYdMsg.setField("YD_CRN_SCH_ID"   , slabUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID"   ).getValue(ii))); //야드크레인스케쥴ID
				jrYdMsg.setField("YD_WBOOK_ID"     , slabUtils.trim(gdReq.getHeader("YD_WBOOK_ID"     ).getValue(ii))); //야드작업예약ID
				jrYdMsg.setField("YD_DN_WO_LOC"    , slabUtils.trim(gdReq.getHeader("YD_DN_WO_LOC"    ).getValue(ii))); //야드권하지시위치(신규)
				jrYdMsg.setField("YD_WRK_PROG_STAT", slabUtils.trim(gdReq.getHeader("YD_WRK_PROG_STAT").getValue(ii))); //야드작업진행상태
				jrYdMsg.setField("YD_UP_WO_LOC"	   , slabUtils.trim(gdReq.getHeader("YD_UP_WO_LOC"    ).getValue(ii))); //야드권상위치
				jrYdMsg.setField("YD_DN_WO_LOC_ORG", slabUtils.trim(gdReq.getHeader("YD_DN_WO_LOC_ORG").getValue(ii))); //야드이전권하위치
*/
				//권하지시위치 변경
				//jrRtn = slabUtils.addSndData(jrRtn, this.updCrnSchDnWoLoc(jrYdMsg));
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}

	
	/**
	 *      [A] 오퍼레이션명 : 크레인스케줄 권하지시위치 변경
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updCrnSchDnWoLoc(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인스케줄 권하지시위치 변경[SlabYdJspSeEJB.updCrnSchDnWoLoc] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			String ydEqpId       = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); //야드설비ID(크레인)
			String ydSchCd       = slabUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
			String ydCrnSchId    = slabUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )); //야드크레인스케쥴ID
			String ydWbookId     = slabUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"     )); //야드작업예약ID
			String ydDnWoLoc     = slabUtils.trim(rcvMsg.getFieldString("YD_DN_WO_LOC"    )); //야드권하지시위치(신규)
			String ydWrkProgStat = slabUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
			String modifier      = slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER"      )); //수정자
			String ydUpWoLoc	 = slabUtils.trim(rcvMsg.getFieldString("YD_UP_WO_LOC"    )); //야드권상위치
			
			if ("".equals(ydCrnSchId)) {
				throw new Exception("크레인스케쥴ID가 없습니다.");
			} else if ("".equals(ydDnWoLoc)) {
				throw new Exception("변경할 권하지시위치가 없습니다.");
			} else if (ydDnWoLoc.length() != 8) {
				throw new Exception("잘못된 권하지시위치[" + ydDnWoLoc + "] 입니다.");
			}

			//Return Value
			JDTORecord jrRtn = null;

			String ydStkColGp     = ydDnWoLoc.substring(0, 6); //야드적치열구분
			String ydStkBedNo     = ydDnWoLoc.substring(6, 8); //야드적치Bed번호
			String ydDnWoLocOld   = ""; //야드권하지시위치(기존)
			String ydDnWoLayer    = ""; //야드권하지시단(신규)
			String ydDnWoLocXaxis = ""; //야드권하지시X축(신규)
			String ydDnWoLocYaxis = ""; //야드권하지시Y축(신규)
			String ydDnWoLocZaxis = ""; //야드권하지시Z축(신규)
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, modifier);

			jrParam.setField("V_YD_CRN_SCH_ID"       , ydCrnSchId);
			jrParam.setField("V_YD_CARLD_WRK_BOOK_ID", ydWbookId );	//야드상차작업예약ID
			jrParam.setField("V_YD_STK_COL_GP"       , ydStkColGp);
			jrParam.setField("V_YD_STK_BED_NO"       , ydStkBedNo);
			
			/**********************************************************
			* 1. 신규 권하지시위치 Bed정보 조회
			**********************************************************/
			JDTORecordSet jsCrnSch = jspDao.getCrnWrkMgt("DMBed", jrParam);

			if (jsCrnSch != null && jsCrnSch.size() > 0) {
		    	JDTORecord jrCrnSch = jsCrnSch.getRecord(0);

		    	ydDnWoLocOld   = slabUtils.trim(jrCrnSch.getFieldString("YD_DN_WO_LOC_OLD"  ));
		    	ydDnWoLayer    = slabUtils.trim(jrCrnSch.getFieldString("YD_DN_WO_LAYER"    ));
		    	ydDnWoLocXaxis = slabUtils.trim(jrCrnSch.getFieldString("YD_DN_WO_LOC_XAXIS"));
		    	ydDnWoLocYaxis = slabUtils.trim(jrCrnSch.getFieldString("YD_DN_WO_LOC_YAXIS"));
		    	ydDnWoLocZaxis = slabUtils.trim(jrCrnSch.getFieldString("YD_DN_WO_LOC_ZAXIS"));
			    String dlLocChkRst = slabUtils.trim(jrCrnSch.getFieldString("DL_LOC_CHK_RST"));

			    if ("UP".equals(dlLocChkRst)) {
					throw new Exception("권상대기(U) 재료가 적치되어 있습니다.");
				} else if ("BW".equals(dlLocChkRst)) {
					throw new Exception("입출고가능(E) Bed가 아닙니다.");
				} else if ("SH".equals(dlLocChkRst)) {
					throw new Exception("적치단 매수를 초과 합니다.");
				} else if ("WT".equals(dlLocChkRst)) {
					throw new Exception("적치단 중량을 초과 합니다.");
				} else if ("HT".equals(dlLocChkRst)) {
					throw new Exception("적치단 높이를 초과 합니다.");
				} else if ("RS".equals(dlLocChkRst)) {
					throw new Exception("지시위치 수정 불가능 스케줄이 존재합니다.");
				}

			    //혹시 권하지시위치가 잘못 등록되어 있으면
			    if (ydDnWoLocOld.length() != 8) {
			    	ydDnWoLocOld = "XX010101";
				}
		    } else {
				throw new Exception("신규 권하지시위치[" + ydDnWoLoc + "] 정보가 없습니다.");
		    }
			
			/**********************************************************
			* 2. 권하지시위치 수정
			*  - 같은 작업예약ID의 이후 스케줄 권상 및 권하지시위치 모두 수정
			**********************************************************/
			jrParam.setField("V_YD_STK_COL_GP_OLD" , ydDnWoLocOld.substring(0, 6));
			jrParam.setField("V_YD_STK_BED_NO_OLD" , ydDnWoLocOld.substring(6, 8));
			jrParam.setField("V_YD_STK_COL_GP_NEW" , ydStkColGp    );
			jrParam.setField("V_YD_STK_BED_NO_NEW" , ydStkBedNo    );
			jrParam.setField("V_YD_DN_WO_LOC"      , ydDnWoLoc     );
			jrParam.setField("V_YD_DN_WO_LAYER"    , ydDnWoLayer   );
			jrParam.setField("V_YD_DN_WO_LOC_XAXIS", ydDnWoLocXaxis);
			jrParam.setField("V_YD_DN_WO_LOC_YAXIS", ydDnWoLocYaxis);
			jrParam.setField("V_YD_DN_WO_LOC_ZAXIS", ydDnWoLocZaxis);

			//적치단 수정 - 기존 및 신규 권하지시위치
			jspDao.updCrnWrkMgt("DMStkLyr", jrParam);

			//적치Bed 수정 - 완산Bed 해제
			jspDao.updCrnWrkMgt("DMStkBed", jrParam);

			//크레인스케줄 수정 - 권상, 권하지시위치
			String ydGp = ydStkColGp.substring(0 , 1);
			if(ydUpWoLoc.equals(ydDnWoLocOld) && "A".equals(ydGp)) {
				jspDao.updCrnWrkMgt("DMCrnSch2", jrParam);
			} else {
				jspDao.updCrnWrkMgt("DMCrnSch", jrParam);
			}

			//기존 대차, 차량 권하위치에서 일반야드로 변경 시 대차 or 차량스케줄 작업예약ID 삭제
			ydDnWoLocOld = ydDnWoLocOld.substring(2, 4);
			if (("TC".equals(ydDnWoLocOld) || "PT".equals(ydDnWoLocOld)) && !ydDnWoLocOld.equals(ydDnWoLoc.substring(2, 4))) {
				if ("TC".equals(ydDnWoLocOld)) {
					//대차스케줄 수정 - 상차작업예약ID 삭제
					jspDao.updCrnWrkMgt("DMTcarSch", jrParam);
				} else {
					//차량스케줄 수정 - 상차작업예약ID 삭제
					jspDao.updCrnWrkMgt("DMCarSch", jrParam);

					//적치열 수정 - 야드적치대용도코드 삭제
					jspDao.updCrnWrkMgt("DMStkCol", jrParam);
				}
			}
			
			/**********************************************************
			* 3. 크레인작업지시요구 전문 조회
			**********************************************************/
			//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
			JDTORecord jrYdMsg = slabUtils.getParam(logId, methodNm, modifier);

			jrYdMsg.setField("JMS_TC_CD"       , "Y1YDL007"   );	//크레인작업지시요구
			jrYdMsg.setField("YD_EQP_ID"       , ydEqpId      );	//야드설비ID
			jrYdMsg.setField("YD_WRK_PROG_STAT", ydWrkProgStat);	//야드작업진행상태
			jrYdMsg.setField("YD_SCH_CD"       , ydSchCd      );	//야드스케쥴코드
			jrYdMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId   );	//야드크레인스케쥴ID
			jrYdMsg.setField("V_MODIFIER"      , modifier     );	//수정자

			EJBConnector sndConn = new EJBConnector("default", "SlabYdL2RcvSeEJB", this);
			jrRtn = (JDTORecord)sndConn.trx("rcvY1YDL007", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });

			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}

	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업관리 보류/해제
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtCrnWrkMgtHR(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리 보류/해제[SlabYdJspSeEJB.trtCrnWrkMgtHR] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			String ydWbookId        = ""; //야드작업예약ID
			String ydWrkProgStatOld = ""; //야드작업진행상태(기존)
			String ydWrkProgStatNew = ""; //야드작업진행상태(신규)
			String trtGp            = slabUtils.trim(gdReq.getParam("V_TRT_GP")); //처리구분
			
			if ("HD".equals(trtGp)) {
				trtGp = "보류";
				ydWrkProgStatOld = "W"; //명령선택대기
				ydWrkProgStatNew = "C"; //스케쥴명령취소
			} else if ("HR".equals(trtGp)) {
				trtGp = "보류해제";
				ydWrkProgStatOld = "C"; //스케쥴명령취소
				ydWrkProgStatNew = "W"; //명령선택대기
			}
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));

			jrParam.setField("V_YD_WRK_PROG_STAT_OLD", ydWrkProgStatOld); //야드작업진행상태(기존)
			jrParam.setField("V_YD_WRK_PROG_STAT_NEW", ydWrkProgStatNew); //야드작업진행상태(신규)

			//처리완료한 야드작업예약ID
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String[] arrYdWbookId = new String[rowCnt];
			
			for (int ii = 0; ii < rowCnt; ii++) {
				/**********************************************************
				* 1. 작업예약ID Check
				**********************************************************/
			    ydWbookId = slabUtils.trim(gdReq.getHeader("YD_WBOOK_ID").getValue(ii)); //야드작업예약ID

			    //작업할  야드작업예약ID가 작업 완료한 야드작업예약ID에 있으면 이전에 모두 처리되었으므로 Skip
				if (slabUtils.chkExist(arrYdWbookId, ydWbookId)) { continue; }
				arrYdWbookId[ii] = ydWbookId;

				slabUtils.printLog(logId, "보류/해제 [ " + ydWbookId + " >> " + trtGp + " ]", "SL");

				/**********************************************************
				* 2. 크레인스케줄 Table의 야드작업진행상태를 Update
				**********************************************************/
				jrParam.setField("V_YD_WBOOK_ID", ydWbookId);
				
				jspDao.updCrnWrkMgt("HRCrnSch", jrParam);
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}

	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업관리 작업취소
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtCrnWrkMgtWC(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리 작업취소[SlabYdJspSeEJB.trtCrnWrkMgtWC] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			String ydCrnSchId = ""; //야드크레인스케쥴ID
			String ydWbookId  = ""; //야드작업예약ID
		    String ydEqpId    = ""; //야드설비ID
		    String ydSchCd    = ""; //야드스케쥴코드
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));

			//처리완료한 야드작업예약ID
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String[] arrYdWbookId = new String[rowCnt];
			
			for (int ii = 0; ii < rowCnt; ii++) {
				ydWbookId = slabUtils.trim(gdReq.getHeader("YD_WBOOK_ID").getValue(ii));
			    ydEqpId   = slabUtils.trim(gdReq.getHeader("YD_EQP_ID"  ).getValue(ii));
			    ydSchCd   = slabUtils.trim(gdReq.getHeader("YD_SCH_CD"  ).getValue(ii));

			    //작업할  야드작업예약ID가 작업 완료한 야드작업예약ID에 있으면 이전에 모두 처리되었으므로 Skip
				if (slabUtils.chkExist(arrYdWbookId, ydWbookId)) { continue; }
				arrYdWbookId[ii] = ydWbookId;

				//기본정보조회
				jrParam.setField("V_YD_WBOOK_ID", ydWbookId);

				JDTORecordSet jsCrn = jspDao.getCrnWrkMgt("WCSch", jrParam);

			    if (jsCrn == null || jsCrn.size() <= 0) {
					throw new Exception("작업예약ID[" + ydWbookId + "]의 크레인스케줄 정보가 존재하지 않습니다.");
			    }

				ydCrnSchId = slabUtils.trim(jsCrn.getRecord(0).getFieldString("YD_CRN_SCH_ID")); //야드크레인스케쥴ID
				
				slabUtils.printLog(logId, "작업취소 [ " + ydWbookId + " - " + ydCrnSchId + " ]", "SL");

				jrParam.setField("YD_WBOOK_ID"  , ydWbookId );
				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);
				jrParam.setField("YD_EQP_ID"    , ydEqpId   );
				jrParam.setField("YD_SCH_CD"    , ydSchCd   );
				
				/**********************************************************
				* 1. 크레인스케줄 취소
				**********************************************************/
				jrRtn = slabUtils.addSndData(jrRtn, this.trtCrnSchCncl(jrParam));

				/**********************************************************
				* 2. 작업예약 취소
				**********************************************************/
				jrRtn = slabUtils.addSndData(jrRtn, this.trtWrkBookCncl(jrParam));
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}

	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업관리 스케줄취소
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtCrnWrkMgtSC(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리 스케줄취소[SlabYdJspSeEJB.trtCrnWrkMgtSC] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			String ydCrnSchId = ""; //야드크레인스케쥴ID
			String ydWbookId  = ""; //야드작업예약ID
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));

			//처리완료한 야드작업예약ID
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String[] arrYdWbookId = new String[rowCnt];
			
			for (int ii = 0; ii < rowCnt; ii++) {
				ydWbookId  = slabUtils.trim(gdReq.getHeader("YD_WBOOK_ID"  ).getValue(ii));
				ydCrnSchId = slabUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID").getValue(ii));

			    //작업할  야드작업예약ID가 작업 완료한 야드작업예약ID에 있으면 이전에 모두 처리되었으므로 Skip
				if (slabUtils.chkExist(arrYdWbookId, ydWbookId)) { continue; }
				arrYdWbookId[ii] = ydWbookId;

				slabUtils.printLog(logId, "스케줄취소 [ " + ydWbookId + " - " + ydCrnSchId + " ]", "SL");

				jrParam.setField("YD_WBOOK_ID"  , ydWbookId );
				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);

				/**********************************************************
				* 1. 크레인스케줄 취소
				**********************************************************/
				jrRtn = slabUtils.addSndData(jrRtn, this.trtCrnSchCncl(jrParam));
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}

	
	/**
	 *      [A] 오퍼레이션명 : 크레인스케줄 취소처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtCrnSchCncl(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인스케줄 취소처리[SlabYdJspSeEJB.trtCrnSchCncl] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			String ydCrnSchId = slabUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID")); //야드크레인스케쥴ID
			String ydWbookId  = slabUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"  )); //야드작업예약ID
			
			if ("".equals(ydCrnSchId)) {
				throw new Exception("크레인스케쥴ID가 없습니다.");
			} else if ("".equals(ydWbookId)) {
				throw new Exception("작업예약ID가 없습니다.");
			}

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER")));

			jrParam.setField("V_YD_CRN_SCH_ID", ydCrnSchId);
			jrParam.setField("V_YD_WBOOK_ID"  , ydWbookId );
			
			/**********************************************************
			* 1. 크레인스케쥴 정보 Check
			**********************************************************/
			JDTORecordSet jsCrnSch = jspDao.getCrnWrkMgt("SCSch", jrParam);

			if (jsCrnSch == null || jsCrnSch.size() <= 0) {
				throw new Exception("크레인스케쥴ID[" + ydCrnSchId + "]의 크레인스케줄 정보가 존재하지 않습니다.");
		    }

			JDTORecord jrCrnSch = jsCrnSch.getRecord(0);
			
		    String ydWrkProgStat = slabUtils.trim(jrCrnSch.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
		    String eqpUpdYn      = slabUtils.trim(jrCrnSch.getFieldString("EQP_UPD_YN"      )); //설비상태수정여부
		    String ydEqpId       = slabUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"       )); //야드설비ID
		    String ydEqpStat     = slabUtils.trim(jrCrnSch.getFieldString("YD_EQP_STAT"     )); //야드설비상태
		    String ydToLocGuide  = slabUtils.trim(jrCrnSch.getFieldString("YD_TO_LOC_GUIDE" )); //야드to위치guide
		    
		    String InterlockWrkYn = slabUtils.trim(jrCrnSch.getFieldString("INTERLOCK_WRK_YN" )); //인터락 구간 여부
			if ("2".equals(ydWrkProgStat)) {
				throw new Exception("크레인스케줄 [" + ydCrnSchId + "]의 작업진행상태가 [2:권상완료]이므로 취소하실 수 없습니다.");
			} else if ("3".equals(ydWrkProgStat)) {
				throw new Exception("크레인스케줄 [" + ydCrnSchId + "]의 작업진행상태가 [3:권하지시]이므로 취소하실 수 없습니다.");
			} else if ("4".equals(ydWrkProgStat)) {
				throw new Exception("크레인스케줄 [" + ydCrnSchId + "]의 작업진행상태가 [4:권하완료]이므로 취소하실 수 없습니다.");
			}
			
			//20240529 ora- 에러 교착상태 발생으로 인한 수정
			//크레인 권하실적은 eqp->stklyr->crnwrkmtl->crnsch 순인데, 스케줄취소는 stklyr->crnwrkmtl->crnsch->eqp 순으로 수정. 수정 순서 맞추기위해 올림.
			/**********************************************************
			* 5-> 1.5. 설비상태 수정 - 크레인이 고장 또는 Off-Line이 아니고 상태가 다르면
			**********************************************************/
			if ("Y".equals(eqpUpdYn)) {
				jrParam.setField("V_YD_EQP_ID"  , ydEqpId  ); //야드설비ID
				jrParam.setField("V_YD_EQP_STAT", ydEqpStat); //야드설비상태

				commDao.updStat("Eqp", jrParam);
			}
			
			/**********************************************************
			* 2. 작업진행상태가 [1:권상지시] 이면 작업지시취소 전문 전송
			**********************************************************/
			if ("1".equals(ydWrkProgStat)) {
				jrParam.setField("V_YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
				jrParam.setField("V_MSG_GP"       , "D"       ); //전문구분(취소)

				//크레인작업지시(YDY1L004, YDY3L004) 전문 조회
				jrRtn = slabUtils.addSndData(commDao.getMsgL2("YDY1L004", jrParam));
			}

			/**********************************************************
			* 3. 권상, 권하위치 원복 - 적치단, 적치Bed
			**********************************************************/
			//적치단 수정 - 권상위치(U -> C), 권하위치(D -> E)
			jspDao.updCrnWrkMgt("SCStkLyr", jrParam);

			//적치Bed 수정 - 완산Bed 해제
			jspDao.updCrnWrkMgt("SCStkBed", jrParam);
			
			/**********************************************************
			 * 3-1. 작업예약에 크레인스케쥴의 to위치 Guide 저장 (후판슬라브야드) 
			**********************************************************/
//////////////////////////////////////////
			 String APPLY_YN34   = PcommDao.PSlabApplyYn("APPLY_YN34");
//////////////////////////////////////////
	
	     if("Y".equals(APPLY_YN34)){
             
	        if(ydEqpId.startsWith("D")) {
				jrParam.setField("V_YD_WBOOK_ID" , ydWbookId);
				jrParam.setField("V_YD_TO_LOC_GUIDE", ydToLocGuide);
				
				jspDao.updCrnWrkMgt("WBtoLocGuide", jrParam);
			}
	     }
			/**********************************************************
			* 4. 크레인스케줄 삭제
			**********************************************************/
			//크레인작업재료 삭제
			jspDao.updCrnWrkMgt("SCCrnMtl", jrParam);
			
			//크레인스케줄 삭제
			jspDao.updCrnWrkMgt("SCCrnSch", jrParam);

			

			/**********************************************************
			* 6. 크레인 인터락 구간 작업 취소시 인터락 전문 l2로 전송
			* 	 인터락 ON시, 인터락구간 작업 하고있다면 그 작업 끝나고 보내기로(L2에서 인터락 전문 받으면 바로 좌표 제한 설정)
			**********************************************************/
			slabUtils.printLog(logId, "장비 ["+ydEqpId+"] 인터락 구간 작업 여부 ["+InterlockWrkYn+"]", "SL");
			
			if("Y".equals(InterlockWrkYn)){
				jrParam.setField("V_YD_EQP_ID", ydEqpId);
				jrParam.setField("C2CR_INTERLOCK_YN", "Y");
				jrRtn = slabUtils.addSndData(jrRtn,commDao.getMsgL2("YDY1L006", jrParam));
			}
			
			/**********************************************************
			* 7. 작업예약에 스케줄 취소 여부 업데이트
			**********************************************************/
			jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER")));
			jrParam.setField("MODIFIER", slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER")));
			jrParam.setField("YD_WBOOK_ID"  , ydWbookId );
			commDao.update(jrParam, "com.inisteel.cim.yd.slabyd.session.SlabYdJspSeEJB.updSchCNCLYn", logId, methodNm, "스케줄취소여부 업데이트");
			
			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}

	
	/**
	 *      [A] 오퍼레이션명 : 작업예약 취소처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtWrkBookCncl(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "작업예약 취소처리[SlabYdJspSeEJB.trtWrkBookCncl] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			String ydWbookId = slabUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID")); //야드작업예약ID
		    String ydEqpId   = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"  )); //야드설비ID
		    String ydSchCd   = slabUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"  )); //야드스케쥴코드
			String modifier  = slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER" )); //수정자
			
			if ("".equals(ydWbookId)) {
				throw new Exception("작업예약ID가 없습니다.");
			}

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, modifier);

			jrParam.setField("V_YD_WBOOK_ID", ydWbookId);
			
			/**********************************************************
			* 1. 크레인스케줄 존재여부 Check
			**********************************************************/
			JDTORecordSet jsCrnSch = jspDao.getComm("WbCrnSch", jrParam);

			if (jsCrnSch != null && jsCrnSch.size() > 0) {
				throw new Exception("작업예약ID[" + ydWbookId + "]의 크레인스케줄 정보가 " + jsCrnSch.size() + " 건 존재합니다.");
		    }

			/**********************************************************
			* 2. 준비스케줄 복원
			**********************************************************/
			//준비재료 복원
		    jspDao.updComm("PrepMtlRcvr", jrParam);
			
			//준비스케줄 복원
		    jspDao.updComm("PrepSchRcvr", jrParam);

			/**********************************************************
			* 3. 차량/대차스케줄 야드상차작업예약ID, 야드하차작업예약ID 삭제
			**********************************************************/
			//차량스케줄 야드상차작업예약ID, 야드하차작업예약ID 삭제
		    jspDao.updComm("CarSchWbDel", jrParam);
		
			//대차스케줄 야드상차작업예약ID, 야드하차작업예약ID 삭제
			jspDao.updComm("TcarSchWbDel", jrParam);

		    /**********************************************************
			* 4. 작업예약/재료 삭제
			**********************************************************/
			//작업예약재료 삭제
			commDao.updDelYn("WrkBookMtl", jrParam);

			//작업예약 삭제
			commDao.updDelYn("WrkBook", jrParam);
			
			/**********************************************************
			* 5. 크레인작업지시요구 전문 조회
			**********************************************************/
			//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
			JDTORecord jrYdMsg = slabUtils.getParam(logId, methodNm, modifier);

			jrYdMsg.setField("JMS_TC_CD"       , "Y1YDL007");	//크레인작업지시요구
			jrYdMsg.setField("YD_EQP_ID"       , ydEqpId   );	//야드설비ID
			jrYdMsg.setField("YD_WRK_PROG_STAT", "4"       );	//야드작업진행상태(권하완료)
			jrYdMsg.setField("YD_SCH_CD"       , ydSchCd   );	//야드스케쥴코드

			EJBConnector sndConn = new EJBConnector("default", "SlabYdL2RcvSeEJB", this);
			jrRtn = (JDTORecord)sndConn.trx("rcvY1YDL007", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });

			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}


	/***************************************************************************
	 * 크레인상태설정
	 **************************************************************************/
	
	/**
	 *      [A] 오퍼레이션명 : 크레인상태설정 조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getCrnStatSet(GridData gdReq) throws DAOException {
		String methodNm = "크레인상태설정 조회[SlabYdJspSeEJB.getCrnStatSet] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			JDTORecordSet jrRst = jspDao.getCrnStatSet(gdReq);
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			return slabUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 크레인상태설정 등록처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtCrnStatSet(GridData gdReq) throws DAOException {
		String methodNm = "크레인상태설정 등록처리[SlabYdJspSeEJB.trtCrnStatSet] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			String trtDtlGp = slabUtils.trim(gdReq.getParam("V_TRT_DTL_GP"));	//처리상세구분
			String ydEqpId  = slabUtils.trim(gdReq.getParam("V_YD_EQP_ID" ));	//야드설비ID(크레인)
			String currDate = slabUtils.getDateTime14();						//현재시각

			if ("".equals(ydEqpId)) {
				throw new Exception("크레인설비ID가 없습니다.");
			}

			//Return Value
			JDTORecord jrRtn = null;

			//EJB Call을 위한 Message 생성 - Log ID, Method, 수정자 Set
			JDTORecord jrYdMsg = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));

			jrYdMsg.setField("YD_EQP_ID", ydEqpId); //야드설비ID

			if ("ST".equals(trtDtlGp)) {
				//설비상태 변경
				jrYdMsg.setField("JMS_TC_CD"          , "C3YDL008"); //설비고장복구실적
				jrYdMsg.setField("YD_EQP_STAT"        , slabUtils.trim(gdReq.getParam("YD_EQP_STAT"))); //야드설비상태(B:고장, N:정상)
				jrYdMsg.setField("YD_EQP_PAUSE_CODE"  , "0000"    ); //야드설비휴지코드
				jrYdMsg.setField("YD_EQP_TRBL_RCVR_DT", currDate  ); //야드설비고장복구일시

				EJBConnector sndConn = new EJBConnector("default", "SlabYdL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvY1YDL004", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
			} else if ("MD".equals(trtDtlGp)) {
				//작업Mode 변경
				jrYdMsg.setField("JMS_TC_CD"      , "C3YDL009"); //설비운전모드전환
				jrYdMsg.setField("YD_EQP_WRK_MODE", slabUtils.trim(gdReq.getParam("YD_EQP_WRK_MODE"))); //야드설비작업Mode(1:On-Line, 0:Off-Line)

				EJBConnector sndConn = new EJBConnector("default", "SlabYdL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvY1YDL003", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
			} else if ("WO".equals(trtDtlGp)) {
				//명령선택기동
				jrYdMsg.setField("JMS_TC_CD"       , "Y1YDL007"); //크레인작업지시요구
				jrYdMsg.setField("YD_WRK_PROG_STAT", "W"       ); //야드작업진행상태(명령선택대기)
				jrYdMsg.setField("YD_SCH_CD"       , slabUtils.trim(gdReq.getHeader("YD_SCH_CD"     ).getValue(0))); //야드스케쥴코드
				jrYdMsg.setField("YD_CRN_SCH_ID"   , slabUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID" ).getValue(0))); //야드크레인스케쥴ID

				EJBConnector sndConn = new EJBConnector("default", "SlabYdL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvY1YDL007", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
			} else if ("WU".equals(trtDtlGp)) {
				//권상실적처리
				jrYdMsg.setField("JMS_TC_CD"       , "Y1YDL008"); //크레인권상실적
				jrYdMsg.setField("YD_EQP_WRK_MODE" , "9"       ); //야드설비작업Mode(Backup)
				jrYdMsg.setField("YD_WRK_PROG_STAT", "2"       ); //야드작업진행상태(권상완료)
				jrYdMsg.setField("YD_SCH_CD"       , slabUtils.trim(gdReq.getHeader("YD_SCH_CD"         ).getValue(0))); //야드스케쥴코드
				jrYdMsg.setField("YD_CRN_SCH_ID"   , slabUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID"     ).getValue(0))); //야드크레인스케쥴ID
				jrYdMsg.setField("YD_UP_WR_LOC"    , slabUtils.trim(gdReq.getHeader("YD_UP_WO_LOC"      ).getValue(0))); //야드권상실적위치
				jrYdMsg.setField("YD_UP_WR_LAYER"  , slabUtils.trim(gdReq.getHeader("YD_UP_WO_LAYER"    ).getValue(0))); //야드권상실적단
				jrYdMsg.setField("YD_CRN_XAXIS"    , slabUtils.trim(gdReq.getHeader("YD_UP_WO_LOC_XAXIS").getValue(0))); //야드크레인X축
				jrYdMsg.setField("YD_CRN_YAXIS"    , slabUtils.trim(gdReq.getHeader("YD_UP_WO_LOC_YAXIS").getValue(0))); //야드크레인Y축
				jrYdMsg.setField("YD_CRN_ZAXIS"    , slabUtils.trim(gdReq.getHeader("YD_UP_WO_LOC_ZAXIS").getValue(0))); //야드크레인Z축
				
				EJBConnector sndConn = new EJBConnector("default", "SlabYdL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvY1YDL008", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
			} else if ("WD".equals(trtDtlGp)) {
				//권하실적처리
				jrYdMsg.setField("JMS_TC_CD"       , "Y1YDL009"); //크레인권하실적
				jrYdMsg.setField("YD_EQP_WRK_MODE" , "9"       ); //야드설비작업Mode(Backup)
				jrYdMsg.setField("YD_WRK_PROG_STAT", "4"       ); //야드작업진행상태(권하완료)
				jrYdMsg.setField("YD_SCH_CD"       , slabUtils.trim(gdReq.getHeader("YD_SCH_CD"         ).getValue(0))); //야드스케쥴코드
				jrYdMsg.setField("YD_CRN_SCH_ID"   , slabUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID"     ).getValue(0))); //야드크레인스케쥴ID
				jrYdMsg.setField("YD_DN_WR_LOC"    , slabUtils.trim(gdReq.getHeader("YD_DN_WO_LOC"      ).getValue(0))); //야드권하실적위치
				jrYdMsg.setField("YD_DN_WR_LAYER"  , slabUtils.trim(gdReq.getHeader("YD_DN_WO_LAYER"    ).getValue(0))); //야드권하실적단
				jrYdMsg.setField("YD_CRN_XAXIS"    , slabUtils.trim(gdReq.getHeader("YD_DN_WO_LOC_XAXIS").getValue(0))); //야드크레인X축
				jrYdMsg.setField("YD_CRN_YAXIS"    , slabUtils.trim(gdReq.getHeader("YD_DN_WO_LOC_YAXIS").getValue(0))); //야드크레인Y축
				jrYdMsg.setField("YD_CRN_ZAXIS"    , slabUtils.trim(gdReq.getHeader("YD_DN_WO_LOC_ZAXIS").getValue(0))); //야드크레인Z축
				
				EJBConnector sndConn = new EJBConnector("default", "SlabYdL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvY1YDL009", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
			} else if ("DL".equals(trtDtlGp)) {
				//권하위치변경
				jrYdMsg.setField("YD_WRK_PROG_STAT", slabUtils.trim(gdReq.getHeader("YD_WRK_PROG_STAT").getValue(0))); //야드작업진행상태
				jrYdMsg.setField("YD_SCH_CD"       , slabUtils.trim(gdReq.getHeader("YD_SCH_CD"       ).getValue(0))); //야드스케쥴코드
				jrYdMsg.setField("YD_CRN_SCH_ID"   , slabUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID"   ).getValue(0))); //야드크레인스케쥴ID
				jrYdMsg.setField("YD_WBOOK_ID"     , slabUtils.trim(gdReq.getHeader("YD_WBOOK_ID"     ).getValue(0))); //야드작업예약ID
				jrYdMsg.setField("YD_DN_WO_LOC"    , slabUtils.trim(gdReq.getParam("YD_DN_WO_LOC"))); //야드권하지시위치(신규)

				jrRtn = this.updCrnSchDnWoLoc(jrYdMsg);
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtDtlGp + "] 입니다.");
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}


	/**
	 *      [A] 오퍼레이션명 : 공장휴지계획 조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getCrnFixPlan(GridData gdReq) throws DAOException {
		String methodNm = "공장휴지계획 조회[SlabYdJspSeEJB.getCrnFixPlan] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			
			slabUtils.printLog(logId, methodNm, "S+", gdReq);
			
			JDTORecordSet jrRst = jspDao.getCrnFixPlan(gdReq);
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			
			slabUtils.printLog(logId, methodNm, "S-");
			
			return slabUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인보수계획관리 조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getCcCrnFixPlanMgt(GridData gdReq) throws DAOException {
		String methodNm = "크레인보수계획관리 조회[SlabYdJspSeEJB.getCcCrnFixPlanMgt] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			
			slabUtils.printLog(logId, methodNm, "S+", gdReq);
			
			JDTORecordSet jrRst = jspDao.getCcCrnFixPlan(gdReq);
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			
			slabUtils.printLog(logId, methodNm, "S-");
			
			return slabUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인 보수계획 수정
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData trtCcCrnFixPlanUpd(GridData gdReq) throws DAOException {
		String methodNm = "크레인 보수계획 수정[SlabYdJspSeEJB.trtCcCrnFixPlanUpd] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			
			slabUtils.printLog(logId, methodNm, "S+", gdReq);
			jspDao.updCrnFixPlan(gdReq);
			
			slabUtils.printLog(logId, methodNm, "S-");

			return this.getCcCrnFixPlanMgt(gdReq);
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인 보수계획 삭제
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData trtCcCrnFixPlanDel(GridData gdReq) throws DAOException {
		String methodNm = "크레인 보수계획 삭제[SlabYdJspSeEJB.trtCcCrnFixPlanDel] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			
			slabUtils.printLog(logId, methodNm, "S+", gdReq);
			jspDao.delCrnFixPlan(gdReq);
			
			slabUtils.printLog(logId, methodNm, "S-");

			return this.getCcCrnFixPlanMgt(gdReq);
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 공장 별 보수명 조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData trtMaintName(GridData gdReq) throws DAOException {
		String methodNm = "공장 별 보수명 조회[SlabYdJspSeEJB.trtMaintName] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			
			slabUtils.printLog(logId, methodNm, "S+", gdReq);
			
			JDTORecordSet jrRst = jspDao.getMaintName(gdReq);
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			
			slabUtils.printLog(logId, methodNm, "S-");
			
			return slabUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 공장 휴지계획 등록
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData insPausePlan(GridData gdReq) throws DAOException {
		String methodNm = "공장 휴지계획 등록[SlabYdJspSeEJB.insPausePlan] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			
			slabUtils.printLog(logId, methodNm, "S+", gdReq);
			jspDao.insPausePlan(gdReq);
			
			slabUtils.printLog(logId, methodNm, "S-");

			return this.trtMaintName(gdReq);
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 공장 휴지계획 삭제
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData delPausePlan(GridData gdReq) throws DAOException {
		String methodNm = "공장 휴지계획 삭제[SlabYdJspSeEJB.delPausePlan] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			
			slabUtils.printLog(logId, methodNm, "S+", gdReq);
			jspDao.delPausePlan(gdReq);
			
			slabUtils.printLog(logId, methodNm, "S-");

			return this.getCrnFixPlan(gdReq);
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 공장 휴지계획 완료 등록
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData updPausePlan(GridData gdReq) throws DAOException {
		String methodNm = "공장 휴지계획 완료 등록[SlabYdJspSeEJB.updPausePlan] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			
			slabUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//휴지계획 연주실적여부,실적일자 등록
			jspDao.updPausePlan(gdReq);
			
			//와이어보수이력 기존 실적 삭제
			jspDao.updWireMaintHist(gdReq);
			//와이어보수이력 등록
			jspDao.insWireMaintHist(gdReq);
			
			slabUtils.printLog(logId, methodNm, "S-");

			return this.getCrnFixPlan(gdReq);
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인별 휴지코드 조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData trtPauseCode(GridData gdReq) throws DAOException {
		String methodNm = "크레인별 휴지코드 조회[SlabYdJspSeEJB.trtPauseCode] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			
			slabUtils.printLog(logId, methodNm, "S+", gdReq);
			
			JDTORecordSet jrRst = jspDao.getPauseCode(gdReq);
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			
			slabUtils.printLog(logId, methodNm, "S-");
			
			return slabUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/***************************************************************************
	 * 압연지시조회
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 압연지시조회 조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getMillWoInq(GridData gdReq) throws DAOException {
		String methodNm = "압연지시조회[SlabYdJspSeEJB.getMillWoInq] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			JDTORecordSet jrRst = jspDao.getMillWoInq(gdReq);
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			return slabUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 압연지시조회 조회2
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getMillWoInq2(GridData gdReq) throws DAOException {
		String methodNm = "압연지시조회[SlabYdJspSeEJB.getMillWoInq2] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			JDTORecordSet jrRst = jspDao.getMillWoInq2(gdReq);
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			return slabUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 압연지시일별조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getMillWoInq3(GridData gdReq) throws DAOException {
		String methodNm = "압연지시일별조회[SlabYdJspSeEJB.getMillWoInq3] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			JDTORecordSet jrRst = jspDao.getMillWoInq3(gdReq);
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			return slabUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 후판예정압연조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getMillWoInqSCH(GridData gdReq) throws DAOException {
		String methodNm = "후판예정압연조회[SlabYdJspSeEJB.getMillWoInqSCH] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			JDTORecordSet jrRst = jspDao.getMillWoInqSCH(gdReq);
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			return slabUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 후판 압연지시 메세지 조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getMillWoInqMsg(GridData gdReq) throws DAOException {
		String methodNm = "후판압연지시메세지조회[SlabYdJspSeEJB.getMillWoInqMsg] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			JDTORecordSet jrRst = jspDao.getMillWoInqMsg(gdReq);
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			return slabUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	/**
	 *      [A] 오퍼레이션명 : 후판 압연지시 메세지 조회 by SEQNO
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getMillWoInqMsgBySeqNo(GridData gdReq) throws DAOException {
		String methodNm = "후판압연지시메세지조회[SlabYdJspSeEJB.getMillWoInqMsgBySeqNo] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			JDTORecordSet jrRst = jspDao.getMillWoInqMsgBySeqNo(gdReq);
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			return slabUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	/**
	 *      [A] 오퍼레이션명 : 후판 압연지시 메세지 조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getMillWoInqMsgAll(GridData gdReq) throws DAOException {
		String methodNm = "후판압연지시메세지이력조회[SlabYdJspSeEJB.getMillWoInqMsgAll] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			JDTORecordSet jrRst = jspDao.getMillWoInqMsgAll(gdReq);
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			return slabUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 후판 압연지시 메세지 확인
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData updMillWoInqMsg(GridData gdReq) throws DAOException {
		String methodNm = "후판압연지시메세지확인[SlabYdJspSeEJB.updMillWoInqMsg] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			JDTORecordSet jrRst = null;
			List list = null;
			int result = jspDao.updMillWoInqMsg(gdReq);
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			return slabUtils.jdtoRecordToGridData(gdRtn,list, gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 압연지시조회 Dummy이적지시
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtMillWoInqDM(GridData gdReq) throws DAOException {
		String methodNm = "압연지시조회 Dummy이적지시[SlabYdJspSeEJB.trtMillWoInqDM] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			String ydStrLoc   = ""; //야드저장위치
			String ydStkLyrNo = ""; //야드적치단번호
			String ydSchCd    = ""; //야드스케쥴코드

			JDTORecordSet jsMsg = JDTORecordFactory.getInstance().createRecordSet("");	//크레인스케줄 정리를 위함
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));

			//Dummy이적지시 대상
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {
				ydStrLoc   = slabUtils.trim(gdReq.getHeader("YD_STR_LOC"   ).getValue(ii));		//야드저장위치
				ydStkLyrNo = slabUtils.trim(gdReq.getHeader("YD_STK_LYR_NO").getValue(ii));		//야드적치단번호
				ydSchCd    = ydStrLoc.substring(0, 2) + "YD" + ydStrLoc.substring(2, 4) + "MM";	//야드스케쥴코드
				
				jrParam.setField("V_YD_SCH_CD"    , ydSchCd                 ); //야드스케쥴코드
				jrParam.setField("V_YD_STK_COL_GP", ydStrLoc.substring(0, 6)); //야드적치열구분
				jrParam.setField("V_YD_STK_BED_NO", ydStrLoc.substring(6, 8)); //야드적치Bed번호
				jrParam.setField("V_YD_STK_LYR_NO", ydStkLyrNo              ); //야드적치단번호

				//작업예약 대상재료 조회
				JDTORecordSet jsWbMtl = jspDao.getMvStkWrkBookReg("DmMtl", jrParam);
				
				if (jsWbMtl.size() > 0) {
					//작업예약등록
					jsMsg.addRecord(this.insMvstkWrkBook(jrParam, jsWbMtl));
				}
			}
			
			//크레인 상태에 따라 크레인별 1개의 스케줄만 전송
			JDTORecord jrRtn = this.setCrnSchMsg(jsMsg, logId, methodNm);

			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}


	/**
	 *      [A] 오퍼레이션명 : 압연지시조회 보급Lot등록
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtMillWoInqCL(GridData gdReq) throws DAOException {
		String methodNm = "압연지시조회 보급Lot등록[SlabYdJspSeEJB.trtMillWoInqCL] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			String ydSchCd  = slabUtils.trim(gdReq.getParam("V_YD_SCH_CD"));	//야드스케쥴코드
			String modifier = slabUtils.trim(gdReq.getParam("userid"     ));	//수정자

			if ("".equals(ydSchCd)) {
				throw new Exception("스케쥴코드가 없습니다.");
			}

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, modifier);

			//야드스케쥴코드 Check
			jrParam.setField("V_YD_SCH_CD", ydSchCd); //야드스케쥴코드

			//야드스케쥴금지유무 조회
			JDTORecordSet jsChk = commDao.getStat("SchCd", jrParam);

			if (jsChk.size() > 0) {
				String ydSchProhExn = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_SCH_PROH_EXN"));	//야드스케쥴금지유무

				//스케줄 금지여부 Check
				if ("Y".equals(ydSchProhExn)) {
					throw new Exception("스케쥴코드[" + ydSchCd + "]는 기동금지 상태입니다.");
				}
			} else {
				throw new Exception("스케쥴코드[" + ydSchCd + "]의 정보가 없습니다.");
			}

			//야드준비스케쥴ID 생성
			String ydPrepSchId = commDao.getSeqId(logId, methodNm, "PrepSch");

			jrParam.setField("V_YD_PREP_SCH_ID", ydPrepSchId); //야드준비스케쥴ID

			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			//선택된 대상재로 Lot편성
			if (rowCnt > 0) {
				String ydStrLoc = "";	//야드저장위치
				String[][] arrMtl = new String[rowCnt][7];
				
				for (int ii = 0; ii < rowCnt; ii++) {
					ydStrLoc = slabUtils.trim(gdReq.getHeader("YD_STR_LOC").getValue(ii));	//야드저장위치
					
					arrMtl[ii][0] = ydPrepSchId;													//야드준비스케쥴ID
					arrMtl[ii][1] = slabUtils.trim(gdReq.getHeader("STL_NO").getValue(ii));			//재료번호
					arrMtl[ii][2] = modifier;														//수정자
					arrMtl[ii][3] = modifier;														//수정자
					arrMtl[ii][4] = ydStrLoc.substring(0, 6);										//야드적치열구분
					arrMtl[ii][5] = ydStrLoc.substring(6, 8);										//야드적치Bed번호
					arrMtl[ii][6] = slabUtils.trim(gdReq.getHeader("YD_STK_LYR_NO").getValue(ii));	//야드적치단번호
				}
				
				//준비재료 등록
				commDao.upsBatch("PrepMtl", arrMtl, logId, methodNm);

				//준비스케쥴 등록
				commDao.insSlabYd("PrepSch", jrParam);
			} else {
				throw new Exception("보급Lot등록할 재료가 없습니다.");
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}

	
	/***************************************************************************
	 * 크레인작업예약관리
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 크레인작업예약관리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getCrnWrkBookMgt(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업예약관리[SlabYdJspSeEJB.getCrnWrkBookMgt] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			JDTORecordSet jrRst = jspDao.getCrnWrkBookMgt(gdReq);
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			return slabUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업예약관리 스케줄점검
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return String
	 *      @throws DAOException
	*/
	public String chkCrnWrkBookMgtSC(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업예약관리 스케줄점검[SlabYdJspSeEJB.trtCrnWrkBookMgtSC] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", "chkCrnWrkBookMgtSC => 크레인작업예약관리 스케줄점검", "APPPI0", "S", "*");			
			
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			//점검용 변수
			String ydWbookId       = "";	//야드작업예약ID
			String ydSchCd         = "";	//야드스케쥴코드
			String ydToLocGuide    = "";	//야드To위치Guide
			String toLocChkGp      = "";	//To위치 점검을 위한 구분(G:To위치Guide, C:차량상차, T:대차상차, E:설비불출, Z:기타)
			String toLocChkRst     = "";	//To위치점검결과
			String ydAimRtGp       = "";	//야드목표행선구분
			String ydCarUseGp      = "";	//야드차량사용구분
			String trnEqpCd        = "";	//운송장비코드
			String carNo           = "";	//차량번호
			String cardNo          = "";	//카드번호
			String ydSchProhExn    = "";	//야드스케쥴금지유무
			String ydWrkPlanCrn    = "";	//야드작업계획크레인
			String ydEqpStatPln    = "";	//야드설비상태(작업계획크레인)
			String ydEqpWrkModePln = "";	//야드설비작업Mode(작업계획크레인)
			String ydWrkCrn        = "";	//야드작업크레인
			String ydEqpStatWrk    = "";	//야드설비상태(작업크레인)
			String ydEqpWrkModeWrk = "";	//야드설비작업Mode(작업크레인)
			String ydAltCrn        = "";	//야드대체크레인
			String ydEqpStatAlt    = "";	//야드설비상태(대체크레인)
			String ydEqpWrkModeAlt = "";	//야드설비작업Mode(대체크레인)
			String ydCurrBayGp     = "";	//야드현재동구분(대차)
			String cmDupYn         = "";	//크레인스케줄 재료중복여부
			String clDupGp         = "";	//크레인스케줄 저장위치중복여부
			int ttMtlSh            = 0;		//전체 재료매수
			int wmMtlSh            = 0;		//작업예약 재료매수
			int stMtlSh            = 0;		//저장품 재료매수
			int slMtlSh            = 0;		//적치단 재료매수
			int statCSh            = 0;		//적치중인 재료매수(야드적치단재료상태가 적치 중[C] 인 재료수)
			int abLocSh            = 0;		//저장위치이상 재료매수
			
			String trtGp           = "";	//처리구분
			String trtMsg          = "";	//처리메세지
			boolean chkRst         = false;	//점검결과
			StringBuffer sbMsg     = new StringBuffer();	//점검Message
			
			String interlockWrkYn = "N";    //인터락 구간 작업 여부
			String upperWrkYn = "N"; //상단재료 작업 여부
			String narrowSlabYn = "N"; //협폭슬라브 2후판 4,5픽업 보급 여부
			
			String planCrnWrkAbleYn = "N";
			String wrkCrnWrkAbleYn = "N";
			String artCrnWrkAbleYn = "N";
			
			//조회결과
			JDTORecordSet jsChk = null;
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, "");
			
			//작업예약
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {
				ydWbookId = slabUtils.trim(gdReq.getHeader("YD_WBOOK_ID").getValue(ii));

				chkRst = true;	//점검결과
				sbMsg  = sbMsg.append((ii + 1) + ". 작업예약ID - " + ydWbookId + "\n");

				//크레인스케줄 상태정보 조회
				jrParam.setField("V_YD_WBOOK_ID", ydWbookId); //야드작업예약ID
				jsChk = schDao.getYDYDJ400("Stat", jrParam);

				if (jsChk.size() <= 0) {
					chkRst = false;
					sbMsg  = sbMsg.append("   ▶ 오류 > 작업예약 정보 없음\n\n");
					continue;
				} else {
					JDTORecord jrChk = jsChk.getRecord(0);

					ydSchCd         = slabUtils.trim(jrChk.getFieldString("YD_SCH_CD"          ));	//야드스케쥴코드
					ydToLocGuide    = slabUtils.trim(jrChk.getFieldString("YD_TO_LOC_GUIDE"    ));	//야드To위치Guide
					toLocChkGp      = slabUtils.trim(jrChk.getFieldString("TO_LOC_CHK_GP"      ));	//To위치점검구분
					ydAimRtGp       = slabUtils.trim(jrChk.getFieldString("YD_AIM_RT_GP"       ));	//야드목표행선구분
					ydCarUseGp      = slabUtils.trim(jrChk.getFieldString("YD_CAR_USE_GP"      ));	//야드차량사용구분
				    trnEqpCd        = slabUtils.trim(jrChk.getFieldString("TRN_EQP_CD"         ));	//운송장비코드
					carNo           = slabUtils.trim(jrChk.getFieldString("CAR_NO"             ));	//차량번호
					cardNo          = slabUtils.trim(jrChk.getFieldString("CARD_NO"            ));	//카드번호
					ydSchProhExn    = slabUtils.trim(jrChk.getFieldString("YD_SCH_PROH_EXN"    ));	//야드스케쥴금지유무
					ydWrkPlanCrn    = slabUtils.trim(jrChk.getFieldString("YD_WRK_PLAN_CRN"    ));	//야드작업계획크레인
					ydEqpStatPln    = slabUtils.trim(jrChk.getFieldString("YD_EQP_STAT_PLN"    ));	//야드설비상태(작업계획크레인)
					ydEqpWrkModePln = slabUtils.trim(jrChk.getFieldString("YD_EQP_WRK_MODE_PLN"));	//야드설비작업Mode(작업계획크레인)
					ydWrkCrn        = slabUtils.trim(jrChk.getFieldString("YD_WRK_CRN"         ));	//야드작업크레인
					ydEqpStatWrk    = slabUtils.trim(jrChk.getFieldString("YD_EQP_STAT_WRK"    ));	//야드설비상태(작업크레인)
					ydEqpWrkModeWrk = slabUtils.trim(jrChk.getFieldString("YD_EQP_WRK_MODE_WRK"));	//야드설비작업Mode(작업크레인)
					ydAltCrn        = slabUtils.trim(jrChk.getFieldString("YD_ALT_CRN"         ));	//야드대체크레인
					ydEqpStatAlt    = slabUtils.trim(jrChk.getFieldString("YD_EQP_STAT_ALT"    ));	//야드설비상태(대체크레인)
					ydEqpWrkModeAlt = slabUtils.trim(jrChk.getFieldString("YD_EQP_WRK_MODE_ALT"));	//야드설비작업Mode(대체크레인)
					ydCurrBayGp     = slabUtils.trim(jrChk.getFieldString("YD_CURR_BAY_GP"     ));	//야드현재동구분(대차)
					cmDupYn         = slabUtils.trim(jrChk.getFieldString("CM_DUP_YN"          ));	//크레인스케줄 재료중복여부
					clDupGp         = slabUtils.trim(jrChk.getFieldString("CL_DUP_GP"          ));	//크레인스케줄 저장위치중복여부
					ttMtlSh         = Integer.parseInt(slabUtils.nvl(jrChk.getFieldString("TT_MTL_SH"),"0"));	//전체 재료매수
					wmMtlSh         = Integer.parseInt(slabUtils.nvl(jrChk.getFieldString("WM_MTL_SH"),"0"));	//작업예약 재료매수
					stMtlSh         = Integer.parseInt(slabUtils.nvl(jrChk.getFieldString("ST_MTL_SH"),"0"));	//저장품 재료매수
					slMtlSh         = Integer.parseInt(slabUtils.nvl(jrChk.getFieldString("SL_MTL_SH"),"0"));	//적치단 재료매수
					statCSh         = Integer.parseInt(slabUtils.nvl(jrChk.getFieldString("STAT_C_SH"),"0"));	//적치중인 재료매수(야드적치단재료상태가 적치 중[C] 인 재료수)
					abLocSh         = Integer.parseInt(slabUtils.nvl(jrChk.getFieldString("AB_LOC_SH"),"0"));	//저장위치이상 재료매수
					
					interlockWrkYn         = slabUtils.nvl(jrChk.getFieldString("INTERLOCK_WRK_YN"),"N");	//인터락 구간 작업 여부
					upperWrkYn             = slabUtils.nvl(jrChk.getFieldString("UPPER_WRK_YN"),"N");//상단재료 작업예약 여부
					narrowSlabYn           = slabUtils.nvl(jrChk.getFieldString("NARROW_SLAB_YN"),"N");//협폭슬라브 2후판 4,5픽업 보급 여부
					
					planCrnWrkAbleYn	    = slabUtils.nvl(jrChk.getFieldString("PLAN_CRN_WRK_ABLE_YN"),"N");//작업계획크레인 작업예약재료 작업 가능 여부
					wrkCrnWrkAbleYn	   		= slabUtils.nvl(jrChk.getFieldString("WRK_CRN_WRK_ABLE_YN"),"N");//작업크레인 작업예약재료 작업 가능 여부
					artCrnWrkAbleYn	   		= slabUtils.nvl(jrChk.getFieldString("ALT_CRN_WRK_ABLE_YN"),"N");//대체크레인 작업예약재료 작업 가능 여부
					
					slabUtils.printLog(logId, trtMsg + "인터락구역작업여부[" + interlockWrkYn + "], 상단재료 작업예약 여부 [" + upperWrkYn + "]", "SL");
					
					
					//스케쥴코드 Check
					if ("".equals(ydSchProhExn)) {
						chkRst = false;
						sbMsg  = sbMsg.append("   ▶ 오류 > 스케쥴코드[" + ydSchCd + "] 정보 없음\n");
					} else if ("Y".equals(ydSchProhExn)) {
						chkRst = false;
						sbMsg  = sbMsg.append("   ▶ 오류 > 스케쥴코드[" + ydSchCd + "] 기동금지\n");
					} else {
						sbMsg  = sbMsg.append("   ▷ 스케쥴코드[" + ydSchCd + "] 기동가능\n");
					}
					
					//크레인 Check
					if (!"".equals(ydWrkPlanCrn) && !"B".equals(ydEqpStatPln) && "1".equals(ydEqpWrkModePln) && "Y".equals(planCrnWrkAbleYn) ) {
						//작업예약 지정크레인 : 최우선 지정
						sbMsg  = sbMsg.append("   ▷ 작업예약 지정크레인[" + ydWrkPlanCrn + ", " + ydEqpStatPln + "] 정상\n");
					} else if (!"".equals(ydWrkCrn) && !"B".equals(ydEqpStatWrk) && "1".equals(ydEqpWrkModeWrk) && "Y".equals(wrkCrnWrkAbleYn)) {
						//주작업크레인
						sbMsg  = sbMsg.append("   ▷ 주작업 크레인[" + ydWrkCrn + ", " + ydEqpStatWrk + "] 정상\n");
					} else {
						//보조작업크레인 : 주작업크레인이 고장이거나 Off-Line 이면
						trtMsg = "주작업 크레인[" + ydWrkCrn + "] ";
						
						if ("".equals(ydWrkCrn)) {
							trtMsg = trtMsg + "정보 없음";
						} else if ("B".equals(ydEqpStatWrk)) {
							trtMsg = trtMsg + "고장";
						} else if (!"1".equals(ydEqpWrkModeWrk)) {
							trtMsg = trtMsg + "Off-Line";
						} else if ("N".equals(wrkCrnWrkAbleYn)){
							trtMsg = trtMsg + "작업예약 재료 작업 불가";
						}
						
						if ("".equals(ydAltCrn)) {
							chkRst = false;
							sbMsg  = sbMsg.append("   ▶ 오류 > " + trtMsg + ", 보조작업 크레인 정보 없음\n");
						} else if ("B".equals(ydEqpStatAlt)) {
							chkRst = false;
							sbMsg  = sbMsg.append("   ▶ 오류 > " + trtMsg + ", 보조작업 크레인[" + ydAltCrn + "] 고장\n");
						} else if (!"1".equals(ydEqpWrkModeAlt)) {
							chkRst = false;
							sbMsg  = sbMsg.append("   ▶ 오류 > " + trtMsg + ", 보조작업 크레인[" + ydAltCrn + "] Off-Line\n");
						} else if ("N".equals(artCrnWrkAbleYn)){
							chkRst = false;
							sbMsg  = sbMsg.append("   ▶ 오류 > " + trtMsg + ", 보조작업 크레인[" + ydAltCrn + "] 작업예약 재료 작업 불가\n");
						}
						else {
						
							sbMsg  = sbMsg.append("   ▷ " + trtMsg + "이므로 보조작업 크레인[" + ydAltCrn + ", " + ydEqpStatAlt + "]으로 대체\n");
						}
					}

					//작업예약재료 Check
					if (wmMtlSh == 0) {
						chkRst = false;
						sbMsg  = sbMsg.append("   ▶ 오류 > 작업예약재료 정보 없음\n");
					} else if (wmMtlSh != ttMtlSh) {
						chkRst = false;
						sbMsg  = sbMsg.append("   ▶ 오류 > 작업예약재료 적치단 중복 등록 [작업예약:" + wmMtlSh + ", 적치단:" + ttMtlSh + "]\n");
					} else if (wmMtlSh != slMtlSh) {
						chkRst = false;
						sbMsg  = sbMsg.append("   ▶ 오류 > 작업예약재료 적치단 정보 이상 [" + (wmMtlSh - slMtlSh) + "매]\n");
					} else if (wmMtlSh != statCSh) {
						chkRst = false;
						sbMsg  = sbMsg.append("   ▶ 오류 > 작업예약재료 적치중[C]이 아님 [" + (wmMtlSh - statCSh) + "매]\n");
					} else if (wmMtlSh != stMtlSh) {
						chkRst = false;
						sbMsg  = sbMsg.append("   ▶ 오류 > 작업예약재료 저장품 정보 이상 [" + (wmMtlSh - stMtlSh) + "매]\n");
					} else if (abLocSh > 0) {
						chkRst = false;
						sbMsg  = sbMsg.append("   ▶ 오류 > 작업예약재료 현재위치 이상 [" + abLocSh + "매]\n");
					} else {
						sbMsg  = sbMsg.append("   ▷ 작업예약재료[" + wmMtlSh + "매] 적치단 및 저장품 정보 정상\n");
					}
					
					//기 등록 크레인스케줄 Check
					if ("Y".equals(cmDupYn)) {
						chkRst = false;
						sbMsg  = sbMsg.append("   ▶ 오류 > 작업예약재료가 기 등록된 크레인작업재료와 중복\n");
					} else if ("1".equals(clDupGp)) {
						chkRst = false;
						sbMsg  = sbMsg.append("   ▶ 오류 > 작업예약재료의 현재위치가 기 등록된 크레인스케쥴 권하위치와 중복\n");
					} else if ("2".equals(clDupGp)) {
						chkRst = false;
						sbMsg  = sbMsg.append("   ▶ 오류 > 작업예약재료의 현재위치가 기 등록된 크레인스케쥴 권상위치와 중복\n");
					} else {
						sbMsg = sbMsg.append("   ▷ 작업예약재료[" + wmMtlSh + "매] 크레인스케쥴 등록여부 및 현재위치 점검 정상\n");
					}
					
					if ("Y".equals(interlockWrkYn)) {
						chkRst = false;
						sbMsg = sbMsg.append("   ▶ 오류 > 작업예약재료의 권상위치 혹은 To위치가 크레인 Interlock 구간\n");
					}
					
					if("Y".equals(upperWrkYn)){
						chkRst = false;
						sbMsg = sbMsg.append("   ▶ 오류 > 작업예약재료 상단에 다른 작업예약 걸려있는 재료 존재\n");
					}
					
					if("Y".equals(narrowSlabYn)){
						chkRst = false;
						sbMsg = sbMsg.append("   ▶ 오류 > : 협폭 슬라브 2후판 4,5번 픽업 Bed 사용 불가\n");
					}
				}

				//To위치 Check
				trtGp = "";
				if ("G".equals(toLocChkGp)) {
					//야드To위치Guide 값이 있고 작업 야드동이 같을 경우 야드To위치Guide로 
					//PU, DP, PI 불출위치에 재료가 있거나, 단수, 중량 초과이면 불가
					trtGp  = "ToLocGuide";
					trtMsg = "To위치점검[To위치지정 : " + ydToLocGuide + "]";
				} else if ("C".equals(toLocChkGp)) {
					//차량상차(__PT__UM)일 경우 적치가능 차량이 없으면 불가
					if ("".equals(ydCarUseGp)) {
						chkRst = false;
						sbMsg  = sbMsg.append("   ▶ 오류 > To위치점검[차량상차 : " + ydSchCd + "] 야드차량사용구분 없음\n");
					} else if ("L".equals(ydCarUseGp)) {
						if ("".equals(trnEqpCd)) {
							chkRst = false;
							sbMsg  = sbMsg.append("   ▶ 오류 > To위치점검[구내운송 차량상차 : " + ydSchCd + "] 운송장비코드 없음\n");
						} else {
							trtGp  = "ToLocCar";
							trtMsg = "To위치점검[구내운송 차량상차 : " + ydSchCd + ", " + trnEqpCd + "]";
						}
					} else if ("G".equals(ydCarUseGp)) {

						//PIDEV		
//						if("Y".equals(sApplyYnPI)) {
						
							if ("".equals(carNo)) {
								chkRst = false;
								sbMsg  = sbMsg.append("   ▶ 오류 > To위치점검[출하 차량상차 : " + ydSchCd + "] 차량번호 \n");
							} else {
								trtGp  = "ToLocCar";
								trtMsg = "To위치점검[출하 차량상차 : " + ydSchCd + ", " + carNo + "-" + cardNo + "]";
							}
							
//						} else {
//							
//							if ("".equals(carNo) || "".equals(cardNo)) {
//								chkRst = false;
//								sbMsg  = sbMsg.append("   ▶ 오류 > To위치점검[출하 차량상차 : " + ydSchCd + "] 차량번호 \n");
//							} else {
//								trtGp  = "ToLocCar";
//								trtMsg = "To위치점검[출하 차량상차 : " + ydSchCd + ", " + carNo + "-" + cardNo + "]";
//							}		
//							
//						}
						

					}
				} else if ("T".equals(toLocChkGp)) {
					//대차상차작업
					if (!ydCurrBayGp.equals(ydSchCd.substring(1, 2))) {
						chkRst = false;
						sbMsg  = sbMsg.append("   ▶ 오류 > To위치점검[대차상차 : " + ydSchCd + "] 현재동 이상 [" + ydSchCd.substring(0, 1) + "XTC" + ydSchCd.substring(4, 6) + " : " + ydCurrBayGp + "]\n");
					}
				} else if ("E".equals(toLocChkGp)) {
					//스케줄코드 및 행선구분으로 To위치 점검
					//불출(__PU__U_, __DP__U_)이고 위치검색범위에 적치매수 0인 Bed가 없으면 불가
					if ("".equals(ydAimRtGp)) {
						chkRst = false;
						sbMsg  = sbMsg.append("   ▶ 오류 > To위치점검[설비보급 : " + ydSchCd + "] 야드목표행선구분 없음\n");
					} else {
						trtGp  = "ToLocExt";
						trtMsg = "To위치점검[설비보급 : " + ydSchCd + ", " + ydAimRtGp + "]";
					}
				} else if ("Z".equals(toLocChkGp)) {
					//스케줄코드 및 행선구분으로 To위치 점검
					if ("".equals(ydAimRtGp)) {
						chkRst = false;
						sbMsg  = sbMsg.append("   ▶ 오류 > To위치점검[기타 : " + ydSchCd + "] 야드목표행선구분 없음\n");
					} else {
						trtGp  = "ToLocEtc";
						trtMsg = "To위치점검[기타 : " + ydSchCd + ", " + ydAimRtGp + "]";
					}
				}
				
				if (!"".equals(trtGp)) {
					toLocChkRst = ""; //To위치점검결과

					jsChk = schDao.getYDYDJ400(trtGp, jrParam);
					
					if (jsChk.size() > 0) {
						toLocChkRst = slabUtils.trim(jsChk.getRecord(0).getFieldString("TO_LOC_CHK_RST"));
					} else {
						toLocChkRst = toLocChkGp + "1";
					}
		
					if ("G1".equals(toLocChkRst)) {
						//TakeInBed1매선보급요구여부가 'Y'이면 장입보급  공Bed가 없더라도 스케줄 생성 가능
						if ("Y".equals(slabUtils.trim(jsChk.getRecord(0).getFieldString("TI_PRE_SUP_YN")))) {
							sbMsg = sbMsg.append("   ▷ " + trtMsg + " 선보급요구\n");
						} else {
							chkRst = false;
							sbMsg  = sbMsg.append("   ▶ 오류 > " + trtMsg + " To위치 적치가능 Bed 없음\n");
						}
					} else if ("G2".equals(toLocChkRst)) {
						chkRst = false;
						sbMsg  = sbMsg.append("   ▶ 오류 > " + trtMsg + " To위치 Max단 초과\n");
					} else if ("G3".equals(toLocChkRst)) {
						chkRst = false;
						sbMsg  = sbMsg.append("   ▶ 오류 > " + trtMsg + " To위치 Max중량 초과\n");
					} else if ("G4".equals(toLocChkRst)) {
						chkRst = false;
						sbMsg  = sbMsg.append("   ▶ 오류 > " + trtMsg + " To위치 Max높이 초과\n");
					} else if ("G5".equals(toLocChkRst)) {
						chkRst = false;
						sbMsg  = sbMsg.append("   ▶ 오류 > " + trtMsg + " To위치에 적치된 재료 있음\n");
					} else if ("G6".equals(toLocChkRst)) {
						chkRst = false;
						sbMsg  = sbMsg.append("   ▶ 오류 > " + trtMsg + " 2차절단 #1,#3 협폭재 장입 불가\n");
					} else if ("G7".equals(toLocChkRst)) {
						chkRst = false;
						sbMsg  = sbMsg.append("   ▶ 오류 > " + trtMsg + " To위치 길이 기준 만족 못함\n");
					} else if ("C1".equals(toLocChkRst)) {
						chkRst = false;
						sbMsg  = sbMsg.append("   ▶ 오류 > " + trtMsg + " 상차가능 차량 없음\n");
					} else if ("E1".equals(toLocChkRst)) {
						chkRst = false;
						sbMsg  = sbMsg.append("   ▶ 오류 > " + trtMsg + " 적치가능 설비 공Bed 없음\n");
					} else if ("Z1".equals(toLocChkRst)) {
						chkRst = false;
						sbMsg  = sbMsg.append("   ▶ 오류 > " + trtMsg + " 적치가능 위치검색Bed 없음\n");
					} else {
						sbMsg = sbMsg.append("   ▷ " + trtMsg + " 정상\n");
					}
				}

				if (chkRst) {
					sbMsg = sbMsg.append("   ◈ 크레인스케줄 기동이 가능 합니다.\n");
				} else {
					sbMsg = sbMsg.append("   ◆ 크레인스케줄 기동이 불가능 합니다.\n");
				}

				sbMsg = sbMsg.append("\n");
			}
			
			slabUtils.printLog(logId, methodNm, "S-");

			//점검결과Message Return
			return sbMsg.toString();
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}


	/**
	 *      [A] 오퍼레이션명 : 크레인작업예약관리 스케줄기동
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtCrnWrkBookMgtSS(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업예약관리 스케줄기동[SlabYdJspSeEJB.trtCrnWrkBookMgtSS] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));

			jrParam.setField("YD_GP"        , slabUtils.trim(gdReq.getParam("V_YD_GP"))); //야드구분
			jrParam.setField("YD_SCH_ST_GP" , "M"                                      ); //야드스케쥴기동구분(Manual)
			jrParam.setField("YD_SCH_REQ_GP", "W"                                      ); //야드스케쥴요청구분(작업예약조회화면)
			
			//작업예약
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				jrParam.setField("YD_WBOOK_ID", slabUtils.trim(gdReq.getHeader("YD_WBOOK_ID").getValue(ii))); //야드작업예약ID
				jrParam.setField("YD_SCH_CD"  , slabUtils.trim(gdReq.getHeader("YD_SCH_CD"  ).getValue(ii))); //야드스케쥴코드
				jrParam.setField("YD_EQP_ID"  , slabUtils.trim(gdReq.getHeader("YD_WRK_CRN" ).getValue(ii))); //야드설비ID
				jrParam.setField("EJB_CALL_YN", "Y"); //EJBCall여부(신 크레인스케줄)

				//크레인스케줄 전문(신 크레인스케줄 : EJB Call, 구 크레인스케줄 : JMS 전송)
				jrRtn = slabUtils.addSndData(jrRtn, slabComm.getCrnSchMsg(jrParam));
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업예약관리 To위치변경
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updWrkBookToGuide(GridData gdReq) throws DAOException {
		String methodNm = "작업예약 To위치변경[SlabYdJspSeEJB.updWrkBookToGuide] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			String modifier = slabUtils.trim(gdReq.getParam("userid")); //수정자

			//작업예약
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String[][] arrWkr = new String[rowCnt][3];
			
			for (int ii = 0; ii < rowCnt; ii++) {
				arrWkr[ii][0] = modifier;	//수정자
				arrWkr[ii][1] = slabUtils.trim(gdReq.getHeader("YD_TO_LOC_GUIDE").getValue(ii));	//야드작업계획크레인
				arrWkr[ii][2] = slabUtils.trim(gdReq.getHeader("YD_WBOOK_ID").getValue(ii));	//야드작업예약ID
			}

			//작업예약 Table To위치 Guide Update
			commDao.upsBatch("WrkBookToGuide", arrWkr, logId, methodNm);

			slabUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	
	
	/***************************************************************************
	 * 설비인출보급
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 설비인출보급 조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getEqpPulloutSup(GridData gdReq) throws DAOException {
		String methodNm = "설비인출보급 조회[SlabYdJspSeEJB.getEqpPulloutSup] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			JDTORecordSet jrRst = jspDao.getEqpPulloutSup(gdReq);
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			return slabUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : 설비인출보급 보급요구(Carry-In)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtEqpPulloutSupCI(GridData gdReq) throws DAOException {
		String methodNm = "설비인출보급 보급요구(Carry-In)[SlabYdJspSeEJB.trtEqpPulloutSupCI] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			String ydEqpId    = slabUtils.trim(gdReq.getParam("V_YD_STK_COL_GP"));	//야드설비ID
			String ydStkBedNo = slabUtils.trim(gdReq.getParam("V_YD_STK_BED_NO"));	//야드적치Bed번호
			String strList 	  = slabUtils.trim(gdReq.getParam("V_STR_LIST"));		//야드선택재료번호
			String locList 	  = slabUtils.trim(gdReq.getParam("V_LOC_LIST"));		//야드선택재료위치
			String sYD_WRK_PLAN_CRN = slabUtils.trim(gdReq.getParam("V_YD_EQP_ID"));//작업크레인
			
			if ("".equals(ydEqpId)) {
				throw new Exception("설비ID가 없습니다.");
			} else if ("".equals(ydStkBedNo)) {
				throw new Exception("적치Bed번호가 없습니다.");
			}
			
			//설비보급요구 전문 생성 - Log ID, Method, 수정자 Set
			JDTORecord jrYdMsg = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));

			jrYdMsg.setField("JMS_TC_CD"    , "YDYDJ420"); //JMSTC코드
			jrYdMsg.setField("YD_EQP_ID"    , ydEqpId   ); //야드설비ID
			jrYdMsg.setField("YD_STK_BED_NO", ydStkBedNo); //야드적치Bed번호
			jrYdMsg.setField("YD_SCH_ST_GP" , "M"       ); //야드스케쥴기동구분(Manual)
			jrYdMsg.setField("STR_LIST" 	, strList   ); //야드선택재료번호
			jrYdMsg.setField("LOC_LIST" 	, locList   ); //야드선택재료위치
			jrYdMsg.setField("YD_WRK_PLAN_CRN" , sYD_WRK_PLAN_CRN ); //작업크레인
			
			//설비보급요구 처리
			EJBConnector sndConn = new EJBConnector("default", "SlabYdSchSeEJB", this);
			JDTORecord jrRst = (JDTORecord)sndConn.trx("rcvYDYDJ420", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });

			//설비보급요구 결과가 있으면 크레인스케줄 호출 임 : 신 크레인스케줄이면 EJB Call 아니면 JSM 전송
			if (jrRst != null) {
				jrRst.setResultCode(logId);		//Log ID
				jrRst.setResultMsg(methodNm);	//Log Method Name
				jrRst.setField("EJB_MSG_ID", "YDYDJ400"); //EJBCall전문ID
				jrRst = slabComm.rcvMsgToEjbCall(jrRst);
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return jrRst;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}


	/**
	 *      [A] 오퍼레이션명 : 설비인출보급 인출요구(Carry-Out)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtEqpPulloutSupCO(GridData gdReq) throws DAOException {
		String methodNm = "설비인출보급 인출요구(Carry-Out)[SlabYdJspSeEJB.trtEqpPulloutSupCO] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			String ydEqpId    = slabUtils.trim(gdReq.getParam("V_YD_STK_COL_GP"));	//야드설비ID
			String ydStkBedNo = slabUtils.trim(gdReq.getParam("V_YD_STK_BED_NO"));	//야드적치Bed번호
			String sYD_WRK_PLAN_CRN = slabUtils.trim(gdReq.getParam("V_YD_EQP_ID"));//작업크레인
			String sSTL_NOS = slabUtils.trim(gdReq.getParam("V_STL_NOS"));

			if ("".equals(ydEqpId)) {
				throw new Exception("설비ID가 없습니다.");
			} else if ("".equals(ydStkBedNo)) {
				throw new Exception("적치Bed번호가 없습니다.");
			}
			
			//설비인출요구 전문 생성 - Log ID, Method, 수정자 Set
			JDTORecord jrYdMsg = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));

			jrYdMsg.setField("JMS_TC_CD"    , "YDYDJ410"); //JMSTC코드
			jrYdMsg.setField("YD_EQP_ID"    , ydEqpId   ); //야드설비ID
			jrYdMsg.setField("YD_STK_BED_NO", ydStkBedNo); //야드적치Bed번호
			jrYdMsg.setField("YD_SCH_ST_GP" , "M"       ); //야드스케쥴기동구분(Manual)
			jrYdMsg.setField("YD_WRK_PLAN_CRN" , sYD_WRK_PLAN_CRN ); //작업크레인
			jrYdMsg.setField("STL_NOS" , sSTL_NOS); //재료번호
			
			//설비인출요구
			EJBConnector sndConn = new EJBConnector("default", "SlabYdSchSeEJB", this);
			JDTORecord jrRst = (JDTORecord)sndConn.trx("rcvYDYDJ410", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });

			//설비인출요구 결과가 있으면 크레인스케줄 호출 임 : 신 크레인스케줄이면 EJB Call 아니면 JSM 전송
			if (jrRst != null) {
				jrRst.setResultCode(logId);		//Log ID
				jrRst.setResultMsg(methodNm);	//Log Method Name
				jrRst.setField("EJB_MSG_ID", "YDYDJ400"); //EJBCall전문ID
				jrRst = slabComm.rcvMsgToEjbCall(jrRst);
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return jrRst;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}


	/**
	 *      [A] 오퍼레이션명 : 설비인출보급 Take-In완료
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtEqpPulloutSupTI(GridData gdReq) throws DAOException {
		String methodNm = "설비인출보급 Take-In완료[SlabYdJspSeEJB.trtEqpPulloutSupTI] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			String ydGp          = slabUtils.trim(gdReq.getParam("V_YD_GP"            ));	//야드구분
			String ydEqpId       = slabUtils.trim(gdReq.getParam("V_YD_STK_COL_GP"    ));	//야드설비ID
			String ydStkBedNo    = slabUtils.trim(gdReq.getParam("V_YD_STK_BED_NO"    ));	//야드적치Bed번호
			String ydStkBedStlSh = slabUtils.trim(gdReq.getParam("V_YD_STK_BED_STL_SH"));	//야드적치Bed재료매수
			String takeInStlNo   = slabUtils.trim(gdReq.getParam("V_STL_NO"           ));	//재료번호(Take-In)

			if ("".equals(ydEqpId)) {
				throw new Exception("설비ID가 없습니다.");
			} else if ("".equals(ydStkBedNo)) {
				throw new Exception("적치Bed번호가 없습니다.");
			} else if ("".equals(ydStkBedStlSh)) {
				throw new Exception("적치Bed재료매수가 없습니다.");
			} else if ("".equals(takeInStlNo)) {
				throw new Exception("Take-In 재료번호가 없습니다.");
			}
			
			//Take-In완료 전문 생성 - Log ID, Method, 수정자 Set
			JDTORecord jrYdMsg = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));
		
			if ("A".equals(ydGp)) {
				jrYdMsg.setField("JMS_TC_CD", "C3YDL005"); //JMSTC코드(C3Take-In완료)
			} else {
				jrYdMsg.setField("JMS_TC_CD", "Y3YDL013"); //JMSTC코드(Y3Take-In완료)
               
			}
			jrYdMsg.setField("YD_EQP_ID"        , ydEqpId      ); //야드설비ID
			jrYdMsg.setField("YD_STK_BED_NO"    , ydStkBedNo   ); //야드적치Bed번호
			jrYdMsg.setField("YD_STK_BED_STL_SH", ydStkBedStlSh); //야드적치Bed재료매수
			jrYdMsg.setField("STL_NO"           , takeInStlNo  ); //재료번호(Take-In)
			
			//Take-In완료 처리
			EJBConnector sndConn = new EJBConnector("default", "SlabYdL2RcvSeEJB", this);
			JDTORecord jrRst = (JDTORecord)sndConn.trx("rcvC3YDL005", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });

			//보급요구(Carry-In)[YDYDJ420] 전문이 있으면 호출  : Carry-In요구는 Take-In완료 후 Bed가 비었으면 자동으로 요청 됨
			if (jrRst != null) {
				//보급요구(Carry-In)[YDYDJ420] 전문 EJB Call
				jrRst.setResultCode(logId);		//Log ID
				jrRst.setResultMsg(methodNm);	//Log Method Name
				jrRst.setField("EJB_MSG_ID", "YDYDJ420"); //EJBCall전문ID
				jrRst = slabComm.rcvMsgToEjbCall(jrRst);
				
				//신 크레인스케줄이 있으면 호출
				if (jrRst != null) {
					//크레인스케줄[YDYDJ400] 전문 EJB Call
					jrRst.setResultCode(logId);		//Log ID
					jrRst.setResultMsg(methodNm);	//Log Method Name
					jrRst.setField("EJB_MSG_ID", "YDYDJ400"); //EJBCall전문ID
					jrRst = slabComm.rcvMsgToEjbCall(jrRst);
				}
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return jrRst;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}


	/**
	 *      [A] 오퍼레이션명 : 설비인출보급 Take-Out완료
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtEqpPulloutSupTO(GridData gdReq) throws DAOException {
		String methodNm = "설비인출보급 Take-Out완료[SlabYdJspSeEJB.trtEqpPulloutSupTO] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			String ydGp          = slabUtils.trim(gdReq.getParam("V_YD_GP"            ));	//야드구분
			String ydEqpId       = slabUtils.trim(gdReq.getParam("V_YD_STK_COL_GP"    ));	//야드설비ID
			String ydStkBedNo    = slabUtils.trim(gdReq.getParam("V_YD_STK_BED_NO"    ));	//야드적치Bed번호
			String ydStkBedStlSh = slabUtils.trim(gdReq.getParam("V_YD_STK_BED_STL_SH"));	//야드적치Bed재료매수
			String takeOutStlNo  = slabUtils.trim(gdReq.getParam("V_STL_NO"           ));	//재료번호(Take-Out)

			if ("".equals(ydEqpId)) {
				throw new Exception("설비ID가 없습니다.");
			} else if ("".equals(ydStkBedNo)) {
				throw new Exception("적치Bed번호가 없습니다.");
			} else if ("".equals(ydStkBedStlSh)) {
				throw new Exception("적치Bed재료매수가 없습니다.");
			} else if ("".equals(takeOutStlNo)) {
				throw new Exception("Take-Out 재료번호가 없습니다.");
			}
			
			//Take-Out완료 전문 생성 - Log ID, Method, 수정자 Set
			JDTORecord jrYdMsg = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));
	
	    

			if ("A".equals(ydGp)) {
				//실제 JMSTC코드는 의미 없지만 추가
				if ("ACPUP7".equals(ydEqpId) || "ADPUP8".equals(ydEqpId) || "ADPI04".equals(ydEqpId) || "ACPI05".equals(ydEqpId)) {
					jrYdMsg.setField("JMS_TC_CD", "C7YDL004"); //JMSTC코드(C7Take-Out완료)
				} else {
					jrYdMsg.setField("JMS_TC_CD", "C3YDL004"); //JMSTC코드(C3Take-Out완료)
				}
			} else {
				jrYdMsg.setField("JMS_TC_CD", "Y3YDL012"); //JMSTC코드(Y3Take-Out완료)	           
			}
			jrYdMsg.setField("YD_EQP_ID"        , ydEqpId                                    ); //야드설비ID
			jrYdMsg.setField("YD_STK_BED_NO"    , ydStkBedNo                                 ); //야드적치Bed번호
			jrYdMsg.setField("YD_STK_BED_STL_SH", ydStkBedStlSh                              ); //야드적치Bed재료매수
			jrYdMsg.setField("CARRY_OUT_REQ_GP" , "N"                                        ); //Carry-Out요구구분
			jrYdMsg.setField("STL_NO"           , takeOutStlNo                               ); //재료번호(Take-Out)
			jrYdMsg.setField("STL_NO1"          , slabUtils.trim(gdReq.getParam("V_STL_NO1")));	//재료번호(적치Bed)
			jrYdMsg.setField("STL_NO2"          , slabUtils.trim(gdReq.getParam("V_STL_NO2")));	//재료번호(적치Bed)
			jrYdMsg.setField("STL_NO3"          , slabUtils.trim(gdReq.getParam("V_STL_NO3")));	//재료번호(적치Bed)
			jrYdMsg.setField("STL_NO4"          , slabUtils.trim(gdReq.getParam("V_STL_NO4")));	//재료번호(적치Bed)
			jrYdMsg.setField("STL_NO5"          , slabUtils.trim(gdReq.getParam("V_STL_NO5")));	//재료번호(적치Bed)
			
			//Take-Out완료 처리
			EJBConnector sndConn = new EJBConnector("default", "SlabYdL2RcvSeEJB", this);
			JDTORecord jrRst = (JDTORecord)sndConn.trx("rcvC3YDL004", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });

			//인출요구(Carry-Out)[YDYDJ410] 전문이 있으면 호출 : Carry-Out요구구분이 'N' 이라 실제 실행되지는 않지만 추가
			if (jrRst != null) {
				//인출요구(Carry-Out)[YDYDJ410] 전문 EJB Call
				jrRst.setResultCode(logId);		//Log ID
				jrRst.setResultMsg(methodNm);	//Log Method Name
				jrRst.setField("EJB_MSG_ID", "YDYDJ410"); //EJBCall전문ID
				jrRst = slabComm.rcvMsgToEjbCall(jrRst);
				
				//신 크레인스케줄이 있으면 호출
				if (jrRst != null) {
					//크레인스케줄[YDYDJ400] 전문 EJB Call
					jrRst.setResultCode(logId);		//Log ID
					jrRst.setResultMsg(methodNm);	//Log Method Name
					jrRst.setField("EJB_MSG_ID", "YDYDJ400"); //EJBCall전문ID
					jrRst = slabComm.rcvMsgToEjbCall(jrRst);
				}
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return jrRst;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}

	
	/**
	 *      [A] 오퍼레이션명 : 설비인출보급 행선변경
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public GridData updEqpPulloutSupRt(GridData gdReq) throws DAOException {
		String methodNm = "설비인출보급 행선변경[SlabYdJspSeEJB.updEqpPulloutSupRt] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			String modifier = slabUtils.trim(gdReq.getParam("userid")); //수정자

			//Bed재료
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String[][] arrMtl = new String[rowCnt][4];
			
			for (int ii = 0; ii < rowCnt; ii++) {
				arrMtl[ii][0] = modifier;	//수정자
				arrMtl[ii][1] = slabUtils.trim(gdReq.getHeader("YD_AIM_RT_GP" ).getValue(ii));	//야드목표행선구분
				arrMtl[ii][2] = slabUtils.trim(gdReq.getHeader("YD_AIM_BAY_GP").getValue(ii));	//야드목표동구분
				arrMtl[ii][3] = slabUtils.trim(gdReq.getHeader("STL_NO"       ).getValue(ii));	//재료번호
			}

			//저장품 Table Update
			jspDao.updEqpPulloutSup("Stock", arrMtl, logId, methodNm);

			slabUtils.printLog(logId, methodNm, "S-");

			//Bed재료 조회
			return this.getEqpPulloutSup(gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}

	
	/**
	 *      [A] 오퍼레이션명 : 설비인출보급 Bed재료삭제
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public GridData delEqpPulloutSupMtl(GridData gdReq) throws DAOException {
		String methodNm = "설비인출보급 Bed재료삭제[SlabYdJspSeEJB.delEqpPulloutSupMtl] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			String modifier = slabUtils.trim(gdReq.getParam("userid")); //수정자

			//Bed재료
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String[][] arrMtl = new String[rowCnt][4];
			
			for (int ii = 0; ii < rowCnt; ii++) {
				arrMtl[ii][0] = modifier;	//수정자
				arrMtl[ii][1] = slabUtils.trim(gdReq.getHeader("YD_STK_COL_GP").getValue(ii));	//야드적치열구분
				arrMtl[ii][2] = slabUtils.trim(gdReq.getHeader("YD_STK_BED_NO").getValue(ii));	//야드적치Bed번호
				arrMtl[ii][3] = slabUtils.trim(gdReq.getHeader("YD_STK_LYR_NO").getValue(ii));	//야드적치단번호
			}

			//적치단 Table 재료삭제
			jspDao.updEqpPulloutSup("StkLyr", arrMtl, logId, methodNm);

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, modifier);

			jrParam.setField("V_YD_STK_COL_GP", gdReq.getHeader("YD_STK_COL_GP").getValue(0));	//야드적치열구분
			jrParam.setField("V_YD_STK_BED_NO", gdReq.getHeader("YD_STK_BED_NO").getValue(0));	//야드적치Bed번호

			//적치단 정리작업
			jspDao.updEqpPulloutSup("StkLyrFix", jrParam);
			
			slabUtils.printLog(logId, methodNm, "S-");

			//Bed재료 조회
			return this.getEqpPulloutSup(gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}

	
	/**
	 *      [A] 오퍼레이션명 : 설비인출보급 보급편성기준변경
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public GridData updEqpPulloutSupRule(GridData gdReq) throws DAOException {
		String methodNm = "설비인출보급 보급편성기준변경[SlabYdJspSeEJB.updEqpPulloutSupRule] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			String ydStkColGp = slabUtils.trim(gdReq.getParam("V_YD_STK_COL_GP")); //야드적치열구분
			String tiSupYn    = slabUtils.trim(gdReq.getParam("V_TI_SUP_YN"    )); //TakeIn공Bed보급요구여부(Y:자동보급, N:수동보급)

			if ("".equals(ydStkColGp)) {
				throw new Exception("설비ID가 없습니다.");
			}

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));

			jrParam.setField("V_YD_STK_COL_GP", ydStkColGp);	//야드적치열구분
			jrParam.setField("V_TI_SUP_YN"    , tiSupYn   );	//TakeIn공Bed보급요구여부

			//야드기준 Table Update
			jspDao.updComm("ChgSupRule", jrParam);

			slabUtils.printLog(logId, methodNm, "S-");

			return gdReq;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	/**
	 *      [A] 오퍼레이션명 : 크레인 보급편성기준변경
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public GridData updCrnPulloutSupRule(GridData gdReq) throws DAOException {
		String methodNm = "크레인 보급편성기준변경[SlabYdJspSeEJB.updCrnPulloutSupRule] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			String ydStkColGp = slabUtils.trim(gdReq.getParam("V_YD_STK_COL_GP")); //야드적치열구분
			String wrkCrn     = slabUtils.trim(gdReq.getParam("V_YD_WRK_CRN"   )); //TakeIn공Bed보급요구여부(Y:자동보급, N:수동보급)

			if ("".equals(ydStkColGp)) {
				throw new Exception("설비ID가 없습니다.");
			}

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));

			jrParam.setField("V_YD_STK_COL_GP", ydStkColGp);	//야드적치열구분
			jrParam.setField("V_YD_WRK_CRN"   , wrkCrn    );	//TakeIn공Bed보급요구여부

			//야드기준 Table Update
			jspDao.updComm("ChgCrnRule", jrParam);

			slabUtils.printLog(logId, methodNm, "S-");

			return gdReq;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
		
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 설비인출보급 순번 변경
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public GridData updStockSeqNo(GridData gdReq) throws DAOException {
		String methodNm = "설비인출보급 순번 변경[SlabYdJspSeEJB.updStockSeqNo] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);
			
			String stl_nos = slabUtils.trim(gdReq.getParam("V_STR_LIST")); //재료번호
			String seq_nos = slabUtils.trim(gdReq.getParam("V_SEQNO_LIST")); //재료 순번

			if ("".equals(stl_nos)) {
				throw new Exception("재료번호가 없습니다.");
			}

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));
			
			String[] stl_no = stl_nos.split(",");
			String[] seq_no = seq_nos.split(",");
			
			for(int ii = 0; ii < stl_no.length; ii++) {
				jrParam.setField("V_STL_NO", stl_no[ii]);	//슬라브번호
				jrParam.setField("V_SEQ_NO", seq_no[ii]);	//순번

				//stock Table Update
				jspDao.updComm("ChgSeqNo", jrParam);
				
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return gdReq;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
		
	}
	
	
	
	
	
	
	/***************************************************************************
	 * 장입보급기준
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 장입보급기준 조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getChgSupRule(GridData gdReq) throws DAOException {
		String methodNm = "장입보급기준 조회[SlabYdJspSeEJB.getEqpPulloutSup] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			JDTORecordSet jrRst = jspDao.getChgSupRule(gdReq);
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			return slabUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : 장입보급기준 등록
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData trtChgSupRule(GridData gdReq) throws DAOException {
		String methodNm = "장입보급기준 등록[SlabYdJspSeEJB.trtChgSupRule] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));

			jrParam.setField("V_YD_STK_COL_GP" , slabUtils.trim(gdReq.getParam("V_YD_STK_COL_GP" )));	//야드적치열구분
			jrParam.setField("V_TI_SUP_YN"     , slabUtils.trim(gdReq.getParam("V_TI_SUP_YN"     )));	//TakeIn공Bed보급요구여부
			jrParam.setField("V_TI_PRE_SUP_YN" , slabUtils.trim(gdReq.getParam("V_TI_PRE_SUP_YN" )));	//TakeInBed1매선보급요구여부
			jrParam.setField("V_CI_AUTO_GP_YN" , slabUtils.trim(gdReq.getParam("V_CI_AUTO_GP_YN" )));	//보급요구대상재자동편성여부
			jrParam.setField("V_CI_CHG_EXT_YN" , slabUtils.trim(gdReq.getParam("V_CI_CHG_EXT_YN" )));	//보급요구자동편성장입확장여부
			jrParam.setField("V_CI_CHG_MTL_CNT", slabUtils.trim(gdReq.getParam("V_CI_CHG_MTL_CNT")));	//보급요구자동편성장입재료수
			jrParam.setField("V_CI_CHG_LOT_CNT", slabUtils.trim(gdReq.getParam("V_CI_CHG_LOT_CNT")));	//보급요구자동편성장입Lot수
			jrParam.setField("V_CI_CHG_NO_SORT", slabUtils.trim(gdReq.getParam("V_CI_CHG_NO_SORT")));	//보급요구자동편성장입순위정렬여부

			//야드기준 Table Update
			jspDao.updChgSupRule("Rule", jrParam);

			slabUtils.printLog(logId, methodNm, "S-");

			return this.getChgSupRule(gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}


	/***************************************************************************
	 * 수불구용도변경
	 **************************************************************************/
	
	/**
	 *      [A] 오퍼레이션명 : 수불구용도변경 조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getBedUsgGpSet(GridData gdReq) throws DAOException {
		String methodNm = "수불구용도변경 조회[SlabYdJspSeEJB.getBedUsgGpSet] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			String ydStkColGp = slabUtils.trim(gdReq.getParam("V_YD_STK_COL_GP")); //야드적치열구분
			String ydStkBedNo = slabUtils.trim(gdReq.getParam("V_YD_STK_BED_NO")); //야드적치Bed번호

			if ("".equals(ydStkColGp)) {
				throw new Exception("적치열구분이 없습니다.");
			} else if ("".equals(ydStkBedNo)) {
				throw new Exception("적치Bed번호가 없습니다.");
			}

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, "");

			jrParam.setField("V_YD_STK_COL_GP", ydStkColGp);	//야드적치열구분
			jrParam.setField("V_YD_STK_BED_NO", ydStkBedNo);	//야드적치Bed번호

			JDTORecordSet jrRst = commDao.getStat("Bed", jrParam);
			
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			return slabUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 수불구용도변경 등록
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtBedUsgGpSet(GridData gdReq) throws DAOException {
		String methodNm = "수불구용도변경 등록[SlabYdJspSeEJB.trtBedUsgGpSet] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			String ydStkColGp    = slabUtils.trim(gdReq.getParam("V_YD_STK_COL_GP"    )); //야드적치열구분
			String ydStkBedNo    = slabUtils.trim(gdReq.getParam("V_YD_STK_BED_NO"    )); //야드적치Bed번호
			String ydStkBedUsgGp = slabUtils.trim(gdReq.getParam("V_YD_STK_BED_USG_GP")); //야드적치Bed용도구분

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydStkColGp)) {
				throw new Exception("적치열구분이 없습니다.");
			} else if ("".equals(ydStkBedNo)) {
				throw new Exception("적치Bed번호가 없습니다.");
			} else if ("".equals(ydStkBedUsgGp)) {
				throw new Exception("적치Bed용도구분이 없습니다.");
			}

			/**********************************************************
			* 2. Bed용도구분설정
			**********************************************************/
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));

			jrParam.setField("V_YD_STK_COL_GP"    , ydStkColGp   );	//야드적치열구분
			jrParam.setField("V_YD_STK_BED_NO"    , ydStkBedNo   );	//야드적치Bed번호
			jrParam.setField("V_YD_STK_BED_USG_GP", ydStkBedUsgGp);	//야드적치Bed용도구분

			//Bed용도구분설정
			if (slabComm.setBedUsgGp(jrParam) <= 0) {
				throw new Exception("변경된 적치Bed용도구분이 없습니다.");
			}

			/**********************************************************
			* 3. 수불구변경응답(YDC3L001, YDC7L001) 전문 조회 : 후판 없음
			**********************************************************/
			JDTORecord jrRtn = null;

			if (ydStkColGp.startsWith("A")) {
				String msgId = "YDC3L001";
				
				if ("ACPUP7".equals(ydStkColGp) || "ADPUP8".equals(ydStkColGp)) {
					msgId = "YDC7L001";
				}
	
				jrParam.setField("V_JMS_TC_CD", msgId); //JMSTC코드
				
				//전송Data 조회
				jrRtn = slabUtils.addSndData(commDao.getMsgL2(msgId, jrParam));
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}


	/***************************************************************************
	 * 고강도재 상하면스카핑여부 등록
	 **************************************************************************/
	
	/**
	 *      [A] 오퍼레이션명 : 고강도재 상하면스카핑구분 조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getHsmAllSfSet(GridData gdReq) throws DAOException {
		String methodNm = "고강도재 상하면스카핑구분 조회[SlabYdJspSeEJB.getHsmAllSfSet] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			JDTORecordSet jrRst = jspDao.getHsmAllSfSet(gdReq);
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			return slabUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 고강도재 상하면스카핑구분 등록
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData trtHsmAllSfSet(GridData gdReq) throws DAOException {
		String methodNm = "고강도재 상하면스카핑구분 등록[SlabYdJspSeEJB.trtHsmAllSfSet] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			String stlNo = slabUtils.trim(gdReq.getParam("V_STL_NO"));	//재료번호
			String jsfGp = slabUtils.trim(gdReq.getParam("V_JSF_GP"));	//상하면스카핑구분

			if ("".equals(stlNo)) {
				throw new Exception("재료번호가 없습니다.");
			}

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));

			jrParam.setField("V_STL_NO", stlNo);
			jrParam.setField("V_JSF_GP", jsfGp);

			//저장품 Table 상하면스카핑구분(SNDBK_GP:반송요청구분) Update
			jspDao.updHsmAllSfSet("Stock", jrParam);

			slabUtils.printLog(logId, methodNm, "S-");

			return this.getHsmAllSfSet(gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	
	/***************************************************************************
	 * 스카핑보급관리
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 스카핑보급관리 조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getSfSupMgt(GridData gdReq) throws DAOException {
		String methodNm = "스카핑보급관리 조회[SlabYdJspSeEJB.getSfSupMgt] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			JDTORecordSet jrRst = jspDao.getSfSupMgt(gdReq);
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			return slabUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : 스카핑보급관리 보급Lot등록
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtSfSupMgtCL(GridData gdReq) throws DAOException {
		String methodNm = "스카핑보급관리 보급Lot등록[SlabYdJspSeEJB.trtSfSupMgtCL] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			String ydStkColGp = slabUtils.trim(gdReq.getParam("V_YD_STK_COL_GP"));	//야드적치열구분
			String lotRegGp   = slabUtils.trim(gdReq.getParam("V_LOT_REG_GP"   ));	//Lot등록구분
			String modifier   = slabUtils.trim(gdReq.getParam("userid"         ));	//수정자

			if ("".equals(ydStkColGp)) {
				throw new Exception("설비ID가 없습니다.");
			}

			//야드스케쥴코드 설정
			String ydSchCd = "";	//야드스케쥴코드

			if ("ACDP01".equals(ydStkColGp) || "ABDP03".equals(ydStkColGp) 
					|| "MADP01".equals(ydStkColGp) || "MART01".equals(ydStkColGp)) {  // 항만야드 기능적용 보완 : 2015.12.21 by LeeJY
				ydSchCd = ydStkColGp + "UM";
			} else {
				throw new Exception("처리할 수 없는 설비[" + ydStkColGp + "] 입니다...");
			}
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, modifier);

			//야드스케쥴코드 Check
			jrParam.setField("V_YD_SCH_CD", ydSchCd); //야드스케쥴코드

			//야드스케쥴금지유무 조회
			JDTORecordSet jsChk = commDao.getStat("SchCd", jrParam);

			if (jsChk.size() > 0) {
				String ydSchProhExn = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_SCH_PROH_EXN"));	//야드스케쥴금지유무

				//스케줄 금지여부 Check
				if ("Y".equals(ydSchProhExn)) {
					throw new Exception("스케쥴코드[" + ydSchCd + "]는 기동금지 상태입니다.");
				}
			} else {
				throw new Exception("스케쥴코드[" + ydSchCd + "]의 정보가 없습니다.");
			}

			//야드준비스케쥴ID 생성
			String ydPrepSchId = commDao.getSeqId(logId, methodNm, "PrepSch");

			jrParam.setField("V_YD_STK_COL_GP" , ydStkColGp ); //야드적치열구분
			jrParam.setField("V_YD_PREP_SCH_ID", ydPrepSchId); //야드준비스케쥴ID

			int rowCnt = 0;
			
			//Lot등록구분에 따라 분리
			if ("A".equals(lotRegGp)) {
				//자동으로 Lot편성하여 준비재료 등록
				rowCnt = jspDao.insSfSupMgt("PrepMtl", jrParam);
			} else {
				//선택된 대상재로 Lot편성
				rowCnt = gdReq.getHeader("CHECK").getRowCount();
				
				//준비재료 등록
				if (rowCnt > 0) {
					String[][] arrMtl = new String[rowCnt][7];
					
					for (int ii = 0; ii < rowCnt; ii++) {
						arrMtl[ii][0] = ydPrepSchId;													//야드준비스케쥴ID
						arrMtl[ii][1] = slabUtils.trim(gdReq.getHeader("STL_NO"       ).getValue(ii));	//재료번호
						arrMtl[ii][2] = modifier;														//수정자
						arrMtl[ii][3] = modifier;														//수정자
						arrMtl[ii][4] = slabUtils.trim(gdReq.getHeader("YD_STK_COL_GP").getValue(ii));	//야드적치열구분
						arrMtl[ii][5] = slabUtils.trim(gdReq.getHeader("YD_STK_BED_NO").getValue(ii));	//야드적치Bed번호
						arrMtl[ii][6] = slabUtils.trim(gdReq.getHeader("YD_STK_LYR_NO").getValue(ii));	//야드적치단번호
					}

					commDao.upsBatch("PrepMtl", arrMtl, logId, methodNm);
				}
			}

			//준비스케쥴 등록
			if (rowCnt > 0) {
				commDao.insSlabYd("PrepSch", jrParam);
			} else {
				throw new Exception("등록된 보급Lot 재료가 없습니다.");
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}

	
	/**
	 *      [A] 오퍼레이션명 : 스카핑보급관리 지연사유등록(긴급재)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtSfSupMgtMD(GridData gdReq) throws DAOException {
		String methodNm = "스카핑보급관리 지연사유등록[SlabYdJspSeEJB.trtSfSupMgtMD] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			String scarfDelyRegister = slabUtils.trim(gdReq.getParam("V_SCARF_DELY_REGISTER")); //Scarfing지연등록자
			String scarfDelyCnts     = slabUtils.trim(gdReq.getParam("V_SCARF_DELY_CNTS"    )); //Scarfing지연내용
			String modifier          = slabUtils.trim(gdReq.getParam("userid"               )); //수정자

			//Bed재료
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String[][] arrMtl = new String[rowCnt][5];
			String stlNos = "";
			for (int ii = 0; ii < rowCnt; ii++) {
				arrMtl[ii][0] = scarfDelyRegister;	//Scarfing지연등록자
				arrMtl[ii][1] = scarfDelyCnts;		//Scarfing지연내용
				arrMtl[ii][2] = modifier;			//수정자
				arrMtl[ii][3] = slabUtils.trim(gdReq.getHeader("STL_NO" ).getValue(ii));	//재료번호
				arrMtl[ii][4] = slabUtils.trim(gdReq.getHeader("STEP_NO").getValue(ii));	//차수
				
				if("".equals(stlNos)){
					stlNos += slabUtils.trim(gdReq.getHeader("STL_NO" ).getValue(ii));
				}
				else {
					stlNos += ","+ slabUtils.trim(gdReq.getHeader("STL_NO" ).getValue(ii));
				}
			}

			//PM_SLAB스카핑긴급대상지정 Table Update
			jspDao.updSfSupMgt("Dely", arrMtl, logId, methodNm);
			
			//PM_SLAB스카핑긴급대상지정 Table Update
			jspDao.updSfSupMgt("DelyMs", arrMtl, logId, methodNm);
			
			//23.02.15 연주 김충만계장 요청 스카핑 지연사유 벤딩 포함시 자동벤딩등록 REQ202301442577
			if(scarfDelyCnts.contains("밴딩") || scarfDelyCnts.contains("벤딩")){
				GridData gridParam = new GridData();
				gridParam.addParam("V_STL_NOS", stlNos);
				gridParam.addParam("V_BENDING_YN", "Y");
				gridParam.addParam("V_ITM_GP", "*");
				gridParam.addParam("V_MODIFIER", modifier);
				gridParam.addParam("action_code", "update");
				
				//재료에 벤딩 표시
				ydStockDao.updStockBendReg(gridParam);
				
				//bending 이력 등록
				ydStockDao.inStockBendReg(gridParam);

			}

			slabUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 스카핑보급관리 지연사유등록(공정요구)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtSfSupMgtMS(GridData gdReq) throws DAOException {
		String methodNm = "스카핑보급관리 지연사유등록(공정요구)[SlabYdJspSeEJB.trtSfSupMgtMS] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			String scarfDelyRegister = slabUtils.trim(gdReq.getParam("V_SCARF_DELY_REGISTER")); //Scarfing지연등록자
			String scarfDelyCnts     = slabUtils.trim(gdReq.getParam("V_SCARF_DELY_CNTS"    )); //Scarfing지연내용
			String modifier          = slabUtils.trim(gdReq.getParam("userid"               )); //수정자

			//Bed재료
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String[][] arrMtl = new String[rowCnt][4];
			String stlNos = "";
			for (int ii = 0; ii < rowCnt; ii++) {
				arrMtl[ii][0] = scarfDelyRegister;	//Scarfing지연등록자
				arrMtl[ii][1] = scarfDelyCnts;		//Scarfing지연내용
				arrMtl[ii][2] = modifier;			//수정자
				arrMtl[ii][3] = slabUtils.trim(gdReq.getHeader("STL_NO" ).getValue(ii));	//재료번호

				if("".equals(stlNos)){
					stlNos += slabUtils.trim(gdReq.getHeader("STL_NO" ).getValue(ii));
				}
				else {
					stlNos += ","+ slabUtils.trim(gdReq.getHeader("STL_NO" ).getValue(ii));
				}
			}
			
			//PM_SLAB스카핑긴급대상지정 Table Update
			jspDao.updSfSupMgt("DelyMs", arrMtl, logId, methodNm);
			
			//23.02.15 연주 김충만계장 요청 스카핑 지연사유 벤딩 포함시 자동벤딩등록 REQ202301442577
			if(scarfDelyCnts.contains("밴딩") || scarfDelyCnts.contains("벤딩")){
				GridData gridParam = new GridData();
				gridParam.addParam("V_STL_NOS", stlNos);
				gridParam.addParam("V_BENDING_YN", "Y");
				gridParam.addParam("V_ITM_GP", "*");
				gridParam.addParam("V_MODIFIER", modifier);
				gridParam.addParam("action_code", "update");
				
				//재료에 벤딩 표시
				ydStockDao.updStockBendReg(gridParam);
				
				//bending 이력 등록
				ydStockDao.inStockBendReg(gridParam);
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/***************************************************************************
	 * 2차절단보급관리
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 2차절단보급관리 조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getStcmSupMgt(GridData gdReq) throws DAOException {
		String methodNm = "2차절단보급관리 조회[SlabYdJspSeEJB.getStcmSupMgt] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			JDTORecordSet jrRst = jspDao.getStcmSupMgt(gdReq);
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			return slabUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 2차절단보급관리 조회(신)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getStcmSupMgtNew(GridData gdReq) throws DAOException {
		String methodNm = "2차절단보급관리 조회[SlabYdJspSeEJB.getStcmSupMgtNew] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			JDTORecordSet jrRst = jspDao.getStcmSupMgtNew(gdReq);
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			return slabUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 2차절단보급관리 조회(신)팝업
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getStcmSupMgtNewpp(GridData gdReq) throws DAOException {
		String methodNm = "2차절단보급관리 조회[SlabYdJspSeEJB.getStcmSupMgtNewpp] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			JDTORecordSet jrRst = jspDao.getStcmSupMgtNewpp(gdReq);
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			return slabUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 2차절단보급관리 보급Lot등록
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtStcmSupMgtCL(GridData gdReq) throws DAOException {
		String methodNm = "2차절단보급관리 보급Lot등록[SlabYdJspSeEJB.trtStcmSupMgtCL] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			String ydStkColGp = slabUtils.trim(gdReq.getParam("V_YD_STK_COL_GP"));	//야드적치열구분
			String lotRegGp   = slabUtils.trim(gdReq.getParam("V_LOT_REG_GP"   ));	//Lot등록구분
			String modifier   = slabUtils.trim(gdReq.getParam("userid"         ));	//수정자

			if ("".equals(ydStkColGp)) {
				throw new Exception("설비ID가 없습니다.");
			}

			String ydSchCd = "";	//야드스케쥴코드

			//야드스케쥴코드 설정
			if ("AADP02".equals(ydStkColGp)) {
				ydSchCd = "AADP02UM";	//#1 2차절단
			} else if ("AAPUP9".equals(ydStkColGp)) {
				ydSchCd = "AAPU09UM";	//#2 2차절단
			} else if ("AAPUPA".equals(ydStkColGp)) {
				ydSchCd = "AAPU10UM";	//#3 2차절단
			} else {
				throw new Exception("처리할 수 없는 설비[" + ydStkColGp + "] 입니다.");
			}
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, modifier);

			//야드스케쥴코드 Check
			jrParam.setField("V_YD_SCH_CD", ydSchCd); //야드스케쥴코드

			//야드스케쥴금지유무 조회
			JDTORecordSet jsChk = commDao.getStat("SchCd", jrParam);

			if (jsChk.size() > 0) {
				String ydSchProhExn = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_SCH_PROH_EXN"));	//야드스케쥴금지유무

				//스케줄 금지여부 Check
				if ("Y".equals(ydSchProhExn)) {
					throw new Exception("스케쥴코드[" + ydSchCd + "]는 기동금지 상태입니다.");
				}
			} else {
				throw new Exception("스케쥴코드[" + ydSchCd + "]의 정보가 없습니다.");
			}

			//야드준비스케쥴ID 생성
			String ydPrepSchId = commDao.getSeqId(logId, methodNm, "PrepSch");

			jrParam.setField("V_YD_STK_COL_GP" , ydStkColGp ); //야드적치열구분
			jrParam.setField("V_YD_PREP_SCH_ID", ydPrepSchId); //야드준비스케쥴ID

			int rowCnt = 0;

			//Lot등록구분에 따라 분리
			if ("A".equals(lotRegGp)) {
				//자동으로 Lot편성하여 준비재료 등록
				rowCnt = jspDao.insStcmSupMgt("PrepMtl", jrParam);
			} else {
				//선택된 대상재로 Lot편성
				rowCnt = gdReq.getHeader("CHECK").getRowCount();

				//준비재료 등록
				if (rowCnt > 0) {
					String[][] arrMtl = new String[rowCnt][7];
					
					for (int ii = 0; ii < rowCnt; ii++) {
						arrMtl[ii][0] = ydPrepSchId;													//야드준비스케쥴ID
						arrMtl[ii][1] = slabUtils.trim(gdReq.getHeader("STL_NO"       ).getValue(ii));	//재료번호
						arrMtl[ii][2] = modifier;														//수정자
						arrMtl[ii][3] = modifier;														//수정자
						arrMtl[ii][4] = slabUtils.trim(gdReq.getHeader("YD_STK_COL_GP").getValue(ii));	//야드적치열구분
						arrMtl[ii][5] = slabUtils.trim(gdReq.getHeader("YD_STK_BED_NO").getValue(ii));	//야드적치Bed번호
						arrMtl[ii][6] = slabUtils.trim(gdReq.getHeader("YD_STK_LYR_NO").getValue(ii));	//야드적치단번호
					}
					
					commDao.upsBatch("PrepMtl", arrMtl, logId, methodNm);
				}
			}

			//준비스케쥴 등록
			if (rowCnt > 0) {
				commDao.insSlabYd("PrepSch", jrParam);
			} else {
				throw new Exception("등록된 보급Lot 재료가 없습니다.");
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	/**
	 *      [A] 오퍼레이션명 : 2차절단보급관리 긴급/보류 등록 팝업
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtStcmSupMgtCLpp(GridData gdReq) throws DAOException {
		String methodNm = "2차절단보급관리 보급Lot등록[SlabYdJspSeEJB.trtStcmSupMgtCLpp] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		JDTORecord jrRtn = null; 
			
		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);
			
			GridData gdRtn 	= OperateGridData.cloneResponseGridData(gdReq);
			String sWorkGp 	= slabUtils.trim(gdReq.getParam("V_WORK_GP"));
			String sEtc 	= slabUtils.trim(gdReq.getParam("V_ETC"));
			String sUserId 	= slabUtils.trim(gdReq.getParam("userid"));
			String sStlList = "|";
			//선택된 대상재로 Lot편성
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			if (rowCnt > 0) {
				
				for (int ii = 0; ii < rowCnt; ii++) {
					sStlList = sStlList + slabUtils.trim(gdReq.getHeader("STL_NO").getValue(ii)) + "|";	//재료번호
				}
			}
			
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("JMS_TC_CD"           	, "YDCTJ036" ); //YDCTJ036
			recPara.setField("JMS_TC_CREATE_DDTT"   , slabUtils.getDateTime14()); //YYYYMMDDHH24MISS
			recPara.setField("PROCESS_GP"			, sWorkGp ); //1:긴급등록,2:긴급해제,3:보류등록,4:보류해제
			recPara.setField("MSLAB_NO"				, sStlList ); //주편번호 (예:  |M00001 01|M00001 02|M00001 03|  )
			recPara.setField("RSN"					, sEtc ); //사유
			recPara.setField("REGISTER"				, sUserId); //등록자
			
			jrRtn = slabUtils.addSndData(recPara);
			
			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	
	/***************************************************************************
	 * 보급Lot관리 (장입/스카핑/2차절단)
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 보급Lot관리 조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getSupLotMgt(GridData gdReq) throws DAOException {
		String methodNm = "보급Lot관리 조회[SlabYdJspSeEJB.getSupLotMgt] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			JDTORecordSet jrRst = jspDao.getSupLotMgt(gdReq);
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			return slabUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 보급Lot관리 보급순서 수정
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData trtSupLotMgtMS(GridData gdReq) throws DAOException {
		String methodNm = "보급Lot관리 보급순서 수정[SlabYdJspSeEJB.trtSupLotMgtMS] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));

			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				jrParam.setField("V_YD_PREP_SCH_ID", slabUtils.trim(gdReq.getHeader("YD_PREP_SCH_ID").getValue(ii)));	//야드준비스케쥴ID
				jrParam.setField("V_YD_CARASGN_SEQ", slabUtils.trim(gdReq.getHeader("YD_CARASGN_SEQ").getValue(ii)));	//보급순서(야드배차순서)
				
				//보급순서(야드배차순서) 수정
				commDao.updSlabYd("PsPrior", jrParam);
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return this.getSupLotMgt(gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}

	
	/**
	 *      [A] 오퍼레이션명 : 보급Lot관리 삭제
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData trtSupLotMgtDS(GridData gdReq) throws DAOException {
		String methodNm = "보급Lot관리 삭제[SlabYdJspSeEJB.trtSupLotMgtDS] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));
			jrParam.setField("V_STL_NO", "");	//재료번호

			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				jrParam.setField("V_YD_PREP_SCH_ID", slabUtils.trim(gdReq.getHeader("YD_PREP_SCH_ID").getValue(ii)));	//야드준비스케쥴ID

				//준비재료(TB_YD_PREPMTL) 삭제
				commDao.updDelYn("PrepMtl", jrParam);
				
				//준비스케줄(TB_YD_PREPSCH) 삭제
				commDao.updDelYn("PrepSch", jrParam);
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return this.getSupLotMgt(gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}

	
	/**
	 *      [A] 오퍼레이션명 : 보급Lot관리 재료삭제
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData trtSupLotMgtDM(GridData gdReq) throws DAOException {
		String methodNm = "보급Lot관리 재료삭제[SlabYdJspSeEJB.trtSupLotMgtDM] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));

			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				jrParam.setField("V_YD_PREP_SCH_ID", slabUtils.trim(gdReq.getHeader("YD_PREP_SCH_ID").getValue(ii)));	//야드준비스케쥴ID
				jrParam.setField("V_STL_NO"        , slabUtils.trim(gdReq.getHeader("STL_NO").getValue(ii)        ));	//재료번호
				
				//준비재료(TB_YD_PREPMTL) 삭제
				commDao.updDelYn("PrepMtl", jrParam);
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return this.getSupLotMgt(gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	/**
	 *      [A] 오퍼레이션명 : 보급Lot관리 크레인 지정
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData trtSupLotMgtCM(GridData gdReq) throws DAOException {
		String methodNm = "보급Lot관리 크레인 지정[SlabYdJspSeEJB.trtSupLotMgtCM] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		YdPrepSchDao ydPrepSchDao = new YdPrepSchDao();
		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));

			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				jrParam.setField("YD_PREP_SCH_ID"		, slabUtils.trim(gdReq.getHeader("YD_PREP_SCH_ID").getValue(ii)));	//야드준비스케쥴ID
				jrParam.setField("YD_WRK_PLAN_CRN"      , slabUtils.trim(gdReq.getHeader("YD_WRK_PLAN_CRN").getComboHiddenValues()[gdReq.getHeader("YD_WRK_PLAN_CRN").getSelectedIndex(ii)]));	//재료번호
				jrParam.setField("MODIFIER"				, slabUtils.trim(gdReq.getParam("userid")));
				//지정크레인 수정
				ydPrepSchDao.updYdPrepsch(jrParam,0);
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return this.getSupLotMgt(gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	
	/***************************************************************************
	 * 스카핑/2차절단보급Lot
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 스카핑/2차절단보급Lot 조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getSfStcmSupLot(GridData gdReq) throws DAOException {
		String methodNm = "스카핑/2차절단보급Lot 조회[SlabYdJspSeEJB.getSfStcmSupLot] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			JDTORecordSet jrRst = jspDao.getSfStcmSupLot(gdReq);
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			return slabUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 스카핑/2차절단보급Lot 보급순서 수정
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData trtSfStcmSupLotMS(GridData gdReq) throws DAOException {
		String methodNm = "스카핑/2차절단보급Lot 보급순서 수정[SlabYdJspSeEJB.trtSfStcmSupLotMS] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));

			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				jrParam.setField("V_YD_PREP_SCH_ID", slabUtils.trim(gdReq.getHeader("YD_PREP_SCH_ID").getValue(ii)));	//야드준비스케쥴ID
				jrParam.setField("V_YD_CARASGN_SEQ", slabUtils.trim(gdReq.getHeader("YD_CARASGN_SEQ").getValue(ii)));	//보급순서(야드배차순서)
				
				//보급순서(야드배차순서) 수정
				commDao.updSlabYd("PsPrior", jrParam);
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return this.getSfStcmSupLot(gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}

	
	/**
	 *      [A] 오퍼레이션명 : 스카핑/2차절단보급Lot 삭제
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData trtSfStcmSupLotDS(GridData gdReq) throws DAOException {
		String methodNm = "스카핑/2차절단보급Lot 삭제[SlabYdJspSeEJB.trtSfStcmSupLotDS] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));
			jrParam.setField("V_STL_NO", "");	//재료번호

			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				jrParam.setField("V_YD_PREP_SCH_ID", slabUtils.trim(gdReq.getHeader("YD_PREP_SCH_ID").getValue(ii)));	//야드준비스케쥴ID

				//준비재료(TB_YD_PREPMTL) 삭제
				commDao.updDelYn("PrepMtl", jrParam);
				
				//준비스케줄(TB_YD_PREPSCH) 삭제
				commDao.updDelYn("PrepSch", jrParam);
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return this.getSfStcmSupLot(gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}

	
	/**
	 *      [A] 오퍼레이션명 : 스카핑/2차절단보급Lot 재료삭제
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData trtSfStcmSupLotDM(GridData gdReq) throws DAOException {
		String methodNm = "스카핑/2차절단보급Lot 재료삭제[SlabYdJspSeEJB.trtSfStcmSupLotDM] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));

			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				jrParam.setField("V_YD_PREP_SCH_ID", slabUtils.trim(gdReq.getHeader("YD_PREP_SCH_ID").getValue(ii)));	//야드준비스케쥴ID
				jrParam.setField("V_STL_NO"        , slabUtils.trim(gdReq.getHeader("STL_NO").getValue(ii)        ));	//재료번호
				
				//준비재료(TB_YD_PREPMTL) 삭제
				commDao.updDelYn("PrepMtl", jrParam);
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return this.getSfStcmSupLot(gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}

	
	/***************************************************************************
	 * 대차스케줄관리
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 대차스케줄관리 조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getTcarSchMgt(GridData gdReq) throws DAOException {
		String methodNm = "대차스케줄관리 조회[SlabYdJspSeEJB.getTcarSchMgt] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			JDTORecordSet jrRst = jspDao.getTcarSchMgt(gdReq);
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			return slabUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : 대차스케줄관리 대차초기화
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtTcarSchMgtTI(GridData gdReq) throws DAOException {
		String methodNm = "대차스케줄관리 대차초기화[SlabYdJspSeEJB.trtTcarSchMgtTI] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			String ydEqpId     = ""; //야드설비ID(대차)
			String ydCurrBayGp = ""; //야드현재동구분(신규)
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));

			jrParam.setField("V_YD_EQP_WRK_STAT"    , "U"); //야드설비작업상태(공차)
			jrParam.setField("V_YD_CARLD_SCH_REQ_GP", "6"); //야드상차스케쥴요청구분(공대차도착)
			jrParam.setField("V_YD_CARUD_SCH_REQ_GP", "3"); //야드하차스케쥴요청구분(영대차도착)
			jrParam.setField("V_YD_CAR_PROG_STAT"   , "0"); //야드차량진행상태(상차대기)

			//대차정보
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				ydEqpId     = slabUtils.trim(gdReq.getHeader("YD_EQP_ID").getValue(ii));
				ydCurrBayGp = slabUtils.trim(gdReq.getHeader("YD_CURR_BAY_GP").getComboHiddenValues()[gdReq.getHeader("YD_CURR_BAY_GP").getSelectedIndex(ii)]);

				if ("".equals(ydEqpId)) {
					throw new Exception("설비ID가 없습니다.");
				} else if ("".equals(ydCurrBayGp)) {
					throw new Exception("변경할 현재동이 없습니다.");
				}
				
				/**********************************************************
				* 2. 기존 대차스케줄/재료 삭제
				**********************************************************/
				jrParam.setField("V_YD_EQP_ID", ydEqpId);

				//대차이송재료 초기화
				commDao.updTcarSch("InitMtl", jrParam);

				//대차스케줄 초기화
				commDao.updTcarSch("InitSch", jrParam);
				
				/**********************************************************
				* 3. 신규 대차스케줄 등록
				**********************************************************/
				//야드대차스케쥴ID 생성
				String ydTcarSchId = commDao.getSeqId(logId, methodNm, "TcarSch");

				if ("".equals(ydTcarSchId)) {
					throw new Exception( "대차스케줄ID 생성 중 오류가 발생하였습니다.");
				}
				
				//대차스케줄 등록
				jrParam.setField("V_YD_TCAR_SCH_ID"   , ydTcarSchId); //야드대차스케쥴ID
				jrParam.setField("V_YD_CAR_PROG_STAT" , "0"        ); //야드차량진행상태(상차대기)
				jrParam.setField("V_YD_CARLD_STOP_LOC", ydEqpId.substring(0, 1) + ydCurrBayGp + ydEqpId.substring(2)); //야드상차정지위치

				commDao.updTcarSch("InsSch", jrParam);
				
				/**********************************************************
				* 4. 대차 현재동 변경
				**********************************************************/
				jrParam.setField("YD_EQP_ID"     , ydEqpId    );
				jrParam.setField("YD_CURR_BAY_GP", ydCurrBayGp);

				jrRtn = slabUtils.addSndData(jrRtn, this.updTcarCurrBay(jrParam));
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}

	
	/**
	 *      [A] 오퍼레이션명 : 대차상태설정 등록처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtTcarStatSet(GridData gdReq) throws DAOException {
		String methodNm = "대차상태설정 등록처리[SlabYdJspSeEJB.trtTcarStatSet] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			String trtDtlGp = slabUtils.trim(gdReq.getParam("V_TRT_DTL_GP"));	//처리상세구분
			String ydEqpId  = slabUtils.trim(gdReq.getParam("V_YD_EQP_ID" ));	//야드설비ID(대차)
			String modifier = slabUtils.trim(gdReq.getParam("userid"      ));	//수정자
			String currDate = slabUtils.getDateTime14();						//현재시각

			if ("".equals(ydEqpId)) {
				throw new Exception("대차설비ID가 없습니다.");
			}

			//Return Value
			JDTORecord jrRtn = null;

			//EJB Call을 위한 Message 생성용 - Log ID, Method, 수정자 Set
			JDTORecord jrYdMsg = slabUtils.getParam(logId, methodNm, modifier);
			
			jrYdMsg.setField("YD_EQP_ID", ydEqpId); //야드설비ID

			if ("ST".equals(trtDtlGp)) {
				//설비상태 변경
				jrYdMsg.setField("JMS_TC_CD"          , "C3YDL008"); //설비고장복구실적
				jrYdMsg.setField("YD_EQP_STAT"        , slabUtils.trim(gdReq.getParam("YD_EQP_STAT"))); //야드설비상태(B:고장, N:정상)
				jrYdMsg.setField("YD_EQP_PAUSE_CODE"  , "0000"    ); //야드설비휴지코드
				jrYdMsg.setField("YD_EQP_TRBL_RCVR_DT", currDate  ); //야드설비고장복구일시

				EJBConnector sndConn = new EJBConnector("default", "SlabYdL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvY1YDL004", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
			} else if ("MD".equals(trtDtlGp)) {
				//작업Mode 변경
				jrYdMsg.setField("JMS_TC_CD"      , "C3YDL009"); //설비운전모드전환
				jrYdMsg.setField("YD_EQP_WRK_MODE", slabUtils.trim(gdReq.getParam("YD_EQP_WRK_MODE"))); //야드설비작업Mode(1:On-Line, 0:Off-Line)

				EJBConnector sndConn = new EJBConnector("default", "SlabYdL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvY1YDL003", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
			} else if ("HB".equals(trtDtlGp)) {
				//Home동 변경 - Log ID, Method, 수정자 Set
				JDTORecord jrParam = slabUtils.getParam(logId, methodNm, modifier);

				jrParam.setField("V_YD_EQP_ID"     , ydEqpId); //야드설비ID
				jrParam.setField("V_YD_HOME_BAY_GP", slabUtils.trim(gdReq.getParam("YD_HOME_BAY_GP")));
				
				commDao.updSlabYd("EqpHomeBay", jrParam);
			} else if ("CB".equals(trtDtlGp)) {
				//현재동 변경
				jrYdMsg.setField("YD_CURR_BAY_GP", slabUtils.trim(gdReq.getParam("YD_CURR_BAY_GP")));

				jrRtn = this.updTcarCurrBay(jrYdMsg);
			} else if ("TS".equals(trtDtlGp)) {
				//공대차출발지시 등록
				jrYdMsg.setField("YD_BAY_GP", slabUtils.trim(gdReq.getParam("YD_CURR_BAY_GP_TS"))); //야드동구분(상차동)

				jrRtn = slabComm.trtTcarSchLevWo(jrYdMsg);
			} else if ("TL".equals(trtDtlGp)) {
				//출발실적처리
				jrYdMsg.setField("JMS_TC_CD"      , "C3YDL007"); //대차이동실적
				jrYdMsg.setField("YD_TCAR_MOVE_GP", "S"       ); //야드대차이동구분(출발)
				jrYdMsg.setField("YD_BAY_GP1"     , slabUtils.trim(gdReq.getParam("YD_CURR_BAY_GP_TL"))); //야드동구분1(현재동)

				EJBConnector sndConn = new EJBConnector("default", "SlabYdL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvC3YDL007", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
			} else if ("TA".equals(trtDtlGp)) {
				//도착실적처리
				jrYdMsg.setField("JMS_TC_CD"      , "C3YDL007"); //대차이동실적
				jrYdMsg.setField("YD_TCAR_MOVE_GP", "E"       ); //야드대차이동구분(도착)
				jrYdMsg.setField("YD_BAY_GP1"     , slabUtils.trim(gdReq.getParam("YD_CURR_BAY_GP_TA"))); //야드동구분1(현재동)

				EJBConnector sndConn = new EJBConnector("default", "SlabYdL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvC3YDL007", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
			} else if ("TC".equals(trtDtlGp)) {
				//완료실적처리
				String ydCarProgStat = slabUtils.trim(gdReq.getHeader("YD_CAR_PROG_STAT").getValue(0));	//야드차량진행상태
				
				if ("4".equals(ydCarProgStat)) {
					//상차개시 -> 상차완료(영대차출발지시)
					jrRtn = slabComm.trtTcarSchLdCmpl(jrYdMsg);
				} else if ("D".equals(ydCarProgStat)) {
					//하차개시 -> 하차완료(공대차출발지시)
					jrYdMsg.setField("YD_BAY_GP", slabUtils.trim(gdReq.getParam("YD_CURR_BAY_GP_TC"))); //야드동구분(공대차출발지시 상차동)

					jrRtn = slabComm.trtTcarSchUdCmpl(jrYdMsg);
				}
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtDtlGp + "] 입니다.");
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}


	/**
	 *      [A] 오퍼레이션명 : 대차 현재동 변경
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updTcarCurrBay(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "대차 현재동 변경[SlabYdJspSeEJB.updTcarCurrBay] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			String ydEqpId        = slabUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"     )); //야드설비ID(대차)
			String ydCurrBayGpNew = slabUtils.trim(rcvMsg.getFieldString("YD_CURR_BAY_GP")); //야드현재동구분(신규)

			if ("".equals(ydEqpId)) {
				throw new Exception("설비ID가 없습니다.");
			} else if ("".equals(ydCurrBayGpNew)) {
				throw new Exception("변경할 현재동이 없습니다.");
			}

			//Return Value
			JDTORecord jrRtn = null;

			String ydCurrBayGpCur     = ""; //야드현재동구분(현재)
			String ydStkColGpCur      = ""; //야드적치열구분(현재)
			String ydStkColGpNew      = ""; //야드적치열구분(신규)
			String ydStkBedActStatCur = ""; //야드적치Bed활성상태(현재Bed)
			String ydStkBedActStatNew = ""; //야드적치Bed활성상태(신규Bed)
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(rcvMsg.getFieldString("V_MODIFIER")));

			jrParam.setField("V_YD_EQP_ID"         , ydEqpId       );
			jrParam.setField("V_YD_CURR_BAY_GP_NEW", ydCurrBayGpNew);
			
			/**********************************************************
			* 1. 대차Bed상태 조회
			**********************************************************/
			JDTORecordSet jsTcar = commDao.getStat("TcarBed", jrParam);

			if (jsTcar != null && jsTcar.size() > 0) {
		    	JDTORecord jrTcar = jsTcar.getRecord(0);

		    	ydCurrBayGpCur     = slabUtils.trim(jrTcar.getFieldString("YD_CURR_BAY_GP"         ));
			    ydStkColGpCur      = slabUtils.trim(jrTcar.getFieldString("YD_STK_COL_GP_CUR"      ));
			    ydStkColGpNew      = slabUtils.trim(jrTcar.getFieldString("YD_STK_COL_GP_NEW"      ));
			    ydStkBedActStatCur = slabUtils.trim(jrTcar.getFieldString("YD_STK_BED_ACT_STAT_CUR"));
			    ydStkBedActStatNew = slabUtils.trim(jrTcar.getFieldString("YD_STK_BED_ACT_STAT_NEW"));

			    if ("".equals(ydStkColGpNew)) {
					throw new Exception("변경할 적치열이 없습니다.");
				} else if ("".equals(ydStkBedActStatNew)) {
					throw new Exception("변경할 Bed[" + ydStkColGpNew + "] 활성상태가 없습니다.");
				}
		    } else {
				throw new Exception("대차 Bed상태 정보가 없습니다.");
		    }
			
			/**********************************************************
			* 2. 대차 저장위치 전체 비 활성화
			**********************************************************/
			jrParam.setField("V_YD_STK_COL_GP", ydEqpId.substring(0, 1) + "_" + ydEqpId.substring(2)); //야드적치열구분(대차전체Bed)

			//적치Bed(전체) 비활성화
			commDao.updStat("StkBedActCA", jrParam);

			//적치단 재료 삭제
			commDao.updSlabYd("StkLyrClr", jrParam);

			/**********************************************************
			* 3. 현재동 변경 및 저장위치제원 전문 조회
			**********************************************************/
			if (!ydCurrBayGpCur.equals(ydCurrBayGpNew)) {
				//설비 현재동 수정
				jrParam.setField("V_YD_CURR_BAY_GP", ydCurrBayGpNew);

				commDao.updSlabYd("EqpCurrBay", jrParam);

				//기존 Bed의 상태가 변경되었으면 저장위치제원(YDY1L001, YDY3L001) 전문 조회
				if ("L".equals(ydStkBedActStatCur)) {
					jrParam.setField("V_YD_INFO_SYNC_CD", "4"          ); //야드정보동기화코드(Bed)
					jrParam.setField("V_YD_STK_COL_GP"  , ydStkColGpCur); //야드적치열구분
					jrParam.setField("V_YD_STK_BED_NO"  , "01"         ); //야드적치Bed번호

					//전송Data 조회
					jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDY1L001", jrParam));
				}
			}
			
			/**********************************************************
			* 4. 신규 저장위치  활성화 및 저장위치제원 전문 조회
			**********************************************************/
			//신규 적치Bed Close 상태이면 활성화
			jrParam.setField("V_YD_STK_COL_GP"      , ydStkColGpNew); //야드적치열구분
			jrParam.setField("V_YD_STK_BED_NO"      , "01"         ); //야드적치Bed번호
			jrParam.setField("V_YD_STK_BED_ACT_STAT", "L"          ); //야드적치Bed활성상태(적치가능)

			commDao.updStat("StkBedAct", jrParam);

			//적치단 재료 삭제
			commDao.updSlabYd("StkLyrClr", jrParam);

			//신규 Bed의 상태가 변경되었으면 저장위치제원(YDY1L001, YDY3L001) 전문 전송
			if ("C".equals(ydStkBedActStatNew)) {
				jrParam.setField("V_YD_INFO_SYNC_CD", "4"); //야드정보동기화코드(Bed)

				//전송Data 조회
				jrRtn = slabUtils.addSndData(jrRtn, commDao.getMsgL2("YDY1L001", jrParam));
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}


	/**
	 *      [A] 오퍼레이션명 : 대차스케줄기준 조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getTcarSchRule(GridData gdReq) throws DAOException {
		String methodNm = "대차스케줄기준 조회[SlabYdJspSeEJB.getTcarSchRule] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			JDTORecordSet jrRst = jspDao.getTcarSchRule(gdReq);
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			return slabUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 대차스케줄기준 등록
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData trtTcarSchRule(GridData gdReq) throws DAOException {
		String methodNm = "대차스케줄기준 등록[SlabYdJspSeEJB.trtTcarSchRule] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			String ydEqpId          = slabUtils.trim(gdReq.getParam("V_YD_EQP_ID"         ));	//야드설비ID(대차)
			String autoTcarSchYn    = slabUtils.trim(gdReq.getParam("AUTO_TCAR_SCH_YN"    ));	//자동대차스케줄여부
			String autoTcarSchShMin = slabUtils.trim(gdReq.getParam("AUTO_TCAR_SCH_SH_MIN"));	//자동대차스케줄매수최소
			String autoTcarSchShMax = slabUtils.trim(gdReq.getParam("AUTO_TCAR_SCH_SH_MAX"));	//자동대차스케줄매수최대
			String modifier         = slabUtils.trim(gdReq.getParam("userid"              ));	//수정자

			if ("".equals(ydEqpId)) {
				throw new Exception("대차설비ID가 없습니다.");
			}

			if (!"Y".equals(autoTcarSchYn)) {
				autoTcarSchYn = "N";
			}

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, modifier);

			jrParam.setField("V_YD_EQP_ID"           , ydEqpId         ); //야드설비ID(대차)
			jrParam.setField("V_AUTO_TCAR_SCH_YN"    , autoTcarSchYn   ); //자동대차스케줄여부
			jrParam.setField("V_AUTO_TCAR_SCH_SH_MIN", autoTcarSchShMin); //자동대차스케줄매수최소
			jrParam.setField("V_AUTO_TCAR_SCH_SH_MAX", autoTcarSchShMax); //자동대차스케줄매수최대

			//설비기준 수정
			jspDao.updTcarSchRule("ER", jrParam);

			//검색기준 삭제
			jspDao.updTcarSchRule("SR", jrParam);
			
			//검색기준 등록
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			if (rowCnt > 0) {
				String[][] arrRule = new String[rowCnt][7];
				
				for (int ii = 0; ii < rowCnt; ii++) {
					arrRule[ii][0] = ydEqpId;					//야드설비ID
					arrRule[ii][1] = String.valueOf(ii + 1);	//순번
					arrRule[ii][2] = modifier;					//등록자
					arrRule[ii][3] = modifier;					//수정자
					arrRule[ii][4] = slabUtils.trim(gdReq.getHeader("YD_AIM_RT_GP").getComboHiddenValues()[gdReq.getHeader("YD_AIM_RT_GP").getSelectedIndex(ii)]);	//야드목표행선구분
					arrRule[ii][5] = slabUtils.trim(gdReq.getHeader("YD_LD_COL_GP").getValue(ii));	//야드상차열구분
					arrRule[ii][6] = slabUtils.trim(gdReq.getHeader("YD_UD_BAY_GP").getComboHiddenValues()[gdReq.getHeader("YD_UD_BAY_GP").getSelectedIndex(ii)]);	//야드하차동구분
				}
	
				//야드기준(TB_YD_RULE) 검색기준 Insert
				jspDao.insTcarSchRule("SR", arrRule, logId, methodNm);
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return this.getTcarSchRule(gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/***************************************************************************
	 * 이적작업예약등록
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 이적작업예약등록 조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getMvStkWrkBookReg(GridData gdReq) throws DAOException {
		String methodNm = "이적작업예약등록 조회[SlabYdJspSeEJB.getMvStkWrkBookReg] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			JDTORecordSet jrRst = jspDao.getMvStkWrkBookReg(gdReq);
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			return slabUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : 이적작업예약등록
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtMvStkWrkBookReg(GridData gdReq) throws DAOException {
		String methodNm = "이적작업예약등록[SlabYdJspSeEJB.trtMvStkWrkBookReg] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;
			JDTORecord jrRow = null;
			JDTORecordSet jsMsg = JDTORecordFactory.getInstance().createRecordSet("");	//크레인스케줄 정리를 위함

			String stlNos        = slabUtils.trim(gdReq.getParam("V_STL_NOS"         )); //재료번호들
			String ydStkColGp    = slabUtils.trim(gdReq.getParam("V_YD_STK_COL_GP"   )); //야드적치열구분(4자리 이상)
			String ydToLocGuide  = slabUtils.trim(gdReq.getParam("V_YD_TO_LOC_GUIDE" )); //야드To위치Guide
			String ydWrkPlanTcar = slabUtils.trim(gdReq.getParam("V_YD_WRK_PLAN_TCAR")); //야드작업계획대차
			String ydWrkCrn		 = slabUtils.trim(gdReq.getParam("V_YD_WRK_CRN"));       //야드작업크레인
			String chkGp		 = slabUtils.trim(gdReq.getParam("V_CHK_GP"));          //B열연이송여부  Y
			String chkGp2		 = slabUtils.trim(gdReq.getParam("V_CHK_GP2"));			//C열연이송여부 Y
			String schPrior 	 = slabUtils.trim(gdReq.getParam("V_YD_SCH_PRIOR"));     //야드스케줄우선순위
			//2023.01.04 연주 동간대차 하차크레인 지정기능 추가.--연주 김충만계장 요청 
			String ydWrkCrnUd		 = slabUtils.trim(gdReq.getParam("V_YD_WRK_CRN_UD"));       //야드대차하차작업크레인
			//2024.06.10 저장위치별 정보조회 하면에 크레인 이적작업 등록시 우선기동 가능하도록 기능개선 --연주 김충만 계장 요청 --//REQ202405572888
			String chkInstant		 = slabUtils.trim(gdReq.getParam("V_CHK_INSTANT"));       //우선기동 체크여부
			
			String strStrLoc	= slabUtils.trim(gdReq.getParam("V_STL_STR_LOC")); // 재료 대표 저장위치 추출
			
			slabUtils.printLog(logId, "trtMvStkWrkBookReg 재료저장위치 [" + strStrLoc + "]", "SL");
			
			slabUtils.printLog(logId, "trtMvStkWrkBookReg우선기동 체크여부[" + chkInstant + "]", "SL");
			if (ydStkColGp.length() < 4) {
				//혹시 이적 적치열구분 값이 잘못되어 있으면 무조건 01 Span 으로 처리
				if(ydStkColGp.startsWith("DB")) {
					ydStkColGp = ydStkColGp.substring(0, 2) + "03";
				}
				else {
					ydStkColGp = ydStkColGp.substring(0, 2) + "01";
				}
			} else if (ydStkColGp.length() > 6) {
				ydStkColGp = ydStkColGp.substring(0, 6);
			}
 
			if ("".equals(stlNos)) {
				throw new Exception("이적 재료번호가 없습니다.");
			} else if ("".equals(ydStkColGp) || ydStkColGp.length() < 4) {
				throw new Exception("Span[" + ydStkColGp + "] 정보가 없습니다.");
			} else if (  !"0".equals(ydStkColGp.substring(2, 3)) && 
						!"PU".equals(ydStkColGp.substring(2, 4)) && 
						!"PI".equals(ydStkColGp.substring(2, 4)) && 
						!"BK".equals(ydStkColGp.substring(2, 4)) &&
						!"DP".equals(ydStkColGp.substring(2, 4)) &&
						!"HD".equals(ydStkColGp.substring(2, 4)) &&
						!"RT".equals(ydStkColGp.substring(2, 4)) &&
						!"BT".equals(ydStkColGp.substring(2, 4)) &&
						!"TC".equals(ydStkColGp.substring(2, 4)) &&
						!"SB".equals(ydStkColGp.substring(2, 4)) &&
						!"GM".equals(ydStkColGp.substring(2, 4)) &&
						!"SH".equals(ydStkColGp.substring(2, 4)) &&						
						!"PT".equals(ydStkColGp.substring(2, 4))) {
				throw new Exception("적치열[" + ydStkColGp + "]에서는 이적 작업예약등록이 불가능합니다.");
			}
			
			/**********************************************************
			* 1. 스케줄코드 설정
			**********************************************************/
			String ydSchCd    = ""; //야드스케쥴코드
			String ydBayGp    = ydStkColGp.substring(1, 2); //야드동구분
			String ydAimBayGp = ""; //야드목표동구분

			if ("".equals(ydToLocGuide)) {
				//위치검색Bed기준 적용
				ydAimBayGp = ydStkColGp.substring(1, 2);
			} else if (ydToLocGuide.startsWith("MBHD01")) {
				JDTORecord jrParam = JDTORecordFactory.getInstance().create();
				JDTORecord jrChk = null;
				JDTORecordSet jsChk;
				SlabYdL2RcvDAO rcv2Dao = new SlabYdL2RcvDAO();
				
				if(ydToLocGuide.length() == 6) {
					jrParam.setField("V_YD_STK_COL_GP", ydToLocGuide.substring(0,6));
					jsChk = rcv2Dao.getY1YDL009("EmpBed", jrParam);
					
					if(jsChk.size() > 0) {
						jrChk = jsChk.getRecord(0);
						ydToLocGuide = slabUtils.trim(jrChk.getFieldString("YD_STK_COL_GP"))+slabUtils.trim(jrChk.getFieldString("YD_STK_BED_NO"));
						ydAimBayGp = ydToLocGuide.substring(1, 2);
					}
				} else {
					ydAimBayGp = ydToLocGuide.substring(1, 2);
				}
				
			} else if (ydToLocGuide.startsWith("DBPU05")) {
				String ydCoilOutdiaGrpGp ="";
				JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));
				jrParam.setField("V_YD_STK_COL_GP" , ydToLocGuide.substring(0, 6));
				jrParam.setField("V_YD_STK_BED_NO"  , ydToLocGuide.substring(6, 8));
				
				JDTORecordSet jsChk = schDao.getYDYDJ420("Rule", jrParam);
				
				if (jsChk != null && jsChk.size() > 0) {
					JDTORecord jrChk = jsChk.getRecord(0);
					ydCoilOutdiaGrpGp = slabUtils.trim(jrChk.getFieldString("YD_COIL_OUTDIA_GRP_GP")); //픽업크레인작업유무
				} 
				
				slabUtils.printLog(logId, "보급Bed[" + ydToLocGuide + "] 픽업 크레인이 현재 작업 여부 : " + ydCoilOutdiaGrpGp, "SL");
				
//				if("Y".equals(ydCoilOutdiaGrpGp)) {
//					throw new Exception("보급Bed[" + ydToLocGuide + "] 픽업 크레인이 현재 작업중입니다.");
//				}else {
//					//To위치지정
//					ydAimBayGp = ydToLocGuide.substring(1, 2);
//					//To위치가 동까지만 있으면 위치검색Bed 기준 적용
//					if (ydToLocGuide.length() < 4) {
//						ydToLocGuide = "";
//					}
//				}
				
				//To위치지정
				ydAimBayGp = ydToLocGuide.substring(1, 2);
				//To위치가 동까지만 있으면 위치검색Bed 기준 적용
				if (ydToLocGuide.length() < 4) {
					ydToLocGuide = "";
				}
				
			} else {
				//To위치지정
				ydAimBayGp = ydToLocGuide.substring(1, 2);
				//To위치가 동까지만 있으면 위치검색Bed 기준 적용
				if (ydToLocGuide.length() < 4) {
					ydToLocGuide = "";
				}
			}

			//스케쥴코드, 대차
			if (ydBayGp.equals(ydAimBayGp)) {
				String ydEqpGp = ydStkColGp.substring(2, 4); //야드설비구분(이적 스케줄코드 생성용)

				//To위치Guide가 있으면 그 값으로 스케줄코드 생성
				if ("".equals(ydToLocGuide)) {
					//Pickup Bed 이적일 경우
					if ("PU".equals(ydEqpGp)) {
						if (ydStkColGp.startsWith("A")) {
							if ("AAPUP9".equals(ydStkColGp)) {
								ydEqpGp = "04";	//보급 - #2 2차절단 A동 Pickup
							} else if ("AAPUPA".equals(ydStkColGp)) {
								ydEqpGp = "04";	//보급 - #3 2차절단 A동 Pickup
							} else if ("AAPUPB".equals(ydStkColGp)) {
								ydEqpGp = "01";	//인출 - #2 Scarfer A동 Pickup
							} else if ("ABPUP5".equals(ydStkColGp)) {
								ydEqpGp = "04";	//인출 - #1 Scarfer B동 Pickup
							} else if ("ACPUP7".equals(ydStkColGp)) {
								ydEqpGp = "05";	//인출 - #4 M/C C동 Pickup
							} else if ("ADPUP1".equals(ydStkColGp)) {
								ydEqpGp = "01";	//인출 - #1 M/C D동 Pickup
							} else if ("ADPUP3".equals(ydStkColGp)) {
								ydEqpGp = "03";	//인출 - #3 M/C D동 Pickup
							} else if ("ADPUP8".equals(ydStkColGp)) {
								ydEqpGp = "06";	//인출 - #5 M/C D동 Pickup
							} else {
								ydEqpGp = "02";
							}
						} else if (ydStkColGp.startsWith("DA")) {
							if ("DAPU02".equals(ydStkColGp)) {
								ydEqpGp = "03";	//인출 - #1 2차절단 A동 Piler
							} else if ("DAPU04".equals(ydStkColGp)) {
								ydEqpGp = "04";	//인출 - #3 2차절단 A동 Piler
							} else {
								ydEqpGp = "01";
							}
						} else if (ydStkColGp.startsWith("DB")) {
							if ("DBPU06".equals(ydStkColGp)) {
								ydEqpGp = "03";	//인출 - #2 2차절단 B동 Piler
							} else {
								ydEqpGp = "04";
							}
						}
					}else if ("PI".equals(ydEqpGp)) {
						if (ydStkColGp.startsWith("A")) {
							if ("ACPI01".equals(ydStkColGp)) {
								ydEqpGp = "01";	//인출 - #1 M/C     C동 Piler
							} else if ("ACPI03".equals(ydStkColGp)) {
								ydEqpGp = "02";	//인출 - #3 M/C     C동 Piler
							} else if ("ADPI04".equals(ydStkColGp)) {
								ydEqpGp = "05";	//인출 - #4 M/C     D동 Piler
							} else if ("ACPI05".equals(ydStkColGp)) {
								ydEqpGp = "06";	//인출 - #5 M/C     C동 Piler
							} else {
								ydEqpGp = "03";
							}
						}
					}else if ("DP".equals(ydEqpGp)) {
						if (ydStkColGp.startsWith("A")) {
							if ("ACDP01".equals(ydStkColGp)) {
								ydEqpGp = "04";	//인출 - #1 Scarfer C동 Depiler
							} else if ("ABDP03".equals(ydStkColGp)) {
								ydEqpGp = "01";	//인출 - #2 Scarfer B동 Depiler
							} else if ("AADP02".equals(ydStkColGp)) {
								ydEqpGp = "04";	//인출 - #1 2차절단 A동 Depiler
							} else if ("AADP04".equals(ydStkColGp)) {
								ydEqpGp = "01";	//인출 - 그라인딩   A동 Depiler
							} else if ("ABDP04".equals(ydStkColGp)) {
								ydEqpGp = "01";	//인출 - 그라인딩   B동 Depiler
							} else {
								ydEqpGp = "01";
							}
						}
					}
					else if("RT".equals(ydEqpGp)){
						if (ydStkColGp.startsWith("MART")) {
							ydEqpGp = "01";
						}
					} else if("SB".equals(ydEqpGp)){
						if (ydStkColGp.startsWith("A")) {
							if("A".equals(ydBayGp)){
								ydEqpGp = "01";	//C#2 핸드장
							}
							else if("B".equals(ydBayGp)){
								if("ABSB04".equals(ydStkColGp)){
									ydEqpGp = "04";	//C#1 핸드장 오른쪽
								}								
								else if (!"ABSB01".equals(ydStkColGp)){
									ydEqpGp = "03";	//C#1 핸드장 왼쪽
								}
								else {
									ydEqpGp = "03";	
								}
							}	
							
						}
					} else if("GM".equals(ydEqpGp)){
						if (ydStkColGp.startsWith("A")) {
							ydEqpGp = "01";	
						}
					} else if("SH".equals(ydEqpGp)){
						//DEFAULT는 1부 코드이나, 아래서 2부 재료 작업 걸 경우 2부로 변경되도록 설정.
						if (ydStkColGp.startsWith("A")) {
							if("ACSH25".equals(ydStkColGp)){
								ydEqpGp = "06";	//2부지상절단장
							} else if("ACSH26".equals(ydStkColGp)){
								ydEqpGp = "06";	//2부지상절단장
							} else if("ACSH27".equals(ydStkColGp)){
								ydEqpGp = "06";	//2부지상절단장
							} else if ("ACSH2".equals(ydStkColGp)){
								ydEqpGp = "06";	//2부지상절단장
							} else if ("ACSH0".equals(ydStkColGp)){
								ydEqpGp = "02";	//1부지상절단장
							} else {
								ydEqpGp = "02";	//1부지상절단장
							}
						
						}
					}
				} else {
					if ("PI".equals(ydEqpGp)) {
						if (ydStkColGp.startsWith("A")) {
							if ("ACPI01".equals(ydStkColGp)) {
								ydEqpGp = "01";	//인출 - #1 M/C     C동 Piler
							} else if ("ACPI03".equals(ydStkColGp)) {
								ydEqpGp = "02";	//인출 - #3 M/C     C동 Piler
							} else if ("ADPI04".equals(ydStkColGp)) {
								ydEqpGp = "05";	//인출 - #4 M/C     D동 Piler
							} else if ("ACPI05".equals(ydStkColGp)) {
								ydEqpGp = "06";	//인출 - #5 M/C     C동 Piler
							} else {
								ydEqpGp = "03";
							}
						}
					}else if ("DP".equals(ydEqpGp)) {
						if (ydStkColGp.startsWith("A")) {
							if ("ACDP01".equals(ydStkColGp)) {
								ydEqpGp = "04";	//인출 - #1 Scarfer C동 Depiler
							} else if ("ABDP03".equals(ydStkColGp)) {
								ydEqpGp = "01";	//인출 - #2 Scarfer B동 Depiler
							} else if ("AADP02".equals(ydStkColGp)) {
								ydEqpGp = "04";	//인출 - #1 2차절단 A동 Depiler
							} else if ("AADP04".equals(ydStkColGp)) {
								ydEqpGp = "01";	//인출 - 그라인딩   A동 Depiler
							} else if ("ABDP04".equals(ydStkColGp)) {
								ydEqpGp = "01";	//인출 - 그라인딩   B동 Depiler
							} else {
								ydEqpGp = "01";
							}
						}
					}else if ("BT".equals(ydEqpGp)) {
						if (ydStkColGp.startsWith("MABT")) {
							ydEqpGp = "01";
						}
					}else if("RT".equals(ydEqpGp)){
						if (ydStkColGp.startsWith("MART")) {
							ydEqpGp = "01";
						}
					}else if("SB".equals(ydEqpGp)){
						if (ydStkColGp.startsWith("A")) {
							if("A".equals(ydBayGp)){
								ydEqpGp = "01";	//C#2 핸드장
							}
							else if("B".equals(ydBayGp)){
								if("ABSB04".equals(ydStkColGp)){
									ydEqpGp = "04";	//C#1 핸드장 오른쪽
								}								
								else {
									ydEqpGp = "03";	
								}
							}	
							
						}
					} else if("GM".equals(ydEqpGp)){
						if (ydStkColGp.startsWith("A")) {
							ydEqpGp = "01";	
						}
					} else if("SH".equals(ydEqpGp)){
						//DEFAULT는 1부 코드이나, 아래서 2부 재료 작업 걸 경우 2부로 변경되도록 설정.
						if (ydStkColGp.startsWith("A")) {
							if("ACSH25".equals(ydStkColGp)){
								ydEqpGp = "06";	//2부지상절단장
							} else if("ACSH26".equals(ydStkColGp)){
								ydEqpGp = "06";	//2부지상절단장
							} else if("ACSH27".equals(ydStkColGp)){
								ydEqpGp = "06";	//2부지상절단장
							} else if ("ACSH2".equals(ydStkColGp)){
								ydEqpGp = "06";	//2부지상절단장
							} else if ("ACSH0".equals(ydStkColGp)){
								ydEqpGp = "02";	//1부지상절단장
							} else {
								ydEqpGp = "02";	//1부지상절단장
							}
						
						}
					}
					else{
						ydEqpGp = ydStkColGp.substring(2, 4);
					}
				}
				
				ydSchCd = ydStkColGp.substring(0, 2) + "YD" + ydEqpGp + "MM";
				ydWrkPlanTcar = "";
				
				if("A".equals(ydStkColGp.substring(0, 1)) && "RT".equals(ydEqpGp)){
					if(ydStkColGp.length() == 6){
						ydSchCd = ydStkColGp + "LM";
					}
					else{
						ydSchCd = ydStkColGp.substring(0, 4) + "01LM";
					}
					 
				}
				
				if("A".equals(ydStkColGp.substring(0, 1)) && "TC".equals(ydEqpGp)){
					if(ydStkColGp.length() == 6){
						ydSchCd = ydStkColGp + "LM";
					}
					else{
						ydSchCd = ydStkColGp.substring(0, 4) + "01LM";
					}
					
				}
				if("A".equals(ydStkColGp.substring(0, 1)) && "PT".equals(ydEqpGp)){
					if(ydStkColGp.length() == 6){
						if( "ACPT01".equals(ydStkColGp) || "ACPT02".equals(ydStkColGp) || "ACPT03".equals(ydStkColGp) ||
								"ADPT01".equals(ydStkColGp) || "ADPT02".equals(ydStkColGp) || "ADPT03".equals(ydStkColGp) || "ADPT03".equals(ydStkColGp)){
							ydSchCd = ydStkColGp.substring(0, 4) + "01LM";
						}
						else if( "AAPT04".equals(ydStkColGp) || "ABPT04".equals(ydStkColGp) || "ACPT04".equals(ydStkColGp) ||
								 "AAPT05".equals(ydStkColGp) || "ABPT05".equals(ydStkColGp) || "ACPT05".equals(ydStkColGp) ){
							ydSchCd = ydStkColGp.substring(0, 4) + "02LM";
						}
						else if( "AAPT06".equals(ydStkColGp) || "ABPT06".equals(ydStkColGp) || "ACPT06".equals(ydStkColGp) ||
								 "ADPT06".equals(ydStkColGp) ){
							ydSchCd = ydStkColGp.substring(0, 4) + "03LM";
						}
						else if( "AAPT07".equals(ydStkColGp) || "ABPT07".equals(ydStkColGp) ){
							ydSchCd = ydStkColGp.substring(0, 4) + "04LM";
						}
						else if( "ACPT07".equals(ydStkColGp) || "ACPT08".equals(ydStkColGp) || "ACPT09".equals(ydStkColGp) ||
								 "ADPT07".equals(ydStkColGp) || "ADPT08".equals(ydStkColGp) || "ADPT09".equals(ydStkColGp) ){
							ydSchCd = ydStkColGp.substring(0, 4) + "05LM";
						}
						else {
							ydSchCd = ydStkColGp.substring(0, 4) + "01LM";
						}
					}
					else{
						ydSchCd = ydStkColGp.substring(0, 4) + "01LM";
					}
					
				}
				
				if("M".equals(ydStkColGp.substring(0, 1)) && "PT".equals(ydEqpGp)){
					ydSchCd = ydStkColGp.substring(0, 4) + "01LM";
				}
				
				//2025.03.11 연주 김충만 계장 요청. 
				//픽업베드->야드 TO위치 지정 후 이적작업예약올릴 시, 스케줄코드가 ADYDPUMM 으로 고정되어
				//D동 #1,#3,#5 머신 픽업에서 불출시 모두 같은 스케줄코드가 부여되어, 크레인 지정에 불편.
				//PU 별 스케줄 분리 요청 
				if("A".equals(ydStkColGp.substring(0, 1)) && "PU".equals(ydEqpGp)){
					if(ydStkColGp.length() == 6){
						//입고 픽업
						if ("ADPUP1".equals(ydStkColGp)) {
							ydSchCd = "ADPU01LM";	//D동 #1 M/C 입고 PU01
						}
						else if ("ACPUP2".equals(ydStkColGp)) {
							ydSchCd = "ACPU02LM";	//C동 #2 M/C 입고 PU02
						}
						else if ("ADPUP3".equals(ydStkColGp)) {
							ydSchCd = "ADPU03LM";	//D동 #3 M/C 입고 PU03
						}
						else if ("ACPUP7".equals(ydStkColGp)) {
							ydSchCd = "ACPU07LM";	//C동 #4 M/C 입고 PU07
						}
						else if ("ADPUP8".equals(ydStkColGp)) {
							ydSchCd = "ADPU08LM";	//D동 #5 M/C 입고 PU08
						}
						
						//장입, 스카퍼, 2차절단 입고 픽업에서 뺄 경우 (재료 이상, 테스트 등)
						if ("AAPUP4".equals(ydStkColGp)) {
							ydSchCd = "AAPU04LM";	//A동 C열연 장입 입고 
						} else if ("ABPUP6".equals(ydStkColGp)) {
							ydSchCd = "ABPU06LM";	//B동 C열연 장입 입고
						} else if ("ABPUP5".equals(ydStkColGp)) {
							ydSchCd = "ABPU05LM";	//B동 #1 Scarfer 입고
						} else if ("AAPUP9".equals(ydStkColGp)) {
							ydSchCd = "AAPU09LM";	//A동 #2 2차절단 입고
						} else if ("AAPUPA".equals(ydStkColGp)) {
							ydSchCd = "AAPU10LM";	//A동 #3 2차절단 입고
						} else if ("AAPUPB".equals(ydStkColGp)) {
							ydSchCd = "AAPU11LM";	//A동 #2 Scarfer 입고PU11
						}
					}
					else{
						if(!"".equals(strStrLoc)){
							//입고 픽업
							if (strStrLoc.startsWith("ADPUP1")) {
								ydSchCd = "ADPU01LM";	//D동 #1 M/C 입고 PU01
							}
							else if (strStrLoc.startsWith("ACPUP2")) {
								ydSchCd = "ACPU02LM";	//C동 #2 M/C 입고 PU02
							}
							else if (strStrLoc.startsWith("ADPUP3")) {
								ydSchCd = "ADPU03LM";	//D동 #3 M/C 입고 PU03
							}
							else if (strStrLoc.startsWith("ACPUP7")) {
								ydSchCd = "ACPU07LM";	//C동 #4 M/C 입고 PU07
							}
							else if (strStrLoc.equals("ADPUP8")) {
								ydSchCd = "ADPU08LM";	//D동 #5 M/C 입고 PU08
							}
							
							//장입, 스카퍼, 2차절단 입고 픽업에서 뺄 경우 (재료 이상, 테스트 등)
							if (strStrLoc.startsWith("AAPUP4")) {
								ydSchCd = "AAPU04LM";	//A동 C열연 장입 입고 
							} else if (strStrLoc.startsWith("ABPUP6")) {
								ydSchCd = "ABPU06LM";	//B동 C열연 장입 입고
							} else if (strStrLoc.startsWith("ABPUP5")) {
								ydSchCd = "ABPU05LM";	//B동 #1 Scarfer 입고
							} else if (strStrLoc.startsWith("AAPUP9")) {
								ydSchCd = "AAPU09LM";	//A동 #2 2차절단 입고
							} else if (strStrLoc.startsWith("AAPUPA")) {
								ydSchCd = "AAPU10LM";	//A동 #3 2차절단 입고
							} else if (strStrLoc.startsWith("AAPUPB")) {
								ydSchCd = "AAPU11LM";	//A동 #2 Scarfer 입고
							}
						}
						
					}
				}
		
			} else {
				if ("".equals(ydWrkPlanTcar)) {
					throw new Exception("To위치지정 동간이적 대차 정보가 없습니다.");
				}
				ydSchCd = ydStkColGp.substring(0, 2) + ydWrkPlanTcar.substring(2) + "UM";
			}
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));

			jrParam.setField("V_STL_NOS"      , stlNos    ); //재료번호들
			jrParam.setField("V_YD_STK_COL_GP", ydStkColGp); //야드적치열구분
			jrParam.setField("V_CHK_GP", chkGp); 			 // B열연이송여부  Y
			jrParam.setField("V_YD_TO_LOC_GUIDE", ydToLocGuide); //to위치가이드  18.10.30 추가



			//작업예약 대상재료 조회
			//후판슬라브야드의 경우 체크한 슬라브 순서대로 작업예약 대상재 조회
			JDTORecordSet jsWbMtl = JDTORecordFactory.getInstance().createRecordSet("");

			// 2022.09.27 수정 시작 -------------------------------------	          
			//연주슬라브야드의 경우 체크한 슬라브  체크 순서대로 작업예약 대상재 조회
			if("A".equals(ydStkColGp.substring(0,1))) {
			    jsWbMtl = jspDao.getMvStkWrkBookReg("MvMtl2", jrParam);

			} else {
				jsWbMtl = jspDao.getMvStkWrkBookReg("MvMtl", jrParam);
			}
			// 2022.09.27 수정 종료 --------------------------------------  

			int rowCnt = jsWbMtl.size();

			if (rowCnt <= 0) {
				throw new Exception("이적 재료 정보가 없습니다.");
			}
			


			//---REQ20211036362020 
			//21.12.01 연주1부 최광석계장님이적작업예약 등록 시, 지정크레인에 대한 작업재료 작업 가능여부 확인
			if(!"".equals(ydWrkCrn)){
				slabUtils.printLog(logId, "지정크레인[" + ydWrkCrn + "]에 대하여 작업재료 작업가능여부 확인", "SL");
				jrParam.setField("YD_EQP_ID", ydWrkCrn);
				JDTORecordSet jrResultSet = commDao.select(jrParam, "com.inisteel.cim.yd.dao.ydcrnspecdao.YdCrnspecDao.getYdCrnspec", logId, methodNm, "크레인 사양 체크");
			
				if(jrResultSet == null || jrResultSet.size() <=0){
					throw new Exception("지정크레인에 대한 사양정보가 없습니다.");
				}	
				/*,YD_WRK_ABLE_L  AS YD_WRK_ABLE_L
			      ,YD_WRK_ABLE_W  AS YD_WRK_ABLE_W
			      ,YD_WRK_ABLE_SH  AS YD_WRK_ABLE_SH
			      ,YD_WRK_ABLE_WT  AS YD_WRK_ABLE_WT
			      ;*/
				int ydWrkCrnAbleL  =  Integer.parseInt(slabUtils.nvl(jrResultSet.getRecord(0).getFieldString("YD_WRK_ABLE_L"),"0"));
				int ydWrkCrnAbleW  =  Integer.parseInt(slabUtils.nvl(jrResultSet.getRecord(0).getFieldString("YD_WRK_ABLE_W"),"0"));
				int ydWrkCrnAbleWt = Integer.parseInt(slabUtils.nvl(jrResultSet.getRecord(0).getFieldString("YD_WRK_ABLE_WT"),"0"));
				String szRtnMsg    = "";
				slabUtils.printLog(logId, "지정크레인[" + ydWrkCrn + "] 작업가능 길이 ["+Integer.toString(ydWrkCrnAbleL)+"] " +
						"작업가능 폭 ["+Integer.toString(ydWrkCrnAbleW)+"] 작업가능 중량 ["+Integer.toString(ydWrkCrnAbleWt)+"]", "SL");
				
				//Integer.parseInt(arg0)
				for(int i=0; i< rowCnt ; i++){
					jrRow = jsWbMtl.getRecord(i);
					
					String stlNo = slabUtils.nvl(jrRow.getFieldString("STL_NO"),"");
					int ydMtlWt  = Integer.parseInt(slabUtils.nvl(jrRow.getFieldString("YD_MTL_WT"),"0")); //재료 중량
					int ydMtlW   = Integer.parseInt(slabUtils.nvl(jrRow.getFieldString("YD_MTL_W"),"0")); //재료 폭
					int ydMtlL   = Integer.parseInt(slabUtils.nvl(jrRow.getFieldString("YD_MTL_L"),"0")); //재료 길이
					
					if(ydMtlWt > ydWrkCrnAbleWt) szRtnMsg = Integer.toString(i+1)+"번째 작업재료 ["+stlNo+"] 재료 중량이 지정크레인["+ydWrkCrn+"] 의 작업가능 중량 ["+ydWrkCrnAbleWt+"] 보다 큼";
					else if(ydMtlW > ydWrkCrnAbleW) szRtnMsg = Integer.toString(i+1)+"번째 작업재료 ["+stlNo+"] 재료 폭이 지정크레인["+ydWrkCrn+"] 의 작업가능 폭 ["+ydWrkCrnAbleW+"] 보다 큼";
					else if(ydMtlL > ydWrkCrnAbleL) szRtnMsg = Integer.toString(i+1)+"번째 작업재료 ["+stlNo+"] 재료 길이가 지정크레인["+ydWrkCrn+"] 의 작업가능 길이 ["+ydWrkCrnAbleL+"] 보다 큼";
					
					if(!"".equals(szRtnMsg)) {
						slabUtils.printLog(logId, "지정크레인[" + ydWrkCrn + "] 작업불가 사유: "+szRtnMsg, "SL");
						jrRtn = JDTORecordFactory.getInstance().create();
						jrRtn.setResultMsg(szRtnMsg);
						return jrRtn;
					}
				}
				
			}
			
			//---END REQ202110363620
			
			//연주 지상절단장. 스케줄코드 재설정 --연주 김충만 계장 요청.
			//저장위치를 ACSH 만 입력하고 이적 작업을 걸 경우, DEFAULT 로 ACYD02MM 으로 설정되는데, ACSH2 재료 있을때 스케줄코드 재설정
			String tempStk = jsWbMtl.getRecord(0).getFieldString("YD_STK_COL_GP");
			if(ydSchCd.startsWith("ACYD") && tempStk.startsWith("ACSH2")) {
				ydSchCd = "ACYD06MM";
			}
			
			/**********************************************************
			* 2. 이적 작업예약 등록
			**********************************************************/
			jrParam.setField("V_YD_SCH_CD"       	, ydSchCd      ); //야드스케쥴코드
			jrParam.setField("V_YD_AIM_BAY_GP"   	, ydAimBayGp   ); //야드목표동구분
			jrParam.setField("V_YD_TO_LOC_GUIDE" 	, ydToLocGuide ); //야드To위치Guide
			jrParam.setField("V_YD_WRK_PLAN_TCAR"	, ydWrkPlanTcar); //야드작업계획대차
			jrParam.setField("V_YD_WRK_PLAN_CRN" 	, ydWrkCrn);      //야드작업크레인
			jrParam.setField("V_YD_SCH_PRIOR"	 	, schPrior);      //야드크레인작업순위
			//2023.01.04 연주 동간대차 하차크레인 지정기능 추가.--CAR_NO를 하차크레인으로 --연주 김충만계장 요청 
			jrParam.setField("V_YD_WRK_PLAN_CRN_UD" , ydWrkCrnUd);     //야드대차하차작업크레인
			//2024.06.10 저장위치별 정보조회 하면에 크레인 이적작업 등록시 우선기동 가능하도록 기능개선 --연주 김충만 계장 요청 --//REQ202405572888
			jrParam.setField("V_CHK_INSTANT" , chkInstant);     //우선기동 체크 여부

			//작업예약등록
			jsMsg.addRecord(this.insMvstkWrkBook(jrParam, jsWbMtl));
			
			/**********************************************************
			* 3. 대차작업이 있으면 공대차출발지시 처리
			**********************************************************/
			if (!"".equals(ydWrkPlanTcar)) {
				//공대차출발지시 처리시 Exception을 발생시키지 않기위해 미리 Check
				String msgTcar = ""; //공대차출발지시 처리 메세지
			 			
				//대차스케쥴정보(공대차출발지시) 조회
				jrParam.setField("V_YD_EQP_ID", ydWrkPlanTcar);	//야드설비ID(대차)

				JDTORecordSet jsChk = commDao.getTcarSch("LevWo", jrParam);

				if (jsChk != null && jsChk.size() > 0) {
					JDTORecord jrChk = jsChk.getRecord(0);

					String ydTcarSchId   = slabUtils.trim(jrChk.getFieldString("YD_TCAR_SCH_ID"  ));
					String ydWbookIdCurr = slabUtils.trim(jrChk.getFieldString("YD_WBOOK_ID_CURR"));

					if ("B".equals(slabUtils.trim(jrChk.getFieldString("YD_EQP_STAT")))) {
						msgTcar = "고장";
					} else if (!"1".equals(slabUtils.trim(jrChk.getFieldString("YD_EQP_WRK_MODE")))) {
						msgTcar = "Off-Line";
					} else if ("Y".equals(slabUtils.trim(jrChk.getFieldString("TC_MTL_YN")))) {
						msgTcar = "대차스케줄[" + ydTcarSchId + "] 이송재료 존재";
					} else if (!"".equals(ydWbookIdCurr)) {
						msgTcar = "대차스케줄[" + ydTcarSchId + "] 상차작업예약[" + ydWbookIdCurr + "] 존재";
					}
				} else {
					msgTcar = "정보 없음";
			    }
				
				//공대차출발지시 처리
				if ("".equals(msgTcar)) {
					jrParam.setField("YD_EQP_ID", ydWrkPlanTcar); //야드설비ID(대차)
					jrParam.setField("YD_BAY_GP", ydBayGp      ); //야드동구분(상차동)

					jrRtn = slabComm.trtTcarSchLevWo(jrParam);
				} else {
					slabUtils.printLog(logId, "대차[" + ydWrkPlanTcar + "] 공대차출발지시 불가 : " + msgTcar, "SL");
				}
			}

			/**********************************************************
			* 4. 동내이적(대차작업이 없음)작업 크레인별 첫번째 스케줄 전송
			**********************************************************/
			jrRtn = slabUtils.addSndData(jrRtn, this.setCrnSchMsg(jsMsg, logId, methodNm));
			
			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}

	
	/**
	 *      [A] 오퍼레이션명 : 이적작업예약등록
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord insMvstkWrkBook(JDTORecord jrParam, JDTORecordSet jsWbMtl) throws DAOException {
		String methodNm = "이적작업예약등록[SlabYdJspSeEJB.insMvstkWrkBook] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		JDTORecord       recPara         = JDTORecordFactory.getInstance().create();
		YdDelegate ydDelegate = new YdDelegate();
		try {
			slabUtils.printLog(logId, methodNm, "S+");

			String ydSchCd       = slabUtils.trim(jrParam.getFieldString("V_YD_SCH_CD"       )); //야드스케쥴코드
			String ydAimBayGp    = slabUtils.trim(jrParam.getFieldString("V_YD_AIM_BAY_GP"   )); //야드목표동구분
			String ydToLocGuide  = slabUtils.trim(jrParam.getFieldString("V_YD_TO_LOC_GUIDE" )); //야드To위치Guide
			String ydWrkPlanTcar = slabUtils.trim(jrParam.getFieldString("V_YD_WRK_PLAN_TCAR")); //야드작업계획대차
			String ydWrkPlanCrn  = slabUtils.trim(jrParam.getFieldString("V_YD_WRK_PLAN_CRN"));  //야드작업크레인
			String modifier      = slabUtils.trim(jrParam.getFieldString("V_MODIFIER"        )); //수정자
			String chkGp		 = slabUtils.trim(jrParam.getFieldString("V_CHK_GP"));          //B,C열연이송여부  Y
			String ydSchPrior	 = slabUtils.trim(jrParam.getFieldString("V_YD_SCH_PRIOR"));	//야드크레인작업순위
			//2023.01.04 연주 동간대차 하차크레인 지정기능 추가.--CAR_NO를 하차크레인으로 --연주 김충만계장 요청 
			String ydWrkPlanCrnUd = slabUtils.trim(jrParam.getFieldString("V_YD_WRK_PLAN_CRN_UD"));  //야드대차하차작업크레인
			//2024.06.10 저장위치별 정보조회 하면에 크레인 이적작업 등록시 우선기동 가능하도록 기능개선 --연주 김충만 계장 요청 --//REQ202405572888
			String chkInstant		 = slabUtils.trim(jrParam.getFieldString("V_CHK_INSTANT"));       //우선기동 체크여부
			/**********************************************************
			* 1. 야드스케쥴코드 Check
			**********************************************************/
			jrParam.setField("CALL_METHOD_NM", "insMvstkWrkBook"); //기동금지 스케줄 예약이 가능하도록 직전 호출 메소드명 추가 2024.02.15
			JDTORecord jrCrnSpec = slabComm.chkSchCdEqp(jrParam);
			
			String ydGp       = ydSchCd.substring(0, 1);	//야드구분
			String ydBayGp    = ydSchCd.substring(1, 2);	//야드동구분
			String ydEqpId    = slabUtils.trim(jrCrnSpec.getFieldString("YD_EQP_ID"   ));	//야드설비ID(크레인)
			
			/*
			 * 2016.01.20 윤재광 
			 * - 크레인 지정기능 추가 
			 */
			if(!"".equals(ydWrkPlanCrn)){
				ydEqpId = ydWrkPlanCrn;
			}
			
			if("".equals(ydSchPrior)) {
				ydSchPrior = slabUtils.trim(jrCrnSpec.getFieldString("YD_SCH_PRIOR"));	//야드스케쥴우선순위
			}
			
			String ydToLocDcsnMtd = "S"; //야드TO위치결정방법(스케줄지정)

			if ("".equals(ydAimBayGp)) {
				ydAimBayGp = ydBayGp;
			}

			if (ydBayGp.equals(ydAimBayGp)) {
				ydWrkPlanTcar = "";
			}

			if (!"".equals(ydToLocGuide)) {
				ydToLocDcsnMtd = "F";	//야드TO위치결정방법(지정위치)
			}
			
			/**********************************************************
			* 2. 크레인사양 분리
			**********************************************************/
			Vector vcLot = this.setCrnSpecSpr(jrCrnSpec, jsWbMtl);

			JDTORecordSet jsLotMtl = null;
			int lotCnt   = vcLot.size();	//크레인사양 분리 작업예약수
			int lotMtlSh = 0;				//작업예약재료매수
			String ydWbookId = "";			//야드작업예약ID
			String ydWbookIdFst = "";		//야드작업예약ID(첫번째)
			JDTORecord jrRow = null;

			/**********************************************************
			* 3. 작업예약 등록
			**********************************************************/
			for (int ii = 0; ii < lotCnt; ii++) {
				//작업예약재료
				jsLotMtl = (JDTORecordSet)vcLot.get(ii);
				lotMtlSh = jsLotMtl.size();

				if (lotMtlSh <= 0) {
					continue;
				}

				//작업예약ID 조회
				ydWbookId = commDao.getSeqId(logId, methodNm, "WrkBook");

				if ("".equals(ydWbookId)) {
					throw new Exception("작업예약ID 생성 실패");
				}
				
				//크레인스케줄 기동용
				if (ii == 0) {
					ydWbookIdFst = ydWbookId;
				}
				
				//to위치가 MBHD01이면서 첫번째 작업예약 등록이 아닌 경우 to위치 지정 다시함
				if (ii != 0 && ydToLocGuide.startsWith("MBHD01")) {
//					JDTORecord jrParam2 = JDTORecordFactory.getInstance().create();
//					JDTORecord jrChk = null;
//					JDTORecordSet jsChk;
//					SlabYdL2RcvDAO rcv2Dao = new SlabYdL2RcvDAO();
//					
//					jrParam2.setField("V_YD_STK_COL_GP", ydToLocGuide.substring(0,6));
//					jrParam2.setField("V_YD_STK_BED_NO", String.valueOf(Integer.parseInt(ydToLocGuide.substring(6,8))+1));
//					
//					jsChk = rcv2Dao.getY1YDL009("EmpBed", jrParam2);
//					
//					if(jsChk.size() > 0) {
//						jrChk = jsChk.getRecord(0);
//						ydToLocGuide = slabUtils.trim(jrChk.getFieldString("YD_STK_COL_GP"))+slabUtils.trim(jrChk.getFieldString("YD_STK_BED_NO"));
//						ydAimBayGp = ydToLocGuide.substring(1, 2);
//					}
					ydToLocGuide = "MBHD01";
				}
				
				//작업예약 등록
				jrParam.setField("V_YD_WBOOK_ID"       , ydWbookId     ); //야드작업예약ID
				jrParam.setField("V_MODIFIER"          , modifier      ); //수정자
				jrParam.setField("V_YD_GP"             , ydGp          ); //야드구분
				jrParam.setField("V_YD_BAY_GP"         , ydBayGp       ); //야드동구분
				jrParam.setField("V_YD_SCH_CD"         , ydSchCd       ); //야드스케쥴코드
				jrParam.setField("V_YD_SCH_PRIOR"      , ydSchPrior    ); //야드스케쥴우선순위
				jrParam.setField("V_YD_SCH_PROG_STAT"  , "W"           ); //야드스케쥴진행상태(스케줄수행대기)
				jrParam.setField("V_YD_SCH_ST_GP"      , "M"           ); //야드스케쥴기동구분(Manual)
				jrParam.setField("V_YD_SCH_REQ_GP"     , "M"           ); //야드스케쥴요청구분(이적)
				jrParam.setField("V_YD_AIM_YD_GP"      , ydGp          ); //야드목표야드구분
				jrParam.setField("V_YD_AIM_BAY_GP"     , ydAimBayGp    ); //야드목표동구분
				jrParam.setField("V_YD_TO_LOC_DCSN_MTD", ydToLocDcsnMtd); //야드TO위치결정방법
				jrParam.setField("V_YD_TO_LOC_GUIDE"   , ydToLocGuide  ); //야드To위치Guide
				jrParam.setField("V_YD_WRK_PLAN_TCAR"  , ydWrkPlanTcar ); //야드작업계획대차
				jrParam.setField("V_YD_WRK_PLAN_CRN"   , ydWrkPlanCrn ); //야드작업크레인
				//2023.01.04 연주 동간대차 하차크레인 지정기능 추가.--CAR_NO를 하차크레인으로 --연주 김충만계장 요청 
				jrParam.setField("V_CAR_NO"   , ydWrkPlanCrnUd ); //야드대차하차작업크레인
				
				
				commDao.insSlabYd("WrkBook", jrParam);

				//작업예약재료 등록
				String[][] wmParam = new String[lotMtlSh][8];
				
				for (int jj = 0; jj < lotMtlSh; jj++) {
					jrRow = jsLotMtl.getRecord(jj);
					
					wmParam[jj][0] = ydWbookId;												//야드작업예약ID
					wmParam[jj][1] = slabUtils.trim(jrRow.getFieldString("STL_NO"       ));	//재료번호
					wmParam[jj][2] = modifier;												//등록자
					wmParam[jj][3] = modifier;												//수정자
					wmParam[jj][4] = slabUtils.trim(jrRow.getFieldString("YD_STK_COL_GP"));	//야드적치열구분
					wmParam[jj][5] = slabUtils.trim(jrRow.getFieldString("YD_STK_BED_NO"));	//야드적치Bed번호
					wmParam[jj][6] = slabUtils.trim(jrRow.getFieldString("YD_STK_LYR_NO"));	//야드적치단번호
					wmParam[jj][7] = String.valueOf(jj + 1);								//야드권상모음순서
					
					
					//B열연이송여부  Y 인 경우 YDCSJ002 
					if("M".equals(ydGp) && !"".equals(chkGp)) {
						recPara  = JDTORecordFactory.getInstance().create();
						recPara.setField("MSG_ID",        "YDCSJ002");
						recPara.setField("YD_GP",		  chkGp);
						recPara.setField("STL_NO", slabUtils.trim(jrRow.getFieldString("STL_NO")));

						ydDelegate.sendMsg(recPara);
					}
				}
				
				commDao.upsBatch("WrkBookMtl", wmParam, logId, methodNm);
			}

			/**********************************************************
			* 4. 크레인스케줄(YDYDJ400) 전송용 기초 전문 생성
			**********************************************************/
			//크레인스케줄 전문 - Log ID, Method, 수정자 Set
			JDTORecord jrYdMsg = slabUtils.getParam(logId, methodNm, modifier);

			jrYdMsg.setField("YD_WBOOK_ID"     , ydWbookIdFst ); //야드작업예약ID(첫번째꺼만)
			jrYdMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
			jrYdMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
			jrYdMsg.setField("YD_SCH_ST_GP"    , "M"          ); //야드스케쥴기동구분(Manual)
			jrYdMsg.setField("YD_SCH_REQ_GP"   , "M"          ); //야드스케쥴요청구분(이적)
			jrYdMsg.setField("YD_WRK_PLAN_TCAR", ydWrkPlanTcar); //야드작업계획대차(대차상차 크레인스케줄을 전송하지 않기 위해 추가)
			jrYdMsg.setField("V_CHK_INSTANT", chkInstant); //야드작업계획대차(대차상차 크레인스케줄을 전송하지 않기 위해 추가)

			slabUtils.printLog(logId, methodNm, "S-");

			return jrYdMsg;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}


	/**
	 *      [A] 오퍼레이션명 : 크레인사양분리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord jrCrnSpec
	 *      @param JDTORecordSet jsWrkMtl
	 *      @return Vector
	 *      @throws DAOException
	*/
	public Vector setCrnSpecSpr(JDTORecord jrCrnSpec, JDTORecordSet jsWrkMtl) throws DAOException {
		String methodNm = "크레인사양분리[SlabYdJspSeEJB.setSlabYdHdLotSprCs] < " + jrCrnSpec.getResultMsg();
		String logId = jrCrnSpec.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			Vector vcLot = new Vector();	//크레인사양분리결과
			JDTORecord    jrRow = null;		//현재 Row
			JDTORecordSet jsLot = JDTORecordFactory.getInstance().createRecordSet("");	//Lot
			String crnSpecOvGp  = "";		//크레인사양초과구분

			//크레인사양분리
			int   mtlSh    = 0;		//재료매수
			int   mtlWt    = 0;		//재료중량
			float mtlT     = 0;		//재료두께
			float mtlW     = 0;		//재료폭
			int   mtlWtSum = 0;		//재료중량합
			float mtlTSum  = 0;		//재료두께합
			float mtlWMax  = 0;		//재료폭최대

			String Is_plate_StoA_Scarfing = "N"; 
			int rowCnt = jsWrkMtl.size();

			for (int ii = 0; ii < rowCnt; ii++) {
				jrRow = jsWrkMtl.getRecord(ii);
				
				mtlWt = Integer.parseInt(slabUtils.nvl(jrRow.getFieldString("YD_MTL_WT"),"0"));
				mtlT  = Float.parseFloat(slabUtils.nvl(jrRow.getFieldString("YD_MTL_T" ),"0"));
				mtlW  = Float.parseFloat(slabUtils.nvl(jrRow.getFieldString("YD_MTL_W" ),"0"));
				Is_plate_StoA_Scarfing = slabUtils.nvl(jrRow.getFieldString("PLATE_SIDE_SCARFING_YN" ),"N");
				if (ii > 0) {
					mtlSh++;
					mtlWtSum += mtlWt;
					mtlTSum  += mtlT;
					
					jrCrnSpec.setField("MTL_SH_SUM", String.valueOf(mtlSh   ));	//재료매수
					jrCrnSpec.setField("MTL_WT_SUM", String.valueOf(mtlWtSum));	//재료중량합
					jrCrnSpec.setField("MTL_T_SUM" , String.valueOf(mtlTSum ));	//재료두께합
					jrCrnSpec.setField("MTL_W"     , String.valueOf(mtlW    ));	//재료폭
					jrCrnSpec.setField("MTL_W_MAX" , String.valueOf(mtlWMax ));	//재료폭최대
					
					jrCrnSpec.setField("PLATE_SIDE_SCARFING_YN" , Is_plate_StoA_Scarfing);	//통합적치이력 있는 후판재 사이드스카핑 여부
					//크레인사양 초과 Check
					crnSpecOvGp = slabComm.chkCrnSpec(jrCrnSpec);

					if (!"".equals(crnSpecOvGp)) {
						//이전 Lot 추가
						vcLot.add(jsLot);

						jsLot = JDTORecordFactory.getInstance().createRecordSet("");
						mtlSh    = 1;
						mtlWtSum = mtlWt;
						mtlTSum  = mtlT;
						mtlWMax  = mtlW;
					}
				} else {
					mtlWtSum = mtlWt;
					mtlTSum  = mtlT;
					mtlWMax  = mtlW;
					mtlSh =1;
				}
				if (mtlW > mtlWMax) { mtlWMax = mtlW; }

				jsLot.addRecord(jrRow);
			}
			
			//마지막 Lot 추가
			vcLot.add(jsLot);
			
			slabUtils.printLog(logId, methodNm, "S-");

			return vcLot;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : 크레인스케줄전문정리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecordSet jsMsg
	 *      @param String logId
	 *      @param String mthdNm
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord setCrnSchMsg(JDTORecordSet jsMsg, String logId, String mthdNm) throws DAOException {
		String methodNm = "크레인스케줄전문정리[SlabYdJspSeEJB.setCrnSchMsg] < " + mthdNm;

		try {
			slabUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn = null;

			if (!slabUtils.isEmpty(jsMsg)) {
				String ydEqpId   = ""; //야드설비ID(크레인)
				String ydEqpStat = ""; //야드설비상태
				boolean fstYn = false; //동일크레인에서 첫번째 여부
				//DAO Parameter - Log ID, Method, 수정자 Set
				JDTORecord jrParam = slabUtils.getParam(logId, methodNm, "");
				JDTORecord jrRow = null;
				JDTORecordSet jsChk = null;
				
				String chkInstant ="";

				int rowCnt = jsMsg.size();

				for (int ii = rowCnt - 1; ii >= 0; ii--) {
					jrRow = jsMsg.getRecord(ii);
					
					if ("".equals(slabUtils.trim(jrRow.getFieldString("YD_WRK_PLAN_TCAR")))) {
						//야드작업계획대차가 있으면 대차상차 크레인스케줄이므로 전송하지 않음 -> 공대차출발지시로 처리
						fstYn = true;
						ydEqpId = slabUtils.trim(jrRow.getFieldString("YD_EQP_ID"));
						chkInstant = slabUtils.trim(jrRow.getFieldString("V_CHK_INSTANT"));
						
						for (int jj = 0; jj < ii; jj++) {
							if (ydEqpId.equals(jsMsg.getRecord(jj).getFieldString("YD_EQP_ID"))) {
								fstYn = false;
								break;
							}
						}
						
						//동일크레인에서 첫번째 이면
						if (fstYn) {
							//크레인 상태 확인
							jrParam.setField("V_YD_EQP_ID", ydEqpId); //야드설비ID

							jsChk = commDao.getStat("Eqp", jrParam);

							ydEqpStat = "";

							if (jsChk.size() > 0) {
								ydEqpStat = slabUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_STAT"));
							}

							if ("W".equals(ydEqpStat)) {
								slabUtils.printLog(logId, "setCrnSchMsg우선기동 체크여부[" + chkInstant + "]", "SL");
								
								//20240610 연주 김충만 계장 요청 저장위치별 정보조회 하면에 크레인 이적작업 등록시 우선기동 가능하도록 기능개선 --//REQ202405572888
								//트레일러 하차같은 경우 작업 취소 후 바로 다시기동 (한매씩 나눠서) 주는 경우가 있는데 0순위기동만으로는 다른 0순위에 걸려 못하는 경우 생김
								//예전 기준처럼 설비가 작업대기상태인경우 바로 기동하는 기능도 필요.
								if("Y".equals(chkInstant)){
									jrRtn = slabUtils.addSndData(jrRtn, slabComm.getCrnSchMsg(jrRow));
								}
								
								else {
									//크레인이 작업대기 상태이면 크레인스케줄 전송
									//20240314 크레인이 작업대기 상태이면, 작업지시 요구 호출해야
									//작업대기만 확인하면, 먼저 작업예약 건게 있어도 현재 등록한게 먼저 호출되며,
									//권하 후 다음 작업 만들고잇는데 불려지면 권하후 다음작업, 현재 등록한것 두개 크레인스케줄 기동된다.
									//jrRtn = slabUtils.addSndData(jrRtn, slabComm.getCrnSchMsg(jrRow));
									
									JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
									jrYdMsg.setResultCode(logId);	//Log ID
									jrYdMsg.setResultMsg(methodNm);	//Log Method Name
	
									jrYdMsg.setField("JMS_TC_CD"         , "YDYDJ440"               ); //JMSTC코드
									jrYdMsg.setField("JMS_TC_CREATE_DDTT", slabUtils.getDateTime14()); //JMSTC생성일시
									jrYdMsg.setField("YD_EQP_ID"         , ydEqpId                  ); //야드설비ID
									jrYdMsg.setField("YD_WRK_PROG_STAT"  , "W"                      ); //야드작업진행상태
									
									jrRtn = slabUtils.addSndData(jrRtn, jrYdMsg);
								}
							}
						}
					}
				}
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}


	/***************************************************************************
	 * 저장위치별정보조회
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 저장위치별정보조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getStrLocInfo(GridData gdReq) throws DAOException {
		String methodNm = "저장위치별정보조회[SlabYdJspSeEJB.getStrLocInfo] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			JDTORecordSet jrRst = jspDao.getStrLocInfo(gdReq);
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			return slabUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/***************************************************************************
	 * 작업재료List
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 작업재료List
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getWrkMtlList(GridData gdReq) throws DAOException {
		String methodNm = "작업재료List[SlabYdJspSeEJB.getWrkMtlList] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			JDTORecordSet jrRst = jspDao.getWrkMtlList(gdReq);
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			return slabUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/***************************************************************************
	 * 화면공통 처리
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 설비보급제한기준 조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getSupRuleMax(GridData gdReq) throws DAOException {
		String methodNm = "설비보급제한기준 조회[SlabYdJspSeEJB.getSupRuleMax] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		try {
			JDTORecordSet jrRst = jspDao.getComm("SupRuleMax", gdReq);
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			return slabUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 작업예약 우선순위변경
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updWrkBookPrior(GridData gdReq) throws DAOException {
		String methodNm = "작업예약 우선순위변경[SlabYdJspSeEJB.updWrkBookPrior] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			String modifier = slabUtils.trim(gdReq.getParam("userid")); //수정자

			//작업예약
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String[][] arrPrior = new String[rowCnt][3];
			
			for (int ii = 0; ii < rowCnt; ii++) {
				arrPrior[ii][0] = modifier;	//수정자
				arrPrior[ii][1] = slabUtils.trim(gdReq.getHeader("YD_SCH_PRIOR").getValue(ii));	//야드스케쥴우선순위
				arrPrior[ii][2] = slabUtils.trim(gdReq.getHeader("YD_WBOOK_ID" ).getValue(ii));	//야드작업예약ID
			}

			//작업예약 Table 우선순위 Update
			commDao.upsBatch("WbPrior", arrPrior, logId, methodNm);

			slabUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}

	
	/**
	 *      [A] 오퍼레이션명 : 작업예약 크레인지정
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updWrkBookCrn(GridData gdReq) throws DAOException {
		String methodNm = "작업예약 크레인지정[SlabYdJspSeEJB.updWrkBookCrn] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			String modifier = slabUtils.trim(gdReq.getParam("userid")); //수정자

			//작업예약
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String[][] arrCrn = new String[rowCnt][3];
			
			for (int ii = 0; ii < rowCnt; ii++) {
				arrCrn[ii][0] = modifier;	//수정자
				arrCrn[ii][1] = slabUtils.trim(gdReq.getHeader("YD_WRK_PLAN_CRN").getComboHiddenValues()[gdReq.getHeader("YD_WRK_PLAN_CRN").getSelectedIndex(ii)]);	//야드작업계획크레인
				arrCrn[ii][2] = slabUtils.trim(gdReq.getHeader("YD_WBOOK_ID").getValue(ii));	//야드작업예약ID
			}

			//작업예약 Table 야드작업계획크레인 Update
			commDao.upsBatch("WbCrn", arrCrn, logId, methodNm);

			slabUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}

	
	/**
	 *      [A] 오퍼레이션명 : 작업예약삭제
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord delWrkBook(GridData gdReq) throws DAOException {
		String methodNm = "작업예약삭제[SlabYdJspSeEJB.delWrkBook] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			String ydWbookId = ""; //야드작업예약ID
			String modifier  = slabUtils.trim(gdReq.getParam("userid")); //수정자
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, modifier);

			//작업예약
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String[][] arrCar     = new String[rowCnt][5];
			String[][] arrWrkBook = new String[rowCnt][2];
			
			/**********************************************************
			* 1. 크레인스케줄 존재여부 Check
			**********************************************************/
			for (int ii = 0; ii < rowCnt; ii++) {
				ydWbookId = slabUtils.trim(gdReq.getHeader("YD_WBOOK_ID").getValue(ii));
				jrParam.setField("V_YD_WBOOK_ID", ydWbookId);
				
				//작업예약 크레인스케줄정보 조회
				JDTORecordSet jsCrn = jspDao.getComm("WbCrnSch", jrParam);

			    if (jsCrn != null && jsCrn.size() > 0) {
					StringBuffer sbMsg = new StringBuffer();
					sbMsg = sbMsg.append("작업예약ID[" + ydWbookId + "]의 크레인스케줄 정보가 " + jsCrn.size() + " 건 존재합니다.");
					for (int mm = 0; mm < jsCrn.size(); mm++) {
						sbMsg = sbMsg.append("\n" + mm + " : " + jsCrn.getRecord(mm).getFieldString("YD_CRN_SCH_ID"));	//야드크레인스케쥴ID
					}
					throw new Exception(sbMsg.toString());
			    }

				//차량/대차스케줄 야드상차작업예약ID, 야드하차작업예약ID 삭제
			    arrCar[ii][0] = modifier;	//수정자
			    arrCar[ii][1] = ydWbookId;	//야드작업예약ID
			    arrCar[ii][2] = ydWbookId;	//야드작업예약ID
			    arrCar[ii][3] = ydWbookId;	//야드작업예약ID
			    arrCar[ii][4] = ydWbookId;	//야드작업예약ID
			
				//작업예약/재료 삭제
			    arrWrkBook[ii][0] = modifier;	//수정자
			    arrWrkBook[ii][1] = ydWbookId;	//야드작업예약ID
			}

			/**********************************************************
			* 2. 차량/대차스케줄 야드상차작업예약ID, 야드하차작업예약ID 삭제
			**********************************************************/
			//차량스케줄 야드상차작업예약ID, 야드하차작업예약ID 삭제
		    jspDao.updComm("CarSchWbDel", arrCar, logId, methodNm);
		
			//대차스케줄 야드상차작업예약ID, 야드하차작업예약ID 삭제
			jspDao.updComm("TcarSchWbDel", arrCar, logId, methodNm);
		
			/**********************************************************
			* 3. 작업예약/재료 삭제
			**********************************************************/
			//작업예약재료 삭제
			commDao.upsBatch("WrkBookMtlDel", arrWrkBook, logId, methodNm);

			//작업예약 삭제
			commDao.upsBatch("WrkBookDel", arrWrkBook, logId, methodNm);

			slabUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}

	
	/***************************************************************************
	 * Monitoring 화면 - Flex
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : Slab야드Monitoring 조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param  JDTORecord jrParam
	 *      @return List
	 *      @throws DAOException
	*/
	public List getSlabYdMonitor(JDTORecord jrParam) throws DAOException {
		String methodNm = "Slab야드Monitoring조회[SlabYdJspSeEJB.getSlabYdMonitor] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			String trtGp = slabUtils.trim(jrParam.getFieldString("V_TRT_GP")); //처리구분
			JDTORecordSet jrRst = commDao.getFlex(trtGp, jrParam);
			return slabUtils.listJdtoRecordTohashMap(jrRst.toList());
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/***************************************************************************
	 * 인터페이스Test
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 인터페이스Test 조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getIfTest(GridData gdReq) throws DAOException {
		String methodNm = "인터페이스Test 조회[SlabYdJspSeEJB.getIfTest] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		try {
			JDTORecordSet jrRst = jspDao.getIfTest(gdReq);
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			return slabUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 인터페이스Test 전송Data 저장
	 * 
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return void
	 *      @throws DAOException
	*/
	public GridData updIfTestData(GridData gdReq) throws DAOException {
		String methodNm = "인터페이스Test 전송Data 저장[SlabYdJspSeEJB.updIfTestData] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			int trtCnt = gdReq.getHeader("CHECK").getRowCount(); //처리건수

			if (trtCnt <= 0) {
				throw new Exception("저장할 Data가 존재하지 않습니다.");
			}

			String ifID = slabUtils.trim(gdReq.getParam("IF_ID")); //IFID
			String[][] param = new String[trtCnt][3];

			//Row수 만큼 Set
			for (int ii = 0; ii < trtCnt; ii++) {
				param[ii][0] = gdReq.getHeader("ITM_VAL").getValue(ii);
				param[ii][1] = ifID;
				param[ii][2] = gdReq.getHeader("ITM_SEQ").getValue(ii);
			}

			//Test Data 저장
			jspDao.updIfTestData(param, logId, methodNm);

			slabUtils.printLog(logId, "인터페이스Layout(TB_YD_Z_IFLAYOUT) 수정 : " + trtCnt + " 건", "DB");
			slabUtils.printLog(logId, methodNm, "S-");

			//Layout 다시 조회
			return this.getIfTest(gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}

	
	/**
	 *      [A] 오퍼레이션명 : 인터페이스Test 전송
	 * 
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return void
	 *      @throws DAOException
	*/
	public GridData sndIfTest(GridData gdReq) throws DAOException {
		String methodNm = "인터페이스Test 전송[SlabYdJspSeEJB.sndIfTest] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			String msgId = slabUtils.trim(gdReq.getParam("IF_ID")); //IFID

			if ("".equals(msgId) || msgId.length() != 8) {
				throw new Exception("잘못된 인터페이스ID 입니다.");
			}

			//전송데이터를 저장하고 조회
			GridData gdRst = this.updIfTestData(gdReq);

			int itmCnt = gdRst.getHeader("ITM_ID").getRowCount(); //항목건수

			if (itmCnt <= 0) {
				throw new Exception("인터페이스Test Data가 없어 전송할 수 없습니다.");
			}
			
			String ifMthGp    = msgId.substring(4, 5); //IF방법구분(L:EAI, 기타:JMS)
			String ifSndRcvGp = "YD".equals(msgId.substring(0, 2)) ? "S" : "R"; //IF송수신구분(송신, 수신)

			//큐에 넣을 데이터를 생성 - Log ID, Method, 수정자 Set
			//JDTORecord sndData = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));
			JDTORecord sndData = slabUtils.getParam(logId, methodNm, "");
			
			sndData.setField("JMS_TC_CD"         , msgId                    );
			sndData.setField("JMS_TC_CREATE_DDTT", slabUtils.getDateTime14());

			if ("L".equals(ifMthGp) && "S".equals(ifSndRcvGp)) {
				//EAI송신처리 일 경우
				ifMthGp = "sndToEAI";

				//EAI전문 Message
				/*StringBuffer sbMsg = new StringBuffer();

				for (int ii = 0; ii < itmCnt; ii++) {
					sbMsg = sbMsg.append(gdRst.getHeader("ITM_VAL").getValue(ii));
				}*/
				
				/*****추가*****/
				String sbMsg = gdReq.getParam("TC_LIST");

				sndData.setField("JMS_TC_MESSAGE", sbMsg.toString());
			} else {
				//수신 처리방법(Q:JMS Queue, E:EJB Call)이 'E'이고 수신처가 야드이면
				if ("E".equals(gdReq.getParam("TRT_MTH")) && "YD".equals(msgId.substring(2, 4))) {
					ifMthGp = "rcvInterface";	//EJB Call
				} else {
					ifMthGp = "sndToJMS";		//JMS송신
				}

				//EAI송신 외 처리 일 경우
				for (int ii = 0; ii < itmCnt; ii++) {
					sndData.setField(slabUtils.trim(gdRst.getHeader("ITM_ID" ).getValue(ii)), slabUtils.trim(gdRst.getHeader("ITM_VAL").getValue(ii)));
				}
			}
		
			//송신 공통 EJB를 이용하여 전송
			EJBConnector ejbConn = new EJBConnector("default", "YdCommEJB", this);
			ejbConn.trx(ifMthGp, new Class[] { JDTORecord.class }, new Object[] { sndData });
			
			slabUtils.printLog(logId, methodNm, "S-");
			
			return gdRst;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}

	
	/**
	 *      [A] 오퍼레이션명 : 인터페이스Test EAI전송
	 * 
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return void
	 *      @throws DAOException
	*/
	public GridData sndIfTestEAI(GridData gdReq) throws DAOException {
		String methodNm = "인터페이스Test EAI전송[SlabYdJspSeEJB.sndIfTestEAI] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			String tcList = gdReq.getParam("TC_LIST"); //전송List
			String tcMsg  = ""; //전송Data
			int sndCnt = 0; //전송건수

			while (tcList.length() > 0) {
				int idx = tcList.indexOf("\r\n");
				
				if (idx > 0) {
					tcMsg  = tcList.substring(0, idx);
					tcList = tcList.substring(idx + 2);
				} else {
					tcMsg = tcList;
					tcList = "";
				}

				//한건 전송
				if (!"".equals(tcMsg) && tcMsg.length() > 60) {
					//큐에 넣을 데이터를 생성 - Log ID, Method, 수정자 Set
					JDTORecord sndData = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));

					//EAI송신처리 일 경우
					sndData.setField("JMS_TC_CD"         , tcMsg.substring(0, 8));
					sndData.setField("JMS_TC_CREATE_DDTT", slabUtils.getDateTime14());
					sndData.setField("JMS_TC_MESSAGE"    , tcMsg);

					//송신 공통 EJB를 이용하여 L2로 전송
					EJBConnector ejbConn = new EJBConnector("default", "YdCommEJB", this);
					ejbConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { sndData });

					sndCnt++;
				}
			}

			gdReq.addParam("SND_CNT", String.valueOf(sndCnt));

			slabUtils.printLog(logId, methodNm, "S-");
			
			return gdReq;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}

	
	/***************************************************************************
	 * 인터페이스관리
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 인터페이스관리 조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getIfMgt(GridData gdReq) throws DAOException {
		String methodNm = "인터페이스관리 조회[SlabYdJspSeEJB.getIfMgt] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		try {
			JDTORecordSet jrRst = jspDao.getIfMgt(gdReq);
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			return slabUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : 인터페이스관리 등록
	 * 
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return void
	 *      @throws DAOException
	*/
	public GridData upsIfMgt(GridData gdReq) throws DAOException {
		String methodNm = "인터페이스관리 등록[SlabYdJspSeEJB.upsIfMgt] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			int trtCnt = gdReq.getHeader("CHECK").getRowCount(); //처리건수

			if (trtCnt <= 0) {
				throw new Exception("등록할 인터페이스가 존재하지 않습니다.");
			}

			String ifID = ""; //IFID
			String crud = ""; //등록/수정구분

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));

			//Row수 만큼 Set
			for (int ii = 0; ii < trtCnt; ii++) {
				ifID = slabUtils.trim(gdReq.getHeader("IF_ID").getValue(ii));
				crud = slabUtils.trim(gdReq.getHeader("CRUD" ).getValue(ii));
				
				if ("".equals(ifID)) {
					throw new Exception("등록할 인터페이스ID가 존재하지 않습니다.");
				}
				
				//신규 등록이고 I/F ID가 이미 존재하면
				if ("I".equals(crud) && jspDao.getIfMgtId(ifID, logId, methodNm)) {
					throw new Exception("인터페이스ID[ " + ifID + " ]는 이미 등록되어 있습니다.");
				}
				
				//인터페이스 등록용
				jrParam.setField("V_IF_ID"      , ifID                                                      ); //I/F ID
				jrParam.setField("V_IF_NM"      , slabUtils.trim(gdReq.getHeader("IF_NM"     ).getValue(ii))); //I/F 명
				jrParam.setField("V_PGM_NM1"    , slabUtils.trim(gdReq.getHeader("RCV_CLASS" ).getValue(ii))); //수신Class
				jrParam.setField("V_PGM_NM2"    , slabUtils.trim(gdReq.getHeader("RCV_METHOD").getValue(ii))); //수신Method
				jrParam.setField("V_PGM_NM3"    , slabUtils.trim(gdReq.getHeader("SND_QUEUE" ).getValue(ii))); //송신Queue
				jrParam.setField("V_OPRN_SYS_GP", slabUtils.trim(gdReq.getHeader("YD_GP"     ).getValue(ii))); //야드구분
				jrParam.setField("V_REMARKS"    , slabUtils.trim(gdReq.getHeader("REMARKS"   ).getValue(ii))); //비고
				//인터페이스Layout 등록용
				jrParam.setField("V_ITM_VAL1"   , slabUtils.trim(gdReq.getHeader("OLD_CLASS" ).getValue(ii))); //기존Class
				jrParam.setField("V_ITM_VAL2"   , slabUtils.trim(gdReq.getHeader("OLD_METHOD").getValue(ii))); //기존Method
				jrParam.setField("V_ITM_VAL3"   , slabUtils.trim(gdReq.getHeader("NEW_CLASS" ).getValue(ii))); //신규Class
				jrParam.setField("V_ITM_VAL4"   , slabUtils.trim(gdReq.getHeader("NEW_METHOD").getValue(ii))); //신규Method

				//인터페이스 등록
				jspDao.upsIfMgt("MstUps", jrParam);
				//인터페이스Layout 등록
				jspDao.upsIfMgt("DtlUps", jrParam);
			}

			slabUtils.printLog(logId, "인터페이스 및 Layout 등록 : " + trtCnt + " 건", "DB");
			slabUtils.printLog(logId, methodNm, "S-");

			return this.getIfMgt(gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}

	
	/**
	 *      [A] 오퍼레이션명 : 인터페이스관리 삭제
	 * 
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return void
	 *      @throws DAOException
	*/
	public GridData delIfMgt(GridData gdReq) throws DAOException {
		String methodNm = "인터페이스관리 삭제[SlabYdJspSeEJB.delIfMgt] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			int trtCnt = gdReq.getHeader("CHECK").getRowCount(); //처리건수

			if (trtCnt <= 0) {
				throw new Exception("삭제할 인터페이스가 존재하지 않습니다.");
			}

			String ifID = ""; //IFID

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));

			//Row수 만큼 Set
			for (int ii = 0; ii < trtCnt; ii++) {
				ifID = slabUtils.trim(gdReq.getHeader("IF_ID").getValue(ii));
				
				if ("".equals(ifID)) {
					throw new Exception("삭제할 인터페이스ID가 존재하지 않습니다.");
				}
				
				//인터페이스 및 Layout 삭제용
				jrParam.setField("V_IF_ID", ifID); //I/F ID

				//인터페이스 삭제
				jspDao.upsIfMgt("MstDel", jrParam);
				//인터페이스Layout 삭제
				jspDao.upsIfMgt("DtlDel", jrParam);
			}

			slabUtils.printLog(logId, "인터페이스 및 Layout 삭제 : " + trtCnt + " 건", "DB");
			slabUtils.printLog(logId, methodNm, "S-");

			return this.getIfMgt(gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}

	
	/***************************************************************************
	 * Code조회
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : Slab야드 코드 조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getSlabYdCode(GridData gdReq) throws DAOException {
		String methodNm = "코드조회[SlabYdJspSeEJB.getSlabYdCode] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		try {
			JDTORecordSet jrRst = jspDao.getSlabYdCode(gdReq);
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			return slabUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * [A] 오퍼레이션명: 후판압연지시 메시지등록
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inParam
	 * @return
	 * @throws DAOException
	 */
  public JDTORecord updateStlMessagePa(GridData gdReq) throws DAOException {
			String methodNm = "후판압연지시 메시지등록[SlabYdJspSeEJB.updateStlMessagePa] < " + gdReq.getNavigateValue();
			String logId = gdReq.getIPAddress(); 
			try { 
				slabUtils.printLog(logId, methodNm, "S+", gdReq);

				String lv_Sndbk_gp_etc  = "";  
				String lv_stl_no = "";  
				
				//DAO Parameter - Log ID, Method, 수정자 Set
				JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));
				
				//처리완료한 야드작업예약ID
				int rowCnt = gdReq.getHeader("CHECK").getRowCount();
				String[] arrYdWbookId = new String[rowCnt];
				
				for (int ii = 0; ii < rowCnt; ii++) {
					/**********************************************************
					* 1.Check
					**********************************************************/
					lv_Sndbk_gp_etc  = slabUtils.trim(gdReq.getHeader("SNDBK_GP_ETC" ).getValue(ii)); //야드작업예약ID
					lv_stl_no = slabUtils.trim(gdReq.getHeader("STL_NO").getValue(ii)); //야드스케쥴우선순위

				

					slabUtils.printLog(logId, "메시지 [ " + lv_stl_no + " >> " + lv_Sndbk_gp_etc + " ]", "SL");

					/**********************************************************
					* 2. 메시지 Table에  우선순위를 Update
					**********************************************************/
					jrParam.setField("SNDBK_GP_ETC" , lv_Sndbk_gp_etc );
					jrParam.setField("STL_NO", lv_stl_no);
					
					//메시지  Update
					commDao2.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updMessage");				
	
				}

				slabUtils.printLog(logId, methodNm, "S-");

				return jrParam;
			} catch(DAOException e) {
				throw e;
			} catch(Exception e) {
				throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
			}	
		}
	
  
  
  	/**
	 *  벤딩재 이력 조회
	 * 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return
	 * @throws JDTOException
	 * @throws JDTOException
	 */
	public JDTORecordSet getBendingSlabHist(JDTORecord inDto) throws JDTOException {
		
		String methodNm = "벤딩재이력조회[SlabYdJspFaEJB.getBendingSlabHist]";
		String logId = slabUtils.getLogId();
		
		Object[] sObj = null;			//조회 파라미터 보관 오브젝트 배열		
		//리턴 할 값들을 가지고 있는 JDTORecordSet
		String jspeed_query_id = "";
		
		try {
			
			slabUtils.printLog(logId, methodNm, "S+");
			
			//기본정보조회
			JDTORecordSet jsCrn = jspDao.getBendingSlabHist(inDto);
			return jsCrn;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {

		}

	}
	
	
	/**
	 *      [A] 오퍼레이션명 : Sizing Slab 이송 재료 List [2후판정정야드]
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getWrkMtlListFromPlate(GridData gdReq) throws DAOException {
		String methodNm = "Sizing Slab 이송 재료 List[SlabYdJspSeEJB.getWrkMtlListFromPlate] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			JDTORecordSet jrRst = jspDao.getWrkMtlListFromPlate(gdReq);
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			return slabUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 스카핑 슬라브 이송실적
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getScarfSlabMvList(GridData gdReq) throws DAOException {
		String methodNm = "스카핑 슬라브 이송실적[SlabYdJspSeEJB.getScarfSlabMvList] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			//
			JDTORecordSet jrRst = jspDao.getScarfSlabMvList(gdReq);
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			return slabUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 현재 적치수량,중량 조회(C1,C2)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getCurCntWt(GridData gdReq) throws DAOException {
		String methodNm = "현재 적치수량,중량 조회(C1,C2)[SlabYdJspSeEJB.getCurCntWt] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			JDTORecordSet jrRst = jspDao.getCurCntWt(gdReq);
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			return slabUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *  야드관리 > 항만슬라브야드 > monitoring > 차량동간이적
	 *  허정욱
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public JDTORecord updSyCarMvStkReg(GridData gdReq) throws DAOException {
		String mthdNm = "차량동간이적등록[SlabYdJspSeEJB.updSyCarMvStkReg] < " + gdReq.getNavigateValue();
		String logId  = gdReq.getIPAddress();		
		JDTORecord jrRtn   = JDTORecordFactory.getInstance().create();
			
		try{
			slabUtils.printLog(logId, mthdNm, "S+"); 
			
			String modifier		= slabUtils.trim(gdReq.getParam("YD_USER_ID")); 
			String ydSchCd  	= slabUtils.trim(gdReq.getParam("YD_SCH_CD"));
			String toBayGp		= slabUtils.trim(gdReq.getParam("TO_YD_BAY_GP")); 
			String carNo   		= slabUtils.trim(gdReq.getParam("CAR_NO"));
			String ydGp			= slabUtils.trim(gdReq.getParam("YD_GP"));
			String stlNos   	= slabUtils.trim(gdReq.getParam("STL_NOS")); //재료번호들
			String fromBayGp	= ydSchCd.substring(1, 2);	

			String msg             	= "";
			String rtnCd			= "";
			String rtnMsg			= "";

			String ydWrkCrn   		= "";
			String ydWrkAltCrn   	= "";
			String ydWrkAltCrnYn   	= "";
			String ydStkColGp		= "";
			
			/********************************
			 * 차량동간이적(MT) 전용 체크
			 ********************************/
			///////////////////
			// FROM 동 체크
			///////////////////
			JDTORecord jrParam = slabUtils.getParam(logId, mthdNm, modifier);
			jrParam.setField("YD_GP"	, ydGp);
			jrParam.setField("YD_BAY_GP", fromBayGp);
			/*SELECT *
			  FROM TB_YD_CARPOINT
			 WHERE YD_CARPNT_CD LIKE :V_YD_GP || NVL(:V_TO_DIR, '_') || NVL(:V_YD_BAY_GP, '_') || '%'
			   AND YD_CAR_USETYPE_GP = 'MT'
			   AND DEL_YN = 'N'
			 ORDER BY YD_STK_COL_GP
			*/
			JDTORecordSet jsCarPoint = commDao.select(jrParam, "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getCarFrtoMovePoint", logId, mthdNm, "차량동간이적포인트 조회");
			if( jsCarPoint.size() < 1 ) {
				msg = fromBayGp + "동의 차량동간이적(MT) 위치가 없습니다.";
				slabUtils.printLog(logId, msg, "SL"); 
				jrRtn.setField("RTN_MSG", msg);
	    		jrRtn.setField("RTN_CD" , "0");
				return jrRtn;
			}
			ydStkColGp	= jsCarPoint.getRecord(0).getFieldString("YD_STK_COL_GP");
			
			///////////////////
			// TO 동 체크
			///////////////////
			jrParam.setField("YD_GP"	, ydGp);
			jrParam.setField("YD_BAY_GP", toBayGp);
			
			jsCarPoint = commDao.select(jrParam, "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getCarFrtoMovePoint", logId, mthdNm, "차량동간이적포인트 조회");
			if( jsCarPoint.size() < 1 ) {	
				msg = fromBayGp + "동의 차량동간이적(MT) 위치가 없습니다.";
				slabUtils.printLog(logId, msg, "SL"); 
				jrRtn.setField("RTN_MSG", msg);
	    		jrRtn.setField("RTN_CD" , "0");
				return jrRtn;
			}
			//스케줄코드로 스케줄기준 Table 조회해서 작업 크레인ID SELECT
			jrParam = slabUtils.getParam(logId, mthdNm, modifier);	
			//jrParam.setField("CARD_NO"		, carNo);		//차량번호
			jrParam.setField("YD_SCH_CD"	, ydSchCd);		//스케줄코드
			//jrParam.setField("YD_AIM_BAY_GP", toBayGp);	//목적동
			
			//스케줄CD 체크
			JDTORecordSet jsSchRule = commDao.select(jrParam, "com.inisteel.cim.yd.dao.ydschruledao.YdSchruleDao.getYdSchrule", logId, mthdNm, "스케줄기준 조회");
			//리턴값 메세지처리
			if(jsSchRule.size() < 1) {
				msg = "스케줄코드(" + ydSchCd + ")에 대한 스케줄기준 데이터가 이상합니다.";
				slabUtils.printLog(logId, msg, "SL");
				jrRtn.setField("RTN_CD" 			, "0");	
				jrRtn.setField("RTN_MSG"			, msg);	
				
				return jrRtn;
			}	
			
			ydWrkCrn      = slabUtils.trim(jsSchRule.getRecord(0).getFieldString("YD_WRK_CRN"));   	//작업크레인
			ydWrkAltCrn   = slabUtils.trim(jsSchRule.getRecord(0).getFieldString("YD_ALT_CRN"));   //작업대체크레인
			ydWrkAltCrnYn = slabUtils.trim(jsSchRule.getRecord(0).getFieldString("YD_ALT_CRN_YN"));   //작업대체크레인 유무
			//작업크레인 사용 가능일시 작업크레인 설정
			//작업크레인 고장이고, 대체크레인 있고 대체크레인 고장 아닐시 대체크레인 설정
			//둘다 고장일시 default 크레인으로 작업크레인 설정.
			jrParam.setField("YD_EQP_ID" , ydWrkCrn);
			JDTORecordSet jsCrn = commDao.select(jrParam, "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqp", logId, mthdNm, "크레인 상태 CHECK");
				
			if(jsCrn.size() < 1) {
				msg = "작업크레인(" + ydWrkCrn + ")에 대한 크레인상태 데이터가 이상합니다.";
				slabUtils.printLog(logId, msg, "SL");
				jrRtn.setField("RTN_CD" 			, "0");	
				jrRtn.setField("RTN_MSG"			, msg);	
				
				return jrRtn;
			}
			jsCrn.first();
    		JDTORecord jrCrn  = jsCrn.getRecord();
			String ydEqpStat  = slabUtils.trim(jrCrn.getFieldString("YD_EQP_STAT"));
			
			if (YdConstant.YD_EQP_STAT_BREAK.equals(ydEqpStat)) {
				msg = "작업크레인(" + ydWrkCrn + ") 고장(" + ydEqpStat + ") 입니다.";
				slabUtils.printLog(logId, msg, "SL");
				
				//작업크레인과 다른 대체크레인이 존재하면 대체 크레인 검사
				if("Y".equals(ydWrkAltCrnYn) && !ydWrkCrn.equals(ydWrkAltCrn)){
					jrParam.setField("YD_EQP_ID" , ydWrkAltCrn);
					jsCrn = commDao.select(jrParam, "com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqp", logId, mthdNm, "크레인 상태 CHECK");
						
					if(jsCrn.size() < 1) {
						msg = "작업대체크레인(" + ydWrkAltCrn + ")에 대한 크레인상태 데이터가 이상합니다.";
						slabUtils.printLog(logId, msg, "SL");
						jrRtn.setField("RTN_CD" 			, "0");	
						jrRtn.setField("RTN_MSG"			, msg);	
						
						return jrRtn;
					}
					jsCrn.first();
		    		jrCrn  = jsCrn.getRecord();
					ydEqpStat  = slabUtils.trim(jrCrn.getFieldString("YD_EQP_STAT"));
					
					if (ydEqpStat.equals(YdConstant.YD_EQP_STAT_BREAK)) {
						msg = "작업대체크레인(" + ydWrkAltCrn + ") 고장(" + ydEqpStat + ") 입니다. --> 작업크레인(" + ydWrkCrn + ")을 default 크레인으로 설정";
						slabUtils.printLog(logId, msg, "SL");
					}
					else{
						msg = "작업대체크레인(" + ydWrkAltCrn + ") 사용가능(" + ydEqpStat + ") 이므로, 작업크레인 설정";
						slabUtils.printLog(logId, msg, "SL");
						
						ydWrkCrn = ydWrkAltCrn;
					}
					
				}
				
			} 
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			//재료매수만큼 루프를 돌아서 크레인사양과 재료 사양을 체크한다.
			//작업예약재료 등록 여부를 체크한다.
			
			for (int i = 1; i <= rowCnt; i++) {
				//다른 작업예약에 재료가 등록되어있는지 체크한다.
				String stlNo   = slabUtils.trim(gdReq.getHeader("STL_NO" ).getValue(i));
				
				JDTORecordSet jsWrkBook = commDao.select(jrParam, "com.inisteel.cim.yd.dao.ydwrkbookmtldao.YdWrkbookmtlDao.getYdWrkbookmtlSTLNO", logId, mthdNm, "재료번호로 작업예약select");
				
				if(jsWrkBook.size() > 0) {
					msg = "재료번호(" + stlNo + ") 로 등록된 작업예약이 존재합니다.";
					slabUtils.printLog(logId, msg, "SL");
					jrRtn.setField("RTN_CD" 			, "0");	
					jrRtn.setField("RTN_MSG"			, msg);	
					
					return jrRtn;
				}
				
			}
					
			//선택한 재료 -->최대 몇톤까지 작업하게 해..? 스펙 확인
			
			//작업예약 등록
			
			jrParam.setField("V_YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
			jrParam.setField("V_YD_AIM_BAY_GP"   , toBayGp   	); //야드목표동구분
			jrParam.setField("V_YD_WRK_PLAN_CRN" , ydWrkCrn		); //야드작업크레인	
			
			jrParam.setField("V_YD_STK_COL_GP" 	 , ydStkColGp.substring(0,2) ); //적치열구분
			jrParam.setField("V_STL_NOS" 		 , stlNos		  ); //재료번호
			
			//작업예약 대상재료 조회
			JDTORecordSet jsWbMtl = JDTORecordFactory.getInstance().createRecordSet("");
			jsWbMtl = jspDao.getMvStkWrkBookReg("MvMtl", jrParam);

			if (jsWbMtl.size() <= 0) {
				throw new Exception("이적 재료 정보가 없습니다.");
			}
						
			this.insMvstkWrkBook(jrParam, jsWbMtl);//작업예약 등록
			
			
			
			jrParam = slabUtils.getParam(logId, mthdNm, modifier);
			jrParam.setField("PT_LOAD_LOC"	    , ydStkColGp);
			jrParam.setField("CAR_NO"			, carNo/*sCarCardNo*/ );
			jrParam.setField("CAR_UPDN_GP"		, "1");  //상차
			
				
			JDTORecord jrRtn1  = slabUtils.getParam(logId, mthdNm, modifier);//this.updSyCarMvArr(jrParam);//차량도착백업처리.
			rtnCd		= slabUtils.nvl(jrRtn1.getFieldString("RTN_CD"), "0");
			rtnMsg	    = slabUtils.nvl(jrRtn1.getFieldString("RTN_MSG"), "");

			if ("0".equals(rtnCd)) {
				slabUtils.printLog(logId, rtnMsg, "SL"); 
				jrRtn.setField("RTN_CD" 			, "0");	
				jrRtn.setField("RTN_MSG"			, rtnMsg);	
				return jrRtn;
			}
			
			jrRtn = slabUtils.addSndData(jrRtn, jrRtn1);				
			

			jrRtn.setField("RTN_CD" 	, "1");	
			jrRtn.setField("RTN_MSG"	, "정상적으로 스케쥴까지 등록했습니다.");	

			slabUtils.printLog(logId, mthdNm, "S-");	
			return jrRtn;				
		}catch(DAOException e) {
			throw e;
		}catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, mthdNm, e));
		}			
	}
	
	/**
	 * 야드관리 > 연주슬라브야드 > monitoring > 연주야드 현황판 메세지 전송
	 * @ejb.interface-method
	 */
	public void sendMsg(HashMap paramMap, ArrayList paramList) throws DAOException {
		String methodNm = "슬라브야드 메세지 전송[SlabYdJspSeEJB.sendMsg] < ";
		String logId = slabUtils.getLogId();
		Object[] returnObj    = null;	
		
		try{
			String sSENDER_NAME="연주 야드운전실";
			String sSENDER_ADDR="slabYardNoReply@hyundai-steel.com";
			String sTitle="[자동메일] 연주슬라브야드 이송알림 메세지";
			
			slabUtils.printLog(logId, methodNm, "S+"); 
			
			JDTORecord		jrParam	= JDTORecordFactory.getInstance().create();
			JDTORecordSet jrResultSet = commDao.select(jrParam, "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getCSlabYdInOutStatMsgRcver", logId, methodNm, "수신인 SELECT");
			
			if(jrResultSet ==null || jrResultSet.size()<=0){
				slabUtils.printLog(logId, "알람메일 대상 리스트 조회 실패 0건", "SL");
				
			}
			else{
				String rcvUsers = "";
				String rcvEmails = "";
				
				for(int i=0; i< jrResultSet.size(); i++){
					String userId = slabUtils.nvl(jrResultSet.getRecord(i).getFieldString("USER_ID"),"");
					String email = slabUtils.nvl(jrResultSet.getRecord(i).getFieldString("EMAIL"),"");
					
					if("".equals(rcvUsers)) rcvUsers += userId;
					else rcvUsers += ";" + userId;
					
					if("".equals(rcvEmails)) rcvEmails += email;
					else rcvEmails += ";" + email;
				}
				
				JDTORecord		recParam	= JDTORecordFactory.getInstance().create();
				recParam.setField("SENDER_ADDR", sSENDER_ADDR);
	    		recParam.setField("SENDER_NAME", sSENDER_NAME);
	    		recParam.setField("SUBJECT",sTitle); 
	    		recParam.setField("CONTENT",paramMap.get("MSG_CONTENTS").toString());  //sContentSummary  
	    		recParam.setField("RECEVER_ADDR",rcvEmails.split(";"));
	    		recParam.setField("RECEVER",rcvUsers.split(";"));
	    		
	    		
	    		
	    		try {
	    			ydUtils.sendMail(recParam);
				}catch (Exception e) {
					
					slabUtils.printLog(logId, "알람메일 대상 전송 실패", "SL");
					
				}
			}
				
			
			slabUtils.printLog(logId, methodNm, "S-"); 
		}catch(DAOException e) {
			throw e;
		}catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
		
		//return returnObj;
	}
	
	/**
	 *      야드관리 > 연주슬라브야드 > monitoring > 연주야드 현황판 계획량 수정
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public JDTORecord cSlabYdInOutStatPlanModify(GridData gdReq) throws DAOException {
		String methodNm = "연주야드 현황판 계획량 수정[SlabYdJspSeEJB.cSlabYdInOutStatPlanModify] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+"); 
			int rowCnt = gdReq.getHeader("WLOC").getRowCount();
			
			for(int i=0; i<rowCnt; i++){
				String stkGp = slabUtils.trim(gdReq.getHeader("STK_GP").getValue(i));
				String userId = slabUtils.trim(gdReq.getParam("USER_ID"));
				String fromMonth = slabUtils.trim(gdReq.getParam("FROM_MONTH"));
				
				for(int j=1; j<=31; j++){
					String day = "";
					if(j<10){
						day = "0"+Integer.toString(j);
					}
					else{
						day = Integer.toString(j);
					}
					String planWt = slabUtils.trim(gdReq.getHeader("DD_"+day).getValue(i));
					
					JDTORecord		jrParam	= JDTORecordFactory.getInstance().create();
					jrParam.setField("YD_INVGRP_DATE", fromMonth+day);
					jrParam.setField("WD_GP", "P");
					jrParam.setField("STK_GP",stkGp); 
					jrParam.setField("STL_WT",planWt);
					jrParam.setField("REGISTER",userId);
					
		    		
					slabUtils.printLog(logId, "날짜["+fromMonth+day+"] stkGp["+stkGp+"] stlWt["+planWt+"] 입력", "SL");
					
		    		commDao.update(jrParam, "com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updCSlabYdInOutStatTransPlan", logId, methodNm, "계획량 수정");
				}
			}
			
			JDTORecord jrRtn   = JDTORecordFactory.getInstance().create();
			jrRtn.setField("RTN_CD" 	, "1");	
			jrRtn.setField("RTN_MSG"	, "정상적으로 계획량 수정했습니다.");	
			slabUtils.printLog(logId, methodNm, "S-"); 
			
			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 권하불가위치 조회
	 *		오원재(1524711)
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getYdDnWoLocNotAllowedInfo(GridData gdReq) throws DAOException {
		String methodNm = "권하불가위치 조회[SlabYdJspSeEJB.getYdDnWoLocNotAllowedInfo] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			
			slabUtils.printLog(logId, methodNm, "S+", gdReq);
			JDTORecordSet jrRst = jspDao.getYdDnWoLocNotAllowedInfo(gdReq);
			slabUtils.printLog(logId, methodNm, "S-");
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			return slabUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 권하불가재료 조회
	 *		오원재(1524711)
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getSlabDnNotAllowedInfo(GridData gdReq) throws DAOException {
		String methodNm = "권하불가재료 조회[SlabYdJspSeEJB.getSlabDnNotAllowedInfo] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			
			slabUtils.printLog(logId, methodNm, "S+", gdReq);
			JDTORecordSet jrRst = jspDao.getSlabDnNotAllowedInfo(gdReq);
			slabUtils.printLog(logId, methodNm, "S-");
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			return slabUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 중복적치재료 조회
	 *		오원재(1524711)
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getMvStkWrkBookRegDup(GridData gdReq) throws DAOException {
		String methodNm = "중복적치재료 조회[SlabYdJspSeEJB.getMvStkWrkBookRegDup] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			
			slabUtils.printLog(logId, methodNm, "S+", gdReq);
			JDTORecordSet jrRst = jspDao.getMvStkWrkBookRegDup(gdReq);
			slabUtils.printLog(logId, methodNm, "S-");
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			return slabUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}	
	
	/**
	 *      [A] 오퍼레이션명 : 핸드스카핑보류재 조회
	 *		오원재(1524711)
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getHandScarfingHoldYn(GridData gdReq) throws DAOException {
		String methodNm = "핸드스카핑보류재 조회[SlabYdJspSeEJB.getHandScarfingHoldYn] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			
			slabUtils.printLog(logId, methodNm, "S+", gdReq);
			JDTORecordSet jrRst = jspDao.getHandScarfingHoldYn(gdReq);
			slabUtils.printLog(logId, methodNm, "S-");
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			return slabUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 팔렛트 자동 상차완료 처리 여부 변경
	 *		오원재(1524711)
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param inDto
	 *      @return 
	*/
	public void updPtAutoComplete(JDTORecord [] inDto) throws DAOException {
		String methodNm = "팔렛트 자동 상차완료 처리 여부 변경[SlabYdJspSeEJB.updPtAutoComplete] < ";
		String logId = slabUtils.getLogId();
		JDTORecord    recPara  			= null;
		JDTORecordSet	rsResult		= null;
		int       	intRtnVal    		= 0;
		
		slabUtils.printLog(logId, methodNm, "S+");
		
		try {
			
			String matlSupMtdGp = "";
			String ydStkColGp = "";
			String modifier = "";
			
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara  = JDTORecordFactory.getInstance().create();
			
			for(int i = 0; i < inDto.length; i++){					
				
				matlSupMtdGp = inDto[i].getField("MATL_SUP_MTD_GP").toString();
				modifier = inDto[i].getFieldString("YD_USER_ID").toString();
				ydStkColGp = inDto[i].getField("YD_STK_COL_GP").toString();
				
				recPara.setField("V_MATL_SUP_MTD_GP",    	matlSupMtdGp);
				slabUtils.printLog(logId, methodNm, "MATL_SUP_MTD_GP : " + matlSupMtdGp);
				recPara.setField("V_MODIFIER",   			modifier);
				slabUtils.printLog(logId, methodNm, "MODIFIER : " + modifier);
				recPara.setField("V_YD_STK_COL_GP",		ydStkColGp);
				slabUtils.printLog(logId, methodNm, "YD_STK_COL_GP : " + ydStkColGp);
				
		        intRtnVal = jspDao.updPtAutoComplete(recPara);
		        
				if (intRtnVal < 0) {
					if (intRtnVal == -1) {
						slabUtils.printLog(logId, methodNm, "ERROR");
					} else {
						slabUtils.printLog(logId, methodNm, "PARAMETER ERROR");
					}
				}
		        
			}

		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
			slabUtils.printLog(logId, methodNm, "S-");
		}
	}
}
