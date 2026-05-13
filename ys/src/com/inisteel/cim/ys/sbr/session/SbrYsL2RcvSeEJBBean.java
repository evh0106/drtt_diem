/**
 * @(#)SbrYsL2RcvSeEJBBean
 *
 * @version          V1.00
 * @author           
 * @date             2026.01.07
 *
 * @description      특수강 소형 야드 L2 수신 처리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자                    요청자           수정자           내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2026.01.07                   최초 등록
 * 특수강 소형 야드 L2 수신 처리 Session EJB
 * rcvN7YSL301 			: 저장위치제원요구(N7YSL301) 처리
 * rcvN7YSL302 			: 저장품제원요구(N7YSL302) 처리
 * rcvN7YSL303 			: 설비고장복구실적(N7YSL303) 처리
 * rcvN7YSL304 			: 크레인작업지시요구(N7YSL304) 처리
 * rcvN7YSL305 			: 크레인권상실적(N7YSL305) 처리
 * rcvN7YSL306 			: 크레인권하실적(N7YSL306) 처리
 * rcvN7YSL307 			: 크레인운전모드전환(N7YSL307) 처리
 * rcvN7YSL308 			: 차량작업예정정보요구(N7YSL308) 처리
 * rcvN7YSL309 			: 크레인작업가능응답(N7YSL309) 처리
 * rcvN7YSL310 			: L2야드적치현황정보(N7YSL310) 처리
 * rcvN7YSL311 			: L2야드좌표정보(N7YSL311) 처리
 * rcvN7YSL312 			: 정정이송요구재료정보(N7YSL312) 처리
 * rcvN7YSL321			: 대차 상하차 실적(정정에서 대차 상하차 완료)(N7YSL321) 처리
 *						  2026.01.28 L2 요청으로 대차 상차,하차 실적을 상하차 실적으로 통합
 * rcvN7YSL323			: 소형야드 대차이동정보(소형 야드 <-> 정정 대차 이동 정보)(N7YSL323) 처리
 * insPbStlFrToMove 	: 소형야드 이송지시 등록(TB_PB_STLFRTOMOVE 테이블)
 * insStrLocMtlIn		: 저장위치별 재료정보 요구(불일치) INSERT
 * rcvM5YSL301 			: 소형압연 추출대 추출요구(M5YSL301) 처리
 * rcvM5YSL302			: 소형압연설비 Tracking 정보(M5YSL302) 처리
 * updBndlCommYsStrLoc	: 번들공통 UPDATE
 * getYSN7L323			: 대차이동요구(YSN7L323)
 * TcProgStatChg        : 대차 진행상태 처리
 * getYSN7L304        	: 크레인작업실적응답(YSN7L304)
 * setDelYn        		: 열처리작업지시, 정정작업지시, 열처리이동요구, 정정이동요구 DEL_YN 처리
 * rcvN7YSL322(사용안함)	: 소형야드 대차 하차 실적(소형 야드 -> 정정 대차 이동후 정정에서 재료 하차 완료)(N7YSL322) 처리
 *						  2026.01.28 L2 요청으로 대차 상차,하차 실적을 상하차 실적으로 통합
 * rcvN7YSL324(사용안함)	: 소형야드 대차상태정보 처리
 *						  2026.01.28 설비고장복구실적(N7YSL303)에 대차 포함
 */

package com.inisteel.cim.ys.sbr.session;

import com.inisteel.cim.common.exception.DAOException;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.ys.common.dao.YsCommDAO;

import com.inisteel.cim.ys.common.session.YsComm;
import com.inisteel.cim.ys.common.util.YsCommUtils;
import com.inisteel.cim.ys.common.util.YsConstant;

import com.inisteel.cim.ys.common.session.YsCommCarTSMvSeEJBBean;


// GridData 사용
import com.inisteel.cim.ys.sbr.session.SbrYsJspSeEJBBean; 
import xlib.cmc.GridData;


/**
 *      [A] 클래스명 : 특수강 소형 야드 L2수신 처리
 *
 * @ejb.bean name="SbrYsL2RcvSeEJB" jndi-name="SbrYsL2RcvSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
*/
 
public class SbrYsL2RcvSeEJBBean extends BaseSessionBean {
	
	private static final long serialVersionUID = 1L;
	
