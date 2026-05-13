/**
 * @(#)GdsYsJspSeEJBBean
 *
 * @version          V1.00
 * @author           허철호
 * @date             2014/12/22
 *
 * @description      제품(봉강,선재) 야드 화면 관리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2014/12/22   윤재광      조병기      최초 등록
 */
package com.inisteel.cim.ys.gds.session;

import java.util.Vector;
import java.util.List;
import java.util.ArrayList;

import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.CmUtil;
import com.inisteel.cim.ym.bcommon.util.YmCommUtils;
import com.inisteel.cim.ys.common.dao.YsCommDAO;
import com.inisteel.cim.ys.common.session.YsComm;
import com.inisteel.cim.ys.common.util.YsCommUtils;
import com.inisteel.cim.ys.common.util.YsConstant;
import com.inisteel.cim.ys.gds.dao.GdsYsDAO;
import com.inisteel.cim.ys.gds.session.GdsYsComm;
import com.inisteel.cim.ys.message.MessageSenderTalk;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;
/**
 *      [A] 클래스명 : 제품(봉강,선재) 야드 화면관리 Session EJB 
 *
 * @ejb.bean name="GdsYsJspSeEJB" jndi-name="GdsYsJspSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300" 
 * @ejb.transaction type="Required" 
*/
public class GdsYsJspSeEJBBean extends BaseSessionBean { 
 
	private static final long serialVersionUID = 1L;
	private YsCommUtils commUtils = new YsCommUtils();
	private YsCommDAO commDao = new YsCommDAO();
	private GdsYsComm gdsYsComm = new GdsYsComm();
	private GdsYsDAO GdsYsDao = new GdsYsDAO();	
	private YsComm YsComm = new YsComm(); 
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}
	
	/**
	/**
	 * GridData - 단순 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public GridData getSelectData(GridData gdReq) throws DAOException {
		String methodNm = "조회[GdsYsJspSeEJB.getSelectData] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		try {
			
			
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//Grid date 를 JDTORecord data 로 변환
			JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
			
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			
			//PIDEV
//			String sApplyYnPI_J = ydCommDAO.ApplyYnPI("", methodNm, "APPPI0", "J", "*");
//			String sApplyYnPI_T = ydCommDAO.ApplyYnPI("", methodNm, "APPPI0", "T", "*");
//			String sApplyYnPI_S = ydCommDAO.ApplyYnPI("", methodNm, "APPPI0", "S", "*");
//			String sApplyYnPI_1 = yfCommDAO.ApplyYnPI("", methodNm, "APPPI0", "1", "*");
//			String sApplyYnPI_3 = ymCommDAO.ApplyYnPI("", methodNm, "APPPI0", "3", "*");
//			String sApplyYnPI_K =   commDao.ApplyYnPI("", methodNm, "APPPI0", "K", "*");
//			
//			String sQueryId = inRecord.getFieldString("QUERY_ID"); 
//			
//			// 모바일 쿼리 PI 적용
//			// 후판야드 PI 적용여부
//			if("Y".equals(sApplyYnPI_T)) {
//				
//				if(
//					   "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0035".equals(sQueryId)
//					|| "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0092".equals(sQueryId)
//					|| "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0103".equals(sQueryId)
//					|| "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getPlateYdProdDtlInfo".equals(sQueryId)
//				  ) {
//					inRecord.setField("QUERY_ID", sQueryId + "_PIDEV");
//				}
//				
//			}
//
//			// 통합슬라브야드 PI 적용여부
//			if("Y".equals(sApplyYnPI_S)) {
//				
//				if(
//					   "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getSlabStackList".equals(sQueryId)
//					|| "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.YdStkLyrMtlCTransDTSeqCardNoDesc3".equals(sQueryId)
//					|| "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getYdWrkHistDaoEventSearchTotBayGp2".equals(sQueryId)
//				  ) {
//					inRecord.setField("QUERY_ID", sQueryId + "_PIDEV");
//				}
//				
//			}			
//			
//			// PI 고객사 그룹 쿼리 ID일 경우.
//			if("com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getDemenderNoGroup".equals(sQueryId)) {
//				
//				String YD_GP = inRecord.getFieldString("YD_GP"); 
//				
//				String sApplyYnPI_CO = "N";
//				
//				// 2열연
//				if("J".equals(YD_GP)) {
//					sApplyYnPI_CO = sApplyYnPI_J;
//				}
//
//				// 후판
//				if("T".equals(YD_GP)) {
//					sApplyYnPI_CO = sApplyYnPI_T;
//				}
//				
//				// 슬라브
//				if("S".equals(YD_GP)) {
//					sApplyYnPI_CO = sApplyYnPI_S;
//				}
//				
//				// 1열연 코일
//				if("3".equals(YD_GP)) {
//					sApplyYnPI_CO = sApplyYnPI_3;
//				}
//
//				// 박판
//				if("1".equals(YD_GP)) {
//					sApplyYnPI_CO = sApplyYnPI_1;
//				}
//				
//				// 특수강
//				if("S".equals(YD_GP)) {
//					sApplyYnPI_CO = sApplyYnPI_K;
//				}								
//				
//				if("Y".equals(sApplyYnPI_CO)) {
//					inRecord.setField("QUERY_ID", sQueryId + "_PIDEV");
//				}
//				
//			}
			
			commDao.jspSelect(inRecord, outRecSet, inRecord.getFieldString("QUERY_ID"), logId, methodNm);	
			
			//UI로 반환 할 Grid data 를 생성 
			//GridData gdRet = CmUtil.genGridData(gdReq, outRecSet); --old 버젼		
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
		String methodNm = "조회[GdsYsJspSeEJB.getSelectData] < " + recPara.getResultMsg();
		String logId = recPara.getResultCode();
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+", recPara);
			
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			
			//PIDEV
//			String sApplyYnPI_J = ydCommDAO.ApplyYnPI("", methodNm, "APPPI0", "J", "*");
//			String sApplyYnPI_T = ydCommDAO.ApplyYnPI("", methodNm, "APPPI0", "T", "*");
//			String sApplyYnPI_S = ydCommDAO.ApplyYnPI("", methodNm, "APPPI0", "S", "*");
//			String sApplyYnPI_1 = yfCommDAO.ApplyYnPI("", methodNm, "APPPI0", "1", "*");
//			String sApplyYnPI_3 = ymCommDAO.ApplyYnPI("", methodNm, "APPPI0", "3", "*");
//			String sApplyYnPI_K =   commDao.ApplyYnPI("", methodNm, "APPPI0", "K", "*");
//			
//			String sQueryId = recPara.getFieldString("QUERY_ID"); 
//			
//			// 모바일 쿼리 PI 적용
//			// 후판야드 PI 적용여부
//			if("Y".equals(sApplyYnPI_T)) {
//				
//				if(
//					   "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0035".equals(sQueryId)
//					|| "com.inisteel.cim.yd.common.dao.YdPlateCommDao.selectQueryId_0092".equals(sQueryId)
//					|| "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0103".equals(sQueryId)
//					|| "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getPlateYdProdDtlInfo".equals(sQueryId)
//				  ) {
//					recPara.setField("QUERY_ID", sQueryId + "_PIDEV");
//				}
//				
//			}
//
//			// 통합슬라브야드 PI 적용여부
//			if("Y".equals(sApplyYnPI_S)) {
//				
//				if(
//					   "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getSlabStackList".equals(sQueryId)
//					|| "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.YdStkLyrMtlCTransDTSeqCardNoDesc3".equals(sQueryId)
//					|| "com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getYdWrkHistDaoEventSearchTotBayGp2".equals(sQueryId)
//				  ) {
//					recPara.setField("QUERY_ID", sQueryId + "_PIDEV");
//				}
//				
//			}			
//			
//			// PI 고객사 그룹 쿼리 ID일 경우.
//			if("com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getDemenderNoGroup".equals(sQueryId)) {
//				
//				String YD_GP = recPara.getFieldString("YD_GP"); 
//				
//				String sApplyYnPI_CO = "N";
//				
//				// 2열연
//				if("J".equals(YD_GP)) {
//					sApplyYnPI_CO = sApplyYnPI_J;
//				}
//
//				// 후판
//				if("T".equals(YD_GP)) {
//					sApplyYnPI_CO = sApplyYnPI_T;
//				}
//				
//				// 슬라브
//				if("S".equals(YD_GP)) {
//					sApplyYnPI_CO = sApplyYnPI_S;
//				}
//				
//				// 1열연 코일
//				if("3".equals(YD_GP)) {
//					sApplyYnPI_CO = sApplyYnPI_3;
//				}
//
//				// 박판
//				if("1".equals(YD_GP)) {
//					sApplyYnPI_CO = sApplyYnPI_1;
//				}
//				
//				// 특수강
//				if("S".equals(YD_GP)) {
//					sApplyYnPI_CO = sApplyYnPI_K;
//				}								
//				
//				if("Y".equals(sApplyYnPI_CO)) {
//					recPara.setField("QUERY_ID", sQueryId + "_PIDEV");
//				}
//				
//			}
			
			
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
	 * 제품입고예정정보-입고예정동 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updgdsWhsPlnInfojl(GridData gdReq) throws DAOException {
		String methodNm = "제품입고예정정보-입고예정동 수정[GdsYsJspSeEJB.updgdsWhsPlnInfojl] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			//Return Value
			JDTORecord jrRtn = null;
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			String sGDS_GP = commUtils.trim(gdReq.getParam("GDS_GP")); // 제품구분 : A:봉강,B:선재
			
			
			
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				//STOCK 수정 
				jrParam.setField("YD_RCPT_STR_LOC"		, commUtils.getValue(gdReq, "YD_RCPT_PLN_BAY_GP", ii) ); 
				jrParam.setField("YD_RCPT_STR_LOC_RSN"	, commUtils.getValue(gdReq, "YD_RCPT_STR_LOC_RSN", ii) ); 
				jrParam.setField("SSTL_NO"				, commUtils.getValue(gdReq, "SSTL_NO", ii) ); 
				
				commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updgdsWhsPlnInfojl", logId, methodNm, "TB_YS_STOCK");
				
				//선재인 경우  예정동을 C-HOOT에 전송 
				if(sGDS_GP.equals("B")) {
					
//					jrParam.setField("YD_INFO_SYNC_CD", "4"          ); //야드정보동기화코드(Bed)
//					jrParam.setField("YS_STK_COL_GP"  , commUtils.trim(gdReq.getHeader("YS_STK_COL_GP").getValue(ii)) ); //야드적치열구분
//					jrParam.setField("YS_STK_BED_NO"  , commUtils.trim(gdReq.getHeader("YS_STK_BED_NO").getValue(ii)) ); //야드적치Bed번호
					
					jrParam.setField("SSTL_NO"        , commUtils.getValue(gdReq, "SSTL_NO", ii)); //특수강재료번호
					jrParam.setField("YD_BAY_TO"      , commUtils.getValue(gdReq, "YD_RCPT_PLN_BAY_GP", ii)  ); //목적 동
	
					//전송Data 조회 후 라우팅 지시 송신
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN7L101", jrParam));
				}	
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updgdsWhsPlnInfojl
	
	/**
	 * 제품입고예정정보-입고예정동 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updgdsWhsPlnInfojl_01(GridData gdReq) throws DAOException {
		String methodNm = "제품입고예정정보-입고예정동 수정[GdsYsJspSeEJB.updgdsWhsPlnInfojl_01] < " + gdReq.getNavigateValue();
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
				
				//STOCK 수정 
				jrParam.setField("YD_RCPT_STR_LOC"		, commUtils.getValue(gdReq, "YD_RCPT_PLN_STR_LOC", ii).substring(1) ); 
				jrParam.setField("YD_RCPT_STR_LOC_RSN"	, "X" ); 
				jrParam.setField("SSTL_NO"				, commUtils.getValue(gdReq, "SSTL_NO", ii) ); 
				
				commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updgdsWhsPlnInfojl", logId, methodNm, "TB_YS_STOCK");
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updgdsWhsPlnInfojl_01

	/**
	 * IFTest Layout 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updIfTestData(GridData gdReq) throws DAOException {
		String methodNm = "IFTest Layout 변경[GdsYsJspSeEJB.updIfTestData] < " + gdReq.getNavigateValue();
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
		String methodNm = "IFTest 전송[GdsYsJspSeEJB.sndIfTest] < " + gdReq.getNavigateValue();
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

			sndData.setField("JMS_TC_CD"         , msgId                    );
			sndData.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14());
			
			if ("L".equals(ifMthGp) && "S".equals(ifSndRcvGp)) {
				//EAI송신처리 일 경우
				ifMthNm = "sndToEAI";

				//EAI전문 Message
				// 2025.09.18 sbMsg 처리 부분 변경				
//								StringBuffer sbMsg = new StringBuffer();
				//
//								for (int ii = 0; ii < rowCnt; ii++) {
//									sbMsg = sbMsg.append(gdReq.getHeader("ITM_VAL").getValue(ii));
//								}

				/*****추가*****/
				String sbMsg = gdReq.getParam("TC_LIST");
								
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
		String methodNm = "IFTest EAI전송[GdsYsJspSeEJB.sndIfTestEAI] < " + gdReq.getNavigateValue();
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
	 *      [A] 오퍼레이션명 :제품 재료상세정보 조회-출하위치송신
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updgdsMtlDtlInfoInqjl(GridData gdReq) throws DAOException {
		String methodNm = "제품 재료상세정보 조회-출하위치송신[GdsYsJspSeEJB.updgdsMtlDtlInfoInqjl] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//EJB Call을 위한 Message 생성 - Log ID, Method, 수정자 Set
			JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name
			
			jrYdMsg.setField("SSTL_NO"       , commUtils.trim(gdReq.getParam("SSTL_NO"))); 

			//PIDEV
//			String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "K", "*");
			
			//제품이적 실적 
//			if("Y".equals(sApplyYnPI)) {				
				jrRtn = commUtils.addSndData(commDao.getMsgL3("M10YDLMJ1034", jrYdMsg));				
//			} else {
//				jrRtn = commUtils.addSndData(commDao.getMsgL3("YSDSJ002", jrYdMsg));				
//			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	/**
	 *  봉강 자동창고 저장계획 -저장계획수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updgdsRbAutoWhuStrPlnjm(GridData gdReq) throws DAOException {
		String methodNm = "봉강 자동창고 저장계획 -저장계획수정[GdsYsJspSeEJB.updgdsRbAutoWhuStrPlnjm] < " + gdReq.getNavigateValue();
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
				
				jrParam.setField("YD_BAY_GP"		, commUtils.trim(gdReq.getParam("V_YD_BAY_GP")) ); 
				jrParam.setField("CUST_CD"			, commUtils.getValue(gdReq, "CUST_CD", ii) ); 
				jrParam.setField("DETAIL_ARR_CD"	, commUtils.getValue(gdReq, "DETAIL_ARR_CD", ii) ); 
				jrParam.setField("YD_STK_COL_NO1"	, commUtils.nvl(commUtils.getValue(gdReq, "YD_STK_COL_NO1", ii) ,"N")); 
				jrParam.setField("YD_STK_COL_NO2"	, commUtils.nvl(commUtils.getValue(gdReq, "YD_STK_COL_NO2", ii) ,"N")); 
				jrParam.setField("YD_STK_COL_NO3"	, commUtils.nvl(commUtils.getValue(gdReq, "YD_STK_COL_NO3", ii) ,"N")); 
				jrParam.setField("YD_STK_COL_NO4"	, commUtils.nvl(commUtils.getValue(gdReq, "YD_STK_COL_NO4", ii) ,"N")); 
				jrParam.setField("YD_STK_COL_NO5"	, commUtils.nvl(commUtils.getValue(gdReq, "YD_STK_COL_NO5", ii) ,"N")); 
				jrParam.setField("YD_STK_COL_NO6"	, commUtils.nvl(commUtils.getValue(gdReq, "YD_STK_COL_NO6", ii) ,"N")); 
				jrParam.setField("YD_STK_COL_NO7"	, commUtils.nvl(commUtils.getValue(gdReq, "YD_STK_COL_NO7", ii) ,"N")); 
				jrParam.setField("YS_STRCHAR_ID"	, commUtils.getValue(gdReq, "YS_STRCHAR_ID", ii) ); 
				
				commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updgdsRbAutoWhuStrPlnjm", logId, methodNm, "TB_YS_AUTOWHSTRCHAR");
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updgdsRbAutoWhuStrPlnjm
	
	/**
	 *  봉강 자동창고 저장계획 -저장계획삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord delgdsRbAutoWhuStrPlnjm(GridData gdReq) throws DAOException {
		String methodNm = "봉강 자동창고 저장계획 -저장계획삭제[GdsYsJspSeEJB.delgdsRbAutoWhuStrPlnjm] < " + gdReq.getNavigateValue();
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
				
				//STOCK 수정 
				jrParam.setField("YS_STRCHAR_ID"	, commUtils.getValue(gdReq, "YS_STRCHAR_ID", ii) ); 
				commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.delgdsRbAutoWhuStrPlnjm", logId, methodNm, "TB_YS_AUTOWHSTRCHAR");
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of delgdsRbAutoWhuStrPlnjm
	
	/**
	 *  봉강 자동창고 저장계획 -저장계획등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord insgdsRbAutoWhuStrPlnjm(GridData gdReq) throws DAOException {
		String methodNm = "봉강 자동창고 저장계획 -저장계획등록[GdsYsJspSeEJB.insgdsRbAutoWhuStrPlnjm] < " + gdReq.getNavigateValue();
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
				
				//STOCK 수정 
				jrParam.setField("YD_BAY_GP"		, commUtils.trim(gdReq.getParam("V_YD_BAY_GP")) ); 
				jrParam.setField("CUST_CD"			, commUtils.getValue(gdReq, "CUST_CD", ii)); 
				jrParam.setField("DETAIL_ARR_CD"	, commUtils.getValue(gdReq, "DETAIL_ARR_CD", ii)); 
				jrParam.setField("YD_STK_COL_NO1"	, commUtils.nvl(commUtils.getValue(gdReq, "YD_STK_COL_NO1", ii) ,"N")); 
				jrParam.setField("YD_STK_COL_NO2"	, commUtils.nvl(commUtils.getValue(gdReq, "YD_STK_COL_NO2", ii) ,"N")); 
				jrParam.setField("YD_STK_COL_NO3"	, commUtils.nvl(commUtils.getValue(gdReq, "YD_STK_COL_NO3", ii) ,"N")); 
				jrParam.setField("YD_STK_COL_NO4"	, commUtils.nvl(commUtils.getValue(gdReq, "YD_STK_COL_NO4", ii) ,"N")); 
				jrParam.setField("YD_STK_COL_NO5"	, commUtils.nvl(commUtils.getValue(gdReq, "YD_STK_COL_NO5", ii) ,"N")); 
				jrParam.setField("YD_STK_COL_NO6"	, commUtils.nvl(commUtils.getValue(gdReq, "YD_STK_COL_NO6", ii) ,"N")); 
				jrParam.setField("YD_STK_COL_NO7"	, commUtils.nvl(commUtils.getValue(gdReq, "YD_STK_COL_NO7", ii) ,"N")); 
				
				commDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insgdsRbAutoWhuStrPlnjm", logId, methodNm, "TB_YS_AUTOWHSTRCHAR");
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of insgdsRbAutoWhuStrPlnjm
		
	/**
	 * 봉강제품 기준관리 - 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updYsRule(GridData gdReq) throws DAOException {
		String methodNm = "봉강제품 기준관리 - 수정[GdsYsJspSeEJB.updYsRule] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;
			String  sCD_GP  = "";
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				if(!commUtils.getValue(gdReq, "REPR_CD_GP", ii).equals("K00015")) { //K00015은 별도 처리.
					//기준 수정 
					jrParam.setField("ITEM"			, commUtils.getValue(gdReq, "ITEM", ii)); 
					jrParam.setField("REPR_CD_GP"	, commUtils.getValue(gdReq, "REPR_CD_GP", ii) ); 
					jrParam.setField("CD_GP"		, commUtils.getValue(gdReq, "CD_GP", ii) ); 
					
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYsRule", logId, methodNm, "봉강제품 기준관리 수정");
				}
				
				if(commUtils.getValue(gdReq, "REPR_CD_GP", ii).equals("K00011")) {

					if(commUtils.getValue(gdReq, "ITEM", ii).equals("Y")) {
						sCD_GP = "M";
					} else {
						sCD_GP = "L";
					}
						
					//기준 수정 
					jrParam.setField("ITEM"			, "KD01"+commUtils.getValue(gdReq, "CD_GP", ii)); 
					jrParam.setField("REPR_CD_GP"	, "K00009" ); 
					jrParam.setField("CD_GP"		, sCD_GP ); 
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYsRule1", logId, methodNm, "봉강제품 기준관리 수정");
					
					
				}
				
				if (commUtils.getValue(gdReq, "REPR_CD_GP", ii).equals("K00004")
						&& commUtils.getValue(gdReq, "CD_GP", ii).equals("0002")) {
					jrParam.setField("REPR_CD_GP"	, "K00008" );
					jrParam.setField("ITEM"			, commUtils.getValue(gdReq, "ITEM", ii)); 
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYsRuleK00008", logId, methodNm, "봉강제품 기준관리 수정1");
				}
				
				if(commUtils.getValue(gdReq, "REPR_CD_GP", ii).equals("K00015")) {  //선재창고 열별 비고(공지) 수정
					//기준 수정 
					jrParam.setField("ITEM"			, commUtils.getValue(gdReq, "ITEM", ii)); 
					jrParam.setField("REPR_CD_GP"	, commUtils.getValue(gdReq, "REPR_CD_GP", ii) ); 
					jrParam.setField("CD_GP"		, commUtils.getValue(gdReq, "CD_GP", ii) );
					jrParam.setField("REPR_CD_CONTENTS"		, commUtils.getValue(gdReq, "REPR_CD_CONTENTS", ii) );
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYsRule2", logId, methodNm, "선재제품 기준관리 수정2");
				}
				
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
	 *      [A] 오퍼레이션명 : 봉강 BED별 이적지시
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updgdsRbBedMoveWojm(GridData gdReq) throws DAOException {
		String methodNm = "봉강 BED별 이적지시[GdsYsJspSeEJB.updgdsRbBedMoveWojm] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String ydWbookIdSet ="";
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;
			JDTORecordSet jsMsg = JDTORecordFactory.getInstance().createRecordSet("");	//크레인스케줄 정리를 위함

			String stlNos        = commUtils.trim(gdReq.getParam("SSTL_NOS"         )); //재료번호들
			String ysStkColGp    = commUtils.trim(gdReq.getParam("YS_STK_COL_GP"   )); //야드적치열구분(4자리 이상)
			String ydToLocGuide  = commUtils.trim(gdReq.getParam("YD_TO_LOC_GUIDE" )); //야드To위치Guide
			String ydWrkPlanTcar = commUtils.trim(gdReq.getParam("YD_WRK_PLAN_TCAR")); //야드작업계획대차
			String modifier      = commUtils.trim(gdReq.getParam("userid"));
			if (ysStkColGp.length() > 6) {
				ysStkColGp = ysStkColGp.substring(0, 6);
			}

			if ("".equals(stlNos)) {
				throw new Exception("이적 재료번호가 없습니다.");
			} else if ("".equals(ysStkColGp) || ysStkColGp.length() < 4) {
				throw new Exception("Span[" + ysStkColGp + "] 정보가 없습니다.");
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
				String ydEqpGp = ysStkColGp.substring(2, 4); //야드설비구분(이적 스케줄코드 생성용)

				//To위치Guide가 있으면 그 값으로 스케줄코드 생성
				if ("".equals(ydToLocGuide)) {
					ydEqpGp = ysStkColGp.substring(2, 4);
				}

				if(ysStkColGp.equals("KA0101") ||ysStkColGp.equals("KA0102")){
					ydSchCd = "KAYD01MM";
				} else if(ysStkColGp.equals("KA0103") ||ysStkColGp.equals("KA0104")){	
					ydSchCd = "KAYD02MM";
				} else if(ysStkColGp.equals("KA0105") ||ysStkColGp.equals("KA0106")){	
					ydSchCd = "KAYD03MM";
				} else if(ysStkColGp.equals("KA0107") ||ysStkColGp.equals("KA0108")){	
					ydSchCd = "KAYD04MM";
				} else if(ysStkColGp.equals("KA0109") ||ysStkColGp.equals("KA0110")){	
					ydSchCd = "KAYD05MM";
				} else if(ysStkColGp.equals("KA0111") ||ysStkColGp.equals("KA0112")){	
					ydSchCd = "KAYD06MM";
				} else if(ysStkColGp.equals("KA0113") ||ysStkColGp.equals("KA0114")){	
					ydSchCd = "KAYD07MM";
				} else {				
					ydSchCd = ysStkColGp.substring(0, 2) + "YD" + ydEqpGp + "MM";
				}
				ydWrkPlanTcar = "";
			} else {
				if ("".equals(ydWrkPlanTcar)) {
					throw new Exception("To위치지정 동간이적 대차 정보가 없습니다.");
				}
				ydSchCd = ysStkColGp.substring(0, 2) + ydWrkPlanTcar.substring(2, 6) + "UM";
			}

					
			
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			jrParam.setField("SSTL_NOS"      	, stlNos    ); //재료번호들
			jrParam.setField("YS_STK_COL_GP"	, ysStkColGp); //야드적치열구분
			jrParam.setField("NEW_YS_STK_COL_GP", ydToLocGuide); //야드적치열구분

			//작업예약 대상재료 조회
			JDTORecordSet jsWbMtl = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getGdsMvStkWrkBookMtlPp", logId, methodNm, "재료번호로 조회");

			int rowCnt = jsWbMtl.size();

			if (rowCnt <= 0) {
				throw new Exception("이적 재료 정보가 없습니다.");
			}
			
			JDTORecord jrWbMtl = jsWbMtl.getRecord(0);

	    	if (commUtils.trim(jrWbMtl.getFieldString("YD_STK_COL_ACT_STAT")).equals("N")) {
				throw new Exception("이적위치 비 활성화 상태 입니다..");
			}
			
			/**********************************************************
			* 2. 이적 작업예약 등록
			**********************************************************/
			jrParam.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
			jrParam.setField("YD_AIM_BAY_GP"   , ydAimBayGp   ); //야드목표동구분
			jrParam.setField("YD_TO_LOC_GUIDE" , ydToLocGuide ); //야드To위치Guide
			jrParam.setField("YD_WRK_PLAN_TCAR", ydWrkPlanTcar); //야드작업계획대차
			
			//작업예약등록
//			jsMsg.addRecord(this.insMvstkWrkBook(jrParam, jsWbMtl));
			
			/**********************************************************
			* 1. 야드스케쥴코드 Check
			**********************************************************/
			JDTORecord jrSchCd = gdsYsComm.chkSchCdEqp(jrParam);
			
			jrSchCd.setResultCode(logId);	//Log ID
			jrSchCd.setResultMsg(methodNm);	//Log Method Name
			
			String ydGp       = ydSchCd.substring(0, 1);	//야드구분
			String ydEqpId    = commUtils.trim(jrSchCd.getFieldString("YD_EQP_ID"   ));	//야드설비ID(크레인)
			String ydSchPrior = commUtils.trim(jrSchCd.getFieldString("YD_SCH_PRIOR"));	//야드스케쥴우선순위
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
			Vector vcLot = this.setCrnSpecSpr(jrSchCd, jsWbMtl);
			commUtils.printParam("", vcLot);
			JDTORecordSet jsLotMtl = null;
			int lotCnt   = vcLot.size();	//크레인사양 분리 작업예약수
			int lotMtlSh = 0;				//작업예약재료매수
			String ydWbookId = "";			//야드작업예약ID
			String ydWbookIdFst = "";		//야드작업예약ID(첫번째)
			JDTORecord jrRow = null;
			commUtils.printLog(logId, "lotCnt:"+lotCnt, "SL");
			JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, modifier);
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
					ydWbookIdSet = ydWbookId;
				}else {
					ydWbookIdSet =ydWbookIdSet+ ","+ydWbookId;
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

				commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBook", logId, methodNm, "TB_YS_WRKBOOK");
				
				 
				//작업예약재료 등록
				
				for (int jj = 0; jj < lotMtlSh; jj++) {
					jrRow = jsLotMtl.getRecord(jj);
					
					JDTORecord jrRtn1 = JDTORecordFactory.getInstance().create();
					
					jrRtn1.setField("YD_WBOOK_ID"   , ydWbookId     ); //야드작업예약ID
					jrRtn1.setField("SSTL_NO"       , commUtils.trim(jrRow.getFieldString("SSTL_NO"       )));	//재료번호
					jrRtn1.setField("YS_STK_COL_GP" , commUtils.trim(jrRow.getFieldString("YS_STK_COL_GP")));	//야드적치열구분
					jrRtn1.setField("YS_STK_BED_NO" , commUtils.trim(jrRow.getFieldString("YS_STK_BED_NO")));	//야드적치Bed번호
					jrRtn1.setField("YS_STK_LYR_NO" , commUtils.trim(jrRow.getFieldString("YS_STK_LYR_NO")));	//야드적치단번호
					jrRtn1.setField("YS_STK_SEQ_NO" , commUtils.trim(jrRow.getFieldString("YS_STK_SEQ_NO")));	//야드적치SEQ번호
					jrRtn1.setField("YD_UP_COLL_SEQ" , ""+jj);	//야드적치SEQ번호
					
					jrRtn1.setField("MODIFIER"     	, modifier      );												//등록자
					commDao.insert(jrRtn1, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBookMtl", logId, methodNm, "TB_YS_WRKBOOKMTL");
				}
				

				//크레인스케줄 전문 - Log ID, Method, 수정자 Set
				
//				if(ydEqpId.substring(2, 4).equals("SC")) {
//					jrYdMsg = JDTORecordFactory.getInstance().create();
//					jrYdMsg.setField("YD_WBOOK_ID"     , ydWbookId ); //야드작업예약ID(첫번째꺼만)
//					jrYdMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
//					jrYdMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
//					jrYdMsg.setField("YD_SCH_ST_GP"    , "M"          ); //야드스케쥴기동구분(Manual)
//					jrYdMsg.setField("YD_SCH_REQ_GP"   , "M"          ); //야드스케쥴요청구분(이적)
//					jrYdMsg.setField("YD_WRK_PLAN_TCAR", ydWrkPlanTcar); //야드작업계획대차(대차상차 크레인스케줄을 전송하지 않기 위해 추가)
//					jsMsg.addRecord(jrYdMsg);
//				}	
			}
			
			/////////////////////////////////////////////////////////////////////////////////
			//-------------------작업예약 적치 순서를 고려해서 크레인 스케쥴 호출 ----------------------
			/////////////////////////////////////////////////////////////////////////////////
			jrParam.setField("YD_WBOOK_ID2"       , ydWbookIdSet     ); //야드작업예약ID
			
			JDTORecordSet jsWbMtl2 = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getGdsMvStkWrkBookList", logId, methodNm, "작업예약번호로 조회");

			int rowCnt2 = jsWbMtl2.size();

			if (rowCnt2 <= 0) {
				throw new Exception("이적 재료 정보가 없습니다.");
			}
			 
			for (int ii = 0; ii < rowCnt2; ii++) {
				//작업예약재료
				JDTORecord jrRow2 = jsWbMtl2.getRecord(ii);
				
				jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setField("YD_WBOOK_ID"     , commUtils.trim(jrRow2.getFieldString("YD_WBOOK_ID"))); //야드작업예약ID
				jrYdMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
				jrYdMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
				jrYdMsg.setField("YD_SCH_ST_GP"    , "M"          ); //야드스케쥴기동구분(Manual)
				jrYdMsg.setField("YD_SCH_REQ_GP"   , "M"          ); //야드스케쥴요청구분(이적)
				jrYdMsg.setField("YD_WRK_PLAN_TCAR", ydWrkPlanTcar); //야드작업계획대차(대차상차 크레인스케줄을 전송하지 않기 위해 추가)
				jsMsg.addRecord(jrYdMsg);
				
			}
			/////////////////////////////////////////////////////////////////////////////////
			

//			if(!ydEqpId.substring(2, 4).equals("SC")) {
//				jrYdMsg = JDTORecordFactory.getInstance().create();
//				jrYdMsg.setField("YD_WBOOK_ID"     , ydWbookIdFst ); //야드작업예약ID(첫번째꺼만)
//				jrYdMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
//				jrYdMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
//				jrYdMsg.setField("YD_SCH_ST_GP"    , "M"          ); //야드스케쥴기동구분(Manual)
//				jrYdMsg.setField("YD_SCH_REQ_GP"   , "M"          ); //야드스케쥴요청구분(이적)
//				jrYdMsg.setField("YD_WRK_PLAN_TCAR", ydWrkPlanTcar); //야드작업계획대차(대차상차 크레인스케줄을 전송하지 않기 위해 추가)
//				jsMsg.addRecord(jrYdMsg);
//			}
			commUtils.printLog(logId, methodNm, "S-");			
			
			/**********************************************************
			* 3. 대차작업이 있으면 공대차출발지시 처리
			**********************************************************/
			if (!"".equals(ydWrkPlanTcar)) {
				//공대차출발지시 처리시 Exception을 발생시키지 않기위해 미리 Check
				String msgTcar = ""; //공대차출발지시 처리 메세지
			 			
				//대차스케쥴정보(공대차출발지시) 조회
				jrParam.setField("YD_EQP_ID", ydWrkPlanTcar);	//야드설비ID(대차)
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
				          FROM TB_YD_TCARFTMVMTL TM
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
				                         FROM TB_YD_TCARSCH
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
					jrParam.setField("YD_L2_ID" , "N4"    ); 
					jrRtn = YsComm.trtTcarSchLevWo(jrParam);
				} else {
					commUtils.printLog(logId, "대차[" + ydWrkPlanTcar + "] 공대차출발지시 불가 : " + msgTcar, "SL");
				}
			}

			/**********************************************************
			* 4. 동내이적(대차작업이 없음)작업 크레인별 첫번째 스케줄 전송
			**********************************************************/
			jrRtn = commUtils.addSndData(jrRtn, this.setAutoCrnSchMsg(jsMsg, logId, methodNm));
		  
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
		String methodNm = "이적작업예약등록[GdsYsJspSeEJB.insMvstkWrkBook] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		try {
			commUtils.printLog(logId, methodNm, "S+");

			String ydSchCd       = commUtils.trim(jrParam.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
			String ydAimBayGp    = commUtils.trim(jrParam.getFieldString("YD_AIM_BAY_GP"   )); //야드목표동구분
			String ydToLocGuide  = commUtils.trim(jrParam.getFieldString("YD_TO_LOC_GUIDE" )); //야드To위치Guide
			String ydWrkPlanTcar = commUtils.trim(jrParam.getFieldString("YD_WRK_PLAN_TCAR")); //야드작업계획대차
			String modifier      = commUtils.trim(jrParam.getFieldString("MODIFIER"        )); //수정자
			
			/**********************************************************
			* 1. 야드스케쥴코드 Check
			**********************************************************/
			JDTORecord jrCrnSpec = gdsYsComm.chkSchCdEqp(jrParam);
			
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
			Vector vcLot = this.setCrnSpecSpr(jrCrnSpec, jsWbMtl);
			commUtils.printParam("", vcLot);
			JDTORecordSet jsLotMtl = null;
			int lotCnt   = vcLot.size();	//크레인사양 분리 작업예약수
			int lotMtlSh = 0;				//작업예약재료매수
			String ydWbookId = "";			//야드작업예약ID
			String ydWbookIdFst = "";		//야드작업예약ID(첫번째)
			JDTORecord jrRow = null;
			commUtils.printLog(logId, "lotCnt:"+lotCnt, "SL");
			JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, modifier);
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

				commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBook", logId, methodNm, "TB_YS_WRKBOOK");

				//작업예약재료 등록
				
				for (int jj = 0; jj < lotMtlSh; jj++) {
					commUtils.printLog(logId, "jj"+ jj + "lotMtlSh" + lotMtlSh, "S--------");
					jrRow = jsLotMtl.getRecord(jj);
					
					JDTORecord jrRtn1 = JDTORecordFactory.getInstance().create();
					
					jrRtn1.setField("YD_WBOOK_ID"   , ydWbookId     ); //야드작업예약ID
					jrRtn1.setField("SSTL_NO"       , commUtils.trim(jrRow.getFieldString("SSTL_NO"       )));	//재료번호
					jrRtn1.setField("YS_STK_COL_GP" , commUtils.trim(jrRow.getFieldString("YS_STK_COL_GP")));	//야드적치열구분
					jrRtn1.setField("YS_STK_BED_NO" , commUtils.trim(jrRow.getFieldString("YS_STK_BED_NO")));	//야드적치Bed번호
					jrRtn1.setField("YS_STK_LYR_NO" , commUtils.trim(jrRow.getFieldString("YS_STK_LYR_NO")));	//야드적치단번호
					jrRtn1.setField("YS_STK_SEQ_NO" , commUtils.trim(jrRow.getFieldString("YS_STK_SEQ_NO")));	//야드적치SEQ번호
					jrRtn1.setField("MODIFIER"     	, modifier      );												//등록자
					commDao.insert(jrRtn1, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBookMtl", logId, methodNm, "TB_YS_WRKBOOKMTL");
				}
				

				//크레인스케줄 전문 - Log ID, Method, 수정자 Set
				
				if(ydEqpId.substring(2, 4).equals("SC")) {
					jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setField("YD_WBOOK_ID"     , ydWbookId ); //야드작업예약ID(첫번째꺼만)
					jrYdMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
					jrYdMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
					jrYdMsg.setField("YD_SCH_ST_GP"    , "M"          ); //야드스케쥴기동구분(Manual)
					jrYdMsg.setField("YD_SCH_REQ_GP"   , "M"          ); //야드스케쥴요청구분(이적)
					jrYdMsg.setField("YD_WRK_PLAN_TCAR", ydWrkPlanTcar); //야드작업계획대차(대차상차 크레인스케줄을 전송하지 않기 위해 추가)
					jrRtn.addRecord(jrYdMsg);
				}	
			}
			commUtils.printLog(logId, methodNm, "S-----------------");
			if(!ydEqpId.substring(2, 4).equals("SC")) {
				jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setField("YD_WBOOK_ID"     , ydWbookIdFst ); //야드작업예약ID(첫번째꺼만)
				jrYdMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
				jrYdMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
				jrYdMsg.setField("YD_SCH_ST_GP"    , "M"          ); //야드스케쥴기동구분(Manual)
				jrYdMsg.setField("YD_SCH_REQ_GP"   , "M"          ); //야드스케쥴요청구분(이적)
				jrYdMsg.setField("YD_WRK_PLAN_TCAR", ydWrkPlanTcar); //야드작업계획대차(대차상차 크레인스케줄을 전송하지 않기 위해 추가)
				jrRtn.addRecord(jrYdMsg);
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
		String methodNm = "크레인스케줄전문정리[GdsYsJspSeEJB.setCrnSchMsg] < " + mthdNm;

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

//????? SJH				
//				for (int ii = rowCnt - 1; ii >= 0; ii--) {
				for (int ii = 0; ii < rowCnt; ii++) {
					jrRow = jsMsg.getRecord(ii);
					jrRow.setResultCode(logId);	//Log ID
					jrRow.setResultMsg(methodNm);	//Log Method Name

					ydEqpId = commUtils.trim(jrRow.getFieldString("YD_EQP_ID"));
					if(ydEqpId.substring(2, 4).equals("SC")) {
						jrRtn = commUtils.addSndData(jrRtn, gdsYsComm.getCrnSchMsg(jrRow));
					} else  {
						
						if ("".equals(commUtils.trim(jrRow.getFieldString("YD_WRK_PLAN_TCAR")))) {
							//야드작업계획대차가 있으면 대차상차 크레인스케줄이므로 전송하지 않음 -> 공대차출발지시로 처리
							fstYn = true;
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
									jrRtn = commUtils.addSndData(jrRtn, gdsYsComm.getCrnSchMsg(jrRow));
								}
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
	 * 출고검수 이상제품 - 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updCarIssueChk(GridData gdReq) throws DAOException {
		String methodNm = "출고검수 이상제품 - 수정[GdsYsJspSeEJB.updCarIssueChk] < " + gdReq.getNavigateValue();
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
				
				//이상코드 수정 
				jrParam.setField("YD_AB_CD"			, commUtils.getValue(gdReq, "YD_AB_CD", ii)); 
				jrParam.setField("YD_AB_CD2"		, commUtils.getValue(gdReq, "YD_AB_CD2", ii) ); 
				jrParam.setField("TRANS_ORD_DATE"	, commUtils.getValue(gdReq, "TRANS_ORD_DATE", ii) ); 
				jrParam.setField("TRANS_ORD_SEQNO"	, commUtils.getValue(gdReq, "TRANS_ORD_SEQNO", ii) ); 
				jrParam.setField("SSTL_NO"			, commUtils.getValue(gdReq, "SSTL_NO", ii) ); 
				
				commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updExamChkListAbCd", logId, methodNm, "출고검수 이상제품 수정");
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updCarIssueChk
	
	/**
	 * 출고검수 PDA - 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updCarIssueChkPda(JDTORecord recPara) throws DAOException {
		String methodNm = "출고검수 PDA - 수정[GdsYsJspSeEJB.updCarIssueChkPda] < " + recPara.getResultMsg();
		String logId = recPara.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+", recPara);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(recPara.getFieldString("userid")));

			jrParam.setField("YD_AB_CD"			, commUtils.trim(recPara.getFieldString("YD_AB_CD")));
			jrParam.setField("YD_AB_CD2"		, commUtils.trim(recPara.getFieldString("YD_AB_CD2")));
			jrParam.setField("CHECKING_YN"		, commUtils.trim(recPara.getFieldString("CHECKING_YN")));
			jrParam.setField("TRANS_ORD_DATE"	, commUtils.trim(recPara.getFieldString("TRANS_ORD_DATE")));
			jrParam.setField("TRANS_ORD_SEQNO"	, commUtils.trim(recPara.getFieldString("TRANS_ORD_SEQNO")));
			jrParam.setField("SSTL_NO"			, commUtils.trim(recPara.getFieldString("SSTL_NO")));
			jrParam.setField("LABEL_YN"		    , commUtils.trim(recPara.getFieldString("LABEL_YN")));
			
			commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updExamChkListAbCdPda", logId, methodNm, "출고검수 PDA 수정");
			
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
	} // end of updCarIssueChkPda
	
	/**
	 *      출고검수
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord distCheckProc(GridData gdReq) throws DAOException {
		String methodNm = "출고검수 [GdsYsJspSeEJB.updCarIssueChk] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name			
			//출고검수 완료 설정 
			jrParam.setField("TRANS_ORD_DATE"	, gdReq.getParam("TRANS_ORD_DATE") ); 
			jrParam.setField("TRANS_ORD_SEQNO"	, gdReq.getParam("TRANS_ORD_SEQNO") ); 
			
			commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updExamChkListSetY", logId, methodNm, "출고검수 이상제품 수정");
			
			//PIDEV			
//			String sApplyYnPI = commDao.ApplyYnPI("", "출고검수 [GdsYsJspSeEJB.updCarIssueChk]", "APPPI0", "K", "*");
			
			//검수완료 실적 전문 생성
//			if("Y".equals(sApplyYnPI)) {
				jrRtn = commUtils.addSndData(commDao.getMsgL3("M10YDLMJ1104", jrParam));
//			} else {
//				jrRtn = commUtils.addSndData(commDao.getMsgL3("YSDSJ009", jrParam));
//			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} //end of distCheckProc
	
	/**
	 * 출고검수 PDA - 출고검수
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord distCheckProcPda(JDTORecord recPara) throws DAOException {
		String methodNm = "출고검수 PDA - 출고검수[GdsYsJspSeEJB.distCheckProcPda] < " + recPara.getResultMsg();
		String logId = recPara.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+", recPara);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(recPara.getFieldString("userid")));

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("TRANS_ORD_DATE"	, commUtils.trim(recPara.getFieldString("TRANS_ORD_DATE")));
			jrParam.setField("TRANS_ORD_SEQNO"	, commUtils.trim(recPara.getFieldString("TRANS_ORD_SEQNO")));
			
			commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updExamChkListSetY", logId, methodNm, "출고검수 PDA 검수완료");

//			jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSDSJ009", jrParam));
//PIDEV			
//			String sApplyYnPI = commDao.ApplyYnPI("", "출고검수 PDA - 출고검수", "APPPI0", "K", "*");
//			if("Y".equals(sApplyYnPI)) {
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1104", jrParam));
//			} else {
//				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSDSJ009", jrParam));
//			}	
			
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
	} // end of distCheckProcPda
	
	/**
	 * 입고검수 PDA - 입고검수
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord whsChkProcPda(JDTORecord recPara) throws DAOException {
		String methodNm = "입고검수 PDA - 입고검수[GdsYsJspSeEJB.distCheckProcPda] < " + recPara.getResultMsg();
		String logId = recPara.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+", recPara);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(recPara.getFieldString("userid")));

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("SSTL_NO"		 , commUtils.trim(recPara.getFieldString("SSTL_NO")));
			jrParam.setField("YS_STK_COL_GP" , commUtils.trim(recPara.getFieldString("YS_STK_COL_GP")));
			
			commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updWhsChkPda", logId, methodNm, "입고검수 PDA 입고완료");

			//jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSDSJ009", jrParam));	
			
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
	} // end of whsChkProcPda

	/**
	 * 입고검수 PDA - 입고검수(각강)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord sbChkProcPda(JDTORecord recPara) throws DAOException {
		String methodNm = "입고검수 PDA - 입고검수(각강)[GdsYsJspSeEJB.sbChkProcPda] < " + recPara.getResultMsg();
		String logId = recPara.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+", recPara);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(recPara.getFieldString("userid")));
			
			jrParam.setField("SSTL_NO"		 , commUtils.trim(recPara.getFieldString("SSTL_NO")));
			jrParam.setField("YS_STK_LYR_NO" , commUtils.trim(recPara.getFieldString("YS_STK_LYR_NO")));
			jrParam.setField("YS_STK_SEQ_NO" , commUtils.trim(recPara.getFieldString("YS_STK_SEQ_NO")));
			jrParam.setField("MODIFIER"		 , commUtils.trim(recPara.getFieldString("MODIFIER")));
			
			if (commUtils.trim(recPara.getFieldString("CHECKING_YN")).equals("N")) 
				commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updSbChkPdaClear", logId, methodNm, "입고검수 PDA(각강) 입고취소");
			else 				
				commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updSbChkPda", logId, methodNm, "입고검수 PDA(각강) 입고완료");
			
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
	} // end of sbChkProcPda
	
	/**
	 * 반입검수 PDA - 반입검수
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord ReturnChkProcPda(JDTORecord recPara) throws DAOException {
		String methodNm = "반입검수 PDA - 반입검수[GdsYsJspSeEJB.ReturnChkProcPda] < " + recPara.getResultMsg();
		String logId = recPara.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+", recPara);

			commUtils.printParam(logId + "반입검수 PDA", recPara);
			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(recPara.getFieldString("userid")));
			
			jrParam.setField("SSTL_NO"		 , commUtils.trim(recPara.getFieldString("SSTL_NO")));
			jrParam.setField("YS_STK_LYR_NO" , commUtils.trim(recPara.getFieldString("YS_STK_LYR_NO")));
			jrParam.setField("MODIFIER"		 , commUtils.trim(recPara.getFieldString("MODIFIER")));
			int irtn = 0;
			if (commUtils.trim(recPara.getFieldString("CHECKING_YN")).equals("N")) {
				irtn = commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updReturnChkPdaClear", logId, methodNm, "반입검수 PDA 반입검수취소");
			} else {
				/* com.inisteel.cim.ys.gds.dao.GdsYsDAO.updReturnChkPda 
				UPDATE TB_YS_CARFTMVMTL A
				SET    REGISTER = DECODE(LENGTH(NVL(REGISTER,' ')),10,SUBSTR(REGISTER,1,9)||'*',REGISTER||'*')
				      ,REG_DDTT = SYSDATE
				      ,MODIFIER = A.REGISTER
				      ,MOD_DDTT = SYSDATE
				      ,YS_STK_BED_NO = '01'
				      ,YS_STK_LYR_NO = :V_YS_STK_LYR_NO
				      ,YS_STK_SEQ_NO = '1'
				WHERE  SSTL_NO = :V_SSTL_NO
				AND    DEL_YN = 'N'
				*/	
				irtn = commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updReturnChkPda", logId, methodNm, "반입검수 PDA 반입검수완료");
			}
			
			commUtils.printLog(logId, "정상처리여부 :" +irtn, "SL");
			//정상처리를 리턴한다.
			jrRtn = JDTORecordFactory.getInstance().create();
			if (irtn == 1) {
				jrRtn.setField("RETN_CD", YsConstant.RETN_CD_SUCCESS);
				jrRtn.setField("RETN_MSG", "정상처리 되었습니다!!");
			}
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of sbChkProcPda
	
	/**
	 * 반입검수 PDA - 차량출발 및 입동 지시 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord ReturnCarBayInOrdReq(JDTORecord recPara) throws DAOException {
		String methodNm = "반입검수 PDA - 차량출발 및 입동 지시 [GdsYsJspSeEJB.ReturnCarBayInOrdReq] < " + recPara.getResultMsg();
		String logId = recPara.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+", recPara);
			
			String sYD_CAR_SCH_ID 	= commUtils.trim(recPara.getFieldString("YD_CAR_SCH_ID"));
			String sYD_CAR_STOP_LOC = commUtils.trim(recPara.getFieldString("YD_CAR_STOP_LOC"));
			String sYD_GP			= commUtils.trim(recPara.getFieldString("YD_GP"));
			String sYD_BAY_GP		= commUtils.trim(recPara.getFieldString("YD_BAY_GP"));
			String sYD_PNT_CD		= null;
			String sWLOC_CD			= null;

			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(recPara.getFieldString("userid")));
			
			//야드구분과 동으로 차량포인트를 구한다.
			jrParam.setField("YD_GP"         	, sYD_GP       ); 	//야드구분
			jrParam.setField("YD_BAY_GP"        , sYD_BAY_GP   ); 	//동구분
			
			JDTORecordSet jsPnt = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getReturnCarPntCd", logId, methodNm, "반입도착가능 차량포인트 조회");

			if (jsPnt.size() <= 0) {
				jrRtn.setField("RETN_CD", YsConstant.RETN_CD_FAILURE);
				jrRtn.setField("RETN_MSG", "도착가능 차량포인트가 없습니다.!!");	
				return jrRtn;
			}

			jsPnt.first();
			JDTORecord jrCarPnt = jsPnt.getRecord();
			
			sYD_CAR_STOP_LOC	= commUtils.trim(jrCarPnt.getFieldString("YS_STK_COL_GP" ));
			sYD_PNT_CD 			= commUtils.trim(jrCarPnt.getFieldString("YD_PNT_CD" ));
			sWLOC_CD 			= commUtils.trim(jrCarPnt.getFieldString("WLOC_CD" ));
			
			jrParam.setField("YD_CAR_SCH_ID" 	, sYD_CAR_SCH_ID);
			jrParam.setField("YD_CARUD_STOP_LOC", sYD_CAR_STOP_LOC);	//차량하차정지위치
			jrParam.setField("YD_PNT_CD3" 		, sYD_PNT_CD);			//야드포인트코드
			jrParam.setField("ARR_WLOC_CD" 		, sWLOC_CD);			//착지개소코드
			jrParam.setField("YD_CAR_PROG_STAT" , "A");					//하차출발

			/* com.inisteel.cim.ys.common.dao.YsCommDAO.updCarSchUdLoc
			-- 차량스케줄 하차위치 변경
			UPDATE TB_YS_CARSCH
			   SET MODIFIER             = :V_MODIFIER
			      ,MOD_DDTT             = SYSDATE
			      ,YD_CARUD_STOP_LOC    = :V_YD_CARUD_STOP_LOC
			      ,YD_PNT_CD3           = :V_YD_PNT_CD3
			      ,ARR_WLOC_CD          = :V_ARR_WLOC_CD
			      ,YD_CAR_PROG_STAT     = :V_YD_CAR_PROG_STAT
			 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
			   AND DEL_YN = 'N'
			*/	
			int irtn = commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCarSchUdLoc", logId, methodNm, "반입검수 PDA 차량스케줄 하차위치 변경");
			
			commUtils.printLog(logId, "정상처리여부 :" +irtn, "SL");
			//정상처리를 리턴한다.
			
			if (irtn == 1) {
				
				
				JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setResultCode(logId);	//Log ID
				recInTemp.setResultMsg(methodNm);	//Log Method Name
				recInTemp.setField("JMS_TC_CD"			, "YSYSJ801");          //차량입동지시 요구 기존:YDYDJ662
				recInTemp.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시
				recInTemp.setField("YD_CAR_SCH_ID"		, sYD_CAR_SCH_ID);	        //차량스케줄ID
				recInTemp.setField("YD_CAR_STOP_LOC"	, sYD_CAR_STOP_LOC);
				
				jrRtn = commUtils.addSndData(jrRtn, recInTemp);
				
				jrRtn.setField("RETN_CD", YsConstant.RETN_CD_SUCCESS);
				jrRtn.setField("RETN_MSG", "정상처리 되었습니다!!");
			}
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of sbChkProcPda
		
	
	/**
	 *      [A] 오퍼레이션명 : 봉강A동 이적작업예약등록
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord insgdsRbABayMoveWkAutojm(GridData gdReq) throws DAOException {
		String methodNm = "봉강A동 이적작업 자동지시[GdsYsJspSeEJB.insgdsRbABayMoveWkAutojm] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;
			JDTORecordSet jsMsg = JDTORecordFactory.getInstance().createRecordSet("");	//크레인스케줄 정리를 위함

			String stlNos        = commUtils.trim(gdReq.getParam("SSTL_NOS"         )); //재료번호들
			String ysStkColGp    = commUtils.trim(gdReq.getParam("YS_STK_COL_GP"   )); //야드적치열구분(4자리 이상)
			String ydToLocGuide  = ysStkColGp.substring(0, 6);
			String modifier      = commUtils.trim(gdReq.getParam("userid"));
			if (ysStkColGp.length() > 6) {
				ysStkColGp = ysStkColGp.substring(0, 6);
			}

			if ("".equals(stlNos)) {
				throw new Exception("이적 재료번호가 없습니다.");
			} else if ("".equals(ysStkColGp) || ysStkColGp.length() < 4) {
				throw new Exception("Span[" + ysStkColGp + "] 정보가 없습니다.");
			} 
			
			/**********************************************************
			* 1. 스케줄코드 설정
			**********************************************************/
			String ydSchCd    = ""; //야드스케쥴코드
			String ydBayGp    = ysStkColGp.substring(1, 2); //야드동구분
			String ydAimBayGp = ""; //야드목표동구분

			//스케쥴코드

			if(ysStkColGp.equals("KA0101") ||ysStkColGp.equals("KA0102")){
				ydSchCd = "KAYD01MM";
			} else if(ysStkColGp.equals("KA0103") ||ysStkColGp.equals("KA0104")){	
				ydSchCd = "KAYD02MM";
			} else if(ysStkColGp.equals("KA0105") ||ysStkColGp.equals("KA0106")){	
				ydSchCd = "KAYD03MM";
			} else if(ysStkColGp.equals("KA0107") ||ysStkColGp.equals("KA0108")){	
				ydSchCd = "KAYD04MM";
			} else if(ysStkColGp.equals("KA0109") ||ysStkColGp.equals("KA0110")){	
				ydSchCd = "KAYD05MM";
			} else if(ysStkColGp.equals("KA0111") ||ysStkColGp.equals("KA0112")){	
				ydSchCd = "KAYD06MM";
			} else if(ysStkColGp.equals("KA0113") ||ysStkColGp.equals("KA0114")){	
				ydSchCd = "KAYD07MM";
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
			* 2. 이적 작업예약 등록
			**********************************************************/
			jrParam.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
			jrParam.setField("YD_AIM_BAY_GP"   , ydAimBayGp   ); //야드목표동구분
			jrParam.setField("YD_TO_LOC_GUIDE" , ydToLocGuide ); //야드To위치Guide
			
			//작업예약등록
//			jsMsg.addRecord(this.insMvstkWrkBook(jrParam, jsWbMtl));
			
			/**********************************************************
			* 1. 야드스케쥴코드 Check
			**********************************************************/
			JDTORecord jrSchCd = gdsYsComm.chkSchCdEqp(jrParam);
			
			String ydGp       = ydSchCd.substring(0, 1);	//야드구분
			String ydEqpId    = commUtils.trim(jrSchCd.getFieldString("YD_EQP_ID"   ));	//야드설비ID(크레인)
			String ydSchPrior = commUtils.trim(jrSchCd.getFieldString("YD_SCH_PRIOR"));	//야드스케쥴우선순위
//			String ydToLocDcsnMtd = "S"; //야드TO위치결정방법(스케줄지정)

			if ("".equals(ydAimBayGp)) {
				ydAimBayGp = ydBayGp;
			}

			/**********************************************************
			* 2. 크레인사양 분리
			**********************************************************/
			Vector vcLot = this.setCrnSpecSpr(jrSchCd, jsWbMtl);
			commUtils.printParam("", vcLot);
			JDTORecordSet jsLotMtl = null;
			int lotCnt   = vcLot.size();	//크레인사양 분리 작업예약수
			int lotMtlSh = 0;				//작업예약재료매수
			String ydWbookId = "";			//야드작업예약ID
			JDTORecord jrRow = null;
			commUtils.printLog(logId, "lotCnt:"+lotCnt, "SL");
			JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, modifier);
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
				
					//작업예약재료 등록
				
				for (int jj = 0; jj < lotMtlSh; jj++) {
					jrRow = jsLotMtl.getRecord(jj);
					
					JDTORecord jrRtn1 = JDTORecordFactory.getInstance().create();
					
					if(jj==0) {
						ydToLocGuide = 	commUtils.trim(jrRow.getFieldString("YS_STK_COL_GP"));
					}
					
					jrRtn1.setField("YD_WBOOK_ID"   , ydWbookId     ); //야드작업예약ID
					jrRtn1.setField("SSTL_NO"       , commUtils.trim(jrRow.getFieldString("SSTL_NO"       )));	//재료번호
					jrRtn1.setField("YS_STK_COL_GP" , commUtils.trim(jrRow.getFieldString("YS_STK_COL_GP")));	//야드적치열구분
					jrRtn1.setField("YS_STK_BED_NO" , commUtils.trim(jrRow.getFieldString("YS_STK_BED_NO")));	//야드적치Bed번호
					jrRtn1.setField("YS_STK_LYR_NO" , commUtils.trim(jrRow.getFieldString("YS_STK_LYR_NO")));	//야드적치단번호
					jrRtn1.setField("YS_STK_SEQ_NO" , commUtils.trim(jrRow.getFieldString("YS_STK_SEQ_NO")));	//야드적치SEQ번호
					jrRtn1.setField("MODIFIER"     	, modifier      );												//등록자
					commDao.insert(jrRtn1, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBookMtl", logId, methodNm, "TB_YS_WRKBOOKMTL");
				}
				

				//작업예약 등록
				jrParam = JDTORecordFactory.getInstance().create();
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
				jrParam.setField("YD_TO_LOC_DCSN_MTD", "F"); //야드TO위치결정방법
				jrParam.setField("YD_TO_LOC_GUIDE"   , ydToLocGuide  ); //야드To위치Guide

				commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBook", logId, methodNm, "TB_YS_WRKBOOK");

				
				
				//크레인스케줄 전문 - Log ID, Method, 수정자 Set
				
				if(ydEqpId.substring(2, 4).equals("SC")) {
					jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setField("YD_WBOOK_ID"     , ydWbookId ); //야드작업예약ID(첫번째꺼만)
					jrYdMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
					jrYdMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
					jrYdMsg.setField("YD_SCH_ST_GP"    , "M"          ); //야드스케쥴기동구분(Manual)
					jrYdMsg.setField("YD_SCH_REQ_GP"   , "M"          ); //야드스케쥴요청구분(이적)
					jsMsg.addRecord(jrYdMsg);
				}	
			}

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
	 *      [A] 오퍼레이션명 : 자동화 창고 크레인스케줄전문정리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecordSet jsMsg
	 *      @param String logId
	 *      @param String mthdNm
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord setAutoCrnSchMsg(JDTORecordSet jsMsg, String logId, String mthdNm) throws DAOException {
		String methodNm = "크레인스케줄전문정리[GdsYsJspSeEJB.setAutoCrnSchMsg] < " + mthdNm;

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn = null;

			if (!commUtils.isEmpty(jsMsg)) {
				JDTORecord jrRow = null;

				int rowCnt = jsMsg.size();

				for (int ii = rowCnt - 1; ii >= 0; ii--) {
					jrRow = jsMsg.getRecord(ii);
				
					//크레인스케줄 전송
					jrRtn = commUtils.addSndData(jrRtn, gdsYsComm.getCrnSchMsg(jrRow));
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
	 *      [A] 오퍼레이션명 : 봉강B동 선별작업 자동지시 << 이적작업예약등록
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord insgdsRbBBaySelWkAutojm(GridData gdReq) throws DAOException {
		String methodNm = "봉강B동선별 작업 자동지시[GdsYsJspSeEJB.insgdsRbBBaySelWkAutojm] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;
			JDTORecordSet jsMsg = JDTORecordFactory.getInstance().createRecordSet("");	//크레인스케줄 정리를 위함

			String stlNos        = commUtils.trim(gdReq.getParam("SSTL_NOS"         )); //재료번호들
			String ysStkColGp    = commUtils.trim(gdReq.getParam("YS_STK_COL_GP"   )); //야드적치열구분(4자리 이상)
			String ydToLocGuide  = commUtils.trim(gdReq.getParam("YD_TO_LOC_GUIDE"   ));;
			String ydWrkPlanTcar = commUtils.trim(gdReq.getParam("YD_WRK_PLAN_TCAR")); //야드작업계획대차
			String modifier      = commUtils.trim(gdReq.getParam("userid"));
			if (ysStkColGp.length() > 6) {
				ysStkColGp = ysStkColGp.substring(0, 6);
			}

			if ("".equals(stlNos)) {
				throw new Exception("이적 재료번호가 없습니다.");
			} else if ("".equals(ysStkColGp) || ysStkColGp.length() < 4) {
				throw new Exception("Span[" + ysStkColGp + "] 정보가 없습니다.");
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
				String ydEqpGp = ysStkColGp.substring(2, 4); //야드설비구분(이적 스케줄코드 생성용)

				//To위치Guide가 있으면 그 값으로 스케줄코드 생성
				if ("".equals(ydToLocGuide)) {
					ydEqpGp = ysStkColGp.substring(2, 4);
				}
				
				ydSchCd = ysStkColGp.substring(0, 2) + "YD" + ydEqpGp + "MM";
				ydWrkPlanTcar = "";
			} else {
				if ("".equals(ydWrkPlanTcar)) {
					throw new Exception("To위치지정 동간이적 대차 정보가 없습니다.");
				}
				ydSchCd = ysStkColGp.substring(0, 2) + ydWrkPlanTcar + "UM";
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
			* 2. 이적 작업예약 등록
			**********************************************************/
			jrParam.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
			jrParam.setField("YD_AIM_BAY_GP"   , ydAimBayGp   ); //야드목표동구분
			jrParam.setField("YD_TO_LOC_GUIDE" , ydToLocGuide ); //야드To위치Guide
			jrParam.setField("YD_WRK_PLAN_TCAR", ydWrkPlanTcar); //야드작업계획대차
			
			//작업예약등록
//			jsMsg.addRecord(this.insMvstkWrkBook(jrParam, jsWbMtl));
			
			/**********************************************************
			* 1. 야드스케쥴코드 Check
			**********************************************************/
			JDTORecord jrSchCd = gdsYsComm.chkSchCdEqp(jrParam);
			
			String ydGp       = ydSchCd.substring(0, 1);	//야드구분
			String ydEqpId    = commUtils.trim(jrSchCd.getFieldString("YD_EQP_ID"   ));	//야드설비ID(크레인)
			String ydSchPrior = commUtils.trim(jrSchCd.getFieldString("YD_SCH_PRIOR"));	//야드스케쥴우선순위
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
			Vector vcLot = this.setCrnSpecSpr(jrSchCd, jsWbMtl);
			commUtils.printParam("", vcLot);
			JDTORecordSet jsLotMtl = null;
			int lotCnt   = vcLot.size();	//크레인사양 분리 작업예약수
			int lotMtlSh = 0;				//작업예약재료매수
			String ydWbookId = "";			//야드작업예약ID
			String ydWbookIdFst = "";		//야드작업예약ID(첫번째)
			JDTORecord jrRow = null;
			commUtils.printLog(logId, "lotCnt:"+lotCnt, "SL");
			JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, modifier);
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

				commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBook", logId, methodNm, "TB_YS_WRKBOOK");

				//작업예약재료 등록
				
				for (int jj = 0; jj < lotMtlSh; jj++) {
					jrRow = jsLotMtl.getRecord(jj);
					
					JDTORecord jrRtn1 = JDTORecordFactory.getInstance().create();
					
					jrRtn1.setField("YD_WBOOK_ID"   , ydWbookId     ); //야드작업예약ID
					jrRtn1.setField("SSTL_NO"       , commUtils.trim(jrRow.getFieldString("SSTL_NO"       )));	//재료번호
					jrRtn1.setField("YS_STK_COL_GP" , commUtils.trim(jrRow.getFieldString("YS_STK_COL_GP")));	//야드적치열구분
					jrRtn1.setField("YS_STK_BED_NO" , commUtils.trim(jrRow.getFieldString("YS_STK_BED_NO")));	//야드적치Bed번호
					jrRtn1.setField("YS_STK_LYR_NO" , commUtils.trim(jrRow.getFieldString("YS_STK_LYR_NO")));	//야드적치단번호
					jrRtn1.setField("YS_STK_SEQ_NO" , commUtils.trim(jrRow.getFieldString("YS_STK_SEQ_NO")));	//야드적치SEQ번호
					jrRtn1.setField("MODIFIER"     	, modifier      );												//등록자
					commDao.insert(jrRtn1, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBookMtl", logId, methodNm, "TB_YS_WRKBOOKMTL");
				}
				

			}

			jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setField("YD_WBOOK_ID"     , ydWbookIdFst ); //야드작업예약ID(첫번째꺼만)
			jrYdMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
			jrYdMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
			jrYdMsg.setField("YD_SCH_ST_GP"    , "M"          ); //야드스케쥴기동구분(Manual)
			jrYdMsg.setField("YD_SCH_REQ_GP"   , "M"          ); //야드스케쥴요청구분(이적)
			jrYdMsg.setField("YD_WRK_PLAN_TCAR", ydWrkPlanTcar); //야드작업계획대차(대차상차 크레인스케줄을 전송하지 않기 위해 추가)
			jsMsg.addRecord(jrYdMsg);

			commUtils.printLog(logId, methodNm, "S-");			
			
			/**********************************************************
			* 3. 대차작업이 있으면 공대차출발지시 처리
			**********************************************************/
			if (!"".equals(ydWrkPlanTcar)) {
				//공대차출발지시 처리시 Exception을 발생시키지 않기위해 미리 Check
				String msgTcar = ""; //공대차출발지시 처리 메세지
			 			
				//대차스케쥴정보(공대차출발지시) 조회
				jrParam.setField("YD_EQP_ID", ydWrkPlanTcar);	//야드설비ID(대차)
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
				          FROM TB_YD_TCARFTMVMTL TM
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
				                         FROM TB_YD_TCARSCH
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
					jrParam.setField("YD_L2_ID" , "N4"    ); 
					jrRtn = YsComm.trtTcarSchLevWo(jrParam);
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
	 * 저장위치 기준관리 - 열정보 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updStrLocPosSetCol(GridData gdReq) throws DAOException {
		String methodNm = "저장위치 기준관리 - 열정보 변경[GdsYsJspSeEJB.updStrLocPosSetCol] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			String msgId = "";
			
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
				jrParam.setField("YD_STK_COL_DIR_GP"	, commUtils.getValue(gdReq, "YD_STK_COL_DIR_GP", ii) );

				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkcol", logId, methodNm, "열정보 수정");
								
				if("A".equals(jrParam.getFieldString("YD_BAY_GP"))) {
					msgId = "YSN6L001";
				} else if("B".equals(jrParam.getFieldString("YD_BAY_GP"))) {
					msgId = "YSN4L001";
				} else if("D".equals(jrParam.getFieldString("YD_BAY_GP"))) {
					msgId = "YSN5L001";
				} else if("E".equals(jrParam.getFieldString("YD_BAY_GP"))) {
					msgId = "YSN3L001";
				}
				
				jrParam.setField("YD_INFO_SYNC_CD", "4"          ); //야드정보동기화코드(Bed)
				jrParam.setField("YS_STK_COL_GP"  , commUtils.trim(gdReq.getHeader("YS_STK_COL_GP").getValue(ii))); //야드적치열구분
				//jrParam.setField("YD_STK_BED_NO"  , "01"         ); //야드적치Bed번호

				//전송Data 조회
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2(msgId, jrParam));				
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
	 * 저장위치 기준관리 - Bed정보 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updStrLocPosSetBed(GridData gdReq) throws DAOException {
		String methodNm = "저장위치 기준관리 - Bed정보 변경[GdsYsJspSeEJB.updStrLocPosSetBed] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			String msgId = "";

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
				jrParam.setField("YS_STK_COL_GP"		, commUtils.getValue(gdReq, "YS_STK_COL_GP", ii) ); 
				jrParam.setField("YS_STK_BED_NO"		, commUtils.getValue(gdReq, "YS_STK_BED_NO", ii) ); 

				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkbed", logId, methodNm, "Bed정보 수정");

				if("A".equals(commUtils.trim(gdReq.getParam("YD_BAY_GP")))) {
					msgId = "YSN6L001";
				} else if("B".equals(commUtils.trim(gdReq.getParam("YD_BAY_GP")))) {
					msgId = "YSN4L001";
				} else if("D".equals(commUtils.trim(gdReq.getParam("YD_BAY_GP")))) {
					msgId = "YSN5L001";
				} else if("E".equals(commUtils.trim(gdReq.getParam("YD_BAY_GP")))) {
					msgId = "YSN3L001";
				}
				
				jrParam.setField("YD_INFO_SYNC_CD", "4"          ); //야드정보동기화코드(Bed)
				jrParam.setField("YS_STK_COL_GP"  , commUtils.trim(gdReq.getHeader("YS_STK_COL_GP").getValue(ii)) ); //야드적치열구분
				jrParam.setField("YS_STK_BED_NO"  , commUtils.trim(gdReq.getHeader("YS_STK_BED_NO").getValue(ii)) ); //야드적치Bed번호

				//전송Data 조회
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2(msgId, jrParam));				
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
	 * 특수강 봉강정정 이송실적 처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord upbundleOutStlInfo(GridData gdReq) throws DAOException {
		String methodNm = "특수강 봉강정정 이송실적 처리[GdsYsJspSeEJB.upbundleOutStlInfo] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			String msgId = "";
			String sYsStrLoc = "";

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				//이송실적 처리 - M10YDLMJ1134 , 1회만 전송하면 됨 
				jrParam.setField("CARLD_ID"			, commUtils.getValue(gdReq, "CARLD_ID", ii) ); 
                jrParam.setField("TRN_FRTOMOVE_GP"    		, "22"    		); //운송이송구분
                jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1134", jrParam));
      			jrRtn.setField("RETN_CD", YsConstant.RETN_CD_SUCCESS);
      			jrRtn.setField("RETN_MSG", "정상처리 되었습니다!!");
			}
				
			//전송할 Data가 있으면 전송 처리
			if (jrRtn != null) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of upbundleOutStlInfo
	
	
	
	/**
	 * 특수강 봉강 사외임가공사 이송완료실적 처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updInlnOutStlInfo(GridData gdReq) throws DAOException {
		String methodNm = "특수강 봉강 사외임가공사 이송완료실적 처리[GdsYsJspSeEJB.updInlnOutStlInfo] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			String msgId = "";

			//Return Value
			JDTORecord jrRtn = null;
			
			//PIDEV
//			String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "K", "*");

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				//이송완료정보 수정 
				jrParam.setField("LD_PROG_STAT"		, commUtils.trim(gdReq.getParam("WORK_STAT"))); 
				jrParam.setField("CARLD_ID"			, commUtils.getValue(gdReq, "CARLD_ID", ii) );
				jrParam.setField("LOC_GBN"			, "H" ); 
				jrParam.setField("YS_STR_LOC"		, "" );
				//jrParam.setField("YS_STR_LOC"		, commUtils.trim(gdReq.getParam("YS_STR_LOC")));  //추후 입력받은 값으로 변경처리 필요.
				
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updInlnOutStlInfo", logId, methodNm, "이송완료 수정");
				
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updInlnOutStlInfo_01", logId, methodNm, "저장위치 수정");
				//쿼리 수정 필요.
				//이 부분이 현재는 GGM00101011(명륜임가공 이송위치), GGM10101011(명륜임가공 입고위치)로 정해짐.
				//GGM00101011(명륜임가공 이송위치), GGS00101011(삼일임가공 이송위치), GGM10101011(명륜임가공 입고위치), GGS10101011(삼일임가공 입고위치)로 세분화 필요.
				
				//
//				if("Y".equals(sApplyYnPI)) {
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSPDJ001", jrParam));  //PP로 임가공사 이송실적 보내줌.임가공사만!!!	
//				}
			}
//			if("Y".equals(sApplyYnPI)) {
				//전송할 Data가 있으면 전송 처리
				if (jrRtn != null) {
					
					jrRtn.setResultCode(logId);
					jrRtn.setResultMsg(methodNm);
	
					EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
					sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
				}
			
//			}	
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updInlnOutStlInfo
	
	
	
	
	/**
	 * 특수강 봉강 사외임가공사 입고실적 처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord sndInlnOutStlInfo(GridData gdReq) throws DAOException {
		String methodNm = "특수강 봉강 사외임가공사 입고실적 처리[GdsYsJspSeEJB.updInlnOutStlInfo] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			String msgId = "";
			String sYsStrLoc = "";

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				//이송완료정보 수정 
				jrParam.setField("LD_PROG_STAT"		, commUtils.trim(gdReq.getParam("WORK_STAT"))); 
				jrParam.setField("CARLD_ID"			, commUtils.getValue(gdReq, "CARLD_ID", ii) ); 
				jrParam.setField("LOC_GBN"			, "S" );
				
				sYsStrLoc = commUtils.getValue(gdReq, "YS_STR_LOC", ii);
				
				jrParam.setField("YS_STR_LOC"			,  sYsStrLoc);
				
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updInlnOutStlInfo", logId, methodNm, "입고실적 수정");
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updInlnOutStlInfo_02", logId, methodNm, "저장위치 수정");
				//이때 db 커밋찍는지 확인필요. 20230324 이재현 책임 문의.
				
				JDTORecordSet jsStlNo = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getInlnOutStlInfo", logId, methodNm, "재료번호로 조회");	
				//입고실적 전송  // 이전문 대상에 봉강정정입고도 포함되도록 변경.

				if("GGM10101011".equals(sYsStrLoc) || "GGB10101011".equals(sYsStrLoc))  //명륜 입고:GGM10101011, 봉강정정입고:GGB10101011
	            {												     
					if(jsStlNo.size() > 0) {
					
						for (int mm = 0; mm < jsStlNo.size(); mm++) {
							
							jrParam.setField("SSTL_NO"	, jsStlNo.getRecord(mm).getFieldString("MATL_NO")); 
							jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSDSJ001OUT", jrParam));	
						}
					}	
				}
				//명륜에서 회송되는 경우 통계로 회송실적 보내준다.
				else if("GGM20202022".equals(sYsStrLoc) ||	//대형공장
						"GGM30303033".equals(sYsStrLoc)||			//소형공장
						"GGM40404044".equals(sYsStrLoc)||			//정정공장
						"GGM50505055".equals(sYsStrLoc)			//스크랩야드
						){
					JDTORecord recordSp = null;
					int[] inParamIndex = {1,2,3,4};
					String ydWrkHdsDd = commUtils.getCurDate("yyyyMMdd");
					String fromLoc = "G081"; 
					String toLoc ="";

					for (int mm = 0; mm < jsStlNo.size(); mm++) 
					{
						//GGM10101011 명륜임가공 입고위치
						if( jsStlNo.getRecord(mm).getFieldString("YS_STR_LOC_HIS1").equals("GGM10101011"))
						{
							String sstlNo     =  jsStlNo.getRecord(mm).getFieldString("MATL_NO");
							switch(sstlNo.charAt(0)){
							case 'S':
								toLoc ="S220";
								break;
							case 'L':
							case 'A':
								toLoc ="S210";
								break;
							case 'H':
								toLoc ="S310";
								break;
							}
						    commUtils.printLog(logId, "sstlNo : "+sstlNo+", ydWrkHdsDd : "+ydWrkHdsDd+", fromLoc : "+fromLoc+", toLoc : "+toLoc, "SL");
							Object[] inParam = {sstlNo, fromLoc, toLoc, ydWrkHdsDd};	
							recordSp = commDao.callProcedure(inParam, inParamIndex, "com.inisteel.cim.ys.common.dao.YsCommDao.callProcedure_PIDEV");
						}
					}
				}
			}
				
			//전송할 Data가 있으면 전송 처리
			if (jrRtn != null) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of sndInlnOutStlInfo
	
	/**
	 * 저장위치 기준관리 - Lyr정보 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updStrLocPosSetLyr(GridData gdReq) throws DAOException {
		String methodNm = "저장위치 기준관리 - 열정보 변경[GdsYsJspSeEJB.updStrLocPosSetLyr] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			String msgId = "";
			
			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				//Lyr정보 수정
				jrParam.setField("YD_STK_LYR_ACT_STAT"		, commUtils.getValue(gdReq, "YD_STK_LYR_ACT_STAT", ii) ); 
				jrParam.setField("YD_STK_LYR_MTL_STAT"		, commUtils.getValue(gdReq, "YD_STK_LYR_MTL_STAT", ii) ); 
				jrParam.setField("YS_STK_COL_GP"			, commUtils.getValue(gdReq, "YS_STK_COL_GP", ii) ); 
				jrParam.setField("YS_STK_BED_NO"			, commUtils.getValue(gdReq, "YS_STK_BED_NO", ii) ); 
				jrParam.setField("YS_STK_LYR_NO"			, commUtils.getValue(gdReq, "YS_STK_LYR_NO", ii) ); 
				jrParam.setField("YS_STK_SEQ_NO"			, commUtils.getValue(gdReq, "YS_STK_SEQ_NO", ii) ); 

				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStklyr", logId, methodNm, "열정보 수정");
								
				if("A".equals(jrParam.getFieldString("YS_STK_COL_GP").substring(1,2))) {
					msgId = "YSN6L001";
				} else if("B".equals(jrParam.getFieldString("YS_STK_COL_GP".substring(1,2)))) {
					msgId = "YSN4L001";
				} else if("D".equals(jrParam.getFieldString("YS_STK_COL_GP").substring(1,2))) {
					msgId = "YSN5L001";
				} else if("E".equals(jrParam.getFieldString("YS_STK_COL_GP").substring(1,2))) {
					msgId = "YSN3L001";
				}
				
				jrParam.setField("YD_INFO_SYNC_CD", "4"          ); //야드정보동기화코드(Bed)
				jrParam.setField("YS_STK_COL_GP"  , commUtils.trim(gdReq.getHeader("YS_STK_COL_GP").getValue(ii))); //야드적치열구분
				//jrParam.setField("YD_STK_BED_NO"  , "01"         ); //야드적치Bed번호

				//전송Data 조회
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2(msgId, jrParam));				
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updStrLocPosSetLyr
	
	/**
	 * GridData -  차량상차정보 조회 - 차량상차정보
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecordSet getgdsCarldInfoInqjl(JDTORecord recPara) throws DAOException {
		String methodNm = "차량상차정보 조회 - 차량상차정보[GdsYsJspSeEJB.getgdsCarldInfoInqjl] < " + recPara.getResultMsg();
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
	
					jsTcar = commDao.select(recPara, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.getgdsCarldInfoInqjlByYdWrkBook", logId, methodNm, "작업예약 조회");

				}	
			} else {		
				
				// 차량 진행 상태 코드값이  그 이외인 경우 는 차량  이송재료 정보를 읽어온다.
				recPara.setField("YD_CAR_SCH_ID", commUtils.trim(jrCrn.getFieldString("YD_CAR_SCH_ID")));						
				
				jsTcar = commDao.select(recPara, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.getgdsCarldInfoInqjlByCarFtmvMtl", logId, methodNm, "차량이송 조회");

			}
			
			if (jsTcar == null || jsTcar.size() == 0) {
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
	 * 저장위치 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord updStrLocMod(GridData gdReq) throws DAOException {
		String methodNm = "저장위치 수정[GdsYsJspSeEJB.updStrLocMod] < " + gdReq.getNavigateValue();
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
			String szBdcCurrProgCd = null;
			String szOldYsStkColGp = null;
			String szOldYsStkBedNo = null;
			String szOldYsStkLyrNo = null;
			String szOldYsStkSeqNo = null;
			
			String szFromStlNo = null;
			String szFromYsStkColGp = null;
			String szFromYsStkBedNo = null;
			String szFromYsStkLyrNo = null;
			String szFromYsStkSeqNo = null;

			String szFromYsStkColGpSaved = null; 
			String szFromYsStkBedNoSaved = null;
			
			String szStkStlNo = null;
			String szWbookId = null;
			String szCrnSchId = null;
			String szToLocMtlStat = null;
			
			String szModGp = null; //작업구분

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			//PIDEV
//			String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "K", "*");
			
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
				
				if(szFromYsStkColGpSaved == null && !"".equals(szFromYsStkColGp)) {
					szFromYsStkColGpSaved = szFromYsStkColGp;
					szFromYsStkBedNoSaved = szFromYsStkBedNo;
				}
				
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
				
				//제품번호가 야드저장품에 존재하는지, 작업예약과 크레인스케줄에 잡혀있는 대상인지를 가져오고 번들공통에서 현재진도코드를 가져온다. 
				jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getIsEnableStlNo", logId, methodNm, "제품번호 작업대상여부 조회");
				
				if (jsTemp != null && jsTemp.size() > 0) {
					jrTemp = jsTemp.getRecord(0);

					szStkStlNo		= commUtils.trim(jrTemp.getFieldString("SSTL_NO"));
					szBdcCurrProgCd	= commUtils.trim(jrTemp.getFieldString("CURR_PROG_CD"));
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
						
					} else { // 재료가 없을 경우(최초 등록의 경우, 입고 이력 등록)
						
						// 재료 작업 이력 정보 조회
						jrParam.setField("SSTL_NO", szStlNo); 
						JDTORecordSet jsStlWrkHist = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStlWrkHist"
																, logId, methodNm, "재료 작업 이력 정보 조회");
//						RECEIPT_INI_DATE 컬럼 YS에서 수정 안하도록 변경 .. 23.01.16 신진희
//						if (jsStlWrkHist.size() == 0) { // 작업  이력 정보가 없을 경우, 입고일 추가 
//							jrParam.setField("SSTL_NO", szStlNo); 
//							commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkReceiptDate", logId, methodNm, "입고일 정보 수정");
//						}
						
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
				
				//번들공통 위치정보 수정하기
				if("DELETE".equals(szModGp)) {
					//삭제일경우 처리 ???
					jrParam.setField("FNL_REG_PGM"			, "gdsStrLocModjm" );
					jrParam.setField("YD_GP"				, "_" );
					jrParam.setField("YD_BAY_GP"			, szYsStkColGp.substring(1,2) );
					jrParam.setField("YD_EQP_GP"			, szYsStkColGp.substring(2,4) );
					jrParam.setField("YS_STK_COL_NO"		, szYsStkColGp.substring(4,6) );
					jrParam.setField("YS_STK_BED_NO"		, szYsStkBedNo );
					jrParam.setField("YS_STK_LYR_NO"		, szYsStkLyrNo );
					jrParam.setField("YS_STK_SEQ_NO"		, szYsStkSeqNo );
					jrParam.setField("YS_STR_LOC"			, "_" + szYsStkColGp.substring(1,6) + szYsStkBedNo + szYsStkLyrNo + szYsStkSeqNo );
					jrParam.setField("SSTL_NO"				, szOldStlNo ); //삭제된 번호
					
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBndlCommYsStrLoc", logId, methodNm, "BUNDLE공통 야드저장위치 수정");
				} else {
					//추가, 이동
					jrParam.setField("FNL_REG_PGM"			, "gdsStrLocModjm" );
					jrParam.setField("YD_GP"				, szYsStkColGp.substring(0,1) );
					jrParam.setField("YD_BAY_GP"			, szYsStkColGp.substring(1,2) );
					jrParam.setField("YD_EQP_GP"			, szYsStkColGp.substring(2,4) );
					jrParam.setField("YS_STK_COL_NO"		, szYsStkColGp.substring(4,6) );
					jrParam.setField("YS_STK_BED_NO"		, szYsStkBedNo );
					jrParam.setField("YS_STK_LYR_NO"		, szYsStkLyrNo );
					jrParam.setField("YS_STK_SEQ_NO"		, szYsStkSeqNo );
					jrParam.setField("YS_STR_LOC"			, szYsStkColGp + szYsStkBedNo + szYsStkLyrNo + szYsStkSeqNo );
					jrParam.setField("SSTL_NO"				, szStlNo );
					
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBndlCommYsStrLoc", logId, methodNm, "BUNDLE공통 야드저장위치 수정");
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
					
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStockYsStrLoc", logId, methodNm, "야드저장품 야드저장위치 수정");
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
				
				if("A".equals(szYsStkColGp.substring(1,2))) {
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN6L002", jrParam));
				} else if("B".equals(szYsStkColGp.substring(1,2))) {
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN4L002", jrParam));
				} else if("D".equals(szYsStkColGp.substring(1,2))) {
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN5L002", jrParam));
				} else if("E".equals(szYsStkColGp.substring(1,2))) {
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN3L002", jrParam));
				}
				
				//출하에 전문전송 이적실적 또는 입고 실적
				if("DELETE".equals(szModGp) ) {
					
				} else {
					
					if("H".equals(szBdcCurrProgCd)) {
						
						//PIDEV						
						//진도코드가 입고대기(H)인 경우 출하에 입고실적 전송
//						if("Y".equals(sApplyYnPI)) {
							jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1014", jrParam));
//						}  else {
//							jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSDSJ001", jrParam));
//						}
					} else {
						
						//그외는 출하에 이적실적 전송
//						if("Y".equals(sApplyYnPI)) {
							jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1034", jrParam));														
//						} else {
//							jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSDSJ002", jrParam));							
//						}
												
					}
				}
				
				/*
				 * 당진공장 내 특수강 이송실적 통계로 송신
				 * USRPDA.SP_SS_PD_MATL_FTMV_WR_MAIN('WBV041018','KD01','KDCS','20220711',:v1)
				 */
				
				String fromLoc = "";
				String toLoc   = "";
				JDTORecord recordSp = null;
				int[] inParamIndex = {1,2,3,4};
				String  currDt      = commUtils.getDateTime14(); //현재일시(yyyyMMddHHmmss)
				String	iniDate     = commUtils.getIniDate(currDt); 
				
				
				if("ADD".equals(szModGp)) {

					commUtils.printLog(logId, "szStlNo : "+szStlNo, "입고SL");
					
					if("H".equals(szYsStkColGp.substring(1,2))){
						fromLoc = "S220";
						toLoc   = "F546";		
					}
					else if("KC81".equals(szYsStkColGp.substring(0,4)) || "KC83".equals(szYsStkColGp.substring(0,4))){
						fromLoc = "S210";
						if("KC81".equals(szYsStkColGp.substring(0,4)))
							toLoc   = "G341";		
						else 
							toLoc   = "G342";		
					}
					Object[] inParam = {szStlNo, fromLoc, toLoc, iniDate};	
					recordSp = commDao.callProcedure(inParam, inParamIndex, "com.inisteel.cim.ys.common.dao.YsCommDao.callProcedure_PIDEV");
					
					commUtils.printLog(logId, "sstlNo : "+szStlNo, "입고SL");
					commUtils.printLog(logId, "ydWrkHdsDd : "+iniDate, "입고SL");
					commUtils.printLog(logId, "fromLoc : "+fromLoc, "입고SL");
					commUtils.printLog(logId, "toLoc : "+toLoc, "입고SL");			
								
				}
				
				
				//FROM 위치 완산BED 일 경우 해제여부 결정
				
				
				
			}
			
			//특수강 제품창고 B동인 경우 저장위치 수정시 적치Bed 활성상태를 변경한다.
			// 1) FROM 위치가 NOT NULL 이고 B동일 경우 FROM 위치 BED의 적치된 제품이 없으면 Bed 활성상태를 초기화 한다.
			if(szFromYsStkColGpSaved != null) {
				if(!"".equals(szFromYsStkColGpSaved)) {
					if("B".equals(szFromYsStkColGpSaved.substring(1,2))) {
						
						jrParam.setField("YS_STK_COL_GP"		, szFromYsStkColGpSaved );
						jrParam.setField("YS_STK_BED_NO"		, szFromYsStkBedNoSaved );
						
						commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updStkBedActStatReSetByLoc", logId, methodNm, "적치Bed 활성상태 초기화2");
					}
				}
			}
			
			// 2) TO 위치의 최 하단 제품의 길이 구분에 따라 Bed 활성상태를 변경한다.
			if(!"".equals(szYsStkColGp)) {
				if("B".equals(szYsStkColGp.substring(1,2))) {
					
					jrParam.setField("YS_STK_COL_GP"		, szYsStkColGp );
					jrParam.setField("YS_STK_BED_NO"		, szYsStkBedNo );
					
					//to 위치에 적치된 제품수가 0이면 활성상태 초기화
					int iCnt = commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updStkBedActStatReSetByLoc", logId, methodNm, "적치Bed 활성상태 초기화2");
					
					//활성상태 초기화 건수가 0 이면 to 위치에 적치된 제품있다는 것으로 to위치 최 하단 제품 길이 구분에 따라 Bed 활성상태를 변경한다.
					if(iCnt == 0) {
						commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updStkBedActStatByLoc", logId, methodNm, "적치Bed 활성상태 변경2");
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
	} // end of updStrLocMod

	/**
	 * 저장위치 수정(재공야드)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord updInlnLocMod(GridData gdReq) throws DAOException {
		String methodNm = "재공야드 저장위치 수정[GdsYsJspSeEJB.updInlnLocMod] < " + gdReq.getNavigateValue();
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
			String szModGp = null; //작업구분
			String sUserId = "";
			String sCheck  = "";

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {

				sCheck 				= commUtils.getValue(gdReq, "CHECK", ii);
				szStlNo 			= commUtils.getValue(gdReq, "SSTL_NO", ii);
				szYsStkColGp 		= commUtils.getValue(gdReq, "YS_STK_COL_GP", ii);
				szYsStkBedNo 		= commUtils.getValue(gdReq, "YS_STK_BED_NO", ii);
				szYsStkLyrNo 		= commUtils.getValue(gdReq, "YS_STK_LYR_NO", ii);
				szYsStkSeqNo 		= commUtils.getValue(gdReq, "YS_STK_SEQ_NO", ii);
				szOldStlNo 			= commUtils.getValue(gdReq, "OLD_SSTL_NO", ii);
				sUserId             = commUtils.trim(gdReq.getParam("userid"));	//수정자
								
				boolean Checked = (Integer.parseInt(sCheck) == 1)? true:false;
				
				if(Checked){

					if("".equals(szStlNo) && !"".equals(szOldStlNo) ) {
						//삭제처리
						szModGp = "DELETE";
						szStkStlNo = szOldStlNo;
					}else{
						szModGp = "ADD";
						szStkStlNo = szStlNo;
					}
					
					commUtils.printLog(logId, "================="				, "SL");
					commUtils.printLog(logId, "szModGp	   ====="+szModGp		, "SL");
					commUtils.printLog(logId, "sCheck	   ====="+sCheck		, "SL");
					commUtils.printLog(logId, "szStlNo	   ====="+szStkStlNo	, "SL");
					commUtils.printLog(logId, "szYsStkColGp====="+szYsStkColGp	, "SL");
					commUtils.printLog(logId, "szYsStkBedNo====="+szYsStkBedNo	, "SL");
					commUtils.printLog(logId, "szYsStkLyrNo====="+szYsStkLyrNo	, "SL");
					commUtils.printLog(logId, "szYsStkSeqNo====="+szYsStkSeqNo	, "SL");
					commUtils.printLog(logId, "sUserId	   ====="+sUserId		, "SL");
					
					if(!"".equals(szStkStlNo)){
						
						if(szStkStlNo.startsWith("A")){
							//번들공통 위치정보 수정하기
							if("DELETE".equals(szModGp)) {
								//삭제일경우 처리 ???
								jrParam.setField("FNL_REG_PGM"			, "updInlnLocMod" );
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
								jrParam.setField("FNL_REG_PGM"			, "updInlnLocMod" );
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
						}else{
							//번들공통 위치정보 수정하기
							if("DELETE".equals(szModGp)) {
								//삭제일경우 처리 ???
						        jrParam.setField("FNL_REG_PGM"			, "updInlnLocMod" );
								jrParam.setField("YD_GP"				, "_" );
								jrParam.setField("YD_BAY_GP"			, szYsStkColGp.substring(1,2) );
								jrParam.setField("YD_EQP_GP"			, szYsStkColGp.substring(2,4) );
								jrParam.setField("YS_STK_COL_NO"		, szYsStkColGp.substring(4,6) );
								jrParam.setField("YS_STK_BED_NO"		, szYsStkBedNo );
								jrParam.setField("YS_STK_LYR_NO"		, szYsStkLyrNo );
								jrParam.setField("YS_STK_SEQ_NO"		, szYsStkSeqNo );
								jrParam.setField("YS_STR_LOC"			, "_" + szYsStkColGp.substring(1,6) + szYsStkBedNo + szYsStkLyrNo + szYsStkSeqNo );
								jrParam.setField("SSTL_NO"				, szOldStlNo ); //삭제된 번호
								
								commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBndlCommYsStrLoc", logId, methodNm, "BUNDLE공통 야드저장위치 수정");
							} else {
								//추가, 이동
								jrParam.setField("FNL_REG_PGM"			, "updInlnLocMod" );
								jrParam.setField("YD_GP"				, szYsStkColGp.substring(0,1) );
								jrParam.setField("YD_BAY_GP"			, szYsStkColGp.substring(1,2) );
								jrParam.setField("YD_EQP_GP"			, szYsStkColGp.substring(2,4) );
								jrParam.setField("YS_STK_COL_NO"		, szYsStkColGp.substring(4,6) );
								jrParam.setField("YS_STK_BED_NO"		, szYsStkBedNo );
								jrParam.setField("YS_STK_LYR_NO"		, szYsStkLyrNo );
								jrParam.setField("YS_STK_SEQ_NO"		, szYsStkSeqNo );
								jrParam.setField("YS_STR_LOC"			, szYsStkColGp + szYsStkBedNo + szYsStkLyrNo + szYsStkSeqNo );
								jrParam.setField("SSTL_NO"				, szStlNo );
								
								commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBndlCommYsStrLoc", logId, methodNm, "BUNDLE공통 야드저장위치 수정");
							}
						}
						if(szYsStkColGp.substring(1,3).equals("HA")){
							if("DELETE".equals(szModGp)) {
								
								jrParam.setField("YS_STK_COL_GP"		, "_" + szYsStkColGp.substring(1,6) );
								jrParam.setField("YS_STK_BED_NO"		, szYsStkBedNo );
								jrParam.setField("YS_STK_LYR_NO"		, szYsStkLyrNo );
								jrParam.setField("YS_STK_SEQ_NO"		, szYsStkSeqNo );
								jrParam.setField("YS_STR_LOC"			, "_" + szYsStkColGp.substring(1,6) + szYsStkBedNo + szYsStkLyrNo + szYsStkSeqNo );
								jrParam.setField("SSTL_NO"				, szOldStlNo ); //삭제된 번호
								
								commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStockYsStrLoc", logId, methodNm, "야드저장품 야드저장위치 수정");
							}
							else{	//ADD 인 경우  
								
								jrParam.setField("YS_STK_COL_GP"		, szYsStkColGp );
								jrParam.setField("YS_STK_BED_NO"		, szYsStkBedNo );
								jrParam.setField("YS_STK_LYR_NO"		, szYsStkLyrNo );
								jrParam.setField("YS_STK_SEQ_NO"		, szYsStkSeqNo );
								jrParam.setField("YS_STR_LOC"			, szYsStkColGp + szYsStkBedNo + szYsStkLyrNo + szYsStkSeqNo );
								jrParam.setField("SSTL_NO"				, szStlNo );
								
								commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStockYsStrLoc", logId, methodNm, "야드저장품 야드저장위치 수정");
							}
							
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
						else{
							//야드 저장위치이력 등록 추가 2018.05.15     
							jrParam.setField("YS_STK_SEQ_NO1"   , szYsStkSeqNo  ); //특수강야드적치Seq번호1
							jrParam.setField("SSTL_NO1"    		, szStlNo    	); //특수강재료번호1 
							jrParam.setField("MODIFIER"         , sUserId       ); //수정자  
							jrParam.setField("DEL_YN"			, "N"		);
								
							commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insStrLocHist", logId, methodNm, "저장위치이력 등록");
							
							if(szStkStlNo.startsWith("A")){
								//L2로 재원정보 전문 전송
								jrParam.setField("YD_INFO_SYNC_CD", "6_1"); //야드정보동기화코드 6_1:빌렛 지정저장품
							}else{
								//L2로 재원정보 전문 전송
								jrParam.setField("YD_INFO_SYNC_CD", "6_2"); //야드정보동기화코드 6_2:번들 지정저장품
							}
							
							jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN4L002", jrParam));
						}
					}
				}
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
	} // end of updInlnLocMod
	
	/**
	 *      [A] 오퍼레이션명 : 차량상차정보 조회 - 차상위치 수정
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updgdsCarldInfoInqjl(GridData gdReq) throws DAOException {
		String methodNm = "차량상차정보 조회 - 차상위치 수정[GdsYsJspFaEJB.updgdsCarldInfoInqjl] < " + gdReq.getNavigateValue();
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
				
				//차량재료정보 수정 
				commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updgdsWhsPlnInfojlByCarFtmvMtl", logId, methodNm, "TB_YS_CARFTMVMTL");

				//기존위치 CLEAR
				commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updgdsWhsPlnInfojlByStkLyr", logId, methodNm, "TB_YS_STKLYR");

				//차량위치 등록
				commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updgdsWhsPlnInfojlByCarStkLyr", logId, methodNm, "TB_YS_STKLYR");

				//차량재료정보 수정 
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
	 *      [A] 오퍼레이션명 :선재 제품단위 이적지시 -크레인작업SCH등록
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updgdsWrMoveWojm(GridData gdReq) throws DAOException {
		String methodNm = "선재 제품단위 이적지시[GdsYsJspSeEJB.updgdsWrMoveWojm] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//Return Value
			JDTORecord jrRtn = null;
			JDTORecordSet jsMsg = JDTORecordFactory.getInstance().createRecordSet("");	//크레인스케줄 정리를 위함

			String stlNos        = commUtils.trim(gdReq.getParam("SSTL_NOS"        )); //재료번호들
			String ysStkColGp    = commUtils.trim(gdReq.getParam("YS_STK_COL_GP"   )); //FROM 위치
			String ydToLocGuide1 = commUtils.trim(gdReq.getParam("YD_TO_LOC_GUIDE" )); //FR위치__야드To위치Guide
			String ydToLocGuide2 = commUtils.trim(gdReq.getParam("YD_TO_LOC_GUIDE" )); //TO위치__야드To위치Guide
			String modifier      = commUtils.trim(gdReq.getParam("userid"));

			if (ysStkColGp.length() > 6) {
				ysStkColGp = ysStkColGp.substring(0, 6);
			}
			
			//To위치가 동까지만 있으면 위치검색Bed 기준 적용

			if ("".equals(stlNos)) {
				throw new Exception("이적 재료번호가 없습니다.");
			} else if ("".equals(ysStkColGp) || ysStkColGp.length() < 4) {
				throw new Exception("Span[" + ysStkColGp + "] 정보가 없습니다.");
			} else if ("".equals(ydToLocGuide1) || ydToLocGuide1.length() < 6) {
				throw new Exception("TO 위치정보가 없습니다.");
			} 
			
			/**********************************************************
			* 1. 스케줄코드 설정
			**********************************************************/
			String ydSchCd    = ""; //야드스케쥴코드
			String toSchCd    = ""; //동간이적 to위치스케쥴코드
			
			String ydBayGp    = ysStkColGp.substring(1, 2); //야드동구분
			String ydAimBayGp = ""; //야드목표동구분

			if ("".equals(ydToLocGuide1)) {
				ydAimBayGp = ysStkColGp.substring(1, 2);  	//위치검색Bed기준 적용
			} else {
				ydAimBayGp = ydToLocGuide1.substring(1, 2);  	//To위치지정
			}

			//스케쥴코드 생성
//			String ydEqpGp = ysStkColGp.substring(2, 4); //야드설비구분(이적 스케줄코드 생성용)
			
			//To위치Guide가 있으면 그 값으로 스케줄코드 생성
//			if ("".equals(ydToLocGuide1)) {
//				ydEqpGp = ysStkColGp.substring(2, 4);
//			}

			if(ydBayGp.equals("D")) {
				String toCol =  ""; 
				String ysCol =  ""; 
				commUtils.printLog(logId, "ysStkColGp : " +  ysStkColGp, "SL");
				commUtils.printLog(logId, "ydToLocGuide1 : " +  ydToLocGuide1, "SL");
				if(ysStkColGp.equals("KD0101") && (ydToLocGuide1.substring(0, 6).equals("KD0101") || ydToLocGuide1.substring(0, 6).equals("KD0102"))){
					ydSchCd = "KDYD01MM";
				} else if(ysStkColGp.equals("KD0102") && (ydToLocGuide1.substring(0, 6).equals("KD0101") || ydToLocGuide1.substring(0, 6).equals("KD0102"))){	
					ydSchCd = "KDYD01MM";
				} else if(ysStkColGp.equals("KD0103") && (ydToLocGuide1.substring(0, 6).equals("KD0103") || ydToLocGuide1.substring(0, 6).equals("KD0104"))){	
					ydSchCd = "KDYD02MM";
				} else if(ysStkColGp.equals("KD0104") && (ydToLocGuide1.substring(0, 6).equals("KD0103") || ydToLocGuide1.substring(0, 6).equals("KD0104"))){	
					ydSchCd = "KDYD02MM";
				} else if(ysStkColGp.equals("KD0105") && (ydToLocGuide1.substring(0, 6).equals("KD0105") || ydToLocGuide1.substring(0, 6).equals("KD0106"))){	
					ydSchCd = "KDYD03MM";
				} else if(ysStkColGp.equals("KD0106") && (ydToLocGuide1.substring(0, 6).equals("KD0105") || ydToLocGuide1.substring(0, 6).equals("KD0106"))){	
					ydSchCd = "KDYD03MM";
				} else if(ysStkColGp.equals("KD0107") && (ydToLocGuide1.substring(0, 6).equals("KD0107") || ydToLocGuide1.substring(0, 6).equals("KD0108"))){	
					ydSchCd = "KDYD04MM";
				} else if(ysStkColGp.equals("KD0108") && (ydToLocGuide1.substring(0, 6).equals("KD0107") || ydToLocGuide1.substring(0, 6).equals("KD0108"))){	
					ydSchCd = "KDYD04MM";
				} else if(ysStkColGp.equals("KD0109") && (ydToLocGuide1.substring(0, 6).equals("KD0109") || ydToLocGuide1.substring(0, 6).equals("KD0109"))){	
					ydSchCd = "KDYD05MM";
				} else if(ysStkColGp.equals("KD0110") && (ydToLocGuide1.substring(0, 6).equals("KD0110") || ydToLocGuide1.substring(0, 6).equals("KD0111"))){	
					ydSchCd = "KDYD06MM";
				} else if(ysStkColGp.equals("KD0111") && (ydToLocGuide1.substring(0, 6).equals("KD0110") || ydToLocGuide1.substring(0, 6).equals("KD0111"))){	
					ydSchCd = "KDYD06MM";
				} else {
					//동간이적 
					// HS -> CELL
					ysCol = ysStkColGp.substring(4, 6);
					
					if( ysCol.equals("01")|| ysCol.equals("02") ){
						toCol =  "01"; 
					} else if( ysCol.equals("03")|| ysCol.equals("04") ){
						toCol =  "02"; 
					} else if( ysCol.equals("05")|| ysCol.equals("06") ){
						toCol =  "03"; 
					} else if( ysCol.equals("07")|| ysCol.equals("08") ){
						toCol =  "04"; 
					} else if( ysCol.equals("09") ){
						toCol =  "05"; 
					} else if( ysCol.equals("10")|| ysCol.equals("11") ){
						toCol =  "06"; 
					}
					
					ydSchCd = "KDYD" + toCol + "DM";
                   
					
					
					
					// HS -> CELL
					ysCol = ydToLocGuide2.substring(4, 6);
					
					if( ysCol.equals("01")|| ysCol.equals("02") ){
						toCol =  "01"; //'KDSC01'
					} else if( ysCol.equals("03")|| ysCol.equals("04") ){
						toCol =  "02"; //'KDSC01'
					} else if( ysCol.equals("05")|| ysCol.equals("06") ){
						toCol =  "03"; //'KDSC01'
					} else if( ysCol.equals("07")|| ysCol.equals("08") ){
						toCol =  "04"; //'KDSC01'
					} else if( ysCol.equals("09") ){
						toCol =  "05"; //'KDSC01'
					} else if( ysCol.equals("10")|| ysCol.equals("11") ){
						toCol =  "06"; //'KDSC01'
					}
					
					toSchCd = "KDYD"+toCol+"DM";  //stacker crane 용 스케줄코드 (ex:KDYD01DM)
				}
			} else {	
				ydSchCd = "KEYD01MM";
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
			
			commUtils.printLog(logId, "ydSchCd : " +  ydSchCd, "SL");
			
			/**********************************************************
			* 2. 이적 작업예약 등록
			**********************************************************/
			jrParam.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
			jrParam.setField("YD_TO_LOC_GUIDE" , ydToLocGuide1 ); //야드To위치Guide
			
			/**********************************************************
			* 1. 야드스케쥴코드 Check
			**********************************************************/
			JDTORecord jrSchCd = gdsYsComm.chkSchCdEqp(jrParam);
			
			String ydGp       = ydSchCd.substring(0, 1);	//야드구분
			String ydEqpId    = commUtils.trim(jrSchCd.getFieldString("YD_EQP_ID"   ));	//야드설비ID(크레인)
			String ydSchPrior = commUtils.trim(jrSchCd.getFieldString("YD_SCH_PRIOR"));	//야드스케쥴우선순위
			String ydToLocDcsnMtd = "S"; //야드TO위치결정방법(스케줄지정)

			if ("".equals(ydAimBayGp)) {
				ydAimBayGp = ydBayGp;
			}

			if (!"".equals(ydToLocGuide1)) {
				ydToLocDcsnMtd = "F";	//야드TO위치결정방법(지정위치)
			}
			
			/**********************************************************
			* 2. 크레인사양 분리
			**********************************************************/
			Vector vcLot = this.setCrnSpecSpr(jrSchCd, jsWbMtl);

			JDTORecordSet jsLotMtl = null;
			int lotCnt   = vcLot.size();	//크레인사양 분리 작업예약수
			int lotMtlSh = 0;				//작업예약재료매수
			String ydWbookId = "";			//야드작업예약ID
			String ydWbookIdFst = "";		//야드작업예약ID(첫번째)
			
			JDTORecord jrRow = null;

			JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, modifier);
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
				jrParam.setField("YD_TO_LOC_GUIDE"   , ydToLocGuide1 ); //야드To위치Guide
			
				commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBook", logId, methodNm, "TB_YS_WRKBOOK");

				//작업예약재료 등록
				
				for (int jj = 0; jj < lotMtlSh; jj++) {

					jrRow = jsLotMtl.getRecord(jj);
					
					JDTORecord jrRtn1 = JDTORecordFactory.getInstance().create();
					
					jrRtn1.setField("YD_WBOOK_ID"   , ydWbookId     ); //야드작업예약ID
					jrRtn1.setField("SSTL_NO"       , commUtils.trim(jrRow.getFieldString("SSTL_NO"       )));	//재료번호
					jrRtn1.setField("YS_STK_COL_GP" , commUtils.trim(jrRow.getFieldString("YS_STK_COL_GP")));	//야드적치열구분
					jrRtn1.setField("YS_STK_BED_NO" , commUtils.trim(jrRow.getFieldString("YS_STK_BED_NO")));	//야드적치Bed번호
					jrRtn1.setField("YS_STK_LYR_NO" , commUtils.trim(jrRow.getFieldString("YS_STK_LYR_NO")));	//야드적치단번호
					jrRtn1.setField("YS_STK_SEQ_NO" , commUtils.trim(jrRow.getFieldString("YS_STK_SEQ_NO")));	//야드적치SEQ번호
					jrRtn1.setField("MODIFIER"     	, modifier      );												//등록자
					commDao.insert(jrRtn1, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBookMtl", logId, methodNm, "TB_YS_WRKBOOKMTL");
				}
				

				//크레인스케줄 전문 - Log ID, Method, 수정자 Set
				
				if(ydEqpId.substring(2, 4).equals("SC")) {
					jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setField("YD_WBOOK_ID"     , ydWbookId ); //야드작업예약ID(첫번째꺼만)
					jrYdMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
					jrYdMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
					jrYdMsg.setField("YD_SCH_ST_GP"    , "M"          ); //야드스케쥴기동구분(Manual)
					jrYdMsg.setField("YD_SCH_REQ_GP"   , "M"          ); //야드스케쥴요청구분(이적)
					jsMsg.addRecord(jrYdMsg);
				}	
			}

			if(!ydEqpId.substring(2, 4).equals("SC")) {
				jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setField("YD_WBOOK_ID"     , ydWbookIdFst ); //야드작업예약ID(첫번째꺼만)
				jrYdMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
				jrYdMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
				jrYdMsg.setField("YD_SCH_ST_GP"    , "M"          ); //야드스케쥴기동구분(Manual)
				jrYdMsg.setField("YD_SCH_REQ_GP"   , "M"          ); //야드스케쥴요청구분(이적)
				jsMsg.addRecord(jrYdMsg);

			}
			/**********************************************************
			* 3.  크레인별 스케줄 전송
			**********************************************************/
			
			jrRtn = commUtils.addSndData(jrRtn, this.setCrnSchMsg(jsMsg, logId, methodNm));
			
			if(!toSchCd.equals("")) {
				commUtils.printLog(logId, "toSchCd : " +  toSchCd, "SL");
				commUtils.printLog(logId, "ydToLocGuideD : " +  ydToLocGuide2, "SL");

				/**********************************************************
				* 2. 동간이적 to 작업예약 등록
				**********************************************************/
				jrParam.setField("YD_SCH_CD"       , toSchCd      ); //야드스케쥴코드
				jrParam.setField("YD_TO_LOC_GUIDE" , ydToLocGuide2 ); //야드To위치Guide
				
				/**********************************************************
				* 1. 야드스케쥴코드 Check
				**********************************************************/
				jrSchCd = JDTORecordFactory.getInstance().create();
				jrSchCd = gdsYsComm.chkSchCdEqp(jrParam);
				
				ydGp       = ydSchCd.substring(0, 1);	//야드구분
				ydEqpId    = commUtils.trim(jrSchCd.getFieldString("YD_EQP_ID"   ));	//야드설비ID(크레인)
				ydSchPrior = commUtils.trim(jrSchCd.getFieldString("YD_SCH_PRIOR"));	//야드스케쥴우선순위
				ydToLocDcsnMtd = "S"; //야드TO위치결정방법(스케줄지정)
	
				if ("".equals(ydAimBayGp)) {
					ydAimBayGp = ydBayGp;
				}
	
				if (!"".equals(ydToLocGuide2)) {
					ydToLocDcsnMtd = "F";	//야드TO위치결정방법(지정위치)
				}
				
				/**********************************************************
				* 2. 크레인사양 분리
				**********************************************************/
				vcLot = this.setCrnSpecSpr(jrSchCd, jsWbMtl);
	
				lotCnt   = vcLot.size();	//크레인사양 분리 작업예약수
				lotMtlSh = 0;				//작업예약재료매수
				ydWbookId = "";			//야드작업예약ID
				
				jrRow = JDTORecordFactory.getInstance().create();
				jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg = commUtils.getParam(logId, methodNm, modifier);
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
					jrParam.setField("YD_SCH_CD"         , toSchCd       ); //야드스케쥴코드
					jrParam.setField("YD_SCH_PRIOR"      , ydSchPrior    ); //야드스케쥴우선순위
					jrParam.setField("YD_SCH_PROG_STAT"  , "W"           ); //야드스케쥴진행상태(스케줄수행대기)
					jrParam.setField("YD_SCH_ST_GP"      , "M"           ); //야드스케쥴기동구분(Manual)
					jrParam.setField("YD_SCH_REQ_GP"     , "M"           ); //야드스케쥴요청구분(이적)
					jrParam.setField("YD_AIM_YD_GP"      , ydGp          ); //야드목표야드구분
					jrParam.setField("YD_AIM_BAY_GP"     , ydAimBayGp    ); //야드목표동구분
					jrParam.setField("YD_TO_LOC_DCSN_MTD", ydToLocDcsnMtd); //야드TO위치결정방법
					jrParam.setField("YD_TO_LOC_GUIDE"   , ydToLocGuide2  ); //야드To위치Guide
				
					commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBook", logId, methodNm, "TB_YS_WRKBOOK");
	
					//작업예약재료 등록
					
					for (int jj = 0; jj < lotMtlSh; jj++) {
	
						jrRow = jsLotMtl.getRecord(jj);
						
						JDTORecord jrRtn1 = JDTORecordFactory.getInstance().create();
						
						jrRtn1.setField("YD_WBOOK_ID"   , ydWbookId     ); //야드작업예약ID
						jrRtn1.setField("SSTL_NO"       , commUtils.trim(jrRow.getFieldString("SSTL_NO"       )));	//재료번호
						jrRtn1.setField("YS_STK_COL_GP" , commUtils.trim(jrRow.getFieldString("YS_STK_COL_GP")));	//야드적치열구분
						jrRtn1.setField("YS_STK_BED_NO" , commUtils.trim(jrRow.getFieldString("YS_STK_BED_NO")));	//야드적치Bed번호
						jrRtn1.setField("YS_STK_LYR_NO" , commUtils.trim(jrRow.getFieldString("YS_STK_LYR_NO")));	//야드적치단번호
						jrRtn1.setField("YS_STK_SEQ_NO" , commUtils.trim(jrRow.getFieldString("YS_STK_SEQ_NO")));	//야드적치SEQ번호
						jrRtn1.setField("MODIFIER"     	, modifier      );												//등록자
						commDao.insert(jrRtn1, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBookMtl", logId, methodNm, "TB_YS_WRKBOOKMTL");
					}
					
//SJH	
//					//크레인스케줄 전문 - Log ID, Method, 수정자 Set
//					
//					if(ydEqpId.substring(2, 4).equals("SC")) {
//						jrYdMsg = JDTORecordFactory.getInstance().create();
//						jrYdMsg.setField("YD_WBOOK_ID"     , ydWbookId ); //야드작업예약ID(첫번째꺼만)
//						jrYdMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
//						jrYdMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
//						jrYdMsg.setField("YD_SCH_ST_GP"    , "M"          ); //야드스케쥴기동구분(Manual)
//						jrYdMsg.setField("YD_SCH_REQ_GP"   , "M"          ); //야드스케쥴요청구분(이적)
//						jsMsg.addRecord(jrYdMsg);
//					}	
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
	 *      [A] 오퍼레이션명 :반납/반송  관리 -크레인작업SCH등록
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updgdsRetnjm(GridData gdReq) throws DAOException {
		String methodNm = "반납/반송  관리 -크레인작업SCH등록[GdsYsJspSeEJB.updgdsRetnjm] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//Return Value
			JDTORecord jrRtn = null;
			JDTORecord sndRecord = JDTORecordFactory.getInstance().create();;
	
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			String ysStkColGp = "";
			String ysStkBedNo = "";
			String ysStklyrNo = "";
			String stlNos     = "";
			String stlNo      = "";
			String ydBayGp    = "";
			String ydSchCd    = "";
			String ydToLocGuide	="";
			String FirstysStkColGp = "";
			String ydPc   	  = "";
			String ToBayGp    = "" ;
			
			for (int ii = 0; ii < rowCnt; ii++) {
				ysStkColGp  =  commUtils.getValue(gdReq, "YS_STK_COL_GP", ii);
				ysStkBedNo  =  commUtils.getValue(gdReq, "YS_STK_BED_NO", ii);
				ysStklyrNo  =  commUtils.getValue(gdReq, "YS_STK_LYR_NO", ii);
				stlNo    	=  commUtils.getValue(gdReq, "SSTL_NO", ii); 
				ydPc    	=  commUtils.getValue(gdReq, "YD_FTMV_MEANS_GP", ii); // 입고PC
				ydBayGp    	=  ysStkColGp.substring(1,2);
				if((ydPc.equals("1"))||(ydPc.equals("2"))) {
					ToBayGp = "A";
				}else if(ydPc.equals("3")) {
					ToBayGp = "B";
				}else if((ydPc.equals(""))&&( ydBayGp.equals("D"))) {
					ToBayGp = "D";
				}else if((ydPc.equals(""))&&( ydBayGp.equals("E"))) {
					ToBayGp = "E";
				}
						
				
				if(!FirstysStkColGp.equals(ysStkColGp)) {
					
					if(ii > 0) {
						commUtils.printLog(logId, "ii > 0:" + ii, "SL");
						sndRecord = this.insMakeRetnWrkBook(logId, methodNm, jrParam  );
						/**********************************************************
						* 3.  크레인별 스케줄 전송
						**********************************************************/
						jrRtn = commUtils.addSndData(jrRtn, sndRecord);
					}	
					if(ydBayGp.equals("A")) {
						if((ysStkColGp.substring(0, 6).equals("KA0101") || ysStkColGp.substring(0, 6).equals("KA0102"))){
							ydSchCd = "KA01HGBM";
							ydToLocGuide = "KAHS01";
						} else if((ysStkColGp.substring(0, 6).equals("KA0103") || ysStkColGp.substring(0, 6).equals("KA0104"))){	
							ydSchCd = "KA02HGBM";
							ydToLocGuide = "KAHS02";
						} else if((ysStkColGp.substring(0, 6).equals("KA0105") || ysStkColGp.substring(0, 6).equals("KA0106"))){	
							ydSchCd = "KA03HGBM";
							ydToLocGuide = "KAHS03";
						} else if((ysStkColGp.substring(0, 6).equals("KA0107") || ysStkColGp.substring(0, 6).equals("KA0108"))){	
							ydSchCd = "KA04HGBM";
							ydToLocGuide = "KAHS04";
						} else if((ysStkColGp.substring(0, 6).equals("KA0109") || ysStkColGp.substring(0, 6).equals("KA0110"))){	
							ydSchCd = "KA05HGBM";
							ydToLocGuide = "KAHS05";
						} else if((ysStkColGp.substring(0, 6).equals("KA0111") || ysStkColGp.substring(0, 6).equals("KA0112"))){	
							ydSchCd = "KA06HGBM";
							ydToLocGuide = "KAHS06";
						} else if((ysStkColGp.substring(0, 6).equals("KA0113") || ysStkColGp.substring(0, 6).equals("KA0114"))){	
							ydSchCd = "KA07HGBM";
							ydToLocGuide = "KAHS07";
						}
						
						//ydToLocGuide = "K"+ ToBayGp + "TY01";
					} else if(ydBayGp.equals("B")) {
						ydSchCd = "KBYD01BM";
						ydToLocGuide = "K"+ ToBayGp + "TY";
					} else if(ydBayGp.equals("D")) {
						ydSchCd = "";
					} else if(ydBayGp.equals("E")) {
						ydSchCd = "KEYD01BM";
						ydToLocGuide = "KETY";
						
					}
					
					// 현재위치가 A동이고 입고PC가 3인 경우 대차 작업
					// 현재위치가 B동이고 입고PC가 1인 경우 대차 작업
					if(ydBayGp.equals("A") && ydPc.equals("3")){
						jrParam.setField("YD_WRK_PLAN_TCAR"		, "KXTC01" ); 
					} else if((ydBayGp.equals("B") && ydPc.equals("1"))||ydBayGp.equals("B") && ydPc.equals("2")){ 
						jrParam.setField("YD_WRK_PLAN_TCAR"		, "KXTC01" ); 
					} else {
						jrParam.setField("YD_WRK_PLAN_TCAR"		, "" ); 
					}
					
					// 차량반납은 별도로 처리함
					commUtils.printLog(logId, "ydSchCd:"+ ydSchCd, "SL");
					
					if(ydSchCd.equals("")) {
						continue;
					}
					stlNos = stlNo;
					FirstysStkColGp = ysStkColGp;

					jrParam.setField("YS_STK_COL_GP"		, ysStkColGp ); 
					jrParam.setField("YS_STK_BED_NO"		, ysStkBedNo ); 
					jrParam.setField("YS_STK_LYR_NO"		, ysStklyrNo ); 
					jrParam.setField("YD_TO_BAY_GP"			, ToBayGp ); 
					jrParam.setField("SSTL_NOS"				, stlNo );
					jrParam.setField("YD_SCH_CD"			, ydSchCd ); 
					jrParam.setField("YD_TO_LOC_GUIDE"		, ydToLocGuide ); 
					jrParam.setField("USERID"				, commUtils.trim(gdReq.getParam("userid")) ); 

				} else {
					stlNos = stlNos +  "," + stlNo ;
					jrParam.setField("SSTL_NOS"				, stlNos );
				}
			}
			
			sndRecord = this.insMakeRetnWrkBook(logId, methodNm, jrParam  );
			/**********************************************************
			* 3.  크레인별 스케줄 전송
			**********************************************************/
			jrRtn = commUtils.addSndData(jrRtn, sndRecord);
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}	
	/**
	 *      [A] 오퍼레이션명 :반납/반송 작업 지시 생성
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord insMakeRetnWrkBook(String logId, String methodNms, JDTORecord InRecord) throws DAOException {
		String methodNm = "반납/반송 작업 지시 생성 [GdsYsJspSeEJB.insMakeRetnWrkBook] < " + methodNms;
		
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			JDTORecordSet jsMsg = JDTORecordFactory.getInstance().createRecordSet("");	//크레인스케줄 정리를 위함

			String stlNos       	= commUtils.trim(InRecord.getFieldString("SSTL_NOS"  ));	
			String ysStkColGp   	= commUtils.trim(InRecord.getFieldString("YS_STK_COL_GP"  ));
			String ydToLocGuide		= commUtils.trim(InRecord.getFieldString("YD_TO_LOC_GUIDE"  ));
			String ydWrkPlanTcar 	= commUtils.trim(InRecord.getFieldString("YD_WRK_PLAN_TCAR")); //야드작업계획대차
			String ydSchCd  		= commUtils.trim(InRecord.getFieldString("YD_SCH_CD"  ));
			String modifier     	= commUtils.trim(InRecord.getFieldString("USERID"    )); 
			if (ysStkColGp.length() > 6) {
				ysStkColGp = ysStkColGp.substring(0, 6);
			}

			if ("".equals(stlNos)) {
				throw new Exception("이적 재료번호가 없습니다.");
			} else if ("".equals(ysStkColGp) || ysStkColGp.length() < 4) {
				throw new Exception("Span[" + ysStkColGp + "] 정보가 없습니다.");
			} 
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();

			jrParam.setField("SSTL_NOS"     , stlNos    ); //재료번호들
			jrParam.setField("YS_STK_COL_GP", ysStkColGp); //야드적치열구분

			//작업예약 대상재료 조회
			JDTORecordSet jsWbMtl = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getblMvStkWrkBookMtlPp", logId, methodNm, "재료번호로 조회");

			int rowCnt = jsWbMtl.size();

			if (rowCnt <= 0) {
				throw new Exception("이적 재료 정보가 없습니다.");
			}
			
			String ydBayGp =  ydSchCd.substring(1,2);
			String ydAimBayGp = "";
			
			if(ydWrkPlanTcar.equals("")) {
				ydAimBayGp = ydBayGp; 
			} else {
				if(ydBayGp.equals("A")) {
					ydAimBayGp = "B";
				} else if(ydBayGp.equals("B")) {
					ydAimBayGp = "A";
				}	
			}
			
			/**********************************************************
			* 2. 이적 작업예약 등록
			**********************************************************/
			jrParam.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
			jrParam.setField("YD_AIM_BAY_GP"   , ydAimBayGp   ); //야드목표동구분
			jrParam.setField("YD_TO_LOC_GUIDE" , ydToLocGuide ); //야드To위치Guide
			jrParam.setField("YD_WRK_PLAN_TCAR", ydWrkPlanTcar); //야드작업계획대차
			
			//작업예약등록
//			jsMsg.addRecord(this.insMvstkWrkBook(jrParam, jsWbMtl));
			
			/**********************************************************
			* 1. 야드스케쥴코드 Check
			**********************************************************/
			JDTORecord jrSchCd = gdsYsComm.chkSchCdEqp(jrParam);
			
			String ydGp       = ydSchCd.substring(0, 1);	//야드구분
			String ydEqpId    = commUtils.trim(jrSchCd.getFieldString("YD_EQP_ID"   ));	//야드설비ID(크레인)
			String ydSchPrior = commUtils.trim(jrSchCd.getFieldString("YD_SCH_PRIOR"));	//야드스케쥴우선순위
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
			Vector vcLot = this.setCrnSpecSpr(jrSchCd, jsWbMtl);
			commUtils.printParam("", vcLot);
			JDTORecordSet jsLotMtl = null;
			int lotCnt   = vcLot.size();	//크레인사양 분리 작업예약수
			int lotMtlSh = 0;				//작업예약재료매수
			String ydWbookId = "";			//야드작업예약ID
			String ydWbookIdFst = "";		//야드작업예약ID(첫번째)
			JDTORecord jrRow = null;
			commUtils.printLog(logId, "lotCnt:"+lotCnt, "SL");
			JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, modifier);
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

				commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBook", logId, methodNm, "TB_YS_WRKBOOK");

				//작업예약재료 등록
				
				for (int jj = 0; jj < lotMtlSh; jj++) {
					jrRow = jsLotMtl.getRecord(jj);
					
					JDTORecord jrRtn1 = JDTORecordFactory.getInstance().create();
					
					jrRtn1.setField("YD_WBOOK_ID"   , ydWbookId     ); //야드작업예약ID
					jrRtn1.setField("SSTL_NO"       , commUtils.trim(jrRow.getFieldString("SSTL_NO"       )));	//재료번호
					jrRtn1.setField("YS_STK_COL_GP" , commUtils.trim(jrRow.getFieldString("YS_STK_COL_GP")));	//야드적치열구분
					jrRtn1.setField("YS_STK_BED_NO" , commUtils.trim(jrRow.getFieldString("YS_STK_BED_NO")));	//야드적치Bed번호
					jrRtn1.setField("YS_STK_LYR_NO" , commUtils.trim(jrRow.getFieldString("YS_STK_LYR_NO")));	//야드적치단번호
					jrRtn1.setField("YS_STK_SEQ_NO" , commUtils.trim(jrRow.getFieldString("YS_STK_SEQ_NO")));	//야드적치SEQ번호
					jrRtn1.setField("MODIFIER"     	, modifier      );												//등록자
					commDao.insert(jrRtn1, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBookMtl", logId, methodNm, "TB_YS_WRKBOOKMTL");
				}
				

				//크레인스케줄 전문 - Log ID, Method, 수정자 Set
				
				if(ydEqpId.substring(2, 4).equals("SC")) {
					
					JDTORecordSet jsWbMtl1 = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnWrkSchCdCnt", logId, methodNm, "스케줄 count");
					jsWbMtl1.first();
					JDTORecord jrRtn1 = JDTORecordFactory.getInstance().create();
					jrRtn1		= jsWbMtl1.getRecord();
					
					String szSCH_CNT_YN = commUtils.trim(jrRtn1.getFieldString("SCH_CNT_YN"));
					
					if((szSCH_CNT_YN.equals("N")) && (ii == 0)) {
						jrYdMsg = JDTORecordFactory.getInstance().create();
						jrYdMsg.setField("YD_WBOOK_ID"     , ydWbookId ); //야드작업예약ID(첫번째꺼만)
						jrYdMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
						jrYdMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
						jrYdMsg.setField("YD_SCH_ST_GP"    , "M"          ); //야드스케쥴기동구분(Manual)
						jrYdMsg.setField("YD_SCH_REQ_GP"   , "M"          ); //야드스케쥴요청구분(이적)
						jrYdMsg.setField("YD_WRK_PLAN_TCAR", ydWrkPlanTcar); //야드작업계획대차(대차상차 크레인스케줄을 전송하지 않기 위해 추가)
						jsMsg.addRecord(jrYdMsg);
					} else {
						commUtils.printLog(logId, "해당크레인 반납작업이 있으므로 스케쥴 기동 Skip", "SL");
					}
				}	
			}

			if(!ydEqpId.substring(2, 4).equals("SC")) {
				jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setField("YD_WBOOK_ID"     , ydWbookIdFst ); //야드작업예약ID(첫번째꺼만)
				jrYdMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
				jrYdMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
				jrYdMsg.setField("YD_SCH_ST_GP"    , "M"          ); //야드스케쥴기동구분(Manual)
				jrYdMsg.setField("YD_SCH_REQ_GP"   , "M"          ); //야드스케쥴요청구분(이적)
				jrYdMsg.setField("YD_WRK_PLAN_TCAR", ydWrkPlanTcar); //야드작업계획대차(대차상차 크레인스케줄을 전송하지 않기 위해 추가)
				jsMsg.addRecord(jrYdMsg);
			}
					
			
			/**********************************************************
			* 3. 대차작업이 있으면 공대차출발지시 처리
			**********************************************************/
			if (!"".equals(ydWrkPlanTcar)) {
				//공대차출발지시 처리시 Exception을 발생시키지 않기위해 미리 Check
				String msgTcar = ""; //공대차출발지시 처리 메세지
			 			
				//대차스케쥴정보(공대차출발지시) 조회
				jrParam.setField("YD_EQP_ID", ydWrkPlanTcar);	//야드설비ID(대차)
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
				          FROM TB_YD_TCARFTMVMTL TM
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
				                         FROM TB_YD_TCARSCH
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
					jrParam.setField("YD_L2_ID" , "N4"    ); 
					jrRtn = YsComm.trtTcarSchLevWo(jrParam);
				} else {
					commUtils.printLog(logId, "대차[" + ydWrkPlanTcar + "] 공대차출발지시 불가 : " + msgTcar, "SL");
				}
			}

			/**********************************************************
			* 4. 동내이적작업 크레인별 첫번째 스케줄 전송
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
	 * 스케줄기준관리 - 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updSchRule(GridData gdReq) throws DAOException {
		String methodNm = "스케줄기준관리 - 수정[GdsYsJspSeEJB.updSchRule] < " + gdReq.getNavigateValue();
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
			
			if(!"CR".equals(gdReq.getParam("YD_SCH_GP"))) {
				
				String msgId;

				for (int ii = 0; ii < rowCnt; ii++) {
					
					//S-Crane 작업순위 변경 전송
					if("A".equals(commUtils.trim(gdReq.getParam("YD_BAY_GP")))) {
						msgId = "YSN6L005";
					} else if("D".equals(commUtils.trim(gdReq.getParam("YD_BAY_GP")))) {
						msgId = "YSN5L005";
					} else {
						msgId = "";
					}
					
					jrParam.setField("YD_GP"			, commUtils.trim(gdReq.getParam("YD_GP"))); 
					jrParam.setField("YD_BAY_GP"		, commUtils.trim(gdReq.getParam("YD_BAY_GP"))); 
					jrParam.setField("YD_EQP_ID"		, commUtils.trim(gdReq.getParam("CRN_NO"))); 
					jrParam.setField("YD_SCH_PRIOR"		, commUtils.getValue(gdReq, "M_CRN_PRIOR1", ii)); 
					jrParam.setField("YD_SCH_CD"		, commUtils.getValue(gdReq, "YD_SCH_CD", ii)); 

					//전송Data 조회
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2(msgId, jrParam));						
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
	 * 스케줄기준관리 - 선택복구
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord resetSchRule(GridData gdReq) throws DAOException {
		String methodNm = "스케줄기준관리 - 선택복구[GdsYsJspSeEJB.resetSchRule] < " + gdReq.getNavigateValue();
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
				
				//스케줄기준 수정 
				jrParam.setField("R_CRN_PRIOR1"		, commUtils.getValue(gdReq, "R_CRN_PRIOR1", ii)); 
				jrParam.setField("R_CRN_PRIOR2"		, commUtils.getValue(gdReq, "R_CRN_PRIOR2", ii)); 
				jrParam.setField("YD_SCH_CD"		, commUtils.getValue(gdReq, "YD_SCH_CD", ii)); 
				jrParam.setField("YD_SCH_GP"		, commUtils.trim(gdReq.getParam("YD_SCH_GP")) ); 
				jrParam.setField("YD_CRN_STAT1"		, commUtils.getValue(gdReq, "YD_CRN_STAT1", ii) ); 
				jrParam.setField("YD_CRN_STAT2"		, commUtils.getValue(gdReq, "YD_CRN_STAT2", ii) ); 
				
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.resetSchRule", logId, methodNm, "스케줄기준 선택복구");
								
			}
			
			if(!"CR".equals(gdReq.getParam("YD_SCH_GP"))) {
				
				String msgId;

				for (int ii = 0; ii < rowCnt; ii++) {
					
					//S-Crane 작업순위 변경 전송
					if("A".equals(commUtils.trim(gdReq.getParam("YD_BAY_GP")))) {
						msgId = "YSN6L005";
					} else if("D".equals(commUtils.trim(gdReq.getParam("YD_BAY_GP")))) {
						msgId = "YSN5L005";
					} else {
						msgId = "";
					}
					
					jrParam.setField("YD_GP"			, commUtils.trim(gdReq.getParam("YD_GP"))); 
					jrParam.setField("YD_BAY_GP"		, commUtils.trim(gdReq.getParam("YD_BAY_GP"))); 
					jrParam.setField("YD_EQP_ID"		, commUtils.trim(gdReq.getParam("CRN_NO"))); 
					jrParam.setField("YD_SCH_PRIOR"		, commUtils.getValue(gdReq, "M_CRN_PRIOR1", ii)); 
					jrParam.setField("YD_SCH_CD"		, commUtils.getValue(gdReq, "YD_SCH_CD", ii)); 

					//전송Data 조회
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2(msgId, jrParam));						
				}				
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
		String methodNm = "스케줄기준관리 - 전체복구[GdsYsJspSeEJB.resetAllSchRule] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			//스케줄기준 수정 
			jrParam.setField("YD_GP"		, commUtils.trim(gdReq.getParam("YD_GP")) ); 
			jrParam.setField("YD_BAY_GP"	, commUtils.trim(gdReq.getParam("YD_BAY_GP")) ); 
			
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.resetAllSchRule", logId, methodNm, "스케줄기준 전체복구");
								
			if(!"CR".equals(gdReq.getParam("YD_SCH_GP"))) {
				
				String msgId;
				
				//S-Crane 작업순위 변경 전송
				if("A".equals(commUtils.trim(gdReq.getParam("YD_BAY_GP")))) {
					msgId = "YSN6L005All";
				} else if("D".equals(commUtils.trim(gdReq.getParam("YD_BAY_GP")))) {
					msgId = "YSN5L005All";
				} else {
					msgId = "";
				}
				
				jrParam.setField("YD_GP"			, commUtils.trim(gdReq.getParam("YD_GP"))); 
				jrParam.setField("YD_BAY_GP"		, commUtils.trim(gdReq.getParam("YD_BAY_GP"))); 

				//전송Data 조회
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2(msgId, jrParam));						
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of resetAllSchRule
	
	/**
	 * 스케줄기준관리 - 전송
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord sndSchRule(GridData gdReq) throws DAOException {
		String methodNm = "스케줄기준관리 - 전송[GdsYsJspSeEJB.sndSchRule] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			String msgId;
				
			//S-Crane 작업순위 변경 전송
			if("A".equals(commUtils.trim(gdReq.getParam("YD_BAY_GP")))) {
				msgId = "YSN6L005All";
			} else if("D".equals(commUtils.trim(gdReq.getParam("YD_BAY_GP")))) {
				msgId = "YSN5L005All";
			} else {
				msgId = "";
			}
			
			jrParam.setField("YD_GP"			, commUtils.trim(gdReq.getParam("YD_GP"))); 
			jrParam.setField("YD_BAY_GP"		, commUtils.trim(gdReq.getParam("YD_BAY_GP"))); 

			//전송Data 조회
			jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2(msgId, jrParam));						

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of sndSchRule
	
	/**
	 * 장비가동상황 - 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updEqpOprnStat(GridData gdReq) throws DAOException {
		String methodNm = "장비가동상황 - 수정[GdsYsJspSeEJB.updEqpOprnStat] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			String msgId = "";
			
			//Return Value
			JDTORecord jrRtn = null;
			String     sYdEqpId = null;
			String     sYdBayGp = null;
			String     mthdNm  = null; //처리 Method명
			
			JDTORecord jrRst = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			EJBConnector rcvConn = new EJBConnector("default", "GdsYsL2RcvSeEJB", this);

			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				sYdEqpId = commUtils.getValue(gdReq, "YD_EQP_ID", ii); 
				sYdBayGp = sYdEqpId.substring(1,2);
				
				if("A".equals(sYdBayGp)) {
					msgId = "N6YSL003";
				} else if("B".equals(sYdBayGp)) {
					msgId = "N4YSL003";
				} else if("D".equals(sYdBayGp)) {
					msgId = "N5YSL003";
				} else if("E".equals(sYdBayGp)) {
					msgId = "N5YSL003";
				}
				mthdNm = "rcv" + msgId;
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name				
				jrParam.setField("MSG_ID"				, msgId);
				jrParam.setField("YD_EQP_ID"			, sYdEqpId);
				jrParam.setField("YD_EQP_STAT"			, commUtils.getValue(gdReq, "YD_EQP_STAT2", ii));
				jrParam.setField("YD_EQP_PAUSE_CODE"	, "0000");
				jrParam.setField("YD_EQP_TRBL_RCVR_DT"	, commUtils.getDateTime14());
				
				
				jrRst = (JDTORecord)rcvConn.trx(mthdNm, new Class[] { JDTORecord.class }, new Object[] { jrParam });				
				

				//전송Data 조회
				jrRtn = commUtils.addSndData(jrRtn, jrRst);				
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updEqpOprnStat

	/**
	 * 차량작업 포인트 현황- 차량출발
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord procLeaveCar(GridData gdReq) throws DAOException {
		String methodNm = "차량작업 포인트 현황-차량출발[GdsYsJspSeEJB.procLeaveCar] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String szMsg = null;
		String szYD_CAR_SCH_ID		= null;
		String szYD_CAR_PROG_STAT	= null;
		String szCAR_NO				= null;
		String szCARD_NO			= null;
		String szSPOS_WLOC_CD		= null;
		String szARR_WLOC_CD		= null;
		String szYD_PNT_CD			= null;
		String szYD_PNT_CD3			= null;
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
				
				if(YsConstant.YD_CARLD_CMPL.equals(commUtils.getValue(gdReq, "YD_CAR_PROG_STAT", ii))){
					szMsg = "["+methodNm+"] 차량스케줄["+szYD_CAR_SCH_ID+"]의 차량진행상태[5]로 강제셋팅 후 강제 출발처리함";
					commUtils.printLog(logId, szMsg, "SL");
				}else{
					if( !szYD_CAR_PROG_STAT.equals(YsConstant.YD_CARLD_CMPL)) {
						if( !szYD_CAR_PROG_STAT.equals(YsConstant.YD_CARUD_CMPL)) {
							szMsg = "["+methodNm+"] 차량스케줄["+szYD_CAR_SCH_ID+"]의 차량진행상태["+szYD_CAR_PROG_STAT+"]가 상하차완료가 아니므로 SKIP시킴";
							commUtils.printLog(logId, szMsg, "SL");
							continue;
						}	
					}
				}
				szCAR_NO				=  commUtils.trim(recTemp.getFieldString("CAR_NO"));
				szCARD_NO				=  commUtils.trim(recTemp.getFieldString("CARD_NO"));
				szSPOS_WLOC_CD			=  commUtils.trim(recTemp.getFieldString("SPOS_WLOC_CD"));
				szARR_WLOC_CD			=  commUtils.trim(recTemp.getFieldString("ARR_WLOC_CD"));
				szYD_PNT_CD				=  commUtils.trim(recTemp.getFieldString("YD_PNT_CD1"));
				szYD_PNT_CD3			=  commUtils.trim(recTemp.getFieldString("YD_PNT_CD3"));
				szTRANS_ORD_DATE		=  commUtils.trim(recTemp.getFieldString("TRANS_ORD_DATE"));
				szTRANS_ORD_SEQNO		=  commUtils.trim(recTemp.getFieldString("TRANS_ORD_SEQNO"));
				
				
				//--------------------------------------------------------------------------------
				
				szMsg = "차량스케줄["+szYD_CAR_SCH_ID+"]의 차량출발 처리 EJB 호출";
				commUtils.printLog(logId, szMsg, "SL");
				
				JDTORecord recInTemp			= JDTORecordFactory.getInstance().create();
				recInTemp.setResultCode(logId);		//Log ID
				recInTemp.setResultMsg(methodNm);	//Log Method Name
				recInTemp.setField("CARD_NO", 				szCARD_NO);
				recInTemp.setField("CAR_NO", 				szCAR_NO);
				//하차완료
				if(szYD_CAR_PROG_STAT.equals("E")) {
					recInTemp.setField("SPOS_WLOC_CD", 		szARR_WLOC_CD);
					recInTemp.setField("SPOS_YD_PNT_CD", 	szYD_PNT_CD3);
				} else {	
					recInTemp.setField("SPOS_WLOC_CD", 		szSPOS_WLOC_CD);
					recInTemp.setField("SPOS_YD_PNT_CD", 	szYD_PNT_CD);
				}	
				recInTemp.setField("TRANS_ORD_DATE", 		szTRANS_ORD_DATE);
				recInTemp.setField("TRANS_ORD_SEQNO", 		szTRANS_ORD_SEQNO);
				
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
	} // end of updgdsWhsPlnInfojl
	
	/**
	 * 차량작업 포인트 현황
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord procPntUnit(GridData gdReq) throws DAOException {
		String methodNm = "차량작업 포인트 현황-포인트 사용 등록[GdsYsJspSeEJB.procPntUnit] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String szMsg = null;
		String szMethodName = "procPntUnit";
		String szYS_STK_COL_GP		= null;
		String szYD_STK_COL_ACT_STAT= null;
		String szOLD_YD_STK_COL_ACT_STAT= null;
		String szTRN_EQP_CD= null;
		
		String szYdGp  = null; 
		String szJMS_TC_CD  = null; 
		JDTORecord recInTemp = null;
		JDTORecord recInTemp1 = null;
		boolean isSendable				= true;
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			//Return Value
//			JDTORecord jrRtn = null;
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
				szTRN_EQP_CD			= commUtils.getValue(gdReq, "TRN_EQP_CD", ii);

    			recOutTemp = JDTORecordFactory.getInstance().create();
    			jrParam.setField("YS_STK_COL_GP"		, szYS_STK_COL_GP);
    			jrParam.setField("YD_STK_COL_ACT_STAT"	, szYD_STK_COL_ACT_STAT);
    			
    			
    			
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
    	    	
		    	if("".equals(szTRN_EQP_CD)){
    				jrParam.setField("TRN_EQP_CD"	, "");
    				jrParam.setField("CAR_NO"		, "");
    				jrParam.setField("CARD_NO"		, "");
    			}else{
    				jrParam.setField("TRN_EQP_CD"	, commUtils.trim(recOutTemp.getFieldString("TRN_EQP_CD")));
    				jrParam.setField("CAR_NO"		, commUtils.trim(recOutTemp.getFieldString("CAR_NO")));
    				jrParam.setField("CARD_NO"		, commUtils.trim(recOutTemp.getFieldString("CARD_NO")));
    			}
		    	
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
		    	recInTemp1.setField("JMS_TC_CD"				, "YSTSJ012");
		    	recInTemp1.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시
		    	recInTemp1.setField("YD_GP"					, szYS_STK_COL_GP.substring(0,1));
		    	recInTemp1.setField("YS_STK_COL_GP"			, szYS_STK_COL_GP);
				
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
		    		szYdGp = szYS_STK_COL_GP.substring(0,2);
		    		/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		    		 * 업무기준 : 차량출발시 저장위치 제원 야드L2로 전송
		    		 *** 야드BLOOM:N1,BILLET:N2,선재:N3,봉강:N4,선재자동화:N5,봉강자동화:N6 	
			             YSN1L001 저장위치제원
			             YSN1L002 저장품제원
			             YSN1L003 크레인작업지시
			             YSN1L004 크레인작업실적응답
		    		 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
					if(szYdGp.startsWith("B") ){
						szJMS_TC_CD =  "YSN1L001";
			    	}else if(szYdGp.startsWith("C")){
						szJMS_TC_CD =  "YSN2L001";
			    	}else if(szYdGp.startsWith("KA")){
						szJMS_TC_CD =  "YSN6L001";
		          	}else if(szYdGp.startsWith("KB")){
						szJMS_TC_CD =  "YSN4L001";
			    	}else if(szYdGp.startsWith("KD")){
						szJMS_TC_CD =  "YSN5L001";
			    	}else if(szYdGp.startsWith("KE")){
						szJMS_TC_CD =  "YSN3L001";
			    	}
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

			szMsg="[구내내운송 소재차량Point개폐 전송  성공";
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
	} // end of updgdsWhsPlnInfojl
	
	/**
	 * 봉강출하대기차량 포인트 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord doModCarPnt(GridData gdReq) throws DAOException {
		String methodNm = "봉강출하대기차량 포인트 변경[GdsYsJspSeEJB.doModCarPnt] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String szMsg = null;
		String szMethodName = "doModCarPnt";
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			JDTORecord sndRecord = JDTORecordFactory.getInstance().create();
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
    		jrParam.setField("CAR_NO"		, commUtils.trim(gdReq.getParam("MOD_CAR_NO")));
    		jrParam.setField("CAR_POINT"	, commUtils.trim(gdReq.getParam("MOD_CAR_POINT")));
    		
		    commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdCarSchPnt", logId, methodNm, "TB_YS_STKCOL 등록");				    	    	
    	    
			szMsg="[봉강출하대기차량 포인트 변경  성공";
			commUtils.printLog(logId, szMsg, "SL");
	    	
			commUtils.printLog(logId, methodNm, "S-");

			return sndRecord;//보내는 값 없슴
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updgdsWhsPlnInfojl	

	/**
	 * 차량작업 포인트 현황- 입동지시
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord procBayInWo(GridData gdReq) throws DAOException {
		String methodNm = "차량작업 포인트 현황-입동지시[GdsYsJspSeEJB.procBayInWo] < " + gdReq.getNavigateValue();
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
	} // end of procBayInWo
	
	/**
	 * 차량작업 관리- 입동순서 변경처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord procGdsBayInWoSeqChang(GridData gdReq) throws DAOException {
		String methodNm = "차량작업 포인트 현황-입동지시[GdsYsJspSeEJB.procGdsBayInWoSeqChang] < " + gdReq.getNavigateValue();
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
	} // end of procBayInWo
	
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
		String methodNm = "차량작업관리화면 하차완료처리[GdsYsJspSeEJB.procCarUd] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String szMsg = null;
		String szARR_YD_PNT_CD = null;
		String szCurrDate = commUtils.getDateTime14();
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
			
//			
//			if( !szYD_CAR_STOP_LOC.equals("") ) {
//				szYD_GP = szYD_CAR_STOP_LOC.substring(0, 1);
//			}
//			
			
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
				recPara.setField("CARUD_CMPL_DT", 		commUtils.getDateTime14());
				
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
	 * 차량작업관리화면 배차취소처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return 
	 * @return 
	 * @return
	 * @throws DAOException
	 */
	public JDTORecord delCarSchNCarPoint(GridData gdReq) throws DAOException {
		String methodNm = "차량작업관리화면 배차취소처리[GdsYsJspSeEJB.delCarSchNCarPoint] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String szMsg = null;
		JDTORecord sndRecord	= JDTORecordFactory.getInstance().create();
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);	
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
	
			for(int i = 0 ; i < rowCnt ; i++ ) {				
				//DAO Parameter - Log ID, Method, 수정자 Set
				
				String szYD_CAR_SCH_ID 		= commUtils.getValue(gdReq, "YD_CAR_SCH_ID" , i);
				String szYD_CAR_STOP_LOC 	= commUtils.getValue(gdReq, "YD_CAR_STOP_LOC", i);
				
				/**********************************************************
				* 1.차량스케쥴 존재 여부
				**********************************************************/				
				szMsg = "차량스케줄ID["+szYD_CAR_SCH_ID+"]로 조회 ";
				commUtils.printLog(logId, szMsg, "SL");
				
				JDTORecord recTemp	= JDTORecordFactory.getInstance().create();
				JDTORecord recPara	= JDTORecordFactory.getInstance().create();
				
				//1. 차량스케줄을 먼저 조회해서 존재하는 지를 확인
				recTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
 
				/*com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarschCHK  
				SELECT NVL(MAX('Y'),'N') AS  CAR_LOC 
				 FROM USRYSA.TB_YS_STKCOL A
				  , USRYSA.TB_YS_CARSCH B
				 WHERE A.YS_STK_COL_GP=(CASE WHEN B.YD_CAR_PROG_STAT IN('1','2','3','4','5') THEN B.YD_CARLD_STOP_LOC ELSE B.YD_CARUD_STOP_LOC END)
				  AND A.CAR_NO <> B.CAR_NO
				  AND A.CAR_NO IS NOT NULL
				  AND B.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				*/
				//차량스케쥴 조회
				JDTORecordSet jsCarSch = commDao.select(recTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarschCHK", logId, methodNm, "차량스케쥴 조회");
				if (jsCarSch.size() <= 0) {
					throw new Exception( "차량스케줄ID["+szYD_CAR_SCH_ID+"]이 존재하지 않습니다");
				}
				jsCarSch.first();
				recPara = jsCarSch.getRecord();

				String szCAR_LOC = commUtils.trim(recPara.getFieldString("CAR_LOC"));	

				/**********************************************************
				* 1.차량스케쥴 존재 여부
				**********************************************************/				
				
				recPara         = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CAR_SCH_ID", 			szYD_CAR_SCH_ID);
	
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarSchWb 
				SELECT * FROM TB_YS_WRKBOOK
				 WHERE YD_WBOOK_ID    IN (SELECT YD_CARLD_WRK_BOOK_ID
				                            FROM TB_YS_CARSCH
				                           WHERE DEL_YN = 'N'
				                             AND YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				                             AND YD_CARLD_WRK_BOOK_ID IS NOT NULL
				                          UNION  ALL
				                          SELECT YD_CARUD_WRK_BOOK_ID
				                            FROM TB_YS_CARSCH
				                           WHERE DEL_YN = 'N'
				                             AND YD_CARUD_WRK_BOOK_ID IS NOT NULL
				                             AND YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID)
				   OR CAR_YD_WBOOK_ID IN (SELECT YD_CARLD_WRK_BOOK_ID
				                            FROM TB_YS_CARSCH
				                           WHERE DEL_YN = 'N'
				                             AND YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				                             AND YD_CARLD_WRK_BOOK_ID IS NOT NULL
				                          UNION  ALL
				                          SELECT YD_CARUD_WRK_BOOK_ID
				                            FROM TB_YS_CARSCH
				                           WHERE DEL_YN = 'N'
				                             AND YD_CARUD_WRK_BOOK_ID IS NOT NULL
				                             AND YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID)                       
				  AND DEL_YN = 'N'
	    			*/
				JDTORecordSet rsCrnWrk = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarSchWb", logId, methodNm, "크레인작업 확인"); 
		    	
		    	if (rsCrnWrk.size() > 0) {
					szMsg="크레인 작업이 존재하여 삭제 불가 합니다.";
					throw new Exception(szMsg);
				}
				
				//--------------------------------------------------------------------------------
				//	조회된 차량스케줄로 차량이송재료삭제
				//--------------------------------------------------------------------------------
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
				recPara.setField("DEL_YN", "Y");
				recPara.setField("MODIFIER",commUtils.trim(gdReq.getParam("YD_USER_ID")));					//수정자
				
				//차량이송소재 종료
				//intRtnVal = ydCarFtmvMtlDao.updYdCarftmvmtl(recInTemp, 1) ;
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarFtmvMtl  
				UPDATE TB_YS_CARFTMVMTL
				   SET MODIFIER = :V_MODIFIER
				     , MOD_DDTT = SYSDATE
			  	     , DEL_YN = :V_DEL_YN
				 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				   AND DEL_YN = 'N'
				  */ 
				
				commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarFtmvMtl", logId, methodNm, "TB_YS_CARFTMVMTL 종료");

				szMsg = "[" + methodNm + "] 차량스케줄["+szYD_CAR_SCH_ID+"]이 존재하므로 삭제 시작";
				commUtils.printLog(logId, szMsg, "SL");
				
				/* 
				com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarSch  
				UPDATE TB_YS_CARSCH
				   SET MODIFIER = :V_MODIFIER
				     , MOD_DDTT = SYSDATE
				     , DEL_YN = 'Y'
				WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
			       AND DEL_YN = 'N'
				*/						
				commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarSch", logId, methodNm, "TB_YS_CARSCH 삭제");
				
				if( !szCAR_LOC.equals("Y") ) { //다른 차량이 입동 한 경우 생략 한다. 
						
					//--------------------------------------------------------------------------------
					//	적치열 조회
					//--------------------------------------------------------------------------------
					recTemp			= JDTORecordFactory.getInstance().create();
					recTemp.setResultCode(logId);	//Log ID
					recTemp.setResultMsg(methodNm);	//Log Method Name
					recTemp.setField("YS_STK_COL_GP", 			szYD_CAR_STOP_LOC);
					recTemp.setField("YD_CAR_USE_GP", 			"G");
					recTemp.setField("YD_STK_COL_ACT_STAT", 	"C");
					recTemp.setField("YD_STK_COL_ACT_STAT", 	"C");
					recTemp.setField("MODIFIER",commUtils.trim(gdReq.getParam("YD_USER_ID")));					//수정자
					
					EJBConnector ejbConn = new EJBConnector("default", "YsCommCarMvSeEJB", this);
					String szRtnMsg = (String)ejbConn.trx("procCarPosActiveOrInActive", new Class[] { JDTORecord.class }, new Object[] { recTemp });
					
					szMsg="["+methodNm+"] 차량도착POINT를 CLEAR 완료 - 반환메세지 : " + szRtnMsg;
					commUtils.printLog(logId, szMsg, "SL");						
				}
			}
			commUtils.printLog(logId, methodNm, "S-", gdReq);	
			return sndRecord;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
			
	/***************************************************************************
	 * 크레인작업관리
	 **************************************************************************/
	/**
	 *      [A] 오퍼레이션명 : 크레인작업관리 크레인변경
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updCraneChange(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리 크레인변경[GdsYsJspSeEJB.updCraneChange] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord sndRecord = null;

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
			String szMsgId = "";
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

				/**********************************************************
				* 3.4 이전 크레인의 작업지시 취소 전문 송신
				**********************************************************/
				if ("1".equals(ydWrkProgStat)) {
					if(ydEqpId.startsWith("KB")){
						jrParam.setField("MSG_GP"       , "D"       ); //전문구분(취소)
						sndRecord = commUtils.addSndData(sndRecord, commDao.getMsgL2("YSN4L003", jrParam));					
					}
				}	
				
				//크레인스케줄 Table 크레인ID, 우선순위 Update
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnWrkMgt", logId, methodNm,  "TB_YS_CRNSCH");				
				
			
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

//					String szYdGp = chgYdEqpId.substring(0, 2);
	
					jrYdMsg.setResultCode(logId);		//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name
					jrYdMsg.setField("JMS_TC_CD"       , YsConstant.YSYSJ001);	//크레인작업지시요구
					jrYdMsg.setField("YD_EQP_ID"       , chgYdEqpId);	//야드설비ID
					jrYdMsg.setField("YD_WRK_PROG_STAT", "1"       );	//야드작업진행상태(권상작업지시)
					jrYdMsg.setField("YD_SCH_CD"       , ydSchCd   );	//야드스케쥴코드
					jrYdMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId);	//야드크레인스케쥴ID

					EJBConnector sndConn = new EJBConnector("default", "YsCommL3RcvSeEJB", this);
					JDTORecord jrSnd = (JDTORecord)sndConn.trx("rcvYSYSJ001", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });

					sndRecord = commUtils.addSndData(sndRecord, jrSnd);

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
//11.30
//			    	if(ydEqpId.startsWith("KACRA2")){
//			    		szMsgId = "YSN4L004";
//			    	}else if(ydEqpId.startsWith("KATC")){
//			    		szMsgId = "YSN4L004";
//					}else if(ydEqpId.startsWith("KA")){
//						szMsgId = "YSN6L004";
//			    	}else if(ydEqpId.startsWith("KB")){
//			    		szMsgId = "YSN4L004";
//			    	}else if(ydEqpId.startsWith("KD")){
//			    		szMsgId = "YSN5L004";
//			    	}else if(ydEqpId.startsWith("KE")){
//			    		szMsgId = "YSN3L004";
//			    	}					
//					if (szMsgId != "") {
//						
//						resMsg.setResultCode(logId);	//Log ID
//						resMsg.setResultMsg(methodNm);	//Log Method Name
//						resMsg.setField("YD_EQP_ID"     , ydEqpId); //야드설비ID
//						resMsg.setField("YD_L2_WR_GP"   , "J"    ); //야드L2실적구분(지시요구)
//						resMsg.setField("YD_L3_HD_RS_CD", "9999" ); //야드L3처리결과코드(Error)
//						resMsg.setField("YD_L3_MSG"     , "크레인변경[" + chgYdEqpId + "]" ); //야드L3MESSAGE
//	
//						sndRecord = commUtils.addSndData(sndRecord, commDao.getMsgL2(szMsgId, resMsg));
//					}

						
					resMsg.setField("YD_EQP_ID"     , ydEqpId); //야드설비ID
					resMsg.setField("YD_L2_WR_GP"   , "J"    ); //야드L2실적구분(지시요구)
					resMsg.setField("YD_L3_HD_RS_CD", "9999" ); //야드L3처리결과코드(Error)
					resMsg.setField("YD_L3_MSG"     , "크레인변경[" + chgYdEqpId + "]" ); //야드L3MESSAGE
					resMsg.setField("MODIFIER"      , modifier  ); //수정자

			    	if(ydEqpId.startsWith("KACRA2")){
						sndRecord = commUtils.addSndData(sndRecord, gdsYsComm.getYSN4L004(resMsg));
			    	}else if(ydEqpId.startsWith("KATC")){
						sndRecord = commUtils.addSndData(sndRecord, gdsYsComm.getYSN4L004(resMsg));
					}else if(ydEqpId.startsWith("KA")){
						sndRecord = commUtils.addSndData(sndRecord, gdsYsComm.getYSN6L004(resMsg));
			    	}else if(ydEqpId.startsWith("KB")){
						sndRecord = commUtils.addSndData(sndRecord, gdsYsComm.getYSN4L004(resMsg));
			    	}else if(ydEqpId.startsWith("KD")){
						sndRecord = commUtils.addSndData(sndRecord, gdsYsComm.getYSN5L004(resMsg));
			    	}else if(ydEqpId.startsWith("KE")){
						sndRecord = commUtils.addSndData(sndRecord, gdsYsComm.getYSN3L004(resMsg));
			    	}					
					
				}
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
	 *      [A] 오퍼레이션명 : 크레인작업관리 권하위치변경
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updDownLocChange(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리 권하위치변경[GdsYsJspSeEJB.updDownLocChange] < " + gdReq.getNavigateValue();
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
			//gdRes.setMessage(szRtnValue);
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
		String methodNm = "크레인스케줄 권하지시위치 변경[GdsYsJspSeEJB.updCrnSchDnWoLoc] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			String ydEqpId       = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); //야드설비ID(크레인)
			String CurrydEqpId   = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); //야드설비ID(크레인)
			String ydSchCd       = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
			String ydCrnSchId    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )); //야드크레인스케쥴ID
			String ydWbookId     = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"     )); //야드작업예약ID
			String ysDnWoLoc     = commUtils.trim(rcvMsg.getFieldString("YS_DN_WO_LOC"    )); //야드권하지시위치(신규)
			String modifier      = commUtils.trim(rcvMsg.getFieldString("MODIFIER"      )); //수정자

			String ydWrkProgStat = commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT"      )); //작업진행상태
			
			String ChgYdRcptPlnStrLoc  = "";
			String ydStkColDirGp = "";
			
			if(ysDnWoLoc.startsWith("KATS0")) {
				if       (ysDnWoLoc.substring(0, 6).equals("KATS01")) {
					ChgYdRcptPlnStrLoc      = "KA0101";
				} else if(ysDnWoLoc.substring(0, 6).equals("KATS02")) {
					ChgYdRcptPlnStrLoc      = "KA0103";
				} else if(ysDnWoLoc.substring(0, 6).equals("KATS03")) {
					ChgYdRcptPlnStrLoc      = "KA0105";
				} else if(ysDnWoLoc.substring(0, 6).equals("KATS04")) {
					ChgYdRcptPlnStrLoc      = "KA0107";
				} else if(ysDnWoLoc.substring(0, 6).equals("KATS05")) {
					ChgYdRcptPlnStrLoc      = "KA0109";
				} else if(ysDnWoLoc.substring(0, 6).equals("KATS06")) {
					ChgYdRcptPlnStrLoc      = "KA0111";
				} else if(ysDnWoLoc.substring(0, 6).equals("KATS07")) {
					ChgYdRcptPlnStrLoc      = "KA0113";
				}				
			}
						
			if ("".equals(ydCrnSchId)) {
				throw new Exception("크레인스케쥴ID가 없습니다.");
			} else if ("".equals(ysDnWoLoc)) {
				throw new Exception("변경할 권하지시위치가 없습니다.");
			} else if (ysDnWoLoc.length() < 8) {
				throw new Exception("잘못된 권하지시위치[" + ysDnWoLoc + "] 입니다.");
			}


			String ysStkColGp     = ysDnWoLoc.substring(0, 6); //야드적치열구분
			String ysStkBedNo     = ysDnWoLoc.substring(6, 8); //야드적치Bed번호
			String ysStkLyrNo     = "";
			if(ydEqpId.substring(2,4).equals("SC")) {
				if(ysDnWoLoc.length() != 10) {
					throw new Exception("자동화 창고는 10자리 입니다.[" + ysDnWoLoc + "] 입니다.");
				}
			}
									
			if(ysDnWoLoc.length() == 10) {
				ysStkLyrNo     = ysDnWoLoc.substring(8, 10); //야드적치lyr번호
			}	
			String ydDnWoLocOld   = ""; //야드권하지시위치(기존)
			String ydDnWoLayerOld = ""; //야드권하지시위치(기존)
			String ydDnWoLayer    = ""; //야드권하지시단(신규)
			String ydDnWoLocXaxis = ""; //야드권하지시X축(신규)
			String ydDnWoLocYaxis = ""; //야드권하지시Y축(신규)
			String ydDnWoLocZaxis = ""; //야드권하지시Z축(신규)
			String MaxMtlLen = ""; 
			
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, modifier);

			jrParam.setField("YD_CRN_SCH_ID"       	, ydCrnSchId);
			jrParam.setField("YD_CARLD_WRK_BOOK_ID"	, ydWbookId );	//야드상차작업예약ID
			jrParam.setField("YD_WBOOK_ID"			, ydWbookId );	
			jrParam.setField("YS_STK_COL_GP"       	, ysStkColGp);
			jrParam.setField("YS_STK_BED_NO"       	, ysStkBedNo);
			jrParam.setField("YS_STK_LYR_NO"       	, ysStkLyrNo);
			jrParam.setField("MODIFIER"       		, modifier);
			
			/**********************************************************
			* 1. 신규 권하지시위치 Bed정보 조회
			**********************************************************/
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchDnWoLocCurBedGds
			SELECT YS_STK_COL_GP  
			     , YS_STK_BED_NO  
			     , YS_STK_LYR_NO
			     , YD_DN_WO_LOC_XAXIS
			     , YD_DN_WO_LOC_YAXIS
			     , YD_DN_WO_LOC_ZAXIS
			     , YD_DN_WO_LOC_OLD
			     , YD_DN_WO_LAYER_OLD
			     , CASE WHEN SUBSTR(YS_STK_COL_GP,1,3) NOT IN ('KA0','KD0') THEN
			                     (SELECT CASE WHEN SUM(DECODE(YD_STK_LYR_MTL_STAT,'U',1,0)) > 0 THEN 'UP' --권상대기 있음
			                             ELSE 'AAA' END 
			                        FROM TB_YS_STKLYR 
			                       WHERE YS_STK_COL_GP = A.YS_STK_COL_GP 
			                         AND YS_STK_BED_NO = A.YS_STK_BED_NO      
			                         AND YS_STK_LYR_NO < A.YS_STK_LYR_NO )     
			                  
			            ELSE     (SELECT CASE WHEN SUM(DECODE(YD_STK_LYR_MTL_STAT,'U',1,'D',1,0)) > 0 THEN 'UP' --권상대기 있음
			                        ELSE 'AAA' END 
			                        FROM TB_YS_STKLYR 
			                       WHERE YS_STK_COL_GP = A.YS_STK_COL_GP 
			                         AND YS_STK_BED_NO = A.YS_STK_BED_NO      
			                         AND YS_STK_LYR_NO = A.YS_STK_LYR_NO )     
			            END AS DL_LOC_CHK_RST  
			     , CASE WHEN SUBSTR(YS_STK_COL_GP,1,3) IN ('KA0','KD0') THEN
			                     (SELECT CASE WHEN SUM(DECODE(YD_STK_LYR_MTL_STAT,'U',1,'D',1,0))+ YD_MTL_SH <= 2 THEN 'Y' --권상대기 있음
			                             ELSE 'N' END 
			                        FROM TB_YS_STKLYR 
			                       WHERE YS_STK_COL_GP = A.YS_STK_COL_GP 
			                         AND YS_STK_BED_NO = A.YS_STK_BED_NO      
			                         AND YS_STK_LYR_NO = A.YS_STK_LYR_NO )     
			                  
			            ELSE  'Y'     
			            END AS DL_LOC_CHK  
			     , YD_MTL_SH    
			      
			  FROM
			(  
			SELECT A.YS_STK_COL_GP  
			     , A.YS_STK_BED_NO  
			     , B.YS_STK_LYR_NO
			     , MIN(A.YD_STK_BED_XAXIS)              AS YD_DN_WO_LOC_XAXIS
			     , MIN(A.YD_STK_BED_YAXIS)              AS YD_DN_WO_LOC_YAXIS
			     , MIN(A.YD_STK_BED_ZAXIS)              AS YD_DN_WO_LOC_ZAXIS
			     , MIN(CM.YS_DN_WO_LOC)                 AS YD_DN_WO_LOC_OLD
			     , MIN(CM.YS_DN_WO_LAYER)               AS YD_DN_WO_LAYER_OLD
			     , MIN(CM.YD_MTL_SH) AS YD_MTL_SH
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
			   AND B.YS_STK_LYR_NO  = DECODE(:V_YS_STK_LYR_NO,NULL,
			                           NVL(
			                              (SELECT MAX(YS_STK_LYR_NO) + 1
			                                   FROM TB_YS_STKLYR C
			                                  WHERE C.YS_STK_COL_GP = A.YS_STK_COL_GP
			                                    AND C.YS_STK_BED_NO = A.YS_STK_BED_NO 
			                                    AND C.SSTL_NO IS NOT NULL
			                                     AND ( (SUBSTR(C.YS_STK_COL_GP,1,3) = 'KA0' AND C.YS_STK_LYR_NO >= (CASE WHEN CM.YD_MTL_T < 30 THEN 1 ELSE 4 END )
			                                                                                AND C.YS_STK_LYR_NO <= (CASE WHEN CM.YD_MTL_T < 30 THEN 3 ELSE 99 END )        
			                                           ) 
			                                          OR (SUBSTR(C.YS_STK_COL_GP,1,3) <> 'KA0') 
			                                        )

			                                   GROUP BY C.YS_STK_COL_GP, C.YS_STK_BED_NO
			                                )
			                                
			                            ,'01'), :V_YS_STK_LYR_NO )      
			   AND A.YS_STK_BED_NO  = :V_YS_STK_BED_NO
			   AND A.DEL_YN = 'N'
			   AND A.YD_STK_BED_ACT_STAT = 'L'
			   AND B.YD_STK_LYR_ACT_STAT = 'E'
			   AND B.SSTL_NO IS NULL
			GROUP BY A.YS_STK_COL_GP,A.YS_STK_BED_NO,B.YS_STK_LYR_NO   
			) A
			*/
			JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchDnWoLocCurBedGds", logId, methodNm, "신규권하위치 조회");
			JDTORecord jrCrnSch = JDTORecordFactory.getInstance().create();
			if (jsCrnSch == null || jsCrnSch.size() <= 0) {
				throw new Exception("신규 권하지시위치[" + ysDnWoLoc + "] 정보가 없습니다.");
			} else {
			
		    	jrCrnSch = jsCrnSch.getRecord(0);

		    	ydDnWoLocOld   		= commUtils.trim(jrCrnSch.getFieldString("YD_DN_WO_LOC_OLD"));
		    	ydDnWoLayerOld 		= commUtils.trim(jrCrnSch.getFieldString("YS_DN_WO_LAYER_OLD"));
		    	ysStkBedNo          = commUtils.trim(jrCrnSch.getFieldString("YS_STK_BED_NO")); 
		    	
		    	if(ydEqpId.substring(2,4).equals("SC")) {
		    		ydDnWoLayer     = ysStkLyrNo;
		    	} else {
		    		ydDnWoLayer     = commUtils.trim(jrCrnSch.getFieldString("YS_STK_LYR_NO"    )); 
		    	}
		    	ydDnWoLocXaxis 		= commUtils.trim(jrCrnSch.getFieldString("YD_DN_WO_LOC_XAXIS"));
		    	ydDnWoLocYaxis 		= commUtils.trim(jrCrnSch.getFieldString("YD_DN_WO_LOC_YAXIS"));
		    	ydDnWoLocZaxis 		= commUtils.trim(jrCrnSch.getFieldString("YD_DN_WO_LOC_ZAXIS"));
		    	String dlLocChkRst 	= commUtils.trim(jrCrnSch.getFieldString("DL_LOC_CHK_RST"));
		    	String dlLocChk 	= commUtils.trim(jrCrnSch.getFieldString("DL_LOC_CHK"));
			    ydStkColDirGp		= commUtils.trim(jrCrnSch.getFieldString("YD_STK_COL_DIR_GP"));
			    MaxMtlLen			= commUtils.trim(jrCrnSch.getFieldString("MAX_MTL_L"));

			    if ("UP".equals(dlLocChkRst)) {
					throw new Exception("해당위치 작업중인  재료가 적치되어 있습니다.");
				}
			    if ("N".equals(dlLocChk)) {
					throw new Exception("해당위치 적치 불가 합니다.");
				}

			    //혹시 권하지시위치가 잘못 등록되어 있으면
			    if (ydDnWoLocOld.length() != 8) {
			    	ydDnWoLocOld = "XX010101";
				}
			    
			    /*  if((ydEqpId.substring(0,4).equals("KASC")) 
			    		 && (!ydDnWoLocOld.substring(0,6).equals(ysDnWoLoc.substring(0,6))) 
			    		 &&  (!ydDnWoLocOld.substring(0,2).equals("XX")) ){
			    	
			    	throw new Exception("자동화 창고는 동일 열만 가능합니다.");
			    }
			    */
			    
			    /*
			    if((ydEqpId.substring(0,4).equals("KDSC"))) {
			    	if (ydEqpId.equals("KDSC01") && !(ysDnWoLoc.substring(4,2).equals("01") || ysDnWoLoc.substring(4,2).equals("02"))) {
			    		throw new Exception("1열/2열만 가능합니다.");
			    	} else if (ydEqpId.equals("KDSC02") && !(ysDnWoLoc.substring(4,2).equals("03") || ysDnWoLoc.substring(4,2).equals("04"))) {
			    		throw new Exception("3열/4열만 가능합니다.");
			    	} else if (ydEqpId.equals("KDSC03") && !(ysDnWoLoc.substring(4,2).equals("05") || ysDnWoLoc.substring(4,2).equals("06"))) {
			    		throw new Exception("5열/6열만 가능합니다.");
			    	} else if (ydEqpId.equals("KDSC04") && !(ysDnWoLoc.substring(4,2).equals("07") || ysDnWoLoc.substring(4,2).equals("08"))) {
			    		throw new Exception("7열/8열만 가능합니다.");
			    	} else if (ydEqpId.equals("KDSC05") && !(ysDnWoLoc.substring(4,2).equals("09"))) {
			    		throw new Exception("9열만 가능합니다.");
			    	} else if (ydEqpId.equals("KDSC06") && !(ysDnWoLoc.substring(4,2).equals("10") || ysDnWoLoc.substring(4,2).equals("11"))) {
			    		throw new Exception("10열/11열만 가능합니다.");
			    	}			    	
			    }*/
		    }
			
			if(ysDnWoLoc.startsWith("KA")||ysDnWoLoc.startsWith("KB")) {
				
				
				if(ysDnWoLoc.substring(2, 4).equals("TR")||ysDnWoLoc.substring(2, 4).equals("TC")||ysDnWoLoc.substring(2, 4).equals("TS")) {
					
				} else if(ydStkColDirGp.equals("X") && ydEqpId.substring(2, 4).equals("CR")) {
					ydDnWoLocXaxis = String.valueOf(Integer.parseInt(ydDnWoLocXaxis) + (Integer.parseInt(MaxMtlLen) /2)) ;
				} else if(ydStkColDirGp.equals("Y") && ydEqpId.substring(2, 4).equals("CR")) {
					ydDnWoLocYaxis = String.valueOf(Integer.parseInt(ydDnWoLocYaxis) + (Integer.parseInt(MaxMtlLen) /2)) ;
				} else {

				}				
				
				
			}	
			
//12.04			
			//// 선재자동창고 는 TO위치에 따라  설비명, 스케쥴코드 변경처리 됨   
			if(ysStkColGp.startsWith("KD0") && ydSchCd.substring(0, 4).equals("KDHS")) {
				if((ysStkColGp.equals("KD0101"))||(ysStkColGp.equals("KD0102"))){
					ydSchCd = "KDHS01"+ ydSchCd.substring(6, 8);
					ydEqpId = "KDSC01";
				} else if((ysStkColGp.equals("KD0103"))||(ysStkColGp.equals("KD0104"))){
					ydSchCd = "KDHS02"+ ydSchCd.substring(6, 8);
					ydEqpId = "KDSC02";
				} else if((ysStkColGp.equals("KD0105"))||(ysStkColGp.equals("KD0106"))){
					ydSchCd = "KDHS03"+ ydSchCd.substring(6, 8);
					ydEqpId = "KDSC03";
				} else if((ysStkColGp.equals("KD0107"))||(ysStkColGp.equals("KD0108"))){
					ydSchCd = "KDHS04"+ ydSchCd.substring(6, 8);
					ydEqpId = "KDSC04";
				} else if((ysStkColGp.equals("KD0109"))){
					ydSchCd = "KDHS05"+ ydSchCd.substring(6, 8);
					ydEqpId = "KDSC05";
				} else if((ysStkColGp.equals("KD0110"))||(ysStkColGp.equals("KD0111"))){
					ydSchCd = "KDHS06"+ ydSchCd.substring(6, 8);
					ydEqpId = "KDSC06";
				}				
			}	
			
			/* 
			//권하위치변경(권상상태) 삭제 처리 - 12.16
			if (ydEqpId.substring(0,4).equals("KDSC") && CurrydEqpId.equals(ydEqpId)) { // Stk 설비명 동일
				if (!ydWrkProgStat.equals("2")) { // 권상상태에서만 수정 가능
					throw new Exception("권상 상태에서만 수정이 가능합니다.");
				}
			}
			*/
			
			/**********************************************************
			* 2. 권하지시위치 수정
			**********************************************************/
			jrParam.setField("YD_STK_COL_GP_OLD" , ydDnWoLocOld.substring(0, 6));
			jrParam.setField("YD_STK_BED_NO_OLD" , ydDnWoLocOld.substring(6, 8));
			jrParam.setField("YD_STK_LYR_NO_OLD" , ydDnWoLayerOld);
			jrParam.setField("YD_STK_COL_GP_NEW" , ysStkColGp    );
			jrParam.setField("YD_STK_BED_NO_NEW" , ysStkBedNo    );
			jrParam.setField("YS_DN_WO_LOC"      , ysDnWoLoc.substring(0, 8)     );
			jrParam.setField("YS_DN_WO_LAYER"    , ydDnWoLayer   );
			jrParam.setField("YS_STK_BED_NO"     , ysStkBedNo);
			jrParam.setField("YS_STK_LYR_NO"     , ydDnWoLayer   );
			jrParam.setField("YD_DN_WO_LOC_XAXIS", ydDnWoLocXaxis);
			jrParam.setField("YD_DN_WO_LOC_YAXIS", ydDnWoLocYaxis);
			jrParam.setField("YD_DN_WO_LOC_ZAXIS", ydDnWoLocZaxis);
			
			jrParam.setField("YD_EQP_ID", ydEqpId);
			jrParam.setField("YD_SCH_CD", ydSchCd);
			
			
			
			//신규 적치단 재료정보READ
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnWekMtlByschidGds 
			SELECT A.YD_CRN_SCH_ID
			     , A.SSTL_NO         -- 기존 재료정보 
			     ,  CASE WHEN SUBSTR(:V_YS_STK_COL_GP,1,3) IN ('KA0','KD0') THEN 
			               (SELECT NVL(MAX(YS_STK_SEQ_NO),0) + 1 AS MAX_YS_STK_SEQ_NO 
			                  FROM TB_YS_STKLYR 
			                 WHERE SSTL_NO = A.SSTL_NO
			                   AND YD_STK_LYR_MTL_STAT IN ('C','U')
			               )
			             ELSE 
			               (  SELECT YS_STK_SEQ_NO
						          FROM TB_YS_STKLYR 
						         WHERE SSTL_NO = A.SSTL_NO
						           AND YD_STK_LYR_MTL_STAT IN ('C','U')
			               ) 
			             END   AS YS_STK_SEQ_NO     --신규 위치에 SEQ_NO   
			  FROM TB_YS_CRNWRKMTL A
			 WHERE A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			   AND A.DEL_YN = 'N'
			*/	   
			
			JDTORecordSet jsCrnSchMtl = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnWekMtlByschidGds", logId, methodNm, "기존권하위치 조회");
			

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
		    	recInTemp.setField("MODIFIER"       		, modifier);
		    	
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
				
				/**********************************************************
				* 2.1 권하위치 변경시 권하위치가 TS 이고 입고 인 경우 
				* 입고예정 위치를 다시 변경 처리 해 줘야 한다.
				**********************************************************/
				
				if(ysDnWoLoc.startsWith("KATS0")) {
					JDTORecord  jrParam1= JDTORecordFactory.getInstance().create();
					
					jrParam1.setResultCode(logId);	//Log ID
					jrParam1.setResultMsg(methodNm);	//Log Method Name						
					jrParam1.setField("SSTL_NO"				,szSSTL_NO);
					jrParam1.setField("YD_RCPT_PLN_STR_LOC"	,ChgYdRcptPlnStrLoc);
					jrParam1.setField("MODIFIER"			,modifier);
					
					EJBConnector tranConn = new EJBConnector("default", "GdsYsSchSeEJB", this);
					tranConn.trx("updStock", new Class[] { JDTORecord.class }, new Object[] { jrParam1 });
				}
			}
	
			//적치단 수정 - 기존 및 신규 권하지시위치
//			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnSchDnWoLocStkLyr", logId, methodNm, "TB_YS_STKLYR");				

			//적치단 수정 - 기존
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdByCrnSchIdGds
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
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdByCrnSchIdGds", logId, methodNm, "기존권하위치 CLEAR");	
			
			
			
			//적치Bed 수정 - 완산Bed 해제
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnSchDnWoLocStkBed", logId, methodNm, "TB_YS_STKBED");				

			//크레인스케줄 수정 - 권상, 권하지시위치
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnSchDnWoLocCrnSch", logId, methodNm, "TB_YS_CRNSCH");
			

			
			//기존 대차, 차량 권하위치에서 일반야드로 변경 시 대차 or 차량스케줄 작업예약ID 삭제
			String ydDnWoLocOld1 = ydDnWoLocOld.substring(2, 4);
			if (("TC".equals(ydDnWoLocOld1) || "PT".equals(ydDnWoLocOld1)) && !ydDnWoLocOld.equals(ysDnWoLoc.substring(2, 4))) {
				if ("TC".equals(ydDnWoLocOld1)) {
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
			
			//설비명이 바뀌면 작업예약까지 변경처리 해야 함
			if(!CurrydEqpId.equals(ydEqpId)) {
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdWbookSchCd 
				UPDATE TB_YS_WRKBOOK
				   SET YD_SCH_CD = :V_YD_SCH_CD
				 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
				   AND DEL_YN = 'N'
				 */		   
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdWbookSchCd", logId, methodNm, "TB_YS_WRKBOOK");				
			}
	
			commUtils.printLog(logId, "[" + methodNm + "] 적치단[" + ydDnWoLocOld+ "/" + ydDnWoLayerOld+ "]비활성화", "SL");
			//변경전 위치 가 1단인 경우 적치단 비활성화		
			if(!ydDnWoLocOld.equals("")) {
				if (ydDnWoLocOld.matches("[K][B]\\d\\d\\d\\d\\d\\d") && ydDnWoLayerOld.equals("01")) { 	
					jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
					jrParam.setField("YS_STK_COL_GP", ydDnWoLocOld.substring(0, 6)); //야드적치열구분
					jrParam.setField("YS_STK_BED_NO", ydDnWoLocOld.substring(6, 8)); //야드적치Bed번호
					
					commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updStkBedActStatReSet", logId, methodNm, "적치Bed 활성상태 초기화");
				}
			}
			
			//변경후 위치 가 1단인 경우 적치단 활성화
			commUtils.printLog(logId, "[" + methodNm + "] 적치단[" + ysDnWoLoc+ "/" + ydDnWoLayer+ "]활성화", "SL");
			if(!ysDnWoLoc.equals("")) {
				if (ysDnWoLoc.matches("[K][B]\\d\\d\\d\\d\\d\\d") && ydDnWoLayer.equals("01")) { 	
					jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
					jrParam.setField("YS_STK_COL_GP", ysDnWoLoc.substring(0, 6)); //야드적치열구분
					jrParam.setField("YS_STK_BED_NO", ysDnWoLoc.substring(6, 8)); //야드적치Bed번호
					
					commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updStkBedActStat", logId, methodNm, "적치Bed 활성상태 변경");
				}			
			}
			
			/**********************************************************
			* 3. 크레인작업지시요구 전문 조회
			**********************************************************/
			JDTORecord jrRtn 	= null;
			String chk 			= "Y"; 
			String sCurrSchId 	= "";
			commUtils.printLog(logId, "윤재광1 :ydWrkProgStat " + ydWrkProgStat+ "/" + ydEqpId, "SL");
			//일반창고
			if("CR".equals(ydEqpId.substring(2,4))|| "B".equals(ydEqpId.substring(1,2))||"E".equals(ydEqpId.substring(1,2))){
				
				//크레인 상태 확인
				jrParam.setField("YD_EQP_ID", ydEqpId); //야드설비ID
				JDTORecordSet jsChk = null;
				jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStatEqp", logId, methodNm, "설비상태조회");
				 
				if (jsChk.size() > 0) {
					ydWrkProgStat = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_STAT"));
					sCurrSchId	  = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_CRN_SCH_ID"));
				}
 				
				//선택이 안된 상태 인 경우  (대기)
				if("W".equals(ydWrkProgStat)){
					chk ="N";
				}else{
					if(ydCrnSchId.equals(sCurrSchId)) chk ="Y";
					else chk ="N";
				}	
			}
			commUtils.printLog(logId, "윤재광2 :ydWrkProgStat " + ydWrkProgStat+ "/" + sCurrSchId+ "/" + chk, "SL");
			//자동화 창고인 경우 무조건 전송 한다.(단 일반창고는 상태에 따라 전송)
			if("Y".equals(chk)){
			
				//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
				JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, modifier);
				
				jrYdMsg.setResultCode(logId);		//Log ID
				jrYdMsg.setResultMsg(methodNm);	//Log Method Name
				jrYdMsg.setField("MSG_GP"	, "U"   	); //전문구분
				jrYdMsg.setField("INFO_GP"	, "I"   	); //정보구분
				jrYdMsg.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
				
				if(ydEqpId.startsWith("KACRA2")){
		    		jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YSN4L003", jrYdMsg));
					
		    	}else if(ydEqpId.startsWith("KATC")){
		    		jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YSN4L003", jrYdMsg));
					
		    	}else if(ydEqpId.startsWith("KASC")){
					jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YSN6L006", jrYdMsg));
					
		    	}else if(ydEqpId.startsWith("KA")){
					jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YSN6L003", jrYdMsg));
					
		    	}else if(ydEqpId.startsWith("KB")){
		    		jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YSN4L003", jrYdMsg));
					
		    	}else if(ydEqpId.startsWith("KDSC")){
		    		jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YSN5L006", jrYdMsg));
				
		    	}else if(ydEqpId.startsWith("KD")){
		    		jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YSN5L003", jrYdMsg));
				
		    	}else if(ydEqpId.startsWith("KE")){
		    		jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YSN3L003", jrYdMsg));
					
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
	 *      [A] 오퍼레이션명 : 크레인작업관리 작업취소
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updCraneWrkCancel(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리 작업취소[GdsYsJspSeEJB.updCraneWrkCancel] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;
			JDTORecord recPara = null; 
			String ydCrnSchId = ""; //야드크레인스케쥴ID
			String ydWbookId  = ""; //야드작업예약ID
		    String ydEqpId    = ""; //야드설비ID
		    String ydSchCd    = ""; //야드스케쥴코드
		    String ydWrkProgStat    = ""; 
			
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
			    ydWrkProgStat = commUtils.trim(gdReq.getHeader("YD_WRK_PROG_STAT").getValue(ii));
			    ydCrnSchId = commUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID").getValue(ii));

				commUtils.printLog(logId, "스케줄 진행 [ " + ydWrkProgStat  +" ]", "SL");


				if(ydEqpId.substring(2, 4).equals("SC") && ydWrkProgStat.equals("W")){
	    			jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setField("MSG_GP"			, "D"   	); //전문구분
					jrParam.setField("YD_CRN_SCH_ID"	, ydCrnSchId );
			
					if(ydEqpId.substring(1, 2).equals("D")){
						jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YSN5L006", jrParam));	//선재		
					} else {
						jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YSN6L006", jrParam));			
					} 	
				}

				
			    //작업할  야드작업예약ID가 작업 완료한 야드작업예약ID에 있으면 이전에 모두 처리되었으므로 Skip
				if (commUtils.chkExist(arrYdWbookId, ydWbookId)) { continue; }
				arrYdWbookId[ii] = ydWbookId;

				//기본정보조회
				jrParam.setField("YD_WBOOK_ID", ydWbookId);


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
				
				if(ydEqpId.substring(2, 4).equals("SC")){
//					for(int jj = 1; jj <= jsCrnSch.size(); jj++) {
//						jsCrnSch.absolute(jj);
//		    			recPara = jsCrnSch.getRecord();		
//		    			
//		    			jrParam = JDTORecordFactory.getInstance().create();
//
//		    			jrParam.setResultCode(logId);	//Log ID
//		    			jrParam.setResultMsg(methodNm);	//Log Method Name
//						jrParam.setField("MSG_GP"	, "D"   	); //전문구분
//						jrParam.setField("YD_CRN_SCH_ID"	   , commUtils.trim(recPara.getFieldString("YD_CRN_SCH_ID")) );
//				
//						if(ydEqpId.substring(1, 2).equals("D")){
//							jrRtn = commUtils.addSndData(jrRtn,commDao.get MsgL2("YSN5L006", jrParam));	//선재		
//						} else {
//							jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YSN6L006", jrParam));			
//						}
//					}	
				} else {
					
					/**********************************************************
					* 5. 크레인작업지시요구 전문 조회
					**********************************************************/
					//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
					JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name
					/** 1:블름,2:빌렛,3:봉강,4:선재	 */
					
					jrYdMsg.setField("JMS_TC_CD"       , YsConstant.YSYSJ001   );	//크레인작업지시요구
					jrYdMsg.setField("YD_EQP_ID"       , ydEqpId      );	//야드설비ID
					jrYdMsg.setField("YD_WRK_PROG_STAT", "4");				//야드작업진행상태
					jrYdMsg.setField("YD_SCH_CD"       , ydSchCd      );	//야드스케쥴코드
					jrYdMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId      );	//야드스케쥴ID
		
					jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
				}	
				
				
				
//				/**********************************************************
//				* 5. 크레인작업지시요구 전문 조회
//				**********************************************************/
//				//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
//				JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, modifier);
	//
//				jrYdMsg.setResultCode(logId);	//Log ID
//				jrYdMsg.setResultMsg(methodNm);	//Log Method Name
//				jrYdMsg.setField("JMS_TC_CD"       , YsConstant.N1YSL004);	//크레인작업지시요구
//				jrYdMsg.setField("YD_EQP_ID"       , ydEqpId   );	//야드설비ID
//				jrYdMsg.setField("YD_WRK_PROG_STAT", "4"       );	//야드작업진행상태(권하완료)
//				jrYdMsg.setField("YD_SCH_CD"       , ydSchCd   );	//야드스케쥴코드
//				jrYdMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId);	//야드크레인스케쥴ID
	//
//				EJBConnector sndConn = new EJBConnector("default", "BlYsL2RcvSeEJB", this);
//				jrRtn = (JDTORecord)sndConn.trx("rcvN1YSL004", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
								
				
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
	 *      [A] 오퍼레이션명 : 크레인스케줄 취소처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtCrnSchCncl(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인스케줄 취소처리[GdsYsJspSeEJB.trtCrnSchCncl] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			String ydCrnSchId = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID")); //야드크레인스케쥴ID
			String ydWbookId  = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"  )); //야드작업예약ID
			String ydSchCd  = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"  )); //야드스케쥴
			
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
			JDTORecordSet jsCrnSch = GdsYsDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnWrkMgtSCSch", logId, methodNm, "크레인작업지시read");
			if (jsCrnSch == null || jsCrnSch.size() <= 0) {
				throw new Exception("크레인스케쥴ID[" + ydCrnSchId + "]의 크레인스케줄 정보가 존재하지 않습니다.");
		    }
			
			JDTORecord jrCrnSch = jsCrnSch.getRecord(0);
			
		    String ydWrkProgStat = commUtils.trim(jrCrnSch.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
		    String eqpUpdYn      = commUtils.trim(jrCrnSch.getFieldString("EQP_UPD_YN"      )); //설비상태수정여부
		    String ydEqpId       = commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"       )); //야드설비ID
		    String ydEqpStat     = commUtils.trim(jrCrnSch.getFieldString("YD_EQP_STAT"     )); //야드설비상태
		    String ydTO_LOC      = commUtils.trim(jrCrnSch.getFieldString("TO_LOC"     )); //TO 위치

		    if ("2".equals(ydWrkProgStat)) {
				//throw new Exception("크레인스케줄 [" + ydCrnSchId + "]의 작업진행상태가 [2:권상완료]이므로 취소하실 수 없습니다.");
			} else if ("3".equals(ydWrkProgStat)) {
				throw new Exception("크레인스케줄 [" + ydCrnSchId + "]의 작업진행상태가 [3:권하지시]이므로 취소하실 수 없습니다.");
			} else if ("4".equals(ydWrkProgStat)) {
				throw new Exception("크레인스케줄 [" + ydCrnSchId + "]의 작업진행상태가 [4:권하완료]이므로 취소하실 수 없습니다.");
			}
			
			if (ydSchCd.substring(0, 4).equals("KDHS")) {						
				jrParam.setField("YD_SCH_CD"  , ydSchCd );
				
				JDTORecordSet stkLyr = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdLyrChStl"
						, logId, methodNm, "작업재료 위치조회 (CH)");
				
				if (stkLyr.size() != 0) {
					throw new Exception("입고진행중인 선재는 취소하실 수 없습니다.");
				}	
				
				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
				jrParam.setField("MSG_GP"       , "D"       ); //전문구분(취소)
				jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN5L006", jrParam));				
			}
		    
			/**********************************************************
			* 2. 작업진행상태가 [1:권상지시] 이면 작업지시취소 전문 전송
			**********************************************************/
			if ("1".equals(ydWrkProgStat)) {	
								
				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
				jrParam.setField("MSG_GP"       , "D"       ); //전문구분(취소)

				//크레인작업지시(YDY1L004, YDY3L004) 전문 조회
				String szJMS_TC_CD = "";
				
				if(ydEqpId.startsWith("KACRA2")){
		    		szJMS_TC_CD = "YSN4L003";
		    	}else if(ydEqpId.startsWith("KATC")){
		    		szJMS_TC_CD = "YSN4L003";
				}else if(ydEqpId.startsWith("KASC")){
		    		szJMS_TC_CD = "YSN6L006";
		    	}else if(ydEqpId.startsWith("KA")){
		    		szJMS_TC_CD = "YSN6L003";
		    	}else if(ydEqpId.startsWith("KB")){
		    		szJMS_TC_CD = "YSN4L003";
		    	}else if(ydEqpId.startsWith("KDSC")){
		    		szJMS_TC_CD = "YSN5L006";
		    	}else if(ydEqpId.startsWith("KD")){
		    		szJMS_TC_CD = "YSN5L003";
		    	}else if(ydEqpId.startsWith("KE")){
		    		szJMS_TC_CD = "YSN3L003";
		    	}					
				
				
				jrRtn = commUtils.addSndData(commDao.getMsgL2(szJMS_TC_CD, jrParam));
			}			

			/**********************************************************
			* 3. 권상, 권하위치 원복 - 적치단, 적치Bed
			**********************************************************/
			//적치단 수정 - 권상위치(U -> C), 권하위치(D -> E)
       
			if(ydEqpId.startsWith("KDSC") && ( ydSchCd.substring(2,4).equals("HS") && (ydSchCd.substring(6,7).equals("L")))){
				// 선재 입고 작업 인 경우 
				// 취소시 정보 틀림
				GdsYsDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDao.updCrnWrkMgtSCStkLyrD", logId, methodNm, "TB_YS_STKLYR");				
			} else {
				//적치단 수정 - 권상위치(U -> C), 권하위치(D -> E)
				GdsYsDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDao.updCrnWrkMgtSCStkLyr", logId, methodNm, "TB_YS_STKLYR");				
			}	
			//적치Bed 수정 - 완산Bed 해제
			GdsYsDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDao.updCrnWrkMgtSCStkBed", logId, methodNm, "TB_YS_STKBED");				
			
			/**********************************************************
			* 4. 크레인스케줄 삭제
			**********************************************************/
			//크레인작업재료 삭제
			GdsYsDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnWrkMgtSCCrnMtl", logId, methodNm, "TB_YS_CRNWRKMTL");				
			
			//크레인스케줄 삭제
			GdsYsDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnWrkMgtSCCrnSch", logId, methodNm, "TB_YS_CRNSCH");				

			/**********************************************************
			* 5. 설비상태 수정 - 크레인이 고장 또는 Off-Line이 아니고 상태가 다르면
			**********************************************************/
			if ("Y".equals(eqpUpdYn)) {
				jrParam.setField("YD_EQP_ID"  , ydEqpId  ); //야드설비ID
				jrParam.setField("YD_EQP_STAT", ydEqpStat); //야드설비상태

				GdsYsDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatEqp", logId, methodNm, "TB_YD_EQP");				
			}
			
			commUtils.printLog(logId, "ydTO_LOC" + ydTO_LOC, "SL");
			
			// 제품창고(K) B동 일반야드에서 권상시 Bed 활성상태 최기화 (Query에서 해당 bed에 적치된 제품이 하나도 없을 때 update 한다.) 
			if(!ydTO_LOC.equals("")) {
				if (ydTO_LOC.matches("[K][B]\\d\\d\\d\\d\\d\\d\\d\\d") && ydTO_LOC.substring(8, 10).equals("01")) { 	
					jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
					jrParam.setField("YS_STK_COL_GP", ydTO_LOC.substring(0, 6)); //야드적치열구분
					jrParam.setField("YS_STK_BED_NO", ydTO_LOC.substring(6, 8)); //야드적치Bed번호
					
					commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updStkBedActStatReSet", logId, methodNm, "적치Bed 활성상태 초기화");
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
	 *      [A] 오퍼레이션명 : 작업예약 취소처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtWrkBookCncl(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "작업예약 취소처리[GdsYsJspSeEJB.trtWrkBookCncl] < " + rcvMsg.getResultMsg();
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
			JDTORecord sndRecord = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, modifier);

			jrParam.setField("YD_WBOOK_ID", ydWbookId);
			
			/**********************************************************
			* 1. 크레인스케줄 존재여부 Check
			**********************************************************/

			JDTORecordSet jsCrnSch = GdsYsDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCommWbCrnSch", logId, methodNm, "크레인작업지시read");
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
			GdsYsDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCommPrepMtlRcvr", logId, methodNm, "TB_YS_PREPMTL");	

			/* com.inisteel.cim.ys.common.dao.YsCommDAO.updCommPrepSchRcvr 
			--준비스케줄 복원 - 
			UPDATE TB_YS_PREPSCH
			   SET MODIFIER    = :V_MODIFIER
			      ,MOD_DDTT    = SYSDATE
			      ,DEL_YN      = 'N'
			      ,YD_WBOOK_ID = NULL
			 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
			*/ 
			GdsYsDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCommPrepSchRcvr", logId, methodNm, "TB_YS_PREPSCH");	
//			//준비스케줄 복원			
			/**********************************************************
			* 3. 차량/대차스케줄 야드상차작업예약ID, 야드하차작업예약ID 삭제
			**********************************************************/
			//차량스케줄 야드상차작업예약ID, 야드하차작업예약ID 삭제
			GdsYsDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCommCarSchWbDel", logId, methodNm, "TB_YS_CARSCH");				
		
			//대차스케줄 야드상차작업예약ID, 야드하차작업예약ID 삭제
			GdsYsDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCommTcarSchWbDel", logId, methodNm, "TB_YS_TCARSCH");				

		    /**********************************************************
			* 4. 작업예약/재료 삭제
			**********************************************************/
			//작업예약재료 삭제
			GdsYsDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnWrkBookMtl", logId, methodNm, "TB_YS_WRKBOOKMTL");				

			//작업예약 삭제
			GdsYsDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnWrkBook", logId, methodNm, "TB_YS_WRKBOOK");				
			
			
//			if(ydEqpId.substring(2, 4).equals("SC")){
//				
//			} else {
//				
//				/**********************************************************
//				* 5. 크레인작업지시요구 전문 조회
//				**********************************************************/
//				//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
//				JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, modifier);
//				jrYdMsg.setResultCode(logId);	//Log ID
//				jrYdMsg.setResultMsg(methodNm);	//Log Method Name
//				/** 1:블름,2:빌렛,3:봉강,4:선재	 */
//				
//				jrYdMsg.setField("JMS_TC_CD"       , YsConstant.YSYSJ001   );	//크레인작업지시요구
//				jrYdMsg.setField("YD_EQP_ID"       , ydEqpId      );	//야드설비ID
//				jrYdMsg.setField("YD_WRK_PROG_STAT", "4");				//야드작업진행상태
//				jrYdMsg.setField("YD_SCH_CD"       , ydSchCd      );	//야드스케쥴코드
//				jrYdMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId      );	//야드스케쥴ID
//				jrYdMsg.setField("MODIFIER"      , modifier     );	//수정자
//	
//				sndRecord = commUtils.addSndData(sndRecord, jrYdMsg);
//			}	
			return sndRecord;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
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
	public JDTORecord updCraneSchCancel(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리 스케줄취소[GdsYsJspSeEJB.updCraneSchCancel] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			String ydCrnSchId = ""; //야드크레인스케쥴ID
			String ydWbookId  = ""; //야드작업예약ID
			String ydEqpId  = ""; 
			String ydSchCd  = ""; 
			String ydWrkProgStat  = "";
			
			
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
				ydEqpId = commUtils.trim(gdReq.getHeader("YD_EQP_ID").getValue(ii));
				ydSchCd = commUtils.trim(gdReq.getHeader("YD_SCH_CD").getValue(ii));
				ydWrkProgStat = commUtils.trim(gdReq.getHeader("YD_WRK_PROG_STAT").getValue(ii));

				commUtils.printLog(logId, "스케줄 진행 [ " + ydWrkProgStat  +" ]", "SL");

			    //작업할  야드작업예약ID가 작업 완료한 야드작업예약ID에 있으면 이전에 모두 처리되었으므로 Skip
				if(ydEqpId.substring(2, 4).equals("SC") && ydWrkProgStat.equals("W")){
	    			jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setField("MSG_GP"			, "D"   	); //전문구분
					jrParam.setField("YD_CRN_SCH_ID"	, ydCrnSchId );
			
					if(ydEqpId.substring(1, 2).equals("D")){
						jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YSN5L006", jrParam));	//선재		
					} else {
						jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YSN6L006", jrParam));			
					} 	
				}
				
				if (commUtils.chkExist(arrYdWbookId, ydWbookId)) { continue; }
				arrYdWbookId[ii] = ydWbookId;

				commUtils.printLog(logId, "스케줄취소 [ " + ydWbookId + " - " + ydCrnSchId + " ]", "SL");

				jrParam.setField("YD_WBOOK_ID"  , ydWbookId );
				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);
				jrParam.setField("YD_SCH_CD"    , ydSchCd   );

				/**********************************************************
				* 1. 크레인스케줄 취소
				**********************************************************/
				jrRtn = commUtils.addSndData(jrRtn, this.trtCrnSchCncl(jrParam));
			}
			
			/**********************************************************
			* 9. 봉강자동화 크레인스케줄 취소 일괄 전송
			**********************************************************/
			/*
			if(ydEqpId.substring(2, 4).equals("SC") && ydEqpId.substring(1, 2).equals("A")){
				
				Vector vWb = new Vector();
				
				String sOldWbookId = "";
				String sNewWbookId = "";
				
				for (int ii = 0; ii < rowCnt; ii++) {
					
					sNewWbookId  = commUtils.trim(gdReq.getHeader("YD_WBOOK_ID"  ).getValue(ii));
					
					if("".equals(sOldWbookId)||!sOldWbookId.equals(sNewWbookId)){
						sOldWbookId = sNewWbookId;
						vWb.add(sNewWbookId);
					}
				}
				
				String sWbookId = "";
				
				for (int ii = 0; ii < vWb.size(); ii++) {
					sWbookId = (String) vWb.get(ii);
					
					commUtils.printLog(logId, "스케줄 일괄취소 진행 [ " + sWbookId  +" ]", "SL");
					
					JDTORecord jrRtn1 = JDTORecordFactory.getInstance().create();
					JDTORecord jrRtn2 = JDTORecordFactory.getInstance().create();
					jrRtn1.setField("YD_WBOOK_ID" ,sWbookId ); //야드작업예약ID
					
					JDTORecordSet jsCrnSch = GdsYsDao.select(jrRtn1, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCommWbDelCrnSch", logId, methodNm, "크레인작업지시read");
					
					for (int jj = 0; jj < jsCrnSch.size(); jj++) {
						jrRtn2 = jsCrnSch.getRecord(jj);
						
						jrParam = JDTORecordFactory.getInstance().create();
						jrParam.setField("MSG_GP"			, "D"   	); //전문구분
						jrParam.setField("YD_CRN_SCH_ID"	, commUtils.trim(jrRtn2.getFieldString("YD_CRN_SCH_ID")) );
						
						jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YSN6L006", jrParam));		
					}
				}
			}
			*/
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	/**
	 *      [A] 오퍼레이션명 :크레인작업관리PP-설비 고장/정상 설정
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updCrnStsSetCrnStat(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리PP-설비 고장/정상 설정[GdsYsJspSeEJB.updCrnStsSetCrnStat] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String szYD_EQP_ID = "";
		String szYD_EQP_STAT = "";
		String szYD_EQP_STAT_Temp = "";
		String szYD_EQP_STAT_Comp = "";
		JDTORecord recEqpInfo = null;
		JDTORecord sndRecord = null;

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
		
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			for (int ii = 0; ii < rowCnt; ii++) {
				
				szYD_EQP_ID  	= commUtils.trim(gdReq.getHeader("YD_EQP_ID"  ).getValue(ii));
				szYD_EQP_STAT  	= commUtils.trim(gdReq.getHeader("YD_EQP_STAT"  ).getValue(ii));

				if(szYD_EQP_ID.equals("")){
					commUtils.printLog(logId, "[설비ID값이 없습니다]", "SL");
					continue ;
				}
				if(szYD_EQP_STAT.equals("")){
					commUtils.printLog(logId, "[설비상태 값이 없습니다]", "SL");
					continue ;
				}

				jrParam.setField("MSG_ID"   , "YD_JSP");
				jrParam.setField("YD_EQP_ID", szYD_EQP_ID);

				if(szYD_EQP_STAT.equals("W")){  				//크레인이 IDLE인 상태 - 스케줄수행대기
					
					szYD_EQP_STAT_Temp = "N";
					jrParam.setField("YD_EQP_PAUSE_CODE", "0000");
					
				}else if(szYD_EQP_STAT.equals("B")){
					
					szYD_EQP_STAT_Temp = "B";
					jrParam.setField("YD_EQP_PAUSE_CODE", "STOP");
					
				} else{
					
					szYD_EQP_STAT_Temp = "N";
					jrParam.setField("YD_EQP_PAUSE_CODE", "0000");
				}


				//------------------------------------------------------------------------------------------------
				// 현 DB 정보와 CHECK  : 처리사유는 현재 설비상태가 진도코드와 중복하여 사용하므로
				//                       정상인 상태에서 두번처리하여 진도코드가 초기화 될 우려가 있음
				// 1. 고장이고 넘겨온 정보가 고장일때 => 처리할 필요없음
				// 2. 고장이 아니고 넘겨온 정보가 고장이 아닐때 => 처리할 필요없음
				//------------------------------------------------------------------------------------------------

				recEqpInfo = JDTORecordFactory.getInstance().create();
				//rsEqpInfo = JDTORecordFactory.getInstance().createRecordSet("rsEqpInfo");

				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdEqp 
				SELECT 
				    YD_EQP_ID AS YD_EQP_ID
				    ,REGISTER AS REGISTER
				    ,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
				    ,MODIFIER AS MODIFIER
				    ,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT
				    ,DEL_YN AS DEL_YN
				    ,YD_GP AS YD_GP
				    ,YD_BAY_GP AS YD_BAY_GP 
				    ,YD_EQP_GP AS YD_EQP_GP
				    ,YD_EQP_NO AS YD_EQP_NO
				    ,YD_EQP_NAME AS YD_EQP_NAME
				    ,YD_EQP_STAT AS YD_EQP_STAT
				    ,YD_EQP_WRK_MODE AS YD_EQP_WRK_MODE
				    ,YD_CURR_BAY_GP AS YD_CURR_BAY_GP
				    ,YD_HOME_BAY_GP AS YD_HOME_BAY_GP
				FROM TB_YS_EQP
				WHERE YD_EQP_ID = :V_YD_EQP_ID
				    AND DEL_YN='N'
				*/ 	
				
				JDTORecordSet rsEqpInfo = GdsYsDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdEqp", logId, methodNm, "크레인작업지시read");
				if (rsEqpInfo == null || rsEqpInfo.size() < 0) {				
					throw new Exception("설비현황[" + szYD_EQP_ID + "]의 장비현황 정보가 " + rsEqpInfo.size() + " 건 존재합니다.");
			    }				

				rsEqpInfo.first();
				recEqpInfo = rsEqpInfo.getRecord();

				szYD_EQP_STAT_Comp = commUtils.trim(recEqpInfo.getFieldString("YD_EQP_STAT"  )); //야드상태


				if(szYD_EQP_STAT_Comp.equals("B") && szYD_EQP_STAT_Temp.equals("B")){
					throw new Exception("변경된 내용이 없습니다");

				}
				
				/****************************************************************
				 * L2에서 올라오는 고장 복구  로직 사용함 (GdsYsL2RcvSeEJB)
				 ****************************************************************/
				String L2_MC = commUtils.getYsGpBayToL2(szYD_EQP_ID);     	// L2구분  RETURN;
				String L2Ejb = commUtils.getYsGpBayToL2Ejb(szYD_EQP_ID);  	// EJB명    RETURN;

				jrParam.setField("JMS_TC_CD"       	, L2_MC +"YSL003"); 	// 명령선택
				jrParam.setField("YD_EQP_STAT"        , szYD_EQP_STAT_Temp);
				jrParam.setField("YD_EQP_TRBL_RCVR_DT", commUtils.getDateTime14());
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				
				EJBConnector sndConn = new EJBConnector("default",L2Ejb, this);
				JDTORecord jrRtn = (JDTORecord)sndConn.trx("rcv"+ L2_MC +"YSL003", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				
				sndRecord = commUtils.addSndData(sndRecord, jrRtn);
				
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
	 * 설비 ON_LINE, OFF_LINE 설정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return String[]
	 * @throws DAOException
	 */
	public JDTORecord updCrnStsSetCrnMode(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리PP-모드변경[GdsYsJspSeEJB.updCrnStsSetCrnMode] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String szYD_EQP_ID = "";
		String szYD_EQP_WRK_MODE = "";
		JDTORecord sndRecord = null;

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
		
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			for (int ii = 0; ii < rowCnt; ii++) {
				szYD_EQP_ID  		= commUtils.trim(gdReq.getHeader("YD_EQP_ID"  ).getValue(ii));
				szYD_EQP_WRK_MODE  	= commUtils.trim(gdReq.getHeader("YD_EQP_WRK_MODE"  ).getValue(ii));
				
				//입출입  상태
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("MSG_ID"   , "YSYSJ902");
				jrParam.setField("YD_EQP_ID", szYD_EQP_ID);
				jrParam.setField("YD_EQP_WRK_MODE", szYD_EQP_WRK_MODE);

				EJBConnector sndConn = new EJBConnector("default","GdsYsL2RcvSeEJB", this);
				sndRecord = (JDTORecord)sndConn.trx("rcvYSYSJ902", new Class[] { JDTORecord.class }, new Object[] { jrParam });

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
	 * 크레인상태관리 - 명령선택기동
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return String
	 * @throws DAOException
	 */
	public JDTORecord updCmdSelStart(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리PP- 명령선택기동[GdsYsJspSeEJB.updCmdSelStart] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String szYD_EQP_ID = "";
		String szYD_EQP_WRK_MODE = "";
		String szYD_SCH_CD = "";
		String szYD_CRN_SCH_ID = "";
		JDTORecord sndRecord = null;

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
		
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			for (int ii = 0; ii < rowCnt; ii++) {
				szYD_EQP_ID  		= commUtils.trim(gdReq.getHeader("YD_EQP_ID"  ).getValue(ii));//야드설비ID
				szYD_EQP_WRK_MODE  	= commUtils.trim(gdReq.getHeader("YD_WRK_PROG_STAT"  ).getValue(ii));//야드작업진행상태
				szYD_SCH_CD  		= commUtils.trim(gdReq.getHeader("YD_SCH_CD"  ).getValue(ii));//야드스케쥴코드
				szYD_CRN_SCH_ID  	= commUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID"  ).getValue(ii));//야드크레인스케쥴ID

				/****************************************************************
				 * L2에서 올라오는 명령선택  로직 사용함 (GdsYsL2RcvSeEJB)
				 ****************************************************************/
				String L2_MC = commUtils.getYsGpBayToL2(szYD_EQP_ID);     	// L2구분  RETURN;
				String L2Ejb = commUtils.getYsGpBayToL2Ejb(szYD_EQP_ID);  	// EJB명    RETURN;

				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("JMS_TC_CD"       	, L2_MC +"YSL004"); 	// 명령선택
				jrParam.setField("YD_EQP_ID", szYD_EQP_ID);
				jrParam.setField("YD_EQP_WRK_MODE", szYD_EQP_WRK_MODE);
				jrParam.setField("YD_SCH_CD", szYD_SCH_CD);
				jrParam.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);

				
				EJBConnector sndConn = new EJBConnector("default",L2Ejb, this);
				JDTORecord jrRtn = (JDTORecord)sndConn.trx("rcv"+ L2_MC +"YSL004", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				
				sndRecord = commUtils.addSndData(sndRecord, jrRtn);
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
	 * 크레인상태관리 - 작업구분 지정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return String
	 * @throws DAOException
	 */
	public JDTORecord crnWrkGPartSet(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리PP- 작업구분 지정[GdsYsJspSeEJB.crnWrkGPartSet] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String szYD_SCH_CD = "";
		JDTORecord sndRecord = null;

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
		
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			for (int ii = 0; ii < rowCnt; ii++) {
				szYD_SCH_CD  		= commUtils.trim(gdReq.getHeader("YD_SCH_CD"  ).getValue(ii));//야드스케쥴코드

				jrParam.setField("YD_SCH_CD", szYD_SCH_CD); 
				jrParam.setField("YD_SCH_PROH_EXN", "Y"); 
				
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdSchRule", logId, methodNm, "스케줄기준 수정");
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
	 * 크레인상태관리 - 권상실적처리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return String
	 * @throws DAOException
	 */
	public JDTORecord updCrnUpPrsBackUp(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리PP-권상실적처리[GdsYsJspSeEJB.updCrnUpPrsBackUp] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String szYD_EQP_ID = "";
		String szYD_EQP_WRK_MODE = "";
		String szYD_SCH_CD = "";
		String szYD_CRN_SCH_ID = "";
		String szYD_WRK_PROG_STAT = "";
		String szYS_UP_WO_LOC = "";
		String szYS_UP_WO_LAYER = "";
		String szYD_UP_WO_LOC_XAXIS = "";
		String szYD_UP_WO_LOC_YAXIS = "";
		String szYD_UP_WO_LOC_ZAXIS = "";
		JDTORecord sndRecord = null;

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
		
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));


			commUtils.printLog(logId, methodNm, "EL1");

			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			for (int ii = 0; ii < rowCnt; ii++) {
			
				
				szYD_EQP_ID  		= commUtils.trim(gdReq.getHeader("YD_EQP_ID"  ).getValue(ii));//야드설비ID
				szYD_EQP_WRK_MODE  	= "9"; //야드설비작업Mode(Backup)
				szYD_WRK_PROG_STAT 	= "2"; //야드작업진행상태(권상완료)
				szYD_SCH_CD  		= commUtils.trim(gdReq.getHeader("YD_SCH_CD"  ).getValue(ii));//야드스케쥴코드
				szYD_CRN_SCH_ID  	= commUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID"  ).getValue(ii));//야드크레인스케쥴ID
				
				commUtils.printLog(logId, methodNm, "EL2");
				szYS_UP_WO_LOC  	= commUtils.trim(gdReq.getHeader("YS_UP_WO_LOC"  ).getValue(ii));//야드크레인스케쥴ID
				szYS_UP_WO_LAYER 	= commUtils.trim(gdReq.getHeader("YS_UP_WO_LAYER"  ).getValue(ii));//야드크레인스케쥴ID
				szYD_UP_WO_LOC_XAXIS= commUtils.trim(gdReq.getHeader("YD_UP_WO_LOC_XAXIS"  ).getValue(ii));//야드크레인X축
				szYD_UP_WO_LOC_YAXIS= commUtils.trim(gdReq.getHeader("YD_UP_WO_LOC_YAXIS"  ).getValue(ii));//야드크레인Y축
				szYD_UP_WO_LOC_ZAXIS= commUtils.trim(gdReq.getHeader("YD_UP_WO_LOC_ZAXIS"  ).getValue(ii));//야드크레인Z축

				/****************************************************************
				 * L2에서 올라오는 권상  로직 사용함 (GdsYsL2RcvSeEJB)
				 ****************************************************************/
				String L2_MC = commUtils.getYsGpBayToL2(szYD_EQP_ID);     // L2구분  RETURN;
			
				commUtils.printLog(logId, szYS_UP_WO_LOC, "EL3");
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name				
				jrParam.setField("JMS_TC_CD"       	, L2_MC +"YSL005"); //크레인권상실적
				jrParam.setField("YD_EQP_ID"		, szYD_EQP_ID);
				jrParam.setField("YD_EQP_WRK_MODE"	, szYD_EQP_WRK_MODE);
				jrParam.setField("YD_WRK_PROG_STAT"	, szYD_WRK_PROG_STAT);
				jrParam.setField("YD_SCH_CD"		, szYD_SCH_CD);
				jrParam.setField("YD_CRN_SCH_ID"	, szYD_CRN_SCH_ID);
				jrParam.setField("YS_UP_WR_LOC"		, szYS_UP_WO_LOC);
				jrParam.setField("YS_UP_WR_LAYER"	, szYS_UP_WO_LAYER);
				jrParam.setField("YD_UP_WO_LOC_XAXIS", szYD_UP_WO_LOC_XAXIS);
				jrParam.setField("YD_UP_WO_LOC_YAXIS", szYD_UP_WO_LOC_YAXIS);
				jrParam.setField("YD_UP_WO_LOC_ZAXIS", szYD_UP_WO_LOC_ZAXIS);

				
				EJBConnector sndConn = new EJBConnector("default","GdsYsL2RcvSeEJB", this);
				JDTORecord jrRtn = (JDTORecord)sndConn.trx("rcv"+ L2_MC +"YSL005", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				
				sndRecord = commUtils.addSndData(sndRecord, jrRtn);
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
	 * 크레인상태관리 - 권하실적처리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return String
	 * @throws DAOException
	 */
	public JDTORecord updCrnDnPrsBackUp(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리PP- 권하실적처리[GdsYsJspSeEJB.updCrnDnPrsBackUp] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String szYD_EQP_ID = "";
		String szYD_EQP_WRK_MODE = "";
		String szYD_SCH_CD = "";
		String szYD_CRN_SCH_ID = "";
		String szYD_WRK_PROG_STAT = "";
		String szYS_DN_WO_LOC = "";
		String szYS_DN_WO_LAYER = "";
		String szYD_DN_WO_LOC_XAXIS = "";
		String szYD_DN_WO_LOC_YAXIS = "";
		String szYD_DN_WO_LOC_ZAXIS = "";
		JDTORecord sndRecord = null;

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
		
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			for (int ii = 0; ii < rowCnt; ii++) {
			
				szYD_EQP_ID  		= commUtils.trim(gdReq.getHeader("YD_EQP_ID"  ).getValue(ii));//야드설비ID
				szYD_EQP_WRK_MODE  	= "9"; //야드설비작업Mode(Backup)
				szYD_WRK_PROG_STAT 	= "4"; //야드작업진행상태(권상완료)
				szYD_SCH_CD  		= commUtils.trim(gdReq.getHeader("YD_SCH_CD"  ).getValue(ii));//야드스케쥴코드
				szYD_CRN_SCH_ID  	= commUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID"  ).getValue(ii));//야드크레인스케쥴ID
				
				szYS_DN_WO_LOC  	= commUtils.trim(gdReq.getHeader("YS_DN_WO_LOC"  ).getValue(ii));//야드크레인스케쥴ID
				szYS_DN_WO_LAYER 	= commUtils.trim(gdReq.getHeader("YS_DN_WO_LAYER"  ).getValue(ii));//야드크레인스케쥴ID
				szYD_DN_WO_LOC_XAXIS= commUtils.trim(gdReq.getHeader("YD_DN_WO_LOC_XAXIS"  ).getValue(ii));//야드크레인X축
				szYD_DN_WO_LOC_YAXIS= commUtils.trim(gdReq.getHeader("YD_DN_WO_LOC_YAXIS"  ).getValue(ii));//야드크레인Y축
				szYD_DN_WO_LOC_ZAXIS= commUtils.trim(gdReq.getHeader("YD_DN_WO_LOC_ZAXIS"  ).getValue(ii));//야드크레인Z축

				/****************************************************************
				 * L2에서 올라오는 권상  로직 사용함 (GdsYsL2RcvSeEJB)
				 ****************************************************************/
				String L2_MC = commUtils.getYsGpBayToL2(szYD_EQP_ID);     // L2구분  RETURN;
		
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name				
				jrParam.setField("JMS_TC_CD"       	, L2_MC +"YSL006"); //크레인권하실적
				jrParam.setField("YD_EQP_ID"		, szYD_EQP_ID);
				jrParam.setField("YD_EQP_WRK_MODE"	, szYD_EQP_WRK_MODE);
				jrParam.setField("YD_WRK_PROG_STAT"	, szYD_WRK_PROG_STAT);
				jrParam.setField("YD_SCH_CD"		, szYD_SCH_CD);
				jrParam.setField("YD_CRN_SCH_ID"	, szYD_CRN_SCH_ID);
				jrParam.setField("YS_DN_WR_LOC"		, szYS_DN_WO_LOC);
				jrParam.setField("YS_DN_WR_LAYER"	, szYS_DN_WO_LAYER);
				jrParam.setField("YD_DN_WO_LOC_XAXIS", szYD_DN_WO_LOC_XAXIS);
				jrParam.setField("YD_DN_WO_LOC_YAXIS", szYD_DN_WO_LOC_YAXIS);
				jrParam.setField("YD_DN_WO_LOC_ZAXIS", szYD_DN_WO_LOC_ZAXIS);

				
				EJBConnector sndConn = new EJBConnector("default","GdsYsL2RcvSeEJB", this);
				JDTORecord jrRtn = (JDTORecord)sndConn.trx("rcv"+ L2_MC +"YSL006", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				
				sndRecord = commUtils.addSndData(sndRecord, jrRtn);
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
	 * 크레인상태관리 - 권하위치변경처리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return String
	 * @throws DAOException
	 */
	public JDTORecord updCrnDnPrsFix(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리PP- 권하위치변경처리[GdsYsJspSeEJB.updCrnDnPrsFix] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String szYD_EQP_ID = "";
//		String szYD_EQP_WRK_MODE = "";
		String szYD_SCH_CD = "";
		String szYD_CRN_SCH_ID = "";
		String szYD_WRK_PROG_STAT = "";
		String szYS_DN_WO_LOC = "";
		String szYD_WBOOK_ID = "";
		String szYS_DN_WO_LAYER = "";
		
		JDTORecord sndRecord = null;

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
		
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			for (int ii = 0; ii < rowCnt; ii++) {
			
				
				szYD_EQP_ID  		= commUtils.trim(gdReq.getHeader("YD_EQP_ID"  ).getValue(ii));//야드설비ID
				szYD_WRK_PROG_STAT 	= commUtils.trim(gdReq.getHeader("YD_WRK_PROG_STAT"  ).getValue(ii)); //야드작업진행상태
				szYD_SCH_CD  		= commUtils.trim(gdReq.getHeader("YD_SCH_CD"  ).getValue(ii));//야드스케쥴코드
				szYD_CRN_SCH_ID  	= commUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID"  ).getValue(ii));//야드크레인스케쥴ID
				szYD_WBOOK_ID	  	= commUtils.trim(gdReq.getHeader("YD_WBOOK_ID"  ).getValue(ii));//야드작업예약ID
				szYS_DN_WO_LOC  	= commUtils.trim(gdReq.getHeader("YS_DN_WO_LOC"  ).getValue(ii));//야드권하지시위치(신규)
				szYS_DN_WO_LAYER  	= commUtils.trim(gdReq.getHeader("YS_DN_WO_LAYER"  ).getValue(ii));//야드권하지시위치(신규)

				/****************************************************************
				 * L2에서 올라오는 권상  로직 사용함 (GdsYsL2RcvSeEJB)
				 ****************************************************************/
//				String L2_MC = commUtils.getYsGpBayToL2(szYD_EQP_ID);     // L2구분  RETURN;
//				String L2Ejb = commUtils.getYsGpBayToL2Ejb(szYD_EQP_ID);  // EJB명    RETURN;
			
				jrParam.setField("YD_EQP_ID"       	, szYD_EQP_ID); //크레인권하실적
				jrParam.setField("YD_SCH_CD"		, szYD_SCH_CD);
				jrParam.setField("YD_CRN_SCH_ID"	, szYD_CRN_SCH_ID);
				jrParam.setField("YD_WBOOK_ID"		, szYD_WBOOK_ID);
				jrParam.setField("YS_DN_WO_LOC"		, szYS_DN_WO_LOC);
				jrParam.setField("YS_DN_WO_LAYER"	, szYS_DN_WO_LAYER);
				jrParam.setField("YD_WRK_PROG_STAT"	, szYD_WRK_PROG_STAT);
				
				//권하지시위치 변경
				sndRecord = commUtils.addSndData(this.updCrnSchDnWoLoc(jrParam));				
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
	 *      [A] 오퍼레이션명 : 크레인작업예약관리 스케줄점검
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return String
	 *      @throws DAOException
	*/
	public JDTORecord procCrnSchRunnable(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업예약관리 스케줄점검[GdsYsJspSeEJB.procCrnSchRunnable] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//점검용 변수
			String ydWbookId       = "";	//야드작업예약ID
			String ydSchCd         = "";	//야드스케쥴코드
			String ydToLocGuide    = "";	//야드To위치Guide
			String toLocChkGp      = "";	//To위치 점검을 위한 구분(G:To위치Guide, C:차량상차, T:대차상차, E:설비불출, Z:기타)
			String toLocChkRst     = "";	//To위치점검결과
//			String ydAimRtGp       = "";	//야드목표행선구분
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

			//조회결과
			JDTORecordSet jsChk = null;
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			//작업예약
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			for (int ii = 0; ii < rowCnt; ii++) {
				ydWbookId = commUtils.trim(gdReq.getHeader("YD_WBOOK_ID").getValue(ii));

				chkRst = true;	//점검결과
				sbMsg  = sbMsg.append((ii + 1) + ". 작업예약ID - " + ydWbookId + "\n");

				//크레인스케줄 상태정보 조회
				jrParam.setField("YD_WBOOK_ID", ydWbookId); //야드작업예약ID
				
				jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchStat", logId, methodNm, "작업예약 조회");

				if (jsChk.size() <= 0) {
					chkRst = false;
					sbMsg  = sbMsg.append("   ▶ 오류 > 작업예약 정보 없음\n\n");
					continue;
				} else {
					JDTORecord jrChk = jsChk.getRecord(0);

					ydSchCd         = commUtils.trim(jrChk.getFieldString("YD_SCH_CD"          ));	//야드스케쥴코드
					ydToLocGuide    = commUtils.trim(jrChk.getFieldString("YD_TO_LOC_GUIDE"    ));	//야드To위치Guide
					toLocChkGp      = commUtils.trim(jrChk.getFieldString("TO_LOC_CHK_GP"      ));	//To위치점검구분
//					ydAimRtGp       = commUtils.trim(jrChk.getFieldString("YD_AIM_RT_GP"       ));	//야드목표행선구분
					ydCarUseGp      = commUtils.trim(jrChk.getFieldString("YD_CAR_USE_GP"      ));	//야드차량사용구분
				    trnEqpCd        = commUtils.trim(jrChk.getFieldString("TRN_EQP_CD"         ));	//운송장비코드
					carNo           = commUtils.trim(jrChk.getFieldString("CAR_NO"             ));	//차량번호
					cardNo          = commUtils.trim(jrChk.getFieldString("CARD_NO"            ));	//카드번호
					ydSchProhExn    = commUtils.trim(jrChk.getFieldString("YD_SCH_PROH_EXN"    ));	//야드스케쥴금지유무
					ydWrkPlanCrn    = commUtils.trim(jrChk.getFieldString("YD_WRK_PLAN_CRN"    ));	//야드작업계획크레인
					ydEqpStatPln    = commUtils.trim(jrChk.getFieldString("YD_EQP_STAT_PLN"    ));	//야드설비상태(작업계획크레인)
					ydEqpWrkModePln = commUtils.trim(jrChk.getFieldString("YD_EQP_WRK_MODE_PLN"));	//야드설비작업Mode(작업계획크레인)
					ydWrkCrn        = commUtils.trim(jrChk.getFieldString("YD_WRK_CRN"         ));	//야드작업크레인
					ydEqpStatWrk    = commUtils.trim(jrChk.getFieldString("YD_EQP_STAT_WRK"    ));	//야드설비상태(작업크레인)
					ydEqpWrkModeWrk = commUtils.trim(jrChk.getFieldString("YD_EQP_WRK_MODE_WRK"));	//야드설비작업Mode(작업크레인)
					ydAltCrn        = commUtils.trim(jrChk.getFieldString("YD_ALT_CRN"         ));	//야드대체크레인
					ydEqpStatAlt    = commUtils.trim(jrChk.getFieldString("YD_EQP_STAT_ALT"    ));	//야드설비상태(대체크레인)
					ydEqpWrkModeAlt = commUtils.trim(jrChk.getFieldString("YD_EQP_WRK_MODE_ALT"));	//야드설비작업Mode(대체크레인)
					ydCurrBayGp     = commUtils.trim(jrChk.getFieldString("YD_CURR_BAY_GP"     ));	//야드현재동구분(대차)
					cmDupYn         = commUtils.trim(jrChk.getFieldString("CM_DUP_YN"          ));	//크레인스케줄 재료중복여부
					clDupGp         = commUtils.trim(jrChk.getFieldString("CL_DUP_GP"          ));	//크레인스케줄 저장위치중복여부
					ttMtlSh         = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("TT_MTL_SH"),"0"));	//전체 재료매수
					wmMtlSh         = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("WM_MTL_SH"),"0"));	//작업예약 재료매수
					stMtlSh         = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("ST_MTL_SH"),"0"));	//저장품 재료매수
					slMtlSh         = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("SL_MTL_SH"),"0"));	//적치단 재료매수
					statCSh         = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("STAT_C_SH"),"0"));	//적치중인 재료매수(야드적치단재료상태가 적치 중[C] 인 재료수)
					abLocSh         = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("AB_LOC_SH"),"0"));	//저장위치이상 재료매수

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
					if (!"".equals(ydWrkPlanCrn) && !"B".equals(ydEqpStatPln) && "1".equals(ydEqpWrkModePln)) {
						//작업예약 지정크레인 : 최우선 지정
						sbMsg  = sbMsg.append("   ▷ 작업예약 지정크레인[" + ydWrkPlanCrn + ", " + ydEqpStatPln + "] 정상\n");
					} else if (!"".equals(ydWrkCrn) && !"B".equals(ydEqpStatWrk) && "1".equals(ydEqpWrkModeWrk)) {
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
						} else {
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
				}

				//To위치 Check
				trtGp = "";
				String ParmSql = "";
				if ("G".equals(toLocChkGp)) {
					//야드To위치Guide 값이 있고 작업 야드동이 같을 경우 야드To위치Guide로 
					//PU, DP, PI 불출위치에 재료가 있거나, 단수, 중량 초과이면 불가
					trtGp  = "ToLocGuide";
					trtMsg = "To위치점검[To위치지정 : " + ydToLocGuide + "]";
					/* com.inisteel.cim.ys.common.dao.YsCommDAO.geToLocGuideChk 
					--크레인스케줄 To위치점검-Guide 조회 
					--201502111922000010
					SELECT TO_LOC_CHK_RST
					      ,YS_STK_COL_GP||YS_STK_BED_NO AS YD_TO_LOC_GUIDE_NEW
					  FROM (SELECT CASE WHEN MIN(WB.EQP_BED_YN) = 'Y' AND COUNT(ST.SSTL_NO) > 0                         THEN 'G1' --공Bed 아님
					                    WHEN MIN(WB.EQP_BED_YN) = 'Y' AND MIN(WB.SAME_COL_YN) = 'Y'                    THEN 'G5' --재료위치, 권하위치 동일
					                    WHEN MIN(SB.YD_STK_BED_LYR_MAX) < MIN(WB.YD_MTL_SH) + COUNT(ST.SSTL_NO)         THEN 'G2' --매수 초과
					                    WHEN MIN(SB.YD_STK_BED_WT_MAX ) < MIN(WB.YD_MTL_WT) + NVL(SUM(ST.YD_MTL_WT),0) THEN 'G3' --중량 초과
					                    WHEN MIN(SB.YD_STK_BED_H_MAX  ) < MIN(WB.YD_MTL_T ) + NVL(SUM(ST.YD_MTL_T ),0) THEN 'G4' --높이 초과
					                    ELSE 'OK'
					                END AS TO_LOC_CHK_RST --설비 공Bed 및 Bed사양
					              ,SB.YS_STK_COL_GP
					              ,SB.YS_STK_BED_NO
					          FROM TB_YS_STKBED SB
					              ,TB_YS_STKLYR SL
					              ,TB_YS_STOCK  ST
					              ,(SELECT SUBSTR(WB.YD_TO_LOC_GUIDE,1,6) AS YS_STK_COL_GP
					                      ,SUBSTR(WB.YD_TO_LOC_GUIDE,7,2) AS YS_STK_BED_NO
					                      ,COUNT(*)                       AS YD_MTL_SH
					                      ,SUM(ST.YD_MTL_WT)              AS YD_MTL_WT
					                      ,SUM(ST.YD_MTL_T )              AS YD_MTL_T
					                      ,CASE WHEN SUBSTR(WB.YD_TO_LOC_GUIDE,3,2) IN ('PU','DP','PI') THEN 'Y' ELSE 'N' END AS EQP_BED_YN
					                      ,MIN((SELECT DECODE(SL.YS_STK_COL_GP,SUBSTR(WB.YD_TO_LOC_GUIDE,1,6),'Y')
					                              FROM TB_YS_STKLYR SL
					                             WHERE SL.SSTL_NO = WM.SSTL_NO
					                               AND SL.YD_STK_LYR_MTL_STAT = 'C')) AS SAME_COL_YN --현 재료위치가 권하위치와 동일열
					                  FROM TB_YS_WRKBOOK    WB
					                      ,TB_YS_WRKBOOKMTL WM
					                      ,TB_YS_STOCK      ST
					                 WHERE WB.YD_WBOOK_ID = :V_YD_WBOOK_ID
					                   AND WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
					                   AND WM.SSTL_NO      = ST.SSTL_NO
					                 GROUP BY WB.YD_TO_LOC_GUIDE) WB
					         WHERE SB.YS_STK_COL_GP LIKE WB.YS_STK_COL_GP||'%'
					           AND SB.YS_STK_BED_NO LIKE DECODE(WB.EQP_BED_YN,'N',WB.YS_STK_BED_NO,SB.YS_STK_BED_NO)||'%'
					           AND SB.YS_STK_COL_GP = SL.YS_STK_COL_GP
					           AND SB.YS_STK_BED_NO = SL.YS_STK_BED_NO
					           AND SL.SSTL_NO        = ST.SSTL_NO(+)
					         GROUP BY SB.YS_STK_COL_GP, SB.YS_STK_BED_NO
					         ORDER BY DECODE(TO_LOC_CHK_RST,'OK','G0',TO_LOC_CHK_RST) --적치가능, 공Bed 순
					                , DECODE(SB.YS_STK_BED_NO,MIN(WB.YS_STK_BED_NO),'00',SB.YS_STK_BED_NO)) WB
					 WHERE ROWNUM = 1
					*/
					ParmSql = "com.inisteel.cim.ys.common.dao.YsCommDAO.geToLocGuideChk";					
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
						
						// PIDEV
//						String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "K", "*");
						
//						if( "Y".equals(sApplyYnPI) ) {
							
							if ( "".equals(carNo) ) {
								chkRst = false;
								sbMsg  = sbMsg.append("   ▶ 오류 > To위치점검[출하 차량상차 : " + ydSchCd + "] 차량번호 없음\n");
							} else {
								trtGp  = "ToLocCar";
								trtMsg = "To위치점검[출하 차량상차 : " + ydSchCd + ", " + carNo + "]";
							}

//						} else {
//							
//							if ("".equals(carNo) || "".equals(cardNo)) {
//								chkRst = false;
//								sbMsg  = sbMsg.append("   ▶ 오류 > To위치점검[출하 차량상차 : " + ydSchCd + "] 차량번호 없음\n");
//							} else {
//								trtGp  = "ToLocCar";
//								trtMsg = "To위치점검[출하 차량상차 : " + ydSchCd + ", " + carNo + "-" + cardNo + "]";
//							}
//							
//						}
					}
					/* com.inisteel.cim.ys.common.dao.YsCommDAO.geToLocCarChk 
					--크레인스케줄 To위치점검-차량상차 조회 
					SELECT CASE WHEN COUNT(*) > 0 THEN 'OK' ELSE 'C1' END AS TO_LOC_CHK_RST --상차가능차량
					  FROM TB_YS_WRKBOOK WB
					      ,TB_YS_STKCOL  SC
					 WHERE WB.YD_WBOOK_ID   = :V_YD_WBOOK_ID
					   AND WB.YD_CAR_USE_GP = SC.YD_CAR_USE_GP
					   AND (WB.TRN_EQP_CD = SC.TRN_EQP_CD OR (WB.CAR_NO = SC.CAR_NO AND WB.CARD_NO = SC.CARD_NO))
					   AND SC.YD_STK_COL_ACT_STAT = 'L' --적치가능
					   AND SC.DEL_YN = 'N'
	                */
					ParmSql = "com.inisteel.cim.ys.common.dao.YsCommDAO.geToLocCarChk";					
				} else if ("T".equals(toLocChkGp)) {
					//대차상차작업
					if (!ydCurrBayGp.equals(ydSchCd.substring(1, 2))) {
						chkRst = false;
						sbMsg  = sbMsg.append("   ▶ 오류 > To위치점검[대차상차 : " + ydSchCd + "] 현재동 이상 [" + ydSchCd.substring(0, 1) + "XTC" + ydSchCd.substring(4, 6) + " : " + ydCurrBayGp + "]\n");
					}
//				} else if ("E".equals(toLocChkGp)) {
//					//스케줄코드 및 행선구분으로 To위치 점검
//					//불출(__PU__U_, __DP__U_)이고 위치검색범위에 적치매수 0인 Bed가 없으면 불가
//					if ("".equals(ydAimRtGp)) {
//						chkRst = false;
//						sbMsg  = sbMsg.append("   ▶ 오류 > To위치점검[설비보급 : " + ydSchCd + "] 야드목표행선구분 없음\n");
//					} else {
//						trtGp  = "ToLocExt";
//						trtMsg = "To위치점검[설비보급 : " + ydSchCd + ", " + ydAimRtGp + "]";
//					}
//				} else if ("Z".equals(toLocChkGp)) {
//					//스케줄코드 및 행선구분으로 To위치 점검
//					if ("".equals(ydAimRtGp)) {
//						chkRst = false;
//						sbMsg  = sbMsg.append("   ▶ 오류 > To위치점검[기타 : " + ydSchCd + "] 야드목표행선구분 없음\n");
//					} else {
//						trtGp  = "ToLocEtc";
//						trtMsg = "To위치점검[기타 : " + ydSchCd + ", " + ydAimRtGp + "]";
//					}
				}
				
				if (!"".equals(trtGp)) {
					toLocChkRst = ""; //To위치점검결과

//					jsChk = schDao.getYDYDJ400(trtGp, jrParam);
					jsChk = commDao.select(jrParam, ParmSql, logId, methodNm, "TO_위치 점검");
					if (jsChk.size() > 0) {
						toLocChkRst = commUtils.trim(jsChk.getRecord(0).getFieldString("TO_LOC_CHK_RST"));
					} else {
						toLocChkRst = toLocChkGp + "1";
					}
		
					if ("G1".equals(toLocChkRst)) {
						//TakeInBed1매선보급요구여부가 'Y'이면 장입보급  공Bed가 없더라도 스케줄 생성 가능
						if ("Y".equals(commUtils.trim(jsChk.getRecord(0).getFieldString("TI_PRE_SUP_YN")))) {
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
			
			commUtils.printLog(logId, methodNm, "S-");
			
			jrParam.setResultMsg(sbMsg.toString());

			return jrParam ;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
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
	public JDTORecord trtCrnWrkBookMgt(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업예약관리 스케줄기동[GdsYsJspSeEJB.trtCrnWrkBookMgt] < " + gdReq.getNavigateValue();
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
				jrParam.setField("YD_EQP_ID"  , commUtils.trim(gdReq.getHeader("YD_EQP_ID" ).getValue(ii))); //야드설비ID
				jrParam.setField("EJB_CALL_YN", "Y"); //EJBCall여부(신 크레인스케줄)
				commUtils.printLog(logId, "A", "S-");
				//크레인스케줄 전문(신 크레인스케줄 : EJB Call, 구 크레인스케줄 : JMS 전송)
				jrRtn = commUtils.addSndData(jrRtn, gdsYsComm.getCrnSchMsg(jrParam));
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
	 *      [A] 오퍼레이션명 : 작업예약관리-스케쥴보류
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord WaitSchedule(GridData gdReq) throws DAOException {
		String methodNm = "작업예약관리-스케쥴보류[GdsYsJspSeEJB.WaitSchedule] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			jrParam.setField("YD_WRK_PROG_STAT" 	, "C"                                      ); //야드 작업 상태
			jrParam.setField("OLD_YD_WRK_PROG_STAT" , "W"                                      ); //야드 작업 상태
			
			//작업예약
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				jrParam.setField("YD_WBOOK_ID", commUtils.trim(gdReq.getHeader("YD_WBOOK_ID").getValue(ii))); //야드작업예약ID
				
				//보류작업 대상정보

				/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdCrnDelay 
				UPDATE TB_YD_CRNSCH
				  SET  YD_WRK_PROG_STAT = :V_YD_WRK_PROG_STAT
				      ,MODIFIER = :V_MODIFIER
				      ,MOD_DDTT =  SYSDATE
				 WHERE YD_WBOOK_ID =  :V_YD_WBOOK_ID
 			       AND YD_WRK_PROG_STAT =  :V_OLD_YD_WRK_PROG_STAT
				*/ 
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdCrnDelay", logId, methodNm, "크레인스케줄수정");
				
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
	 *      [A] 오퍼레이션명 : 작업예약관리-스케쥴보류해제
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord unWaitSchedule(GridData gdReq) throws DAOException {
		String methodNm = "작업예약관리-스케쥴보류해제[GdsYsJspSeEJB.unWaitSchedule] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			jrParam.setField("YD_WRK_PROG_STAT" , "W"                                      ); //야드 작업 상태
			jrParam.setField("OLD_YD_WRK_PROG_STAT" , "C"                                      ); //야드 작업 상태
					
			//작업예약
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				jrParam.setField("YD_WBOOK_ID", commUtils.trim(gdReq.getHeader("YD_WBOOK_ID").getValue(ii))); //야드작업예약ID
				
				//보류작업 대상정보

				/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdCrnDelay 
				UPDATE TB_YD_CRNSCH
				  SET  YD_WRK_PROG_STAT = :V_YD_WRK_PROG_STAT
				      ,MODIFIER = :V_MODIFIER
				      ,MOD_DDTT =  SYSDATE
				 WHERE YD_WBOOK_ID =  :V_YD_WBOOK_ID
    			   AND YD_WRK_PROG_STAT =  :V_OLD_YD_WRK_PROG_STAT
				*/ 
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdCrnDelay", logId, methodNm, "크레인스케줄수정");
				
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
	public JDTORecord procWrkBookCncl(GridData gdReq) throws DAOException {
		String methodNm = "작업예약관리-삭제[GdsYsJspSeEJB.procWrkBookCncl] < " + gdReq.getNavigateValue();
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
			    ydEqpId   = commUtils.trim(gdReq.getHeader("YD_EQP_ID"  ).getValue(ii));
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
	 * 이송Lot등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord regFtmvLot(GridData gdReq) throws DAOException {
		String methodNm = "이송Lot등록[GdsYsJspSeEJB.regFtmvLot] < " + gdReq.getNavigateValue();
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
	} // end of regFtmvLot
	
	/**
	 * 설비인출보급 - 재료등록 (다베드 1단)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updPulloutSupMtl(GridData gdReq) throws DAOException {
		String methodNm = "설비인출보급 - 재료등록[GdsYsJspSeEJB.updPulloutSupMtl] < " + gdReq.getNavigateValue();
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
			String szYdRcptPlnStrLoc = null;
			
			
			String szStkStlNo = null;
//			String szBdcCurrProgCd = null;
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
				szYdRcptPlnStrLoc	= commUtils.getValue(gdReq, "YD_RCPT_PLN_STR_LOC", ii);
		
				
				szModGp = "ADD";
				jrParam.setField("SSTL_NO"	, szStlNo );
				jrParam.setField("YS_STK_COL_GP"	, szYsStkColGp );
				jrParam.setField("YS_STK_BED_NO"	, szYsStkBedNo );
				jrParam.setField("YS_STK_LYR_NO"	, szYsStkLyrNo );
				jrParam.setField("YS_STK_SEQ_NO"	, szYsStkSeqNo );
				
				//제품번호가 야드저장품에 존재하는지, 작업예약과 크레인스케줄에 잡혀있는 대상인지를 가져오고 번들공통에서 현재진도코드를 가져온다. 
				jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getIsEnableStlNo", logId, methodNm, "제품번호 작업대상여부 조회");
				
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
				
				//작업예약에 대상으로 잡혀있으면 작업 불가함 - 작업예약만 있는 경우에는 삭제 가능하도록 수정 (신진희)
				//if(!"".equals(szWbookId)) {
					//throw new Exception("제품번호 : "+jrParam.getFieldString("SSTL_NO")+" 가 작업예약("+szWbookId+")에 작업대상으로 잡혀있습니다! 작업예약을 취소한 후 삭제가 가능합니다.");
				//}
				
				//추가일 경우는 TO위치의 재료상태가 'E' 가 아니면 작업할 수 없음
				//삭제일 경우 TO위치의 재료상태가  'U'나 'D'일 경우 작업할 수 없음
				if("DELETE".equals(szModGp)) {
					if("U".equals(szToLocMtlStat) || "D".equals(szToLocMtlStat) ) {
						throw new Exception("TO 위치의 재료상태가 " + szToLocMtlStat + " 입니다. 삭제 작업을 할 수 없습니다.");
					}
				} else {
					if(!"E".equals(szToLocMtlStat) && !"C".equals(szToLocMtlStat) ) {
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
							if(!"PC".equals(jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_COL_GP").substring(2,4)) &&
							   !"TY".equals(jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_COL_GP").substring(2,4)) &&		
							   !"CH".equals(jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_COL_GP").substring(2,4))  ) {
								//설비구분이 PC,TY,CH가 아니면 에러 메세지를 리턴하고 종료한다.

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
					
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBndlCommYsStrLoc", logId, methodNm, "BUNDLE공통 야드저장위치 수정");
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
					
					if("".equals(szYdRcptPlnStrLoc)) {
						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStockYsStrLoc", logId, methodNm, "야드저장품 야드저장위치 수정");
					} else {
						jrParam.setField("YD_RCPT_PLN_STR_LOC"	, szYdRcptPlnStrLoc);
						commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updStockRcptPlnStrLoc", logId, methodNm, "야드저장품,입고예정위치 야드저장위치 수정");
					}
				}
				
				// 입고예정위치가 공란인 상태로 등록하면, 룰에 따라 입고예정위치 등록 시작
				if("".equals(szYdRcptPlnStrLoc)){
					
					jrParam.setField("SSTL_NO"					, szStlNo);
					jrParam.setField("YS_STK_COL_GP"			, szYsStkColGp );
					// 기준에 조회
					JDTORecordSet jsFindYdRcptPlnStrLoc = commDao.select(jrParam, "com.inisteel.cim.ys.gds.session.updPulloutSupMtl.updPulloutSupMtl.getjsFindYdRcptPlnStrLoc", logId, methodNm, "재료번호로 번들 입고예정위치 조회");
					if (jsFindYdRcptPlnStrLoc != null && jsFindYdRcptPlnStrLoc.size() > 0) {
						JDTORecord jrFindYdRcptPlnStrLoc = jsFindYdRcptPlnStrLoc.getRecord(0);
						commUtils.printParam(logId + "입고예정위치 조회 결과", jrFindYdRcptPlnStrLoc);
						
						szStkStlNo		= commUtils.trim(jrFindYdRcptPlnStrLoc.getFieldString("BNDL_NO")); 								// 재료번호
						String szItemnameCd			= commUtils.trim(jrFindYdRcptPlnStrLoc.getFieldString("ITEMNAME_CD"));  			// 품명코드
						String szRealMeasureBundleWt= commUtils.trim(jrFindYdRcptPlnStrLoc.getFieldString("REAL_MEASURE_BUNDLE_WT"));	// 실측BUNDLE중량
						String szWYdMtlMinW			= commUtils.trim(jrFindYdRcptPlnStrLoc.getFieldString("W_YD_MTL_MIN_W"));			// 선재제품폭MIN
						String szWYdMtlMaxW			= commUtils.trim(jrFindYdRcptPlnStrLoc.getFieldString("W_YD_MTL_MAX_W"));			// 선재제품폭MAX
						String szWYdMtlWt			= commUtils.trim(jrFindYdRcptPlnStrLoc.getFieldString("W_YD_MTL_WT"));				// 선재제품중량
						String szRYdMtlWt			= commUtils.trim(jrFindYdRcptPlnStrLoc.getFieldString("R_YD_MTL_WT"));				// 봉강제품중량
						String szRYdMtlMinWt		= commUtils.trim(jrFindYdRcptPlnStrLoc.getFieldString("R_YD_MTL_MIN_WT"));			// 봉강제품중량MIN
						String szRYdMtlMaxWt		= commUtils.trim(jrFindYdRcptPlnStrLoc.getFieldString("R_YD_MTL_MAX_WT"));			// 봉강제품중량MAX
						String szRYdMtlMinL			= commUtils.trim(jrFindYdRcptPlnStrLoc.getFieldString("R_YD_MTL_MIN_L"));			// 봉강제품길이MIN
						String szRYdMtlMaxL			= commUtils.trim(jrFindYdRcptPlnStrLoc.getFieldString("R_YD_MTL_MAX_L"));			// 봉강제품길이MAX
						String szRGongBedCnt		= commUtils.trim(jrFindYdRcptPlnStrLoc.getFieldString("R_GONG_BED_CNT"));			// 봉강공BED 수
						String szRNetBEdCnt			= commUtils.trim(jrFindYdRcptPlnStrLoc.getFieldString("R_NET_BED_CNT"));			// 봉강낱본BED 수
						String szRYdSize			= commUtils.trim(jrFindYdRcptPlnStrLoc.getFieldString("R_YD_SIZE"));				// 제품규격
						szYdRcptPlnStrLoc			= commUtils.trim(jrFindYdRcptPlnStrLoc.getFieldString("YD_RCPT_PLN_STR_LOC"));			// 야드입고예정저장위치

						commUtils.printLog(logId, "[임시베드-1단]입고예정위치 등록값이 공란이어서 룰에 따라 변경 처리 :"	+
								                  "\n재료번호: " 			+ szStkStlNo +
								                  "\n품명코드: " 			+ szItemnameCd +
								                  "\n실측BUNDLE중량: " 	+ szRealMeasureBundleWt +
								                  "\n선재제품폭MIN: " 		+ szWYdMtlMinW +
								                  "\n선재제품폭MAX: "		+ szWYdMtlMaxW +
								                  "\n선재제품중량: " 		+ szWYdMtlWt +
								                  "\n봉강제품중량: " 		+ szRYdMtlWt +
								                  "\n봉강제품중량MIN: " 	+ szRYdMtlMinWt +
								                  "\n봉강제품중량MAX: " 	+ szRYdMtlMaxWt +
								                  "\n봉강제품길이MIN: " 	+ szRYdMtlMinL +
								                  "\n봉강제품길이MAX: " 	+ szRYdMtlMaxL +
								                  "\n봉강공BED 수: " 		+ szRGongBedCnt +
								                  "\n봉강낱본BED 수: "		+ szRNetBEdCnt +
								                  "\n제품규격: " 			+ szRYdSize +
								                  "\n야드입고예정저장위치: "	+ szYdRcptPlnStrLoc
								                  , "SL");
						
						jrParam.setField("SSTL_NO"				, szStkStlNo );
						jrParam.setField("YD_RCPT_PLN_STR_LOC"	, szYdRcptPlnStrLoc );

						// 입고예정위치 업데이트
						commDao.update(jrParam, "com.inisteel.cim.ys.gds.session.updPulloutSupMtl.updPulloutSupMtl.upjsFindYdRcptPlnStrLoc", logId, methodNm, "입고예정위치 업데이트");
					}
				} // 입고예정위치가 공란인 상태로 등록하면, 룰에 따라 입고예정위치 등록 끝
				
			} // for문 끝
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updPulloutSupMtl

	/**
	 * 설비인출보급 - 재료삭제 (다베드 1단)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 *            gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord delPulloutSupMtl(GridData gdReq) throws DAOException {
		String methodNm = "설비인출보급 - 재료삭제[GdsYsJspSeEJB.delPulloutSupMtl] < " + gdReq.getNavigateValue();
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
//			String szBdcCurrProgCd = null;
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
				
				szOldStlNo 			= commUtils.getValue(gdReq, "OLD_SSTL_NO", ii);
				
				//삭제처리
				szModGp = "DELETE";
				jrParam.setField("SSTL_NO"	, szOldStlNo );
				
				jrParam.setField("YS_STK_COL_GP"	, szYsStkColGp );
				jrParam.setField("YS_STK_BED_NO"	, szYsStkBedNo );
				jrParam.setField("YS_STK_LYR_NO"	, szYsStkLyrNo );
				jrParam.setField("YS_STK_SEQ_NO"	, szYsStkSeqNo );
				
				//제품번호가 야드저장품에 존재하는지, 작업예약과 크레인스케줄에 잡혀있는 대상인지를 가져오고 번들공통에서 현재진도코드를 가져온다. 
				jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getIsEnableStlNo", logId, methodNm, "제품번호 작업대상여부 조회");
				
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
				if("ADD".equals(szModGp)||"MOVE".equals(szModGp)) {
					if("".equals(szStkStlNo)) {
						throw new Exception("제품번호 : "+jrParam.getFieldString("SSTL_NO")+" 가 TB_YS_STOCK 에 없습니다.");
					}
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
				
				//BLOOM공통 위치정보 수정하기
				if("DELETE".equals(szModGp)) {
					//삭제일경우 처리 ???
				} else {
					//추가, 이동
					jrParam.setField("FNL_REG_PGM"			, "delPulloutSupMtl" );
					jrParam.setField("YD_GP"				, szYsStkColGp.substring(0,1) );
					jrParam.setField("YD_BAY_GP"			, szYsStkColGp.substring(1,2) );
					jrParam.setField("YD_EQP_GP"			, szYsStkColGp.substring(2,4) );
					jrParam.setField("YS_STK_COL_NO"		, szYsStkColGp.substring(4,6) );
					jrParam.setField("YS_STK_BED_NO"		, szYsStkBedNo );
					jrParam.setField("YS_STK_LYR_NO"		, szYsStkLyrNo );
					jrParam.setField("YS_STK_SEQ_NO"		, szYsStkSeqNo );
					jrParam.setField("YS_STR_LOC"			, szYsStkColGp + szYsStkBedNo + szYsStkLyrNo + szYsStkSeqNo );
					jrParam.setField("SSTL_NO"				, szStlNo );
					
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBndlCommYsStrLoc", logId, methodNm, "BUNDLE공통 야드저장위치 수정");
					
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
	} // end of delPulloutSupMtl

	/**
	 * 설비인출보급 - 재료등록 (1베드 다단)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord updPulloutSupMtlMLyr(GridData gdReq) throws DAOException {
		String methodNm = "설비인출보급 - 재료등록[GdsYsJspSeEJB.updPulloutSupMtlMLyr] < " + gdReq.getNavigateValue();
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
			String szYdRcptPlnStrLoc = null;
			
			
			String szStkStlNo = null;
//			String szBdcCurrProgCd = null;
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
				szYsStkLyrNo = commUtils.getValue(gdReq, "YS_STK_LYR_NO", ii);
				szYsStkSeqNo 		= commUtils.getValue(gdReq, "YS_STK_SEQ_NO", ii);
				szYdRcptPlnStrLoc	= commUtils.getValue(gdReq, "YD_RCPT_PLN_STR_LOC", ii);
	
				
				szModGp = "ADD";
				jrParam.setField("SSTL_NO"	, szStlNo );
				jrParam.setField("YS_STK_COL_GP"	, szYsStkColGp );
				jrParam.setField("YS_STK_BED_NO"	, szYsStkBedNo );
				jrParam.setField("YS_STK_LYR_NO"	, szYsStkLyrNo );
				jrParam.setField("YS_STK_SEQ_NO"	, szYsStkSeqNo );

				//제품번호가 야드저장품에 존재하는지, 작업예약과 크레인스케줄에 잡혀있는 대상인지를 가져오고 번들공통에서 현재진도코드를 가져온다. 
				jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getIsEnableStlNo", logId, methodNm, "제품번호 작업대상여부 조회");

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
				
				// 저장품에 존재하는 제품번호인지 체크
				if ("ADD".equals(szModGp)) {
					if ("".equals(szStkStlNo)) {
						throw new Exception("제품번호 : " + jrParam.getFieldString("SSTL_NO") + " 가 TB_YS_STOCK 에 없습니다.");
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
					if(!"E".equals(szToLocMtlStat) && !"C".equals(szToLocMtlStat) ) {
						throw new Exception("TO 위치의 재료상태가 " + szToLocMtlStat + " 입니다. 등록 작업을 할 수 없습니다.");
					}
				}

				// ------------------------------------------------------------------------------------------
				if ("ADD".equals(szModGp)) {
					// SSTL_NO로 저장위치 조회하여 FROM위치가 존재하면 그 위치에서 SSTL_NO를 Clear 한다.
					jrParam.setField("SSTL_NO", szStlNo);
					jrParam.setField("YD_GP", gdReq.getParam("YD_GP"));

					JDTORecordSet jsStkLyrStlNo = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStrLocRegPda", logId, methodNm, "재료번호로 조회");

					if(jsStkLyrStlNo.size() > 0) {
					
						String sFromLoc = null;
						
						for (int mm = 0; mm < jsStkLyrStlNo.size(); mm++) {
							if(!"PC".equals(jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_COL_GP").substring(2,4)) &&
							   !"TY".equals(jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_COL_GP").substring(2,4)) &&		
							   !"CH".equals(jsStkLyrStlNo.getRecord(mm).getFieldString("YS_STK_COL_GP").substring(2,4))  ) {
								//설비구분이 PC,TY,CH가 아니면 에러 메세지를 리턴하고 종료한다.

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
					jrParam.setField("FNL_REG_PGM"			, "updPulloutSupMtlMLyr" );
					jrParam.setField("YD_GP"				, szYsStkColGp.substring(0,1) );
					jrParam.setField("YD_BAY_GP"			, szYsStkColGp.substring(1,2) );
					jrParam.setField("YD_EQP_GP"			, szYsStkColGp.substring(2,4) );
					jrParam.setField("YS_STK_COL_NO"		, szYsStkColGp.substring(4,6) );
					jrParam.setField("YS_STK_BED_NO"		, szYsStkBedNo );
					jrParam.setField("YS_STK_LYR_NO"		, szYsStkLyrNo );
					jrParam.setField("YS_STK_SEQ_NO"		, szYsStkSeqNo );
					jrParam.setField("YS_STR_LOC"			, szYsStkColGp + szYsStkBedNo + szYsStkLyrNo + szYsStkSeqNo );
					jrParam.setField("SSTL_NO"				, szStlNo );
					
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBndlCommYsStrLoc", logId, methodNm, "BUNDLE공통 야드저장위치 수정");
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
					
					if("".equals(szYdRcptPlnStrLoc)) {
						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStockYsStrLoc", logId, methodNm, "야드저장품 야드저장위치 수정");
					} else {
						jrParam.setField("YD_RCPT_PLN_STR_LOC"	, szYdRcptPlnStrLoc);
						commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updStockRcptPlnStrLoc", logId, methodNm, "야드저장품,입고예정위치 야드저장위치 수정");
					}
				}
				
				// 입고예정위치가 공란인 상태로 등록하면, 룰에 따라 입고예정위치 등록 시작
				if("".equals(szYdRcptPlnStrLoc)){
					
					jrParam.setField("SSTL_NO"					, szStlNo);
					jrParam.setField("YS_STK_COL_GP"			, szYsStkColGp );
					// 기준에 조회
					JDTORecordSet jsFindYdRcptPlnStrLoc = commDao.select(jrParam, "com.inisteel.cim.ys.gds.session.updPulloutSupMtl.updPulloutSupMtl.getjsFindYdRcptPlnStrLoc", logId, methodNm, "재료번호로 번들 입고예정위치 조회");
					if (jsFindYdRcptPlnStrLoc != null && jsFindYdRcptPlnStrLoc.size() > 0) {
						JDTORecord jrFindYdRcptPlnStrLoc = jsFindYdRcptPlnStrLoc.getRecord(0);
						commUtils.printParam(logId + "입고예정위치 조회 결과", jrFindYdRcptPlnStrLoc);
						
						szStkStlNo		= commUtils.trim(jrFindYdRcptPlnStrLoc.getFieldString("BNDL_NO")); 								// 재료번호
						String szItemnameCd			= commUtils.trim(jrFindYdRcptPlnStrLoc.getFieldString("ITEMNAME_CD"));  			// 품명코드
						String szRealMeasureBundleWt= commUtils.trim(jrFindYdRcptPlnStrLoc.getFieldString("REAL_MEASURE_BUNDLE_WT"));	// 실측BUNDLE중량
						String szWYdMtlMinW			= commUtils.trim(jrFindYdRcptPlnStrLoc.getFieldString("W_YD_MTL_MIN_W"));			// 선재제품폭MIN
						String szWYdMtlMaxW			= commUtils.trim(jrFindYdRcptPlnStrLoc.getFieldString("W_YD_MTL_MAX_W"));			// 선재제품폭MAX
						String szWYdMtlWt			= commUtils.trim(jrFindYdRcptPlnStrLoc.getFieldString("W_YD_MTL_WT"));				// 선재제품중량
						String szRYdMtlWt			= commUtils.trim(jrFindYdRcptPlnStrLoc.getFieldString("R_YD_MTL_WT"));				// 봉강제품중량
						String szRYdMtlMinWt		= commUtils.trim(jrFindYdRcptPlnStrLoc.getFieldString("R_YD_MTL_MIN_WT"));			// 봉강제품중량MIN
						String szRYdMtlMaxWt		= commUtils.trim(jrFindYdRcptPlnStrLoc.getFieldString("R_YD_MTL_MAX_WT"));			// 봉강제품중량MAX
						String szRYdMtlMinL			= commUtils.trim(jrFindYdRcptPlnStrLoc.getFieldString("R_YD_MTL_MIN_L"));			// 봉강제품길이MIN
						String szRYdMtlMaxL			= commUtils.trim(jrFindYdRcptPlnStrLoc.getFieldString("R_YD_MTL_MAX_L"));			// 봉강제품길이MAX
						String szRGongBedCnt		= commUtils.trim(jrFindYdRcptPlnStrLoc.getFieldString("R_GONG_BED_CNT"));			// 봉강공BED 수
						String szRNetBEdCnt			= commUtils.trim(jrFindYdRcptPlnStrLoc.getFieldString("R_NET_BED_CNT"));			// 봉강낱본BED 수
						String szRYdSize			= commUtils.trim(jrFindYdRcptPlnStrLoc.getFieldString("R_YD_SIZE"));				// 제품규격
						szYdRcptPlnStrLoc			= commUtils.trim(jrFindYdRcptPlnStrLoc.getFieldString("YD_RCPT_PLN_STR_LOC"));			// 야드입고예정저장위치

						commUtils.printLog(logId, "[임시베드-다단]입고예정위치 등록값이 공란이어서 룰에 따라 변경 처리 :"	+
								                  "\n재료번호: " 			+ szStkStlNo +
								                  "\n품명코드: " 			+ szItemnameCd +
								                  "\n실측BUNDLE중량: " 	+ szRealMeasureBundleWt +
								                  "\n선재제품폭MIN: " 		+ szWYdMtlMinW +
								                  "\n선재제품폭MAX: "		+ szWYdMtlMaxW +
								                  "\n선재제품중량: " 		+ szWYdMtlWt +
								                  "\n봉강제품중량: " 		+ szRYdMtlWt +
								                  "\n봉강제품중량MIN: " 	+ szRYdMtlMinWt +
								                  "\n봉강제품중량MAX: " 	+ szRYdMtlMaxWt +
								                  "\n봉강제품길이MIN: " 	+ szRYdMtlMinL +
								                  "\n봉강제품길이MAX: " 	+ szRYdMtlMaxL +
								                  "\n봉강공BED 수: " 		+ szRGongBedCnt +
								                  "\n봉강낱본BED 수: "		+ szRNetBEdCnt +
								                  "\n제품규격: " 			+ szRYdSize +
								                  "\n야드입고예정저장위치: "	+ szYdRcptPlnStrLoc
								                  , "SL");
						
						jrParam.setField("SSTL_NO"				, szStkStlNo );
						jrParam.setField("YD_RCPT_PLN_STR_LOC"	, szYdRcptPlnStrLoc );

						// 입고예정위치 업데이트
						commDao.update(jrParam, "com.inisteel.cim.ys.gds.session.updPulloutSupMtl.updPulloutSupMtl.upjsFindYdRcptPlnStrLoc", logId, methodNm, "입고예정위치 업데이트");
					}
				} // 입고예정위치가 공란인 상태로 등록하면, 룰에 따라 입고예정위치 등록 끝
				
			}  // for문 끝
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updPulloutSupMtlMLyr

	/**
	 * 설비인출보급 - 재료삭제 (1베드 다단)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 *            gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord delPulloutSupMtlMLyr(GridData gdReq) throws DAOException {
		String methodNm = "설비인출보급 - 재료삭제[GdsYsJspSeEJB.delPulloutSupMtlMLyr] < " + gdReq.getNavigateValue();
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
//			String szBdcCurrProgCd = null;
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
				szYsStkLyrNo 		= commUtils.getValue(gdReq, "YS_STK_LYR_NO", ii);
				szYsStkSeqNo 		= commUtils.getValue(gdReq, "YS_STK_SEQ_NO", ii);
				
				szOldStlNo 			= commUtils.getValue(gdReq, "OLD_SSTL_NO", ii);
				
				//삭제처리
				szModGp = "DELETE";
				jrParam.setField("SSTL_NO"	, szOldStlNo );
				
				jrParam.setField("YS_STK_COL_GP"	, szYsStkColGp );
				jrParam.setField("YS_STK_BED_NO"	, szYsStkBedNo );
				jrParam.setField("YS_STK_LYR_NO"	, szYsStkLyrNo );
				jrParam.setField("YS_STK_SEQ_NO"	, szYsStkSeqNo );
				
				//제품번호가 야드저장품에 존재하는지, 작업예약과 크레인스케줄에 잡혀있는 대상인지를 가져오고 번들공통에서 현재진도코드를 가져온다. 
				jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getIsEnableStlNo", logId, methodNm, "제품번호 작업대상여부 조회");
				
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
				if("ADD".equals(szModGp)||"MOVE".equals(szModGp)) {
					if("".equals(szStkStlNo)) {
						throw new Exception("제품번호 : "+jrParam.getFieldString("SSTL_NO")+" 가 TB_YS_STOCK 에 없습니다.");
					}
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
				
				//BLOOM공통 위치정보 수정하기
				if("DELETE".equals(szModGp)) {
					//삭제일경우 처리 ???
				} else {
					//추가, 이동
					jrParam.setField("FNL_REG_PGM"			, "delPulloutSupMtlMLyr" );
					jrParam.setField("YD_GP"				, szYsStkColGp.substring(0,1) );
					jrParam.setField("YD_BAY_GP"			, szYsStkColGp.substring(1,2) );
					jrParam.setField("YD_EQP_GP"			, szYsStkColGp.substring(2,4) );
					jrParam.setField("YS_STK_COL_NO"		, szYsStkColGp.substring(4,6) );
					jrParam.setField("YS_STK_BED_NO"		, szYsStkBedNo );
					jrParam.setField("YS_STK_LYR_NO"		, szYsStkLyrNo );
					jrParam.setField("YS_STK_SEQ_NO"		, szYsStkSeqNo );
					jrParam.setField("YS_STR_LOC"			, szYsStkColGp + szYsStkBedNo + szYsStkLyrNo + szYsStkSeqNo );
					jrParam.setField("SSTL_NO"				, szStlNo );
					
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBndlCommYsStrLoc", logId, methodNm, "BUNDLE공통 야드저장위치 수정");
					
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
	} // end of delPulloutSupMtlMLyr
	
	/**
	 * 대차스케줄관리 - 대차초기화
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord initTcarSchMgt(GridData gdReq) throws DAOException {
		String methodNm = "대차스케줄관리 대차초기화[GdsYsJspSeEJB.initTcarSchMgt] < " + gdReq.getNavigateValue();
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
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updTcarSchInitMtl", logId, methodNm, "대차이송재료 초기화");

				//대차스케줄 초기화
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updTcarSchInitSch", logId, methodNm, "대차스케줄 초기화");
				
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

				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updTcarSchInsSch", logId, methodNm, "대차스케줄 등록");
				
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
		String methodNm = "작업예약 우선순위변경[GdsYsJspSeEJB.updWrkBookPrior] < " + gdReq.getNavigateValue();
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
	 * 대차스케줄관리/ 작업예약화면  - 작업예약삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord delWrkBook(GridData gdReq) throws DAOException {
		String methodNm = "작업예약삭제[GdsYsJspSeEJB.delWrkBook] < " + gdReq.getNavigateValue();
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
			GdsYsDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCommCarSchWbDel", logId, methodNm, "TB_YS_CARSCH");				
		
			//대차스케줄 야드상차작업예약ID, 야드하차작업예약ID 삭제
			GdsYsDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCommTcarSchWbDel", logId, methodNm, "TB_YS_TCARSCH");				
			
			
			 /**********************************************************
			* 4. 작업예약/재료 삭제
			**********************************************************/
			//작업예약재료 삭제
			GdsYsDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnWrkBookMtl", logId, methodNm, "TB_YS_WRKBOOKMTL");				

			//작업예약 삭제
			GdsYsDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnWrkBook", logId, methodNm, "TB_YS_WRKBOOK");
			

			commUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of delWrkBook	
	
	/**
	 *      [A] 오퍼레이션명 : 대차 현재동 변경
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updTcarCurrBay(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "대차 현재동 변경[GdsYsJspSeEJB.updTcarCurrBay] < " + rcvMsg.getResultMsg();
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
			JDTORecordSet jsTcar = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStatTcarBed", logId, methodNm, "대차Bed상태 조회");

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
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatStkBedActCA", logId, methodNm, "적치Bed(전체) 비활성화");

			//적치단 재료 삭제
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrClr", logId, methodNm, "적치단 재료 삭제");

			/**********************************************************
			* 3. 현재동 변경 및 저장위치제원 전문 조회
			**********************************************************/
			if (!ydCurrBayGpCur.equals(ydCurrBayGpNew)) {
				//설비 현재동 수정
				jrParam.setField("YD_CURR_BAY_GP", ydCurrBayGpNew);

				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdEqpCurrBay", logId, methodNm, "설비 현재동 수정");

				//기존 Bed의 상태가 변경되었으면 저장위치제원 전문 조회
				if ("L".equals(ydStkBedActStatCur)) {
					jrParam.setField("YD_INFO_SYNC_CD", "4"          ); //야드정보동기화코드(Bed)
					jrParam.setField("YS_STK_COL_GP"  , ydStkColGpCur); //야드적치열구분
					jrParam.setField("YS_STK_BED_NO"  , "01"         ); //야드적치Bed번호

					//전송Data 조회
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN4L001", jrParam));
				}
			}
			
			/**********************************************************
			* 4. 신규 저장위치  활성화 및 저장위치제원 전문 조회
			**********************************************************/
			//신규 적치Bed Close 상태이면 활성화
			jrParam.setField("YD_STK_COL_GP"      , ydStkColGpNew); //야드적치열구분
			jrParam.setField("YD_STK_BED_NO"      , "01"         ); //야드적치Bed번호
			jrParam.setField("YD_STK_BED_ACT_STAT", "L"          ); //야드적치Bed활성상태(적치가능)

			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatStkBedAct", logId, methodNm, "신규 적치Bed Close 상태이면 활성화");

			//적치단 재료 삭제
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrClr", logId, methodNm, "적치단 재료 삭제");

			//신규 Bed의 상태가 변경되었으면 저장위치제원 전문 전송
			if ("C".equals(ydStkBedActStatNew)) {
				jrParam.setField("YD_INFO_SYNC_CD", "4"); //야드정보동기화코드(Bed)
				jrParam.setField("YS_STK_COL_GP"  , ydStkColGpNew); //야드적치열구분
				jrParam.setField("YS_STK_BED_NO"  , "01"         ); //야드적치Bed번호

				//전송Data 조회
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN4L001", jrParam));
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
		String methodNm = "대차상태설정 등록처리[GdsYsJspSeEJB.trtTcarStatSet] < " + gdReq.getNavigateValue();
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
				jrYdMsg.setField("JMS_TC_CD"          , "N4YSL003"); //설비고장복구실적
				jrYdMsg.setField("YD_EQP_STAT"        , commUtils.trim(gdReq.getParam("YD_EQP_STAT"))); //야드설비상태(B:고장, N:정상)
				jrYdMsg.setField("YD_EQP_PAUSE_CODE"  , "0000"    ); //야드설비휴지코드
				jrYdMsg.setField("YD_EQP_TRBL_RCVR_DT", currDate  ); //야드설비고장복구일시

				EJBConnector sndConn = new EJBConnector("default", "GdsYsL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvN4YSL003", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
				
			} else if ("MD".equals(trtDtlGp)) {
				//작업Mode 변경
				jrYdMsg.setField("JMS_TC_CD"      , "N4YSL003"); //설비운전모드전환
				jrYdMsg.setField("YD_EQP_WRK_MODE", commUtils.trim(gdReq.getParam("YD_EQP_WRK_MODE"))); //야드설비작업Mode(1:On-Line, 0:Off-Line)

				EJBConnector sndConn = new EJBConnector("default", "GdsYsL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvN4YSL003", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
				
			} else if ("HB".equals(trtDtlGp)) {
				//Home동 변경 - Log ID, Method, 수정자 Set
				JDTORecord jrParam = commUtils.getParam(logId, methodNm, modifier);

				jrParam.setField("YD_EQP_ID"     , ydEqpId); //야드설비ID
				jrParam.setField("YD_HOME_BAY_GP", commUtils.trim(gdReq.getParam("YD_HOME_BAY_GP")));
				
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdEqpHomeBay", logId, methodNm, "Home동 변경");
				
			} else if ("CB".equals(trtDtlGp)) {
				//현재동 변경
				jrYdMsg.setField("YD_CURR_BAY_GP", commUtils.trim(gdReq.getParam("YD_CURR_BAY_GP")));

				jrRtn = this.updTcarCurrBay(jrYdMsg);
				
			} else if ("TS".equals(trtDtlGp)) {
				//공대차출발지시 등록
				jrYdMsg.setField("YD_BAY_GP", commUtils.trim(gdReq.getParam("YD_CURR_BAY_GP_TS"))); //야드동구분(상차동)
				jrYdMsg.setField("YD_L2_ID" , "N4"    ); 
				jrRtn = YsComm.trtTcarSchLevWo(jrYdMsg);
				
			} else if ("TL".equals(trtDtlGp)) {
				//출발실적처리
				jrYdMsg.setField("JMS_TC_CD"      , "N4YSL007"); //대차이동실적
				jrYdMsg.setField("YD_TCAR_MOVE_GP", "S"       ); //야드대차이동구분(출발)
				jrYdMsg.setField("YD_BAY_GP1"     , commUtils.trim(gdReq.getParam("YD_CURR_BAY_GP_TL"))); //야드동구분1(현재동)

				EJBConnector sndConn = new EJBConnector("default", "GdsYsL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvN4YSL007", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
			} else if ("TA".equals(trtDtlGp)) {
				//도착실적처리
				jrYdMsg.setField("JMS_TC_CD"      , "N4YSL007"); //대차이동실적
				jrYdMsg.setField("YD_TCAR_MOVE_GP", "E"       ); //야드대차이동구분(도착)
				jrYdMsg.setField("YD_BAY_GP1"     , commUtils.trim(gdReq.getParam("YD_CURR_BAY_GP_TA"))); //야드동구분1(현재동)

				EJBConnector sndConn = new EJBConnector("default", "GdsYsL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvN4YSL007", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
			} else if ("TC".equals(trtDtlGp)) {
				//완료실적처리
				String ydCarProgStat = commUtils.trim(gdReq.getHeader("YD_CAR_PROG_STAT").getValue(0));	//야드차량진행상태
				
				if ("4".equals(ydCarProgStat)) {
					//상차개시 -> 상차완료(영대차출발지시)
					jrRtn = YsComm.trtTcarSchLdCmpl(jrYdMsg);
				} else if ("D".equals(ydCarProgStat)) {
					//하차개시 -> 하차완료(공대차출발지시)
					jrYdMsg.setField("YD_BAY_GP", commUtils.trim(gdReq.getParam("YD_CURR_BAY_GP_TC"))); //야드동구분(공대차출발지시 상차동)
					jrYdMsg.setField("YD_L2_ID", "N4");
					
					jrRtn = YsComm.trtTcarSchUdCmpl(jrYdMsg);
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
	 * 입고실적 BACKUP
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord rcptWrkBackUp(GridData gdReq) throws DAOException {
		String methodNm = "입고실적 BACKUP[GdsYsJspSeEJB.rcptWrkBackUp] < " + gdReq.getNavigateValue();
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
			
			//PIDEV
//			String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "K", "*");			
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				jrParam.setField("SSTL_NO", commUtils.getValue(gdReq, "SSTL_NO", ii));
				
//				if("Y".equals(sApplyYnPI)) {
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1014", jrParam));
//				}  else {	
//					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSDSJ001", jrParam));
//				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of rcptWrkBackUp
	
	/**
	 * 준비스케줄 - 재료삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord delPrepMtl(GridData gdReq) throws DAOException {
		String methodNm = "준비스케줄 - 재료삭제[GdsYsJspSeEJB.delPrepMtl] < " + gdReq.getNavigateValue();
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
		String methodNm = "준비스케줄 - 수정[GdsYsJspSeEJB.updPrepSchLot] < " + gdReq.getNavigateValue();
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
		String methodNm = "준비스케줄 - 삭제[GdsYsJspSeEJB.delPrepSchLot] < " + gdReq.getNavigateValue();
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
	 *      [A] 오퍼레이션명 : Traverser 상태설정 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtTsStatSet(GridData gdReq) throws DAOException {
		String methodNm = "Traverser 상태설정 [GdsYsJspSeEJB.trtTsStatSet] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			String trtDtlGp = commUtils.trim(gdReq.getParam("TRT_DTL_GP"));	//처리상세구분
			String ydEqpId  = commUtils.trim(gdReq.getParam("YD_EQP_ID" ));	//야드설비ID(대차)
			String modifier = commUtils.trim(gdReq.getParam("userid"      ));	//수정자
			String currDate = commUtils.getDateTime14();						//현재시각

			if ("".equals(ydEqpId)) {
				throw new Exception("설비ID가 없습니다.");
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
				jrYdMsg.setField("JMS_TC_CD"          , "N6YSL003"); //설비고장복구실적
				jrYdMsg.setField("YD_EQP_STAT"        , commUtils.trim(gdReq.getParam("YD_EQP_STAT"))); //야드설비상태(B:고장, N:정상)
				jrYdMsg.setField("YD_EQP_PAUSE_CODE"  , "0000"    ); //야드설비휴지코드
				jrYdMsg.setField("YD_EQP_TRBL_RCVR_DT", currDate  ); //야드설비고장복구일시

				EJBConnector sndConn = new EJBConnector("default", "GdsYsL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvN6YSL003", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
				
			} else if ("CB".equals(trtDtlGp)) {
				//현재위치 변경 --> 공차 도착실적처리
				jrYdMsg.setField("JMS_TC_CD"      		, "N6YSL007"); //설비이동실적
				jrYdMsg.setField("YD_TCAR_MOVE_GP"		, "E"); //설비이동구분 (S:출발, E:도착)
				jrYdMsg.setField("TRN_WRK_FULLVOID_GP"	, "E"); //운송작업영공구분 (E:공차, F:영차)
				jrYdMsg.setField("YS_STR_LOC"			, commUtils.trim(gdReq.getParam("YD_CURR_STR_LOC")));
				jrYdMsg.setField("SSTL_NO1"				, commUtils.trim(gdReq.getParam("SSTL_NO1"))); //재료번호1
				jrYdMsg.setField("SSTL_NO2"				, commUtils.trim(gdReq.getParam("SSTL_NO2"))); //재료번호2
				jrYdMsg.setField("YD_CRN_WRK_SH"		, commUtils.trim(gdReq.getParam("MTL_SH"))); //작업매수
				jrYdMsg.setField("YD_CRN_SCH_ID"		, commUtils.trim(gdReq.getParam("YD_CRN_SCH_ID"))); //야드크레인스케줄ID
				
				EJBConnector sndConn = new EJBConnector("default", "GdsYsL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvN6YSL007", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
				
			} else if ("TL".equals(trtDtlGp)) {
				//출발실적처리
				jrYdMsg.setField("JMS_TC_CD"      		, "N6YSL007"); //설비이동실적
				jrYdMsg.setField("YD_TCAR_MOVE_GP"		, "S"); //설비이동구분 (S:출발, E:도착)
				jrYdMsg.setField("TRN_WRK_FULLVOID_GP"	, commUtils.trim(gdReq.getParam("TRN_WRK_FULLVOID_GP"))); //운송작업영공구분 (E:공차, F:영차)
				jrYdMsg.setField("YS_STR_LOC"			, commUtils.trim(gdReq.getParam("YD_CURR_STR_LOC_TL")));
				jrYdMsg.setField("SSTL_NO1"				, commUtils.trim(gdReq.getParam("SSTL_NO1"))); //재료번호1
				jrYdMsg.setField("SSTL_NO2"				, commUtils.trim(gdReq.getParam("SSTL_NO2"))); //재료번호2
				jrYdMsg.setField("YD_CRN_WRK_SH"		, commUtils.trim(gdReq.getParam("MTL_SH"))); //작업매수
				jrYdMsg.setField("YD_CRN_SCH_ID"		, commUtils.trim(gdReq.getParam("YD_CRN_SCH_ID"))); //야드크레인스케줄ID

				EJBConnector sndConn = new EJBConnector("default", "GdsYsL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvN6YSL007", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
			} else if ("TA".equals(trtDtlGp)) {
				//도착실적처리
				jrYdMsg.setField("JMS_TC_CD"      		, "N6YSL007"); //설비이동실적
				jrYdMsg.setField("YD_TCAR_MOVE_GP"		, "E"); //설비이동구분 (S:출발, E:도착)
				jrYdMsg.setField("TRN_WRK_FULLVOID_GP"	, commUtils.trim(gdReq.getParam("TRN_WRK_FULLVOID_GP"))); //운송작업영공구분 (E:공차, F:영차)
				jrYdMsg.setField("YS_STR_LOC"			, commUtils.trim(gdReq.getParam("YD_CURR_STR_LOC_TA")));
				jrYdMsg.setField("SSTL_NO1"				, commUtils.trim(gdReq.getParam("SSTL_NO1"))); //재료번호1
				jrYdMsg.setField("SSTL_NO2"				, commUtils.trim(gdReq.getParam("SSTL_NO2"))); //재료번호2
				jrYdMsg.setField("YD_CRN_WRK_SH"		, commUtils.trim(gdReq.getParam("MTL_SH"))); //작업매수
				jrYdMsg.setField("YD_CRN_SCH_ID"		, commUtils.trim(gdReq.getParam("YD_CRN_SCH_ID"))); //야드크레인스케줄ID

				EJBConnector sndConn = new EJBConnector("default", "GdsYsL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvN6YSL007", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
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
	} // end of trtTsStatSet
	
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
		String methodNm = "크레인사양분리[GdsYsJspSeEJB.setCrnSpecSpr] < " + jrCrnSpec.getResultMsg();
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
	 *      [A] 오퍼레이션명 : CoilCar 상태설정 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtCcStatSet(GridData gdReq) throws DAOException {
		String methodNm = "CoilCar 상태설정 [GdsYsJspSeEJB.trtCcStatSet] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			String trtDtlGp = commUtils.trim(gdReq.getParam("TRT_DTL_GP"));	//처리상세구분
			String ydEqpId  = commUtils.trim(gdReq.getParam("YD_EQP_ID" ));	//야드설비ID(대차)
			String modifier = commUtils.trim(gdReq.getParam("userid"      ));	//수정자
			String currDate = commUtils.getDateTime14();						//현재시각

			if ("".equals(ydEqpId)) {
				throw new Exception("설비ID가 없습니다.");
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
				jrYdMsg.setField("JMS_TC_CD"          , "N5YSL003"); //설비고장복구실적
				jrYdMsg.setField("YD_EQP_STAT"        , commUtils.trim(gdReq.getParam("YD_EQP_STAT"))); //야드설비상태(B:고장, N:정상)
				jrYdMsg.setField("YD_EQP_PAUSE_CODE"  , "0000"    ); //야드설비휴지코드
				jrYdMsg.setField("YD_EQP_TRBL_RCVR_DT", currDate  ); //야드설비고장복구일시

				EJBConnector sndConn = new EJBConnector("default", "GdsYsL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvN5YSL003", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
				
			} else if ("CB".equals(trtDtlGp)) {
				//현재위치 변경 --> 공차 도착실적처리
				jrYdMsg.setField("JMS_TC_CD"      		, "N5YSL007"); //설비이동실적
				jrYdMsg.setField("YD_TCAR_MOVE_GP"		, "E"); //설비이동구분 (S:출발, E:도착)
				jrYdMsg.setField("TRN_WRK_FULLVOID_GP"	, "E"); //운송작업영공구분 (E:공차, F:영차)
				jrYdMsg.setField("YS_STR_LOC"			, commUtils.trim(gdReq.getParam("YD_CURR_STR_LOC")));
				jrYdMsg.setField("SSTL_NO1"				, commUtils.trim(gdReq.getParam("SSTL_NO1"))); //재료번호1
				jrYdMsg.setField("SSTL_NO2"				, commUtils.trim(gdReq.getParam("SSTL_NO2"))); //재료번호2
				jrYdMsg.setField("YD_CRN_WRK_SH"		, commUtils.trim(gdReq.getParam("MTL_SH"))); //작업매수
				jrYdMsg.setField("YD_CRN_SCH_ID"		, commUtils.trim(gdReq.getParam("YD_CRN_SCH_ID"))); //야드크레인스케줄ID
				
				EJBConnector sndConn = new EJBConnector("default", "GdsYsL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvN5YSL007", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
				
			} else if ("TL".equals(trtDtlGp)) {
				//출발실적처리
				jrYdMsg.setField("JMS_TC_CD"      		, "N5YSL007"); //설비이동실적
				jrYdMsg.setField("YD_TCAR_MOVE_GP"		, "S"); //설비이동구분 (S:출발, E:도착)
				jrYdMsg.setField("TRN_WRK_FULLVOID_GP"	, commUtils.trim(gdReq.getParam("TRN_WRK_FULLVOID_GP"))); //운송작업영공구분 (E:공차, F:영차)
				jrYdMsg.setField("YS_STR_LOC"			, commUtils.trim(gdReq.getParam("YD_CURR_STR_LOC")));
				jrYdMsg.setField("SSTL_NO1"				, commUtils.trim(gdReq.getParam("SSTL_NO1"))); //재료번호1
				jrYdMsg.setField("SSTL_NO2"				, commUtils.trim(gdReq.getParam("SSTL_NO2"))); //재료번호2
				jrYdMsg.setField("YD_CRN_WRK_SH"		, commUtils.trim(gdReq.getParam("MTL_SH"))); //작업매수
				jrYdMsg.setField("YD_CRN_SCH_ID"		, commUtils.trim(gdReq.getParam("YD_CRN_SCH_ID"))); //야드크레인스케줄ID

				EJBConnector sndConn = new EJBConnector("default", "GdsYsL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvN5YSL007", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
			} else if ("TA".equals(trtDtlGp)) {
				//도착실적처리
				jrYdMsg.setField("JMS_TC_CD"      		, "N5YSL007"); //설비이동실적
				jrYdMsg.setField("YD_TCAR_MOVE_GP"		, "E"); //설비이동구분 (S:출발, E:도착)
				jrYdMsg.setField("TRN_WRK_FULLVOID_GP"	, commUtils.trim(gdReq.getParam("TRN_WRK_FULLVOID_GP"))); //운송작업영공구분 (E:공차, F:영차)
				jrYdMsg.setField("YS_STR_LOC"			, commUtils.trim(gdReq.getParam("YD_CURR_STR_LOC")));
				jrYdMsg.setField("SSTL_NO1"				, commUtils.trim(gdReq.getParam("SSTL_NO1"))); //재료번호1
				jrYdMsg.setField("SSTL_NO2"				, commUtils.trim(gdReq.getParam("SSTL_NO2"))); //재료번호2
				jrYdMsg.setField("YD_CRN_WRK_SH"		, commUtils.trim(gdReq.getParam("MTL_SH"))); //작업매수
				jrYdMsg.setField("YD_CRN_SCH_ID"		, commUtils.trim(gdReq.getParam("YD_CRN_SCH_ID"))); //야드크레인스케줄ID

				EJBConnector sndConn = new EJBConnector("default", "GdsYsL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvN5YSL007", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
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
	} // end of trtCcStatSet
	
	/**
	 * 임시적치대 인출요구
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord reqTyBedCarryOut(GridData gdReq) throws DAOException {
		String methodNm = "임시적치대 인출요구[GdsYsJspSeEJB.reqTyBedCarryOut] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			//대상 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			String sYS_STK_COL_GP = commUtils.trim(gdReq.getParam("YS_STK_COL_GP"));
			String sSSTL_NO1 = null;
			String sSSTL_NO2 = null;
			String sSSTL_NO3 = null;
			int iCnt = 0;
			String sBED_NO_OLD = null;
			String sBED_NO = null;
			String sYD_RCPT_PLN_STR_LOC_OLD = null;
			String sYD_RCPT_PLN_STR_LOC = null;
			
			String ohcWbookId = null;
			String ohcSchCd = null;
			
			JDTORecord jrYdMsg = null;
				
			
			commUtils.printLog(logId, methodNm + "rowCnt: " + rowCnt, "SL", gdReq);
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				if(ii == 0) {
					sBED_NO_OLD = commUtils.getValue(gdReq, "YS_STK_BED_NO", ii);
					sYD_RCPT_PLN_STR_LOC_OLD = commUtils.getValue(gdReq, "YD_RCPT_PLN_STR_LOC", ii);
				}
				
				sBED_NO = commUtils.getValue(gdReq, "YS_STK_BED_NO", ii);
				sYD_RCPT_PLN_STR_LOC = commUtils.getValue(gdReq, "YD_RCPT_PLN_STR_LOC", ii);
				
				commUtils.printLog(logId, methodNm + "sBED_NO_OLD: " + sBED_NO_OLD, "SL", gdReq);

				if(sBED_NO_OLD.equals(sBED_NO) && sYD_RCPT_PLN_STR_LOC_OLD.equals(sYD_RCPT_PLN_STR_LOC)) {
					
					//BED번호와 입고예정위치가 동일 할 경우만 수행
					if(iCnt == 0) {
						sSSTL_NO1  = commUtils.getValue(gdReq, "SSTL_NO", ii); 
					} else if(iCnt == 1) {
						sSSTL_NO2  = commUtils.getValue(gdReq, "SSTL_NO", ii); 
					} else if(iCnt == 2){
						sSSTL_NO3  = commUtils.getValue(gdReq, "SSTL_NO", ii); 
					}
					iCnt++;
				}
				
				/* 작업예약 단위결정 (해당요건충족시 다음 작업예약으로 생성)
				 * 1. 베드 변화 체크 
				 * 2. 목적예정위치 (작업했다가 취소하면  달라질 수도 있으니 주의..) 
				 * 3. 작업예약 재료 3개
				 * 4. 작업예약 재료 2개 (목적동 KA)
				 * 5. 마지막
				 * */
				if(!sBED_NO_OLD.equals(sBED_NO) 
						||!sYD_RCPT_PLN_STR_LOC_OLD.equals(sYD_RCPT_PLN_STR_LOC) 
						||iCnt == 3
						||(sYS_STK_COL_GP.startsWith("KA") && sYD_RCPT_PLN_STR_LOC.startsWith("KA") && !sYD_RCPT_PLN_STR_LOC.startsWith("KATY") && iCnt==2)
						||ii == rowCnt-1) {
					
					ohcWbookId = commDao.getSeqId(logId, methodNm, "WrkBook"); //OHC crane 용 작업예약ID
					
					ohcSchCd = sYS_STK_COL_GP.substring(0,4) + "01" + "LM"; //스케줄코드 (ex:KATY01LM)
					
					jrParam.setField("OHC_YD_WBOOK_ID"	, ohcWbookId);  	// crane 작업예약ID
					jrParam.setField("OHC_YD_SCH_CD"	, ohcSchCd); 		// crane 스케줄 코드
					jrParam.setField("YS_STK_COL_GP"	, sYS_STK_COL_GP); 	
					jrParam.setField("SSTL_NO1"			, sSSTL_NO1); 	
					jrParam.setField("SSTL_NO2"			, sSSTL_NO2);
					jrParam.setField("SSTL_NO3"			, sSSTL_NO3); 	
					
					iCnt = 0;
					sSSTL_NO1  = null;
					sSSTL_NO2  = null;
					sSSTL_NO3  = null;
					
					//1.1) OHC Crane 작업예약재료 생성 
					commDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insOhcLmWbMtl", logId, methodNm, "OHC Crane 입고작업예약재료 생성");
					
					//1.2) OHC Crane 작업예약  생성 
					commDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insOhcLmWbook", logId, methodNm, "OHC Crane 입고작업예약  생성");
					
					if(jrYdMsg == null) {

						//맨 처음 한번만 호출
						
						//2) OHC Crane 스케줄 Main 호출
						//크레인스케줄 전문 - Log ID, Method, 수정자 Set
						jrYdMsg = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));;
						jrYdMsg.setResultCode(logId);	//Log ID
						jrYdMsg.setResultMsg(methodNm);	//Log Method Name
						
						jrYdMsg.setField("JMS_TC_CD", "YSYSJ302");
						jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()  ); //JMSTC생성일시
						jrYdMsg.setField("YD_WBOOK_ID"       , ohcWbookId ); //야드작업예약ID
						jrYdMsg.setField("YD_SCH_CD"         , ohcSchCd   ); //야드스케쥴코드
						jrYdMsg.setField("YD_EQP_ID"         , ""   ); //야드설비ID
						jrYdMsg.setField("YD_SCH_ST_GP"      , "A" ); //야드스케쥴기동구분
						jrYdMsg.setField("YD_SCH_REQ_GP"     , "M"); //야드스케쥴요청구분					
						
						jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
					}
					
				}
				
				if(!sBED_NO_OLD.equals(sBED_NO) || !sYD_RCPT_PLN_STR_LOC_OLD.equals(sYD_RCPT_PLN_STR_LOC)) {
					ii--;
				}
				
				sBED_NO_OLD = sBED_NO;
				sYD_RCPT_PLN_STR_LOC_OLD = sYD_RCPT_PLN_STR_LOC;
			}

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of reqTyBedCarryOut

	/**
	 * 다단 임시적치대 인출요구
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord reqTyMLyrBedCarryOut(GridData gdReq) throws DAOException {
		String methodNm = "다단 임시적치대 인출요구[GdsYsJspSeEJB.reqTyMLyrBedCarryOut] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			//대상 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			String sYS_STK_COL_GP = commUtils.trim(gdReq.getParam("YS_STK_COL_GP"));
			String sSSTL_NO1 = null;
			String sSSTL_NO2 = null;
			String sSSTL_NO3 = null;
			String sSSTL_NO4 = null;
			String sSSTL_NO5 = null;
			String sSSTL_NO6 = null;
			String sSSTL_NO7 = null;
			String sSSTL_NO8 = null;
			String sSSTL_NO9 = null;
			String sSSTL_NO10 = null;
			String sSSTL_NO11 = null;
			String sSSTL_NO12 = null;
			String sSSTL_NO13 = null;
			String sSSTL_NO14 = null;
			String sSSTL_NO15 = null;
			int iCnt = 0;
			String sBED_NO_OLD = null;
			String sBED_NO = null;
			String sYD_RCPT_PLN_STR_LOC_OLD = null;
			String sYD_RCPT_PLN_STR_LOC = null;
			
			String ohcWbookId = null;
			String ohcSchCd = null;
			
			JDTORecord jrYdMsg = null;
				
			
			commUtils.printLog(logId, methodNm + "rowCnt: " + rowCnt, "SL", gdReq);
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				if(ii == 0) {
					sBED_NO_OLD = commUtils.getValue(gdReq, "YS_STK_BED_NO", ii);
					sYD_RCPT_PLN_STR_LOC_OLD = commUtils.getValue(gdReq, "YD_RCPT_PLN_STR_LOC", ii);
				}
				
				sBED_NO = commUtils.getValue(gdReq, "YS_STK_BED_NO", ii);
				sYD_RCPT_PLN_STR_LOC = commUtils.getValue(gdReq, "YD_RCPT_PLN_STR_LOC", ii);
				
				commUtils.printLog(logId, methodNm + "sBED_NO_OLD: " + sBED_NO_OLD + ", sBED_NO: " + sBED_NO+ ", sYD_RCPT_PLN_STR_LOC_OLD : " + sYD_RCPT_PLN_STR_LOC_OLD + ", sYD_RCPT_PLN_STR_LOC : " + sYD_RCPT_PLN_STR_LOC, "SL", gdReq);

				if(sBED_NO_OLD.equals(sBED_NO) && sYD_RCPT_PLN_STR_LOC_OLD.equals(sYD_RCPT_PLN_STR_LOC)) {
					
					//BED번호와 입고예정위치가 동일 할 경우만 수행
					if(iCnt == 0) {
						sSSTL_NO1  = commUtils.getValue(gdReq, "SSTL_NO", ii); 
					} else if(iCnt == 1) {
						sSSTL_NO2  = commUtils.getValue(gdReq, "SSTL_NO", ii); 
					} else if(iCnt == 2){
						sSSTL_NO3  = commUtils.getValue(gdReq, "SSTL_NO", ii); 
					} else if(iCnt == 3) {
						sSSTL_NO4  = commUtils.getValue(gdReq, "SSTL_NO", ii); 
					} else if(iCnt == 4){
						sSSTL_NO5  = commUtils.getValue(gdReq, "SSTL_NO", ii); 
					} else if(iCnt == 5) {
						sSSTL_NO6  = commUtils.getValue(gdReq, "SSTL_NO", ii); 
					} else if(iCnt == 6){
						sSSTL_NO7  = commUtils.getValue(gdReq, "SSTL_NO", ii); 
					} else if(iCnt == 7) {
						sSSTL_NO8  = commUtils.getValue(gdReq, "SSTL_NO", ii); 
					} else if(iCnt == 8){
						sSSTL_NO9  = commUtils.getValue(gdReq, "SSTL_NO", ii); 
					} else if(iCnt == 9) {
						sSSTL_NO10  = commUtils.getValue(gdReq, "SSTL_NO", ii); 
					} else if(iCnt == 10){
						sSSTL_NO11  = commUtils.getValue(gdReq, "SSTL_NO", ii); 
					} else if(iCnt == 11) {
						sSSTL_NO12  = commUtils.getValue(gdReq, "SSTL_NO", ii); 
					} else if(iCnt == 12){
						sSSTL_NO13  = commUtils.getValue(gdReq, "SSTL_NO", ii); 
					} else if(iCnt == 13) {
						sSSTL_NO14  = commUtils.getValue(gdReq, "SSTL_NO", ii); 
					} else if(iCnt == 14){
						sSSTL_NO15  = commUtils.getValue(gdReq, "SSTL_NO", ii); 
					}
					iCnt++;
				}
				
				/* 작업예약 단위결정 (해당요건충족시 다음 작업예약으로 생성)
				 * 1. 베드 변화 체크 
				 * 2. 목적예정위치 (작업했다가 취소하면  달라질 수도 있으니 주의..) 
				 * 3. 작업예약 재료 15개
				 * 4. 마지막
				 * */
				if(	  !sBED_NO_OLD.equals(sBED_NO) 
					||!sYD_RCPT_PLN_STR_LOC_OLD.equals(sYD_RCPT_PLN_STR_LOC)
					||iCnt == 15
					||ii == rowCnt-1) {
					
					ohcWbookId = commDao.getSeqId(logId, methodNm, "WrkBook"); //OHC crane 용 작업예약ID
					
					ohcSchCd = sYS_STK_COL_GP.substring(0,4) + "01" + "LM"; //스케줄코드 (ex:KATY01LM)
					
					jrParam.setField("OHC_YD_WBOOK_ID"	, ohcWbookId);  	// crane 작업예약ID
					jrParam.setField("OHC_YD_SCH_CD"	, ohcSchCd); 		// crane 스케줄 코드
					jrParam.setField("YS_STK_COL_GP"	, sYS_STK_COL_GP); 	
					jrParam.setField("SSTL_NO1"			, sSSTL_NO1); 	
					jrParam.setField("SSTL_NO2"			, sSSTL_NO2); 	
					jrParam.setField("SSTL_NO3"			, sSSTL_NO3);
					jrParam.setField("SSTL_NO4"			, sSSTL_NO4); 	
					jrParam.setField("SSTL_NO5"			, sSSTL_NO5); 	
					jrParam.setField("SSTL_NO6"			, sSSTL_NO6);
					jrParam.setField("SSTL_NO7"			, sSSTL_NO7); 	
					jrParam.setField("SSTL_NO8"			, sSSTL_NO8); 	
					jrParam.setField("SSTL_NO9"			, sSSTL_NO9);
					jrParam.setField("SSTL_NO10"		, sSSTL_NO10); 	
					jrParam.setField("SSTL_NO11"		, sSSTL_NO11); 	
					jrParam.setField("SSTL_NO12"		, sSSTL_NO12);
					jrParam.setField("SSTL_NO13"		, sSSTL_NO13); 	
					jrParam.setField("SSTL_NO14"		, sSSTL_NO14); 	
					jrParam.setField("SSTL_NO15"		, sSSTL_NO15);
					
					iCnt = 0;
					sSSTL_NO1	= null;
					sSSTL_NO2	= null;
					sSSTL_NO3	= null;
					sSSTL_NO4	= null;
					sSSTL_NO5	= null;
					sSSTL_NO6	= null;
					sSSTL_NO7	= null;
					sSSTL_NO8	= null;
					sSSTL_NO9	= null;
					sSSTL_NO10  = null;
					sSSTL_NO11  = null;
					sSSTL_NO12  = null;
					sSSTL_NO13  = null;
					sSSTL_NO14  = null;
					sSSTL_NO15  = null;
					
					//1.1) OHC Crane 작업예약재료 생성 
					commDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insOhcLmWbMtlMLyr", logId, methodNm, "OHC Crane 입고작업예약재료 생성");
					
					//1.2) OHC Crane 작업예약  생성 
					commDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insOhcLmWbook", logId, methodNm, "OHC Crane 입고작업예약  생성");
					
					if(jrYdMsg == null) {

						//맨 처음 한번만 호출
						
						//2) OHC Crane 스케줄 Main 호출
						//크레인스케줄 전문 - Log ID, Method, 수정자 Set
						jrYdMsg = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));;
						jrYdMsg.setResultCode(logId);	//Log ID
						jrYdMsg.setResultMsg(methodNm);	//Log Method Name
						
						jrYdMsg.setField("JMS_TC_CD", "YSYSJ302");
						jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()  ); //JMSTC생성일시
						jrYdMsg.setField("YD_WBOOK_ID"       , ohcWbookId ); //야드작업예약ID
						jrYdMsg.setField("YD_SCH_CD"         , ohcSchCd   ); //야드스케쥴코드
						jrYdMsg.setField("YD_EQP_ID"         , ""   ); //야드설비ID
						jrYdMsg.setField("YD_SCH_ST_GP"      , "A" ); //야드스케쥴기동구분
						jrYdMsg.setField("YD_SCH_REQ_GP"     , "M"); //야드스케쥴요청구분					
						
						jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
					}
					
				}
				
				if(!sBED_NO_OLD.equals(sBED_NO) || !sYD_RCPT_PLN_STR_LOC_OLD.equals(sYD_RCPT_PLN_STR_LOC)) {
					ii--;
				}
				
				sBED_NO_OLD = sBED_NO;
				sYD_RCPT_PLN_STR_LOC_OLD = sYD_RCPT_PLN_STR_LOC;
			}

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of reqTyMLyrBedCarryOut	
	
	/**
	 *      [A] 오퍼레이션명 : RGV BookOut 등록
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtRgvBookOut(GridData gdReq) throws DAOException {
		String methodNm = "RGV BookOut등록 [GdsYsJspSeEJB.trtRgvBookOut] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			String ydEqpId  = commUtils.trim(gdReq.getParam("YD_EQP_ID" ));	//야드설비ID(RGV)
			String ydEqpGp  = commUtils.trim(gdReq.getParam("YD_EQP_GP" ));	//야드설비구분
			String infoGp 	= commUtils.trim(gdReq.getParam("INFO_GP"));	//정보구분
			String sstlNo  	= commUtils.trim(gdReq.getParam("SSTL_NO" ));	//특수강재료번호
			String modifier = commUtils.trim(gdReq.getParam("userid"      ));	//수정자
			String currDate = commUtils.getDateTime14();						//현재시각

			if ("".equals(ydEqpId)) {
				throw new Exception("설비ID가 없습니다.");
			}
			
			if ("".equals(sstlNo)) {
				throw new Exception("재료번호가 없습니다.");
			}

			//Return Value
			JDTORecord jrRtn = null;

			//EJB Call을 위한 Message 생성용 - Log ID, Method, 수정자 Set
			JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, modifier);
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name
			
			jrYdMsg.setField("JMS_TC_CD", "N5YSL008"); //선재자동창고 RGV BOOKOUT 정보(N5YSL008)
			jrYdMsg.setField("YD_EQP_ID", ydEqpId); //야드설비ID
			jrYdMsg.setField("YD_EQP_GP", ydEqpGp); //야드설비구분
			jrYdMsg.setField("INFO_GP", infoGp); //정보구분
			jrYdMsg.setField("SSTL_NO", sstlNo); //특수강재료번호

			EJBConnector sndConn = new EJBConnector("default", "GdsYsL2RcvSeEJB", this);
			jrRtn = (JDTORecord)sndConn.trx("rcvN5YSL008", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of trtCcStatSet
	
	/**
	 * 저장위치 수정 - 저장품등록 및 변경전 정합성 체크
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord insGdsYsStock(GridData gdReq) throws DAOException {
		String methodNm = "저장위치 수정 - 저장품등록 및 변경전 정합성 체크[GdsYsJspSeEJB.insGdsYsStock] < " + gdReq.getNavigateValue();
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
				
				//제품번호가 야드저장품에 존재하는지, 작업예약과 크레인스케줄에 잡혀있는 대상인지를 가져오고 번들공통에서 현재진도코드를 가져온다. 
				jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getIsEnableStlNo", logId, methodNm, "제품번호 작업대상여부 조회");
				
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
						commDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insBdlYdStock", logId, methodNm, "저장품 등록");
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
	} // end of insGdsYsStock
	
	/**
	 * B동 Bed 정리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord reSetBedStat(GridData gdReq) throws DAOException {
		String methodNm = "B동 Bed 정리[GdsYsJspSeEJB.reSetBedStat] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			//1) B동 01,02 스판에서 활성상태가 'C'비활성화 인 대상을 'L'적치가능 으로 변경한다. 
			commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updStkBedActStatReSetAll", logId, methodNm, "적치Bed 활성상태 초기화3");
			
			//2) B동 01,02 스판에 제품이 적치된 Bed를 찾아 최하단 재료의 길이 구분에 따라 관련 Bed의 활성상태를 변경한다.
			commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updStkBedActStatAll", logId, methodNm, "적치Bed 활성상태 변경3");

			//3) BED적치단이 비어있는경우 MAX값 7로 일괄변경한다.(오주원주임요청)
			commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updStkBedLyrMax", logId, methodNm, "적치BedLyr Max값 7 변경3");
		
			
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of reSetBedStat

	/**
	 *선재 CRANE 권상처리 (PDA)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord regLoadPda(JDTORecord recPara) throws DAOException {
		
		String methodNm = "선재 CRANE 권상처리 (PDA) [GdsYsJspSeEJB.regLoadPda] < " + recPara.getResultMsg();
		String logId = recPara.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+", recPara);

			//Return Value
			JDTORecord jrRtn = null;
			
			String szYD_EQP_ID = commUtils.trim(recPara.getFieldString("YD_EQP_ID"));
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(recPara.getFieldString("userid")));

			String L2_MC = commUtils.getYsGpBayToL2(szYD_EQP_ID);     // L2구분  반환 (D동:N5, E동:N3)
			
			jrParam.setField("YD_EQP_ID"		, szYD_EQP_ID);
			
			JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.gdsCrnStsSetPp", logId, methodNm, "크레인작업지시조회");			
			
			if(jsCrnSch.size() <= 0) {
				jrRtn = JDTORecordFactory.getInstance().create();
				jrRtn.setField("RETN_CD", YsConstant.RETN_CD_FAILURE);
				jrRtn.setField("RETN_MSG", "크레인 스케줄 조회 실패! ");
				
				return jrRtn; 
			}
			
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name				
			jrParam.setField("JMS_TC_CD"       		, L2_MC +"YSL005"); //크레인권상실적
			jrParam.setField("YD_EQP_ID"			, szYD_EQP_ID);
			jrParam.setField("YD_EQP_WRK_MODE"		, "9"); //야드설비작업Mode(Backup)
			jrParam.setField("YD_WRK_PROG_STAT"		, "2"); //야드작업진행상태(권상완료)
			jrParam.setField("YD_SCH_CD"			, jsCrnSch.getRecord(0).getFieldString("YD_SCH_CD"));
			jrParam.setField("YD_CRN_SCH_ID"		, jsCrnSch.getRecord(0).getFieldString("YD_CRN_SCH_ID"));
			jrParam.setField("YS_UP_WR_LOC"			, jsCrnSch.getRecord(0).getFieldString("YS_UP_WO_LOC"));
			jrParam.setField("YS_UP_WR_LAYER"		, jsCrnSch.getRecord(0).getFieldString("YS_UP_WO_LAYER"));
			jrParam.setField("YD_UP_WO_LOC_XAXIS"	, jsCrnSch.getRecord(0).getFieldString("YD_UP_WO_LOC_XAXIS"));
			jrParam.setField("YD_UP_WO_LOC_YAXIS"	, jsCrnSch.getRecord(0).getFieldString("YD_UP_WO_LOC_YAXIS"));
			jrParam.setField("YD_UP_WO_LOC_ZAXIS"	, jsCrnSch.getRecord(0).getFieldString("YD_UP_WO_LOC_ZAXIS"));
			
			EJBConnector sndConn = new EJBConnector("default","GdsYsL2RcvSeEJB", this);
			jrRtn = (JDTORecord)sndConn.trx("rcv"+ L2_MC +"YSL005", new Class[] { JDTORecord.class }, new Object[] { jrParam });

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
	} // end of regLoadPda
	
	/**
	 *선재 CRANE 권하처리 (PDA)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  JDTORecord
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord regUnLoadPda(JDTORecord recPara) throws DAOException {
		
		String methodNm = "선재 CRANE 권하처리 (PDA) [GdsYsJspSeEJB.regUnLoadPda] < " + recPara.getResultMsg();
		String logId = recPara.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+", recPara);

			//Return Value
			JDTORecord jrRtn = null;
			
			String szYD_EQP_ID = commUtils.trim(recPara.getFieldString("YD_EQP_ID"));
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(recPara.getFieldString("userid")));

			String L2_MC = commUtils.getYsGpBayToL2(szYD_EQP_ID);     // L2구분  반환 (D동:N5, E동:N3)
			
			jrParam.setField("YD_EQP_ID"		, szYD_EQP_ID);
			
			JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.gdsCrnStsSetPp", logId, methodNm, "크레인작업지시조회");			
			
			if(jsCrnSch.size() <= 0) {
				jrRtn = JDTORecordFactory.getInstance().create();
				jrRtn.setField("RETN_CD", YsConstant.RETN_CD_FAILURE);
				jrRtn.setField("RETN_MSG", "크레인 스케줄 조회 실패! ");
				
				return jrRtn; 
			}
			
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name				
			jrParam.setField("JMS_TC_CD"       		, L2_MC +"YSL005"); //크레인권상실적
			jrParam.setField("YD_EQP_ID"			, szYD_EQP_ID);
			jrParam.setField("YD_EQP_WRK_MODE"		, "9"); //야드설비작업Mode(Backup)
			jrParam.setField("YD_WRK_PROG_STAT"		, "4"); //야드작업진행상태(권하완료)
			jrParam.setField("YD_SCH_CD"			, jsCrnSch.getRecord(0).getFieldString("YD_SCH_CD"));
			jrParam.setField("YD_CRN_SCH_ID"		, jsCrnSch.getRecord(0).getFieldString("YD_CRN_SCH_ID"));
			jrParam.setField("YS_DN_WR_LOC"			, jsCrnSch.getRecord(0).getFieldString("YS_DN_WO_LOC"));
			jrParam.setField("YS_DN_WR_LAYER"		, jsCrnSch.getRecord(0).getFieldString("YS_DN_WO_LAYER"));
			jrParam.setField("YD_DN_WO_LOC_XAXIS"	, jsCrnSch.getRecord(0).getFieldString("YD_DN_WO_LOC_XAXIS"));
			jrParam.setField("YD_DN_WO_LOC_YAXIS"	, jsCrnSch.getRecord(0).getFieldString("YD_DN_WO_LOC_YAXIS"));
			jrParam.setField("YD_DN_WO_LOC_ZAXIS"	, jsCrnSch.getRecord(0).getFieldString("YD_DN_WO_LOC_ZAXIS"));
			
			EJBConnector sndConn = new EJBConnector("default","GdsYsL2RcvSeEJB", this);
			jrRtn = (JDTORecord)sndConn.trx("rcv"+ L2_MC +"YSL006", new Class[] { JDTORecord.class }, new Object[] { jrParam });

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
	} // end of regUnLoadPda
	
	/**
	 * Carry-Out완료전송
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord sndCarryOutDone(GridData gdReq) throws DAOException {
		String methodNm = "Carry-Out완료전송[GdsYsJspSeEJB.sndCarryOutDone] < " + gdReq.getNavigateValue();
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
			
			
			// CARRY-OUT 완료 실적을 전송한다.
			if("PC".equals(szYsStkColGp.substring(2,4))) {
				
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
					
					if(!"".equals(commUtils.getValue(gdReq, "SSTL_NO", ii))) {
						iWkShCnt++;
						jrParam.setField("SSTL_NO"+(ii+1), commUtils.getValue(gdReq, "SSTL_NO", ii));
					}
				}					

				if(iWkShCnt > 0) {
					jrParam.setField("YD_EQP_ID", szYsStkColGp );
					jrParam.setField("YS_STK_BED_NO", "21");
					jrParam.setField("YD_STK_BED_STL_SH", ""+iWkShCnt);
					jrParam.setField("YD_EQP_WRK_SH", ""+iWkShCnt);
					
					//장입이상재 Carry-out 완료 송신
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSM6L101BackUp", jrParam));
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
	 * 설비인출보급 - CARRY-OUT BackUp
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord reqCarryOutBackUp(GridData gdReq) throws DAOException {
		String methodNm = "설비인출보급 - CARRY-OUT BackUp[GdsYsJspSeEJB.reqCarryOutBackUp] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			//대상 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			String sYS_STK_COL_GP = commUtils.trim(gdReq.getParam("YS_STK_COL_GP"));
			String sSSTL_NO1 = null;
			String sSSTL_NO2 = null;
			String sSSTL_NO3 = null;
			int iCnt = 0;
			//String sBED_NO_OLD = null;
			//String sBED_NO = null;
			String sYD_RCPT_PLN_STR_LOC_OLD = null;
			String sYD_RCPT_PLN_STR_LOC = null;
			
			String ohcWbookId = null;
			String ohcSchCd = null;
			
			JDTORecord jrYdMsg = null;
				
			String szMsg = "";
			
			commUtils.printLog(logId, methodNm + "rowCnt: " + rowCnt, "SL", gdReq);
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				if(ii == 0) {
					//sBED_NO_OLD = commUtils.getValue(gdReq, "YS_STK_BED_NO", ii);
					sYD_RCPT_PLN_STR_LOC_OLD = commUtils.getValue(gdReq, "YD_RCPT_PLN_STR_LOC", ii);
				}
				
				//sBED_NO = commUtils.getValue(gdReq, "YS_STK_BED_NO", ii);
				sYD_RCPT_PLN_STR_LOC = commUtils.getValue(gdReq, "YD_RCPT_PLN_STR_LOC", ii);
				
				commUtils.printLog(logId, methodNm + "sYD_RCPT_PLN_STR_LOC_OLD: " + sYD_RCPT_PLN_STR_LOC_OLD, "SL", gdReq);
				
				String sHmiStat 		= "N";
				
				String sQuery1			= "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.SelectWBHMISTAT";
				JDTORecord wbJr 		= (new com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO()).getData(sQuery1, new Object[]{ "2XRT07" });
				if (wbJr != null){ 
					sHmiStat	= StringHelper.evl(wbJr.getFieldString("HMI_STAT"), "");
				}
				
				if("Y".equals(sHmiStat)){
					
					String sTmpSstlNo = commUtils.getValue(gdReq, "SSTL_NO", ii); 
					
					szMsg="■■■■■■■■■■ 입고작업 중복체크1 : sTmpSstlNo="+sTmpSstlNo;
					commUtils.printLog(logId, szMsg, "SL");
					
					JDTORecord jrParamTmp 	= JDTORecordFactory.getInstance().create();
					jrParamTmp.setField("SSTL_NO" , sTmpSstlNo     ); //번들번호
					
					JDTORecordSet jsWbMtlTmp = commDao.select(jrParamTmp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getblMvStkWrkBookMtlDup", logId, methodNm, "재료번호로 조회");
					
					jsWbMtlTmp.absolute(1);
					JDTORecord jrWbMtlTmp  = jsWbMtlTmp.getRecord();
					String sDupCnt = commUtils.trim(jrWbMtlTmp.getFieldString("IS_DUP_CNT"));
					
					if(!"0".equals(sDupCnt)){
						
						szMsg="■■■■■■■■■■ 입고작업 중복체크2 : sTmpSstlNo=" + sDupCnt + "sDupCnt" ;
						commUtils.printLog(logId, szMsg, "SL");
						continue;
					}
				}

				if(sYD_RCPT_PLN_STR_LOC_OLD.equals(sYD_RCPT_PLN_STR_LOC)) {
					
					//BED번호와 입고예정위치가 동일 할 경우만 수행
					if(iCnt == 0) {
						sSSTL_NO1  = commUtils.getValue(gdReq, "SSTL_NO", ii); 
					} else if(iCnt == 1) {
						sSSTL_NO2  = commUtils.getValue(gdReq, "SSTL_NO", ii); 
					} else if(iCnt == 2){
						sSSTL_NO3  = commUtils.getValue(gdReq, "SSTL_NO", ii); 
					}
					iCnt++;
				}
				
				if((iCnt>1 && rowCnt>2) ||  rowCnt<=2)  //3매 이상 입고 처리시 1매 낱장단위의 작업 생성 안함.  2022.06.05 박종호 오주원 주임 요청: 일괄처리 기능. 일괄처리시 한장남는거는 처리 안되도록.
				{
					if( !sYD_RCPT_PLN_STR_LOC_OLD.equals(sYD_RCPT_PLN_STR_LOC) 
						||iCnt == 3
						||(sYS_STK_COL_GP.startsWith("KA") && sYD_RCPT_PLN_STR_LOC.startsWith("KA") && !sYD_RCPT_PLN_STR_LOC.startsWith("KATY") && iCnt==2)
						||ii == rowCnt-1) {
						
						ohcWbookId = commDao.getSeqId(logId, methodNm, "WrkBook"); //OHC crane 용 작업예약ID
						
						ohcSchCd = sYS_STK_COL_GP.substring(0,6) + "LM"; //스케줄코드 (ex:KAPC01LM)  : KA->TS , KA->TC 모두 포함
						jrParam.setField("OHC_YD_WBOOK_ID"	, ohcWbookId);  	// crane 작업예약ID
						jrParam.setField("OHC_YD_SCH_CD"	, ohcSchCd); 		// crane 스케줄 코드
						jrParam.setField("YS_STK_COL_GP"	, sYS_STK_COL_GP); 	
						jrParam.setField("SSTL_NO1"			, sSSTL_NO1); 	
						jrParam.setField("SSTL_NO2"			, sSSTL_NO2); 	
						jrParam.setField("SSTL_NO3"			, sSSTL_NO3); 	
						
						int tmpiCnt=iCnt; //만적조건 판단위해 임시 저장.
						iCnt = 0;
						sSSTL_NO1  = null;
						sSSTL_NO2  = null;
						sSSTL_NO3  = null;
						
						//1.1) OHC Crane 작업예약재료 생성 
						commDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insOhcLmWbMtl", logId, methodNm, "OHC Crane 입고작업예약재료 생성");
						//여기 작업예약 생성할때, YD_AIM_GP, YD_TP_LOC_GUIDE값 여기서 넣고있는지 확인필요 EX)WB:202207280200084685  AIM_BAY:B, YD_TO_LOC:KB01
						//1.2) OHC Crane 작업예약  생성 
						commDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insOhcLmWbook", logId, methodNm, "OHC Crane 입고작업예약  생성");

						
						//작업예약 생성후, 목적동, TO위치 가이드등 변경 필요(A->A 입고중 만적시,  B->B입고중 만적시)
						//별도 메소드 생성 필요(만적여부, 공베드의 SC, TS 정상 여부 체크 및 만적부합시, 목적동,TO 위치 가이드 등 변경하기)
						//carry out시 만적여부 판단해서 c야드로 가는 부분 제거(재료 등록시, 목적동 셋팅시 판단하는것으로 변경)
						/*
						if( (sYS_STK_COL_GP.startsWith("KA") &&sYD_RCPT_PLN_STR_LOC.startsWith("KA"))
							||(	sYS_STK_COL_GP.startsWith("KB")&&sYD_RCPT_PLN_STR_LOC.startsWith("KA"))	
						    ){ //최종 목적동이 A동이면 A동 만적여부 체크 후 만적이면 목적동을 B동으로 변경함.
							boolean isFull=false; //만적여부 체크
							JDTORecord jrParam2 	= JDTORecordFactory.getInstance().create();
							JDTORecord outRecResult=null;
							String szYS_STK_COL_GP="";
							String szYS_STK_BED_NO="";
							String szYS_STK_LYR_NO="";
							String szYS_DN_WO_LOC="";
							String szYS_DN_WO_LAYER="";
							
							
							jrParam2.setField("HEAT_NO"			, "");//heat 필요 x
							jrParam2.setField("CUST_CD", 		""); //cust 필요x
							jrParam2.setField("DETAIL_ARR_CD", 	"");//상세착지 필요x
							jrParam2.setField("BUNDLE_T", 	commUtils.getValue(gdReq, "YD_MTL_T", ii));//두께 필요o
							jrParam2.setField("SH_CNT", 	""+tmpiCnt);//재료매수 필요o
						
							JDTORecordSet jsIsBedFull = commDao.select(jrParam2, "com.inisteel.cim.ys.common.dao.YsCommDAO.getWrkMoveRbA", logId, methodNm, "저장가능 공베드 조회");
							if(jsIsBedFull.size()<=0){  //조회안되면 만적
								szMsg=" 가능 공베드 없음(만적)" ;
								commUtils.printLog(logId, szMsg, "SL");
								isFull=true;
							}
							for(int i = 1; i <= jsIsBedFull.size(); i++) { //공베드가 있더라도 Traverser BED 가능 여부 조회 필요.
								jsIsBedFull.absolute(i);
								outRecResult  = jsIsBedFull.getRecord();
								szYS_STK_COL_GP = commUtils.trim(outRecResult.getFieldString("YS_STK_COL_GP"  ));
								szYS_STK_BED_NO = commUtils.trim(outRecResult.getFieldString("YS_STK_BED_NO"  ));
								szYS_STK_LYR_NO = commUtils.trim(outRecResult.getFieldString("YS_STK_LYR_NO"  ));
								
								if(szYS_STK_COL_GP.substring(4,6).equals("01")||szYS_STK_COL_GP.substring(4,6).equals("02") ) {
									szYS_DN_WO_LOC      = "KATS0101";
									szYS_DN_WO_LAYER 	= "01";
								} else if(szYS_STK_COL_GP.substring(4,6).equals("03")||szYS_STK_COL_GP.substring(4,6).equals("04") ) {
									szYS_DN_WO_LOC      = "KATS0201";
									szYS_DN_WO_LAYER 	= "01";
								} else if(szYS_STK_COL_GP.substring(4,6).equals("05")||szYS_STK_COL_GP.substring(4,6).equals("06") ) {
									szYS_DN_WO_LOC      = "KATS0301";
									szYS_DN_WO_LAYER 	= "01";
								} else if(szYS_STK_COL_GP.substring(4,6).equals("07")||szYS_STK_COL_GP.substring(4,6).equals("08") ) {
									szYS_DN_WO_LOC      = "KATS0401";
									szYS_DN_WO_LAYER 	= "01";
								} else if(szYS_STK_COL_GP.substring(4,6).equals("09")||szYS_STK_COL_GP.substring(4,6).equals("10") ) {
									szYS_DN_WO_LOC      = "KATS0501";
									szYS_DN_WO_LAYER 	= "01";
								} else if(szYS_STK_COL_GP.substring(4,6).equals("11")||szYS_STK_COL_GP.substring(4,6).equals("12") ) {
									szYS_DN_WO_LOC      = "KATS0601";
									szYS_DN_WO_LAYER 	= "01";
								} else if(szYS_STK_COL_GP.substring(4,6).equals("13")||szYS_STK_COL_GP.substring(4,6).equals("14") ) {
									szYS_DN_WO_LOC      = "KATS0701";
									szYS_DN_WO_LAYER 	= "01";
								}

								jrParam2.setField("YS_STK_COL_GP", 	szYS_DN_WO_LOC.substring(0, 6));		
								jrParam2.setField("YS_STK_BED_NO", 	szYS_DN_WO_LOC.substring(6));		
								jrParam2.setField("YS_STK_LYR_NO", 	szYS_DN_WO_LAYER);
								JDTORecordSet jsTRBedl = commDao.select(jrParam2, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedAnalysisRbA", logId, methodNm, "Traverser 가능BED 조회");
								
								if (jsTRBedl.size() <= 0) {
									szMsg=" 가능 Traverser베드 없음(만적)" ;
									commUtils.printLog(logId, szMsg, "SL");
									isFull=true;
									break;
								}
							}
							
							if(isFull){  //목적동:A동에서 만적시 =>목적동을 B동으로, 목적위치를 KBC1으로, 대차계획을:KXTC01 변경해준다.
								jrParam2.setField("YD_AIM_BAY_GP", 	"B");
								jrParam2.setField("YD_TO_LOC_GUIDE", 	"KBC1");
								jrParam2.setField("YD_WBOOK_ID"			, ohcWbookId);
								commDao.update(jrParam2, "com.inisteel.cim.ys.common.dao.YsCommDAO.updWrkBookAimdong", logId, methodNm, "TB_YS_WRKBOOK");
							}
						   
						}
						*/
						//반납 재 입고 시 반납구분 삭제 작업(SPST_FRTOMOVE_GP)
						jrParam.setField("YD_WBOOK_ID"			, ohcWbookId); 	
		    			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.upSpstFrtomoveStock", logId, methodNm, "TB_YS_STOCK");
		    			
						//if(jrYdMsg == null) {
		    			//chito 2017.08.16 오주원 주임님 요청으로 모든 크레인 기동
							//맨 처음 한번만 호출
							
							//2) OHC Crane 스케줄 Main 호출
							//크레인스케줄 전문 - Log ID, Method, 수정자 Set
							jrYdMsg = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));;
							jrYdMsg.setResultCode(logId);	//Log ID
							jrYdMsg.setResultMsg(methodNm);	//Log Method Name
							
							jrYdMsg.setField("JMS_TC_CD", "YSYSJ302");
							jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()  ); //JMSTC생성일시
							jrYdMsg.setField("YD_WBOOK_ID"       , ohcWbookId ); //야드작업예약ID
							jrYdMsg.setField("YD_SCH_CD"         , ohcSchCd   ); //야드스케쥴코드
							jrYdMsg.setField("YD_EQP_ID"         , ""   ); //야드설비ID
							jrYdMsg.setField("YD_SCH_ST_GP"      , "A" ); //야드스케쥴기동구분
							jrYdMsg.setField("YD_SCH_REQ_GP"     , "M"); //야드스케쥴요청구분					
							
							jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
						//}
						
					}
			    }
				
				if(!sYD_RCPT_PLN_STR_LOC_OLD.equals(sYD_RCPT_PLN_STR_LOC)) {
					ii--;
				}
				
				sYD_RCPT_PLN_STR_LOC_OLD = sYD_RCPT_PLN_STR_LOC;
			}

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of reqCarryOutBackUp
	


	/**
	 * 설비인출보급 - CARRY-OUT allBackUp
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord reqCarryOutAllBackUp(GridData gdReq) throws DAOException {
		String methodNm = "설비인출보급 - CARRY-OUT AllBackUp[GdsYsJspSeEJB.reqCarryOutAllBackUp] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			//대상 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String sYS_STK_COL_GP = commUtils.trim(gdReq.getParam("YS_STK_COL_GP"));
			String sSSTL_NO1 = null;
			String sSSTL_NO2 = null;
			String sSSTL_NO3 = null;
			int iCnt = 0;
			
			int[]		gdCntList		=   new int[rowCnt+1];
			ArrayList ohcWbookIdList =   new ArrayList();
			
			int num_A = 0;
			int num_B = 0;
			String sEqp = "";
			
			
			String ohcWbookId = null;
			int wbookCnt = 0;
			String ohcSchCd = null;
			
			JDTORecord jrYdMsg = null;
			
			String szMsg = "";
			
			commUtils.printLog(logId, methodNm + "rowCnt: " + rowCnt, "SL", gdReq);
			
			
			// A동 B동 중 물량이 더 많은 것 부터 크레인 스케줄 생성, 같을경우 A동부터 처리
			for(int i=0; i<rowCnt; i++){
				if("KA01".equals(commUtils.getValue(gdReq, "YD_RCPT_PLN_STR_LOC", i))) num_A++;
				else num_B++;
			}
			
			if(num_A<num_B) sEqp = "KB01";
			else sEqp = "KA01";
			
			for(int i=0; i<2; i++){
				for(int ii=rowCnt-1; ii>=0; ii--){
					if(i==0){ 
						if(sEqp.equals(commUtils.getValue(gdReq, "YD_RCPT_PLN_STR_LOC", ii))){
							gdCntList[iCnt] = ii;
							iCnt++;
							}
						}
					else{
						if(!sEqp.equals(commUtils.getValue(gdReq, "YD_RCPT_PLN_STR_LOC", ii))){
							gdCntList[iCnt] = ii;
							iCnt++;
						}
					}					
				}
				
			}
			iCnt = 0;
			
			for(int i=0;i<rowCnt;i++){
				
				int key = gdCntList[i];
				String sHmiStat 		= "N";
				
				String sQuery1			= "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.SelectWBHMISTAT";
				JDTORecord wbJr 		= (new com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO()).getData(sQuery1, new Object[]{ "2XRT07" });
				if (wbJr != null){ 
					sHmiStat	= StringHelper.evl(wbJr.getFieldString("HMI_STAT"), "");
				}
				
				if("Y".equals(sHmiStat)){
					
					String sTmpSstlNo = commUtils.getValue(gdReq, "SSTL_NO", key); 
					
					szMsg="■■■■■■■■■■ 입고작업 중복체크1 : sTmpSstlNo="+sTmpSstlNo;
					commUtils.printLog(logId, szMsg, "SL");
					
					JDTORecord jrParamTmp 	= JDTORecordFactory.getInstance().create();
					jrParamTmp.setField("SSTL_NO" , sTmpSstlNo     ); //번들번호
					
					JDTORecordSet jsWbMtlTmp = commDao.select(jrParamTmp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getblMvStkWrkBookMtlDup", logId, methodNm, "재료번호로 조회");
					
					jsWbMtlTmp.absolute(1);
					JDTORecord jrWbMtlTmp  = jsWbMtlTmp.getRecord();
					String sDupCnt = commUtils.trim(jrWbMtlTmp.getFieldString("IS_DUP_CNT"));
					
					if(!"0".equals(sDupCnt)){
						
						szMsg="■■■■■■■■■■ 입고작업 중복체크2 : sTmpSstlNo=" + sDupCnt + "sDupCnt" ;
						commUtils.printLog(logId, szMsg, "SL");
						continue;
					}
				}
				
				if("KA01".equals(commUtils.getValue(gdReq, "YD_RCPT_PLN_STR_LOC", key))) {
					//A동인 경우 번들 2개 셋팅
					if(iCnt == 0) {
						sSSTL_NO1  = commUtils.getValue(gdReq, "SSTL_NO", key); 
					} else if(iCnt == 1) {
						sSSTL_NO2  = commUtils.getValue(gdReq, "SSTL_NO", key); 
					}
					iCnt++;
					
				}
				else if("KB01".equals(commUtils.getValue(gdReq, "YD_RCPT_PLN_STR_LOC", key))) {
					//B동인 경우 번들 3개 셋팅
					if(iCnt == 0) {
						sSSTL_NO1  = commUtils.getValue(gdReq, "SSTL_NO", key); 
					} else if(iCnt == 1) {
						sSSTL_NO2  = commUtils.getValue(gdReq, "SSTL_NO", key); 
					} else if(iCnt == 2){
						sSSTL_NO3  = commUtils.getValue(gdReq, "SSTL_NO", key); 
					}
					iCnt++;
				}
				
				if((iCnt>1 && rowCnt>2) ||  rowCnt<=2)  //3매 이상 입고 처리시 1매 낱장단위의 작업 생성 안함.  2022.06.05 박종호 오주원 주임 요청: 일괄처리 기능. 일괄처리시 한장남는거는 처리 안되도록.
				{
					if( iCnt == 3
						|| (iCnt ==2 && "KA01".equals(commUtils.getValue(gdReq, "YD_RCPT_PLN_STR_LOC", key)))) {
						
						ohcWbookId = commDao.getSeqId(logId, methodNm, "WrkBook"); //OHC crane 용 작업예약ID
						
						ohcSchCd = sYS_STK_COL_GP.substring(0,6) + "LM"; //스케줄코드 (ex:KAPC01LM)  : KA->TS , KA->TC 모두 포함
						jrParam.setField("OHC_YD_WBOOK_ID"	, ohcWbookId);  	// crane 작업예약ID
						jrParam.setField("OHC_YD_SCH_CD"	, ohcSchCd); 		// crane 스케줄 코드
						jrParam.setField("YS_STK_COL_GP"	, sYS_STK_COL_GP); 	
						jrParam.setField("SSTL_NO1"			, sSSTL_NO1); 	
						jrParam.setField("SSTL_NO2"			, sSSTL_NO2); 	
						jrParam.setField("SSTL_NO3"			, sSSTL_NO3); 	
						
						iCnt = 0;
						sSSTL_NO1  = null;
						sSSTL_NO2  = null;
						sSSTL_NO3  = null;
						
						//1.1) OHC Crane 작업예약재료 생성 
						commDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insOhcLmWbMtl", logId, methodNm, "OHC Crane 입고작업예약재료 생성");
						//여기 작업예약 생성할때, YD_AIM_GP, YD_TP_LOC_GUIDE값 여기서 넣고있는지 확인필요 EX)WB:202207280200084685  AIM_BAY:B, YD_TO_LOC:KB01
						//1.2) OHC Crane 작업예약  생성 
						commDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insOhcLmWbook", logId, methodNm, "OHC Crane 입고작업예약  생성");

						//반납 재 입고 시 반납구분 삭제 작업(SPST_FRTOMOVE_GP)
						jrParam.setField("YD_WBOOK_ID"			, ohcWbookId); 	
		    			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.upSpstFrtomoveStock", logId, methodNm, "TB_YS_STOCK");
		    			
						//if(jrYdMsg == null) {
		    			//chito 2017.08.16 오주원 주임님 요청으로 모든 크레인 기동
							//맨 처음 한번만 호출
		    			ohcWbookIdList.add(wbookCnt, ohcWbookId);
		    			wbookCnt++;

					}
			    }
				
			}
			
			//2) OHC Crane 스케줄 Main 호출
			//크레인스케줄 전문 - Log ID, Method, 수정자 Set
			jrYdMsg = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));;
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name
			
			jrYdMsg.setField("JMS_TC_CD", "YSYSJ314");
			jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()  ); //JMSTC생성일시
			jrYdMsg.setField("YD_WBOOK_CNT", wbookCnt-1); //JMSTC생성일시
			
			for(int j=0; j<wbookCnt; j++){
				jrYdMsg.setField("YD_WBOOK_ID"+(j+1)       , ohcWbookIdList.get(j) ); //야드작업예약ID
			}			
			
			jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
			return jrRtn;
		
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of reqCarryOutAllBackUp
	
	
	
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
 
    	JDTORecordSet rsrstDataSch =  JDTORecordFactory.getInstance().createRecordSet("YD");
    	
		try{
 
			for(int x=0 ; x < recMsg.length ;x++){
				recPara = JDTORecordFactory.getInstance().create();
	
				// 1.  작업예약 ID ,크레인 ID , 입력받은 스케줄 우선순위
				recPara.setField("YD_WBOOK_ID" 	, recMsg[x].getField("YD_WBOOK_ID"));
				recPara.setField("YD_SCH_PRIOR" , recMsg[x].getField("YD_SCH_PRIOR"));
				intSchPrior = recMsg[x].getFieldInt("YD_SCH_PRIOR");	
				
				// 2. 작업 예약 정보 변경
				commDao.update(recPara, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updcrnChgSchPriorWbook", "SYSTEM", "crnChgSchPrior", "TB_YS_WRKBOOK");
				
				// 3. 작업예약에 편성된 스케줄정보 조회	
				
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID" 	, recMsg[x].getField("YD_WBOOK_ID"));
				recPara.setField("YD_WRK_PROG_STAT" ,"W");					
				
				rsrstDataSch= commDao.select(recPara, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.crnChgSchPrior", "SYSTEM", "crnChgSchPrior", "크레인작업지시조회");			
				 
				
				if(rsrstDataSch.size() <= 0) {				
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
					commDao.update(recPara, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updcrnChgSchPrior", "SYSTEM", "crnChgSchPrior", "TB_YS_CRNSCH");
					
				}while(rsrstDataSch.next());		
				
		}
			
		}catch(Exception e){
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		}
		
		bool = true;
 
		return new Boolean(bool);
		
	}
	/**
	 *      [A] 오퍼레이션명 :선재 제품단위 이적지시 -크레인작업SCH등록
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updgdsWrMoveWojm1(GridData gdReq) throws DAOException {
		String methodNm = "선재 제품단위 이적지시[GdsYsJspSeEJB.updgdsWrMoveWojm1] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//Return Value
			JDTORecord jrRtn = null;
			JDTORecordSet jsMsg = JDTORecordFactory.getInstance().createRecordSet("");	//크레인스케줄 정리를 위함

			String stlNos        = commUtils.trim(gdReq.getParam("SSTL_NOS"        )); //재료번호들
			String ysStkColGp    = commUtils.trim(gdReq.getParam("YS_STK_COL_GP"   )); //FROM 위치
			String ydToLocGuide1 = commUtils.trim(gdReq.getParam("YD_TO_LOC_GUIDE" )); //FR위치__야드To위치Guide
			String ydToLocGuide2 = commUtils.trim(gdReq.getParam("YD_TO_LOC_GUIDE" )); //TO위치__야드To위치Guide
			String modifier      = commUtils.trim(gdReq.getParam("userid"));

			if (ysStkColGp.length() > 6) {
				ysStkColGp = ysStkColGp.substring(0, 6);
			}
			
			//To위치가 동까지만 있으면 위치검색Bed 기준 적용

			if ("".equals(stlNos)) {
				throw new Exception("이적 재료번호가 없습니다.");
			} else if ("".equals(ysStkColGp) || ysStkColGp.length() < 4) {
				throw new Exception("Span[" + ysStkColGp + "] 정보가 없습니다.");
			} else if ("".equals(ydToLocGuide1) || ydToLocGuide1.length() < 6) {
				throw new Exception("TO 위치정보가 없습니다.");
			} 
			
			/**********************************************************
			* 1. 스케줄코드 설정
			**********************************************************/
			String ydSchCd    = ""; //야드스케쥴코드
			String toSchCd    = ""; //동간이적 to위치스케쥴코드
			
			String ydBayGp    = ysStkColGp.substring(1, 2); //야드동구분
			String ydAimBayGp = ""; //야드목표동구분

			if ("".equals(ydToLocGuide1)) {
				ydAimBayGp = ysStkColGp.substring(1, 2);  	//위치검색Bed기준 적용
			} else {
				ydAimBayGp = ydToLocGuide1.substring(1, 2);  	//To위치지정
			}

			//스케쥴코드 생성
//			String ydEqpGp = ysStkColGp.substring(2, 4); //야드설비구분(이적 스케줄코드 생성용)
			
			//To위치Guide가 있으면 그 값으로 스케줄코드 생성
//			if ("".equals(ydToLocGuide1)) {
//				ydEqpGp = ysStkColGp.substring(2, 4);
//			}

			if(ydBayGp.equals("D")) {
				String toCol =  ""; 
				String ysCol =  ""; 
				commUtils.printLog(logId, "ysStkColGp : " +  ysStkColGp, "SL");
				commUtils.printLog(logId, "ydToLocGuide1 : " +  ydToLocGuide1, "SL");
				if(ysStkColGp.equals("KD0101") && (ydToLocGuide1.substring(0, 6).equals("KD0101") || ydToLocGuide1.substring(0, 6).equals("KD0102"))){
					ydSchCd = "KDYD01MM";
				} else if(ysStkColGp.equals("KD0102") && (ydToLocGuide1.substring(0, 6).equals("KD0101") || ydToLocGuide1.substring(0, 6).equals("KD0102"))){	
					ydSchCd = "KDYD01MM";
				} else if(ysStkColGp.equals("KD0103") && (ydToLocGuide1.substring(0, 6).equals("KD0103") || ydToLocGuide1.substring(0, 6).equals("KD0104"))){	
					ydSchCd = "KDYD02MM";
				} else if(ysStkColGp.equals("KD0104") && (ydToLocGuide1.substring(0, 6).equals("KD0103") || ydToLocGuide1.substring(0, 6).equals("KD0104"))){	
					ydSchCd = "KDYD02MM";
				} else if(ysStkColGp.equals("KD0105") && (ydToLocGuide1.substring(0, 6).equals("KD0105") || ydToLocGuide1.substring(0, 6).equals("KD0106"))){	
					ydSchCd = "KDYD03MM";
				} else if(ysStkColGp.equals("KD0106") && (ydToLocGuide1.substring(0, 6).equals("KD0105") || ydToLocGuide1.substring(0, 6).equals("KD0106"))){	
					ydSchCd = "KDYD03MM";
				} else if(ysStkColGp.equals("KD0107") && (ydToLocGuide1.substring(0, 6).equals("KD0107") || ydToLocGuide1.substring(0, 6).equals("KD0108"))){	
					ydSchCd = "KDYD04MM";
				} else if(ysStkColGp.equals("KD0108") && (ydToLocGuide1.substring(0, 6).equals("KD0107") || ydToLocGuide1.substring(0, 6).equals("KD0108"))){	
					ydSchCd = "KDYD04MM";
				} else if(ysStkColGp.equals("KD0109") && (ydToLocGuide1.substring(0, 6).equals("KD0109") || ydToLocGuide1.substring(0, 6).equals("KD0109"))){	
					ydSchCd = "KDYD05MM";
				} else if(ysStkColGp.equals("KD0110") && (ydToLocGuide1.substring(0, 6).equals("KD0110") || ydToLocGuide1.substring(0, 6).equals("KD0111"))){	
					ydSchCd = "KDYD06MM";
				} else if(ysStkColGp.equals("KD0111") && (ydToLocGuide1.substring(0, 6).equals("KD0110") || ydToLocGuide1.substring(0, 6).equals("KD0111"))){	
					ydSchCd = "KDYD06MM";
				} else {
					//동간이적 
					// HS -> CELL
					ysCol = ysStkColGp.substring(4, 6);
					
					if( ysCol.equals("01")|| ysCol.equals("02") ){
						toCol =  "01"; 
					} else if( ysCol.equals("03")|| ysCol.equals("04") ){
						toCol =  "02"; 
					} else if( ysCol.equals("05")|| ysCol.equals("06") ){
						toCol =  "03"; 
					} else if( ysCol.equals("07")|| ysCol.equals("08") ){
						toCol =  "04"; 
					} else if( ysCol.equals("09") ){
						toCol =  "05"; 
					} else if( ysCol.equals("10")|| ysCol.equals("11") ){
						toCol =  "06"; 
					}
					
					ydSchCd = "KDYD" + toCol + "DM";
                   
					
					
					
					// HS -> CELL
					ysCol = ydToLocGuide2.substring(4, 6);
					
					if( ysCol.equals("01")|| ysCol.equals("02") ){
						toCol =  "01"; //'KDSC01'
					} else if( ysCol.equals("03")|| ysCol.equals("04") ){
						toCol =  "02"; //'KDSC01'
					} else if( ysCol.equals("05")|| ysCol.equals("06") ){
						toCol =  "03"; //'KDSC01'
					} else if( ysCol.equals("07")|| ysCol.equals("08") ){
						toCol =  "04"; //'KDSC01'
					} else if( ysCol.equals("09") ){
						toCol =  "05"; //'KDSC01'
					} else if( ysCol.equals("10")|| ysCol.equals("11") ){
						toCol =  "06"; //'KDSC01'
					}
					
					toSchCd = "KDYD"+toCol+"DM";  //stacker crane 용 스케줄코드 (ex:KDYD01DM)
				}
			} else {	
				ydSchCd = "KEYD01MM";
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
			
			commUtils.printLog(logId, "ydSchCd : " +  ydSchCd, "SL");
			
			/**********************************************************
			* 2. 이적 작업예약 등록
			**********************************************************/
			jrParam.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
			jrParam.setField("YD_TO_LOC_GUIDE" , ydToLocGuide1 ); //야드To위치Guide
			
			/**********************************************************
			* 1. 야드스케쥴코드 Check
			**********************************************************/
			JDTORecord jrSchCd = gdsYsComm.chkSchCdEqp(jrParam);
			
			String ydGp       = ydSchCd.substring(0, 1);	//야드구분
			String ydEqpId    = commUtils.trim(jrSchCd.getFieldString("YD_EQP_ID"   ));	//야드설비ID(크레인)
			String ydSchPrior = commUtils.trim(jrSchCd.getFieldString("YD_SCH_PRIOR"));	//야드스케쥴우선순위
			String ydToLocDcsnMtd = "S"; //야드TO위치결정방법(스케줄지정)

			if ("".equals(ydAimBayGp)) {
				ydAimBayGp = ydBayGp;
			}

			if (!"".equals(ydToLocGuide1)) {
				ydToLocDcsnMtd = "F";	//야드TO위치결정방법(지정위치)
			}
			
			/**********************************************************
			* 2. 크레인사양 분리
			**********************************************************/
			Vector vcLot = this.setCrnSpecSpr(jrSchCd, jsWbMtl);

			JDTORecordSet jsLotMtl = null;
			int lotCnt   = vcLot.size();	//크레인사양 분리 작업예약수
			int lotMtlSh = 0;				//작업예약재료매수
			String ydWbookId = "";			//야드작업예약ID
			String ydWbookIdFst = "";		//야드작업예약ID(첫번째)
			
			JDTORecord jrRow = null;
			JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, modifier);
			
			/**SJH********************************************************
			* 2. 01 단 빈곳 검색 
			**********************************************************/
			String[] bedMtl = new String[100];	//Bed재료정보
			for (int ii = 0; ii < 100; ii++) {
				bedMtl[ii] = "";
			}
			JDTORecord jrWbMtl1 = commUtils.getParam(logId, methodNm, modifier);
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getblMvStkWrkBookMtlPp1
			SELECT YS_STK_BED_NO 
			  FROM TB_YS_STKLYR
			 WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
			   AND YS_STK_LYR_NO = '01'
			   AND SSTL_NO IS NULL
			*/   
			JDTORecordSet jsWbMtl1 = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getblMvStkWrkBookMtlPp1", logId, methodNm, "재료번호로 조회");
			for(int i = 1; i <= jsWbMtl1.size(); i++) {

				jsWbMtl1.absolute(i);
				jrWbMtl1  = jsWbMtl1.getRecord();
				bedMtl[i-1] = commUtils.trim(jrWbMtl1.getFieldString("YS_STK_BED_NO"));		//가이드 TEMP
			}	
			
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
//				jrParam.setField("YD_TO_LOC_GUIDE"   , ydToLocGuide1); //야드To위치Guide
				jrParam.setField("YD_TO_LOC_GUIDE"   , ydToLocGuide1.substring(0,6)+bedMtl[ii]+"01" ); //야드To위치Guide
						
				commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBook", logId, methodNm, "TB_YS_WRKBOOK");

				//작업예약재료 등록
				
				for (int jj = 0; jj < lotMtlSh; jj++) {

					jrRow = jsLotMtl.getRecord(jj);
					
					JDTORecord jrRtn1 = JDTORecordFactory.getInstance().create();
					
					jrRtn1.setField("YD_WBOOK_ID"   , ydWbookId     ); //야드작업예약ID
					jrRtn1.setField("SSTL_NO"       , commUtils.trim(jrRow.getFieldString("SSTL_NO"       )));	//재료번호
					jrRtn1.setField("YS_STK_COL_GP" , commUtils.trim(jrRow.getFieldString("YS_STK_COL_GP")));	//야드적치열구분
					jrRtn1.setField("YS_STK_BED_NO" , commUtils.trim(jrRow.getFieldString("YS_STK_BED_NO")));	//야드적치Bed번호
					jrRtn1.setField("YS_STK_LYR_NO" , commUtils.trim(jrRow.getFieldString("YS_STK_LYR_NO")));	//야드적치단번호
					jrRtn1.setField("YS_STK_SEQ_NO" , commUtils.trim(jrRow.getFieldString("YS_STK_SEQ_NO")));	//야드적치SEQ번호
					jrRtn1.setField("MODIFIER"     	, modifier      );												//등록자
					commDao.insert(jrRtn1, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBookMtl", logId, methodNm, "TB_YS_WRKBOOKMTL");
				}
				

				//크레인스케줄 전문 - Log ID, Method, 수정자 Set
				
				if(ydEqpId.substring(2, 4).equals("SC")) {
					jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setField("YD_WBOOK_ID"     , ydWbookId ); //야드작업예약ID(첫번째꺼만)
					jrYdMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
					jrYdMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
					jrYdMsg.setField("YD_SCH_ST_GP"    , "M"          ); //야드스케쥴기동구분(Manual)
					jrYdMsg.setField("YD_SCH_REQ_GP"   , "M"          ); //야드스케쥴요청구분(이적)
					jsMsg.addRecord(jrYdMsg);
				}	
			}

			if(!ydEqpId.substring(2, 4).equals("SC")) {
				jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setField("YD_WBOOK_ID"     , ydWbookIdFst ); //야드작업예약ID(첫번째꺼만)
				jrYdMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
				jrYdMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
				jrYdMsg.setField("YD_SCH_ST_GP"    , "M"          ); //야드스케쥴기동구분(Manual)
				jrYdMsg.setField("YD_SCH_REQ_GP"   , "M"          ); //야드스케쥴요청구분(이적)
				jsMsg.addRecord(jrYdMsg);

			}
			/**********************************************************
			* 3.  크레인별 스케줄 전송
			**********************************************************/
			
			jrRtn = commUtils.addSndData(jrRtn, this.setCrnSchMsg(jsMsg, logId, methodNm));
			
			if(!toSchCd.equals("")) {
				commUtils.printLog(logId, "toSchCd : " +  toSchCd, "SL");
				commUtils.printLog(logId, "ydToLocGuideD : " +  ydToLocGuide2, "SL");

				/**********************************************************
				* 2. 동간이적 to 작업예약 등록
				**********************************************************/
				jrParam.setField("YD_SCH_CD"       , toSchCd      ); //야드스케쥴코드
				jrParam.setField("YD_TO_LOC_GUIDE" , ydToLocGuide2 ); //야드To위치Guide
				
				/**********************************************************
				* 1. 야드스케쥴코드 Check
				**********************************************************/
				jrSchCd = JDTORecordFactory.getInstance().create();
				jrSchCd = gdsYsComm.chkSchCdEqp(jrParam);
				
				ydGp       = ydSchCd.substring(0, 1);	//야드구분
				ydEqpId    = commUtils.trim(jrSchCd.getFieldString("YD_EQP_ID"   ));	//야드설비ID(크레인)
				ydSchPrior = commUtils.trim(jrSchCd.getFieldString("YD_SCH_PRIOR"));	//야드스케쥴우선순위
				ydToLocDcsnMtd = "S"; //야드TO위치결정방법(스케줄지정)
	
				if ("".equals(ydAimBayGp)) {
					ydAimBayGp = ydBayGp;
				}
	
				if (!"".equals(ydToLocGuide2)) {
					ydToLocDcsnMtd = "F";	//야드TO위치결정방법(지정위치)
				}
				
				/**********************************************************
				* 2. 크레인사양 분리
				**********************************************************/
				vcLot = this.setCrnSpecSpr(jrSchCd, jsWbMtl); 
	
				lotCnt   = vcLot.size();	//크레인사양 분리 작업예약수
				lotMtlSh = 0;				//작업예약재료매수
				ydWbookId = "";			//야드작업예약ID
				
				jrRow = JDTORecordFactory.getInstance().create();
				jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg = commUtils.getParam(logId, methodNm, modifier);
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
					jrParam.setField("YD_SCH_CD"         , toSchCd       ); //야드스케쥴코드
					jrParam.setField("YD_SCH_PRIOR"      , ydSchPrior    ); //야드스케쥴우선순위
					jrParam.setField("YD_SCH_PROG_STAT"  , "W"           ); //야드스케쥴진행상태(스케줄수행대기)
					jrParam.setField("YD_SCH_ST_GP"      , "M"           ); //야드스케쥴기동구분(Manual)
					jrParam.setField("YD_SCH_REQ_GP"     , "M"           ); //야드스케쥴요청구분(이적)
					jrParam.setField("YD_AIM_YD_GP"      , ydGp          ); //야드목표야드구분
					jrParam.setField("YD_AIM_BAY_GP"     , ydAimBayGp    ); //야드목표동구분
					jrParam.setField("YD_TO_LOC_DCSN_MTD", ydToLocDcsnMtd); //야드TO위치결정방법
					jrParam.setField("YD_TO_LOC_GUIDE"   , ydToLocGuide2  ); //야드To위치Guide
				
					commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBook", logId, methodNm, "TB_YS_WRKBOOK");
	
					//작업예약재료 등록
					
					for (int jj = 0; jj < lotMtlSh; jj++) {
	
						jrRow = jsLotMtl.getRecord(jj);
						
						JDTORecord jrRtn1 = JDTORecordFactory.getInstance().create();
						
						jrRtn1.setField("YD_WBOOK_ID"   , ydWbookId     ); //야드작업예약ID
						jrRtn1.setField("SSTL_NO"       , commUtils.trim(jrRow.getFieldString("SSTL_NO"       )));	//재료번호
						jrRtn1.setField("YS_STK_COL_GP" , commUtils.trim(jrRow.getFieldString("YS_STK_COL_GP")));	//야드적치열구분
						jrRtn1.setField("YS_STK_BED_NO" , commUtils.trim(jrRow.getFieldString("YS_STK_BED_NO")));	//야드적치Bed번호
						jrRtn1.setField("YS_STK_LYR_NO" , commUtils.trim(jrRow.getFieldString("YS_STK_LYR_NO")));	//야드적치단번호
						jrRtn1.setField("YS_STK_SEQ_NO" , commUtils.trim(jrRow.getFieldString("YS_STK_SEQ_NO")));	//야드적치SEQ번호
						jrRtn1.setField("MODIFIER"     	, modifier      );												//등록자
						commDao.insert(jrRtn1, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBookMtl", logId, methodNm, "TB_YS_WRKBOOKMTL");
					}
					
//SJH	
//					//크레인스케줄 전문 - Log ID, Method, 수정자 Set
//					
//					if(ydEqpId.substring(2, 4).equals("SC")) {
//						jrYdMsg = JDTORecordFactory.getInstance().create();
//						jrYdMsg.setField("YD_WBOOK_ID"     , ydWbookId ); //야드작업예약ID(첫번째꺼만)
//						jrYdMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
//						jrYdMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
//						jrYdMsg.setField("YD_SCH_ST_GP"    , "M"          ); //야드스케쥴기동구분(Manual)
//						jrYdMsg.setField("YD_SCH_REQ_GP"   , "M"          ); //야드스케쥴요청구분(이적)
//						jsMsg.addRecord(jrYdMsg);
//					}	
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
	 * 설비인출보급 - CARRY-OUT BackUp PDA
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord reqCarryOutBackUpPda(String userid , String YS_STK_BED_NO, JDTORecord[] inRecordSet) throws DAOException {
		String methodNm = "설비인출보급 - CARRY-OUT BackUp[GdsYsJspSeEJB.reqCarryOutBackUpPda] < ";
		String logId = commUtils.getLogId();
		
		JDTORecord inRecord			= JDTORecordFactory.getInstance().create();
		JDTORecord inRecord2		= JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, userid);

			//대상 레코드 수
//			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			int rowCnt = inRecordSet.length;
			
			String sYS_STK_COL_GP = YS_STK_BED_NO;
			String sSSTL_NO1 = null;
			String sSSTL_NO2 = null;
			String sSSTL_NO3 = null;
			int iCnt = 0;
			//String sBED_NO_OLD = null;
			//String sBED_NO = null;
			String sYD_RCPT_PLN_STR_LOC_OLD = null;
			String sYD_RCPT_PLN_STR_LOC = null;
			
			String ohcWbookId = null;
			String ohcSchCd = null;
			
			JDTORecord jrYdMsg = null;
			
			EJBConnector sndConn = null;
				
			String szMsg = "";
			
			//commUtils.printLog(logId, methodNm + "rowCnt: " + rowCnt, "SL", gdReq);
			for (int ii = 0; ii < rowCnt; ii++) {
				
				inRecord = JDTORecordFactory.getInstance().create();
				inRecord2 = JDTORecordFactory.getInstance().create();
				
				inRecord = inRecordSet[ii];
				
				if(ii == 0) {
					//sYD_RCPT_PLN_STR_LOC_OLD = commUtils.getValue(gdReq, "YD_RCPT_PLN_STR_LOC", ii);
					sYD_RCPT_PLN_STR_LOC_OLD = inRecordSet[ii].getFieldString("YD_RCPT_PLN_STR_LOC");
				}
				
//				sYD_RCPT_PLN_STR_LOC = commUtils.getValue(gdReq, "YD_RCPT_PLN_STR_LOC", ii);
				sYD_RCPT_PLN_STR_LOC = inRecord.getFieldString("YD_RCPT_PLN_STR_LOC"); 
				
				commUtils.printLog(logId, methodNm + "sYD_RCPT_PLN_STR_LOC_OLD: " + sYD_RCPT_PLN_STR_LOC_OLD, "SL");
				
				String sHmiStat 		= "N";
				
				String sQuery1			= "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.SelectWBHMISTAT";
				JDTORecord wbJr 		= (new com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO()).getData(sQuery1, new Object[]{ "2XRT07" });
				if (wbJr != null){ 
					sHmiStat	= StringHelper.evl(wbJr.getFieldString("HMI_STAT"), "");
				}
				
				if("Y".equals(sHmiStat)){
					
					String sTmpSstlNo = inRecord.getFieldString("SSTL_NO");
					
					szMsg="■■■■■■■■■■ 입고작업 중복체크1 : sTmpSstlNo="+sTmpSstlNo;
					commUtils.printLog(logId, szMsg, "SL");
					
					JDTORecord jrParamTmp 	= JDTORecordFactory.getInstance().create();
					jrParamTmp.setField("SSTL_NO" , sTmpSstlNo     ); //번들번호  
					
					JDTORecordSet jsWbMtlTmp = commDao.select(jrParamTmp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getblMvStkWrkBookMtlDup", logId, methodNm, "재료번호로 조회");
					
					jsWbMtlTmp.absolute(1);
					JDTORecord jrWbMtlTmp  = jsWbMtlTmp.getRecord();
					String sDupCnt = commUtils.trim(jrWbMtlTmp.getFieldString("IS_DUP_CNT"));
					
					if(!"0".equals(sDupCnt)){
						
						szMsg="■■■■■■■■■■ 입고작업 중복체크2 : sTmpSstlNo=" + sDupCnt + "sDupCnt" ;
						commUtils.printLog(logId, szMsg, "SL");
						continue;
					}
				}

				if(sYD_RCPT_PLN_STR_LOC_OLD.equals(sYD_RCPT_PLN_STR_LOC)) {
					
					//BED번호와 입고예정위치가 동일 할 경우만 수행
					if(iCnt == 0) {
//						sSSTL_NO1  = commUtils.getValue(gdReq, "SSTL_NO", ii); 
						sSSTL_NO1  = inRecord.getFieldString("SSTL_NO");  
					} else if(iCnt == 1) {
//						sSSTL_NO2  = commUtils.getValue(gdReq, "SSTL_NO", ii); 
						sSSTL_NO2  = inRecord.getFieldString("SSTL_NO");  
					} else if(iCnt == 2){
//						sSSTL_NO3  = commUtils.getValue(gdReq, "SSTL_NO", ii); 
						sSSTL_NO3  = inRecord.getFieldString("SSTL_NO");  
					}
					iCnt++;
				}
				
				if((iCnt>1 && rowCnt>2) ||  rowCnt<=2)  //3매 이상 입고 처리시 1매 낱장단위의 작업 생성 안함.  2022.06.05 박종호 오주원 주임 요청: 일괄처리 기능. 일괄처리시 한장남는거는 처리 안되도록.
				{
						if( !sYD_RCPT_PLN_STR_LOC_OLD.equals(sYD_RCPT_PLN_STR_LOC) 
							||iCnt == 3
							||(sYS_STK_COL_GP.startsWith("KA") && sYD_RCPT_PLN_STR_LOC.startsWith("KA") && !sYD_RCPT_PLN_STR_LOC.startsWith("KATY") && iCnt==2)
							||ii == rowCnt-1) {
							
							ohcWbookId = commDao.getSeqId(logId, methodNm, "WrkBook"); //OHC crane 용 작업예약ID
							szMsg="■■■■■■ 생성 작업예약 ID : ohcWbookId="+ohcWbookId;
							commUtils.printLog(logId, szMsg, "SL");
							
							ohcSchCd = sYS_STK_COL_GP.substring(0,6) + "LM"; //스케줄코드 (ex:KAPC01LM)
							
							jrParam.setField("OHC_YD_WBOOK_ID"	, ohcWbookId);  	// crane 작업예약ID
							jrParam.setField("OHC_YD_SCH_CD"	, ohcSchCd); 		// crane 스케줄 코드
							jrParam.setField("YS_STK_COL_GP"	, sYS_STK_COL_GP); 	
							jrParam.setField("SSTL_NO1"			, sSSTL_NO1); 	
							jrParam.setField("SSTL_NO2"			, sSSTL_NO2); 	
							jrParam.setField("SSTL_NO3"			, sSSTL_NO3); 	
							
							iCnt = 0;
							sSSTL_NO1  = null;
							sSSTL_NO2  = null;
							sSSTL_NO3  = null;
							
							//1.1) OHC Crane 작업예약재료 생성 
							commDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insOhcLmWbMtl", logId, methodNm, "OHC Crane 입고작업예약재료 생성");
							
							//1.2) OHC Crane 작업예약  생성 
							commDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insOhcLmWbook", logId, methodNm, "OHC Crane 입고작업예약  생성");
							
							//반납 재 입고 시 반납구분 삭제 작업(SPST_FRTOMOVE_GP)
							jrParam.setField("YD_WBOOK_ID"			, ohcWbookId); 	
			    			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.upSpstFrtomoveStock", logId, methodNm, "TB_YS_STOCK");
			    			
							//if(jrYdMsg == null) {
			    			//chito 2017.08.16 오주원 주임님 요청으로 모든 크레인 기동
								//맨 처음 한번만 호출
							//정종균책임님 가이드: SEQID 할당받는 부분이랑 작업예약 INSERT부분을 별도 TRANSACTION으로 분리해서 처리(new required).
			    			//->T/R의 범위를 좁혀서 해당 작업이 끝나기전 다른 T/R이 들어와서 이중처리되는 현상 방지되도록.
			    			//딜레이(작업예약 생성 딜레이)로인해 서로 다른 작업예약 ID로 같은 작업 두번생성되는 경우 존재함.(사용자가 딜레이 기다리다 두번 클릭한 경우)
			    			//이런 경우 대비하여, 동일 작업, 동일 재료의 작업예약 아이디가 존재하는 flag sql 조회하고, flag에 걸리면, 작업예약/작업재료 삭제하고, 스케줄 기동도 안하도록
			    			//2022.06.27 박종호 오주원주임 문의사항:동일 작업 중복생성되는 현상
			    			//if(!중복 작업예약 존재)
								//2) OHC Crane 스케줄 Main 호출
								//크레인스케줄 전문 - Log ID, Method, 수정자 Set
								jrYdMsg = commUtils.getParam(logId, methodNm, userid);
								jrYdMsg.setResultCode(logId);	//Log ID
								jrYdMsg.setResultMsg(methodNm);	//Log Method Name
								
								jrYdMsg.setField("JMS_TC_CD", "YSYSJ302");
								jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()  ); //JMSTC생성일시
								jrYdMsg.setField("YD_WBOOK_ID"       , ohcWbookId ); //야드작업예약ID
								jrYdMsg.setField("YD_SCH_CD"         , ohcSchCd   ); //야드스케쥴코드
								jrYdMsg.setField("YD_EQP_ID"         , ""   ); //야드설비ID
								jrYdMsg.setField("YD_SCH_ST_GP"      , "A" ); //야드스케쥴기동구분
								jrYdMsg.setField("YD_SCH_REQ_GP"     , "M"); //야드스케쥴요청구분					
								
								jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
								
							//}
							
						}
				}
				
				sYD_RCPT_PLN_STR_LOC_OLD = sYD_RCPT_PLN_STR_LOC;
			}

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of reqCarryOutBackUpPda
	
	
	/**
	 * 설비인출보급 - CARRY-OUT BackUp PDA
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord reqCarryOutBackUpPda2(String userid , String YS_STK_BED_NO, JDTORecord[] inRecordSet) throws DAOException {
		String methodNm = "설비인출보급 - CARRY-OUT BackUp[GdsYsJspSeEJB.reqCarryOutBackUpPda] < ";
		String logId = commUtils.getLogId();
		
		JDTORecord inRecord			= JDTORecordFactory.getInstance().create();
		JDTORecord inRecord2		= JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, userid);

			//대상 레코드 수
//			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			int rowCnt = inRecordSet.length;

			int[]		gdCntList		=   new int[rowCnt];
			ArrayList ohcWbookIdList =   new ArrayList();
			
			String sYS_STK_COL_GP = YS_STK_BED_NO;
			String sSSTL_NO1 = null;
			String sSSTL_NO2 = null;
			String sSSTL_NO3 = null;
			int iCnt = 0;
			//String sBED_NO_OLD = null;
			//String sBED_NO = null;
			String sYD_RCPT_PLN_STR_LOC_OLD = null;
			String sYD_RCPT_PLN_STR_LOC = null;
			
			String ohcWbookId = null;
			String ohcSchCd = null;
			
			JDTORecord jrYdMsg = null;

			EJBConnector sndConn = null;
				
			String szMsg = "";

			
			int num_A = 0;
			int num_B = 0;
			String sEqp = "";
			int wbookCnt = 0;
			

			// A동 B동 중 물량이 더 많은 것 부터 크레인 스케줄 생성, 같을경우 A동부터 처리
			for(int i=0; i<rowCnt; i++){
				if("KA01".equals(inRecordSet[i].getFieldString("YD_RCPT_PLN_STR_LOC"))) 
					num_A++;
				else if ("KB01".equals(inRecordSet[i].getFieldString("YD_RCPT_PLN_STR_LOC"))) 
					num_B++;
			}
			
			if(num_A<num_B) sEqp = "KB01";
			else sEqp = "KA01";
			for(int i=0; i<4; i++){
				for(int ii=0; ii<rowCnt; ii++){
					if(i==0){ 
						if(sEqp.equals(inRecordSet[ii].getFieldString("YD_RCPT_PLN_STR_LOC"))){
							gdCntList[iCnt] = ii;
							iCnt++;
							}
						}
					else if(i==1){
						if(!sEqp.equals(inRecordSet[ii].getFieldString("YD_RCPT_PLN_STR_LOC")) 
								&&("KA01".equals( inRecordSet[ii].getFieldString("YD_RCPT_PLN_STR_LOC"))
								|| "KB01".equals( inRecordSet[ii].getFieldString("YD_RCPT_PLN_STR_LOC")))){
							gdCntList[iCnt] = ii;
							iCnt++;
						}
					}
					else if(i==2){
						if("KBC1".equals(inRecordSet[ii].getFieldString("YD_RCPT_PLN_STR_LOC"))){
							gdCntList[iCnt] = ii;
							iCnt++;
							}
					}
					else{
							if("KATY".equals(inRecordSet[ii].getFieldString("YD_RCPT_PLN_STR_LOC")) || 
									"KBTY".equals(inRecordSet[ii].getFieldString("YD_RCPT_PLN_STR_LOC"))){
								gdCntList[iCnt] = ii;
								iCnt++;
								}
						}
					}
				}
				
				
			iCnt = 0;	
			for(int i=0;i<rowCnt;i++){
				
				inRecord = JDTORecordFactory.getInstance().create();
				
				inRecord = inRecordSet[gdCntList[i]];
				
				
				if(i == 0) {
					sYD_RCPT_PLN_STR_LOC_OLD = inRecord.getFieldString("YD_RCPT_PLN_STR_LOC");
				}
				sYD_RCPT_PLN_STR_LOC = inRecord.getFieldString("YD_RCPT_PLN_STR_LOC"); 
				
				commUtils.printLog(logId, methodNm + "sYD_RCPT_PLN_STR_LOC_OLD: " + sYD_RCPT_PLN_STR_LOC_OLD, "SL");
				
				
				String sHmiStat 		= "N";
				 
				String sQuery1			= "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.SelectWBHMISTAT";
				JDTORecord wbJr 		= (new com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO()).getData(sQuery1, new Object[]{ "2XRT07" });
				if (wbJr != null){ 
					sHmiStat	= StringHelper.evl(wbJr.getFieldString("HMI_STAT"), "");
				}
				
				if("Y".equals(sHmiStat)){
					
					String sTmpSstlNo =inRecord.getFieldString("SSTL_NO"); 
					
					szMsg="■■■■■■■■■■ 입고작업 중복체크1 : sTmpSstlNo="+sTmpSstlNo;
					commUtils.printLog(logId, szMsg, "SL");
					
					JDTORecord jrParamTmp 	= JDTORecordFactory.getInstance().create();
					jrParamTmp.setField("SSTL_NO" , sTmpSstlNo     ); //번들번호
					
					JDTORecordSet jsWbMtlTmp = commDao.select(jrParamTmp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getblMvStkWrkBookMtlDup", logId, methodNm, "재료번호로 조회");
					
					jsWbMtlTmp.absolute(1);
					JDTORecord jrWbMtlTmp  = jsWbMtlTmp.getRecord();
					String sDupCnt = commUtils.trim(jrWbMtlTmp.getFieldString("IS_DUP_CNT"));
					
					if(!"0".equals(sDupCnt)){
						
						szMsg="■■■■■■■■■■ 입고작업 중복체크2 : sTmpSstlNo=" + sDupCnt + "sDupCnt" ;
						commUtils.printLog(logId, szMsg, "SL");
						continue;
					}
				}
				
				if(sYD_RCPT_PLN_STR_LOC_OLD.equals(sYD_RCPT_PLN_STR_LOC)) {
					
					//BED번호와 입고예정위치가 동일 할 경우만 수행
					if(iCnt == 0) {
						sSSTL_NO1  = inRecord.getFieldString("SSTL_NO");  
					} else if(iCnt == 1) {
						sSSTL_NO2  = inRecord.getFieldString("SSTL_NO");  
					} else if(iCnt == 2){
						sSSTL_NO3  = inRecord.getFieldString("SSTL_NO");  
					}
					iCnt++;
				}
				
				if(iCnt>=1)  //3매 이상 입고 처리시 1매 낱장단위의 작업 생성 안함.  2022.06.05 박종호 오주원 주임 요청: 일괄처리 기능. 일괄처리시 한장남는거는 처리 안되도록.
				{
						if( !sYD_RCPT_PLN_STR_LOC_OLD.equals(sYD_RCPT_PLN_STR_LOC) 
							||iCnt == 3
							|| (sYD_RCPT_PLN_STR_LOC.startsWith("KA") || sYD_RCPT_PLN_STR_LOC.startsWith("KATY")) && iCnt==2
							|| i == rowCnt-1) {
						ohcWbookId = commDao.getSeqId(logId, methodNm, "WrkBook"); //OHC crane 용 작업예약ID
						
						ohcSchCd = sYS_STK_COL_GP.substring(0,6) + "LM"; //스케줄코드 (ex:KAPC01LM)  : KA->TS , KA->TC 모두 포함
						jrParam.setField("OHC_YD_WBOOK_ID"	, ohcWbookId);  	// crane 작업예약ID
						jrParam.setField("OHC_YD_SCH_CD"	, ohcSchCd); 		// crane 스케줄 코드
						jrParam.setField("YS_STK_COL_GP"	, sYS_STK_COL_GP); 	
						jrParam.setField("SSTL_NO1"			, sSSTL_NO1); 	
						jrParam.setField("SSTL_NO2"			, sSSTL_NO2); 	
						jrParam.setField("SSTL_NO3"			, sSSTL_NO3); 	
						
						iCnt = 0;
						sSSTL_NO1  = null;
						sSSTL_NO2  = null;
						sSSTL_NO3  = null;
						
						//1.1) OHC Crane 작업예약재료 생성 
						commDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insOhcLmWbMtl", logId, methodNm, "OHC Crane 입고작업예약재료 생성");
						//여기 작업예약 생성할때, YD_AIM_GP, YD_TP_LOC_GUIDE값 여기서 넣고있는지 확인필요 EX)WB:202207280200084685  AIM_BAY:B, YD_TO_LOC:KB01
						//1.2) OHC Crane 작업예약  생성 
						commDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insOhcLmWbook", logId, methodNm, "OHC Crane 입고작업예약  생성");

						//반납 재 입고 시 반납구분 삭제 작업(SPST_FRTOMOVE_GP)
						jrParam.setField("YD_WBOOK_ID"			, ohcWbookId); 	
		    			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.upSpstFrtomoveStock", logId, methodNm, "TB_YS_STOCK");
		    			
						//if(jrYdMsg == null) {
		    			//chito 2017.08.16 오주원 주임님 요청으로 모든 크레인 기동
							//맨 처음 한번만 호출
		    			ohcWbookIdList.add(wbookCnt, ohcWbookId);
		    			wbookCnt++;

					}
			
			
				}
				
				if(!sYD_RCPT_PLN_STR_LOC_OLD.equals(sYD_RCPT_PLN_STR_LOC)) {
					i--;
					}
				sYD_RCPT_PLN_STR_LOC_OLD = sYD_RCPT_PLN_STR_LOC;
			}
		
			//2) OHC Crane 스케줄 Main 호출
			//크레인스케줄 전문 - Log ID, Method, 수정자 Set
			jrYdMsg = commUtils.getParam(logId, methodNm, userid);
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name
			
			jrYdMsg.setField("JMS_TC_CD", "YSYSJ314");
			jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()  ); //JMSTC생성일시
			jrYdMsg.setField("YD_WBOOK_CNT", wbookCnt); //JMSTC생성일시
			
			for(int j=0; j<wbookCnt; j++){
				jrYdMsg.setField("YD_WBOOK_ID"+(j+1)       , ohcWbookIdList.get(j) ); //야드작업예약ID
			}			
			
			jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
			
			return jrRtn;
		
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of reqCarryOutBackUpPda
	
	
	
	/**
	 * 설비인출보급 - 재료삭제 모바일
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param String userid , JDTORecord[] inRecordSet
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord delPulloutSupMtlMobile(String userid , JDTORecord[] inRecordSet) throws DAOException {
		String methodNm = "설비인출보급 - 재료삭제[GdsYsJspSeEJB.delPulloutSupMtl] < ";
		String logId = commUtils.getLogId();
		//String logId = gdReq.getIPAddress();
		
		JDTORecord inRecord		= JDTORecordFactory.getInstance().create();
		JDTORecord inRecord2		= JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, methodNm, "S+");

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
//			String szBdcCurrProgCd = null;
			String szWbookId = null;
			String szCrnSchId = null;
			String szToLocMtlStat = null;
			
			String szModGp = null; //작업구분

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, userid);

			//수정할 레코드 수
			//int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			int rowCnt = inRecordSet.length;
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				inRecord = JDTORecordFactory.getInstance().create();
				inRecord2 = JDTORecordFactory.getInstance().create();
				
				inRecord = inRecordSet[ii];
				
//				szStlNo 			= commUtils.getValue(gdReq, "SSTL_NO", ii);
				szStlNo 			= inRecord.getFieldString("SSTL_NO");
//				szYsStkColGp 		= commUtils.getValue(gdReq, "YS_STK_COL_GP", ii);
				szYsStkColGp 		= inRecord.getFieldString("YS_STK_COL_GP");
//				szYsStkBedNo 		= commUtils.getValue(gdReq, "YS_STK_BED_NO", ii);
				szYsStkBedNo 		= inRecord.getFieldString("YS_STK_BED_NO");
				szYsStkLyrNo 		= "01";
//				szYsStkSeqNo 		= commUtils.getValue(gdReq, "YS_STK_SEQ_NO", ii);
				szYsStkSeqNo 		= inRecord.getFieldString("YS_STK_SEQ_NO");
				
//				szOldStlNo 			= commUtils.getValue(gdReq, "OLD_SSTL_NO", ii);
				szOldStlNo 			= inRecord.getFieldString("OLD_SSTL_NO");
				
				//삭제처리
				szModGp = "DELETE";
				jrParam.setField("SSTL_NO"	, szOldStlNo );
				
				jrParam.setField("YS_STK_COL_GP"	, szYsStkColGp );
				jrParam.setField("YS_STK_BED_NO"	, szYsStkBedNo );
				jrParam.setField("YS_STK_LYR_NO"	, szYsStkLyrNo );
				jrParam.setField("YS_STK_SEQ_NO"	, szYsStkSeqNo );
				
				//제품번호가 야드저장품에 존재하는지, 작업예약과 크레인스케줄에 잡혀있는 대상인지를 가져오고 번들공통에서 현재진도코드를 가져온다. 
				jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getIsEnableStlNo", logId, methodNm, "제품번호 작업대상여부 조회");
				
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
				if("ADD".equals(szModGp)||"MOVE".equals(szModGp)) {
					if("".equals(szStkStlNo)) {
						throw new Exception("제품번호 : "+jrParam.getFieldString("SSTL_NO")+" 가 TB_YS_STOCK 에 없습니다.");
					}
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
				
				//BLOOM공통 위치정보 수정하기
				if("DELETE".equals(szModGp)) {
					//삭제일경우 처리 ???
				} else {
					//추가, 이동
					jrParam.setField("FNL_REG_PGM"			, "delPulloutSupMtl" );
					jrParam.setField("YD_GP"				, szYsStkColGp.substring(0,1) );
					jrParam.setField("YD_BAY_GP"			, szYsStkColGp.substring(1,2) );
					jrParam.setField("YD_EQP_GP"			, szYsStkColGp.substring(2,4) );
					jrParam.setField("YS_STK_COL_NO"		, szYsStkColGp.substring(4,6) );
					jrParam.setField("YS_STK_BED_NO"		, szYsStkBedNo );
					jrParam.setField("YS_STK_LYR_NO"		, szYsStkLyrNo );
					jrParam.setField("YS_STK_SEQ_NO"		, szYsStkSeqNo );
					jrParam.setField("YS_STR_LOC"			, szYsStkColGp + szYsStkBedNo + szYsStkLyrNo + szYsStkSeqNo );
					jrParam.setField("SSTL_NO"				, szStlNo );
					
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBndlCommYsStrLoc", logId, methodNm, "BUNDLE공통 야드저장위치 수정");
					
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
	} // end of delPulloutSupMtlMobile
	
	
	/**
	 * 봉강재공야드 저장위치 수정Mobile
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord updStrLocModMobile(JDTORecord recPara) throws DAOException {
		String methodNm = "봉강재공야드 저장위치 수정[GdsYsJspSeEJB.updStrLocModMobile] < ";
		String logId = commUtils.getLogId();
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			
			String szStlNo = null;
			String szYsStkColGp = null;
			String szYsStkBedNo = null;
			String szYsStkLyrNo = null;
			String szYsStkSeqNo = null; 

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

				
			szStlNo 			= commUtils.trim(recPara.getFieldString("SSTL_NO"));
			szYsStkColGp 		= commUtils.trim(recPara.getFieldString("YS_STK_COL_GP"));
			szYsStkBedNo 		= commUtils.trim(recPara.getFieldString("YS_STK_BED_NO"));
			szYsStkLyrNo 		= commUtils.trim(recPara.getFieldString("YS_STK_LYR_NO"));
			szYsStkSeqNo 		= commUtils.trim(recPara.getFieldString("YS_STK_SEQ_NO"));
 
			
			//번들공통 위치정보 수정하기
			//추가, 이동
			jrParam.setField("FNL_REG_PGM"			, "LocModMobile" );
			jrParam.setField("YD_GP"				, "G" );
			jrParam.setField("YD_BAY_GP"			, szYsStkColGp.substring(1,2) );
			jrParam.setField("YD_EQP_GP"			, szYsStkColGp.substring(2,4) );
			jrParam.setField("YS_STK_COL_NO"		, szYsStkColGp.substring(4,6) );
			jrParam.setField("YS_STK_BED_NO"		, szYsStkBedNo );
			jrParam.setField("YS_STK_LYR_NO"		, szYsStkLyrNo );
			jrParam.setField("YS_STK_SEQ_NO"		, szYsStkSeqNo );
			jrParam.setField("YS_STR_LOC"			, szYsStkColGp + szYsStkBedNo + szYsStkLyrNo + szYsStkSeqNo );
			jrParam.setField("SSTL_NO"				, szStlNo );
			
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBndlCommYsStrLoc", logId, methodNm, "BUNDLE공통 야드저장위치 수정");
  
			//야드 저장위치이력 등록 추가 2018.05.15     
			jrParam.setField("YS_STK_SEQ_NO1"   , szYsStkSeqNo   ); //특수강야드적치Seq번호1
			jrParam.setField("SSTL_NO1"    		, szStlNo    		); //특수강재료번호1 
			jrParam.setField("MODIFIER"         , "ModMobile"       ); //수정자  
			jrParam.setField("DEL_YN"			, "N"		);
			
			
			
			commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insStrLocHist", logId, methodNm, "저장위치이력 등록");
			
			
				//L2로 재원정보 전문 전송
			jrParam.setField("YD_INFO_SYNC_CD", "6"); //야드정보동기화코드 6:지정저장품
			
			jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN4L002", jrParam));
			
 
			jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSDSJ001L2", jrParam));
	
  
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
	} // end of updStrLocModMobile
	
	
	/**
	 * 봉강재공야드 저장위치 수정Mobile
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord updStrLocModMobile2(String userid , JDTORecord[] inRecordSet) throws DAOException {
		String methodNm = "봉강재공야드 저장위치 수정2[GdsYsJspSeEJB.updStrLocModMobile2] < ";
		String logId = commUtils.getLogId();
		
		JDTORecord inRecord			= JDTORecordFactory.getInstance().create();
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn = null;
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, userid);

			//대상 레코드 수
			int rowCnt = inRecordSet.length;
			
			String sSTL_NO = null;
			String szYsStkColGp = null;
			String szYsStkBedNo = null;
			String szYsStkLyrNo = null;
			String szYsStkSeqNo = null; 
			
			for(int ii = 0; ii < rowCnt; ii++) {
				inRecord = JDTORecordFactory.getInstance().create();
				
				inRecord = inRecordSet[ii];
				sSTL_NO 			= inRecord.getFieldString("SSTL_NO");
				szYsStkColGp 		= inRecord.getFieldString("YS_STK_COL_GP");
				szYsStkBedNo 		= inRecord.getFieldString("YS_STK_BED_NO");
				szYsStkLyrNo 		= inRecord.getFieldString("YS_STK_LYR_NO");
				szYsStkSeqNo 		= inRecord.getFieldString("YS_STK_SEQ_NO");
				
				//번들공통 위치정보 수정하기
				//추가, 이동
				jrParam.setField("FNL_REG_PGM"			, "LocModMobile" );
				jrParam.setField("YD_GP"				, "G" );
				jrParam.setField("YD_BAY_GP"			, szYsStkColGp.substring(1,2) );
				jrParam.setField("YD_EQP_GP"			, szYsStkColGp.substring(2,4) );
				jrParam.setField("YS_STK_COL_NO"		, szYsStkColGp.substring(4,6) );
				jrParam.setField("YS_STK_BED_NO"		, szYsStkBedNo );
				jrParam.setField("YS_STK_LYR_NO"		, szYsStkLyrNo );
				jrParam.setField("YS_STK_SEQ_NO"		, szYsStkSeqNo );
				jrParam.setField("YS_STR_LOC"			, szYsStkColGp + szYsStkBedNo + szYsStkLyrNo + szYsStkSeqNo );
				jrParam.setField("SSTL_NO"				, sSTL_NO );
				
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBndlCommYsStrLoc", logId, methodNm, "BUNDLE공통 야드저장위치 수정");
				
				//야드 저장위치이력 등록 추가 2018.05.15     
				jrParam.setField("YS_STK_SEQ_NO1"   , szYsStkSeqNo   ); //특수강야드적치Seq번호1
				jrParam.setField("SSTL_NO1"    		, sSTL_NO    		); //특수강재료번호1 
				jrParam.setField("MODIFIER"         , "ModMobile"       ); //수정자  
				jrParam.setField("DEL_YN"			, "N"		);
					
				commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insStrLocHist", logId, methodNm, "저장위치이력 등록");
				
				//L2로 재원정보 전문 전송
				jrParam.setField("YD_INFO_SYNC_CD", "6"); //야드정보동기화코드 6:지정저장품
				
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN4L002", jrParam));
				
	 
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSDSJ001L2", jrParam));
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
	} // end of updStrLocModMobile2
	
	
	
	/**
     * 상차확인서 등록(모바일) / 하차등록(임가공) / 입고등록(임가공) /입고등록(봉강정정):MENU_GP-Z
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord regCarStlChk(String userid , JDTORecord[] inRecordSet) throws DAOException {
		String methodNm = "상차확인서 등록(모바일) - [GdsYsJspSeEJB.regCarStlChk] < ";
		String logId = commUtils.getLogId();
		
		JDTORecord inRecord			= JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn = null;
			JDTORecord jrTemp = null;
			

			JDTORecordSet jsTemp = null;
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, userid);

			//대상 레코드 수
			int rowCnt = inRecordSet.length;
			
			String sSTL_NO = null;
			String sLD_OCCR_REG = null;
			String sMENU_GP = null;
			
			
			//상/하차시, 글로비스 QR코드 리딩에 따른 추가 변수 선언
			String sCAR_NO = null;   //차량번호
			String sTRNC_CD = null;  //운송사코드
			String sDRIVER_NAME = null;  //운전자명
			String sDRIVER_HANDPHONE_NO = null;  //운전자번호
			String sYARD_GP_TO = null; // 하차지코드
			String sYD_TO_STR_LOC = null; //야드 하차지번
			String sYARD_GP_FROM = null; //야드 상차지번
			String sTRNC_CARASGN_NO =null; //배차번호
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				inRecord = JDTORecordFactory.getInstance().create();
				
				inRecord = inRecordSet[ii];
				sSTL_NO = inRecord.getFieldString("STL_NO");
				sLD_OCCR_REG = inRecord.getFieldString("LD_OCCR_REG");
				sMENU_GP = inRecord.getFieldString("MENU_GP");
			    
				if("G".equals(sMENU_GP) && "S".equals(sSTL_NO.substring(0,1))) {
					jrParam.setField("LD_PROG_STAT", "9");
				} else if("G".equals(sMENU_GP)) {
					jrParam.setField("LD_PROG_STAT", "1");
				} else if("L".equals(sMENU_GP)) {
					jrParam.setField("LD_PROG_STAT", "1");
				} else if("L2".equals(sMENU_GP)) {   //L2: 신규 하차 기능(글로비스QR코드 리딩, 하차지 선택 기능 포함)
					jrParam.setField("LD_PROG_STAT", "1");
				}else if("U".equals(sMENU_GP)) {
					jrParam.setField("LD_PROG_STAT", "9");
                }else if("Z".equals(sMENU_GP)) {  //봉강정정 입고등록
                    jrParam.setField("LD_PROG_STAT", "#");  //봉강정정 입고 
				}
				
				if("L2".equals(sMENU_GP)){
					sCAR_NO= inRecord.getFieldString("CAR_NO");
					sTRNC_CD= inRecord.getFieldString("TRANS_COM_CODE");
					sDRIVER_NAME= inRecord.getFieldString("DRIVER_NAME");
					sDRIVER_HANDPHONE_NO= inRecord.getFieldString("DRIVER_HANDPHONE_NO");
					sYARD_GP_TO=inRecord.getFieldString("YARD_GP_TO");
					sYD_TO_STR_LOC=inRecord.getFieldString("YD_TO_STR_LOC");
				}
				 if("Z".equals(sMENU_GP)){
						sCAR_NO= inRecord.getFieldString("CAR_NO");
						sTRNC_CD= inRecord.getFieldString("TRANS_COM_CODE");
						sDRIVER_NAME= inRecord.getFieldString("DRIVER_NAME");
						sDRIVER_HANDPHONE_NO= inRecord.getFieldString("DRIVER_HANDPHONE_NO");
						sYARD_GP_FROM = inRecord.getFieldString("YARD_GP_FROM");
						sYARD_GP_TO = inRecord.getFieldString("YARD_GP_TO");
						sYD_TO_STR_LOC =  inRecord.getFieldString("YD_TO_STR_LOC");
						sTRNC_CARASGN_NO =  inRecord.getFieldString("TRNC_CARASGN_NO");
						
						
				 }
				
				jrParam.setField("MATL_NO", sSTL_NO);
				jrParam.setField("LD_OCCR_REG", sLD_OCCR_REG);
				
				if("G".equals(sMENU_GP)) {
					commDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insCarStlChk", logId, methodNm, "특수강봉강 상차확인서 등록 완료");
				} else if("L2".equals(sMENU_GP)){  //QR, 하차정보 포함해서 등록
					jrParam.setField("CAR_NO", sCAR_NO);
					jrParam.setField("TRNC_CD", sTRNC_CD);
					jrParam.setField("DRIVER_NAME", sDRIVER_NAME);
					jrParam.setField("DRIVER_HANDPHONE_NO", sDRIVER_HANDPHONE_NO);
					jrParam.setField("YARD_GP_TO", sYARD_GP_TO);
					 jrParam.setField("YARD_GP_FROM", "");		//상차지코드
					 jrParam.setField("TRNC_CARASGN_NO", "");		//배차번호
					jrParam.setField("YD_TO_STR_LOC", sYD_TO_STR_LOC);
					
					commDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insCarStlChk3", logId, methodNm, "특수강봉강 하차/입고실적 등록 완료");
                } else if("Z".equals(sMENU_GP)){  //봉강정정 입고등록
                    jrParam.setField("CAR_NO", sCAR_NO);			//차량번호
                    jrParam.setField("TRNC_CD", sTRNC_CD);		//운송사코드
                    jrParam.setField("DRIVER_NAME", sDRIVER_NAME);		//운전자명
                    jrParam.setField("DRIVER_HANDPHONE_NO", sDRIVER_HANDPHONE_NO);		//운전자번호
                    jrParam.setField("YARD_GP_TO", sYARD_GP_TO);		//하차지코드
                    jrParam.setField("YARD_GP_FROM", sYARD_GP_FROM);		//상차지코드
                    jrParam.setField("TRNC_CARASGN_NO", sTRNC_CARASGN_NO);		//배차번호
                    jrParam.setField("YD_TO_STR_LOC", "GGB10101011"); 
                    
                    commDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insCarStlChk3", logId, methodNm, "특수강봉강 하차/입고실적 등록 완료");
            
    				jrParam.setField("FNL_REG_PGM"			, "LocModMobile" );
    				jrParam.setField("YD_GP"				, "G" );
    				jrParam.setField("YD_BAY_GP"			, "G" );
    				jrParam.setField("YD_EQP_GP"			, "B1" );
    				jrParam.setField("YS_STK_COL_NO"		, "01" );
    				jrParam.setField("YS_STK_BED_NO"		, "01" );
    				jrParam.setField("YS_STK_LYR_NO"		, "01" );
    				jrParam.setField("YS_STK_SEQ_NO"		, "1");
    				jrParam.setField("YS_STR_LOC"			, "GGB10101011" );
    				jrParam.setField("SSTL_NO"				, sSTL_NO );
    				
                    commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBndlCommYsStrLoc", logId, methodNm, "BUNDLE공통 야드저장위치 수정");
        			
        			
				}else {
					commDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insCarStlChk2", logId, methodNm, "특수강봉강 하차/입고실적 등록 완료");
				}
				
			}       
			
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
	} // end of regCarStlChk
	
	/**
	 * 대형 → 정정 오프라인 이송 소재 하차 등록 시스템 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord regOffMVStlChk(String userid , JDTORecord inRecord) throws DAOException {
		String methodNm = "대형 → 정정 오프라인 이송 소재 하차 등록 시스템 (모바일) - [GdsYsJspSeEJB.regOffMVStlChk] < ";
		String logId = commUtils.getLogId();
		

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, userid);


			String sSTL_NO = null;
			String sMODIFIER = null;
			String sMENU_GP = null;
			

			sSTL_NO = inRecord.getFieldString("STL_NO");
			sMODIFIER = inRecord.getFieldString("MODIFIER");
			sMENU_GP = inRecord.getFieldString("MENU_GP");
		    
			jrParam.setField("STL_NO", sSTL_NO);
			jrParam.setField("MODIFIER", sMODIFIER);
			
			commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updOffMVStlChk", logId, methodNm, "대형 → 정정 오프라인 이송 소재 하차 등록 완료");
   
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
	} // end of regCarStlChk
	
	/**
	 * 정정 → 대형 회송 등록 시스템 
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord regRetHtStlChk(String userid , JDTORecord inRecord) throws DAOException {
		String methodNm = "정정 → 대형 회송 등록 시스템 (모바일) - [GdsYsJspSeEJB.regRetHtStlChk] < ";
		String logId = commUtils.getLogId();
		

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, userid);


			String sSTL_NO = null;
			String sMODIFIER = null;
			String sMENU_GP = null;
			

			sSTL_NO = inRecord.getFieldString("STL_NO");
			sMODIFIER = inRecord.getFieldString("MODIFIER");
			sMENU_GP = inRecord.getFieldString("MENU_GP");
		    
			jrParam.setField("STL_NO", sSTL_NO);
			jrParam.setField("MODIFIER", sMODIFIER);
			
			commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updRetHtStlChk", logId, methodNm, "정정 → 대형 회송 등록 완료");
   
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
	} // end of regRetHtStlChk
	
	/**
	 * 철분말 상차등록(임가공)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord regCarLdStlChk(String userid , JDTORecord[] inRecordSet) throws DAOException {
		String methodNm = "철분말 상차등록(모바일) - [GdsYsJspSeEJB.regCarLdStlChk] < ";
		String logId = commUtils.getLogId();
		
		JDTORecord inRecord			= JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			
			JDTORecordSet jsTemp = null;
			JDTORecord jrTemp = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, userid);

			//대상 레코드 수
			int rowCnt = inRecordSet.length;
			
			String sSTL_NO = null;
			String sLD_OCCR_REG = null;
			//하차시, 글로비스 QR코드 리딩에 따른 추가 변수 선언
			String sCAR_NO 				= null;   //차량번호
			String sTRNC_CD 			= null;  //운송사코드
			String sDRIVER_NAME 		= null;  //운전자명
			String sDRIVER_HANDPHONE_NO = null;  //운전자번호
			String sYARD_GP_FROM 		= null; // 상차지코드
			String sYARD_GP_TO 			= null; // 하차지코드
			String sYD_TO_STR_LOC 		= null; //야드 하차지번
			String sTRNC_CARASGN_NO = null; //배차번호
			
			//PIDEV
//			String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "K", "*");
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				inRecord = JDTORecordFactory.getInstance().create();
				
				inRecord 				= inRecordSet[ii];
				sSTL_NO  				= inRecord.getFieldString("STL_NO");
				sLD_OCCR_REG 			= inRecord.getFieldString("LD_OCCR_REG");
				sCAR_NO					= inRecord.getFieldString("CAR_NO");
				sTRNC_CD				= inRecord.getFieldString("TRANS_COM_CODE");
				sDRIVER_NAME			= inRecord.getFieldString("DRIVER_NAME");
				sDRIVER_HANDPHONE_NO	= inRecord.getFieldString("DRIVER_HANDPHONE_NO");
				sYARD_GP_FROM			= inRecord.getFieldString("YARD_GP_FROM");
				sYARD_GP_TO				= inRecord.getFieldString("YARD_GP_TO");
				sYD_TO_STR_LOC			= inRecord.getFieldString("YD_TO_STR_LOC");
				sTRNC_CARASGN_NO = inRecord.getFieldString("TRNC_CARASGN_NO");
				
				commUtils.printLog(logId, "철분말상차 재료 ["+ sSTL_NO + "] to위치 ["+sYD_TO_STR_LOC+"]", "SL");
				
				if (sYD_TO_STR_LOC.length() <11) {
					commUtils.printLog(logId, "철분말상차 재료 ["+ sSTL_NO + "] from위치 ["+sYD_TO_STR_LOC+"] 길이 11 미만!", "SL");
					throw new Exception("철분말상차 재료 ["+ sSTL_NO + "] from위치 ["+sYD_TO_STR_LOC+"] 길이 11 미만!");
				}
				
				jrParam.setField("MATL_NO"				, sSTL_NO);
				jrParam.setField("LD_OCCR_REG"			, sLD_OCCR_REG);
				jrParam.setField("LD_PROG_STAT"			, "#");//상차 # 하차 *
				jrParam.setField("CAR_NO"				, sCAR_NO);
				jrParam.setField("TRNC_CD"				, sTRNC_CD);
				jrParam.setField("DRIVER_NAME"			, sDRIVER_NAME);
				jrParam.setField("DRIVER_HANDPHONE_NO"	, sDRIVER_HANDPHONE_NO);
				jrParam.setField("YARD_GP_FROM"			, sYARD_GP_FROM);
				jrParam.setField("YARD_GP_TO"			, sYARD_GP_TO);
				jrParam.setField("YD_TO_STR_LOC"		, sYD_TO_STR_LOC);
				jrParam.setField("TRNC_CARASGN_NO"		, sTRNC_CARASGN_NO);
				
				
				//TB_YS_C_CARLDWR 로 이송완료실적 INSERT
				commDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insCarUdStlChk_m", logId, methodNm, "철분말 상차실적 등록");
			
				
				if(sSTL_NO.charAt(0) == 'A')
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBilletCommCarUd", logId, methodNm, "철분말 상차 공통저장위치 수정");
				else if(sSTL_NO.charAt(0) == 'S' || sSTL_NO.charAt(0) == 'L' )
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBundleCommCarUd", logId, methodNm, "철분말 상차 공통저장위치 수정");
				
				if(sYARD_GP_TO.equals("IM0A")) { //철분말 > 인천, 트렉슈용 빌렛
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStklyrClear", logId, methodNm, "철분말 STKLYR 초기화");
					
				} else if (sYARD_GP_TO.equals("DK0C")) { //소형출측 > 진양
					
					//L2 저장위치 변경이력 전송
					jrParam.setField("YD_GP"			, sYD_TO_STR_LOC.substring(0,1));
					jrParam.setField("YD_BAY_GP"		, sYD_TO_STR_LOC.substring(1,2)		);
					jrParam.setField("YD_EQP_GP"		, sYD_TO_STR_LOC.substring(2,4)		); //야드설비구분
					jrParam.setField("YS_STK_COL_NO"    , sYD_TO_STR_LOC.substring(4,6)		); //특수강야드적치열번호
					jrParam.setField("YS_STK_BED_NO"    , sYD_TO_STR_LOC.substring(6,8)    	); //특수강야드적치Bed번호
					jrParam.setField("YS_STK_LYR_NO"    , sYD_TO_STR_LOC.substring(8,10)   	); //특수강야드적치단번호
					jrParam.setField("YS_STK_SEQ_NO1"   , sYD_TO_STR_LOC.substring(10,11)  	); //특수강야드적치Seq번호1
					jrParam.setField("SSTL_NO1"    		, sSTL_NO   ); //특수강재료번호1
					jrParam.setField("MODIFIER"			, userid    ); //수정자
					jrParam.setField("DEL_YN"    		, "N"    	); 
					
					commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insStrLocHist", logId, methodNm, "L2야드 저장위치 변경이력 등록");

					//L2저장위치 변경요청 전문 전송
					jrParam.setField("SSTL_NO"    		, sSTL_NO    		); //특수강재료번호
					jrParam.setField("YS_STR_LOC"       , sYD_TO_STR_LOC	); //저장위치
					jrParam.setField("YD_INFO_SYNC_CD", "6_3");  //6_3으로 진양특수강용 L2 저장위치 전송 전문 쿼리 생성
					//전송할 전문에 추가
					jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YSN4L002", jrParam));  //L2에 저장위치 철분말로 변경요청(전문아이디/형식은 L2와 협의 필요)

					//L2 저장위치? 저장품? 제원 전송해야하나? //TBD
					
					//생산통제 전문 전송 //TBD
					//jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSUJ00X_LD", jrParam)); 
				}
				
			}       
			
			//대상 상차LOTID 조회(상차 제품중 한 제품으로 가장 최근 LOTID(방금 등록한) 조회)
			jsTemp=commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCarldIdByMatlNo", logId, methodNm, "제품번호 작업대상여부 조회");
			jrTemp=jsTemp.getRecord(0);
			String sCARLD_ID= commUtils.trim(jrTemp.getFieldString("CARLD_ID"));
			
			
			jrParam.setField("CARLD_ID"    		, sCARLD_ID    		); //상차LOTID 
			jrParam.setField("TRN_FRTOMOVE_GP"    		, "21"    		); //운송이송구분
			jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1134", jrParam));


			jrRtn.setField("RETN_CD", YsConstant.RETN_CD_SUCCESS);
			jrRtn.setField("RETN_MSG", "정상처리 되었습니다!!");
				
			commUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of regCarStlChk
	
	/**
	 * 진양특수강 하차등록(철분말->진양특수강)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord regCarUdStlChk(String userid , JDTORecord[] inRecordSet) throws DAOException {
		String methodNm = "진양특수강 하차등록(모바일) - [GdsYsJspSeEJB.regCarUdStlChk] < ";
		String logId = commUtils.getLogId();
		
		JDTORecord inRecord			= JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, userid);

			//대상 레코드 수
			int rowCnt = inRecordSet.length;
			
			String sSTL_NO = null;
			String sLD_OCCR_REG = null;
			//하차시, 글로비스 QR코드 리딩에 따른 추가 변수 선언
			String sCAR_NO 				= null;   //차량번호
			String sTRNC_CD 			= null;  //운송사코드
			String sDRIVER_NAME 		= null;  //운전자명
			String sDRIVER_HANDPHONE_NO = null;  //운전자번호
			String sYARD_GP_FROM 		= null; // 상차지코드
			String sYARD_GP_TO 			= null; // 하차지코드
			String sYD_TO_STR_LOC 		= null; //야드 하차지번
			
			//PIDEV
//			String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "K", "*");
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				inRecord = JDTORecordFactory.getInstance().create();
				
				inRecord 				= inRecordSet[ii];
				sSTL_NO  				= inRecord.getFieldString("STL_NO");
				sLD_OCCR_REG 			= inRecord.getFieldString("LD_OCCR_REG");
				sCAR_NO					= inRecord.getFieldString("CAR_NO");
				sTRNC_CD				= inRecord.getFieldString("TRANS_COM_CODE");
				sDRIVER_NAME			= inRecord.getFieldString("DRIVER_NAME");
				sDRIVER_HANDPHONE_NO	= inRecord.getFieldString("DRIVER_HANDPHONE_NO");
				sYARD_GP_FROM			= inRecord.getFieldString("YARD_GP_FROM");
				sYARD_GP_TO				= inRecord.getFieldString("YARD_GP_TO");
				sYD_TO_STR_LOC			= inRecord.getFieldString("YD_TO_STR_LOC");
								
				jrParam.setField("MATL_NO"				, sSTL_NO);
				jrParam.setField("LD_OCCR_REG"			, sLD_OCCR_REG);
				jrParam.setField("LD_PROG_STAT"			, "*");
				jrParam.setField("CAR_NO"				, sCAR_NO);
				jrParam.setField("TRNC_CD"				, sTRNC_CD);
				jrParam.setField("DRIVER_NAME"			, sDRIVER_NAME);
				jrParam.setField("DRIVER_HANDPHONE_NO"	, sDRIVER_HANDPHONE_NO);
				jrParam.setField("YARD_GP_FROM"			, sYARD_GP_FROM);
				jrParam.setField("YARD_GP_TO"			, sYARD_GP_TO);
				jrParam.setField("YD_TO_STR_LOC"		, sYD_TO_STR_LOC);
				
				//TB_YS_C_CARLDWR 로 이송완료실적 INSERT
				commDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insCarUdStlChk_m", logId, methodNm, "진양특수강 하차실적 등록");
				
				//
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBilletCommCarUd", logId, methodNm, "진양특수강 하차 공통저장위치 수정");
				
				
				/*진양하차시 이송실적 전송 기능 제거(물류시스템 포함 후, 물류에서 이송실적 전송함) 2023.03.29	
					if(sYARD_GP_FROM !=sYARD_GP_TO)  //FROM위치와, TO위치가 다를경우만 이송실적 전송해준다. 빌렛 정정->철분말 이송 후 하차 등록시, FROM/TO위치가 같음.(빌렛 정정 상차시 L2가 위치를 철분말로 변경함.) 
						                            //이후 철분말 하차 등록시, FROM위치와 TO위치가 같게됨. 
					{
						jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSPDJ001_BILLET", jrParam));  //PP로 임가공사 이송실적 보내줌.임가공사만!!!
					}
				*/
				
				//생산통제 전문 전송 //TBD
				//jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSUJ00X_LD", jrParam)); 
				
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
	} // end of regCarStlChk
	
	/**
	 * 진양특수강 하차등록(빌렛소재->철분말)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord regCarUdStlChk2(String userid , JDTORecord[] inRecordSet) throws DAOException {
		String methodNm = "철분말 하차등록(모바일) - [GdsYsJspSeEJB.regCarUdStlChk2] < ";
		String logId = commUtils.getLogId();
		
		JDTORecord inRecord			= JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, userid);

			//대상 레코드 수
			int rowCnt = inRecordSet.length;
			
			String sSTL_NO = null;
			String sLD_OCCR_REG = null;
			//하차시, 글로비스 QR코드 리딩에 따른 추가 변수 선언
			String sCAR_NO 				= null;   //차량번호
			String sTRNC_CD 			= null;  //운송사코드
			String sDRIVER_NAME 		= null;  //운전자명
			String sDRIVER_HANDPHONE_NO = null;  //운전자번호
			String sYARD_GP_FROM 		= null; // 상차지코드
			String sYARD_GP_TO 			= null; // 하차지코드
			String sYD_TO_STR_LOC 		= null; //야드 하차지번
			String sYD_FROM_STR_LOC 	= null; //야드 이전지번
			
			//PIDEV
//			String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "K", "*");
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				inRecord = JDTORecordFactory.getInstance().create();
				
				inRecord 				= inRecordSet[ii];
				sSTL_NO  				= inRecord.getFieldString("STL_NO");
				sLD_OCCR_REG 			= inRecord.getFieldString("LD_OCCR_REG");
				sCAR_NO					= inRecord.getFieldString("CAR_NO");
				sTRNC_CD				= inRecord.getFieldString("TRANS_COM_CODE");
				sDRIVER_NAME			= inRecord.getFieldString("DRIVER_NAME");
				sDRIVER_HANDPHONE_NO	= inRecord.getFieldString("DRIVER_HANDPHONE_NO");
				sYARD_GP_FROM			= inRecord.getFieldString("YARD_GP_FROM");
				sYARD_GP_TO				= inRecord.getFieldString("YARD_GP_TO");
				sYD_TO_STR_LOC			= inRecord.getFieldString("YD_TO_STR_LOC");
				sYD_FROM_STR_LOC		= inRecord.getFieldString("YD_FROM_STR_LOC");
				
				commUtils.printLog(logId, "철분말 하차 재료 ["+ sSTL_NO + "] from위치 ["+sYD_FROM_STR_LOC+"] to위치 ["+sYD_TO_STR_LOC+"]", "SL");
				
				if (sYD_TO_STR_LOC.length() <11) {
					commUtils.printLog(logId, "철분말 하차 재료 ["+ sSTL_NO + "] from위치 ["+sYD_TO_STR_LOC+"] 길이 11 미만!", "SL");
					throw new Exception("철분말 하차 재료 ["+ sSTL_NO + "] from위치 ["+sYD_TO_STR_LOC+"] 길이 11 미만!");
				}

				jrParam.setField("MATL_NO"				, sSTL_NO);
				jrParam.setField("LD_OCCR_REG"			, sLD_OCCR_REG);
				jrParam.setField("LD_PROG_STAT"			, "*");
				jrParam.setField("CAR_NO"				, sCAR_NO);
				jrParam.setField("TRNC_CD"				, sTRNC_CD);
				jrParam.setField("DRIVER_NAME"			, sDRIVER_NAME);
				jrParam.setField("DRIVER_HANDPHONE_NO"	, sDRIVER_HANDPHONE_NO);
				jrParam.setField("YARD_GP_FROM"			, sYARD_GP_FROM);
				jrParam.setField("YARD_GP_TO"			, sYARD_GP_TO);
				jrParam.setField("YD_TO_STR_LOC"		, sYD_TO_STR_LOC);
				jrParam.setField("YD_FROM_STR_LOC"		, sYD_FROM_STR_LOC);
				
				//TB_YS_C_CARLDWR 로 이송완료실적 INSERT
				commDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insCarUdStlChk_m", logId, methodNm, "진양특수강 하차실적 등록");
				
				/*
				 * 공통 저장 위치 SSTL_NO
				 * A로 시작하는경우 -> 빌렛공통 수정
				 * S 또는 L로 시작하는경우 -> 번들공통 수정 
				 */
		
				if(sSTL_NO.charAt(0) == 'A')
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBilletCommCarUd", logId, methodNm, "진양특수강 하차 공통저장위치 수정");
				else if(sSTL_NO.charAt(0) == 'S' || sSTL_NO.charAt(0) == 'L' )
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBundleCommCarUd", logId, methodNm, "진양특수강 하차 공통저장위치 수정"); 
					  
					
					

				//L2 저장위치 변경이력 전송
				jrParam.setField("YD_GP"			, sYD_TO_STR_LOC.substring(0,1));
				jrParam.setField("YD_BAY_GP"		, sYD_TO_STR_LOC.substring(1,2)		);
				jrParam.setField("YD_EQP_GP"		, sYD_TO_STR_LOC.substring(2,4)		); //야드설비구분
				jrParam.setField("YS_STK_COL_NO"    , sYD_TO_STR_LOC.substring(4,6)		); //특수강야드적치열번호
				jrParam.setField("YS_STK_BED_NO"    , sYD_TO_STR_LOC.substring(6,8)    	); //특수강야드적치Bed번호
				jrParam.setField("YS_STK_LYR_NO"    , sYD_TO_STR_LOC.substring(8,10)   	); //특수강야드적치단번호
				jrParam.setField("YS_STK_SEQ_NO1"   , sYD_TO_STR_LOC.substring(10,11)  	); //특수강야드적치Seq번호1
				jrParam.setField("SSTL_NO1"    		, sSTL_NO   ); //특수강재료번호1
				jrParam.setField("MODIFIER"			, userid    ); //수정자
				jrParam.setField("DEL_YN"    		, "N"    	); 
				
				commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insStrLocHist", logId, methodNm, "L2야드 저장위치 변경이력 등록");
				
				
				//L2저장위치 변경요청 전문 전송
				jrParam.setField("SSTL_NO"    		, sSTL_NO    		); //특수강재료번호
				jrParam.setField("YS_STR_LOC"         , sYD_TO_STR_LOC	); //저장위치
				jrParam.setField("YD_INFO_SYNC_CD", "6_3");  //6_3으로 진양특수강용 L2 저장위치 전송 전문 쿼리 생성
				//전송할 전문에 추가
				jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YSN4L002", jrParam));  //L2에 저장위치 철분말로 변경요청(전문아이디/형식은 L2와 협의 필요)
				
				commUtils.printLog(logId, "철분말 하차 재료 ["+ sSTL_NO + "] 진도코드 변경 체크", "SL");
				 
		
				//진도코드 D, 여재구분:1일경우만 진행관리로 진도 변경 전문 전송	
				if(sSTL_NO.charAt(0) == 'A')
					jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL3("YSPBJ002ByStlNo", jrParam));  
				else if(sSTL_NO.charAt(0) == 'S' || sSTL_NO.charAt(0) == 'L' )
					jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL3("YSPBJ004ByStlNo", jrParam));  
				
				
				//CASE WHEN CURR_PROG_CD='D' AND ORD_YEOJAE_GP='1' THEN 'B' ELSE CURR_PROG_CD
				//진도코드 D, 여재구분:1일경우만 B로 진도코드 수정
				
				//진양임가공 이송 없으므로 진도 변경 로직 삭제 
				
				//if(sSTL_NO.charAt(0) == 'A')
				//	commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBilletCommProgCd", logId, methodNm, "진양특수강 철분말하차 공통 진도코드 수정");				
				//else if(sSTL_NO.charAt(0) == 'S' || sSTL_NO.charAt(0) == 'L' )
				//	commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBundleCommProgCd", logId, methodNm, "진양특수강 철분말하차 공통 진도코드 수정"); 
					  
				
				
				
				
				//L2위치 변경이력 추가 (일반적케이스면 L2->L3로 변경이력 정보 보내줘야하는데, L3모바일에서 L2야드 바꾸는 구조라, L3가 직접 바꿈.)
				 
				if(sSTL_NO.charAt(0) == 'A')
				{
				    //from 위치 to위치 모두 한글자 이상이고, 
				    //첫글자가 다를경우(야드가 다른경우(C,_ vs G)) 이송실적 전송 ->철분말 지번이 S210->G342로 변경됐으므로, 첫글자 상관없이 모두 전송
					if(sYD_FROM_STR_LOC.length() >1 && sYD_TO_STR_LOC.length() >1){
					   //&& sYD_FROM_STR_LOC.charAt(0) !=sYD_TO_STR_LOC.charAt(0)){
						if(sYD_FROM_STR_LOC.charAt(0)=='C' ||sYD_FROM_STR_LOC.charAt(0)=='_'){  //소형에서 온 빌렛이면
							jrParam.setField("YARD_GP_FROM"			, "S220");
						}
						if(sYD_FROM_STR_LOC.charAt(0)=='G'){  //대형에서 온 빌렛이면
							jrParam.setField("YARD_GP_FROM"			, "S210");
						}
						jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSPDJ001_BILLET", jrParam));  //PP로 임가공사 이송실적 보내줌.임가공사만!!!
					} 	
				}
				else if(sSTL_NO.charAt(0) == 'S' || sSTL_NO.charAt(0) == 'L' ){
					jrParam.setField("YARD_GP_FROM"			, "S220");
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSPDJ001_BUNDLE", jrParam));  //PP로 임가공사 이송실적 보내줌.임가공사만!!!
				}
	
					
				
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
	} // end of regCarStlChk2	
	
	/**
	 * 진양특수강 입고등록(임가공)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord regRecvStlChk(String userid , JDTORecord[] inRecordSet) throws DAOException {
		String methodNm = "진양특수강 입고등록(모바일) - [GdsYsJspSeEJB.regRecvStlChk] < ";
		String logId = commUtils.getLogId();
		
		JDTORecord inRecord			= JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, userid);

			//대상 레코드 수
			int rowCnt = inRecordSet.length;
			
			String sSTL_NO = null;
			String sLD_OCCR_REG = null;
			String sYD_TO_STR_LOC 		= null; //야드 하차지번
			
			//PIDEV
//			String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "K", "*");
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				inRecord = JDTORecordFactory.getInstance().create();
				
				inRecord 				= inRecordSet[ii];
				sSTL_NO  				= inRecord.getFieldString("STL_NO");
				sLD_OCCR_REG 			= inRecord.getFieldString("LD_OCCR_REG");
				sYD_TO_STR_LOC			= inRecord.getFieldString("YD_TO_STR_LOC");
								
				jrParam.setField("MATL_NO"				, sSTL_NO);
				jrParam.setField("LD_OCCR_REG"			, sLD_OCCR_REG);
				jrParam.setField("LD_PROG_STAT"			, "*");
				jrParam.setField("YD_TO_STR_LOC"		, sYD_TO_STR_LOC);
				
				//TB_YS_C_CARLDWR 로 이송완료실적 INSERT
				commDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insCarUdStlChk_m", logId, methodNm, "진양특수강 입고실적 등록");
				
				//
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBundleCommRecvStl", logId, methodNm, "진양특수강 입고 공통저장위치 수정");
				
//				if("Y".equals(sApplyYnPI)) {
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1014_I", jrParam));
//				}
				
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
	} // end of regCarStlChk
	/**
	 * 특수강 진양 사외임가공사 하차완료실적 처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updInlnOutStlInfo_JY(GridData gdReq) throws DAOException {
		String methodNm = "진양 사외임가공사 하차완료실적 처리[GdsYsJspSeEJB.updInlnOutStlInfo_JY] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			String msgId = "";

			//Return Value
			JDTORecord jrRtn = null;
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			
			String ydUserId	   	    = gdReq.getParam("userid");        // 화면에서 SET
			String sFM_YS_STR_LOC	= gdReq.getParam("FM_YS_STR_LOC"); // 화면에서 SET
			String sSTL_NO 	        = "";
				
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				sSTL_NO =  commUtils.getValue(gdReq, "MATL_NO", ii);
				jrParam.setField("MATL_NO"	, sSTL_NO); 
				jrParam.setField("LD_PROG_STAT"			, "*");
				jrParam.setField("LD_OCCR_REG"			, ydUserId);
//				jrParam.setField("CAR_NO"				, sCAR_NO);
//				jrParam.setField("TRNC_CD"				, sTRNC_CD);
//				jrParam.setField("DRIVER_NAME"			, sDRIVER_NAME);
//				jrParam.setField("DRIVER_HANDPHONE_NO"	, sDRIVER_HANDPHONE_NO);
//				jrParam.setField("YARD_GP_FROM"			, sYARD_GP_FROM);
//				jrParam.setField("YARD_GP_TO"			, sYARD_GP_TO);
				jrParam.setField("YD_TO_STR_LOC"		, sFM_YS_STR_LOC);  

				String sCARLD_ID  = commUtils.getDateTime14();	
				jrParam.setField("CARLD_ID"		        , sCARLD_ID);  
				//TB_YS_C_CARLDWR 로 이송완료실적 INSERT
				/** com.inisteel.cim.ys.gds.dao.GdsYsDAO.insCarUdStlChk_JY 진양특수강 하차실적 등록(모바일) 

				INSERT INTO USRYSA.TB_YS_C_CARLDWR
				( CARLD_ID
				 ,MATL_NO
				 ,LD_PROG_STAT
				 ,LD_OCCR_DT
				 ,LD_OCCR_REG
				 ,REGISTER
				 ,REG_DDTT
				 ,MODIFIER
				 ,MOD_DDTT
				 ,CAR_NO
				 ,TRNC_CD
				 ,DRIVER_NAME
				 ,DRIVER_HANDPHONE_NO
				 ,YARD_GP_FROM
				 ,YARD_GP_TO
				 ,YS_STR_LOC
				)
				VALUES
				(
				:V_CARLD_ID,
				TRIM(:V_MATL_NO),
				:V_LD_PROG_STAT,
				SYSDATE,
				:V_LD_OCCR_REG,
				:V_LD_OCCR_REG,
				SYSDATE,
				:V_LD_OCCR_REG,
				SYSDATE,

				:V_CAR_NO,
				:V_TRNC_CD,
				:V_DRIVER_NAME,
				:V_DRIVER_HANDPHONE_NO,
				:V_YARD_GP_FROM,
				:V_YARD_GP_TO,
				:V_YD_TO_STR_LOC
				)
				*/
				commDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insCarUdStlChk_JY", logId, methodNm, "진양특수강 하차실적 등록");
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.updBilletCommCarUd2 
					MERGE INTO USRPBA.TB_PB_BILLETCOMM BC USING (
					    SELECT BLT_NO
					          ,:V_YD_TO_STR_LOC AS YS_STR_LOC_NEW
					          ,YS_STR_LOC
					          ,YS_STR_LOC_HIS1
					      FROM USRPBA.TB_PB_BILLETCOMM
					     WHERE 1=1
					       AND BLT_NO = :V_MATL_NO
					) DD ON (DD.BLT_NO = BC.BLT_NO)
					WHEN MATCHED THEN
					UPDATE 
					   SET BC.YD_GP            = SUBSTR(DD.YS_STR_LOC_NEW,1,1)
					      ,BC.YD_BAY_GP        = SUBSTR(DD.YS_STR_LOC_NEW,2,1)
					      ,BC.YD_EQP_GP        = SUBSTR(DD.YS_STR_LOC_NEW,3,2)
					      ,BC.YS_STK_COL_NO    = SUBSTR(DD.YS_STR_LOC_NEW,5,2)
					      ,BC.YS_STK_BED_NO    = SUBSTR(DD.YS_STR_LOC_NEW,7,2)
					      ,BC.YS_STK_LYR_NO    = SUBSTR(DD.YS_STR_LOC_NEW,9,2)
					      ,BC.YS_STK_SEQ_NO    = SUBSTR(DD.YS_STR_LOC_NEW,11,1)
					      ,BC.YS_STR_LOC       = DD.YS_STR_LOC_NEW
					      ,BC.YS_STR_LOC_HIS1  = BC.YS_STR_LOC
					      ,BC.YS_STR_LOC_HIS2  = BC.YS_STR_LOC_HIS1
					      ,BC.BEFOBEFO_PROG_CD_REG_PGM=BC.BEFO_PROG_CD_REG_PGM
					      ,BC.BEFOBEFO_PROG_REG_DDTT=BC.BEFO_PROG_REG_DDTT
					      ,BC.BEFOBEFO_PROG_CD=BC.BEFO_PROG_CD
					      ,BC.BEFO_PROG_CD_REG_PGM=BC.CURR_PROG_CD_REG_PGM
					      ,BC.BEFO_PROG_REG_DDTT=BC.CURR_PROG_REG_DDTT
					      ,BC.BEFO_PROG_CD=BC.CURR_PROG_CD
					      ,BC.CURR_PROG_CD_REG_PGM='M10YDLMJ1125'
					      ,BC.CURR_PROG_REG_DDTT=SYSDATE
					      ,BC.CURR_PROG_CD='C' 
				*/
				if(sSTL_NO.charAt(0) == 'A')
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBilletCommCarUd2", logId, methodNm, "진양특수강 하차 빌렛 공통저장위치 수정");
				else if(sSTL_NO.charAt(0) == 'S' || sSTL_NO.charAt(0) == 'L' )
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBundleCommCarUd2", logId, methodNm, "진양특수강 하차 번들 공통저장위치 수정");
				
				
				//진행관리로 진도 변경정보 전송 추가 필요한지 체크	 
				jrParam.setField("SSTL_NO"	, sSTL_NO);
				if(sSTL_NO.charAt(0) == 'A')
				jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL3("YSPBJ002ByStlNo", jrParam));
				else if(sSTL_NO.charAt(0) == 'S' || sSTL_NO.charAt(0) == 'L' )
				jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL3("YSPBJ004ByStlNo", jrParam));
				
				//L2 저장위치 변경이력 전송
				jrParam.setField("YD_GP"			, "G");
				jrParam.setField("YD_BAY_GP"		, "J");
				jrParam.setField("YD_EQP_GP"		, "M1"	     ); //야드설비구분
				jrParam.setField("YS_STK_COL_NO"    , "01"	     ); //특수강야드적치열번호
				jrParam.setField("YS_STK_BED_NO"    , "01"       ); //특수강야드적치Bed번호
				jrParam.setField("YS_STK_LYR_NO"    , "01"       ); //특수강야드적치단번호
				jrParam.setField("YS_STK_SEQ_NO1"   , "1"        ); //특수강야드적치Seq번호1
				jrParam.setField("SSTL_NO1"    		, sSTL_NO    ); //특수강재료번호1
				jrParam.setField("MODIFIER"		    , ydUserId   ); //수정자
				jrParam.setField("DEL_YN"    		, "N"    	 ); 
				
				commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insStrLocHist", logId, methodNm, "L2야드 저장위치 변경이력 등록");
				
				
				//L2저장위치 변경요청 전문 전송
				jrParam.setField("SSTL_NO"    		, sSTL_NO    		); //특수강재료번호
				jrParam.setField("YS_STR_LOC"       , sFM_YS_STR_LOC    ); //저장위치
				jrParam.setField("YD_INFO_SYNC_CD", "6_3");  //6_3으로 진양특수강용 L2 저장위치 전송 전문 쿼리 생성
				//전송할 전문에 추가
				jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YSN4L002", jrParam));  //L2에 저장위치 철분말로 변경요청(전문아이디/형식은 L2와 협의 필요)
				
				//L2위치 변경이력 추가 (일반적케이스면 L2->L3로 변경이력 정보 보내줘야하는데, L3모바일에서 L2야드 바꾸는 구조라, L3가 직접 바꿈.)
				//jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSPDJ001_JY", jrParam));  //PP로 임가공사 이송실적 보내줌.임가공사만!!!
				if(ii==0){  //하차실적은 낱개 재료단위가 아니고, 운송지시 단위이기에 최초 1회 재료번호로 운송지시 조회후 전송
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1125_JY", jrParam));  //야드->물류 하차실적 전송
				}
				
				
			}
			
			//전송할 Data가 있으면 전송 처리
			if (jrRtn != null) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of 
	
	/**
	 * 차량작업관리 작업지연 알림톡전송
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord procBayInWoSmsSend(GridData gdReq) throws DAOException {
		String methodNm = "차량작업관리 작업지연 알림톡전송[GdsYsJspSeEJB.procBayInWoSmsSend] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		try{
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			String sTelNo      = ""; //전화번호
			String sCarNo      = ""; //차량번호
			String ydCarpntCd  = ""; //포인트코드
			String sMsg        = "";
			
			String sModifier   = gdReq.getParam("userid"); //수정자(Backup Only)
			String sFlag       = gdReq.getParam("SMS_FLAG"); //BAYIN, DELAY
			String sDelyRsn    = gdReq.getParam("DELY_RSN"); //작업지연사유
			String sDelyText   = gdReq.getParam("DELY_TEXT"); //작업지연사
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, sModifier);
			
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String sSeq        = "";
			String ysStkColGp  = "";
			String ysBayGp     = "";
			String sPassGp     = "";
			String smsSndNum ="";
			
			
			//차량개수만큼 알림톡 전송

			for (int i = 0; i < rowCnt; i++ ) {
				ysStkColGp = commUtils.trim(commUtils.getValue(gdReq, "YS_STK_COL_GP", i)); //상차도
				
				ysBayGp    = ysStkColGp.substring(1, 2);
				sPassGp = ysStkColGp.substring(5, 6);
				if(ysBayGp.equals("D") || ysBayGp.equals("E") ){
					smsSndNum = "0416806698";
				}else{
					smsSndNum = "0416806678";
				}
				
				for (int j = 0; j < 21; j++ ) {
					
					sSeq = j + "";
					if (j == 0) {
						sSeq = "";
					}
					
					//01077289613<BR>(도착시간:03/23 09:48:04)
					sTelNo      = commUtils.trim(commUtils.getValue(gdReq, "DRIVER_NO" + sSeq  , i)); //전화번호  
					String[] array = sTelNo.split("<BR>");
					sTelNo = array[0];
					sTelNo.replaceAll("[^ㄱ-ㅎㅏ-ㅣa-zA-Z0-9]", "");
					sMsg        = "현재 특수강 " + ysBayGp + "동 " + sPassGp + "통로 (" + sDelyText + ")로 인하여 지연 발생중입니다. ";
					
					if ("".equals(sTelNo)) {
						continue;
					}
					
					MessageSenderTalk    sender = new MessageSenderTalk();
					
					JDTORecord recPara1 = JDTORecordFactory.getInstance().create();
	 		    	recPara1.setField("PHONE_NUM"	, new String(sTelNo));
	 		    	recPara1.setField("TMPL_CD"		, new String("CM1"));
	 		    	recPara1.setField("SND_MSG"		, new String("[현대제철 공지사항]\n" + sMsg));
	 		    	recPara1.setField("SUBJECT"		, new String(" 작업 지연 알림"));
	 		    	recPara1.setField("SMS_SND_NUM"	, new String(smsSndNum));
	 		    	recPara1.setField("RECV_ID"		,"1525733");
	 		    	recPara1.setField("GROUP_ID"	,"KaKao");
	 		    	recPara1.setField("PROGRAM_ID"	,"udttalk");
					sender.sendTalk(recPara1);
					
					
					
				}
			} //end for

			//전송할 Data가 있으면 전송 처리
			if (jrRtn != null) {
				
				jrRtn.setResultCode(logId);
				jrRtn.setResultMsg(methodNm);

				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
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
	 * 박판선재 지번정리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord reSetLocationW(GridData gdReq) throws DAOException {
		String methodNm = "박판선재 지번정리[GdsYsJspSeEJB.reSetLocationW] <" + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			//1) 박판선재 출하 완료 선재 정보 정리 TB_YS_STKLYR
			commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updStkBedActStatlocationW", logId, methodNm, "박판선재 지번정리");
			
			//2) TB_YS_STOCK 정리
			commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updStkLocW", logId, methodNm, "박판선재 지번정리");
			
			//3) TB_PB_BUNDLECOMM 정리 
			commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updBundlecommLocW", logId, methodNm, "박판선재 지번정리");
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			 
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of reSetLocationW

	/**
	 * 출하차량 오류 처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord resetCarData(GridData gdReq) throws DAOException {
		String methodNm = "출하차량 오류처리[GdsYsJspSeEJB.resetCarData] <" + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		String szMsg = null;
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			
			szMsg = "[특수강 야드 데이터 강제 처리 진행  - 수정자 : "+commUtils.trim(gdReq.getParam("USER_CHK_ID"))+"]";
			commUtils.printLog(logId, szMsg, "SL");

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			JDTORecord recPara	= JDTORecordFactory.getInstance().create();
			
			/*
			 * 	obj.SetParam("USER_CHK_ID" , frm.USER_ID.value);
					obj.setParam("DEL_ID", DEL_ID);
					obj.setParam("TYPE", type);
			 * */
			
			int[] inParamIndex = {1,2,3,4,5};
			Object[] inParam;

			szMsg  = commUtils.trim(gdReq.getParam("TRANS_ORD_DATE")) +"," + commUtils.trim(gdReq.getParam("TRANS_ORD_SEQNO"))+","+
					commUtils.trim(gdReq.getParam("DEL_ID"))+","+commUtils.trim(gdReq.getParam("USER_CHK_ID"));
			commUtils.printLog(logId, szMsg, "SL");
			
			switch (commUtils.trim(gdReq.getParam("TYPE"))){
			case "ALL":
				//전체 삭제

				szMsg = "[" + methodNm + "] 차량스케줄["+commUtils.trim(gdReq.getParam("DEL_ID"))+"] 관련 데이터 전체 삭제 시작 ";
				commUtils.printLog(logId, szMsg, "SL");
				
				 inParam  = new Object[] {
						commUtils.trim(gdReq.getParam("TRANS_ORD_DATE")),
						commUtils.trim(gdReq.getParam("TRANS_ORD_SEQNO")),
						"",
					   commUtils.trim(gdReq.getParam("USER_CHK_ID")),
					  "ALL" 
						};	
				recPara = commDao.callProcedure(inParam, inParamIndex, "com.inisteel.cim.ys.common.dao.YsCommDao.callProcedure_resetCarData");

				if(recPara != null || recPara.size() > 0){
					
					jrParam.setField("YD_CAR_SCH_ID"         	, commUtils.trim(gdReq.getParam("DEL_ID"))       ); 	//야드구분
					
					JDTORecordSet jsPnt = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getGdsOutStklyrInfo", logId, methodNm, "전체삭제 제품 조회");
					
							for (int mm = 0; mm < jsPnt.size(); mm++) {
								String szYsStklyr =  jsPnt.getRecord(mm).getFieldString("YD_STK_LYR");
								String szPbStklyr =  jsPnt.getRecord(mm).getFieldString("PB_STK_LYR");
								
								if("".equals(szYsStklyr))
										szYsStklyr  = szPbStklyr;
								
								String sStl_no = jsPnt.getRecord(mm).getFieldString("SSTL_NO");
								
								jrParam.setField("FNL_REG_PGM"			, "gdsStrLocModjm" );
								jrParam.setField("YD_GP"				, "_" );
								jrParam.setField("YD_BAY_GP"			, szYsStklyr.substring(1,2) );
								jrParam.setField("YD_EQP_GP"			, szYsStklyr.substring(2,4) );
								jrParam.setField("YS_STK_COL_NO"		, szYsStklyr.substring(4,6) );
								jrParam.setField("YS_STK_BED_NO"		, szYsStklyr.substring(6,8) );
								jrParam.setField("YS_STK_LYR_NO"		, szYsStklyr.substring(8,10));
								jrParam.setField("YS_STK_SEQ_NO"		, szYsStklyr.substring(10,11) );
								jrParam.setField("YS_STR_LOC"			, "_" + szYsStklyr.substring(1) );
								jrParam.setField("SSTL_NO"				, sStl_no ); //삭제된 번호

								jrParam.setField("MODIFIER"		, commUtils.trim(gdReq.getParam("USER_CHK_ID")));
								commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBndlCommYsStrLoc", logId, methodNm, "BUNDLE공통 야드저장위치 수정");
								
								jrParam.setField("YS_STK_COL_GP"		, "_" + szYsStklyr.substring(1,6) );
								jrParam.setField("YS_STK_BED_NO"		, szYsStklyr.substring(6,8) );
								jrParam.setField("YS_STK_LYR_NO"		,  szYsStklyr.substring(8,10) );
								jrParam.setField("YS_STK_SEQ_NO"		, szYsStklyr.substring(10,11) );
								jrParam.setField("YS_STR_LOC"			, "_" + szYsStklyr.substring(1) );
								jrParam.setField("SSTL_NO"				, sStl_no ); //삭제된 번호
								jrParam.setField("MODIFIER"		, commUtils.trim(gdReq.getParam("USER_CHK_ID")));
								
								commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStockYsStrLoc", logId, methodNm, "야드저장품 야드저장위치 수정");
								
								jrParam.setField("SSTL_NO"				, sStl_no );
								jrParam.setField("YD_GP"				, szYsStklyr.substring(0,1) );
								jrParam.setField("YD_SCH_CD"			, szYsStklyr.substring(0,2)+"YD01MM" );

								jrParam.setField("YS_UP_WR_LOC"			,  szYsStklyr.substring(0,8));
								jrParam.setField("YS_UP_WR_LAYER"		, szYsStklyr.substring(8,10) );
								jrParam.setField("YS_UP_WR_SEQ_NO"		, szYsStklyr.substring(10,11) );

								jrParam.setField("YS_DN_WR_LOC"			, "" );
								jrParam.setField("YS_DN_WR_LAYER"		, "" );
								jrParam.setField("YS_DN_WR_SEQ_NO"		, "" );
								
								jrParam.setField("MODIFIER"		, commUtils.trim(gdReq.getParam("USER_CHK_ID")));
								
								
								commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkHistByJsp", logId, methodNm, "사용자에 의한 이력정보 수정");
								
						}
					}
					
					szMsg = "[" + methodNm + "] 차량스케줄["+commUtils.trim(gdReq.getParam("DEL_ID"))+"] 관련 데이터 전체 삭제 완료 ";
			
					commUtils.printLog(logId, szMsg, "SL");
				
				
				break;
			case "CAR":
				//차량스케줄 삭제
				szMsg = "[" + methodNm + "] 차량스케줄["+commUtils.trim(gdReq.getParam("DEL_ID"))+"] 데이터 삭제 시작 ";
				commUtils.printLog(logId, szMsg, "SL");

				
				 inParam  = new Object[] {
						commUtils.trim(gdReq.getParam("TRANS_ORD_DATE")),
						commUtils.trim(gdReq.getParam("TRANS_ORD_SEQNO")),
						commUtils.trim(gdReq.getParam("DEL_ID")),
					   commUtils.trim(gdReq.getParam("USER_CHK_ID")),
					 "CAR"   
						};	
				recPara = commDao.callProcedure(inParam, inParamIndex, "com.inisteel.cim.ys.common.dao.YsCommDao.callProcedure_resetCarData");
	
		
					szMsg = "[" + methodNm + "] 차량스케줄["+commUtils.trim(gdReq.getParam("DEL_ID"))+"] 데이터 삭제 완료 ";
					commUtils.printLog(logId, szMsg, "SL");
				
				break;
			case "WRK":
				//작업예약삭제
				//작업예약재료 삭제
			
				szMsg = "[" + methodNm + "] 차량- 작업예약["+commUtils.trim(gdReq.getParam("DEL_ID"))+"] 데이터 삭제 시작 ";
				commUtils.printLog(logId, szMsg, "SL");
				
				 inParam  = new Object[] {
						commUtils.trim(gdReq.getParam("TRANS_ORD_DATE")),
						commUtils.trim(gdReq.getParam("TRANS_ORD_SEQNO")),
						commUtils.trim(gdReq.getParam("DEL_ID")),
					   commUtils.trim(gdReq.getParam("USER_CHK_ID")),
					   "WRK"  
						};	
				recPara = commDao.callProcedure(inParam, inParamIndex, "com.inisteel.cim.ys.common.dao.YsCommDao.callProcedure_resetCarData");
	
			
					szMsg = "[" + methodNm + "] 차량- 작업예약["+commUtils.trim(gdReq.getParam("DEL_ID"))+"] 데이터 삭제 완료 ";
					commUtils.printLog(logId, szMsg, "SL");
				
				break;	
			case "CRN":
				//크레인스케줄삭제
				szMsg = "[" + methodNm + "] 차량- 크레인스케줄["+commUtils.trim(gdReq.getParam("DEL_ID"))+"] 데이터 삭제 시작 ";
				commUtils.printLog(logId, szMsg, "SL");
				
				 inParam  = new Object[] {
						commUtils.trim(gdReq.getParam("TRANS_ORD_DATE")),
						commUtils.trim(gdReq.getParam("TRANS_ORD_SEQNO")),
						commUtils.trim(gdReq.getParam("DEL_ID")),
					   commUtils.trim(gdReq.getParam("USER_CHK_ID")),
					   "CRN"
						};	
				recPara = commDao.callProcedure(inParam, inParamIndex, "com.inisteel.cim.ys.common.dao.YsCommDao.callProcedure_resetCarData");

				szMsg = "[" + methodNm + "] 차량- 크레인스케줄["+commUtils.trim(gdReq.getParam("DEL_ID"))+"] 데이터 삭제 완료 ";
				commUtils.printLog(logId, szMsg, "SL");
				
				break;
			case "STKLYR":
				//지번삭제 
				String szYsStklyr =  commUtils.trim(gdReq.getParam("YD_STK_LYR"));
				String szPbStklyr =  commUtils.trim(gdReq.getParam("PB_STK_LYR"));
				
				String sStl_no = commUtils.trim(gdReq.getParam("SSTL_NO"));
		
				
				szMsg = "[" + methodNm + "] 차량- 저장위저장위치["+commUtils.trim(gdReq.getParam("DEL_ID"))+"] 데이터 삭제 시작 ";
				commUtils.printLog(logId, szMsg, "SL");
				
				 inParam  = new Object[] {
						commUtils.trim(gdReq.getParam("TRANS_ORD_DATE")),
						commUtils.trim(gdReq.getParam("TRANS_ORD_SEQNO")),
						commUtils.trim(gdReq.getParam("DEL_ID")),
					   commUtils.trim(gdReq.getParam("USER_CHK_ID")),
					   "STKLYR"
						};	
				recPara = commDao.callProcedure(inParam, inParamIndex, "com.inisteel.cim.ys.common.dao.YsCommDao.callProcedure_resetCarData");
				
					
					commUtils.printLog(logId, szMsg, "SL");
					
					if("".equals(szYsStklyr))
					{
						szYsStklyr = szPbStklyr;
					}
					jrParam.setField("FNL_REG_PGM"			, "gdsStrLocModjm" );
					jrParam.setField("YD_GP"				, "_" );
					jrParam.setField("YD_BAY_GP"			, szYsStklyr.substring(1,2) );
					jrParam.setField("YD_EQP_GP"			, szYsStklyr.substring(2,4) );
					jrParam.setField("YS_STK_COL_NO"		, szYsStklyr.substring(4,6) );
					jrParam.setField("YS_STK_BED_NO"		, szYsStklyr.substring(6,8) );
					jrParam.setField("YS_STK_LYR_NO"		, szYsStklyr.substring(8,10));
					jrParam.setField("YS_STK_SEQ_NO"		, szYsStklyr.substring(10,11) );
					jrParam.setField("YS_STR_LOC"			, "_" + szYsStklyr.substring(1) );
					jrParam.setField("SSTL_NO"				, sStl_no ); //삭제된 번호

					jrParam.setField("MODIFIER"		, commUtils.trim(gdReq.getParam("USER_CHK_ID")));
					
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBndlCommYsStrLoc", logId, methodNm, "BUNDLE공통 야드저장위치 수정");
					
					
					jrParam.setField("YS_STK_COL_GP"		, "_" + szYsStklyr.substring(1,6) );
					jrParam.setField("YS_STK_BED_NO"		, szYsStklyr.substring(6,8) );
					jrParam.setField("YS_STK_LYR_NO"		,  szYsStklyr.substring(8,10) );
					jrParam.setField("YS_STK_SEQ_NO"		, szYsStklyr.substring(10,11) );
					jrParam.setField("YS_STR_LOC"			, "_" + szYsStklyr.substring(1) );
					jrParam.setField("SSTL_NO"				, sStl_no ); //삭제된 번호
					jrParam.setField("MODIFIER"		, commUtils.trim(gdReq.getParam("USER_CHK_ID")));
					
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStockYsStrLoc", logId, methodNm, "야드저장품 야드저장위치 수정");
					
					jrParam.setField("SSTL_NO"				, sStl_no );
					jrParam.setField("YD_GP"				, szYsStklyr.substring(0,1));
					jrParam.setField("YD_SCH_CD"			, szYsStklyr.substring(0,2)+"YD01MM" );

					jrParam.setField("YS_UP_WR_LOC"			,  szYsStklyr.substring(0,8));
					jrParam.setField("YS_UP_WR_LAYER"		, szYsStklyr.substring(8,10) );
					jrParam.setField("YS_UP_WR_SEQ_NO"		, szYsStklyr.substring(10,11) );

					jrParam.setField("YS_DN_WR_LOC"			, "" );
					jrParam.setField("YS_DN_WR_LAYER"		, "" );
					jrParam.setField("YS_DN_WR_SEQ_NO"		, "" );
					
					jrParam.setField("MODIFIER"		, commUtils.trim(gdReq.getParam("USER_CHK_ID")));
					
					
					commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkHistByJsp", logId, methodNm, "사용자에 의한 이력정보 수정");
					

					szMsg = "[" + methodNm + "] 차량- 저장위치["+commUtils.trim(gdReq.getParam("DEL_ID"))+"] 데이터 삭제 완료 ";
				
				break;
				
			case "CARPOINT" : 
			
				
				break;
				
			}
			
			
		
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			 
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of resetCarData
	
	/**
	 *      [A] 오퍼레이션명 : 인터페이스Test 조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getIfTest(GridData gdReq) throws DAOException {
		String methodNm = "인터페이스Test 조회[GdsYsJspSeEJB.getIfTest] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		
		try {
			JDTORecordSet jrRst = commDao.getIfTest(gdReq);
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			return commUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
}