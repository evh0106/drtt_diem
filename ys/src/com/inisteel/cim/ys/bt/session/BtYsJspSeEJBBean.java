/**
 * @(#)BtYsJspSeEJBBean
 *
 * @version          V1.00
 * @author           허철호
 * @date             2014/12/22
 *
 * @description      BILLET 야드 화면 관리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2014/12/22   윤재광      조병기      최초 등록
 */
package com.inisteel.cim.ys.bt.session;

import java.util.Vector;

import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.CmUtil;
import com.inisteel.cim.hr.common.util.CmnUtil;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.ys.bt.session.BtYsComm;
import com.inisteel.cim.ys.common.dao.YsCommDAO;
import com.inisteel.cim.ys.bt.dao.BtYsDAO;
import com.inisteel.cim.ys.common.util.YsCommUtils;
import com.inisteel.cim.ys.common.util.YsConstant;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

/**
 *      [A] 클래스명 : BILLET 야드 화면관리 Session EJB 
 *
 * @ejb.bean name="BtYsJspSeEJB" jndi-name="BtYsJspSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300" 
 * @ejb.transaction type="Required"
*/
public class BtYsJspSeEJBBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;
	private YsCommUtils commUtils = new YsCommUtils();
	private YsCommDAO commDao = new YsCommDAO();
	private BtYsComm btYsComm = new BtYsComm();
	private BtYsDAO BtYsDao = new BtYsDAO();
	
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
		String methodNm = "조회[BtYsJspSeEJB.getSelectData] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//Grid date 를 JDTORecord data 로 변환
			JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
			
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			
			commDao.jspSelect(inRecord, outRecSet, inRecord.getFieldString("QUERY_ID"), logId, methodNm);	
			
			//UI로 반환 할 Grid data 를 생성 
			//GridData gdRet = CmUtil.genGridData(gdReq, outRecSet); -- old version
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			GridData gdRet = commUtils.jdtoRecordToGridData(gdRtn, outRecSet.toList(), gdReq);

			commUtils.printLog(logId, methodNm, "S-", gdReq);
			
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
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
		String methodNm = "조회[BtYsJspSeEJB.getSelectData] < " + recPara.getResultMsg();
		String logId = recPara.getResultCode();
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+", recPara);
			
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			
			commDao.jspSelect(recPara, outRecSet, recPara.getFieldString("QUERY_ID"), logId, methodNm);	
			
			commUtils.printLog(logId, methodNm, "S-", recPara);
			
			return outRecSet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of getSelectData		
	
	/**
	 * 대차스케줄관리 - 대차초기화
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord initTcarSchMgt(GridData gdReq) throws DAOException {
		String methodNm = "대차스케줄관리 대차초기화[BtYsJspSeEJB.initTcarSchMgt] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			String ydEqpId     = ""; //야드설비ID(대차)
			String ydCurrBayGp = ""; //야드현재동구분(신규)
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_EQP_WRK_STAT"    , "U"); //야드설비작업상태(공차)
			jrParam.setField("YD_CARLD_SCH_REQ_GP", "6"); //야드상차스케쥴요청구분(공대차도착)
			jrParam.setField("YD_CARUD_SCH_REQ_GP", "3"); //야드하차스케쥴요청구분(영대차도착)
			jrParam.setField("YD_CAR_PROG_STAT"   , "0"); //야드차량진행상태(상차대기)

			//대차정보
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				ydEqpId     = commUtils.trim(gdReq.getHeader("YD_EQP_ID").getValue(ii));
				ydCurrBayGp = commUtils.trim(gdReq.getHeader("YD_CURR_BAY_GP").getComboHiddenValues()[gdReq.getHeader("YD_CURR_BAY_GP").getSelectedIndex(ii)]);

				if ("".equals(ydEqpId)) {
					throw new Exception("설비ID가 없습니다.");
				} else if ("".equals(ydCurrBayGp)) {
					throw new Exception("변경할 현재동이 없습니다.");
				}
				
				/**********************************************************
				* 2. 기존 대차스케줄/재료 삭제
				**********************************************************/
				jrParam.setField("YD_EQP_ID", ydEqpId);

				//대차이송재료 초기화
				commDao.update(jrParam, "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updTcarSchInitMtl", logId, methodNm, "대차이송재료 초기화");

				//대차스케줄 초기화
				commDao.update(jrParam, "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updTcarSchInitSch", logId, methodNm, "대차스케줄 초기화");
				
				/**********************************************************
				* 3. 신규 대차스케줄 등록
				**********************************************************/
				//야드대차스케쥴ID 생성
				String ydTcarSchId = commDao.getSeqId(logId, methodNm, "TcarSch");

				if ("".equals(ydTcarSchId)) {
					throw new Exception( "대차스케줄ID 생성 중 오류가 발생하였습니다.");
				}
				
				//대차스케줄 등록
				jrParam.setField("YD_TCAR_SCH_ID"   , ydTcarSchId); //야드대차스케쥴ID
				jrParam.setField("YD_CAR_PROG_STAT" , "0"        ); //야드차량진행상태(상차대기)
				jrParam.setField("YD_CARLD_STOP_LOC", ydEqpId.substring(0, 1) + ydCurrBayGp + ydEqpId.substring(2)); //야드상차정지위치

				commDao.update(jrParam, "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updTcarSchInsSch", logId, methodNm, "대차스케줄 등록");
				
				/**********************************************************
				* 4. 대차 현재동 변경
				**********************************************************/
				jrParam.setField("YD_EQP_ID"     , ydEqpId    );
				jrParam.setField("YD_CURR_BAY_GP", ydCurrBayGp);

				jrRtn = commUtils.addSndData(jrRtn, this.updTcarCurrBay(jrParam));
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of initTcarSchMgt
	
	/**
	 * 대차스케줄관리 - 작업예약 우선순위변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updWrkBookPrior(GridData gdReq) throws DAOException {
		String methodNm = "작업예약 우선순위변경[BtYsJspSeEJB.updWrkBookPrior] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			String modifier = commUtils.trim(gdReq.getParam("userid")); //수정자

			//작업예약
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String[][] arrPrior = new String[rowCnt][3];
			
			for (int ii = 0; ii < rowCnt; ii++) {
				arrPrior[ii][0] = modifier;	//수정자
				arrPrior[ii][1] = commUtils.trim(gdReq.getHeader("YD_SCH_PRIOR").getValue(ii));	//야드스케쥴우선순위
				arrPrior[ii][2] = commUtils.trim(gdReq.getHeader("YD_WBOOK_ID" ).getValue(ii));	//야드작업예약ID
			}

			//작업예약 Table 우선순위 Update
			commDao.upsBatch("WbPrior", arrPrior, logId, methodNm);

			commUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updWrkBookPrior	
	
	/**
	 * 대차스케줄관리 - 작업예약삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord delWrkBookback(GridData gdReq) throws DAOException {
		String methodNm = "작업예약삭제[BtYsJspSeEJB.delWrkBook] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			String ydWbookId = ""; //야드작업예약ID
			String modifier  = commUtils.trim(gdReq.getParam("userid")); //수정자
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, modifier);

			//작업예약
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String[][] arrCar     = new String[rowCnt][5];
			String[][] arrWrkBook = new String[rowCnt][2];
			
			/**********************************************************
			* 1. 크레인스케줄 존재여부 Check
			**********************************************************/
			for (int ii = 0; ii < rowCnt; ii++) {
				ydWbookId = commUtils.trim(gdReq.getHeader("YD_WBOOK_ID").getValue(ii));
				jrParam.setField("YD_WBOOK_ID", ydWbookId);
				
				//작업예약 크레인스케줄정보 조회
				JDTORecordSet jsCrn = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCommWbCrnSch", logId, methodNm, "작업예약 크레인스케줄정보 조회"); 

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
			BtYsDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCommCarSchWbDel", logId, methodNm, "차량스케줄 야드상차작업예약ID, 야드하차작업예약ID 삭제");
		
			//대차스케줄 야드상차작업예약ID, 야드하차작업예약ID 삭제
			BtYsDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCommTcarSchWbDel", logId, methodNm, "대차스케줄 야드상차작업예약ID, 야드하차작업예약ID 삭제");
		
			/**********************************************************
			* 3. 작업예약/재료 삭제
			**********************************************************/
			//작업예약재료 삭제
			BtYsDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnWrkBookMtl", logId, methodNm, "TB_YS_WRKBOOKMTL");				

			//작업예약 삭제
			BtYsDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnWrkBook", logId, methodNm, "TB_YS_WRKBOOK");
			

			commUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of delWrkBook	
	/**
	 *      [A] 오퍼레이션명 : 작업예약관리-삭제
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord delWrkBook(GridData gdReq) throws DAOException {
		String methodNm = "작업예약관리-삭제[BtYsJspSeEJB.delWrkBook] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			String ydWbookId  = ""; //야드작업예약ID
		    String ydEqpId    = ""; //야드설비ID
		    String ydSchCd    = ""; //야드스케쥴코드
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			//처리완료한 야드작업예약ID
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				ydWbookId = commUtils.trim(gdReq.getHeader("YD_WBOOK_ID").getValue(ii));
			    ydEqpId   = commUtils.trim(gdReq.getHeader("YD_WRK_CRN"  ).getValue(ii));
			    ydSchCd   = commUtils.trim(gdReq.getHeader("YD_SCH_CD"  ).getValue(ii));

				jrParam.setField("YD_WBOOK_ID"  , ydWbookId );
				jrParam.setField("YD_EQP_ID"    , ydEqpId   );
				jrParam.setField("YD_SCH_CD"    , ydSchCd   );
				

				/**********************************************************
				* 2. 작업예약 취소
				**********************************************************/
				jrRtn = commUtils.addSndData(jrRtn, this.trtWrkBookCncl(jrParam));
			}
			

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
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
		String methodNm = "대차 현재동 변경[BtYsJspSeEJB.updTcarCurrBay] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			String ydEqpId        = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"     )); //야드설비ID(대차)
			String ydCurrBayGpNew = commUtils.trim(rcvMsg.getFieldString("YD_CURR_BAY_GP")); //야드현재동구분(신규)

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
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(rcvMsg.getFieldString("MODIFIER")));

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_EQP_ID"         , ydEqpId       );
			jrParam.setField("YD_CURR_BAY_GP_NEW", ydCurrBayGpNew);
			
			/**********************************************************
			* 1. 대차Bed상태 조회
			**********************************************************/
			JDTORecordSet jsTcar = commDao.select(jrParam, "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getStatTcarBed", logId, methodNm, "대차Bed상태 조회");

			if (jsTcar != null && jsTcar.size() > 0) {
		    	JDTORecord jrTcar = jsTcar.getRecord(0);

		    	ydCurrBayGpCur     = commUtils.trim(jrTcar.getFieldString("YD_CURR_BAY_GP"         ));
			    ydStkColGpCur      = commUtils.trim(jrTcar.getFieldString("YD_STK_COL_GP_CUR"      ));
			    ydStkColGpNew      = commUtils.trim(jrTcar.getFieldString("YD_STK_COL_GP_NEW"      ));
			    ydStkBedActStatCur = commUtils.trim(jrTcar.getFieldString("YD_STK_BED_ACT_STAT_CUR"));
			    ydStkBedActStatNew = commUtils.trim(jrTcar.getFieldString("YD_STK_BED_ACT_STAT_NEW"));

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
			jrParam.setField("YD_STK_COL_GP", ydEqpId.substring(0, 1) + "_" + ydEqpId.substring(2)); //야드적치열구분(대차전체Bed)

			//적치Bed(전체) 비활성화
			commDao.update(jrParam, "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updStatStkBedActCA", logId, methodNm, "적치Bed(전체) 비활성화");

			//적치단 재료 삭제
			commDao.update(jrParam, "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updSlabYdStkLyrClr", logId, methodNm, "적치단 재료 삭제");

			/**********************************************************
			* 3. 현재동 변경 및 저장위치제원 전문 조회
			**********************************************************/
			if (!ydCurrBayGpCur.equals(ydCurrBayGpNew)) {
				//설비 현재동 수정
				jrParam.setField("YD_CURR_BAY_GP", ydCurrBayGpNew);

				commDao.update(jrParam, "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updSlabYdEqpCurrBay", logId, methodNm, "설비 현재동 수정");

				//기존 Bed의 상태가 변경되었으면 저장위치제원(YDY1L001, YDY3L001) 전문 조회
				if ("L".equals(ydStkBedActStatCur)) {
					jrParam.setField("YD_INFO_SYNC_CD", "4"          ); //야드정보동기화코드(Bed)
					jrParam.setField("YD_STK_COL_GP"  , ydStkColGpCur); //야드적치열구분
					jrParam.setField("YD_STK_BED_NO"  , "01"         ); //야드적치Bed번호

					//전송Data 조회
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN2L001", jrParam));
				}
			}
			
			/**********************************************************
			* 4. 신규 저장위치  활성화 및 저장위치제원 전문 조회
			**********************************************************/
			//신규 적치Bed Close 상태이면 활성화
			jrParam.setField("YD_STK_COL_GP"      , ydStkColGpNew); //야드적치열구분
			jrParam.setField("YD_STK_BED_NO"      , "01"         ); //야드적치Bed번호
			jrParam.setField("YD_STK_BED_ACT_STAT", "L"          ); //야드적치Bed활성상태(적치가능)

			commDao.update(jrParam, "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updStatStkBedAct", logId, methodNm, "신규 적치Bed Close 상태이면 활성화");

			//적치단 재료 삭제
			commDao.update(jrParam, "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updSlabYdStkLyrClr", logId, methodNm, "적치단 재료 삭제");

			//신규 Bed의 상태가 변경되었으면 저장위치제원(YDY1L001, YDY3L001) 전문 전송
			if ("C".equals(ydStkBedActStatNew)) {
				jrParam.setField("YD_INFO_SYNC_CD", "4"); //야드정보동기화코드(Bed)

				//전송Data 조회
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN2L001", jrParam));
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updTcarCurrBay
	
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
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			String trtDtlGp = commUtils.trim(gdReq.getParam("TRT_DTL_GP"));	//처리상세구분
			String ydEqpId  = commUtils.trim(gdReq.getParam("YD_EQP_ID" ));	//야드설비ID(대차)
			String modifier = commUtils.trim(gdReq.getParam("userid"      ));	//수정자
			String currDate = commUtils.getDateTime14();						//현재시각

			if ("".equals(ydEqpId)) {
				throw new Exception("대차설비ID가 없습니다.");
			}

			//Return Value
			JDTORecord jrRtn = null;

			//EJB Call을 위한 Message 생성용 - Log ID, Method, 수정자 Set
			JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, modifier);
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name			
			jrYdMsg.setField("YD_EQP_ID", ydEqpId); //야드설비ID

			if ("ST".equals(trtDtlGp)) {
				//설비상태 변경
				jrYdMsg.setField("JMS_TC_CD"          , "N2YSL003"); //설비고장복구실적
				jrYdMsg.setField("YD_EQP_STAT"        , commUtils.trim(gdReq.getParam("YD_EQP_STAT"))); //야드설비상태(B:고장, N:정상)
				jrYdMsg.setField("YD_EQP_PAUSE_CODE"  , "0000"    ); //야드설비휴지코드
				jrYdMsg.setField("YD_EQP_TRBL_RCVR_DT", currDate  ); //야드설비고장복구일시

				EJBConnector sndConn = new EJBConnector("default", "BtYsL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvN2YSL003", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
			} else if ("MD".equals(trtDtlGp)) {
				//작업Mode 변경
				jrYdMsg.setField("JMS_TC_CD"      , "N2YSL003"); //설비운전모드전환
				jrYdMsg.setField("YD_EQP_WRK_MODE", commUtils.trim(gdReq.getParam("YD_EQP_WRK_MODE"))); //야드설비작업Mode(1:On-Line, 0:Off-Line)

				EJBConnector sndConn = new EJBConnector("default", "BtYsL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvN2YSL003", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
			} else if ("HB".equals(trtDtlGp)) {
				//Home동 변경 - Log ID, Method, 수정자 Set
				JDTORecord jrParam = commUtils.getParam(logId, methodNm, modifier);

				jrParam.setField("YD_EQP_ID"     , ydEqpId); //야드설비ID
				jrParam.setField("YD_HOME_BAY_GP", commUtils.trim(gdReq.getParam("YD_HOME_BAY_GP")));
				
				commDao.update(jrParam, "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updSlabYdEqpHomeBay", logId, methodNm, "Home동 변경");
				
			} else if ("CB".equals(trtDtlGp)) {
				//현재동 변경
				jrYdMsg.setField("YD_CURR_BAY_GP", commUtils.trim(gdReq.getParam("YD_CURR_BAY_GP")));
  
				jrRtn = this.updTcarCurrBay(jrYdMsg);
			} else if ("TS".equals(trtDtlGp)) {
				//공대차출발지시 등록
				jrYdMsg.setField("YD_BAY_GP", commUtils.trim(gdReq.getParam("YD_CURR_BAY_GP_TS"))); //야드동구분(상차동)

	//			jrRtn = btYsComm.trtTcarSchLevWo(jrYdMsg);
			} else if ("TL".equals(trtDtlGp)) {
				//출발실적처리
				jrYdMsg.setField("JMS_TC_CD"      , "N2YSL007"); //대차이동실적
				jrYdMsg.setField("YD_TCAR_MOVE_GP", "S"       ); //야드대차이동구분(출발)
				jrYdMsg.setField("YD_BAY_GP1"     , commUtils.trim(gdReq.getParam("YD_CURR_BAY_GP_TL"))); //야드동구분1(현재동)

				EJBConnector sndConn = new EJBConnector("default", "BtYsL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvN2YSL007", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
			} else if ("TA".equals(trtDtlGp)) {
				//도착실적처리
				jrYdMsg.setField("JMS_TC_CD"      , "N2YSL007"); //대차이동실적
				jrYdMsg.setField("YD_TCAR_MOVE_GP", "E"       ); //야드대차이동구분(도착)
				jrYdMsg.setField("YD_BAY_GP1"     , commUtils.trim(gdReq.getParam("YD_CURR_BAY_GP_TA"))); //야드동구분1(현재동)

				EJBConnector sndConn = new EJBConnector("default", "BtYsL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvN2YSL007", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
			} else if ("TC".equals(trtDtlGp)) {
				//완료실적처리
				String ydCarProgStat = commUtils.trim(gdReq.getHeader("YD_CAR_PROG_STAT").getValue(0));	//야드차량진행상태
				
				if ("4".equals(ydCarProgStat)) {
				} else if ("D".equals(ydCarProgStat)) {
				}
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtDtlGp + "] 입니다.");
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of trtTcarStatSet

	/**
	 * 저장위치 좌표설정 - 열정보 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updStrLocPosSetCol(GridData gdReq) throws DAOException {
		String methodNm = "저장위치 좌표설정 - 열정보 변경[BtYsJspSeEJB.updStrLocPosSetCol] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				//열정보 수정
				jrParam.setField("YD_GP"				, commUtils.getValue(gdReq, "YD_GP", ii) ); 
				jrParam.setField("YD_BAY_GP"			, commUtils.getValue(gdReq, "YD_BAY_GP", ii) ); 
				jrParam.setField("YD_EQP_GP"			, commUtils.getValue(gdReq, "YD_EQP_GP", ii) ); 
				jrParam.setField("YD_STK_COL_NO"		, commUtils.getValue(gdReq, "YD_STK_COL_NO", ii) ); 
				jrParam.setField("YD_STK_COL_ACT_STAT"	, commUtils.getValue(gdReq, "YD_STK_COL_ACT_STAT", ii) ); 
				jrParam.setField("YD_STK_COL_RULE_XAXIS", commUtils.getValue(gdReq, "YD_STK_COL_RULE_XAXIS", ii) ); 
				jrParam.setField("YD_STK_COL_RULE_YAXIS", commUtils.getValue(gdReq, "YD_STK_COL_RULE_YAXIS", ii) ); 
				jrParam.setField("YD_STK_COL_W"			, commUtils.getValue(gdReq, "YD_STK_COL_W", ii) ); 
				jrParam.setField("YD_STK_COL_L"			, commUtils.getValue(gdReq, "YD_STK_COL_L", ii) ); 
				jrParam.setField("YS_STK_COL_L_GP"		, commUtils.getValue(gdReq, "YS_STK_COL_L_GP", ii) ); 
				jrParam.setField("YS_STK_COL_GP"		, commUtils.getValue(gdReq, "YS_STK_COL_GP", ii) ); 
				jrParam.setField("YD_STKBED_USG_CD"		, commUtils.getValue(gdReq, "YD_STKBED_USG_CD", ii) ); 

				commDao.update(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.updYdStkcol", logId, methodNm, "열정보 수정");
								
				jrParam.setField("YD_INFO_SYNC_CD", "4"          ); //야드정보동기화코드(Bed)
				jrParam.setField("YS_STK_COL_GP"  , commUtils.trim(gdReq.getHeader("YS_STK_COL_GP").getValue(ii))); //야드적치열구분
				//jrParam.setField("YD_STK_BED_NO"  , "01"         ); //야드적치Bed번호

				//전송Data 조회
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN2L001", jrParam));				
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updStrLocPosSetCol
	
	/**
	 * 저장위치 좌표설정 - Bed정보 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updStrLocPosSetBed(GridData gdReq) throws DAOException {
		String methodNm = "저장위치 좌표설정 - Bed정보 변경[BtYsJspSeEJB.updStrLocPosSetBed] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				//Bed정보 수정 
				jrParam.setField("YD_STR_GTR_CD"		, commUtils.getValue(gdReq, "YD_STR_GTR_CD", ii) ); 
				jrParam.setField("YS_STK_BED_TP"		, commUtils.getValue(gdReq, "YS_STK_BED_TP", ii) ); 
				jrParam.setField("YS_STK_BED_L_GP"		, commUtils.getValue(gdReq, "YS_STK_BED_L_GP", ii) ); 
				jrParam.setField("YD_STK_BED_DIR_GP"	, commUtils.getValue(gdReq, "YD_STK_BED_DIR_GP", ii) ); 
				jrParam.setField("YD_STK_BED_ACT_STAT"	, commUtils.getValue(gdReq, "YD_STK_BED_ACT_STAT", ii) ); 
				jrParam.setField("YD_STK_BED_WHIO_STAT"	, commUtils.getValue(gdReq, "YD_STK_BED_WHIO_STAT", ii) ); 
				jrParam.setField("YD_STK_BED_XAXIS"		, commUtils.getValue(gdReq, "YD_STK_BED_XAXIS", ii) ); 
				jrParam.setField("YD_STK_BED_YAXIS"		, commUtils.getValue(gdReq, "YD_STK_BED_YAXIS", ii) ); 
				jrParam.setField("YD_STK_BED_LYR_MAX"	, commUtils.getValue(gdReq, "YD_STK_BED_LYR_MAX", ii) ); 
				jrParam.setField("YD_STK_BED_WT_MAX"	, commUtils.getValue(gdReq, "YD_STK_BED_WT_MAX", ii) ); 
				jrParam.setField("YD_STK_BED_H_MAX"		, commUtils.getValue(gdReq, "YD_STK_BED_H_MAX", ii) ); 
				jrParam.setField("YD_STK_BED_L_MAX"		, commUtils.getValue(gdReq, "YD_STK_BED_L_MAX", ii) ); 
				jrParam.setField("YD_STK_BED_W_MAX"		, commUtils.getValue(gdReq, "YD_STK_BED_W_MAX", ii) ); 
				jrParam.setField("YD_STK_BED_XAXIS_TOL"	, commUtils.getValue(gdReq, "YD_STK_BED_XAXIS_TOL", ii) ); 
				jrParam.setField("YD_STK_BED_YAXIS_TOL"	, commUtils.getValue(gdReq, "YD_STK_BED_YAXIS_TOL", ii) );
				jrParam.setField("YD_STK_BED_XAXIS1"	, commUtils.getValue(gdReq, "YD_STK_BED_XAXIS1", ii) ); 
				jrParam.setField("YD_STK_BED_YAXIS1"	, commUtils.getValue(gdReq, "YD_STK_BED_YAXIS1", ii) ); 
				jrParam.setField("YS_STK_COL_GP"		, commUtils.getValue(gdReq, "YS_STK_COL_GP", ii) ); 
				jrParam.setField("YS_STK_BED_NO"		, commUtils.getValue(gdReq, "YS_STK_BED_NO", ii) ); 

				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkbed", logId, methodNm, "Bed정보 수정");
								
				jrParam.setField("YD_INFO_SYNC_CD", "4"          ); //야드정보동기화코드(Bed)
				jrParam.setField("YS_STK_COL_GP"  , commUtils.trim(gdReq.getHeader("YS_STK_COL_GP").getValue(ii)) ); //야드적치열구분
				jrParam.setField("YS_STK_BED_NO"  , commUtils.trim(gdReq.getHeader("YS_STK_BED_NO").getValue(ii)) ); //야드적치Bed번호

				//전송Data 조회
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN2L001", jrParam));				
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updStrLocPosSetBed
	
	/**
	 * 스케줄기준관리 - 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updSchRule(GridData gdReq) throws DAOException {
		String methodNm = "스케줄기준관리 - 수정[BtYsJspSeEJB.updSchRule] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				//스케줄기준 수정 
				jrParam.setField("M_CRN_PRIOR1"		, commUtils.getValue(gdReq, "M_CRN_PRIOR1", ii)); 
				jrParam.setField("M_CRN_PRIOR2"		, commUtils.getValue(gdReq, "M_CRN_PRIOR2", ii)); 
				jrParam.setField("YD_SCH_CD"		, commUtils.getValue(gdReq, "YD_SCH_CD", ii)); 
				jrParam.setField("YD_SCH_GP"		, commUtils.trim(gdReq.getParam("YD_SCH_GP")) ); 
				jrParam.setField("YD_CRN_STAT1"		, commUtils.getValue(gdReq, "YD_CRN_STAT1", ii) ); 
				jrParam.setField("YD_CRN_STAT2"		, commUtils.getValue(gdReq, "YD_CRN_STAT2", ii) ); 
				
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdSchRule", logId, methodNm, "스케줄기준 수정");
			}
			
			if("CR".equals(gdReq.getParam("YD_SCH_GP"))) {
				
				for (int ii = 0; ii < rowCnt; ii++) {
					
					//스케줄금지여부 수정 
					jrParam.setField("YD_SCH_PROH_EXN"	, commUtils.getValue(gdReq, "YD_SCH_PROH_EXN", ii)); 
					jrParam.setField("YD_SCH_CD"		, commUtils.getValue(gdReq, "YD_SCH_CD", ii)); 
					
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdSchProhExn", logId, methodNm, "스케줄금지여부수정");
				}
			}			

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updSchRule

	/**
	 * 야드 저장위치 등록 (PDA)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord regStrLocPda(JDTORecord recPara) throws DAOException {
		String methodNm = "야드 저장위치 등록 (PDA) - 등록[BtYsJspSeEJB.regStrLocPda] < " + recPara.getResultMsg();
		String logId = recPara.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+", recPara);

			//Return Value
			JDTORecord jrRtn = null;
			JDTORecord jrParam2 = null;
			
			String szFromYsStkColGp = "";
			String szFromYsStkBedNo = "";
			String szFromYsStkLyrNo = "";
			String szFromYsStkSeqNo = "";
			
			String szYsStkColGp = commUtils.trim(recPara.getFieldString("YS_STK_COL_GP"));
			String szYsStkBedNo = commUtils.trim(recPara.getFieldString("YS_STK_BED_NO"));
			String szYsStkLyrNo = commUtils.trim(recPara.getFieldString("YS_STK_LYR_NO"));
			String szYsStkSeqNo = commUtils.trim(recPara.getFieldString("YS_STK_SEQ_NO"));
			
			String szStlNo = commUtils.trim(recPara.getFieldString("SSTL_NO"));
			String szCurrProgCd = "";
			String szOrdYeojaeGp = "";
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(recPara.getFieldString("userid")));

			//입력받은 TO위치에 재료가 이미 존재하는지 체크하여 존재하거나 적치단 활성상태가 'E'적치가능이 아니면 에러 메세지를 리턴하고 종료한다.
			jrParam.setField("YS_STK_COL_GP", commUtils.trim(recPara.getFieldString("YS_STK_COL_GP")));
			jrParam.setField("YS_STK_BED_NO", commUtils.trim(recPara.getFieldString("YS_STK_BED_NO")));
			jrParam.setField("YS_STK_LYR_NO", commUtils.trim(recPara.getFieldString("YS_STK_LYR_NO")));
			jrParam.setField("YS_STK_SEQ_NO", commUtils.trim(recPara.getFieldString("YS_STK_SEQ_NO")));
			
			
				if(szYsStkColGp.matches("KH\\d{4}") ){
					JDTORecordSet jsStkLyr = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStkLyr", logId, methodNm, "적치단정보조회");
					if (jsStkLyr.size() > 0) {
						
						if(!"".equals(jsStkLyr.getRecord(0).getFieldString("SSTL_NO"))) {
							
							jrRtn = JDTORecordFactory.getInstance().create();
							jrRtn.setField("RETN_CD", YsConstant.RETN_CD_FAILURE);
							jrRtn.setField("RETN_MSG", "입력한 TO위치에 재료번호:" + jsStkLyr.getRecord(0).getFieldString("SSTL_NO") + " 가 존재합니다!!");
							
							return jrRtn; 
						}
						
						if(!"E".equals(jsStkLyr.getRecord(0).getFieldString("YD_STK_LYR_ACT_STAT"))) {
							
							jrRtn = JDTORecordFactory.getInstance().create();
							jrRtn.setField("RETN_CD", YsConstant.RETN_CD_FAILURE);
							jrRtn.setField("RETN_MSG", "입력한 TO위치가 적치가능하지 않습니다!! 현재상태:" + jsStkLyr.getRecord(0).getFieldString("YD_STK_LYR_ACT_STAT"));
							
							return jrRtn; 
						}
				    } else {
				    	//TO 위치가 존재하지 않으면 에러메세지를 리턴하고 종료한다.
						jrRtn = JDTORecordFactory.getInstance().create();
						jrRtn.setField("RETN_CD", YsConstant.RETN_CD_FAILURE);
						jrRtn.setField("RETN_MSG", "입력한 TO위치가 존재하지 않습니다!! ");
						
						return jrRtn; 
				    	}
				}
			   if("KH".equals(szYsStkColGp.substring(0,2))){
			    	String result  = this.doChkHLocation(recPara);
			     
			    	if(!"OK".equals(result)){
			    		
			    		jrRtn = JDTORecordFactory.getInstance().create();
						jrRtn.setField("RETN_CD", YsConstant.RETN_CD_FAILURE);
						if("I".equals(result))
								jrRtn.setField("RETN_MSG", "하단에 코일이 없습니다. ");
						else if("D".equals(result))
							jrRtn.setField("RETN_MSG", "상단에 코일이 있습니다. ");
							
						return jrRtn; 
						
			    	}
			    }
			//SSTL_NO로 저장위치 조회하여 FROM위치가 존재하면 그 위치에서 SSTL_NO를 Clear 한다.
			jrParam.setField("SSTL_NO", commUtils.trim(recPara.getFieldString("SSTL_NO")));
			jrParam.setField("YD_GP", commUtils.trim(recPara.getFieldString("YD_GP")));
			
			JDTORecordSet jsStkLyrStlNo = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStrLocRegPda", logId, methodNm, "재료번호로 조회");
			
			if(jsStkLyrStlNo.size() > 0) {
			
				String sFromLoc = null;
				
				for (int mm = 0; mm < jsStkLyrStlNo.size(); mm++) {
					if(!"".equals(jsStkLyrStlNo.getRecord(mm).getFieldString("YD_CRN_SCH_ID"))) {
						//크레인스케줄 편성 대상이면 에러 메세지를 리턴하고 종료한다.
						
						sFromLoc = jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_COL_GP") + "-" 
						 + jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_BED_NO") + "-"
						 + jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_LYR_NO") + "-"
						 + jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_SEQ_NO");
						
						jrRtn = JDTORecordFactory.getInstance().create();
						jrRtn.setField("RETN_CD", YsConstant.RETN_CD_FAILURE);
						jrRtn.setField("RETN_MSG", "재료번호: "+ jsStkLyrStlNo.getRecord(mm).getFieldString("SSTL_NO") +  
								" 는 FROM 위치("+sFromLoc+")에서  크레인스케줄에 편성되어 있습니다. 등록 작업을 할 수 없습니다."
									   );
						
						return jrRtn; 
					} else {
						
						szFromYsStkColGp = jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_COL_GP");
						szFromYsStkBedNo = jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_BED_NO");
						szFromYsStkLyrNo = jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_LYR_NO");
						szFromYsStkSeqNo = jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_SEQ_NO");
						
						szCurrProgCd  	 = jsStkLyrStlNo.getRecord(mm).getFieldString("CURR_PROG_CD");
						szOrdYeojaeGp    = jsStkLyrStlNo.getRecord(mm).getFieldString("ORD_YEOJAE_GP");
						
					}
				}
				
				//SSTL_NO 로 STKLYR Clear 하기
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.clearStkLyr", logId, methodNm, "이전 SSTL_NO가 있던 위치 Clear");	
				
				
				if("KB".equals(szYsStkColGp.substring(0,2))){
					jrParam2 = JDTORecordFactory.getInstance().create();
					jrParam2.setField("MODIFIER", commUtils.trim(recPara.getFieldString("userid")));  
					jrParam2.setField("YD_STK_COL_GP", szFromYsStkColGp);  
					jrParam2.setField("YD_STK_BED_NO", szFromYsStkBedNo); 
					jrParam2.setField("SSTL_NO"		 , szStlNo );
					
					//적치Bed(완산Bed->입출고가능) 수정
					commDao.update(jrParam2, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005StkBedF", logId, methodNm, "권상위치 적치Bed(완산Bed->입출고가능) 수정2");
					
					commDao.update(jrParam2, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updStkBedActStatReSetSstlNo", logId, methodNm, "적치Bed 활성상태 초기화2");
				}
			}
			
			//입력받은 위치에 SSTL_NO을 적치중으로 설정한다.
			jrParam.setField("YD_STK_LYR_ACT_STAT", ""); //적치가능 :"" 값은 이전값을 변경안한다는 의미
			jrParam.setField("YD_STK_LYR_MTL_STAT", "C"); //적치중
			
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyr", logId, methodNm, "적치단정보 수정");
			
			
			
			
			jrParam.setField("FNL_REG_PGM"			, "btStrLocModjm" );
			jrParam.setField("YD_GP"				, szYsStkColGp.substring(0,1) );
			jrParam.setField("YD_BAY_GP"			, szYsStkColGp.substring(1,2) );
			jrParam.setField("YD_EQP_GP"			, szYsStkColGp.substring(2,4) );
			jrParam.setField("YS_STK_COL_NO"		, szYsStkColGp.substring(4,6) );
			jrParam.setField("YS_STK_BED_NO"		, szYsStkBedNo );
			jrParam.setField("YS_STK_LYR_NO"		, szYsStkLyrNo );
			jrParam.setField("YS_STK_SEQ_NO"		, szYsStkSeqNo );
			jrParam.setField("YS_STR_LOC"			, szYsStkColGp + szYsStkBedNo + szYsStkLyrNo + szYsStkSeqNo );
			jrParam.setField("SSTL_NO"				, szStlNo );
			jrParam.setField("MODIFIER"				, commUtils.trim(recPara.getFieldString("userid")) );
			
			if("K".equals(szYsStkColGp.substring(0,1))){
				//번들공통 위치정보 수정하기
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBndlCommYsStrLoc", logId, methodNm, "번들공통 야드저장위치 수정");
			}else{
				if("D".equals(szCurrProgCd) && "1".equals(szOrdYeojaeGp)) {
					//공통의 진도코드가 'D':이송지시대기 이고 주여구분이 '1':주문재 이면  야드저장품의 재료진도코드를 'B':지시대기 로 변경한다. + 위치정보 수정
					jrParam.setField("CURR_PROG_CD"		, "B" );
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBtCommYsStrLocProgCd", logId, methodNm, "BILLET공통 야드저장위치,진도코드 수정");
				} else {
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBtCommYsStrLoc", logId, methodNm, "BILLET공통 야드저장위치 수정");
				}
			}
			
			//야드저장품 위치정보 수정하기
			jrParam.setField("YS_STK_COL_GP"		, szYsStkColGp );
			jrParam.setField("YS_STK_BED_NO"		, szYsStkBedNo );
			jrParam.setField("YS_STK_LYR_NO"		, szYsStkLyrNo );
			jrParam.setField("YS_STK_SEQ_NO"		, szYsStkSeqNo );
			jrParam.setField("YS_STR_LOC"			, szYsStkColGp + szYsStkBedNo + szYsStkLyrNo + szYsStkSeqNo );
			jrParam.setField("SSTL_NO"				, szStlNo );
			
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStockYsStrLoc", logId, methodNm, "야드저장품 야드저장위치 수정");
			
			
			
			// 제품창고(K) A,B동 일반야드에 권하시 Bed 활성상태 변경 (해당 bed에 제품이 있어도 권하시 update. 해당 bed에 같은 길이 구분의 제품이 적치되어 있다는 전제) 
			if("KA".equals(szYsStkColGp.substring(0,2))||"KB".equals(szYsStkColGp.substring(0,2)) ){ 
				
				commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updStkBedActStatSstlNo", logId, methodNm, "권하위치 적치Bed 활성상태 변경");
			}
			
			
			//YS_작업이력(TB_YS_WRKHIST)에 변경정보를 등록한다.. 
			jrParam.setField("SSTL_NO"				, szStlNo );
			jrParam.setField("YD_GP"				, szYsStkColGp.substring(0,1) );
			jrParam.setField("YD_SCH_CD"			, szYsStkColGp.substring(0,2)+"YD01MM" );
			
			jrParam.setField("YS_UP_WR_LOC"			, szFromYsStkColGp + szFromYsStkBedNo );
			jrParam.setField("YS_UP_WR_LAYER"		, szFromYsStkLyrNo );
			jrParam.setField("YS_UP_WR_SEQ_NO"		, szFromYsStkSeqNo );

			jrParam.setField("YS_DN_WR_LOC"			, szYsStkColGp + szYsStkBedNo );
			jrParam.setField("YS_DN_WR_LAYER"		, szYsStkLyrNo );
			jrParam.setField("YS_DN_WR_SEQ_NO"		, szYsStkSeqNo );
			
			jrParam.setField("YD_SCH_ST_GP"			, "B" ); // 야드스케줄 기동 구분 "B" 로 넣어준다. B:작업자 Backup
			jrParam.setField("YD_AID_WRK_YN"		, "N" ); // 야드보조작업여부 - N:주작업
			
			commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkHistByJsp", logId, methodNm, "PDA화면에의한 이력정보 수정");
			
			
			//전송Data 조회
			jrParam.setField("YD_INFO_SYNC_CD", "5" ); 
			jrParam.setField("YS_STK_COL_GP"  , commUtils.trim(recPara.getFieldString("YS_STK_COL_GP")) ); //야드적치열구분
			jrParam.setField("YS_STK_BED_NO"  , commUtils.trim(recPara.getFieldString("YS_STK_BED_NO")) ); //야드적치Bed번호

			if("KA0".equals(szYsStkColGp.substring(0,3))){ 
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN6L002", jrParam));	
			}else if("KATY".equals(szYsStkColGp.substring(0,4))||"KB".equals(szYsStkColGp.substring(0,2)) ){ 
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN4L002", jrParam));	
			}else if("KD".equals(szYsStkColGp.substring(0,2)) ){ 
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN5L002", jrParam));	
			}else if("KE".equals(szYsStkColGp.substring(0,2)) ){ 
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN3L002", jrParam));	
			}else{
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN2L002", jrParam));
				
				//빌렛입고실적 송신
				jrParam.setField("SSTL_NO", szStlNo);
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSCUJ038Backup", jrParam));
			}
						
			//정상처리를 리턴한다.
			if (commUtils.isEmpty(jrRtn)) {
				jrRtn = JDTORecordFactory.getInstance().create();
			}
			jrRtn.setField("RETN_CD", YsConstant.RETN_CD_SUCCESS);
			jrRtn.setField("RETN_MSG", "정상처리 되었습니다!!");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of regStrLocPda
	
	/**
	 *      [A] 오퍼레이션명 : 크레인 상태 설정변경
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updbtCrnStsSetPp(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리 상태 설정변경[BtYsJspSeEJB.updbtCrnStsSetPp] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			String trtDtlGp = commUtils.trim(gdReq.getParam("TRT_DTL_GP"));	//처리상세구분
			String currDate = commUtils.getDateTime14();						//현재시각
			String ydEqpId  = commUtils.trim(gdReq.getParam("W_YD_EQP_ID" ));	//야드설비ID(크레인)

			
			if ("".equals(ydEqpId)) {
				throw new Exception("크레인설비ID가 없습니다.");
			}

			jrParam.setField("YD_EQP_ID", ydEqpId); //야드설비ID

			if ("ST".equals(trtDtlGp)) {
				//설비상태 변경
				jrParam.setField("JMS_TC_CD"          , "N2YSL003"); //설비고장복구실적
				jrParam.setField("YD_EQP_STAT"        , commUtils.trim(gdReq.getParam("YD_EQP_STAT"))); //야드설비상태(B:고장, N:정상)
				jrParam.setField("YD_EQP_PAUSE_CODE"  , "0000"    ); //야드설비휴지코드
				jrParam.setField("YD_EQP_TRBL_RCVR_DT", currDate  ); //야드설비고장복구일시

				EJBConnector sndConn = new EJBConnector("default", "BtYsL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvN2YSL003", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				
				
			} else if ("MD".equals(trtDtlGp)) {
				//작업Mode 변경
				jrParam.setField("JMS_TC_CD"      , "N2YSL003"); //설비운전모드전환
				jrParam.setField("YD_EQP_WRK_MODE", commUtils.trim(gdReq.getParam("YD_EQP_WRK_MODE"))); //야드설비작업Mode(1:On-Line, 0:Off-Line)

				EJBConnector sndConn = new EJBConnector("default", "BtYsL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvN2YSL003", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			} else if ("WO".equals(trtDtlGp)) {
				//명령선택기동
				jrParam.setField("JMS_TC_CD"       , "N2YSL004"); //크레인작업지시요구
				jrParam.setField("YD_WRK_PROG_STAT", "W"       ); //야드작업진행상태(명령선택대기)
				jrParam.setField("YD_SCH_CD"       , commUtils.trim(gdReq.getHeader("YD_SCH_CD"     ).getValue(0))); //야드스케쥴코드
				jrParam.setField("YD_CRN_SCH_ID"   , commUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID" ).getValue(0))); //야드크레인스케쥴ID

				EJBConnector sndConn = new EJBConnector("default", "BtYsL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvN2YSL004", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			} else if ("WU".equals(trtDtlGp)) {
				//권상실적처리
				jrParam.setField("JMS_TC_CD"       , "N2YSL005"); //크레인권상실적
				jrParam.setField("YD_EQP_WRK_MODE" , "9"       ); //야드설비작업Mode(Backup)
				jrParam.setField("YD_WRK_PROG_STAT", "2"       ); //야드작업진행상태(권상완료)
				jrParam.setField("YD_SCH_CD"       , commUtils.trim(gdReq.getHeader("YD_SCH_CD"         ).getValue(0))); //야드스케쥴코드
				jrParam.setField("YD_CRN_SCH_ID"   , commUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID"     ).getValue(0))); //야드크레인스케쥴ID
				jrParam.setField("YS_UP_WR_LOC"    , commUtils.trim(gdReq.getHeader("YS_UP_WO_LOC"      ).getValue(0))); //야드권상실적위치
				jrParam.setField("YS_UP_WR_LAYER"  , commUtils.trim(gdReq.getHeader("YS_UP_WO_LAYER"    ).getValue(0))); //야드권상실적단
				jrParam.setField("YD_CRN_XAXIS"    , commUtils.trim(gdReq.getHeader("YD_UP_WO_LOC_XAXIS").getValue(0))); //야드크레인X축
				jrParam.setField("YD_CRN_YAXIS"    , commUtils.trim(gdReq.getHeader("YD_UP_WO_LOC_YAXIS").getValue(0))); //야드크레인Y축
				jrParam.setField("YD_CRN_ZAXIS"    , commUtils.trim(gdReq.getHeader("YD_UP_WO_LOC_ZAXIS").getValue(0))); //야드크레인Z축
				
				EJBConnector sndConn = new EJBConnector("default", "BtYsL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvN2YSL005", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			} else if ("WD".equals(trtDtlGp)) {
				//권하실적처리
				jrParam.setField("JMS_TC_CD"       , "N2YSL006"); //크레인권하실적
				jrParam.setField("YD_EQP_WRK_MODE" , "9"       ); //야드설비작업Mode(Backup)
				jrParam.setField("YD_WRK_PROG_STAT", "4"       ); //야드작업진행상태(권하완료)
				jrParam.setField("YD_SCH_CD"       , commUtils.trim(gdReq.getHeader("YD_SCH_CD"         ).getValue(0))); //야드스케쥴코드
				jrParam.setField("YD_CRN_SCH_ID"   , commUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID"     ).getValue(0))); //야드크레인스케쥴ID
				jrParam.setField("YS_DN_WR_LOC"    , commUtils.trim(gdReq.getHeader("YS_DN_WO_LOC"      ).getValue(0))); //야드권하실적위치
				jrParam.setField("YS_DN_WR_LAYER"  , commUtils.trim(gdReq.getHeader("YS_DN_WO_LAYER"    ).getValue(0))); //야드권하실적단
				jrParam.setField("YD_CRN_XAXIS"    , commUtils.trim(gdReq.getHeader("YD_DN_WO_LOC_XAXIS").getValue(0))); //야드크레인X축
				jrParam.setField("YD_CRN_YAXIS"    , commUtils.trim(gdReq.getHeader("YD_DN_WO_LOC_YAXIS").getValue(0))); //야드크레인Y축
				jrParam.setField("YD_CRN_ZAXIS"    , commUtils.trim(gdReq.getHeader("YD_DN_WO_LOC_ZAXIS").getValue(0))); //야드크레인Z축
				
				EJBConnector sndConn = new EJBConnector("default", "BtYsL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvN2YSL006", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			} else if ("DL".equals(trtDtlGp)) {
				//권하위치변경
				jrParam.setField("YD_WRK_PROG_STAT", commUtils.trim(gdReq.getHeader("YD_WRK_PROG_STAT").getValue(0))); //야드작업진행상태
				jrParam.setField("YD_SCH_CD"       , commUtils.trim(gdReq.getHeader("YD_SCH_CD"       ).getValue(0))); //야드스케쥴코드
				jrParam.setField("YD_CRN_SCH_ID"   , commUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID"   ).getValue(0))); //야드크레인스케쥴ID
				jrParam.setField("YD_WBOOK_ID"     , commUtils.trim(gdReq.getHeader("YD_WBOOK_ID"     ).getValue(0))); //야드작업예약ID
				jrParam.setField("YS_DN_WO_LOC"    , commUtils.trim(gdReq.getParam("YS_DN_WO_LOC"))); //야드권하지시위치(신규)

				jrRtn = this.updCrnSchDnWoLoc(jrParam);
			} else {
				throw new Exception("정의되지 않은 처리구분[" + trtDtlGp + "] 입니다.");
			}
			
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
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
		String methodNm = "크레인스케줄 권하지시위치 변경[BtYsJspSeEJB.updCrnSchDnWoLoc] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			String ydEqpId       = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); //야드설비ID(크레인)
			String ydSchCd       = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
			String ydCrnSchId    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )); //야드크레인스케쥴ID
			String ydWbookId     = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"     )); //야드작업예약ID
			String ysDnWoLoc     = commUtils.trim(rcvMsg.getFieldString("YS_DN_WO_LOC"    )); //야드권하지시위치(신규)
			String ydWrkProgStat = commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
			String modifier      = commUtils.trim(rcvMsg.getFieldString("MODIFIER"      )); //수정자
			
			if ("".equals(ydCrnSchId)) {
				throw new Exception("크레인스케쥴ID가 없습니다.");
			} else if ("".equals(ysDnWoLoc)) {
				throw new Exception("변경할 권하지시위치가 없습니다.");
			}

			//Return Value
			JDTORecord sndRecord = JDTORecordFactory.getInstance().create();

			String ysStkColGp     = ysDnWoLoc.substring(0, 6); //야드적치열구분
			
			String ysStkBedNo     = ""; //야드권하지시위치(기존)
			String ydDnWoLocOld   = ""; //야드권하지시위치(기존)
			String ydDnWoLayerOld = ""; //야드권하지시위치(기존)
			String ydDnWoLayer    = ""; //야드권하지시단(신규)
			String ydDnWoLocXaxis = ""; //야드권하지시X축(신규)
			String ydDnWoLocYaxis = ""; //야드권하지시Y축(신규)
			String ydDnWoLocZaxis = ""; //야드권하지시Z축(신규)
			
			JDTORecordSet jsCrnSch = JDTORecordFactory.getInstance().createRecordSet("Temp");
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, modifier);

			jrParam.setField("YD_CRN_SCH_ID"       	, ydCrnSchId);
			jrParam.setField("YD_CARLD_WRK_BOOK_ID"	, ydWbookId );	//야드상차작업예약ID
			jrParam.setField("YS_STK_COL_GP"       	, ysStkColGp);
			
			jrParam.setField("MODIFIER"       		, modifier);

			if(ysDnWoLoc.length() == 6) {
			
				/**********************************************************
				* 1. 신규 권하지시위치 Bed정보 조회
				**********************************************************/
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchDnWoLocBt
				-- 베드 조회
				SELECT * FROM
				(
				SELECT A.YS_STK_COL_GP  
				     , A.YS_STK_BED_NO  
				     , B.YS_STK_LYR_NO
				     , MIN(A.YD_STK_BED_XAXIS)              AS YD_DN_WO_LOC_XAXIS
				     , MIN(A.YD_STK_BED_YAXIS)              AS YD_DN_WO_LOC_YAXIS
				     , MIN(A.YD_STK_BED_ZAXIS)              AS YD_DN_WO_LOC_ZAXIS
				     , MIN(CM.YS_DN_WO_LOC)                 AS YD_DN_WO_LOC_OLD
				     , MIN(CM.YS_DN_WO_LAYER)               AS YD_DN_WO_LAYER_OLD
				     , (SELECT CASE WHEN SUM(DECODE(YD_STK_LYR_MTL_STAT,'U',1,0)) > 0                           THEN 'UP' --권상대기 있음
				                   ELSE 'AAA' END 
				          FROM TB_YS_STKLYR 
				         WHERE YS_STK_COL_GP = A.YS_STK_COL_GP 
				           AND YS_STK_LYR_NO < B.YS_STK_LYR_NO 
				       ) AS DL_LOC_CHK_RST  
				  FROM TB_YS_STKBED A
				     , TB_YS_STKLYR B
				     , (SELECT CM.YS_DN_WO_LOC
				              ,CM.YS_DN_WO_LAYER 
				              ,CM.YD_MTL_SH
				              ,CM.YD_MTL_WT
				              ,CM.YD_MTL_T
				             
				          FROM (SELECT CS.YD_CRN_SCH_ID
				                      ,MIN(CS.YD_WBOOK_ID   ) AS YD_WBOOK_ID
				                      ,MIN(CS.YS_DN_WO_LOC  ) AS YS_DN_WO_LOC
				                      ,MIN(CS.YS_DN_WO_LAYER) AS YS_DN_WO_LAYER
				                      ,COUNT(*)               AS YD_MTL_SH
				                      ,SUM(ST.YD_MTL_WT)      AS YD_MTL_WT
				                      ,SUM(ST.YD_MTL_T )      AS YD_MTL_T
				                  FROM TB_YS_CRNSCH    CS
				                      ,TB_YS_CRNWRKMTL CM
				                      ,TB_YS_STOCK     ST
				                 WHERE CS.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				                   AND CM.YD_CRN_SCH_ID = CS.YD_CRN_SCH_ID
				                   AND CM.SSTL_NO = ST.SSTL_NO
				                   AND CM.DEL_YN = 'N'
				                 GROUP BY CS.YD_CRN_SCH_ID) CM) CM
				 WHERE A.YS_STK_COL_GP  = SUBSTR(:V_YS_STK_COL_GP, 1, 6)
				   AND A.YS_STK_COL_GP  = B.YS_STK_COL_GP
				   AND A.YS_STK_BED_NO  = B.YS_STK_BED_NO
				   AND B.YS_STK_LYR_NO  = NVL(
				                            (SELECT YS_STK_LYR_NO + DECODE(SUM_MTL_CNT,6,1,0) --블름은 6개bed
				                             FROM
				                                    (
				                                    SELECT YS_STK_LYR_NO
				                                         , SUM(CASE WHEN MTL_CNT > 0 THEN 1 ELSE 0 END ) AS SUM_MTL_CNT 
				                                    FROM (
				                                             SELECT YS_STK_COL_GP
				                                                  , YS_STK_BED_NO
				                                                  , YS_STK_LYR_NO
				                                                  , COUNT(SSTL_NO)  AS MTL_CNT
				                                               FROM TB_YS_STKLYR C
				                                              WHERE C.YS_STK_COL_GP = SUBSTR(:V_YS_STK_COL_GP, 1, 6)
				                                                AND C.SSTL_NO IS NOT NULL
				                                                AND C.YD_STK_LYR_ACT_STAT = 'E'
				                                              GROUP BY  YS_STK_COL_GP, YS_STK_BED_NO, YS_STK_LYR_NO
				                                         )  C         
				                                     GROUP BY YS_STK_COL_GP, YS_STK_LYR_NO
				                                     ORDER BY YS_STK_COL_GP, YS_STK_LYR_NO DESC
				                            ) 
				                            WHERE ROWNUM =1 
				                            ),'01')       
				   AND A.YS_STK_COL_GP  = SUBSTR(:V_YS_STK_COL_GP, 1, 6)
				   AND A.DEL_YN = 'N'
				   AND A.YD_STK_BED_ACT_STAT = 'L'
				   AND B.YD_STK_LYR_ACT_STAT = 'E'
				   AND B.SSTL_NO IS NULL
				  GROUP BY  A.YS_STK_COL_GP, A.YS_STK_BED_NO, B.YS_STK_LYR_NO
				  ORDER BY YS_STK_BED_NO
				) WHERE ROWNUM = 1   
				 */
				jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchDnWoLocBt", logId, methodNm, "신규권하위치 조회");
			} else {
				ysStkBedNo     = ysDnWoLoc.substring(6, 8); //야드적치Bed번호	
				jrParam.setField("YS_STK_BED_NO"       	, ysStkBedNo);
				
				/**********************************************************
				* 1. 신규 권하지시위치 Bed정보 조회
				**********************************************************/
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchDnWoLocCurBed 
			    SELECT A.YS_STK_COL_GP  
			         , A.YS_STK_BED_NO  
			         , B.YS_STK_LYR_NO
			         , MIN(A.YD_STK_BED_XAXIS)              AS YD_DN_WO_LOC_XAXIS
			         , MIN(A.YD_STK_BED_YAXIS)              AS YD_DN_WO_LOC_YAXIS
			         , MIN(A.YD_STK_BED_ZAXIS)              AS YD_DN_WO_LOC_ZAXIS
			         , MIN(CM.YS_DN_WO_LOC)                 AS YD_DN_WO_LOC_OLD
			         , MIN(CM.YS_DN_WO_LAYER)               AS YD_DN_WO_LAYER_OLD
			         , (SELECT CASE WHEN SUM(DECODE(YD_STK_LYR_MTL_STAT,'U',1,0)) > 0                           THEN 'UP' --권상대기 있음
			                       ELSE 'AAA' END 
			              FROM TB_YS_STKLYR 
			             WHERE YS_STK_COL_GP = A.YS_STK_COL_GP 
			               AND YS_STK_LYR_NO < B.YS_STK_LYR_NO 
			           ) AS DL_LOC_CHK_RST  
			      FROM TB_YS_STKBED A
			         , TB_YS_STKLYR B
			         , (SELECT CM.YS_DN_WO_LOC
			                  ,CM.YS_DN_WO_LAYER 
			                  ,CM.YD_MTL_SH
			                  ,CM.YD_MTL_WT
			                  ,CM.YD_MTL_T
			                  
			              FROM (SELECT CS.YD_CRN_SCH_ID
			                          ,MIN(CS.YD_WBOOK_ID   ) AS YD_WBOOK_ID
			                          ,MIN(CS.YS_DN_WO_LOC  ) AS YS_DN_WO_LOC
			                          ,MIN(CS.YS_DN_WO_LAYER) AS YS_DN_WO_LAYER
			                          ,COUNT(*)               AS YD_MTL_SH
			                          ,SUM(ST.YD_MTL_WT)      AS YD_MTL_WT
			                          ,SUM(ST.YD_MTL_T )      AS YD_MTL_T
			                      FROM TB_YS_CRNSCH    CS
			                          ,TB_YS_CRNWRKMTL CM
			                          ,TB_YS_STOCK     ST
			                     WHERE CS.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			                       AND CM.YD_CRN_SCH_ID = CS.YD_CRN_SCH_ID
			                       AND CM.SSTL_NO = ST.SSTL_NO
			                       AND CM.DEL_YN = 'N'
			                     GROUP BY CS.YD_CRN_SCH_ID) CM) CM
			     WHERE A.YS_STK_COL_GP  = SUBSTR(:V_YS_STK_COL_GP, 1, 6)
			       AND A.YS_STK_COL_GP  = B.YS_STK_COL_GP
			       AND A.YS_STK_BED_NO  = B.YS_STK_BED_NO
			       AND B.YS_STK_LYR_NO  = NVL(
			                                (SELECT MAX(YS_STK_LYR_NO) + 1
			                                   FROM TB_YS_STKLYR C
			                                  WHERE C.YS_STK_COL_GP = A.YS_STK_COL_GP
			                                    AND C.YS_STK_BED_NO = A.YS_STK_BED_NO 
			                                    AND C.SSTL_NO IS NOT NULL
			                                   GROUP BY C.YS_STK_COL_GP, C.YS_STK_BED_NO
			                                ),'01')       
			       AND A.YS_STK_BED_NO  = :V_YS_STK_BED_NO
			       AND A.DEL_YN = 'N'
			       AND A.YD_STK_BED_ACT_STAT = 'L'
			       AND B.YD_STK_LYR_ACT_STAT = 'E'
			       AND B.SSTL_NO IS NULL
			    GROUP BY A.YS_STK_COL_GP,A.YS_STK_BED_NO,B.YS_STK_LYR_NO   
			     
				 */
				 jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchDnWoLocCurBed", logId, methodNm, "신규권하위치 조회");				
			}
			
			if (jsCrnSch == null || jsCrnSch.size() <= 0) {
				throw new Exception("신규 권하지시위치[" + ysDnWoLoc + "] 정보가 없습니다.");
			} else {
			
		    	JDTORecord jrCrnSch = jsCrnSch.getRecord(0);

		    	ydDnWoLocOld   		= commUtils.trim(jrCrnSch.getFieldString("YD_DN_WO_LOC_OLD"  ));
		    	ydDnWoLayerOld 		= commUtils.trim(jrCrnSch.getFieldString("YS_DN_WO_LAYER_OLD"    ));
		    	ysStkBedNo          = commUtils.trim(jrCrnSch.getFieldString("YS_STK_BED_NO"    )); 
		    	ydDnWoLayer         = commUtils.trim(jrCrnSch.getFieldString("YS_STK_LYR_NO"    )); 
		    	ydDnWoLocXaxis 		= commUtils.trim(jrCrnSch.getFieldString("YD_DN_WO_LOC_XAXIS"));
		    	ydDnWoLocYaxis 		= commUtils.trim(jrCrnSch.getFieldString("YD_DN_WO_LOC_YAXIS"));
		    	ydDnWoLocZaxis 		= commUtils.trim(jrCrnSch.getFieldString("YD_DN_WO_LOC_ZAXIS"));
			    String dlLocChkRst 	= commUtils.trim(jrCrnSch.getFieldString("DL_LOC_CHK_RST"));
	

			    if ("UP".equals(dlLocChkRst)) {
					throw new Exception("권상/권하대기(U) 재료가 적치되어 있습니다.");
				}

			    //혹시 권하지시위치가 잘못 등록되어 있으면
			    if (ydDnWoLocOld.length() != 8) {
			    	ydDnWoLocOld = "XX010101";
				}
		    }
			
			/**********************************************************
			* 2. 권하지시위치 수정
			**********************************************************/
			jrParam.setField("YD_STK_COL_GP_OLD" , ydDnWoLocOld.substring(0, 6));
			jrParam.setField("YD_STK_BED_NO_OLD" , ydDnWoLocOld.substring(6, 8));
			jrParam.setField("YD_STK_LYR_NO_OLD" , ydDnWoLayerOld);
			jrParam.setField("YD_STK_COL_GP_NEW" , ysStkColGp    );
			jrParam.setField("YD_STK_BED_NO_NEW" , ysStkBedNo    );
			if(ysDnWoLoc.length() == 6) {
				jrParam.setField("YS_DN_WO_LOC"      , ysDnWoLoc+ysStkBedNo     );
			} else {
				jrParam.setField("YS_DN_WO_LOC"      , ysDnWoLoc     );
			}

			jrParam.setField("YS_DN_WO_LAYER"    , ydDnWoLayer   );
			jrParam.setField("YS_STK_BED_NO"     , ysStkBedNo);
			jrParam.setField("YS_STK_LYR_NO"     , ydDnWoLayer   );
			jrParam.setField("YD_DN_WO_LOC_XAXIS", ydDnWoLocXaxis);
			jrParam.setField("YD_DN_WO_LOC_YAXIS", ydDnWoLocYaxis);
			jrParam.setField("YD_DN_WO_LOC_ZAXIS", ydDnWoLocZaxis);

			//적치단 수정 - 기존 및 신규 권하지시위치
//			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnSchDnWoLocStkLyr", logId, methodNm, "TB_YS_STKLYR");				

			//적치단 수정 - 기존
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdByCrnSchId
			--기존 권하지시위치
			UPDATE TB_YS_STKLYR
			   SET SSTL_NO = NULL
			     , YD_STK_LYR_MTL_STAT = 'E'
			 WHERE SSTL_NO IN (SELECT SSTL_NO
			                    FROM TB_YS_CRNWRKMTL
			                   WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			                 )
			   AND YS_STK_COL_GP = :V_YD_STK_COL_GP_OLD
			   AND YS_STK_BED_NO = :V_YD_STK_BED_NO_OLD                  
     
			*/
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdByCrnSchId", logId, methodNm, "기존권하위치 CLEAR");	
			
			
			
			//신규 적치단 재료정보READ
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnWekMtlByschid 
			SELECT A.YD_CRN_SCH_ID
			     , A.SSTL_NO         -- 기존 재료정보 
			     , (SELECT YS_STK_SEQ_NO
			          FROM TB_YS_STKLYR 
			         WHERE SSTL_NO = A.SSTL_NO
			           AND YD_STK_LYR_MTL_STAT IN ('C','U')
			           ) AS YS_STK_SEQ_NO     --신규 위치에 SEQ_NO   
			  FROM TB_YS_CRNWRKMTL A
			 WHERE A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			   AND A.DEL_YN = 'N'
			*/	   
			
			JDTORecordSet jsCrnSchMtl = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnWekMtlByschid", logId, methodNm, "기존권하위치 조회");
			

			JDTORecord recOutTemp = null;
			JDTORecord recInTemp = null;
			
			String szSSTL_NO = null; 
			String szSEQ_NO = null; 
			 
			int intRtnVal = 0; 
			
			//----------------------------------------------------------------------------------------------------------
			//신규적치단 활성화
			//----------------------------------------------------------------------------------------------------------
			for(int Loop_i = 1; Loop_i <= jsCrnSchMtl.size(); Loop_i++) {
				jsCrnSchMtl.absolute(Loop_i);
		    	recOutTemp = JDTORecordFactory.getInstance().create();
		    	recOutTemp.setRecord(jsCrnSchMtl.getRecord());
		    	
		    	szSSTL_NO   = commUtils.trim(recOutTemp.getFieldString("SSTL_NO"       ));
		    	szSEQ_NO 	= commUtils.trim(recOutTemp.getFieldString("YS_STK_SEQ_NO"));
		    	
		    	recInTemp  = JDTORecordFactory.getInstance().create();
		    	recInTemp.setField("YS_STK_COL_GP", ysStkColGp);
		    	recInTemp.setField("YS_STK_BED_NO", ysStkBedNo);
		    	recInTemp.setField("YS_STK_LYR_NO" ,ydDnWoLayer);
		    	recInTemp.setField("YS_STK_SEQ_NO", szSEQ_NO);
		    	recInTemp.setField("SSTL_NO",       szSSTL_NO);
		    	recInTemp.setField("YD_STK_LYR_ACT_STAT", "E");
		    	recInTemp.setField("YD_STK_LYR_MTL_STAT", "D");
		    	recInTemp.setField("MODIFIER"      , modifier);
		    	
		    	/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColBedGp  
		    	UPDATE TB_YS_STKLYR            
		    	   SET MOD_DDTT     = SYSDATE             
		    		 , MODIFIER     = :V_MODIFIER             
		    		 , YD_STK_LYR_ACT_STAT = NVL(:V_YD_STK_LYR_ACT_STAT,YD_STK_LYR_ACT_STAT)
		    	     , SSTL_NO = NVL(:V_SSTL_NO,SSTL_NO)
		    	     , YD_STK_LYR_MTL_STAT = NVL(:V_YD_STK_LYR_MTL_STAT,YD_STK_LYR_MTL_STAT)
		    	 WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
		    	   AND YS_STK_BED_NO = :V_YS_STK_BED_NO
		    	   AND YS_STK_LYR_NO = :V_YS_STK_LYR_NO   
		    	   AND YS_STK_SEQ_NO = :V_YS_STK_SEQ_NO   
		    	 */  
				intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColBedGp", logId, methodNm, "TB_YS_STKLYR 등록");
				
				if(intRtnVal <= 0) {
					commUtils.printLog(logId, "[" + methodNm + "] 적치단[" + ysStkColGp + "]활성화중 ERROR 발생", "SL");
					throw new Exception("적치단변경시 오류 발생.");
				}
			}
			
			/**********************************************************
			* 1. 크레인스케줄 취소
			* 빌렛소형 임시베드 권하위치를 수정 하는 경우 , 단 입고시에만,
			**********************************************************/
			if("TY".equals(ydDnWoLocOld.substring(2, 4))  
			   && ("CATF01LM".equals(ydSchCd) || "CBTF01LM".equals(ydSchCd)) ){
			
				JDTORecordSet jsCrnSchWB = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnWekCancelschid", logId, methodNm, "나머지 스케쥴 조회");
				
				for(int Loop_i = 1; Loop_i <= jsCrnSchWB.size(); Loop_i++) {
					jsCrnSchWB.absolute(Loop_i);
			    	recOutTemp = JDTORecordFactory.getInstance().create();
			    	recOutTemp.setRecord(jsCrnSchWB.getRecord());
			    	
			    	String szWbookId   = commUtils.trim(recOutTemp.getFieldString("YD_WBOOK_ID"));
			    	String szCrnSchId 	= commUtils.trim(recOutTemp.getFieldString("YD_CRN_SCH_ID"));
			    	
			    	recInTemp  = JDTORecordFactory.getInstance().create();
			    	recInTemp.setField("YD_WBOOK_ID"  , szWbookId );
			    	recInTemp.setField("YD_CRN_SCH_ID", szCrnSchId);
			    	
			    	this.trtCrnSchCncl(recInTemp);
				}
			}
			
			//적치Bed 수정 - 완산Bed 해제
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnSchDnWoLocStkBed", logId, methodNm, "TB_YS_STKBED");				

			//크레인스케줄 수정 - 권상, 권하지시위치
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnSchDnWoLocCrnSch", logId, methodNm, "TB_YS_CRNSCH");				

			//기존 대차, 차량 권하위치에서 일반야드로 변경 시 대차 or 차량스케줄 작업예약ID 삭제
			ydDnWoLocOld = ydDnWoLocOld.substring(2, 4);
			if (("TC".equals(ydDnWoLocOld) || "TR".equals(ydDnWoLocOld)) && !ydDnWoLocOld.equals(ysDnWoLoc.substring(2, 4))) {
				if ("TC".equals(ydDnWoLocOld)) {
					//대차스케줄 수정 - 상차작업예약ID 삭제
					//작업예약 Table 우선순위 Update
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnSchDnWoLocTCarSch", logId, methodNm, "TB_YS_TCARSCH");				
					
				} else {
					//차량스케줄 수정 - 상차작업예약ID 삭제
					//작업예약 Table 우선순위 Update
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnSchDnWoLocCarSch", logId, methodNm, "TB_YS_CARSCH");				

					//적치열 수정 - 야드적치대용도코드 삭제
					//작업예약 Table 우선순위 Update
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnSchDnWoLocStkCol", logId, methodNm, "TB_YS_STKCOL");				
				}
			}
			
			/**********************************************************
			* 3. 크레인작업지시요구 전문 조회
			**********************************************************/
			//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
			JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, modifier);

			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name			
			jrYdMsg.setField("JMS_TC_CD"       , YsConstant.N2YSL004   );	//크레인작업지시요구
			jrYdMsg.setField("YD_EQP_ID"       , ydEqpId      );	//야드설비ID
			jrYdMsg.setField("YD_WRK_PROG_STAT", ydWrkProgStat);	//야드작업진행상태
			jrYdMsg.setField("YD_SCH_CD"       , ydSchCd      );	//야드스케쥴코드
			jrYdMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId   );	//야드크레인스케쥴ID
			jrYdMsg.setField("MODIFIER"        , modifier     );	//수정자

			EJBConnector sndConn = new EJBConnector("default", "BtYsL2RcvSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)sndConn.trx("rcvN2YSL004", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });

			sndRecord = commUtils.addSndData(sndRecord, jrRtn);
			
			commUtils.printLog(logId, methodNm, "S-");

			return sndRecord;

		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
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
	public JDTORecord updCraneChange(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리 크레인변경[BtYsJspSeEJB.updCraneChange] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

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
			String modifier = commUtils.trim(gdReq.getParam("userid")); //수정자
			
			//DAO Parameter
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			//처리완료한 야드작업예약ID
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String[] arrYdWbookId = new String[rowCnt];
			
			for (int ii = 0; ii < rowCnt; ii++) {
				ydWbookId  = commUtils.trim(gdReq.getHeader("YD_WBOOK_ID"  ).getValue(ii));
				ydCrnSchId = commUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID").getValue(ii));

			    //작업할  야드작업예약ID가 작업 완료한 야드작업예약ID에 있으면 이전에 모두 처리되었으므로 Skip
				if (commUtils.chkExist(arrYdWbookId, ydWbookId)) { continue; }  //해당 값이 있는지를 Check
				
				arrYdWbookId[ii] = ydWbookId;

				/**********************************************************
				* 1. 크레인스케줄, 스케줄기준, 설비정보 Check
				* 1.1 크레인스케줄의 스케줄ID 및 설비상태 Check
				* 1.2 크레인스케줄 설비ID로 스케줄기준의 주 및 대체 크레인설비ID와 비교하여 변경 크레인설비ID와 순위를 Set
				* 1.3 변경 할 크레인 정보를 Check
				**********************************************************/
				jrParam.setField("YD_WBOOK_ID"  , ydWbookId );
				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);
				
				//기본정보조회
				JDTORecordSet jsCrn = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCraneChange1", logId, methodNm, "크레인변경 조회");

			    if (jsCrn == null || jsCrn.size() <= 0) {
					throw new Exception("크레인스케쥴ID[" + ydCrnSchId + "]의 크레인스케줄 정보가 존재하지 않습니다.");
			    }
				
			    JDTORecord jrCrn = jsCrn.getRecord(0);
				
			    ydWrkProgStat   = commUtils.trim(jrCrn.getFieldString("YD_WRK_PROG_STAT"   )); //야드작업진행상태
				ydSchCd         = commUtils.trim(jrCrn.getFieldString("YD_SCH_CD"          )); //야드스케쥴코드
				ydEqpId         = commUtils.trim(jrCrn.getFieldString("YD_EQP_ID"          )); //야드설비ID
				chgYdEqpId      = commUtils.trim(jrCrn.getFieldString("CHG_YD_EQP_ID"      )); //변경 야드설비ID
				chgYdSchPrior   = commUtils.trim(jrCrn.getFieldString("CHG_YD_SCH_PRIOR"   )); //변경 야드스케쥴우선순위
				chgYdEqpStat    = commUtils.trim(jrCrn.getFieldString("CHG_YD_EQP_STAT"    )); //변경 야드설비상태
				chgYdEqpWrkMode = commUtils.trim(jrCrn.getFieldString("CHG_YD_EQP_WRK_MODE")); //변경 야드설비작업Mode

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

				commUtils.printLog(logId, "크레인변경 [ " + ydWbookId + " : " + ydEqpId + " >> " + chgYdEqpId + " ]", "SL");

				/**********************************************************
				* 2. 작업예약 및 크레인스케줄 Table에  대체 크레인ID와 우선순위를 Update
				**********************************************************/
				jrParam.setField("MODIFIER"		, modifier);
				jrParam.setField("YD_SCH_PRIOR"	, chgYdSchPrior);
				jrParam.setField("YD_EQP_ID"   	, chgYdEqpId   );
				
				//작업예약 Table 우선순위 Update
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updWrkBookPrior", logId, methodNm, "TB_YS_WRKBOOK");				
				
				if ("1".equals(ydWrkProgStat)) {
					/**********************************************************
					* 2.1  이전 크레인의 작업지시 취소 전문 송신
					**********************************************************/
					jrParam.setField("MSG_GP"       , "D"       ); //전문구분(취소)
					jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YSN2L003", jrParam));
				}
				
				//크레인스케줄 Table 크레인ID, 우선순위 Update
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnWrkMgtW", logId, methodNm,  "TB_YS_CRNSCH");				
				
			
				/**********************************************************
				* 3. 현 작업상태가 권상지시[1]인 경우
				**********************************************************/
				if ("1".equals(ydWrkProgStat)) {
					/**********************************************************
					* 3.1 변경 크레인의 설비 Table 상태정보를 Update
					**********************************************************/
					jrParam.setField("MODIFIER"		, modifier);
					jrParam.setField("YD_EQP_STAT", "1"); //야드설비상태 : 권상작업지시
					jrParam.setField("YD_EQP_ID"   	, chgYdEqpId   );
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnYsEqp", logId, methodNm,  "TB_YS_EQP");				

					/**********************************************************
					* 3.2 변경 크레인의 크레인작업지시요구 처리
					**********************************************************/
					//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
					JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, modifier);
					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name
					jrYdMsg.setField("JMS_TC_CD"       , YsConstant.N2YSL004);	//크레인작업지시요구
					jrYdMsg.setField("YD_EQP_ID"       , chgYdEqpId);	//야드설비ID
					jrYdMsg.setField("YD_WRK_PROG_STAT", "1"       );	//야드작업진행상태(권상작업지시)
					jrYdMsg.setField("YD_SCH_CD"       , ydSchCd   );	//야드스케쥴코드
					jrYdMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId);	//야드크레인스케쥴ID

					EJBConnector sndConn = new EJBConnector("default", "BtYsL2RcvSeEJB", this);
					JDTORecord jrSnd = (JDTORecord)sndConn.trx("rcvN2YSL004", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });

					jrRtn = commUtils.addSndData(jrRtn, jrSnd);

					/**********************************************************
					* 3.3 이전 크레인의 설비 Table 상태정보를 Update
					**********************************************************/
					jrParam.setField("MODIFIER"		, modifier);
					jrParam.setField("YD_EQP_ID"  	, ydEqpId);
					jrParam.setField("YD_EQP_STAT"	, "W"    ); //야드설비상태 : 권상작업지시
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnYsEqp", logId, methodNm,  "TB_YS_EQP");				
					
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

					jrRtn = commUtils.addSndData(jrRtn, btYsComm.getYSN2L004(resMsg));
					
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
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
	public JDTORecord updPriorChange(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리 순위변경[BtYsJspSeEJB.updPriorChange] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			String ydWbookId  = ""; //야드작업예약ID
			String ydSchPrior = ""; //야드스케쥴우선순위
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			//처리완료한 야드작업예약ID
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String[] arrYdWbookId = new String[rowCnt];
			
			for (int ii = 0; ii < rowCnt; ii++) {
				/**********************************************************
				* 1. 작업예약ID Check
				**********************************************************/
			    ydWbookId  = commUtils.trim(gdReq.getHeader("YD_WBOOK_ID" ).getValue(ii)); //야드작업예약ID
			    ydSchPrior = commUtils.trim(gdReq.getHeader("YD_SCH_PRIOR").getValue(ii)); //야드스케쥴우선순위

			    //작업할  야드작업예약ID가 작업 완료한 야드작업예약ID에 있으면 이전에 모두 처리되었으므로 Skip
				if (commUtils.chkExist(arrYdWbookId, ydWbookId)) { continue; }
				arrYdWbookId[ii] = ydWbookId;

				commUtils.printLog(logId, "우선순위변경 [ " + ydWbookId + " >> " + ydSchPrior + " ]", "SL");

				/**********************************************************
				* 2. 작업예약 및 크레인스케줄 Table에  우선순위를 Update
				**********************************************************/
				jrParam.setField("YD_WBOOK_ID" , ydWbookId );
				jrParam.setField("YD_SCH_PRIOR", ydSchPrior);
				
				//작업예약 Table 우선순위 Update
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updWrkBookPrior", logId, methodNm, "TB_YS_WRKBOOK");				
				
				//크레인스케줄 Table 크레인ID, 우선순위 Update
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnWrkMgt", logId, methodNm,  "TB_YS_CRNSCH");				
		
				
			}

			commUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업관리 긴급작업
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updPriorWrkChange(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리 순위변경[BtYsJspSeEJB.updPriorWrkChange] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			String ydWbookId  = ""; //야드작업예약ID
			String ydSchPrior = ""; //야드스케쥴우선순위
			String ydEqpId = ""; 
			String ydCrnSchId = ""; 
			String ydCrnSchIdWrk = ""; 
			String ydSchCd = ""; 
			
			JDTORecord jrRtn = null;
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			//처리완료한 야드작업예약ID
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String[] arrYdWbookId = new String[rowCnt];
			
			for (int ii = 0; ii < rowCnt; ii++) {
				/**********************************************************
				* 1. 작업예약ID Check
				**********************************************************/
				ydEqpId  	= commUtils.trim(gdReq.getHeader("YD_EQP_ID" ).getValue(ii)); 
			    ydWbookId  	= commUtils.trim(gdReq.getHeader("YD_WBOOK_ID" ).getValue(ii)); //야드작업예약ID
			    ydSchPrior 	= commUtils.trim(gdReq.getHeader("YD_SCH_PRIOR").getValue(ii)); 
			    ydCrnSchId 	= commUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID").getValue(ii));
			    ydSchCd 	= commUtils.trim(gdReq.getHeader("YD_SCH_CD").getValue(ii));

				commUtils.printLog(logId, "긴급작업 [ " + ydEqpId + " >> " + ydWbookId + " >> " + ydSchPrior + " >> " + ydCrnSchId +" >> " + ydSchCd + " ]", "SL");
 
			    //작업할  야드작업예약ID가 작업 완료한 야드작업예약ID에 있으면 이전에 모두 처리되었으므로 Skip
				if (commUtils.chkExist(arrYdWbookId, ydWbookId)) { continue; }
				arrYdWbookId[ii] = ydWbookId;

			    jrParam.setField("YD_EQP_ID"		, ydEqpId );
			    jrParam.setField("YD_CRN_SCH_ID" 	, ydCrnSchId );
				jrParam.setField("YD_WBOOK_ID" 		, ydWbookId );
				jrParam.setField("YD_SCH_PRIOR"		, "0");

				JDTORecordSet jsCrn = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnWrkMgtPriorWrk", logId, methodNm, "크레인변경 조회");

				//작업예약 Table 우선순위 Update
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updWrkBookPrior", logId, methodNm, "TB_YS_WRKBOOK");				

				if (jsCrn == null || jsCrn.size() <= 0) {
					// 기존 작업 우선순위 변경
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnWrkMgt", logId, methodNm,  "TB_YS_CRNSCH");
					
			    } else {
					
				    JDTORecord jrCrn = jsCrn.getRecord(0);
				    ydCrnSchIdWrk   = commUtils.trim(jrCrn.getFieldString("YD_CRN_SCH_ID"   )); 
				    jrParam.setField("YD_CRN_SCH_ID" 	, ydCrnSchIdWrk );
					//크레인스케줄 Table 크레인ID, 우선순위 Update, 
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnWrkMgtPriorWrk", logId, methodNm,  "TB_YS_CRNSCH");				
					
					/**********************************************************
					* 2. 작업진행상태가 [1:권상지시] 이면 작업지시취소 전문 전송
					**********************************************************/
					if (!"".equals(ydCrnSchIdWrk)) {
						jrParam.setField("MSG_GP"       , "D"       ); //전문구분(취소)
	
						jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN2L003", jrParam));
					}
					/**********************************************************
					* 3.2 변경 크레인의 크레인작업지시요구 처리
					**********************************************************/
					//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
					JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name
					jrYdMsg.setField("JMS_TC_CD"       , YsConstant.N2YSL004   );	//크레인작업지시요구
					jrYdMsg.setField("YD_EQP_ID"       , ydEqpId);	//야드설비ID
					jrYdMsg.setField("YD_WRK_PROG_STAT", "4"       );	//야드작업진행상태(권상작업지시)
					jrYdMsg.setField("YD_SCH_CD"       , ydSchCd   );	//야드스케쥴코드
					jrYdMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId);	//야드크레인스케쥴ID

					//rcvYSYSJ001 에서 공장l2 확인함 
					EJBConnector sndConn = new EJBConnector("default", "BtYsL2RcvSeEJB", this);
					JDTORecord jrSnd = (JDTORecord)sndConn.trx("rcvN2YSL004", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });

					jrRtn = commUtils.addSndData(jrRtn, jrSnd);
			    }					
			}

			commUtils.printLog(logId, methodNm, "S-");
			return jrRtn;

		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
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
	public JDTORecord updDownLocChange(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리 권하위치변경[BtYsJspSeEJB.trtCrnWrkMgtDM] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//EJB Call을 위한 Message 생성 - Log ID, Method, 수정자 Set
			JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name			
			
			//권하위치변경 대상 스케줄
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {
				jrYdMsg.setField("YD_EQP_ID"       , commUtils.trim(gdReq.getHeader("YD_EQP_ID"       ).getValue(ii))); //야드설비ID(크레인)
				jrYdMsg.setField("YD_SCH_CD"       , commUtils.trim(gdReq.getHeader("YD_SCH_CD"       ).getValue(ii))); //야드스케쥴코드
				jrYdMsg.setField("YD_CRN_SCH_ID"   , commUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID"   ).getValue(ii))); //야드크레인스케쥴ID
				jrYdMsg.setField("YD_WBOOK_ID"     , commUtils.trim(gdReq.getHeader("YD_WBOOK_ID"     ).getValue(ii))); //야드작업예약ID
				jrYdMsg.setField("YS_DN_WO_LOC"    , commUtils.trim(gdReq.getHeader("YS_DN_WO_LOC"    ).getValue(ii))); //야드권하지시위치(신규)
				jrYdMsg.setField("YD_WRK_PROG_STAT", commUtils.trim(gdReq.getHeader("YD_WRK_PROG_STAT").getValue(ii))); //야드작업진행상태

				//권하지시위치 변경
				jrRtn = commUtils.addSndData(jrRtn, this.updCrnSchDnWoLoc(jrYdMsg));
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
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
	public JDTORecord updCraneWrkCancel(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리 작업취소[BtYsJspSeEJB.updCraneWrkCancel] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			String ydCrnSchId = ""; //야드크레인스케쥴ID
			String ydWbookId  = ""; //야드작업예약ID
		    String ydEqpId    = ""; //야드설비ID
		    String ydSchCd    = ""; //야드스케쥴코드
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			//처리완료한 야드작업예약ID
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String[] arrYdWbookId = new String[rowCnt];
			
			for (int ii = 0; ii < rowCnt; ii++) {
				ydWbookId = commUtils.trim(gdReq.getHeader("YD_WBOOK_ID").getValue(ii));
			    ydEqpId   = commUtils.trim(gdReq.getHeader("YD_EQP_ID"  ).getValue(ii));
			    ydSchCd   = commUtils.trim(gdReq.getHeader("YD_SCH_CD"  ).getValue(ii));

			    //작업할  야드작업예약ID가 작업 완료한 야드작업예약ID에 있으면 이전에 모두 처리되었으므로 Skip
				if (commUtils.chkExist(arrYdWbookId, ydWbookId)) { continue; }
				arrYdWbookId[ii] = ydWbookId;

				//기본정보조회
				jrParam.setField("YD_WBOOK_ID", ydWbookId);

//				JDTORecordSet jsCrn = jspDao.getCrnWrkMgt("WCSch", jrParam);
//              com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getCrnWrkMgtWCSch   
//			    if (jsCrn == null || jsCrn.size() <= 0) {
//					throw new Exception("작업예약ID[" + ydWbookId + "]의 크레인스케줄 정보가 존재하지 않습니다.");
//			    }

				JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnsch", logId, methodNm, "크레인작업지시read");
				if (jsCrnSch == null || jsCrnSch.size() <= 0) {
					throw new Exception("작업예약ID[" + ydWbookId + "]의 크레인스케줄 정보가 존재하지 않습니다.");
			    }
				ydCrnSchId = commUtils.trim(jsCrnSch.getRecord(0).getFieldString("YD_CRN_SCH_ID")); //야드크레인스케쥴ID
				
				commUtils.printLog(logId, "작업취소 [ " + ydWbookId + " - " + ydCrnSchId + " ]", "SL");

				jrParam.setField("YD_WBOOK_ID"  , ydWbookId );
				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);
				jrParam.setField("YD_EQP_ID"    , ydEqpId   );
				jrParam.setField("YD_SCH_CD"    , ydSchCd   );
				
				/**********************************************************
				* 1. 크레인스케줄 취소
				**********************************************************/
				jrRtn = commUtils.addSndData(jrRtn, this.trtCrnSchCncl(jrParam));

				/**********************************************************
				* 2. 작업예약 취소
				**********************************************************/
				jrRtn = commUtils.addSndData(jrRtn, this.trtWrkBookCncl(jrParam));
			}

			/**********************************************************
			* 5. 크레인작업지시요구 전문 조회
			**********************************************************/
			//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
			JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, "delWrkBook");

			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name
			jrYdMsg.setField("JMS_TC_CD"       , YsConstant.N2YSL004);	//크레인작업지시요구
			jrYdMsg.setField("YD_EQP_ID"       , ydEqpId   );	//야드설비ID
			jrYdMsg.setField("YD_WRK_PROG_STAT", "4"       );	//야드작업진행상태(권하완료)
			jrYdMsg.setField("YD_SCH_CD"       , ydSchCd   );	//야드스케쥴코드
			jrYdMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId);	//야드크레인스케쥴ID

			EJBConnector sndConn = new EJBConnector("default", "BtYsL2RcvSeEJB", this);
			JDTORecord jrRtn1 = (JDTORecord)sndConn.trx("rcvN2YSL004", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
			
			jrRtn = commUtils.addSndData(jrRtn,jrRtn1);
		
			commUtils.printParam(logId, jrRtn);
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
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
		String methodNm = "크레인스케줄 취소처리[BtYsJspSeEJB.trtCrnSchCncl] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			String ydCrnSchId = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID")); //야드크레인스케쥴ID
			String ydWbookId  = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"  )); //야드작업예약ID
			
			if ("".equals(ydCrnSchId)) {
				throw new Exception("크레인스케쥴ID가 없습니다.");
			} else if ("".equals(ydWbookId)) {
				throw new Exception("작업예약ID가 없습니다.");
			}

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(rcvMsg.getFieldString("MODIFIER")));
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);
			jrParam.setField("YD_WBOOK_ID"  , ydWbookId );
			
			/**********************************************************
			* 1. 크레인스케쥴 정보 Check
			**********************************************************/