	private YsCommUtils 		commUtils 		= new YsCommUtils();
	private YsCommDAO 			commDao 		= new YsCommDAO();
	private SbrYsComm 			sbrYsComm 		= new SbrYsComm();
	private SbrYsJspSeEJBBean	sbrYsJsp   		= new SbrYsJspSeEJBBean();  

// 구내 운송 처리	
	private YsCommCarTSMvSeEJBBean	CarTSMv   	= new YsCommCarTSMvSeEJBBean();  
	
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}	
	/***************************************************************************
	 * 
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 저장위치제원요구(N7YSL301)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvN7YSL301(JDTORecord rcvMsg) throws DAOException {
        String szMsg	= "";
        String methodNm = "소형야드 저장위치제원요구[SbrYsL2RcvSeEJB.rcvN7YSL301]";
        String logId 	= commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);	

		
		try {

			commUtils.printLog(logId, methodNm, "S+");

			szMsg = rcvMsg.toString();
			commUtils.printLog(logId, szMsg, "");
			
			/*
			7	YD_INFO_SYNC_CD	야드정보동기화코드		CHAR	1
			8	YD_GP			야드구분			CHAR	1
			9	YD_BAY_GP		야드동구분			CHAR	1
			10	YD_EQP_GP		야드설비구분			CHAR	2
			11	YS_STK_COL_NO	특수강야드적치열번호	CHAR	2
			12	YS_STK_BED_NO	특수강야드적치Bed번호	CHAR	2
			 */

			// 수신 항목 값
			String msgId        	= commUtils.getMsgId(rcvMsg); // EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String srydInfoSyncCd 	= commUtils.trim(rcvMsg.getFieldString("YD_INFO_SYNC_CD")); // 야드정보동기화코드
			String srydGp         	= commUtils.trim(rcvMsg.getFieldString("YD_GP"          )); // 야드구분
			String srydBayGp      	= commUtils.trim(rcvMsg.getFieldString("YD_BAY_GP"      )); // 야드동구분
			String srydEqpGp      	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_GP"      )); // 야드설비구분
			String srydStkColNo   	= commUtils.trim(rcvMsg.getFieldString("YS_STK_COL_NO"  )); // 야드적치열번호
			String srydStkBedNo   	= commUtils.trim(rcvMsg.getFieldString("YS_STK_BED_NO"  )); // 야드적치Bed번호
			
			methodNm = msgId.substring(0, 2) + methodNm;

			szMsg = "\n\t YD_INFO_SYNC_CD   : " + srydInfoSyncCd 
				  + "\n\t YD_GP             : " + srydGp 
				  + "\n\t YD_BAY_GP         : " + srydBayGp 
				  + "\n\t YD_EQP_GP         : " + srydEqpGp 
				  + "\n\t YS_STK_COL_NO     : " + srydStkColNo 
				  + "\n\t YS_STK_BED_NO     : " + srydStkBedNo;

			commUtils.printLog(logId, szMsg, "");

			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (!"G".equals(srydGp)) {
				throw new Exception("야드구분(YD_GP) 오류");
			} else if (!"D".equals(srydBayGp)) {
				throw new Exception("야드동구분(YD_BAY_GP) 오류");
			} else if ("".equals(srydEqpGp) && !"".equals(srydStkColNo)) {
				throw new Exception("야드설비구분(YD_EQP_GP) 없음");
			}

			/**********************************************************
			* 2. 저장위치제원(YSN7L301) 전문 생성
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	// Log ID
			jrParam.setResultMsg(methodNm);	// Log Method Name

			jrParam.setField("YD_INFO_SYNC_CD"	, srydInfoSyncCd								);	// 야드정보동기화코드
			jrParam.setField("YS_STK_COL_GP"  	, srydGp + srydBayGp + srydEqpGp + srydStkColNo	);	// 야드적치열구분
			jrParam.setField("YS_STK_BED_NO"  	, srydStkBedNo									);	// 야드적치Bed번호

			// 전송 Data 생성
			JDTORecord jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN7L301", jrParam));
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	}
	
	
	
	

	/**
	 *      [A] 오퍼레이션명 : 저장품제원요구(N7YSL302)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvN7YSL302(JDTORecord rcvMsg) throws DAOException {
		String szMsg	= "";
		String methodNm = "소형야드 저장품제원요구[SbrYsL2RcvSeEJB.rcvN7YSL302]";
		String logId 	= commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);	

		try {

			commUtils.printLog(logId, methodNm, "S+");

			szMsg = rcvMsg.toString();
			commUtils.printLog(logId, szMsg, "");
			
			/*
			7	YD_INFO_SYNC_CD	야드정보동기화코드		CHAR	1
			8	YD_GP			야드구분			CHAR	1
			9	YD_BAY_GP		야드동구분			CHAR	1
			10	YD_EQP_GP		야드설비구분			CHAR	2
			11	YS_STK_COL_NO	특수강야드적치열번호	CHAR	2
			12	YS_STK_BED_NO	특수강야드적치Bed번호	CHAR	2
			13	SSTL_NO			특수강재료번호		CHAR	12
			*/

			// 수신 항목 값
			String msgId        	= commUtils.getMsgId(rcvMsg); // EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String srydInfoSyncCd 	= commUtils.trim(rcvMsg.getFieldString("YD_INFO_SYNC_CD"));	// 야드정보동기화코드
			String srydGp         	= commUtils.trim(rcvMsg.getFieldString("YD_GP"          )); // 야드구분
			String srydBayGp      	= commUtils.trim(rcvMsg.getFieldString("YD_BAY_GP"      )); // 야드동구분
			String srydEqpGp      	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_GP"      )); // 야드설비구분
			String srydStkColNo   	= commUtils.trim(rcvMsg.getFieldString("YS_STK_COL_NO"  )); // 야드적치열번호
			String srydStkBedNo   	= commUtils.trim(rcvMsg.getFieldString("YS_STK_BED_NO"  )); // 야드적치Bed번호
			String srSstlNo       	= commUtils.trim(rcvMsg.getFieldString("SSTL_NO"        )); // 재료번호
			
			methodNm = msgId.substring(0, 2) + methodNm;

			szMsg = "\n\t YD_INFO_SYNC_CD   : " + srydInfoSyncCd 
				  + "\n\t YD_GP             : " + srydGp 
				  + "\n\t YD_BAY_GP         : " + srydBayGp 
				  + "\n\t YD_EQP_GP         : " + srydEqpGp 
				  + "\n\t YS_STK_COL_NO     : " + srydStkColNo 
				  + "\n\t YS_STK_BED_NO     : " + srydStkBedNo
				  + "\n\t SSTL_NO     		: " + srSstlNo;

			commUtils.printLog(logId, szMsg, "");

			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (!"G".equals(srydGp)) {
				throw new Exception("야드구분(Yd_GP) 다름");
			} else if (!"D".equals(srydBayGp)) {
				throw new Exception("동구분(ydBayGp) 다름");
			}

			if ("1".equals(srydInfoSyncCd) || "2".equals(srydInfoSyncCd) || "3".equals(srydInfoSyncCd) || "4".equals(srydInfoSyncCd)) {
				//저장위치별
				if ("".equals(srydEqpGp) && !"".equals(srydStkColNo)) {
					throw new Exception("야드설비구분(YS_EQP_GP) 없음");
				}
			} else {
				//재료별
				if ("".equals(srSstlNo)) {
					throw new Exception("재료번호(SSTL_NO) 없음");
				}
			}

			/**********************************************************
			* 2. 저장품제원(YSN7L302) 전문 생성
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	// Log ID
			jrParam.setResultMsg(methodNm);	// Log Method Name

			jrParam.setField("YD_INFO_SYNC_CD"	, srydInfoSyncCd								); // 야드정보동기화코드
			jrParam.setField("YS_STK_COL_GP"  	, srydGp + srydBayGp + srydEqpGp + srydStkColNo	); // 야드적치열구분
			jrParam.setField("YS_STK_BED_NO"  	, srydStkBedNo									); // 야드적치Bed번호
			jrParam.setField("YD_GP"          	, srydGp										); // 야드구분
			jrParam.setField("SSTL_NO"        	, srSstlNo										); // 재료번호

			// 전송Data 생성
			JDTORecord jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN7L302", jrParam));
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 설비고장복구실적(N7YSL303)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN7YSL303(JDTORecord rcvMsg) throws DAOException {
		String szMsg	= "";
		String methodNm = "소형야드 설비고장복구실적[SbrYsL2RcvSeEJB.rcvN7YSL303]";
		String logId 	= commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);	
		
		
		JDTORecord resMsg = JDTORecordFactory.getInstance().create(); // 크레인작업실적응답 전문 생성용
		boolean resYn = false;	// 크레인작업실적응답 전문 전송여부


		try {
			
			commUtils.printLog(logId, methodNm, "S+");

			szMsg = rcvMsg.toString();
			commUtils.printLog(logId, szMsg, "");
			
			/*
			7	YD_EQP_ID			야드설비ID			CHAR	6
			8	YD_EQP_STAT			야드설비상태			CHAR	1
			9	YD_EQP_PAUSE_CODE	야드설비휴지코드		CHAR	4
			10	YD_EQP_TRBL_RCVR_DT	야드설비고장복구일시	CHAR	14
			*/

			// 수신 항목 값
			String msgId				= commUtils.getMsgId(rcvMsg); 									// EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String srydEqpId			= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"			)); // 야드설비ID
			String srydEqpStat			= commUtils.trim(rcvMsg.getFieldString("YD_EQP_STAT"		)); // 야드설비상태(B:고장, N:정상, R:복구 등)
			String srydEqpPauseCode		= commUtils.trim(rcvMsg.getFieldString("YD_EQP_PAUSE_CODE"	)); // 야드설비휴지코드
			String srydEqpTrblRcvrDt	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_TRBL_RCVR_DT")); // 야드설비고장복구일시
			String sModifier			= commUtils.trim(rcvMsg.getFieldString("MODIFIER"			)); // 수정자(Backup Only)
			String sBrGp				= ""; 															// 고장복구구분
			
			if ("".equals(sModifier)) { sModifier = msgId; }
			
			methodNm = msgId.substring(0, 2) + methodNm;


			szMsg = "\n\t YD_EQP_ID   			: " + srydEqpId 
				  + "\n\t YD_EQP_STAT           : " + srydEqpStat 
				  + "\n\t YD_EQP_PAUSE_CODE     : " + srydEqpPauseCode 
				  + "\n\t YD_EQP_TRBL_RCVR_DT   : " + srydEqpTrblRcvrDt 
				  + "\n\t MODIFIER     			: " + sModifier 
				  ;

    		commUtils.printLog(logId, szMsg, "");
			
			// 크레인작업실적응답 전문 전송여부를 Check
			if (srydEqpId.length() == 6 && "CR".equals(srydEqpId.substring(2, 4))) {
				resYn = true;
			}

			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	// 전문 Return
			String ydL3HdRsCd = "";		// 야드L3처리결과코드
			String ydL3Msg    = ""; 	// 야드L3MESSAGE

			// 크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);									// Log ID
			resMsg.setResultMsg(methodNm);									// Log Method Name
			resMsg.setField("YD_EQP_ID"       	, srydEqpId					); 	// 야드설비ID
			resMsg.setField("YD_L2_WR_GP"     	, "R"						);	// 야드L2실적구분(고장복구실적)
			resMsg.setField("YD_L3_HD_RS_CD"  	, "BR99"					); 	// 야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       	, "오류:설비고장복구실적 수신처리"		); 	// 야드L3MESSAGE(Error)

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(srydEqpId)) {
				ydL3HdRsCd = "BR01";
				ydL3Msg    = "오류:설비ID 없음";
			} else if (srydEqpId.length() < 6 || !srydEqpId.startsWith("GD")) {
				ydL3HdRsCd = "BR02";
				ydL3Msg    = "오류:설비ID[" + srydEqpId + "] 이상";
			} else if ("".equals(srydEqpStat)) {
				ydL3HdRsCd = "BR03";
				ydL3Msg    = "오류:야드설비상태 없음";
			} else if ("".equals(srydEqpPauseCode) && "B".equals(srydEqpStat)) {
				ydL3HdRsCd = "BR04";
				ydL3Msg    = "오류:설비휴지코드 없음";
			} else if ("".equals(srydEqpTrblRcvrDt) && ("B".equals(srydEqpStat) || "N".equals(srydEqpStat) || "R".equals(srydEqpStat))) {
				ydL3HdRsCd = "BR05";
				ydL3Msg    = "오류:고장복구일시 없음";
			}

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); 	// 야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); 	// 야드L3MESSAGE
				
				throw new Exception(ydL3Msg);
			}
			
			/**********************************************************
			* 2. 설비상태 Check
			**********************************************************/
			if ("B".equals(srydEqpStat)) {
				sBrGp = "B"; //고장
				if ("0000".equals(srydEqpPauseCode)) {
					srydEqpPauseCode = "B000";
				}
			} else if ("N".equals(srydEqpStat) || "R".equals(srydEqpStat)) {
				sBrGp = "R"; //복구
				if ("0000".equals(srydEqpPauseCode)) {
					srydEqpPauseCode = "R000";
				}

				if ("CR".equals(srydEqpId.substring(2, 4))) {
					srydEqpStat = "W";
				} else {
					srydEqpStat = "N";
				}
			}

			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);									// Log ID
			jrParam.setResultMsg(methodNm);									// Log Method Name
			
			jrParam.setField("YD_EQP_ID"          	, srydEqpId			); 	// 야드설비ID
			jrParam.setField("YD_EQP_PAUSE_CODE"  	, srydEqpPauseCode 	); 	// 야드설비휴지코드
			jrParam.setField("YD_EQP_PAUSE_OCC_DT"	, srydEqpTrblRcvrDt	); 	// 야드설비휴지발생일시
			jrParam.setField("YD_EQP_STAT"        	, srydEqpStat		); 	// 야드설비상태
			jrParam.setField("BR_GP"              	, sBrGp				); 	// 고장복구구분
			jrParam.setField("MODIFIER"           	, sModifier			); 	// 수정자

			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStatEqp", logId, methodNm, "설비상태조회");

			if (jsChk == null || jsChk.size() == 0) {
				// 설비 Table 존재유무 Check
				ydL3HdRsCd 	= "BR11";
				ydL3Msg 	= "오류:설비ID[" + srydEqpId + "] 정보 없음";
			} else if (srydEqpStat.equals(commUtils.trim(jsChk.getRecord(0).getFieldString("YS_EQP_STAT")))) {
				// 설비 Table 설비상태 Check
				ydL3HdRsCd 	= "BR12";
				ydL3Msg 	= "오류:현재 설비상태[" + srydEqpStat + "]와 동일";
			}
			
			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); // 야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); // 야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			/**********************************************************
			* 3. 설비상태 수정
			**********************************************************/
			commDao.update(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.updStatEqp", logId, methodNm, "설비상태 수정");

			/**********************************************************
			* 4. 설비휴지 등록
			**********************************************************/
			if (!"".equals(sBrGp)) {
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updEqpPause", logId, methodNm, "설비휴지 등록");
			}
			
			/**********************************************************
			* 5. 크레인 리스케줄
			*  - 고장복구구분 [R:복구 리스케줄, B:고장 리스케줄]
			*  - 작업예약 야드스케쥴우선순위 수정
			*  - 크레인스케줄 야드스케쥴우선순위, 야드설비ID 수정
			*  - 대기상태인 야드설비ID에 해당하는 크레인작업지시 전문 추가
			**********************************************************/
			if ("CR".equals(srydEqpId.substring(2, 4))) {
				// 해당 크레인 스케줄 상태가 권상지시(1)일 경우 명령선택대기(W)로 변경
				if ("B".equals(srydEqpStat)) {
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnSchW", logId, methodNm, "크래인스케줄명령선택대기설정");
				}
				// 크레인 리스케줄
				jrParam.setField("MSG_ID", msgId); // 수신 전문 I/F ID
				jrRtn = this.trtCrnResch(jrParam);
			}
			
			/**********************************************************
			* 6. 크레인작업실적응답 전문 전송(YSN7L304)
			**********************************************************/
			if (resYn) {
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); 	// 야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ""    ); 	// 야드L3MESSAGE
				jrRtn = commUtils.addSndData(jrRtn, this.getYSN7L304(resMsg));
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			if (resYn) {
				try {
					
					// PIDEV_F : 정상SET후  ERROR 발생한 경우								
					if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {
						resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    	// 야드L3처리결과코드(Error)
						resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); 	// 야드L3MESSAGE(Error)
					}
					
					// 크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "YsCommEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { this.getYSN7L304(resMsg) });
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : Crane Reschedule 처리 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord jrParam
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtCrnResch(JDTORecord jrParam) throws DAOException {
		String methodNm = "크레인리스케줄[SbrYsL2RcvSeEJB.trtCrnResch]";
		String logId 	= commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);	

		try {

			commUtils.printLog(logId, methodNm, "S+");
			
			jrParam.setResultMsg(methodNm);		// Log Method Name

			JDTORecord jrRtn = null;			// 크레인작업지시 전문 Return

			// 작업예약 야드스케쥴우선순위 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnReschWrkBook", logId, methodNm, "작업예약 야드스케쥴우선순위 수정");

			// 크레인스케줄 야드스케쥴우선순위, 야드설비ID 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnReschCrnSch", logId, methodNm, "크레인스케줄 야드스케쥴우선순위, 야드설비ID 수정");

			// 크레인작업지시 대상 설비 조회
			JDTORecordSet jsWoEqp = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnReschWoEqp", logId, methodNm, "크레인작업지시 대상 설비 조회");

			int schCnt = jsWoEqp.size();

			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setResultCode(logId);			// Log ID
			jrYdMsg.setResultMsg(methodNm);			// Log Method Name

			String msgId = commUtils.trim(jrParam.getFieldString("MSG_ID")); 	// 수신 전문 I/F ID
			msgId = msgId.substring(0, 2);

			jrYdMsg.setField("JMS_TC_CD" ,	 		msgId + "YSL304"					);	// JMS TC 코드
			jrYdMsg.setField("JMS_TC_CREATE_DDTT" , commUtils.getDateTime14()			);	// JMS TC 생성일시
			jrYdMsg.setField("MODIFIER" , 			jrParam.getFieldString("MODIFIER")	);	// 수정자

			JDTORecord recInTemp	= null;
			
			for (int Loop_i = 0; Loop_i < schCnt; Loop_i++) {
				jsWoEqp.absolute(Loop_i);
				
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setRecord(jsWoEqp.getRecord());
				
				jrYdMsg.setField("YD_EQP_ID" , 			recInTemp.getFieldString("YD_EQP_ID")	); 	// 야드설비ID
				jrYdMsg.setField("YD_WRK_PROG_STAT" , 	"W"										); 	// 야드작업진행상태
				
				// 크레인작업지시요구 전문을 추가
				jrRtn = commUtils.addSndData(jrRtn, this.rcvN7YSL304(jrYdMsg));
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	

	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업지시요구(N7YSL304)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvN7YSL304(JDTORecord rcvMsg) throws DAOException {
		String szMsg	= "";
		String methodNm = "소형야드 크레인작업지시요구[SbrYsL2RcvSeEJB.rcvN7YSL304]";
		String logId 	= commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		
		JDTORecord resMsg = JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용

		try {

			commUtils.printLog(logId, methodNm, "S+");

			szMsg = rcvMsg.toString();
			commUtils.printLog(logId, szMsg, "");
			
			
			/*
			7	YD_EQP_ID			야드설비ID			CHAR	 6
			8	YD_EQP_WRK_MODE		야드설비작업Mode		CHAR	 1
			9	YD_WRK_PROG_STAT	야드작업진행상태		CHAR	 1
			10	YD_SCH_CD			야드스케쥴코드		CHAR	 8
			11	YD_CRN_SCH_ID		야드크레인스케쥴ID		CHAR	18
			12	YD_CRN_XAXIS		야드크레인X축		NUMBER	 7
			13	YD_CRN_YAXIS		야드크레인Y축		NUMBER	 5
			*/

			// 수신 항목 값
			String msgId			= commUtils.getMsgId(rcvMsg); 									// EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String srydEqpId		= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"			)); // 야드설비ID
			String srydWrkProgStat	= commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT"	)); // 야드작업진행상태
			String srydSchCd		= commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"			)); // 야드스케쥴코드
			String srydCrnSchId		= commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"		)); // 야드크레인스케쥴ID
			String sModifier		= commUtils.trim(rcvMsg.getFieldString("MODIFIER"			)); // 수정자(Backup Only)

			if ("".equals(sModifier)) { sModifier = msgId; }
			
			methodNm = msgId.substring(0, 2) + methodNm;

			szMsg = "\n\t YD_EQP_ID   		: " + srydEqpId 
				  + "\n\t YD_WRK_PROG_STAT	: " + srydWrkProgStat 
				  + "\n\t YD_SCH_CD     	: " + srydSchCd 
				  + "\n\t YD_CRN_SCH_ID   	: " + srydCrnSchId 
				  + "\n\t MODIFIER     		: " + sModifier 
				  ;

			commUtils.printLog(logId, szMsg, "");
				
			
			szMsg = "크레인작업지시요구 [ " + srydEqpId + " : " + srydWrkProgStat +  " - " + srydCrnSchId + " ]";
			commUtils.printLog(logId, szMsg, "");

			JDTORecord jrRtn  = null;	// 전문 Return
			String ydL3HdRsCd = "";		// 야드L3처리결과코드
			String ydL3Msg    = ""; 	// 야드L3MESSAGE

			//크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);									// Log ID
			resMsg.setResultMsg(methodNm);									// Log Method Name
			
			resMsg.setField("YD_EQP_ID" 		, srydEqpId				); 	// 야드설비ID
			resMsg.setField("YD_WRK_PROG_STAT"	, srydWrkProgStat		); 	// 야드작업진행상태
			resMsg.setField("YD_SCH_CD" 		, srydSchCd				); 	// 야드스케쥴코드
			resMsg.setField("YD_CRN_SCH_ID" 	, srydCrnSchId			); 	// 야드크레인스케쥴ID
			resMsg.setField("YD_L2_WR_GP" 		, "J"					); 	// 야드L2실적구분(지시요구)
			resMsg.setField("YD_L3_HD_RS_CD" 	, "JR99"				); 	// 야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG" 		, "오류:크레인작업지시요구 수신처리"	); 	// 야드L3MESSAGE(Error)

			//조회 및 등록용
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);						// Log ID
			jrParam.setResultMsg(methodNm);						// Log Method Name
			
			jrParam.setField("YD_EQP_ID" 		, srydEqpId   	); 	// 야드설비ID
			jrParam.setField("YD_CRN_SCH_ID" 	, srydCrnSchId	); 	// 야드크레인스케쥴ID
			jrParam.setField("MODIFIER" 		, sModifier  	); 	// 수정자

			
			/**********************************************************
			* 1. 설비상태 Check
			**********************************************************/
			JDTORecord jrChk = sbrYsComm.chkEqpStat(jrParam);

			ydL3HdRsCd = commUtils.trim(jrChk.getFieldString("YD_L3_HD_RS_CD"));
			ydL3Msg    = commUtils.trim(jrChk.getFieldString("YD_L3_MSG"     ));

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD" 	, ydL3HdRsCd	); // 야드L3처리결과코드
				resMsg.setField("YD_L3_MSG" 		, ydL3Msg   	); // 야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			/**********************************************************
			* 2. 크레인스케줄 조회
			*    2.1 크레인스케줄이 존재하면 전송
			*    2.2 크레인스케줄이 존재하지 않으면 수신된 야드작업진행상태에 따라 처리
			**********************************************************/
			
			JDTORecordSet jsSch = commDao.select(jrParam, "com.inisteel.cim.ys.sbr.dao.getCrnSchN7YSL304", logId, methodNm, "크레인스케줄 조회");

			if (jsSch.size() > 0) {
				/**********************************************************
				* 2.1 크레인스케줄이 존재하면 수신된 야드작업진행상태에 상관없이 작업지시 전송
				**********************************************************/
				srydCrnSchId    = commUtils.trim(jsSch.getRecord(0).getFieldString("YD_CRN_SCH_ID"   ));
				srydWrkProgStat = commUtils.trim(jsSch.getRecord(0).getFieldString("YD_WRK_PROG_STAT"));

				jrParam.setField("YD_CRN_SCH_ID", srydCrnSchId); // 야드크레인스케쥴ID

				if ("1".equals(srydWrkProgStat) || "2".equals(srydWrkProgStat) || "3".equals(srydWrkProgStat)) {
					/**********************************************************
					* 2.1.1 권상지시[1], 권상완료[2], 권하지시[3] 이면 재지시 전송
					**********************************************************/
					jrParam.setField("MSG_GP", "U"); //전문구분 - 재지시
				} else {
					/**********************************************************
					* 2.1.2 대기[W] 이면 다음 작업지시 전송
					**********************************************************/
					jrParam.setField("MSG_GP", "I"); //전문구분 - 신규

					//설비의 야드설비상태 수정
					jrParam.setField("YD_EQP_STAT", "1"); //권상작업지시

					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatEqp", logId, methodNm, "설비상태 수정");

					//크레인스케줄 야드작업진행상태 수정
					jrParam.setField("YD_WRK_PROG_STAT", "1"); //권상지시

					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatCrnSchWrkProg", logId, methodNm, "크레인스케줄 야드작업진행상태 수정");

				}
				
				// 크레인작업지시(YSN7L303) 전문 생성
				jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN7L303", jrParam));

				szMsg = "크레인작업지시요구 작업지시 전송 [ " + srydEqpId + " : " + srydWrkProgStat +  " - " + srydCrnSchId + " ]";
				commUtils.printLog(logId, szMsg, "");
				
				
			} else {
				/**********************************************************
				* 2.2 크레인스케줄이 존재하지 않으면 수신된 야드작업진행상태에 따라 처리
				*    2.1 권상지시[1], 권상완료[2], 권하지시[3] 이면 Error 처리
				*    2.2 권하완료[4] 이면 스케줄을 생성
				*    2.3 명령선택대기[W] 이면 응답 전문을 전송 -> 2.2로 통합
				**********************************************************/
				if ("1".equals(srydWrkProgStat) || "2".equals(srydWrkProgStat) || "3".equals(srydWrkProgStat)) {
					/**********************************************************
					* 2.2.1 재지시요구 시
					**********************************************************/
					resMsg.setField("YD_L3_HD_RS_CD", "9999" 													); // 야드L3처리결과코드
					resMsg.setField("YD_L3_MSG"     , "크레인[" + srydEqpId + "-" + srydWrkProgStat + "] 작업지시 없음"	); // 야드L3MESSAGE
					jrRtn = commUtils.addSndData(jrRtn, this.getYSN7L304(resMsg));

					szMsg = "크레인작업지시요구(재지시요구) 작업지시 없음 [ " + srydEqpId + " : " + srydWrkProgStat + " - " + srydCrnSchId + " ]";
					commUtils.printLog(logId, szMsg, "");
					
				} else {
					/**********************************************************
					* 2.2.2 대기상태[W], 권하완료[4] 지시요구
					**********************************************************/
					// 크레인작업지시가 없으면 설비의 야드설비상태 수정
					jrParam.setField("YD_EQP_STAT", "W"); // 대기(Wait)

					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatEqp", logId, methodNm, "설비상태 수정");


					JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name

// 2026.02.23 PARM 으 로 넘어온 스케쥴코드 있으면 해당 스케쥴코드 제외(크레인 대차 상차 완료후 다음 작업이 해당 대차 작업이면 제외 하기 위해 추가)
					jrParam.setField("YD_SCH_CD", srydSchCd); 	// 야드크레인스케쥴ID
					
					// 작업예약 조회
					JDTORecordSet jsWrkBook = commDao.select(jrParam, "com.inisteel.cim.ys.sbr.dao.getWbIdN7YSL304", logId, methodNm, "작업예약 조회");

					// 작업예약이 있으면 크레인스케줄호출
					if (jsWrkBook.size() > 0) {
						ydL3Msg = "크레인스케줄 호출";

						jrYdMsg.setField("YD_WBOOK_ID"		, jsWrkBook.getRecord(0).getFieldString("YD_WBOOK_ID")	); 	// 야드작업예약ID
						jrYdMsg.setField("YD_SCH_CD"		, jsWrkBook.getRecord(0).getFieldString("YD_SCH_CD"  )	); 	// 야드스케쥴코드
						jrYdMsg.setField("YD_EQP_ID"		, srydEqpId 											); 	// 야드설비ID
						jrYdMsg.setField("YD_SCH_ST_GP"		, "A"													); 	// 야드스케쥴기동구분(Auto)
						jrYdMsg.setField("YD_SCH_REQ_GP"	, "N"													); 	// 야드스케쥴요청구분(권하완료후 다음)
						jrYdMsg.setField("MODIFIER"			, sModifier												); 	// 수정자

						jrRtn = sbrYsComm.getCrnSchMsg(jrYdMsg);
					} else {
						ydL3Msg = "다음 크레인작업지시 없음";
						
					}

					resMsg.setField("YD_L3_HD_RS_CD"	, "9999" ); // 야드L3처리결과코드
					resMsg.setField("YD_L3_MSG" 		, ydL3Msg); // 야드L3MESSAGE
					
					jrRtn = commUtils.addSndData(jrRtn, this.getYSN7L304(resMsg));

					szMsg = "크레인작업지시요구(다음지시) " + ydL3Msg + " [ " + srydEqpId + " : " + srydWrkProgStat + " ]";
					commUtils.printLog(logId, szMsg, "");

				}
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			try {
				
				// PIDEV_F : 정상SET후  ERROR 발생한 경우								
				if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
					resMsg.setField("YD_L3_HD_RS_CD"	, "UP99"				);  // 야드L3처리결과코드(Error)							
					resMsg.setField("YD_L3_MSG" 		, "오류:작업지시요구 수신처리"		); 	// 야드L3MESSAGE(Error)							
				}
				
				// 크레인작업실적응답 전문 전송
				EJBConnector resConn = new EJBConnector("default", "YsCommEJB", this);
				resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { this.getYSN7L304(resMsg) });
			} catch (Exception se) {}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인권상실적(N7YSL305)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN7YSL305(JDTORecord rcvMsg) throws DAOException {
		String szMsg	= "";
		String methodNm = "크레인권상실적[SbrYsL2RcvSeEJB.rcvN7YSL305]";
		String logId 	= commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		
		JDTORecord resMsg = JDTORecordFactory.getInstance().create(); // 크레인작업실적응답 전문 생성용
		boolean resYn = true;	//크레인작업실적응답 전문 전송여부

		try {

			commUtils.printLog(logId, methodNm, "S+");

			szMsg = rcvMsg.toString();
			commUtils.printLog(logId, szMsg, "");
			
			/*
			7	YD_EQP_ID			야드설비ID			CHAR	 6
			8	YD_EQP_WRK_MODE		야드설비작업Mode		CHAR	 1
			9	YD_WRK_PROG_STAT	야드작업진행상태		CHAR	 1
			10	YD_SCH_CD			야드스케쥴코드		CHAR	 8
			11	YD_CRN_SCH_ID		야드크레인스케쥴ID		CHAR	18
			12	YS_UP_WR_LOC		특수강야드권상실적위치	CHAR	 8
			13	YS_UP_WR_LAYER		특수강야드권상실적단	CHAR	 2
			14	YD_CRN_XAXIS		야드크레인X축		NUMBER	 7
			15	YD_CRN_YAXIS		야드크레인Y축		NUMBER	 5
			16	YD_CRN_ZAXIS		야드크레인Z축		NUMBER	 5
			17	YD_EQP_WRK_MODE2	야드설비작업Mode2	CHAR	 1
			*/

			//수신 항목 값
			String msgId			= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String srydEqpId		= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); 	// 야드설비ID
			String srydEqpWrkMode	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE" )); 	// 야드설비작업Mode("1": On-Line, "2": Off-Line )
			String srydWrkProgStat	= commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")); 	// 야드작업진행상태
			String srydSchCd		= commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )); 	// 야드스케쥴코드
			String srydCrnSchId		= commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )); 	// 야드크레인스케쥴ID
			String srydUpWrLoc		= commUtils.trim(rcvMsg.getFieldString("YS_UP_WR_LOC"    )); 	// 야드권상실적위치
			String srydUpWrLayer	= commUtils.trim(rcvMsg.getFieldString("YS_UP_WR_LAYER"  )); 	// 야드권상실적단
			String srydCrnXaxis		= commUtils.trim(rcvMsg.getFieldString("YD_CRN_XAXIS"    )); 	// 야드크레인X축
			String srydCrnYaxis		= commUtils.trim(rcvMsg.getFieldString("YD_CRN_YAXIS"    )); 	// 야드크레인Y축
			String srydCrnZaxis		= commUtils.trim(rcvMsg.getFieldString("YD_CRN_ZAXIS"    )); 	// 야드크레인Z축			
			String sModifier		= commUtils.trim(rcvMsg.getFieldString("MODIFIER"        )); 	// 수정자(Backup Only)
			String ydWbookId		= ""; 															// 야드작업예약ID
			String ydDnWoLoc		= ""; 															// 야드권하지시위치
			
			if ("".equals(sModifier)) { sModifier = msgId; }
			
			methodNm = msgId.substring(0, 2) + methodNm;
			
			szMsg = "\n\t YD_EQP_ID			: " + srydEqpId 
				  + "\n\t YD_EQP_WRK_MODE	: " + srydEqpWrkMode 
				  + "\n\t YD_WRK_PROG_STAT	: " + srydWrkProgStat 
				  + "\n\t YD_SCH_CD			: " + srydSchCd 
				  + "\n\t YD_CRN_SCH_ID		: " + srydCrnSchId 
				  + "\n\t YS_UP_WR_LOC		: " + srydUpWrLoc 
				  + "\n\t YS_UP_WR_LAYER	: " + srydUpWrLayer 
				  + "\n\t YD_CRN_XAXIS		: " + srydCrnXaxis 
				  + "\n\t YD_CRN_YAXIS		: " + srydCrnYaxis 
				  + "\n\t YD_CRN_ZAXIS		: " + srydCrnZaxis 
				  + "\n\t MODIFIER			: " + sModifier 
				  ;

    		commUtils.printLog(logId, szMsg, "");
				

			JDTORecord jrRtn 	= null;	// 전문 Return
			String ydL3HdRsCd 	= "";		// 야드L3처리결과코드
			String ydL3Msg    	= ""; 	// 야드L3MESSAGE

			// 크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);	// Log ID
			resMsg.setResultMsg(methodNm);	// Log Method Name
			resMsg.setField("YD_EQP_ID"			, srydEqpId      	); 	// 야드설비ID
			resMsg.setField("YD_WRK_PROG_STAT"	, srydWrkProgStat	); 	// 야드작업진행상태
			resMsg.setField("YD_SCH_CD"			, srydSchCd      	); 	// 야드스케쥴코드
			resMsg.setField("YD_CRN_SCH_ID"		, srydCrnSchId   	); 	// 야드크레인스케쥴ID
			resMsg.setField("YD_L2_WR_GP"		, "U"          		); 	// 야드L2실적구분(권상실적)
			resMsg.setField("YD_L3_HD_RS_CD"	, "UP99"       		); 	// 야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"			, "오류:권상실적 수신처리"	); 	// 야드L3MESSAGE(Error)

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(srydEqpId)) {
				ydL3HdRsCd = "UP01";
				ydL3Msg    = "오류:설비ID 없음";
			} else if (srydEqpId.length() < 6 || !srydEqpId.startsWith("GD")) {
				ydL3HdRsCd = "UP02";
				ydL3Msg    = "오류:설비ID[" + srydEqpId + "] 이상";
			} else if ("".equals(srydCrnSchId)) {
				ydL3HdRsCd = "UP03";
				ydL3Msg    = "오류:크레인스케쥴ID 없음";
			} else if ("".equals(srydUpWrLoc)) {
				ydL3HdRsCd = "UP04";
				ydL3Msg    = "오류:권상실적위치 없음";
			} else if ("".equals(srydUpWrLayer)) {
				ydL3HdRsCd = "UP05";
				ydL3Msg    = "오류:권상실적단 없음";
			}

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); // 야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); // 야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			
			
			/**********************************************************
			* 2. 크레인스케쥴ID Check
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_CRN_SCH_ID"	, srydCrnSchId					); 	// 야드크레인스케쥴ID
			jrParam.setField("MODIFIER"     	, sModifier  					); 	// 수정자
			
			jrParam.setField("YD_STK_COL_GP"	, srydUpWrLoc.substring(0, 6)	); 	// 야드적치열구분
			jrParam.setField("YD_STK_BED_NO"	, srydUpWrLoc.substring(6, 8)	); 	// 야드적치Bed번호

			JDTORecord jrChk = null;

			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.sbr.dao.getStatCrnSchSbr", logId, methodNm, "크레인스케줄상태 조회");

			if (jsChk.size() == 0) {
				// 크레인스케쥴 Table 존재유무 Check
				ydL3HdRsCd 	= "UP11";
				ydL3Msg 	= "오류:없거나 종료된 크레인스케쥴ID";
			} else {
				// 크레인스케쥴 Table 야드작업진행상태 Check
				jrChk = jsChk.getRecord(0);
				ydWbookId       = commUtils.trim(jrChk.getFieldString("YD_WBOOK_ID"     )); // 야드작업예약ID
				ydDnWoLoc       = commUtils.trim(jrChk.getFieldString("YS_DN_WO_LOC"    )); // 야드권하지시위치
				String tmpStat  = commUtils.trim(jrChk.getFieldString("YD_WRK_PROG_STAT")); // 야드작업진행상태
				String tmpEqpId = commUtils.trim(jrChk.getFieldString("YD_EQP_ID"       )); // 야드설비ID
				if (!"1".equals(tmpStat) && !"W".equals(tmpStat)) {
					ydL3HdRsCd 	= "UP12";
					ydL3Msg 	= "오류:현재 작업진행상태[" + tmpStat + "] 이상";
				} else if (!srydEqpId.equals(tmpEqpId)) {
					ydL3HdRsCd 	= "UP13";
					ydL3Msg 	= "오류:현재 설비ID와[" + tmpEqpId + "] 다름";
				}
			}
			
			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); // 야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); // 야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			jrParam.setField("YD_WBOOK_ID", ydWbookId); // 야드작업예약ID
			
			String currDt = commUtils.getDateTime14(); // 현재시각
			
			/**********************************************************
			* 3. 전송 전문 조회
			* 3.1  추출완료 전문 전송
			*     소형압연 추출대 추출완료
			*     대차 하차 실적
			**********************************************************/
			if(   "GDPC11".equals(srydUpWrLoc.substring(0, 6))
			   || "GDPC21".equals(srydUpWrLoc.substring(0, 6))) {
				
				// 소형압연 추출대 저장위치 권상 완료시(GDPC11, GDPC21)
				// 소형압연 추출대 추출완료(YSM5L301) 송신

				jrParam.setField("YD_EQP_ID"     , srydUpWrLoc.substring(0, 6));	// 설비코드

				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSM5L301", jrParam));
				
		    } else if("GDTC04".equals(srydUpWrLoc.substring(0, 6))  
				   || "GDTC05".equals(srydUpWrLoc.substring(0, 6))
				   || "GDTC06".equals(srydUpWrLoc.substring(0, 6))) {
				
				// 대차 저장위치 권상  완료시(GDTC04, GDTC05, GDTC06)
				// 대차하차실적(YSN7L322) 송신

		    	jrParam.setField("YD_EQP_ID"		, srydUpWrLoc.substring(0, 6)	);	// 설비코드
				// 2026.01.28 L2 요청으로 대차 상차,하차 실적을 상하차 실적으로 통합
				jrParam.setField("YD_EQP_WRK_STAT" 	, "U"         					); 	// 적재상태(상하차구분 L : 상차,  U : 하차)
				
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN7L311", jrParam));

		    }
			
			
			/**********************************************************
			* 4. 권상실적위치가 차량(하차)
			*    차량스케줄 야드차량진행상태가 하차도착(B) 또는 하차검수(C) 이고
			*  	   야드차량사용구분이 구내운송(L) 이면
			* 4.1 구내운송 소재차량하차개시(YSTSJ009) 전송
			* 4.2 차량이송재료 삭제
			* 4.3 차량스케줄 수정
			*   - 야드차량진행상태(D:하차개시), 야드설비작업매수 등 수정
			**********************************************************/
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			if("TR".equals(srydUpWrLoc.substring(2, 4))) {
				// 차량스케줄  야드하차작업예약ID 등록 (차량스케줄에 야드하차작업예약ID 없을 경우)
				recPara.setField("YD_WBOOK_ID" , ydWbookId 	); 
				recPara.setField("MODIFIER" , sModifier     ); 

				commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CarSchUdWbId", logId, methodNm, "차량스케줄(하차) 수정");
				
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setResultCode(logId);	// Log ID
				recPara.setResultMsg(methodNm);	// Log Method Name
				recPara.setField("WR_DT" 		 , commUtils.getDateTime14()  	); 
				recPara.setField("YS_STK_COL_GP" , srydUpWrLoc.substring(0, 6)	); 
				// 구내운송 소재차량하차개시
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSTSJ009", recPara));
				
				
				// 하차 차량스케줄 야드설비작업매수, 중량, 야드차량진행상태, 야드하차개시일시 수정
				
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID", srydCrnSchId 				); 
				recPara.setField("YD_WBOOK_ID" 	, ydWbookId					); 
				recPara.setField("WR_DT" 		, commUtils.getDateTime14()	); 
				recPara.setField("MODIFIER" 	, sModifier     			); 
    	
				commDao.update(recPara, "com.inisteel.cim.ys.sbr.dao.updN7YSL305CarSchUd", logId, methodNm, "하차 차량스케줄 수정 ");
				
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID" , srydCrnSchId 	); 
				recPara.setField("MODIFIER" , sModifier     		); 
    	
				commDao.update(recPara, "com.inisteel.cim.ys.sbr.dao.updN7YSL305CarMtlDel", logId, methodNm, "차량이송재료 삭제");
				
				
			} 
			else if ("TC".equals(srydUpWrLoc.substring(2, 4))) {
////////////////////////////////////////////////////////////////////////////
//2026.02.26 권상 위치가 대차 이면 대차 스케쥴 변경
////////////////////////////////////////////////////////////////////////////
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setResultCode(logId);	//Log ID
				recPara.setResultMsg(methodNm);	//Log Method Name
				
				recPara.setField("YD_CRN_SCH_ID", srydCrnSchId	); 

				jsChk = commDao.select(recPara, "com.inisteel.cim.ys.sbr.dao.getCrSchMatl", logId, methodNm, "크레인 스케쥴 재료 조회 "); 
				if (jsChk.size() > 0) {
					jrChk = jsChk.getRecord(0);

					String srSstlNo = commUtils.trim(jrChk.getFieldString("SSTL_NO")); 	// 재료 번호

					JDTORecordSet 	jsTemp 			= null;
					JDTORecord 		jrTemp 			= null;
					
					String 			szYD_TC_SCH_ID 	= "";
					
				    int 			intRtnVal		= 0 ;
					
					jrParam.setField("SSTL_NO"		, srSstlNo	); // 재료번호
					
					jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.sbr.dao.getTcarSchMatl", logId, methodNm, "재료번호로 대차 스케줄 조회");
					
					if (jsTemp != null && jsTemp.size() > 0) {
						
						jrTemp 		= jsTemp.getRecord(0);
		
						szYD_TC_SCH_ID	= commUtils.trim(jrTemp.getFieldString("YD_TCAR_SCH_ID")); 
						
						szMsg	= "대차 재료 번호[" + srSstlNo + "] 대차 스케쥴 [" + szYD_TC_SCH_ID + "] 대차 하차 개시(D)";
						commUtils.printLog(logId, szMsg, "SL");

						this.TcProgStatChg(logId, sModifier, szYD_TC_SCH_ID, "D", srydEqpId, ydWbookId);

					}					
				}				
			}			
			else if ("TC".equals(ydDnWoLoc.substring(2, 4))) {
// 2026.02.26 권상 실적 처리시 권하 위치가 대차 이면 대차 상태 변경				
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setResultCode(logId);	//Log ID
				recPara.setResultMsg(methodNm);	//Log Method Name
				
				recPara.setField("YD_CRN_SCH_ID", srydCrnSchId	); 

				jsChk = commDao.select(recPara, "com.inisteel.cim.ys.sbr.dao.getCrSchMatl", logId, methodNm, "크레인 스케쥴 재료 조회 "); 
				if (jsChk.size() > 0) {
					jrChk = jsChk.getRecord(0);

					String srSstlNo = commUtils.trim(jrChk.getFieldString("SSTL_NO")); 	// 재료 번호

					JDTORecordSet 	jsTemp 			= null;
					JDTORecord 		jrTemp 			= null;
					
					String 			szYD_TC_SCH_ID 	= "";
					
				    int 			intRtnVal		= 0 ;
					
					jrParam.setField("SSTL_NO"		, srSstlNo	); // 재료번호
					
					jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.sbr.dao.getTcarSchMatl", logId, methodNm, "재료번호로 대차 스케줄 조회");
					
					if (jsTemp != null && jsTemp.size() > 0) {
						
						jrTemp 		= jsTemp.getRecord(0);
		
						szYD_TC_SCH_ID	= commUtils.trim(jrTemp.getFieldString("YD_TCAR_SCH_ID")); 

			    		szMsg	= "대차 재료 번호[" + srSstlNo + "] 대차 스케쥴 [" + szYD_TC_SCH_ID + "] 대차 상차 개시(4)";
			    		commUtils.printLog(logId, szMsg, "SL");
						
		    	    	this.TcProgStatChg(logId, sModifier, szYD_TC_SCH_ID, "4", srydEqpId, ydWbookId);
						
					} else {
////////////////////////////////////////////////////////////////////////////
// 2026.04.09 권상 실적 처리에서 권하 위치가 대차 인데 대차 스케쥴 없으면 신규 생성
						JDTORecord jrParam1 = JDTORecordFactory.getInstance().create();
						
			    		szMsg	= "크레인 스케쥴 ID [" + srydCrnSchId + "] 대차 스케쥴 없어서 신규 생성";
			    		commUtils.printLog(logId, szMsg, "SL");
						
						// 대차 스케쥴 ID 신규 작성 
						szYD_TC_SCH_ID = commDao.getSeqId(logId, methodNm, "TcarSch" );
						
						/**********************************************************
						* 대차 이송재료 등록
						**********************************************************/
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setResultCode(logId);	//Log ID
						recPara.setResultMsg(methodNm);	//Log Method Name
						
						recPara.setField("YD_CRN_SCH_ID", srydCrnSchId	); 

						jsTemp = commDao.select(recPara, "com.inisteel.cim.ys.sbr.dao.getCrSchMatl", logId, methodNm, "크레인 스케쥴 재료 조회 "); 
						for (int Loop_i = 1; Loop_i <= jsTemp.size(); Loop_i++) {
							jsTemp.absolute(Loop_i);
							
							jrTemp = JDTORecordFactory.getInstance().create();
							jrTemp.setRecord(jsTemp.getRecord());

							srSstlNo 	= commUtils.trim(jrTemp.getFieldString("SSTL_NO")	);
							

							jrParam1 = JDTORecordFactory.getInstance().create();
							jrParam1.setResultCode(logId);							// Log ID
							jrParam1.setResultMsg(methodNm);							// Log Method Name

							jrParam1.setField("YD_TCAR_SCH_ID",		szYD_TC_SCH_ID			);	// 야드 대차스케쥴ID
							jrParam1.setField("SSTL_NO", 			srSstlNo   				); 	// 재료번호
							jrParam1.setField("YS_STK_SEQ_NO", 		String.valueOf(Loop_i)	); 	// 야드적치SEQ
							jrParam1.setField("MODIFIER", 			sModifier  				); 	// 수정자							

							intRtnVal = commDao.insert(jrParam1, "com.inisteel.cim.ys.sbr.dao.insTcarMtlSbr", logId, methodNm, "소형 야드 대차 이송재료 등록"); 

							szMsg	= methodNm + " 소형 야드 대차 이송재료 등록 - 반환값 : " + intRtnVal;
							commUtils.printLog(logId, szMsg, "SL");
							
						}		

						/**********************************************************
						* 대차 스케줄 등록
						**********************************************************/
						jrParam1 = JDTORecordFactory.getInstance().create();

						jrParam1.setField("YD_TCAR_SCH_ID",    	szYD_TC_SCH_ID				);		// 야드대차스케쥴ID
						jrParam1.setField("YD_EQP_ID",        	ydDnWoLoc.substring(0, 6)	);		// 야드설비ID(권하위치 대차설비ID)
						jrParam1.setField("YD_EQP_WRK_STAT",  	"L"							);		// 야드설비작업상태( U : 공차, L : 영차)
						jrParam1.setField("YD_CARLD_LEV_LOC", 	"A"			 				);		// 야드상차출발위치
						jrParam1.setField("YD_CARLD_STOP_LOC", 	"A"			 				);		// 야드상차정지위치
						jrParam1.setField("YD_CARLD_SCH_REQ_GP", "6"			 			);		// 야드상차스케쥴요청구분(6 : 공대차도착)
						jrParam1.setField("YD_CARUD_STOP_LOC", 	"B"			 				);		// 야드하차정지위치
						jrParam1.setField("YD_CAR_PROG_STAT", 	"4"							);		// 야드차량진행상태 (4 : 상차개시)
						jrParam1.setField("MODIFIER",			sModifier					);		// 등록자
						
						intRtnVal = commDao.update(jrParam1, "com.inisteel.cim.ys.sbr.dao.updTcarSchInsSchSbr", logId, methodNm, "소형 야드 대차 스케줄 등록 및 갱신");
						
						if( intRtnVal <= 0 ){
							szMsg	= methodNm + " 소형 야드 대차 스케줄 등록 및 갱신 시 오류발생 - 반환값 : " + intRtnVal;
							commUtils.printLog(logId, szMsg, "SL");
						} else {
							szMsg	= methodNm + " 소형 야드 대차 스케줄 등록 및 갱신 완료 - 반환값 : " + intRtnVal;
							commUtils.printLog(logId, szMsg, "SL");
						}
						
//			    		szMsg	= "대차 재료 번호[" + srSstlNo + "] 대차 스케쥴 [" + szYD_TC_SCH_ID + "] 대차 상차 개시(4)";
//			    		commUtils.printLog(logId, szMsg, "SL");
//						
//		    	    	this.TcProgStatChg(logId, sModifier, szYD_TC_SCH_ID, "4", srydEqpId, ydWbookId);
						
////////////////////////////////////////////////////////////////////////////

					}
					
					
				}			
			}
			
			/**********************************************************
			* 5. 권하지시위치가 차량(상차)
			*    차량스케줄 야드차량진행상태가 상차도착(2) 또는 상차검수(3) 이면
			* 5.1 구내운송 소재차량상차개시(YSTSJ007) 전송
			*   - 야드차량사용구분이 구내운송(L)
			* 5.2 차량스케줄 수정
			*   - 야드설비작업상태(U:공차), 야드차량진행상태(4:상차개시) 등 수정
			**********************************************************/
			if ("TR".equals(ydDnWoLoc.substring(2, 4))) {
				// 차량하차스케줄 정보 조회
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID" , 	ydWbookId     ); 
				recPara.setField("YD_CRN_SCH_ID" , 	srydCrnSchId ); 

//				jsChk = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getN7YSL005CarSchLd", logId, methodNm, "크레인권상실적 상차 차량스케줄 조회 "); 
				jsChk = commDao.select(recPara, "com.inisteel.cim.ys.sbr.dao.getN7YSL305CarSchLd", logId, methodNm, "크레인 상차 차량스케줄 조회 "); 
		    	
				if (jsChk.size() > 0) {
					jrChk = jsChk.getRecord(0);
				
					//상차개시 전문
					JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name
					
					if("L".equals(jrChk.getFieldString("YD_CAR_USE_GP"))) {
						//구내운송 소재차량상차개시
						jrYdMsg.setField("JMS_TC_CD"         , "YSTSJ007"												); 	// JMSTC코드
						jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDt													); 	// JMSTC생성일시
						jrYdMsg.setField("TRN_EQP_CD"        , commUtils.trim(jrChk.getFieldString("TRN_EQP_CD"    ))	); 	// 운송장비코드
						jrYdMsg.setField("SPOS_WLOC_CD"      , commUtils.trim(jrChk.getFieldString("SPOS_WLOC_CD"  ))	); 	// 발지개소코드
						jrYdMsg.setField("SPOS_YD_PNT_CD"    , commUtils.trim(jrChk.getFieldString("SPOS_YD_PNT_CD"))	); 	// 발지야드포인트코드
						jrYdMsg.setField("ARR_WLOC_CD"       , commUtils.trim(jrChk.getFieldString("ARR_WLOC_CD"   ))	); 	// 착지개소코드
						jrYdMsg.setField("TRN_WRK_ST_DT"     , currDt													); 	// 운송작업시작일시

						//전송할 전문에 추가
						jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
					}
					

					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("MODIFIER" 	, sModifier												); 
					recPara.setField("ARR_WLOC_CD"  , commUtils.trim(jrChk.getFieldString("ARR_WLOC_CD"))	); // 착지개소코드
					recPara.setField("YD_WBOOK_ID" 	, ydWbookId												); 
					recPara.setField("WR_DT" 		, commUtils.getDateTime14()								); 
					recPara.setField("YD_CAR_SCH_ID", commUtils.trim(jrChk.getFieldString("YD_CAR_SCH_ID"))	); // 야드차량스케쥴ID
					// 상차 차량스케줄 야드설비작업상태, 야드차량진행상태, 야드상차작업예약ID, 착지개소코드 등 수정
					

					commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CarSchLd", logId, methodNm, "상차 차량스케줄 수정 ");
				}
			}
	
			/**********************************************************
			* 7. 설비, 크레인스케쥴, 적치단, 적치Bed 수정
			* 7.1 설비 야드설비상태(권상중) 수정
			* 7.2 적치단
			*   - 크레인 재료정보 등록
			*   - 권상위치 재료정보 삭제
			* 7.3 적치Bed 야드적치Bed입출고상태(완산Bed->입출고가능) 수정
			* 7.4 크레인스케쥴 권상실적 수정
			**********************************************************/
			//야드권상작업수행구분
			String ydUpWrkActGp = srydEqpWrkMode;

			//설비
			jrParam.setField("YD_EQP_ID"       , srydEqpId     	); // 야드설비ID
			jrParam.setField("YD_EQP_STAT"     , "2"         	); // 야드설비상태(권상중)
			//크레인스케쥴
			jrParam.setField("YD_UP_CMPL_DT"   , currDt      	); // 야드권상완료일시
			jrParam.setField("YD_UP_WR_LOC"    , srydUpWrLoc   	); // 야드권상실적위치
			jrParam.setField("YD_UP_WR_LAYER"  , srydUpWrLayer 	); // 야드권상실적단
			jrParam.setField("YD_UP_WRK_ACT_GP", ydUpWrkActGp	); // 야드권상작업수행구분
			jrParam.setField("YD_UP_WR_XAXIS"  , srydCrnXaxis  	); // 야드권상실적X축
			jrParam.setField("YD_UP_WR_YAXIS"  , srydCrnYaxis  	); // 야드권상실적Y축
			jrParam.setField("YD_UP_WR_ZAXIS"  , srydCrnZaxis  	); // 야드권상실적Z축

			//설비(야드설비상태) 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatEqp", logId, methodNm, "설비상태 수정");
			
			//적치단(크레인 및 권상위치) 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005StkLyrblbt", logId, methodNm, "적치단(크레인 및 권상위치) 수정");
			
			//적치Bed(완산Bed->입출고가능) 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005StkBedF", logId, methodNm, "적치Bed(완산Bed->입출고가능) 수정");
			
			//크레인스케쥴 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CrnSch", logId, methodNm, "크레인스케쥴 수정");

			
			resMsg.setField("YD_UP_WR_LOC", srydUpWrLoc); // 야드권상실적위치


			//크레인작업실적응답 전송
			if (resYn) {
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); // 야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ""    ); // 야드L3MESSAGE
				
		        szMsg = "크레인작업실적응답 >>>>>>> " + resMsg.toString();
				commUtils.printLog(logId, szMsg, "");
				
				jrRtn = commUtils.addSndData(jrRtn, this.getYSN7L304(resMsg));
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			if (resYn) {
				try {
					
					//PIDEV_F : 정상SET후  ERROR 발생한 경우								
					if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {
						resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"			);	//야드L3처리결과코드(Error)
						resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"	); 	//야드L3MESSAGE(Error)
					}
					
					// 크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "YsCommEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { this.getYSN7L304(resMsg) });
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인권하실적(N7YSL306)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN7YSL306(JDTORecord rcvMsg) throws DAOException {
        String szMsg	= "";
		String methodNm = "크레인권하실적[SbrYsL2RcvSeEJB.rcvN7YSL306]";
		String logId 	= commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		
		JDTORecord resMsg = JDTORecordFactory.getInstance().create(); // 크레인작업실적응답 전문 생성용
		boolean resYn = true;	// 크레인작업실적응답 전문 전송여부

		try {

			commUtils.printLog(logId, methodNm, "S+");

			szMsg = rcvMsg.toString();
			commUtils.printLog(logId, szMsg, "");

			/*
			7	YD_EQP_ID			야드설비ID			CHAR	 6
			8	YD_EQP_WRK_MODE		야드설비작업Mode		CHAR	 1
			9	YD_WRK_PROG_STAT	야드작업진행상태		CHAR	 1
			10	YD_SCH_CD			야드스케쥴코드		CHAR	 8
			11	YD_CRN_SCH_ID		야드크레인스케쥴ID		CHAR	18
			12	YS_DN_WR_LOC		특수강야드권하실적위치	CHAR	 8
			13	YS_DN_WR_LAYER 		특수강야드권하실적단	CHAR	 2
			14	YD_CRN_XAXIS		야드크레인X축		NUMBER	 7
			15	YD_CRN_YAXIS		야드크레인Y축		NUMBER	 5
			16	YD_CRN_ZAXIS		야드크레인Z축		NUMBER	 5
			17	YD_EQP_WRK_MODE2	야드설비작업Mode2	CHAR	 1
			*/

			// 수신 항목 값
			String msgId         	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String srydEqpId       	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); // 야드설비ID
			String srydEqpWrkMode  	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE" )); // 야드설비작업Mode("1": On-Line, "2": Off-Line )
			String srydWrkProgStat 	= commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")); // 야드작업진행상태(4:권하완료, 5:강제권하)
			String srydSchCd       	= commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )); // 야드스케쥴코드
			String srydCrnSchId    	= commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )); // 야드크레인스케쥴ID
			String srydDnWrLoc     	= commUtils.trim(rcvMsg.getFieldString("YS_DN_WR_LOC"    )); // 야드권하실적위치
			String srydDnWrLayer   	= commUtils.trim(rcvMsg.getFieldString("YS_DN_WR_LAYER"  )); // 야드권하실적단
			String srydCrnXaxis    	= commUtils.trim(rcvMsg.getFieldString("YD_CRN_XAXIS"    )); // 야드크레인X축
			String srydCrnYaxis    	= commUtils.trim(rcvMsg.getFieldString("YD_CRN_YAXIS"    )); // 야드크레인Y축
			String srydCrnZaxis    	= commUtils.trim(rcvMsg.getFieldString("YD_CRN_ZAXIS"    )); // 야드크레인Z축			
			String sModifier      	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"        )); // 수정자(Backup Only)
			String ydWbookId     	= ""; // 야드작업예약ID
			String ydUpWrLoc     	= ""; // 야드권상실적위치
			
			if ("".equals(sModifier)) { sModifier = msgId; }
			
			methodNm = msgId.substring(0, 2) + methodNm;

