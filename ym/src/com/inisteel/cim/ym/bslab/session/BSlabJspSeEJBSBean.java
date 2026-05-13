/**
 * @(#)BSlabJspSeEJBSBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2017/02/02
 *
 * @description      B열연 SLAB 야드 화면 관리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2017/02/02   정종균      조병기      최초 등록 
 *   
 */
package com.inisteel.cim.ym.bslab.session; 

import java.util.Vector;
import java.util.regex.Pattern;

import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.CmUtil;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.bcommon.util.YmCommUtils;
import com.inisteel.cim.ym.bcommon.util.YmConstant;
import com.inisteel.cim.ym.bcommon.dao.YmCommDAO;
import com.inisteel.cim.ym.bslab.session.BSlabComm;
import com.inisteel.cim.ym.common.YmCommonConst;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;


/**
 *      [A] 클래스명 : B열연 SLAB 야드 화면관리 Session EJB 
 *
 * @ejb.bean name="BSlabJspSeEJB" jndi-name="BSlabJspSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300" 
 * @ejb.transaction type="Required" 
*/
public class BSlabJspSeEJBSBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;
	private YmCommUtils commUtils = new YmCommUtils();
	private YmCommDAO commDao = new YmCommDAO();
	private String szSessionName = getClass().getName();
	private YmComm ymComm = new YmComm();
	private BSlabComm bSlabComm = new BSlabComm();
	
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
		String methodNm = "조회[BSlabJspSeEJB.getSelectData] < " + gdReq.getNavigateValue();
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
		String methodNm = "조회[BSlabJspSeEJB.getSelectData] < " + recPara.getResultMsg();
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
		String methodNm = "IFTest Layout 변경[BSlabJspSeEJB.updIfTestData] < " + gdReq.getNavigateValue();
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
		String methodNm = "IFTest 전송[BSlabJspSeEJB.sndIfTest] < " + gdReq.getNavigateValue();
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

			sndData.setResultCode(logId);	//Log ID
			sndData.setResultMsg(methodNm);	//Log Method Name			
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
		String methodNm = "IFTest EAI전송[BSlabJspSeEJB.sndIfTestEAI] < " + gdReq.getNavigateValue();
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
	 * B열연 SLAB 위치별적치현황조회 - LAYER활성상태 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updLayerActStat(GridData gdReq) throws DAOException {
		String methodNm = "B열연 SLAB 위치별적치현황조회 - LAYER활성상태 변경[BSlabJspSeEJB.updBedActStat] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			jrParam.setField("STACK_COL_GP"		, gdReq.getParam("STACK_COL_GP"));
			jrParam.setField("STACK_BED_GP"		, gdReq.getParam("STACK_BED_GP")); 
			jrParam.setField("MIN_LAYER_GP"		, gdReq.getParam("MIN_LAYER_GP")); 
			jrParam.setField("MAX_LAYER_GP"		, gdReq.getParam("MAX_LAYER_GP")); 
			jrParam.setField("NEW_ACTIVE_STAT"	, gdReq.getParam("NEW_ACTIVE_STAT")); 
			jrParam.setField("OLD_ACTIVE_STAT"	, gdReq.getParam("OLD_ACTIVE_STAT")); 
			
			commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.bslabLocStkStatjv.updLayerActStat", logId, methodNm, "LAYER활성상태 변경");
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updLayerActStat
	
	/**
	 * 차량예정정보 전송
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord regCarUdExplainInfo(GridData gdReq) throws DAOException {
		String methodNm = "차량예정정보 전송[BSlabJspSeEJB.regCarUdExplainInfo] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

	    String szMsg           		= "";
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			//차량위치조회
			jrParam.setField("PT_LOAD_LOC"		, gdReq.getParam("YD_CARPNT_CD"));
			JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdGetCarNoByLoc", logId, methodNm, "차량위치조회"); 
			
	    	if(rsResult == null || rsResult.size() <= 0) {
	    		
	    		szMsg = gdReq.getParam("YD_CARPNT_CD") + " 해당 위치에 차량이 없습니다.";
	    		jrRtn.setField("RTN_MSG", szMsg);
	    		
				szMsg="["+methodNm+"] " + szMsg;
				commUtils.printLog(logId, szMsg, "SL");
				
				return jrRtn;
			}
	    	if(!gdReq.getParam("CAR_NO").equals(rsResult.getRecord(0).getFieldString("CAR_NO"))) {
	    		
	    		szMsg = "해당위치에 차량정보가 틀립니다. 입력차량번호:" + gdReq.getParam("CAR_NO") + ",검색결과차량번호:"+rsResult.getRecord(0).getFieldString("CAR_NO");
	    		jrRtn.setField("RTN_MSG", szMsg);
	    		
				szMsg="["+methodNm+"] " + szMsg;
				commUtils.printLog(logId, szMsg, "SL");
				
				return jrRtn;
	    	}
			
	    	//전송 데이터 설정
	    	jrParam.setField("PT_LOAD_LOC"			, gdReq.getParam("YD_CARPNT_CD")); //상차도 위치
	    	jrParam.setField("CAR_NO"				, gdReq.getParam("CAR_NO")); //차량번호	
	    	jrParam.setField("CARD_NO"				, ""); //차량번호	
	    	jrParam.setField("PT_CLS"				, gdReq.getParam("CAR_WK_GP")); //차량구분 "TT":TTcar, "TR":트레일러
	    	jrParam.setField("WORK_CLS"				, gdReq.getParam("RETN_WK_GP")); //작업구분 1:출하입고,2:출하출고,3:구내입고,4:구내출고
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

	    	jrParam.setField("WORK_COIL_MAX_CNT"	, Integer.toString(rowCnt)); //작업총수량	
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				jrParam.setField("STOCK_ID_"+ii			,commUtils.getValue(gdReq, "YD_STL_NO"			, ii)); //재료번호
				jrParam.setField("LOAD_LOC_CD_"+ii		,commUtils.getValue(gdReq, "YD_CAR_UPP_LOC_CD"	, ii)); //차량적재위치
				//jrParam.setField("MAT_WGT_"+ii			,commUtils.getValue(gdReq, "YD_COIL_WT"			, ii)); //재료중량
				//jrParam.setField("MAT_THK_"+ii			,commUtils.getValue(gdReq, "YD_COIL_T"			, ii)); //재료두께
				//jrParam.setField("MAT_WTH_"+ii			,commUtils.getValue(gdReq, "YD_COIL_W"			, ii)); //재료폭
				//jrParam.setField("MAT_LEN_"+ii			,commUtils.getValue(gdReq, "YD_COIL_LEN"		, ii)); //재료길이
				//jrParam.setField("MAT_ODIA_"+ii			,commUtils.getValue(gdReq, "YD_COIL_OUTDIA"		, ii)); //재료외경
				//jrParam.setField("MAT_IDIA_"+ii			,commUtils.getValue(gdReq, "YD_COIL_INDIA"		, ii)); //재료내경
				jrParam.setField("WORK_STATE_"+ii		,commUtils.getValue(gdReq, "YD_WORK_STATE"		, ii)); //작업상태
				//jrParam.setField("YD_CURR_BAY_GP_"+ii	,gdReq.getParam("YD_CARPNT_CD")); //동번호?? 
				
				//주석처리된 정보는 SLAB공통 테이블에서 가져오도록 쿼리 작성함..
			}

			//차량예정정보 백업 송신
			jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L008BackUp", jrParam));
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of regCarUdExplainInfo
	
	
	/**
	 * SLAB 재열재 조회 - 작업요구
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord reHtWrkDmd(GridData gdReq) throws DAOException {
		String methodNm = "SLAB 재열재 조회 - 작업요구[BSlabJspSeEJB.reHtWrkDmd] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			String sYD_GP = gdReq.getParam("YD_GP");
			String sHB_GP = "";
			String sYD_SCH_CD = "";
			
			//Return Value
			JDTORecord    jrRtn    = null;
			JDTORecordSet rsResult = null;
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
//			jrParam.setField("STOCK_ID", gdReq.getParam("STOCK_ID"));
			
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				sHB_GP     = commUtils.getValue(gdReq, "HB_GP"   , ii);
				sYD_SCH_CD = sYD_GP+sHB_GP+"HB01LM";
				/************************************
				 *  0.LAYER 정보 삭제 20171114
				 ***********************************/
				/*
				UPDATE TB_YM_STACKLAYER
				   SET STOCK_ID         = NULL
				     , STACK_LAYER_STAT = 'E'
				     , MODIFIER         = :V_MODIFIER
				     , MOD_DDTT         = SYSDATE
				 WHERE STOCK_ID = (SELECT STOCK_ID
				                     FROM TB_YM_STACKLAYER
				                    WHERE STACK_COL_GP LIKE '2_CT%'
				                      AND STOCK_ID = :V_STOCK_ID
				                  )
				 */
				jrParam.setField("STOCK_ID"        , commUtils.getValue(gdReq, "SLAB_NO" , ii)); //
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updClearLyrCtcByStockId");
				
				/************************************
				 *** 1. M Table 저장품 정보 셋팅
				 ***    M Table 저장품 정보 'C' 셋팅
				 ************************************/
				jrParam.setField("STACK_LAYER_STAT", "C");
				jrParam.setField("STACK_COL_GP"  , sYD_GP+sHB_GP+"MT01");
				jrParam.setField("STACK_BED_GP"  , "01");
				jrParam.setField("STACK_LAYER_GP", "01");
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
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateCraneStackLayerStat", logId, methodNm, "저장품 정보 세팅");
				
				/********************************************
				 *** 2. 작업요구(Holding Bed Line Off) 생성 TODO
				 ********************************************/
				/*
				INSERT INTO TB_YM_WRKBOOK (
				       YD_WBOOK_ID        --야드작업예약ID
				     , YD_GP              --야드구분
				     , YD_BAY_GP          --야드동구분
				     , YD_SCH_CD          --야드스케쥴코드
				     , YD_SCH_PRIOR       --야드스케쥴우선순위
				     , YD_SCH_PROG_STAT   --야드스케쥴진행상태
				     , YD_SCH_ST_GP       --야드스케쥴기동구분
				     , YD_SCH_REQ_GP      --야드스케쥴요청구분
				     , YD_AIM_YD_GP       --야드목표야드구분
				     , YD_AIM_BAY_GP      --야드목표동구분
				     , YD_TO_LOC_DCSN_MTD --야드To위치결정방법
				     , YD_TO_LOC_GUIDE    --야드To위치Guide
				     , YD_WRK_PLAN_TCAR   --야드작업계획대차
				     , YD_CAR_USE_GP      --야드차량사용구분
				     , TRN_EQP_CD         --운송장비코드
				     , CAR_NO             --차량번호
				     , CARD_NO            --카드번호
				     , PTOP_PLNT_GP       --조업공장구분
				     , DEST_TEL_NO        --목적지전화번호
				     , DIST_SHIPASSIGN_GP --출하배선지시구분 
				     , YD_WRK_PLAN_CRN    --야드작업계획크레인
				     , REGISTER           --등록자
				     , REG_DDTT           --등록일시
				     , MODIFIER           --수정자
				     , MOD_DDTT           --수정일시
				     , DEL_YN             --삭제유무
				) VALUES (
				      :V_YD_WBOOK_ID
				     ,:V_YD_GP
				     ,:V_YD_BAY_GP
				     ,:V_YD_SCH_CD
				     ,TO_NUMBER(:V_YD_SCH_PRIOR)
				     ,:V_YD_SCH_PROG_STAT
				     ,:V_YD_SCH_ST_GP
				     ,:V_YD_SCH_REQ_GP
				     ,:V_YD_AIM_YD_GP
				     ,:V_YD_AIM_BAY_GP
				     ,:V_YD_TO_LOC_DCSN_MTD
				     ,:V_YD_TO_LOC_GUIDE
				     ,:V_YD_WRK_PLAN_TCAR
				     ,:V_YD_CAR_USE_GP
				     ,:V_TRN_EQP_CD
				     ,:V_CAR_NO
				     ,:V_CARD_NO
				     ,:V_PTOP_PLNT_GP
				     ,:V_DEST_TEL_NO
				     ,:V_DIST_SHIPASSIGN_GP
				     ,:V_YD_WRK_PLAN_CRN      
				     ,:V_MODIFIER
				     ,SYSDATE
				     ,:V_MODIFIER
				     ,SYSDATE
				     ,'N'
				)
				 */
				String ydWbookId = commDao.getSeqId(logId, methodNm, "WrkBook");
				
				jrParam.setField("YD_WBOOK_ID", ydWbookId);
				jrParam.setField("YD_GP"      , sYD_GP);
				jrParam.setField("YD_BAY_GP"  , sHB_GP);
				jrParam.setField("YD_SCH_CD"         , sYD_SCH_CD);
				jrParam.setField("YD_SCH_PRIOR"      , "");
				jrParam.setField("YD_SCH_PROG_STAT"  , "W");//야드스케쥴진행상태(스케줄수행대기)
				jrParam.setField("YD_SCH_ST_GP"      , "O");//야드스케쥴기동구분(Manual)
				jrParam.setField("YD_SCH_REQ_GP"     , "M");//야드스케쥴요청구분(이적)
				jrParam.setField("YD_AIM_YD_GP"      , "");
				jrParam.setField("YD_AIM_BAY_GP"     , "");
				jrParam.setField("YD_TO_LOC_DCSN_MTD", "O"); 
				jrParam.setField("YD_TO_LOC_GUIDE"   , sYD_GP+sHB_GP+"HB"+("A".equals(sHB_GP)?"01":"03"));    
				jrParam.setField("YD_WRK_PLAN_TCAR"  , "");   
				jrParam.setField("YD_CAR_USE_GP"     , "");      
				jrParam.setField("TRN_EQP_CD"        , "");         
				jrParam.setField("CAR_NO"            , "");             
				jrParam.setField("CARD_NO"           , "");            
				jrParam.setField("PTOP_PLNT_GP"      , "");       
				jrParam.setField("DEST_TEL_NO"       , "");        
				jrParam.setField("DIST_SHIPASSIGN_GP", ""); 
				jrParam.setField("YD_WRK_PLAN_CRN"   , "");    
				
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule  
				SELECT YD_SCH_CD
				     , YD_WRK_CRN
				     , YD_WRK_CRN_PRIOR
				  FROM TB_YM_SCHEDULERULE
				 WHERE DEL_YN = 'N'
				   AND YD_SCH_CD = :V_YD_SCH_CD
				*/   
				JDTORecordSet jsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
				if (jsResult != null && jsResult.size() > 0) {
					String sYD_SCH_PRIOR = jsResult.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
					jrParam.setField("YD_SCH_PRIOR"      , sYD_SCH_PRIOR);
				} 
				
				//commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insWrkBook");
				bSlabComm.execQueryId(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insWrkBook"); //작업예약 생성	
				/**********************************************************
				* 2. 작업예약재료(TB_YM_WRKBOOKMTL) 생성
				**********************************************************/
				
				jrParam.setField("YD_WBOOK_ID"		, ydWbookId);
				jrParam.setField("STOCK_ID"			, commUtils.getValue(gdReq, "SLAB_NO", ii));
				jrParam.setField("STACK_COL_GP"		, sYD_GP + sHB_GP + "MT01");
				jrParam.setField("STACK_BED_GP"		, "01");
				jrParam.setField("STACK_LAYER_GP"	, "01");
				
				/*jrParam.setField("STACK_COL_GP"  , sYD_GP+sHB_GP+"MT01");
				jrParam.setField("STACK_BED_GP"  , "01");
				jrParam.setField("STACK_LAYER_GP", "01");*/
				
				//commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insYmWrkBookMtl", logId, methodNm, "작업예약재료(TB_YM_WRKBOOKMTL) 생성");
				bSlabComm.execQueryId(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insYmWrkBookMtl");			
				/**********************************************************
				* 3. 크레인 스케줄 호출
				**********************************************************/		
				jrParam.setField("JMS_TC_CD"    , "YMYMJ202"); 
				jrParam.setField("YD_WBOOK_ID"  , ydWbookId); //야드작업예약ID
				jrParam.setField("YD_SCH_CD"    , ""  ); //야드스케쥴코드
				jrParam.setField("YD_EQP_ID"    , ""  ); //야드설비ID
				
				EJBConnector ejbConn = new EJBConnector("default","BSlabSchSeEJB",this);
				jrRtn = (JDTORecord)ejbConn.trx("procYMYMJ202",new Class[]{JDTORecord.class},new Object[]{ jrParam });				
					
				/*JDTORecord jrCrnSchMsg = JDTORecordFactory.getInstance().create();
				jrCrnSchMsg.setField("JMS_TC_CD"			, "YMYMJ202"); 
				jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시				
				jrCrnSchMsg.setField("YD_WBOOK_ID"  		, ydWbookId); //작업예약ID
				jrCrnSchMsg.setField("YD_SCH_CD"  		, ""); //야드스케쥴코드
				jrCrnSchMsg.setField("YD_EQP_ID"  		, ""); //야드설비ID
				jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);	*/
				
				/**********************************************************
				* 4. 재열재 저장품제원(YMA8L002) 송신
				**********************************************************/	

				jrParam.setField("MSG_GP"			, "I" 		); //전문구분(U:수정)
				jrParam.setField("YD_INFO_SYNC_CD"	, "5" 		); //야드정보동기화코드(지정저장품)
				jrParam.setField("STOCK_ID"		 	, commUtils.getValue(gdReq, "SLAB_NO" , ii)); 
				
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L002", jrParam));
								
			} // end for

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of reHtWrkDmd
	
	
	
	/**
	 * SLAB 재열재 조회 - 구분변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updReheatSlabGp(GridData gdReq) throws DAOException {
		String methodNm = "SLAB 재열재 조회 - 구분변경[BSlabJspSeEJB.updReheatSlabGp] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			String sGUNBUN = "";
			
			//Return Value
			JDTORecord    jrRtn    = null;
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				sGUNBUN = commUtils.getValue(gdReq, "GUNBUN"  , ii);
				
				/************************************
				 *  1.SLABCOMM 정보 UPDATE
				 ***********************************/
				if("B".equals(sGUNBUN)) {
					jrParam.setField("SLAB_NO"        , commUtils.getValue(gdReq, "SLAB_NO" , ii));
					jrParam.setField("REHEAT_SLAB_GP" , commUtils.getValue(gdReq, "GUNBUN"  , ii));
					commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updReheatSlabGp2");
				} else {
					jrParam.setField("SLAB_NO"        , commUtils.getValue(gdReq, "SLAB_NO" , ii));
					jrParam.setField("REHEAT_SLAB_GP" , commUtils.getValue(gdReq, "GUNBUN"  , ii));
					commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updReheatSlabGp1");
				}
				
				/**********************************************************
				* 2. 재열재 저장품제원(YMA8L002) 송신
				**********************************************************/	

				jrParam.setField("MSG_GP"			, "U" 		); //전문구분(U:수정)
				jrParam.setField("YD_INFO_SYNC_CD"	, "5" 		); //야드정보동기화코드(지정저장품)
				jrParam.setField("STOCK_ID"		 	, commUtils.getValue(gdReq, "SLAB_NO" , ii)); 
				
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L002", jrParam));
								
			} // end for

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updReheatSlabGp
	
	
	
	/**
	 * 야드설비정비등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord insEqpTrblReg(GridData gdReq) throws DAOException {
		String methodNm = "야드설비정비등록[BSlabJspFaEJB.insEqpTrblReg] < " + gdReq.getNavigateValue();
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
	 *      [A] 오퍼레이션명 : 크레인작업예약관리 스케줄기동
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord procCrnWrkBookStart(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업예약관리 스케줄기동[BSlabJspSeEJB.procCrnWrkBookStart] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;
			String currDate   = commUtils.getDateTime14();	//현재시각
			
			String sFLAG            = commUtils.nvl(gdReq.getParam("FLAG"           ), "N"); //
			String sARR_YD_WBOOK_ID = commUtils.trim(gdReq.getParam("ARR_YD_WBOOK_ID")); //
			String sDIST_SHIPASSIGN_GP = commUtils.nvl(gdReq.getParam("DIST_SHIPASSIGN_GP"           ), ""); //1매 스케줄기동
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			//작업예약 상단스케줄 전부 기동
			if ("Y".equals(sFLAG)) {
				/*
				SELECT DISTINCT WB.YD_WBOOK_ID
				  FROM TB_YM_WRKBOOK     WB
				     , TB_YM_WRKBOOKMTL  WM
				     , (SELECT WM.STACK_COL_GP
				             , WM.STACK_BED_GP
				             , WM.STACK_LAYER_GP
				          FROM TB_YM_WRKBOOK     WB
				             , TB_YM_WRKBOOKMTL  WM
				         WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID   
				           AND WB.DEL_YN = 'N'
				           AND WM.DEL_YN = 'N'
				           AND WB.YD_WBOOK_ID IN (SELECT REGEXP_SUBSTR(SSTL_NOS, '[^,]+', 1, LEVEL) AS YD_WBOOK_ID
				                                    FROM (SELECT :V_ARR_YD_WBOOK_ID AS SSTL_NOS FROM DUAL)
				                                 CONNECT BY REGEXP_SUBSTR(SSTL_NOS, '[^,]+', 1, LEVEL) IS NOT NULL)
				       ) CUR_WB
				 WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
				   AND WB.DEL_YN = 'N'
				   AND WM.DEL_YN = 'N'
				   AND WM.STACK_COL_GP    = CUR_WB.STACK_COL_GP
				   AND WM.STACK_BED_GP    = CUR_WB.STACK_BED_GP
				   AND WM.STACK_LAYER_GP >= CUR_WB.STACK_LAYER_GP
				   AND WB.YD_WBOOK_ID NOT IN (SELECT YD_WBOOK_ID
				                                FROM TB_YM_CRNSCH 
				                               WHERE DEL_YN = 'N'
				                                 AND YD_GP  = '2'
				                              )
				 ORDER BY YD_WBOOK_ID
				 */
				jrParam.setField("ARR_YD_WBOOK_ID", sARR_YD_WBOOK_ID);
				JDTORecordSet jrSchList = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getPreGoWBookIdStList");
				
				for (int idx = 0; idx < jrSchList.size(); ++idx) {
					//DAO Parameter - Log ID, Method, 수정자 Set
					JDTORecord jrTcParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
					jrTcParam.setResultCode(logId);	//Log ID
					jrTcParam.setResultMsg(methodNm);	//Log Method Name
	
					jrTcParam.setField("JMS_TC_CD", "YMYMJ202"); //야드작업예약ID
					jrTcParam.setField("JMS_TC_CREATE_DDTT", currDate); //JMSTC생성일시				
					
					jrTcParam.setField("YD_WBOOK_ID", jrSchList.getRecord(idx).getFieldString("YD_WBOOK_ID")); //야드작업예약ID
					jrTcParam.setField("YD_SCH_CD"  , ""); //야드스케쥴코드
					jrTcParam.setField("YD_EQP_ID"  , ""); //야드설비ID
					jrTcParam.setField("DIST_SHIPASSIGN_GP"  , sDIST_SHIPASSIGN_GP); //1매 스케줄기동여부
					jrTcParam.setField("YD_TO_LOC_GUIDE_FNL" , "");
					
					//작업예약 화면에서 작업예약 1매씩 실행여부 Update
					/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updDistShipassWrkBook 
						UPDATE TB_YM_WRKBOOK
						   SET DIST_SHIPASSIGN_GP = :V_DIST_SHIPASSIGN_GP
						      ,MODIFIER    = :V_MODIFIER
						      ,MOD_DDTT    = SYSDATE
						      ,YD_TO_LOC_GUIDE_FNL = :V_YD_TO_LOC_GUIDE_FNL
						WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
					 */
					commDao.update(jrTcParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updDistShipassWrkBook", logId, methodNm, "작업예약 1매씩 실행여부 Update");
					
					
					//크레인스케줄기동 전문
					EJBConnector sndConn = new EJBConnector("default", "BSlabSchSeEJB", this);
					JDTORecord jrRtn1 = (JDTORecord)sndConn.trx("rcvYMYMJ202", new Class[] { JDTORecord.class }, new Object[] { jrTcParam });
					jrRtn = commUtils.addSndData(jrRtn, jrRtn1);
				}
			}
			
			//선택된 스케줄만 기동
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			if ("N".equals(sFLAG)) {
				for (int ii = 0; ii < rowCnt; ii++) {
					//DAO Parameter - Log ID, Method, 수정자 Set
					JDTORecord jrTcParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
					jrTcParam.setResultCode(logId);	//Log ID
					jrTcParam.setResultMsg(methodNm);	//Log Method Name
	
					jrTcParam.setField("JMS_TC_CD", "YMYMJ202"); //야드작업예약ID
					jrTcParam.setField("JMS_TC_CREATE_DDTT", currDate); //JMSTC생성일시				
					
					jrTcParam.setField("YD_WBOOK_ID", commUtils.trim(gdReq.getHeader("YD_WBOOK_ID").getValue(ii))); //야드작업예약ID
					jrTcParam.setField("YD_SCH_CD"  , ""); //야드스케쥴코드
					jrTcParam.setField("YD_EQP_ID"  , ""); //야드설비ID
					jrTcParam.setField("DIST_SHIPASSIGN_GP"  , sDIST_SHIPASSIGN_GP); //1매 스케줄기동여부
					jrTcParam.setField("YD_TO_LOC_GUIDE_FNL" , "");
					
					//작업예약 화면에서 작업예약 1매씩 실행여부 Update
					/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updDistShipassWrkBook 
						UPDATE TB_YM_WRKBOOK
						   SET DIST_SHIPASSIGN_GP = :V_DIST_SHIPASSIGN_GP
						      ,MODIFIER    = :V_MODIFIER
						      ,MOD_DDTT    = SYSDATE
						      ,YD_TO_LOC_GUIDE_FNL = :V_YD_TO_LOC_GUIDE_FNL
						WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
					 */
					commDao.update(jrTcParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updDistShipassWrkBook", logId, methodNm, "작업예약 1매씩 실행여부 Update");

					//크레인스케줄기동 전문
					EJBConnector sndConn = new EJBConnector("default", "BSlabSchSeEJB", this);
					JDTORecord jrRtn2 = (JDTORecord)sndConn.trx("procYMYMJ202", new Class[] { JDTORecord.class }, new Object[] { jrTcParam });
					jrRtn = commUtils.addSndData(jrRtn, jrRtn2);
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
	 *      [A] 오퍼레이션명 : 작업예약관리-삭제
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord delWrkBook(GridData gdReq) throws DAOException {
		String methodNm = "작업예약관리-삭제[BSlabJspSeEJB.delWrkBook] < " + gdReq.getNavigateValue();
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
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				
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
	 *      [A] 오퍼레이션명 : 작업예약 취소처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtWrkBookCncl(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "작업예약 취소처리[BSlabJspSeEJB.trtWrkBookCncl] < " + rcvMsg.getResultMsg();
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
			JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getCommWbCrnSch", logId, methodNm, "크레인작업지시read");
			if (jsCrnSch != null && jsCrnSch.size() > 0) {				
				throw new Exception("작업예약ID[" + ydWbookId + "]의 크레인스케줄 정보가 " + jsCrnSch.size() + " 건 존재합니다.");
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
			commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updCommCarSchWbDel", logId, methodNm, "TB_YD_CARSCH");				
		
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
			commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updCommTcarSchWbDel", logId, methodNm, "TB_YM_TCARSCH");				

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
			commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updDelYnWrkBookMtl", logId, methodNm, "TB_YM_WRKBOOKMTL");				

			//작업예약 삭제
			/*
			UPDATE TB_YM_WRKBOOK
			   SET MODIFIER    = :V_MODIFIER
			      ,MOD_DDTT    = SYSDATE
			      ,DEL_YN      = 'Y'
			 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
			   AND DEL_YN      = 'N'
			 */
			commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updDelYnWrkBook", logId, methodNm, "TB_YM_WRKBOOK");				
			

			
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
		String methodNm = "크레인작업요구현황조회 스케줄취소[BSlabJspSeEJB.updCraneSchCancel] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			String sTRT_DTL_GP = commUtils.trim(gdReq.getParam("TRT_DTL_GP"));	//처리상세구분 RS스케줄재기동 SC, MC 스케줄 취소
//			String sIS_SCH_MTL = StringHelper.evl(commUtils.trim(gdReq.getParam("IS_SCH_MTL")), "N"); // 재료단위 스케줄 취소여부
			String sYD_GP      = StringHelper.evl(commUtils.trim(gdReq.getParam("YD_GP")), "2");
			
			//Return Value
			JDTORecord jrRtn = null;
			
			boolean autoFlag = false;
			
			String szydEqpStat      = "";
			String szEqpAutoCrnMode = "";
			String szEqpAutoCrnYN   = "";
			
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
			
			String sYD_WRK_PROG_STAT_FLAG = "W";
			
			String sYD_DN_WO_LOC    = ""; //권하지시위치 java에서 체크
			String sYD_TO_LOC_GUIDE = ""; //To위치 가이드
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			commUtils.printLog(logId, "○○○ 스케줄취소 리스트 = " + rowCnt + " 건", "[info]");
			
			for (int i = 0; i < rowCnt; i++) {
				
				sSTACK_COL_GP     = commUtils.getValue(gdReq, "YD_UP_WO_LOC_LAYER", i).substring(0, 6);
				sYD_BED_GP        = commUtils.getValue(gdReq, "YD_UP_WO_LOC_LAYER", i).substring(6, 8);
				sYD_TO_LOC_GUIDE  = commUtils.getValue(gdReq, "YD_TO_LOC_GUIDE"   , i); //to위치 가이드
				sYD_WRK_PROG_STAT = commUtils.getValue(gdReq, "YD_WRK_PROG_STAT", i);
				sYD_EQP_ID        = commUtils.getValue(gdReq, "YD_EQP_ID"       , i);
				sYD_CRN_SCH_ID    = commUtils.getValue(gdReq, "YD_CRN_SCH_ID"   , i);
				sYD_SCH_CD        = commUtils.getValue(gdReq, "YD_SCH_CD"       , i);
				sYD_WBOOK_ID      = commUtils.getValue(gdReq, "YD_WBOOK_ID"     , i);

				jrParam.setField("STACK_COL_GP", sSTACK_COL_GP);
				jrParam.setField("STACK_BED_GP", sYD_BED_GP);
				
				/*************************************
				 * 크레인 상태 체크
				 ************************************/
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
					
					szydEqpStat      = jrEqpInfo.getFieldString("WPROG_STAT");          // 설비 상태
					szEqpAutoCrnMode = jrEqpInfo.getFieldString("YD_EQP_AUTO_CRN_MODE");// AutoCrn 상태
					szEqpAutoCrnYN   = jrEqpInfo.getFieldString("YD_EQP_WRK_MODE2"); 	// AutoCrn 여부
					sWORK_MODE       = jrEqpInfo.getFieldString("WORK_MODE");
					
					if ("A".equals(szEqpAutoCrnYN)) {// A:무인
						autoFlag = true; 
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
					sYD_WRK_PROG_STAT = rstCrnSch.getRecord(0).getFieldString("YD_WRK_PROG_STAT");
					sYD_DN_WO_LOC     = rstCrnSch.getRecord(0).getFieldString("YD_DN_WO_LOC");
				}
				
				/*********************************************
				 *  권하지시(권상)상태에서는 스케줄취소 스킵
				 *  권상 전에만 스케줄 취소가 가능함
				 *********************************************/
				if (!"W".equals(sYD_WRK_PROG_STAT)   //대기
				  &&!"S".equals(sYD_WRK_PROG_STAT)   //대기
				  &&!"1".equals(sYD_WRK_PROG_STAT)) {//선택(권상지시)
					//continue;
					throw new Exception("권상한 작업스케줄은 스케줄 취소할 수 없습니다.");
				}
				
				/******************************************
				 * 작업예약은 같되 크레인스케줄이 분리된경우 권상할 경우 취소하지 못함
				 ******************************************/
				/*
				SELECT YD_WRK_PROG_STAT
				  FROM TB_YM_CRNSCH CS
				 WHERE CS.YD_WBOOK_ID = (SELECT WB.YD_WBOOK_ID
				                           FROM TB_YM_WRKBOOK    WB
				                              , TB_YM_WRKBOOKMTL WM
				                          WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
				                            AND WB.DEL_YN = 'N'
				                            AND WM.DEL_YN = 'N'  
				                            AND WM.YD_WBOOK_ID = (SELECT YD_WBOOK_ID
				                                                    FROM TB_YM_CRNSCH
				                                                   WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				                                                     AND DEL_YN = 'N')
				
				                        )
				  AND CS.DEL_YN = 'N'       
				  AND ROWNUM = 1
				 ORDER BY DECODE(YD_WRK_PROG_STAT, 'W', 1, 'S', 2, '1', 3, '2', 4) DESC 
				 */
				JDTORecordSet jrWrkProg = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getWtkProgStatByCrnSchId");
				String sMAX_YD_WRK_PROG_STAT = "";
				String sMAX_YD_CRN_SCH_ID = "";
				if (jrWrkProg.size() > 0) {
					sMAX_YD_WRK_PROG_STAT = jrWrkProg.getRecord(0).getFieldString("YD_WRK_PROG_STAT");
					sMAX_YD_CRN_SCH_ID = jrWrkProg.getRecord(0).getFieldString("YD_CRN_SCH_ID");
					//if ("2".equals(sMAX_YD_WRK_PROG_STAT)) {
					//	throw new Exception("권상한 스케줄을 포함한 작업예약은 스케줄 취소할 수 없습니다.");
					//}
					
					String sIS_TC_LM = "N"; //대차 하차 작업 FLAG (대차하차 작업이면 'Y')
					
					if(sYD_SCH_CD.length() == 8) {
						if("TC".equals(sYD_SCH_CD.substring(2, 4)) && "LM".equals(sYD_SCH_CD.substring(6, 8))) {
							sIS_TC_LM = "Y";
						}
					}
					
					if ("N".equals(sIS_TC_LM)) {
						//대차 하차작업이 아닐 경우
						if ("S".equals(sMAX_YD_WRK_PROG_STAT) || "1".equals(sMAX_YD_WRK_PROG_STAT)) {
							if(!sYD_CRN_SCH_ID.equals(sMAX_YD_CRN_SCH_ID)) {
								throw new Exception("권상지시가 L2로 전송된 스케줄을 포함한 작업예약은 스케줄 취소시 L2로 전송된 크레인스케줄ID를 선택하여 취소하십시요!");
							}
						} else if ("2".equals(sMAX_YD_WRK_PROG_STAT)) {
							throw new Exception("권상한 스케줄을 포함한 작업예약은 스케줄 취소할 수 없습니다.");
						}
					}
				}
				
				commUtils.printLog(logId, "[무인크레인 여부]="+ autoFlag + "[YD_EQP_AUTO_CRN_MODE]="+szEqpAutoCrnMode , "[INFO]");
				commUtils.printLog(logId, "[YD_EQP_WRK_MODE2]="+ szEqpAutoCrnYN + "[YD_WRK_PROG_STAT]="+sYD_WRK_PROG_STAT , "[INFO]");
				

				// 위치검색실패인 경우 유인으로 처리
				if ("XX010101".equals(sYD_DN_WO_LOC)) {
					autoFlag = false;
				} 
				
				
				/*********************************
				 * 무인 크레인 작업일 경우 
				 *********************************/
				if (autoFlag && !"W".equals(sYD_WRK_PROG_STAT)) { //W:명령선택대기
					
					//설비상태 체크하여 Auto일경우 일시정지 상태에서만 가능하게 수정
					//4: 일시정지 B:고장
					if (!"4".equals(szEqpAutoCrnMode) && !"B".equals(szydEqpStat)) { 
						throw new Exception("무인크레인 [" + sYD_EQP_ID + "]이 일시정지이거나 고장상태가 아니면 취소할 수 없습니다.");
					}
					
					commUtils.printLog(logId, "○○○ 스케줄취소 리스트 조회(해당 번지의 하단 전부 스케줄 취소)", "[info]");
					/*
					SELECT CS.YD_WBOOK_ID
					     , MIN(CM.YD_CRN_SCH_ID) AS YD_CRN_SCH_ID
					  FROM TB_YM_STACKLAYER      SL
					     , (SELECT CS.YD_UP_WO_LOC
					             , MIN(CM.STACK_LAYER_GP) OVER() AS STACK_LAYER_GP
					          FROM TB_YM_CRNWRKMTL  CM
					             , TB_YM_CRNSCH     CS
					         WHERE 1 = 1
					           AND CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
					           AND CS.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
					           AND CS.DEL_YN = 'N'
					           AND CM.DEL_YN = 'N'
					           AND ROWNUM = 1
					       ) CNCL
					     , TB_YM_CRNWRKMTL CM
					     , TB_YM_CRNSCH    CS
					 WHERE SL.STACK_COL_GP LIKE '2%'                                                     
					   AND SL.STACK_COL_GP||SL.STACK_BED_GP = CNCL. YD_UP_WO_LOC
					   AND SL.STACK_LAYER_GP < CNCL.STACK_LAYER_GP
					   AND SL.STACK_LAYER_STAT IN('U')
					   AND SL.STOCK_ID = CM.STOCK_ID
					   AND CM.DEL_YN = 'N'
					   AND CS.DEL_YN = 'N'
					   AND CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
					   AND CS.YD_WBOOK_ID  != :V_YD_WBOOK_ID
					 GROUP BY CS.YD_WBOOK_ID
					 ORDER BY YD_CRN_SCH_ID      
					 */
					jrParam.setField("YD_CRN_SCH_ID"	,sYD_CRN_SCH_ID);
					jrParam.setField("YD_WBOOK_ID"		,sYD_WBOOK_ID  );
					JDTORecordSet jrSchCnclList = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSchCnclList", logId, methodNm, "스케줄취소목록");
					

					//크레인 스케줄의 취소 전문 전송
					JDTORecord tcRecord = JDTORecordFactory.getInstance().create();
					tcRecord.setField("YD_CRN_SCH_ID"   , sYD_CRN_SCH_ID);
					tcRecord.setField("MSG_GP"          , "D");

					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L004", tcRecord));
					
					commUtils.printLog(logId, "["+szSessionName+"] 스케줄["+sYD_CRN_SCH_ID+"]을 스케쥴 취소 요청 전송", "S+");
		        	
					/**
					 * 권상위치 하단 스케줄 취소
					 */
					commUtils.printLog(logId, "▷▷▷["+sYD_CRN_SCH_ID+"] 권상위치 하단 스케줄 취소 START = "+jrSchCnclList.size()+" 건", "[info]");
					if (jrSchCnclList.size() > 0) {
						for (int ii = 0; ii < jrSchCnclList.size(); ++ii) {
							JDTORecord jParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
							jParam.setField("YD_WBOOK_ID"	, jrSchCnclList.getRecord(ii).getFieldString("YD_WBOOK_ID"));
							jParam.setField("YD_CRN_SCH_ID"	, jrSchCnclList.getRecord(ii).getFieldString("YD_CRN_SCH_ID"));
							jParam.setField("YD_SCH_CD"   	, jrSchCnclList.getRecord(ii).getFieldString("YD_SCH_CD"));
							jParam.setField("IS_SCH_MTL"	, "N");
							
							jrRtn = commUtils.addSndData(jrRtn, this.trtCrnSchCncl(jParam));
						}
					}
					commUtils.printLog(logId, "▷▷▷["+sYD_CRN_SCH_ID+"] 권상위치 하단 스케줄 취소 END", "[info]");
					
					// 작업대기상태 update : 작업취소와 구분되게 D 로 상태 없데이트 함...
					/*
					UPDATE TB_YM_CRNSCH  
					   SET YD_WRK_PROG_STAT   = :V_YD_WRK_PROG_STAT
					     , YD_L2_REQUEST_STAT = :V_YD_L2_REQUEST_STAT
					     , MODIFIER           = :V_MODIFIER
					     , MOD_DDTT           = SYSDATE
					 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
					 */
					jrParam.setField("YD_WRK_PROG_STAT"  , "S");
					jrParam.setField("YD_L2_REQUEST_STAT", YmConstant.YD_L2_REQUEST_STAT_D);
					jrParam.setField("YD_CRN_SCH_ID"     , sYD_CRN_SCH_ID);
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdCrnSchProgStat");
					
					/**
					 * 스케줄 취소시 작업예약테이블에 스케줄취소 컬럼 UPDATE
					 */
					/*
					UPDATE TB_YM_WRKBOOK
					   SET SCH_CNCL_YN = 'Y'
					     , MODIFIER    = :V_MODIFIER
					     , MOD_DDTT    = SYSDATE
					 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
					 */
					jrParam.setField("YD_WBOOK_ID", sYD_WBOOK_ID);
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.bcoilTcarDistCarldjl.updSchCnclYn");
					
					/**
					 * 무인크레인이면 종료
					 */
					return jrRtn;
					
				} else {
					commUtils.printLog(logId, "○○○ 스케줄취소 리스트 조회(해당 번지의 하단 전부 스케줄 취소)", "[info]");
					/*
					SELECT CS.YD_WBOOK_ID
					     , MIN(CM.YD_CRN_SCH_ID) AS YD_CRN_SCH_ID
					  FROM TB_YM_STACKLAYER      SL
					     , (SELECT CS.YD_UP_WO_LOC
					             , MIN(CM.STACK_LAYER_GP) OVER() AS STACK_LAYER_GP
					          FROM TB_YM_CRNWRKMTL  CM
					             , TB_YM_CRNSCH     CS
					         WHERE 1 = 1
					           AND CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
					           AND CS.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
					           AND CS.DEL_YN = 'N'
					           AND CM.DEL_YN = 'N'
					           AND ROWNUM = 1
					       ) CNCL
					     , TB_YM_CRNWRKMTL CM
					     , TB_YM_CRNSCH    CS
					 WHERE SL.STACK_COL_GP LIKE '2%'                                                     
					   AND SL.STACK_COL_GP||SL.STACK_BED_GP = CNCL. YD_UP_WO_LOC
					   AND SL.STACK_LAYER_GP < CNCL.STACK_LAYER_GP
					   AND SL.STACK_LAYER_STAT IN('U')
					   AND SL.STOCK_ID = CM.STOCK_ID
					   AND CM.DEL_YN = 'N'
					   AND CS.DEL_YN = 'N'
					   AND CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
					   AND CS.YD_WBOOK_ID  != :V_YD_WBOOK_ID
					 GROUP BY CS.YD_WBOOK_ID
					 ORDER BY YD_CRN_SCH_ID      
					 */
					jrParam.setField("YD_CRN_SCH_ID"	,sYD_CRN_SCH_ID);
					jrParam.setField("YD_WBOOK_ID"		,sYD_WBOOK_ID  );
					JDTORecordSet jrSchCnclList = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSchCnclList", logId, methodNm, "스케줄취소목록");
					

					commUtils.printLog(logId, "○○○ 해당 스케줄 취소", "[info]");
					JDTORecord inRecord = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
					inRecord.setField("YD_CRN_SCH_ID"	,sYD_CRN_SCH_ID);
					inRecord.setField("YD_SCH_CD"		,sYD_SCH_CD);
					inRecord.setField("YD_EQP_ID"		,sYD_EQP_ID);
					inRecord.setField("YD_WBOOK_ID"		,sYD_WBOOK_ID);
					
					jrRtn = commUtils.addSndData(jrRtn, this.trtCrnSchCncl(inRecord));
					
					/**
					 * 권상위치 하단 스케줄 취소
					 */
					commUtils.printLog(logId, "▷▷▷["+sYD_CRN_SCH_ID+"] 권상위치 하단 스케줄 취소 START = "+jrSchCnclList.size()+" 건", "[info]");
					if (jrSchCnclList.size() > 0) {
						for (int ii = 0; ii < jrSchCnclList.size(); ++ii) {
							JDTORecord jParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
							jParam.setField("YD_WBOOK_ID"	, jrSchCnclList.getRecord(ii).getFieldString("YD_WBOOK_ID"));
							jParam.setField("YD_CRN_SCH_ID"	, jrSchCnclList.getRecord(ii).getFieldString("YD_CRN_SCH_ID"));
							jParam.setField("YD_SCH_CD"   	, jrSchCnclList.getRecord(ii).getFieldString("YD_SCH_CD"));
							jParam.setField("IS_SCH_MTL"	, "N");
							
							jrRtn = commUtils.addSndData(jrRtn, this.trtCrnSchCncl(jParam));
						}
					}
					commUtils.printLog(logId, "▷▷▷["+sYD_CRN_SCH_ID+"] 권상위치 하단 스케줄 취소 END", "[info]");
					
					/**
					 * 스케줄 취소시 작업예약테이블에 스케줄취소 컬럼 UPDATE
					 */
					/*
					UPDATE TB_YM_WRKBOOK
					   SET SCH_CNCL_YN = 'Y'
					     , MODIFIER    = :V_MODIFIER
					     , MOD_DDTT    = SYSDATE
					 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
					 */
					jrParam.setField("YD_WBOOK_ID", sYD_WBOOK_ID);
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.bcoilTcarDistCarldjl.updSchCnclYn");
					
					
					/****************************
					 * 스케줄 재기동
					 ****************************/
					if ("RS".equals(sTRT_DTL_GP)) {
						
						if (sYD_TO_LOC_GUIDE.length() == 8) {
							/*
							SELECT COUNT(*) OVER() AS CNT_STK_ALBE_LOC
							     , SL.*
							  FROM TB_YM_STACKLAYER   SL
							 WHERE STACK_COL_GP||STACK_BED_GP LIKE :V_YD_TO_LOC_GUIDE||'%'
							   AND STACK_LAYER_STAT = 'E'
							   AND STACK_LAYER_ACTIVE_STAT IN ('E', 'L')
							 */
							jrParam.setField("YD_TO_LOC_GUIDE", sYD_TO_LOC_GUIDE);
							JDTORecordSet jrAbleLoc = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getCntStkAlbeLoc");
							
							if (jrAbleLoc.size() == 0) {
								throw new Exception("TO위치가이드로 지정된 번지가 닫혀있습니다.");
							}
						}
						
						commUtils.printLog(logId, "○○○ 해당 스케줄 재기동", "[info]");
						JDTORecord jParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
						jParam.setResultCode(logId);	//Log ID
						jParam.setResultMsg(methodNm);	//Log Method Name
	
						jParam.setField("JMS_TC_CD", "YMYMJ202"); //야드작업예약ID
						jParam.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시				
						
						jParam.setField("YD_WBOOK_ID"	, sYD_WBOOK_ID);
						jParam.setField("YD_SCH_CD"  , ""); //야드스케쥴코드
						jParam.setField("YD_EQP_ID"  , ""); //야드설비ID
						
						//크레인스케줄기동 전문
						EJBConnector sndConnD = new EJBConnector("default", "BSlabSchSeEJB", this);
						JDTORecord jrRtnD = (JDTORecord)sndConnD.trx("procYMYMJ202", new Class[] { JDTORecord.class }, new Object[] { jParam });
						jrRtn = commUtils.addSndData(jrRtn, jrRtnD);
						
						/**
						 * 하단 스케줄 취소대상 스케줄 재기동
						 */
						for (int ii = 0; ii < jrSchCnclList.size(); ++ii) {
							JDTORecord jParamD = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
							jParamD.setResultCode(logId);	//Log ID
							jParamD.setResultMsg(methodNm);	//Log Method Name
		
							jParamD.setField("JMS_TC_CD", "YMYMJ202"); //야드작업예약ID
							jParamD.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시				
							
							jParamD.setField("YD_WBOOK_ID"	, jrSchCnclList.getRecord(ii).getFieldString("YD_WBOOK_ID"));
							jParamD.setField("YD_SCH_CD"  , ""); //야드스케쥴코드
							jParamD.setField("YD_EQP_ID"  , ""); //야드설비ID
							
							//크레인스케줄기동 전문
							EJBConnector sndConnSt = new EJBConnector("default", "BSlabSchSeEJB", this);
							JDTORecord jrRtnSt = (JDTORecord)sndConnSt.trx("procYMYMJ202", new Class[] { JDTORecord.class }, new Object[] { jParamD });
							jrRtn = commUtils.addSndData(jrRtn, jrRtnSt);
						}
						
					} //end RS
					
				} // end if
				
				/***************************************************
				 * 해당 크레인의 마지막 작업일 때 스케줄 취소만 시키고 명령 선택은 하지 않는다.
				 ****************************************************/
				/*
				SELECT *
				  FROM TB_YM_WRKBOOK
				 WHERE DEL_YN = 'N'
				   AND YD_GP  = :V_YD_GP
				   AND YD_WRK_PLAN_CRN LIKE :V_YD_EQP_ID||'%'
				 */
				jrParam.setField("YD_GP"     , sYD_GP    );
				jrParam.setField("YD_EQP_ID" , sYD_EQP_ID);
				JDTORecordSet rsWrkBookList = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getWrkBookCntByEqpId");
				
				if (rsWrkBookList.size() == 1) {
					commUtils.printLog(logId, "○○○ 해당 스케줄의 마지막 작업 취소 -> 명령 선택 안함", "[info]");
					return jrRtn;
				}
				
			} // end for
			
			/*********************************************
			 * 해당 크레인 스케줄이 없으면 종료
			 *********************************************/
			/*
			SELECT CS.*
			  FROM TB_YM_CRNSCH    CS
			     , TB_YM_CRNWRKMTL CM
			 WHERE CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
			   AND CS.DEL_YN = 'N'
			   AND CM.DEL_YN = 'N'
			   AND CS.YD_EQP_ID = :V_YD_EQP_ID
			 */
			jrParam.setField("YD_EQP_ID" , sYD_EQP_ID);
			JDTORecordSet jrSchInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnSchByEqpId");
			if (jrSchInfo.size() == 0) {
				return jrRtn;
			}
			
			/************************************
			 * 스케줄 취소시 명령 선택 기동
			 ************************************/
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
				jrEqpInfo   = rsResult.getRecord();
				szydEqpStat = jrEqpInfo.getFieldString("WPROG_STAT");          // 설비 상태
			}
			
			// 스케줄취소시 설비가 offline, 고장이 아니고 대기 일때  명령 선택
			if (!"B".equals(szydEqpStat) && !"2".equals(sWORK_MODE) && "W".equals(szydEqpStat)) { 

					JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, "A8YML007");

					jrYdMsg.setField("JMS_TC_CD"       , YmConstant.A8YML007);	//크레인작업지시요구
					jrYdMsg.setField("YD_EQP_ID"       , sYD_EQP_ID    );	//야드설비ID
					jrYdMsg.setField("YD_WRK_PROG_STAT", "4"           );	//야드작업진행상태(권하완료)
					jrYdMsg.setField("YD_SCH_CD"       , sYD_SCH_CD    );	//야드스케쥴코드
					jrYdMsg.setField("YD_CRN_SCH_ID"   , sYD_CRN_SCH_ID);	//야드크레인스케쥴ID

					EJBConnector sndConn = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
					JDTORecord jrRtn1 = (JDTORecord)sndConn.trx("rcvA8YML007", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
					
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
		String methodNm = "크레인작업요구현황조회 스케줄재전송[BSlabJspSeEJB.reSndCrnSch] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn = null;
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
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
				JDTORecordSet jsCrnResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYmCrnsch", logId, methodNm, "크레인스케쥴 검색");
				
				if (jsCrnResult.size() <= 0) {
					throw new Exception("해당 스케줄크레인스케줄 ID  정보: ["+ commUtils.getValue(gdReq, "YD_CRN_SCH_ID", i) + "] 가 존재하지않습니다");
				}
				
               // TO위치 재 점검
				
				String ydDnWrLoc   = jsCrnResult.getRecord(0).getFieldString("YD_DN_WO_LOC");
				
				if(!"XX010101".equals(ydDnWrLoc)) {
					/*****************************************************
					 **  단이상 여부 CHECK
					 *   정상이면 재송신
					 *   이상이면 LAYER TABLE 변경
					 *   공통 UPDATE      
					 *****************************************************/					
					/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getDanErrChk 
					WITH LOC_DAN AS (
					SELECT A.*
					     --권하할 재료 단 위치
					     , (SELECT STACK_LAYER_GP 
					          FROM TB_YM_STACKLAYER 
					         WHERE STACK_COL_GP = :V_STACK_COL_GP
					           AND STACK_BED_GP = :V_STACK_BED_GP 
					           AND STOCK_ID = A.STOCK_ID
					       )  AS LOC_STACK_LAYER_GP     
					 FROM
					       ( SELECT B.STOCK_ID
					              , A.YD_EQP_WRK_SH
					           FROM TB_YM_CRNSCH A
					              , TB_YM_CRNWRKMTL B
					          WHERE A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID
					            AND A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID 
					            AND A.DEL_YN = 'N'
					            AND B.DEL_YN = 'N'
					          ORDER BY STACK_LAYER_GP 
					       ) A
					 WHERE ROWNUM <= 1
					)
					SELECT * 
					  FROM (SELECT CASE WHEN 
					                    -- 재료매수와 하단 권하할 재료의 단-1 동일여부  
					                    SUM(CASE WHEN A.STOCK_ID IS NOT NULL THEN 1 ELSE 0 END) OVER() = B.LOC_STACK_LAYER_GP -1 THEN 'N'
					                    ELSE 'Y' END DAN_ERR_YN
					             , B.LOC_STACK_LAYER_GP   
					             , B.YD_EQP_WRK_SH
					             , TO_NUMBER(B.LOC_STACK_LAYER_GP) + TO_NUMBER(B.YD_EQP_WRK_SH) - 1 AS WRK_MAX_DAN
					          FROM TB_YM_STACKLAYER A
					             , LOC_DAN B
					         WHERE A.STACK_COL_GP = :V_STACK_COL_GP
					           AND A.STACK_BED_GP = :V_STACK_BED_GP
					           AND A.STACK_LAYER_GP < B.LOC_STACK_LAYER_GP
					       )
					 WHERE ROWNUM <= 1		
					 */	
					jrParam.setField("STACK_COL_GP" , ydDnWrLoc.substring(0,6)); 
					jrParam.setField("STACK_BED_GP" , ydDnWrLoc.substring(6,8)); 
				
					JDTORecordSet jsDanErr = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getDanErrChk", logId, methodNm, "단 이상여부 검색");	
					if (jsDanErr.size() <= 0) {
						throw new Exception("해당 스케줄에 권하위치 정보확인.");
					}
					String sDanErrYn   = jsDanErr.getRecord(0).getFieldString("DAN_ERR_YN");
					String sWrkMaxDan  = jsDanErr.getRecord(0).getFieldString("WRK_MAX_DAN");
					String sWrkMinDan  = jsDanErr.getRecord(0).getFieldString("WRK_MIN_DAN");
					
					if(sDanErrYn.equals("Y")) {
						//ERROR 인 경우
						
						/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStackLayerZip
						MERGE INTO TB_YM_STACKLAYER SL USING (

						WITH TB_MAX_DAN AS (
						    SELECT MAX(STACK_LAYER_GP) AS MAX_DAN
						      FROM TB_YM_STACKLAYER 
						     WHERE STACK_COL_GP = :V_STACK_COL_GP
						       AND STACK_BED_GP = :V_STACK_BED_GP 
						       AND STOCK_ID IS NOT NULL
						)
						SELECT A.STACK_COL_GP
						     , A.STACK_BED_GP
						     , A.CHG_STACK_LAYER_GP
						     , CASE WHEN B.STOCK_ID IS NOT NULL 
						            THEN B.STOCK_ID                ELSE A.STOCK_ID                END AS STOCK_ID
						     , CASE WHEN B.STOCK_ID IS NOT NULL 
						            THEN B.STACK_LAYER_ACTIVE_STAT ELSE A.STACK_LAYER_ACTIVE_STAT END AS STACK_LAYER_ACTIVE_STAT
						     , CASE WHEN B.STOCK_ID IS NOT NULL 
						            THEN B.STACK_LAYER_STAT        ELSE A.STACK_LAYER_STAT        END AS STACK_LAYER_STAT
						  FROM
						        (
						        SELECT STACK_COL_GP
						             , STACK_BED_GP
						             , STACK_LAYER_GP
						             , ''  AS STOCK_ID
						             , 'E' AS STACK_LAYER_ACTIVE_STAT
						             , 'E' AS STACK_LAYER_STAT
						             , LPAD(ROWNUM,'2','0')  AS CHG_STACK_LAYER_GP
						          FROM TB_YM_STACKLAYER A
						             , TB_MAX_DAN B
						         WHERE STACK_COL_GP = :V_STACK_COL_GP --'2A0102'  
						           AND STACK_BED_GP = :V_STACK_BED_GP --'02'
						           AND TO_NUMBER(STACK_LAYER_GP) <= B.MAX_DAN   --5 
						        ) A,
						        (
						        SELECT STACK_COL_GP
						             , STACK_BED_GP
						             , STACK_LAYER_GP
						             , STOCK_ID
						             , STACK_LAYER_ACTIVE_STAT
						             , STACK_LAYER_STAT
						             , LPAD(ROWNUM,'2','0')  AS CHG_STACK_LAYER_GP
						          FROM TB_YM_STACKLAYER A
						             , TB_MAX_DAN B
						         WHERE STACK_COL_GP = :V_STACK_COL_GP --'2A0102'  
						           AND STACK_BED_GP = :V_STACK_BED_GP --'02'
						           AND STOCK_ID IS NOT NULL
						           AND TO_NUMBER(STACK_LAYER_GP) <= B.MAX_DAN    

						        ) B
						 WHERE A.STACK_COL_GP = B.STACK_COL_GP(+)
						   AND A.STACK_BED_GP = B.STACK_BED_GP(+)
						   AND A.CHG_STACK_LAYER_GP = B.CHG_STACK_LAYER_GP(+)
						 ORDER BY A.CHG_STACK_LAYER_GP 
						 
						) DD ON (SL.STACK_COL_GP = DD.STACK_COL_GP AND SL.STACK_BED_GP = DD.STACK_BED_GP AND SL.STACK_LAYER_GP = DD.CHG_STACK_LAYER_GP) 
						WHEN MATCHED THEN UPDATE SET
						         SL.MODIFIER         = :V_MODIFIER
						        ,SL.MOD_DDTT         = SYSDATE
						        ,SL.STOCK_ID         = DD.STOCK_ID
						        ,SL.STACK_LAYER_STAT = DD.STACK_LAYER_STAT 
						
						*/
						jrParam.setField("WRK_MAX_DAN" , sWrkMaxDan); 
						commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStackLayerZip", logId, methodNm, "단정보 압축");
						
						/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updCarSchDnDan 
						UPDATE TB_YM_CRNSCH 
						   SET MODIFIER    = :V_MODIFIER
						     , MOD_DDTT    = SYSDATE
						     , YD_DN_WO_LAYER  = :V_WRK_MIN_DAN
						 WHERE A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID 
						   AND A.DEL_YN = 'N'
						*/   
						jrParam.setField("WRK_MIN_DAN" , sWrkMinDan); 
						commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updCarSchDnDan", logId, methodNm, "크레인 스케쥴 단 갱신");
						
						/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getCrnWrkMtlChangStock 
						SELECT A.STOCK_ID         -- 기존 재료정보 
						     , A.STACK_COL_GP
						     , A.STACK_BED_GP
						     , A.STACK_LAYER_GP
						     , A.STACK_COL_GP|| A.STACK_BED_GP || A.STACK_LAYER_GP AS TO_LOC
						  FROM TB_YM_STACKLAYER A
						 WHERE A.STACK_COL_GP = :V_STACK_COL_GP
						   AND A.STACK_BED_GP = :V_STACK_BED_GP
						   AND A.STOCK_ID IS NOT NULL
						   AND A.STOCK_ID NOT IN (SELECT STOCK_ID FROM TB_YM_CRNWRKMTL WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID)
						 ORDER BY A.STACK_LAYER_GP
						*/
						JDTORecordSet jsCrnMtlResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getCrnWrkMtlChangStock", logId, methodNm, "공통 갱신 대상 검색");
						
						if (jsCrnMtlResult.size() <= 0) {
							throw new Exception("해당 스케줄에 해당되는 크레인 작업 재료가 없습니다");
						} else {
							JDTORecord recOutTemp = JDTORecordFactory.getInstance().create();
							
							for(int j = 0; j < jsCrnMtlResult.size(); j++) {

						    	recOutTemp = JDTORecordFactory.getInstance().create();
						    	recOutTemp.setRecord(jsCrnMtlResult.getRecord(j));
								jrParam.setField("STOCK_ID"   , commUtils.trim(recOutTemp.getFieldString("STOCK_ID")));
								jrParam.setField("YD_LOC"     , commUtils.trim(recOutTemp.getFieldString("TO_LOC")));
								EJBConnector ejbConn1 = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
								ejbConn1.trx("UpdSlabComLoc", new Class[] { JDTORecord.class }, new Object[] { jrParam }); 			
							}
						}
					}
				}
				/*****************************************************
				 **  크레인스케줄 재전송
				 *****************************************************/
				//크레인 스케줄의 취소 전문 전송
				JDTORecord tcRecord = JDTORecordFactory.getInstance().create();
				tcRecord.setField("YD_CRN_SCH_ID"   , sYD_CRN_SCH_ID);
				tcRecord.setField("MSG_GP"          , "R");

//SJH				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L004", tcRecord));
        		JDTORecordSet jsRtn1 = commDao.getMsgL2("YMA8L004", tcRecord);
				jrRtn = commUtils.addSndData(jrRtn , jsRtn1);
				//크레인작업지시 Z값 갱신 처리
        		if(jsRtn1.size() > 0 ) {
        			
        			tcRecord.setField("YD_UP_WO_LOC_ZAXIS"   , jsRtn1.getRecord(0).getFieldString("YD_UP_WO_LOC_ZAXIS"));	
        			tcRecord.setField("YD_DN_WO_LOC_ZAXIS"   , jsRtn1.getRecord(0).getFieldString("YD_DN_WO_LOC_ZAXIS"));	
        			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updZAXIS 
    				--크레인스케줄 z값 갱신
    				UPDATE TB_YM_CRNSCH
    				   SET YD_UP_WO_LOC_ZAXIS = :V_YD_UP_WO_LOC_ZAXIS
    				     , YD_DN_WO_LOC_ZAXIS = :V_YD_DN_WO_LOC_ZAXIS
    				 WHERE YD_CRN_SCH_ID    = :V_YD_CRN_SCH_ID
    				   AND DEL_YN           = 'N' 
    				*/
            		commDao.update(tcRecord, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updZAXIS", logId, methodNm, "SLAB크레인스케줄 Z값 수정");
        			
        			
//        			EJBConnector ejbConn1 = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
//					ejbConn1.trx("procCrnsSchZaxis", new Class[] { JDTORecord.class }, new Object[] { tcRecord }); 
        		}
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
		String methodNm = "크레인작업관리 상태 설정변경[BSlabJspSeEJB.updbtCrnStsSetPp] < " + gdReq.getNavigateValue();
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
			
			/****************************************************
			 * 야드작업진행상태  JAVA단에서 한번 더 체크 TODO
			 ****************************************************/
			/*
			 SELECT *
			   FROM TB_YM_CRNSCH
			  WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID 
			 */
			JDTORecordSet rstCrnSch = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYmCrnsch");
			String sYD_WRK_PROG_STAT = "";
			
			if (rstCrnSch == null || rstCrnSch.size() <= 0) {
				if ("WU".equals(trtDtlGp) || "WD".equals(trtDtlGp) || "DL".equals(trtDtlGp) || "XX".equals(trtDtlGp)) {
					throw new Exception("크레인스케줄이 없습니다.");
				}
			} else {
				sYD_WRK_PROG_STAT = rstCrnSch.getRecord(0).getFieldString("YD_WRK_PROG_STAT");
			}
			
			if ("ST".equals(trtDtlGp)) {
				//설비상태 변경
				jrParam.setField("JMS_TC_CD"          , "A8YML004"); //설비고장복구실적
				jrParam.setField("YD_EQP_STAT"        , commUtils.trim(gdReq.getParam("YD_EQP_STAT"))); //야드설비상태(B:고장, N:정상)
				jrParam.setField("YD_EQP_PAUSE_CODE"  , "0000"    ); //야드설비휴지코드
				jrParam.setField("YD_EQP_TRBL_RCVR_DT", currDate  ); //야드설비고장복구일시

				EJBConnector sndConn = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvA8YML004", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				
			} else if ("MD".equals(trtDtlGp)) {
				//작업Mode 변경
				jrParam.setField("JMS_TC_CD"      , "A8YML003"); //설비운전모드전환
				jrParam.setField("YD_EQP_WRK_MODE", commUtils.trim(gdReq.getParam("YD_EQP_WRK_MODE"))); //야드설비작업Mode(1:On-Line, 0:Off-Line)

				EJBConnector sndConn = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvA8YML003", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				
			} else if ("WO".equals(trtDtlGp)) {
				//명령선택기동
				jrParam.setField("JMS_TC_CD"       , "A8YML007"); //크레인작업지시요구
				jrParam.setField("YD_WRK_PROG_STAT", "W"       ); //야드작업진행상태(명령선택대기)
				jrParam.setField("YD_SCH_CD"       , commUtils.trim(gdReq.getHeader("YD_SCH_CD"     ).getValue(0))); //야드스케쥴코드
				jrParam.setField("YD_CRN_SCH_ID"   , commUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID" ).getValue(0))); //야드크레인스케쥴ID

				EJBConnector sndConn = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvA8YML007", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			} else if ("WU".equals(trtDtlGp)) {
				if (!"1".equals(sYD_WRK_PROG_STAT)) {
					throw new Exception("["+sYD_WRK_PROG_STAT+"]해당 작업진행상태는 권상지시 상태가 아닙니다.");
				}
				//권상실적처리
				jrParam.setField("JMS_TC_CD"       , "A8YML008"); //크레인권상실적
				jrParam.setField("YD_EQP_WRK_MODE" , "9"       ); //야드설비작업Mode(Backup)
				jrParam.setField("YD_WRK_PROG_STAT", "2"       ); //야드작업진행상태(권상완료)
				jrParam.setField("YD_SCH_CD"       , commUtils.trim(gdReq.getHeader("YD_SCH_CD"         ).getValue(0))); //야드스케쥴코드
				jrParam.setField("YD_CRN_SCH_ID"   , commUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID"     ).getValue(0))); //야드크레인스케쥴ID
				jrParam.setField("YD_UP_WR_LOC"    , commUtils.trim(gdReq.getHeader("YD_UP_WO_LOC"      ).getValue(0))); //야드권상실적위치
				jrParam.setField("YD_UP_WR_LAYER"  , commUtils.trim(gdReq.getHeader("YD_UP_WO_LAYER"    ).getValue(0))); //야드권상실적단
				jrParam.setField("YD_CRN_XAXIS"    , commUtils.trim(gdReq.getHeader("YD_UP_WO_LOC_XAXIS").getValue(0))); //야드크레인X축
				jrParam.setField("YD_CRN_YAXIS"    , commUtils.trim(gdReq.getHeader("YD_UP_WO_LOC_YAXIS").getValue(0))); //야드크레인Y축
				jrParam.setField("YD_CRN_ZAXIS"    , commUtils.trim(gdReq.getHeader("YD_UP_WO_LOC_ZAXIS").getValue(0))); //야드크레인Z축
				
				EJBConnector sndConn = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvA8YML008", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			} else if ("WD".equals(trtDtlGp)) {
				if (!"2".equals(sYD_WRK_PROG_STAT)) {
					throw new Exception("["+sYD_WRK_PROG_STAT+"]해당 작업진행상태는 권상완료 상태가 아닙니다.");
				}
				//권하실적처리
				jrParam.setField("JMS_TC_CD"       , "A8YML009"); //크레인권하실적
				jrParam.setField("YD_EQP_WRK_MODE" , "9"       ); //야드설비작업Mode(Backup)
				jrParam.setField("YD_WRK_PROG_STAT", "4"       ); //야드작업진행상태(권하완료)
				jrParam.setField("YD_SCH_CD"       , commUtils.trim(gdReq.getHeader("YD_SCH_CD"         ).getValue(0))); //야드스케쥴코드
				jrParam.setField("YD_CRN_SCH_ID"   , commUtils.trim(gdReq.getHeader("YD_CRN_SCH_ID"     ).getValue(0))); //야드크레인스케쥴ID
				jrParam.setField("YD_DN_WR_LOC"    , commUtils.trim(gdReq.getHeader("YD_DN_WO_LOC"      ).getValue(0))); //야드권하실적위치
				jrParam.setField("YD_DN_WR_LAYER"  , commUtils.trim(gdReq.getHeader("YD_DN_WO_LAYER"    ).getValue(0))); //야드권하실적단
				jrParam.setField("YD_CRN_XAXIS"    , commUtils.trim(gdReq.getHeader("YD_DN_WO_LOC_XAXIS").getValue(0))); //야드크레인X축
				jrParam.setField("YD_CRN_YAXIS"    , commUtils.trim(gdReq.getHeader("YD_DN_WO_LOC_YAXIS").getValue(0))); //야드크레인Y축
				jrParam.setField("YD_CRN_ZAXIS"    , commUtils.trim(gdReq.getHeader("YD_DN_WO_LOC_ZAXIS").getValue(0))); //야드크레인Z축
				EJBConnector sndConn = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
				JDTORecord rst = (JDTORecord)sndConn.trx("rcvA8YML009", new Class[] { JDTORecord.class }, new Object[] { jrParam });
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
				jrParam.setField("JMS_TC_CD"      , "A8YML003"); //운전모드전환
				jrParam.setField("YD_EQP_WRK_MODE" , commUtils.trim(gdReq.getParam("YD_EQP_WRK_MODE"))); //야드설비작업Mode(1:On-Line, 2:Off-Line)
				jrParam.setField("YD_EQP_WRK_MODE2", commUtils.trim(gdReq.getParam("YD_EQP_WRK_MODE2"))); //

				EJBConnector sndConn = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvA8YML003", new Class[] { JDTORecord.class }, new Object[] { jrParam });

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
					jrParam.setField("JMS_TC_CD"       , "YMA8L005"); //작업실적응답
					jrParam.setField("YD_EQP_ID"       , ydEqpId); //야드설비ID
					jrParam.setField("YD_WRK_PROG_STAT", YmConstant.YD_EQP_STAT_UP_CMPL);
					jrParam.setField("YD_SCH_CD"       , sYD_SCH_CD);
					jrParam.setField("YD_CRN_SCH_ID"   , sYD_CRN_SCH_ID);
					jrParam.setField("YD_L2_WR_GP"     , YmConstant.CRN_WRK_RE_LD_WR); //야드L2실적구분(지시요구)
					jrParam.setField("YD_L3_HD_RS_CD"  , YmConstant.CRN_WRK_RE_CD_NORMAL_HD); //야드L3처리결과코드(Error)
					jrParam.setField("YD_L3_MSG"       , "작업실적응답" ); //야드L3MESSAGE

					jrRtn = commUtils.addSndData(jrRtn, bSlabComm.getYMA8L005(jrParam));
				} else {
					jrParam.setResultCode(logId);	//Log ID
					jrParam.setResultMsg(methodNm);	//Log Method Name
					jrParam.setField("JMS_TC_CD"       , "YMA8L005"); //작업실적응답
					jrParam.setField("YD_EQP_ID"       , ydEqpId); //야드설비ID
					jrParam.setField("YD_WRK_PROG_STAT", YmConstant.YD_EQP_STAT_DN_CMPL);
					jrParam.setField("YD_SCH_CD"       , sYD_SCH_CD);
					jrParam.setField("YD_CRN_SCH_ID"   , sYD_CRN_SCH_ID);
					jrParam.setField("YD_L2_WR_GP"     , YmConstant.CRN_WRK_RE_DN_WR    ); //야드L2실적구분(지시요구)
					jrParam.setField("YD_L3_HD_RS_CD"  , YmConstant.CRN_WRK_RE_CD_NORMAL_HD); //야드L3처리결과코드(Error)
					jrParam.setField("YD_L3_MSG"       , "작업실적응답" ); //야드L3MESSAGE

					jrRtn = commUtils.addSndData(jrRtn, bSlabComm.getYMA8L005(jrParam));
				}
				
			} else if ("XX".equals(trtDtlGp)) {
				if (!"S".equals(sYD_WRK_PROG_STAT)) {
					throw new Exception("["+sYD_WRK_PROG_STAT+"]해당 작업진행상태는 응답대기 상태가 아닙니다.");
				}
				//응답 백업(개발용)
				jrParam.setField("MSG_ID"	        , "A8YML015" );
				jrParam.setField("MSG_GP"		    , "I" );
				jrParam.setField("YD_EQP_ID"		, ydEqpId );
				jrParam.setField("YD_WRK_PROG_STAT"	, "1" );
				jrParam.setField("YD_SCH_CD"		, sYD_SCH_CD );
				jrParam.setField("YD_CRN_SCH_ID"	, sYD_CRN_SCH_ID );
				jrParam.setField("REQ_YN"	        , "Y" );
				
				EJBConnector ejbConn = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
				jrRtn = (JDTORecord)ejbConn.trx("rcvA8YML015", new Class[] { JDTORecord.class }, new Object[] { jrParam });
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
		String methodNm = "크레인스케줄 권하지시위치 변경[BSlabJspSeEJB.updCrnSchDnWoLoc] < " + rcvMsg.getResultMsg();
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
			String sSTACK_LAYER_GP = "";
			if (sYD_DN_WO_LOC.length() == 10) {
				sSTACK_LAYER_GP = sYD_DN_WO_LOC.substring(8, 10);
			}
			 //야드권하지시위치
			String ydDnWoLocOld   = ""; //야드권하지시위치(기존)
			String ydDnWoLayerOld = ""; //야드권하지시위치(기존)
			String ydDnWoLayer    = ""; //야드권하지시단(신규)
			String ydDnWoLocXaxis = ""; //야드권하지시X축(신규)
			String ydDnWoLocYaxis = ""; //야드권하지시Y축(신규)
			String ydDnWoLocZaxis = ""; //야드권하지시Z축(신규)
			String sSTACK_LAYER_GP_SCH = "";
			
			JDTORecordSet jsCrnSch = JDTORecordFactory.getInstance().createRecordSet("Temp");
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, sMODIFIER);
			
			
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
			jrParam.setField("STACK_BED_GP"       	, sSTACK_BED_GP);
			
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
			
			if ("XX010101".equals(sYD_DN_WO_LOC_ORG)) {
				// 위치검색실패인 경우 유인으로 처리
				autoFlag = false;
			} 
			
			// 일시정지-권하위치변경 적용여부
			String sAPP030 = ymComm.BCoilApplyYn("APP030","2","S5");
			
			if (autoFlag && !"Y".equals(sAPP030)) { 
			
				if (autoFlag && !"W".equals(sYD_WRK_PROG_STAT)){ 
					//W:명령선택대기 S:스케줄작성중 1:권상
					
					//설비상태 체크하여 Auto일경우 일시정지 상태에서만 가능하게 수정
					if (!"4".equals(sYD_EQP_AUTO_CRN_MODE) && !"B".equals(sWPROG_STAT)) { //4: 일시정지 B:고장
						if ("R".equals(sYD_EQP_WRK_MODE2)) {
							//리모컨이거나 위치검색 실패면 권하위치 변경이 가능함
						} else {
							throw new Exception("무인크레인 [" + sYD_EQP_ID + "]이 일시정지이거나 고장상태가 아니면 권하위치를 변경할 수 없습니다.");
						}
					}
				}
			}
			
			/********************************************
			 * 적치위치 변경에 따른 위치 정합성 체크
			 *******************************************/
			jrParam.setField("YD_CRN_SCH_ID"       	, sYD_CRN_SCH_ID);
			jrParam.setField("STOCK_ID"      , sSTOCK_ID);
			jrParam.setField("STACK_COL_GP"  , sSTACK_COL_GP);
			jrParam.setField("STACK_BED_GP"  , sSTACK_BED_GP);
			jrParam.setField("STACK_LAYER_GP", sSTACK_LAYER_GP);
			
			/*
			SELECT STACK_COL_GP  
			     , STACK_BED_GP  
			     , STACK_LAYER_GP
			     , YD_DN_WO_LOC_XAXIS
			     , YD_DN_WO_LOC_YAXIS
			     , YD_DN_WO_LOC_ZAXIS
			     , YD_DN_WO_LOC_OLD
			     , YD_DN_WO_LAYER_OLD
			     , (SELECT CASE WHEN SUM(DECODE(STACK_LAYER_STAT,'U',1,0)) > 0 THEN 'UP' --권상대기 있음
			                    WHEN SUM(DECODE(STACK_LAYER_STAT,'D',1,0)) > 0 THEN 'DW' --권하대기 있음
			                    WHEN SUM(DECODE(STACK_LAYER_STAT,'E',1,0)) > 0 THEN 'ET' --아랫단 공백있음
			               ELSE '' END 
			          FROM TB_YM_STACKLAYER 
			         WHERE STACK_COL_GP = A.STACK_COL_GP 
			           AND STACK_BED_GP = A.STACK_BED_GP      
			           AND STACK_LAYER_GP < A.STACK_LAYER_GP   
			       ) AS DL_LOC_CHK_RST  
			     , YD_STK_BED_XAXIS_TOL
			     , YD_STK_BED_YAXIS_TOL
			     , YD_STK_BED_ZAXIS_TOL     
			     , (SELECT CASE WHEN SUM(DECODE(STACK_LAYER_STAT,'U',1,0)) > 0 THEN 'UP'
			                    WHEN SUM(DECODE(STACK_LAYER_STAT,'D',1,0)) > 0 THEN 'DW' --권하대기 있음
			               ELSE '' END 
			          FROM TB_YM_STACKLAYER
			         WHERE STACK_COL_GP||STACK_BED_GP = A.YD_DN_WO_LOC_OLD 
			           AND STACK_LAYER_GP > A.YD_DN_WO_LAYER_OLD   
			       ) AS OLD_LOC_CHK_RST
			  FROM
			       (  
			        SELECT A.STACK_COL_GP  
			             , A.STACK_BED_GP  
			             , B.STACK_LAYER_GP
			             , MIN(A.YD_STK_BED_XAXIS_TOL)          AS YD_STK_BED_XAXIS_TOL
			             , MIN(A.YD_STK_BED_YAXIS_TOL)          AS YD_STK_BED_YAXIS_TOL
			             , MIN(A.YD_STK_BED_ZAXIS_TOL)          AS YD_STK_BED_ZAXIS_TOL
			             , MIN(B.STACK_LAYER_X_AXIS)            AS YD_DN_WO_LOC_XAXIS
			             , MIN(B.STACK_LAYER_Y_AXIS)            AS YD_DN_WO_LOC_YAXIS
			             , MIN(B.STACK_LAYER_Z_AXIS)            AS YD_DN_WO_LOC_ZAXIS
			             , MIN(CM.YD_DN_WO_LOC)                 AS YD_DN_WO_LOC_OLD
			             , MIN(CM.YD_DN_WO_LAYER)               AS YD_DN_WO_LAYER_OLD
			          FROM TB_YM_STACKER A
			             , TB_YM_STACKLAYER B
			             , (SELECT CM.YD_DN_WO_LOC
			                      ,CM.YD_DN_WO_LAYER 
			                      ,CM.YD_MTL_SH
			                      ,CM.YD_MTL_WT
			                      ,CM.YD_MTL_T
			                  FROM (SELECT CS.YD_CRN_SCH_ID
			                              ,MIN(CS.YD_WBOOK_ID   ) AS YD_WBOOK_ID
			                              ,MIN(CS.YD_DN_WO_LOC  ) AS YD_DN_WO_LOC
			                              ,MIN(CS.YD_DN_WO_LAYER) AS YD_DN_WO_LAYER
			                              ,COUNT(*)               AS YD_MTL_SH
			                              ,SUM(CC.SLAB_WT  )      AS YD_MTL_WT
			                              ,SUM(CC.SLAB_T   )      AS YD_MTL_T
			                          FROM TB_YM_CRNSCH    CS
			                              ,TB_YM_CRNWRKMTL CM
			                              ,TB_YM_STOCK     ST
			                              ,VW_YD_SLABCOMM  CC
			                         WHERE CS.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			                           AND CM.YD_CRN_SCH_ID = CS.YD_CRN_SCH_ID
			                           AND CM.STOCK_ID = ST.STOCK_ID
			                           AND CM.STOCK_ID = CC.SLAB_NO
			                           AND CM.DEL_YN = 'N'
			                         GROUP BY CS.YD_CRN_SCH_ID) CM) CM
			         WHERE A.STACK_COL_GP  = SUBSTR(:V_STACK_COL_GP, 1, 6)
			           AND A.STACK_COL_GP  = B.STACK_COL_GP
			           AND A.STACK_BED_GP  = B.STACK_BED_GP
			           AND A.STACK_BED_GP   = :V_STACK_BED_GP
			           AND B.STACK_LAYER_GP = NVL(:V_STACK_LAYER_GP, (SELECT MIN(STACK_LAYER_GP) 
			                                                           FROM TB_YM_STACKLAYER 
			                                                          WHERE STACK_COL_GP = :V_STACK_COL_GP
			                                                            AND STACK_BED_GP = :V_STACK_BED_GP
			                                                            AND STACK_LAYER_ACTIVE_STAT = 'E'
			                                                            AND STACK_LAYER_STAT        = 'E'
			                                                            AND STACK_LAYER_GP < (SELECT MAX(STACK_LAYER_GP) 
			                                                                                    FROM TB_YM_STACKLAYER 
			                                                                                   WHERE STACK_COL_GP = :V_STACK_COL_GP
			                                                                                     AND STACK_BED_GP = :V_STACK_BED_GP
			                                                                                     AND STACK_LAYER_ACTIVE_STAT IN ('C', 'N')
			                                                                                 )
			                                     ))
			           AND A.DEL_YN = 'N'
			           AND A.STACK_BED_ACTIVE_STAT IN ('L', 'O')
			           AND B.STACK_LAYER_ACTIVE_STAT = 'E'
			           AND B.STACK_LAYER_STAT        = 'E'
			           AND B.STOCK_ID IS NULL
			        GROUP BY A.STACK_COL_GP,A.STACK_BED_GP,B.STACK_LAYER_GP   
			       ) A
			 WHERE 1 = 1        
			 */
			
			//com.inisteel.cim.yd.slabyd.dao.SlabYdJspDAO.getCrnWrkMgtDMBed
			jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getCrnSchDnWoLocCurLyr", logId, methodNm, "신규권하위치 조회");
			
			if (jsCrnSch == null || jsCrnSch.size() <= 0) {
				throw new Exception("변경할 권하지시위치[" + sYD_DN_WO_LOC + "] 정보가 없습니다.");
			} else {
			
		    	JDTORecord jrCrnSch = jsCrnSch.getRecord(0);

		    	ydDnWoLocOld   		    = commUtils.trim(jrCrnSch.getFieldString("YD_DN_WO_LOC_OLD"  ));
		    	ydDnWoLayerOld 		    = commUtils.trim(jrCrnSch.getFieldString("YD_DN_WO_LAYER_OLD"));
		    	sSTACK_BED_GP           = commUtils.trim(jrCrnSch.getFieldString("STACK_BED_GP"      )); 
		    	ydDnWoLayer             = commUtils.trim(jrCrnSch.getFieldString("STACK_LAYER_GP"    )); 
		    	ydDnWoLocXaxis 		    = commUtils.trim(jrCrnSch.getFieldString("YD_DN_WO_LOC_XAXIS"));
		    	ydDnWoLocYaxis 		    = commUtils.trim(jrCrnSch.getFieldString("YD_DN_WO_LOC_YAXIS"));
		    	ydDnWoLocZaxis 		    = commUtils.trim(jrCrnSch.getFieldString("YD_DN_WO_LOC_ZAXIS"));
			    String dlLocChkRst 	    = commUtils.trim(jrCrnSch.getFieldString("DL_LOC_CHK_RST"    ));
			    String sOLD_LOC_CHK_RST = commUtils.trim(jrCrnSch.getFieldString("OLD_LOC_CHK_RST"   ));
	            sSTACK_LAYER_GP_SCH     = commUtils.trim(jrCrnSch.getFieldString("STACK_LAYER_GP_SCH"));

			    if ("UP".equals(dlLocChkRst)) {
					throw new Exception("변경할 위치에 권상대기(U) 재료가 적치되어 있습니다.");
				} else if ("DW".equals(dlLocChkRst)) {
					throw new Exception("변경할 위치에 권하대기(D) 재료가 적치되어 있습니다.");
				} else if ("ET".equals(dlLocChkRst)) {
					throw new Exception("지시위치 하단에 공백이 존재합니다.");
				} 
			    
			    if ("DW".equals(sOLD_LOC_CHK_RST)){
			    	throw new Exception("기존 권하위치 상단에 권하스케줄이 존재합니다.");
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
				jrParam.setField("STK_LYR_NO_TEMP"   , sSTACK_LAYER_GP_SCH);
				jrParam.setField("YD_L2_REQUEST_STAT", YmConstant.YD_L2_REQUEST_STAT_5);
				jrParam.setField("YD_CRN_SCH_ID", sYD_CRN_SCH_ID);
				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.upYmCrnSchLocStat");
				/**********************************************************
				* 크레인작업지시요구 전문 조회
				**********************************************************/
				// 일시정지-권하위치변경 적용여부
				if ("Y".equals(sAPP030)) {
					JDTORecord jrS5Msg = commUtils.getParam(logId, methodNm, sMODIFIER);
					
					jrS5Msg.setField("JMS_TC_CD"         , YmConstant.YMA8L004); //크레인작업지시요구
					jrS5Msg.setField("YD_CRN_SCH_ID"     , sYD_CRN_SCH_ID     ); //야드크레인스케쥴ID
					jrS5Msg.setField("MSG_GP"            , "U"   ); //전문구분 - 재지시
					jrS5Msg.setField("YD_CRN_SCH_RMD_CNT", "S5"  ); //S5 일시정지 후 권하위치 변경
	
					sndRecord = commUtils.addSndData(commDao.getMsgL2("YMA8L004", jrS5Msg));
					
					return sndRecord;
				}
				
				//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
				JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, sMODIFIER);
				
				jrYdMsg.setField("JMS_TC_CD"       , YmConstant.A8YML007);	//크레인작업지시요구
				jrYdMsg.setField("YD_EQP_ID"       , sYD_EQP_ID       );	//야드설비ID
				jrYdMsg.setField("YD_WRK_PROG_STAT", "2".equals(sYD_WRK_PROG_STAT) ? "5" : sYD_WRK_PROG_STAT);	//야드작업진행상태(권하위치변경 요구상태)
				jrYdMsg.setField("YD_SCH_CD"       , sYD_SCH_CD       );	//야드스케쥴코드
				jrYdMsg.setField("YD_CRN_SCH_ID"   , sYD_CRN_SCH_ID   );	//야드크레인스케쥴ID
				jrYdMsg.setField("MODIFIER"        , sMODIFIER        );	//수정자

				EJBConnector sndConn = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
				JDTORecord jrRtn = (JDTORecord)sndConn.trx("rcvA8YML007", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });

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
			jrParam.setField("STACK_LAYER_GP_SCH", sSTACK_LAYER_GP_SCH);
			
			//기존 권하지시 위치 수정
			/*
			UPDATE TB_YM_STACKLAYER
			   SET STOCK_ID = NULL
			     , STACK_LAYER_STAT = 'E'
			     , MODIFIER         = :V_MODIFIER
			     , MOD_DDTT         = SYSDATE     
			 WHERE STOCK_ID IN (SELECT STOCK_ID
			                     FROM TB_YM_CRNWRKMTL
			                    WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			                 )
			   AND STACK_COL_GP = :V_STACK_COL_GP_OLD
			   AND STACK_BED_GP = :V_STACK_BED_GP_OLD   
			   AND STACK_LAYER_STAT = 'D'     
			 */
			commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getBfCrnDnWoLoc", logId, methodNm, "기존권하위치 CLEAR");
			
			//신규 권하지시 위치 수정
//			commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getAfCrnDnWoLoc");
			
			
			//신규 적치단 재료정보READ
			/*
			SELECT YD_CRN_SCH_ID
			     , STOCK_ID         -- 기존 재료정보 
			     , STACK_LAYER_GP
			  FROM TB_YM_CRNWRKMTL A
			 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			   AND DEL_YN = 'N'
			 ORDER BY STACK_LAYER_GP
			 */
			JDTORecordSet jsCrnSchMtl = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getCrnWrkMtlBySchId", logId, methodNm, "기존권하위치 조회");

			JDTORecord recOutTemp = null;
			JDTORecord recInTemp = null;
			
			String szSTOCK_ID = null; 
			 
			int intRtnVal = 0; 
			
			//----------------------------------------------------------------------------------------------------------
			//신규적치단 활성화
			//----------------------------------------------------------------------------------------------------------
			String sLYR_GP = "";
			for(int i = 0; i < jsCrnSchMtl.size(); i++) {

		    	recOutTemp = JDTORecordFactory.getInstance().create();
		    	recOutTemp.setRecord(jsCrnSchMtl.getRecord(i));
		    	
		    	szSTOCK_ID = commUtils.trim(recOutTemp.getFieldString("STOCK_ID"));
		    	sLYR_GP    = Integer.toString(Integer.parseInt(ydDnWoLayer)+i);
		    	if (sLYR_GP.length() == 1) {
		    		sLYR_GP = "0" + sLYR_GP;
		    	}
		    	recInTemp  = JDTORecordFactory.getInstance().create();
		    	recInTemp.setField("STACK_COL_GP"   , sSTACK_COL_GP);
		    	recInTemp.setField("STACK_BED_GP"   , sSTACK_BED_GP);
		    	recInTemp.setField("STACK_LAYER_GP" , sLYR_GP);
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
			/*
			MERGE INTO TB_YM_CRNSCH CS USING (
			WITH TEMP_TABLE AS (
			SELECT :V_YD_DN_WO_LOC       AS V_YD_DN_WO_LOC 
			     , :V_YD_DN_WO_LAYER     AS V_YD_DN_WO_LAYER
			     , :V_YD_DN_WO_LOC_XAXIS AS V_YD_DN_WO_LOC_XAXIS
			     , :V_YD_DN_WO_LOC_YAXIS AS V_YD_DN_WO_LOC_YAXIS
			     , :V_YD_DN_WO_LOC_ZAXIS AS V_YD_DN_WO_LOC_ZAXIS
			     , :V_YD_EQP_ID          AS V_YD_EQP_ID
			     , :V_YD_SCH_CD          AS V_YD_SCH_CD
			     , :V_STACK_COL_GP_OLD   AS V_STACK_COL_GP_OLD
			     , :V_STACK_BED_GP_OLD   AS V_STACK_BED_GP_OLD
			     , :V_STACK_LAYER_GP_OLD AS V_STACK_LAYER_GP_OLD
			     , :V_YD_CRN_SCH_ID      AS V_YD_CRN_SCH_ID
			  FROM DUAL
			)
			SELECT CS.YD_CRN_SCH_ID
			      ,CS.YD_UP_WO_LOC       AS YD_UP_WO_LOC
			      ,CS.YD_UP_WO_LAYER     AS YD_UP_WO_LAYER
			      ,CS.YD_UP_WO_LOC_XAXIS AS YD_UP_WO_LOC_XAXIS
			      ,CS.YD_UP_WO_LOC_YAXIS AS YD_UP_WO_LOC_YAXIS
			      ,CS.YD_UP_WO_LOC_ZAXIS AS YD_UP_WO_LOC_ZAXIS
			      
			      ,DECODE(CS.YD_DN_WO_LOC,DD.OLD_YD_DN_WO_LOC,DD.NEW_YD_DN_WO_LOC  , CS.YD_DN_WO_LOC   ) AS YD_DN_WO_LOC
			      ,DECODE(CS.YD_DN_WO_LOC,DD.OLD_YD_DN_WO_LOC,DD.NEW_YD_DN_WO_LAYER, CS.YD_DN_WO_LAYER ) AS YD_DN_WO_LAYER
			      
			      ,DECODE(CS.YD_DN_WO_LOC,DD.OLD_YD_DN_WO_LOC,DD.NEW_YD_DN_WO_LOC_XAXIS,CS.YD_DN_WO_LOC_XAXIS) AS YD_DN_WO_LOC_XAXIS
			      ,DECODE(CS.YD_DN_WO_LOC,DD.OLD_YD_DN_WO_LOC,DD.NEW_YD_DN_WO_LOC_YAXIS,CS.YD_DN_WO_LOC_YAXIS) AS YD_DN_WO_LOC_YAXIS
			      ,DECODE(CS.YD_DN_WO_LOC,DD.OLD_YD_DN_WO_LOC,DD.NEW_YD_DN_WO_LOC_ZAXIS,CS.YD_DN_WO_LOC_ZAXIS) AS YD_DN_WO_LOC_ZAXIS
			
			      ,CS.YD_DN_WO_XAXIS_GAP_MAX
			      ,CS.YD_DN_WO_XAXIS_GAP_MIN
			      ,CS.YD_DN_WO_YAXIS_GAP_MAX
			      ,CS.YD_DN_WO_YAXIS_GAP_MIN
			      ,DD.YD_EQP_ID
			      ,DD.YD_SCH_CD
			      ,DD.YD_STK_BED_XAXIS_TOL
			      ,DD.YD_STK_BED_YAXIS_TOL      
			  FROM TB_YM_CRNSCH CS
			      ,(SELECT YD_CRN_SCH_ID
			              ,YD_WBOOK_ID
			              ,YD_DN_WO_LOC                     AS OLD_YD_DN_WO_LOC
			              ,YD_DN_WO_LAYER                   AS OLD_YD_DN_WO_LAYER
			              ,YD_DN_WO_LOC_ZAXIS               AS OLD_YD_DN_WO_LOC_ZAXIS
			              ,V_YD_DN_WO_LOC                  AS NEW_YD_DN_WO_LOC
			              ,V_YD_DN_WO_LAYER                AS NEW_YD_DN_WO_LAYER
			              ,TO_NUMBER(V_YD_DN_WO_LOC_XAXIS) AS NEW_YD_DN_WO_LOC_XAXIS
			              ,TO_NUMBER(V_YD_DN_WO_LOC_YAXIS) AS NEW_YD_DN_WO_LOC_YAXIS
			              ,TO_NUMBER(V_YD_DN_WO_LOC_ZAXIS) AS NEW_YD_DN_WO_LOC_ZAXIS
			              ,NVL(V_YD_EQP_ID,YD_EQP_ID)      AS YD_EQP_ID
			              ,NVL(V_YD_SCH_CD,YD_SCH_CD)      AS YD_SCH_CD
			              ,(SELECT YD_STK_BED_XAXIS_TOL FROM TB_YM_STACKER WHERE STACK_COL_GP = SUBSTR(V_YD_DN_WO_LOC,1,6) AND STACK_BED_GP = SUBSTR(V_YD_DN_WO_LOC,7,2)) AS YD_STK_BED_XAXIS_TOL
			              ,(SELECT YD_STK_BED_YAXIS_TOL FROM TB_YM_STACKER WHERE STACK_COL_GP = SUBSTR(V_YD_DN_WO_LOC,1,6) AND STACK_BED_GP = SUBSTR(V_YD_DN_WO_LOC,7,2)) AS YD_STK_BED_YAXIS_TOL
			          FROM TB_YM_CRNSCH
			             , TEMP_TABLE
			         WHERE YD_CRN_SCH_ID = V_YD_CRN_SCH_ID
			         ) DD
			 WHERE CS.YD_WBOOK_ID   = DD.YD_WBOOK_ID
			   AND CS.YD_CRN_SCH_ID = DD.YD_CRN_SCH_ID
			   AND ((CS.YD_DN_WO_LOC )= DD.OLD_YD_DN_WO_LOC AND NVL(CS.YD_DN_WO_LAYER,'01') >= NVL(DD.OLD_YD_DN_WO_LAYER,'01')
			     OR (CS.YD_UP_WO_LOC = DD.OLD_YD_DN_WO_LOC AND NVL(CS.YD_UP_WO_LAYER,'01') >= NVL(DD.OLD_YD_DN_WO_LAYER,'01')))
			   AND CS.DEL_YN = 'N'
			) DD ON (CS.YD_CRN_SCH_ID = DD.YD_CRN_SCH_ID)
			WHEN MATCHED THEN UPDATE SET
			     CS.MODIFIER           = :V_MODIFIER
			    ,CS.MOD_DDTT           = SYSDATE
			    ,CS.YD_EQP_ID          = DD.YD_EQP_ID
			    ,CS.YD_SCH_CD          = DD.YD_SCH_CD
			    ,CS.YD_UP_WO_LOC       = DD.YD_UP_WO_LOC
			    ,CS.YD_UP_WO_LAYER     = DD.YD_UP_WO_LAYER
			    ,CS.YD_UP_WO_LOC_XAXIS = DD.YD_UP_WO_LOC_XAXIS
			    ,CS.YD_UP_WO_LOC_YAXIS = DD.YD_UP_WO_LOC_YAXIS
			    ,CS.YD_UP_WO_LOC_ZAXIS = DD.YD_UP_WO_LOC_ZAXIS
			    ,CS.YD_DN_WO_LOC       = DD.YD_DN_WO_LOC
			    ,CS.YD_DN_WO_LAYER     = DD.YD_DN_WO_LAYER
			    ,CS.YD_DN_WO_LOC_XAXIS = DD.YD_DN_WO_LOC_XAXIS
			    ,CS.YD_DN_WO_LOC_YAXIS = DD.YD_DN_WO_LOC_YAXIS
			    ,CS.YD_DN_WO_LOC_ZAXIS = DD.YD_DN_WO_LOC_ZAXIS
			    ,CS.YD_DN_WO_XAXIS_GAP_MAX =DD.YD_STK_BED_XAXIS_TOL
			    ,CS.YD_DN_WO_XAXIS_GAP_MIN =DD.YD_STK_BED_XAXIS_TOL
			    ,CS.YD_DN_WO_YAXIS_GAP_MAX =DD.YD_STK_BED_YAXIS_TOL
			    ,CS.YD_DN_WO_YAXIS_GAP_MIN =DD.YD_STK_BED_YAXIS_TOL
			 */
			
			commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updCrnSchDnWoLocCrnSch", logId, methodNm, "TB_YM_CRNSCH");				

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

			jrYdMsg.setField("JMS_TC_CD"       , YmConstant.A8YML007);	//크레인작업지시요구
			jrYdMsg.setField("YD_EQP_ID"       , sYD_EQP_ID      );	//야드설비ID
			jrYdMsg.setField("YD_WRK_PROG_STAT", sYD_WRK_PROG_STAT);	//야드작업진행상태
			jrYdMsg.setField("YD_SCH_CD"       , sYD_SCH_CD      );	//야드스케쥴코드
			jrYdMsg.setField("YD_CRN_SCH_ID"   , sYD_CRN_SCH_ID   );	//야드크레인스케쥴ID
			jrYdMsg.setField("MODIFIER"        , sMODIFIER     );	//수정자

			EJBConnector sndConn = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
			JDTORecord jrRtn = (JDTORecord)sndConn.trx("rcvA8YML007", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });

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
	 * 크레인스케줄 기준 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updSchRuleMgt(GridData gdReq) throws DAOException {
		String methodNm = "크레인스케줄 기준 변경[BSlabJspSeEJB.updSchRuleMgt] < " + gdReq.getNavigateValue();
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
				
				//jrParam.setField("MODIFIER"            , commUtils.getValue(gdReq, "MODIFIER"            , ii)); //수정자              
				jrParam.setField("YD_SCH_RNG_CD"       , commUtils.getValue(gdReq, "YD_SCH_RNG_CD"       , ii)); //스케줄범위코드      
				jrParam.setField("YD_SCH_WHIO_GP"      , commUtils.getValue(gdReq, "YD_SCH_WHIO_GP"      , ii)); //스케줄입출고구분    
				jrParam.setField("YD_SCH_DIV_GP"       , commUtils.getValue(gdReq, "YD_SCH_DIV_GP"       , ii)); //스케줄분할구분      
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
				
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updSchRuleInfo", logId, methodNm, "크레인스케줄 기준 수정");
								
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
	 * 야드및설비 열정보수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecord updSlabYdStkPosSet(GridData gdReq) throws DAOException {
		String methodNm = "야드및설비 열정보수정[BSlabJspSeEJB.updSlabYdStkPosSet] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		//String StackLayerActiveStat = "";
		JDTORecord sndRecord = JDTORecordFactory.getInstance().create();
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
			
			inRecord.setField("MODIFIER"	            	,commUtils.trim(gdReq.getParam("userid")));
			inRecord.setField("STACK_COL_GP"	        	,commUtils.getValue(gdReq, "STACK_COL_GP", 0));
			//inRecord.setField("STACK_BED_ACTIVE_STAT"	    ,commUtils.getValue(gdReq, "STACK_COL_ACTIVE_STAT", 0));
			//inRecord.setField("STACK_LAYER_ACTIVE_STAT"	    ,commUtils.getValue(gdReq, "STACK_COL_ACTIVE_STAT", 0));
			//inRecord.setField("STACK_COL_ACTIVE_STAT"	    ,commUtils.getValue(gdReq, "STACK_COL_ACTIVE_STAT", 0));
			
			//inRecord.setField("STACK_BED_X_AXIS"	        ,commUtils.getValue(gdReq, "STACK_COL_RULE_X_AXIS", 0));
			//inRecord.setField("STACK_LAYER_X_AXIS"	        ,commUtils.getValue(gdReq, "STACK_COL_RULE_X_AXIS", 0));
			//inRecord.setField("STACK_COL_RULE_X_AXIS"	    ,commUtils.getValue(gdReq, "STACK_COL_RULE_X_AXIS", 0));
			
			//inRecord.setField("STACK_BED_Y_AXIS"	        ,commUtils.getValue(gdReq, "STACK_COL_RULE_Y_AXIS", 0));
			//inRecord.setField("STACK_LAYER_Y_AXIS"	        ,commUtils.getValue(gdReq, "STACK_COL_RULE_Y_AXIS", 0));
			//inRecord.setField("STACK_COL_RULE_Y_AXIS"	    ,commUtils.getValue(gdReq, "STACK_COL_RULE_Y_AXIS", 0));
			
			//inRecord.setField("STACK_BED_Z_AXIS"	        ,commUtils.getValue(gdReq, "STACK_COL_RULE_Z_AXIS", 0));
			//inRecord.setField("STACK_LAYER_Z_AXIS"	        ,commUtils.getValue(gdReq, "STACK_COL_RULE_Z_AXIS", 0));
			//inRecord.setField("STACK_COL_RULE_Z_AXIS"	    ,commUtils.getValue(gdReq, "STACK_COL_RULE_Z_AXIS", 0));
			
			//적치 베드 정보의  UPDATE
			/*
			UPDATE TB_YM_STACKER
			      SET STACK_BED_ACTIVE_STAT = :V_STACK_BED_ACTIVE_STAT
			       , MOD_DDTT = SYSDATE
			       , MODIFIER  = :V_MODIFIER  
			 WHERE STACK_COL_GP = :V_STACK_COL_GP
			 */
//			commDao.update(inRecord, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.bslabYdNEqpXyaxisSet.updYmStkbedYmStrActStat");
			
			//TB_YD_STKLYR 적치 베드 정보의  UPDATE
			/*
			UPDATE TB_YM_STACKLAYER            
			   SET STACK_LAYER_ACTIVE_STAT = :V_STACK_LAYER_ACTIVE_STAT
			       , MOD_DDTT = SYSDATE
			       , MODIFIER  = :V_MODIFIER  
			WHERE STACK_COL_GP  = :V_STACK_COL_GP
			 */
			
//			commDao.update(inRecord, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.bslabYdNEqpXyaxisSet.updYmStklyrColActStat");
			
			//TB_YD_STKBED 적치 베드 정보의  UPDATE
			/*
			UPDATE TB_YM_STACKER
			   SET STACK_BED_X_AXIS = :V_STACK_BED_X_AXIS
			     , STACK_BED_Y_AXIS = :V_STACK_BED_Y_AXIS
			     , STACK_BED_Z_AXIS = :V_STACK_BED_Z_AXIS
			     , MOD_DDTT   = SYSDATE
			     , MODIFIER   = :V_MODIFIER  
			 WHERE STACK_COL_GP = :V_STACK_COL_GP
			 */
//			commDao.update(inRecord, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.bslabYdNEqpXyaxisSet.updYmStkbedYmStrX");

			//야드적치단 좌표만  UPDATE
			/*
			UPDATE TB_YM_STACKLAYER            
			   SET STACK_LAYER_X_AXIS = :V_STACK_LAYER_X_AXIS
			     , STACK_LAYER_Y_AXIS = :V_STACK_LAYER_Y_AXIS
			     , STACK_LAYER_Z_AXIS = :V_STACK_LAYER_Z_AXIS
			     , MOD_DDTT  = SYSDATE
			     , MODIFIER  = :V_MODIFIER  
			 WHERE STACK_COL_GP  = :V_STACK_COL_GP
			 */
//			commDao.update(inRecord, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.bslabYdNEqpXyaxisSet.updYmStklyrX");
			
			//적치열 정보 UPDATE
			/*
			UPDATE TB_YM_STACKCOL
			   SET STACK_COL_ACTIVE_STAT = :V_STACK_COL_ACTIVE_STAT  
				 , STACK_COL_RULE_X_AXIS = :V_STACK_COL_RULE_X_AXIS
				 , STACK_COL_RULE_Y_AXIS = :V_STACK_COL_RULE_Y_AXIS
				 , STACK_COL_RULE_Z_AXIS = :V_STACK_COL_RULE_Z_AXIS     
			     , MOD_DDTT = SYSDATE             
				 , MODIFIER = :V_MODIFIER             
			 WHERE STACK_COL_GP = :V_STACK_COL_GP
			 */
//			commDao.update(inRecord, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.bslabYdNEqpXyaxisSet.updYmStkcol");
			
			JDTORecord jDrd = JDTORecordFactory.getInstance().create();
			
			jDrd.setField("MSG_ID"				, "YMA8L001");
			jDrd.setField("DATE"				, commUtils.getDate10());
			jDrd.setField("TIME"				, commUtils.getTime8());
			jDrd.setField("MSG_GP"				, "");
			jDrd.setField("MSG_LEN"				, "0089");
			jDrd.setField("YD_INFO_SYNC_CD"		, "3");						//1:동,2:SPAN,3:열,4:BED
			jDrd.setField("YD_GP"				, inRecord.getFieldString("STACK_COL_GP").substring(0, 1));
			jDrd.setField("COL_GP"				, inRecord.getFieldString("STACK_COL_GP").substring(5, 6));
			jDrd.setField("STACK_COL_GP"		, inRecord.getFieldString("STACK_COL_GP"));

			sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2("YMA8L001", jDrd));
			
			commUtils.printLog(logId, methodNm, "S-", gdReq);
			
			return sndRecord;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of updSlabYdStkPosSet	
	
	/**
	 * 야드및설비 베드정보수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecord updSlabYdStkPosSetBed(GridData gdReq) throws DAOException {
		String methodNm = "야드및설비 베드정보수정[BSlabJspSeEJB.updSlabYdStkPosSetBed] < " + gdReq.getNavigateValue();
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
				 */
				commDao.update(inRecord, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updSlabYdStkPosSetBed.updYdStklyrDan");
				
				//적치 베드 정보의  UPDATE 
				/*
				UPDATE TB_YM_STACKER
				   SET MODIFIER = :V_MODIFIER
				     , MOD_DDTT = SYSDATE
				     , STACK_BED_X_AXIS      = :V_STACK_LAYER_X_AXIS
				     , STACK_BED_Y_AXIS      = :V_STACK_LAYER_Y_AXIS
				     , STACK_BED_Z_AXIS      = NVL(:V_STACK_LAYER_Z_AXIS,STACK_LAYER_Z_AXIS)
				     , YD_STK_BED_XAXIS_TOL  = :V_YD_STK_BED_XAXIS_TOL
				     , YD_STK_BED_YAXIS_TOL  = :V_YD_STK_BED_YAXIS_TOL
				     , YD_STK_BED_ZAXIS_TOL  = NVL(:V_YD_STK_BED_ZAXIS_TOL,YD_STK_BED_ZAXIS_TOL)
				 WHERE STACK_COL_GP = :V_STACK_COL_GP
				   AND STACK_BED_GP = :V_STACK_BED_GP
				 */
				commDao.update(inRecord, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updSlabYdStkPosSetBed.updYdStklyrTol");
			
				jDrd.setField("MSG_ID"				, "YMA8L001");
				jDrd.setField("DATE"				, commUtils.getDate10());
				jDrd.setField("TIME"				, commUtils.getTime8());
				jDrd.setField("MSG_GP"				, "");
				jDrd.setField("MSG_LEN"				, "0089");
				jDrd.setField("YD_INFO_SYNC_CD"		, "4");						//1:동,2:SPAN,3:열,4:BED
				jDrd.setField("YD_GP"				, "2");
				jDrd.setField("STACK_BED_GP"		, commUtils.getValue(gdReq, "STACK_BED_GP", ii));
				jDrd.setField("STACK_COL_GP"		, commUtils.getValue(gdReq, "STACK_COL_GP", ii));
				
				sndRecord = commUtils.addSndData(sndRecord,commDao.getMsgL2("YMA8L001", jDrd));
			}
			
			commUtils.printLog(logId, methodNm, "S-", gdReq);
			
			return sndRecord;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of updSlabYdStkPosSetBed		
	/**
	 * 설비상태 (변경 설비기준조회 )
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updEqpOprnStat(GridData gdReq) throws DAOException {
		String methodNm = "설비상태 변경[BSlabJspFaEJB.updEqpOprnStat] < " + gdReq.getNavigateValue();
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
					
					commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updEqpOprnStat", logId, methodNm, "설비상태 변경");
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
	 * 저장영역별검색순서조회 - 저장
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updStrAreaSrchSeq(GridData gdReq) throws DAOException {
		String methodNm = "저장영역별검색순서조회 저장[BSlabJspSeEJB.updStrAreaSrchSeq] < " + gdReq.getNavigateValue();
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
			
			String sSORT       = gdReq.getParam("SORT"); //적용유무
			
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setField("YD_SCH_CD"  , gdReq.getParam("YD_SCH_CD"));   //스케줄코드
			jrParam.setField("YD_ROUTE_GP", gdReq.getParam("YD_ROUTE_GP")); //행선
			
			//수정할 레코드 수(전체로 넘어옴)
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			 
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
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.delSchLocSrch");
			
			
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
				
				if ("U".equals(sCRUD) || "".equals(sCRUD)) {
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
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updSchLocSrch");
				}
				
				if ("C".equals(sCRUD)) {
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
					rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getSchLocSrchAll", logId, methodNm, "");
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
						commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updSchLocSrch");
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
						commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insSchLocSrch");
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
	} // end of updStrAreaSrchSeq

	/**
	 * 기준관리 - 세부항목수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updYmRule(GridData gdReq) throws DAOException {
		String methodNm = "기준관리 - 세부항목수정[BSlabJspSeEJB.updYmRule] < " + gdReq.getNavigateValue();
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
				//세부항목수정
				//if(!commUtils.getValue(gdReq, "DTL_ITM1", ii).equals("")){
					jrParam.setField("DTL_ITM1"		, commUtils.getValue(gdReq, "DTL_ITM1", ii) );
				//}
				//if(!commUtils.getValue(gdReq, "DTL_ITM2", ii).equals("")){
					jrParam.setField("DTL_ITM2"		, commUtils.getValue(gdReq, "DTL_ITM2", ii) );
				//}
				//if(!commUtils.getValue(gdReq, "DTL_ITM3", ii).equals("")){
					jrParam.setField("DTL_ITM3"		, commUtils.getValue(gdReq, "DTL_ITM3", ii) );
				//}
				//if(!commUtils.getValue(gdReq, "DTL_ITM4", ii).equals("")){
					jrParam.setField("DTL_ITM4"		, commUtils.getValue(gdReq, "DTL_ITM4", ii) );
				//}
				//if(!commUtils.getValue(gdReq, "DTL_ITM5", ii).equals("")){
					jrParam.setField("DTL_ITM5"		, commUtils.getValue(gdReq, "DTL_ITM5", ii) );
				//}
				//if(!commUtils.getValue(gdReq, "DTL_ITM6", ii).equals("")){
					jrParam.setField("DTL_ITM6"		, commUtils.getValue(gdReq, "DTL_ITM6", ii) );
				//}
				//if(!commUtils.getValue(gdReq, "DTL_ITM7", ii).equals("")){
					jrParam.setField("DTL_ITM7"		, commUtils.getValue(gdReq, "DTL_ITM7", ii) );
				//}
				//if(!commUtils.getValue(gdReq, "DTL_ITM8", ii).equals("")){
					jrParam.setField("DTL_ITM8"		, commUtils.getValue(gdReq, "DTL_ITM8", ii) );
				//}
				//if(!commUtils.getValue(gdReq, "DTL_ITM9", ii).equals("")){
					jrParam.setField("DTL_ITM9"		, commUtils.getValue(gdReq, "DTL_ITM9", ii) );
				//}
				//if(!commUtils.getValue(gdReq, "DTL_ITM10", ii).equals("")){
					jrParam.setField("DTL_ITM10"	, commUtils.getValue(gdReq, "DTL_ITM10", ii) );
				//}
				jrParam.setField("MODIFIER"		, gdReq.getParam("userid"));
				jrParam.setField("REPR_CD_GP"	, commUtils.getValue(gdReq, "REPR_CD_GP", ii) );
				jrParam.setField("CD_GP"		, commUtils.getValue(gdReq, "CD_GP", ii) );
				jrParam.setField("ITEM"			, commUtils.getValue(gdReq, "ITEM", ii));
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updYmRule", logId, methodNm, "기준관리 수정");
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
	 * 폭, 외경기준 수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public GridData updateStackRuleInfo(GridData gdReq) throws DAOException {
		String methodNm = "폭, 외경기준 변경[BSlabJspSeEJB.updateStackRuleInfo] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			//Grid date 를 JDTORecord data 로 변환
			JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
			
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			
			inRecord.setField("SCH_CD", inRecord.getFieldString("P_WIDTH"));
			inRecord.setField("SCH_RULE_VAL", "1");
			
			commDao.update(inRecord, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.bslabStkPrfrPriorjl.updStackRuleInfo_02"); //폭기준 수정
			
			inRecord.setField("SCH_CD", inRecord.getFieldString("P_LENGTH"));
			inRecord.setField("SCH_RULE_VAL", "2");
			
			commDao.update(inRecord, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.bslabStkPrfrPriorjl.updStackRuleInfo_02"); //외경기준 수정
			
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
	 * 목적동순위
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updateSlabMoveBayRank(GridData gdReq) throws DAOException {
		String methodNm = "목적동순위[BSlabJspSeEJB.updateSlabMoveBayRank] < " + gdReq.getNavigateValue();
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
				
				jrParam.setField("RANKING"	        ,commUtils.getValue(gdReq, "RANKING", ii)); 
				jrParam.setField("DEST_BAY"	        ,commUtils.getValue(gdReq, "DEST_BAY_NM", ii)); 
				jrParam.setField("PT_DEST_BAY"	    ,commUtils.getValue(gdReq, "DEST_BAY_NM1", ii)); 
				jrParam.setField("REGISTER"	        ,commUtils.trim(gdReq.getParam("userid"))); 
				jrParam.setField("ORD_YEOJAE_GP"	,commUtils.getValue(gdReq, "ORD_YEOJAE_GP", ii)); 
				jrParam.setField("SLAB_GP"	        ,commUtils.getValue(gdReq, "SLAB_GP", ii)); 
				
				/*com.inisteel.cim.ym.bslab.dao.BSlabDAO.updateSlabMoveBayRanking*/
				/*UPDATE TB_YM_SLABMOVEBAYRANKING
				SET RANKING 	  = :V_RANKING,
					DEST_BAY      = :V_DEST_BAY,
                    PT_DEST_BAY   = :V_PT_DEST_BAY, 					
					REGISTER 	  = :V_REGISTER ,
					REG_DD 		  = SYSDATE
				WHERE ORD_YEOJAE_GP = :V_ORD_YEOJAE_GP 
				  AND SLAB_GP 	    = :V_SLAB_GP */				
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updateSlabMoveBayRanking", logId, methodNm, "목적동순위 변경");
								
			}
			if(!(commUtils.trim(gdReq.getParam("COOL_TIME"))).equals(commUtils.trim(gdReq.getParam("STACK_RULE_MIN")))){
			   jrParam.setField("STACK_RULE_MIN"	,commUtils.trim(gdReq.getParam("COOL_TIME"))); 
			   jrParam.setField("ORD_YEOJAE_GP"	,commUtils.trim(gdReq.getParam("ORD_YEOJAE_GP"))); 
			
			   /*com.inisteel.cim.ym.bslab.dao.BSlabDAO.updateListTimeSLAB
			   update TB_YM_RULE
			   set DTL_ITM1 = :V_STACK_RULE_MIN
			   where CD_GP='2'
			    and ITEM= DECODE(:V_ORD_YEOJAE_GP,'H,C','A','H','A','C','A'
			                                         ,'I,D','S','I','S','D','S'
			                                         ,'J,E','C','J','C','E','C' 
			                                         ,'K','M'
			                                         ,'')
			    and REPR_CD_GP = 'CLHR' */
			   commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updateListTimeSLAB", logId, methodNm, "냉각시간 변경");
			}
			   
			   
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updateSlabMoveBayRank	
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인 스케줄취소처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtCrnSchCncl(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인 스케줄취소[BSlabJspSeEJB.trtCrnSchCncl] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try { 
			commUtils.printLog(logId, methodNm, "S+");

			String sYD_CRN_SCH_ID     = StringHelper.evl(commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID")),""); //야드크레인스케쥴ID
			String sYD_WBOOK_ID       = StringHelper.evl(commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"  )),""); //야드작업예약ID
			String sYD_L2_RETURN_FLAG = StringHelper.evl(commUtils.trim(rcvMsg.getFieldString("YD_L2_RETURN_FLAG")),""); //
			String sYD_SCH_CD         = StringHelper.evl(commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD")),"");  // 스케줄코드 
			String sCNCL_BY_WHO       = StringHelper.evl(commUtils.trim(rcvMsg.getFieldString("CNCL_BY_WHO")),"");  // 스케줄취소가 L2 서포팅블록 이상으로 발생한것 인지 확인 변수 [E100]

			if ("".equals(sYD_CRN_SCH_ID)) {
				throw new Exception("크레인스케쥴ID가 없습니다.");
			} else if ("".equals(sYD_WBOOK_ID)) {
				throw new Exception("작업예약ID가 없습니다.");
			} else if ("".equals(sYD_SCH_CD)) {
				throw new Exception("스케줄코드가 없습니다.");
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
			  FROM TB_YM_EQUIP EQ
			      ,(SELECT MIN(DECODE(YD_CRN_SCH_ID,SC_YD_CRN_SCH_ID,SC_YD_CRN_SCH_ID)) AS YD_CRN_SCH_ID
			              ,MIN(DECODE(YD_CRN_SCH_ID,SC_YD_CRN_SCH_ID,YD_WRK_PROG_STAT)) AS YD_WRK_PROG_STAT
			              ,MIN(DECODE(RN,1,DECODE(YD_CRN_SCH_ID,SC_YD_CRN_SCH_ID,'W',YD_WRK_PROG_STAT))) AS YD_EQP_STAT
			              ,MIN(DECODE(RN,1,YD_EQP_ID)) AS YD_EQP_ID
			          FROM (SELECT YD_CRN_SCH_ID
			                      ,YD_WRK_PROG_STAT
			                      ,YD_EQP_ID
			                      ,:V_YD_CRN_SCH_ID AS SC_YD_CRN_SCH_ID --취소 크레인스케줄ID
			                      ,ROW_NUMBER() OVER (ORDER BY YD_CRN_SCH_ID) AS RN
			                  FROM TB_YM_CRNSCH
			                 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
			                   AND DEL_YN      = 'N')) CS
			 WHERE CS.YD_EQP_ID = EQ.EQUIP_GP(+);
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
				
				jrParam.setField("YD_CRN_SCH_ID", sYD_CRN_SCH_ID); //야드크레인스케쥴ID
				jrParam.setField("MSG_GP"       , "D"       ); //전문구분(취소)

				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L004", jrParam));
			}
			
			//2024.04.17 크레인 권하실적과 겹칠시, deadlock 발생하는 현상 해결을 위해 변경(ora- 에러 수정)
			//크레인스케줄수정 후 설비상태 수정  -> 설비상태 수정 후 크레인스케줄 수정 
			/**********************************************************
			* 5. 설비상태 수정 - 크레인이 고장 또는 Off-Line이 아니고 상태가 다르면
			**********************************************************/
			if ("Y".equals(eqpUpdYn)) {
				jrParam.setField("EQUIP_GP"  , ydEqpId  ); //야드설비ID
				jrParam.setField("EQUIP_STAT", ydEqpStat); //야드설비상태
				/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updStatEqp 
				--설비 상태 수정 
				UPDATE TB_YM_EQUIP
				   SET MODIFIER   = :V_MODIFIER
				      ,MOD_DDTT   = SYSDATE
				      ,WPROG_STAT = :V_EQUIP_STAT
				 WHERE EQUIP_GP   = :V_EQUIP_GP
				   AND DEL_YN     = 'N'
				*/	   
				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updStatEqp", logId, methodNm, "TB_YM_EQUIP");				
			}
			
 
			/**********************************************************
			* 3-1. 대차하차작업 판단
			*      - 대차 하차 작업 일 경우 하단에서 쿼리 변경
			*      - 대차 하차 작업은 작업예약ID+크레인스케줄ID로 조회
			*        크레인 스케줄ID 보다 크거나 같은 대상만 취소처리
			**********************************************************/
			String sIS_TC_LM = "N"; //대차 하차 작업 FLAG (대차하차 작업이면 'Y')
			
			if(sYD_SCH_CD.length() == 8) {
				if("TC".equals(sYD_SCH_CD.substring(2, 4)) && "LM".equals(sYD_SCH_CD.substring(6, 8))) {
					sIS_TC_LM = "Y";
				}
			}
			
			/**********************************************************
			* 3-2. 권상, 권하위치 원복 - 적치단, 적치Bed
			**********************************************************/
			commUtils.printLog(logId, "○○○ 권상, 권하위치 원복", "[info]");
			//적치단 수정 - 권상위치(U -> C), 권하위치(D -> E)
			//commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updCrnWrkMgtSCStkLyr", logId, methodNm, "TB_YM_STACKLAYER");
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getCrnWrkMgtSCStkLyr
			WITH CSM AS ( 
			SELECT CM.STOCK_ID
			     , SUBSTR(CS.YD_UP_WO_LOC,1,6) AS STACK_COL_GP_UP
			     , SUBSTR(CS.YD_UP_WO_LOC,7,2) AS STACK_BED_GP_UP
			     , SUBSTR(CS.YD_DN_WO_LOC,1,6) AS STACK_COL_GP_DN
			     , SUBSTR(CS.YD_DN_WO_LOC,7,2) AS STACK_BED_GP_DN
			     , CS.YD_UP_WO_LAYER AS STACK_LAYER_GP
			     , CS.YD_CRN_SCH_ID
			     , CS.YD_TO_LOC_DCSN_MTD
			     , CM.RNUM
			  FROM TB_YM_CRNSCH CS
			     ,(
			        SELECT CS.YD_CRN_SCH_ID AS YD_CRN_SCH_ID
			             , CM.STOCK_ID
			             , ROW_NUMBER() OVER(PARTITION BY CM.STOCK_ID ORDER BY CS.YD_CRN_SCH_ID) AS RNUM
			          FROM TB_YM_CRNSCH    CS
			              ,TB_YM_CRNWRKMTL CM
			         WHERE CM.YD_CRN_SCH_ID  = CS.YD_CRN_SCH_ID
			--           AND CS.YD_WBOOK_ID    = :V_YD_WBOOK_ID
			--           AND CM.YD_CRN_SCH_ID >= :V_YD_CRN_SCH_ID
			           AND CS.DEL_YN         = 'N'
			           AND CM.DEL_YN         = 'N'
			           AND CS.YD_WBOOK_ID = :V_YD_WBOOK_ID
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
			           AND SL.STACK_LAYER_STAT = 'U' --권상대기
			--           AND CSM.RNUM = '1'
			         UNION ALL 
			        --권하지시위치
			        SELECT SL.STACK_COL_GP
			             , SL.STACK_BED_GP
			             , SL.STACK_LAYER_GP
			             , NULL AS STOCK_ID
			             , 'E'  AS STACK_LAYER_STAT --적치가능
			          FROM TB_YM_STACKLAYER SL
			             , CSM
			         WHERE SL.STOCK_ID     = CSM.STOCK_ID 
			           AND SL.STACK_COL_GP = CSM.STACK_COL_GP_DN
			           AND SL.STACK_BED_GP = CSM.STACK_BED_GP_DN
			--           AND SL.STACK_LAYER_STAT = 'D' --권하대기
			--           AND CSM.RNUM = '1'
			       )
			 GROUP BY STACK_COL_GP, STACK_BED_GP, STACK_LAYER_GP
			 
			 */
			String sQueryId = "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getCrnWrkMgtSCStkLyr";
			if ("Y".equals(sIS_TC_LM)) {
				sQueryId = "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getCrnWrkMgtSCStkLyrMtl";
			}
			JDTORecordSet rsResult = commDao.select(jrParam, sQueryId, logId, methodNm, "TB_YM_STACKLAYER ○○○ 권상, 권하위치 원복 대상 조회");
			
			for (int i = 0; i < rsResult.size(); ++i) {
				
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
				commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updYdStkLyrYdStkColBedGp", logId, methodNm, "적치단 수정 - 권상위치(U -> C), 권하위치(D -> E)");
			}
			
			/**********************************************************
			* 4. 크레인스케줄 삭제
			**********************************************************/
			commUtils.printLog(logId, "○○○ 크레인스케줄 삭제", "[info]");
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
			if ("Y".equals(sIS_TC_LM)) {
				/*
				UPDATE TB_YM_CRNWRKMTL
				   SET MODIFIER = :V_MODIFIER
				     , MOD_DDTT = SYSDATE
				     , DEL_YN   = 'Y'
				 WHERE DEL_YN   = 'N'
				   AND YD_CRN_SCH_ID >= :V_YD_CRN_SCH_ID
				   AND YD_WBOOK_ID   = :V_YD_WBOOK_ID
				 */
				sQueryId = "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updCrnWrkMgtSCCrnMtlUnitMtl";
			}
			commDao.update(jrParam, sQueryId, logId, methodNm, "TB_YM_CRNWRKMTL - 크레인작업재료 삭제");				
			
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
			if ("Y".equals(sIS_TC_LM)) {
				/*
				UPDATE TB_YM_CRNSCH
				   SET MODIFIER = :V_MODIFIER
				      ,MOD_DDTT = SYSDATE
				      ,DEL_YN   = 'Y'
				 WHERE YD_CRN_SCH_ID >= :V_YD_CRN_SCH_ID
				   AND YD_WBOOK_ID    = :V_YD_WBOOK_ID
				 */
				sQueryId = "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updCrnWrkMgtSCCrnSchUnitMtl";
			}
			commDao.update(jrParam, sQueryId, logId, methodNm, "TB_YM_CRNSCH - 크레인스케줄 삭제");				
			
			
			/**********************************************************
			* 이송하차일 경우 작업예약 삭제
			**********************************************************/
			
			if ("PT02LM".equals(sYD_SCH_CD.substring(2, 8)) && !"E100".equals(sCNCL_BY_WHO)){
				
				commUtils.printLog(logId, "○○○ 이송하차일 경우 작업예약 취소 START ○○○"+sYD_SCH_CD, "[info]");
				//작업예약재료 삭제
				/*
				UPDATE TB_YM_WRKBOOKMTL
				   SET MODIFIER    = :V_MODIFIER
				      ,MOD_DDTT    = SYSDATE
				      ,DEL_YN      = 'Y'
				 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
				   AND DEL_YN      = 'N'
				 */
				jrParam.setField("sYD_WBOOK_ID", sYD_WBOOK_ID);
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updDelYnWrkBookMtl", logId, methodNm, "TB_YM_WRKBOOKMTL");				

				//작업예약 삭제
				/*
				UPDATE TB_YM_WRKBOOK
				   SET MODIFIER    = :V_MODIFIER
				      ,MOD_DDTT    = SYSDATE
				      ,DEL_YN      = 'Y'
				 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
				   AND DEL_YN      = 'N'
				 */
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updDelYnWrkBook", logId, methodNm, "TB_YM_WRKBOOK");
				
				commUtils.printLog(logId, "○○○ 이송하차일 경우 작업예약 취소 END ○○○"+sYD_SCH_CD, "[info]");
				
			}else{
				commUtils.printLog(logId, "○○○ Grouping대상 작업예약 취소 START ○○○"+sYD_WBOOK_ID, "[info]");
				/*com.inisteel.cim.ym.bslab.dao.BSlabDAO.getGrpSlabWrkBookMtl
				SELECT A.YD_WBOOK_ID
				      ,A.YD_GP
				      ,A.YD_BAY_GP
				      ,A.YD_SCH_CD
				      ,A.YD_SCH_PRIOR
				      ,A.YD_SCH_PROG_STAT
				      ,A.YD_TO_LOC_DCSN_MTD
				      ,A.YD_TO_LOC_GUIDE
				      ,A.YD_WRK_PLAN_CRN
				      ,B.STOCK_ID
				      ,B.STACK_COL_GP
				      ,B.STACK_BED_GP
				      ,B.STACK_LAYER_GP
				      ,B.YD_UP_COLL_SEQ
				      ,B.YD_TAKE_OUT_CD
				      ,(SELECT CASE WHEN SUBSTR(YD_DN_WO_LOC,1,2) = 'XX' THEN ''
				               ELSE YD_DN_WO_LOC END AS YD_TO_LOC_GUIDE_FNL
				         FROM TB_YM_CRNSCH C
				             ,TB_YM_CRNWRKMTL D
				        WHERE C.YD_WBOOK_ID = A.YD_WBOOK_ID
				          AND C.YD_CRN_SCH_ID = D.YD_CRN_SCH_ID
				          AND D.STOCK_ID = B.STOCK_ID
				          AND C.DEL_YN = 'N'
				          AND D.DEL_YN = 'N'
				          AND D.YD_AID_WRK_YN = 'N') AS YD_TO_LOC_GUIDE_FNL
				 FROM  TB_YM_WRKBOOK A, TB_YM_WRKBOOKMTL B
				WHERE  A.YD_WBOOK_ID = B.YD_WBOOK_ID
				  AND  A.YD_WBOOK_ID = :V_YD_WBOOK_ID
				 */
				JDTORecordSet rsGrpResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getGrpSlabWrkBookMtl", logId, methodNm, "Grouping TB_YM_WRKBOOK 작업취소 대상재 조회");
				
				for (int i = 0; i < rsGrpResult.size(); ++i) {
					
					String oldWbookId = rsGrpResult.getRecord(i).getFieldString("YD_TAKE_OUT_CD");
					
					if(!"".equals(oldWbookId)){
						jrParam.setField("STOCK_ID"		, rsGrpResult.getRecord(i).getFieldString("STOCK_ID"));
						jrParam.setField("OLD_WBOOK_ID"	, rsGrpResult.getRecord(i).getFieldString("YD_TAKE_OUT_CD"));
						
						//grouping 작업예약 복구
						/*com.inisteel.cim.ym.bslab.dao.BSlabDAO.updGrpCnclYnWrkBook
						UPDATE TB_YM_WRKBOOK
						   SET MODIFIER    = :V_MODIFIER
						      ,MOD_DDTT    = SYSDATE
						      ,DEL_YN      = 'N'
						      ,SCH_CNCL_YN = 'Y'
						 WHERE YD_WBOOK_ID = :V_OLD_WBOOK_ID
						   AND DEL_YN      = 'Y'
						 */
						commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updGrpCnclYnWrkBook", logId, methodNm, "Grouping TB_YM_WRKBOOK 복구");

						//grouping 작업예약재료 복구
						/*com.inisteel.cim.ym.bslab.dao.BSlabDAO.updGrpCnclYnWrkBookMtl
						UPDATE TB_YM_WRKBOOKMTL
						   SET MODIFIER    = :V_MODIFIER
						      ,MOD_DDTT    = SYSDATE
						      ,DEL_YN      = 'N'
						 WHERE YD_WBOOK_ID = :V_OLD_WBOOK_ID
						   AND STOCK_ID    = :V_STOCK_ID
						   AND DEL_YN      = 'Y'
						 */
						commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updGrpCnclYnWrkBookMtl", logId, methodNm, "Grouping TB_YM_WRKBOOKMtl 복구");
						
						//grouping 작업예약재료 삭제
						/*com.inisteel.cim.ym.bslab.dao.BSlabDAO.delGrpCnclYnWrkBookMtl
						DELETE TB_YM_WRKBOOKMTL
						 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
						   AND STOCK_ID = :V_STOCK_ID
						 */
						commDao.delete(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.delGrpCnclYnWrkBookMtl", logId, methodNm, "Grouping TB_YM_WRKBOOKMTL 삭제");
					}
					
				}
				commUtils.printLog(logId, "○○○ Grouping대상 작업예약 취소 END ○○○"+sYD_WBOOK_ID, "[info]");
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
	public JDTORecord updSlabMvStkWrkBook(GridData gdReq) throws DAOException {
		String methodNm = "이적작업예약등록[BSalbJspSeEJB.updSlabMvStkWrkBook] < " + gdReq.getNavigateValue();
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
			String ydWrkPlanCrn2 = commUtils.trim(gdReq.getParam("YD_WRK_PLAN_CRN2")); //야드지정크레인(멀티크레인일 경우 사용)
			String sCHARGE_LOT_NO_DIV_YN = commUtils.trim(gdReq.getParam("CHARGE_LOT_NO_DIV_YN")); //장입순번 분리여부
			
			
			String sTO_YD_BAY_GP = "";
			String sTO_SECT_GP   = "";  
			
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
//			else if ("".equals(ydWrkPlanCrn)) {
//				throw new Exception("지정된 크레인이 없습니다.");
//			}
			
			//크레인 작업불가 적치BED 조회 TB_YM_RULE : YM2021
			int gRowCnt = gdReq.getHeader("CHECK").getRowCount();
			for (int ii = 0; ii < gRowCnt; ii++) {
				String sSTACK_COL_BED_GP 		= commUtils.getValue(gdReq, "YM_STR_LOC"         , ii).substring(0, 8);//야드적치열BED구분(8자리)
			
				JDTORecord jrYmParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
				jrYmParam.setField("YD_EQP_ID"       , ydWrkPlanCrn); //크레인정보
				jrYmParam.setField("STACK_COL_BED_GP", sSTACK_COL_BED_GP); //적치위치
				JDTORecordSet ym2021Rule = commDao.select(jrYmParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.selectRuleYM2021Crn", logId, methodNm, "SLAB 크레인작업불가 적치금지 BED 조건조회");
				
				if (ym2021Rule.size() > 0) {
					throw new Exception("이적대상재의 위치가 ["+ydWrkPlanCrn.substring(4, 6)+"]크레인 작업불가 적치BED에 적치되어 있습니다.");
				}
				
				jrYmParam.setField("STACK_COL_GP", sSTACK_COL_GP); //적치위치
				JDTORecordSet ym2010Rule = commDao.select(jrYmParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.selectRuleYM2010Crn", logId, methodNm, "SLAB 크레인작업불가 적치열금지 조건조회");
				
				if (ym2010Rule.size() > 0) {
					throw new Exception("이적대상재의 위치가 ["+ydWrkPlanCrn.substring(4, 6)+"]크레인 작업불가 적치열에 적치되어 있습니다.");
				}
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

			//스케쥴코드
			String sCraneNo = ydWrkPlanCrn.substring(5, 6);
			
			if (ydBayGp.equals(ydAimBayGp)) {  
				
				//동내이적
				if (!"".equals(ydWrkPlanCrn)) { //지정크레인 유무
					ydSchCd = sSTACK_COL_GP.substring(0, 2) + "YD" + sCraneNo + "1MM";	
				} else {
					ydSchCd = sSTACK_COL_GP.substring(0, 2) + "YD11MM";
				}
				
				ydWrkPlanTcar = "";
				
			} else {
				
				if ("".equals(ydWrkPlanTcar)) {
					throw new Exception("To위치지정 동간이적 대차 정보가 없습니다.");
				}
				
				//동간이적
				if (!"".equals(ydWrkPlanCrn)) { //지정크레인 유무
					if ("1".equals(ydWrkPlanCrn.substring(5, 6))) {
						ydSchCd = sSTACK_COL_GP.substring(0, 2) + "TC11UM";
					} else if ("2".equals(ydWrkPlanCrn.substring(5, 6))) {
						ydSchCd = sSTACK_COL_GP.substring(0, 2) + "TC22UM";
					} else if ("3".equals(ydWrkPlanCrn.substring(5, 6))) {
						ydSchCd = sSTACK_COL_GP.substring(0, 2) + "TC33UM";
					} else {
						ydSchCd = sSTACK_COL_GP.substring(0, 2) + "TC11UM";
					}
				} else {
					ydSchCd = sSTACK_COL_GP.substring(0, 2) + "TC11UM";
				}
			} 
			
			/**********************************************************
			 * 2. 동간이적일 경우 대차기준 변경 및 대차스케줄 수정
			 **********************************************************/
			if (!ydBayGp.equals(ydAimBayGp)) {  
				
				jrParam.setField("EQUIP_GP"        , ydWrkPlanTcar); //대차번호
				jrParam.setField("YD_WRK_PLAN_TCAR", ydWrkPlanTcar); //대차번호
				JDTORecordSet equipInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.selectEquipInfo");
				
				if (equipInfo.size() <= 0) {
					throw new Exception("지정된 대차설비 정보가 없습니다.");
				} 
				
				String sL_BAY              = commUtils.trim(equipInfo.getRecord(0).getFieldString("L_BAY"             )); //상차동
				String sU_BAY              = commUtils.trim(equipInfo.getRecord(0).getFieldString("U_BAY"             )); //하차동
				String sWPROG_STAT         = commUtils.trim(equipInfo.getRecord(0).getFieldString("WPROG_STAT"        )); //작업진행상태
				String sCURR_STOP_LOC      = commUtils.trim(equipInfo.getRecord(0).getFieldString("CURR_STOP_LOC"     )); //현재 정지 위치  
				String sCARLOAD_STOP_LOC   = commUtils.trim(equipInfo.getRecord(0).getFieldString("CARLOAD_STOP_LO"   )); //상차 정지 위치  
				String sCARUNLOAD_STOP_LOC = commUtils.trim(equipInfo.getRecord(0).getFieldString("CARUNLOAD_STOP_LOC")); //하차 정지 위치  
				String sCARLD_SCH_CD	   = commUtils.trim(equipInfo.getRecord(0).getFieldString("CARLD_SCH_CD"	  )); //상차스케줄코드
				String sCARUD_SCH_CD	   = commUtils.trim(equipInfo.getRecord(0).getFieldString("CARUD_SCH_CD"	  )); //하차스케줄코드
				
				String sCURR_BAY = sCURR_STOP_LOC.substring(1, 2); //대차현재위치동
				
				/*
				SELECT COUNT(*) AS WRK_CNT
				  FROM TB_YM_WRKBOOK
				 WHERE YD_GP  = '2'
				   AND DEL_YN = 'N'
				   AND YD_WRK_PLAN_TCAR = :V_YD_WRK_PLAN_TCAR
				 */
				JDTORecordSet jrWbCnt = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getWrkBookCntByTcar", logId, methodNm, "대차별 작업예약 개수");
				
				int nWRK_CNT = 99;
				if (jrWbCnt.size() > 0) {
					nWRK_CNT = Integer.parseInt(jrWbCnt.getRecord(0).getFieldString("WRK_CNT"));
				}
				
				if (nWRK_CNT == 0) {
					
					if(!sCARLD_SCH_CD.equals(sCARUD_SCH_CD)) {
						//상차스케줄과 하차스케줄이 다를 경우만
						
						//해당 대차의 작업이 존재 하지 않을 경우 대차기준 상하차동 수정 
						/*
						UPDATE TB_YM_EQUIP
						   SET MODIFIER          = :V_MODIFIER
						     , MOD_DDTT          = SYSDATE
						     , CARLOAD_STOP_LOC  =(SELECT ITEM 
						                             FROM TB_YM_RULE
						                            WHERE REPR_CD_GP = 'TCAR01'
						                              AND DEL_YN     = 'N'
						                              AND DTL_ITM1   = :V_EQUIP_GP
						                              AND DTL_ITM2   = :V_CARLOAD_STOP_LOC
						                              AND ROWNUM     = 1)
						     , CARUNLOAD_STOP_LOC =(SELECT ITEM 
						                             FROM TB_YM_RULE
						                            WHERE REPR_CD_GP = 'TCAR01'
						                              AND DEL_YN     = 'N'
						                              AND DTL_ITM1   = :V_EQUIP_GP
						                              AND DTL_ITM2   = :V_CARUNLOAD_STOP_LOC
						                              AND ROWNUM     = 1)                             
						 WHERE YD_GP = '2'
						   AND EQUIP_GP = :V_EQUIP_GP
						 */
						jrParam.setField("CARLOAD_STOP_LOC"  , ydBayGp); // 상차동
						jrParam.setField("CARUNLOAD_STOP_LOC", ydAimBayGp); // 하차동
						commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updTcarInfo");
					}
					
					//대차가 현재동에 있으면 대차스케줄 상/하차위치 수정
					if (ydBayGp.equals(sCURR_BAY)) {
						/*
						UPDATE TB_YM_TCARSCH
						   SET YD_CARLD_STOP_LOC =(SELECT ITEM 
						                             FROM TB_YM_RULE
						                            WHERE REPR_CD_GP = 'TCAR01'
						                              AND DEL_YN     = 'N'
						                              AND DTL_ITM1   = :V_EQUIP_GP
						                              AND DTL_ITM2   = :V_YD_CARLD_STOP_LOC
						                              AND ROWNUM     = 1)
						     , YD_CARUD_STOP_LOC =(SELECT ITEM 
						                             FROM TB_YM_RULE
						                            WHERE REPR_CD_GP = 'TCAR01'
						                              AND DEL_YN     = 'N'
						                              AND DTL_ITM1   = :V_EQUIP_GP
						                              AND DTL_ITM2   = :V_YD_CARUD_STOP_LOC
						                              AND ROWNUM     = 1)       
						     , MODIFIER          = :V_MODIFIER
						     , MOD_DDTT          = SYSDATE
						 WHERE DEL_YN    = 'N'
						   AND YD_EQP_ID = :V_YD_EQP_ID
						 */
						jrParam.setField("YD_CARLD_STOP_LOC", ydBayGp); //
						jrParam.setField("YD_CARUD_STOP_LOC", ydAimBayGp); //
						jrParam.setField("YD_EQP_ID"        , ydWrkPlanTcar); //대차번호
						commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updTcarSch");
					}
				}
			}			
			
			/**********************************************************
			* 3. 이적 작업예약 등록
			**********************************************************/
			jrParam.setField("ARR_STOCK_ID", stlNos    ); //재료번호들
			jrParam.setField("STACK_COL_GP", sSTACK_COL_GP); //야드적치열구분
			
			//SLAB 이적대상재 조회 장입순번 고도화 적용여부
			String sAPP100_Z02_YN = ymComm.BCoilApplyYn("APP100","2","Z02_YN");
			JDTORecordSet jsWbMtl = null;
			if("Y".equals(sAPP100_Z02_YN)) {
				//한 BED안에 서로 다른 장입번호가 있을경우 장입순번을 합친순으로 정렬
				//작업예약 대상재료 조회
				/*
				--이적작업예약등록 이적재료조회
				SELECT SL.STOCK_ID
				     , SL.STACK_COL_GP
				     , SL.STACK_BED_GP
				     , SL.STACK_LAYER_GP
				     , SC.SLAB_WT
				     , SC.SLAB_T
				     , SC.SLAB_W
				     , SC.SLAB_LEN
				     , TO_CHAR(SC.SLAB_T)||' X '||TO_CHAR(SC.SLAB_W,'FM9,999') || ' X '||TO_CHAR(SC.SLAB_LEN,'FM99,999') AS MTL_SIZE
				     , SL.STACK_COL_GP||SL.STACK_BED_GP||'-'||SL.STACK_LAYER_GP AS YM_STR_LOC
				     , MIN(NVL(CHARGE_LOT_NO, '999999')) OVER(PARTITION BY STACK_COL_GP, STACK_BED_GP) AS MIN_CHARGE_LOT_NO --적치bed별 장입순번 MIN
				     , MAX(NVL(CHARGE_LOT_NO, '999999')) OVER(PARTITION BY STACK_COL_GP, STACK_BED_GP) AS MAX_CHARGE_LOT_NO --적치bed별 장입순번 MAX
				  FROM TB_YM_STACKLAYER SL
				     , TB_YM_STOCK      ST
				     ,(SELECT REGEXP_SUBSTR(SSTL_NOS, '[^,]+', 1, LEVEL) AS STOCK_ID
				         FROM (SELECT :V_ARR_STOCK_ID AS SSTL_NOS FROM DUAL)
				      CONNECT BY REGEXP_SUBSTR(SSTL_NOS, '[^,]+', 1, LEVEL) IS NOT NULL) SN  -- 입력 배열
				     , VW_YD_SLABCOMM   SC
				 WHERE SL.STOCK_ID = SN.STOCK_ID
				   AND SL.STOCK_ID = ST.STOCK_ID
				   AND SL.STOCK_ID = SC.SLAB_NO
				   AND SL.STACK_COL_GP LIKE SUBSTR(:V_STACK_COL_GP,1,2)||'%'
				   AND SL.STACK_LAYER_STAT = 'C'
				   AND SL.STOCK_ID NOT IN (SELECT STOCK_ID 
				                             FROM TB_YM_WRKBOOK    WB
				                                , TB_YM_WRKBOOKMTL WM
				                            WHERE WB.DEL_YN      = 'N' 
				                              AND WM.DEL_YN      = 'N' 
				                              AND WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
				                              AND WB.YD_GP       = '2'
				                          )
				 ORDER BY MIN_CHARGE_LOT_NO||MAX_CHARGE_LOT_NO DESC --동일BED 장입순번 MIN,MAX 역순으로 정렬
				        , SL.STACK_COL_GP
				        , SL.STACK_BED_GP
				        , SL.STACK_LAYER_GP DESC
				 */
				jsWbMtl = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getMvStkWrkBookMtlNew", logId, methodNm, "재료번호로 조회");
			}else{
				//작업예약 대상재료 조회
				/*
				SELECT SL.STOCK_ID
				     , SL.STACK_COL_GP
				     , SL.STACK_BED_GP
				     , SL.STACK_LAYER_GP
				     , SC.SLAB_WT
				     , SC.SLAB_T
				     , SC.SLAB_W
				     , SC.SLAB_LEN
				     , TO_CHAR(SC.SLAB_T)||' X '||TO_CHAR(SC.SLAB_W,'FM9,999') || ' X '||TO_CHAR(SC.SLAB_LEN,'FM99,999') AS MTL_SIZE
				     , SL.STACK_COL_GP||SL.STACK_BED_GP||'-'||SL.STACK_LAYER_GP AS YM_STR_LOC
				     , MIN(NVL(CHARGE_LOT_NO, '999999')) OVER(PARTITION BY STACK_COL_GP, STACK_BED_GP) AS MIN_CHARGE_LOT_NO
				  FROM TB_YM_STACKLAYER SL
				     , TB_YM_STOCK      ST
				     ,(SELECT REGEXP_SUBSTR(SSTL_NOS, '[^,]+', 1, LEVEL) AS STOCK_ID
				         FROM (SELECT :V_ARR_STOCK_ID AS SSTL_NOS FROM DUAL)
				      CONNECT BY REGEXP_SUBSTR(SSTL_NOS, '[^,]+', 1, LEVEL) IS NOT NULL) SN  -- 입력 배열
				     , VW_YD_SLABCOMM   SC
				 WHERE SL.STOCK_ID = SN.STOCK_ID
				   AND SL.STOCK_ID = ST.STOCK_ID
				   AND SL.STOCK_ID = SC.SLAB_NO
				   AND SL.STACK_COL_GP LIKE SUBSTR(:V_STACK_COL_GP,1,2)||'%'
				   AND SL.STACK_LAYER_STAT = 'C'
				 ORDER BY MIN_CHARGE_LOT_NO
				        , SL.STACK_COL_GP
				        , SL.STACK_BED_GP
				        , SL.STACK_LAYER_GP DESC
				 */
				jsWbMtl = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getMvStkWrkBookMtl", logId, methodNm, "재료번호로 조회");

			}
			
			int rowCnt = jsWbMtl.size();

			if (rowCnt <= 0) {
				throw new Exception("이적 재료 정보가 없습니다.");
			}
			
			jrParam.setField("YD_SCH_CD"           , ydSchCd              ); //야드스케쥴코드
			jrParam.setField("YD_AIM_BAY_GP"       , ydAimBayGp           ); //야드목표동구분
			jrParam.setField("YD_TO_LOC_GUIDE"     , ydToLocGuide         ); //야드To위치Guide
			jrParam.setField("YD_WRK_PLAN_TCAR"    , ydWrkPlanTcar        ); //야드작업계획대차
			jrParam.setField("YD_WRK_PLAN_CRN"     , ydWrkPlanCrn         ); //야드작업계획크레인
			jrParam.setField("CHARGE_LOT_NO_DIV_YN", sCHARGE_LOT_NO_DIV_YN); //장입순번 분리여부
			jrParam.setField("YD_WRK_PLAN_CRN2"    , ydWrkPlanCrn2        ); //야드작업계획크레인
			
			//작업예약등록
			jsMsg.addRecord(this.insMvstkWrkBook(jrParam, jsWbMtl));
			
			/**********************************************************
			* 4. 대차작업이 있으면 공대차출발지시 처리
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
				JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcarSchLevWoSlab", logId, methodNm, "공대차출발지시 조회");
				
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
					jrParam.setField("OPR_YN"   , "Y"          ); //화면에서 작업예약 생성
					
					jrRtn = ymComm.trtTcarSchLevWo_Slab(jrParam);
				} else {
					commUtils.printLog(logId, "대차[" + ydWrkPlanTcar + "] 공대차출발지시 불가 : " + msgTcar, "SL");
				}
			}

			/**********************************************************
			* 4. 동내이적(대차작업이 없음)작업 크레인별 첫번째 스케줄 전송
			**********************************************************/
			// 202171030 현업요청 스케줄 기동 금지
			//jrRtn = commUtils.addSndData(jrRtn, this.setCrnSchMsg(jsMsg, logId, methodNm)); 
			
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
		String methodNm = "크레인스케줄전문정리[BSlabJspSeEJB.setCrnSchMsg] < " + mthdNm;

		try {
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam("A", jsMsg);
			//Return Value
			JDTORecord jrRtn = null;

			if (!commUtils.isEmpty(jsMsg)) {
				String ydEqpId   = ""; //야드설비ID(크레인)
				String ydEqpStat = ""; //야드설비상태
				String sYD_SCH_CD   = "";
				String sYD_WBOOK_ID = "";
				boolean fstYn = false; //동일크레인에서 첫번째 여부
				//DAO Parameter - Log ID, Method, 수정자 Set
				JDTORecord jrParam = commUtils.getParam(logId, methodNm, "");
				JDTORecord jrRow = null;
				JDTORecordSet jsChk = null;

				int rowCnt = jsMsg.size();

				for (int ii = rowCnt - 1; ii >= 0; ii--) {
					jrRow = jsMsg.getRecord(ii);
					
					if (!"".equals(commUtils.trim(jrRow.getFieldString("YD_WRK_PLAN_TCAR")))) {
						//야드작업계획대차가 있으면 대차상차 크레인스케줄이므로 무조건 스케줄기동
						ydEqpId      = commUtils.trim(jrRow.getFieldString("YD_EQP_ID"));
						sYD_SCH_CD   = commUtils.trim(jrRow.getFieldString("YD_SCH_CD"));
						sYD_WBOOK_ID = commUtils.trim(jrRow.getFieldString("YD_WBOOK_ID"));
						
//					 	jrRtn = commUtils.addSndData(jrRtn, ymComm.getCrnSchMsg(jrRow));
						
					} else {

						fstYn = true;
						ydEqpId      = commUtils.trim(jrRow.getFieldString("YD_EQP_ID"));
						sYD_SCH_CD   = commUtils.trim(jrRow.getFieldString("YD_SCH_CD"));
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

							if ("W".equals(ydEqpStat)) {
								//크레인이 작업대기 상태이면 크레인스케줄 전송
								jrRtn = commUtils.addSndData(jrRtn, ymComm.getCrnSchMsg(jrRow));
							}
							
//							jrParam.setField("YD_SCH_CD", sYD_SCH_CD);
							/*
							SELECT DECODE(COUNT(*), 0, 'N', 'Y') AS IS_SCH
							  FROM TB_YM_CRNSCH
							 WHERE 1=1
							   AND DEL_YN = 'N'
							   AND YD_EQP_ID = :V_YD_EQP_ID
							   AND YD_SCH_CD = :V_YD_SCH_CD
							 */
//							JDTORecordSet rst = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getIsSchKind");
//							commUtils.printLog(logId, "■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■", "[info]");
//							commUtils.printLog(logId, "■■■■■"+ sYD_WBOOK_ID + " " + ydEqpId + " "+sYD_SCH_CD, "[info]");
//							commUtils.printLog(logId, "■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■", "[info]");
//							if ("N".equals(rst.getRecord(0).getFieldString("IS_SCH"))) {
//								//크레인스케줄 전송YMYMJ302
//								jrRtn = commUtils.addSndData(jrRtn, ymComm.getCrnSchMsg(jrRow));
//							}
							
						} // fstYn
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
	 *      [A] 오퍼레이션명 : 이적작업예약등록
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord insMvstkWrkBook(JDTORecord jrParam, JDTORecordSet jsWbMtl) throws DAOException {
		String methodNm = "이적작업예약등록[BSlabJspSeEJB.insMvstkWrkBook] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			String ydSchCd       = commUtils.trim(jrParam.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
			String ydAimBayGp    = commUtils.trim(jrParam.getFieldString("YD_AIM_BAY_GP"   )); //야드목표동구분
			String ydToLocGuide  = commUtils.trim(jrParam.getFieldString("YD_TO_LOC_GUIDE" )); //야드To위치Guide
			String ydWrkPlanTcar = commUtils.trim(jrParam.getFieldString("YD_WRK_PLAN_TCAR")); //야드작업계획대차
			String modifier      = commUtils.trim(jrParam.getFieldString("MODIFIER"        )); //수정자
			String ydWrkPlanCrn  = commUtils.trim(jrParam.getFieldString("YD_WRK_PLAN_CRN" )); //야드작업계획크레인
			String ydWrkPlanCrn2 = commUtils.trim(jrParam.getFieldString("YD_WRK_PLAN_CRN2")); //야드작업계획크레인
			String sCHARGE_LOT_NO_DIV_YN = commUtils.trim(jrParam.getFieldString("CHARGE_LOT_NO_DIV_YN")); //장입순번 분리여부
			
			// 멀티크레인
			if (!"".equals(ydWrkPlanCrn)) {
				if ("7".equals(ydWrkPlanCrn.substring(5, 6))) {
					ydWrkPlanCrn  = ydWrkPlanCrn.substring(0, 5) + "1";
					ydWrkPlanCrn2 = ydWrkPlanCrn.substring(0, 5) + "2";
				} else if ("8".equals(ydWrkPlanCrn.substring(5, 6))) {
					ydWrkPlanCrn  = ydWrkPlanCrn.substring(0, 5) + "1";
					ydWrkPlanCrn2 = ydWrkPlanCrn.substring(0, 5) + "3";
				} else if ("9".equals(ydWrkPlanCrn.substring(5, 6))) {
					ydWrkPlanCrn  = ydWrkPlanCrn.substring(0, 5) + "2";
					ydWrkPlanCrn2 = ydWrkPlanCrn.substring(0, 5) + "3";
				} 
			}
			
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
			String ydEqpId    = commUtils.trim(jrCrnSpec.getFieldString("YD_WRK_CRN"   ));	  //야드설비ID(크레인)
			String ydSchPrior = commUtils.trim(jrCrnSpec.getFieldString("YD_WRK_CRN_PRIOR")); //야드스케쥴우선순위
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
			
			/****************************************
			 * 0. 동간 이적의 경우 대차 매수 체크
			 ****************************************/
			int nSTACK_MAX_QNTY = 0;
			if (!"".equals(ydWrkPlanTcar)) {
				/*
				SELECT *
				  FROM TB_YM_EQUIP    
				 WHERE EQUIP_GP = :V_EQUIP_GP
				   AND DEL_YN   = 'N'
				 */
				jrParam.setField("EQUIP_GP" , ydWrkPlanTcar);
				JDTORecordSet tcarInfo =  commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getYmEqp");
				nSTACK_MAX_QNTY = Integer.parseInt(tcarInfo.getRecord(0).getFieldString("STACK_MAX_QNTY"));
			}
			
			/**********************************************************
			* 2. 크레인사양 분리
			**********************************************************/
			jrCrnSpec.setResultCode(logId);  	//Log ID
			jrCrnSpec.setResultMsg(methodNm);	//Log Method Name
			
			Vector vcLot = this.setCrnSpecSpr(jrCrnSpec, jsWbMtl);//, nSTACK_MAX_QNTY);

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
				
				jrParam.setField("YD_WRK_PLAN_CRN"   , ydWrkPlanCrn  ); //야드작업계획크레인
				jrParam.setField("YD_WRK_PLAN_CRN2"  , ydWrkPlanCrn2 ); //야드작업계획크레인2
				
				jrParam.setField("CHARGE_LOT_NO_DIV_YN", sCHARGE_LOT_NO_DIV_YN ); //야드작업계획크레인2

				commDao.insert(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.insWrkBook", logId, methodNm, "TB_YM_WRKBOOK");

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
	 *      [A] 오퍼레이션명 : 크레인사양분리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord jrCrnSpec
	 *      @param JDTORecordSet jsWrkMtl
	 *      @return Vector
	 *      @throws DAOException
	*/
	public Vector setCrnSpecSpr(JDTORecord jrCrnSpec, JDTORecordSet jsWrkMtl) throws DAOException {
		String methodNm = "크레인사양분리[BSlabJspSeEJB.setCrnSpecSpr] < " + jrCrnSpec.getResultMsg();
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

			int tmp = 0;
			
//			if (nSTACK_MAX_QNTY > 0) { //대차일경우
//				
//				commUtils.printLog(logId, "■ 동간이적(nSTACK_MAX_QNTY) : "+ nSTACK_MAX_QNTY, "[info]");
//				
//				for (int ii = 0; ii < rowCnt; ii++) {
//					jrRow = jsWrkMtl.getRecord(ii);
//					
//					sSTACK_COL_GP   = commUtils.trim(jrRow.getFieldString("STACK_COL_GP"));
//					sSTACK_BED_GP   = commUtils.trim(jrRow.getFieldString("STACK_BED_GP"));
//					
//					if (ii > 0) {
//						if (!(szCHK_STACK_COL_GP+szCHK_STACK_BED_GP).equals(sSTACK_COL_GP+sSTACK_BED_GP) || tmp == nSTACK_MAX_QNTY) {
//							//이전 Lot 추가
//							vcLot.add(jsLot);
//
//							jsLot = JDTORecordFactory.getInstance().createRecordSet("");
//							szCHK_STACK_COL_GP   = sSTACK_COL_GP;
//							szCHK_STACK_BED_GP   = sSTACK_BED_GP;
//							tmp = 0;
//						}
//					} else {
//						szCHK_STACK_COL_GP   = sSTACK_COL_GP;
//						szCHK_STACK_BED_GP   = sSTACK_BED_GP;
//					}
//					commUtils.printLog(logId, "■ tmp : "+ tmp, "[info]");
//					tmp++;
//					jsLot.addRecord(jrRow);
//				}
//			} else {
			
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
//			}
			
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
	 * SLAB  STACK_LAYER_ACTIVE_STAT  활성 비할성을 Toggle 처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updActiveStat(GridData gdReq) throws DAOException {
		String methodNm = "SLAB Walking Beam 조회[BSlabJspSeEJB.updActiveStat] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			//수정할 레코드 수
			String sActiveStat ="";
			String sActiveStatParm ="";
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				jrParam.setField("STACK_COL_GP" 	        ,commUtils.getValue(gdReq, "STACK_COL_GP", ii)); 
				jrParam.setField("STACK_BED_GP"	            ,commUtils.getValue(gdReq, "STACK_BED_GP", ii)); 
				jrParam.setField("STACK_LAYER_GP"	        ,commUtils.getValue(gdReq, "STACK_LAYER_GP", ii)); 
				
	    			
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getStackLayerInfoWithPk 
				SELECT *
				  FROM TB_YM_STACKLAYER
				 WHERE STACK_COL_GP  	= :V_STACK_COL_GP
				   AND STACK_BED_GP	= :V_STACK_BED_GP 
				   AND STACK_LAYER_GP	= :V_STACK_LAYER_GP 
		    	*/
				
				JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getStackLayerInfoWithPk", logId, methodNm, "Crane UP실적 적치단정보조회");
				
				if (jsChk != null &&  jsChk.size() > 0) {
					
					sActiveStat 	= commUtils.trim(jsChk.getRecord(0).getFieldString("STACK_LAYER_ACTIVE_STAT")); // 적치단활성화 상태
					
					sActiveStatParm = sActiveStat.equals("E") ?   "C" :   
						              sActiveStat.equals("C") ?   "E" : sActiveStat ;  //"활성 비할성을 Toggle";
					
					jrParam.setField("STACK_LAYER_ACTIVE_STAT" 	        ,sActiveStatParm); 
					
					/* com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateCraneStackLayerActivStat
					UPDATE TB_YM_STACKLAYER
					   SET STACK_LAYER_ACTIVE_STAT = :V_STACK_LAYER_ACTIVE_STAT
					     , MODIFIER   = 'SYSTEM'
					     , MOD_DDTT   = SYSDATE     
					 WHERE STACK_COL_GP   = :V_STACK_COL_GP 
					   AND STACK_BED_GP   = :V_STACK_BED_GP 
					   AND STACK_LAYER_GP = :V_STACK_LAYER_GP 
					
					 */
					
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateCraneStackLayerActivStat", logId, methodNm, "LayerActStat변경");
					
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
	 * B열연 SLAB 설비 스케줄사용여부 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updEquipUseSch(GridData gdReq) throws DAOException {
		String methodNm = "B열연 SLAB 설비 스케줄사용여부 변경[BSlabJspSeEJB.updEquipUseSch] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			jrParam.setField("HMI_STAT"		, gdReq.getParam("HMI_STAT"));
			jrParam.setField("EQUIP_GP"		, gdReq.getParam("EQUIP_GP")); 
			
			commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updEquipHmiStat", logId, methodNm, "B열연 SLAB 설비 스케줄사용여부 변경");
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updEquipUseSch
	
	/**
	 * B열연 SLAB 선작업지시사용여부 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updEquipBefWork(GridData gdReq) throws DAOException {
		String methodNm = "B열연 SLAB 선작업지시사용여부 변경[BSlabJspSeEJB.updEquipBefWork] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			jrParam.setField("WORK_MODE"	, gdReq.getParam("WORK_MODE"));
			jrParam.setField("EQUIP_GP"		, gdReq.getParam("EQUIP_GP")); 
			
			commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updEquipBefWork", logId, methodNm, "B열연 SLAB 선작업지시사용여부 변경");
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updEquipBefWork
	
	//2017년 9월 5일 화요일 - CTC1, 2, 4(A동, C동)
	
	/** 2017년 9월 5일 화요일 CTC보급요구 기능 - SCP
	 * A동 #1, 2, 4 CTC보급요구  스케줄사용여부 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updEquipUseSchAC(GridData gdReq) throws DAOException {
		String methodNm = "A, C동 CTC보급요구 스케줄사용여부 변경[BSlabJspSeEJB.updEquipUseSchAC] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			//A동 01 => 2ACT01 (EQUIP_GP) => radio A =>	ExecObj.SetParam("EQUIP_GP" , "2ACT01")
			//A동 02 => 2ACT02 (EQUIP_GP) => radio A =>  ExecObj.SetParam("EQUIP_GP" , "2ACT02")
			
			//C동 04 => 2CCT04 (EQUIP_GP) => radio C =>  ExecObj.SetParam("EQUIP_GP" , "2CCT04")
			/*
			 	UPDATE TB_YM_EQUIP
			       SET HMI_STAT = :V_HMI_STAT,
			           MODIFIER = :V_MODIFIER,
			           MOD_DDTT = SYSDATE
			     WHERE EQUIP_GP = :V_EQUIP_GP 
			 */
			/*jrParam.setField("HMI_STAT"		, gdReq.getParam("HMI_STAT"));
			jrParam.setField("EQUIP_GP"		, gdReq.getParam("EQUIP_GP1")); 
			commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updEquipHmiStatListAC", logId, methodNm, "A CTC1 보급요구 스케줄사용여부 변경");
			
			jrParam.setField("EQUIP_GP"		, gdReq.getParam("EQUIP_GP2")); 
			commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updEquipHmiStatListAC", logId, methodNm, "A CTC2 보급요구 스케줄사용여부 변경");
			*/
			
			String sFlag = gdReq.getParam("flag");// A동 or C동
			
			if("A".equals(sFlag)){
				jrParam.setField("HMI_STAT"		, gdReq.getParam("HMI_STAT"));
				jrParam.setField("EQUIP_GP"		, gdReq.getParam("EQUIP_GP1")); 
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updEquipHmiStatListAC", logId, methodNm, "A CTC1 보급요구 스케줄사용여부 변경");
				
				jrParam.setField("EQUIP_GP"		, gdReq.getParam("EQUIP_GP2")); 
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updEquipHmiStatListAC", logId, methodNm, "A CTC1 보급요구 스케줄사용여부 변경");
			}else{
				jrParam.setField("HMI_STAT"		, gdReq.getParam("HMI_STAT"));
				jrParam.setField("EQUIP_GP"		, gdReq.getParam("EQUIP_GP")); 
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updEquipHmiStatListAC", logId, methodNm, "A CTC1 보급요구 스케줄사용여부 변경");
			}
				
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} 
	
	
	/** 2017년 9월 5일 화요일 CTC보급요구 기능 - SCP
	 * A동 #1, 2, 4 CTC보급요구 선작업지시사용여부 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updEquipBefWorkAC(GridData gdReq) throws DAOException {
		String methodNm = "A, C동  CTC보급요구 선작업지시사용여부 변경[BSlabJspSeEJB.updEquipBefWorkAC] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			
			String sFlagWrk = gdReq.getParam("flagWkr");// A동 or C동
			
			if("A".equals(sFlagWrk)){
				jrParam.setField("WORK_MODE"		, gdReq.getParam("WORK_MODE"));
				jrParam.setField("EQUIP_GP"		    , gdReq.getParam("EQUIP_GP01")); 
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updEquipBefWorkListAC", logId, methodNm, "A, C동  CTC보급요구 선작업지시사용여부 변경");
				
				jrParam.setField("EQUIP_GP"		    , gdReq.getParam("EQUIP_GP02")); 
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updEquipBefWorkListAC", logId, methodNm, "A, C동  CTC보급요구 선작업지시사용여부 변경");
			}else{
				jrParam.setField("WORK_MODE"		, gdReq.getParam("WORK_MODE"));
				jrParam.setField("EQUIP_GP"		    , gdReq.getParam("EQUIP_GP")); 
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updEquipBefWorkListAC", logId, methodNm, "A, C동  CTC보급요구 선작업지시사용여부 변경");
			}
			
			/*jrParam.setField("WORK_MODE"	, gdReq.getParam("WORK_MODE"));
			jrParam.setField("EQUIP_GP"		, gdReq.getParam("EQUIP_GP")); 
			
			commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updEquipBefWorkListAC", logId, methodNm, "A, C동  CTC보급요구 선작업지시사용여부 변경");*/
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	/**
	 * 산적위치수정 - 저장품 생성
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord insStockInfo(JDTORecord rcvMsg) throws DAOException {
		
		String methodNm = "산적위치수정-저장품생성[BSlabJspSeEJB.insStockInfo] <" + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+");
			
			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

			String sYD_GP       = rcvMsg.getFieldString("YD_GP");
			String sSTOCK_ID    = rcvMsg.getFieldString("STOCK_ID");

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(rcvMsg.getFieldString("MODIFIER")));
			jrParam.setField("YD_GP"	      , sYD_GP   );
			jrParam.setField("STOCK_ID"       , sSTOCK_ID);		
			jrParam.setField("STOCK_MOVE_TERM", bSlabComm.getStockMoveTerm(sSTOCK_ID));

			/*
			MERGE INTO TB_YM_STOCK ST USING (
			    SELECT :V_STOCK_ID      AS STOCK_ID    --재료번호
			         , :V_MODIFIER      AS MODIFIER    --수정자
			         , SYSDATE          AS MOD_DDTT    --수정일시
			         , 'N'              AS DEL_YN      --삭제유무
			         , :V_STOCK_MOVE_TERM AS STOCK_MOVE_TERM
			      FROM DUAL
			) DD ON ( ST.STOCK_ID = DD.STOCK_ID)
			WHEN NOT MATCHED THEN
			    INSERT (
			           STOCK_ID     , STOCK_ITEM    , STOCK_MOVE_TERM  --저장품이동조건
			         , REGISTER     , REG_DDTT      , MODIFIER         -- 'SYSTEM'
			         , MOD_DDTT     , DEL_YN        , YD_AIM_RT_GP)
			    VALUES (:V_STOCK_ID , 'SM'          , DD.STOCK_MOVE_TERM
			         , DD.MODIFIER  , DD.MOD_DDTT   , DD.MODIFIER
			         , DD.MOD_DDTT  , DD.DEL_YN 	, DD.STOCK_MOVE_TERM) 
			WHEN MATCHED THEN UPDATE SET
			     ST.STOCK_ITEM      = 'SM'
			   , ST.STOCK_MOVE_TERM = DD.STOCK_MOVE_TERM
			   , ST.MODIFIER        = DD.MODIFIER
			   , ST.MOD_DDTT        = DD.MOD_DDTT
			   , ST.DEL_YN          = DD.DEL_YN 
			 */
			commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.insertStockTransInfo");
			
			commUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of insStockInfo	
	
	
	/**
	 * 산적위치수정 - 수정 TODO
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord updStkLoc(GridData gdReq) throws DAOException {
		
		String methodNm = "산적위치수정-수정[BSlabJspSeEJB.updStkLoc] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		JDTORecordSet dmRc = null;
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			String sMODIFIER = StringHelper.evl(commUtils.trim(gdReq.getParam("userid")), "SYSTEM");
			
			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();

			String sYD_GP       = gdReq.getParam("YD_GP");
			String sYD_BAY_GP   = gdReq.getParam("YD_BAY_GP"); 

			String sSTOCK_ID    = gdReq.getParam("STOCK_ID"  ).toUpperCase();
			String sFROM_ADDR   = gdReq.getParam("FROM_ADDR" ).toUpperCase(); //from 위치
			String sYD_STR_LOC  = gdReq.getParam("YD_STR_LOC").toUpperCase(); //TO 위치
			String sFTMV_BKUP   = ""; //gdReq.getParam("FTMV_BKUP");  //이송유무
			
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
			
			String sPUT_BETWEEN_YN   = gdReq.getParam("PUT_BETWEEN_YN"  ); //끼워넣기여부
			
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setField("YD_GP"	 , sYD_GP   );
			jrParam.setField("STOCK_ID"  , sSTOCK_ID);
			jrParam.setField("PUT_BETWEEN_YN", sPUT_BETWEEN_YN);
			
			//String sYD_STOCK_YN = 
			String errMsg   = "";
			
			// 저장품 생성
			if ("N".equals(sYD_STOCK_YN)) {
				EJBConnector ejbConnS = new EJBConnector("default", "BSlabJspSeEJB", this);
				jrRtn = (JDTORecord)ejbConnS.trx("insStockInfo", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			}
			
			/**************************************************
			 *** FROM위치와 TO위치가 같은 경우 종료 
			 *** (저장품이 사라졌을 경우 생성후 종료)
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
			JDTORecordSet rsn = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getStackLayerInfo");
			
			String sToStockId 	= "";
			String sToStat   	= "";
			String sToAtiveStat = "";
			String sToLayerComm = "";
			
			if (rsn.size() > 0) {
				sToStockId 	 = StringHelper.evl(rsn.getRecord(0).getFieldString("STOCK_ID"), "");
				sToStat   	 = StringHelper.evl(rsn.getRecord(0).getFieldString("STACK_LAYER_STAT"), "");
				sToAtiveStat = StringHelper.evl(rsn.getRecord(0).getFieldString("STACK_LAYER_ACTIVE_STAT"), "");
				sToLayerComm = StringHelper.evl(rsn.getRecord(0).getFieldString("STACK_LAYER_COMMENTS"), "").trim();
			}
			
			/************************************************
			 ******  SLAB 산적위치 수정  ********************
			 ************************************************/
			JDTORecord jparam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jparam.setField("STOCK_ID"      , sSTOCK_ID      ); //저장품
			jparam.setField("FROM_ADDR"     , sFROM_ADDR     ); //from위치
			jparam.setField("YD_STR_LOC"    , sYD_STR_LOC    ); //to위치
			jparam.setField("FTMV_BKUP"     , sFTMV_BKUP     ); //이송백업여부
			jparam.setField("PUT_BETWEEN_YN", sPUT_BETWEEN_YN);
			
			// SLAB 산적위치 수정 메소드(트랜젝션 분리 작업)
			commUtils.printLog(logId, "SLAB 산적위치 수정 START", "[INFO]+");
			
			EJBConnector ejbConn1 = new EJBConnector("default", "BSlabJspSeEJB", this);
			jrRtn = (JDTORecord)ejbConn1.trx("changeSlabLocationInfo", new Class[] { JDTORecord.class }, new Object[] { jparam });
			
			commUtils.printLog(logId, "SLAB 산적위치 수정 END", "[INFO]-");
			
			/************************************************
			 ** 이송백업 START
			 ************************************************/
			/*
			SELECT CASE WHEN COUNT(*) > 0 THEN 'Y' ELSE 'N' END AS IS_FTMV_BKUP
			  FROM USRPTA.TB_PT_STLFRTOMOVE A
			 WHERE FRTOMOVE_STAT_CD ='3'
			   AND ARR_WLOC_CD ||'' IN ('D3Y43')
			   AND SPOS_WLOC_CD||'' NOT IN ('D3Y43')
			   AND TRANSWORD_SEQNO = (SELECT MAX(TRANSWORD_SEQNO)
			                            FROM USRPTA.TB_PT_STLFRTOMOVE B
			                           WHERE B.STL_NO = A.STL_NO
			                             AND ROWNUM <= 1)
			   AND A.STL_NO = :V_STOCK_ID
			 */
			jrParam.setField("STOCK_ID"  , sSTOCK_ID);
			JDTORecordSet rsIsFtmvBkup = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getIsFtmvBkup");
			if (rsIsFtmvBkup.size() > 0) {
				sFTMV_BKUP = rsIsFtmvBkup.getRecord(0).getFieldString("IS_FTMV_BKUP");
			}
			
			if ("Y".equals(sFTMV_BKUP)) { 
				commUtils.printLog(logId, "이송백업 실적처리 START", "[INFO]+");
				JDTORecord tcRecord = JDTORecordFactory.getInstance().create(); 
	     
			    /*********************
			     * 실적BACKUP처리 CALL
			     *********************/
			    tcRecord.setField("STOCK_ID", sSTOCK_ID);
			    tcRecord.setField("YD_LOC"  , sYD_STR_LOC    ); //to위치
			    
				//Slab공통 테이블 업데이트
				EJBConnector ejbConnPT = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
				ejbConnPT.trx("UpdSlabComLoc", new Class[] { JDTORecord.class }, new Object[] { tcRecord });
				
				
				//======================================================
				// 저장품제원 : 슬라브야드L2로 송신(YMA8L002)
				//======================================================
				// 산적위치 수정시 해당 번지 정보를 전부 L2로 송신하므로 따로 전문 송신 할 필요가 없다.
/*				commUtils.printLog(logId, "YMA8L002 JMS전송", "[INFO]");
				*//******************************************************************************//*
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setField("TC_CD"          , "YMA8L002");
				jrYdMsg.setField("YD_INFO_SYNC_CD", "5");
				jrYdMsg.setField("STOCK_ID"       , sSTOCK_ID);
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L002", jrYdMsg));
				*//*******************************************************************************//*
				*/
			    //저장품 이동조건 업데이트 
			    //JDTORecord rVal = this.getSlabCurrProgCd(sSTOCK_ID);
				JDTORecord rVal = ymComm.getSlabYdAimRtGp(jrParam);
			    String sYD_AIM_RT_GP  = rVal.getFieldString("YD_AIM_RT_GP");
			    /*
				UPDATE TB_YM_STOCK
				   SET STOCK_MOVE_TERM = :V_STOCK_MOVE_TERM
				     , YD_AIM_RT_GP    = :V_YD_AIM_RT_GP
				     , MOD_DDTT        = SYSDATE
				     , MODIFIER        = :V_MODIFIER
				     , DEL_YN          = 'N'
				 WHERE STOCK_ID = :V_STOCK_ID
			     */
			    tcRecord.setField("STOCK_MOVE_TERM", sYD_AIM_RT_GP);
			    tcRecord.setField("YD_AIM_RT_GP"   , sYD_AIM_RT_GP);
			    tcRecord.setField("MODIFIER"       , sMODIFIER);
			    tcRecord.setField("STOCK_ID"       , sSTOCK_ID);
			    commDao.update(tcRecord, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updYdStock");
			    
			    
			    tcRecord.setField("SLAB_NO", sSTOCK_ID);
			    JDTORecord stlRecord = commDao.select(tcRecord, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getSLABCOMM").getRecord(0);
			    String sSTL_APPEAR_GP =StringHelper.evl(stlRecord.getFieldString("STL_APPEAR_GP"), "");
			    
			    if (!sSTL_APPEAR_GP.equals("Y")) {
					
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
					
				    //슬라브소재 이송완료실적(YDPTJ001)
					JDTORecord tcRecord2 = JDTORecordFactory.getInstance().create();

					tcRecord2.setField("JMS_TC_CD"         , "YDPTJ001");
					tcRecord2.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14());
					
				    tcRecord2.setField("STL_NO"             , StringHelper.evl(stlRecord.getFieldString("SLAB_NO"), ""));
				    tcRecord2.setField("ORD_NO"             , StringHelper.evl(stlRecord.getFieldString("ORD_NO"), ""));// 주문번호
				    tcRecord2.setField("ORD_DTL"            , StringHelper.evl(stlRecord.getFieldString("ORD_DTL"), ""));// 주문행번
				    tcRecord2.setField("PLNT_PROC_CD"       , StringHelper.evl(stlRecord.getFieldString("PLNT_PROC_CD"), ""));// 공장공정코드
				    tcRecord2.setField("STL_APPEAR_GP"      , StringHelper.evl(stlRecord.getFieldString("STL_APPEAR_GP"), ""));// 재료외형구분
				    tcRecord2.setField("CURR_PROG_CD"       , StringHelper.evl(stlRecord.getFieldString("CURR_PROG_CD"), ""));// 현재진도코드
				    tcRecord2.setField("ORD_YEOJAE_GP"      , StringHelper.evl(stlRecord.getFieldString("ORD_YEOJAE_GP"), ""));// 주문여재구분
				    tcRecord2.setField("STL_WT"             , StringHelper.evl(stlRecord.getFieldString("SLAB_WT"), ""));// 재료중량 (SLAB중량)
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
//					jrRtn = commUtils.addSndData(jrRtn, tcRecord2);	
				    
				    commUtils.printLog(logId, "내부IF호출=YDPTJ001 슬라브소재 이송완료실적BACKUP처리", "[INFO]");
				}
			    
			    // /*********************************
			    //  * 주편공통 진도코드 UPDATE
			    //  *********************************/
				//jrParam.setField("MSLAB_NO", sSTOCK_ID);  
				//JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getListcurrprogcd", logId, methodNm, "공정 함수를 이용한 진도코드 가져오기 "); 
				//String szCurrProgCd = "";
				
				//if (rsResult.size() > 0) {
				//	szCurrProgCd = rsResult.getRecord(0).getFieldString("CURR_PROG_CD");
				//}
				
				//jrParam.setField("CURR_PROG_CD", szCurrProgCd); 
				//bSlabComm.updMSlabCommCurrProgCd(jrParam);
			    
				
				
				/**********************************************************
				* 주편공통 진도에 따라 주편공통, SLAB공통 UPDATE
				**********************************************************/
				//주편공통 진행 상태가 진행중(3)인 경우 SLAB공통을 update 
				String sRECORD_PROG_STAT = "3"; // 2018.02.28 주편공통에 존재하지 않을 경우 레코드진행상태를 '3'으로 본다.
				JDTORecordSet rsResult;
				String szCurrProgCd = "";
				
				// 주편 공통에서 조회
				jrParam.setField("STOCK_ID"		, sSTOCK_ID		);  
				JDTORecordSet rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.bslabCommDtlInqjl.getMSlabComm", logId, methodNm, "주편 공통에서 조회"); 
				
				if(rsResult2.size() > 0) {
					
					sRECORD_PROG_STAT = rsResult2.getRecord(0).getFieldString("RECORD_PROG_STAT");
					
				}
				
				if("3".equals(sRECORD_PROG_STAT)) {
					//RECORD_PROG_STAT == '3'
					//공정 함수를 이용한 진도코드 가져오기
					jrParam.setField("SLAB_NO"		, sSTOCK_ID		);  
					rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getListcurrprogcdSlab", logId, methodNm, "공정 함수를 이용한 진도코드 가져오기 "); 
					
					if(rsResult.size() > 0) {
						szCurrProgCd = rsResult.getRecord(0).getFieldString("CURR_PROG_CD");
					}
					
					//SLAB공통 진도코드 UPDATE
					jrParam.setField("CURR_PROG_CD"		, szCurrProgCd		); 
					bSlabComm.updSlabCommCurrProgCd(jrParam);
					
				} else {
					//RECORD_PROG_STAT != '3'
					//공정 함수를 이용한 진도코드 가져오기
					jrParam.setField("MSLAB_NO"		, sSTOCK_ID		);  
					rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getListcurrprogcd", logId, methodNm, "공정 함수를 이용한 진도코드 가져오기 "); 
					
					if(rsResult.size() > 0) {
						szCurrProgCd = rsResult.getRecord(0).getFieldString("CURR_PROG_CD");
					}
					
					//주편공통 진도코드 UPDATE
					jrParam.setField("CURR_PROG_CD"		, szCurrProgCd		); 
					bSlabComm.updMSlabCommCurrProgCd(jrParam);
					
				}
				
				
				
			    
			    commUtils.printLog(logId, "이송백업 실적처리 END", "[INFO]-");
			} // end if ("Y".equals(sFTMV_BKUP))
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updStkLoc

	
	/**
	 * 산적위치수정 - 삭제
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord delStkLoc(GridData gdReq) throws DAOException {
		String methodNm = "산적위치수정-삭제[BSlabJspSeEJB.delStkLoc] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			String sYD_GP            = gdReq.getParam("YD_GP");
			String sSTOCK_ID         = gdReq.getParam("STOCK_ID");
			String sYD_WRKBOOK_YN    = gdReq.getParam("YD_WRKBOOK_YN"   );
			String sYD_CARSCH_YN     = gdReq.getParam("YD_CARSCH_YN"    );
			String sYD_TCARSCH_YN    = gdReq.getParam("YD_TCARSCH_YN"   );
			String sFROM_ADDR        = gdReq.getParam("FROM_ADDR");  //from 위치
			String sMODIFIER         = gdReq.getParam("userid"          );
			String sDEL_RST          = gdReq.getParam("DEL_RST"         ); // 삭제이유
			
			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			/*****************************************************
			 * 저장품제원 : 야드L2로 송신(YMA8L002)
			 ******************************************************/
			commUtils.printLog(logId, "YMA8L002 JMS전송", "[INFO]");
			
			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setField("TC_CD"          , "YMA8L002");
			jrYdMsg.setField("MSG_GP"         , "D"); //삭제일경우 D로 보냄 20170904
			jrYdMsg.setField("YD_INFO_SYNC_CD", "5");
			jrYdMsg.setField("STOCK_ID"       , sSTOCK_ID);

			jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L002Del", jrYdMsg));
			
			
			//산적위치수정-삭제
			/*
			UPDATE TB_YM_STACKLAYER
			   SET STOCK_ID         = NULL
			     , STACK_LAYER_STAT = 'E'
			     , MODIFIER = :V_MODIFIER
			     , MOD_DDTT = SYSDATE
			 WHERE STOCK_ID = :V_STOCK_ID
			 */
			jrParam.setField("STOCK_ID", sSTOCK_ID);
			commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStackLayer", logId, methodNm, "산적위치수정-삭제");
			
			/* 삭제이유 
			UPDATE TB_YM_STOCK
			   SET DEL_RST = :V_DEL_RST
			     , MODIFIER = :V_MODIFIER
			     , MOD_DDTT = SYSDATE
			 WHERE STOCK_ID = :V_STOCK_ID
			 */
			jrParam.setField("DEL_RST", sDEL_RST);
			commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updDelRst");
			
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
					jrParam.setField("YD_WBOOK_ID", rst.getRecord(0).getField("YD_WBOOK_ID"));
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
					jrParam.setField("YD_TCAR_SCH_ID", rstTcar.getRecord(0).getField("YD_TCAR_SCH_ID"));
					/*
					UPDATE TB_YM_TCARFTMVMTL
					   SET MODIFIER    = :V_MODIFIER
					      ,MOD_DDTT    = SYSDATE
					      ,DEL_YN      = 'Y'
					 WHERE YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
					   AND STOCK_ID       = :V_STOCK_ID
					   AND DEL_YN         = 'N'
					 */
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updDelTcarFtMvMtl");
					
					if ("1".equals(rstTcar.getRecord(0).getField("CNT"))) {
						/*
						UPDATE TB_YM_TCARSCH
						   SET MODIFIER    = :V_MODIFIER
						      ,MOD_DDTT    = SYSDATE
						      ,DEL_YN      = 'Y'
						 WHERE YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
						   AND DEL_YN         = 'N' 
						 */
						commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updDelTcarSch");
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
					jrParam.setField("YD_CAR_SCH_ID", rstCar.getRecord(0).getField("YD_CAR_SCH_ID"));
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
					
					if ("1".equals(rstCar.getRecord(0).getField("CNT"))) {
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
			this.insertUpPutWrslRtData(sSTOCK_ID, sFROM_ADDR, "", "3X9999", sYD_GP, sMODIFIER);//
			
			/*****************************************************
			 * 저장위치제원 : 코일야드L2로 송신(YMA8L001)
			 ******************************************************/
			commUtils.printLog(logId, "YMA8L001 JMS전송", "[INFO]");
			
			JDTORecord jrYdMsg1 = JDTORecordFactory.getInstance().create();
			jrYdMsg1.setField("TC_CD"          , "YMA8L001");
			jrYdMsg1.setField("MSG_GP"         , "I");
			jrYdMsg1.setField("YD_INFO_SYNC_CD", "4");
			jrYdMsg1.setField("STACK_COL_GP"   , sFROM_ADDR.substring(0,6));                        
			jrYdMsg1.setField("STACK_BED_GP"   , sFROM_ADDR.substring(6,8));

			jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L001", jrYdMsg1));
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of delStkLoc    
	
	
	/**
	 * 산적위치수정 - 전문백업 TODO
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData
	 * @return JDTORecord
	 * @throws DAOException
	 */ 

	public JDTORecord updStkLocBackUp(GridData gdReq) throws DAOException {
		
		String methodNm = "산적위치수정-백업[BSlabJspSeEJB.updStkLocBackUp] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		JDTORecordSet dmRc = null;
		
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
			if ("STOCK".equals(sSND_FLAG)) {
				
				jrParam.setField("STOCK_ID"  , sSTOCK_ID);
				
				// 저장품 생성
				EJBConnector ejbConnS = new EJBConnector("default", "BSlabJspSeEJB", this);
				jrRtn = (JDTORecord)ejbConnS.trx("insStockInfo", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				
				//======================================================
				// 저장품제원 : 코일야드L2로 송신(YMA8L002)
				//======================================================
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setField("TC_CD"          , "YMA8L002");
				jrYdMsg.setField("MSG_GP"         , "I");
				jrYdMsg.setField("YD_INFO_SYNC_CD", "5");
				jrYdMsg.setField("STOCK_ID"       , sSTOCK_ID);
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L002", jrYdMsg));
			}
			
			/******************************
			 *** 스카핑보급
			 ******************************/
			if ("SCARFING_IN".equals(sSND_FLAG)) { //CS1PB02
				//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setField("JMS_TC_CD"       , YmConstant.A8YML022);	//크레인작업지시요구
				jrYdMsg.setField("WRK_GP"          , "1"      );	//작업처리구분 '1’ : Line In (보급), ‘2’ : Take Out, ‘3’ : Line Off (추출)
				jrYdMsg.setField("SLAB_NO"         , sSTOCK_ID);	//SLAB_NO
				jrYdMsg.setField("POSITION"        , ""       );	//POSITION
				jrYdMsg.setField("CAU_CD"          , ""       );	//원인코드

				EJBConnector sndConn = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
				JDTORecord jrSnd = (JDTORecord)sndConn.trx("rcvA8YML022", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });

				jrRtn = commUtils.addSndData(jrRtn, jrSnd);
			} //end if
			
			/******************************
			 *** 장입실적
			 ******************************/
			if (sSND_FLAG.equals("SND_YDCTJ032")) { 
				
				//생산통제 장입진행실적 송신 
//				jrRtn = commUtils.addSndData(jrRtn, bSlabComm.makeYDCTJ032(sSTOCK_ID, YmConstant.TC_YMPC030, logId ));
				JDTORecord jrYdMsgH2LINE = JDTORecordFactory.getInstance().create();
				
				//압연L2LINE OFF실적송신
				jrYdMsgH2LINE.setField("STOCK_ID"         , sSTOCK_ID);
				jrYdMsgH2LINE.setField("CHG_SUP_PROG_STAT", YmConstant.TC_YMPC030); //TODO 모르겠음!!
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YDCTJ032BackUp", jrYdMsgH2LINE)); // 전문쿼리 생성 해야함
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
	 * 오퍼레이션명 : 
	 *
	 * SLAB 산적위치 수정 메소드 TODO
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws Exception 
	 * @ejb.transaction type="RequiresNew"
	 */                    									 
	public JDTORecord changeSlabLocationInfo(JDTORecord jparam) throws Exception {
		
		String logId = jparam.getResultCode();
		
		String sSTOCK_ID = jparam.getFieldString("STOCK_ID"); 
		String sUpLoc    = jparam.getFieldString("FROM_ADDR"); // FROM위치
		String sPutLoc   = jparam.getFieldString("YD_STR_LOC");// TO위치
		String sMODIFIER = StringHelper.evl(jparam.getFieldString("MODIFIER"), "SYSTEM");
		String sPUT_BETWEEN_YN = commUtils.nvl(jparam.getFieldString("PUT_BETWEEN_YN"), "N");// 끼워넣기여부
		
		JDTORecord jrRtn = null;
		
		try {

			String sWbookId = "";
			String sDelYn   = "";
			String sYD_CRN_SCH_ID   = "";
			String sCurSchCode = "";
			String sCurBayGp = "";

			String sPutStackColGp = "";
			String sPutStackBedGp = "";
			String sPutStackLayerGp = "";

			String sUpYardGp  = "";
			String sPutYardGp = "";

			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setField("MODIFIER", sMODIFIER);
			jrParam.setField("STOCK_ID", sSTOCK_ID);
			
			JDTORecord sStockInfo = ymComm.getSlabYdAimRtGp(jrParam);
			String sCURR_PROG_CD = sStockInfo.getFieldString("CURR_PROG_CD");
			String sYD_AIM_RT_GP = sStockInfo.getFieldString("YD_AIM_RT_GP");
			
			if (sUpLoc.length() > 1) {
				sUpYardGp = sUpLoc.substring(0, 1); //from 야드구분
			}

			if (sPutLoc.length() > 1) {
				sPutYardGp = sPutLoc.substring(0, 1); //to 야드구분
			}

			if (sCURR_PROG_CD.equals(YmConstant.CURR_PROG_CD_SLAB_3)) { //생산종료
				throw new Exception("산적위치 수정=> 생산종료된 저장품은 수정을 할 수 없습니다.");
			}

			if (!"".equals(sUpYardGp) && !"".equals(sPutYardGp)	&& !sUpYardGp.equals(sPutYardGp)) {
				if (!sCURR_PROG_CD.equals(YmConstant.CURR_PROG_CD_SLAB_C)) {
					throw new Exception("산적위치 수정=> 공장간 산적위치 수정은 할 수 없습니다.");
				}
			}

			
			/**
			 * 0. 입력한 To 위치 정합성 점검  
			 */
			if (sPutLoc.length() == 10) {
				sPutStackColGp   = sPutLoc.substring(0, 6);
				sPutStackBedGp   = sPutLoc.substring(6, 8);
				sPutStackLayerGp = sPutLoc.substring(8, 10);
			}

			/*			
			SELECT *
			  FROM USRYMA.TB_YM_STACKCOL
			 WHERE STACK_COL_GP = :V_STACK_COL_GP
			*/ 
			jrParam.setField("STACK_COL_GP", sPutStackColGp);
			JDTORecordSet vColGp = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getStackColInfoWithPk");

			if (vColGp.size() <= 0) {
				throw new Exception("산적위치 수정=> To위치정보가 잘못 입력되었습니다.");
			}
			
			/********************************
			 * 1. 작업예약 유무 체크
			 ********************************/
			/*
			WITH PARAM AS (
			SELECT :V_STOCK_ID AS P_STOCK_ID FROM DUAL
			)
			SELECT A.CARUNLOAD_PUT_LOC
			     , (
			        SELECT WB.YD_WBOOK_ID 
			          FROM TB_YM_WRKBOOK    WB
			             , TB_YM_WRKBOOKMTL WM
			             , PARAM
			         WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
			           AND WM.STOCK_ID = P_STOCK_ID
			           AND WB.DEL_YN = 'N'
			           AND WM.DEL_YN = 'N'     
			        )AS WBOOK_ID
			     , A.DEL_YN
			  FROM TB_YM_STOCK A
			     , PARAM
			 WHERE A.STOCK_ID = P_STOCK_ID
			 */
			jrParam.setField("STOCK_ID", sSTOCK_ID);
			JDTORecordSet stockV = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getStockInfo");
			
			if (stockV.size() > 0) {
				sWbookId = StringHelper.evl(stockV.getRecord(0).getFieldString("WBOOK_ID"), "");
				sDelYn   = StringHelper.evl(stockV.getRecord(0).getFieldString("DEL_YN"), "");
			} else {
//				throw new Exception("산적위치 수정=> 저장품정보가 존재하지 않습니다.");
			}
			
			commUtils.printLog(logId, "산적위치 수정=> 작업예약ID = " + sWbookId, "[INFO]");
			commUtils.printLog(logId, "산적위치 수정=> 삭제유무   = " + sDelYn  , "[INFO]");
			
			if (!"".equals(sWbookId)) {
				
				/* 작업예약 재료 위치 수정 */
				jrParam.setField("STOCK_ID"   , sSTOCK_ID);
				jrParam.setField("MODIFIER"   , sMODIFIER);
				jrParam.setField("YD_WBOOK_ID", sWbookId);
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
				//commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updWmStrLoc");
			    /**********************************************************
				* 작업예약/재료 삭제 
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
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updDelYnWrkBookMtl");				

				//작업예약 삭제
				/*
				UPDATE TB_YM_WRKBOOK
				   SET MODIFIER    = :V_MODIFIER
				      ,MOD_DDTT    = SYSDATE
				      ,DEL_YN      = 'Y'
				 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
				   AND DEL_YN      = 'N'
				 */
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updDelYnWrkBook");	

				/*
				SELECT * 
				  FROM TB_YM_WRKBOOK
				 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID 
				*/
				jrParam.setField("YD_WBOOK_ID", sWbookId);
				JDTORecordSet wbookV = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getWbookInfo");
				
				/**
				 * 1.2 작업예약 스케쥴 코드 체크
				 */
				if (wbookV.size() > 0) {
					sCurSchCode = StringHelper.evl(wbookV.getRecord(0).getFieldString("YD_SCH_CD"), "");
					sCurBayGp 	= StringHelper.evl(wbookV.getRecord(0).getFieldString("YD_BAY_GP"), "");
				}
				commUtils.printLog(logId, "산적위치 수정=> 스케쥴코드=" + sCurSchCode, "[INFO]");

				// 즉 상차지시 편성된 시점의 작업예약은 삭제하지 않는다.
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
					jrParam.setField("YD_WBOOK_ID", sWbookId);
					//commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateBayGpWithWbookId");
				}
			}

			String sUP_STACK_COL_GP   = "";
			String sUP_STACK_BED_GP   = "";
			String sUP_STACK_LAYER_GP = "";
			String sUP_SECT_GP = "";

			/********************************************************
			 * 2. 저장품의 MAP정보를 가져온다. 중복위치도 체크한다.
			 ********************************************************/
			commUtils.printLog(logId, "저장품의 MAP정보를 가져온다. 중복위치도 체크한다", "[INFO]");
			/*
			SELECT * 
			  FROM TB_YM_STACKLAYER
			 WHERE STOCK_ID = :V_STOCK_ID
			 */
			jrParam.setField("STOCK_ID", sSTOCK_ID);
			JDTORecordSet stockL = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getStackLayerInfoWithStockId_03");

			JDTORecord stackV = null;

			if (stockL.size() > 0) {
				for (int inx = 0; inx < stockL.size(); inx++) {
					stackV = stockL.getRecord(inx);

					sUP_STACK_COL_GP 	= StringHelper.evl(stackV.getFieldString("STACK_COL_GP"), "");
					sUP_STACK_BED_GP 	= StringHelper.evl(stackV.getFieldString("STACK_BED_GP"), "");
					sUP_STACK_LAYER_GP  = StringHelper.evl(stackV.getFieldString("STACK_LAYER_GP"), "");

					sUP_SECT_GP = sUP_STACK_COL_GP.substring(2, 4);
					
					if ("XX".equals(sUP_SECT_GP)// SALB 비상적치위치
					 || "SE".equals(sUP_SECT_GP)// SALB Scarfing 입측
					 || "SD".equals(sUP_SECT_GP)// SALB Scarfing 출측
					 ) {
						/**
						 * 컨베이어정보삭제 
						 */
						//this.deleteConveyorInfo(sUpStackColGp,sStockId); 

					} else {

						/***************************
						 * FROM위치가 대차일 경우 
						 ***************************/
						if ("TC".equals(sUP_SECT_GP)) {

							/**
							 * 대차위치 CLEAR
							 */
							String sCurrQty = "0";
							sCurrQty = "-1";

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
							jrParam.setField("QTY"         , sCurrQty);
							jrParam.setField("STACK_COL_GP", sUP_STACK_COL_GP);
							jrParam.setField("STACK_BED_GP", sUP_STACK_BED_GP);
							commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateStackerQtyInfo");
							
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
							JDTORecordSet rst = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdTcarSchIdByStockId");
							
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
								commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updDelTcarFtMvMtl");
								
								/* 대차재료 개수 조회
								SELECT COUNT(*) AS TCAR_CNT
								  FROM TB_YM_TCARFTMVMTL
								 WHERE DEL_YN = 'N'
								   AND YD_TCAR_SCH_ID = (
								                        SELECT YD_TCAR_SCH_ID 
								                          FROM TB_YM_TCARFTMVMTL
								                         WHERE DEL_YN = 'N'
								                           AND STOCK_ID = :V_STOCK_ID
								                        )
								 */
								JDTORecordSet rstCnt = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCntTcarMtlCntByStockId");
								
								if ("0".equals(rstCnt.getRecord(0).getField("TCAR_CNT"))){ 
									// 대차에 남아있는 재료가 없을 때 대차스케줄도 삭제
									/*
									UPDATE TB_YM_TCARSCH
									   SET MODIFIER    = :V_MODIFIER
									      ,MOD_DDTT    = SYSDATE
									      ,DEL_YN      = 'Y'
									 WHERE YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
									   AND DEL_YN         = 'N' 
									 */
									commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updDelTcarSch");
								}
								
							}

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

						commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateStockMoveEquipInfo");
						
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
						
						commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateCraneStackLayerStat");
						
						/**
						 * FROM 위치 상단 적치상태 수정
						 */
						/*
						 * B열연 Slab 바로 위 상단 상태정보를 UPDATE
						 */
						//this.setSlabUpperState_V(jrParam); TODO
					}

					
					//commUtils.printLog(logId, "산적위치 수정=> FROM 위치 수정 = " + iReq, "[INFO]");
				
				}
			}

			/********************************
			 * 4. TO 위치 수정
			 ********************************/
			String sPUT_SECT_GP = sPutStackColGp.substring(2, 4); //SECT_GP

			if ("XX".equals(sPUT_SECT_GP) || // SLAB 비상적치위치
				"SE".equals(sPUT_SECT_GP) || // SLAB 스카핑 입측
				"SD".equals(sPUT_SECT_GP)) { // SLAB 스카핑 출측

				//this.insertConveyorInfo(sPutStackColGp, sStockId, sPutStackBedGp);

			} else {

				/**
				 * 3. TO 위치 정보 체크
				 */

				jrParam.setField("STACK_COL_GP"  , sPutStackColGp);
				jrParam.setField("STACK_BED_GP"  , sPutStackBedGp);
				jrParam.setField("STACK_LAYER_GP", sPutStackLayerGp);
				jrParam.setField("STOCK_ID"      , sSTOCK_ID);
				jrParam.setField("YD_STR_LOC"    , sPutLoc);
				
				commUtils.printLog(logId, "산적위치 수정 => 끼워넣기여부 : " + sPUT_BETWEEN_YN, "[INFO]");
				if ("Y".equals(sPUT_BETWEEN_YN)) { //끼워넣기 TODO
					/*
					 * 적치단 Put위치정보부터 상단으로 정보를 SHIFT한다.
					 */
					//this.updateLegacyStockId_Slab_01(jrParam);
					this.updPutBetweenSlab(jrParam);
				} else if ("N".equals(sPUT_BETWEEN_YN)) {
					/*
					 * 적치단 Put위치에 다른 SLAB가 있을 경우. 해당동의 XX번지로 저장품 MAP을 수정한다.
					 */
					this.updateLegacyStockId_Slab(jrParam);
				}
				
	            
				if ("TC".equals(sPUT_SECT_GP)) {
									
					/***************************
					 * 대차위치 CLEAR
					 ***************************/
					String sCurrQty = "0";
					sCurrQty = "1";

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
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateStackerQtyInfo");

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

					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateStockMoveEquipInfo");
					
				} //END if ("TC".equals(sPUT_SECT_GP))

				/*
				 * 적치단 Put위치를 적치상태로 변경 tb_ym_stacklayer Table : stock_id = Coil
				 * No tb_ym_stacklayer Table : stack_layer_stat = 'C'(적치중)
				 */
				jrParam.setField("STOCK_ID"        , sSTOCK_ID);
				jrParam.setField("STACK_LAYER_STAT", YmConstant.YD_STK_LYR_MTL_STAT_C);
				jrParam.setField("STACK_COL_GP"    , sPutStackColGp);
				jrParam.setField("STACK_BED_GP"    , sPutStackBedGp);
				jrParam.setField("STACK_LAYER_GP"  , sPutStackLayerGp);
				
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
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateCraneStackLayerStat");
				
				/*
				 * B열연 Slab 바로 위 상단 상태정보를 UPDATE
				 */

				//this.setSlabUpperState_E(jrParam); // TODO

			}

			/**
			 * 5.2 장입대상재 산적위치 수정으로 보급시 장입순번을 CLEAR한다.
			 */
			if (YmConstant.STACK_COL_USAGE_CD_WB.equals(sPUT_SECT_GP) || // W/B
					YmConstant.STACK_COL_USAGE_CD_CT.equals(sPUT_SECT_GP)) {// CTC

				// 저장품 TABLE CHARGE_LOT_NO 항목 CLEAR
				/*
				UPDATE TB_YM_STOCK
				   SET CHARGE_LOT_NO = :V_CHARGE_LOT_NO	
				     , MODIFIER      = :V_MODIFIER
				     , MOD_DDTT      = SYSDATE     
				 WHERE STOCK_ID = :V_STOCK_ID   
				 */
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateStockLotNoWithStockId");
				
				//야드 L2 전송 장입순번 Clear CALL
				//저장품 제원 송신 CM1BP02 -> YMA8L002   R
				//boolean isTrue = callL2LotEndInfo_Slab(sSTOCK_ID);	
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setField("TC_CD"          , "YMA8L002");
				jrYdMsg.setField("MSG_GP"         , "I");
				jrYdMsg.setField("YD_INFO_SYNC_CD", "5");
				jrYdMsg.setField("STOCK_ID"       , sSTOCK_ID.trim());

				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L002", jrYdMsg));
			}

			/**
			 * 5.3 SLAB이송의 상차 실적을 송신
			 */
			if (sPutLoc.startsWith(YmConstant.YD_GP_2)
					&& sCURR_PROG_CD.equals(YmConstant.CURR_PROG_CD_SLAB_C)) {

				JDTORecord tcParam = JDTORecordFactory.getInstance().create();
				tcParam.setField("JMS_TC_CD"         , "YMPOJ155");
				tcParam.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14());
				tcParam.setField("tcCode"          	 , "YMPOJ155");           // TC Code
				tcParam.setField("tcDate"            , commUtils.getDate10());// 발생일자
				tcParam.setField("tcTime"            , commUtils.getTime8()); // 발생시각
				tcParam.setField("slabNo"            , sSTOCK_ID);            // SLAB번호
				tcParam.setField("upDownGbn"         , YmConstant.CAR_GP_D);  // U상차 D하차
				tcParam.setField("upDownDate"        , commUtils.getDate8()); // yyymmdd
				tcParam.setField("upDownLoc"         , sPutLoc);              // 상차하차 산적위치

				jrRtn = commUtils.addSndData(jrRtn, tcParam);
			}
			
			
			/**
			 * 4. SLAB 공통 TABLE 수정
			 */
			jrParam.setField("STOCK_ID"   , sSTOCK_ID);
			jrParam.setField("YD_LOC"     , sPutLoc);
			EJBConnector ejbConn1 = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
			ejbConn1.trx("UpdSlabComLoc", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			
			/*
			 * 저장품 예상PUT 위치 CLEAR 저장품지정조회화면에서 예약상태로 남아있는것을 방지하기 위해
			 */
			JDTORecordSet stockRc = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getStockInfo");
			
			if (stockRc.size() > 0) {
				String sCarunloadBay = StringHelper.evl(stockRc.getRecord(0).getFieldString("CARUNLOAD_PUT_LOC"), "");// 하차PUT위치
				if (sPutLoc.equals(sCarunloadBay)) {
					/*
					UPDATE TB_YM_STOCK
					   SET CARUNLOAD_PUT_LOC = :V_CARUNLOAD_PUT_LOC
					     , MODIFIER = 'SYSTEM'
					     , MOD_DDTT = SYSDATE     
					 WHERE STOCK_ID = :V_STOCK_ID          
					 */
					jrParam.setField("CARUNLOAD_PUT_LOC", "");
					jrParam.setField("STOCK_ID"         , sSTOCK_ID);
					commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.updateStockPutLocWithStockId");
				}
			}

			/**
			 * 6. 저장품이동조건 수정
			 */
			/*
			UPDATE TB_YM_STOCK
			   SET STOCK_MOVE_TERM = :V_YD_AIM_RT_GP
			     , YD_AIM_RT_GP    = :V_YD_AIM_RT_GP
			     , MODIFIER        = :V_MODIFIER
			     , MOD_DDTT        = SYSDATE     
			 WHERE STOCK_ID = :V_STOCK_ID
			 */
			jrParam.setField("YD_AIM_RT_GP"   , sYD_AIM_RT_GP);
			jrParam.setField("STOCK_ID"       , sSTOCK_ID);
			jrParam.setField("MODIFIER"       , sMODIFIER);
			commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updateStockTransInfo");
			

			/**
			 * 7. Crane 작업 실적 등록
			 */
			this.insertUpPutWrslRtData(sSTOCK_ID.trim(), sUpLoc.trim(), sPutLoc.trim(), sCurSchCode, sPutYardGp ,sMODIFIER);


			/**
			 * 9. YARD MAP 정보 실적 등록  YMA8L002
			 */
			/**
			 * 9. YARD MAP 정보 실적 등록  YMA8L002
			 * --> FROM 위치 TO위치 번지 저장위치제원, 저장품제원 정보 전부 송신
			 */
//			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
//			jrYdMsg.setField("TC_CD"          , "YMA8L001");
//			jrYdMsg.setField("MSG_GP"         , "I");
//			jrYdMsg.setField("YD_INFO_SYNC_CD", "4"); //
//			jrYdMsg.setField("STOCK_ID"       , sSTOCK_ID.trim());
//			jrYdMsg.setField("STACK_COL_GP"   , sPutStackColGp);
//			jrYdMsg.setField("STACK_BED_GP"   , sPutStackBedGp);
//
//			jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L001", jrYdMsg));
			
			
			// 변경전 저장위치 제원 송신
			if (!"".equals(sUpLoc)) {
				JDTORecord jrYMA8L001 = JDTORecordFactory.getInstance().create();              
				jrYMA8L001.setField("TC_CD"          , "YMA8L001");                            
				jrYMA8L001.setField("MSG_GP"         , "I");                                   
				jrYMA8L001.setField("YD_INFO_SYNC_CD", "4"); //                                
				jrYMA8L001.setField("STACK_COL_GP"   , sUpLoc.substring(0,6));                        
				jrYMA8L001.setField("STACK_BED_GP"   , sUpLoc.substring(6,8));                        
				                                                                            
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L001", jrYMA8L001)); 
				
				JDTORecord jrYMA8L002 = JDTORecordFactory.getInstance().create();              
				jrYMA8L002.setField("TC_CD"          , "YMA8L002");                            
				jrYMA8L002.setField("MSG_GP"         , "I");                                   
				jrYMA8L002.setField("YD_INFO_SYNC_CD", "4"); //                                
				jrYMA8L002.setField("STACK_COL_GP"   , sUpLoc.substring(0,6));                        
				jrYMA8L002.setField("STACK_BED_GP"   , sUpLoc.substring(6,8));                        
				                                                                            
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L002", jrYMA8L002));
			}
			
			//변경후 저장위치 제원 송신
			if (!"".equals(sPutLoc)) {
				JDTORecord jrYMA8L001 = JDTORecordFactory.getInstance().create();              
				jrYMA8L001.setField("TC_CD"          , "YMA8L001");                            
				jrYMA8L001.setField("MSG_GP"         , "I");                                   
				jrYMA8L001.setField("YD_INFO_SYNC_CD", "4"); //                                
				jrYMA8L001.setField("STACK_COL_GP"   , sPutLoc.substring(0,6));                        
				jrYMA8L001.setField("STACK_BED_GP"   , sPutLoc.substring(6,8));                        
				                                                                            
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L001", jrYMA8L001)); 
				
				JDTORecord jrYMA8L002 = JDTORecordFactory.getInstance().create();              
				jrYMA8L002.setField("TC_CD"          , "YMA8L002");                            
				jrYMA8L002.setField("MSG_GP"         , "I");                                   
				jrYMA8L002.setField("YD_INFO_SYNC_CD", "4"); //                                
				jrYMA8L002.setField("STACK_COL_GP"   , sPutLoc.substring(0,6));                        
				jrYMA8L002.setField("STACK_BED_GP"   , sPutLoc.substring(6,8));                        
				                                                                            
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L002", jrYMA8L002));
			}
			
			return jrRtn;
			
		} catch (DAOException daoe) {
			throw daoe;
		} 

	}	
	
	
	/*
	 * SLAB
	 * 바로 위 상단 상태정보를 '적치불가' 으로 UPDATE
	 */
	public void setSlabUpperState_V(JDTORecord rcvParam) throws JDTOException {
		
		JDTORecord jrRtn = null;
		
		String sSTACK_COL_GP   = StringHelper.evl(rcvParam.getFieldString("STACK_COL_GP"  ), ""); 
		String sSTACK_BED_GP   = StringHelper.evl(rcvParam.getFieldString("STACK_BED_GP"  ), "");
		String sSTACK_LAYER_GP = StringHelper.evl(rcvParam.getFieldString("STACK_LAYER_GP"), "");
		String sMODIFIER       = StringHelper.evl(rcvParam.getFieldString("MODIFIER"), "SYSTEM");
		
		JDTORecord jparam = JDTORecordFactory.getInstance().create();
		jparam.setField("MODIFIER"      , sMODIFIER);
		
		{
			/*
			SELECT *
			  FROM TB_YM_STACKLAYER
			 WHERE STACK_COL_GP  	= :V_STACK_COL_GP
			   AND STACK_BED_GP		= :V_STACK_BED_GP 
			   AND STACK_LAYER_GP	= :V_STACK_LAYER_GP 
			 */
		 	jparam.setField("STACK_COL_GP"  , sSTACK_COL_GP);
		 	jparam.setField("STACK_BED_GP"  , sSTACK_BED_GP);
		 	jparam.setField("STACK_LAYER_GP", YmCommUtils.changeLayerFormat(sSTACK_LAYER_GP, "M"));
		 	
		 	JDTORecordSet lyrJr = commDao.select(jparam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getStackLayerInfoWithPk");
		 	
    		if (lyrJr.size() > 0) {
    			/**
	    		 * 상단정보 수정시에 하단이 적치가능이면('E')
	    		 * 현위치정보도 적치불가('V')로 셋팅한다.
	    		 */
	    		if ("".equals(StringHelper.evl(lyrJr.getRecord(0).getFieldString("STOCK_ID"), ""))){ 	
		    		/* 
					 * 적치단 UP위치의 2단 -1번지 Clear
					 * tb_ym_stacklayer Table : stock_id = ''(Empty)
					 * tb_ym_stacklayer Table : stack_layer_stat	   = 'V'(하단에 적치되지 않은 위치)
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
					jparam.setField("STACK_COL_GP"    , sSTACK_COL_GP);
					jparam.setField("STACK_BED_GP"    , sSTACK_BED_GP);
					jparam.setField("STACK_LAYER_GP"  , sSTACK_LAYER_GP);
					
					commDao.update(jparam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateCraneStackLayerStat");					 
	    		}
	    	}
		}	
    	
    	{
		 	jparam.setField("STACK_COL_GP"  , sSTACK_COL_GP);
		 	jparam.setField("STACK_BED_GP"  , sSTACK_BED_GP);
		 	jparam.setField("STACK_LAYER_GP", YmCommUtils.changeLayerFormat(sSTACK_LAYER_GP, "P"));
		 	
		 	JDTORecordSet lyrJr = commDao.select(jparam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getStackLayerInfoWithPk");
		 	
		 	
    		if (lyrJr.size() > 0) {							 
		    	/**
	    		 * 작업충돌로 저장품 정보가 존재하면 
	    		 * 권상시점에서 셋팅하지 않는다.
	    		 */
	    		if ("".equals(StringHelper.evl(lyrJr.getRecord(0).getFieldString("STOCK_ID"), ""))) { 	
			    	/* 
					 * 적치단 UP위치의 2단 동일번지 Clear
					 * tb_ym_stacklayer Table : stock_id = ''(Empty)
					 * tb_ym_stacklayer Table : stack_layer_stat	   = 'V'(하단에 적치되지 않은 위치)
					 */	
					jparam.setField("STOCK_ID"        , "");
					jparam.setField("STACK_LAYER_STAT", YmConstant.YD_STK_LYR_MTL_STAT_E);
					jparam.setField("STACK_COL_GP"    , sSTACK_COL_GP);
					jparam.setField("STACK_BED_GP"    , sSTACK_BED_GP);
					jparam.setField("STACK_LAYER_GP"  , YmCommUtils.changeLayerFormat(sSTACK_LAYER_GP, "P"));
					
					commDao.update(jparam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateCraneStackLayerStat");					
	    		}
	    	}
    	}
    	
//    	return iSeq;
	}
	
	public void updPutSortSlab(JDTORecord rcvParam) throws JDTOException {
		String sSTACK_COL_GP   = commUtils.nvl(rcvParam.getFieldString("STACK_COL_GP"  ), ""); 
		String sSTACK_BED_GP   = commUtils.nvl(rcvParam.getFieldString("STACK_BED_GP"  ), "");
		String sMODIFIER       = commUtils.nvl(rcvParam.getFieldString("MODIFIER"), "SYSTEM");
		
		JDTORecord jrParam = JDTORecordFactory.getInstance().create();
		jrParam.setField("MODIFIER"    , sMODIFIER);
		/*
		SELECT COUNT(*) OVER() AS LAYER_CNT
		     , SUM(DECODE(STACK_LAYER_STAT, 'C', 1)) OVER() AS STOCK_CNT
		     , STACK_COL_GP
		     , STACK_BED_GP
		     , STACK_LAYER_GP
		     , STACK_LAYER_STAT
		  FROM TB_YM_STACKLAYER
		 WHERE STACK_COL_GP	= :V_STACK_COL_GP
		   AND STACK_BED_GP	= :V_STACK_BED_GP 
		 */
		JDTORecordSet stockList = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabLyrList");
		if (stockList.size() == 0) {
			return;
		}
		
		JDTORecord info = null;
		int nLAYER_CNT = Integer.parseInt(stockList.getRecord(0).getFieldString("LAYER_CNT"));
		int nSTOCK_CNT = Integer.parseInt(stockList.getRecord(0).getFieldString("STOCK_CNT"));
		String sLAYER = "";
		
		for (int i = 0 ; i < nLAYER_CNT; ++i) {

	    	if (Integer.toString(i+1).length() == 1) {
	    		sLAYER = "0" + Integer.toString(i+1);
	    	} 
		 	if (i < nSTOCK_CNT) {
		 		info = stockList.getRecord(i);
		 		jrParam.setField("STACK_COL_GP"    , commUtils.nvl(info.getFieldString("STACK_COL_GP"), ""));
			 	jrParam.setField("STACK_BED_GP"    , commUtils.nvl(info.getFieldString("STACK_BED_GP"), ""));
			 	jrParam.setField("STACK_LAYER_GP"  , sLAYER);
			 	jrParam.setField("STOCK_ID"        , commUtils.nvl(info.getFieldString("STOCK_ID"), ""));
			 	jrParam.setField("STACK_LAYER_STAT", commUtils.nvl(info.getFieldString("STACK_LAYER_STAT"), ""));
	    	} else {
	    		jrParam.setField("STACK_COL_GP"    , sSTACK_COL_GP);
			 	jrParam.setField("STACK_BED_GP"    , sSTACK_BED_GP);
			 	jrParam.setField("STACK_LAYER_GP"  , sLAYER);
			 	jrParam.setField("STOCK_ID"        , "");
			 	jrParam.setField("STACK_LAYER_STAT", "E");
	    	}
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
		 	commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateCraneStackLayerStat");
		}
	}
	
	
	
	/* 
	 * 적치단 Put위치정보부터 상단으로 정보를 SHIFT한다.
	 */	
	public void updPutBetweenSlab(JDTORecord rcvParam) throws JDTOException {
		
		commUtils.printLog("", "산적위치 수정 => 끼워넣기 로직 = updPutBetweenSlab", "[INFO]");
		
		String sSTACK_COL_GP   = commUtils.nvl(rcvParam.getFieldString("STACK_COL_GP"  ), ""); 
		String sSTACK_BED_GP   = commUtils.nvl(rcvParam.getFieldString("STACK_BED_GP"  ), "");
		String sSTACK_LAYER_GP = commUtils.nvl(rcvParam.getFieldString("STACK_LAYER_GP"), "");
		String sSTOCK_ID       = commUtils.nvl(rcvParam.getFieldString("STOCK_ID"      ), "");
		String sMODIFIER       = commUtils.nvl(rcvParam.getFieldString("MODIFIER"), "SYSTEM"); 
//		String sYD_STR_LOC     = commUtils.nvl(rcvParam.getFieldString("YD_STR_LOC"    ), "");
		JDTORecord listJ = null;
		
		JDTORecord jrParam = JDTORecordFactory.getInstance().create();
		jrParam.setField("MODIFIER"    , sMODIFIER);
		/*
		SELECT *
		  FROM TB_YM_STACKLAYER
		 WHERE STACK_COL_GP	= :V_STACK_COL_GP
		   AND STACK_BED_GP	= :V_STACK_BED_GP 
		 */
		JDTORecordSet listV = commDao.select(rcvParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getStackLayerInfoWithBed");
		
		for(int inx = Integer.parseInt(sSTACK_LAYER_GP); inx < listV.size(); inx++){
				
		 	listJ = listV.getRecord(inx -1);
		 	
		 	jrParam.setField("STACK_COL_GP"    , commUtils.nvl(listJ.getFieldString("STACK_COL_GP"), ""));
		 	jrParam.setField("STACK_BED_GP"    , commUtils.nvl(listJ.getFieldString("STACK_BED_GP"), ""));
		 	jrParam.setField("STACK_LAYER_GP"  , commUtils.changeLayerFormat(commUtils.nvl(listJ.getFieldString("STACK_LAYER_GP"),""), "P"));
		 	jrParam.setField("STOCK_ID"        , commUtils.nvl(listJ.getFieldString("STOCK_ID"), ""));
		 	jrParam.setField("STACK_LAYER_STAT", commUtils.nvl(listJ.getFieldString("STACK_LAYER_STAT"), ""));
		 	
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
		 	commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateCraneStackLayerStat");
		 	
		 	// TODO 저장품 제원 보내줘야 한다면 이부분에 넣을것..20171014
		}
		
		if (!"".equals(sSTOCK_ID)) {
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
		 	jrParam.setField("STACK_COL_GP"    , sSTACK_COL_GP);
		 	jrParam.setField("STACK_BED_GP"    , sSTACK_BED_GP);
		 	jrParam.setField("STACK_LAYER_GP"  , sSTACK_LAYER_GP);
		 	jrParam.setField("STOCK_ID"        , sSTOCK_ID);
		 	jrParam.setField("STACK_LAYER_STAT", YmConstant.STACK_LAYER_STAT_C);
		 	commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateCraneStackLayerStat");
		} 
	}	
	
	/* 
	 * 적치단 Put위치에 다른 SLAB가 있을 경우.
	 * 해당동의 XX번지로 저장품 MAP을 수정한다.
	 */	
	public void updateLegacyStockId_Slab(JDTORecord rcvParam) throws Exception {
		
		String sSTACK_COL_GP   = commUtils.nvl(rcvParam.getFieldString("STACK_COL_GP"  ), ""); 
		String sSTACK_BED_GP   = commUtils.nvl(rcvParam.getFieldString("STACK_BED_GP"  ), "");
		String sSTACK_LAYER_GP = commUtils.nvl(rcvParam.getFieldString("STACK_LAYER_GP"), "");
		String sSTOCK_ID       = commUtils.nvl(rcvParam.getFieldString("STOCK_ID"      ), "");
		String sMODIFIER       = commUtils.nvl(rcvParam.getFieldString("MODIFIER"), "SYSTEM"); 
		String sYD_STR_LOC     = commUtils.nvl(rcvParam.getFieldString("YD_STR_LOC"    ), "");

		String sTempLayer = sSTACK_COL_GP.substring(0,2) 
					      + YmConstant.STACK_COL_USAGE_CD_XX
					      + YmConstant.STACK_BED_GP_01 +"0101";;
	    //산적위치수정 임의 스케줄코드
		String sSchCd = "3X9999";
		
		//W/B인 경우 생략 
		if (sSTACK_COL_GP.substring(2,4).equals("WB")) {
			return;
		}
		
		/*
		SELECT *
		  FROM TB_YM_STACKLAYER
		 WHERE STACK_COL_GP  	= :V_STACK_COL_GP
		   AND STACK_BED_GP		= :V_STACK_BED_GP 
		   AND STACK_LAYER_GP	= :V_STACK_LAYER_GP
	 	*/
		JDTORecordSet rst = commDao.select(rcvParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getStackLayerInfoWithPk");
		String sToStockId 	= "";
		
		if (rst.size() > 0) {
			sToStockId 	= StringHelper.evl(rst.getRecord(0).getFieldString("STOCK_ID"), "");
		}
		
		/******************************************************
		 *** 산적위치수정위치에 존재하던 기존 저장품 이력 저장 
		 ******************************************************/
		if (!"".equals(sToStockId) && !sSTOCK_ID.equals(sToStockId)) { 	
			
			// 이력 저장
			this.insertUpPutWrslRtData(sToStockId, sYD_STR_LOC, sTempLayer, sSchCd, "2", sMODIFIER);
			/***************************************
			 * SALB 공통 Table 저장위치 Update 
			 ***************************************/	 
//			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
//  			jrParam.setField("STOCK_ID"   , sSTOCK_ID);
//  			jrParam.setField("YD_LOC"     , sTempLayer);
//  			EJBConnector ejbConn1 = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
//  			ejbConn1.trx("UpdSlabComLoc", new Class[] { JDTORecord.class }, new Object[] { jrParam });
		}
		
	} //end		
	
	
	/**
	 * 산적위치 수정 화면에서 From 위치와 To 위치 정보를 실적 처리한다. TODO
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param String :  저장품ID
	 * @param String :  UP LOC
	 * @param String :  PUT LOC
	 * @param String :  스케쥴 코드
	 * @param String :  야드구분
	 * @throws Exception 
	 * 
	 */
	public int insertUpPutWrslRtData(String sStockId, String sUpLoc, String sPutLoc
			                        , String sSchCode, String sYdGp , String sUserId
							         ) throws Exception {
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
			scrane_sch_code = "";
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
	 * 작업예약현황 - TO지정위치 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updWrkDmdMgt(GridData gdReq) throws DAOException {
		String methodNm = "작업예약현황 - TO지정위치 수정[BSlabJspSeEJB.updWrkDmdMgt] < " + gdReq.getNavigateValue();
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
				
				jrParam.setField("YD_TO_LOC_GUIDE"  , commUtils.getValue(gdReq, "YD_TO_LOC_GUIDE"  , ii));       
				jrParam.setField("YD_WBOOK_ID"      , commUtils.getValue(gdReq, "YD_WBOOK_ID"      , ii));     
				/*
				UPDATE TB_YM_WRKBOOK
				   SET YD_TO_LOC_GUIDE = :V_YD_TO_LOC_GUIDE
				     , MODIFIER        = :V_MODIFIER
				     , MOD_DDTT        = SYSDATE
				 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID 
				   AND DEL_YN      = 'N'
				 */
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdToLocGuide", logId, methodNm, "작업예약 YD_TO_LOC_GUIDE 수정");
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updWrkDmdMgt	
	
	/**
	 *      [A] 오퍼레이션명 : W/B 보급(wbSupply)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord wbSupply(GridData gdReq) throws DAOException {
		String methodNm = "W/B 보급[BSlabJspSeEJB.wbSupply] < " + gdReq.getNavigateValue();
		 
		
		JDTORecord jrRtn 		= JDTORecordFactory.getInstance().create();
	    JDTORecord jrParam		= JDTORecordFactory.getInstance().create(); //Query 실행시 파라메터 전달용 JDTORecord 
	    JDTORecordSet rsResult  = null;
	    JDTORecordSet rsResult2 = null;
		String logId = gdReq.getIPAddress();
	    
		

		
	    try{
			commUtils.printLog(logId, methodNm, "S+");
	    	
 		   jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
 		   jrParam.setResultMsg(methodNm);	//상위 Method 명


			
			
			/**********************************************************
			* 1. W/B 01Bed에 권하지시(D) 예약이 잡혀있으면 01 Bed 정보 Clear
			**********************************************************/
			
			//적치단 테이블에서 W/B 01Bed 에 권하지시(D) 예약이 잡혀 있는지 확인
			jrParam.setField("STACK_COL_GP"		, "2CWB01"); 
			jrParam.setField("STACK_BED_GP"		, "01"); 
			jrParam.setField("STACK_LAYER_STAT"	, "D");
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getPutStockIdByBedNo 
			SELECT  LYR.STACK_COL_GP
			       ,LYR.STACK_BED_GP
			       ,LYR.STACK_LAYER_GP
			       ,LYR.STOCK_ID
			       ,LYR.STACK_LAYER_STAT
			       ,LYR.STACK_LAYER_ACTIVE_STAT
			       ,(
			            SELECT WBMTL.YD_WBOOK_ID
			              FROM TB_YM_WRKBOOK    WB
			                  ,TB_YM_WRKBOOKMTL WBMTL
			             WHERE WB.YD_WBOOK_ID = WBMTL.YD_WBOOK_ID
			               AND WB.DEL_YN = 'N'
			               AND WBMTL.DEL_YN = 'N'
			               AND WBMTL.STOCK_ID = LYR.STOCK_ID
			        ) AS YD_WBOOK_ID       
			  FROM  TB_YM_STACKLAYER LYR
			 WHERE  LYR.STACK_COL_GP = :V_STACK_COL_GP
			   AND  LYR.STACK_BED_GP = :V_STACK_BED_GP
			   AND  LYR.STACK_LAYER_STAT LIKE :V_STACK_LAYER_STAT || '%'
			   AND  LYR.STOCK_ID IS NOT NULL
			  ORDER BY LYR.STACK_LAYER_GP DESC */
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getPutStockIdByBedNo", logId, methodNm, "W/B 01Bed 권하지시 예약 확인 "); 
			
			if(rsResult.size() > 0) {
				// W/B 01 Bed 권하지시 예약 위치 Clear
				jrParam.setField("STOCK_ID", ""); 
				jrParam.setField("STACK_LAYER_STAT2", "E"); 
				jrParam.setField("STACK_COL_GP"		, "2CWB01"); 
				jrParam.setField("STACK_BED_GP"		, "01"); 
				jrParam.setField("STACK_LAYER_STAT1", "D");
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLyrByBedNo 
				UPDATE TB_YM_STACKLAYER
				   SET 
				       MODIFIER = :V_MODIFIER
				      ,MOD_DDTT = SYSDATE
				      ,STOCK_ID = :V_STOCK_ID
				      ,STACK_LAYER_STAT = :V_STACK_LAYER_STAT2
				 WHERE  STACK_COL_GP = :V_STACK_COL_GP
				   AND  STACK_BED_GP = :V_STACK_BED_GP
				   AND  STACK_LAYER_STAT LIKE :V_STACK_LAYER_STAT1 || '%' */
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLyrByBedNo", logId, methodNm, "W/B 01 Bed 권하지시 예약 위치 Clear");
			}			
			

				String sYD_SCH_CD = null;
				String sYD_TO_LOC_GUIDE = null;
				String sYD_SCH_PRIOR = null;
				String sYD_WBOOK_ID = "";
				
 
					
					//스케줄코드, To위치 Guide 
					sYD_SCH_CD = "2CWB01UM";
					sYD_TO_LOC_GUIDE = "";
				
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
					rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
			    	
					if (rsResult2 != null && rsResult2.size() > 0) {
						sYD_SCH_PRIOR = rsResult2.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
					} else {
						throw new Exception("B열연 코일 스케쥴 코드 이상 : [" + sYD_SCH_CD + "]");
					}			
					
					//작업예약ID생성
					sYD_WBOOK_ID = commDao.getSeqId(logId, methodNm, "WrkBook");
				
					/**********************************************************
					* 2-1. 작업예약(TB_YM_WRKBOOK) 생성
					**********************************************************/
					jrParam.setField("YD_WBOOK_ID"		, sYD_WBOOK_ID);
					jrParam.setField("YD_GP"			, YmConstant.YD_GP_2);
					jrParam.setField("YD_BAY_GP"		, sYD_SCH_CD.substring(1,2));
					jrParam.setField("YD_SCH_CD"		, sYD_SCH_CD); //야드스케쥴코드
					jrParam.setField("YD_SCH_PRIOR"		, sYD_SCH_PRIOR); //야드스케쥴우선순위
					jrParam.setField("YD_SCH_PROG_STAT"	, "W"); //야드스케쥴진행상태(W:스케줄수행대기)
					jrParam.setField("YD_SCH_ST_GP"		, "A"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업)
					jrParam.setField("YD_TO_LOC_GUIDE"	, sYD_TO_LOC_GUIDE); //야드To위치Guide
					
					commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insWrkBook", logId, methodNm, "작업예약(TB_YM_WRKBOOK) 생성");
					
					/**********************************************************
					* 2-2. 작업예약재료(TB_YM_WRKBOOKMTL) 생성
					**********************************************************/
					//등록 할  레코드 수
					int rowCnt = gdReq.getHeader("CHECK").getRowCount();
					for(int ii = 0; ii < rowCnt; ii++) {
						jrParam.setField("YD_WBOOK_ID"		, sYD_WBOOK_ID);
						jrParam.setField("STOCK_ID"			, commUtils.getValue(gdReq, "STOCK_ID", ii));
						jrParam.setField("STACK_COL_GP"		, commUtils.getValue(gdReq, "FROM_LOC", ii).substring(0,6));
						jrParam.setField("STACK_BED_GP"		, commUtils.getValue(gdReq, "FROM_LOC", ii).substring(7,9));
						jrParam.setField("STACK_LAYER_GP"	, commUtils.getValue(gdReq, "FROM_LOC", ii).substring(10));
						
						commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insYmWrkBookMtl", logId, methodNm, "작업예약재료(TB_YM_WRKBOOKMTL) 생성");
						
						/**********************************************************
						* 2-3. TB_YM_STOCK의 저장품 이동조건 등록
						**********************************************************/
						jrParam.setField("STOCK_ID"			, commUtils.getValue(gdReq, "STOCK_ID", ii));
						jrParam.setField("STOCK_MOVE_TERM"	, YmConstant.NEW_STOCK_MOVE_TERM_FS ); //압연작업대기
						
						commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockMoveTerm", logId, methodNm, "TB_YM_STOCK의 저장품 이동 조건 UPDATE");
					}
				 
				
				//스케줄 메인 호출
				//JDTORecord jrCrnSchMsg = JDTORecordFactory.getInstance().create();
				//jrCrnSchMsg.setField("JMS_TC_CD"			, "YMYMJ202"); 
				//jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시				
				//jrCrnSchMsg.setField("YD_WBOOK_ID"  		, sYD_WBOOK_ID); //작업예약ID
				//jrCrnSchMsg.setField("YD_SCH_CD"  		, ""); //야드스케쥴코드
				//jrCrnSchMsg.setField("YD_EQP_ID"  		, ""); //야드설비ID
				//jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);		
				
				//Slab Schedule EJB Call
				jrParam.setField("JMS_TC_CD"    , "YMYMJ202"); 
				jrParam.setField("YD_WBOOK_ID"  , sYD_WBOOK_ID); //야드작업예약ID
				jrParam.setField("YD_SCH_CD"    , ""  ); //야드스케쥴코드
				jrParam.setField("YD_EQP_ID"    , ""  ); //야드설비ID
				
				
				EJBConnector ejbConn = new EJBConnector("default","BSlabSchSeEJB",this);
				jrRtn = (JDTORecord)ejbConn.trx("procYMYMJ202",new Class[]{JDTORecord.class},new Object[]{ jrParam });			
				
	 
			

			commUtils.printLog(logId, methodNm, "S-");
			
	    	return jrRtn;
	    	
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of W/B 보급 
	
	/**
	 *      [A] 오퍼레이션명 : 저장품의 장입순번 CLEAR(modifyZoneInNo)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord modifyZoneInNo(GridData gdReq) throws DAOException {
		String methodNm = "저장품의 장입순번 CLEAR[BSlabJspSeEJB.modifyZoneInNo] < " + gdReq.getNavigateValue();
		 
		
		JDTORecord jrRtn 		= JDTORecordFactory.getInstance().create();
	    JDTORecord jrParam		= JDTORecordFactory.getInstance().create(); //Query 실행시 파라메터 전달용 JDTORecord 
	    JDTORecord tcParam		= JDTORecordFactory.getInstance().create(); //Query 실행시 파라메터 전달용 JDTORecord 
	    JDTORecord eaiParam		= JDTORecordFactory.getInstance().create(); //Query 실행시 파라메터 전달용 JDTORecord 
	    JDTORecordSet rsResult = null;
		String logId = gdReq.getIPAddress();
	    
		

		
	    try{
			commUtils.printLog(logId, methodNm, "S+");
	    	
 		   jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
 		   jrParam.setResultMsg(methodNm);	//상위 Method 명


			//등록 할  레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			for(int ii = 0; ii < rowCnt; ii++) {
				String stockID = commUtils.getValue(gdReq, "STOCK_ID", ii);
				
				if("".equals(stockID)) continue;
				jrParam.setField("STOCK_ID"			, stockID);
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getYmStockInfoJoinSlabcomm", logId, methodNm, "저장품 조회"); 
				
				if(rsResult.size()>0){
					
					tcParam		= JDTORecordFactory.getInstance().create();
					//2020.03 생산통제로 취소전문 전송 YDCTJ038
					//jrParam = JDTORecordFactory.getInstance().create();
					tcParam.setResultCode(logId);	//Log ID
					tcParam.setResultMsg(methodNm);	//Log Method Name
					
					tcParam.setField("JMS_TC_CD"	       , "YDCTJ038"  );
					tcParam.setField("JMS_TC_CREATE_DDTT"  , commUtils.getDateTime14()  );
					
					tcParam.setField("SLAB_WO_RT_CD"	   , rsResult.getRecord(0).getFieldString("SLAB_WO_RT_CD")  ); //SLAB 지시 행선 코드
					tcParam.setField("SLAB_NO"  	       , stockID); //SLAB 번호
					tcParam.setField("PLAN_SLAB_NO"  	   , rsResult.getRecord(0).getFieldString("PLAN_SLAB_NO")); //예정 SLAB번호

					//전송 Data 생성
					//jrRtn = commUtils.addSndData(commDao.getMsgL2("YMA8L001", jrParam));
					jrRtn = commUtils.addSndData(jrRtn, tcParam);
					
				}
				else{
					throw new Exception("저장품ID ["+commUtils.getValue(gdReq, "STOCK_ID", ii)+"] 에 대해 저장품이 존재하지 않습니다.");
				}
				
			
			}
		

			commUtils.printLog(logId, methodNm, "S-");
			
	    	return jrRtn;
	    	
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of modifyZoneInNo
	
	
	/**
	 * 동간작업기준 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updBayBetWrkRule(GridData gdReq) throws DAOException {
		String methodNm = "동간작업기준 변경[BSlabJspSeEJB.updBayBetWrkRule] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			String sCARLOAD_STOP_LOC = ""; //상차동
			String sEQUIP_GP         = ""; //대차번호
			
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				/*
				 * 2018.01.12 동간작업기준조회 - 현재동 기준으로 3번 대차로 상하차동 변경
				 */
				/***************************************************************************
				* 2DTC31 변경조건 추가 (동간작업기준조회)
	  			* 2DTC32를 2DTC31로 변경할 때는 3번대차가 2DTC32에 있을때 변경가능함
	            * 2DTC31를 2DTC32로 변경할 때는 3번대차가 2DTC31에 있을때 변경가능함
	            * 
	            * 위 조건일 경우 3번대차가 D동에 없을 경우는 상차동을 2DTC31, 2DTC32로 변경이 불가능함  
	            * 
	            * 대차3호 상차동을 2DTC31 또는 2DTC32 로 변경할 경우 D동에 대차상차스케줄이 존재하고
	            * TO위치가 변경하려는 상차동 위치와 다르면 변경 불가능하다. (스케줄 완료 후 변경가능)
	            * 
				****************************************************************************/
				sEQUIP_GP         = commUtils.getValue(gdReq, "EQUIP_GP"           , ii);
				
/*			    //1차버젼	
				if("2XTC03".equals(sEQUIP_GP)) {
					
					sCARLOAD_STOP_LOC = commUtils.getValue(gdReq, "CARLOAD_STOP_LOC"   , ii); //상차동 
					//sCARUNLOAD_STOP_LOC = commUtils.getValue(gdReq, "CARUNLOAD_STOP_LOC" , ii); //하차동

					if("2DTC31".equals(sCARLOAD_STOP_LOC) || "2DTC32".equals(sCARLOAD_STOP_LOC)) { 
					//|| "2DTC31".equals(sCARUNLOAD_STOP_LOC) || "2DTC32".equals(sCARUNLOAD_STOP_LOC)) {
						
						jrParam.setField("EQUIP_GP", sEQUIP_GP);
						
						com.inisteel.cim.ym.bcommon.dao.BSlabDAO.getTransCarRuleList 
			 			SELECT EQUIP_GP
			      		      ,WAIT_STOP_LOC
			                  ,CURR_STOP_LOC
			                  ,CARLOAD_STOP_LOC
			                  ,CARUNLOAD_STOP_LOC
			              FROM TB_YM_EQUIP
			             WHERE YD_GP = '2'
			               AND EQUIP_KIND = 'TC'
			               AND EQUIP_GP = :V_EQUIP_GP
			            
						
						JDTORecordSet jdrs = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.BSlabDAO.getTransCarRuleList", logId, methodNm, "동간작업기준 변경 시 설비 테이블 조회");
						
						if (jdrs != null && jdrs.size() > 0) {
							
							if(!sCARLOAD_STOP_LOC.equals(jdrs.getRecord(0).getFieldString("CARLOAD_STOP_LOC"))) {
								//대차 3호기 D동 상차동 변경  
								if("2DTC31".equals(sCARLOAD_STOP_LOC)) {
									if("2DTC31".equals(jdrs.getRecord(0).getFieldString("CURR_STOP_LOC"))) {
										//현위치가 2DTC31 인데 상차지를 2DTC31로 변경하는건 가능하다
									} else if(!"2DTC32".equals(jdrs.getRecord(0).getFieldString("CURR_STOP_LOC"))) {
										throw new Exception("상차동 2DTC32를 2DTC31로 변경할 때는 3번대차가 2DTC32에 있을때 변경가능함");
									}
								} else if("2DTC32".equals(sCARLOAD_STOP_LOC)) {
									if("2DTC32".equals(jdrs.getRecord(0).getFieldString("CURR_STOP_LOC"))) {
										//현위치가 2DTC32 인데 상차지를 2DTC32로 변경하는건 가능하다
									} else if(!"2DTC31".equals(jdrs.getRecord(0).getFieldString("CURR_STOP_LOC"))) {
										throw new Exception("상차동 2DTC31를 2DTC32로 변경할 때는 3번대차가 2DTC31에 있을때 변경가능함");
									}
								}
							}
							
							//if(!sCARUNLOAD_STOP_LOC.equals(jdrs.getRecord(0).getFieldString("CARUNLOAD_STOP_LOC"))) {
							//	//대차 3호기 D동 하차동 변경  
							//	if("2DTC31".equals(sCARUNLOAD_STOP_LOC)) {
							//		if(!"2DTC32".equals(jdrs.getRecord(0).getFieldString("CURR_STOP_LOC"))) {
							//			throw new Exception("하차동 2DTC32를 2DTC31로 변경할 때는 3번대차가 2DTC32에 있을때 변경가능함");
							//		}
							//	} else if("2DTC32".equals(sCARUNLOAD_STOP_LOC)) {
							//		if(!"2DTC31".equals(jdrs.getRecord(0).getFieldString("CURR_STOP_LOC"))) {
							//			throw new Exception("하차동 2DTC31를 2DTC32로 변경할 때는 3번대차가 2DTC31에 있을때 변경가능함");
							//		}
							//	}
							//}
						}
					}
				}
*/				
				/*2차 버전 */
				if("2XTC03".equals(sEQUIP_GP)) {
					
					sCARLOAD_STOP_LOC = commUtils.getValue(gdReq, "CARLOAD_STOP_LOC"   , ii); //상차지  (화면에서 변경한 상차지 예)2DTC31를 2DTC32로 변경하면 2DTC32 값이 들어 있다)
					
					if("2DTC31".equals(sCARLOAD_STOP_LOC) || "2DTC32".equals(sCARLOAD_STOP_LOC)) { 
						
						jrParam.setField("EQUIP_GP", sEQUIP_GP);
						
						/*com.inisteel.cim.ym.bcommon.dao.BSlabDAO.getTransCarRuleList
						SELECT EQUIP_GP
						      ,WAIT_STOP_LOC
						      ,CURR_STOP_LOC
						      ,CARLOAD_STOP_LOC
						      ,CARUNLOAD_STOP_LOC
						      ,SUBSTR(CURR_STOP_LOC,2,1) AS CURR_BAY_GP
						FROM TB_YM_EQUIP
						WHERE YD_GP = '2'
						AND   EQUIP_KIND = 'TC'
						AND   EQUIP_GP = :V_EQUIP_GP
			            */ 
						
						JDTORecordSet jdrs = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.BSlabDAO.getTransCarRuleList", logId, methodNm, "동간작업기준 변경 시 설비 테이블 조회");
						
						if (jdrs != null && jdrs.size() > 0) {
							
							if(!sCARLOAD_STOP_LOC.equals(jdrs.getRecord(0).getFieldString("CARLOAD_STOP_LOC"))) {
								
								if(!"D".equals(jdrs.getRecord(0).getFieldString("CURR_BAY_GP"))) {
									
									/* com.inisteel.cim.ym.bcommon.dao.BSlabDAO.getCrnSchNotLikeDnWoLoc 
									SELECT YD_CRN_SCH_ID
									      ,YD_WBOOK_ID
									      ,YD_EQP_ID
									      ,YD_SCH_CD
									      ,YD_SCH_PRIOR
									      ,YD_WRK_PROG_STAT
									      ,YD_UP_WO_LOC
									      ,YD_UP_WO_LAYER
									      ,YD_DN_WO_LOC
									      ,YD_DN_WO_LAYER
									FROM  TB_YM_CRNSCH
									WHERE DEL_YN = 'N'
									AND   YD_GP = '2'
									AND   YD_DN_WO_LOC LIKE :V_YD_DN_WO_LOC1 || '%'
									AND   YD_DN_WO_LOC NOT LIKE :V_YD_DN_WO_LOC2 || '%'
									*/
									jrParam.setField("YD_DN_WO_LOC1", "2DTC");
									jrParam.setField("YD_DN_WO_LOC2", sCARLOAD_STOP_LOC);
									jdrs = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.BSlabDAO.getCrnSchNotLikeDnWoLoc", logId, methodNm, "크레인 스케줄 조회");
									
									if (jdrs != null && jdrs.size() > 0) {
										if("2DTC31".equals(sCARLOAD_STOP_LOC)) {
											throw new Exception("D동 크레인 스케줄에 권하위치가 2DTC32로 지정된 대차상차 스케줄이 존재합니다! 대차가 D동에 도착 후 상차동을 변경해 주십시요.");
										} else if("2DTC32".equals(sCARLOAD_STOP_LOC)) {
											throw new Exception("D동 크레인 스케줄에 권하위치가 2DTC31로 지정된 대차상차 스케줄이 존재합니다! 대차가 D동에 도착 후 상차동을 변경해 주십시요.");
											
										}
									}
								}
							}
						}
					}
				}
				
				
				
				jrParam.setField("WAIT_STOP_LOC"       , commUtils.getValue(gdReq, "WAIT_STOP_LOC"      , ii)); //대기동(1BTC03) ; 대차코드의 X대신 동구분이 들어감 
				jrParam.setField("CARLOAD_STOP_LOC"    , commUtils.getValue(gdReq, "CARLOAD_STOP_LOC"   , ii)); //상차동(1BTC03) ; 상동                             
				jrParam.setField("CARUNLOAD_STOP_LOC"  , commUtils.getValue(gdReq, "CARUNLOAD_STOP_LOC" , ii)); //하차동(1ATC03) ; 상동                             
				jrParam.setField("CARLOAD_ASSIGN_YN"   , commUtils.getValue(gdReq, "CARLOAD_ASSIGN_YN"  , ii)); //상차지정(Y OR N)                                  
				jrParam.setField("CARUNLOAD_ASSIGN_YN" , commUtils.getValue(gdReq, "CARUNLOAD_ASSIGN_YN", ii)); //하차지정(Y OR N)                                  
				jrParam.setField("CARLD_SCH_CD"        , commUtils.getValue(gdReq, "CARLD_SCH_CD"       , ii)); //상차스케줄                                        
				jrParam.setField("CARUD_SCH_CD"        , commUtils.getValue(gdReq, "CARUD_SCH_CD"       , ii)); //하차스케줄                                        
				jrParam.setField("STACK_MAX_QNTY"      , commUtils.getValue(gdReq, "STACK_MAX_QNTY"     , ii)); //적치가능수량                                      
				jrParam.setField("CTS_RELAY_YN"        , commUtils.getValue(gdReq, "CTS_RELAY_YN"       , ii)); //연속작업횟수                                      
				jrParam.setField("PALLET_NO"           , commUtils.getValue(gdReq, "PRE_WRK_WO"         , ii)); //선작업지시 1:실행 2:대기                          
				jrParam.setField("EQUIP_GP"            , commUtils.getValue(gdReq, "EQUIP_GP"           , ii)); //
				
				/*
				UPDATE TB_YM_EQUIP 
				   SET WAIT_STOP_LOC       = :V_WAIT_STOP_LOC       --대기동(1BTC03) ; 대차코드의 X대신 동구분이 들어감
				     , CARLOAD_STOP_LOC    = :V_CARLOAD_STOP_LOC    --상차동(1BTC03) ; 상동
				     , CARUNLOAD_STOP_LOC  = :V_CARUNLOAD_STOP_LOC  --하차동(1ATC03) ; 상동
				     , CARLOAD_ASSIGN_YN   = :V_CARLOAD_ASSIGN_YN   --상차지정(Y OR N)
				     , CARUNLOAD_ASSIGN_YN = :V_CARUNLOAD_ASSIGN_YN --하차지정(Y OR N)
				     , CARLD_SCH_CD        = :V_CARLD_SCH_CD        --상차스케줄   
				     , CARUD_SCH_CD        = :V_CARUD_SCH_CD        --하차스케줄
				     , STACK_MAX_QNTY      = :V_STACK_MAX_QNTY      --적치가능수량
				     , CTS_RELAY_YN        = :V_CTS_RELAY_YN        --연속작업횟수
				     , PALLET_NO           = :V_PALLET_NO           --선작업지시 1:실행 2:대기
				     , MODIFIER            = :V_MODIFIER
				     , MOD_DDTT            = SYSDATE
				 WHERE EQUIP_GP = :V_EQUIP_GP    --대차코드(1XTC03)
				 */
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateTransCarRoute", logId, methodNm, "동간작업기준 변경");
				
				/**********************************************************
				* 대차 상하차동 관련(TB_YM_RULE) 기준 테이블 Update - 상차동 *
				**********************************************************/
				jrParam.setField("STOP_LOC", commUtils.getValue(gdReq, "CARLOAD_STOP_LOC" , ii));
				jrParam.setField("EQUIP_GP", commUtils.getValue(gdReq, "EQUIP_GP" , ii));
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updateTransCarRule 
				MERGE INTO TB_YM_RULE RL USING (

				  SELECT REPR_CD_GP 
				        ,CD_GP
				        ,ITEM
				        ,CASE WHEN ITEM = :V_STOP_LOC THEN '*' ELSE '' END AS DTL_ITM3
				    FROM TB_YM_RULE
				   WHERE REPR_CD_GP = 'TCAR01'
				     AND CD_GP = '2'
				     AND DTL_ITM1 = :V_EQUIP_GP
				     AND DTL_ITM2 = SUBSTR(:V_STOP_LOC,2,1)

				) DD ON ( RL.REPR_CD_GP = DD.REPR_CD_GP AND RL.CD_GP = DD.CD_GP AND RL.ITEM = DD.ITEM )
				WHEN MATCHED THEN UPDATE SET
				     DTL_ITM3 = DD.DTL_ITM3
				    ,MOD_DDTT = SYSDATE
				    ,MODIFIER = :V_MODIFIER */
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updateTransCarRule", logId, methodNm, "동간작업기준 변경 시 기준 테이블 변경");
				
				/**********************************************************
				* 대차 상하차동 관련(TB_YM_RULE) 기준 테이블 Update - 하차동 *
				**********************************************************/
				jrParam.setField("STOP_LOC", commUtils.getValue(gdReq, "CARUNLOAD_STOP_LOC" , ii));
				jrParam.setField("EQUIP_GP", commUtils.getValue(gdReq, "EQUIP_GP" , ii));
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updateTransCarRule 
				MERGE INTO TB_YM_RULE RL USING (

				  SELECT REPR_CD_GP 
				        ,CD_GP
				        ,ITEM
				        ,CASE WHEN ITEM = :V_STOP_LOC THEN '*' ELSE '' END AS DTL_ITM3
				    FROM TB_YM_RULE
				   WHERE REPR_CD_GP = 'TCAR01'
				     AND CD_GP = '2'
				     AND DTL_ITM1 = :V_EQUIP_GP
				     AND DTL_ITM2 = SUBSTR(:V_STOP_LOC,2,1)

				) DD ON ( RL.REPR_CD_GP = DD.REPR_CD_GP AND RL.CD_GP = DD.CD_GP AND RL.ITEM = DD.ITEM )
				WHEN MATCHED THEN UPDATE SET
				     DTL_ITM3 = DD.DTL_ITM3
				    ,MOD_DDTT = SYSDATE
				    ,MODIFIER = :V_MODIFIER */
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updateTransCarRule", logId, methodNm, "동간작업기준 변경 시 기준 테이블 변경");

//				sCARLOAD_STOP_LOC = commUtils.getValue(gdReq, "CARUNLOAD_STOP_LOC", ii);
				sCARLOAD_STOP_LOC = commUtils.getValue(gdReq, "CARLOAD_STOP_LOC"  , ii);
				sEQUIP_GP         = commUtils.getValue(gdReq, "EQUIP_GP"          , ii);
				

				/*
				SELECT COUNT(*) AS WRK_CNT
				  FROM TB_YM_WRKBOOK
				 WHERE YD_GP  = '2'
				   AND DEL_YN = 'N'
				   AND YD_WRK_PLAN_TCAR = :V_YD_WRK_PLAN_TCAR
				 */
				jrParam.setField("YD_WRK_PLAN_TCAR", sEQUIP_GP); //대차번호
				JDTORecordSet jrWbCnt = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getWrkBookCntByTcar", logId, methodNm, "대차별 작업예약 개수");
				
				int nWRK_CNT = 99;
				if (jrWbCnt.size() > 0) {
					nWRK_CNT = Integer.parseInt(jrWbCnt.getRecord(0).getFieldString("WRK_CNT"));
				}
				
				if (nWRK_CNT == 0) {
					
					/*
					UPDATE TB_YM_TCARSCH
					   SET YD_CARLD_STOP_LOC = :V_YD_CARLD_STOP_LOC
					     , MODIFIER          = :V_MODIFIER
					     , MOD_DDTT          = SYSDATE
					 WHERE DEL_YN    = 'N'
					   AND YD_EQP_ID = :V_YD_EQP_ID
					 */
					jrParam.setField("YD_CARLD_STOP_LOC", sCARLOAD_STOP_LOC); //상차동
					jrParam.setField("YD_EQP_ID"        , sEQUIP_GP        ); //대차번호
					commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updTcarSchL");
				}			

			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updBayBetWrkRule	
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업요구현황조회 크레인변경
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updCraneChange(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업요구현황조회 크레인변경[BSlabJspSeEJB.updCraneChange] < " + gdReq.getNavigateValue();
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
					jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA8L004", jrParam));
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
					jrYdMsg.setField("JMS_TC_CD"       , YmConstant.A8YML007);	//크레인작업지시요구
					jrYdMsg.setField("YD_EQP_ID"       , chgYdEqpId);	//야드설비ID
					jrYdMsg.setField("YD_WRK_PROG_STAT", "1"       );	//야드작업진행상태(권상작업지시)
					jrYdMsg.setField("YD_SCH_CD"       , ydSchCd   );	//야드스케쥴코드
					jrYdMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId);	//야드크레인스케쥴ID

					EJBConnector sndConn = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
					JDTORecord jrSnd = (JDTORecord)sndConn.trx("rcvA8YML007", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });

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

					jrRtn = commUtils.addSndData(jrRtn, bSlabComm.getYMA8L005(resMsg));
					
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
					if ("W".equals(rst.getRecord(0).getFieldString("YD_WRK_PROG_STAT"))) {
						/*********************************************
						 * 이전 크레인의 다음 스케줄 명령 선택 기동 
						 ********************************************/
						JDTORecord jrA8YML007 = JDTORecordFactory.getInstance().create();
						jrA8YML007.setField("JMS_TC_CD", YmConstant.A8YML007);
						jrA8YML007.setField("YD_EQP_ID", ydEqpId);
						EJBConnector sndConn = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
						JDTORecord jrSnd = (JDTORecord)sndConn.trx("rcvA8YML007", new Class[] { JDTORecord.class }, new Object[] { jrA8YML007 });
		
						jrRtn = commUtils.addSndData(jrRtn, jrSnd);
					}
				}
				//변경된 크레인 상태 w이면 명령선택기동 EQP
//				if ("W".equals(chgYdEqpStat)) {
					/*********************************************
					 * 변경 크레인의 다음 스케줄 명령 선택 기동 
					 ********************************************/
//					JDTORecord jrA8YML007a = JDTORecordFactory.getInstance().create();
//					jrA8YML007a.setField("JMS_TC_CD", YmConstant.A8YML007);
//					jrA8YML007a.setField("YD_EQP_ID", chgYdEqpId);
//					EJBConnector sndConn1 = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
//					JDTORecord jrSnd1 = (JDTORecord)sndConn1.trx("rcvA8YML007", new Class[] { JDTORecord.class }, new Object[] { jrA8YML007a });
//
//					jrRtn = commUtils.addSndData(jrRtn, jrSnd1);
//				}
				
				
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
		String methodNm = "크레인작업요구현황조회 순위변경[BSlabJspSeEJB.updPriorChange] < " + gdReq.getNavigateValue();
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
				/*
				--작업예약 스케쥴우선순위 수정
				UPDATE TB_YM_WRKBOOK
				   SET MODIFIER     = :V_MODIFIER
				     , MOD_DDTT     = SYSDATE
				     , YD_SCH_PRIOR = NVL(TO_NUMBER(:V_YD_SCH_PRIOR),1)
				 WHERE YD_WBOOK_ID  = :V_YD_WBOOK_ID
				   AND DEL_YN       = 'N'
				 */
				//commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updWrkBookPrior", logId, methodNm, "TB_YM_WRKBOOK");				
				
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
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCrnWrkMgt", logId, methodNm,  "TB_YM_CRNSCH");				
				
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
	 *      [A] 오퍼레이션명 : 크레인작업요구현황조회 긴급작업
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord updPriorWrkChange(GridData gdReq) throws DAOException {
		String methodNm = "크레인작업관리 긴급작업[BSlabJspSeEJB.updPriorWrkChange] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			String ydWbookId  = ""; //야드작업예약ID
			String ydSchPrior = ""; //야드스케쥴우선순위
			String ydEqpId = ""; 
			String ydCrnSchId = ""; 
			String ydCrnSchIdWrk = ""; 
			String ydSchCd = ""; 
			String sYD_DN_LOC = "";
			
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
			    sYD_DN_LOC  = commUtils.trim(gdReq.getHeader("YD_DN_LOC").getValue(ii));
			    
			    if ("XX010101".equals(sYD_DN_LOC)) {
			    	throw new Exception("To위치가 XX010101일경우 긴급작업을 할 수 없습니다.");
			    }
			    
				commUtils.printLog(logId, "긴급작업 [ " + ydEqpId + " >> " + ydWbookId + " >> " + ydSchPrior + " >> " + ydCrnSchId +" >> " + ydSchCd + " ]", "SL");
 
			    //작업할  야드작업예약ID가 작업 완료한 야드작업예약ID에 있으면 이전에 모두 처리되었으므로 Skip
				if (commUtils.chkExist(arrYdWbookId, ydWbookId)) { continue; }
				arrYdWbookId[ii] = ydWbookId;

			    jrParam.setField("YD_EQP_ID"		, ydEqpId );      //신규
			    jrParam.setField("YD_CRN_SCH_ID" 	, ydCrnSchId );   //신규
				jrParam.setField("YD_WBOOK_ID" 		, ydWbookId );    //신규
				jrParam.setField("YD_SCH_PRIOR"		, "0");           //신규
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
			    		
			    		if ("A".equals(sYD_EQP_WRK_MODE2)) {

			    			//이전 크레인 스케줄이 위치검색 실패 일 때
			    			/*
							SELECT *
							  FROM TB_YM_CRNSCH
							 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID 
			    			 */
			    			jrParam.setField("YD_CRN_SCH_ID" 	, ydCrnSchIdWrk );
			    			JDTORecordSet frst = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSrnSchInfoByPk");
			    			
			    			String sYD_DN_WO_LOC = frst.getRecord(0).getFieldString("YD_DN_WO_LOC");
			    			
			    			if ("XX010101".equals(sYD_DN_WO_LOC)) {
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
								
								//기존 작업이 위치검색실패 이면 스케줄 기동
								JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();

								jrYdMsg.setResultCode(logId);	//Log ID
								jrYdMsg.setResultMsg(methodNm);	//Log Method Name
								jrYdMsg.setField("JMS_TC_CD"       , YmConstant.YMA8L004);	//크레인작업지시요구
								jrYdMsg.setField("MSG_GP"          , "I");	//야드설비ID
								jrYdMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId);	//야드크레인스케쥴ID

			//SJH				jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA8L004", jrYdMsg));	
				        		JDTORecordSet jsRtn1 = commDao.getMsgL2("YMA8L004", jrYdMsg);
								jrRtn = commUtils.addSndData(jrRtn , jsRtn1);
								
								//크레인작업지시 Z값 갱신 처리
				        		if(jsRtn1.size() > 0 ) {
				        			jrYdMsg.setField("YD_UP_WO_LOC_ZAXIS"   , jsRtn1.getRecord(0).getFieldString("YD_UP_WO_LOC_ZAXIS"));	
				        			jrYdMsg.setField("YD_DN_WO_LOC_ZAXIS"   , jsRtn1.getRecord(0).getFieldString("YD_DN_WO_LOC_ZAXIS"));	
				        			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updZAXIS 
				    				--크레인스케줄 z값 갱신
				    				UPDATE TB_YM_CRNSCH
				    				   SET YD_UP_WO_LOC_ZAXIS = :V_YD_UP_WO_LOC_ZAXIS
				    				     , YD_DN_WO_LOC_ZAXIS = :V_YD_DN_WO_LOC_ZAXIS
				    				 WHERE YD_CRN_SCH_ID    = :V_YD_CRN_SCH_ID
				    				   AND DEL_YN           = 'N' 
				    				*/
				            		commDao.update(jrYdMsg, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updZAXIS", logId, methodNm, "SLAB크레인스케줄 Z값 수정");
				        			
//				        			EJBConnector ejbConn1 = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
//									ejbConn1.trx("procCrnsSchZaxis", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg }); 					
				        		}
				        		return jrRtn;
			    			}
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
							String sAPP030 = ymComm.BCoilApplyYn("APP030","2","S1");
							
							if ("Y".equals(sAPP030)) {
								//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
								JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
				                
				               	jrYdMsg.setField("JMS_TC_CD"         , YmConstant.YMA8L004); //크레인작업지시요구
								jrYdMsg.setField("YD_CRN_SCH_ID"     , ydCrnSchId         ); //야드크레인스케쥴ID
								jrYdMsg.setField("MSG_GP"            , "I"   ); //전문구분
								jrYdMsg.setField("YD_CRN_SCH_RMD_CNT", "S1"  ); //S1 일시정지 후 긴급작업

								jrRtn = commUtils.addSndData(commDao.getMsgL2("YMA8L004", jrYdMsg));
							}
							
			    			return jrRtn;
			    			
			    		} 
			    		if ("M".equals(sYD_EQP_WRK_MODE2) || "R".equals(sYD_EQP_WRK_MODE2)) { //유인

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
			    	}
							
			    	/**
			    	 * 순위변경시에는 작업예약 순위는 변경하지 않는다. 
			    	 */
					//신규 작업예약 Table 우선순위 Update
			    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updWrkBookPrior1 
			    	--작업예약 스케쥴우선순위 수정
			    	UPDATE TB_YM_WRKBOOK
			    	   SET MODIFIER     = :V_MODIFIER
			    	     , MOD_DDTT     = SYSDATE
			    	     , YD_SCH_PRIOR = NVL(TO_NUMBER(:V_YD_SCH_PRIOR),1)
			    	 WHERE YD_WBOOK_ID  = :V_YD_WBOOK_ID
			    	   AND DEL_YN       = 'N'

					 */
					//commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updWrkBookPrior1", logId, methodNm, "TB_YM_WRKBOOK");				

			    	
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
						
					/*
					UPDATE TB_YM_CRNSCH
					   SET MODIFIER         = :V_MODIFIER
					     , MOD_DDTT         = SYSDATE
					     , YD_WORD_DT       = SYSDATE
					 WHERE YD_CRN_SCH_ID    = :V_YD_CRN_SCH_ID
					   AND DEL_YN           = 'N'					 
					 */
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdWorkDt");
					
					/**********************************************************
					* 3.2 신  크레인작업지시 요구 처리
					**********************************************************/

					//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
					JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();

					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name
					jrYdMsg.setField("JMS_TC_CD"       , YmConstant.YMA8L004);	//크레인작업지시요구
					jrYdMsg.setField("MSG_GP"          , "I");	//야드설비ID
					jrYdMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId);	//야드크레인스케쥴ID

//SJH					jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA8L004", jrYdMsg));	
	        		JDTORecordSet jsRtn1 = commDao.getMsgL2("YMA8L004", jrYdMsg);
					jrRtn = commUtils.addSndData(jrRtn , jsRtn1);
					
					//크레인작업지시 Z값 갱신 처리
	        		if(jsRtn1.size() > 0 ) {
	        			commUtils.printLog(logId,jsRtn1.getRecord(0).getFieldString("JMS_TC_MESSAGE").substring(165, 170), "SL");	        			
	        			commUtils.printLog(logId,jsRtn1.getRecord(0).getFieldString("JMS_TC_MESSAGE").substring(225, 230), "SL");	        			
	        			
	        			jrYdMsg.setField("YD_UP_WO_LOC_ZAXIS"   , jsRtn1.getRecord(0).getFieldString("YD_UP_WO_LOC_ZAXIS"));	
	        			jrYdMsg.setField("YD_DN_WO_LOC_ZAXIS"   , jsRtn1.getRecord(0).getFieldString("YD_DN_WO_LOC_ZAXIS"));	
	        			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updZAXIS 
	    				--크레인스케줄 z값 갱신
	    				UPDATE TB_YM_CRNSCH
	    				   SET YD_UP_WO_LOC_ZAXIS = :V_YD_UP_WO_LOC_ZAXIS
	    				     , YD_DN_WO_LOC_ZAXIS = :V_YD_DN_WO_LOC_ZAXIS
	    				 WHERE YD_CRN_SCH_ID    = :V_YD_CRN_SCH_ID
	    				   AND DEL_YN           = 'N' 
	    				*/
	            		commDao.update(jrYdMsg, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updZAXIS", logId, methodNm, "SLAB크레인스케줄 Z값 수정");
	            		
//	        			EJBConnector ejbConn1 = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
//						ejbConn1.trx("procCrnsSchZaxis", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg }); 		

	        		}
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
		String methodNm = "크레인작업관리 권하위치변경[BSlabJspSeEJB.updDownLocChange] < " + gdReq.getNavigateValue();
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
		String methodNm = "크레인작업요구현황조회 작업취소[BSlabJspSeEJB.updCraneWrkCancel] < " + gdReq.getNavigateValue();
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
				
				if (autoFlag && !"W".equals(sYD_WRK_PROG_STAT)){ //W:명령선택대기 S:스케줄작성중
					
					//설비상태 체크하여 Auto일경우 일시정지 상태에서만 가능하게 수정
					if (!"4".equals(sYD_EQP_AUTO_CRN_MODE) && !"B".equals(sWPROG_STAT)) { //4: 일시정지 B:고장
						//m_ctx.setRollbackOnly();
						throw new Exception("무인크레인 [" + ydEqpId + "]이 일시정지이거나 고장상태가 아니면 취소할 수 없습니다.");
						
					}
				}

				/**************************************
				 * 무인크레인
				 **************************************/
				if (autoFlag && !"W".equals(sYD_WRK_PROG_STAT)) {
					
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
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L004", jrParam));
					
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
				JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, "A8YML007");
	
				jrYdMsg.setField("JMS_TC_CD"       , YmConstant.A8YML007);	//크레인작업지시요구
				jrYdMsg.setField("YD_EQP_ID"       , ydEqpId   );	//야드설비ID
				jrYdMsg.setField("YD_WRK_PROG_STAT", "4"       );	//야드작업진행상태(권하완료)
				jrYdMsg.setField("YD_SCH_CD"       , ydSchCd   );	//야드스케쥴코드
				jrYdMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId);	//야드크레인스케쥴ID
	
				EJBConnector sndConn = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
				JDTORecord jrRtn1 = (JDTORecord)sndConn.trx("rcvA8YML007", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
				
				
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
	 * 대차스케줄관리 - 대차초기화
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord initTcarSchMgt(JDTORecord rcvMsg) throws DAOException {
		
		String methodNm = "대차스케줄관리 대차초기화[BSlabJspSeEJB.initTcarSchMgt] < " + rcvMsg.getResultMsg();
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
	 * 대차스케줄 복구
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord TcarSchRollBack(GridData gdReq) throws DAOException {
		String methodNm = "대차스케줄 복구[BSlabJspFaEJB.TcarSchRollBack] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			//String ydEqpId =  commUtils.trim(gdReq.getParam("ydEqpId")) ;

			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String ydWrkBookId_Unload ="";  // 하차지 예약 id
			String ydWrkBookId_load ="";	// 상차지 예약 id		 
			String YdTcarSchId 	   ="";
			String ProgStat         = "";
			String LU              ="";
			String UnloadStopLoc   ="";
			
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
			if (jsDnTc.size() <= 0 ){		
				if(ii == 0 ){
				  
					
					 
					 LU       = commUtils.getValue(gdReq, "WPROG_STAT", ii); //상하차구분
					String EquipGp  = commUtils.getValue(gdReq, "EQUIP_GP", ii);   //대차설비번호
					 
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getEqpSchCd 	
					--설비(대차)스케줄조회
						SELECT EQ.CARLOAD_STOP_LOC      
						     , EQ.CARUNLOAD_STOP_LOC      
							 , SUBSTR(EQ.CARLOAD_STOP_LOC,1,2)||SUBSTR(EQ.CARLD_SCH_CD,3)  AS CARLD_SCH_CD	                  
							 , SUBSTR(EQ.CARUNLOAD_STOP_LOC,1,2)||SUBSTR(EQ.CARUD_SCH_CD,3) AS CARUD_SCH_CD	
						     , R1.YD_WRK_CRN_PRIOR AS L_CRN_PRIOR
						     , R2.YD_WRK_CRN_PRIOR AS U_CRN_PRIOR
						     , TC.YD_TCAR_SCH_ID
						  FROM TB_YM_EQUIP EQ
						      ,TB_YM_SCHEDULERULE R1
						      ,TB_YM_SCHEDULERULE R2      
						      ,TB_YM_TCARSCH TC
						 WHERE EQUIP_GP = :V_YD_EQP_ID
						   AND EQUIP_GP = TC.YD_EQP_ID
						   AND SUBSTR(EQ.CARLOAD_STOP_LOC,1,2)||SUBSTR(EQ.CARLD_SCH_CD,3) = R1.YD_SCH_CD 
						   AND SUBSTR(EQ.CARUNLOAD_STOP_LOC,1,2)||SUBSTR(EQ.CARUD_SCH_CD,3) = R2.YD_SCH_CD 
						   AND EQ.DEL_YN    = 'N' 
						   AND TC.DEL_YN    = 'N'
					*/
					jrParam.setField("YD_EQP_ID"           , EquipGp); //대차설비id
			
					JDTORecordSet jsTcSchCd = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getEqpSchCd", logId, methodNm, "대차스케줄정보조회 "); 
					
					String LoadStopLoc      	= commUtils.trim(jsTcSchCd.getRecord(0).getFieldString("CARLOAD_STOP_LOC"));   //상차정지위치
					UnloadStopLoc 	    = commUtils.trim(jsTcSchCd.getRecord(0).getFieldString("CARUNLOAD_STOP_LOC")); //하차정지위치
					String CarldSchCd 	        = commUtils.trim(jsTcSchCd.getRecord(0).getFieldString("CARLD_SCH_CD"));       //상차스케줄
					String CarudSchCd 	        = commUtils.trim(jsTcSchCd.getRecord(0).getFieldString("CARUD_SCH_CD"));       //하차스케줄
					String LCrnPrior 	        = commUtils.trim(jsTcSchCd.getRecord(0).getFieldString("L_CRN_PRIOR"));        //상차지스케줄우선순위
					String UCrnPrior 	        = commUtils.trim(jsTcSchCd.getRecord(0).getFieldString("U_CRN_PRIOR"));        //하차지스케줄우선순위
					       YdTcarSchId 	        = commUtils.trim(jsTcSchCd.getRecord(0).getFieldString("YD_TCAR_SCH_ID"));     //대차스케줄ID
			
					String SetStopLoc      	= "";        //정지위치
					String SetSchCd 	    = "";        //스케줄
					String SetCrnPrior 	    = "";        //스케줄우선순위
					//String ProgStat         = "";
					if(LU.equals("L")){   //상차지 세팅
						SetStopLoc    =  LoadStopLoc;
						SetSchCd      =  CarldSchCd ;
						SetCrnPrior   =  LCrnPrior ;
						ProgStat      =  "5" ;        //상차완료
					}else if(LU.equals("U")){ //하차지 세팅
						SetStopLoc    =  UnloadStopLoc;
						SetSchCd      =  CarudSchCd ;
						SetCrnPrior   =  UCrnPrior ;
						ProgStat      =  "B" ;      //영대차하차도착
					}else {
						break;
					}
					
					//작업예약 등록
				if(LU.equals("L")){  
					/*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insA8YML012WbTCarIns
					INSERT INTO TB_YM_WRKBOOK WB
					       (WB.YD_WBOOK_ID         , WB.REGISTER              , WB.REG_DDTT              ,
					        WB.DEL_YN              , WB.YD_GP                 , WB.YD_BAY_GP             , WB.YD_SCH_CD   , WB.YD_SCH_PRIOR ,
					        WB.YD_SCH_PROG_STAT    , WB.YD_SCH_ST_GP          , WB.YD_SCH_REQ_GP         , WB.YD_AIM_YD_GP, WB.YD_AIM_BAY_GP,
					        WB.YD_TO_LOC_DCSN_MTD  , WB.YD_TO_LOC_GUIDE       , WB.YD_WRK_PLAN_TCAR)
					VALUES (:V_YD_WBOOK_ID         , :V_MODIFIER              ,  SYSDATE                 ,
					        :V_DEL_YN              , SUBSTR(:V_YD_SCH_CD,1,1) , SUBSTR(:V_YD_SCH_CD,2,1) , :V_YD_SCH_CD   , :V_YD_WRK_CRN_PRIOR ,
					        'W'                    , 'O'                      , '1'                      ,SUBSTR(:V_YD_SCH_CD,1,1) , :V_YD_AIM_BAY_GP    ,
					        NULL                   , NULL                     , :V_YD_EQP_ID)
		            */
					
					ydWrkBookId_load = commDao.getSeqId(logId, methodNm, "WrkBook");
					jrParam.setField("YD_WBOOK_ID"         , ydWrkBookId_load); //상차지작업예약ID
					jrParam.setField("DEL_YN"              , "Y"       	); 
					jrParam.setField("YD_SCH_CD"           , CarldSchCd); //하차지스케쥴 코드
					jrParam.setField("YD_WRK_CRN_PRIOR"    , LCrnPrior); //스케줄우선순위
					jrParam.setField("YD_AIM_BAY_GP"       , UnloadStopLoc.substring(1, 2)); //목표동
					
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insA8YML012WbTCarIns", logId, methodNm, "작업예약 하차지 등록");	
					}
				if(LU.equals("U")){  
					ydWrkBookId_Unload = commDao.getSeqId(logId, methodNm, "WrkBook");
					 jrParam.setField("YD_WBOOK_ID"         , ydWrkBookId_Unload); //하차야드작업예약ID
					 jrParam.setField("DEL_YN"              , "N"       	); 
					 jrParam.setField("YD_SCH_CD"           , CarudSchCd); //하차지스케쥴 코드
					 jrParam.setField("YD_WRK_CRN_PRIOR"    , UCrnPrior); //스케줄우선순위
					 jrParam.setField("YD_AIM_BAY_GP"       , UnloadStopLoc.substring(1, 2)); //목표동

				     commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insA8YML012WbTCarIns", logId, methodNm, "작업예약 하차지 등록");			
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
				    
				    // if(LU.equals("L")){ 
				        
				    	 jrParam.setField("YD_CARLD_WRK_BOOK_ID"       , ydWrkBookId_load   ); // 상차인 예약 id
				    // }else if(LU.equals("U")){
				       
				    	jrParam.setField("YD_CARUD_WRK_BOOK_ID"        , ydWrkBookId_Unload   ); // 하차인 예약 id
				    	 
				    // }else{
				    //	 break;
				     //}
				     jrParam.setField("YD_CARLD_STOP_LOC"          , LoadStopLoc       ); //상차정지위치
					 jrParam.setField("YD_CARUD_STOP_LOC"          , UnloadStopLoc     ); //하차정지위치
					 jrParam.setField("YD_CAR_PROG_STAT"           , ProgStat          ); //차량상태
					 
		    		commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateTcshbefore3", logId, methodNm, "초기 대차스케줄에 갱신");	 				     
					 
					}
				
				
				jrParam.setField("STACK_COL_GP" 	, commUtils.getValue(gdReq, "STACK_COL_GP", ii)); 
				jrParam.setField("STACK_BED_GP" 	, commUtils.getValue(gdReq, "STACK_BED_GP", ii)); 
				jrParam.setField("STACK_LAYER_GP"   , commUtils.getValue(gdReq, "STACK_LAYER_GP", ii)); 
				
				if(LU.equals("U")){  //
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
					jrParam.setField("YD_CARUD_WRK_BOOK_ID" 		, ydWrkBookId_Unload); 
					//jrParam.setField("STOCK_ID" 		, commUtils.getValue(gdReq, "STOCK_ID", ii)); 
					jrParam.setField("YD_UP_COLL_SEQ"   , commUtils.getValue(gdReq, "STACK_LAYER_GP", ii)); 
					
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insA7YML009WbMtlTCarIns", logId, methodNm, "작업예약재료 등록");
				}
				
			

		                // 대차Layer복구 
						/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStkLyrTCarStl3  
						UPDATE TB_YM_STACKLAYER
						   SET MODIFIER                = :V_MODIFIER
						      ,MOD_DDTT                = SYSDATE
						      ,STACK_LAYER_ACTIVE_STAT = 'E'
						      ,STACK_LAYER_STAT        = 'C'
						      ,STOCK_ID                = :V_STOCK_ID
						 WHERE STACK_COL_GP            = :V_STACK_COL_GP
						   AND STACK_LAYER_GP          = :V_STACK_LAYER_GP
						   AND STACK_LAYER_ACTIVE_STAT IN('C','E')
						   AND STACK_LAYER_STAT        ='E'
						   AND DEL_YN                  ='N'
						*/
						commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updStkLyrTCarStl3", logId, methodNm, "TC_LYAER복구");
				if(LU.equals("L")){			
						jrParam.setField("YD_CARUD_STOP_LOC", UnloadStopLoc); 
						jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L006", jrParam));
				
				}
				
				
				
				
				
				//대차재료 등록
				
				/*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getInformStock
				SELECT     
				      CC.HCR_GP
				    , CC.CURR_PROG_CD  AS STL_PROG_CD
				    , ST.STOCK_ITEM    AS YD_MTL_ITEM
				FROM TB_YM_STOCK     ST
				    , VW_YD_SLABCOMM  CC  
				WHERE  ST.STOCK_ID = CC.SLAB_NO
				AND  ST.STOCK_ID = :V_STOCK_ID
				*/
				
				JDTORecordSet jsStInfor = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getInformStock", logId, methodNm, "대차스케줄정보조회 "); 				
				String HcrGp      	= commUtils.trim(jsStInfor.getRecord(0).getFieldString("HCR_GP"));        //
				String StlProgCd 	= commUtils.trim(jsStInfor.getRecord(0).getFieldString("STL_PROG_CD")); //
				String YdMtlItem 	= commUtils.trim(jsStInfor.getRecord(0).getFieldString("YD_MTL_ITEM"));       //
				
				
				
				/*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insA8YML012TCarMtlIns   
				INSERT INTO TB_YM_TCARFTMVMTL        
				       (TM.YD_TCAR_SCH_ID, TM.STOCK_ID     , TM.REGISTER   , TM.REG_DDTT     ,
				        TM.MODIFIER      , TM.MOD_DDTT     , TM.DEL_YN     , TM.STACK_BED_GP,
				        TM.STACK_LAYER_GP, TM.HCR_GP       , TM.STL_PROG_CD, TM.YD_MTL_ITEM   )
				VALUES (:V_YD_TCAR_SCH_ID, :V_STOCK_ID     , :V_MODIFIER   , SYSDATE     ,
				        :V_MODIFIER      , NULL            , 'N'           , :V_STACK_BED_GP,
				        :V_STACK_LAYER_GP, :V_HCR_GP       , :V_STL_PROG_CD, :V_YD_MTL_ITEM   ) 				
				*/
				jrParam.setField("HCR_GP"        , HcrGp); 
				jrParam.setField("STL_PROG_CD"   , StlProgCd); 
				jrParam.setField("YD_MTL_ITEM"   , YdMtlItem); 
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insA8YML012TCarMtlIns", logId, methodNm, "대차재료 등록");
			}else{
			   throw new Exception("작업예약이  이미 존재 합니다.");
			}
		  }

			if(!("".equals(ydWrkBookId_Unload))&& LU.equals("U")){
				//JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
				

				String currDate   = commUtils.getDateTime14();	//현재시각
				
				jrParam.setField("JMS_TC_CD", "YMYMJ202"); //야드작업예약ID
				jrParam.setField("JMS_TC_CREATE_DDTT", currDate); //JMSTC생성일시				
				
				jrParam.setField("YD_WBOOK_ID", ydWrkBookId_Unload); //야드작업예약ID
				jrParam.setField("YD_SCH_CD"  , ""); //야드스케쥴코드
				jrParam.setField("YD_EQP_ID"  , ""); //야드설비ID
				
				//크레인스케줄기동 전문
				EJBConnector sndConn = new EJBConnector("default", "BSlabSchSeEJB", this);
				JDTORecord jrRtn2 = (JDTORecord)sndConn.trx("procYMYMJ202", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				
				jrRtn = commUtils.addSndData(jrRtn, jrRtn2); 
				
				
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
	 * 대차스케줄관리 - 대차초기화(화면)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord initTcarSchMgt(GridData gdReq) throws DAOException {
		String methodNm = "대차스케줄관리 대차초기화[BSlabJspSeEJB.initTcarSchMgt] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			String sEQUIP_GP     = ""; //야드설비ID(대차)
			//String ydCurrBayGp = ""; //야드현재동구분(신규)
			
			sEQUIP_GP = gdReq.getParam("TC_NO"); //대차번호
			
			/**********************************************************
			* 1. 정합성 체크 및 현재동, 상/하차동 조회
			**********************************************************/
			if ("".equals(sEQUIP_GP)) {
				throw new Exception("대차설비ID가 없습니다.");
			}
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			jrParam.setField("YD_EQP_ID"          , sEQUIP_GP); //대차
			jrParam.setField("YD_EQP_WRK_STAT"    , "U"); //야드설비작업상태(공차)
			jrParam.setField("YD_CARLD_SCH_REQ_GP", "6"); //야드상차스케쥴요청구분(공대차도착)
			jrParam.setField("YD_CARUD_SCH_REQ_GP", "3"); //야드하차스케쥴요청구분(영대차도착)
			jrParam.setField("YD_CAR_PROG_STAT"   , "0"); //야드차량진행상태(상차대기)

			JDTORecordSet rst = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.readListBSTCCTracking2");
			
			//SLAB는 동 6자리
			String sCURR_STOP_LOC      = "";//현재위치 
			String sCARLOAD_STOP_LOC   = "";//상차동 
			String sCARUNLOAD_STOP_LOC = "";//하차동 
			
			if (rst.size() > 0) {
				sCURR_STOP_LOC      = rst.getRecord(0).getFieldString("CURR_STOP_LOC");
				sCARLOAD_STOP_LOC   = rst.getRecord(0).getFieldString("CARLOAD_STOP_LOC");
				sCARUNLOAD_STOP_LOC = rst.getRecord(0).getFieldString("CARUNLOAD_STOP_LOC");
			}
			
			/**********************************************************
			 * 0. Map 등록 BACK-UP
			 *********************************************************/
			String sMAP_REG_YN = commUtils.nvl(gdReq.getParam("MAP_REG_YN"), "N");
			
			if ("Y".equals(sMAP_REG_YN)) {
				
				/*
				SELECT *
				  FROM TB_YM_STACKLAYER
				 WHERE STACK_COL_GP LIKE '2%'
				   AND STOCK_ID IN (SELECT TM.STOCK_ID
				                      FROM TB_YM_TCARSCH       TS
				                         , TB_YM_TCARFTMVMTL   TM
				                     WHERE TS.YD_TCAR_SCH_ID = TM.YD_TCAR_SCH_ID
				                       AND TS.DEL_YN    = 'N'
				                       AND TM.DEL_YN    = 'N'
				                       AND TS.YD_EQP_ID = :V_YD_EQP_ID
				                    )
				 */
				JDTORecordSet jrMtlList =  commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getTcarMtlStkListByEqpId");
				
				if (jrMtlList.size() > 0) {
					throw new Exception("해당 대차의 저장품은 야드맵에 존재합니다.");
				}
				/*
				MERGE INTO TB_YM_STACKLAYER SL USING (
				SELECT TS.YD_EQP_ID
				     , TM.STOCK_ID
				     , TS.YD_CARUD_STOP_LOC AS STACK_COL_GP
				     , TM.STACK_BED_GP
				     , TM.STACK_LAYER_GP
				  FROM TB_YM_TCARSCH       TS
				     , TB_YM_TCARFTMVMTL   TM
				 WHERE TS.YD_TCAR_SCH_ID = TM.YD_TCAR_SCH_ID(+)
				   AND TS.DEL_YN    = 'N'
				   AND TM.DEL_YN(+) = 'N'
				   AND TS.YD_EQP_ID = :V_YD_EQP_ID
				) DD ON (SL.STACK_COL_GP   = DD.STACK_COL_GP 
				     AND SL.STACK_BED_GP   = DD.STACK_BED_GP
				     AND SL.STACK_LAYER_GP = DD.STACK_LAYER_GP)   
				WHEN MATCHED THEN UPDATE SET 
				     SL.STOCK_ID         = DD.STOCK_ID
				   , SL.STACK_LAYER_STAT = 'C'
				   , SL.MODIFIER         = 'YM-BACKUP'
				   , SL.MOD_DDTT         = SYSDATE
				 */
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStackLayerTcarMtlBackUp");
				return jrRtn;
			}
			
			/**********************************************************
			* 2. 기존 대차스케줄/재료 삭제
			**********************************************************/
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
			jrParam.setField("YD_CARLD_STOP_LOC", sCARLOAD_STOP_LOC); //야드상차정지위치
			
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
			jrParam.setField("EQUIP_GP"      , sEQUIP_GP     );
			jrParam.setField("YD_CURR_BAY_GP", sCURR_STOP_LOC);

			jrRtn = commUtils.addSndData(jrRtn, this.updTcarCurrBay(jrParam));
			//slab대차는 현재동 설정부분이 없으므로 현재동 변경 로직은 실행안함
				
				
			//}

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
		String methodNm = "대차 현재동 변경[BSlabJspSeEJB.updTcarCurrBay] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		//Return Value
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
		try {
			commUtils.printLog(logId, methodNm, "S+");

			String ydEqpId 		       = commUtils.trim(rcvMsg.getFieldString("EQUIP_GP"     ));  //야드설비ID(대차)
			String sYD_CURR_BAY_GP_NEW = commUtils.trim(rcvMsg.getFieldString("YD_CURR_BAY_GP")); //야드신규동구분 6자리

			if ("".equals(ydEqpId)) {
				throw new Exception("설비ID가 없습니다.");
			} else if ("".equals(sYD_CURR_BAY_GP_NEW)) {
				throw new Exception("변경할 현재동이 없습니다.");
			}

	
//			String ydBayGpCurr  	= ""; //야드현재동구분(현재)
//			String ydStkColGpCurr   = ""; //야드적치열구분(현재)
			String sCURR_STOP_LOC = ""; //야드현재동구분(현재)-SLAB의 경우 ex) 2ATC12
			//String ydStkColGpNew    = ydEqpId.substring(0, 1) + ydBayGpNew + ydEqpId.substring(2); //야드적치열(신규)
//			String ydStkColGpNew    = ydBayGpNew; //야드적치열(신규)

			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(rcvMsg.getFieldString("MODIFIER")));

			jrParam.setField("YD_EQP_ID"         , ydEqpId       );
			jrParam.setField("YD_CURR_BAY_GP_NEW", sYD_CURR_BAY_GP_NEW);
			
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
	
		    	sCURR_STOP_LOC	= commUtils.trim(jrTcar.getFieldString("CURR_STOP_LOC"));//대차현재위치
	
//			    if ("".equals(sYD_CURR_BAY_GP_NEW)) {
//					throw new Exception("변경할 적치열이 없습니다.");
//				}
		    } else {
				throw new Exception("대차 Bed상태 정보가 없습니다.");
		    }

			/**********************************************************
			* 2. 대차 저장위치 전체 비 활성화
			**********************************************************/
			jrParam.setField("STACK_COL_GP", sCURR_STOP_LOC); //야드적치열구분(대차전체Bed)

			//적치Bed(전체) 비활성화
			/*  
			UPDATE TB_YM_STACKER
			   SET MODIFIER            = :V_MODIFIER
			     , MOD_DDTT            = SYSDATE
			     , STACK_BED_ACTIVE_STAT = 'C'   --비활성화
			 WHERE STACK_COL_GP  LIKE '2_TC'||SUBSTR(:V_STACK_COL_GP,5,1)||'%' -- 2ATC11
			   AND DEL_YN              = 'N'
			*/	   
			commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStatStkBedActCA", logId, methodNm, "적치Bed(전체) 비활성화");

			//적치단(전체) 재료 삭제
			/* 
			UPDATE TB_YM_STACKLAYER
			   SET MODIFIER                = :V_MODIFIER
			     , MOD_DDTT                = SYSDATE
			     , STOCK_ID                = NULL
			     , STACK_LAYER_ACTIVE_STAT = 'C'
			     , STACK_LAYER_STAT        = 'E'
			 WHERE STACK_COL_GP  LIKE  '2_TC'||SUBSTR(:V_STACK_COL_GP,5,1)||'%'
			   AND DEL_YN                  = 'N'
			*/	   
			commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updYdStkLyrClr1", logId, methodNm, "적치단 재료 삭제");

			/**********************************************************
			* 3. 현재동 변경 및 저장위치제원 전문 조회
			**********************************************************/
			if (!sCURR_STOP_LOC.equals(sYD_CURR_BAY_GP_NEW)) {
				//설비 현재동 수정
				jrParam.setField("STACK_COL_GP", sYD_CURR_BAY_GP_NEW);

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
				jrParam.setField("STACK_COL_GP"   , sYD_CURR_BAY_GP_NEW); //야드적치열구분
				jrParam.setField("STACK_BED_GP"   , "01"         ); //야드적치Bed번호

				//전송Data 조회
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L001", jrParam));
			}
			
			/**********************************************************
			* 4. 신규 저장위치  활성화 및 저장위치제원 전문 조회
			**********************************************************/
			//신규 적치Bed Close 상태이면 활성화
			jrParam.setField("STACK_COL_GP"      	, sYD_CURR_BAY_GP_NEW); //야드적치열구분
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
			/* 
			UPDATE TB_YM_STACKLAYER
			   SET MODIFIER                = :V_MODIFIER
			     , MOD_DDTT                = SYSDATE
			     , STOCK_ID                = NULL
			     , STACK_LAYER_ACTIVE_STAT = 'E'
			     , STACK_LAYER_STAT        = 'E'
			 WHERE STACK_COL_GP            = :V_STACK_COL_GP
			   AND DEL_YN                  = 'N' 
			*/	   
			commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updYdStkLyrActiveTC", logId, methodNm, "신규 적치Bed Close 상태이면 활성화");
			

			//신규 Bed의 상태가 변경되었으면 저장위치제원(YDY1L001, YDY3L001) 전문 전송
			jrParam.setField("YD_INFO_SYNC_CD", "3"); //야드정보동기화코드(Bed)
			jrParam.setField("STACK_BED_GP"   , "");  
			//전송Data 조회
			jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L001", jrParam));

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
		String methodNm = "대차상태설정 등록처리[BSlabJspFaEJB.trtTcarStatSet] < " + gdReq.getNavigateValue();
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
				jrYdMsg.setField("JMS_TC_CD"          , "A8YML004"); //설비고장복구실적
				jrYdMsg.setField("YD_EQP_STAT"        , commUtils.trim(gdReq.getParam("YD_EQP_STAT"))); //야드설비상태(B:고장, N:정상)
				jrYdMsg.setField("YD_EQP_PAUSE_CODE"  , "0000"    ); //야드설비휴지코드
				jrYdMsg.setField("YD_EQP_TRBL_RCVR_DT", currDate  ); //야드설비고장복구일시

				EJBConnector sndConn = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvA8YML004", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
			} else if ("MD".equals(trtDtlGp)) {
				//작업Mode 변경
				jrYdMsg.setField("JMS_TC_CD"      , "A8YML003"); //설비운전모드전환
				jrYdMsg.setField("YD_EQP_WRK_MODE", commUtils.trim(gdReq.getParam("YD_EQP_WRK_MODE"))); //야드설비작업Mode(1:On-Line, 0:Off-Line)

				EJBConnector sndConn = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvA8YML003", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
			} else if ("HB".equals(trtDtlGp)) {
				//Home동 변경 - Log ID, Method, 수정자 Set
				JDTORecord jrParam = commUtils.getParam(logId, methodNm, modifier);

				jrParam.setField("EQUIP_GP"   , ydEqpId); //야드설비ID
				jrParam.setField("YD_HOME_LOC", commUtils.trim(gdReq.getParam("YD_HOME_BAY_GP")));
				
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
				jrYdMsg.setField("YD_CURR_BAY_GP", commUtils.trim(gdReq.getParam("YD_CURR_BAY_GP"))); //신규현재동
				jrRtn = this.updTcarCurrBay(jrYdMsg);
				
			} else if ("TS".equals(trtDtlGp)) {
				//공대차출발지시 등록
				jrYdMsg.setField("YD_BAY_GP", commUtils.trim(gdReq.getParam("YD_CURR_BAY_GP_TS"))); //야드동구분(상차동)
				jrYdMsg.setField("OPRN"     , sOPRN); //영대차여부
				jrRtn = ymComm.trtTcarSchLevWo_Slab(jrYdMsg);
			} else if ("TL".equals(trtDtlGp)) {
				//출발실적처리
				jrYdMsg.setField("JMS_TC_CD"      	, "A8YML011"); //대차이동실적
				jrYdMsg.setField("YD_MOVE_GP"		, "S"       ); //야드대차이동구분(출발)
				jrYdMsg.setField("YD_TCAR_CURR_BAY" , commUtils.trim(gdReq.getParam("YD_CURR_BAY_GP_TL"))); //야드동구분1(현재동)

				EJBConnector sndConn = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvA8YML011", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
			} else if ("TA".equals(trtDtlGp)) {
				//도착실적처리
				jrYdMsg.setField("JMS_TC_CD"      	, "A8YML011"); //대차이동실적
				jrYdMsg.setField("YD_MOVE_GP"		, "E"       ); //야드대차이동구분(도착)
				jrYdMsg.setField("YD_TCAR_CURR_BAY" , commUtils.trim(gdReq.getParam("YD_CURR_BAY_GP_TA"))); //야드동구분1(현재동)

				EJBConnector sndConn = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
				jrRtn = (JDTORecord)sndConn.trx("rcvA8YML011", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
			} else if ("TC".equals(trtDtlGp)) {
				//완료실적처리
				String ydCarProgStat = commUtils.trim(gdReq.getHeader("YD_CAR_PROG_STAT").getValue(0));	//야드차량진행상태
				
//				if ("4".equals(ydCarProgStat)) { 
					//상차개시 -> 상차완료
					/*
					UPDATE TB_YM_TCARSCH
					   SET YD_CAR_PROG_STAT = :V_YD_CAR_PROG_STAT
					     , MODIFIER         = :V_MODIFIER
					     . MOD_DDTT         = SYSDATE
					 WHERE DEL_YN    = 'N'
					   AND YD_EQP_ID = :V_YD_EQP_ID
					 */
					jrYdMsg.setField("YD_CAR_PROG_STAT", "5"); //상차완료
					commDao.update(jrYdMsg, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTcarSchProgStatByEqpId");
					
					//대차출발지시
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YMA8L006BACKUP", jrYdMsg));
//				}
				
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
		
	
	//이송지시 상차 등록 - SLAB 이송상차 지시
	/**
	 * SLAB 이송상차 지시- 등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord regCARLD(GridData gdReq) throws DAOException {
		String methodNm = "SLAB 이송상차 지시- 등록[BSlabJspSeEJB.regCARLD] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			String sYD_GP            = gdReq.getParam("YD_GP"); //야드
			String sBAY_GP           = gdReq.getParam("BAY_GP"); //동
			String sYD_CARPNT_CD     = gdReq.getParam("YD_CARPNT_CD"); //차량 포인트
			
			String sYD_SCH_CD        = ""; //스케줄
			String sYD_TO_LOC_GUIDE  = "";
			String sTRN_EQP_CD       = "";
			String szMsg             = "";
			String sFRTOMOVE_WORD_NO = ""; //이송작업지시 번호
			//String sARR_WLOC_CD2 		= commUtils.getValue(gdReq, "ARR_WLOC_CD2"         , ii);
			String sARR_WLOC_CD      = commUtils.getValue(gdReq, "ARR_WLOC_CD2"         , 0);
			
			
			//Return Value
			JDTORecord    jrRtn     = null;
			JDTORecord    jrRtn2     = null;
			//JDTORecordSet rsResult = null;
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			String sLoc_CBL_GP      = "";
			String ydWbookId        = "";
			
			//이송 작업지시 번호 가져오기
			sFRTOMOVE_WORD_NO = commDao.getSeqId(logId, methodNm, "FtMvWo");
			
			//스케줄 기동용 전문 세팅
			JDTORecord jrMsg = JDTORecordFactory.getInstance().create();
			jrMsg.setField("JMS_TC_CD"    , "YMYMJ203"); 
			jrMsg.setField("YD_SCH_CD"    , ""  ); //야드스케쥴코드
			jrMsg.setField("YD_EQP_ID"    , ""  ); //야드설비ID
			
			int wbIdx = 0;
			
			for (int ii = 0; ii < rowCnt; ii++) {
				String sSTOCK_ID 		= commUtils.getValue(gdReq, "STOCK_ID"         , ii);
				String sSTOCK_CBL_GP    = commUtils.getValue(gdReq, "STOCK_CBL_GP"     , ii).substring(0,8); //현재 위치 값(from위치)
								
				String sSTACK_COL_GP    = commUtils.getValue(gdReq, "STOCK_CBL_GP", ii).substring(0, 6);
				String sBED_GP          = commUtils.getValue(gdReq, "STOCK_CBL_GP", ii).substring(6, 8);
				String sSTACK_LAYER_GP  = commUtils.getValue(gdReq, "STOCK_CBL_GP", ii).substring(8, 10);
				sYD_SCH_CD 		        = sYD_GP + sBAY_GP + "PT02UM";
				
				if("".equals(sLoc_CBL_GP)||!sSTOCK_CBL_GP.equals(sLoc_CBL_GP)){ //if (임시위치 ="" || !임시위치 = 해당위치(현재 위치 값 (from위치))
					
					sLoc_CBL_GP         = commUtils.getValue(gdReq, "STOCK_CBL_GP"   , ii).substring(0,8); //임시위치 = commUtils.getValue(gdReq, "STACK_CBL_GP"   , ii).substring(0,8);
					
//					if (!"".equals(ydWbookId)) {
//						/**********************************************************
//						* 3. 크레인 스케줄 호출
//						**********************************************************/
//						JDTORecord jrMsg = JDTORecordFactory.getInstance().create();
//						jrMsg.setField("JMS_TC_CD"    , "YMYMJ202"); 
//						jrMsg.setField("YD_WBOOK_ID"  , ydWbookId); //야드작업예약ID
//						jrMsg.setField("YD_SCH_CD"    , ""  ); //야드스케쥴코드
//						jrMsg.setField("YD_EQP_ID"    , ""  ); //야드설비ID
//						
//						EJBConnector ejbConn = new EJBConnector("default","BSlabSchSeEJB",this);
//						JDTORecord jrRtn1 = (JDTORecord)ejbConn.trx("procYMYMJ202",new Class[]{JDTORecord.class},new Object[]{ jrMsg });	
//						
//						jrRtn = commUtils.addSndData(jrRtn,jrRtn1);
//					}
					
					/********************************************
					 *** 2. 작업예약 ID 가져오기
					 ********************************************/
					ydWbookId = commDao.getSeqId(logId, methodNm, "WrkBook"); //예약ID = 작업예약 생성
					jrMsg.setField("YD_WBOOK_ID" + ++wbIdx, ydWbookId); //작업예약
					
					if("2APT01".equals(sYD_CARPNT_CD)){
						sYD_SCH_CD = "2APT02UM";
						sYD_TO_LOC_GUIDE = "2APT01";
					}else if("2APT02".equals(sYD_CARPNT_CD)){
						sYD_SCH_CD = "2APT02UM";
						sYD_TO_LOC_GUIDE = "2APT02";
					}else if("2DPT01".equals(sYD_CARPNT_CD)){
						sYD_SCH_CD = "2DPT02UM";
						sYD_TO_LOC_GUIDE = "2DPT01";
					}else if("2DPT02".equals(sYD_CARPNT_CD)){
						sYD_SCH_CD = "2DPT02UM";
						sYD_TO_LOC_GUIDE = "2DPT02";
					}else if("2DPT03".equals(sYD_CARPNT_CD)){
						sYD_SCH_CD = "2DPT02UM";
						sYD_TO_LOC_GUIDE = "2DPT03";
					}else if("2DPT04".equals(sYD_CARPNT_CD)){
						sYD_SCH_CD = "2DPT02UM";
						sYD_TO_LOC_GUIDE = "2DPT04";
					}else if("2EPT01".equals(sYD_CARPNT_CD)){
						sYD_SCH_CD = "2EPT02UM";
						sYD_TO_LOC_GUIDE = "2EPT01";
					}else if("2EPT02".equals(sYD_CARPNT_CD)){
						sYD_SCH_CD = "2EPT02UM";
						sYD_TO_LOC_GUIDE = "2EPT02";
					}else if("2EPT03".equals(sYD_CARPNT_CD)){
						sYD_SCH_CD = "2EPT02UM";
						sYD_TO_LOC_GUIDE = "2EPT03";
					}else if("2EPT04".equals(sYD_CARPNT_CD)){
						sYD_SCH_CD = "2EPT02UM";
						sYD_TO_LOC_GUIDE = "2EPT04";
					}
									
					 /********************************************
					  *** 2. 운송장비 코드 가져오기
					  ********************************************/

					jrParam.setField("YD_STK_COL_GP"		, sYD_TO_LOC_GUIDE);
					
					/*
					 SELECT TRN_EQP_CD, YD_STK_COL_GP 
	  				   FROM TB_YD_CARPOINT
	                  WHERE DEL_YN = 'N'
	                    AND YD_STK_COL_GP = :V_YD_STK_COL_GP*/
					 
					
					JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getFtmvCarLoad", logId, methodNm, "이송지시 상차등록"); 
					
					if(jsChk.size() > 0) {
						sTRN_EQP_CD = jsChk.getRecord(0).getFieldString("TRN_EQP_CD");
					} else {
					
			    		szMsg = gdReq.getParam("YD_CARPNT_CD") + " 해당 위치에 차량이 없습니다.";
			    		
			    		//jrRtn.setField("RTN_MSG", szMsg);
			    		
						szMsg="["+methodNm+"] " + szMsg;
						commUtils.printLog(logId, szMsg, "SL");
						
						return jrRtn;
					}

						jrParam.setField("YD_WBOOK_ID"			, ydWbookId);
						jrParam.setField("YD_GP"      			, sYD_GP);
						jrParam.setField("YD_BAY_GP"  			, sBAY_GP);
						jrParam.setField("YD_SCH_CD"        	, sYD_SCH_CD);
						jrParam.setField("YD_SCH_PRIOR"      	, "");
						jrParam.setField("YD_SCH_PROG_STAT"  	, "W");//야드스케쥴진행상태(스케줄수행대기)
						jrParam.setField("YD_SCH_ST_GP"      	, "O");//야드스케쥴기동구분(Manual)
						jrParam.setField("YD_SCH_REQ_GP"     	, "M");//야드스케쥴요청구분(이적)
						jrParam.setField("YD_AIM_YD_GP"      	, "");
						jrParam.setField("YD_AIM_BAY_GP"     	, sBAY_GP);
						jrParam.setField("YD_TO_LOC_DCSN_MTD"	, "O"); 
						jrParam.setField("YD_TO_LOC_GUIDE"   	, sYD_TO_LOC_GUIDE);    
						jrParam.setField("YD_WRK_PLAN_TCAR"  	, "");   
						jrParam.setField("YD_CAR_USE_GP"     	, "L");      
						jrParam.setField("TRN_EQP_CD"       	, sTRN_EQP_CD); 
						jrParam.setField("CAR_NO"            	, "");             
						jrParam.setField("CARD_NO"           	, "");            
						jrParam.setField("PTOP_PLNT_GP"      	, "");       
						jrParam.setField("DEST_TEL_NO"       	, "");        
						jrParam.setField("DIST_SHIPASSIGN_GP"	, ""); 
						jrParam.setField("YD_WRK_PLAN_CRN"   	, "");    
						
						/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule  
						SELECT YD_SCH_CD
						     , YD_WRK_CRN
						     , YD_WRK_CRN_PRIOR
						  FROM TB_YM_SCHEDULERULE
						 WHERE DEL_YN = 'N'
						   AND YD_SCH_CD = :V_YD_SCH_CD
						*/   
						String sYD_WRK_CRN = "";
						JDTORecordSet jsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
						if (jsResult != null && jsResult.size() > 0) {
							String sYD_SCH_PRIOR = jsResult.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
							jrParam.setField("YD_SCH_PRIOR"      , sYD_SCH_PRIOR);
							
							sYD_WRK_CRN = jsResult.getRecord(0).getFieldString("YD_WRK_CRN"); //크레인
						} 
						
						
						//크레인 작업불가 적치BED 조회 TB_YM_RULE : YM2021
						JDTORecord jrYmParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
						jrYmParam.setField("YD_EQP_ID"       , sYD_WRK_CRN); //크레인정보
						jrYmParam.setField("STACK_COL_BED_GP", sSTOCK_CBL_GP); //적치위치
						JDTORecordSet ym2021Rule = commDao.select(jrYmParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.selectRuleYM2021Crn", logId, methodNm, "SLAB 크레인작업불가 적치금지 BED 조건조회");
						
						if (ym2021Rule.size() > 0) {
							throw new Exception("이적대상재의 위치가 ["+sYD_WRK_CRN.substring(4, 6)+"]크레인 작업불가 적치BED에 적치되어 있습니다.");
						} 
						

						jrYmParam.setField("STACK_COL_GP", sSTACK_COL_GP); //적치위치
						JDTORecordSet ym2010Rule = commDao.select(jrYmParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.selectRuleYM2010Crn", logId, methodNm, "SLAB 크레인작업불가 적치열금지 조건조회");
						
						if (ym2010Rule.size() > 0) {
							throw new Exception("이적대상재의 위치가 ["+sYD_WRK_CRN.substring(4, 6)+"]크레인 작업불가 적치열에 적치되어 있습니다.");
						}
						
						/*
						INSERT INTO TB_YM_WRKBOOK (
						       YD_WBOOK_ID        --야드작업예약ID
						     , YD_GP              --야드구분
						     , YD_BAY_GP          --야드동구분
						     , YD_SCH_CD          --야드스케쥴코드
						     , YD_SCH_PRIOR       --야드스케쥴우선순위
						     , YD_SCH_PROG_STAT   --야드스케쥴진행상태
						     , YD_SCH_ST_GP       --야드스케쥴기동구분
						     , YD_SCH_REQ_GP      --야드스케쥴요청구분
						     , YD_AIM_YD_GP       --야드목표야드구분
						     , YD_AIM_BAY_GP      --야드목표동구분
						     , YD_TO_LOC_DCSN_MTD --야드To위치결정방법
						     , YD_TO_LOC_GUIDE    --야드To위치Guide
						     , YD_WRK_PLAN_TCAR   --야드작업계획대차
						     , YD_CAR_USE_GP      --야드차량사용구분
						     , TRN_EQP_CD         --운송장비코드
						     , CAR_NO             --차량번호
						     , CARD_NO            --카드번호
						     , PTOP_PLNT_GP       --조업공장구분
						     , DEST_TEL_NO        --목적지전화번호
						     , DIST_SHIPASSIGN_GP --출하배선지시구분 
						     , YD_WRK_PLAN_CRN    --야드작업계획크레인
						     , REGISTER           --등록자
						     , REG_DDTT           --등록일시
						     , MODIFIER           --수정자
						     , MOD_DDTT           --수정일시
						     , DEL_YN             --삭제유무
						) VALUES (
						      :V_YD_WBOOK_ID
						     ,:V_YD_GP
						     ,:V_YD_BAY_GP
						     ,:V_YD_SCH_CD
						     ,TO_NUMBER(:V_YD_SCH_PRIOR)
						     ,:V_YD_SCH_PROG_STAT
						     ,:V_YD_SCH_ST_GP
						     ,:V_YD_SCH_REQ_GP
						     ,:V_YD_AIM_YD_GP
						     ,:V_YD_AIM_BAY_GP
						     ,:V_YD_TO_LOC_DCSN_MTD
						     ,:V_YD_TO_LOC_GUIDE
						     ,:V_YD_WRK_PLAN_TCAR
						     ,:V_YD_CAR_USE_GP
						     ,:V_TRN_EQP_CD
						     ,:V_CAR_NO
						     ,:V_CARD_NO
						     ,:V_PTOP_PLNT_GP
						     ,:V_DEST_TEL_NO
						     ,:V_DIST_SHIPASSIGN_GP
						     ,:V_YD_WRK_PLAN_CRN      
						     ,:V_MODIFIER
						     ,SYSDATE
						     ,:V_MODIFIER
						     ,SYSDATE
						     ,'N'
						)
						 */
						bSlabComm.execQueryId(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insWrkBook"); //작업예약 생성
				}
					
						/**********************************************************
						* 2-2. 작업예약재료(TB_YM_WRKBOOKMTL) 생성
						**********************************************************/
						/*String YDWbookId = commDao.getSeqId(logId, methodNm, "WrkBook");*/
				
						jrParam.setField("YD_WBOOK_ID"		, ydWbookId);
						jrParam.setField("STOCK_ID"			, sSTOCK_ID);
						jrParam.setField("STACK_COL_GP"		, sSTACK_COL_GP);
						jrParam.setField("STACK_BED_GP"		, sBED_GP);
						jrParam.setField("STACK_LAYER_GP"	, sSTACK_LAYER_GP);
					
						bSlabComm.execQueryId(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insYmWrkBookMtl");
						
						/**********************************************************
						* 1-3. TB_YM_STOCK의이송작업지시번호(TRANS_WORD_NO) 등록
						**********************************************************/
						jrParam.setField("STOCK_ID"			, sSTOCK_ID);
						jrParam.setField("STOCK_MOVE_TERM"	, ""); //이송대기
						jrParam.setField("FRTOMOVE_WORD_NO"	, sFRTOMOVE_WORD_NO); //이송작업지시번호
						/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockTransWordNo
						UPDATE TB_YM_STOCK
						   SET MODIFIER   = :V_MODIFIER
						     , MOD_DDTT   = SYSDATE 
						     , STOCK_MOVE_TERM = NVL(:V_STOCK_MOVE_TERM,STOCK_MOVE_TERM)
						     , FRTOMOVE_WORD_NO = :V_FRTOMOVE_WORD_NO
						WHERE STOCK_ID = :V_STOCK_ID */
						commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockTransWordNo", logId, methodNm, "TB_YM_STOCK의 이송작업지시번호(TRANS_WORD_NO) 등록");
						
						/*
						jrParam.setField("SEARCH_FLAG" , "3");		//1:상차도, 2:차량스케쥴 ID, 3:차량상차 포인트  
						jrParam.setField("PT_LOAD_LOC" , sSTACK_COL_GP); 	//상차도 위치
						jrParam.setField("YD_WBOOK_ID"  , ydWbookId); //야드작업예약ID
						jrRtn = commUtils.addSndData(jrRtn, ymComm.procCarPlanInfo_Slab(jrParam));*/

						
						/*JDTORecord jrCrnSchMsg = JDTORecordFactory.getInstance().create();
						jrCrnSchMsg.setField("JMS_TC_CD"			, "YMYMJ202"); 
						jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시				
						jrCrnSchMsg.setField("YD_WBOOK_ID"  		, YDWbookId); //작업예약ID
						jrCrnSchMsg.setField("YD_SCH_CD"  		, ""); //야드스케쥴코드
						jrCrnSchMsg.setField("YD_EQP_ID"  		, ""); //야드설비ID
						jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);*/	
				
		
			}//for문
			
						
			/**********************************************************
			* 차량스케줄 업데이트
			**********************************************************/
			jrParam.setField("FRTOMOVE_WORD_NO"	  , sFRTOMOVE_WORD_NO); //이송작업지시번호
			jrParam.setField("YD_CARUD_STOP_LOC"  , sYD_CARPNT_CD);
			jrParam.setField("ARR_WLOC_CD"       , sARR_WLOC_CD);
			
			commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updCarFrtpmoveWordNo", logId, methodNm, "TB_YD_CARSCH의  등록");
			  
			
			/**********************************************************
			* 차량작업 예정정보 송신 (YMA8L008)
			**********************************************************/
//			jrParam.setField("SEARCH_FLAG" , "3");		//1:상차도, 2:차량스케쥴 ID, 3:차량상차 포인트  
//			jrParam.setField("PT_LOAD_LOC" , sYD_CARPNT_CD); 	//상차도 위치
//			jrParam.setField("FRTOMOVE_WORD_NO"	, sFRTOMOVE_WORD_NO); //이송작업지시번호
//			jrRtn = commUtils.addSndData(jrRtn, ymComm.procCarPlanInfo_Slab(jrParam));
			
			/**********************************************************
			* 3. 크레인 스케줄 호출
			**********************************************************/
			// 20171113  김기훈 주임 요청 스케줄 기동하지 않음
//			jrMsg.setField("SCH_CNT", Integer.toString(wbIdx));
//			EJBConnector ejbConn = new EJBConnector("default","BSlabSchSeEJB",this);
//			jrRtn2 = (JDTORecord)ejbConn.trx("rcvYMYMJ203",new Class[]{JDTORecord.class},new Object[]{ jrMsg });	
//			
//			jrRtn = commUtils.addSndData(jrRtn,jrRtn2);
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of regCARLD
	
	
	//CTC 지시폭 수정
	/**
	 * CTC 지시폭 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updSlabWMgt(GridData gdReq) throws DAOException {
		String methodNm = "CTC 지시폭 변경[BSlabJspSeEJB.updSlabWMgt] < " + gdReq.getNavigateValue();
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
					   
				jrParam.setField("STOCK_ID"    , commUtils.getValue(gdReq, "STOCK_ID", ii));       
				jrParam.setField("JISI_SLAB_W"      , commUtils.getValue(gdReq, "JISI_SLAB_W"      , ii));            
				
					/* UPDATE TB_CT_L_HRMILLWO
						  SET SLAB_W = :V_JISI_SLAB_W,
						      MODIFIER = :V_MODIFIER,
						      MOD_DDTT = SYSDATE
						WHERE SLAB_NO = :V_STOCK_ID   */
								
				
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updSlabInfo", logId, methodNm, "CTC 폭 변경");
								
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updSlabWMgt
	
	/**
	 * B열연 SLAB 벤딩표시,해제,보급 설정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updBendingStat(GridData gdReq) throws DAOException {
		String methodNm = "B열연 SLAB 벤딩표시,해제,보급 설정 - LAYER활성상태 변경[BSlabJspSeEJB.updBedActStat] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			String sGUBUN 			= commUtils.trim(gdReq.getParam("GUBUN")			); //1:벤딩표시,2:벤딩해제,3:벤딩보급
			String sBENDING_GP 		= commUtils.trim(gdReq.getParam("BENDING_GP")		); //BENDING구분(+:상,-:하)
			String sBENDING_AXIS 	= commUtils.nvl( gdReq.getParam("BENDING_AXIS"),"0"	); //BENDING량(mm)
			String sARR_STOCK_ID 	= commUtils.trim(gdReq.getParam("ARR_STOCK_ID")		); //대상 SLAB 리스트
			
			String sSTOCK_ID;
			String sSTOCK_MOVE_TERM;
			
			//대상 SLAB 리스트를 구한다.
			jrParam.setField("ARR_STOCK_ID"		, sARR_STOCK_ID);
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getStockIdList
			SELECT REGEXP_SUBSTR(SSTL_NOS, '[^,]+', 1, LEVEL) AS STOCK_ID
			  FROM (SELECT :V_ARR_STOCK_ID AS SSTL_NOS FROM DUAL)
			CONNECT BY REGEXP_SUBSTR(SSTL_NOS, '[^,]+', 1, LEVEL) IS NOT NULL  */
			JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getStockIdList", logId, methodNm, "대상 SLAB 리스트를 구한다.");
			if(rsResult.size() <= 0) {
				throw new Exception("대상 SLAB 리스트가 없습니다!!");
			}				
			
			for(int ii = 0; ii < rsResult.size(); ii++) {
				
				sSTOCK_ID = commUtils.trim(rsResult.getRecord(ii).getFieldString("STOCK_ID"));
				sSTOCK_MOVE_TERM =  bSlabComm.getStockMoveTerm(sSTOCK_ID);

				if("1".equals(sGUBUN)) {
					jrParam.setField("YD_RULE_PL_RS_GP"		, "Y"); //표시
				} else if("2".equals(sGUBUN)) {
					jrParam.setField("YD_RULE_PL_RS_GP"		, "" ); //해제
				} else if("3".equals(sGUBUN)) {
					jrParam.setField("YD_RULE_PL_RS_GP"		, "S"); //보급
				} else {
					throw new Exception("작업구분 오류!! GUBUN:"+sGUBUN);
				}
			
				jrParam.setField("STOCK_MOVE_TERM"	, sSTOCK_MOVE_TERM);
				jrParam.setField("BENDING_GP"		, sBENDING_GP); 
				jrParam.setField("BENDING_AXIS"		, sBENDING_AXIS); 
				jrParam.setField("STOCK_ID"			, sSTOCK_ID); 
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockBendingStat
				UPDATE TB_YM_STOCK
				SET    MODIFIER = :V_MODIFIER
				      ,MOD_DDTT = SYSDATE
				      ,STOCK_MOVE_TERM = :V_STOCK_MOVE_TERM
				      ,YD_RULE_PL_RS_GP = :V_YD_RULE_PL_RS_GP
				      ,BENDING_GP = :V_BENDING_GP
				      ,BENDING_AXIS = :V_BENDING_AXIS
				WHERE  STOCK_ID = :V_STOCK_ID */
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockBendingStat", logId, methodNm, "저장품 Bending상태 변경");
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updBendingStat	
	
	/**
	 * B차량포인트 상태 변경(사용가능,사용불가)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updCarPointStat(GridData gdReq) throws DAOException {
		String methodNm = "차량포인트 상태 변경(사용가능,사용불가)[BSlabJspSeEJB.updCarPointStat] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			String sYD_STK_COL_ACT_STAT = commUtils.trim(gdReq.getParam("YD_STK_COL_ACT_STAT")	); 
			String sYD_STK_COL_GP 		= commUtils.trim(gdReq.getParam("YD_STK_COL_GP")		); 
			
			if("C".equals(sYD_STK_COL_ACT_STAT)) {
				//사용가능

				jrParam.setField("YD_STK_COL_GP"		, sYD_STK_COL_GP);
				JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getYdCarPnt", logId, methodNm, "차량포인트 조회");
				if(rsResult.size() <= 0) {
					throw new Exception("대상 차량 Point 가 리스트에 없습니다!!");
				}
				String sTRN_EQP_CD = rsResult.getRecord(0).getFieldString("TRN_EQP_CD");
				String sYD_CAR_PROG_STAT = rsResult.getRecord(0).getFieldString("YD_CAR_PROG_STAT");
				
				if(!"".equals(sTRN_EQP_CD)) {
					if("1".equals(sYD_CAR_PROG_STAT)||"A".equals(sYD_CAR_PROG_STAT)) {
						sYD_STK_COL_ACT_STAT = "R";
					} else {
						sYD_STK_COL_ACT_STAT = "L";
					}
				} 
			}  
			
			jrParam.setField("YD_STK_COL_ACT_STAT"	, sYD_STK_COL_ACT_STAT);
			jrParam.setField("YD_STK_COL_GP"		, sYD_STK_COL_GP); 
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdCarPointActStat
			UPDATE  TB_YD_CARPOINT
			   SET  MODIFIER = :V_MODIFIER
			       ,MOD_DDTT = SYSDATE
			       ,YD_STK_COL_ACT_STAT = :V_YD_STK_COL_ACT_STAT
			 WHERE  YD_STK_COL_GP = :V_YD_STK_COL_GP      */
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdCarPointActStat", logId, methodNm, "차량포인트 적치열활성상태 UPDATE");
			
			
			/**********************************************************
			* 2. 저장위치제원(YMA8L001) 전문 생성
			**********************************************************/
			jrParam.setField("YD_INFO_SYNC_CD"	, "4"            ); //야드정보동기화코드
			jrParam.setField("STACK_COL_GP"  	, sYD_STK_COL_GP ); //야드적치열구분
			jrParam.setField("STACK_BED_GP"  	, "01"           ); //야드적치Bed번호

			//전송 Data 생성
			jrRtn = commUtils.addSndData(commDao.getMsgL2("YMA8L001", jrParam));
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updCarPointStat		
	
	/**
	 *      [A] 오퍼레이션명 : 이송차량 실적처리 팝업 - 등록
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtMvCarStatSet2(GridData gdReq) throws DAOException {
		String methodNm = "이송차량 실적처리 팝업 - 등록 [BSlabSeEJB.trtMvCarStatSet2] < " + gdReq.getNavigateValue();
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
			
			if("TSYDJ003".equals(sJMS_TC_CD)) { //소재차량도착
				
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
	 * 이송작업재료등록
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updCarFtMvMtl(GridData gdReq) throws DAOException {
		String methodNm = "이송작업재료등록[BSlabJspFaEJB.updCarFtMvMtl] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			String sSlabNo;
			String sStockMv;
			JDTORecordSet rsResult;

			//등록 할  레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {

				sSlabNo = commUtils.getValue(gdReq, "STL_NO", ii);
				
				//코일공통의 차공정코드로 이동조건 설정, 리턴 값이 ""이면 코일공통에 존재하지 않는 코일 번호로 Error 처리 한다.
				sStockMv	= bSlabComm.getStockMoveTerm(sSlabNo);
				
				if("".equals(sStockMv)) {
					throw new Exception("SLAB공통에 존재하지 않는 SLAB_NO : " + sSlabNo);
				}
				
				//TB_YM_STOCK 에 존재 하는지 확인
				jrParam.setField("STOCK_ID"	, sSlabNo);
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getStockInfoWcrGp", logId, methodNm, "TB_YM_STOCK에 존재하는지 체크");
				
				if(rsResult.size()<=0) {
					
					//TB_YM_STOCK 에 STOCK_ID가 존재하지 않을 경우..
					
					/**********************************************************
					* 1-1-2-1. TB_YM_STOCK에 STOCK_ID를 신규생성
					**********************************************************/
					jrParam.setField("STOCK_ID"			, sSlabNo );
					jrParam.setField("STOCK_MOVE_TERM"	, sStockMv  ); //저장품 이동 조건 
					
					commDao.insert(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.insStockInfo", logId, methodNm, "TB_YM_STOCK에 STOCK_ID를 신규생성");
					
					/**********************************************************
					* 1-1-2-2. 저장품제원정보(YMA8L002)  L2 송신
					**********************************************************/
					jrParam.setField("STOCK_ID"			, sSlabNo ); //재료번호(SLAB번호)
					jrParam.setField("MSG_GP"			, "I"		 ); //정보구분(I:신규)
					jrParam.setField("YD_INFO_SYNC_CD"	, "A"		 ); //야드정보동기화코드(A:생산실적)
		
					jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA8L002", jrParam));
					
				} else {
					/**********************************************************
					* 1-1-2-3. TB_YM_STOCK의 STL_NO에 저장품 이동 조건 갱신
					**********************************************************/
					jrParam.setField("STOCK_ID"			, sSlabNo );
					jrParam.setField("STOCK_MOVE_TERM"	, sStockMv  ); //저장품 이동 조건 
					
					commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockMoveTerm", logId, methodNm, "TB_YM_STOCK의 STOCK_ID에 저장품 이동 조건 갱신");
				}
				
				//이송작업재료삭제
				jrParam.setField("YD_CAR_SCH_ID"	, commUtils.trim(gdReq.getParam("YD_CAR_SCH_ID"))); 
				jrParam.setField("STL_NO"			, commUtils.getValue(gdReq, "OLD_SSTL_NO", ii)); 
 				/* 이송작업재료삭제 -- com.inisteel.cim.ym.bcoil.dao.BCoilDAO.delCarFtMvMtl 

				DELETE USRYDA.TB_YD_CARFTMVMTL
				WHERE  YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
				AND    STL_NO = :V_STL_NO   
				*/
				commDao.insert(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.delCarFtMvMtl", logId, methodNm, "이송작업재료삭제");
				
				//이송작업재료등록
				jrParam.setField("STL_NO"			, sSlabNo); 
				jrParam.setField("YD_CAR_SCH_ID"	, commUtils.trim(gdReq.getParam("YD_CAR_SCH_ID"))); 
				jrParam.setField("STACK_BED_GP"	    , commUtils.getValue(gdReq, "STACK_BED_GP", ii)); 
				jrParam.setField("STACK_LAYER_GP"	, commUtils.getValue(gdReq, "STACK_LAYER_GP",ii)); 
				jrParam.setField("MODIFIER"	        , commUtils.trim(gdReq.getParam("userid"))); 
				//jrParam.setField("YS_STK_SEQ_NO"	, commUtils.getValue(gdReq, "YS_STK_SEQ_NO", ii)); 
				/* 이송작업재료등록 -- com.inisteel.cim.ym.bslab.dao.BSlabDAO.updCarFtMvMtl 

				MERGE INTO TB_YD_CARFTMVMTL TM USING (
				    SELECT STK.STOCK_ID
				          ,SLAB.HCR_GP
				          ,SLAB.RECORD_PROG_STAT AS STL_PROG_CD
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
				                AND STACK_LAYER_STAT IN ('C','U') ) LAY --권상대기 적치중
				          ,VW_YD_SLABCOMM   SLAB    
				    WHERE  STK.STOCK_ID = :V_STL_NO
				      AND  STK.STOCK_ID = SLAB.SLAB_NO 
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
				   ,TM.STL_PROG_CD = DD.STL_PROG_CD	 */
				
				commDao.insert(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updCarFtMvMtl", logId, methodNm, "이송작업재료등록");
			}
						
			//차량 작업 상태,매수,작업완료시간 update
			jrParam.setField("YD_CAR_SCH_ID"	, commUtils.trim(gdReq.getParam("YD_CAR_SCH_ID"))); 
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCarSchWrkStSlab", logId, methodNm, "이송차량스케줄 차량작업상태 수정");
			
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
		String methodNm = "이송작업재료삭제[BSlabJspFaEJB.delCarFtMvMtl] < " + gdReq.getNavigateValue();
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
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCarSchWrkStSlab", logId, methodNm, "이송차량스케줄 차량작업상태 수정");
			
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
		String methodNm = "이송작업재료위치변경[BSlabJspFaEJB.chgCarFtMvMtl] < " + gdReq.getNavigateValue();
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
				//jrParam.setField("YS_STK_SEQ_NO"	, commUtils.getValue(gdReq, "YS_STK_SEQ_NO", ii)); 
				jrParam.setField("MODIFIER"	        , commUtils.trim(gdReq.getParam("userid"))); 
				//jrParam.setField("YS_STK_SEQ_NO"	, commUtils.getValue(gdReq, "YS_STK_SEQ_NO", ii)); 
				/* 이송작업재료등록 -- com.inisteel.cim.ym.bslab.dao.BSlabDAO.updCarFtMvMtl 

				MERGE INTO TB_YD_CARFTMVMTL TM USING (
				    SELECT STK.STOCK_ID
				          ,SLAB.HCR_GP
				          ,SLAB.RECORD_PROG_STAT AS STL_PROG_CD
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
				                AND STACK_LAYER_STAT IN ('C','U') ) LAY --권상대기 적치중
				          ,VW_YD_SLABCOMM   SLAB    
				    WHERE  STK.STOCK_ID = :V_STL_NO
				      AND  STK.STOCK_ID = SLAB.SLAB_NO 
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
				   ,TM.STL_PROG_CD = DD.STL_PROG_CD	 */
				
				commDao.insert(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updCarFtMvMtl", logId, methodNm, "이송작업재료등록");
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
	 * 하차백업생성
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord mkUdCarSch(GridData gdReq) throws DAOException {
		String methodNm = "하차백업생성[BSlabJspFaEJB.mkUdCarSch] < " + gdReq.getNavigateValue();
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
			
			commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insCarSchLd", logId, methodNm, "차량스케쥴 상차완료(5)로 INSERT ");
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of mkUdCarSch		
	
	/**
	 * Pallet조회 (C) 목적동변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updDestBay(GridData gdReq) throws DAOException {
		String methodNm = "Pallet조회 (C) 목적동변경[BSlabJspSeEJB.updDestBay] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			jrParam.setField("YD_STK_COL_GP"	, gdReq.getParam("YD_STK_COL_GP"));
			jrParam.setField("YD_STKBED_USG_CD"	, gdReq.getParam("YD_STKBED_USG_CD")); 
			
			commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updateListTargetDong", logId, methodNm, "Pallet조회 (C) 목적동변경");
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updDestBay
	
	/**
	 * 야드기준 변경1
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updYmRuleDtlItm1(GridData gdReq) throws DAOException {
		String methodNm = "야드기준 변경1[BSlabJspSeEJB.updYmRuleDtlItm1] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			jrParam.setField("REPR_CD_GP"	, gdReq.getParam("REPR_CD_GP") );
			jrParam.setField("CD_GP"		, gdReq.getParam("CD_GP") );
			jrParam.setField("ITEM"			, gdReq.getParam("ITEM"));
			jrParam.setField("DTL_ITM1"		, gdReq.getParam("DTL_ITM1"));

			commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updYmRuleNvl", logId, methodNm, "TB_YM_RULE 수정");
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updYmRuleDtlItm1
	
	/**
	 * 스케줄 기준 야드멀티작업여부 설정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updSchRuleMultiYn(GridData gdReq) throws DAOException {
		String methodNm = "야드기준 변경1[BSlabJspSeEJB.updSchRuleMultiYn] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			jrParam.setField("YD_MULTI_WRK_YN"	, gdReq.getParam("YD_MULTI_WRK_YN") );
			jrParam.setField("YD_WRK_CRN"		, gdReq.getParam("YD_WRK_CRN") );
			jrParam.setField("YD_ALT_CRN"		, gdReq.getParam("YD_ALT_CRN"));
			jrParam.setField("YD_SCH_CD"		, gdReq.getParam("YD_SCH_CD"));
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updSchRuleMultiYn
			UPDATE TB_YM_SCHEDULERULE
			   SET MODIFIER = :V_MODIFIER
			      ,MOD_DDTT = SYSDATE
			      ,YD_MULTI_WRK_YN = :V_YD_MULTI_WRK_YN
			      ,YD_WRK_CRN = :V_YD_WRK_CRN
			      ,YD_ALT_CRN = :V_YD_ALT_CRN
			 WHERE YD_SCH_CD = :V_YD_SCH_CD */
			commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updSchRuleMultiYn", logId, methodNm, "스케줄 기준 야드멀티작업여부 설정");
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updSchRuleMultiYn			
	
	/**
	 *      [A] 오퍼레이션명 : Pallet조회 (B) - 스케줄기동2 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord procCrnSchStart2(GridData gdReq) throws DAOException {
		String methodNm = "Pallet조회 (B) - 스케줄기동2 [BSlabJspSeEJB.procCrnSchStart2] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			//JDTORecord jrRtn = null;
			String currDate   = commUtils.getDateTime14();	//현재시각
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			JDTORecord jrCrnSchMsg = JDTORecordFactory.getInstance().create();
			
			JDTORecordSet rsResult  = null;
			JDTORecordSet rsResult2 = null;
			String sSchCode = "";
			String s_STACK_YD_GP = "";
			String s_STACK_BAY_GP = "";
			String sYD_SCH_PRIOR = "";
			String sYD_MULTI_WRK_YN = "";
			String sYD_WRK_CRN = "";
			String sYD_WBOOK_ID = "";
			String sStkCol = "";
			String szTRN_EQP_CD = "";
			String sCHANGE_CRN = "";
			String sYD_SCH_ST_GP = "";
			String sYD_WRK_PLAN_CRN = "";
			String sYD_WRK_PLAN_CRN2 = "";
			String sYD_TO_LOC_GUIDE = "";
			String sSTOCK_ID = "";
			
			String sCHARGE_LOT_NO_DIV_YN = commUtils.nvl(gdReq.getParam("CHARGE_LOT_NO_DIV_YN"),"Y");
			String sYD_CAR_SCH_ID = commUtils.trim(gdReq.getParam("YD_CAR_SCH_ID"));
			String sDIST_SHIPASSIGN_GP   = commUtils.nvl(gdReq.getParam("DIST_SHIPASSIGN_GP"),""); //상단1매 스케줄 기동 인지 판단하기 위힌 파라메터
			
	    	int iWbook_ID_Cnt = 0;
	    	int iWbook_NOID_Cnt = 0;
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			if(rowCnt > 0) {
			
				sStkCol = commUtils.trim(gdReq.getHeader("STACK_COL_GP").getValue(0));
				s_STACK_YD_GP	= sStkCol.substring(0,1);
				s_STACK_BAY_GP	= sStkCol.substring(1,2);
				szTRN_EQP_CD	= commUtils.trim(gdReq.getHeader("TRN_EQP_CD").getValue(0));
				
				commUtils.printLog(logId, "=======:::: STACK_COL_GP : " +  sStkCol , "SL");
				commUtils.printLog(logId, "=======:::: TRN_EQP_CD : " +  szTRN_EQP_CD , "SL");
				commUtils.printLog(logId, "=======:::: YD_CAR_SCH_ID : " +  sYD_CAR_SCH_ID , "SL");
				commUtils.printLog(logId, "=======:::: CHARGE_LOT_NO_DIV_YN : " +  sCHARGE_LOT_NO_DIV_YN , "SL");
				commUtils.printLog(logId, "=======:::: DIST_SHIPASSIGN_GP : " +  sDIST_SHIPASSIGN_GP , "SL");
				
				jrCrnSchMsg.setField("SCH_CNT" , "0");
				
				//스케줄코드 생성  - 이송하차(L)
				sSchCode = s_STACK_YD_GP + s_STACK_BAY_GP + "PT02LM";
				
				//스케줄코드로 스케줄기준Table조회
				jrParam.setField("YD_SCH_CD", sSchCode);
		    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule  
				SELECT YD_SCH_CD
				     , YD_WRK_CRN
				     , YD_WRK_CRN_PRIOR
				     , YD_MULTI_WRK_YN
				  FROM TB_YM_SCHEDULERULE
				 WHERE DEL_YN = 'N'
				   AND YD_SCH_CD = :V_YD_SCH_CD
				*/   
				rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
		    	
				if (rsResult2 != null && rsResult2.size() > 0) {
					sYD_WRK_CRN = rsResult2.getRecord(0).getFieldString("YD_WRK_CRN"); //야드작업크레인
					sYD_SCH_PRIOR = rsResult2.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
					sYD_MULTI_WRK_YN = rsResult2.getRecord(0).getFieldString("YD_MULTI_WRK_YN"); //야드멀티작업여부
				} else {
					throw new Exception("B열연 코일 스케쥴 코드 이상 : [" + sSchCode + "]");
				}			
				
				if("Y".equals(sYD_MULTI_WRK_YN)) {
					//jrParam.setField("YD_SCH_ST_GP"		, "N"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업, N:멀티작업)
					sYD_SCH_ST_GP = "N"; //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업, N:멀티작업)
					if("A".equals(s_STACK_BAY_GP)) {
						if("2ACRA1".equals(sYD_WRK_CRN)) {
							sYD_WRK_PLAN_CRN = "2ACRA1";
							sYD_WRK_PLAN_CRN2 = "2ACRA2";
						} else {
							sYD_WRK_PLAN_CRN = "2ACRA2";
							sYD_WRK_PLAN_CRN2 = "2ACRA1";
						}
					} else if("D".equals(s_STACK_BAY_GP)) {
						if("2DCRD3".equals(sYD_WRK_CRN)) {
							sYD_WRK_PLAN_CRN = "2DCRD3";
							sYD_WRK_PLAN_CRN2 = "2DCRD2";
						} else {
							sYD_WRK_PLAN_CRN = "2DCRD2";
							sYD_WRK_PLAN_CRN2 = "2DCRD3";
						}
					} else {
						sYD_WRK_PLAN_CRN = sYD_WRK_CRN;
						sYD_WRK_PLAN_CRN2 = "";
					}
					
				} else {
					sYD_SCH_ST_GP = "A"; //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업, N:멀티작업)
					sYD_WRK_PLAN_CRN = sYD_WRK_CRN;
					sYD_WRK_PLAN_CRN2 = "";
				}
				
				for(int ii = 0; ii < rowCnt; ii++) {
					sYD_WBOOK_ID = commUtils.getValue(gdReq, "YD_WBOOK_ID", ii);
					if(!"".equals(sYD_WBOOK_ID)) {
						iWbook_ID_Cnt++;
					} else {
						iWbook_NOID_Cnt++;
					}
				}
				
				if(iWbook_NOID_Cnt == rowCnt) {
					//작업예약이 없는 경우.. 작업예약 생성 후 스케줄 호출
					
						
					//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
					//------------------------------------------------------------------------------------------------------------------------
					//작업예약 슬라브 단위로 생성
					
					iWbook_ID_Cnt = 0;
					
					for(int ii = 0; ii < rowCnt; ii++) {
						
						sYD_TO_LOC_GUIDE = commUtils.getValue(gdReq, "MTL_YD_TO_LOC_GUIDE", ii);
						sCHANGE_CRN = commUtils.getValue(gdReq, "CHANGE_CRN", ii);
						sYD_WBOOK_ID = commUtils.getValue(gdReq, "YD_WBOOK_ID", ii);

						if("".equals(sYD_WBOOK_ID)) {
							
							//작업예약ID생성
							sYD_WBOOK_ID = commDao.getSeqId(logId, methodNm, "WrkBook");
							iWbook_ID_Cnt++;
							
							jrCrnSchMsg.setField("YD_WBOOK_ID"+(iWbook_ID_Cnt),sYD_WBOOK_ID);
							jrCrnSchMsg.setField("SCH_CNT" , Integer.toString(iWbook_ID_Cnt));
							
							/**********************************************************
							* 1-1. 작업예약(TB_YM_WRKBOOK) 생성
							**********************************************************/
							jrParam.setField("YD_WBOOK_ID"			, sYD_WBOOK_ID);
							jrParam.setField("YD_GP"				, s_STACK_YD_GP);
							jrParam.setField("YD_BAY_GP"			, s_STACK_BAY_GP);
							jrParam.setField("YD_SCH_CD"			, sSchCode); //야드스케쥴코드
							jrParam.setField("YD_SCH_PRIOR"			, sYD_SCH_PRIOR); //야드스케쥴우선순위
							jrParam.setField("YD_SCH_PROG_STAT"		, "W"); //야드스케쥴진행상태(W:스케줄수행대기)
							jrParam.setField("YD_SCH_REQ_GP"		, "C"); //야드스케쥴요청구분 C:영차차량도착, F:공차차량도착
							jrParam.setField("TRN_EQP_CD"			, szTRN_EQP_CD); //운송장비코드
							jrParam.setField("YD_CAR_USE_GP"		, "L"); //야드차량사용구분 L:구내운송
							jrParam.setField("CHARGE_LOT_NO_DIV_YN"	, sCHARGE_LOT_NO_DIV_YN); //장입순번분리여부
							jrParam.setField("YD_TO_LOC_GUIDE"		, sYD_TO_LOC_GUIDE);
							
							if(!"".equals(sCHANGE_CRN)) {
								jrParam.setField("YD_SCH_ST_GP"			, "A"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업, N:멀티작업)
								jrParam.setField("YD_WRK_PLAN_CRN"		, sCHANGE_CRN); //야드작업계획크레인
								jrParam.setField("YD_WRK_PLAN_CRN2"		, ""); //야드작업계획크레인2
							} else {
								jrParam.setField("YD_SCH_ST_GP"			, sYD_SCH_ST_GP); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업, N:멀티작업)
								jrParam.setField("YD_WRK_PLAN_CRN"		, sYD_WRK_PLAN_CRN); //야드작업계획크레인
								jrParam.setField("YD_WRK_PLAN_CRN2"		, sYD_WRK_PLAN_CRN2); //야드작업계획크레인2
							}
							jrParam.setField("DIST_SHIPASSIGN_GP"	, sDIST_SHIPASSIGN_GP); //상단 1매 스케줄 기동
							
							commDao.insert(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.insWrkBook", logId, methodNm, "작업예약(TB_YM_WRKBOOK) 생성");
							//bSlabComm.insWrkBook(jrParam);
							
							/**********************************************************
							* 작업예약재료(TB_YM_WRKBOOKMTL) 생성
							**********************************************************/
							jrParam.setField("YD_WBOOK_ID"		, sYD_WBOOK_ID);
							jrParam.setField("STOCK_ID"			, commUtils.getValue(gdReq, "STOCK_ID", ii));
							jrParam.setField("STACK_COL_GP"		, sStkCol);
							jrParam.setField("STACK_BED_GP"		, "01");
							jrParam.setField("STACK_LAYER_GP"	, commUtils.getValue(gdReq, "STACK_LAYER_GP", ii));
							
							
							commDao.insert(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.insYmWrkBookMtl", logId, methodNm, "작업예약재료(TB_YM_WRKBOOKMTL) 생성");
							//bSlabComm.insWrkBookMtl(jrParam);
						}
						
					}
					//------------------------------------------------------------------------------------------------------------------------
					//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
						
					
					
				} else if(iWbook_ID_Cnt == rowCnt) {
					//작업예약이 이미 존재하는 경우.. 기존작업예약으로 스케줄 호출
					
					sYD_WBOOK_ID = "";
					iWbook_ID_Cnt = 0;
					
					for(int ii = 0; ii < rowCnt; ii++) {
						
						if(!sYD_WBOOK_ID.equals(commUtils.getValue(gdReq, "YD_WBOOK_ID", ii))) {
							
							sYD_WBOOK_ID = commUtils.getValue(gdReq, "YD_WBOOK_ID", ii);
							iWbook_ID_Cnt++;
							
							jrCrnSchMsg.setField("YD_WBOOK_ID"+(iWbook_ID_Cnt),sYD_WBOOK_ID);
							jrCrnSchMsg.setField("SCH_CNT" , Integer.toString(iWbook_ID_Cnt));
							
							sCHANGE_CRN = commUtils.getValue(gdReq, "CHANGE_CRN", ii);
							
							if(!"".equals(sCHANGE_CRN)) {
								if("A".equals(s_STACK_BAY_GP)) {
									if("2ACRA1".equals(sCHANGE_CRN)) {
										sYD_WRK_PLAN_CRN = "2ACRA1";
										sYD_WRK_PLAN_CRN2 = "2ACRA2";
									} else {
										sYD_WRK_PLAN_CRN = "2ACRA2";
										sYD_WRK_PLAN_CRN2 = "2ACRA1";
									}
								} else if("D".equals(s_STACK_BAY_GP)) {
									if("2DCRD3".equals(sCHANGE_CRN)) {
										sYD_WRK_PLAN_CRN = "2DCRD3";
										sYD_WRK_PLAN_CRN2 = "2DCRD2";
									} else {
										sYD_WRK_PLAN_CRN = "2DCRD2";
										sYD_WRK_PLAN_CRN2 = "2DCRD3";
									}
								} else {
									sYD_WRK_PLAN_CRN = sCHANGE_CRN;
									sYD_WRK_PLAN_CRN2 = "";
								}
							}
							
							/**********************************************************
							* 작업예약(TB_YM_WRKBOOK) 수정
							**********************************************************/
							jrParam.setField("YD_BAY_GP"			, s_STACK_BAY_GP); //동구분
							jrParam.setField("YD_SCH_CD"			, sSchCode		); //스케줄코드
							jrParam.setField("YD_SCH_PRIOR"			, sYD_SCH_PRIOR	); //야드스케쥴우선순위
							jrParam.setField("YD_WBOOK_ID"			, sYD_WBOOK_ID	); //작업예약ID
							jrParam.setField("CHARGE_LOT_NO_DIV_YN"	, sCHARGE_LOT_NO_DIV_YN); //장입순번분리여부
							jrParam.setField("YD_TO_LOC_GUIDE"		, commUtils.getValue(gdReq, "MTL_YD_TO_LOC_GUIDE", ii));
							if(!"".equals(sCHANGE_CRN)) {
								jrParam.setField("YD_SCH_ST_GP"			, "A"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업, N:멀티작업)
								jrParam.setField("YD_WRK_PLAN_CRN"		, sCHANGE_CRN); //야드작업계획크레인
								jrParam.setField("YD_WRK_PLAN_CRN2"		, ""); //야드작업계획크레인2
							} else {
								jrParam.setField("YD_SCH_ST_GP"			, sYD_SCH_ST_GP); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업, N:멀티작업)
								jrParam.setField("YD_WRK_PLAN_CRN"		, sYD_WRK_PLAN_CRN); //야드작업계획크레인
								jrParam.setField("YD_WRK_PLAN_CRN2"		, sYD_WRK_PLAN_CRN2); //야드작업계획크레인2
							}
							jrParam.setField("DIST_SHIPASSIGN_GP"	, sDIST_SHIPASSIGN_GP); //상단 1매 스케줄 기동
							/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTbYmWrkBook
							--작업예약 크레인 변경,Multi 작업
							UPDATE TB_YM_WRKBOOK
							   SET MODIFIER = :V_MODIFIER
							      ,MOD_DDTT = SYSDATE
							      ,YD_BAY_GP = :V_YD_BAY_GP
							      ,YD_SCH_CD = :V_YD_SCH_CD
							      ,YD_SCH_PRIOR = :V_YD_SCH_PRIOR
							      ,YD_SCH_ST_GP = :V_YD_SCH_ST_GP
							      ,YD_WRK_PLAN_CRN = :V_YD_WRK_PLAN_CRN
							      ,YD_WRK_PLAN_CRN2 = :V_YD_WRK_PLAN_CRN2
							      ,CHARGE_LOT_NO_DIV_YN = :V_CHARGE_LOT_NO_DIV_YN
							      ,YD_TO_LOC_GUIDE = :V_YD_TO_LOC_GUIDE
							      ,DIST_SHIPASSIGN_GP = NVL(:V_DIST_SHIPASSIGN_GP,DIST_SHIPASSIGN_GP)
							   
							 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID    */
							commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTbYmWrkBook", logId, methodNm, "작업예약(TB_YM_WRKBOOK) 변경");				
						}
					}
					
					//크레인 스케줄 기동 YMYMJ203 호출
					jrCrnSchMsg.setField("JMS_TC_CD"			, "YMYMJ203"); 
					jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시	
					jrCrnSchMsg.setField("YD_SCH_CD"  			, ""); //야드스케쥴코드
					jrCrnSchMsg.setField("YD_EQP_ID"  			, ""); //야드설비ID
					
					//jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);
					
				} else {
					throw new Exception("작업예약이 존재하는 재료번호와 작업예약이 존재하지 않는 재료번호를 동시에 스케줄 기동 시킬 수 없습니다!!");
				}
				
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrCrnSchMsg;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of procCrnSchStart2
	
	/**
	 *      [A] 오퍼레이션명 : Pallet조회 (B) - 스케줄기동2 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord procCrnSchStart2_save(GridData gdReq) throws DAOException {
		String methodNm = "Pallet조회 (B) - 스케줄기동2 [BSlabJspSeEJB.procCrnSchStart2] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			//JDTORecord jrRtn = null;
			String currDate   = commUtils.getDateTime14();	//현재시각
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			JDTORecord jrCrnSchMsg = JDTORecordFactory.getInstance().create();
			
			JDTORecordSet rsResult  = null;
			JDTORecordSet rsResult2 = null;
			String sSchCode = "";
			String s_STACK_YD_GP = "";
			String s_STACK_BAY_GP = "";
			String sYD_SCH_PRIOR = "";
			String sYD_MULTI_WRK_YN = "";
			String sYD_WRK_CRN = "";
			String sYD_WBOOK_ID = "";
			String sStkCol = "";
			String szTRN_EQP_CD = "";
			String sCHANGE_CRN = "";
			String sYD_SCH_ST_GP = "";
			String sYD_WRK_PLAN_CRN = "";
			String sYD_WRK_PLAN_CRN2 = "";
			String sYD_TO_LOC_GUIDE = "";
			String sSTOCK_ID = "";
			
			String sCHARGE_LOT_NO_DIV_YN = commUtils.nvl(gdReq.getParam("CHARGE_LOT_NO_DIV_YN"),"Y");
			String sYD_CAR_SCH_ID = commUtils.trim(gdReq.getParam("YD_CAR_SCH_ID"));
			
	    	int iWbook_ID_Cnt = 0;
	    	int iWbook_NOID_Cnt = 0;
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			if(rowCnt > 0) {
			
				sStkCol = commUtils.trim(gdReq.getHeader("STACK_COL_GP").getValue(0));
				s_STACK_YD_GP	= sStkCol.substring(0,1);
				s_STACK_BAY_GP	= sStkCol.substring(1,2);
				szTRN_EQP_CD	= commUtils.trim(gdReq.getHeader("TRN_EQP_CD").getValue(0));
				
				commUtils.printLog(logId, "=======:::: STACK_COL_GP : " +  sStkCol , "SL");
				commUtils.printLog(logId, "=======:::: TRN_EQP_CD : " +  szTRN_EQP_CD , "SL");
				commUtils.printLog(logId, "=======:::: YD_CAR_SCH_ID : " +  sYD_CAR_SCH_ID , "SL");
				commUtils.printLog(logId, "=======:::: CHARGE_LOT_NO_DIV_YN : " +  sCHARGE_LOT_NO_DIV_YN , "SL");
				
				jrCrnSchMsg.setField("SCH_CNT" , "0");
				
				//스케줄코드 생성  - 이송하차(L)
				sSchCode = s_STACK_YD_GP + s_STACK_BAY_GP + "PT02LM";
				
				//스케줄코드로 스케줄기준Table조회
				jrParam.setField("YD_SCH_CD", sSchCode);
		    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule  
				SELECT YD_SCH_CD
				     , YD_WRK_CRN
				     , YD_WRK_CRN_PRIOR
				     , YD_MULTI_WRK_YN
				  FROM TB_YM_SCHEDULERULE
				 WHERE DEL_YN = 'N'
				   AND YD_SCH_CD = :V_YD_SCH_CD
				*/   
				rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
		    	
				if (rsResult2 != null && rsResult2.size() > 0) {
					sYD_WRK_CRN = rsResult2.getRecord(0).getFieldString("YD_WRK_CRN"); //야드작업크레인
					sYD_SCH_PRIOR = rsResult2.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
					sYD_MULTI_WRK_YN = rsResult2.getRecord(0).getFieldString("YD_MULTI_WRK_YN"); //야드멀티작업여부
				} else {
					throw new Exception("B열연 코일 스케쥴 코드 이상 : [" + sSchCode + "]");
				}			
				
				if("Y".equals(sYD_MULTI_WRK_YN)) {
					//jrParam.setField("YD_SCH_ST_GP"		, "N"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업, N:멀티작업)
					sYD_SCH_ST_GP = "N"; //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업, N:멀티작업)
					if("A".equals(s_STACK_BAY_GP)) {
						if("2ACRA1".equals(sYD_WRK_CRN)) {
							sYD_WRK_PLAN_CRN = "2ACRA1";
							sYD_WRK_PLAN_CRN2 = "2ACRA2";
						} else {
							sYD_WRK_PLAN_CRN = "2ACRA2";
							sYD_WRK_PLAN_CRN2 = "2ACRA1";
						}
					} else if("D".equals(s_STACK_BAY_GP)) {
						if("2DCRD3".equals(sYD_WRK_CRN)) {
							sYD_WRK_PLAN_CRN = "2DCRD3";
							sYD_WRK_PLAN_CRN2 = "2DCRD2";
						} else {
							sYD_WRK_PLAN_CRN = "2DCRD2";
							sYD_WRK_PLAN_CRN2 = "2DCRD3";
						}
					} else {
						sYD_WRK_PLAN_CRN = sYD_WRK_CRN;
						sYD_WRK_PLAN_CRN2 = "";
					}
					
				} else {
					sYD_SCH_ST_GP = "A"; //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업, N:멀티작업)
					sYD_WRK_PLAN_CRN = sYD_WRK_CRN;
					sYD_WRK_PLAN_CRN2 = "";
				}
				
				for(int ii = 0; ii < rowCnt; ii++) {
					sYD_WBOOK_ID = commUtils.getValue(gdReq, "YD_WBOOK_ID", ii);
					if(!"".equals(sYD_WBOOK_ID)) {
						iWbook_ID_Cnt++;
					} else {
						iWbook_NOID_Cnt++;
					}
				}
				
				if(iWbook_NOID_Cnt == rowCnt) {
					
					/**********************************************************
					* 크레인 스케줄 GROUPING 호출
					**********************************************************/
					JDTORecord jrParamSet			= JDTORecordFactory.getInstance().create();
					
					jrParamSet.setField("YD_CAR_SCH_ID"	, sYD_CAR_SCH_ID	);
					jrParamSet.setField("YD_SCH_CD"		, sSchCode			);
					jrParamSet.setField("YD_EQP_ID"		, sYD_WRK_CRN		);
					jrParamSet.setField("STACK_COL_GP"	, sStkCol			);
					
					//Slab Schedule EJB Call
					EJBConnector ejbConn = new EJBConnector("default","BSlabSchSeEJB",this);
					JDTORecordSet rsHandlingCntSpec = (JDTORecordSet)ejbConn.trx("CrnSchGrpPT",new Class[]{String.class, String.class, JDTORecord.class},new Object[]{ logId, methodNm, jrParamSet });			
					
					if(rsHandlingCntSpec.size() > 0) {
						//신규 모듈 - 스케줄작업단위로 작업예약 생성
						
						String sHANDLING_CNT_SPEC_OLD = "0";
						String sHANDLING_CNT_SPEC;
						
						
						iWbook_ID_Cnt = 0;
						
						for(int jj = 0; jj < rsHandlingCntSpec.size(); jj++) {
							
							sHANDLING_CNT_SPEC = rsHandlingCntSpec.getRecord(jj).getFieldString("HANDLING_CNT_SPEC");
							
							if(!sHANDLING_CNT_SPEC_OLD.equals(sHANDLING_CNT_SPEC)) {
								
								sSTOCK_ID = commUtils.trim(rsHandlingCntSpec.getRecord(jj).getFieldString("STOCK_ID"));
								
								sYD_TO_LOC_GUIDE = "";
								sCHANGE_CRN = "";
								
								for(int ii = 0; ii < rowCnt; ii++) {
									if(sSTOCK_ID.equals(commUtils.getValue(gdReq, "STOCK_ID", ii))) {
										sYD_TO_LOC_GUIDE = commUtils.getValue(gdReq, "MTL_YD_TO_LOC_GUIDE", ii);
										sCHANGE_CRN = commUtils.getValue(gdReq, "CHANGE_CRN", ii);
										sYD_WBOOK_ID = commUtils.getValue(gdReq, "YD_WBOOK_ID", ii);
										break;
									}
								}
								
								if("".equals(sYD_WBOOK_ID)) {
									
									//작업예약ID생성
									sYD_WBOOK_ID = commDao.getSeqId(logId, methodNm, "WrkBook");
									iWbook_ID_Cnt++;
									
									jrCrnSchMsg.setField("YD_WBOOK_ID"+(iWbook_ID_Cnt),sYD_WBOOK_ID);
									jrCrnSchMsg.setField("SCH_CNT" , Integer.toString(iWbook_ID_Cnt));
									
									sHANDLING_CNT_SPEC_OLD = sHANDLING_CNT_SPEC;
									
									/**********************************************************
									* 1-1. 작업예약(TB_YM_WRKBOOK) 생성
									**********************************************************/
									jrParam.setField("YD_WBOOK_ID"			, sYD_WBOOK_ID);
									jrParam.setField("YD_GP"				, s_STACK_YD_GP);
									jrParam.setField("YD_BAY_GP"			, s_STACK_BAY_GP);
									jrParam.setField("YD_SCH_CD"			, sSchCode); //야드스케쥴코드
									jrParam.setField("YD_SCH_PRIOR"			, sYD_SCH_PRIOR); //야드스케쥴우선순위
									jrParam.setField("YD_SCH_PROG_STAT"		, "W"); //야드스케쥴진행상태(W:스케줄수행대기)
									jrParam.setField("YD_SCH_REQ_GP"		, "C"); //야드스케쥴요청구분 C:영차차량도착, F:공차차량도착
									jrParam.setField("TRN_EQP_CD"			, szTRN_EQP_CD); //운송장비코드
									jrParam.setField("YD_CAR_USE_GP"		, "L"); //야드차량사용구분 L:구내운송
									jrParam.setField("CHARGE_LOT_NO_DIV_YN"	, sCHARGE_LOT_NO_DIV_YN); //장입순번분리여부
									jrParam.setField("YD_TO_LOC_GUIDE"		, sYD_TO_LOC_GUIDE);
									
									if(!"".equals(sCHANGE_CRN)) {
										jrParam.setField("YD_SCH_ST_GP"			, "A"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업, N:멀티작업)
										jrParam.setField("YD_WRK_PLAN_CRN"		, sCHANGE_CRN); //야드작업계획크레인
										jrParam.setField("YD_WRK_PLAN_CRN2"		, ""); //야드작업계획크레인2
									} else {
										jrParam.setField("YD_SCH_ST_GP"			, sYD_SCH_ST_GP); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업, N:멀티작업)
										jrParam.setField("YD_WRK_PLAN_CRN"		, sYD_WRK_PLAN_CRN); //야드작업계획크레인
										jrParam.setField("YD_WRK_PLAN_CRN2"		, sYD_WRK_PLAN_CRN2); //야드작업계획크레인2
									}
									
									commDao.insert(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.insWrkBook", logId, methodNm, "작업예약(TB_YM_WRKBOOK) 생성");
									//bSlabComm.insWrkBook(jrParam);
									
									/**********************************************************
									* 작업예약재료(TB_YM_WRKBOOKMTL) 생성
									**********************************************************/
									jrParam.setField("YD_WBOOK_ID"		, sYD_WBOOK_ID);
									jrParam.setField("STOCK_ID"			, commUtils.trim(rsHandlingCntSpec.getRecord(jj).getFieldString("STOCK_ID")));
									jrParam.setField("STACK_COL_GP"		, sStkCol);
									jrParam.setField("STACK_BED_GP"		, "01");
									jrParam.setField("STACK_LAYER_GP"	, commUtils.trim(rsHandlingCntSpec.getRecord(jj).getFieldString("STACK_LAYER_GP")));
									
									commDao.insert(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.insYmWrkBookMtl", logId, methodNm, "작업예약재료(TB_YM_WRKBOOKMTL) 생성");
									//bSlabComm.insWrkBookMtl(jrParam);
								}
								
							} else {
								
								/**********************************************************
								* 작업예약재료(TB_YM_WRKBOOKMTL) 생성
								**********************************************************/
								jrParam.setField("YD_WBOOK_ID"		, sYD_WBOOK_ID);
								jrParam.setField("STOCK_ID"			, commUtils.trim(rsHandlingCntSpec.getRecord(jj).getFieldString("STOCK_ID")));
								jrParam.setField("STACK_COL_GP"		, sStkCol);
								jrParam.setField("STACK_BED_GP"		, "01");
								jrParam.setField("STACK_LAYER_GP"	, commUtils.trim(rsHandlingCntSpec.getRecord(jj).getFieldString("STACK_LAYER_GP")));
								
								commDao.insert(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.insYmWrkBookMtl", logId, methodNm, "작업예약재료(TB_YM_WRKBOOKMTL) 생성");
								//bSlabComm.insWrkBookMtl(jrParam);
							}
						}						
						
						//크레인 스케줄 기동 YMYMJ203 호출
						jrCrnSchMsg.setField("JMS_TC_CD"			, "YMYMJ203"); 
						jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시	
						jrCrnSchMsg.setField("YD_SCH_CD"  			, ""); //야드스케쥴코드
						jrCrnSchMsg.setField("YD_EQP_ID"  			, ""); //야드설비ID
						
						//jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);
						
					} else {
						//기존 모듈 - 한개의 작업예약 생성
						
						sYD_TO_LOC_GUIDE = commUtils.getValue(gdReq, "MTL_YD_TO_LOC_GUIDE", 0);
						sCHANGE_CRN = commUtils.getValue(gdReq, "CHANGE_CRN", 0);
						
						//작업예약ID생성
						sYD_WBOOK_ID = commDao.getSeqId(logId, methodNm, "WrkBook");
						
						/**********************************************************
						* 1-1. 작업예약(TB_YM_WRKBOOK) 생성
						**********************************************************/
						jrParam.setField("YD_WBOOK_ID"			, sYD_WBOOK_ID);
						jrParam.setField("YD_GP"				, s_STACK_YD_GP);
						jrParam.setField("YD_BAY_GP"			, s_STACK_BAY_GP);
						jrParam.setField("YD_SCH_CD"			, sSchCode); //야드스케쥴코드
						jrParam.setField("YD_SCH_PRIOR"			, sYD_SCH_PRIOR); //야드스케쥴우선순위
						jrParam.setField("YD_SCH_PROG_STAT"		, "W"); //야드스케쥴진행상태(W:스케줄수행대기)
						jrParam.setField("YD_SCH_REQ_GP"		, "C"); //야드스케쥴요청구분 C:영차차량도착, F:공차차량도착
						jrParam.setField("TRN_EQP_CD"			, szTRN_EQP_CD); //운송장비코드
						jrParam.setField("YD_CAR_USE_GP"		, "L"); //야드차량사용구분 L:구내운송
						jrParam.setField("CHARGE_LOT_NO_DIV_YN"	, sCHARGE_LOT_NO_DIV_YN); //장입순번분리여부
						jrParam.setField("YD_TO_LOC_GUIDE"		, sYD_TO_LOC_GUIDE);
						
						if(!"".equals(sCHANGE_CRN)) {
							jrParam.setField("YD_SCH_ST_GP"			, "A"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업, N:멀티작업)
							jrParam.setField("YD_WRK_PLAN_CRN"		, sCHANGE_CRN); //야드작업계획크레인
							jrParam.setField("YD_WRK_PLAN_CRN2"		, ""); //야드작업계획크레인2
						} else {
							jrParam.setField("YD_SCH_ST_GP"			, sYD_SCH_ST_GP); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업, N:멀티작업)
							jrParam.setField("YD_WRK_PLAN_CRN"		, sYD_WRK_PLAN_CRN); //야드작업계획크레인
							jrParam.setField("YD_WRK_PLAN_CRN2"		, sYD_WRK_PLAN_CRN2); //야드작업계획크레인2
						}
						
						commDao.insert(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.insWrkBook", logId, methodNm, "작업예약(TB_YM_WRKBOOK) 생성");
						//bSlabComm.insWrkBook(jrParam);
						
						//하차대상 갯수 만큼 Looping...
						for(int ii = 0; ii < rowCnt; ii++) {
							
							/**********************************************************
							* 작업예약재료(TB_YM_WRKBOOKMTL) 생성
							**********************************************************/
							jrParam.setField("YD_WBOOK_ID"			, sYD_WBOOK_ID);
							jrParam.setField("STOCK_ID"				, commUtils.getValue(gdReq, "STOCK_ID", ii));
							jrParam.setField("STACK_COL_GP"			, sStkCol);
							jrParam.setField("STACK_BED_GP"			, "01");
							jrParam.setField("STACK_LAYER_GP"		, commUtils.getValue(gdReq, "STACK_LAYER_GP", ii));
							//jrParam.setField("MTL_YD_TO_LOC_GUIDE"	, commUtils.getValue(gdReq, "MTL_YD_TO_LOC_GUIDE", ii));
							
							commDao.insert(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.insYmWrkBookMtl", logId, methodNm, "작업예약재료(TB_YM_WRKBOOKMTL) 생성");
							//bSlabComm.insWrkBookMtl(jrParam);
						}
						
						//크레인 스케줄 기동 YMYMJ203 호출
						jrCrnSchMsg.setField("JMS_TC_CD"			, "YMYMJ203"); 
						jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시	
						jrCrnSchMsg.setField("YD_WBOOK_ID1"  		, sYD_WBOOK_ID); //작업예약ID
						jrCrnSchMsg.setField("SCH_CNT"  			, "1"); 
						jrCrnSchMsg.setField("YD_SCH_CD"  			, ""); //야드스케쥴코드
						jrCrnSchMsg.setField("YD_EQP_ID"  			, ""); //야드설비ID
						
						//jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);
						
					}
					
				} else if(iWbook_ID_Cnt == rowCnt) {
					
					sYD_WBOOK_ID = "";
					iWbook_ID_Cnt = 0;
					
					for(int ii = 0; ii < rowCnt; ii++) {
						
						if(!sYD_WBOOK_ID.equals(commUtils.getValue(gdReq, "YD_WBOOK_ID", ii))) {
							
							sYD_WBOOK_ID = commUtils.getValue(gdReq, "YD_WBOOK_ID", ii);
							iWbook_ID_Cnt++;
							
							jrCrnSchMsg.setField("YD_WBOOK_ID"+(iWbook_ID_Cnt),sYD_WBOOK_ID);
							jrCrnSchMsg.setField("SCH_CNT" , Integer.toString(iWbook_ID_Cnt));
							
							sCHANGE_CRN = commUtils.getValue(gdReq, "CHANGE_CRN", ii);
							
							if(!"".equals(sCHANGE_CRN)) {
								if("A".equals(s_STACK_BAY_GP)) {
									if("2ACRA1".equals(sCHANGE_CRN)) {
										sYD_WRK_PLAN_CRN = "2ACRA1";
										sYD_WRK_PLAN_CRN2 = "2ACRA2";
									} else {
										sYD_WRK_PLAN_CRN = "2ACRA2";
										sYD_WRK_PLAN_CRN2 = "2ACRA1";
									}
								} else if("D".equals(s_STACK_BAY_GP)) {
									if("2DCRD3".equals(sCHANGE_CRN)) {
										sYD_WRK_PLAN_CRN = "2DCRD3";
										sYD_WRK_PLAN_CRN2 = "2DCRD2";
									} else {
										sYD_WRK_PLAN_CRN = "2DCRD2";
										sYD_WRK_PLAN_CRN2 = "2DCRD3";
									}
								} else {
									sYD_WRK_PLAN_CRN = sCHANGE_CRN;
									sYD_WRK_PLAN_CRN2 = "";
								}
							}
							
							/**********************************************************
							* 작업예약(TB_YM_WRKBOOK) 수정
							**********************************************************/
							jrParam.setField("YD_BAY_GP"			, s_STACK_BAY_GP); //동구분
							jrParam.setField("YD_SCH_CD"			, sSchCode		); //스케줄코드
							jrParam.setField("YD_SCH_PRIOR"			, sYD_SCH_PRIOR	); //야드스케쥴우선순위
							jrParam.setField("YD_WBOOK_ID"			, sYD_WBOOK_ID	); //작업예약ID
							jrParam.setField("CHARGE_LOT_NO_DIV_YN"	, sCHARGE_LOT_NO_DIV_YN); //장입순번분리여부
							jrParam.setField("YD_TO_LOC_GUIDE"		, commUtils.getValue(gdReq, "MTL_YD_TO_LOC_GUIDE", ii));
							if(!"".equals(sCHANGE_CRN)) {
								jrParam.setField("YD_SCH_ST_GP"			, "A"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업, N:멀티작업)
								jrParam.setField("YD_WRK_PLAN_CRN"		, sCHANGE_CRN); //야드작업계획크레인
								jrParam.setField("YD_WRK_PLAN_CRN2"		, ""); //야드작업계획크레인2
							} else {
								jrParam.setField("YD_SCH_ST_GP"			, sYD_SCH_ST_GP); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업, N:멀티작업)
								jrParam.setField("YD_WRK_PLAN_CRN"		, sYD_WRK_PLAN_CRN); //야드작업계획크레인
								jrParam.setField("YD_WRK_PLAN_CRN2"		, sYD_WRK_PLAN_CRN2); //야드작업계획크레인2
							}
							
							/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTbYmWrkBook
							--작업예약 크레인 변경,Multi 작업
							UPDATE TB_YM_WRKBOOK
							   SET MODIFIER = :V_MODIFIER
							      ,MOD_DDTT = SYSDATE
							      ,YD_BAY_GP = :V_YD_BAY_GP
							      ,YD_SCH_CD = :V_YD_SCH_CD
							      ,YD_SCH_PRIOR = :V_YD_SCH_PRIOR
							      ,YD_SCH_ST_GP = :V_YD_SCH_ST_GP
							      ,YD_WRK_PLAN_CRN = :V_YD_WRK_PLAN_CRN
							      ,YD_WRK_PLAN_CRN2 = :V_YD_WRK_PLAN_CRN2
							      ,CHARGE_LOT_NO_DIV_YN = :V_CHARGE_LOT_NO_DIV_YN
							      ,YD_TO_LOC_GUIDE = :V_YD_TO_LOC_GUIDE
							   
							 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID   */
							commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTbYmWrkBook", logId, methodNm, "작업예약(TB_YM_WRKBOOK) 변경");				
						}
					}
					
					//크레인 스케줄 기동 YMYMJ203 호출
					jrCrnSchMsg.setField("JMS_TC_CD"			, "YMYMJ203"); 
					jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시	
					jrCrnSchMsg.setField("YD_SCH_CD"  			, ""); //야드스케쥴코드
					jrCrnSchMsg.setField("YD_EQP_ID"  			, ""); //야드설비ID
					
					//jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);
					
				} else {
					throw new Exception("작업예약이 존재하는 재료번호와 작업예약이 존재하지 않는 재료번호를 동시에 스케줄 기동 시킬 수 없습니다!!");
				}
				
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrCrnSchMsg;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of procCrnSchStart2_save	
	
	/**
	 *      [A] 오퍼레이션명 : Pallet조회 (B) - 스케줄기동 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord procCrnSchStart(GridData gdReq) throws DAOException {
		String methodNm = "Pallet조회 (B) - 스케줄기동 [BSlabJspSeEJB.procCrnSchStart] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;
			String currDate   = commUtils.getDateTime14();	//현재시각
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			JDTORecordSet rsResult  = null;
			JDTORecordSet rsResult2 = null;
			String sSchCode = "";
			String s_STACK_YD_GP = "";
			String s_STACK_BAY_GP = "";
			String sYD_SCH_PRIOR = "";
			String sYD_MULTI_WRK_YN = "";
			String sYD_WRK_CRN = "";
			String sYD_WBOOK_ID = "";
			String sStkCol = "";
			String sSTACK_LAYER_STAT = "";
			String szTRN_EQP_CD = "";
			String sCHANGE_CRN = "";
			
			String sCHARGE_LOT_NO_DIV_YN = commUtils.nvl(gdReq.getParam("CHARGE_LOT_NO_DIV_YN"),"Y");
			
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();

			if(rowCnt > 0) {
			
				sStkCol = commUtils.trim(gdReq.getHeader("STACK_COL_GP").getValue(0));
				s_STACK_YD_GP	= sStkCol.substring(0,1);
				s_STACK_BAY_GP	= sStkCol.substring(1,2);
				sYD_WBOOK_ID	= commUtils.trim(gdReq.getHeader("YD_WBOOK_ID").getValue(0));
				szTRN_EQP_CD	= commUtils.trim(gdReq.getHeader("TRN_EQP_CD").getValue(0));
				sCHANGE_CRN		= commUtils.getValue(gdReq, "CHANGE_CRN", 0); 
				
				commUtils.printLog(logId, "=======:::: STACK_COL_GP : " +  sStkCol , "SL");
				commUtils.printLog(logId, "=======:::: YD_WBOOK_ID : " +  sYD_WBOOK_ID , "SL");
				commUtils.printLog(logId, "=======:::: TRN_EQP_CD : " +  szTRN_EQP_CD , "SL");
				commUtils.printLog(logId, "=======:::: CHANGE_CRN : " +  sCHANGE_CRN , "SL");
				
				//스케줄코드 생성  - 이송하차(L)
				sSchCode = s_STACK_YD_GP + s_STACK_BAY_GP + "PT02LM";
				
				if("".equals(sCHANGE_CRN)) { //그리드에서 크레인변경을 지정하지 않으면 스케줄 기준에 의해서 크래인을 지정한다.
				
					//스케줄코드로 스케줄기준Table조회
					jrParam.setField("YD_SCH_CD", sSchCode);
			    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule  
					SELECT YD_SCH_CD
					     , YD_WRK_CRN
					     , YD_WRK_CRN_PRIOR
					     , YD_MULTI_WRK_YN
					  FROM TB_YM_SCHEDULERULE
					 WHERE DEL_YN = 'N'
					   AND YD_SCH_CD = :V_YD_SCH_CD
					*/   
					rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
			    	
					if (rsResult2 != null && rsResult2.size() > 0) {
						sYD_WRK_CRN = rsResult2.getRecord(0).getFieldString("YD_WRK_CRN"); //야드작업크레인
						sYD_SCH_PRIOR = rsResult2.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
						sYD_MULTI_WRK_YN = rsResult2.getRecord(0).getFieldString("YD_MULTI_WRK_YN"); //야드멀티작업여부
					} else {
						throw new Exception("B열연 코일 스케쥴 코드 이상 : [" + sSchCode + "]");
					}			
					
					if("Y".equals(sYD_MULTI_WRK_YN)) {
						jrParam.setField("YD_SCH_ST_GP"		, "N"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업, N:멀티작업)
						if("A".equals(s_STACK_BAY_GP)) {
							if("2ACRA1".equals(sYD_WRK_CRN)) {
								jrParam.setField("YD_WRK_PLAN_CRN"	, "2ACRA1"); //야드작업계획크레인
								jrParam.setField("YD_WRK_PLAN_CRN2"	, "2ACRA2"); //야드작업계획크레인2
							} else {
								jrParam.setField("YD_WRK_PLAN_CRN"	, "2ACRA2"); //야드작업계획크레인
								jrParam.setField("YD_WRK_PLAN_CRN2"	, "2ACRA1"); //야드작업계획크레인2
							}
						} else if("D".equals(s_STACK_BAY_GP)) {
							if("2DCRD3".equals(sYD_WRK_CRN)) {
								jrParam.setField("YD_WRK_PLAN_CRN"	, "2DCRD3"); //야드작업계획크레인
								jrParam.setField("YD_WRK_PLAN_CRN2"	, "2DCRD2"); //야드작업계획크레인2
							} else {
								jrParam.setField("YD_WRK_PLAN_CRN"	, "2DCRD2"); //야드작업계획크레인
								jrParam.setField("YD_WRK_PLAN_CRN2"	, "2DCRD3"); //야드작업계획크레인2
							}
						} else {
							jrParam.setField("YD_WRK_PLAN_CRN"	, sYD_WRK_CRN); //야드작업계획크레인
							jrParam.setField("YD_WRK_PLAN_CRN2"	, ""); //야드작업계획크레인2
						}
						
					} else {
						jrParam.setField("YD_SCH_ST_GP"		, "M"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업, N:멀티작업)
						jrParam.setField("YD_WRK_PLAN_CRN"	, sYD_WRK_CRN); //야드작업계획크레인
						jrParam.setField("YD_WRK_PLAN_CRN2"	, ""); //야드작업계획크레인2
					}
				} else { //그리드에서 크래인변경을 지정한 경우
					
					if("MULTI".equals(sCHANGE_CRN)) {
						jrParam.setField("YD_SCH_ST_GP"		, "N"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업, N:멀티작업)
						
						if("A".equals(s_STACK_BAY_GP)) {
							jrParam.setField("YD_WRK_PLAN_CRN"	, "2ACRA1"); //야드작업계획크레인
							jrParam.setField("YD_WRK_PLAN_CRN2"	, "2ACRA2"); //야드작업계획크레인2
						} else if("D".equals(s_STACK_BAY_GP)) {
							jrParam.setField("YD_WRK_PLAN_CRN"	, "2DCRD2"); //야드작업계획크레인
							jrParam.setField("YD_WRK_PLAN_CRN2"	, "2DCRD3"); //야드작업계획크레인2
						} else {
							jrParam.setField("YD_WRK_PLAN_CRN"	, sCHANGE_CRN); //야드작업계획크레인
							jrParam.setField("YD_WRK_PLAN_CRN2"	, ""); //야드작업계획크레인2
						}
					} else if("MULTI2".equals(sCHANGE_CRN)) {
						jrParam.setField("YD_SCH_ST_GP"		, "N"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업, N:멀티작업)
						
						if("A".equals(s_STACK_BAY_GP)) {
							jrParam.setField("YD_WRK_PLAN_CRN"	, "2ACRA2"); //야드작업계획크레인
							jrParam.setField("YD_WRK_PLAN_CRN2"	, "2ACRA1"); //야드작업계획크레인2
						} else if("D".equals(s_STACK_BAY_GP)) {
							jrParam.setField("YD_WRK_PLAN_CRN"	, "2DCRD3"); //야드작업계획크레인
							jrParam.setField("YD_WRK_PLAN_CRN2"	, "2DCRD2"); //야드작업계획크레인2
						} else {
							jrParam.setField("YD_WRK_PLAN_CRN"	, sCHANGE_CRN); //야드작업계획크레인
							jrParam.setField("YD_WRK_PLAN_CRN2"	, ""); //야드작업계획크레인2
						}
					} else {
						jrParam.setField("YD_SCH_ST_GP"		, "M"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업, N:멀티작업)
						if("A".equals(s_STACK_BAY_GP)) {
							if("2ACRA1".equals(sCHANGE_CRN)) {
								jrParam.setField("YD_WRK_PLAN_CRN"	, "2ACRA1"); //야드작업계획크레인
								jrParam.setField("YD_WRK_PLAN_CRN2"	, "2ACRA2"); //야드작업계획크레인2
							} else {
								jrParam.setField("YD_WRK_PLAN_CRN"	, "2ACRA2"); //야드작업계획크레인
								jrParam.setField("YD_WRK_PLAN_CRN2"	, "2ACRA1"); //야드작업계획크레인2
							}
						} else if("D".equals(s_STACK_BAY_GP)) {
							if("2DCRD3".equals(sCHANGE_CRN)) {
								jrParam.setField("YD_WRK_PLAN_CRN"	, "2DCRD3"); //야드작업계획크레인
								jrParam.setField("YD_WRK_PLAN_CRN2"	, "2DCRD2"); //야드작업계획크레인2
							} else {
								jrParam.setField("YD_WRK_PLAN_CRN"	, "2DCRD2"); //야드작업계획크레인
								jrParam.setField("YD_WRK_PLAN_CRN2"	, "2DCRD3"); //야드작업계획크레인2
							}
						} else {
							jrParam.setField("YD_WRK_PLAN_CRN"	, sCHANGE_CRN); //야드작업계획크레인
							jrParam.setField("YD_WRK_PLAN_CRN2"	, ""); //야드작업계획크레인2
						}
					}
				}
				
				
				if("".equals(sYD_WBOOK_ID)) {
				
					//작업예약 생성
					sYD_WBOOK_ID = commDao.getSeqId(logId, methodNm, "WrkBook");
					
					/**********************************************************
					* 작업예약(TB_YM_WRKBOOK) 생성
					**********************************************************/
					jrParam.setField("YD_WBOOK_ID"			, sYD_WBOOK_ID);
					jrParam.setField("YD_GP"				, s_STACK_YD_GP);
					jrParam.setField("YD_BAY_GP"			, s_STACK_BAY_GP);
					jrParam.setField("YD_SCH_CD"			, sSchCode); //야드스케쥴코드
					jrParam.setField("YD_SCH_PRIOR"			, sYD_SCH_PRIOR); //야드스케쥴우선순위
					jrParam.setField("YD_SCH_PROG_STAT"		, "W"); //야드스케쥴진행상태(W:스케줄수행대기)
					jrParam.setField("YD_SCH_REQ_GP"		, "C"); //야드스케쥴요청구분 C:영차차량도착, F:공차차량도착
					jrParam.setField("TRN_EQP_CD"			, szTRN_EQP_CD); //운송장비코드
					jrParam.setField("YD_CAR_USE_GP"		, "L"); //야드차량사용구분 L:구내운송
					jrParam.setField("CHARGE_LOT_NO_DIV_YN"	, sCHARGE_LOT_NO_DIV_YN); //장입순번분리여부
					
					commDao.insert(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.insWrkBook", logId, methodNm, "작업예약(TB_YM_WRKBOOK) 생성");
					
					for (int ii = 0; ii < rowCnt; ii++) {
						
						/**********************************************************
						* 작업예약재료(TB_YM_WRKBOOKMTL) 생성
						**********************************************************/
						jrParam.setField("YD_WBOOK_ID"			, sYD_WBOOK_ID);
						jrParam.setField("STOCK_ID"				, commUtils.getValue(gdReq, "STOCK_ID", ii));
						jrParam.setField("STACK_COL_GP"			, sStkCol);
						jrParam.setField("STACK_BED_GP"			, "01");
						jrParam.setField("STACK_LAYER_GP"		, commUtils.getValue(gdReq, "STACK_LAYER_GP", ii));
						jrParam.setField("MTL_YD_TO_LOC_GUIDE"	, commUtils.getValue(gdReq, "MTL_YD_TO_LOC_GUIDE", ii));
						
						commDao.insert(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.insYmWrkBookMtl", logId, methodNm, "작업예약재료(TB_YM_WRKBOOKMTL) 생성");
					}

				} else {
					
					/**********************************************************
					* 작업예약(TB_YM_WRKBOOK) 수정
					**********************************************************/
					jrParam.setField("YD_BAY_GP"			, s_STACK_BAY_GP); //동구분
					jrParam.setField("YD_SCH_CD"			, sSchCode		); //스케줄코드
					jrParam.setField("YD_SCH_PRIOR"			, sYD_SCH_PRIOR	); //야드스케쥴우선순위
					jrParam.setField("YD_WBOOK_ID"			, sYD_WBOOK_ID	); //작업예약ID
					jrParam.setField("CHARGE_LOT_NO_DIV_YN"	, sCHARGE_LOT_NO_DIV_YN); //장입순번분리여부
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTbYmWrkBook
					--작업예약 크레인 변경,Multi 작업
					UPDATE TB_YM_WRKBOOK
					   SET MODIFIER = :V_MODIFIER
					      ,MOD_DDTT = SYSDATE
					      ,YD_BAY_GP = :V_YD_BAY_GP
					      ,YD_SCH_CD = :V_YD_SCH_CD
					      ,YD_SCH_PRIOR = :V_YD_SCH_PRIOR
					      ,YD_SCH_ST_GP = :V_YD_SCH_ST_GP
					      ,YD_WRK_PLAN_CRN = :V_YD_WRK_PLAN_CRN
					      ,YD_WRK_PLAN_CRN2 = :V_YD_WRK_PLAN_CRN2
					      ,CHARGE_LOT_NO_DIV_YN = :V_CHARGE_LOT_NO_DIV_YN
					      ,YD_TO_LOC_GUIDE = :V_YD_TO_LOC_GUIDE
					      ,DIST_SHIPASSIGN_GP = NVL(:V_DIST_SHIPASSIGN_GP,DIST_SHIPASSIGN_GP)
					   
					 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID    */
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTbYmWrkBook", logId, methodNm, "작업예약(TB_YM_WRKBOOK) 변경");				
					
					for (int ii = 0; ii < rowCnt; ii++) {
						
						/**********************************************************
						* 작업예약재료(TB_YM_WRKBOOKMTL) 수정
						**********************************************************/
						jrParam.setField("YD_WBOOK_ID"			, sYD_WBOOK_ID);
						jrParam.setField("STOCK_ID"				, commUtils.getValue(gdReq, "STOCK_ID", ii));
						jrParam.setField("MTL_YD_TO_LOC_GUIDE"	, commUtils.getValue(gdReq, "MTL_YD_TO_LOC_GUIDE", ii));
						/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updYmWrkBookMtlToLoc
						UPDATE TB_YM_WRKBOOKMTL
						   SET MODIFIER = :V_MODIFIER
						      ,MOD_DDTT = SYSDATE
						      ,MTL_YD_TO_LOC_GUIDE = :V_MTL_YD_TO_LOC_GUIDE
						 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
						   AND STOCK_ID = :V_STOCK_ID	 */					
						commDao.insert(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updYmWrkBookMtlToLoc", logId, methodNm, "작업예약재료(TB_YM_WRKBOOKMTL) 변경");
					}
				}
				
				
				//-------------------------------------------------------------------------------------------------------------------------
				//jrParam.setField("JMS_TC_CD", "YMYMJ202"); //야드작업예약ID
				//jrParam.setField("JMS_TC_CREATE_DDTT", currDate); //JMSTC생성일시				
				
				//jrParam.setField("YD_WBOOK_ID", sYD_WBOOK_ID); //야드작업예약ID
				//jrParam.setField("YD_SCH_CD"  , ""); //야드스케쥴코드
				//jrParam.setField("YD_EQP_ID"  , ""); //야드설비ID

				//jrRtn = commUtils.addSndData(jrRtn, jrParam);
				
				//크레인스케줄기동 전문
				//EJBConnector sndConn = new EJBConnector("default", "BSlabSchSeEJB", this);
				//jrRtn = (JDTORecord)sndConn.trx("procYMYMJ202", new Class[] { JDTORecord.class }, new Object[] { jrParam });
				
			}
			
			jrRtn = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			jrRtn.setField("JMS_TC_CD", "YMYMJ202"); //야드작업예약ID
			jrRtn.setField("JMS_TC_CREATE_DDTT", currDate); //JMSTC생성일시				
			
			jrRtn.setField("YD_WBOOK_ID", sYD_WBOOK_ID); //야드작업예약ID
			jrRtn.setField("YD_SCH_CD"  , ""); //야드스케쥴코드
			jrRtn.setField("YD_EQP_ID"  , ""); //야드설비ID
			
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of procCrnSchStart
	
	/**
	 *      [A] 오퍼레이션명 : Pallet조회 (B) - 하차위치변경 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord procChangeUdLoc(GridData gdReq) throws DAOException {
		String methodNm = "Pallet조회 (B) - 하차위치변경 [BSlabJspSeEJB.procChangeUdLoc] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;
			JDTORecordSet rsResult 	= null;
			JDTORecordSet rsResult2 = null;
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			String sSTOCK_ID;
			String sSTACK_LAYER_STAT;
			String sSTACK_LAYER_ACTIVE_STAT;
			String sSTACK_LAYER_GP;
			
			String sSchCode;
			String s_STACK_YD_GP = "";
			String s_STACK_BAY_GP = "";
			String sYD_SCH_PRIOR = "";
			String sYD_MULTI_WRK_YN = "";
			String sYD_WRK_CRN = "";
			String sYD_WBOOK_ID = "";
			String sYD_WBOOK_ID_OLD = "";
			String sYD_EQP_WRK_STAT = "";
			
			String sFROM_LOC 	= gdReq.getParam("FROM_LOC");
			String sTO_LOC 		= gdReq.getParam("TO_LOC");
			String sTRN_EQP_CD  = gdReq.getParam("TRN_EQP_CD");
			
			String sWLOC_CD = gdReq.getParam("WLOC_CD");
			String sYD_PNT_CD = gdReq.getParam("YD_PNT_CD");
			String sMOD_WLOC_CD = gdReq.getParam("MOD_WLOC_CD");
			String sMOD_YD_PNT_CD = gdReq.getParam("MOD_YD_PNT_CD");
			String sYD_CAR_PROG_STAT = gdReq.getParam("YD_CAR_PROG_STAT");
			
			
			/**********************************************************
			* 1. TB_YD_CARPOINT 변경
			**********************************************************/
			//FROM 위치 Clear
			jrParam.setField("YD_STK_COL_ACT_STAT"	, "C");
			jrParam.setField("TRN_EQP_CD"			, "");
			jrParam.setField("YD_STK_COL_GP"		, sFROM_LOC);
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updYdCarpointByYdStkColGp
			UPDATE TB_YD_CARPOINT
			   SET MODIFIER = :V_MODIFIER
			      ,MOD_DDTT = SYSDATE
			      ,YD_STK_COL_ACT_STAT = :V_YD_STK_COL_ACT_STAT
			      ,TRN_EQP_CD = :V_TRN_EQP_CD
			 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP   */ 			
			commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updYdCarpointByYdStkColGp", logId, methodNm, "TB_YD_CARPOINT 변경 - FROM위치");
			
			//TO 위치에 차량 설정
			jrParam.setField("YD_STK_COL_ACT_STAT"	, "L");
			jrParam.setField("TRN_EQP_CD"			, sTRN_EQP_CD);
			jrParam.setField("YD_STK_COL_GP"		, sTO_LOC);
			commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updYdCarpointByYdStkColGp", logId, methodNm, "TB_YD_CARPOINT 변경 - TO위치");
			
			
			/**********************************************************
			* 2. TB_YM_STACKCOL 변경
			**********************************************************/
			//FROM 위치 Clear
			jrParam.setField("YD_CAR_USE_GP"	, "");
			jrParam.setField("TRN_EQP_CD"		, "");
			jrParam.setField("CAR_NO"			, "");
			jrParam.setField("CARD_NO"			, "");
			jrParam.setField("WLOC_CD"			, "D3Y43");
			jrParam.setField("YD_PNT_CD"		, "1"+sFROM_LOC.substring(1,2)+sFROM_LOC.substring(4,6));
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateLayerstat_01
			UPDATE  TB_YM_STACKCOL
			   SET  YD_CAR_USE_GP = :V_YD_CAR_USE_GP
			       ,TRN_EQP_CD = :V_TRN_EQP_CD
			       ,CAR_NO = :V_CAR_NO
			       ,CARD_NO = :V_CARD_NO
			 WHERE  WLOC_CD = :V_WLOC_CD
			   AND  YD_PNT_CD = :V_YD_PNT_CD
			   AND  SECT_GP = 'PT' 		 */ 
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateLayerstat_01", logId, methodNm, "TB_YM_STACKCOL 변경 - FROM위치");
			
			//TO 위치에 차량 설정
			jrParam.setField("YD_CAR_USE_GP"	, "L");
			jrParam.setField("TRN_EQP_CD"		, sTRN_EQP_CD);
			jrParam.setField("CAR_NO"			, "");
			jrParam.setField("CARD_NO"			, "");
			jrParam.setField("WLOC_CD"			, "D3Y43");
			jrParam.setField("YD_PNT_CD"		, "1"+sTO_LOC.substring(1,2)+sTO_LOC.substring(4,6));
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateLayerstat_01", logId, methodNm, "TB_YM_STACKCOL 변경 - TO위치");
			
			
			/**********************************************************
			* 3. TB_YM_STACKLAYER 변경
			**********************************************************/
			jrParam.setField("STACK_COL_GP"		, sFROM_LOC); 
			jrParam.setField("STACK_BED_GP"		, "01"); 
			jrParam.setField("STACK_LAYER_GP"	, "%"); 
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getStockIdByLoc
			SELECT  STOCK_ID
			       ,STACK_LAYER_STAT
			       ,STACK_LAYER_ACTIVE_STAT
			       ,TRIM(TO_CHAR(STACK_BED_GP + 1,'00')) AS NEXT_BED_GP
			       ,STACK_COL_GP
			       ,STACK_BED_GP
			       ,STACK_LAYER_GP
			  FROM  TB_YM_STACKLAYER
			 WHERE  STACK_COL_GP = :V_STACK_COL_GP
			   AND  STACK_BED_GP = :V_STACK_BED_GP
			   AND  STACK_LAYER_GP LIKE :V_STACK_LAYER_GP || '%' */
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getStockIdByLoc", logId, methodNm, "TB_YM_STACKLAYER 정보 조회 - FROM위치 "); 
			for(int ii = 0; ii < rsResult.size() ; ii++) {
				
				sSTOCK_ID 				 = rsResult.getRecord(ii).getFieldString("STOCK_ID");	
				sSTACK_LAYER_STAT 		 = rsResult.getRecord(ii).getFieldString("STACK_LAYER_STAT");	
				sSTACK_LAYER_ACTIVE_STAT = rsResult.getRecord(ii).getFieldString("STACK_LAYER_ACTIVE_STAT");
				sSTACK_LAYER_GP			 = rsResult.getRecord(ii).getFieldString("STACK_LAYER_GP");
				
				//FROM 위치 Clear
				jrParam.setField("STOCK_ID"					, ""); 
				jrParam.setField("STACK_LAYER_STAT"			, "E"); 
				jrParam.setField("STACK_LAYER_ACTIVE_STAT"	, "C"); 
				jrParam.setField("STACK_COL_GP"				, sFROM_LOC); 
				jrParam.setField("STACK_BED_GP"				, "01"); 
				jrParam.setField("STACK_LAYER_GP"			, sSTACK_LAYER_GP); 
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLyrByLoc
				UPDATE TB_YM_STACKLAYER
				   SET 
				       MODIFIER = :V_MODIFIER
				      ,MOD_DDTT = SYSDATE
				      ,STOCK_ID = :V_STOCK_ID
				      ,STACK_LAYER_STAT = :V_STACK_LAYER_STAT
				      ,STACK_LAYER_ACTIVE_STAT = :V_STACK_LAYER_ACTIVE_STAT
				 WHERE  STACK_COL_GP = :V_STACK_COL_GP
				   AND  STACK_BED_GP = :V_STACK_BED_GP
				   AND  STACK_LAYER_GP = :V_STACK_LAYER_GP  */
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLyrByLoc", logId, methodNm, "TB_YM_STACKLAYER 변경 - FROM위치 ");
				
				//TO 위치 적치단 설정
				jrParam.setField("STOCK_ID"					, sSTOCK_ID); 
				jrParam.setField("STACK_LAYER_STAT"			, sSTACK_LAYER_STAT); 
				jrParam.setField("STACK_LAYER_ACTIVE_STAT"	, sSTACK_LAYER_ACTIVE_STAT); 
				jrParam.setField("STACK_COL_GP"				, sTO_LOC); 
				jrParam.setField("STACK_BED_GP"				, "01"); 
				jrParam.setField("STACK_LAYER_GP"			, sSTACK_LAYER_GP); 
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLyrByLoc", logId, methodNm, "TB_YM_STACKLAYER 변경 - TO위치 ");
			
			}
			
			//하차위치 변경
			if(sYD_CAR_PROG_STAT.equals("B") || sYD_CAR_PROG_STAT.equals("C") || sYD_CAR_PROG_STAT.equals("D"))
			{
				sYD_EQP_WRK_STAT = "L";//영차
					
				/**********************************************************
				* STOCK에서 기존  SCAN 좌표값 초기 화 
				**********************************************************/
				for(int ii = 0; ii < rsResult.size() ; ii++) {
					
					sSTOCK_ID 				 = rsResult.getRecord(ii).getFieldString("STOCK_ID");	
					
					jrParam.setField("STOCK_ID"			, sSTOCK_ID); 
					jrParam.setField("LOAD_LOC_CD"		, ""); 
					jrParam.setField("WGT_CENTER_XAXIS"	, ""); 
					jrParam.setField("WGT_CENTER_YAXIS"	, ""); 
					jrParam.setField("WGT_CENTER_ZAXIS"	, ""); 
					jrParam.setField("BENDING_GP"		, ""); 
					jrParam.setField("BENDING_AXIS"		, ""); 
					jrParam.setField("YD_STK_COL_DIR_GP", ""); 
					jrParam.setField("YD_STK_COL_DEG"	, ""); 
					jrParam.setField("CAU_CD"			, "_"); 
					/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockByA8YML029
					UPDATE TB_YM_STOCK
					   SET MODIFIER = :V_MODIFIER
					      ,MOD_DDTT = SYSDATE
					      ,LOAD_LOC_CD = :V_LOAD_LOC_CD
					      ,WGT_CENTER_XAXIS = :V_WGT_CENTER_XAXIS
					      ,WGT_CENTER_YAXIS = :V_WGT_CENTER_YAXIS
					      ,WGT_CENTER_ZAXIS = :V_WGT_CENTER_ZAXIS
					      ,BENDING_GP = :V_BENDING_GP
					      ,BENDING_AXIS = :V_BENDING_AXIS
					      ,YD_STK_COL_DIR_GP = :V_YD_STK_COL_DIR_GP
					      ,YD_STK_COL_DEG = :V_YD_STK_COL_DEG
					      ,YD_RULE_PL_RS_GP = CASE WHEN TO_NUMBER(NVL(:V_BENDING_AXIS,0)) >= (SELECT NVL(DTL_ITM1,0)
					                                                                                FROM USRYMA.TB_YM_RULE 
					                                                                               WHERE CD_GP = '2' 
					                                                                                 AND REPR_CD_GP LIKE'YM2009'
					                                                                                 AND DEL_YN = 'N') THEN 'Y'
					                               ELSE '' END 
					      ,CAU_CD = NVL(:V_CAU_CD,'0000');
					 WHERE STOCK_ID = :V_STOCK_ID  */
					commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockByA8YML029", logId, methodNm, "STOCK에서 기존  SCAN 좌표값 초기 화  (tb_ym_stock 관련 항목 update)");
				}
				
				/**********************************************************
				* 변경할 작업예약 조회 및 스케줄 코드 기준 조회
				**********************************************************/
				jrParam.setField("YD_SCH_CD"		, sFROM_LOC.substring(0,2) + "PT02LM"); 
				jrParam.setField("TRN_EQP_CD"		, sTRN_EQP_CD); 
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getWbookIdBySchCd 
				SELECT YD_WBOOK_ID
				  FROM TB_YM_WRKBOOK
				 WHERE DEL_YN = 'N'
				   AND YD_SCH_CD = :V_YD_SCH_CD
				   AND TRN_EQP_CD = :V_TRN_EQP_CD */
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getWbookIdBySchCd", logId, methodNm, "작업예약ID 조회 - FROM위치 ");
				
				
				//TO위치 스케줄 코드및 기준
				s_STACK_YD_GP	= sTO_LOC.substring(0,1);
				s_STACK_BAY_GP	= sTO_LOC.substring(1,2);
				
				//스케줄코드 생성  - 이송하차(L)
				sSchCode = s_STACK_YD_GP + s_STACK_BAY_GP + "PT02LM";
				
				//스케줄코드로 스케줄기준Table조회
				jrParam.setField("YD_SCH_CD", sSchCode);
		    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule  
				SELECT YD_SCH_CD
				     , YD_WRK_CRN
				     , YD_WRK_CRN_PRIOR
				     , YD_MULTI_WRK_YN
				  FROM TB_YM_SCHEDULERULE
				 WHERE DEL_YN = 'N'
				   AND YD_SCH_CD = :V_YD_SCH_CD
				*/   
				rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
		    	
				if (rsResult2 != null && rsResult2.size() > 0) {
					sYD_WRK_CRN = rsResult2.getRecord(0).getFieldString("YD_WRK_CRN"); //야드작업크레인
					sYD_SCH_PRIOR = rsResult2.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
					sYD_MULTI_WRK_YN = rsResult2.getRecord(0).getFieldString("YD_MULTI_WRK_YN"); //야드멀티작업여부
				} else {
					throw new Exception("B열연 코일 스케쥴 코드 이상 : [" + sSchCode + "]");
				}			
				
				if("Y".equals(sYD_MULTI_WRK_YN)) {
					jrParam.setField("YD_SCH_ST_GP"		, "N"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업, N:멀티작업)
					if("A".equals(s_STACK_BAY_GP)) {
						if("2ACRA1".equals(sYD_WRK_CRN)) {
							jrParam.setField("YD_WRK_PLAN_CRN"	, "2ACRA1"); //야드작업계획크레인
							jrParam.setField("YD_WRK_PLAN_CRN2"	, "2ACRA2"); //야드작업계획크레인2
						} else {
							jrParam.setField("YD_WRK_PLAN_CRN"	, "2ACRA2"); //야드작업계획크레인
							jrParam.setField("YD_WRK_PLAN_CRN2"	, "2ACRA1"); //야드작업계획크레인2
						}
					} else if("D".equals(s_STACK_BAY_GP)) {
						if("2DCRD3".equals(sYD_WRK_CRN)) {
							jrParam.setField("YD_WRK_PLAN_CRN"	, "2DCRD3"); //야드작업계획크레인
							jrParam.setField("YD_WRK_PLAN_CRN2"	, "2DCRD2"); //야드작업계획크레인2
						} else {
							jrParam.setField("YD_WRK_PLAN_CRN"	, "2DCRD2"); //야드작업계획크레인
							jrParam.setField("YD_WRK_PLAN_CRN2"	, "2DCRD3"); //야드작업계획크레인2
						}
					} else {
						jrParam.setField("YD_WRK_PLAN_CRN"	, sYD_WRK_CRN); //야드작업계획크레인
						jrParam.setField("YD_WRK_PLAN_CRN2"	, ""); //야드작업계획크레인2
					}
					
				} else {
					jrParam.setField("YD_SCH_ST_GP"		, "M"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업, N:멀티작업)
					jrParam.setField("YD_WRK_PLAN_CRN"	, sYD_WRK_CRN); //야드작업계획크레인
					jrParam.setField("YD_WRK_PLAN_CRN2"	, ""); //야드작업계획크레인2
				}
				
				
				for(int ii = 0; ii < rsResult.size() ; ii++) {
					
					sYD_WBOOK_ID = rsResult.getRecord(ii).getFieldString("YD_WBOOK_ID");
					
					if(!sYD_WBOOK_ID_OLD.equals(sYD_WBOOK_ID)) {
						
						/**********************************************************
						* 4. TB_YM_WRKBOOK 변경
						**********************************************************/
						jrParam.setField("YD_BAY_GP"		, s_STACK_BAY_GP); //동구분
						jrParam.setField("YD_SCH_CD"		, sSchCode		); //스케줄코드
						jrParam.setField("YD_SCH_PRIOR"		, sYD_SCH_PRIOR	); //야드스케쥴우선순위
						jrParam.setField("YD_WBOOK_ID"		, sYD_WBOOK_ID	); //작업예약ID
						/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTbYmWrkBook
						--작업예약 크레인 변경,Multi 작업
						UPDATE TB_YM_WRKBOOK
						   SET MODIFIER = :V_MODIFIER
						      ,MOD_DDTT = SYSDATE
						      ,YD_BAY_GP = :V_YD_BAY_GP
						      ,YD_SCH_CD = :V_YD_SCH_CD
						      ,YD_SCH_PRIOR = :V_YD_SCH_PRIOR
						      ,YD_SCH_ST_GP = :V_YD_SCH_ST_GP
						      ,YD_WRK_PLAN_CRN = :V_YD_WRK_PLAN_CRN
						      ,YD_WRK_PLAN_CRN2 = :V_YD_WRK_PLAN_CRN2
						   
						 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID   */
						commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updTbYmWrkBook", logId, methodNm, "작업예약(TB_YM_WRKBOOK) 변경");				
						
						/**********************************************************
						* 5. TB_YM_WRKBOOKMTL 변경
						**********************************************************/
						jrParam.setField("STACK_COL_GP"		, sTO_LOC); 
						jrParam.setField("YD_WBOOK_ID"		, sYD_WBOOK_ID	); //작업예약ID
						/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updWrkBookMtlStackColGp
						UPDATE  TB_YM_WRKBOOKMTL
						   SET  MODIFIER = :V_MODIFIER
						       ,MOD_DDTT = SYSDATE
						       ,STACK_COL_GP = :V_STACK_COL_GP
						 WHERE  YD_WBOOK_ID = :V_YD_WBOOK_ID    */    
						commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updWrkBookMtlStackColGp", logId, methodNm, "작업예약재료(TB_YM_WRKBOOKMTL) 변경");				
					}
					sYD_WBOOK_ID_OLD = sYD_WBOOK_ID;
				}

				
				/**********************************************************
				* 6. TB_YD_CARSCH 변경
				**********************************************************/
				jrParam.setField("YD_PNT_CD3"	, "1" + s_STACK_BAY_GP + sTO_LOC.substring(4,6)); 
				jrParam.setField("TO_LOC"		, sTO_LOC); 
				jrParam.setField("FROM_LOC"		, sFROM_LOC); 
				jrParam.setField("TRN_EQP_CD"	, sTRN_EQP_CD);
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updYdCarSchYdPntCd3
				--하차위치변경
				UPDATE  TB_YD_CARSCH
				   SET  MODIFIER = :V_MODIFIER
				       ,MOD_DDTT = SYSDATE
				       ,YD_PNT_CD3 = :V_YD_PNT_CD3
				       ,YD_CARUD_STOP_LOC = :V_TO_LOC
				       
				 WHERE  YD_CAR_SCH_ID = (
				                               SELECT MAX(YD_CAR_SCH_ID) AS YD_CAR_SCH_ID
				                                FROM USRYDA.TB_YD_CARSCH
				                               WHERE (CASE WHEN YD_CAR_PROG_STAT IN('1','2','3','4','5') THEN YD_CARLD_STOP_LOC
				                                           ELSE YD_CARUD_STOP_LOC END) = :V_FROM_LOC
				                                 AND DEL_YN='N'
				                                 AND TRN_EQP_CD = :V_TRN_EQP_CD
				                          )  */
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updYdCarSchYdPntCd3", logId, methodNm, "차량스케줄(TB_YD_CARSCH) 하차위치변경");

			//상차위치변경
			}else{

				sYD_EQP_WRK_STAT = "U";//공차
				
				//TO위치 스케줄 코드및 기준
				s_STACK_YD_GP	= sTO_LOC.substring(0,1);
				s_STACK_BAY_GP	= sTO_LOC.substring(1,2);
				
				/**********************************************************
				* 변경할 작업예약 조회 및 스케줄 코드 기준 조회
				**********************************************************/
				jrParam.setField("YD_SCH_CD"		, sFROM_LOC.substring(0,2) + "PT02UM"); 
				jrParam.setField("TRN_EQP_CD"		, sTRN_EQP_CD); 
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getWbookIdBySchCd 
				SELECT YD_WBOOK_ID
				  FROM TB_YM_WRKBOOK
				 WHERE DEL_YN = 'N'
				   AND YD_SCH_CD = :V_YD_SCH_CD
				   AND TRN_EQP_CD = :V_TRN_EQP_CD */
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getWbookIdBySchCd", logId, methodNm, "작업예약ID 조회 - FROM위치 ");
				
				
				for(int ii = 0; ii < rsResult.size() ; ii++) {
					
					sYD_WBOOK_ID = rsResult.getRecord(ii).getFieldString("YD_WBOOK_ID");
					
					if(!sYD_WBOOK_ID_OLD.equals(sYD_WBOOK_ID)) {
						
						jrParam.setResultCode(logId);	//Log ID
						jrParam.setResultMsg(methodNm);	//Log Method Name
						
						jrParam.setField("YD_WBOOK_ID"  , sYD_WBOOK_ID );
						
						/**********************************************************
						* 2. 작업예약 취소
						**********************************************************/
						jrRtn = commUtils.addSndData(jrRtn, this.trtWrkBookCncl(jrParam));
					}
					sYD_WBOOK_ID_OLD = sYD_WBOOK_ID;
				}

				
				/**********************************************************
				* 6. TB_YD_CARSCH 변경
				**********************************************************/
				jrParam.setField("YD_PNT_CD1"	, "1" + s_STACK_BAY_GP + sTO_LOC.substring(4,6)); 
				jrParam.setField("TO_LOC"		, sTO_LOC); 
				jrParam.setField("FROM_LOC"		, sFROM_LOC); 
				jrParam.setField("TRN_EQP_CD"	, sTRN_EQP_CD);
				/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updYdCarSchYdPntCd4 
					--상차위치변경
					UPDATE  TB_YD_CARSCH
					   SET  MODIFIER = :V_MODIFIER
					       ,MOD_DDTT = SYSDATE
					       ,YD_PNT_CD1 = :V_YD_PNT_CD1
					       ,YD_CARLD_STOP_LOC = :V_TO_LOC
					       
					 WHERE  YD_CAR_SCH_ID = (
					                               SELECT MAX(YD_CAR_SCH_ID) AS YD_CAR_SCH_ID
					                                FROM USRYDA.TB_YD_CARSCH
					                               WHERE (CASE WHEN YD_CAR_PROG_STAT IN('1','2','3','4','5') THEN YD_CARLD_STOP_LOC
					                                           ELSE YD_CARUD_STOP_LOC END) = :V_FROM_LOC
					                                 AND DEL_YN='N'
					                                 AND TRN_EQP_CD = :V_TRN_EQP_CD
					                          )  */
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updYdCarSchYdPntCd4", logId, methodNm, "차량스케줄(TB_YD_CARSCH) 상차위치변경");

			
				//이송상차 도착처리시 TB_YM_RULE 의 YM2019 해당 포인트를 초기화 한다..
				jrParam.setField("REPR_CD_GP"	, "YM2019" );
				jrParam.setField("CD_GP"		, "2" );
				jrParam.setField("ITEM"			, sTO_LOC);
				jrParam.setField("DTL_ITM4"		, "");
				jrParam.setField("DTL_ITM5"		, "N");

				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updYmRuleNvl", logId, methodNm, "TB_YM_RULE YM2019 수정 - 초기화");
				
			}
			
			
			/**********************************************************
			* 7. 차량작업 예정정보(YMA8L008->YMA8L001->YMA8L002) 전문 생성
			**********************************************************/
			jrParam.setField("PT_LOAD_LOC", sTO_LOC);  
			jrParam.setField("TC_CODE", "A8YML016");  
			
	    	//차량예정정보
		    EJBConnector ejbConnPT2 = new EJBConnector("default", "BSlabL2RcvSeEJB", this);
		    jrRtn = (JDTORecord)ejbConnPT2.trx("rcvA8YML016", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			
			/**********************************************************
			* 7. 저장위치제원(YMA8L001) 전문 생성
			**********************************************************/
			//FROM 위치 저장위치제원  전송 Data 생성
			jrParam.setField("YD_INFO_SYNC_CD"		, "4"           	); //야드정보동기화코드
			jrParam.setField("STACK_COL_GP"  		, sFROM_LOC 		); //야드적치열구분
			jrParam.setField("STACK_BED_GP"  		, "01"          	); //야드적치Bed번호
			jrParam.setField("YD_CAR_ARRSTRT_STAT" 	, "S"				); //A:도착, S:출발
			jrParam.setField("YD_CAR_USE_GP"    	, "L"				); //L:구내운송, G:출하차량
			jrParam.setField("YD_EQP_WRK_STAT"  	, sYD_EQP_WRK_STAT	); //U:공차, L:영차
			jrParam.setField("TRN_EQP_CD"  			, sTRN_EQP_CD		); //운송장비코드
			jrParam.setField("YD_CAR_AIM_YD_GP"		, s_STACK_YD_GP		); 
			jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA8L001_CarInfo", jrParam));

			//TO 위치 저장위치제원  전송 Data 생성
//			jrParam.setField("YD_INFO_SYNC_CD"		, "4"           	); //야드정보동기화코드
//			jrParam.setField("STACK_COL_GP"  		, sTO_LOC 			); //야드적치열구분
//			jrParam.setField("STACK_BED_GP"  		, "01"          	); //야드적치Bed번호
//			jrParam.setField("YD_CAR_ARRSTRT_STAT" 	, "A"				); //A:도착, S:출발
//			jrParam.setField("YD_CAR_USE_GP"    	, "L"				); //L:구내운송, G:출하차량
//			jrParam.setField("YD_EQP_WRK_STAT"  	, "L"				); //U:공차, L:영차
//			jrParam.setField("TRN_EQP_CD"  			, sTRN_EQP_CD		); //운송장비코드
//			jrParam.setField("YD_CAR_AIM_YD_GP"		, s_STACK_YD_GP		); 
//			jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA8L001_CarInfo", jrParam));
  
			  
			/**********************************************************
			* 8. 하차위치변경(YDTSJ017) 전문 생성
			**********************************************************/
			JDTORecord jrTemp = JDTORecordFactory.getInstance().create();

			jrTemp.setResultCode(logId);	//Log ID
			jrTemp.setResultMsg(methodNm);	//Log Method Name
			jrTemp.setField("JMS_TC_CD"				, "YDTSJ017");
			jrTemp.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시
			jrTemp.setField("TRN_EQP_CD"			, sTRN_EQP_CD);
			jrTemp.setField("WLOC_CD"				, sWLOC_CD);
			jrTemp.setField("YD_PNT_CD"				, sYD_PNT_CD);
			jrTemp.setField("MOD_WLOC_CD"			, sMOD_WLOC_CD);
			jrTemp.setField("MOD_YD_PNT_CD"			, sMOD_YD_PNT_CD);
			
			//전송 Data 생성
			jrRtn = commUtils.addSndData(jrRtn,jrTemp);		
			
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of procChangeUdLoc
	
	/**
	 *      [A] 오퍼레이션명 : Pallet조회 (B) - 상차완료처리 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord runLdCmplProc(GridData gdReq) throws DAOException {
		String methodNm = "Pallet조회 (B) - 상차완료처리 [BSlabJspSeEJB.runLdCmplProc] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//Return Value
			JDTORecord jrRtn = null;
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
	    	jrParam.setField("YD_CAR_SCH_ID", gdReq.getParam("YD_CAR_SCH_ID"));
	    	jrParam.setField("TRN_EQP_CD", gdReq.getParam("TRN_EQP_CD"));
	    	
	    	//상차완료처리
		    EJBConnector ejbConnPT2 = new EJBConnector("default", "YmCommSeEJB", this);
		    jrRtn = (JDTORecord)ejbConnPT2.trx("runLdCmplProc", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of runLdCmplProc
	
	/**
	 *      [A] 오퍼레이션명 : W/B, CTC - Take Out
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord runTakeOut(GridData gdReq) throws DAOException {
		String methodNm = "W/B, CTC - Take Out [BSlabJspSeEJB.runTakeOut] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//Return Value
			JDTORecord jrRtn = null;
			String currDate   = commUtils.getDateTime14();	//현재시각
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			JDTORecordSet rsResult2 = null;
			String s_STACK_YD_GP = "";
			String s_STACK_BAY_GP = "";
			String sYD_SCH_PRIOR = "";
			String sYD_MULTI_WRK_YN = "N";
			String sYD_WRK_CRN = "";
			String wbook_ID = "";
			String sYD_TO_LOC_GUIDE 		= commUtils.nvl(gdReq.getParam("YD_TO_LOC_GUIDE"), "");
			String sYD_WRK_PLAN_CRN			= commUtils.nvl(gdReq.getParam("YD_WRK_PLAN_CRN"), "");
			String sYD_ALT_CRN				= "";
			//스케줄코드
			String sSchCode = gdReq.getParam("YD_SCH_CD");

			//--------------------------------------------------------------------------------------------------------------------------
			if("2CCRC1".equals(sYD_WRK_PLAN_CRN)) {
				sYD_WRK_CRN = "2CCRC1";
				sYD_ALT_CRN = "2CCRC2";
			} else if("2CCRC2".equals(sYD_WRK_PLAN_CRN)) {
				sYD_WRK_CRN = "2CCRC2";
				sYD_ALT_CRN = "2CCRC1";
			} else if("2CCRC3".equals(sYD_WRK_PLAN_CRN)) {
				sYD_WRK_CRN = "2CCRC3";
				sYD_ALT_CRN = "2CCRC2";
			} else if("2ACRA1".equals(sYD_WRK_PLAN_CRN)) {
				sYD_WRK_CRN = "2ACRA1";
				sYD_ALT_CRN = "2ACRA2";
			} else if("2ACRA2".equals(sYD_WRK_PLAN_CRN)) {
				sYD_WRK_CRN = "2ACRA2";
				sYD_ALT_CRN = "2ACRA1";
			}			
			
			jrParam.setField("YD_MULTI_WRK_YN"	, sYD_MULTI_WRK_YN );
			jrParam.setField("YD_WRK_CRN"		, sYD_WRK_CRN );
			jrParam.setField("YD_ALT_CRN"		, sYD_ALT_CRN);
			jrParam.setField("YD_SCH_CD"		, sSchCode);
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updSchRuleMultiYn
			UPDATE TB_YM_SCHEDULERULE
			   SET MODIFIER = :V_MODIFIER
			      ,MOD_DDTT = SYSDATE
			      ,YD_MULTI_WRK_YN = :V_YD_MULTI_WRK_YN
			      ,YD_WRK_CRN = :V_YD_WRK_CRN
			      ,YD_ALT_CRN = :V_YD_ALT_CRN
			 WHERE YD_SCH_CD = :V_YD_SCH_CD */
			commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updSchRuleMultiYn", logId, methodNm, "스케줄 기준 야드멀티작업여부 설정");
			//--------------------------------------------------------------------------------------------------------------------------
			
			
			//스케줄코드로 스케줄기준Table조회
			jrParam.setField("YD_SCH_CD", sSchCode);
	    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule  
			SELECT YD_SCH_CD
			     , YD_WRK_CRN
			     , YD_WRK_CRN_PRIOR
			     , YD_MULTI_WRK_YN
			  FROM TB_YM_SCHEDULERULE
			 WHERE DEL_YN = 'N'
			   AND YD_SCH_CD = :V_YD_SCH_CD
			*/   
			rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
	    	
			if (rsResult2 != null && rsResult2.size() > 0) {
				sYD_WRK_CRN = rsResult2.getRecord(0).getFieldString("YD_WRK_CRN"); //야드작업크레인
				sYD_SCH_PRIOR = rsResult2.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
				sYD_MULTI_WRK_YN = rsResult2.getRecord(0).getFieldString("YD_MULTI_WRK_YN"); //야드멀티작업여부
			} else {
				throw new Exception("B열연 코일 스케쥴 코드 이상 : [" + sSchCode + "]");
			}			
			s_STACK_YD_GP	= commUtils.getValue(gdReq, "STACK_COL_GP"   , 0).substring(0,1);
			s_STACK_BAY_GP	= commUtils.getValue(gdReq, "STACK_COL_GP"   , 0).substring(1,2);
			
			
			//작업예약ID생성
			wbook_ID = commDao.getSeqId(logId, methodNm, "WrkBook");
			
			/**********************************************************
			* 작업예약(TB_YM_WRKBOOK) 생성
			**********************************************************/
			jrParam.setField("YD_WBOOK_ID"		, wbook_ID);
			jrParam.setField("YD_GP"			, s_STACK_YD_GP);
			jrParam.setField("YD_BAY_GP"		, s_STACK_BAY_GP);
			jrParam.setField("YD_SCH_CD"		, sSchCode); //야드스케쥴코드
			jrParam.setField("YD_SCH_PRIOR"		, sYD_SCH_PRIOR); //야드스케쥴우선순위
			jrParam.setField("YD_SCH_PROG_STAT"	, "W"); //야드스케쥴진행상태(W:스케줄수행대기)
			jrParam.setField("YD_SCH_REQ_GP"	, "C"); //야드스케쥴요청구분 C:영차차량도착, F:공차차량도착
			jrParam.setField("YD_SCH_ST_GP"		, "M"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업, N:멀티작업)
			jrParam.setField("YD_WRK_PLAN_CRN"	, sYD_WRK_CRN); //야드작업계획크레인
			jrParam.setField("YD_WRK_PLAN_CRN2"	, ""); //야드작업계획크레인2
			jrParam.setField("YD_TO_LOC_GUIDE"	, sYD_TO_LOC_GUIDE); //야드To위치Guide
			
			commDao.insert(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.insWrkBook", logId, methodNm, "작업예약(TB_YM_WRKBOOK) 생성");
			
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				/**********************************************************
				* 작업예약재료(TB_YM_WRKBOOKMTL) 생성
				**********************************************************/
				jrParam.setField("YD_WBOOK_ID"		, wbook_ID);
				jrParam.setField("YD_SCH_CD"		, sSchCode); //야드스케쥴코드
				jrParam.setField("STOCK_ID"			, commUtils.trim(gdReq.getHeader("STOCK_ID").getValue(ii)));
				jrParam.setField("STACK_COL_GP"		, commUtils.trim(gdReq.getHeader("STACK_COL_GP").getValue(ii)));
				jrParam.setField("STACK_BED_GP"		, commUtils.trim(gdReq.getHeader("STACK_BED_GP").getValue(ii)));
				jrParam.setField("STACK_LAYER_GP"	, commUtils.trim(gdReq.getHeader("STACK_LAYER_GP").getValue(ii)));
				
				commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insYmWrkBookMtl", logId, methodNm, "작업예약재료(TB_YM_WRKBOOKMTL) 생성");
				
				/**********************************************************
				* 적치단(TB_YM_STACKLAYER)에 정보 생성하기
				**********************************************************/
				jrParam.setField("STOCK_ID"			, commUtils.trim(gdReq.getHeader("STOCK_ID").getValue(ii)));
				jrParam.setField("STACK_COL_GP"		, commUtils.trim(gdReq.getHeader("STACK_COL_GP").getValue(ii)));
				jrParam.setField("STACK_BED_GP"		, commUtils.trim(gdReq.getHeader("STACK_BED_GP").getValue(ii)));
				jrParam.setField("STACK_LAYER_GP"	, commUtils.trim(gdReq.getHeader("STACK_LAYER_GP").getValue(ii)));
				
				commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.setStackLayer", logId, methodNm, "적치단(TB_YM_STACKLAYER)에 SLAB정보 생성하기");
			}
			
			//-------------------------------------------------------------------------------------------------------------------------
			jrParam.setField("JMS_TC_CD", "YMYMJ202"); //야드작업예약ID
			jrParam.setField("JMS_TC_CREATE_DDTT", currDate); //JMSTC생성일시				
			
			jrParam.setField("YD_WBOOK_ID", wbook_ID); //야드작업예약ID
			jrParam.setField("YD_SCH_CD"  , ""); //야드스케쥴코드
			jrParam.setField("YD_EQP_ID"  , ""); //야드설비ID
			
			//크레인스케줄기동 전문
			EJBConnector sndConn = new EJBConnector("default", "BSlabSchSeEJB", this);
			jrRtn = (JDTORecord)sndConn.trx("procYMYMJ202", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of runTakeOut
	
	
	/**
	 * [A] 오퍼레이션명 : 화면을 통해서 시편재 및 핸드스카핑 보급요구
	 * 작업예약을 생성한다.
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecord scarfingWork(GridData gdReq) throws DAOException {
		
		/** As-Is 로직
			1. 스카핑생성 (별도 모듈 호출)
			 1-1. 화면의 스카핑 패턴(G, S, SP, 그외)에 따라 작업ID 생성
			 1-2. 저장품테이블 TB_YM_STOCK      UPDATE
			 1-3. 적치단테이블 TB_YM_STACKLAYER UPDATE
			 1-4. 1-1에서 생성한 작업ID를 List에 담아 return.
			 
			2. 스케쥴 call
			 2-1.  1-4에서 반환된 작업ID List를 인자로 스케쥴 모듈 call
		**/
		
		
		
		// To-Be Start
		String methodNm = "화면을 통해서 시편재 및 핸드스카핑 보급요구[BSlabJspSeEJB.scarfingWork] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		JDTORecord sndRecord = JDTORecordFactory.getInstance().create();
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
			JDTORecord jDrd = JDTORecordFactory.getInstance().create();
			
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			/**********************************************************
			* 1. 스카핑생성
			**********************************************************/
			JDTORecord jrParam		= JDTORecordFactory.getInstance().create();
			JDTORecord jrRtn 		= JDTORecordFactory.getInstance().create();
			JDTORecordSet rsResult  = null;
			JDTORecordSet rsResult2 = null;
			
			// 작업예약ID생성
			// 스카핑 작업 스케쥴 기준 변동 E동 핸드스카핑 >> E동 동내이적으로 변경
			
			String sYD_WBOOK_ID = commDao.getSeqId(logId, methodNm, "WrkBook");
			
			String sYD_SCH_CD	= "";	//  슬라브 스카핑 보급 스케쥴 코드
			//String sYD_SCH_CD	= "2EYD31MM";	//  슬라브 스카핑 보급 스케쥴 코드 : E동 동내이적(3)
			//String sYD_SCH_CD	= "2ESE01UM";	//  슬라브 스카핑 보급 스케쥴 코드 : E동 Scarfing 보급
			
		    jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
	 		jrParam.setResultMsg(methodNm);	//상위 Method 명
		    
			
	 		// 스케줄코드로 스케줄기준Table조회
	 		jrParam.setField("YD_SCH_CD", sYD_SCH_CD);
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule  
			SELECT YD_SCH_CD
			     , YD_WRK_CRN
			     , YD_WRK_CRN_PRIOR
			  FROM TB_YM_SCHEDULERULE
			 WHERE DEL_YN = 'N'
			   AND YD_SCH_CD = :V_YD_SCH_CD
			*/   
			rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
			
			String	sYD_SCH_PRIOR = "";
			if (rsResult2.size() > 0){
				sYD_SCH_PRIOR = rsResult2.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
			}
			
			
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				if(ii == 0){	// 하나의 작업ID에 여러개의 재료를 포함함
					
					// 스카핑 패턴 G, S는 핸드스카핑, 그 외는 머신스카핑 (SP 시편은 안들어오므로 제외함)
					if("G".equals(commUtils.getValue(gdReq, "SCF패턴", ii)) || "S".equals(commUtils.getValue(gdReq, "SCF패턴", ii))){
						sYD_SCH_CD = "2EYD31MM"; // 핸드스카핑 (3번크레인 동내이적)
					}else{
						sYD_SCH_CD = "2ESE01UM"; // 머신스카핑 (E동 Scarfing 보급)w
					}
					
					/**********************************************************
					* 1. 작업예약(TB_YM_WRKBOOK) 생성
					**********************************************************/
					jrParam.setField("YD_WBOOK_ID"         , sYD_WBOOK_ID				); //작업ID
					jrParam.setField("YD_GP"               , YmConstant.YD_GP_2			); //야드구분
					jrParam.setField("YD_BAY_GP"           , sYD_SCH_CD.substring(1,2)	); //동구분
					jrParam.setField("YD_SCH_CD"           , sYD_SCH_CD					); //야드스케쥴코드
					jrParam.setField("YD_SCH_PRIOR"        , sYD_SCH_PRIOR				); //야드스케쥴우선순위
					jrParam.setField("YD_SCH_PROG_STAT"    , "W"						); //야드스케쥴진행상태(W:스케줄수행대기)
					jrParam.setField("YD_SCH_ST_GP"        , "A"						); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업)
					jrParam.setField("YD_SCH_REQ_GP"       , ""							); //야드To위치Guide
					jrParam.setField("YD_AIM_YD_GP"        , ""							);
					jrParam.setField("YD_AIM_BAY_GP"       , ""							);
					jrParam.setField("YD_TO_LOC_DCSN_MTD"  , ""							);
					jrParam.setField("YD_TO_LOC_GUIDE"     , ""							);
					jrParam.setField("YD_WRK_PLAN_TCAR"    , ""							);
					jrParam.setField("YD_CAR_USE_GP"       , ""							);
					jrParam.setField("TRN_EQP_CD"          , ""							);
					jrParam.setField("CAR_NO"              , ""							);
					jrParam.setField("CARD_NO"             , ""							);
					jrParam.setField("PTOP_PLNT_GP"        , ""							);
					jrParam.setField("DEST_TEL_NO"         , ""							);
					jrParam.setField("DIST_SHIPASSIGN_GP"  , ""							);
					jrParam.setField("YD_WRK_PLAN_CRN"     , ""							);
					
					commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insWrkBook", logId, methodNm, "작업예약(TB_YM_WRKBOOK) 생성");
				}
				
				/**********************************************************
				 * 2. 작업예약재료(TB_YM_WRKBOOKMTL) 생성
				 **********************************************************/
				jrParam.setField("YD_WBOOK_ID"		, sYD_WBOOK_ID												);
				jrParam.setField("STOCK_ID"			, commUtils.getValue(gdReq, "저장품", ii)					);	// 재료번호
				jrParam.setField("STACK_COL_GP"		, commUtils.getValue(gdReq, "FROM_LOC", ii).substring(0,6)	);
				jrParam.setField("STACK_BED_GP"		, commUtils.getValue(gdReq, "FROM_LOC", ii).substring(7,9)	);
				jrParam.setField("STACK_LAYER_GP"	, commUtils.getValue(gdReq, "FROM_LOC", ii).substring(10)	);
				jrParam.setField("YD_UP_COLL_SEQ"	, ""														);
				jrParam.setField("YD_ISPTOR"     	, ""														);
				jrParam.setField("YD_TAKE_OUT_DT"	, ""														);
				jrParam.setField("YD_TAKE_OUT_CD"	, ""														);
				
				//등록 할  레코드 수
				commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insYmWrkBookMtl", logId, methodNm, "작업예약재료(TB_YM_WRKBOOKMTL) 생성");
				
				/**********************************************************
				* 3-1. TB_YM_STOCK의 저장품 이동조건 등록
				**********************************************************/
				jrParam.setField("STOCK_ID"			, commUtils.getValue(gdReq, "저장품", ii)										);
				jrParam.setField("YD_AIM_RT_GP"		, bSlabComm.getStockMoveTerm(commUtils.getValue(gdReq, "저장품", ii)) 			); // 이동조건
				jrParam.setField("STOCK_MOVE_TERM"	, bSlabComm.getStockMoveTerm(commUtils.getValue(gdReq, "저장품", ii)) 			); // 이동조건
				
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockMoveTerm", logId, methodNm, "TB_YM_STOCK의 저장품 이동 조건 UPDATE");
				
				/**********************************************************
				* 3-2. 적치단 테이블(TB_YM_STACKLAYER)의 '적치상태'를 UPDATE
				* >> 기준변경 : 재료의 상태 값으로 적치상태를 조회하므로 따로 Set 할 필요 없음('S'코드 미사용 처리)
				**********************************************************/
				/*jrParam.setField("STACK_LAYER_STAT"	, YmConstant.STACK_LAYER_STAT_S 				); // 적치상태
				jrParam.setField("FROM_LOC"			, commUtils.getValue(gdReq, "FROM_LOC"	, ii)	);
				jrParam.setField("STOCK_ID"			, commUtils.getValue(gdReq, "저장품"	, ii)	);
				
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updateLayerState1", logId, methodNm, "TB_YM_STACKLAYER의 '적치상태'를 UPDATE");*/
				
				
			 
			
				//스케줄 메인 호출
				//JDTORecord jrCrnSchMsg = JDTORecordFactory.getInstance().create();
				//jrCrnSchMsg.setField("JMS_TC_CD"			, "YMYMJ202"); 
				//jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시				
				//jrCrnSchMsg.setField("YD_WBOOK_ID"  		, sYD_WBOOK_ID); //작업예약ID
				//jrCrnSchMsg.setField("YD_SCH_CD"  		, ""); //야드스케쥴코드
				//jrCrnSchMsg.setField("YD_EQP_ID"  		, ""); //야드설비ID
				//jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);		
				
			}
			
			//Slab Schedule EJB Call
			JDTORecord jrParam1		= JDTORecordFactory.getInstance().create();
			jrParam1.setField("JMS_TC_CD"    , "YMYMJ202"	); 
			jrParam1.setField("YD_WBOOK_ID"  , sYD_WBOOK_ID	); //야드작업예약ID
			jrParam1.setField("YD_SCH_CD"    , ""		  	); //야드스케쥴코드
			jrParam1.setField("YD_EQP_ID"    , ""  			); //야드설비ID
			
			EJBConnector ejbConn = new EJBConnector("default","BSlabSchSeEJB",this);
			jrRtn = (JDTORecord)ejbConn.trx("procYMYMJ202",new Class[]{JDTORecord.class},new Object[]{ jrParam1 });	
			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of scarfingWork
	
	/**
	 * [A] 오퍼레이션명 : 화면을 통해서 시편재 및 핸드스카핑 추출요구
	 * 작업예약을 생성한다.
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public JDTORecord scarfingOut(GridData gdReq) throws DAOException {
		
		/** As-Is 로직
			1.	야드 MAP정보를 체크
			2.	SLAB INFO 체크(조회만 하고 아무 작업하지 않음)
			 2-1. 시편재인 경우 체크 (미구현)
			 2-2. 핸드 스카핑재인 경우 체크(미구현)
			
			3. 스케쥴 코드 셋팅
			4. 작업예약 생성.
			5. 스케쥴 call
		**/
		
		// To-Be Start
		String methodNm = "화면을 통해서 시편재 및 핸드스카핑 추출요구[BSlabJspSeEJB.scarfingWork] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		JDTORecord sndRecord = JDTORecordFactory.getInstance().create();
		
		String sMessage = "추출 작업예약을 생성했습니다";
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
			JDTORecord jDrd = JDTORecordFactory.getInstance().create();
			
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			/**********************************************************
			* 1. 야드 MAP정보를 체크
			**********************************************************/
			JDTORecord jrParam		= JDTORecordFactory.getInstance().create();
			JDTORecord jrRtn 		= JDTORecordFactory.getInstance().create();
			JDTORecordSet rsResult  = null;
			JDTORecordSet rsResult2 = null;
			String sStockId = "";
			
			for (int ii = 0; ii < rowCnt; ii++) {
				// SLAB_NO(STOCK_ID)로 YM_적치단 Table조회
				jrParam.setField("STACK_COL_GP"	, commUtils.getValue(gdReq, "적치열", ii)		);
		 		jrParam.setField("STOCK_ID"		, commUtils.getValue(gdReq, "저장품", ii)		);
		 		/*
			 		SELECT *
			 		  FROM TB_YM_STACKLAYER
			 		 WHERE STACK_COL_GP = :STACK_COL_GP
			 		   AND   STOCK_ID	= :STOCK_ID 
		 		*/
		 		rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getStackLayerInfoWithStockId", logId, methodNm, "YM_적치단 정보를 체크"); 
				
				if(rsResult.size() < 1){
					sMessage = "시편재/핸드스카핑 추출=>저장품정보 존재안함.";
					throw new Exception(sMessage);
				}else{
					// 적치단 재료상태 확인
					String sLayerStat = StringHelper.evl(rsResult.getRecord(0).getFieldString("YD_STK_LYR_MTL_STAT"),""); // AsIs : STACK_LAYER_STAT
					
					if("U".equals(sLayerStat)){			// U : 권상대기
						sMessage = "=시편재/핸드스카핑 추출=>저장품이 스케쥴 등록 상태임.";
						throw new Exception(sMessage);
					}else if("D".equals(sLayerStat)){	// D : 권하대기
						sMessage = "=시편재/핸드스카핑 추출=>저장품이 스케쥴 등록 상태임.";
						throw new Exception(sMessage);
					}	
					
					// AsIs 소스
					/*if(YmCommonConst.STACK_LAYER_STAT_S.equals(sLayerStat)){
						sMessage = "=시편재/핸드스카핑 추출=>저장품이 작업예약이 존재함.";
						throw new Exception(sMessage);
					}else if(YmCommonConst.STACK_LAYER_STAT_U.equals(sLayerStat)){
						sMessage = "=시편재/핸드스카핑 추출=>저장품이 스케쥴 등록 상태임.";
						throw new Exception(sMessage);
					}else if(YmCommonConst.STACK_LAYER_STAT_P.equals(sLayerStat)){
						sMessage = "=시편재/핸드스카핑 추출=>저장품이 스케쥴 등록 상태임.";
						throw new Exception(sMessage);
					}*/		
				}
			}
			
			
			/**********************************************************
			* 2. SLAB INFO 체크
			**********************************************************/
			String sCurrProgCd			= "";
			String sReagentPickYn   	= "";
			String sReagentPickDoneYn  	= "";
			String sScarfingYn   		= "";
			String sScarfingDoneYn   	= "";
			String sScarfingPattern   	= "";
			
			rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.selectSlabMatirialInfo", logId, methodNm, "스케줄 기준 조회"); 
			
			commUtils.printLog(logId, "[스케줄 기준 조회건수 rsResult2]="+ rsResult2.size(), "[INFO]");
			
        	if(rsResult2.size() > 0){
	    		sCurrProgCd	   		= StringHelper.evl(rsResult2.getFieldString("CURR_PROG_CD"), "");
	    		sReagentPickYn   	= StringHelper.evl(rsResult2.getFieldString("REAGENT_PICK_TARGET_YN"), "");
	    		sReagentPickDoneYn  = StringHelper.evl(rsResult2.getFieldString("REAGENTPICK_DONE_YN"), "");
	    		sScarfingYn   		= StringHelper.evl(rsResult2.getFieldString("SCARFING_YN"), "");
	    		sScarfingDoneYn   	= StringHelper.evl(rsResult2.getFieldString("SCARFING_DONE_YN"), "");
	    		sScarfingPattern   	= StringHelper.evl(rsResult2.getFieldString("SCARFING_PATTERN"), "");
	    		
	    		
	    		commUtils.printLog(logId, "[프로그램 구분(H:핸드스카핑/S:시편) p_gbn]="+ gdReq.getParam("p_gbn"), "[INFO]");
	    		
	    		/*
	        	 *	2-1. 시편재인 경우 체크
	        	 */
	        	if("S".equals(gdReq.getParam("p_gbn"))){
	            /*
	        	 *	2-2. 핸드 스카핑재인 경우 체크
	        	 */	
	            }else if("H".equals(gdReq.getParam("p_gbn"))){
	            	// 추후 처리 로직 반영
	            }
	        	
	    	}
        	
			
			
			// 작업예약ID생성
        	// 스카핑 작업 스케쥴 기준 변동 E동 핸드스카핑 >> E동 동내이적(3번크레인)으로 변경
			String sYD_WBOOK_ID = commDao.getSeqId(logId, methodNm, "WrkBook");
			
			String sYD_SCH_CD	= "";	//  슬라브 스카핑 보급 스케쥴 코드
			//String sYD_SCH_CD	= "2EYD31MM";	//  슬라브 스카핑 보급 스케쥴 코드 : E동 동내이적(3)
			//String sYD_SCH_CD	= "2ESE01LM";	//  슬라브 스카핑 보급 스케쥴 코드 : E동 Scarfing 추출
			
		    jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
	 		jrParam.setResultMsg(methodNm);	//상위 Method 명
		    
			
	 		// 스케줄코드로 스케줄기준Table조회
	 		jrParam.setField("YD_SCH_CD", sYD_SCH_CD);
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule  
			SELECT YD_SCH_CD
			     , YD_WRK_CRN
			     , YD_WRK_CRN_PRIOR
			  FROM TB_YM_SCHEDULERULE
			 WHERE DEL_YN = 'N'
			   AND YD_SCH_CD = :V_YD_SCH_CD
			*/   
			rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
			
			String	sYD_SCH_PRIOR = "";
			if (rsResult2.size() > 0){
				sYD_SCH_PRIOR = rsResult2.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
			}
			
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				if(ii == 0){	// 하나의 작업ID에 여러개의 재료를 포함함
					
					// 스카핑 패턴 G, S는 핸드스카핑, 그 외는 머신스카핑 (SP 시편은 안들어오므로 제외함)
					if("G".equals(commUtils.getValue(gdReq, "SCF패턴", ii)) || "S".equals(commUtils.getValue(gdReq, "SCF패턴", ii))){
						sYD_SCH_CD = "2EYD31MM"; // 핸드스카핑 (3번크레인 동내이적)	>> 핸드스카핑은 스케쥴 내에서 진행상태에 따라 보급/추출 분리작업하므로 스케쥴 코드가 같음
					}else{
						sYD_SCH_CD = "2ESE01LM"; // 머신스카핑 (E동 Scarfing 추출)
					}
					
					/**********************************************************
					* 3. 작업예약 생성.
					**********************************************************/
					jrParam.setField("YD_WBOOK_ID"         , sYD_WBOOK_ID				); //작업ID
					jrParam.setField("YD_GP"               , YmConstant.YD_GP_2			); //야드구분
					jrParam.setField("YD_BAY_GP"           , sYD_SCH_CD.substring(1,2)	); //동구분
					jrParam.setField("YD_SCH_CD"           , sYD_SCH_CD					); //야드스케쥴코드
					jrParam.setField("YD_SCH_PRIOR"        , sYD_SCH_PRIOR				); //야드스케쥴우선순위
					jrParam.setField("YD_SCH_PROG_STAT"    , "W"						); //야드스케쥴진행상태(W:스케줄수행대기)
					jrParam.setField("YD_SCH_ST_GP"        , "A"						); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업)
					jrParam.setField("YD_SCH_REQ_GP"       , ""							); //야드To위치Guide
					jrParam.setField("YD_AIM_YD_GP"        , ""							);
					jrParam.setField("YD_AIM_BAY_GP"       , ""							);
					jrParam.setField("YD_TO_LOC_DCSN_MTD"  , ""							);
					jrParam.setField("YD_TO_LOC_GUIDE"     , ""							);
					jrParam.setField("YD_WRK_PLAN_TCAR"    , ""							);
					jrParam.setField("YD_CAR_USE_GP"       , ""							);
					jrParam.setField("TRN_EQP_CD"          , ""							);
					jrParam.setField("CAR_NO"              , ""							);
					jrParam.setField("CARD_NO"             , ""							);
					jrParam.setField("PTOP_PLNT_GP"        , ""							);
					jrParam.setField("DEST_TEL_NO"         , ""							);
					jrParam.setField("DIST_SHIPASSIGN_GP"  , ""							);
					jrParam.setField("YD_WRK_PLAN_CRN"     , ""							);
					
					commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insWrkBook", logId, methodNm, "작업예약(TB_YM_WRKBOOK) 생성");
					
				}
				/**********************************************************
				 * 3-1. 작업예약재료(TB_YM_WRKBOOKMTL) 생성
				 **********************************************************/
				jrParam.setField("YD_WBOOK_ID"		, sYD_WBOOK_ID												);
				jrParam.setField("STOCK_ID"			, commUtils.getValue(gdReq, "저장품", ii)					);	// 재료번호
				jrParam.setField("STACK_COL_GP"		, commUtils.getValue(gdReq, "FROM_LOC", ii).substring(0,6)	);
				jrParam.setField("STACK_BED_GP"		, commUtils.getValue(gdReq, "FROM_LOC", ii).substring(7,9)	);
				jrParam.setField("STACK_LAYER_GP"	, commUtils.getValue(gdReq, "FROM_LOC", ii).substring(10)	);
				jrParam.setField("YD_UP_COLL_SEQ"	, ""														);
				jrParam.setField("YD_ISPTOR"     	, ""														);
				jrParam.setField("YD_TAKE_OUT_DT"	, ""														);
				jrParam.setField("YD_TAKE_OUT_CD"	, ""														);
				
				//등록 할  레코드 수
				commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insYmWrkBookMtl", logId, methodNm, "작업예약재료(TB_YM_WRKBOOKMTL) 생성");
				
				/**********************************************************
				* 3-2. TB_YM_STOCK의 저장품 이동조건 등록
				**********************************************************/
				jrParam.setField("STOCK_ID"			, commUtils.getValue(gdReq, "저장품", ii)										);
				jrParam.setField("YD_AIM_RT_GP"		, bSlabComm.getStockMoveTerm(commUtils.getValue(gdReq, "저장품", ii)) 			); // 이동조건
				jrParam.setField("STOCK_MOVE_TERM"	, bSlabComm.getStockMoveTerm(commUtils.getValue(gdReq, "저장품", ii)) 			); // 이동조건
				
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockMoveTerm", logId, methodNm, "TB_YM_STOCK의 저장품 이동 조건 UPDATE");
				
				/**********************************************************
				* 3-2. 적치단 테이블(TB_YM_STACKLAYER)의 '적치상태'를 UPDATE
				* >> 기준변경 : 재료의 상태 값으로 적치상태를 조회하므로 따로 Set 할 필요 없음('S'코드 미사용 처리)
				**********************************************************/
				/*jrParam.setField("STACK_LAYER_STAT"	, YmConstant.STACK_LAYER_STAT_S 				); // 적치상태
				jrParam.setField("FROM_LOC"			, commUtils.getValue(gdReq, "FROM_LOC"	, ii)	);
				jrParam.setField("STOCK_ID"			, commUtils.getValue(gdReq, "저장품"	, ii)	);
				
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updateLayerState1", logId, methodNm, "TB_YM_STACKLAYER의 '적치상태'를 UPDATE");*/

			}
			
			
			/**********************************************************
			* 4. 스케쥴 호출
			**********************************************************/
			
			//스케줄 메인 호출
			//JDTORecord jrCrnSchMsg = JDTORecordFactory.getInstance().create();
			//jrCrnSchMsg.setField("JMS_TC_CD"			, "YMYMJ202"); 
			//jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시				
			//jrCrnSchMsg.setField("YD_WBOOK_ID"  		, sYD_WBOOK_ID); //작업예약ID
			//jrCrnSchMsg.setField("YD_SCH_CD"  		, ""); //야드스케쥴코드
			//jrCrnSchMsg.setField("YD_EQP_ID"  		, ""); //야드설비ID
			//jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);		
			
			//Slab Schedule EJB Call
			JDTORecord jrParam1		= JDTORecordFactory.getInstance().create();
			jrParam1.setField("JMS_TC_CD"    , "YMYMJ202"	); 
			jrParam1.setField("YD_WBOOK_ID"  , sYD_WBOOK_ID	); //야드작업예약ID
			jrParam1.setField("YD_SCH_CD"    , ""		  	); //야드스케쥴코드
			jrParam1.setField("YD_EQP_ID"    , ""  			); //야드설비ID
			
			
			EJBConnector ejbConn = new EJBConnector("default","BSlabSchSeEJB",this);
			jrRtn = (JDTORecord)ejbConn.trx("procYMYMJ202",new Class[]{JDTORecord.class},new Object[]{ jrParam1 });	
			
			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of scarfingWork
	
	/**
	 * [A] 오퍼레이션명 : SCARFING 대상재조회 - 지연사유등록
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public void updYd_SlabScarfDelyReg(GridData gdReq) throws DAOException {
		
		// To-Be Start
		String methodNm = "SCARFING 대상재조회 - 지연사유등록[BSlabJspSeEJB.updYd_SlabScarfDelyReg] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//수정할 레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			/**********************************************************
			*  스카핑대상재 지연사유등록처리
			**********************************************************/
			JDTORecord jrParam		= JDTORecordFactory.getInstance().create();
			
			for (int ii = 0; ii < rowCnt; ii++) {	
				/*
					-- com.inisteel.cim.ym.bslab.dao.BSlabDAO.updYd_SlabScarfDelyReg
					-- 스카핑대상재 지연사유등록
					UPDATE USRPMA.TB_PM_B_SLABSCARFINGUGNTASGN A
					SET SCARF_DELY_REGISTER = :V_SCARF_DELY_REGISTER
					  , SCARF_DELY_CNTS     = :V_SCARF_DELY_REG_CNTS
					  , SCARF_DELY_REG_DATE = SYSDATE
					  , MODIFIER            = 'ydsystem'
					  , MOD_DDTT            = SYSDATE
					WHERE STL_NO            = :V_STL_NO
					  AND STEP_NO           = :V_STEP_NO
				 * */
				
				jrParam.setField("SCARF_DELY_REGISTER"	, gdReq.getParam("SCARF_DELY_REGISTER")			);	// 요청자사번
				jrParam.setField("SCARF_DELY_REG_CNTS"	, gdReq.getParam("SCARF_DELY_REG_CNTS") 		);	// 지연내용
				jrParam.setField("STL_NO"				, commUtils.getValue(gdReq, "저장품" , ii)		);	// 재료번호
				jrParam.setField("STEP_NO"				, "1"											);	// 차수(As-Is에 "1"로 하드코딩 되어있음)
				//jrParam.setField("STEP_NO"	, commUtils.getValue(gdReq, "STEP_NO"				, ii)	);
				
				//등록 할  레코드 수
				commDao.insert(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updYd_SlabScarfDelyReg", logId, methodNm, "스카핑대상재 지연사유등록 - USRPMA.TB_PM_B_SLABSCARFINGUGNTASGN");
				
			}
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of updYd_SlabScarfDelyReg
	
	/**
	 *      [A] 오퍼레이션명 : STE 비상보급 실행
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord runSteEmgSup(GridData gdReq) throws DAOException {
		String methodNm = "STE 비상보급 실행 [BSlabJspSeEJB.runSteEmgSup] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//Return Value
			JDTORecord jrRtn = null;
			String currDate   = commUtils.getDateTime14();	//현재시각
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			

			//W/B 05번 번지 최상단 Slab 정보를 작업예약한다.
			String sYD_SCH_CD = "2CHB02UM"; //C동 STE 비상보급
			String sYD_TO_LOC_GUIDE = "2CCT040101"; //sYD_TO_LOC_GUIDE = "2CCT040101";
			
			
			//CTC4 고장시 비상보급 TO위치 L4 테이블 (2CLT04) 지정 여부
			String sAPP100_ZY001_YN = ymComm.BCoilApplyYn("APP100","2","ZY001_YN");
			
			if("Y".equals(sAPP100_ZY001_YN)) {
				
				//CTC#4 고장여부 확인
				jrParam.setField("EQUIP_GP"  , "2CCT04"); 
				
				JDTORecordSet rsCtc4Stat = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.selectEquipInfo", logId, methodNm, "CTC#4 고장여부 확인");

				if(rsCtc4Stat.size() > 0) {
					if("B".equals(rsCtc4Stat.getRecord(0).getFieldString("WPROG_STAT"))) {
						commUtils.printLog(logId, "=======:::: CTC#4 고장 = TO위치가이드를 2CLT04 로 변경  " , "SL");
						sYD_TO_LOC_GUIDE = "2CLT040101"; //CTC#4 고장일 경우 TO위치 L4 테이블
					}
				}
			}
			
			//선작업지시 존재여부 체크
			jrParam.setField("YD_SCH_CD" 		, sYD_SCH_CD); 
			jrParam.setField("YD_CRN_SCH_ID"	, "1");
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getPreWrkCrnSch
			--권상,권하시 해당 크레인스케줄 id를 제외한 동일 스케줄 작업이 만들어져 있는지 조회
			SELECT CS.YD_CRN_SCH_ID
			      ,CS.YD_SCH_CD
			      ,CS.YD_UP_WO_LOC
			      ,CS.YD_DN_WO_LOC
			      ,CM.STOCK_ID
			      ,CM.YD_AID_WRK_YN
			FROM   TB_YM_CRNSCH CS
			      ,TB_YM_CRNWRKMTL CM
			WHERE  CS.DEL_YN = 'N'
			  AND  CM.DEL_YN = 'N'
			  AND  CS.YD_SCH_CD LIKE :V_YD_SCH_CD ||'%'
			  AND  CS.YD_CRN_SCH_ID NOT IN (NVL(:V_YD_CRN_SCH_ID,'1'))
			  AND  CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
			  AND  CM.YD_AID_WRK_YN = 'N'
			 */
			JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getPreWrkCrnSch", logId, methodNm, "선작업지시 존재여부 체크");
			
			if(rsResult.size() == 0) {
			
				//--------------------------------------------------------------------------------
				//C동 STE 비상보급  시작
				//--------------------------------------------------------------------------------
				//적치단 테이블에서 W/B 05Bed 정보를 읽어 온다.
				jrParam.setField("STACK_COL_GP"		, "2CWB01"); 
				jrParam.setField("STACK_BED_GP"		, "05");
				/*
				SELECT *
				  FROM TB_YM_STACKLAYER
				 WHERE STACK_COL_GP	= :V_STACK_COL_GP
				   AND STACK_BED_GP	= :V_STACK_BED_GP 
				   AND STOCK_ID IS NOT NULL
				 ORDER BY STACK_LAYER_GP DESC
				 */
				JDTORecordSet rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getStackLayerInfoWithBedL", logId, methodNm, "W/B 05Bed 조회 ");

				boolean bFLAG = true;
				String sSTOCK_ID = "";
				String sSTACK_LAYER_STAT = "";
				for (int ii = 0; ii <  rsResult2.size(); ++ii ) {
					sSTOCK_ID         = rsResult2.getRecord(ii).getFieldString("STOCK_ID");
					sSTACK_LAYER_STAT = rsResult2.getRecord(ii).getFieldString("STACK_LAYER_STAT");
				}
				
				if ("".equals(sSTOCK_ID)) {
					//"STE고장 비상스케쥴 = W/B 5번 번지 저장품정보 존재안함");
					commUtils.printLog(logId, "=======:::: STE고장 비상스케쥴 = W/B 5번 번지 저장품정보 존재안함. " , "SL");
					throw new Exception("STE고장 비상스케쥴 = W/B 5번 번지 저장품정보 존재안함. ");
				}
				
				if (!"C".equals(sSTACK_LAYER_STAT)) {
					//"STE고장 비상스케쥴 = W/B 5번 번지 저장품정보 작업등록상태 ="+sLayerStat);
					commUtils.printLog(logId, "=======:::: STE고장 비상스케쥴 = W/B 5번 번지 저장품정보 작업등록상태 = " + sSTACK_LAYER_STAT, "SL");
					throw new Exception("STE고장 비상스케쥴 = W/B 5번 번지 저장품정보 작업등록상태 ");
				}
			
				if (bFLAG) {
					//CTC4 트랙킹 관리가 안되기 때문에 크레인 작업지시 생성 전에 Clear
					jrParam.setField("STACK_COL_GP"		, "2CCT04"); 
					jrParam.setField("STACK_BED_GP"		, "01");
					jrParam.setField("STACK_LAYER_GP"	, "01");
					jrParam.setField("STACK_LAYER_STAT" , "E");
					jrParam.setField("STOCK_ID"         , "");
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
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateCraneStackLayerStat", logId, methodNm, "CTC4 Clear");
					
					
					
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
					JDTORecordSet rsResult3 = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdSchrule", logId, methodNm, "스케줄 기준 조회"); 
					
					if(rsResult3.size() > 0) {
						String sYD_SCH_PRIOR = rsResult3.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
						
						//작업예약ID생성
						String sYD_WBOOK_ID = commDao.getSeqId(logId, methodNm, "WrkBook");
					
						/**********************************************************
						* 2-1. 작업예약(TB_YM_WRKBOOK) 생성
						**********************************************************/
						jrParam.setField("YD_WBOOK_ID"		, sYD_WBOOK_ID);
						jrParam.setField("YD_GP"			, YmConstant.YD_GP_2);
						jrParam.setField("YD_BAY_GP"		, sYD_SCH_CD.substring(1,2));
						jrParam.setField("YD_SCH_CD"		, sYD_SCH_CD); //야드스케쥴코드
						jrParam.setField("YD_SCH_PRIOR"		, sYD_SCH_PRIOR); //야드스케쥴우선순위
						jrParam.setField("YD_SCH_PROG_STAT"	, "W"); //야드스케쥴진행상태(W:스케줄수행대기)
						jrParam.setField("YD_SCH_ST_GP"		, "A"); //야드스케쥴기동구분(A:자동, B:작업자BackUp, M:Manual작업)
						jrParam.setField("YD_TO_LOC_GUIDE"	, sYD_TO_LOC_GUIDE); //야드To위치Guide
						jrParam.setField("YD_TO_LOC_DCSN_MTD", "S"); //야드TO위치결정방법
						
						commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insWrkBook", logId, methodNm, "작업예약(TB_YM_WRKBOOK) 생성");
						
						/**********************************************************
						* 2-2. 작업예약재료(TB_YM_WRKBOOKMTL) 생성
						**********************************************************/
						jrParam.setField("YD_WBOOK_ID"		, sYD_WBOOK_ID);
						jrParam.setField("STOCK_ID"			, commUtils.trim(rsResult2.getRecord(0).getFieldString("STOCK_ID")));
						jrParam.setField("STACK_COL_GP"		, commUtils.trim(rsResult2.getRecord(0).getFieldString("STACK_COL_GP")));
						jrParam.setField("STACK_BED_GP"		, commUtils.trim(rsResult2.getRecord(0).getFieldString("STACK_BED_GP")));
						jrParam.setField("STACK_LAYER_GP"	, commUtils.trim(rsResult2.getRecord(0).getFieldString("STACK_LAYER_GP")));
						
						commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insYmWrkBookMtl", logId, methodNm, "작업예약재료(TB_YM_WRKBOOKMTL) 생성");
						
						/**********************************************************
						* 2-3. TB_YM_STOCK의 저장품 이동조건 등록
						**********************************************************/
						jrParam.setField("STOCK_ID"			, commUtils.trim(rsResult2.getRecord(0).getFieldString("STOCK_ID")));
						jrParam.setField("STOCK_MOVE_TERM"	, YmConstant.NEW_STOCK_MOVE_TERM_FS ); //압연작업대기
						
						commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockMoveTerm", logId, methodNm, "TB_YM_STOCK의 저장품 이동 조건 UPDATE");
						
						//스케줄 메인 호출
						JDTORecord jrCrnSchMsg = JDTORecordFactory.getInstance().create();
						jrCrnSchMsg.setField("JMS_TC_CD"			, "YMYMJ202"); 
						jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시				
						jrCrnSchMsg.setField("YD_WBOOK_ID"  		, sYD_WBOOK_ID); //작업예약ID
						jrCrnSchMsg.setField("YD_SCH_CD"  		, ""); //야드스케쥴코드
						jrCrnSchMsg.setField("YD_EQP_ID"  		, ""); //야드설비ID
						jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);		
					}
				} //bFLAG
			} else {
				commUtils.printLog(logId, "=======:::: C동 STE 비상보급 스페줄이 이미 존재합니다. " , "SL");					
				throw new Exception("C동 STE 비상보급 스페줄이 이미 존재합니다. ");
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of runSteEmgSup
	/**
	 * 대차형상관리여부 설정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updYdTcarFormRule(GridData gdReq) throws DAOException {
		String methodNm = "대차형상관리여부 설정[BSlabJspSeEJB.updYdTcarFormRule] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try { 
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			jrParam.setField("DTL_ITM1"	, gdReq.getParam("DTL_ITM1") );
			jrParam.setField("ITEM"		, gdReq.getParam("ITEM") );
	
			/*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdTcarFormRule
			UPDATE  USRYMA.TB_YM_RULE
			SET DTL_ITM1  = :V_DTL_ITM1
			    ,MODIFIER = :V_MODIFIER
			    ,MOD_DDTT = sysdate
			 WHERE 1=1
			  AND REPR_CD_GP ='YM2006' 
			  AND CD_GP      ='2'
			  AND DEL_YN     = 'N'
		      AND ITEM       = :V_ITEM */
			
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdTcarFormRule", logId, methodNm, "스케줄 기준 야드멀티작업여부 설정");
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updYdTcarFormRule		
	/**
	 * 적치기준 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updStockRule(GridData gdReq) throws DAOException {
		String methodNm = "적치기준 변경[BSlabJspFaEJB.updStockRule] < " + gdReq.getNavigateValue();
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
				
				jrParam.setField("DTL_ITM1"	,commUtils.getValue(gdReq, "DTL_ITM1", ii)); 
				jrParam.setField("DTL_ITM2"	,commUtils.getValue(gdReq, "DTL_ITM2", ii)); 	
				jrParam.setField("DTL_ITM3"	,commUtils.getValue(gdReq, "DTL_ITM3", ii)); 	
				jrParam.setField("DEL_YN"	,commUtils.getValue(gdReq, "DEL_YN", ii)); 				
				jrParam.setField("CD_GP"	,commUtils.getValue(gdReq, "CD_GP", ii)); 
				jrParam.setField("ITEM"	    ,commUtils.getValue(gdReq, "ITEM", ii)); 
				
				commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockRule", logId, methodNm, "적치기준 변경");
								
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updStockRule		
	
	
	
	/**
	 * Take Out 장입순번 복구 설정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updYdTakeOutRecvRule(GridData gdReq) throws DAOException {
		String methodNm = "Take Out 장입순번 복구 여부설정[BSlabJspSeEJB.updYdTakeOutRecvRule] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try { 
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			jrParam.setField("DTL_ITM1"	, gdReq.getParam("DTL_ITM1") );
			jrParam.setField("ITEM"		, gdReq.getParam("ITEM") );
	
			/*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdTakeOutRecvRule
			UPDATE  USRYMA.TB_YM_RULE
			SET DTL_ITM1  = :V_DTL_ITM1
			    ,MODIFIER = :V_MODIFIER
			    ,MOD_DDTT = sysdate
			 WHERE 1=1
			  AND REPR_CD_GP ='YM3002' 
			  AND CD_GP      ='2'
			  AND DEL_YN     = 'N'
		      AND ITEM       = :V_ITEM */
			
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdTakeOutRecvRule", logId, methodNm, "스케줄 기준 야드멀티작업여부 설정");
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updYdTakeOutRecvRule	
	
	/**
	 * 모음작업 처리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord procGatherWork(GridData gdReq) throws DAOException {
		String methodNm = "모음작업 처리[BSlabJspSeEJB.procGatherWork] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try { 
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			//jrParam.setField("DTL_ITM1"	, gdReq.getParam("DTL_ITM1") );
			//jrParam.setField("ITEM"		, gdReq.getParam("ITEM") );
	
			/*com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdTakeOutRecvRule
			UPDATE  USRYMA.TB_YM_RULE
			SET DTL_ITM1  = :V_DTL_ITM1
			    ,MODIFIER = :V_MODIFIER
			    ,MOD_DDTT = sysdate
			 WHERE 1=1
			  AND REPR_CD_GP ='YM3002' 
			  AND CD_GP      ='2'
			  AND DEL_YN     = 'N'
		      AND ITEM       = :V_ITEM */
			
			//commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdTakeOutRecvRule", logId, methodNm, "스케줄 기준 야드멀티작업여부 설정");
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of procGatherWork	
	
	
	/**
	 * 이상정보정리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord delAbnomalInfo(GridData gdReq) throws DAOException {
		String methodNm = "이상정보정리[BSlabJspSeEJB.delAbnomalInfo] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			JDTORecordSet rsResult = null;
			String sDEL_YN = "";
			String sYD_CRN_SCH_ID_YN = "";
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			//등록 할  레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			int iDelCnt = 0;
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				if(!"".equals(commUtils.getValue(gdReq, "YD_WBOOK_ID", ii))) {
					//TB_YM_WRKBOOK 값은 삭제되었는데 TB_YM_WRKBOOKMTL 값이 남아 있는 정보 이상재 처리
					jrParam.setField("YD_WBOOK_ID"	, commUtils.getValue(gdReq, "YD_WBOOK_ID", ii));
					
					//작업예약 테이블에 작업예약이 존재하는지 체크 
					rsResult = commDao.select(jrParam,"com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYmWrkbook", logId, methodNm, "::::: 작업예약 테이블에 작업예약이 존재하는지 체크 :::::"); 
					
					if (rsResult.size() > 0) {
						//존재하면 DEL_YN = 'N' 인지 확인한다.
						sDEL_YN = commUtils.nvl(rsResult.getRecord(0).getFieldString("DEL_YN"),"");
						
						if("Y".equals(sDEL_YN)) {
							//작업예약재료 테이블에 DEL_YN = 'Y'로 UPDATE
							commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updDelYnWrkBookMtl", logId, methodNm, "::::1 작업예약이 DEL_YN='Y'임으로 작업예약재료 삭제 :::::");
							iDelCnt++;
						}
	
					} else {
					
						//작업예약재료 테이블에 DEL_YN = 'Y'로 UPDATE
						commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updDelYnWrkBookMtl", logId, methodNm, "::::2 작업예약이 존재하지 않음으로 작업예약재료 삭제 :::::");
						iDelCnt++;
					}
				}
				
				if(!"".equals(commUtils.getValue(gdReq, "STOCK_ID", ii))) {
					//크레인 스케줄이 존재하지 않는데 적치단에 'U' 나 'D'로 잡혀 있는 정보 이상재 처리
					jrParam.setField("STOCK_ID"	, commUtils.getValue(gdReq, "STOCK_ID", ii));
					
					//크레인스케줄 테이블에  존재하는지 체크 
					rsResult = commDao.select(jrParam,"com.inisteel.cim.ym.bslab.dao.BSlabDAO.getStockLoc", logId, methodNm, "::::: 작업예약 테이블에 작업예약이 존재하는지 체크 :::::"); 
					
					if (rsResult.size() > 0) {
						//크레인스케줄이 존재하지 않는다면 'N'
						sYD_CRN_SCH_ID_YN = commUtils.nvl(rsResult.getRecord(0).getFieldString("YD_CRN_SCH_ID_YN"),"");
						
						if("N".equals(sYD_CRN_SCH_ID_YN)) {
							
							commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLayerU2CByStockId", logId, methodNm, "::::3 크레인 스케줄이 존재하지 않는데 적치단에 'U'로 잡혀 있는 정보 이상재 처리 :::::");
							
							commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLayerD2EByStockId", logId, methodNm, "::::4 크레인 스케줄이 존재하지 않는데 적치단에 'D'로 잡혀 있는 정보 이상재 처리 :::::");
							
							iDelCnt++;
						}
					}
				}
			}
			
			if(iDelCnt>0) {
				jrRtn.setField("RTN_MSG", "이상정보정리 " + iDelCnt + " 건이 정상 처리되었습니다!");	
				
			} else {
				jrRtn.setField("RTN_MSG", "이상정보정리 대상이 없습니다!!");	
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of delAbnomalInfo
	
	
	//2018년 1월 15일 (월) - Scarfing 입출측 조회 화면 
	//Scarfing M/C상태 변경
	/**
	 * Scarfing M/C상태 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updbSlabMcState(GridData gdReq) throws DAOException {
		String methodNm = "Scarfing입출측 조회 - Scarfing M/C상태 변경[BSlabJspSeEJB.updbSlabMcState] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
   
			jrParam.setField("STACK_COL_GP"    , gdReq.getParam("STACK_COL_GP"));     
			jrParam.setField("YD_CAR_USE_GP"    , gdReq.getParam("YD_CAR_USE_GP"));
				
			/* UPDATE TB_YM_STACKCOL
				  SET YD_CAR_USE_GP = :V_YD_CAR_USE_GP,
			      	  MODIFIER = :V_MODIFIER,
					  MOD_DDTT = SYSDATE
				WHERE STACK_COL_GP = :V_STACK_COL_GP  
				  AND YD_GP = '2'
				  AND DEL_YN = 'N'
			*/
				
			commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updbSlabMcState", logId, methodNm, "Scarfing M/C상태 변경");
						
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updbSlabMcState
	
	//Scarfing입출측 조회 - 보급/추출 크레인 변경
	/**
	 * 보급/추출 크레인 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updSlabScarfingSupp(GridData gdReq) throws DAOException {
		String methodNm = "Scarfing입출측 조회 - 보급/추출 크레인 변경[BSlabJspSeEJB.updSlabScarfingSupp] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
   
			jrParam.setField("YD_WRK_CRN"    , gdReq.getParam("YD_WRK_CRN"));     
			jrParam.setField("YD_SCH_CD"    , gdReq.getParam("YD_SCH_CD"));
				
			/* UPDATE TB_YM_SCHEDULERULE
				  SET YD_WRK_CRN = :V_YD_WRK_CRN,
			      	  MODIFIER = :V_MODIFIER,
					  MOD_DDTT = SYSDATE
				WHERE YD_SCH_CD = :V_YD_SCH_CD   
				  AND YD_GP = '2'
				  AND DEL_YN = 'N'
			*/
				
			commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updSlabScarfingSupp", logId, methodNm, "E동 보급/추출 크레인 변경");
						
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updSlabScarfingSupp
	
	
	//SCARFING 실적조회 수정
	/**
	 * SCARFING 실적조회 - 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updSlabScarfingPattern(GridData gdReq) throws DAOException {
		String methodNm = "스카핑결과 실적패턴 수정[BSlabJspSeEJB.updSlabScarfingPattern] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
   
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				jrParam.setField("SCARFING_PATTERN"    		   , commUtils.getValue(gdReq, "SCARFING_PATTERN"			, ii));       
				jrParam.setField("SCARFING_PATTERN_WRSLT"      , commUtils.getValue(gdReq, "SCARFING_PATTERN_COMM"      , ii));       
				jrParam.setField("SLAB_NO"     				   , commUtils.getValue(gdReq, "SLAB_NO"      				, ii));       
				jrParam.setField("STEP_NO"      			   , commUtils.getValue(gdReq, "STEP_NO"      				, ii));   
				
			/*  UPDATE TB_PO_SCARFINGWRSLT
				   SET SCARFING_PATTERN = :V_SCARFING_PATTERN
                      ,SCARFING_PATTERN_WRSLT = :V_SCARFING_PATTERN_WRSLT
                 WHERE SLAB_NO = :V_SLAB_NO
                   AND STEP_NO = :V_STEP_NO
			*/
				
			}
			commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updSlabScarfingPattern", logId, methodNm, "스카핑결과 실적패턴 수정");
						
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updSlabScarfingPattern
	
	
	
	//SCARFING 근조/본수/중량 입력
	/**
	 * SCARFING 근조/본수/중량 입력
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updSlabScarfingQtyWt(GridData gdReq) throws DAOException {
		String methodNm = "스카핑결과 실적패턴 수정[BSlabJspSeEJB.updSlabScarfingQtyWt] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			String sFROM_DATES     = gdReq.getParam("FROM_DATES");
			String sINSPECT_DUTY     = gdReq.getParam("INSPECT_DUTY");
			String sSLAB_LEN     = gdReq.getParam("SLAB_LEN");
			String sSLAB_WT     = gdReq.getParam("SLAB_WT");
			
			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			jrParam.setField("INSPECT_DATE"  , sFROM_DATES);
			jrParam.setField("INSPECT_DUTY"  , sINSPECT_DUTY);
			jrParam.setField("SLAB_LEN"  , sSLAB_LEN);
			jrParam.setField("SLAB_WT"  , sSLAB_WT);
			
			/*  MERGE INTO TB_YM_HDSCARFINGWRSLT USING DUAL ON ( 
					       INSPECT_DATE = :V_INSPECT_DATE
					   AND INSPECT_DUTY = :V_INSPECT_DUTY)
				 WHEN MATCHED THEN
					   UPDATE
					      SET SLAB_LEN  = :V_SLAB_LEN, 
					          SLAB_WT   = :V_SLAB_WT,
					          MODIFIER  = :V_MODIFIER,
					          MOD_DDTT  = SYSDATE
				 WHEN NOT MATCHED THEN
					       INSERT (
					               INSPECT_DATE,
					               INSPECT_DUTY,
					               SLAB_LEN,
					               SLAB_WT,
					               REGISTER,
					               REG_DDTT)
					       VALUES (
					               :V_INSPECT_DATE 
					              ,:V_INSPECT_DUTY 
					              ,:V_SLAB_LEN
					              ,:V_SLAB_WT
					              ,:V_MODIFIE,
					              ,SYSDATE) */
		
			
			commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updSlabScarfingQtyWt", logId, methodNm, "근조/본수/중량 입력");
						
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updSlabScarfingQtyWt
	
	//SCARFING 정지실적등록 수정
	/**
	 * SCARFING 정지실적등록 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updSlabScarfingStopWrReg(GridData gdReq) throws DAOException {
		String methodNm = "SCARFING 정지실적등록 수정[BSlabJspSeEJB.updSlabScarfingStopWrReg] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
   
			String sCLASS1_CD     = gdReq.getParam("CLASS1_CD");	
			String sCLASS2_CD     = gdReq.getParam("CLASS2_CD");		
			String sREMARK     = gdReq.getParam("REMARK");
			String sSLAB_NO     = gdReq.getParam("SLAB_NO");
			String sSTEP_NO     = gdReq.getParam("STEP_NO");
			
			jrParam.setField("SCARFING_DELAY_GP"    		   , sCLASS1_CD);       
			jrParam.setField("SCARFING_DERAY_DTL"      		   , sCLASS2_CD);       
			jrParam.setField("REMARK"     			   , sREMARK);          
			jrParam.setField("SLAB_NO"     			   , sSLAB_NO);          
			jrParam.setField("STEP_NO"     			   , sSTEP_NO);          
				
			/*  UPDATE TB_PO_SCARFINGWRSLT
				   SET SCARFING_DELAY_GP = :V_SCARFING_DELAY_GP
				      ,SCARFING_DERAY_DTL = :V_SCARFING_DERAY_DTL
				      ,REMARK = :V_REMARK
				      ,MODIFIER = :V_MODIFIER
				      ,MOD_DDTT = SYSDATE 
				 WHERE SLAB_NO = :V_SLAB_NO 
				   AND STEP_NO = :V_STEP_NO
			*/
				
			commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updSlabScarfingStopWrReg", logId, methodNm, "SCARFING 정지실적등록 수정");
						
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updSlabScarfingPattern
	
	/**
	 *      [A] 오퍼레이션명 : 차량위치 초기화
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord initCarLoc(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "차량위치 초기화[BSlabJspSeEJB.initCarLoc] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			String modifier  	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"   )); //수정자
			String ydStkColGp	= commUtils.trim(rcvMsg.getFieldString("YD_STK_COL_GP")); //적치열(차량위치)
			String ydWbookId 	= null;
			String ydSchCd   	= null;
			
			if ("".equals(ydStkColGp)) {
				throw new Exception("차량위치(YD_STK_COL_GP) 정보가 없습니다!");
			}
			
			if (ydStkColGp.length() != 6) {
				throw new Exception("차량위치(YD_STK_COL_GP) 정보 길이가 6자리가 아닙니다!");
			}

			if (!"PT".equals(ydStkColGp.substring(2,4))) {
				throw new Exception("차량위치(YD_STK_COL_GP) 가 PT가 아닙니다!");
			}
			
			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, modifier);

			jrParam.setField("STACK_COL_GP", ydStkColGp);
			
			/**********************************************************
			* 1. 상하차 작업예약 존재여부 Check
			**********************************************************/
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.getWbookIdByLoc
			SELECT  A.YD_WBOOK_ID
			       ,A.YD_SCH_CD
			  FROM  TB_YM_WRKBOOK A
			       ,TB_YM_WRKBOOKMTL B
			 WHERE  A.YD_WBOOK_ID = B.YD_WBOOK_ID
			   AND  A.DEL_YN = 'N'
			   AND  B.DEL_YN = 'N'
			   AND  B.STOCK_ID IN (

			            SELECT STOCK_ID 
			            FROM   TB_YM_STACKLAYER
			            WHERE  STACK_COL_GP = :V_STACK_COL_GP
			              AND  STOCK_ID IS NOT NULL
			              
			       )
			GROUP BY A.YD_WBOOK_ID, A.YD_SCH_CD   */
			JDTORecordSet jsWbookId = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getWbookIdByLoc", logId, methodNm, "저장위치로 작업예약ID 조회");
			if( jsWbookId.size() > 0) {
				
				for(int ii = 0; ii < jsWbookId.size(); ii++) {
					
					ydWbookId 	= commUtils.trim(jsWbookId.getRecord(ii).getFieldString("YD_WBOOK_ID"));
					ydSchCd		= commUtils.trim(jsWbookId.getRecord(ii).getFieldString("YD_SCH_CD"));
					
					if("PT02LM".equals(ydSchCd.substring(2,8))||"PT02UM".equals(ydSchCd.substring(2,8))) {
						//이송하차 또는 이송상차의 경우
						
						jrParam.setField("YD_WBOOK_ID", ydWbookId);

					    /**********************************************************
						* 2. 작업예약/재료 삭제
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
						commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updDelYnWrkBookMtl", logId, methodNm, "TB_YM_WRKBOOKMTL");				

						//작업예약 삭제
						/*
						UPDATE TB_YM_WRKBOOK
						   SET MODIFIER    = :V_MODIFIER
						      ,MOD_DDTT    = SYSDATE
						      ,DEL_YN      = 'Y'
						 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
						   AND DEL_YN      = 'N'
						 */
						commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updDelYnWrkBook", logId, methodNm, "TB_YM_WRKBOOK");				
					}
				}
		    }
			
			/**********************************************************
			* 3. 적치단 Clear
			**********************************************************/
			jrParam.setField("STACK_COL_GP"    	, ydStkColGp	);
			jrParam.setField("STACK_BED_GP"    	, "01"		);
			jrParam.setField("STACK_LAYER_STAT1", ""		);
			jrParam.setField("STACK_LAYER_STAT2", "E"		);
			jrParam.setField("STOCK_ID"			, ""		);
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLyrByBedNo
			UPDATE TB_YM_STACKLAYER
			   SET 
			       MODIFIER = :V_MODIFIER
			      ,MOD_DDTT = SYSDATE
			      ,STOCK_ID = :V_STOCK_ID
			      ,STACK_LAYER_STAT = :V_STACK_LAYER_STAT2
			 WHERE  STACK_COL_GP = :V_STACK_COL_GP
			   AND  STACK_BED_GP = :V_STACK_BED_GP
			   AND  STACK_LAYER_STAT LIKE :V_STACK_LAYER_STAT1 || '%' */
			commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLyrByBedNo", logId, methodNm, "적치단  정보 Clear");
			
			/**********************************************************
			* 4. 적치열 Clear
			**********************************************************/
			jrParam.setField("STACK_COL_GP"    	, ydStkColGp	);
			/* com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStkColCarInitByLoc
			UPDATE TB_YM_STACKCOL
			   SET MODIFIER = :V_MODIFIER
			      ,MOD_DDTT = SYSDATE
			      ,CAR_CARD_NO = ''
			      ,STACK_STAT = ''
			      ,TRN_EQP_CD = ''
			      ,YD_CAR_USE_GP = ''
			 WHERE STACK_COL_GP = :V_STACK_COL_GP   */
			commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStkColCarInitByLoc", logId, methodNm, "적치열  정보 Clear");

			/**********************************************************
			* 5. 차량포인트 Clear
			**********************************************************/
			jrParam.setField("STAT"    				, "C"	);
			jrParam.setField("TRN_EQP_CD"    		, ""	);
			jrParam.setField("YS_STK_COL_GP"    	, ydStkColGp	); //쿼리 파라메터 수정 후 삭제해야 함 
			jrParam.setField("YD_STK_COL_GP"    	, ydStkColGp	);
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.carpointtrneqpcdupdateC
			UPDATE TB_YD_CARPOINT
			   SET YD_STK_COL_ACT_STAT=:V_STAT
			     , TRN_EQP_CD =:V_TRN_EQP_CD
			     , MOD_DDTT=sysdate
			     , MODIFIER='CarPointCP'
			 WHERE YD_STK_COL_GP=:V_YD_STK_COL_GP */
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.carpointtrneqpcdupdateC", logId, methodNm, "차량포인트  정보 Clear");
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	
	
	/**
	 * Holding Bed 조회 - Layer 용도 변경
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updLayerOption(GridData gdReq) throws DAOException {
		String methodNm = "Layer 용도 변경[BSlabJspSeEJB.updLayerOption] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
   
			String sSTACK_COL_GP1 = gdReq.getParam("STACK_COL_GP1");
			String sSTACK_BED_GP1 = gdReq.getParam("STACK_BED_GP1");
			String sSTACK_LAYER_ACTIVE_STAT1 = gdReq.getParam("STACK_LAYER_ACTIVE_STAT1");
			
			String sSTACK_COL_GP2 = gdReq.getParam("STACK_COL_GP2");
			String sSTACK_BED_GP2 = gdReq.getParam("STACK_BED_GP2");
			String sSTACK_LAYER_ACTIVE_STAT2 = gdReq.getParam("STACK_LAYER_ACTIVE_STAT2");
			
			/*if("N".equals(sSTACK_LAYER_ACTIVE_STAT1)) {
				//사용불가 상태로 바꾸기 전 적치되어 있는 슬라브 갯수 확인
				jrParam.setField("STACK_COL_GP"    		       , sSTACK_COL_GP1);       
				jrParam.setField("STACK_BED_GP"      		   , sSTACK_BED_GP1);       
			} else if("N".equals(sSTACK_LAYER_ACTIVE_STAT2)) {
				jrParam.setField("STACK_COL_GP"    		       , sSTACK_COL_GP2);       
				jrParam.setField("STACK_BED_GP"      		   , sSTACK_BED_GP2);
			}*/
			
			/* SELECT COUNT(STOCK_ID) AS SLAB_CNT
			   FROM TB_YM_STACKLAYER
			   WHERE STACK_COL_GP = :V_STACK_COL_GP
			   AND STACK_BED_GP = :V_STACK_BED_GP
			*/
			//JDTORecordSet jsSlabCnt = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getSlabCntByLoc", logId, methodNm, "저장위치로 적치중인 슬라브 갯수 조회");
			
			/*if( jsSlabCnt.size() > 0) {
				int ydSlabCnt = jsSlabCnt.getRecord(0).getFieldInt("SLAB_CNT");
			
				if(ydSlabCnt == 0) {*/
					jrParam.setField("STACK_COL_GP"    		       , sSTACK_COL_GP1);       
					jrParam.setField("STACK_BED_GP"      		   , sSTACK_BED_GP1);       
					jrParam.setField("STACK_LAYER_ACTIVE_STAT"     , sSTACK_LAYER_ACTIVE_STAT1);      
						
					/*  UPDATE TB_YM_STACKLAYER
						SET STACK_LAYER_ACTIVE_STAT = :V_STACK_LAYER_ACTIVE_STAT
						   ,MODIFIER = :V_MODIFIER
						   ,MOD_DDTT = SYSDATE
						WHERE STACK_COL_GP = :V_STACK_COL_GP
						AND STACK_BED_GP = :V_STACK_BED_GP
					*/
						
					commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLayerOption", logId, methodNm, "Layer 용도 변경1");
					
					jrParam.setField("STACK_COL_GP"    		       , sSTACK_COL_GP2);       
					jrParam.setField("STACK_BED_GP"      		   , sSTACK_BED_GP2);       
					jrParam.setField("STACK_LAYER_ACTIVE_STAT"     , sSTACK_LAYER_ACTIVE_STAT2);      
					
					commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updLayerOption", logId, methodNm, "Layer 용도 변경2");
					
			/*	} else {
					throw new Exception("적치중인 슬라브가 존재해 적치 단 활성상태를 변경할 수 없습니다!");
				}
			} else {
				throw new Exception("쿼리 실행 중 오류 발생!");
			}*/
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updSlabScarfingPattern
	
	/**
	 *      [A] 오퍼레이션명 : 구내운송 회송처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord runTsRetHt(GridData gdReq) throws DAOException {
		String methodNm = "구내운송 회송처리[BSlabJspSeEJB.runTsRetHt] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//Return Value
			JDTORecord jrRtn = null;
			String currDate   = commUtils.getDateTime14();	//현재시각
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			String sRetHt_ID = "";
			
			//회송ID생성
			sRetHt_ID = commDao.getSeqId(logId, methodNm, "RetHt");

			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			int iHsCnt = 0;

			commUtils.printLog(logId, "ㅁㅁㅁㅁㅁㅁ rowCnt : " + rowCnt, "");
			
			/**********************************************************
			* 회송 대상재 이송하차 작업예약 삭제처리
			**********************************************************/
			commUtils.printLog(logId, "ㅁㅁㅁㅁㅁㅁ 회송 대상재 이송하차 작업예약 삭제처리 시작", "");
			for (int ii = 0; ii < rowCnt; ii++) {
				
				if("HS".equals(commUtils.getValue(gdReq, "CMPL_GP", ii) )) { //회송인 경우만 처리..
			
					if(!"".equals( commUtils.getValue(gdReq, "YD_WBOOK_ID", ii))) {
						jrParam.setField("YD_WBOOK_ID", commUtils.getValue(gdReq, "YD_WBOOK_ID", ii) );
						
						this.trtWrkBookCncl(jrParam);
				
						iHsCnt++;
					}
				}
			}
			
			//if(iHsCnt == 0) {
			//	throw new Exception("회송 대상재가 없습니다! 회송처리 비정상 종료");
			//}
			commUtils.printLog(logId, "ㅁㅁㅁㅁㅁㅁ 회송 대상재 이송하차 작업예약 " + iHsCnt + "건 삭제처리 종료 ", "");
			
			
			/**********************************************************
			* 신규 회송 차량 스케줄ID 생성
			**********************************************************/
			commUtils.printLog(logId, "ㅁㅁㅁㅁㅁㅁ 신규 회송 차량 스케줄 ID 생성 시작 ", "");
			
			String sYdCarSchId = "";
			
			//차량스케줄ID
			sYdCarSchId = commDao.getSeqId(logId, methodNm, "CarSch");
			
			commUtils.printLog(logId, "ㅁㅁㅁㅁㅁㅁ 신규 회송 차량 스케줄 ID : " + sYdCarSchId , "");
			
			
			/**********************************************************
			* 회송이력 테이블 (TB_YD_RETHTHIST) 테이블 INSERT
			**********************************************************/
			commUtils.printLog(logId, "ㅁㅁㅁㅁㅁㅁ 회송이력 테이블 (TB_YD_RETHTHIST) 테이블 INSERT 시작 ", "");
			for (int ii = 0; ii < rowCnt; ii++) {
				
				if("HS".equals(commUtils.getValue(gdReq, "CMPL_GP", ii) )) { //회송인 경우만 처리..
					
					jrParam.setField("YD_RETHT_HIST_ID"		, sRetHt_ID);
					jrParam.setField("STL_NO"				, commUtils.getValue(gdReq, "STL_NO", ii) );
					jrParam.setField("YD_RETHT_EMPNO"		, gdReq.getParam("userid"));
					jrParam.setField("YD_RETHT_REQ_DT"		, currDate);
					jrParam.setField("YD_RETHT_RSN_CD"		, gdReq.getParam("RTNHT_RSN_CD"));
					jrParam.setField("YD_RETHT_RSN_CNTS"	, commUtils.trim(gdReq.getParam("RTNHT_RSN_MSG")));
					jrParam.setField("YD_RETHT_STAT_CD"		, "1");
					jrParam.setField("SPOS_WLOC_CD"			, commUtils.getValue(gdReq, "SPOS_WLOC_CD", ii) ); 
					jrParam.setField("ARR_WLOC_CD"			, commUtils.getValue(gdReq, "ARR_WLOC_CD", ii) ); 
					jrParam.setField("TRN_EQP_CD"			, commUtils.getValue(gdReq, "TRN_EQP_CD", ii) ); 
					jrParam.setField("YD_CAR_SCH_ID"		, sYdCarSchId ); 
					
					commDao.insert(jrParam, "com.inisteel.cim.ym.bslab.dao.BSalbDAO.insRetHtHist", logId, methodNm, "회송이력 테이블 INSERT");
					
					//iHsCnt++;
				}
			}
			
			//if(iHsCnt == 0) {
			//	throw new Exception("회송 대상재가 없습니다! 회송처리 비정상 종료");
			//}
			commUtils.printLog(logId, "ㅁㅁㅁㅁㅁㅁ 회송이력 테이블 (TB_YD_RETHTHIST) 테이블 INSERT 종료 ", "");
			
			
			/**********************************************************
			* 기존 차량 스케줄 종료 처리
			**********************************************************/
			commUtils.printLog(logId, "ㅁㅁㅁㅁㅁㅁ 기존 차량 스케줄 종료 처리 시작 ", "");
			
			jrParam.setField("YD_CAR_SCH_ID", commUtils.getValue(gdReq, "YD_CAR_SCH_ID", 0) ); //기존 차량스케줄ID
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.delCarschID", logId, methodNm, "기존 차량스케줄정보삭제");
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updDelYnCarSchMtl", logId, methodNm, "기존 차량스케줄재료 삭제");
			
			commUtils.printLog(logId, "ㅁㅁㅁㅁㅁㅁ 기존 차량 스케줄 종료 처리 종료 ", "");
			
			
			/**********************************************************
			* 신규 회송 차량 스케줄 생성
			**********************************************************/
			commUtils.printLog(logId, "ㅁㅁㅁㅁㅁㅁ 신규 회송 차량 스케줄 생성 시작 ", "");
			
			//String sYdCarSchId = "";
			
			//차량스케줄ID
			//sYdCarSchId = commDao.getSeqId(logId, methodNm, "CarSch");
			
			jrParam.setField("YD_CAR_SCH_ID"		, sYdCarSchId);
			jrParam.setField("OLD_YD_CAR_SCH_ID"	, commUtils.getValue(gdReq, "YD_CAR_SCH_ID", 0) ); //기존 차량스케줄ID
			jrParam.setField("REGISTER"				, "runTsRetHt"); //Pallet조회(B)에서 회송 표시를 하기위해 REGISTER 에 "runTsRetHt"를 입력한다.
			
			commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insRetHtCarSch", logId, methodNm, "회송 차량스케줄 INSERT");
			
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				if("HS".equals(commUtils.getValue(gdReq, "CMPL_GP", ii))) { //회송인 경우만 처리..
					
					jrParam.setField("YD_CAR_SCH_ID"		, sYdCarSchId);
					jrParam.setField("STL_NO"				, commUtils.getValue(gdReq, "STL_NO", ii) ); 
					jrParam.setField("DEL_YN"				, "N");
					jrParam.setField("YD_STK_BED_NO"		, "01");
					jrParam.setField("YD_STK_LYR_NO"		, commUtils.getValue(gdReq, "YD_STK_LYR_NO", ii) );  //적치단
					
					commDao.insert(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insCarFtMvMtl", logId, methodNm, "회송 차량스케줄 재료 INSERT");
				}
			}
			commUtils.printLog(logId, "ㅁㅁㅁㅁㅁㅁ 신규 회송 차량 스케줄 생성 종료 ", "");
			
			
			/**********************************************************
			* 구내운송 회송하차 완료실적 전문 편집
			**********************************************************/
			commUtils.printLog(logId, "ㅁㅁㅁㅁㅁㅁ 구내운송 회송하차 완료실적 전문 편집 시작 ", "");
			
			JDTORecord jrYDTSJ016 = JDTORecordFactory.getInstance().create();
			jrYDTSJ016.setField("JMS_TC_CD", "YDTSJ016"); //야드작업예약ID
			jrYDTSJ016.setField("JMS_TC_CREATE_DDTT", currDate); //JMSTC생성일시	
			
			jrYDTSJ016.setField("TRN_EQP_CD", commUtils.getValue(gdReq, "TRN_EQP_CD", 0) ); //운송장비코드
			jrYDTSJ016.setField("ARR_WLOC_CD", commUtils.getValue(gdReq, "ARR_WLOC_CD", 0) ); //착지개소코드
			jrYDTSJ016.setField("ARR_YD_PNT_CD", commUtils.getValue(gdReq, "YD_PNT_CD3", 0) ); //착지야드포인트코드
			jrYDTSJ016.setField("CARUD_CMPL_DT", currDate); //하차완료일시
			jrYDTSJ016.setField("CARLD_SH", commUtils.getValue(gdReq, "CARLD_SH", 0) ); //상차매수
			
			for (int ii = 0; ii < rowCnt; ii++) {
				
				jrYDTSJ016.setField("STL_NO" + (ii+1), commUtils.getValue(gdReq, "STL_NO", ii) ); //재료번호n
				jrYDTSJ016.setField("RETHT_CARUD_CMPL_GP" + (ii+1), commUtils.getValue(gdReq, "CMPL_GP", ii) ); //회송하차완료구분n
			}
			
			jrRtn = commUtils.addSndData(jrRtn, jrYDTSJ016);
			
			commUtils.printLog(logId, "ㅁㅁㅁㅁㅁㅁ 구내운송 회송하차 완료실적 전문 편집 종료 ", "");
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of runTsRetHt
	
	
	
	/**
	 * [A] 오퍼레이션명: 벤딩처리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inParam
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public GridData updStockBendReg(GridData gdReq) throws DAOException {
		
		String methodNm = "벤딩처리(모바일)[BSlabJspSeEJB.updStockBendReg] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		GridData outGrid = null;
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
		
			YmCommDAO dao = new YmCommDAO();

			outGrid = dao.updStockBendReg(gdReq);
			
			//bend 이력 등록 관리 추가 (chito 2016.09.01)
			//outGrid = dao.inStockBendReg(gdReq);
			
			return outGrid;
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + " :: " + e.getMessage(), e);
		}
	}
	
	////////////////////
	/**
	 * [A] 오퍼레이션명: 벤딩처리
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inParam
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public GridData updStockMarkReg(GridData gdReq) throws DAOException {
		
		String methodNm = "마킹처리(모바일)[BSlabJspSeEJB.updStockMarkReg] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		GridData outGrid = null;
		
		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);
		
			YmCommDAO dao = new YmCommDAO();

			outGrid = dao.updStockMarkReg(gdReq);
			
			//bend 이력 등록 관리 추가 (chito 2016.09.01)
			//outGrid = dao.inStockBendReg(gdReq);
			
			return outGrid;
		} catch(Exception e) {
			throw new DAOException(getClass().getName() + " :: " + e.getMessage(), e);
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 1열연 SLAB SCARP 이동지시 전송
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/	
	public JDTORecord sendSlabScrapMove(GridData gdReq) throws DAOException {
		String methodNm = "1열연 SLAB SCARP 이동지시 전송[BSlabJspSeEJB.sendSlabScrapMove] < " +  gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		JDTORecord jrRtn = null;	//크레인작업지시 전문 Return
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+");
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			jrParam.setResultMsg(methodNm);	//Log Method Name
			
			String sYD_EQP_ID 	= commUtils.trim(gdReq.getParam("YD_EQP_ID"));
			String sMV_GP 		= commUtils.nvl(gdReq.getParam("MV_GP"), "2");
			String sSCRAP_YARD 	= commUtils.nvl(gdReq.getParam("SCRAP_YARD"), "1");
			
			//TB_YM_STACKLAYER 테이블에서 SCRAP위치 X,Y 값을 읽어 온다.
			jrParam.setField("STACK_COL_GP"	, sSCRAP_YARD);
			JDTORecordSet rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getStackColGpXYAxisYMA8L010", logId, methodNm, "야드 기준 조회"); 
			
			if(rsResult2.size() > 0) {
				
				String sX_AXIS = commUtils.nvl(rsResult2.getRecord(0).getFieldString("STACK_LAYER_X_AXIS"),"0");
				String sY_AXIS = commUtils.nvl(rsResult2.getRecord(0).getFieldString("STACK_LAYER_Y_AXIS"),"0");
				
				if("0".equals(sX_AXIS) || "0".equals(sY_AXIS)) {
					
					commUtils.printLog(logId, ">>>> 1열연 SLAB SCRAP 이동지시 X,Y 좌표 조회 오류!!!", "SL");
					throw new Exception("1열연 SLAB SCRAP 이동지시 X,Y 좌표 조회 오류 ");
				} else {
					
					JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
					sndL2Msg.setResultCode(logId);	//Log ID
					sndL2Msg.setResultMsg(methodNm);	//Log Method Name
					sndL2Msg.setField("YD_EQP_ID"		, sYD_EQP_ID  ); 
					sndL2Msg.setField("MV_GP"			, sMV_GP      ); 
					sndL2Msg.setField("YD_WO_LOC_XAXIS" , sX_AXIS);
					sndL2Msg.setField("YD_WO_LOC_YAXIS" , sY_AXIS);				
		 
					jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA8L010", sndL2Msg));	 //전송 Data 생성	
				}
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of sendSlabScrapMove 처리
	
	/////////////////
	
	/**
	 *      [A] 오퍼레이션명 : E동 Turn 작업 시작
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/	
	public JDTORecord doTurnStart(GridData gdReq) throws DAOException {
		String methodNm = "E동 Turn 작업 시작[BSlabJspSeEJB.doTurnStart] < " +  gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		JDTORecord jrRtn = null;	//크레인작업지시 전문 Return
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+");
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			jrParam.setResultMsg(methodNm);	//Log Method Name
			
			String sYD_EQP_ID 		= commUtils.nvl(gdReq.getParam("YD_EQP_ID")		,"2ECRE3");
			String sMV_GP 			= commUtils.nvl(gdReq.getParam("MV_GP")			, "T");
			String sSTACK_COL_GP 	= commUtils.nvl(gdReq.getParam("STACK_COL_GP")	, "");
			
			//TB_YM_STACKLAYER 테이블에서 SCRAP위치 X,Y 값을 읽어 온다.
			jrParam.setField("STACK_COL_GP"	, sSTACK_COL_GP);
			JDTORecordSet rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getStackColGpXYAxisYMA8L010", logId, methodNm, "야드 기준 조회"); 
			
			if(rsResult2.size() > 0) {
				
				String sX_AXIS = commUtils.nvl(rsResult2.getRecord(0).getFieldString("STACK_LAYER_X_AXIS"),"0");
				String sY_AXIS = commUtils.nvl(rsResult2.getRecord(0).getFieldString("STACK_LAYER_Y_AXIS"),"0");
				
				if("0".equals(sX_AXIS) || "0".equals(sY_AXIS)) {
					
					commUtils.printLog(logId, ">>>> 1열연 SLAB Turn 작업  X,Y 좌표 조회 오류!!!", "SL");
					throw new Exception("1열연 SLAB STurn 작업  X,Y 좌표 조회 오류 ");
				} else {
					
					JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
					sndL2Msg.setResultCode(logId);	//Log ID
					sndL2Msg.setResultMsg(methodNm);	//Log Method Name
					sndL2Msg.setField("YD_EQP_ID"		, sYD_EQP_ID  ); 
					sndL2Msg.setField("MV_GP"			, sMV_GP      ); 
					sndL2Msg.setField("YD_WO_LOC_XAXIS" , sX_AXIS);
					sndL2Msg.setField("YD_WO_LOC_YAXIS" , sY_AXIS);				
		 
					jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA8L010", sndL2Msg));	 //전송 Data 생성	
					
					//TB_YM_EQUIP 테이블 EQUIP_STAT 에 'T' UPDATE
					jrParam.setField("EQUIP_STAT"		, sMV_GP);
					jrParam.setField("YD_EQP_ID"		, sYD_EQP_ID);
					
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updEquipStat", logId, methodNm, "TB_YM_EQUIP 수정");
				}
				
			}
			
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of doTurnStart 처리
	
	/**
	 *      [A] 오퍼레이션명 : E동 Turn 작업 종료
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/	
	public JDTORecord doTurnEnd(GridData gdReq) throws DAOException {
		String methodNm = "E동 Turn 작업 종료[BSlabJspSeEJB.doTurnEnd] < " +  gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		JDTORecord jrRtn = null;	//크레인작업지시 전문 Return
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+");
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			jrParam.setResultMsg(methodNm);	//Log Method Name
			
			String sYD_EQP_ID 		= commUtils.nvl(gdReq.getParam("YD_EQP_ID")		,"2ECRE3");
			String sMV_GP 			= commUtils.nvl(gdReq.getParam("MV_GP")			, "R");
			String sSTACK_COL_GP 	= commUtils.nvl(gdReq.getParam("STACK_COL_GP")	, "");
			
			//TB_YM_STACKLAYER 테이블에서 SCRAP위치 X,Y 값을 읽어 온다.
			jrParam.setField("STACK_COL_GP"	, sSTACK_COL_GP);
			JDTORecordSet rsResult2 = commDao.select(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.getStackColGpXYAxisYMA8L010", logId, methodNm, "야드 기준 조회"); 
			
			if(rsResult2.size() > 0) {
				
				String sX_AXIS = commUtils.nvl(rsResult2.getRecord(0).getFieldString("STACK_LAYER_X_AXIS"),"0");
				String sY_AXIS = commUtils.nvl(rsResult2.getRecord(0).getFieldString("STACK_LAYER_Y_AXIS"),"0");
				
				if("0".equals(sX_AXIS) || "0".equals(sY_AXIS)) {
					
					commUtils.printLog(logId, ">>>> 1열연 SLAB Turn 작업  X,Y 좌표 조회 오류!!!", "SL");
					throw new Exception("1열연 SLAB STurn 작업  X,Y 좌표 조회 오류 ");
				} else {
					
					JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
					sndL2Msg.setResultCode(logId);	//Log ID
					sndL2Msg.setResultMsg(methodNm);	//Log Method Name
					sndL2Msg.setField("YD_EQP_ID"		, sYD_EQP_ID  ); 
					sndL2Msg.setField("MV_GP"			, sMV_GP      ); 
					sndL2Msg.setField("YD_WO_LOC_XAXIS" , sX_AXIS);
					sndL2Msg.setField("YD_WO_LOC_YAXIS" , sY_AXIS);				
		 
					jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA8L010", sndL2Msg));	 //전송 Data 생성
					
					//TB_YM_EQUIP 테이블 EQUIP_STAT 에 'R' UPDATE
					jrParam.setField("EQUIP_STAT"		, sMV_GP);
					jrParam.setField("YD_EQP_ID"		, sYD_EQP_ID);
					
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updEquipStat", logId, methodNm, "TB_YM_EQUIP 수정");
				}
				
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of doTurnEnd 처리
	
	
	/**
	 * WB이상정보정리
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updWBChargeLotInfo(GridData gdReq) throws DAOException {
		String methodNm = "WB이상정보정리[BSlabJspSeEJB.updWBChargeLotInfo] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			//등록 할  레코드 수
			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			int iDelCnt = 0;
			
			for (int ii = 0; ii < rowCnt; ii++) {
				commUtils.printLog(logId, commUtils.getValue(gdReq, "STACK_COL_GP", ii).substring(2, 4)+" / "+commUtils.getValue(gdReq, "STOCK_ID", ii), "#####");
				if("WB".equals(commUtils.getValue(gdReq, "STACK_COL_GP", ii).substring(2, 4))) {
					
					/*com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockChargeLotNoClearWb 
					--CHARGE_LOT_NO 항목 CLEAR
					MERGE INTO TB_YM_STOCK ST USING (
					
					    SELECT A.STOCK_ID
					          ,SUBSTR(A.CHARGE_LOT_NO,-4) AS CHARGE_LOT_NO
					          ,A.CTS_RELAY_SADDLE
					      FROM TB_YM_STOCK     A
					     WHERE A.STOCK_ID = :V_STOCK_ID
					       AND A.DEL_YN = 'N'
					       
					) DD ON (ST.STOCK_ID = DD.STOCK_ID )
					WHEN MATCHED THEN UPDATE SET
						 ST.MODIFIER         = :V_MODIFIER
					    ,ST.MOD_DDTT         = SYSDATE
					    ,ST.CHARGE_LOT_NO    = ''
					    ,ST.CTS_RELAY_SADDLE = DD.CHARGE_LOT_NO
					 * */
					jrParam.setField("STOCK_ID", commUtils.getValue(gdReq, "STOCK_ID", ii)); 
					commDao.update(jrParam, "com.inisteel.cim.ym.bslab.dao.BSlabDAO.updStockChargeLotNoClearWb", logId, methodNm, "장입재료  STOCK의 CHARGE_LOT_NO 오류 정보 Clear ");
					
					iDelCnt++;
				}

			}
			
			if(iDelCnt>0) {
				jrRtn.setField("RTN_MSG", "WB이상정보정리 " + iDelCnt + " 건이 정상 처리되었습니다!");	
				
			} else {
				jrRtn.setField("RTN_MSG", "WB이상정보정리 대상이 없습니다!!");	
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updWBChargeLotInfo
	
	/**
	 * 야드차량사용TYPE
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updCarUseTypeGp(GridData gdReq) throws DAOException {
		String methodNm = "야드차량사용TYPE 변경[BSlabJspSeEJB.updCarUseTypeGp] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));
			
			jrParam.setField("YD_CAR_USETYPE_GP"	, gdReq.getParam("YD_CAR_USETYPE_GP") );
			jrParam.setField("YD_STK_COL_GP"		, gdReq.getParam("YD_STK_COL_GP") );
			
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCarUseTypeGp 
			UPDATE USRYDA.TB_YD_CARPOINT
			SET YD_CAR_USETYPE_GP=:V_YD_CAR_USETYPE_GP
			  , MOD_DDTT=SYSDATE
			  , MODIFIER=:V_MODIFIER
			 WHERE YD_STK_COL_GP=:V_YD_STK_COL_GP */
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCarUseTypeGp", logId, methodNm, "TB_YD_CARPOINT 수정");
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updCarUseTypeGp
	
	/**
	 * 기준관리 - 생산통제 압연지시 메세지 DEL_YN 수정
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param GridData gdReq
	 * @return JDTORecord
	 * @throws DAOException
	*/
	public JDTORecord updCtMsgRule(GridData gdReq) throws DAOException {
		String methodNm = "기준관리 - 생산통제 압연지시 메세지 DEL_YN 수정[BSlabJspSeEJB.updCtMsgRule] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			commUtils.printLog(logId, methodNm, "S+", gdReq);

			//Return Value
			JDTORecord jrRtn = null;

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, commUtils.trim(gdReq.getParam("userid")));

			jrParam.setField("DEL_YN"		, "Y"			);
			jrParam.setField("REPR_CD_GP"	, "MSG001"		);
			jrParam.setField("CD_GP"		, "*"			);
			jrParam.setField("ITEM"			, "*"			);
			commDao.update(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updYmRule", logId, methodNm, "생산통제MSG RULE 수정");
			

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}	
	} // end of updYmRule
	
}	