//			com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnWrkMgtSCSch
			JDTORecordSet jsCrnSch = BtYsDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnWrkMgtSCSch", logId, methodNm, "크레인작업지시read");
			if (jsCrnSch == null || jsCrnSch.size() <= 0) {
				throw new Exception("크레인스케쥴ID[" + ydCrnSchId + "]의 크레인스케줄 정보가 존재하지 않습니다.");
		    }
			
			JDTORecord jrCrnSch = jsCrnSch.getRecord(0);
			
		    String ydWrkProgStat = commUtils.trim(jrCrnSch.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
		    String eqpUpdYn      = commUtils.trim(jrCrnSch.getFieldString("EQP_UPD_YN"      )); //설비상태수정여부
		    String ydEqpId       = commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"       )); //야드설비ID
		    String ydEqpStat     = commUtils.trim(jrCrnSch.getFieldString("YD_EQP_STAT"     )); //야드설비상태

			if ("2".equals(ydWrkProgStat)) {
				throw new Exception("크레인스케줄 [" + ydCrnSchId + "]의 작업진행상태가 [2:권상완료]이므로 취소하실 수 없습니다.");
			} else if ("3".equals(ydWrkProgStat)) {
				throw new Exception("크레인스케줄 [" + ydCrnSchId + "]의 작업진행상태가 [3:권하지시]이므로 취소하실 수 없습니다.");
			} else if ("4".equals(ydWrkProgStat)) {
				throw new Exception("크레인스케줄 [" + ydCrnSchId + "]의 작업진행상태가 [4:권하완료]이므로 취소하실 수 없습니다.");
			}
			
			/**********************************************************
			* 2. 작업진행상태가 [1:권상지시] 이면 작업지시취소 전문 전송
			**********************************************************/
			if ("1".equals(ydWrkProgStat)) {
				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
				jrParam.setField("MSG_GP"       , "D"       ); //전문구분(취소)

				//크레인작업지시(YDY1L004, YDY3L004) 전문 조회
				String szJMS_TC_CD = "";
//				String szYdGpBay = ydEqpId.substring(0,2);
				
	    		szJMS_TC_CD = "YSN2L003";
				
				jrRtn = commUtils.addSndData(commDao.getMsgL2(szJMS_TC_CD, jrParam));
			}

			/**********************************************************
			* 3. 권상, 권하위치 원복 - 적치단, 적치Bed
			**********************************************************/
			//적치단 수정 - 권상위치(U -> C), 권하위치(D -> E)
//			jspDao.updCrnWrkMgt("SCStkLyr", jrParam); com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updCrnWrkMgtSCStkLyr
       
			//적치단 수정 - 권상위치(U -> C), 권하위치(D -> E)
			BtYsDao.update(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.updCrnWrkMgtSCStkLyr", logId, methodNm, "TB_YS_STKLYR");				
			
			//적치Bed 수정 - 완산Bed 해제
//			jspDao.updCrnWrkMgt("SCStkBed", jrParam); com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updCrnWrkMgtSCStkBed
//			BtYsDao.update(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.updCrnWrkMgtSCStkBed", logId, methodNm, "TB_YS_STKBED");				
			
			/**********************************************************
			* 4. 크레인스케줄 삭제
			**********************************************************/
			//크레인작업재료 삭제
//			jspDao.updCrnWrkMgt("SCCrnMtl", jrParam); com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updCrnWrkMgtSCCrnMtl
			BtYsDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnWrkMgtSCCrnMtl", logId, methodNm, "TB_YS_CRNWRKMTL");				
			
			//크레인스케줄 삭제
//			jspDao.updCrnWrkMgt("SCCrnSch", jrParam); com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.updCrnWrkMgtSCCrnSch
			BtYsDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnWrkMgtSCCrnSch", logId, methodNm, "TB_YS_CRNSCH");				

			// 분리 및 모음 작업시  작업예약MTL은 삭제이나 작업예약 TABLE에 존재 할수 있음
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnWrkBookClear 
			 UPDATE TB_YS_WRKBOOK
			    SET DEL_YN = 'Y'
			  WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
			    AND 0 = (
			      SELECT SUM(DECODE(DEL_YN,'N',1,0)) 
			       FROM TB_YS_WRKBOOKMTL
			      WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
			      )
			*/      
			BtYsDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnWrkBookClear", logId, methodNm, "TB_YS_WRKBOOK");				
			
			
			/**********************************************************
			* 5. 설비상태 수정 - 크레인이 고장 또는 Off-Line이 아니고 상태가 다르면
			**********************************************************/
			if ("Y".equals(eqpUpdYn)) {
				jrParam.setField("YD_EQP_ID"  , ydEqpId  ); //야드설비ID
				jrParam.setField("YD_EQP_STAT", ydEqpStat); //야드설비상태

//				commDao.updStat("Eqp", jrParam); com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.updStatEqp
				BtYsDao.update(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.updStatEqp", logId, methodNm, "TB_YD_EQP");				
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
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
		String methodNm = "작업예약 취소처리[BtYsJspSeEJB.trtWrkBookCncl] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			String ydCrnSchId= commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"  )); //야드설비ID
			String ydWbookId = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID")); //야드작업예약ID
		    String ydEqpId   = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"  )); //야드설비ID
		    String ydSchCd   = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"  )); //야드스케쥴코드
			String modifier  = commUtils.trim(rcvMsg.getFieldString("MODIFIER" )); //수정자
			
			if ("".equals(ydWbookId)) {
				throw new Exception("작업예약ID가 없습니다.");
			}

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, modifier);

			jrParam.setField("YD_WBOOK_ID", ydWbookId);
			
			/**********************************************************
			* 1. 크레인스케줄 존재여부 Check
			**********************************************************/

			JDTORecordSet jsCrnSch = BtYsDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCommWbCrnSch", logId, methodNm, "크레인작업지시read");
			if (jsCrnSch != null && jsCrnSch.size() > 0) {				
				throw new Exception("작업예약ID[" + ydWbookId + "]의 크레인스케줄 정보가 " + jsCrnSch.size() + " 건 존재합니다.");
		    }
			
			/**********************************************************
			* 2. 준비스케줄 복원
			**********************************************************/
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.updCommPrepMtlRcvr 
			--준비재료 복원 - 
			UPDATE TB_YS_PREPMTL
			   SET MODIFIER = :V_MODIFIER
			      ,MOD_DDTT = SYSDATE
			      ,DEL_YN   = 'N'
			 WHERE YD_PREP_SCH_ID IN
			      (SELECT YD_PREP_SCH_ID
			         FROM TB_YS_PREPSCH
			        WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID)
			*/
			BtYsDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCommPrepMtlRcvr", logId, methodNm, "TB_YS_PREPMTL");	

			/* com.inisteel.cim.ys.common.dao.YsCommDAO.updCommPrepSchRcvr 
			--준비스케줄 복원 - 
			UPDATE TB_YS_PREPSCH
			   SET MODIFIER    = :V_MODIFIER
			      ,MOD_DDTT    = SYSDATE
			      ,DEL_YN      = 'N'
			      ,YD_WBOOK_ID = NULL
			 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
			*/ 
			BtYsDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCommPrepSchRcvr", logId, methodNm, "TB_YS_PREPSCH");	
//			//준비스케줄 복원

			/**********************************************************
			* 3. 차량/대차스케줄 야드상차작업예약ID, 야드하차작업예약ID 삭제
			**********************************************************/
			//차량스케줄 야드상차작업예약ID, 야드하차작업예약ID 삭제
			BtYsDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCommCarSchWbDel", logId, methodNm, "TB_YS_CARSCH");				
		
			//대차스케줄 야드상차작업예약ID, 야드하차작업예약ID 삭제
			BtYsDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCommTcarSchWbDel", logId, methodNm, "TB_YS_TCARSCH");				

		    /**********************************************************
			* 4. 작업예약/재료 삭제
			**********************************************************/
			//작업예약재료 삭제
			BtYsDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnWrkBookMtl", logId, methodNm, "TB_YS_WRKBOOKMTL");				

			//작업예약 삭제
			BtYsDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnWrkBook", logId, methodNm, "TB_YS_WRKBOOK");				
			
//			/**********************************************************
//			* 5. 크레인작업지시요구 전문 조회
//			**********************************************************/
//			//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
//			JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, modifier);
//			jrYdMsg.setResultCode(logId);	//Log ID
//			jrYdMsg.setResultMsg(methodNm);	//Log Method Name
//			jrYdMsg.setField("JMS_TC_CD"       , YsConstant.N2YSL004);	//크레인작업지시요구
//			jrYdMsg.setField("YD_EQP_ID"       , ydEqpId   );	//야드설비ID
//			jrYdMsg.setField("YD_WRK_PROG_STAT", "4"       );	//야드작업진행상태(권하완료)
//			jrYdMsg.setField("YD_SCH_CD"       , ydSchCd   );	//야드스케쥴코드
//			jrYdMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId);	//야드크레인스케쥴ID
//			
//			EJBConnector sndConn = new EJBConnector("default", "BtYsL2RcvSeEJB", this);
//			jrRtn = (JDTORecord)sndConn.trx("rcvN2YSL004", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });


			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	/**
	 * [A] 오퍼레이션명 : 크레인작업관리-스케줄취소
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public JDTORecord updCraneSchCancel(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리 스케줄취소[BtYsJspSeEJB.updCraneSchCancel] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			String ydCrnSchId = ""; //야드크레인스케쥴ID
			String ydWbookId  = ""; //야드작업예약ID
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			//처리완료한 야드작업예약ID
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String[] arrYdWbookId = new String[rowCnt];
			
			for (int ii = 0; ii < rowCnt; ii++) {
				ydWbookId  = commUtils.trim(gdReq.getHeader("YD_WBOOK_ID"  ).getValue(ii));
				ydCrnSchId = commUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID").getValue(ii));

			    //작업할  야드작업예약ID가 작업 완료한 야드작업예약ID에 있으면 이전에 모두 처리되었으므로 Skip
				if (commUtils.chkExist(arrYdWbookId, ydWbookId)) { continue; }
				arrYdWbookId[ii] = ydWbookId;

				commUtils.printLog(logId, "스케줄취소 [ " + ydWbookId + " - " + ydCrnSchId + " ]", "SL");

				jrParam.setField("YD_WBOOK_ID"  , ydWbookId );
				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);

				/**********************************************************
				* 1. 크레인스케줄 취소
				**********************************************************/
				jrRtn = commUtils.addSndData(jrRtn, this.trtCrnSchCncl(jrParam));
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}	// end of updCraneSchCancel	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업예약관리 스케줄기동
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord procCrnWrkBookStart(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업예약관리 스케줄기동[BtYsJspSeEJB.procCrnWrkBookStart] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_GP"        , commUtils.trim(gdReq.getParam("YD_GP"))); //야드구분
			jrParam.setField("YD_SCH_ST_GP" , "M"                                      ); //야드스케쥴기동구분(Manual)
			jrParam.setField("YD_SCH_REQ_GP", "W"                                      ); //야드스케쥴요청구분(작업예약조회화면)
			
			//작업예약
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				jrParam.setField("YD_WBOOK_ID", commUtils.trim(gdReq.getHeader("YD_WBOOK_ID").getValue(ii))); //야드작업예약ID
				jrParam.setField("YD_SCH_CD"  , commUtils.trim(gdReq.getHeader("YD_SCH_CD"  ).getValue(ii))); //야드스케쥴코드
				jrParam.setField("YD_EQP_ID"  , commUtils.trim(gdReq.getHeader("YD_WRK_CRN" ).getValue(ii))); //야드설비ID
				jrParam.setField("EJB_CALL_YN", "Y"); //EJBCall여부(신 크레인스케줄)

				//크레인스케줄기동 전문
				jrRtn = commUtils.addSndData(jrRtn, btYsComm.getCrnSchMsg(jrParam));
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
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
	public JDTORecord updbtMvStkWrkBook(GridData gdReq) throws DAOException {
		String methodNm = "이적작업예약등록[BtYsJspSeEJB.updbtMvStkWrkBook] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;
			JDTORecordSet jsMsg = JDTORecordFactory.getInstance().createRecordSet("");	//크레인스케줄 정리를 위함

			String stlNos        = commUtils.trim(gdReq.getParam("SSTL_NOS"         )); //재료번호들
			String ysStkColGp    = commUtils.trim(gdReq.getParam("YS_STK_COL_GP"   )); //야드적치열구분(4자리 이상)
			String ydToLocGuide  = commUtils.trim(gdReq.getParam("YD_TO_LOC_GUIDE" )); //야드To위치Guide
			String ydWrkPlanTcar = commUtils.trim(gdReq.getParam("YD_WRK_PLAN_TCAR")); //야드작업계획대차
			String ydWrkCrn		 = commUtils.trim(gdReq.getParam("YD_WRK_CRN")); //야드작업계획대차

			if (ysStkColGp.length() < 4) {
				//혹시 이적 적치열구분 값이 잘못되어 있으면 무조건 01 Span 으로 처리
				ysStkColGp = ysStkColGp.substring(0, 2) + "01";
			} else if (ysStkColGp.length() > 6) {
				ysStkColGp = ysStkColGp.substring(0, 6);
			}

			if ("".equals(stlNos)) {
				throw new Exception("이적 재료번호가 없습니다.");
			} else if ("".equals(ysStkColGp) || ysStkColGp.length() < 4) {
				throw new Exception("Span[" + ysStkColGp + "] 정보가 없습니다.");
			} else if (!"0".equals(ysStkColGp.substring(2, 3)) && !"TY".equals(ysStkColGp.substring(2, 4))) {
				throw new Exception("적치열[" + ysStkColGp + "]에서는 이적 작업예약등록이 불가능합니다.");
			}
			
			/**********************************************************
			* 1. 스케줄코드 설정
			**********************************************************/
			String ydSchCd    = ""; //야드스케쥴코드
			String ydBayGp    = ysStkColGp.substring(1, 2); //야드동구분
			String ydAimBayGp = ""; //야드목표동구분

			if ("".equals(ydToLocGuide)) {
				//위치검색Bed기준 적용
				ydAimBayGp = ysStkColGp.substring(1, 2);
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
				
				String sSpan = ysStkColGp.substring(2, 4);
				
				if("01".equals(sSpan)||"04".equals(sSpan)){
					ydSchCd = ysStkColGp.substring(0, 2) + "YD14MM";
				}else if("02".equals(sSpan)||"05".equals(sSpan)){
					ydSchCd = ysStkColGp.substring(0, 2) + "YD25MM";
				}else if("03".equals(sSpan)||"06".equals(sSpan)){
					ydSchCd = ysStkColGp.substring(0, 2) + "YD36MM";
				}else{
					ydSchCd = ysStkColGp.substring(0, 2) + "YD01MM";
				}
				
				ydWrkPlanTcar = "";
			} else {
				if ("".equals(ydWrkPlanTcar)) {
					throw new Exception("To위치지정 동간이적 대차 정보가 없습니다.");
				}
				ydSchCd = ysStkColGp.substring(0, 2) + ydWrkPlanTcar.substring(2) + "UM";
			}

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			jrParam.setField("SSTL_NOS"      , stlNos    ); //재료번호들
			jrParam.setField("YS_STK_COL_GP", ysStkColGp); //야드적치열구분

			//작업예약 대상재료 조회
			JDTORecordSet jsWbMtl = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getblMvStkWrkBookMtlPp", logId, methodNm, "재료번호로 조회");

			int rowCnt = jsWbMtl.size();

			if (rowCnt <= 0) {
				throw new Exception("이적 재료 정보가 없습니다.");
			}
			
			/**********************************************************
			* 2. 임시 배드 낫본작업 가능 여부 체크 chito 2016.10
			**********************************************************/
			if(ydToLocGuide.length() > 6){
				if("TY".equals(ydToLocGuide.substring(2 , 4))){
					
					jrParam.setField("YS_STK_COL_GP2", ysStkColGp); //야드적치열구분
					
					//이적단의 적치재료 수 조회
					JDTORecordSet jsWbMtlMove = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getblMvStkCNT", logId, methodNm, "이적단의 적치재료 수 조회");
	
					int rowCntMo = jsWbMtlMove.size();
					
					jrParam.setField("YS_STK_COL_GP2" , ydToLocGuide ); //야드To위치Guide
					//임시베드 적치재료 수 조회
					JDTORecordSet jsWbMtlTY = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getblMvStkCNT", logId, methodNm, "임시베드 적치재료 수 조회");
	
					int rowCntTY = jsWbMtlTY.size();
	
					//임시배드 적치재료 수 + 이적 적치재료 수  > 7
					if (rowCntTY + rowCntMo > 7) {
						throw new Exception("이적 단의 재료가 지정한 임시배드 적치공간을 초과 합니다.");
					}
				}
			}
			
			
			/**********************************************************
			* 2. 이적 작업예약 등록
			**********************************************************/
			jrParam.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
			jrParam.setField("YD_AIM_BAY_GP"   , ydAimBayGp   ); //야드목표동구분
			jrParam.setField("YD_TO_LOC_GUIDE" , ydToLocGuide ); //야드To위치Guide
			jrParam.setField("YD_WRK_PLAN_TCAR", ydWrkPlanTcar); //야드작업계획대차
			jrParam.setField("YD_WRK_CRN"	   , ydWrkCrn); //크레인 지정
			
			
			//작업예약등록
			jsMsg.addRecord(this.insMvstkWrkBook(jrParam, jsWbMtl));
			
			/**********************************************************
			* 3. 대차작업이 있으면 공대차출발지시 처리
			**********************************************************/
			if (!"".equals(ydWrkPlanTcar)) {
				//공대차출발지시 처리시 Exception을 발생시키지 않기위해 미리 Check
				String msgTcar = ""; //공대차출발지시 처리 메세지
			 			
				//대차스케쥴정보(공대차출발지시) 조회
				jrParam.setField("YD_EQP_ID", ydWrkPlanTcar);	//야드설비ID(대차)
				//trtNm = "공대차출발지시 조회";
				/* 대차스케줄 공대차출발지시 조회 - com.inisteel.cim.ys.common.dao.YsCommDAO.getTcarSchLevWo 
				SELECT TS.YD_TCAR_SCH_ID
				      ,EQ.YD_EQP_STAT
				      ,EQ.YD_EQP_WRK_MODE
				      ,NVL(EQ.YD_CURR_BAY_GP,WB.YD_BAY_GP) AS YD_CURR_BAY_GP --이동중이면 상차동을 현재동으로
				      ,EQ.YD_HOME_BAY_GP
				      ,NVL(EQ.AUTO_TCAR_SCH_YN,'N') AS AUTO_TCAR_SCH_YN   --자동대차스케줄여부
				      ,WB.YD_WBOOK_ID               AS YD_WBOOK_ID_CURR   --현재 대차스케줄 상차작업예약ID
				      ,WB.YD_BAY_GP                 AS YD_BAY_GP_CURR     --현재 대차스케줄 상차동
				      ,WB.YD_AIM_BAY_GP             AS YD_AIM_BAY_GP_CURR --현재 대차스케줄 하차동
				      ,XB.YD_WBOOK_ID               AS YD_WBOOK_ID_NEXT   --다음 상차작업예약ID
				      ,XB.YD_BAY_GP                 AS YD_BAY_GP_NEXT     --다음 상차동
				      ,XB.YD_AIM_BAY_GP             AS YD_AIM_BAY_GP_NEXT --다음 하차동
				      ,(SELECT CASE WHEN COUNT(*) > 0 THEN 'Y' ELSE 'N' END
				          FROM TB_YS_TCARFTMVMTL TM
				         WHERE TM.YD_TCAR_SCH_ID = TS.YD_TCAR_SCH_ID
				           AND TM.DEL_YN = 'N') AS TC_MTL_YN
				  FROM TB_YS_EQP     EQ
				      ,TB_YS_TCARSCH TS
				      ,TB_YS_WRKBOOK WB
				      ,(SELECT MIN(YD_WBOOK_ID  ) AS YD_WBOOK_ID
				              ,MIN(YD_BAY_GP    ) AS YD_BAY_GP
				              ,MIN(YD_AIM_BAY_GP) AS YD_AIM_BAY_GP
				          FROM (SELECT YD_WBOOK_ID
				                      ,YD_BAY_GP
				                      ,YD_AIM_BAY_GP
				                  FROM TB_YS_WRKBOOK
				                 WHERE YD_WRK_PLAN_TCAR = :V_YD_EQP_ID
				                   AND YD_WBOOK_ID NOT IN
				                      (SELECT NVL(YD_CARLD_WRK_BOOK_ID,YD_CARUD_WRK_BOOK_ID) AS YD_WBOOK_ID
				                         FROM TB_YS_TCARSCH
				                        WHERE DEL_YN = 'N'
				                          AND (YD_CARLD_WRK_BOOK_ID IS NOT NULL	OR YD_CARUD_WRK_BOOK_ID IS NOT NULL))
				                   AND YD_SCH_CD LIKE '__TC__U%'
				                   AND DEL_YN = 'N'
				                 ORDER BY YD_SCH_PRIOR, YD_WBOOK_ID)
				         WHERE ROWNUM = 1) XB
				 WHERE EQ.YD_EQP_ID            = TS.YD_EQP_ID(+)
				   AND 'N'                     = TS.DEL_YN(+)
				   AND TS.YD_CARLD_WRK_BOOK_ID = WB.YD_WBOOK_ID(+)
				   AND 'N'                     = WB.DEL_YN(+)
				   AND EQ.YD_EQP_ID            = :V_YD_EQP_ID
				  */ 
				JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getTcarSchLevWo", logId, methodNm, "공대차출발지시 조회");
				
				if (jsChk != null && jsChk.size() > 0) {
					JDTORecord jrChk = jsChk.getRecord(0);

					String ydTcarSchId   = commUtils.trim(jrChk.getFieldString("YD_TCAR_SCH_ID"  ));
					String ydWbookIdCurr = commUtils.trim(jrChk.getFieldString("YD_WBOOK_ID_CURR"));

					if ("B".equals(commUtils.trim(jrChk.getFieldString("YD_EQP_STAT")))) {
						msgTcar = "고장";
					} else if (!"1".equals(commUtils.trim(jrChk.getFieldString("YD_EQP_WRK_MODE")))) {
						msgTcar = "Off-Line";
					} else if ("Y".equals(commUtils.trim(jrChk.getFieldString("TC_MTL_YN")))) {
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

//					jrRtn = btYsComm.trtTcarSchLevWo(jrParam);
				} else {
					commUtils.printLog(logId, "대차[" + ydWrkPlanTcar + "] 공대차출발지시 불가 : " + msgTcar, "SL");
				}
			}

			/**********************************************************
			* 4. 동내이적(대차작업이 없음)작업 크레인별 첫번째 스케줄 전송
			**********************************************************/
			jrRtn = commUtils.addSndData(jrRtn, this.setCrnSchMsg(jsMsg, logId, methodNm));
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
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
		String methodNm = "이적작업예약등록[BtYsJspSeEJB.insMvstkWrkBook] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			String ydSchCd       = commUtils.trim(jrParam.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
			String ydAimBayGp    = commUtils.trim(jrParam.getFieldString("YD_AIM_BAY_GP"   )); //야드목표동구분
			String ydToLocGuide  = commUtils.trim(jrParam.getFieldString("YD_TO_LOC_GUIDE" )); //야드To위치Guide
			String ydWrkPlanTcar = commUtils.trim(jrParam.getFieldString("YD_WRK_PLAN_TCAR")); //야드작업계획대차
			String modifier      = commUtils.trim(jrParam.getFieldString("MODIFIER"        )); //수정자
			String ydWrkCrn      = commUtils.trim(jrParam.getFieldString("YD_WRK_CRN"        )); //지정크레인
			
			/**********************************************************
			* 1. 야드스케쥴코드 Check
			**********************************************************/
			JDTORecord jrCrnSpec = btYsComm.chkSchCdEqp(jrParam);
			
			String ydGp       = ydSchCd.substring(0, 1);	//야드구분
			String ydBayGp    = ydSchCd.substring(1, 2);	//야드동구분
			String ydEqpId    = commUtils.trim(jrCrnSpec.getFieldString("YD_EQP_ID"   ));	//야드설비ID(크레인)
			String ydSchPrior = commUtils.trim(jrCrnSpec.getFieldString("YD_SCH_PRIOR"));	//야드스케쥴우선순위
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
			jrCrnSpec.setResultCode(logId);	//Log ID
			jrCrnSpec.setResultMsg(methodNm);	//Log Method Name
			Vector vcLot = this.setCrnSpecSpr(jrCrnSpec, jsWbMtl);

			JDTORecordSet jsLotMtl = null;
			int lotCnt   = vcLot.size();	//크레인사양 분리 작업예약수
			int lotMtlSh = 0;				//작업예약재료매수
			String ydWbookId = "";			//야드작업예약ID
			String ydWbookIdFst = "";		//야드작업예약ID(첫번째)
			JDTORecord jrRow = null;
			commUtils.printLog(logId, "lotCnt:"+lotCnt, "SL");
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
				
				//작업예약 등록
				jrParam.setField("YD_WBOOK_ID"       , ydWbookId     ); //야드작업예약ID
				jrParam.setField("MODIFIER"          , modifier      ); //수정자
				jrParam.setField("YD_GP"             , ydGp          ); //야드구분
				jrParam.setField("YD_BAY_GP"         , ydBayGp       ); //야드동구분
				jrParam.setField("YD_SCH_CD"         , ydSchCd       ); //야드스케쥴코드
				jrParam.setField("YD_SCH_PRIOR"      , ydSchPrior    ); //야드스케쥴우선순위
				jrParam.setField("YD_SCH_PROG_STAT"  , "W"           ); //야드스케쥴진행상태(스케줄수행대기)
				jrParam.setField("YD_SCH_ST_GP"      , "M"           ); //야드스케쥴기동구분(Manual)
				jrParam.setField("YD_SCH_REQ_GP"     , "M"           ); //야드스케쥴요청구분(이적)
				jrParam.setField("YD_AIM_YD_GP"      , ydGp          ); //야드목표야드구분
				jrParam.setField("YD_AIM_BAY_GP"     , ydAimBayGp    ); //야드목표동구분
				jrParam.setField("YD_TO_LOC_DCSN_MTD", ydToLocDcsnMtd); //야드TO위치결정방법
				jrParam.setField("YD_TO_LOC_GUIDE"   , ydToLocGuide  ); //야드To위치Guide
				jrParam.setField("YD_WRK_PLAN_TCAR"  , ydWrkPlanTcar ); //야드작업계획대차
				jrParam.setField("YD_WRK_PLAN_CRN"  , ydWrkCrn ); //야드작업계획크레인
				
				commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBook", logId, methodNm, "TB_YS_WRKBOOK");

				//작업예약재료 등록
	//			String[][] wmParam = new String[lotMtlSh][8];
				
				for (int jj = 0; jj < lotMtlSh; jj++) {
					jrRow = jsLotMtl.getRecord(jj);
					
					JDTORecord jrRtn1 = JDTORecordFactory.getInstance().create();
					
					jrRtn1.setField("YD_WBOOK_ID"   , ydWbookId     ); //야드작업예약ID
					jrRtn1.setField("SSTL_NO"       	, commUtils.trim(jrRow.getFieldString("SSTL_NO"       )));	//재료번호
					jrRtn1.setField("YS_STK_COL_GP" , commUtils.trim(jrRow.getFieldString("YS_STK_COL_GP")));	//야드적치열구분
					jrRtn1.setField("YS_STK_BED_NO" , commUtils.trim(jrRow.getFieldString("YS_STK_BED_NO")));	//야드적치Bed번호
					jrRtn1.setField("YS_STK_LYR_NO" , commUtils.trim(jrRow.getFieldString("YS_STK_LYR_NO")));	//야드적치단번호
//					jrRtn1.setField("YS_STK_SEQ_NO" , ydWbookId     ); //야드작업예약ID
					jrRtn1.setField("MODIFIER"     	, modifier      );												//등록자
					commDao.insert(jrRtn1, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBookMtl", logId, methodNm, "TB_YS_WRKBOOKMTL");
				}
			}		
//			int lotMtlSh = jsWbMtl.size();				//작업예약재료매수
//			String ydWbookId = "";			//야드작업예약ID
//			String ydWbookIdFst = "";		//야드작업예약ID(첫번째)
//			JDTORecord jrRow = null;
//
//			/**********************************************************
//			* 3. 작업예약 등록
//			**********************************************************/
//
//			//작업예약ID 조회
//			//com.inisteel.cim.ys.common.dao.YsCommDAO.getSeqIdWrkBook
//			ydWbookId = commDao.getSeqId(logId, methodNm, "WrkBook");
//			
//			
//			if ("".equals(ydWbookId)) {
//				throw new Exception("작업예약ID 생성 실패");
//			}
//			
//			
//			//작업예약 등록
//			jrParam.setField("YD_WBOOK_ID"       , ydWbookId     ); //야드작업예약ID
//			jrParam.setField("MODIFIER"          , modifier      ); //수정자
//			jrParam.setField("YD_GP"             , ydGp          ); //야드구분
//			jrParam.setField("YD_BAY_GP"         , ydBayGp       ); //야드동구분
//			jrParam.setField("YD_SCH_CD"         , ydSchCd       ); //야드스케쥴코드
//			jrParam.setField("YD_SCH_PRIOR"      , ydSchPrior    ); //야드스케쥴우선순위
//			jrParam.setField("YD_SCH_PROG_STAT"  , "W"           ); //야드스케쥴진행상태(스케줄수행대기)
//			jrParam.setField("YD_SCH_ST_GP"      , "M"           ); //야드스케쥴기동구분(Manual)
//			jrParam.setField("YD_SCH_REQ_GP"     , "M"           ); //야드스케쥴요청구분(이적)
//			jrParam.setField("YD_AIM_YD_GP"      , ydGp          ); //야드목표야드구분
//			jrParam.setField("YD_AIM_BAY_GP"     , ydAimBayGp    ); //야드목표동구분
//			jrParam.setField("YD_TO_LOC_DCSN_MTD", ydToLocDcsnMtd); //야드TO위치결정방법
//			jrParam.setField("YD_TO_LOC_GUIDE"   , ydToLocGuide  ); //야드To위치Guide
//			jrParam.setField("YD_WRK_PLAN_TCAR"  , ydWrkPlanTcar ); //야드작업계획대차
//
//			//com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBook
////			commDao.insSlabYd("WrkBook", jrParam);
//			commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBook", logId, methodNm, "TB_YS_WRKBOOK");
//			
//			//작업예약재료 등록
//			
//			for (int jj = 0; jj < lotMtlSh; jj++) {
//				jrRow = jsWbMtl.getRecord(jj);
//				JDTORecord jrRtn1 = JDTORecordFactory.getInstance().create();
//				
//				jrRtn1.setField("YD_WBOOK_ID"   , ydWbookId     ); //야드작업예약ID
//				jrRtn1.setField("SSTL_NO"       , commUtils.trim(jrRow.getFieldString("SSTL_NO"       )));	//재료번호
//				jrRtn1.setField("YS_STK_COL_GP" , commUtils.trim(jrRow.getFieldString("YS_STK_COL_GP")));	//야드적치열구분
//				jrRtn1.setField("YS_STK_BED_NO" , commUtils.trim(jrRow.getFieldString("YS_STK_BED_NO")));	//야드적치Bed번호
//				jrRtn1.setField("YS_STK_LYR_NO" , commUtils.trim(jrRow.getFieldString("YS_STK_LYR_NO")));	//야드적치단번호
////				jrRtn1.setField("YS_STK_SEQ_NO" , ydWbookId     ); //야드작업예약ID
//				jrRtn1.setField("MODIFIER"     	, modifier      );												//등록자
//				commDao.insert(jrRtn1, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBookMtl", logId, methodNm, "TB_YS_WRKBOOKMTL");
//			}

			/**********************************************************
			* 4. 크레인스케줄(YDYDJ400) 전송용 기초 전문 생성
			**********************************************************/
			//크레인스케줄 전문 - Log ID, Method, 수정자 Set
			JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, modifier);

			jrYdMsg.setField("YD_WBOOK_ID"     , ydWbookIdFst ); //야드작업예약ID(첫번째꺼만)
			jrYdMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
			jrYdMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
			jrYdMsg.setField("YD_SCH_ST_GP"    , "M"          ); //야드스케쥴기동구분(Manual)
			jrYdMsg.setField("YD_SCH_REQ_GP"   , "M"          ); //야드스케쥴요청구분(이적)
			jrYdMsg.setField("YD_WRK_PLAN_TCAR", ydWrkPlanTcar); //야드작업계획대차(대차상차 크레인스케줄을 전송하지 않기 위해 추가)

			commUtils.printLog(logId, methodNm, "S-");

			return jrYdMsg;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
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
		String methodNm = "크레인스케줄전문정리[BtYsJspSeEJB.setCrnSchMsg] < " + mthdNm;

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn = null;

			if (!commUtils.isEmpty(jsMsg)) {
				String ydEqpId   = ""; //야드설비ID(크레인)
				String ydEqpStat = ""; //야드설비상태
				boolean fstYn = false; //동일크레인에서 첫번째 여부
				//DAO Parameter - Log ID, Method, 수정자 Set
				JDTORecord jrParam = commUtils.getParam(logId, methodNm, "");
				JDTORecord jrRow = null;
				JDTORecordSet jsChk = null;

				int rowCnt = jsMsg.size();

				for (int ii = rowCnt - 1; ii >= 0; ii--) {
					jrRow = jsMsg.getRecord(ii);
					jrRow.setResultCode(logId);	//Log ID
					jrRow.setResultMsg(methodNm);	//Log Method Name	
					
					if ("".equals(commUtils.trim(jrRow.getFieldString("YD_WRK_PLAN_TCAR")))) {
						//야드작업계획대차가 있으면 대차상차 크레인스케줄이므로 전송하지 않음 -> 공대차출발지시로 처리
						fstYn = true;
						ydEqpId = commUtils.trim(jrRow.getFieldString("YD_EQP_ID"));
						
						for (int jj = 0; jj < ii; jj++) {
							if (ydEqpId.equals(jsMsg.getRecord(jj).getFieldString("YD_EQP_ID"))) {
								fstYn = false;
								break;
							}
						}
						
						//동일크레인에서 첫번째 이면
						if (fstYn) {
							//크레인 상태 확인
							jrParam.setField("YD_EQP_ID", ydEqpId); //야드설비ID

							jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStatEqp", logId, methodNm, "설비상태조회");
							
							ydEqpStat = "";

							if (jsChk.size() > 0) {
								ydEqpStat = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_STAT"));
							}

							if ("W".equals(ydEqpStat)) {
								//크레인이 작업대기 상태이면 크레인스케줄 전송
								jrRtn = commUtils.addSndData(jrRtn, btYsComm.getCrnSchMsg(jrRow));
							}
						}
					}
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	/**
	 * IFTest Layout 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updIfTestData(GridData gdReq) throws DAOException {
		String methodNm = "IFTest Layout 변경[BtYsJspSeEJB.updIfTestData] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				jrParam.setField("ITM_VAL"	,commUtils.getValue(gdReq, "ITM_VAL", ii)); 
				jrParam.setField("IF_ID"	,gdReq.getParam("IF_ID")); 
				jrParam.setField("ITM_SEQ"	,commUtils.getValue(gdReq, "ITM_SEQ", ii) ); 
				
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updIfTestData", logId, methodNm, "IFTest 항목값 수정");
								
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updIfTestData
	
	/**
	 * IFTest 전송
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord sndIfTest(GridData gdReq) throws DAOException {
		String methodNm = "IFTest 전송[BtYsJspSeEJB.sndIfTest] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			//레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			String msgId = commUtils.trim(gdReq.getParam("IF_ID")); //IFID
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				jrParam.setField("ITM_VAL"	,commUtils.getValue(gdReq, "ITM_VAL", ii)); 
				jrParam.setField("IF_ID"	,msgId); 
				jrParam.setField("ITM_SEQ"	,commUtils.getValue(gdReq, "ITM_SEQ", ii) ); 
				
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updIfTestData", logId, methodNm, "IFTest 항목값 수정");
			}

			
			String ifMthGp    = msgId.substring(4, 5); //IF방법구분(L:EAI, 기타:JMS)
			String ifMthNm    = null;
			String ifSndRcvGp = "YS".equals(msgId.substring(0, 2)) ? "S" : "R"; //IF송수신구분(송신, 수신)

			//큐에 넣을 데이터를 생성 - Log ID, Method, 수정자 Set
			JDTORecord sndData = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			sndData.setResultCode(logId);	//Log ID
			sndData.setResultMsg(methodNm);	//Log Method Name			
			sndData.setField("JMS_TC_CD"         , msgId                    );
			sndData.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14());
			
			if ("L".equals(ifMthGp) && "S".equals(ifSndRcvGp)) {
				//EAI송신처리 일 경우
				ifMthNm = "sndToEAI";

				//EAI전문 Message
				StringBuffer sbMsg = new StringBuffer();

				for (int ii = 0; ii < rowCnt; ii++) {
					sbMsg = sbMsg.append(gdReq.getHeader("ITM_VAL").getValue(ii));
				}

				sndData.setField("JMS_TC_MESSAGE", sbMsg.toString());
			} else {
				//수신 처리방법(Q:JMS Queue, E:EJB Call)이 'E'이고 수신처가 야드이면
				if ("E".equals(gdReq.getParam("TRT_MTH")) && "YS".equals(msgId.substring(2, 4))) {
					ifMthNm = "rcvInterface";	//EJB Call
				} else {
					ifMthNm = "sndToJMS";		//JMS송신
				}

				//EAI송신 외 처리 일 경우
				for (int ii = 0; ii < rowCnt; ii++) {
					sndData.setField(commUtils.trim(gdReq.getHeader("ITM_ID" ).getValue(ii)), commUtils.trim(gdReq.getHeader("ITM_VAL").getValue(ii)));
				}
			}
			
			//송신 공통 EJB를 이용하여 전송
			EJBConnector ejbConn = new EJBConnector("default", "YsCommEJB", this);
			ejbConn.trx(ifMthNm, new Class[] { JDTORecord.class }, new Object[] { sndData });
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of sndIfTest
	
	/**
	 * IFTest EAI전송
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public GridData sndIfTestEAI(GridData gdReq) throws DAOException {
		String methodNm = "IFTest EAI전송[BtYsJspSeEJB.sndIfTestEAI] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

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
					JDTORecord sndData = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
					sndData.setResultCode(logId);	//Log ID
					sndData.setResultMsg(methodNm);	//Log Method Name
					//EAI송신처리 일 경우
					sndData.setField("JMS_TC_CD"         , tcMsg.substring(0, 8));
					sndData.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14());
					sndData.setField("JMS_TC_MESSAGE"    , tcMsg);

					//송신 공통 EJB를 이용하여 L2로 전송
					EJBConnector ejbConn = new EJBConnector("default", "YsCommEJB", this);
					ejbConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { sndData });

					sndCnt++;
				}
			}

			gdReq.addParam("SND_CNT", String.valueOf(sndCnt));
			
			commUtils.printLog(logId, methodNm, "S-");

			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of sndIfTestEAI
	
	/**
	 * 저장위치 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord updStrLocMod(GridData gdReq) throws DAOException {
		String methodNm = "저장위치 수정[BtYsJspSeEJB.updStrLocMod] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;
			JDTORecordSet jsTemp = null;
			JDTORecord jrTemp = null;
			
			String szStlNo = null;
			String szYsStkColGp = null;
			String szYsStkBedNo = null;
			String szYsStkLyrNo = null;
			String szYsStkSeqNo = null;
			
			String szOldStlNo = null;
			String szOldYsStkColGp = null;
			String szOldYsStkBedNo = null;
			String szOldYsStkLyrNo = null;
			String szOldYsStkSeqNo = null;
			
			String szFromStlNo = null;
			String szFromYsStkColGp = null;
			String szFromYsStkBedNo = null;
			String szFromYsStkLyrNo = null;
			String szFromYsStkSeqNo = null;
			
			String szStkStlNo = null;
			String szCurrProgCd = null;
			String szOrdYeojaeGp = null;
			String szWbookId = null;
			String szCrnSchId = null;
			String szToLocMtlStat = null;
			
			String szModGp = null; //작업구분
			String szFtmvCarudCmplYn = null; //이송하차완료처리

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			
			//이송하차완료처리
			szFtmvCarudCmplYn = commUtils.trim(gdReq.getParam("FTMV_CARUD_CMPL_YN"));
			
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				szStlNo 			= commUtils.getValue(gdReq, "SSTL_NO", ii);
				szYsStkColGp 		= commUtils.getValue(gdReq, "YS_STK_COL_GP", ii);
				szYsStkBedNo 		= commUtils.getValue(gdReq, "YS_STK_BED_NO", ii);
				szYsStkLyrNo 		= commUtils.getValue(gdReq, "YS_STK_LYR_NO", ii);
				szYsStkSeqNo 		= commUtils.getValue(gdReq, "YS_STK_SEQ_NO", ii);
				
				szOldStlNo 			= commUtils.getValue(gdReq, "OLD_SSTL_NO", ii);
				szOldYsStkColGp 	= commUtils.getValue(gdReq, "OLD_YS_STK_COL_GP", ii);
				szOldYsStkBedNo 	= commUtils.getValue(gdReq, "OLD_YS_STK_BED_NO", ii);
				szOldYsStkLyrNo 	= commUtils.getValue(gdReq, "OLD_YS_STK_LYR_NO", ii);
				szOldYsStkSeqNo 	= commUtils.getValue(gdReq, "OLD_YS_STK_SEQ_NO", ii);
				
				szFromStlNo			= commUtils.getValue(gdReq, "FROM_SSTL_NO", ii);
				szFromYsStkColGp 	= commUtils.getValue(gdReq, "FROM_YS_STK_COL_GP", ii);
				szFromYsStkBedNo 	= commUtils.getValue(gdReq, "FROM_YS_STK_BED_NO", ii);
				szFromYsStkLyrNo 	= commUtils.getValue(gdReq, "FROM_YS_STK_LYR_NO", ii);
				szFromYsStkSeqNo 	= commUtils.getValue(gdReq, "FROM_YS_STK_SEQ_NO", ii);
				
				if(szStlNo.equals(szOldStlNo)
						&& szYsStkColGp.equals(szOldYsStkColGp)
						&& szYsStkBedNo.equals(szOldYsStkBedNo)
						&& szYsStkLyrNo.equals(szOldYsStkLyrNo)
						&& szYsStkSeqNo.equals(szOldYsStkSeqNo)) {
					//변경사항이 없음으로 Skip 한다.
					continue;
				}
				
				if("".equals(szStlNo) && !"".equals(szOldStlNo) ) {
					//삭제처리
					szModGp = "DELETE";
					jrParam.setField("SSTL_NO"	, szOldStlNo );
				} 
				
				if(!"".equals(szStlNo) && "".equals(szOldStlNo) && "".equals(szFromStlNo)) {
					//추가처리
					szModGp = "ADD";
					jrParam.setField("SSTL_NO"	, szStlNo );
				} 
				
				if(!"".equals(szStlNo) && "".equals(szOldStlNo) && szStlNo.equals(szFromStlNo)) {
					//이동처리
					szModGp = "MOVE";
					jrParam.setField("SSTL_NO"	, szStlNo );
				} 
			
				if(!szYsStkColGp.equals(szOldYsStkColGp)
						|| !szYsStkBedNo.equals(szOldYsStkBedNo)
						|| !szYsStkLyrNo.equals(szOldYsStkLyrNo)
						|| !szYsStkSeqNo.equals(szOldYsStkSeqNo)
						) {
					//SEQ변경처리 UP,DOWN
					szModGp = "UPDOWN";
					jrParam.setField("SSTL_NO"	, szStlNo );
				}
				
				jrParam.setField("YS_STK_COL_GP"	, szYsStkColGp );
				jrParam.setField("YS_STK_BED_NO"	, szYsStkBedNo );
				jrParam.setField("YS_STK_LYR_NO"	, szYsStkLyrNo );
				jrParam.setField("YS_STK_SEQ_NO"	, szYsStkSeqNo );
				
				//제품번호가 야드저장품에 존재하는지, 작업예약과 크레인스케줄에 잡혀있는 대상인지를 가져오고 BILLET공통에서 현재진도코드를 가져온다. 
				jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.getIsEnableStlNo", logId, methodNm, "제품번호 작업대상여부 조회");
				
				if (jsTemp != null && jsTemp.size() > 0) {
					jrTemp = jsTemp.getRecord(0);

					szStkStlNo		= commUtils.trim(jrTemp.getFieldString("SSTL_NO"));
					szCurrProgCd	= commUtils.trim(jrTemp.getFieldString("CURR_PROG_CD"));
					szOrdYeojaeGp	= commUtils.trim(jrTemp.getFieldString("ORD_YEOJAE_GP"));
					szWbookId		= commUtils.trim(jrTemp.getFieldString("YD_WBOOK_ID"));
					szCrnSchId		= commUtils.trim(jrTemp.getFieldString("YD_CRN_SCH_ID"));
					szToLocMtlStat  = commUtils.trim(jrTemp.getFieldString("TOLOC_MTL_STAT"));

			    } else {
					throw new Exception("제품번호로 저장품 및 작업대상여부 조회시 에러가 발생했습니다!");
			    }				
				
				//저장품에 존재하는 제품번호인지 체크
				if("ADD".equals(szModGp)||"MOVE".equals(szModGp)) {
					if("".equals(szStkStlNo)) {
						throw new Exception("제품번호 : "+jrParam.getFieldString("SSTL_NO")+" 가 TB_YS_STOCK 에 없습니다.");
					}
				}

				//------------------------------------------------------------------------------------------
				if("ADD".equals(szModGp)) {
					
					//SSTL_NO로 저장위치 조회하여 FROM위치가 존재하면 그 위치에서 SSTL_NO를 Clear 한다.
					jrParam.setField("SSTL_NO", szStlNo);
					jrParam.setField("YD_GP",   gdReq.getParam("YD_GP"));
					
					JDTORecordSet jsStkLyrStlNo = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStrLocRegPda", logId, methodNm, "재료번호로 조회");
					
					if(jsStkLyrStlNo.size() > 0) {
					
						String sFromLoc = null;
						
						for (int mm = 0; mm < jsStkLyrStlNo.size(); mm++) {
							if(!"".equals(jsStkLyrStlNo.getRecord(mm).getFieldString("YD_CRN_SCH_ID"))) {
								//크레인스케줄 편성 대상이면 에러 메세지를 리턴하고 종료한다.
						
								sFromLoc = jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_COL_GP") + "-" 
										 + jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_BED_NO") + "-"
										 + jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_LYR_NO") + "-"
										 + jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_SEQ_NO");
								
								
								throw new Exception("재료번호: "+ jsStkLyrStlNo.getRecord(mm).getFieldString("SSTL_NO") +  
										" 는 FROM 위치("+sFromLoc+")에서  크레인스케줄에 편성되어 있습니다. 등록 작업을 할 수 없습니다.");
							} else {
								//작업이력에 남길 From 위치설정를 읽어 온다. 
								szFromYsStkColGp = jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_COL_GP");
								szFromYsStkBedNo = jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_BED_NO");
								szFromYsStkLyrNo = jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_LYR_NO");
								szFromYsStkSeqNo = jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_SEQ_NO");
							}
						}
					}
				}
				//------------------------------------------------------------------------------------------
				
				//SSTL_NO 로 STKLYR 'C','U','D' 모두 Clear 하기
				jrParam.setField("SSTL_NO", szStlNo);
				jrParam.setField("YD_GP",   gdReq.getParam("YD_GP"));
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.clearStkLyr", logId, methodNm, "모든 SSTL_NO가 있던 위치 Clear");	
				
				if("UPDOWN".equals(szModGp)) {
					//UP,DOWN 키를 눌러 SEQ 가 변경되었다면 해당 야드맵의 적치단재료상태를 재료번호가 있으면 적치중으로 없으면 적치가능으로 설정한다.
					
					jrParam.setField("SSTL_NO"					, szStlNo); //szStlNo 에는 삭제일경우 빈값이 들어있고 그외는 제품번호가 들어 있다.
					jrParam.setField("YD_STK_LYR_ACT_STAT"		, ""); // "" 값은 이전값을 변경안한다는 의미
					if("".equals(szStlNo)) {
						jrParam.setField("YD_STK_LYR_MTL_STAT"	, "E"); //적치가능
					} else {
						jrParam.setField("YD_STK_LYR_MTL_STAT"	, "C"); //적치중
					}
					jrParam.setField("YS_STK_COL_GP"			, szYsStkColGp );
					jrParam.setField("YS_STK_BED_NO"			, szYsStkBedNo );
					jrParam.setField("YS_STK_LYR_NO"			, szYsStkLyrNo );
					jrParam.setField("YS_STK_SEQ_NO"			, szYsStkSeqNo );
					
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyr", logId, methodNm, "적치단정보 수정");
					
				} else {
					
					//To 위치 적치단 정보 수정
					jrParam.setField("SSTL_NO"					, szStlNo); //szStlNo 에는 삭제일경우 빈값이 들어있고 그외는 제품번호가 들어 있다.
					jrParam.setField("YD_STK_LYR_ACT_STAT"		, ""); // "" 값은 이전값을 변경안한다는 의미
					if("DELETE".equals(szModGp)) {
						jrParam.setField("YD_STK_LYR_MTL_STAT"	, "E"); //적치가능
					} else {
						jrParam.setField("YD_STK_LYR_MTL_STAT"	, "C"); //적치중
					}
					jrParam.setField("YS_STK_COL_GP"			, szYsStkColGp );
					jrParam.setField("YS_STK_BED_NO"			, szYsStkBedNo );
					jrParam.setField("YS_STK_LYR_NO"			, szYsStkLyrNo );
					jrParam.setField("YS_STK_SEQ_NO"			, szYsStkSeqNo );
					
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyr", logId, methodNm, "적치단정보 수정");
				}
				
				//BILLET공통 위치정보 수정하기
				if("DELETE".equals(szModGp)) {
					//삭제일경우 처리 ???
					jrParam.setField("FNL_REG_PGM"			, "btStrLocModjm" );
					jrParam.setField("YD_GP"				, "_" );
					jrParam.setField("YD_BAY_GP"			, szYsStkColGp.substring(1,2) );
					jrParam.setField("YD_EQP_GP"			, szYsStkColGp.substring(2,4) );
					jrParam.setField("YS_STK_COL_NO"		, szYsStkColGp.substring(4,6) );
					jrParam.setField("YS_STK_BED_NO"		, szYsStkBedNo );
					jrParam.setField("YS_STK_LYR_NO"		, szYsStkLyrNo );
					jrParam.setField("YS_STK_SEQ_NO"		, szYsStkSeqNo );
					jrParam.setField("YS_STR_LOC"			, "_" + szYsStkColGp.substring(1,6) + szYsStkBedNo + szYsStkLyrNo + szYsStkSeqNo );
					jrParam.setField("SSTL_NO"				, szOldStlNo ); //삭제된 번호
					
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBtCommYsStrLoc", logId, methodNm, "BILLET공통 야드저장위치 수정");
				} else {
					//추가, 이동
					jrParam.setField("FNL_REG_PGM"			, "btStrLocModjm" );
					jrParam.setField("YD_GP"				, szYsStkColGp.substring(0,1) );
					jrParam.setField("YD_BAY_GP"			, szYsStkColGp.substring(1,2) );
					jrParam.setField("YD_EQP_GP"			, szYsStkColGp.substring(2,4) );
					jrParam.setField("YS_STK_COL_NO"		, szYsStkColGp.substring(4,6) );
					jrParam.setField("YS_STK_BED_NO"		, szYsStkBedNo );
					jrParam.setField("YS_STK_LYR_NO"		, szYsStkLyrNo );
					jrParam.setField("YS_STK_SEQ_NO"		, szYsStkSeqNo );
					jrParam.setField("YS_STR_LOC"			, szYsStkColGp + szYsStkBedNo + szYsStkLyrNo + szYsStkSeqNo );
					jrParam.setField("SSTL_NO"				, szStlNo );
					
					if("Y".equals(szFtmvCarudCmplYn)) {

						//이송하차완료 처리
						if("1".equals(szOrdYeojaeGp)) { //주문재인경우 
							jrParam.setField("CURR_PROG_CD"		, "B" );
						} else if("2".equals(szOrdYeojaeGp)) { //여재인경우
							jrParam.setField("CURR_PROG_CD"		, "Y" );
						} else {
							jrParam.setField("CURR_PROG_CD"		, szCurrProgCd );
						}
						
						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBtCommYsStrLocProgCd", logId, methodNm, "BILLET공통 야드저장위치,진도코드 수정");
						
						//진행관리로 YSPBJ002 전송
						jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSPBJ002", jrParam));
						
						//2)이송지시 테이블 변경
						//       - 완료일자,계상일자,STATUS('*') 변경하기
						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updFtmvCarudCmpl", logId, methodNm, "이송하차완료 처리");
						
					} else {
						if("D".equals(szCurrProgCd) && "1".equals(szOrdYeojaeGp)) {
							//공통의 진도코드가 'D':이송지시대기 이고 주여구분이 '1':주문재 이면  야드저장품의 재료진도코드를 'B':지시대기 로 변경한다. + 위치정보 수정
							jrParam.setField("CURR_PROG_CD"		, "B" );
							commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBtCommYsStrLocProgCd", logId, methodNm, "BILLET공통 야드저장위치,진도코드 수정");
						} else {
							commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBtCommYsStrLoc", logId, methodNm, "BILLET공통 야드저장위치 수정");
						}
					}
				}	
				
				//야드저장품 위치정보 수정하기
				if("DELETE".equals(szModGp)) {
					//삭제일경우 처리 ???
					jrParam.setField("YS_STK_COL_GP"		, "_" + szYsStkColGp.substring(1,6) );
					jrParam.setField("YS_STK_BED_NO"		, szYsStkBedNo );
					jrParam.setField("YS_STK_LYR_NO"		, szYsStkLyrNo );
					jrParam.setField("YS_STK_SEQ_NO"		, szYsStkSeqNo );
					jrParam.setField("YS_STR_LOC"			, "_" + szYsStkColGp.substring(1,6) + szYsStkBedNo + szYsStkLyrNo + szYsStkSeqNo );
					jrParam.setField("SSTL_NO"				, szOldStlNo ); //삭제된 번호
					
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStockYsStrLoc", logId, methodNm, "야드저장품 야드저장위치 수정");
				} else {
					//추가, 이동
					jrParam.setField("YS_STK_COL_GP"		, szYsStkColGp );
					jrParam.setField("YS_STK_BED_NO"		, szYsStkBedNo );
					jrParam.setField("YS_STK_LYR_NO"		, szYsStkLyrNo );
					jrParam.setField("YS_STK_SEQ_NO"		, szYsStkSeqNo );
					jrParam.setField("YS_STR_LOC"			, szYsStkColGp + szYsStkBedNo + szYsStkLyrNo + szYsStkSeqNo );
					jrParam.setField("SSTL_NO"				, szStlNo );
					
					if("D".equals(szCurrProgCd) && "1".equals(szOrdYeojaeGp)) {
						//공통의 진도코드가 'D':이송지시대기 이고 주여구분이 '1':주문재 이면  야드저장품의 재료진도코드를 'B':지시대기 로 변경한다. + 위치정보 수정
						jrParam.setField("STL_PROG_CD"		, "B" );
						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStockYsStrLocProgCd", logId, methodNm, "야드저장품 야드저장위치,재료진도코드 수정");
					} else {
						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStockYsStrLoc", logId, methodNm, "야드저장품 야드저장위치 수정");
					}
				}
				
				//이력정보 등록하기
				if("DELETE".equals(szModGp)) {
					//삭제일경우
					jrParam.setField("SSTL_NO"				, szOldStlNo );
					jrParam.setField("YD_GP"				, szYsStkColGp.substring(0,1) );
					jrParam.setField("YD_SCH_CD"			, szYsStkColGp.substring(0,2)+"YD01MM" );

					jrParam.setField("YS_UP_WR_LOC"			, szYsStkColGp + szYsStkBedNo );
					jrParam.setField("YS_UP_WR_LAYER"		, szYsStkLyrNo );
					jrParam.setField("YS_UP_WR_SEQ_NO"		, szYsStkSeqNo );

					jrParam.setField("YS_DN_WR_LOC"			, "" );
					jrParam.setField("YS_DN_WR_LAYER"		, "" );
					jrParam.setField("YS_DN_WR_SEQ_NO"		, "" );
					
				} else if("ADD".equals(szModGp)) {
					//추가
					jrParam.setField("SSTL_NO"				, szStlNo );
					jrParam.setField("YD_GP"				, szYsStkColGp.substring(0,1) );
					jrParam.setField("YD_SCH_CD"			, szYsStkColGp.substring(0,2)+"YD01MM" );
					
					jrParam.setField("YS_UP_WR_LOC"			, szFromYsStkColGp + szFromYsStkBedNo );
					jrParam.setField("YS_UP_WR_LAYER"		, szFromYsStkLyrNo );
					jrParam.setField("YS_UP_WR_SEQ_NO"		, szFromYsStkSeqNo );

					jrParam.setField("YS_DN_WR_LOC"			, szYsStkColGp + szYsStkBedNo );
					jrParam.setField("YS_DN_WR_LAYER"		, szYsStkLyrNo );
					jrParam.setField("YS_DN_WR_SEQ_NO"		, szYsStkSeqNo );
					
				} else if("MOVE".equals(szModGp)) {
					//이동
					jrParam.setField("SSTL_NO"				, szStlNo );
					jrParam.setField("YD_GP"				, szYsStkColGp.substring(0,1) );
					jrParam.setField("YD_SCH_CD"			, szYsStkColGp.substring(0,2)+"YD01MM" );
					
					jrParam.setField("YS_UP_WR_LOC"			, szFromYsStkColGp + szFromYsStkBedNo );
					jrParam.setField("YS_UP_WR_LAYER"		, szFromYsStkLyrNo );
					jrParam.setField("YS_UP_WR_SEQ_NO"		, szFromYsStkSeqNo );

					jrParam.setField("YS_DN_WR_LOC"			, szYsStkColGp + szYsStkBedNo );
					jrParam.setField("YS_DN_WR_LAYER"		, szYsStkLyrNo );
					jrParam.setField("YS_DN_WR_SEQ_NO"		, szYsStkSeqNo );
					
				} else {
					//UPDOWN
					jrParam.setField("SSTL_NO"				, szStlNo );
					jrParam.setField("YD_GP"				, szYsStkColGp.substring(0,1) );
					jrParam.setField("YD_SCH_CD"			, szYsStkColGp.substring(0,2)+"YD01MM" );
					
					jrParam.setField("YS_UP_WR_LOC"			, szOldYsStkColGp + szOldYsStkBedNo );
					jrParam.setField("YS_UP_WR_LAYER"		, szOldYsStkLyrNo );
					jrParam.setField("YS_UP_WR_SEQ_NO"		, szOldYsStkSeqNo );

					jrParam.setField("YS_DN_WR_LOC"			, szYsStkColGp + szYsStkBedNo );
					jrParam.setField("YS_DN_WR_LAYER"		, szYsStkLyrNo );
					jrParam.setField("YS_DN_WR_SEQ_NO"		, szYsStkSeqNo );
				}
				jrParam.setField("YD_SCH_ST_GP"				, "B" ); // 야드스케줄 기동 구분 "B" 로 넣어준다. B:작업자 Backup
				jrParam.setField("YD_AID_WRK_YN"			, "N" ); // 야드보조작업여부 - N:주작업
				
				
				commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkHistByJsp", logId, methodNm, "화면에의한 이력정보 수정");
				
				//L2로 재원정보 전문 전송
				if("DELETE".equals(szModGp)) {
					jrParam.setField("YD_INFO_SYNC_CD", "D"); //야드정보동기화코드 D:생산종료(삭제)
				} else if("ADD".equals(szModGp)) {
					jrParam.setField("YD_INFO_SYNC_CD", "A"); //야드정보동기화코드 A:생산실적
				} else {
					jrParam.setField("YD_INFO_SYNC_CD", "5"); //야드정보동기화코드 5:지정저장품
				}
				
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN2L002", jrParam));
				
				//TO위치가 장입대(TZ)이면 생산통제 소형압연장입진행실적 (YSCUJ032) 전송
				if("ADD".equals(szModGp) || "MOVE".equals(szModGp)) {
					
					if("TZ".equals(szYsStkColGp.substring(2,4)) && "01".equals(szYsStkBedNo)) {
					
						jrParam.setField("CHG_SUP_PROG_STAT", "30"); 
						jrParam.setField("SSTL_NO", szStlNo);
						jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSCUJ032Backup", jrParam));				
					}
				}
				
				//  생산통제 빌렛입고실적(YSCUJ038)
				jrParam.setField("SSTL_NO", szStlNo);
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSCUJ038Backup", jrParam));
				
				//소형압연L2로 장입이상재 CARRY-OUT 완료 실적을 전송
				if( 
						("MOVE".equals(szModGp) && "LB".equals(szFromYsStkColGp.substring(2,4)))
					||	("DELETE".equals(szModGp) && "LB".equals(szYsStkColGp.substring(2,4)))
					) {
					jrParam.setField("SSTL_NO1", "");
					jrParam.setField("SSTL_NO2", "");
					jrParam.setField("SSTL_NO3", "");
					jrParam.setField("SSTL_NO4", "");
					jrParam.setField("SSTL_NO5", "");
					jrParam.setField("SSTL_NO6", "");
					jrParam.setField("SSTL_NO7", "");
					jrParam.setField("SSTL_NO8", "");
					jrParam.setField("SSTL_NO9", "");
					jrParam.setField("SSTL_NO10", "");
					
					jrParam.setField("YD_STK_BED_STL_SH", "1");
					jrParam.setField("YD_EQP_WRK_SH", "1");
					
					if("DELETE".equals(szModGp)) {
						jrParam.setField("SSTL_NO1", szOldStlNo);
						jrParam.setField("YD_STK_COL_GP", szYsStkColGp );
						jrParam.setField("YD_STK_BED_NO", szYsStkBedNo);
					} else {
						jrParam.setField("SSTL_NO1", szStlNo);
						jrParam.setField("YD_STK_COL_GP", szFromYsStkColGp );
						jrParam.setField("YD_STK_BED_NO", szFromYsStkBedNo);
					}
					
					//장입이상재 Carry-out 완료 송신
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSM5L101BackUp", jrParam));
				}

//				String sApplyYnPI1 = commDao.ApplyYnPI("", methodNm, "APPPI1", "*", "*");
				
				/*
				* MES_PI 2022-09-14
				* 이준기 당진공장 내 특수강 이송실적 통계로 송신
				* USRPDA.SP_SS_PD_MATL_FTMV_WR_MAIN('WBV041018','KD01','KDCS','20220711',:v1)					 
				*///PIDEV
				
//				if("Y".equals(sApplyYnPI1)){
					
					String fromLoc = "";
					String toLoc   = "";
					JDTORecord recordSp = null;
					int[] inParamIndex = {1,2,3,4};
					String  currDt      = commUtils.getDateTime14(); //현재일시(yyyyMMddHHmmss)
					String	iniDate     = commUtils.getIniDate(currDt); 
					
					if("DELETE".equals(szModGp) && "C".equals(szYsStkColGp.substring(1,2))) {
						
						commUtils.printLog(logId, "szOldStlNo : "+szOldStlNo, "반입SL");						
						
						fromLoc = "S220";
						toLoc   = "S210";		
						Object[] inParam = {szOldStlNo, fromLoc, toLoc, iniDate};	
						recordSp = commDao.callProcedure(inParam, inParamIndex, "com.inisteel.cim.ys.common.dao.YsCommDao.callProcedure_PIDEV");
						
						commUtils.printLog(logId, "sstlNo : "+szOldStlNo, "반입SL");
						commUtils.printLog(logId, "ydWrkHdsDd : "+iniDate, "반입SL");
						commUtils.printLog(logId, "fromLoc : "+fromLoc, "반입SL");
						commUtils.printLog(logId, "toLoc : "+toLoc, "반입SL");		
						
					} else if("ADD".equals(szModGp) && ("A".equals(szYsStkColGp.substring(1,2)) || "B".equals(szYsStkColGp.substring(1,2)))) {
						
						commUtils.printLog(logId, "szStlNo : "+szStlNo, "입고SL");
						
						fromLoc = "S210";
						toLoc   = "S220";		
						Object[] inParam = {szStlNo, fromLoc, toLoc, iniDate};	
						recordSp = commDao.callProcedure(inParam, inParamIndex, "com.inisteel.cim.ys.common.dao.YsCommDao.callProcedure_PIDEV");
						
						commUtils.printLog(logId, "sstlNo : "+szStlNo, "입고SL");
						commUtils.printLog(logId, "ydWrkHdsDd : "+iniDate, "입고SL");
						commUtils.printLog(logId, "fromLoc : "+fromLoc, "입고SL");
						commUtils.printLog(logId, "toLoc : "+toLoc, "입고SL");						
					}
//				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updStrLocMod
	
	/**
	 * 스케줄기준관리 - 선택복구
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord resetSchRule(GridData gdReq) throws DAOException {
		String methodNm = "스케줄기준관리 - 선택복구[BtYsJspSeEJB.resetSchRule] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				//스케줄기준 수정 
				jrParam.setField("R_CRN_PRIOR1"		, commUtils.getValue(gdReq, "R_CRN_PRIOR1", ii)); 
				jrParam.setField("R_CRN_PRIOR2"		, commUtils.getValue(gdReq, "R_CRN_PRIOR2", ii)); 
				jrParam.setField("YD_SCH_CD"		, commUtils.getValue(gdReq, "YD_SCH_CD", ii)); 
				jrParam.setField("YD_SCH_GP"		, commUtils.trim(gdReq.getParam("YD_SCH_GP")) ); 
				jrParam.setField("YD_CRN_STAT1"		, commUtils.getValue(gdReq, "YD_CRN_STAT1", ii) ); 
				jrParam.setField("YD_CRN_STAT2"		, commUtils.getValue(gdReq, "YD_CRN_STAT2", ii) ); 
				
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.resetSchRule", logId, methodNm, "스케줄기준 선택복구");
								
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of resetSchRule

	/**
	 * 스케줄기준관리 - 전체복구
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord resetAllSchRule(GridData gdReq) throws DAOException {
		String methodNm = "스케줄기준관리 - 전체복구[BtYsJspSeEJB.resetAllSchRule] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			//스케줄기준 수정 
			jrParam.setField("YD_GP"		, commUtils.trim(gdReq.getParam("YD_GP")) ); 
			jrParam.setField("YD_BAY_GP"	, commUtils.trim(gdReq.getParam("YD_BAY_GP")) ); 
			
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.resetAllSchRule", logId, methodNm, "스케줄기준 전체복구");
								

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of resetAllSchRule
	/**
	 * 차량작업 포인트 현황
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord procPntUnit(GridData gdReq) throws DAOException {
		String methodNm = "차량작업 포인트 현황-포인트 사용 등록[BtYsJspSeEJB.procPntUnit] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String szMsg = null;
		String szMethodName = "procPntUnit";
		String szYS_STK_COL_GP		= null;
		String szYD_STK_COL_ACT_STAT= null;
		String szOLD_YD_STK_COL_ACT_STAT= null;
		String szJMS_TC_CD  = null; 
		JDTORecord recInTemp = null;
		JDTORecord recInTemp1 = null;
		boolean isSendable				= true;
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			//Return Value
			JDTORecord recOutTemp = null;
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			JDTORecordSet rsStkCol = JDTORecordFactory.getInstance().createRecordSet("");	
			JDTORecord sndRecord = JDTORecordFactory.getInstance().create();
			
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				szYS_STK_COL_GP 		= commUtils.getValue(gdReq, "YS_STK_COL_GP", ii);
				szYD_STK_COL_ACT_STAT	= commUtils.getValue(gdReq, "YD_STK_COL_ACT_STAT", ii);

    			recOutTemp = JDTORecordFactory.getInstance().create();
    			jrParam.setField("YS_STK_COL_GP", szYS_STK_COL_GP);
    			jrParam.setField("YD_STK_COL_ACT_STAT", szYD_STK_COL_ACT_STAT);
    			jrParam.setField("MODIFIER", 	szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
    	    	
    	    	rsStkCol = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkCol1", logId, methodNm, "적치열 조회"); 
		    	
		    	if (rsStkCol == null || rsStkCol.size() <= 0) {
					szMsg="["+methodNm+"] 적치열 조회 getYdStkcol data not found";
					throw new Exception(szMsg);
				}

		    	rsStkCol.absolute(1);
		    	recOutTemp = JDTORecordFactory.getInstance().create();
		    	recOutTemp.setRecord(rsStkCol.getRecord());

		    	szOLD_YD_STK_COL_ACT_STAT   = commUtils.trim(recOutTemp.getFieldString("YD_STK_COL_ACT_STAT"       )); 
    	    	
    	    	/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkcolByColActStat 
				UPDATE TB_YS_STKCOL
				   SET MOD_DDTT     = SYSDATE             
					 , MODIFIER     = :V_MODIFIER             
					 , YD_STK_COL_ACT_STAT = NVL(:V_YD_STK_COL_ACT_STAT,YD_STK_COL_ACT_STAT)
					 , TRN_EQP_CD   = NVL(:V_TRN_EQP_CD, TRN_EQP_CD)       
					 , CAR_NO       = NVL(:V_CAR_NO, CAR_NO)           
					 , CARD_NO      = NVL(:V_CARD_NO, CARD_NO)              
				     , YD_STKBED_USG_CD = NVL(:V_STKBED_USG_CD,YD_STKBED_USG_CD)
				WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
			   */
		    	commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkcolByColActStat", logId, methodNm, "TB_YS_STKCOL 등록");				    	    	
    	    	
		    	commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdCarpoint", logId, methodNm, "Car-Point 등록");
		    	
		    	/******************************************
		    	 * 포인트 구내 운송 으로 전송처리
		    	 ***************************************/
		    	recInTemp1  = JDTORecordFactory.getInstance().create();
		    	recInTemp1.setResultCode(logId);	//Log ID
		    	recInTemp1.setResultMsg(methodNm);	//Log Method Name
		    	recInTemp1.setField("JMS_TC_CD",		"YSTSJ012");
		    	recInTemp1.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시
		    	recInTemp1.setField("YD_GP", 			szYS_STK_COL_GP.substring(0,1));
		    	recInTemp1.setField("YS_STK_COL_GP", 	szYS_STK_COL_GP);
				
		    	szMsg= "szYD_STK_COL_ACT_STAT: ["+szYD_STK_COL_ACT_STAT+"  szOLD_YD_STK_COL_ACT_STAT: ["+szOLD_YD_STK_COL_ACT_STAT+"] 비교";
				commUtils.printLog(logId, szMsg, "SL");		
				
				
		    	if(szYD_STK_COL_ACT_STAT.equals ("C") 
						|| szYD_STK_COL_ACT_STAT.equals("L")
						|| szYD_STK_COL_ACT_STAT.equals("R")){
		    		
					if( szOLD_YD_STK_COL_ACT_STAT.equals("N")) {			//사용불가
						recInTemp1.setField("PNT_UNIT_CL_GP",	"C");
						sndRecord = commUtils.addSndData(sndRecord,recInTemp1);	

					}else{
						isSendable = false;
					}
				}else if(szYD_STK_COL_ACT_STAT.equals ("N")){
					
					recInTemp1.setField("PNT_UNIT_CL_GP",		"C");
					sndRecord = commUtils.addSndData(sndRecord,recInTemp1);						
				}		    
		    	
		    	if( isSendable ) {
//		    		szYdGp = szYS_STK_COL_GP.substring(0,2);
		    		/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		    		 * 업무기준 : 차량출발시 저장위치 제원 야드L2로 전송
		    		 *** 야드BLOOM:N1,BILLET:N2,선재:N3,봉강:N4,선재자동화:N5,봉강자동화:N6 	
			             YSN1L001 저장위치제원
			             YSN1L002 저장품제원
			             YSN1L003 크레인작업지시
			             YSN1L004 크레인작업실적응답
		    		 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
					szJMS_TC_CD =  "YSN2L001";
					recInTemp  = JDTORecordFactory.getInstance().create();
					recInTemp.setResultCode(logId);	//Log ID
					recInTemp.setResultMsg(methodNm);	//Log Method Name
		    		recInTemp.setField("MSG_ID",    szJMS_TC_CD);
					recInTemp.setField("YD_INFO_SYNC_CD", "3");						//1:동,2:SPAN,3:열,4:BED
					recInTemp.setField("YD_GP", szYS_STK_COL_GP.substring(0, 1));
					recInTemp.setField("YS_STK_COL_GP", szYS_STK_COL_GP);
					
					//전송 Data 생성
					sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2(szJMS_TC_CD, recInTemp));

					szMsg="["+methodNm+"] 포인트 개패시 시 저장위치 제원 야드L2로 전송";
					commUtils.printLog(logId, szMsg, "SL");			    		
		    	}
				
			}

			szMsg="[구내내운송 소재차량Point개폐 전송  성공]";
			commUtils.printLog(logId, szMsg, "SL");
	    	
	    	szMsg = "["+methodNm+"] YS_STK_COL_GP["+szYS_STK_COL_GP+"]의 진행상태["+szYD_STK_COL_ACT_STAT+"] 변경처리함";
			commUtils.printLog(logId, szMsg, "SL");

			commUtils.printLog(logId, methodNm, "S-");

			return sndRecord;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} 
	
	/**
	 * 차량작업 관리- 입동순서 변경처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord procGdsBayInWoSeqChang(GridData gdReq) throws DAOException {
		String methodNm = "차량작업 포인트 현황-입동지시[BtYsJspSeEJB.procGdsBayInWoSeqChang] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		JDTORecord recInTemp = null;
		JDTORecord sndRecord = JDTORecordFactory.getInstance().create();
		int RtnVal = 0;
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			String syd_car_sch_id = null;
			
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
				
			for(int x=0;x<rowCnt;x++){					
				for(int i=1;i<=15;i++){

					syd_car_sch_id = commUtils.getValue(gdReq, "YD_CAR_SCH_ID"+i, x);
					
					if(!syd_car_sch_id.equals("")){
						
						recInTemp = JDTORecordFactory.getInstance().create();
						recInTemp.setField("YD_CAR_SCH_ID"	    ,commUtils.getValue(gdReq, "YD_CAR_SCH_ID"+i, x));
						recInTemp.setField("YD_BAYIN_WO_SEQ"	,commUtils.getValue(gdReq, "YD_BAYIN_WO_SEQ"+i, x));
						recInTemp.setField("MODIFIER"			,commUtils.getValue(gdReq, "YD_USER_ID"+i, x));

						/*com.inisteel.cim.ys.common.dao.YsCommDAO.updBayInWoSeqChang
						UPDATE TB_YS_CARSCH
						   SET MOD_DDTT = SYSDATE
						     , MODIFIER = :V_MODIFIER
						     , YD_BAYIN_WO_SEQ = :V_YD_BAYIN_WO_SEQ
						 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID  
						*/
						RtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBayInWoSeqChang", logId, methodNm, "차량스케쥴 등록");		
						
					}
					if (RtnVal < 0) {
						commUtils.printLog(logId, "차량스케쥴 등록 오류", "SL", gdReq);
					} // end of if
				}	
			}
                         
			commUtils.printLog(logId, methodNm, "S-");

			return sndRecord;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // 
	/**
	 * 차량작업 포인트 현황- 입동지시
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord procBayInWo(GridData gdReq) throws DAOException {
		String methodNm = "차량작업 포인트 현황-입동지시[BtYsJspSeEJB.procBayInWo] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		JDTORecord recInTemp = null;
		JDTORecord sndRecord = JDTORecordFactory.getInstance().create();
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setResultCode(logId);	//Log ID
			recInTemp.setResultMsg(methodNm);	//Log Method Name
			recInTemp.setField("JMS_TC_CD"				,"YSYSJ801");  //차량입동지시 요구 기존:YDYDJ662
			recInTemp.setField("JMS_TC_CREATE_DDTT"		,commUtils.getDateTime14());
				
			for (int ii = 0; ii < rowCnt; ii++) {
				
				commUtils.printLog(logId, commUtils.getValue(gdReq, "YD_CARPNT_CD", ii), "SL", gdReq);
				
				
				recInTemp.setField("YD_CARPNT_CD"	, commUtils.getValue(gdReq, "YD_CARPNT_CD", ii));		//입동포인트
				recInTemp.setField("YD_CAR_STOP_LOC", commUtils.getValue(gdReq, "YS_STK_COL_GP", ii));		//입동포인트
//				recInTemp.setField("YD_CAR_SCH_ID"	, commUtils.getValue(gdReq, "YD_CAR_SCH_ID", ii));	        //차량스케줄ID
				recInTemp.setField("CAR_NO" 		, commUtils.getValue(gdReq, "TRN_EQP_CD", ii));
				sndRecord = commUtils.addSndData(sndRecord,recInTemp);
			}
                         
			commUtils.printLog(logId, methodNm, "S-");

			return sndRecord;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} 
	
	/**
	 * 이송차량스케줄 초기화
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord initMvCarSchMgt(GridData gdReq) throws DAOException {
		String methodNm = "이송차량스케줄 초기화[BtYsJspSeEJB.initMvCarSchMgt] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			String ydCarSchId  		= null;
			String trnEqpCd    		= null;
			String ysStkColGp 		= null;  
			String ydCarpntCd 		= null;  
			
			String WLOC_CD			= null;
			String YD_PNT_CD		= null;
			
	    	int				intLevLocGp     	    = 0;
	    	int 			intRtnVal				= 0;
	    	String			szMsg					= null;
	    	String 			szYD_CARLD_STOP_LOC		= null;
	    	
			JDTORecordSet 	rsStkCol 				= JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord 		recInTemp 				= JDTORecordFactory.getInstance().create();
	    	JDTORecord		recOutTemp				= JDTORecordFactory.getInstance().create();
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			
			String ydGp		 	= commUtils.trim(gdReq.getParam("YD_GP"));
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				ydCarSchId		= commUtils.getValue(gdReq, "YD_CAR_SCH_ID", ii);
				trnEqpCd		= commUtils.getValue(gdReq, "TRN_EQP_CD", ii);
				ysStkColGp 		= commUtils.getValue(gdReq, "YS_STK_COL_GP", ii);
				ydCarpntCd		= commUtils.getValue(gdReq, "YD_CARPNT_CD", ii);
				WLOC_CD			= commUtils.getValue(gdReq, "WLOC_CD", ii);
				YD_PNT_CD 		= commUtils.getValue(gdReq, "YD_PNT_CD", ii);
				
				/**********************************************************
		    	 * 5.출발지 적치열 베드/단 정보 체크
		    	 **********************************************************/			
		    	recInTemp.setField("WLOC_CD",   WLOC_CD);
		    	recInTemp.setField("YD_PNT_CD", YD_PNT_CD);

		    	rsStkCol = commDao.select(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkcolWLocCdandPntCd", logId, methodNm, "적치열 조회"); 
		    	intLevLocGp = rsStkCol.size();	    	
		    	if (rsStkCol == null || intLevLocGp == 0) {
		    		szMsg= "[" + methodNm + "] 발지개소["+WLOC_CD+"] 및 포인트 코드["+YD_PNT_CD+"]가 타공정코드가 아니고 대기장입니다.";
		    		commUtils.printLog(logId, szMsg, "SL");
		    	}
		    	
				/**********************************************************
		    	 * 6.출발지 정보 CLEAR / 비활성화 상태(YD_STK_COL_ACT_STAT = C)로 업데이트
		    	 **********************************************************/
		    	if(intLevLocGp > 0) {
		    		
		    		rsStkCol.absolute(1);
			    	recOutTemp.setRecord(rsStkCol.getRecord());		    	
			    	szYD_CARLD_STOP_LOC     	= commUtils.trim(recOutTemp.getFieldString("YS_STK_COL_GP")); 
			    	String szCOL_TRN_EQP_CD   	= commUtils.trim(recOutTemp.getFieldString("TRN_EQP_CD")); 		    	
			    	szMsg = "[" + methodNm + "] 발지개소코드["+WLOC_CD+"], " +
			    			"발지개소POINT코드["+YD_PNT_CD+"]로 야드에서 관리되는 적치열구분[출발지:"+szYD_CARLD_STOP_LOC+"]이 존재합니다.";
			    	commUtils.printLog(logId, szMsg, "SL");
					
					/**********************************************************
			    	 * 6-1.(적치열의 운송코드 = 전문 운송코드) -> 맵 Clear
			    	 **********************************************************/
					if( szCOL_TRN_EQP_CD.equals(trnEqpCd))	{					
						szMsg= "[" + methodNm + "] 출발야드의 적치열["+szYD_CARLD_STOP_LOC+"]의 운송장비코드["+szCOL_TRN_EQP_CD+"]와 전문의 운송장비코드["+trnEqpCd+"]가 같으므로 맵 Clear 시작 ";
						commUtils.printLog(logId, szMsg, "SL");
						
						/**********************************************************
				    	 * 6-1-1. 출발야드 적치열 -> 비활성상태(C) 로 업데이트
				    	 **********************************************************/
						szMsg= "[" + methodNm + "] 출발야드의 적치열["+szYD_CARLD_STOP_LOC+"]을 비활성상태로 변경처리 시작 ";
						commUtils.printLog(logId, szMsg, "SL");
						
				    	recInTemp.setField("YS_STK_COL_GP",        szYD_CARLD_STOP_LOC);
				    	recInTemp.setField("YD_STK_COL_ACT_STAT",  "C");
				    	recInTemp.setField("YD_CAR_USE_GP",        "");
				    	recInTemp.setField("TRN_EQP_CD",           "");
				    	recInTemp.setField("CAR_NO",               "");
				    	recInTemp.setField("CARD_NO",              "");
				    	recInTemp.setField("MODIFIER", 			   commUtils.trim(gdReq.getParam("userid")));
				    	
				    	intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkcolByColActStatClear", logId, methodNm, "TB_YS_STKCOL 등록");
						if(intRtnVal <= 0) {
							szMsg="[" + methodNm + "] 적치열[" + szYD_CARLD_STOP_LOC + "]활성화중 ERROR 발생.";
							commUtils.printLog(logId, methodNm, "SL");
							m_ctx.setRollbackOnly();
							throw new DAOException(szMsg);
						}
					
						/**********************************************************
				    	 * 6-1-2. 차량포인트통합관리 
				    	 **********************************************************/
						//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
						//YsCarPointinforeg2("2","","",szYD_CARLD_STOP_LOC,"","","C",logId,methodNm);

						recInTemp.setField("STAT", "C");
						recInTemp.setField("YS_STK_COL_GP", szYD_CARLD_STOP_LOC);
						
						intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.carpointstackcolgpupdateCT", logId, methodNm, "저장위치로 초기화 하는 경우(구내운송)");
						if(intRtnVal <= 0) {
							szMsg="저장위치로 차량포인트 초기화중 ERROR 발생.";
							commUtils.printLog(logId, methodNm, "SL");
							throw new DAOException(szMsg);
						}
						
						 // 적치베드 비활성상태로 변경
						/**********************************************************
				    	 * 6-1-3. 출발야드 적치베드 -> 야드적치베드활성상태(=C(비활성상태), YD_STK_BED_ACT_STAT) 
				    	 *                         및 BED중량MAX(=기본값, YD_STK_BED_WT_MAX) 으로 업데이트
				    	 **********************************************************/
						szMsg= "[" + methodNm + "] 출발야드의 적치열["+szYD_CARLD_STOP_LOC+"]의 베드를 비활성상태와 BED중량MAX기본값으로 변경처리 시작 ";
						commUtils.printLog(logId, szMsg, "SL");
						
						recInTemp.setField("YD_STK_BED_WT_MAX", YsConstant.YD_STK_BED_WT_MAX_DEFAULT);
						recInTemp.setField("YS_STK_COL_GP", szYD_CARLD_STOP_LOC);
						recInTemp.setField("YD_STK_BED_ACT_STAT", "C");
						
						intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkbedYdStkColGp", logId, methodNm, "TB_YS_STKBED 등록");
						if(intRtnVal <= 0) {
							szMsg="[" + methodNm + "] 적치BED[" + szYD_CARLD_STOP_LOC + "]활성화중 ERROR 발생.";
							commUtils.printLog(logId, methodNm, "SL");
							throw new DAOException(szMsg);
						}
						
						/**********************************************************
				    	 * 6-1-4. 출발야드 적치단 -> 야드적치단활성상태(=C(비활성상태), YD_STK_LYR_ACT_STAT) 
				    	 *                       및 야드적치단재료상태(=E(적치가능), YD_STK_LYR_MTL_STAT) 로 업데이트
				    	 **********************************************************/
						szMsg= "[" + methodNm + "] 출발야드의 적치열["+szYD_CARLD_STOP_LOC+"]의 적치단을 비활성상태로 변경처리 시작 ";
						commUtils.printLog(logId, methodNm, "SL");
						
						recInTemp.setResultCode(logId);	//Log ID
						recInTemp.setResultMsg(methodNm);	//Log Method Name
						recInTemp.setField("YS_STK_COL_GP", szYD_CARLD_STOP_LOC);
						recInTemp.setField("YD_STK_LYR_ACT_STAT", "C");
						recInTemp.setField("SSTL_NO", "");
						recInTemp.setField("YD_STK_LYR_MTL_STAT", "E");
						
						intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColGpClear", logId, methodNm, "TB_YS_STKLYR 등록");
						if(intRtnVal <= 0) {
							szMsg="[" + methodNm + "] 적치단[" + szYD_CARLD_STOP_LOC + "]활성화중 ERROR 발생.";
							commUtils.printLog(logId, szMsg, "SL");
							throw new DAOException(szMsg);
						}
						
						/**********************************************************
				    	 * 6-1-5. 차량 출발 시 상차지 저장위치 제원 야드 L2 로 전송
				    	 *          야드BLOOM:N1,BILLET:N2,선재:N3,봉강:N4,선재자동화:N5,봉강자동화:N6
				    	 *          YSN1L001 저장위치제원
				    	 *          YSN1L002 저장품제원
				    	 *          YSN1L003 크레인작업지시
				    	 *          YSN1L004 크레인작업실적응답
				    	 **********************************************************/
						String	szJMS_TC_CD = "YSN2L001";
			    		recInTemp.setField("MSG_ID"			,    szJMS_TC_CD);
						recInTemp.setField("YD_INFO_SYNC_CD", "3");						//1:동,2:SPAN,3:열,4:BED
						recInTemp.setField("YD_GP"			, szYD_CARLD_STOP_LOC.substring(0, 1));
						recInTemp.setField("YS_STK_COL_GP"	, szYD_CARLD_STOP_LOC);
						recInTemp.setField("YD_CAR_PROG_STAT", "1");
						recInTemp.setField("YD_EQP_WRK_STAT" , "U");
						szMsg = "[" + methodNm + "] 공차출발시 시 저장위치 제원 야드L2로 전송";
						
						//전송 Data 생성
						jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2(szJMS_TC_CD, recInTemp));
					}
		    	}
				
				/**********************************************************
				* 2. 기존 이송차량스케줄/재료 삭제
				**********************************************************/
				jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);

				//이송차량재료 초기화
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarSchMtl", logId, methodNm, "이송차량재료 초기화");

				//이송차량스케줄 초기화
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarSch", logId, methodNm, "이송차량스케줄 초기화");
				
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of initMvCarSchMgt
	/**
	 * 차량작업 포인트 현황- 차량출발
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord procLeaveCar(GridData gdReq) throws DAOException {
		String methodNm = "차량작업 포인트 현황-차량출발[BtYsJspSeEJB.procLeaveCar] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String szMsg = null;
		String szYD_CAR_SCH_ID		= null;
		String szYD_CAR_PROG_STAT	= null;
		String szCAR_NO				= null;
		String szCARD_NO			= null;
		String szSPOS_WLOC_CD		= null;
		String szYD_PNT_CD			= null;
		String szTRANS_ORD_DATE		= null;
		String szTRANS_ORD_SEQNO	= null;
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			//Return Value
			JDTORecord jrRtn = null;
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				szYD_CAR_SCH_ID = commUtils.getValue(gdReq, "YD_CAR_SCH_ID", ii);
				
				
				//--------------------------------------------------------------------------------
				//	차량스케줄ID로 차량스케줄 조회
				//--------------------------------------------------------------------------------
				
			
				JDTORecord recTemp			= JDTORecordFactory.getInstance().create();
				jrParam.setField("YD_CAR_SCH_ID", 			szYD_CAR_SCH_ID);
				
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarsch
				SELECT *
				FROM TB_YS_CARSCH C
				WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				*/
				//차량스케쥴 조회
				JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarsch", logId, methodNm, "차량스케쥴 조회");

				int rowCnt1 = jsCarSch.size();

				if (rowCnt1 <= 0) {
					commUtils.printLog(logId, "차량스케줄이 업습니다. SKIP", "SL");
					continue;
				}				
				
				jsCarSch.first();
				recTemp		= jsCarSch.getRecord();
				
				szYD_CAR_PROG_STAT		= commUtils.trim(recTemp.getFieldString("YD_CAR_PROG_STAT"));
				
				if( !szYD_CAR_PROG_STAT.equals(YdConstant.YD_CARLD_CMPL)) {
					szMsg = "["+methodNm+"] 차량스케줄["+szYD_CAR_SCH_ID+"]의 차량진행상태["+szYD_CAR_PROG_STAT+"]가 상차완료가 아니므로 SKIP시킴";
					commUtils.printLog(logId, szMsg, "SL");
					continue;
				}
				
				szCAR_NO				=  commUtils.trim(recTemp.getFieldString("CAR_NO"));
				szCARD_NO				=  commUtils.trim(recTemp.getFieldString("CARD_NO"));
				szSPOS_WLOC_CD			=  commUtils.trim(recTemp.getFieldString("SPOS_WLOC_CD"));
				szYD_PNT_CD				=  commUtils.trim(recTemp.getFieldString("YD_PNT_CD1"));
				szTRANS_ORD_DATE		=  commUtils.trim(recTemp.getFieldString("TRANS_ORD_DATE"));
				szTRANS_ORD_SEQNO		=  commUtils.trim(recTemp.getFieldString("TRANS_ORD_SEQNO"));
				
				
				//--------------------------------------------------------------------------------
				
				szMsg = "차량스케줄["+szYD_CAR_SCH_ID+"]의 차량출발 처리 EJB 호출";
				commUtils.printLog(logId, szMsg, "SL");
				
				JDTORecord recInTemp			= JDTORecordFactory.getInstance().create();
				recInTemp.setResultCode(logId);	//Log ID
				recInTemp.setResultMsg(methodNm);	//Log Method Name				
				recInTemp.setField("CARD_NO", 				szCARD_NO);
				recInTemp.setField("CAR_NO", 				szCAR_NO);			
				recInTemp.setField("SPOS_WLOC_CD", 			szSPOS_WLOC_CD);
				recInTemp.setField("SPOS_YD_PNT_CD", 		szYD_PNT_CD);
				recInTemp.setField("TRANS_ORD_DATE", 		szTRANS_ORD_DATE);
				recInTemp.setField("TRANS_ORD_SEQNO", 		szTRANS_ORD_SEQNO);
				recInTemp.setResultCode(logId);		//Log ID
				recInTemp.setResultMsg(methodNm);	//Log Method Name
				
				EJBConnector ejbConn = new EJBConnector("default", "YsCommCarMvSeEJB", this);
				jrRtn = (JDTORecord)ejbConn.trx("procOutCarLevWr", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
				
				//ydDelegate.sendMsg(recInTemp);
			
				szMsg = "차량스케줄["+szYD_CAR_SCH_ID+"]의 차량출발 처리 EJB 호출완료";
				commUtils.printLog(logId, szMsg, "SL");
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} 
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
	public JDTORecord procCarUd(GridData gdReq) throws DAOException {
		String methodNm = "차량작업관리화면 하차완료처리[BtYsJspSeEJB.procCarUd] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String szMsg = null;
		String szARR_YD_PNT_CD = null;
		String szCurrDate = commUtils.getCurDate("yyyyMMddHHmmss");
		JDTORecord sndRecord	= JDTORecordFactory.getInstance().create();
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);	
	
			//DAO Parameter - Log ID, Method, 수정자 Set
			
			String szYD_CAR_SCH_ID 		= commUtils.getValue(gdReq, "YD_CAR_SCH_ID", 0);
			String szYD_WBOOK_ID 		= commUtils.getValue(gdReq, "YD_WRK_BOOK_ID", 0);
			String szARR_WLOC_CD 		= commUtils.getValue(gdReq, "ARR_WLOC_CD", 0);
			String szTRN_EQP_CD 		= commUtils.getValue(gdReq, "TRN_EQP_CD", 0);
			String szYD_CAR_STOP_LOC 	= commUtils.getValue(gdReq, "YD_CAR_STOP_LOC", 0);
			String szYD_CAR_USE_GP 		= commUtils.getValue(gdReq, "YD_CAR_USE_GP", 0);
			
			