// Seq 정순,역순 등록
			String szREVERSE_YN	= "N";

			szMsg = "\n\t YD_EQP_ID   			: " 	+ srydEqpId 
				  + "\n\t YD_EQP_WRK_MODE      	: " 	+ srydEqpWrkMode 
				  + "\n\t YD_WRK_PROG_STAT     	: " 	+ srydWrkProgStat 
				  + "\n\t YD_SCH_CD   			: " 	+ srydSchCd 
				  + "\n\t YD_CRN_SCH_ID   		: " 	+ srydCrnSchId 
				  + "\n\t YS_DN_WR_LOC   		: " 	+ srydDnWrLoc 
				  + "\n\t YS_DN_WR_LAYER   		: " 	+ srydDnWrLayer 
				  + "\n\t YD_CRN_XAXIS   		: " 	+ srydCrnXaxis 
				  + "\n\t YD_CRN_YAXIS   		: " 	+ srydCrnYaxis 
				  + "\n\t YD_CRN_ZAXIS   		: " 	+ srydCrnZaxis 
				  + "\n\t MODIFIER     			: " 	+ sModifier 
				  ;

			commUtils.printLog(logId, szMsg, "");

			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			JDTORecord jrRtn = null;	// 전문 Return
			String ydL3HdRsCd = "";		// 야드L3처리결과코드
			String ydL3Msg    = ""; 	// 야드L3MESSAGE

			// 크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);	// Log ID
			resMsg.setResultMsg(methodNm);	// Log Method Name
			resMsg.setField("YD_EQP_ID"       , srydEqpId		); 	// 야드설비ID
			resMsg.setField("YD_WRK_PROG_STAT", srydWrkProgStat	); 	// 야드작업진행상태
			resMsg.setField("YD_SCH_CD"       , srydSchCd		); 	// 야드스케쥴코드
			resMsg.setField("YD_CRN_SCH_ID"   , srydCrnSchId	); 	// 야드크레인스케쥴ID
			resMsg.setField("YD_L3_HD_RS_CD"  , "DN99"			); 	// 야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:권하실적 수신처리"	); 	// 야드L3MESSAGE(Error)
			resMsg.setField("YD_L2_WR_GP", 		"D"				); 	// 야드L2실적구분(권하실적)
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(srydEqpId)) {
				ydL3HdRsCd = "DN01";
				ydL3Msg    = "오류:설비ID 없음";
			} else if (srydEqpId.length() < 6 || !srydEqpId.startsWith("GD")) {
				ydL3HdRsCd = "DN02";
				ydL3Msg    = "오류:설비ID[" + srydEqpId + "] 이상";
			} else if ("".equals(srydCrnSchId)) {
				ydL3HdRsCd = "DN03";
				ydL3Msg    = "오류:크레인스케쥴ID 없음";
			} else if ("".equals(srydDnWrLoc)) {
				ydL3HdRsCd = "DN04";
				ydL3Msg    = "오류:권하실적위치 없음";
			} else if ("XX".equals(srydDnWrLoc.substring(2, 4))) {
				ydL3HdRsCd = "DN04";
				ydL3Msg    = "오류:권하실적위치 이상";
			}

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); // 야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); // 야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			/**********************************************************
			* 2. 크레인스케쥴ID Check
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);		// Log ID
			jrParam.setResultMsg(methodNm);		// Log Method Name
			
			jrParam.setField("YD_CRN_SCH_ID", srydCrnSchId); 	// 야드크레인스케쥴ID
			jrParam.setField("MODIFIER"     , sModifier  ); 	// 수정자
			
			JDTORecord jrParam2 = JDTORecordFactory.getInstance().create();
			jrParam2.setResultCode(logId);		// Log ID
			jrParam2.setResultMsg(methodNm);	// Log Method Name
			
			jrParam2.setField("YD_CRN_SCH_ID", srydCrnSchId); 	// 야드크레인스케쥴ID
			jrParam2.setField("MODIFIER"     , sModifier  ); 	// 수정자
			
			JDTORecord jrChk = null;

			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.sbr.dao.getStatCrnSchSbr", logId, methodNm, "크레인스케줄상태 조회");
			
			if (jsChk.size() == 0) {
				// 크레인스케쥴 Table 존재유무 Check
				ydL3HdRsCd = "DN11";
				ydL3Msg = "오류:없거나 종료된 크레인스케쥴ID";
			} else {
				
				// 크레인스케쥴 Table 야드작업진행상태 Check
				jrChk = jsChk.getRecord(0);
				ydWbookId       = commUtils.trim(jrChk.getFieldString("YD_WBOOK_ID"     )); // 야드작업예약ID
//				ydSchCd         = commUtils.trim(jrChk.getFieldString("YD_SCH_CD"       )); // 야드스케쥴코드
				ydUpWrLoc       = commUtils.trim(jrChk.getFieldString("YS_UP_WR_LOC"    )); // 야드권상실적위치
				String tmpStat  = commUtils.trim(jrChk.getFieldString("YD_WRK_PROG_STAT")); // 야드작업진행상태
				String tmpEqpId = commUtils.trim(jrChk.getFieldString("YD_EQP_ID"       )); // 야드설비ID
				
// Seq 정순 역순 등록
				szREVERSE_YN 	= commUtils.trim(jrChk.getFieldString("REVERSE_YN"  ));

// 권하 실적 권하 위치 적치열, 단 체크
				String tmpDnLoc  	= commUtils.trim(jrChk.getFieldString("YS_DN_WO_LOC"    )); 	// 특수강야드권하지시위치
				String tmpDnLayer  	= commUtils.trim(jrChk.getFieldString("YS_DN_WO_LAYER"  )); 	// 특수강야드권하지시단
				if (!srydDnWrLayer.equals(tmpDnLayer) || !srydDnWrLoc.equals(tmpDnLoc)) {
					
					szMsg	= "오류:지시 실적 권하위치,단 다름"
					  +	"\n\t 실적 권하 위치/단" 
					  +	"\n\t YS_DN_WR_LOC   			: " 	+ srydDnWrLoc 
					  + "\n\t YS_DN_WR_LAYER      		: " 	+ srydDnWrLayer 
					  + "\n\t 지시 권하 위치/단"
					  + "\n\t YS_DN_WO_LOC     			: " 	+ tmpDnLoc 
					  + "\n\t YS_DN_WO_LAYER   			: " 	+ tmpDnLayer 
					  ;

					commUtils.printLog(logId, szMsg, "");
					
					// 권하실적 권하위치, 단 체크
					ydL3HdRsCd 	= "DN11";
					ydL3Msg 	= "오류:지시 실적 권하위치,단 다름.";
				}
				
// 권하 실적 주행, 횡행이 크레인 지시 허용오차 내인지 체크 
// 차량 (TR), 트랜스퍼(TF)는 주행, 횡행 좌표 체크 하지 않음 
				if(!"TR".equals(srydDnWrLoc.substring(2, 4)) && !"TF".equals(srydDnWrLoc.substring(2, 4))) {

					String szYD_DN_X_MIN = commUtils.trim(jrChk.getFieldString("YD_DN_X_MIN")); // 권하 주행 MIN
					String szYD_DN_X_MAX = commUtils.trim(jrChk.getFieldString("YD_DN_X_MAX")); // 권하 주행 MAX
					String szYD_DN_Y_MIN = commUtils.trim(jrChk.getFieldString("YD_DN_Y_MIN")); // 권하 횡행 MIN
					String szYD_DN_Y_MAX = commUtils.trim(jrChk.getFieldString("YD_DN_Y_MAX")); // 권하 횡행 MAX

					szMsg = "****** 권하 실적 주행, 횡행 체크 "
						  + "\n\t 권하 실적 주행           : " + srydCrnXaxis 
						  + "\n\t 권하 실적 주행 MIN : " + szYD_DN_X_MIN 
						  + "\n\t 권하 실적 주행 MAx : " + szYD_DN_X_MAX 
						  + "\n\t 권하 실적 횡행           : " + srydCrnYaxis
						  + "\n\t 권하 실적 횡행 MIN : " + szYD_DN_Y_MIN 
						  + "\n\t 권하 실적 횡행 MAx : " + szYD_DN_Y_MAX 
						  ;
					
					commUtils.printLog(logId, szMsg, "");

					if (srydCrnXaxis == null || "".equals(srydCrnXaxis)) {
						srydCrnXaxis = "0"; 
					}

					if(Integer.parseInt(szYD_DN_X_MIN) > Integer.parseInt(srydCrnXaxis) || Integer.parseInt(srydCrnXaxis) > Integer.parseInt(szYD_DN_X_MAX)) {
						// 권하실적 권하위치 주행 체크
						ydL3HdRsCd 	= "DN11";
						ydL3Msg 	= "오류:주행 허용 오차를 벗어남.";
					}

					szMsg = "****** 권하 실적 횡행 체크";
					commUtils.printLog(logId, szMsg, "");

					if (srydCrnYaxis == null || "".equals(srydCrnYaxis)) {
						srydCrnYaxis = "0"; 
					}

					if(Integer.parseInt(szYD_DN_Y_MIN) > Integer.parseInt(srydCrnYaxis) || Integer.parseInt(srydCrnYaxis) > Integer.parseInt(szYD_DN_Y_MAX)) {
						// 권하실적 권하위치 횡행 체크
						ydL3HdRsCd 	= "DN11";
						ydL3Msg 	= "오류:횡행 허용 오차를 벗어남.";
					}
				}
					
				
				if (!"2".equals(tmpStat) && !"3".equals(tmpStat)) {
					ydL3HdRsCd 	= "DN12";
					ydL3Msg 	= "오류:현재 작업진행상태[" + tmpStat + "] 이상";
				} else if (!srydEqpId.equals(tmpEqpId)) {
					ydL3HdRsCd 	= "DN13";
					ydL3Msg 	= "오류:현재 설비ID와[" + tmpEqpId + "] 다름";
				}
			}
			
			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); // 야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); // 야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			// 조회 Parameter
			jrParam.setField("YD_WBOOK_ID"  , 	ydWbookId					); // 야드작업예약ID
			jrParam.setField("YD_STK_COL_GP", 	srydDnWrLoc.substring(0, 6)	); // 야드적치열구분
			jrParam.setField("YD_STK_BED_NO", 	srydDnWrLoc.substring(6, 8)	); // 야드적치Bed번호
			
			// 설비
			jrParam.setField("YD_EQP_ID"  , 	srydEqpId					); // 야드설비ID(크레인)
			jrParam.setField("YD_EQP_STAT", 	"4"    						); // 야드설비상태(권하완료)
			
			// 실제 야드권하실적단 및 기타 정보 조회
			String wbCmplYn = ""; 			// 작업예약완료여부
			String ydStkBedUseCd = ""; 		// 적치대(P),우물정자(V1) 구분
			boolean chgDnWrLayer = false; 	// 권하위치 적치단 변경여부
			
			jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getWbCmplYn", logId, methodNm, "작업예약 완료여부 조회");
			
			if (jsChk.size() > 0) {

				jrChk = jsChk.getRecord(0);
				wbCmplYn 		= commUtils.trim(jrChk.getFieldString("WB_CMPL_YN"));
				ydStkBedUseCd 	= commUtils.trim(jrChk.getFieldString("YD_STKBED_USG_CD"));
				commUtils.printLog(logId, "작업예약[" + ydWbookId + "] 완료여부 : " + wbCmplYn, "SL");
				
			} else {
				resMsg.setField("YD_L3_HD_RS_CD", "DN14"); 					// 야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , "오류:작업예약 완료여부 조회 오류"); 	// 야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}


			// 야드권하작업수행구분
			String ydDnWrkActGp = srydEqpWrkMode;
			
			String currDt = commUtils.getDateTime14(); //현재시각

			//크레인스케쥴
			jrParam.setField("YD_DN_CMPL_DT"   , currDt      	); // 야드권하완료일시
			jrParam.setField("YD_DN_WR_LOC"    , srydDnWrLoc   	); // 야드권하실적위치
			jrParam.setField("YD_DN_WR_LAYER"  , srydDnWrLayer 	); // 야드권하실적단
			jrParam.setField("YD_DN_WRK_ACT_GP", ydDnWrkActGp	); // 야드권하작업수행구분
			jrParam.setField("YD_DN_WR_XAXIS"  , srydCrnXaxis  	); // 야드권하실적X축
			jrParam.setField("YD_DN_WR_YAXIS"  , srydCrnYaxis  	); // 야드권하실적Y축
			jrParam.setField("YD_DN_WR_ZAXIS"  , srydCrnZaxis  	); // 야드권하실적Z축
			jrParam.setField("WR_DT"           , currDt      	); // 실적일시
			jrParam.setField("UP_DN_GP"        , "D"         	); // 권상권하구분(권하)

			
			/**********************************************************
			* 3. 전문 전송
			* 3.1 스케쥴 코드에 따른 전문 전송
			*     대차상차실적(대차 이동 요구)
			**********************************************************/
			
			if("TC".equals(srydDnWrLoc.substring(2, 4))) { 

////////////////////////////////////////////////////////////////////////////
//2026.02.26 권하 위치가 대차 이면 대차 스케쥴 변경
////////////////////////////////////////////////////////////////////////////
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setResultCode(logId);	//Log ID
				recPara.setResultMsg(methodNm);	//Log Method Name
				
				recPara.setField("YD_CRN_SCH_ID", srydCrnSchId	); 

				jsChk = commDao.select(recPara, "com.inisteel.cim.ys.sbr.dao.getCrSchMatl", logId, methodNm, "크레인 스케쥴 재료 조회 "); 
		    	
				if (jsChk.size() > 0) {
					jrChk = jsChk.getRecord(0);

					String srSstlNo = commUtils.trim(jrChk.getFieldString("SSTL_NO")); 	// 재료 번호

					JDTORecordSet 	jsTemp 			= null;
					JDTORecord 		jrTemp 			= null;
					
					String 			szYD_TC_SCH_ID 	= "";
					
				    int 			intRtnVal		= 0 ;
					
					jrParam.setField("SSTL_NO"		, srSstlNo	); // 재료번호
					
					jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.sbr.dao.getTcarSchMatl", logId, methodNm, "재료번호로 대차 스케줄 조회");
					
					if (jsTemp != null && jsTemp.size() > 0) {
						
						jrTemp 		= jsTemp.getRecord(0);
		
						szYD_TC_SCH_ID	= commUtils.trim(jrTemp.getFieldString("YD_TCAR_SCH_ID")); 

						szMsg	= "대차 재료 번호[" + srSstlNo + "] 대차 스케쥴 [" + szYD_TC_SCH_ID + "] 대차 상차 완료(5)";
						commUtils.printLog(logId, szMsg, "SL");

						this.TcProgStatChg(logId, sModifier, szYD_TC_SCH_ID, "5", srydEqpId, ydWbookId);

					} 			
					
				}

				
				// 대차 저장위치 권하 완료시(GDTC04, GDTC05, GDTC06)
				// 대차실적(YSN7L311) 송신
				// 2026.01.28 L2 요청으로 대차 상차,하차 실적을 대차 실적으로 통합
				jrParam.setField("YD_EQP_WRK_STAT"		, "L"         	); 		// 적재상태(상하차구분 L : 상차,  U : 하차)
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN7L311", jrParam));

				// 대차이동요구(YSN7L323) A(소형야드) -> B(정정) 전송 			
				jrParam2.setField("YD_EQP_ID"   		, srydDnWrLoc.substring(0, 6)   ); 	// 대차 설비 ID
				jrParam2.setField("YD_EQP_WRK_STAT"		, "L"         					);	// 적재상태(상하차구분 L : 영차,  U : 공차)
				jrParam2.setField("ST_LOC"   			, "A"      						); 	// 출발동 구분 A : 소형야드, B : 정정
				jrParam2.setField("END_LOC"   			, "B"      						); 	// 도착동 구분 A : 소형야드, B : 정정
				
				// 2026.01.28 L2 요청으로 YSN7L323 -> YSN7L312 전문ID 변경
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN7L312", jrParam2));
		    	
			} else if("TF".equals(srydDnWrLoc.substring(2, 4))) {
				
////////////////////////////////////////////////////////////////////////
// 2026.04.06 권하 위치가 TF(GDTF11, GDTF12, GDTF21, GDTF22) 이면 YSM5L302(재정렬재 ReTracking 정보) 송신
				szMsg	= "****** YSM5L302(재정렬재 ReTracking 정보) 송신";
				commUtils.printLog(logId, szMsg, "SL");
				
				jrParam2.setField("YD_EQP_ID"			, srydDnWrLoc.substring(0, 6)	); 	// TF 설비ID
				jrParam2.setField("YD_STK_BED_NO"		, "01"   						); 	// Bed번호
				jrParam2.setField("YD_CRN_SCH_ID"		, srydCrnSchId					); 	// 야드크레인스케쥴ID
				
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSM5L302", jrParam2));
////////////////////////////////////////////////////////////////////////
				
			}
			
			
// 2026.04.10 권하 위치
			szMsg	= "****** 권하 위치 [" + srydDnWrLoc + "] - [" + srydDnWrLoc.substring(2, 4) + "]";
			commUtils.printLog(logId, szMsg, "SL");
			
			
			
			/**********************************************************
			* 4. 권하실적위치가 차량(상차)
			* 4.1 차량이송재료 등록
			* 4.2 차량스케줄 야드차량진행상태, 야드설비작업상태(L:영차), 야드설비작업매수 등 수정
			*   - 직상차(차량 스케줄코드가 아님) : 야드차량진행상태(4:상차개시)
			*   - 상차완료(차량 스케줄코드가 아님) : 야드차량진행상태(5:상차완료)
			*   - 야드차량사용구분이 구내운송(L)이고 상차완료이면
			*     . 마지막 크레인스케줄 이면
			* 4.3 공통 처리 : 야드차량사용구분이 구내운송(L)이고 상차완료이면
			* 4.3.2 소재이송지시 야드재료예정저장From위치코드, 이송상차일자 수정
			* 4.3.3 저장품 목표야드, 목표동, 목표행선 등을 수정
			* 4.4 야드차량사용구분이 출하차량(G)
			* 4.4.1 출하관리 일품출하상차실적(YSDSJ007) 전송
			* 4.4.2 출하관리 출하상차완료(YSDSJ008) 전송
			*     - 상차완료(마지막 크레인스케줄)이면
			**********************************************************/
			//차량상차완료여부(소재이송지시 수정 및 공통Table 소재이송일시 수정)
			String carLdCmplYn = "N";
			String ydCarUseGp  = "";
			String ydCarSchId  = "";
			if("TR".equals(srydDnWrLoc.substring(2, 4))) {
				//상차 차량스케줄  조회
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID" , srydCrnSchId     ); 
				recPara.setField("YS_STK_COL_GP" , srydDnWrLoc.substring(0, 6) ); 

				jsChk = commDao.select(recPara, "com.inisteel.cim.ys.sbr.dao.getN7YSL306CarSchLd", logId, methodNm, "상차 차량스케줄 조회 ");

				if (jsChk.size() > 0) {
					jrChk = jsChk.getRecord(0);

					ydCarSchId  = commUtils.trim(jrChk.getFieldString("YD_CAR_SCH_ID"));
					ydCarUseGp 	= commUtils.trim(jrChk.getFieldString("YD_CAR_USE_GP")); //야드차량사용구분
					carLdCmplYn = commUtils.trim(jrChk.getFieldString("CAR_LD_CMPL_YN")); //차량상차완료여부
					
////////////////////////////////////////////////////////////////////////
// 차량 도착전 권상된 경우 권하 처리시 상차 개시 전송 START

					szMsg = "차량 도착전 권상된 경우 권하 처리시 상차 개시 전송 ";
					commUtils.printLog(logId, szMsg, "");
					
					String ydCarProgStat = commUtils.trim(jrChk.getFieldString("YD_CAR_PROG_STAT")); 	// 야드차량진행상태

					szMsg = "YD_CAR_PROG_STAT [" + ydCarProgStat + "]";
					commUtils.printLog(logId, szMsg, "");
					
					// 2 : 상차도착, 3 : 상차검수
					if ("2".equals(ydCarProgStat) || "3".equals(ydCarProgStat)) {
// 야드차량진행상태 2,3 인 경우 차량 도착전 권상 처리 된 경우 상차 개시 전문 편집 및  착지 개소 코드 UPDATE

						szMsg = "권하 실적에서 상차 개시 전문 편집 및  착지 개소 코드 UPDATE";
			      		commUtils.printLog(logId, szMsg, "");

						
						// 상차 차량스케줄 조회
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("YD_WBOOK_ID", 	ydWbookId);
						recPara.setField("YD_CRN_SCH_ID", 	srydCrnSchId);
						
//						jsChk = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getN7YSL005CarSchLd", logId, methodNm, "크레인권상실적 상차 차량스케줄 조회 "); 
						jsChk = commDao.select(recPara, "com.inisteel.cim.ys.sbr.dao.getN7YSL305CarSchLd", logId, methodNm, "크레인 상차 차량스케줄 조회 "); 

						if (jsChk.size() > 0) {
							jrChk = jsChk.getRecord(0);

							// 상차개시 전문
							JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
							jrYdMsg.setResultCode(logId); // Log ID
							jrYdMsg.setResultMsg(methodNm); // Log Method Name

							if ("L".equals(jrChk.getFieldString("YD_CAR_USE_GP"))) {
								// 구내운송 소재차량상차개시
								jrYdMsg.setField("JMS_TC_CD", 			"YSTSJ007"												);	// JMSTC코드
								jrYdMsg.setField("JMS_TC_CREATE_DDTT", 	currDt													); 	// JMSTC생성일시
								jrYdMsg.setField("TRN_EQP_CD", 			commUtils.trim(jrChk.getFieldString("TRN_EQP_CD"))		); 	// 운송장비코드
								jrYdMsg.setField("SPOS_WLOC_CD", 		commUtils.trim(jrChk.getFieldString("SPOS_WLOC_CD"))	); 	// 발지개소코드
								jrYdMsg.setField("SPOS_YD_PNT_CD", 		commUtils.trim(jrChk.getFieldString("SPOS_YD_PNT_CD"))	); 	// 발지야드포인트코드
								jrYdMsg.setField("ARR_WLOC_CD", 		commUtils.trim(jrChk.getFieldString("ARR_WLOC_CD"))		); 	// 착지개소코드
								jrYdMsg.setField("TRN_WRK_ST_DT", 		currDt													); 	// 운송작업시작일시

								// 전송할 전문에 추가
								jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
							}


							recPara = JDTORecordFactory.getInstance().create();
							recPara.setField("MODIFIER", 		sModifier												);
							recPara.setField("ARR_WLOC_CD", 	commUtils.trim(jrChk.getFieldString("ARR_WLOC_CD"))		); 	// 착지개소코드
							recPara.setField("YD_WBOOK_ID", 	ydWbookId												);
							recPara.setField("WR_DT", 			commUtils.getDateTime14()								);
							recPara.setField("YD_CAR_SCH_ID", 	commUtils.trim(jrChk.getFieldString("YD_CAR_SCH_ID"))	); 	// 야드차량스케쥴ID
							// 상차 차량스케줄 야드설비작업상태, 야드차량진행상태, 야드상차작업예약ID, 착지개소코드 등 수정

							commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CarSchLd", logId, methodNm, "상차 차량스케줄 수정 ");
						}
						
					}
// 차량 도착전 권상된 경우 권하 처리시 상차 개시 전송 END
////////////////////////////////////////////////////////////////////////
						
					
					
					//차량이송재료(TB_YS_CARFTMVMTL) 상차 등록
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_CAR_SCH_ID"	, 	ydCarSchId 		); 
					recPara.setField("MODIFIER" 		,	sModifier		); 
					recPara.setField("YD_CRN_SCH_ID"	, 	srydCrnSchId 	);
					recPara.setField("YS_DN_WR_LOC"		, 	srydDnWrLoc 	);

					commDao.insert(recPara, "com.inisteel.cim.ys.sbr.dao.updN7YSL306CarMtlIns", logId, methodNm, "상차 이송재료 등록 ");
					
					
// 구내운송 상차완료
					carLdCmplYn = CarTSMv.carLdCmplYn(logId, recPara);
					
					// 차량스케줄 야드설비작업매수, 중량, 야드차량진행상태, 야드상차개시일시, 야드상차완료일시 등 수정
					recPara = JDTORecordFactory.getInstance().create();
					
					if ("Y".equals(carLdCmplYn)) {
						recPara.setField("YD_CAR_PROG_STAT", "5"); // 야드차량진행상태(상차완료)
					} else {
						recPara.setField("YD_CAR_PROG_STAT", "4"); // 야드차량진행상태(상차개시)
					}
					
					recPara.setField("YD_WBOOK_ID" 		, ydWbookId 					); 
					recPara.setField("YS_STK_COL_GP" 	, srydDnWrLoc.substring(0, 6) 	); 
					recPara.setField("WR_DT" 			, currDt 						); 
					recPara.setField("YD_CAR_SCH_ID" 	, ydCarSchId 					); 
					recPara.setField("MODIFIER" 		, sModifier    					); 
					

					commDao.update(recPara, "com.inisteel.cim.ys.sbr.dao.updN7YSL306CarSchLd", logId, methodNm, "상차 차량스케줄 수정 ");

					recPara = JDTORecordFactory.getInstance().create();
					recPara.setResultCode(logId);	//Log ID
					recPara.setResultMsg(methodNm);	//Log Method Name
					recPara.setField("YD_CAR_SCH_ID", ydCarSchId	); 
					recPara.setField("YD_CRN_SCH_ID", srydCrnSchId	); 
					recPara.setField("MODIFIER"     , "N7YSL306" 	); 	// 수정자
					

					if ("L".equals(ydCarUseGp)) {
// 상차 완료 기준 조회 "Y" 이면 상차 완료 전송 (YSTSJ008)							
						if ("Y".equals(carLdCmplYn)) {
							
// 상차 완료 막음 (L3 화면에서 상차완료 처리)
							jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSTSJ008", recPara));
						}	

						// 이송지시 상차 완료 UPDATE
						CarTSMv.updPbStlFrToMoveUp(recPara);
						
// TB_YS_PREPSCH(YS_준비스케줄), TB_YS_PREPMTL(YS_준비재료) 종료
						recPara.setField("YD_WBOOK_ID" 		, ydWbookId ); 

						CarTSMv.delPrepSch(recPara);
						
					}
				}
			}	
			
			/**********************************************************
			* 5. 권상실적위치가 차량(하차)
			*    마지막 스케줄이면(차량이송재료 없음)
			* 5.1 차량스케줄 야드차량진행상태(E:하차완료), 야드설비작업상태(U:공차) 등 수정
			* 5.4 구내운송 소재차량하차완료 송신(YSTSJ010) 전송
			* 5.5 권하실적재료 적치단 수정 후 등록하여야 하므로 하단부로 이동
			*   - 소재이송지시 이송완료일자, 이송계상일자, 이송상태코드(*:작업완료), 야드재료예정저장To위치코드 수정
			**********************************************************/
			//차량하차여부(공통Table 소재인수일시 수정 송신)
			String carUdCmplYn = "N";

			if ("TR".equals(ydUpWrLoc.substring(2, 4)) && !"TR".equals(srydDnWrLoc.substring(2, 4))) {
				// 야드하차작업예약ID 차량하차스케줄 정보 조회
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID" 		, ydWbookId ); 
				
				jsChk = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL006CarSchUd", logId, methodNm, "하차 차량스케줄 조회 "); 
		    	
				if (jsChk.size() > 0) {
					jrChk = jsChk.getRecord(0);

					jrParam.setField("YD_CAR_SCH_ID", commUtils.trim(jrChk.getFieldString("YD_CAR_SCH_ID"))); //야드차량스케쥴ID(이력등록시에도 사용)
					ydCarSchId  = commUtils.trim(jrChk.getFieldString("YD_CAR_SCH_ID"));
					carUdCmplYn = commUtils.trim(jrChk.getFieldString("CAR_UD_CMPL_YN")); //차량하차완료여부
					
					// 차량하차완료이면
					if ("Y".equals(carUdCmplYn)) {
						// 하차 차량스케줄 야드설비작업상태, 야드차량진행상태, 야드상차작업예약ID, 착지개소코드 등 수정
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setResultCode(logId);	//Log ID
						recPara.setResultMsg(methodNm);	//Log Method Name
						recPara.setField("MODIFIER" , 		sModifier	); 
						recPara.setField("WR_DT" , 			currDt 		); 
						recPara.setField("YD_CAR_SCH_ID" , 	ydCarSchId 	); 
						
						commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CarSchUd", logId, methodNm, "하차 차량스케줄 수정  ");

						
						//구내운송 소재차량하차완료
						jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSTSJ010", recPara));
						
					}
				} else {
					//야드하차작업예약ID가 없는 경우 권상실적수신에서 작업예약 재료번호로 차량하차스케줄ID를 조회하여 작업예약ID 등록
					commUtils.printLog(logId, "권하실적 하차 차량스케줄이 없습니다.", "SL");
				}
			}

			
////////////////////////////////////////////////////////////////////////////
// 2026.02.26 권상 위치가 대차 이면 대차 스케쥴 종료
////////////////////////////////////////////////////////////////////////////
			
			if ("TC".equals(ydUpWrLoc.substring(2, 4))) {
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setResultCode(logId);	//Log ID
				recPara.setResultMsg(methodNm);	//Log Method Name
				
				recPara.setField("YD_CRN_SCH_ID", srydCrnSchId	); 

				jsChk = commDao.select(recPara, "com.inisteel.cim.ys.sbr.dao.getCrSchMatl", logId, methodNm, "크레인 스케쥴 재료 조회 "); 
		    	
				if (jsChk.size() > 0) {
					jrChk = jsChk.getRecord(0);

					String srSstlNo = commUtils.trim(jrChk.getFieldString("SSTL_NO")); 	// 재료 번호

					JDTORecordSet 	jsTemp 			= null;
					JDTORecord 		jrTemp 			= null;
					
					String 			szYD_TC_SCH_ID 	= "";
					
				    int 			intRtnVal		= 0 ;
					
					jrParam.setField("SSTL_NO"		, srSstlNo	); // 재료번호
					
					jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.sbr.dao.getTcarSchMatl", logId, methodNm, "재료번호로 대차 스케줄 조회");
					
					if (jsTemp != null && jsTemp.size() > 0) {
						
						jrTemp 		= jsTemp.getRecord(0);
		
						szYD_TC_SCH_ID	= commUtils.trim(jrTemp.getFieldString("YD_TCAR_SCH_ID")); 

						szMsg	= "대차 재료 번호[" + srSstlNo + "] 대차 스케쥴 [" + szYD_TC_SCH_ID + "] 대차 하차 완료(E)";
						commUtils.printLog(logId, szMsg, "SL");

						this.TcProgStatChg(logId, sModifier, szYD_TC_SCH_ID, "E", srydEqpId, ydWbookId);

					} 			
					
				}
			}
			
////////////////////////////////////////////////////////////////////////////
			
			
			/**********************************************************
			* 6. 설비, 적치단 , 크레인스케쥴, 저장품, 작업예약재료, 작업예약 수정
			* 6.1 설비 야드설비상태(권하완료) 수정
			* 6.2 적치단	
			*   - 크레인 재료정보 삭제
			*   - 권하위치 재료정보 수정
			*   - 권하위치외 같은 재료번호로 등록된 적치단 수정(권하분리 재료 제외)
			* 6.3 크레인스케쥴
			*   - 크레인작업재료 삭제
			*   - 크레인스케쥴 권하실적 수정 및 삭제
			* 6.4 작업예약 마지막 크레인스케쥴 이면
			*   - 작업예약재료 삭제
			*   - 작업예약 수정 및 삭제
			**********************************************************/
			//설비(야드설비상태) 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatEqp", logId, methodNm, "설비상태 수정");
			
			//이전 권하위치 단을 Clear 한다.
			jrParam2.setField("YD_CRN_SCH_ID", srydCrnSchId	); // 야드크레인스케쥴ID
			jrParam2.setField("YS_STK_COL_GP", "GD%"		); 
			jrParam2.setField("YS_STK_BED_NO", "%"			); 
			commDao.update(jrParam2, "com.inisteel.cim.ys.common.dao.YsCommDAO.clrUpDnWrkMtlN7", logId, methodNm, "적치단 작업Clear ");
			
// Seq 정순 역순 등록

			if("Y".equals(szREVERSE_YN))	
			{ 
// 2026.04.28 소형 야드 추출대 권상 -> 야드 권하시 역순 처리
//			  추출대는 제일 밑이 순번 1 이지만 야드는 제일 밑이 순번 3 이기 때문(1,2,3,4 베드 순서와 같은 순번 체계 때문으로 추측)	
//				
//				------------------------------------------------------
//					                1단(홀수단)   		|      2단(짝수단)
//				------------------------------------------------------
//					열	번지		주행		횡행			| 	주행    	횡행
//				------------------------------------------------------
//				GD0101	01	  259019	18439  	  	| 256880	16360
//				GD0101	02	  259060	17040	  	| 258240	16360
//				GD0101	03	  259060	15490	  	| 259700	16360
//				GD0101	04	  259060	14260	  	| 261080	16360
//				------------------------------------------------------
//				GD0201	01	  259550	10530	  	| 257340	 8440
//				GD0201	02	  259550	 9200	  	| 258420	 8440
//				GD0201	03	  259560	 7620	  	| 260190	 8440
//				GD0201	04	  259560	 6140	  	| 261620	 8440
//				------------------------------------------------------
//				* 홀수단 : 추출대 에서 권상 상태 그대로 권하(TAG 방향 열처리 방향)
//				           압연 방향 1 베드 -> 정정 방향 2,3,4 베드로 증가(횡행값 감소)
//				* 짝수단 : 추출대 에서 권상후 시계방향 회전후 권하(TAG 방향 정정 방향)
//				           열처리 방향 1 베드 -> 비열처리 방향 2,3,4 베드로 증가(주행값 증가)
				
				szMsg = "Seq 역순 등록";
				commUtils.printLog(logId, szMsg, "");
				
			    int intRtnVal	= 0 ;
				
				// 적치단(크레인 및 권하위치) 수정
			    intRtnVal = commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyrSeqReverse", logId, methodNm, "크레인 권하실적 적치단 Seq 역순 수정 ");
			
				if(intRtnVal <= 0) {
					resMsg.setField("YD_L3_HD_RS_CD", "DN14"); 					// 야드L3처리결과코드
					resMsg.setField("YD_L3_MSG"     , "오류 : 권하 위치 역순 변경 오류 "); 	// 야드L3MESSAGE
					throw new Exception(ydL3Msg);
				}	      		
			
				szMsg = "크레인 권하실적 적치단 Seq 역순 수정 수정 건수 [" + intRtnVal + "]";
				commUtils.printLog(logId, szMsg, "");
			} else {
				szMsg = "Seq 역순 변경 없음";
				commUtils.printLog(logId, szMsg, "");
			}
				
	
// 권하 실적 처리시 권하 위치YS_적치단(TB_YS_STKLYR) 에 재료정보가 없는 상태가 크레인 작업지시에서 생성되어 UPDATE 건수 체크 
			
			int intRtnVal	= 0 ;

			// 적치단(크레인 및 권하위치) 수정
//			intRtnVal = commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updN7YSL306StkLyr", logId, methodNm, "적치단(크레인 및 권하위치) 수정");
			intRtnVal = commDao.update(jrParam, "com.inisteel.cim.ys.sbr.dao.updN7YSL306StkLyr", logId, methodNm, "적치단(크레인 및 권하위치) 수정");

			szMsg = "적치단(크레인 및 권하위치) 수정 건수 [" + intRtnVal + "]";
			commUtils.printLog(logId, szMsg, "");

			
			// 크레인작업재료 삭제
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CrnMtl", logId, methodNm, "크레인작업재료 삭제");
			
			// 크레인스케쥴 권하실적 수정 및 삭제
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CrnSch", logId, methodNm, "크레인스케쥴 권하실적 수정 및 삭제");

			//작업예약완료 이면
			if ("Y".equals(wbCmplYn)) {
				// 작업예약재료 삭제
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006WbMtlDel", logId, methodNm, "작업예약재료 삭제");
				// 작업예약 수정 및 삭제
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006WbDel", logId, methodNm, "작업예약 수정 및 삭제");
			} else {
				// 크레인작업재료번호로 작업예약재료 삭제 (작업예약재료에 작업이 완료된 재료는 DEL_YN='Y' 함으로써 스케줄 취소 후 재 작업시 작업대상에서 제외시킨다.) 
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006WbMtlDelBySchIdBLBT", logId, methodNm, "크레인권하실적 권하완료된 작업예약재료만 삭제");
			}
			
			/**********************************************************
			* 7. 공통 Table, 저장품, 작업이력 등록 (순서 변경 안됨)
			* 7.1 주편 및 Slab 공통 Table 수정
			*   - 크레인스케줄 재료를 대상
			*   - 권하실적위치로 저장위치 수정
			*   - 권상실적위치가 차량이면 현재진도코드 수정
			* 7.2 저장품 수정
			*   - 작업예약 재료를 대상
			*   - 작업예약이 삭제되었으면 작업예약ID, 스케줄코드 삭제
			*   - 현재진도코드가 저장품과 다르면 관련 항목(산적LotType 등) 수정
			*   - 저장위치가 저장품과 다르면 저장위치 수정
			* 7.3 작업이력 등록
			*   - 크레인스케줄 재료를 대상
			* 7.4 차량상차 또는 하차완료 시 차량이송재료 대상으로
			* 7.4.1 진행관리 Slab이송완료실적(YDPTJ001) 전송
			*     - 하차완료 시 차량이송재료 현재진도코드 수정 후
			* 7.4.1 소재이송지시 수정 : 권하실적재료 적치단 수정 후
			*     - 상차 : 이송상차일자, 야드재료예정저장From위치코드
			*     - 하차 : 이송완료일자, 이송계상일자, 이송상태코드(*:작업완료), 야드재료예정저장To위치코드
			* 7.4.2 주편 및 Slab 공통 Table 수정
			*     - 상차 : 소재이송일시
			*     - 하차 : 소재인수일시
			**********************************************************/
			
			jrParam.setField("YD_CRN_SCH_ID", srydCrnSchId); //야드크레인스케쥴ID

// 번들 공통 야드저장위치 UPDATE			
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006BundleComm", logId, methodNm, "번들 공통 수정");
			
// 권상 위치가 차량 이면			
			if ("TR".equals(ydUpWrLoc.substring(2, 4))) {
	
				commDao.update(jrParam, "com.inisteel.cim.ys.sbr.dao.updN7YSL306StlfrToMove", logId, methodNm, "이송지시 수정");	
			}

			if ("TR".equals(srydDnWrLoc.substring(2, 4))) {
				
				// 권하 위치가 차량 이면 열처리작업지시, 정정작업지시, 열처리이동요구, 정정이동요구, 포항이송재 종료 처리

				this.setDelYn(logId, sModifier, srydCrnSchId, "Y");

			}
			
			
			//저장품 수정**
			commDao.update(jrParam, "com.inisteel.cim.ys.sbr.dao.updN7YSL306Stock", logId, methodNm, "저장품 수정");
			
			//작업이력 등록
			jrParam.setField("YD_CAR_SCH_ID"   , commUtils.trim(jrParam.getFieldString("YD_CAR_SCH_ID"))	); 
			jrParam.setField("YD_TCAR_SCH_ID"  , commUtils.trim(jrParam.getFieldString("YD_TCAR_SCH_ID"))	); 
			jrParam.setField("YD_CRN_SCH_ID"   , commUtils.trim(jrParam.getFieldString("YD_CRN_SCH_ID"))	); 
			
// 차량 작업시 TB_YS_WRKBOOK 테이블에 차량 정보 UPDATE
			szMsg = "권상,권하실적 처리시 차량이면 TB_YS_WRKHIST 에 운송장비코드 UPDATE";
			commUtils.printLog(logId, szMsg, "");

			jrParam.setField("YD_CAR_SCH_ID"   , ydCarSchId); 
			
			commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkHist", logId, methodNm, "작업이력 등록");
			
			/**********************************************************
			* 8. 권하지시위치 단과 실적 단이 다르면 저장품제원 전문 전송(YSNxL002)
			**********************************************************/
			if (chgDnWrLayer) {
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN7L302DnWr", jrParam));
			}
			
			resMsg.setField("YD_UP_WR_LOC", ydUpWrLoc	); // 야드권상실적위치
			resMsg.setField("YD_DN_WR_LOC", srydDnWrLoc	); // 야드권하실적위치

			// 크레인작업실적응답 전송
			if (resYn) {
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); // 야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ""    ); // 야드L3MESSAGE
				jrRtn = commUtils.addSndData(jrRtn, this.getYSN7L304(resMsg));
			}
			

