/**
 * @(#)BCoilJspSeEJBSBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2017/02/02
 *
 * @description      B열연 COIL 야드 화면 관리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2017/02/02   정종균      조병기      최초 등록
 * 
 */
package com.inisteel.cim.ym.bcoil.session;
 
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.CmUtil;
import com.inisteel.cim.ts.common.TsCommUtil;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;
import com.inisteel.cim.yf.common.dao.YfCommDAO;
import com.inisteel.cim.ym.bcommon.util.YmConstant;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO;
import com.inisteel.cim.ym.bcommon.util.YmCommUtils;
import com.inisteel.cim.ym.bcommon.dao.YmCommDAO;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.yd.message.MessageSenderTalk;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.record.JDTOException;
import jspeed.base.util.StringHelper;
import jspeed.base.util.DateHelper;

/**
 *      [A] 클래스명 : 1열연 COIL 야드 화면관리 Session EJB 
 *
 * @ejb.bean name="BCoilJspSeEJB" jndi-name="BCoilJspSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300" 
 * @ejb.transaction type="Required"
*/
public class BCoilJspSeEJBSBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;
	private YmCommUtils commUtils = new YmCommUtils();
	private YmCommDAO commDao = new YmCommDAO();
	private String szSessionName = getClass().getName();
	private YmComm ymComm = new YmComm();
	
	
	private YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();	
	private YfCommDAO yfcommDao = new YfCommDAO();
	

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
		String methodNm = "조회[BCoilJspSeEJB.getSelectData] < " + gdReq.getNavigateValue();
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
		String methodNm = "조회[BCoilJspSeEJB.getSelectData] < " + recPara.getResultMsg();
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
	 * IFTest Layout 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updIfTestData(GridData gdReq) throws DAOException {
		String methodNm = "IFTest Layout 변경[BCoilJspSeEJB.updIfTestData] < " + gdReq.getNavigateValue();
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
				
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updIfTestData", logId, methodNm, "IFTest 항목값 수정");
								
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
		String methodNm = "IFTest 전송[BCoilJspSeEJB.sndIfTest] < " + gdReq.getNavigateValue();
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
				
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updIfTestData", logId, methodNm, "IFTest 항목값 수정");
			}

			
			//String ifMthGp    = msgId.substring(4, 5); //IF방법구분(L:EAI, 기타:JMS)
			String ifMthGp    = gdReq.getParam("MTH_GP");
			String ifClassNm  = null;
			String ifMthNm    = null;
			//String ifSndRcvGp = "YM".equals(msgId.substring(0, 2)) ? "S" : "R"; //IF송수신구분(송신, 수신)
			String ifSndRcvGp = gdReq.getParam("SNDRCV_GP");

			//큐에 넣을 데이터를 생성 - Log ID, Method, 수정자 Set
			JDTORecord sndData = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			sndData.setField("JMS_TC_CD"         , msgId                    );
			sndData.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14());
			
			if ("E".equals(ifMthGp) && "S".equals(ifSndRcvGp)) {
				//EAI송신처리 일 경우
				ifClassNm = "YmCommEJB";
				ifMthNm = "sndToEAI";

				//EAI전문 Message
				StringBuffer sbMsg = new StringBuffer();

				for (int ii = 0; ii < rowCnt; ii++) {
					sbMsg = sbMsg.append(gdReq.getHeader("ITM_VAL").getValue(ii));
				}

				sndData.setField("JMS_TC_MESSAGE", sbMsg.toString());
			} else {
				
				if("Y".equals(gdReq.getParam("EFF_YN_TEST"))) {

					//신규모듈 적용여부 테스트 CheckBox 체크시 TB_YM_Z_IF 테이블의 BEF_PGM_NM1, BEF_PGM_NM2 을 읽어 호출 한다.
					ifClassNm = gdReq.getParam("BEF_PGM_NM1");
					ifMthNm = gdReq.getParam("BEF_PGM_NM2");	//EJB Call
					
					String szMsg="["+methodNm+"] ifClassNm:" + ifClassNm + " , ifMthNm:" + ifMthNm ;
					commUtils.printLog(logId, szMsg, "SL");			    		
					
				} else {
				
					//수신 처리방법(Q:JMS Queue, E:EJB Call)이 'E'이고 수신처가 야드이면
					if ("E".equals(gdReq.getParam("TRT_MTH")) ) {
						ifClassNm = "YmCommEJB";
						ifMthNm = "rcvInterface";	//EJB Call
					} else {
						ifClassNm = "YmCommEJB";
						ifMthNm = "sndToJMS";		//JMS송신
					}
				} 

				//EAI송신 외 처리 일 경우
				for (int ii = 0; ii < rowCnt; ii++) {
					sndData.setField(commUtils.trim(gdReq.getHeader("ITM_ID" ).getValue(ii)), commUtils.trim(gdReq.getHeader("ITM_VAL").getValue(ii)));
				}
			}
			
			//송신 공통 EJB를 이용하여 전송
			EJBConnector ejbConn = new EJBConnector("default", ifClassNm, this);
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
		String methodNm = "IFTest EAI전송[BCoilJspSeEJB.sndIfTestEAI] < " + gdReq.getNavigateValue();
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

					//EAI송신처리 일 경우
					sndData.setField("JMS_TC_CD"         , tcMsg.substring(0, 8));
					sndData.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14());
					sndData.setField("JMS_TC_MESSAGE"    , tcMsg);

					//송신 공통 EJB를 이용하여 L2로 전송
					EJBConnector ejbConn = new EJBConnector("default", "YmCommEJB", this);
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
	 * 설비상태 (변경 설비기준조회 )
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updEqpOprnStat(GridData gdReq) throws DAOException {
		String methodNm = "설비상태 변경[BCoilJspFaEJB.updEqpOprnStat] < " + gdReq.getNavigateValue();
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
				
				//설비테이블(TB_YM_EQUIP) 설비상태가 고장이 아니면 작업진행 상태 값으로 변경
				if(!"고장".equals(commUtils.getValue(gdReq, "EQUIP_STATE", ii))) {
					
					if("S".equals(commUtils.getValue(gdReq, "WPROG_STAT", ii))) {
						//S 일경우 설비는 W 로 설정
						jrParam.setField("WPROG_STAT"	,"W"); 
					} else {
						jrParam.setField("WPROG_STAT"	,commUtils.getValue(gdReq, "WPROG_STAT", ii)); 
					}
					jrParam.setField("EQUIP_GP"		,commUtils.getValue(gdReq, "EQUIP_GP", ii)); 
					
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updEqpOprnStat", logId, methodNm, "설비상태 변경");
				}
				
				//크레인스케줄 ID가 있다면 크레인스케쥴 상태 변경
				if(!"".equals(commUtils.getValue(gdReq, "YD_CRN_SCH_ID", ii))) {
				
					jrParam.setField("WPROG_STAT"		,commUtils.getValue(gdReq, "WPROG_STAT", ii)); 
					jrParam.setField("YD_CRN_SCH_ID"	,commUtils.getValue(gdReq, "YD_CRN_SCH_ID", ii)); 
					
					commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updCreSchOprnStat", logId, methodNm, "크레인상태 변경");
				}
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
	 * 야드설비정비등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord insEqpTrblReg(GridData gdReq) throws DAOException {
		String methodNm = "야드설비정비등록[BCoilJspFaEJB.insEqpTrblReg] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//Grid date 를 JDTORecord data 로 변환
			JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			//gdReq.getParam("TRT_MTH")
			
			jrParam.setField("EQUIP_GP"	,gdReq.getParam("EQUIP_GP")); 
			jrParam.setField("DOWN_CD"	,gdReq.getParam("DOWN_CD")); 
			jrParam.setField("DOWN_OCCUR_DDTT"	,gdReq.getParam("DOWN_OCCUR_DDTT")); 
			jrParam.setField("DOWN_OCCUR_WORK_DUTY"	,gdReq.getParam("DOWN_OCCUR_WORK_DUTY")); 
			jrParam.setField("DOWN_OCCUR_WORK_PARTY"	,gdReq.getParam("DOWN_OCCUR_WORK_PARTY")); 
			jrParam.setField("DOWN_RECOVER_CONTENTS"	,gdReq.getParam("DOWN_RECOVER_CONTENTS"));
			jrParam.setField("REGISTER"	,gdReq.getParam("REGISTER"));
			
			commDao.insert(jrParam, inRecord.getFieldString("QUERY_ID"), logId, methodNm, "야드설비정비등록");
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of insEqpTrblReg
	
	
	/**
	 * 야드설비정비상태 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updEqpTrblReg(GridData gdReq) throws DAOException {
		String methodNm = "야드설비정비상태 변경[BCoilJspFaEJB.updEqpTrblReg] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
//			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			jrParam.setField("EQUIP_GP"	,gdReq.getParam("EQUIP_GP")); 
			jrParam.setField("DOWN_CD"	,gdReq.getParam("DOWN_CD")); 
			
			commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updEqpTrblRegji", logId, methodNm, "야드설비정비상태 변경");

			commUtils.printLog(logId, methodNm, "S-");

			return jrParam;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updEqpTrblReg
	
	
	/**
	 * 저장품 작업예약 호출
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */
	public JDTORecord callLoadWbookInfo(GridData gdReq) throws DAOException {
		String methodNm = "출하상차[BCoilJspSeEJB.callLoadWbookInfo] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			jrParam.setField("YD_GP"	 , gdReq.getParam("YD_GP"));
			
			String sYD_GP     = gdReq.getParam("YD_GP");       //야드구분
			String sYD_BAY_GP = gdReq.getParam("V_YD_BAY_GP"); //동구분 
			String sYD_SCH_CD = "";
			
			String sSTOCK_ID       = "";
			String sSTACK_COL_GP   = "";  
			String sSTACK_BED_GP   = "";
			String sSTACK_LAYER_GP = "";
			
			int    iWrkBookCnt	= 0;
			
			/********************************************
			 *  작업예약 생성시 2단 부터 생성후 1단 생성
			 ********************************************/
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int idx = 0; idx < rowCnt; idx++) {

				sYD_SCH_CD      = sYD_GP + commUtils.getValue(gdReq, "STORE_LOC_CD", idx).substring(1, 2) + "TC12UM";
				sSTOCK_ID       = commUtils.getValue(gdReq, "STOCK_ID", idx); 
				sSTACK_COL_GP   = commUtils.getValue(gdReq, "STORE_LOC_CD", idx).substring(0, 6);
				sSTACK_BED_GP   = commUtils.getValue(gdReq, "STORE_LOC_CD", idx).substring(6, 8);
				sSTACK_LAYER_GP = commUtils.getValue(gdReq, "STORE_LOC_CD", idx).substring(8, 10);
				sYD_BAY_GP      = commUtils.getValue(gdReq, "STORE_LOC_CD", idx).substring(1, 2);
				
				String ydWbookId = commDao.getSeqId(logId, methodNm, "WrkBook");
				
				if ("02".equals(sSTACK_LAYER_GP)) {
					/*
					INSERT INTO TB_YM_WRKBOOK ( 
					      YD_WBOOK_ID 
					    , YD_GP 
					    , YD_BAY_GP
					    , YD_SCH_CD
					    , REGISTER 
					    , REG_DDTT 
					    , MODIFIER 
					    , MOD_DDTT 
					    , DEL_YN
					    , YD_AIM_BAY_GP
					    , YD_TO_LOC_GUIDE
					    , YD_WRK_PLAN_TCAR
					    , YD_SCH_PRIOR
					) VALUES (
					      :V_YD_WBOOK_ID
					    , :V_YD_GP
					    , :V_YD_BAY_GP
					    , :V_YD_SCH_CD
					    , :V_MODIFIER
					    , SYSDATE
					    , :V_MODIFIER
					    , SYSDATE
					    , 'N'
					    , 'H'
					    , '3H'
					    , '3XTC02'
					    , (SELECT YD_WRK_CRN_PRIOR FROM TB_YM_SCHEDULERULE WHERE YD_SCH_CD = :V_YD_SCH_CD AND ROWNUM = 1)
					)
					 */
					jrParam.setField("YD_BAY_GP"    , sYD_BAY_GP);
					jrParam.setField("YD_WBOOK_ID"	, ydWbookId); 
					jrParam.setField("YD_SCH_CD"	, sYD_SCH_CD);
					commDao.insert(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.InsertYdWBook", logId, methodNm, "작업예약(TB_YM_WRKBOOK) Table Insert");
					
					/*
					INSERT INTO TB_YM_WRKBOOKMTL (
					  YD_WBOOK_ID    --야드작업예약ID
					 ,STOCK_ID         --재료번호
					 ,STACK_COL_GP  --야드적치열구분
					 ,STACK_BED_GP  --야드적치BED번호
					 ,STACK_LAYER_GP  --야드적치단번호
					 ,YD_UP_COLL_SEQ
					 ,REGISTER       --등록자
					 ,REG_DDTT       --등록일시
					 ,MODIFIER       --수정자
					 ,MOD_DDTT       --수정일시
					 ,DEL_YN         --삭제유무
					) VALUES (
					  :V_YD_WBOOK_ID
					 ,:V_STOCK_ID
					 ,:V_STACK_COL_GP
					 ,:V_STACK_BED_GP
					 ,:V_STACK_LAYER_GP
					 ,:V_YD_UP_COLL_SEQ
					 ,:V_MODIFIER
					 ,SYSDATE
					 ,:V_MODIFIER
					 ,SYSDATE
					 ,'N'
					)
					 */
					jrParam.setField("STOCK_ID"      , sSTOCK_ID);	//재료번호
					jrParam.setField("STACK_COL_GP"  , sSTACK_COL_GP);	//야드적치열구분
					jrParam.setField("STACK_BED_GP"  , sSTACK_BED_GP);	//야드적치Bed번호
					jrParam.setField("STACK_LAYER_GP", sSTACK_LAYER_GP);	//야드적치단번호
					jrParam.setField("YD_UP_COLL_SEQ", ""+idx);
					commDao.insert(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.insWrkBookMtl", logId, methodNm, "TB_YM_WRKBOOKMTL");
					
					/*
					UPDATE TB_YM_STOCK 
					   SET MODIFIER        = :V_MODIFIER
					     , MOD_DDTT        = SYSDATE
					     , WBOOK_ID        = NVL(:V_WBOOK_ID, WBOOK_ID)
					     , STOCK_MOVE_TERM = NVL(:V_STOCK_MOVE_TERM,STOCK_MOVE_TERM) 
					 WHERE STOCK_ID        = :V_STOCK_ID
					 */
					jrParam.setField("WBOOK_ID"       , ydWbookId);
					jrParam.setField("STOCK_MOVE_TERM", YmConstant.NEW_STOCK_MOVE_TERM_KG);
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateYdStockStockId", logId, methodNm, "저장품Table(TB_YM_STOCK) WBOOK_ID Update");
					
					iWrkBookCnt++;
				}
			} // end for
			
			for (int idx = 0; idx < rowCnt; idx++) {

				sYD_SCH_CD      = sYD_GP + commUtils.getValue(gdReq, "STORE_LOC_CD", idx).substring(1, 2) + "TC12UM";
				sSTOCK_ID       = commUtils.getValue(gdReq, "STOCK_ID", idx); 
				sSTACK_COL_GP   = commUtils.getValue(gdReq, "STORE_LOC_CD", idx).substring(0, 6);
				sSTACK_BED_GP   = commUtils.getValue(gdReq, "STORE_LOC_CD", idx).substring(6, 8);
				sSTACK_LAYER_GP = commUtils.getValue(gdReq, "STORE_LOC_CD", idx).substring(8, 10);
				sYD_BAY_GP      = commUtils.getValue(gdReq, "STORE_LOC_CD", idx).substring(1, 2);
				
				String ydWbookId = commDao.getSeqId(logId, methodNm, "WrkBook");
				
				if ("01".equals(sSTACK_LAYER_GP)) {
					/*
					INSERT INTO TB_YM_WRKBOOK ( 
					      YD_WBOOK_ID 
					    , YD_GP 
					    , YD_BAY_GP
					    , YD_SCH_CD
					    , REGISTER 
					    , REG_DDTT 
					    , MODIFIER 
					    , MOD_DDTT 
					    , DEL_YN
					    , YD_AIM_BAY_GP
					    , YD_TO_LOC_GUIDE
					    , YD_WRK_PLAN_TCAR
					    , YD_SCH_PRIOR
					) VALUES (
					      :V_YD_WBOOK_ID
					    , :V_YD_GP
					    , :V_YD_BAY_GP
					    , :V_YD_SCH_CD
					    , :V_MODIFIER
					    , SYSDATE
					    , :V_MODIFIER
					    , SYSDATE
					    , 'N'
					    , 'H'
					    , '3H'
					    , '3XTC02'
					    , (SELECT YD_WRK_CRN_PRIOR FROM TB_YM_SCHEDULERULE WHERE YD_SCH_CD = :V_YD_SCH_CD AND ROWNUM = 1)
					)
					 */
					jrParam.setField("YD_BAY_GP"    , sYD_BAY_GP);
					jrParam.setField("YD_WBOOK_ID"	, ydWbookId); 
					jrParam.setField("YD_SCH_CD"	, sYD_SCH_CD);
					commDao.insert(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.InsertYdWBook", logId, methodNm, "작업예약(TB_YM_WRKBOOK) Table Insert");
					
					/*
					INSERT INTO TB_YM_WRKBOOKMTL (
					  YD_WBOOK_ID    --야드작업예약ID
					 ,STOCK_ID         --재료번호
					 ,STACK_COL_GP  --야드적치열구분
					 ,STACK_BED_GP  --야드적치BED번호
					 ,STACK_LAYER_GP  --야드적치단번호
					 ,YD_UP_COLL_SEQ
					 ,REGISTER       --등록자
					 ,REG_DDTT       --등록일시
					 ,MODIFIER       --수정자
					 ,MOD_DDTT       --수정일시
					 ,DEL_YN         --삭제유무
					) VALUES (
					  :V_YD_WBOOK_ID
					 ,:V_STOCK_ID
					 ,:V_STACK_COL_GP
					 ,:V_STACK_BED_GP
					 ,:V_STACK_LAYER_GP
					 ,:V_YD_UP_COLL_SEQ
					 ,:V_MODIFIER
					 ,SYSDATE
					 ,:V_MODIFIER
					 ,SYSDATE
					 ,'N'
					)
					 */
					jrParam.setField("STOCK_ID"      , sSTOCK_ID);	//재료번호
					jrParam.setField("STACK_COL_GP"  , sSTACK_COL_GP);	//야드적치열구분
					jrParam.setField("STACK_BED_GP"  , sSTACK_BED_GP);	//야드적치Bed번호
					jrParam.setField("STACK_LAYER_GP", sSTACK_LAYER_GP);	//야드적치단번호
					jrParam.setField("YD_UP_COLL_SEQ", ""+idx);
					commDao.insert(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.insWrkBookMtl", logId, methodNm, "TB_YM_WRKBOOKMTL");
					
					/*
					UPDATE TB_YM_STOCK 
					   SET MODIFIER        = :V_MODIFIER
					     , MOD_DDTT        = SYSDATE
					     , WBOOK_ID        = NVL(:V_WBOOK_ID, WBOOK_ID)
					     , STOCK_MOVE_TERM = NVL(:V_STOCK_MOVE_TERM,STOCK_MOVE_TERM) 
					 WHERE STOCK_ID        = :V_STOCK_ID
					 */
					jrParam.setField("WBOOK_ID"       , ydWbookId);
					jrParam.setField("STOCK_MOVE_TERM", YmConstant.NEW_STOCK_MOVE_TERM_KG);
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateYdStockStockId", logId, methodNm, "저장품Table(TB_YM_STOCK) WBOOK_ID Update");
					
					iWrkBookCnt++;
				}
			} // end for
			
			
			if(iWrkBookCnt > 0) {
				
				String ydWrkPlanTcar = "3XTC02"; //대차출하상차는 2번대차만 한다.
				
				/**********************************************************
				* 3. 대차작업이 있으면 공대차출발지시 처리
				**********************************************************/
				if (!"".equals(ydWrkPlanTcar)) {
					//공대차출발지시 처리시 Exception을 발생시키지 않기위해 미리 Check
					String msgTcar = ""; //공대차출발지시 처리 메세지
				 			
					//대차스케쥴정보(공대차출발지시) 조회
					jrParam.setField("YD_EQP_ID", ydWrkPlanTcar);	//야드설비ID(대차)
					
					/*
					SELECT TS.YD_TCAR_SCH_ID
					      ,EQ.WPROG_STAT                AS YD_EQP_STAT
					      ,EQ.WORK_MODE                 AS YD_EQP_WRK_MODE
					      ,NVL(SUBSTR(CURR_STOP_LOC,2,1),WB.YD_BAY_GP) 
					                                    AS YD_CURR_BAY_GP --이동중이면 상차동을 현재동으로
					      ,SUBSTR(EQ.WAIT_STOP_LOC,2,1) AS YD_HOME_BAY_GP
					      ,WB.YD_WBOOK_ID               AS YD_WBOOK_ID_CURR   --현재 대차스케줄 상차작업예약ID
					      ,WB.YD_BAY_GP                 AS YD_BAY_GP_CURR     --현재 대차스케줄 상차동
					      ,WB.YD_AIM_BAY_GP             AS YD_AIM_BAY_GP_CURR --현재 대차스케줄 하차동
					      ,XB.YD_WBOOK_ID               AS YD_WBOOK_ID_NEXT   --다음 상차작업예약ID
					      ,XB.YD_BAY_GP                 AS YD_BAY_GP_NEXT     --다음 상차동
					      ,XB.YD_AIM_BAY_GP             AS YD_AIM_BAY_GP_NEXT --다음 하차동
					      ,(SELECT CASE WHEN COUNT(*) > 0 THEN 'Y' ELSE 'N' END
					          FROM TB_YM_TCARFTMVMTL TM
					         WHERE TM.YD_TCAR_SCH_ID = TS.YD_TCAR_SCH_ID
					           AND TM.DEL_YN = 'N')     AS TC_MTL_YN
					--      ,NVL(EQ.AUTO_TCAR_SCH_YN,'N') AS AUTO_TCAR_SCH_YN   --자동대차스케줄여부
					  FROM TB_YM_EQUIP   EQ
					      ,TB_YM_TCARSCH TS
					      ,TB_YM_WRKBOOK WB
					      ,(SELECT MIN(YD_WBOOK_ID  ) AS YD_WBOOK_ID
					              ,MIN(YD_BAY_GP    ) AS YD_BAY_GP
					              ,MIN(YD_AIM_BAY_GP) AS YD_AIM_BAY_GP
					          FROM (SELECT YD_WBOOK_ID
					                      ,YD_BAY_GP
					                      ,YD_AIM_BAY_GP
					                  FROM TB_YM_WRKBOOK
					                 WHERE YD_WRK_PLAN_TCAR = :V_YD_EQP_ID
					                   AND YD_WBOOK_ID NOT IN
					                      (SELECT NVL(YD_CARLD_WRK_BOOK_ID,YD_CARUD_WRK_BOOK_ID) AS YD_WBOOK_ID
					                         FROM TB_YM_TCARSCH
					                        WHERE DEL_YN = 'N'
					                          AND (YD_CARLD_WRK_BOOK_ID IS NOT NULL	OR YD_CARUD_WRK_BOOK_ID IS NOT NULL))
					--                   AND YD_SCH_CD LIKE '__TC__U%'
					                   AND ((SUBSTR(YD_SCH_CD,1,2) <> (NVL(SUBSTR(YD_TO_LOC_GUIDE,1,2),SUBSTR(YD_SCH_CD,1,2))))
					                         OR 
					                        (YD_SCH_CD LIKE SUBSTR(YD_SCH_CD,1,2)|| 'TC__U%')
					                       )
					                   AND DEL_YN = 'N'
					                 ORDER BY YD_SCH_PRIOR, YD_WBOOK_ID)
					         WHERE ROWNUM = 1) XB
					 WHERE EQ.EQUIP_GP             = TS.YD_EQP_ID(+)
					   AND 'N'                     = TS.DEL_YN(+)
					   AND TS.YD_CARLD_WRK_BOOK_ID = WB.YD_WBOOK_ID(+)
					   AND 'N'                     = WB.DEL_YN(+)
					   AND EQ.EQUIP_GP             = :V_YD_EQP_ID
					 */
					JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarSchLevWo", logId, methodNm, "공대차출발지시 조회");
					
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
						jrParam.setField("YD_BAY_GP", sYD_BAY_GP   ); //야드동구분(상차동)
						

						jrRtn = ymComm.trtTcarSchLevWo(jrParam);
					} else {
						commUtils.printLog(logId, "대차[" + ydWrkPlanTcar + "] 공대차출발지시 불가 : " + msgTcar, "SL");
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
	} // end of callLoadWbookInfo
	
	
	/**
	 *  상차위치 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecord carLiftPosSet(GridData gdReq) throws DAOException {
		String methodNm = "상차위치변경 [BCoilJspFaEJB.carLiftPosSet] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//Grid date 를 JDTORecord data 로 변환
//			JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			//gdReq.getParam("TRT_MTH")
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String sSqlId   = "";
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
		
				jrParam.setField("MODIFIER"	            ,commUtils.trim(gdReq.getParam("userid")));
				jrParam.setField("STACK_BED_GP"	        ,commUtils.getValue(gdReq, "STACK_BED_GP", ii)); 
				jrParam.setField("STACK_LAYER_GP"	    ,commUtils.getValue(gdReq, "STACK_LAYER_GP", ii)); 
				jrParam.setField("YD_CAR_SCH_ID"	    ,commUtils.getValue(gdReq, "YD_CAR_SCH_ID", ii)); 
				jrParam.setField("STOCK_ID"     	    ,commUtils.getValue(gdReq, "STOCK_ID", ii)); 
				jrParam.setField("STACK_COL_GP"	        ,commUtils.getValue(gdReq, "YD_CARLD_STOP_LOC", ii)); 			
				/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.bcoilCarldInfoInqjl.updYmCarftmvmtl 
				UPDATE TB_YM_CARFTMVMTL
				      SET MODIFIER = :V_MODIFIER
				         ,MOD_DDTT = SYSDATE
				 
				         ,STACK_BED_GP = :V_STACK_BED_GP
				         ,STACK_LAYER_GP = :V_STACK_LAYER_GP
				 
				  WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				    AND STOCK_ID = :V_STOCK_ID
				*/    
				sSqlId="com.inisteel.cim.ym.bcoil.dao.BcoilDAO.bcoilCarldInfoInqjl.updYmCarftmvmtl";
				commDao.update(jrParam, sSqlId, logId, methodNm, "차량이송재료 UPDATE"); 			

//				/*com.inisteel.cim.ym.bcoil.dao.BcoilDAO.bcoilCarldInfoInqjl.updateCarLayer
//				update TB_YM_STACKLAYER
//				set STOCK_ID = ''
//				    ,STACK_LAYER_STAT = 'E' 
//				where STOCK_ID = :V_STOCK_ID
//				*/
//				sSqlId="com.inisteel.cim.ym.bcoil.dao.BcoilDAO.bcoilCarldInfoInqjl.updateCarLayer";
//				commDao.update(jrParam, sSqlId, logId, methodNm, "Ym_차상위치 단정보 삭제");			
//				
//				
//				 /*com.inisteel.cim.ym.bcoil.dao.BcoilDAO.bcoilCarldInfoInqjl.updateCarMapLayer 
//				UPDATE TB_YM_STACKLAYER    
//				   SET STOCK_ID         = :V_STOCK_ID
//				     , STACK_LAYER_STAT = 'C'
//				 WHERE STACK_COL_GP   = :V_STACK_COL_GP
//				   AND STACK_BED_GP   = :V_STACK_BED_GP
//				   AND STACK_LAYER_GP = :V_STACK_LAYER_GP    */
//				sSqlId="com.inisteel.cim.ym.bcoil.dao.BcoilDAO.bcoilCarldInfoInqjl.updateCarMapLayer";
//				commDao.update(jrParam, sSqlId, logId, methodNm, "Ym_차량야드맵 UPDATE");	
			}
		
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
		
	} // end of carLiftPosSet
	
	
	/**
	 *  차량작업 포인트 현황-포인트 사용 등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecord procCoilYdGdsPntUnitCLCoil(GridData gdReq) throws DAOException {
		String methodNm = "차량작업 포인트 현황-포인트 사용 등록[BCoilJspSeEJBSBean.procCoilYdGdsPntUnitCLCoil] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String szMsg = null;
		String szYD_STK_COL_GP		= null;
		String szYD_STK_COL_ACT_STAT= null;
 
		String szTRN_EQP_CD = null;
		String szTRN_EQP_CD_ORG = null;
		String szTRN_EQP_CD_CARPOINT = null;
		String szYD_STKBED_USG_CD_PARAM	= null;
		String szYD_STK_COL_ACT_STAT_PARAM	= null;		
 
		String szJMS_TC_CD  = null; 
		String szCAR_NO          		= "";
		String szCARD_NO          		= "";	
		String szYD_CAR_USE_GP          = "";		
		JDTORecord recInTemp = null;
		int       	intRtnVal    		= 0;		
		//JDTORecord recInTemp1 = null;
		boolean isSendable				= true;
		
		String szYD_GP					= "";
		
		String szTO_YD_STK_COL_GP 		= "";
		String szYD_FRM_YN          	= "";
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			JDTORecord recOutTemp = null;
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			JDTORecordSet rsStkCol = JDTORecordFactory.getInstance().createRecordSet("");	
			JDTORecord sndRecord = JDTORecordFactory.getInstance().create();
			//String modifier = commUtils.trim(gdReq.getParam("userid"));	//수정자
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				szYD_STK_COL_GP 		    = commUtils.getValue(gdReq, "YD_STK_COL_GP", ii);
				szYD_STK_COL_ACT_STAT_PARAM	= commUtils.getValue(gdReq, "YD_STK_COL_ACT_STAT", ii);//사용구분
				szYD_STKBED_USG_CD_PARAM    = commUtils.getValue(gdReq, "YD_STKBED_USG_CD",ii);    //전용
				szTRN_EQP_CD			    = commUtils.getValue(gdReq, "TRN_EQP_CD", ii);         //차량번호
				szYD_FRM_YN                 = commUtils.getValue(gdReq, "YD_FRM_YN", ii);         //차량형상유무
				szTRN_EQP_CD_ORG			= commUtils.getValue(gdReq, "UPDATE_CHK1", ii);         //차량번호_변경체크용 
				
				commUtils.printLog(logId, "TRN_EQP_CD : " + szTRN_EQP_CD, "SL");	
				commUtils.printLog(logId, "TRN_EQP_CD_ORG : " + szTRN_EQP_CD_ORG, "SL");	
				
    			recOutTemp = JDTORecordFactory.getInstance().create();
    			jrParam.setField("YD_STK_COL_GP"		, szYD_STK_COL_GP);
    			jrParam.setField("YD_STK_COL_ACT_STAT"	, szYD_STK_COL_ACT_STAT);
    			
    			szYD_GP = szYD_STK_COL_GP.substring(0 , 1);
    			
				if (szYD_GP.equals("1")||szYD_GP.equals("3")){
					/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCarPointByLoc 
					SELECT 
						 YD_STK_COL_GP AS 
						,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
						,REGISTER 
						,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT
						,MODIFIER 
						,DEL_YN 
						,YD_GP 
						,YD_STK_COL_ACT_STAT 
						,CASE WHEN CAR_NO IS NOT NULL AND TRN_EQP_CD IS NULL THEN 'G' 
					          WHEN TRN_EQP_CD IS NOT NULL AND CAR_NO IS NULL THEN 'L'
					          ELSE '' END AS YD_CAR_USE_GP 
						,TRN_EQP_CD 
						,CAR_NO 
						,CARD_NO 
						,WLOC_CD 
						,YD_PNT_CD 
					 
					FROM TB_YD_CARPOINT A
					WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
					  AND DEL_YN ='N'
					*/
					//rsStkCol = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYmStkcol", logId, methodNm, "AB열연코일적치열 조회"); 
					rsStkCol = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCarPointByLoc", logId, methodNm, "1열연 적치열로 Carpoint 정보 조회"); 
					 
				} else {
					rsStkCol = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdStkcoly", logId, methodNm, "AB열연외적치열 조회"); //? 필요없을듯
				}
		    	
		    	if (rsStkCol == null || rsStkCol.size() <= 0) {
					szMsg="["+methodNm+"] 적치열 조회 getYmstackcol data not found";
					commUtils.printLog(logId, szMsg, "SL");	
					continue;
				}

		    	rsStkCol.absolute(1);
		    	recOutTemp = JDTORecordFactory.getInstance().create();
		    	recOutTemp.setRecord(rsStkCol.getRecord());

				szYD_STK_COL_ACT_STAT 	    = commUtils.trim(recOutTemp.getFieldString("YD_STK_COL_ACT_STAT"));
				szTRN_EQP_CD_CARPOINT		= commUtils.trim(recOutTemp.getFieldString("TRN_EQP_CD"));
				szCAR_NO 				    = commUtils.trim(recOutTemp.getFieldString("CAR_NO"));
				szCARD_NO 				    = commUtils.trim(recOutTemp.getFieldString("CARD_NO"));
				szYD_CAR_USE_GP 			= commUtils.trim(recOutTemp.getFieldString("YD_CAR_USE_GP"));
				
				if("".equals(szTRN_EQP_CD)&&"".equals(szTRN_EQP_CD_ORG)){ 
					//원래 빈 포인트라 차량번호를 변경하지 않고 다른 속성만 변경하려 했는데 그사이 Carpoint 에 차량이 들어온 경우
					
					if(!szTRN_EQP_CD_ORG.equals(szTRN_EQP_CD_CARPOINT) || !szTRN_EQP_CD_ORG.equals(szCAR_NO)) {
					
						if(!"".equals(szTRN_EQP_CD_CARPOINT)) {
							
							szTRN_EQP_CD = szTRN_EQP_CD_CARPOINT;
							commUtils.printLog(logId, "..1.1.. : " + szTRN_EQP_CD, "SL");	
							
						} else if(!"".equals(szCAR_NO)) {
						
							szTRN_EQP_CD = szCAR_NO;
							commUtils.printLog(logId, "..1.2.. : " + szTRN_EQP_CD, "SL");	
						}
						
						if(!"".equals(szTRN_EQP_CD)) {
							if(!"N".equals(szYD_STK_COL_ACT_STAT_PARAM)) {
								//사용가능으로 변경하려는데 차량이 들어 왔다면 carpoint 의 값을 그대로 사용한다.
								szYD_STK_COL_ACT_STAT_PARAM = szYD_STK_COL_ACT_STAT;
							}
						}
					}
				}
				
				if("".equals(szTRN_EQP_CD)){ 
					//포인트 차량이 존재 안하는 경우 
					if(!szTRN_EQP_CD.equals(szTRN_EQP_CD_ORG)) {
						//찰량번호가 변경 되었을 경우
						commUtils.printLog(logId, "..2.. : " + szTRN_EQP_CD, "SL");	
						szTRN_EQP_CD  	= "";
						szYD_CAR_USE_GP = "";
						szCAR_NO 		= "";
						szCARD_NO 		= "";
						szYD_CAR_USE_GP = "";
					} 
					
				}else{
					if(!szTRN_EQP_CD.equals(szTRN_EQP_CD_ORG)) {
						//차량번호가 변경 되었을 경우
						if((szTRN_EQP_CD.substring(0 , 1).equals("G")||szTRN_EQP_CD.substring(0 , 1).equals("K")) && !"TT".equals(szYD_STKBED_USG_CD_PARAM)){			 
							commUtils.printLog(logId, "..3.. : " + szTRN_EQP_CD, "SL");	
							szCAR_NO 		= "";
							szCARD_NO 		= "";
							szYD_CAR_USE_GP = "L";
						}else {
							commUtils.printLog(logId, "..4.. : " + szTRN_EQP_CD, "SL");	
							szCAR_NO      = szTRN_EQP_CD;
							
							jrParam.setField("CAR_NO"		, szCAR_NO);
							
							JDTORecordSet rsCarSch = JDTORecordFactory.getInstance().createRecordSet("");	
							rsCarSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYmCarInfoByCarNo", logId, methodNm, "차량번호로 차량스케줄정보 조회"); 
							
							if (rsCarSch == null || rsCarSch.size() <= 0) {
								szCARD_NO 		= "";
							} else {
								szCARD_NO = commUtils.trim(rsCarSch.getRecord(0).getFieldString("CARD_NO"));
							}
							commUtils.printLog(logId, "CARD_NO : " + szCARD_NO, "SL");	
							
							szTRN_EQP_CD  ="";
							szYD_CAR_USE_GP = "G";
						}
					} else {
						
						if((szTRN_EQP_CD.substring(0 , 1).equals("G")||szTRN_EQP_CD.substring(0 , 1).equals("K")) && !"TT".equals(szYD_STKBED_USG_CD_PARAM)){			 
							szCAR_NO 		= "";
							szCARD_NO 		= "";
						}else {
							szTRN_EQP_CD  ="";
						}
					}
				}
		    	
				if (szYD_GP.equals("1")||szYD_GP.equals("3")){
					jrParam.setField("STACK_COL_GP"      , szYD_STK_COL_GP);
					jrParam.setField("STACK_COL_ACTIVE_STAT", "L");
					jrParam.setField("YD_CAR_USE_GP"      , szYD_CAR_USE_GP);
					jrParam.setField("TRN_EQP_CD"         , szTRN_EQP_CD);
					jrParam.setField("CAR_NO"             , szCAR_NO);
					jrParam.setField("CARD_NO"            , szCARD_NO);
					
					/*UPDATE USRYMA.TB_YM_STACKCOL
					SET STACK_COL_ACTIVE_STAT=:V_STACK_COL_ACTIVE_STAT
					   , YD_CAR_USE_GP =:V_YD_CAR_USE_GP
					   , TRN_EQP_CD=:V_TRN_EQP_CD
					   , CAR_NO=:V_CAR_NO
					   , CARD_NO =:V_CARD_NO
					   , MOD_DDTT= SYSDATE
					   , MODIFIER ='맵활성화'
					 WHERE STACK_COL_GP=:V_STACK_COL_GP*/
					intRtnVal= commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYmStkcol", logId, methodNm, "AB열연적치열수정");
					
					
					
					if (intRtnVal <= 0) {
						szMsg = "["+methodNm+"] 적치열 수정, ErrorCode:" + intRtnVal;
						commUtils.printLog(logId, szMsg, "SL");	
					}  
					
					//jrParam.setField("MODIFIER"	            , modifier);
					jrParam.setField("YD_STK_COL_ACT_STAT"	, szYD_STK_COL_ACT_STAT_PARAM);
					jrParam.setField("YD_STKBED_USG_CD"		, szYD_STKBED_USG_CD_PARAM);

					jrParam.setField("TRN_EQP_CD"			, szTRN_EQP_CD);
					jrParam.setField("YD_CAR_USE_GP"		, szYD_CAR_USE_GP);
					jrParam.setField("CAR_NO"				, szCAR_NO);
					jrParam.setField("CARD_NO"				, szCARD_NO);
	    			jrParam.setField("YD_STK_COL_GP"		, szYD_STK_COL_GP);
	    			jrParam.setField("YD_FRM_YN"			, szYD_FRM_YN);
	    			
					/*UPDATE USRYDA.TB_YD_CARPOINT
					SET MODIFIER=:V_MODIFIER
					  , MOD_DDTT=SYSDATE
					  , YD_STK_COL_ACT_STAT=:V_YD_STK_COL_ACT_STAT
					  , YD_CAR_USETYPE_GP=:V_YD_STKBED_USG_CD
					  , TRN_EQP_CD=:V_TRN_EQP_CD
					  , CAR_NO=:V_CAR_NO
					  , CARD_NO=:V_CARD_NO
					  , YD_FRM_YN=:V_YD_FRM_YN
					WHERE YD_STK_COL_GP=:V_YD_STK_COL_GP*/
					
					intRtnVal=commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updydcarpoint", logId, methodNm, "B열연차량포인트수정");
					
					if (intRtnVal <= 0) {
						szMsg = "["+methodNm+"] 차량포인트 수정, ErrorCode:" + intRtnVal;
						commUtils.printLog(logId, szMsg, "SL");	
					}  
					
				}else{  // AB열연 이외는  구현 HOLD 
					
					rsStkCol = commDao.select(jrParam, "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcolNotDelYn", logId, methodNm, "AB열연코일야드외_적치열수정전정합성조회"); 
					
			    	if (rsStkCol == null || rsStkCol.size() <= 0) {
						szMsg="["+methodNm+"] AB열연코일야드외_적치열수정전정합성조회 getYdStkcolNotDelYn data not found";
						commUtils.printLog(logId, szMsg, "SL");	
						continue;
					} else {
						 
						commDao.update(jrParam, "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.updYdStkcol", logId, methodNm, "AB열연외적치열수정"); 
						
						commDao.update(jrParam, "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.updydcarpoint", logId, methodNm, "AB_열연외차량포인트수정"); 
					 
					}
				}
				
    	    	
				//사용불가 인경우 대기차량을 다른 포인트로 변경 한다. 2020.01.04
				if("N".equals(szYD_STK_COL_ACT_STAT_PARAM) && "3".equals(szYD_GP)){
					if("1".equals(szYD_STK_COL_GP.substring(5 , 6)) || "2".equals(szYD_STK_COL_GP.substring(5 , 6))){
						if("1".equals(szYD_STK_COL_GP.substring(5 , 6))){//1통로 인경우
							szTO_YD_STK_COL_GP =szYD_STK_COL_GP.substring(0 , 5)+"2";
						}else{
							szTO_YD_STK_COL_GP =szYD_STK_COL_GP.substring(0 , 5)+"1";
						}
					}else{
						if("3".equals(szYD_STK_COL_GP.substring(5 , 6))){ //2통로 인경우
							szTO_YD_STK_COL_GP =szYD_STK_COL_GP.substring(0 , 5)+"4";
						}else{
							szTO_YD_STK_COL_GP =szYD_STK_COL_GP.substring(0 , 5)+"3";
						}
					}
					
					recInTemp = JDTORecordFactory.getInstance().create();
			    	recInTemp.setField("STACK_COL_GP"      , szYD_STK_COL_GP);			//FROM 차량위치
			    	recInTemp.setField("TO_YD_STK_COL_GP"      , szTO_YD_STK_COL_GP);//TO 차량위치
			    	
			    	intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updCarschUpdatePoint");
					
					szMsg= "차량포인트 변경 완료 :" +intRtnVal ;
					commUtils.printLog(logId, szMsg, "SL");	
		
				}
				//----------------------------------------------------------------------------------------------
				
		    	/******************************************
		    	 * 포인트 구내 운송 으로 전송처리
		    	 ***************************************/
		    	recInTemp  = JDTORecordFactory.getInstance().create();
		    	recInTemp.setResultCode(logId);	//Log ID
		    	recInTemp.setResultMsg(methodNm);	//Log Method Name
		    	recInTemp.setField("JMS_TC_CD"			, "YDTSJ012");
		    	recInTemp.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시
		    	recInTemp.setField("YD_GP"				, szYD_STK_COL_GP.substring(0,1));
		    	//recInTemp.setField("YS_STK_COL_GP"			, szYD_STK_COL_GP);
				
		    	szMsg= "szYD_STK_COL_ACT_STAT: ["+szYD_STK_COL_ACT_STAT+"]  szYD_STK_COL_ACT_STAT_PARAM: ["+szYD_STK_COL_ACT_STAT_PARAM+"] 비교";
				commUtils.printLog(logId, szMsg, "SL");		
				
		    	
				if (szYD_STK_COL_ACT_STAT_PARAM.equals ("C") 
						|| szYD_STK_COL_ACT_STAT_PARAM.equals("L")
						|| szYD_STK_COL_ACT_STAT_PARAM.equals("R")){
					if ( szYD_STK_COL_ACT_STAT.equals("N")) {			//사용불가
						szMsg = " 적치열["+szYD_STK_COL_GP+"] - 변경된 야드적치열활성상태["+szYD_STK_COL_ACT_STAT+"]에 대한 포인트 OPEN 처리 전문 송신 ";
						commUtils.printLog(logId, szMsg, "SL");	
						
						recInTemp.setField("PNT_UNIT_CL_GP",	"Y");
					}else{
						isSendable = false;
					}
				}else if (szYD_STK_COL_ACT_STAT_PARAM.equals ("N")){
					
					szMsg = "적치열["+szYD_STK_COL_GP+"]에 대한 사용불가(포인트 CLOSE) 처리 전문 송신 ";
					commUtils.printLog(logId, szMsg, "SL");	
					
					recInTemp.setField("PNT_UNIT_CL_GP",		"N");
				}else{
					szMsg = "포인트 개폐구분의 값이 없습니다 !!!";
					commUtils.printLog(logId, szMsg, "SL");	
				}		    	
		    	
		    	
		    	if ( isSendable ) {
		    		
		    		commUtils.printParam(logId, recInTemp);
		    		
		    		commUtils.addSndData(recInTemp);	

					recInTemp  = JDTORecordFactory.getInstance().create();
					recInTemp.setResultCode(logId);	//Log ID
					recInTemp.setResultMsg(methodNm);	//Log Method Name
		    		recInTemp.setField("MSG_ID",    szJMS_TC_CD);
					recInTemp.setField("YD_INFO_SYNC_CD", "3");						//1:동,2:SPAN,3:열,4:BED
					recInTemp.setField("MSG_GP"			, "I"                         ); //전문구분
					recInTemp.setField("YD_GP", szYD_STK_COL_GP.substring(0, 1));
					recInTemp.setField("STACK_COL_GP", szYD_STK_COL_GP);
					recInTemp.setField("STK_BED_GP"  , "");
					
					//전송 Data 생성
 
					sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2("YMA7L001", recInTemp));
					szMsg="["+methodNm+"] 포인트 개패시 시 저장위치 제원 야드L2로 전송";
					commUtils.printLog(logId, szMsg, "SL");			    		
		    	}
			}

			szMsg="[구내내운송 소재차량Point개폐 전송  성공";
			commUtils.printLog(logId, szMsg, "SL");
	    	
	    	szMsg = "["+methodNm+"] STACK_COL_GP["+szYD_STK_COL_GP+"]의 진행상태["+szYD_STK_COL_ACT_STAT+"] 변경처리함";
			commUtils.printLog(logId, szMsg, "SL");

			commUtils.printLog(logId, methodNm, "S-");

			return sndRecord;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of uprocCoilYdGdsPntUnitCLCoil
		
	/**
	 * "차량입동위치변경
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew" 
	*/
	public JDTORecord changeCarLoc(GridData gdReq) throws DAOException {
		String methodNm = "차량입동위치변경[BCoilJspSeEJBSBean.changeCarLoc] < " + gdReq.getNavigateValue();
		JDTORecord 		jrRtn  		= null;
		String szMsg = null;
		
		String logId = gdReq.getIPAddress();
		
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			jrParam.setField("CAR_NO"      			, gdReq.getParam("CAR_NO"));
			jrParam.setField("YD_STK_COL_GP"      	, gdReq.getParam("YD_STK_COL_GP"));
				
			/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updCarStopLoc 
			UPDATE TB_YD_CARSCH
			   SET YD_CARLD_STOP_LOC = CASE REGEXP_INSTR(YD_CAR_PROG_STAT,'[^0-9]') --상차차량
			                           WHEN 0
			                            THEN :V_YD_STK_COL_GP
			                           ELSE YD_CARLD_STOP_LOC
			                            END
			      ,YD_CARUD_STOP_LOC = CASE REGEXP_INSTR(YD_CAR_PROG_STAT,'[^0-9]') --하차차량
			                           WHEN 0
			                            THEN YD_CARUD_STOP_LOC
			                           ELSE :V_YD_STK_COL_GP
			                            END
			      ,YD_PNT_CD1 = CASE REGEXP_INSTR(YD_CAR_PROG_STAT,'[^0-9]') --상차차량
			                    WHEN 0 
			                        THEN (SELECT YD_PNT_CD
			                                FROM TB_YD_CARPOINT
			                               WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
			                                 AND DEL_YN = 'N') 
			                    ELSE YD_PNT_CD1
			                    END
			      ,YD_PNT_CD3 = CASE REGEXP_INSTR(YD_CAR_PROG_STAT,'[^0-9]') --하차차량
			                    WHEN 0 
			                        THEN YD_PNT_CD3 
			                    ELSE (SELECT YD_PNT_CD
			                                FROM TB_YD_CARPOINT
			                               WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
			                                 AND DEL_YN = 'N') 
			                    END
			 WHERE DEL_YN = 'N'
			   AND NVL(TRN_EQP_CD,CAR_NO) = :V_CAR_NO
			   */
			commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updCarStopLoc", logId, methodNm, "차량입동위치 수정");
	    	
			jrParam.setField("CAR_NO"      			, gdReq.getParam("CAR_NO"));
			/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYmCarInfoByCarNo
			SELECT A.*
			      ,B.YD_CARPNT_CD
			 FROM TB_YD_CARSCH A
			     ,TB_YD_CARPOINT B
			 WHERE A.DEL_YN = 'N'
			   AND NVL(A.TRN_EQP_CD,A.CAR_NO) = :V_CAR_NO
			   AND DECODE(REGEXP_INSTR(YD_CAR_PROG_STAT,'[^0-9]'),0,YD_CARLD_STOP_LOC,YD_CARUD_STOP_LOC) = B.YD_STK_COL_GP
			   */
			JDTORecordSet jsCarSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYmCarInfoByCarNo", logId, methodNm,  "크레인 스케쥴 read");
			
			if(jsCarSch.size()>0){
				
				String szYD_CARPNT_CD = jsCarSch.getRecord(0).getFieldString("YD_CARPNT_CD");	//입동포인트
				String szYD_CAR_SCH_ID = jsCarSch.getRecord(0).getFieldString("YD_CAR_SCH_ID");	//차량스케줄ID
				
				JDTORecord 		recInTemp  		= null;
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setResultCode(logId);	//Log ID
				recInTemp.setResultMsg(methodNm);	//Log Method Name
				recInTemp.setField("JMS_TC_CD"			, "YMYMJ662");          //차량입동지시 요구 기존:YDYDJ662
				recInTemp.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시
				recInTemp.setField("YD_CARPNT_CD"		, szYD_CARPNT_CD);		//입동포인트
				recInTemp.setField("YD_CAR_SCH_ID"		, szYD_CAR_SCH_ID);	//차량스케줄ID
				
				EJBConnector ejbConn9 = new EJBConnector("default", "YmCommCarMvSeEJB", this);	
				JDTORecord jrRtn9 = (JDTORecord)ejbConn9.trx("rcvYMYMJ662", new Class[] { JDTORecord.class }, new Object[] { recInTemp });		
				jrRtn = commUtils.addSndData(jrRtn, jrRtn9);	
				
				szMsg="[" + methodNm + "] 차량입동포인트[" + szYD_CARPNT_CD + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 호출 성공";
				commUtils.printLog(logId, szMsg, "SL");	
				
				
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of changeCarLoc
	
	/**
	 * 차량작업 포인트 현황-입동순서변경
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord procCoilYdGdsBayInWoSeqChangCoil(GridData gdReq) throws DAOException {
		String methodNm = "차량작업 포인트 현황-입동순서변경[BCoilJspSeEJBSBean.procCoilYdGdsBayInWoSeqChangCoil] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String szMsg = null;
		String syd_car_sch_id ="";		
		String szYD_CAR_SCH_ID		= "";
		String szYD_BAYIN_WO_SEQ= "";
		
		int       	intRtnVal    		= 0;		

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
 
			JDTORecord sndRecord = JDTORecordFactory.getInstance().create();
			
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			
			for (int ii = 0; ii < rowCnt; ii++) {
				for(int i=1;i<=20;i++){
					syd_car_sch_id 		= commUtils.getValue(gdReq, "YD_CAR_SCH_ID"+i, ii);
					
					if(!syd_car_sch_id.equals("")){
						szYD_CAR_SCH_ID =  commUtils.getValue(gdReq, "YD_CAR_SCH_ID"+i, ii) ;
						szYD_BAYIN_WO_SEQ = commUtils.getValue(gdReq, "YD_BAYIN_WO_SEQ"+i, ii) ;
						jrParam.setField("YD_CAR_SCH_ID"	,szYD_CAR_SCH_ID );
						jrParam.setField("YD_BAYIN_WO_SEQ"	,szYD_BAYIN_WO_SEQ );
	
						/*com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdCarschYdBayinWoSeq
							UPDATE TB_YD_CARSCH
							   SET MOD_DDTT = SYSDATE
							       , MODIFIER = :V_MODIFIER
							       , YD_BAYIN_WO_SEQ = :V_YD_BAYIN_WO_SEQ
							WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID	 
						  */
						intRtnVal=commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdCarschYdBayinWoSeq", logId, methodNm, "B열연차량스케줄수정");
					}
				}
			 
				if (intRtnVal <= 0) {
					szMsg = "["+methodNm+"] YD_CAR_SCH_ID:["+szYD_CAR_SCH_ID+"]YD_BAYIN_WO_SEQ:["+szYD_BAYIN_WO_SEQ+"] 변경처리중에러   intRtnVal" +intRtnVal;
					commUtils.printLog(logId, szMsg, "SL");

				} // end of if
				
			}
			if (intRtnVal > 0){
					szMsg="[입동순서변경  성공";
					commUtils.printLog(logId, szMsg, "SL");
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return sndRecord;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of procCoilYdGdsBayInWoSeqChangCoil	
	
	
	 /**
	 * "배차차량작업관리 - 차량입동요구
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew" 
	*/
	public JDTORecord CarArrivalNEW(GridData gdReq) throws DAOException {
		String methodNm = "배차차량작업관리 - 차량입동요구[BCoilJspSeEJBSBean.CarArrivalNEW] < " + gdReq.getNavigateValue();
		JDTORecord 		recInTemp  		= null;
		
		String logId = gdReq.getIPAddress();
		String szMsg = null;
		String szYD_CARPNT_CD ="";	
		String szYD_CAR_SCH_ID		= "";
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			JDTORecord jrRtn = null;
 
			jrRtn = JDTORecordFactory.getInstance().create();
			
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				szYD_CARPNT_CD = commUtils.getValue(gdReq, "YD_CARPNT_CD", ii);
				
				//for(int i=1;i<=20;i++){
				//	szYD_CAR_SCH_ID 		= commUtils.getValue(gdReq, "YD_CAR_SCH_ID"+i, ii);
				//	if(!szYD_CAR_SCH_ID.equals("")){
						
						recInTemp = JDTORecordFactory.getInstance().create();
						recInTemp.setResultCode(logId);	//Log ID
						recInTemp.setResultMsg(methodNm);	//Log Method Name
						recInTemp.setField("JMS_TC_CD"			, "YMYMJ662");          //차량입동지시 요구 기존:YDYDJ662
						recInTemp.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시
						recInTemp.setField("YD_CARPNT_CD"		, szYD_CARPNT_CD);		//입동포인트
						recInTemp.setField("YD_CAR_SCH_ID"		, szYD_CAR_SCH_ID);	//차량스케줄ID
						
						EJBConnector ejbConn9 = new EJBConnector("default", "YmCommCarMvSeEJB", this);	
						JDTORecord jrRtn9 = (JDTORecord)ejbConn9.trx("rcvYMYMJ662", new Class[] { JDTORecord.class }, new Object[] { recInTemp });		
						jrRtn = commUtils.addSndData(jrRtn, jrRtn9);	
						
						szMsg="[" + methodNm + "] 차량입동포인트[" + szYD_CARPNT_CD + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 호출 성공";
						commUtils.printLog(logId, szMsg, "SL");							
				//	}
				//}
			}

			szMsg="[출하차량도착  성공]";
			commUtils.printLog(logId, szMsg, "SL");
	    	
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of CarArrivalNEW		
		

	/**
	 * 차량 작업 관리 화면 :출발처리
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param gdReq
	 * @return
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public JDTORecord updCarStart(GridData gdReq) throws DAOException {
		//		LOG
		String szMsg 			= "";
		String methodNm		= "배차내역 - 출발처리";
		String logId = gdReq.getIPAddress();
		JDTORecord jrRst = JDTORecordFactory.getInstance().create();
		EJBConnector ejbConn 	= null;
 
		JDTORecord inRecord2  	= JDTORecordFactory.getInstance().create();	
		inRecord2.setResultCode(logId);	//Log ID
		inRecord2.setResultMsg(methodNm);	//Log Method Name
		
		try{
			 
			
			szMsg = "["+methodNm+"] 배차내역 출발처리  처리 시작  ==>";
			commUtils.printLog(logId, szMsg, "SL");	
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {
		        /* SELECT 
				    YD_CAR_SCH_ID  AS YD_CAR_SCH_ID
				    ,REGISTER AS REGISTER
				    ,TO_CHAR(REG_DDTT,'YYYYMMDDHH24MISS') AS REG_DDTT
				    ,MODIFIER AS MODIFIER
				    ,TO_CHAR(MOD_DDTT,'YYYYMMDDHH24MISS') AS MOD_DDTT
				    ,DEL_YN AS DEL_YN
				    ,YD_EQP_ID AS YD_EQP_ID
				    ,YD_CAR_USE_GP AS YD_CAR_USE_GP
				    ,CAR_NO AS CAR_NO
				    ,TRN_EQP_CD AS TRN_EQP_CD
				    ,CAR_KIND AS CAR_KIND
				    ,TRANS_EQUIPMENT_TYPE AS TRANS_EQUIPMENT_TYPE 
				    ,YD_EQP_WRK_STAT  AS YD_EQP_WRK_STAT
				    ,YD_WRK_PROG_STAT  AS YD_WRK_PROG_STAT
				    ,NVL(YD_EQP_WRK_SH,'0')  AS YD_EQP_WRK_SH
				    ,YD_EQP_WRK_WT  AS YD_EQP_WRK_WT
				    ,YD_STK_BED_TP  AS YD_STK_BED_TP
				    ,SPOS_WLOC_CD  AS SPOS_WLOC_CD
				       ,(CASE WHEN ARR_WLOC_CD IS NULL THEN (SELECT 
				                                                    (SELECT 
				                                                            D.ARR_WLOC_CD
				                                                        FROM TB_PT_STLFRTOMOVE D
				                                                       WHERE D.TRANSWORD_SEQNO=(SELECT
				                                                                            MAX(TRANSWORD_SEQNO) 
				                                                                         FROM TB_PT_STLFRTOMOVE K
				                                                                         WHERE D.STL_NO=K.STL_NO
				                                                                          AND ROWNUM<=1)
				                                                         AND B.STL_NO =D.STL_NO
				                                                         ) AS ARR_WLOC_CD
				                                             FROM  TB_YD_STKLYR B                                              
				                                              WHERE C.YD_CARLD_STOP_LOC=B.YD_STK_COL_GP     
				                                                AND ROWNUM<=1 ) 
				          ELSE ARR_WLOC_CD END) AS ARR_WLOC_CD
				    ,YD_CARLD_LEV_LOC  AS YD_CARLD_LEV_LOC
				    ,TO_CHAR(YD_CARLD_LEV_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_LEV_DT
				    ,TO_CHAR(YD_CARLD_PNT_WO_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_PNT_WO_DT
				    ,YD_PNT_CD1 AS YD_PNT_CD1
				    ,YD_PNT_CD2 AS YD_PNT_CD2
				    ,YD_CARLD_WRK_BOOK_ID  AS YD_CARLD_WRK_BOOK_ID
				    ,YD_CARLD_SCH_REQ_GP  AS YD_CARLD_SCH_REQ_GP
				    ,YD_CARLD_STOP_LOC  AS YD_CARLD_STOP_LOC
				    ,TO_CHAR(YD_CARLD_ARR_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_ARR_DT
				    ,TO_CHAR(YD_CARLD_ST_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_ST_DT
				    ,TO_CHAR(YD_CARLD_CMPL_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_CMPL_DT
				    ,YD_CARLD_WRK_ACT_GP  AS YD_CARLD_WRK_ACT_GP
				    ,TO_CHAR(YD_CARLD_CHK_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_CHK_DT
				    ,TO_CHAR(YD_CARUD_LEV_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_LEV_DT
				    ,TO_CHAR(YD_CARUD_PNT_WO_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_PNT_WO_DT
				    ,YD_PNT_CD3 AS YD_PNT_CD3
				    ,YD_PNT_CD4 AS YD_PNT_CD4
				    ,YD_CARUD_WRK_BOOK_ID  AS YD_CARUD_WRK_BOOK_ID
				    ,YD_CARUD_STOP_LOC  AS YD_CARUD_STOP_LOC
				    ,YD_CARUD_SCH_REQ_GP  AS YD_CARUD_SCH_REQ_GP
				    ,TO_CHAR(YD_CARUD_ARR_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_ARR_DT
				    ,TO_CHAR(YD_CARUD_CHK_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_CHK_DT
				    ,TO_CHAR(YD_CARUD_ST_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_ST_DT
				    ,TO_CHAR(YD_CARUD_CMPL_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_CMPL_DT
				    ,YD_CARUD_WRK_ACT_GP  AS YD_CARUD_WRK_ACT_GP
				    ,YD_TRN_WRK_DELY_CD  AS YD_TRN_WRK_DELY_CD
				    ,CARD_NO  AS CARD_NO
				    ,YD_CAR_PROG_STAT AS YD_CAR_PROG_STAT
				    ,FRTOMOVE_PLANT_GP AS FRTOMOVE_PLANT_GP
				    ,PROC_TO AS PROC_TO
				    ,RENTPROC_CD AS RENTPROC_CD
				    ,YD_FRTOMOVE_YD_GP AS YD_FRTOMOVE_YD_GP
				    ,YD_FRTOMOVE_BAY_GP AS YD_FRTOMOVE_BAY_GP
				    ,URGENT_FRTOMOVE_WORD_GP AS URGENT_FRTOMOVE_WORD_GP
				    ,DEST_TEL_NO AS DEST_TEL_NO
				    ,YD_DLVRDD_RULE_DD AS YD_DLVRDD_RULE_DD
				    ,SHIPASSIGN_WORD_DATE AS SHIPASSIGN_WORD_DATE
				    ,SHIPASSIGN_WORD_SEQNO AS SHIPASSIGN_WORD_SEQNO
				    ,SHIP_CD AS SHIP_CD
				    ,SHIP_NAME AS SHIP_NAME
				    ,RSHP_HOLD_NO AS RSHP_HOLD_NO
				    ,BERTH_NO AS BERTH_NO
				    ,SAILNO AS SAILNO
				    ,YD_CAR_WRK_GP AS YD_CAR_WRK_GP
				    ,TRANS_ORD_DATE AS TRANS_ORD_DATE
				    ,TRANS_ORD_SEQNO AS TRANS_ORD_SEQNO
				    ,YD_BAYIN_WO_SEQ
				    ,YD_CAR_RCPT_CHK_YN
				    ,YD_CAR_ISSUE_CHK_YN
				    ,YD_CAR_RCPT_CHECKER
				    ,YD_CAR_ISSUE_CHECKER    
				    ,SUBSTR(YD_CARLD_STOP_LOC,1,1) AS YD_GP
				    ,(select count(*)
				     from TB_YD_CRNSCH
				     where YD_WBOOK_ID = YD_CARLD_WRK_BOOK_ID
				     AND DEL_YN = 'N') YD_CRN_SCH_ID
				FROM TB_YD_CARSCH C
				WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID */       
				
		    	JDTORecord jrParam = JDTORecordFactory.getInstance().create();
		    	String sydcarschid =commUtils.getValue(gdReq, "YD_CAR_SCH_ID", ii);
		    	jrParam.setField("YD_CAR_SCH_ID", sydcarschid);
				JDTORecordSet loadCarSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdCarsch", logId, methodNm, "TB_YD_CARSCH 조회");
				
				if(loadCarSch.size() <= 0 ){
					szMsg="["+methodNm+"]  TB_YD_CARSCH >YD_CAR_SCH_ID"+"["+sydcarschid+"] SELECT Error ::  DO NOT EXIST"  ;
					commUtils.printLog(logId, szMsg, "SL");	
					//return gdRes ;
				}else{
					
					//inRecord2 	= JDTORecordFactory.getInstance().create();
					//inRecord2.setField("CAR_NO"				, commUtils.getValue(gdReq, "CAR_NO", ii));
					//inRecord2.setField("CARD_NO"			, commUtils.getValue(gdReq, "CARD_NO", ii));
					//inRecord2.setField("SPOS_WLOC_CD"		, commUtils.getValue(gdReq, "SPOS_WLOC_CD", ii));
					//inRecord2.setField("SPOS_YD_PNT_CD"		, commUtils.getValue(gdReq, "YD_PNT_CD1", ii));
					//inRecord2.setField("TRANS_ORD_DATE"		, commUtils.getValue(gdReq, "TRANS_ORD_DT", ii));
					//inRecord2.setField("TRANS_ORD_SEQNO"	, commUtils.getValue(gdReq, "TRANS_ORD_SEQNO", ii));
					
					inRecord2.setField("CAR_NO"				, loadCarSch.getRecord(0).getFieldString("CAR_NO"));
					inRecord2.setField("CARD_NO"			, loadCarSch.getRecord(0).getFieldString("CARD_NO"));
					inRecord2.setField("SPOS_WLOC_CD"		, loadCarSch.getRecord(0).getFieldString("SPOS_WLOC_CD"));
					inRecord2.setField("SPOS_YD_PNT_CD"		, loadCarSch.getRecord(0).getFieldString("YD_PNT_CD1"));
					inRecord2.setField("TRANS_ORD_DATE"		, loadCarSch.getRecord(0).getFieldString("TRANS_ORD_DATE"));
					inRecord2.setField("TRANS_ORD_SEQNO"	, loadCarSch.getRecord(0).getFieldString("TRANS_ORD_SEQNO"));
					
					ejbConn = new EJBConnector("default", "YmCommCarMvSeEJB", this);
					ejbConn.trx("procOutCarLevWr", new Class[] { JDTORecord.class }, new Object[] { inRecord2 });
				}				
			}
			
			szMsg = "["+methodNm+"]  배차내역 출발처리  처리 끝 ===>";
			commUtils.printLog(logId, szMsg, "SL");	
			
			jrRst.setField("RTN_MSG", szMsg);
			 
			return jrRst;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}  //end of getStandByYdArrive	
	
	
	/**
	 * 크레인스케줄 기준 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updSchRuleMgt(GridData gdReq) throws DAOException {
		String methodNm = "크레인스케줄 기준 변경[BCoilJspSeEJB.updSchRuleMgt] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			String sYD_WRK_CRN     = ""; //작업크레인
			String sYD_WRK_CRN_OLD = ""; //변경후 작업크레인
			
			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				//jrParam.setField("MODIFIER"            , commUtils.getValue(gdReq, "MODIFIER"            , ii)); //수정자              
				jrParam.setField("YD_SCH_RNG_CD"       , commUtils.getValue(gdReq, "YD_SCH_RNG_CD"       , ii)); //스케줄범위코드      
				jrParam.setField("YD_SCH_WHIO_GP"      , commUtils.getValue(gdReq, "YD_SCH_WHIO_GP"      , ii)); //스케줄입출고구분    
				jrParam.setField("YD_SCH_RULE_ACT_STAT", commUtils.getValue(gdReq, "YD_SCH_RULE_ACT_STAT", ii)); //스케줄기준활성상태  
				jrParam.setField("YD_WRK_CRN"          , commUtils.getValue(gdReq, "YD_WRK_CRN"          , ii)); //작업크레인          
				jrParam.setField("YD_WRK_CRN_PRIOR"    , commUtils.getValue(gdReq, "YD_WRK_CRN_PRIOR"    , ii)); //작업크레인우선순위  
				jrParam.setField("YD_ALT_CRN_YN"       , commUtils.getValue(gdReq, "YD_ALT_CRN_YN"       , ii)); //대체크레인유무      
				jrParam.setField("YD_ALT_CRN"          , commUtils.getValue(gdReq, "YD_ALT_CRN"          , ii)); //야드대체크레인      
				jrParam.setField("YD_ALT_CRN_PRIOR"    , commUtils.getValue(gdReq, "YD_ALT_CRN_PRIOR"    , ii)); //대체크레인우선순위  
				jrParam.setField("CD_CONTENTS"         , commUtils.getValue(gdReq, "CD_CONTENTS"         , ii)); //코드설명            
				jrParam.setField("YD_SCH_PROH_EXN"     , commUtils.getValue(gdReq, "YD_SCH_PROH_EXN"     , ii)); //야드스케줄금지유무  
				jrParam.setField("YD_SCH_CD"           , commUtils.getValue(gdReq, "YD_SCH_CD"           , ii)); //스케줄코드         
				jrParam.setField("DAN_PRIOR"           , commUtils.getValue(gdReq, "DAN_PRIOR"           , ii)); //단우선순위
				jrParam.setField("YD_SCH_AUTO_ST_YN"   , commUtils.getValue(gdReq, "YD_SCH_AUTO_ST_YN"   , ii)); //스케줄자동기동여부
				jrParam.setField("YD_WRK_CRN_OLD"      , commUtils.getValue(gdReq, "YD_WRK_CRN_OLD"      , ii)); //작업크레인
				
				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updSchRuleInfo", logId, methodNm, "크레인스케줄 기준 수정");
								
				/** 
				 * 해당 작업크레인으로 지정되어 있는 작업예약 크레인변경
				 */
				sYD_WRK_CRN     = commUtils.getValue(gdReq, "YD_WRK_CRN"    , ii);
				sYD_WRK_CRN_OLD = commUtils.getValue(gdReq, "YD_WRK_CRN_OLD", ii);
				
				//작업 크레인이 변경되었을 때
				if (!sYD_WRK_CRN.equals(sYD_WRK_CRN_OLD)) {
					/*
					UPDATE TB_YM_WRKBOOK
					   SET YD_WRK_PLAN_CRN = :V_YD_WRK_PLAN_CRN
					     , MOD_DDTT        = SYSDATE
					     , MODIFIER        = :V_MODIFIER
					 WHERE YD_WBOOK_ID IN ( SELECT YD_WBOOK_ID
					                          FROM TB_YM_WRKBOOK    WB
					                             , TB_YM_WRKBOOKMTL WM
					                         WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
					                           AND WB.DEL_YN = 'N'
					                           AND WM.DEL_YN = 'N'
					                           AND NOT EXISTS (SELECT 1
					                                             FROM TB_YM_CRNSCH CS
					                                            WHERE CS.YD_WBOOK_ID = WB.YD_WBOOK_ID
					                                          )
					                           AND WB.YD_GP = '3'
					                           AND WB.YD_SCH_CD = :V_YD_SCH_CD
					                      )
					   AND YD_WRK_PLAN_CRN IS NOT NULL
					 */
					jrParam.setField("YD_WRK_PLAN_CRN", sYD_WRK_CRN); //작업크레인
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updWrkCrnByChgSchCrn", logId, methodNm, "지정크레인변경"); 		 	
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updSchRuleMgt
	
	
	/**
	 * 크레인스케줄 고도화기준 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updAdvSchRuleMgt(GridData gdReq) throws DAOException {
		String methodNm = "크레인스케줄 고도화기준 변경[BCoilJspSeEJB.updAdvSchRuleMgt] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			String sTAB_INDEX = gdReq.getParam("TAB_INDEX");
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				if ("1".equals(sTAB_INDEX)) {
					/**
					 * 크레인스케줄 고도화 기준 수정
					 */
					jrParam.setField("YD_WRK_CRN_PRIOR"    , commUtils.getValue(gdReq, "YD_WRK_CRN_PRIOR" , ii)); //작업크레인우선순위  
					jrParam.setField("YD_SCH_CD"           , commUtils.getValue(gdReq, "YD_SCH_CD"        , ii));          
					jrParam.setField("TERM1"               , commUtils.getValue(gdReq, "TERM1"            , ii));
					jrParam.setField("TERM2"               , commUtils.getValue(gdReq, "TERM2"            , ii));
					jrParam.setField("TERM3"               , commUtils.getValue(gdReq, "TERM3"            , ii));
					jrParam.setField("TERM4"               , commUtils.getValue(gdReq, "TERM4"            , ii));
					jrParam.setField("TERM5"               , commUtils.getValue(gdReq, "TERM5"            , ii));
					
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updAdvSchRuleInfo", logId, methodNm, "크레인스케줄 고도화기준 수정");
					
					/**
					 * 스케줄 고도화 기준 수정시 스케줄 기준도 같이 수정
					 */
					/*
					UPDATE TB_YM_SCHEDULERULE
					   SET MODIFIER              = :V_MODIFIER             --수정자
					     , MOD_DDTT              = SYSDATE                 --수정일
					     , ADV_CRN_PRIOR         = :V_ADV_CRN_PRIOR        --고도화우선순위
					 WHERE YD_SCH_CD             = :V_YD_SCH_CD            --스케줄코드   
					 */
					jrParam.setField("ADV_CRN_PRIOR"    , commUtils.getValue(gdReq, "YD_WRK_CRN_PRIOR" , ii)); //작업크레인우선순위
					
//					String sAPP100A = ymComm.BCoilApplyYn("APP100","3","A");
//					String sAPP100C = ymComm.BCoilApplyYn("APP100","3","C");
//					String sAPP100E = ymComm.BCoilApplyYn("APP100","3","E");
//					if ("Y".equals(sAPP100A) || "Y".equals(sAPP100C) || "Y".equals(sAPP100E)) {
						commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updAdvCrnPrior", logId, methodNm, "고도화우선순위 수정");
//					}
					
					
				} else if ("2".equals(sTAB_INDEX)) {
					/**
					 * 홈위치 기준 수정
					 */
					jrParam.setField("DEL_YN"                 , commUtils.getValue(gdReq, "DEL_YN"              , ii));
					jrParam.setField("DTL_ITM1"               , commUtils.getValue(gdReq, "DTL_ITM1"            , ii));
					jrParam.setField("DTL_ITM2"               , commUtils.getValue(gdReq, "DTL_ITM2"            , ii));
					jrParam.setField("DTL_ITM3"               , commUtils.getValue(gdReq, "DTL_ITM3"            , ii));
					jrParam.setField("DTL_ITM4"               , commUtils.getValue(gdReq, "DTL_ITM4"            , ii));
					jrParam.setField("DTL_ITM5"               , commUtils.getValue(gdReq, "DTL_ITM5"            , ii));
					jrParam.setField("CD_GP"                  , commUtils.getValue(gdReq, "CD_GP"               , ii));
					jrParam.setField("ITEM"                   , commUtils.getValue(gdReq, "ITEM"                , ii));
					
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updHomeMvTermList", logId, methodNm, "홈위치기준 수정");
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updAdvSchRuleMgt
	
	
	/**
	 * 차량예정정보 전송
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord regCarUdExplainInfo(GridData gdReq) throws DAOException {
		String methodNm = "차량예정정보 전송[BCoilJspSeEJB.regCarUdExplainInfo] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

	    String szMsg = "";
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			//차량위치조회
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdGetCarNoByLoc 
			SELECT NVL(CAR_NO ,TRN_EQP_CD) AS CAR_NO
			     , CARD_NO
			  FROM TB_YD_CARPOINT
			 WHERE DEL_YN = 'N'
			   AND YD_STK_COL_GP = :V_PT_LOAD_LOC  */
			jrParam.setField("PT_LOAD_LOC"		, gdReq.getParam("YD_CARPNT_CD"));
			JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdGetCarNoByLoc", logId, methodNm, "차량위치조회"); 
			
	    	if (rsResult == null || rsResult.size() <= 0) {
	    		
	    		szMsg = gdReq.getParam("YD_CARPNT_CD") + " 해당 위치에 차량이 없습니다.";
	    		jrRtn.setField("RTN_MSG", szMsg);
	    		
				szMsg="["+methodNm+"] " + szMsg;
				commUtils.printLog(logId, szMsg, "SL");
				
				return jrRtn;
			}
	    	if (!gdReq.getParam("CAR_NO").equals(rsResult.getRecord(0).getFieldString("CAR_NO"))) {
	    		
	    		szMsg = "해당위치에 차량정보가 틀립니다. 입력차량번호:" + gdReq.getParam("CAR_NO") + ",검색결과차량번호:"+rsResult.getRecord(0).getFieldString("CAR_NO");
	    		jrRtn.setField("RTN_MSG", szMsg);
	    		
				szMsg="["+methodNm+"] " + szMsg;
				commUtils.printLog(logId, szMsg, "SL");
				
				return jrRtn;
	    	}
			
	    	//전송 데이터 설정
	    	jrParam.setField("PT_LOAD_LOC"			, gdReq.getParam("YD_CARPNT_CD")); //상차도 위치
	    	jrParam.setField("CAR_NO"				, gdReq.getParam("CAR_NO")); //차량번호	
	    	jrParam.setField("PT_CLS"				, gdReq.getParam("CAR_WK_GP")); //차량구분 "TT":TTcar, "TR":트레일러
	    	jrParam.setField("WORK_CLS"				, gdReq.getParam("RETN_WK_GP")); //작업구분 1:출하입고,2:출하출고,3:구내입고,4:구내출고
	    	jrParam.setField("CARD_NO"				, rsResult.getRecord(0).getFieldString("CARD_NO"));
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

	    	jrParam.setField("WORK_COIL_MAX_CNT"	, Integer.toString(rowCnt)); //작업총수량	
			
			for (int ii = 0; ii < rowCnt; ii++) {
				jrParam.setField("STOCK_ID_"+ii			,commUtils.getValue(gdReq, "YD_STL_NO"			, ii)); //재료번호
				jrParam.setField("LOAD_LOC_CD_"+ii		,commUtils.getValue(gdReq, "YD_CAR_UPP_LOC_CD"	, ii)); //차량적재위치
				jrParam.setField("WORK_STATE_"+ii		,commUtils.getValue(gdReq, "YD_WORK_STATE"		, ii)); //작업상태
			}

			//차량예정정보 백업 송신
			jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA7L008BackUp", jrParam));
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of regCarUdExplainInfo
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업예약관리 스케줄기동
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord procCrnWrkBookStart(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업예약관리 스케줄기동[BCoilJspSeEJB.procCrnWrkBookStart] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;
			String currDate   = commUtils.getDateTime14();	//현재시각
			
			//작업예약
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			int wrkBookCnt = 0;
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			jrParam.setField("JMS_TC_CD", "YMYMJ303"); //야드작업예약ID
			jrParam.setField("JMS_TC_CREATE_DDTT", currDate); //JMSTC생성일시
			jrParam.setField("YD_SCH_CD"  , ""); //야드스케쥴코드
			jrParam.setField("YD_EQP_ID"  , ""); //야드설비ID

			for (int ii = 0; ii < rowCnt; ii++) {
				jrParam.setField("YD_WBOOK_ID"+(++wrkBookCnt), commUtils.trim(gdReq.getHeader("YD_WBOOK_ID").getValue(ii))); //야드작업예약ID
			}
			
			jrParam.setField("SCH_CNT" , Integer.toString(wrkBookCnt)); //작업예약 개수
			
			//크레인스케줄기동 전문
			EJBConnector sndConn = new EJBConnector("default", "BCoilSchSeEJB", this);
			jrRtn = (JDTORecord)sndConn.trx("rcvYMYMJ303", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			
			/**
			 * 스케줄 취소시 해당 스케줄 자동기동 제한
			 */
			String sYD_SCH_CD  = "";
			String sTMP_SCH_CD = "";
			for (int jj = 0; jj < rowCnt; ++jj) {
				sTMP_SCH_CD = commUtils.trim(gdReq.getHeader("YD_SCH_CD").getValue(jj));
				
				if (sYD_SCH_CD.equals(sTMP_SCH_CD)) {
					continue;
				} else {
					sYD_SCH_CD = sTMP_SCH_CD;
				}
				/*
				UPDATE TB_YM_SCHEDULERULE
				   SET YD_SCH_AUTO_ST_YN = :V_YD_SCH_AUTO_ST_YN
				     , MODIFIER          = :V_MODIFIER
				     , MOD_DDTT          = SYSDATE
				 WHERE YD_SCH_CD         = :V_YD_SCH_CD
				 */
				jrParam.setField("YD_SCH_AUTO_ST_YN", "Y"); // 'Y' 스케줄 자동기동
				jrParam.setField("YD_SCH_CD"        , sYD_SCH_CD);
				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updSchAutoStYn", logId, methodNm, "TB_YM_SCHEDULERULE >> YD_SCH_AUTO_ST_YN");
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
	 *      [A] 오퍼레이션명 : 작업예약관리-삭제
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord delWrkBook(GridData gdReq) throws DAOException {
		String methodNm = "작업예약관리-삭제[BCoilJspSeEJB.delWrkBook] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			String ydWbookId  = ""; //야드작업예약ID
			String ydEqpId    = ""; //야드설비ID
		    String ydSchCd    = ""; //야드스케쥴코드
			
			
			//처리완료한 야드작업예약ID
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				//DAO Parameter - Log ID, Method, 수정자 Set
				JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
				
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
	 * 이송작업재료등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updCarFtMvMtl(GridData gdReq) throws DAOException {
		String methodNm = "이송작업재료등록[BCoilJspFaEJB.updCarFtMvMtl] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			String sCoilNo;
			String sStockMv;
			JDTORecordSet rsResult;

			//등록 할  레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {

				sCoilNo = commUtils.getValue(gdReq, "STL_NO", ii);
				
				//TB_YM_STOCK 에 존재 하는지 확인
				jrParam.setField("STL_NO" , sCoilNo);
				/*com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYmStockchk
				SELECT A.STOCK_ID         AS STL_NO
				      ,B.COIL_T           AS YD_MTL_T
				      ,B.COIL_W           AS YD_MTL_W
				      ,B.COIL_LEN         AS YD_MTL_L
				      ,B.COIL_WT          AS YD_MTL_WT
				      ,''                         AS YD_AIM_YD_GP
				      ,''                         AS YD_AIM_BAY_GP
				  FROM TB_YM_STOCK  A
				      ,USRPTA.TB_PT_COILCOMM B
				 WHERE  A.STOCK_ID    =B.COIL_NO 
				   AND  A.STOCK_ID   = :V_STL_NO */
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYmStockchk", logId, methodNm, "TB_YM_STOCK 에 존재 하는지 확인");
				
				if(rsResult.size()<=0) {
					
					//코일공통의 차공정코드로 이동조건 설정, 리턴 값이 ""이면 코일공통에 존재하지 않는 코일 번호로 Error 처리 한다.
					sStockMv	= ymComm.getStockMv(logId, methodNm, sCoilNo);
					
					if("".equals(sStockMv)) {
						throw new Exception("코일공통에 존재하지 않는 COIL_NO : " + sCoilNo);
					}
					
					//TB_YM_STOCK에 존재 한지 않으면 생성한다.
					jrParam.setField("STOCK_ID" 		, sCoilNo);
					jrParam.setField("STOCK_ITEM" 		, YmConstant.ITEM_CM);
					jrParam.setField("STOCK_MOVE_TERM" 	, sStockMv);
					/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.insStock 
					MERGE INTO TB_YM_STOCK ST USING (
					    SELECT :V_STOCK_ID          AS STOCK_ID                                 --재료번호
					         , :V_MODIFIER          AS MODIFIER         --수정자
					         , SYSDATE              AS MOD_DDTT         --수정일시
					         , 'N'                  AS DEL_YN           --삭제유무
					         , :V_STOCK_ITEM        AS STOCK_ITEM       --저장품 품목
					         , :V_STOCK_MOVE_TERM   AS STOCK_MOVE_TERM  --저장품 이동 조건
					      FROM DUAL
					) DD ON ( ST.STOCK_ID = DD.STOCK_ID)

					WHEN NOT MATCHED THEN
					    INSERT (
					           STOCK_ID             , STOCK_ITEM        , STOCK_MOVE_TERM 
					         , REGISTER             , REG_DDTT          , MODIFIER  
					         , MOD_DDTT             , DEL_YN    
					         )
					    VALUES (
					           :V_STOCK_ID          , DD.STOCK_ITEM     , DD.STOCK_MOVE_TERM 
					         , DD.MODIFIER          , DD.MOD_DDTT       , DD.MODIFIER  
					         , DD.MOD_DDTT          , DD.DEL_YN 
					         )
					WHEN MATCHED THEN 
					    UPDATE SET
					           STOCK_ITEM       = (CASE WHEN KEEPSTOCK_STL_YN='Y' THEN STOCK_ITEM ELSE DD.STOCK_ITEM END) 
					         , STOCK_MOVE_TERM  = DD.STOCK_MOVE_TERM 
					         , MODIFIER         = DD.MODIFIER 
					         , MOD_DDTT         = DD.MOD_DDTT     */     
					commDao.insert(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.insStock", logId, methodNm, "TB_YM_STOCK 생성");
				}
				
				//이송작업재료삭제
				jrParam.setField("YD_CAR_SCH_ID"	, commUtils.trim(gdReq.getParam("YD_CAR_SCH_ID"))); 
				jrParam.setField("STL_NO"			, commUtils.getValue(gdReq, "OLD_SSTL_NO", ii)); 

				/*  
				DELETE USRYDA.TB_YD_CARFTMVMTL
				WHERE  YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				AND    STL_NO = :V_STL_NO   
				*/
				commDao.insert(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.delCarFtMvMtl", logId, methodNm, "이송작업재료삭제");
				
				//이송작업재료등록
				jrParam.setField("STL_NO"			, sCoilNo); 
				jrParam.setField("YD_CAR_SCH_ID"	, commUtils.trim(gdReq.getParam("YD_CAR_SCH_ID"))); 
				jrParam.setField("STACK_BED_GP"	    , commUtils.getValue(gdReq, "STACK_BED_GP", ii)); 
				jrParam.setField("STACK_LAYER_GP"	, commUtils.getValue(gdReq, "STACK_LAYER_GP",ii)); 
				jrParam.setField("MODIFIER"	        , commUtils.trim(gdReq.getParam("userid"))); 

				/*MERGE INTO TB_YD_CARFTMVMTL TM USING (
				    SELECT STK.STOCK_ID
				          ,COIL.HCR_GP
				          ,COIL.RECORD_PROG_STAT AS STL_PROG_CD
				          ,NVL(:V_STACK_BED_GP,LAY.STACK_BED_GP) AS STACK_BED_GP
				          ,NVL(:V_STACK_LAYER_GP,LAY.STACK_LAYER_GP) AS STACK_LAYER_GP
				          ,:V_YD_CAR_SCH_ID AS YD_CAR_SCH_ID
				          ,:V_MODIFIER AS MODIFIER
				          ,SYSDATE AS MOD_DDTT
				          ,'N' AS DEL_YN
				    FROM   TB_YM_STOCK STK
				          ,(SELECT  STOCK_ID
				                   ,STACK_BED_GP
				                   ,STACK_LAYER_GP 
				              FROM  USRYMA.TB_YM_STACKLAYER
				              WHERE STOCK_ID = :V_STL_NO
				                AND STACK_LAYER_STAT IN ('C','U') ) LAY 권상대기 적치중
				          ,USRPTA.TB_PT_COILCOMM   COIL    
				    WHERE  STK.STOCK_ID = :V_STL_NO
				      AND  STK.STOCK_ID = COIL.COIL_NO 
				      AND  STK.STOCK_ID = LAY.STOCK_ID(+)
				 
				) DD ON (TM.YD_CAR_SCH_ID = DD.YD_CAR_SCH_ID AND TM.STL_NO = DD.STOCK_ID )    
				WHEN NOT MATCHED THEN
				INSERT (TM.YD_CAR_SCH_ID, TM.STL_NO, TM.REGISTER, TM.REG_DDTT,
				        TM.MODIFIER, TM.MOD_DDTT, TM.DEL_YN, TM.YD_STK_BED_NO,
				        TM.YD_STK_LYR_NO, TM.HCR_GP, TM.STL_PROG_CD)
				VALUES (DD.YD_CAR_SCH_ID, DD.STOCK_ID, DD.MODIFIER, DD.MOD_DDTT,
				        DD.MODIFIER, DD.MOD_DDTT, DD.DEL_YN, DD.STACK_BED_GP,
				        DD.STACK_LAYER_GP, DD.HCR_GP, DD.STL_PROG_CD)
				WHEN MATCHED THEN
				UPDATE SET
				    TM.MODIFIER = DD.MODIFIER
				   ,TM.MOD_DDTT = DD.MOD_DDTT
				   ,TM.DEL_YN = DD.DEL_YN
				   ,TM.YD_STK_BED_NO = DD.STACK_BED_GP
				   ,TM.YD_STK_LYR_NO = DD.STACK_LAYER_GP
				   ,TM.HCR_GP = DD.HCR_GP
				   ,TM.STL_PROG_CD = DD.STL_PROG_CD*/		
				
				commDao.insert(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updCarFtMvMtl", logId, methodNm, "이송작업재료등록");
			}
			
			//차량 작업 상태,매수,작업완료시간 update
			jrParam.setField("YD_CAR_SCH_ID"	, commUtils.trim(gdReq.getParam("YD_CAR_SCH_ID"))); 
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCarSchWrkSt", logId, methodNm, "이송차량스케줄 차량작업상태 수정");
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updCarFtMvMtl
	
	
	/**
	 * 이송작업재료삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord delCarFtMvMtl(GridData gdReq) throws DAOException {
		String methodNm = "이송작업재료삭제[BCoilJspFaEJB.delCarFtMvMtl] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			//등록 할  레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				//이송작업재료삭제
				jrParam.setField("YD_CAR_SCH_ID"	, commUtils.trim(gdReq.getParam("YD_CAR_SCH_ID"))); 
				jrParam.setField("STL_NO"			, commUtils.getValue(gdReq, "OLD_SSTL_NO", ii)); 
 				/* 이송작업재료삭제 -- com.inisteel.cim.ym.bcoil.dao.BCoilDAO.delCarFtMvMtl 

				DELETE USRYDA.TB_YD_CARFTMVMTL
				WHERE  YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				AND    STL_NO = :V_STL_NO   
				*/
				commDao.insert(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.delCarFtMvMtl", logId, methodNm, "이송작업재료삭제");
			}
			
			//차량 작업 상태,매수,작업완료시간 update
			jrParam.setField("YD_CAR_SCH_ID"	, commUtils.trim(gdReq.getParam("YD_CAR_SCH_ID"))); 
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCarSchWrkSt", logId, methodNm, "이송차량스케줄 차량작업상태 수정");
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of delCarFtMvMtl
	
	/**
	 * 이송작업재료위치변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord chgCarFtMvMtl(GridData gdReq) throws DAOException {
		String methodNm = "이송작업재료위치변경[BCoilJspFaEJB.chgCarFtMvMtl] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			//등록 할  레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				//이송작업재료삭제
				jrParam.setField("YD_CAR_SCH_ID"	, commUtils.trim(gdReq.getParam("YD_CAR_SCH_ID"))); 
				jrParam.setField("STL_NO"			, commUtils.getValue(gdReq, "STL_NO", ii)); 
				/* 이송작업재료삭제 -- com.inisteel.cim.ym.bcoil.dao.BCoilDAO.delCarFtMvMtl 

				DELETE USRYDA.TB_YD_CARFTMVMTL
				WHERE  YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				AND    STL_NO = :V_STL_NO   
				*/
				commDao.insert(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.delCarFtMvMtl", logId, methodNm, "이송작업재료위치변경");
			}
			
			for (int ii = 0; ii < rowCnt; ii++) {
				//이송작업재료등록
				jrParam.setField("STL_NO"			, commUtils.getValue(gdReq, "OLD_SSTL_NO", ii)); 
				jrParam.setField("YD_CAR_SCH_ID"	, commUtils.trim(gdReq.getParam("YD_CAR_SCH_ID"))); 
				jrParam.setField("STACK_BED_GP"  	, commUtils.getValue(gdReq, "STACK_BED_GP", ii)); 
				jrParam.setField("STACK_LAYER_GP"	, commUtils.getValue(gdReq, "STACK_LAYER_GP",ii)); 
				jrParam.setField("MODIFIER"	        , commUtils.trim(gdReq.getParam("userid"))); 

				/*MERGE INTO TB_YD_CARFTMVMTL TM USING (
				    SELECT STK.STOCK_ID
				          ,COIL.HCR_GP
				          ,COIL.RECORD_PROG_STAT AS STL_PROG_CD
				          ,NVL(:V_STACK_BED_GP,LAY.STACK_BED_GP) AS STACK_BED_GP
				          ,NVL(:V_STACK_LAYER_GP,LAY.STACK_LAYER_GP) AS STACK_LAYER_GP
				          ,:V_YD_CAR_SCH_ID AS YD_CAR_SCH_ID
				          ,:V_MODIFIER AS MODIFIER
				          ,SYSDATE AS MOD_DDTT
				          ,'N' AS DEL_YN
				    FROM   TB_YM_STOCK STK
				          ,(SELECT  STOCK_ID
				                   ,STACK_BED_GP
				                   ,STACK_LAYER_GP 
				              FROM  USRYMA.TB_YM_STACKLAYER
				              WHERE STOCK_ID = :V_STL_NO
				                AND STACK_LAYER_STAT IN ('C','U') ) LAY 권상대기 적치중
				          ,USRPTA.TB_PT_COILCOMM   COIL    
				    WHERE  STK.STOCK_ID = :V_STL_NO
				      AND  STK.STOCK_ID = COIL.COIL_NO 
				      AND  STK.STOCK_ID = LAY.STOCK_ID(+)
				 
				) DD ON (TM.YD_CAR_SCH_ID = DD.YD_CAR_SCH_ID AND TM.STL_NO = DD.STOCK_ID )    
				WHEN NOT MATCHED THEN
				INSERT (TM.YD_CAR_SCH_ID, TM.STL_NO, TM.REGISTER, TM.REG_DDTT,
				        TM.MODIFIER, TM.MOD_DDTT, TM.DEL_YN, TM.YD_STK_BED_NO,
				        TM.YD_STK_LYR_NO, TM.HCR_GP, TM.STL_PROG_CD)
				VALUES (DD.YD_CAR_SCH_ID, DD.STOCK_ID, DD.MODIFIER, DD.MOD_DDTT,
				        DD.MODIFIER, DD.MOD_DDTT, DD.DEL_YN, DD.STACK_BED_GP,
				        DD.STACK_LAYER_GP, DD.HCR_GP, DD.STL_PROG_CD)
				WHEN MATCHED THEN
				UPDATE SET
				    TM.MODIFIER = DD.MODIFIER
				   ,TM.MOD_DDTT = DD.MOD_DDTT
				   ,TM.DEL_YN = DD.DEL_YN
				   ,TM.YD_STK_BED_NO = DD.STACK_BED_GP
				   ,TM.YD_STK_LYR_NO = DD.STACK_LAYER_GP
				   ,TM.HCR_GP = DD.HCR_GP
				   ,TM.STL_PROG_CD = DD.STL_PROG_CD*/					
				commDao.insert(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updCarFtMvMtl", logId, methodNm, "이송작업재료등록");
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of chgCarFtMvMtl	
	
	
	/**
	 *      [A] 오퍼레이션명 : 이송차량 실적처리 팝업 - 등록
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtMvCarStatSet2(GridData gdReq) throws DAOException {
		String methodNm = "이송차량 실적처리 팝업 - 등록 [BCoilSeEJB.trtMvCarStatSet2] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			
			String sJMS_TC_CD 			= commUtils.trim(gdReq.getParam("JMS_TC_CD"));
			String sTRN_WRK_FULLVOID_GP = commUtils.trim(gdReq.getParam("TRN_WRK_FULLVOID_GP"));
			String sTRN_EQP_CD			= commUtils.trim(gdReq.getParam("TRN_EQP_CD"));
			String sYD_CAR_SCH_ID		= commUtils.trim(gdReq.getParam("YD_CAR_SCH_ID"));
			String sSPOS_WLOC_CD 		= commUtils.trim(gdReq.getParam("SPOS_WLOC_CD"));
			String sYD_PNT_CD1 			= commUtils.trim(gdReq.getParam("YD_PNT_CD1"));
			String sYD_CARLD_STOP_LOC 	= commUtils.trim(gdReq.getParam("YD_CARLD_STOP_LOC"));
			String sARR_WLOC_CD 		= commUtils.trim(gdReq.getParam("ARR_WLOC_CD"));
			String sYD_PNT_CD3 			= commUtils.trim(gdReq.getParam("YD_PNT_CD3"));
			String sYD_CARUD_STOP_LOC 	= commUtils.trim(gdReq.getParam("YD_CARUD_STOP_LOC"));	
			String sTO_LOC 				= commUtils.trim(gdReq.getParam("TO_LOC"));
			String sWLOC_CD 			= null;
			String sYD_PNT_CD 			= null;

			String modifier = commUtils.trim(gdReq.getParam("userid"      ));	//수정자
			String currDate = commUtils.getDateTime14();						//현재시각

			if ("".equals(sTRN_EQP_CD)) {
				throw new Exception("운송장비코드가 없습니다.");
			}

			//Return Value
			JDTORecord jrRtn = null;
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, modifier);
			
			jrYdMsg.setField("JMS_TC_CD"         		, sJMS_TC_CD);
			
			if ("TSYDJ003".equals(sJMS_TC_CD)) { //소재차량도착
				
				jrYdMsg.setField("TRN_EQP_CD"   	  		, sTRN_EQP_CD);
				
				if("F".equals(sTRN_WRK_FULLVOID_GP)) {
					//하차도착
					jrYdMsg.setField("ARR_WLOC_CD"     	  		, sARR_WLOC_CD);
					jrYdMsg.setField("ARR_YD_PNT_CD"   	  		, sYD_PNT_CD3);
					jrYdMsg.setField("TRN_WRK_FULLVOID_GP"		, sTRN_WRK_FULLVOID_GP);
				} else if("E".equals(sTRN_WRK_FULLVOID_GP)) {
					//상차도착
					jrYdMsg.setField("ARR_WLOC_CD"     	  		, sSPOS_WLOC_CD);
					jrYdMsg.setField("ARR_YD_PNT_CD"   	  		, sYD_PNT_CD1);
					jrYdMsg.setField("TRN_WRK_FULLVOID_GP"		, sTRN_WRK_FULLVOID_GP);
				}

				EJBConnector sndConn = new EJBConnector("default", "YmCommCarMvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvTSYDJ003", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
				
			} else if("TSYDJ004".equals(sJMS_TC_CD)) { //소재차량출발
				
				jrYdMsg.setField("TRN_EQP_CD"   	  		, sTRN_EQP_CD);
				jrYdMsg.setField("TRN_WRK_FULLVOID_GP"		, sTRN_WRK_FULLVOID_GP);
				jrYdMsg.setField("TRN_EQP_STK_CAPA"		    , "80000");
				
				if("F".equals(sTRN_WRK_FULLVOID_GP)) { //영차:하차하러 출발 
					
					jrYdMsg.setField("SPOS_WLOC_CD"     	  	, sSPOS_WLOC_CD);
					jrYdMsg.setField("SPOS_YD_PNT_CD"   	  	, sYD_PNT_CD1);
					jrYdMsg.setField("ARR_WLOC_CD"     	  		, sARR_WLOC_CD);
					jrYdMsg.setField("ARR_YD_PNT_CD"   	  		, sYD_PNT_CD3);
					
				} else if("E".equals(sTRN_WRK_FULLVOID_GP)) { //하차완료후 출발처리로 착지개소를 DMY1P로 줌으로써 차량스케줄 완료처리를 한다.
					
					jrYdMsg.setField("SPOS_WLOC_CD"     	  	, sARR_WLOC_CD);
					jrYdMsg.setField("SPOS_YD_PNT_CD"   	  	, sYD_PNT_CD3);
					jrYdMsg.setField("ARR_WLOC_CD"     	  		, "DMY1P");
					jrYdMsg.setField("ARR_YD_PNT_CD"   	  		, "");
				}

				EJBConnector sndConn = new EJBConnector("default", "YmCommCarMvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvTSYDJ004", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });

			} else if("YDTSJ007".equals(sJMS_TC_CD)) { //소재차량상차개시
				
				//차량진행상태를 하차완료로 설정
				jrParam.setField("YD_CAR_PROG_STAT"		, "4"		);  //상차개시
				jrParam.setField("YD_CARLD_ST_DT"		, currDate	);  //상차개시일시 ""이면 이전값 유지
				jrParam.setField("YD_CARLD_CMPL_DT"		, ""		);  //상차완료일시 ""이면 이전값 유지
				jrParam.setField("YD_CARUD_ST_DT"		, ""		);  //하차개시일시 ""이면 이전값 유지
				jrParam.setField("YD_CARUD_CMPL_DT"		, ""		);  //하차완료일시 ""이면 이전값 유지
				jrParam.setField("YD_CAR_SCH_ID"		, sYD_CAR_SCH_ID); 
				
				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updMvCarSchCmpl", logId, methodNm, "이송차량스케줄 상차개시로 수정");
				
				jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDate		); //JMSTC생성일시
				jrYdMsg.setField("TRN_EQP_CD"        , sTRN_EQP_CD	); //운송장비코드
				jrYdMsg.setField("SPOS_WLOC_CD"      , sSPOS_WLOC_CD); //발지개소코드
				jrYdMsg.setField("SPOS_YD_PNT_CD"    , sYD_PNT_CD1	); //발지야드포인트코드
				jrYdMsg.setField("ARR_WLOC_CD"       , sARR_WLOC_CD	); //착지개소코드
				jrYdMsg.setField("TRN_WRK_ST_DT"     , currDate    	); //운송작업시작일시
				
				//전송할 전문에 추가
				jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
				
			} else if("YDTSJ008".equals(sJMS_TC_CD)) { //소재차량상차완료
				
				//차량진행상태를 상차완료로 설정
				jrParam.setField("YD_CAR_PROG_STAT"		, "5"		);  //상차완료
				jrParam.setField("YD_CARLD_ST_DT"		, ""		);  //상차개시일시 ""이면 이전값 유지
				jrParam.setField("YD_CARLD_CMPL_DT"		, currDate	);  //상차완료일시 ""이면 이전값 유지
				jrParam.setField("YD_CARUD_ST_DT"		, ""		);  //하차개시일시 ""이면 이전값 유지
				jrParam.setField("YD_CARUD_CMPL_DT"		, ""		);  //하차완료일시 ""이면 이전값 유지
				jrParam.setField("YD_CAR_SCH_ID"		, sYD_CAR_SCH_ID); 
				
				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updMvCarSchCmpl", logId, methodNm, "이송차량스케줄 상차완료로 수정");
				
				jrYdMsg.setField("YD_CAR_SCH_ID"     , sYD_CAR_SCH_ID); //차량스케줄ID
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YDTSJ008", jrYdMsg));
				
			} else if("YDTSJ009".equals(sJMS_TC_CD)) { //소재차량하차개시
				
				//차량진행상태를 하차완료로 설정
				jrParam.setField("YD_CAR_PROG_STAT"		, "D"		);  //하차개시
				jrParam.setField("YD_CARLD_ST_DT"		, ""		);  //상차개시일시 ""이면 이전값 유지
				jrParam.setField("YD_CARLD_CMPL_DT"		, ""		);  //상차완료일시 ""이면 이전값 유지
				jrParam.setField("YD_CARUD_ST_DT"		, currDate	);  //하차개시일시 ""이면 이전값 유지
				jrParam.setField("YD_CARUD_CMPL_DT"		, ""		);  //하차완료일시 ""이면 이전값 유지
				jrParam.setField("YD_CAR_SCH_ID"		, sYD_CAR_SCH_ID); 
				
				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updMvCarSchCmpl", logId, methodNm, "이송차량스케줄 하차개시로 수정");
				
				jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDate    ); //JMSTC생성일시
				jrYdMsg.setField("TRN_EQP_CD"        , sTRN_EQP_CD); //운송장비코드
				jrYdMsg.setField("ARR_WLOC_CD"       , sARR_WLOC_CD); //착지개소코드
				jrYdMsg.setField("ARR_YD_PNT_CD"     , sYD_PNT_CD3); //착지야드포인트코드
				jrYdMsg.setField("TRN_WRK_ST_DT"     , currDate    ); //운송작업시작일시
				
				//전송할 전문에 추가
				jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
				
			} else if("YDTSJ010".equals(sJMS_TC_CD)) { //소재차량하차완료
				
				//차량진행상태를 하차완료로 설정
				jrParam.setField("YD_CAR_PROG_STAT"		, "E"		);  //하차완료
				jrParam.setField("YD_CARLD_ST_DT"		, ""		);  //상차개시일시 ""이면 이전값 유지
				jrParam.setField("YD_CARLD_CMPL_DT"		, ""		);  //상차완료일시 ""이면 이전값 유지
				jrParam.setField("YD_CARUD_ST_DT"		, ""		);  //하차개시일시 ""이면 이전값 유지
				jrParam.setField("YD_CARUD_CMPL_DT"		, currDate	);  //하차완료일시 ""이면 이전값 유지
				jrParam.setField("YD_CAR_SCH_ID"		, sYD_CAR_SCH_ID); 
				
				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updMvCarSchCmpl", logId, methodNm, "이송차량스케줄 하차완료로 수정");
				jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDate    ); //JMSTC생성일시
				jrYdMsg.setField("TRN_EQP_CD"        , sTRN_EQP_CD); //운송장비코드
				jrYdMsg.setField("ARR_WLOC_CD"       , sARR_WLOC_CD); //착지개소코드
				jrYdMsg.setField("ARR_YD_PNT_CD"     , sYD_PNT_CD3); //착지야드포인트코드
				jrYdMsg.setField("TRN_WRK_ST_DT"     , currDate    ); //운송작업시작일시
				
				//전송할 전문에 추가
				jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
				
			} else if("YDTSJ011".equals(sJMS_TC_CD)) { //소재차량Point지시

				//야드적치열구분으로 차량포인트 정보 조회
				jrParam.setField("YD_STK_COL_GP", sTO_LOC);
				/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdPntByStkColGp
				SELECT YD_CARPNT_CD
				      ,YD_STK_COL_ACT_STAT
				      ,YD_CAR_USETYPE_GP
				      ,YD_GP
				      ,YD_BAY_GP
				      ,YD_STK_COL_GP
				      ,TRN_EQP_CD
				      ,CAR_NO
				      ,CARD_NO
				      ,WLOC_CD
				      ,YD_PNT_CD
				      ,YD_CARPNT_DESC
				      ,YD_SPAN_FROM
				      ,YD_SPAN_TO
				      ,YD_FRM_YN
				  FROM TB_YD_CARPOINT  
				 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP */
				JDTORecordSet jsCol = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdPntByStkColGp", logId, methodNm, "야드적치열구분으로 차량포인트 정보 조회");
				
				if(jsCol != null && jsCol.size() > 0) {
					sWLOC_CD	= commUtils.trim(jsCol.getRecord(0).getFieldString("WLOC_CD"));
					sYD_PNT_CD	= commUtils.trim(jsCol.getRecord(0).getFieldString("YD_PNT_CD"));
					
					if("".equals(sWLOC_CD) || "".equals(sYD_PNT_CD)) {
						
						throw new Exception(sTO_LOC + " 의 개소코드 또는 야드포인트에 NULL 값이 있습니다.");
					}
					
					if(!"".equals(commUtils.trim(jsCol.getRecord(0).getFieldString("TRN_EQP_CD")))) {
						
						throw new Exception(sTO_LOC + " 에 이미 " + commUtils.trim(jsCol.getRecord(0).getFieldString("TRN_EQP_CD")) + " 운송장비가 점유하고  있습니다.");
					}
					
					if(!"".equals(commUtils.trim(jsCol.getRecord(0).getFieldString("CAR_NO")))) {
						
						throw new Exception(sTO_LOC + " 에 이미 " + commUtils.trim(jsCol.getRecord(0).getFieldString("CAR_NO")) + " 차량이 점유하고  있습니다.");
					}
					
				} else {
					throw new Exception(sTO_LOC + " 의 개소코드와 야드포인트를 TB_YD_CARPOINT 에서 찾지 못했습니다.");
				}
				
				jrYdMsg.setField("JMS_TC_CD"         	, sJMS_TC_CD	); //"YDTSJ011"
				jrYdMsg.setField("JMS_TC_CREATE_DDTT"	, currDate  	); //JMSTC생성일시
				jrYdMsg.setField("TRN_EQP_CD"        	, sTRN_EQP_CD	); //운송장비코드
				jrYdMsg.setField("WLOC_CD"     	 		, sWLOC_CD		);
				jrYdMsg.setField("YD_PNT_CD"     		, sYD_PNT_CD	); 
				jrYdMsg.setField("PNT_WO_GP"     		, "A"    		);
				jrYdMsg.setField("PNT_WO_DT"     		, currDate 		); 
				
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
				
				//이송차량스케줄 수정 
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
				/* 이송차량스케줄 상하차 포인트지시 수정 -- com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updMvCarSchPntWo 
				UPDATE USRYDA.TB_YD_CARSCH
				SET    MOD_DDTT = SYSDATE
				      ,MODIFIER = :V_MODIFIER
				      ,YD_CAR_PROG_STAT = NVL(:V_YD_CAR_PROG_STAT,YD_CAR_PROG_STAT)
				      ,SPOS_WLOC_CD = NVL(:V_SPOS_WLOC_CD,SPOS_WLOC_CD)
				      ,YD_CARLD_PNT_WO_DT = DECODE(NVL(:V_YD_CARLD_PNT_WO_DT,'NULL'),'NULL',YD_CARLD_PNT_WO_DT,SYSDATE)
				      ,YD_PNT_CD1 = NVL(:V_YD_PNT_CD1,YD_PNT_CD1)
				      ,YD_CARLD_STOP_LOC = NVL(:V_YD_CARLD_STOP_LOC,YD_CARLD_STOP_LOC)
				      ,ARR_WLOC_CD = NVL(:V_ARR_WLOC_CD,ARR_WLOC_CD)
				      ,YD_CARUD_PNT_WO_DT = DECODE(NVL(:V_YD_CARUD_PNT_WO_DT,'NULL'),'NULL',YD_CARUD_PNT_WO_DT,SYSDATE)
				      ,YD_PNT_CD3 = NVL(:V_YD_PNT_CD3,YD_PNT_CD3)
				      ,YD_CARUD_STOP_LOC = NVL(:V_YD_CARUD_STOP_LOC,YD_CARUD_STOP_LOC)
				WHERE  YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID   */			
				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updMvCarSchPntWo", logId, methodNm, "차량포이트 지시 수정");
				
				//TB_YM_STACKCOL 예약정보등록 
				jrParam.setField("STACK_STAT"	, "L"); 
				jrParam.setField("CAR_CARD_NO"	, sTRN_EQP_CD);
				jrParam.setField("STACK_COL_GP"	, sTO_LOC);
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateEquipcolStat       
				UPDATE TB_YM_STACKCOL
				   SET STACK_STAT = :V_STACK_STAT
				      ,CAR_CARD_NO = :V_CAR_CARD_NO
				      ,MODIFIER = :V_MODIFIER
				      ,MOD_DDTT = SYSDATE
				 WHERE STACK_COL_GP = :V_STACK_COL_GP */ 
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateEquipcolStat", logId, methodNm, "TB_YM_STACKCOL 예약정보등록");
				
				//TB_YD_CARPOINT 포인트지시 예약하기
		        EJBConnector ejbConn1 = new EJBConnector("default","YmCommCarMvSeEJB",this);
				ejbConn1.trx("YmCarPointinforeg", new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class},
			  	             new Object[]{"3","",sTRN_EQP_CD,sTO_LOC,"","","R",logId,methodNm});
				
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of trtMvCarStatSet2		
	
	
	/**
	 * 적치단 수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet updStacklayerStat(JDTORecord recPara) throws DAOException {
		String methodNm = "적치단수정[BCoilJspSeEJB.updStacklayerStat] < " + recPara.getResultMsg();
		String logId = recPara.getResultCode();
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+", recPara);
			
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			
			commDao.update(recPara, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.bcoilPdaStkLocRegjl.updStacklayerStat");
			
			commUtils.printLog(logId, methodNm, "S-", recPara);
			
			return outRecSet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of updStacklayerStat	
	
	
	/**
	 * 크레인 스케줄 삭제
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet delCrnSch(JDTORecord recPara) throws DAOException {
		String methodNm = "크레인 스케줄 삭제[BCoilJspSeEJB.delCrnSch] < " + recPara.getResultMsg();
		String logId = recPara.getResultCode();
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+", recPara);
			
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			
			commDao.delete(recPara, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.bcoilPdaStkLocRegjl.delCrnSch");
			
			commUtils.printLog(logId, methodNm, "S-", recPara);
			
			return outRecSet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of delCrnSch	
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업요구현황조회 크레인변경
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updCraneChange(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업요구현황조회 크레인변경[BCoilJspSeEJB.updCraneChange] < " + gdReq.getNavigateValue();
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
			String sYD_EQP_WRK_MODE2 = "";//유무인여부
			String sOLD_WORK_MODE  = ""; //이전 크레인의 on off-line 상태
			String sOLD_WPROG_STAT = ""; //이전 크레인 설비상태
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
				/*
				SELECT CS.YD_WBOOK_ID
				      ,CS.YD_WRK_PROG_STAT
				      ,CS.YD_SCH_CD
				      ,CS.YD_EQP_ID
				      ,CS.CHG_YD_EQP_ID
				      ,CS.CHG_YD_SCH_PRIOR
				      ,(SELECT DECODE(WPROG_STAT, 'B', WPROG_STAT, 'W') FROM TB_YM_EQUIP EP WHERE EP.EQUIP_GP = CS.CHG_YD_EQP_ID) AS CHG_YD_EQP_STAT
				      ,EQ.WORK_MODE AS CHG_YD_EQP_WRK_MODE
				      ,EQ.YD_EQP_WRK_MODE2
				      ,(SELECT YD_EQP_WRK_MODE2 FROM TB_YM_EQUIP WHERE EQUIP_GP = CS.YD_EQP_ID) AS OLD_YD_EQP_WRK_MODE2
				      ,(SELECT WORK_MODE        FROM TB_YM_EQUIP WHERE EQUIP_GP = CS.YD_EQP_ID) AS OLD_WORK_MODE
				      ,(SELECT WPROG_STAT       FROM TB_YM_EQUIP WHERE EQUIP_GP = CS.YD_EQP_ID) AS OLD_WPROG_STAT
				  FROM TB_YM_EQUIP EQ
				      ,(
				        SELECT CS.YD_WBOOK_ID
				              ,CS.YD_WRK_PROG_STAT
				              ,CS.YD_SCH_CD
				              ,CS.YD_EQP_ID
				              ,(CASE WHEN CS.YD_EQP_ID = SR.YD_WRK_CRN  THEN YD_ALT_CRN
				                     WHEN CS.YD_EQP_ID = SR.YD_ALT_CRN  THEN YD_WRK_CRN
				                     ELSE CS.YD_EQP_ID END) AS CHG_YD_EQP_ID
				              , CS.YD_SCH_PRIOR  AS CHG_YD_SCH_PRIOR
				          FROM TB_YM_CRNSCH       CS
				              ,TB_YM_SCHEDULERULE SR
				         WHERE CS.YD_SCH_CD     = SR.YD_SCH_CD
				           AND CS.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				           AND CS.DEL_YN = 'N'           
				           ) CS
				 WHERE CS.CHG_YD_EQP_ID = EQ.EQUIP_GP
				 */
				JDTORecordSet jsCrn = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCraneChange1", logId, methodNm, "크레인변경 조회");

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
				sYD_EQP_WRK_MODE2 = commUtils.trim(jrCrn.getFieldString("OLD_YD_EQP_WRK_MODE2" )); //유무인 여부
				sOLD_WORK_MODE    = commUtils.trim(jrCrn.getFieldString("OLD_WORK_MODE"    ));
				sOLD_WPROG_STAT   = commUtils.trim(jrCrn.getFieldString("OLD_WPROG_STAT"   ));
				
				if ("A".equals(sYD_EQP_WRK_MODE2) && !"W".equals(ydWrkProgStat)) {
					throw new Exception("자동화크레인의 경우 상태값이 없을 경우 변경하실 수 있습니다.");
				}
				
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
				} else if (ydEqpId.equals(chgYdEqpId)) {
					throw new Exception("변경 크레인 [" + chgYdEqpId + "]과 현재 크레인과 같습니다. ");
				}

				commUtils.printLog(logId, "크레인변경 [ " + ydWbookId + " : " + ydEqpId + " >> " + chgYdEqpId + " ]", "SL");

				/**********************************************************
				* 2. 작업예약 및 크레인스케줄 Table에  대체 크레인ID와 우선순위를 Update
				**********************************************************/
				jrParam.setField("MODIFIER"		, modifier);
				jrParam.setField("YD_SCH_PRIOR"	, chgYdSchPrior);
				jrParam.setField("YD_EQP_ID"   	, chgYdEqpId   );
				
				//작업예약 Table 우선순위 Update
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updWrkBookPrior", logId, methodNm, "TB_YM_WRKBOOK");				
				
				if ("1".equals(ydWrkProgStat) || "S".equals(ydWrkProgStat)) {
					/**********************************************************
					* 2.1  이전 크레인의 작업지시 취소 전문 송신
					**********************************************************/
					jrParam.setField("MSG_GP"       , "D"       ); //전문구분(취소)
					jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA7L004", jrParam));
				}
				
				//크레인스케줄 Table 크레인ID, 우선순위 Update
				/*
				UPDATE TB_YM_CRNSCH
				   SET MODIFIER     = :V_MODIFIER
				      ,MOD_DDTT     = SYSDATE
				      ,YD_SCH_PRIOR = TO_NUMBER(:V_YD_SCH_PRIOR)
				      ,YD_EQP_ID    = NVL(:V_YD_EQP_ID,YD_EQP_ID)
				      ,YD_WRK_PROG_STAT= 'W' 
				 WHERE YD_WBOOK_ID  = :V_YD_WBOOK_ID
				   AND YD_WRK_PROG_STAT IN ('1','W', 'S')
				   AND DEL_YN = 'N'
				 */
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCrnWrkMgtW", logId, methodNm,  "TB_YM_CRNSCH");				
				
			
				/**********************************************************
				* 3. 현 작업상태가 권상지시[1]인 경우
				**********************************************************/
				if ("1".equals(ydWrkProgStat) || "S".equals(ydWrkProgStat)) {
					/**********************************************************
					* 3.1 변경 크레인의 설비 Table 상태정보를 Update
					**********************************************************/
					jrParam.setField("MODIFIER"		, modifier);
					jrParam.setField("EQUIP_STAT"   , "1"); //야드설비상태 : 권상작업지시
					jrParam.setField("EQUIP_GP"   	, chgYdEqpId   );
					/*
					UPDATE TB_YM_EQUIP
					   SET MODIFIER    = :V_MODIFIER
					      ,MOD_DDTT    = SYSDATE
					      ,WPROG_STAT  = :V_EQUIP_STAT
					 WHERE EQUIP_GP    = :V_EQUIP_GP
					   AND DEL_YN      = 'N'
					 */
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCrnYsEqp", logId, methodNm,  "TB_YM_EQUIP");				

					/**********************************************************
					* 3.2 변경 크레인의 크레인작업지시요구 처리
					**********************************************************/
					//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
					JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, modifier);
					jrYdMsg.setField("JMS_TC_CD"       , YmConstant.A7YML007);	//크레인작업지시요구
					jrYdMsg.setField("YD_EQP_ID"       , chgYdEqpId);	//야드설비ID
					jrYdMsg.setField("YD_WRK_PROG_STAT", "1"       );	//야드작업진행상태(권상작업지시)
					jrYdMsg.setField("YD_SCH_CD"       , ydSchCd   );	//야드스케쥴코드
					jrYdMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId);	//야드크레인스케쥴ID

					EJBConnector sndConn = new EJBConnector("default", "BCoilL2RcvSeEJB", this);
					JDTORecord jrSnd = (JDTORecord)sndConn.trx("rcvA7YML007", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });

					jrRtn = commUtils.addSndData(jrRtn, jrSnd);

					/**********************************************************
					* 3.3 이전 크레인의 설비 Table 상태정보를 Update
					**********************************************************/
					jrParam.setField("MODIFIER"		, modifier);
					jrParam.setField("EQUIP_STAT"	, "W"    ); //야드설비상태 : 권상작업지시
					jrParam.setField("EQUIP_GP"  	, ydEqpId);
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCrnYsEqp", logId, methodNm,  "TB_YM_EQUIP");				
					
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

					jrRtn = commUtils.addSndData(jrRtn, ymComm.getYMA7L005(resMsg));
					
				}
				
				jrParam.setField("YD_EQP_ID", ydEqpId);
				/*
				SELECT YD_WRK_PROG_STAT
				  FROM TB_YM_CRNSCH 
				 WHERE DEL_YN = 'N'
				   AND YD_CRN_SCH_ID = (
				                        SELECT YD_CRN_SCH_ID
				                          FROM TB_YM_CRNSCH 
				                         WHERE YD_EQP_ID = :V_YD_EQP_ID --'3DCRD1'
				                           AND DEL_YN    = 'N'
				                           AND YD_WRK_PROG_STAT NOT IN ('W')
				                        )
				 */
				JDTORecordSet rst = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnSchId");
				if (rst.size() > 0) {
					if (!"B".equals(sOLD_WPROG_STAT) && "1".equals(sOLD_WORK_MODE)) { // 고장이 아니고 on-line일때 명령선택 기동
						if ("W".equals(rst.getRecord(0).getFieldString("YD_WRK_PROG_STAT"))) {
							/*********************************************
							 * 이전 크레인의 다음 스케줄 명령 선택 기동 
							 ********************************************/
							JDTORecord jrA7YML007 = JDTORecordFactory.getInstance().create();
							jrA7YML007.setField("JMS_TC_CD", YmConstant.A7YML007);
							jrA7YML007.setField("YD_EQP_ID", ydEqpId);
							EJBConnector sndConn = new EJBConnector("default", "BCoilL2RcvSeEJB", this);
							JDTORecord jrSnd = (JDTORecord)sndConn.trx("rcvA7YML007", new Class[] { JDTORecord.class }, new Object[] { jrA7YML007 });
			
							jrRtn = commUtils.addSndData(jrRtn, jrSnd);
						}
					}
				}
				//변경된 크레인 상태 w이면 명령선택기동 EQP
				if ("W".equals(chgYdEqpStat)) {
					/*********************************************
					 * 변경 크레인의 다음 스케줄 명령 선택 기동 
					 ********************************************/
					JDTORecord jrA7YML007a = JDTORecordFactory.getInstance().create();
					jrA7YML007a.setField("JMS_TC_CD", YmConstant.A7YML007);
					jrA7YML007a.setField("YD_EQP_ID", chgYdEqpId);
					EJBConnector sndConn1 = new EJBConnector("default", "BCoilL2RcvSeEJB", this);
					JDTORecord jrSnd1 = (JDTORecord)sndConn1.trx("rcvA7YML007", new Class[] { JDTORecord.class }, new Object[] { jrA7YML007a });

					jrRtn = commUtils.addSndData(jrRtn, jrSnd1);
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
	 *      [A] 오퍼레이션명 : 크레인작업요구현황조회 순위변경
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updPriorChange(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업요구현황조회 순위변경[BCoilJspSeEJB.updPriorChange] < " + gdReq.getNavigateValue();
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
				
				//크레인스케줄 Table 크레인ID, 우선순위 Update
				/*
				--크레인작업관리 크레인변경 크레인스케줄 수정
				UPDATE TB_YM_CRNSCH
				   SET MODIFIER     = :V_MODIFIER
				      ,MOD_DDTT     = SYSDATE
				      ,YD_SCH_PRIOR = TO_NUMBER(:V_YD_SCH_PRIOR)
				      ,YD_EQP_ID    = NVL(:V_YD_EQP_ID,YD_EQP_ID)
				 WHERE YD_WBOOK_ID  = :V_YD_WBOOK_ID
				   AND YD_WRK_PROG_STAT IN ('1','W','S')
				   AND DEL_YN = 'N'
				 */
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCrnWrkMgt", logId, methodNm,  "크레인스케줄 Table 크레인ID, 우선순위 Update");				
				
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
	 *      [A] 오퍼레이션명 : 긴급작업
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updPriorWrkChange2(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인스케줄 긴급작업[BCoilJspSeEJB.updPriorWrkChange2] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			String ydWbookId  = ""; //야드작업예약ID
			//String ydSchPrior = ""; //야드스케쥴우선순위
			String ydEqpId = ""; 
			String ydCrnSchId = ""; 
			String ydCrnSchIdWrk = ""; 
			String ydSchCd = ""; 
			String sMODIFIER = "";
		    String sStockId  = "";
			
			ydEqpId  	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"        ));  //야드설비ID(크레인)
		    ydWbookId  	= commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"      ));  //야드작업예약ID
		    ydCrnSchId 	= commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"    ));  //야드크레인스케쥴ID
		    ydSchCd 	= commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"    	  ));  //야드스케쥴코드
		    sStockId	= commUtils.trim(rcvMsg.getFieldString("STOCK_ID"    	  ));  //코일번호
		    sMODIFIER   = commUtils.trim(rcvMsg.getFieldString("MODIFIER"         ));  //수정자
			
			if ("".equals(ydCrnSchId)) {
				throw new Exception("크레인스케쥴ID가 없습니다!");
			} else if ("".equals(ydWbookId)) {
				throw new Exception("야드작업예약ID가 없습니다!");
			} else if ("".equals(ydEqpId)) {
				throw new Exception("야드설비ID가 없습니다!");
			} else if ("".equals(ydSchCd)) {
				throw new Exception("야드스케쥴코드가 없습니다!");
			} 
		    
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			
		    boolean autoFlag  = false; //무인 여부
		    
			commUtils.printLog(logId, "긴급작업 [ " + ydEqpId + " >> " + ydWbookId + " >> " + ydCrnSchId +" >> " + ydSchCd + " ]", "SL");
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, sMODIFIER);
			
		    jrParam.setField("YD_EQP_ID"		, ydEqpId );      //신규
		    jrParam.setField("YD_CRN_SCH_ID" 	, ydCrnSchId );   //신규
			jrParam.setField("YD_WBOOK_ID" 		, ydWbookId );    //신규
			jrParam.setField("YD_SCH_PRIOR"		, "0");           //신규
			jrParam.setField("YD_SCH_CD"		, ydSchCd);		  //신규 2021.12.07
			
            // 기존작업지시
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnWrkMgtPriorWrk1   
			SELECT YD_CRN_SCH_ID
			  FROM (
			        SELECT YD_CRN_SCH_ID
			             , COUNT(*)  AS CRN_WRK_CNT
			          FROM TB_YM_CRNSCH
			         WHERE YD_EQP_ID = :V_YD_EQP_ID
			           AND YD_WRK_PROG_STAT IN ('1', 'S')
			           AND DEL_YN = 'N'   
			         GROUP BY YD_CRN_SCH_ID  
			        )
			 WHERE CRN_WRK_CNT = 1            
			 */
			JDTORecordSet jsCrn = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnWrkMgtPriorWrk1", logId, methodNm, "기존크레인 조회");

			
			if (jsCrn.size() == 0) {
				/*  
				UPDATE TB_YM_CRNSCH
				   SET MODIFIER     = :V_MODIFIER
				     , MOD_DDTT     = SYSDATE
				     , YD_SCH_PRIOR = TO_NUMBER(:V_YD_SCH_PRIOR)
				 WHERE YD_WBOOK_ID  = :V_YD_WBOOK_ID
				   AND DEL_YN = 'N'						   
				 */
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCrnWrkMgt0", logId, methodNm,  "긴급작업 우선순위 변경");
				
				
			    /*********************************
				 * 무인크레인 여부 체크 
				 ********************************/
		    	/*
				SELECT YD_EQP_WRK_MODE2
				  FROM TB_YM_EQUIP
				 WHERE DEL_YN = 'N'
				   AND EQUIP_GP = :V_YD_EQP_ID
		    	 */
		    	JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.ChkCrnMode2");
		    	if (rsResult.size() > 0) {
		    		
		    		String sYD_EQP_WRK_MODE2 = rsResult.getRecord(0).getFieldString("YD_EQP_WRK_MODE2");

		    		if ("A".equals(sYD_EQP_WRK_MODE2)) {// A:무인 R:리모컨
						autoFlag = true; 
					}

		    		if (autoFlag) { 

		    			/******************************************************
		    			 * 긴급작업시 Auto크레인 우선순위 변경후 아무것도 안함
		    			 ******************************************************/
						//신규 작업 우선순위 변경
						/*  
						UPDATE TB_YM_CRNSCH
						   SET MODIFIER     = :V_MODIFIER
						      ,MOD_DDTT     = SYSDATE
						      ,YD_SCH_PRIOR = TO_NUMBER(:V_YD_SCH_PRIOR)
						      ,YD_EQP_ID    = NVL(:V_YD_EQP_ID,YD_EQP_ID)
						 WHERE YD_WBOOK_ID  = :V_YD_WBOOK_ID
						   AND YD_WRK_PROG_STAT IN ('1','W','S')
						   AND DEL_YN = 'N'							   
						 */
						commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCrnWrkMgt", logId, methodNm,  "TB_YM_CRNSCH");
						
						/**********************************************************
						* 크레인작업지시요구 전문 조회 - 일시정지 긴급작업
						**********************************************************/
						String sAPP030 = ymComm.BCoilApplyYn("APP030","3","S1");
						
						if ("Y".equals(sAPP030)) {
							//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
							JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, sMODIFIER);
							
							if ("3EYD99MM".equals(ydSchCd) || "3AYD99MM".equals(ydSchCd) || "3BYD99MM".equals(ydSchCd)) { //분동코일

								jrParam.setField("REPR_CD_GP"	, "BDCOIL");
								jrParam.setField("CD_GP"		, "3");
								jrParam.setField("ITEM"			, sStockId);
								
								JDTORecordSet jsRuleInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmRule", logId, methodNm, "분동코일 기준 조회");
								
				               	jrYdMsg.setField("JMS_TC_CD"         , YmConstant.YMA7L004); //크레인작업지시요구
								jrYdMsg.setField("YD_CRN_SCH_ID"     , ydCrnSchId         ); //야드크레인스케쥴ID
								jrYdMsg.setField("MSG_GP"            , "I"   ); //전문구분
								jrYdMsg.setField("YD_CRN_SCH_RMD_CNT", "S1"  ); //S1 일시정지 후 긴급작업
	
								jrYdMsg.setField("YD_STL_WT"         , jsRuleInfo.getRecord(0).getFieldString("DTL_ITM1")  ); //중량
								jrYdMsg.setField("YD_STL_T"          , jsRuleInfo.getRecord(0).getFieldString("DTL_ITM2")   );
								jrYdMsg.setField("YD_STL_W"          , jsRuleInfo.getRecord(0).getFieldString("DTL_ITM3")   );
								jrYdMsg.setField("COIL_OUTDIA"       , jsRuleInfo.getRecord(0).getFieldString("DTL_ITM4")); //외경
								jrYdMsg.setField("COIL_INDIA"        , jsRuleInfo.getRecord(0).getFieldString("DTL_ITM5") );  //내경
								jrYdMsg.setField("STOCK_ID"          , sStockId    );
								
								jrRtn = commUtils.addSndData(commDao.getMsgL2("YMA7L004WC", jrYdMsg));
								
							} else {
			                
				               	jrYdMsg.setField("JMS_TC_CD"         , YmConstant.YMA7L004); //크레인작업지시요구
								jrYdMsg.setField("YD_CRN_SCH_ID"     , ydCrnSchId         ); //야드크레인스케쥴ID
								jrYdMsg.setField("MSG_GP"            , "I"   ); //전문구분
								jrYdMsg.setField("YD_CRN_SCH_RMD_CNT", "S1"  ); //S1 일시정지 후 긴급작업
	
								jrRtn = commUtils.addSndData(commDao.getMsgL2("YMA7L004", jrYdMsg));
							}
						}
						
		    			return jrRtn;
		    			
		    		} 
		    	}
				
		    } else {
		    	
				/**********************************************************
				* 3.1 기존 작업 정리 
				* 3.2 신규 작업 처리 함  
				**********************************************************/
		    	
		    	JDTORecord jrCrn = jsCrn.getRecord(0);
			    ydCrnSchIdWrk   = commUtils.trim(jrCrn.getFieldString("YD_CRN_SCH_ID"));    //기존
			    
			    
			    /*********************************
				 * 무인크레인 여부 체크 
				 ********************************/
		    	/*
				SELECT YD_EQP_WRK_MODE2
				  FROM TB_YM_EQUIP
				 WHERE DEL_YN = 'N'
				   AND EQUIP_GP = :V_YD_EQP_ID
		    	 */
		    	JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.ChkCrnMode2");
		    	if (rsResult.size() > 0) {
		    		
		    		String sYD_EQP_WRK_MODE2 = rsResult.getRecord(0).getFieldString("YD_EQP_WRK_MODE2");

		    		if ("A".equals(sYD_EQP_WRK_MODE2)) {// A:무인 R:리모컨
						autoFlag = true; 
					}

		    		if (autoFlag) { 

		    			/******************************************************
		    			 * 긴급작업시 Auto크레인 우선순위 변경후 아무것도 안함
		    			 ******************************************************/
						//신규 작업 우선순위 변경
						/*  
						UPDATE TB_YM_CRNSCH
						   SET MODIFIER     = :V_MODIFIER
						      ,MOD_DDTT     = SYSDATE
						      ,YD_SCH_PRIOR = TO_NUMBER(:V_YD_SCH_PRIOR)
						      ,YD_EQP_ID    = NVL(:V_YD_EQP_ID,YD_EQP_ID)
						 WHERE YD_WBOOK_ID  = :V_YD_WBOOK_ID
						   AND YD_WRK_PROG_STAT IN ('1','W','S')
						   AND DEL_YN = 'N'							   
						 */
						commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCrnWrkMgt", logId, methodNm,  "TB_YM_CRNSCH");
						
						/**********************************************************
						* 크레인작업지시요구 전문 조회 - 일시정지 긴급작업
						**********************************************************/
						String sAPP030 = ymComm.BCoilApplyYn("APP030","3","S1");
						
						if ("Y".equals(sAPP030)) {
							//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
							JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, sMODIFIER);
							
							if ("3EYD99MM".equals(ydSchCd) || "3AYD99MM".equals(ydSchCd) || "3BYD99MM".equals(ydSchCd)) { //분동코일

								jrParam.setField("REPR_CD_GP"	, "BDCOIL");
								jrParam.setField("CD_GP"		, "3");
								jrParam.setField("ITEM"			, sStockId);
								
								JDTORecordSet jsRuleInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmRule", logId, methodNm, "분동코일 기준 조회");
								
				               	jrYdMsg.setField("JMS_TC_CD"         , YmConstant.YMA7L004); //크레인작업지시요구
								jrYdMsg.setField("YD_CRN_SCH_ID"     , ydCrnSchId         ); //야드크레인스케쥴ID
								jrYdMsg.setField("MSG_GP"            , "I"   ); //전문구분
								jrYdMsg.setField("YD_CRN_SCH_RMD_CNT", "S1"  ); //S1 일시정지 후 긴급작업
	
								jrYdMsg.setField("YD_STL_WT"         , jsRuleInfo.getRecord(0).getFieldString("DTL_ITM1")  ); //중량
								jrYdMsg.setField("YD_STL_T"          , jsRuleInfo.getRecord(0).getFieldString("DTL_ITM2")   );
								jrYdMsg.setField("YD_STL_W"          , jsRuleInfo.getRecord(0).getFieldString("DTL_ITM3")   );
								jrYdMsg.setField("COIL_OUTDIA"       , jsRuleInfo.getRecord(0).getFieldString("DTL_ITM4")); //외경
								jrYdMsg.setField("COIL_INDIA"        , jsRuleInfo.getRecord(0).getFieldString("DTL_ITM5") );  //내경
								jrYdMsg.setField("STOCK_ID"          , sStockId    );
								
								jrRtn = commUtils.addSndData(commDao.getMsgL2("YMA7L004WC", jrYdMsg));
								
							} else {
			                
				               	jrYdMsg.setField("JMS_TC_CD"         , YmConstant.YMA7L004); //크레인작업지시요구
								jrYdMsg.setField("YD_CRN_SCH_ID"     , ydCrnSchId         ); //야드크레인스케쥴ID
								jrYdMsg.setField("MSG_GP"            , "I"   ); //전문구분
								jrYdMsg.setField("YD_CRN_SCH_RMD_CNT", "S1"  ); //S1 일시정지 후 긴급작업
	
								jrRtn = commUtils.addSndData(commDao.getMsgL2("YMA7L004", jrYdMsg));
							}
						}
						
		    			return jrRtn;
		    			
		    		} 

	    			/******************************************************
	    			 * 유인 긴급작업일 경우 명령선택 기동(기존작업)
	    			 ******************************************************/
				    jrParam.setField("YD_CRN_SCH_ID" 	, ydCrnSchIdWrk );
				    
				    
				    /**** 기존 작업 지시 정리 ***********/
					//크레인스케줄 Table 크레인ID, 우선순위 Update, 
				    /* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCrnWrkMgtPriorWrkNext1 
				    UPDATE TB_YM_CRNSCH A
				       SET MODIFIER         = :V_MODIFIER
				         , MOD_DDTT         = SYSDATE
				         , YD_WRK_PROG_STAT = 'W'
				    	 , YD_WORD_DT       = NULL 
				    	 , YD_SCH_PRIOR     = (SELECT YD_WRK_CRN_PRIOR FROM USRYMA.TB_YM_SCHEDULERULE B
					                            WHERE B.YD_SCH_CD=A.YD_SCH_CD)
				     WHERE YD_CRN_SCH_ID    = :V_YD_CRN_SCH_ID
				       AND DEL_YN = 'N'       
				     */   
				    jrParam.setField("YD_CRN_SCH_ID" , ydCrnSchIdWrk);
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCrnWrkMgtPriorWrkNext1", logId, methodNm,  "TB_YM_CRNSCH");						    
					    
		    	}
						
				//신규 작업 우선순위 변경
				/*  
				UPDATE TB_YM_CRNSCH
				   SET MODIFIER     = :V_MODIFIER
				      ,MOD_DDTT     = SYSDATE
				      ,YD_SCH_PRIOR = TO_NUMBER(:V_YD_SCH_PRIOR)
				      ,YD_EQP_ID    = NVL(:V_YD_EQP_ID,YD_EQP_ID)
				 WHERE YD_WBOOK_ID  = :V_YD_WBOOK_ID
				   AND YD_WRK_PROG_STAT IN ('1','W','S')
				   AND DEL_YN = 'N'							   
				 */
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCrnWrkMgt", logId, methodNm,  "TB_YM_CRNSCH");
				
				/*  
				UPDATE TB_YM_CRNSCH
				   SET MODIFIER        = :V_MODIFIER
				      ,MOD_DDTT        = SYSDATE
				      ,YD_SCH_PRIOR    = TO_NUMBER(:V_YD_SCH_PRIOR)
				      ,YD_EQP_ID       = NVL(:V_YD_EQP_ID,YD_EQP_ID)
				      ,YD_WRK_PROG_STAT= 'S' 
				 WHERE YD_CRN_SCH_ID  = (SELECT MIN(YD_CRN_SCH_ID)
				                           FROM TB_YM_CRNSCH
				                          WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
				                            AND DEL_YN = 'N'
				                        )
				   AND YD_WRK_PROG_STAT IN ('1','W','S')
				   AND DEL_YN = 'N'  
				 */
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCrnWrkMgtS", logId, methodNm,  "TB_YM_CRNSCH");
					
				/**********************************************************
				* 3.2 신  크레인작업지시 요구 처리
				**********************************************************/

				//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();

				jrYdMsg.setResultCode(logId);	//Log ID
				jrYdMsg.setResultMsg(methodNm);	//Log Method Name

				if ("3EYD99MM".equals(ydSchCd) || "3AYD99MM".equals(ydSchCd) || "3BYD99MM".equals(ydSchCd)) { //분동코일
					
					jrParam.setField("REPR_CD_GP"	, "BDCOIL");
					jrParam.setField("CD_GP"		, "3");
					jrParam.setField("ITEM"			, sStockId);
					
					JDTORecordSet jsRuleInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmRule", logId, methodNm, "분동코일 기준 조회");
					
	               	jrYdMsg.setField("JMS_TC_CD"         , YmConstant.YMA7L004); //크레인작업지시요구
					jrYdMsg.setField("YD_CRN_SCH_ID"     , ydCrnSchId         ); //야드크레인스케쥴ID
					jrYdMsg.setField("MSG_GP"            , "I"   ); //전문구분
					
					jrYdMsg.setField("YD_STL_WT"         , jsRuleInfo.getRecord(0).getFieldString("DTL_ITM1")  ); //중량
					jrYdMsg.setField("YD_STL_T"          , jsRuleInfo.getRecord(0).getFieldString("DTL_ITM2")   );
					jrYdMsg.setField("YD_STL_W"          , jsRuleInfo.getRecord(0).getFieldString("DTL_ITM3")   );
					jrYdMsg.setField("COIL_OUTDIA"       , jsRuleInfo.getRecord(0).getFieldString("DTL_ITM4")); //외경
					jrYdMsg.setField("COIL_INDIA"        , jsRuleInfo.getRecord(0).getFieldString("DTL_ITM5") );  //내경
					jrYdMsg.setField("STOCK_ID"          , sStockId    );
					
					jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA7L004WC", jrYdMsg));
					
				} else {
					
					jrYdMsg.setField("JMS_TC_CD"       , YmConstant.YMA7L004);	//크레인작업지시요구
					jrYdMsg.setField("MSG_GP"          , "I");	//야드설비ID
					jrYdMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId);	//야드크레인스케쥴ID
					
					jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA7L004", jrYdMsg));
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
	 *      [A] 오퍼레이션명 : 크레인작업요구현황조회 긴급작업
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updPriorWrkChange(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리 긴급작업[BCoilJspSeEJB.updPriorWrkChange] < " + gdReq.getNavigateValue();
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
			    ydCrnSchId 	= commUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID").getValue(ii));  // 신규작업
			    ydSchCd 	= commUtils.trim(gdReq.getHeader("YD_SCH_CD").getValue(ii));
			    
				commUtils.printLog(logId, "긴급작업 [ " + ydEqpId + " >> " + ydWbookId + " >> " + ydSchPrior + " >> " + ydCrnSchId +" >> " + ydSchCd + " ]", "SL");
 
			    //작업할  야드작업예약ID가 작업 완료한 야드작업예약ID에 있으면 이전에 모두 처리되었으므로 Skip
				if (commUtils.chkExist(arrYdWbookId, ydWbookId)) { continue; }
				arrYdWbookId[ii] = ydWbookId;

			    jrParam.setField("YD_EQP_ID"		, ydEqpId );      //신규
			    jrParam.setField("YD_CRN_SCH_ID" 	, ydCrnSchId );   //신규
				jrParam.setField("YD_WBOOK_ID" 		, ydWbookId );    //신규
				jrParam.setField("YD_SCH_CD"		, ydSchCd);		  //신규 2021.12.07

				jrRtn = commUtils.addSndData(jrRtn, this.updPriorWrkChange2(jrParam));
				
			} //end for

			commUtils.printLog(logId, methodNm, "S-");
			return jrRtn;

		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}	

	/**
	 *      [A] 오퍼레이션명 : 크레인작업요구현황조회 긴급작업
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updPriorWrkChange_OLD(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리 긴급작업[BCoilJspSeEJB.updPriorWrkChange] < " + gdReq.getNavigateValue();
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
			    ydCrnSchId 	= commUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID").getValue(ii));  // 신규작업
			    ydSchCd 	= commUtils.trim(gdReq.getHeader("YD_SCH_CD").getValue(ii));
			    boolean autoFlag  = false; //무인 여부
			    
				commUtils.printLog(logId, "긴급작업 [ " + ydEqpId + " >> " + ydWbookId + " >> " + ydSchPrior + " >> " + ydCrnSchId +" >> " + ydSchCd + " ]", "SL");
 
			    //작업할  야드작업예약ID가 작업 완료한 야드작업예약ID에 있으면 이전에 모두 처리되었으므로 Skip
				if (commUtils.chkExist(arrYdWbookId, ydWbookId)) { continue; }
				arrYdWbookId[ii] = ydWbookId;

			    jrParam.setField("YD_EQP_ID"		, ydEqpId );      //신규
			    jrParam.setField("YD_CRN_SCH_ID" 	, ydCrnSchId );   //신규
				jrParam.setField("YD_WBOOK_ID" 		, ydWbookId );    //신규
				jrParam.setField("YD_SCH_PRIOR"		, "0");           //신규
				jrParam.setField("YD_SCH_CD"		, ydSchCd);		  //신규 2021.12.07
				
                // 기존작업지시
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnWrkMgtPriorWrk1   
				SELECT YD_CRN_SCH_ID
				  FROM (
				        SELECT YD_CRN_SCH_ID
				             , COUNT(*)  AS CRN_WRK_CNT
				          FROM TB_YM_CRNSCH
				         WHERE YD_EQP_ID = :V_YD_EQP_ID
				           AND YD_WRK_PROG_STAT IN ('1', 'S')
				           AND DEL_YN = 'N'   
				         GROUP BY YD_CRN_SCH_ID  
				        )
				 WHERE CRN_WRK_CNT = 1            
				 */
				JDTORecordSet jsCrn = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnWrkMgtPriorWrk1", logId, methodNm, "기존크레인 조회");

				
				if (jsCrn.size() == 0) {
					/*  
					UPDATE TB_YM_CRNSCH
					   SET MODIFIER     = :V_MODIFIER
					     , MOD_DDTT     = SYSDATE
					     , YD_SCH_PRIOR = TO_NUMBER(:V_YD_SCH_PRIOR)
					 WHERE YD_WBOOK_ID  = :V_YD_WBOOK_ID
					   AND DEL_YN = 'N'						   
					 */
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCrnWrkMgt0", logId, methodNm,  "긴급작업 우선순위 변경");
					
			    } else {
			    	
					/**********************************************************
					* 3.1 기존 작업 정리 
					* 3.2 신규 작업 처리 함  
					**********************************************************/
			    	
			    	JDTORecord jrCrn = jsCrn.getRecord(0);
				    ydCrnSchIdWrk   = commUtils.trim(jrCrn.getFieldString("YD_CRN_SCH_ID"));    //기존
				    
				    
				    /*********************************
					 * 무인크레인 여부 체크 
					 ********************************/
			    	/*
					SELECT YD_EQP_WRK_MODE2
					  FROM TB_YM_EQUIP
					 WHERE DEL_YN = 'N'
					   AND EQUIP_GP = :V_YD_EQP_ID
			    	 */
			    	JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.ChkCrnMode2");
			    	if (rsResult.size() > 0) {
			    		
			    		String sYD_EQP_WRK_MODE2 = rsResult.getRecord(0).getFieldString("YD_EQP_WRK_MODE2");

			    		if ("A".equals(sYD_EQP_WRK_MODE2)) {// A:무인 R:리모컨
							autoFlag = true; 
						}

			    		if (autoFlag) { 

			    			/******************************************************
			    			 * 긴급작업시 Auto크레인 우선순위 변경후 아무것도 안함
			    			 ******************************************************/
							//신규 작업 우선순위 변경
							/*  
							UPDATE TB_YM_CRNSCH
							   SET MODIFIER     = :V_MODIFIER
							      ,MOD_DDTT     = SYSDATE
							      ,YD_SCH_PRIOR = TO_NUMBER(:V_YD_SCH_PRIOR)
							      ,YD_EQP_ID    = NVL(:V_YD_EQP_ID,YD_EQP_ID)
							 WHERE YD_WBOOK_ID  = :V_YD_WBOOK_ID
							   AND YD_WRK_PROG_STAT IN ('1','W','S')
							   AND DEL_YN = 'N'							   
							 */
							commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCrnWrkMgt", logId, methodNm,  "TB_YM_CRNSCH");
							
							/**********************************************************
							* 크레인작업지시요구 전문 조회 - 일시정지 긴급작업
							**********************************************************/
							String sAPP030 = ymComm.BCoilApplyYn("APP030","3","S1");
							
							if ("Y".equals(sAPP030)) {
								//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
								JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
				                
				               	jrYdMsg.setField("JMS_TC_CD"         , YmConstant.YMA7L004); //크레인작업지시요구
								jrYdMsg.setField("YD_CRN_SCH_ID"     , ydCrnSchId         ); //야드크레인스케쥴ID
								jrYdMsg.setField("MSG_GP"            , "I"   ); //전문구분
								jrYdMsg.setField("YD_CRN_SCH_RMD_CNT", "S1"  ); //S1 일시정지 후 긴급작업

								jrRtn = commUtils.addSndData(commDao.getMsgL2("YMA7L004", jrYdMsg));
							}
							
			    			return jrRtn;
			    			
			    		} 

		    			/******************************************************
		    			 * 유인 긴급작업일 경우 명령선택 기동(기존작업)
		    			 ******************************************************/
					    jrParam.setField("YD_CRN_SCH_ID" 	, ydCrnSchIdWrk );
					    
					    
					    /**** 기존 작업 지시 정리 ***********/
						//크레인스케줄 Table 크레인ID, 우선순위 Update, 
					    /* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCrnWrkMgtPriorWrkNext1 
					    UPDATE TB_YM_CRNSCH A
					       SET MODIFIER         = :V_MODIFIER
					         , MOD_DDTT         = SYSDATE
					         , YD_WRK_PROG_STAT = 'W'
					    	 , YD_WORD_DT       = NULL 
					    	 , YD_SCH_PRIOR     = (SELECT YD_WRK_CRN_PRIOR FROM USRYMA.TB_YM_SCHEDULERULE B
						                            WHERE B.YD_SCH_CD=A.YD_SCH_CD)
					     WHERE YD_CRN_SCH_ID    = :V_YD_CRN_SCH_ID
					       AND DEL_YN = 'N'       
					     */   
					    jrParam.setField("YD_CRN_SCH_ID" , ydCrnSchIdWrk);
						commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCrnWrkMgtPriorWrkNext1", logId, methodNm,  "TB_YM_CRNSCH");						    
						    
			    	}
							
					//신규 작업 우선순위 변경
					/*  
					UPDATE TB_YM_CRNSCH
					   SET MODIFIER     = :V_MODIFIER
					      ,MOD_DDTT     = SYSDATE
					      ,YD_SCH_PRIOR = TO_NUMBER(:V_YD_SCH_PRIOR)
					      ,YD_EQP_ID    = NVL(:V_YD_EQP_ID,YD_EQP_ID)
					 WHERE YD_WBOOK_ID  = :V_YD_WBOOK_ID
					   AND YD_WRK_PROG_STAT IN ('1','W','S')
					   AND DEL_YN = 'N'							   
					 */
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCrnWrkMgt", logId, methodNm,  "TB_YM_CRNSCH");
					
					/*  
					UPDATE TB_YM_CRNSCH
					   SET MODIFIER        = :V_MODIFIER
					      ,MOD_DDTT        = SYSDATE
					      ,YD_SCH_PRIOR    = TO_NUMBER(:V_YD_SCH_PRIOR)
					      ,YD_EQP_ID       = NVL(:V_YD_EQP_ID,YD_EQP_ID)
					      ,YD_WRK_PROG_STAT= 'S' 
					 WHERE YD_CRN_SCH_ID  = (SELECT MIN(YD_CRN_SCH_ID)
					                           FROM TB_YM_CRNSCH
					                          WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
					                            AND DEL_YN = 'N'
					                        )
					   AND YD_WRK_PROG_STAT IN ('1','W','S')
					   AND DEL_YN = 'N'  
					 */
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCrnWrkMgtS", logId, methodNm,  "TB_YM_CRNSCH");
						
					/**********************************************************
					* 3.2 신  크레인작업지시 요구 처리
					**********************************************************/

					//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
					JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();

					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name
					jrYdMsg.setField("JMS_TC_CD"       , YmConstant.YMA7L004);	//크레인작업지시요구
					jrYdMsg.setField("MSG_GP"          , "I");	//야드설비ID
					jrYdMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId);	//야드크레인스케쥴ID

					jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA7L004", jrYdMsg));	

			    }		
				
			} //end for

			commUtils.printLog(logId, methodNm, "S-");
			return jrRtn;

		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업요구현황조회 권하위치변경
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updDownLocChange(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리 권하위치변경[BCoilJspSeEJB.updDownLocChange] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//권하위치변경 대상 스케줄
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {
				//EJB Call을 위한 Message 생성 - Log ID, Method, 수정자 Set
				JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
				
				jrYdMsg.setField("STOCK_ID"        , commUtils.trim(gdReq.getHeader("STOCK_ID"        ).getValue(ii))); //저장품
				jrYdMsg.setField("YD_EQP_ID"       , commUtils.trim(gdReq.getHeader("YD_EQP_ID"       ).getValue(ii))); //야드설비ID(크레인)
				jrYdMsg.setField("YD_SCH_CD"       , commUtils.trim(gdReq.getHeader("YD_SCH_CD"       ).getValue(ii))); //야드스케쥴코드
				jrYdMsg.setField("YD_CRN_SCH_ID"   , commUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID"   ).getValue(ii))); //야드크레인스케쥴ID
				jrYdMsg.setField("YD_WBOOK_ID"     , commUtils.trim(gdReq.getHeader("YD_WBOOK_ID"     ).getValue(ii))); //야드작업예약ID
				jrYdMsg.setField("YD_DN_WO_LOC"    , commUtils.trim(gdReq.getHeader("YD_DN_WO_LOC"    ).getValue(ii))); //야드권하지시위치(신규)
				jrYdMsg.setField("YD_WRK_PROG_STAT", commUtils.trim(gdReq.getHeader("YD_WRK_PROG_STAT").getValue(ii))); //야드작업진행상태
				jrYdMsg.setField("YD_DN_WO_LOC_ORG", commUtils.trim(gdReq.getHeader("YD_DN_WO_LOC_ORG").getValue(ii))); //야드권하지시위치(기존)
				
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
	 *      [A] 오퍼레이션명 : 크레인작업요구현황조회 작업취소
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updCraneWrkCancel(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업요구현황조회 작업취소[BCoilJspSeEJB.updCraneWrkCancel] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			String ydCrnSchId = ""; //야드크레인스케쥴ID
			String ydWbookId  = ""; //야드작업예약ID
		    String ydEqpId    = ""; //야드설비ID
		    String ydSchCd    = ""; //야드스케쥴코드
			String sWORK_MODE = "";
			
		    boolean autoFlag = false;
		    String sWPROG_STAT           = "";
		    String sYD_EQP_AUTO_CRN_MODE = "";
		    String sYD_EQP_WRK_MODE2     = "";
		    String sYD_WRK_PROG_STAT     = "";
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			//처리완료한 야드작업예약ID
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String[] arrYdWbookId = new String[rowCnt];
			
			for (int ii = 0; ii < rowCnt; ii++) {
				ydCrnSchId        = commUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID").getValue(ii));
				ydWbookId         = commUtils.trim(gdReq.getHeader("YD_WBOOK_ID").getValue(ii));
			    ydEqpId           = commUtils.trim(gdReq.getHeader("YD_EQP_ID"  ).getValue(ii));
			    ydSchCd           = commUtils.trim(gdReq.getHeader("YD_SCH_CD"  ).getValue(ii));
			    sYD_WRK_PROG_STAT = commUtils.trim(gdReq.getHeader("YD_WRK_PROG_STAT").getValue(ii));
			    
			    /*****************************************
			     * 무인크레인일 때는 작업취소가 되면 안됨
			     *****************************************/
			    /*
				SELECT *
				  FROM TB_YM_EQUIP    
				 WHERE EQUIP_GP = :V_EQUIP_GP
				   AND DEL_YN   = 'N'
				 */
				jrParam.setField("EQUIP_GP" , ydEqpId);
				JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYmEqp");
				JDTORecord jrEqpInfo = null;
				
				if (rsResult.size() > 0) {
					rsResult.first();
					jrEqpInfo = rsResult.getRecord();
					
					sWPROG_STAT           = jrEqpInfo.getFieldString("WPROG_STAT");          // 설비 상태
					sYD_EQP_AUTO_CRN_MODE = jrEqpInfo.getFieldString("YD_EQP_AUTO_CRN_MODE");// AutoCrn 상태
					sYD_EQP_WRK_MODE2     = jrEqpInfo.getFieldString("YD_EQP_WRK_MODE2"); 	// AutoCrn 여부
					sWORK_MODE            = jrEqpInfo.getFieldString("sWORK_MODE");
					
					if ("A".equals(sYD_EQP_WRK_MODE2)) {// A:무인 R:리모컨
						autoFlag = true; 
					}
				}
				
				//W:명령선택대기 S:스케줄작성중
				if ("W".equals(sYD_WRK_PROG_STAT)) {
					autoFlag = false;
				}
				
				if (autoFlag){ 
					
					//설비상태 체크하여 Auto일경우 일시정지 상태에서만 가능하게 수정
					if (!"4".equals(sYD_EQP_AUTO_CRN_MODE) && !"B".equals(sWPROG_STAT)) { //4: 일시정지 B:고장
						//m_ctx.setRollbackOnly();
						throw new Exception("무인크레인 [" + ydEqpId + "]이 일시정지이거나 고장상태가 아니면 취소할 수 없습니다.");
						
					}
				}

				/**************************************
				 * 무인크레인
				 **************************************/
				if (autoFlag) {
					
					// 작업대기상태 update
					/*
					UPDATE TB_YM_CRNSCH  
					   SET YD_WRK_PROG_STAT   = :V_YD_WRK_PROG_STAT
					     , YD_L2_REQUEST_STAT = :V_YD_L2_REQUEST_STAT
					     , MODIFIER           = :V_MODIFIER
					     , MOD_DDTT           = SYSDATE
					 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
					 */
					jrParam.setField("YD_WRK_PROG_STAT"  , "S");
					jrParam.setField("YD_L2_REQUEST_STAT", YmConstant.YD_L2_REQUEST_STAT_X);
					jrParam.setField("YD_CRN_SCH_ID"     , ydCrnSchId);
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdCrnSchProgStat");
					
					jrParam.setField("MSG_GP"       , "D"       ); //전문구분(취소)
					
					if ("3EYD99MM".equals(ydSchCd) || "3AYD99MM".equals(ydSchCd) || "3BYD99MM".equals(ydSchCd)) { //분동코일
						/*
						SELECT CM.STOCK_ID
						  FROM TB_YM_CRNSCH    CS
						     , TB_YM_CRNWRKMTL CM
						 WHERE CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
						   AND CS.DEL_YN = 'N'
						   AND CM.DEL_YN = 'N'
						   AND CS.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID 
						 */
						JDTORecordSet jsInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getStockIdByCrnSchId", logId, methodNm, "분동코일ID 조회");
						if (jsInfo.size() > 0) {
							jrParam.setField("STOCK_ID", jsInfo.getRecord(0).getFieldString("STOCK_ID"));
							jrRtn = commUtils.addSndData(commDao.getMsgL2("YMA7L004WC", jrParam));
						}
					} else {
						jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA7L004", jrParam));
					}
					
					return jrRtn;
				}
				
			    
			    //작업할  야드작업예약ID가 작업 완료한 야드작업예약ID에 있으면 이전에 모두 처리되었으므로 Skip
				if (commUtils.chkExist(arrYdWbookId, ydWbookId)) { continue; }
				arrYdWbookId[ii] = ydWbookId;

				//기본정보조회
				jrParam.setField("YD_WBOOK_ID", ydWbookId);

				JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCrnsch", logId, methodNm, "크레인작업지시read");
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

			// 작업취소시 설비가 offline, 고장이 아니고 대기 일때  명령 선택
			if (!"B".equals(sWPROG_STAT) && !"2".equals(sWORK_MODE) && "W".equals(sWPROG_STAT)) { 
	
				/**********************************************************
				* 5. 크레인작업지시요구 전문 조회
				**********************************************************/
				//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
				JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, "A7YML007");
	
				jrYdMsg.setField("JMS_TC_CD"       , YmConstant.A7YML007);	//크레인작업지시요구
				jrYdMsg.setField("YD_EQP_ID"       , ydEqpId   );	//야드설비ID
				jrYdMsg.setField("YD_WRK_PROG_STAT", "4"       );	//야드작업진행상태(권하완료)
				jrYdMsg.setField("YD_SCH_CD"       , ydSchCd   );	//야드스케쥴코드
				jrYdMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId);	//야드크레인스케쥴ID
	
				EJBConnector sndConn = new EJBConnector("default", "BCoilL2RcvSeEJB", this);
				JDTORecord jrRtn1 = (JDTORecord)sndConn.trx("rcvA7YML007", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
				
				
				jrRtn = commUtils.addSndData(jrRtn,jrRtn1);
			}
		
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
	 *      [A] 오퍼레이션명 : 크레인 스케줄취소처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtCrnSchCncl(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인 스케줄취소[BCoilJspSeEJB.trtCrnSchCncl] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try { 
			commUtils.printLog(logId, methodNm, "S+");

			String sYD_CRN_SCH_ID     = StringHelper.evl(commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID")),""); //야드크레인스케쥴ID
			String sYD_WBOOK_ID       = StringHelper.evl(commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"  )),""); //야드작업예약ID
			String sYD_L2_RETURN_FLAG = StringHelper.evl(commUtils.trim(rcvMsg.getFieldString("YD_L2_RETURN_FLAG")),""); //
			String sIS_SCH_MTL        = StringHelper.evl(commUtils.trim(rcvMsg.getFieldString("IS_SCH_MTL"   )),""); 
			String sWRK_CNCL_YN       = StringHelper.evl(commUtils.trim(rcvMsg.getFieldString("WRK_CNCL_YN"  )),"N"); //작업취소여부

			if ("".equals(sYD_CRN_SCH_ID)) {
				throw new Exception("크레인스케쥴ID가 없습니다.");
			} else if ("".equals(sYD_WBOOK_ID)) {
				throw new Exception("작업예약ID가 없습니다.");
			}
 
			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(rcvMsg.getFieldString("MODIFIER")));
			jrParam.setField("YD_CRN_SCH_ID", sYD_CRN_SCH_ID);
			jrParam.setField("YD_WBOOK_ID"  , sYD_WBOOK_ID );
			
			/**********************************************************
			* 1. 크레인스케쥴 정보 Check
			**********************************************************/
			/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCrnWrkMgtSCSch
			--크레인작업관리 크레인스케줄취소 조회 
			SELECT CS.YD_WRK_PROG_STAT
			      ,CS.YD_EQP_ID
			      ,CS.YD_EQP_STAT
			      ,EQ.WPROG_STAT
			      ,CASE WHEN EQ.WPROG_STAT IN ('B',CS.YD_EQP_STAT) OR EQ.WORK_MODE  != '1'
			            THEN 'N' ELSE 'Y' END AS EQP_UPD_YN --설비상태수정여부
			      ,(SELECT YD_DN_WO_LOC||YD_DN_WO_LAYER 
			          FROM TB_YM_CRNSCH
			         WHERE YD_CRN_SCH_ID = CS.YD_CRN_SCH_ID 
			       ) AS TO_LOC  
			      ,CS.YD_SCH_CD
			  FROM TB_YM_EQUIP EQ
			      ,(SELECT MIN(DECODE(YD_CRN_SCH_ID,SC_YD_CRN_SCH_ID,SC_YD_CRN_SCH_ID)) AS YD_CRN_SCH_ID
			              ,MIN(DECODE(YD_CRN_SCH_ID,SC_YD_CRN_SCH_ID,YD_WRK_PROG_STAT)) AS YD_WRK_PROG_STAT
			              ,MIN(DECODE(RN,1,DECODE(YD_CRN_SCH_ID,SC_YD_CRN_SCH_ID,'W',YD_WRK_PROG_STAT))) AS YD_EQP_STAT
			              ,MIN(DECODE(RN,1,YD_EQP_ID)) AS YD_EQP_ID
			              ,MIN(DECODE(RN,1,YD_SCH_CD)) AS YD_SCH_CD
			          FROM (SELECT YD_CRN_SCH_ID
			                      ,YD_WRK_PROG_STAT
			                      ,YD_EQP_ID
			                      ,:V_YD_CRN_SCH_ID AS SC_YD_CRN_SCH_ID --취소 크레인스케줄ID
			                      ,ROW_NUMBER() OVER (ORDER BY YD_CRN_SCH_ID) AS RN
			                      ,YD_SCH_CD
			                  FROM TB_YM_CRNSCH
			                 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
			                   AND DEL_YN      = 'N')) CS
			 WHERE CS.YD_EQP_ID = EQ.EQUIP_GP(+)
			*/ 
			JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCrnWrkMgtSCSch", logId, methodNm, "크레인작업지시read");
			if (jsCrnSch == null || jsCrnSch.size() <= 0) {
				throw new Exception("크레인스케쥴ID[" + sYD_CRN_SCH_ID + "]의 크레인스케줄 정보가 존재하지 않습니다.");
		    }
			
			JDTORecord jrCrnSch = jsCrnSch.getRecord(0);
			
		    String ydWrkProgStat = commUtils.trim(jrCrnSch.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
		    String eqpUpdYn      = commUtils.trim(jrCrnSch.getFieldString("EQP_UPD_YN"      )); //설비상태수정여부
		    String ydEqpId       = commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"       )); //야드설비ID
		    String ydEqpStat     = commUtils.trim(jrCrnSch.getFieldString("YD_EQP_STAT"     )); //야드설비상태
		    String ydSchCd		 = commUtils.trim(jrCrnSch.getFieldString("YD_SCH_CD"       )); //야드스케줄코드
		    String sSTOCK_ID	 = "";
		    
		    commUtils.printLog(logId, "삭제대상크레인스케줄 YD_CRN_SCH_ID ["+sYD_CRN_SCH_ID+"]", "[INFO]");
		    commUtils.printLog(logId, "야드작업진행상태 YD_WRK_PROG_STAT ["+ydWrkProgStat+"]", "[INFO]");
		    
			if ("2".equals(ydWrkProgStat)) {
				throw new Exception("크레인스케줄 [" + sYD_CRN_SCH_ID + "]의 작업진행상태가 [2:권상완료]이므로 취소하실 수 없습니다.");
			} else if ("3".equals(ydWrkProgStat)) {
				throw new Exception("크레인스케줄 [" + sYD_CRN_SCH_ID + "]의 작업진행상태가 [3:권하지시]이므로 취소하실 수 없습니다.");
			} else if ("4".equals(ydWrkProgStat)) {
				throw new Exception("크레인스케줄 [" + sYD_CRN_SCH_ID + "]의 작업진행상태가 [4:권하완료]이므로 취소하실 수 없습니다.");
			}
			
			/**********************************************************
			* 2. 작업진행상태가 [1:권상지시] 이면 작업지시취소 전문 전송
			**********************************************************/
			if ("1".equals(ydWrkProgStat) && !"Y".equals(sYD_L2_RETURN_FLAG)) {
				if ("Y".equals(sWRK_CNCL_YN)) {
					// 작업대기상태 update : 작업취소이므로 X
					/*
					UPDATE TB_YM_CRNSCH  
					   SET YD_L2_REQUEST_STAT = :V_YD_L2_REQUEST_STAT
					     , MODIFIER           = :V_MODIFIER
					     , MOD_DDTT           = SYSDATE
					 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
					 */
					jrParam.setField("YD_L2_REQUEST_STAT", YmConstant.YD_L2_REQUEST_STAT_X);
					jrParam.setField("YD_CRN_SCH_ID"     , sYD_CRN_SCH_ID);
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdCrnSchProgStat", logId, methodNm, "작업대기상태 스케줄 취소(X) UPDATE");
				}
				
				jrParam.setField("YD_CRN_SCH_ID", sYD_CRN_SCH_ID); //야드크레인스케쥴ID
				jrParam.setField("MSG_GP"       , "D"       ); //전문구분(취소)

				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA7L004", jrParam));
			}
 
			/**********************************************************
			* 3. 권상, 권하위치 원복 - 적치단, 적치Bed
			**********************************************************/
			//적치단 수정 - 권상위치(U -> C), 권하위치(D -> E)
			/*
			WITH CSM AS ( 
			SELECT CM.STOCK_ID
			     , SUBSTR(CS.YD_UP_WO_LOC,1,6) AS STACK_COL_GP_UP
			     , SUBSTR(CS.YD_UP_WO_LOC,7,2) AS STACK_BED_GP_UP
			     , SUBSTR(CS.YD_DN_WO_LOC,1,6) AS STACK_COL_GP_DN
			     , SUBSTR(CS.YD_DN_WO_LOC,7,2) AS STACK_BED_GP_DN
			     , CS.YD_UP_WO_LAYER AS STACK_LAYER_GP
			     , CS.YD_CRN_SCH_ID
			     , CS.YD_TO_LOC_DCSN_MTD
			  FROM TB_YM_CRNSCH CS
			     ,(
			        SELECT CS.YD_CRN_SCH_ID AS YD_CRN_SCH_ID
			             , CM.STOCK_ID
			          FROM TB_YM_CRNSCH    CS
			              ,TB_YM_CRNWRKMTL CM
			         WHERE CM.YD_CRN_SCH_ID  = CS.YD_CRN_SCH_ID
			           AND CS.YD_WBOOK_ID    = :V_YD_WBOOK_ID
			           AND CM.YD_CRN_SCH_ID >= :V_YD_CRN_SCH_ID
			           AND CS.DEL_YN         = 'N'
			           AND CM.DEL_YN         = 'N'
			     ) CM
			 WHERE CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
			   AND CS.DEL_YN = 'N'
			)
			SELECT STACK_COL_GP
			     , STACK_BED_GP
			     , STACK_LAYER_GP
			     , MAX(STOCK_ID        ) AS STOCK_ID
			     , MIN(STACK_LAYER_STAT) AS STACK_LAYER_STAT
			  FROM (
			        --원래위치
			        SELECT SL.STACK_COL_GP
			             , SL.STACK_BED_GP
			             , SL.STACK_LAYER_GP
			             , SL.STOCK_ID
			             , 'C' AS STACK_LAYER_STAT --적치중
			          FROM TB_YM_STACKLAYER SL
			             , CSM
			         WHERE SL.STOCK_ID     = CSM.STOCK_ID
			           AND SL.STACK_COL_GP = CSM.STACK_COL_GP_UP
			           AND SL.STACK_BED_GP LIKE DECODE(SUBSTR(CSM.STACK_COL_GP_UP,3,2),'TY','%',CSM.STACK_BED_GP_UP)
			           AND SL.STACK_LAYER_STAT = 'U' --권상대기
			         UNION ALL 
			        --권하지시위치
			        SELECT SL.STACK_COL_GP
			             , SL.STACK_BED_GP
			             , SL.STACK_LAYER_GP
			             , NULL AS STOCK_ID
			             , 'E'  AS STACK_LAYER_STAT --적치가능
			          FROM TB_YM_STACKLAYER SL
			             , CSM
			         WHERE SL.STACK_LAYER_STAT = 'D' --권하대기
			           AND SL.STOCK_ID     = CSM.STOCK_ID 
			           AND SL.STACK_COL_GP = CSM.STACK_COL_GP_DN
			           AND SL.STACK_BED_GP = CSM.STACK_BED_GP_DN
			       )
			 GROUP BY STACK_COL_GP, STACK_BED_GP, STACK_LAYER_GP
			 */
			String sQueryId = "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCrnWrkMgtSCStkLyr";
			if ("Y".equals(sIS_SCH_MTL)) {
				sQueryId = "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCrnWrkMgtSCStkLyrMtl";
			}
			JDTORecordSet rsResult = commDao.select(jrParam, sQueryId, logId, methodNm, "TB_YM_STACKLAYER");
			
			for (int i = 0; i < rsResult.size(); ++i) {
				
				sSTOCK_ID = rsResult.getRecord(i).getFieldString("STOCK_ID");
				jrParam.setField("STOCK_ID"        , rsResult.getRecord(i).getFieldString("STOCK_ID"));
				jrParam.setField("STACK_LAYER_STAT", rsResult.getRecord(i).getFieldString("STACK_LAYER_STAT"));
				jrParam.setField("STACK_COL_GP"    , rsResult.getRecord(i).getFieldString("STACK_COL_GP"));
				jrParam.setField("STACK_BED_GP"    , rsResult.getRecord(i).getFieldString("STACK_BED_GP"));
				jrParam.setField("STACK_LAYER_GP"  , rsResult.getRecord(i).getFieldString("STACK_LAYER_GP"));
				/*
				UPDATE TB_YM_STACKLAYER            
				   SET MOD_DDTT     = SYSDATE             
				     , MODIFIER     = :V_MODIFIER             
				     , STACK_LAYER_ACTIVE_STAT = NVL(:V_STACK_LAYER_ACTIVE_STAT, STACK_LAYER_ACTIVE_STAT)
				     , STOCK_ID                = :V_STOCK_ID
				     , STACK_LAYER_STAT        = NVL(:V_STACK_LAYER_STAT       , STACK_LAYER_STAT)
				 WHERE STACK_COL_GP   = :V_STACK_COL_GP
				   AND STACK_BED_GP   = :V_STACK_BED_GP
				   AND STACK_LAYER_GP = :V_STACK_LAYER_GP 
				 */
				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStkLyrYdStkColBedGp", logId, methodNm, "적치단 UPDATE");
			}
			
			/**********************************************************
			* 4. 크레인스케줄 삭제
			**********************************************************/
			/*크레인작업재료 삭제
			UPDATE TB_YM_CRNWRKMTL
			   SET MODIFIER = :V_MODIFIER
			      ,MOD_DDTT = SYSDATE
			      ,DEL_YN   = 'Y'
			 WHERE DEL_YN   = 'N'
			   AND YD_CRN_SCH_ID IN (SELECT YD_CRN_SCH_ID
								       FROM TB_YM_CRNSCH
								      WHERE YD_WBOOK_ID    = :V_YD_WBOOK_ID
								        AND DEL_YN         = 'N')
			 */
			sQueryId = "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updCrnWrkMgtSCCrnMtl";
			if ("Y".equals(sIS_SCH_MTL)) {
				/*
				UPDATE TB_YM_CRNWRKMTL
				   SET MODIFIER = :V_MODIFIER
				     , MOD_DDTT = SYSDATE
				     , DEL_YN   = 'Y'
				 WHERE DEL_YN   = 'N'
				   AND YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				 */
				sQueryId = "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updCrnWrkMgtSCCrnMtlUnitMtl";
			}
			commDao.update(jrParam, sQueryId, logId, methodNm, "TB_YM_CRNWRKMTL");				
			
			/*크레인스케줄 삭제
			UPDATE TB_YM_CRNSCH
			   SET MODIFIER = :V_MODIFIER
			      ,MOD_DDTT = SYSDATE
			      ,DEL_YN   = 'Y'
			 WHERE YD_CRN_SCH_ID IN (SELECT YD_CRN_SCH_ID
								       FROM TB_YM_CRNSCH
								      WHERE YD_WBOOK_ID    = :V_YD_WBOOK_ID
								        AND DEL_YN         = 'N')
			*/
			sQueryId = "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updCrnWrkMgtSCCrnSch";
			if ("Y".equals(sIS_SCH_MTL)) {
				/*
				UPDATE TB_YM_CRNSCH
				   SET MODIFIER = :V_MODIFIER
				      ,MOD_DDTT = SYSDATE
				      ,DEL_YN   = 'Y'
				 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				 */
				sQueryId = "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updCrnWrkMgtSCCrnSchUnitMtl";
			}
			commDao.update(jrParam, sQueryId, logId, methodNm, "TB_YM_CRNSCH");				

			/**********************************************************
			* 5. 설비상태 수정 - 크레인이 고장 또는 Off-Line이 아니고 상태가 다르면
			**********************************************************/
			if ("Y".equals(eqpUpdYn)) {
				/*
				SELECT *
				  FROM TB_YM_CRNSCH
				 WHERE YD_EQP_ID      = :V_YD_EQP_ID
				   AND YD_CRN_SCH_ID != :V_YD_CRN_SCH_ID
				   AND DEL_YN = 'N'
				 ORDER BY DECODE(YD_WRK_PROG_STAT, 'W', 1, 'S', 2, '1', 3, '2', 4) DESC
				 */
				jrParam.setField("YD_EQP_ID"  , ydEqpId  ); //야드설비ID
				JDTORecordSet jsEqpStat = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getWrkListByEqpId", logId, methodNm, "[INFO]");
				if (jsEqpStat.size() == 0) {
					jrParam.setField("EQUIP_STAT", "W"); //야드설비상태
				} else {
					jrParam.setField("EQUIP_STAT", jsEqpStat.getRecord(0).getFieldString("YD_WRK_PROG_STAT")); //야드설비상태
				}
				
				jrParam.setField("EQUIP_GP"  , ydEqpId  ); //야드설비ID
//				jrParam.setField("EQUIP_STAT", ydEqpStat); //야드설비상태
				/* 
				--설비 상태 수정 
				UPDATE TB_YM_EQUIP
				   SET MODIFIER   = :V_MODIFIER
				      ,MOD_DDTT   = SYSDATE
				      ,WPROG_STAT = :V_EQUIP_STAT
				 WHERE EQUIP_GP   = :V_EQUIP_GP
				   AND DEL_YN     = 'N'
				*/	   
				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updStatEqp", logId, methodNm, "설비상태 수정");				
			}

			/**********************************************************
			* 6. E동 SPM2 커팅후 보급 취소 일 경우 밴딩커팅장(3EBD01) 재료 삭제 처리
			**********************************************************/
//			if("3EKE02MM".equals(ydSchCd)) {
//			
//				/*****************************************************
//				 * 저장품제원 : 코일야드L2로 송신(YMA7L002)
//				 ******************************************************/
//				commUtils.printLog(logId, "YMA7L002 JMS전송", "[INFO]");
//				
//				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
//				jrYdMsg.setField("TC_CD"          , "YMA7L002");
//				jrYdMsg.setField("MSG_GP"         , "D"); //삭제일경우 D로 보냄 20170904
//				jrYdMsg.setField("YD_INFO_SYNC_CD", "5");
//				jrYdMsg.setField("STOCK_ID"       , sSTOCK_ID);
//
//				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA7L002", jrYdMsg));
//
//				
//				/*****************************************************
//				 *  밴딩커팅장(3EBD01) 재료 삭제
//				 ******************************************************/
//				jrParam.setField("STOCK_ID"        , "");
//				jrParam.setField("STACK_LAYER_STAT", "E");
//				jrParam.setField("STACK_COL_GP"    , "3EBD01");
//				jrParam.setField("STACK_BED_GP"    , "01");
//				jrParam.setField("STACK_LAYER_GP"  , "01");
//				/*
//				UPDATE TB_YM_STACKLAYER            
//				   SET MOD_DDTT     = SYSDATE             
//				     , MODIFIER     = :V_MODIFIER             
//				     , STACK_LAYER_ACTIVE_STAT = NVL(:V_STACK_LAYER_ACTIVE_STAT, STACK_LAYER_ACTIVE_STAT)
//				     , STOCK_ID                = :V_STOCK_ID
//				     , STACK_LAYER_STAT        = NVL(:V_STACK_LAYER_STAT       , STACK_LAYER_STAT)
//				 WHERE STACK_COL_GP   = :V_STACK_COL_GP
//				   AND STACK_BED_GP   = :V_STACK_BED_GP
//				   AND STACK_LAYER_GP = :V_STACK_LAYER_GP 
//				 */
//				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStkLyrYdStkColBedGp", logId, methodNm, "적치단 UPDATE");
//			} 
			
			/* 크레인작업지시요구 전송
			JDTORecord jrEqpInfo   = JDTORecordFactory.getInstance().create();
			String szydEqpStat = "";
			String sWORK_MODE = "";
			
			
			SELECT *
			  FROM TB_YM_EQUIP    
			 WHERE EQUIP_GP = :V_EQUIP_GP
			   AND DEL_YN   = 'N'
			 
			jrParam.setField("EQUIP_GP" , ydEqpId);
			JDTORecordSet rsResult1 = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYmEqp", logId, methodNm, "설비상태 조회");
			
			if (rsResult1.size() > 0) {
				rsResult1.first();
				jrEqpInfo   = rsResult1.getRecord();
				szydEqpStat = jrEqpInfo.getFieldString("WPROG_STAT");          // 설비 상태
				sWORK_MODE = jrEqpInfo.getFieldString("WORK_MODE");
			}
			
			if (!"B".equals(szydEqpStat) && !"2".equals(sWORK_MODE) && "W".equals(szydEqpStat)) {
				
				commUtils.printLog(logId, "명령선택 기동", "[info]");
				//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
				JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, "A7YML007");

				jrYdMsg.setField("JMS_TC_CD"       , YmConstant.A7YML007);	//크레인작업지시요구
				jrYdMsg.setField("YD_EQP_ID"       , ydEqpId    );	//야드설비ID

				EJBConnector sndConn = new EJBConnector("default", "BCoilL2RcvSeEJB", this);
				JDTORecord jrRtn1 = (JDTORecord)sndConn.trx("rcvA7YML007", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
				
				jrRtn = commUtils.addSndData(jrRtn,jrRtn1);
			}*/

			
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
		String methodNm = "크레인스케줄 권하지시위치 변경[BCoilJspSeEJB.updCrnSchDnWoLoc] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			String sSTOCK_ID         = commUtils.trim(rcvMsg.getFieldString("STOCK_ID"        )); //저장품
			String sYD_EQP_ID        = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); //야드설비ID(크레인)
			String sYD_SCH_CD        = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
			String sYD_CRN_SCH_ID    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )); //야드크레인스케쥴ID
			String sYD_WBOOK_ID      = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"     )); //야드작업예약ID
			String sYD_DN_WO_LOC     = commUtils.trim(rcvMsg.getFieldString("YD_DN_WO_LOC"    )); //야드권하지시위치(신규)
			String sYD_WRK_PROG_STAT = commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
			String sMODIFIER         = commUtils.trim(rcvMsg.getFieldString("MODIFIER"        )); //수정자
			String sYD_DN_WO_LOC_ORG = commUtils.trim(rcvMsg.getFieldString("YD_DN_WO_LOC_ORG")); //야드권하지시위치(기존)
			
			if ("".equals(sYD_CRN_SCH_ID)) {
				throw new Exception("크레인스케쥴ID가 없습니다.");
			} else if ("".equals(sYD_DN_WO_LOC)) {
				throw new Exception("변경할 권하지시위치가 없습니다.");
			} 

			//Return Value
			JDTORecord sndRecord = JDTORecordFactory.getInstance().create();

			String sSTACK_COL_GP   = sYD_DN_WO_LOC.substring(0,  6); //야드적치열구분
			String sSTACK_BED_GP   = sYD_DN_WO_LOC.substring(6,  8); //야드권하지시위치
			String sSTACK_LAYER_GP = sYD_DN_WO_LOC.substring(8, 10); //야드권하지시위치
			String ydDnWoLocOld   = ""; //야드권하지시위치(기존)
			String ydDnWoLayerOld = ""; //야드권하지시위치(기존)
			String ydDnWoLayer    = ""; //야드권하지시단(신규)
			String ydDnWoLocXaxis = ""; //야드권하지시X축(신규)
			String ydDnWoLocYaxis = ""; //야드권하지시Y축(신규)
			String ydDnWoLocZaxis = ""; //야드권하지시Z축(신규)
			String sHOTCHK		  = "";
			JDTORecordSet jsCrnSch = JDTORecordFactory.getInstance().createRecordSet("Temp");
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, sMODIFIER);
			
			
			// PIDEV CHITO007 20230905
			String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "3", "*");

			if ("Y".equals(sApplyYnPI)) {
				/*********************************************
				 * 지포장재 권하위치변경 시 주변 HOT코일 체크
				 *********************************************/		 
				jrParam.setField("STOCK_ID"   , sSTOCK_ID);
				jrParam.setField("STACK_COL_GP"   , sSTACK_COL_GP);
				jrParam.setField("STACK_BED_GP"   , sSTACK_BED_GP);
				jrParam.setField("STACK_LAYER_GP"   , sSTACK_LAYER_GP);
				JDTORecordSet rstHotChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYmPaperPackingHotChk");
				
				if (rstHotChk == null || rstHotChk.size() <= 0) {
					commUtils.printLog(logId, ">>> 지포장 대상재가 아님 skip <<<", "SL");
				} else {
					commUtils.printLog(logId, ">>> 지포장재 권하위치변경 시 주변 hot코일 여부 확인 :  <<<", "SL");
					
					sHOTCHK = rstHotChk.getRecord(0).getFieldString("MILL_WORD_CHK");
					
					commUtils.printLog(logId, ">>> 지포장재 권하위치변경 시 주변 hot코일 여부 확인 : " + sHOTCHK + " <<<", "SL");
					
					if( "Y".equals(sHOTCHK)){
						throw new Exception("권하위치 주변에 HOT코일(48hr미만)이 존재하여 권하위치변경을 할 수 없습니다.");
					}
				}
			}
			
			/*********************************************
			 * 야드작업진행상태  JAVA단에서 한번 더 체크
			 *********************************************/
			/*
			 SELECT *
			   FROM TB_YM_CRNSCH
			  WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID 
			 */
			jrParam.setField("YD_CRN_SCH_ID"   , sYD_CRN_SCH_ID);
			JDTORecordSet rstCrnSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYmCrnsch");
			
			if (rstCrnSch == null || rstCrnSch.size() <= 0) {
				throw new Exception("크레인스케줄이 없습니다.");
			} else {
				commUtils.printLog(logId, ">>> 화면에서 받은 YD_WRK_PROG_STAT 값 : " + sYD_WRK_PROG_STAT + " <<<", "SL");
				
				sYD_WRK_PROG_STAT = rstCrnSch.getRecord(0).getFieldString("YD_WRK_PROG_STAT");
				
				commUtils.printLog(logId, ">>> DB에서 읽은 YD_WRK_PROG_STAT 값 : " + sYD_WRK_PROG_STAT + " <<<", "SL");
			}
			
			
			jrParam.setField("YD_EQP_ID"            , sYD_EQP_ID);
			jrParam.setField("YD_SCH_CD"            , sYD_SCH_CD);
			jrParam.setField("YD_CRN_SCH_ID"       	, sYD_CRN_SCH_ID);
			jrParam.setField("YD_CARLD_WRK_BOOK_ID"	, sYD_WBOOK_ID );	//야드상차작업예약ID
			jrParam.setField("STACK_COL_GP"       	, sSTACK_COL_GP);
			
			/*********************************
			 * 무인화 관련 위치변경 조건 체크
			 ********************************/
			boolean autoFlag       = false;
			JDTORecordSet rsResult = null;
			JDTORecord jrEqpInfo   = JDTORecordFactory.getInstance().create();
			
			String sWPROG_STAT           = "";
			String sYD_EQP_AUTO_CRN_MODE = "";
			String sYD_EQP_WRK_MODE2     = "";
			String sWORK_MODE            = "";//online/off

			/*
			SELECT *
			  FROM TB_YM_EQUIP    
			 WHERE EQUIP_GP = :V_EQUIP_GP
			   AND DEL_YN   = 'N'
			 */
			jrParam.setField("EQUIP_GP" , sYD_EQP_ID);
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYmEqp");
			
			if (rsResult.size() > 0) {
				rsResult.first();
				jrEqpInfo = rsResult.getRecord();
				
				sWPROG_STAT           = jrEqpInfo.getFieldString("WPROG_STAT");          // 설비 상태
				sYD_EQP_AUTO_CRN_MODE = jrEqpInfo.getFieldString("YD_EQP_AUTO_CRN_MODE");// AutoCrn 상태
				sYD_EQP_WRK_MODE2     = jrEqpInfo.getFieldString("YD_EQP_WRK_MODE2"); 	 // AutoCrn 여부
				sWORK_MODE            = jrEqpInfo.getFieldString("WORK_MODE");
				
				if ("A".equals(sYD_EQP_WRK_MODE2)) {// A:무인 R:리모컨
					autoFlag = true; 
				}
			}
			
			//W:명령선택대기 S:스케줄작성중 
			if ("W".equals(sYD_WRK_PROG_STAT)) {
				autoFlag = false;
			}
			
			// 위치검색실패인 경우 유인으로 처리
			if ("XX010101".equals(sYD_DN_WO_LOC_ORG)) {
				autoFlag = false;
			}

			// 일시정지-권하위치변경 적용여부
			String sAPP030 = ymComm.BCoilApplyYn("APP030","3","S5");
			
			if (autoFlag && !"Y".equals(sAPP030)) { 
				//설비상태 체크하여 Auto일경우 일시정지 상태에서만 가능하게 수정
				if (!"4".equals(sYD_EQP_AUTO_CRN_MODE) && !"B".equals(sWPROG_STAT)) { //4: 일시정지 B:고장
					throw new Exception("무인크레인 [" + sYD_EQP_ID + "]이 일시정지이거나 고장상태가 아니면 권하위치를 변경할 수 없습니다.");
				}
			}
			
			/********************************************
			 * 적치위치 변경에 따른 위치 정합성 체크
			 *******************************************/
			jrParam.setField("STOCK_ID"      , sSTOCK_ID);
			jrParam.setField("STACK_COL_GP"  , sSTACK_COL_GP);
			jrParam.setField("STACK_BED_GP"  , sSTACK_BED_GP);
			jrParam.setField("STACK_LAYER_GP", sSTACK_LAYER_GP);
			
			//2단적치 기울기 공식 적용 여부
//			String sAPP024 = ymComm.BCoilApplyYn("APP024","3","1");
			
			commUtils.printLog(logId, "[STOCK_ID : "+sSTOCK_ID+"] [YD_DN_WO_LOC : "+sYD_DN_WO_LOC+"] 권하위치변경 >> 적치기준 조회", "[INFO]");
			/*****************************************************************************
			 * 스크랩 위치검색실패일경우 스크랩 정보조회후 정보 없으면 권하위치변경 못함
			 ****************************************************************************/
			if (sSTOCK_ID.startsWith("S") && "XX010101".equals(sYD_DN_WO_LOC_ORG)) {
				/*
				SELECT *
				  FROM USRPOA.TB_PO_COILSHEARORD_SCRAP
				 WHERE SCRAP_COIL_NO = :V_STOCK_ID
				   AND STEP_NO = (SELECT MAX(STEP_NO) 
				                    FROM USRPOA.TB_PO_COILSHEARORD_SCRAP
				                   WHERE SCRAP_COIL_NO = :V_STOCK_ID)
				 */
				JDTORecordSet jsScrInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getScrInfo", logId, methodNm, "스크랩 정보 조회");
				if (jsScrInfo.size() == 0) {
					throw new Exception("스크랩["+sSTOCK_ID+"] 정보가 없으므로 권하위치를 변경할 수 없습니다. 조업L2에 문의하세요");
				}
			}
			
			//권하변경위치가 스크랩이 아니고 야드일 때만 적치기준 확인
			if (!sSTOCK_ID.startsWith("S") && sSTACK_COL_GP.matches("[3][A-E]\\d\\d\\d\\d")) {
				
				EJBConnector ejbConn = new EJBConnector("default", "BCoilSchSeEJB", this);
				String isSuc = "";
//				if ("Y".equals(sAPP024)) {
					isSuc = (String)ejbConn.trx("procStockIdBaseCheckNew", new Class[] { String.class, String.class, JDTORecord.class }, new Object[] { logId, methodNm, jrParam });
//				} else {
//					isSuc = (String)ejbConn.trx("procStockIdBaseCheck", new Class[] { String.class, String.class, JDTORecord.class }, new Object[] { logId, methodNm, jrParam });	
//				}
				
				if (YmConstant.RETN_CD_FAILURE.equals(isSuc)) {
					String LogMsg = commUtils.trim(jrParam.getFieldString("LOG_MSG"));
					if("".equals(LogMsg)) {
						if ("01".equals(sSTACK_LAYER_GP)) {
							LogMsg = "좌우 코일 적치기준불가(두께, 폭, 중량)";
						} else {
							LogMsg = "하단 코일 적치기준불가(두께, 폭, 중량)";
						}
					}
					if ("01".equals(sSTACK_LAYER_GP)) {
						throw new Exception(LogMsg);
					} else {
						throw new Exception(LogMsg);
					}
				}
			}
			

			if (sYD_DN_WO_LOC.length() == 6) {
			
				/**********************************************************
				* 1. 신규 권하지시위치 Bed정보 조회
				**********************************************************/
				jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCrnSchDnWoLocBt", logId, methodNm, "신규권하위치 조회");
				
			} else if (sYD_DN_WO_LOC.length() == 8) {
				
				jrParam.setField("STACK_BED_GP"       	, sSTACK_BED_GP);
				jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCrnSchDnWoLocCurBed", logId, methodNm, "신규권하위치 조회");
				
			} else {
				
				jrParam.setField("STACK_BED_GP"       	, sSTACK_BED_GP);
				jrParam.setField("STACK_LAYER_GP"      	, sSTACK_LAYER_GP);
				jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCrnSchDnWoLocCurLyr", logId, methodNm, "신규권하위치 조회");
			}
			
			
			if (jsCrnSch == null || jsCrnSch.size() <= 0) {
				throw new Exception("신규 권하지시위치[" + sYD_DN_WO_LOC + "] 정보가 없습니다.");
			} else {
			
		    	JDTORecord jrCrnSch = jsCrnSch.getRecord(0);

		    	ydDnWoLocOld   		= commUtils.trim(jrCrnSch.getFieldString("YD_DN_WO_LOC_OLD"  ));
		    	ydDnWoLayerOld 		= commUtils.trim(jrCrnSch.getFieldString("YD_DN_WO_LAYER_OLD"));
		    	sSTACK_BED_GP       = commUtils.trim(jrCrnSch.getFieldString("STACK_BED_GP"      )); 
		    	ydDnWoLayer         = commUtils.trim(jrCrnSch.getFieldString("STACK_LAYER_GP"    )); 
		    	ydDnWoLocXaxis 		= commUtils.trim(jrCrnSch.getFieldString("YD_DN_WO_LOC_XAXIS"));
		    	ydDnWoLocYaxis 		= commUtils.trim(jrCrnSch.getFieldString("YD_DN_WO_LOC_YAXIS"));
		    	ydDnWoLocZaxis 		= commUtils.trim(jrCrnSch.getFieldString("YD_DN_WO_LOC_ZAXIS"));
			    String dlLocChkRst 	= commUtils.trim(jrCrnSch.getFieldString("DL_LOC_CHK_RST"    ));
	

			    if ("UP".equals(dlLocChkRst)) {
					throw new Exception("권상/권하대기(U) 재료가 적치되어 있습니다.");
				}

			    //혹시 권하지시위치가 잘못 등록되어 있으면
			    if (ydDnWoLocOld.length() != 8) {
			    	ydDnWoLocOld = "XX010101";
				}
		    }
			
			
			/**************************************
			 * 무인화 일때 처리
			 **************************************/
			if (autoFlag && !"W".equals(sYD_WRK_PROG_STAT)) {
				//변경위치 임시 저장
				/*
				UPDATE TB_YM_CRNSCH
				   SET YD_DN_WO_LOC_TO    = :V_YD_DN_WO_LOC_TO
				     , STL_NO_TEMP        = :V_STL_NO_TEMP
				     , STK_LYR_NO_TEMP    = :V_STK_LYR_NO_TEMP
				     , YD_L2_REQUEST_STAT = :V_YD_L2_REQUEST_STAT
				     , MODIFIER           = :V_MODIFIER
				     , MOD_DDTT           = SYSDATE
				 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				 */
				jrParam.setField("YD_DN_WO_LOC_TO"   , sSTACK_COL_GP+sSTACK_BED_GP);
				jrParam.setField("STL_NO_TEMP"       , sSTOCK_ID);
				jrParam.setField("STK_LYR_NO_TEMP"   , sSTACK_LAYER_GP);
				jrParam.setField("YD_L2_REQUEST_STAT", YmConstant.YD_L2_REQUEST_STAT_5);
				jrParam.setField("YD_CRN_SCH_ID", sYD_CRN_SCH_ID);
				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.upYmCrnSchLocStat");
				/**********************************************************
				* 크레인작업지시요구 전문 조회
				**********************************************************/
				// 일시정지-권하위치변경 적용여부
				if ("Y".equals(sAPP030)) {
					JDTORecord jrS5Msg = commUtils.getParam(logId, methodNm, sMODIFIER);
					
					jrS5Msg.setField("JMS_TC_CD"         , YmConstant.YMA7L004); //크레인작업지시요구
					jrS5Msg.setField("YD_CRN_SCH_ID"     , sYD_CRN_SCH_ID     ); //야드크레인스케쥴ID
					jrS5Msg.setField("MSG_GP"            , "U"   ); //전문구분 - 재지시
					jrS5Msg.setField("YD_CRN_SCH_RMD_CNT", "S5"  ); //S5 일시정지 후 권하위치 변경
	
					//분동코일 권하위치 변경
					if ("YD99MM".equals(sYD_SCH_CD.substring(2,8))) { //분동코일
						/*
						SELECT CM.STOCK_ID
						  FROM TB_YM_CRNSCH    CS
						     , TB_YM_CRNWRKMTL CM
						 WHERE CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
						   AND CS.DEL_YN = 'N'
						   AND CM.DEL_YN = 'N'
						   AND CS.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID 
						 */
						JDTORecordSet jsInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getStockIdByCrnSchId", logId, methodNm, "분동코일ID 조회");
						if (jsInfo.size() > 0) {
							
							String sStockId = jsInfo.getRecord(0).getFieldString("STOCK_ID");
							
							jrParam.setField("REPR_CD_GP"	, "BDCOIL");
							jrParam.setField("CD_GP"		, "3");
							jrParam.setField("ITEM"			, sStockId);
							
							JDTORecordSet jsRuleInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmRule", logId, methodNm, "분동코일 기준 조회");
							
							jrS5Msg.setField("YD_STL_WT"         , jsRuleInfo.getRecord(0).getFieldString("DTL_ITM1")  ); //중량
							jrS5Msg.setField("YD_STL_T"          , jsRuleInfo.getRecord(0).getFieldString("DTL_ITM2")   );
							jrS5Msg.setField("YD_STL_W"          , jsRuleInfo.getRecord(0).getFieldString("DTL_ITM3")   );
							jrS5Msg.setField("COIL_OUTDIA"       , jsRuleInfo.getRecord(0).getFieldString("DTL_ITM4")); //외경
							jrS5Msg.setField("COIL_INDIA"        , jsRuleInfo.getRecord(0).getFieldString("DTL_ITM5") );  //내경
							jrS5Msg.setField("STOCK_ID"          , sStockId    );
							
							jrS5Msg.setField("STOCK_ID", jsInfo.getRecord(0).getFieldString("STOCK_ID"));
							sndRecord = commUtils.addSndData(commDao.getMsgL2("YMA7L004WC", jrS5Msg));
						}
					}else{
						sndRecord = commUtils.addSndData(commDao.getMsgL2("YMA7L004", jrS5Msg));
					}
					
					return sndRecord;
				}
				
				//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
				JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, sMODIFIER);

				jrYdMsg.setField("JMS_TC_CD"       , YmConstant.A7YML007);	//크레인작업지시요구
				jrYdMsg.setField("YD_EQP_ID"       , sYD_EQP_ID       );	//야드설비ID
				jrYdMsg.setField("YD_WRK_PROG_STAT", "2".equals(sYD_WRK_PROG_STAT) ? "5" : sYD_WRK_PROG_STAT);	//야드작업진행상태(권하위치변경 요구상태)
				jrYdMsg.setField("YD_SCH_CD"       , sYD_SCH_CD       );	//야드스케쥴코드
				jrYdMsg.setField("YD_CRN_SCH_ID"   , sYD_CRN_SCH_ID   );	//야드크레인스케쥴ID
				jrYdMsg.setField("MODIFIER"        , sMODIFIER        );	//수정자

				EJBConnector sndConn = new EJBConnector("default", "BCoilL2RcvSeEJB", this);
				JDTORecord jrRtn = (JDTORecord)sndConn.trx("rcvA7YML007", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });

				sndRecord = commUtils.addSndData(sndRecord, jrRtn);
					
				return sndRecord;
			}
			
			
			/*************************************
			 * 유인크레인(현행)일 때 처리
			 *************************************/
			//W상태는 유인크레인과 같은 방법으로 처리 
			
			
			/**********************************************************
			* 2. 권하지시위치 수정
			**********************************************************/
			jrParam.setField("STACK_COL_GP_OLD"   , ydDnWoLocOld.substring(0, 6));
			jrParam.setField("STACK_BED_GP_OLD"   , ydDnWoLocOld.substring(6, 8));
			jrParam.setField("STACK_LAYER_GP_OLD" , ydDnWoLayerOld);
			jrParam.setField("STACK_COL_GP_NEW"   , sSTACK_COL_GP    );
			jrParam.setField("STACK_BED_GP_NEW"   , sSTACK_BED_GP    );
			if (sYD_DN_WO_LOC.length() == 6) {
				jrParam.setField("YD_DN_WO_LOC"      , sYD_DN_WO_LOC+sSTACK_BED_GP     );
			} else {
				jrParam.setField("YD_DN_WO_LOC"      , sYD_DN_WO_LOC.substring(0, 8));
			}

			jrParam.setField("YD_DN_WO_LAYER"    , ydDnWoLayer   );
			jrParam.setField("STACK_BED_GP"      , sSTACK_BED_GP );
			jrParam.setField("STACK_LAYER_GP"    , ydDnWoLayer   );
			jrParam.setField("YD_DN_WO_LOC_XAXIS", ydDnWoLocXaxis);
			jrParam.setField("YD_DN_WO_LOC_YAXIS", ydDnWoLocYaxis);
			jrParam.setField("YD_DN_WO_LOC_ZAXIS", ydDnWoLocZaxis);


			//적치단 수정 - 기존
			/*
			UPDATE TB_YM_STACKLAYER
			   SET STOCK_ID = NULL
			     , STACK_LAYER_STAT = 'E'
			 WHERE STOCK_ID IN (SELECT STOCK_ID
			                     FROM TB_YM_CRNWRKMTL
			                    WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			                 )
			   AND STACK_COL_GP = :V_STACK_COL_GP_OLD
			   AND STACK_BED_GP = :V_STACK_BED_GP_OLD   
			   AND STACK_LAYER_STAT = 'D'
			 */
			commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStkLyrYdByCrnSchId", logId, methodNm, "기존권하위치 CLEAR");	
			
			
			//신규 적치단 재료정보READ
			/*
			SELECT A.YD_CRN_SCH_ID
			     , A.STOCK_ID         -- 기존 재료정보 
			  FROM TB_YM_CRNWRKMTL A
			 WHERE A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			   AND A.DEL_YN = 'N'
			 */
			JDTORecordSet jsCrnSchMtl = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCrnWekMtlByschid", logId, methodNm, "기존권하위치 조회");
			

			JDTORecord recOutTemp = null;
			JDTORecord recInTemp = null;
			
			String szSTOCK_ID = null; 
			 
			int intRtnVal = 0; 
			
			//----------------------------------------------------------------------------------------------------------
			//신규적치단 활성화
			//----------------------------------------------------------------------------------------------------------
			for(int Loop_i = 1; Loop_i <= jsCrnSchMtl.size(); Loop_i++) {
				jsCrnSchMtl.absolute(Loop_i);
		    	recOutTemp = JDTORecordFactory.getInstance().create();
		    	recOutTemp.setRecord(jsCrnSchMtl.getRecord());
		    	
		    	szSTOCK_ID   = commUtils.trim(recOutTemp.getFieldString("STOCK_ID"     ));
		    	
		    	recInTemp  = JDTORecordFactory.getInstance().create();
		    	recInTemp.setField("STACK_COL_GP"   , sSTACK_COL_GP);
		    	recInTemp.setField("STACK_BED_GP"   , sSTACK_BED_GP);
		    	recInTemp.setField("STACK_LAYER_GP" , ydDnWoLayer);
		    	recInTemp.setField("STOCK_ID"       , szSTOCK_ID);
		    	recInTemp.setField("STACK_LAYER_ACTIVE_STAT", "E");
		    	recInTemp.setField("STACK_LAYER_STAT"       , "D");
		    	recInTemp.setField("MODIFIER"      , sMODIFIER);
		    	
		    	/*
				UPDATE TB_YM_STACKLAYER            
				   SET MOD_DDTT     = SYSDATE             
				     , MODIFIER     = :V_MODIFIER             
				     , STACK_LAYER_ACTIVE_STAT = NVL(:V_STACK_LAYER_ACTIVE_STAT, STACK_LAYER_ACTIVE_STAT)
				     , STOCK_ID                = :V_STOCK_ID
				     , STACK_LAYER_STAT        = NVL(:V_STACK_LAYER_STAT       , STACK_LAYER_STAT)
				 WHERE STACK_COL_GP   = :V_STACK_COL_GP
				   AND STACK_BED_GP   = :V_STACK_BED_GP
				   AND STACK_LAYER_GP = :V_STACK_LAYER_GP 
		    	 */
				intRtnVal = commDao.update(recInTemp, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStkLyrYdStkColBedGp", logId, methodNm, "TB_YM_STACKLAYER 등록");
				
				if (intRtnVal <= 0) {
					commUtils.printLog(logId, "[" + methodNm + "] 적치단[" + sSTACK_COL_GP + "]활성화중 ERROR 발생", "SL");
					throw new Exception("적치단변경시 오류 발생.");
				}
			}
			

			//크레인스케줄 수정 - 권상, 권하지시위치
			commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updCrnSchDnWoLocCrnSch", logId, methodNm, "TB_YM_CRNSCH");				

			//기존 대차, 차량 권하위치에서 일반야드로 변경 시 대차 or 차량스케줄 작업예약ID 삭제
			ydDnWoLocOld = ydDnWoLocOld.substring(2, 4);
			if (("TC".equals(ydDnWoLocOld) || "TR".equals(ydDnWoLocOld)) && !ydDnWoLocOld.equals(sYD_DN_WO_LOC.substring(2, 4))) {
				if ("TC".equals(ydDnWoLocOld)) {
					//대차스케줄 수정 - 상차작업예약ID 삭제
					//작업예약 Table 우선순위 Update
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updCrnSchDnWoLocTCarSch", logId, methodNm, "TB_YM_TCARSCH");				
					
				} else {
					//차량스케줄 수정 - 상차작업예약ID 삭제
					//작업예약 Table 우선순위 Update
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updCrnSchDnWoLocCarSch", logId, methodNm, "TB_YD_CARSCH");				
				}
			}
			
			/**********************************************************
			* 3. 크레인작업지시요구 전문 조회
			**********************************************************/
			//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
			JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, sMODIFIER);

			jrYdMsg.setField("JMS_TC_CD"       , YmConstant.A7YML007);	//크레인작업지시요구
			jrYdMsg.setField("YD_EQP_ID"       , sYD_EQP_ID      );	//야드설비ID
			jrYdMsg.setField("YD_WRK_PROG_STAT", sYD_WRK_PROG_STAT);	//야드작업진행상태
			jrYdMsg.setField("YD_SCH_CD"       , sYD_SCH_CD      );	//야드스케쥴코드
			jrYdMsg.setField("YD_CRN_SCH_ID"   , sYD_CRN_SCH_ID   );	//야드크레인스케쥴ID
			jrYdMsg.setField("MODIFIER"        , sMODIFIER     );	//수정자

			EJBConnector sndConn = new EJBConnector("default", "BCoilL2RcvSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)sndConn.trx("rcvA7YML007", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });

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
	 *      [A] 오퍼레이션명 : 작업예약 취소처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtWrkBookCncl(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "작업예약 취소처리[BCoilJspSeEJB.trtWrkBookCncl] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			String ydCrnSchId= commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID")); //야드설비ID
			String ydWbookId = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID")); //야드작업예약ID
		    String ydEqpId   = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"  )); //야드설비ID
		    String ydSchCd   = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"  )); //야드스케쥴코드
			String modifier  = commUtils.trim(rcvMsg.getFieldString("MODIFIER"   )); //수정자
			
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
			/*
			SELECT YD_CRN_SCH_ID
			  FROM TB_YM_CRNSCH
			 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
			   AND DEL_YN      = 'N'
			 */
			JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCommWbCrnSch", logId, methodNm, "크레인작업지시read");
			if (jsCrnSch != null && jsCrnSch.size() > 0) {				
				throw new Exception("작업예약ID[" + ydWbookId + "]의 크레인스케줄 정보가 " + jsCrnSch.size() + " 건 존재합니다.");
		    }

			String sAPP060_ZZ002_YN = ymComm.BCoilApplyYn("APP060","3","ZZ002_YN");   //작업예약 삭제시 이송상차 STOCK 이송 작업지시 번호 Clear 여부
			if (sAPP060_ZZ002_YN.equals("Y")) {
			
				if("".equals(ydSchCd)) {
					//YD_SCH_CD 가 파라메터로 넘어오지 않았다면 작업예약ID 로 야드스케줄코드 값을 가져온다.
					JDTORecordSet jsWrkBook = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYmWrkbook", logId, methodNm, "작업예약조회");
					if(jsWrkBook.size() > 0) {
						ydSchCd = commUtils.trim(jsWrkBook.getRecord(0).getFieldString("YD_SCH_CD"));
					}
				}
			}
			
			/**********************************************************
			* 3. 차량/대차스케줄 야드상차작업예약ID, 야드하차작업예약ID 삭제
			**********************************************************/
			//차량스케줄 야드상차작업예약ID, 야드하차작업예약ID 삭제
			/*
			UPDATE USRYDA.TB_YD_CARSCH
			   SET MODIFIER              = :V_MODIFIER
			      ,MOD_DDTT              = SYSDATE
			      ,YD_CARLD_WRK_BOOK_ID  = DECODE(YD_CARLD_WRK_BOOK_ID,:V_YD_WBOOK_ID,NULL,YD_CARLD_WRK_BOOK_ID)
			      ,YD_CARUD_WRK_BOOK_ID  = DECODE(YD_CARUD_WRK_BOOK_ID,:V_YD_WBOOK_ID,NULL,YD_CARUD_WRK_BOOK_ID)
			 WHERE DEL_YN                = 'N'
			   AND (YD_CARLD_WRK_BOOK_ID = :V_YD_WBOOK_ID OR YD_CARUD_WRK_BOOK_ID = :V_YD_WBOOK_ID)
			 */
			commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updCommCarSchWbDel", logId, methodNm, "TB_YD_CARSCH");				
		
			//대차스케줄 야드상차작업예약ID, 야드하차작업예약ID 삭제
			/*
			UPDATE USRYDA.TB_YD_TCARSCH
			   SET MODIFIER              = :V_MODIFIER
			      ,MOD_DDTT              = SYSDATE
			      ,YD_CARLD_WRK_BOOK_ID  = DECODE(YD_CARLD_WRK_BOOK_ID,:V_YD_WBOOK_ID,NULL,YD_CARLD_WRK_BOOK_ID)
			      ,YD_CARUD_WRK_BOOK_ID  = DECODE(YD_CARUD_WRK_BOOK_ID,:V_YD_WBOOK_ID,NULL,YD_CARUD_WRK_BOOK_ID)
			 WHERE DEL_YN                = 'N'
			   AND (YD_CARLD_WRK_BOOK_ID = :V_YD_WBOOK_ID OR YD_CARUD_WRK_BOOK_ID = :V_YD_WBOOK_ID)
			 */
			commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updCommTcarSchWbDel", logId, methodNm, "TB_YM_TCARSCH");				

		    /**********************************************************
			* 4. 작업예약/재료 삭제
			**********************************************************/
			//작업예약재료 삭제
			/*
			UPDATE TB_YM_WRKBOOKMTL
			   SET MODIFIER    = :V_MODIFIER
			      ,MOD_DDTT    = SYSDATE
			      ,DEL_YN      = 'Y'
			 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
			   AND DEL_YN      = 'N'
			 */
			commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updDelYnWrkBookMtl", logId, methodNm, "작업예약/재료 삭제");				

			//작업예약 삭제
			/*
			UPDATE TB_YM_WRKBOOK
			   SET MODIFIER    = :V_MODIFIER
			      ,MOD_DDTT    = SYSDATE
			      ,DEL_YN      = 'Y'
			 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
			   AND DEL_YN      = 'N'
			 */
			commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updDelYnWrkBook", logId, methodNm, "작업예약 삭제");	
			
			if (sAPP060_ZZ002_YN.equals("Y")) {
				
				if(!"".equals(ydSchCd)) {
					//이송상차의 경우 STOCK 의 이송 작업지시 번호 Clear
					if("PT02UM".equals(ydSchCd.substring(2)) || "PT06UM".equals(ydSchCd.substring(2))) {
						
						/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updClearStockTransWordNo 
						UPDATE TB_YM_STOCK
						   SET FRTOMOVE_WORD_NO = ''
						      ,SHEAR_SUPPLY_SEQ = ''
						 WHERE STOCK_ID = (
						 
						            SELECT B.STOCK_ID
						            FROM   TB_YM_WRKBOOK A
						                  ,TB_YM_WRKBOOKMTL B
						            WHERE  A.YD_WBOOK_ID = :V_YD_WBOOK_ID
						            AND    A.YD_WBOOK_ID = B.YD_WBOOK_ID
						 ) */
						commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updClearStockTransWordNo", logId, methodNm, "STOCK 의 이송 작업지시 번호 Clear");	
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
	 * [A] 오퍼레이션명 : 크레인작업요구현황조회-스케줄취소
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return GridData
	 * @throws DAOException
	 */	
	public JDTORecord updCraneSchCancel(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업요구현황조회 스케줄취소[BCoilJspSeEJB.updCraneSchCancel] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			String sTRT_DTL_GP = commUtils.trim(gdReq.getParam("TRT_DTL_GP"));	//처리상세구분 RS스케줄재기동
			String sIS_SCH_MTL = StringHelper.evl(commUtils.trim(gdReq.getParam("IS_SCH_MTL")), "N"); // 재료단위 스케줄 취소여부
			String sYD_GP      = StringHelper.evl(commUtils.trim(gdReq.getParam("YD_GP")), "3");
			
			//Return Value
			JDTORecord jrRtn = null;
			
			boolean autoFlag = false;
			boolean mainFlag = false; //취소트랜젝션 플래그
			
			String szydEqpStat = "";
			String szEqpAutoCrnMode = "";
			String szEqpAutoCrnYN = "";
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			JDTORecordSet rsResult = null;
			JDTORecord jrEqpInfo   = JDTORecordFactory.getInstance().create();
			
			String sYD_WRK_PROG_STAT = "";
			String sYD_EQP_ID        = "";
			String sYD_CRN_SCH_ID    = "";
			String sYD_SCH_CD        = "";
			String sYD_WBOOK_ID      = "";
			String sWORK_MODE        = "";//online/off
			
			String sSTACK_COL_GP = "";
			String sYD_BED_GP    = "";
			String sYD_LYR_GP    = "";
			String sYD_DN_WO_LOC_ORG = "";
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int i = 0; i < rowCnt; i++) {
				
				sSTACK_COL_GP     = commUtils.getValue(gdReq, "YD_UP_WO_LOC_LAYER", i).substring(0, 6);
				sYD_BED_GP        = commUtils.getValue(gdReq, "YD_UP_WO_LOC_LAYER", i).substring(6, 8);
				sYD_LYR_GP        = commUtils.getValue(gdReq, "YD_UP_WO_LAYER"    , i); //단
				sYD_DN_WO_LOC_ORG = commUtils.getValue(gdReq, "YD_DN_WO_LOC_ORG"  , i); //기존권하지시위치
				sYD_WRK_PROG_STAT = commUtils.getValue(gdReq, "YD_WRK_PROG_STAT", i);
				sYD_EQP_ID        = commUtils.getValue(gdReq, "YD_EQP_ID"       , i);
				sYD_CRN_SCH_ID    = commUtils.getValue(gdReq, "YD_CRN_SCH_ID"   , i);
				sYD_SCH_CD        = commUtils.getValue(gdReq, "YD_SCH_CD"       , i);
				sYD_WBOOK_ID      = commUtils.getValue(gdReq, "YD_WBOOK_ID"     , i);
				
				jrParam.setField("STACK_COL_GP", sSTACK_COL_GP);
				jrParam.setField("STACK_BED_GP", sYD_BED_GP);
				
				/*********************************
				 * 무인크레인 관련 위치변경 조건 체크
				 ********************************/
				/*
				SELECT *
				  FROM TB_YM_EQUIP    
				 WHERE EQUIP_GP = :V_EQUIP_GP
				   AND DEL_YN   = 'N'
				 */
				jrParam.setField("EQUIP_GP" , sYD_EQP_ID);
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYmEqp", logId, methodNm, "무인크레인 관련 위치변경 조건 체크");
				
				if (rsResult.size() > 0) {
					rsResult.first();
					jrEqpInfo = rsResult.getRecord();
					
					szydEqpStat      = jrEqpInfo.getFieldString("WPROG_STAT");          // 설비 상태
					szEqpAutoCrnMode = jrEqpInfo.getFieldString("YD_EQP_AUTO_CRN_MODE");// AutoCrn 상태
					szEqpAutoCrnYN   = jrEqpInfo.getFieldString("YD_EQP_WRK_MODE2"); 	// AutoCrn 여부
					sWORK_MODE       = jrEqpInfo.getFieldString("WORK_MODE");
					
					if ("A".equals(szEqpAutoCrnYN)) {// A:무인
						autoFlag = true; 
						mainFlag = true;
					}
				}
				
				/*********************************************
				 * 야드작업진행상태  JAVA단에서 한번 더 체크
				 *********************************************/
				/*
				 SELECT *
				   FROM TB_YM_CRNSCH
				  WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID 
				 */
				jrParam.setField("YD_CRN_SCH_ID"   , sYD_CRN_SCH_ID);
				JDTORecordSet rstCrnSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYmCrnsch", logId, methodNm, "야드작업진행상태  JAVA단에서 한번 더 체크");
				
				if (rstCrnSch == null || rstCrnSch.size() <= 0) {
					throw new Exception("크레인스케줄이 없습니다.");
				} else {
					sYD_WRK_PROG_STAT = rstCrnSch.getRecord(0).getFieldString("YD_WRK_PROG_STAT");
				}
				commUtils.printLog(logId, "["+sYD_CRN_SCH_ID+"] = "+ sYD_WRK_PROG_STAT , "[INFO]");
				
				/*********************************************
				 *  권하지시(권상)상태에서는 스케줄취소 스킵
				 *  권상 전에만 스케줄 취소가 가능함
				 *********************************************/
				if (!"W".equals(sYD_WRK_PROG_STAT)   //대기
				  &&!"S".equals(sYD_WRK_PROG_STAT)   //대기
				  &&!"1".equals(sYD_WRK_PROG_STAT)) {//선택(권상지시)
					continue;
				}
				
				commUtils.printLog(logId, "[무인크레인 여부]="+ autoFlag + "[YD_EQP_AUTO_CRN_MODE]="+szEqpAutoCrnMode , "[INFO]");
				commUtils.printLog(logId, "[YD_EQP_WRK_MODE2]="+ szEqpAutoCrnYN + "[YD_WRK_PROG_STAT]="+sYD_WRK_PROG_STAT , "[INFO]");

				// 위치검색실패인 경우 유인으로 처리
				if ("XX010101".equals(sYD_DN_WO_LOC_ORG)) {
					autoFlag = false;
				} 
				
				//W:명령선택대기 - L2에 작업지시가 내려가지 않은 상태
				if ("W".equals(sYD_WRK_PROG_STAT)) {
					autoFlag = false;
				}
				
				/**********************************
				 * 스케줄 재기동의 경우 동일 작업예약 스케줄상태 확인후 재기동여부 판단
				 **********************************/
				if ("RS".equals(sTRT_DTL_GP)) {
					/*
					SELECT CS.*
					  FROM TB_YM_CRNSCH    CS
					     , TB_YM_CRNWRKMTL CM
					     , (SELECT CS.YD_WBOOK_ID
					             , CS.YD_CRN_SCH_ID
					          FROM TB_YM_CRNSCH    CS
					             , TB_YM_CRNWRKMTL CM
					         WHERE CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
					           AND CS.DEL_YN = 'N'
					           AND CM.DEL_YN = 'N'
					           AND CS.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
					       ) PP
					 WHERE CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
					   AND CS.DEL_YN = 'N'
					   AND CM.DEL_YN = 'N'
					   AND CS.YD_WBOOK_ID    = PP.YD_WBOOK_ID
					   AND CS.YD_CRN_SCH_ID != PP.YD_CRN_SCH_ID 
					 */
					JDTORecordSet jsSchList = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCrnSchSameWrkBook", logId, methodNm, "동일 작업예약 스케줄 조회");
					if (jsSchList.size() > 0) {
						for (int ii = 0; ii < jsSchList.size(); ++ii) {
							if (!"W".equals(jsSchList.getRecord(ii).getFieldString("YD_WRK_PROG_STAT"))) {
								throw new Exception("동일 작업예약의 다른 스케줄이 대기상태가 아니므로 스케줄 재기동을 할 수 없습니다.");
							}
						}
					}
				}
				
				/*********************************
				 * 무인 크레인 작업일 경우 
				 *********************************/
				if (autoFlag) { 
					
					//일시정지-스케줄취소 적용여부
					String sAPP030 = ymComm.BCoilApplyYn("APP030","3","SD"); 
					
					//설비상태 체크하여 Auto일경우 일시정지 상태에서만 가능하게 수정
					if ("Y".equals(sAPP030)) {
						//일시정지-스케줄취소
					} else if (!"4".equals(szEqpAutoCrnMode) && !"B".equals(szydEqpStat)) { //4: 일시정지 B:고장
						throw new Exception("무인크레인 [" + sYD_EQP_ID + "]이 일시정지이거나 고장상태가 아니면 취소할 수 없습니다.");
					}
					
					/**********************************************************
					* 크레인작업지시요구 전문 조회 - 일시정지-스케줄취소
					**********************************************************/
					if ("Y".equals(sAPP030)) {
						//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
						JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
		                
		               	jrYdMsg.setField("JMS_TC_CD"         , YmConstant.YMA7L004); //크레인작업지시요구
						jrYdMsg.setField("YD_CRN_SCH_ID"     , sYD_CRN_SCH_ID         ); //야드크레인스케쥴ID
						jrYdMsg.setField("MSG_GP"            , "D"   ); //전문구분
						jrYdMsg.setField("YD_CRN_SCH_RMD_CNT", "SD"  ); //S1 일시정지 후 스케줄취소

						//jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA7L004", jrYdMsg));
						
						if ("3EYD99MM".equals(sYD_SCH_CD) || "3AYD99MM".equals(sYD_SCH_CD) || "3BYD99MM".equals(sYD_SCH_CD)) { //분동코일
							/*
							SELECT CM.STOCK_ID
							  FROM TB_YM_CRNSCH    CS
							     , TB_YM_CRNWRKMTL CM
							 WHERE CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
							   AND CS.DEL_YN = 'N'
							   AND CM.DEL_YN = 'N'
							   AND CS.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID 
							 */
							JDTORecordSet jsInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getStockIdByCrnSchId", logId, methodNm, "분동코일ID 조회");
							if (jsInfo.size() > 0) {
								jrYdMsg.setField("STOCK_ID", jsInfo.getRecord(0).getFieldString("STOCK_ID"));
								jrRtn = commUtils.addSndData(commDao.getMsgL2("YMA7L004WC", jrYdMsg));
							}
						} else {
							jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA7L004", jrYdMsg));
						}
						
					} else {
						//크레인 스케줄의 취소 전문 전송
						JDTORecord tcRecord = JDTORecordFactory.getInstance().create();
						tcRecord.setField("YD_CRN_SCH_ID"   , sYD_CRN_SCH_ID);
						tcRecord.setField("MSG_GP"          , "D");
	
						if ("3EYD99MM".equals(sYD_SCH_CD) || "3AYD99MM".equals(sYD_SCH_CD) || "3BYD99MM".equals(sYD_SCH_CD)) { //분동코일
							/*
							SELECT CM.STOCK_ID
							  FROM TB_YM_CRNSCH    CS
							     , TB_YM_CRNWRKMTL CM
							 WHERE CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
							   AND CS.DEL_YN = 'N'
							   AND CM.DEL_YN = 'N'
							   AND CS.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID 
							 */
							JDTORecordSet jsInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getStockIdByCrnSchId", logId, methodNm, "분동코일ID 조회");
							if (jsInfo.size() > 0) {
								tcRecord.setField("STOCK_ID", jsInfo.getRecord(0).getFieldString("STOCK_ID"));
								jrRtn = commUtils.addSndData(commDao.getMsgL2("YMA7L004WC", tcRecord));
							}
						} else {
							jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA7L004", tcRecord));
						}
					}
					
					commUtils.printLog(logId, "["+szSessionName+"] 스케줄["+sYD_CRN_SCH_ID+"]을 스케쥴 취소 요청 전송", "S+");
		        	
					// 작업대기상태 update : 작업취소와 구분되게 D 로 상태 없데이트 함...
					/*
					UPDATE TB_YM_CRNSCH  
					   SET YD_L2_REQUEST_STAT = :V_YD_L2_REQUEST_STAT
					     , MODIFIER           = :V_MODIFIER
					     , MOD_DDTT           = SYSDATE
					 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
					 */
					jrParam.setField("YD_L2_REQUEST_STAT", YmConstant.YD_L2_REQUEST_STAT_D);
					jrParam.setField("YD_CRN_SCH_ID"     , sYD_CRN_SCH_ID);
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdCrnSchProgStat", logId, methodNm, "작업대기상태 스케줄 취소(D) UPDATE");
					
				} else {
					
					JDTORecord inRecord = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
					inRecord.setField("YD_CRN_SCH_ID"	,sYD_CRN_SCH_ID);
					inRecord.setField("YD_SCH_CD"		,sYD_SCH_CD);
					inRecord.setField("YD_EQP_ID"		,sYD_EQP_ID);
					inRecord.setField("YD_WBOOK_ID"		,sYD_WBOOK_ID);
					inRecord.setField("IS_SCH_MTL"		,sIS_SCH_MTL);
					
					jrRtn = commUtils.addSndData(jrRtn, this.trtCrnSchCncl(inRecord));
					
					
				} // end if (autoFlag)
				
				
				/*******************************************************
				 * 스케줄 취소시 작업예약테이블에 스케줄취소 컬럼 UPDATE
				 *******************************************************/
				/*
				UPDATE TB_YM_WRKBOOK
				   SET SCH_CNCL_YN = 'Y'
				     , MODIFIER    = :V_MODIFIER
				     , MOD_DDTT    = SYSDATE
				 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
				 */
				jrParam.setField("YD_WBOOK_ID", sYD_WBOOK_ID);
				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.bcoilTcarDistCarldjl.updSchCnclYn", logId, methodNm, "TB_YM_WRKBOOK >> SCH_CNCL_YN");
				
				
				/**********************************************************
				* 스케줄 취소시 해당 스케줄 자동기동여부 N
				**********************************************************/
				if (!"RS".equals(sTRT_DTL_GP)) {
					/*
					UPDATE TB_YM_SCHEDULERULE
					   SET YD_SCH_AUTO_ST_YN = :V_YD_SCH_AUTO_ST_YN
					     , MODIFIER          = :V_MODIFIER
					     , MOD_DDTT          = SYSDATE
					 WHERE YD_SCH_CD         = :V_YD_SCH_CD
					 */
					jrParam.setField("YD_SCH_AUTO_ST_YN", "N"); // 'N' 스케줄 자동기동 금지
					jrParam.setField("YD_SCH_CD"        , sYD_SCH_CD);
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updSchAutoStYn", logId, methodNm, "TB_YM_SCHEDULERULE >> YD_SCH_AUTO_ST_YN");
				}
				
				/****************************
				 * 스케줄 재기동
				 ****************************/
				if ("RS".equals(sTRT_DTL_GP)) {
					
					commUtils.printLog(logId, "○○○ 스케줄 재기동", "[info]");
					JDTORecord jParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
					jParam.setResultCode(logId);	//Log ID
					jParam.setResultMsg(methodNm);	//Log Method Name

					jParam.setField("JMS_TC_CD", "YMYMJ302"); //야드작업예약ID
					jParam.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시				
					
					jParam.setField("YD_WBOOK_ID", sYD_WBOOK_ID);
					jParam.setField("YD_SCH_CD"  , ""); //야드스케쥴코드
					jParam.setField("YD_EQP_ID"  , ""); //야드설비ID
					
					//크레인스케줄기동 전문
					EJBConnector sndConnD = new EJBConnector("default", "BCoilSchSeEJB", this);
					JDTORecord jrRtnD = (JDTORecord)sndConnD.trx("procYMYMJ302", new Class[] { JDTORecord.class }, new Object[] { jParam });
					jrRtn = commUtils.addSndData(jrRtn, jrRtnD);
					
				} //end RS
			} // end for
			
			/**************************************************
			 * 무인일 경우 L2응답에 따라 스케줄 취소여부가 결정되므로 명령선택 기동않고 종료
			 *************************************************/
			if (mainFlag) {
				commUtils.printLog(logId, "자동화크레인 스케줄 취소 종료", "[info]");
				return jrRtn;
			}
			
			/**************************************************
			 * 명령선택 재기동
			 **************************************************/
			commUtils.printLog(logId, "설비상태 재조회 - 명령선택 조건 재설정", "[info]");
			/*
			SELECT *
			  FROM TB_YM_EQUIP    
			 WHERE EQUIP_GP = :V_EQUIP_GP
			   AND DEL_YN   = 'N'
			 */
			jrParam.setField("EQUIP_GP" , sYD_EQP_ID);
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYmEqp", logId, methodNm, "설비상태 조회");
			
			if (rsResult.size() > 0) {
				rsResult.first();
				jrEqpInfo   = rsResult.getRecord();
				szydEqpStat = jrEqpInfo.getFieldString("WPROG_STAT");          // 설비 상태
			}
			
			commUtils.printLog(logId, "스케줄취소시 설비가 offline, 고장이 아니고 대기 일때  명령 선택", "[info]");
			commUtils.printLog(logId,  szydEqpStat + sWORK_MODE + szydEqpStat, "[info]");
			if (!"B".equals(szydEqpStat) && !"2".equals(sWORK_MODE) && "W".equals(szydEqpStat)) { 

				commUtils.printLog(logId, "명령선택 기동", "[info]");
				/**********************************************************
				* 스케줄취소시 명령선택
				**********************************************************/
				//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
				JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, "A7YML007");

				jrYdMsg.setField("JMS_TC_CD"       , YmConstant.A7YML007);	//크레인작업지시요구
				jrYdMsg.setField("YD_EQP_ID"       , sYD_EQP_ID    );	//야드설비ID

				EJBConnector sndConn = new EJBConnector("default", "BCoilL2RcvSeEJB", this);
				JDTORecord jrRtn1 = (JDTORecord)sndConn.trx("rcvA7YML007", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
				
				jrRtn = commUtils.addSndData(jrRtn,jrRtn1);
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
	 *      [A] 오퍼레이션명 : 크레인작업요구현황조회 스케줄재전송
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord reSndCrnSch(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업요구현황조회 스케줄재전송[BCoilJspSeEJB.reSndCrnSch] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn = null;
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			JDTORecordSet rsResult = null;
			
			String sYD_CRN_SCH_ID = "";
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int i = 0; i < rowCnt; i++) {
		
				sYD_CRN_SCH_ID = commUtils.getValue(gdReq, "YD_CRN_SCH_ID", i);
				jrParam.setField("YD_CRN_SCH_ID" , sYD_CRN_SCH_ID); 

				/*
				 SELECT *
				   FROM TB_YM_CRNSCH
				  WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID 			
				 */
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYmCrnsch");
				
				if (rsResult.size() <= 0) {
					throw new Exception("해당 스케줄크레인스케줄 ID  정보: ["+ commUtils.getValue(gdReq, "YD_CRN_SCH_ID", i) + "] 가 존재하지않습니다");
				}
				
				/*****************************************************
				 **  크레인스케줄 재전송
				 *****************************************************/
				JDTORecord tcRecord = JDTORecordFactory.getInstance().create();
				tcRecord.setField("YD_CRN_SCH_ID"   , sYD_CRN_SCH_ID);
				tcRecord.setField("MSG_GP"          , "R");

				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA7L004", tcRecord));
				
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}// reSndCrnSch

	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업요구현황조회 - 권상권하처리 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updbtCrnStsSetPp(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리 상태 설정변경[BCoilJspSeEJB.updbtCrnStsSetPp] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			String trtDtlGp = commUtils.trim(gdReq.getParam("TRT_DTL_GP"));	//처리상세구분
			String currDate = commUtils.getDateTime14();						//현재시각
			String ydEqpId  = commUtils.trim(gdReq.getParam("W_YD_EQP_ID" ));	//야드설비ID(크레인)
			
			String sYD_CRN_ANSWER    = commUtils.trim(gdReq.getParam("YD_CRN_ANSWER" )); //작업실적응답
			String sYD_SCH_CD        = StringHelper.evl(commUtils.getValue(gdReq, "YD_SCH_CD"    , 0), ""); 
			String sYD_CRN_SCH_ID    = commUtils.nvl(commUtils.getValue(gdReq, "YD_CRN_SCH_ID", 0), "");
			
			if ("".equals(ydEqpId)) {
				throw new Exception("크레인설비ID가 없습니다.");
			}
			
			jrParam.setField("YD_EQP_ID"    , ydEqpId); //야드설비ID
			jrParam.setField("YD_CRN_SCH_ID", sYD_CRN_SCH_ID); // 크레인스케줄ID
			
			/********************************************
			 * 야드작업진행상태  JAVA단에서 한번 더 체크
			 *********************************************/
			/*
			 SELECT *
			   FROM TB_YM_CRNSCH
			  WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID 
			 */
			JDTORecordSet rstCrnSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYmCrnsch", logId, methodNm,  "야드작업진행상태 확인");
			String sYD_WRK_PROG_STAT = "";
			
			if (rstCrnSch == null || rstCrnSch.size() <= 0) {
				if ("WU".equals(trtDtlGp) || "WD".equals(trtDtlGp) || "DL".equals(trtDtlGp) || "XX".equals(trtDtlGp)) {
					throw new Exception("크레인스케줄이 없습니다.");
				}
			} else {
				sYD_WRK_PROG_STAT = rstCrnSch.getRecord(0).getFieldString("YD_WRK_PROG_STAT");
			}
			
			commUtils.printLog(logId, "야드작업진행상태 YD_WRK_PROG_STAT [ " + sYD_WRK_PROG_STAT + " ]", "[INFO]");
			
			
			if ("ST".equals(trtDtlGp)) {
				//설비상태 변경
				jrParam.setField("JMS_TC_CD"          , "A7YML004"); //설비고장복구실적
				jrParam.setField("YD_EQP_STAT"        , commUtils.trim(gdReq.getParam("YD_EQP_STAT"))); //야드설비상태(B:고장, N:정상)
				jrParam.setField("YD_EQP_PAUSE_CODE"  , "0000"    ); //야드설비휴지코드
				jrParam.setField("YD_EQP_TRBL_RCVR_DT", currDate  ); //야드설비고장복구일시

				EJBConnector sndConn = new EJBConnector("default", "BCoilL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvA7YML004", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				
			} else if ("MD".equals(trtDtlGp)) {
				//작업Mode 변경
				jrParam.setField("JMS_TC_CD"      , "A7YML003"); //설비운전모드전환
				jrParam.setField("YD_EQP_WRK_MODE", commUtils.trim(gdReq.getParam("YD_EQP_WRK_MODE"))); //야드설비작업Mode(1:On-Line, 0:Off-Line)

				EJBConnector sndConn = new EJBConnector("default", "BCoilL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvA7YML003", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				
			} else if ("WO".equals(trtDtlGp)) {
				//명령선택기동
				jrParam.setField("JMS_TC_CD"       , "A7YML007"); //크레인작업지시요구
				jrParam.setField("YD_WRK_PROG_STAT", "W"       ); //야드작업진행상태(명령선택대기)
				jrParam.setField("YD_SCH_CD"       , commUtils.trim(gdReq.getHeader("YD_SCH_CD"     ).getValue(0))); //야드스케쥴코드
				jrParam.setField("YD_CRN_SCH_ID"   , commUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID" ).getValue(0))); //야드크레인스케쥴ID

				EJBConnector sndConn = new EJBConnector("default", "BCoilL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvA7YML007", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			} else if ("WU".equals(trtDtlGp)) {
				if (!"1".equals(sYD_WRK_PROG_STAT)) {
					throw new Exception("["+sYD_WRK_PROG_STAT+"]해당 작업진행상태는 권상지시 상태가 아닙니다.");
				}
				//권상실적처리
				jrParam.setField("JMS_TC_CD"       , "A7YML008"); //크레인권상실적
				jrParam.setField("YD_EQP_WRK_MODE" , "9"       ); //야드설비작업Mode(Backup)
				jrParam.setField("YD_WRK_PROG_STAT", "2"       ); //야드작업진행상태(권상완료)
				jrParam.setField("YD_SCH_CD"       , commUtils.trim(gdReq.getHeader("YD_SCH_CD"         ).getValue(0))); //야드스케쥴코드
				jrParam.setField("YD_CRN_SCH_ID"   , commUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID"     ).getValue(0))); //야드크레인스케쥴ID
				jrParam.setField("YD_UP_WR_LOC"    , commUtils.trim(gdReq.getHeader("YD_UP_WO_LOC"      ).getValue(0))); //야드권상실적위치
				jrParam.setField("YD_UP_WR_LAYER"  , commUtils.trim(gdReq.getHeader("YD_UP_WO_LAYER"    ).getValue(0))); //야드권상실적단
				jrParam.setField("YD_CRN_XAXIS"    , commUtils.trim(gdReq.getHeader("YD_UP_WO_LOC_XAXIS").getValue(0))); //야드크레인X축
				jrParam.setField("YD_CRN_YAXIS"    , commUtils.trim(gdReq.getHeader("YD_UP_WO_LOC_YAXIS").getValue(0))); //야드크레인Y축
				jrParam.setField("YD_CRN_ZAXIS"    , commUtils.trim(gdReq.getHeader("YD_UP_WO_LOC_ZAXIS").getValue(0))); //야드크레인Z축
				
				EJBConnector sndConn = new EJBConnector("default", "BCoilL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvA7YML008", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			} else if ("WD".equals(trtDtlGp)) {
				if (!"2".equals(sYD_WRK_PROG_STAT)) {
					throw new Exception("["+sYD_WRK_PROG_STAT+"]해당 작업진행상태는 권상완료 상태가 아닙니다.");
				}
				//권하실적처리
				jrParam.setField("JMS_TC_CD"       , "A7YML009"); //크레인권하실적
				jrParam.setField("YD_EQP_WRK_MODE" , "9"       ); //야드설비작업Mode(Backup)
				jrParam.setField("YD_WRK_PROG_STAT", "4"       ); //야드작업진행상태(권하완료)
				jrParam.setField("YD_SCH_CD"       , commUtils.trim(gdReq.getHeader("YD_SCH_CD"         ).getValue(0))); //야드스케쥴코드
				jrParam.setField("YD_CRN_SCH_ID"   , commUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID"     ).getValue(0))); //야드크레인스케쥴ID
				jrParam.setField("YD_DN_WR_LOC"    , commUtils.trim(gdReq.getHeader("YD_DN_WO_LOC"      ).getValue(0))); //야드권하실적위치
				jrParam.setField("YD_DN_WR_LAYER"  , commUtils.trim(gdReq.getHeader("YD_DN_WO_LAYER"    ).getValue(0))); //야드권하실적단
				jrParam.setField("YD_CRN_XAXIS"    , commUtils.trim(gdReq.getHeader("YD_DN_WO_LOC_XAXIS").getValue(0))); //야드크레인X축
				jrParam.setField("YD_CRN_YAXIS"    , commUtils.trim(gdReq.getHeader("YD_DN_WO_LOC_YAXIS").getValue(0))); //야드크레인Y축
				jrParam.setField("YD_CRN_ZAXIS"    , commUtils.trim(gdReq.getHeader("YD_DN_WO_LOC_ZAXIS").getValue(0))); //야드크레인Z축
				EJBConnector sndConn = new EJBConnector("default", "BCoilL2RcvSeEJB", this);
				JDTORecord rst = (JDTORecord)sndConn.trx("rcvA7YML009", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				jrRtn = commUtils.addSndData(jrRtn, rst);
				
			} else if ("DL".equals(trtDtlGp)) {
				if ("4".equals(sYD_WRK_PROG_STAT)) {
					throw new Exception("["+sYD_WRK_PROG_STAT+"]해당 작업진행상태는 권하위치변경을 할 수 없습니다.");
				}
				//권하위치변경
				jrParam.setField("YD_WRK_PROG_STAT", commUtils.trim(gdReq.getHeader("YD_WRK_PROG_STAT").getValue(0))); //야드작업진행상태
				jrParam.setField("YD_SCH_CD"       , commUtils.trim(gdReq.getHeader("YD_SCH_CD"       ).getValue(0))); //야드스케쥴코드
				jrParam.setField("YD_CRN_SCH_ID"   , commUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID"   ).getValue(0))); //야드크레인스케쥴ID
				jrParam.setField("YD_WBOOK_ID"     , commUtils.trim(gdReq.getHeader("YD_WBOOK_ID"     ).getValue(0))); //야드작업예약ID
				jrParam.setField("YD_DN_WO_LOC"    , commUtils.trim(gdReq.getParam("YD_DN_WO_LOC"))); //야드권하지시위치(신규)

				jrRtn = this.updCrnSchDnWoLoc(jrParam);
				
			} else if ("WM".equals(trtDtlGp)) {
				//운전모드 변경
				jrParam.setField("JMS_TC_CD"      , "A7YML003"); //운전모드전환
				jrParam.setField("YD_EQP_WRK_MODE" , commUtils.trim(gdReq.getParam("YD_EQP_WRK_MODE"))); //야드설비작업Mode(1:On-Line, 2:Off-Line)
				jrParam.setField("YD_EQP_WRK_MODE2", commUtils.trim(gdReq.getParam("YD_EQP_WRK_MODE2"))); //

				EJBConnector sndConn = new EJBConnector("default", "BCoilL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvA7YML003", new Class[] { JDTORecord.class }, new Object[] { jrParam });

			} else if ("WR".equals(trtDtlGp)) {
				
				String sSTOCK_ID = commUtils.nvl(gdReq.getParam("STOCK_ID" ),"");//응답실적 코일번호
				
				if(sSTOCK_ID.length() < 12){//재료번호로 스케줄 조회
					if (!"".equals(sSTOCK_ID)) {
						jrParam.setField("STOCK_ID"    , sSTOCK_ID); 
						
						/*
		 				--com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmCrnStsSchStock 
							SELECT * FROM (
							  SELECT CR.YD_CRN_SCH_ID
							        ,CR.YD_WBOOK_ID
							        ,CR.YD_EQP_ID
							        ,CR.YD_GP
							        ,CR.YD_BAY_GP
							        ,CR.YD_SCH_CD
							        ,CR.YD_UP_WO_LOC
							        ,CR.YD_UP_WO_LAYER
							        ,CR.YD_DN_WO_LOC
							        ,CR.YD_DN_WO_LAYER
							        ,CR.YD_UP_WR_LOC
							        ,CR.YD_UP_WR_LAYER
							        ,CR.YD_DN_WR_LOC
							        ,CR.YD_DN_WR_LAYER
							        ,CM.STOCK_ID
							        ,CM.YD_AID_WRK_YN
							        ,CM.YD_TO_LOC_DCSN_MTD
							   FROM TB_YM_CRNSCH CR, TB_YM_CRNWRKMTL CM
							  WHERE CR.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
							    AND CR.YD_EQP_ID = :V_YD_EQP_ID
							    AND CM.STOCK_ID = :V_STOCK_ID
							  ORDER BY CR.YD_CRN_SCH_ID DESC
							  ) A
							  WHERE ROWNUM = 1
						 */
						JDTORecordSet wrCrnSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmCrnStsSchStock", logId, methodNm,  "실적응답대상 스케줄 조회");
						
						if (wrCrnSch == null || wrCrnSch.size() <= 0) {
							
							throw new Exception("해당재료의 스케줄 실적이 없습니다.");
							
						} else {
							sYD_CRN_SCH_ID = wrCrnSch.getRecord(0).getFieldString("YD_CRN_SCH_ID");
							sYD_SCH_CD	   = wrCrnSch.getRecord(0).getFieldString("YD_SCH_CD");
							ydEqpId	       = wrCrnSch.getRecord(0).getFieldString("YD_EQP_ID");
						}
					}
				}else{//스케줄ID로 스케줄 정보 조회
					jrParam.setField("YD_CRN_SCH_ID"    , sSTOCK_ID);
					/*
						-- com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCrnSchLocLog
						SELECT YD_CRN_SCH_ID
						     , YD_DN_WO_LOC_TO
						     , STL_NO_TEMP
						     , STK_LYR_NO_TEMP
						     , YD_DN_WO_LOC
						     , YD_DN_WO_LAYER
						     , YD_WBOOK_ID
						     , YD_WRK_PROG_STAT
						     , YD_SCH_CD
						     , YD_EQP_ID
						     , YD_UP_WR_LOC
						     , YD_L2_REQUEST_STAT
						     , YD_SCH_PRIOR
						     ,YD_TO_LOC_DCSN_MTD
						  FROM TB_YM_CRNSCH
						 WHERE YD_EQP_ID = :V_YD_EQP_ID
						   AND YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
					 */
					JDTORecordSet wrCrnSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCrnSchLocLog", logId, methodNm,  "실적응답대상 스케줄 조회");
					
					if (wrCrnSch == null || wrCrnSch.size() <= 0) {
						
						throw new Exception("해당재료의 스케줄 실적이 없습니다.");
						
					} else {
						sYD_CRN_SCH_ID = wrCrnSch.getRecord(0).getFieldString("YD_CRN_SCH_ID");
						sYD_SCH_CD	   = wrCrnSch.getRecord(0).getFieldString("YD_SCH_CD");
						ydEqpId	       = wrCrnSch.getRecord(0).getFieldString("YD_EQP_ID");
					}
					
				}
				

				//작업실적응답
				if (YmConstant.CRN_WRK_RE_LD_WR.equals(sYD_CRN_ANSWER)) { //권상
					jrParam.setResultCode(logId);	//Log ID
					jrParam.setResultMsg(methodNm);	//Log Method Name
					jrParam.setField("JMS_TC_CD"       , "YMA7L005"); //작업실적응답
					jrParam.setField("YD_EQP_ID"       , ydEqpId); //야드설비ID
					jrParam.setField("YD_WRK_PROG_STAT", YmConstant.YD_EQP_STAT_UP_CMPL);
					jrParam.setField("YD_SCH_CD"       , sYD_SCH_CD);
					jrParam.setField("YD_CRN_SCH_ID"   , sYD_CRN_SCH_ID);
					jrParam.setField("YD_L2_WR_GP"     , YmConstant.CRN_WRK_RE_LD_WR); //야드L2실적구분(지시요구)
					jrParam.setField("YD_L3_HD_RS_CD"  , YmConstant.CRN_WRK_RE_CD_NORMAL_HD); //야드L3처리결과코드(Error)
					jrParam.setField("YD_L3_MSG"       , "작업실적응답" ); //야드L3MESSAGE

					jrRtn = commUtils.addSndData(jrRtn, ymComm.getYMA7L005(jrParam));
				} else {
					jrParam.setResultCode(logId);	//Log ID
					jrParam.setResultMsg(methodNm);	//Log Method Name
					jrParam.setField("JMS_TC_CD"       , "YMA7L005"); //작업실적응답
					jrParam.setField("YD_EQP_ID"       , ydEqpId); //야드설비ID
					jrParam.setField("YD_WRK_PROG_STAT", YmConstant.YD_EQP_STAT_DN_CMPL);
					jrParam.setField("YD_SCH_CD"       , sYD_SCH_CD);
					jrParam.setField("YD_CRN_SCH_ID"   , sYD_CRN_SCH_ID);
					jrParam.setField("YD_L2_WR_GP"     , YmConstant.CRN_WRK_RE_DN_WR    ); //야드L2실적구분(지시요구)
					jrParam.setField("YD_L3_HD_RS_CD"  , YmConstant.CRN_WRK_RE_CD_NORMAL_HD); //야드L3처리결과코드(Error)
					jrParam.setField("YD_L3_MSG"       , "작업실적응답" ); //야드L3MESSAGE

					jrRtn = commUtils.addSndData(jrRtn, ymComm.getYMA7L005(jrParam));
				}
				
			} else if ("XX".equals(trtDtlGp)) {
				if (!"S".equals(sYD_WRK_PROG_STAT)) {
					throw new Exception("["+sYD_WRK_PROG_STAT+"]해당 작업진행상태는 응답대기 상태가 아닙니다.");
				}
				//응답 백업(개발용)
				jrParam.setField("MSG_ID"	        , "A7YML015" );
				jrParam.setField("MSG_GP"		    , "I" );
				jrParam.setField("YD_EQP_ID"		, ydEqpId );
				jrParam.setField("YD_WRK_PROG_STAT"	, "1" );
				jrParam.setField("YD_SCH_CD"		, sYD_SCH_CD );
				jrParam.setField("YD_CRN_SCH_ID"	, sYD_CRN_SCH_ID );
				jrParam.setField("REQ_YN"	        , "Y" );
				
				EJBConnector ejbConn = new EJBConnector("default", "BCoilL2RcvSeEJB", this);
				jrRtn = (JDTORecord)ejbConn.trx("rcvA7YML015", new Class[] { JDTORecord.class }, new Object[] { jrParam });
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
	 * 개소코드 변경
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public GridData updWlocCd(GridData gdReq) throws DAOException {
		String methodNm = "개소코드 변경[BCoilJspSeEJB.updWlocCd] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			//Grid date 를 JDTORecord data 로 변환
			JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
			
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			
			commDao.update(inRecord, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.bcoilCarPntUnitClInqjl.updWlocCd");
			
			//UI로 반환 할 Grid data 를 생성 
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			GridData gdRet = commUtils.jdtoRecordToGridData(gdRtn, outRecSet.toList(), gdReq);
			
			commUtils.printLog(logId, methodNm, "S-", gdReq);
			
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of updWlocCd		
	
	
	/**
	 * 사용여부 변경
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecord updPtat(GridData gdReq) throws DAOException {
		String methodNm = "사용여부 변경[BCoilJspSeEJB.updPtat] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		JDTORecord sndRecord = JDTORecordFactory.getInstance().create();
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
			
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			
			commDao.update(inRecord, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.bcoilCarPntUnitClInqjl.updPtat");
			commDao.update(inRecord, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.bcoilCarPntUnitClInqjl.updCarPointStat");
			
			JDTORecord jDrd = JDTORecordFactory.getInstance().create();
			
			jDrd.setField("JMS_TC_CD", "YDTSJ012");
			jDrd.setField("JMS_TC_CREATE_DDTT", 	commUtils.getDateTime14());
			jDrd.setField("PRSNT_LOC_WLOC_CD", 		inRecord.getFieldString("WLOC_CD"));
			jDrd.setField("YD_PNT_CD", 				inRecord.getFieldString("YD_PNT_CD2"));
			if (inRecord.getFieldString("CAR_CARD_NO").equals("Y")){ //사용가능
				jDrd.setField("PNT_UNIT_CL_GP", 		"O");
			}else if (inRecord.getFieldString("CAR_CARD_NO").equals("N")){ //사용불가
				jDrd.setField("PNT_UNIT_CL_GP", 		"C");
			}
			jDrd.setField("YD_PNT_OP_CL_TT", 		commUtils.getDateTime14());
			
			sndRecord = commUtils.addSndData(sndRecord,jDrd);		
			
			commUtils.printLog(logId, methodNm, "S-", gdReq);
			
			return sndRecord;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of updPtat	
	
	
	/**
	 * 적치기준 변경
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public GridData updStackRuleInfo(GridData gdReq) throws DAOException {
		String methodNm = "적치기준 변경[BCoilJspSeEJB.updStackRuleInfo] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			//Grid date 를 JDTORecord data 로 변환
			JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
			
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			
			commDao.update(inRecord, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.bcoilStkPrfrPriorjl.updStackRuleInfo");
			
			//UI로 반환 할 Grid data 를 생성 
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			GridData gdRet = commUtils.jdtoRecordToGridData(gdRtn, outRecSet.toList(), gdReq);
			
			commUtils.printLog(logId, methodNm, "S-", gdReq);
			
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of updWlocCd
	
	
	/**
	 * 폭, 외경기준 수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public GridData updateStackRuleInfo(GridData gdReq) throws DAOException {
		String methodNm = "폭, 외경기준 변경[BCoilJspSeEJB.updateStackRuleInfo] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			//Grid date 를 JDTORecord data 로 변환
			JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
			
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			
			inRecord.setField("SCH_CD", inRecord.getFieldString("P_WIDTH"));
			inRecord.setField("SCH_RULE_VAL", "1");
			
			commDao.update(inRecord, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.bcoilStkPrfrPriorjl.updStackRuleInfo_02"); //폭기준 수정
			
			inRecord.setField("SCH_CD", inRecord.getFieldString("P_LENGTH"));
			inRecord.setField("SCH_RULE_VAL", "2");
			
			commDao.update(inRecord, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.bcoilStkPrfrPriorjl.updStackRuleInfo_02"); //외경기준 수정
			
			//UI로 반환 할 Grid data 를 생성 
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			GridData gdRet = commUtils.jdtoRecordToGridData(gdRtn, outRecSet.toList(), gdReq);
			
			commUtils.printLog(logId, methodNm, "S-", gdReq);
			
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of updateStackRuleInfo
	
	
	/**
	 * 야드및설비 열정보수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecord updCoilYdStkPosSet(GridData gdReq) throws DAOException {
		String methodNm = "야드및설비 열정보수정[BCoilJspSeEJB.updCoilYdStkPosSet] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		JDTORecord sndRecord = JDTORecordFactory.getInstance().create();
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
			
			inRecord.setField("MODIFIER"	            	,commUtils.trim(gdReq.getParam("userid")));
			inRecord.setField("STACK_COL_GP"	        	,commUtils.getValue(gdReq, "STACK_COL_GP", 0));
			/*
			inRecord.setField("STACK_BED_ACTIVE_STAT"	    ,commUtils.getValue(gdReq, "STACK_COL_ACTIVE_STAT", 0));
			inRecord.setField("STACK_LAYER_ACTIVE_STAT"	    ,commUtils.getValue(gdReq, "STACK_COL_ACTIVE_STAT", 0));
			inRecord.setField("STACK_COL_ACTIVE_STAT"	    ,commUtils.getValue(gdReq, "STACK_COL_ACTIVE_STAT", 0));
			inRecord.setField("STACK_BED_X_AXIS"	        ,commUtils.getValue(gdReq, "STACK_COL_RULE_X_AXIS", 0));
			inRecord.setField("STACK_LAYER_X_AXIS"	        ,commUtils.getValue(gdReq, "STACK_COL_RULE_X_AXIS", 0));
			inRecord.setField("STACK_COL_RULE_X_AXIS"	    ,commUtils.getValue(gdReq, "STACK_COL_RULE_X_AXIS", 0));
			
			inRecord.setField("STACK_BED_Y_AXIS"	        ,commUtils.getValue(gdReq, "STACK_COL_RULE_Y_AXIS", 0));
			inRecord.setField("STACK_LAYER_Y_AXIS"	        ,commUtils.getValue(gdReq, "STACK_COL_RULE_Y_AXIS", 0));
			inRecord.setField("STACK_COL_RULE_Y_AXIS"	    ,commUtils.getValue(gdReq, "STACK_COL_RULE_Y_AXIS", 0));
			
			inRecord.setField("STACK_BED_Z_AXIS"	        ,commUtils.getValue(gdReq, "STACK_COL_RULE_Z_AXIS", 0));
			inRecord.setField("STACK_LAYER_Z_AXIS"	        ,commUtils.getValue(gdReq, "STACK_COL_RULE_Z_AXIS", 0));
			inRecord.setField("STACK_COL_RULE_Z_AXIS"	    ,commUtils.getValue(gdReq, "STACK_COL_RULE_Z_AXIS", 0));
			
			*/
			inRecord.setField("ROTATION_ANGLE"	            ,commUtils.getValue(gdReq, "ROTATION_ANGLE", 0));
			
			
//			//적치 베드 정보의  UPDATE
//			/*
//			UPDATE TB_YM_STACKER
//			      SET STACK_BED_ACTIVE_STAT = :V_STACK_BED_ACTIVE_STAT
//			       , MOD_DDTT = SYSDATE
//			       , MODIFIER  = :V_MODIFIER  
//			 WHERE STACK_COL_GP = :V_STACK_COL_GP
//			 */
//			commDao.update(inRecord, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.bcoilYdNEqpXyaxisSet.updYmStkbedYmStrActStat");
//			
//			//TB_YD_STKLYR 적치 베드 정보의  UPDATE
//			/*
//			UPDATE TB_YM_STACKLAYER            
//			   SET STACK_LAYER_ACTIVE_STAT = :V_STACK_LAYER_ACTIVE_STAT
//			       , MOD_DDTT = SYSDATE
//			       , MODIFIER  = :V_MODIFIER  
//			WHERE STACK_COL_GP  = :V_STACK_COL_GP
//			 */
//			commDao.update(inRecord, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.bcoilYdNEqpXyaxisSet.updYmStklyrColActStat");
//			
//			//TB_YD_STKBED 적치 베드 정보의  UPDATE
//			/*
//			UPDATE TB_YM_STACKER
//			   SET STACK_BED_X_AXIS = :V_STACK_BED_X_AXIS
//			     , STACK_BED_Y_AXIS = :V_STACK_BED_Y_AXIS
//			     , STACK_BED_Z_AXIS = :V_STACK_BED_Z_AXIS
//			     , MOD_DDTT   = SYSDATE
//			     , MODIFIER   = :V_MODIFIER  
//			 WHERE STACK_COL_GP = :V_STACK_COL_GP
//			 */
//			commDao.update(inRecord, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.bcoilYdNEqpXyaxisSet.updYmStkbedYmStrX");
//
//			//야드적치단 좌표만  UPDATE
//			/*
//			UPDATE TB_YM_STACKLAYER            
//			   SET STACK_LAYER_X_AXIS = :V_STACK_LAYER_X_AXIS
//			     , STACK_LAYER_Y_AXIS = :V_STACK_LAYER_Y_AXIS
//			     , STACK_LAYER_Z_AXIS = :V_STACK_LAYER_Z_AXIS
//			     , MOD_DDTT  = SYSDATE
//			     , MODIFIER  = :V_MODIFIER  
//			 WHERE STACK_COL_GP  = :V_STACK_COL_GP
//			 */
//			commDao.update(inRecord, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.bcoilYdNEqpXyaxisSet.updYmStklyrX");
//			
//			//적치열 정보 UPDATE
//			/*
//			UPDATE TB_YM_STACKCOL
//			   SET STACK_COL_ACTIVE_STAT = :V_STACK_COL_ACTIVE_STAT  
//				 , STACK_COL_RULE_X_AXIS = :V_STACK_COL_RULE_X_AXIS
//				 , STACK_COL_RULE_Y_AXIS = :V_STACK_COL_RULE_Y_AXIS
//				 , STACK_COL_RULE_Z_AXIS = :V_STACK_COL_RULE_Z_AXIS     
//			     , MOD_DDTT              = SYSDATE             
//				 , MODIFIER              = :V_MODIFIER             
//			     , ROTATION_ANGLE        = :V_ROTATION_ANGLE
//			 WHERE STACK_COL_GP = :V_STACK_COL_GP
//			 */
//			commDao.update(inRecord, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.bcoilYdNEqpXyaxisSet.updYmStkcol");
//			
			
			/* 적치열 Rotation 변경
			com.inisteel.cim.ym.bcoil.dao.BcoilDAO.bcoilYdNEqpXyaxisSet.updYmStkcolRotation
			 
			UPDATE TB_YM_STACKCOL
			   SET MOD_DDTT              = SYSDATE             
				 , MODIFIER              = :V_MODIFIER             
			     , ROTATION_ANGLE        = :V_ROTATION_ANGLE
			 WHERE STACK_COL_GP = :V_STACK_COL_GP
			*/
			commDao.update(inRecord, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.bcoilYdNEqpXyaxisSet.updYmStkcolRotation", logId, methodNm, "적치열 Rotation 변경");
			 
			JDTORecord jDrd = JDTORecordFactory.getInstance().create();
			
			jDrd.setField("MSG_ID"				, "YMA7L001");
			jDrd.setField("DATE"				, commUtils.getDate10());
			jDrd.setField("TIME"				, commUtils.getTime8());
			jDrd.setField("MSG_GP"				, "");
			jDrd.setField("MSG_LEN"				, "0089");
			jDrd.setField("YD_INFO_SYNC_CD"		, "3");						//1:동,2:SPAN,3:열,4:BED
			jDrd.setField("YD_GP"				, inRecord.getFieldString("STACK_COL_GP").substring(0, 1));
			jDrd.setField("COL_GP"				, inRecord.getFieldString("STACK_COL_GP").substring(5, 6));
			jDrd.setField("STACK_COL_GP"		, inRecord.getFieldString("STACK_COL_GP"));

			sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2("YMA7L001", jDrd));
			
			commUtils.printLog(logId, methodNm, "S-", gdReq);
			
			return sndRecord;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of updCoilYdStkPosSet	
	
	
	/**
	 * 야드및설비 베드정보수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecord updCoilYdStkPosSetBed(GridData gdReq) throws DAOException {
		String methodNm = "야드및설비 베드정보수정[BCoilJspSeEJB.updCoilYdStkPosSetBed] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		JDTORecord sndRecord = JDTORecordFactory.getInstance().create();
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
			
			JDTORecord jDrd = JDTORecordFactory.getInstance().create();
			
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				inRecord.setField("MODIFIER"	            	,commUtils.trim(gdReq.getParam("userid")));
				inRecord.setField("STACK_LAYER_ACTIVE_STAT"	    ,commUtils.getValue(gdReq, "STACK_LAYER_ACTIVE_STAT", ii));
				inRecord.setField("STACK_LAYER_X_AXIS"	        ,commUtils.getValue(gdReq, "STACK_LAYER_X_AXIS", ii));
				inRecord.setField("STACK_LAYER_Y_AXIS"	        ,commUtils.getValue(gdReq, "STACK_LAYER_Y_AXIS", ii));
				inRecord.setField("STACK_LAYER_Z_AXIS"			,commUtils.getValue(gdReq, "STACK_LAYER_Z_AXIS", ii));
				inRecord.setField("STACK_COL_GP"	        	,commUtils.getValue(gdReq, "STACK_COL_GP", ii));
				inRecord.setField("STACK_BED_GP"	        	,commUtils.getValue(gdReq, "STACK_BED_GP", ii));
				inRecord.setField("STACK_LAYER_GP"	        	,commUtils.getValue(gdReq, "STACK_LAYER_GP", ii));
				inRecord.setField("YD_STK_BED_XAXIS_TOL"	    ,commUtils.getValue(gdReq, "YD_STK_BED_XAXIS_TOL", ii));
				inRecord.setField("YD_STK_BED_YAXIS_TOL"	    ,commUtils.getValue(gdReq, "YD_STK_BED_YAXIS_TOL", ii));
				inRecord.setField("YD_STK_BED_ZAXIS_TOL"	    ,commUtils.getValue(gdReq, "YD_STK_BED_ZAXIS_TOL", ii));
				
				//적치 단 정보의  UPDATE 
				/*
				UPDATE TB_YM_STACKLAYER
				   SET MODIFIER = :V_MODIFIER
				     , MOD_DDTT = SYSDATE
				     , STACK_LAYER_ACTIVE_STAT = :V_STACK_LAYER_ACTIVE_STAT
				     , STACK_LAYER_X_AXIS = :V_STACK_LAYER_X_AXIS
				     , STACK_LAYER_Y_AXIS = :V_STACK_LAYER_Y_AXIS
				     , STACK_LAYER_Z_AXIS =  NVL(:V_STACK_LAYER_Z_AXIS,STACK_LAYER_Z_AXIS)
				 WHERE STACK_COL_GP = :V_STACK_COL_GP
				   AND STACK_BED_GP = :V_STACK_BED_GP
				   AND STACK_LAYER_GP = :V_STACK_LAYER_GP
				 */
				commDao.update(inRecord, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updCoilYdStkPosSetBed.updYdStklyrDan");
				
				//적치 베드 정보의  UPDATE 
				/*
				UPDATE TB_YM_STACKER
				   SET MODIFIER = :V_MODIFIER
				     , MOD_DDTT = SYSDATE
				     , YD_STK_BED_XAXIS_TOL  = :V_YD_STK_BED_XAXIS_TOL
				     , YD_STK_BED_YAXIS_TOL  = :V_YD_STK_BED_YAXIS_TOL
				     , YD_STK_BED_ZAXIS_TOL  = NVL(:V_YD_STK_BED_ZAXIS_TOL,YD_STK_BED_ZAXIS_TOL)
				 WHERE STACK_COL_GP = :V_STACK_COL_GP
				   AND STACK_BED_GP = :V_STACK_BED_GP
				 */
				commDao.update(inRecord, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updCoilYdStkPosSetBed.updYdStklyrTol");
			
				jDrd.setField("MSG_ID"				, "YMA7L001");
				jDrd.setField("DATE"				, commUtils.getDate10());
				jDrd.setField("TIME"				, commUtils.getTime8());
				jDrd.setField("MSG_GP"				, "");
				jDrd.setField("MSG_LEN"				, "0089");
				jDrd.setField("YD_INFO_SYNC_CD"		, "4");						//1:동,2:SPAN,3:열,4:BED
				jDrd.setField("YD_GP"				, "3");
				jDrd.setField("STACK_BED_GP"		, commUtils.getValue(gdReq, "STACK_BED_GP", ii));
				jDrd.setField("STACK_COL_GP"		, commUtils.getValue(gdReq, "STACK_COL_GP", ii));
				
				sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2("YMA7L001", jDrd));
			}
			
			commUtils.printLog(logId, methodNm, "S-", gdReq);
			
			return sndRecord;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of updCoilYdStkPosSetBed	
	
	
	/**
	 * 산적위치수정 - 저장품 생성
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord insStockInfo(JDTORecord rcvMsg) throws DAOException {
		
		String methodNm = "산적위치수정-저장품생성[BCoilJspSeEJB.insStockInfo] <" + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+");
			
			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

			String sYD_GP       = rcvMsg.getFieldString("YD_GP");
			String sSTOCK_ID    = rcvMsg.getFieldString("STOCK_ID");

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(rcvMsg.getFieldString("userid")));
			jrParam.setField("YD_GP"	 , sYD_GP   );
			jrParam.setField("STOCK_ID"  , sSTOCK_ID);		
			
			/*
			MERGE INTO TB_YM_STOCK ST USING (
			    SELECT :V_STOCK_ID      AS STOCK_ID    --재료번호
			         , :V_MODIFIER      AS MODIFIER    --수정자
			         , SYSDATE          AS MOD_DDTT    --수정일시
			         , 'N'              AS DEL_YN      --삭제유무
			      FROM DUAL
			) DD ON ( ST.STOCK_ID = DD.STOCK_ID)
			WHEN NOT MATCHED THEN
			    INSERT (
			           STOCK_ID     , STOCK_ITEM    , STOCK_MOVE_TERM  --저장품이동조건
			         , REGISTER     , REG_DDTT      , MODIFIER         -- 'SYSTEM'
			         , MOD_DDTT     , DEL_YN )
			    VALUES (:V_STOCK_ID , ''            , ''
			         , DD.MOD_DDTT  , DD.MOD_DDTT   , DD.MOD_DDTT
			         , DD.MOD_DDTT  , DD.DEL_YN 	) 
			WHEN MATCHED THEN UPDATE SET
			     ST.STOCK_ITEM      = ''
			   , ST.STOCK_MOVE_TERM = ''
			   , ST.MODIFIER        = DD.MODIFIER
			   , ST.MOD_DDTT        = DD.MOD_DDTT
			   , ST.DEL_YN          = DD.DEL_YN 						
			 */
			commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.insertStockTransInfo", logId, methodNm, "저장품 생성");
			
			commUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of insStockInfo	
	
	
	/**
	 * 산적위치수정 - 수정
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord updStkLoc(GridData gdReq) throws DAOException {
		
		String methodNm = "산적위치수정-수정[BCoilJspSeEJB.updStkLoc] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		JDTORecordSet dmRc = null;
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			String sMODIFIER = StringHelper.evl(commUtils.trim(gdReq.getParam("userid")), "SYSTEM");
			
			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

			String sYD_GP       = gdReq.getParam("YD_GP");
			String sYD_BAY_GP   = gdReq.getParam("YD_BAY_GP");

			String sSTOCK_ID    = gdReq.getParam("STOCK_ID");
			String sFROM_ADDR   = gdReq.getParam("FROM_ADDR");  //from 위치
			String sYD_STR_LOC  = gdReq.getParam("YD_STR_LOC"); //TO 위치
			String sFTMV_BKUP   = ""; //gdReq.getParam("FTMV_BKUP");  //이송유무
			String sT_CAR_HOLD  = gdReq.getParam("T_CAR_HOLD");	//대차출하 보류
			
			String sYD_SECT_GP = StringHelper.evl(gdReq.getParam("YD_SECT_GP"), sYD_STR_LOC.substring(2,  4));
			String sYD_COL_GP  = StringHelper.evl(gdReq.getParam("YD_COL_GP") , sYD_STR_LOC.substring(4,  6));
			String sYD_BED_GP  = StringHelper.evl(gdReq.getParam("YD_BED_GP") , sYD_STR_LOC.substring(6,  8));
			String sYD_LYR_GP  = StringHelper.evl(gdReq.getParam("YD_LYR_GP") , sYD_STR_LOC.substring(8, 10));
			
			String sYD_STACK_COL_GP = sYD_STR_LOC.substring(0,  6);
			
			String sDEL_YN           = gdReq.getParam("DEL_YN"          );
			String sYD_STOCK_YN      = gdReq.getParam("YD_STOCK_YN"     );     
			String sYD_CRN_SCH_ID_YN = gdReq.getParam("YD_CRN_SCH_ID_YN");
			String sYD_WRKBOOK_YN    = gdReq.getParam("YD_WRKBOOK_YN"   );
			String sYD_CARSCH_YN     = gdReq.getParam("YD_CARSCH_YN"    );
			String sYD_TCARSCH_YN    = gdReq.getParam("YD_TCARSCH_YN"   );   
			
			String sTO_YD_GP3 = commUtils.nvl(gdReq.getParam("TO_YD_GP3"), "N");//타야드 여부

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setField("YD_GP"	 , sYD_GP   );
			jrParam.setField("STOCK_ID"  , sSTOCK_ID);
			

			/************************
			 * 저장품 생성
			 ************************/
			if ("N".equals(sYD_STOCK_YN)) {
				EJBConnector ejbConnS = new EJBConnector("default", "BCoilJspSeEJB", this);
				jrRtn = (JDTORecord)ejbConnS.trx("insStockInfo", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			}
			
			/**************************************************
			 * FROM위치와 TO위치가 같은 경우 종료 
			 * (저장품이 사라졌을 경우 생성후 종료)
			 **************************************************/
			if (sFROM_ADDR.equals(sYD_STR_LOC)) {
				return jrRtn;
			}
			
			/*
			SELECT STOCK_ID
			     , STACK_LAYER_STAT
			     , STACK_LAYER_ACTIVE_STAT
			     , STACK_LAYER_COMMENTS
			  FROM TB_YM_STACKLAYER
			 WHERE STACK_COL_GP  	= :V_STACK_COL_GP
			   AND STACK_BED_GP		= :V_STACK_BED_GP 
			   AND STACK_LAYER_GP	= :V_STACK_LAYER_GP 
			 */
			jrParam.setField("STACK_COL_GP"   	,sYD_STACK_COL_GP);
			jrParam.setField("STACK_BED_GP"    	,sYD_BED_GP);
			jrParam.setField("STACK_LAYER_GP"  	,sYD_LYR_GP);
			JDTORecordSet jsLyrInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getStackLayerInfo", logId, methodNm, "신규저장위치 조회");
			
			if (jsLyrInfo.size() == 0) {
				throw new Exception("산적위치 이상 [" + sYD_STACK_COL_GP + sYD_BED_GP + sYD_LYR_GP + "]");
			}
			
			/************************************************
			 *  COIL 산적위치 수정
			 ************************************************/
			JDTORecord jparam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jparam.setResultCode(logId);	//Log ID
			jparam.setResultMsg(methodNm);	//Log Method Name
			jparam.setField("STOCK_ID"  , sSTOCK_ID);  //저장품
			jparam.setField("FROM_ADDR" , sFROM_ADDR); //from위치
			jparam.setField("YD_STR_LOC", sYD_STR_LOC);//to위치
			jparam.setField("FTMV_BKUP" , sFTMV_BKUP); //이송백업여부
			jparam.setField("TO_YD_GP3" , sTO_YD_GP3); //타야드 이적 여부
			jparam.setField("T_CAR_HOLD" , sT_CAR_HOLD); //대차출하 보류

			// COIL 산적위치 수정 메소드(트랜젝션 분리 작업)
			commUtils.printLog(logId, "COIL 산적위치 수정 START", "[INFO]+");
			
			EJBConnector ejbConn1 = new EJBConnector("default", "BCoilJspSeEJB", this);
			jrRtn = (JDTORecord)ejbConn1.trx("changeCoilLocationInfo", new Class[] { JDTORecord.class }, new Object[] { jparam });
			
			commUtils.printLog(logId, "COIL 산적위치 수정 END", "[INFO]-");
			
			
			/************************************************
			 * 이송백업 START
			 ************************************************/
			/*
			SELECT CASE WHEN COUNT(*) > 0 THEN 'Y' ELSE 'N' END AS IS_FTMV_BKUP
			  FROM USRPTA.TB_PT_STLFRTOMOVE A
			 WHERE FRTOMOVE_STAT_CD = '3'
			   AND ARR_WLOC_CD ||'' IN ('D3Y41','D3Y42')
			   AND SPOS_WLOC_CD||'' NOT IN ('D3Y41','D3Y42')
			   AND TRANSWORD_SEQNO = (SELECT MAX(TRANSWORD_SEQNO)
			                            FROM USRPTA.TB_PT_STLFRTOMOVE B
			                           WHERE B.STL_NO = A.STL_NO
			                             AND ROWNUM <= 1)
			   AND A.STL_NO = :V_STOCK_ID
			 */
			jrParam.setField("STOCK_ID"  , sSTOCK_ID);
			JDTORecord rs = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getIsFtmvBkup").getRecord(0);
			sFTMV_BKUP = rs.getFieldString("IS_FTMV_BKUP");

			
			commUtils.printLog(logId, "○○○이송백업 실적처리 START", "[INFO]+");
			commUtils.printLog(logId, "○○○sFTMV_BKUP = "+sFTMV_BKUP, "[INFO]+");
			if ("Y".equals(sFTMV_BKUP)) { 
				
				JDTORecord tcRecord = JDTORecordFactory.getInstance().create(); 
	     
			    /*********************
			     * 실적BACKUP처리 CALL
			     *********************/
				//코일공통 업데이트
				/*
				UPDATE TB_PT_COILCOMM
				SET( 
				    CURR_PROG_CD_REG_PGM,   -- 현재진도코드 PGM
				    CURR_PROG_REG_DDTT,     -- 현재진도코드등록일시
				    CURR_PROG_CD,           -- 현재진도코드
				    BEFO_PROG_CD_REG_PGM,   -- 전 진도코드 PGM
				    BEFO_PROG_REG_DDTT,     -- 전 진도코드등록일시
				    BEFO_PROG_CD,           -- 전 진도코드
				    BEFOBEFO_PROG_CD_REG_PGM,
				    BEFOBEFO_PROG_REG_DDTT,
				    BEFOBEFO_PROG_CD
				   )=
				   (
				    SELECT 'ydcallStartLastWo'
				         , SYSDATE
				         , DECODE(A.STL_APPEAR_GP, 'Y', A.CURR_PROG_CD,(
				              CASE 
				                WHEN B.TO_CURR_PROG_CD IS NOT NULL AND A.CURR_PROG_CD='E' THEN TO_CURR_PROG_CD
				                WHEN A.ORD_YEOJAE_GP = '1'  AND A.CURR_PROG_CD='E' THEN 'B' 
				                WHEN A.ORD_YEOJAE_GP <>'1'  AND A.CURR_PROG_CD='E' THEN 'Y'
				                ELSE A.CURR_PROG_CD
				              END)) CURR_PROG_CD,
				        A.CURR_PROG_CD_REG_PGM,
				        A.CURR_PROG_REG_DDTT,
				        A.CURR_PROG_CD,   
				        A.BEFO_PROG_CD_REG_PGM,
				        A.BEFO_PROG_REG_DDTT,
				        A.BEFO_PROG_CD
				    FROM  TB_PT_COILCOMM A
				         ,(SELECT *
				             FROM USRPTA.TB_PT_STLFRTOMOVE AA
				           WHERE AA.TRANSWORD_SEQNO = (SELECT MAX(TRANSWORD_SEQNO) 
				                                         FROM TB_PT_STLFRTOMOVE C
				                                        WHERE C.STL_NO=AA.STL_NO )
				           )B
				    WHERE A.COIL_NO = B.STL_NO(+)
				      AND A.COIL_NO = :V_COIL_NO
				    )
				 WHERE COIL_NO = :V_COIL_NO
				 */
			    tcRecord.setField("COIL_NO", sSTOCK_ID);
			    
				//Coil공통 테이블 업데이트
			    commUtils.printLog(logId, "□□□TB_PT_COILCOMM UPDATE START", "[INFO]+");
			    
			    EJBConnector ejbConnPT = new EJBConnector("default", "BCoilL2RcvSeEJB", this);
				ejbConnPT.trx("UpdCoilComProg", new Class[] { JDTORecord.class }, new Object[] { tcRecord });
				
				commUtils.printLog(logId, "□□□TB_PT_COILCOMM UPDATE START", "[INFO]+");
				
				
				/***********************************************
				 * 저장품제원 : 코일야드L2로 송신(YMA7L002)
				 ***********************************************/
				commUtils.printLog(logId, "YMA7L002 JMS전송", "[INFO]");
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setField("TC_CD"          , "YMA7L002");
				jrYdMsg.setField("YD_INFO_SYNC_CD", "5");
				jrYdMsg.setField("STOCK_ID"       , sSTOCK_ID);
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA7L002", jrYdMsg));

				
				/*************************************************
				 * 저장품 이동조건 업데이트 
				 *************************************************/
			    JDTORecord rVal = this.getCoilCurrProgCd(sSTOCK_ID);
			    String sSTOCK_MOVE_TERM  = rVal.getFieldString("STOCK_MOVE_TERM");
			    /*
				UPDATE TB_YM_STOCK
				   SET STOCK_MOVE_TERM = :V_STOCK_MOVE_TERM
				     , MOD_DDTT        = SYSDATE
				     , MODIFIER        = :V_MODIFIER
				     , DEL_YN          = 'N'
				 WHERE STOCK_ID = :V_STOCK_ID
			     */
			    tcRecord.setField("STOCK_MOVE_TERM", sSTOCK_MOVE_TERM);
			    tcRecord.setField("MODIFIER"       , sMODIFIER);
			    tcRecord.setField("T_CAR_HOLD" 	   , sT_CAR_HOLD); //대차출하 보류
			    tcRecord.setField("STOCK_ID"       , sSTOCK_ID);
			    commDao.update(tcRecord, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStock", logId, methodNm, "저장품 이동조건");
			    
			    
			    /***********************************************
			     * YDPTJ002 코일소재 이송완료실적BACKUP처리
			     ***********************************************/
			    tcRecord.setField("COIL_NO", sSTOCK_ID);
			    JDTORecord stlRecord = commDao.select(tcRecord, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCOILCOMM").getRecord(0);
			    String sSTL_APPEAR_GP =StringHelper.evl(stlRecord.getFieldString("STL_APPEAR_GP"), "");
			    
			    if (!"Y".equals(sSTL_APPEAR_GP)) {
					
			    	/*
					UPDATE TB_PT_STLFRTOMOVE
					   SET FRTOMOVE_DONE_DATE =  SYSDATE
					     , FTMV_HDS_DD = TO_CHAR(SYSDATE - (6/24),'YYYYMMDD')
					     , FRTOMOVE_STAT_CD = '*'
					     , MODIFIER = 'SYSTEM'
					     , MOD_DDTT =  SYSDATE
					 WHERE STL_NO = :V_STL_NO
					   AND FRTOMOVE_STAT_CD <> '*'  --이미 실적처리가 된 경우
					   AND TRANSWORD_SEQNO = (SELECT MAX(TRANSWORD_SEQNO)
					                            FROM TB_PT_STLFRTOMOVE
					                           WHERE STL_NO = :V_STL_NO
					                             AND FRTOMOVE_STAT_CD NOT IN ('Z','C')
					                          )
			    	 */
					jparam.setField("STL_NO", sSTOCK_ID);
			    	
			    	//TB_PT_STLFRTOMOVE 테이블 업데이트
				    EJBConnector ejbConnPT2 = new EJBConnector("default", "YmCommSeEJB", this);
					ejbConnPT2.trx("updProcStlFrToMove", new Class[] { JDTORecord.class }, new Object[] { jparam });
					
				    //코일소재 이송완료실적(YDPTJ002)
					JDTORecord tcRecord2 = JDTORecordFactory.getInstance().create();

					tcRecord2.setField("JMS_TC_CD"         , "YDPTJ002");
					tcRecord2.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14());
					
				    tcRecord2.setField("STL_NO"             , StringHelper.evl(stlRecord.getFieldString("COIL_NO"), ""));
				    tcRecord2.setField("ORD_NO"             , StringHelper.evl(stlRecord.getFieldString("ORD_NO"), ""));// 주문번호
				    tcRecord2.setField("ORD_DTL"            , StringHelper.evl(stlRecord.getFieldString("ORD_DTL"), ""));// 주문행번
				    tcRecord2.setField("PLNT_PROC_CD"       , StringHelper.evl(stlRecord.getFieldString("PLNT_PROC_CD"), ""));// 공장공정코드
				    tcRecord2.setField("STL_APPEAR_GP"      , StringHelper.evl(stlRecord.getFieldString("STL_APPEAR_GP"), ""));// 재료외형구분
				    tcRecord2.setField("CURR_PROG_CD"       , StringHelper.evl(stlRecord.getFieldString("CURR_PROG_CD"), ""));// 현재진도코드
				    tcRecord2.setField("ORD_YEOJAE_GP"      , StringHelper.evl(stlRecord.getFieldString("ORD_YEOJAE_GP"), ""));// 주문여재구분
				    tcRecord2.setField("STL_WT"             , StringHelper.evl(stlRecord.getFieldString("COIL_WT"), ""));// 재료중량 (SLAB중량)
			    	tcRecord2.setField("DS_MTL_WT"          , "");// 설계재료중량
				    tcRecord2.setField("MTL_STAT_GP"        , StringHelper.evl(stlRecord.getFieldString("RECORD_PROG_STAT"), ""));// 재료상태구분
				    tcRecord2.setField("RECORD_END_GP"      , StringHelper.evl(stlRecord.getFieldString("RECORD_END_GP"), ""));// Record 종료구분
				    tcRecord2.setField("RECORD_END_GP1"     , "");//Record 종료구분 1
				    tcRecord2.setField("BEFO_PROG_CD"       , StringHelper.evl(stlRecord.getFieldString("BEFO_PROG_CD"), ""));//전진도 코드
				    tcRecord2.setField("BEF_ORD_NO"         , StringHelper.evl(stlRecord.getFieldString("BEF_ORD_NO"), ""));// 전주문 번호
				    tcRecord2.setField("BEF_ORD_DTL"        , StringHelper.evl(stlRecord.getFieldString("BEF_ORD_DTL"), ""));// 전주문 행번
				    tcRecord2.setField("MMATL_FEE_NO"       , StringHelper.evl(stlRecord.getFieldString("MMATL_FEE_NO"), ""));// 모재료번호
				    tcRecord2.setField("ORDERTRANS_MATCH_GP", StringHelper.evl(stlRecord.getFieldString("MATCH_ORDERTRANS_GP"), ""));// 목전충당구분	
				
				    //내부인터페이스 송신모듈 호출 
					jrRtn = commUtils.addSndData(jrRtn, tcRecord2);	
				    
				    commUtils.printLog(logId, "내부IF호출=YDPTJ002 코일소재 이송완료실적BACKUP처리", "[INFO]");
				}
			} // end if ("Y".equals(sFTMV_BKUP))
			commUtils.printLog(logId, "○○○이송백업 실적처리 END", "[INFO]-");
			
			
			/*********************************************
			 * YDDMR001(일관제철 코일입고작업실적) 송신
			 *********************************************/
			/*
			WITH PARAM AS (
			SELECT :V_STOCK_ID AS STOCK_ID 
			  FROM DUAL
			)
			SELECT A.STACK_COL_GP||A.STACK_BED_GP||A.STACK_LAYER_GP AS PUT_POSITION
			     , D.CURR_PROG_CD
			  FROM TB_YM_STACKLAYER A
				 , TB_YM_STACKCOL   B
			     , TB_YM_STOCK      C
			     , TB_PT_COILCOMM   D
			 WHERE A.STOCK_ID     = (SELECT STOCK_ID FROM PARAM)   
			   AND A.STACK_COL_GP = B.STACK_COL_GP
			   AND A.STOCK_ID     = C.STOCK_ID
			   AND A.STOCK_ID     = D.COIL_NO
			   AND D.CURR_PROG_CD IN ('2','H') --강관진도포함               
			   AND B.SECT_GP BETWEEN '00' AND '99'
			   
			 UNION 
			 
			SELECT YD_STR_LOC AS PUT_POSITION 
			     , CURR_PROG_CD
			  FROM TB_PT_COILCOMM D
			 WHERE COIL_NO LIKE 'R%' 
			   AND D.CURR_PROG_CD = '3' ----구입코일
			   AND COIL_NO = (SELECT STOCK_ID FROM PARAM)
			 
			 UNION
			
			SELECT A.STACK_COL_GP||A.STACK_BED_GP||A.STACK_LAYER_GP AS PUT_POSITION
			     , D.CURR_PROG_CD
			  FROM TB_YM_STACKLAYER A
			     , TB_YM_STACKCOL   B
			     , TB_YM_STOCK      C
			     , TB_PT_COILCOMM   D
			 WHERE A.STOCK_ID    		= (SELECT STOCK_ID FROM PARAM) 
			   AND A.STACK_LAYER_STAT 	IN('L', 'C')
			   AND A.STACK_COL_GP 		= B.STACK_COL_GP
			   AND A.STOCK_ID           = C.STOCK_ID
			   AND A.STOCK_ID           = D.COIL_NO
			   AND B.STACK_COL_USAGE_CD = 'CX' -- 대차
			   AND C.STOCK_MOVE_TERM    = 'L1' -- 대차출하완료
			 */
			jrParam.setField("STOCK_ID", sSTOCK_ID);
			dmRc = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYMDM001Info");

			
			if (dmRc.size() > 0) {

				String sPut_Position = StringHelper.evl(dmRc.getRecord(0).getFieldString("PUT_POSITION"), "");
				String sCURR_PROG_CD = StringHelper.evl(dmRc.getRecord(0).getFieldString("CURR_PROG_CD"), "");
			
				String sYardGp = sPut_Position.substring(0, 1);
				commUtils.printLog(logId, "내부IF호출 : YDDMR001(일관제철 코일입고작업실적)","[INFO]");
                //코일입고작업실적
				JDTORecord tcRecordDM = JDTORecordFactory.getInstance().create();
				
				// PIDEV
//				String sApplyYnPI = commDao.ApplyYnPI("", "BCoilJspSeEJBSBean => 코일입고작업실적", "APPPI0", "*", "*");
				
//				if("Y".equals(sApplyYnPI)) {
					tcRecordDM.setField("MQ_TC_CD"           	, "M10YDLMJ1011"			);
					tcRecordDM.setField("MQ_TC_CREATE_DDTT"     , commUtils.getDateTime14()	);
					tcRecordDM.setField("YD_GP"      			, sYardGp					);
					tcRecordDM.setField("DIST_GOODS_GP"    		, "H"						);
					tcRecordDM.setField("YARD_GP"    			, ""						);					
					tcRecordDM.setField("GOODS_NO"    			, sSTOCK_ID					);
					tcRecordDM.setField("STORE_LOC_CD"   		, sPut_Position				);					
					tcRecordDM.setField("RECEIPT_DATE"      	, commUtils.getDate8()		);
					tcRecordDM.setField("RECEIPT_TIME"      	, commUtils.getTime6()		);					
					tcRecordDM.setField("CURR_PROG_CD"			, sCURR_PROG_CD				);		
//				} else {
//					tcRecordDM.setField("JMS_TC_CD"         , "YDDMR001");
//					tcRecordDM.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14());
//					tcRecordDM.setField("TC_CODE"           , "YDDMR001");
//					tcRecordDM.setField("TC_CREATE_DDTT"    , commUtils.getDateTime14());
//					tcRecordDM.setField("RECEIPT_DATE"      , commUtils.getDate8());
//					tcRecordDM.setField("RECEIPT_TIME"      , commUtils.getTime6());
//					tcRecordDM.setField("GOODS_NO"    , sSTOCK_ID);
//					tcRecordDM.setField("YD_GP"       , sYardGp);
//					tcRecordDM.setField("STORE_LOC"   , sPut_Position);
//					tcRecordDM.setField("CURR_PROG_CD", sCURR_PROG_CD);
//				}
				
				//인터페이스 전문 호출
		        jrRtn = commUtils.addSndData(jrRtn, tcRecordDM);		
			}
				
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updStkLoc
	
	
	
	/**
	 * 산적위치수정 - 전문백업
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return JDTORecord
	 * @throws DAOException
	 */ 

	public JDTORecord updStkLocBackUp(GridData gdReq) throws DAOException {
		
		String methodNm = "산적위치수정-수정[BCoilJspSeEJB.updStkLoc] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			jrParam.setField("YD_GP"	 , gdReq.getParam("YD_GP"));
			String sSTOCK_ID  = commUtils.getValue(gdReq, "STOCK_ID", 0); //재료번호
			String sINPUT_DATA1 = gdReq.getParam("PARA_INPUT_DATA1");	//설비ID
			
			String sSND_FLAG  = gdReq.getParam("SND_FLAG");	// 백업종류
			
			/******************************
			 *** 저장품
			 ******************************/
			String sFLAG   = ""; //신규 or 수정
			String sDEL_YN = "";
			if ("STOCK".equals(sSND_FLAG)) {
				jrParam.setField("STOCK_ID", sSTOCK_ID);
				/*
				SELECT ST.*
				     ,(SELECT WB.YD_WBOOK_ID 
				          FROM TB_YM_WRKBOOK    WB
				             , TB_YM_WRKBOOKMTL WM
				         WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
				           AND WM.STOCK_ID = ST.STOCK_ID
				           AND WB.DEL_YN = 'N'
				           AND WM.DEL_YN = 'N'     
				      ) AS YD_WBOOK_ID
				  FROM TB_YM_STOCK ST
				 WHERE ST.STOCK_ID = :V_STOCK_ID
				 */
				JDTORecordSet rst = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getStockInfoByPk");
				if (rst.size() <= 0) {
					sFLAG = "I";
				} else {
					sDEL_YN = rst.getRecord(0).getFieldString("DEL_YN");
					if ("Y".equals(sDEL_YN)) {
						sFLAG = "U";
					} else {
						throw new Exception("해당 재료는 저장품에 있습니다.");
					}
				}
			
				if ("I".equals(sFLAG)) {
					/*
					INSERT INTO USRYMA.TB_YM_STOCK
					(
					  STOCK_ID
					, STOCK_ITEM 
					, STOCK_STAT 
					, STOCK_MOVE_TERM 
					, REGISTER 
					, REG_DDTT 
					, DEL_YN
					)
					SELECT COIL_NO 
					     , (CASE WHEN STL_APPEAR_GP='Y' THEN 'CG' ELSE 'CM' END)  --재료회형구분 Y(제품)이면 CG(coil제품) 아니면CM(coil소재)
					     , '2'  --정정실적 처리
					     , 'EC' --이송작업대기
					     , :V_MODIFIER
					     , SYSDATE
					     ,'N'
					  FROM USRPTA.TB_PT_COILCOMM
					 WHERE COIL_NO = :V_COIL_NO
					 */
					jrParam.setField("COIL_NO", sSTOCK_ID);
					commDao.insert(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.insertStock");
				} else if ("U".equals(sFLAG)) {
					/*
					UPDATE TB_YM_STOCK
					   SET DEL_YN   = :V_DEL_YN
					     , MODIFIER = :V_MODIFIER
					     , MOD_DDTT = SYSDATE           
					 WHERE STOCK_ID = :V_STOCK_ID
					 */
					jrParam.setField("DEL_YN"  , "N");
					jrParam.setField("STOCK_ID", sSTOCK_ID);
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateStockDelYnInfo");
				}
				
				//======================================================
				// 저장품제원 : 코일야드L2로 송신(YMA7L002)
				//======================================================
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setField("TC_CD"          , "YMA7L002");
				jrYdMsg.setField("MSG_GP"         , "I");
				jrYdMsg.setField("YD_INFO_SYNC_CD", "5");
				jrYdMsg.setField("STOCK_ID"       , sSTOCK_ID);
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA7L002", jrYdMsg));
			}
			
			/******************************
			 *** 보급
			 ******************************/
			if ("LINEIN".equals(sSND_FLAG)) {
				
				JDTORecord jrYdMsgLINEIN = JDTORecordFactory.getInstance().create();

				//열연조업 코일보급 및 보급취소
				jrYdMsgLINEIN.setField("YD_DN_WR_LOC", sINPUT_DATA1);
				jrYdMsgLINEIN.setField("STOCK_ID"    , sSTOCK_ID);
				
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYMPOJ161BackUp 
				SELECT 'YMPOJ161'                                       AS JMS_TC_CD          --JMSTC코드  
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')              AS JMS_TC_CREATE_DDTT --JMSTC생성일시  
				     , 'YMPOJ161'                                       AS TCCODE
				     , substr(TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'),1,8)  AS TCDATE
				     , substr(TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'),9,6)  AS TCTIME
				     , 'B'                                              AS PLANTGBN  
				     , SUBSTR(A.TRK_INFO,1,1)                           AS PROCGBN            --S:1SPM,N:2SPM,H:HFL 
				     , A.STOCK_ID                                       AS COILNO  
				     , SUBSTR(A.TRK_INFO,2,1)                           AS PROCESSID  
				     , TO_CHAR(SYSDATE,'YYYYMMDD')                      AS DOWNDATE  
				     , TO_CHAR(SYSDATE,'HH24MISS')                      AS DOWNTIME  
				     , 'D'||SUBSTR(A.TRK_INFO,2,1)                      AS POSITIONNO  
				  FROM (SELECT CASE WHEN SUBSTR(LOC,3,2) IN ('KE') AND SUBSTR(YD_SCH_CD,2,6) IN ('DKE01U','EKE01U') THEN 'N1'  --2SPM 보급존 
				                    WHEN SUBSTR(LOC,3,2) IN ('KE') AND SUBSTR(YD_SCH_CD,3,5) IN ('KE01U'          ) THEN 'S1'  --1SPM 보급존 
				                    WHEN SUBSTR(LOC,3,2) IN ('KE') AND SUBSTR(YD_SCH_CD,3,6) IN ('DKE03U','EKE03U') THEN 'N5'  --2SPM TAKE IN 
				                    WHEN SUBSTR(LOC,3,2) IN ('KE') AND SUBSTR(YD_SCH_CD,3,5) IN ('KE03U'          ) THEN 'S5'  --1SPM TAKE IN 
				                    WHEN SUBSTR(LOC,3,2) IN ('FE') AND SUBSTR(YD_SCH_CD,3,5) IN ('FE01U'          ) THEN 'H1'  --HFL  보급존 
				                    WHEN SUBSTR(LOC,3,2) IN ('FE') AND SUBSTR(YD_SCH_CD,3,5) IN ('FE03U'          ) THEN 'H5'  --HFL  TAKE IN 
				                    WHEN SUBSTR(LOC,3,2) IN ('FE')                                                  THEN 'F1'  --2SPM내 HFL보급 
				                    ELSE '' END AS TRK_INFO 
				            , STOCK_ID 
				         FROM (SELECT :V_YD_DN_WR_LOC  AS LOC  
				                    , STOCK_ID         AS STOCK_ID
				                    , (SELECT YD_SCH_CD FROM TB_YM_CRNSCH WHERE YD_CRN_SCH_ID = C.YD_CRN_SCH_ID) AS YD_SCH_CD
				                 FROM TB_YM_CRNWRKMTL C 
				                WHERE C.YD_CRN_SCH_ID = (
				                                        SELECT MAX(CM.YD_CRN_SCH_ID) AS YD_CRN_SCH_ID
				                                          FROM TB_YM_CRNWRKMTL CM
				                                             , TB_YM_CRNSCH    CR
				                                         WHERE CM.YD_CRN_SCH_ID = CR.YD_CRN_SCH_ID
				                                           AND CM.STOCK_ID = :V_STOCK_ID
				                                        )
				                
				              ) 
				       ) A                   	
				*/

    			JDTORecordSet jsYMPOJ161 = commDao.select(jrYdMsgLINEIN, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYMPOJ161BackUp", logId, methodNm, "설비위 권하처리 조회 "); 
				if ( jsYMPOJ161.size() <= 0 ) {
					commUtils.printLog(logId, methodNm+  "보급실적 송신:" + sSTOCK_ID, "SL");
				} else {
				
					JDTORecord jrYMPOJ161 = JDTORecordFactory.getInstance().create();
					
					jsYMPOJ161.first();
					jrYMPOJ161.setRecord(jsYMPOJ161.getRecord());
					JDTORecord jrSnd = JDTORecordFactory.getInstance().create();
					jrSnd.setField("JMS_TC_CD"			, commUtils.trim(jrYMPOJ161.getFieldString("JMS_TC_CD")));
					jrSnd.setField("JMS_TC_CREATE_DDTT"	, commUtils.trim(jrYMPOJ161.getFieldString("JMS_TC_CREATE_DDTT")));
					jrSnd.setField("tcCode"				, commUtils.trim(jrYMPOJ161.getFieldString("TCCODE")));
					jrSnd.setField("tcDate"				, commUtils.trim(jrYMPOJ161.getFieldString("TCDATE")));
					jrSnd.setField("tcTime"				, commUtils.trim(jrYMPOJ161.getFieldString("TCTIME")));
					jrSnd.setField("plantGbn"			, commUtils.trim(jrYMPOJ161.getFieldString("PLANTGBN")));
					jrSnd.setField("procGbn"			, commUtils.trim(jrYMPOJ161.getFieldString("PROCGBN")));
					jrSnd.setField("coilNo"				, commUtils.trim(jrYMPOJ161.getFieldString("COILNO")));
					jrSnd.setField("processId"			, commUtils.trim(jrYMPOJ161.getFieldString("PROCESSID")));
					jrSnd.setField("downDate"			, commUtils.trim(jrYMPOJ161.getFieldString("DOWNDATE")));
					jrSnd.setField("downTime"			, commUtils.trim(jrYMPOJ161.getFieldString("DOWNTIME")));
					jrSnd.setField("positionNo"			, commUtils.trim(jrYMPOJ161.getFieldString("POSITIONNO")));
					jrRtn = commUtils.addSndData(jrRtn, jrSnd);			
					
				}						
				
				
				//품질L3열연정정입측보급실적
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YDQMJ002B" , jrYdMsgLINEIN));
				
			} //end if
			
			/******************************
			 *** 추출
			 ******************************/
			if ("LINEOFF".equals(sSND_FLAG)) {
				
			}
			
			/******************************
			 *** TAKE-IN
			 ******************************/
			if (sSND_FLAG.equals("TAKEIN")) {
			
				JDTORecord jrYdMsgTAKEIN = JDTORecordFactory.getInstance().create();

				//열연조업 코일보급 및 보급취소
				jrYdMsgTAKEIN.setField("YD_DN_WR_LOC", sINPUT_DATA1);
				jrYdMsgTAKEIN.setField("STOCK_ID"    , sSTOCK_ID);
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYMPOJ161BackUp 
				SELECT 'YMPOJ161'                                       AS JMS_TC_CD          --JMSTC코드  
				     , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')              AS JMS_TC_CREATE_DDTT --JMSTC생성일시  
				     , 'YMPOJ161'                                       AS TCCODE
				     , substr(TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'),1,8)  AS TCDATE
				     , substr(TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'),9,6)  AS TCTIME
				     , 'B'                                              AS PLANTGBN  
				     , SUBSTR(A.TRK_INFO,1,1)                           AS PROCGBN            --S:1SPM,N:2SPM,H:HFL 
				     , A.STOCK_ID                                       AS COILNO  
				     , SUBSTR(A.TRK_INFO,2,1)                           AS PROCESSID  
				     , TO_CHAR(SYSDATE,'YYYYMMDD')                      AS DOWNDATE  
				     , TO_CHAR(SYSDATE,'HH24MISS')                      AS DOWNTIME  
				     , 'D'||SUBSTR(A.TRK_INFO,2,1)                      AS POSITIONNO  
				  FROM (SELECT CASE WHEN SUBSTR(LOC,3,2) IN ('KE') AND SUBSTR(YD_SCH_CD,2,6) IN ('DKE01U','EKE01U') THEN 'N1'  --2SPM 보급존 
				                    WHEN SUBSTR(LOC,3,2) IN ('KE') AND SUBSTR(YD_SCH_CD,3,5) IN ('KE01U'          ) THEN 'S1'  --1SPM 보급존 
				                    WHEN SUBSTR(LOC,3,2) IN ('KE') AND SUBSTR(YD_SCH_CD,3,6) IN ('DKE03U','EKE03U') THEN 'N5'  --2SPM TAKE IN 
				                    WHEN SUBSTR(LOC,3,2) IN ('KE') AND SUBSTR(YD_SCH_CD,3,5) IN ('KE03U'          ) THEN 'S5'  --1SPM TAKE IN 
				                    WHEN SUBSTR(LOC,3,2) IN ('FE') AND SUBSTR(YD_SCH_CD,3,5) IN ('FE01U'          ) THEN 'H1'  --HFL  보급존 
				                    WHEN SUBSTR(LOC,3,2) IN ('FE') AND SUBSTR(YD_SCH_CD,3,5) IN ('FE03U'          ) THEN 'H5'  --HFL  TAKE IN 
				                    WHEN SUBSTR(LOC,3,2) IN ('FE')                                                  THEN 'F1'  --2SPM내 HFL보급 
				                    ELSE '' END AS TRK_INFO 
				            , STOCK_ID 
				         FROM (SELECT :V_YD_DN_WR_LOC  AS LOC  
				                    , STOCK_ID         AS STOCK_ID
				                    , (SELECT YD_SCH_CD FROM TB_YM_CRNSCH WHERE YD_CRN_SCH_ID = C.YD_CRN_SCH_ID) AS YD_SCH_CD
				                 FROM TB_YM_CRNWRKMTL C 
				                WHERE C.YD_CRN_SCH_ID = (
				                                        SELECT MAX(CM.YD_CRN_SCH_ID) AS YD_CRN_SCH_ID
				                                          FROM TB_YM_CRNWRKMTL CM
				                                             , TB_YM_CRNSCH    CR
				                                         WHERE CM.YD_CRN_SCH_ID = CR.YD_CRN_SCH_ID
				                                           AND CM.STOCK_ID = :V_STOCK_ID
				                                        )
				                
				              ) 
				       ) A                   	
				*/

    			JDTORecordSet jsYMPOJ161 = commDao.select(jrYdMsgTAKEIN, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.TcYMPOJ161BackUp", logId, methodNm, "설비위 권하처리 조회 "); 
				if ( jsYMPOJ161.size() <= 0 ) {
					commUtils.printLog(logId, methodNm+  "보급실적 송신:" + sSTOCK_ID, "SL");
				} else {
				
					JDTORecord jrYMPOJ161 = JDTORecordFactory.getInstance().create();
					
					jsYMPOJ161.first();
					jrYMPOJ161.setRecord(jsYMPOJ161.getRecord());
					JDTORecord jrSnd = JDTORecordFactory.getInstance().create();
					jrSnd.setField("JMS_TC_CD"			, commUtils.trim(jrYMPOJ161.getFieldString("JMS_TC_CD")));
					jrSnd.setField("JMS_TC_CREATE_DDTT"	, commUtils.trim(jrYMPOJ161.getFieldString("JMS_TC_CREATE_DDTT")));
					jrSnd.setField("tcCode"				, commUtils.trim(jrYMPOJ161.getFieldString("TCCODE")));
					jrSnd.setField("tcDate"				, commUtils.trim(jrYMPOJ161.getFieldString("TCDATE")));
					jrSnd.setField("tcTime"				, commUtils.trim(jrYMPOJ161.getFieldString("TCTIME")));
					jrSnd.setField("plantGbn"			, commUtils.trim(jrYMPOJ161.getFieldString("PLANTGBN")));
					jrSnd.setField("procGbn"			, commUtils.trim(jrYMPOJ161.getFieldString("PROCGBN")));
					jrSnd.setField("coilNo"				, commUtils.trim(jrYMPOJ161.getFieldString("COILNO")));
					jrSnd.setField("processId"			, commUtils.trim(jrYMPOJ161.getFieldString("PROCESSID")));
					jrSnd.setField("downDate"			, commUtils.trim(jrYMPOJ161.getFieldString("DOWNDATE")));
					jrSnd.setField("downTime"			, commUtils.trim(jrYMPOJ161.getFieldString("DOWNTIME")));
					jrSnd.setField("positionNo"			, commUtils.trim(jrYMPOJ161.getFieldString("POSITIONNO")));
					jrRtn = commUtils.addSndData(jrRtn, jrSnd);			
					
				}						
				
				//품질L3열연정정입측보급실적
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YDQMJ002B" , jrYdMsgTAKEIN));				
			}
			
			/******************************
			 *** TAKE-OUT
			 ******************************/
			if (sSND_FLAG.equals("TAKEOUT")) { 
				
			}
			
			/******************************
			 *** 수입LINE-OFF
			 ******************************/
			if (sSND_FLAG.equals("H2LINE")) { 
				
				JDTORecord jrYdMsgH2LINE = JDTORecordFactory.getInstance().create();
				
				//압연L2LINE OFF실적송신
				jrYdMsgH2LINE.setField("STOCK_ID", sSTOCK_ID);
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("CF1BP04B", jrYdMsgH2LINE));
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updStkLocBackUp
	
	
	/**
	 * 오퍼레이션명 : COIL 산적위치 수정 메소드
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws Exception 
	 * @throws 
	 * @ejb.transaction type="RequiresNew"
	 */                    									 
	public JDTORecord changeCoilLocationInfo(JDTORecord jparam) throws Exception {
		
		String methodNm = "산적위치수정[BCoilJspSeEJB.changeCoilLocationInfo] < " + jparam.getResultMsg();
		String logId    = jparam.getResultCode();
		
		String sSTOCK_ID  = jparam.getFieldString("STOCK_ID"); 
		String sUpLoc     = jparam.getFieldString("FROM_ADDR"); // FROM위치
		String sPutLoc    = jparam.getFieldString("YD_STR_LOC");// TO위치
		String sMODIFIER  = commUtils.nvl(jparam.getFieldString("MODIFIER"), "updStkLoc");
		String sTCarHold  = jparam.getFieldString("T_CAR_HOLD");//대차출하 보류
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		
		try {

			String sYD_WBOOK_ID = "";
			String sDEL_YN      = "";
			String sCurBayGp = "";

			String sPutStackColGp = "";
			String sPutStackBedGp = "";
			String sPutStackLayerGp = "";

			/**
			 * 저장품이동조건
			 */
			JDTORecord jrStockInfo  = this.getCoilCurrProgCd(sSTOCK_ID);
			String sProgCd          = jrStockInfo.getFieldString("CURR_PROG_CD");
			String sSTOCK_MOVE_TERM = jrStockInfo.getFieldString("STOCK_MOVE_TERM");

			String sUpYardGp  = "";
			String sPutYardGp = "";

			if (sUpLoc.length() > 1) {
				sUpYardGp = sUpLoc.substring(0, 1); //from 야드구분
			}

			if (sPutLoc.length() > 1) {
				sPutYardGp = sPutLoc.substring(0, 1); //to 야드구분
			}

			JDTORecord jrParam = commUtils.getParam(logId, methodNm, sMODIFIER);
			
			/**********************************************
			 * 0. 입력한 To 위치 정합성 점검  
			 **********************************************/
			if (sPutLoc.length() == 10) {
				sPutStackColGp   = sPutLoc.substring(0, 6);
				sPutStackBedGp   = sPutLoc.substring(6, 8);
				sPutStackLayerGp = sPutLoc.substring(8, 10);
			}

			if (sUpLoc.length() == 11) {
				sUpLoc = sUpLoc.substring(0, 8) + sUpLoc.substring(9, 11);
			}
			
			/*
			SELECT *
			  FROM TB_YM_STACKLAYER
			 WHERE STACK_COL_GP  	= :V_STACK_COL_GP
			   AND STACK_BED_GP		= :V_STACK_BED_GP 
			   AND STACK_LAYER_GP	= :V_STACK_LAYER_GP
		 	*/
			jrParam.setField("STACK_COL_GP"  , sPutStackColGp);
			jrParam.setField("STACK_BED_GP"  , sPutStackBedGp);
			jrParam.setField("STACK_LAYER_GP", sPutStackLayerGp);
		 	
			JDTORecordSet jsPutLocInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getStackLayerInfoWithPk", logId, methodNm, "산적위치 조회");
			
			if (jsPutLocInfo.size() <= 0) {
				throw new Exception("산적위치 수정=> To위치정보가 잘못 입력되었습니다.");
			}

			if (YmConstant.STACK_LAYER_GP_01.equals(sPutStackLayerGp)
			 || YmConstant.STACK_LAYER_GP_02.equals(sPutStackLayerGp)) {
				
			} else {
				throw new Exception("산적위치 수정=> 적치단(01단/02단) 정보가 잘못 입력되었습니다.");
			}
			
			
			/********************************
			 * 1. 작업예약 유무 체크
			 ********************************/
			/*
			SELECT ST.*
			     ,(SELECT WB.YD_WBOOK_ID 
			          FROM TB_YM_WRKBOOK    WB
			             , TB_YM_WRKBOOKMTL WM
			         WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
			           AND WM.STOCK_ID = ST.STOCK_ID
			           AND WB.DEL_YN = 'N'
			           AND WM.DEL_YN = 'N'     
			      ) AS YD_WBOOK_ID
			  FROM TB_YM_STOCK ST
			 WHERE ST.STOCK_ID = :V_STOCK_ID
			 */
			jrParam.setField("STOCK_ID", sSTOCK_ID);
			JDTORecordSet stockV = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getStockInfoByPk", logId, methodNm, "");
			
			if (stockV.size() > 0) {
				sYD_WBOOK_ID = StringHelper.evl(stockV.getRecord(0).getFieldString("YD_WBOOK_ID"), "");
				sDEL_YN      = StringHelper.evl(stockV.getRecord(0).getFieldString("DEL_YN"), "");
			} else {
//				throw new Exception("산적위치 수정=> 저장품정보가 존재하지 않습니다.");
			}
			
			commUtils.printLog(logId, "산적위치 수정=> 작업예약ID = " + sYD_WBOOK_ID, "[INFO]");
			commUtils.printLog(logId, "산적위치 수정=> 삭제유무   = " + sDEL_YN     , "[INFO]");
			
			if (!"".equals(sYD_WBOOK_ID)) {
				
				/* 작업예약 재료 위치 수정 */
				jrParam.setField("STOCK_ID"   , sSTOCK_ID);
				jrParam.setField("YD_WBOOK_ID", sYD_WBOOK_ID);
				/*
				MERGE INTO TB_YM_WRKBOOKMTL WM USING (
				 SELECT YD_WBOOK_ID
				       ,STOCK_ID
				       ,:V_MODIFIER AS MODIFIER
				       ,SYSDATE     AS MOD_DDTT
				       ,STACK_COL_GP
				       ,STACK_BED_GP
				       ,STACK_LAYER_GP
				       ,YD_UP_COLL_SEQ 
				   FROM (SELECT WM.*
				           FROM (SELECT WB.YD_WBOOK_ID
				                       ,WB.YD_SCH_CD
				                       ,WM.STOCK_ID
				                       ,SL.STACK_COL_GP
				                       ,SL.STACK_BED_GP
				                       ,SL.STACK_LAYER_GP
				                       ,SL.STACK_COL_GP||SL.STACK_BED_GP||SL.STACK_LAYER_GP AS YD_STR_LOC
				                       ,SL.STACK_COL_GP||SL.STACK_BED_GP AS YD_STK_COL_BED
				                       ,RANK() OVER(PARTITION BY SL.STACK_COL_GP,SL.STACK_BED_GP
				                                        ORDER BY SL.STACK_COL_GP,SL.STACK_BED_GP,SL.STACK_LAYER_GP) AS YD_UP_COLL_SEQ
				                       
				                   FROM TB_YM_WRKBOOK    WB
				                       ,TB_YM_WRKBOOKMTL WM
				                       ,TB_YM_STACKLAYER SL
				                       ,TB_YM_STOCK      ST
				                  WHERE WM.YD_WBOOK_ID = WB.YD_WBOOK_ID
				                    AND WM.STOCK_ID    = SL.STOCK_ID
				                    AND WM.STOCK_ID    = ST.STOCK_ID
				                    AND WB.YD_WBOOK_ID = :V_YD_WBOOK_ID
				                    AND WB.DEL_YN      = 'N'
				                    AND WM.DEL_YN      = 'N'
				                    AND SL.STACK_LAYER_STAT = 'C'
				                    AND WB.YD_GP=SUBSTR(SL.STACK_COL_GP,1,1)
				                  ORDER BY YD_STR_LOC DESC) WM
				          ORDER BY YD_STR_LOC DESC)
				 ) DD ON (WM.YD_WBOOK_ID = DD.YD_WBOOK_ID AND WM.STOCK_ID = DD.STOCK_ID)
				 WHEN MATCHED THEN UPDATE SET
				      WM.MODIFIER       = DD.MODIFIER
				     ,WM.MOD_DDTT       = DD.MOD_DDTT
				     ,WM.STACK_COL_GP   = DD.STACK_COL_GP
				     ,WM.STACK_BED_GP   = DD.STACK_BED_GP
				     ,WM.STACK_LAYER_GP = DD.STACK_LAYER_GP
				     ,WM.YD_UP_COLL_SEQ = DD.YD_UP_COLL_SEQ
				 */
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updWmStrLoc", logId, methodNm, "작업예약 재료 위치 수정");
				
				/**
				 * 1.1 크레인스케줄 존재유무 체크
				 */
				/*
			    SELECT CS.YD_CRN_SCH_ID 
				     , CS.YD_SCH_CD
				  FROM TB_YM_CRNSCH     CS
				     , TB_YM_CRNWRKMTL  CM
				 WHERE 1 = 1
				   AND CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
				   AND CS.YD_WBOOK_ID   = :V_YD_WBOOK_ID
				   AND CM.STOCK_ID      = :V_STOCK_ID
				   AND CM.DEL_YN        = 'N'
				   AND CS.DEL_YN        = 'N'
				 */
				jrParam.setField("YD_WBOOK_ID", sYD_WBOOK_ID);
				jrParam.setField("STOCK_ID"   , sSTOCK_ID);
				JDTORecordSet jsSchInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getSchInfoWithWbookId", logId, methodNm, "크레인스케줄 존재유무 체크");
				
				if (jsSchInfo.size() > 0) {
					throw new Exception("해당 저장품의 크레인스케줄이 존재합니다.");
				}

				/*
				SELECT * 
				  FROM TB_YM_WRKBOOK
				 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID 
				*/
				jrParam.setField("YD_WBOOK_ID", sYD_WBOOK_ID);
				JDTORecordSet jsWrkBookInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getWbookInfo", logId, methodNm, "작업예약 조회");
				
				/**
				 * 1.2 작업예약 체크
				 */
				if (jsWrkBookInfo.size() > 0) {
					sCurBayGp = commUtils.nvl(jsWrkBookInfo.getRecord(0).getFieldString("YD_BAY_GP"), "");
				}

				if (sCurBayGp.equals(sPutStackColGp.substring(1, 2))) {
					// 같은 동에 산적위치 수정을 한 경우
				} else {
					// 다른 동으로 산적위치 수정을 한 경우.
					// 이 경우에 작업예약 동구분 항목도 수정을 해준다.
					/*
					UPDATE TB_YM_WRKBOOK
					   SET YD_BAY_GP   = :V_YD_BAY_GP
					     , MODIFIER    = :V_MODIFIER
					     , MOD_DDTT    = SYSDATE     
					 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID     
					 */
					jrParam.setField("YD_BAY_GP"  , sPutStackColGp.substring(1, 2));
					jrParam.setField("YD_WBOOK_ID", sYD_WBOOK_ID);
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateBayGpWithWbookId", logId, methodNm, "");
				}
			}

			String sUP_STACK_COL_GP   = "";
			String sUP_STACK_BED_GP   = "";
			String sUP_STACK_LAYER_GP = "";
			String sUP_SECT_GP = "";

			/********************************************************
			 * 2. 저장품의 MAP정보를 가져온다. 중복위치도 체크한다.
			 ********************************************************/
			commUtils.printLog(logId, "************************************************", "[INFO]");
			commUtils.printLog(logId, "저장품의 MAP정보를 가져온다. 중복위치도 체크한다", "[INFO]");
			commUtils.printLog(logId, "************************************************", "[INFO]");
			
			JDTORecord jStkLyrInfo = null;
			
			/*
			SELECT *
			  FROM TB_YM_STACKLAYER
			 WHERE STOCK_ID = :V_STOCK_ID
			 */
			jrParam.setField("STOCK_ID", sSTOCK_ID);
			JDTORecordSet jsYmStkLyrInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYmStkLyrInfoByStockId", logId, methodNm, "TB_YM_STACKLAYER 조회");

			/*
			SELECT *
			  FROM TB_YD_STKLYR
			 WHERE STL_NO = :V_STOCK_ID
			 */
			JDTORecordSet jsYdStkLyrInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdStkLyrInfoByStockId", logId, methodNm, "TB_YD_STKLYR 조회");
			
			//저장품이 TB_YD_STKLYR에 존재할 때
			if (jsYdStkLyrInfo.size() > 0) {
				for (int i = 0; i < jsYdStkLyrInfo.size(); ++i) {
					
					jStkLyrInfo = jsYdStkLyrInfo.getRecord(i);
					
					sUP_STACK_COL_GP 	= commUtils.nvl(jStkLyrInfo.getFieldString("YD_STK_COL_GP"), "");
					sUP_STACK_BED_GP 	= commUtils.nvl(jStkLyrInfo.getFieldString("YD_STK_BED_NO"), "");
					sUP_STACK_LAYER_GP  = commUtils.nvl(jStkLyrInfo.getFieldString("YD_STK_LYR_NO"), ""); //YD테이블은 3자리
					
					/*
					UPDATE TB_YM_STOCK
					   SET FRTOMOVE_EQUIP_GP 	   = :V_FRTOMOVE_EQUIP_GP
					     , FRTOMOVE_EQUIP_BED_GP   = :V_FRTOMOVE_EQUIP_BED_GP
					     , FRTOMOVE_EQUIP_LAYER_GP = :V_FRTOMOVE_EQUIP_LAYER_GP
					     , CTS_RELAY_SADDLE 	   = :V_CTS_RELAY_SADDLE
					     , CTS_RELAY_YN			   = :V_CTS_RELAY_YN
					     , MODIFIER   = 'SYSTEM'
					     , MOD_DDTT   = SYSDATE     
					 WHERE STOCK_ID   = :V_STOCK_ID
					 */
					jrParam.setField("STOCK_ID"               , sSTOCK_ID);
					jrParam.setField("FRTOMOVE_EQUIP_GP"      , "");      
					jrParam.setField("FRTOMOVE_EQUIP_BED_GP"  , "");  
					jrParam.setField("FRTOMOVE_EQUIP_LAYER_GP", "");
					jrParam.setField("CTS_RELAY_SADDLE"       , "");       
					jrParam.setField("CTS_RELAY_YN"           , "");           

					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateStockMoveEquipInfo", logId, methodNm, "TB_YM_STOCK 수정");
					
					/*
					UPDATE USRYDA.TB_YD_STKLYR
					   SET STL_NO			   = :V_STL_NO
						 , YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
						 , MODIFIER            = :V_MODIFIER
					 	 , MOD_DDTT            = SYSDATE     
					 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
					   AND YD_STK_BED_NO = :V_YD_STK_BED_NO
					   AND YD_STK_LYR_NO = :V_YD_STK_LYR_NO				
					 */
					jrParam.setField("STL_NO"             , "");
					jrParam.setField("YD_STK_LYR_MTL_STAT", YmConstant.YD_STK_LYR_MTL_STAT_E);
					jrParam.setField("YD_STK_COL_GP"      , sUP_STACK_COL_GP);
					jrParam.setField("YD_STK_BED_NO"      , sUP_STACK_BED_GP);
					jrParam.setField("YD_STK_LYR_NO"      , sUP_STACK_LAYER_GP);
					
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdStkLyrByPk", logId, methodNm, "TB_YD_STKLYR 수정");
					
				} //end for
			} //if (jrYdStkLyrInfo.size() > 0) 
			

			if (jsYmStkLyrInfo.size() > 0) {
				for (int inx = 0; inx < jsYmStkLyrInfo.size(); inx++) {
					jStkLyrInfo = jsYmStkLyrInfo.getRecord(inx);

					sUP_STACK_COL_GP 	= commUtils.nvl(jStkLyrInfo.getFieldString("STACK_COL_GP"  ), "");
					sUP_STACK_BED_GP 	= commUtils.nvl(jStkLyrInfo.getFieldString("STACK_BED_GP"  ), "");
					sUP_STACK_LAYER_GP  = commUtils.nvl(jStkLyrInfo.getFieldString("STACK_LAYER_GP"), "");

					sUP_SECT_GP         = sUP_STACK_COL_GP.substring(2, 4);
					
					/***************************
					 * FROM위치가 대차일 경우 
					 ***************************/
					if ("TC".equals(sUP_SECT_GP)) {
						/*
						UPDATE TB_YM_STACKER
						   SET(
						       STACK_BED_QNTY_CURR,
						       STACK_BED_ABLE_QNTY,
						       MODIFIER,
						       MOD_DDTT
						      )= 
						        (
						         SELECT 
						                CASE WHEN TO_NUMBER(NVL(STACK_BED_QNTY_CURR,0) + :V_QTY) < 0
						                     THEN 0 ELSE TO_NUMBER(NVL(STACK_BED_QNTY_CURR,0) + :V_QTY
						                END AS CUR_QNT,-- 적치BED수량현재
						                CASE WHEN TO_NUMBER(NVL(STACK_BED_ABLE_QNTY,0) + (:V_QTY*-1)) < 0
						                     THEN 0 ELSE TO_NUMBER(NVL(STACK_BED_ABLE_QNTY,0) + (:V_QTY*-1))
						                END AS ABLE_QNT,-- 적치BED가능수량
						                'SYSTEM',
						                SYSDATE     
						           FROM TB_YM_STACKER
						          WHERE STACK_COL_GP = :V_STACK_COL_GP
						            AND STACK_BED_GP = :V_STACK_BED_GP
						        )
						 WHERE STACK_COL_GP = :V_STACK_COL_GP
						   AND STACK_BED_GP = :V_STACK_BED_GP						 
						 */
						jrParam.setField("QTY"         , "-1");
						jrParam.setField("STACK_COL_GP", sUP_STACK_COL_GP);
						jrParam.setField("STACK_BED_GP", sUP_STACK_BED_GP);
						commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateStackerQtyInfo", logId, methodNm, "TB_YM_STACKER 정보 초기화");
						
					} // end if ("TC".equals(sUP_SECT_GP))

					/*
					UPDATE TB_YM_STOCK
					   SET FRTOMOVE_EQUIP_GP 	   = :V_FRTOMOVE_EQUIP_GP
					     , FRTOMOVE_EQUIP_BED_GP   = :V_FRTOMOVE_EQUIP_BED_GP
					     , FRTOMOVE_EQUIP_LAYER_GP = :V_FRTOMOVE_EQUIP_LAYER_GP
					     , CTS_RELAY_SADDLE 	   = :V_CTS_RELAY_SADDLE
					     , CTS_RELAY_YN			   = :V_CTS_RELAY_YN
					     , MODIFIER   = 'SYSTEM'
					     , MOD_DDTT   = SYSDATE     
					 WHERE STOCK_ID   = :V_STOCK_ID
					 */
					jrParam.setField("STOCK_ID"               , sSTOCK_ID);
					jrParam.setField("FRTOMOVE_EQUIP_GP"      , "");      
					jrParam.setField("FRTOMOVE_EQUIP_BED_GP"  , "");  
					jrParam.setField("FRTOMOVE_EQUIP_LAYER_GP", "");
					jrParam.setField("CTS_RELAY_SADDLE"       , "");       
					jrParam.setField("CTS_RELAY_YN"           , "");           

					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateStockMoveEquipInfo", logId, methodNm, "TB_YM_STOCK 수정");
					
					/**
					 * 적치단 UP위치 Clear tb_ym_stacklayer Table : stock_id =
					 * ''(Empty) tb_ym_stacklayer Table : stack_layer_stat =
					 * 'E'(적치가능)
					 */
					/*
					UPDATE TB_YM_STACKLAYER
					   SET STOCK_ID			= :V_STOCK_ID
						 , STACK_LAYER_STAT	= :V_STACK_LAYER_STAT
						 , MODIFIER         = 'SYSTEM'
					 	 , MOD_DDTT         = SYSDATE     
					 WHERE STACK_COL_GP   = :V_STACK_COL_GP 
					   AND STACK_BED_GP   = :V_STACK_BED_GP 
					   AND STACK_LAYER_GP = :V_STACK_LAYER_GP 					
					 */
					jrParam.setField("STOCK_ID"        , "");
					jrParam.setField("STACK_LAYER_STAT", YmConstant.YD_STK_LYR_MTL_STAT_E);
					jrParam.setField("STACK_COL_GP"    , sUP_STACK_COL_GP);
					jrParam.setField("STACK_BED_GP"    , sUP_STACK_BED_GP);
					jrParam.setField("STACK_LAYER_GP"  , sUP_STACK_LAYER_GP);
					
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateCraneStackLayerStat", logId, methodNm, "TB_YM_STACKLAYER 초기화");

				} // end for
			} //if (jsYmStkLyrInfo.size() > 0)
			
			
			if ("TC".equals(sUP_SECT_GP)) {
				/*********************************
				 * 대차재료 삭제
				 ********************************/
				/*
				SELECT YD_TCAR_SCH_ID
				  FROM TB_YM_TCARSCH
				 WHERE DEL_YN = 'N'
				   AND YD_TCAR_SCH_ID = (
				                        SELECT YD_TCAR_SCH_ID 
				                          FROM TB_YM_TCARFTMVMTL
				                         WHERE DEL_YN = 'N'
				                           AND STOCK_ID = :V_STOCK_ID
				                        )
				 */
				jrParam.setField("STOCK_ID"   , sSTOCK_ID);
				JDTORecordSet rst = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdTcarSchIdByStockId", logId, methodNm, "대차스케줄 조회");
				
				if (rst.size() > 0) {
					String sYD_TCAR_SCH_ID = rst.getRecord(0).getFieldString("YD_TCAR_SCH_ID");
					jrParam.setField("YD_TCAR_SCH_ID", sYD_TCAR_SCH_ID);
					/* 대차재료 삭제
					UPDATE TB_YM_TCARFTMVMTL
					   SET MODIFIER    = :V_MODIFIER
					      ,MOD_DDTT    = SYSDATE
					      ,DEL_YN      = 'Y'
					 WHERE YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
					   AND STOCK_ID       = :V_STOCK_ID
					   AND DEL_YN         = 'N'
					 */
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updDelTcarFtMvMtl", logId, methodNm, "대차재료 삭제");
					
					/**
					 * 목적동에 작업예약이 생성 되었을 때에 삭제
					 */
					/*  
					SELECT WB.YD_WBOOK_ID 
					     , WB.YD_SCH_CD 
					     ,(SELECT C.YD_CRN_SCH_ID FROM TB_YM_CRNSCH C WHERE C.YD_WBOOK_ID = WB.YD_WBOOK_ID AND ROWNUM = 1) AS YD_CRN_SCH_ID
					  FROM TB_YM_WRKBOOK    WB
					     , TB_YM_WRKBOOKMTL WM
					 WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
					   AND WM.STOCK_ID = :V_STOCK_ID
					   AND WB.DEL_YN = 'N'
					   AND WM.DEL_YN = 'N'
					*/
					jrParam.setField("STOCK_ID", sSTOCK_ID);
					JDTORecordSet jsWbookInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getWrkBookIdByStockId", logId, methodNm, "작업예약 조회");
					
					if (jsWbookInfo.size() > 0) {
						
						String sWRK_KD = jsWbookInfo.getRecord(0).getFieldString("YD_SCH_CD").substring(2, 4); // 대차작업
						String sWRK_GP = jsWbookInfo.getRecord(0).getFieldString("YD_SCH_CD").substring(6, 8); // 하차작업
						
						if ("TC".equals(sWRK_KD) && "LM".equals(sWRK_GP)) { // 대차하차 스케줄
							jrParam.setField("YD_WBOOK_ID", jsWbookInfo.getRecord(0).getFieldString("YD_WBOOK_ID"));
							/*
							UPDATE TB_YM_WRKBOOKMTL
							   SET MODIFIER    = :V_MODIFIER
							      ,MOD_DDTT    = SYSDATE
							      ,DEL_YN      = 'Y'
							 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
							   AND DEL_YN      = 'N'
							 */
							commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updDelYnWrkBookMtl", logId, methodNm, "작업예약재료 삭제");
							/*
							UPDATE TB_YM_WRKBOOK
							   SET MODIFIER    = :V_MODIFIER
							      ,MOD_DDTT    = SYSDATE
							      ,DEL_YN      = 'Y'
							 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
							   AND DEL_YN      = 'N' 
							 */
							commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updDelYnWrkBook", logId, methodNm, "작업예약 삭제");
						}
					}
				} //대차재료 삭제
			}
			
			commUtils.printLog(logId, "○○○FROM 위치 수정 END", "[INFO]");

			/********************************
			 * 3. TO 위치 수정
			 ********************************/
			String sPUT_SECT_GP = sPutStackColGp.substring(2, 4); //SECT_GP

			// 가상 위치
			String sTempLayer = sPutStackColGp.substring(0,2) + "XX010101";
			
			/*
			SELECT *
			  FROM TB_YM_STACKLAYER
			 WHERE STACK_COL_GP  	= :V_STACK_COL_GP
			   AND STACK_BED_GP		= :V_STACK_BED_GP 
			   AND STACK_LAYER_GP	= :V_STACK_LAYER_GP
		 	*/
			jrParam.setField("STACK_COL_GP"  , sPutStackColGp);
			jrParam.setField("STACK_BED_GP"  , sPutStackBedGp);
			jrParam.setField("STACK_LAYER_GP", sPutStackLayerGp);
		 	
		 	jsPutLocInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getStackLayerInfoWithPk", logId, methodNm, "TB_YM_STACKLAYER 정보 조회");		
			
			String sToStockId = "";
			
			if (jsPutLocInfo.size() > 0){
				sToStockId 	= commUtils.nvl(jsPutLocInfo.getRecord(0).getFieldString("STOCK_ID"), "");
			}
			
			/******************************************************
			 * TO위치에 존재하는 기존 저장품 이력 저장 
			 ******************************************************/
			if (!"".equals(sToStockId) && !sSTOCK_ID.equals(sToStockId)){ 
	    	 	this.insertUpPutWrslRtData(sToStockId, sPutLoc, sTempLayer, sUpYardGp, sMODIFIER);// 

	    	 	//코일공통에 위치이력 UPDATE
	    	 	JDTORecord jRecord = JDTORecordFactory.getInstance().create();
	    	 	jRecord.setField("STOCK_ID"   , sToStockId);
	    	 	jRecord.setField("YD_LOC"     , sTempLayer);
	    	 	
				EJBConnector ejbConn1 = new EJBConnector("default", "BCoilL2RcvSeEJB", this);
				ejbConn1.trx("UpdCoilComLoc", new Class[] { JDTORecord.class }, new Object[] { jRecord });
			}
			
			/************************************
			 * 코일제품이적작업실적 전문 송신 
			 *************************************/
			commUtils.printLog(logId, "[YDDMR004] 코일제품이적작업실적 전문 전송", "[INFO]");
 			JDTORecord tcRecordDM = JDTORecordFactory.getInstance().create(); 
 			
			// PIDEV
//			String sApplyYnPI = commDao.ApplyYnPI("", "BCoilJspSeEJBSBean => 코일제품이적작업실적", "APPPI0", "*", "*");
//			
//			if("Y".equals(sApplyYnPI)) {
	 			tcRecordDM.setField("MQ_TC_CD"          , "M10YDLMJ1031");
				tcRecordDM.setField("MQ_TC_CREATE_DDTT" , commUtils.getDateTime14());				
				tcRecordDM.setField("YD_GP"          	, "3");
				tcRecordDM.setField("DIST_GOODS_GP"     , "H");
				tcRecordDM.setField("YARD_GP"     , "");
	 			tcRecordDM.setField("GOODS_NO"          , sToStockId);
	 			tcRecordDM.setField("STORE_LOC_CD_FROM"    , sPutStackColGp + sPutStackBedGp + sPutStackLayerGp);
	 			tcRecordDM.setField("STORE_LOC_CD_TO"      , sTempLayer);				
//			} else {
//	 			tcRecordDM.setField("JMS_TC_CD"         , "YDDMR004");
//				tcRecordDM.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14());
//	 			tcRecordDM.setField("GOODS_NO"          , sToStockId);
//	 			tcRecordDM.setField("BEFO_STORE_LOC"    , sPutStackColGp + sPutStackBedGp + sPutStackLayerGp);
//	 			tcRecordDM.setField("TO_STORE_LOC"      , sTempLayer);				
//			}
 			
 			//인터페이스 전문 호출
 			jrRtn = commUtils.addSndData(jrRtn, tcRecordDM);
 			
            
 			/********************************
 			 * TO위치가 대차일 경우
 			 ********************************/
			if ("TC".equals(sPUT_SECT_GP)) {
				/* *******************************************************************
				 * 1. 대차위치 파악
				 * 2. 대차위 다른 제품 존재하면                      대차재료 생성
				 *                     존재하지 않으면 대차초기화 후 대차재료 생성
				 * *******************************************************************/
				/*
				SELECT CURR_STOP_LOC
                     , EQUIP_GP					
				  FROM USRYMA.TB_YM_EQUIP 
				 WHERE DEL_YN = 'N'
				   AND EQUIP_GP LIKE '__TC__'
				   AND CURR_STOP_LOC = :V_CURR_STOP_LOC
				 */
				jrParam.setField("CURR_STOP_LOC", sPutStackColGp);
				JDTORecordSet jsTCInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarCurrLoc", logId, methodNm, "대차위치 조회");
				
				if (jsTCInfo.size() > 0) {
					/*
					SELECT YD_TCAR_SCH_ID
					     , CASE WHEN :V_YD_MOVE_GP = 'S' THEN                   --야드대차이동구분(S:출발,E:도착)
					            CASE WHEN YD_EQP_WRK_STAT = 'U' THEN '1'        --공대차(상차)출발
					            ELSE 'A' END                                    --영대차(하차)출발
					       ELSE CASE WHEN YD_EQP_WRK_STAT = 'L' THEN 'B'        --영대차(하차)도착
					                 WHEN YD_CARLD_WRK_BOOK_ID IS NULL THEN '0' --공대차도착(상차대기)
					            ELSE '2' END                                    --공대차(상차)도착
					       END AS YD_CAR_PROG_STAT                              --야드차량진행상태
					  FROM TB_YM_TCARSCH
					 WHERE YD_EQP_ID = :V_YD_EQP_ID
					   AND DEL_YN    = 'N'   
					 */
					String sEQUIP_GP = jsTCInfo.getRecord(0).getFieldString("EQUIP_GP");
					jrParam.setField("YD_EQP_ID" , sEQUIP_GP);
					jrParam.setField("YD_MOVE_GP", "");
					JDTORecordSet jsTcarSchInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarSchByEqpid", logId, methodNm, "대차스케줄 조회");
					
					/*
					SELECT YD_TCAR_SCH_ID
					     , STOCK_ID
					     , DEL_YN
					     , STACK_BED_GP
					     , STACK_LAYER_GP
					     , HCR_GP
					     , STL_PROG_CD
					     , YD_MTL_ITEM
					     , YD_ROUTE_GP
					  FROM TB_YM_TCARFTMVMTL
					 WHERE YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
					   AND DEL_YN = 'N'
					 */
					jrParam.setField("YD_TCAR_SCH_ID", jsTcarSchInfo.getRecord(0).getFieldString("YD_TCAR_SCH_ID"));
					JDTORecordSet jsTcarMtlInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdTcarftmvmtlId", logId, methodNm, "대차 이송재료 조회");
					
					if (jsTcarMtlInfo.size() > 0) { 
						//대차위에 이송재료존재하면
					} else {
						//대차에 이송재료가 존재하지 않으면 대차초기화
						jrParam.setField("EQUIP_GP"      , sEQUIP_GP);
						jrParam.setField("YD_CURR_BAY_GP", sPutStackColGp.substring(1, 2));//현재동
						jrParam.setField("userid"        , sMODIFIER);
						this.initTcarSchMgt(jrParam);
					}
					
					/*
					INSERT INTO TB_YM_TCARFTMVMTL
					( YD_TCAR_SCH_ID
					, STOCK_ID
					, REGISTER
					, REG_DDTT
					, MODIFIER
					, MOD_DDTT
					, DEL_YN
					, STACK_BED_GP
					, STACK_LAYER_GP
					, HCR_GP
					, STL_PROG_CD
					, YD_MTL_ITEM
					, YD_ROUTE_GP
					) 
					   SELECT 
					          (SELECT YD_TCAR_SCH_ID 
					             FROM TB_YM_TCARSCH
					            WHERE DEL_YN = 'N'
					              AND YD_EQP_ID = :V_YD_EQP_ID
					          ) AS YD_TCAR_SCH_ID
					        , ST.STOCK_ID
					        , :V_MODIFIER
					        , SYSDATE
					        , :V_MODIFIER
					        , SYSDATE
					        , 'N'
					        , :V_STACK_BED_GP
					        , '01'
					        , CC.HCR_GP
					        , CC.CURR_PROG_CD  AS STL_PROG_CD 
					        , ST.STOCK_ITEM AS YD_MTL_ITEM
					        , ''
					     FROM USRYMA.TB_YM_STOCK      ST
					        , USRPTA.TB_PT_COILCOMM   CC
					    WHERE ST.STOCK_ID = CC.COIL_NO
					      AND ST.STOCK_ID = :V_STOCK_ID
					 */
					jrParam.setField("STOCK_ID"       , sSTOCK_ID);
					jrParam.setField("YD_EQP_ID"      , sEQUIP_GP);
					commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insTcarFtmvMtl", logId, methodNm, "대차이송재료 등록");
					
				} else {
					throw new Exception("해당 대차위치가 위치수정할 동에 있지 않습니다.");
				}
				
				/***************************
				 * 대차위치 CLEAR
				 ***************************/
				String sCurrQty = "1";

				jrParam.setField("QTY"         , sCurrQty);
				jrParam.setField("STACK_COL_GP", sPutStackColGp);
				jrParam.setField("STACK_BED_GP", sPutStackBedGp);
				/*
				UPDATE TB_YM_STACKER
				   SET(
				       STACK_BED_QNTY_CURR,
				       STACK_BED_ABLE_QNTY,
				       MODIFIER,
				       MOD_DDTT
				      )= 
				        (
				         SELECT 
				                CASE WHEN TO_NUMBER(NVL(STACK_BED_QNTY_CURR,0) + :V_QTY) < 0
				                     THEN 0 ELSE TO_NUMBER(NVL(STACK_BED_QNTY_CURR,0) + :V_QTY
				                END AS CUR_QNT,-- 적치BED수량현재
				                CASE WHEN TO_NUMBER(NVL(STACK_BED_ABLE_QNTY,0) + (:V_QTY*-1)) < 0
				                     THEN 0 ELSE TO_NUMBER(NVL(STACK_BED_ABLE_QNTY,0) + (:V_QTY*-1))
				                END AS ABLE_QNT,-- 적치BED가능수량
				                'SYSTEM',
				                SYSDATE     
				           FROM TB_YM_STACKER
				          WHERE STACK_COL_GP = :V_STACK_COL_GP
				            AND STACK_BED_GP = :V_STACK_BED_GP
				        )
				 WHERE STACK_COL_GP = :V_STACK_COL_GP
				   AND STACK_BED_GP = :V_STACK_BED_GP	
				 */
				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateStackerQtyInfo", logId, methodNm, "");

				/*
				UPDATE TB_YM_STOCK
				   SET FRTOMOVE_EQUIP_GP 	   = :V_FRTOMOVE_EQUIP_GP
				     , FRTOMOVE_EQUIP_BED_GP   = :V_FRTOMOVE_EQUIP_BED_GP
				     , FRTOMOVE_EQUIP_LAYER_GP = :V_FRTOMOVE_EQUIP_LAYER_GP
				     , CTS_RELAY_SADDLE 	   = :V_CTS_RELAY_SADDLE
				     , CTS_RELAY_YN			   = :V_CTS_RELAY_YN
				     , MODIFIER   = 'SYSTEM'
				     , MOD_DDTT   = SYSDATE     
				 WHERE STOCK_ID   = :V_STOCK_ID
				 */
				jrParam.setField("STOCK_ID"               , sSTOCK_ID);
				jrParam.setField("FRTOMOVE_EQUIP_GP"      , sPutStackColGp.substring(0, 1) + "X" + sPutStackColGp.substring(2));      
				jrParam.setField("FRTOMOVE_EQUIP_BED_GP"  , sPutStackBedGp);  
				jrParam.setField("FRTOMOVE_EQUIP_LAYER_GP", sPutStackLayerGp);
				jrParam.setField("CTS_RELAY_SADDLE"       , "");       
				jrParam.setField("CTS_RELAY_YN"           , "");           

				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateStockMoveEquipInfo", logId, methodNm, "TB_YM_STOCK 수정");
				
			} else {
				//COIL 저장품 TABLE 수정 저장품TABLE의 이동설비항목에 권하위치값을 삭제한다.
				/*
				UPDATE TB_YM_STOCK
				   SET FRTOMOVE_EQUIP_GP 	   = :V_FRTOMOVE_EQUIP_GP
				     , FRTOMOVE_EQUIP_BED_GP   = :V_FRTOMOVE_EQUIP_BED_GP
				     , FRTOMOVE_EQUIP_LAYER_GP = :V_FRTOMOVE_EQUIP_LAYER_GP
				     , CTS_RELAY_SADDLE 	   = :V_CTS_RELAY_SADDLE
				     , CTS_RELAY_YN			   = :V_CTS_RELAY_YN
				     , MODIFIER   = 'SYSTEM'
				     , MOD_DDTT   = SYSDATE     
				 WHERE STOCK_ID   = :V_STOCK_ID
				 */
				jrParam.setField("STOCK_ID"               , sSTOCK_ID);
				jrParam.setField("FRTOMOVE_EQUIP_GP"      , "");      
				jrParam.setField("FRTOMOVE_EQUIP_BED_GP"  , "");  
				jrParam.setField("FRTOMOVE_EQUIP_LAYER_GP", "");
				jrParam.setField("CTS_RELAY_SADDLE"       , "");       
				jrParam.setField("CTS_RELAY_YN"           , "");
				jrParam.setField("T_CAR_HOLD"             , sTCarHold);

				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateStockMoveEquipInfo", logId, methodNm, "TB_YM_STOCK 수정");
			} //END if ("TC".equals(sPUT_SECT_GP))

			
			/*
			UPDATE TB_YM_STACKLAYER
			   SET STOCK_ID			= :V_STOCK_ID
				 , STACK_LAYER_STAT	= :V_STACK_LAYER_STAT
				 , MODIFIER         = 'SYSTEM'
			 	 , MOD_DDTT         = SYSDATE     
			 WHERE STACK_COL_GP   = :V_STACK_COL_GP 
			   AND STACK_BED_GP   = :V_STACK_BED_GP 
			   AND STACK_LAYER_GP = :V_STACK_LAYER_GP 
			 */
			jrParam.setField("STOCK_ID"        , sSTOCK_ID);
			jrParam.setField("STACK_LAYER_STAT", YmConstant.YD_STK_LYR_MTL_STAT_C);
			jrParam.setField("STACK_COL_GP"    , sPutStackColGp);
			jrParam.setField("STACK_BED_GP"    , sPutStackBedGp);
			jrParam.setField("STACK_LAYER_GP"  , sPutStackLayerGp);
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateCraneStackLayerStat", logId, methodNm, "적치단 수정");
			
			
			/*******************************************
			 * To위치 -> 코일공통에 위치이력 UPDATE
			 *******************************************/
    	 	JDTORecord jRecordTO = JDTORecordFactory.getInstance().create();
    	 	jRecordTO.setField("STOCK_ID"   , sSTOCK_ID);
    	 	jRecordTO.setField("YD_LOC"     , sPutLoc);
    	 	
			EJBConnector ejbConnTO = new EJBConnector("default", "BCoilL2RcvSeEJB", this);
			ejbConnTO.trx("UpdCoilComLoc", new Class[] { JDTORecord.class }, new Object[] { jRecordTO });


			/*
			 * 저장품 예상PUT 위치 CLEAR 저장품지정조회화면에서 예약상태로 남아있는것을 방지하기 위해
			 */
			/*
			SELECT ST.*
			     ,(SELECT WB.YD_WBOOK_ID 
			          FROM TB_YM_WRKBOOK    WB
			             , TB_YM_WRKBOOKMTL WM
			         WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
			           AND WM.STOCK_ID = ST.STOCK_ID
			           AND WB.DEL_YN = 'N'
			           AND WM.DEL_YN = 'N'     
			      ) AS YD_WBOOK_ID
			  FROM TB_YM_STOCK ST
			 WHERE ST.STOCK_ID = :V_STOCK_ID
			 */
			jrParam.setField("STOCK_ID", sSTOCK_ID);
			JDTORecordSet jsStockInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getStockInfoByPk", logId, methodNm, "저장품 정보 조회");
			
			if (jsStockInfo.size() > 0) {
				
				String sCARUNLOAD_PUT_LOC = StringHelper.evl(jsStockInfo.getRecord(0).getFieldString("CARUNLOAD_PUT_LOC"), "");// 하차PUT위치
				
				if (sPutLoc.equals(sCARUNLOAD_PUT_LOC)) {
					/*
					UPDATE TB_YM_STOCK
					   SET CARUNLOAD_PUT_LOC = :V_CARUNLOAD_PUT_LOC
					     , MODIFIER = :V_MODIFIER
					     , MOD_DDTT = SYSDATE     
					 WHERE STOCK_ID = :V_STOCK_ID            
					 */
					jrParam.setField("CARUNLOAD_PUT_LOC", "");
					jrParam.setField("STOCK_ID"         , sSTOCK_ID);
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStockByPk", logId, methodNm, "");
				}
			}

			/**
			 * 5. 저장품이동조건 수정
			 */
			/*
			UPDATE TB_YM_STOCK
			   SET STOCK_MOVE_TERM = DECODE(STOCK_MOVE_TERM,'BD',STOCK_MOVE_TERM, :V_STOCK_MOVE_TERM) ,
			     , MODIFIER   = :V_MODIFIER
			     , MOD_DDTT   = SYSDATE     
			 WHERE STOCK_ID = :V_STOCK_ID
			 */
			jrParam.setField("STOCK_MOVE_TERM", sSTOCK_MOVE_TERM);
			jrParam.setField("STOCK_ID"       , sSTOCK_ID);
			commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateStockTransInfo", logId, methodNm, "저장품이동조건 수정");
			

			/**
			 * 6. Crane 작업 실적 등록
			 */
			this.insertUpPutWrslRtData(sSTOCK_ID.trim(), sUpLoc.trim(), sPutLoc.trim(), sPutYardGp ,sMODIFIER);

			/**
			 * 7. Coil 이적실적 (출하로 이적실적 송신 YDDMR004) 
			 * 공통 진도 Code가 출하작업지시대기 K
			 *                  제품충당대기     Z
			 *                  출하작업대기     L
			 *                  보관매출         M 이면 출하로 "이적실적 송신"
			 */

			if (sProgCd.equals(YmConstant.CURR_PROG_CD_COIL_K) ||
			    sProgCd.equals(YmConstant.CURR_PROG_CD_COIL_P) ||
			    sProgCd.equals(YmConstant.CURR_PROG_CD_COIL_Z) ||
			    sProgCd.equals(YmConstant.CURR_PROG_CD_COIL_J) ||
			    sProgCd.equals(YmConstant.CURR_PROG_CD_COIL_L) ||
			    sProgCd.equals(YmConstant.CURR_PROG_CD_COIL_X) ||
			    sProgCd.equals(YmConstant.CURR_PROG_CD_COIL_M)) {
				
				JDTORecord tcParam = JDTORecordFactory.getInstance().create();
				
				// PIDEV				
//				if("Y".equals(sApplyYnPI)) {
		 			tcRecordDM.setField("MQ_TC_CD"          , "M10YDLMJ1031");
					tcRecordDM.setField("MQ_TC_CREATE_DDTT" , commUtils.getDateTime14());				
					tcRecordDM.setField("YD_GP"          	, "3");
					tcRecordDM.setField("DIST_GOODS_GP"     , "H");
					tcRecordDM.setField("YARD_GP"     , "");
		 			tcRecordDM.setField("GOODS_NO"          , sToStockId);
		 			tcRecordDM.setField("STORE_LOC_CD_FROM"    , sUpLoc.trim());
		 			tcRecordDM.setField("STORE_LOC_CD_TO"      , sPutLoc.trim());	
//				} else {
//					tcParam.setField("JMS_TC_CD"         , "YDDMR004");
//					tcParam.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14());
//					tcParam.setField("GOODS_NO"          , sSTOCK_ID.trim());
//					tcParam.setField("BEFO_STORE_LOC"    , sUpLoc.trim());
//					tcParam.setField("TO_STORE_LOC"      , sPutLoc.trim());
//				}
				
				jrRtn = commUtils.addSndData(jrRtn, tcParam);		
			}
			
			/**
			 * 10. TO위치가 SPM,HFL 입측이면 보급실적을 조업으로 송신한다.
			 */
			if ("FE".equals(sPUT_SECT_GP) ||// COIL HFL보급위치
				"FI".equals(sPUT_SECT_GP) ||// COIL HFLTAKEIN위치
				"KE".equals(sPUT_SECT_GP) ||// COIL SPM보급위치
				"KI".equals(sPUT_SECT_GP)) {// COIL SPMTAKEIN위치
				
				String sPlantGbn   = "";
				String sProcessId  = "";
				String sPositionNo = "";
				
				if ("FE".equals(sPUT_SECT_GP)) {
					sPlantGbn = "H";	sProcessId = "1";	sPositionNo = "D1";
				}	
				if ("FI".equals(sPUT_SECT_GP)) {
					sPlantGbn = "H";	sProcessId = "5";	sPositionNo = "D5";
				}
				if ("KE".equals(sPUT_SECT_GP)) {
					sPlantGbn = "S";	sProcessId = "1";	sPositionNo = "D1";
				}
				if ("KI".equals(sPUT_SECT_GP)) {
					sPlantGbn = "S";	sProcessId = "5";	sPositionNo = "D5";
				}
				
				//코일보급및 보급취소
				JDTORecord tcParamYMPOJ161 = JDTORecordFactory.getInstance().create();
				tcParamYMPOJ161.setField("JMS_TC_CD"         , YmConstant.YMPOJ161);
				tcParamYMPOJ161.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14());
				tcParamYMPOJ161.setField("tcCode"            , YmConstant.YMPOJ161);
				tcParamYMPOJ161.setField("tcDate"            , YmCommUtils.getCurDate("yyyy-MM-dd"));
				tcParamYMPOJ161.setField("tcTime"            , YmCommUtils.getCurDate("HH-mm-ss"));
				tcParamYMPOJ161.setField("plantGbn"          , "B");      //B열연
				tcParamYMPOJ161.setField("procGbn"           , sPlantGbn);  //* 공정구분 CHAR(1) H : Hot Filnal, S : SkinPass
				tcParamYMPOJ161.setField("coilNo"            , sSTOCK_ID.trim()); //* COIL번호 CHAR(11)
				tcParamYMPOJ161.setField("processId"         , sProcessId);    //* 처리구분 CHAR(1) 1:보급,2:보급취소,3:추출,4:Take-Out,5:Take-In
				tcParamYMPOJ161.setField("downDate"          , YmCommUtils.getCurDate("yyyyMMdd"));//* 권하일자 CHAR(8) yyyymmdd
				tcParamYMPOJ161.setField("downTime"          , YmCommUtils.getCurDate("HHmmss"));  //* 권하시각 CHAR(6) HHMMSS
				tcParamYMPOJ161.setField("positionNo"        , sPositionNo); //* 위치포지션 CHAR(2)
				jrRtn = commUtils.addSndData(jrRtn, tcParamYMPOJ161);
				
				//
				JDTORecord tcParamYDQMJ002 = JDTORecordFactory.getInstance().create();
				tcParamYDQMJ002.setField("JMS_TC_CD"         , YmConstant.YDQMJ002);
				tcParamYDQMJ002.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14());
				tcParamYDQMJ002.setField("STL_NO"            , sSTOCK_ID.trim());
				jrRtn = commUtils.addSndData(jrRtn, tcParamYDQMJ002);
			}
			

			/**
			 * 12. YARD MAP 정보 실적 등록  YMA7L002
			 */
			commUtils.printLog(logId, "YMA7L002 INTERFACE SEND", "[INFO]");
			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setField("TC_CD"          , "YMA7L002");
			jrYdMsg.setField("MSG_GP"         , "I");
			jrYdMsg.setField("YD_INFO_SYNC_CD", "5");
			jrYdMsg.setField("STOCK_ID"       , sSTOCK_ID.trim());

			jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA7L002", jrYdMsg));
			
			
			
			return jrRtn;
			
		} catch (DAOException daoe) {
			throw daoe;
		} 

	}	
	
	/**
	 * @throws Exception 
	 * 오퍼레이션명 : 
	 *
	 * COIL 스케쥴 취소 메소드.
	 * 
	 * param String : YD_CRN_SCH_ID
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */                  
	public JDTORecord cancelCoilSchInfo(String sYD_CRN_SCH_ID) throws Exception {
		
		JDTORecord jrRtn = null;
		
		try {

			JDTORecord jparam = JDTORecordFactory.getInstance().create();
			
			/*
			SELECT *
			  FROM TB_YM_CRNSCH     SC
			     , TB_YM_CRNWRKMTL  MT
			 WHERE SC.YD_CRN_SCH_ID = MT.YD_CRN_SCH_ID
			   AND SC.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			   AND SC.DEL_YN = 'N'
			   AND MT.DEL_YN = 'N'
			 */
			jparam.setField("YD_CRN_SCH_ID", sYD_CRN_SCH_ID);
			JDTORecordSet schInfo = commDao.select(jparam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getSchInfoWithSchId");

			if (schInfo.size() == 0) {
				throw new Exception("=스케쥴 취소 => 스케쥴정보 존재안함.");
			}

			String sSTOCK_ID      = StringHelper.evl(schInfo.getRecord(0).getFieldString("STOCK_ID"), "");
			String sYD_SCH_CD     = StringHelper.evl(schInfo.getRecord(0).getFieldString("YD_SCH_CD"), "");
			String sYD_AID_WRK_YN = StringHelper.evl(schInfo.getRecord(0).getFieldString("YD_AID_WRK_YN"), "N");//보조작업유무
			
			/***************************
			 * 1. 스케쥴 정보 가져온다.
			 ***************************/
			String sYD_WRK_PROG_STAT = StringHelper.evl(schInfo.getRecord(0).getFieldString("YD_WRK_PROG_STAT"), "");
			if (!YmConstant.SCH_WORK_STAT_S.equals(sYD_WRK_PROG_STAT)
			 && !YmConstant.SCH_WORK_STAT_1.equals(sYD_WRK_PROG_STAT)
			 && !YmConstant.WORK_PROG_STAT_W.equals(sYD_WRK_PROG_STAT)) {

				commUtils.printLog("", "=스케쥴 취소=>스케쥴정보를 취소할 수 없음.", "[INFO]");
			}

			/**************************
			 * 6. 작업취소 전문 전송
			 **************************/
			String sYD_EQP_ID = StringHelper.evl(schInfo.getRecord(0).getFieldString("YD_EQP_ID"),"");
			
			if (YmConstant.SCH_WORK_STAT_1.equals(sYD_WRK_PROG_STAT)) { //UP지시
				
				commUtils.printLog("", "YMA7L004(코일크레인작업지시) JMS전송", "[INFO]");
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setField("TC_CD"          , "YMA7L004");
				jrYdMsg.setField("MSG_GP"         , "I");
				jrYdMsg.setField("YD_EQP_ID"      , sYD_EQP_ID); //야드설비ID
				jrYdMsg.setField("YD_CRN_SCH_ID"  , sYD_CRN_SCH_ID);
				
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA7L004", jrYdMsg));
			}

			/*****************************
			 * 2. FROM 위치 초기화
			 *****************************/
			/*
			SELECT *
			  FROM(
			       SELECT B.STACK_COL_GP
						, B.STACK_LAYER_GP
						, B.STACK_BED_GP
						, B.STACK_LAYER_X_AXIS
						, B.STACK_LAYER_Y_AXIS
						, B.STACK_LAYER_Z_AXIS                
					 FROM TB_YM_CRNWRKMTL  A 
			            , TB_YM_STACKLAYER B
					WHERE A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
					  AND A.STOCK_ID 	  = B.STOCK_ID
					  AND B.STACK_LAYER_STAT IN('C','U')
					  AND B.STACK_COL_GP NOT LIKE '__CR__'
			        ORDER BY B.STACK_LAYER_STAT DESC
			      ) A
			 WHERE ROWNUM <= 1
			 */
			jparam.setField("YD_CRN_SCH_ID", sYD_CRN_SCH_ID);
			JDTORecordSet rstLyr = commDao.select(jparam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getUpStackLayerListWithSchId");
			
			if (rstLyr.size() > 0) {

				String sUpColGp   = StringHelper.evl(rstLyr.getRecord(0).getFieldString("STACK_COL_GP"), "");
				String sUpBedGp   = StringHelper.evl(rstLyr.getRecord(0).getFieldString("STACK_BED_GP"), "");
				String sUpLayerGp = StringHelper.evl(rstLyr.getRecord(0).getFieldString("STACK_LAYER_GP"), "");

				/*
				 * 적치단 UP위치 초기화 tb_ym_stacklayer Table : stock_id = sStockId
				 * tb_ym_stacklayer Table : stack_layer_stat= 'S'(예약상태)
				 */
				/*
				UPDATE TB_YM_STACKLAYER
				   SET STOCK_ID			= :V_STOCK_ID
					 , STACK_LAYER_STAT	= :V_STACK_LAYER_STAT
					 , MODIFIER         = 'SYSTEM'
				 	 , MOD_DDTT         = SYSDATE     
				 WHERE STACK_COL_GP   = :V_STACK_COL_GP 
				   AND STACK_BED_GP   = :V_STACK_BED_GP 
				   AND STACK_LAYER_GP = :V_STACK_LAYER_GP 
				 */
				jparam.setField("STOCK_ID"        , sSTOCK_ID);
				jparam.setField("STACK_LAYER_STAT", YmConstant.YD_STK_LYR_MTL_STAT_C);
				jparam.setField("STACK_COL_GP"    , sUpColGp);
				jparam.setField("STACK_BED_GP"    , sUpBedGp);
				jparam.setField("STACK_LAYER_GP"  , sUpLayerGp);
				
				commDao.update(jparam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateCraneStackLayerStat");

			}

			/******************************
			 * 3. TO 위치 초기화
			 ******************************/
			{
				/*
				SELECT B.STACK_COL_GP,
				       B.STACK_LAYER_GP,
				       B.STACK_BED_GP,
				       B.STACK_LAYER_X_AXIS,
				       B.STACK_LAYER_Y_AXIS,
				       B.STACK_LAYER_Z_AXIS
				  FROM TB_YM_CRNWRKMTL  A 
				     , TB_YM_STACKLAYER B
				     , TB_YM_CRNSCH     C
				 WHERE A.YD_CRN_SCH_ID 	  = :V_YD_CRN_SCH_ID
				   AND A.STOCK_ID 		  = B.STOCK_ID
				   AND A.YD_CRN_SCH_ID    = C.YD_CRN_SCH_ID
				   AND B.STACK_LAYER_STAT = 'D'
				   AND B.STACK_COL_GP     = SUBSTR(C.YD_DN_WO_LOC,0,6) 
				 */
				jparam.setField("YD_CRN_SCH_ID", sYD_CRN_SCH_ID);
				JDTORecordSet layerRc = commDao.select(jparam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getPutStackLayerListWithSchId2");
				
				if (layerRc.size() > 0) {

					String sPutColGp   = StringHelper.evl(layerRc.getRecord(0).getFieldString("STACK_COL_GP"), "");
					String sPutBedGp   = StringHelper.evl(layerRc.getRecord(0).getFieldString("STACK_BED_GP"), "");
					String sPutLayerGp = StringHelper.evl(layerRc.getRecord(0).getFieldString("STACK_LAYER_GP"), "");

					String sPUT_SECT_GP = sPutColGp.substring(2, 4);

					if ("N".equals(sYD_AID_WRK_YN)

						&& ("PT01UM".equals(sYD_SCH_CD.substring(2)) || //COIL 제품출하상차
						    "PT05UM".equals(sYD_SCH_CD.substring(2)) || //COIL 제품출하상차
						    "PT02UM".equals(sYD_SCH_CD.substring(2)) || //COIL 소재이송상차
						    "PT06UM".equals(sYD_SCH_CD.substring(2)) || //COIL 소재이송상차
						    "PT03UM".equals(sYD_SCH_CD.substring(2)) || //COIL 제품이송상차
						    "PT07UM".equals(sYD_SCH_CD.substring(2))    //COIL 제품이송상차								
						)) {
						
						
						/*
						SELECT *
						  FROM TB_YM_STACKLAYER
						 WHERE STACK_COL_GP   = :V_STACK_COL_GP
						   AND STACK_BED_GP	  = :V_STACK_BED_GP 
						   AND STACK_LAYER_GP = :V_STACK_LAYER_GP 
						   AND STOCK_ID       = :V_STOCK_ID
						 */
						jparam.setField("STACK_COL_GP"  , sPutColGp);
						jparam.setField("STACK_BED_GP"  , sPutBedGp);
						jparam.setField("STACK_LAYER_GP", sPutLayerGp);
						jparam.setField("STOCK_ID"      , sSTOCK_ID);
					   	JDTORecordSet lyrJr = commDao.select(jparam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getStackLayerInfoWithPkCHK");
					   						   	
			    		if (lyrJr.size() > 0){
							/*
							 * 적치단 UP위치 Clear tb_ym_stacklayer Table : stock_id =
							 * ''(Empty) tb_ym_stacklayer Table : stack_layer_stat =
							 * 'E'(적치가능)
							 */
							/*
							UPDATE TB_YM_STACKLAYER
							   SET STOCK_ID			= :V_STOCK_ID
								 , STACK_LAYER_STAT	= :V_STACK_LAYER_STAT
								 , MODIFIER         = 'SYSTEM'
							 	 , MOD_DDTT         = SYSDATE     
							 WHERE STACK_COL_GP   = :V_STACK_COL_GP 
							   AND STACK_BED_GP   = :V_STACK_BED_GP 
							   AND STACK_LAYER_GP = :V_STACK_LAYER_GP 	 
							*/				    			
							jparam.setField("STOCK_ID"        , "");
							jparam.setField("STACK_LAYER_STAT", YmConstant.YD_STK_LYR_MTL_STAT_E);
							jparam.setField("STACK_COL_GP"    , sPutColGp);
							jparam.setField("STACK_BED_GP"    , sPutBedGp);
							jparam.setField("STACK_LAYER_GP"  , sPutLayerGp);
							
			    			commDao.update(jparam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateCraneStackLayerStat");
	
							/*
							 * 적치단 PUT 위치 CLOSE tb_ym_stacklayer Table :
							 * STACK_LAYER_ACTIVE_STAT_C = 'C'(적치가능)
							 */
							if ("PT01UM".equals(sYD_SCH_CD.substring(2))// Coil 제품출하상차
							  ||"PT05UM".equals(sYD_SCH_CD.substring(2))) {
								/*
								UPDATE TB_YM_STACKLAYER
								   SET STACK_LAYER_ACTIVE_STAT = :V_STACK_LAYER_ACTIVE_STAT
								     , MODIFIER   = 'SYSTEM'
								     , MOD_DDTT   = SYSDATE     
								 WHERE STACK_COL_GP   = :V_STACK_COL_GP 
								   AND STACK_BED_GP   = :V_STACK_BED_GP 
								   AND STACK_LAYER_GP = :V_STACK_LAYER_GP 
								 */
								jparam.setField("STACK_LAYER_ACTIVE_STAT", YmConstant.STACK_LAYER_ACTIVE_STAT_E);
								jparam.setField("STACK_COL_GP"           , sPutColGp);
								jparam.setField("STACK_BED_GP"           , sPutBedGp);
								jparam.setField("STACK_LAYER_GP"         , sPutLayerGp);
								commDao.update(jparam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateCraneStackLayerActivStat");
								
							}else{
								jparam.setField("STACK_LAYER_ACTIVE_STAT", YmConstant.STACK_LAYER_ACTIVE_STAT_C);
								jparam.setField("STACK_COL_GP"           , sPutColGp);
								jparam.setField("STACK_BED_GP"           , sPutBedGp);
								jparam.setField("STACK_LAYER_GP"         , sPutLayerGp);
								commDao.update(jparam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateCraneStackLayerActivStat");
							}

			    		}

					} else { 
						/*
						SELECT *
						  FROM TB_YM_STACKLAYER
						 WHERE STACK_COL_GP   = :V_STACK_COL_GP
						   AND STACK_BED_GP	  = :V_STACK_BED_GP 
						   AND STACK_LAYER_GP = :V_STACK_LAYER_GP 
						   AND STOCK_ID       = :V_STOCK_ID
						 */						
						jparam.setField("STACK_COL_GP"  , sPutColGp);
						jparam.setField("STACK_BED_GP"  , sPutBedGp);
						jparam.setField("STACK_LAYER_GP", sPutLayerGp);
						jparam.setField("STOCK_ID"      , sSTOCK_ID);
					   	JDTORecordSet lyrJr = commDao.select(jparam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getStackLayerInfoWithPkCHK");
					   	
			    		if (lyrJr.size() > 0){
							/*
							 * 적치단 UP위치 Clear tb_ym_stacklayer Table : stock_id =
							 * ''(Empty) tb_ym_stacklayer Table : stack_layer_stat =
							 * 'E'(적치가능)
							 */
			    			/*
							UPDATE TB_YM_STACKLAYER
							   SET STOCK_ID			= :V_STOCK_ID
								 , STACK_LAYER_STAT	= :V_STACK_LAYER_STAT
								 , MODIFIER         = 'SYSTEM'
							 	 , MOD_DDTT         = SYSDATE     
							 WHERE STACK_COL_GP   = :V_STACK_COL_GP 
							   AND STACK_BED_GP   = :V_STACK_BED_GP 
							   AND STACK_LAYER_GP = :V_STACK_LAYER_GP 	 
							*/				    			
							jparam.setField("STOCK_ID"        , "");
							jparam.setField("STACK_LAYER_STAT", YmConstant.YD_STK_LYR_MTL_STAT_E);
							jparam.setField("STACK_COL_GP"    , sPutColGp);
							jparam.setField("STACK_BED_GP"    , sPutBedGp);
							jparam.setField("STACK_LAYER_GP"  , sPutLayerGp);
							
			    			commDao.update(jparam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateCraneStackLayerStat");
	
			    		}
					}
				}

			}
			
			/**
			 * 4. 크레인 설비 초기화
			 */
			{
				if (YmConstant.SCH_WORK_STAT_1.equals(sYD_WRK_PROG_STAT)) {
					/*
					UPDATE TB_YM_EQUIP 
					   SET WPROG_STAT 			 = :V_WPROG_STAT       -- 작업진행상태
					     , CARLOAD_SCH_WORK_KIND = :V_CARLOAD_SCH_WORK_KIND -- 스케쥴코드
					     , MODIFIER   = 'SYSTEM'
					     , MOD_DDTT   = SYSDATE     
					WHERE YD_GP 		= :V_YD_GP
					  AND BAY_GP  		= :V_BAY_GP
					  AND EQUIP_KIND 	= :V_EQUIP_KIND
					  AND EQUIP_NO 		= :V_EQUIP_NO
					 */
					jparam.setField("WPROG_STAT"           , YmConstant.WORK_PROG_STAT_W);
					jparam.setField("CARLOAD_SCH_WORK_KIND", "");
					jparam.setField("YD_GP"                , StringHelper.evl(schInfo.getFieldString("YD_GP"), ""));
					jparam.setField("BAY_GP"               , StringHelper.evl(schInfo.getFieldString("BAY_GP"), ""));
					jparam.setField("EQUIP_KIND"           , YmConstant.EQUIP_KIND_CR);
					jparam.setField("EQUIP_NO"             , StringHelper.evl(schInfo.getFieldString("SCH_WORK_EQUIP_NO"), ""));
					
					commDao.update(jparam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateSubCraneEquipStat");
					commUtils.printLog("", "=스케쥴 취소=>크레인 설비 초기화.", "[INFO]");
				}
			}
			
			/**
			 * 5. 작업예약  TO위치  정보 삭제
			 */
			{	
				if ("PT02".equals(sYD_SCH_CD.substring(2, 6)) || //COIL소재차량이송상하차
					"PT03".equals(sYD_SCH_CD.substring(2, 6))) {  //COIL제품차량이송상하차
					/*
					UPDATE TB_YM_WRKBOOK
					   SET YD_TO_LOC_DCSN_MTD = 'S'
					     , YD_TO_LOC_GUIDE    = NULL   
					 WHERE YD_WBOOK_ID = (SELECT YD_WBOOK_ID 
					                        FROM USRYMA.TB_YM_CRNSCH
					                       WHERE YD_CRN_SCH_ID =:V_YD_CRN_SCH_ID)
					 */
					jparam.setField("YD_CRN_SCH_ID", sYD_CRN_SCH_ID);
					commDao.update(jparam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateputlocWbookId");
					
					commUtils.printLog("", "=스케쥴 취소=>작업예약  to위치  정보 삭제.", "[INFO]");
				}
			}
			
			/**
			 * 5. 스케쥴 정보 삭제
			 */
			{
				/*
				UPDATE TB_YM_CRNSCH
				   SET DEL_YN = 'Y'
				 WHERE YD_CRN_SCH_ID =: V_YD_CRN_SCH_ID
				 */
				jparam.setField("YD_CRN_SCH_ID", sYD_CRN_SCH_ID);
				commDao.update(jparam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.deleteSchInfo");
				commUtils.printLog("", "=스케쥴 취소=>스케쥴 정보 삭제.", "[INFO]");
			}

			
			return jrRtn;
		} catch (DAOException daoe) {
			throw daoe;
		} 

	}	
	

	/**
	 * 산적위치 수정 화면에서 From 위치와 To 위치 정보를 실적 처리한다.
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param String :  저장품ID
	 * @param String :  UP LOC
	 * @param String :  PUT LOC
	 * @param String :  스케쥴 코드
	 * @param String :  야드구분
	 * @throws Exception 
	 * 
	 */
	public int insertUpPutWrslRtData(String sStockId, String sUpLoc, String sPutLoc, String sYdGp , String sUserId) throws Exception {
		int iSeq = -1;
	
		try {
			String scrane_sch_id   = "";
			String scrane_stock_id = "";
			String scrane_equip_gp = "";
			String scrane_sch_code = "";
			String scrane_up_loc   = "";
			String scrane_put_loc  = "";
			String scrane_register = "";
			String scrane_modifier = "";
			String scrane_yd_gp    = "";
			String scrane_work_duty  = "";
			String scrane_work_party = "";
			String sch_wdemand_duty  = "";
			String sch_wdemand_party = "";
	
			scrane_sch_id 	= "000000000000000000";
			scrane_stock_id = sStockId.trim();
			scrane_equip_gp = "";
			scrane_up_loc 	= sUpLoc;
			scrane_put_loc 	= sPutLoc;
			scrane_register = sUserId;
			scrane_modifier = sUserId;
			scrane_yd_gp 	= sYdGp;
			
			String sUpBay  = sUpLoc.length()  > 2 ? sUpLoc.substring(1, 2) : "";
			String sPutBay = sPutLoc.length() > 2 ? sPutLoc.substring(1, 2)	: "";
			
			scrane_sch_code = "3X9999"; //산적위치수정 임의스케줄
			scrane_equip_gp = sYdGp + sPutBay + YmConstant.EQUIP_KIND_CR + "00";
	
			scrane_work_duty  = YmCommUtils.getWorkDuty();
			scrane_work_party = YmCommUtils.getWorkParty();
			sch_wdemand_duty  = YmCommUtils.getWorkDuty();
			sch_wdemand_party = YmCommUtils.getWorkParty();
			
			/*
			INSERT INTO TB_YM_WRSLT (
			      CRANE_WRSLT_ID 
			    , SCH_ID   
			    , STOCK_ID  
			    , EQUIP_GP  
			    , YD_SCH_CD--SCH_WORK_KIND     
			    , CRANE_WORK_DDTT      
			    , CRANE_WORK_DUTY
			    , CRANE_WORK_PARTY
			    , CRANE_WORD_DDTT   
			    , CRANE_WRSLT_CD  
			    , SCH_WPREFER
			    , SCH_WDEMAND_DDTT
			    , SCH_WDEMAND_DUTY
			    , SCH_WDEMAND_PARTY
			    , CRANE_WORD_UP_LOC   
			    , CRANE_WORD_PUT_LOC
			    , CRANE_WRSLT_UP_LOC   
			    , CRANE_WRSLT_UP_FUNC   
			    , CRANE_WRSLT_UP_DDTT 
			    , CRANE_WRSLT_PUT_LOC 
			    , CRANE_WRSLT_PUT_FUNC	
			    , CRANE_WRSLT_PUT_DDTT 
			    , REGISTER    
			    , REG_DDTT    
			    , MODIFIER     
			    , MOD_DDTT    
			    , DEL_YN
			    , YD_GP
			) VALUES (
			      TO_CHAR(SYSDATE, 'YYYYMMDDHH24MI')||YM_WRSLT_SEQ.NEXTVAL 
			    , :V_SCH_ID    
			    , :V_STOCK_ID     
			    , :V_EQUIP_GP    
			    , :V_YD_SCH_CD 
			    , TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS')   
			    , :V_SCRANE_WORK_DUTY
			    , :V_SCRANE_WORK_PARTY
			    , TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS') 
			    , 'N' 
			    , '1'
			    , TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS') 
			    , :V_SCH_WDEMAND_DUTY
			    , :V_SCH_WDEMAND_PARTY
			    , '' --UP_LOC  
			    , :V_PUT_LOC
			    , :V_UP_LOC 
			    , :V_UP_FUNC 
			    , TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS') 
			    , :V_PUT_LOC 
			    , :V_PUT_FUNC 
			    , TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS') 
			    , :V_REGISTER 
			    , SYSDATE  
			    , :V_MODIFIER 
			    , SYSDATE 
			    , 'N'
			    , :V_YD_GP
			)
			 */
	
			JDTORecord jparam = JDTORecordFactory.getInstance().create();
	
			jparam.setField("SCH_ID"           , scrane_sch_id);
			jparam.setField("STOCK_ID"         , scrane_stock_id);
			jparam.setField("EQUIP_GP"         , scrane_equip_gp);
			jparam.setField("YD_SCH_CD"        , scrane_sch_code);
			jparam.setField("SCRANE_WORK_DUTY" , scrane_work_duty);
			jparam.setField("SCRANE_WORK_PARTY", scrane_work_party);
			jparam.setField("SCH_WDEMAND_DUTY" , sch_wdemand_duty);
			jparam.setField("SCH_WDEMAND_PARTY", sch_wdemand_party);
			jparam.setField("UP_LOC"           , scrane_up_loc);
			jparam.setField("PUT_LOC"          , scrane_put_loc);
			jparam.setField("UP_FUNC"          , YmConstant.CRANE_FUNC_S);
			jparam.setField("PUT_FUNC"         , YmConstant.CRANE_FUNC_S);
			jparam.setField("YD_GP"            , scrane_yd_gp);
			jparam.setField("REGISTER"         , scrane_register);
			jparam.setField("MODIFIER"         , scrane_modifier);
			
			commDao.insert(jparam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.insertCrnWrslt");
			
		} catch (DAOException daoe) {
			throw daoe;
		} 
		return iSeq;
	}
	
	
	
	/**
	 * @throws JDTOException 
	 * 코일 공통 테이블의 진도코드를 참조해서 
	 * 저장품이동조건을 가져온다.
	 *
	 * @param  String	:	저장품ID
	 * @return JDTORecord
	 * @throws  
	 */			 
	public JDTORecord getCoilCurrProgCd(String sStockId) throws JDTOException {	
		
		String sProgCd   = "";
		String sStocMv   = "";
		String sReturnGp = "";
			
		JDTORecord jRtn = JDTORecordFactory.getInstance().create();
		
		try {
			JDTORecord jparam = JDTORecordFactory.getInstance().create();
			
			/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCoilCommonInfo
			SELECT (CASE WHEN YD_GP ='1' THEN 'A' ELSE HR_PLNT_GP END) 		AS PLNT_GP
			     , HR_PLNT_GP                                       -- 공장구분
				 , REPLACE(PASS_PROC1,'6R','6K')        AS PASS_PROC1   
				 , REPLACE(PASS_PROC2,'6R','6K')        AS PASS_PROC2   
				 , REPLACE(PASS_PROC3,'6R','6K')        AS PASS_PROC3   
				 , REPLACE(PASS_PROC4,'6R','6K')        AS PASS_PROC4   
				 , REPLACE(PASS_PROC5,'6R','6K')        AS PASS_PROC5   
			     , ORD_NO 		                                    -- 제작번호
			     , ORD_DTL 		                                    -- 제작행번
			     , COIL_T 			                                -- 코일두께
			     , COIL_W 			                                -- 코일폭
			     , CURR_COIL_LEN	                                -- 코일길이
			     , COIL_INDIA 		                                -- 코일내경
			     , COIL_OUTDIA 	                                    -- 코일외경
			     , DECODE(COIL_WT,0,NET_CAL_WT,COIL_WT) AS COIL_WT  -- 코일중량
			     , NEXT_PROC 		                                -- 차공정
			     , PLAN_PROC1                                       -- 계획공정
			     , BRANCH_CD 		                                -- 분기위치코드
			     , EXTEND_CONVEYOR_BRANCH_CD                        -- 확장분기위치코드
			     , HYSCO_TRANS_GP 	                                -- HYSCO이송수단
			     , COOL_METHOD 	                                    -- 냉각방법
			     , DECODE(CURR_PROG_CD,'2','H','3','D','4','E','6','L','7','K',CURR_PROG_CD) AS CURR_PROG_CD
			     , RETURN_GP
			FROM  TB_PT_COILCOMM 
			WHERE COIL_NO = :V_COIL_NO
			 */
			jparam.setField("COIL_NO", sStockId);
			JDTORecordSet jtR = commDao.select(jparam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCoilCommonInfo");
			
			if (jtR.size() > 0){
				sProgCd 	= StringHelper.evl(jtR.getRecord(0).getFieldString("CURR_PROG_CD"), "");
				sReturnGp	= StringHelper.evl(jtR.getRecord(0).getFieldString("RETURN_GP"), "");
			}
			
			// 일관제철 진도코드
			if (YmConstant.CURR_PROG_CD_COIL_A.equals(sProgCd)||
				YmConstant.CURR_PROG_CD_COIL_R.equals(sProgCd)){
				sStocMv   = YmConstant.NEW_STOCK_MOVE_TERM_AC;
			} else if (YmConstant.CURR_PROG_CD_COIL_B.equals(sProgCd)) {
				sStocMv   = YmConstant.NEW_STOCK_MOVE_TERM_BC;
			} else if (YmConstant.CURR_PROG_CD_COIL_C.equals(sProgCd)) {
				sStocMv   = YmConstant.NEW_STOCK_MOVE_TERM_CC;
			} else if (YmConstant.CURR_PROG_CD_COIL_D.equals(sProgCd)) {
				sStocMv   = YmConstant.NEW_STOCK_MOVE_TERM_DC;
			} else if (YmConstant.CURR_PROG_CD_COIL_E.equals(sProgCd)) {
				sStocMv   = YmConstant.NEW_STOCK_MOVE_TERM_CS;
			} else if (YmConstant.CURR_PROG_CD_COIL_F.equals(sProgCd)) {
				sStocMv   = YmConstant.NEW_STOCK_MOVE_TERM_FC;
			} else if (YmConstant.CURR_PROG_CD_COIL_K.equals(sProgCd)) {
				sStocMv   = YmConstant.NEW_STOCK_MOVE_TERM_KG;
			} else if (YmConstant.CURR_PROG_CD_COIL_G.equals(sProgCd)) {
				sStocMv   = YmConstant.NEW_STOCK_MOVE_TERM_GC;
			} else if (YmConstant.CURR_PROG_CD_COIL_H.equals(sProgCd)) {
				sStocMv   = YmConstant.NEW_STOCK_MOVE_TERM_HG;
			} else if (YmConstant.CURR_PROG_CD_COIL_J.equals(sProgCd)) {
	
				if (YmConstant.RETURN_GP_1.equals(sReturnGp)){
					sStocMv   = YmConstant.NEW_STOCK_MOVE_TERM_JR;
				} else {
					sStocMv   = YmConstant.NEW_STOCK_MOVE_TERM_JG;
				}
			} else if (YmConstant.CURR_PROG_CD_COIL_L.equals(sProgCd)) {//코일제품상차지시 
				sStocMv   = YmConstant.NEW_STOCK_MOVE_TERM_LG;
			} else if (YmConstant.CURR_PROG_CD_COIL_N.equals(sProgCd)) {
				sStocMv   = YmConstant.NEW_STOCK_MOVE_TERM_NG;
			} else if (YmConstant.CURR_PROG_CD_COIL_M.equals(sProgCd)||
					YmConstant.CURR_PROG_CD_COIL_P.equals(sProgCd)) {
				sStocMv   = YmConstant.NEW_STOCK_MOVE_TERM_MG;
			} else if (YmConstant.CURR_PROG_CD_COIL_X.equals(sProgCd)) {
				sStocMv   = YmConstant.NEW_STOCK_MOVE_TERM_XG;	
			} else if (YmConstant.CURR_PROG_CD_COIL_Y.equals(sProgCd)) {
				sStocMv   = YmConstant.NEW_STOCK_MOVE_TERM_YG;
			} else if (YmConstant.CURR_PROG_CD_COIL_Z.equals(sProgCd)) {
				sStocMv   = YmConstant.NEW_STOCK_MOVE_TERM_ZG;
			}															
			
			jRtn.setField("CURR_PROG_CD"   , sProgCd);
			jRtn.setField("STOCK_MOVE_TERM", sStocMv);

		} catch(DAOException de) {
			throw de;
		} 
	    return jRtn;
	}

    
	/**
	 * 산적위치수정 - 삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord delStkLoc(GridData gdReq) throws DAOException {
		String methodNm = "산적위치수정-삭제[BCoilJspSeEJB.delStkLoc] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			String sYD_GP            = gdReq.getParam("YD_GP");
			String sSTOCK_ID         = gdReq.getParam("STOCK_ID");
			String sYD_WRKBOOK_YN    = gdReq.getParam("YD_WRKBOOK_YN"   );
			String sYD_CARSCH_YN     = gdReq.getParam("YD_CARSCH_YN"    );
			String sYD_TCARSCH_YN    = gdReq.getParam("YD_TCARSCH_YN"   );
			String sFROM_ADDR        = gdReq.getParam("FROM_ADDR");  //from 위치
			String sMODIFIER         = gdReq.getParam("userid");
			
			//Return Value
			JDTORecord jrRtn = null;
			
			/*****************************************************
			 * 저장품제원 : 코일야드L2로 송신(YMA7L002)
			 ******************************************************/
			commUtils.printLog(logId, "YMA7L002 JMS전송", "[INFO]");
			
			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setField("TC_CD"          , "YMA7L002");
			jrYdMsg.setField("MSG_GP"         , "D"); //삭제일경우 D로 보냄 20170904
			jrYdMsg.setField("YD_INFO_SYNC_CD", "5");
			jrYdMsg.setField("STOCK_ID"       , sSTOCK_ID);

			jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA7L002", jrYdMsg));

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			/*************************
			 * 산적위치수정-삭제
			 ***************************/
			/*
			UPDATE TB_YM_STACKLAYER
			   SET STOCK_ID         = NULL
			     , STACK_LAYER_STAT = 'E'
			     , MODIFIER = :V_MODIFIER
			     , MOD_DDTT = SYSDATE
			 WHERE STOCK_ID = :V_STOCK_ID
			 */
			jrParam.setField("STOCK_ID", sSTOCK_ID);
			commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updStackLayer", logId, methodNm, "산적위치수정-삭제");
			
			/*************************
			 * 작업예약 삭제
			 ***************************/
			if ("Y".equals(sYD_WRKBOOK_YN)) {
				/*  
				SELECT YD_WBOOK_ID
				  FROM TB_YM_WRKBOOKMTL
				 WHERE STOCK_ID = :V_STOCK_ID
				   AND DEL_YN = 'N'
				*/
				JDTORecordSet rst = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getWrkBookIdByStockId");
				
				if (rst.size() > 0) {
					jrParam.setField("YD_WBOOK_ID", rst.getRecord(0).getFieldString("YD_WBOOK_ID"));
					/*
					UPDATE TB_YM_WRKBOOKMTL
					   SET MODIFIER    = :V_MODIFIER
					      ,MOD_DDTT    = SYSDATE
					      ,DEL_YN      = 'Y'
					 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
					   AND DEL_YN      = 'N'
					 */
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updDelYnWrkBookMtl");
					/*
					UPDATE TB_YM_WRKBOOK
					   SET MODIFIER    = :V_MODIFIER
					      ,MOD_DDTT    = SYSDATE
					      ,DEL_YN      = 'Y'
					 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
					   AND DEL_YN      = 'N' 
					 */
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updDelYnWrkBook");
				}
			}
			/*******************
			 * 대차스케줄 삭제
			 ********************/
			if ("Y".equals(sYD_TCARSCH_YN)) {
				/*
				SELECT COUNT(*) AS CNT
				     , YD_TCAR_SCH_ID
				  FROM TB_YM_TCARFTMVMTL
				 WHERE YD_TCAR_SCH_ID IN(SELECT YD_TCAR_SCH_ID
				                          FROM TB_YM_TCARFTMVMTL
				                         WHERE STOCK_ID = :V_STOCK_ID
				                           AND DEL_YN   = 'N'
				                        )
				   AND DEL_YN = 'N'
				 GROUP BY YD_TCAR_SCH_ID
				 */
				JDTORecordSet rstTcar = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarFtMvMtlByStockId");
				
				if (rstTcar.size() > 0) {
					jrParam.setField("YD_TCAR_SCH_ID", rstTcar.getRecord(0).getFieldString("YD_TCAR_SCH_ID"));
					/*
					UPDATE TB_YM_TCARFTMVMTL
					   SET MODIFIER    = :V_MODIFIER
					      ,MOD_DDTT    = SYSDATE
					      ,DEL_YN      = 'Y'
					 WHERE YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
					   AND STOCK_ID       = :V_STOCK_ID
					   AND DEL_YN         = 'N'
					 */
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updDelTcarFtMvMtl", logId, methodNm, "대차재료 삭제");
					
					if ("1".equals(rstTcar.getRecord(0).getFieldString("CNT"))) {
						/*
						UPDATE TB_YM_TCARSCH
						   SET MODIFIER    = :V_MODIFIER
						      ,MOD_DDTT    = SYSDATE
						      ,DEL_YN      = 'Y'
						 WHERE YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
						   AND DEL_YN         = 'N' 
						 */
						commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updDelTcarSch", logId, methodNm, "대차스케줄 삭제");
					}
				}
			}
			/**************************
			 * 차량스케줄 삭제
			 ***************************/
			if ("Y".equals(sYD_CARSCH_YN)) {
				/*
				SELECT COUNT(*) AS CNT
				     , YD_CAR_SCH_ID
				  FROM USRYDA.TB_YD_CARFTMVMTL
				 WHERE YD_CAR_SCH_ID IN(SELECT YD_CAR_SCH_ID
				                          FROM USRYDA.TB_YD_CARFTMVMTL
				                         WHERE STL_NO = :V_STOCK_ID
				                           AND DEL_YN = 'N'
				                       )
				   AND DEL_YN = 'N'
				 GROUP BY YD_CAR_SCH_ID
				 */
				JDTORecordSet rstCar = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCarFtMvMtlByStockId");
				
				if (rstCar.size() > 0) {
					jrParam.setField("YD_CAR_SCH_ID", rstCar.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
					/*
					UPDATE USRYDA.TB_YD_CARFTMVMTL
					   SET MODIFIER    = :V_MODIFIER
					      ,MOD_DDTT    = SYSDATE
					      ,DEL_YN      = 'Y'
					 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
					   AND STL_NO        = :V_STOCK_ID
					   AND DEL_YN        = 'N'
					 */
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updDelCarFtMvMtl");
					
					if ("1".equals(rstCar.getRecord(0).getFieldString("CNT"))) {
						/*
						UPDATE USRYDA.TB_YD_CARSCH
						   SET MODIFIER    = :V_MODIFIER
						      ,MOD_DDTT    = SYSDATE
						      ,DEL_YN      = 'Y'
						 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
						   AND DEL_YN        = 'N' 
						 */
						commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updDelCarSch");
					}
				}
			}
				
			/**************************
			 * 실적테이블에 저장
			 **************************/
			this.insertUpPutWrslRtData(sSTOCK_ID, sFROM_ADDR, "", sYD_GP, sMODIFIER);//
			
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of delStkLoc    

	
	
	/**
	 * 저장영역별검색순서조회 - 저장
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updStrAreaSrchSeq(GridData gdReq) throws DAOException {
		String methodNm = "저장영역별검색순서조회 저장[BCoilJspSeEJB.updStrAreaSrchSeq] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;
			JDTORecordSet rsResult = null;
			String sCRUD = "";
			
			String sEQP_PRIOR  = gdReq.getParam("EQP_PRIOR");
			String sYARD_PRIOR = gdReq.getParam("YARD_PRIOR");
			String sCAR_PRIOR  = gdReq.getParam("CAR_PRIOR");
			
			String sSORT       = gdReq.getParam("SORT"  ); //적용유무
			String sARR_RT     = gdReq.getParam("ARR_RT"); //복사대상 행선
			
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setField("YD_SCH_CD"  , gdReq.getParam("YD_SCH_CD"  )); //스케줄코드
//			jrParam.setField("YD_ROUTE_GP", gdReq.getParam("YD_ROUTE_GP")); //행선
			
			
			/* 복사할 행선 목록 조회
			 SELECT REGEXP_SUBSTR(ITEMS, '[^,]+', 1, LEVEL) AS ITEM
			   FROM (SELECT :V_ARR_ITEM AS ITEMS FROM DUAL)
			CONNECT BY REGEXP_SUBSTR(ITEMS, '[^,]+', 1, LEVEL) IS NOT NULL
			 */
			jrParam.setField("ARR_ITEM", sARR_RT);
			JDTORecordSet jrRtList = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getArrToUnit", logId, methodNm, "복사행선목록 조회");
			
			//수정할 레코드 수(전체로 넘어옴)
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			 
			// 행선별 반복 
			for (int i = 0; i < jrRtList.size(); ++i) {
				commUtils.printLog(logId, "○○○["+i+"]"+ jrRtList.getRecord(i).getFieldString("ITEM") , "[info]");
				jrParam.setField("YD_ROUTE_GP", jrRtList.getRecord(i).getFieldString("ITEM")); //행선
				
				String sSPAN_CD = "";
	
				int nMaxEQP  = 0;
				int nMaxYARD = 0;
				int nMaxCAR  = 0;
				boolean bSortGp = false;
				 
				// 우선순위별 개수 조회
				for (int idx = 0; idx < rowCnt; ++idx) {
					sSPAN_CD = commUtils.getValue(gdReq, "STACK_COL_GP", idx).substring(2, 4);
					bSortGp  = Pattern.matches("^[a-zA-Z]*$", sSPAN_CD);
					
					if (bSortGp) {
						if ("PT".equals(sSPAN_CD)) {
							nMaxCAR++;
						} else {
							nMaxEQP++;
						}
					} else {
						nMaxYARD++;
					}
				}
	
				int nStartEQP  = 1;
				int nStartYARD = 1;
				int nStartCAR  = 1;
				
				if ("1".equals(sEQP_PRIOR)) {
					nStartEQP = 1;
				} else if ("3".equals(sEQP_PRIOR)) {
					nStartEQP = nMaxYARD + nMaxCAR + 1;
				} else {
					if ("1".equals(sYARD_PRIOR)) {
						nStartEQP = nMaxYARD + 1;
					} else {
						nStartEQP = nMaxCAR + 1;
					}
				} 
				
				if ("1".equals(sYARD_PRIOR)) {
					nStartYARD = 1;
				} else if ("3".equals(sYARD_PRIOR)) {
					nStartYARD = nMaxEQP + nMaxCAR + 1;
				} else {
					if ("1".equals(sEQP_PRIOR)) {
						nStartYARD = nMaxEQP + 1;
					} else {
						nStartYARD = nMaxCAR + 1;
					}
				}
				
				if ("1".equals(sCAR_PRIOR)) {
					nStartCAR = 1;
				} else if ("3".equals(sCAR_PRIOR)) {
					nStartCAR = nMaxYARD + nMaxEQP + 1;
				} else {
					if ("1".equals(sYARD_PRIOR)) {
						nStartCAR = nMaxYARD + 1;
					} else {
						nStartCAR = nMaxEQP + 1;
					}
				}
			
				if ("Y".equals(sSORT)) {
					/* 
					 MERGE INTO TB_YM_SCHLOCSRCHPRIOR SP USING (
					 SELECT :V_YD_SCH_PRFR_PRIOR AS YD_SCH_PRFR_PRIOR
					      , :V_MODIFIER          AS MODIFIER
					      , SYSDATE              AS MOD_DDTT
					      , :V_YD_SCH_CD         AS YD_SCH_CD
					      , :V_YD_ROUTE_GP       AS YD_ROUTE_GP
					   FROM DUAL
					 ) DD ON (SP.YD_SCH_CD = DD.YD_SCH_CD AND SP.YD_ROUTE_GP = DD.YD_ROUTE_GP)
					
					WHEN NOT MATCHED THEN
					    INSERT (
					           YD_SCH_CD         , YD_ROUTE_GP      , REGISTER
					         , REG_DDTT          , MODIFIER         , MOD_DDTT
					         , YD_SCH_PRFR_PRIOR , DEL_YN
					         )
					    VALUES (
					           DD.YD_SCH_CD      , DD.YD_ROUTE_GP   , DD.MODIFIER
					         , DD.MOD_DDTT       , DD.MODIFIER      , DD.MOD_DDTT
					         , DD.YD_SCH_PRFR_PRIOR , 'N'
					         )
					WHEN MATCHED THEN 
					     UPDATE SET
					      SP.MODIFIER       = DD.MODIFIER
					    , SP.MOD_DDTT       = DD.MOD_DDTT
					    , SP.YD_SCH_PRFR_PRIOR = DD.YD_SCH_PRFR_PRIOR
					 */
					jrParam.setField("YD_SCH_PRFR_PRIOR" , sYARD_PRIOR+sEQP_PRIOR+sCAR_PRIOR);
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updSchPrfrPrior", logId, methodNm, "스케줄우선순위 수정");
				}
				
				commUtils.printLog(logId, "[EQP ] : "+sEQP_PRIOR +" MAX:"+nMaxEQP +" START:"+nStartEQP , "[info]");
				commUtils.printLog(logId, "[YARD] : "+sYARD_PRIOR+" MAX:"+nMaxYARD+" START:"+nStartYARD, "[info]");
				commUtils.printLog(logId, "[CAR ] : "+sCAR_PRIOR +" MAX:"+nMaxCAR +" START:"+nStartCAR , "[info]");
				
				
				//전체 삭제
				/*
				UPDATE TB_YM_SCHLOCSRCH
				   SET DEL_YN   = 'Y'
				     , MODIFIER = :V_MODIFIER
				     , MOD_DDTT = SYSDATE
				 WHERE YD_SCH_CD    = :V_YD_SCH_CD
			  	   AND YD_ROUTE_GP  = :V_YD_ROUTE_GP
				 */
				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.delSchLocSrch", logId, methodNm, "저장위치 삭제");
				
				for (int ii = 0; ii < rowCnt; ii++) {
					sSPAN_CD = commUtils.getValue(gdReq, "STACK_COL_GP", ii).substring(2, 4);
					bSortGp  = Pattern.matches("^[a-zA-Z]*$", sSPAN_CD);
					if (bSortGp) {
						if ("PT".equals(sSPAN_CD)) {
							jrParam.setField("YD_LOC_SRCH_RNG_SEQ" , nStartCAR+"");
							nStartCAR++;
						} else {
							jrParam.setField("YD_LOC_SRCH_RNG_SEQ" , nStartEQP+"");
							nStartEQP++;
						}
					} else {
						jrParam.setField("YD_LOC_SRCH_RNG_SEQ" , nStartYARD+"");
						nStartYARD++;
					}
	
					if ("N".equals(sSORT)) {
						jrParam.setField("YD_LOC_SRCH_RNG_SEQ" , commUtils.getValue(gdReq, "YD_LOC_SRCH_RNG_SEQ" , ii)); //
					}
					jrParam.setField("STACK_COL_GP"        , commUtils.getValue(gdReq, "STACK_COL_GP"        , ii)); //
					
					sCRUD = commUtils.getValue(gdReq, "CRUD", ii);
					
//					if ("U".equals(sCRUD) || "".equals(sCRUD)) {
						/*
						UPDATE TB_YM_SCHLOCSRCH
						   SET YD_LOC_SRCH_RNG_SEQ = :V_YD_LOC_SRCH_RNG_SEQ
						     , MODIFIER = :V_MODIFIER
						     , MOD_DDTT = SYSDATE
						     , DEL_YN   = 'N'
						 WHERE YD_SCH_CD    = :V_YD_SCH_CD
						   AND STACK_COL_GP = :V_STACK_COL_GP
						   AND YD_ROUTE_GP  = :V_YD_ROUTE_GP
						 */
//						commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updSchLocSrch");
//					}
					
//					if ("C".equals(sCRUD)) {
						/*
						SELECT YD_SCH_CD
						     , STACK_COL_GP
						     , REGISTER
						     , REG_DDTT
						     , MODIFIER
						     , MOD_DDTT
						     , DEL_YN
						     , YD_LOC_SRCH_RNG_SEQ
						  FROM TB_YM_SCHLOCSRCH
						 WHERE YD_SCH_CD   = :V_YD_SCH_CD
						   AND YD_ROUTE_GP = :V_YD_ROUTE_GP
						 */
						rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getSchLocSrchAll", logId, methodNm, "");
						if (rsResult.size() > 0) {
							/*
							UPDATE TB_YM_SCHLOCSRCH
							   SET YD_LOC_SRCH_RNG_SEQ = :V_YD_LOC_SRCH_RNG_SEQ
							     , MODIFIER = :V_MODIFIER
							     , MOD_DDTT = SYSDATE
							     , DEL_YN   = 'N'
							 WHERE YD_SCH_CD    = :V_YD_SCH_CD
							   AND STACK_COL_GP = :V_STACK_COL_GP
							   AND YD_ROUTE_GP  = :V_YD_ROUTE_GP
							 */
							commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updSchLocSrch");
						} else {
							/*
							INSERT INTO TB_YM_SCHLOCSRCH
							(
							  YD_SCH_CD
							, STACK_COL_GP
							, REGISTER
							, REG_DDTT
							, MODIFIER
							, MOD_DDTT
							, DEL_YN
							, YD_LOC_SRCH_RNG_SEQ
							, YD_ROUTE_GP
							) VALUES (
							  :V_YD_SCH_CD
							, :V_STACK_COL_GP
							, :V_MODIFIER
							, SYSDATE
							, :V_MODIFIER
							, SYSDATE
							, 'N'
							, :V_YD_LOC_SRCH_RNG_SEQ
							, :V_YD_ROUTE_GP
							)
							 */
							commDao.insert(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.insSchLocSrch");
						}
//					}
				}
			
			} // end for
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updStrAreaSrchSeq
	
	
	/**
	 * 차량동간이적 (이적지시 )
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updColUnitCarMvstkRegNew(GridData gdReq) throws DAOException {

		String methodNm = "차량이적등록[BCoilJspSeEJB.updColUnitCarMvstkRegNew] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//Return Value
			JDTORecord jrRtn = null;
			JDTORecordSet rsResult = null;
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			String sSTOCK_ID 		= null;
			String sSTACK_COL_GP 	= ""; //적치 열 구분
			String sSTACK_BED_GP 	= ""; //적치 BED 구분
			String sSTACK_LAYER_GP 	= ""; //적치 단 구분
			String sYD_SCH_PRIOR 	= ""; //야드스케쥴우선순위
			String sYD_WBOOK_ID 	= ""; //작업예약ID
			String sYD_WBOOK_ID_1 	= ""; //첫번째 작업예약ID
			String sYD_SCH_REQ_GP 	= ""; //야드스케줄요청구분
			String ydFrmYn          = "";
			String ydStkColGp       = "";
			boolean isFirst = false;
			
			int nLtRtRule = 13;
			int nSECT_GP = 0;
			
			//화면에서 전달 받은 파레메터 저장
			String sCAR_CARD_NO 	= commUtils.trim(gdReq.getParam("CAR_NO"));	//차량번호(차량카드번호)
			String sYD_GP	  		= commUtils.trim(gdReq.getParam("YD_GP"));	//야드구분 		
			String sYD_BAY_GP 		= commUtils.trim(gdReq.getParam("BAY_GP")); //동 정보
			String sTO_YD_BAY_GP 	= commUtils.trim(gdReq.getParam("TO_YD_BAY_GP")); //목표동 정보
			String sT_CNT			= commUtils.trim(gdReq.getParam("T_CNT")); //작업매수
			String sPT_LOC			= commUtils.trim(gdReq.getParam("PT_LOC")); //위치구분(L,R)
			String sYD_SCH_CD		= commUtils.trim(gdReq.getParam("YD_SCH_CD")); //스케줄코드
			
			int iT_CNT			    = Integer.parseInt(commUtils.nvl(commUtils.trim(gdReq.getParam("T_CNT")),"0")); //작업매수
			
			
			
			//현재일자 구하기
			String sCURR_DATE 	= commUtils.getDate8(); //"yyyyMMdd"
			
			//파라메터 체크
			if("".equals(sYD_GP)) {
				throw new Exception("YD_GP를 입력하지 않았습니다!.");
			}
			if("".equals(sYD_BAY_GP)) {
				throw new Exception("YD_BAY_GP를 입력하지 않았습니다!.");
			}
			if("".equals(sSTOCK_ID)) {
				throw new Exception("COIL_NO를 입력하지 않았습니다!.");
			}
			
			jrParam.setField("YD_GP" 		, sYD_GP); 	
			jrParam.setField("YD_BAY_GP" 	, sYD_BAY_GP); 
			jrParam.setField("PT_LOC" 		, sPT_LOC); 
			jrParam.setField("TO_YD_BAY_GP" , sTO_YD_BAY_GP); 
			
			
			// TO 위치 차량이송 포인트 CHECK
			/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCarPntFrmYnToChk 
			SELECT YD_CARPNT_CD
			  FROM TB_YD_CARPOINT A
			 WHERE A.YD_CAR_USETYPE_GP = 'MT'
			   AND A.YD_GP     = :V_YD_GP
			   AND A.YD_BAY_GP = :V_TO_YD_BAY_GP
			   AND CASE WHEN SUBSTR(A.YD_STK_COL_GP,6,1) IN ('1','2') THEN '1' 
			            WHEN SUBSTR(A.YD_STK_COL_GP,6,1) IN ('3','4') THEN '3'
			            END 
			     = CASE WHEN :V_PT_LOC IN ('1','4') THEN '3'
			            WHEN :V_PT_LOC IN ('2','3') THEN '1' END
			 */
		    JDTORecordSet jsCarPntFrmYnToChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCarPntFrmYnToChk", logId, methodNm, "TB_YM_STACKLAYER 에 적치상태  Check");
			if(jsCarPntFrmYnToChk.size() == 0) {	
				throw new Exception("이적동에 차량이송 포인트(MT)가 없습니다..");
			}
			
			/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getDongMvCarSch 
			SELECT YD_CAR_SCH_ID
			  FROM TB_YD_CARSCH
			 WHERE DEL_YN = 'N'  
			   AND CAR_NO = :V_CAR_CARD_NO
			   */
			jrParam.setField("CAR_CARD_NO"		, sCAR_CARD_NO);
			JDTORecordSet jsMvCarSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getDongMvCarSch", logId, methodNm, "TB_YM_STACKLAYER 에 적치상태  Check");

			if(jsMvCarSch.size() == 0) {	
				/**********************************************************
				//화면에서 전달 받은 파레메터 저장
				* Crane스케줄 호출
				*  - CarPoint 테이블에서 차량형상 시스템 사용 여부 확인
				*  - 사용여부가 N 일 경우 생성된 작업예약 모두를 스케줄 기동
				**********************************************************/
				/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCarPntFrmYn
				SELECT YD_STK_COL_GP
				     , NVL(YD_FRM_YN,'N') AS YD_FRM_YN
				  FROM USRYDA.TB_YD_CARPOINT
				 WHERE YD_CAR_USETYPE_GP = 'MT'
				   AND YD_GP     = :V_YD_GP
				   AND YD_BAY_GP = :V_YD_BAY_GP
				   AND CASE WHEN SUBSTR(YD_STK_COL_GP,6,1) IN ('1','2') THEN '1' 
				            WHEN SUBSTR(YD_STK_COL_GP,6,1) IN ('3','4') THEN '3'
				            END 
				     = CASE WHEN :V_PT_LOC IN ('1','2') THEN '1'
				            WHEN :V_PT_LOC IN ('3','4') THEN '3' END
				   AND CAR_NO IS NULL         
				   AND ROWNUM = 1
				*/
	
	
				
				JDTORecordSet jsPntFrm = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCarPntFrmYn", logId, methodNm, "TB_YD_CARPOINT 테이블에서 차량형상 시스템 사용 여부 확인 ");
				if(jsPntFrm.size() > 0) {
					ydFrmYn    = commUtils.nvl(jsPntFrm.getRecord(0).getFieldString("YD_FRM_YN"),"N");
					ydStkColGp = commUtils.trim(jsPntFrm.getRecord(0).getFieldString("YD_STK_COL_GP"));
					
				} else {
					throw new Exception(sYD_BAY_GP+ "상차도에 차량동간이적 지정이 되어 있지 않습니다.");	
				}
			}				

			
			/**********************************************************
			* 스케줄코드 편성
			**********************************************************/
			
			/**********************************************************
			* 야드스케쥴 우선순위 검색
			**********************************************************/
			//스케줄코드로 스케줄기준Table조회
			jrParam.setField("YD_SCH_CD", sYD_SCH_CD);
	    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule  
			SELECT YD_SCH_CD
			     , YD_WRK_CRN
			     , YD_WRK_CRN_PRIOR
			  FROM TB_YM_SCHEDULERULE
			 WHERE DEL_YN = 'N'
			   AND YD_SCH_CD = :V_YD_SCH_CD
			*/   
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
	    	
			if (rsResult != null && rsResult.size() > 0) {
				sYD_SCH_PRIOR = rsResult.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
			} else {
				throw new Exception("B열연 코일 스케쥴 코드 이상 : [" + sYD_SCH_CD + "]");
			}			

			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for(int jj = 2; jj > 0; jj--) {
				
				for(int ii = 0; ii < rowCnt; ii++) {
					
					sSTACK_LAYER_GP = commUtils.getValue(gdReq, "STACK_LAYER_GP", ii); //Grid에서 선택한 코일의 적치단 '01','02'
					
					if(sSTACK_LAYER_GP.equals("0"+jj)) { //2단 먼저 처리 후 1단 처리..
					
						sSTOCK_ID = commUtils.getValue(gdReq, "STOCK_ID", ii); //Grid에서 선택한 코일 번호
						
						/**********************************************************
						* 1. TB_YM_STOCK 에 정보가 존재하는지 Check
						*    - 존재한다면 작업예약에 걸려있는지 Check 
						**********************************************************/
						jrParam.setField("STOCK_ID", sSTOCK_ID);
						rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectStockID", logId, methodNm, "TB_YM_STOCK 에 정보가 존재하는지 Check");
						
						if (rsResult == null || rsResult.size() <= 0) {
							throw new Exception("TB_YM_STOCK(YM_저장품) 에 존재하지 않는 COIL_NO 입니다!");
						} else {
							sYD_WBOOK_ID = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_WBOOK_ID"));
							if(!"".equals(sYD_WBOOK_ID)) {
								throw new Exception("작업예약 ID:"+sYD_WBOOK_ID+" 에 등록된 COIL 입니다!");
							}
						}
	
						/**********************************************************
						* 2. TB_YM_STACKLAYER 에 적치상태  Check
						*    - 적치열,bed,단 정보를 가져온다.
						**********************************************************/
						sSTACK_COL_GP = ""; //적치 열 구분
						sSTACK_BED_GP = ""; //적치 BED 구분
						sSTACK_LAYER_GP = ""; //적치 단 구분
						
						jrParam.setField("STOCK_ID", sSTOCK_ID);
						rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getselectStackColGp", logId, methodNm, "TB_YM_STACKLAYER 에 적치상태  Check");
						if (rsResult == null || rsResult.size() <= 0) {
							throw new Exception("TB_YM_STACKLAYER(YM_적치단) 에 존재하지 않는 COIL_NO 입니다!");
						} else {
							String sSTACK_LAYER_STAT = commUtils.trim(rsResult.getRecord(0).getFieldString("STACK_LAYER_STAT"));
							if(!"C".equals(sSTACK_LAYER_STAT) && !"L".equals(sSTACK_LAYER_STAT)) {
								throw new Exception("적치단 상태가 '적치중(C)' 이 아닙니다! 현재 상태 : " + sSTACK_LAYER_STAT);
							}
							
							sSTACK_COL_GP 	= commUtils.trim(rsResult.getRecord(0).getFieldString("STACK_COL_GP")); //적치 열 구분
							sSTACK_BED_GP 	= commUtils.trim(rsResult.getRecord(0).getFieldString("STACK_BED_GP")); //적치 BED 구분
							sSTACK_LAYER_GP	= commUtils.trim(rsResult.getRecord(0).getFieldString("STACK_LAYER_GP")); //적치 단 구분
							
						}

						/**********************************************************
						* 3. TB_YM_STOCK 에 차량 카드번호 등록
						*    - 저장품 이동 조건 지정
						*    - CAR_CARD_NO 에 화면에서 선택한 번호 설정 (9999,9998,..,9995)
						*    - TRANS_WORD_NO 설정
						**********************************************************/
						jrParam.setField("STOCK_MOVE_TERM"	, YmConstant.NEW_STOCK_MOVE_TERM_CS); //이송대기
						jrParam.setField("CAR_CARD_NO"		, sCAR_CARD_NO);
//						jrParam.setField("TRANS_WORD_NO"	, sCURR_DATE + iTransOrdSeqNo);
//						jrParam.setField("TRANS_ORD_DATE2"	, sCURR_DATE);
//						jrParam.setField("TRANS_ORD_SEQNO2"	, ""+iTransOrdSeqNo);
						jrParam.setField("CARUNLOAD_YD"		, sYD_GP);
						jrParam.setField("CARUNLOAD_BAY"	, sTO_YD_BAY_GP);
						jrParam.setField("CARUNLOAD_PUT_LOC", sYD_GP + sTO_YD_BAY_GP);
						jrParam.setField("CTS_RELAY_BAY"	, sT_CNT);
						jrParam.setField("CTS_RELAY_SADDLE"	, sPT_LOC);
						jrParam.setField("STOCK_ID"         , sSTOCK_ID);
						/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateStockTransInfo_01 
						UPDATE TB_YM_STOCK A
						   SET MODIFIER = :V_MODIFIER
						      ,MOD_DDTT = SYSDATE
						      ,STOCK_MOVE_TERM  = NVL(:V_STOCK_MOVE_TERM, A.STOCK_MOVE_TERM)
						      ,CAR_NO2          = NVL(:V_CAR_CARD_NO    , A.CAR_NO2)
						      ,CAR_CARD_NO      = NVL(:V_CAR_CARD_NO    , A.CAR_CARD_NO)
						      ,TRANS_WORD_NO    = NVL(:V_TRANS_WORD_NO  , A.TRANS_WORD_NO)
						      ,TRANS_ORD_DATE2  = NVL(:V_TRANS_ORD_DATE2, A.TRANS_ORD_DATE2)
						      ,TRANS_ORD_SEQNO2 = NVL(:V_TRANS_ORD_SEQNO2, A.TRANS_ORD_SEQNO2)
						      ,CARUNLOAD_YD     = NVL(:V_CARUNLOAD_YD   , A.CARUNLOAD_YD)
						      ,CARUNLOAD_BAY    = NVL(:V_CARUNLOAD_BAY  , A.CARUNLOAD_BAY) --목표동
						      ,CARUNLOAD_PUT_LOC= NVL(:V_CARUNLOAD_PUT_LOC, A.CARUNLOAD_PUT_LOC)
						      ,CTS_RELAY_BAY    = NVL(:V_CTS_RELAY_BAY  , A.CTS_RELAY_BAY) --작업매수
						      ,CTS_RELAY_SADDLE = NVL(:V_CTS_RELAY_SADDLE, A.CTS_RELAY_SADDLE) --방향(L,R)
						 WHERE STOCK_ID = :V_STOCK_ID 
						 */
						 commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateStockTransInfo_01", logId, methodNm, "TB_YM_STOCK 에 차량 카드번호 등록");
						
						/**********************************************************
						* 4. 작업예약 ID 생성
						**********************************************************/
						sYD_WBOOK_ID = commDao.getSeqId(logId, methodNm, "WrkBook"); 
						
						/**********************************************************
						* 5. 작업예약(TB_YM_WRKBOOK) 생성
						**********************************************************/
						jrParam.setField("YD_WBOOK_ID"			, sYD_WBOOK_ID);
						jrParam.setField("YD_GP"				, sYD_GP);
						jrParam.setField("YD_BAY_GP"			, sYD_BAY_GP);
						jrParam.setField("YD_SCH_CD"			, sYD_SCH_CD); //야드스케쥴코드
						jrParam.setField("YD_SCH_PRIOR"			, sYD_SCH_PRIOR); //야드스케쥴우선순위
						jrParam.setField("YD_SCH_PROG_STAT"		, "W"); //야드스케쥴진행상태(W:스케줄수행대기)
						jrParam.setField("YD_SCH_ST_GP"			, "M"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업)
						jrParam.setField("YD_SCH_REQ_GP"		, sYD_SCH_REQ_GP); //야드스케쥴요청구분
						jrParam.setField("CARD_NO"				, sCAR_CARD_NO); 
						jrParam.setField("CAR_NO"				, sCAR_CARD_NO); 
						jrParam.setField("YD_AIM_YD_GP"			, sYD_GP); 
						jrParam.setField("YD_AIM_BAY_GP"		, sTO_YD_BAY_GP); 
						jrParam.setField("DIST_SHIPASSIGN_GP"	, sPT_LOC); 
						jrParam.setField("YD_CAR_USE_GP"		, YmConstant.YD_CAR_USE_GP_DM); //G:출하차량 (차량이적시 G로 설정한다 C열연 참조)
						jrParam.setField("YD_TO_LOC_DCSN_MTD"	, "S"); //TO위치결정방법 S:스케줄기준적용
						
						
						commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insWrkBook", logId, methodNm, "작업예약(TB_YM_WRKBOOK) 생성");
	
						/**********************************************************
						* 6. 작업예약재료(TB_YM_WRKBOOKMTL) 생성
						**********************************************************/
						jrParam.setField("YD_WBOOK_ID"		, sYD_WBOOK_ID);
						jrParam.setField("STOCK_ID"			, sSTOCK_ID);
						jrParam.setField("STACK_COL_GP"		, sSTACK_COL_GP);
						jrParam.setField("STACK_BED_GP"		, sSTACK_BED_GP);
						jrParam.setField("STACK_LAYER_GP"	, sSTACK_LAYER_GP);
						
						commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insYmWrkBookMtl", logId, methodNm, "작업예약재료(TB_YM_WRKBOOKMTL) 생성");
					}
					
				}
			}
			// 형상여부
			if("N".equals(ydFrmYn)) {
				// 형상이 없는 경우 도착 미리 기동처리 함	
				jrParam.setField("JMS_TC_CD"		, "A7YML018" );
				jrParam.setField("PT_LOAD_LOC"	    , ydStkColGp);
				jrParam.setField("CAR_NO"			, sCAR_CARD_NO );
				jrParam.setField("CAR_UPDN_GP"		, "1");  //상차
				jrParam.setField("MODIFIER"			, commUtils.trim(gdReq.getParam("userid")) );
				
				EJBConnector sndConn = new EJBConnector("default", "BCoilL2RcvSeEJB", this);
				JDTORecord jrRtn1 = (JDTORecord)sndConn.trx("procA7YML018", new Class[] { JDTORecord.class }, new Object[] { jrParam });

				jrRtn = commUtils.addSndData(jrRtn, jrRtn1);				
			}

			commUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
		
	} // end of updColUnitCarMvstkRegNew
	
	
	/**
	 *      [A] 오퍼레이션명 : 이적작업예약등록
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updblMvStkWrkBook(GridData gdReq) throws DAOException {
		String methodNm = "이적작업예약등록[BCoilJspSeEJB.updblMvStkWrkBook] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;
			JDTORecordSet jsMsg = JDTORecordFactory.getInstance().createRecordSet("");	//크레인스케줄 정리를 위함

			String sYD_GP        = commUtils.trim(gdReq.getParam("YD_GP"           )); //야드구분
			String stlNos        = commUtils.trim(gdReq.getParam("ARR_STOCK_ID"    )); //재료번호들
			String sSTACK_COL_GP = commUtils.trim(gdReq.getParam("STACK_COL_GP"    )); //야드적치열구분(4자리 이상)
			String ydToLocGuide  = commUtils.trim(gdReq.getParam("YD_TO_LOC_GUIDE" )); //야드To위치Guide
			String ydWrkPlanTcar = commUtils.trim(gdReq.getParam("YD_WRK_PLAN_TCAR")); //야드작업계획대차
			String ydWrkPlanCrn  = commUtils.trim(gdReq.getParam("YD_WRK_PLAN_CRN" )); //야드지정크레인
			String sWB_SORT_GP   = commUtils.trim(gdReq.getParam("WB_SORT_GP"      )); //작업예약 생성순서
			String sMVSTK        = commUtils.trim(gdReq.getParam("MVSTK"           )); //스크랩이적 팝업 구분자
			String sA_TO_E       = commUtils.trim(gdReq.getParam("A_TO_E"          )); //A~E동 동간 이적
			String sTO_YD_BAY_GP = "";
			String sTO_SECT_GP   = "";
			String sYD_TO_LOC_GUIDE_FNL = "";
			
			if (ydWrkPlanCrn.length() != 6) {
				ydWrkPlanCrn = "";
			}
			
			if (sSTACK_COL_GP.length() < 4) {
				//혹시 이적 적치열구분 값이 잘못되어 있으면 무조건 01 Span 으로 처리
				sSTACK_COL_GP = sSTACK_COL_GP.substring(0, 2) + "01";
			} else if (sSTACK_COL_GP.length() > 6) {
				sSTACK_COL_GP = sSTACK_COL_GP.substring(0, 6);
			}

			if ("".equals(stlNos)) {
				throw new Exception("이적 재료번호가 없습니다.");
			} else if ("".equals(sSTACK_COL_GP) || sSTACK_COL_GP.length() < 4) {
				throw new Exception("Span[" + sSTACK_COL_GP + "] 정보가 없습니다.");
			}
			
			if ("Y".equals(sA_TO_E)) {
				sYD_TO_LOC_GUIDE_FNL = ydToLocGuide;
				ydToLocGuide         = "3DTC05";
			}
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			JDTORecordSet rsResult = null;
			
			/**********************************************************
			* 1. 스케줄코드 설정
			**********************************************************/
			String ydSchCd    = ""; //야드스케쥴코드
			String ydBayGp    = sSTACK_COL_GP.substring(1, 2); //야드동구분
			String ydAimBayGp = ""; //야드목표동구분

			if ("".equals(ydToLocGuide)) {
				//위치검색Bed기준 적용
				ydAimBayGp = sSTACK_COL_GP.substring(1, 2);
			} else {
				//To위치지정
				ydAimBayGp = ydToLocGuide.substring(1, 2);
				//To위치가 동까지만 있으면 위치검색Bed 기준 적용
				if (ydToLocGuide.length() < 4) {
					ydToLocGuide = "";
				} else {
					sTO_YD_BAY_GP = commUtils.nvl(ydToLocGuide.substring(1, 2), "");
					sTO_SECT_GP   = commUtils.nvl(ydToLocGuide.substring(2, 4), "");
				}
				
			}

			jrParam.setField("REPR_CD_GP", "SCH001");
			jrParam.setField("CD_GP"     , "LTRT_RULE");
			jrParam.setField("ITEM"      , ydBayGp);
			
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmRule", logId, methodNm, "스케줄코드 좌우기준 조회");
			
			if (rsResult.size() <= 0 && "".equals(sMVSTK)) {
				throw new Exception("해당 동의 스케줄코드 좌우 구분이 없습니다.");
			}
			int nLtRtRule   = Integer.parseInt(rsResult.getRecord(0).getFieldString("DTL_ITM1"));
			int nSECT_GP    = 0; //Integer.parseInt(sSTACK_COL_GP.substring(2, 4));
			
			String sSECT_GP = sTO_SECT_GP == "" ? sSTACK_COL_GP.substring(2, 4) : sTO_SECT_GP;
			String sCOL_GP  = sSTACK_COL_GP.substring(4, 6);
			String sBAY_GP  = sSTACK_COL_GP.substring(1, 2);
			
			boolean bIS_EQP = false;
			 
			bIS_EQP  = Pattern.matches("^[a-zA-Z]*$", sSECT_GP);
				
			String sLtRtGp = "";
			
			if (bIS_EQP) {
				
				sLtRtGp = "L"; //좌우구분 모르는 것들은 default 'L'
				
				// 설비
				if ("TC".equals(sSECT_GP)){
					// 대차
					if ("01".equals(sCOL_GP) || "04".equals(sCOL_GP)) {
						sLtRtGp = "L";
					}
					if ("02".equals(sCOL_GP) || "03".equals(sCOL_GP) || "05".equals(sCOL_GP)) { 
						sLtRtGp = "R";
					}
				} else if("A".equals(sBAY_GP) && "SC".equals(sSECT_GP)){
					sLtRtGp = "R"; //A동 스크랩 일경우 우측 크레인 사용
				}
				
			} else {
				nSECT_GP = Integer.parseInt(sSECT_GP);
				// 야드
				if (nSECT_GP < nLtRtRule) {
					sLtRtGp = "L";
				} else if (nSECT_GP >= nLtRtRule) {
					sLtRtGp = "R";
				}
			}

			//스케쥴코드
			if (ydBayGp.equals(ydAimBayGp)) { //동내이적 
				
				if ("L".equals(sLtRtGp)) {
					ydSchCd = sSTACK_COL_GP.substring(0, 2) + "YD01MM";
				} else if ("R".equals(sLtRtGp)) { 
					ydSchCd = sSTACK_COL_GP.substring(0, 2) + "YD05MM";
				} else {
					ydSchCd = sSTACK_COL_GP.substring(0, 2) + "YD01MM";//default
				}
				
				ydWrkPlanTcar = "";
				
			} else {
				if ("".equals(ydWrkPlanTcar)) {
					throw new Exception("To위치지정 동간이적 대차 정보가 없습니다.");
				}
				
				ydSchCd = sSTACK_COL_GP.substring(0, 2) + ydWrkPlanTcar.substring(2, 6) + "UM";
			}
			
			if ("Y".equals(sA_TO_E)) {
//				ydSchCd       = "3ATC02UM";
				ydWrkPlanTcar = "3XTC02";
			}
			
			
			jrParam.setField("ARR_STOCK_ID", stlNos    ); //재료번호들
			jrParam.setField("STACK_COL_GP", sSTACK_COL_GP); //야드적치열구분

			//작업예약 대상재료 조회
			/*
			SELECT SL.STOCK_ID
			     , SL.STACK_COL_GP
			     , SL.STACK_BED_GP
			     , SL.STACK_LAYER_GP
			     , PT.COIL_WT
			     , PT.COIL_T
			     , PT.COIL_W
			     , PT.CURR_COIL_LEN
			     , TO_CHAR(PT.COIL_T)||' X '||TO_CHAR(PT.COIL_W,'FM9,999') AS MTL_SIZE
			     , TO_CHAR(PT.COIL_OUTDIA,'FM99,999') AS COIL_OUTDIA
			     , SL.STACK_COL_GP||SL.STACK_BED_GP||'-'||SL.STACK_LAYER_GP AS YM_STR_LOC
			     , CASE WHEN SUBSTR(SL.STACK_COL_GP, 3, 2) BETWEEN '01' AND '13' THEN 'L' 
			            WHEN SUBSTR(SL.STACK_COL_GP, 3, 2) BETWEEN '14' AND '99' THEN 'R' 
			            ELSE NULL END 
			       AS LR_GP
			     , (SELECT MILL_INI_DATE FROM TB_PT_COILCOMM WHERE COIL_NO = SL.STOCK_ID) AS MILL_INI_DATE
			  FROM TB_YM_STACKLAYER SL
			     , TB_YM_STOCK      ST
			     ,(SELECT REGEXP_SUBSTR(SSTL_NOS, '[^,]+', 1, LEVEL) AS STOCK_ID
			         FROM (SELECT :V_ARR_STOCK_ID AS SSTL_NOS FROM DUAL)
			      CONNECT BY REGEXP_SUBSTR(SSTL_NOS, '[^,]+', 1, LEVEL) IS NOT NULL) SN  -- 입력 배열
			     , USRPTA.TB_PT_COILCOMM   PT
			 WHERE SL.STOCK_ID = SN.STOCK_ID
			   AND SL.STOCK_ID = ST.STOCK_ID
			   AND SL.STOCK_ID = PT.COIL_NO
			   AND SL.STACK_COL_GP LIKE SUBSTR(:V_STACK_COL_GP,1,2)||'%'
			   AND SL.STACK_LAYER_STAT = 'C'
			 */
			JDTORecordSet jsWbMtl = null;
			if (!"".equals(sMVSTK)) {
				jsWbMtl = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getblMvStkWrkBookMtlScPp", logId, methodNm, "스크랩 조회");				
			} else {
				jsWbMtl = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getMvStkWrkBookMtl", logId, methodNm, "재료번호로 조회");//이전정렬
//				jsWbMtl = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getblMvStkWrkBookMtlPp", logId, methodNm, "재료번호로 조회");//기존
			}

			int rowCnt = jsWbMtl.size();

			if (rowCnt <= 0) {
				throw new Exception("이적 재료 정보가 없습니다.");
			}
			
			/**********************************************************
			* 2. 이적 작업예약 등록
			**********************************************************/
			jrParam.setField("YD_SCH_CD"          , ydSchCd      ); //야드스케쥴코드
			jrParam.setField("YD_AIM_BAY_GP"      , ydAimBayGp   ); //야드목표동구분
			jrParam.setField("YD_TO_LOC_GUIDE"    , ydToLocGuide ); //야드To위치Guide
			jrParam.setField("YD_WRK_PLAN_TCAR"   , ydWrkPlanTcar); //야드작업계획대차
			jrParam.setField("YD_WRK_PLAN_CRN"    , ydWrkPlanCrn ); //야드작업계획크레인
			jrParam.setField("YD_TO_LOC_GUIDE_FNL", sYD_TO_LOC_GUIDE_FNL); //야드To위치GuideFinal(A->E)
			
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
				
				/*
				SELECT TS.YD_TCAR_SCH_ID
				      ,EQ.WPROG_STAT                AS YD_EQP_STAT
				      ,EQ.WORK_MODE                 AS YD_EQP_WRK_MODE
				      ,NVL(SUBSTR(CURR_STOP_LOC,2,1),WB.YD_BAY_GP) 
				                                    AS YD_CURR_BAY_GP --이동중이면 상차동을 현재동으로
				      ,SUBSTR(EQ.WAIT_STOP_LOC,2,1) AS YD_HOME_BAY_GP
				      ,WB.YD_WBOOK_ID               AS YD_WBOOK_ID_CURR   --현재 대차스케줄 상차작업예약ID
				      ,WB.YD_BAY_GP                 AS YD_BAY_GP_CURR     --현재 대차스케줄 상차동
				      ,WB.YD_AIM_BAY_GP             AS YD_AIM_BAY_GP_CURR --현재 대차스케줄 하차동
				      ,XB.YD_WBOOK_ID               AS YD_WBOOK_ID_NEXT   --다음 상차작업예약ID
				      ,XB.YD_BAY_GP                 AS YD_BAY_GP_NEXT     --다음 상차동
				      ,XB.YD_AIM_BAY_GP             AS YD_AIM_BAY_GP_NEXT --다음 하차동
				      ,(SELECT CASE WHEN COUNT(*) > 0 THEN 'Y' ELSE 'N' END
				          FROM TB_YM_TCARFTMVMTL TM
				         WHERE TM.YD_TCAR_SCH_ID = TS.YD_TCAR_SCH_ID
				           AND TM.DEL_YN = 'N')     AS TC_MTL_YN
				--      ,NVL(EQ.AUTO_TCAR_SCH_YN,'N') AS AUTO_TCAR_SCH_YN   --자동대차스케줄여부
				  FROM TB_YM_EQUIP   EQ
				      ,TB_YM_TCARSCH TS
				      ,TB_YM_WRKBOOK WB
				      ,(SELECT MIN(YD_WBOOK_ID  ) AS YD_WBOOK_ID
				              ,MIN(YD_BAY_GP    ) AS YD_BAY_GP
				              ,MIN(YD_AIM_BAY_GP) AS YD_AIM_BAY_GP
				          FROM (SELECT YD_WBOOK_ID
				                      ,YD_BAY_GP
				                      ,YD_AIM_BAY_GP
				                  FROM TB_YM_WRKBOOK
				                 WHERE YD_WRK_PLAN_TCAR = :V_YD_EQP_ID
				                   AND YD_WBOOK_ID NOT IN
				                      (SELECT NVL(YD_CARLD_WRK_BOOK_ID,YD_CARUD_WRK_BOOK_ID) AS YD_WBOOK_ID
				                         FROM TB_YM_TCARSCH
				                        WHERE DEL_YN = 'N'
				                          AND (YD_CARLD_WRK_BOOK_ID IS NOT NULL	OR YD_CARUD_WRK_BOOK_ID IS NOT NULL))
				--                   AND YD_SCH_CD LIKE '__TC__U%'
				                   AND ((SUBSTR(YD_SCH_CD,1,2) <> (NVL(SUBSTR(YD_TO_LOC_GUIDE,1,2),SUBSTR(YD_SCH_CD,1,2))))
				                         OR 
				                        (YD_SCH_CD LIKE SUBSTR(YD_SCH_CD,1,2)|| 'TC__U%')
				                       )
				                   AND DEL_YN = 'N'
				                 ORDER BY YD_SCH_PRIOR, YD_WBOOK_ID)
				         WHERE ROWNUM = 1) XB
				 WHERE EQ.EQUIP_GP             = TS.YD_EQP_ID(+)
				   AND 'N'                     = TS.DEL_YN(+)
				   AND TS.YD_CARLD_WRK_BOOK_ID = WB.YD_WBOOK_ID(+)
				   AND 'N'                     = WB.DEL_YN(+)
				   AND EQ.EQUIP_GP             = :V_YD_EQP_ID
				 */
				JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarSchLevWo", logId, methodNm, "공대차출발지시 조회");
				
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
					

					jrRtn = ymComm.trtTcarSchLevWo(jrParam);
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
	 *      [A] 오퍼레이션명 : 이적작업예약등록(스크랩)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord insMvstkWrkBookScrap(JDTORecord jrParam, JDTORecordSet jsWbMtl) throws DAOException {
		String methodNm = "스크랩 이적작업[BCoilJspSeEJB.insMvstkWrkBookScrap] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		JDTORecord jrRtn  = null;	//전문 Return

		try {
			commUtils.printLog(logId, methodNm, "S+");

			String ydSchCd       = commUtils.trim(jrParam.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
			String ydAimBayGp    = commUtils.trim(jrParam.getFieldString("YD_AIM_BAY_GP"   )); //야드목표동구분
			String ydToLocGuide  = commUtils.trim(jrParam.getFieldString("YD_TO_LOC_GUIDE" )); //야드To위치Guide
			String modifier      = commUtils.trim(jrParam.getFieldString("MODIFIER"        )); //수정자
			String ydWrkPlanCrn  = commUtils.trim(jrParam.getFieldString("YD_WRK_PLAN_CRN" )); //야드작업계획대차
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrYMYM303 = commUtils.getParam(logId, methodNm, modifier);
			int wrkBookCnt = 0;
			
			/**********************************************************
			* 1. 야드스케쥴코드 Check
			**********************************************************/
			/*
			SELECT YD_SCH_CD
			     , YD_WRK_CRN       
			     , YD_WRK_CRN_PRIOR 
			     , YD_SCH_PROH_EXN
			  FROM TB_YM_SCHEDULERULE
			 WHERE YD_SCH_CD = :V_YD_SCH_CD
			   AND DEL_YN    = 'N'
			 */
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getStatSchCd", logId, methodNm, "야드스케쥴코드 Check");
			String sYD_WRK_CRN = "";
			if (jsChk.size() > 0) {
				sYD_WRK_CRN = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_WRK_CRN"));
			}
			
			if ("".equals(sYD_WRK_CRN)) {
				throw new Exception("스케쥴코드[" + ydSchCd + "] 작업가능 크레인 없음");
			}
			
			JDTORecord jrCrnSpec = jsChk.getRecord(0);
			
			String ydGp       = ydSchCd.substring(0, 1);	//야드구분
			String ydBayGp    = ydSchCd.substring(1, 2);	//야드동구분
			String ydEqpId    = commUtils.trim(jrCrnSpec.getFieldString("YD_WRK_CRN"   ));	//야드설비ID(크레인)
			String ydSchPrior = commUtils.trim(jrCrnSpec.getFieldString("YD_WRK_CRN_PRIOR"));	//야드스케쥴우선순위
			String ydToLocDcsnMtd = "S"; //야드TO위치결정방법(스케줄지정)

			// 지정크레인일 경우 지정크레인으로 스케줄 기동
			if (!"".equals(ydWrkPlanCrn)) {
				ydEqpId = ydWrkPlanCrn;
			}
			
			if ("".equals(ydAimBayGp)) {
				ydAimBayGp = ydBayGp;
			}

			if (!"".equals(ydToLocGuide)) {
				ydToLocDcsnMtd = "F";	//야드TO위치결정방법(지정위치)
			}
			
			/**********************************************************
			* 2. 크레인사양 분리
			**********************************************************/
			jrCrnSpec.setResultCode(logId);  	//Log ID
			jrCrnSpec.setResultMsg(methodNm);	//Log Method Name
			
			Vector vcLot = this.setCrnSpecSpr(jrCrnSpec, jsWbMtl);

			JDTORecordSet jsLotMtl = null;
			int lotCnt   = vcLot.size();	//크레인사양 분리 작업예약수
			int lotMtlSh = 0;				//작업예약재료매수
			String ydWbookId = "";			//야드작업예약ID
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
				jrParam.setField("YD_WRK_PLAN_CRN"   , ydWrkPlanCrn ); //야드작업계획크레인

				commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insWrkBook", logId, methodNm, "TB_YM_WRKBOOK");

				//작업예약재료 등록
				for (int jj = 0; jj < lotMtlSh; jj++) {
					jrRow = jsLotMtl.getRecord(jj);
					
					JDTORecord jrRtn1 = JDTORecordFactory.getInstance().create();
					
					jrRtn1.setField("YD_WBOOK_ID"   , ydWbookId     ); //야드작업예약ID
					jrRtn1.setField("STOCK_ID"      , commUtils.trim(jrRow.getFieldString("STOCK_ID"       )));	//재료번호
					jrRtn1.setField("STACK_COL_GP"  , commUtils.trim(jrRow.getFieldString("STACK_COL_GP")));	//야드적치열구분
					jrRtn1.setField("STACK_BED_GP"  , commUtils.trim(jrRow.getFieldString("STACK_BED_GP")));	//야드적치Bed번호
					jrRtn1.setField("STACK_LAYER_GP", commUtils.trim(jrRow.getFieldString("STACK_LAYER_GP")));	//야드적치단번호
					jrRtn1.setField("MODIFIER"     	, modifier      );												//등록자
					commDao.insert(jrRtn1, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.insWrkBookMtl", logId, methodNm, "TB_YM_WRKBOOKMTL");
				}
				
				
				// 스케줄 기동
//				JDTORecord jrYmMsg = commUtils.getParam(logId, methodNm, modifier);
//				jrYmMsg.setResultCode(logId);	//Log ID
//				jrYmMsg.setResultMsg(methodNm);	//Log Method Name
//
//				jrYmMsg.setField("JMS_TC_CD", "YMYMJ302"); //야드작업예약ID
//				jrYmMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시				
//				
//				jrYmMsg.setField("YD_WBOOK_ID", ydWbookId);
//				jrYmMsg.setField("YD_SCH_CD"  , ""); //야드스케쥴코드
//				jrYmMsg.setField("YD_EQP_ID"  , ""); //야드설비ID
//				
//				//크레인스케줄기동 전문
//				EJBConnector sndConnD = new EJBConnector("default", "BCoilSchSeEJB", this);
//				JDTORecord jrRtnD = (JDTORecord)sndConnD.trx("rcvYMYMJ302", new Class[] { JDTORecord.class }, new Object[] { jrYmMsg });
//				jrRtn = commUtils.addSndData(jrRtn, jrRtnD);
				
				jrYMYM303.setField("YD_WBOOK_ID"+(++wrkBookCnt), ydWbookId); //야드작업예약ID
			}

//			if (!"".equals(ydWbookId)) {
//				jrYMYM303.setField("JMS_TC_CD"         , "YMYMJ303"); //야드작업예약ID
//				jrYMYM303.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
//				jrYMYM303.setField("YD_SCH_CD"         , ""); //야드스케쥴코드
//				jrYMYM303.setField("YD_EQP_ID"         , ""); //야드설비ID
//				jrYMYM303.setField("SCH_CNT"           , Integer.toString(wrkBookCnt)); //작업예약 개수
//				
//				jrRtn = commUtils.addSndData(jrRtn, jrYMYM303);
//			}
			
			//크레인스케줄기동 전문
//			EJBConnector sndConn = new EJBConnector("default", "BCoilSchSeEJB", this);
//			jrRtn = (JDTORecord)sndConn.trx("rcvYMYMJ303", new Class[] { JDTORecord.class }, new Object[] { jrYMYM303 });
			
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
		String methodNm = "이적작업예약등록[BCoilJspSeEJB.insMvstkWrkBook] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			String ydSchCd              = commUtils.trim(jrParam.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
			String ydAimBayGp           = commUtils.trim(jrParam.getFieldString("YD_AIM_BAY_GP"   )); //야드목표동구분
			String ydToLocGuide         = commUtils.trim(jrParam.getFieldString("YD_TO_LOC_GUIDE" )); //야드To위치Guide
			String ydWrkPlanTcar        = commUtils.trim(jrParam.getFieldString("YD_WRK_PLAN_TCAR")); //야드작업계획대차
			String modifier             = commUtils.trim(jrParam.getFieldString("MODIFIER"        )); //수정자
			String ydWrkPlanCrn         = commUtils.trim(jrParam.getFieldString("YD_WRK_PLAN_CRN" )); //야드작업계획대차
			String sYD_TO_LOC_GUIDE_FNL = commUtils.trim(jrParam.getFieldString("YD_TO_LOC_GUIDE_FNL")); //야드To위치Guide
			
			/**********************************************************
			* 1. 야드스케쥴코드 Check
			**********************************************************/
			/*
			SELECT YD_SCH_CD
			     , YD_WRK_CRN       
			     , YD_WRK_CRN_PRIOR 
			     , YD_SCH_PROH_EXN
			  FROM TB_YM_SCHEDULERULE
			 WHERE YD_SCH_CD = :V_YD_SCH_CD
			   AND DEL_YN    = 'N'
			 */
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getStatSchCd");
			String sYD_WRK_CRN = "";
			if (jsChk.size() > 0) {
				sYD_WRK_CRN = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_WRK_CRN"));
			}
			
			if ("".equals(sYD_WRK_CRN)) {
				throw new Exception("스케쥴코드[" + ydSchCd + "] 작업가능 크레인 없음");
			}
			
			JDTORecord jrCrnSpec = jsChk.getRecord(0);
			
			String ydGp       = ydSchCd.substring(0, 1);	//야드구분
			String ydBayGp    = ydSchCd.substring(1, 2);	//야드동구분
			String ydEqpId    = commUtils.trim(jrCrnSpec.getFieldString("YD_WRK_CRN"   ));	//야드설비ID(크레인)
			String ydSchPrior = commUtils.trim(jrCrnSpec.getFieldString("YD_WRK_CRN_PRIOR"));	//야드스케쥴우선순위
			String ydToLocDcsnMtd = "S"; //야드TO위치결정방법(스케줄지정)

			// 지정크레인일 경우 지정크레인으로 스케줄 기동
			if (!"".equals(ydWrkPlanCrn)) {
				ydEqpId = ydWrkPlanCrn;
			}
			
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
			jrCrnSpec.setResultCode(logId);  	//Log ID
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
				jrParam.setField("YD_WBOOK_ID"        , ydWbookId     ); //야드작업예약ID
				jrParam.setField("MODIFIER"           , modifier      ); //수정자
				jrParam.setField("YD_GP"              , ydGp          ); //야드구분
				jrParam.setField("YD_BAY_GP"          , ydBayGp       ); //야드동구분
				jrParam.setField("YD_SCH_CD"          , ydSchCd       ); //야드스케쥴코드
				jrParam.setField("YD_SCH_PRIOR"       , ydSchPrior    ); //야드스케쥴우선순위
				jrParam.setField("YD_SCH_PROG_STAT"   , "W"           ); //야드스케쥴진행상태(스케줄수행대기)
				jrParam.setField("YD_SCH_ST_GP"       , "M"           ); //야드스케쥴기동구분(Manual)
				jrParam.setField("YD_SCH_REQ_GP"      , "M"           ); //야드스케쥴요청구분(이적)
				jrParam.setField("YD_AIM_YD_GP"       , ydGp          ); //야드목표야드구분
				jrParam.setField("YD_AIM_BAY_GP"      , ydAimBayGp    ); //야드목표동구분
				jrParam.setField("YD_TO_LOC_DCSN_MTD" , ydToLocDcsnMtd); //야드TO위치결정방법
				jrParam.setField("YD_TO_LOC_GUIDE"    , ydToLocGuide  ); //야드To위치Guide
				jrParam.setField("YD_WRK_PLAN_TCAR"   , ydWrkPlanTcar ); //야드작업계획대차
				jrParam.setField("YD_WRK_PLAN_CRN"    , ydWrkPlanCrn  ); //야드작업계획크레인
				jrParam.setField("YD_TO_LOC_GUIDE_FNL", sYD_TO_LOC_GUIDE_FNL); //야드To위치Guide

				commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insWrkBook", logId, methodNm, "TB_YM_WRKBOOK");

				//작업예약재료 등록
				
				for (int jj = 0; jj < lotMtlSh; jj++) {
					jrRow = jsLotMtl.getRecord(jj);
					
					JDTORecord jrRtn1 = JDTORecordFactory.getInstance().create();
					
					jrRtn1.setField("YD_WBOOK_ID"   , ydWbookId     ); //야드작업예약ID
					jrRtn1.setField("STOCK_ID"      , commUtils.trim(jrRow.getFieldString("STOCK_ID"       )));	//재료번호
					jrRtn1.setField("STACK_COL_GP"  , commUtils.trim(jrRow.getFieldString("STACK_COL_GP")));	//야드적치열구분
					jrRtn1.setField("STACK_BED_GP"  , commUtils.trim(jrRow.getFieldString("STACK_BED_GP")));	//야드적치Bed번호
					jrRtn1.setField("STACK_LAYER_GP", commUtils.trim(jrRow.getFieldString("STACK_LAYER_GP")));	//야드적치단번호
					jrRtn1.setField("MODIFIER"     	, modifier      );												//등록자
					commDao.insert(jrRtn1, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.insWrkBookMtl", logId, methodNm, "TB_YM_WRKBOOKMTL");
				}
			}
			
			/**********************************************************
			* 4. 크레인스케줄(YMYMJ302) 전송용 기초 전문 생성
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
		String methodNm = "크레인스케줄전문정리[BCoilJspSeEJB.setCrnSchMsg] < " + mthdNm;

		try {
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam("A", jsMsg);
			//Return Value
			JDTORecord jrRtn = null;

			if (!commUtils.isEmpty(jsMsg)) {
				String ydEqpId   = ""; //야드설비ID(크레인)
				String ydEqpStat = ""; //야드설비상태
				String sYD_SCH_CD = "";
				String sYD_WBOOK_ID = "";
				boolean fstYn = false; //동일크레인에서 첫번째 여부
				//DAO Parameter - Log ID, Method, 수정자 Set
				JDTORecord jrParam = commUtils.getParam(logId, methodNm, "");
				JDTORecord jrRow = null;
				JDTORecordSet jsChk = null;

				int rowCnt = jsMsg.size();

				for (int ii = rowCnt - 1; ii >= 0; ii--) {
					jrRow = jsMsg.getRecord(ii);
					
					if ("".equals(commUtils.trim(jrRow.getFieldString("YD_WRK_PLAN_TCAR")))) {
						//야드작업계획대차가 있으면 대차상차 크레인스케줄이므로 전송하지 않음 -> 공대차출발지시로 처리
						fstYn = true;
						ydEqpId    = commUtils.trim(jrRow.getFieldString("YD_EQP_ID"));
						sYD_SCH_CD = commUtils.trim(jrRow.getFieldString("YD_SCH_CD"));
						sYD_WBOOK_ID = commUtils.trim(jrRow.getFieldString("YD_WBOOK_ID"));
						
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

							/*
							--설비상태조회 
							SELECT WPROG_STAT     AS YD_EQP_STAT
							     , WORK_MODE      AS YD_EQP_WRK_MODE
								 , STACK_MAX_QNTY	                  --적재 최대 수량
								 , STACK_MAX_WT		                  --적재 최대 중량
							     , CURR_STOP_LOC
							  FROM TB_YM_EQUIP EQ
							 WHERE EQUIP_GP = :V_YD_EQP_ID
							   AND DEL_YN    = 'N'  
							 */
							jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getStatEqp", logId, methodNm, "설비상태조회");
							
							ydEqpStat = "";

							if (jsChk.size() > 0) {
								ydEqpStat = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_STAT"));
							}

							jrParam.setField("YD_SCH_CD", sYD_SCH_CD);
							/*
							SELECT DECODE(COUNT(*), 0, 'N', 'Y') AS IS_SCH
							  FROM TB_YM_CRNSCH
							 WHERE 1=1
							   AND DEL_YN = 'N'
							   AND YD_EQP_ID = :V_YD_EQP_ID
							   AND YD_SCH_CD = :V_YD_SCH_CD
							 */
							JDTORecordSet rst = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getIsSchKind");
							commUtils.printLog(logId, "■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■", "[info]");
							commUtils.printLog(logId, "■■■■■"+ sYD_WBOOK_ID + " " + ydEqpId + " "+sYD_SCH_CD, "[info]");
							commUtils.printLog(logId, "■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■", "[info]");
							
							/**
							 * 스케줄 자동기동 여부 체크 
							 */
							/*
							SELECT *
							  FROM TB_YM_SCHEDULERULE
							 WHERE YD_SCH_CD = :V_YD_SCH_CD
							 */
							JDTORecordSet jsSchInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getSchCdInfo", logId, methodNm, "자동기동여부 조회");
							if (jsSchInfo.size() > 0) {
								String sYD_SCH_AUTO_ST_YN = jsSchInfo.getRecord(0).getFieldString("YD_SCH_AUTO_ST_YN");
								//스케줄 자동기동여부 Y , 스케줄 기동된 것이 없으면 기동시킴
								if ("Y".equals(sYD_SCH_AUTO_ST_YN) && "N".equals(rst.getRecord(0).getFieldString("IS_SCH"))) {
									//크레인스케줄 전송YMYMJ302
									jrRtn = commUtils.addSndData(jrRtn, ymComm.getCrnSchMsg(jrRow));
								}
							}
						} //if (fstYn)
					}
				} //end for
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
	 *      [A] 오퍼레이션명 : 크레인사양분리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord jrCrnSpec
	 *      @param JDTORecordSet jsWrkMtl
	 *      @return Vector
	 *      @throws DAOException
	*/
	public Vector setCrnSpecSpr(JDTORecord jrCrnSpec, JDTORecordSet jsWrkMtl) throws DAOException {
		String methodNm = "크레인사양분리[BCoilJspSeEJB.setCrnSpecSpr] < " + jrCrnSpec.getResultMsg();
		String logId = jrCrnSpec.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			Vector vcLot = new Vector();	//크레인사양분리결과
			JDTORecord    jrRow = null;		//현재 Row
			JDTORecordSet jsLot = JDTORecordFactory.getInstance().createRecordSet("");	//Lot
			String sSTACK_COL_GP    = "";	
			String sSTACK_BED_GP    = "";	
			String sSTACK_LAYER_GP  = "";	
			String szCHK_STACK_COL_GP   = "";
			String szCHK_STACK_BED_GP   = "";			
			String szCHK_STACK_LAYER_GP = "";			

			int rowCnt = jsWrkMtl.size();

			for (int ii = 0; ii < rowCnt; ii++) {
				jrRow = jsWrkMtl.getRecord(ii);
				
				sSTACK_COL_GP   = commUtils.trim(jrRow.getFieldString("STACK_COL_GP"));
				sSTACK_BED_GP   = commUtils.trim(jrRow.getFieldString("STACK_BED_GP"));
				sSTACK_LAYER_GP = commUtils.trim(jrRow.getFieldString("STACK_LAYER_GP"));
				
				if (ii > 0) {

					if (!(szCHK_STACK_COL_GP+szCHK_STACK_BED_GP+szCHK_STACK_LAYER_GP).equals(sSTACK_COL_GP+sSTACK_BED_GP+sSTACK_LAYER_GP)) {
						//이전 Lot 추가
						vcLot.add(jsLot);

						jsLot = JDTORecordFactory.getInstance().createRecordSet("");
						szCHK_STACK_COL_GP   = sSTACK_COL_GP;
						szCHK_STACK_BED_GP   = sSTACK_BED_GP;
						szCHK_STACK_LAYER_GP = sSTACK_LAYER_GP;
					}
				} else {
					szCHK_STACK_COL_GP   = sSTACK_COL_GP;
					szCHK_STACK_BED_GP   = sSTACK_BED_GP;
					szCHK_STACK_LAYER_GP = sSTACK_LAYER_GP;
				}
				jsLot.addRecord(jrRow);
			}
			
			//마지막 Lot 추가
			vcLot.add(jsLot);
			commUtils.printParam(logId, vcLot);
			commUtils.printLog(logId, methodNm, "S-");

			return vcLot;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	

	/**
	 * 출하검수등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord jrecord
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updateCarExamination(JDTORecord jrecord) throws DAOException {

		String methodNm = "출하검수등록[BCoilJspSeEJB.updateCarExamination] < " + jrecord.getResultMsg();
		String logId = jrecord.getResultCode();
		
		//Return Value
		JDTORecord jrRtn = null;
		try {	
			int 					count 		= 0;
			JDTORecord 				tcRecordDM 	=JDTORecordFactory.getInstance().create(); 
	
			String 					cnt			= "";
			String 					chk			= "";
			String 					trans_ord_no= "";
			String 					sTRANS_ORD_DATE= "";
			String 					sTRANS_ORD_SEQNO= "";
			String					szTRANS_EQUIPMENT_TYPE= "";
			String					szMsg= "";
			String					msgId= "";
	 
			//JDTORecord recPara = null;
			
			String szCR_FRTOMOVE_GP = "";
			
			if(logId == null ) logId = commUtils.trim(jrecord.getFieldString("logId"));
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(jrecord.getFieldString("userid")));
				
			// PI_YD : 병행가동 : PIDEV
			String sPI_YD = commUtils.setDataDefault(jrecord.getFieldString("PI_YD"), "*");
			
//			String sApplyYnPI = "N";
			
			// CH2010_m.jsp(PDA 출하검수) 병행가동
//			if("J".equals(sPI_YD) || "T".equals(sPI_YD)) { // 2열연 또는 후판
//				sApplyYnPI = ydPICommDAO.ApplyYnPI("", "BCoilJspSeEJBSBean => 열연코일검수완료실적수신", "APPPI0", sPI_YD, "*");
//			} else if("1".equals(sPI_YD)) { // 박판
//				sApplyYnPI = yfcommDao.ApplyYnPI("", "BCoilJspSeEJBSBean => 열연코일검수완료실적수신", "APPPI0", sPI_YD, "*");
//				
//			} else {
//				sApplyYnPI = "Y";
//			}
			
			//운송지시 번호 단위로 검수 완료 처리 작업
			trans_ord_no=commUtils.trim(jrecord.getFieldString("TRANS_ORD_NO"));
			
			if("".equals(trans_ord_no)){

				
				//count += dao.updateCoilloadLotRankingJip(queryID, inData);
				jrParam.setField("YD_CAR_UPP_LOC_CD"	,jrecord.getFieldString("LOC_CD"));
				jrParam.setField("YD_AB_CD"	            ,jrecord.getFieldString("EXAM_CD"));
				jrParam.setField("YD_AB_CD2"	        ,jrecord.getFieldString("EXAM_CD2") );
				jrParam.setField("LABEL_YN"	            ,jrecord.getFieldString("LABEL_CD"));
				jrParam.setField("TRANS_ORD_DATE"	    ,jrecord.getFieldString("TRANS_WORD_NO"));
				jrParam.setField("TRANS_ORD_SEQNO"	    ,jrecord.getFieldString("TRANS_WORD_SEQNO"));
				jrParam.setField("STL_NO"	            ,jrecord.getFieldString("STOCK_ID"));
				//jrParam.setField("MODIFIER"			,commUtils.trim(jrecord.getFieldString("userid")));
				
				//검수 완료 처리
				/*com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateCarExaminationGoodsDetjlNEW2
			     UPDATE TB_YD_EXAMINATIONCHKLIST
				   SET YD_CAR_UPP_LOC_CD=NVL(:V_YD_CAR_UPP_LOC_CD,YD_CAR_UPP_LOC_CD)
				  , YD_AB_CD            =:V_YD_AB_CD
				  , YD_AB_CD2           =:V_YD_AB_CD2
				  , LABEL_YN            =:V_LABEL_YN
				  , CHECKING_YN         ='Y'
				  , MODIFIER            ='YDPDA'
				  , MOD_DDTT            = SYSDATE
				WHERE TRANS_ORD_DATE    =:V_TRANS_ORD_DATE
				  AND TRANS_ORD_SEQNO   =:V_TRANS_ORD_SEQNO
				  AND STL_NO =:V_STL_NO*/
				  

				count=commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateCarExaminationGoodsDetjlNEW2", logId, methodNm, "B열연검수완료처리");
			 
				
				//검수 완료 TC 전송 가능 유무 체크 
				sTRANS_ORD_DATE =jrecord.getFieldString("TRANS_WORD_NO");
				sTRANS_ORD_SEQNO =jrecord.getFieldString("TRANS_WORD_SEQNO");
				
				return jrRtn;
				
			}else{
				
				trans_ord_no = trans_ord_no.replaceAll("-", "");
				//검수 완료 TC 전송 가능 유무 체크 
				
				sTRANS_ORD_DATE =trans_ord_no.substring(0, 8);
				sTRANS_ORD_SEQNO =trans_ord_no.substring(8);
				
				/*String[] OrdArr = null;
				OrdArr = trans_ord_no.split("-");
				sTRANS_ORD_DATE =OrdArr[0];
				sTRANS_ORD_SEQNO =OrdArr[1];*/
				
				jrParam.setField("TRANS_ORD_DATE"	    ,sTRANS_ORD_DATE);
				jrParam.setField("TRANS_ORD_SEQNO"	    ,sTRANS_ORD_SEQNO);
				
				/*com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateCarExaminationGoodsDetjl2
				UPDATE TB_YD_EXAMINATIONCHKLIST
				SET LABEL_YN =NVL(LABEL_YN,'N')
				  , CHECKING_YN ='Y'
				  , MODIFIER='YDPDA2'
				  , MOD_DDTT= SYSDATE
				WHERE TRANS_ORD_DATE=:V_TRANS_ORD_DATE
				  AND TRANS_ORD_SEQNO=:V_TRANS_ORD_SEQNO
				*/
				//운송지시 단위 검수 완료 처리
				count=commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateCarExaminationGoodsDetjl2", logId, methodNm, "B열연검수완료처리");
 
			}
			
			/*com.inisteel.cim.ym.bcoil.dao.BCoilDAO.CarExaminationChk 
			SELECT CNT,CNT - CHECKING AS CHK ,CAR_NO
			    ,(SELECT TRANS_EQUIPMENT_TYPE FROM USRYDA.TB_YD_CARSCH B
			       WHERE B.TRANS_ORD_DATE=A.TRANS_ORD_DATE
			           AND B.TRANS_ORD_SEQNO=A.TRANS_ORD_SEQNO
			           AND B.CAR_NO=A.CAR_NO
			           AND ROWNUM<=1
			           ) AS TRANS_EQUIPMENT_TYPE
			  FROM (
			SELECT COUNT(*) AS CNT
			      ,SUM(CASE WHEN A.CHECKING_YN='Y' THEN 1 ELSE 0 END) AS CHECKING 
			      ,CAR_NO
			      ,TRANS_ORD_DATE
			      ,TRANS_ORD_SEQNO
			 FROM USRYDA.TB_YD_EXAMINATIONCHKLIST A
			 WHERE TRANS_ORD_DATE=:V_TRANS_ORD_DATE
			    AND TRANS_ORD_SEQNO=:V_TRANS_ORD_SEQNO
			    AND A.DEL_YN='N'
			   GROUP BY CAR_NO ,TRANS_ORD_DATE , TRANS_ORD_SEQNO
			   
			   ) A
			   */

			JDTORecordSet jsCarExamin = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.CarExaminationChk", logId, methodNm, "차량스케쥴 조회");
			

			if(jsCarExamin.size()>0){
				//jrecrd = (JDTORecord)StockList.get(0);
				cnt =  commUtils.trim(jsCarExamin.getRecord(0).getFieldString("CNT")) ; 
				chk =  commUtils.trim(jsCarExamin.getRecord(0).getFieldString("CHK")) ; 
				szTRANS_EQUIPMENT_TYPE = commUtils.trim(jsCarExamin.getRecord(0).getFieldString("TRANS_EQUIPMENT_TYPE")) ;
				
				if(chk.equals("0") && !cnt.equals("0") ){
					
					szMsg = "=============검수완료 전문 전송 시작 ========";
					commUtils.printLog(logId, szMsg, "SL");
					// 레코드생성-----------------------------------------------------------------
					//rsResult = JDTORecordFactory.getInstance().createRecordSet("");
					//recPara = JDTORecordFactory.getInstance().create();
					//recPara.setField("TRANS_ORD_DATE",  sTRANS_ORD_DATE);
					//recPara.setField("TRANS_ORD_SEQNO", sTRANS_ORD_SEQNO);
					//recPara.setField("CAR_NO", commUtils.trim(jsCarExamin.getRecord(0).getFieldString("CAR_NO")));
					
//					if ("Y".equals(sApplyYnPI)) {
						tcRecordDM.setField("MQ_TC_CD"              , new String("M10YDLMJ1101"));
						tcRecordDM.setField("MQ_TC_CREATE_DDTT"		, commUtils.getDateTime14());
						//PDA출하 인경우 
						if("P".equals(szTRANS_EQUIPMENT_TYPE)){
							tcRecordDM.setField("SCH_YN"            , "Y");
						}else{
							tcRecordDM.setField("SCH_YN"            , "N");
						}
						
						tcRecordDM.setField("TRN_REQ_DATE"			, sTRANS_ORD_DATE);
	                    tcRecordDM.setField("TRN_REQ_SEQ"			, sTRANS_ORD_SEQNO);
	                    tcRecordDM.setField("CAR_NO"				, commUtils.trim(jsCarExamin.getRecord(0).getFieldString("CAR_NO")));
	                    
	                    // PIDEV
	                    if("*".equals(sPI_YD)) {
	                    	tcRecordDM.setField("YD_GP"					, "3");	
	                    } else {
	                    	tcRecordDM.setField("YD_GP"					, sPI_YD);
	                    }
	                    
	                    tcRecordDM.setField("DIST_GOODS_GP"			, "H");
	                    tcRecordDM.setField("CARLD_CHK_DONE_DATE"	, YmCommUtils.getCurDate("yyyyMMdd"));
	                    tcRecordDM.setField("CARLD_CHK_DONE_TIME"	, YmCommUtils.getCurDate("HHmmss"));
//					} else {
//						//PDA출하 인경우 
//						if("P".equals(szTRANS_EQUIPMENT_TYPE)){
//							tcRecordDM.setField("JMS_TC_CD"             , new String("YDDMR074"));
//							msgId = "YDDMR074";
//						}else{
//							tcRecordDM.setField("JMS_TC_CD"             , new String("YDDMR036"));
//							msgId = "YDDMR036";
//						}
//						tcRecordDM.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14());
//						tcRecordDM.setField("TRANS_WORD_DATE"		, sTRANS_ORD_DATE);
//	                    tcRecordDM.setField("TRANS_WORD_SEQNO"		, sTRANS_ORD_SEQNO);
//	                    tcRecordDM.setField("CARLD_CHK_DONE_DATE"	, YmCommUtils.getCurDate("yyyyMMdd"));
//	                    tcRecordDM.setField("CARLD_CHK_DONE_TIME"	, YmCommUtils.getCurDate("HHmmss"));	
//	                    tcRecordDM.setField("CAR_NO", commUtils.trim(jsCarExamin.getRecord(0).getFieldString("CAR_NO")));
//					}
	                    
					//검수완료 TC대상 조회
                    /*com.inisteel.cim.ym.bcoil.dao.BCoilDAO.CarExaminationTCListNEW
                    SELECT TRANS_ORD_DATE as TRANS_WORD_DATE
                         , TRANS_ORD_SEQNO AS TRANS_WORD_SEQNO
                         , TO_CHAR(SYSDATE,'YYYYMMDD') AS CARLD_CHK_DONE_DATE
                         , TO_CHAR(SYSDATE,'HH24MISS') AS CARLD_CHK_DONE_TIME
                         , COUNT(*) OVER (PARTITION BY TRANS_ORD_DATE,TRANS_ORD_SEQNO) AS  GOODS_NO_CNT
                         , STL_NO AS GOODS_NO
                         , A.YD_GP
                         , YD_AB_CD AS GOODS_CHK_AB_CD
                         , YD_AB_CD2 AS GOODS_CHK_AB_CD2
                         , (CASE WHEN  ( SUBSTR(B.NEXT_PROC,2,1) IN('A')  
                                                                            OR EXISTS (SELECT 1
                                                                                  FROM TB_HR_C_SHEARWOWR SR
                                                                                 WHERE SR.HR_PLNT_GP = 'C'
                                                                                   AND SR.WORK_STAT  = '*'
                                                                                   AND SR.WORD_PROC  LIKE '%A'
                                                                                   AND SR.COIL_NO = A.STL_NO
                                                                                   AND SR.RECEIPT_HOLD_SCRAP_CAUSE_GP IN ('I','B')
                                                                                   AND SR.STEP_NO = (SELECT MAX(STEP_NO)
                                                                                                         FROM TB_HR_C_SHEARWOWR
                                                                                                        WHERE COIL_NO = SR.COIL_NO) 
                                                                                )
                                                                            ) THEN 'N' 
                                 WHEN A.LABEL_YN ='Y'
                                THEN 'Y' ELSE
                             (CASE WHEN (B.FNL_MATCH_ORDERTRANS_OCCURDATE||B.FNL_MATCH_ORDERTRANS_OCCURTIME)>= to_char(B.SHEAR_WORD_DT,'YYYYMMDDHH24MISS')
                                 THEN 'Y' ELSE USRDMA.FNC_DM_GET_GOODSPROGLABEL_YN @DL_SMDB (STL_NO) END)
                                 END ) AS LABEL_REISSUE_YN
                         , YD_CAR_UPP_LOC_CD
                         ,(CASE WHEN A.YD_GP IN('H','J') THEN (SELECT   CR_FRTOMOVE_GP
                                                                 FROM USRYDA.TB_YD_STOCK B
                                                              WHERE B.STL_NO=A.STL_NO)
                                WHEN A.YD_GP = '1' THEN (SELECT CR_FRTOMOVE_GP
                                                       FROM TB_YF_STOCK C
                                                      WHERE C.STL_NO=A.STL_NO)                
                                                ELSE (SELECT   CR_FRTOMOVE_GP
                                                         FROM USRYMA.TB_YM_STOCK C
                                                      WHERE C.STOCK_ID=A.STL_NO)
                                                
                            END) AS  CR_FRTOMOVE_GP
                         ,'N' AS MAKE_TC_TO_Y5
                         ,'Y' AS MAKE_TC_TO_A7
                     FROM USRYDA.TB_YD_EXAMINATIONCHKLIST A
                         ,USRYFA.VW_YF_COILCOMM B
                     WHERE 1=1
                       AND A.STL_NO = B.COIL_NO
                       AND DEL_YN='N'
                       AND TRANS_ORD_DATE=:V_TRANS_ORD_DATE
                       AND TRANS_ORD_SEQNO=:V_TRANS_ORD_SEQNO
                      ORDER BY A.MOD_DDTT
                      */ 

					String sQueryID = "";
					
					// PIDEV
//					if("Y".equals(sApplyYnPI)) {
						sQueryID = "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.CarExaminationTCListNEW_PIDEV";
//					} else {
//						sQueryID = "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.CarExaminationTCListNEW";
//					}
					
			    	JDTORecordSet rst = commDao.select(jrParam, sQueryID, logId, methodNm, "검수완료 TC대상 조회");
			    	
			    	
			    	//불량코일 발생시 L2로 전문 전송
					boolean isYmAbExist = false;
					boolean isYdAbExist = false;
					for (int i = 0; i < rst.size(); i++) {
				   		//jrecrd2 = (JDTORecord)StockList2.get(i);
				   		szCR_FRTOMOVE_GP = commUtils.trim(rst.getRecord(i).getFieldString("CR_FRTOMOVE_GP"));
				   		
	         			if(i ==0){
	         				tcRecordDM.setField("GOODS_NO_CNT", commUtils.trim(rst.getRecord(i).getFieldString("GOODS_NO_CNT")));			                    
	         			}
	         			
	         			tcRecordDM.setField("GOODS_NO" 			+ (1+i),commUtils.trim(rst.getRecord(i).getFieldString("GOODS_NO")));			         			
	                    tcRecordDM.setField("GOODS_CHK_AB_CD" 	+ (1+i),commUtils.trim(rst.getRecord(i).getFieldString("GOODS_CHK_AB_CD")));
	                    tcRecordDM.setField("LABEL_REISSUE_YN" 	+ (1+i),commUtils.trim(rst.getRecord(i).getFieldString("LABEL_REISSUE_YN")));
	                    tcRecordDM.setField("GDS_CARLD_LOC" 	+ (1+i),commUtils.trim(rst.getRecord(i).getFieldString("YD_CAR_UPP_LOC_CD")));
					
	                    //전문 전송 여부 flag
	                    String sendTcToY5 = StringHelper.evl(rst.getRecord(i).getFieldString("MAKE_TC_TO_Y5"), "");
	                    String sendTcToA7 = StringHelper.evl(rst.getRecord(i).getFieldString("MAKE_TC_TO_A7"), "");
	                    
	                    //이상코드 존재시, 
	                    if(!"".equals(StringHelper.evl(rst.getRecord(i).getFieldString("GOODS_CHK_AB_CD"), ""))){
	                    	//1열연제품 이상
	                    	if("Y".equals(sendTcToA7) 
	                    			&& "3".equals(StringHelper.evl(rst.getRecord(i).getFieldString("YD_GP"), ""))) isYmAbExist = true;
	                    	if("Y".equals(sendTcToY5) 
	                    			&& "J".equals(StringHelper.evl(rst.getRecord(i).getFieldString("YD_GP"), ""))) isYdAbExist = true;
	                    }
	                    
					}
					if(isYmAbExist){
						JDTORecord recInTemp  = JDTORecordFactory.getInstance().create();
						recInTemp.setResultCode(logId);	//Log ID
						recInTemp.setResultMsg(methodNm);	//Log Method Name
						recInTemp.setField("TRANS_ORD_DATE"  , sTRANS_ORD_DATE);
						recInTemp.setField("TRANS_ORD_SEQNO"  , sTRANS_ORD_SEQNO);
						
						
						szMsg = "=============1열연 불량코일 발생정보 전송========";
						commUtils.printLog(logId, szMsg, "SL");
						
						JDTORecord sndRecord = JDTORecordFactory.getInstance().create();
						sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2("YMA7L014", recInTemp));
						
						EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
						sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { sndRecord });
					}
					if(isYdAbExist){
						
						JDTORecord recInTemp  = JDTORecordFactory.getInstance().create();
						recInTemp.setResultCode(logId);	//Log ID
						recInTemp.setResultMsg(methodNm);	//Log Method Name
						
						recInTemp.setField("MSG_ID"  	      , "YDY5L009");
						recInTemp.setField("TRANS_ORD_DATE"   , sTRANS_ORD_DATE);
						recInTemp.setField("TRANS_ORD_SEQNO"  , sTRANS_ORD_SEQNO);
						
						szMsg = "=============2열연 불량코일 발생정보 전송========";
						commUtils.printLog(logId, szMsg, "SL");

						YdDelegate ydDelegate = new YdDelegate();
						
						ydDelegate.sendMsg(recInTemp);
					}	
					
					if(!szCR_FRTOMOVE_GP.equals("")){
						tcRecordDM.setField("CR_FRTOMOVE_GP"           , szCR_FRTOMOVE_GP);
					} else {
						tcRecordDM.setField("CR_FRTOMOVE_GP"           , "");
					}
					
					// PIDEV
//					if("N".equals(sApplyYnPI)) {
//						tcRecordDM.setField("TC_CODE", msgId);	
//					}
					
//						tcRecordDM.setField("TRANS_ORD_DATE", sTRANS_ORD_DATE);
//	                    tcRecordDM.setField("TRANS_ORD_SEQNO", sTRANS_ORD_SEQNO);
         			//인터페이스 전문 호출
					
					
				   //EJBConnector ejbConn1 = new EJBConnector("default","YmCommDAO",this);
				   //ejbConn1.trx("getMsgL3",new Class[]{String.class,JDTORecord.class}, new Object[]{ msgId,tcRecordDM}); 
				   
					jrRtn = commUtils.addSndData(jrRtn, tcRecordDM);
				   
				    szMsg= "내부IF호출=== 일관제철 코일제품검수완료.===";
					commUtils.printLog(logId, szMsg, "SL");
                    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                    

                    if("".equals(trans_ord_no)){
    					jrParam.setField("TRANS_ORD_DATE"	    ,jrecord.getFieldString("TRANS_ORD_DATE"));
    					jrParam.setField("TRANS_ORD_SEQNO"	    ,jrecord.getFieldString("TRANS_ORD_DATE"));
    					

                    }else{
    					jrParam.setField("TRANS_ORD_DATE"	    ,sTRANS_ORD_DATE);
    					jrParam.setField("TRANS_ORD_SEQNO"	    ,sTRANS_ORD_SEQNO);	      
                    	
                    }
					
					//검수 완료종료 처리
                    /*com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateCarExaminationGoodsEnd
                    UPDATE USRYDA.TB_YD_EXAMINATIONCHKLIST
                    SET DEL_YN='Y'
                     , MODIFIER='YDSYSTEM'
                     , MOD_DDTT=SYSDATE 
                    WHERE TRANS_ORD_DATE=:V_TRANS_ORD_DATE
                      AND TRANS_ORD_SEQNO=:V_TRANS_ORD_SEQNO 
                      AND DEL_YN='N'*/
                    count=commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateCarExaminationGoodsEnd", logId, methodNm, "B열연검수완료처리");
			    	
					szMsg= "====== 검수완료종료 처리  ========";
					commUtils.printLog(logId, szMsg, "SL");
				}
				szMsg="=============검수완료 전문 전송 완료 ========";
				commUtils.printLog(logId, szMsg, "SL");
			}
			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	/**
	 * 지포장 - 보급(1), 추출(3) 요구 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return JDTOrecord
	 * @throws DAOException
	 */
	public JDTORecord reqPapWrapInOut(GridData gdReq) throws DAOException {
		String methodNm = "지포장 - 보급,추출 요구[BCoilJspSeEJB.reqPapWrapInOut] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
	
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;
			JDTORecordSet rsResult = null;
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			//화면에서 전달 받은 파레메터 저장
			String sPROCESSID     = commUtils.trim(gdReq.getParam("PROCESSID")); 	  //1:보급, 3:추출
			String sYD_GP	      = commUtils.trim(gdReq.getParam("YD_GP"));	 	  //야드구분 		
			String sYD_BAY_GP     = commUtils.trim(gdReq.getParam("YD_BAY_GP")); 	  //동 정보
			String sSTOCK_ID      = commUtils.trim(gdReq.getParam("COIL_NO")); 		  //코일 번호
			String sSTACK_COL_BED = commUtils.trim(gdReq.getParam("STACK_COL_BED"));  //번지
			
			//파라메터 체크
			if(!"1".equals(sPROCESSID) && !"3".equals(sPROCESSID)) {
				throw new Exception("PROCESSID는 '1':보급,'3':추출만 가능합니다. 수신된 값은 '" + sPROCESSID + "' 입니다.");
			}
			if("".equals(sYD_GP)) {
				throw new Exception("YD_GP를 입력하지 않았습니다!.");
			}
			if("".equals(sYD_BAY_GP)) {
				throw new Exception("YD_BAY_GP를 입력하지 않았습니다!.");
			}
			if("".equals(sSTOCK_ID)) {
				throw new Exception("COIL_NO를 입력하지 않았습니다!.");
			}

			/**********************************************************
			* 1. TB_YM_STOCK 에 정보가 존재하는지 Check
			*    - 존재한다면 작업예약에 걸려있는지 Check 
			**********************************************************/
			jrParam.setField("STOCK_ID", sSTOCK_ID);
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectStockID", logId, methodNm, "TB_YM_STOCK 에 정보가 존재하는지 Check");
			if (rsResult == null || rsResult.size() <= 0) {
				throw new Exception("TB_YM_STOCK(YM_저장품) 에 존재하지 않는 COIL_NO 입니다!");
			} else {
				String sYD_WBOOK_ID = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_WBOOK_ID"));
				if(!"".equals(sYD_WBOOK_ID)) {
					throw new Exception("작업예약 ID:"+sYD_WBOOK_ID+" 에 등록된 COIL 입니다!");
				}
			}

			/**********************************************************
			* 2. TB_YM_STACKLAYER 에 적치상태  Check
			*    - 적치열,bed,단 정보를 가져온다.
			**********************************************************/
			String sSTACK_COL_GP = ""; //적치 열 구분
			String sSTACK_BED_GP = ""; //적치 BED 구분
			String sSTACK_LAYER_GP = ""; //적치 단 구분
			
			jrParam.setField("STOCK_ID", sSTOCK_ID);
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getselectStackColGp", logId, methodNm, "TB_YM_STACKLAYER 에 적치상태  Check");
			if (rsResult == null || rsResult.size() <= 0) {
				throw new Exception("TB_YM_STACKLAYER(YM_적치단) 에 존재하지 않는 COIL_NO 입니다!");
			} else {
				String sSTACK_LAYER_STAT = commUtils.trim(rsResult.getRecord(0).getFieldString("STACK_LAYER_STAT"));
				if(!"C".equals(sSTACK_LAYER_STAT) && !"L".equals(sSTACK_LAYER_STAT)) {
					throw new Exception("적치단 상태가 '적치중(C)' 이 아닙니다! 현재 상태 : " + sSTACK_LAYER_STAT);
				}
				
				sSTACK_COL_GP 	= commUtils.trim(rsResult.getRecord(0).getFieldString("STACK_COL_GP")); //적치 열 구분
				sSTACK_BED_GP 	= commUtils.trim(rsResult.getRecord(0).getFieldString("STACK_BED_GP")); //적치 BED 구분
				sSTACK_LAYER_GP	= commUtils.trim(rsResult.getRecord(0).getFieldString("STACK_LAYER_GP")); //적치 단 구분
				
			}
			
			/**********************************************************
			* 3. 스케줄 코드, 작업예약 ID 생성
			*    - 저장품 이동 조건 지정
			*    - 야드스케쥴 우선순위 검색
			**********************************************************/
			String sYD_SCH_CD = "";
			String sYD_WBOOK_ID = "";
			String sSTOCK_MOVE_TERM = ""; 	//저장품 이동 조건
			String sYD_SCH_PRIOR = "";		//야드스케쥴우선순위
			String sYD_SCH_REQ_GP = "";		//야드스케줄요청구분
			
			if("1".equals(sPROCESSID)) {
				//보급
				sYD_SCH_CD 			= sYD_GP + sYD_BAY_GP + "GF01UM"; //3EGF01UM
				sSTOCK_MOVE_TERM 	= YmConstant.NEW_STOCK_MOVE_TERM_GC; //종합판정대기 
				sYD_SCH_REQ_GP		= "U"; //조업설비 보급 스케줄 
				
			} else if("3".equals(sPROCESSID)) {
				//추출
				sYD_SCH_CD 			= sYD_GP + sYD_BAY_GP + "GF01LM"; //3EGF01LM
				sSTOCK_MOVE_TERM 	= YmConstant.NEW_STOCK_MOVE_TERM_A8; //지포장  추출 
				sYD_SCH_REQ_GP		= "L"; //조업설비 인출 스케줄 
			}
			sYD_WBOOK_ID = commDao.getSeqId(logId, methodNm, "WrkBook"); 
			
			//스케줄코드로 스케줄기준Table조회
			jrParam.setField("YD_SCH_CD", sYD_SCH_CD);
	    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule  
			SELECT YD_SCH_CD
			     , YD_WRK_CRN
			     , YD_WRK_CRN_PRIOR
			  FROM TB_YM_SCHEDULERULE
			 WHERE DEL_YN = 'N'
			   AND YD_SCH_CD = :V_YD_SCH_CD
			*/   
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
	    	
			if (rsResult != null && rsResult.size() > 0) {
				sYD_SCH_PRIOR = rsResult.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
			} else {
				throw new Exception("1열연 코일 스케쥴 코드 이상 : [" + sYD_SCH_CD + "]");
			}			
			
			/**********************************************************
			* 4. 작업예약(TB_YM_WRKBOOK) 생성
			**********************************************************/
			jrParam.setField("YD_WBOOK_ID"		, sYD_WBOOK_ID);
			jrParam.setField("YD_GP"			, sYD_GP);
			jrParam.setField("YD_BAY_GP"		, sYD_BAY_GP);
			jrParam.setField("YD_SCH_CD"		, sYD_SCH_CD); //야드스케쥴코드
			jrParam.setField("YD_SCH_PRIOR"		, sYD_SCH_PRIOR); //야드스케쥴우선순위
			jrParam.setField("YD_SCH_PROG_STAT"	, "W"); //야드스케쥴진행상태(W:스케줄수행대기)
			jrParam.setField("YD_SCH_ST_GP"		, "M"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업)
			jrParam.setField("YD_SCH_REQ_GP"	, sYD_SCH_REQ_GP); //야드스케쥴요청구분
			//보급인 경우
			if("1".equals(sPROCESSID)) {
				jrParam.setField("YD_TO_LOC_GUIDE"	, sYD_SCH_CD.substring(0, 6)+sSTACK_COL_BED);
			}
			commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insWrkBook", logId, methodNm, "작업예약(TB_YM_WRKBOOK) 생성");

			/**********************************************************
			* 5. 작업예약재료(TB_YM_WRKBOOKMTL) 생성
			**********************************************************/
			jrParam.setField("YD_WBOOK_ID"		, sYD_WBOOK_ID);
			jrParam.setField("STOCK_ID"			, sSTOCK_ID);
			jrParam.setField("STACK_COL_GP"		, sSTACK_COL_GP);
			jrParam.setField("STACK_BED_GP"		, sSTACK_BED_GP);
			jrParam.setField("STACK_LAYER_GP"	, sSTACK_LAYER_GP);
			
			commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insYmWrkBookMtl", logId, methodNm, "작업예약재료(TB_YM_WRKBOOKMTL) 생성");
			
			/**********************************************************
			* 6. TB_YM_STOCK의 저장품 이동 조건(STOCK_MOVE_TERM) 변경
			**********************************************************/
			jrParam.setField("STOCK_ID"			, sSTOCK_ID);
			jrParam.setField("STOCK_MOVE_TERM"	, sSTOCK_MOVE_TERM);
			
			commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStockTransInfo", logId, methodNm, "TB_YM_STOCK의 저장품 이동 조건(STOCK_MOVE_TERM) 변경");
			
			/**********************************************************
			* 7. 크레인스케줄 전문 호출
			**********************************************************/
			jrParam.setField("YD_WBOOK_ID"  	, sYD_WBOOK_ID); //야드작업예약ID
			jrParam.setField("YD_SCH_CD"    	, sYD_SCH_CD  ); //야드스케쥴코드
			jrParam.setField("YD_SCH_ST_GP" 	, "M"      ); //야드스케쥴기동구분
			jrParam.setField("YD_SCH_REQ_GP"	, sYD_SCH_REQ_GP); //야드스케쥴요청구분
			
			jrRtn = commUtils.addSndData(jrRtn, ymComm.getCrnSchMsg(jrParam));
			
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of reqPapWrapInOut
	
	/**
	 * 기준관리 - 세부항목수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updYmRule(GridData gdReq) throws DAOException {
		String methodNm = "기준관리 - 세부항목수정[BCoilJspSeEJB.updYmRule] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
	
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			String REPR_CD_GP = gdReq.getParam("REPR_CD_GP");
			if(REPR_CD_GP.equals("CR0001") || REPR_CD_GP.equals("CR0002") || REPR_CD_GP.equals("CR0003")){
				
				jrParam.setField("MODIFIER"				, gdReq.getParam("userid"));
				jrParam.setField("REPR_CD_GP"			, REPR_CD_GP );
				jrParam.setField("CD_GP"				, gdReq.getParam("CR_GP") );
				jrParam.setField("REPR_CD_CONTENTS"		, gdReq.getParam("REPR_CD_NAME"));
				
				commDao.delete(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.delYmRuleCr", logId, methodNm, "크레인작업범위 삭제");
				
				for (int ii = 0; ii < rowCnt; ii++) {
					jrParam.setField("ITEM"					, commUtils.getValue(gdReq, "STACK_COL_GP", ii));	
					//세부항목수정
					if(!commUtils.getValue(gdReq, "DTL_ITM1", ii).equals("")){
						jrParam.setField("DTL_ITM1"		, commUtils.getValue(gdReq, "DTL_ITM1", ii) );
					}
					if(!commUtils.getValue(gdReq, "DTL_ITM2", ii).equals("")){
						jrParam.setField("DTL_ITM2"		, commUtils.getValue(gdReq, "DTL_ITM2", ii) );
					}
					if(!commUtils.getValue(gdReq, "DTL_ITM3", ii).equals("")){
						jrParam.setField("DTL_ITM3"		, commUtils.getValue(gdReq, "DTL_ITM3", ii) );
					}
					if(!commUtils.getValue(gdReq, "DTL_ITM4", ii).equals("")){
						jrParam.setField("DTL_ITM4"		, commUtils.getValue(gdReq, "DTL_ITM4", ii) );
					}
					if(!commUtils.getValue(gdReq, "DTL_ITM5", ii).equals("")){
						jrParam.setField("DTL_ITM5"		, commUtils.getValue(gdReq, "DTL_ITM5", ii) );
					}
					if(!commUtils.getValue(gdReq, "DTL_ITM6", ii).equals("")){
						jrParam.setField("DTL_ITM6"		, commUtils.getValue(gdReq, "DTL_ITM6", ii) );
					}
					if(!commUtils.getValue(gdReq, "DTL_ITM7", ii).equals("")){
						jrParam.setField("DTL_ITM7"		, commUtils.getValue(gdReq, "DTL_ITM7", ii) );
					}
					if(!commUtils.getValue(gdReq, "DTL_ITM8", ii).equals("")){
						jrParam.setField("DTL_ITM8"		, commUtils.getValue(gdReq, "DTL_ITM8", ii) );
					}
					if(!commUtils.getValue(gdReq, "DTL_ITM9", ii).equals("")){
						jrParam.setField("DTL_ITM9"		, commUtils.getValue(gdReq, "DTL_ITM9", ii) );
					}
					if(!commUtils.getValue(gdReq, "DTL_ITM10", ii).equals("")){
						jrParam.setField("DTL_ITM10"	, commUtils.getValue(gdReq, "DTL_ITM10", ii) );
					}

					commDao.insert(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.insCYmRuleCr", logId, methodNm, "크레인작업범위 등록");
				}
			}else if(REPR_CD_GP.equals("APP062")) {
				
				for (int ii = 0; ii < rowCnt; ii++) {
					
					if("".equals(commUtils.getValue(gdReq, "DTL_ITM1", ii))) {
						//전화번호 삭제처리
						jrParam.setField("MODIFIER"		, gdReq.getParam("userid"));
						jrParam.setField("REPR_CD_GP"	, commUtils.getValue(gdReq, "REPR_CD_GP", ii) );
						jrParam.setField("CD_GP"		, commUtils.getValue(gdReq, "CD_GP", ii) );
						jrParam.setField("ITEM"			, commUtils.getValue(gdReq, "ITEM", ii));
						jrParam.setField("DEL_YN"		, "Y"); 
						commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updYmRule", logId, methodNm, "기준관리 수정");
						
					} else {
						//세부항목수정
						if(!commUtils.getValue(gdReq, "DTL_ITM1", ii).equals("")){
							jrParam.setField("DTL_ITM1"		, commUtils.getValue(gdReq, "DTL_ITM1", ii) );
						}
						if(!commUtils.getValue(gdReq, "DTL_ITM2", ii).equals("")){
							jrParam.setField("DTL_ITM2"		, commUtils.getValue(gdReq, "DTL_ITM2", ii) );
						}
						if(!commUtils.getValue(gdReq, "DTL_ITM3", ii).equals("")){
							jrParam.setField("DTL_ITM3"		, commUtils.getValue(gdReq, "DTL_ITM3", ii) );
						}
						if(!commUtils.getValue(gdReq, "DTL_ITM4", ii).equals("")){
							jrParam.setField("DTL_ITM4"		, commUtils.getValue(gdReq, "DTL_ITM4", ii) );
						}
						if(!commUtils.getValue(gdReq, "DTL_ITM5", ii).equals("")){
							jrParam.setField("DTL_ITM5"		, commUtils.getValue(gdReq, "DTL_ITM5", ii) );
						}
						if(!commUtils.getValue(gdReq, "DTL_ITM6", ii).equals("")){
							jrParam.setField("DTL_ITM6"		, commUtils.getValue(gdReq, "DTL_ITM6", ii) );
						}
						if(!commUtils.getValue(gdReq, "DTL_ITM7", ii).equals("")){
							jrParam.setField("DTL_ITM7"		, commUtils.getValue(gdReq, "DTL_ITM7", ii) );
						}
						if(!commUtils.getValue(gdReq, "DTL_ITM8", ii).equals("")){
							jrParam.setField("DTL_ITM8"		, commUtils.getValue(gdReq, "DTL_ITM8", ii) );
						}
						if(!commUtils.getValue(gdReq, "DTL_ITM9", ii).equals("")){
							jrParam.setField("DTL_ITM9"		, commUtils.getValue(gdReq, "DTL_ITM9", ii) );
						}
						if(!commUtils.getValue(gdReq, "DTL_ITM10", ii).equals("")){
							jrParam.setField("DTL_ITM10"	, commUtils.getValue(gdReq, "DTL_ITM10", ii) );
						}
						jrParam.setField("MODIFIER"		, gdReq.getParam("userid"));
						jrParam.setField("REPR_CD_GP"	, commUtils.getValue(gdReq, "REPR_CD_GP", ii) );
						jrParam.setField("CD_GP"		, commUtils.getValue(gdReq, "CD_GP", ii) );
						jrParam.setField("ITEM"			, commUtils.getValue(gdReq, "ITEM", ii));
						jrParam.setField("DEL_YN"		, "N"); 
						commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updYmRule", logId, methodNm, "기준관리 수정");
					}
				}
				
			}else{
				for (int ii = 0; ii < rowCnt; ii++) {
					//세부항목수정
					if(!commUtils.getValue(gdReq, "DTL_ITM1", ii).equals("")){
						jrParam.setField("DTL_ITM1"		, commUtils.getValue(gdReq, "DTL_ITM1", ii) );
					}
					if(!commUtils.getValue(gdReq, "DTL_ITM2", ii).equals("")){
						jrParam.setField("DTL_ITM2"		, commUtils.getValue(gdReq, "DTL_ITM2", ii) );
					}
					if(!commUtils.getValue(gdReq, "DTL_ITM3", ii).equals("")){
						jrParam.setField("DTL_ITM3"		, commUtils.getValue(gdReq, "DTL_ITM3", ii) );
					}
					if(!commUtils.getValue(gdReq, "DTL_ITM4", ii).equals("")){
						jrParam.setField("DTL_ITM4"		, commUtils.getValue(gdReq, "DTL_ITM4", ii) );
					}
					if(!commUtils.getValue(gdReq, "DTL_ITM5", ii).equals("")){
						jrParam.setField("DTL_ITM5"		, commUtils.getValue(gdReq, "DTL_ITM5", ii) );
					}
					if(!commUtils.getValue(gdReq, "DTL_ITM6", ii).equals("")){
						jrParam.setField("DTL_ITM6"		, commUtils.getValue(gdReq, "DTL_ITM6", ii) );
					}
					if(!commUtils.getValue(gdReq, "DTL_ITM7", ii).equals("")){
						jrParam.setField("DTL_ITM7"		, commUtils.getValue(gdReq, "DTL_ITM7", ii) );
					}
					if(!commUtils.getValue(gdReq, "DTL_ITM8", ii).equals("")){
						jrParam.setField("DTL_ITM8"		, commUtils.getValue(gdReq, "DTL_ITM8", ii) );
					}
					if(!commUtils.getValue(gdReq, "DTL_ITM9", ii).equals("")){
						jrParam.setField("DTL_ITM9"		, commUtils.getValue(gdReq, "DTL_ITM9", ii) );
					}
					if(!commUtils.getValue(gdReq, "DTL_ITM10", ii).equals("")){
						jrParam.setField("DTL_ITM10"	, commUtils.getValue(gdReq, "DTL_ITM10", ii) );
					}
					jrParam.setField("MODIFIER"		, gdReq.getParam("userid"));
					jrParam.setField("REPR_CD_GP"	, commUtils.getValue(gdReq, "REPR_CD_GP", ii) );
					jrParam.setField("CD_GP"		, commUtils.getValue(gdReq, "CD_GP", ii) );
					jrParam.setField("ITEM"			, commUtils.getValue(gdReq, "ITEM", ii));
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updYmRule", logId, methodNm, "기준관리 수정");
				}
			}
			

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updYmRule

	/**
	 * 기준관리 - 등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord regYmRule(GridData gdReq) throws DAOException {
		String methodNm = "기준관리 - 등록[BCoilJspSeEJB.regYmRule] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
	
			String REPR_CD_GP = gdReq.getParam("REPR_CD_GP");
			
			if("APP062".equals(REPR_CD_GP)) {

				jrParam.setField("REPR_CD_GP"			, REPR_CD_GP );
				jrParam.setField("CD_GP"				, gdReq.getParam("CD_GP") );
				jrParam.setField("ITEM"					, gdReq.getParam("APP062_ITEM") );
				
				JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmRule2", logId, methodNm, "기준 조회");
				
				if(rsResult.size() > 0) {

					jrParam.setField("REPR_CD_GP"			, REPR_CD_GP );
					jrParam.setField("CD_GP"				, gdReq.getParam("CD_GP") );
					jrParam.setField("ITEM"					, gdReq.getParam("APP062_ITEM") );
					jrParam.setField("REPR_CD_CONTENTS"		, gdReq.getParam("REPR_CD_NAME"));
					jrParam.setField("DTL_ITM1"				, gdReq.getParam("APP062_DTL_ITM1") );
					jrParam.setField("DTL_ITM2"				, gdReq.getParam("APP062_DTL_ITM2") );
					jrParam.setField("DTL_ITM3"				, gdReq.getParam("APP062_DTL_ITM3") );
					jrParam.setField("DEL_YN"				, "N" );
					
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updYmRule", logId, methodNm, "기준관리 수정");
					
				} else {
				
					jrParam.setField("REPR_CD_GP"			, REPR_CD_GP );
					jrParam.setField("CD_GP"				, gdReq.getParam("CD_GP") );
					jrParam.setField("ITEM"					, gdReq.getParam("APP062_ITEM") );
					jrParam.setField("REPR_CD_CONTENTS"		, gdReq.getParam("REPR_CD_NAME"));
					jrParam.setField("DTL_ITM1"				, gdReq.getParam("APP062_DTL_ITM1") );
					jrParam.setField("DTL_ITM2"				, gdReq.getParam("APP062_DTL_ITM2") );
					jrParam.setField("DTL_ITM3"				, gdReq.getParam("APP062_DTL_ITM3") );
					
					commDao.insert(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.insCYmRuleCr", logId, methodNm, "카톡 알람 수신 전화번호  등록");
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of regYmRule
	
	/**
	 *      [A] 오퍼레이션명 : 이송지시 취소
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updFtmvWrkCancel(GridData gdReq) throws DAOException {
		String methodNm = "이송지시 취소[BCoilJspSeEJB.updFtmvWrkCancel] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;
		    
		    String stlNo = "";
			String sposWlocCd = "";
			String arrWlocCd = "";
			String ordYeojaeGp = "";
			String reWoLmtRsnCd = "";
			String reWoLmtYn = "";
		    
			//처리완료한 야드작업예약ID
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				stlNo 		 = commUtils.getValue(gdReq, "STOCK_ID", ii);
				sposWlocCd   = commUtils.getValue(gdReq, "SPOS_WLOC_CD", ii);
				arrWlocCd 	 = commUtils.getValue(gdReq, "ARR_WLOC_CD", ii);
				ordYeojaeGp  = commUtils.getValue(gdReq, "ORD_YEOJAE_GP", ii);
				reWoLmtRsnCd = commUtils.getValue(gdReq, "RE_WO_LMT_RSN_CD", ii);
				
				/**********************************************************
				* 3. 재료단위 이송지시 취소 작업 전문
				**********************************************************/
				if(reWoLmtRsnCd.equals("X")){
					reWoLmtRsnCd = "";
					reWoLmtYn = "N";
				}else{
					reWoLmtYn = "Y";
				}
				
				//재료단위 이송지시 취소 작업 전문 - Log ID, Method, 수정자 Set
				JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

				jrYdMsg.setField("JMS_TC_CD"       , YmConstant.YDPTJ007);	//크레인작업지시요구
				jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14());
				jrYdMsg.setField("STL_NO", stlNo);
				jrYdMsg.setField("SPOS_WLOC_CD", sposWlocCd);
				jrYdMsg.setField("ARR_WLOC_CD", arrWlocCd);
				jrYdMsg.setField("ORD_YEOJAE_GP", ordYeojaeGp);
				jrYdMsg.setField("RE_WO_LMT_RSN_CD", reWoLmtRsnCd); //그리드 콤보 값
				jrYdMsg.setField("RE_WO_LMT_YN", reWoLmtYn); //기본값 Y - RE_WO_LMT_RSN_CD X값이면 N
				jrYdMsg.setField("CANCEL_DATE", commUtils.getDate8());

				jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL3(YmConstant.YDPTJ007, jrYdMsg));
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
	 *      [A] 오퍼레이션명 : 이송지시 취소2 (스케줄 취소, 작업예약 취소)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updFtmvWrkCancel2(GridData gdReq) throws DAOException {
		String methodNm = "이송지시 취소2[BCoilJspSeEJB.updFtmvWrkCancel2] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			String ydCrnSchId = ""; //야드크레인스케쥴ID
			String ydWbookId  = ""; //야드작업예약ID
		    String ydEqpId    = ""; //야드설비ID
		    String ydSchCd    = ""; //야드스케쥴코드
		    String stlNo 	  = "";

		    JDTORecordSet rsResult = null;
		    
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			//처리완료한 야드작업예약ID
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String[] arrYdWbookId = new String[rowCnt];
			
			//크레인 스케줄 취소 ------------------------------------------------------------------------------
			for (int ii = 0; ii < rowCnt; ii++) {
				
				stlNo 		 = commUtils.getValue(gdReq, "STOCK_ID", ii);

				/*com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getSchInfo
				SELECT A.YD_CRN_SCH_ID --야드크레인스케쥴ID
				      ,C.YD_WBOOK_ID --야드작업예약ID
				      ,A.YD_EQP_ID --야드설비ID 
				      ,A.YD_SCH_CD --야드스케쥴코드 YD_SCH_CD
				  FROM TB_YM_CRNSCH A,
				       TB_YM_CRNWRKMTL B,
				       TB_YM_WRKBOOK C,
				       TB_YM_WRKBOOKMTL D
				 WHERE A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID
				 AND C.YD_WBOOK_ID = D.YD_WBOOK_ID
				 AND D.STOCK_ID = B.STOCK_ID
				 AND B.STOCK_ID = :V_STOCK_ID
				 AND A.DEL_YN = 'N'
				 AND C.DEL_YN = 'N' */
				jrParam.setField("STOCK_ID", stlNo);
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getSchInfo", logId, methodNm, "스케줄조회");
				if (rsResult == null || rsResult.size() <= 0) {
					 
			    }else{
					ydWbookId  = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_WBOOK_ID")); //야드크레인스케쥴ID
					ydCrnSchId = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CRN_SCH_ID")); //야드크레인스케쥴ID
					ydEqpId    = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_EQP_ID")); //야드크레인스케쥴ID
					ydSchCd    = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_SCH_CD")); //야드크레인스케쥴ID
					
				    //작업할  야드작업예약ID가 작업 완료한 야드작업예약ID에 있으면 이전에 모두 처리되었으므로 Skip
					if (commUtils.chkExist(arrYdWbookId, ydWbookId)) { continue; }
					arrYdWbookId[ii] = ydWbookId;
					
					if(ydSchCd.length() >= 8) {
						
						if("PT02UM".equals(ydSchCd.substring(2,8))||"PT06UM".equals(ydSchCd.substring(2,8))) {
							//이송상차 스케줄만 취소처리한다.
						
							commUtils.printLog(logId, "작업취소 [ STOCK_ID:"+ stlNo +" ,작업예약ID:"+ ydWbookId + " 관련 크레인 스케줄 ]", "SL");
			
							jrParam.setField("YD_WBOOK_ID"  , ydWbookId );
							jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);
							jrParam.setField("YD_EQP_ID"    , ydEqpId   );
							jrParam.setField("YD_SCH_CD"    , ydSchCd   );
							
							/**********************************************************
							* 1. 크레인스케줄 취소
							**********************************************************/
							try {
								jrRtn = commUtils.addSndData(jrRtn, this.trtCrnSchCncl(jrParam));
							} catch (DAOException e) {
							} catch (Exception e) {
							}
			
							/**********************************************************
							* 2. 작업예약 취소
							**********************************************************/
							try {
								jrRtn = commUtils.addSndData(jrRtn, this.trtWrkBookCncl(jrParam));
							} catch (DAOException e) {
							} catch (Exception e) {
							}
						}
					}
			    }
			}
			
			//작업예약 취소 (작업예약만 있는 경우) -------------------------------------------------------------------
			for (int ii = 0; ii < rowCnt; ii++) {

				/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getWrkBookInfo 
				SELECT  A.YD_WBOOK_ID --야드작업예약ID
				       ,A.YD_SCH_CD --야드스케쥴코드 YD_SCH_CD
				       ,(SELECT C.YD_CRN_SCH_ID FROM TB_YM_CRNSCH C WHERE C.YD_WBOOK_ID = A.YD_WBOOK_ID AND ROWNUM = 1) AS YD_CRN_SCH_ID
				  FROM  TB_YM_WRKBOOK A,
				        TB_YM_WRKBOOKMTL B
				 WHERE  A.YD_WBOOK_ID = B.YD_WBOOK_ID
				   AND  B.STOCK_ID = :V_STOCK_ID
				   AND  A.DEL_YN = 'N'
				   AND  B.DEL_YN = 'N' */
				jrParam.setField("STOCK_ID", stlNo);
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getWrkBookInfo", logId, methodNm, "스케줄조회");
				if (rsResult == null || rsResult.size() <= 0) {
					 
			    }else{
					ydWbookId  = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_WBOOK_ID")); //야드크레인스케쥴ID
					ydSchCd    = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_SCH_CD")); //야드크레인스케쥴ID
					ydCrnSchId = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CRN_SCH_ID")); //야드크레인스케쥴ID
					
					if(ydSchCd.length() >= 8 && "".equals(ydCrnSchId)) {
						
						if("PT02UM".equals(ydSchCd.substring(2,8))||"PT06UM".equals(ydSchCd.substring(2,8))) {
							//이송상차 스케줄만 취소처리한다.
						
							commUtils.printLog(logId, "작업취소 [ STOCK_ID:"+ stlNo +" ,작업예약ID:"+ ydWbookId + " ]", "SL");
			
							jrParam.setField("YD_WBOOK_ID"  , ydWbookId );
							jrParam.setField("YD_SCH_CD"    , ydSchCd   );
							
							/**********************************************************
							* 2. 작업예약 취소
							**********************************************************/
							try {
								jrRtn = commUtils.addSndData(jrRtn, this.trtWrkBookCncl(jrParam));
							} catch (DAOException e) {
							} catch (Exception e) {
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
	 *      [A] 오퍼레이션명 : 상차대상순위별조회 긴급작업
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updUgntWrk(GridData gdReq) throws DAOException {
		String methodNm = "긴급작업[BCoilJspSeEJB.updUgntWrk] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

		    String stlNo = "";
		    
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			jrParam.setField("MODIFIER", commUtils.trim(gdReq.getParam("userid")));
			/*
			 UPDATE TB_TS_MATL_FTMV_WO A
			SET MTL_UGNT_GP = 'P'
			 , MODIFIER = :V_MODIFIER
			 , MOD_DDTT = sysdate 
			 where MATL_FTMV_WO_NML_HD_YN   IN('Y','X')    	
			   and TS_MATL_FTMV_STAT_GP     = '1'
			   and SPOS_WLOC_CD IN ('D3Y41','D3Y42')
			   AND DEL_YN='N'
			   AND MTL_UGNT_GP IN('P','Y')
			   AND TRANSWORD_SEQNO=(SELECT MAX(TRANSWORD_SEQNO) FROM TB_TS_MATL_FTMV_WO B
			                         WHERE A.STL_NO=B.STL_NO ) 
			 */
			commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updTSmatlftmvwoUgntgp", logId, methodNm, "기존 긴급재 삭제");
			
			//처리완료한 야드작업예약ID
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				stlNo 		 = commUtils.getValue(gdReq, "STOCK_ID", ii);

				jrParam.setField("STL_NO", stlNo);
				
				/*
				 UPDATE TB_TS_MATL_FTMV_WO A
				SET MTL_UGNT_GP = 'Y'
				 , MODIFIER = :V_MODIFIER
				 , MOD_DDTT = sysdate 
				WHERE STL_NO = :V_STL_NO
				  AND DEL_YN = 'N'
				  AND MTL_UGNT_GP = 'N'
				  AND TRANSWORD_SEQNO=(SELECT MAX(TRANSWORD_SEQNO) FROM TB_TS_MATL_FTMV_WO B
				 */
				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updTSmatlftmvwoUgntCHK", logId, methodNm, "새로운 긴급재 편성");
			}

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
	 * 상차대상순위별조회 - SCH기동
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return JDTOrecord
	 * @throws DAOException
	 */
	public JDTORecord reqCarldSchSt(GridData gdReq) throws DAOException {
		String methodNm = "상차대상순위별조회 - SCH기동[BCoilJspSeEJB.reqCarldSchSt] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
	
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;
			JDTORecordSet rsResult = null;
			
			String sSTOCK_ID 			= null;
			String szTRN_EQP_CD 		= null;
			String szYD_CAR_SCH_ID 		= null;
			String szYD_CAR_PROG_STAT 	= null;
			String szYD_CARLD_STOP_LOC	= null;
			String szYD_GP				= null;
			String szSTACK_BAY_GP		= null;
			String sSTACK_COL_GP 		= null; //적치 열 구분
			String sSTACK_BED_GP 		= null; //적치 BED 구분
			String sSTACK_LAYER_GP 		= null; //적치 단 구분
			String sYD_SCH_CD 			= null;
			String sYD_WBOOK_ID 		= null;
			String sSTOCK_MOVE_TERM 	= null;	//저장품 이동 조건
			String sYD_SCH_PRIOR 		= null;	//야드스케쥴우선순위
			String sYD_SCH_REQ_GP 		= null;	//야드스케줄요청구분
			String sPoint				= null;
			String sGP					= null; //소재,제품 구분
			String szFRTOMOVE_WORD_NO   = null; //이송작업지시 번호
			int    iMTL_CNT				= 0;    //해당차량 상차대상 COIL 갯수
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				sSTOCK_ID = commUtils.getValue(gdReq, "STOCK_ID", ii);
				
				/**********************************************************
				* 1. 이전 작업예약 존재 유무 Check
				**********************************************************/
				jrParam.setField("STOCK_ID", sSTOCK_ID);
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getWBookID3 
				SELECT  A.*
				       ,B.STOCK_ID
				       ,B.STACK_COL_GP
				       ,B.STACK_BED_GP
				       ,B.STACK_LAYER_GP
				       ,B.YD_UP_COLL_SEQ
				  FROM  TB_YM_WRKBOOK A
				       ,TB_YM_WRKBOOKMTL B
				 WHERE  A.DEL_YN = 'N'
				   AND  B.DEL_YN = 'N'
				   AND  A.YD_WBOOK_ID = B.YD_WBOOK_ID
				   AND  B.STOCK_ID = :V_STOCK_ID */
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getWBookID3", logId, methodNm, " 이전 작업예약 존재 유무 Check");
				if(rsResult.size() > 0) {
					throw new Exception("COIL번호 '"+sSTOCK_ID+"' 는 작업예약 ID:"+commUtils.trim(rsResult.getRecord(0).getFieldString("YD_WBOOK_ID"))+" 에 등록된 COIL 입니다!");
				}
				
				szTRN_EQP_CD = commUtils.trim(gdReq.getParam("TRN_EQP_CD"));
				/**********************************************************
				* 2. 차량정보 검색
				**********************************************************/
				jrParam.setField("YD_CAR_USE_GP", "L");
				jrParam.setField("TRN_EQP_CD"	, szTRN_EQP_CD);
				jrParam.setField("STL_NO"		, sSTOCK_ID);
//				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListSposYNchk3 
//				SELECT A.*
//				      ,(SELECT COUNT(*) FROM TB_YM_STOCK WHERE FRTOMOVE_WORD_NO = A.FRTOMOVE_WORD_NO) AS MTL_CNT
//				  FROM TB_YD_CARSCH A
//				 WHERE A.TRN_EQP_CD = :V_TRN_EQP_CD
//				   AND A.DEL_YN = 'N'
//				   AND A.ARR_WLOC_CD = (
//				   
//				                         SELECT B.ARR_WLOC_CD 
//				                           FROM TB_PT_STLFRTOMOVE B
//				                          WHERE B.STL_NO = :V_STL_NO
//				                            AND B.TRANSWORD_SEQNO = (
//				                                                      
//				                                                      SELECT /*+ INDEX_DESC(C PK_PT_STLFRTOMOVE)*/ 
//				                                                             MAX(TRANSWORD_SEQNO)
//				                                                        FROM TB_PT_STLFRTOMOVE C
//				                                                       WHERE ROWNUM <= 1
//				                                                         AND B.STL_NO = C.STL_NO
//				                                                         AND C.FRTOMOVE_STAT_CD IN('1','3')
//				                          
//				                                                    )
//				   
//				                       ) */
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getListSposYNchk3", logId, methodNm, " 운송장비 코드와 재료번호로 이송대상 차량스케줄 조회");
				if(rsResult.size() > 0) {
					szYD_CAR_SCH_ID 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CAR_SCH_ID"));
					szYD_CAR_PROG_STAT	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CAR_PROG_STAT"));
					szYD_CARLD_STOP_LOC = commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CARLD_STOP_LOC"));
					szYD_GP 			= szYD_CARLD_STOP_LOC.substring(0, 1);
					szSTACK_BAY_GP		= szYD_CARLD_STOP_LOC.substring(1, 2);
					
					szFRTOMOVE_WORD_NO  = commUtils.trim(rsResult.getRecord(0).getFieldString("FRTOMOVE_WORD_NO"));
					iMTL_CNT			= rsResult.getRecord(0).getFieldInt("MTL_CNT");
					
					if(iMTL_CNT >= 4) {
						throw new Exception("FRTOMOVE_WORD_NO : '"+szFRTOMOVE_WORD_NO+"', 운송장비코드: " + szTRN_EQP_CD + " 로 상차대상이 이미 4 개 이상 입니다!!");
					}
					
				} else {
					throw new Exception("COIL번호: '"+sSTOCK_ID+"', 운송장비코드: " + szTRN_EQP_CD + " 로 차량스케줄을 찾지 못했습니다!!");
				}
				
				if("2".equals(szYD_CAR_PROG_STAT)||"3".equals(szYD_CAR_PROG_STAT)||"4".equals(szYD_CAR_PROG_STAT)) {
					//2:상차도착 ,3:상차검수, 4:상차개시
					
					/**********************************************************
					* 3. TB_YM_STACKLAYER 에 적치상태  Check
					*    - 적치열,bed,단 정보를 가져온다.
					**********************************************************/
					jrParam.setField("STOCK_ID", sSTOCK_ID);
					rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getselectStackColGp", logId, methodNm, "TB_YM_STACKLAYER 에 적치상태  Check");
					if (rsResult == null || rsResult.size() <= 0) {
						throw new Exception("TB_YM_STACKLAYER(YM_적치단) 에 존재하지 않는 COIL_NO 입니다!");
					} else {
						String sSTACK_LAYER_STAT = commUtils.trim(rsResult.getRecord(0).getFieldString("STACK_LAYER_STAT"));
						if(!"C".equals(sSTACK_LAYER_STAT) && !"L".equals(sSTACK_LAYER_STAT)) {
							throw new Exception("적치단 상태가 '적치중(C)' 이 아닙니다! 현재 상태 : " + sSTACK_LAYER_STAT);
						}
						
						sSTACK_COL_GP 	= commUtils.trim(rsResult.getRecord(0).getFieldString("STACK_COL_GP")); //적치 열 구분
						sSTACK_BED_GP 	= commUtils.trim(rsResult.getRecord(0).getFieldString("STACK_BED_GP")); //적치 BED 구분
						sSTACK_LAYER_GP	= commUtils.trim(rsResult.getRecord(0).getFieldString("STACK_LAYER_GP")); //적치 단 구분
						
					}
					
					/**********************************************************
					* 3. 스케줄 코드, 작업예약 ID 생성
					*    - 저장품 이동 조건 지정
					*    - 야드스케쥴 우선순위 검색
					**********************************************************/
					sPoint = szYD_CARLD_STOP_LOC.substring(5,6);
					
					if("1".equals(sPoint)||"2".equals(sPoint)) {
						sYD_SCH_CD 	= szYD_GP + szSTACK_BAY_GP + "PT02UM"; //이송상차(L)
					} else {
						sYD_SCH_CD 	= szYD_GP + szSTACK_BAY_GP + "PT06UM"; //이송상차(R)
					}
					sYD_SCH_REQ_GP = "";
					
					if("CG".equals(sGP)) {
						//제품
						sSTOCK_MOVE_TERM 	= YmConstant.NEW_STOCK_MOVE_TERM_GC; //종합판정대기 
					} else if("CM".equals(sGP)) {
						//소재
						sSTOCK_MOVE_TERM 	= YmConstant.NEW_STOCK_MOVE_TERM_A8; //지포장  추출 
					}
					sYD_WBOOK_ID = commDao.getSeqId(logId, methodNm, "WrkBook"); 
					
					//스케줄코드로 스케줄기준Table조회
					jrParam.setField("YD_SCH_CD", sYD_SCH_CD);
			    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule  
					SELECT YD_SCH_CD
					     , YD_WRK_CRN
					     , YD_WRK_CRN_PRIOR
					  FROM TB_YM_SCHEDULERULE
					 WHERE DEL_YN = 'N'
					   AND YD_SCH_CD = :V_YD_SCH_CD
					*/   
					rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
			    	
					if (rsResult != null && rsResult.size() > 0) {
						sYD_SCH_PRIOR = rsResult.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
					} else {
						throw new Exception("1열연 코일 스케쥴 코드 이상 : [" + sYD_SCH_CD + "]");
					}			
					
					/**********************************************************
					* 4. 작업예약(TB_YM_WRKBOOK) 생성
					**********************************************************/
					jrParam.setField("YD_WBOOK_ID"		, sYD_WBOOK_ID);
					jrParam.setField("YD_GP"			, szYD_GP);
					jrParam.setField("YD_BAY_GP"		, szSTACK_BAY_GP);
					jrParam.setField("YD_SCH_CD"		, sYD_SCH_CD); //야드스케쥴코드
					jrParam.setField("YD_SCH_PRIOR"		, sYD_SCH_PRIOR); //야드스케쥴우선순위
					jrParam.setField("YD_SCH_PROG_STAT"	, "W"); //야드스케쥴진행상태(W:스케줄수행대기)
					jrParam.setField("YD_SCH_ST_GP"		, "M"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업)
					jrParam.setField("YD_SCH_REQ_GP"	, sYD_SCH_REQ_GP); //야드스케쥴요청구분
					jrParam.setField("YD_CAR_USE_GP"	, "L"); //L:구내운송
					jrParam.setField("TRN_EQP_CD"		, szTRN_EQP_CD); //운송장비코드
					
					commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insWrkBook", logId, methodNm, "작업예약(TB_YM_WRKBOOK) 생성");
		
					/**********************************************************
					* 5. 작업예약재료(TB_YM_WRKBOOKMTL) 생성
					**********************************************************/
					jrParam.setField("YD_WBOOK_ID"		, sYD_WBOOK_ID);
					jrParam.setField("STOCK_ID"			, sSTOCK_ID);
					jrParam.setField("STACK_COL_GP"		, sSTACK_COL_GP);
					jrParam.setField("STACK_BED_GP"		, sSTACK_BED_GP);
					jrParam.setField("STACK_LAYER_GP"	, sSTACK_LAYER_GP);
					
					commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insYmWrkBookMtl", logId, methodNm, "작업예약재료(TB_YM_WRKBOOKMTL) 생성");
					
					/**********************************************************
					* 6. 크레인스케줄 전문 호출
					**********************************************************/
					jrParam.setField("YD_WBOOK_ID"  	, sYD_WBOOK_ID); //야드작업예약ID
					jrParam.setField("YD_SCH_CD"    	, sYD_SCH_CD  ); //야드스케쥴코드
					jrParam.setField("YD_SCH_ST_GP" 	, "M"      	); //야드스케쥴기동구분
					jrParam.setField("YD_SCH_REQ_GP"	, sYD_SCH_REQ_GP); //야드스케쥴요청구분
					
					jrRtn = commUtils.addSndData(jrRtn, ymComm.getCrnSchMsg(jrParam));
					
					
					/**********************************************************
					* 7. TB_YM_STOCK의 이송작업지시번호(TRANS_WORD_NO) 등록
					**********************************************************/
					jrParam.setField("STOCK_ID"			, sSTOCK_ID);
					jrParam.setField("STOCK_MOVE_TERM"	, YmConstant.NEW_STOCK_MOVE_TERM_CS ); //이송대기
					jrParam.setField("FRTOMOVE_WORD_NO"	, szFRTOMOVE_WORD_NO); //이송작업지시번호
					
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStockTransWordNo", logId, methodNm, "TB_YM_STOCK의 이송작업지시번호(TRANS_WORD_NO) 등록");
					
					//- 예정정보를 정확히 보내기 위해서 TB_YM_STOCK의 SHEAR_SUPPLY_SEQ 항목에 차상위치를 설정한다.
					jrParam.setField("SHEAR_SUPPLY_SEQ"		, "0"+(++iMTL_CNT));		//차량적재위치
					jrParam.setField("STOCK_ID"				, sSTOCK_ID);
					
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updStockShearSupplySeq", logId, methodNm, "TB_YM_STOCK의 SHEAR_SUPPLY_SEQ에 차상위치 셋팅");
					
					
				} else {
					throw new Exception("차량스케줄ID:" + szYD_CAR_SCH_ID + " 의 차량진행상태가 '" + szYD_CAR_PROG_STAT + "' 로 상차상태(2,3,4)가 아닙니다!!");
				}
				
			}
			
			/**********************************************************
			* 차량작업 예정정보 송신 (YMA7L008)
			**********************************************************/
			jrParam.setField("SEARCH_FLAG"   , "2");				//1:상차도, 2:차량스케쥴 ID	
			jrParam.setField("YD_CAR_SCH_ID" , szYD_CAR_SCH_ID); 	//야드차량스케쥴ID
			jrRtn = commUtils.addSndData(jrRtn, ymComm.procCarPlanInfo(jrParam));
			
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of reqCarldSchSt
	
	
	
	/**
	 *  하차작업등록 (반품,회송,부분하차)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */	
	public JDTORecord regCarUdWrk(GridData gdReq) throws DAOException {
		
		String methodNm = "하차작업등록[BCoilJspSeEJB.regCarUdWrk] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
	    String szMsg = "";
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+", gdReq);	
			
			int intRtnVal 	= 0;
			
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			JDTORecordSet 	rsResult	= null;
			JDTORecord		recParam	= null;
			JDTORecord		recTemp		= null;
			
			JDTORecord recEditColumn	= JDTORecordFactory.getInstance().create();
			JDTORecord recOutTemp	= JDTORecordFactory.getInstance().create();
			
			String szStlNo 			= null;
			String szMsgContents 	= null;
			String szCAR_KIND 		= "";
			String szYD_STK_BED_NO 	= null;
			String szWLOC_CD 		= null;
			String szYD_STK_COL_GP 	= null;
			String szYD_PNT_CD 		= null;
			String szTRANS_ORD_DATE = null;
			String szTRANS_ORD_SEQNO = null;
			String szIF_SEQ_NO		= null;
			String szYD_CAR_SCH_ID 	= null;
			String[] rVal = new String[1];
			String szCoilWt = "";
			//------------------------------------------------------------------
			//화면으로 부터 전달 받은 정보
			String szCAR_NO 		= gdReq.getParam("CAR_NO").toUpperCase();
			String szRETN_WK_GP 	= gdReq.getParam("RETN_WK_GP");  //1:반품, 2:회송, 3:부분하차 , 4:소재반품
			String szYD_CARPNT_CD 	= gdReq.getParam("YD_CARPNT_CD").toUpperCase();
			String szTEL_NO			= StringHelper.evl(gdReq.getParam("TEL_NO"),"00000000000");
			String szCARD_NO		= StringHelper.evl(gdReq.getParam("CARD_NO"),"").toUpperCase(); //차량번호의 뒤의 4자리를 사용한다.
			String szUser			= gdReq.getParam("userid");
			//------------------------------------------------------------------
			
	    	// PIDEV
//	    	String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "*", "*");			
			
			String sRETN_TRGT_YN = ""; //반품회송대상 여부 (Y : 대상)
			
			/******************************************************
			 * 화면으로 부터 전달 받은 정보로 중복 등록 불가 체크
			 ******************************************************/
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {
				
				szStlNo       = commUtils.trim(gdReq.getHeader("STOCK_ID"    ).getValue(ii));
				sRETN_TRGT_YN = commUtils.trim(gdReq.getHeader("RETN_TRGT_YN").getValue(ii));
			
				// 전달받은 재료번호와 차량 번호로 TB_YD_CARSCH 조회
				recTemp = JDTORecordFactory.getInstance().create();
				recTemp.setField("CAR_NO",         szCAR_NO);
				recTemp.setField("STL_NO",         szStlNo);
				/*
				SELECT A.YD_CAR_SCH_ID 
				  FROM TB_YD_CARSCH A
				     , TB_YD_CARFTMVMTL B
				 WHERE 1 = 1
				   AND A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID
				   AND A.YD_CAR_PROG_STAT NOT IN ('5','E')
				   AND A.CAR_NO  = :V_CAR_NO
				   AND B.STL_NO  = :V_STL_NO
				   AND A.DEL_YN   = 'N' 
				   AND B.DEL_YN   = 'N'
				*/	   
			    rsResult = commDao.select(recTemp, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYdCarYdCarMtlDEL_YN", logId, methodNm, "차량스케줄조회");
			    
			    if (rsResult.size() > 0) {
			    	szMsg="["+methodNm+"] 해당재료로 등록된 차량 스케줄이 있습니다. ";
					return jrRtn;
							
			    }
			}
			
			//CARD_NO가 NULL인경우 차량번호 뒤에 4자리로 셋팅
			if(szCARD_NO.equals("") && szCAR_NO.length() >= 4)
			{
				szCARD_NO = szCAR_NO.substring(szCAR_NO.length()-4, szCAR_NO.length());
			}
			
			/**
			 * 화면으로 부터 전달 받은 차량번호가 GT(TT카) 일때 초기화 호출
			 * TT카이고 부분하차 일경우 강제로 빼고 다시 넣어야 함...
			 */
			if (szCAR_NO.startsWith("GT")) {
				szCAR_KIND = "TT";
			} else {
				szCAR_KIND = "TR";
			}
			
			
			//------------------------------------------------------------------
			//YD_CARPNT_CD 로 WLOC_CD, YD_PNT_CD, 하차위치(YD_STK_COL_GP)를 조회한다.
			recParam = JDTORecordFactory.getInstance().create();
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			recParam.setField("YD_CARPNT_CD", szYD_CARPNT_CD);
			/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdCarPoint
			SELECT SUBSTR(YD_STK_COL_GP,1,4)||'0'||SUBSTR(YD_STK_COL_GP,6,1)  AS YD_STK_COL_GP2
			     , DECODE(A.YD_STK_COL_ACT_STAT,'R','C',YD_STK_COL_ACT_STAT)  AS YD_STK_COL_ACT_STAT
			     , YD_CARPNT_CD
			     , REG_DDTT
			     , REGISTER
			     , MOD_DDTT
			     , MODIFIER
			     , DEL_YN
			     , YD_CAR_USETYPE_GP
			     , YD_GP
			     , YD_BAY_GP
			     , YD_STK_COL_GP
			     , TRN_EQP_CD
			     , CAR_NO
			     , CARD_NO
			     , WLOC_CD
			     , YD_PNT_CD
			     , YD_CARPNT_DESC
			     , YD_SPAN_FROM
			     , YD_SPAN_TO
			  FROM TB_YD_CARPOINT A
			 WHERE YD_CARPNT_CD=:V_YD_CARPNT_CD
			*/ 
			rsResult = commDao.select(recParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdCarPoint", logId, methodNm, "차량포인트조회");
			
			if (rsResult.size() < 1) {
				szMsg="["+methodNm+"] 차량포인트 조회 실패!! - intRtnVal : " + intRtnVal;
				throw new Exception(szMsg);
			}
			
			rsResult.first();
			recTemp	= rsResult.getRecord();
			
			szWLOC_CD    		= StringHelper.evl(recTemp.getFieldString("WLOC_CD"), "");
			szYD_STK_COL_GP    	= StringHelper.evl(recTemp.getFieldString("YD_STK_COL_GP"), "");		
			szYD_PNT_CD	    	= StringHelper.evl(recTemp.getFieldString("YD_PNT_CD"), "");	
			
			//------------------------------------------------------------------
			//운송지시일자, 순번 생성  (999001 처럼 앞에  999를 붙인다.)
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			/*com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getRetnTransOrdNo
			SELECT  A.TRANS_ORD_DATE
			       ,A.TRANS_ORD_SEQNO + 1 AS TRANS_ORD_SEQNO
			FROM    (
			            SELECT  TO_CHAR(SYSDATE,'YYYYMMDD') AS TRANS_ORD_DATE
			                   ,NVL(MAX(TRANS_ORD_SEQNO),999000) AS TRANS_ORD_SEQNO
			            FROM   TB_YD_CARSCH
			            WHERE  TRANS_ORD_DATE = TO_CHAR(SYSDATE,'YYYYMMDD')
			            AND    TRANS_ORD_SEQNO >  999000
			        ) A
			*/        
			rsResult = commDao.select(recParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getRetnTransOrdNo", logId, methodNm, "운송지시 일자 생성");
			
			if (rsResult.size() < 1) {
				szMsg="["+methodNm+"] 운송지시일자,순번  생성시  오류발생 실패!! - intRtnVal : " + intRtnVal;
				throw new Exception(szMsg);
			}
			
			rsResult.first();
			recTemp	= rsResult.getRecord();
			
			szTRANS_ORD_DATE 	=  commUtils.trim(recTemp.getFieldString("TRANS_ORD_DATE"));
			szTRANS_ORD_SEQNO 	=  commUtils.trim(recTemp.getFieldString("TRANS_ORD_SEQNO"));
			

			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER" 		, szUser);
			jrParam.setField("STOCK_ITEM" 		, "CG");
			jrParam.setField("STOCK_MOVE_TERM" 	, "CS");
			jrParam.setField("TRANS_ORD_DT" 	, szTRANS_ORD_DATE);
			jrParam.setField("TRANS_ORD_SEQNO" 	, szTRANS_ORD_SEQNO);
			
	    	//PIDEV				
//			if("N".equals(sApplyYnPI)) {
//				jrParam.setField("CAR_CARD_NO" 		, szCARD_NO);
//			}
			
			jrParam.setField("CAR_NO2" 			, szCAR_NO);
//			jrParam.setField("CR_FRTOMOVE_GP" 	, transFrtoMoveGp);
			jrParam.setField("YD_CARPNT_CD"		, szYD_CARPNT_CD);
			jrParam.setField("TRANS_WORD_NO" 	, szTRANS_ORD_SEQNO+szTRANS_ORD_SEQNO);
			String StockId  = "";
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				StockId 		= commUtils.trim(gdReq.getHeader("STOCK_ID"     ).getValue(ii));
				szYD_STK_BED_NO = commUtils.trim(gdReq.getHeader("YD_STK_BED_NO").getValue(ii));
				szMsgContents 	= commUtils.trim(gdReq.getHeader("MSG_CONTENTS" ).getValue(ii));
				szCoilWt 		= commUtils.trim(gdReq.getHeader("COIL_WT"      ).getValue(ii));

				if (StockId.equals("")) {
					break;
				}
				jrParam.setField("STOCK_ID" 		, StockId);
				jrParam.setField("SHEAR_SUPPLY_SEQ" , szYD_STK_BED_NO); // 차상위치
				jrParam.setField("SNBK_WT"          , szCoilWt);        // 반송중량
				jrParam.setField("YD_ABMTL_REM"     , szMsgContents);   // 반품 메시지

				/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.insStockTransInfoSNBK_PIDEV
				MERGE INTO TB_YM_STOCK ST USING (
				    SELECT :V_STOCK_ID          AS STOCK_ID         --재료번호
				         , :V_MODIFIER          AS MODIFIER         --수정자
				         , SYSDATE              AS MOD_DDTT         --수정일시
				         , 'N'                  AS DEL_YN           --삭제유무
				         , :V_STOCK_ITEM        AS STOCK_ITEM       --저장품 품목
				         , :V_STOCK_MOVE_TERM   AS STOCK_MOVE_TERM  --저장품 이동 조건
				         , :V_SHEAR_SUPPLY_SEQ  AS SHEAR_SUPPLY_SEQ --차상위치
				         , :V_TRANS_ORD_DT      AS TRANS_ORD_DATE2  --운송지시
				         , :V_TRANS_ORD_SEQNO   AS TRANS_ORD_SEQNO2 --운송지시순번
				         , :V_CAR_CARD_NO       AS CAR_CARD_NO      --카드번호
				         , :V_CAR_NO2           AS CAR_NO2          --차량번호
				         , :V_TRANS_WORD_NO     AS TRANS_WORD_NO
				         , :V_SNBK_WT           AS SNBK_WT 
				         , :V_YD_ABMTL_REM      AS YD_ABMTL_REM
				      FROM DUAL
				) DD ON ( ST.STOCK_ID = DD.STOCK_ID)
				
				WHEN NOT MATCHED THEN
				    INSERT (
				           STOCK_ID             , STOCK_ITEM        , STOCK_MOVE_TERM 
				         , REGISTER             , REG_DDTT          , MODIFIER  
				         , MOD_DDTT             , DEL_YN            , TRANS_WORD_NO
				         , SHEAR_SUPPLY_SEQ     , TRANS_ORD_DATE2   , TRANS_ORD_SEQNO2
				         , CAR_CARD_NO          , CAR_NO2           , SNBK_WT  
				         , YD_ABMTL_REM
				         )
				    VALUES (
				           :V_STOCK_ID          , DD.STOCK_ITEM     , DD.STOCK_MOVE_TERM 
				         , DD.MODIFIER          , DD.MOD_DDTT       , DD.MODIFIER  
				         , DD.MOD_DDTT          , DD.DEL_YN         , DD.TRANS_WORD_NO 
				         , DD.SHEAR_SUPPLY_SEQ  , DD.TRANS_ORD_DATE2, DD.TRANS_ORD_SEQNO2
				         , DD.CAR_CARD_NO       , DD.CAR_NO2        , DD.SNBK_WT  
				         , DD.YD_ABMTL_REM
				         )
				WHEN MATCHED THEN 
				    UPDATE SET
				           STOCK_ITEM       = DD.STOCK_ITEM
				         , STOCK_MOVE_TERM  = DD.STOCK_MOVE_TERM 
				         , MODIFIER         = DD.MODIFIER 
				         , MOD_DDTT         = DD.MOD_DDTT          
				         , SHEAR_SUPPLY_SEQ = DD.SHEAR_SUPPLY_SEQ     
				         , TRANS_WORD_NO    = DD.TRANS_WORD_NO
				         , TRANS_ORD_DATE2  = DD.TRANS_ORD_DATE2     
				         , TRANS_ORD_SEQNO2 = DD.TRANS_ORD_SEQNO2
				         , CAR_CARD_NO      = DD.CAR_CARD_NO         
				         , CAR_NO2          = DD.CAR_NO2       
				         , SNBK_WT          = DD.SNBK_WT 
				         , DEL_YN           = DD.DEL_YN
				         , YD_ABMTL_REM     = DD.YD_ABMTL_REM
			*/	  
		    	// PIDEV				
//				if("Y".equals(sApplyYnPI)) {
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.insStockTransInfoSNBK_PIDEV", logId, methodNm, "TB_YM_STOCK 등록");
//				} else {
//					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.insStockTransInfoSNBK", logId, methodNm, "TB_YM_STOCK 등록");
//				}
			}
			
		    //--------------------------------------------------------------------------
			//2. 차량스케줄 생성
			String ydCarSchId = commDao.getSeqId(logId, methodNm, "CarSch");
    		JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_CAR_SCH_ID"		, ydCarSchId);
			recInTemp.setField("REGISTER"			, szUser);
			recInTemp.setField("YD_EQP_ID"			, YmConstant.YD_DM_CAR_EQP_ID);			//야드설비ID
			recInTemp.setField("YD_CAR_USE_GP"		, YmConstant.YD_CAR_USE_GP_DM);			//차량사용구분 
			recInTemp.setField("CAR_NO"				, szCAR_NO);							//차량번호
			recInTemp.setField("CAR_KIND"			, szCAR_KIND);							//차량종류
			recInTemp.setField("YD_EQP_WRK_STAT"	, "L");									//야드설비작업상태
			recInTemp.setField("SPOS_WLOC_CD"		, szWLOC_CD);							//발지개소코드
			recInTemp.setField("ARR_WLOC_CD"		, szWLOC_CD);							//착지개소코드
			recInTemp.setField("YD_CARUD_LEV_DT"	, commUtils.getDateTime14());			//하차출발일시
			recInTemp.setField("YD_PNT_CD3"			, szYD_PNT_CD);							//야드포인트코드3
			recInTemp.setField("YD_CARUD_STOP_LOC"	, szYD_STK_COL_GP);						//야드하차차정지위치
	    	// PIDEV				
//			if("N".equals(sApplyYnPI)) {
//				recInTemp.setField("CARD_NO"			, szCARD_NO);							//카드번호
//			}
			recInTemp.setField("YD_CAR_PROG_STAT"	, "A");									//하차출발상태
			recInTemp.setField("TRANS_ORD_DATE"		, szTRANS_ORD_DATE);					//운송지시일자
			recInTemp.setField("TRANS_ORD_SEQNO"	, szTRANS_ORD_SEQNO);					//운송지시순번 
			recInTemp.setField("YD_BAYIN_WO_SEQ"	, YmConstant.YD_BAYIN_WO_SEQ_DEFAULT);	//입동지시순번 - 기본값으로 설정(9)
			recInTemp.setField("YD_CAR_WRK_GP"		, szRETN_WK_GP);
			if (szCAR_KIND.equals("TT")) {
				recInTemp.setField("CAR_KIND",          "TT");								//차량종류
			} else {
				recInTemp.setField("CAR_KIND",          "TR");								//차량종류
			}				
    		//차량스케줄 등록
			
			/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.insYdCarsch_PIDEV
			INSERT INTO USRYDA.TB_YD_CARSCH
			(	   YD_CAR_SCH_ID
			     , REGISTER
			     , REG_DDTT	
			     , DEL_YN
			     , YD_EQP_ID
			     , YD_CAR_USE_GP
			     , CAR_NO
			     , TRN_EQP_CD
			     , CAR_KIND
			     , YD_EQP_WRK_STAT
			     , SPOS_WLOC_CD
			     , ARR_WLOC_CD
			     , YD_CARLD_LEV_LOC
			     , YD_CARLD_LEV_DT
			     , YD_CARUD_LEV_DT
			     , YD_PNT_CD1
			     , YD_PNT_CD3
			     , YD_CARLD_STOP_LOC
			     , YD_CARUD_STOP_LOC
			     , CARD_NO
			     , YD_CAR_PROG_STAT
			     , YD_CAR_WRK_GP
			     , TRANS_ORD_DATE
			     , TRANS_ORD_SEQNO
			     , YD_BAYIN_WO_SEQ
			     , TEL_NO
			     , CMBN_CARLD_YN
			     , WAIT_ARR_DDTT
			     , WAIT_ARR_GP
			     , TRANS_EQUIPMENT_TYPE
			       )
			VALUES (
			       :V_YD_CAR_SCH_ID
			     , :V_REGISTER
			     , SYSDATE
			     , 'N'
			     , :V_YD_EQP_ID
			     , :V_YD_CAR_USE_GP
			     , :V_CAR_NO
			     , :V_TRN_EQP_CD
			     , :V_CAR_KIND
			     , :V_YD_EQP_WRK_STAT
			     , :V_SPOS_WLOC_CD
			     , :V_ARR_WLOC_CD              --
			     , :V_YD_CARLD_LEV_LOC
			     , :V_YD_CARLD_LEV_DT
			     , :V_YD_CARUD_LEV_DT
			     , NVL(:V_YD_PNT_CD1,'0000')
			     , NVL(:V_YD_PNT_CD3,'0000')
			     , :V_YD_CARLD_STOP_LOC
			     , :V_YD_CARUD_STOP_LOC        --
			     , :V_CARD_NO
			     , :V_YD_CAR_PROG_STAT
			     , :V_YD_CAR_WRK_GP
			     , :V_TRANS_ORD_DATE
			     , :V_TRANS_ORD_SEQNO
			     , :V_YD_BAYIN_WO_SEQ
			     , :V_TEL_NO
			     , :V_CMBN_CARLD_YN
			     , :V_WAIT_ARR_DDTT
			     , :V_WAIT_ARR_GP     
			     , :V_TRANS_EQUIPMENT_TYPE
			       )
			 */

	    	// PIDEV				
//			if("Y".equals(sApplyYnPI)) {
				commDao.insert(recInTemp, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.insYdCarsch_PIDEV", logId, methodNm, "TB_YD_CARSCH 등록");
//			} else {
//				commDao.insert(recInTemp, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.insYdCarsch", logId, methodNm, "TB_YD_CARSCH 등록");
//			}
		    //--------------------------------------------------------------------------
			JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create(); 
			//3.차량스케줄재료 생성
			for (int ii = 0; ii < rowCnt; ii++) {
				szStlNo       = commUtils.trim(gdReq.getHeader("STOCK_ID"    ).getValue(ii));
				sRETN_TRGT_YN = commUtils.trim(gdReq.getHeader("RETN_TRGT_YN").getValue(ii));
				
				if ("".equals(szStlNo)) {
					break;
				}
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("YD_CAR_SCH_ID"	, ydCarSchId);
				recInTemp.setField("MODIFIER"		, szUser    );
				recInTemp.setField("STL_NO"			, szStlNo   );
				recInTemp.setField("YD_STK_BED_NO"	, commUtils.trim(gdReq.getHeader("YD_STK_BED_NO").getValue(ii)));
				recInTemp.setField("YD_STK_LYR_NO"	, "001");
				recInTemp.setField("DEL_YN"	        , "Y".equals(sRETN_TRGT_YN) ? "N" : "Y");
				/* 
				INSERT INTO TB_YD_CARFTMVMTL(
				       YD_CAR_SCH_ID
				     , STL_NO
				     , REGISTER
				     , REG_DDTT
				     , MODIFIER
				     , MOD_DDTT
				     , DEL_YN
				     , YD_STK_BED_NO
				     , YD_STK_LYR_NO
				     ) 
				VALUES ( 
				       :V_YD_CAR_SCH_ID
				     , :V_STL_NO
				     , :V_MODIFIER
				     , SYSDATE
				     , :V_MODIFIER
				     , SYSDATE
				     , :V_DEL_YN
				     , :V_YD_STK_BED_NO
				     , :V_YD_STK_LYR_NO
				)
				*/
				commDao.insert(recInTemp, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insCarFtMvMtl", logId, methodNm, "차량재료 스케쥴 INSERT ");	
				
				if ("Y".equals(sRETN_TRGT_YN)){
					sndL2Msg = JDTORecordFactory.getInstance().create();
					sndL2Msg.setField("TC_CD"          , "YMA7L002");
					sndL2Msg.setField("MSG_GP"         , "I");
					sndL2Msg.setField("YD_INFO_SYNC_CD", "R");
					sndL2Msg.setField("STOCK_ID"       , szStlNo);
					
					jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA7L002", sndL2Msg));	 //전송 Data 생성	
				}
			}
			
			//입동지시요구모듈 호출(trailer인 경우)
			if (szCAR_KIND.equals("T") || szCAR_KIND.equals("TR")) {
				/*
				 * 6. 차량정지위치에대한 입동대기차량들중에서 가장빠른 입동순서를 가진 차량을 입동지시하는 차량입동지시요구 모듈을 호출한다.
				 */
				recInTemp = JDTORecordFactory.getInstance().create();			 
				recInTemp.setResultCode(logId);	//Log ID
				recInTemp.setResultMsg(methodNm);	//Log Method Name
				recInTemp.setField("JMS_TC_CD"			, "YMYMJ662");          //차량입동지시 요구 기존:YDYDJ662
				recInTemp.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시
				recInTemp.setField("YD_CARPNT_CD"		, szYD_CARPNT_CD);
				recInTemp.setField("YD_CAR_SCH_ID"		, ydCarSchId);
				recInTemp.setField("CHK_YN"				, "N");
				
				jrRtn = commUtils.addSndData(jrRtn, recInTemp);	
				
				commUtils.printLog(logId, methodNm + "차량정지위치[" + szYD_STK_COL_GP + "], 차량스케줄ID[" + ydCarSchId + "] -AB 차량입동지시요구 모듈을 호출", "SL");
			}			
			

			commUtils.printLog(logId, methodNm, "S-", gdReq);			

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} //regCarUdWrk
	
	
	
	/**
	 *  하차작업등록 (반품,회송,부분하차)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */	
	public JDTORecord regCarUdWrkBackUp(GridData gdReq) throws DAOException {
		
		String methodNm = "하차작업등록[BCoilJspSeEJB.regCarUdWrk] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

	    String szMsg           		= "";
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+", gdReq);	
			
			int intRtnVal 	= 0;
			
			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			
			JDTORecordSet 	rsResult	= null;
			JDTORecord		recParam	= null;
			JDTORecord		recTemp		= null;
			
			JDTORecord outRecord = JDTORecordFactory.getInstance().create();
			JDTORecord recEditColumn	= JDTORecordFactory.getInstance().create();
			JDTORecord recOutTemp	= JDTORecordFactory.getInstance().create();
			JDTORecord inRecord = null;
			
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
				
				szStlNo = commUtils.trim(gdReq.getHeader("STL_NO").getValue(ii));
			
				// 전달받은 재료번호와 차량 번호로 TB_YD_CARSCH 조회
				recTemp = JDTORecordFactory.getInstance().create();
				recTemp.setField("CAR_NO",           szCAR_NO);
				recTemp.setField("STL_NO",           szStlNo);
			    rsResult = commDao.select(recTemp, "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarYdCarMtlDEL_YN", logId, methodNm, "차량스케줄조회");
			    
			    if(rsResult.size()>0){
			    	szMsg="["+methodNm+"] 해당재료로 등록된 차량 스케줄이 있습니다. ";
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
			    rsResult = commDao.select(recTemp, "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschDaoByYdCarNoOrTrnEqpCd", logId, methodNm, "차량스케줄조회"); 
			    
			    rsResult.first();
		        recOutTemp = JDTORecordFactory.getInstance().create();
		        recOutTemp = rsResult.getRecord();
		        szYD_CAR_SCH_ID = recOutTemp.getFieldString("YD_CAR_SCH_ID");
			    
				inCarRecord[0] = JDTORecordFactory.getInstance().create();
				inCarRecord[0].setField("YD_USER_ID", 					"YdSystem");
				inCarRecord[0].setField("YD_CAR_SCH_ID", 				szYD_CAR_SCH_ID);
				inCarRecord[0].setField("TRN_EQP_CD", 					szCAR_NO);
				
			}
			
			
			//------------------------------------------------------------------
			//YD_CARPNT_CD 로 WLOC_CD, YD_PNT_CD, 하차위치(YD_STK_COL_GP)를 조회한다.
			recParam = JDTORecordFactory.getInstance().create();
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			recParam.setField("YD_CARPNT_CD", szYD_CARPNT_CD);
			
			rsResult = commDao.select(recParam, "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarPoint");
			
			if(rsResult.size()<1) {
				szMsg="["+methodNm+"] 차량포인트 조회 실패!! - intRtnVal : " + intRtnVal;
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
			
			rsResult = commDao.select(recParam, "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getRetnTransOrdNo");
			
			if(rsResult.size()<1) {
				szMsg="["+methodNm+"] 운송지시일자,순번  생성시  오류발생 실패!! - intRtnVal : " + intRtnVal;
				throw new Exception(szMsg);
			}
			
			rsResult.first();
			recTemp		= rsResult.getRecord();
			
			//------------------------------------------------------------------------
			//차량스케줄 생성 전에 입동대기중인 차량들에서 IF_SEQ_NO MAX값을 읽어온다. 
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recParam.setField("WLOC_CD"	, szWLOC_CD);
			recParam.setField("YD_GP"		, szYD_STK_COL_GP.substring(0,1));
			recParam.setField("YD_BAY_GP"	, szYD_STK_COL_GP.substring(1,2));
			
			rsResult = commDao.select(recParam, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0082");
			
			if(rsResult.size()<1) {
				szMsg="["+methodNm+"] IF_SEQ_NO 조회시  오류발생 - rsResult : " + rsResult.size();
				szUNIQUE_ID = "0";
			} else {
				rsResult.first();
				recTemp		= rsResult.getRecord();
				
				//szUNIQUE_ID		= ydDaoUtils.paraRecChkNull(recTemp,"IF_SEQ_NO");//TODO
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
				
				szStlNo = commUtils.trim(gdReq.getHeader("STL_NO").getValue(ii));
				szYD_STK_BED_NO = commUtils.trim(gdReq.getHeader("YD_STK_BED_NO").getValue(ii));
				szMsgContents = commUtils.trim(gdReq.getHeader("MSG_CONTENTS").getValue(ii));
				szCoilWt = commUtils.trim(gdReq.getHeader("COIL_WT").getValue(ii));
				
	    		//C열연 코일 저장품 등록 
	    		//CoilSpecRegSeEJBBean.stockProcCom(szStlNo,1);//TODO
	    		
	    		recEditColumn.setField("STL_NO", 				szStlNo);
	    		recEditColumn.setField("YD_CAR_UPP_LOC_CD", 	szYD_STK_BED_NO); //야드 차상위치코드
	    		
				//야드목표행선지구분(제품구분-S:SLAB, C:COIL ,P:후판)
	    		recParam.setField("STL_NO", szStlNo);
				//rVal= YdCommonUtils.getYdAimRtGp("C",recParam );//TODO		
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
				
	    		//intRtnVal = ydStockDao.updYdStockReg(recEditColumn);//TODO
	    		
			}
			
		    //--------------------------------------------------------------------------
			//2. 차량스케줄 생성
			//szYD_CAR_SCH_ID = ydCarSchDao.getYdCarschId();//TODO
			recParam = JDTORecordFactory.getInstance().create();
			recParam.setField("YD_CAR_SCH_ID",    		szYD_CAR_SCH_ID);
			recParam.setField("REGISTER",         		szUser);
			recParam.setField("YD_EQP_WRK_STAT",  		"L");									//야드설비작업상태(영차:하차해야함)
			//recParam.setField("YD_EQP_ID",        		YdConstant.YD_DM_CAR_EQP_ID);			//야드설비ID //TODO
			//recParam.setField("YD_CAR_USE_GP",    		YdConstant.YD_CAR_USE_GP_DM);			//차량사용구분 //TODO
			recParam.setField("CAR_KIND", 				szCAR_KIND);
			recParam.setField("ARR_WLOC_CD",     		szWLOC_CD);								//착지개소코드
			recParam.setField("SPOS_WLOC_CD",     		szWLOC_CD);								//발지개소코드
			recParam.setField("TRANS_EQUIPMENT_TYPE",   "P");									//운송장비Type
			recParam.setField("YD_PNT_CD3",     		szYD_PNT_CD);							//야드포인트코드3
			recParam.setField("CAR_NO",           		szCAR_NO);								//차량번호
			recParam.setField("CARD_NO",          		szCARD_NO);								//카드번호
			//recParam.setField("YD_CARUD_LEV_DT",  		YdUtils.getCurDate("yyyyMMddHHmmss"));	//하차출발일시 //TODO
			recParam.setField("TRANS_ORD_DATE",   		szTRANS_ORD_DATE);						//운송지시일자
			recParam.setField("TRANS_ORD_SEQNO",  		szTRANS_ORD_SEQNO);						//운송지시순번
			recParam.setField("YD_CARUD_STOP_LOC",		szYD_STK_COL_GP);						//차량하차정지위치
			//recParam.setField("YD_BAYIN_WO_SEQ",  		YdConstant.YD_BAYIN_WO_SEQ_DEFAULT);	//입동지시순번 --기본값으로 설정(9) ** //TODO
			//recParam.setField("YD_CAR_PROG_STAT", 		YdConstant.YD_CARUD_LEV);				//하차출발상태(A) //TODO
			recParam.setField("IF_SEQ_NO", 				szUNIQUE_ID);							// 운송지시 SEQ
			recParam.setField("TEL_NO", 				szTEL_NO);								// 전화번호
			recParam.setField("YD_CAR_WRK_GP", 			szRETN_WK_GP);							// 야드차량작업구분  
			
    		//차량스케줄 등록
	    	//intRtnVal = ydCarSchDao.insYdCarsch(recParam); //TODO
    		if( intRtnVal <= 0 ){
				szMsg="[" + methodNm + "] 차량스케줄 생성 시 오류발생[반환값 : " + intRtnVal + "]";
				throw new Exception(szMsg);
    		}
    		
			
		    //--------------------------------------------------------------------------
			//3.차량스케줄재료 생성
			for (int ii = 0; ii < rowCnt; ii++) {
				
				szStlNo = commUtils.trim(gdReq.getHeader("STL_NO").getValue(ii));
				szYD_STK_BED_NO = commUtils.trim(gdReq.getHeader("YD_STK_BED_NO").getValue(ii));
				
				recParam.setField("YD_CAR_SCH_ID",    	szYD_CAR_SCH_ID);
				recParam.setField("REGISTER",         	szUser);
				recParam.setField("STL_NO", 			szStlNo);	 
				recParam.setField("YD_STK_BED_NO", 		szYD_STK_BED_NO); //야드 차상위치코드
				recParam.setField("YD_STK_LYR_NO", 		"001");
				recParam.setField("DEL_YN", 			"N");
		 
				//intRtnVal = ydCarftmvmtlDao.insYdCarftmvmtl(recParam); //TODO
	    		if(intRtnVal != 1) {
	    			szMsg="[" + methodNm + "] 차량스케줄재료 생성 시 오류발생[반환값 : " + intRtnVal + "]";
					throw new Exception(szMsg);
	    		}
			}
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				szStlNo = commUtils.trim(gdReq.getHeader("STL_NO").getValue(ii));
				//L2저장품재원 정보 송신
				//======================================================
				// 저장품제원 : 코일야드L2로 송신(YMA7L002)
				//======================================================
				JDTORecord recResult = null;
				recResult = JDTORecordFactory.getInstance().create();
				recResult.setField("MSG_ID"         , "YMA7L002");
				recResult.setField("YD_INFO_SYNC_CD", "5");    // 5:지정저장품
				recResult.setField("STL_NO"         , szStlNo);
				recResult.setField("YD_STK_COL_GP"  , "");
				recResult.setField("YD_STK_BED_NO"  , "");
				
				//ydDelegate.sendMsg(recResult); //TODO
	
				szMsg = "코일야드L2로 응답전문 [YMA7L002] 전송완료";
			}
		
		    //--------------------------------------------------------------------------
			//4. 입동지시 요구
			szMsg="[" + methodNm + "] 차량입동포인트[" + szYD_STK_COL_GP + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 호출 시작";
			
			recParam = JDTORecordFactory.getInstance().create();
			recParam.setField("JMS_TC_CD",  		"YDDMR025"); //YDYDJ662
			
			szMsg="[" + methodNm + "] 차량입동포인트[" + szYD_STK_COL_GP + "], 차량스케줄ID[" + szYD_CAR_SCH_ID + "] - 차량입동지시요구 모듈을 호출 성공";
			
			
			commUtils.printLog(logId, methodNm, "S-", gdReq);			
			
			jrRtn.setField("RTN_CD" , "0");	
			jrRtn.setField("RTN_MSG", "정상적으로 등록하였습니다.");	
			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} //regCarUdWrk	
	
	
	/**
	 * 대차스케줄관리 - 대차초기화
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord initTcarSchMgt(JDTORecord rcvMsg) throws DAOException {
		
		String methodNm = "대차스케줄관리 대차초기화[BCoilJspSeEJB.initTcarSchMgt] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn = null;

			String ydEqpId     = ""; //야드설비ID(대차)
			String ydCurrBayGp = ""; //야드현재동구분(신규)
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(rcvMsg.getFieldString("userid")));

			jrParam.setField("YD_EQP_WRK_STAT"    , "U"); //야드설비작업상태(공차)
			jrParam.setField("YD_CARLD_SCH_REQ_GP", "6"); //야드상차스케쥴요청구분(공대차도착)
			jrParam.setField("YD_CARUD_SCH_REQ_GP", "3"); //야드하차스케쥴요청구분(영대차도착)
			jrParam.setField("YD_CAR_PROG_STAT"   , "0"); //야드차량진행상태(상차대기)

			//대차정보
			ydEqpId     = commUtils.trim(rcvMsg.getFieldString("EQUIP_GP"));
			ydCurrBayGp = commUtils.trim(rcvMsg.getFieldString("YD_CURR_BAY_GP"));

			if ("".equals(ydEqpId)) {
				throw new Exception("설비ID가 없습니다.");
			} else if ("".equals(ydCurrBayGp)) {
				throw new Exception("변경할 현재동이 없습니다.");
			}
			
			/**********************************************************
			* 1. 작업예약 삭제
			**********************************************************/
			String sAPP051 = ymComm.BCoilApplyYn("APP051","3","1");
			if ("Y".equals(sAPP051)) {
				/*
				SELECT WM.STOCK_ID
				     , WB.*
				  FROM TB_YM_WRKBOOK     WB
				     , TB_YM_WRKBOOKMTL  WM
				     , TB_YM_TCARFTMVMTL TM
				 WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
				   AND WB.DEL_YN = 'N'
				   AND WM.DEL_YN = 'N'
				   AND WM.STOCK_ID = TM.STOCK_ID
				   AND TM.YD_TCAR_SCH_ID IN (SELECT YD_TCAR_SCH_ID
				                               FROM TB_YM_TCARSCH
				                              WHERE YD_EQP_ID = :V_YD_EQP_ID
				                                AND DEL_YN    = 'N')
				   AND NOT EXISTS (SELECT 1
				                     FROM TB_YM_CRNSCH     CS
				                        , TB_YM_CRNWRKMTL  CM
				                    WHERE CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
				                      AND CS.DEL_YN = 'N'
				                      AND CS.DEL_YN = 'N'
				                      AND CM.STOCK_ID = TM.STOCK_ID
				                   )    
				 */
				jrParam.setField("YD_EQP_ID", ydEqpId);
				JDTORecordSet jsList = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getInitTrgtWrkList", logId, methodNm, "삭제대상 작업예약 조회");
				for (int ii = 0; ii < jsList.size(); ++ii) {
					jrParam.setField("YD_EQP_ID", jsList.getRecord(ii).getFieldString("YD_WBOOK_ID"));	
					/*
					UPDATE TB_YM_WRKBOOKMTL
					   SET MODIFIER    = :V_MODIFIER
					      ,MOD_DDTT    = SYSDATE
					      ,DEL_YN      = 'Y'
					 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
					   AND DEL_YN      = 'N'
					 */
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updDelYnWrkBookMtl", logId, methodNm, "TB_YM_WRKBOOKMTL 삭제");				
	
					/*
					UPDATE TB_YM_WRKBOOK
					   SET MODIFIER    = :V_MODIFIER
					      ,MOD_DDTT    = SYSDATE
					      ,DEL_YN      = 'Y'
					 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
					   AND DEL_YN      = 'N'
					 */
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updDelYnWrkBook", logId, methodNm, "TB_YM_WRKBOOK 삭제");
				}
			}
			
			/**********************************************************
			* 2. 기존 대차스케줄/재료 삭제
			**********************************************************/
			jrParam.setField("YD_EQP_ID", ydEqpId);
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarSchInitMtl 
			UPDATE TB_YM_TCARFTMVMTL
			   SET MODIFIER = :V_MODIFIER
			      ,MOD_DDTT = SYSDATE
			      ,DEL_YN   = 'Y'
			 WHERE DEL_YN   = 'N'
			   AND YD_TCAR_SCH_ID IN (SELECT YD_TCAR_SCH_ID
			                            FROM TB_YM_TCARSCH
			                           WHERE YD_EQP_ID = :V_YD_EQP_ID
			                             AND DEL_YN    = 'N')
			*/
			//대차이송재료 초기화
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarSchInitMtl", logId, methodNm, "대차이송재료 초기화");

			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarSchInitSch 
			UPDATE TB_YM_TCARSCH
			   SET MODIFIER  = :V_MODIFIER
			      ,MOD_DDTT  = SYSDATE
			      ,DEL_YN    = 'Y'
			 WHERE YD_EQP_ID = :V_YD_EQP_ID
			   AND DEL_YN    = 'N'
			*/	   
			//대차스케줄 초기화
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarSchInitSch", logId, methodNm, "대차스케줄 초기화");
			
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
			
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarSchInsSch 
			MERGE INTO TB_YM_TCARSCH TS USING (
			SELECT :V_YD_TCAR_SCH_ID       AS YD_TCAR_SCH_ID
			      ,:V_MODIFIER             AS MODIFIER
			      ,SYSDATE                 AS MOD_DDTT
			      ,'N'                     AS DEL_YN
			      ,:V_YD_EQP_ID            AS YD_EQP_ID
			      ,'U'                     AS YD_EQP_WRK_STAT     --공차
			      ,:V_YD_CAR_PROG_STAT     AS YD_CAR_PROG_STAT
			      ,:V_YD_CARLD_WRK_BOOK_ID AS YD_CARLD_WRK_BOOK_ID
			      ,:V_YD_CARLD_LEV_LOC     AS YD_CARLD_LEV_LOC
			      ,:V_YD_CARLD_STOP_LOC    AS YD_CARLD_STOP_LOC
			      ,:V_YD_CARUD_STOP_LOC    AS YD_CARUD_STOP_LOC
			      ,'6'                     AS YD_CARLD_SCH_REQ_GP --공대차도착
			      ,'3'                     AS YD_CARUD_SCH_REQ_GP --영대차도착
			  FROM DUAL
			) DD ON (TS.YD_TCAR_SCH_ID = DD.YD_TCAR_SCH_ID)
			WHEN MATCHED THEN UPDATE SET
				 TS.MODIFIER             = DD.MODIFIER
			    ,TS.MOD_DDTT             = DD.MOD_DDTT
			    ,TS.YD_EQP_WRK_STAT      = DD.YD_EQP_WRK_STAT
			    ,TS.YD_CAR_PROG_STAT     = DD.YD_CAR_PROG_STAT
			    ,TS.YD_CARLD_WRK_BOOK_ID = DD.YD_CARLD_WRK_BOOK_ID
			    ,TS.YD_CARLD_LEV_LOC     = DD.YD_CARLD_LEV_LOC
			    ,TS.YD_CARLD_STOP_LOC    = DD.YD_CARLD_STOP_LOC
			    ,TS.YD_CARUD_STOP_LOC    = DD.YD_CARUD_STOP_LOC
			    ,TS.YD_CARLD_SCH_REQ_GP  = DD.YD_CARLD_SCH_REQ_GP
			    ,TS.YD_CARUD_SCH_REQ_GP  = DD.YD_CARUD_SCH_REQ_GP
			WHEN NOT MATCHED THEN
			INSERT (TS.YD_TCAR_SCH_ID   , TS.REGISTER            , TS.REG_DDTT           , TS.MODIFIER         ,
			        TS.MOD_DDTT         , TS.DEL_YN              , TS.YD_EQP_ID          , TS.YD_EQP_WRK_STAT  ,
			        TS.YD_CAR_PROG_STAT , TS.YD_CARLD_WRK_BOOK_ID, TS.YD_CARLD_LEV_LOC   , TS.YD_CARLD_STOP_LOC,
			        TS.YD_CARUD_STOP_LOC, TS.YD_CARLD_SCH_REQ_GP , TS.YD_CARUD_SCH_REQ_GP)
			VALUES (DD.YD_TCAR_SCH_ID   , DD.MODIFIER            , DD.MOD_DDTT           , DD.MODIFIER         ,
			        DD.MOD_DDTT         , DD.DEL_YN              , DD.YD_EQP_ID          , DD.YD_EQP_WRK_STAT  ,
			        DD.YD_CAR_PROG_STAT , DD.YD_CARLD_WRK_BOOK_ID, DD.YD_CARLD_LEV_LOC   , DD.YD_CARLD_STOP_LOC,
			        DD.YD_CARUD_STOP_LOC, DD.YD_CARLD_SCH_REQ_GP , DD.YD_CARUD_SCH_REQ_GP)
			 */	        
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarSchInsSch", logId, methodNm, "대차스케줄 등록");
			
			/**********************************************************
			* 4. 대차 현재동 변경
			**********************************************************/
			jrParam.setField("EQUIP_GP"      , ydEqpId    );
			jrParam.setField("YD_CURR_BAY_GP", ydCurrBayGp);

			jrRtn = commUtils.addSndData(jrRtn, this.updTcarCurrBay(jrParam));

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of initTcarSchMgt
	
	
	/**
	 * 대차스케줄관리 - 대차초기화
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord initTcarSchMgt(GridData gdReq) throws DAOException {
		String methodNm = "대차스케줄관리 대차초기화[BCoilJspSeEJB.initTcarSchMgt] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			String ydEqpId     = ""; //야드설비ID(대차)
			String ydCurrBayGp = ""; //야드현재동구분(신규)
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			jrParam.setField("YD_EQP_WRK_STAT"    , "U"); //야드설비작업상태(공차)
			jrParam.setField("YD_CARLD_SCH_REQ_GP", "6"); //야드상차스케쥴요청구분(공대차도착)
			jrParam.setField("YD_CARUD_SCH_REQ_GP", "3"); //야드하차스케쥴요청구분(영대차도착)
			jrParam.setField("YD_CAR_PROG_STAT"   , "0"); //야드차량진행상태(상차대기)

			//대차정보
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				ydEqpId     = commUtils.trim(gdReq.getHeader("EQUIP_GP").getValue(ii));
				ydCurrBayGp = commUtils.trim(gdReq.getHeader("YD_CURR_BAY_GP").getValue(ii));

				if ("".equals(ydEqpId)) {
					throw new Exception("설비ID가 없습니다.");
				} else if ("".equals(ydCurrBayGp)) {
					throw new Exception("변경할 현재동이 없습니다.");
				}
				
				/**********************************************************
				* 1. 작업예약 삭제
				**********************************************************/
				String sAPP051 = ymComm.BCoilApplyYn("APP051","3","1");
				if ("Y".equals(sAPP051)) {
					/*
					SELECT WM.STOCK_ID
					     , WB.*
					  FROM TB_YM_WRKBOOK     WB
					     , TB_YM_WRKBOOKMTL  WM
					     , TB_YM_TCARFTMVMTL TM
					 WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
					   AND WB.DEL_YN = 'N'
					   AND WM.DEL_YN = 'N'
					   AND WM.STOCK_ID = TM.STOCK_ID
					   AND TM.YD_TCAR_SCH_ID IN (SELECT YD_TCAR_SCH_ID
					                               FROM TB_YM_TCARSCH
					                              WHERE YD_EQP_ID = :V_YD_EQP_ID
					                                AND DEL_YN    = 'N')
					   AND NOT EXISTS (SELECT 1
					                     FROM TB_YM_CRNSCH     CS
					                        , TB_YM_CRNWRKMTL  CM
					                    WHERE CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
					                      AND CS.DEL_YN = 'N'
					                      AND CS.DEL_YN = 'N'
					                      AND CM.STOCK_ID = TM.STOCK_ID
					                   )    
					 */
					jrParam.setField("YD_EQP_ID", ydEqpId);
					JDTORecordSet jsList = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getInitTrgtWrkList", logId, methodNm, "삭제대상 작업예약 조회");
					for (int i = 0; i < jsList.size(); ++i) {
						jrParam.setField("YD_EQP_ID", jsList.getRecord(i).getFieldString("YD_WBOOK_ID"));	
						/*
						UPDATE TB_YM_WRKBOOKMTL
						   SET MODIFIER    = :V_MODIFIER
						      ,MOD_DDTT    = SYSDATE
						      ,DEL_YN      = 'Y'
						 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
						   AND DEL_YN      = 'N'
						 */
						commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updDelYnWrkBookMtl", logId, methodNm, "TB_YM_WRKBOOKMTL 삭제");				
		
						/*
						UPDATE TB_YM_WRKBOOK
						   SET MODIFIER    = :V_MODIFIER
						      ,MOD_DDTT    = SYSDATE
						      ,DEL_YN      = 'Y'
						 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
						   AND DEL_YN      = 'N'
						 */
						commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updDelYnWrkBook", logId, methodNm, "TB_YM_WRKBOOK 삭제");
					}
				}
				
				/**********************************************************
				* 2. 기존 대차스케줄/재료 삭제
				**********************************************************/
				jrParam.setField("YD_EQP_ID", ydEqpId);
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarSchInitMtl 
				UPDATE TB_YM_TCARFTMVMTL
				   SET MODIFIER = :V_MODIFIER
				      ,MOD_DDTT = SYSDATE
				      ,DEL_YN   = 'Y'
				 WHERE DEL_YN   = 'N'
				   AND YD_TCAR_SCH_ID IN (SELECT YD_TCAR_SCH_ID
				                            FROM TB_YM_TCARSCH
				                           WHERE YD_EQP_ID = :V_YD_EQP_ID
				                             AND DEL_YN    = 'N')
				*/
				//대차이송재료 초기화
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarSchInitMtl", logId, methodNm, "대차이송재료 초기화");

				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarSchInitSch 
				UPDATE TB_YM_TCARSCH
				   SET MODIFIER  = :V_MODIFIER
				      ,MOD_DDTT  = SYSDATE
				      ,DEL_YN    = 'Y'
				 WHERE YD_EQP_ID = :V_YD_EQP_ID
				   AND DEL_YN    = 'N'
				*/	   
				//대차스케줄 초기화
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarSchInitSch", logId, methodNm, "대차스케줄 초기화");
				
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
				
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarSchInsSch 
				MERGE INTO TB_YM_TCARSCH TS USING (
				SELECT :V_YD_TCAR_SCH_ID       AS YD_TCAR_SCH_ID
				      ,:V_MODIFIER             AS MODIFIER
				      ,SYSDATE                 AS MOD_DDTT
				      ,'N'                     AS DEL_YN
				      ,:V_YD_EQP_ID            AS YD_EQP_ID
				      ,'U'                     AS YD_EQP_WRK_STAT     --공차
				      ,:V_YD_CAR_PROG_STAT     AS YD_CAR_PROG_STAT
				      ,:V_YD_CARLD_WRK_BOOK_ID AS YD_CARLD_WRK_BOOK_ID
				      ,:V_YD_CARLD_LEV_LOC     AS YD_CARLD_LEV_LOC
				      ,:V_YD_CARLD_STOP_LOC    AS YD_CARLD_STOP_LOC
				      ,:V_YD_CARUD_STOP_LOC    AS YD_CARUD_STOP_LOC
				      ,'6'                     AS YD_CARLD_SCH_REQ_GP --공대차도착
				      ,'3'                     AS YD_CARUD_SCH_REQ_GP --영대차도착
				  FROM DUAL
				) DD ON (TS.YD_TCAR_SCH_ID = DD.YD_TCAR_SCH_ID)
				WHEN MATCHED THEN UPDATE SET
					 TS.MODIFIER             = DD.MODIFIER
				    ,TS.MOD_DDTT             = DD.MOD_DDTT
				    ,TS.YD_EQP_WRK_STAT      = DD.YD_EQP_WRK_STAT
				    ,TS.YD_CAR_PROG_STAT     = DD.YD_CAR_PROG_STAT
				    ,TS.YD_CARLD_WRK_BOOK_ID = DD.YD_CARLD_WRK_BOOK_ID
				    ,TS.YD_CARLD_LEV_LOC     = DD.YD_CARLD_LEV_LOC
				    ,TS.YD_CARLD_STOP_LOC    = DD.YD_CARLD_STOP_LOC
				    ,TS.YD_CARUD_STOP_LOC    = DD.YD_CARUD_STOP_LOC
				    ,TS.YD_CARLD_SCH_REQ_GP  = DD.YD_CARLD_SCH_REQ_GP
				    ,TS.YD_CARUD_SCH_REQ_GP  = DD.YD_CARUD_SCH_REQ_GP
				WHEN NOT MATCHED THEN
				INSERT (TS.YD_TCAR_SCH_ID   , TS.REGISTER            , TS.REG_DDTT           , TS.MODIFIER         ,
				        TS.MOD_DDTT         , TS.DEL_YN              , TS.YD_EQP_ID          , TS.YD_EQP_WRK_STAT  ,
				        TS.YD_CAR_PROG_STAT , TS.YD_CARLD_WRK_BOOK_ID, TS.YD_CARLD_LEV_LOC   , TS.YD_CARLD_STOP_LOC,
				        TS.YD_CARUD_STOP_LOC, TS.YD_CARLD_SCH_REQ_GP , TS.YD_CARUD_SCH_REQ_GP)
				VALUES (DD.YD_TCAR_SCH_ID   , DD.MODIFIER            , DD.MOD_DDTT           , DD.MODIFIER         ,
				        DD.MOD_DDTT         , DD.DEL_YN              , DD.YD_EQP_ID          , DD.YD_EQP_WRK_STAT  ,
				        DD.YD_CAR_PROG_STAT , DD.YD_CARLD_WRK_BOOK_ID, DD.YD_CARLD_LEV_LOC   , DD.YD_CARLD_STOP_LOC,
				        DD.YD_CARUD_STOP_LOC, DD.YD_CARLD_SCH_REQ_GP , DD.YD_CARUD_SCH_REQ_GP)
				 */	        
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarSchInsSch", logId, methodNm, "대차스케줄 등록");
				
				/**********************************************************
				* 4. 대차 현재동 변경
				**********************************************************/
				jrParam.setField("EQUIP_GP"      , ydEqpId    );
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
	 *      [A] 오퍼레이션명 : 대차 현재동 변경
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updTcarCurrBay(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "대차 현재동 변경[BCoilJspSeEJB.updTcarCurrBay] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		//Return Value
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		try {
			commUtils.printLog(logId, methodNm, "S+");

			String ydEqpId 		= commUtils.trim(rcvMsg.getFieldString("EQUIP_GP"     )); //야드설비ID(대차)
			String ydBayGpNew 	= commUtils.trim(rcvMsg.getFieldString("YD_CURR_BAY_GP")); //야드신규동구분

			if ("".equals(ydEqpId)) {
				throw new Exception("설비ID가 없습니다.");
			} else if ("".equals(ydBayGpNew)) {
				throw new Exception("변경할 현재동이 없습니다.");
			}

	
			String ydBayGpCurr  	= ""; //야드현재동구분(현재)
			String ydStkColGpCurr   = ""; //야드적치열구분(현재)
			String ydStkColGpNew    = ydEqpId.substring(0, 1) + ydBayGpNew + ydEqpId.substring(2); //야드적치열(신규)

			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(rcvMsg.getFieldString("MODIFIER")));

			jrParam.setField("YD_EQP_ID"         , ydEqpId       );
			jrParam.setField("YD_CURR_BAY_GP_NEW", ydBayGpNew);
			
			/**********************************************************
			* 1. 대차Bed상태 조회
			**********************************************************/
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getStatEqp 
			--설비상태조회 
			SELECT WPROG_STAT     AS YD_EQP_STAT
			     , WORK_MODE      AS YD_EQP_WRK_MODE
				 , STACK_MAX_QNTY	                  --적재 최대 수량
				 , STACK_MAX_WT		                  --적재 최대 중량
			     , CURR_STOP_LOC
			  FROM TB_YM_EQUIP EQ
			 WHERE EQUIP_GP = :V_YD_EQP_ID
			   AND DEL_YN    = 'N' 
			*/	   
			JDTORecordSet jsTcar = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getStatEqp", logId, methodNm, "대차Bed상태 조회");
			if (jsTcar != null && jsTcar.size() > 0) {
		    	JDTORecord jrTcar = jsTcar.getRecord(0);
	
		    	ydStkColGpCurr	= commUtils.trim(jrTcar.getFieldString("CURR_STOP_LOC"));                  //대차현재위치
		    	ydBayGpCurr     = commUtils.trim(jrTcar.getFieldString("CURR_STOP_LOC")).substring(1, 2);  //대차현재동
	
			    if ("".equals(ydStkColGpNew)) {
					throw new Exception("변경할 적치열이 없습니다.");
				}
		    } else {
				throw new Exception("대차 Bed상태 정보가 없습니다.");
		    }

			/**********************************************************
			* 2. 대차 저장위치 전체 비 활성화
			**********************************************************/
			jrParam.setField("STACK_COL_GP", ydEqpId.substring(0, 1) + "_" + ydEqpId.substring(2)); //야드적치열구분(대차전체Bed)

			//적치Bed(전체) 비활성화
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatStkBedActCA 
			UPDATE TB_YM_STACKER
			   SET MODIFIER            = :V_MODIFIER
			      ,MOD_DDTT            = SYSDATE
			      ,STACK_BED_ACTIVE_STAT = 'C'   --비활성화
			 WHERE STACK_COL_GP    LIKE '3_TC'||SUBSTR(:V_STACK_COL_GP,5,2)
			     AND DEL_YN              = 'N'
			*/	   
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatStkBedActCA", logId, methodNm, "적치Bed(전체) 비활성화");

			//적치단(전체) 재료 삭제
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdStkLyrClr1
			UPDATE TB_YM_STACKLAYER
			   SET MODIFIER                = :V_MODIFIER
			      ,MOD_DDTT                = SYSDATE
			      ,STOCK_ID                = NULL
			      ,STACK_LAYER_ACTIVE_STAT = 'C'
			      ,STACK_LAYER_STAT        = 'E'
			 WHERE STACK_COL_GP  LIKE  '3_TC'||SUBSTR(:V_STACK_COL_GP,5,2)
			   AND DEL_YN                  = 'N'

			*/	   
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdStkLyrClr1", logId, methodNm, "적치단 재료 삭제");

			/**********************************************************
			* 3. 현재동 변경 및 저장위치제원 전문 조회
			**********************************************************/
			if (!ydBayGpCurr.equals(ydBayGpNew)) {
				//설비 현재동 수정
				jrParam.setField("STACK_COL_GP", ydStkColGpNew);

				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdEqpCurrBay
				UPDATE TB_YM_EQUIP
				   SET MODIFIER       = :V_MODIFIER
				      ,MOD_DDTT       = SYSDATE
				      ,CURR_STOP_LOC  = :V_STACK_COL_GP
				 WHERE EQUIP_GP       = :V_YD_EQP_ID
				*/ 
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdEqpCurrBay", logId, methodNm, "설비 현재동 수정");

				//기존 Bed의 상태가 변경되었으면 저장위치제원(YDY1L001, YDY3L001) 전문 조회
				jrParam.setField("YD_INFO_SYNC_CD", "4"          ); //야드정보동기화코드(Bed)
				jrParam.setField("STACK_COL_GP"   , ydStkColGpNew); //야드적치열구분
				jrParam.setField("STACK_BED_GP"   , "01"         ); //야드적치Bed번호

				//전송Data 조회
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA7L001", jrParam));
			}
			
			/**********************************************************
			* 4. 신규 저장위치  활성화 및 저장위치제원 전문 조회
			**********************************************************/
			//신규 적치Bed Close 상태이면 활성화
			jrParam.setField("STACK_COL_GP"      	, ydStkColGpNew); //야드적치열구분
			jrParam.setField("STACK_BED_ACTIVE_STAT", "L"          ); //야드적치Bed활성상태(적치가능)
			
			//적치Bed 수정
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatStkBedActByCol/
			UPDATE TB_YM_STACKER
			   SET MODIFIER              = :V_MODIFIER
			      ,MOD_DDTT              = SYSDATE
			      ,STACK_BED_ACTIVE_STAT = :V_STACK_BED_ACTIVE_STAT
			 WHERE STACK_COL_GP          = :V_STACK_COL_GP
			   AND DEL_YN                = 'N'
			*/	   
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStatStkBedActByCol", logId, methodNm, "신규 적치Bed Close 상태이면 활성화");

			//적치단 수정
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdStkLyrActiveTC
			UPDATE TB_YM_STACKLAYER
			   SET MODIFIER                = :V_MODIFIER
			      ,MOD_DDTT                = SYSDATE
			      ,STOCK_ID                = NULL
			      ,STACK_LAYER_ACTIVE_STAT = 'E'
			      ,STACK_LAYER_STAT        = 'E'
			 WHERE STACK_COL_GP            = :V_STACK_COL_GP
			   AND SUBSTR(STACK_COL_GP,1,1)= '3'
			   AND SUBSTR(STACK_COL_GP,3,2)= 'TC'
			   AND DEL_YN                  = 'N'
			*/	   
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdStkLyrActiveTC", logId, methodNm, "신규 적치Bed Close 상태이면 활성화");
			

			//신규 Bed의 상태가 변경되었으면 저장위치제원(YDY1L001, YDY3L001) 전문 전송
			jrParam.setField("YD_INFO_SYNC_CD", "3"); //야드정보동기화코드(Bed)
			jrParam.setField("STACK_BED_GP"   , "");  
			//전송Data 조회
			jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA7L001", jrParam));

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
		String methodNm = "대차상태설정 등록처리[BCoilJspSeEJB.trtTcarStatSet] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			String sOPRN    = commUtils.nvl(commUtils.trim(gdReq.getParam("OPRN")), "N");	//영대차여부
			String trtDtlGp = commUtils.trim(gdReq.getParam("TRT_DTL_GP"));	//처리상세구분
			String ydEqpId  = commUtils.trim(gdReq.getParam("EQUIP_GP" ));	//야드설비ID(대차)
			String modifier = commUtils.trim(gdReq.getParam("userid"      ));	//수정자
			String currDate = commUtils.getDateTime14();						//현재시각

			if ("".equals(ydEqpId)) {
				throw new Exception("대차설비ID가 없습니다.");
			}

			//Return Value
			JDTORecord jrRtn = null;

			//EJB Call을 위한 Message 생성용 - Log ID, Method, 수정자 Set
			JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, modifier);

			jrYdMsg.setField("YD_EQP_ID", ydEqpId); //야드설비ID
			jrYdMsg.setField("EQUIP_GP" , ydEqpId); //야드설비ID

			if ("ST".equals(trtDtlGp)) {
				//설비상태 변경
				jrYdMsg.setField("JMS_TC_CD"          , "A7YML004"); //설비고장복구실적
				jrYdMsg.setField("YD_EQP_STAT"        , commUtils.trim(gdReq.getParam("YD_EQP_STAT"))); //야드설비상태(B:고장, N:정상)
				jrYdMsg.setField("YD_EQP_PAUSE_CODE"  , "0000"    ); //야드설비휴지코드
				jrYdMsg.setField("YD_EQP_TRBL_RCVR_DT", currDate  ); //야드설비고장복구일시

				EJBConnector sndConn = new EJBConnector("default", "BCoilL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvA7YML004", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
			} else if ("MD".equals(trtDtlGp)) {
				//작업Mode 변경
				jrYdMsg.setField("JMS_TC_CD"      , "A7YML003"); //설비운전모드전환
				jrYdMsg.setField("YD_EQP_WRK_MODE", commUtils.trim(gdReq.getParam("YD_EQP_WRK_MODE"))); //야드설비작업Mode(1:On-Line, 0:Off-Line)

				EJBConnector sndConn = new EJBConnector("default", "BCoilL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvA7YML003", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
			} else if ("HB".equals(trtDtlGp)) {
				//Home동 변경 - Log ID, Method, 수정자 Set
				JDTORecord jrParam = commUtils.getParam(logId, methodNm, modifier);

				jrParam.setField("EQUIP_GP"   , ydEqpId); //야드설비ID
				jrParam.setField("YD_HOME_LOC", ydEqpId.substring(0, 1) + commUtils.trim(gdReq.getParam("YD_HOME_BAY_GP")) + ydEqpId.substring(2));
				
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updEqpHomeBay 
				--설비 홈동 수정 - 
				UPDATE TB_YM_EQUIP
				   SET MODIFIER       = :V_MODIFIER
				      ,MOD_DDTT       = SYSDATE
				      ,WAIT_STOP_LOC  = :V_YD_HOME_LOC
				 WHERE EQUIP_GP       = :V_EQUIP_GP

				*/ 
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updEqpHomeBay", logId, methodNm, "Home동 변경");
				
			} else if ("CB".equals(trtDtlGp)) {
				//현재동 변경
				jrYdMsg.setField("YD_CURR_BAY_GP", commUtils.trim(gdReq.getParam("YD_CURR_BAY_GP")));
				jrRtn = this.updTcarCurrBay(jrYdMsg);
			} else if ("TS".equals(trtDtlGp)) {
				//공대차출발지시 등록
				jrYdMsg.setField("YD_BAY_GP", commUtils.trim(gdReq.getParam("YD_CURR_BAY_GP_TS"))); //야드동구분(상차동)
				jrYdMsg.setField("OPRN"     , sOPRN); //영대차여부
				jrRtn = ymComm.trtTcarSchLevWo(jrYdMsg);
			} else if ("TL".equals(trtDtlGp)) {
				//출발실적처리
				jrYdMsg.setField("JMS_TC_CD"      	, "A7YML011"); //대차이동실적
				jrYdMsg.setField("YD_MOVE_GP"		, "S"       ); //야드대차이동구분(출발)
				jrYdMsg.setField("YD_TCAR_CURR_BAY" , commUtils.trim(gdReq.getParam("YD_CURR_BAY_GP_TL"))); //야드동구분1(현재동)

				EJBConnector sndConn = new EJBConnector("default", "BCoilL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvA7YML011", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
			} else if ("TA".equals(trtDtlGp)) {
				//도착실적처리
				jrYdMsg.setField("JMS_TC_CD"      	, "A7YML011"); //대차이동실적
				jrYdMsg.setField("YD_MOVE_GP"		, "E"       ); //야드대차이동구분(도착)
				jrYdMsg.setField("YD_TCAR_CURR_BAY" , commUtils.trim(gdReq.getParam("YD_CURR_BAY_GP_TA"))); //야드동구분1(현재동)

				EJBConnector sndConn = new EJBConnector("default", "BCoilL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvA7YML011", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
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
	 * 대차스케줄 복구
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord TcarSchRollBack(GridData gdReq) throws DAOException {
		String methodNm = "대차스케줄 복구[BCoilJspSeEJB.TcarSchRollBack] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String ydWrkBookId_Unload ="";  // 하차지 예약 id
			String ydWrkBookId_load ="";	// 상차지 예약 id		 
			String YdTcarSchId 	   ="";
			String ProgStat         = "";
			String LU              ="";
			String UnloadStopLoc   ="";
			String LoadStopLoc   ="";
			String sCoilNo 	= "";
			String EquipGp  = "";
			String sStockMv;
			String sSTACK_COL_GP = "";
			
			JDTORecordSet rsResult;
			
			for (int ii = 0; ii < rowCnt; ii++) {

				/*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getBookChkYn
				SELECT *
				 FROM TB_YM_WRKBOOK WB
				    , TB_YM_WRKBOOKMTL WM
				WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID  
				  AND WB.DEL_YN = 'N'
				  AND WM.DEL_YN = 'N'
				  AND WM.STOCK_ID  = :V_STOCK_ID 
				*/
				jrParam.setField("STOCK_ID" 		, commUtils.getValue(gdReq, "STOCK_ID", ii));
				
				JDTORecordSet jsDnTc = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getBookChkYn", logId, methodNm, "작업예약없는건 대상");
				
				if (jsDnTc.size() > 0 ){
				   throw new Exception(commUtils.getValue(gdReq, "STOCK_ID", ii) + " 코일에 대한 작업예약이  존재 합니다!! 크레인스케줄 및 작업예약 취소(삭제)후에 실행가능 합니다!");
				}	
			}
			
			//기존 대차재료정보 삭제 및 저장품 등록
			for (int ii = 0; ii < rowCnt; ii++) {

				if(!"".equals(commUtils.getValue(gdReq, "OLD_SSTL_NO", ii))) {
					//이전 대차재료정보는 삭제 처리 한다.
					jrParam.setField("YD_TCAR_SCH_ID"	, commUtils.getValue(gdReq, "YD_TCAR_SCH_ID", ii)); 
					jrParam.setField("STOCK_ID"			, commUtils.getValue(gdReq, "OLD_SSTL_NO", ii)); 
	
					/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.delTCarFtMvMtl 
					DELETE USRYMA.TB_YM_TCARFTMVMTL
					 WHERE YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
					   AND STOCK_ID = :V_STOCK_ID   
					*/
					commDao.insert(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.delTCarFtMvMtl", logId, methodNm, "이송작업재료삭제");
				}
				
				sCoilNo 	= commUtils.getValue(gdReq, "STOCK_ID", ii);			//코일번호
				
				if(!"".equals(sCoilNo)) { 
					//새로 등록한 코일 번호 처리
					
					sStockMv	= ymComm.getStockMv(logId, methodNm, sCoilNo);
					
					if("".equals(sStockMv)) {
						//코일공통의 차공정코드로 이동조건 설정, 리턴 값이 ""이면 코일공통에 존재하지 않는 코일 번호로 Error 처리 한다.
						throw new Exception("코일공통에 존재하지 않는 COIL_NO : " + sCoilNo);
					}
					
					//TB_YM_STOCK 에 존재 하는지 확인
					jrParam.setField("STL_NO" , sCoilNo);
					/*com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYmStockchk
					SELECT A.STOCK_ID         AS STL_NO
					      ,B.COIL_T           AS YD_MTL_T
					      ,B.COIL_W           AS YD_MTL_W
					      ,B.COIL_LEN         AS YD_MTL_L
					      ,B.COIL_WT          AS YD_MTL_WT
					      ,''                         AS YD_AIM_YD_GP
					      ,''                         AS YD_AIM_BAY_GP
					  FROM TB_YM_STOCK  A
					      ,USRPTA.TB_PT_COILCOMM B
					 WHERE  A.STOCK_ID    =B.COIL_NO 
					   AND  A.STOCK_ID   = :V_STL_NO */
					rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYmStockchk", logId, methodNm, "TB_YM_STOCK 에 존재 하는지 확인");
					
					if(rsResult.size()<=0) {
						
						//TB_YM_STOCK에 존재 한지 않으면 생성한다.
						jrParam.setField("STOCK_ID" 		, sCoilNo);
						jrParam.setField("STOCK_ITEM" 		, YmConstant.ITEM_CM);
						jrParam.setField("STOCK_MOVE_TERM" 	, sStockMv);
						/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.insStock 
						MERGE INTO TB_YM_STOCK ST USING (
						    SELECT :V_STOCK_ID          AS STOCK_ID                                 --재료번호
						         , :V_MODIFIER          AS MODIFIER         --수정자
						         , SYSDATE              AS MOD_DDTT         --수정일시
						         , 'N'                  AS DEL_YN           --삭제유무
						         , :V_STOCK_ITEM        AS STOCK_ITEM       --저장품 품목
						         , :V_STOCK_MOVE_TERM   AS STOCK_MOVE_TERM  --저장품 이동 조건
						      FROM DUAL
						) DD ON ( ST.STOCK_ID = DD.STOCK_ID)
	
						WHEN NOT MATCHED THEN
						    INSERT (
						           STOCK_ID             , STOCK_ITEM        , STOCK_MOVE_TERM 
						         , REGISTER             , REG_DDTT          , MODIFIER  
						         , MOD_DDTT             , DEL_YN    
						         )
						    VALUES (
						           :V_STOCK_ID          , DD.STOCK_ITEM     , DD.STOCK_MOVE_TERM 
						         , DD.MODIFIER          , DD.MOD_DDTT       , DD.MODIFIER  
						         , DD.MOD_DDTT          , DD.DEL_YN 
						         )
						WHEN MATCHED THEN 
						    UPDATE SET
						           STOCK_ITEM       = (CASE WHEN KEEPSTOCK_STL_YN='Y' THEN STOCK_ITEM ELSE DD.STOCK_ITEM END) 
						         , STOCK_MOVE_TERM  = DD.STOCK_MOVE_TERM 
						         , MODIFIER         = DD.MODIFIER 
						         , MOD_DDTT         = DD.MOD_DDTT     */     
						commDao.insert(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.insStock", logId, methodNm, "TB_YM_STOCK 생성");
					}
				}
			}

			for (int ii = 0; ii < rowCnt; ii++) {
				
				sCoilNo 	= commUtils.getValue(gdReq, "STOCK_ID", ii);			//코일번호
				
				if(!"".equals(sCoilNo)) {
					
					LU  		= commUtils.trim(gdReq.getParam("WPROG_STAT")); //상하차구분 (L:상차완료, U:하차도착)
					EquipGp  	= commUtils.trim(gdReq.getParam("EQUIP_GP"));   //대차설비번호
					
					if("U".equals(LU)) {
						
						/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insA7YML011WbTCarIns 
						INSERT INTO TB_YM_WRKBOOK WB
						       (WB.YD_WBOOK_ID         , WB.REGISTER              , WB.REG_DDTT              ,
						        WB.DEL_YN              , WB.YD_GP                 , WB.YD_BAY_GP             , WB.YD_SCH_CD   , WB.YD_SCH_PRIOR ,
						        WB.YD_SCH_PROG_STAT    , WB.YD_SCH_ST_GP          , WB.YD_SCH_REQ_GP         , WB.YD_AIM_YD_GP, WB.YD_AIM_BAY_GP,
						        WB.YD_TO_LOC_DCSN_MTD  , WB.YD_TO_LOC_GUIDE       , WB.YD_WRK_PLAN_TCAR)
						VALUES (:V_YD_CARUD_WRK_BOOK_ID, :V_MODIFIER              ,  SYSDATE                 ,
						        'N'                    , SUBSTR(:V_YD_SCH_CD,1,1) , SUBSTR(:V_YD_SCH_CD,2,1) , :V_YD_SCH_CD   , :V_YD_WRK_CRN_PRIOR ,
						        'W'                    , 'O'                      , '1'                      ,SUBSTR(:V_YD_SCH_CD,1,1) , SUBSTR(:V_YD_SCH_CD,2,1)    ,
						        NULL                   , NULL                     , :V_YD_EQP_ID)
						*/        
						ydWrkBookId_Unload = commDao.getSeqId(logId, methodNm, "WrkBook");
						UnloadStopLoc      = commUtils.getValue(gdReq, "CURR_STOP_LOC", ii);
						
						jrParam.setField("YD_CARUD_WRK_BOOK_ID"  ,ydWrkBookId_Unload);
						jrParam.setField("YD_SCH_CD"			 ,UnloadStopLoc + "LM");
						jrParam.setField("YD_WRK_CRN_PRIOR"      ,"6");
						jrParam.setField("YD_EQP_ID"             , EquipGp       	); 
								
						commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insA7YML011WbTCarIns", logId, methodNm, "작업예약 등록");
						
						//작업예약재료 등록
						/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insA7YML009WbMtlTCarIns 
						INSERT INTO TB_YM_WRKBOOKMTL WM
						       (WM.YD_WBOOK_ID          , WM.STOCK_ID       , WM.REGISTER       , WM.REG_DDTT    ,
						        WM.MODIFIER             , WM.MOD_DDTT       , WM.DEL_YN         , WM.STACK_COL_GP,
						        WM.STACK_BED_GP         , WM.STACK_LAYER_GP , WM.YD_UP_COLL_SEQ)
						VALUES (:V_YD_CARUD_WRK_BOOK_ID , :V_STOCK_ID       , :V_MODIFIER       , SYSDATE        ,
						        :V_MODIFIER             , SYSDATE           , 'N'               , :V_STACK_COL_GP,
						        :V_STACK_BED_GP         , :V_STACK_LAYER_GP , :V_YD_UP_COLL_SEQ);        
						*/        
						jrParam.setField("YD_CARUD_WRK_BOOK_ID"  ,ydWrkBookId_Unload);
						jrParam.setField("STACK_COL_GP" 	, EquipGp); 
						jrParam.setField("STACK_BED_GP" 	, commUtils.getValue(gdReq, "STACK_BED_GP", ii)); 
						jrParam.setField("STACK_LAYER_GP" 	, commUtils.getValue(gdReq, "STACK_LAYER_GP", ii)); 
						jrParam.setField("YD_UP_COLL_SEQ" 	, "1");
						
						commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insA7YML009WbMtlTCarIns", logId, methodNm, "작업예약재료 등록");							        
						
					}
				}
			}
			
			
			for (int ii = 0; ii < rowCnt; ii++) {

				LU       		= commUtils.trim(gdReq.getParam("WPROG_STAT")); //상하차구분 (L:상차완료, U:하차도착)
				EquipGp  		= commUtils.trim(gdReq.getParam("EQUIP_GP"));   //대차설비번호

				sCoilNo 		= commUtils.getValue(gdReq, "STOCK_ID", ii);			//코일번호
				LoadStopLoc		= commUtils.getValue(gdReq, "CARLOAD_STOP_LOC", ii);   	//상차정지위치
				UnloadStopLoc	= commUtils.getValue(gdReq, "CARUNLOAD_STOP_LOC", ii); 	//하차정지위치
		    	YdTcarSchId		= commUtils.getValue(gdReq, "YD_TCAR_SCH_ID", ii);     	//대차스케줄ID
		    	
		    	sSTACK_COL_GP   = commUtils.getValue(gdReq, "CURR_STOP_LOC", ii);     	
			       
				if(LU.equals("L")){   //상차지 세팅

					ProgStat      =  "5" ;        //상차완료
					LoadStopLoc   =  sSTACK_COL_GP;
					
				}else if(LU.equals("U")){ //하차지 세팅

					ProgStat      =  "B" ;      //하차도착
					UnloadStopLoc =  sSTACK_COL_GP;
					
				}else {
					break;
				}
				
               	//대차스케줄 갱신
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateTcshbefore3 
				UPDATE TB_YM_TCARSCH
				   SET MODIFIER              = :V_MODIFIER
				      ,MOD_DDTT              = SYSDATE
				      ,YD_CARLD_WRK_BOOK_ID  = nvl(:V_YD_CARLD_WRK_BOOK_ID,YD_CARLD_WRK_BOOK_ID)
				      ,YD_CARUD_WRK_BOOK_ID  = nvl(:V_YD_CARUD_WRK_BOOK_ID,YD_CARUD_WRK_BOOK_ID)
				      ,YD_CARLD_STOP_LOC     = :V_YD_CARLD_STOP_LOC
				      ,YD_CARUD_STOP_LOC     = :V_YD_CARUD_STOP_LOC
                      ,YD_CAR_PROG_STAT      = :V_YD_CAR_PROG_STAT	
                      ,YD_EQP_WRK_STAT       = 'L' 					      
				WHERE YD_TCAR_SCH_ID         = :V_YD_TCAR_SCH_ID
				  AND DEL_YN                 = 'N'
				*/				     
			     
			     jrParam.setField("YD_TCAR_SCH_ID" 		       , YdTcarSchId);
			     jrParam.setField("YD_CARLD_WRK_BOOK_ID"       , ""   ); // 상차인 예약 id
			     jrParam.setField("YD_CARUD_WRK_BOOK_ID"        , ""   ); // 하차인 예약 id

			     jrParam.setField("YD_CARLD_STOP_LOC"          , LoadStopLoc       ); //상차정지위치
				 jrParam.setField("YD_CARUD_STOP_LOC"          , UnloadStopLoc     ); //하차정지위치
				 jrParam.setField("YD_CAR_PROG_STAT"           , ProgStat          ); //차량상태
				 
	    		commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateTcshbefore3", logId, methodNm, "초기 대차스케줄에 갱신");
			
				jrParam.setField("STACK_COL_GP" 	, commUtils.getValue(gdReq, "STACK_COL_GP", ii)); 
				jrParam.setField("STACK_BED_GP" 	, commUtils.getValue(gdReq, "STACK_BED_GP", ii)); 
				jrParam.setField("STACK_LAYER_GP"   , commUtils.getValue(gdReq, "STACK_LAYER_GP", ii)); 

		
				//이송작업재료등록
				jrParam.setField("STOCK_ID"			, sCoilNo); 
				jrParam.setField("MODIFIER"	        , commUtils.trim(gdReq.getParam("userid"))); 

				
				/*MERGE INTO TB_YM_TCARFTMVMTL TM USING (
				SELECT  :V_YD_TCAR_SCH_ID AS YD_TCAR_SCH_ID
				      , ST.STOCK_ID
				      , :V_MODIFIER       AS MODIFIER
				      , SYSDATE           AS MOD_DDTT
				      , 'N'               AS DEL_YN
				      , :V_STACK_BED_GP AS STACK_BED_GP
  					  , :V_STACK_LAYER_GP AS STACK_LAYER_GP
				      , CC.HCR_GP
				      , CC.CURR_PROG_CD    AS STL_PROG_CD
				      , ST.STOCK_ITEM      AS YD_MTL_ITEM
				  FROM TB_YM_STOCK     ST
				     , USRPTA.TB_PT_COILCOMM   CC  
				 WHERE ST.STOCK_ID      = CC.COIL_NO
				   AND ST.STOCK_ID      = :V_STOCK_ID
				   AND ST.DEL_YN        = 'N'
				) DD ON (TM.YD_TCAR_SCH_ID = DD.YD_TCAR_SCH_ID AND TM.STOCK_ID = DD.STOCK_ID)
				WHEN NOT MATCHED THEN
				INSERT (TM.YD_TCAR_SCH_ID, TM.STOCK_ID     , TM.REGISTER   , TM.REG_DDTT     ,
				        TM.MODIFIER      , TM.MOD_DDTT     , TM.DEL_YN     , TM.STACK_BED_GP,
				        TM.STACK_LAYER_GP, TM.HCR_GP       , TM.STL_PROG_CD, TM.YD_MTL_ITEM   )
				VALUES (DD.YD_TCAR_SCH_ID, DD.STOCK_ID     , DD.MODIFIER   , DD.MOD_DDTT     ,
				        DD.MODIFIER      , DD.MOD_DDTT     , DD.DEL_YN     , DD.STACK_BED_GP,
				        DD.STACK_LAYER_GP, DD.HCR_GP       , DD.STL_PROG_CD, DD.YD_MTL_ITEM   )
				WHEN MATCHED THEN
				UPDATE SET
				    TM.MODIFIER = DD.MODIFIER
				   ,TM.MOD_DDTT = DD.MOD_DDTT
				   ,TM.DEL_YN = DD.DEL_YN
				   ,TM.STACK_BED_GP = DD.STACK_BED_GP
				   ,TM.STACK_LAYER_GP = DD.STACK_LAYER_GP
				   ,TM.HCR_GP = DD.HCR_GP
				   ,TM.STL_PROG_CD = DD.STL_PROG_CD
				   ,TM.YD_MTL_ITEM = DD.YD_MTL_ITEM*/		
				
				commDao.insert(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updTCarFtMvMtl", logId, methodNm, "이송작업재료등록");

			}

			//하차위치 적치단 재료번호 등록 -> 혹시 정보가 맞지 않을 수도 있으므로 무조건 Update
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStkLyrTCarStl 
			MERGE INTO TB_YM_STACKLAYER SL USING (
			SELECT SL.STACK_COL_GP
			      ,SL.STACK_BED_GP
			      ,SL.STACK_LAYER_GP
			      ,'E'                              AS STACK_LAYER_ACTIVE_STAT --적치가능
			      ,DECODE(TM.STOCK_ID,NULL,'E','C') AS STACK_LAYER_STAT --적치가능,적치중
			      ,TM.STOCK_ID
			  FROM TB_YM_STACKLAYER      SL
			      ,TB_YM_TCARFTMVMTL TM
			 WHERE SL.STACK_COL_GP    = :V_STACK_COL_GP
			   AND SL.STACK_BED_GP    = TM.STACK_BED_GP(+) 
			   AND :V_YD_TCAR_SCH_ID  = TM.YD_TCAR_SCH_ID(+)
			   AND 'N'                = TM.DEL_YN(+)
			) DD ON (SL.STACK_COL_GP = DD.STACK_COL_GP AND SL.STACK_BED_GP   = DD.STACK_BED_GP
			                                           AND SL.STACK_LAYER_GP = DD.STACK_LAYER_GP)
			WHEN MATCHED THEN UPDATE SET
			     SL.MODIFIER                = :V_MODIFIER
			    ,SL.MOD_DDTT                = SYSDATE
			    ,SL.STACK_LAYER_ACTIVE_STAT = DD.STACK_LAYER_ACTIVE_STAT
			    ,SL.STACK_LAYER_STAT        = DD.STACK_LAYER_STAT
			    ,SL.STOCK_ID                = DD.STOCK_ID
			 */  
		    jrParam.setField("YD_TCAR_SCH_ID" 		       , YdTcarSchId);
		    jrParam.setField("STACK_COL_GP" 		       , sSTACK_COL_GP);
		     
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStkLyrTCarStl", logId, methodNm, "도착위치 단 활성화");

			
			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name

			jrYdMsg.setField("STACK_COL_GP"  , UnloadStopLoc ); // 작업위치
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdTcarWbookidLD 
			SELECT YD_WBOOK_ID
			  FROM TB_YM_WRKBOOK
			 WHERE DEL_YN = 'N'
			   AND YD_SCH_CD = :V_STACK_COL_GP ||'LM'
			   AND YD_WBOOK_ID NOT IN ( SELECT YD_WBOOK_ID FROM TB_YM_CRNSCH WHERE DEL_YN = 'N')
			*/
			JDTORecordSet jsTcsch = commDao.select(jrYdMsg, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdTcarWbookidLD", logId, methodNm, "크레인스케줄 조회");   		
			JDTORecord jrTcsch = JDTORecordFactory.getInstance().create();
			JDTORecord jsMsg = JDTORecordFactory.getInstance().create();
    		for(int Loop_i = 1; Loop_i <= jsTcsch.size(); Loop_i++) {
    			jsTcsch.absolute(Loop_i);
    			jrTcsch = JDTORecordFactory.getInstance().create();
    			jrTcsch.setRecord(jsTcsch.getRecord());
    			
    			//크레인 스케줄 기동 YMYMJ303 호출
    			jsMsg.setField("JMS_TC_CD"			, "YMYMJ303"); 
    			jsMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시				
    			jsMsg.setField("YD_SCH_CD"  		, ""); //야드스케쥴코드
    			jsMsg.setField("YD_EQP_ID"  		, ""); //야드설비ID
    			jsMsg.setField("YD_WBOOK_ID"+Loop_i	, jrTcsch.getFieldString("YD_WBOOK_ID")); //야드작업예약ID
			}	
			jsMsg.setField("SCH_CNT"				, ""+jsTcsch.size()); 
			
			if (jsTcsch.size() > 0 ) {
				jrRtn = commUtils.addSndData(jrRtn, jsMsg);
			}	
			
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of TcarSchRollBack		
	
	/**
	 * 대차이동구간변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updTCarYdGpMgt(GridData gdReq) throws DAOException {
		String methodNm = "대차이동구간변경[BCoilJspSeEJB.updTCarYdGpMgt] < " + gdReq.getNavigateValue();
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
				
				jrParam.setField("EQUIP_GP"					,commUtils.getValue(gdReq, "EQUIP_GP", ii));
				jrParam.setField("YD_TCAR_WRK_ABLE_BAY1"	,commUtils.getValue(gdReq, "YD_TCAR_WRK_ABLE_BAY1", ii));
				jrParam.setField("YD_TCAR_WRK_ABLE_BAY2"	,commUtils.getValue(gdReq, "YD_TCAR_WRK_ABLE_BAY2", ii));
				jrParam.setField("YD_TCAR_WRK_ABLE_BAY3"	,commUtils.getValue(gdReq, "YD_TCAR_WRK_ABLE_BAY3", ii));
				jrParam.setField("YD_TCAR_WRK_ABLE_BAY4"	,commUtils.getValue(gdReq, "YD_TCAR_WRK_ABLE_BAY4", ii));
				jrParam.setField("YD_TCAR_WRK_ABLE_BAY5"	,commUtils.getValue(gdReq, "YD_TCAR_WRK_ABLE_BAY5", ii));
				jrParam.setField("YD_TCAR_WRK_ABLE_BAY6"	,commUtils.getValue(gdReq, "YD_TCAR_WRK_ABLE_BAY6", ii));
				
				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updTCarYdGpMgt", logId, methodNm, "대차이동구간변경");
								
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updTCarYdGpMgt
	
	
	/**
	 * 위치별 적치현황조회 - Bed상태 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updBedActStat(GridData gdReq) throws DAOException {
		String methodNm = "위치별 적치현황조회 - Bed상태 수정[BCoilJspSeEJB.updBedActStat] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			String sSTOCK_ID_L = StringHelper.evl(gdReq.getParam("ABLE_LOC_LIST_L"), "");
			String sSTOCK_ID_A = StringHelper.evl(gdReq.getParam("ABLE_LOC_LIST_A"), "");
			String sSTOCK_ID_X = StringHelper.evl(gdReq.getParam("ABLE_LOC_LIST_X"), "");
			
			String[] arrSTOCK_ID_L = sSTOCK_ID_L == "" ? null : gdReq.getParam("ABLE_LOC_LIST_L").split(",");
			String[] arrSTOCK_ID_A = sSTOCK_ID_A == "" ? null : gdReq.getParam("ABLE_LOC_LIST_A").split(",");
			String[] arrSTOCK_ID_X = sSTOCK_ID_X == "" ? null : gdReq.getParam("ABLE_LOC_LIST_X").split(",");
			
			int CntL = arrSTOCK_ID_L == null ? 0 : arrSTOCK_ID_L.length;
			int CntA = arrSTOCK_ID_A == null ? 0 : arrSTOCK_ID_A.length;
			int CntX = arrSTOCK_ID_X == null ? 0 : arrSTOCK_ID_X.length;
			
			/************************
			 *** 적치가능
			 ************************/
			for (int l = 0 ; l < CntL; ++l) {
				jrParam.setField("STACK_COL_GP"    , arrSTOCK_ID_L[l].substring(0, 6));
				jrParam.setField("STACK_BED_GP"    , arrSTOCK_ID_L[l].substring(6, 8));
				jrParam.setField("STACK_LAYER_ACTIVE_STAT", YmConstant.STACK_LAYER_ACTIVE_STAT_E);

				/*
				UPDATE TB_YM_STACKLAYER
				   SET STACK_LAYER_ACTIVE_STAT = :V_STACK_LAYER_ACTIVE_STAT
				     , MODIFIER                = :V_MODIFIER
				     , MOD_DDTT                = SYSDATE
				 WHERE DEL_YN = 'N'
				   AND STACK_COL_GP = :V_STACK_COL_GP
				   AND STACK_BED_GP = :V_STACK_BED_GP
				 */
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStackLayerActiveStat", logId, methodNm, "TB_YM_STACKLAYER - 적치단상태 수정");
				
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setField("TC_CD"          , "YMA7L001");
				jrYdMsg.setField("MSG_GP"         , "I");
				jrYdMsg.setField("YD_INFO_SYNC_CD", "4");
				jrYdMsg.setField("STACK_COL_GP"   , arrSTOCK_ID_L[l].substring(0, 6));
				jrYdMsg.setField("STACK_BED_GP"   , arrSTOCK_ID_L[l].substring(6, 8));
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA7L001", jrYdMsg));
			}
			
			/****************************
			 *** 2단적치불가능
			 ****************************/
			for (int a = 0 ; a < CntA; ++a) {
				jrParam.setField("STACK_COL_GP"    , arrSTOCK_ID_A[a].substring(0, 6));
				jrParam.setField("STACK_BED_GP"    , arrSTOCK_ID_A[a].substring(6, 8));
				jrParam.setField("STACK_LAYER_ACTIVE_STAT", YmConstant.STACK_LAYER_ACTIVE_STAT_N);
				
				/*
				UPDATE TB_YM_STACKLAYER
				   SET STACK_LAYER_ACTIVE_STAT = :V_STACK_LAYER_ACTIVE_STAT
				     , MODIFIER                = :V_MODIFIER
				     , MOD_DDTT                = SYSDATE
				 WHERE DEL_YN = 'N'
				   AND STACK_COL_GP   = :V_STACK_COL_GP
				   AND STACK_BED_GP   = :V_STACK_BED_GP
				   AND STACK_LAYER_GP = '02'
				 */
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStackLayerActiveStat2", logId, methodNm, "TB_YM_STACKLAYER - 적치단상태 수정");
				
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setField("TC_CD"          , "YMA7L001");
				jrYdMsg.setField("MSG_GP"         , "I");
				jrYdMsg.setField("YD_INFO_SYNC_CD", "4");
				jrYdMsg.setField("STACK_COL_GP"   , arrSTOCK_ID_A[a].substring(0, 6));
				jrYdMsg.setField("STACK_BED_GP"   , arrSTOCK_ID_A[a].substring(6, 8));
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA7L001", jrYdMsg));					
			} //end for

			/**************************
			 *** 적치불가
			 **************************/
			for (int x = 0 ; x < CntX; ++x) {
				jrParam.setField("STACK_COL_GP"    , arrSTOCK_ID_X[x].substring(0, 6));
				jrParam.setField("STACK_BED_GP"    , arrSTOCK_ID_X[x].substring(6, 8));
				jrParam.setField("STACK_LAYER_ACTIVE_STAT", YmConstant.STACK_LAYER_ACTIVE_STAT_N);

				/*
				UPDATE TB_YM_STACKLAYER
				   SET STACK_LAYER_ACTIVE_STAT = :V_STACK_LAYER_ACTIVE_STAT
				     , MODIFIER                = :V_MODIFIER
				     , MOD_DDTT                = SYSDATE
				 WHERE DEL_YN = 'N'
				   AND STACK_COL_GP = :V_STACK_COL_GP
				   AND STACK_BED_GP = :V_STACK_BED_GP
				 */
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStackLayerActiveStat", logId, methodNm, "TB_YM_STACKLAYER - 적치단상태 수정");
				
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setField("TC_CD"          , "YMA7L001");
				jrYdMsg.setField("MSG_GP"         , "I");
				jrYdMsg.setField("YD_INFO_SYNC_CD", "4");
				jrYdMsg.setField("STACK_COL_GP"   , arrSTOCK_ID_X[x].substring(0, 6));
				jrYdMsg.setField("STACK_BED_GP"   , arrSTOCK_ID_X[x].substring(6, 8));
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA7L001", jrYdMsg));		

			} //end for
						
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updBedActStat
	
	
	
	/**
	 * 오퍼레이션명 :PDA 
	 *
	 * 야드운영자가 Schedule관리기능을 통해 Crane Up Down 실적을 발생시킨다.
        * 
        * param String	 : 전문코드
        * param String	 : SCH_ID
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public JDTORecord callCraneUpLnRtInfo(JDTORecord jrecord){
		

		String logId = commUtils.trim(jrecord.getFieldString("userid"));
 
		String sTC      = commUtils.trim(jrecord.getFieldString("TC_CD")) ;
		String sTCNm =   YmConstant.A7YML008.equals(sTC) ? "권상":"권하";
		String methodNm = " Crane  실적을 발생[BCoilJspSeEJB.callCraneUpLnRtInfo] < " + sTCNm ;
		
		String sYdGp    = commUtils.trim(jrecord.getFieldString("YD_GP")) ;
		String sYdBayGP = commUtils.trim(jrecord.getFieldString("YD_BAY_GP")) ;
		String sYdEqpId = commUtils.trim(jrecord.getFieldString("YD_EQP_ID")) ;
		String sYdSchCd = commUtils.trim(jrecord.getFieldString("YD_SCH_CD")) ;
		String sSchId   = commUtils.trim(jrecord.getFieldString("YD_CRN_SCH_ID")) ;

		
		commUtils.printParam(logId, jrecord);
		commUtils.printLog(logId, methodNm, "S+");
 
		
		try{
			//Return Value
			JDTORecord jrRtn = null;
	    	JDTORecord jrParam = JDTORecordFactory.getInstance().create();
	    	
	    	// USER_ID 셋팅
	    	jrParam.setField("USER_ID", logId);
	    	jrParam.setField("YD_GP", sYdGp);
	    	jrParam.setField("YD_BAY_GP", sYdBayGP);
	    	jrParam.setField("YD_EQP_ID", sYdEqpId);
	    	jrParam.setField("YD_SCH_CD", sYdSchCd);
	    	jrParam.setField("YD_CRN_SCH_ID", sSchId);

	    	
	    	/*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getSchInfoWithSchId 
	    	SELECT *
	    	FROM TB_YM_CRNSCH
	    	WHERE YD_CRN_SCH_ID= :V_YD_CRN_SCH_ID
            */ 
			JDTORecordSet jsschInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getSchInfoWithSchId", logId, methodNm, "TB_YM_CRNSCH 조회"); 	
			if (jsschInfo == null || jsschInfo.size() <= 0) {
 
    			throw new Exception("스케쥴정보 존재안함.");
    		}
			
			
			/*  com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getbtCrnWrkMgtjm 
				--크레인작업관리 스케줄조회
				WITH TEMP_TABLE AS (
				SELECT A.YD_CRN_SCH_ID
				     , A.YD_WORD_DT
				     , A.YD_WBOOK_ID
				     , A.YD_SCH_CD
				     , A.YD_EQP_ID
				     , SUBSTR(A.YD_UP_WO_LOC, 2, 5) || TO_NUMBER(A.YD_UP_WO_LAYER) ||
				       SUBSTR(A.YD_UP_WO_LOC, 7, 2)              AS YD_UP_WO_LOC2
				     , SUBSTR(A.YD_DN_WO_LOC, 2, 5) || TO_NUMBER(A.YD_DN_WO_LAYER) ||
				       SUBSTR(A.YD_DN_WO_LOC, 7, 2)              AS YD_DN_WO_LOC2
				     , YD_DN_WO_LOC || YD_DN_WO_LAYER AS YD_DN_LOC 
				     , YD_UP_WO_LOC
				     , YD_UP_WO_LAYER
				     , YD_DN_WO_LOC
				     , YD_DN_WO_LAYER
				     , YD_EQP_WRK_SH
				     , YD_EQP_WRK_WT
				     , DECODE (A.YD_WRK_PROG_STAT, '1' , '선택'
				                                 , '2' , '권상'
				                                 , 'C' , '보류'
				                                 , 'S' , '대기'
				                                 ,' ') AS YD_WRK_PROG_STAT_NM  
				     , YD_SCH_PRIOR
				     , B.STOCK_ID
				     , A.YD_GP
				     , A.YD_TO_LOC_DCSN_MTD
				     , A.YD_WRK_PROG_STAT
				     , TO_CHAR(A.YD_UP_CMPL_DT,'DD-HH24:MI:SS') AS YD_UP_CMPL_DT
				     , (SELECT COUNT(*) FROM USRYMA.TB_YM_CRNSCH WHERE YD_WBOOK_ID = A.YD_WBOOK_ID AND DEL_YN = 'N') AS STL_CNT
				     , (CASE WHEN B.YD_AID_WRK_YN = 'Y' THEN '보조' ELSE '주' END ) AS YD_AID_WRK_YN
				     , B.YD_AID_WRK_YN AS MAIN_CHK
				     , SUBSTR(A.YD_UP_WO_LOC, 1, 1) AS DN_WO_LOC_YD 
				     , A.YD_L2_REQUEST_STAT
				     , A.YD_WRK_PROG_REQ_MSG
				     , A.YD_WRK_PARTY AS YD_SCH_ST_GP
				     , MIN(A.YD_WRK_PROG_STAT||A.YD_CRN_SCH_ID) OVER (PARTITION BY  A.YD_WBOOK_ID ) AS CHK
				     , A.YD_UP_WO_LOC_XAXIS
				     , A.YD_UP_WO_LOC_YAXIS
				     , A.YD_UP_WO_LOC_ZAXIS
				     , A.YD_DN_WO_LOC_XAXIS
				     , A.YD_DN_WO_LOC_YAXIS
				     , A.YD_DN_WO_LOC_ZAXIS       
				     , (SELECT CAR_CARD_NO FROM TB_YM_STOCK WHERE STOCK_ID = B.STOCK_ID) AS CAR_CARD_NO
				  FROM USRYMA.TB_YM_CRNSCH    A
				     , USRYMA.TB_YM_CRNWRKMTL B
				 WHERE A.YD_CRN_SCH_ID=B.YD_CRN_SCH_ID
				   AND A.DEL_YN = 'N'
				   AND B.DEL_YN = 'N'
				   AND A.YD_GP LIKE :V_YD_GP||'%'
				   AND A.YD_BAY_GP LIKE :V_YD_BAY_GP||'%'
				   AND A.YD_EQP_ID LIKE :V_YD_EQP_ID||'%'
				   AND A.YD_SCH_CD LIKE :V_YD_SCH_CD||'%' 
				   AND A.YD_CRN_SCH_ID LIKE :V_YD_CRN_SCH_ID||'%' 
				)
							SELECT *
							 FROM (
				
				                SELECT COUNT(*) OVER() AS TOTALCOUNT 
				                     , D.YD_SCH_PRIOR   
				                     , CASE WHEN F.YD_EQP_AUTO_CRN_MODE = '4' OR F.YD_EQP_AUTO_CRN_MODE = '5'
				                            THEN F.EQUIP_NAME||'[정지]'
				                       ELSE F.EQUIP_NAME 
				                       END YD_EQP_NAME  
				                     , E.CD_CONTENTS AS YD_SCH_CD_NM
				                     , D.YD_AID_WRK_YN
				                     , D.STOCK_ID             
				                     , D.YD_DN_LOC
				                     , D.YD_UP_WO_LOC         
				                     , D.YD_UP_WO_LAYER
				                     , D.YD_UP_WO_LOC || D.YD_UP_WO_LAYER AS YD_UP_WO_LOC_LAYER
				                     , D.YD_DN_WO_LOC         
				                     , D.YD_DN_WO_LAYER
				                     , D.YD_EQP_WRK_SH
				                     , D.YD_EQP_WRK_WT
				                     , TO_CHAR(D.YD_WORD_DT, 'DD/HH24:MI:SS')  AS YD_WORD_DT
				                     , CASE WHEN D.YD_WORD_DT IS NULL THEN NULL
				                            ELSE TRUNC(TO_DATE(TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'), 'YYYYMMDDHH24MISS') - TO_DATE(TO_CHAR(D.YD_WORD_DT, 'YYYYMMDDHH24MISS'), 'YYYYMMDDHH24MISS')) ||'/ ' ||
				                                 LPAD(TRUNC(MOD((TO_DATE(TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'), 'YYYYMMDDHH24MISS') - TO_DATE(TO_CHAR(D.YD_WORD_DT, 'YYYYMMDDHH24MISS'), 'YYYYMMDDHH24MISS')),1)*24), 2, '0') || ':' ||
				                                 LPAD(TRUNC(MOD((TO_DATE(TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'), 'YYYYMMDDHH24MISS') - TO_DATE(TO_CHAR(D.YD_WORD_DT, 'YYYYMMDDHH24MISS'), 'YYYYMMDDHH24MISS'))*24,1)*60), 2, '0') ||':'||
				                                 LPAD(TRUNC(MOD((TO_DATE(TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'), 'YYYYMMDDHH24MISS') - TO_DATE(TO_CHAR(D.YD_WORD_DT, 'YYYYMMDDHH24MISS'), 'YYYYMMDDHH24MISS'))*24*60,1)*60), 2, '0')
				                            END
				                       AS PASS_HR
				                     , D.YD_TO_LOC_DCSN_MTD  
				                     , D.YD_WRK_PROG_STAT    
				                     , D.YD_WRK_PROG_STAT_NM
				                     , D.YD_CRN_SCH_ID       
				                     , D.YD_WBOOK_ID         
				                     , D.YD_UP_CMPL_DT
				                     , D.YD_EQP_ID           
				                     , SUBSTR(D.YD_EQP_ID, 5, 6) AS YD_EQP_NM
				                     , F.YD_EQP_WRK_MODE2      -- 야드설비운전모드 -
				                     , D.YD_SCH_CD             
				                     , D.STL_CNT
				                     , D.MAIN_CHK
				                     , D.DN_WO_LOC_YD
				                     , D.YD_L2_REQUEST_STAT
				                     , D.YD_WRK_PROG_REQ_MSG AS YD_WRK_PROG_REQ_MSG
				                     , YD_SCH_ST_GP
				                     , D.YD_UP_WO_LOC_XAXIS
				                     , D.YD_UP_WO_LOC_YAXIS
				                     , D.YD_UP_WO_LOC_ZAXIS
				                     , D.YD_DN_WO_LOC_XAXIS
				                     , D.YD_DN_WO_LOC_YAXIS
				                     , D.YD_DN_WO_LOC_ZAXIS 
				                     , D.CAR_CARD_NO
				                  FROM TEMP_TABLE D 
				                     , TB_YM_SCHEDULERULE E
				                     , TB_YM_EQUIP        F
				                 WHERE D.YD_SCH_CD = E.YD_SCH_CD(+)
				                   AND D.YD_EQP_ID = F.EQUIP_GP(+)
				                   AND D.YD_WRK_PROG_STAT||D.YD_CRN_SCH_ID = (SELECT MIN(CHK)                                     
				                                                                   FROM TEMP_TABLE C
				                                                                  WHERE C.YD_WBOOK_ID=D.YD_WBOOK_ID  
				                                                                 )  
				                ORDER BY F.EQUIP_NAME, D.YD_WRK_PROG_STAT_NM DESC, D.YD_SCH_PRIOR, D.YD_CRN_SCH_ID
				                 ) A
							  WHERE ROWNUM<=1;
							
		  */	
			
			JDTORecordSet jslayerRc = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getbtCrnWrkMgtjm", logId, methodNm, "적치정보 조회"); 	

	    	if(jslayerRc == null || jslayerRc.size() <= 0){
	    		
	    		throw new Exception("크레인작업관리 스케줄조회 없음.");
    		}
	    	if(YmConstant.A7YML008.equals(sTC)){//1열연 Coil 권상실적	

				jrParam.setField("JMS_TC_CD"       , "A7YML008"); //크레인권상실적
				jrParam.setField("YD_EQP_WRK_MODE" , "9"       ); //야드설비작업Mode(Backup)
				jrParam.setField("YD_WRK_PROG_STAT", "2"       ); //야드작업진행상태(권상완료)
				jrParam.setField("YD_SCH_CD"       , commUtils.trim(jslayerRc.getRecord(0).getFieldString("YD_SCH_CD" ))); //야드스케쥴코드
				
				jrParam.setField("YD_CRN_SCH_ID"   , commUtils.trim(jslayerRc.getRecord(0).getFieldString("YD_CRN_SCH_ID" ))); //야드크레인스케쥴ID
				jrParam.setField("YD_UP_WR_LOC"    , commUtils.trim(jslayerRc.getRecord(0).getFieldString("YD_UP_WO_LOC" ))); //야드권상실적위치
				jrParam.setField("YD_UP_WR_LAYER"  , commUtils.trim(jslayerRc.getRecord(0).getFieldString("YD_UP_WO_LAYER" ))); //야드권상실적단
				jrParam.setField("YD_CRN_XAXIS"    , commUtils.trim(jslayerRc.getRecord(0).getFieldString("YD_UP_WO_LOC_XAXIS" ))); //야드크레인X축
				jrParam.setField("YD_CRN_YAXIS"    , commUtils.trim(jslayerRc.getRecord(0).getFieldString("YD_UP_WO_LOC_YAXIS" ))); //야드크레인Y축
				jrParam.setField("YD_CRN_ZAXIS"    , commUtils.trim(jslayerRc.getRecord(0).getFieldString("YD_UP_WO_LOC_ZAXIS" ))); //야드크레인Z축
				EJBConnector sndConn = new EJBConnector("default", "BCoilL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvA7YML008", new Class[] { JDTORecord.class }, new Object[] { jrParam });	
				
				
				 
				
			}else if(YmConstant.A7YML009.equals(sTC)){ //1열연 Coil 권하실적
				//권하실적처리
				jrParam.setField("JMS_TC_CD"       , "A7YML009"); //크레인권하실적
				jrParam.setField("YD_EQP_WRK_MODE" , "9"       ); //야드설비작업Mode(Backup)
				jrParam.setField("YD_WRK_PROG_STAT", "4"       ); //야드작업진행상태(권하완료)
				jrParam.setField("YD_SCH_CD"       , commUtils.trim(jslayerRc.getRecord(0).getFieldString("YD_SCH_CD"         ))); //야드스케쥴코드
				jrParam.setField("YD_CRN_SCH_ID"   , commUtils.trim(jslayerRc.getRecord(0).getFieldString("YD_CRN_SCH_ID"     ))); //야드크레인스케쥴ID
				jrParam.setField("YD_DN_WR_LOC"    , commUtils.trim(jslayerRc.getRecord(0).getFieldString("YD_DN_WO_LOC"      ))); //야드권하실적위치
				jrParam.setField("YD_DN_WR_LAYER"  , commUtils.trim(jslayerRc.getRecord(0).getFieldString("YD_DN_WO_LAYER"    ))); //야드권하실적단
				jrParam.setField("YD_CRN_XAXIS"    , commUtils.trim(jslayerRc.getRecord(0).getFieldString("YD_DN_WO_LOC_XAXIS"))); //야드크레인X축
				jrParam.setField("YD_CRN_YAXIS"    , commUtils.trim(jslayerRc.getRecord(0).getFieldString("YD_DN_WO_LOC_YAXIS"))); //야드크레인Y축
				jrParam.setField("YD_CRN_ZAXIS"    , commUtils.trim(jslayerRc.getRecord(0).getFieldString("YD_DN_WO_LOC_ZAXIS"))); //야드크레인Z축
				EJBConnector sndConn = new EJBConnector("default", "BCoilL2RcvSeEJB", this);
				JDTORecord rst = (JDTORecord)sndConn.trx("rcvA7YML009", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				jrRtn = commUtils.addSndData(jrRtn, rst);
			}

			commUtils.printLog(logId, methodNm, "S-");
	    	return jrRtn;
	    	
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	    	throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }
	}	
	
	
	/**
	 *      [A] 오퍼레이션명 : 대차작업현황조회- 우선순위변경
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord modPriorChange(GridData gdReq) throws DAOException {
		String methodNm = "대차작업현황조회 순위변경[BCoilJspSeEJB.modPriorChange] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			String sYD_SCH_PRIOR     = ""; //야드스케쥴우선순위
			String sYD_SCH_CD        = "";//
			String sYD_WRK_PLAN_TCAR = "";//
			String sYD_BAY_GP        = "";//
			String sYD_AIM_BAY_GP    = "";//    
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			//처리완료한 야드작업예약ID
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				/**********************************************************
				* 1. 작업예약ID Check
				**********************************************************/
				sYD_SCH_PRIOR     = commUtils.trim(gdReq.getHeader("YD_SCH_PRIOR"    ).getValue(ii)); //야드스케쥴우선순위
			    sYD_SCH_CD        = commUtils.trim(gdReq.getHeader("YD_SCH_CD"       ).getValue(ii)); //스케줄코드
			    sYD_WRK_PLAN_TCAR = commUtils.trim(gdReq.getHeader("YD_WRK_PLAN_TCAR").getValue(ii)); //대차
			    sYD_BAY_GP        = commUtils.trim(gdReq.getHeader("LD_BAY"          ).getValue(ii)); //상차동
			    sYD_AIM_BAY_GP    = commUtils.trim(gdReq.getHeader("UD_BAY"          ).getValue(ii)); //하차동
			    
				jrParam.setField("YD_SCH_PRIOR"    , sYD_SCH_PRIOR    );			    
				jrParam.setField("YD_SCH_CD"       , sYD_SCH_CD       );
				jrParam.setField("YD_WRK_PLAN_TCAR", sYD_WRK_PLAN_TCAR);
				jrParam.setField("YD_BAY_GP"       , sYD_BAY_GP       );
				jrParam.setField("YD_AIM_BAY_GP"   , sYD_AIM_BAY_GP   );
			    
				commUtils.printLog(logId, "우선순위변경 [ " + sYD_SCH_CD + " >> " + sYD_SCH_PRIOR + " ]", "SL");

				/**********************************************************
				* 2. 작업예약 및 크레인스케줄 Table에  우선순위를 Update
				**********************************************************/
				/*
				--작업예약 스케쥴우선순위 수정
				UPDATE TB_YM_WRKBOOK
				   SET MODIFIER     = :V_MODIFIER
				     , MOD_DDTT     = SYSDATE
				     , YD_SCH_PRIOR = NVL(TO_NUMBER(:V_YD_SCH_PRIOR),1)
				 WHERE 1 = 1
				   AND DEL_YN       = 'N'
				   AND YD_WBOOK_ID  IN(
				                        SELECT YD_WBOOK_ID
				                          FROM TB_YM_WRKBOOK 
				                         WHERE DEL_YN           = 'N'
				                           AND YD_SCH_CD        = :V_YD_SCH_CD
				                           AND YD_WRK_PLAN_TCAR = :V_YD_WRK_PLAN_TCAR
				                           AND YD_BAY_GP        = :V_YD_BAY_GP     --상차동
				                           AND YD_AIM_BAY_GP    = :V_YD_AIM_BAY_GP --하차동      
				                       )
				 */
				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updSchPriorModWrkBook", logId, methodNm, "TB_YM_WRKBOOK");				
				
				/*
				--크레인스케줄 우선순위 수정
				UPDATE TB_YM_CRNSCH
				   SET MODIFIER     = :V_MODIFIER
				     , MOD_DDTT     = SYSDATE
				     , YD_SCH_PRIOR = NVL(TO_NUMBER(:V_YD_SCH_PRIOR),1)
				 WHERE 1 = 1
				   AND DEL_YN       = 'N'
				   AND YD_WBOOK_ID  IN(
				                        SELECT YD_WBOOK_ID
				                          FROM TB_YM_WRKBOOK 
				                         WHERE DEL_YN           = 'N'
				                           AND YD_SCH_CD        = :V_YD_SCH_CD
				                           AND YD_WRK_PLAN_TCAR = :V_YD_WRK_PLAN_TCAR
				                           AND YD_BAY_GP        = :V_YD_BAY_GP     --상차동
				                           AND YD_AIM_BAY_GP    = :V_YD_AIM_BAY_GP --하차동      
				                       )
				 */
				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updSchPriorModCrnSch", logId, methodNm,  "TB_YM_CRNSCH");				
				
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
	 *      [A] 오퍼레이션명 : 대차작업현황조회-작업예약삭제
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord delWrkBookDel(GridData gdReq) throws DAOException {
		String methodNm = "대차작업현황조회-작업예약삭제[BCoilJspSeEJB.delWrkBookDel] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			String sYD_WBOOK_ID      = ""; //야드작업예약ID
			String sYD_SCH_CD        = "";//
			String sYD_WRK_PLAN_TCAR = "";//
			String sYD_BAY_GP        = "";//
			String sYD_AIM_BAY_GP    = "";//
			
			
			//처리완료한 야드작업예약ID
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				//DAO Parameter - Log ID, Method, 수정자 Set
				JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
				
				sYD_SCH_CD        = commUtils.trim(gdReq.getHeader("YD_SCH_CD"       ).getValue(ii)); //스케줄코드
			    sYD_WRK_PLAN_TCAR = commUtils.trim(gdReq.getHeader("YD_WRK_PLAN_TCAR").getValue(ii)); //대차
			    sYD_BAY_GP        = commUtils.trim(gdReq.getHeader("LD_BAY"          ).getValue(ii)); //상차동
			    sYD_AIM_BAY_GP    = commUtils.trim(gdReq.getHeader("UD_BAY"          ).getValue(ii)); //하차동
		    
				jrParam.setField("YD_SCH_CD"       , sYD_SCH_CD       );
				jrParam.setField("YD_WRK_PLAN_TCAR", sYD_WRK_PLAN_TCAR);
				jrParam.setField("YD_BAY_GP"       , sYD_BAY_GP       );
				jrParam.setField("YD_AIM_BAY_GP"   , sYD_AIM_BAY_GP   );
				
				/*
				--작업예약 삭제 목록
				SELECT YD_WBOOK_ID
				  FROM TB_YM_WRKBOOK 
				 WHERE DEL_YN           = 'N'
				   AND YD_SCH_CD        = :V_YD_SCH_CD
				   AND YD_WRK_PLAN_TCAR = :V_YD_WRK_PLAN_TCAR
				   AND YD_BAY_GP        = :V_YD_BAY_GP     --상차동
				   AND YD_AIM_BAY_GP    = :V_YD_AIM_BAY_GP --하차동    
				 */
				JDTORecordSet rst = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getDelWrkBookList");
				
				if (rst.size() == 0) {
					return jrRtn;
				}
				
				for (int jj = 0; jj < rst.size(); ++jj) {
					sYD_WBOOK_ID = rst.getRecord(jj).getFieldString("YD_WBOOK_ID");
					jrParam.setField("YD_WBOOK_ID"  , sYD_WBOOK_ID );
					
					/**********************************************************
					* 작업예약 취소
					**********************************************************/
					jrRtn = commUtils.addSndData(jrRtn, this.trtWrkBookCncl(jrParam));
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
	 * 야드설비정비등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord mkUdCarSch(GridData gdReq) throws DAOException {
		String methodNm = "하차백업생성[BCoilJspFaEJB.mkUdCarSch] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			jrParam.setField("YD_CAR_SCH_ID"		, commDao.getSeqId(logId, methodNm, "CarSch")); //야드차량스케쥴ID
			jrParam.setField("YD_CAR_PROG_STAT"		, "5"							); //차량진행상태 (5:상차완료)
			jrParam.setField("YD_CAR_USE_GP"		, "L"							); //야드차량사용구분 (L:구내운송, G:출하차량 )
			jrParam.setField("YD_EQP_WRK_STAT"		, "L"							); //야드설비작업상태 (L:영차, U:공차)
			jrParam.setField("SPOS_WLOC_CD"			, gdReq.getParam("SPOS_WLOC_CD")); //발지개소코드(상차지)
			jrParam.setField("ARR_WLOC_CD"			, gdReq.getParam("ARR_WLOC_CD")	); //착지개소코드(하차지)
			jrParam.setField("YD_PNT_CD"			, ""							); //야드상차포인트코드(발지)
			jrParam.setField("YD_CARLD_WRK_BOOK_ID"	, ""							); //야드상차작업예약ID
			jrParam.setField("YD_CARLD_STOP_LOC"	, ""							); //야드하차정지위치
			jrParam.setField("TRN_EQP_CD"			, gdReq.getParam("TRN_EQP_CD")	); //운송장비코드
			jrParam.setField("CAR_KIND"				, gdReq.getParam("TRN_EQP_CD").substring(1, 3)		); //TR,PT 구분
			
			commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insCarSchLd", logId, methodNm, "차량스케쥴 상차출발(5)로 INSERT ");
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of mkUdCarSch	
	
	
	/**
	 *      [A] 오퍼레이션명 : 대차작업현황조회 - 최대적치매수 수정
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updStackMaxQnty(GridData gdReq) throws DAOException {
		String methodNm = "대차작업현황조회 - 최대적치매수 수정[BCoilJspSeEJB.updStackMaxQnty] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			String sEQUIP_GP       = ""; //야드설비ID
			String sSTACK_MAX_QNTY = "";
			String sEQP_DIR_TO_LOC = "";

			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				//DAO Parameter - Log ID, Method, 수정자 Set
				JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
				
				sEQUIP_GP       = commUtils.trim(gdReq.getHeader("EQUIP_GP").getValue(ii));
				sSTACK_MAX_QNTY = commUtils.trim(gdReq.getHeader("STACK_MAX_QNTY").getValue(ii));
				sEQP_DIR_TO_LOC = commUtils.trim(gdReq.getHeader("EQP_DIR_TO_LOC").getValue(ii));

				jrParam.setField("EQUIP_GP"       , sEQUIP_GP      );
				jrParam.setField("STACK_MAX_QNTY" , sSTACK_MAX_QNTY);
				jrParam.setField("EQP_DIR_TO_LOC" , sEQP_DIR_TO_LOC);
				
				/*
				UPDATE TB_YM_EQUIP
				   SET STACK_MAX_QNTY = :V_STACK_MAX_QNTY
				     , EQP_DIR_TO_LOC = :V_EQP_DIR_TO_LOC
				     , MODIFIER       = :V_MODIFIER
				     , MOD_DDTT       = SYSDATE
				 WHERE EQUIP_GP = :V_EQUIP_GP
				 */
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStackMaxQnty");
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
	 *      [A] 오퍼레이션명 : 대차작업현황조회 - HFL직보급 여부 수정
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updDirYn(GridData gdReq) throws DAOException {
		String methodNm = "대차작업현황조회 - HFL직보급 수정[BCoilJspSeEJB.updDirYn] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			String sDIR_YN  = commUtils.trim(gdReq.getParam("DIR_YN"));
			
			
				//DAO Parameter - Log ID, Method, 수정자 Set
				JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
				jrParam.setField("DIR_YN", sDIR_YN);
				
				/*
				UPDATE USRYMA.TB_YM_RULE 
				   SET DTL_ITM1 = :V_DIR_YN
				     , MODIFIER = :V_MODIFIER
				     , MOD_DDTT = SYSDATE
				 WHERE REPR_CD_GP = 'APP007' 
				 */
				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updDirYn");
			

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}	
	
	
	/**
	 *      [A] 오퍼레이션명 : 코일공통상세조회-정정검사메시지 수정
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updShearInspectMsg(GridData gdReq) throws DAOException {
		String methodNm = "코일공통상세조회-정정검사메시지 수정[BCoilJspSeEJB.updShearInspectMsg] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			String sCOIL_NO           = commUtils.trim(gdReq.getParam("COIL_NO"));
			String sSHEAR_INSPECT_MSG = commUtils.trim(gdReq.getParam("SHEAR_INSPECT_MSG"));
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			jrParam.setField("COIL_NO"          , sCOIL_NO);
			jrParam.setField("SHEAR_INSPECT_MSG", sSHEAR_INSPECT_MSG);
			jrParam.setField("MSG_CONTENTS"     , sSHEAR_INSPECT_MSG);
			jrParam.setField("REGISTER"         , commUtils.trim(gdReq.getParam("userid")));
			
			/*
			INSERT INTO TB_HR_C_SHEARWOWR_MSG_LOG
			SELECT CC.COIL_NO                                 
			      ,SYSDATE                                 
			      ,NVL(SR.STEP_NO, 1)
			      ,NVL(SR.HR_PLNT_GP, CC.HR_PLNT_GP)
			      ,'J' --:V_SHEAR_WRK_MSG_GP    정정작업MESSGAE구분  'J' 정정
			      ,:V_MSG_CONTENTS                         
			      ,:V_REGISTER                             
			  FROM USRPTA.TB_PT_COILCOMM CC
			     , USRHRA.TB_HR_C_SHEARWOWR SR
			 WHERE CC.COIL_NO = SR.COIL_NO(+)
			   AND CC.COIL_NO = :V_COIL_NO
			   AND NVL(SR.STEP_NO, 0) = (SELECT NVL(MAX(X.STEP_NO),0)
			                              FROM TB_HR_C_SHEARWOWR X
			                             WHERE X.COIL_NO = :V_COIL_NO)   
			 */
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insHrShrMsgLog", logId, methodNm, "");
			

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}	//updShearInspectMsg
	
	
	/**
	 *      [A] 오퍼레이션명 : 스크랩현황조회- 스크랩비우기
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord procClearScrap(GridData gdReq) throws DAOException {
		String methodNm = "스크랩현황조회-스크랩비우기[BCoilJspSeEJB.procClearScrap] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			String sCOL_GP		   = commUtils.trim(gdReq.getParam("COL_GP"		   )); //삭제 동구분
			String sSCRAP_CLEAR_GP = commUtils.trim(gdReq.getParam("SCRAP_CLEAR_GP")); //삭제할 열 %: 전체삭제
			String sCOL_NO         = commUtils.trim(gdReq.getParam("COL_NO"        )); //삭제할 열 %: 전체삭제
			String sAREA_GP        = commUtils.trim(gdReq.getParam("AREA_GP"       )); //삭제할 지역
			String sARR_STOCK_ID   = commUtils.trim(gdReq.getParam("ARR_STOCK_ID"  )); //재료번호들
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setField("ARR_STOCK_ID" , sARR_STOCK_ID); //재료번호들
			jrParam.setField("COL_NO"       , sCOL_NO      ); //열
			jrParam.setField("AREA_GP"      , sAREA_GP     ); //지역구분
			jrParam.setField("COL_GP"       , sCOL_GP      ); //동구분
			
			/*****************************************************
			 * 저장품제원 : 코일야드L2로 송신(YMA7L002)
			 ******************************************************/
			commUtils.printLog(logId, "YMA7L002 JMS전송", "[INFO]");
			
			JDTORecordSet arrStock = null;
			if ("COL".equals(sSCRAP_CLEAR_GP)) {
				jrParam.setField("COL_GP"       , sCOL_GP+sCOL_NO      ); //동구분
				jrParam.setField("STACK_BED"       , ""      ); //bed구분
				/*
				WITH DATA_TEMP AS
				(
				SELECT CD_GP||ITEM||B.STACK_LAYER_GP AS COL_BED_LAY_GP
				  FROM USRYMA.TB_YM_RULE A, 
				       USRYMA.TB_YM_STACKLAYER B
				 WHERE A.CD_GP||A.ITEM = B.STACK_COL_GP||B.STACK_BED_GP
				   AND REPR_CD_GP = 'SCRAP'
				   AND CD_GP = :V_COL_GP
				   AND DTL_ITM1 = :V_AREA_GP
				   AND :V_AREA_GP IN ('A','B')
				 UNION ALL
				 --B구역일경우 전BED 2단 스크랩 삭제
				SELECT MIN(CD_GP)||LPAD(TO_NUMBER(MIN(ITEM))-1,2,0)||'02'  AS COL_BED_LAY_GP
				  FROM USRYMA.TB_YM_RULE 
				 WHERE REPR_CD_GP = 'SCRAP'
				   AND CD_GP = :V_COL_GP
				   AND DTL_ITM1 LIKE :V_AREA_GP
				   AND :V_AREA_GP IN ('B')
				 UNION ALL
				SELECT STACK_COL_GP||STACK_BED_GP||STACK_LAYER_GP  AS COL_BED_LAY_GP
				  FROM TB_YM_STACKLAYER
				 WHERE STACK_COL_GP = :V_COL_GP
				   AND STACK_BED_GP = :V_STACK_BED
				   AND :V_AREA_GP NOT IN ('A','B')
				 UNION ALL
				 --BED별 삭제일경우 전BED 2단 스크랩 삭제
				SELECT STACK_COL_GP||LPAD(TO_NUMBER(STACK_BED_GP)-1,2,0)||'02'  AS COL_BED_LAY_GP
				  FROM TB_YM_STACKLAYER
				 WHERE STACK_COL_GP = :V_COL_GP
				   AND STACK_BED_GP = :V_STACK_BED
				   AND STACK_LAYER_GP = '02'
				   AND :V_AREA_GP NOT IN ('A','B')
				)
				
				SELECT STOCK_ID 
				  FROM TB_YM_STACKLAYER A
				      ,DATA_TEMP B
				WHERE STACK_LAYER_STAT IN ('C')
				  AND STACK_COL_GP||STACK_BED_GP||STACK_LAYER_GP = B.COL_BED_LAY_GP
				 */
				arrStock = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getScrapStockByCol", logId, methodNm, "");
			} else {
				//작업예약과 스케줄대상재료는 제외하고 삭제 처리 진행
				/*
					WITH DATA_TBL AS (
					  SELECT REGEXP_SUBSTR(SSTL_NOS, '[^,]+', 1, LEVEL) AS STOCK_ID
					    FROM (SELECT :V_ARR_STOCK_ID AS SSTL_NOS FROM DUAL)
					 CONNECT BY REGEXP_SUBSTR(SSTL_NOS, '[^,]+', 1, LEVEL) IS NOT NULL
					)
					SELECT STOCK_ID FROM DATA_TBL
					WHERE STOCK_ID NOT IN (
					   SELECT B.STOCK_ID 
					     FROM TB_YM_WRKBOOK A,
					          TB_YM_WRKBOOKMTL B,
					          DATA_TBL C
					    WHERE A.YD_WBOOK_ID = B.YD_WBOOK_ID
					      AND B.STOCK_ID = C.STOCK_ID 
					      AND A.DEL_YN = 'N'
					      AND B.DEL_YN = 'N'
					    UNION ALL
					   SELECT B.STOCK_ID 
					     FROM TB_YM_CRNSCH A, 
					          TB_YM_CRNWRKMTL B,
					          DATA_TBL C
					    WHERE A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID 
					      AND B.STOCK_ID = C.STOCK_ID 
					      AND A.DEL_YN = 'N'
					      AND B.DEL_YN = 'N'
					)
				 */
				arrStock = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getStockListByArrList", logId, methodNm, "");
			}
			
			for (int idx = 0; idx < arrStock.size(); ++idx) {
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setField("TC_CD"          , "YMA7L002");
				jrYdMsg.setField("MSG_GP"         , "D"); 
				jrYdMsg.setField("YD_INFO_SYNC_CD", "5");
				jrYdMsg.setField("STOCK_ID"       , arrStock.getRecord(idx).getFieldString("STOCK_ID"));

				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA7L002_SCRAP", jrYdMsg));			
			}
			
			/***************************
			 * 스크랩 비우기
			 ***************************/
			// 열단위 스크랩 삭제
			if ("COL".equals(sSCRAP_CLEAR_GP)) {
				
				
				for (int idx = 0; idx < arrStock.size(); ++idx) {
					JDTORecord jrScrList = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
					jrScrList.setField("COL_GP"       , sCOL_GP+sCOL_NO      ); //동구분
					jrScrList.setField("STOCK_ID"   , arrStock.getRecord(idx).getFieldString("STOCK_ID"));	
					
					/*
					UPDATE TB_YM_STOCK
					   SET DEL_YN   = 'Y'
					     , MODIFIER = :V_MODIFIER
					     , MOD_DDTT = SYSDATE
					 WHERE STOCK_ID = :V_STOCK_ID
					 */
					commDao.update(jrScrList, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updClearScrapStockByCol", logId, methodNm, "");
					
					/*
						UPDATE TB_YM_STACKLAYER
						   SET STOCK_ID         = NULL
						     , STACK_LAYER_STAT = 'E'
						     , MODIFIER         = :V_MODIFIER
						     , MOD_DDTT         = SYSDATE
						 WHERE STACK_COL_GP = :V_COL_GP
						   AND STOCK_ID = :V_STOCK_ID
						   AND STACK_LAYER_STAT = 'C'
					 */
					commDao.update(jrScrList, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updClearScrapLyrByCol", logId, methodNm, "");
				}
				
			}
			
			
			// 코일 단위 스크랩 삭제
			if ("STOCK".equals(sSCRAP_CLEAR_GP)) {
				
				
				for (int idx = 0; idx < arrStock.size(); ++idx) {
					
					JDTORecord jrScrList = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
					jrScrList.setField("STOCK_ID"       , arrStock.getRecord(idx).getFieldString("STOCK_ID"));		
					jrScrList.setField("COL_GP"       	, sCOL_GP);
					
					
					/*
					--com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updClearScrapLyrByArrStockId                     
					UPDATE TB_YM_STACKLAYER
					   SET STOCK_ID         = NULL
					     , STACK_LAYER_STAT = 'E'
					     , MODIFIER         = :V_MODIFIER
					     , MOD_DDTT         = SYSDATE
					 WHERE STACK_COL_GP LIKE :V_COL_GP||'%'
					   AND STOCK_ID = :V_STOCK_ID
					 */
					commDao.update(jrScrList, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updClearScrapLyrByArrStockId", logId, methodNm, "");
					
					/*
					--com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updClearScrapStockByArrStockId                    
					UPDATE TB_YM_STOCK
					   SET DEL_YN   = 'Y'
					     , MODIFIER = :V_MODIFIER
					     , MOD_DDTT = SYSDATE
					 WHERE STOCK_ID = :V_STOCK_ID
					 */
					commDao.update(jrScrList, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updClearScrapStockByArrStockId", logId, methodNm, "");
					
				}
				
				
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}	//procClearScrap
	/**
	 *      [A] 오퍼레이션명 : 스크랩현황조회- 스크랩생성
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord procCreateScrap(GridData gdReq) throws DAOException {
		String methodNm = "스크랩현황조회-스크랩생성[BCoilJspSeEJB.procCreateScrap] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			String sARR_STOCK_ID   = commUtils.trim(gdReq.getParam("ARR_STOCK_ID"  )); //재료번호들
			String sCOL_GP		   = commUtils.trim(gdReq.getParam("COL_GP"		   )); //동구분
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setField("ARR_STOCK_ID" , sARR_STOCK_ID); //재료번호들
			jrParam.setField("COL_GP" 		, sCOL_GP); //동구분
			
			/***************************
			 * 스크랩 생성
			 ***************************/
			/* 
			UPDATE TB_YM_STACKLAYER
			   SET STOCK_ID         = ('SC'||TO_CHAR(SYSDATE,'YYMMDD')||LPAD(TO_NUMBER(NVL((SELECT SUBSTR(MAX(STOCK_ID),9) FROM TB_YM_STOCK WHERE STOCK_ID LIKE 'SC'||TO_CHAR(SYSDATE,'YYMMDD')||'%'),0))+ROWNUM,3,0))
			     , STACK_LAYER_STAT = 'C'
			     , MODIFIER         = :V_MODIFIER
			     , MOD_DDTT         = SYSDATE
			 WHERE STACK_COL_GP LIKE :V_COL_GP||'%'
			   AND STACK_COL_GP||STACK_BED_GP||STACK_LAYER_GP IN (SELECT REGEXP_SUBSTR(SSTL_NOS, '[^,]+', 1, LEVEL) AS STOCK_ID
						                                            FROM (SELECT :V_ARR_STOCK_ID AS SSTL_NOS FROM DUAL)
						                                          CONNECT BY REGEXP_SUBSTR(SSTL_NOS, '[^,]+', 1, LEVEL) IS NOT NULL)
			   AND STACK_LAYER_STAT = 'E'   
			 */
			commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.insCrScrLyrByLoc", logId, methodNm, "TB_YM_STACKLAYER 스크랩 생성");
			
			/*
			SELECT (SELECT STOCK_ID FROM TB_YM_STACKLAYER WHERE STACK_COL_GP||STACK_BED_GP||STACK_LAYER_GP = A.STOCK_ID) AS STOCK_ID
			  FROM (SELECT REGEXP_SUBSTR(SSTL_NOS, '[^,]+', 1, LEVEL) AS STOCK_ID
			          FROM (SELECT :V_ARR_STOCK_ID AS SSTL_NOS FROM DUAL)
			        CONNECT BY REGEXP_SUBSTR(SSTL_NOS, '[^,]+', 1, LEVEL) IS NOT NULL) A
			 WHERE LENGTH(STOCK_ID) = 10
			 */
			JDTORecordSet jsCrList = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getCrScrList", logId, methodNm, "생성스크랩 목록 조회");
			
			for (int ii = 0; ii < jsCrList.size(); ++ii) {
				/*
				MERGE INTO TB_YM_STOCK ST USING (
				    SELECT :V_STOCK_ID      AS STOCK_ID    --재료번호
				         , :V_MODIFIER      AS MODIFIER    --수정자
				         , SYSDATE          AS MOD_DDTT    --수정일시
				         , 'N'              AS DEL_YN      --삭제유무
				      FROM DUAL
				) DD ON ( ST.STOCK_ID = DD.STOCK_ID)
				WHEN NOT MATCHED THEN
				    INSERT (
				           STOCK_ID     , STOCK_ITEM    , STOCK_MOVE_TERM  --저장품이동조건
				         , REGISTER     , REG_DDTT      , MODIFIER         -- 'SYSTEM'
				         , MOD_DDTT     , DEL_YN )
				    VALUES (:V_STOCK_ID , ''            , ''
				         , DD.MOD_DDTT  , DD.MOD_DDTT   , DD.MOD_DDTT
				         , DD.MOD_DDTT  , DD.DEL_YN 	) 
				WHEN MATCHED THEN UPDATE SET
				     ST.STOCK_ITEM      = ''
				   , ST.STOCK_MOVE_TERM = ''
				   , ST.MODIFIER        = DD.MODIFIER
				   , ST.MOD_DDTT        = DD.MOD_DDTT
				   , ST.DEL_YN          = DD.DEL_YN 						
				 */
				jrParam.setField("STOCK_ID"  , jsCrList.getRecord(ii).getFieldString("STOCK_ID"));
				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.insertStockTransInfo", logId, methodNm, "TB_YM_STOCK 스크랩 생성");
			}
			
			/*****************************************************
			 * 저장품제원 : 코일야드L2로 송신(YMA7L002)
			 ******************************************************/
			commUtils.printLog(logId, "YMA7L002 JMS전송", "[INFO]");
			
			for (int idx = 0; idx < jsCrList.size(); ++idx) {
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setField("TC_CD"          , "YMA7L002");
				jrYdMsg.setField("MSG_GP"         , "I"); 
				jrYdMsg.setField("YD_INFO_SYNC_CD", "5");
				jrYdMsg.setField("STOCK_ID"       , jsCrList.getRecord(idx).getFieldString("STOCK_ID"));

				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA7L002_SCRAP", jrYdMsg));			
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}	//procCreateScrap		
	
	
	/**
	 * GridData -  크레인 작업지시(스케쥴) 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public GridData getSchAvdWoList(GridData gdReq) throws DAOException {
		String methodNm = "조회[BCoilJspSeEJB.getSchAvdWoList] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		String sQueryId		= "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getWrkWaitCrnSch";		// 크레인 별 작업 지시 목록 조회
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			
			/***************************************************** 크레인 작업지시(스케쥴) 목록 조회 ****************************************************/
			//Grid 파라미터를 JDTORecord data 로 변환
			JDTORecord jrParam = CmUtil.genJDTORecord(gdReq);
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			
			commDao.jspSelect(jrParam, outRecSet, sQueryId, logId, methodNm);		// 크레인 별 작업 지시 목록 조회
			commUtils.printLog(logId, "outRecSet cnt : " + outRecSet.size(), "SL");
			
			
			// 조회된 작업 지시별로 고도화 기준 체크
			if (outRecSet.size() > 0) {
				
				jrParam.setField("logId", logId);
				
				outRecSet = this.procCoilCrnSchChoiceYnNew(outRecSet, jrParam);
				
			}
			
			
			//UI로 반환 할 Grid data 를 생성 
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			GridData gdRet = commUtils.jdtoRecordToGridData(gdRtn, outRecSet.toList(), gdReq);
			
			commUtils.printLog(logId, methodNm, "S-", gdReq);
			
			gdRet.addParam("ADV_RESULT", "ok");	// 이 값으로 화면에서 상태판단
			gdRet.setStatus("true");
			
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of getSchAvdWoList
	
	
	/**
	 * 1열연 COIL 고도화 모니터링 화면 냉각코일자동이적, 더미 자동이적 편성 기능 추가
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updYdAdvancemtRule(GridData gdReq) throws DAOException {
		String methodNm = "냉각코일자동이적/더미 자동이적 편성[BCoilJspFaEJB.updYdAdvancemtRule] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
			
			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			jrParam.setField("DTL_ITM1"		, gdReq.getParam("DTL_ITM1")); 
			jrParam.setField("REPR_CD_GP"	, gdReq.getParam("REPR_CD_GP")); 
			jrParam.setField("MODIFIER"		, gdReq.getParam("MODIFIER")); 
			jrParam.setField("BAY_GP"		, gdReq.getParam("BAY_GP"));
			
			commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updColCoilPickUp", logId, methodNm, "냉각코일자동이적/더미 자동이적 편성 체크 여부");
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updYdAdvancemtRule
	
	
	/**
	 * 스크랩 차량 진입여부 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updScrpCar(GridData gdReq) throws DAOException {
		String methodNm = "스크랩 차량 진입여부 변경[BCoilJspFaEJB.updScrpCar] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			JDTORecord jrRtn = null;
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			jrParam.setField("HMI_STAT"		, gdReq.getParam("HMI_STAT")); 
			jrParam.setField("MODIFIER"		, gdReq.getParam("MODIFIER")); 
			jrParam.setField("EQUIP_GP"		, gdReq.getParam("EQUIP_GP")); 
			
			commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updScrpCarEntYn", logId, methodNm, "스크랩 차량 진입여부 변경");
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updScrpCar
	
	
	/**
	 *      [A] 오퍼레이션명 : YM-RULE 수정
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord modiYmRule(GridData gdReq) throws DAOException {
		String methodNm = "YM-RULE 수정[BCoilJspSeEJB.modiYmRule] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setField("DTL_ITM1" , commUtils.trim(gdReq.getParam("DTL_ITM1" )));
			jrParam.setField("DTL_ITM2" , commUtils.trim(gdReq.getParam("DTL_ITM2" )));
			jrParam.setField("DTL_ITM3" , commUtils.trim(gdReq.getParam("DTL_ITM3" )));
			jrParam.setField("DTL_ITM4" , commUtils.trim(gdReq.getParam("DTL_ITM4" )));
			jrParam.setField("DTL_ITM5" , commUtils.trim(gdReq.getParam("DTL_ITM5" )));
			jrParam.setField("DTL_ITM6" , commUtils.trim(gdReq.getParam("DTL_ITM6" )));
			jrParam.setField("DTL_ITM7" , commUtils.trim(gdReq.getParam("DTL_ITM7" )));
			jrParam.setField("DTL_ITM8" , commUtils.trim(gdReq.getParam("DTL_ITM8" )));
			jrParam.setField("DTL_ITM9" , commUtils.trim(gdReq.getParam("DTL_ITM9" )));
			jrParam.setField("DTL_ITM10", commUtils.trim(gdReq.getParam("DTL_ITM10")));
			
			jrParam.setField("REPR_CD_GP", commUtils.trim(gdReq.getParam("REPR_CD_GP")));
			jrParam.setField("CD_GP"     , commUtils.trim(gdReq.getParam("CD_GP")));
			jrParam.setField("ITEM"      , commUtils.trim(gdReq.getParam("ITEM")));
			
			/*
			UPDATE TB_YM_RULE
			   SET DTL_ITM1 = NVL(:V_DTL_ITM1, DTL_ITM1)
			      ,DTL_ITM2 = NVL(:V_DTL_ITM2, DTL_ITM2)
			      ,DTL_ITM3 = NVL(:V_DTL_ITM3, DTL_ITM3)
			      ,DTL_ITM4 = NVL(:V_DTL_ITM4, DTL_ITM4)
			      ,DTL_ITM5 = NVL(:V_DTL_ITM5, DTL_ITM5)
			      ,DTL_ITM6 = NVL(:V_DTL_ITM6, DTL_ITM6)
			      ,DTL_ITM7 = NVL(:V_DTL_ITM7, DTL_ITM7)
			      ,DTL_ITM8 = NVL(:V_DTL_ITM8, DTL_ITM8)
			      ,DTL_ITM9 = NVL(:V_DTL_ITM9, DTL_ITM9)
			      ,DTL_ITM10 = NVL(:V_DTL_ITM10, DTL_ITM10)
			      ,MODIFIER = :V_MODIFIER
			      ,MOD_DDTT = SYSDATE
			 WHERE REPR_CD_GP = :V_REPR_CD_GP
			   AND CD_GP = :V_CD_GP
			   AND ITEM = :V_ITEM
			 */
			commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updYmRule", logId, methodNm, "YM_RULE수정");
			

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}		
	
	/**
	 * 신고도화 크레인 스케쥴 결정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecordSet jsRtnWrkWaitCrn
	 * @param JDTORecord jrParam
	 * @return JDTORecordSet
	 * @throws JDTOException
	 */
    public JDTORecordSet procCoilCrnSchChoiceYnNew(JDTORecordSet jsRtnWrkWaitCrn, JDTORecord jrParam) throws JDTOException {
    	String logId	= jrParam.getFieldString("logId");
    	String ydEqpId	= "";
    	
    	String methodNm = "신고도화 크레인 스케쥴 결정여부[BCoilJspSeEJB.procCoilCrnSchChoiceYn] < ";
    	JDTORecordSet jsAbleResult = JDTORecordFactory.getInstance().createRecordSet("");

    	String rtnYdCrnSchId	= "";
    	String rtnYdSchCd		= "";
    	String szDBLogMsg 		= "";
    	String szLogMsg         = "";
		long   lngSchUpXaxis   = 0;
		long   lngSchDnXaxis   = 0;
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//조회 및 등록용
			
			commUtils.printLog(logId, "step 1 >> " + " 고도화 기준 조회 START ******* ", "SL");
			
			String sAvdYn = "N";	// 고도화 가능여부
			if(jsRtnWrkWaitCrn.size()>0 ) {
				ydEqpId = jsRtnWrkWaitCrn.getRecord(0).getFieldString("P_YD_EQP_ID");	// 크레인 ID
				
				commUtils.printLog(logId, "ydEqpId : " + ydEqpId, "SL");
				
				////신고도화 Final 단계 START
				String sAPP300_A_YN = ymComm.BCoilApplyYn("APP300","3","A");  
				String sAPP300_C_YN = ymComm.BCoilApplyYn("APP300","3","C");  
				String sAPP300_E_YN = ymComm.BCoilApplyYn("APP300","3","E");  
				
				commUtils.printLog(logId,  "스케쥴고도화Final단계  A동:" + sAPP300_A_YN + "  C동:" + sAPP300_C_YN+ "  E동:" + sAPP300_E_YN, "SL");

				if( sAPP300_A_YN.equals("Y") && ("A1".equals(ydEqpId.substring(4,6))||"A2".equals(ydEqpId.substring(4,6)))){
					commUtils.printLog(logId,  "스케쥴고도화Final단계  A동:" + sAPP300_A_YN, "SL");
					jsRtnWrkWaitCrn = this.coilCrnSchChoiceAutoNewA(jsRtnWrkWaitCrn, logId, ydEqpId);
					return jsRtnWrkWaitCrn;
				}
				if( sAPP300_C_YN.equals("Y") && "C".equals(ydEqpId.substring(1,2))){
					commUtils.printLog(logId,  "스케쥴고도화Final단계  C동:" + sAPP300_C_YN, "SL");
					jsRtnWrkWaitCrn = this.coilCrnSchChoiceAutoNewF(jsRtnWrkWaitCrn, logId, ydEqpId);
					return jsRtnWrkWaitCrn;
				}
				if( sAPP300_E_YN.equals("Y") && "E".equals(ydEqpId.substring(1,2))){
					commUtils.printLog(logId,  "스케쥴고도화Final단계  E동:" + sAPP300_E_YN, "SL");
					jsRtnWrkWaitCrn = this.coilCrnSchChoiceAutoNewF(jsRtnWrkWaitCrn, logId, ydEqpId);
					return jsRtnWrkWaitCrn;
				}
				////신고도화Final단계 END
			}
			
			jrParam.setField("YD_EQP_ID", ydEqpId);
			
			// Lv2 크레인 상태 조회
			JDTORecordSet jsL2Crn = commDao.selectL2(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getL2CrnInfoNew", logId, methodNm, "크레인 L2 이동상태 조회");
			commUtils.printLog(logId, "jsL2Crn cnt : " + jsL2Crn.size(), "SL");
			
			JDTORecord jrL2Crn = JDTORecordFactory.getInstance().create();	
			
			if (jsL2Crn.size() <= 0) {	// L2 현재위치가 없으면 L3 현재 위치 데이터로 대체
				
				commUtils.printLog(logId, "db error 고도화 불가 : L2 현재위치 없음 " + " [ " + ydEqpId  + " ]", "SL");
				commUtils.printLog(logId, methodNm, "S-");
				
				jsL2Crn = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnInfoNew", logId, methodNm, "크레인 L3 이동상태 조회");
				
				
				if (jsL2Crn.size() <= 0) {
					commUtils.printLog(logId, "db error 고도화 불가 : L2, L3 현재위치 없음 " + " [ " + ydEqpId + " ]", "SL");
					commUtils.printLog(logId, methodNm, "S-");
					sAvdYn = "N";
					szLogMsg = "실패: db error 고도화 불가 : L2, L3 현재위치 없음 " + " [ " + ydEqpId + " ]";
					commUtils.printLog(logId, szLogMsg , "SL");
					szDBLogMsg =  szDBLogMsg + szLogMsg+"\r\n";
				} else {
					jrL2Crn = jsL2Crn.getRecord(0);
				}
			} else {
				jrL2Crn = jsL2Crn.getRecord(0);
			}
			

			String targetCrnSt 	 		= commUtils.trim(jrL2Crn.getFieldString("TARGET_CRN_ST"  			)); //
			String targetEquipGp 	 	= commUtils.trim(jrL2Crn.getFieldString("TARGET_EQUIP_GP"  			)); //대상크레인ID
			String targetWorkStat 	 	= commUtils.trim(jrL2Crn.getFieldString("TARGET_WORK_STAT"  		)); //대상크레인작업구분(1:권상전,2:권상후,3:이상)
			String targetWrkProgStat	= commUtils.trim(jrL2Crn.getFieldString("TARGET_CRN_WRK_PROG_STAT"	)); //대상크레인작업상태
			long targetCurrXaxis 	 	= Long.parseLong(commUtils.nvl(jrL2Crn.getFieldString("TARGET_CURR_XAXIS"),"0")); //
			long targetFromXaxis 	 	= Long.parseLong(commUtils.nvl(jrL2Crn.getFieldString("TARGET_FROM_XAXIS"),"0")); //
			long targetToXaxis 	 	    = Long.parseLong(commUtils.nvl(jrL2Crn.getFieldString("TARGET_TO_XAXIS"  ),"0")); //
			
			String otherCrnSt	 		= commUtils.trim(jrL2Crn.getFieldString("OTHER_CRN_ST"  			)); //
			String otherEquipGp 	 	= commUtils.trim(jrL2Crn.getFieldString("OTHER_EQUIP_GP"  			)); //상대크레인ID
			String otherWorkStat 	 	= commUtils.trim(jrL2Crn.getFieldString("OTHER_WORK_STAT"  			)); //상대크레인작업구분(1:권상전,2:권상후,3:이상)
			String otherWrkProgStat 	= commUtils.trim(jrL2Crn.getFieldString("OTHER_CRN_WRK_PROG_STAT"  	)); //대상크레인작업상태
			long otherCurrXaxis 	 	= Long.parseLong(commUtils.nvl(jrL2Crn.getFieldString("OTHER_CURR_XAXIS"),"0")); //
			long otherFromXaxis 	 	= Long.parseLong(commUtils.nvl(jrL2Crn.getFieldString("OTHER_FROM_XAXIS"),"0")); //
			long otherToXaxis 	 		= Long.parseLong(commUtils.nvl(jrL2Crn.getFieldString("OTHER_TO_XAXIS"  ),"0")); //
			String otherDirGp 	 		= commUtils.trim(jrL2Crn.getFieldString("OTHER_DIR_GP"  			)); //
			String otherCrnLoc	 		= commUtils.trim(jrL2Crn.getFieldString("OTHER_CRN_LOC"  			)); //
			long craneGap 	 			= Long.parseLong(commUtils.nvl(jrL2Crn.getFieldString("CRANE_GAP"  ),"0")); //
			
			long RYdBayGpX 	 			= Long.parseLong(commUtils.nvl(jrL2Crn.getFieldString("R_YD_BAY_GP_X"  ),"999999")); //
			
						
//			szDBLogMsg =  szDBLogMsg + "▶대상크레인 정보("+targetEquipGp+")"+"\r\n";
//			szDBLogMsg =  szDBLogMsg + "-- 현재위치("+targetCurrXaxis+")"+"\r\n";

			if(otherWorkStat.equals("0")) {
				if(otherCrnLoc.equals("L")) {  //상대 크레인이 좌측인 경우
					szDBLogMsg =  szDBLogMsg + "▶상대크레인 정보("+otherEquipGp+")-- 좌측 대기상태("+otherWorkStat+")"+"<br>";
				} else {
					szDBLogMsg =  szDBLogMsg + "▶상대크레인 정보("+otherEquipGp+")-- 우측 대기상태("+otherWorkStat+")"+"<br>";
				}
			} else if(otherWorkStat.equals("1")) {
				if(otherCrnLoc.equals("L")) {  //상대 크레인이 좌측인 경우
					
					szDBLogMsg =  szDBLogMsg + "▶상대크레인 정보("+otherEquipGp+")-- 좌측 권상전   ("+otherWorkStat+")"+"<br>";
				} else {
					szDBLogMsg =  szDBLogMsg + "▶상대크레인 정보("+otherEquipGp+")-- 우측 권상전   ("+otherWorkStat+")"+"<br>";
				}
				
			} else if(otherWorkStat.equals("2")) {
				if(otherCrnLoc.equals("L")) {  //상대 크레인이 좌측인 경우
					szDBLogMsg =  szDBLogMsg + "▶상대크레인 정보("+otherEquipGp+")-- 좌측 권상후   ("+otherWorkStat+")"+"<br>";
				} else {
					szDBLogMsg =  szDBLogMsg + "▶상대크레인 정보("+otherEquipGp+")-- 우측 권상후   ("+otherWorkStat+")"+"<br>";
				}
			}
			commUtils.printLog(logId, szDBLogMsg, "SL");
			// 대상 스케쥴  정보  
			String locStat         	= "";
			String newYdCrnSchId  	= "";
//			String newCrnSchNm   	= "";
			String newFromLoc   	= "";
			String newToLoc   		= "";
			String ydUpWoLoc 		= ""; 
			String ydUpWoLayer 		= "";
			String ydDnWoLoc 	 	= "";
			String ydDnWoLayer 		= ""; 
			String sFirstCar 		= ""; 
			String sLastDCLinOff    = "";
			String sLastSPM2LinOff  = "";
			String newStock         = "";
			String othWaitCraneYn   = "N"; 
			String sLocCntYn   		= ""; 
			String sSpmSeqYn        = "";
			String sCrnWrkTargerYn  = "";
			
			JDTORecord jrRtnWrkWaitCrn = JDTORecordFactory.getInstance().create();
			
			
			for (int i = 0; i < jsRtnWrkWaitCrn.size(); i++) {
				szLogMsg = "";
				jrRtnWrkWaitCrn  = jsRtnWrkWaitCrn.getRecord(i);
				newYdCrnSchId 	= commUtils.trim(jrRtnWrkWaitCrn.getFieldString("YD_CRN_SCH_ID")); //대상여부
				//newCrnSchNm   	= commUtils.trim(jrRtnWrkWaitCrn.getFieldString("SCH_NM")); 
				newStock   		= commUtils.trim(jrRtnWrkWaitCrn.getFieldString("STOCK_ID")); 
				newFromLoc 		= commUtils.trim(jrRtnWrkWaitCrn.getFieldString("FROM_LOC")); 
				newToLoc 		= commUtils.trim(jrRtnWrkWaitCrn.getFieldString("TO_LOC")); 
				ydUpWoLoc 		= commUtils.trim(jrRtnWrkWaitCrn.getFieldString("YD_UP_WO_LOC"     )); //권상위치 
				ydUpWoLayer 	= commUtils.trim(jrRtnWrkWaitCrn.getFieldString("YD_UP_WO_LAYER"   )); //권상위치단
				ydDnWoLoc 	 	= commUtils.trim(jrRtnWrkWaitCrn.getFieldString("YD_DN_WO_LOC"     )); //권하위치
				ydDnWoLayer 	= commUtils.trim(jrRtnWrkWaitCrn.getFieldString("YD_DN_WO_LAYER"   )); //권하위치단
				sFirstCar 		= commUtils.trim(jrRtnWrkWaitCrn.getFieldString("FIRST_CAR"        )); //
				sLastDCLinOff   = commUtils.trim(jrRtnWrkWaitCrn.getFieldString("DC_LINE_OFF_MAX"  )); //
				sLastSPM2LinOff = commUtils.trim(jrRtnWrkWaitCrn.getFieldString("SPM2_LINE_OFF_MAX")); //
				sLocCntYn 		= commUtils.trim(jrRtnWrkWaitCrn.getFieldString("LOC_CNT_YN")); //
				sSpmSeqYn 		= commUtils.trim(jrRtnWrkWaitCrn.getFieldString("SPM_LINE_IN_MIN")); //
				sCrnWrkTargerYn	= commUtils.trim(jrRtnWrkWaitCrn.getFieldString("CRN_WRK_TARGET_YN")); //
				
				lngSchUpXaxis = Long.parseLong(commUtils.nvl(jrRtnWrkWaitCrn.getFieldString("YD_UP_WO_LOC_XAXIS"),"0")); //스케쥴지시 권상위치
				lngSchDnXaxis = Long.parseLong(commUtils.nvl(jrRtnWrkWaitCrn.getFieldString("YD_DN_WO_LOC_XAXIS"),"0")); //스케쥴지시 권하위치
				
				/**********************************************************
				* 1. 기본적인 상하단 Check
				**********************************************************/
				if (ydUpWoLoc.length() != 8) {
					sAvdYn = "N";
					szLogMsg = "실패:("+newStock +")권상위치이상-불가"+"<br>";
					commUtils.printLog(logId, szLogMsg , "SL");
					// 받은 작업지시(스케쥴) 목록에 고도화 가능여부만 추가 후 그대로 return.
					jsRtnWrkWaitCrn.getRecord(i).addField("AVD_YN"	, sAvdYn		);	// 고도화 가능여부
					jsRtnWrkWaitCrn.getRecord(i).addField("AVD_MSG"	, szDBLogMsg + szLogMsg		);	// 고도화 메세지
					continue;
				}
				
				if (ydDnWoLoc.length() != 8) {
					sAvdYn = "N";
					szLogMsg = "실패:("+newStock +")권하위치이상-불가"+"<br>";
					commUtils.printLog(logId, szLogMsg , "SL");
					// 받은 작업지시(스케쥴) 목록에 고도화 가능여부만 추가 후 그대로 return.
					jsRtnWrkWaitCrn.getRecord(i).addField("AVD_YN"	, sAvdYn		);	// 고도화 가능여부
					jsRtnWrkWaitCrn.getRecord(i).addField("AVD_MSG"	, szDBLogMsg + szLogMsg		);	// 고도화 메세지
					continue;
				}
				
				if (!sFirstCar.equals("T")) {
					sAvdYn = "N";
					szLogMsg = "실패:("+newStock +")먼저 들어온 차량이 있습니다."+"<br>";
					commUtils.printLog(logId, szLogMsg , "SL");
					// 받은 작업지시(스케쥴) 목록에 고도화 가능여부만 추가 후 그대로 return.
					jsRtnWrkWaitCrn.getRecord(i).addField("AVD_YN"	, sAvdYn		);	// 고도화 가능여부
					jsRtnWrkWaitCrn.getRecord(i).addField("AVD_MSG"	, szDBLogMsg + szLogMsg		);	// 고도화 메세지
					continue;
				}
				
				if (!"T".equals(sLastDCLinOff)) {
					sAvdYn = "N";
					szLogMsg = "실패:("+newStock +")DCLineOff 마지막스케줄이 아닙니다."+"<br>";
					commUtils.printLog(logId, szLogMsg , "SL");
					// 받은 작업지시(스케쥴) 목록에 고도화 가능여부만 추가 후 그대로 return.
					jsRtnWrkWaitCrn.getRecord(i).addField("AVD_YN"	, sAvdYn		);	// 고도화 가능여부
					jsRtnWrkWaitCrn.getRecord(i).addField("AVD_MSG"	, szDBLogMsg + szLogMsg		);	// 고도화 메세지
					continue;
				}
				
				if (!"T".equals(sLastSPM2LinOff)) {
					sAvdYn = "N";
					szLogMsg = "실패:("+newStock +")SPM2추출 마지막스케줄이 아닙니다."+"<br>";
					commUtils.printLog(logId, szLogMsg , "SL");
					// 받은 작업지시(스케쥴) 목록에 고도화 가능여부만 추가 후 그대로 return.
					jsRtnWrkWaitCrn.getRecord(i).addField("AVD_YN"	, sAvdYn		);	// 고도화 가능여부
					jsRtnWrkWaitCrn.getRecord(i).addField("AVD_MSG"	, szDBLogMsg + szLogMsg		);	// 고도화 메세지
					continue;
				}
				
				if (!"T".equals(sLocCntYn)) {
					sAvdYn = "N";
					szLogMsg = "실패:("+newStock +")크레인 작업 범위가 아닙니다."+"<br>";
					commUtils.printLog(logId, szLogMsg , "SL");
					jsRtnWrkWaitCrn.getRecord(i).addField("AVD_YN"	, sAvdYn		);	// 고도화 가능여부
					jsRtnWrkWaitCrn.getRecord(i).addField("AVD_MSG"	, szDBLogMsg + szLogMsg		);	// 고도화 메세지
					continue;
				}
				
				if (!"T".equals(sSpmSeqYn)) {
					sAvdYn = "N";
					szLogMsg = "실패:("+newStock +")SPM 보급순서가 빠른 정보가 있습니다.."+"<br>";
					commUtils.printLog(logId, szLogMsg , "SL");
					jsRtnWrkWaitCrn.getRecord(i).addField("AVD_YN"	, sAvdYn		);	// 고도화 가능여부
					jsRtnWrkWaitCrn.getRecord(i).addField("AVD_MSG"	, szDBLogMsg + szLogMsg		);	// 고도화 메세지
					continue;
				}
				
				if (!"T".equals(sCrnWrkTargerYn)) {
					sAvdYn = "N";
					szLogMsg = "실패:("+newStock +")주작업/작업범위 대상에서 제외됩니다...."+"<br>";
					commUtils.printLog(logId, szLogMsg , "SL");
					jsRtnWrkWaitCrn.getRecord(i).addField("AVD_YN"	, sAvdYn		);	// 고도화 가능여부
					jsRtnWrkWaitCrn.getRecord(i).addField("AVD_MSG"	, szDBLogMsg + szLogMsg		);	// 고도화 메세지
					continue;
				}
				
				if (ydUpWoLayer.equals("01")) {
					jrParam.setField("STACK_COL_GP" , ydUpWoLoc.substring(0, 6)); //권상위치
					jrParam.setField("STACK_BED_GP" , ydUpWoLoc.substring(6, 8)); //권상위치단
					/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getLoc1DanChk 
					-- 1단일 경우 가능여부:2단 상단 CHECK  
					SELECT DECODE(COUNT(*),0,'Y','N') AS ABLE_YN
					  FROM TB_YM_STACKLAYER
					 WHERE STACK_COL_GP = :V_STACK_COL_GP
					   AND STACK_BED_GP IN (:V_STACK_BED_GP, LPAD(TO_NUMBER(:V_STACK_BED_GP) - 1, 2,'0'))
					   AND STACK_LAYER_GP = '02'
					   AND STOCK_ID IS NOT NULL
					*/   
					JDTORecordSet jsLocChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getLoc1DanChk", logId, methodNm, "1단 CHECK");
						
					if (jsLocChk.size() > 0) {
						if( jsLocChk.getRecord(0).getFieldString("ABLE_YN").equals("N") ) {
							commUtils.printLog(logId, methodNm + "권상상단에 코일이 있음-불가", "SL");
							sAvdYn = "N";
							szLogMsg = "실패:("+newStock +")  FROM :"+newFromLoc +"  TO:"+newToLoc + ")권상상단에 코일이 있음-불가"+"<br>";
							commUtils.printLog(logId, szLogMsg , "SL");
							// 받은 작업지시(스케쥴) 목록에 고도화 가능여부만 추가 후 그대로 return.
							jsRtnWrkWaitCrn.getRecord(i).addField("AVD_YN"	, sAvdYn		);	// 고도화 가능여부
							jsRtnWrkWaitCrn.getRecord(i).addField("AVD_MSG"	, szDBLogMsg + szLogMsg		);	// 고도화 메세지
							continue;
						}
					}
				}
				
				if(ydDnWoLayer.equals("01")) {
					jrParam.setField("STACK_COL_GP" , ydDnWoLoc.substring(0, 6)); //권하위치
					jrParam.setField("STACK_BED_GP" , ydDnWoLoc.substring(6, 8)); //권하위치단
					/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getLoc1DanChk 
					-- 1단일 경우 가능여부:2단 상단 CHECK  
					SELECT DECODE(COUNT(*),0,'Y','N') AS ABLE_YN
					  FROM TB_YM_STACKLAYER
					 WHERE STACK_COL_GP = :V_STACK_COL_GP
					   AND STACK_BED_GP IN (:V_STACK_BED_GP, LPAD(TO_NUMBER(:V_STACK_BED_GP) - 1, 2,'0'))
					   AND STACK_LAYER_GP = '02'
					   AND STOCK_ID IS NOT NULL
					*/   
					JDTORecordSet jsLocChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getLoc1DanChk", logId, methodNm, "1단 CHECK");
						
					if (jsLocChk.size() > 0) {
						if( jsLocChk.getRecord(0).getFieldString("ABLE_YN").equals("N") ) {
							commUtils.printLog(logId, methodNm + "권하상단에 코일이 있음- 코일이 있음-불가", "SL");
							sAvdYn = "N";
							szLogMsg = "실패:("+newStock +")  FROM :"+newFromLoc +"  TO:"+newToLoc + ")권하상단에 코일이 있음-불가"+"<br>";
							commUtils.printLog(logId, szLogMsg , "SL");
							// 받은 작업지시(스케쥴) 목록에 고도화 가능여부만 추가 후 그대로 return.
							jsRtnWrkWaitCrn.getRecord(i).addField("AVD_YN"	, sAvdYn		);	// 고도화 가능여부
							jsRtnWrkWaitCrn.getRecord(i).addField("AVD_MSG"	,  szDBLogMsg + szLogMsg	);	// 고도화 메세지
							continue;
							
						}
					}
				}
				
				if(ydDnWoLayer.equals("02")) {
					jrParam.setField("STACK_COL_GP" , ydDnWoLoc.substring(0, 6)); //권하위치
					jrParam.setField("STACK_BED_GP" , ydDnWoLoc.substring(6, 8)); //권하위치단
					/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getLoc2DanChk  
					-- 2단일 경우 가능여부:1단 하단 CHECK  
					SELECT DECODE(COUNT(*),2,'Y','N') AS ABLE_YN
					  FROM TB_YM_STACKLAYER
					 WHERE STACK_COL_GP = :V_STACK_COL_GP
					   AND STACK_BED_GP IN (:V_STACK_BED_GP, LPAD(TO_NUMBER(:V_STACK_BED_GP) + 1, 2,'0'))
					   AND STACK_LAYER_GP = '01'
					   AND STOCK_ID IS NOT NULL
					   AND STACK_LAYER_STAT = 'C'
					*/   
					JDTORecordSet jsLocChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getLoc2DanChk", logId, methodNm, "2단 CHECK");
						
					if (jsLocChk.size() > 0) {
						if( jsLocChk.getRecord(0).getFieldString("ABLE_YN").equals("N") ) {

							commUtils.printLog(logId, methodNm + "권하하단에 코일이 없음-불가", "SL");
							sAvdYn = "N";
							szLogMsg = "실패:("+newStock +")  FROM :"+newFromLoc +"  TO:"+newToLoc + ")권하하단에 코일이 없음-불가"+"<br>";
							commUtils.printLog(logId, szLogMsg , "SL");
							// 받은 작업지시(스케쥴) 목록에 고도화 가능여부만 추가 후 그대로 return.
							jsRtnWrkWaitCrn.getRecord(i).addField("AVD_YN"	, sAvdYn		);	// 고도화 가능여부
							jsRtnWrkWaitCrn.getRecord(i).addField("AVD_MSG"	,  szDBLogMsg + szLogMsg		);	// 고도화 메세지
							continue;
							
						}
					}
				}		
				
				if(otherWorkStat.equals("0")) {
					//상대 크레인 작업 대기인 경우
					/**********************************************************
					* 권상전 인 경우
					* 1.동일 방향 대상선택
					* 2.현재크레인 위치 - 대상권상위치가 짧은 거리
					* 3.대상크레인이 좌측인 경우 
					*   - 상대크레인 작업권상 < 대상작업권상 인 대상
					* 4.대상크레인이 우측인 경우  
					*   - 상대크레인 작업권상 > 대상작업권상 인 대상 
					**********************************************************/
					
					if(targetEquipGp.substring(4,6).equals("A1")||targetEquipGp.substring(4,6).equals("C1")||targetEquipGp.substring(4,6).equals("E1")) {
						jrRtnWrkWaitCrn.setField("DISTAN"	, ""+ Math.abs(0 - lngSchUpXaxis) );  //최좌측(0) - 대상권상위치
						
					} else if(targetEquipGp.substring(4,6).equals("A2")||targetEquipGp.substring(4,6).equals("C3")||targetEquipGp.substring(4,6).equals("E3")) {
						jrRtnWrkWaitCrn.setField("DISTAN"	, ""+ Math.abs(RYdBayGpX - lngSchUpXaxis) );  //최우측(9999999) - 대상권상위치
						
					} else {
						jrRtnWrkWaitCrn.setField("DISTAN"	, ""+ Math.abs(targetCurrXaxis - lngSchUpXaxis) );  //현재크레인 위치 - 대상권상위치
					}
					jsAbleResult.addRecord(jrRtnWrkWaitCrn);
					sAvdYn = "Y";
					szLogMsg = "성공:("+newStock +")  FROM :"+newFromLoc +"(" + lngSchUpXaxis + ") TO:"+newToLoc + "(" + lngSchDnXaxis + "))"+"<br>";
					szLogMsg = szLogMsg + "... 거리:" + (Math.abs(targetCurrXaxis - lngSchUpXaxis));
					commUtils.printLog(logId, szLogMsg, "SL");

				} else if(otherWorkStat.equals("1")) {
					//권상 전	'+ = +', '- = -'
					/**********************************************************
					* 권상전 인 경우
					* 1.동일 방향 대상선택
					* 2.현재크레인 위치 - 대상권상위치가 짧은 거리
					* 3.대상크레인이 좌측인 경우 
					*   - 상대크레인 작업권상 < 대상작업권상 인 대상
					* 4.대상크레인이 우측인 경우  
					*   - 상대크레인 작업권상 > 대상작업권상 인 대상 
					**********************************************************/
					
					if(otherCrnLoc.equals("L")) {  //상대 크레인이 좌측인 경우
						 
						if(((otherFromXaxis+craneGap) <  lngSchUpXaxis) && ((otherToXaxis+craneGap) < lngSchDnXaxis)) {  //권상위치가 상대크레인 권상위치 보다 크고 권하위치가 상대크레인 권하위치 보다 큰경우
							jrRtnWrkWaitCrn.setField("DISTAN"	, ""+ Math.abs(targetCurrXaxis - lngSchUpXaxis) );  //현재크레인 위치 - 대상권상위치
							jsAbleResult.addRecord(jrRtnWrkWaitCrn);
							sAvdYn = "Y";
							szLogMsg = "성공:("+newStock +")  FROM :"+newFromLoc +"(" + lngSchUpXaxis + ") TO:"+newToLoc + "(" + lngSchDnXaxis + "))"+"<br>";
							szLogMsg = szLogMsg + "... 거리:" + (Math.abs(targetCurrXaxis - lngSchUpXaxis));
							commUtils.printLog(logId, szLogMsg, "SL");

						} else {
							sAvdYn = "N";
							szLogMsg = "실패:("+newStock +")  FROM :"+newFromLoc +"(" + lngSchUpXaxis + ") TO:"+newToLoc + "(" + lngSchDnXaxis + "))"+"<br>"; 
							if((otherFromXaxis+craneGap) >=  lngSchUpXaxis) {
								szLogMsg = szLogMsg + "상대 크레인 권상좌표+GAP(" + (otherFromXaxis+craneGap) +") >=  "+ "지시권상좌표" + "(" + lngSchUpXaxis + ") 간섭걸림:";
							} else if((otherToXaxis+craneGap) >=  lngSchDnXaxis) {
								szLogMsg = szLogMsg + "상대 크레인 권하좌표+GAP(" + (otherToXaxis  +craneGap) +") >=  "+ "지시권하좌표" + "(" + lngSchDnXaxis + ") 간섭걸림:";
							}
							
							commUtils.printLog(logId, szLogMsg , "SL");
						}
					} else {
						
						if(((otherFromXaxis-craneGap) >  lngSchUpXaxis) && ((otherToXaxis-craneGap) > lngSchDnXaxis)) {  //권상위치가 상대크레인 권상위치 보다 작고 권하위치가 상대크레인 권하위치 보다 작은경우
							jrRtnWrkWaitCrn.setField("DISTAN"	, ""+ Math.abs(targetCurrXaxis - lngSchUpXaxis) );  //현재크레인 위치 - 대상권상위치
							jsAbleResult.addRecord(jrRtnWrkWaitCrn);
							sAvdYn = "Y";
							
							szLogMsg = "성공:("+newStock +")  FROM :"+newFromLoc +"(" + lngSchUpXaxis + ") TO:"+newToLoc + "(" + lngSchDnXaxis + "))"+"<br>";
//							szLogMsg = szLogMsg + "상대 크레인 권상좌표-GAP(" + (otherFromXaxis-craneGap) +") > "+ "지시권상좌표" + "(" + lngSchUpXaxis + ")";
//							szLogMsg = szLogMsg + "상대 크레인 권하좌표-GAP(" + (otherToXaxis  -craneGap) +") > "+ "지시권하좌표" + "(" + lngSchDnXaxis + ")";
//							szLogMsg = szLogMsg + "상대가 우측/권상전/권상위치가 상대크레인 권상위치-GAP 보다 작고 권하위치가 상대크레인 권하위치-GAP 보다 작은 대상";
							szLogMsg = szLogMsg + "... 거리:" + (Math.abs(targetCurrXaxis - lngSchUpXaxis));
							commUtils.printLog(logId, szLogMsg, "SL");
							
						} else {
							sAvdYn = "N";
							szLogMsg = "실패:("+newStock +")  FROM :"+newFromLoc +"(" + lngSchUpXaxis + ") TO:"+newToLoc + "(" + lngSchDnXaxis + "))"+"<br>";;
							
							if((otherFromXaxis-craneGap) <=  lngSchUpXaxis) {
								szLogMsg = szLogMsg + "상대 크레인 권상좌표-GAP(" + (otherFromXaxis-craneGap) +") <= "+ "지시권상좌표" + "(" + lngSchUpXaxis + ") 간섭걸림:";
							} else if((otherToXaxis-craneGap) <=  lngSchDnXaxis) {
								szLogMsg = szLogMsg + "상대 크레인 권하좌표-GAP(" + (otherToXaxis  -craneGap) +") <= "+ "지시권하좌표" + "(" + lngSchDnXaxis + ") 간섭걸림:";
							}
							
//							szLogMsg = szLogMsg + "상대가 우측/권상전/권상위치가 상대크레인 권상위치-GAP 보다 작고 권하위치가 상대크레인 권하위치-GAP 보다 작은 대상에 위배";
							commUtils.printLog(logId, szLogMsg, "SL");
						}
					}
				} else {
				//권상 후	
					/**********************************************************
					* 권상후 인 경우
					* 1.역 방향 대상선택
					* 2.현재크레인 위치 - 대상권상위치가 짧은 거리
					* 3.대상크레인이 좌측인 경우 
					*   - 상대크레인 작업권하 < 대상작업권상 이고 작업권하 < 대상작업권하 이고  인 경우
					* 4.대상크레인이 우측인 경우 
					*   - 상대크레인 작업권하 > 대상작업권상 이고 작업권하 > 대상작업권하 이고  인 경우
					**********************************************************/
					if(otherCrnLoc.equals("L")) {  //상대 크레인이 좌측인 경우
						
						if(((otherToXaxis+craneGap) < lngSchUpXaxis) && ((otherToXaxis+craneGap) < lngSchDnXaxis)) {
							jrRtnWrkWaitCrn.setField("DISTAN"	, ""+ Math.abs(targetCurrXaxis - lngSchUpXaxis) );  //현재크레인 위치 - 대상권상위치
							jsAbleResult.addRecord(jrRtnWrkWaitCrn);
							sAvdYn = "Y";
							
							szLogMsg = "성공:("+newStock +")  FROM :"+newFromLoc +"(" + lngSchUpXaxis + ") TO:"+newToLoc + "(" + lngSchDnXaxis + "))"+"<br>"; 
//							szLogMsg = szLogMsg + "상대 크레인 권하좌표+GAP(" + (otherToXaxis  +craneGap) +") <  "+ "지시권상좌표" + "(" + lngSchUpXaxis + ")";
//							szLogMsg = szLogMsg + "상대 크레인 권하좌표+GAP(" + (otherToXaxis  +craneGap) +") <  "+ "지시권하좌표" + "(" + lngSchDnXaxis + ")";
//							szLogMsg = szLogMsg + "상대가 좌측/권상후/권상권하위치가 상대크레인 권하위치보다 큰 대상";
							szLogMsg = szLogMsg + "... 거리:" + (Math.abs(targetCurrXaxis - lngSchUpXaxis));
							commUtils.printLog(logId, szLogMsg, "SL");
						} else {
							sAvdYn = "N";
							szLogMsg = "실패:("+newStock +")  FROM :"+newFromLoc +"(" + lngSchUpXaxis + ") TO:"+newToLoc + "(" + lngSchDnXaxis + "))"+"<br>"; 
							if((otherToXaxis+craneGap) >=  lngSchUpXaxis) {
								szLogMsg = szLogMsg + "상대 크레인 권하좌표+GAP(" + (otherToXaxis  +craneGap) +") >=  "+ "지시권상좌표" + "(" + lngSchUpXaxis + ") 간섭걸림:";
							} else if((otherToXaxis+craneGap) >=  lngSchDnXaxis){
								szLogMsg = szLogMsg + "상대 크레인 권하좌표+GAP(" + (otherToXaxis  +craneGap) +") >=  "+ "지시권하좌표" + "(" + lngSchDnXaxis + ") 간섭걸림:";
							}
//							szLogMsg = szLogMsg + "상대가 좌측/권상후/권상권하위치가 상대크레인 권하위치보다 큰 대상에 위배";
							commUtils.printLog(logId, szLogMsg, "SL");
							
						}
					} else {
						if(((otherToXaxis-craneGap) > lngSchUpXaxis) && ((otherToXaxis-craneGap) > lngSchDnXaxis)) {
							jrRtnWrkWaitCrn.setField("DISTAN"	, ""+ Math.abs(targetCurrXaxis - lngSchUpXaxis) );  //현재크레인 위치 - 대상권상위치
							jsAbleResult.addRecord(jrRtnWrkWaitCrn);
							sAvdYn = "Y";
							
							szLogMsg = "성공:("+newStock +")  FROM :"+newFromLoc +"(" + lngSchUpXaxis + ") TO:"+newToLoc + "(" + lngSchDnXaxis + "))"+"<br>"; 
							
//							szLogMsg = szLogMsg + "상대 크레인 권하좌표-GAP(" + (otherToXaxis  -craneGap) +") >  "+ "지시권상좌표" + "(" + lngSchUpXaxis + ")";
//							szLogMsg = szLogMsg + "상대 크레인 권하좌표-GAP(" + (otherToXaxis  -craneGap) +") >  "+ "지시권하좌표" + "(" + lngSchDnXaxis + ")";
//							szLogMsg = szLogMsg + "상대가 우측/권상후/권상권하위치가 상대크레인 권하위치보다 작은 대상";
							szLogMsg = szLogMsg + "... 거리:" + (Math.abs(targetCurrXaxis - lngSchUpXaxis));
							commUtils.printLog(logId, szLogMsg, "SL");
							
						} else {
							sAvdYn = "N";
							szLogMsg = "실패:("+newStock +")  FROM :"+newFromLoc +"(" + lngSchUpXaxis + ") TO:"+newToLoc + "(" + lngSchDnXaxis + "))"+"<br>"; 
							if((otherToXaxis-craneGap) <=  lngSchUpXaxis) {
								szLogMsg = szLogMsg + "상대 크레인 권하좌표-GAP(" + (otherToXaxis  -craneGap) +") <= "+ "지시권상좌표" + "(" + lngSchUpXaxis + ") 간섭걸림:";
							} else if((otherToXaxis-craneGap) <=  lngSchDnXaxis){
								szLogMsg = szLogMsg + "상대 크레인 권하좌표-GAP(" + (otherToXaxis  -craneGap) +") <= "+ "지시권하좌표" + "(" + lngSchDnXaxis + ") 간섭걸림:";
							}
//							szLogMsg = szLogMsg + "상대가 우측/권상후/권상권하위치가 상대크레인 권하위치보다 작은 대상에 위배";
							commUtils.printLog(logId, szLogMsg, "SL");
						}
					}
				}
				// 받은 작업지시(스케쥴) 목록에 고도화 가능여부만 추가 후 그대로 return.
				jsRtnWrkWaitCrn.getRecord(i).addField("AVD_YN"	, sAvdYn		);	// 고도화 가능여부
				jsRtnWrkWaitCrn.getRecord(i).addField("AVD_MSG"	, szDBLogMsg + szLogMsg		);	// 고도화 메세지
			}
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }//end of try~catch				
		return jsRtnWrkWaitCrn;
	}
    
    /**
	 *      [A] 오퍼레이션명 : A동 고도화 크레인작업지시 가능여부 확인(모니터링 전용)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecordSet coilCrnSchChoiceAutoNewA(JDTORecordSet jsRtnWrkWaitCrn, String logId, String ydEqpId) throws DAOException {
		
		String methodNm = "A동 고도화 크레인작업지시 가능여부 확인[BCoilJspSeEJB.coilCrnSchChoiceAutoNewA] < ";

    	String szLogMsg         = "";
    	
		long   lngSchUpXaxis   = 0;
		long   lngSchDnXaxis   = 0;
		
		String ydL3Msg    		= ""; 		//야드L3MESSAGE
		String firstYdCrnSchId 	= "";
		String firstYdSchCd    	= "";
		
		JDTORecord jrRtnWrkWaitCrn = JDTORecordFactory.getInstance().create();
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+");
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			
			/* 크레인 이동상태 정보  */
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getL2CrnInfoA 
			SELECT A.*
			     , MIN(CASE WHEN CRN_ST = 2 AND TO_NUMBER(SUBSTR(EQUIP_GP,6,1)) < TO_NUMBER(SUBSTR(:V_YD_EQP_ID,6,1)) THEN 'L' 
			            ELSE 'R' END) OVER() OTHER_CRN_LOC               -- 상대크레인 위치정보
			  FROM (
			        SELECT CASE WHEN A.EQUIP_GP  = :V_YD_EQP_ID THEN '1' -- 본크레인 정보
			                    ELSE '2' END CRN_ST                      -- 상대크레인 정보
			             , A.EQUIP_GP          AS EQUIP_GP
			             , CASE WHEN C.WPROG_STAT IN ('4','W') AND C.WORK_MODE = '1'  THEN '0'  -- 작업대기
					            WHEN C.WPROG_STAT IN ('1')     AND C.WORK_MODE = '1'  THEN '1'  -- 권상전
					            WHEN C.WPROG_STAT IN ('2')     AND C.WORK_MODE = '1'  THEN '2'   -- 권상후  
					            ELSE '3' END   AS OTHER_WORK_STAT -- 이상케이스  이상       
			             , A.CRN_WRK_PROC_STAT AS OTHER_CRN_WRK_PROG_STAT
			             , A.CURR_XAXIS        AS OTHER_CURR_XAXIS
			             , A.FROM_XAXIS        AS OTHER_FROM_XAXIS
			             , A.TO_XAXIS          AS OTHER_TO_XAXIS
			             , CASE WHEN FROM_XAXIS - TO_XAXIS < 0 THEN '+'
			                    ELSE '-' END   AS OTHER_DIR_GP
			         FROM BHCYD.TB_AY_CRN_STA_L3@DL_BHCYDL2    A
			            , (SELECT :V_YD_EQP_ID AS CURR_YD_EQP_ID FROM DUAL) B
			            , TB_YM_EQUIP C
			        WHERE A.EQUIP_GP = C.EQUIP_GP
			          AND A.EQUIP_GP IN (B.CURR_YD_EQP_ID,  CASE WHEN B.CURR_YD_EQP_ID = '3ACRA1' THEN '3ACRA2' 
			                                                     WHEN B.CURR_YD_EQP_ID = '3ACRA2' THEN '3ACRA1' 
			                                                     ELSE B.CURR_YD_EQP_ID END  )
			       ) A
			  ORDER BY CRN_ST, EQUIP_GP 
			 */
			
			jrParam.setField("YD_EQP_ID"    , ydEqpId  ); //크레인 정보
			
			JDTORecordSet jsL2Crn = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getL2CrnInfoA", logId, methodNm, "크레인 이동상태 조회");
			
			commUtils.printLog(logId, "step 1 >> " + " 고도화 기준 조회 START ******* ", "SL");
			
			String sAvdYn = "N";	// 고도화 가능여부
			
			// 대상 스케쥴  정보  
			String newFromLoc   	= "";
			String newToLoc   		= "";
			String newStock         = "";
			
			for(int i = 0; i < jsRtnWrkWaitCrn.size(); i++) {	// Start loop
				jrRtnWrkWaitCrn  = jsRtnWrkWaitCrn.getRecord(i);
				
				//수신 항목 값
				String ydWrkProgStat = commUtils.trim(jrRtnWrkWaitCrn.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
				String ydSchCd       = commUtils.trim(jrRtnWrkWaitCrn.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
				String oldYdCrnSchId = commUtils.trim(jrRtnWrkWaitCrn.getFieldString("YD_CRN_SCH_ID"   )); //야드크레인스케쥴ID
				String ydCrnSchId    = commUtils.trim(jrRtnWrkWaitCrn.getFieldString("YD_CRN_SCH_ID"   )); //야드크레인스케쥴ID
				
				newStock   		= commUtils.trim(jrRtnWrkWaitCrn.getFieldString("STOCK_ID")); 
				newFromLoc 		= commUtils.trim(jrRtnWrkWaitCrn.getFieldString("FROM_LOC")); 
				newToLoc 		= commUtils.trim(jrRtnWrkWaitCrn.getFieldString("TO_LOC")); 
				
				lngSchUpXaxis = Long.parseLong(commUtils.nvl(jrRtnWrkWaitCrn.getFieldString("YD_UP_WO_LOC_XAXIS"),"0")); //스케쥴지시 권상위치
				lngSchDnXaxis = Long.parseLong(commUtils.nvl(jrRtnWrkWaitCrn.getFieldString("YD_DN_WO_LOC_XAXIS"),"0")); //스케쥴지시 권하위치
				
				/**********************************************************
				* 1. 설비상태 Check
				**********************************************************/
				JDTORecord jrChk = ymComm.chkEqpStat(jrParam);
				
				ydL3Msg    = commUtils.trim(jrChk.getFieldString("YD_L3_MSG"     ));

				if (!"".equals(ydL3Msg)) {
					sAvdYn = "N";			// 고도화 불가
					szLogMsg = ydL3Msg;
					commUtils.printLog(logId, szLogMsg , "SL");
					jsRtnWrkWaitCrn.getRecord(i).addField("AVD_YN"	, sAvdYn		);	// 고도화 가능여부 Set
					jsRtnWrkWaitCrn.getRecord(i).addField("AVD_MSG"	, szLogMsg		);	// 고도화 메세지 Set
					continue;
				}
				
				
				/**********************************************************
				* 2. 크레인스케줄 조회
				*    2.1 크레인스케줄이 존재하면 전송
				*    2.2 크레인스케줄이 존재하지 않으면 수신된 야드작업진행상태에 따라 처리
				**********************************************************/
				JDTORecordSet jsSch = JDTORecordFactory.getInstance().createRecordSet("Temp");
				/* 크레인작업지시요구 크레인스케줄 조회  */
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnSchAxYML007A 
				SELECT * 
				  FROM
				       ( SELECT CS.YD_CRN_SCH_ID
				              , CS.YD_WRK_PROG_STAT
				              , CS.YD_UP_WO_LOC 
				              , CS.YD_DN_WO_LOC 
				              , CS.YD_SCH_CD
				              , NVL(SR.ADV_CRN_PRIOR,CS.YD_SCH_PRIOR)  AS YD_SCH_PRIOR
				              , DECODE(CS.YD_WRK_PROG_STAT,'W','0','S','1', CS.YD_WRK_PROG_STAT) AS SEQ1
				              , CASE WHEN CS.YD_SCH_PRIOR = 0  OR SR.ADV_CRN_PRIOR = 0         THEN 0 
				                     WHEN CS.YD_CRN_GRAB_USE_RULE_ID = (SELECT YD_CRN_GRAB_USE_RULE_ID 
				                                                          FROM TB_YM_CRNSCH 
				                                                         WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				                                                           AND SUBSTR(YD_SCH_CD,3,2) = 'PT')     THEN 1  --차량만
				                     ELSE 2  END AS SEQ2 -- 이전 
				           FROM TB_YM_CRNSCH CS
				              , TB_YM_CRNWRKMTL CM
				              , TB_YM_SCHEDULERULE SR
				          WHERE CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
				            AND CS.YD_SCH_CD = SR.YD_SCH_CD
				            AND CS.DEL_YN = 'N'
				            AND CM.DEL_YN = 'N'
				            AND CS.YD_EQP_ID =  :V_YD_EQP_ID 
				          ORDER BY SEQ1 DESC, SEQ2, NVL(SR.ADV_CRN_PRIOR,CS.YD_SCH_PRIOR)
				                 , CASE WHEN YD_SCH_CD = '3ADC01LM' THEN TO_NUMBER(YD_CRN_SCH_ID) * -1
				                        ELSE TO_NUMBER(YD_CRN_SCH_ID) END 
				       )    
				 WHERE ROWNUM = 1
				 */
				
				jrParam.setField("YD_CRN_SCH_ID"    , ydCrnSchId  ); //크레인 정보
				jsSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnSchAxYML007A", logId, methodNm, "크레인스케줄 조회");
				
				if (jsSch.size() > 0) {
					/**********************************************************
					* 2.1 크레인스케줄이 존재하면 수신된 야드작업진행상태에 상관없이 작업지시 전송대상(성공)
					**********************************************************/
					ydCrnSchId    = commUtils.trim(jsSch.getRecord(0).getFieldString("YD_CRN_SCH_ID"   ));
					ydWrkProgStat = commUtils.trim(jsSch.getRecord(0).getFieldString("YD_WRK_PROG_STAT"));
					
					if ("S".equals(ydWrkProgStat)||"1".equals(ydWrkProgStat)||"2".equals(ydWrkProgStat)||"3".equals(ydWrkProgStat)||"5".equals(ydWrkProgStat)) {
						/**********************************************************
						* 2.1.1 권상지시[1], 권상완료[2], 권하지시[3] 이면 재지시 전송대상
						**********************************************************/
					} else {
						firstYdCrnSchId = commUtils.trim(jsSch.getRecord(0).getFieldString("YD_CRN_SCH_ID"   ));
						firstYdSchCd    = commUtils.trim(jsSch.getRecord(0).getFieldString("YD_SCH_CD"   ));

						commUtils.printLog(logId, "▶▶▶▶▶▶▶▶▶▶  A동 1,2 호기 작업    ◀◀◀◀◀◀◀◀◀◀", "SL");	
						ydL3Msg = "";
						ydL3Msg = this.procCoilCrnSchChoiceYnNewA(logId, methodNm, firstYdSchCd, oldYdCrnSchId, firstYdCrnSchId, ydEqpId, jsL2Crn);
					}
					
					if(!"".equals(ydL3Msg)){
						sAvdYn = "N";		// 실패
						szLogMsg = ydL3Msg;
						commUtils.printLog(logId, szLogMsg , "SL");
						jsRtnWrkWaitCrn.getRecord(i).addField("AVD_YN"	, sAvdYn		);	// 고도화 가능여부 Set
						jsRtnWrkWaitCrn.getRecord(i).addField("AVD_MSG"	, szLogMsg		);	// 고도화 메세지 Set
						continue;
					}else{
						sAvdYn = "Y";
						szLogMsg = "성공:("+newStock +")  FROM :"+newFromLoc +"(" + lngSchUpXaxis + ") TO:"+newToLoc + "(" + lngSchDnXaxis + "))"+"<br>";
					}
					
					commUtils.printLog(logId, szLogMsg, "SL");
					
				}else{
					// 여기 탈 일은 없을 거 같지만..
					sAvdYn = "N";		// 실패
					szLogMsg = "실패:(크레인스케쥴 정보 미존재)";
					commUtils.printLog(logId, szLogMsg , "SL");
					jsRtnWrkWaitCrn.getRecord(i).addField("AVD_YN"	, sAvdYn		);	// 고도화 가능여부 Set
					jsRtnWrkWaitCrn.getRecord(i).addField("AVD_MSG"	, szLogMsg		);	// 고도화 메세지 Set
					continue;
				}
				
				jsRtnWrkWaitCrn.getRecord(i).addField("AVD_YN"	, sAvdYn		);	// 고도화 가능여부
				jsRtnWrkWaitCrn.getRecord(i).addField("AVD_MSG"	, szLogMsg		);	// 고도화 메세지
				
			}	// End loop
				
			commUtils.printLog(logId, methodNm, "S-");

			return jsRtnWrkWaitCrn;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : C,E동 고도화 크레인작업지시 가능여부 확인(모니터링 전용)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecordSet coilCrnSchChoiceAutoNewF(JDTORecordSet jsRtnWrkWaitCrn, String logId, String ydEqpId) throws DAOException {
		String methodNm = "C,E동 고도화 크레인작업지시 가능여부 확인[BCoilJspSeEJB.coilCrnSchChoiceAutoNewF] < ";
		
		String ydL3Msg    		= ""; 		//야드L3MESSAGE
		String szLogMsg         = "";
		String sAvdYn			= "";
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setField("YD_EQP_ID"    , ydEqpId  ); //크레인 정보
			
			// Lv2 크레인 상태 조회
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getL2CrnInfoNew 
			-- 1:대상크레인,2:상대크레인
			SELECT MAX(CASE WHEN CRN_ST = 1 THEN A.CRN_ST            ELSE NULL END) AS TARGET_CRN_ST                  
			     , MAX(CASE WHEN CRN_ST = 1 THEN A.EQUIP_GP          ELSE NULL END) AS TARGET_EQUIP_GP                
			     , MAX(CASE WHEN CRN_ST = 1 THEN A.WORK_STAT         ELSE NULL END) AS TARGET_WORK_STAT         
			     , MAX(CASE WHEN CRN_ST = 1 THEN A.CRN_WRK_PROG_STAT ELSE NULL END) AS TARGET_CRN_WRK_PROG_STAT 
			     , MAX(CASE WHEN CRN_ST = 1 THEN A.CURR_XAXIS        ELSE 0    END) AS TARGET_CURR_XAXIS        
			     , MAX(CASE WHEN CRN_ST = 1 THEN A.FROM_XAXIS        ELSE 0    END) AS TARGET_FROM_XAXIS        
			     , MAX(CASE WHEN CRN_ST = 1 THEN A.TO_XAXIS          ELSE 0    END) AS TARGET_TO_XAXIS          
			     , MAX(CASE WHEN CRN_ST = 1 THEN A.DIR_GP            ELSE NULL END) AS TARGET_DIR_GP            
			     , MAX(CASE WHEN CRN_ST = 2 THEN A.CRN_ST            ELSE NULL END) AS OTHER_CRN_ST                  
			     , MAX(CASE WHEN CRN_ST = 2 THEN A.EQUIP_GP          ELSE NULL END) AS OTHER_EQUIP_GP                
			     , MAX(CASE WHEN CRN_ST = 2 THEN A.WORK_STAT         ELSE NULL END) AS OTHER_WORK_STAT         
			     , MAX(CASE WHEN CRN_ST = 2 THEN A.CRN_WRK_PROG_STAT ELSE NULL END) AS OTHER_CRN_WRK_PROG_STAT 
			     , MAX(CASE WHEN CRN_ST = 2 THEN A.CURR_XAXIS        ELSE 0    END) AS OTHER_CURR_XAXIS        
			     , MAX(CASE WHEN CRN_ST = 2 THEN A.FROM_XAXIS        ELSE 0    END) AS OTHER_FROM_XAXIS        
			     , MAX(CASE WHEN CRN_ST = 2 THEN A.TO_XAXIS          ELSE 0    END) AS OTHER_TO_XAXIS          
			     , MAX(CASE WHEN CRN_ST = 2 THEN A.DIR_GP            ELSE NULL END) AS OTHER_DIR_GP            
			     , MIN(CASE WHEN CRN_ST = 2  AND TO_NUMBER(SUBSTR(EQUIP_GP,6,1)) < TO_NUMBER(SUBSTR(:V_YD_EQP_ID,6,1)) THEN 'L' 
			            ELSE 'R' END)                                             AS OTHER_CRN_LOC     
			     , MAX(A.CRANE_GAP) AS CRANE_GAP            
			            
			  FROM (
			        SELECT CASE WHEN A.EQUIP_GP  = :V_YD_EQP_ID THEN '1' -- 본크레인 정보
			                    ELSE '2' END   AS CRN_ST                      -- 상대크레인 정보
			             , A.EQUIP_GP          AS EQUIP_GP
			             , CASE WHEN WPROG_STAT IN ('4','W') AND WORK_MODE = '1'  THEN '0'  -- 작업대기
			                    WHEN WPROG_STAT IN ('1')     AND WORK_MODE = '1'  THEN '1'  -- 권상전
			                    WHEN WPROG_STAT IN ('2')     AND WORK_MODE = '1'  THEN '2'   -- 권상후  
			                    ELSE '3' END   AS WORK_STAT -- 이상케이스  이상 
			             , A.CRN_WRK_PROC_STAT AS CRN_WRK_PROG_STAT
			             , A.CURR_XAXIS        AS CURR_XAXIS
			             , A.FROM_XAXIS        AS FROM_XAXIS
			             , A.TO_XAXIS          AS TO_XAXIS
			             , CASE WHEN FROM_XAXIS = TO_XAXIS     THEN '='
			                    WHEN FROM_XAXIS - TO_XAXIS < 0 THEN '+'
			                    ELSE '-' END   AS DIR_GP
			             , (SELECT DTL_ITM1  
			                  FROM USRYMA.TB_YM_RULE 
			                  WHERE REPR_CD_GP = 'CRNDIF') AS CRANE_GAP  -- 간섭범위
			          FROM TB_YM_CRANEEQUIP A
			             , (SELECT :V_YD_EQP_ID AS CURR_YD_EQP_ID FROM DUAL) B
			             , TB_YM_EQUIP C
			         WHERE A.EQUIP_GP = C.EQUIP_GP
			           AND A.EQUIP_GP IN (B.CURR_YD_EQP_ID,  CASE WHEN B.CURR_YD_EQP_ID = '3CCRC1' THEN '3CCRC2' 
			                                                      WHEN B.CURR_YD_EQP_ID = '3CCRC2' THEN '3CCRC3' 
			                                                      WHEN B.CURR_YD_EQP_ID = '3CCRC3' THEN '3CCRC2' 
			                                                      WHEN B.CURR_YD_EQP_ID = '3ECRE1' THEN '3ECRE2' 
			                                                      WHEN B.CURR_YD_EQP_ID = '3ECRE2' THEN '3ECRE3' 
			                                                      WHEN B.CURR_YD_EQP_ID = '3ECRE3' THEN '3ECRE2' 
			                                                      ELSE B.CURR_YD_EQP_ID END  )
			       ) A
			 */
			
			JDTORecordSet jsL2Crn = commDao.selectL2(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getL2CrnInfoNew", logId, methodNm, "크레인 L2 이동상태 조회");
			commUtils.printLog(logId, "jsL2Crn cnt : " + jsL2Crn.size(), "SL");
			
			JDTORecord jrL2Crn = JDTORecordFactory.getInstance().create();	
			
			if(jsL2Crn.size() <= 0) {	// L2 현재위치가 없으면 L3 현재 위치 데이터로 대체
				
				commUtils.printLog(logId, "db error 고도화 불가 : L2 현재위치 없음 " + " [ " + ydEqpId  + " ]", "SL");
				commUtils.printLog(logId, methodNm, "S-");
				
				jsL2Crn = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnInfoNew", logId, methodNm, "크레인 L3 이동상태 조회");
				
				
				if(jsL2Crn.size() <= 0) {
					commUtils.printLog(logId, "db error 고도화 불가 : L2, L3 현재위치 없음 " + " [ " + ydEqpId + " ]", "SL");
					commUtils.printLog(logId, methodNm, "S-");
					sAvdYn = "N";
					szLogMsg = "실패: db error 고도화 불가 : L2, L3 현재위치 없음 " + " [ " + ydEqpId + " ]";
					commUtils.printLog(logId, szLogMsg , "SL");
				}else{
					jrL2Crn = jsL2Crn.getRecord(0);
				}
			}else{
				jrL2Crn = jsL2Crn.getRecord(0);
			}
			
			
			if (jsL2Crn.size() > 0) {
				
				// 대상 스케쥴  정보  
				String newFromLoc   	= "";
				String newToLoc   		= "";
				String newStock         = "";
				
				long   lngSchUpXaxis   = 0;
				long   lngSchDnXaxis   = 0;
				
				
				/**********************************************************
				* 1. 설비상태 조회
				**********************************************************/
				jrParam.setField("YD_EQP_ID"    , ydEqpId  	); //크레인 정보
				JDTORecord jrChk = ymComm.chkEqpStat(jrParam);

				
				JDTORecord jrRtnWrkWaitCrn = JDTORecordFactory.getInstance().create();
				for(int i = 0; i < jsRtnWrkWaitCrn.size(); i++) {	// Start loop
					
					jrRtnWrkWaitCrn  = jsRtnWrkWaitCrn.getRecord(i);
					
					/**********************************************************
					* 1-2. 설비상태 Check
					**********************************************************/
					ydL3Msg    = commUtils.trim(jrChk.getFieldString("YD_L3_MSG"     ));
					if (!"".equals(ydL3Msg)) {
						sAvdYn = "N";			// 고도화 불가
						szLogMsg = ydL3Msg;
						commUtils.printLog(logId, szLogMsg , "SL");
						jsRtnWrkWaitCrn.getRecord(i).addField("AVD_YN"	, sAvdYn		);	// 고도화 가능여부 Set
						jsRtnWrkWaitCrn.getRecord(i).addField("AVD_MSG"	, szLogMsg		);	// 고도화 메세지 Set
						continue;
					}
					
					//수신 항목 값
					String ydWrkProgStat = commUtils.trim(jrRtnWrkWaitCrn.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
					String ydSchCd       = commUtils.trim(jrRtnWrkWaitCrn.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
					String ydCrnSchId    = commUtils.trim(jrRtnWrkWaitCrn.getFieldString("YD_CRN_SCH_ID"   )); //야드크레인스케쥴ID
					
					newStock   		= commUtils.trim(jrRtnWrkWaitCrn.getFieldString("STOCK_ID")); 
					newFromLoc 		= commUtils.trim(jrRtnWrkWaitCrn.getFieldString("FROM_LOC")); 
					newToLoc 		= commUtils.trim(jrRtnWrkWaitCrn.getFieldString("TO_LOC")); 
					
					lngSchUpXaxis = Long.parseLong(commUtils.nvl(jrRtnWrkWaitCrn.getFieldString("YD_UP_WO_LOC_XAXIS"),"0")); //스케쥴지시 권상위치
					lngSchDnXaxis = Long.parseLong(commUtils.nvl(jrRtnWrkWaitCrn.getFieldString("YD_DN_WO_LOC_XAXIS"),"0")); //스케쥴지시 권하위치
					
					szLogMsg = this.procCoilCrnSchChoiceYnNewF(logId, ydEqpId, jrRtnWrkWaitCrn, jrL2Crn);
					
					if(!"".equals(ydL3Msg)){
						sAvdYn = "N";		// 실패
						szLogMsg = ydL3Msg;
						commUtils.printLog(logId, szLogMsg , "SL");
						jsRtnWrkWaitCrn.getRecord(i).addField("AVD_YN"	, sAvdYn		);	// 고도화 가능여부 Set
						jsRtnWrkWaitCrn.getRecord(i).addField("AVD_MSG"	, szLogMsg		);	// 고도화 메세지 Set
						continue;
					}else{
						sAvdYn = "Y";
						szLogMsg = "성공:("+newStock +")  FROM :"+newFromLoc +"(" + lngSchUpXaxis + ") TO:"+newToLoc + "(" + lngSchDnXaxis + "))"+"<br>";
					}
					
					jsRtnWrkWaitCrn.getRecord(i).addField("AVD_YN"	, sAvdYn		);	// 고도화 가능여부
					jsRtnWrkWaitCrn.getRecord(i).addField("AVD_MSG"	, szLogMsg		);	// 고도화 메세지
					
				} // End Loop
				
				
			}else{
				for(int i = 0; i < jsRtnWrkWaitCrn.size(); i++) {	// Start loop
					sAvdYn = "N";		// 실패
					szLogMsg = "db error 고도화 불가 : L2 현재위치 없음 " + " [ " + ydEqpId  + " ]";
					commUtils.printLog(logId, szLogMsg , "SL");
					jsRtnWrkWaitCrn.getRecord(i).addField("AVD_YN"	, sAvdYn		);	// 고도화 가능여부 Set
					jsRtnWrkWaitCrn.getRecord(i).addField("AVD_MSG"	, szLogMsg		);	// 고도화 메세지 Set
				} // End Loop
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jsRtnWrkWaitCrn;
			
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 * 신고도화 크레인 스케쥴 결정 - A동(모니터링)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecordSet jsRtnWrkWaitCrn
	 * @param JDTORecord jrParam
	 * @return JDTORecordSet
	 * @throws JDTOException
	 */
    public String procCoilCrnSchChoiceYnNewA(String logId, String methodNms, String firstYdSchCd, String oldYdCrnSchId, String firstYdCrnSchId, String ydEqpId, JDTORecordSet jsL2Crn) throws JDTOException {
    	
    	String methodNm = "신고도화 크레인 스케쥴 결정 - A동(모니터링)[BCoilJspSeEJB.procCoilCrnSchChoiceYn] < ";
    	String szLogMsg			= "";
    	String rtnYdCrnSchId	= "";
    	JDTORecordSet jsAbleResult = JDTORecordFactory.getInstance().createRecordSet("");
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ			
		//----------------------------------------------------------------------------------------------------------------------
		commUtils.printLog(logId, methodNm, "S+");

		try {

			/**********************************************************
			* 2.1.2 대기[W] 인 경우
			* 2.1.2.1 첫번재 대상이 예외 스케쥴 인지 확인 
			*       - 예외 대상이면 기존 방식으로
			*       - 예외 대상이 아니면  상대크레인 작업 상태 확인
			**********************************************************/
			String otherWorkStat = "";
			String otherDirGp    = "";
			String otherCrnLoc   = "";
			long   lngCurrXaxis = 0;
			long   lngotherCurrXaxis = 0;
			long   lngotherFromXaxis = 0;
			long   lngotherToXaxis   = 0;
			long   lngSchUpXaxis     = 0;
			long   lngSchDnXaxis     = 0;
			long   craneGap  		 = 0;
			
			//조회 및 등록용
			JDTORecord jrL2Crn = JDTORecordFactory.getInstance().create();
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			
	    	//예외  대상 스케쥴  CHECK
			if(ymComm.chkRule(firstYdSchCd)&& !"".equals(firstYdSchCd)) {
				//첫번재 작업지시
				rtnYdCrnSchId = firstYdCrnSchId;
			} else if(jsL2Crn.size() <= 0 ){
				rtnYdCrnSchId = firstYdCrnSchId;
				szLogMsg = "L2 크레인 이동상태 조회 실패  >> 첫번재 대상재: "+ rtnYdCrnSchId ;
				commUtils.printLog(logId, szLogMsg, "SL");

			} else {
				/* 크레인작업지시요구 크레인스케줄 조회  */
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnSchSelectA 
				WITH DATA_TBL AS (
				  SELECT :V_YD_EQP_ID   AS YD_EQP_ID 
				       , CASE WHEN :V_YD_EQP_ID = '3ACRA1' THEN '3ACRA2' 
				              WHEN :V_YD_EQP_ID = '3ACRA2' THEN '3ACRA1' 
				              ELSE 'XX' END AS OTHER_YD_EQP_ID 
				    FROM DUAL
				)
				SELECT * 
				  FROM
				        (
				        SELECT CS.YD_EQP_ID  
				             , CS.YD_CRN_SCH_ID 
				             , CS.YD_WRK_PROG_STAT 
				             , CS.YD_SCH_CD 
				             , CS.YD_UP_WO_LOC  
				             , CS.YD_UP_WO_LAYER  
				             , CS.YD_DN_WO_LOC  
				             , CS.YD_DN_WO_LAYER  
				             , NVL(SR.ADV_CRN_PRIOR, CS.YD_SCH_PRIOR ) AS YD_SCH_PRIOR
				             , CASE WHEN CS.YD_UP_WO_LOC_XAXIS - YD_DN_WO_LOC_XAXIS < 0 THEN '+' 
				                    ELSE '-' END AS DIR_GP 
				             , NVL(CS.YD_UP_WO_LOC_XAXIS,0) AS YD_UP_WO_LOC_XAXIS -- 권상좌표
				             , CASE WHEN TO_NUMBER(NVL(CS.YD_UP_WO_LOC_XAXIS,0)) = 0 THEN 0 
				                    WHEN TO_NUMBER(NVL(CS.YD_UP_WO_LOC_XAXIS,0)) - TO_NUMBER(YR.DTL_ITM1) < 0       THEN 0 
				                    ELSE TO_NUMBER(NVL(CS.YD_UP_WO_LOC_XAXIS,0)) - TO_NUMBER(YR.DTL_ITM1) END AS UP_ABLE_MIN_XAXIS --권상간섭최소 값
				             , CASE WHEN TO_NUMBER(NVL(CS.YD_UP_WO_LOC_XAXIS,0)) = 0 THEN 999999 
				                    WHEN TO_NUMBER(NVL(CS.YD_UP_WO_LOC_XAXIS,0)) + TO_NUMBER(YR.DTL_ITM1) > 9999999 THEN 999999 
				                    ELSE TO_NUMBER(NVL(CS.YD_UP_WO_LOC_XAXIS,0)) + TO_NUMBER(YR.DTL_ITM1) END AS UP_ABLE_MAX_XAXIS --권상간섭최대 값  
				             , NVL(CS.YD_DN_WO_LOC_XAXIS,0) AS YD_DN_WO_LOC_XAXIS -- 권하 좌표 
				             , CASE WHEN TO_NUMBER(NVL(CS.YD_DN_WO_LOC_XAXIS,0)) = 0 THEN 0 
				                    WHEN TO_NUMBER(NVL(CS.YD_DN_WO_LOC_XAXIS,0)) - TO_NUMBER(YR.DTL_ITM1) < 0       THEN 0      
				                    ELSE TO_NUMBER(NVL(CS.YD_DN_WO_LOC_XAXIS,0)) - TO_NUMBER(YR.DTL_ITM1) END AS DN_ABLE_MIN_XAXIS --권하간섭최소 값
				             , CASE WHEN TO_NUMBER(NVL(CS.YD_DN_WO_LOC_XAXIS,0)) = 0 THEN 999999 
				                    WHEN TO_NUMBER(NVL(CS.YD_DN_WO_LOC_XAXIS,0)) + TO_NUMBER(YR.DTL_ITM1) > 9999999 THEN 999999 
				                    ELSE TO_NUMBER(NVL(CS.YD_DN_WO_LOC_XAXIS,0)) + TO_NUMBER(YR.DTL_ITM1) END AS DN_ABLE_MAX_XAXIS --권하간섭최대 값
				             , TO_NUMBER(YR.DTL_ITM1) AS GAP_XAXIS 
				             , CASE  
				                    -- 권상위치 2단이고 권하위치 1단인 경우 OK 
				                    WHEN YD_UP_WO_LAYER = '02' AND YD_DN_WO_LAYER = '01' THEN 'OK' 
				                    -- 권상위치 2단이고 권하위치 2단인 경우  권하위치 하단에 코일이 2개있는 경우 OK 
				                    WHEN YD_UP_WO_LAYER = '02' AND YD_DN_WO_LAYER = '02'  
				                         AND (SELECT DECODE(COUNT(STOCK_ID),2,'T','F')  
				                                FROM USRYMA.TB_YM_STACKLAYER 
				                               WHERE STACK_COL_GP     = SUBSTR(CS.YD_DN_WO_LOC,1,6) 
				                                 AND STACK_BED_GP     IN (SUBSTR(CS.YD_DN_WO_LOC,7,2), LPAD(TO_NUMBER(SUBSTR(CS.YD_DN_WO_LOC,7,2)) + 1,'2','0')) 
				                                 AND STACK_LAYER_GP   = '01' 
				                                 AND STACK_LAYER_STAT = 'C') = 'T' 
				                         THEN 'OK'  
				                    -- 권상위치 1단이고 권하위치 1단인 경우  권상위치 상단에 코일이 없는 경우 OK 
				                    WHEN YD_UP_WO_LAYER = '01' AND YD_DN_WO_LAYER = '01'  
				                         AND (SELECT DECODE(COUNT(STOCK_ID),1,'T','F')  
				                                FROM USRYMA.TB_YM_STACKLAYER 
				                               WHERE STACK_COL_GP     = SUBSTR(CS.YD_UP_WO_LOC,1,6) 
				                                 AND STACK_BED_GP     IN (SUBSTR(CS.YD_UP_WO_LOC,7,2), LPAD(TO_NUMBER(SUBSTR(CS.YD_UP_WO_LOC,7,2)) - 1,'2','0')) 
				                                 AND STACK_LAYER_GP   IN (CASE WHEN STACK_BED_GP = SUBSTR(CS.YD_UP_WO_LOC,7,2) THEN '01' ELSE '02' END,'02') 
				                             ) = 'T'  
				                          
				                         THEN 'OK'  
				                    -- 권상위치 1단이고 권하위치 2단인 경우  권상위치 상단에 코일이 없고 및 권하위치 하단에 코일이 2개인 경우 OK 
				                    WHEN YD_UP_WO_LAYER = '01'  AND YD_DN_WO_LAYER = '02'  
				                         AND (SELECT DECODE(COUNT(STOCK_ID),1,'T','F')  
				                                FROM USRYMA.TB_YM_STACKLAYER 
				                               WHERE STACK_COL_GP     = SUBSTR(CS.YD_UP_WO_LOC,1,6) 
				                                 AND STACK_BED_GP     IN (SUBSTR(CS.YD_UP_WO_LOC,7,2), LPAD(TO_NUMBER(SUBSTR(CS.YD_UP_WO_LOC,7,2)) - 1,'2','0')) 
				                                 AND STACK_LAYER_GP   IN (CASE WHEN STACK_BED_GP = SUBSTR(CS.YD_UP_WO_LOC,7,2) THEN '01' ELSE '02' END,'02') 
				                              ) = 'T'  
				                         AND (SELECT DECODE(COUNT(STOCK_ID),2,'T','F')  
				                                FROM USRYMA.TB_YM_STACKLAYER 
				                               WHERE STACK_COL_GP     = SUBSTR(CS.YD_DN_WO_LOC,1,6) 
				                                 AND STACK_BED_GP     IN (SUBSTR(CS.YD_DN_WO_LOC,7,2), LPAD(TO_NUMBER(SUBSTR(CS.YD_DN_WO_LOC,7,2)) + 1,'2','0')) 
				                                 AND STACK_LAYER_GP   = '01' 
				                                 AND STACK_LAYER_STAT = 'C') = 'T' 
				                         THEN 'OK'  
				                    ELSE 'NO' END        AS LOC_STAT 
				             , P_EQUIP.YD_EQP_ID         AS TARGET_EQUIP
				             , CASE WHEN EQ.EQUIP_GP = CS.YD_EQP_ID THEN 'Y'
				                    ELSE 'N'  END        AS REQ_YD_CRN_SCH_ID_YN    --요구크레인 작업여부
				             , EQ.YD_EQP_WRK_MODE2       AS EQ_YD_EQP_WRK_MODE2
				             , OTHER_EQ.YD_EQP_WRK_MODE2 AS OTHER_EQ_YD_EQP_WRK_MODE2
				             , EQ.WPROG_STAT              AS EQ_WORK_MODE
				             , OTHER_EQ.WPROG_STAT        AS OTHER_EQ_WORK_MODE
				             , (SELECT CD_CONTENTS FROM TB_YM_SCHEDULERULE WHERE YD_SCH_CD = CS.YD_SCH_CD AND ROWNUM <= 1)  AS SCH_NM
				             , (SELECT STOCK_ID    FROM TB_YM_CRNWRKMTL    WHERE YD_CRN_SCH_ID = CS.YD_CRN_SCH_ID AND ROWNUM <= 1) AS STOCK_ID
				             , CS.YD_UP_WO_LOC||CS.YD_UP_WO_LAYER  AS FROM_LOC
				             , CS.YD_DN_WO_LOC||CS.YD_DN_WO_LAYER  AS TO_LOC
				             , ROW_NUMBER() OVER(PARTITION BY CS.YD_SCH_CD ORDER BY CASE WHEN CS.YD_EQP_ID =  P_EQUIP.YD_EQP_ID THEN 1  
				                                                                         ELSE 2  END  ASC
				                                                                  , CS.YD_SCH_PRIOR
				                                                                  , TO_NUMBER(CS.YD_CRN_SCH_ID)) RANK_SEQ
				          FROM TB_YM_CRNSCH CS 
				             , DATA_TBL     P_EQUIP
				             , TB_YM_EQUIP  EQ  
				             , TB_YM_EQUIP  OTHER_EQ 
				             , (SELECT *  
				                  FROM USRYMA.TB_YM_RULE 
				                 WHERE REPR_CD_GP = 'CRNDIF') YR  --간섭범위    
				             , TB_YM_SCHEDULERULE SR    
				         WHERE 1=1 
				           AND CS.YD_SCH_CD = SR.YD_SCH_CD
				           AND CS.DEL_YN = 'N' 
				           AND CS.YD_WRK_PROG_STAT = 'W'                                               
				           AND CS.YD_EQP_ID IN ( P_EQUIP.YD_EQP_ID , P_EQUIP.OTHER_YD_EQP_ID )
				           AND P_EQUIP.YD_EQP_ID         = EQ.EQUIP_GP(+) 
				           AND P_EQUIP.OTHER_YD_EQP_ID   = OTHER_EQ.EQUIP_GP(+) 
				           AND SUBSTR(CS.YD_SCH_CD,3,2) NOT IN (SELECT NVL(DTL_ITM1,'1')
				                                                  FROM USRYMA.TB_YM_RULE
				                                                 WHERE REPR_CD_GP = 'APP013')  -- 제외 스케쥴
				           AND 1 = CASE WHEN SUBSTR(CS.YD_SCH_CD,3,2) = 'PT' AND  YD_TO_LOC_DCSN_MTD = 'S' AND SUBSTR(P_EQUIP.YD_EQP_ID,5,2) IN ('A2','C2') THEN 0
				                        ELSE 1 END
				           -- 작업구간
				           AND 1 = CASE WHEN P_EQUIP.YD_EQP_ID IN ('3ACRA1') AND (YD_UP_WO_LOC_XAXIS BETWEEN          1 AND 118329 + 10)
				                                                             AND (YD_DN_WO_LOC_XAXIS BETWEEN          1 AND 118329 + 10) THEN 1
				                        WHEN P_EQUIP.YD_EQP_ID IN ('3ACRA2') AND (YD_UP_WO_LOC_XAXIS BETWEEN 28069 - 10 AND 999999     ) 
				                                                             AND (YD_DN_WO_LOC_XAXIS BETWEEN 28069 - 10 AND 999999     ) THEN 1 
				                        ELSE 2 END 
				            )  A 
				       , DATA_TBL B 
				     WHERE 1 = 1
				       AND 1 = CASE WHEN SUBSTR(YD_SCH_CD,3,2) = 'PT' AND RANK_SEQ != 1 THEN 2 
				                    ELSE 1 END                
				     ORDER BY CASE WHEN A.YD_EQP_ID =  B.YD_EQP_ID THEN 1  
				                   ELSE 2  END  ASC
				            , A.YD_SCH_PRIOR
				            , TO_NUMBER(A.YD_CRN_SCH_ID)
				*/
				jrParam.setField("YD_EQP_ID"    , ydEqpId  		); 
				jrParam.setField("YD_CRN_SCH_ID", oldYdCrnSchId ); 
				JDTORecordSet jsSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnSchSelectA", logId, methodNm, "크레인스케줄 조회");
				
				if (jsSch.size() > 0) {			
				
					if(!(("A".equals(jsSch.getRecord(0).getFieldString("EQ_YD_EQP_WRK_MODE2"))) 
							&& ("A".equals(jsSch.getRecord(0).getFieldString("OTHER_EQ_YD_EQP_WRK_MODE2")))
							&& (!"B".equals(jsSch.getRecord(0).getFieldString("EQ_WORK_MODE")))
						    && (!"B".equals(jsSch.getRecord(0).getFieldString("OTHER_EQ_WORK_MODE")))))	{

							rtnYdCrnSchId = firstYdCrnSchId;
							szLogMsg = "L2 크레인 이동상태 조회 실패  >> 첫번재 대상재: "+ rtnYdCrnSchId;
							commUtils.printLog(logId, szLogMsg, "SL");		
						
					} else {
					
						for(int i = 1; i <= jsL2Crn.size(); i++) {
	
							jsL2Crn.absolute(i);
							jrL2Crn  = jsL2Crn.getRecord();
							if("1".equals(commUtils.trim(jrL2Crn.getFieldString("CRN_ST")))){ // --본크레인 
								otherCrnLoc 	= commUtils.trim(jrL2Crn.getFieldString("OTHER_CRN_LOC")); //상대크레인 위치정보('L':좌측,'R':우측)
								lngCurrXaxis	= Long.parseLong(commUtils.nvl(jrL2Crn.getFieldString("OTHER_CURR_XAXIS"),"0")); //크레인현재위치
							} else {
								//상대크레인 정보
								otherWorkStat 	= commUtils.trim(jrL2Crn.getFieldString("OTHER_WORK_STAT")); //권상전후 상태
								otherDirGp    	= commUtils.trim(jrL2Crn.getFieldString("OTHER_DIR_GP"));    //작업방향
								lngotherCurrXaxis= Long.parseLong(commUtils.nvl(jrL2Crn.getFieldString("OTHER_CURR_XAXIS"),"0")); //크레인현재위치
								lngotherFromXaxis= Long.parseLong(commUtils.nvl(jrL2Crn.getFieldString("OTHER_FROM_XAXIS"),"0")); //크레인from 위치
								lngotherToXaxis  = Long.parseLong(commUtils.nvl(jrL2Crn.getFieldString("OTHER_TO_XAXIS"),"0"));   //크레인to 위치
							}
						}					
						
						// 대상 스케쥴  정보  
						String locStat         	= "";
						String newYdCrnSchId  	= "";
						String newCrnSchNm   	= "";
						String newFromLoc   	= "";
						String newToLoc   		= "";
						String SchYdEqpId       = ""; 
						
						JDTORecord jrSch = JDTORecordFactory.getInstance().create();
						for(int i = 1; i <= jsSch.size(); i++) {
							jsSch.absolute(i);
							jrSch  = jsSch.getRecord();
							newYdCrnSchId 	= commUtils.trim(jrSch.getFieldString("YD_CRN_SCH_ID")); //대상여부
							newCrnSchNm   	= commUtils.trim(jrSch.getFieldString("SCH_NM")); 
							newFromLoc 		= commUtils.trim(jrSch.getFieldString("FROM_LOC")); 
							newToLoc 		= commUtils.trim(jrSch.getFieldString("TO_LOC")); 
							SchYdEqpId    	= commUtils.trim(jrSch.getFieldString("YD_EQP_ID")); //대상여부
							lngSchUpXaxis = Long.parseLong(commUtils.nvl(jrSch.getFieldString("YD_UP_WO_LOC_XAXIS"),"0")); //대상권상위치
							lngSchDnXaxis = Long.parseLong(commUtils.nvl(jrSch.getFieldString("YD_DN_WO_LOC_XAXIS"),"0")); //대상권하위치
							locStat 	  = commUtils.trim(jrSch.getFieldString("LOC_STAT")); //대상여부
							craneGap      = Long.parseLong(commUtils.nvl(jrSch.getFieldString("GAP_XAXIS"  ),"0")); // 간섭+-

							if(!locStat.equals("OK")) {
								commUtils.printLog(logId,  "실패:("+newCrnSchNm +"  작업설비:"+SchYdEqpId +"FROM :"+newFromLoc +"  TO:"+newToLoc + ") 상단에 코일이 있거나 하단에 코일이 없는 경우", "SL");
								szLogMsg =  "실패:("+newCrnSchNm +"  작업설비:"+SchYdEqpId +"FROM :"+newFromLoc +"  TO:"+newToLoc + ") 상단에 코일이 있거나 하단에 코일이 없는 경우";
								
							} else {
								if(otherWorkStat.equals("1")) {
									//권상 전	'+ = +', '- = -'
									/**********************************************************
									* 권상전 인 경우
									* 1.동일 방향 대상선택
									* 2.현재크레인 위치 - 대상권상위치가 짧은 거리
									* 3.대상크레인이 좌측인 경우 
									*   - 상대크레인 작업권상 < 대상작업권상 인 대상
									* 4.대상크레인이 우측인 경우  
									*   - 상대크레인 작업권상 > 대상작업권상 인 대상 
									**********************************************************/
									
									if(otherDirGp.equals(commUtils.trim(jrSch.getFieldString("DIR_GP")))){ // 방향이 같은 경우
	
										if(otherCrnLoc.equals("L")) {  //상대 크레인이 좌측인 경우
											if( (lngotherFromXaxis+craneGap) <  lngSchUpXaxis ) {

												jrSch.setField("DISTAN"	, ""+ Math.abs(lngCurrXaxis - lngSchUpXaxis) );  //현재크레인 위치 - 대상권상위치
												jsAbleResult.addRecord(jrSch);
	
												//szLogMsg = "성공:("+newCrnSchNm +"  작업설비:"+SchYdEqpId +"FROM :"+newFromLoc +"  TO:"+newToLoc + ")권상전/같은 방향/상대가 좌측/상대크레인 작업권상 ("+ lngotherFromXaxis+ ")< 대상작업권상("+ lngSchUpXaxis+")인 대상 인경우";
												//szLogMsg = szLogMsg + "... 거리:" + (Math.abs(lngCurrXaxis - lngSchUpXaxis));
												commUtils.printLog(logId, szLogMsg, "SL");
	
												
											} else {
												szLogMsg = "실패:("+newCrnSchNm +"  작업설비:"+SchYdEqpId +"FROM :"+newFromLoc +"  TO:"+newToLoc + ")권상전/같은 방향/상대가 좌측/상대크레인 작업권상 ("+ lngotherFromXaxis+ ")< 대상작업권상("+ lngSchUpXaxis+")인 대상 아닌경우";
												commUtils.printLog(logId, szLogMsg , "SL");
											}
										} else {
											if(( lngotherFromXaxis-craneGap) >  lngSchUpXaxis ) {
												jrSch.setField("DISTAN"	, ""+ Math.abs(lngCurrXaxis - lngSchUpXaxis) );  //현재크레인 위치 - 대상권상위치
												jsAbleResult.addRecord(jrSch);
												
												//szLogMsg = "성공:("+newCrnSchNm +"  작업설비:"+SchYdEqpId +"FROM :"+newFromLoc +"  TO:"+newToLoc + ")권상전/같은 방향/상대가우측/상대크레인 작업권상 ("+ lngotherFromXaxis+ ")< 대상작업권상("+ lngSchUpXaxis+")인 대상 인경우";
												//szLogMsg = szLogMsg + "... 거리:" + (Math.abs(lngCurrXaxis - lngSchUpXaxis));
												commUtils.printLog(logId, szLogMsg, "SL");
	
											} else {
												szLogMsg = "실패:("+newCrnSchNm +"  작업설비:"+SchYdEqpId +"FROM :"+newFromLoc +"TO:"+newToLoc + ")권상전/같은 방향/상대가우측/상대크레인 작업권상 ("+ lngotherFromXaxis+ ")> 대상작업권상("+ lngSchUpXaxis+")인 대상 아닌경우";
												commUtils.printLog(logId, szLogMsg, "SL");
											}
										}
									} else {
										commUtils.printLog(logId,  "실패:("+newCrnSchNm +"  작업설비:"+SchYdEqpId +"FROM :"+newFromLoc +"  TO:"+newToLoc + ")권상전/다른방향 인 경우", "SL");
										szLogMsg =  "실패:("+newCrnSchNm +"  작업설비:"+SchYdEqpId +"FROM :"+newFromLoc +"  TO:"+newToLoc + ")권상전/다른방향 인 경우" +"\r\n";
									}
								} else {
								//권상 후	
									/**********************************************************
									* 권상후 인 경우
									* 1.역 방향 대상선택
									* 2.현재크레인 위치 - 대상권상위치가 짧은 거리
									* 3.대상크레인이 좌측인 경우 
									*   - 상대크레인 작업권하 < 대상작업권상 이고 작업권하 < 대상작업권하 이고  인 경우
									* 4.대상크레인이 우측인 경우 
									*   - 상대크레인 작업권하 > 대상작업권상 이고 작업권하 > 대상작업권하 이고  인 경우
									**********************************************************/
									if(!otherDirGp.equals(commUtils.trim(jrSch.getFieldString("DIR_GP")))){ // 방향이 틀린 경우
										
										if(otherCrnLoc.equals("L")) {  //상대 크레인이 좌측인 경우
											
											if( (lngotherToXaxis+craneGap) < lngSchUpXaxis) {
												jrSch.setField("DISTAN"	, ""+ Math.abs(lngCurrXaxis - lngSchUpXaxis) );  //현재크레인 위치 - 대상권상위치
												jsAbleResult.addRecord(jrSch);
	
												//szLogMsg = "성공("+newCrnSchNm +"  작업설비:"+SchYdEqpId +"FROM :"+newFromLoc +"  TO:"+newToLoc + ")권상후/다른 방향/상대가 좌측/상대크레인 작업권하("+ lngotherToXaxis+ ")< 대상작업권상("+ lngSchUpXaxis+") 이고";
												//szLogMsg = szLogMsg + "상대크레인 작업권하("+ lngotherToXaxis+ ") < 대상작업권하("+ lngSchUpXaxis+")인 경우... 거리:" + (Math.abs(lngCurrXaxis - lngSchUpXaxis));
												commUtils.printLog(logId, szLogMsg, "SL");
											} else {
												szLogMsg = "실패:("+newCrnSchNm +"  작업설비:"+SchYdEqpId +"FROM :"+newFromLoc +"  TO:"+newToLoc + ")권상후/다른 방향/상대가 좌측/상대크레인 작업권하("+ lngotherToXaxis+ ")< 대상작업권상("+ lngSchUpXaxis+") 이고";
												szLogMsg = szLogMsg + "상대크레인 작업권하("+ lngotherToXaxis+ ") < 대상작업권하("+ lngSchUpXaxis+")인 아닌 경우";
												commUtils.printLog(logId, szLogMsg, "SL");
												
											}
										} else {
											if( (lngotherToXaxis-craneGap) > lngSchUpXaxis) {
												jrSch.setField("DISTAN"	, ""+ Math.abs(lngCurrXaxis - lngSchUpXaxis) );  //현재크레인 위치 - 대상권상위치
												jsAbleResult.addRecord(jrSch);
												
												//szLogMsg = "성공:("+newCrnSchNm +"  작업설비:"+SchYdEqpId +"FROM :"+newFromLoc +"  TO:"+newToLoc + ")권상후/다른 방향/상대가 우측/상대크레인 작업권하("+ lngotherToXaxis+ ") > 대상작업권상("+ lngSchUpXaxis+") 이고";
												//szLogMsg = szLogMsg + "상대크레인 작업권하("+ lngotherToXaxis+ ") > 대상작업권하("+ lngSchUpXaxis+")인  경우... 거리:" + (Math.abs(lngCurrXaxis - lngSchUpXaxis));
												commUtils.printLog(logId, szLogMsg, "SL");
												
											} else {
												szLogMsg = "실패:("+newCrnSchNm +"  작업설비:"+SchYdEqpId +"FROM :"+newFromLoc +"  TO:"+newToLoc + ")권상후/다른 방향/상대가 우측/상대크레인 작업권하("+ lngotherToXaxis+ ") > 대상작업권상("+ lngSchUpXaxis+") 이고";
												szLogMsg = szLogMsg + "상대크레인 작업권하("+ lngotherToXaxis+ ") > 대상작업권하("+ lngSchUpXaxis+")인 아닌 경우";
												commUtils.printLog(logId, szLogMsg, "SL");
											}
										}
									} else {
										commUtils.printLog(logId,  "실패:("+newCrnSchNm +"  작업설비:"+SchYdEqpId +"FROM :"+newFromLoc +"  TO:"+newToLoc + ")권상후/다른방향 인경우", "SL");
										szLogMsg = "실패:("+newCrnSchNm +"  작업설비:"+SchYdEqpId +"FROM :"+newFromLoc +"  TO:"+newToLoc + ")권상후/다른방향 인경우"+"\r\n";
									}
								}
	
							}
						}
						
						if(jsAbleResult.size() <= 0) {
							//첫번째 대상재 선택
							rtnYdCrnSchId = firstYdCrnSchId;
							commUtils.printLog(logId,  "상대크레인 상태조건에 맞는 대상이 없음 ", "SL");
							szLogMsg = "실패:첫번째 대상재 선택 : "+ rtnYdCrnSchId + "상대크레인 상태조건에 맞는 대상이 없음"+"\r\n";
						} else {
							
		     			    // 거리SORT = 1
							commUtils.printParam(logId, jsAbleResult);
		     				JDTORecord	    jrAbleResult	= JDTORecordFactory.getInstance().create();
		     				JDTORecord	    jrResult		= JDTORecordFactory.getInstance().create();
		     				
		     				int iMinDistan = 999999;
		     				for(int i = 1; i <= jsAbleResult.size(); i++) {
		     					jsAbleResult.absolute(i);
		     					jrAbleResult  = jsAbleResult.getRecord();
		     					int iDisTan = Integer.parseInt(commUtils.trim(jrAbleResult.getFieldString("DISTAN"  )));
		     					if(iMinDistan > iDisTan){  // 최고 가까운 거리 CHECK
		     						jrResult = jrAbleResult;
			     					iMinDistan = iDisTan;
		     					} 
		     				}
							commUtils.printLog(logId, "거리별 Sort ", "SL");
	
							commUtils.printParam(logId, jrResult);
		     				if("".equals(commUtils.trim(jrResult.getFieldString("YD_CRN_SCH_ID")))) {
		     					//첫번째 대상재 선택
								rtnYdCrnSchId = firstYdCrnSchId;
								szLogMsg = "실패:첫번째 대상재 선택 : "+ rtnYdCrnSchId + "거리별 Sort 이상";
		     				}
		     				commUtils.printLog(logId, "거리별 Sort 결과 ("+newCrnSchNm +"FROM :"+newFromLoc +"  TO:"+newToLoc + ")", "SL");
							
						}	
					}	
				} else {
					rtnYdCrnSchId = firstYdCrnSchId; //20170928
				}
			}

			commUtils.printLog(logId, "최종 결과 ("+ szLogMsg + ")", "SL");
			commUtils.printLog(logId, methodNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }//end of try~catch				
		return szLogMsg;
	}
    
    /**
	 * 신고도화 크레인 스케쥴 결정 - C,E동(모니터링)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecordSet jsRtnWrkWaitCrn
	 * @param JDTORecord jrParam
	 * @return JDTORecordSet
	 * @throws JDTOException
	 */
    public String procCoilCrnSchChoiceYnNewF(String logId, String ydEqpId,JDTORecord jrRtnWrkWaitCrn, JDTORecord jrL2Crn) throws JDTOException {
    	
    	String methodNm = "신고도화 크레인 스케쥴 결정 - C,E동(모니터링)[BCoilJspSeEJB.procCoilCrnSchChoiceYnNewF] < ";
    	
    	String szLogMsg	= "";
    	String szDBLogMsg 		= "";

		long   lngSchUpXaxis   = 0;
		long   lngSchDnXaxis   = 0;
		long   lngAdvCrnPrior  = 99;
		long   lngMinAdvCrnPrior  = 99;
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+");

			//조회 및 등록용
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			String targetCrnSt 	 		= commUtils.trim(jrL2Crn.getFieldString("TARGET_CRN_ST"  			)); //
			String targetEquipGp 	 	= commUtils.trim(jrL2Crn.getFieldString("TARGET_EQUIP_GP"  			)); //대상크레인ID
			String targetWorkStat 	 	= commUtils.trim(jrL2Crn.getFieldString("TARGET_WORK_STAT"  		)); //대상크레인작업구분(1:권상전,2:권상후,3:이상)
			String targetWrkProgStat	= commUtils.trim(jrL2Crn.getFieldString("TARGET_CRN_WRK_PROG_STAT"	)); //대상크레인작업상태
			long targetCurrXaxis 	 	= Long.parseLong(commUtils.nvl(jrL2Crn.getFieldString("TARGET_CURR_XAXIS"),"0")); //
			long targetFromXaxis 	 	= Long.parseLong(commUtils.nvl(jrL2Crn.getFieldString("TARGET_FROM_XAXIS"),"0")); //
			long targetToXaxis 	 	    = Long.parseLong(commUtils.nvl(jrL2Crn.getFieldString("TARGET_TO_XAXIS"  ),"0")); //
			
			String otherCrnSt	 		= commUtils.trim(jrL2Crn.getFieldString("OTHER_CRN_ST"  			)); //
			String otherEquipGp 	 	= commUtils.trim(jrL2Crn.getFieldString("OTHER_EQUIP_GP"  			)); //상대크레인ID
			String otherWorkStat 	 	= commUtils.trim(jrL2Crn.getFieldString("OTHER_WORK_STAT"  			)); //상대크레인작업구분(1:권상전,2:권상후,3:이상)
			String otherWrkProgStat 	= commUtils.trim(jrL2Crn.getFieldString("OTHER_CRN_WRK_PROG_STAT"  	)); //대상크레인작업상태
			long otherCurrXaxis 	 	= Long.parseLong(commUtils.nvl(jrL2Crn.getFieldString("OTHER_CURR_XAXIS"),"0")); //
			long otherFromXaxis 	 	= Long.parseLong(commUtils.nvl(jrL2Crn.getFieldString("OTHER_FROM_XAXIS"),"0")); //
			long otherToXaxis 	 		= Long.parseLong(commUtils.nvl(jrL2Crn.getFieldString("OTHER_TO_XAXIS"  ),"0")); //
			String otherDirGp 	 		= commUtils.trim(jrL2Crn.getFieldString("OTHER_DIR_GP"  			)); //
			String otherCrnLoc	 		= commUtils.trim(jrL2Crn.getFieldString("OTHER_CRN_LOC"  			)); //
			long craneGap 	 			= Long.parseLong(commUtils.nvl(jrL2Crn.getFieldString("CRANE_GAP"  ),"0")); //
			
			long RYdBayGpX 	 			= Long.parseLong(commUtils.nvl(jrL2Crn.getFieldString("R_YD_BAY_GP_X"  ),"999999")); //
			
			commUtils.printLog(logId, szDBLogMsg, "SL");
			
			// 대상 스케쥴  정보  
			String newCrnSchNm   	= "";
			String newFromLoc   	= "";
			String newToLoc   		= "";
			String ydUpWoLoc 		= ""; 
			String ydUpWoLayer 		= "";
			String ydDnWoLoc 	 	= "";
			String ydDnWoLayer 		= ""; 
			String sFirstCar 		= ""; 
			String sLastDCLinOff    = "";
			String sLastSPM2LinOff  = "";
			String sLocCntYn        = "";
			String sSpmSeqYn        = "";
			String sEcSeqYn         = "";
			
			newCrnSchNm   	= commUtils.trim(jrRtnWrkWaitCrn.getFieldString("SCH_NM")); 
			newFromLoc 		= commUtils.trim(jrRtnWrkWaitCrn.getFieldString("FROM_LOC")); 
			newToLoc 		= commUtils.trim(jrRtnWrkWaitCrn.getFieldString("TO_LOC")); 
			ydUpWoLoc 		= commUtils.trim(jrRtnWrkWaitCrn.getFieldString("YD_UP_WO_LOC"     )); //권상위치 
			ydUpWoLayer 	= commUtils.trim(jrRtnWrkWaitCrn.getFieldString("YD_UP_WO_LAYER"   )); //권상위치단
			ydDnWoLoc 	 	= commUtils.trim(jrRtnWrkWaitCrn.getFieldString("YD_DN_WO_LOC"     )); //권하위치
			ydDnWoLayer 	= commUtils.trim(jrRtnWrkWaitCrn.getFieldString("YD_DN_WO_LAYER"   )); //권하위치단
			
			sFirstCar 		= commUtils.trim(jrRtnWrkWaitCrn.getFieldString("FIRST_CAR"        )); //
			sLastDCLinOff   = commUtils.trim(jrRtnWrkWaitCrn.getFieldString("DC_LINE_OFF_MAX"  )); //
			sLastSPM2LinOff = commUtils.trim(jrRtnWrkWaitCrn.getFieldString("SPM2_LINE_OFF_MAX")); //
			sLocCntYn 		= commUtils.trim(jrRtnWrkWaitCrn.getFieldString("LOC_CNT_YN")); //
			sSpmSeqYn 		= commUtils.trim(jrRtnWrkWaitCrn.getFieldString("SPM_LINE_IN_MIN")); //
			sEcSeqYn 		= commUtils.trim(jrRtnWrkWaitCrn.getFieldString("EC_LINE_OFF_MAX")); //
			
			lngAdvCrnPrior= Long.parseLong(commUtils.nvl(jrRtnWrkWaitCrn.getFieldString("ADV_CRN_PRIOR"),"99")); //고도화 우선순위
			lngSchUpXaxis = Long.parseLong(commUtils.nvl(jrRtnWrkWaitCrn.getFieldString("YD_UP_WO_LOC_XAXIS"),"0")); //스케쥴지시 권상위치
			lngSchDnXaxis = Long.parseLong(commUtils.nvl(jrRtnWrkWaitCrn.getFieldString("YD_DN_WO_LOC_XAXIS"),"0")); //스케쥴지시 권하위치
			
			/**********************************************************
			* 1. 기본적인 상하단 Check
			**********************************************************/
			if(ydUpWoLoc.length() != 8) {
				szLogMsg = "실패:("+newCrnSchNm +"  작업설비:"+ydEqpId +"  FROM :"+newFromLoc +"  TO:"+newToLoc + ")권상위치이상-불가";
				commUtils.printLog(logId, szLogMsg , "SL");
				return szLogMsg;
			}
			if(ydDnWoLoc.length() != 8) {
				szLogMsg = "실패:("+newCrnSchNm +"  작업설비:"+ydEqpId +"  FROM :"+newFromLoc +"  TO:"+newToLoc + ")권하위치이상-불가";
				commUtils.printLog(logId, szLogMsg , "SL");
				return szLogMsg;
			}
			
			if(!sFirstCar.equals("T")) {
				szLogMsg = "실패:("+newCrnSchNm +"  작업설비:"+ydEqpId +"  FROM :"+newFromLoc +"  TO:"+newToLoc + ")먼저 들어온 차량이 있습니다.";
				commUtils.printLog(logId, szLogMsg , "SL");
				return szLogMsg;
			}
			
			if(!"T".equals(sLastDCLinOff)) {
				szLogMsg = "실패:("+newCrnSchNm +"  작업설비:"+ydEqpId +"  FROM :"+newFromLoc +"  TO:"+newToLoc + ")DCLineOff 마지막스케줄이 아닙니다.";
				commUtils.printLog(logId, szLogMsg , "SL");
				return szLogMsg;
			}
			
			if(!"T".equals(sLastSPM2LinOff)) {
				szLogMsg = "실패:("+newCrnSchNm +"  작업설비:"+ydEqpId +"  FROM :"+newFromLoc +"  TO:"+newToLoc + ")SPM2추출 마지막스케줄이 아닙니다.";
				commUtils.printLog(logId, szLogMsg , "SL");
				return szLogMsg;
			}
			if(!"T".equals(sLocCntYn)) {
				szLogMsg = "실패:("+newCrnSchNm +"  작업설비:"+ydEqpId +"  FROM :"+newFromLoc +"  TO:"+newToLoc + ")크레인 작업 범위가 아닙니다.";
				commUtils.printLog(logId, szLogMsg , "SL");
				return szLogMsg;
			}
			if(!"T".equals(sSpmSeqYn)) {
				szLogMsg = "실패:("+newCrnSchNm +"  작업설비:"+ydEqpId +"  FROM :"+newFromLoc +"  TO:"+newToLoc + ")SPM 보급순서가 빠른 정보가 있습니다..";
				commUtils.printLog(logId, szLogMsg , "SL");
				return szLogMsg;
			}
			if(!"T".equals(sEcSeqYn)) {
				szLogMsg = "실패:("+newCrnSchNm +"  작업설비:"+ydEqpId +"  FROM :"+newFromLoc +"  TO:"+newToLoc + ")ECLineOff중 우선작업이 있습니다...";
				commUtils.printLog(logId, szLogMsg , "SL");
				return szLogMsg;
			}
			
			if(lngMinAdvCrnPrior >= lngAdvCrnPrior) {
				lngMinAdvCrnPrior = lngAdvCrnPrior;
			} else {
				szLogMsg = "실패:("+newCrnSchNm +"  작업설비:"+ydEqpId +"  FROM :"+newFromLoc +"  TO:"+newToLoc +"  MIN:"+lngMinAdvCrnPrior +"  AVE:"+lngAdvCrnPrior + ") 작업우선순위가 후순위 입니다..";
				commUtils.printLog(logId, szLogMsg , "SL");
				return szLogMsg;
			}
			
			if(ydUpWoLayer.equals("01")) {
				jrParam.setField("STACK_COL_GP" , ydUpWoLoc.substring(0, 6)); //권상위치
				jrParam.setField("STACK_BED_GP" , ydUpWoLoc.substring(6, 8)); //권상위치단
				/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getLoc1DanChk 
				-- 1단일 경우 가능여부:2단 상단 CHECK  
				SELECT DECODE(COUNT(*),0,'Y','N') AS ABLE_YN
				  FROM TB_YM_STACKLAYER
				 WHERE STACK_COL_GP = :V_STACK_COL_GP
				   AND STACK_BED_GP IN (:V_STACK_BED_GP, LPAD(TO_NUMBER(:V_STACK_BED_GP) - 1, 2,'0'))
				   AND STACK_LAYER_GP = '02'
				   AND STOCK_ID IS NOT NULL
				*/   
				JDTORecordSet jsLocChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getLoc1DanChk", logId, methodNm, "1단 CHECK");
					
				if (jsLocChk.size() > 0) {
					if( jsLocChk.getRecord(0).getFieldString("ABLE_YN").equals("N") ) {
						szLogMsg = "실패:("+newCrnSchNm +"  작업설비:"+ydEqpId +"  FROM :"+newFromLoc +"  TO:"+newToLoc + ")권상상단에 코일이 있음-불가";
						commUtils.printLog(logId, szLogMsg , "SL");
						return szLogMsg;
					}
				}
			}
			
			if(ydDnWoLayer.equals("01")) {
				jrParam.setField("STACK_COL_GP" , ydDnWoLoc.substring(0, 6)); //권하위치
				jrParam.setField("STACK_BED_GP" , ydDnWoLoc.substring(6, 8)); //권하위치단
				/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getLoc1DanChk 
				-- 1단일 경우 가능여부:2단 상단 CHECK  
				SELECT DECODE(COUNT(*),0,'Y','N') AS ABLE_YN
				  FROM TB_YM_STACKLAYER
				 WHERE STACK_COL_GP = :V_STACK_COL_GP
				   AND STACK_BED_GP IN (:V_STACK_BED_GP, LPAD(TO_NUMBER(:V_STACK_BED_GP) - 1, 2,'0'))
				   AND STACK_LAYER_GP = '02'
				   AND STOCK_ID IS NOT NULL
				*/   
				JDTORecordSet jsLocChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getLoc1DanChk", logId, methodNm, "1단 CHECK");
					
				if (jsLocChk.size() > 0) {
					if( jsLocChk.getRecord(0).getFieldString("ABLE_YN").equals("N") ) {
						szLogMsg = "실패:("+newCrnSchNm +"  작업설비:"+ydEqpId +"  FROM :"+newFromLoc +"  TO:"+newToLoc + ")권하상단에 코일이 있음-불가";
						commUtils.printLog(logId, szLogMsg , "SL");
						return szLogMsg;
						
					}
				}
			}
			
			if(ydDnWoLayer.equals("02")) {
				jrParam.setField("STACK_COL_GP" , ydDnWoLoc.substring(0, 6)); //권하위치
				jrParam.setField("STACK_BED_GP" , ydDnWoLoc.substring(6, 8)); //권하위치단
				/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getLoc2DanChk  
				-- 2단일 경우 가능여부:1단 하단 CHECK  
				SELECT DECODE(COUNT(*),2,'Y','N') AS ABLE_YN
				  FROM TB_YM_STACKLAYER
				 WHERE STACK_COL_GP = :V_STACK_COL_GP
				   AND STACK_BED_GP IN (:V_STACK_BED_GP, LPAD(TO_NUMBER(:V_STACK_BED_GP) + 1, 2,'0'))
				   AND STACK_LAYER_GP = '01'
				   AND STOCK_ID IS NOT NULL
				   AND STACK_LAYER_STAT = 'C'
				*/   
				JDTORecordSet jsLocChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getLoc2DanChk", logId, methodNm, "2단 CHECK");
					
				if (jsLocChk.size() > 0) {
					if( jsLocChk.getRecord(0).getFieldString("ABLE_YN").equals("N") ) {
						szLogMsg = "실패:("+newCrnSchNm +"  작업설비:"+ydEqpId +"  FROM :"+newFromLoc +"  TO:"+newToLoc + ")권하하단에 코일이 없음-불가";
						commUtils.printLog(logId, szLogMsg , "SL");
						return szLogMsg;
						
					}
				}
			}
			
			if(otherWorkStat.equals("0")) {
				//상대 크레인 작업 대기인 경우(성공)
				// 성공
				
			} else if(otherWorkStat.equals("1")) {
				//권상 전	'+ = +', '- = -'
				/**********************************************************
				* 권상전 인 경우
				* 1.동일 방향 대상선택
				* 2.현재크레인 위치 - 대상권상위치가 짧은 거리
				* 3.대상크레인이 좌측인 경우 
				*   - 상대크레인 작업권상 < 대상작업권상 인 대상
				* 4.대상크레인이 우측인 경우  
				*   - 상대크레인 작업권상 > 대상작업권상 인 대상 
				**********************************************************/
				
				if(otherCrnLoc.equals("L")) {  //상대 크레인이 좌측인 경우
					 
//					if(((otherFromXaxis+craneGap) <  lngSchUpXaxis) && ((otherToXaxis+craneGap) < lngSchDnXaxis)) {  //권상위치가 상대크레인 권상위치 보다 크고 권하위치가 상대크레인 권하위치 보다 큰경우
					if(((otherFromXaxis+craneGap) <  lngSchUpXaxis) ) {  //권상위치가 상대크레인 권상위치 보다 크고 권하위치가 상대크레인 권하위치 보다 큰경우
						// 성공
						
					} else {
						
						szLogMsg = "실패:("+newCrnSchNm +"  작업설비:"+ydEqpId +"  FROM :"+newFromLoc +"(" + lngSchUpXaxis + ") TO:"+newToLoc + "(" + lngSchDnXaxis + "))"; 
						if((otherFromXaxis+craneGap) >=  lngSchUpXaxis) {
							szLogMsg = szLogMsg + "상대 크레인 권상좌표+GAP(" + (otherFromXaxis+craneGap) +") >=  "+ "지시권상좌표" + "(" + lngSchUpXaxis + ") 간섭걸림:";
						} else if((otherToXaxis+craneGap) >=  lngSchDnXaxis) {
							szLogMsg = szLogMsg + "상대 크레인 권하좌표+GAP(" + (otherToXaxis  +craneGap) +") >=  "+ "지시권하좌표" + "(" + lngSchDnXaxis + ") 간섭걸림:";
						}
						
						szLogMsg = szLogMsg + "상대가 좌측/권상전/권상위치가 상대크레인 권상위치+GAP 보다 크고 권하위치가 상대크레인 권하위치+GAP 보다 큰 대상에 위배";
						commUtils.printLog(logId, szLogMsg , "SL");
						return szLogMsg;
					}
				} else {
					
					if(((otherFromXaxis-craneGap) >  lngSchUpXaxis)) {  //권상위치가 상대크레인 권상위치 보다 작고 권하위치가 상대크레인 권하위치 보다 작은경우
						// 성공
						
					} else {
						szLogMsg = "실패:("+newCrnSchNm +"  작업설비:"+ydEqpId +"  FROM :"+newFromLoc +"(" + lngSchUpXaxis + ") TO:"+newToLoc + "(" + lngSchDnXaxis + "))";
						
						if((otherFromXaxis-craneGap) <=  lngSchUpXaxis) {
							szLogMsg = szLogMsg + "상대 크레인 권상좌표-GAP(" + (otherFromXaxis-craneGap) +") <= "+ "지시권상좌표" + "(" + lngSchUpXaxis + ") 간섭걸림:";
						} else if((otherToXaxis-craneGap) <=  lngSchDnXaxis) {
							szLogMsg = szLogMsg + "상대 크레인 권하좌표-GAP(" + (otherToXaxis  -craneGap) +") <= "+ "지시권하좌표" + "(" + lngSchDnXaxis + ") 간섭걸림:";
						}
						
						szLogMsg = szLogMsg + "상대가 우측/권상전/권상위치가 상대크레인 권상위치-GAP 보다 작고 권하위치가 상대크레인 권하위치-GAP 보다 작은 대상에 위배";
						commUtils.printLog(logId, szLogMsg, "SL");
						return szLogMsg;
					}
				}
			} else {
			//권상 후	
				/**********************************************************
				* 권상후 인 경우
				* 1.역 방향 대상선택
				* 2.현재크레인 위치 - 대상권상위치가 짧은 거리
				* 3.대상크레인이 좌측인 경우 
				*   - 상대크레인 작업권하 < 대상작업권상 이고 작업권하 < 대상작업권하 이고  인 경우
				* 4.대상크레인이 우측인 경우 
				*   - 상대크레인 작업권하 > 대상작업권상 이고 작업권하 > 대상작업권하 이고  인 경우
				**********************************************************/
				if(otherCrnLoc.equals("L")) {  //상대 크레인이 좌측인 경우
					
					//if(((otherToXaxis+craneGap) < lngSchUpXaxis) && ((otherToXaxis+craneGap) < lngSchDnXaxis)) {
					if(((otherToXaxis+craneGap) < lngSchUpXaxis)) {
						//성공
					} else {
						szLogMsg = "실패:("+newCrnSchNm +"  작업설비:"+ydEqpId +"  FROM :"+newFromLoc +"(" + lngSchUpXaxis + ") TO:"+newToLoc + "(" + lngSchDnXaxis + "))";
						if((otherToXaxis+craneGap) >=  lngSchUpXaxis) {
							szLogMsg = szLogMsg + "상대 크레인 권하좌표+GAP(" + (otherToXaxis  +craneGap) +") >=  "+ "지시권상좌표" + "(" + lngSchUpXaxis + ") 간섭걸림:";
						}
						szLogMsg = szLogMsg + "상대가 좌측/권상후/권상권하위치가 상대크레인 권하위치보다 큰 대상에 위배";
						commUtils.printLog(logId, szLogMsg, "SL");
						return szLogMsg;
					}
				} else {
					if(((otherToXaxis-craneGap) > lngSchUpXaxis)) {
						// 성공
					} else {
						szLogMsg = "실패:("+newCrnSchNm +"  작업설비:"+ydEqpId +"  FROM :"+newFromLoc +"(" + lngSchUpXaxis + ") TO:"+newToLoc + "(" + lngSchDnXaxis + "))";
						if((otherToXaxis-craneGap) <=  lngSchUpXaxis) {
							szLogMsg = szLogMsg + "상대 크레인 권하좌표-GAP(" + (otherToXaxis  -craneGap) +") <= "+ "지시권상좌표" + "(" + lngSchUpXaxis + ") 간섭걸림:";
						}
						szLogMsg = szLogMsg + "상대가 우측/권상후/권상권하위치가 상대크레인 권하위치보다 작은 대상에 위배";
						commUtils.printLog(logId, szLogMsg, "SL");
						return szLogMsg;
					}
				}
			}
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }//end of try~catch
		
		return szLogMsg;
	}
    
    
	/**
	 * GridData -  권하위치변경가능 위치
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public GridData getDownLocChange(GridData gdReq) throws DAOException {
		String methodNm = "조회[BCoilJspSeEJB.getDownLocChange] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			
			//Grid 파라미터를 JDTORecord data 로 변환
		
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			
			String sYD_CRN_SCH_ID  = commUtils.trim(gdReq.getParam("YD_CRN_SCH_ID"));

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_CRN_SCH_ID" , sYD_CRN_SCH_ID); //
			
			/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYmCrnSchDel 
			SELECT A.YD_CRN_SCH_ID,A.YD_WBOOK_ID,B.STOCK_ID 
			  FROM TB_YM_CRNSCH A
			     , TB_YM_CRNWRKMTL B 
			 WHERE A.DEL_YN = 'N'
			   AND B.DEL_YN = 'N'
			   AND A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID
			   AND A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			    
			 */
			JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYmCrnSchDel", logId, methodNm,  "크레인 스케쥴 read");
			
			if(jsCrnSch.size()> 0 ) {
				JDTORecord jrCrnSch = jsCrnSch.getRecord(0);
				outRecSet = this.procReSch ( logId, methodNm, jrCrnSch );
			}	

			//UI로 반환 할 Grid data 를 생성 
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			GridData gdRet = commUtils.jdtoRecordToGridData(gdRtn, outRecSet.toList(), gdReq);
			
			commUtils.printLog(logId, methodNm, "S-", gdReq);
			
			//gdRet.addParam("ADV_RESULT", "ok");	// 이 값으로 화면에서 상태판단
			//gdRet.setStatus("true");
			
			return gdRet;			
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}			
	} // end of getDownLocChange
	   
	
	//////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////
	

	
	/**
     * 오퍼레이션명 : 코일크레인리스케줄(procReSch)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
     * @param  inRecord, recGetCrnWrkMtl, rsResultCrnwrkmtl
     * @return JDTORecordSet
     * @throws DAOException
     */
    public JDTORecordSet procReSch(String logId, String methodNms, JDTORecord inRecord) throws DAOException{
    	String methodNm = "TO 위치 결정[BCoilReSchSeEJB.procReSch] < " + methodNms;
    	
    	JDTORecord jrLocSrcRngRtn = JDTORecordFactory.getInstance().create();
    	String szMsg        		= "";     	  
    	
    	int intRtnVal 				= 0 ;
    	String TcarTcSndyn   		= "N";    // 대차이동지시 
    	JDTORecordSet jsRtn 		= JDTORecordFactory.getInstance().createRecordSet("");
    	
    	try{
        	commUtils.printLog(logId, methodNm, "S+");
        	//-------------------------------------------------------------------------------------------------------------
        	//	파라미터 확인
        	//-------------------------------------------------------------------------------------------------------------
			//파라미터 Null Check
        	String szWbookId	= commUtils.trim(inRecord.getFieldString("YD_WBOOK_ID" ));	
        	String ydCrnSchId	= commUtils.trim(inRecord.getFieldString("YD_CRN_SCH_ID" ));	
        	String szEqpId 		= commUtils.trim(inRecord.getFieldString("YD_EQP_ID"   ));	
        	String szSearchYd	= commUtils.trim(inRecord.getFieldString("SEARCH_YD"   ));	
       		
			//-------------------------------------------------------------------------------------------------------------
			//작업예약을 조회한다. To위치 결정방법이  사용자 지정인지 알기위해서...
			//-------------------------------------------------------------------------------------------------------------
        	JDTORecordSet jsTemp 		= JDTORecordFactory.getInstance().createRecordSet("");
        	
        	 /* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYmWrkbook

        	SELECT YD_WBOOK_ID      AS YD_WBOOK_ID
        	      ,REGISTER         AS REGISTER
        	      ,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
        	      ,MODIFIER         AS MODIFIER
        	      ,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT
        	      ,DEL_YN           AS DEL_YN
        	      ,YD_GP            AS YD_GP
        	      ,YD_BAY_GP        AS YD_BAY_GP
        	      ,YD_SCH_CD        AS YD_SCH_CD
        	      ,YD_SCH_PRIOR     AS YD_SCH_PRIOR
        	      ,YD_SCH_PROG_STAT AS YD_SCH_PROG_STAT
        	      ,YD_SCH_ST_GP     AS YD_SCH_ST_GP
        	      ,YD_SCH_REQ_GP    AS YD_SCH_REQ_GP
        	      ,YD_AIM_YD_GP     AS YD_AIM_YD_GP
        	      ,YD_AIM_BAY_GP    AS YD_AIM_BAY_GP
        	      ,YD_CTS_RELAY_YN  AS YD_CTS_RELAY_YN
        	      ,YD_CTS_RELAY_BAY_GP  AS YD_CTS_RELAY_BAY_GP
        	      ,YD_TO_LOC_DCSN_MTD AS YD_TO_LOC_DCSN_MTD
        	      ,YD_TO_LOC_GUIDE  AS YD_TO_LOC_GUIDE
        	      ,YD_WRK_PLAN_TCAR AS YD_WRK_PLAN_TCAR
        	      ,YD_CAR_USE_GP
        	      ,TRN_EQP_CD       AS TRN_EQP_CD
        	      ,CAR_NO           AS CAR_NO
        	      ,CARD_NO          AS CARD_NO
        	   FROM TB_YM_WRKBOOK A
        	 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
			 */
			jsTemp = commDao.select(inRecord, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYmWrkbook", logId, methodNm, "작업예약 조회"); 
	    	
	    	if (jsTemp == null || jsTemp.size() <= 0) {
				commUtils.printLog(logId, methodNm + "[작업예약종료]", "SL");
				
    			jrLocSrcRngRtn.setField("RTN", "-1");

				return jsRtn;
				
			}			
			
			jsTemp.absolute(1);
			JDTORecord jrWbook = JDTORecordFactory.getInstance().create();
			jrWbook.setRecord(jsTemp.getRecord());
			
			String szSchCd 	= commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"));
			String StockId 	= commUtils.trim(jrWbook.getFieldString("STOCK_ID"));
			//-------------------------------------------------------------------------------------------------------------
			//	크레인스케줄 조회
			//-------------------------------------------------------------------------------------------------------------
			JDTORecordSet jsCrnsch = JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord jrInPara = JDTORecordFactory.getInstance().create();
			jrInPara.setField("YD_WBOOK_ID"		, szWbookId);
			jrInPara.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
			jrInPara.setField("YD_EQP_ID"		, szEqpId);

			/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdCrnSchByWBookIdALL
			SELECT A.EQUIP_GP                AS YD_EQP_ID                       
			     , A.EQUIP_NAME              AS YD_EQP_NAME                     
			     , B.YD_CRN_SCH_ID           AS YD_CRN_SCH_ID                   
			     , B.REGISTER                AS REGISTER                        
			     , TO_CHAR(B.REG_DDTT       , 'YYYYMMDDHH24MISS') AS REG_DDTT                         
			     , B.MODIFIER                AS MODIFIER                        
			     , TO_CHAR(B.MOD_DDTT       , 'YYYYMMDDHH24MISS') AS MOD_DDTT                        
			     , B.DEL_YN                  AS DEL_YN                          
			     , B.YD_WBOOK_ID             AS YD_WBOOK_ID                     
			     , B.YD_GP                   AS YD_GP                           
			     , B.YD_BAY_GP               AS YD_BAY_GP                       
			     , B.YD_SCH_CD               AS YD_SCH_CD                       
			     , B.YD_SCH_ST_GP            AS YD_SCH_ST_GP                    
			     , B.YD_SCH_REQ_GP           AS YD_SCH_REQ_GP                   
			     , B.YD_SCH_PRIOR            AS YD_SCH_PRIOR                    
			     , B.YD_EQP_WRK_STAT         AS YD_EQP_WRK_STAT                 
			     , B.YD_WRK_PROG_STAT        AS YD_WRK_PROG_STAT                
			     , TO_CHAR(B.YD_WBOOK_DT    , 'YYYYMMDDHH24MISS') AS YD_WBOOK_DT
			     , TO_CHAR(B.YD_SCH_DT      , 'YYYYMMDDHH24MISS') AS YD_SCH_DT
			     , TO_CHAR(B.YD_WORD_DT     , 'YYYYMMDDHH24MISS') AS YD_WORD_DT
			     , TO_CHAR(B.YD_UP_CMPL_DT  , 'YYYYMMDDHH24MISS') AS YD_UP_CMPL_DT
			     , TO_CHAR(B.YD_DN_CMPL_DT  , 'YYYYMMDDHH24MISS') AS YD_DN_CMPL_DT
			     , B.YD_WRK_HDS_DD           AS YD_WRK_HDS_DD                   
			     , B.YD_WRK_DUTY             AS YD_WRK_DUTY                     
			     , B.YD_WRK_PARTY            AS YD_WRK_PARTY                    
			     , B.YD_MAIN_WRK_MTL_SH      AS YD_MAIN_WRK_MTL_SH              
			     , B.YD_AID_WRK_MTL_SH       AS YD_AID_WRK_MTL_SH               
			     , B.YD_AID_WRK_UPDN_GP      AS YD_AID_WRK_UPDN_GP              
			     , B.YD_TO_LOC_DCSN_MTD      AS YD_TO_LOC_DCSN_MTD              
			     , B.YD_TO_LOC_GUIDE         AS YD_TO_LOC_GUIDE                 
			     , B.YD_EQP_WRK_SH           AS YD_EQP_WRK_SH                   
			     , B.YD_EQP_WRK_WT           AS YD_EQP_WRK_WT                   
			     , B.YD_EQP_WRK_T            AS YD_EQP_WRK_T                    
			     , B.YD_EQP_WRK_MAX_W        AS YD_EQP_WRK_MAX_W                
			     , B.YD_EQP_WRK_MAX_L        AS YD_EQP_WRK_MAX_L                
			     , B.YD_CRN_SB_CTL_H         AS YD_CRN_SB_CTL_H                 
			     , B.YD_CRN_GRAB_USE_RULE_ID AS YD_CRN_GRAB_USE_RULE_ID         
			     , B.YD_UP_WO_LOC            AS YD_UP_WO_LOC                    
			     , B.YD_UP_WO_LAYER          AS YD_UP_WO_LAYER                  
			     , B.YD_UP_WO_LOC_XAXIS      AS YD_UP_WO_LOC_XAXIS              
			     , B.YD_UP_WO_XAXIS_GAP_MAX  AS YD_UP_WO_XAXIS_GAP_MAX          
			     , B.YD_UP_WO_XAXIS_GAP_MIN  AS YD_UP_WO_XAXIS_GAP_MIN          
			     , B.YD_UP_WO_LOC_YAXIS      AS YD_UP_WO_LOC_YAXIS              
			     , B.YD_UP_WO_LOC_YAXIS1     AS YD_UP_WO_LOC_YAXIS1             
			     , B.YD_UP_WO_LOC_YAXIS2     AS YD_UP_WO_LOC_YAXIS2             
			     , B.YD_UP_WO_YAXIS_GAP_MAX  AS YD_UP_WO_YAXIS_GAP_MAX          
			     , B.YD_UP_WO_YAXIS_GAP_MIN  AS YD_UP_WO_YAXIS_GAP_MIN          
			     , B.YD_UP_WO_LOC_ZAXIS      AS YD_UP_WO_LOC_ZAXIS              
			     , B.YD_UP_WO_ZAXIS_GAP_MAX  AS YD_UP_WO_ZAXIS_GAP_MAX          
			     , B.YD_UP_WO_ZAXIS_GAP_MIN  AS YD_UP_WO_ZAXIS_GAP_MIN          
			     , B.YD_DN_WO_LOC            AS YD_DN_WO_LOC                    
			     , B.YD_DN_WO_LAYER          AS YD_DN_WO_LAYER                  
			     , B.YD_DN_WO_LOC_XAXIS      AS YD_DN_WO_LOC_XAXIS              
			     , B.YD_DN_WO_XAXIS_GAP_MAX  AS YD_DN_WO_XAXIS_GAP_MAX          
			     , B.YD_DN_WO_XAXIS_GAP_MIN  AS YD_DN_WO_XAXIS_GAP_MIN          
			     , B.YD_DN_WO_LOC_YAXIS      AS YD_DN_WO_LOC_YAXIS              
			     , B.YD_DN_WO_LOC_YAXIS1     AS YD_DN_WO_LOC_YAXIS1             
			     , B.YD_DN_WO_LOC_YAXIS2     AS YD_DN_WO_LOC_YAXIS2             
			     , B.YD_DN_WO_YAXIS_GAP_MAX  AS YD_DN_WO_YAXIS_GAP_MAX          
			     , B.YD_DN_WO_YAXIS_GAP_MIN  AS YD_DN_WO_YAXIS_GAP_MIN          
			     , B.YD_DN_WO_LOC_ZAXIS      AS YD_DN_WO_LOC_ZAXIS              
			     , B.YD_DN_WO_ZAXIS_GAP_MAX  AS YD_DN_WO_ZAXIS_GAP_MAX          
			     , B.YD_DN_WO_ZAXIS_GAP_MIN  AS YD_DN_WO_ZAXIS_GAP_MIN          
			     , B.YD_UP_WR_LOC            AS YD_UP_WR_LOC                    
			     , B.YD_UP_WR_LAYER          AS YD_UP_WR_LAYER                  
			     , B.YD_UP_WRK_ACT_GP        AS YD_UP_WRK_ACT_GP                
			     , B.YD_UP_WR_XAXIS          AS YD_UP_WR_XAXIS                  
			     , B.YD_UP_WR_YAXIS          AS YD_UP_WR_YAXIS                  
			     , B.YD_UP_WR_YAXIS1         AS YD_UP_WR_YAXIS1                 
			     , B.YD_UP_WR_YAXIS2         AS YD_UP_WR_YAXIS2                 
			     , B.YD_UP_WR_ZAXIS          AS YD_UP_WR_ZAXIS                  
			     , B.YD_DN_WR_LOC            AS YD_DN_WR_LOC                    
			     , B.YD_DN_WR_LAYER          AS YD_DN_WR_LAYER                  
			     , B.YD_DN_WRK_ACT_GP        AS YD_DN_WRK_ACT_GP                
			     , B.YD_DN_WR_XAXIS          AS YD_DN_WR_XAXIS                  
			     , B.YD_DN_WR_YAXIS          AS YD_DN_WR_YAXIS                  
			     , B.YD_DN_WR_YAXIS1         AS YD_DN_WR_YAXIS1                 
			     , B.YD_DN_WR_YAXIS2         AS YD_DN_WR_YAXIS2                 
			     , B.YD_DN_WR_ZAXIS          AS YD_DN_WR_ZAXIS   
			     , C.YD_AID_WRK_YN           AS YD_AID_WRK_YN   
			     , C.HCR_GP                  AS HCR_GP                 
			     , C.STL_PROG_CD             AS STL_PROG_CD       
			     , D.HR_PLNT_GP 		    
			     , D.ORD_NO 			-- 제작번호,
			     , D.ORD_DTL 		    -- 제작행번,
			     , D.COIL_T 			-- 코일두께,
			     , D.COIL_W 			-- 코일폭,
			     , D.CURR_COIL_LEN	    -- 코일길이,
			     , D.COIL_INDIA 		-- 코일내경,
			     , D.COIL_OUTDIA 	    -- 코일외경,
			     , decode(D.COIL_WT, 0, D.NET_CAL_WT, D.COIL_WT) 		--AS 코일중량,
			     , D.NEXT_PROC 		    -- 차공정,
			     , D.PLAN_PROC1         -- 계획공정,
			     , D.BRANCH_CD 		    -- 분기위치코드,
			     , D.EXTEND_CONVEYOR_BRANCH_CD -- 확장분기위치코드,
			     , D.HYSCO_TRANS_GP 	-- HYSCO이송수단,
			     , D.COOL_METHOD 	    -- 냉각방법,
			     , decode(D.CURR_PROG_CD,'2','H','3','D','4','E','6','L','7','K',D.CURR_PROG_CD) AS CURR_PROG_CD
			     , D.RETURN_GP
			--     , (CASE WHEN D.YD_EQP_GP='QE'AND B.YD_BAY_GP IN('E','F') 
			--             THEN 'G' ELSE 'G' END) AS YD_BAY_GP
			     , (CASE WHEN EXISTS(SELECT 1 
			                           FROM (SELECT STACK_RULE_NAME
			    			                   FROM USRYMA.TB_YM_STACKRULE
			    			                  WHERE YD_GP = '3' 
			                                    AND STACK_COL_USAGE_CD='X' 
			                                    AND BAY_GP ='A'
			    			                    AND STACK_RULE_CD LIKE 'SPE%'
			    			                    AND STACK_RULE_USE_YN='Y') 
			                                T 
			                          WHERE T.STACK_RULE_NAME = D.HR_SPEC_ABBSYM) 
			             THEN 'Y' ELSE 'N' END ) AS JJANGGU_CHK  
			     , C.STOCK_ID       
			     , SUM(D.COIL_WT)   OVER (ORDER BY C.STACK_LAYER_GP DESC) AS SUM_MTL_WT      
				 , SUM(D.COIL_T)    OVER (ORDER BY C.STACK_LAYER_GP DESC) AS SUM_MTL_T   
				 , MAX(D.COIL_W)    OVER (ORDER BY C.STACK_LAYER_GP DESC) AS MAX_MTL_W 
				 , MAX(D.COIL_LEN)  OVER (ORDER BY C.STACK_LAYER_GP DESC) AS MAX_MTL_L 
				 , COUNT(D.COIL_NO) OVER (ORDER BY C.STACK_LAYER_GP DESC) AS SH_CNT 
			  FROM TB_YM_EQUIP  A                                               
			     , TB_YM_CRNSCH B                                               
			     , TB_YM_CRNWRKMTL C                                               
			     , USRPTA.TB_PT_COILCOMM  D  
			 WHERE B.YD_EQP_ID      = A.EQUIP_GP  
			   AND B.YD_CRN_SCH_ID  = C.YD_CRN_SCH_ID  --COIL은 1:1
			   AND C.STOCK_ID       = D.COIL_NO
			   AND B.YD_CRN_SCH_ID  = :V_YD_CRN_SCH_ID
			   AND B.YD_EQP_ID      = :V_YD_EQP_ID                         
			   AND B.DEL_YN = 'N'
			   AND C.DEL_YN = 'N'
			 ORDER BY B.YD_CRN_SCH_ID
			 */
			
			jsCrnsch = commDao.select(inRecord, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdCrnSchByWBookIdALL", logId, methodNm, "크레인스케줄 조회"); 
			
			//-------------------------------------------------------------------------------------------------------------
			//	크레인스케줄의 권하지시위치 결정
			//-------------------------------------------------------------------------------------------------------------
	    	String szToLocDcsnMtd 	= "";
	    	String szToLocGuide 	= "";
			String sWrkFlag 		= "";
			JDTORecord jrCrnSch		= JDTORecordFactory.getInstance().create();

			// A동 대차 하차 작업 인 경우 
			String sDIR_YN = "N";
			
		    for(int Loop_i = 1; Loop_i <= jsCrnsch.size(); Loop_i++) {

        		jsCrnsch.absolute(Loop_i);
        		jrCrnSch  = jsCrnsch.getRecord();
        		
        		//크레인스케줄Data저장
        		ydCrnSchId     = jrCrnSch.getFieldString("YD_CRN_SCH_ID");
        		szSchCd        = jrCrnSch.getFieldString("YD_SCH_CD");
        		szToLocDcsnMtd = jrCrnSch.getFieldString("YD_TO_LOC_DCSN_MTD");
        		StockId 	   = jrCrnSch.getFieldString("STOCK_ID");
        		szToLocGuide   = jrCrnSch.getFieldString("YD_TO_LOC_GUIDE");
        		
        		szMsg = "작업예약 " + szWbookId + " [" + Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]에 대한 권하지시위치 결정 "; //szWbookId
        		commUtils.printLog(logId, szMsg, "SL");
        		
				/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getTcDirYn 
				SELECT NVL(MAX(CASE WHEN YD_SCH_CD = '3ATC01LM' 
				                     AND SUBSTR(STACK_COL_GP,3,2) = 'FE' 
				                     
				                     AND (SELECT DECODE(COUNT(*),0,'N','Y') 
				                            FROM TB_HR_C_SHEARWOWR SR,
				                                 TB_PT_COILCOMM CC
				                            WHERE SR.COIL_NO = CC.COIL_NO
				                              AND SR.COIL_NO = A.STOCK_ID
				                              AND SR.WORD_PROC IN ('5H','6H')
				                              AND SR.WORK_STAT IN ('2','B','E')
				                              AND CC.CURR_PROG_CD = 'C'
				                              AND SR.STEP_NO IN (SELECT MAX(STEP_NO)
				                                                   FROM TB_HR_C_SHEARWOWR
				                                                   WHERE COIL_NO = SR.COIL_NO)) = 'Y' 
				                    THEN 'Y'
				                    ELSE 'N' END ),'N')    AS DIR_YN
				  FROM
				     (  SELECT A1.YD_SCH_CD                AS YD_SCH_CD
				             , A1.STACK_COL_GP             AS STACK_COL_GP 
				             , A1.YD_LOC_SRCH_RNG_SEQ      AS YD_LOC_SRCH_RNG_SEQ
				             , B1.YD_SCH_PRFR_PRIOR        AS YD_SCH_PRFR_PRIOR
				             , CASE WHEN SUBSTR(A1.STACK_COL_GP,3,2) BETWEEN '00' AND '99' THEN SUBSTR(YD_SCH_PRFR_PRIOR,1,1)
				                    WHEN SUBSTR(A1.STACK_COL_GP,3,2) =                'PT' THEN SUBSTR(YD_SCH_PRFR_PRIOR,3,1)
				                    ELSE SUBSTR(B1.YD_SCH_PRFR_PRIOR,2,1)    END GROUP_SEQ   --검색조건 그룹순위
				             , :V_STOCK_ID AS STOCK_ID     
				          FROM TB_YM_SCHLOCSRCH      A1   
				              ,TB_YM_SCHLOCSRCHPRIOR B1   
				              , (SELECT WB.YD_SCH_CD
				                        -- 행선구분 추가
				                      , (SELECT NVL((SELECT ITEM
				                                       FROM USRYMA.TB_YM_RULE B
				                                      WHERE B.REPR_CD_GP = 'YD_RT'
				                                        AND SUBSTR(B.CD_GP,3,6) = SUBSTR(WB.YD_SCH_CD,3,6) 
				                                        AND NVL(ITEM,'GN')  = CASE WHEN SUBSTR(WB.YD_SCH_CD,3,2) IN ('DC','EC')                   
				                                                                        THEN A.NEXT_PROC
				                                                                   WHEN SUBSTR(WB.YD_SCH_CD,3,2) = 'TC' AND A.STL_APPEAR_GP = 'Y' 
				                                                                        THEN 'GD'                           --제품
				                                                                   WHEN SUBSTR(WB.YD_SCH_CD,3,2) = 'TC'                        
				                                                                        THEN A.NEXT_PROC                    --소재
				                                                                   ELSE 'GN' END 
				                                        AND ROWNUM = 1                           
				                                  ),'GN') 
				                           FROM USRPTA.TB_PT_COILCOMM A
				                          WHERE COIL_NO = :V_STOCK_ID)  
				                        AS YD_ROUTE_GP
				                   FROM (SELECT :V_YD_SCH_CD AS YD_SCH_CD FROM DUAL) WB
				                ) C1
				         WHERE A1.YD_SCH_CD               = B1.YD_SCH_CD
				           AND A1.YD_ROUTE_GP             = B1.YD_ROUTE_GP
				           AND A1.YD_SCH_CD               = C1.YD_SCH_CD
				           AND A1.YD_ROUTE_GP             = C1.YD_ROUTE_GP
				           AND A1.DEL_YN='N'
				         ORDER BY A1.YD_LOC_SRCH_RNG_SEQ
				      ) A
				WHERE ROWNUM = 1
				
				 */    		
				JDTORecord jrTcInPara 	= JDTORecordFactory.getInstance().create();
				jrTcInPara.setField("STOCK_ID"	, StockId);
				jrTcInPara.setField("YD_SCH_CD"	, szSchCd);
				
				JDTORecordSet jsTcDirYn = commDao.select(jrTcInPara, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getTcDirYn", logId, methodNm, "직보급 가능여부 조회");   			
			    if(jsTcDirYn.size() > 0 ){
		    		JDTORecord jrTcDirYn = jsTcDirYn.getRecord(0);
		    		sDIR_YN = commUtils.trim(jrTcDirYn.getFieldString("DIR_YN"));//대차 직보급 여부
			    } else {
			    	sDIR_YN = "N";
			    }
				commUtils.printLog(logId, "대차직보급 여부 :" + sDIR_YN , "SL");
				// 직보급이 가능 하면 TO위치 가이드를 HFL 보급존으로 변경처리 함
				if(sDIR_YN.equals("Y")) {
					szToLocGuide = "3AFE010001";
				}	
			
        		
       			if (StockId.substring(0,1).equals("S")) {       				
            		/**********************************************************
    				* SPM2 Scrap 인 경우  : 적치 가능 여부 CHECK 안함
    				**********************************************************/            		
            		szMsg = "["+ Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은  Scrap To위치 결정 시작";
        			commUtils.printLog(logId, szMsg, "SL");
        			
        			jsRtn = this.procScrapToLoc(logId, methodNm, jrWbook, jrCrnSch);

            	} else if (szToLocDcsnMtd.equals("W")) {
            		/**********************************************************
    				* 보조작업인 경우 TO위치 결정 (일반적치대로...)
    				**********************************************************/            		
            		szMsg = "["+ Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 보조작업 스케줄의  To위치 결정 시작";
        			commUtils.printLog(logId, szMsg, "SL");
        			
        			if(szSchCd.substring(2,4).equals("YD") ) {
        				sWrkFlag = "D";
        				jsRtn = this.procToLocPrimaryWorkMulti(logId, methodNm, sWrkFlag, szSearchYd, jrWbook, jrCrnSch);
        			} else {	
        				jsRtn = this.procToLocDummy(logId, methodNm, jrWbook, jrCrnSch);
        			}	

            	} else if (  szToLocDcsnMtd.equals("S")            //설비위 주작업
              			 &&(szSchCd.substring(2,4).equals("FE")   //HFL 보급,TAKE IN
              			     || szSchCd.substring(2,4).equals("FD")   //HFL추출  
//          	           	 || szSchCd.substring(2,4).equals("HS")   //HFL결속대  
   	           			     || szSchCd.substring(2,4).equals("KE")   //SPM 보급,TAKE IN
   	           			     || szSchCd.substring(2,4).equals("KD")   //SPM 추출,TAKE IN
   	           			   )  //SPM보급
   	           			 && szSchCd.substring(6,7).equals("U") ) {    
               		
               		if (szSchCd.substring(4,6).equals("05")) {
                   		// 재처리 작업 임(3EKE05UM)	  --> 재처리 요청시 to위치 가이드 있음
                   		szMsg = "["+ Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 재처리사용자지정 스케줄의  To위치 결정 시작";
               			commUtils.printLog(logId, szMsg, "SL");
               			
               			jsRtn = this.procToLocUser(logId, methodNm, jrWbook, jrCrnSch);           			
               			
               		} else {
               		
   		           		/**********************************************************
   		   				* HFL/SPM보급  
   		   				**********************************************************/            		
   		       			
//   		       			jsRtn = this.procToLocConveyor(logId, methodNm, jrWbook, jrCrnSch);
               		}
            	} else if (szToLocDcsnMtd.equals("S") 
              			 && "3EKE02MM".equals(szSchCd) ) {    
               		
   		           		/**********************************************************
   		   				* SPM2보급 : 커팅장에서 보급존  
   		   				**********************************************************/            		
//   		       			jsRtn = this.procToLocConveyor(logId, methodNm, jrWbook, jrCrnSch);
   		       			
            	} else if( szSchCd.substring(2,4).equals("PT") && (szSchCd.substring(6,7).equals("U"))) {
            		
            	} else if( szSchCd.substring(2,4).equals("TC") && (szSchCd.substring(6,7).equals("U"))) {  
  
      			} else if (szToLocGuide.length() >= 4) {
            		/**********************************************************
    				* 사용자 지정 :
    				**********************************************************/            		
            		szMsg = "["+ Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 사용자지정 스케줄의  To위치 결정 시작";
        			commUtils.printLog(logId, szMsg, "SL");
        			// 대차 직보급
        			if(sDIR_YN.equals("Y")) {
    					jrWbook.setField("YD_TO_LOC_GUIDE", szToLocGuide);
    				}
        			
        			jsRtn = this.procToLocUser(logId, methodNm, jrWbook, jrCrnSch);
        			
        			// 동내이적이고 TO위치 가이드가 8자리 미만인 경우만
        			if((jsRtn.size() > 0) && (szSchCd.substring(2,4).equals("YD")) && (szToLocGuide.length() < 8) ) {
        				szMsg =  "[" + Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "] : 주작업 TO위치결정-사용자 지정에서 야드로 TO위치결정 시작";
        				commUtils.printLog(logId, szMsg, "SL");
        				
        				jsRtn = this.procToLocPrimaryWork(logId, methodNm, jrWbook, jrCrnSch);
        				szMsg =  "[" + Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 사용자 지정에서 주작업 스케줄의 To위치 결정 완료 ";
            			commUtils.printLog(logId, szMsg, "SL");
        			} 
        			// 동간이적시(대차하차)에도 To위치 가이드 위치검색 실패시 저장영역별 검색순서로 to위치 결정 20180214 정문식 주임 요청
        			else if((jsRtn.size() > 0) && (szSchCd.substring(2,4).equals("TC")) && (szToLocGuide.length() < 8) && (szSchCd.substring(6,7).equals("L"))) {
        				szMsg =  "[" + Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "] : 주작업 TO위치결정-사용자 지정에서 야드로 TO위치결정 시작";
        				commUtils.printLog(logId, szMsg, "SL");
        				
        				jsRtn = this.procToLocPrimaryWork(logId, methodNm, jrWbook, jrCrnSch);
        				szMsg =  "[" + Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 사용자 지정에서 주작업 스케줄의 To위치 결정 완료 ";
            			commUtils.printLog(logId, szMsg, "SL");
        			}
        				
        			
    			} else {
    				/*****************************************************************************
    				* 일반작업 TO위치 
    				* TO위치가 없을 경우  동내이적인 경우 XXXX
    				******************************************************************************/      

    				szMsg =  "[" + Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "] : 주작업 TO위치결정-야드로 TO위치결정 시작";
    				commUtils.printLog(logId, szMsg, "SL");

    				jsRtn = this.procToLocPrimaryWork(logId, methodNm, jrWbook, jrCrnSch);
    				
    				
    				if(jsRtn.size() == 0) {
    					sWrkFlag = "S";
    					jsRtn = this.procToLocPrimaryWorkMulti(logId, methodNm, sWrkFlag, szSearchYd, jrWbook, jrCrnSch);
    				}
    				        				
    				szMsg =  "[" + Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 주작업 스케줄의 To위치 결정 완료 ";
        			commUtils.printLog(logId, szMsg, "SL");
    			}
        	}
        	
		//-------------------------------------------------------------------------------------------------------------
    		
        	commUtils.printLog(logId, methodNm, "S-");
        	
			return jsRtn;			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
    }//end of procReSch()   
    


	/**
	 * 보조작업TO위치결정
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public JDTORecordSet procToLocDummy(String logId, String methodNms, JDTORecord jrWbook, JDTORecord jrCrnSch) throws JDTOException {
    	String methodNm  = "TO위치결정:보조작업[BCoilReSchSeEJB.procToLocDummy] < " + methodNms;
    	String szLogMsg  = "";
    	JDTORecord jrTemp = JDTORecordFactory.getInstance().create();
		JDTORecordSet	jsRtn = JDTORecordFactory.getInstance().createRecordSet("Temp");

		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------
		commUtils.printLog(logId, methodNm, "S+");

		String szYD_SCH_CD 	   		= commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"));			//크레인스케줄코드
		String szYD_WBOOK_ID		= commUtils.trim(jrWbook.getFieldString("YD_WBOOK_ID"));			//작업예약

		String szSTOCK_ID	   		= commUtils.trim(jrCrnSch.getFieldString("STOCK_ID"));			//크레인작업재료
		String szYD_CRN_SCH_ID 		= commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"));		//크레인스케줄ID
		String szYD_EQP_ID     		= commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"));			//크레인설비ID
		String szYD_UP_WO_LOC 		= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LOC"));		
		String szYD_UP_WO_LAYER 	= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LAYER"));		

		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			if( szYD_UP_WO_LOC.equals("") ) {
				szLogMsg = methodNm+ "크레인작업재료의  재료정보["+szSTOCK_ID+"]에 대한 권하 또는 권상위치 이상 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return jsRtn;
			}
			
				
			//권상재료에 따라 알맞은 적치가능한 베드 검색 방법을 적용
			jrTemp = JDTORecordFactory.getInstance().create();
	    	jrTemp.setField("STOCK_ID"      , szSTOCK_ID);											//권상 STOCK
			jrTemp.setField("YD_SCH_CD"		, szYD_SCH_CD);		//스케줄 코드
			jrTemp.setField("YD_EQP_ID"		, szYD_EQP_ID);		//설비ID
			jrTemp.setField("YD_CRN_SCH_ID"	, szYD_CRN_SCH_ID);	//크레인 스케쥴 ID
			jrTemp.setField("YD_UP_WO_LOC"  , szYD_UP_WO_LOC);		
			jrTemp.setField("SEARCH_ALL"    , "Y");		
			
			szLogMsg =  " TOSQL:["+szYD_CRN_SCH_ID+ "] 권상재료["+szSTOCK_ID +" 의 적치가능한 베드 조회 시작";
			commUtils.printLog(logId, szLogMsg, "SL");
	      	
	    	//----------------------------------------------------------------------------------------------------------------------
			//	DUMMY 의 적치가능한 베드 조회
			//  적재가능 위치 만 검색
			//----------------------------------------------------------------------------------------------------------------------
			
			/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdDummySearch 
			WITH SPEC_TABLE AS (
			    -- 특정규격CHECK 
			    SELECT DTL_ITM1 AS STACK_RULE_NAME 
			      FROM USRYMA.TB_YM_RULE
			     WHERE REPR_CD_GP = 'YM004'
			       AND DEL_YN = 'N'
			), AUTO_CR_TABLE AS
			(
			SELECT CASE WHEN EQUIP_GP IN ('3ACRA1', '3ACRA2') AND YD_EQP_WRK_MODE2 = 'A' THEN 'Y'
			            WHEN EQUIP_GP IN ('3ACRA1', '3ACRA2') AND YD_EQP_WRK_MODE2 = 'R' THEN 'Y'
			            WHEN EQUIP_GP IN ('3CCRC1', '3CCRC2', '3CCRC3') AND YD_EQP_WRK_MODE2 = 'A' THEN 'Y'
			            WHEN EQUIP_GP IN ('3CCRC1', '3CCRC2', '3CCRC3') AND YD_EQP_WRK_MODE2 = 'R' THEN 'Y'
			            WHEN EQUIP_GP IN ('3ECRE1', '3ECRE2', '3ECRE3') AND YD_EQP_WRK_MODE2 = 'A' THEN 'Y'
			            ELSE 'N' END AS IS_AUTO
			     , EQUIP_GP
			  FROM TB_YM_EQUIP
			 WHERE DEL_YN = 'N'
			   AND EQUIP_GP = :V_YD_EQP_ID
			), TO_LOC_TABLE AS
			(
			 -- A.B 동  대상 위치 SELECT 
			SELECT 0                                                     AS PRIOR1 --BED 방향 우선('5','X','4')
			     , ABS(TO_NUMBER(B.SECT_GP) - TO_NUMBER(SUBSTR(C.STACK_COL_GP,3,2)))             AS PRIOR2 --SPAN 우선
			     , NVL(ABS(TO_NUMBER(A.STACK_BED_GP) - TO_NUMBER(NVL(C.STACK_BED_GP,0))),99)               --BED + SPAN + 열  
			         + ABS(TO_NUMBER(B.COL_GP)       - TO_NUMBER(SUBSTR(C.STACK_COL_GP,5,2))) 
			         + ABS(TO_NUMBER(B.SECT_GP)      - TO_NUMBER(SUBSTR(C.STACK_COL_GP,3,2)))   
			                                                                                     AS PRIOR3 
			     , ABS(TO_NUMBER(B.COL_GP)           - TO_NUMBER(SUBSTR(C.STACK_COL_GP,5,2)))    AS PRIOR4
			     , A.STACK_COL_GP         AS TAG_STACK_COL_GP
			     , A.STACK_BED_GP         AS TAG_STACK_BED_GP
			     , A.STACK_LAYER_GP       AS TAG_STACK_LAYER_GP
			     , B.STACK_COL_USAGE_CD   AS TAG_COL_USE_CD
			     , DECODE(A.STACK_LAYER_GP, '01', LPAD(TO_NUMBER(A.STACK_BED_GP)   - 1, 2, '0'),
			                                '02', A.STACK_BED_GP)                                AS TAG_LEFT_BED
			     , DECODE(A.STACK_LAYER_GP, '01', A.STACK_LAYER_GP,
			                                '02', LPAD(TO_NUMBER(A.STACK_LAYER_GP) - 1, 2, '0')) AS TAG_LEFT_LAYER
			     , DECODE(A.STACK_LAYER_GP, '01', LPAD(TO_NUMBER(A.STACK_BED_GP)   + 1, 2, '0'),
			                                '02', LPAD(TO_NUMBER(A.STACK_BED_GP)   + 1, 2, '0')) AS TAG_RIGHT_BED
			     , DECODE(A.STACK_LAYER_GP, '01', A.STACK_LAYER_GP,
			                                '02', LPAD(TO_NUMBER(A.STACK_LAYER_GP) - 1, 2, '0')) AS TAG_RIGHT_LAYER
			     , C.JJANG_GP
			    --20180104 A동 더미(5~8 SPAN)인경우 TO위치가 좌측으로 나와야한다. 정종균GJ요청     
			     , CASE WHEN SUBSTR(C.STACK_COL_GP, 2, 1) = 'A' 
			             AND SUBSTR(C.STACK_COL_GP, 3, 2) IN ('05','06','07','08') 
			             AND SUBSTR(C.STACK_COL_GP, 1, 4) < SUBSTR(A.STACK_COL_GP, 1, 4) THEN 2
			            WHEN SUBSTR(A.STACK_COL_GP, 3, 2) IN ('01','02','03','04')       THEN 0
			            ELSE 1 END AS ORDER_SEQ
			  FROM (SELECT * 
			          FROM TB_YM_STACKLAYER  AA
			            -- 설비 작업대상재 제외
			            , (SELECT NVL(NEXT_COIL_UP_LOC,1)  AS NEXT_COIL_UP_LOC
			                  FROM
			                   (
			                    SELECT NEXT_COIL_UP_LOC
			                         , ROWNUM AS  ROW_SEQ 
			                      FROM 
			                           ( 
			                            SELECT S2.COIL_NO        AS NEXT_COIL_NO 
			                                 , (SELECT STACK_COL_GP||STACK_BED_GP||STACK_LAYER_GP  
			                                      FROM TB_YM_STACKLAYER  
			                                     WHERE STOCK_ID = S2.COIL_NO 
			                                       AND STACK_LAYER_STAT IN ('C','U') 
			                                       AND ROWNUM = 1) AS  NEXT_COIL_UP_LOC 
			                              FROM TB_HR_C_SHEARWOUNIT     S1,    -- 정정작업단위순위 
			                                   TB_HR_C_SHEARWOPRIOR    S2,    -- 정정지시순위 
			                                   TB_HR_C_SHEARWOWR       S3,    -- 정정지시실적 
			                                   TB_PT_COILCOMM          S4,    -- COIL공통 
			                                    (SELECT A.* 
			                                             , SUBSTR(A.PROC_GP,1,1)||DECODE(SUBSTR(A.PROC_GP,2,1),'K','R') as PROC_GP2                
			                                          FROM  
			                                              ( 
			                                                SELECT 'B'        AS HR_PLNT_GP 
			                                                     , CASE WHEN SUBSTR(:V_YD_EQP_ID,2,1) = 'A' THEN '5H' 
			                                                            WHEN SUBSTR(:V_YD_EQP_ID,2,1) = 'D' THEN '6K' 
			                                                            ELSE '5K' END AS PROC_GP   -- 5H(HFL),5K(SPM1),6K(SPM2) 
			                                                  FROM DUAL 
			                                              ) A 
			                                    ) DD        
			                            WHERE  S1.HR_PLNT_GP            = DD.HR_PLNT_GP 
			                               AND  (S3.WORD_PROC = DD.PROC_GP OR S3.WORD_PROC = DD.PROC_GP2)   
			                               AND  S1.HR_PLNT_GP            = S2.HR_PLNT_GP 
			                               AND  S1.PROC_GP               = S2.PROC_GP 
			                               AND  S1.PUT_PRIOR            >= 1 
			                               AND  S1.PUT_PRIOR            <= 100 
			                               AND  S1.WORD_UNIT_NAME        = S2.WORD_UNIT_NAME 
			                               AND  S1.WORK_STAT             IN ('3','5')            -- 2:투입전, 3:작업중,  *:작업완료, 5: 보관매출(BY강태경) 
			                               AND  S2.WORK_STAT             in ('2','X','R')        -- 2:진행중, X:보류취소, R:임시보류 
			                               AND  S3.HR_PLNT_GP            = S2.HR_PLNT_GP 
			                               AND  S3.PROC_GP               = S2.PROC_GP 
			                               AND  S3.WORD_UNIT_NAME        = S2.WORD_UNIT_NAME 
			                               AND  S3.COIL_NO               = S2.COIL_NO 
			                               AND  S3.STEP_NO               = S2.STEP_NO 
			                               AND  EXISTS(SELECT 1 FROM TB_HR_C_SHEARWOWR SS WHERE SS.WORK_STAT IN( 'B') AND SS.WORD_UNIT_NAME=S1.WORD_UNIT_NAME) --추가(보급요구 중인 단위)
			                               AND  S2.STEP_NO               = (SELECT MAX(Y.STEP_NO)   
			                                                                 FROM TB_HR_C_SHEARWOPRIOR Y    
			                                                                WHERE Y.COIL_NO        = S2.COIL_NO  
			                                                                  AND Y.HR_PLNT_GP     = S2.HR_PLNT_GP 
			                                                                  AND Y.PROC_GP        = S2.PROC_GP 
			                                                                  AND Y.WORD_UNIT_NAME = S2.WORD_UNIT_NAME) 
			                               AND  S4.COIL_NO               = S2.COIL_NO 
			                               AND  DECODE(S1.WORK_STAT,'5','C',S4.CURR_PROG_CD)   = 'C'   
			                            ORDER BY S1.PUT_PRIOR, S2.WORD_UNIT_SEQNO 
			                        )  A 
			                    ) A1
			                  , (SELECT * FROM TB_YM_RULE WHERE REPR_CD_GP = 'APP110') B1  
			                    WHERE A1.ROW_SEQ >  0  
			                      AND A1.ROW_SEQ <= TO_NUMBER(B1.DTL_ITM3) )  BB
			         WHERE 1=1
			           AND SUBSTR(AA.STACK_COL_GP,1,2) = SUBSTR(:V_YD_EQP_ID,1,2)
			           AND AA.STACK_COL_GP||AA.STACK_BED_GP||AA.STACK_LAYER_GP=BB.NEXT_COIL_UP_LOC(+)
			           AND BB.NEXT_COIL_UP_LOC IS NULL
			       ) A
			     , TB_YM_STACKCOL B
			     , (
			         SELECT K.STACK_COL_GP
			              , K.STACK_BED_GP
			              , K.STACK_LAYER_GP
			              , (SELECT CASE WHEN EXISTS(SELECT 1 FROM SPEC_TABLE T WHERE T.STACK_RULE_NAME=A.HR_SPEC_ABBSYM) --특정규격존재                                 
			                                  AND NVL(A.NEXT_PROC,1) NOT IN ('5K','6K') --정정대상                                  
			                                  AND TRUNC((SYSDATE - A.HRMILL_CMPL_DT)*24,0)<=48 --압연시간 48시간이 안된 경우                                       
						                 THEN 'Y' 
						                 ELSE 'N' END  
			                   FROM USRPTA.TB_PT_COILCOMM A
			                   WHERE COIL_NO = K.STOCK_ID)  
			                AS JJANG_GP --짱구 여부    
			           FROM TB_YM_STACKLAYER K
			          WHERE STOCK_ID = :V_STOCK_ID
			       )C
			     , (SELECT ITEM , DTL_ITM1 FROM TB_YM_RULE
			         WHERE REPR_CD_GP = 'SCH001') D  
			 WHERE B.YD_GP 			  	    = SUBSTR(C.STACK_COL_GP,1,1)	-- 야드구분(1)
			   AND B.BAY_GP 		  	    = SUBSTR(C.STACK_COL_GP,2,1)	-- 동구분(E)
			   AND A.STACK_LAYER_GP         IN ('01','02')
			   AND B.SECT_GP                BETWEEN '00' AND '99'
			   AND A.STACK_LAYER_ACTIVE_STAT= 'E'
			   AND A.STACK_LAYER_STAT       = 'E'
			   AND A.STACK_COL_GP           = B.STACK_COL_GP
			   AND SUBSTR(A.STACK_COL_GP,1,2)  = SUBSTR(C.STACK_COL_GP ,1,2)
			   -- DC,EC TO위치 제외
			   AND A.STACK_COL_GP NOT IN ( SELECT STACK_COL_GP FROM TB_YM_SCHLOCSRCH D 
			                                WHERE D.YD_SCH_CD     IN ('3ADC01LM','3BDC01LM','3CDC01LM','3BEC01LM','3CEC01LM','3DEC01LM')
			                                  AND SUBSTR(D.STACK_COL_GP,1,2) =  SUBSTR(C.STACK_COL_GP,1,2) 
			                                  AND D.DEL_YN = 'N')
			   AND SUBSTR(C.STACK_COL_GP,2,1) =  D.ITEM                         
			   AND ((SUBSTR(C.STACK_COL_GP,3,2) < D.DTL_ITM1 AND SUBSTR(A.STACK_COL_GP,3,2) BETWEEN '00' AND DTL_ITM1)
			         OR
			        (SUBSTR(C.STACK_COL_GP,3,2) > D.DTL_ITM1 AND SUBSTR(A.STACK_COL_GP,3,2) BETWEEN DTL_ITM1 AND '99')
			       )                           
			   AND SUBSTR(C.STACK_COL_GP,2,1) IN ('A','B')    
			   AND A.STACK_COL_GP||A.STACK_BED_GP||A.STACK_LAYER_GP NOT IN ( SELECT NVL(YD_DN_WO_LOC||YD_DN_WO_LAYER,'1') FROM TB_YM_CRNSCH WHERE DEL_YN = 'N')
			   AND B.YD_GP 			  	    = '3'
			   
			 UNION ALL
			 -- C.D.E 동  대상 위치 SELECT 
			SELECT 0                                                    AS PRIOR1 --BED 방향 우선('5','X','4')
			     , ABS(TO_NUMBER(B.SECT_GP) - TO_NUMBER(SUBSTR(C.STACK_COL_GP,3,2)))             AS PRIOR2 --SPAN 우선
			     , NVL(ABS(TO_NUMBER(A.STACK_BED_GP) - TO_NUMBER(NVL(C.STACK_BED_GP,0))),99)               --BED + SPAN + 열  
			         + ABS(TO_NUMBER(B.SECT_GP)      - TO_NUMBER(SUBSTR(C.STACK_COL_GP,3,2)))   
			         + ABS(TO_NUMBER(B.COL_GP)       - TO_NUMBER(SUBSTR(C.STACK_COL_GP,5,2)))    AS PRIOR3 
			     , ABS(TO_NUMBER(B.COL_GP)           - TO_NUMBER(SUBSTR(C.STACK_COL_GP,5,2)))    AS PRIOR4
			     , A.STACK_COL_GP         AS TAG_STACK_COL_GP
			     , A.STACK_BED_GP         AS TAG_STACK_BED_GP
			     , A.STACK_LAYER_GP       AS TAG_STACK_LAYER_GP
			     , B.STACK_COL_USAGE_CD   AS TAG_COL_USE_CD
			     , DECODE(A.STACK_LAYER_GP, '01', LPAD(TO_NUMBER(A.STACK_BED_GP)   - 1, 2, '0'),
			                                '02', A.STACK_BED_GP)                                AS TAG_LEFT_BED
			     , DECODE(A.STACK_LAYER_GP, '01', A.STACK_LAYER_GP,
			                                '02', LPAD(TO_NUMBER(A.STACK_LAYER_GP) - 1, 2, '0')) AS TAG_LEFT_LAYER
			     , DECODE(A.STACK_LAYER_GP, '01', LPAD(TO_NUMBER(A.STACK_BED_GP)   + 1, 2, '0'),
			                                '02', LPAD(TO_NUMBER(A.STACK_BED_GP)   + 1, 2, '0')) AS TAG_RIGHT_BED
			     , DECODE(A.STACK_LAYER_GP, '01', A.STACK_LAYER_GP,
			                                '02', LPAD(TO_NUMBER(A.STACK_LAYER_GP) - 1, 2, '0')) AS TAG_RIGHT_LAYER
			     , C.JJANG_GP              
			     , 1 AS ORDER_SEQ
			  FROM (SELECT * 
			          FROM TB_YM_STACKLAYER  AA
			            -- 설비 작업대상재 제외
			            , (SELECT NVL(NEXT_COIL_UP_LOC,1)  AS NEXT_COIL_UP_LOC
			                  FROM
			                   (
			                    SELECT NEXT_COIL_UP_LOC
			                         , ROWNUM AS  ROW_SEQ 
			                      FROM 
			                           ( 
			                            SELECT S2.COIL_NO        AS NEXT_COIL_NO 
			                                 , (SELECT STACK_COL_GP||STACK_BED_GP||STACK_LAYER_GP  
			                                      FROM TB_YM_STACKLAYER  
			                                     WHERE STOCK_ID = S2.COIL_NO 
			                                       AND STACK_LAYER_STAT IN ('C','U') 
			                                       AND ROWNUM = 1) AS  NEXT_COIL_UP_LOC 
			                              FROM TB_HR_C_SHEARWOUNIT     S1,    -- 정정작업단위순위 
			                                   TB_HR_C_SHEARWOPRIOR    S2,    -- 정정지시순위 
			                                   TB_HR_C_SHEARWOWR       S3,    -- 정정지시실적 
			                                   TB_PT_COILCOMM          S4,    -- COIL공통 
			                                    (SELECT A.* 
			                                             , SUBSTR(A.PROC_GP,1,1)||DECODE(SUBSTR(A.PROC_GP,2,1),'K','R') as PROC_GP2                
			                                          FROM  
			                                              ( 
			                                                SELECT 'B'        AS HR_PLNT_GP 
			                                                     , CASE WHEN SUBSTR(:V_YD_EQP_ID,2,1) = 'A' THEN '5H' 
			                                                            WHEN SUBSTR(:V_YD_EQP_ID,2,1) = 'D' THEN '6K' 
			                                                            ELSE '5K' END AS PROC_GP   -- 5H(HFL),5K(SPM1),6K(SPM2) 
			                                                  FROM DUAL 
			                                              ) A 
			                                    ) DD        
			                            WHERE  S1.HR_PLNT_GP            = DD.HR_PLNT_GP 
			                               AND  (S3.WORD_PROC = DD.PROC_GP OR S3.WORD_PROC = DD.PROC_GP2)   
			                               AND  S1.HR_PLNT_GP            = S2.HR_PLNT_GP 
			                               AND  S1.PROC_GP               = S2.PROC_GP 
			                               AND  S1.PUT_PRIOR            >= 1 
			                               AND  S1.PUT_PRIOR            <= 100 
			                               AND  S1.WORD_UNIT_NAME        = S2.WORD_UNIT_NAME 
			                               AND  S1.WORK_STAT             IN ('3','5')            -- 2:투입전, 3:작업중,  *:작업완료, 5: 보관매출(BY강태경) 
			                               AND  S2.WORK_STAT             in ('2','X','R')        -- 2:진행중, X:보류취소, R:임시보류 
			                               AND  S3.HR_PLNT_GP            = S2.HR_PLNT_GP 
			                               AND  S3.PROC_GP               = S2.PROC_GP 
			                               AND  S3.WORD_UNIT_NAME        = S2.WORD_UNIT_NAME 
			                               AND  S3.COIL_NO               = S2.COIL_NO 
			                               AND  S3.STEP_NO               = S2.STEP_NO 
			                               AND  EXISTS(SELECT 1 FROM TB_HR_C_SHEARWOWR SS WHERE SS.WORK_STAT IN( 'B') AND SS.WORD_UNIT_NAME=S1.WORD_UNIT_NAME) --추가(보급요구 중인 단위)
			                               AND  S2.STEP_NO               = (SELECT MAX(Y.STEP_NO)   
			                                                                 FROM TB_HR_C_SHEARWOPRIOR Y    
			                                                                WHERE Y.COIL_NO        = S2.COIL_NO  
			                                                                  AND Y.HR_PLNT_GP     = S2.HR_PLNT_GP 
			                                                                  AND Y.PROC_GP        = S2.PROC_GP 
			                                                                  AND Y.WORD_UNIT_NAME = S2.WORD_UNIT_NAME) 
			                               AND  S4.COIL_NO               = S2.COIL_NO 
			                               AND  DECODE(S1.WORK_STAT,'5','C',S4.CURR_PROG_CD)   = 'C'   
			                            ORDER BY S1.PUT_PRIOR, S2.WORD_UNIT_SEQNO 
			                        )  A 
			                    ) A1
			                  , (SELECT * FROM TB_YM_RULE WHERE REPR_CD_GP = 'APP110') B1  
			                    WHERE A1.ROW_SEQ >  0  
			                      AND A1.ROW_SEQ <= TO_NUMBER(B1.DTL_ITM3) )  BB
			         WHERE 1=1
			           AND SUBSTR(AA.STACK_COL_GP,1,2) = SUBSTR(:V_YD_EQP_ID,1,2)
			           AND AA.STACK_COL_GP||AA.STACK_BED_GP||AA.STACK_LAYER_GP=BB.NEXT_COIL_UP_LOC(+)
			           AND BB.NEXT_COIL_UP_LOC IS NULL
			       ) A
			     , TB_YM_STACKCOL B
			     , (
			         SELECT K.STACK_COL_GP
			              , K.STACK_BED_GP
			              , K.STACK_LAYER_GP
			              , (SELECT CASE WHEN EXISTS(SELECT 1 FROM SPEC_TABLE T WHERE T.STACK_RULE_NAME=A.HR_SPEC_ABBSYM) --특정규격존재                                 
			                                  AND NVL(A.NEXT_PROC,1) NOT IN ('5K','6K') --정정대상                                  
			                                  AND TRUNC((SYSDATE - A.HRMILL_CMPL_DT)*24,0)<=48 --압연시간 48시간이 안된 경우                                       
						                 THEN 'Y' 
						                 ELSE 'N' END  
			                   FROM USRPTA.TB_PT_COILCOMM A
			                   WHERE COIL_NO = K.STOCK_ID)  
			                 AS JJANG_GP --짱구 여부  
			           FROM TB_YM_STACKLAYER K
			          WHERE STOCK_ID = :V_STOCK_ID
			       )C
			 WHERE B.YD_GP 			  	    = SUBSTR(C.STACK_COL_GP,1,1)	-- 야드구분(1)
			   AND B.BAY_GP 		  	    = SUBSTR(C.STACK_COL_GP,2,1)	-- 동구분(E)
			   AND A.STACK_LAYER_GP         IN ('01','02')
			   AND B.SECT_GP                BETWEEN '00' AND '99'
			   AND A.STACK_LAYER_ACTIVE_STAT= 'E'
			   AND A.STACK_LAYER_STAT       = 'E'
			   AND A.STACK_COL_GP           = B.STACK_COL_GP

			   AND SUBSTR(A.STACK_COL_GP,1,2)  = SUBSTR(C.STACK_COL_GP ,1,2)
			   -- DC,EC TO위치 제외
			   AND A.STACK_COL_GP NOT IN ( SELECT STACK_COL_GP FROM TB_YM_SCHLOCSRCH D 
			                                WHERE D.YD_SCH_CD     IN ('3ADC01LM','3BDC01LM','3CDC01LM','3BEC01LM','3CEC01LM','3DEC01LM')
			                                  AND SUBSTR(D.STACK_COL_GP,1,2) =  SUBSTR(C.STACK_COL_GP,1,2) 
			                                  AND D.DEL_YN='N')
			   AND SUBSTR(C.STACK_COL_GP,2,1) IN ('C','D','E')    
			   AND A.STACK_COL_GP||A.STACK_BED_GP||A.STACK_LAYER_GP NOT IN ( SELECT NVL(YD_DN_WO_LOC||YD_DN_WO_LAYER,'1') FROM TB_YM_CRNSCH WHERE DEL_YN = 'N')
			   AND B.YD_GP 			  	    = '3'
			) 
			, TO_LOC_DATA_TABLE AS (
			-- TO 위치코일정보 SELECT
			SELECT A.PRIOR1 --BED 방향 우선
			     , A.PRIOR2 --SPAN 우선
			     , A.PRIOR3 
			     , A.PRIOR4
			     , :V_STOCK_ID AS STOCK_ID
			     , A.TAG_STACK_COL_GP
			     , A.TAG_STACK_BED_GP
			     , A.TAG_STACK_LAYER_GP
			     , A.TAG_COL_USE_CD
			     , A.TAG_LEFT_BED
			     , A.TAG_LEFT_LAYER
			     , A.JJANG_GP
			     , (SELECT STACK_LAYER_ACTIVE_STAT 
			          FROM TB_YM_STACKLAYER B 
			         WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
			           AND B.STACK_BED_GP   = A.TAG_LEFT_BED
			           AND B.STACK_LAYER_GP = A.TAG_LEFT_LAYER)  AS  TAG_LEFT_ACTIVE_STAT 
			     , (SELECT STACK_LAYER_STAT 
			          FROM TB_YM_STACKLAYER B 
			         WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
			           AND B.STACK_BED_GP   = A.TAG_LEFT_BED
			           AND B.STACK_LAYER_GP = A.TAG_LEFT_LAYER)  AS  TAG_LEFT_LAYER_STAT
			     , (SELECT STOCK_ID 
			          FROM TB_YM_STACKLAYER B 
			         WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
			           AND B.STACK_BED_GP   = A.TAG_LEFT_BED
			           AND B.STACK_LAYER_GP = A.TAG_LEFT_LAYER)  AS  TAG_LEFT_STOCK_ID
			     , A.TAG_RIGHT_BED
			     , A.TAG_RIGHT_LAYER
			     , (SELECT STACK_LAYER_ACTIVE_STAT 
			          FROM TB_YM_STACKLAYER B 
			         WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
			           AND B.STACK_BED_GP   = A.TAG_RIGHT_BED
			           AND B.STACK_LAYER_GP = A.TAG_RIGHT_LAYER) AS  TAG_RIGHT_ACTIVE_STAT 
			     , (SELECT STACK_LAYER_STAT 
			          FROM TB_YM_STACKLAYER B 
			         WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
			           AND B.STACK_BED_GP   = A.TAG_RIGHT_BED
			           AND B.STACK_LAYER_GP = A.TAG_RIGHT_LAYER) AS  TAG_RIGHT_LAYER_STAT
			     , (SELECT STOCK_ID 
			          FROM TB_YM_STACKLAYER B 
			         WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
			           AND B.STACK_BED_GP   = A.TAG_RIGHT_BED
			           AND B.STACK_LAYER_GP = A.TAG_RIGHT_LAYER) AS  TAG_RIGHT_STOCK_ID
			     , NVL((SELECT TO_NUMBER(DTL_ITM1) 
			              FROM USRYMA.TB_YM_RULE 
			             WHERE REPR_CD_GP='SCH_TO' AND CD_GP = '3' AND ITEM= 'ODIA_DIFF1'),180) AS ODIA_DIFF1     
			     , NVL((SELECT TO_NUMBER(DTL_ITM1) 
			              FROM USRYMA.TB_YM_RULE 
			             WHERE REPR_CD_GP='SCH_TO' AND CD_GP = '3' AND ITEM= 'WID_DIFF1'),200) AS WID_DIFF1   
			     , (SELECT STOCK_ID 
			          FROM TB_YM_STACKLAYER B 
			         WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
			           AND B.STACK_BED_GP   = A.TAG_LEFT_BED
			           AND B.STACK_LAYER_GP = '02') AS TAG_2DAN_LEFT_STOCK_ID
			     , (SELECT STOCK_ID 
			          FROM TB_YM_STACKLAYER B 
			         WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
			           AND B.STACK_BED_GP   = A.TAG_STACK_BED_GP
			           AND B.STACK_LAYER_GP = '02') AS TAG_2DAN_RIGHT_STOCK_ID         
			     , ORDER_SEQ
			  FROM TO_LOC_TABLE A

			)
			SELECT * 
			  FROM
			       (
			        SELECT KK.*   
			             , ROW_NUMBER() OVER(PARTITION BY PRIOR5 ORDER BY PRIOR1,PRIOR5,PRIOR2,PRIOR3,PRIOR4 ) GROUP_ROW       
			          FROM
			               (
			                SELECT K.* 
			                     ,  CASE WHEN TAG_LEFT_STOCK_ID  IS NOT NULL AND TAG_RIGHT_STOCK_ID IS NOT NULL THEN '2'
			                             WHEN TAG_LEFT_STOCK_ID  IS NOT NULL                            THEN '2'
			                             WHEN TAG_RIGHT_STOCK_ID IS NOT NULL                            THEN '2'
			                             ELSE '3' END AS PRIOR5 
			                         , C.COIL_NO AS C_COIL_NO      
			                         , L.COIL_NO AS L_COIL_NO          
			                         , R.COIL_NO AS R_COIL_NO       
			                         , C.COIL_OUTDIA  AS C_OUTDIA    
			                         , L.COIL_OUTDIA  AS L_OUTDIA        
			                         , R.COIL_OUTDIA  AS R_OUTDIA       
			                         , C.COIL_W  AS C_WIDTH    
			                         , L.COIL_W  AS L_WIDTH        
			                         , R.COIL_W  AS R_WIDTH  
			                         , C.COIL_WT AS C_WEIGTH    
			                         , L.COIL_WT AS L_WEIGTH        
			                         , R.COIL_WT AS R_WEIGTH
			                         , C.COIL_T  AS C_THICK    
			                         , L.COIL_T  AS L_THICK        
			                         , R.COIL_T  AS R_THICK
			                  FROM TO_LOC_DATA_TABLE K
			                     , (SELECT 1 T_ROW, A.* 
			                          FROM USRPTA.TB_PT_COILCOMM  A ) C  --대상코일
			                     , (SELECT 1 T_ROW, A.* 
			                          FROM USRPTA.TB_PT_COILCOMM  A ) L  --하단LEFT
			                     , (SELECT 1 T_ROW, A.* 
			                          FROM USRPTA.TB_PT_COILCOMM  A ) R  --하단RIGHT
			                 WHERE K.STOCK_ID           = C.COIL_NO(+) 
			                   AND K.TAG_LEFT_STOCK_ID  = L.COIL_NO(+) 
			                   AND K.TAG_RIGHT_STOCK_ID = R.COIL_NO(+) 
			                   AND 1 = CASE WHEN TAG_STACK_LAYER_GP = '01'  THEN 1
			                                WHEN TAG_STACK_LAYER_GP = '02' AND JJANG_GP = 'Y'  THEN 0  -- 짱구코일 2단제외
			                                WHEN TAG_STACK_LAYER_GP = '02'    --2 단일 경우 좌우 적치 상태 CHECK
			                                     AND TAG_LEFT_ACTIVE_STAT = 'E' AND TAG_RIGHT_ACTIVE_STAT = 'E' 
			                                     AND TAG_LEFT_LAYER_STAT  = 'C' AND TAG_RIGHT_LAYER_STAT  = 'C' THEN 1
			                                ELSE 0 END  
			                   AND 1 = CASE WHEN K.TAG_STACK_LAYER_GP = '02'  THEN 1
			                                --1단폭CHECK -> 2단폭CHECK로 변경
			                                WHEN K.TAG_STACK_LAYER_GP = '01' AND (SELECT DTL_ITM1 
			                                                                        FROM USRYMA.TB_YM_RULE 
			                                                                       WHERE REPR_CD_GP = 'APP044' 
			                                                                         AND ITEM = SUBSTR(K.TAG_STACK_COL_GP,2,1)) = 'N'
			                                     AND ABS(TO_NUMBER(C.COIL_OUTDIA)-TO_NUMBER(CASE WHEN TO_NUMBER(L.COIL_OUTDIA) > 0 THEN  L.COIL_OUTDIA ELSE C.COIL_OUTDIA END)) < ODIA_DIFF1  --외경차이
			                                     AND ABS(TO_NUMBER(C.COIL_OUTDIA)-TO_NUMBER(CASE WHEN TO_NUMBER(R.COIL_OUTDIA) > 0 THEN  R.COIL_OUTDIA ELSE C.COIL_OUTDIA END)) < ODIA_DIFF1
			                                     AND ABS(TO_NUMBER(C.COIL_W) -TO_NUMBER(CASE WHEN TO_NUMBER(L.COIL_W)  > 0 THEN  L.COIL_W  ELSE C.COIL_W  END)) < WID_DIFF1
			                                     AND ABS(TO_NUMBER(C.COIL_W) -TO_NUMBER(CASE WHEN TO_NUMBER(R.COIL_W)  > 0 THEN  R.COIL_W  ELSE C.COIL_W  END)) < WID_DIFF1                    
			                                THEN 1
			                                WHEN K.TAG_STACK_LAYER_GP = '01' AND (SELECT DTL_ITM1 
			                                                                        FROM USRYMA.TB_YM_RULE 
			                                                                       WHERE REPR_CD_GP = 'APP044' 
			                                                                         AND ITEM = SUBSTR(K.TAG_STACK_COL_GP,2,1)) = 'Y'
			                                     AND ABS(TO_NUMBER(C.COIL_OUTDIA)-TO_NUMBER(CASE WHEN TO_NUMBER(L.COIL_OUTDIA) > 0 THEN  L.COIL_OUTDIA ELSE C.COIL_OUTDIA END)) < ODIA_DIFF1  --외경차이
			                                     AND ABS(TO_NUMBER(C.COIL_OUTDIA)-TO_NUMBER(CASE WHEN TO_NUMBER(R.COIL_OUTDIA) > 0 THEN  R.COIL_OUTDIA ELSE C.COIL_OUTDIA END)) < ODIA_DIFF1
			                                THEN 1
			                                ELSE 0 END                     
			                   AND 1 = CASE WHEN K.TAG_STACK_LAYER_GP = '01' AND (TAG_2DAN_LEFT_STOCK_ID IS NOT NULL OR TAG_2DAN_RIGHT_STOCK_ID IS NOT NULL) THEN 0
			                           ELSE 1 END                                
			                   AND 1 = CASE WHEN :V_YD_EQP_ID = '3CCRC1' AND SUBSTR(TAG_STACK_COL_GP, 3, 2) > '08' THEN 2 ELSE 1 END 
			                   AND 1 = CASE WHEN :V_YD_EQP_ID = '3CCRC3' AND SUBSTR(TAG_STACK_COL_GP, 3, 2) < '13' THEN 2 ELSE 1 END 
			                          
			               ) KK             
			             , AUTO_CR_TABLE  CR          
			         WHERE ((CR.IS_AUTO = 'Y' AND TAG_STACK_COL_GP IN (SELECT R.ITEM
			                                                            FROM TB_YM_RULE      R
			                                                               , AUTO_CR_TABLE   A
			                                                           WHERE R.REPR_CD_GP = 'CR0001'
			                                                             AND R.CD_GP      = A.EQUIP_GP
			                                                             AND R.DEL_YN     = 'N')
			               )  OR CR.IS_AUTO = 'N')
			       )          
			 WHERE 1=1
			   AND ((PRIOR5 < 3) OR (PRIOR5 = 3 AND GROUP_ROW <= CASE WHEN NVL(:V_SEARCH_ALL,'N') = 'Y'  THEN 50 ELSE 10 END))  -- 공BED 50개만 검색
			 ORDER BY PRIOR1   -- BED방향(군) 
			        , ORDER_SEQ
			        , PRIOR5   -- 평점계산시 우선순위
			        , PRIOR2   -- SPAN 
			        , PRIOR3   -- BED + SPAN + 열  
			        , PRIOR4   -- 열                  
			*/ 
			
			JDTORecordSet outjsResult = commDao.select(jrTemp, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdDummySearchALL", logId, methodNm, "동일한 적치가능한 베드 조회");
			if (outjsResult.size() <= 0) {
				szLogMsg = methodNm+ "적치가능한 베드 검색 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return jsRtn;
			}
	
			JDTORecord	jrResult	= JDTORecordFactory.getInstance().create();
			JDTORecord	jrToLocDummyRtn	= JDTORecordFactory.getInstance().create();
			
	    	String szStackColGp 	= "";
			String szStackBedGp 	= "";
			String szStackLayerGp 	= "";	
			String szToPosGrade 	= "999";

			String sRtnBedDan 		= "";	
	    	//----------------------------------------------------------------------------------------------------------------------
			//	적재가능 위치 SCH RULL 검색
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecord jrSchSule = this.procSchSule(logId, methodNms, szSTOCK_ID, szYD_SCH_CD);
			
			String szDBLogMsg	 = "";
		    // 평점 CEHCK	
			for(int i = 1; i <= outjsResult.size(); i++) {
	
				outjsResult.absolute(i);
				jrResult  = outjsResult.getRecord();

				szStackColGp 	= commUtils.trim(jrResult.getFieldString("TAG_STACK_COL_GP"  ));
				szStackBedGp 	= commUtils.trim(jrResult.getFieldString("TAG_STACK_BED_GP"  ));
				szStackLayerGp 	= commUtils.trim(jrResult.getFieldString("TAG_STACK_LAYER_GP"  ));	


		//평점 분석  /////////////////////////////////////////////////////////////////////////////////////////////////////		
				jrResult.setField("YD_CRN_SCH_ID"	, szYD_CRN_SCH_ID); 
				jrResult.setField("YD_SCH_CD"	 	, szYD_SCH_CD); 
				
				jrToLocDummyRtn = this.procGradeAnalysisDummy(logId, methodNm, jrResult, jrSchSule);
				
				szToPosGrade    		= commUtils.trim(jrToLocDummyRtn.getFieldString("GRIDE"  ));
				String szToPosGradeMsg 	= commUtils.trim(jrToLocDummyRtn.getFieldString("GRADE_CONTENTS"));
				if(szToPosGradeMsg.length() > 0 ) {
					szDBLogMsg = szDBLogMsg + szToPosGradeMsg +"\r\n";
				}
				
				outjsResult.getRecord(i-1).setField("GRIDE" , szToPosGrade ); 
			}
			
		    // 평점 CEHCK	 SORT
			String szGrideSort  = "99";
			int igride          = 10; 
			JDTORecordSet	jsGrideResult	= JDTORecordFactory.getInstance().createRecordSet("Temp");
			JDTORecord	    jrGrideResult	= JDTORecordFactory.getInstance().create();
			
			for(int i = 1; i <= igride; i++) {
				for(int j = 1; j <= outjsResult.size(); j++) {
					outjsResult.absolute(j);
					jrGrideResult  = outjsResult.getRecord();
					szGrideSort 	= commUtils.trim(jrGrideResult.getFieldString("GRIDE"  ));
					int iGrideSort = Integer.parseInt(szGrideSort);
	
					if(!szGrideSort.equals(YmConstant.RETN_CD_FAILURE)) {
					
						if(iGrideSort == i) {
							jsGrideResult.addRecord(outjsResult.getRecord());
						}
					}
				}
			}
				
		    // 적치 가능 check
			JDTORecord	    jrAbleResult	= JDTORecordFactory.getInstance().create();
			String szGRIDE = "9";
			for(int i = 1; i <= jsGrideResult.size(); i++) {

				jsGrideResult.absolute(i);
				jrAbleResult  = jsGrideResult.getRecord();
				
				szGRIDE 		= commUtils.nvl (jrAbleResult.getFieldString("GRIDE"),"9");
				szStackColGp 	= commUtils.trim(jrAbleResult.getFieldString("TAG_STACK_COL_GP"  ));
				szStackBedGp 	= commUtils.trim(jrAbleResult.getFieldString("TAG_STACK_BED_GP"  ));
				szStackLayerGp 	= commUtils.trim(jrAbleResult.getFieldString("TAG_STACK_LAYER_GP"  ));	

				//적치가능 check
//				JDTORecord jrLocAbleRtn = this.procLocAbleChk(logId, methodNm, jrAbleResult, jrSchSule);
				
				
				EJBConnector ejbConn = new EJBConnector("default", "BCoilSchSeEJB", this);
				JDTORecord jrLocAbleRtn = (JDTORecord)ejbConn.trx("procLocAbleChk", new Class[] { String.class, String.class, JDTORecord.class, JDTORecord.class }, new Object[] { logId, methodNm, jrAbleResult, jrSchSule});

				String LocAbleRtn 	 = commUtils.trim(jrLocAbleRtn.getFieldString("LOC_ABLE_RTN")) ;
				String LocAbleRtnMsg = commUtils.trim(jrLocAbleRtn.getFieldString("LOC_ABLE_CONTENTS")) ;
				if (LocAbleRtn.equals("1")){
					szLogMsg = methodNm+ szStackColGp+szStackBedGp+ szStackLayerGp+"  적치가능 위치평점:"+ szGRIDE;
					commUtils.printLog(logId, szLogMsg, "SL");
					
				    //적치가능 
					sRtnBedDan = szStackColGp + szStackBedGp + szStackLayerGp;
					JDTORecord 		jrRtn = JDTORecordFactory.getInstance().create();
					
					jrRtn.setField("SEARCH"		, "D");
					jrRtn.setField("GRADE"		, szGRIDE); 
					jrRtn.setField("SEARCH_LOC"	, sRtnBedDan); 
					jsRtn.addRecord(jrRtn);
				}
			}	
						
			commUtils.printLog(logId, methodNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }//end of try~catch				
		return jsRtn;
	}    
 
	/**
	 *      [A] 오퍼레이션명 : 평점분석
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procGradeAnalysis( String logId, String methodNms, JDTORecord jrResult, JDTORecord jrSchRule) throws JDTOException {
    	String methodNm = "평점분석[BCoilReSchSeEJB.procGradeAnalysis] < " + methodNms;
    	String szLogMsg			= null;
    	String szGRIDE  		= "99";

    	String szSTOCK_ID 		= commUtils.trim(jrResult.getFieldString("STOCK_ID"  ));
    	String szSTACK_COL_GP 	= commUtils.trim(jrResult.getFieldString("TAG_STACK_COL_GP"  ));
		String szSTACK_BED_GP 	= commUtils.trim(jrResult.getFieldString("TAG_STACK_BED_GP"  ));
		String szSTACK_LAYER_GP	= commUtils.trim(jrResult.getFieldString("TAG_STACK_LAYER_GP"  ));	
		
		String szLEFT_STOCK_ID 	= commUtils.trim(jrResult.getFieldString("TAG_LEFT_STOCK_ID"  ));	//2단일 경우 좌측
		String szLEFT_BED  		= commUtils.trim(jrResult.getFieldString("TAG_LEFT_BED"  ));		//2단일 경우 좌측
		String szLEFT_LAYER    	= commUtils.trim(jrResult.getFieldString("TAG_LEFT_LAYER"  ));		//2단일 경우 좌측
		
		String szRIGHT_STOCK_ID	= commUtils.trim(jrResult.getFieldString("TAG_RIGHT_STOCK_ID"  ));	//2단일 경우 우측		
		String szRIGHT_BED	   	= commUtils.trim(jrResult.getFieldString("TAG_RIGHT_BED"  ));		//2단일 경우 우측
		String szRIGHT_LAYER    = commUtils.trim(jrResult.getFieldString("TAG_RIGHT_LAYER"  ));		//2단일 경우 우측

		String szYD_CRN_SCH_ID  = commUtils.trim(jrResult.getFieldString("YD_CRN_SCH_ID"  ));		//크레인 스케쥴ID
		String szYD_SCH_CD      = commUtils.trim(jrResult.getFieldString("YD_SCH_CD"  ));		    //크레인 스케쥴코드
		
		String szGROUP_SEQ		= commUtils.trim(jrSchRule.getFieldString("GROUP_SEQ"  ));		   
		commUtils.printLog(logId, methodNm, "S+");
		
		commUtils.printLog(logId, "대상코일위치:"+ szSTACK_COL_GP+szSTACK_BED_GP+szSTACK_LAYER_GP, "SL");
		commUtils.printLog(logId, "대상코일번호:"+ szSTOCK_ID + "좌측코일번호:"+ szLEFT_STOCK_ID+ "우측코일번호:"+ szRIGHT_STOCK_ID, "SL");
		
		JDTORecord jrLocAbleRtn = JDTORecordFactory.getInstance().create();
		JDTORecord jrGradeAnalysisRtn = JDTORecordFactory.getInstance().create();
		try {
						
			JDTORecord jrTemp = JDTORecordFactory.getInstance().create();
			jrTemp.setField("STOCK_ID"			, szSTOCK_ID); 
			jrTemp.setField("STACK_COL_GP"		, szSTACK_COL_GP); 
			jrTemp.setField("STACK_BED_GP"		, szSTACK_BED_GP); 
			jrTemp.setField("STACK_LAYER_GP"	, szSTACK_LAYER_GP); 
			jrTemp.setField("LEFT_STOCK_ID"		, szLEFT_STOCK_ID);	 
			jrTemp.setField("RIGHT_STOCK_ID"	, szRIGHT_STOCK_ID);	 
			jrTemp.setField("TAG_STACK_LAYER_GP", szSTACK_LAYER_GP);
			jrTemp.setField("GROUP_SEQ", szGROUP_SEQ);
			
			 /* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getGride 
			WITH SPEC_TABLE AS (
			    -- 특정규격CHECK 
			    SELECT DTL_ITM1 AS STACK_RULE_NAME 
			      FROM USRYMA.TB_YM_RULE
			     WHERE REPR_CD_GP = 'YM004'
			       AND DEL_YN = 'N'
			),
			TEMP_DATA AS 
			(
			SELECT C.COIL_NO          AS C_COIL_NO
			     , C.CURR_PROG_CD     AS C_PROG_CD    -- 진도코드
			     , C.NEXT_PROC        AS C_NEXT_PROC  -- 차공정
			     , C.HR_SPEC_ABBSYM   AS C_SPEC_ABBSYM-- 규격약호
			     , C.ORD_NO           AS C_ORD_NO     -- 주문번호
			     , C.ORD_DTL          AS C_ORD_DTL    -- 주문행번
			     , C.RECEIPT_DATE     AS C_RECEIPT_DT -- 입고일자
			     , C.MILL_INI_DATE    AS C_MILL_DT    -- 압연일시
			     , C.DEMANDER_CD      AS C_DEMANDER_CD-- 수요가코드 
			     , C.COIL_T           AS C_THICK      -- 두께
			     , C.COIL_W           AS C_WIDTH      -- 폭
			     , C.COIL_WT          AS C_WEIGTH     -- 중량 
			     , C.COIL_OUTDIA      AS C_OUTDIA     -- 외경 
			     , CASE WHEN EXISTS(SELECT 1 FROM SPEC_TABLE T WHERE T.STACK_RULE_NAME=C.HR_SPEC_ABBSYM) --특정규격존재                                 
			             AND NVL(C.NEXT_PROC,1) NOT IN('5K','6K') --정정대상                                  
			             AND TRUNC((SYSDATE - C.HRMILL_CMPL_DT)*24,0)<=48 --압연시간 48시간이 안된 경우                                       
						THEN 'Y'  --짱구(적치불가)
						ELSE 'N' END  AS C_JJANG_GP  --짱구여부    
			     , L.COIL_NO          AS L_COIL_NO
			     , L.CURR_PROG_CD     AS L_PROG_CD    -- 진도코드
			     , L.NEXT_PROC        AS L_NEXT_PROC  -- 차공정
			     , L.HR_SPEC_ABBSYM   AS L_SPEC_ABBSYM-- 규격약호
			     , L.ORD_NO           AS L_ORD_NO     -- 주문번호
			     , L.ORD_DTL          AS L_ORD_DTL    -- 주문행번
			     , L.RECEIPT_DATE     AS L_RECEIPT_DT -- 입고일자
			     , L.MILL_INI_DATE    AS L_MILL_DT    -- 압연일시
			     , L.DEMANDER_CD      AS L_DEMANDER_CD-- 수요가코드 
			     , L.COIL_T           AS L_THICK      -- 두께
			     , L.COIL_W           AS L_WIDTH      -- 폭
			     , L.COIL_WT          AS L_WEIGTH     -- 중량 
			     , L.COIL_OUTDIA      AS L_OUTDIA     -- 외경 
			     , CASE WHEN EXISTS(SELECT 1 FROM SPEC_TABLE T WHERE T.STACK_RULE_NAME=L.HR_SPEC_ABBSYM) --특정규격존재                                 
			             AND NVL(L.NEXT_PROC,1) NOT IN('5K','6K') --정정대상                                  
			             AND TRUNC((SYSDATE - L.HRMILL_CMPL_DT)*24,0)<=48 --압연시간 48시간이 안된 경우                                       
						THEN 'Y'  --짱구(적치불가)
						ELSE 'N' END  AS L_JJANG_GP  --짱구여부    
			     , R.COIL_NO          AS R_COIL_NO
			     , R.CURR_PROG_CD     AS R_PROG_CD    -- 진도코드
			     , R.NEXT_PROC        AS R_NEXT_PROC  -- 차공정
			     , R.HR_SPEC_ABBSYM   AS R_SPEC_ABBSYM-- 규격약호
			     , R.ORD_NO           AS R_ORD_NO     -- 주문번호
			     , R.ORD_DTL          AS R_ORD_DTL    -- 주문행번
			     , R.RECEIPT_DATE     AS R_RECEIPT_DT -- 입고일자
			     , R.MILL_INI_DATE    AS R_MILL_DT    -- 압연일시
			     , R.DEMANDER_CD      AS R_DEMANDER_CD-- 수요가코드 
			     , R.COIL_T           AS R_THICK      -- 두께
			     , R.COIL_W           AS R_WIDTH      -- 폭
			     , R.COIL_WT          AS R_WEIGTH     -- 중량 
			     , R.COIL_OUTDIA      AS R_OUTDIA     -- 외경 
			     , CASE WHEN EXISTS(SELECT 1 FROM SPEC_TABLE T WHERE T.STACK_RULE_NAME=R.HR_SPEC_ABBSYM) --특정규격존재                                 
			             AND NVL(R.NEXT_PROC,1) NOT IN('5K','6K') --정정대상                                  
			             AND TRUNC((SYSDATE - R.HRMILL_CMPL_DT)*24,0)<=48 --압연시간 48시간이 안된 경우                                       
						THEN 'Y'  --짱구(적치불가)
						ELSE 'N' END  AS R_JJANG_GP  --짱구여부
			     , NVL((SELECT TO_NUMBER(DTL_ITM1) 
			              FROM USRYMA.TB_YM_RULE 
			             WHERE REPR_CD_GP='SCH_TO' AND CD_GP = '3' AND ITEM= 'ODIA_DIFF1'),180) AS ODIA_DIFF1     
			     , NVL((SELECT TO_NUMBER(DTL_ITM1) 
			              FROM USRYMA.TB_YM_RULE 
			             WHERE REPR_CD_GP='SCH_TO' AND CD_GP = '3' AND ITEM= 'WID_DIFF1'),200) AS WID_DIFF1     
			     , (SELECT CASE WHEN COUNT(*) > 0 THEN 'S' ELSE 'N' END   
			          FROM TB_YM_WRKBOOK A1
			             , TB_YM_WRKBOOKMTL B1
			         WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
			           AND A1.DEL_YN = 'N'
			           AND B1.DEL_YN = 'N'
			           AND B1.STOCK_ID = C.COIL_NO) AS     C_WB         
			     , (SELECT CASE WHEN COUNT(*) > 0 THEN 'S' ELSE 'N' END   
			          FROM TB_YM_WRKBOOK A1
			             , TB_YM_WRKBOOKMTL B1
			         WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
			           AND A1.DEL_YN = 'N'
			           AND B1.DEL_YN = 'N'
			           AND B1.STOCK_ID = L.COIL_NO) AS     L_WB         
			     , (SELECT CASE WHEN COUNT(*) > 0 THEN 'S' ELSE 'N' END   
			          FROM TB_YM_WRKBOOK A1
			             , TB_YM_WRKBOOKMTL B1
			         WHERE A1.YD_WBOOK_ID = B1.YD_WBOOK_ID
			           AND A1.DEL_YN = 'N'
			           AND B1.DEL_YN = 'N'
			           AND B1.STOCK_ID = R.COIL_NO) AS     R_WB                 
			  FROM (SELECT 1 T_ROW, A.* 
			          FROM USRPTA.TB_PT_COILCOMM  A
			         WHERE COIL_NO = :V_STOCK_ID            ) C        --대상코일
			     , (SELECT 1 T_ROW, A.* 
			          FROM USRPTA.TB_PT_COILCOMM  A
			         WHERE COIL_NO = :V_LEFT_STOCK_ID       ) L  --하단LEFT
			     , (SELECT 1 T_ROW, A.* 
			          FROM USRPTA.TB_PT_COILCOMM  A 
			         WHERE COIL_NO = :V_RIGHT_STOCK_ID      ) R  --하단RIGHT
			 WHERE C.T_ROW = L.T_ROW(+)          
			   AND C.T_ROW = R.T_ROW(+)      
			)
			SELECT * FROM 
			(
			SELECT CASE --//대차 우선 작업인 경우
			            WHEN :V_STACK_COL_GP LIKE '3_TC%' AND NVL(:V_GROUP_SEQ,'1')='1' THEN '1' 
			            -- 1단이면서 소재인 경우     
			            WHEN :V_TAG_STACK_LAYER_GP = '01' AND C_PROG_CD NOT IN ('F','G','H','J','K','L','M','5','6','7','8') 
			             AND ABS(TO_NUMBER(C_OUTDIA)-TO_NUMBER(CASE WHEN TO_NUMBER(L_OUTDIA) > 0 THEN  L_OUTDIA ELSE C_OUTDIA END)) < ODIA_DIFF1  --외경차이
			             AND ABS(TO_NUMBER(C_OUTDIA)-TO_NUMBER(CASE WHEN TO_NUMBER(R_OUTDIA) > 0 THEN  R_OUTDIA ELSE C_OUTDIA END)) < ODIA_DIFF1 
			            THEN CASE WHEN C_PROG_CD = L_PROG_CD AND C_PROG_CD = R_PROG_CD THEN '1' --동일(좌우) 진도코드
			                      WHEN C_PROG_CD = L_PROG_CD                           THEN '2' --동일(좌단) 진도코드
			                      WHEN C_PROG_CD = R_PROG_CD                           THEN '2' --동일(우단) 진도코드
			                      WHEN L_COIL_NO IS NULL     AND R_COIL_NO IS NOT NULL THEN '3' --좌 공BED
			                      WHEN R_COIL_NO IS NULL     AND L_COIL_NO IS NOT NULL THEN '3' --우 공BED
			                      ELSE '7' END 
			             -- 2단이면서 소재인 경우     
			            WHEN :V_TAG_STACK_LAYER_GP = '02' AND C_PROG_CD NOT IN ('F','G','H','J','K','L','M','5','6','7','8')
			             AND ABS(TO_NUMBER(C_WIDTH) -TO_NUMBER(CASE WHEN TO_NUMBER(L_WIDTH)  > 0 THEN  L_WIDTH  ELSE C_WIDTH  END)) < WID_DIFF1 --폭차이
			             AND ABS(TO_NUMBER(C_WIDTH) -TO_NUMBER(CASE WHEN TO_NUMBER(R_WIDTH)  > 0 THEN  R_WIDTH  ELSE C_WIDTH  END)) < WID_DIFF1
			            THEN CASE WHEN L_JJANG_GP = 'Y'  OR  R_JJANG_GP = 'Y'           THEN '9' -- 짱구 위에서 못 올린다.
			                      WHEN L_WB = 'S'        OR  R_WB  = 'S'                THEN '8' --좌하단우하단 작업 예약
			                      WHEN C_PROG_CD  = L_PROG_CD AND C_PROG_CD = R_PROG_CD THEN '1' --동일(좌하단우하단) 진도코드
			                      WHEN C_PROG_CD  = L_PROG_CD                           THEN '2' --동일(좌하단) 진도코드
			                      WHEN C_PROG_CD  = R_PROG_CD                           THEN '2' --동일(우하단) 진도코드
			                      ELSE '7' END 
			            -- 1단이면서 제품인 경우           
			            WHEN :V_TAG_STACK_LAYER_GP = '01' AND C_PROG_CD     IN ('F','G','H','J','K','L','M','5','6','7','8')   
			             AND ABS(TO_NUMBER(C_OUTDIA)-TO_NUMBER(CASE WHEN TO_NUMBER(L_OUTDIA) > 0 THEN  L_OUTDIA ELSE C_OUTDIA END)) < ODIA_DIFF1  --외경차이
			             AND ABS(TO_NUMBER(C_OUTDIA)-TO_NUMBER(CASE WHEN TO_NUMBER(R_OUTDIA) > 0 THEN  R_OUTDIA ELSE C_OUTDIA END)) < ODIA_DIFF1 
			             
			             
			            THEN CASE WHEN C_DEMANDER_CD = L_DEMANDER_CD             
			                       AND C_DEMANDER_CD = R_DEMANDER_CD 
			                       AND C_ORD_NO||C_ORD_DTL = L_ORD_NO||L_ORD_DTL 
			                       AND C_ORD_NO||C_ORD_DTL = R_ORD_NO||R_ORD_DTL     THEN '1' --좌우축 동일 고객사+주문번호행번
			                      WHEN C_ORD_NO||C_ORD_DTL = L_ORD_NO||L_ORD_DTL 
			                       AND C_ORD_NO||C_ORD_DTL = R_ORD_NO||R_ORD_DTL     THEN '2' --좌우측 동일 주문번호행번 
			                      WHEN C_ORD_NO||C_ORD_DTL = L_ORD_NO||L_ORD_DTL     THEN '2' --좌측   동일 주문번호행번 
			                      WHEN C_ORD_NO||C_ORD_DTL = R_ORD_NO||R_ORD_DTL     THEN '2' --우측   동일 주문번호행번 
			                      WHEN C_ORD_NO = L_ORD_NO AND C_ORD_NO = R_ORD_NO   THEN '4' --좌우측 동일 주문번호 
			                      WHEN C_ORD_NO = L_ORD_NO                           THEN '4' --우측   동일 주문번호 
			                      WHEN C_ORD_NO = R_ORD_NO                           THEN '4' --좌측   동일 주문번호 
			                      WHEN L_COIL_NO IS NULL   AND R_COIL_NO IS NOT NULL THEN '4' --좌 공BED
			                      WHEN R_COIL_NO IS NULL   AND L_COIL_NO IS NOT NULL THEN '4' --우 공BED
			                      
			                      ELSE '7' END 
			            -- 2단이면서 제품인 경우           
			            WHEN :V_TAG_STACK_LAYER_GP = '02' AND C_PROG_CD IN ('F','G','H','J','K','L','M','5','6','7','8') --제품
			             AND ABS(TO_NUMBER(C_WIDTH) -TO_NUMBER(CASE WHEN TO_NUMBER(L_WIDTH)  > 0 THEN  L_WIDTH  ELSE C_WIDTH  END)) < WID_DIFF1 --폭차이
			             AND ABS(TO_NUMBER(C_WIDTH) -TO_NUMBER(CASE WHEN TO_NUMBER(R_WIDTH)  > 0 THEN  R_WIDTH  ELSE C_WIDTH  END)) < WID_DIFF1
			            THEN CASE WHEN L_JJANG_GP = 'Y' OR  R_JJANG_GP = 'Y'           THEN '9' -- 짱구 위에서 못 올린다.
			                      WHEN L_WB = 'S'       OR  R_WB  = 'S'                THEN '8' --좌하단우하단 작업 예약
			                      WHEN C_DEMANDER_CD = L_DEMANDER_CD             
			                       AND C_DEMANDER_CD = R_DEMANDER_CD 
			                       AND C_ORD_NO||C_ORD_DTL = L_ORD_NO||L_ORD_DTL 
			                       AND C_ORD_NO||C_ORD_DTL = R_ORD_NO||R_ORD_DTL       THEN '1' --좌우하단 동일 고객사+주문번호행번
			                      WHEN C_ORD_NO||C_ORD_DTL = L_ORD_NO||L_ORD_DTL 
			                       AND C_ORD_NO||C_ORD_DTL = R_ORD_NO||R_ORD_DTL       THEN '2' --좌우하단 동일 주문번호행번 
			                      WHEN C_ORD_NO||C_ORD_DTL = L_ORD_NO||L_ORD_DTL       THEN '2' --좌하단   동일 주문번호행번 
			                      WHEN C_ORD_NO||C_ORD_DTL = R_ORD_NO||R_ORD_DTL       THEN '2' --우하단   동일 주문번호행번 
			                      WHEN C_ORD_NO = L_ORD_NO AND C_ORD_NO = R_ORD_NO     THEN '4' --좌우하단 동일 주문번호 
			                      WHEN C_ORD_NO = L_ORD_NO                             THEN '4' --우하단   동일 주문번호 
			                      WHEN C_ORD_NO = R_ORD_NO                             THEN '4' --좌하단   동일 주문번호 
			                      ELSE '7' END 
			                       
			             ELSE '9' END GRIDE           
			     , C.*        
			  FROM TEMP_DATA C
			)  
			*/
			JDTORecordSet jsCommResult = commDao.select(jrTemp, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getGride", logId, methodNm, "코일공통 평점 조회");
			JDTORecord jrCommResult = JDTORecordFactory.getInstance().create();
			
			if (jsCommResult.size() == 1) {
				jsCommResult.absolute(1);
				jrCommResult  = jsCommResult.getRecord();
				
				szGRIDE = commUtils.nvl(jrCommResult.getFieldString("GRIDE"),"9");
					
				jrGradeAnalysisRtn.setField("GRIDE"		    , szGRIDE);
				jrGradeAnalysisRtn.setField("GRADE_RTN"		, "1");
    			jrGradeAnalysisRtn.setField("GRADE_CONTENTS", "LOC:"+szSTACK_COL_GP+szSTACK_BED_GP+ szSTACK_LAYER_GP+"적치가능 위치평점:"+ szGRIDE );
            	commUtils.printLog(logId, methodNm, "S-");
    			return jrGradeAnalysisRtn;
			} else {
				szLogMsg = methodNm+ "코일공통  평점 검색 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
	        	commUtils.printLog(logId, methodNm, "S-");
				
    			jrGradeAnalysisRtn.setField("GRADE_RTN"		, "-1");
    			jrGradeAnalysisRtn.setField("GRADE_CONTENTS"	, "코일공통  평점 검색 실패 ");
				return jrGradeAnalysisRtn;
			}
		} catch(Exception e) {
			
			szLogMsg = methodNm+ "코일창고야드위치평점항목Set 중 예외발생! 예외메세지: " + e.getMessage();
			commUtils.printLog(logId, szLogMsg, "SL");
			jrGradeAnalysisRtn.setField("GRIDE"		    , YmConstant.RETN_CD_FAILURE);
			jrGradeAnalysisRtn.setField("GRADE_RTN"		, "-1");
			return jrGradeAnalysisRtn;
		}
	} //   
          
    
	/**
	 * 사용자지정작업
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public JDTORecordSet procToLocUser(String logId, String methodNms, JDTORecord jrWbook, JDTORecord jrCrnSch) throws JDTOException {
    	String methodNm = "TO위치결정:사용자지정작업[BCoilReSchSeEJB.procToLocUser] < " + methodNms;
    	String szLogMsg					= null;
		JDTORecord		jrTemp			= null;
		JDTORecordSet	jsRtn = JDTORecordFactory.getInstance().createRecordSet("Temp");
		
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------
		commUtils.printLog(logId, methodNm, "S+");

		String ydSchCd 	   	= commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"));		//크레인스케줄코드
		String ydWookId		= commUtils.trim(jrWbook.getFieldString("YD_WBOOK_ID"));	//작업예약
		String ydToLocGuide	= commUtils.trim(jrWbook.getFieldString("YD_TO_LOC_GUIDE")); //작업예약

		String StockId	   	= commUtils.trim(jrCrnSch.getFieldString("STOCK_ID"));			//크레인작업재료
		String ydCrnSchId 	= commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"));		//크레인스케줄ID
		String ydEqpId     	= commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"));			//크레인설비ID
		String ydUpWoLoc 	= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LOC"));		
		String ydUpWoLayer 	= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LAYER"));		
		boolean blTcStat 	= false;
		String sRtnBedDan 	= "";  //TO위치	
		JDTORecord jrToLocUserRtn = JDTORecordFactory.getInstance().create();
		try {
			
			String szDBLogMsg	 = "";
			commUtils.printLog(logId, methodNm, "S+");
			
			if( ydUpWoLoc.equals("") ) {
				szLogMsg = methodNm+ "크레인작업재료의  재료정보["+StockId+"]에 대한 권하 또는 권상위치 이상 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return jsRtn;
			}

			//권상재료에 따라 알맞은 적치가능한 베드 검색 방법을 적용
			jrTemp = JDTORecordFactory.getInstance().create();
			jrTemp.setField("STOCK_ID"			, StockId);											//권상 STOCK
			jrTemp.setField("YD_TO_LOC_GUIDE"	, ydToLocGuide);									//가이드
			jrTemp.setField("YD_SCH_CD"			, ydSchCd);											//스케쥴 코드
			jrTemp.setField("YD_CRN_SCH_ID"		, ydCrnSchId);										//크레인 작업지시 ID
			jrTemp.setField("YD_EQP_ID"			, ydEqpId);											//설비 번호
			jrTemp.setField("SEARCH_ALL"    	, "Y");		

			szLogMsg =  " TOSQL:["+ydCrnSchId+ "] 권상재료["+StockId +" +스케쥴 코드 : "+ ydSchCd + "의 적치가능한 베드 조회 시작";
			commUtils.printLog(logId, szLogMsg, "SL");
	      	
	    	//----------------------------------------------------------------------------------------------------------------------
			//	사용자 지정한 위치만 검색
			//  적재가능 위치 만 검색
			//----------------------------------------------------------------------------------------------------------------------
			if(ydToLocGuide.length() == 10) {
				// 차량
				szLogMsg =  " 적재위치 가이드 열+ 베드+ 단  지정된 경우 ["+ydToLocGuide+"]의 베드 조회 시작";
				commUtils.printLog(logId, szLogMsg, "SL");
				sRtnBedDan = ydToLocGuide;
				
			} else {
				commUtils.printLog(logId, szLogMsg + ydSchCd.substring(2, 4), "SL");
				  // 1단 
                 if(ydSchCd.substring(2, 4).equals("HS")||ydSchCd.substring(2, 4).equals("GF")){
                	 /* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdUserSearch1Dan 
                	 SELECT STACK_COL_GP
                	      , STACK_BED_GP 
                	      , STACK_LAYER_GP 
                	      , YD_MTL_SH 
                	   FROM
                	 (
                	 SELECT SL.STACK_COL_GP
                	      , SL.STACK_BED_GP 
                	      , SL.STACK_LAYER_GP 
                	      , COUNT(SL.STOCK_ID) AS YD_MTL_SH
                	   FROM TB_YM_STACKLAYER SL
                	  WHERE SL.DEL_YN        = 'N'
                	    AND SL.STACK_COL_GP          LIKE SUBSTR(:V_YD_TO_LOC_GUIDE,1,6) || '%' --가이드 열
                	    AND SL.STACK_BED_GP          LIKE SUBSTR(:V_YD_TO_LOC_GUIDE,7,2) || '%' --가이드 BED 
                	    AND SL.STACK_LAYER_GP='01' --차량위치는 1단만 적치
                	    AND SL.STACK_COL_GP||SL.STACK_BED_GP||SL.STACK_LAYER_GP NOT IN ( SELECT NVL(YD_DN_WO_LOC||YD_DN_WO_LAYER,'1') FROM TB_YM_CRNSCH WHERE DEL_YN = 'N')
                	    AND SL.STOCK_ID IS NULL
                	  GROUP BY SL.STACK_COL_GP, SL.STACK_BED_GP, SL.STACK_LAYER_GP 
                	  ORDER BY SL.STACK_COL_GP, SL.STACK_BED_GP, SL.STACK_LAYER_GP 
                	  )
                	 WHERE YD_MTL_SH = 0
                	    AND ROWNUM = 1
                	*/ 
     				JDTORecordSet outjsResult = commDao.select(jrTemp, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdUserSearch1Dan", logId, methodNm, "사용자TOSQL 베드 조회");
     				outjsResult.first();
					JDTORecord outjrResult = outjsResult.getRecord();
					
        			String StackColGp1 	= commUtils.trim(outjrResult.getFieldString("STACK_COL_GP"));//차량정지위치 적치열
        			String StackBedGp1	= commUtils.trim(outjrResult.getFieldString("STACK_BED_GP"));//차량정지위치 적치베드
        			String StackLayerGp1= commUtils.trim(outjrResult.getFieldString("STACK_LAYER_GP"));//차량정지위치 적치단
        			
        			sRtnBedDan = StackColGp1 + StackBedGp1 + StackLayerGp1;
     				if(sRtnBedDan.length() < 10) {
     					szLogMsg = methodNm+ "크레인작업재료의  재료정보["+StockId+"]에 대한 TO위치 결정 실패 ";
     					commUtils.printLog(logId, szLogMsg, "SL");
    					
     					return jsRtn;				
     				}        			
                 } else {
                	 
                	 /* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdUserSearch 
                	 WITH SPEC_TABLE AS (
                	     -- 특정규격CHECK 
                	     SELECT DTL_ITM1 AS STACK_RULE_NAME 
                	       FROM USRYMA.TB_YM_RULE
                	      WHERE REPR_CD_GP = 'YM004'
                	        AND DEL_YN = 'N'
                	 ), TO_LOC_TABLE AS
                	 (
                	 -- 대상 위치 SELECT
                	 SELECT 0                            AS PRIOR1 --BED 방향 우선('5','X','4')
                	      , A.STACK_COL_GP               AS TAG_STACK_COL_GP
                	      , A.STACK_BED_GP               AS TAG_STACK_BED_GP
                	      , A.STACK_LAYER_GP             AS TAG_STACK_LAYER_GP
                	      , B.STACK_COL_USAGE_CD         AS TAG_COL_USE_CD
                	      , DECODE(A.STACK_LAYER_GP, '01', LPAD(TO_NUMBER(A.STACK_BED_GP)   - 1, 2, '0'),
                	                                 '02', A.STACK_BED_GP)                                AS TAG_LEFT_BED
                	      , DECODE(A.STACK_LAYER_GP, '01', A.STACK_LAYER_GP,
                	                                 '02', LPAD(TO_NUMBER(A.STACK_LAYER_GP) - 1, 2, '0')) AS TAG_LEFT_LAYER
                	      , DECODE(A.STACK_LAYER_GP, '01', LPAD(TO_NUMBER(A.STACK_BED_GP)   + 1, 2, '0'),
                	                                 '02', LPAD(TO_NUMBER(A.STACK_BED_GP)   + 1, 2, '0')) AS TAG_RIGHT_BED
                	      , DECODE(A.STACK_LAYER_GP, '01', A.STACK_LAYER_GP,
                	                                 '02', LPAD(TO_NUMBER(A.STACK_LAYER_GP) - 1, 2, '0')) AS TAG_RIGHT_LAYER
                	       -- 단 우선순위
                	      , NVL((SELECT '0'||NVL(DAN_PRIOR,'2') 
                	               FROM TB_YM_SCHEDULERULE 
                	              WHERE YD_SCH_CD = C.YD_SCH_CD),'02')  AS RULL_DAN_PRIOR  
                	      , C.JJANG_GP  
                	      , C.STOCK_ID
                	   FROM TB_YM_STACKLAYER A
                	      , TB_YM_STACKCOL B
                	      , (
                	          SELECT K1.STACK_COL_GP
                	               , K1.STACK_BED_GP
                	               , K1.STACK_LAYER_GP
                	               , WB.YD_SCH_CD
                	               , K2.STOCK_ID
                	               , (SELECT CASE WHEN EXISTS(SELECT 1 FROM SPEC_TABLE T WHERE T.STACK_RULE_NAME=A.HR_SPEC_ABBSYM) --특정규격존재                                 
                	                                   AND NVL(A.NEXT_PROC,1) NOT IN ('5K','6K') --정정대상                                  
                	                                   AND TRUNC((SYSDATE - A.HRMILL_CMPL_DT)*24,0)<=48 --압연시간 48시간이 안된 경우                                       
                	 			                 THEN 'Y' 
                	 			                 ELSE 'N' END  
                	                    FROM USRPTA.TB_PT_COILCOMM A
                	                    WHERE COIL_NO = K1.STOCK_ID)  
                	                 AS JJANG_GP --짱구 여부
                	            FROM TB_YM_STACKLAYER K1
                	               , TB_YM_WRKBOOK    WB 
                	               , TB_YM_WRKBOOKMTL WM 
                	               , TB_YM_STOCK K2 
                	           WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
                	             AND WB.YD_SCH_CD   = :V_YD_SCH_CD 
                	             AND WM.STOCK_ID    = :V_STOCK_ID
                	             AND WM.STOCK_ID    = K1.STOCK_ID(+)
                	             AND WM.STOCK_ID    = K2.STOCK_ID
                	             AND WM.DEL_YN       = 'N'
                	             AND WB.DEL_YN = 'N'
                	         
                	        )C
                	  WHERE SUBSTR(A.STACK_COL_GP,1,1)  = '3'
                	    AND A.STACK_COL_GP          LIKE SUBSTR(:V_YD_TO_LOC_GUIDE,1,6) || '%' --가이드 열
                	    AND A.STACK_BED_GP          LIKE SUBSTR(:V_YD_TO_LOC_GUIDE,7,2) || '%' --가이드 BED
                	    AND A.STACK_LAYER_GP       IN ('01','02')
                	    AND A.STACK_LAYER_ACTIVE_STAT= 'E'
                	    AND A.STACK_LAYER_STAT       = 'E'
                	    AND A.STACK_COL_GP           = B.STACK_COL_GP
                	    AND SUBSTR(A.STACK_COL_GP,1,2)  = SUBSTR(C.STACK_COL_GP ,1,2)  

                	 ) 
                	 , TO_LOC_DATA_TABLE AS (
                	 -- TO 위치코일정보 SELECT
                	 SELECT A.PRIOR1 
                	      , A.TAG_STACK_LAYER_GP AS PRIOR3    -- 단우선순위
                	      , A.RULL_DAN_PRIOR     AS RULL_DAN_PRIOR
                	      , A.STOCK_ID           AS STOCK_ID
                	      , A.TAG_STACK_COL_GP
                	      , A.TAG_STACK_BED_GP
                	      , A.TAG_STACK_LAYER_GP
                	      , A.TAG_LEFT_BED
                	      , A.TAG_LEFT_LAYER
                	      , A.JJANG_GP
                	      , (SELECT STACK_LAYER_ACTIVE_STAT 
                	           FROM TB_YM_STACKLAYER B 
                	          WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
                	            AND B.STACK_BED_GP   = A.TAG_LEFT_BED
                	            AND B.STACK_LAYER_GP = A.TAG_LEFT_LAYER)  AS  TAG_LEFT_ACTIVE_STAT 
                	      , (SELECT STACK_LAYER_STAT 
                	           FROM TB_YM_STACKLAYER B 
                	          WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
                	            AND B.STACK_BED_GP   = A.TAG_LEFT_BED
                	            AND B.STACK_LAYER_GP = A.TAG_LEFT_LAYER)  AS  TAG_LEFT_LAYER_STAT
                	      , (SELECT STOCK_ID 
                	           FROM TB_YM_STACKLAYER B 
                	          WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
                	            AND B.STACK_BED_GP   = A.TAG_LEFT_BED
                	            AND B.STACK_LAYER_GP = A.TAG_LEFT_LAYER)  AS  TAG_LEFT_STOCK_ID
                	      , A.TAG_RIGHT_BED
                	      , A.TAG_RIGHT_LAYER
                	      , (SELECT STACK_LAYER_ACTIVE_STAT 
                	           FROM TB_YM_STACKLAYER B 
                	          WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
                	            AND B.STACK_BED_GP   = A.TAG_RIGHT_BED
                	            AND B.STACK_LAYER_GP = A.TAG_RIGHT_LAYER) AS  TAG_RIGHT_ACTIVE_STAT 
                	      , (SELECT STACK_LAYER_STAT 
                	           FROM TB_YM_STACKLAYER B 
                	          WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
                	            AND B.STACK_BED_GP   = A.TAG_RIGHT_BED
                	            AND B.STACK_LAYER_GP = A.TAG_RIGHT_LAYER) AS  TAG_RIGHT_LAYER_STAT
                	      , (SELECT STOCK_ID 
                	           FROM TB_YM_STACKLAYER B 
                	          WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
                	            AND B.STACK_BED_GP   = A.TAG_RIGHT_BED
                	            AND B.STACK_LAYER_GP = A.TAG_RIGHT_LAYER) AS  TAG_RIGHT_STOCK_ID
                	      , NVL((SELECT TO_NUMBER(DTL_ITM1) 
                	               FROM USRYMA.TB_YM_RULE 
                	              WHERE REPR_CD_GP='SCH_TO' AND CD_GP = '3' AND ITEM= 'ODIA_DIFF1'),180) AS ODIA_DIFF1     
                	      , NVL((SELECT TO_NUMBER(DTL_ITM1) 
                	               FROM USRYMA.TB_YM_RULE 
                	              WHERE REPR_CD_GP='SCH_TO' AND CD_GP = '3' AND ITEM= 'WID_DIFF1'),200) AS WID_DIFF1     
                	      , (SELECT STOCK_ID 
                	           FROM TB_YM_STACKLAYER B 
                	          WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
                	            AND B.STACK_BED_GP   = A.TAG_LEFT_BED
                	            AND B.STACK_LAYER_GP = '02') AS TAG_2DAN_LEFT_STOCK_ID
                	      , (SELECT STOCK_ID 
                	           FROM TB_YM_STACKLAYER B 
                	          WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
                	            AND B.STACK_BED_GP   = A.TAG_STACK_BED_GP
                	            AND B.STACK_LAYER_GP = '02') AS TAG_2DAN_RIGHT_STOCK_ID               
                	   FROM TO_LOC_TABLE A
                	 )

                	 SELECT * 
                	   FROM
                	 (
                	 SELECT KK.*
                	      , ROW_NUMBER() OVER(PARTITION BY PRIOR5 ORDER BY CASE WHEN RULL_DAN_PRIOR = PRIOR3 THEN '1'
                	                                                            ELSE '2' END
                	                                                     , PRIOR5
                	                                                    -- , PRIOR2 
                	                                                     , TAG_STACK_BED_GP
                	                                                     ) AS GROUP_ROW       
                	   FROM
                	         (
                	         SELECT K.* 
                	              , CASE WHEN (SUBSTR(K.TAG_STACK_COL_GP,3,2) BETWEEN '00' AND '99')  AND TAG_LEFT_STOCK_ID  IS NOT NULL AND TAG_RIGHT_STOCK_ID IS NOT NULL THEN '1'
                	                     WHEN (SUBSTR(K.TAG_STACK_COL_GP,3,2) BETWEEN '00' AND '99')  AND TAG_LEFT_STOCK_ID  IS NOT NULL                            THEN '2'
                	                     WHEN (SUBSTR(K.TAG_STACK_COL_GP,3,2) BETWEEN '00' AND '99')  AND TAG_RIGHT_STOCK_ID IS NOT NULL                            THEN '2'
                	                     ELSE '3' END AS PRIOR5 
                	              , C.COIL_NO AS C_COIL_NO      
                	              , L.COIL_NO AS L_COIL_NO          
                	              , R.COIL_NO AS R_COIL_NO       
                	              , C.COIL_OUTDIA  AS C_OUTDIA    
                	              , L.COIL_OUTDIA  AS L_OUTDIA        
                	              , R.COIL_OUTDIA  AS R_OUTDIA       
                	              , C.COIL_W       AS C_WIDTH    
                	              , L.COIL_W       AS L_WIDTH        
                	              , R.COIL_W       AS R_WIDTH  
                	              , C.COIL_WT      AS C_WEIGTH    
                	              , L.COIL_WT      AS L_WEIGTH        
                	              , R.COIL_WT      AS R_WEIGTH
                	              , C.COIL_T  AS C_THICK    
                	              , L.COIL_T  AS L_THICK        
                	              , R.COIL_T  AS R_THICK
                	           FROM TO_LOC_DATA_TABLE K
                	              , (SELECT 1 T_ROW, A.* 
                	                   FROM USRPTA.TB_PT_COILCOMM  A ) C  --대상코일
                	              , (SELECT 1 T_ROW, A.* 
                	                   FROM USRPTA.TB_PT_COILCOMM  A ) L  --하단LEFT
                	              , (SELECT 1 T_ROW, A.* 
                	                   FROM USRPTA.TB_PT_COILCOMM  A ) R  --하단RIGHT
                	          WHERE K.STOCK_ID           = C.COIL_NO(+) 
                	            AND K.TAG_LEFT_STOCK_ID  = L.COIL_NO(+) 
                	            AND K.TAG_RIGHT_STOCK_ID = R.COIL_NO(+)
                	            AND 1  = CASE WHEN TAG_STACK_LAYER_GP = '01'                     THEN 1
                	                          WHEN TAG_STACK_LAYER_GP = '02' AND JJANG_GP = 'Y'  THEN 0  -- 짱구코일 2단제외
                	                          WHEN TAG_STACK_LAYER_GP = '02'                             -- 2단일 경우 좌우 적치 상태 CHECK
                	                               AND TAG_LEFT_ACTIVE_STAT = 'E' AND TAG_RIGHT_ACTIVE_STAT = 'E' 
                	                               AND TAG_LEFT_LAYER_STAT  = 'C' AND TAG_RIGHT_LAYER_STAT  = 'C' THEN 1
                	                     ELSE 0 END 
                	            AND 1 = CASE WHEN K.TAG_STACK_LAYER_GP = '02'  THEN 1
                	                         --1단폭CHECK -> 2단폭CHECK로 변경
                	                         WHEN K.TAG_STACK_LAYER_GP = '01' AND (SELECT DTL_ITM1 
                	                                                                 FROM USRYMA.TB_YM_RULE 
                	                                                                WHERE REPR_CD_GP = 'APP044' 
                	                                                                  AND ITEM = SUBSTR(K.TAG_STACK_COL_GP,2,1)) = 'N'
                	                              AND ABS(TO_NUMBER(C.COIL_OUTDIA)-TO_NUMBER(CASE WHEN TO_NUMBER(L.COIL_OUTDIA) > 0 THEN  L.COIL_OUTDIA ELSE C.COIL_OUTDIA END)) < ODIA_DIFF1  --외경차이
                	                              AND ABS(TO_NUMBER(C.COIL_OUTDIA)-TO_NUMBER(CASE WHEN TO_NUMBER(R.COIL_OUTDIA) > 0 THEN  R.COIL_OUTDIA ELSE C.COIL_OUTDIA END)) < ODIA_DIFF1
                	                              AND ABS(TO_NUMBER(C.COIL_W) -TO_NUMBER(CASE WHEN TO_NUMBER(L.COIL_W)  > 0 THEN  L.COIL_W  ELSE C.COIL_W  END)) < WID_DIFF1
                	                              AND ABS(TO_NUMBER(C.COIL_W) -TO_NUMBER(CASE WHEN TO_NUMBER(R.COIL_W)  > 0 THEN  R.COIL_W  ELSE C.COIL_W  END)) < WID_DIFF1                    
                	                         THEN 1
                	                         WHEN K.TAG_STACK_LAYER_GP = '01' AND (SELECT DTL_ITM1 
                	                                                                 FROM USRYMA.TB_YM_RULE 
                	                                                                WHERE REPR_CD_GP = 'APP044' 
                	                                                                  AND ITEM = SUBSTR(K.TAG_STACK_COL_GP,2,1)) = 'Y'
                	                              AND ABS(TO_NUMBER(C.COIL_OUTDIA)-TO_NUMBER(CASE WHEN TO_NUMBER(L.COIL_OUTDIA) > 0 THEN  L.COIL_OUTDIA ELSE C.COIL_OUTDIA END)) < ODIA_DIFF1  --외경차이
                	                              AND ABS(TO_NUMBER(C.COIL_OUTDIA)-TO_NUMBER(CASE WHEN TO_NUMBER(R.COIL_OUTDIA) > 0 THEN  R.COIL_OUTDIA ELSE C.COIL_OUTDIA END)) < ODIA_DIFF1
                	                         THEN 1
                	                    ELSE 0 END        
                	            AND 1 = CASE WHEN K.TAG_STACK_LAYER_GP = '01' AND (TAG_2DAN_LEFT_STOCK_ID IS NOT NULL OR TAG_2DAN_RIGHT_STOCK_ID IS NOT NULL) THEN 0
                	                    ELSE 1 END                                                   
                	         ) KK       
                	 )
                	  WHERE (PRIOR5 < 3) OR (PRIOR5 = 3 AND GROUP_ROW <= CASE WHEN NVL(:V_SEARCH_ALL,'N') = 'Y'  THEN 50 ELSE 10 END))  -- 공BED 50개만 검색
                	  ORDER BY CASE WHEN RULL_DAN_PRIOR = PRIOR3 THEN '1'
                	                ELSE '2' END                             --단우선순위 
                	         , PRIOR5                                        --평점계산시 우선순위
                	         , TAG_STACK_BED_GP
     				*/ 
     				
     				JDTORecordSet outjsResult = commDao.select(jrTemp, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdUserSearchALL", logId, methodNm, "사용자TOSQL 베드 조회");
     				if (outjsResult.size() <= 0) {
     					szLogMsg = methodNm+ "적치가능한 베드 검색 실패 ";
     					commUtils.printLog(logId, szLogMsg, "SL");
     					
     					return jsRtn;
     				}
     				
     				JDTORecord	jrResult		= JDTORecordFactory.getInstance().create();
     				JDTORecord	jrResultCar		= JDTORecordFactory.getInstance().create();
     				JDTORecord	jrResultCarLay	= JDTORecordFactory.getInstance().create();
     				JDTORecordSet jsResultCar 	= JDTORecordFactory.getInstance().createRecordSet("Temp");
     				JDTORecordSet jsResultCarLay = JDTORecordFactory.getInstance().createRecordSet("Temp");
     		    	String szStackColGp 	= "";
     				String szStackBedGp 	= "";
     				String szStackLayerGp 	= "";	
     				String szToPosGrade 	= "999";
     	
     				String sRtnBed  		= "";	
     				
     				String sRtnBedDanCheck 	= "";	
     				int iToPosGrade 		= 999;
     				int iToPosGradeCheck	= 999;
     				String szCOIL_CARD_NO   = "";
     				String szUpUsageCd      = "";   //용도
     				
     			    //----------------------------------------------------------------------------------------------------------------------
     				//	적재가능 위치 SCH RULL 검색
     				//----------------------------------------------------------------------------------------------------------------------
     				JDTORecord  jrSchSule = this.procSchSule(logId, methodNms, StockId, ydSchCd);
     				
     				
     			    // 평점 CEHCK	
     				for(int i = 1; i <= outjsResult.size(); i++) {
     		
     					outjsResult.absolute(i);
     					jrResult  = outjsResult.getRecord();
     	
     					szStackColGp 	= commUtils.trim(jrResult.getFieldString("TAG_STACK_COL_GP"  ));
     					szStackBedGp 	= commUtils.trim(jrResult.getFieldString("TAG_STACK_BED_GP"  ));
     					szStackLayerGp 	= commUtils.trim(jrResult.getFieldString("TAG_STACK_LAYER_GP"  ));	
     					szUpUsageCd 	= commUtils.trim(jrResult.getFieldString("TAG_COL_USE_CD"  ));	
     	
     					// 이송차량상차스케줄 인 경우
     					if(ydSchCd.substring(2, 8).equals("PT07UM") || ydSchCd.substring(2, 8).equals("PT08UM")) {
     						
     						//권상재료에 따라 알맞은 적치가능한 베드 검색 방법을 적용
     						jrResultCar = JDTORecordFactory.getInstance().create();
     						jrResultCar.setField("STOCK_ID", 	StockId);											//권상 STOCK
     				
     						szLogMsg =  " TOSQL:["+ydCrnSchId+ "] 권상재료["+StockId +" 의 적치가능한 베드 조회 시작";
     						commUtils.printLog(logId, szLogMsg, "SL");
     				      	
     				    	
     						 /* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCardNo 
     						SELECT CAR_CARD_NO 
     						  FROM USRYMA.TB_YM_STOCK
     						 WHERE STOCK_ID = :V_STOCK_ID
     						*/ 
     						jsResultCar = commDao.select(jrResultCar, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCardNo", logId, methodNm, "재료 CARD READ");
     						if (jsResultCar.size() > 0) {
     							
     							
     							szCOIL_CARD_NO  = commUtils.trim(jsResultCar.getRecord(0).getFieldString("CAR_CARD_NO"));
     							
     							//차량이적 차량별 상차위치 결정 작업
     							if(szCOIL_CARD_NO.equals("9999")){
     								sRtnBed =szStackColGp.substring(0, 4) + "09" ;
     								jrResult.setField("TAG_STACK_COL_GP"	, sRtnBed);
     							}else if(szCOIL_CARD_NO.equals("9998")){
     								sRtnBed =szStackColGp.substring(0, 4) + "08" ;
     								jrResult.setField("TAG_STACK_COL_GP"	, sRtnBed);
     							}else if(szCOIL_CARD_NO.equals("9997")){
     								sRtnBed =szStackColGp.substring(0, 4) + "07" ;
     								jrResult.setField("TAG_STACK_COL_GP"	, sRtnBed);
     							}else if(szCOIL_CARD_NO.equals("9996")){
     								sRtnBed =szStackColGp.substring(0, 4) + "06" ;
     								jrResult.setField("TAG_STACK_COL_GP"	, sRtnBed);
     							}else if(szCOIL_CARD_NO.equals("9995")){
     								sRtnBed =szStackColGp.substring(0, 4) + "05" ;
     								jrResult.setField("TAG_STACK_COL_GP"	, sRtnBed);
     							}	
     							
     							//차량별 상차위치에 따른 상차 단위치 베드 정보 조회 작업 
     							if(	szCOIL_CARD_NO.equals("9999")||
     									szCOIL_CARD_NO.equals("9998")||
     									szCOIL_CARD_NO.equals("9997")||
     									szCOIL_CARD_NO.equals("9996")||
     									szCOIL_CARD_NO.equals("9995") ){
     						
     								
     								//권상재료에 따라 알맞은 적치가능한 베드 검색 방법을 적용
     								jrResultCarLay = JDTORecordFactory.getInstance().create();
     								jrResultCarLay.setField("STACK_COL_GP", 	szStackColGp);											//권상 STOCK
     						
     								/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCarLayer
     								SELECT *
     								 FROM (
     								    SELECT STACK_BED_GP AS TO_STACK_BED_GP
     								      FROM USRYMA.TB_YM_STACKLAYER
     								     WHERE STACK_COL_GP =: V_STACK_COL_GP
     								       AND STOCK_ID IS NULL
     								     ORDER BY STACK_BED_GP
     								    ) A
     								 WHERE ROWNUM<=1
     								*/ 
     								
     								jsResultCarLay = commDao.select(jrResultCarLay, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCarLayer", logId, methodNm, "재료 CARD READ");
     								if (jsResultCarLay.size() > 0) {
     									jrResult.setField("TAG_STACK_BED_GP", commUtils.trim(jsResultCarLay.getRecord(0).getFieldString("TO_STACK_BED_GP")));
     								}
     							}
     						}  
     					}
     					

     					//평점 분석  /////////////////////////////////////////////////////////////////////////////////////////////////////		
     					jrResult.setField("YD_CRN_SCH_ID"	, ydCrnSchId); 
     					jrResult.setField("YD_SCH_CD"	 	, ydSchCd); 
     					
     					jrToLocUserRtn = this.procGradeAnalysis(logId, methodNm, jrResult, jrSchSule);
     					
     					szToPosGrade    		= commUtils.trim(jrToLocUserRtn.getFieldString("GRIDE"  ));
     					String szToPosGradeMsg 	= commUtils.trim(jrToLocUserRtn.getFieldString("GRADE_CONTENTS"));
     					if(szToPosGradeMsg.length() > 0 ) {
     						szDBLogMsg = szDBLogMsg + szToPosGradeMsg +"\r\n";
     					}
     					
     					outjsResult.getRecord(i-1).setField("GRIDE" , szToPosGrade ); 
     				}
     				
     			    // 평점 CEHCK	 SORT
     				String szGrideSort  = "99";
     				int igride          = 10; 
     				JDTORecordSet	jsGrideResult	= JDTORecordFactory.getInstance().createRecordSet("Temp");
     				JDTORecord	    jrGrideResult	= JDTORecordFactory.getInstance().create();
     				
     				for(int i = 1; i <= igride; i++) {
     					for(int j = 1; j <= outjsResult.size(); j++) {
     						outjsResult.absolute(j);
     						jrGrideResult  = outjsResult.getRecord();
     						szGrideSort 	= commUtils.trim(jrGrideResult.getFieldString("GRIDE"  ));
     						int iGrideSort = Integer.parseInt(szGrideSort);
     		
     						if(!szGrideSort.equals(YmConstant.RETN_CD_FAILURE)) {
     						
     							if(iGrideSort == i) {
     								jsGrideResult.addRecord(outjsResult.getRecord());
     							}
     						}
     					}
     				}
     					
     			    // 적치 가능 check
     				JDTORecord	    jrAbleResult	= JDTORecordFactory.getInstance().create();
     				String szGRIDE = "9";
     				for(int i = 1; i <= jsGrideResult.size(); i++) {

     					jsGrideResult.absolute(i);
     					jrAbleResult  = jsGrideResult.getRecord();
     					
     					szGRIDE 		= commUtils.nvl (jrAbleResult.getFieldString("GRIDE"),"9");
     					szStackColGp 	= commUtils.trim(jrAbleResult.getFieldString("TAG_STACK_COL_GP"  ));
     					szStackBedGp 	= commUtils.trim(jrAbleResult.getFieldString("TAG_STACK_BED_GP"  ));
     					szStackLayerGp 	= commUtils.trim(jrAbleResult.getFieldString("TAG_STACK_LAYER_GP"  ));	

     					//적치가능 check
//     					JDTORecord jrLocAbleRtn = this.procLocAbleChk(logId, methodNm, jrAbleResult, jrSchSule);
     					EJBConnector ejbConn = new EJBConnector("default", "BCoilSchSeEJB", this);
     					JDTORecord jrLocAbleRtn = (JDTORecord)ejbConn.trx("procLocAbleChk", new Class[] { String.class, String.class, JDTORecord.class, JDTORecord.class }, new Object[] { logId, methodNm, jrAbleResult, jrSchSule});


     					String LocAbleRtn 	 = commUtils.trim(jrLocAbleRtn.getFieldString("LOC_ABLE_RTN")) ;
     					String LocAbleRtnMsg = commUtils.trim(jrLocAbleRtn.getFieldString("LOC_ABLE_CONTENTS")) ;
     					
     					if (LocAbleRtn.equals("1")){
     						szLogMsg = methodNm+ szStackColGp+szStackBedGp+ szStackLayerGp+"  적치가능 위치평점:"+ szGRIDE;
     						commUtils.printLog(logId, szLogMsg, "SL");				
     					    //적치가능 
     						JDTORecord 		jrRtn = JDTORecordFactory.getInstance().create();
     						jrRtn.setField("SEARCH"		, "U");
     						jrRtn.setField("GRADE"		, szGRIDE);
     						jrRtn.setField("SEARCH_LOC"	, sRtnBedDan); 
     						jsRtn.addRecord(jrRtn);
     					}
     				}
     				
     			}				
            }	 
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }//end of try~catch				
		return jsRtn;
	}      
  
    
    
	/**
	 * 주작업TO위치결정  -> 야드
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public JDTORecordSet procToLocPrimaryWork(String logId, String methodNms, JDTORecord jrWbook, JDTORecord jrCrnSch) throws JDTOException {
    	String methodNm = "주작업TO위치결정[BCoilReSchSeEJB.procToLocPrimaryWork] < " + methodNms;
    	String szLogMsg					= null;
		JDTORecord		jrTemp			= null;
		JDTORecordSet	jsRtn = JDTORecordFactory.getInstance().createRecordSet("Temp");
		
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------
		commUtils.printLog(logId, methodNm, "S+");

		String szYD_SCH_CD 	   		= commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"));			//크레인스케줄코드
		String szYD_WBOOK_ID		= commUtils.trim(jrWbook.getFieldString("YD_WBOOK_ID"));			//작업예약

		String szSTOCK_ID	   		= commUtils.trim(jrCrnSch.getFieldString("STOCK_ID"));			//크레인작업재료
		String szYD_CRN_SCH_ID 		= commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"));		//크레인스케줄ID
		String szYD_EQP_ID     		= commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"));			//크레인설비ID
		String szYD_UP_WO_LOC 		= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LOC"));		
		String szYD_UP_WO_LAYER 	= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LAYER"));		

		try {
			
			commUtils.printLog(logId, methodNm, "S+");
			
			if( szYD_UP_WO_LOC.equals("") ) {
				szLogMsg = methodNm+ "크레인작업재료의  재료정보["+szSTOCK_ID+"]에 대한 권하 또는 권상위치 이상 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return jsRtn;
			}
			
			JDTORecord jrToLocPrimary = JDTORecordFactory.getInstance().create();			
			
			//권상재료에 따라 알맞은 적치가능한 베드 검색 방법을 적용
			jrTemp = JDTORecordFactory.getInstance().create();
			jrTemp.setField("STOCK_ID"		, szSTOCK_ID);		//권상 STOCK
			jrTemp.setField("YD_SCH_CD"		, szYD_SCH_CD);		//스케줄 코드
			jrTemp.setField("YD_EQP_ID"		, szYD_EQP_ID);		//설비ID
			jrTemp.setField("YD_CRN_SCH_ID"	, szYD_CRN_SCH_ID);	//크레인 스케쥴 ID
			jrTemp.setField("YD_UP_WO_LOC"  , szYD_UP_WO_LOC);
			jrTemp.setField("SEARCH_ALL"    	, "Y");		
			
			szLogMsg =  " TOSQL:["+szYD_CRN_SCH_ID+ "] 권상재료["+szSTOCK_ID +" 의 적치가능한 베드 조회 시작";
			commUtils.printLog(logId, szLogMsg, "SL");
			
			JDTORecordSet outjsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	//----------------------------------------------------------------------------------------------------------------------
			//	일반적인 적치가능한 베드 조회
			//  적재가능 위치 만 검색
			//----------------------------------------------------------------------------------------------------------------------
			outjsResult = commDao.select(jrTemp, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdPrimaryWorkSearchALL", logId, methodNm, "동일한 적치가능한 베드 조회");
			
			if (outjsResult.size() <= 0) {
				szLogMsg = methodNm+ "적치가능한 베드 검색 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return jsRtn;
			}
			JDTORecord	jrResult	= JDTORecordFactory.getInstance().create();
	    	String szStackColGp 	= "";
			String szStackBedGp 	= "";
			String szStackLayerGp 	= "";	
			String szToPosGrade 	= "999";

			String sRtnBedDan 		= "";	
			String sRtnBedDanCheck 	= "";	
			int iToPosGrade 		= 999;
			int iToPosGradeCheck	= 999;

		    //----------------------------------------------------------------------------------------------------------------------
			//	적재가능 위치 SCH RULL 검색
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecord  jrSchSule = this.procSchSule(logId, methodNms, szSTOCK_ID, szYD_SCH_CD);
			String szDBLogMsg = "";
			String szGROUP_SEQ = "";
		    // 평점 CEHCK	
			for(int i = 1; i <= outjsResult.size(); i++) {
	
				outjsResult.absolute(i);
				jrResult  = outjsResult.getRecord();

				szStackColGp 	= commUtils.trim(jrResult.getFieldString("TAG_STACK_COL_GP"  ));
				szStackBedGp 	= commUtils.trim(jrResult.getFieldString("TAG_STACK_BED_GP"  ));
				szStackLayerGp 	= commUtils.trim(jrResult.getFieldString("TAG_STACK_LAYER_GP"  ));	
				szGROUP_SEQ 	= commUtils.trim(jrResult.getFieldString("GROUP_SEQ"  ));
				
				
				//평점 분석  /////////////////////////////////////////////////////////////////////////////////////////////////////		
				jrResult.setField("YD_CRN_SCH_ID"	, szYD_CRN_SCH_ID); 
				jrResult.setField("YD_SCH_CD"	 	, szYD_SCH_CD); 
				jrResult.setField("GROUP_SEQ"	 	, szGROUP_SEQ); 				
				
				jrToLocPrimary = this.procGradeAnalysis(logId, methodNm, jrResult, jrSchSule);
				
				szToPosGrade    		= commUtils.trim(jrToLocPrimary.getFieldString("GRIDE"  ));
				String szToPosGradeMsg 	= commUtils.trim(jrToLocPrimary.getFieldString("GRADE_CONTENTS"));
				if(szToPosGradeMsg.length() > 0 ) {
					szDBLogMsg = szDBLogMsg + szToPosGradeMsg +"\r\n";
				}
				
				outjsResult.getRecord(i-1).setField("GRIDE" , szToPosGrade ); 
			}
			
		    // 평점 CEHCK	 SORT
			String szGrideSort  = "99";
			int igride          = 10; 
			JDTORecordSet	jsGrideResult	= JDTORecordFactory.getInstance().createRecordSet("Temp");
			JDTORecord	    jrGrideResult	= JDTORecordFactory.getInstance().create();
			
			for(int i = 1; i <= igride; i++) {
				for(int j = 1; j <= outjsResult.size(); j++) {
					outjsResult.absolute(j);
					jrGrideResult  = outjsResult.getRecord();
					szGrideSort 	= commUtils.trim(jrGrideResult.getFieldString("GRIDE"  ));
					int iGrideSort = Integer.parseInt(szGrideSort);
	
					if(!szGrideSort.equals(YmConstant.RETN_CD_FAILURE)) {
					
						if(iGrideSort == i) {
							jsGrideResult.addRecord(outjsResult.getRecord());
						}
					}
				}
			}
				
		    // 적치 가능 check
			JDTORecord	    jrAbleResult	= JDTORecordFactory.getInstance().create();
			String szGRIDE = "9";
			
			for(int i = 1; i <= jsGrideResult.size(); i++) {

				jsGrideResult.absolute(i);
				jrAbleResult  = jsGrideResult.getRecord();
				
				szGRIDE 		= commUtils.nvl (jrAbleResult.getFieldString("GRIDE"),"9");
				szStackColGp 	= commUtils.trim(jrAbleResult.getFieldString("TAG_STACK_COL_GP"  ));
				szStackBedGp 	= commUtils.trim(jrAbleResult.getFieldString("TAG_STACK_BED_GP"  ));
				szStackLayerGp 	= commUtils.trim(jrAbleResult.getFieldString("TAG_STACK_LAYER_GP"  ));	

				//적치가능 check
//				JDTORecord jrLocAbleRtn = this.procLocAbleChk(logId, methodNm, jrAbleResult, jrSchSule);
				EJBConnector ejbConn = new EJBConnector("default", "BCoilSchSeEJB", this);
				JDTORecord jrLocAbleRtn = (JDTORecord)ejbConn.trx("procLocAbleChk", new Class[] { String.class, String.class, JDTORecord.class, JDTORecord.class }, new Object[] { logId, methodNm, jrAbleResult, jrSchSule});

				String LocAbleRtn 	 = commUtils.trim(jrLocAbleRtn.getFieldString("LOC_ABLE_RTN")) ;
				String LocAbleRtnMsg = commUtils.trim(jrLocAbleRtn.getFieldString("LOC_ABLE_CONTENTS")) ;
				
				if (LocAbleRtn.equals("1")){
					szLogMsg = methodNm+ szStackColGp+szStackBedGp+ szStackLayerGp+"  적치가능 위치평점:"+ szGRIDE;
					commUtils.printLog(logId, szLogMsg, "SL");				
				    //적치가능 
					sRtnBedDan = szStackColGp+szStackBedGp+ szStackLayerGp;
					JDTORecord 		jrRtn = JDTORecordFactory.getInstance().create();
					jrRtn.setField("SEARCH"		, "P");
					jrRtn.setField("GRADE"		, szGRIDE);
					jrRtn.setField("SEARCH_LOC"	, sRtnBedDan); 
					jsRtn.addRecord(jrRtn);
				}				
			}
			
			//----------------------------------------------------------------------------------------------------------------------
			// ERROR 발생시 ?
			//----------------------------------------------------------------------------------------------------------------------
			commUtils.printLog(logId, methodNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }//end of try~catch				
		return jsRtn;
	}      

    
	/**
	 * 주작업TO위치결정  -> 야드
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public JDTORecordSet procToLocPrimaryWorkMulti(String logId, String methodNms, String sWrkFlag, String szSearchYd, JDTORecord jrWbook, JDTORecord jrCrnSch) throws JDTOException {
    	String methodNm = "주작업TO위치결정[BCoilReSchSeEJB.procToLocPrimaryWorkMulti] < " + methodNms;
    	String szLogMsg					= null;
		JDTORecord		jrTemp			= null;
		JDTORecordSet	jsRtn = JDTORecordFactory.getInstance().createRecordSet("Temp");
		
		
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------
		commUtils.printLog(logId, methodNm, "S+");

		String szYD_SCH_CD 	   		= commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"));			//크레인스케줄코드

		String szSTOCK_ID	   		= commUtils.trim(jrCrnSch.getFieldString("STOCK_ID"));			//크레인작업재료
		String szYD_CRN_SCH_ID 		= commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"));		//크레인스케줄ID
		String szYD_EQP_ID     		= commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"));			//크레인설비ID
		String szYD_UP_WO_LOC 		= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LOC"));		

		try {
			
			commUtils.printLog(logId, methodNm, "S+");
			
			if( szYD_UP_WO_LOC.equals("") ) {
				szLogMsg = methodNm+ "크레인작업재료의  재료정보["+szSTOCK_ID+"]에 대한 권하 또는 권상위치 이상 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return jsRtn;
			}
			
			JDTORecord jrToLocPrimary = JDTORecordFactory.getInstance().create();			
			
			//권상재료에 따라 알맞은 적치가능한 베드 검색 방법을 적용
			jrTemp = JDTORecordFactory.getInstance().create();
			jrTemp.setField("STOCK_ID"		, szSTOCK_ID);		//권상 STOCK
			
			if("D".equals(sWrkFlag)) {
				// 일반 Dummy 검색
				jrTemp.setField("YD_SCH_CD"		, szYD_SCH_CD);		//스케줄 코드
			} else {
				// 주작업 실패 검색
				jrTemp.setField("YD_SCH_CD"		, szSearchYd );		//스케줄 코드
			}
			jrTemp.setField("YD_EQP_ID"		, szYD_EQP_ID);		//설비ID
			jrTemp.setField("YD_CRN_SCH_ID"	, szYD_CRN_SCH_ID);	//크레인 스케쥴 ID
			jrTemp.setField("YD_UP_WO_LOC"  , szYD_UP_WO_LOC);
			jrTemp.setField("SEARCH_ALL"    , "Y");
			
			szLogMsg =  " TOSQL:["+szYD_CRN_SCH_ID+ "] 권상재료["+szSTOCK_ID +" 의 적치가능한 베드 조회 시작";
			commUtils.printLog(logId, szLogMsg, "SL");
			
			JDTORecordSet outjsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	//----------------------------------------------------------------------------------------------------------------------
			//	일반적인 적치가능한 베드 조회
			//  적재가능 위치 만 검색
			//----------------------------------------------------------------------------------------------------------------------
			String sINQ_FLAG = "N"; // 검색순서 : 1.차공정(N) 2.진도코드(P) 3.일반재(G) 99. 종료(END)
			
			JDTORecord	jrResult	= JDTORecordFactory.getInstance().create();
	    	String szStackColGp 	= "";
			String szStackBedGp 	= "";
			String szStackLayerGp 	= "";	
			String szToPosGrade 	= "999";

			String sRtnBedDan 		= "";	
			String sSearchGp 		= "Z";	
			String szGRIDE 			= "9";
			
			for (int ii = 1; ii <= 3; ii++) {
			
				if("Y".equals(sINQ_FLAG)) {
					break;
				}
				if((ii == 1) && ("N".equals(sINQ_FLAG))) {
					outjsResult = commDao.select(jrTemp, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdPrimaryWorkSearchNextProcALL", logId, methodNm, "차공정행선");
					sSearchGp = "A";
				}
				if((ii == 2) && ("N".equals(sINQ_FLAG))) {
					outjsResult = commDao.select(jrTemp, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdPrimaryWorkSearchProgCdALL", logId, methodNm, "진도코드행선");
					sSearchGp = "B";
				}
				if((ii == 3) && ("N".equals(sINQ_FLAG))) {
					outjsResult = commDao.select(jrTemp, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdPrimaryWorkSearchDummyALL", logId, methodNm, "일반재");
					sSearchGp = "C";
				}

				
			    /*****************************************
			     * 적재가능 위치 SCH RULE 검색
			     *****************************************/
				JDTORecord jrSchSule = this.procSchSule(logId, methodNms, szSTOCK_ID, szYD_SCH_CD);
				String szGROUP_SEQ = "";
			    // 평점 CEHCK	
				for (int i = 1; i <= outjsResult.size(); i++) {
		
					outjsResult.absolute(i);
					jrResult  = outjsResult.getRecord();

					szStackColGp 	= commUtils.trim(jrResult.getFieldString("TAG_STACK_COL_GP"  ));
					szStackBedGp 	= commUtils.trim(jrResult.getFieldString("TAG_STACK_BED_GP"  ));
					szStackLayerGp 	= commUtils.trim(jrResult.getFieldString("TAG_STACK_LAYER_GP"  ));	
					szGROUP_SEQ 	= commUtils.trim(jrResult.getFieldString("GROUP_SEQ"  ));
					
					//평점 분석  /////////////////////////////////////////////////////////////////////////////////////////////////////		
					jrResult.setField("YD_CRN_SCH_ID"	, szYD_CRN_SCH_ID); 
					jrResult.setField("YD_SCH_CD"	 	, szYD_SCH_CD); 
					jrResult.setField("GROUP_SEQ"	 	, szGROUP_SEQ); 				
					
					jrToLocPrimary = this.procGradeAnalysis(logId, methodNm, jrResult, jrSchSule);
					
					szToPosGrade    		= commUtils.trim(jrToLocPrimary.getFieldString("GRIDE"  ));
					
					outjsResult.getRecord(i-1).setField("GRIDE" , szToPosGrade ); 
				}
				
			    // 평점 CEHCK	 SORT
				String szGrideSort  = "99";
				int igride          = 10; 
				JDTORecordSet	jsGrideResult	= JDTORecordFactory.getInstance().createRecordSet("Temp");
				JDTORecord	    jrGrideResult	= JDTORecordFactory.getInstance().create();
				
				for (int i = 1; i <= igride; i++) {
					for (int j = 1; j <= outjsResult.size(); j++) {
						outjsResult.absolute(j);
						jrGrideResult  = outjsResult.getRecord();
						szGrideSort    = commUtils.trim(jrGrideResult.getFieldString("GRIDE"));
						int iGrideSort = Integer.parseInt(szGrideSort);
		
						if (!szGrideSort.equals(YmConstant.RETN_CD_FAILURE)) {
						
							if (iGrideSort == i) {
								jsGrideResult.addRecord(outjsResult.getRecord());
							}
						}
					}
				}
					
			    // 적치 가능 check
				JDTORecord jrAbleResult	= JDTORecordFactory.getInstance().create();
				
				for (int i = 1; i <= jsGrideResult.size(); i++) {

					jsGrideResult.absolute(i);
					jrAbleResult  = jsGrideResult.getRecord();
					
					szGRIDE 		= commUtils.nvl (jrAbleResult.getFieldString("GRIDE"),"9");
					szStackColGp 	= commUtils.trim(jrAbleResult.getFieldString("TAG_STACK_COL_GP"  ));
					szStackBedGp 	= commUtils.trim(jrAbleResult.getFieldString("TAG_STACK_BED_GP"  ));
					szStackLayerGp 	= commUtils.trim(jrAbleResult.getFieldString("TAG_STACK_LAYER_GP"  ));	

					//적치가능 check
 					EJBConnector ejbConn = new EJBConnector("default", "BCoilSchSeEJB", this);
 					JDTORecord jrLocAbleRtn = (JDTORecord)ejbConn.trx("procLocAbleChk", new Class[] { String.class, String.class, JDTORecord.class, JDTORecord.class }, new Object[] { logId, methodNm, jrAbleResult, jrSchSule});

					String LocAbleRtn 	 = commUtils.trim(jrLocAbleRtn.getFieldString("LOC_ABLE_RTN")) ;
					
					if (LocAbleRtn.equals("1")) {
						szLogMsg = methodNm+ szStackColGp+szStackBedGp+ szStackLayerGp+"  적치가능 위치평점:"+ szGRIDE;
						commUtils.printLog(logId, szLogMsg, "SL");				
					    //적치가능 
						sRtnBedDan = szStackColGp+szStackBedGp+ szStackLayerGp;
						JDTORecord 	jrRtn = JDTORecordFactory.getInstance().create();
						jrRtn.setField("SEARCH"		, sSearchGp);
						jrRtn.setField("GRADE"		, szGRIDE);
						jrRtn.setField("SEARCH_LOC"	, sRtnBedDan); 
						jsRtn.addRecord(jrRtn);
						sINQ_FLAG = "Y";//TO위치 검색 완료
					}				
				}
			} 

			commUtils.printLog(logId, methodNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }//end of try~catch				
		return jsRtn;
	}
    
    

    
	/**
	 * TO위치 RULE
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
    public JDTORecord procSchSule(String logId, String methodNms) throws JDTOException {
    	String methodNm = "TO위치 RULE [BCoilReSchSeEJB.procSchSule] < " + methodNms;
		JDTORecord jrResult  = commUtils.getParam(logId, methodNm, "");
		JDTORecord jrOutResult  = commUtils.getParam(logId, methodNm, "");
		JDTORecord jrParam   = commUtils.getParam(logId, methodNm, "");
		try {
			
			commUtils.printLog(logId, methodNm, "S+");
			
			jrParam.setField("REPR_CD_GP", "SCH_TO"  ); //작업구분
			jrParam.setField("CD_GP"     , "3"      ); 	//공장구분
			
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getSchToLocRule
			SELECT REPR_CD_GP,CD_GP,ITEM,REPR_CD_CONTENTS,DTL_ITM1
			  FROM USRYMA.TB_YM_RULE
			 WHERE REPR_CD_GP = :V_REPR_CD_GP  -- SCH_TO
			   AND CD_GP = :V_CD_GP            -- 3
			   AND DEL_YN = 'N'
			*/  
			JDTORecordSet jsSchToLocRule = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getSchToLocRule", logId, methodNm, "스케줄 기준 Read"); 
		    // 평점 CEHCK	
			for(int i = 1; i <= jsSchToLocRule.size(); i++) {
	 
				jsSchToLocRule.absolute(i);
				jrResult  = jsSchToLocRule.getRecord();
				if ("ODIA_DIFF1".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) {     // 1단외경편차
					jrOutResult.setField("ODIA_DIFF1", 	commUtils.nvl(jrResult.getFieldString("DTL_ITM1"), "180")); 
	        	}
				if ("WID_DIFF1".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) {      // 1단폭편차
					jrOutResult.setField("WID_DIFF1", 	commUtils.nvl(jrResult.getFieldString("DTL_ITM1"), "200")); 
	        	}
				if ("ODIA_DIFF2".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) {     // 2단외경간격
					jrOutResult.setField("ODIA_DIFF2", 	commUtils.nvl(jrResult.getFieldString("DTL_ITM1"), "550")); 
	        	}
				if ("BED_DIFF2".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) {     // 2단BED길이
					jrOutResult.setField("BED_DIFF2", 	commUtils.nvl(jrResult.getFieldString("DTL_ITM1"), "1300")); 
	        	}
				if ("WID_DIFF2".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) {      // 2단폭편차
					jrOutResult.setField("WID_DIFF2", 	commUtils.nvl(jrResult.getFieldString("DTL_ITM1"), "0")); 
	        	}
				if ("WGT_DIFF2".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) {      // 2단중량편차
					jrOutResult.setField("WGT_DIFF2", 	commUtils.nvl(jrResult.getFieldString("DTL_ITM1"), "0")); 
	        	}
				if ("WID_LIFT1".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) {      // LIFT 간격
					jrOutResult.setField("WID_LIFT1", 	commUtils.nvl(jrResult.getFieldString("DTL_ITM1"), "650")); 
	        	}

				if ("WID_SKID1".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) {      // SKID 폭
					jrOutResult.setField("WID_SKID1", 	commUtils.nvl(jrResult.getFieldString("DTL_ITM1"), "1000")); 
	        	}
				if ("SKID_SKID".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) {      // SKID 간격
					jrOutResult.setField("SKID_SKID", 	commUtils.nvl(jrResult.getFieldString("DTL_ITM1"), "1150")); 
	        	}
			}
			
			jrOutResult.setField("WID_CHK_A", "Y"); 
			jrOutResult.setField("WID_CHK_B", "Y"); 
			jrOutResult.setField("WID_CHK_C", "Y"); 
			jrOutResult.setField("WID_CHK_D", "Y"); 
			jrOutResult.setField("WID_CHK_E", "Y"); 
        	

			commUtils.printLog(logId, methodNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }//end of try~catch				
		return jrOutResult;
	}      
    
    

	/**
	 * TO위치 RULE
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
    public JDTORecord procSchSule(String logId, String methodNms, String sSTOCK_ID, String sYD_SCH_CD) throws JDTOException {
    	String methodNm = "TO위치 RULE [BCoilReSchSeEJB.procSchSule] < " + methodNms;
		JDTORecord jrResult  = commUtils.getParam(logId, methodNm, "");
		JDTORecord jrOutResult  = commUtils.getParam(logId, methodNm, "");
		JDTORecord jrParam   = commUtils.getParam(logId, methodNm, "");
		try {
			
			commUtils.printLog(logId, methodNm, "S+");
			
			jrParam.setField("REPR_CD_GP", "SCH_TO"  ); //작업구분
			jrParam.setField("CD_GP"     , "3"      ); 	//공장구분
			
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getSchToLocRule
			SELECT REPR_CD_GP,CD_GP,ITEM,REPR_CD_CONTENTS,DTL_ITM1
			  FROM USRYMA.TB_YM_RULE
			 WHERE REPR_CD_GP = :V_REPR_CD_GP  -- SCH_TO
			   AND CD_GP = :V_CD_GP            -- 3
			   AND DEL_YN = 'N'
			*/  
			JDTORecordSet jsSchToLocRule = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getSchToLocRule", logId, methodNm, "스케줄 기준 Read"); 
		    // 평점 CEHCK	
			for(int i = 1; i <= jsSchToLocRule.size(); i++) {
	 
				jsSchToLocRule.absolute(i);
				jrResult  = jsSchToLocRule.getRecord();
				if ("ODIA_DIFF1".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) {     // 1단외경편차
					jrOutResult.setField("ODIA_DIFF1", 	commUtils.nvl(jrResult.getFieldString("DTL_ITM1"), "180")); 
	        	}
				if ("WID_DIFF1".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) {      // 1단폭편차
					jrOutResult.setField("WID_DIFF1", 	commUtils.nvl(jrResult.getFieldString("DTL_ITM1"), "200")); 
	        	}
				if ("ODIA_DIFF2".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) {     // 2단외경간격
					jrOutResult.setField("ODIA_DIFF2", 	commUtils.nvl(jrResult.getFieldString("DTL_ITM1"), "550")); 
	        	}
				if ("BED_DIFF2".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) {     // 2단BED길이
					jrOutResult.setField("BED_DIFF2", 	commUtils.nvl(jrResult.getFieldString("DTL_ITM1"), "1300")); 
	        	}
				if ("WID_DIFF2".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) {      // 2단폭편차
					jrOutResult.setField("WID_DIFF2", 	commUtils.nvl(jrResult.getFieldString("DTL_ITM1"), "0")); 
	        	}
				if ("WGT_DIFF2".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) {      // 2단중량편차
					jrOutResult.setField("WGT_DIFF2", 	commUtils.nvl(jrResult.getFieldString("DTL_ITM1"), "0")); 
	        	}
				if ("WID_LIFT1".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) {      // LIFT 간격
					jrOutResult.setField("WID_LIFT1", 	commUtils.nvl(jrResult.getFieldString("DTL_ITM1"), "650")); 
	        	}

				if ("WID_SKID1".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) {      // SKID 폭
					jrOutResult.setField("WID_SKID1", 	commUtils.nvl(jrResult.getFieldString("DTL_ITM1"), "1000")); 
	        	}
				if ("SKID_SKID".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) {      // SKID 간격
					jrOutResult.setField("SKID_SKID", 	commUtils.nvl(jrResult.getFieldString("DTL_ITM1"), "1150")); 
	        	}
			}
			
			jrOutResult.setField("WID_CHK_A", "Y"); 
			jrOutResult.setField("WID_CHK_B", "Y"); 
			jrOutResult.setField("WID_CHK_C", "Y"); 
			jrOutResult.setField("WID_CHK_D", "Y"); 
			jrOutResult.setField("WID_CHK_E", "Y"); 
        	
			//----------------------------------------------------------------------------------------------------------------------------------------
			jrParam.setField("YD_SCH_CD", sYD_SCH_CD  );
			jrParam.setField("STOCK_ID"	, sSTOCK_ID  );
			
			//1단 외경편차 계산 수행 여부 기준
			JDTORecordSet jsOutdiaDevCalActYn =  commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getOutdiaDevCalActYn", logId, methodNm, "1단 외경편차 계산 수행 여부 기준");
			
			if(jsOutdiaDevCalActYn.size() > 0) {

				jrOutResult.setField("STK_RT"	    			, commUtils.trim(jsOutdiaDevCalActYn.getRecord(0).getFieldString("STK_RT"))); 	
				jrOutResult.setField("RULE_APP054"	    		, commUtils.trim(jsOutdiaDevCalActYn.getRecord(0).getFieldString("RULE_APP054"))); 	
				jrOutResult.setField("OUTDIA_DEV_CAL_ACT_YN"	, commUtils.trim(jsOutdiaDevCalActYn.getRecord(0).getFieldString("OUTDIA_DEV_CAL_ACT_YN"))); 
			} else {
				jrOutResult.setField("STK_RT"	    			, ""); 	
				jrOutResult.setField("RULE_APP054"	    		, ""); 	
				jrOutResult.setField("OUTDIA_DEV_CAL_ACT_YN"	, "Y"); 
			}
			//----------------------------------------------------------------------------------------------------------------------------------------

			commUtils.printLog(logId, methodNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }//end of try~catch				
		return jrOutResult;
	}
    
    

	/**
	 *      [A] 오퍼레이션명 : DUMMY 평점분석
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procGradeAnalysisDummy( String logId, String methodNms, JDTORecord jrResult, JDTORecord jrSchRule) throws JDTOException {
    	String methodNm = "평점분석[BCoilReSchSeEJB.procGradeAnalysisDummy] < " + methodNms;
    	String szLogMsg			= null;
    	String szGRIDE  		= "99";

    	String szSTOCK_ID 		= commUtils.trim(jrResult.getFieldString("STOCK_ID"  ));
    	String szSTACK_COL_GP 	= commUtils.trim(jrResult.getFieldString("TAG_STACK_COL_GP"  ));
		String szSTACK_BED_GP 	= commUtils.trim(jrResult.getFieldString("TAG_STACK_BED_GP"  ));
		String szSTACK_LAYER_GP	= commUtils.trim(jrResult.getFieldString("TAG_STACK_LAYER_GP"  ));	
		
		String szLEFT_STOCK_ID 	= commUtils.trim(jrResult.getFieldString("TAG_LEFT_STOCK_ID"  ));	//2단일 경우 좌측
		String szLEFT_BED  		= commUtils.trim(jrResult.getFieldString("TAG_LEFT_BED"  ));		//2단일 경우 좌측
		String szLEFT_LAYER    	= commUtils.trim(jrResult.getFieldString("TAG_LEFT_LAYER"  ));		//2단일 경우 좌측
		
		String szRIGHT_STOCK_ID	= commUtils.trim(jrResult.getFieldString("TAG_RIGHT_STOCK_ID"  ));	//2단일 경우 우측		
		String szRIGHT_BED	   	= commUtils.trim(jrResult.getFieldString("TAG_RIGHT_BED"  ));		//2단일 경우 우측
		String szRIGHT_LAYER    = commUtils.trim(jrResult.getFieldString("TAG_RIGHT_LAYER"  ));		//2단일 경우 우측

		String szYD_CRN_SCH_ID  = commUtils.trim(jrResult.getFieldString("YD_CRN_SCH_ID"  ));		//크레인 스케쥴ID
		String szYD_SCH_CD      = commUtils.trim(jrResult.getFieldString("YD_SCH_CD"  ));		    //크레인 스케쥴코드
		
		commUtils.printLog(logId, methodNm, "S+");
		
		commUtils.printLog(logId, "대상코일위치:"+ szSTACK_COL_GP+szSTACK_BED_GP+szSTACK_LAYER_GP, "SL");
		commUtils.printLog(logId, "대상코일번호:"+ szSTOCK_ID + "좌측코일번호:"+ szLEFT_STOCK_ID+ "우측코일번호:"+ szRIGHT_STOCK_ID, "SL");
		
		JDTORecord jrLocAbleRtn = JDTORecordFactory.getInstance().create();
		JDTORecord jrGradeAnalysisRtn = JDTORecordFactory.getInstance().create();
		try {
						
			JDTORecord jrTemp = JDTORecordFactory.getInstance().create();
			jrTemp.setField("STOCK_ID"			, szSTOCK_ID); 
			jrTemp.setField("STACK_COL_GP"		, szSTACK_COL_GP); 
			jrTemp.setField("STACK_BED_GP"		, szSTACK_BED_GP); 
			jrTemp.setField("STACK_LAYER_GP"	, szSTACK_LAYER_GP); 
			jrTemp.setField("LEFT_STOCK_ID"		, szLEFT_STOCK_ID);	 
			jrTemp.setField("RIGHT_STOCK_ID"	, szRIGHT_STOCK_ID);	 
			jrTemp.setField("TAG_STACK_LAYER_GP", szSTACK_LAYER_GP);	 
				
			/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getGrideDummy
			WITH SPEC_TABLE AS (
			    -- 특정규격CHECK 
			    SELECT DTL_ITM1 AS STACK_RULE_NAME 
			      FROM USRYMA.TB_YM_RULE
			     WHERE REPR_CD_GP = 'YM004'
			       AND DEL_YN = 'N'
			),
			TEMP_DATA AS 
			(
			SELECT C.COIL_NO          AS C_COIL_NO
			     , C.CURR_PROG_CD     AS C_PROG_CD    -- 진도코드
			     , C.NEXT_PROC        AS C_NEXT_PROC  -- 차공정
			     , C.HR_SPEC_ABBSYM   AS C_SPEC_ABBSYM-- 규격약호
			     , C.ORD_NO           AS C_ORD_NO     -- 주문번호
			     , C.ORD_DTL          AS C_ORD_DTL    -- 주문행번
			     , C.RECEIPT_DATE     AS C_RECEIPT_DT -- 입고일자
			     , C.MILL_INI_DATE    AS C_MILL_DT    -- 압연일시
			     , C.DEMANDER_CD      AS C_DEMANDER_CD-- 수요가코드 
			     , C.COIL_T           AS C_THICK      -- 두께
			     , C.COIL_W           AS C_WIDTH      -- 폭
			     , C.COIL_WT          AS C_WEIGTH     -- 중량 
			     , C.COIL_OUTDIA      AS C_OUTDIA     -- 외경 
			     , CASE WHEN EXISTS(SELECT 1 FROM SPEC_TABLE T WHERE T.STACK_RULE_NAME=C.HR_SPEC_ABBSYM) --특정규격존재                                 
			             AND NVL(C.NEXT_PROC,1) NOT IN('5K','6K') --정정대상                                  
			             AND TRUNC((SYSDATE - C.HRMILL_CMPL_DT)*24,0)<=48 --압연시간 48시간이 안된 경우                                       
						THEN 'Y'  --짱구(적치불가)
						ELSE 'N' END  AS C_JJANG_GP  --짱구여부    
			     , L.COIL_NO          AS L_COIL_NO
			     , L.CURR_PROG_CD     AS L_PROG_CD    -- 진도코드
			     , L.NEXT_PROC        AS L_NEXT_PROC  -- 차공정
			     , L.HR_SPEC_ABBSYM   AS L_SPEC_ABBSYM-- 규격약호
			     , L.ORD_NO           AS L_ORD_NO     -- 주문번호
			     , L.ORD_DTL          AS L_ORD_DTL    -- 주문행번
			     , L.RECEIPT_DATE     AS L_RECEIPT_DT -- 입고일자
			     , L.MILL_INI_DATE    AS L_MILL_DT    -- 압연일시
			     , L.DEMANDER_CD      AS L_DEMANDER_CD-- 수요가코드 
			     , L.COIL_T           AS L_THICK      -- 두께
			     , L.COIL_W           AS L_WIDTH      -- 폭
			     , L.COIL_WT          AS L_WEIGTH     -- 중량 
			     , L.COIL_OUTDIA      AS L_OUTDIA     -- 외경 
			     , CASE WHEN EXISTS(SELECT 1 FROM SPEC_TABLE T WHERE T.STACK_RULE_NAME=L.HR_SPEC_ABBSYM) --특정규격존재                                 
			             AND NVL(L.NEXT_PROC,1) NOT IN('5K','6K') --정정대상                                  
			             AND TRUNC((SYSDATE - L.HRMILL_CMPL_DT)*24,0)<=48 --압연시간 48시간이 안된 경우                                       
						THEN 'Y'  --짱구(적치불가)
						ELSE 'N' END  AS L_JJANG_GP  --짱구여부    
			     , R.COIL_NO          AS R_COIL_NO
			     , R.CURR_PROG_CD     AS R_PROG_CD    -- 진도코드
			     , R.NEXT_PROC        AS R_NEXT_PROC  -- 차공정
			     , R.HR_SPEC_ABBSYM   AS R_SPEC_ABBSYM-- 규격약호
			     , R.ORD_NO           AS R_ORD_NO     -- 주문번호
			     , R.ORD_DTL          AS R_ORD_DTL    -- 주문행번
			     , R.RECEIPT_DATE     AS R_RECEIPT_DT -- 입고일자
			     , R.MILL_INI_DATE    AS R_MILL_DT    -- 압연일시
			     , R.DEMANDER_CD      AS R_DEMANDER_CD-- 수요가코드 
			     , R.COIL_T           AS R_THICK      -- 두께
			     , R.COIL_W           AS R_WIDTH      -- 폭
			     , R.COIL_WT          AS R_WEIGTH     -- 중량 
			     , R.COIL_OUTDIA      AS R_OUTDIA     -- 외경 
			     , CASE WHEN EXISTS(SELECT 1 FROM SPEC_TABLE T WHERE T.STACK_RULE_NAME=R.HR_SPEC_ABBSYM) --특정규격존재                                 
			             AND NVL(R.NEXT_PROC,1) NOT IN('5K','6K') --정정대상                                  
			             AND TRUNC((SYSDATE - R.HRMILL_CMPL_DT)*24,0)<=48 --압연시간 48시간이 안된 경우                                       
						THEN 'Y'  --짱구(적치불가)
						ELSE 'N' END  AS R_JJANG_GP  --짱구여부
			  FROM (SELECT 1 T_ROW, A.* 
			          FROM USRPTA.TB_PT_COILCOMM  A
			         WHERE COIL_NO = :V_STOCK_ID            ) C        --대상코일
			     , (SELECT 1 T_ROW, A.* 
			          FROM USRPTA.TB_PT_COILCOMM  A
			         WHERE COIL_NO = :V_LEFT_STOCK_ID       ) L  --하단LEFT
			     , (SELECT 1 T_ROW, A.* 
			          FROM USRPTA.TB_PT_COILCOMM  A 
			         WHERE COIL_NO = :V_RIGHT_STOCK_ID      ) R  --하단RIGHT
			 WHERE C.T_ROW = L.T_ROW(+)          
			   AND C.T_ROW = R.T_ROW(+)      
			)
			SELECT CASE  -- 1단이면서 소재인 경우     
			            WHEN :V_TAG_STACK_LAYER_GP = '01' AND C_PROG_CD NOT IN ('F','G','H','J','K','L','M','5','6','7','8') 
			             AND ABS(TO_NUMBER(C_OUTDIA)-TO_NUMBER(CASE WHEN TO_NUMBER(L_OUTDIA) > 0 THEN  L_OUTDIA ELSE C_OUTDIA END)) < 180  --외경차이
			             AND ABS(TO_NUMBER(C_OUTDIA)-TO_NUMBER(CASE WHEN TO_NUMBER(R_OUTDIA) > 0 THEN  R_OUTDIA ELSE C_OUTDIA END)) < 180 
			             AND ABS(TO_NUMBER(C_WIDTH) -TO_NUMBER(CASE WHEN TO_NUMBER(L_WIDTH)  > 0 THEN  L_WIDTH  ELSE C_WIDTH  END)) < 200 --폭차이
			             AND ABS(TO_NUMBER(C_WIDTH) -TO_NUMBER(CASE WHEN TO_NUMBER(R_WIDTH)  > 0 THEN  R_WIDTH  ELSE C_WIDTH  END)) < 200
			            THEN CASE WHEN C_PROG_CD = L_PROG_CD AND C_PROG_CD = R_PROG_CD THEN '1' --동일(좌우) 진도코드
			                      WHEN C_PROG_CD = L_PROG_CD                           THEN '2' --동일(좌단) 진도코드
			                      WHEN C_PROG_CD = R_PROG_CD                           THEN '2' --동일(우단) 진도코드
			                      WHEN L_COIL_NO IS NULL                               THEN '3' --좌 공BED
			                      WHEN R_COIL_NO IS NULL                               THEN '3' --우 공BED
			                      ELSE '9' END 
			             -- 2단이면서 소재인 경우     
			            WHEN :V_TAG_STACK_LAYER_GP = '02' AND C_PROG_CD NOT IN ('F','G','H','J','K','L','M','5','6','7','8')
			            THEN CASE WHEN L_JJANG_GP = 'Y'  OR  R_JJANG_GP = 'Y'           THEN '9' -- 짱구 위에서 못 올린다.
			                      WHEN C_PROG_CD  = L_PROG_CD AND C_PROG_CD = R_PROG_CD THEN '1' --동일(좌하단우하단) 진도코드
			                      WHEN C_PROG_CD  = L_PROG_CD                           THEN '2' --동일(좌하단) 진도코드
			                      WHEN C_PROG_CD  = R_PROG_CD                           THEN '2' --동일(우하단) 진도코드
			                      ELSE '8' END 
			            -- 1단이면서 제품인 경우           
			            WHEN :V_TAG_STACK_LAYER_GP = '01' AND C_PROG_CD     IN ('F','G','H','J','K','L','M','5','6','7','8')   
			             AND ABS(TO_NUMBER(C_OUTDIA)-TO_NUMBER(CASE WHEN TO_NUMBER(L_OUTDIA) > 0 THEN  L_OUTDIA ELSE C_OUTDIA END)) < 180  --외경차이
			             AND ABS(TO_NUMBER(C_OUTDIA)-TO_NUMBER(CASE WHEN TO_NUMBER(R_OUTDIA) > 0 THEN  R_OUTDIA ELSE C_OUTDIA END)) < 180 
			             AND ABS(TO_NUMBER(C_WIDTH) -TO_NUMBER(CASE WHEN TO_NUMBER(L_WIDTH)  > 0 THEN  L_WIDTH  ELSE C_WIDTH  END)) < 200 --폭차이
			             AND ABS(TO_NUMBER(C_WIDTH) -TO_NUMBER(CASE WHEN TO_NUMBER(R_WIDTH)  > 0 THEN  R_WIDTH  ELSE C_WIDTH  END)) < 200
			             
			            THEN CASE WHEN C_DEMANDER_CD = L_DEMANDER_CD             
			                       AND C_DEMANDER_CD = R_DEMANDER_CD 
			                       AND C_ORD_NO||C_ORD_DTL = L_ORD_NO||L_ORD_DTL 
			                       AND C_ORD_NO||C_ORD_DTL = R_ORD_NO||R_ORD_DTL       THEN '1' --좌우축 동일 고객사+주문번호행번
			                      WHEN C_ORD_NO||C_ORD_DTL = L_ORD_NO||L_ORD_DTL 
			                       AND C_ORD_NO||C_ORD_DTL = R_ORD_NO||R_ORD_DTL       THEN '2' --좌우측 동일 주문번호행번 
			                      WHEN C_ORD_NO||C_ORD_DTL = L_ORD_NO||L_ORD_DTL       THEN '3' --좌측   동일 주문번호행번 
			                      WHEN C_ORD_NO||C_ORD_DTL = R_ORD_NO||R_ORD_DTL       THEN '3' --우측   동일 주문번호행번 
			                      WHEN C_ORD_NO = L_ORD_NO AND C_ORD_NO = R_ORD_NO     THEN '4' --좌우측 동일 주문번호 
			                      WHEN C_ORD_NO = L_ORD_NO                             THEN '5' --우측   동일 주문번호 
			                      WHEN C_ORD_NO = R_ORD_NO                             THEN '5' --좌측   동일 주문번호 
			                      WHEN L_COIL_NO IS NULL                               THEN '6' --좌측 공BED
			                      WHEN R_COIL_NO IS NULL                               THEN '6' --우측 공BED
			                      ELSE '9' END 
			            -- 2단이면서 제품인 경우           
			            WHEN :V_TAG_STACK_LAYER_GP = '02' AND C_PROG_CD IN ('F','G','H','J','K','L','M','5','6','7','8') --제품
			            THEN CASE WHEN L_JJANG_GP = 'Y' OR  R_JJANG_GP = 'Y'           THEN '9' -- 짱구 위에서 못 올린다.
			                      WHEN C_DEMANDER_CD = L_DEMANDER_CD             
			                       AND C_DEMANDER_CD = R_DEMANDER_CD 
			                       AND C_ORD_NO||C_ORD_DTL = L_ORD_NO||L_ORD_DTL 
			                       AND C_ORD_NO||C_ORD_DTL = R_ORD_NO||R_ORD_DTL       THEN '1' --좌우하단 동일 고객사+주문번호행번
			                      WHEN C_ORD_NO||C_ORD_DTL = L_ORD_NO||L_ORD_DTL 
			                       AND C_ORD_NO||C_ORD_DTL = R_ORD_NO||R_ORD_DTL       THEN '2' --좌우하단 동일 주문번호행번 
			                      WHEN C_ORD_NO||C_ORD_DTL = L_ORD_NO||L_ORD_DTL       THEN '3' --좌하단   동일 주문번호행번 
			                      WHEN C_ORD_NO||C_ORD_DTL = R_ORD_NO||R_ORD_DTL       THEN '3' --우하단   동일 주문번호행번 
			                      WHEN C_ORD_NO = L_ORD_NO AND C_ORD_NO = R_ORD_NO     THEN '4' --좌우하단 동일 주문번호 
			                      WHEN C_ORD_NO = L_ORD_NO                             THEN '5' --우하단   동일 주문번호 
			                      WHEN C_ORD_NO = R_ORD_NO                             THEN '5' --좌하단   동일 주문번호 
			                      ELSE '8' END 
			                       
			             ELSE '9' END GRIDE           
			     , C.*        
			  FROM TEMP_DATA C
			*/
			JDTORecordSet jsCommResult = commDao.select(jrTemp, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getGrideDummy", logId, methodNm, "코일공통 평점 조회");
			JDTORecord jrCommResult = JDTORecordFactory.getInstance().create();
			
			if (jsCommResult.size() == 1) {
				jsCommResult.absolute(1);
				jrCommResult  = jsCommResult.getRecord();
				
				szGRIDE = commUtils.nvl(jrCommResult.getFieldString("GRIDE"),"9");
					
				jrGradeAnalysisRtn.setField("GRIDE"		    , szGRIDE);
				jrGradeAnalysisRtn.setField("GRADE_RTN"		, "1");
    			jrGradeAnalysisRtn.setField("GRADE_CONTENTS", "LOC:"+szSTACK_COL_GP+szSTACK_BED_GP+ szSTACK_LAYER_GP+"적치가능 위치평점:"+ szGRIDE );
            	commUtils.printLog(logId, methodNm, "S-");
    			return jrGradeAnalysisRtn;
			} else {
				szLogMsg = methodNm+ "코일공통  평점 검색 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
	        	commUtils.printLog(logId, methodNm, "S-");
				
    			jrGradeAnalysisRtn.setField("GRADE_RTN"		, "-1");
    			jrGradeAnalysisRtn.setField("GRADE_CONTENTS"	, "코일공통  평점 검색 실패 ");
				return jrGradeAnalysisRtn;
			}
		} catch(Exception e) {
			
			szLogMsg = methodNm+ "코일창고야드위치평점항목Set 중 예외발생! 예외메세지: " + e.getMessage();
			commUtils.printLog(logId, szLogMsg, "SL");
			jrGradeAnalysisRtn.setField("GRIDE"		    , YmConstant.RETN_CD_FAILURE);
			jrGradeAnalysisRtn.setField("GRADE_RTN"		, "-1");
			return jrGradeAnalysisRtn;
		}
	} //         
	
	/**
	 * GridData -  권하위치변경가능 위치  (사용자 지정 span 조회)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public GridData getDownLocChangeByUser(GridData gdReq) throws DAOException {
		String methodNm = "권하위치변경가능 위치  (사용자 지정 span 조회)[BCoilJspSeEJB.getDownLocChangeByUser] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String szLogMsg					= null;
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Grid 파라미터를 JDTORecord data 로 변환
			
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			
			String sYD_CRN_SCH_ID  = commUtils.trim(gdReq.getParam("YD_CRN_SCH_ID"));
			String szToLocGuide    = commUtils.trim(gdReq.getParam("YD_GP"))
			                       + commUtils.trim(gdReq.getParam("BAY_GP"))
			                       + commUtils.trim(gdReq.getParam("SECT_GP"));

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_CRN_SCH_ID" , sYD_CRN_SCH_ID); //
			
			/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYmCrnSchDel
			SELECT A.YD_CRN_SCH_ID
			     , A.YD_WBOOK_ID
			     , B.STOCK_ID
			     , A.YD_EQP_ID 
			     , (SELECT SUBSTR(A.YD_EQP_ID,1,2)
			            || DECODE(C.YD_SCH_RNG_CD,NULL,'YD01',C.YD_SCH_RNG_CD)
			            ||'MM'
			          FROM TB_YM_SCHEDULERULE C
			         WHERE C.YD_SCH_CD = A.YD_SCH_CD
			           AND ROWNUM = 1)  AS SEARCH_YD
			     , A.YD_SCH_CD
			     , A.YD_UP_WO_LOC
			     , A.YD_UP_WO_LAYER
			  FROM TB_YM_CRNSCH A
			     , TB_YM_CRNWRKMTL B 
			 WHERE A.DEL_YN = 'N'
			   AND B.DEL_YN = 'N'
			   AND A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID
			   AND A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			 */    
			JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYmCrnSchDel", logId, methodNm,  "크레인 스케쥴 read");
			
			if(jsCrnSch.size()> 0 ) {
				
				JDTORecord jrCrnSch = jsCrnSch.getRecord(0);
				
				jrCrnSch.setField("YD_TO_LOC_GUIDE", szToLocGuide);
				
				///////////////////////////////////////////////////////////////////////////////////////
				String ydSchCd 	   	= commUtils.trim(jrCrnSch.getFieldString("YD_SCH_CD"));		//크레인스케줄코드
				String StockId	   	= commUtils.trim(jrCrnSch.getFieldString("STOCK_ID"));		//크레인작업재료
				String ydCrnSchId 	= commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"));	//크레인스케줄ID
				String sRtnBedDan 	= "";  //TO위치	
				String szDBLogMsg	 = "";
				
				JDTORecord jrToLocUserRtn = JDTORecordFactory.getInstance().create();
				
				
 				JDTORecordSet outjsResult = commDao.select(jrCrnSch, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getDownLocChangeByUser", logId, methodNm, "사용자지정 SPAN 베드 조회");
 				
 				
	 			if (outjsResult.size() > 0) {
	 				
	 				JDTORecord	jrResult		= JDTORecordFactory.getInstance().create();
	 				JDTORecord	jrResultCar		= JDTORecordFactory.getInstance().create();
	 				JDTORecord	jrResultCarLay	= JDTORecordFactory.getInstance().create();
	 				JDTORecordSet jsResultCar 	= JDTORecordFactory.getInstance().createRecordSet("Temp");
	 				JDTORecordSet jsResultCarLay = JDTORecordFactory.getInstance().createRecordSet("Temp");
	 		    	String szStackColGp 	= "";
	 				String szStackBedGp 	= "";
	 				String szStackLayerGp 	= "";	
	 				String szToPosGrade 	= "999";
	 	
	 				String sRtnBed  		= "";	
	 				
	 				String sRtnBedDanCheck 	= "";	
	 				int iToPosGrade 		= 999;
	 				int iToPosGradeCheck	= 999;
	 				String szCOIL_CARD_NO   = "";
	 				String szUpUsageCd      = "";   //용도
	 				
	 			    //----------------------------------------------------------------------------------------------------------------------
	 				//	적재가능 위치 SCH RULL 검색
	 				//----------------------------------------------------------------------------------------------------------------------
	 				JDTORecord  jrSchSule = this.procSchSule(logId, methodNm, StockId, ydSchCd);
	 				
	 				
	 			    // 평점 CEHCK	
	 				for(int i = 1; i <= outjsResult.size(); i++) {
	 		
	 					outjsResult.absolute(i);
	 					jrResult  = outjsResult.getRecord();
	 	
	 					szStackColGp 	= commUtils.trim(jrResult.getFieldString("TAG_STACK_COL_GP"  ));
	 					szStackBedGp 	= commUtils.trim(jrResult.getFieldString("TAG_STACK_BED_GP"  ));
	 					szStackLayerGp 	= commUtils.trim(jrResult.getFieldString("TAG_STACK_LAYER_GP"  ));	
	 					szUpUsageCd 	= commUtils.trim(jrResult.getFieldString("TAG_COL_USE_CD"  ));	
	 	
	 					// 이송차량상차스케줄 인 경우
	 					if(ydSchCd.substring(2, 8).equals("PT07UM") || ydSchCd.substring(2, 8).equals("PT08UM")) {
	 						
	 						//권상재료에 따라 알맞은 적치가능한 베드 검색 방법을 적용
	 						jrResultCar = JDTORecordFactory.getInstance().create();
	 						jrResultCar.setField("STOCK_ID", 	StockId);											//권상 STOCK
	 				
	 						szLogMsg =  " TOSQL:["+ydCrnSchId+ "] 권상재료["+StockId +" 의 적치가능한 베드 조회 시작";
	 						commUtils.printLog(logId, szLogMsg, "SL");
	 				      	
	 				    	
	 						 /* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCardNo 
	 						SELECT CAR_CARD_NO 
	 						  FROM USRYMA.TB_YM_STOCK
	 						 WHERE STOCK_ID = :V_STOCK_ID
	 						*/ 
	 						jsResultCar = commDao.select(jrResultCar, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCardNo", logId, methodNm, "재료 CARD READ");
	 						if (jsResultCar.size() > 0) {
	 							
	 							
	 							szCOIL_CARD_NO  = commUtils.trim(jsResultCar.getRecord(0).getFieldString("CAR_CARD_NO"));
	 							
	 							//차량이적 차량별 상차위치 결정 작업
	 							if(szCOIL_CARD_NO.equals("9999")){
	 								sRtnBed =szStackColGp.substring(0, 4) + "09" ;
	 								jrResult.setField("TAG_STACK_COL_GP"	, sRtnBed);
	 							}else if(szCOIL_CARD_NO.equals("9998")){
	 								sRtnBed =szStackColGp.substring(0, 4) + "08" ;
	 								jrResult.setField("TAG_STACK_COL_GP"	, sRtnBed);
	 							}else if(szCOIL_CARD_NO.equals("9997")){
	 								sRtnBed =szStackColGp.substring(0, 4) + "07" ;
	 								jrResult.setField("TAG_STACK_COL_GP"	, sRtnBed);
	 							}else if(szCOIL_CARD_NO.equals("9996")){
	 								sRtnBed =szStackColGp.substring(0, 4) + "06" ;
	 								jrResult.setField("TAG_STACK_COL_GP"	, sRtnBed);
	 							}else if(szCOIL_CARD_NO.equals("9995")){
	 								sRtnBed =szStackColGp.substring(0, 4) + "05" ;
	 								jrResult.setField("TAG_STACK_COL_GP"	, sRtnBed);
	 							}	
	 							
	 							//차량별 상차위치에 따른 상차 단위치 베드 정보 조회 작업 
	 							if(	szCOIL_CARD_NO.equals("9999")||
	 									szCOIL_CARD_NO.equals("9998")||
	 									szCOIL_CARD_NO.equals("9997")||
	 									szCOIL_CARD_NO.equals("9996")||
	 									szCOIL_CARD_NO.equals("9995") ){
	 						
	 								
	 								//권상재료에 따라 알맞은 적치가능한 베드 검색 방법을 적용
	 								jrResultCarLay = JDTORecordFactory.getInstance().create();
	 								jrResultCarLay.setField("STACK_COL_GP", 	szStackColGp);											//권상 STOCK
	 						
	 								/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCarLayer
	 								SELECT *
	 								 FROM (
	 								    SELECT STACK_BED_GP AS TO_STACK_BED_GP
	 								      FROM USRYMA.TB_YM_STACKLAYER
	 								     WHERE STACK_COL_GP =: V_STACK_COL_GP
	 								       AND STOCK_ID IS NULL
	 								     ORDER BY STACK_BED_GP
	 								    ) A
	 								 WHERE ROWNUM<=1
	 								*/ 
	 								
	 								jsResultCarLay = commDao.select(jrResultCarLay, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCarLayer", logId, methodNm, "재료 CARD READ");
	 								if (jsResultCarLay.size() > 0) {
	 									jrResult.setField("TAG_STACK_BED_GP", commUtils.trim(jsResultCarLay.getRecord(0).getFieldString("TO_STACK_BED_GP")));
	 								}
	 							}
	 						}  
	 					}
	 					
	
	 					//평점 분석  /////////////////////////////////////////////////////////////////////////////////////////////////////		
	 					jrResult.setField("YD_CRN_SCH_ID"	, ydCrnSchId); 
	 					jrResult.setField("YD_SCH_CD"	 	, ydSchCd); 
	 					
	 					jrToLocUserRtn = this.procGradeAnalysis(logId, methodNm, jrResult, jrSchSule);
	 					
	 					szToPosGrade    		= commUtils.trim(jrToLocUserRtn.getFieldString("GRIDE"  ));
	 					String szToPosGradeMsg 	= commUtils.trim(jrToLocUserRtn.getFieldString("GRADE_CONTENTS"));
	 					if(szToPosGradeMsg.length() > 0 ) {
	 						szDBLogMsg = szDBLogMsg + szToPosGradeMsg +"\r\n";
	 					}
	 					
	 					outjsResult.getRecord(i-1).setField("GRIDE" , szToPosGrade ); 
	 				}
	 				
	 			    // 평점 CEHCK	 SORT
	 				String szGrideSort  = "99";
	 				int igride          = 10; 
	 				JDTORecordSet	jsGrideResult	= JDTORecordFactory.getInstance().createRecordSet("Temp");
	 				JDTORecord	    jrGrideResult	= JDTORecordFactory.getInstance().create();
	 				
	 				for(int i = 1; i <= igride; i++) {
	 					for(int j = 1; j <= outjsResult.size(); j++) {
	 						outjsResult.absolute(j);
	 						jrGrideResult  = outjsResult.getRecord();
	 						szGrideSort 	= commUtils.trim(jrGrideResult.getFieldString("GRIDE"  ));
	 						int iGrideSort = Integer.parseInt(szGrideSort);
	 		
	 						if(!szGrideSort.equals(YmConstant.RETN_CD_FAILURE)) {
	 						
	 							if(iGrideSort == i) {
	 								jsGrideResult.addRecord(outjsResult.getRecord());
	 							}
	 						}
	 					}
	 				}
	 					
	 			    // 적치 가능 check
	 				JDTORecord	    jrAbleResult	= JDTORecordFactory.getInstance().create();
	 				String szGRIDE = "9";
	 				for(int i = 1; i <= jsGrideResult.size(); i++) {
	
	 					jsGrideResult.absolute(i);
	 					jrAbleResult  = jsGrideResult.getRecord();
	 					
	 					szGRIDE 		= commUtils.nvl (jrAbleResult.getFieldString("GRIDE"),"9");
	 					szStackColGp 	= commUtils.trim(jrAbleResult.getFieldString("TAG_STACK_COL_GP"  ));
	 					szStackBedGp 	= commUtils.trim(jrAbleResult.getFieldString("TAG_STACK_BED_GP"  ));
	 					szStackLayerGp 	= commUtils.trim(jrAbleResult.getFieldString("TAG_STACK_LAYER_GP"  ));	
	
	 					//적치가능 check
	// 					JDTORecord jrLocAbleRtn = this.procLocAbleChk(logId, methodNm, jrAbleResult, jrSchSule);
	 					EJBConnector ejbConn = new EJBConnector("default", "BCoilSchSeEJB", this);
	 					JDTORecord jrLocAbleRtn = (JDTORecord)ejbConn.trx("procLocAbleChk", new Class[] { String.class, String.class, JDTORecord.class, JDTORecord.class }, new Object[] { logId, methodNm, jrAbleResult, jrSchSule});
	
	
	 					String LocAbleRtn 	 = commUtils.trim(jrLocAbleRtn.getFieldString("LOC_ABLE_RTN")) ;
	 					String LocAbleRtnMsg = commUtils.trim(jrLocAbleRtn.getFieldString("LOC_ABLE_CONTENTS")) ;
	 					
	 					if (LocAbleRtn.equals("1")){
	 						szLogMsg = methodNm+ szStackColGp+szStackBedGp+ szStackLayerGp+"  적치가능 위치평점:"+ szGRIDE;
	 						commUtils.printLog(logId, szLogMsg, "SL");				
	 					    //적치가능 
	 						sRtnBedDan = szStackColGp+szStackBedGp+ szStackLayerGp;
	 						JDTORecord 		jrRtn = JDTORecordFactory.getInstance().create();
	 						jrRtn.setField("SEARCH"		, "U");
	 						jrRtn.setField("GRADE"		, szGRIDE);
	 						jrRtn.setField("SEARCH_LOC"	, sRtnBedDan); 
	 						outRecSet.addRecord(jrRtn);
	 					}
	 				}
	 			}
			}	

			//UI로 반환 할 Grid data 를 생성 
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			GridData gdRet = commUtils.jdtoRecordToGridData(gdRtn, outRecSet.toList(), gdReq);
			
			commUtils.printLog(logId, methodNm, "S-", gdReq);
			
			//gdRet.addParam("ADV_RESULT", "ok");	// 이 값으로 화면에서 상태판단
			//gdRet.setStatus("true");
			
			return gdRet;			

		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}			
	} // end of getDownLocChangeByUser	
	
	/**
	 * GridData -  Scrap 권하위치변경가능 위치  (사용자 지정 Scrap 열 조회)
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public GridData getScrapDownLocChangeByUser(GridData gdReq) throws DAOException {
		String methodNm = "Scrap 권하위치변경가능 위치  (사용자 지정 Scrap 열 조회)[BCoilJspSeEJB.getScrapDownLocChangeByUser] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String szLogMsg					= null;
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Grid 파라미터를 JDTORecord data 로 변환
			
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			
			String sYD_CRN_SCH_ID  = commUtils.trim(gdReq.getParam("YD_CRN_SCH_ID"));
			String szToLocGuide    = commUtils.trim(gdReq.getParam("YD_GP"))
			                       + commUtils.trim(gdReq.getParam("BAY_GP"))
			                       + commUtils.trim(gdReq.getParam("SCRAP_COL_GP"));

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_CRN_SCH_ID" , sYD_CRN_SCH_ID); //
			
			/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYmCrnSchDel 
			SELECT A.YD_CRN_SCH_ID,A.YD_WBOOK_ID,B.STOCK_ID 
			  FROM TB_YM_CRNSCH A
			     , TB_YM_CRNWRKMTL B 
			 WHERE A.DEL_YN = 'N'
			   AND B.DEL_YN = 'N'
			   AND A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID
			   AND A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			    
			 */
			JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYmCrnSchDel", logId, methodNm,  "크레인 스케쥴 read");

			JDTORecord jrWbook 	= JDTORecordFactory.getInstance().create();
			JDTORecord jrCrnSch	= JDTORecordFactory.getInstance().create();
			
			jrWbook.setField("YD_SCH_CD"		, jsCrnSch.getRecord(0).getFieldString("YD_SCH_CD"));
			jrWbook.setField("FLAG_YN"			, "Y");  //권하위치 변경 POPUP 창에서 사용자가  Scrap 열 선택 할 경우 "Y"
			
			jrCrnSch.setField("STOCK_ID"		, jsCrnSch.getRecord(0).getFieldString("STOCK_ID"));
			jrCrnSch.setField("YD_EQP_ID"		, jsCrnSch.getRecord(0).getFieldString("YD_EQP_ID"));
			jrCrnSch.setField("YD_UP_WO_LOC"	, jsCrnSch.getRecord(0).getFieldString("YD_UP_WO_LOC"));
			jrCrnSch.setField("YD_CRN_SCH_ID"	, jsCrnSch.getRecord(0).getFieldString("YD_CRN_SCH_ID"));
			jrCrnSch.setField("YD_TO_LOC_GUIDE"	, szToLocGuide);
			jrCrnSch.setField("YD_BAY_GP"		, jsCrnSch.getRecord(0).getFieldString("YD_SCH_CD").substring(1,2));
			jrCrnSch.setField("SUM_MTL_WT"		, "0");
			
			outRecSet = this.procScrapToLoc(logId, methodNm, jrWbook, jrCrnSch);
			
			//UI로 반환 할 Grid data 를 생성 
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			GridData gdRet = commUtils.jdtoRecordToGridData(gdRtn, outRecSet.toList(), gdReq);
			
			commUtils.printLog(logId, methodNm, "S-", gdReq);
			
			//gdRet.addParam("ADV_RESULT", "ok");	// 이 값으로 화면에서 상태판단
			//gdRet.setStatus("true");
			
			return gdRet;			

		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}			
	} // end of getScrapDownLocChangeByUser	
	
	
	/**
	 *      [A] 오퍼레이션명 : HFL/SPM/SPM2 - 회전 요구 (9)(reqRotaion)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord reqRotaion(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "HFL/SPM/SPM2 - 회전 요구[BCoilJspSeEJB.reqRotaion] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		String szLogMsg = "";
		JDTORecord jrRtn = null;	//전문 Return
		try {
			commUtils.printLog(logId, methodNm, "S+");
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			
			String msgId           			= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sMODIFIER       			= commUtils.trim(rcvMsg.getFieldString("MODIFIER" )); //수정자(Backup Only)
			
			String sYD_GP                	= commUtils.trim(rcvMsg.getFieldString("YD_GP"       ));
			String sCOIL_NO                	= commUtils.trim(rcvMsg.getFieldString("COIL_NO"     )); 
			String sBAY_GP                	= ""; 
			String sSECT_GP                	= ""; 
			String sSTACK_COL_GP            = ""; 
			String sSTACK_BED_GP			= ""; 
			String sEQUIP_NM				= "";
			String sRTN_MSG					= "";
			
			
			String sAPP060SUP_ROT_YN = ymComm.BCoilApplyYn("APP060","3","SUP_ROT");   //COIL 설비보급존 180도 회전
			if (sAPP060SUP_ROT_YN.equals("N")) {
				sRTN_MSG = "설비보급 회전 적용여부 N 입니다!!";
				commUtils.printLog(logId, sRTN_MSG, "FL");
				throw new Exception(sRTN_MSG);
			}
			/**********************************************************
			* 0. 수신 항목 값 Check
			**********************************************************/
			
			jrParam.setField("STL_NO"         , sCOIL_NO  ); //재료번호
			jrParam.setField("STOCK_ID"       , sCOIL_NO  ); //재료번호
			/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getEqpTrkStock 
			SELECT YD_GP
			     , PROC_GP
			     , EQUIP_GP
			     , STL_NO
			     , SORT_SEQ
			     , EQUIP_NM
			     , LOC_NO
			     , SKID_NO
			     , STACK_COL_GP
			     , STACK_BED_GP
			     , BAY_GP
			  FROM USRYMA.TB_YM_EQPTRACKING
			 WHERE STL_NO = :V_STOCK_ID
			   AND DEL_YN = 'N'
			   */ 
			JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getEqpTrkStock", logId, methodNm, "코일의 크래킹정보 조히 "); 
			if(rsResult.size() > 0) {
			
				sBAY_GP 		= rsResult.getRecord(0).getFieldString("STACK_COL_GP").substring(1,2);
				sSTACK_COL_GP 	= rsResult.getRecord(0).getFieldString("STACK_COL_GP");
				sSTACK_BED_GP 	= rsResult.getRecord(0).getFieldString("STACK_BED_GP");
				sSECT_GP		= rsResult.getRecord(0).getFieldString("STACK_COL_GP").substring(2, 4);
				sEQUIP_NM 		= rsResult.getRecord(0).getFieldString("EQUIP_NM");
				
				if("H".equals(sEQUIP_NM)) {//H : HFL
					//HFL A,B동 FE에 적치된 코일만 가능
					if(!( ("A".equals(sBAY_GP)||"B".equals(sBAY_GP)) && "FE".equals(sSECT_GP) )) {
						sRTN_MSG = "회전 불가능 코일입니다!! 동구분:"+sBAY_GP+",설비위치:"+sEQUIP_NM;
					}
				} else if("K".equals(sEQUIP_NM)) {//K : SPM
					//SPM B,C동 KE에 적치된 코일만 가능
					if(!( ("B".equals(sBAY_GP)||"C".equals(sBAY_GP)) && "KE".equals(sSECT_GP) )) {
						sRTN_MSG = "회전 불가능 코일입니다!! 동구분:"+sBAY_GP+",설비위치:"+sEQUIP_NM;
					}
				} else if("P".equals(sEQUIP_NM)) {//P : SPM2
					//SPM2 D,E동 KE에 적치된 코일만 가능
					if(!( ("D".equals(sBAY_GP)||"E".equals(sBAY_GP)) && "KE".equals(sSECT_GP) )) {
						sRTN_MSG = "회전 불가능 코일입니다!! 동구분:"+sBAY_GP+",설비위치:"+sEQUIP_NM;
					}
				}

			} else {
				sRTN_MSG = "트래킹정보가 없는 Coil번호 입니다!!";
				
			}
			
			//트래킹정보 오류
			if(!"".equals(sRTN_MSG)) {
				commUtils.printLog(logId, sRTN_MSG, "FL");
				throw new Exception(sRTN_MSG);
			}
			
			
			commUtils.printLog(logId, "##########sBAY_GP : "+sBAY_GP+" / sSECT_GP : "+sSECT_GP, "S+");
			
			String sYD_SCH_CD  = sYD_GP + sBAY_GP + sSECT_GP + "99MM"; // "3EKE99MM", "3AFE99MM"
			commUtils.printLog(logId, "##########sYD_SCH_CD : "+sYD_SCH_CD, "S+");
						
			

			jrParam.setField("YD_SCH_CD"      , sYD_SCH_CD); //분동코일 이적
			jrParam.setField("MODIFIER"       , sMODIFIER ); //


			
			/*********************************************************
			 * 1.크레인 선택
			 **********************************************************/
			/*
			SELECT SR.YD_WRK_CRN
			     , SR.YD_WRK_CRN_PRIOR
			     , E1.WPROG_STAT      AS WRK_WPROG_STAT       --작업크레인 야드설비상태 B
			     , E1.WORK_MODE       AS WRK_WORK_MODE        --작업크레인 야드설비작업Mode  1 ONLINE 2 OFFLINE   
			     , SR.YD_ALT_CRN
			     , E2.WPROG_STAT      AS ALT_WPROG_STAT       --대체크레인 야드설비상태
			     , E2.WORK_MODE       AS ALT_WORK_MODE        --대체크레인 야드설비작업Mode     
			  FROM TB_YM_SCHEDULERULE   SR
			     , TB_YM_EQUIP          E1
			     , TB_YM_EQUIP          E2
			 WHERE YD_SCH_CD     = :V_YD_SCH_CD
			   AND SR.YD_WRK_CRN = E1.EQUIP_GP(+)
			   AND SR.YD_ALT_CRN = E2.EQUIP_GP(+)
			   AND SR.DEL_YN     = 'N'
			   AND E1.DEL_YN(+)  = 'N'
			 */
			JDTORecordSet jrEqpInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getWrkCrnForBC");
			if (jrEqpInfo.size() <= 0) {
				throw new Exception("크레인 정보 이상");
			}
			
			String sYD_WRK_CRN_PRIOR = jrEqpInfo.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR");//작업우선순위
			String sYD_WRK_CRN       = jrEqpInfo.getRecord(0).getFieldString("YD_WRK_CRN"    ); //
			String sWRK_WPROG_STAT   = jrEqpInfo.getRecord(0).getFieldString("WRK_WPROG_STAT"); //
			String sWRK_WORK_MODE    = jrEqpInfo.getRecord(0).getFieldString("WRK_WORK_MODE" ); //
			String sYD_ALT_CRN       = jrEqpInfo.getRecord(0).getFieldString("YD_ALT_CRN"    ); //
			String sALT_WPROG_STAT   = jrEqpInfo.getRecord(0).getFieldString("ALT_WPROG_STAT"); //
			String sALT_WORK_MODE    = jrEqpInfo.getRecord(0).getFieldString("ALT_WORK_MODE" ); //
			String sYD_EQP_ID = sYD_WRK_CRN;
			String sEQP_STAT  = sWRK_WPROG_STAT;
			
			if ("B".equals(sWRK_WPROG_STAT) || "2".equals(sWRK_WORK_MODE)) {
				if (!"".equals(sYD_ALT_CRN) && !"B".equals(sALT_WPROG_STAT) && !"2".equals(sALT_WORK_MODE)) {
					sYD_EQP_ID = sYD_ALT_CRN;
					sEQP_STAT  = sALT_WPROG_STAT;
				}
			}
			
			/**********************************************************
			* 동일분동코일 작업스케줄 있을 시에 스케줄 생성안함
			**********************************************************/
			/*
				SELECT CS.*
				  FROM TB_YM_CRNSCH    CS
				     , TB_YM_CRNWRKMTL CM
				 WHERE 1 = 1
				   AND CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
				   AND CS.DEL_YN = 'N'
				   AND CM.DEL_YN = 'N'
				   AND CS.YD_SCH_CD = :V_YD_SCH_CD
				   AND CM.STOCK_ID = :V_STOCK_ID
			 */
			JDTORecordSet jsBDSchlist = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getRotSchList", logId, methodNm, "회전스케줄 조회");
			if (jsBDSchlist.size() > 0) {
				throw new Exception("해당 코일["+sCOIL_NO+"] 회전스케줄 존재함");
			}
			
			JDTORecordSet jsCoilComm = JDTORecordFactory.getInstance().createRecordSet("");
			
			/*com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYmStockchk
			SELECT A.STOCK_ID         AS STL_NO
			      ,B.COIL_T           AS YD_MTL_T
			      ,B.COIL_W           AS YD_MTL_W
			      ,B.COIL_LEN         AS YD_MTL_L
			      ,B.COIL_WT          AS YD_MTL_WT
			      ,''                         AS YD_AIM_YD_GP
			      ,''                         AS YD_AIM_BAY_GP
			  FROM TB_YM_STOCK  A
			      ,USRPTA.TB_PT_COILCOMM B
			 WHERE  A.STOCK_ID    =B.COIL_NO 
			   AND  A.STOCK_ID   = :V_STL_NO
			 */
			jsCoilComm = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYmStockchk", logId, methodNm, "COIL 공통정보조회");
			if (jsCoilComm.size() <= 0) {
				szLogMsg =  "확인:"+sCOIL_NO+" COIL정보 조회 검색 실패.";
				commUtils.printLog(logId, szLogMsg, "SL");
			}
			jsCoilComm.first();
			JDTORecord jrCoilComm = jsCoilComm.getRecord();
			
			JDTORecord		recInBed		= null;
			JDTORecordSet jsLayerXy = JDTORecordFactory.getInstance().createRecordSet("");
			recInBed= JDTORecordFactory.getInstance().create();
			recInBed.setField("STACK_COL_GP", 			sSTACK_COL_GP); //권상지시위치
			recInBed.setField("STACK_BED_GP", 			sSTACK_BED_GP);	 //권상지시위치
			recInBed.setField("STACK_LAYER_GP", 		"01");
			
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStkLayerBybed 
			SELECT A.STACK_COL_GP 
			     , A.STACK_BED_GP 
			     , A.STACK_LAYER_X_AXIS
			     , A.STACK_LAYER_Y_AXIS
			     , A.STACK_LAYER_Z_AXIS
			     , B.YD_STK_BED_XAXIS_TOL 
			     , B.YD_STK_BED_YAXIS_TOL 
			     , B.YD_STK_BED_ZAXIS_TOL 
			     , (SELECT ROTATION_ANGLE FROM TB_YM_STACKCOL WHERE STACK_COL_GP = A.STACK_COL_GP) AS ROTATION_ANGLE
			     , A.STACK_LAYER_STAT
			  FROM TB_YM_STACKLAYER A
			     , TB_YM_STACKER    B
			 WHERE A.STACK_COL_GP = B.STACK_COL_GP
			   AND A.STACK_BED_GP = B.STACK_BED_GP
			   AND A.STACK_COL_GP = :V_STACK_COL_GP
			   AND A.STACK_BED_GP = :V_STACK_BED_GP
			   AND A.STACK_LAYER_GP = :V_STACK_LAYER_GP
			   AND A.DEL_YN ='N'
			   AND B.DEL_YN ='N'
			 */
			jsLayerXy = commDao.select(recInBed, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStkLayerBybed", logId, methodNm, "권상 BED 좌표 조회");
			if (jsLayerXy.size() <= 0) {
				szLogMsg =  "확인:"+sCOIL_NO+"권상 Layer 좌표 조회 검색 실패.";
				commUtils.printLog(logId, szLogMsg, "SL");
			}
			commUtils.printLog(logId, "##########"+szLogMsg, "S+");
			jsLayerXy.first();
			JDTORecord jrLayerXy = jsLayerXy.getRecord();
				
			/**********************************************************
			* 2. 작업예약ID 생성
			**********************************************************/
			String sYD_WBOOK_ID = commDao.getSeqId(logId, methodNm, "WrkBook");
			
			/**********************************************************
			*  3.크레인 스케줄 등록
			**********************************************************/
			JDTORecord recInCrn = JDTORecordFactory.getInstance().create();

			String sYD_CRN_SCH_ID = commDao.getSeqId(logId, methodNm, "CrnSch");
			String sYD_WO_LOC = sSTACK_COL_GP+sSTACK_BED_GP;

			recInCrn.setField("YD_CRN_SCH_ID",		sYD_CRN_SCH_ID);
			recInCrn.setField("MODIFIER",			sMODIFIER ); //
			recInCrn.setField("YD_WBOOK_ID",      	sYD_WBOOK_ID);
			recInCrn.setField("YD_EQP_ID",        	sYD_EQP_ID);
			recInCrn.setField("YD_GP",            	sYD_WO_LOC.substring(0,1));
			recInCrn.setField("YD_BAY_GP",        	sYD_WO_LOC.substring(1,2));
			recInCrn.setField("YD_SCH_CD",        	sYD_SCH_CD);	
			recInCrn.setField("YD_SCH_PRIOR",     	sYD_WRK_CRN_PRIOR);
			recInCrn.setField("YD_WRK_PROG_STAT", 	"W"); //스케즐 생성후 작업지시 전송
			recInCrn.setField("YD_MAIN_WRK_MTL_SH", "1");
			recInCrn.setField("YD_TO_LOC_GUIDE"   ,	sYD_WO_LOC);
			recInCrn.setField("YD_TO_LOC_DCSN_MTD", "S"); // S W
			recInCrn.setField("YD_EQP_WRK_SH"     , "1");
			recInCrn.setField("YD_EQP_WRK_MAX_L"  , "0");

			recInCrn.setField("YD_UP_WO_LOC",				sYD_WO_LOC);
			recInCrn.setField("YD_UP_WO_LAYER",				"01");
			recInCrn.setField("YD_UP_WO_LOC_XAXIS",  		commUtils.trim(jrLayerXy.getFieldString("STACK_LAYER_X_AXIS"  ))) ;
			recInCrn.setField("YD_UP_WO_LOC_YAXIS",  		commUtils.trim(jrLayerXy.getFieldString("STACK_LAYER_Y_AXIS"  ))) ;
			recInCrn.setField("YD_UP_WO_LOC_ZAXIS",  		commUtils.trim(jrLayerXy.getFieldString("STACK_LAYER_Z_AXIS"  )) ) ;
			recInCrn.setField("YD_UP_WO_XAXIS_GAP_MAX",  	commUtils.trim(jrLayerXy.getFieldString("YD_STK_BED_XAXIS_TOL"  )) ) ;
			recInCrn.setField("YD_UP_WO_XAXIS_GAP_MIN",  	commUtils.trim(jrLayerXy.getFieldString("YD_STK_BED_XAXIS_TOL"  )) ) ;
			recInCrn.setField("YD_UP_WO_YAXIS_GAP_MAX",  	commUtils.trim(jrLayerXy.getFieldString("YD_STK_BED_YAXIS_TOL"  )) ) ;
			recInCrn.setField("YD_UP_WO_YAXIS_GAP_MIN",  	commUtils.trim(jrLayerXy.getFieldString("YD_STK_BED_YAXIS_TOL"  )) ) ;
			recInCrn.setField("YD_UP_WO_LOC_YAXIS1",  		"" ) ;
			recInCrn.setField("YD_UP_WO_LOC_YAXIS2",  		"" ) ;
			recInCrn.setField("YD_UP_WO_ZAXIS_GAP_MAX",  	commUtils.trim(jrLayerXy.getFieldString("YD_STK_BED_ZAXIS_TOL"  )) ) ;
			recInCrn.setField("YD_UP_WO_ZAXIS_GAP_MIN",  	commUtils.trim(jrLayerXy.getFieldString("YD_STK_BED_ZAXIS_TOL"  )) ) ;
			recInCrn.setField("UP_ROTATION_ANGLE",  		commUtils.trim(jrLayerXy.getFieldString("ROTATION_ANGLE"  )) ) ;
			
			recInCrn.setField("YD_DN_WO_LOC",				sYD_WO_LOC);
			recInCrn.setField("YD_DN_WO_LAYER",				"01");
			recInCrn.setField("YD_DN_WO_LOC_XAXIS",  		commUtils.trim(jrLayerXy.getFieldString("STACK_LAYER_X_AXIS"  )) ) ;
			recInCrn.setField("YD_DN_WO_LOC_YAXIS",  		commUtils.trim(jrLayerXy.getFieldString("STACK_LAYER_Y_AXIS"  )) ) ;
			recInCrn.setField("YD_DN_WO_LOC_ZAXIS",  		commUtils.trim(jrLayerXy.getFieldString("STACK_LAYER_Z_AXIS"  )) ) ;
			recInCrn.setField("YD_DN_WO_XAXIS_GAP_MAX",  	commUtils.trim(jrLayerXy.getFieldString("YD_STK_BED_XAXIS_TOL"  )) ) ;
			recInCrn.setField("YD_DN_WO_XAXIS_GAP_MIN",  	commUtils.trim(jrLayerXy.getFieldString("YD_STK_BED_XAXIS_TOL"  )) ) ;
			recInCrn.setField("YD_DN_WO_YAXIS_GAP_MAX",  	commUtils.trim(jrLayerXy.getFieldString("YD_STK_BED_YAXIS_TOL"  )) ) ;
			recInCrn.setField("YD_DN_WO_YAXIS_GAP_MIN",  	commUtils.trim(jrLayerXy.getFieldString("YD_STK_BED_YAXIS_TOL"  )) ) ;
			recInCrn.setField("YD_DN_WO_LOC_YAXIS1",  		"" ) ;
			recInCrn.setField("YD_DN_WO_LOC_YAXIS2",  		"" ) ;
			recInCrn.setField("YD_DN_WO_ZAXIS_GAP_MAX",  	commUtils.trim(jrLayerXy.getFieldString("YD_STK_BED_ZAXIS_TOL"  )) ) ;
			recInCrn.setField("YD_DN_WO_ZAXIS_GAP_MIN",  	commUtils.trim(jrLayerXy.getFieldString("YD_STK_BED_ZAXIS_TOL"  )) ) ;
			
			String dnRotaionAngle = commUtils.trim(jrLayerXy.getFieldString("ROTATION_ANGLE"  ));
			//SPM2 보급만 권하회전각도 3으로 저장
			if(sSTACK_COL_GP.equals("3EKE01")){
				dnRotaionAngle = "3";
			}else{
				dnRotaionAngle = "2";
			}
			recInCrn.setField("DOWN_ROTATION_ANGLE",  		dnRotaionAngle ) ;			

			//기타   					
			recInCrn.setField("YD_EQP_WRK_SH", 				"1");//크레인작업재료 총매수
			recInCrn.setField("YD_EQP_WRK_WT", 				commUtils.trim(jrCoilComm.getFieldString("YD_MTL_WT" )));//크레인작업재료 총중량
			recInCrn.setField("YD_EQP_WRK_T", 				commUtils.trim(jrCoilComm.getFieldString("YD_MTL_T"  )));//크레인작업재료 총높이
			recInCrn.setField("YD_EQP_WRK_MAX_W", 			commUtils.trim(jrCoilComm.getFieldString("YD_MTL_W"  )));//크레인작업재료 중 최대 폭
			recInCrn.setField("YD_EQP_WRK_MAX_L", 			commUtils.trim(jrCoilComm.getFieldString("YD_MTL_L"  )));//크레인작업재료 중 최대 길이
			
			
			int intRtnVal = commDao.insert(recInCrn, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.insYmCrnsch", logId, methodNm, "TB_YM_CRNSCH 생성");
			if (intRtnVal < 1) {
				szLogMsg = "["+ methodNm +"]크레인 스케줄 등록중  Error!! ErrorCode: " + intRtnVal;
				throw new Exception(szLogMsg);
			}
			
			/**********************************************************
			*  4.크레인 스케줄 작업재료 등록
			**********************************************************/			
			JDTORecord recInCrnMtl = JDTORecordFactory.getInstance().create();

			recInCrnMtl.setField("YD_CRN_SCH_ID", sYD_CRN_SCH_ID);
			recInCrnMtl.setField("YD_AID_WRK_YN" , "N"); //주작업
			recInCrnMtl.setField("STOCK_ID"      , sCOIL_NO);
			recInCrnMtl.setField("STACK_LAYER_GP", "01");
			recInCrnMtl.setField("MODIFIER",			sMODIFIER ); //
			
			intRtnVal = commDao.insert(recInCrnMtl, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.insYmCrnwrkmtl", logId, methodNm, "TB_YM_CRNWRKMTL 생성");
			if (intRtnVal <= 0) {
				szLogMsg = "["+ methodNm +"] 크레인 스케줄 작업재료 등록중 실패: " + intRtnVal;
				throw new Exception(szLogMsg);
			}
			
			/**********************************************************
			* 5.크레인작업지시 호출
  			**********************************************************/
			commUtils.printLog(logId, "분동코일 작업지시 전문 송신"+methodNm, "[INFO]");

			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name

			jrYdMsg.setField("JMS_TC_CD"         , "YMA7L004"               ); //JMSTC코드
			jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
			jrYdMsg.setField("YD_CRN_SCH_ID"     , sYD_CRN_SCH_ID           ); //
			jrYdMsg.setField("MSG_GP"            , "I"           ); //전문구분
			
			jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA7L004ROT", jrYdMsg));
			
			
			//전송할 Data가 있으면 전송 처리
			if (jrRtn != null) {
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	//reqRotaion
	
	
	/**
	 * 입동지시 SMS 전송
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  jrParam
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecord procBayInWoSmsSend(GridData gdReq) throws DAOException {
		String mthdNm = "배차차량작업관리 입동지시/작업지연 SMS 전송 [BCoilJspSeEJB.procBayInWoSmsSend]  < " + gdReq.getNavigateValue();
		String logId  = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, mthdNm, "S+");
			
			String sTelNo      = ""; //전화번호
			String sCarNo      = ""; //차량번호
			String ydCarpntCd  = ""; //포인트코드
			String sMsg        = "";
			
			String sModifier   = gdReq.getParam("userid"); //수정자(Backup Only)
			String sFlag       = gdReq.getParam("SMS_FLAG"); //BAYIN, DELAY
			String sDelyRsn    = gdReq.getParam("DELY_RSN"); //작업지연사유
			String sDelyText   = gdReq.getParam("DELY_TEXT"); //작업지연사
			
			JDTORecord jrParam = commUtils.getParam(logId, mthdNm, sModifier);
			
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			//크레인작업지연 sms 전송
			String sSeq        = "";
			String ydStkColGp  = "";
			String ydBayGp     = "";
			String sPassGp     = "";

			
			if ("DELAY".equals(sFlag)) {
				for (int i = 0; i < rowCnt; i++ ) {
					ydStkColGp = commUtils.trim(commUtils.getValue(gdReq, "YD_STK_COL_GP", i)); //상차도
					ydBayGp    = ydStkColGp.substring(1, 2);
					
					if ("3".equals(ydStkColGp.substring(5, 6)) || "4".equals(ydStkColGp.substring(5, 6))) {
						sPassGp = "2";
					} else {
						sPassGp = "1";
					}
					
					for (int j = 0; j < 21; j++ ) {
						
						sSeq = j + "";
						if (j == 0) {
							sSeq = "";
						}
						
						sTelNo      = commUtils.trim(commUtils.getValue(gdReq, "TEL_NUMBER" + sSeq  , i)); //전화번호  

						sMsg        = "현재 1열연 " + ydBayGp + "동 " + sPassGp + "통로 (" + sDelyText + ")로 인하여 지연 발생중입니다. ";
						
						if ("".equals(sTelNo)) {
							continue;
						}
						
						JDTORecord recPara1 = JDTORecordFactory.getInstance().create();
						//recPara1.setField("FROM_PHONE_NO", "0416801378"); //1열연 출하반 연락처
						//recPara1.setField("TO_PHONE_NO"  , sTelNo      ); // 010-XXXX-XXXX
						//recPara1.setField("TO_CONTENT"   , sMsg        );
						//YmCommUtils.updSmsMsgSend(recPara1); // SMS 송신 
						
						// 2025.09.29 SMS송신 -> 카카오톡 전환
	                    MessageSenderTalk    sender = new MessageSenderTalk();		
	                    String subJect = "입동지시/작업지연";
	                    recPara1.setField("PHONE_NUM", sTelNo);
	                    recPara1.setField("TMPL_CD", new String("CM1"));
	                    recPara1.setField("SND_MSG", new String("[현대제철 공지사항]\n" + sMsg));
	                    recPara1.setField("SUBJECT", new String(subJect));
	                    recPara1.setField("SMS_SND_NUM", new String("0416801378"));
	                    recPara1.setField("RECV_ID","1525771");
	                    recPara1.setField("GROUP_ID","KaKao");
	                    recPara1.setField("PROGRAM_ID","udttalk");
						sender.sendTalk(recPara1);
						
						sMsg = "작업지연SMS 정상적으로 전송하였습니다.";
					}
				} //end for
			}
			
			commUtils.printLog(logId, sMsg, "SL");
			
			jrRtn.setField("RTN_CD"	, "1");
			jrRtn.setField("RTN_MSG", sMsg);

			commUtils.printLog(logId, mthdNm, "S-");
			
			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * Scrap작업TO위치결정
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public JDTORecordSet procScrapToLoc(String logId, String methodNms, JDTORecord jrWbook, JDTORecord jrCrnSch) throws JDTOException {
    	String methodNm = "TO 위치 결정:Scrap작업[BCoilSchSeEJB.procScrapToLoc] < " + methodNms;
    	String szLogMsg					= null;
		JDTORecordSet	jsRtn = JDTORecordFactory.getInstance().createRecordSet("Temp");
		
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------
		commUtils.printLog(logId, methodNm, "S+");

		String ydSchCd 	   	= commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"  ));			//크레인스케줄코드
		String ydWbookId	= commUtils.trim(jrWbook.getFieldString("YD_WBOOK_ID"  ));			//작업예약
		
		String StockId	   	= commUtils.trim(jrCrnSch.getFieldString("STOCK_ID"));			//크레인작업재료
		String ydEqpId     	= commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"  ));			//크레인설비ID
		String ydUpWoLoc 	= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LOC"  ));		
		String ydUpWoLayer 	= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LAYER"  ));		
		String ydCrnSchId 	= commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"  ));	//크레인스케줄ID
		String ydToLocGuide = commUtils.trim(jrCrnSch.getFieldString("YD_TO_LOC_GUIDE"));
		String ydBayGp	 	= commUtils.trim(jrCrnSch.getFieldString("YD_BAY_GP"));
		int    iCoilWt   	= Integer.parseInt(commUtils.nvl(jrCrnSch.getFieldString("SUM_MTL_WT"),"0")); 
		
		String sFLAG_YN     = commUtils.trim(jrWbook.getFieldString("FLAG_YN"  ));	//권하위치 변경 POPUP 창에서 사용자가  Scrap 열 선택 할 경우 "Y"
		
		JDTORecord jrToLocUserRtn = JDTORecordFactory.getInstance().create();
		try {
			
			String szDBLogMsg	 	= "";
			String sRtnBedDan 		= "";  //TO위치	
		    String szStackColGp 	= "";
 			String szStackBedGp 	= "";
 			String szStackLayerGp 	= "";	
 			
 			commUtils.printLog(logId, methodNm, "S+");
			
			if ( ydUpWoLoc.equals("") ) {
				szLogMsg = methodNm+ "크레인작업재료의  재료정보["+StockId+"]에 대한 권하 또는 권상위치 이상 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return jsRtn;
			}

			//권상재료에 따라 알맞은 적치가능한 베드 검색 방법을 적용
			JDTORecord jrTemp = JDTORecordFactory.getInstance().create();
			jrTemp.setField("STOCK_ID"			, StockId);											//권상 STOCK
			jrTemp.setField("YD_SCH_CD"			, ydSchCd);											//스케쥴 코드
			jrTemp.setField("YD_CRN_SCH_ID"		, ydCrnSchId);										//크레인 작업지시 ID
			jrTemp.setField("YD_EQP_ID"			, ydEqpId);											//설비 번호
			jrTemp.setField("COIL_WGT"			, ""+iCoilWt);											//설비 번호
			jrTemp.setField("SEARCH_ALL"    	, "Y");		

			szLogMsg =  " TOSQL:["+ydCrnSchId+ "] 권상재료["+StockId +" +스케쥴 코드 : "+ ydSchCd + "의 적치가능한 베드 조회 시작";
			commUtils.printLog(logId, szLogMsg, "SL");
	      	
	    	//----------------------------------------------------------------------------------------------------------------------
			//	사용자 지정한 위치만 검색
			//  적재가능 위치 만 검색
			//----------------------------------------------------------------------------------------------------------------------
			if (ydToLocGuide.length() == 10) {
				szLogMsg =  " 적재위치 가이드 열+ 베드+ 단  지정된 경우 ["+ydToLocGuide+"]의 베드 조회 시작";
				commUtils.printLog(logId, szLogMsg, "SL");
				sRtnBedDan = ydToLocGuide;
				
			} else {
				commUtils.printLog(logId, szLogMsg + ydSchCd.substring(2, 4), "SL");
				
				/**********************************************************
				 * ★★★★검색조건은 스크랩거 사용 3EKE02LM  -> 3ESC01LM ★★★★
				 **********************************************************/
				/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdPrimaryWorkSearchScrapNew
				WITH AUTO_CR_TABLE AS
				(
				SELECT CASE WHEN EQUIP_GP IN ('3ACRA1', '3ACRA2') AND YD_EQP_WRK_MODE2 = 'A' THEN 'Y'
				            WHEN EQUIP_GP IN ('3ACRA1', '3ACRA2') AND YD_EQP_WRK_MODE2 = 'R' THEN 'Y'
				            WHEN EQUIP_GP IN ('3CCRC1', '3CCRC2', '3CCRC3') AND YD_EQP_WRK_MODE2 = 'A' THEN 'Y'
				            WHEN EQUIP_GP IN ('3CCRC1', '3CCRC2', '3CCRC3') AND YD_EQP_WRK_MODE2 = 'R' THEN 'Y'
				            --E동 무인크레인별 위치 추가해야함
				            ELSE 'N' END AS IS_AUTO
				     , EQUIP_GP
				  FROM TB_YM_EQUIP
				 WHERE DEL_YN = 'N'
				   AND EQUIP_GP = :V_YD_EQP_ID       
				),
				TO_LOC_TABLE AS
				(
				SELECT * 
				  FROM (
				        SELECT
				               '1' AS PRIOR1 
				             , K2.HMI_STAT
				             , CASE WHEN K2.HMI_STAT = '1' AND K1.TAG_STACK_COL_GP IN ('3ESC02','3ESC03') THEN 'N'
				                    WHEN SUBSTR(K1.YD_SCH_CD,3,2) = 'YD' AND K1.TAG_STACK_COL_GP IN ('3ESC01') THEN 'N'
				                    ELSE 'Y' END  AS LOC_ABLE_YN
				             , K1.*       
				          FROM 
				               (
				                SELECT 1                      AS PRIOR2 
				                     , A.STACK_COL_GP         AS TAG_STACK_COL_GP
				                     , A.STACK_BED_GP         AS TAG_STACK_BED_GP
				                     , A.STACK_LAYER_GP       AS TAG_STACK_LAYER_GP
				                     , DECODE(A.STACK_LAYER_GP, '01', LPAD(TO_NUMBER(A.STACK_BED_GP)   - 1, 2, '0'),
				                                                '02', A.STACK_BED_GP)                                AS TAG_LEFT_BED
				                     , DECODE(A.STACK_LAYER_GP, '01', A.STACK_LAYER_GP,
				                                                '02', LPAD(TO_NUMBER(A.STACK_LAYER_GP) - 1, 2, '0')) AS TAG_LEFT_LAYER
				                     , DECODE(A.STACK_LAYER_GP, '01', LPAD(TO_NUMBER(A.STACK_BED_GP)   + 1, 2, '0'),
				                                                '02', LPAD(TO_NUMBER(A.STACK_BED_GP)   + 1, 2, '0')) AS TAG_RIGHT_BED
				                     , DECODE(A.STACK_LAYER_GP, '01', A.STACK_LAYER_GP,
				                                                '02', LPAD(TO_NUMBER(A.STACK_LAYER_GP) - 1, 2, '0')) AS TAG_RIGHT_LAYER
				                     -- 단 우선순위
				                     , '02'                   AS RULL_DAN_PRIOR  
				                     , :V_YD_SCH_CD           AS YD_SCH_CD         
				                     , :V_STOCK_ID            AS STOCK_ID
				                  FROM TB_YM_STACKLAYER A
				                     , TB_YM_STACKCOL   B
				                 WHERE A.STACK_LAYER_GP         IN ('01','02')
				                   AND A.STACK_LAYER_ACTIVE_STAT= 'E'
				                   AND A.STACK_LAYER_STAT       = 'E'
				                   AND A.STACK_COL_GP           = B.STACK_COL_GP
				                   AND A.STACK_COL_GP           IN ('3ESC01','3ESC02','3ESC03')
				                   AND SUBSTR(A.STACK_COL_GP,1,2)  = '3E'
				                   AND A.STACK_COL_GP||A.STACK_BED_GP||A.STACK_LAYER_GP NOT IN ( SELECT NVL(YD_DN_WO_LOC||YD_DN_WO_LAYER,'1') FROM TB_YM_CRNSCH WHERE DEL_YN = 'N')
				                ) K1 
				         -- HMI_STAT :0 차량없음 ,1 : 차량있음
				              , (SELECT HMI_STAT FROM TB_YM_EQUIP WHERE EQUIP_GP = '3EGT01' ) K2            
				        )      
				  WHERE LOC_ABLE_YN = 'Y'      
				--  ORDER BY PRIOR1, PRIOR2 
				)  
				, TO_LOC_DATA_TABLE AS (
				-- TO 위치코일정보 SELECT
				SELECT A.PRIOR1 
				     , A.PRIOR2 
				     , A.TAG_STACK_LAYER_GP AS PRIOR3    -- 단우선순위
				     , A.RULL_DAN_PRIOR     AS RULL_DAN_PRIOR
				     , A.STOCK_ID           AS STOCK_ID
				     , A.TAG_STACK_COL_GP   
				     , A.TAG_STACK_BED_GP
				     , A.TAG_STACK_LAYER_GP
				     , A.TAG_LEFT_BED
				     , A.TAG_LEFT_LAYER
				     , (SELECT STACK_LAYER_ACTIVE_STAT 
				          FROM TB_YM_STACKLAYER B 
				         WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
				           AND B.STACK_BED_GP   = A.TAG_LEFT_BED
				           AND B.STACK_LAYER_GP = A.TAG_LEFT_LAYER)  AS  TAG_LEFT_ACTIVE_STAT 
				     , (SELECT STACK_LAYER_STAT 
				          FROM TB_YM_STACKLAYER B 
				         WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
				           AND B.STACK_BED_GP   = A.TAG_LEFT_BED
				           AND B.STACK_LAYER_GP = A.TAG_LEFT_LAYER)  AS  TAG_LEFT_LAYER_STAT
				     , (SELECT STOCK_ID 
				          FROM TB_YM_STACKLAYER B 
				         WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
				           AND B.STACK_BED_GP   = A.TAG_LEFT_BED
				           AND B.STACK_LAYER_GP = A.TAG_LEFT_LAYER)  AS  TAG_LEFT_STOCK_ID
				     , A.TAG_RIGHT_BED
				     , A.TAG_RIGHT_LAYER
				     , (SELECT STACK_LAYER_ACTIVE_STAT 
				          FROM TB_YM_STACKLAYER B 
				         WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
				           AND B.STACK_BED_GP   = A.TAG_RIGHT_BED
				           AND B.STACK_LAYER_GP = A.TAG_RIGHT_LAYER) AS  TAG_RIGHT_ACTIVE_STAT 
				     , (SELECT STACK_LAYER_STAT 
				          FROM TB_YM_STACKLAYER B 
				         WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
				           AND B.STACK_BED_GP   = A.TAG_RIGHT_BED
				           AND B.STACK_LAYER_GP = A.TAG_RIGHT_LAYER) AS  TAG_RIGHT_LAYER_STAT
				     , (SELECT STOCK_ID 
				          FROM TB_YM_STACKLAYER B 
				         WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
				           AND B.STACK_BED_GP   = A.TAG_RIGHT_BED
				           AND B.STACK_LAYER_GP = A.TAG_RIGHT_LAYER) AS  TAG_RIGHT_STOCK_ID
				  FROM TO_LOC_TABLE A
				)
				SELECT * 
				  FROM
				(
				SELECT KK.*
				     , ROW_NUMBER() OVER(PARTITION BY PRIOR5 ORDER BY PRIOR1
				                                                    , PRIOR5
				                                                    , PRIOR2 
				                                                    , TAG_STACK_BED_GP
				                                                    ) AS GROUP_ROW       
				  FROM
				        (
				        SELECT K.* 
				             , CASE WHEN TAG_LEFT_STOCK_ID  IS NOT NULL AND TAG_RIGHT_STOCK_ID IS NOT NULL THEN '1'
				                    WHEN TAG_LEFT_STOCK_ID  IS NOT NULL                            THEN '2'
				                    WHEN TAG_RIGHT_STOCK_ID IS NOT NULL                            THEN '2'
				                    ELSE '3' END AS PRIOR5 
				          FROM TO_LOC_DATA_TABLE K
				         WHERE 1  = CASE WHEN TAG_STACK_LAYER_GP = '01'    THEN 1
				                         WHEN TAG_STACK_LAYER_GP = '02'                             -- 2단일 경우 좌우 적치 상태 CHECK
				                              AND TAG_LEFT_ACTIVE_STAT = 'E' AND TAG_RIGHT_ACTIVE_STAT = 'E' 
				                              AND TAG_LEFT_LAYER_STAT  = 'C' AND TAG_RIGHT_LAYER_STAT  = 'C' THEN 1
				                    ELSE 0 END  
				        ) KK       
				)
				, AUTO_CR_TABLE  CR
				 WHERE ((PRIOR5 < 3) OR (PRIOR5 = 3 AND GROUP_ROW < 10))   -- PRIOR5=3은 공BED: 1개만 검색
				   AND ((CR.IS_AUTO = 'Y' AND TAG_STACK_COL_GP IN (SELECT R.ITEM
				                                                     FROM TB_YM_RULE      R
				                                                        , AUTO_CR_TABLE   A
				                                                    WHERE R.REPR_CD_GP = 'CR0001'
				                                                      AND R.CD_GP      = A.EQUIP_GP
				                                                      AND R.DEL_YN     = 'N'
				                                                  ))
				      OR CR.IS_AUTO = 'N'
				      )
				 ORDER BY PRIOR1                                    --위치검색그룹
				        , TAG_STACK_COL_GP DESC
				        , PRIOR2                                        --야드위치 검색범위순서   
				        , CASE WHEN RULL_DAN_PRIOR = PRIOR3 THEN '1'
				               ELSE '2' END                             --단우선순위 
				        , PRIOR5                                        --평점계산시 우선순위
				        , TAG_STACK_BED_GP       
				*/        
 				JDTORecordSet outjsResult = null;
 				if(ydToLocGuide.length() >= 6) {
 					if(ydToLocGuide.length() == 6) {
 						jrTemp.setField("STACK_COL_GP"			, ydToLocGuide.substring(0,6));		//적치열									
 						jrTemp.setField("STACK_BED_GP"			, "%");		//적치BED									
 					} else if(ydToLocGuide.length() >= 8) {
 						jrTemp.setField("STACK_COL_GP"			, ydToLocGuide.substring(0,6));		//적치열									
 						jrTemp.setField("STACK_BED_GP"			, ydToLocGuide.substring(6,8));		//적치BED									
 					}
 					
 					if("Y".equals(sFLAG_YN)) {
 						//권하위치 변경 POPUP 창에서 사용자가  Scrap 열 선택 할 경우 "Y"
 						if(ydBayGp.equals("E")){//E동 스크랩
 							outjsResult =	commDao.select(jrTemp, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getScrapDownLocChangeByUser", logId, methodNm, "TO위치 가이드로 Scrap 열 베드 조회");
 						}else if(ydBayGp.equals("A")){//A동 스크랩
 							outjsResult =	commDao.select(jrTemp, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getScrapDownLocChangeByUserA", logId, methodNm, "TO위치 가이드로 Scrap 열 베드 조회");
 						}
 					} else {
 						
 	 					if(ydBayGp.equals("E")){//E동 스크랩
 	 						outjsResult =	commDao.select(jrTemp, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdPrimaryWorkSearchScrapNewToLocGuide", logId, methodNm, "TO위치 가이드로 ScrapTOSQL 베드 조회");
 	 					}else if(ydBayGp.equals("A")){//A동 스크랩
 	 						outjsResult =	commDao.select(jrTemp, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdPrimaryWorkSearchScrapNewToLocGuideA", logId, methodNm, "TO위치 가이드로 ScrapTOSQL 베드 조회");
 	 					}
 					}
 					
 				} else {
 					if(ydBayGp.equals("E")){//E동 스크랩
 						outjsResult =	commDao.select(jrTemp, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdPrimaryWorkSearchScrapNew", logId, methodNm, "ScrapTOSQL 베드 조회");
 					}else if(ydBayGp.equals("A")){//A동 스크랩
 						outjsResult =	commDao.select(jrTemp, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdPrimaryWorkSearchScrapNewA", logId, methodNm, "ScrapTOSQL 베드 조회");
 					}
 					
 				}
 				
 			    // 적치 가능 check
 				JDTORecord	    jrOutResult	= JDTORecordFactory.getInstance().create();
 				
 				if (outjsResult.size() <= 0) {
 					szLogMsg = methodNm+ "적치가능한 베드 검색 실패 ";
 					commUtils.printLog(logId, szLogMsg, "SL"); 					
 					return jsRtn;		
 				} else {
 					
     				for(int i = 1; i <= outjsResult.size(); i++) {

     					outjsResult.absolute(i);
     					jrOutResult  = outjsResult.getRecord();
     					
     					String szGRIDE = "9";
     					szStackColGp 	= commUtils.trim(jrOutResult.getFieldString("TAG_STACK_COL_GP"  ));
     					szStackBedGp 	= commUtils.trim(jrOutResult.getFieldString("TAG_STACK_BED_GP"  ));
     					szStackLayerGp 	= commUtils.trim(jrOutResult.getFieldString("TAG_STACK_LAYER_GP"  ));	
     					sRtnBedDan 	    = szStackColGp+szStackBedGp+ szStackLayerGp;
     					
     					if (sRtnBedDan.length() == 10) {
     						szLogMsg = methodNm+ szStackColGp+szStackBedGp+ szStackLayerGp+"  적치가능 위치평점:"+ szGRIDE;
     						commUtils.printLog(logId, szLogMsg, "SL");				
     					    //적치가능 
     						JDTORecord 		jrRtn = JDTORecordFactory.getInstance().create();
     						jrRtn.setField("SEARCH"		, "U");
     						jrRtn.setField("GRADE"		, szGRIDE);
     						jrRtn.setField("SEARCH_LOC"	, sRtnBedDan); 
     						jsRtn.addRecord(jrRtn);
     					}
     				}
 					
 				}
 				
 			}				
         

			commUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
        }//end of try~catch			

		return jsRtn;
	} 

	/**
	 * [A] 오퍼레이션명 : 임가공 입고 등록 처리  
	 *
	 * [B] Action위치 : 야드관리 > 임가공 > 입고등록 > 등록 버튼 실행 
	 *		
	 * PIDEV
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException 
	*/
	public JDTORecord updcoilFromToResultjlNEW(GridData inDto) throws DAOException {
		
		String methodNm = "임가공 등록 [BCoilJspSeEJB.updcoilFromToResultjlNEW] < " + inDto.getNavigateValue();
		String logId = inDto.getIPAddress();
		
		//Return Value
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, methodNm, "S+", inDto);
			
			CraneSchDAO	craneSchDao = new CraneSchDAO();			
			ymCommonDAO ymCommonDao = ymCommonDAO.getInstance();
			
			
			// form 값 Setting
			String sModifier  = commUtils.trim(inDto.getParam("userid")); 	// 수정자			
			String sYdGp  	  = commUtils.trim(inDto.getParam("YD_GP")); 	// 야드구분
			String sLocation  = commUtils.trim(inDto.getParam("LOCATION")); 	// 야드구분
			
			//파라미터 사라짐을 막기위해 복사
			GridData gdReq = TsCommUtil.cloneResponseGridData(inDto);
			
			//GRID파리미터를 List로 변환
			List paramList = TsCommUtil.GridDataToList(inDto);			
						
			// 진도코드 업데이트 처리 여부
			Boolean isUpdCurrProdSuc = false;
			
			// 코일소재임가공이송완료실적(YDPTJ003) 전송여부
			Boolean isYDPTJ003Success = false;
			
			// 임가공 하차개시(M10YDLMJ1115) 전송여부
			Boolean isM10YDLMJ1115Success = false;

			// 임가공 하차완료(M10YDLMJ1125) 전송여부
			Boolean isM10YDLMJ1125Success = false;
			
			String queryID = "";
			
			EJBConnector ejbConn = null;
			
			JDTORecord tcRecordDM = JDTORecordFactory.getInstance().create(); 
			
			JDTORecord tcRecordPT = JDTORecordFactory.getInstance().create();
			
			// check
			for (int x = 0; x < paramList.size(); x++) {
				
				JDTORecord rowData = (JDTORecord)paramList.get(x);
				
				commUtils.printLog(logId, "코일 번호 : " + rowData.getFieldString("GOODS_NO"), "SL");
				
				String sGoodsNo = rowData.getFieldString("GOODS_NO");
				
				commUtils.printLog(logId, "==>> 1. 이송완료 처리 시작  =============", "SL");
				
				// 1. 이송완료 처리 : TB_YD_STOCK 테이블의 YD_RCPT_DATE(야드입고일자) 업데이트
				int iUpdFrDone = craneSchDao.updateFrToDoneInfo2(sGoodsNo);				
				
				commUtils.printLog(logId, "==>> 1. 이송완료 처리 죵료  =============", "SL");
				
				// 공통 코일 정보
				queryID = "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.getCOILCOMM";
				List productList = ymCommonDao.getCommonList(queryID, new Object[]{sGoodsNo});
				JDTORecord productInfo = (JDTORecord)productList.get(0);
				
				// 진도코드  값
				String sCurrProgCd = StringHelper.evl(productInfo.getFieldString("CURR_PROG_CD"), "");
				
				// 이미진행 된 경우
				if(sCurrProgCd.equals("C")){ 
				
					commUtils.printLog(logId, "==>>코일 재료공통 변경이 완료 된 경우 skip처리  =============", "SL");
					
				} else {
					
					commUtils.printLog(logId, "==>> 2. 코일 진도코드 수정(E > B) 시작  =============", "SL");
					
					// 2. 코일 진도코드 수정(E > B)
					ejbConn = new EJBConnector("default","JNDIYMCCLdWrkOrdReg",this);
		    	 	isUpdCurrProdSuc = (Boolean)ejbConn.trx("CarinfoFrtoMoveBackupSub2",new  Class[]{String.class},
																new Object[]{sGoodsNo});					
					
				
		    	 	commUtils.printLog(logId, "==>> 2. 코일 진도코드 수정(E > B) 종료 =============", "SL");
		    	 	
		    	 	// 3. 코일소재임가공이송완료실적(YDPTJ003) 전송

		    	 	commUtils.printLog(logId, "==>> 3. 코일소재임가공이송완료실적(YDPTJ003) 시작  =============", "SL");
					
		    	 	tcRecordPT = JDTORecordFactory.getInstance().create();
		            //코일소재임가공이송완료실적(YDPTJ003)
		    	 	tcRecordPT.setField("JMS_TC_CD", "YDPTJ003");
		    	 	tcRecordPT.setField("JMS_TC_CREATE_DDTT", new String(YmCommonUtil.getTcDate("yyyyMMddHHmmss")));
		    	 	tcRecordPT.setField("STL_NO",  StringHelper.evl(productInfo.getFieldString("COIL_NO"), ""));
					// 주문번호
		    	 	tcRecordPT.setField("ORD_NO",  StringHelper.evl(productInfo.getFieldString("ORD_NO"), ""));
					// 주문행번
		    	 	tcRecordPT.setField("ORD_DTL", StringHelper.evl(productInfo.getFieldString("ORD_DTL"), ""));
					// 공장공정코드
		    	 	tcRecordPT.setField("PLNT_PROC_CD",  StringHelper.evl(productInfo.getFieldString("PLNT_PROC_CD"), ""));
					// 재료외형구분
		    	 	tcRecordPT.setField("STL_APPEAR_GP",  StringHelper.evl(productInfo.getFieldString("STL_APPEAR_GP"), ""));
					// 현재진도코드
		    	 	tcRecordPT.setField("CURR_PROG_CD",  StringHelper.evl(productInfo.getFieldString("CURR_PROG_CD"), ""));
					// 주문여재구분
		    	 	tcRecordPT.setField("ORD_YEOJAE_GP", StringHelper.evl(productInfo.getFieldString("ORD_YEOJAE_GP"), ""));
					// 재료중량 (SLAB중량) 
		    	 	tcRecordPT.setField("STL_WT",  StringHelper.evl(productInfo.getFieldString("COIL_WT"), ""));
					// 설계재료중량(항목명?)
		    	 	tcRecordPT.setField("DS_MTL_WT", "");
					// 재료상태구분(항목명?)
		    	 	tcRecordPT.setField("MTL_STAT_GP",  StringHelper.evl(productInfo.getFieldString("RECORD_PROG_STAT"), ""));
					// Record 종료구분
		    	 	tcRecordPT.setField("RECORD_END_GP",  StringHelper.evl(productInfo.getFieldString("RECORD_END_GP"), ""));
					// Record 종료구분 1(항목명?)
		    	 	tcRecordPT.setField("RECORD_END_GP1", "");
					// 전진도 코드
		    	 	tcRecordPT.setField("BEFO_PROG_CD",  StringHelper.evl(productInfo.getFieldString("BEFO_PROG_CD"), ""));
					// 전주문 번호
		    	 	tcRecordPT.setField("BEF_ORD_NO",  StringHelper.evl(productInfo.getFieldString("BEF_ORD_NO"), ""));
					// 전주문 행번
		    	 	tcRecordPT.setField("BEF_ORD_DTL",  StringHelper.evl(productInfo.getFieldString("BEF_ORD_DTL"), ""));
					// 모재료번호   
		    	 	tcRecordPT.setField("MMATL_FEE_NO",  StringHelper.evl(productInfo.getFieldString("MMATL_FEE_NO"), ""));
					// 목전충당구분
		    	 	tcRecordPT.setField("ORDERTRANS_MATCH_GP", StringHelper.evl(productInfo.getFieldString("MATCH_ORDERTRANS_GP"), ""));	
					
					ejbConn = new EJBConnector("default","JNDIYMYardWrkResReg",this);
					isYDPTJ003Success = (Boolean)ejbConn.trx("sendInternalModel",new Class[]{JDTORecord.class}, new Object[]{tcRecordPT});
					
					commUtils.printLog(logId, "==>> 3. 코일소재임가공이송완료실적(YDPTJ003) 종료  =============", "SL");
		    	 	
					// 4. 이송지시 전체 대상 갯수
					commUtils.printLog(logId, "==>> 4. 이송지시 전체 대상 갯수 가져오기 시작  =============", "SL");
					
					List allDmFnsList = craneSchDao.getYdDmCommonInfoPI(sGoodsNo);
					
					int iAllDmFns = allDmFnsList.size();
					
					commUtils.printLog(logId, "==>> 4. 이송지시 전체 대상 갯수 가져오기 종료  =============", "SL");
					
					// 5. 이송지시 완료된 대상 갯수
					commUtils.printLog(logId, "==>> 5. 이송지시 완료된 대상 갯수 가져오기 시작  =============", "SL");
					
					List sucDmFnsList = craneSchDao.getYmDmFrToInfo2PI(sGoodsNo);
					
					int iSucDmFns = sucDmFnsList.size();
					
					commUtils.printLog(logId, "==>> 5. 이송지시 완료된 대상 갯수 가져오기 종료  =============", "SL");
					
					// 이송지시 전체 대상에 대한 완료가 되었을 경우
					if(iAllDmFns > 0 && iAllDmFns == iSucDmFns){ 
						
						JDTORecord dmInfo   = (JDTORecord)allDmFnsList.get(0);
						
				    	String sTransWordNo   = StringHelper.evl(dmInfo.getFieldString("TRANS_WORD_NO"), "");
				    	String sCarNo   	  = StringHelper.evl(dmInfo.getFieldString("CAR_NO"), "");
				    	
						// 6. 임가공 이송 하차개시 전송
						commUtils.printLog(logId, "==>> 6. 임가공 이송 하차개시 시작  =============", "SL");				    	
				    	
		                //임가공이송하차개시
						tcRecordDM = JDTORecordFactory.getInstance().create(); 
		     			tcRecordDM.setField("UPCARUNLOAD_GP", "D"); 
		                tcRecordDM.setField("CAR_NO", sCarNo);
		                tcRecordDM.setField("YD_GP", sYdGp);		
		                tcRecordDM.setField("TRANS_WORD_DATE",sTransWordNo.length() > 8 ? sTransWordNo.substring(0,8) : sTransWordNo);
		                tcRecordDM.setField("TRANS_WORD_SEQNO", sTransWordNo.length() > 8 ? sTransWordNo.substring(8) : "");
		     			
		                commUtils.printLog(logId, "==>> 6-1. 임가공 이송 하차개시 내부IF호출 시작 ================", "SL");
		                
		     			ejbConn = new EJBConnector("default","JNDIYMYardWrkResReg",this);
		     			isM10YDLMJ1115Success = (Boolean)ejbConn.trx("getM10YDLMJ1115",new Class[]{JDTORecord.class}, new Object[]{tcRecordDM});
		     			
		     			commUtils.printLog(logId, "==>> 6-1. 임가공 이송 하차개시 내부IF호출 종료 ================", "SL");
		     			
		     			commUtils.printLog(logId, "==>> 6. 하차개시 시작 종료  =============", "SL");
		     					
		     			// 7. 임가공 이송 하차완료 전송
						commUtils.printLog(logId, "==>> 7. 임가공 이송 하차완료 시작  =============", "SL");				    	
				    	
		                //임가공이송하차완료
						tcRecordDM = JDTORecordFactory.getInstance().create(); 
		     			tcRecordDM.setField("UPCARUNLOAD_GP", "D");
		                tcRecordDM.setField("CAR_NO", sCarNo);
		                tcRecordDM.setField("YD_GP", sYdGp);		
		                tcRecordDM.setField("TRANS_WORD_DATE",sTransWordNo.length() > 8 ? sTransWordNo.substring(0,8) : sTransWordNo);
		                tcRecordDM.setField("TRANS_WORD_SEQNO",  sTransWordNo.length() > 8 ? sTransWordNo.substring(8) : "");
		                
		                commUtils.printLog(logId, "==>> 7-1. 임가공 이송 하차완료 내부IF호출 시작 ================", "SL");
		                
		                ejbConn = new EJBConnector("default","JNDIYMYardWrkResReg",this);
		                isM10YDLMJ1125Success = (Boolean)ejbConn.trx("getM10YDLMJ1125",new Class[]{JDTORecord.class}, new Object[]{tcRecordDM});
		     			
		     			commUtils.printLog(logId, "==>> 7-1. 임가공 이송 하차완료 내부IF호출 종료 ================", "SL");
		     			
		     			commUtils.printLog(logId, "==>> 7. 하차완료 시작 종료  =============", "SL");						
		     			
					}
					
				}
								
				// 임가공 산적위치 수정
				commUtils.printLog(logId, "==>> 8. 임가공  산적위치  =============", "SL");
				
				commUtils.printLog(logId, "==>> 8-1. 임가공  저장위치 지정 시작  =============", "SL");
				
				// 현재위치는 무조건 10자리이다.
				String sAddr = "";
				
				// YD_GP가 7일 경우
				if("7".equals(sYdGp) ) {
										
					// YD_GP가 7일 경우 sLocation 길이는 무조건 3자리로 들어온다.
					sAddr = sYdGp + sLocation + "010101";
					
				} else if("5".equals(sYdGp) ) {
				
					// YD_GP가 5일 경우 sLocation 길이는 무조건 4자리로 들어온다.
					//sAddr = sLocation + "010101";
					sAddr = sYdGp + "A01010101"; //2025.07.02 수정 삼우스틸 저장위치 지정 기능 제외
					
				} else {

					//임가공사 저장위치 검색
					ejbConn= new EJBConnector("default","JNDIYMCraneSchReg",this);
					sAddr = (String)ejbConn.trx("getEmptyLoc",new Class[]{String.class}, new Object[]{sYdGp});
			
					// 저장위치가 존재하지 않는 경우
					if ( sAddr == null || "".equals(sAddr)) 
					{
						sAddr = sYdGp + "A01010101";
					}					
					
				}		
					
				// 저장위치 길이가 10자리가 아닐 경우.
				if (sAddr.length() != 10) {
					
//					substr(:pos,1,1),-- 야드구분
//			        substr(:pos,2,1),-- 동
//			        substr(:pos,3,2),-- SPAN
//			        substr(:pos,5,2),-- 적치열번지
//			        substr(:pos,7,2),-- 적치번지
//			        substr(:pos,9,2),-- 적치단
					sAddr =	 sYdGp + "A01010101";
				}
				
				commUtils.printLog(logId, "==>> 8-1. 임가공  저장위치 지정 종료 =============", "SL");
				
				commUtils.printLog(logId, "==>> 8-2. 임가공  저장위치 수정 처리 시작  =============", "SL");

//				String sPutStackColGp   = sAddr.substring(0, 6);		// 적치열
//				String sPutStackBedGp   = sAddr.substring(6, 8);		// 적치대(Bed)
//				String sPutStackLayerGp = sAddr.substring(8, 10);		// 적치단				
//				
//				// 적치단 Put위치를 적치상태로 변경 tb_ym_stacklayer Table : stock_id = Coil
//				// No tb_ym_stacklayer Table : stack_layer_stat = 'L'(적치중)
//				int iUpdCnt = 0;
//				String sLayerStat = YmCommonConst.STACK_LAYER_STAT_L;
//				iUpdCnt = craneSchDao.updateCraneStackLayerStat(sPutStackColGp, sPutStackBedGp, sPutStackLayerGp, sGoodsNo, sLayerStat);				
				
				commUtils.printLog(logId, "==>> 산적위치 수정=> 저장위치  UPDATE = =============", "SL");
				
				int iUpdCnt = 0;
				// Coil 공통 Table 저장위치 Update
				iUpdCnt = craneSchDao.updateCoilCommonLocInfo(sGoodsNo, sAddr);
				commUtils.printLog(logId, "==>> 산적위치 수정=> 코일공통  UPDATE = =============" + iUpdCnt, "SL");
				
				commUtils.printLog(logId, "==>> 8-2. 임가공  저장위치 수정 처리 종료  =============", "SL");
				
				
				// 9. 크레인 작업실적 등록
				
				commUtils.printLog(logId, "==>> 9. 크레인 작업실적 등록 시작  =============", "SL");
				
				String sPutYardGp = sAddr.substring(0, 1);
				
				EJBConnector ejbConn1 = new EJBConnector("default","JNDIYMCraneSchReg",this);
				ejbConn1.trx("insertUpPutWrslRtData",new Class[]{String.class, 
															   String.class, 
															   String.class,
															   String.class,
															   String.class,
															   String.class},
														   new Object[]{sGoodsNo, 
														    			"", 
														    			sAddr.trim(),
														    			"",
														    			sPutYardGp,
														    			sModifier});				
				
				
				commUtils.printLog(logId, "==>> 9. 크레인 작업실적 등록 종료  =============", "SL");
				
			} // for문 종료
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	/**
	 * [A] 오퍼레이션명    : 임가공 산적위치 수정 처리
	 *		
	 * [B] Action 위치 : 야드관리 -> 임가공 -> 적치위치수정 -> 수정버튼 실행
	 *		
	 * PIDEV
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord headLocationModify(GridData inDto) throws DAOException {
		
		String methodNm = "임가공 산적위치 수정[BCoilJspSeEJB.headLocationModify] < " + inDto.getNavigateValue();
		String logId = inDto.getIPAddress();
		
		//Return Value
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+", inDto);
			
			CraneSchDAO	craneSchDao = new CraneSchDAO();			
			ymCommonDAO ymCommonDao = ymCommonDAO.getInstance();			
			
			// form 값 Setting
			String sModifier  		= commUtils.trim(inDto.getParam("userid")); 		// 수정자			
			String sYdGp  	  		= commUtils.trim(inDto.getParam("YD_GP")); 		// 야드구분
			String sStockId   		= commUtils.trim(inDto.getParam("STOCK_ID")); 	// 재료번호
			
			String sYD_STACK_COL_GP	= commUtils.trim(inDto.getParam("t_addr1")); 	// to 적치열
			String sYD_BED_GP   	= commUtils.trim(inDto.getParam("t_addr2")); 	// to 적치대(Bed)
			String sYD_LYR_GP   	= commUtils.trim(inDto.getParam("t_addr3")); 	// to 적치단
			
			// 야드구분이 7일 경우.
			if(sYdGp.equals("7")) {
				sYD_STACK_COL_GP = sYdGp + sYD_STACK_COL_GP;
			}
			
			String sToLocation = sYD_STACK_COL_GP + sYD_BED_GP + sYD_LYR_GP;
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();			
			/*
			SELECT STOCK_ID
			     , STACK_LAYER_STAT
			     , STACK_LAYER_ACTIVE_STAT
			     , STACK_LAYER_COMMENTS
			  FROM TB_YM_STACKLAYER
			 WHERE STACK_COL_GP  	= :V_STACK_COL_GP
			   AND STACK_BED_GP		= :V_STACK_BED_GP 
			   AND STACK_LAYER_GP	= :V_STACK_LAYER_GP 
			 */
			jrParam.setField("STACK_COL_GP"   	,sYD_STACK_COL_GP);
			jrParam.setField("STACK_BED_GP"    	,sYD_BED_GP);
			jrParam.setField("STACK_LAYER_GP"  	,sYD_LYR_GP);
			JDTORecordSet jsLyrInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getStackLayerInfo", logId, methodNm, "신규저장위치 조회");
			
			// to 위치 적치단이 없을 경우.
			if (jsLyrInfo.size() == 0) {
				
				throw new Exception("산적위치 이상 [" + sYD_STACK_COL_GP + sYD_BED_GP + sYD_LYR_GP + "]");
				
			} else {
				
				// to 위치 적치단이 존재할 경우.				
				String sLyrStrockId = StringHelper.evl(jsLyrInfo.getRecord(0).getFieldString("STOCK_ID") , "");
				int iUpdCnt = 0;
				
				// 적치단에 재료번호가 존재할 경우.
				if(!sLyrStrockId.equals("")) {
					
					// 공통 코일 테이블에 재료번호가 해당하는 현재 위치를 NULL로 수정
					commUtils.printLog(logId, "==>> 0. TO위치에 적치된 재료번호를 공통코일테이블에서 NULL로 수정 시작  =============", "SL");
					
					iUpdCnt = craneSchDao.updateCoilCommonLocInfo(sStockId, "");
					
					commUtils.printLog(logId, "==>> 0. TO위치에 적치된 재료번호를 공통코일테이블에서 NULL로 수정 종료 =============", "SL");
					
				}
				
				// TB_YM_STACKLAYER(적치단) 테이블 적치 위치 수정
				commUtils.printLog(logId, "==>> 1. 적치단에 TO 위치 수정 시작  =============", "SL");
				
				String sLayerStat = YmCommonConst.STACK_LAYER_STAT_L;
				iUpdCnt = craneSchDao.updateCraneStackLayerStat(sYD_STACK_COL_GP, sYD_BED_GP, sYD_LYR_GP, sStockId, sLayerStat);				
				
				commUtils.printLog(logId, "==>> 1. 적치단에 TO 위치 수정 종료  =============", "SL");
				
				commUtils.printLog(logId, "==>> 2. 공통테이블 대상 코일  TO 적치 위치 수정 시작  =============", "SL");
				
				iUpdCnt = craneSchDao.updateCoilCommonLocInfo(sStockId, sToLocation);
				
				commUtils.printLog(logId, "==>> 2. 공통테이블 대상 코일  TO 적치 위치 수정 종료  =============", "SL");
				
				// 3. 크레인 작업실적 등록				
				commUtils.printLog(logId, "==>> 3. 크레인 작업실적 등록 시작  =============", "SL");
				
				String sPutYardGp = sYD_STACK_COL_GP.substring(0, 1);
				
				EJBConnector ejbConn1 = new EJBConnector("default","JNDIYMCraneSchReg",this);
				ejbConn1.trx("insertUpPutWrslRtData",new Class[]{String.class, 
															   String.class, 
															   String.class,
															   String.class,
															   String.class,
															   String.class},
														   new Object[]{sStockId, 
														    			"", 
														    			sToLocation,
														    			"",
														    			sPutYardGp,
														    			sModifier});				
				
				
				commUtils.printLog(logId, "==>> 3. 크레인 작업실적 등록 종료  =============", "SL");				
				
			}
			
			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 * [A] 오퍼레이션명    : 임가공 산적위치 삭제처리
	 *		
	 * [B] Action 위치 : 야드관리 -> 임가공 -> 적치위치수정 -> 삭제버튼 실행
	 *		
	 * PIDEV
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord headLocationDelete(GridData inDto) throws DAOException {
		
		String methodNm = "임가공 산적위치 수정[BCoilJspSeEJB.headLocationDelete] < " + inDto.getNavigateValue();
		String logId = inDto.getIPAddress();
		
		//Return Value
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+", inDto);
			
			CraneSchDAO	craneSchDao = new CraneSchDAO();			
			ymCommonDAO ymCommonDao = ymCommonDAO.getInstance();			
			
			// form 값 Setting
			String sStockId   = commUtils.trim(inDto.getParam("STOCK_ID")); 	// 재료번호
			
			String sFromLocation = commUtils.trim(inDto.getParam("f_addr")); 	// to 적치단
			
			String sYD_STACK_COL_GP   = sFromLocation.substring(0, 6);		// 적치열
			String sYD_BED_GP   = sFromLocation.substring(6, 8);		// 적치대(Bed)
			String sYD_LYR_GP = sFromLocation.substring(8, 10);		// 적치단						
			
			int iUpdCnt = 0;
				
			commUtils.printLog(logId, "==>> 1. 적치단 수정 시작  =============", "SL");
			
			String sLayerStat = YmCommonConst.STACK_LAYER_STAT_E;
			iUpdCnt = craneSchDao.updateCraneStackLayerStat(sYD_STACK_COL_GP, sYD_BED_GP, sYD_LYR_GP, "", sLayerStat);				
			
			commUtils.printLog(logId, "==>> 1. 적치단 수정 종료  =============", "SL");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
}