//			if( !szYD_CAR_STOP_LOC.equals("") ) {
//				szYD_GP = szYD_CAR_STOP_LOC.substring(0, 1);
//			}
			
			
			szMsg = "차량스케줄ID["+szYD_CAR_SCH_ID+"], 작업예약ID["+szYD_WBOOK_ID+"], 운송장비코드["+szTRN_EQP_CD+"], 착지개소코드["+szARR_WLOC_CD+"]";
			commUtils.printLog(logId, szMsg, "SL");
			
			//------------------------------------------------------------------------------------------------------
			//	차량스케줄 조회 후 차량진행상태 확인 시작 - 다른유저에 의해서 상태가 변경될 수 있으므로 먼저 상태를 확인 필요
			//	차량스케줄 조회
			//------------------------------------------------------------------------------------------------------
			szMsg = "차량스케줄ID["+szYD_CAR_SCH_ID+"]로 조회 전";
			commUtils.printLog(logId, szMsg, "SL");
			
			JDTORecord recTemp	= JDTORecordFactory.getInstance().create();
			JDTORecord recPara	= JDTORecordFactory.getInstance().create();
			JDTORecord recStkCol= JDTORecordFactory.getInstance().create();
			//1. 차량스케줄을 먼저 조회해서 존재하는 지를 확인
			recTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
			
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarsch
			SELECT *
			FROM TB_YS_CARSCH C
			WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
			*/
			//차량스케쥴 조회
			JDTORecordSet jsCarSch = commDao.select(recTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarsch", logId, methodNm, "차량스케쥴 조회");
			if (jsCarSch.size() <= 0) {
				throw new Exception( "차량스케줄ID["+szYD_CAR_SCH_ID+"]이 존재하지 않습니다");
			}				
		
			jsCarSch.first();
			recPara = jsCarSch.getRecord();

			String szYD_CAR_PROG_STAT = commUtils.trim(recPara.getFieldString("YD_CAR_PROG_STAT"          ));	//차량진행상태
			szMsg = "차량스케줄ID["+szYD_CAR_SCH_ID+"]로 야드차량진행상태["+szYD_CAR_PROG_STAT+"]";
			commUtils.printLog(logId, szMsg, "SL");

			if( !szYD_CAR_PROG_STAT.equals("B") && !szYD_CAR_PROG_STAT.equals("C")) {
				throw new Exception( "차량스케줄["+szYD_CAR_SCH_ID+"]의 야드차량진행상태["+szYD_CAR_PROG_STAT+"]가 하차완료[하차완료가능상태 : 하차도착(B), 하차검수(C)]할 수 있는 상태가 아닙니다.");
			}
			
			szMsg = "차량스케줄["+szYD_CAR_SCH_ID+"]의 야드차량진행상태["+szYD_CAR_PROG_STAT+"]가 하차완료처리가능한 상태입니다.";
			commUtils.printLog(logId, szMsg, "SL");

			//------------------------------------------------------------------------------------------------------
			// 차량스케줄의 차량진행상태를 하차완료로 변경 - 삭제처리를 하지 않음
			//------------------------------------------------------------------------------------------------------
			recPara         = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CAR_SCH_ID"	, szYD_CAR_SCH_ID);			//차량스케줄ID
			recPara.setField("YD_EQP_WRK_STAT"	, "U");						//야드설비작업상태
			recPara.setField("YD_CARUD_ST_DT"	, szCurrDate);				//하차개시일시
			recPara.setField("YD_CARUD_CMPL_DT"	, szCurrDate);				//하차완료일시
			recPara.setField("YD_CAR_PROG_STAT"	, "E");						//차량진행상태 : 하차완료[E]
			recPara.setField("MODIFIER"			, commUtils.trim(gdReq.getParam("userid")));					//수정자
			
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdCarSchCarWrkDn 
			UPDATE TB_YS_CARSCH
			   SET MODIFIER     = :V_MODIFIER
			     , MOD_DDTT     = SYSDATE
			     , YD_EQP_WRK_STAT = NVL(:V_YD_EQP_WRK_STAT,YD_EQP_WRK_STAT)
			     , YD_CARUD_ST_DT= NVL(:V_YD_CARUD_ST_DT,YD_CARUD_ST_DT)
			     , YD_CARUD_CMPL_DT= NVL(:V_YD_CARUD_CMPL_DT,YD_CARUD_CMPL_DT)
			     , YD_CAR_PROG_STAT= NVL(:V_YD_CAR_PROG_STAT,YD_CAR_PROG_STAT)
			 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
			 */
			int intRtnVal = commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdCarSchCarWrkDn", logId, methodNm, "차량스케줄 갱신");
			
			if( intRtnVal == 0 ) {
				throw new Exception( "차량스케줄["+szYD_CAR_SCH_ID+"]에 하차개시일시, 하차완료일시, 차량진행상태[하차완료-E]를 업데이트시 차량스케줄이 존재하지 않습니다");
			}
			
			//------------------------------------------------------------------------------------------------------
			// 1. 차량 이송재료를 조회 후 삭제처리
			//------------------------------------------------------------------------------------------------------
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
			recPara.setField("DEL_YN", "Y");
			recPara.setField("MODIFIER",commUtils.trim(gdReq.getParam("userid")));					//수정자
			
			//차량이송소재 종료
			//intRtnVal = ydCarFtmvMtlDao.updYdCarftmvmtl(recInTemp, 1) ;
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarFtmvMtl  
			UPDATE TB_YS_CARFTMVMTL
			   SET MODIFIER = :V_MODIFIER
			     , MOD_DDTT    = SYSDATE
		  	     , DEL_YN = :V_DEL_YN
			 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
			   AND DEL_YN = 'N'
			  */ 
			
			commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnWrkBook", logId, methodNm, "TB_YS_CARFTMVMTL 종료");

			if(szYD_CAR_USE_GP.equals("G")) {
			
			} else {	
				//구내운송
				/**********************************************************
				* 1.하차개시 전송 시작
				**********************************************************/
				recPara         = JDTORecordFactory.getInstance().create();
				recStkCol       = JDTORecordFactory.getInstance().create();
				recPara.setField("YS_STK_COL_GP", 			szYD_CAR_STOP_LOC);
	
				//적치열 Table를 조회한다.
	    		 /* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkCol1 
	    		SELECT 
	    			YS_STK_COL_GP AS YS_STK_COL_GP
	    			,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
	    			,REGISTER AS REGISTER
	    			,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT
	    			,MODIFIER AS MODIFIER
	    			,DEL_YN AS DEL_YN
	    			,YD_GP AS YD_GP
	    			,YD_BAY_GP AS YD_BAY_GP
	    			,YD_EQP_GP	AS YD_EQP_GP
	    			,YD_STK_COL_NO AS YD_STK_COL_NO
	    			,YD_STK_COL_ACT_STAT AS YD_STK_COL_ACT_STAT
	    			,YD_STK_COL_RULE_XAXIS AS YD_STK_COL_RULE_XAXIS
	    			,YD_STK_COL_RULE_YAXIS AS YD_STK_COL_RULE_YAXIS
	    			,YD_STK_COL_W AS YD_STK_COL_W
	    			,YD_STK_COL_L AS YD_STK_COL_L
	    			,YD_CAR_USE_GP AS YD_CAR_USE_GP
	    			,TRN_EQP_CD AS TRN_EQP_CD
	    			,CAR_NO AS CAR_NO
	    			,CARD_NO AS CARD_NO
	    			,WLOC_CD AS WLOC_CD
	    			,YD_PNT_CD AS YD_PNT_CD
	    			,YS_STK_COL_T_GP AS YS_STK_COL_T_GP
	    			,YS_STK_COL_W_GP AS YS_STK_COL_W_GP
	    			,YS_STK_COL_L_GP AS YD_STK_COL_L_GP
	    		--	,YD_STK_COL_H_MAX AS YD_STK_COL_H_MAX--
	    			--,YD_STK_COL_BED_L_TP AS YD_STK_COL_BED_L_TP
	    		    ,YS_OUTDIA_GRP_GP AS YS_OUTDIA_GRP_GP 
	    		    ,YD_STKBED_USG_CD
	    		FROM TB_YS_STKCOL
	    		WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
	    		AND DEL_YN ='N'
	    			*/
				JDTORecordSet rsStkCol = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkCol1", logId, methodNm, "적치열 조회"); 
		    	
		    	if (rsStkCol == null || rsStkCol.size() <= 0) {
					szMsg="차량정지위치[" + szYD_CAR_STOP_LOC + "]에 대한 개소코드가 없습니다.";
					throw new Exception(szMsg);
				}
				
		    	rsStkCol.first();
				recStkCol = rsStkCol.getRecord();
				
				szARR_WLOC_CD   = commUtils.trim(recStkCol.getFieldString("WLOC_CD"          ));
				szARR_YD_PNT_CD = commUtils.trim(recStkCol.getFieldString("YD_PNT_CD"          ));
				szMsg="차량정지위치[" + szYD_CAR_STOP_LOC + "]에 대한 개소코드[" + szARR_WLOC_CD + "]와 야드포인트코드[" + szARR_YD_PNT_CD + "]";
				commUtils.printLog(logId, szMsg, "SL");
				
				recPara         = JDTORecordFactory.getInstance().create();
				recPara.setResultCode(logId);	//Log ID
				recPara.setResultMsg(methodNm);	//Log Method Name
				/**********************************************************
				* 1.하차개시 전송 시작
				*  JMS_TC_CD	JMSTC코드	CHAR	8
				*  JMS_TC_CREATE_DDTT	JMSTC생성일시	DATE	14
				*  TRN_EQP_CD            운송장비코드	CHAR	8
				*  ARR_WLOC_CD           착지개소코드	CHAR	5
				*  ARR_YD_PNT_CD         착지야드포인트코드	CHAR	4
				*  TRN_WRK_ST_DT			운송작업시작일시	DATE	14
				**********************************************************/
				
				recPara.setField("JMS_TC_CD", 			"YSTSJ009");
				recPara.setField("JMS_TC_CREATE_DDTT", 	szCurrDate);
				recPara.setField("TRN_EQP_CD",     		szTRN_EQP_CD);
				recPara.setField("ARR_WLOC_CD", 		szARR_WLOC_CD);
				recPara.setField("ARR_YD_PNT_CD", 		szARR_YD_PNT_CD);
				recPara.setField("TRN_WRK_ST_DT", 		szCurrDate);
				sndRecord = commUtils.addSndData(sndRecord,recPara);
				
				commUtils.printLog(logId, "하차개시전문을 구내운송으로 전송 완료", "SL");
							
				//+++++++++++++++++ 하차개시 전송 끝 ++++++++++++++++
				
				//+++++++++++++++++ 하차완료 전송 시작 ++++++++++++++++
				/**********************************************************
				* 1.하차완료 전송 시작
				*  JMS_TC_CD	JMSTC코드	CHAR	8
				*  JMS_TC_CREATE_DDTT	JMSTC생성일시	DATE	14
				*  TRN_EQP_CD            운송장비코드	CHAR	8
				*  ARR_WLOC_CD           착지개소코드	CHAR	5
				*  ARR_YD_PNT_CD         착지야드포인트코드	CHAR	4
				*  CARUD_CMPL_DT		  하차완료일시	DATE	14
				**********************************************************/
				
				recPara         = JDTORecordFactory.getInstance().create();
				recPara.setResultCode(logId);	//Log ID
				recPara.setResultMsg(methodNm);	//Log Method Name
				//2. 하차완료를 구내운송으로 전송
				recPara.setField("JMS_TC_CD", 			"YSTSJ010");
				recPara.setField("JMS_TC_CREATE_DDTT", 	szCurrDate);
				recPara.setField("TRN_EQP_CD",     		szTRN_EQP_CD);
				recPara.setField("ARR_WLOC_CD", 		szARR_WLOC_CD);
				recPara.setField("ARR_YD_PNT_CD", 		szARR_YD_PNT_CD);
				recPara.setField("CARUD_CMPL_DT", 		commUtils.getCurDate("yyyyMMddHHmmss"));
				
				sndRecord = commUtils.addSndData(sndRecord,recPara);
				
				commUtils.printLog(logId, "하차완료전문을 구내운송으로 전송 완료", "SL");
				//+++++++++++++++++ 하차완료 전송 끝 ++++++++++++++++
				
				commUtils.printLog(logId, methodNm, "S-");
			}	
			return sndRecord;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}		
	/**
	 * GridData -  차량상차정보 조회 - 차량상차정보
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getbtCarldInfoInqjl(JDTORecord recPara) throws DAOException {
		String methodNm = "차량상차정보 조회 - 차량상차정보[BtYsJspSeEJB.getbtCarldInfoInqjl] < " + recPara.getResultMsg();
		String logId = recPara.getResultCode();
		String szCarProgStat = null;
		JDTORecordSet jsTcar = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		try {
			
			commUtils.printLog(logId, methodNm, "S+", recPara);
				
			recPara.setField("CAR_NO",    	commUtils.nvl(recPara.getFieldString("CAR_NO"),""));
			recPara.setField("TRN_EQP_CD",  commUtils.nvl(recPara.getFieldString("CAR_NO"),""));
			
			//기본정보조회
			JDTORecordSet jsCrn = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCarSch", logId, methodNm, "TB_YS_CARSCH");
		    if (jsCrn == null || jsCrn.size() <= 0) {
				throw new Exception("차량스케줄에서 조회시 에러..스케줄 정보가 존재하지 않습니다.");
		    }
			
		    JDTORecord jrCrn = jsCrn.getRecord(0);

		    // 차량 진행 상태 코드 값이 '1','2',(상차출발, 상차도착) 인 경우
		    szCarProgStat   = commUtils.trim(jrCrn.getFieldString("YD_CAR_PROG_STAT")); //야드작업진행상태
			
			//******************************
			// 2개의 쿼리는 컬럼명을 동일하게 하여 읽어올수 있도록 한다.
			//******************************

			if ("1".equals(szCarProgStat) || "2".equals(szCarProgStat)){
			
				//차량 스케줄에 상차 작업예약 ID 로 작업예약 재료 정보 조회를 한다.
				if(commUtils.trim(jrCrn.getFieldString("YD_CARLD_WRK_BOOK_ID")).equals("")){
					
//					throw new Exception("작업예약 ID가 없습니다( 차량진도코드가 : 1, 2 경우)");
					commUtils.printLog(logId, methodNm, "S-");
					return jsTcar;
					
				} else {
				
					recPara.setField("YD_WBOOK_ID", commUtils.trim(jrCrn.getFieldString("YD_CARLD_WRK_BOOK_ID")));					
	
					jsTcar = commDao.select(recPara, "com.inisteel.cim.ys.bt.dao.BtYsDAO.getbtCarldInfoInqjlByYdWrkBook", logId, methodNm, "작업예약 조회");

				}	
			} else {		
				
				// 차량 진행 상태 코드값이  그 이외인 경우 는 차량  이송재료 정보를 읽어온다.
				recPara.setField("YD_CAR_SCH_ID", commUtils.trim(jrCrn.getFieldString("YD_CAR_SCH_ID")));						
				
				jsTcar = commDao.select(recPara, "com.inisteel.cim.ys.bt.dao.BtYsDAO.getbtCarldInfoInqjlByCarFtmvMtl", logId, methodNm, "차량이송 조회");

			}
			
			if (jsTcar == null || jsTcar.size() == 0) {
				throw new Exception("상차 조회된 정보가 없습니다)");
			}	
			
			
			// 데이터 존재시 첫번째 레코드 위치에 차량진도코드를 보내준다.
			
//			jsTcar.first();
//			recCarProgStat = jsTcar.getRecord(0);
//			recCarProgStat.setField("CAR_PROG_STAT", szCarProgStat);
			commUtils.printLog(logId, methodNm, "S-");
			
			return jsTcar;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}//end of getgdsCarldInfoInqjl
	/**
	 *      [A] 오퍼레이션명 : 차량상차정보 조회 - 차상위치 수정
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updbtCarldInfoInqjl(GridData gdReq) throws DAOException {
		String methodNm = "차량상차정보 조회 - 차상위치 수정[BtYsJspSeEJB.updbtCarldInfoInqjl] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				//차량상세 수정 
				jrParam.setField("SSTL_NO"				, commUtils.getValue(gdReq, "SSTL_NO", ii) ); 
				jrParam.setField("YD_CAR_SCH_ID"		, commUtils.getValue(gdReq, "YD_CAR_SCH_ID", ii) ); 
				jrParam.setField("YS_STK_COL_GP"		, commUtils.getValue(gdReq, "YS_STK_COL_GP", ii) ); 
				jrParam.setField("YS_STK_BED_NO"		, commUtils.getValue(gdReq, "YS_STK_BED_NO", ii) );     // 차상위치 
				jrParam.setField("YS_STK_LYR_NO"		, commUtils.getValue(gdReq, "YS_STK_LYR_NO", ii) );     // 단  
				
				//차량재료정보 수정 (공통으로 사용)
				commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updgdsWhsPlnInfojlByCarFtmvMtl", logId, methodNm, "TB_YS_CARFTMVMTL");

				//기존위치 CLEAR (공통으로 사용)
				commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updgdsWhsPlnInfojlByStkLyr", logId, methodNm, "TB_YS_STKLYR");

				//차량위치 등록 (공통으로 사용)
				commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updgdsWhsPlnInfojlByCarStkLyr", logId, methodNm, "TB_YS_STKLYR");

				//차량재료정보 수정  (공통으로 사용)
				commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updgdsWhsPlnInfojlByCarFtmvMtl", logId, methodNm, "TB_YS_CARFTMVMTL");
				
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}

	/**
	 * 보급Lot등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord regSupLot(GridData gdReq) throws DAOException {
		String methodNm = "보급Lot등록[BtYsJspSeEJB.regSupLot] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			String ydPrepSchId = commDao.getSeqId(logId, methodNm, "PrepSch");

			if ("".equals(ydPrepSchId)) {
				throw new Exception("준비스케쥴ID 생성 실패");
			}				
			
			jrParam.setField("YD_PREP_SCH_ID", ydPrepSchId); //야드준비스케쥴ID
			jrParam.setField("YD_SCH_CD", gdReq.getParam("YD_SCH_CD")); //스케줄코드
			jrParam.setField("YD_PREP_WK_ST", commUtils.nvl(gdReq.getParam("YD_PREP_WK_ST"),"")); //야드준비작업상태 

			//등록 할  레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				//준비재료 등록
				jrParam.setField("SSTL_NO"			, commUtils.getValue(gdReq, "SSTL_NO", ii)); 
				jrParam.setField("YS_STK_COL_GP"	, commUtils.getValue(gdReq, "YD_STR_LOC", ii).substring(0,6)); 
				jrParam.setField("YS_STK_BED_NO"	, commUtils.getValue(gdReq, "YD_STR_LOC", ii).substring(6,8)); 
				jrParam.setField("YS_STK_LYR_NO"	, commUtils.getValue(gdReq, "YS_STK_LYR_NO",ii)); 
				jrParam.setField("YS_STK_SEQ_NO"	, commUtils.getValue(gdReq, "YS_STK_SEQ_NO", ii)); 
				
				commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insPrepMtl", logId, methodNm, "준비재료 등록");
			}
			
			commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insPrepSch", logId, methodNm, "준비스케줄 등록");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of regSupLot
	
	/**
	 * 이송Lot등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord regFtmvLot(GridData gdReq) throws DAOException {
		String methodNm = "이송Lot등록[BtYsJspSeEJB.regFtmvLot] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			String ydPrepSchId = commDao.getSeqId(logId, methodNm, "PrepSch");
			
			if ("".equals(ydPrepSchId)) {
				throw new Exception("준비스케쥴ID 생성 실패");
			}			

			jrParam.setField("YD_PREP_SCH_ID", ydPrepSchId); //야드준비스케쥴ID
			jrParam.setField("YD_SCH_CD", commUtils.getValue(gdReq, "YD_STR_LOC", 0).substring(0, 2) + gdReq.getParam("YD_SCH_CD")); //스케줄코드
			jrParam.setField("YD_PREP_WK_ST", commUtils.nvl(gdReq.getParam("YD_PREP_WK_ST"),"")); //야드준비작업상태 
			
			//등록 할  레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				//준비재료 등록
				jrParam.setField("SSTL_NO"			, commUtils.getValue(gdReq, "SSTL_NO", ii)); 
				jrParam.setField("YS_STK_COL_GP"	, commUtils.getValue(gdReq, "YD_STR_LOC", ii).substring(0,6)); 
				jrParam.setField("YS_STK_BED_NO"	, commUtils.getValue(gdReq, "YD_STR_LOC", ii).substring(7,8)); 
				jrParam.setField("YS_STK_LYR_NO"	, commUtils.getValue(gdReq, "YS_STK_LYR_NO",ii)); 
				jrParam.setField("YS_STK_SEQ_NO"	, commUtils.getValue(gdReq, "YS_STK_SEQ_NO", ii)); 
				
				commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insPrepMtl", logId, methodNm, "준비재료 등록");
			}
			
			commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insPrepSch", logId, methodNm, "준비스케줄 등록");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of regFtmvLot
	
	/**
	 * 준비스케줄 - 재료삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord delPrepMtl(GridData gdReq) throws DAOException {
		String methodNm = "준비스케줄 - 재료삭제[BtYsJspSeEJB.delPrepMtl] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			//DEL = 'Y' 할  레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				//준비재료 삭제
				jrParam.setField("SSTL_NO"			, commUtils.getValue(gdReq, "SSTL_NO", ii)); 
				jrParam.setField("YD_PREP_SCH_ID"	, commUtils.getValue(gdReq, "YD_PREP_SCH_ID", ii)); 
				
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updPrepMtlDelY", logId, methodNm, "준비재료 삭제");
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of delPrepMtl
	
	/**
	 * 준비스케줄 - 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updPrepSchLot(GridData gdReq) throws DAOException {
		String methodNm = "준비스케줄 - 수정[BtYsJspSeEJB.updPrepSchLot] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			//DEL = 'Y' 할  레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				jrParam.setField("YD_PREP_SCH_ID"	, commUtils.getValue(gdReq, "YD_PREP_SCH_ID", ii));
				jrParam.setField("YD_AIM_BAY_GP"	, commUtils.getValue(gdReq, "YD_AIM_BAY_GP", ii)); 
				jrParam.setField("YD_CARASGN_SEQ"	, commUtils.getValue(gdReq, "YD_CARASGN_SEQ", ii)); 
				
				//준비스케줄 수정
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updPrepSch", logId, methodNm, "준비스케줄 수정");
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updPrepSchLot
	
	/**
	 * 준비스케줄 - 삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord delPrepSchLot(GridData gdReq) throws DAOException {
		String methodNm = "준비스케줄 - 삭제[BtYsJspSeEJB.delPrepSchLot] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			//DEL = 'Y' 할  레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				jrParam.setField("YD_PREP_SCH_ID"	, commUtils.getValue(gdReq, "YD_PREP_SCH_ID", ii));
				jrParam.setField("SSTL_NO"			, ""); 
				
				//준비재료 삭제
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updPrepMtlDelY", logId, methodNm, "준비재료 삭제");
				
				//준비스케줄 삭제
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updPrepSchDelY", logId, methodNm, "준비스케줄 삭제");
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of delPrepSchLot
	
	/**
	 * 설비인출보급 - 재료등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updPulloutSupMtl(GridData gdReq) throws DAOException {
		String methodNm = "설비인출보급 - 재료등록[BtYsJspSeEJB.updPulloutSupMtl] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;
			JDTORecordSet jsTemp = null;
			JDTORecord jrTemp = null;
			
			String szStlNo = null;
			String szYsStkColGp = null;
			String szYsStkBedNo = null;
			String szYsStkLyrNo = null;
			String szYsStkSeqNo = null;
			String szStkStlNo = null;
			String szWbookId = null;
			String szCrnSchId = null;
			String szToLocMtlStat = null;
			
			String szModGp = null; //작업구분

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				szStlNo 			= commUtils.getValue(gdReq, "SSTL_NO", ii);
				szYsStkColGp 		= commUtils.getValue(gdReq, "YS_STK_COL_GP", ii);
				szYsStkBedNo 		= commUtils.getValue(gdReq, "YS_STK_BED_NO", ii);
				szYsStkLyrNo 		= "01";
				szYsStkSeqNo 		= commUtils.getValue(gdReq, "YS_STK_SEQ_NO", ii);
		
				
				szModGp = "ADD";
				jrParam.setField("SSTL_NO"	, szStlNo );
				jrParam.setField("YS_STK_COL_GP"	, szYsStkColGp );
				jrParam.setField("YS_STK_BED_NO"	, szYsStkBedNo );
				jrParam.setField("YS_STK_LYR_NO"	, szYsStkLyrNo );
				jrParam.setField("YS_STK_SEQ_NO"	, szYsStkSeqNo );
				
				//제품번호가 야드저장품에 존재하는지, 작업예약과 크레인스케줄에 잡혀있는 대상인지를 가져오고 BILLET공통에서 현재진도코드를 가져온다. 
				jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.getIsEnableStlNo", logId, methodNm, "제품번호 작업대상여부 조회");
				
				if (jsTemp != null && jsTemp.size() > 0) {
					jrTemp = jsTemp.getRecord(0);

					szStkStlNo		= commUtils.trim(jrTemp.getFieldString("SSTL_NO"));
//					szBdcCurrProgCd	= commUtils.trim(jrTemp.getFieldString("CURR_PROG_CD"));
					szWbookId		= commUtils.trim(jrTemp.getFieldString("YD_WBOOK_ID"));
					szCrnSchId		= commUtils.trim(jrTemp.getFieldString("YD_CRN_SCH_ID"));
					szToLocMtlStat  = commUtils.trim(jrTemp.getFieldString("TOLOC_MTL_STAT"));

			    } else {
					throw new Exception("제품번호로 저장품 및 작업대상여부 조회시 에러가 발생했습니다!");
			    }				
				
				//저장품에 존재하는 제품번호인지 체크
				if("ADD".equals(szModGp)) {
					if("".equals(szStkStlNo)) {
						throw new Exception("제품번호 : "+jrParam.getFieldString("SSTL_NO")+" 가 TB_YS_STOCK 에 없습니다.");
					}
				}
				
				//추가,삭제,이동 모두 크레인스케줄에 작업대상인지 체크
				if(!"".equals(szCrnSchId)) {
					throw new Exception("제품번호 : "+jrParam.getFieldString("SSTL_NO")+" 가 크레인스케줄("+szCrnSchId+")에 작업대상으로 잡혀있습니다! 크레인작업이 완료된 이후나 크래인작업을 취소 한 후 수정이 가능합니다.");
				}
				
				//작업예약에 대상으로 잡혀있으면 작업 불가함
				if(!"".equals(szWbookId)) {
					throw new Exception("제품번호 : "+jrParam.getFieldString("SSTL_NO")+" 가 작업예약("+szWbookId+")에 작업대상으로 잡혀있습니다! 작업예약을 취소한 후 삭제가 가능합니다.");
				}
				
				//추가일 경우는 TO위치의 재료상태가 'E' 가 아니면 작업할 수 없음
				//삭제일 경우 TO위치의 재료상태가  'U'나 'D'일 경우 작업할 수 없음
				if("DELETE".equals(szModGp)) {
					if("U".equals(szToLocMtlStat) || "D".equals(szToLocMtlStat) ) {
						throw new Exception("TO 위치의 재료상태가 " + szToLocMtlStat + " 입니다. 삭제 작업을 할 수 없습니다.");
					}
				} else {
					if(!"E".equals(szToLocMtlStat) ) {
						throw new Exception("TO 위치의 재료상태가 " + szToLocMtlStat + " 입니다. 등록 작업을 할 수 없습니다.");
					}
				}
				
				//------------------------------------------------------------------------------------------
				if("ADD".equals(szModGp)) {
					//SSTL_NO로 저장위치 조회하여 FROM위치가 존재하면 그 위치에서 SSTL_NO를 Clear 한다.
					jrParam.setField("SSTL_NO", szStlNo);
					jrParam.setField("YD_GP",   gdReq.getParam("YD_GP"));
					
					JDTORecordSet jsStkLyrStlNo = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStrLocRegPda", logId, methodNm, "재료번호로 조회");
					
					if(jsStkLyrStlNo.size() > 0) {
					
						String sFromLoc = null;
						
						for (int mm = 0; mm < jsStkLyrStlNo.size(); mm++) {
							if(!"TF".equals(jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_COL_GP").substring(2,4)) &&
							   !"LB".equals(jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_COL_GP").substring(2,4)) &&		
							   !"TC".equals(jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_COL_GP").substring(2,4))  ) {
								//설비구분이 TF,LB,TC가 아니면 에러 메세지를 리턴하고 종료한다.

								sFromLoc = jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_COL_GP") + "-" 
										 + jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_BED_NO") + "-"
										 + jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_LYR_NO") + "-"
										 + jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_SEQ_NO");
								
								throw new Exception(szStlNo + " 는 야드저장위치("+sFromLoc+")에 이미 등록된 재료입니다! 등록 작업을 할 수 없습니다.");
							}
						}
						
						for (int mm = 0; mm < jsStkLyrStlNo.size(); mm++) {
							if(!"".equals(jsStkLyrStlNo.getRecord(mm).getFieldString("YD_CRN_SCH_ID"))) {
								//크레인스케줄 편성 대상이면 에러 메세지를 리턴하고 종료한다.
						
								sFromLoc = jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_COL_GP") + "-" 
										 + jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_BED_NO") + "-"
										 + jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_LYR_NO") + "-"
										 + jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_SEQ_NO");
								
								
								throw new Exception("재료번호: "+ jsStkLyrStlNo.getRecord(mm).getFieldString("SSTL_NO") +  
										" 는 FROM 위치("+sFromLoc+")에서  크레인스케줄에 편성되어 있습니다. 등록 작업을 할 수 없습니다.");
							}
						}
						
						//SSTL_NO 로 STKLYR Clear 하기
						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.clearStkLyr", logId, methodNm, "이전 SSTL_NO가 있던 위치 Clear");	
					}
				}
				//------------------------------------------------------------------------------------------
				
				//To 위치 적치단 정보 수정
				jrParam.setField("SSTL_NO"					, szStlNo); //szStlNo 에는 삭제일경우 빈값이 들어있고 그외는 제품번호가 들어 있다.
				jrParam.setField("YD_STK_LYR_ACT_STAT"		, ""); // "" 값은 이전값을 변경안한다는 의미
				if("DELETE".equals(szModGp)) {
					jrParam.setField("YD_STK_LYR_MTL_STAT"	, "E"); //적치가능
				} else {
					jrParam.setField("YD_STK_LYR_MTL_STAT"	, "C"); //적치중
				}
				jrParam.setField("YS_STK_COL_GP"			, szYsStkColGp );
				jrParam.setField("YS_STK_BED_NO"			, szYsStkBedNo );
				jrParam.setField("YS_STK_LYR_NO"			, szYsStkLyrNo );
				jrParam.setField("YS_STK_SEQ_NO"			, szYsStkSeqNo );
				
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyr", logId, methodNm, "적치단정보 수정");
				
				//BLOOM공통 위치정보 수정하기
				if("DELETE".equals(szModGp)) {
					//삭제일경우 처리 ???
				} else {
					//추가, 이동
					jrParam.setField("FNL_REG_PGM"			, "updPulloutSupMtl" );
					jrParam.setField("YD_GP"				, szYsStkColGp.substring(0,1) );
					jrParam.setField("YD_BAY_GP"			, szYsStkColGp.substring(1,2) );
					jrParam.setField("YD_EQP_GP"			, szYsStkColGp.substring(2,4) );
					jrParam.setField("YS_STK_COL_NO"		, szYsStkColGp.substring(4,6) );
					jrParam.setField("YS_STK_BED_NO"		, szYsStkBedNo );
					jrParam.setField("YS_STK_LYR_NO"		, szYsStkLyrNo );
					jrParam.setField("YS_STK_SEQ_NO"		, szYsStkSeqNo );
					jrParam.setField("YS_STR_LOC"			, szYsStkColGp + szYsStkBedNo + szYsStkLyrNo + szYsStkSeqNo );
					jrParam.setField("SSTL_NO"				, szStlNo );
					
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBtCommYsStrLoc", logId, methodNm, "BILLET공통 야드저장위치 수정");
				}
				
				//야드저장품 위치정보 수정하기
				if("DELETE".equals(szModGp)) {
					//삭제일경우 처리 ???
				} else {
					//추가, 이동
					jrParam.setField("YS_STK_COL_GP"		, szYsStkColGp );
					jrParam.setField("YS_STK_BED_NO"		, szYsStkBedNo );
					jrParam.setField("YS_STK_LYR_NO"		, szYsStkLyrNo );
					jrParam.setField("YS_STK_SEQ_NO"		, szYsStkSeqNo );
					jrParam.setField("YS_STR_LOC"			, szYsStkColGp + szYsStkBedNo + szYsStkLyrNo + szYsStkSeqNo );
					jrParam.setField("SSTL_NO"				, szStlNo );
					
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStockYsStrLoc", logId, methodNm, "야드저장품 야드저장위치 수정");
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updPulloutSupMtl
	
	/**
	 * 설비인출보급 - 재료삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord delPulloutSupMtl(GridData gdReq) throws DAOException {
		String methodNm = "설비인출보급 - 재료삭제[BtYsJspSeEJB.delPulloutSupMtl] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;
			JDTORecordSet jsTemp = null;
			JDTORecord jrTemp = null;
			
			String szStlNo = null;
			String szYsStkColGp = null;
			String szYsStkBedNo = null;
			String szYsStkLyrNo = null;
			String szYsStkSeqNo = null;
			
			String szOldStlNo = null;
			
			String szStkStlNo = null;
			String szWbookId = null;
			String szCrnSchId = null;
			String szToLocMtlStat = null;
			
			String szModGp = null; //작업구분
			
			int    iWkShCnt = 0;
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				szStlNo 			= commUtils.getValue(gdReq, "SSTL_NO", ii);
				szYsStkColGp 		= commUtils.getValue(gdReq, "YS_STK_COL_GP", ii);
				szYsStkBedNo 		= commUtils.getValue(gdReq, "YS_STK_BED_NO", ii);
				szYsStkLyrNo 		= "01";
				szYsStkSeqNo 		= commUtils.getValue(gdReq, "YS_STK_SEQ_NO", ii);
				
				szOldStlNo 			= commUtils.getValue(gdReq, "OLD_SSTL_NO", ii);
				
				//삭제처리
				szModGp = "DELETE";
				jrParam.setField("SSTL_NO"	, szOldStlNo );
				
				jrParam.setField("YS_STK_COL_GP"	, szYsStkColGp );
				jrParam.setField("YS_STK_BED_NO"	, szYsStkBedNo );
				jrParam.setField("YS_STK_LYR_NO"	, szYsStkLyrNo );
				jrParam.setField("YS_STK_SEQ_NO"	, szYsStkSeqNo );
				
				//제품번호가 야드저장품에 존재하는지, 작업예약과 크레인스케줄에 잡혀있는 대상인지를 가져오고 BILLET공통에서 현재진도코드를 가져온다. 
				jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.getIsEnableStlNo", logId, methodNm, "제품번호 작업대상여부 조회");
				
				if (jsTemp != null && jsTemp.size() > 0) {
					jrTemp = jsTemp.getRecord(0);

					szStkStlNo		= commUtils.trim(jrTemp.getFieldString("SSTL_NO"));
//					szBdcCurrProgCd	= commUtils.trim(jrTemp.getFieldString("CURR_PROG_CD"));
					szWbookId		= commUtils.trim(jrTemp.getFieldString("YD_WBOOK_ID"));
					szCrnSchId		= commUtils.trim(jrTemp.getFieldString("YD_CRN_SCH_ID"));
					szToLocMtlStat  = commUtils.trim(jrTemp.getFieldString("TOLOC_MTL_STAT"));

			    } else {
					throw new Exception("제품번호로 저장품 및 작업대상여부 조회시 에러가 발생했습니다!");
			    }				
				
				//추가,삭제,이동 모두 크레인스케줄에 작업대상인지 체크
				if(!"".equals(szCrnSchId)) {
					throw new Exception("제품번호 : "+jrParam.getFieldString("SSTL_NO")+" 가 크레인스케줄("+szCrnSchId+")에 작업대상으로 잡혀있습니다! 크레인작업이 완료된 이후나 크래인작업을 취소 한 후 수정이 가능합니다.");
				}
				
				//삭제는 작업예약에 대상으로 잡혀있으면 삭제 불가함
				if("DELETE".equals(szModGp) && !"".equals(szWbookId)) {
					throw new Exception("제품번호 : "+jrParam.getFieldString("SSTL_NO")+" 가 작업예약("+szWbookId+")에 작업대상으로 잡혀있습니다! 작업예약을 취소한 후 삭제가 가능합니다.");
				}
				
				//TO위치의 재료상태가 'U'나 'D'일 경우  수정작업을 할 수 없음
				if("U".equals(szToLocMtlStat) || "D".equals(szToLocMtlStat) ) {
					throw new Exception("TO 위치의 재료상태가 " + szToLocMtlStat + " 로 변경되었습니다. 수정 작업을 할 수 없습니다.");
				}
				
				
				
				//To 위치 적치단 정보 수정
				jrParam.setField("SSTL_NO"					, ""); //szStlNo 에는 삭제일경우 빈값이 들어있고 그외는 제품번호가 들어 있다.
				jrParam.setField("YD_STK_LYR_ACT_STAT"		, ""); // "" 값은 이전값을 변경안한다는 의미
				if("DELETE".equals(szModGp)) {
					jrParam.setField("YD_STK_LYR_MTL_STAT"	, "E"); //적치가능
				} else {
					jrParam.setField("YD_STK_LYR_MTL_STAT"	, "C"); //적치중
				}
				jrParam.setField("YS_STK_COL_GP"			, szYsStkColGp );
				jrParam.setField("YS_STK_BED_NO"			, szYsStkBedNo );
				jrParam.setField("YS_STK_LYR_NO"			, szYsStkLyrNo );
				jrParam.setField("YS_STK_SEQ_NO"			, szYsStkSeqNo );
				
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyr", logId, methodNm, "적치단정보 수정");
				
				//BILLET공통 위치정보 수정하기
				if("DELETE".equals(szModGp)) {
					//삭제일경우 처리 ???
					jrParam.setField("FNL_REG_PGM"			, "delPulloutSupMtl" );
					jrParam.setField("YD_GP"				, "_" );
					jrParam.setField("YD_BAY_GP"			, szYsStkColGp.substring(1,2) );
					jrParam.setField("YD_EQP_GP"			, szYsStkColGp.substring(2,4) );
					jrParam.setField("YS_STK_COL_NO"		, szYsStkColGp.substring(4,6) );
					jrParam.setField("YS_STK_BED_NO"		, szYsStkBedNo );
					jrParam.setField("YS_STK_LYR_NO"		, szYsStkLyrNo );
					jrParam.setField("YS_STK_SEQ_NO"		, szYsStkSeqNo );
					jrParam.setField("YS_STR_LOC"			, "_" + szYsStkColGp.substring(1,6) + szYsStkBedNo + szYsStkLyrNo + szYsStkSeqNo );
					jrParam.setField("SSTL_NO"				, szOldStlNo ); //삭제된 번호
					
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBtCommYsStrLoc", logId, methodNm, "BILLET공통 야드저장위치 수정");
					
				} 
				
				//야드저장품 위치정보 수정하기
				if("DELETE".equals(szModGp)) {
					//삭제일경우 처리 ???
					jrParam.setField("YS_STK_COL_GP"		, "_" + szYsStkColGp.substring(1,6) );
					jrParam.setField("YS_STK_BED_NO"		, szYsStkBedNo );
					jrParam.setField("YS_STK_LYR_NO"		, szYsStkLyrNo );
					jrParam.setField("YS_STK_SEQ_NO"		, szYsStkSeqNo );
					jrParam.setField("YS_STR_LOC"			, "_" + szYsStkColGp.substring(1,6) + szYsStkBedNo + szYsStkLyrNo + szYsStkSeqNo );
					jrParam.setField("SSTL_NO"				, szOldStlNo ); //삭제된 번호
					
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStockYsStrLoc", logId, methodNm, "야드저장품 야드저장위치 수정");
					
				}
				
			}
			

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of delPulloutSupMtl
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
		String methodNm = "크레인사양분리[BtYsJspSeEJB.setCrnSpecSpr] < " + jrCrnSpec.getResultMsg();
		String logId = jrCrnSpec.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			Vector vcLot = new Vector();	//크레인사양분리결과
			JDTORecord    jrRow = null;		//현재 Row
			JDTORecordSet jsLot = JDTORecordFactory.getInstance().createRecordSet("");	//Lot
			String szYS_STK_COL_GP  = "";	
			String szYS_STK_BED_NO  = "";	
			String szYS_STK_LYR_NO  = "";	
			String szCHK_YS_STK_COL_GP = "";
			String szCHK_YS_STK_BED_NO  = "";			
			String szCHK_YS_STK_LYR_NO  = "";			

			int rowCnt = jsWrkMtl.size();

			for (int ii = 0; ii < rowCnt; ii++) {
				jrRow = jsWrkMtl.getRecord(ii);
				
				szYS_STK_COL_GP = commUtils.trim(jrRow.getFieldString("YS_STK_COL_GP"));
				szYS_STK_BED_NO = commUtils.trim(jrRow.getFieldString("YS_STK_BED_NO"));
				szYS_STK_LYR_NO = commUtils.trim(jrRow.getFieldString("YS_STK_LYR_NO"));
				
				if (ii > 0) {

					if (!(szCHK_YS_STK_COL_GP+szCHK_YS_STK_BED_NO+szCHK_YS_STK_LYR_NO).equals(szYS_STK_COL_GP+szYS_STK_BED_NO+szYS_STK_LYR_NO)) {
						//이전 Lot 추가
						vcLot.add(jsLot);

						jsLot = JDTORecordFactory.getInstance().createRecordSet("");
						szCHK_YS_STK_COL_GP  = szYS_STK_COL_GP;
						szCHK_YS_STK_BED_NO  = szYS_STK_BED_NO;
						szCHK_YS_STK_LYR_NO  = szYS_STK_LYR_NO;
					}
				} else {
					szCHK_YS_STK_COL_GP  = szYS_STK_COL_GP;
					szCHK_YS_STK_BED_NO  = szYS_STK_BED_NO;
					szCHK_YS_STK_LYR_NO  = szYS_STK_LYR_NO;
				}
				jsLot.addRecord(jrRow);
			}
			
			//마지막 Lot 추가
			vcLot.add(jsLot);
			
			commUtils.printLog(logId, methodNm, "S-");

			return vcLot;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
		
	/**
	 * 설비보급 - 장입보급기준 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updAutoSupRule(GridData gdReq) throws DAOException {
		String methodNm = "기준관리 - 수정[BtYsJspSeEJB.updAutoSupRule] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			//기준 수정 
			jrParam.setField("ITEM"			, gdReq.getParam("ITEM") );
			jrParam.setField("REPR_CD_GP"	, gdReq.getParam("REPR_CD_GP") );
			jrParam.setField("CD_GP"		, gdReq.getParam("CD_GP") );	
			
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYsRule", logId, methodNm, "기준관리 수정");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updAutoSupRule
	
	/**
	 * 장입실적 BACKUP
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord chgWrBackUp(GridData gdReq) throws DAOException {
		String methodNm = "장입실적 BACKUP[BtYsJspSeEJB.chgWrBackUp] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			//전송 할  레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				jrParam.setField("CHG_SUP_PROG_STAT", "30"); 
				jrParam.setField("SSTL_NO", commUtils.getValue(gdReq, "SSTL_NO", ii));
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSCUJ032Backup", jrParam));				
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of chgWrBackUp

	/**
	 * 대차하차작업예약 등록 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord insTCarUdWrk(GridData gdReq) throws DAOException {
		String methodNm = "대차하차작업예약 등록[BtYsJspSeEJB.insTCarUdWrk] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			
			String sYS_STK_COL_GP = commUtils.trim(gdReq.getParam("YS_STK_COL_GP"));
			String ohcSchCd = sYS_STK_COL_GP + "LM"; //스케줄코드 (ex:CATC01LM)
			String ohcWbookId = commDao.getSeqId(logId, methodNm, "WrkBook"); //OHC crane 용 작업예약ID
			
			jrParam.setField("OHC_YD_WBOOK_ID"	, ohcWbookId);  	// crane 작업예약ID
			jrParam.setField("OHC_YD_SCH_CD"	, ohcSchCd); 		// crane 스케줄 코드
			jrParam.setField("YS_STK_COL_GP"	, sYS_STK_COL_GP);
			
			jrParam.setField("SSTL_NO1"			, ""); 	
			jrParam.setField("SSTL_NO2"			, ""); 	
			jrParam.setField("SSTL_NO3"			, ""); 	
			jrParam.setField("SSTL_NO4"			, ""); 	
			jrParam.setField("SSTL_NO5"			, ""); 	
			jrParam.setField("SSTL_NO6"			, ""); 	
			jrParam.setField("SSTL_NO7"			, ""); 	
			
			int rowCnt =  Integer.parseInt(commUtils.trim(gdReq.getParam("YD_STK_BED_STL_SH")));
			String sstl_No ="";
			for (int ii = 1; ii <= rowCnt; ii++) {
				sstl_No = gdReq.getParam("SSTL_NO"+ ii );
				jrParam.setField("SSTL_NO"+ii	, sstl_No );
			}
			
			//1.1) OHC Crane 작업예약재료 생성 
			commDao.insert(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.insOhcLmWbMtl", logId, methodNm, "OHC Crane 작업예약재료 생성");
			
			//1.2) OHC Crane 작업예약  생성 
			commDao.insert(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.insOhcLmWbook", logId, methodNm, "OHC Crane 작업예약  생성");
			
			//2) OHC Crane 스케줄 Main 호출
			jrParam.setField("JMS_TC_CD", "YSYSJ202");
			jrParam.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()  ); //JMSTC생성일시
			jrParam.setField("YD_WBOOK_ID"       , ohcWbookId ); //야드작업예약ID
			jrParam.setField("YD_SCH_CD"         , ohcSchCd   ); //야드스케쥴코드
			jrParam.setField("YD_EQP_ID"         , ""   ); //야드설비ID
			jrParam.setField("YD_SCH_ST_GP"      , "A" ); //야드스케쥴기동구분
			jrParam.setField("YD_SCH_REQ_GP"     , "M"); //야드스케쥴요청구분					
			
			jrRtn = commUtils.addSndData(jrRtn, jrParam);
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of insTCarUdWrk
	
	/**
	 * 기준관리 - 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updYsRule(GridData gdReq) throws DAOException {
		String methodNm = "기준관리 - 수정[BtYsJspSeEJB.updYsRule] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				//기준 수정 
				jrParam.setField("ITEM"			, commUtils.getValue(gdReq, "ITEM", ii)); 
				jrParam.setField("REPR_CD_GP"	, commUtils.getValue(gdReq, "REPR_CD_GP", ii) ); 
				jrParam.setField("CD_GP"		, commUtils.getValue(gdReq, "CD_GP", ii) ); 
				
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYsRule", logId, methodNm, "기준관리 수정");
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updYsRule
	
	/**
	 * 저장위치 수정 - 저장품등록 및 변경전 정합성 체크
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord insBtYsStock(GridData gdReq) throws DAOException {
		String methodNm = "저장위치 수정 - 저장품등록 및 변경전 정합성 체크[BtYsJspSeEJB.insBtYsStock] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;
			JDTORecordSet jsTemp = null;
			JDTORecord jrTemp = null;
			
			String szStlNo = null;
			String szYsStkColGp = null;
			String szYsStkBedNo = null;
			String szYsStkLyrNo = null;
			String szYsStkSeqNo = null;
			
			String szOldStlNo = null;
			String szOldYsStkColGp = null;
			String szOldYsStkBedNo = null;
			String szOldYsStkLyrNo = null;
			String szOldYsStkSeqNo = null;
			
			String szFromStlNo = null;
			String szFromYsStkColGp = null;
			String szFromYsStkBedNo = null;
			String szFromYsStkLyrNo = null;
			String szFromYsStkSeqNo = null;
			
			String szStkStlNo = null;
			String szCurrProgCd = null;
			String szOrdYeojaeGp = null;
			String szWbookId = null;
			String szCrnSchId = null;
			String szToLocMtlStat = null;
			
			String szModGp = null; //작업구분
			String sFromLoc = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				szStlNo 			= commUtils.getValue(gdReq, "SSTL_NO", ii);
				szYsStkColGp 		= commUtils.getValue(gdReq, "YS_STK_COL_GP", ii);
				szYsStkBedNo 		= commUtils.getValue(gdReq, "YS_STK_BED_NO", ii);
				szYsStkLyrNo 		= commUtils.getValue(gdReq, "YS_STK_LYR_NO", ii);
				szYsStkSeqNo 		= commUtils.getValue(gdReq, "YS_STK_SEQ_NO", ii);
				
				szOldStlNo 			= commUtils.getValue(gdReq, "OLD_SSTL_NO", ii);
				szOldYsStkColGp 	= commUtils.getValue(gdReq, "OLD_YS_STK_COL_GP", ii);
				szOldYsStkBedNo 	= commUtils.getValue(gdReq, "OLD_YS_STK_BED_NO", ii);
				szOldYsStkLyrNo 	= commUtils.getValue(gdReq, "OLD_YS_STK_LYR_NO", ii);
				szOldYsStkSeqNo 	= commUtils.getValue(gdReq, "OLD_YS_STK_SEQ_NO", ii);
				
				szFromStlNo			= commUtils.getValue(gdReq, "FROM_SSTL_NO", ii);
				szFromYsStkColGp 	= commUtils.getValue(gdReq, "FROM_YS_STK_COL_GP", ii);
				szFromYsStkBedNo 	= commUtils.getValue(gdReq, "FROM_YS_STK_BED_NO", ii);
				szFromYsStkLyrNo 	= commUtils.getValue(gdReq, "FROM_YS_STK_LYR_NO", ii);
				szFromYsStkSeqNo 	= commUtils.getValue(gdReq, "FROM_YS_STK_SEQ_NO", ii);
				
				if(szStlNo.equals(szOldStlNo)
						&& szYsStkColGp.equals(szOldYsStkColGp)
						&& szYsStkBedNo.equals(szOldYsStkBedNo)
						&& szYsStkLyrNo.equals(szOldYsStkLyrNo)
						&& szYsStkSeqNo.equals(szOldYsStkSeqNo)) {
					//변경사항이 없음으로 Skip 한다.
					continue;
				}
				
				if("".equals(szStlNo) && !"".equals(szOldStlNo) ) {
					//삭제처리
					szModGp = "DELETE";
					jrParam.setField("SSTL_NO"	, szOldStlNo );
				} 
				
				if(!"".equals(szStlNo) && "".equals(szOldStlNo) && "".equals(szFromStlNo)) {
					//추가처리
					szModGp = "ADD";
					jrParam.setField("SSTL_NO"	, szStlNo );
				} 
				
				if(!"".equals(szStlNo) && "".equals(szOldStlNo) && szStlNo.equals(szFromStlNo)) {
					//이동처리
					szModGp = "MOVE";
					jrParam.setField("SSTL_NO"	, szStlNo );
				} 
			
				if(!szYsStkColGp.equals(szOldYsStkColGp)
						|| !szYsStkBedNo.equals(szOldYsStkBedNo)
						|| !szYsStkLyrNo.equals(szOldYsStkLyrNo)
						|| !szYsStkSeqNo.equals(szOldYsStkSeqNo)
						) {
					//SEQ변경처리 UP,DOWN
					szModGp = "UPDOWN";
					jrParam.setField("SSTL_NO"	, szStlNo );
				}
				
				jrParam.setField("YS_STK_COL_GP"	, szYsStkColGp );
				jrParam.setField("YS_STK_BED_NO"	, szYsStkBedNo );
				jrParam.setField("YS_STK_LYR_NO"	, szYsStkLyrNo );
				jrParam.setField("YS_STK_SEQ_NO"	, szYsStkSeqNo );
				
				//제품번호가 야드저장품에 존재하는지, 작업예약과 크레인스케줄에 잡혀있는 대상인지를 가져오고 BILLET공통에서 현재진도코드를 가져온다. 
				jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.getIsEnableStlNo", logId, methodNm, "제품번호 작업대상여부 조회");
				
				if (jsTemp != null && jsTemp.size() > 0) {
					jrTemp = jsTemp.getRecord(0);

					szStkStlNo		= commUtils.trim(jrTemp.getFieldString("SSTL_NO"));
					szCurrProgCd	= commUtils.trim(jrTemp.getFieldString("CURR_PROG_CD"));
					szOrdYeojaeGp	= commUtils.trim(jrTemp.getFieldString("ORD_YEOJAE_GP"));
					szWbookId		= commUtils.trim(jrTemp.getFieldString("YD_WBOOK_ID"));
					szCrnSchId		= commUtils.trim(jrTemp.getFieldString("YD_CRN_SCH_ID"));
					szToLocMtlStat  = commUtils.trim(jrTemp.getFieldString("TOLOC_MTL_STAT"));

			    } else {
					throw new Exception("제품번호로 저장품 및 작업대상여부 조회시 에러가 발생했습니다!");
			    }				
				
				//저장품에 존재하는 제품번호인지 체크
				if("ADD".equals(szModGp)||"MOVE".equals(szModGp)) {
					if("".equals(szStkStlNo)) {
                        //저장품 등록
						commDao.insert(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.insBtYdStock", logId, methodNm, "저장품 등록");
					}
				}
				
				//추가,삭제,이동 모두 크레인스케줄에 작업대상인지 체크
				if(!"".equals(szCrnSchId)) {
					throw new Exception("제품번호 : "+jrParam.getFieldString("SSTL_NO")+" 가 크레인스케줄("+szCrnSchId+")에 작업대상으로 잡혀있습니다! 크레인작업이 완료된 이후나 크래인작업을 취소 한 후 수정이 가능합니다.");
				}
				
				//삭제는 작업예약에 대상으로 잡혀있으면 삭제 불가함
				if("DELETE".equals(szModGp) && !"".equals(szWbookId) && "C".equals(szToLocMtlStat)) {
					throw new Exception("제품번호 : "+jrParam.getFieldString("SSTL_NO")+" 가 작업예약("+szWbookId+")에 작업대상으로 잡혀있습니다! 작업예약을 취소한 후 삭제가 가능합니다.");
				}
				
				if("ADD".equals(szModGp)||"MOVE".equals(szModGp)) {
					//TO위치의 재료상태가 'E' 가 아니면 작업할 수 없음
					if(!"E".equals(szToLocMtlStat) ) {
						throw new Exception("TO 위치의 재료상태가 " + szToLocMtlStat + " 입니다. 등록(이동) 작업을 할 수 없습니다.");
					}
				} else {
					//TO위치의 재료상태가 'U'나 'D'일 경우  수정작업을 할 수 없음
					//if("U".equals(szToLocMtlStat) || "D".equals(szToLocMtlStat) ) {
					//	throw new Exception("TO 위치의 재료상태가 " + szToLocMtlStat + " 로 변경되었습니다. 삭제(변경) 작업을 할 수 없습니다.");
					//}
				}
				
				//이동인 경우 From위치에 szStlNo가 적치중 인지 확인
				if("MOVE".equals(szModGp)) {
					jrParam.setField("YS_STK_COL_GP"		, szFromYsStkColGp );
					jrParam.setField("YS_STK_BED_NO"		, szFromYsStkBedNo );
					jrParam.setField("YS_STK_LYR_NO"		, szFromYsStkLyrNo );
					jrParam.setField("YS_STK_SEQ_NO"		, szFromYsStkSeqNo );

					//From 위치 확인 하기 
					jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStkLyr", logId, methodNm, "적치단정보조회");
					
					if (jsTemp != null && jsTemp.size() > 0) {
						jrTemp = jsTemp.getRecord(0);

						if(!szStlNo.equals(commUtils.trim(jrTemp.getFieldString("SSTL_NO")))) {
							
							sFromLoc = szFromYsStkColGp + "-" 
									 + szFromYsStkBedNo + "-"
									 + szFromYsStkLyrNo + "-"
									 + szFromYsStkSeqNo;
							
							throw new Exception("From 위치["+sFromLoc+"]의 재료번호가 [" + szStlNo + "]가 아닙니다. Crane작업이나 다른 작업자에 의해 이미 수정되었습니다.");
						}

				    } else {
						throw new Exception("From 위치 조회시 에러가 발생했습니다!");
				    }				
				}

				//SEQ변경처리 UP,DOWN 인 경우 이전위치에 szStlNo가 적치중 인지 확인
				if("UPDOWN".equals(szModGp)) {
					
					jrParam.setField("YS_STK_COL_GP"		, szOldYsStkColGp );
					jrParam.setField("YS_STK_BED_NO"		, szOldYsStkBedNo );
					jrParam.setField("YS_STK_LYR_NO"		, szOldYsStkLyrNo );
					jrParam.setField("YS_STK_SEQ_NO"		, szOldYsStkSeqNo );

					//From 위치 확인 하기 
					jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStkLyr", logId, methodNm, "적치단정보조회");
					
					if (jsTemp != null && jsTemp.size() > 0) {
						jrTemp = jsTemp.getRecord(0);

						if(!szStlNo.equals(commUtils.trim(jrTemp.getFieldString("SSTL_NO")))) {
							
							sFromLoc = szOldYsStkColGp + "-" 
									 + szOldYsStkBedNo + "-"
									 + szOldYsStkLyrNo + "-"
									 + szOldYsStkSeqNo;
					
							throw new Exception("이전위치["+sFromLoc+"]의 재료번호가 [" + szStlNo + "]가 아닙니다. Crane작업이나 다른 작업자에 의해 이미 수정되었습니다.");
						}

				    } else {
						throw new Exception("이전(Old) 위치 조회시 에러가 발생했습니다!");
				    }				
				}
				
				//DELETE인 경우 이전위치에 szOldStlNo가 적치중 인지 확인
				if("DELETE".equals(szModGp)) {
					
					jrParam.setField("YS_STK_COL_GP"		, szYsStkColGp );
					jrParam.setField("YS_STK_BED_NO"		, szYsStkBedNo );
					jrParam.setField("YS_STK_LYR_NO"		, szYsStkLyrNo );
					jrParam.setField("YS_STK_SEQ_NO"		, szYsStkSeqNo );

					//From 위치 확인 하기 
					jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStkLyr", logId, methodNm, "적치단정보조회");
					
					if (jsTemp != null && jsTemp.size() > 0) {
						jrTemp = jsTemp.getRecord(0);

						if(!szOldStlNo.equals(commUtils.trim(jrTemp.getFieldString("SSTL_NO")))) {
							
							sFromLoc = szYsStkColGp + "-" 
									 + szYsStkBedNo + "-"
									 + szYsStkLyrNo + "-"
									 + szYsStkSeqNo;
							
							throw new Exception("현재 위치["+sFromLoc+"]의 재료번호가 [" + szOldStlNo + "]가 아닙니다. Crane작업이나 다른 작업자에 의해 이미 수정되었습니다.");
						}

				    } else {
						throw new Exception("현재 위치 조회시 에러가 발생했습니다!");
				    }				
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of insBtYsStock
	
	/**
	 * Carry-In완료전송
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord sndCarryInDone(GridData gdReq) throws DAOException {
		String methodNm = "Carry-In완료전송[BtYsJspSeEJB.sndCarryInDone] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			
			
			//재료번호 1~10까지 미리 "" 값으로 설정
			jrParam.setField("SSTL_NO1", "");
			jrParam.setField("SSTL_NO2", "");
			jrParam.setField("SSTL_NO3", "");
			jrParam.setField("SSTL_NO4", "");
			jrParam.setField("SSTL_NO5", "");
			jrParam.setField("SSTL_NO6", "");
			jrParam.setField("SSTL_NO7", "");
			jrParam.setField("SSTL_NO8", "");
			jrParam.setField("SSTL_NO9", "");
			jrParam.setField("SSTL_NO10", "");
			
			//전송 할  레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				jrParam.setField("SSTL_NO"+(ii+1), commUtils.getValue(gdReq, "SSTL_NO", ii));
			}

			jrParam.setField("YD_STK_BED_STL_SH", ""+rowCnt);
			jrParam.setField("YD_EQP_WRK_SH", ""+rowCnt);
			jrParam.setField("YD_STK_COL_GP", gdReq.getParam("YS_STK_COL_GP") );
			jrParam.setField("YD_STK_BED_NO", "01");
			
			//장입 Carry-in 완료 송신
			jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSM5L102BackUp", jrParam));
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of sndCarryInDone
	
	/**
	 * Carry-Out완료전송
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord sndCarryOutDone(GridData gdReq) throws DAOException {
		String methodNm = "Carry-Out완료전송[BtYsJspSeEJB.sndCarryOutDone] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;
			
			String szYsStkColGp = null;		
			
			int    iWkShCnt = 0;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			
			
			szYsStkColGp = gdReq.getParam("YS_STK_COL_GP");
			
			//레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			
			//BILLET 장입이상재 추출대에서의 삭제 처리시 소형압연L2로 장입이상재 CARRY-OUT 완료 실적을 전송한다.
			if("LB".equals(szYsStkColGp.substring(2,4))) {
				
				//재료번호 1~10까지 미리 "" 값으로 설정
				jrParam.setField("SSTL_NO1", "");
				jrParam.setField("SSTL_NO2", "");
				jrParam.setField("SSTL_NO3", "");
				jrParam.setField("SSTL_NO4", "");
				jrParam.setField("SSTL_NO5", "");
				jrParam.setField("SSTL_NO6", "");
				jrParam.setField("SSTL_NO7", "");
				jrParam.setField("SSTL_NO8", "");
				jrParam.setField("SSTL_NO9", "");
				jrParam.setField("SSTL_NO10", "");
				
				for (int ii = 0; ii < rowCnt; ii++) {
					
					if(!"".equals(commUtils.getValue(gdReq, "OLD_SSTL_NO", ii))) {
						iWkShCnt++;
						jrParam.setField("SSTL_NO"+(ii+1), commUtils.getValue(gdReq, "OLD_SSTL_NO", ii));
					}
				}					

				if(iWkShCnt > 0) {
					jrParam.setField("YD_STK_BED_STL_SH", ""+iWkShCnt);
					jrParam.setField("YD_EQP_WRK_SH", ""+iWkShCnt);
					jrParam.setField("YD_STK_COL_GP", szYsStkColGp );
					jrParam.setField("YD_STK_BED_NO", "01");
					
					//장입이상재 Carry-out 완료 송신
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSM5L101BackUp", jrParam));
				}
			}
			
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of sndCarryOutDone

	/**
	 * 설비인출 - 입고동 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updRcptBay(GridData gdReq) throws DAOException {
		String methodNm = "설비인출 - 입고동 변경[BtYsJspSeEJB.updRcptBay] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			//기준 수정 
			jrParam.setField("ITEM"			, gdReq.getParam("ITEM") );
			jrParam.setField("REPR_CD_GP"	, gdReq.getParam("REPR_CD_GP") );
			jrParam.setField("CD_GP"		, gdReq.getParam("CD_GP") );	
			
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYsRule", logId, methodNm, "기준관리 수정");

			
			jrParam.setField("AIM_BAY"	,  gdReq.getParam("CHK_RCPT_BAY") ); //목표동
			jrParam.setField("GROUP_ID"	,  gdReq.getParam("CHK_HEAT_GP_YN") ); //그룹ID (HEAT구분YN)

			//전송 Data 라우팅 지시 생성
			jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN2L006", jrParam));
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updRcptBay
	
	/**
	 * 설비인출 - 크레인 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updBayActiveCrn(GridData gdReq) throws DAOException {
		String methodNm = "설비인출 - 크레인 변경[BtYsJspSeEJB.updBayActiveCrn] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			//기준 수정 
			jrParam.setField("ITEM"			, gdReq.getParam("ITEM") );
			jrParam.setField("REPR_CD_GP"	, gdReq.getParam("REPR_CD_GP") );
			jrParam.setField("CD_GP"		, gdReq.getParam("CD_GP") );	
			
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYsRule", logId, methodNm, "기준관리 수정");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updBayActiveCrn

	/**
	 * 설비인출 - HEAT구분YN 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updHeatGpYn(GridData gdReq) throws DAOException {
		String methodNm = "설비인출 - HEAT구분YN 변경[BtYsJspSeEJB.updHeatGpYn] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			//기준 수정 
			jrParam.setField("ITEM"			, gdReq.getParam("ITEM") );
			jrParam.setField("REPR_CD_GP"	, gdReq.getParam("REPR_CD_GP") );
			jrParam.setField("CD_GP"		, gdReq.getParam("CD_GP") );	
			
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYsRule", logId, methodNm, "기준관리 수정");

			jrParam.setField("AIM_BAY"	,  gdReq.getParam("CHK_RCPT_BAY") ); //목표동
			jrParam.setField("GROUP_ID"	,  gdReq.getParam("CHK_HEAT_GP_YN") ); //그룹ID (HEAT구분YN)

			//전송 Data 라우팅 지시 생성
			jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN2L006", jrParam));
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updHeatGpYn

	/**
	 * 설비인출 - 기준전송
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord sndYSN2L006(GridData gdReq) throws DAOException {
		String methodNm = "설비인출 - 기준전송[BtYsJspSeEJB.sndYSN2L006] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			jrParam.setField("AIM_BAY"	,  gdReq.getParam("CHK_RCPT_BAY") ); //목표동
			jrParam.setField("GROUP_ID"	,  gdReq.getParam("CHK_HEAT_GP_YN") ); //그룹ID (HEAT구분YN)

			//전송 Data 라우팅 지시 생성
			jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN2L006", jrParam));
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of sndYSN2L006
	
	/**
	 * 저장위치별 현황 - BED활성상태 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updBedActStat(GridData gdReq) throws DAOException {
		String methodNm = "저장위치별 현황 - BED활성상태 변경[BtYsJspSeEJB.updBedActStat] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			jrParam.setField("YS_STK_COL_GP"		, gdReq.getParam("YS_STK_COL_GP"));
			jrParam.setField("YS_STK_BED_NO"		, gdReq.getParam("YS_STK_BED_NO")); 
			jrParam.setField("YD_STK_BED_ACT_STAT"	, gdReq.getParam("YD_STK_BED_ACT_STAT")); 
			
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBedActStat", logId, methodNm, "BED활성상태 변경");
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updBedActStat
	
	
	
	
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
		BtYsDAO 		dao 			= new BtYsDAO();
		//기본변수 정의
		String methodNm = "특수강야드정합성 - 이상재 조회[BtYsJspFaEJB.getMtlErrorList]";
		String logId = inDto.getIPAddress();
		//로컬변수 정의
	    String      szSEARCH_LIST_GP	= "";
	    String      szYD_BAY_GP 		= "";
		String		szYD_GP				= "";
		int intRtnVal = 0;
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+", inDto);
			
			szYD_GP 				= inDto.getParam("YD_GP");
			szSEARCH_LIST_GP		= inDto.getParam("SEARCH_LIST_GP");
			szYD_BAY_GP				= inDto.getParam("YD_BAY_GP");
			
			outRecSet      = JDTORecordFactory.getInstance().createRecordSet("YD");
			recPara        = JDTORecordFactory.getInstance().create();
			
			recPara.setField("V_SEARCH_LIST_GP",     	szSEARCH_LIST_GP);
		    recPara.setField("V_YD_GP",     	szYD_GP);
			outRecSet = dao.getMtlErrorList(recPara);
			
			
			if (outRecSet != null) {
				retGrid = CmUtil.genGridData(inDto , outRecSet);
				retGrid.addParam("ret", "0");
				retGrid.setStatus("true");
			} else{
				retGrid.addParam("ret", "-1");
				retGrid.setStatus("false");
			}
			
			commUtils.printLog(logId, methodNm, "S-");
		
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return retGrid;
	}//end of getMtlErrorList
	
	
	
	
	/**
	 * 재료 지정 등록/해제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updStockAgsnReg(GridData gdReq) throws DAOException {
		String methodNm = "재료 지정 등록/해제[BtYsJspSeEJB.updStockAgsnReg] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
		
			String vStock_No = gdReq.getParam("V_STL_NOS");
			String vStockList[] = vStock_No.split(",");
				
			for (int i = 0; i < vStockList.length; i++) {	
				//열정보 수정
				
				if(gdReq.getParam("V_GP").equals("1")) {
					jrParam.setField("SSTL_NO"		, vStockList[i]); 
					jrParam.setField("CHK_YN"		, gdReq.getParam("V_CHK_YN")); 
		
					commDao.update(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.updYdStkAsgnReg", logId, methodNm, "재료 지정 등록");
					commUtils.printLog(logId, methodNm, "S-");
				}
				else {
					jrParam.setField("SSTL_NO"		, vStockList[i]); 
					jrParam.setField("CHK_YN"		, null); 
		
					commDao.update(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.updYdStkAsgnReg", logId, methodNm, "재료 지정 해제");
					commUtils.printLog(logId, methodNm, "S-");
				}
			}
			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updStockAgsnReg
	
	
	
	/**
	 * 빌렛정정 야드 투입 우선순위 지정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord insBltShearPrior(GridData gdReq) throws DAOException {
		String methodNm = "빌렛정정 야드 투입 우선순위 지정[BtYsJspSeEJB.insBltShearPrior] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;
			JDTORecordSet jsMsg = null;//JDTORecordFactory.getInstance().createRecordSet("");

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrYdMsg = JDTORecordFactory.getInstance().create();

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			
			String vSpan_id = gdReq.getParam("V_SPAN_ID");
			String vSpanList[] = vSpan_id.split(",");
			
			String vPrior = gdReq.getParam("V_PRIOR");
			String vPriorList[] = vPrior.split(",");
			
			String vCmpl_req_dt1 = gdReq.getParam("V_CMPL_REQ_DT1");
			String vCmplReqDt1List[] = vCmpl_req_dt1.split(",");
			
			String vReq_qty1 = gdReq.getParam("V_REQ_QTY1");
			String vReqQty1List[] = vReq_qty1.split(",");
			
			String vCmpl_req_dt2 = gdReq.getParam("V_CMPL_REQ_DT2");
			String vCmplReqDt2List[] = vCmpl_req_dt2.split(",");
			
			String vReq_qty2 = gdReq.getParam("V_REQ_QTY2");
			String vReqQty2List[] = vReq_qty2.split(",");
			
			for (int i = 0; i < vSpanList.length; i++) {	
				jrParam.setField("GP", vSpanList[i]);
				
				if(vPriorList[i].equals(" ")) {
					vPriorList[i] = "";
				}
				
				jrParam.setField("PRIORITY", vPriorList[i]);
				jrParam.setField("CMPL_REQ_DT1", vCmplReqDt1List[i]);
				jrParam.setField("REQ_QTY1", vReqQty1List[i]);
				jrParam.setField("CMPL_REQ_DT2", vCmplReqDt2List[i]);
				jrParam.setField("REQ_QTY2", vReqQty2List[i]);
				jrParam.setField("USER_ID", gdReq.getParam("V_USER_ID"));
				
				commDao.insert(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.insBltShearPrior", logId, methodNm, "빌렛정정 야드 투입 우선순위 지정");
				
				jrYdMsg.setField("YS_STK_COL_GP"+(i+1) , "GE"+vSpanList[i]);
				jrYdMsg.setField("SPAN_PRIOR"+(i+1) , vPriorList[i]);
			}
			
			//특수강재공야드L2로 전문 전송
			jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN7L002", jrYdMsg));

			commUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updBltShearPrior
	
	
	
	
	/**
	 * 기준관리 - 검색가이드 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updYsRuleSrchGdBt(GridData gdReq) throws DAOException {
		String methodNm = "기준관리 - 검색가이드 수정[BtYsJspSeEJB.updYsRuleSrchGdBt] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			//수정할 레코드 수
			int rowCnt = 0;
			int cdGpCnt = 0;
			
			int ruleCnt = Integer.parseInt(gdReq.getParam("REPR_CD_GP_CNT"));
			String szRuleCdGp = null;
			String szRuleCdContents = null;
			
			for (int jj = 0; jj < ruleCnt; jj++) {
				
				szRuleCdGp = gdReq.getParam("REPR_CD_GP"+(jj+1)); //B00011, B00012, B00013 ...
				szRuleCdContents = gdReq.getParam("REPR_CD_CONTENTS"+(jj+1)); 
				
				//TB_YS_RUEL 에서 해당 REPR_CD_GP 전체 삭제하기
				jrParam.setField("REPR_CD_GP"	, szRuleCdGp ); 
				jrParam.setField("CD_GP"		, "%" );
				
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.delYsRule", logId, methodNm, "기준관리 삭제");
				
				rowCnt = gdReq.getHeader("CHECK").getRowCount();
				cdGpCnt = 0;
				
				//TB_YS_RUEL 에 등록하기
				for (int ii = 0; ii < rowCnt; ii++) {
					
					if("1".equals(commUtils.getValue(gdReq, szRuleCdGp, ii))) {
						
						//기준 등록
						jrParam.setField("REPR_CD_GP"		, szRuleCdGp );
						jrParam.setField("CD_GP"			, commUtils.getValue(gdReq, "YS_STK_BED_NO", ii)); 
						jrParam.setField("ITEM"				, commUtils.getValue(gdReq, "YS_STK_COL_GP", ii)); 
						jrParam.setField("REPR_CD_CONTENTS"	, szRuleCdContents); 
						
						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insYsRule", logId, methodNm, "기준관리 등록");
					}
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updYsRuleSrchGdBt
	
	/**
	 * 차량입고LOT등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord regCarFtmvLot(GridData gdReq) throws DAOException {
		String methodNm = "차량입고LOT등록[BtYsJspSeEJB.regCarFtmvLot] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;
			
			JDTORecord recCarSch        	= null;
			JDTORecord rcvMsgCol			= null;
			JDTORecord rcvMsg				= null;
			
			String szTRN_EQP_CD    			= null;
			String szARR_WLOC_CD			= null;
			String szARR_YD_PNT_CD			= null;
		    String szYD_WBOOK_ID   			= "";
		    String szYD_SCH_CD				= "";
			
			String szMsg           			= null;
			
			JDTORecordSet rsResult 			= null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			String ydPrepSchId = commDao.getSeqId(logId, methodNm, "PrepSch");
			
			if ("".equals(ydPrepSchId)) {
				throw new Exception("입고이송Lot 준비스케쥴ID 생성 실패");
			}
			
			jrParam.setField("YD_PREP_SCH_ID", ydPrepSchId); //야드준비스케쥴ID
			jrParam.setField("YD_SCH_CD", gdReq.getParam("YD_SCH_CD")); //스케줄코드
			jrParam.setField("YD_PREP_WK_ST", commUtils.nvl(gdReq.getParam("YD_PREP_WK_ST"),"")); //야드준비작업상태
			jrParam.setField("YD_AIM_BAY_GP", gdReq.getParam("YD_AIM_BAY_GP")); //목적동

			//등록 할  레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				//준비재료 등록
				jrParam.setField("SSTL_NO"			, commUtils.getValue(gdReq, "SSTL_NO", ii)); 
				jrParam.setField("YD_GP"			, commUtils.getValue(gdReq, "YS_STR_LOC", ii).substring(0,1)); 
				jrParam.setField("YS_STK_COL_GP"	, commUtils.getValue(gdReq, "YS_STR_LOC", ii).substring(0,6)); 
				jrParam.setField("YS_STK_BED_NO"	, commUtils.getValue(gdReq, "YS_STR_LOC", ii).substring(6,8)); 
				jrParam.setField("YS_STK_LYR_NO"	, commUtils.getValue(gdReq, "YS_STK_LYR_NO", ii)); 
				jrParam.setField("YS_STK_SEQ_NO"	, commUtils.getValue(gdReq, "YS_STK_SEQ_NO", ii)); 
				
				commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insPrepMtl", logId, methodNm, "준비재료 등록");
			}
			
			commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insPrepSchAimBay", logId, methodNm, "준비스케줄 등록");
			
			
			//이송LOT를 생성한 동에 도착한 이송차량이 있는지 체크 (차량스케줄에 위치가 이송LOT생성한 동이고 상차도착상태에 작업예약이 없는 스케줄)
			jrParam.setField("YD_GP"		, gdReq.getParam("YD_GP")); //야드구분
			jrParam.setField("YD_BAY_GP"	, gdReq.getParam("YD_BAY_GP")); //동구분
			
			JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarSchArrNoJob", logId, methodNm, "작업예약 없는 도착차량 조회");

			if (jsCarSch != null && jsCarSch.size() > 0) {
			
				jsCarSch.first();
				rcvMsg = jsCarSch.getRecord(); //상차도착전문 정보를 담는다..  	
				
				szTRN_EQP_CD 		= rcvMsg.getFieldString("TRN_EQP_CD");
				szARR_WLOC_CD		= rcvMsg.getFieldString("ARR_WLOC_CD");
				szARR_YD_PNT_CD		= rcvMsg.getFieldString("ARR_YD_PNT_CD");
				

				//운송장비코드로 차량스케줄 조회 --------------------------------------------------------------------------------	    
				jrParam.setField("TRN_EQP_CD", szTRN_EQP_CD);
		    	
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarschDaoTrnEqpCd", logId, methodNm, "차량스케줄을 조회"); 	
		    	
				if (rsResult == null || rsResult.size() < 0) {
					szMsg="["+methodNm+"] 이송Lot생성 후 차량스케줄 조회시 운송장비코드["+szTRN_EQP_CD+"] : parameter error";
					commUtils.printLog(logId, szMsg, "SL");
					throw new Exception(szMsg);
				} else if (rsResult.size() > 1) {
					szMsg= "[" + methodNm + "] 이송Lot생성 후 차량스케줄 조회 시 오류발생 - 운송장비코드로 차량스케줄이 여러건["+rsResult.size()+"]이 존재합니다.";
					commUtils.printLog(logId, szMsg, "SL");
					throw new Exception(szMsg);
				}
		    	
		    	rsResult.first();
		    	recCarSch = rsResult.getRecord(); 


		    	//착지개소코드와 착지야드포인트코드로 적치열을 조회한다. ------------------------------------------------------------
		    	jrParam.setField("WLOC_CD",   szARR_WLOC_CD);
		    	jrParam.setField("YD_PNT_CD", szARR_YD_PNT_CD);

		    	rsResult = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkcolWLocCdandPntCd", logId, methodNm, "적치열 조회"); 
		    	
		    	if (rsResult == null || rsResult.size() <= 0) {
		    		szMsg="["+methodNm+"] 수신된 착지개소코드["+szARR_WLOC_CD+"]와 수신된 착지야드포인트코드["+szARR_YD_PNT_CD+"] 적치열 조회 시 적치열이 존재하지 않습니다.";
		    		commUtils.printLog(logId, szMsg, "SL");
					throw new Exception(szMsg);
		    	}
		    	
		    	rsResult.first();
		    	rcvMsgCol = rsResult.getRecord();
		    	
	    		if(commUtils.trim(rcvMsgCol.getFieldString("YD_STK_COL_ACT_STAT")).equals("N")) {
	    			szMsg="["+methodNm+"] 차량정지위치가 사용 불가상태입니다.";
		    		commUtils.printLog(logId, szMsg, "SL");
					throw new Exception(szMsg);
		    	}
		    	
		    	
		    	//작업예약정보에서 --------------------------------------------------------------------------------------------
		    	//운송장비코드 , 야드차량사용구분으로  조회 
		    	//해당된 작업예약 재료 정보를 가지고 온다
	    		jrParam.setField("TRN_EQP_CD",    szTRN_EQP_CD);
		    
	    		rsResult = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getWorkBookMtlbyCarUsrGpTrnEqpCd", logId, methodNm, "작업예약을 조회"); 
				
		    	if (rsResult == null || rsResult.size() < 0 ) {
					szMsg="["+methodNm+"] 운송장비코드["+szTRN_EQP_CD+"]로 작업예약 조회 시 : parameter error";
					commUtils.printLog(logId, szMsg, "SL");
					throw new Exception(szMsg);
				} else if (rsResult.size() == 0 ){
					
				} else {
					
			    	rsResult.first();
			    	JDTORecord recOutTemp = rsResult.getRecord();
			    	
					szYD_WBOOK_ID	= commUtils.trim(recOutTemp.getFieldString("YD_WBOOK_ID")); 
					szYD_SCH_CD    	= commUtils.trim(recOutTemp.getFieldString("YD_SCH_CD")); 
				}
	    		
	    		rcvMsg.setField("YD_CARLD_WRK_BOOK_ID"	, szYD_WBOOK_ID);
	    		rcvMsg.setField("YD_SCH_CD"				, szYD_SCH_CD);
	    		
	    		//소재차량 공차도착 실적 호출
				EJBConnector ejbConn = new EJBConnector("default", "YsCommCarMvSeEJB", this);
				
				jrRtn = (JDTORecord)ejbConn.trx("procLDMatlCarArr", new Class[] { String.class, JDTORecord.class, JDTORecord.class, JDTORecord.class }, new Object[] { logId, rcvMsg, recCarSch , rcvMsgCol });
	    		
			} else {
				
				//상차위치가 Lot편성 동과 같고 작업예약 없는 상차출발차량 조회 : 이 차량이 들어올 차량임으로 포인트 지시를 할 필요 없음  
				jrParam.setField("YD_GP"		, gdReq.getParam("YD_GP")); //야드구분
				jrParam.setField("YD_BAY_GP"	, gdReq.getParam("YD_BAY_GP")); //동구분
				
				jsCarSch = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarSchStartToLd", logId, methodNm, "상차위치 있고 작업예약 없는 상차출발차량 조회");
				
				if (jsCarSch.size() == 0) {
					
					//차량스케줄 중에 상차출발이면서 도착포인트가 없는 차량이 있으면 포인트 지시 처리
					jsCarSch = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarSchNoLdStopLoc", logId, methodNm, "상차위치 없는 상차출발차량 조회");
					
					if (jsCarSch != null && jsCarSch.size() > 0) {
						
						jsCarSch.first();
						rcvMsg = jsCarSch.getRecord(); 
						
						String sTRN_EQP_CD 		= rcvMsg.getFieldString("TRN_EQP_CD");
						String sYD_CAR_SCH_ID	= rcvMsg.getFieldString("YD_CAR_SCH_ID");
						String sYD_GP			= gdReq.getParam("YD_GP");
						String sTO_LOC 			= gdReq.getParam("YD_GP") + gdReq.getParam("YD_BAY_GP") + "TR1";
						
						String sWLOC_CD		= null;
						String sYD_PNT_CD	= null;
						
						String modifier 			= commUtils.trim(gdReq.getParam("userid"));	//수정자
						
						JDTORecord jrYdMsg 			= commUtils.getParam(logId, methodNm, modifier);
						jrYdMsg.setResultCode(logId);	//Log ID
						jrYdMsg.setResultMsg(methodNm);	//Log Method Name
						
						String currDate 			= commUtils.getDateTime14();	//현재시각
						String sTRN_WRK_FULLVOID_GP = "E";	//공차
						String sSPOS_WLOC_CD		= "";
						String sYD_PNT_CD1			= "";
						String sYD_CARLD_STOP_LOC	= "";
						String sARR_WLOC_CD			= "";
						String sYD_PNT_CD3			= "";
						String sYD_CARUD_STOP_LOC	= "";
						
						//-------------------------------------------------------------------------------------------
						//소재차량Point지시 

						
						//목표지위치로 TB_YS_STKCOL에서 개소코드와 야드포인트를 읽어온다. 
						jrParam.setField("YS_STK_COL_GP", sTO_LOC);
						
						JDTORecordSet jsCol = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkCol1", logId, methodNm, "적치열 개소코드,포인트 조회");
						
						if(jsCol != null && jsCol.size() > 0) {
							sWLOC_CD	= commUtils.trim(jsCol.getRecord(0).getFieldString("WLOC_CD"));
							sYD_PNT_CD	= commUtils.trim(jsCol.getRecord(0).getFieldString("YD_PNT_CD"));
							
							if("".equals(sWLOC_CD) || "".equals(sYD_PNT_CD)) {
								
								throw new Exception(sTO_LOC + " 의 개소코드 또는 야드포인트에 NULL 값이 있습니다.");
							}
							
							if(!"".equals(commUtils.trim(jsCol.getRecord(0).getFieldString("TRN_EQP_CD")))) {
								
								throw new Exception(sTO_LOC + " 에 이미 " + commUtils.trim(jsCol.getRecord(0).getFieldString("TRN_EQP_CD")) + " 운송장비가 점유하고  있습니다.");
							}
							
						} else {
							throw new Exception(sTO_LOC + " 의 개소코드와 야드포인트를 TB_YS_SCKCOL 에서 찾지 못했습니다.");
						}
						
						jrYdMsg.setField("JMS_TC_CD"         	, YsConstant.YSTSJ011);
						jrYdMsg.setField("JMS_TC_CREATE_DDTT"	, currDate    ); //JMSTC생성일시
						jrYdMsg.setField("TRN_EQP_CD"        	, sTRN_EQP_CD); //운송장비코드
						jrYdMsg.setField("WLOC_CD"     	 		, sWLOC_CD);
						jrYdMsg.setField("YD_PNT_CD"     		, sYD_PNT_CD); 
						jrYdMsg.setField("PNT_WO_GP"     		, "A"    	);
						jrYdMsg.setField("PNT_WO_DT"     		, currDate ); 
						
						//전송할 전문에 추가
						jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
						
						String sYD_CARLD_PNT_WO_DT = "";
						String sYD_CARUD_PNT_WO_DT = "";

						//차량스케줄의 개소코드, 야드포인트, 정지위치를 UDPATE 한다.
						if("E".equals(sTRN_WRK_FULLVOID_GP)) { //공차:상차
							sSPOS_WLOC_CD 		= sWLOC_CD;
							sYD_PNT_CD1			= sYD_PNT_CD;	
							sYD_CARLD_STOP_LOC 	= sTO_LOC;
							sYD_CARLD_PNT_WO_DT = currDate;
						} else { //영차:하차
							sARR_WLOC_CD 		= sWLOC_CD;
							sYD_PNT_CD3			= sYD_PNT_CD;	
							sYD_CARUD_STOP_LOC 	= sTO_LOC;
							sYD_CARUD_PNT_WO_DT = currDate;
						}
						
						jrParam.setField("YD_CAR_PROG_STAT"		, "");  //""이면 이전 상태 유지된다.
						jrParam.setField("SPOS_WLOC_CD"			, sSPOS_WLOC_CD);
						jrParam.setField("YD_CARLD_PNT_WO_DT"	, sYD_CARLD_PNT_WO_DT);
						jrParam.setField("YD_PNT_CD1"			, sYD_PNT_CD1);
						jrParam.setField("YD_CARLD_STOP_LOC"	, sYD_CARLD_STOP_LOC); 
						jrParam.setField("ARR_WLOC_CD"			, sARR_WLOC_CD);
						jrParam.setField("YD_CARUD_PNT_WO_DT"	, sYD_CARUD_PNT_WO_DT);
						jrParam.setField("YD_PNT_CD3"			, sYD_PNT_CD3);
						jrParam.setField("YD_CARUD_STOP_LOC"	, sYD_CARUD_STOP_LOC); 
						jrParam.setField("YD_CAR_SCH_ID"		, sYD_CAR_SCH_ID); 
						
						//이송차량스케줄 수정 - 차량포인트 수정
						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updMvCarSchPntWo", logId, methodNm, "차량포이트 지시 수정");
						
						
						jrParam.setField("YD_GP"				, sYD_GP);
						jrParam.setField("TRN_EQP_CD"			, sTRN_EQP_CD);
						jrParam.setField("YS_STK_COL_GP"		, sTO_LOC);
						
						//적치열 포인트지시 예약하기
						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkColPntWo", logId, methodNm, "적치열 포인트지시 예약하기");
						
						//TB_YD_CARPOINT 포인트지시 예약하기 
						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCarPointPntWo", logId, methodNm, "TB_YD_CARPOINT 포인트지시 예약하기");
						
						if("E".equals(sTRN_WRK_FULLVOID_GP)) { //공차:상차

							jrParam.setField("YD_GP", sYD_GP);
							jrParam.setField("YD_SCH_CD", sTO_LOC.substring(0,2) + "TR___M");
							
							JDTORecordSet jsPrepSch = commDao.select(jrParam, "com.inisteel.cim.ys.bl.dao.BlYsDAO.getPrepSchWithOutTrnEqpCd", logId, methodNm, "예약 안걸린 이송LOT ID 조회");

							if(jsPrepSch != null && jsPrepSch.size() > 0) {
								
								String sYD_PREP_SCH_ID	= commUtils.trim(jsPrepSch.getRecord(0).getFieldString("YD_PREP_SCH_ID"));
								
								jrParam.setField("TRN_EQP_CD"			, sTRN_EQP_CD);
								jrParam.setField("YD_PREP_SCH_ID"		, sYD_PREP_SCH_ID);
								
								//TB_YS_PREPSCH 이송LOT 예약하기
								commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updPreSchYdToLocGuide", logId, methodNm, "이송LOT 예약하기");
							}		
						}
						//-------------------------------------------------------------------------------------------
						
						
					}
				}
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of regFtmvLot
	
	/**
	 * 빌렛차량작업 관리-차량회송처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return 
	 * @return 
	 * @return
	 * @throws DAOException
	 */
	public JDTORecord doDelCarSch(GridData gdReq) throws DAOException {
		
		String methodNm = "빌렛차량작업 관리-차량회송처리[BtYsJspSeEJB.doDelCarSch] < " + gdReq.getNavigateValue();
		String logId 			= gdReq.getIPAddress();
		String szMsg 			= null;
		String szARR_YD_PNT_CD 	= null;
		String szCurrDate 		= commUtils.getCurDate("yyyyMMddHHmmss");
		int intRtnVal 			= 0;
		
		JDTORecord sndRecord	= JDTORecordFactory.getInstance().create();
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);	
			
			String szYD_CAR_SCH_ID 		= commUtils.getValue(gdReq, "YD_CAR_SCH_ID", 0);
			String szYD_WBOOK_ID 		= commUtils.getValue(gdReq, "YD_WBOOK_ID", 0);
			String szYD_CRN_SCH_ID 		= commUtils.getValue(gdReq, "YD_CRN_SCH_ID", 0);
			String szARR_WLOC_CD 		= commUtils.getValue(gdReq, "ARR_WLOC_CD", 0);
			String szTRN_EQP_CD 		= commUtils.getValue(gdReq, "TRN_EQP_CD", 0);
			String szYD_CAR_STOP_LOC 	= commUtils.getValue(gdReq, "YD_CAR_STOP_LOC", 0);
			String szYD_CAR_PROG_STAT 	= commUtils.getValue(gdReq, "YD_CAR_PROG_STAT", 0);
			String szYD_CAR_USE_GP 		= commUtils.getValue(gdReq, "YD_CAR_USE_GP", 0);
			
			szMsg = "차량 STATUS["+szYD_CAR_PROG_STAT+"],차량스케줄ID["+szYD_CAR_SCH_ID+"], 작업예약ID["+szYD_WBOOK_ID+"], 스케쥴ID["+szYD_CRN_SCH_ID+"], 운송장비코드["+szTRN_EQP_CD+"], 착지개소코드["+szARR_WLOC_CD+"]";
			
			commUtils.printLog(logId, szMsg, "SL");
			
			JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
			
			if("B".equals(szYD_CAR_PROG_STAT)||
			   "C".equals(szYD_CAR_PROG_STAT)||
			   "D".equals(szYD_CAR_PROG_STAT)||
			   "E".equals(szYD_CAR_PROG_STAT)){
				/**********************************************************
		    	 * 1. 출발야드 적치열 -> 비활성상태(C) 로 업데이트
		    	 **********************************************************/
				szMsg= "[" + methodNm + "] 출발야드의 적치열["+szYD_CAR_STOP_LOC+"]을 비활성상태로 변경처리 시작 ";
				commUtils.printLog(logId, szMsg, "SL");
				
		    	recInTemp.setField("YS_STK_COL_GP",        szYD_CAR_STOP_LOC);
		    	recInTemp.setField("YD_STK_COL_ACT_STAT",  "C");
		    	recInTemp.setField("YD_CAR_USE_GP",        "");
		    	recInTemp.setField("TRN_EQP_CD",           "");
		    	recInTemp.setField("CAR_NO",               "");
		    	recInTemp.setField("CARD_NO",              "");
		    	recInTemp.setField("MODIFIER", 			   commUtils.trim(gdReq.getParam("userid")));
		    	
		    	intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkcolByColActStatClear", logId, methodNm, "TB_YS_STKCOL 등록");
								
				/**********************************************************
		    	 * 2. 차량포인트통합관리 
		    	 **********************************************************/
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YS_STK_COL_GP", szYD_CAR_STOP_LOC);
				recInTemp.setField("STAT", "C");
				
				intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.carpointstackcolgpupdateCT", logId, methodNm, "저장위치로 초기화 하는 경우(구내운송)");
				
				/**********************************************************
		    	 * 3. 출발야드 적치베드 -> 야드적치베드활성상태(=C(비활성상태), YD_STK_BED_ACT_STAT) 
		    	 *                     및 BED중량MAX(=기본값, YD_STK_BED_WT_MAX) 으로 업데이트
		    	 **********************************************************/
				szMsg= "[" + methodNm + "] 출발야드의 적치열["+szYD_CAR_STOP_LOC+"]의 베드를 비활성상태와 BED중량MAX기본값으로 변경처리 시작 ";
				commUtils.printLog(logId, szMsg, "SL");
				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_STK_BED_WT_MAX", YsConstant.YD_STK_BED_WT_MAX_DEFAULT);
				recInTemp.setField("YS_STK_COL_GP", szYD_CAR_STOP_LOC);
				recInTemp.setField("YD_STK_BED_ACT_STAT", "C");
				
				intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkbedYdStkColGp", logId, methodNm, "TB_YS_STKBED 등록");
				
				/**********************************************************
		    	 * 4. 출발야드 적치단 -> 야드적치단활성상태(=C(비활성상태), YD_STK_LYR_ACT_STAT) 
		    	 *                   및 야드적치단재료상태(=E(적치가능), YD_STK_LYR_MTL_STAT) 로 업데이트
		    	 **********************************************************/
				szMsg= "[" + methodNm + "] 출발야드의 적치열["+szYD_CAR_STOP_LOC+"]의 적치단을 비활성상태로 변경처리 시작 ";
				commUtils.printLog(logId, methodNm, "SL");
				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setResultCode(logId);	//Log ID
				recInTemp.setResultMsg(methodNm);	//Log Method Name
				recInTemp.setField("YS_STK_COL_GP", szYD_CAR_STOP_LOC);
				recInTemp.setField("YD_STK_LYR_ACT_STAT", "C");
				recInTemp.setField("SSTL_NO", "");
				recInTemp.setField("YD_STK_LYR_MTL_STAT", "E");
				
				intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColGpClear", logId, methodNm, "TB_YS_STKLYR 등록");
				
			}
			
			/**********************************************************
			* 5. 기존 이송차량스케줄/재료 삭제
			**********************************************************/
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);

			//이송차량재료 초기화
			commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarSchMtl", logId, methodNm, "이송차량재료 초기화");

			//이송차량스케줄 초기화
			commDao.update(recInTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarSch", logId, methodNm, "이송차량스케줄 초기화");
			
			
			/**********************************************************
	    	 * 6. 빌렛옥외야드(L2)이송시 송신
	    	 **********************************************************/
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
			recInTemp.setField("YD_INFO_SYNC_CD", "7" );
			
			//전송 Data 생성
			sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2("YSN4L002", recInTemp));
			
           /**********************************************************
			* 7.소재차량회송 하차완료 전송 시작
			**********************************************************/
			sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL3("YSTSJ016", recInTemp));
						
			commUtils.printLog(logId, methodNm, "S-");
				
			return sndRecord;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
		
	/**
	 * 특수강야드진도별재고현황
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getYsTotalMgtList(JDTORecord inDto) throws DAOException {
		
		String logId = commUtils.getLogId();
		JDTORecordSet    outRecSet       = JDTORecordFactory.getInstance().createRecordSet("YS");
		
		String szMsg        	= "";
		String methodNm = "특수강야드진도별재고현황[BtYsJspSeEJB.getYsTotalMgtList] < " + "Facade" + " < " + " jsp";
		String sDD_CHK 			= "";
		String sDD_CHK2 		= "";
		String sDD_CHK3 		= "";
		
		int intRtnVal = 0;
		
		try {			
			commUtils.printLog(logId, methodNm, "S+", inDto);	
			sDD_CHK	 =	commUtils.trim(inDto.getFieldString("DD_CHK"));   // 기준일 CHECK
			sDD_CHK2 =  commUtils.trim(inDto.getFieldString("DD_CHK2"));  // 기준일 CHECK2
			sDD_CHK3 =  commUtils.trim(inDto.getFieldString("DD_CHK3"));  // 적치기준 CHECK3
			
			if(sDD_CHK.equals("N")) {   							// 현재
				JDTORecord jrParam = commUtils.getParam(logId, methodNm, logId);
				jrParam.setField("CHK", 			sDD_CHK3);
				
				if(sDD_CHK2.equals("N")) { 
					outRecSet = commDao.select(jrParam, "com.inisteel.cim.ys.dao.ydeqpdao.getYsTotalMgtList", logId, methodNm, "904");
				}else{
					outRecSet = commDao.select(jrParam, "com.inisteel.cim.ys.dao.ydeqpdao.getYsTotalMgtList2", logId, methodNm, "906");
				}
				
			} else {												// 기준일 
				JDTORecord jrParam = commUtils.getParam(logId, methodNm, logId);
				jrParam.setField("CHK", 			sDD_CHK3);
				jrParam.setField("YD_INV_DATE", 	commUtils.trim(inDto.getFieldString("YD_WRK_HDS_DD")));
			    
			    if(sDD_CHK2.equals("N")) { 
			    	outRecSet = commDao.select(jrParam, "com.inisteel.cim.ys.dao.ydeqpdao.getYsTotalMgtListXL", logId, methodNm, "905");
			    }else{
			    	outRecSet = commDao.select(jrParam, "com.inisteel.cim.ys.dao.ydeqpdao.getYsTotalMgtListXL2", logId, methodNm, "907");
			    }
			}
			if (intRtnVal < 0) {
				if (intRtnVal == -1) {
					szMsg = "routine error!!!, ErrorCode:" + intRtnVal;
					commUtils.printLog(logId, szMsg, "ERROR");
				} else {
					szMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
					commUtils.printLog(logId, szMsg, "ERROR");
				}
				//return outRecSet;
				return outRecSet;
			} // end of if
			
			commUtils.printLog(logId, methodNm, "S-");
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {
		}
		return outRecSet;
	}//end of getYsTotalMgtList	
	
	/**
	 *      [A] 오퍼레이션명 : 특수강이송관리 조회
	 *
	 *		MES_PI 2022.09.20 문예섭
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	 *      //PIDEV
	*/
	
	public GridData getYsSpstFtmv(GridData gdReq) throws DAOException {
		try {
			JDTORecordSet jrResult = BtYsDao.getYsSpstFtmv(gdReq);
			GridData gdReturn = OperateGridData.cloneResponseGridData(gdReq);

			//args[] - 1 : 리턴할 GridData,  2 : 디비 결과 List,  3 : JSP에서 받은 GridData
			//3번째 아규먼트가 있었을 경우 JSP에서 받은 파라미터를 리턴할 GridData에 그대로 세팅한다.
	        return CmnUtil.jdtoRecordToGridData(gdReturn, jrResult.toList(), gdReq);
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 특수강이송관리 전송
	 *
	 *		MES_PI 2022.09.20 문예섭
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	 *      //PIDEV
	*/ 
	
	public GridData insYsSpstFtmv(GridData gdReq) throws DAOException {
		try {	
			String modifier   = CmnUtil.nvl(gdReq.getParam("userid"        ), "");
			
			Object[] objs = null;
			for(int i = 0; i < gdReq.getHeader("CHECK").getRowCount(); i++) {
				 
				objs = new Object[]{
						CmnUtil.nvl(gdReq.getHeader("PLNT_TP" ).getValue(i), "")
						,CmnUtil.nvl(gdReq.getHeader("CANCEL_FLAG" ).getValue(i), "")
						,CmnUtil.nvl(gdReq.getHeader("CANCEL_IF_SEQ_ID" ).getValue(i), "")
						,CmnUtil.nvl(gdReq.getHeader("TXN_TYPE" ).getValue(i), "")
						,CmnUtil.nvl(gdReq.getHeader("ERP_HDS_DD" ).getValue(i), "")
						,CmnUtil.nvl(gdReq.getHeader("PRD_ITM_CD" ).getValue(i), "")
						,CmnUtil.nvl(gdReq.getHeader("LOC_CD" ).getValue(i), "")
						,CmnUtil.nvl(gdReq.getHeader("STL_NO" ).getValue(i), "")
						,CmnUtil.nvl(gdReq.getHeader("PROD_QNTY" ).getValue(i), "")
						,CmnUtil.nvl(gdReq.getHeader("UOM" ).getValue(i), "")
						,CmnUtil.nvl(gdReq.getHeader("RCV_PRD_ITM_CD" ).getValue(i), "")
						,CmnUtil.nvl(gdReq.getHeader("RCV_PLNT_TP" ).getValue(i), "")
						,CmnUtil.nvl(gdReq.getHeader("RCV_LOC_CD" ).getValue(i), "")
						,CmnUtil.nvl(gdReq.getHeader("RCV_STL_NO" ).getValue(i), "")
						,CmnUtil.nvl(gdReq.getHeader("HEAT_NO" ).getValue(i), "")
						,CmnUtil.nvl(gdReq.getHeader("ACTL_T_MM" ).getValue(i), "")
						,CmnUtil.nvl(gdReq.getHeader("ACTL_W_MM" ).getValue(i), "")
						,CmnUtil.nvl(gdReq.getHeader("ACTL_DIA_MM" ).getValue(i), "")
						,CmnUtil.nvl(gdReq.getHeader("ACTL_L_MM" ).getValue(i), "")
						,CmnUtil.nvl(gdReq.getHeader("ACTL_WT_KG" ).getValue(i), "")
						,CmnUtil.nvl(gdReq.getHeader("ACTL_WT_CNT" ).getValue(i), "")
						,CmnUtil.nvl(gdReq.getHeader("QT_GRD" ).getValue(i), "")
						,CmnUtil.nvl(gdReq.getHeader("ORD_NO" ).getValue(i), "")
						,CmnUtil.nvl(gdReq.getHeader("ORD_DTL" ).getValue(i), "")
						,CmnUtil.nvl(gdReq.getHeader("IFID" ).getValue(i), "")
						,CmnUtil.nvl(gdReq.getHeader("IF_DATE" ).getValue(i), "")
						,CmnUtil.nvl(gdReq.getHeader("IF_TIME" ).getValue(i), "")
						,CmnUtil.nvl(gdReq.getHeader("IF_MSG" ).getValue(i), "")
						,CmnUtil.nvl(gdReq.getHeader("REGISTER" ).getValue(i), "")
						,CmnUtil.nvl(gdReq.getHeader("REGISTER_NM" ).getValue(i), "")
						,CmnUtil.nvl(gdReq.getHeader("REG_DDTT" ).getValue(i), "")
						,modifier
					};	                                                
				BtYsDao.insYsSpstFtmv(objs);
			}
			return gdReq;
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}
	
	/**
	 * 특수강 박판 선재 창고 저장위치 등록/삭제 정합성 체크
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	
	public String doChkHLocation(JDTORecord recPara) throws DAOException
	{
		String methodNm = "특수강 박판 선재 창고 저장위치 정합성 체크(PDA)[BtYsJspSeEJB.doChkHLocation] < " + recPara.getResultMsg();
		String logId = recPara.getResultCode();
		JDTORecord jrRtn = null;
		try {
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(recPara.getFieldString("userid")));

			String szStlNo = commUtils.trim(recPara.getFieldString("SSTL_NO"));
			String szYsStkColGp = commUtils.trim(recPara.getFieldString("YS_STK_COL_GP"));

			String szYsStrLoc = "";

			String delCheck = "";
			
			String flag = "";
			
			jrParam.setField("SSTL_NO",szStlNo);
			JDTORecordSet jsStkLyrCheck = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYsStkNo", logId, methodNm, "저장품조회");
			
			if (jsStkLyrCheck.size() > 0) {
				szYsStrLoc =  jsStkLyrCheck.getRecord(0).getFieldString("YS_STR_LOC");
			}
			
			
			
			if("KHTR11".equals(szYsStkColGp)){
				//상차처리
				if(!"KHTY1101011".equals(szYsStrLoc) ){
					// 지번 > 상차 하는 경우 
					flag = "D";
				}
				else  
				{
					flag = "OK";
					return flag;
				}
			}
			else if("KHTY11".equals(szYsStkColGp)){
				//대기장처리
				flag = "D";
				
			}
			else {
				//H동 지번인지 
				if(szYsStrLoc.matches("KH\\d{9}")){
					//삭제가능 확인
				
					flag = "D";
					jrParam.setField("YS_STK_COL_GP", szYsStrLoc.substring(0,6));
					jrParam.setField("YS_STK_BED_NO", szYsStrLoc.substring(6,8));
					jrParam.setField("YS_STK_LYR_NO", szYsStrLoc.substring(8,10));
					jrParam.setField("GUBUN", commUtils.trim(flag));
					
					 jsStkLyrCheck = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStrLocCheckHdongRegPda", logId, methodNm, "적치단정보조회");
					if (jsStkLyrCheck.size() > 0) {
						delCheck =  jsStkLyrCheck.getRecord(0).getFieldString("RESULT");
						if("N".equals(delCheck)) {
							return flag;  
						}
					}
				}
				flag = "I";
			}
			if(flag.equals("D")){
				jrParam.setField("YS_STK_COL_GP", szYsStrLoc.substring(0,6));
				jrParam.setField("YS_STK_BED_NO", szYsStrLoc.substring(6,8));
				jrParam.setField("YS_STK_LYR_NO", szYsStrLoc.substring(8,10));
				jrParam.setField("GUBUN", commUtils.trim(flag));
			}
			else{
			jrParam.setField("YS_STK_COL_GP", commUtils.trim(recPara.getFieldString("YS_STK_COL_GP")));
			jrParam.setField("YS_STK_BED_NO", commUtils.trim(recPara.getFieldString("YS_STK_BED_NO")));
			jrParam.setField("YS_STK_LYR_NO", commUtils.trim(recPara.getFieldString("YS_STK_LYR_NO")));
			jrParam.setField("GUBUN", commUtils.trim(flag));
			}
			 jsStkLyrCheck = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStrLocCheckHdongRegPda", logId, methodNm, "적치단정보조회");
			
			if (jsStkLyrCheck.size() > 0) {
				delCheck =  jsStkLyrCheck.getRecord(0).getFieldString("RESULT");
				if("N".equals(delCheck)) {
					return flag;
				}
			}
			
			//정상처리를 리턴한다.

			commUtils.printLog(logId, methodNm, "S-");

			return "OK";
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
		
	}
}	