// 권하 처리후 다음 작업지시 요청 부분 개선(EAI에서 권하 응답 보다 다음 작업지시가 먼저 전송 되는 현상 빌생 하여 개선)
			szMsg = "권하 처리후 다음 작업지시 요청";
      		commUtils.printLog(logId, szMsg, "");
	      		
// 내부 처리 YSYSJ001 사용
			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name

			jrYdMsg.setField("JMS_TC_CD"         , "YSYSJ001"               ); // JMSTC코드
			jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); // JMSTC생성일시
			jrYdMsg.setField("YD_EQP_ID"         , srydEqpId              	); // 야드설비ID
			jrYdMsg.setField("YD_WRK_PROG_STAT"  , "W"                      ); // 야드작업진행상태: [W:명령선택대기]

// 2026.02.23 현재 권하 완료 작업이 대차 상차 작업 이면 스케쥴코드 같이 넘겨서 다음 스케쥴에서 같은 스케쥴코드 제외
//			  대차는 A 위치에 있지만 이미 상차된 재료가 있기 때문에 같은 작업 기동 하면 권하 위치가 XX 뜨기 때문
			if(    "GDTC04UM".equals(srydSchCd.substring(0, 8))
				|| "GDTC05UM".equals(srydSchCd.substring(0, 8)) 
				|| "GDTC06UM".equals(srydSchCd.substring(0, 8))) {
				
				jrYdMsg.setField("YD_SCH_CD"  , srydSchCd					); // 야드스케쥴코드
			}
			
			commUtils.printLog(logId, "JMS_TC_CD >>>>>>>>>> " + jrYdMsg.toString(), "");
			
			jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			if (resYn) {
				try {
					
					//PIDEV_F : 정상SET후  ERROR 발생한 경우								
					if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {
						resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );		// 야드L3처리결과코드(Error)
						resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); 	// 야드L3MESSAGE(Error)
					}
					
					//크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "YsCommEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { this.getYSN7L304(resMsg) });
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인운전모드전환(N7YSL307)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvN7YSL307(JDTORecord rcvMsg) throws DAOException {
		String szMsg	= "";
		String methodNm = "크레인운전모드전환[SbrYsL2RcvSeEJB.rcvN7YSL307]";
		String logId 	= commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);	
		
		try {

			commUtils.printLog(logId, methodNm, "S+");

			szMsg = rcvMsg.toString();
			commUtils.printLog(logId, szMsg, "");
			
			
			/*
			7	YS_EQP_ID			야드설비ID			CHAR	6
			8	YS_EQP_WRK_MODE		야드설비작업Mode		CHAR	1
			9	YS_EQP_WRK_MODE2	야드설비작업Mode2	CHAR	1
			*/

			// 수신 항목 값
			String msgId        	= commUtils.getMsgId(rcvMsg); 										// EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String srydEqpId      	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"          )); 	// 야드설비ID
			String srydEqpWrkMode 	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE"    )); 	// 야드설비작업Mode
			String srydEqpWrkMode2	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE2"   )); 	// 야드설비작업Mode2
			String sModifier		= commUtils.trim(rcvMsg.getFieldString("MODIFIER"			)); 	// 수정자(Backup Only)
			
			methodNm = msgId.substring(0, 2) + methodNm;

			szMsg = "\n\t YD_EQP_ID   		: " + srydEqpId 
				  + "\n\t YD_EQP_WRK_MODE   : " + srydEqpWrkMode 
				  + "\n\t YD_EQP_WRK_MODE2  : " + srydEqpWrkMode2
				  + "\n\t MODIFIER			: " + sModifier 
				  ;

			commUtils.printLog(logId, szMsg, "");

			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(srydEqpId)) {
				throw new Exception("오류:설비ID 없음");
			} else if (srydEqpId.length() < 6 || !srydEqpId.startsWith("GD")) {
				throw new Exception("오류:설비ID[" + srydEqpId + "] 이상");
			} else if ("".equals(srydEqpWrkMode)) {
				throw new Exception("야드설비작업Mode 없음");
			} else if ("".equals(srydEqpWrkMode2)) {
				throw new Exception("야드설비작업Mode2 없음");
			}

			/**********************************************************
			* 2. 설비테이블 업데이트
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);									// Log ID
			jrParam.setResultMsg(methodNm);									// Log Method Name

			jrParam.setField("YD_EQP_ID"		, srydEqpId			); 	// 야드설비ID
			jrParam.setField("YD_EQP_WRK_MODE"	, srydEqpWrkMode	); 	// 야드설비작업Mode
			jrParam.setField("MODIFIER"			, sModifier			); 	// 수정자

			commDao.update(jrParam, "com.inisteel.cim.ys.sbr.dao.updEqpMode", logId, methodNm, "설비모드 UPDATE");

			/**********************************************************
			* 3. 크레인작업실적응답(YSN7L304) 전문 생성(모드변경) 
			**********************************************************/

			// 전송 Data 생성
			JDTORecord resMsg = JDTORecordFactory.getInstance().create(); 	// 크레인작업실적응답 전문 생성용
			
			// 크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);									// Log ID
			resMsg.setResultMsg(methodNm);									// Log Method Name
			resMsg.setField("YD_EQP_ID"       , srydEqpId     	); 			// 야드설비ID
			resMsg.setField("YD_L2_WR_GP"     , "M"         	); 			// 야드L2실적구분(운전모드전환)
			resMsg.setField("YD_L3_HD_RS_CD"  , "0000"      	); 			// 야드L3처리결과코드
			resMsg.setField("YD_L3_MSG"       , ""				); 			// 야드L3MESSAGE
			
			JDTORecord jrRtn = null;	// 전문 Return
			jrRtn = commUtils.addSndData(jrRtn, this.getYSN7L304(resMsg));
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	}
	


	
	/**
	 *      [A] 오퍼레이션명 : 차량작업예정정보요구(N7YSL308)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvN7YSL308(JDTORecord rcvMsg) throws DAOException {

        String szMsg	= "";
        String methodNm = "차량작업예정정보요구[SbrYsL2RcvSeEJB.rcvN7YSL308]";
        String logId 	= commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);	
		
		try {

			commUtils.printLog(logId, methodNm, "S+");

			szMsg = rcvMsg.toString();
			commUtils.printLog(logId, szMsg, "");
			
			/*
			7	PT_LOAD_LOC			상차도 위치			CHAR	6
			*/

			// 수신 항목 값
			String msgId        	= commUtils.getMsgId(rcvMsg); 										// EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String srPtLoadLoc     	= commUtils.trim(rcvMsg.getFieldString("PT_LOAD_LOC"          )); 	// 상차도 위치
			
			methodNm = msgId.substring(0, 2) + methodNm;

			szMsg = "\n\t PT_LOAD_LOC   	: " 	+ srPtLoadLoc ;

			commUtils.printLog(logId, szMsg, "");
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(srPtLoadLoc) || !srPtLoadLoc.startsWith("GDTR")) {
				throw new Exception("오류:상차도 오류");
			}

			
			/**********************************************************
			* 2. 차량작업예정정보(YSN7L305) 전문 생성
			**********************************************************/

			// 전송 Data 생성
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			JDTORecord jrRtn = null;	// 전문 Return
			
			// 차량작업예정정보 전문 생성용
			jrParam.setResultCode(logId);		// Log ID
			jrParam.setResultMsg(methodNm);		// Log Method Name
			
			jrParam.setField("PT_LOAD_LOC", srPtLoadLoc	); 		// 상차도 위치
			
			jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN7L305", jrParam));
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업가능응답(N7YSL309)
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN7YSL309(JDTORecord rcvMsg) throws DAOException {

        String szMsg	= "";
		String methodNm = "크레인작업가능응답[SbrYsL2RcvSeEJB.rcvN7YSL309]";
        String logId 	= commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);	
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			szMsg = rcvMsg.toString();
			commUtils.printLog(logId, szMsg, "");
			
			/*
			7	YD_EQP_ID			야드설비ID			CHAR	  6
			8	YD_WRK_PROG_STAT	야드작업진행상태		CHAR	  1
			9	YD_SCH_CD			야드스케쥴코드		CHAR	  8
			10	YD_CRN_SCH_ID		야드크레인스케쥴ID		CHAR	 18
			11	REQ_YN				유무응답			CHAR	  1
			12	YD_WRK_PROG_REQ_MSG	메시지				CHAR	100
			*/

			
			//수신 항목 값ydEqpId
			String msgId        	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String srydEqpId     	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"			));     // 설비ID
			String srydWrkProgStat	= commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT"	));    	// 야드작업진행상태
			String srydSchCd   		= commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"			));    	// 야드스케쥴코드
			String srydCrnSchId  	= commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"		)); 	// 야드크레인스케쥴ID
			String srreqYn  		= commUtils.trim(rcvMsg.getFieldString("REQ_YN"				)); 	// 유무응답
			String srreqMsg  		= commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_REQ_MSG")); 	// 메시지
			String sModifier    	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"           )); 	// 수정자(Backup Only)
			 
			if ("".equals(sModifier)) { sModifier = msgId; }
			
			methodNm = msgId.substring(0, 2) + methodNm;			
			
			szMsg = "\n\t YD_EQP_ID   			: " 	+ srydEqpId 
				  + "\n\t YD_WRK_PROG_STAT  	: " 	+ srydWrkProgStat 
				  + "\n\t YD_SCH_CD         	: " 	+ srydSchCd 
				  + "\n\t YD_CRN_SCH_ID     	: " 	+ srydCrnSchId 
				  + "\n\t REQ_YN     			: " 	+ srreqYn 
				  + "\n\t YD_WRK_PROG_REQ_MSG   : " 	+ srreqMsg
				  + "\n\t MODIFIER     			: " 	+ sModifier 
				  ;

			commUtils.printLog(logId, szMsg, "");
			

			String ydCrnSchId 		= "";
			String ydWbookId 		= "";
			String ydSchCd 			= "";
			String ydWrkProgStst	= "";	
			String ydUpWoLoc        = "";
			String ydDnWoLoc		= "";	
			String ydSchPrior       = ""; //스케줄 우선순위
			String autoYn           = "N";
			
				
			if (srydEqpId.length() < 6 || !srydEqpId.startsWith("GD")) {
				szMsg = "설비ID 이상 [" + srydEqpId + "]";
				commUtils.printLog(logId, szMsg, "");
				throw new Exception(szMsg);
			}
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			*   무인 크레인이 이면 
			**********************************************************/
			autoYn = "N";

			JDTORecord jrParam = commUtils.getParam(logId, methodNm, sModifier);
			
			jrParam.setField("YD_EQP_ID"         		, srydEqpId			);
			jrParam.setField("YD_CRN_SCH_ID"      		, srydCrnSchId		);
			jrParam.setField("YD_WRK_PROG_STAT"    		, srydWrkProgStat	);
			jrParam.setField("YD_WRK_PROG_REQ_MSG"   	, srreqMsg			);

			commUtils.printLog(logId, "크레인 CHECK : [" + autoYn + "]", "");	
			
			JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYsCbtCrnSch", logId, methodNm, "크레인스케줄 조회");
			if (jsCrnSch.size() == 0) {
				szMsg = "크레인 스케쥴 번호가 없습니다..! [" + srydCrnSchId + "]";
				commUtils.printLog(logId, szMsg, "");

				throw new Exception(szMsg);
				
			} else {
				ydCrnSchId 		= jsCrnSch.getRecord(0).getFieldString("YD_CRN_SCH_ID"		);
				ydWbookId 		= jsCrnSch.getRecord(0).getFieldString("YD_WBOOK_ID"		);
				ydSchCd 		= jsCrnSch.getRecord(0).getFieldString("YD_SCH_CD"			);
				ydWrkProgStst	= jsCrnSch.getRecord(0).getFieldString("YD_WRK_PROG_STAT"	);
				ydUpWoLoc	    = jsCrnSch.getRecord(0).getFieldString("YS_UP_WO_LOC"		);	//권상위치
				ydDnWoLoc	    = jsCrnSch.getRecord(0).getFieldString("YS_DN_WO_LOC"		);	//권하위치
				ydSchPrior      = jsCrnSch.getRecord(0).getFieldString("YD_SCH_PRIOR"		);	//스케줄우선순위
			} 
			

			if ("Y".equals(srreqYn)){
				/**********************************************************
				* 2.가능인 경우
				**********************************************************/						
				
				szMsg = methodNm + "가능인 경우";
				commUtils.printLog(logId, szMsg, "");

				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYsCrnSchProgStat", logId, methodNm,  "크레인 스케줄 작업진행상태 변경");
				
			
			} else if ("N".equals(srreqYn)){
				/**********************************************************
				* 2.불가인 경우 (L2 작업 불가인 경우 L2에서는 크레인 작업지시 CLEAR 하기 때문에 크레인 작업지시 취소 불필요)
				*    - TB_YS_CRNSCH.YD_WRK_PROG_STAT = 'W'
				**********************************************************/						
				
				szMsg = methodNm + "불가 일경우";
				commUtils.printLog(logId, szMsg, "");

				jrParam.setField("YD_WRK_PROG_STAT"	, "W");  					
				jrParam.setField("YD_CRN_SCH_ID"   	, ydCrnSchId);  					
				
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYsCrnSchProgStatW", logId, methodNm,  "크레인 스케줄 작업진행상태 대기로 변경");
			}
		
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	

	
	/**
	 *      [A] 오퍼레이션명 : L2야드적치현황정보(N7YSL310)
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN7YSL310(JDTORecord rcvMsg) throws DAOException {

		String szMsg	= "";
		String methodNm = "소형야드 L2야드적치현황정보[SbrYsL2RcvSeEJB.rcvN7YSL310]";
		String logId 	= commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);	
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
	        szMsg = rcvMsg.toString();
			commUtils.printLog(logId, szMsg, "");
			
			/*
			7	INF_REQ_NO	정보요구번호				CHAR	14
			8	T0TAL_CNT	야드적치현황정보송신TOTAL	NUMBER	 5
			9	CURR_CNT	야드적치현황정보현재순번		NUMBER	 5
			10	LOD_LOC1	적재위치1				CHAR	11
			11	SSTL_NO1	특수강재료번호1			CHAR	12
			...
			88	LOD_LOC40	적재위치40				CHAR	11
			89	SSTL_NO40	특수강재료번호40			CHAR	12
			*/
			
			//수신 항목 값ydEqpId
			String msgId        = commUtils.getMsgId(rcvMsg); 									// EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String srInfReqNo   = commUtils.trim(rcvMsg.getFieldString("INF_REQ_NO"		));		// 정보요구번호
			String srT0talCnt   = commUtils.trim(rcvMsg.getFieldString("T0TAL_CNT"		));		// 야드적치현황정보송신TOTAL
			String srCurrCnt    = commUtils.trim(rcvMsg.getFieldString("CURR_CNT"		));		// 야드적치현황정보현재순번
			String sModifier    = commUtils.trim(rcvMsg.getFieldString("MODIFIER"		)); // 수정자(Backup Only)
			
			
			if ("".equals(sModifier)) { sModifier = msgId; }
			
			methodNm = msgId.substring(0, 2) + methodNm;
			
			szMsg = "\n\t INF_REQ_NO   	: " 	+ srInfReqNo 
				  + "\n\t T0TAL_CNT  	: " 	+ srT0talCnt 
				  + "\n\t CURR_CNT      : " 	+ srCurrCnt
				  + "\n\t MODIFIER		: " 	+ sModifier 
				  ;

			commUtils.printLog(logId, szMsg, "");
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/

			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, sModifier);
			
			jrParam.setField("INF_REQ_NO"         		, srInfReqNo);		// 정보요구번호
			
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.getStrLocMtlInfoDmd", logId, methodNm, "정보요구번호 조회");
			if (jsChk == null || jsChk.size() == 0) {
				szMsg = "수신된 정보요구번호가 없습니다..! [" + srInfReqNo + "]";
				commUtils.printLog(logId, szMsg, "");

				throw new Exception(szMsg);
			} 

			
			String sstlNo 	= "";
			String sLodLoc 	= "";
			
			// 최대 40 까지 위치,재료번호
			for (int Loop_i = 1; Loop_i <= 40; Loop_i++) {
				
				sLodLoc = commUtils.trim(rcvMsg.getFieldString("LOD_LOC" + Loop_i));	// 적재위치
				sstlNo 	= commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + Loop_i));	// 재료번호
				
				jrParam.setField("LOD_LOC"	, 	sLodLoc );
				jrParam.setField("SSTL_NO_L2", 	sstlNo );
				
				if ("".equals(sLodLoc)) {
					szMsg = "수신된 적재위치 없습니다..! 정보요구번호 [" + srInfReqNo + "] 재료번호 [" + sstlNo + "]";
					commUtils.printLog(logId, szMsg, "");

// 현재 이후 더이상 없으면 break 있으면 계속 loop
//		            continue;
					break;
				}

//  L2재료정보 UPDATE
				jrParam.setField("INF_REQ_NO"         		, srInfReqNo);		// 정보요구번호
				
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.updStrLocMtlInfoCnfmRs", logId, methodNm, "L2야드적치현황정보 L2재료정보 등록"); 
				
			}
			
// T0TAL_CNT = CURR_CNT 집계 
			if(srT0talCnt.equals(srCurrCnt)){

// TB_YS_STRLOC_MTLINFOCNFMRS(저장위치재료정보확인결과) 테이블 일치/불일치 UPDATE
				jrParam.setField("INF_REQ_NO"         		, srInfReqNo);		// 정보요구번호
				
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.updStrLocMtlInfoCnfmRsCheckYn", logId, methodNm, "L2야드적치현황정보 일지/불일치 등록"); 
				

// TB_YS_STRLOC_MTLINFODMD(저장위치재료정보요구) 테이블 집계 UPDATE
				jrParam.setField("LOD_LOC"	, 	jsChk.getRecord(0).getFieldString("LOD_LOC") );

				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.updStrLocMtlInfoDmd", logId, methodNm, "L2야드적치현황정보 집계 등록"); 
				
			}
			

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
		
	
	
	
	/**
	 *      [A] 오퍼레이션명 : L2야드좌표정보(N7YSL311)
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN7YSL311(JDTORecord rcvMsg) throws DAOException {

		String szMsg	= "";
		String methodNm = "L2야드좌표정보[SbrYsL2RcvSeEJB.rcvN7YSL311]";
		String logId 	= commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);	
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			szMsg = rcvMsg.toString();
			commUtils.printLog(logId, szMsg, "");
			
			/*
			7	YS_STK_COL_GP	적치열		CHAR	6
			8	YS_STK_BED_NO	베드		CHAR	2
			9	YS_STK_LYR_CLS	단구분		CHAR	1
			10	YD_XAXIS		야드X축	NUMBER	7
			11	YD_YAXIS		야드Y축	NUMBER	5
			*/
			
			//수신 항목 값ydEqpId
			String msgId     		= commUtils.getMsgId(rcvMsg); 										// EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String srYsStkColGp   	= commUtils.trim(rcvMsg.getFieldString("YS_STK_COL_GP"		));		// 적치열
			String srYsStkBedNo   	= commUtils.trim(rcvMsg.getFieldString("YS_STK_BED_NO"		));		// 베드
			String srYsStkLyrCls  	= commUtils.trim(rcvMsg.getFieldString("YS_STK_LYR_CLS"		));		// 단구분 (1 : 홀수단, 2 : 짝수단)
			String srYdXaxis		= commUtils.trim(rcvMsg.getFieldString("YD_XAXIS"    		)); 	// 야드X축
			String srYdYaxis   		= commUtils.trim(rcvMsg.getFieldString("YD_YAXIS"    		)); 	// 야드Y축
			String sModifier    	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"           )); 	// 수정자(Backup Only)
			
			if ("".equals(sModifier)) { sModifier = msgId; }
			
			methodNm = msgId.substring(0, 2) + methodNm;

			szMsg = "\n\t YS_STK_COL_GP   	: " 	+ srYsStkColGp 
				  + "\n\t YS_STK_BED_NO  	: " 	+ srYsStkBedNo 
				  + "\n\t YS_STK_LYR_CLS    : " 	+ srYsStkLyrCls
				  + "\n\t YD_XAXIS      	: " 	+ srYdXaxis
				  + "\n\t YD_YAXIS      	: " 	+ srYdYaxis
				  + "\n\t MODIFIER     		: " 	+ sModifier 
				  ;

			commUtils.printLog(logId, szMsg, "");
			
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/

			if (!srYsStkColGp.startsWith("GD")) {
				szMsg = "수신된 적치열 오류..! [" + srYsStkColGp + "]";
				commUtils.printLog(logId, szMsg, "");

				throw new Exception(szMsg);
			} else if (!"1".equals(srYsStkLyrCls) && !"2".equals(srYsStkLyrCls) ) {
				szMsg = "수신된 단구분 오류..! [" + srYsStkLyrCls + "]";
				commUtils.printLog(logId, szMsg, "");

				throw new Exception(szMsg);
				
			} 
			
			String sYdStkBedXaxis    	= ""; 			// 야드적치BedX축(홀수단)
			String sYdStkBedYaxis    	= ""; 			// 야드적치BedY축(홀수단)
			String sYdStkBedXaxis1    	= ""; 			// 야드적치BedX축(짝수단)
			String sYdStkBedYaxis1    	= ""; 			// 야드적치BedY축(짝수단)
			
			if ("1".equals(srYsStkLyrCls)) {
				// 1 : 홀수단	
				sYdStkBedXaxis 	= srYdXaxis;
				sYdStkBedYaxis	= srYdYaxis;
				
			} else {
				// 2 : 짝수단
				sYdStkBedXaxis1 = srYdXaxis;
				sYdStkBedYaxis1	= srYdYaxis;
			}

			/**********************************************************
			* 2. 베드 좌표 수정
			**********************************************************/
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			
			recPara.setField("MODIFIER" 			, sModifier     	); 
			recPara.setField("YS_STK_COL_GP"  		, srYsStkColGp		); // 적치열
			recPara.setField("YS_STK_BED_NO" 		, srYsStkBedNo  	); // 베드
			recPara.setField("YD_STK_BED_XAXIS" 	, sYdStkBedXaxis	); // 야드적치BedX축(홀수단)
			recPara.setField("YD_STK_BED_YAXIS"		, sYdStkBedYaxis	); // 야드적치BedY축(홀수단)
			recPara.setField("YD_STK_BED_XAXIS1"	, sYdStkBedXaxis1	); // 야드적치BedX축(짝수단)
			recPara.setField("YD_STK_BED_YAXIS1"	, sYdStkBedYaxis1	); // 야드적치BedY축(짝수단)

			commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updLocXY", logId, methodNm, "베드 좌표 수정");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : L2정정이송요구재료정보(N7YSL312)
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN7YSL312(JDTORecord rcvMsg) throws DAOException {

		String szMsg	= "";
		String methodNm = "정정 이송 요구 재료 정보[SbrYsL2RcvSeEJB.rcvN7YSL312]";
		String logId 	= commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);	
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			szMsg = rcvMsg.toString();
			commUtils.printLog(logId, szMsg, "");
			
			/*
			7	CLS			구분			CHAR	 1
			8	YD_EQP_ID	야드설비ID		CHAR	 6
			9	STL_SH		재료매수		NUMBER	 2
			10	SSTL_NO1	특수강재료번호1	CHAR	12
			11	SSTL_NO2	특수강재료번호2	CHAR	12
			... SSTL_NO3 ~ SSTL_NO38
			48	SSTL_NO39	특수강재료번호39	CHAR	12
			49	SSTL_NO40	특수강재료번호40	CHAR	12
			50	SPARE		SPARE		CHAR	50
			*/
			
			//수신 항목 값ydEqpId
			String msgId     	= commUtils.getMsgId(rcvMsg); 								// EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String srCls    	= commUtils.trim(rcvMsg.getFieldString("CLS"		)); 	// 구분 (A : 열처리, B : 정정)
			String srydEqpId   	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"  )); 	// 야드설비ID
			String srStlSh 		= commUtils.trim(rcvMsg.getFieldString("STL_SH"		)); 	// 재료매수
			String sModifier    = commUtils.trim(rcvMsg.getFieldString("MODIFIER"	)); 	// 수정자(Backup Only)
			String sstlNo 		= "";														// 재료번호
			String srMsgGp    	= commUtils.trim(rcvMsg.getFieldString("MSG_GP"     )); 	// head 전문구분

			
			if ("".equals(sModifier)) { sModifier = msgId; }
			
			methodNm = msgId.substring(0, 2) + methodNm;
			
			// 작업 지시 취소시 크레인 작업 및 작업 예약 취소시 사용
			String stlNos = "";
			
			szMsg = "\n\t CLS   	: " 	+ srCls 
				  + "\n\t YD_EQP_ID	: " 	+ srydEqpId 
				  + "\n\t STL_SH    : " 	+ srStlSh
				  + "\n\t MODIFIER	: " 	+ sModifier 
				  ;

			commUtils.printLog(logId, szMsg, "");
			
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/

			if (srydEqpId.length() < 6 || !srydEqpId.startsWith("GDTC")) {
				szMsg = "전문 이상! 설비ID(YD_EQP_ID) 이상 [" + srydEqpId + "]";
				commUtils.printLog(logId, szMsg, "");

				throw new Exception(szMsg);
			} else if (!"A".equals(srCls) && !"B".equals(srCls) ) {
				szMsg = "수신된 구분 오류..! [" + srCls + "]";
				commUtils.printLog(logId, szMsg, "");

				throw new Exception(szMsg);
			} 

			int mtlSh = Integer.parseInt(srStlSh); 	// 재료매수
			int intRtnVal	= 0 ;

			if(40 < mtlSh) {
				mtlSh = 40;
			}

			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			
			jrParam.setResultCode(logId);							// Log ID
			jrParam.setResultMsg(methodNm);							// Log Method Name
			
			jrParam.setField("REQ_EQP_ID"	, srydEqpId	); 	// 야드설비ID
			jrParam.setField("MODIFIER"		, sModifier	); 	// 수정자
			if ("D".equals(srMsgGp)) {
				szMsg = "N9YSL312(정정이송요구재료정보) 전문구분 취소 수신[" + srMsgGp + "]";
				commUtils.printLog(logId, szMsg, "");

				jrParam.setField("DEL_YN"		, "Y"		);	// 삭제유무
			}
			else {
				jrParam.setField("DEL_YN"		, "N"		);	// 삭제유무
			}
			
			if ("A".equals(srCls)) {
				szMsg = "열처리 정정이송요구재료정보";
				commUtils.printLog(logId, szMsg, "");
				
				for (int Loop_i = 1; Loop_i <= mtlSh; Loop_i++) {
					
					sstlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + Loop_i));	// 재료번호
					
					if ("".equals(sstlNo)) {
						
						szMsg = "전문 이상! 재료번호(SSTL_NO" + Loop_i + ") 없음";
						commUtils.printLog(logId, szMsg, "");

						throw new Exception(szMsg);
						
					}

