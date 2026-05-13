/**
 * @(#)YsCommSeEJBBean
 *
 * @version          V1.00
 * @author           조병기
 * @date             2014/12/22
 *
 * @description      야드공통 관리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2014/12/22   윤재광      조병기      최초 등록
 */
package com.inisteel.cim.ys.common.session;

import m2soft.rdsystem.server.rdon.RemoteInfo;
import m2soft.rdsystem.server.rdon.RemoteProxy;
import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.CommonUtil;
import com.inisteel.cim.hr.common.util.CmnUtil;
import com.inisteel.cim.hr.common.util.HrConstant;
import com.inisteel.cim.ys.common.dao.YsCommDAO;
import com.inisteel.cim.ys.common.util.YsCommUtils;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
/**
 *      [A] 클래스명 : 야드공통관리 Session EJB 
 * @ejb.bean name="YsCommSeEJB" jndi-name="YsCommSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300" 
 * @ejb.transaction type="Required"
*/
public class YsCommSeEJBBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;
	private YsCommUtils commUtils = new YsCommUtils();
	private YsCommDAO commDao = new YsCommDAO();
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}
	
	/**
	 *      [A] 오퍼레이션명 : 야드공통관리  코드 조회(WiseGrid)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return GridData
	 *      @throws DAOException
	*/
	public GridData getYsCode(GridData gdReq) throws DAOException {
		String methodNm = "특수강야드코드조회[YsCommSeEJB.getYdCode]";
		String logId = gdReq.getIPAddress();

		try {
			JDTORecordSet jrRst = commDao.getYsCode(gdReq);
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			return commUtils.jdtoRecordToGridData(gdRtn, jrRst.toList(), gdReq);
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 예외처리이력 테이블 Log 등록
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return void
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public void insExcptHist(JDTORecord jrParam) throws DAOException {
		String methodNm = "예외처리이력 테이블 Log 등록[YsCommSeEJB.insExcptHist] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			//예외처리이력 테이블  Log등록
			commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insExcptHist", logId, methodNm, "예외처리이력 테이블 등록");			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	
	
	/**
	 *      [A] 오퍼레이션명 : 특수강선재Label발행정보송신
	 *
	 * 	    @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param String sBndlNo
	 *      @return JDTORecord
	 *      @throws DAOException
	 */
	public void sndSrrLblIssueInfo(String sBndlNo) throws DAOException {
		
		Logger logger = new Logger("ys");
		
		try {
			logger.println(LogLevel.DEBUG, "▒▒ 특수강선재Label발행정보송신(sndSrrLblIssueInfo) 처리 시작");
			logger.println(LogLevel.DEBUG, HrConstant.LOG_LINE2);
			String errMsg = ""; //오류내용
			/**********************************************************
			 * 열연정정Label정보 조회
		     **********************************************************/
			/*
			  com.inisteel.cim.ys.common.dao.YsCommDAO.getSriLblInfo 특수강 선재 라벨 정보
				
				SELECT 
				    A.SPEC_ABBSYM                   AS A1,
				    'Φ'||A.REAL_MEASURE_BUNDLE_T    AS A2,
				    A.REAL_MEASURE_BUNDLE_WT        AS A3,
				    B.MARKING_DEMANDER_NAME         AS A4,
				    SUBSTR(A.MILL_INI_DATE,0,4)||'/'||
				    SUBSTR(A.MILL_INI_DATE,5,2)||'/'||
				    SUBSTR(A.MILL_INI_DATE,7,2)     AS A5,
				    A.USAGE_CD                      AS A6,
				    C.C_WRSLT                       AS A7,  
				    C.MN_WRSLT                      AS A8,
				    A.HEAT_NO                       AS A9,
				    A.BNDL_NO                       AS A10,
				   (SELECT CD_MNNG
				      FROM VW_CM_CODES
				     WHERE CD_EN_ID    = 'USAGE_CD'
				       AND CD_CAT_ID   = 'HS0000' 
				       AND CD_VAL      = A.USAGE_CD)AS A11,  
				    A.ORD_NO||A.ORD_DTL||A.BNDL_NO  AS A12
				    
				FROM TB_PB_BUNDLECOMM A,
				     TB_PB_OSCOMM B,
				     TB_PB_HEATCOMM C
				WHERE A.ORD_NO  = B.ORD_NO
				  AND A.ORD_DTL = B.ORD_DTL
				  AND A.HEAT_NO = C.HEAT_NO
				  AND A.BNDL_NO = :BNDL_NO
			 */
			JDTORecordSet lblInfos = commDao.getSriLblInfo(sBndlNo);
			JDTORecord lblInfo 	   = null;
			
			if(lblInfos == null || lblInfos.size() < 1) {
				errMsg = "\n▒▒▒ 특수강선재Label발행정보가 DB에 존재하지 않습니다.\n";
				throw new DAOException(errMsg);
			} else {
				lblInfo = lblInfos.getRecord(0);
			}
			
			CmnUtil.setEjbParamLog("특수강봉강Label정보", lblInfo);
			logger.println(LogLevel.DEBUG, HrConstant.LOG_LINE2);

			String sndMsg = "";
			String sSPEC_ABBSYM 			= lblInfo.getFieldString("A1");
			String sREAL_MEASURE_BUNDLE_T 	= lblInfo.getFieldString("A2");
			String sREAL_MEASURE_BUNDLE_WT 	= lblInfo.getFieldString("A3");
			String sMARKING_DEMANDER_NAME 	= lblInfo.getFieldString("A4");
			String sMILL_INI_DATE 			= lblInfo.getFieldString("A5");
			String sUSAGE_CD 				= lblInfo.getFieldString("A6");
			String sC_WRSLT 				= lblInfo.getFieldString("A7");
			String sMN_WRSLT 				= lblInfo.getFieldString("A8");
			String sHEAT_NO 				= lblInfo.getFieldString("A9");
			String sBNDL_NO 				= lblInfo.getFieldString("A10");
			String sUSAGE_NM 				= lblInfo.getFieldString("A11");
			String sORD_NO 					= lblInfo.getFieldString("A12"); //A.ORD_NO||A.ORD_DTL||A.BNDL_NO
			
			//Label용지 선택
			String paperType = "label1";
			String lblPrtNm  = "ZEBRA105_SRR";
			
			logger.println(LogLevel.DEBUG,"▒▒▒▒▒ l1  [ " + sSPEC_ABBSYM  			+ " : 규격약호 ]");
			logger.println(LogLevel.DEBUG,"▒▒▒▒▒ l2  [ " + sREAL_MEASURE_BUNDLE_T  	+ " : 두께 ]");
			logger.println(LogLevel.DEBUG,"▒▒▒▒▒ l3  [ " + sREAL_MEASURE_BUNDLE_WT  + " : 중량 ]");
			logger.println(LogLevel.DEBUG,"▒▒▒▒▒ l4  [ " + sMARKING_DEMANDER_NAME  	+ " : 마킹수요가명 ]");
			logger.println(LogLevel.DEBUG,"▒▒▒▒▒ l5  [ " + sMILL_INI_DATE  			+ " : 압연일자 ]");
			logger.println(LogLevel.DEBUG,"▒▒▒▒▒ l6  [ " + sUSAGE_CD  				+ " : 용도코드 ]");
			logger.println(LogLevel.DEBUG,"▒▒▒▒▒ l7  [ " + sC_WRSLT  				+ " : 소강성분C실적치 ]");
			logger.println(LogLevel.DEBUG,"▒▒▒▒▒ l8  [ " + sMN_WRSLT  				+ " : 소강성분Mn실적치 ]");
			logger.println(LogLevel.DEBUG,"▒▒▒▒▒ l9  [ " + sHEAT_NO  				+ " : 히트번호 ]");
			logger.println(LogLevel.DEBUG,"▒▒▒▒▒ l10 [ " + sBNDL_NO  				+ " : 번들번호 ]");
			logger.println(LogLevel.DEBUG,"▒▒▒▒▒ l11 [ " + sUSAGE_NM  				+ " : 용도명 ]");
			logger.println(LogLevel.DEBUG,"▒▒▒▒▒ l12 [ " + sORD_NO  				+ " : 주문번호/행번/번들번호 ]");
			logger.println(LogLevel.DEBUG,"▒▒▒▒▒ Label용지 [ " + paperType + " ]");
			logger.println(LogLevel.DEBUG,"▒▒▒▒▒ Label Printer [ " + lblPrtNm + " ]");
			
			//Label Form Data
			String tmpStr	= "";
			tmpStr  = " /rdata [";
			tmpStr += sSPEC_ABBSYM 				+ "^"; //a1
			tmpStr += sREAL_MEASURE_BUNDLE_T 	+ "^"; //a2 
			tmpStr += sREAL_MEASURE_BUNDLE_WT 	+ "^"; //a3
			tmpStr += sMARKING_DEMANDER_NAME 	+ "^"; //a4
			tmpStr += sMILL_INI_DATE 			+ "^"; //a5
			tmpStr += sUSAGE_CD 				+ "^"; //a6
			tmpStr += sC_WRSLT 					+ "^"; //a7
			tmpStr += sMN_WRSLT 				+ "^"; //a8
			tmpStr += sHEAT_NO 					+ "^"; //a9
			tmpStr += sBNDL_NO 					+ "^"; //a10
			tmpStr += sUSAGE_NM 				+ "^"; //a11
			tmpStr += sORD_NO					+ "^^^^]";//a12
			tmpStr += " /rpprnform [" + paperType + "]";
			tmpStr += " /rpdrv [";
			tmpStr += lblPrtNm + "]";
			
			String mrdPath = "";
			String rdonIp = "" ;
			String rdonURL = "" ;
			
			String sReal_Hs = "10.216.133.87"; // 운영
			String sDev_Hs  = "10.216.253.89"; // 개발
			
			rdonIp = sDev_Hs;
			
			rdonURL = "http://" + rdonIp + ":8080";
			mrdPath = rdonURL + "/rdontest/BNDL_LBL_v1.mrd";
			
			logger.println(LogLevel.DEBUG," mrdPath =  " + mrdPath);
			logger.println(LogLevel.DEBUG," rdonIp  =  " + rdonIp);

			//make RemoteInfo
			RemoteInfo info = new RemoteInfo();
			
			info.put("rdonip"   , rdonIp);
			info.put("rdonport" , "6585"         );
			info.put("opcode"   , "1"            );
			info.put("mrd"      , mrdPath        );
			info.put("mrdparams", tmpStr         );

			//Create Proxy
			RemoteProxy proxy = new RemoteProxy();
			proxy.setConnectTimeout(3);		// 접속 타임아웃 설정
			proxy.setTransferTimeout(20);	// 송수신 타임아웃 설정
			proxy.setReconnectionCount(2);	// 접속 시도 회수 설정

			//Transmission & Receive
			try {
				String response = proxy.process(info);	//RDON으로 전송, 응답
				logger.println(LogLevel.DEBUG,"▒▒▒▒▒ Report Designer Proxy Process Response [ " + response + " ]");
			} catch(Exception e) {
				throw new DAOException(getClass().getName() + e.getMessage(), e);
			}

			logger.println(LogLevel.DEBUG, HrConstant.LOG_LINE2);
				
			logger.println(LogLevel.DEBUG, "▒▒ 특수강선재Label발행정보송신(sndSrrLblIssueInfo) 처리 완료");
			logger.println(LogLevel.DEBUG, HrConstant.LOG_LINE2);

		} catch(Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
	}
	
}	