// 2026.03.13 재료 번호 List
					stlNos += sstlNo;	// 재료번호
					
					// 작업 재료 마지막이 아니면 재료번호 뒤에 "," 추가  
					if(Loop_i < mtlSh) {
						stlNos += ",";
					}
						
					
					jrParam.setField("SSTL_NO"		, sstlNo		);	// 재료번호
					
					intRtnVal = commDao.update(jrParam, "com.inisteel.cim.ys.sbr.dao.insHttrtMoveReq", logId, methodNm, "열처리 정정이송요구 재료 정보 등록 및 갱신");
					
					if( intRtnVal <= 0 ){
						szMsg	= methodNm + " 열처리 정정이송요구 재료 정보 등록 및 갱신 시 오류발생 재료번호 [" + sstlNo + "] - 반환값 : " + intRtnVal;
						commUtils.printLog(logId, szMsg, "SL");
					} else {
			    		szMsg	= methodNm + " 열처리 정정이송요구 재료 정보 등록 및 갱신 완료 재료번호 [" + sstlNo + "] - 반환값 : " + intRtnVal;
			    		commUtils.printLog(logId, szMsg, "SL");
					}
				}

			} else if ("B".equals(srCls) ) {
				szMsg = "정정 정정이송요구재료정보";
				commUtils.printLog(logId, szMsg, "");
				
				for (int Loop_i = 1; Loop_i <= mtlSh; Loop_i++) {
					
					sstlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + Loop_i));	// 재료번호
					
					if ("".equals(sstlNo)) {
						
						szMsg = "전문 이상! 재료번호(SSTL_NO" + Loop_i + ") 없음";
						commUtils.printLog(logId, szMsg, "");

						throw new Exception(szMsg);
						
					}
					
// 2026.03.13 재료 번호 List
					stlNos += sstlNo;	// 재료번호
					
					// 작업 재료 마지막이 아니면 재료번호 뒤에 "," 추가  
					if(Loop_i < mtlSh) {
						stlNos += ",";
					}

					jrParam.setField("SSTL_NO"		, sstlNo		);	// 재료번호
					
					intRtnVal = commDao.update(jrParam, "com.inisteel.cim.ys.sbr.dao.insStbrMoveReq", logId, methodNm, "정정 정정이송요구 재료 정보 등록 및 갱신");
					
					if( intRtnVal <= 0 ){
						szMsg	= methodNm + " 정정 정정이송요구 재료 정보 등록 및 갱신 시 오류발생 재료번호 [" + sstlNo + "] - 반환값 : " + intRtnVal;
						commUtils.printLog(logId, szMsg, "SL");
					} else {
						szMsg	= methodNm + " 정정 정정이송요구 재료 정보 등록 및 갱신 완료 재료번호 [" + sstlNo + "] - 반환값 : " + intRtnVal;
						commUtils.printLog(logId, szMsg, "SL");
					}
				}

			} 

// 2026.03.13 작업 지시 취소 재료 번호 List
			if ("D".equals(srMsgGp)) {
				
				JDTORecordSet 	jsTemp 			= null;
				JDTORecord 		jrTemp 			= null;
				
				String ydCrnSchId 		= ""; // 야드크레인스케쥴ID
				String ydWbookId 		= ""; // 야드작업예약ID
				String ydCarSchId 		= ""; // 야드준비스케쥴ID(차량상차작업예약ID)
				String ydTcarSchId 		= ""; // 야드대차스케줄ID
				
				
				szMsg	= methodNm + " 이송요구 취소 재료  LIST [" + stlNos + "]";
				commUtils.printLog(logId, szMsg, "SL");

				// 크레인 작업 취소
				jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setResultCode(logId);	// Log ID
				jrParam.setResultMsg(methodNm);	// Log Method Name
				
				jrParam.setField("SSTL_NO", stlNos	); 	// 이송요구 취소 재료  LIST

				jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.sbr.dao.getYsCrSchSstlNo", logId, methodNm, "재료번호 LIST로 크레인 작업 조회");
				if (jsTemp != null && jsTemp.size() > 0) {
					szMsg	= methodNm + " 크레인 작업 있음 - 이송요구 취소 재료  LIST [" + stlNos + "]";
					commUtils.printLog(logId, szMsg, "SL");

					for (int Loop_i = 1; Loop_i <= jsTemp.size(); Loop_i++) {
						jsTemp.absolute(Loop_i);
						
						jrTemp = JDTORecordFactory.getInstance().create();
						jrTemp.setRecord(jsTemp.getRecord());
						
						ydCrnSchId 	= commUtils.trim(jrTemp.getFieldString("YD_CRN_SCH_ID")	);
						ydWbookId 	= commUtils.trim(jrTemp.getFieldString("YD_WBOOK_ID")	);

			    		szMsg	= methodNm + " 작업 지시 취소 - YD_CRN_SCH_ID [" + ydCrnSchId + "] YD_WBOOK_ID [" + ydWbookId+ "]";
			    		commUtils.printLog(logId, szMsg, "SL");
						
						jrParam.setField("YD_WBOOK_ID", 	ydWbookId	);
						jrParam.setField("YD_CRN_SCH_ID", 	ydCrnSchId	);
						jrParam.setField("MODIFIER"		, 	sModifier	); 	// 수정자
						
						/**********************************************************
						 * 크레인스케줄 취소
						 **********************************************************/
						jrRtn = commUtils.addSndData(jrRtn, sbrYsJsp.trtCrnSchCncl(jrParam));
					}

				} else {
					szMsg	= methodNm + " 크레인 작업 없음 - 이송요구 취소 재료  LIST [" + stlNos + "]";
					commUtils.printLog(logId, szMsg, "SL");
				}


				// 작업 예약 취소
				jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setResultCode(logId);	// Log ID
				jrParam.setResultMsg(methodNm);	// Log Method Name
				
				jrParam.setField("SSTL_NO", stlNos	); 	// 이송요구 취소 재료  LIST


				jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.sbr.dao.getYsWrkbookSstlNo", logId, methodNm, "재료번호 LIST로 작업 예약 조회");
				if (jsTemp != null && jsTemp.size() > 0) {
					szMsg	= methodNm + " 작업 예약 있음 - 이송요구 취소 재료  LIST [" + stlNos + "]";
					commUtils.printLog(logId, szMsg, "SL");


					for (int Loop_i = 1; Loop_i <= jsTemp.size(); Loop_i++) {
						jsTemp.absolute(Loop_i);
						
						jrTemp = JDTORecordFactory.getInstance().create();
						jrTemp.setRecord(jsTemp.getRecord());
						
						ydWbookId 		= commUtils.trim(jrTemp.getFieldString("YD_WBOOK_ID")		);
						ydCarSchId 		= commUtils.trim(jrTemp.getFieldString("CAR_YD_WBOOK_ID")	);
						ydTcarSchId 	= commUtils.trim(jrTemp.getFieldString("YD_TCAR_SCH_ID")	);

						szMsg	= methodNm + " 작업예약 취소 - YD_WBOOK_ID [" + ydWbookId + "] CAR_YD_WBOOK_ID [" + ydCarSchId+ "] YD_TCAR_SCH_ID [" + ydTcarSchId + "]";
						commUtils.printLog(logId, szMsg, "SL");
						
						jrParam.setField("YD_WBOOK_ID", 	ydWbookId	);
						jrParam.setField("CAR_YD_WBOOK_ID", ydCarSchId	);
						jrParam.setField("YD_TCAR_SCH_ID", 	ydTcarSchId	);
						jrParam.setField("MODIFIER"		, 	sModifier	); 	// 수정자
						
						/**********************************************************
						 * 작업예약 취소
						 **********************************************************/
						jrRtn = commUtils.addSndData(jrRtn, sbrYsJsp.trtWrkBookCncl(jrParam));
					}

				} else {
					szMsg	= methodNm + " 작업 예약 없음 - 이송요구 취소 재료  LIST [" + stlNos + "]";
					commUtils.printLog(logId, szMsg, "SL");
				}

				
				// 대차 스케쥴 취소
				jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setResultCode(logId);	// Log ID
				jrParam.setResultMsg(methodNm);	// Log Method Name
				
				jrParam.setField("SSTL_NO", stlNos	); 	// 이송요구 취소 재료  LIST
				
				jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.sbr.dao.getYsTcarSchSstlNo", logId, methodNm, "재료번호 LIST로 대차 스케쥴 조회");
				if (jsTemp != null && jsTemp.size() > 0) {
					szMsg	= methodNm + " 대차 스케쥴 있음 - 이송요구 취소 재료  LIST [" + stlNos + "]";
					commUtils.printLog(logId, szMsg, "SL");

					for (int Loop_i = 1; Loop_i <= jsTemp.size(); Loop_i++) {
						jsTemp.absolute(Loop_i);
						
						jrTemp = JDTORecordFactory.getInstance().create();
						jrTemp.setRecord(jsTemp.getRecord());
						
						ydTcarSchId 		= commUtils.trim(jrTemp.getFieldString("YD_TCAR_SCH_ID")		);

						szMsg	= methodNm + " 대차 스케쥴 취소 - YD_TCAR_SCH_ID [" + ydTcarSchId + "]";
						commUtils.printLog(logId, szMsg, "SL");
						
						/**********************************************************
						 * 대차 스케쥴 취소
						 **********************************************************/
						this.TcProgStatChg(logId, sModifier, ydTcarSchId, "E", "", "");
					}

				} else {
					szMsg	= methodNm + " 대차 스케쥴 없음 - 이송요구 취소 재료  LIST [" + stlNos + "]";
					commUtils.printLog(logId, szMsg, "SL");
				}
				
				
			} else {
				JDTORecordSet 	jsTemp 			= null;
				JDTORecord 		jrTemp 			= null;
				
				String ysStkColGp 	= ""; // 야드적치열구분

				GridData inGridData = new GridData();
				
// 2026.03.17 작업 예약 		
				jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setResultCode(logId);	// Log ID
				jrParam.setResultMsg(methodNm);	// Log Method Name
				
				jrParam.setField("SSTL_NO", stlNos	); 	// 이송요구 재료  LIST


				jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.sbr.dao.getYsStklyrSstlNo", logId, methodNm, "소형 야드 재료번호 LIST로 적치단 조회");
				if (jsTemp != null && jsTemp.size() > 0) {
					
					jsTemp.absolute(1);
					
					jrTemp = JDTORecordFactory.getInstance().create();
					jrTemp.setRecord(jsTemp.getRecord());
					
					ysStkColGp 	= commUtils.trim(jrTemp.getFieldString("YS_STK_COL_GP")	);
					
					inGridData.addParam("SSTL_NOS", 		stlNos);			// 재료번호들
					inGridData.addParam("YS_STK_COL_GP", 	ysStkColGp);		// 야드적치열구분(6자리 이상)
					inGridData.addParam("YD_TO_LOC_GUIDE", 	srydEqpId);			// 야드To위치Guide(작업예정대차)
					inGridData.addParam("YD_WRK_CRN", 		"");				// 야드작업크레인(작업자지정 크레인)
					inGridData.addParam("userid", 			sModifier);			// 수정자
					
					szMsg = "\n\t SSTL_NO   		: " 	+ stlNos 
						  + "\n\t YS_STK_COL_GP     : " 	+ ysStkColGp 
						  + "\n\t YD_TO_LOC_GUIDE   : " 	+ srydEqpId;

					commUtils.printLog(logId, szMsg, "");

					/**********************************************************
					 * 작업 예약 기동
					 **********************************************************/
					
// 2026.04.08 작업 예약 기동 막음					
// * 이송 대상이 크레인 1작업 이상인 경우 대차 스케쥴(TB_YS_TCARSCH) 테이블 어느 시점에 만들건지
// 2026.04.27 작업 예약 기동(관통 테스트 현업 요청)					
					jrRtn = commUtils.addSndData(jrRtn, sbrYsJsp.updbtMvStkWrkBook(inGridData));

					// 대차 현재 위치 조회
					jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setResultCode(logId);	// Log ID
					jrParam.setResultMsg(methodNm);	// Log Method Name
					
					jrParam.setField("YD_EQP_ID", srydEqpId	); 	// 작업예정대차


					jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.sbr.dao.getYsEqp", logId, methodNm, "소형 야드 대차 조회");
					if (jsTemp != null && jsTemp.size() > 0) {

						String ydCurrBayGp 	= ""; // 대차 현 위치
						
						jsTemp.absolute(1);
						
						jrTemp = JDTORecordFactory.getInstance().create();
						jrTemp.setRecord(jsTemp.getRecord());

						
						ydCurrBayGp 	= commUtils.trim(jrTemp.getFieldString("YD_CURR_BAY_GP"));	// 대차 현위치
						if("B".equals(ydCurrBayGp)) {
							// 대차 현위치가 B 이면 이동 요구
							
							// 대차이동요구(YSN7L323) B(정정) -> A(소형야드) 전송 			
							szMsg = "대차 이동 요구 B(정정) -> A(소형야드) 전송";
							commUtils.printLog(logId, szMsg, "");

							jrParam = JDTORecordFactory.getInstance().create();
							jrParam.setResultCode(logId);	// Log ID
							jrParam.setResultMsg(methodNm);	// Log Method Name

							jrParam.setField("YD_EQP_ID"   		, srydEqpId		); 	// 대차 설비 ID
							jrParam.setField("YD_EQP_WRK_STAT"	, "U"			);	// 적재상태(상하차구분 L : 영차,  U : 공차)
							jrParam.setField("ST_LOC"   		, "B"			); 	// 출발위치
							jrParam.setField("END_LOC"   		, "A"			); 	// 도착위치

// 2026.04.27 대차 이동 요구 막음 (화면에서 열처리, 정정이송 대차 이적시 B 위치에 있으면 대차 이동 요구 전송)
// 2026.04.27 대차 이동 요구(관통 테스트 현업 요청)							
							jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN7L312", jrParam));

						}
						
					}
					
				} else {
					szMsg	= methodNm + " 소형 야드 재료번호 LIST로 적치단 없음 - 이송요구 취소 재료  LIST [" + stlNos + "]";
					commUtils.printLog(logId, szMsg, "SL");
				}

				
			}
			
			commUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 이송지시 INSERT
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return void
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public void insPbStlFrToMove(JDTORecord jrParam) throws DAOException {
		String szMsg	= "";
		String methodNm = "소형야드 이송지시 INSERT[SbrYsL2RcvSeEJB.insPbStlFrToMove]";
		String logId 	= commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);	

		try {
			
			commUtils.printLog(logId, methodNm, "S+");

			szMsg = jrParam.toString();
			commUtils.printLog(logId, szMsg, "");
			
			// 이송지시 테이블 INSERT
			commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.insPbStlFrToMove", logId, methodNm, "이송지시 등록");

			commUtils.printLog(logId, methodNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	
		  
	
	
	/**
	 *      [A] 오퍼레이션명 : 저장위치별 재료정보 요구(불일치) INSERT
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return void
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord insStrLocMtlIn(JDTORecord rcvMsg) throws DAOException {
		String szMsg	= "";
		String methodNm = "소형야드 저장위치별 재료정보 요구 INSERT[SbrYsL2RcvSeEJB.insStrLocMtlIn]";
		String logId 	= commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);	

		try {

			commUtils.printLog(logId, methodNm, "S+");

			szMsg = rcvMsg.toString();
			commUtils.printLog(logId, szMsg, "");

			
			String ysInfReqNo 	= commUtils.trim(rcvMsg.getFieldString("INF_REQ_NO"		)); // 정보요구번호
			String ysLodLoc  	= commUtils.trim(rcvMsg.getFieldString("LOD_LOC"        )); // 요청 적치열
			
			szMsg = "\n\t INF_REQ_NO   : " 	+ ysInfReqNo 
				  + "\n\t LOD_LOC      : " 	+ ysLodLoc;

			commUtils.printLog(logId, szMsg, "");
			
			// 저장위치재료정보요구 테이블 INSERT
			commDao.insert(rcvMsg, "com.inisteel.cim.ys.common.dao.insStrLocMtlInfoDmd", logId, methodNm, "저장위치별 재료정보 요구 등록");
			
			// 저장위치재료정보확인결과 테이블 L3 정보 INSERT
			commDao.insert(rcvMsg, "com.inisteel.cim.ys.common.dao.insStrLocMtlInfoCnfmRs", logId, methodNm, "저장위치재료정보확인결과 등록");
			
			commUtils.printLog(logId, methodNm, "");

			/**********************************************************
			* 2. 야드적치현황정보요구(YSN7L210) 전문 생성
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();

			jrParam.setResultCode(logId);	// Log ID
			jrParam.setResultMsg(methodNm);	// Log Method Name

			jrParam.setField("INF_REQ_NO", 	ysInfReqNo	); // 정보요구번호
			jrParam.setField("LOD_LOC"  , 	ysLodLoc	); // 요청 적치열
			
			//전송 Data 생성
			JDTORecord jrRtn = null;	// 전문 Return
			
			jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN7L310", jrParam));

			szMsg = "야드적치현황정보요구(YSN7L310) 전문 생성 ****** " + jrRtn.toString();
			commUtils.printLog(logId, szMsg, "");
			
			// 야드적치현황정보요구(YSN7L210)
			EJBConnector resConn = new EJBConnector("default", "YsCommEJB", this);
			resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { jrRtn });

			commUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	
		  



	/**
	 *      [A] 오퍼레이션명 : 대차 상하차 실적(정정에서 대차 상하차 완료)(N7YSL321)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN7YSL321(JDTORecord rcvMsg) throws DAOException {
		
		String szMsg	= "";
		String methodNm = "대차 상하차 실적(정정에서 대차 상하차 완료)[SbrYsL2RcvSeEJB.rcvN7YSL321]";
		String logId 	= commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);	

		JDTORecord jrParam 		= JDTORecordFactory.getInstance().create();
		JDTORecord jrParam2		= JDTORecordFactory.getInstance().create();
		String sExcptMsg 		= "";
		String sIfData 			= "";
		String msgId 			= null;
		JDTORecord jrTemp 		= null;
		JDTORecord jrRtn 		= null;
		JDTORecordSet jsTemp 	= null;
		String szYD_TC_SCH_ID 	= "";

		try {
			commUtils.printLog(logId, methodNm, "S+");

			szMsg = rcvMsg.toString();
			commUtils.printLog(logId, szMsg, "");
			
			/*
			7	YD_EQP_ID	야드설비ID		CHAR	 6	 	대차 설비 ID		
			8	YD_WRK_STAT	작업상태		CHAR	 1		U : 하차, L:상차		
			9	LOC_CD		상하차위치		CHAR	 1	 	A : 소형야드, B : 정정	(항상 B : 정정)
			10	YD_STL_SH	상하차재료매수	NUMBER	 2	 	상하차재료매수		
			11	SSTL_NO1	특수강재료번호1	CHAR	12	 			
			12	SSTL_NO2	특수강재료번호2	CHAR	12				
			13	SSTL_NO3	특수강재료번호3	CHAR	12				
			14	SSTL_NO4	특수강재료번호4	CHAR	12				
			15	SSTL_NO5	특수강재료번호5	CHAR	12				
			16	SSTL_NO6	특수강재료번호6	CHAR	12				
			17	SSTL_NO7	특수강재료번호7	CHAR	12				
			18	SSTL_NO8	특수강재료번호8	CHAR	12				
			*/
			
			//수신 항목 값
			msgId         		= commUtils.getMsgId(rcvMsg); 
			String srydEqpId   	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"  )); 	// 야드설비ID
			String srydWrkStat 	= commUtils.trim(rcvMsg.getFieldString("YD_WRK_STAT"  )); 	// 작업상태(U : 하차, L:상차)
			String srLocCd    	= commUtils.trim(rcvMsg.getFieldString("LOC_CD"		)); 	// 상하차위치 (A : 소형야드, B : 정정)
			String srydStlSh 	= commUtils.trim(rcvMsg.getFieldString("YD_STL_SH"	)); 	// 상하차재료매수
			String sModifier	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"   )); 	// 수정자(Backup Only)
			String ysStkBedNo  	= "01"; 													// 야드적치Bed번호
			String ysStkLyrNo  	= "01"; 													// 야드적치단번호
			String stlNo 		= "";														// 재료번호

			int intRtnVal		= 0 ;
			
			if ("".equals(sModifier)) { sModifier = msgId; }
			
			methodNm = msgId.substring(0, 2) + methodNm;


			szMsg = "\n\t YD_EQP_ID	: " + srydEqpId 
				  + "\n\t LOC_CD 	: " + srLocCd
				  + "\n\t YD_STL_SH : " + srydStlSh
				  + "\n\t MODIFIER 	: " + sModifier
				  ;

			commUtils.printLog(logId, szMsg, "");
			
						
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (srydEqpId.length() < 6 || !srydEqpId.startsWith("GDTC")) {
				sExcptMsg = "전문 이상! 설비ID(YD_EQP_ID) 이상 [" + srydEqpId + "]";
			} else if (!"U".equals(srydWrkStat) && !"L".equals(srydWrkStat)) {
				sExcptMsg = "전문 이상! 작업상태  이상 [" + srydWrkStat + "]";
			} else if ("".equals(srLocCd)) {
				sExcptMsg = "전문 이상! 상하차위치  없음";
			} else if (!"B".equals(srLocCd)) {
				// 상하차 위치는 정정 아니면 오류
				sExcptMsg = "전문 이상! 상하차위치  이상 [" + srLocCd + "]";
			} else if ("".equals(srydStlSh)) {
				sExcptMsg = "전문 이상! 상하차재료매수 없음";
			}

			if(!"".equals(sExcptMsg)) {
				throw new Exception(sExcptMsg);
			}
			
			String ysStkColGp    = srydEqpId;
			
			if ("L".equals(srydWrkStat)) {
				
////////////////////////////////////////////////////////////////////////////				
// 정정 상차 완료 시작
////////////////////////////////////////////////////////////////////////////
				
				/**********************************************************
				* 2. 적치Bed 재료번호, 재료상태  및 저장품 Update
				**********************************************************/
				jrParam.setResultCode(logId);					// Log ID
				jrParam.setResultMsg(methodNm);					// Log Method Name
				
				int mtlSh = Integer.parseInt(srydStlSh); 		// 상차재료매수

				// 설비 적치열 CLEAR
				jrParam.setField("MODIFIER" , 			sModifier		); 	// 수정자							
				jrParam.setField("YD_STK_LYR_ACT_STAT", "E"       		); 	// 야드적치단활성상태
				jrParam.setField("YD_STK_LYR_MTL_STAT", "E"       		); 	// 야드적치단재료상태(적치가능)
				jrParam.setField("YS_STK_COL_GP" , 		ysStkColGp   	); 	// 야드적치열구분
				
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColGpClear", logId, methodNm, "설비 적치단 CLEAR"); 

				
				// 대차 적치열 매수 만큼만 트래킹 처리(대차 적치열 Seq 최대 3) 
				if(3 < mtlSh) {
					mtlSh = 3;
				}
				
				/**********************************************************
				* 2.1 적재단, 저장품 등록
				**********************************************************/
				
				for (int Loop_i = 1; Loop_i <= mtlSh; Loop_i++) {
					stlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + Loop_i));	// 재료번호
							
					if ("".equals(stlNo)) {
						sExcptMsg = "재료번호(SSTL_NO" + Loop_i + ") 없음";
						throw new Exception(sExcptMsg);
					}

					jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setResultCode(logId);							// Log ID
					jrParam.setResultMsg(methodNm);							// Log Method Name

					// 2025.10.01 설비 적치열 정보 CLEAR
					jrParam.setField("MODIFIER", 					sModifier  		); 	// 수정자							
					jrParam.setField("SSTL_NO", 					stlNo   		); 	// 재료번호
					jrParam.setField("YD_STK_LYR_ACT_STAT"	, 		"E"				); 	// 야드적치단활성상태(적치 가능)
					jrParam.setField("YD_STK_LYR_MTL_STAT"	, 		"E"       		); 	// 야드적치단재료상태(적치가능)

					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyrStlNoClear", logId, methodNm, "저장위치 재료번호 CLEAR "); 
					
					
					jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setResultCode(logId);							// Log ID
					jrParam.setResultMsg(methodNm);							// Log Method Name

					jrParam.setField("YS_STK_COL_GP"      	, ysStkColGp   				); // 야드적치열구분
					jrParam.setField("MODIFIER"           	, sModifier  				); // 수정자							
					jrParam.setField("SSTL_NO"       		, stlNo    					); // 재료번호
					jrParam.setField("YS_STK_BED_NO"		, ysStkBedNo 				); // 야드적치Bed번호
					jrParam.setField("YS_STK_LYR_NO"		, ysStkLyrNo  				); // 특수강야드적치단번호
					jrParam.setField("YS_STK_SEQ_NO"		, String.valueOf(Loop_i)	); // 야드적치SEQ
					//CARRY_OUT 시점에 저장픔 YD_RCPT_DATE UPDATE
					jrParam.setField("CARRY_OUT"	    	, "Y"       				); 


					jrParam.setField("YD_STK_LYR_MTL_STAT"	, "C"       			); // 야드적치단재료상태(적치중)
					
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyr", logId, methodNm, "야드 적치단 등록"); 
					
					commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insBundleYdStock2", logId, methodNm, "야드 저장품 등록"); 
					
				}

////////////////////////////////////////////////////////////////////////////
// 2026.02.24 대차 스케쥴, 재료 정보 생성(TB_YS_TCARSCH, TB_YS_TCARFTMVMTL)
////////////////////////////////////////////////////////////////////////////
				//----------------------------------------------------------------------------------------
				// 대차 스케줄을 생성
				//----------------------------------------------------------------------------------------
				
				// 2026.02.24 마지막 재료 번호로 대차 스케쥴 ID 검색(있으면 아래에서 대차 스케쥴 ID 신규 작성 하지 않음)
				jrParam.setField("SSTL_NO"       		, stlNo    					); // 재료번호
				
				jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.sbr.dao.getTcarSchMatl", logId, methodNm, "재료번호로 대차 스케줄 조회");
				
				if (jsTemp != null && jsTemp.size() > 0) {
					
					jrTemp 		= jsTemp.getRecord(0);
	
					szYD_TC_SCH_ID	= commUtils.trim(jrTemp.getFieldString("YD_TCAR_SCH_ID")); 
				} 			

				// 대차 스케쥴 ID 없으면 신규 작성 
				if("".equals(szYD_TC_SCH_ID)) {
					

					// 대차 스케쥴 ID 신규 작성 
					szYD_TC_SCH_ID = commDao.getSeqId(logId, methodNm, "TcarSch" );
					
					/**********************************************************
					* 대차 이송재료 등록
					**********************************************************/
					
					for (int Loop_i = 1; Loop_i <= mtlSh; Loop_i++) {
						stlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + Loop_i));	// 재료번호

						jrParam = JDTORecordFactory.getInstance().create();
						jrParam.setResultCode(logId);							// Log ID
						jrParam.setResultMsg(methodNm);							// Log Method Name

						jrParam.setField("YD_TCAR_SCH_ID",		szYD_TC_SCH_ID			);	// 야드 대차스케쥴ID
						jrParam.setField("SSTL_NO", 			stlNo   				); 	// 재료번호
						jrParam.setField("YS_STK_SEQ_NO", 		String.valueOf(Loop_i)	); 	// 야드적치SEQ
						jrParam.setField("MODIFIER", 			sModifier  				); 	// 수정자							

						intRtnVal = commDao.insert(jrParam, "com.inisteel.cim.ys.sbr.dao.insTcarMtlSbr", logId, methodNm, "소형 야드 대차 이송재료 등록"); 

						szMsg	= methodNm + " 소형 야드 대차 이송재료 등록 - 반환값 : " + intRtnVal;
						commUtils.printLog(logId, szMsg, "SL");
						
					}
					
					/**********************************************************
					* 대차 스케줄 등록
					**********************************************************/
					JDTORecord	recPara = JDTORecordFactory.getInstance().create();

					recPara.setField("YD_TCAR_SCH_ID",    	szYD_TC_SCH_ID		);		// 야드대차스케쥴ID
					recPara.setField("YD_EQP_ID",        	srydEqpId			);		// 야드설비ID
					recPara.setField("YD_EQP_WRK_STAT",  	"L"					);		// 야드설비작업상태( U : 공차, L : 영차)
					recPara.setField("YD_CARLD_LEV_LOC", 	"B"			 		);		// 야드상차출발위치
					recPara.setField("YD_CARLD_STOP_LOC", 	"B"			 		);		// 야드상차정지위치
					recPara.setField("YD_CARUD_SCH_REQ_GP", "1"			 		);		// 야드하차스케쥴요청구분(1 : 대차상차완료)
					recPara.setField("YD_CARUD_STOP_LOC", 	"A"			 		);		// 야드하차정지위치
					recPara.setField("YD_CAR_PROG_STAT", 	"5"					);		// 야드차량진행상태 (5 : 상차완료)
					recPara.setField("MODIFIER",			sModifier			);		// 등록자
					
					intRtnVal = commDao.update(recPara, "com.inisteel.cim.ys.sbr.dao.updTcarSchInsSchSbr", logId, methodNm, "소형 야드 대차 스케줄 등록 및 갱신");
					
					if( intRtnVal <= 0 ){
						szMsg	= methodNm + " 소형 야드 대차 스케줄 등록 및 갱신 시 오류발생 - 반환값 : " + intRtnVal;
						commUtils.printLog(logId, szMsg, "SL");
					} else {
						szMsg	= methodNm + " 소형 야드 대차 스케줄 등록 및 갱신 완료 - 반환값 : " + intRtnVal;
						commUtils.printLog(logId, szMsg, "SL");
					}

				}
				

////////////////////////////////////////////////////////////////////////////
//2026.03.30 대차 B -> A 이동 요구
				// 대차이동요구(YSN7L323) B(정정) -> A(소형야드) 전송 			
				szMsg = "대차 B 위치에서 상차 완료 수신시 대차 이동 요구 B(정정) -> A(소형야드) 전송";
				commUtils.printLog(logId, szMsg, "");

				jrParam2 = JDTORecordFactory.getInstance().create();
				jrParam2.setResultCode(logId);							// Log ID
				jrParam2.setResultMsg(methodNm);						// Log Method Name

				jrParam2.setField("YD_EQP_ID"   		, srydEqpId   	); 	// 대차 설비 ID
				jrParam2.setField("YD_EQP_WRK_STAT"		, "L"         	);	// 적재상태(상하차구분 L : 영차,  U : 공차)
				jrParam2.setField("ST_LOC"   			, "B"			); 	// 출발위치
				jrParam2.setField("END_LOC"   			, "A"			); 	// 도착위치

				// 2026.01.28 L2 요청으로 YSN7L323 -> YSN7L312 전문ID 변경
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN7L312", jrParam2));
////////////////////////////////////////////////////////////////////////////

				
////////////////////////////////////////////////////////////////////////////
// 정정 상차 완료 끝
////////////////////////////////////////////////////////////////////////////
				
			} else if ("U".equals(srydWrkStat)) {
				
////////////////////////////////////////////////////////////////////////////
// 정정 하차 완료 시작
////////////////////////////////////////////////////////////////////////////
				jrParam.setResultCode(logId);							// Log ID
				jrParam.setResultMsg(methodNm);							// Log Method Name

// 2026.02.25 하차 이면 대차스케쥴 종료
				stlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + 1));	// 첫번째 재료번호
				
				if ("".equals(stlNo)) {
					szMsg	= methodNm + " 하차 재료번호 없음";
					commUtils.printLog(logId, szMsg, "SL");
				} else {
					jrParam.setField("SSTL_NO"		, stlNo	); // 재료번호
					
					jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.sbr.dao.getTcarSchMatl", logId, methodNm, "재료번호로 대차 스케줄 조회");
					
					if (jsTemp != null && jsTemp.size() > 0) {
						
						jrTemp 		= jsTemp.getRecord(0);
		
						szYD_TC_SCH_ID	= commUtils.trim(jrTemp.getFieldString("YD_TCAR_SCH_ID")); 
						
						szMsg	= "대차 재료 번호[" + stlNo + "] 대차 스케쥴 [" + szYD_TC_SCH_ID + "] 대차 하차 완료(E)";
						commUtils.printLog(logId, szMsg, "SL");

						this.TcProgStatChg(logId, sModifier, szYD_TC_SCH_ID, "E", "", "");

					}
					
				}

////////////////////////////////////////////////////////////////////////////
// 2026.03.26 대차 하차시 정정 이송 재료 종료
				int mtlSh = Integer.parseInt(srydStlSh); 		// 하차재료매수
				
				// 전문 하차 재료 매수 만큼만 트래킹 처리(최대 8) 
				if(8 < mtlSh) {
					mtlSh = 8;
				}

				jrParam2 = JDTORecordFactory.getInstance().create();
				jrParam2.setResultCode(logId);							// Log ID
				jrParam2.setResultMsg(methodNm);						// Log Method Name

				jrParam2.setField("WK_EQP_ID"	, srydEqpId	); 	// 작업설비ID
				jrParam2.setField("MODIFIER"	, sModifier	); 	// 수정자
				jrParam2.setField("DEL_YN"		, "Y"		);	// 삭제유무
				
				for (int Loop_i = 1; Loop_i <= mtlSh; Loop_i++) {
					stlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + Loop_i));	// 재료번호
							
					if ("".equals(stlNo)) {
						sExcptMsg = "재료번호(SSTL_NO" + Loop_i + ") 없음";
						throw new Exception(sExcptMsg);
					}

					jrParam2.setField("SSTL_NO"		, stlNo		);	// 재료번호
					
					if ("GDTC06".equals(srydEqpId)) {
						// GDTC06 이면 열처리 
						intRtnVal = commDao.update(jrParam2, "com.inisteel.cim.ys.sbr.dao.insHttrtMoveReq", logId, methodNm, "열처리 정정이송요구 재료 정보 등록 및 갱신");
						
						if( intRtnVal <= 0 ){
							szMsg	= methodNm + " 열처리 정정이송요구 재료 정보 등록 및 갱신 시 오류발생 재료번호 [" + stlNo + "] - 반환값 : " + intRtnVal;
							commUtils.printLog(logId, szMsg, "SL");
						} else {
				    		szMsg	= methodNm + " 열처리 정정이송요구 재료 정보 등록 및 갱신 완료 재료번호 [" + stlNo + "] - 반환값 : " + intRtnVal;
				    		commUtils.printLog(logId, szMsg, "SL");
						}
						
						intRtnVal = commDao.update(jrParam2, "com.inisteel.cim.ys.sbr.dao.updHttrtWrkWo", logId, methodNm, "열처리작업지시 재료 정보 갱신");
						
					} else {
						// GDTC04, GDTC05 이면 정정 
						intRtnVal = commDao.update(jrParam2, "com.inisteel.cim.ys.sbr.dao.insStbrMoveReq", logId, methodNm, "정정 정정이송요구 재료 정보 등록 및 갱신");
						
						if( intRtnVal <= 0 ){
							szMsg	= methodNm + " 정정 정정이송요구 재료 정보 등록 및 갱신 시 오류발생 재료번호 [" + stlNo + "] - 반환값 : " + intRtnVal;
							commUtils.printLog(logId, szMsg, "SL");
						} else {
							szMsg	= methodNm + " 정정 정정이송요구 재료 정보 등록 및 갱신 완료 재료번호 [" + stlNo + "] - 반환값 : " + intRtnVal;
							commUtils.printLog(logId, szMsg, "SL");
						}
						
						intRtnVal = commDao.update(jrParam2, "com.inisteel.cim.ys.sbr.dao.updStbrWrkWo", logId, methodNm, "정정작업지시 재료 정보 갱신");
						
					}
				}				
////////////////////////////////////////////////////////////////////////////
				
				
				// 설비 적치열 CLEAR
				jrParam.setField("MODIFIER" , 			sModifier		); 	// 수정자							
				jrParam.setField("YD_STK_LYR_ACT_STAT", "E"       		); 	// 야드적치단활성상태
				jrParam.setField("YD_STK_LYR_MTL_STAT", "E"       		); 	// 야드적치단재료상태(적치가능)
				jrParam.setField("YS_STK_COL_GP"      , ysStkColGp   	); 	// 야드적치열구분
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColGpClear", logId, methodNm, "설비 적치단 CLEAR"); 

				// 해당 대차 소형 야드 -> 정정 이송할 작업 있으면 B -> A 대차 이동 요구 전송
				String szTcMoveYN	= "N";
				String szYD_SCH_CD	= "";

				if("GDTC04".equals(srydEqpId)){    				
					szYD_SCH_CD = "GDTC04UM"; 								// 소형 대차상차 (#4)
				} else if("GDTC05".equals(srydEqpId)){    				
					szYD_SCH_CD = "GDTC05UM"; 								// 소형 대차상차 (#5)
				} else if("GDTC06".equals(srydEqpId)){    				
					szYD_SCH_CD = "GDTC06UM"; 								// 소형 대차상차 (#6)
				}

				jrParam.setField("YD_SCH_CD", szYD_SCH_CD); 	// 스케쥴코드
				
	// 해당 대차 스케쥴코드로 크레인 작업 조회
				JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.sbr.dao.getCrnSchCnt", logId, methodNm, "대차상차 스케줄  조회");
				if (jsChk.size() == 0) {
					// 크레인 스케쥴  Table 존재유무 Check
					
					// 해당 대차  스케쥴코드로 작업예약조회		
					jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.sbr.dao.getCrnSchWbookSchcd", logId, methodNm, "대차상차 작업예약조회");
					if (jsChk.size() == 0) {
						szTcMoveYN	= "N";
					}
					else {
						szTcMoveYN	= "Y";
					}
				} else {
					szTcMoveYN	= "Y";
				}
				
				if("Y".equals(szTcMoveYN)) {
					
					// 대차이동요구(YSN7L323) B(정정) -> A(소형야드) 전송 			
					szMsg = "대차 이동 요구 B(정정) -> A(소형야드) 전송";
					commUtils.printLog(logId, szMsg, "");

					jrParam2 = JDTORecordFactory.getInstance().create();
					jrParam2.setResultCode(logId);							// Log ID
					jrParam2.setResultMsg(methodNm);						// Log Method Name

					jrParam2.setField("YD_EQP_ID"   		, srydEqpId   	); 	// 대차 설비 ID
					jrParam2.setField("YD_EQP_WRK_STAT"		, "U"         	);	// 적재상태(상하차구분 L : 영차,  U : 공차)
					jrParam2.setField("ST_LOC"   			, "B"			); 	// 출발위치
					jrParam2.setField("END_LOC"   			, "A"			); 	// 도착위치

//					jrRtn = commUtils.addSndData(jrRtn, getYSN7L323(logId, srydEqpId, "A","B"));
					// 2026.01.28 L2 요청으로 YSN7L323 -> YSN7L312 전문ID 변경
//					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN7L323", jrParam));
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN7L312", jrParam2));

				}			   
				
////////////////////////////////////////////////////////////////////////////
// 정정 하차 완료 끝
////////////////////////////////////////////////////////////////////////////

			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch (DAOException e) {
			
			throw e;
			
		} catch (Exception e) {
			
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
			
		} finally {
			try {
				
				if(!"".equals(sExcptMsg)) {

					if(sExcptMsg.indexOf("■Error■ Message :")>0) {
						sExcptMsg = sExcptMsg.substring(sExcptMsg.indexOf("■Error■ Message :")+"■Error■ Message :".length()+1,sExcptMsg.length());
					}
					
					if(sExcptMsg.getBytes().length > YsConstant.YS_EXCPTHIST_EXCPT_MSG_LEN) {
						sExcptMsg = commUtils.substr(sExcptMsg, 0, YsConstant.YS_EXCPTHIST_EXCPT_MSG_LEN); 
					}
					
					if(sIfData.getBytes().length > YsConstant.YS_EXCPTHIST_IF_DATA_LEN) {
						sIfData = commUtils.substr(sIfData, 0, YsConstant.YS_EXCPTHIST_IF_DATA_LEN); 
					}
					
					//TB_YS_EXCPTHIST(예외처리이력 테이블)에 저장
					jrParam.setField("IF_ID"		, msgId  	); 	//인터페이스Id
					jrParam.setField("IF_DATA"		, sIfData  	); 	//인터페이스Data
					jrParam.setField("EXCPT_MSG"	, sExcptMsg );  //예외메세지
					jrParam.setField("MODIFIER"		, logId     ); 	//수정자
					
					//commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insExcptHist", logId, methodNm, "예외처리이력 테이블 등록");
					EJBConnector tranConn = new EJBConnector("default", "YsCommSeEJB", this);
					tranConn.trx("insExcptHist", new Class[] { JDTORecord.class }, new Object[] { jrParam });
					
				} 
				
			} catch (Exception e) {
				throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
			}			
		}
	}
		 	
	
	

	/**
	 *      [A] 오퍼레이션명 : 대차 하차 실적(소형 야드 -> 정정 대차 이동후 정정에서 재료 하차 완료)(N7YSL322)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN7YSL322(JDTORecord rcvMsg) throws DAOException {
		
		String szMsg	= "";
		String methodNm = "대차 하차 실적(소형 야드 -> 정정 대차 이동후 정정에서 재료 하차 완료)[SbrYsL2RcvSeEJB.rcvN7YSL322]";
		String logId 	= commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);	

		JDTORecord jrParam 		= JDTORecordFactory.getInstance().create();
		JDTORecord jrParam2		= JDTORecordFactory.getInstance().create();
		String sExcptMsg 		= "";
		String sIfData 			= "";
		String msgId 			= null;
		JDTORecord jrTemp 		= null;
		JDTORecord jrRtn 		= null;

		try {
			commUtils.printLog(logId, methodNm, "S+");

			szMsg = rcvMsg.toString();
			commUtils.printLog(logId, szMsg, "");
			
			/*
			7	YD_EQP_ID	야드설비ID		CHAR	 6
			8	LOC_CD		하차위치		CHAR	 1
			9	YD_STL_SH	하차재료매수		NUMBER	 2
			10	SSTL_NO1	특수강재료번호1	CHAR	12
			11	SSTL_NO2	특수강재료번호2	CHAR	12
			12	SSTL_NO3	특수강재료번호3	CHAR	12
			13	SSTL_NO4	특수강재료번호4	CHAR	12
			14	SSTL_NO5	특수강재료번호5	CHAR	12
			15	SSTL_NO6	특수강재료번호6	CHAR	12
			16	SSTL_NO7	특수강재료번호7	CHAR	12
			17	SSTL_NO8	특수강재료번호8	CHAR	12
			*/
			
			//수신 항목 값
			msgId         		= commUtils.getMsgId(rcvMsg); 
			String srydEqpId   	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"  )); 	// 야드설비ID
			String srLocCd    	= commUtils.trim(rcvMsg.getFieldString("LOC_CD"		)); 	// 상차위치 (A : 소형야드, B : 정정)
			String srydStlSh 	= commUtils.trim(rcvMsg.getFieldString("YD_STL_SH"	)); 	// 상차재료매수
			String sModifier	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"   )); 	// 수정자(Backup Only)
			String ysStkBedNo  	= "01"; 													// 야드적치Bed번호
			String ysStkLyrNo  	= "01"; 													// 야드적치단번호
			
			if ("".equals(sModifier)) { sModifier = msgId; }
			
			methodNm = msgId.substring(0, 2) + methodNm;


			szMsg = "\n\t YD_EQP_ID	: " + srydEqpId 
				  + "\n\t LOC_CD 	: " + srLocCd
				  + "\n\t YD_STL_SH : " + srydStlSh
				  + "\n\t MODIFIER	: " + sModifier 
				  ;

    		commUtils.printLog(logId, szMsg, "");
			
						
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (srydEqpId.length() < 6 || !srydEqpId.startsWith("GDTC")) {
				sExcptMsg = "전문 이상! 설비ID(YD_EQP_ID) 이상 [" + srydEqpId + "]";
			} else if ("".equals(srLocCd)) {
				sExcptMsg = "전문 이상! 상차위치  없음";
			} else if (!"A".equals(srLocCd)) {
				// 상차 위치는 정정 아니면 오류
				sExcptMsg = "전문 이상! 상차위치  이상 [" + srLocCd + "]";
			} else if ("".equals(srydStlSh)) {
				sExcptMsg = "전문 이상! 상차재료매수 없음";
			}

			if(!"".equals(sExcptMsg)) {
				throw new Exception(sExcptMsg);
			}
			
			String ysStkColGp    = srydEqpId;
			
			jrParam.setResultCode(logId);							// Log ID
			jrParam.setResultMsg(methodNm);							// Log Method Name

			
// 설비 적치열 CLEAR
			jrParam.setField("MODIFIER"           	, sModifier		); 	// 수정자							
			jrParam.setField("YD_STK_LYR_ACT_STAT", "E"       		); 	// 야드적치단활성상태
			jrParam.setField("YD_STK_LYR_MTL_STAT", "E"       		); 	// 야드적치단재료상태(적치가능)
			jrParam.setField("YS_STK_COL_GP"      , ysStkColGp   	); 	// 야드적치열구분
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColGpClear", logId, methodNm, "설비 적치단 CLEAR"); 

// 해당 대차 소형 야드 -> 정정 이송할 작업 있으면 A -> B 대차 이동 요구 전송
			String szTcMoveYN	= "N";
			String szYD_SCH_CD	= "";

			if("GDTC04".equals(srydEqpId)){    				
				szYD_SCH_CD = "GDTC04UM"; 								// 소형 대차상차 (#4)
			} else if("GDTC05".equals(srydEqpId)){    				
				szYD_SCH_CD = "GDTC05UM"; 								// 소형 대차상차 (#5)
			} else if("GDTC06".equals(srydEqpId)){    				
				szYD_SCH_CD = "GDTC06UM"; 								// 소형 대차상차 (#6)
			}

			jrParam.setField("YD_SCH_CD", szYD_SCH_CD); 	// 스케쥴코드
			
// 해당 대차 스케쥴코드로 크레인 작업 조회
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.sbr.dao.getCrnSchCnt", logId, methodNm, "대차상차 스케줄 조회");
			if (jsChk.size() == 0) {
				// 크레인 스케쥴  Table 존재유무 Check
				
				// 해당 대차  스케쥴코드로 작업예약조회		
				jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.sbr.dao.getCrnSchWbookSchcd", logId, methodNm, "대차상차 작업예약조회");
				if (jsChk.size() == 0) {
					szTcMoveYN	= "N";
				}
				else {
					szTcMoveYN	= "Y";
				}
			} else {
				szTcMoveYN	= "Y";
			}
			
			
			if("Y".equals(szTcMoveYN)) {
				
				// 대차이동요구(YSN7L323) B(정정) -> A(소형야드) 전송 			
				szMsg = "대차 이동 요구 B(정정) -> A(소형야드) 전송";
				commUtils.printLog(logId, szMsg, "");

				jrParam2.setField("YD_EQP_ID"   		, srydEqpId   	); 	// 대차 설비 ID
				jrParam2.setField("YD_EQP_WRK_STAT"		, "U"         	);	// 적재상태(상하차구분 L : 영차,  U : 공차)
				jrParam2.setField("ST_LOC"   			, "B"			); 	// 출발위치
				jrParam2.setField("END_LOC"   			, "A"			); 	// 도착위치
	    		
				// 2026.01.28 L2 요청으로 YSN7L323 -> YSN7L312 전문ID 변경
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN7L312", jrParam2));

			}			   
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch (DAOException e) {

			throw e;
			
		} catch (Exception e) {

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
			
		} finally {
			try {
				
				if(!"".equals(sExcptMsg)) {

					if(sExcptMsg.indexOf("■Error■ Message :")>0) {
						sExcptMsg = sExcptMsg.substring(sExcptMsg.indexOf("■Error■ Message :")+"■Error■ Message :".length()+1,sExcptMsg.length());
					}
					
					if(sExcptMsg.getBytes().length > YsConstant.YS_EXCPTHIST_EXCPT_MSG_LEN) {
						sExcptMsg = commUtils.substr(sExcptMsg, 0, YsConstant.YS_EXCPTHIST_EXCPT_MSG_LEN); 
					}
					
					if(sIfData.getBytes().length > YsConstant.YS_EXCPTHIST_IF_DATA_LEN) {
						sIfData = commUtils.substr(sIfData, 0, YsConstant.YS_EXCPTHIST_IF_DATA_LEN); 
					}
					
					//TB_YS_EXCPTHIST(예외처리이력 테이블)에 저장
					jrParam.setField("IF_ID"		, msgId  	); 	//인터페이스Id
					jrParam.setField("IF_DATA"		, sIfData  	); 	//인터페이스Data
					jrParam.setField("EXCPT_MSG"	, sExcptMsg );  //예외메세지
					jrParam.setField("MODIFIER"		, logId     ); 	//수정자
					
					EJBConnector tranConn = new EJBConnector("default", "YsCommSeEJB", this);
					tranConn.trx("insExcptHist", new Class[] { JDTORecord.class }, new Object[] { jrParam });
					
				} 
				
			} catch (Exception e) {
				throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
			}			
		}
	}
	
	


	/**
	 *      [A] 오퍼레이션명 : 대차이동정보(소형 야드 <-> 정정 대차 이동 정보)(N7YSL323)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN7YSL323(JDTORecord rcvMsg) throws DAOException {
		
		String szMsg	= "";
		String methodNm = "대차이동정보(소형 야드 <-> 정정 대차 이동 정보)[SbrYsL2RcvSeEJB.rcvN7YSL323]";
		String logId 	= commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);	

		JDTORecord jrParam 		= JDTORecordFactory.getInstance().create();
		String sExcptMsg 		= "";
		String sIfData 			= "";
		String msgId 			= null;
		JDTORecord jrRtn 		= null;

		try {
			commUtils.printLog(logId, methodNm, "S+");

			szMsg = rcvMsg.toString();
			commUtils.printLog(logId, szMsg, "");
			
			/*
			7	YD_EQP_ID	야드설비ID	CHAR	6		대차 설비 ID		
			8	MOVE_GP		이동구분	CHAR	1		S : 출발, M : 이동중, E : 도착		
			9	ST_LOC		이동방향	CHAR	1		F : Forward, B : Backward		
			10	CURR_LOC	현위치		CHAR	1		A : 소형야드, B : 정정		
			11	END_LOC		도착위치	CHAR	1		A : 소형야드, B : 정정		
			*/
			
			//수신 항목 값
			msgId         		= commUtils.getMsgId(rcvMsg); 
			String srydEqpId   	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"  )); 	// 야드설비ID
			String srMoveGp    	= commUtils.trim(rcvMsg.getFieldString("MOVE_GP"	)); 	// 이동구분	(S : 출발, M : 이동중, E : 도착)
			String srStLoc 		= commUtils.trim(rcvMsg.getFieldString("ST_LOC"		)); 	// 이동방향	(F : Forward, B : Backward)
			String srCurrLoc 	= commUtils.trim(rcvMsg.getFieldString("CURR_LOC"	)); 	// 현위치 	(A : 소형야드, B : 정정)
			String srEndLoc 	= commUtils.trim(rcvMsg.getFieldString("END_LOC"	)); 	// 도착위치	(A : 소형야드, B : 정정)
			String sModifier	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"   )); 	// 수정자(Backup Only)
			
			if ("".equals(sModifier)) { sModifier = msgId; }
			
			methodNm = msgId.substring(0, 2) + methodNm;

			szMsg = "\n\t YD_EQP_ID	: " + srydEqpId 
				  + "\n\t MOVE_GP 	: " + srMoveGp
				  + "\n\t ST_LOC 	: " + srStLoc
				  + "\n\t CURR_LOC 	: " + srCurrLoc
				  + "\n\t END_LOC 	: " + srEndLoc
				  + "\n\t MODIFIER	: " + sModifier 
				  ;

			commUtils.printLog(logId, szMsg, "");

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (srydEqpId.length() < 6 || !srydEqpId.startsWith("GDTC")) {
				sExcptMsg = "전문 이상! 설비ID(YD_EQP_ID) 이상 [" + srydEqpId + "]";
			} else if (!"S".equals(srMoveGp) && !"M".equals(srMoveGp) && !"E".equals(srMoveGp)) {
				sExcptMsg = "전문 이상! 이동구분  이상 [" + srMoveGp + "]";
			} else if (!"F".equals(srStLoc) && !"B".equals(srStLoc)) {
				sExcptMsg = "전문 이상! 이동방향  이상 [" + srStLoc + "]";
			} else if (!"A".equals(srCurrLoc) && !"B".equals(srCurrLoc)) {
				sExcptMsg = "전문 이상! 현위치  이상 [" + srCurrLoc + "]";
			} else if (!"A".equals(srEndLoc) && !"B".equals(srEndLoc)) {
				sExcptMsg = "전문 이상! 도착위치  이상 [" + srEndLoc + "]";
			}

			if(!"".equals(sExcptMsg)) {
				throw new Exception(sExcptMsg);
			}
			
			jrParam.setResultCode(logId);							// Log ID
			jrParam.setResultMsg(methodNm);							// Log Method Name

			
// 이동구분 도착(E) 이면 동 UPDATE
			if ("E".equals(srMoveGp)) {
				szMsg = "\n\t 도착 대차  	: " 	+ srydEqpId 
					  + "\n\t 도착 현재동	: " 	+ srCurrLoc 
					  ;

				commUtils.printLog(logId, szMsg, "");
				
				jrParam.setField("MODIFIER"           	, sModifier		); 	// 수정자							
				jrParam.setField("YD_EQP_ID"			, srydEqpId		); 	// 대차 설비 ID
				jrParam.setField("YD_CURR_BAY_GP"		, srCurrLoc		); 	// 현위치 	(A : 소형야드, B : 정정)
				
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdEqpCurrBay", logId, methodNm, "대차 현재동 수정"); 
				
				if ("A".equals(srCurrLoc)) {
					
					// 도착이면서 A (소형야드)
					jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setResultCode(logId);	//Log ID
					jrParam.setResultMsg(methodNm);	//Log Method Name
					
					jrParam.setField("YS_STK_COL_GP", srydEqpId	); 	// 야드적치열구분
					jrParam.setField("YS_STK_BED_NO", "01"		); 	// 야드적치Bed번호
					jrParam.setField("YS_STK_LYR_NO", "01"		); 	// 야드적치단번호

					JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.sbr.dao.getSstlNoLoc", logId, methodNm, "대차 적치단 재료 조회");
					if (jsChk != null && jsChk.size() > 0) {

						String szSSTL_NO1	= "";				
						
						// 대차에 재료 있으면 하차 작업
		    			szMsg	=	"대차 [" + srydEqpId + "] 하차 작업 작업예약등록 건수 [" + jsChk.size() + "]";
						commUtils.printLog(logId, szMsg, "SL");
						
						int iCnt = jsChk.size();
						
						// 작업예약등록
						GridData inGridData = new GridData();

						String stlNos = "";
// 2026.02.02 대차 하차 작업 TO위치가이드 
						String ydToLocGuide = "";

						szMsg	=	"대차 [" + srydEqpId + "] 하차 작업 작업예약등록 for loop 전";
						commUtils.printLog(logId, szMsg, "SL");
						
						JDTORecord recInTemp	= null;
						
						for (int Loop_i = 1; Loop_i <= jsChk.size(); Loop_i++) {
							jsChk.absolute(Loop_i);
							
							recInTemp = JDTORecordFactory.getInstance().create();
							recInTemp.setRecord(jsChk.getRecord());
							
							stlNos += commUtils.trim(recInTemp.getFieldString("SSTL_NO"));

// 2026.02.25 대차 작업 관련 추가
							if(Loop_i == 1) {
								// 첫번째 재료 번호로 체크
								szSSTL_NO1 = stlNos;
							}
							
							if(Loop_i < jsChk.size()) {
								stlNos += ",";
							}
							
						}

						szMsg	=	"대차 [" + srydEqpId + "] 하차 작업 작업예약등록 for loop 후";
						commUtils.printLog(logId, szMsg, "SL");

//////////////////////////////////////////////////////////////////////////////////////////////
// 2026.02.25 위치 A 도착 이고 재료 있으면 대차 진행 상태(YD_CAR_PROG_STAT) B(하차도착) 으로 변경(대차 테이블은 대차 상하차 실적시 등록)
						JDTORecordSet 	jsTemp 			= null;
						JDTORecord 		jrTemp 			= null;

						if(!"".equals(szSSTL_NO1)) {
							String 			szYD_TC_SCH_ID 		= "";
							String 			szYD_CARLD_LEV_LOC 	= "";
							
							int intRtnVal	= 0;
							
							// 2026.02.24 재료 번호로 대차 스케쥴 ID 검색(있으면 아래에서 대차 스케쥴 ID 신규 작성 하지 않음)
							jrParam.setField("SSTL_NO"		, szSSTL_NO1	); // 재료번호
							
							jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.sbr.dao.getTcarSchMatl", logId, methodNm, "재료번호로 대차 스케줄 조회");
							
							if (jsTemp != null && jsTemp.size() > 0) {
								
								jrTemp 		= jsTemp.getRecord(0);
				
								szYD_TC_SCH_ID		= commUtils.trim(jrTemp.getFieldString("YD_TCAR_SCH_ID")	);	// 야드대차스케쥴ID
								szYD_CARLD_LEV_LOC	= commUtils.trim(jrTemp.getFieldString("YD_CARLD_LEV_LOC")	);	// 야드상차출발위치

								szMsg	= "대차 재료 번호[" + szSSTL_NO1 + "] 대차 스케쥴 [" + szYD_TC_SCH_ID + "] 야드상차출발위치 [" + szYD_CARLD_LEV_LOC + "]";
								commUtils.printLog(logId, szMsg, "SL");

								// 도착 위치 A 이고 야드상차출발위치 A 이면 진행 상태 변경 하지 않음
								if (!szYD_CARLD_LEV_LOC.equals(srCurrLoc)) {
									this.TcProgStatChg(logId, sModifier, szYD_TC_SCH_ID, "B", "", "");
								}

							} else {
								szMsg	= "대차 재료 번호[" + szSSTL_NO1 + "] 대차 스케쥴 없음";
								commUtils.printLog(logId, szMsg, "SL");
							}

							// 2026.02.25 대차 첫번째 재료 번호 작업 예약 있으면 작업 예약 등록 하지 않음
							jrParam.setField("SSTL_NO"		, szSSTL_NO1	); // 재료번호
							
							jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdWrkbookmtlSTLNO", logId, methodNm, "재료번호로 작업 예약 조회");
							
							if (jsTemp == null || jsTemp.size() == 0) {
								// 도착 위치 A 이고 야드상차출발위치 A 이면 작업 예약 등록 하지 않음
								if (!szYD_CARLD_LEV_LOC.equals(srCurrLoc)) {
									// 작업 예약 등록
									inGridData.addParam("SSTL_NOS", 		stlNos			);		// 재료번호들
									inGridData.addParam("YS_STK_COL_GP", 	srydEqpId		);		// 야드적치열구분(6자리 이상)
									inGridData.addParam("YD_TO_LOC_GUIDE", 	ydToLocGuide	);		// 야드To위치Guide
									inGridData.addParam("YD_WRK_CRN", 		""				);		// 야드작업크레인(작업자지정 크레인)
									inGridData.addParam("userid", 			sModifier		);		// 수정자
									
									szMsg = "\n\t SSTL_NO   		: " 	+ stlNos 
										  + "\n\t YS_STK_COL_GP     : " 	+ srydEqpId 
										  + "\n\t YD_TO_LOC_GUIDE   : " 	+ ydToLocGuide;

						      		commUtils.printLog(logId, szMsg, "");
									
									// 대차도착 하차 작업 예약 등록
					    			szMsg	= "대차 [" + srydEqpId + "] 도착 하차 작업 예약 등록";
									commUtils.printLog(logId, szMsg, "SL");
						      		
						      		// 작업예약등록
						      		jrRtn = commUtils.addSndData(jrRtn, sbrYsJsp.updbtMvStkWrkBook(inGridData));
								} else {
									szMsg	= "야드상차출발위치 [" + szYD_CARLD_LEV_LOC + "] 와 도착 위치 [" + srCurrLoc + "] 같음";
									commUtils.printLog(logId, szMsg, "SL");
								}
 								
							} else {
				    			szMsg	= "대차 [" + srydEqpId + "] 도착 재료 번호 [" + szSSTL_NO1 + "] 이미 등록된 작업 예약이 있습니다.";
								commUtils.printLog(logId, szMsg, "SL");
							}
							
						} else {
				    		szMsg	= "대차 첫번째 재료 번호 [" + szSSTL_NO1 + "] 없음";
				    		commUtils.printLog(logId, szMsg, "SL");
						}
//////////////////////////////////////////////////////////////////////////////////////////////
						
						
						
					} else {
						// 대차에 재료 없으면 상차 작업
						szMsg	=	"대차 [" + srydEqpId + "] 상차 작업 작업예약 조회 ";
						commUtils.printLog(logId, szMsg, "SL");
						
						String szYD_SCH_CD		= "";
						
						// LOT 편성된 정보가 없기 때문에 SCH_CD편성
						if("GDTC04".equals(srydEqpId)){    				
							szYD_SCH_CD = "GDTC04UM"; 						// 소형 대차상차 (#4)
						} else if("GDTC05".equals(srydEqpId)){    				
							szYD_SCH_CD = "GDTC05UM"; 						// 소형 대차상차 (#5)
						} else if("GDTC06".equals(srydEqpId)){    				
							szYD_SCH_CD = "GDTC06UM"; 						// 소형 대차상차 (#6)
						}

						jrParam.setField("YD_SCH_CD", szYD_SCH_CD); 	// 스케쥴코드
						
						// 해당 대차  스케쥴코드로 작업예약조회		
						jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.sbr.dao.getCrnSchWbookSchcd", logId, methodNm, "대차상차 작업예약조회");
						if (jsChk != null && jsChk.size() > 0) {
							// 작업예약이 존재하는 경우
							String szYD_WBOOK_ID   	= "";

							JDTORecord recInTemp 	= JDTORecordFactory.getInstance().create();

							JDTORecordSet jsTemp 	= null;
							JDTORecord jrTemp 		= null;
							String szYD_TC_SCH_ID 	= "";
							String szSstlNo 		= "";

							szYD_WBOOK_ID = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_WBOOK_ID"));	// 야드작업예약ID

							jrParam.setField("YD_WBOOK_ID", szYD_WBOOK_ID); 	// 야드작업예약ID
							
							/*
							 * 대차 상차 도착 
							 */
							
							jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.sbr.dao.getYdWrkBookMatl", logId, methodNm, "작업 예약 ID로 재료 조회");
							
							if (jsTemp != null && jsTemp.size() > 0) {
								
								// 재료 번호로 대차 스케쥴 있는지 체크
								jrTemp = jsTemp.getRecord(0);

								szSstlNo = commUtils.trim(jrTemp.getFieldString("SSTL_NO")); 	// 재료 번호
								
								jrParam.setField("SSTL_NO"		, szSstlNo	); // 재료번호
								
								jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.sbr.dao.getTcarSchMatl", logId, methodNm, "재료번호로 대차 스케줄 조회");
								if (jsTemp != null && jsTemp.size() > 0) {
									jrTemp 		= jsTemp.getRecord(0);
									
									szYD_TC_SCH_ID	= commUtils.trim(jrTemp.getFieldString("YD_TCAR_SCH_ID")); 

									szMsg	= "대차 재료 번호[" + szSstlNo + "] 대차 스케쥴 [" + szYD_TC_SCH_ID + "] 대차 상차 도착(2)";
									commUtils.printLog(logId, szMsg, "SL");

									// 대차 상차 출발
									this.TcProgStatChg(logId, sModifier, szYD_TC_SCH_ID, "2", "", "");

								}						
								
							}	

							szMsg	=	"대차 [" + srydEqpId + "] 상차 작업 야드작업예약ID [" + szYD_WBOOK_ID + "]";
							commUtils.printLog(logId, szMsg, "SL");
							
							recInTemp.setField("JMS_TC_CD"	, 			"YSYSJ702");
							recInTemp.setField("JMS_TC_CREATE_DDTT"	, 	commUtils.getDateTime14());
							recInTemp.setField("YD_SCH_CD"	, 			szYD_SCH_CD);
							recInTemp.setField("YD_EQP_ID"	, 			"");
							recInTemp.setField("YD_WBOOK_ID", 			szYD_WBOOK_ID);

							szMsg = recInTemp.toString();
							commUtils.printLog(logId, szMsg, "");

							// 대차도착 상차 작업 예약 기동
							szMsg	=	"대차 [" + srydEqpId + "] 도착 상차 작업 예약 기동";
							commUtils.printLog(logId, szMsg, "SL");
							
							//내부 전송 Data 생성
							jrRtn = commUtils.addSndData(jrRtn, recInTemp);
							
						}  else {
							// 대차도착 했는데 상차,하차 작업 없음
							szMsg	=	"대차 [" + srydEqpId + "] 도착 상차/하차 작업 없음";
							commUtils.printLog(logId, szMsg, "SL");
						}
						
					}
					
				} else if ("B".equals(srCurrLoc)) {
					
					JDTORecordSet 	jsTemp 			= null;
					JDTORecord 		jrTemp 			= null;
					String 			szYD_TC_SCH_ID 	= "";
					
					// 대차 도착 위치 B 이면서 재료 있으면 하차도착 
					// 도착이면서 B (야드)
					jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setResultCode(logId);	//Log ID
					jrParam.setResultMsg(methodNm);	//Log Method Name
					
					jrParam.setField("YS_STK_COL_GP", srydEqpId	); 	// 야드적치열구분
					jrParam.setField("YS_STK_BED_NO", "01"		); 	// 야드적치Bed번호
					jrParam.setField("YS_STK_LYR_NO", "01"		); 	// 야드적치단번호

					JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.sbr.dao.getSstlNoLoc", logId, methodNm, "대차 적치단 재료 조회");
					if (jsChk != null && jsChk.size() > 0) {
						
						String szSstlNo	= "";				

						jrTemp = jsChk.getRecord(0);

						szSstlNo = commUtils.trim(jrTemp.getFieldString("SSTL_NO")); 	// 재료 번호

						jrParam.setField("SSTL_NO", szSstlNo	); 	// 재료 번호
						
						jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.sbr.dao.getTcarSchMatl", logId, methodNm, "재료번호로 대차 스케줄 조회");
						
						if (jsTemp != null && jsTemp.size() > 0) {
							jrTemp 		= jsTemp.getRecord(0);
							
							szYD_TC_SCH_ID	= commUtils.trim(jrTemp.getFieldString("YD_TCAR_SCH_ID")); 

							szMsg	= "대차 재료 번호[" + szSstlNo + "] 대차 스케쥴 [" + szYD_TC_SCH_ID + "] 대차 하차 도착(B)";
							commUtils.printLog(logId, szMsg, "SL");

							// 대차 하차출발
							this.TcProgStatChg(logId, sModifier, szYD_TC_SCH_ID, "B", "", "");

						}						
						
					}
					
				}
				
			} else if ("S".equals(srMoveGp)) {
// 2026.02.26 대차 출발시 대차 위치에 재료 있으면 영차(하차 출발 일시)
				szMsg = "\n\t 도착 출발  	: " 	+ srydEqpId 
					  + "\n\t 도착 동		: " 	+ srEndLoc 
					  ;

				commUtils.printLog(logId, szMsg, "");

				JDTORecordSet 	jsTemp 			= null;
				JDTORecord 		jrTemp 			= null;
				String 			szYD_TC_SCH_ID 	= "";
				String 			szSstlNo 		= "";

				jrParam.setField("YS_STK_COL_GP", srydEqpId	); 	// 야드적치열구분
				jrParam.setField("YS_STK_BED_NO", "01"		); 	// 야드적치Bed번호
				jrParam.setField("YS_STK_LYR_NO", "01"		); 	// 야드적치단번호

				JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.sbr.dao.getSstlNoLoc", logId, methodNm, "대차 적치단 재료 조회");
				if (jsChk != null && jsChk.size() > 0) {
					// 재료 번호로 대차 스케쥴 있는지 체크
					jrTemp = jsChk.getRecord(0);

					szSstlNo = commUtils.trim(jrTemp.getFieldString("SSTL_NO")); 	// 재료 번호
					
					jrParam.setField("SSTL_NO"		, szSstlNo	); // 재료번호
					
					jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.sbr.dao.getTcarSchMatl", logId, methodNm, "재료번호로 대차 스케줄 조회");
					if (jsTemp != null && jsTemp.size() > 0) {
						jrTemp 		= jsTemp.getRecord(0);
						
						szYD_TC_SCH_ID	= commUtils.trim(jrTemp.getFieldString("YD_TCAR_SCH_ID")); 

						szMsg	= "대차 재료 번호[" + szSstlNo + "] 대차 스케쥴 [" + szYD_TC_SCH_ID + "] 대차 하차 출발(A)";
						commUtils.printLog(logId, szMsg, "SL");

						// 대차 하차출발
						this.TcProgStatChg(logId, sModifier, szYD_TC_SCH_ID, "A", "", "");

					}						
				} else if ("A".equals(srEndLoc)) {
					// 대차에 재료 없이 출발시 도착이 A 위치 이면 작업 예약 체크 작업 예약 있으면 대차 스케쥴에 상차 출발 등록
				    String szYD_SCH_CD	= "";

					if("GDTC04".equals(srydEqpId)){    				
						szYD_SCH_CD = "GDTC04UM"; 								// 소형 대차상차 (#4)
					} else if("GDTC05".equals(srydEqpId)){    				
						szYD_SCH_CD = "GDTC05UM"; 								// 소형 대차상차 (#5)
					} else if("GDTC06".equals(srydEqpId)){    				
						szYD_SCH_CD = "GDTC06UM"; 								// 소형 대차상차 (#6)
					}
					
					jrParam.setField("YD_SCH_CD", szYD_SCH_CD); 	// 스케쥴코드

					// 해당 대차  스케쥴코드로 작업예약조회		
					jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.sbr.dao.getCrnSchWbookSchcd", logId, methodNm, "대차상차 작업예약조회");
					if (jsChk != null && jsChk.size() > 0) {
						
					    String szYD_WBOOK_ID   	= "";
						
						// 작업 예약 ID 재료
					    
					    szYD_WBOOK_ID = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_WBOOK_ID"));	// 야드작업예약ID
					    
					    jrParam.setField("YD_WBOOK_ID", szYD_WBOOK_ID); 	// 야드작업예약ID
					    
						jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.sbr.dao.getYdWrkBookMatl", logId, methodNm, "작업 예약 ID로 재료 조회");
						
						if (jsTemp != null && jsTemp.size() > 0) {
							
							// 재료 번호로 대차 스케쥴 있는지 체크
							jrTemp = jsTemp.getRecord(0);

							szSstlNo = commUtils.trim(jrTemp.getFieldString("SSTL_NO")); 	// 재료 번호
							
							jrParam.setField("SSTL_NO"		, szSstlNo	); // 재료번호
							
							jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.sbr.dao.getTcarSchMatl", logId, methodNm, "재료번호로 대차 스케줄 조회");
							if (jsTemp != null && jsTemp.size() > 0) {
								jrTemp 		= jsTemp.getRecord(0);
								
								szYD_TC_SCH_ID	= commUtils.trim(jrTemp.getFieldString("YD_TCAR_SCH_ID")); 

								szMsg	= "대차 재료 번호[" + szSstlNo + "] 대차 스케쥴 [" + szYD_TC_SCH_ID + "] 대차 상차 출발(1)";
								commUtils.printLog(logId, szMsg, "SL");

								// 대차 상차 출발
								this.TcProgStatChg(logId, sModifier, szYD_TC_SCH_ID, "1", "", "");

							}						
							
						}							
					    
					}
					
				}
				
			} else {
				szMsg = "\n\t 대차		: " 	+ srydEqpId 
					  + "\n\t 현재동		: " 	+ srCurrLoc 
					  + "\n\t 이동구분		: " 	+ srMoveGp 
					  + "\n\t 이동구분	(S : 출발, M : 이동중, E : 도착) 출발, 도착 아니면 처리 없음"  
					  ;

				commUtils.printLog(logId, szMsg, "");
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch (DAOException e) {

			throw e;
			
		} catch (Exception e) {

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
			
		} finally {
			try {
				
				if(!"".equals(sExcptMsg)) {

					if(sExcptMsg.indexOf("■Error■ Message :")>0) {
						sExcptMsg = sExcptMsg.substring(sExcptMsg.indexOf("■Error■ Message :")+"■Error■ Message :".length()+1,sExcptMsg.length());
					}
					
					if(sExcptMsg.getBytes().length > YsConstant.YS_EXCPTHIST_EXCPT_MSG_LEN) {
						sExcptMsg = commUtils.substr(sExcptMsg, 0, YsConstant.YS_EXCPTHIST_EXCPT_MSG_LEN); 
					}
					
					if(sIfData.getBytes().length > YsConstant.YS_EXCPTHIST_IF_DATA_LEN) {
						sIfData = commUtils.substr(sIfData, 0, YsConstant.YS_EXCPTHIST_IF_DATA_LEN); 
					}
					
					//TB_YS_EXCPTHIST(예외처리이력 테이블)에 저장
					jrParam.setField("IF_ID"		, msgId  	); 	//인터페이스Id
					jrParam.setField("IF_DATA"		, sIfData  	); 	//인터페이스Data
					jrParam.setField("EXCPT_MSG"	, sExcptMsg );  //예외메세지
					jrParam.setField("MODIFIER"		, logId     ); 	//수정자
					
					//commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insExcptHist", logId, methodNm, "예외처리이력 테이블 등록");
					EJBConnector tranConn = new EJBConnector("default", "YsCommSeEJB", this);
					tranConn.trx("insExcptHist", new Class[] { JDTORecord.class }, new Object[] { jrParam });
					
				} 
				
			} catch (Exception e) {
				throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
			}			
		}
	}
		
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 대차상태정보(N7YSL324)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN7YSL324(JDTORecord rcvMsg) throws DAOException {

		String szMsg	= "";
		String methodNm = "소형야드 대차상태정보[SbrYsL2RcvSeEJB.rcvN7YSL324]";
		String logId 	= commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);	
		
		JDTORecord jrParam 		= JDTORecordFactory.getInstance().create();
		String sExcptMsg 		= "";
		String sIfData 			= "";
		String msgId 			= null;

		try {
			
			commUtils.printLog(logId, methodNm, "S+");

			szMsg = rcvMsg.toString();
			commUtils.printLog(logId, szMsg, "");
			
			/*
			7	YD_EQP_ID			야드설비ID			CHAR	6	대차 설비 ID		
			8	YD_EQP_STAT			야드설비상태			CHAR	1	"B": 고장, "N": 복구		
			9	YD_EQP_TRBL_RCVR_DT	야드설비고장복구일시	CHAR	14	YYYYMMDDHHMMSS		
			*/


			// 수신 항목 값
			msgId           			= commUtils.getMsgId(rcvMsg); 									// EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String srydEqpId         	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"          )); 	// 야드설비ID
			String srydEqpStat       	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_STAT"        )); 	// 야드설비상태(B:고장, N:정상, R:복구 등)
			String srydEqpTrblRcvrDt 	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_TRBL_RCVR_DT")); 	// 야드설비고장복구일시
			String sModifier			= commUtils.trim(rcvMsg.getFieldString("MODIFIER"           )); 	// 수정자(Backup Only)
			String srydEqpPauseCode		= ""; 																// 야드설비휴지코드
			String brGp            		= ""; 																// 고장복구구분
			
			if ("".equals(sModifier)) { sModifier = msgId; }
			
			methodNm = msgId.substring(0, 2) + methodNm;


			szMsg = "\n\t YD_EQP_ID   			: " 	+ srydEqpId 
				  + "\n\t YD_EQP_STAT           : " 	+ srydEqpStat 
				  + "\n\t YD_EQP_TRBL_RCVR_DT   : " 	+ srydEqpTrblRcvrDt 
				  + "\n\t MODIFIER     			: " 	+ sModifier 
				  ;

			commUtils.printLog(logId, szMsg, "");
			
			JDTORecord jrRtn = null;	// 전문 Return

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(srydEqpId)) {
				sExcptMsg = "전문 이상! 설비ID(YD_EQP_ID) 없음";
			} else if (srydEqpId.length() < 6 || !srydEqpId.startsWith("GD")) {
				sExcptMsg = "전문 이상! 설비ID(YD_EQP_ID) 이상 [" + srydEqpId + "]";
			} else if ("".equals(srydEqpStat)) {
				sExcptMsg = "오류:야드설비상태 없음";
			} else if ("".equals(srydEqpTrblRcvrDt) && ("B".equals(srydEqpStat) || "N".equals(srydEqpStat) || "R".equals(srydEqpStat))) {
				sExcptMsg = "오류:고장복구일시 없음";
			}

			if(!"".equals(sExcptMsg)) {
				throw new Exception(sExcptMsg);
			}
			
			
			/**********************************************************
			* 2. 설비상태 Check
			**********************************************************/
			if ("B".equals(srydEqpStat)) {
				brGp 				= "B"; //고장
				srydEqpPauseCode 	= "B000";
			} else if ("N".equals(srydEqpStat) || "R".equals(srydEqpStat)) {
				brGp 				= "R"; //복구
				srydEqpPauseCode 	= "R000";
			}

			jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);								// Log ID
			jrParam.setResultMsg(methodNm);								// Log Method Name
			jrParam.setField("YD_EQP_ID"          , srydEqpId        	); 	// 야드설비ID
			jrParam.setField("YD_EQP_PAUSE_CODE"  , srydEqpPauseCode 	); 	// 야드설비휴지코드
			jrParam.setField("YD_EQP_PAUSE_OCC_DT", srydEqpTrblRcvrDt	); 	// 야드설비휴지발생일시
			jrParam.setField("YD_EQP_STAT"        , srydEqpStat      	); 	// 야드설비상태
			jrParam.setField("BR_GP"              , brGp           		); 	// 고장복구구분
			jrParam.setField("MODIFIER"           , sModifier			); 	// 수정자

			/**********************************************************
			* 3. 설비상태 수정
			**********************************************************/
			commDao.update(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.updStatEqp", logId, methodNm, "설비상태 수정");

			/**********************************************************
			* 4. 설비휴지 등록
			**********************************************************/
			if (!"".equals(brGp)) {
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updEqpPause", logId, methodNm, "설비휴지 등록");
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {

			throw e;
			
		} catch (Exception e) {

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
			
		} finally {
			try {
				
				if(!"".equals(sExcptMsg)) {

					if(sExcptMsg.indexOf("■Error■ Message :")>0) {
						sExcptMsg = sExcptMsg.substring(sExcptMsg.indexOf("■Error■ Message :")+"■Error■ Message :".length()+1,sExcptMsg.length());
					}
					
					if(sExcptMsg.getBytes().length > YsConstant.YS_EXCPTHIST_EXCPT_MSG_LEN) {
						sExcptMsg = commUtils.substr(sExcptMsg, 0, YsConstant.YS_EXCPTHIST_EXCPT_MSG_LEN); 
					}
					
					if(sIfData.getBytes().length > YsConstant.YS_EXCPTHIST_IF_DATA_LEN) {
						sIfData = commUtils.substr(sIfData, 0, YsConstant.YS_EXCPTHIST_IF_DATA_LEN); 
					}
					
					//TB_YS_EXCPTHIST(예외처리이력 테이블)에 저장
					jrParam.setField("IF_ID"		, msgId  	); 	//인터페이스Id
					jrParam.setField("IF_DATA"		, sIfData  	); 	//인터페이스Data
					jrParam.setField("EXCPT_MSG"	, sExcptMsg );  //예외메세지
					jrParam.setField("MODIFIER"		, logId     ); 	//수정자
					
					//commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insExcptHist", logId, methodNm, "예외처리이력 테이블 등록");
					EJBConnector tranConn = new EJBConnector("default", "YsCommSeEJB", this);
					tranConn.trx("insExcptHist", new Class[] { JDTORecord.class }, new Object[] { jrParam });
					
				} 
				
			} catch (Exception e) {
				throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
			}			
		}
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 소형압연 추출대 추출요구(M5YSL301)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvM5YSL301(JDTORecord rcvMsg) throws DAOException {
		String szMsg			= "";
		String methodNm 		= "소형압연 추출대 추출요구[SbrYsL2RcvSeEJB.rcvM5YSL301]";
		String logId 			= commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);
		
		String sExcptMsg 		= "";
		String sIfData 			= "";
		String msgId 			= null;

		JDTORecord jrParam 		= JDTORecordFactory.getInstance().create();
		JDTORecord inParam 		= JDTORecordFactory.getInstance().create();
		
		JDTORecord jrRtn 		= null;
		JDTORecord jrTemp 		= null;
		JDTORecordSet jsTemp 	= null;

		try {
			commUtils.printLog(logId, methodNm, "S+");

			szMsg = rcvMsg.toString();
			commUtils.printLog(logId, szMsg, "");
			
			/*
			7	YD_EQP_ID			야드설비ID 			CHAR	 6
			8	YS_STK_BED_NO		Bed번호			CHAR	 2
			9	YD_STK_BED_STL_SH	야드적치Bed재료매수	NUMBER	 2
			10	YD_EQP_WRK_SH 		야드설비작업매수		NUMBER	 2
			11	STL_APPEAR_GP		재료외형구분			CHAR	 1
			12	SSTL_NO1			특수강재료번호1		CHAR	12
			13	SSTL_NO2			특수강재료번호2		CHAR	12
			... SSTL_NO3 ~ SSTL_NO8
			20	SSTL_NO9			특수강재료번호9		CHAR	12
			21	SSTL_NO10			특수강재료번호10		CHAR	12
			*/
			
			//수신 항목 값
			msgId         		 	= commUtils.getMsgId(rcvMsg); 
			String srydEqpId       	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"        	)); 	// 야드설비ID
			String srysStkBedNo    	= commUtils.trim(rcvMsg.getFieldString("YS_STK_BED_NO"    	)); 	// Bed번호
			String srydStkBedStlSh 	= commUtils.trim(rcvMsg.getFieldString("YD_STK_BED_STL_SH"	)); 	// 야드적치Bed재료매수
			String srydEqpWrkSh 	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_SH"		)); 	// 야드설비작업매수
			String srstlAppearGp 	= commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP"		)); 	// 재료외형구분
			String sModifier      	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"           )); 	// 수정자(Backup Only)
			String sstlNo 			= "";																// 재료번호

			if ("".equals(sModifier)) { sModifier = msgId; }
			
			methodNm = msgId.substring(0, 2) + methodNm;

			// 추출대 추출요구는 베드는 01
			srysStkBedNo = "01";
			
			szMsg = "\n\t YD_EQP_ID   		: " 	+ srydEqpId 
				  + "\n\t YS_STK_BED_NO 	: " 	+ srysStkBedNo 
				  + "\n\t YD_STK_BED_STL_SH : " 	+ srydStkBedStlSh 
				  + "\n\t YD_EQP_WRK_SH 	: " 	+ srydEqpWrkSh 
				  + "\n\t STL_APPEAR_GP 	: " 	+ srstlAppearGp
				  + "\n\t MODIFIER			: " 	+ sModifier 
				  ;

			commUtils.printLog(logId, szMsg, "");
			
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(srydEqpId) || (!srydEqpId.startsWith("GDPC1") && !srydEqpId.startsWith("GDPC2"))) {
				sExcptMsg = "전문 이상! 설비ID[" + srydEqpId + "]";
			} else if ("".equals(srysStkBedNo)) {
				sExcptMsg = "전문 이상! 적치Bed번호 없음";
			} else if ("".equals(srydEqpWrkSh)) {
				sExcptMsg = "전문 이상! 야드설비작업매수 없음";
			}

			if(!"".equals(sExcptMsg)) {
				throw new Exception(sExcptMsg);
			}			

// *************** TB_YS_STOCK 테이블 INSERT/UPDATE
//          재료 외형 구분			
//			M - Bloom 	: Bloom 생산 ~ 대형압연 이전
//			T - Billet 	: 대형압연완료 ~ 소형압연 이전
//			L - 대형봉강 	: 대형압연완료 ~ 제품창고 입고 이전 (종합판정)
//			P - 각강(빌렛) 	: 각강생산(대형압연) ~ 빌렛정정완료 이전
//			Q - 각강(번들) 	: 빌렛정정완료 ~ 제품창고 입고 이전(종합판정)
//			S - 소형봉강 	: 소형압연완료 ~ 제품창고 입고 이전 (종합판정)
//			W - 선재 		: 선재생산(소형압연) ~ 제품창고 입고 이전 (종합판정)
//			N - 바인코일 	: BIC생산(소형압연) ~ 제품창고 입고 이전 (종합판정)
			

// 출하상 ON/OFF
			String sOnOff = "N";
			
			szMsg = "소형 압연 출하상 [" + srydEqpId +"] ON/OFF ";
			commUtils.printLog(logId, szMsg, "");

// 2026.01.09 RULE 테이블 확인 필요
			sOnOff = CarTSMv.ApplyRuleItem(logId, methodNm, "GD0001", srydEqpId);
			if("N".equals(sOnOff)){

				szMsg = "소형 압연 출하상 [" + srydEqpId +"] 추출 요구 OFF 추출요구 작업 하지 않음 ";
				commUtils.printLog(logId, szMsg, "");

				commUtils.printLog(logId, methodNm, "S-");
				
				return jrRtn;

			}

			int iStkSh	= Integer.parseInt(srydStkBedStlSh); 	// 야드적치Bed재료매수
			int iWrkSh  = Integer.parseInt(srydEqpWrkSh); 		// 야드설비작업매수

			for (int Loop_i = 1; Loop_i <= iWrkSh; Loop_i++) {
				
				sIfData += commUtils.trim(rcvMsg.getFieldString("SSTL_NO"+Loop_i));
				if(Loop_i < iWrkSh) {
					sIfData += ", ";
				}
				
			}

// 적치열 가능 매수 만큼만 처리
			if(10 < iStkSh) {
				iStkSh = 10;
			}

// 적치열 가능 매수 만큼만 처리
			if(10 < iWrkSh) {
				iWrkSh = 10;
			}

			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YS_STK_COL_GP"      , srydEqpId   ); //야드적치열구분

			/**********************************************************
			* 2.1 재료번호  CHECK : 저장위치 등록여부
			**********************************************************/
			JDTORecordSet jsChk = JDTORecordFactory.getInstance().createRecordSet("Temp");
			
// ex) 재료 매수가 4, 작업 매수가 3 이면 아래에서 3매만 작업대상 이기 때문에 작업 대상만 체크			
// 2026.04.21 추출대 트래킹 SSTL_NO1 위치가 최하단 재료 1 베드 1 Seq(2026.04.21 L2 관통테스트시 확인)
//			    작업매수 만큼만 재료 번호 있음(L2 관통테스트시 확인)
//			for (int Loop_i = iStkSh; (iStkSh - iWrkSh) < Loop_i; Loop_i--) {
			for (int Loop_i = 1; Loop_i <= iWrkSh; Loop_i++) {
				sstlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + Loop_i));	// 재료번호
						
				if ("".equals(sstlNo)) {
					sExcptMsg = "전문 이상! 재료번호(SSTL_NO" + Loop_i + ") 없음";
					throw new Exception(sExcptMsg);
				}
				jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("YS_STK_COL_GP"      , srydEqpId   ); 	// 야드적치열구분
				jrParam.setField("SSTL_NO"			  , sstlNo    	); 	// 재료 번호
				jrParam.setField("YS_STK_BED_NO"      , srysStkBedNo); 	// 야드적치Bed번호
				
				jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getSstlNoYardYN", logId, methodNm, "저장위치 등록여부");
	
				if (jsChk != null && jsChk.size() > 0) {
					
					jrTemp = jsChk.getRecord(0);
					
					szMsg   = "이미 야드에 적치된 재료 [" + sstlNo + "] ["
							+ commUtils.trim(jrTemp.getFieldString("YS_STK_COL_GP")) 
							+ "-" 
							+ commUtils.trim(jrTemp.getFieldString("YS_STK_BED_NO"))
							+ "]";
					commUtils.printLog(logId, szMsg, "");
					
					return jrRtn;
				}	

			}


			/**********************************************************
			* 2.1 적재단, 저장품 등록
			**********************************************************/

			String ysStkBedNo  	= "01"; 	// 야드적치Bed번호
			
			// 추출대 Bed, Seq
			int iBed = 1;
			int iSeq = 1;
			
// ex) 재료 매수가 4, 작업 매수가 3 이면 아래에서 3매만 작업대상 이기 때문에 작업 대상만 체크			
// 2026.04.21 추출대 트래킹 SSTL_NO1 위치가 최하단 재료 1 베드 1 Seq(2026.04.21 L2 관통테스트시 확인)
//		    작업매수 만큼만 재료 번호 있음(L2 관통테스트시 확인)
//			for (int Loop_i = iStkSh; (iStkSh - iWrkSh) < Loop_i; Loop_i--) {
			for (int Loop_i = 1; Loop_i <= iWrkSh; Loop_i++) {
				sstlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + Loop_i));		// 재료번호
						
				if ("".equals(sstlNo)) {
					sExcptMsg = "전문 이상! 재료번호(SSTL_NO" + Loop_i + ") 없음";
					throw new Exception(sExcptMsg);
				}

				// 추출대는 01 ~ 07 베드
				ysStkBedNo 	= (iBed < 10) ? "0" + String.valueOf(iBed) : String.valueOf(iBed);
				
				jrParam = JDTORecordFactory.getInstance().create();
				
				jrParam.setResultCode(logId);											// Log ID
				jrParam.setResultMsg(methodNm);											// Log Method Name
				jrParam.setField("YS_STK_COL_GP"      	, srydEqpId   			); 		// 야드적치열구분
				jrParam.setField("SSTL_NO"			  	, sstlNo     			); 		// 재료번호
				jrParam.setField("YS_STK_BED_NO"      	, ysStkBedNo			); 		// 야드적치Bed번호
				jrParam.setField("YS_STK_LYR_NO"      	, "01"					); 		// 야드적치 단 -> 무조건 1단
				// 추출대 적치단은 무조건 1베드, 1단, Seq = 1 + 매수 - 현Count (1매 : Seq 1, 2매 : Seq 2,1, 3매 Seq 3, 2, 1)
//				jrParam.setField("YS_STK_SEQ_NO"		, String.valueOf(ii)); 			// 야드적치SEQ
				jrParam.setField("YS_STK_SEQ_NO"		, String.valueOf(iSeq)	); 		// 야드적치SEQ
				jrParam.setField("YD_STK_LYR_MTL_STAT"	, "C"       			); 		// 야드적치단재료상태(적치중)
				jrParam.setField("MODIFIER"           	, sModifier  			); 		// 수정자

				szMsg = "야드 적치단 등록 재료번호 [" + sstlNo +"] BED ["+ ysStkBedNo + "] Seq [" + iSeq + "] Cnt [" + Loop_i + "]";
				commUtils.printLog(logId, szMsg, "");
				
				// 재료번호 이전 저장위치 재료번호 CLEAR
				jrParam.setField("YD_STK_LYR_MTL_STAT", "E"       ); 	// 야드적치단재료상태(적치가능)
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyrStlNoClear", logId, methodNm, "야드 적치단 재료번호 CLEAR"); 
				
				jrParam.setField("YD_STK_LYR_MTL_STAT", "C"       ); 	// 야드적치단재료상태(적치중)
				
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyr", logId, methodNm, "야드 적치단 등록");
				
// 베드 1 -> 7, Seq 1 -> 3 으로 증가 (Seq > 3 이면 베드 + 1, Seq = 1 				
				iSeq 		= iSeq + 1;
				if(iSeq > 3 ) {
					iSeq 	= 1;
					iBed 	= iBed + 1;
				}
				
			}

			jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	// Log ID
			jrParam.setResultMsg(methodNm);	// Log Method Name
			jrParam.setField("MODIFIER", sModifier  ); // 수정자			

			
			// 추출대 Bed, Seq
			iBed = 1;
			iSeq = 1;
			
// ex) 재료 매수가 4, 작업 매수가 3 이면 아래에서 3매만 작업대상 이기 때문에 작업 대상만 체크			
// 2026.04.21 추출대 트래킹 SSTL_NO1 위치가 최하단 재료 1 베드 1 Seq(2026.04.21 L2 관통테스트시 확인)
//		    작업매수 만큼만 재료 번호 있음(L2 관통테스트시 확인)
//			for (int Loop_i = iStkSh; (iStkSh - iWrkSh) < Loop_i; Loop_i--) {
			for (int Loop_i = 1; Loop_i <= iWrkSh; Loop_i++) {
				sstlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + Loop_i));	//재료번호

				jrParam.setField("SSTL_NO"	, sstlNo	); 
				jrParam.setField("CARRY_OUT", "Y"		); 
				commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insBundleYdStock2", logId, methodNm, "야드 저장품 등록"); 
				
				// 추출대는 01 ~ 07 베드
				ysStkBedNo 	= (iBed < 10) ? "0" + String.valueOf(iBed) : String.valueOf(iBed);
				
				inParam = JDTORecordFactory.getInstance().create();
				inParam.setResultCode(logId);	//Log ID
				inParam.setResultMsg(methodNm);	//Log Method Name
				//추가, 이동
				inParam.setField("MODIFIER"				, sModifier  ); // 수정자			
				inParam.setField("FNL_REG_PGM"			, "M5YSL301" );
				inParam.setField("YD_GP"				, "G" );
				inParam.setField("YD_BAY_GP"			, srydEqpId.substring(1,2) );
				inParam.setField("YD_EQP_GP"			, srydEqpId.substring(2,4) );
				inParam.setField("YS_STK_COL_NO"		, srydEqpId.substring(4,6) );
				inParam.setField("YS_STK_BED_NO"		, ysStkBedNo );
				inParam.setField("YS_STK_LYR_NO"		, "01" );
				// 추출대 적치단은 무조건 1베드, 1단, Seq = 1 + 매수 - 현Count (1매 : Seq 1, 2매 : Seq 2,1, 3매 Seq 3, 2, 1)
				inParam.setField("YS_STK_SEQ_NO"		, "" + iSeq);
				inParam.setField("YS_STR_LOC"			, srydEqpId + commUtils.trim(srysStkBedNo) + "01" + "" + iSeq) ;
				inParam.setField("SSTL_NO"				, sstlNo  );
				
				szMsg = "공통 등록 재료번호 [" + sstlNo +"] BED ["+ ysStkBedNo + "] Seq [" + iSeq + "] Cnt [" + Loop_i + "]";

				commUtils.printLog(logId, szMsg, "");
				
				/**********************************************************
				* 1.2 공통 저장위치 Update (별도 Transaction 으로 처리)
				**********************************************************/
				EJBConnector tranConn = new EJBConnector("default", "SbrYsL2RcvSeEJB", this);
				tranConn.trx("updBndlCommYsStrLoc", new Class[] { JDTORecord.class }, new Object[] { inParam });
				
				// 베드 1 -> 7, Seq 1 -> 3 으로 증가 (Seq > 3 이면 베드 + 1, Seq = 1 				
				iSeq 		= iSeq + 1;
				if(iSeq > 3 ) {
					iSeq 	= 1;
					iBed 	= iBed + 1;
				}
				
			}
			
			// STOCK 재료번호가 존재하지 않으면 작업예약을 만들지 않는다.
			String szStkStlNo = "";

// ex) 재료 매수가 4, 작업 매수가 3 이면 아래에서 3매만 작업대상 이기 때문에 작업 대상만 체크			
// 2026.04.21 추출대 트래킹 SSTL_NO1 위치가 최하단 재료 1 베드 1 Seq(2026.04.21 L2 관통테스트시 확인)
//		    작업매수 만큼만 재료 번호 있음(L2 관통테스트시 확인)
//			for (int Loop_i = iStkSh; (iStkSh - iWrkSh) < Loop_i; Loop_i--) {
			for (int Loop_i = 1; Loop_i <= iWrkSh; Loop_i++) {
				szStkStlNo 	= "";
				sstlNo 		= commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + Loop_i));	//재료번호
				
				jrParam.setField("SSTL_NO"	, sstlNo );
				
				// 재료번호가 야드저장품에 존재하는지, 작업예약과 크레인스케줄에 잡혀있는 대상인지를 가져오고 BILLET공통에서 현재진도코드를 가져온다. 
				jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getIsEnableStlNo", logId, methodNm, "재료번호 작업대상여부 조회");
				
				if (jsTemp != null && jsTemp.size() > 0) {
					
					jrTemp 		= jsTemp.getRecord(0);
	
					szStkStlNo	= commUtils.trim(jrTemp.getFieldString("SSTL_NO")); // Stock 에 존재하지 않으면 ""
			    } 			
				
				// 저장품에 존재하는 재료번호인지 체크
				if("".equals(szStkStlNo)) {

					sExcptMsg = "실적 누락 " + sstlNo;
					//throw new Exception(sExcptMsg);
					break;
				}
			}
      		
			if("".equals(sExcptMsg)){
				
				// 작업예약등록
				GridData inGridData = new GridData();

				String stlNos = "";
				
// ex) 재료 매수가 4, 작업 매수가 3 이면 아래에서 3매만 작업대상 이기 때문에 작업 대상만 체크			
// 2026.04.21 추출대 트래킹 SSTL_NO1 위치가 최하단 재료 1 베드 1 Seq(2026.04.21 L2 관통테스트시 확인)
//			    작업매수 만큼만 재료 번호 있음(L2 관통테스트시 확인)
//				for (int Loop_i = iStkSh; (iStkSh - iWrkSh) < Loop_i; Loop_i--) {
				for (int Loop_i = 1; Loop_i <= iWrkSh; Loop_i++) {
					
					stlNos += commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + Loop_i));	// 재료번호
					
					// 작업 재료 마지막이 아니면 재료번호 뒤에 "," 추가  
//					if((iStkSh - iWrkSh + 1) < Loop_i) {
					if(Loop_i < iWrkSh) {
						stlNos += ",";
					}
					
				}

				String ydToLocGuide = "";

// TB_YS_RULE GD0002 소형압연 추출대(GDPC11, GDPC21) 예정저장위치
				szMsg = "TB_YS_RULE GD0002 소형압연 추출대(GDPC11, GDPC21) - 예정저장위치 ";
				commUtils.printLog(logId, szMsg, "");
				
				ydToLocGuide = CarTSMv.ApplyRuleItem(logId, methodNm, "GD0002", srydEqpId);
				
				inGridData.addParam("SSTL_NOS", 		stlNos);			// 재료번호들
				inGridData.addParam("YS_STK_COL_GP", 	srydEqpId);			// 야드적치열구분(6자리 이상)
				inGridData.addParam("YD_TO_LOC_GUIDE", 	ydToLocGuide);		// 야드To위치Guide
				inGridData.addParam("YD_WRK_CRN", 		"");				// 야드작업크레인(작업자지정 크레인)
				inGridData.addParam("userid", 			sModifier);			// 수정자
				
				
				szMsg = "\n\t SSTL_NO   		: " 	+ stlNos 
					  + "\n\t YS_STK_COL_GP     : " 	+ srydEqpId 
					  + "\n\t YD_TO_LOC_GUIDE   : " 	+ ydToLocGuide;

				commUtils.printLog(logId, szMsg, "");
				
				jrRtn = sbrYsJsp.updbtMvStkWrkBook(inGridData);
				
			}

			commUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
			
		} catch (DAOException e) {

			throw e;
			
		} catch (Exception e) {

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
			
		} finally {
			try {
				
				if(!"".equals(sExcptMsg)) {

					if(sExcptMsg.indexOf("■Error■ Message :")>0) {
						sExcptMsg = sExcptMsg.substring(sExcptMsg.indexOf("■Error■ Message :")+"■Error■ Message :".length()+1,sExcptMsg.length());
					}
					
					if(sExcptMsg.getBytes().length > YsConstant.YS_EXCPTHIST_EXCPT_MSG_LEN) {
						sExcptMsg = commUtils.substr(sExcptMsg, 0, YsConstant.YS_EXCPTHIST_EXCPT_MSG_LEN); 
					}
					
					if(sIfData.getBytes().length > YsConstant.YS_EXCPTHIST_IF_DATA_LEN) {
						sIfData = commUtils.substr(sIfData, 0, YsConstant.YS_EXCPTHIST_IF_DATA_LEN); 
					}
					
					//TB_YS_EXCPTHIST(예외처리이력 테이블)에 저장
					jrParam.setField("IF_ID"		, msgId  	); 	//인터페이스Id
					jrParam.setField("IF_DATA"		, sIfData  	); 	//인터페이스Data
					jrParam.setField("EXCPT_MSG"	, sExcptMsg );  //예외메세지
					jrParam.setField("MODIFIER"		, logId     ); 	//수정자
					
					EJBConnector tranConn = new EJBConnector("default", "YsCommSeEJB", this);
					tranConn.trx("insExcptHist", new Class[] { JDTORecord.class }, new Object[] { jrParam });
					
				} 
				
			} catch (Exception e) {
				throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
			}			
		}
	}	
		
	
	

	
	/**
	 *      [A] 오퍼레이션명 : 소형압연설비 Tracking 정보(M5YSL302)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvM5YSL302(JDTORecord rcvMsg) throws DAOException {
		String szMsg	= "";
		String methodNm = "소형압연설비 Tracking 정보[SbrYsL2RcvSeEJB.rcvM5YSL302]";
		String logId 	= commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_D);	

		JDTORecord jrParam 		= JDTORecordFactory.getInstance().create();
		String sExcptMsg 		= "";
		String sIfData 			= "";
		String msgId 			= null;

		try {
			commUtils.printLog(logId, methodNm, "S+");


			szMsg = rcvMsg.toString();
			commUtils.printLog(logId, szMsg, "");
			
			/*
			7	YD_EQP_ID			야드설비ID 			CHAR	 6	설비ID (추출대 A(GDPC11), 추출대 B(GDPC21), RT (GDRT01)	
			8	YD_STK_BED_STL_SH	야드적치Bed재료매수	NUMBER	 2	재료 매수				
			9	SSTL_NO1			특수강재료번호1		CHAR	12						
			10	SSTL_NO2			특수강재료번호2		CHAR	12						
			... SSTL_NO3 ~ SSTL_NO18
			27	SSTL_NO19			특수강재료번호19		CHAR	12						
			28	SSTL_NO20			특수강재료번호20		CHAR	12						
			*/
			
			
			//수신 항목 값
			msgId         			= commUtils.getMsgId(rcvMsg); 
			String srydEqpId       	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"        )	); 	// 야드설비ID
			String srydStkBedStlSh 	= commUtils.trim(rcvMsg.getFieldString("YD_STK_BED_STL_SH")	); 	// 야드적치Bed재료매수
			String sModifier      	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"       )	); 	// 수정자(Backup Only)
			String ysStkBedNo    	= "01"; 														// 야드적치Bed번호
			String ysStkLyrNo    	= "01"; 														// 야드적치단번호
			String ysStkSeq    		= "1"; 															// SEQ

			if ("".equals(sModifier)) { sModifier = msgId; }
			
			methodNm = msgId.substring(0, 2) + methodNm;


			szMsg = "\n\t YD_EQP_ID   		: " + srydEqpId 
				  + "\n\t YD_STK_BED_STL_SH : " + srydStkBedStlSh
				  + "\n\t MODIFIER			: " + sModifier 
				  ;

			commUtils.printLog(logId, szMsg, "");
			
			// sExcptMsg 메세지가 있으면 처리
			for (int Loop_i = 0; Loop_i < 20; Loop_i++) {
				sIfData += commUtils.trim(rcvMsg.getFieldString("SSTL_NO"+(Loop_i+1))) + " ";
				if((Loop_i+1) < 20) {
					sIfData += ", ";
				}
			}			
			
						
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (srydEqpId.length() < 6 || !srydEqpId.startsWith("GD")) {
				sExcptMsg = "전문 이상! 설비ID(YD_EQP_ID) 이상 [" + srydEqpId + "]";
			} else if ("".equals(srydStkBedStlSh)) {
				sExcptMsg = "전문 이상! 적치Bed재료매수(YD_STK_BED_STL_SH) 없음";
			}

			if(!"".equals(sExcptMsg)) {
				throw new Exception(sExcptMsg);
			}

			String ysStkColGp    = srydEqpId;

			/**********************************************************
			* 2. 적치Bed 재료번호, 재료상태  및 저장품 Update
			**********************************************************/
			jrParam.setResultCode(logId);					// Log ID
			jrParam.setResultMsg(methodNm);					// Log Method Name
			
			int mtlSh = Integer.parseInt(srydStkBedStlSh); 	// 야드적치Bed재료매수
			String stlNo = "";								// 재료번호
		
			jrParam.setResultCode(logId);							// Log ID
			jrParam.setResultMsg(methodNm);							// Log Method Name
			jrParam.setField("YS_STK_COL_GP"      , ysStkColGp   ); // 야드적치열구분


// 설비 적치열 CLEAR
			jrParam.setField("MODIFIER" , 			sModifier		); // 수정자							
			jrParam.setField("YD_STK_LYR_ACT_STAT", "E"       		); // 야드적치단활성상태
			jrParam.setField("YD_STK_LYR_MTL_STAT", "E"       		); // 야드적치단재료상태(적치가능)
			jrParam.setField("YS_STK_COL_GP" , 		ysStkColGp   	); // 야드적치열구분
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColGpClear", logId, methodNm, "설비 적치단 CLEAR"); 
			
			/**********************************************************
			* 2.2 적재단, 저장품 등록
			**********************************************************/
			
			int iBed = 1;
			int iSeq = 1;
			
// 추출대, RT 별도 처리
			if (srydEqpId.startsWith("GDPC")) {
// 추출대는 중간에 빈칸 없음				
				// 적치열 매수 만큼만 트래킹 처리(추출대 PC는 베드 01 ~ 07, 1 단, 1 ~ 3 SEQ)
				// 소형 압연 방향 07 베드 3 SEQ
				if(20 < mtlSh) {
					mtlSh = 20;
				}

				// 추출대는 RT 쪽이 7 베드, 3 Seq
				iBed = 1;
				iSeq = 1;
				
// 2026.03.04 추출대 트래킹 최하단 재료가 1 베드 1 Seq
// 2026.04.21 추출대 트래킹 SSTL_NO1 위치가 최하단 재료 1 베드 1 Seq(2026.04.21 L2 관통테스트시 확인)				
//				for (int Loop_i = mtlSh; Loop_i > 0; Loop_i--) {
				for (int Loop_i = 1; Loop_i <= mtlSh; Loop_i++) {
					stlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + Loop_i));	// 재료번호
							
					if ("".equals(stlNo)) {
						sExcptMsg = "재료번호(SSTL_NO" + Loop_i + ") 없음";
						throw new Exception(sExcptMsg);
					}
					
					// 추출대는 01 ~ 07 베드
					ysStkBedNo 	= (iBed < 10) ? "0" + String.valueOf(iBed) : String.valueOf(iBed);

					// 추출대는 1 ~ 3 Seq
					ysStkSeq	= String.valueOf(iSeq);
					
					szMsg = "전문 " + Loop_i + "번째 재료번호 [" + stlNo + "] 베드 [" + ysStkBedNo + "] SEQ [" + ysStkSeq + "]" ;
		      		commUtils.printLog(logId, szMsg, "");
					
					jrParam.setField("YS_STK_COL_GP"      	, ysStkColGp   	); // 야드적치열구분
					jrParam.setField("SSTL_NO"       		, stlNo    		); // 재료번호
					jrParam.setField("YS_STK_BED_NO"		, ysStkBedNo 	); // 야드적치Bed번호
					jrParam.setField("YS_STK_LYR_NO"		, ysStkLyrNo  	); // 특수강야드적치단번호
					jrParam.setField("YS_STK_SEQ_NO"		, ysStkSeq		); // 야드적치SEQ
					jrParam.setField("YD_STK_LYR_MTL_STAT"	, "C"       	); // 야드적치단재료상태(적치중)
					jrParam.setField("MODIFIER"           	, sModifier  	); // 수정자							
					//CARRY_OUT 시점에 저장픔 YD_RCPT_DATE UPDATE
					jrParam.setField("CARRY_OUT"	    	, "Y"       	); 

					// 재료번호 이전 저장위치 재료번호 CLEAR
					jrParam.setField("YD_STK_LYR_MTL_STAT", "E"       ); 	// 야드적치단재료상태(적치가능)
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyrStlNoClear", logId, methodNm, "야드 적치단 재료번호 CLEAR"); 

					
					jrParam.setField("YD_STK_LYR_MTL_STAT", "C"       ); 	// 야드적치단재료상태(적치중)
					
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyr", logId, methodNm, "야드 적치단 등록"); 
						
					// 추출대 STOCK 등록
					commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insBundleYdStock2", logId, methodNm, "야드 저장품 등록"); 
						
					iSeq 		= iSeq + 1;
					if(iSeq > 3 ) {
						iSeq 	= 1;
						iBed 	= iBed + 1;
					}
					
				}
			} else if(srydEqpId.startsWith("GDRT01")) {
// RT는 중간에 빈칸 있음
				// 적치열 매수 만큼만 트래킹 처리(RT는 베드 01 ~ 10, 1 단, 1 SEQ) 
				// 추출대 방향 01 베드 1 SEQ
				if(10 < mtlSh) {
					mtlSh = 10;
				}

				for (int Loop_i = 1; Loop_i <= mtlSh; Loop_i++) {
					stlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + Loop_i));	// 재료번호

					if ("".equals(stlNo)) {
						szMsg = "재료번호(SSTL_NO" + Loop_i + ") 없음";
			      		commUtils.printLog(logId, szMsg, "");

						continue;
					}
					
					// RT는 01 ~ 10 베드
					iBed 		= Loop_i;
					ysStkBedNo 	= (iBed < 10) ? "0" + String.valueOf(iBed) : String.valueOf(iBed);

					// RT는 1 Seq
					ysStkSeq	= String.valueOf(1);
					
					szMsg = "전문 " + Loop_i + "번째 재료번호 [" + stlNo + "] 베드 [" + ysStkBedNo + "] SEQ [" + ysStkSeq + "]" ;
		      		commUtils.printLog(logId, szMsg, "");
					
					jrParam.setField("YS_STK_COL_GP"      	, ysStkColGp   	); // 야드적치열구분
					jrParam.setField("SSTL_NO"       		, stlNo    		); // 재료번호
					jrParam.setField("YS_STK_BED_NO"		, ysStkBedNo 	); // 야드적치Bed번호
					jrParam.setField("YS_STK_LYR_NO"		, ysStkLyrNo  	); // 특수강야드적치단번호
					jrParam.setField("YS_STK_SEQ_NO"		, ysStkSeq		); // 야드적치SEQ
					jrParam.setField("YD_STK_LYR_MTL_STAT"	, "C"       	); // 야드적치단재료상태(적치중)
					jrParam.setField("MODIFIER"           	, sModifier  	); // 수정자							
					// CARRY_OUT 시점에 저장픔 YD_RCPT_DATE UPDATE
					jrParam.setField("CARRY_OUT"	    	, "Y"       	); 

					// 재료번호 이전 저장위치 재료번호 CLEAR
					jrParam.setField("YD_STK_LYR_MTL_STAT", "E"       ); 	// 야드적치단재료상태(적치가능)
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyrStlNoClear", logId, methodNm, "야드 적치단 재료번호 CLEAR"); 
					
					jrParam.setField("YD_STK_LYR_MTL_STAT", "C"       ); 	// 야드적치단재료상태(적치중)
					
					
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyr", logId, methodNm, "야드 적치단 등록"); 
						
					// RT STOCK 등록
					commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insBundleYdStock2", logId, methodNm, "야드 저장품 등록"); 
					
				}
				
			}

			JDTORecord jrRtn = null;
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch (DAOException e) {

			throw e;
			
		} catch (Exception e) {

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
			
		} finally {
			try {
				
				if(!"".equals(sExcptMsg)) {

					if(sExcptMsg.indexOf("■Error■ Message :")>0) {
						sExcptMsg = sExcptMsg.substring(sExcptMsg.indexOf("■Error■ Message :")+"■Error■ Message :".length()+1,sExcptMsg.length());
					}
					
					if(sExcptMsg.getBytes().length > YsConstant.YS_EXCPTHIST_EXCPT_MSG_LEN) {
						sExcptMsg = commUtils.substr(sExcptMsg, 0, YsConstant.YS_EXCPTHIST_EXCPT_MSG_LEN); 
					}
					
					if(sIfData.getBytes().length > YsConstant.YS_EXCPTHIST_IF_DATA_LEN) {
						sIfData = commUtils.substr(sIfData, 0, YsConstant.YS_EXCPTHIST_IF_DATA_LEN); 
					}
					
					//TB_YS_EXCPTHIST(예외처리이력 테이블)에 저장
					jrParam.setField("IF_ID"		, msgId  	); 	//인터페이스Id
					jrParam.setField("IF_DATA"		, sIfData  	); 	//인터페이스Data
					jrParam.setField("EXCPT_MSG"	, sExcptMsg );  //예외메세지
					jrParam.setField("MODIFIER"		, logId     ); 	//수정자
					
					//commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insExcptHist", logId, methodNm, "예외처리이력 테이블 등록");
					EJBConnector tranConn = new EJBConnector("default", "YsCommSeEJB", this);
					tranConn.trx("insExcptHist", new Class[] { JDTORecord.class }, new Object[] { jrParam });
					
				} 
				
			} catch (Exception e) {
				throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
			}			
		}
	}
		
	
	/**
	 *      [A] 오퍼레이션명 : 번들공통 UPDATE
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return void
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public void updBndlCommYsStrLoc(JDTORecord jrParam) throws DAOException {
		String methodNm = "번들공통 UPDATE[SbrYsL2RcvSeEJB.updBndlCommYsStrLoc]";
		String logId 	= jrParam.getResultCode();

		try {
			
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBndlCommYsStrLoc", logId, methodNm, "번들공통 UPDATE");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	

	
	/**
	 *      [A] 오퍼레이션명 : 대차이동요구(YSN7L323)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord getYSN7L323(String logId, String ydEqpId, String StLoc, String EndLoc) throws DAOException {

		String szMsg	= "";
		String methodNm = "소형야드 대차이동요구 전문 편집[SbrYsL2RcvSeEJB.getYSN7L323]";

		try {

			commUtils.printLog(logId, methodNm, "S+");

			szMsg = "\n\t YD_EQP_ID  : " 	+ ydEqpId 
				  + "\n\t ST_LOC     : " 	+ StLoc 
				  + "\n\t END_LOC    : " 	+ EndLoc 
				  ;

			commUtils.printLog(logId, szMsg, "");
			
			String szTCID      = "YSN7L323";											// 전문ID : YSN7L323(대차이동요구)
			StringBuffer sbMsg = new StringBuffer();

			sbMsg = sbMsg.append(szTCID										); 			// 전문ID : YSN7L323(대차이동요구)
			sbMsg = sbMsg.append(commUtils.getDateTime18()					); 			// 생성일,생성시간(yyyy-MM-ddHH:mm:ss)
			sbMsg = sbMsg.append("I"										); 			// 전문구분
			sbMsg = sbMsg.append("0058"										); 			// 전문길이
			sbMsg = sbMsg.append(commUtils.getRPad(" "       , 29, " "	)	); 			// 임시
			sbMsg = sbMsg.append(commUtils.getRPad(ydEqpId   ,  6, " "	)	); 			// 야드설비ID
			sbMsg = sbMsg.append(StLoc										); 			// 출발위치
			sbMsg = sbMsg.append(EndLoc										); 			// 도착위치
			sbMsg = sbMsg.append(commUtils.getRPad(" "       , 50, " "	)	); 			// SPARE

			JDTORecord sndMsg = JDTORecordFactory.getInstance().create();

			sndMsg.setResultCode(logId);	//Log ID
			sndMsg.setResultMsg(methodNm);	//Log Method Name
			sndMsg.addField("JMS_TC_CD"          , szTCID					); // JMS TC 코드
			sndMsg.addField("JMS_TC_CREATE_DDTT" , commUtils.getDateTime14()); // JMS TC 생성일시(yyyyMMddHHmmss)
			sndMsg.addField("JMS_TC_MESSAGE"     , sbMsg.toString()         ); // JMS TC Message

			szMsg = "\n\t sndMsg	: " + sndMsg.toString();
			commUtils.printLog(logId, szMsg, "");
			
			//전송 Data Return
			return commUtils.addSndData(sndMsg);
		} catch (Exception e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return null;
		}
	}
	

	
	/**
	 *      [A] 오퍼레이션명 : 대차 진행상태 처리 유무 RETURN
	 *      @param  String, String, 
	 *      @return String
	 *      @throws DAOException
	*/
	public String TcProgStatChg(String logId, String modifier, String sYD_TC_SCH_ID, String sYD_CAR_PROG_STAT, String sWRK_CRN, String sWRK_BOOK_ID) throws DAOException {

		String mthdNm 	= "대차 진행상태 처리[SbrYsL2RcvSeEJB.TcProgStatChg]";
		String szRet 	= "N";
		String szMsg	= "";

		try {
			commUtils.printLog(logId, mthdNm, "S+");

			String szDEL_YN = "N";
			
		    int intRtnVal	= 0 ;
			
			// E : 하차완료 이면 종료
			if ("E".equals(sYD_CAR_PROG_STAT)) {
				szDEL_YN 	= "Y";
			}
			
			
			JDTORecord	recParaTc = JDTORecordFactory.getInstance().create();

			recParaTc.setField("YD_TCAR_SCH_ID",	sYD_TC_SCH_ID		);		// 야드대차스케쥴ID
			recParaTc.setField("MODIFIER",			modifier			);		// 등록자
			recParaTc.setField("DEL_YN",			szDEL_YN			);		// 삭제유무

			if ("Y".equals(szDEL_YN)) {
				intRtnVal = commDao.update(recParaTc, "com.inisteel.cim.ys.sbr.dao.updTcarSchMtlEnd", logId, mthdNm, "소형 야드 대차 스케줄 재료 종료");
				
				if( intRtnVal <= 0 ){
					szMsg	= mthdNm + " 소형 야드 대차 스케줄 재료 종료 시 오류발생 - 반환값 : " + intRtnVal;
					commUtils.printLog(logId, szMsg, "SL");
				} else {
		    		szMsg	= mthdNm + " 소형 야드 대차 스케줄 재료 종료 완료 - 반환값 : " + intRtnVal;
		    		commUtils.printLog(logId, szMsg, "SL");
				}
			}			

			/**********************************************************
			* 야드차량진행상태
			* 0 : 상차대기
			* 1 : 상차출발
			* 2 : 상차도착
			* 3 : 상차검수
			* 4 : 상차개시
			* 5 : 상차완료
			* A : 하차출발
			* B : 하차도착
			* C : 하차검수
			* D : 하차개시
			* E : 하차완료
			**********************************************************/
			
			if ("1".equals(sYD_CAR_PROG_STAT)) {
				recParaTc.setField("YD_CARLD_LEV_DT_YN",	"Y"				);		// 야드상차출발일시 유무
				
				szMsg	= "야드상차출발일시 유무 [Y] 설정";
				commUtils.printLog(logId, szMsg, "SL");
				
			} else if ("2".equals(sYD_CAR_PROG_STAT)) {
				recParaTc.setField("YD_CARLD_ARR_DT_YN",	"Y"				);		// 야드상차도착일시 유무
				
				szMsg	= "야드상차도착일시 유무 [Y] 설정";
				commUtils.printLog(logId, szMsg, "SL");
				
			} else if ("4".equals(sYD_CAR_PROG_STAT)) {
				recParaTc.setField("YD_CARLD_ST_DT_YN",		"Y"				);		// 야드상차개시일시 유무
				recParaTc.setField("YD_CARLD_WRK_CRN",		sWRK_CRN		);		// 야드상차작업크레인
				recParaTc.setField("YD_CARLD_WRK_BOOK_ID",	sWRK_BOOK_ID	);		// 야드상차작업예약ID
				
				szMsg	= "야드상차개시일시 유무 [Y] 설정";
				commUtils.printLog(logId, szMsg, "SL");
				
			} else if ("5".equals(sYD_CAR_PROG_STAT)) {
				recParaTc.setField("YD_CARLD_CMPL_DT_YN",	"Y"				);		// 야드상차완료일시 유무
				recParaTc.setField("YD_CARLD_WRK_CRN",		sWRK_CRN		);		// 야드상차작업크레인
				recParaTc.setField("YD_CARLD_WRK_BOOK_ID",	sWRK_BOOK_ID	);		// 야드상차작업예약ID
				
				szMsg	= "야드상차완료일시 유무 [Y] 설정";
				commUtils.printLog(logId, szMsg, "SL");
				
			} else if ("A".equals(sYD_CAR_PROG_STAT)) {
				recParaTc.setField("YD_CARUD_LEV_DT_YN",	"Y"				);		// 야드하차출발일시 유무
				
				szMsg	= "야드하차출발일시 유무 [Y] 설정";
				commUtils.printLog(logId, szMsg, "SL");
				
			} else if ("B".equals(sYD_CAR_PROG_STAT)) {
				recParaTc.setField("YD_CARUD_ARR_DT_YN",	"Y"				);		// 야드하차도착일시 유무
				
				szMsg	= "야드하차도착일시 유무 [Y] 설정";
				commUtils.printLog(logId, szMsg, "SL");
				
			} else if ("D".equals(sYD_CAR_PROG_STAT)) {
				recParaTc.setField("YD_CARUD_ST_DT_YN",		"Y"				);		// 야드하차개시일시 유무
				recParaTc.setField("YD_CARUD_WRK_CRN",		sWRK_CRN		);		// 야드하차작업크레인
				recParaTc.setField("YD_CARUD_WRK_BOOK_ID",	sWRK_BOOK_ID	);		// 야드하차작업예약ID
				
				szMsg	= "야드하차개시일시 유무 [Y] 설정";
				commUtils.printLog(logId, szMsg, "SL");
				
			} else if ("E".equals(sYD_CAR_PROG_STAT)) {
				recParaTc.setField("YD_CARUD_CMPL_DT_YN",	"Y"				);		// 야드하차완료일시 유무
				recParaTc.setField("YD_CARUD_WRK_CRN",		sWRK_CRN		);		// 야드하차작업크레인
				recParaTc.setField("YD_CARUD_WRK_BOOK_ID",	sWRK_BOOK_ID	);		// 야드하차작업예약ID
				
				szMsg	= "야드하차완료일시 유무 [Y] 설정";
				commUtils.printLog(logId, szMsg, "SL");
				
			} 
			
			recParaTc.setField("YD_CAR_PROG_STAT", 		sYD_CAR_PROG_STAT	);		// 야드차량진행상태
			
			intRtnVal = commDao.update(recParaTc, "com.inisteel.cim.ys.sbr.dao.updTcarSchInsSchSbr", logId, mthdNm, "소형 야드 대차 스케줄 등록 및 갱신");
			
			if( intRtnVal <= 0 ){
				szMsg	= mthdNm + " 소형 야드 대차 스케줄 등록 및 갱신 시 오류발생 - 반환값 : " + intRtnVal;
				commUtils.printLog(logId, szMsg, "SL");
			} else {
				szMsg	= mthdNm + " 소형 야드 대차 스케줄 등록 및 갱신 완료 - 반환값 : " + intRtnVal;
				commUtils.printLog(logId, szMsg, "SL");
			}
			
			commUtils.printLog(logId, mthdNm, "S-");

			szRet 	= "Y";
			
			return szRet;
			
		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, mthdNm, e), this, e);
			return szRet;
		} catch (Exception e) {
			return szRet;
		}
	}
	

	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업실적응답(YSN7L304)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord getYSN7L304(JDTORecord rcvMsg) throws DAOException {
		String szMsg	= "";
		String methodNm = "소형야드 크레인작업실적응답 조회[SbrYsL2RcvSeEJB.getYSN7L304]";
		String logId 	= rcvMsg.getResultCode();

		try {

			commUtils.printLog(logId, methodNm, "S+");

			szMsg = rcvMsg.toString();
			commUtils.printLog(logId, szMsg, "");
			
			// 수신 항목 값
			String msgId      = ""; 														// 전문ID
			String srydEqpId    	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"			)); 	// 야드설비ID
			String srydWrkProgStat	= commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT"	)); 	// 야드작업진행상태
			String srydSchCd		= commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"			)); 	// 야드스케쥴코드
			String srydCrnSchId		= commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"		)); 	// 야드크레인스케쥴ID
			String srydL2WrGp   	= commUtils.trim(rcvMsg.getFieldString("YD_L2_WR_GP"		)); 	// 야드L2실적구분
			String srydL3HdRsCd 	= commUtils.trim(rcvMsg.getFieldString("YD_L3_HD_RS_CD"		)); 	// 야드L3처리결과코드
			String srydL3Msg    	= commUtils.trim(rcvMsg.getFieldString("YD_L3_MSG"			)); 	// 야드L3MESSAGE

			szMsg = "\n\t YD_EQP_ID   		: " 	+ srydEqpId 
				  + "\n\t YD_WRK_PROG_STAT	: " 	+ srydWrkProgStat 
				  + "\n\t YD_SCH_CD   		: " 	+ srydSchCd 
				  + "\n\t YD_CRN_SCH_ID   	: " 	+ srydCrnSchId 
				  + "\n\t YD_L2_WR_GP      	: " 	+ srydL2WrGp 
				  + "\n\t YD_L3_HD_RS_CD    : " 	+ srydL3HdRsCd 
				  + "\n\t YD_L3_MSG   		: " 	+ srydL3Msg 
				  ;

			commUtils.printLog(logId, szMsg, "");
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(srydEqpId)) {
				szMsg = "\n\t YD_EQP_ID 없음";
				commUtils.printLog(logId, szMsg, "");

				return null;
			}

			if (srydEqpId.startsWith("GD")) {
				// YSN7L304(크레인작업실적응답)
				msgId = "YSN7L304";

				szMsg = "\n\t msgId		: " + msgId;
				commUtils.printLog(logId, szMsg, "");
				
			} else {
				szMsg = "\n\t YD_EQP_ID 특수강 소형야드 아님";
				commUtils.printLog(logId, szMsg, "");

				return null;
			}

			/**********************************************************
			* 2. 크레인작업실적응답 전문 생성
			**********************************************************/
			// 야드L3Message가 없으면 생성
			if ("".equals(srydL3Msg)) {
				if ("U".equals(srydL2WrGp)) {
					srydL3Msg = "권상실적";
				} else if ("D".equals(srydL2WrGp)) {
					srydL3Msg = "권하실적";
				} else if ("R".equals(srydL2WrGp)) {
					srydL3Msg = "고장복구실적";
				} else if ("M".equals(srydL2WrGp)) {
					srydL3Msg = "운전모드전환";
				} else if ("J".equals(srydL2WrGp)) {
					srydL3Msg = "지시요구";
				} else {
					srydL3Msg = srydL2WrGp;
				}

				if ("0000".equals(srydL3HdRsCd)) {
					srydL3Msg = srydL3Msg + " 정상 처리";
				} else if ("9999".equals(srydL3HdRsCd)) {
					srydL3Msg = srydL3Msg + " 정보 없음";
				} else {
					srydL3Msg = srydL3Msg + " 오류 <" + logId + ">";
				}
			}

			szMsg = "\n\t ydL3Msg	: " + srydL3Msg;
			commUtils.printLog(logId, szMsg, "");
			

			StringBuffer sbMsg = new StringBuffer();

			sbMsg = sbMsg.append(msgId												); 		// 전문ID
			sbMsg = sbMsg.append(commUtils.getDateTime18()							); 		// 생성일,생성시간(yyyy-MM-ddHH:mm:ss)
			sbMsg = sbMsg.append("I"												); 		// 전문구분
			sbMsg = sbMsg.append("0128"												); 		// 전문길이
			sbMsg = sbMsg.append(commUtils.getRPad(" ", 				29, 	" ")); 		// 임시
			sbMsg = sbMsg.append(commUtils.getRPad(srydEqpId,			 6, 	" ")); 		// 야드설비ID
			sbMsg = sbMsg.append(commUtils.getRPad(srydWrkProgStat,		 1, 	" ")); 		// 야드작업진행상태
			sbMsg = sbMsg.append(commUtils.getRPad(srydSchCd,			 8, 	" ")); 		// 야드스케쥴코드
			sbMsg = sbMsg.append(commUtils.getRPad(srydCrnSchId, 		18, 	" ")); 		// 야드크레인스케쥴ID
			sbMsg = sbMsg.append(commUtils.getRPad(srydL2WrGp  ,  		 1, 	" ")); 		// 야드L2실적구분
			sbMsg = sbMsg.append(commUtils.getRPad(srydL3HdRsCd,  		 4, 	" ")); 		// 야드L3처리결과코드
			sbMsg = sbMsg.append(commUtils.getRPad(srydL3Msg	, 		40, 	" ")); 		// 야드L3Message
			sbMsg = sbMsg.append(commUtils.getRPad(" "		, 			50, 	" ")); 		// SPARE

			JDTORecord sndMsg = JDTORecordFactory.getInstance().create();

			sndMsg.setResultCode(logId);	//Log ID
			sndMsg.setResultMsg(methodNm);	//Log Method Name
			sndMsg.addField("JMS_TC_CD"          , msgId                    ); // JMS TC 코드
			sndMsg.addField("JMS_TC_CREATE_DDTT" , commUtils.getDateTime14()); // JMS TC 생성일시(yyyyMMddHHmmss)
			sndMsg.addField("JMS_TC_MESSAGE"     , sbMsg.toString()         ); // JMS TC Message

			szMsg = "\n\t sndMsg	: " + sndMsg.toString();
			commUtils.printLog(logId, szMsg, "");
			
			commUtils.printLog(logId, methodNm, "S-");
			
			//전송 Data Return
			return commUtils.addSndData(sndMsg);
		} catch (Exception e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return null;
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 열처리작업지시, 정정작업지시, 열처리이동요구, 정정이동요구 DEL_YN 처리 유무 RETURN
	 *      @param  String, String, 
	 *      @return String
	 *      @throws DAOException
	*/
	public String setDelYn(String logId, String modifier, String srYD_CRN_SCH_ID, String srDEL_YN) throws DAOException {

		String mthdNm 	= "열처리작업지시, 정정작업지시, 열처리이동요구, 정정이동요구, 포항이송재 종료 처리[SbrYsL2RcvSeEJB.setDelYn]";
		String szRet 	= "N";
		String szMsg	= "";

		try {
			commUtils.printLog(logId, mthdNm, "S+");

			int intRtnVal	= 0 ;
			
			szMsg = "\n\t YD_CRN_SCH_ID   : " 	+ srYD_CRN_SCH_ID 
				  + "\n\t DEL_YN          : " 	+ srDEL_YN 
				  + "\n\t MODIFIER        : " 	+ modifier
				  ;
			
			commUtils.printLog(logId, szMsg, "");

			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);									// Log ID
			jrParam.setResultMsg(mthdNm);									// Log Method Name
			
			jrParam.setField("YD_CRN_SCH_ID"		, srYD_CRN_SCH_ID	); 	// 야드설비ID
			jrParam.setField("DEL_YN"           	, srDEL_YN			); 	// 삭제유무
			jrParam.setField("MODIFIER"           	, modifier			); 	// 수정자
			
			// 정정작업지시 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.sbr.dao.updStbr_WrkWo", logId, mthdNm, "정정작업지시 수정");
			
			// 열처리작업지시 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.sbr.dao.updHttrt_WrkWo", logId, mthdNm, "열처리작업지시 수정");
			
			// 포항이송재 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.sbr.dao.updPohangMoveReq", logId, mthdNm, "포항이송재 수정");
			
			// 정정이송요구 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.sbr.dao.updStbrMoveReq", logId, mthdNm, "정정이송요구 수정");
			
			// 열처리이송요구 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.sbr.dao.updHttrtMoveReq", logId, mthdNm, "열처리이송요구 수정");
			
			commUtils.printLog(logId, mthdNm, "S-");

			szRet 	= "Y";
			
			return szRet;
			
		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, mthdNm, e), this, e);
			return szRet;
		} catch (Exception e) {
			return szRet;
		}
	}
	

	
} // end of class SbrYsL2RcvSeEJBBean


