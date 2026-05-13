/**
 * @(#)CbtYsL2RcvSeEJBBean
 *
 * @version          V1.00
 * @author           
 * @date             2025.07.21
 *
 * @description      특수강 정정 야드 L2 수신 처리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자                    요청자           수정자           내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2025.07.21                   최초 등록
 */

package com.inisteel.cim.ys.cbt.session;

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
import com.inisteel.cim.ys.cbt.session.CbtYsJspSeEJBBean; 
import xlib.cmc.GridData;


/**
 *      [A] 클래스명 : 특수강 정정 야드 L2수신 처리
 *
 * @ejb.bean name="CbtYsL2RcvSeEJB" jndi-name="CbtYsL2RcvSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
*/
 
public class CbtYsL2RcvSeEJBBean extends BaseSessionBean {
	
	private static final long serialVersionUID = 1L;
	
	private YsCommUtils 		commUtils 	= new YsCommUtils();
	private YsCommDAO 			commDao 	= new YsCommDAO();
	private CbtYsComm 			cbtYsComm 	= new CbtYsComm();
	private CbtYsJspSeEJBBean	CbtYsJsp   	= new CbtYsJspSeEJBBean();  

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
	 *      [A] 오퍼레이션명 : 저장위치제원요구(N7YSL201)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvN7YSL201(JDTORecord rcvMsg) throws DAOException {
        String szMsg	= "";
//        String methodNm = "저장위치제원요구[CbtYsL2RcvSeEJB.rcvN7YSL201] < " + rcvMsg.getResultMsg();
        String methodNm = "저장위치제원요구[CbtYsL2RcvSeEJB.rcvN7YSL201] < ";
        String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);	

		commUtils.printLog(logId, methodNm, "S+");
        
        szMsg = rcvMsg.toString();
		commUtils.printLog(logId, szMsg, "");
		
		
		try {

			
            /*
			7	YD_INFO_SYNC_CD	야드정보동기화코드		CHAR	1
			8	YD_GP			야드구분			CHAR	1
			9	YD_BAY_GP		야드동구분			CHAR	1
			10	YD_EQP_GP		야드설비구분			CHAR	2
			11	YS_STK_COL_NO	특수강야드적치열번호	CHAR	2
			12	YS_STK_BED_NO	특수강야드적치Bed번호	CHAR	2
            */

			// 수신 항목 값
			String msgId        = commUtils.getMsgId(rcvMsg); // EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydInfoSyncCd = commUtils.trim(rcvMsg.getFieldString("YD_INFO_SYNC_CD")); // 야드정보동기화코드
			String ydGp         = commUtils.trim(rcvMsg.getFieldString("YD_GP"          )); // 야드구분
			String ydBayGp      = commUtils.trim(rcvMsg.getFieldString("YD_BAY_GP"      )); // 야드동구분
			String ydEqpGp      = commUtils.trim(rcvMsg.getFieldString("YD_EQP_GP"      )); // 야드설비구분
			String ydStkColNo   = commUtils.trim(rcvMsg.getFieldString("YS_STK_COL_NO"  )); // 야드적치열번호
			String ydStkBedNo   = commUtils.trim(rcvMsg.getFieldString("YS_STK_BED_NO"  )); // 야드적치Bed번호
			
			methodNm = msgId.substring(0, 2) + methodNm;

            szMsg = "\n\t YD_INFO_SYNC_CD   : " 	+ ydInfoSyncCd 
              	  + "\n\t YD_GP             : " 	+ ydGp 
              	  + "\n\t YD_BAY_GP         : " 	+ ydBayGp 
              	  + "\n\t YD_EQP_GP         : " 	+ ydEqpGp 
              	  + "\n\t YS_STK_COL_NO     : " 	+ ydStkColNo 
              	  + "\n\t YS_STK_BED_NO     : " 	+ ydStkBedNo;

    		commUtils.printLog(logId, szMsg, "");
            
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydGp)) {
				throw new Exception("야드구분(YD_GP) 없음");
			} else if (!"G".equals(ydGp)) {
				throw new Exception("야드구분(YD_GP) 다름");
			} else if ("".equals(ydBayGp) && (!"".equals(ydEqpGp) || "".equals(ydStkColNo))) {
				throw new Exception("야드동구분(YD_BAY_GP) 없음");
			} else if ("".equals(ydEqpGp) && !"".equals(ydStkColNo)) {
				throw new Exception("야드설비구분(YD_EQP_GP) 없음");
			}

			/**********************************************************
			* 2. 저장위치제원(YSN7L201) 전문 생성
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	// Log ID
			jrParam.setResultMsg(methodNm);	// Log Method Name

			jrParam.setField("YD_INFO_SYNC_CD", ydInfoSyncCd                         ); // 야드정보동기화코드
			jrParam.setField("YS_STK_COL_GP"  , ydGp + ydBayGp + ydEqpGp + ydStkColNo); // 야드적치열구분
			jrParam.setField("YS_STK_BED_NO"  , ydStkBedNo                           ); // 야드적치Bed번호

			//전송 Data 생성
			JDTORecord jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN7L201", jrParam));
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	}
	
	
	
	

	/**
	 *      [A] 오퍼레이션명 : 저장품제원요구(N7YSL202)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvN7YSL202(JDTORecord rcvMsg) throws DAOException {
        String szMsg	= "";
        String methodNm = "저장품제원요구[CbtYsL2RcvSeEJB.rcvN7YSL202] < " + rcvMsg.getResultMsg();
        String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);	

		commUtils.printLog(logId, methodNm, "S+");
        
        szMsg = rcvMsg.toString();
		commUtils.printLog(logId, szMsg, "");
		
		
//		szMsg = "N7저장위치제원요구 처리(" + methodNm + ") 시작";
//		ysUtils.putLog(methodNm, szMsg, logId);
		
		try {

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
			String msgId        = commUtils.getMsgId(rcvMsg); // EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydInfoSyncCd = commUtils.trim(rcvMsg.getFieldString("YD_INFO_SYNC_CD")); // 야드정보동기화코드
			String ydGp         = commUtils.trim(rcvMsg.getFieldString("YD_GP"          )); // 야드구분
			String ydBayGp      = commUtils.trim(rcvMsg.getFieldString("YD_BAY_GP"      )); // 야드동구분
			String ydEqpGp      = commUtils.trim(rcvMsg.getFieldString("YD_EQP_GP"      )); // 야드설비구분
			String ydStkColNo   = commUtils.trim(rcvMsg.getFieldString("YS_STK_COL_NO"  )); // 야드적치열번호
			String ydStkBedNo   = commUtils.trim(rcvMsg.getFieldString("YS_STK_BED_NO"  )); // 야드적치Bed번호
			String SstlNo       = commUtils.trim(rcvMsg.getFieldString("SSTL_NO"        )); // 재료번호
			
			methodNm = msgId.substring(0, 2) + methodNm;

            szMsg = "\n\t YD_INFO_SYNC_CD   : " 	+ ydInfoSyncCd 
              	  + "\n\t YD_GP             : " 	+ ydGp 
              	  + "\n\t YD_BAY_GP         : " 	+ ydBayGp 
              	  + "\n\t YD_EQP_GP         : " 	+ ydEqpGp 
              	  + "\n\t YS_STK_COL_NO     : " 	+ ydStkColNo 
              	  + "\n\t YS_STK_BED_NO     : " 	+ ydStkBedNo
              	  + "\n\t SSTL_NO     		: " 	+ SstlNo;

    		commUtils.printLog(logId, szMsg, "");
            
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydGp)) {
				throw new Exception("야드구분(YD_GP) 없음");
			} else if (!"G".equals(ydGp)) {
				throw new Exception("야드구분(Yd_GP) 다름");
			} else if (!"E".equals(ydBayGp)) {
				throw new Exception("동구분(ydBayGp) 다름");
			}

			if ("1".equals(ydInfoSyncCd) || "2".equals(ydInfoSyncCd) || "3".equals(ydInfoSyncCd) || "4".equals(ydInfoSyncCd)) {
				//저장위치별
				if ("".equals(ydBayGp) && (!"".equals(ydEqpGp) || "".equals(ydStkColNo))) {
					throw new Exception("야드동구분(YS_BAY_GP) 없음");
				} else if ("".equals(ydEqpGp) && !"".equals(ydStkColNo)) {
					throw new Exception("야드설비구분(YS_EQP_GP) 없음");
				}
			} else {
				//재료별
				if ("".equals(SstlNo)) {
					throw new Exception("재료번호(SSTL_NO) 없음");
				}
			}

			/**********************************************************
			* 2. 저장품제원(YSN1L002) 전문 생성
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("YD_INFO_SYNC_CD", ydInfoSyncCd                         ); // 야드정보동기화코드
			jrParam.setField("YS_STK_COL_GP"  , ydGp + ydBayGp + ydEqpGp + ydStkColNo); // 야드적치열구분
			jrParam.setField("YS_STK_BED_NO"  , ydStkBedNo                           ); // 야드적치Bed번호
			jrParam.setField("YD_GP"          , ydGp                                 ); // 야드구분
			jrParam.setField("SSTL_NO"        , SstlNo                               ); // 재료번호

			//전송Data 생성
			JDTORecord jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN7L202", jrParam));
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 설비고장복구실적(N7YSL203)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN7YSL203(JDTORecord rcvMsg) throws DAOException {
        String szMsg	= "";
		String methodNm = "설비고장복구실적[CbtYsL2RcvSeEJB.rcvN7YSL203] < " + rcvMsg.getResultMsg();
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);	
		
		
		JDTORecord resMsg = JDTORecordFactory.getInstance().create(); // 크레인작업실적응답 전문 생성용
		boolean resYn = false;	// 크레인작업실적응답 전문 전송여부

		commUtils.printLog(logId, methodNm, "S+");
        
        szMsg = rcvMsg.toString();
		commUtils.printLog(logId, szMsg, "");
		

		try {
			
            /*
			7	YD_EQP_ID			야드설비ID			CHAR	6
			8	YD_EQP_STAT			야드설비상태			CHAR	1
			9	YD_EQP_PAUSE_CODE	야드설비휴지코드		CHAR	4
			10	YD_EQP_TRBL_RCVR_DT	야드설비고장복구일시	CHAR	14
            */


			//수신 항목 값
			String msgId           = commUtils.getMsgId(rcvMsg); 									// EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId         = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"          )); 	// 야드설비ID
			String ydEqpStat       = commUtils.trim(rcvMsg.getFieldString("YD_EQP_STAT"        )); 	// 야드설비상태(B:고장, N:정상, R:복구 등)
			String ydEqpPauseCode  = commUtils.trim(rcvMsg.getFieldString("YD_EQP_PAUSE_CODE"  )); 	// 야드설비휴지코드
			String ydEqpTrblRcvrDt = commUtils.trim(rcvMsg.getFieldString("YD_EQP_TRBL_RCVR_DT")); 	// 야드설비고장복구일시
			String modifier        = commUtils.trim(rcvMsg.getFieldString("MODIFIER"           )); 	// 수정자(Backup Only)
			String brGp            = ""; //고장복구구분
			
			if ("".equals(modifier)) { modifier = msgId; }
			
			methodNm = msgId.substring(0, 2) + methodNm;


            szMsg = "\n\t YD_EQP_ID   			: " 	+ ydEqpId 
              	  + "\n\t YD_EQP_STAT           : " 	+ ydEqpStat 
              	  + "\n\t YD_EQP_PAUSE_CODE     : " 	+ ydEqpPauseCode 
              	  + "\n\t YD_EQP_TRBL_RCVR_DT   : " 	+ ydEqpTrblRcvrDt 
              	  + "\n\t MODIFIER     			: " 	+ modifier 
              	  ;

    		commUtils.printLog(logId, szMsg, "");
			
			// 크레인작업실적응답 전문 전송여부를 Check
			if (msgId.startsWith("N7") || (ydEqpId.length() == 6 && "CR".equals(ydEqpId.substring(2, 4)))) {
				resYn = true;
			}

			JDTORecord jrRtn = null;	// 전문 Return
			String ydL3HdRsCd = "";		// 야드L3처리결과코드
			String ydL3Msg    = ""; 	// 야드L3MESSAGE

			// 크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);											// Log ID
			resMsg.setResultMsg(methodNm);											// Log Method Name
			resMsg.setField("YD_EQP_ID"       , ydEqpId       ); 					// 야드설비ID
			resMsg.setField("YD_L2_WR_GP"     , "R"           ); 					// 야드L2실적구분(고장복구실적)
			resMsg.setField("YD_L3_HD_RS_CD"  , "BR99"        ); 					// 야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:설비고장복구실적 수신처리"); 				// 야드L3MESSAGE(Error)

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				ydL3HdRsCd = "BR01";
				ydL3Msg    = "오류:설비ID 없음";
			} else if (ydEqpId.length() < 6 || !ydEqpId.startsWith("GE")) {
				ydL3HdRsCd = "BR02";
				ydL3Msg    = "오류:설비ID[" + ydEqpId + "] 이상";
			} else if ("".equals(ydEqpStat)) {
				ydL3HdRsCd = "BR03";
				ydL3Msg    = "오류:야드설비상태 없음";
			} else if ("".equals(ydEqpPauseCode) && "B".equals(ydEqpStat)) {
				ydL3HdRsCd = "BR04";
				ydL3Msg    = "오류:설비휴지코드 없음";
			} else if ("".equals(ydEqpTrblRcvrDt) && ("B".equals(ydEqpStat) || "N".equals(ydEqpStat) || "R".equals(ydEqpStat))) {
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
			if ("B".equals(ydEqpStat)) {
				brGp = "B"; //고장
				if ("0000".equals(ydEqpPauseCode)) {
					ydEqpPauseCode = "B000";
				}
			} else if ("N".equals(ydEqpStat) || "R".equals(ydEqpStat)) {
				brGp = "R"; //복구
				if ("0000".equals(ydEqpPauseCode)) {
					ydEqpPauseCode = "R000";
				}

				if ("CR".equals(ydEqpId.substring(2, 4))) {
					ydEqpStat = "W";
				} else {
					ydEqpStat = "N";
				}
			}

			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);								// Log ID
			jrParam.setResultMsg(methodNm);								// Log Method Name
			jrParam.setField("YD_EQP_ID"          , ydEqpId        ); 	// 야드설비ID
			jrParam.setField("YD_EQP_PAUSE_CODE"  , ydEqpPauseCode ); 	// 야드설비휴지코드
			jrParam.setField("YD_EQP_PAUSE_OCC_DT", ydEqpTrblRcvrDt); 	// 야드설비휴지발생일시
			jrParam.setField("YD_EQP_STAT"        , ydEqpStat      ); 	// 야드설비상태
			jrParam.setField("BR_GP"              , brGp           ); 	// 고장복구구분
			jrParam.setField("MODIFIER"           , modifier       ); 	// 수정자

			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStatEqp", logId, methodNm, "설비상태조회");
			

			if (jsChk == null || jsChk.size() == 0) {
				// 설비 Table 존재유무 Check
				ydL3HdRsCd = "BR11";
				ydL3Msg = "오류:설비ID[" + ydEqpId + "] 정보 없음";
			} else if (ydEqpStat.equals(commUtils.trim(jsChk.getRecord(0).getFieldString("YS_EQP_STAT")))) {
				// 설비 Table 설비상태 Check
				ydL3HdRsCd = "BR12";
				ydL3Msg = "오류:현재 설비상태[" + ydEqpStat + "]와 동일";
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
			if (!"".equals(brGp)) {
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updEqpPause", logId, methodNm, "설비휴지 등록");
			}
			
			/**********************************************************
			* 5. 크레인 리스케줄
			*  - 고장복구구분 [R:복구 리스케줄, B:고장 리스케줄]
			*  - 작업예약 야드스케쥴우선순위 수정
			*  - 크레인스케줄 야드스케쥴우선순위, 야드설비ID 수정
			*  - 대기상태인 야드설비ID에 해당하는 크레인작업지시 전문 추가
			**********************************************************/
			if ("CR".equals(ydEqpId.substring(2, 4))) {
				// 해당 크레인 스케줄 상태가 권상지시(1)일 경우 명령선택대기(W)로 변경
				if ("B".equals(ydEqpStat)) {
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnSchW", logId, methodNm, "크래인스케줄명령선택대기설정");
				}
				// 크레인 리스케줄
				jrParam.setField("MSG_ID", msgId); //수신 전문 I/F ID
				jrRtn = this.trtCrnResch(jrParam);
			}
			
			/**********************************************************
			* 6. 크레인작업실적응답 전문 전송(YSN7L204)
			**********************************************************/
			if (resYn) {
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); // 야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ""    ); // 야드L3MESSAGE
				jrRtn = commUtils.addSndData(jrRtn, cbtYsComm.getYSN7L204(resMsg));
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			if (resYn) {
				try {
					
					// PIDEV_F : 정상SET후  ERROR 발생한 경우								
					if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
						resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
						resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
					}
					
					// 크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "YsCommEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { cbtYsComm.getYSN7L204(resMsg) });
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
        String szMsg	= "";
		String methodNm = "크레인리스케줄[CbtYsL2RcvSeEJB.trtCrnResch] < " + jrParam.getResultMsg();
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);	

		try {

			commUtils.printLog(logId, methodNm, "S+");
			
			jrParam.setResultMsg(methodNm);	//Log Method Name

			JDTORecord jrRtn = null;	//크레인작업지시 전문 Return

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

			jrYdMsg.setField("JMS_TC_CD"         ,	 msgId + "YSL204"         ); 			// JMS TC 코드
			jrYdMsg.setField("JMS_TC_CREATE_DDTT", 	commUtils.getDateTime14()); 			// JMS TC 생성일시
			jrYdMsg.setField("MODIFIER"        , 	jrParam.getFieldString("MODIFIER")); 	// 수정자

			for (int ii = 0; ii < schCnt; ii++) {
				jrYdMsg.setField("YD_EQP_ID"       , jsWoEqp.getRecord(ii).getFieldString("YD_EQP_ID")); // 야드설비ID
				jrYdMsg.setField("YD_WRK_PROG_STAT", "W"                                              ); // 야드작업진행상태
				
				//크레인작업지시 전문을 추가
				jrRtn = commUtils.addSndData(jrRtn, this.rcvN7YSL204(jrYdMsg));
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
	 *      [A] 오퍼레이션명 : 크레인작업지시요구(N7YSL204)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvN7YSL204(JDTORecord rcvMsg) throws DAOException {
        String szMsg	= "";
		String methodNm = "크레인작업지시요구[CbtYsL2RcvSeEJB.rcvN7YSL204] < " + rcvMsg.getResultMsg();
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);
		
		JDTORecord resMsg = JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용

		try {

			commUtils.printLog(logId, methodNm, "S+");
	        
	        szMsg = rcvMsg.toString();
			commUtils.printLog(logId, szMsg, "");
			

			//수신 항목 값
			String msgId         = commUtils.getMsgId(rcvMsg); 									// EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId       = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       	)); // 야드설비ID
			String ydWrkProgStat = commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT"	)); // 야드작업진행상태
			String ydSchCd       = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       	)); // 야드스케쥴코드
			String ydCrnSchId    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   	)); // 야드크레인스케쥴ID
			String modifier      = commUtils.trim(rcvMsg.getFieldString("MODIFIER"      	)); // 수정자(Backup Only)

            szMsg = "\n\t YD_EQP_ID   			: " 	+ ydEqpId 
               	  + "\n\t YD_WRK_PROG_STAT      : " 	+ ydWrkProgStat 
               	  + "\n\t YD_SCH_CD     		: " 	+ ydSchCd 
               	  + "\n\t YD_CRN_SCH_ID   		: " 	+ ydCrnSchId 
               	  + "\n\t MODIFIER     			: " 	+ modifier 
               	  ;

    		commUtils.printLog(logId, szMsg, "");
			
			if ("".equals(modifier)) { modifier = msgId; }
			
			methodNm = msgId.substring(0, 2) + methodNm;

//			commUtils.printLog(logId, "크레인작업지시요구 [ " + ysEqpId + " : " + ydWrkProgStat +  " - " + ydCrnSchId + " ]", "SL");
	        szMsg = "크레인작업지시요구 [ " + ydEqpId + " : " + ydWrkProgStat +  " - " + ydCrnSchId + " ]";
	        
    		commUtils.printLog(logId, szMsg, "");

			JDTORecord jrRtn  = null;	// 전문 Return
			String ydL3HdRsCd = "";		// 야드L3처리결과코드
			String ydL3Msg    = ""; 	// 야드L3MESSAGE

			//크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);									// Log ID
			resMsg.setResultMsg(methodNm);									// Log Method Name
			resMsg.setField("YD_EQP_ID"       , ydEqpId      ); 			// 야드설비ID
			resMsg.setField("YD_WRK_PROG_STAT", ydWrkProgStat); 			// 야드작업진행상태
			resMsg.setField("YD_SCH_CD"       , ydSchCd      ); 			// 야드스케쥴코드
			resMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId   ); 			// 야드크레인스케쥴ID
			resMsg.setField("YD_L2_WR_GP"     , "J"          ); 			// 야드L2실적구분(지시요구)
			resMsg.setField("YD_L3_HD_RS_CD"  , "JR99"       ); 			// 야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:크레인작업지시요구 수신처리"); 	// 야드L3MESSAGE(Error)

			//조회 및 등록용
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);						// Log ID
			jrParam.setResultMsg(methodNm);						// Log Method Name
			jrParam.setField("YD_EQP_ID"    , ydEqpId   ); 		// 야드설비ID
			jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); 		// 야드크레인스케쥴ID
			jrParam.setField("MODIFIER"     , modifier  ); 		// 수정자

			/**********************************************************
			* 1. 설비상태 Check
			**********************************************************/
			JDTORecord jrChk = cbtYsComm.chkEqpStat(jrParam);

			ydL3HdRsCd = commUtils.trim(jrChk.getFieldString("YD_L3_HD_RS_CD"));
			ydL3Msg    = commUtils.trim(jrChk.getFieldString("YD_L3_MSG"     ));

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			/**********************************************************
			* 2. 크레인스케줄 조회
			*    2.1 크레인스케줄이 존재하면 전송
			*    2.2 크레인스케줄이 존재하지 않으면 수신된 야드작업진행상태에 따라 처리
			**********************************************************/
			JDTORecordSet jsSch = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchN7YSL204", logId, methodNm, "크레인스케줄 조회");

			if (jsSch.size() > 0) {
				/**********************************************************
				* 2.1 크레인스케줄이 존재하면 수신된 야드작업진행상태에 상관없이 작업지시 전송
				**********************************************************/
				ydCrnSchId    = commUtils.trim(jsSch.getRecord(0).getFieldString("YD_CRN_SCH_ID"   ));
				ydWrkProgStat = commUtils.trim(jsSch.getRecord(0).getFieldString("YD_WRK_PROG_STAT"));

				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); // 야드크레인스케쥴ID

				if ("1".equals(ydWrkProgStat) || "2".equals(ydWrkProgStat) || "3".equals(ydWrkProgStat)) {
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
				
				// 크레인작업지시(YSN7L203) 전문 생성
				jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN7L203", jrParam));

//				commUtils.printLog(logId, "크레인작업지시요구 작업지시 전송 [ " + ysEqpId + " : " + ydWrkProgStat +  " - " + ydCrnSchId + " ]", "SL");
		        szMsg = "크레인작업지시요구 작업지시 전송 [ " + ydEqpId + " : " + ydWrkProgStat +  " - " + ydCrnSchId + " ]";

		        commUtils.printLog(logId, szMsg, "");
				
				
			} else {
				/**********************************************************
				* 2.2 크레인스케줄이 존재하지 않으면 수신된 야드작업진행상태에 따라 처리
				*    2.1 권상지시[1], 권상완료[2], 권하지시[3] 이면 Error 처리
				*    2.2 권하완료[4] 이면 스케줄을 생성
				*    2.3 명령선택대기[W] 이면 응답 전문을 전송 -> 2.2로 통합
				**********************************************************/
				if ("1".equals(ydWrkProgStat) || "2".equals(ydWrkProgStat) || "3".equals(ydWrkProgStat)) {
					/**********************************************************
					* 2.2.1 재지시요구 시
					**********************************************************/
					resMsg.setField("YD_L3_HD_RS_CD", "9999" ); // 야드L3처리결과코드
					resMsg.setField("YD_L3_MSG"     , "크레인[" + ydEqpId + "-" + ydWrkProgStat + "] 작업지시 없음"); //야드L3MESSAGE
					jrRtn = commUtils.addSndData(jrRtn, cbtYsComm.getYSN7L204(resMsg));

//					commUtils.printLog(logId, "크레인작업지시요구(재지시요구) 작업지시 없음 [ " + ysEqpId + " : " + ydWrkProgStat + " - " + ydCrnSchId + " ]", "SL");
			        szMsg = "크레인작업지시요구(재지시요구) 작업지시 없음 [ " + ydEqpId + " : " + ydWrkProgStat + " - " + ydCrnSchId + " ]";

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

	    			// 작업예약 조회
	        		JDTORecordSet jsWrkBook = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getWbIdN7YSL204", logId, methodNm, "작업예약 조회");

					// 작업예약이 있으면 크레인스케줄호출
					if (jsWrkBook.size() > 0) {
						ydL3Msg = "크레인스케줄 호출";

						jrYdMsg.setField("YD_WBOOK_ID"  , 	jsWrkBook.getRecord(0).getFieldString("YD_WBOOK_ID")); 	//야드작업예약ID
						jrYdMsg.setField("YD_SCH_CD"    , 	jsWrkBook.getRecord(0).getFieldString("YD_SCH_CD"  )); 	//야드스케쥴코드
						jrYdMsg.setField("YD_EQP_ID"    , 	ydEqpId ); 												//야드설비ID
						jrYdMsg.setField("YD_SCH_ST_GP" , 	"A"     ); 												//야드스케쥴기동구분(Auto)
						jrYdMsg.setField("YD_SCH_REQ_GP", 	"N"     ); 												//야드스케쥴요청구분(권하완료후 다음)
						jrYdMsg.setField("MODIFIER"   ,		 modifier); 											//수정자
						
						//26.03.05 이송운전실 신형선 사원 요청. 크레인작업지시 간헐적으로 차상국 미표출 현상
						//N7YSL204 이후 getCrnSchMsg 에 의해 전문전송되는 YSYSJ502 및 YSN7L204 전송
						// YSYSJ502 전송 후 YSN7L204 전송처리 중 YSYSJ502 전문이 모두 처리되면, 502 전문 안에서 크레인 상태를 'W'가 아닌 '4' 로 인식하여
						//플래그를 따로 넘겨 상태를 'W' 로 인식해 처리되게끔. (즉, 크레인상태 4 (권하완료) 및 플래그'Y' 일시 처리 - 허정욱 책임
						
						String sApplyYnPI = commDao.ApplyYnPI(logId, methodNm, "APPNEW", "027", "*");
						if("Y".equals(sApplyYnPI)){
							szMsg = "작업지시요구에서 YSYSJ502 호출 시 플래그 전달";
				      		commUtils.printLog(logId, szMsg, "");
				      		jrYdMsg.setField("IS_CALL_BY_204"   ,		 "Y"); 		
						}
															//

						jrRtn = cbtYsComm.getCrnSchMsg(jrYdMsg);
					} else {
						ydL3Msg = "다음 크레인작업지시 없음";
						
					}

					resMsg.setField("YD_L3_HD_RS_CD", "9999" ); // 야드L3처리결과코드
					resMsg.setField("YD_L3_MSG"     , ydL3Msg); // 야드L3MESSAGE
					jrRtn = commUtils.addSndData(jrRtn, cbtYsComm.getYSN7L204(resMsg));

//					commUtils.printLog(logId, "크레인작업지시요구(다음지시) " + ydL3Msg + " [ " + ysEqpId + " : " + ydWrkProgStat + " ]", "SL");
			        szMsg = "크레인작업지시요구(다음지시) " + ydL3Msg + " [ " + ydEqpId + " : " + ydWrkProgStat + " ]";

			        commUtils.printLog(logId, szMsg, "");

					
				}
			}
			
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			try {
				
				// PIDEV_F : 정상SET후  ERROR 발생한 경우								
				if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
					resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
					resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
				}
				
				// 크레인작업실적응답 전문 전송
				EJBConnector resConn = new EJBConnector("default", "YsCommEJB", this);
				resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { cbtYsComm.getYSN7L204(resMsg) });
			} catch (Exception se) {}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	
	
	
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인권상실적(N7YSL205)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN7YSL205(JDTORecord rcvMsg) throws DAOException {
        String szMsg	= "";
		String methodNm = "크레인권상실적[CbtYsL2RcvSeEJB.rcvN7YSL205] < " + rcvMsg.getResultMsg();
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);
		
		JDTORecord resMsg = JDTORecordFactory.getInstance().create(); // 크레인작업실적응답 전문 생성용
		boolean resYn = true;	//크레인작업실적응답 전문 전송여부

		try {

			commUtils.printLog(logId, methodNm, "S+");
	        
	        szMsg = rcvMsg.toString();
			commUtils.printLog(logId, szMsg, "");
			

			//수신 항목 값
			String msgId         = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId       = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); 	// 야드설비ID
			String ydEqpWrkMode  = commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE" )); 	// 야드설비작업Mode("1": On-Line, "2": Off-Line )
			String ydWrkProgStat = commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")); 	// 야드작업진행상태
			String ydSchCd       = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )); 	// 야드스케쥴코드
			String ydCrnSchId    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )); 	// 야드크레인스케쥴ID
			String ydUpWrLoc     = commUtils.trim(rcvMsg.getFieldString("YS_UP_WR_LOC"    )); 	// 야드권상실적위치
			String ydUpWrLayer   = commUtils.trim(rcvMsg.getFieldString("YS_UP_WR_LAYER"  )); 	// 야드권상실적단
			String ydCrnXaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_XAXIS"    )); 	// 야드크레인X축
			String ydCrnYaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_YAXIS"    )); 	// 야드크레인Y축
			String ydCrnZaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_ZAXIS"    )); 	// 야드크레인Z축			
			String modifier      = commUtils.trim(rcvMsg.getFieldString("MODIFIER"        )); 	// 수정자(Backup Only)
			String ydWbookId     = ""; 															// 야드작업예약ID
			String ydDnWoLoc     = ""; 															// 야드권하지시위치
			
            szMsg = "\n\t YD_EQP_ID   			: " 	+ ydEqpId 
                  + "\n\t YD_EQP_WRK_MODE      	: " 	+ ydEqpWrkMode 
                  + "\n\t YD_WRK_PROG_STAT     	: " 	+ ydWrkProgStat 
                  + "\n\t YD_SCH_CD   			: " 	+ ydSchCd 
                  + "\n\t YD_CRN_SCH_ID   		: " 	+ ydCrnSchId 
                  + "\n\t YS_UP_WR_LOC   		: " 	+ ydUpWrLoc 
                  + "\n\t YS_UP_WR_LAYER   		: " 	+ ydUpWrLayer 
                  + "\n\t YD_CRN_XAXIS   		: " 	+ ydCrnXaxis 
                  + "\n\t YD_CRN_YAXIS   		: " 	+ ydCrnYaxis 
                  + "\n\t YD_CRN_ZAXIS   		: " 	+ ydCrnZaxis 
                  + "\n\t MODIFIER     			: " 	+ modifier 
                  ;

    		commUtils.printLog(logId, szMsg, "");
			
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;
			

			JDTORecord jrRtn = null;	// 전문 Return
			String ydL3HdRsCd = "";		// 야드L3처리결과코드
			String ydL3Msg    = ""; 	// 야드L3MESSAGE

			// 크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);	// Log ID
			resMsg.setResultMsg(methodNm);	// Log Method Name
			resMsg.setField("YD_EQP_ID"       , ydEqpId      ); 	// 야드설비ID
			resMsg.setField("YD_WRK_PROG_STAT", ydWrkProgStat); 	// 야드작업진행상태
			resMsg.setField("YD_SCH_CD"       , ydSchCd      ); 	// 야드스케쥴코드
			resMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId   ); 	// 야드크레인스케쥴ID
			resMsg.setField("YD_L2_WR_GP"     , "U"          ); 	// 야드L2실적구분(권상실적)
			resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       ); 	// 야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:권상실적 수신처리"); 	// 야드L3MESSAGE(Error)

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				ydL3HdRsCd = "UP01";
				ydL3Msg    = "오류:설비ID 없음";
			} else if (ydEqpId.length() < 6 || !ydEqpId.startsWith("GE")) {
				ydL3HdRsCd = "UP02";
				ydL3Msg    = "오류:설비ID[" + ydEqpId + "] 이상";
			} else if ("".equals(ydCrnSchId)) {
				ydL3HdRsCd = "UP03";
				ydL3Msg    = "오류:크레인스케쥴ID 없음";
			} else if ("".equals(ydUpWrLoc)) {
				ydL3HdRsCd = "UP04";
				ydL3Msg    = "오류:권상실적위치 없음";
			} else if ("".equals(ydUpWrLayer)) {
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
			jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); 					// 야드크레인스케쥴ID
			jrParam.setField("MODIFIER"     , modifier  ); 					// 수정자
			
			jrParam.setField("YD_STK_COL_GP", ydUpWrLoc.substring(0, 6)); 	// 야드적치열구분
			jrParam.setField("YD_STK_BED_NO", ydUpWrLoc.substring(6, 8)); 	// 야드적치Bed번호

			JDTORecord jrChk = null;
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStatCrnSch2", logId, methodNm, "크레인스케줄상태 조회");

			if (jsChk.size() == 0) {
				// 크레인스케쥴 Table 존재유무 Check
				ydL3HdRsCd = "UP11";
				ydL3Msg = "오류:크레인스케쥴 DB정보 없음";
			} else {
				// 크레인스케쥴 Table 야드작업진행상태 Check
				jrChk = jsChk.getRecord(0);
				ydWbookId       = commUtils.trim(jrChk.getFieldString("YD_WBOOK_ID"     )); // 야드작업예약ID
				ydDnWoLoc       = commUtils.trim(jrChk.getFieldString("YS_DN_WO_LOC"    )); // 야드권하지시위치
				String tmpStat  = commUtils.trim(jrChk.getFieldString("YD_WRK_PROG_STAT")); // 야드작업진행상태
				String tmpEqpId = commUtils.trim(jrChk.getFieldString("YD_EQP_ID"       )); // 야드설비ID
				if (!"1".equals(tmpStat) && !"W".equals(tmpStat)) {
					ydL3HdRsCd = "UP12";
					ydL3Msg = "오류:현재 작업진행상태[" + tmpStat + "] 이상";
				} else if (!ydEqpId.equals(tmpEqpId)) {
					ydL3HdRsCd = "UP13";
					ydL3Msg = "오류:현재 설비ID와[" + tmpEqpId + "] 다름";
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
			* 3.1  스케쥴 코드에 따른 추출완료 전문 전송
			*     대형압연 빌렛/봉강 출하상 추출완료
			*     PRESS교정 추출완료
			*     PRESS교정 Reject완료
			*     빌렛정정출하상 추출완료
			**********************************************************/
			if(   "GEPC3".equals(ydUpWrLoc.substring(0, 5))
			   || "GEPC4".equals(ydUpWrLoc.substring(0, 5))
			   || "GEPC5".equals(ydUpWrLoc.substring(0, 5))) {
				
				// 대형압연 빌렛/봉강 출하상 저장위치 권상 완료시(GEPC31, GEPC32, GEPC41, GEPC42, GEPC51)
				// 대형압연 빌렛/봉강 출하상 추출완료(YSM3L201) 송신
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSM3L201", jrParam));
		    } else if("GEPC03".equals(ydUpWrLoc.substring(0, 6))) { 
				
				// PRESS교정기 추출 저장위치 권상  완료시(GEPC03)
				// PRESS교정 추출완료(YSM4L211) 송신
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSM4L211", jrParam));
		    	
		    } else if("GETF02".equals(ydUpWrLoc.substring(0, 6))) { 
				// PRESS교정 Reject 저장위치 권상  완료시(GETF02)
				// PRESS교정 Reject완료(YSM4L212) 송신
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSM4L212", jrParam));
		    	
		    } else if("GETF01".equals(ydUpWrLoc.substring(0, 6))) { 
// 2025.10.22 ShotBlast 장입대(GETF01) Reject 완료시 포함 요구(양준석 부장)
				// PShotBlast 장입대(GETF01) Reject 완료(YSM4L214) 송신
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSM4L214", jrParam));
		    	
		    } else if("GEPC01".equals(ydUpWrLoc.substring(0, 6))  
				   || "GEPC02".equals(ydUpWrLoc.substring(0, 6))
				   || "GEPC04".equals(ydUpWrLoc.substring(0, 6))) {
				
				// 빌렛정정출하상 저장위치 권상  완료시(GEPC01, GEPC02)
				// 빌렛정정출하상 추출완료(YSM4L215) 송신
// 2025.11.13 빌렛정정 S/B 크레들(GEPC05) 추출완료 는 남42문 차량 도착시 전송
//            기존 빌렛정정출하상 (GEPC01, GEPC02) 과  빌렛정정 S/B 크레들(GEPC05) Query 가 달라서 구분 하기 위해 추가
// 2025.11.14 수동그란인더 크레들(GEPC04) 추출완료 추가
		    	
		    	jrParam.setField("YD_EQP_ID"     , "GEPC01");	// 설비코드
		    	
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSM4L215", jrParam));
		    	
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
			if("TR".equals(ydUpWrLoc.substring(2, 4))) {
				// 차량스케줄  야드하차작업예약ID 등록 (차량스케줄에 야드하차작업예약ID 없을 경우)
				recPara.setField("YD_WBOOK_ID" , ydWbookId ); 
				recPara.setField("MODIFIER" , modifier     ); 

				commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CarSchUdWbId", logId, methodNm, "차량스케줄(하차) 수정");
				
				
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setResultCode(logId);	// Log ID
				recPara.setResultMsg(methodNm);	// Log Method Name
				recPara.setField("WR_DT" 		 , commUtils.getDateTime14()  ); 
				recPara.setField("YS_STK_COL_GP" , ydUpWrLoc.substring(0, 6)     ); 
				// 구내운송 소재차량하차개시
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSTSJ009", recPara));
				
				
				// 하차 차량스케줄 야드설비작업매수, 중량, 야드차량진행상태, 야드하차개시일시 수정
				
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID", ydCrnSchId ); 
				recPara.setField("YD_WBOOK_ID" 	, ydWbookId); 
				recPara.setField("WR_DT" 		, commUtils.getDateTime14()     ); 
				recPara.setField("MODIFIER" 	, modifier     ); 
    	
				commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updN7YSL205CarSchUd", logId, methodNm, "하차 차량스케줄 수정 ");
				

// 2025.09.16 위 하차 차량스케줄 수정 이 원래 아래 있었는데 자량이송재료삭제 하면서 DEL_YN = 'Y' 하면서 
//            YD_EQP_WRK_SH(야드설비작업매수), YD_EQP_WRK_WT(야드설비작업중량) 값이 0 로 UPDATE 되어 순서 변경
				
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID" , ydCrnSchId ); 
				recPara.setField("MODIFIER" , modifier     ); 
    	
				commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CarMtlDel", logId, methodNm, "차량이송재료 삭제");
				
				
			}
			
			/**********************************************************
			* 5. 권하지시위치가 차량(상차)
			*    차량스케줄 야드차량진행상태가 상차도착(2) 또는 상차검수(3) 이면
			* 5.1 구내운송 소재차량상차개시(YSTSJ007) 전송
			*   - 야드차량사용구분이 구내운송(L)
			* 5.2 출하관리출하상차개시(YSDSJ006) 전송
			*   - 야드차량사용구분이 출하차량(G)
			* 5.3 차량스케줄 수정
			*   - 야드설비작업상태(U:공차), 야드차량진행상태(4:상차개시) 등 수정
			**********************************************************/
			if ("TR".equals(ydDnWoLoc.substring(2, 4))) {
				// 차량하차스케줄 정보 조회
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID" , 	ydWbookId     ); 
				recPara.setField("YD_CRN_SCH_ID" , 	ydCrnSchId ); 

// 2025.09.08 TB_PB_STLFRTOMOVE.ARR_WLOC_CD 에서 ARR_WLOC_CD 가져와 TB_YS_CARSCH.ARR_WLOC_CD UPDATE 없는경우 기존값이 없어짐 jSpeed Query 변경
//				jsChk = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL005CarSchLd", logId, methodNm, "차량하차스케줄 정보 "); 
				jsChk = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getN7YSL005CarSchLd", logId, methodNm, "크레인권상실적 상차 차량스케줄 조회 "); 
		    	
				if (jsChk.size() > 0) {
					jrChk = jsChk.getRecord(0);
				
					//상차개시 전문
					JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name
					
					if("L".equals(jrChk.getFieldString("YD_CAR_USE_GP"))) {
						//구내운송 소재차량상차개시
						jrYdMsg.setField("JMS_TC_CD"         , "YSTSJ007"); 											// JMSTC코드
						jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDt    ); 											// JMSTC생성일시
						jrYdMsg.setField("TRN_EQP_CD"        , commUtils.trim(jrChk.getFieldString("TRN_EQP_CD"    ))); // 운송장비코드
						jrYdMsg.setField("SPOS_WLOC_CD"      , commUtils.trim(jrChk.getFieldString("SPOS_WLOC_CD"  ))); // 발지개소코드
						jrYdMsg.setField("SPOS_YD_PNT_CD"    , commUtils.trim(jrChk.getFieldString("SPOS_YD_PNT_CD"))); // 발지야드포인트코드
						jrYdMsg.setField("ARR_WLOC_CD"       , commUtils.trim(jrChk.getFieldString("ARR_WLOC_CD"   ))); // 착지개소코드
						jrYdMsg.setField("TRN_WRK_ST_DT"     , currDt    ); 											// 운송작업시작일시

						//전송할 전문에 추가
						jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
					}
					

					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("MODIFIER" 	, modifier     ); 
					recPara.setField("ARR_WLOC_CD"  , commUtils.trim(jrChk.getFieldString("ARR_WLOC_CD"  ))); // 착지개소코드
					recPara.setField("YD_WBOOK_ID" 	, ydWbookId     ); 
					recPara.setField("WR_DT" 		, commUtils.getDateTime14()     ); 
					recPara.setField("YD_CAR_SCH_ID", commUtils.trim(jrChk.getFieldString("YD_CAR_SCH_ID"))); // 야드차량스케쥴ID
					// 상차 차량스케줄 야드설비작업상태, 야드차량진행상태, 야드상차작업예약ID, 착지개소코드 등 수정
					

					commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CarSchLd", logId, methodNm, " 상차 차량스케줄 수정 ");
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
			String ydUpWrkActGp = ydEqpWrkMode;

// 2025.09.05 야드설비작업Mode(0:Manual, 1:Auto, 9:Backup) 막음
//			if ("0".equals(ydEqpWrkMode)) {
//				ydUpWrkActGp = "M"; //Manual
//			} else if("1".equals(ydEqpWrkMode)) {
//				ydUpWrkActGp = "A"; //Auto
//			} else if("9".equals(ydEqpWrkMode)) {
//				ydUpWrkActGp = "B"; //Backup
//			}

			//설비
			jrParam.setField("YD_EQP_ID"       , ydEqpId     ); //야드설비ID
			jrParam.setField("YD_EQP_STAT"     , "2"         ); //야드설비상태(권상중)
			//크레인스케쥴
			jrParam.setField("YD_UP_CMPL_DT"   , currDt      ); //야드권상완료일시
			jrParam.setField("YD_UP_WR_LOC"    , ydUpWrLoc   ); //야드권상실적위치
			jrParam.setField("YD_UP_WR_LAYER"  , ydUpWrLayer ); //야드권상실적단
			jrParam.setField("YD_UP_WRK_ACT_GP", ydUpWrkActGp); //야드권상작업수행구분
			jrParam.setField("YD_UP_WR_XAXIS"  , ydCrnXaxis  ); //야드권상실적X축
			jrParam.setField("YD_UP_WR_YAXIS"  , ydCrnYaxis  ); //야드권상실적Y축
			jrParam.setField("YD_UP_WR_ZAXIS"  , ydCrnZaxis  ); //야드권상실적Z축

			//설비(야드설비상태) 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatEqp", logId, methodNm, "설비상태 수정");
			
			//적치단(크레인 및 권상위치) 수정
//			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005StkLyr", logId, methodNm, "적치단(크레인 및 권상위치) 수정");
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005StkLyrblbt", logId, methodNm, "적치단(크레인 및 권상위치) 수정");
			//적치Bed(완산Bed->입출고가능) 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005StkBedF", logId, methodNm, "적치Bed(완산Bed->입출고가능) 수정");
			//크레인스케쥴 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CrnSch", logId, methodNm, "크레인스케쥴 수정");

			
			resMsg.setField("YD_UP_WR_LOC", ydUpWrLoc); //야드권상실적위치


			//크레인작업실적응답 전송
			if (resYn) {
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
				
		        szMsg = "크레인작업실적응답 >>>>>>> " + resMsg.toString();
				commUtils.printLog(logId, szMsg, "");
				
				jrRtn = commUtils.addSndData(jrRtn, cbtYsComm.getYSN7L204(resMsg));
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			if (resYn) {
				try {
					
					//PIDEV_F : 정상SET후  ERROR 발생한 경우								
					if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
						resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
						resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
					}
					
					// 크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "YsCommEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { cbtYsComm.getYSN7L204(resMsg) });
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 크레인권하실적(N7YSL206)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN7YSL206(JDTORecord rcvMsg) throws DAOException {
        String szMsg	= "";
		String methodNm = "크레인권하실적[CbtYsL2RcvSeEJB.rcvN7YSL206] < " + rcvMsg.getResultMsg();
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);
		
		JDTORecord resMsg = JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용
		boolean resYn = true;	//크레인작업실적응답 전문 전송여부

		try {

			commUtils.printLog(logId, methodNm, "S+");
	        
	        szMsg = rcvMsg.toString();
			commUtils.printLog(logId, szMsg, "");


			// 수신 항목 값
			String msgId         = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId       = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); // 야드설비ID
			String ydEqpWrkMode  = commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE" )); // 야드설비작업Mode("1": On-Line, "2": Off-Line )
			String ydWrkProgStat = commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")); // 야드작업진행상태(4:권하완료, 5:강제권하)
			String ydSchCd       = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )); // 야드스케쥴코드
			String ydCrnSchId    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )); // 야드크레인스케쥴ID
			String ydDnWrLoc     = commUtils.trim(rcvMsg.getFieldString("YS_DN_WR_LOC"    )); // 야드권하실적위치
			String ydDnWrLayer   = commUtils.trim(rcvMsg.getFieldString("YS_DN_WR_LAYER"  )); // 야드권하실적단
			String ydCrnXaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_XAXIS"    )); // 야드크레인X축
			String ydCrnYaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_YAXIS"    )); // 야드크레인Y축
			String ydCrnZaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_ZAXIS"    )); // 야드크레인Z축			
			String modifier      = commUtils.trim(rcvMsg.getFieldString("MODIFIER"        )); // 수정자(Backup Only)
			String ydWbookId     = ""; // 야드작업예약ID
			String ydUpWrLoc     = ""; // 야드권상실적위치
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

// 2025.12.04 설비 보급 이나 추출시 Seq 역순 등록
			String szREVERSE_YN	= "N";
			
            szMsg = "\n\t YD_EQP_ID   			: " 	+ ydEqpId 
                  + "\n\t YD_EQP_WRK_MODE      	: " 	+ ydEqpWrkMode 
                  + "\n\t YD_WRK_PROG_STAT     	: " 	+ ydWrkProgStat 
                  + "\n\t YD_SCH_CD   			: " 	+ ydSchCd 
                  + "\n\t YD_CRN_SCH_ID   		: " 	+ ydCrnSchId 
                  + "\n\t YS_DN_WR_LOC   		: " 	+ ydDnWrLoc 
                  + "\n\t YS_DN_WR_LAYER   		: " 	+ ydDnWrLayer 
                  + "\n\t YD_CRN_XAXIS   		: " 	+ ydCrnXaxis 
                  + "\n\t YD_CRN_YAXIS   		: " 	+ ydCrnYaxis 
                  + "\n\t YD_CRN_ZAXIS   		: " 	+ ydCrnZaxis 
                  + "\n\t MODIFIER     			: " 	+ modifier 
                  ;

    		commUtils.printLog(logId, szMsg, "");

			String szYD_TO_LOC_DCSN_MTD = ""; 		// 분리 작업용
			String szNEXT_YD_CRN_SCH_ID = ""; 		// 분리 작업용		
			String szNEXT_NEXT_YD_CRN_SCH_ID = ""; 	// 분리 작업용		
			
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			JDTORecord jrRtn = null;	// 전문 Return
			String ydL3HdRsCd = "";		// 야드L3처리결과코드
			String ydL3Msg    = ""; 	// 야드L3MESSAGE

			// 크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);	// Log ID
			resMsg.setResultMsg(methodNm);	// Log Method Name
			resMsg.setField("YD_EQP_ID"       , ydEqpId      ); 	// 야드설비ID
			resMsg.setField("YD_WRK_PROG_STAT", ydWrkProgStat); 	// 야드작업진행상태
			resMsg.setField("YD_SCH_CD"       , ydSchCd      ); 	// 야드스케쥴코드
			resMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId   ); 	// 야드크레인스케쥴ID
			resMsg.setField("YD_L3_HD_RS_CD"  , "DN99"       ); 	// 야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:권하실적 수신처리"); 	// 야드L3MESSAGE(Error)
			if ("4".equals(ydWrkProgStat)) {
				resMsg.setField("YD_L2_WR_GP", "D"); // 야드L2실적구분(권하실적)
			} else {
				resMsg.setField("YD_L2_WR_GP", "F"); // 야드L2실적구분(강제권하)
			}
			
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				ydL3HdRsCd = "DN01";
				ydL3Msg    = "오류:설비ID 없음";
			} else if (ydEqpId.length() < 6 || !ydEqpId.startsWith("GE")) {
				ydL3HdRsCd = "DN02";
				ydL3Msg    = "오류:설비ID[" + ydEqpId + "] 이상";
			} else if ("".equals(ydCrnSchId)) {
				ydL3HdRsCd = "DN03";
				ydL3Msg    = "오류:크레인스케쥴ID 없음";
			} else if ("".equals(ydDnWrLoc)) {
				ydL3HdRsCd = "DN04";
				ydL3Msg    = "오류:권하실적위치 없음";
			} else if ("XX".equals(ydDnWrLoc.substring(2, 4))) {
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
			
			jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); // 야드크레인스케쥴ID
			jrParam.setField("MODIFIER"     , modifier  ); // 수정자
			
			JDTORecord jrParam2 = JDTORecordFactory.getInstance().create();
			jrParam2.setResultCode(logId);		// Log ID
			jrParam2.setResultMsg(methodNm);	// Log Method Name
			
			jrParam2.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
			jrParam2.setField("MODIFIER"     , modifier  ); //수정자
			
			JDTORecord jrChk = null;
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStatCrnSch2", logId, methodNm, "크레인스케줄상태 조회");
			
			if (jsChk.size() == 0) {
				// 크레인스케쥴 Table 존재유무 Check
				ydL3HdRsCd = "DN11";
				ydL3Msg = "오류:크레인스케쥴ID DB정보 없음";
			} else {
				
				// 크레인스케쥴 Table 야드작업진행상태 Check
				jrChk = jsChk.getRecord(0);
				ydWbookId       = commUtils.trim(jrChk.getFieldString("YD_WBOOK_ID"     )); // 야드작업예약ID
				ydSchCd         = commUtils.trim(jrChk.getFieldString("YD_SCH_CD"       )); // 야드스케쥴코드
				ydUpWrLoc       = commUtils.trim(jrChk.getFieldString("YS_UP_WR_LOC"    )); // 야드권상실적위치
				String tmpStat  = commUtils.trim(jrChk.getFieldString("YD_WRK_PROG_STAT")); // 야드작업진행상태
				String tmpEqpId = commUtils.trim(jrChk.getFieldString("YD_EQP_ID"       )); // 야드설비ID
				//분리
				szYD_TO_LOC_DCSN_MTD 		= commUtils.trim(jrChk.getFieldString("YD_TO_LOC_DCSN_MTD"       )); 
				szNEXT_YD_CRN_SCH_ID 		= commUtils.trim(jrChk.getFieldString("NEXT_YD_CRN_SCH_ID"       )); 
				szNEXT_NEXT_YD_CRN_SCH_ID 	= commUtils.trim(jrChk.getFieldString("NEXT_NEXT_YD_CRN_SCH_ID"  )); 

// 2025.12.05 설비 보급 이나 추출시 Seq 역순 등록
				String sApplyYnPI = commDao.ApplyYnPI(logId, methodNm, "APPNEW", "007", "*");
				if("Y".equals(sApplyYnPI)){
					szMsg = "TB_YS_RULE 007 발랫정정야드 Seq 역순 등록";
		      		commUtils.printLog(logId, szMsg, "");

		      		szREVERSE_YN 	= commUtils.trim(jrChk.getFieldString("REVERSE_YN"  ));
				}
				
				
// 2025.09.08 강제권하가 아니면 권하 실적 권하 위치 적치열, 단 체크
//				if ("4".equals(ydWrkProgStat)) {
					String tmpDnLoc  	= commUtils.trim(jrChk.getFieldString("YS_DN_WO_LOC"    )); 	// 특수강야드권하지시위치
					String tmpDnLayer  	= commUtils.trim(jrChk.getFieldString("YS_DN_WO_LAYER"  )); 	// 특수강야드권하지시단
					if (!ydDnWrLayer.equals(tmpDnLayer) || !ydDnWrLoc.equals(tmpDnLoc)) {
			            szMsg = "\n\t YS_DN_WR_LOC   			: " 	+ ydDnWrLoc 
			                  + "\n\t YS_DN_WR_LAYER      		: " 	+ ydDnWrLayer 
			                  + "\n\t getStatCrnSch SELECT RESULT"
			                  + "\n\t YS_DN_WO_LOC     			: " 	+ tmpDnLoc 
			                  + "\n\t YS_DN_WO_LAYER   			: " 	+ tmpDnLayer 
			                  ;

			      		commUtils.printLog(logId, szMsg, "");
						
						// 권하실적 권하위치, 단 체크
						ydL3HdRsCd = "DN11";
						ydL3Msg = "오류:지시 실적 권하위치,단 다름.";
					}
//				}
				
// 2025.11.12 권하 실적 주행, 횡행이 크레인 지시 허용오차 내인지 체크 
// 			     차량은 주행, 횡행 좌표 체크 하지 않음 
				if(!"TR".equals(ydDnWrLoc.substring(2, 4))) {
					
					String szYD_DN_X_MIN = commUtils.trim(jrChk.getFieldString("YD_DN_X_MIN")); // 권하 주행 MIN
					String szYD_DN_X_MAX = commUtils.trim(jrChk.getFieldString("YD_DN_X_MAX")); // 권하 주행 MAX
					String szYD_DN_Y_MIN = commUtils.trim(jrChk.getFieldString("YD_DN_Y_MIN")); // 권하 횡행 MIN
					String szYD_DN_Y_MAX = commUtils.trim(jrChk.getFieldString("YD_DN_Y_MAX")); // 권하 횡행 MAX

					szMsg = "****** 권하 실적 주행, 횡행 체크 "
						  + "\n\t 권하 실적 주행           : " + ydCrnXaxis 
						  + "\n\t 권하 실적 주행 MIN : " + szYD_DN_X_MIN 
						  + "\n\t 권하 실적 주행 MAx : " + szYD_DN_X_MAX 
						  + "\n\t 권하 실적 횡행           : " + ydCrnYaxis
						  + "\n\t 권하 실적 횡행 MIN : " + szYD_DN_Y_MIN 
						  + "\n\t 권하 실적 횡행 MAx : " + szYD_DN_Y_MAX 
						  ;
					
		      		commUtils.printLog(logId, szMsg, "");
	          		
		      		if (ydCrnXaxis == null || "".equals(ydCrnXaxis)) {
		      			ydCrnXaxis = "0"; 
		      		}
		      		
					if(Integer.parseInt(szYD_DN_X_MIN) > Integer.parseInt(ydCrnXaxis) || Integer.parseInt(ydCrnXaxis) > Integer.parseInt(szYD_DN_X_MAX)) {
						// 권하실적 권하위치 주행 체크
						ydL3HdRsCd = "DN11";
						ydL3Msg = "오류:주행 허용 오차를 벗어남.";
					}

					szMsg = "****** 권하 실적 횡행 체크";
		      		commUtils.printLog(logId, szMsg, "");

		      		if (ydCrnYaxis == null || "".equals(ydCrnYaxis)) {
		      			ydCrnYaxis = "0"; 
		      		}
		      		
					if(Integer.parseInt(szYD_DN_Y_MIN) > Integer.parseInt(ydCrnYaxis) || Integer.parseInt(ydCrnYaxis) > Integer.parseInt(szYD_DN_Y_MAX)) {
						// 권하실적 권하위치 횡행 체크
						ydL3HdRsCd = "DN11";
						ydL3Msg = "오류:횡행 허용 오차를 벗어남.";
					}
				}
					
				
				if (!"2".equals(tmpStat) && !"3".equals(tmpStat)) {
					ydL3HdRsCd = "DN12";
					ydL3Msg = "오류:현재 작업진행상태[" + tmpStat + "] 이상";
				} else if (!ydEqpId.equals(tmpEqpId)) {
					ydL3HdRsCd = "DN13";
					ydL3Msg = "오류:현재 설비ID와[" + tmpEqpId + "] 다름";
				}
			}
			
			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); // 야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); // 야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			// 조회 Parameter
			jrParam.setField("YD_WBOOK_ID"  , ydWbookId); // 야드작업예약ID
			jrParam.setField("YD_STK_COL_GP", ydDnWrLoc.substring(0, 6)); // 야드적치열구분
			jrParam.setField("YD_STK_BED_NO", ydDnWrLoc.substring(6, 8)); // 야드적치Bed번호
			// 설비
			jrParam.setField("YD_EQP_ID"  , ydEqpId); // 야드설비ID(크레인)
			jrParam.setField("YD_EQP_STAT", "4"    ); // 야드설비상태(권하완료)
			
			// 실제 야드권하실적단 및 기타 정보 조회
			String wbCmplYn = ""; 			// 작업예약완료여부
			String ydStkBedUseCd = ""; 		// 적치대(P),우물정자(V1) 구분
			boolean chgDnWrLayer = false; 	// 권하위치 적치단 변경여부
			
			jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getWbCmplYn", logId, methodNm, "작업예약 완료여부 조회");
			
			if (jsChk.size() > 0) {

				jrChk = jsChk.getRecord(0);
				wbCmplYn = commUtils.trim(jrChk.getFieldString("WB_CMPL_YN"));
				ydStkBedUseCd = commUtils.trim(jrChk.getFieldString("YD_STKBED_USG_CD"));
				commUtils.printLog(logId, "작업예약[" + ydWbookId + "] 완료여부 : " + wbCmplYn, "SL");
				
			} else {
				resMsg.setField("YD_L3_HD_RS_CD", "DN14"); 					// 야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , "오류:작업예약 완료여부 조회 오류"); 	// 야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			
// 2025.10.29 특수강 야드적치대용도코드(YD_STKBED_USG_CD) 항상 "V1"
			ydStkBedUseCd = "V1";
			
			// 권하시 단 체크 로직
			if("V1".equals(ydStkBedUseCd)) {
				
				// 물정자 적치대 단 체크
// 2025.09.05 기존 Query WHERE 조건에 적치열 만 있어서 AND 베드 추가 Query 로 변경				
// 2025.09.08 단 검색 베드 단위가 아니고 적치열 단위로 검색이 맞음 이전 Query 원복				
				jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL006CurrV1", logId, methodNm, "현재정보  단 조회");
//				jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getN7YSL006CurrV1", logId, methodNm, "크레인권하실적 우물정자 권하 단 조회");
				if (jsChk.size() > 0) {
					jrChk = jsChk.getRecord(0);
					String tbDnWrLayer = commUtils.trim(jrChk.getFieldString("YD_DN_WR_LAYER"));
					
					//우물정자 Bed 체크
					jrParam.setField("YS_STK_LYR_NO", tbDnWrLayer); //야드적치단구분
					
					jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL006CurrV1Bed", logId, methodNm, "현재정보 Bed 조회");
					
					if (jsChk.size() > 0) {

						jrChk = jsChk.getRecord(0);
						String tbDnWrBedNo = commUtils.trim(jrChk.getFieldString("YD_DN_WR_BED_NO"));
					
// 2025.11.12 크레인에서 올라온 좌표값 변경  막음						
//						if ("".equals(ydCrnXaxis) || "".equals(ydCrnYaxis) || "".equals(ydCrnZaxis)) {
//							ydCrnXaxis = commUtils.trim(jrChk.getFieldString("YD_DN_WR_XAXIS"));
//							ydCrnYaxis = commUtils.trim(jrChk.getFieldString("YD_DN_WR_YAXIS"));
//							ydCrnZaxis = commUtils.trim(jrChk.getFieldString("YD_DN_WR_ZAXIS"));
//						}						
					}
				}
				
			} else {
				// 일반 적치대 단 체크 
				
				jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL006Curr", logId, methodNm, "현재정보  조회");
				
				if (jsChk.size() > 0) {
					jrChk = jsChk.getRecord(0);
					String tbDnWrLayer = commUtils.trim(jrChk.getFieldString("YD_DN_WR_LAYER"));

					if (!ydDnWrLayer.equals(tbDnWrLayer)) {
						
						// 우물정자가 아닐 경우만 적용
						if(!"V1".equals(ydStkBedUseCd)&&!"TR".equals(ydDnWrLoc.substring(2, 4))) {
							commUtils.printLog(logId, "권하위치[" + ydDnWrLoc + "] 적치단 변경 : " + ydDnWrLayer + " -> " + tbDnWrLayer, "SL");
							chgDnWrLayer = true;
							ydDnWrLayer  = tbDnWrLayer;
							//ydCrnZaxis   = "";
							
							// 새 권하위치 단을 Clear 한다. 
							jrParam.setField("YS_STK_COL_GP", ydDnWrLoc.substring(0, 6)); // 야드적치열구분
							jrParam.setField("YS_STK_BED_NO", ydDnWrLoc.substring(6, 8)); // 야드적치Bed번호
							jrParam.setField("YS_STK_LYR_NO", ydDnWrLayer); //변경된 단
							
							commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYsStkLyrClr", logId, methodNm, "적치단 Clear");
							
							// 이전 권하위치 단을 Clear 한다.
							//jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
							//commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.clrUpDnWrkMtl", logId, methodNm, "적치단 작업Clear ");
						}
					}

// 2025.11.12 크레인에서 올라온 좌표값 변경  막음						
//					if ("".equals(ydCrnXaxis) || "".equals(ydCrnYaxis) || "".equals(ydCrnZaxis)) {
//						ydCrnXaxis = commUtils.trim(jrChk.getFieldString("YD_DN_WR_XAXIS"));
//						ydCrnYaxis = commUtils.trim(jrChk.getFieldString("YD_DN_WR_YAXIS"));
//						ydCrnZaxis = commUtils.trim(jrChk.getFieldString("YD_DN_WR_ZAXIS"));
//					}
					
				} else {
					resMsg.setField("YD_L3_HD_RS_CD", "DN14"); //야드L3처리결과코드
					resMsg.setField("YD_L3_MSG"     , "오류:권하위치 DB정보 없음"); //야드L3MESSAGE
					throw new Exception(ydL3Msg);
				}
			}


			// 야드권하작업수행구분
			String ydDnWrkActGp = ydEqpWrkMode;

// 2025.09.05 야드설비작업Mode(0:Manual, 1:Auto, 9:Backup) 막음
//			  I/F 에 정의된 "1": On-Line, "2": Off-Line  사용
//			if ("0".equals(ydEqpWrkMode)) {
//				ydDnWrkActGp = "M"; //Manual
//			} else if("1".equals(ydEqpWrkMode)) {
//				ydDnWrkActGp = "A"; //Auto
//			} else if("9".equals(ydEqpWrkMode)) {
//				ydDnWrkActGp = "B"; //Backup
//			}
			
			String currDt = commUtils.getDateTime14(); //현재시각

			//크레인스케쥴
			jrParam.setField("YD_DN_CMPL_DT"   , currDt      ); // 야드권하완료일시
			jrParam.setField("YD_DN_WR_LOC"    , ydDnWrLoc   ); // 야드권하실적위치
			jrParam.setField("YD_DN_WR_LAYER"  , ydDnWrLayer ); // 야드권하실적단
			jrParam.setField("YD_DN_WRK_ACT_GP", ydDnWrkActGp); // 야드권하작업수행구분
			jrParam.setField("YD_DN_WR_XAXIS"  , ydCrnXaxis  ); // 야드권하실적X축
			jrParam.setField("YD_DN_WR_YAXIS"  , ydCrnYaxis  ); // 야드권하실적Y축
			jrParam.setField("YD_DN_WR_ZAXIS"  , ydCrnZaxis  ); // 야드권하실적Z축
			jrParam.setField("WR_DT"           , currDt      ); // 실적일시
			jrParam.setField("UP_DN_GP"        , "D"         ); // 권상권하구분(권하)

			
			/**********************************************************
			* 3. 전문 전송
			* 3.1 스케쥴 코드에 따른 보급완료 전문 전송
			*     PRESS교정 보급완료
			*     ShotBlast 장입대 보급완료
			*     소형적재테이블 보급완료
			**********************************************************/
			
			
			if("GETF02".equals(ydDnWrLoc.substring(0, 6))) { 
				
				// PRESS교정기 장입 저장위치 권하 완료시(GETF02)
				// PRESS교정 보급완료(YSM4L210) 송신
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSM4L210", jrParam));
		    	
		    } else if( "GETF01".equals(ydDnWrLoc.substring(0, 6))
		    		|| "GETF03".equals(ydDnWrLoc.substring(0, 6))) { 
				
				// ShotBlast 장입대 보급완료시(GETF01)
				// ShotBlast 장입대 보급완료(YSM4L213) 송신
		    	// 2025.11.05 하부비파괴추출테이블(GETF03) 추가
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSM4L213", jrParam));
		    	
		    } else if("GETF04".equals(ydDnWrLoc.substring(0, 6))) { 
				
				// 소형적재테이블 보급완료(GETF04)
				// 소형적재테이블 보급완료(YSM4L216) 송신
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSM4L216", jrParam));
		    	
			}
			
			
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
			if("TR".equals(ydDnWrLoc.substring(2, 4))) {
				//상차 차량스케줄  조회
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID" , ydCrnSchId     ); 
				recPara.setField("YS_STK_COL_GP" , ydDnWrLoc.substring(0, 6) ); 

// 2025.12.17 Query 변경				
//				jsChk = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL006CarSchLd", logId, methodNm, "상차 차량스케줄 조회 "); 
				jsChk = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getN7YSL206CarSchLd", logId, methodNm, "상차 차량스케줄 조회 ");
		    	
				if (jsChk.size() > 0) {
					jrChk = jsChk.getRecord(0);

//					jrParam.setField("YD_CAR_SCH_ID", commUtils.trim(jrChk.getFieldString("YD_CAR_SCH_ID"))); //야드차량스케쥴ID(이력등록시에도 사용)
					
					ydCarSchId  = commUtils.trim(jrChk.getFieldString("YD_CAR_SCH_ID"));
					ydCarUseGp 	= commUtils.trim(jrChk.getFieldString("YD_CAR_USE_GP")); //야드차량사용구분
					carLdCmplYn = commUtils.trim(jrChk.getFieldString("CAR_LD_CMPL_YN")); //차량상차완료여부
					
////////////////////////////////////////////////////////////////////////
// 2025.12.17 차량 도착전 권상된 경우 권하 처리시 상차 개시 전송 START
					String sApplyYnPI = commDao.ApplyYnPI(logId, methodNm, "APPNEW", "019", "*");
					if("Y".equals(sApplyYnPI)){

						szMsg = "TB_YS_RULE APPNEW 019 차량 도착전 권상된 경우 권하 처리시 상차 개시 전송 ";
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
							recPara.setField("YD_CRN_SCH_ID", 	ydCrnSchId);
							
							jsChk = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getN7YSL005CarSchLd", logId, methodNm, "크레인권상실적 상차 차량스케줄 조회 "); 

							if (jsChk.size() > 0) {
								jrChk = jsChk.getRecord(0);

								// 상차개시 전문
								JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
								jrYdMsg.setResultCode(logId); // Log ID
								jrYdMsg.setResultMsg(methodNm); // Log Method Name

								if ("L".equals(jrChk.getFieldString("YD_CAR_USE_GP"))) {
									// 구내운송 소재차량상차개시
									jrYdMsg.setField("JMS_TC_CD", 			"YSTSJ007"); 												// JMSTC코드
									jrYdMsg.setField("JMS_TC_CREATE_DDTT", 	currDt); 													// JMSTC생성일시
									jrYdMsg.setField("TRN_EQP_CD", 			commUtils.trim(jrChk.getFieldString("TRN_EQP_CD"))); 		// 운송장비코드
									jrYdMsg.setField("SPOS_WLOC_CD", 		commUtils.trim(jrChk.getFieldString("SPOS_WLOC_CD"))); 		// 발지개소코드
									jrYdMsg.setField("SPOS_YD_PNT_CD", 		commUtils.trim(jrChk.getFieldString("SPOS_YD_PNT_CD"))); 	// 발지야드포인트코드
									jrYdMsg.setField("ARR_WLOC_CD", 		commUtils.trim(jrChk.getFieldString("ARR_WLOC_CD"))); 		// 착지개소코드
									jrYdMsg.setField("TRN_WRK_ST_DT", 		currDt); 													// 운송작업시작일시

									// 전송할 전문에 추가
									jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
								}


								recPara = JDTORecordFactory.getInstance().create();
								recPara.setField("MODIFIER", 		modifier);
								recPara.setField("ARR_WLOC_CD", 	commUtils.trim(jrChk.getFieldString("ARR_WLOC_CD"))); 	// 착지개소코드
								recPara.setField("YD_WBOOK_ID", 	ydWbookId);
								recPara.setField("WR_DT", 			commUtils.getDateTime14());
								recPara.setField("YD_CAR_SCH_ID", 	commUtils.trim(jrChk.getFieldString("YD_CAR_SCH_ID"))); // 야드차량스케쥴ID
								// 상차 차량스케줄 야드설비작업상태, 야드차량진행상태, 야드상차작업예약ID, 착지개소코드 등 수정

								commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CarSchLd", logId, methodNm, " 상차 차량스케줄 수정 ");
							}
							
						}
					}
// 2025.12.17 차량 도착전 권상된 경우 권하 처리시 상차 개시 전송 END
////////////////////////////////////////////////////////////////////////
						
					
					
					//차량이송재료(TB_YS_CARFTMVMTL) 상차 등록
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_CAR_SCH_ID", 	ydCarSchId ); 
					recPara.setField("MODIFIER" ,		modifier     ); 
					recPara.setField("YD_CRN_SCH_ID", 	ydCrnSchId );
// 2025.12.03 차량 상차완료 ON/OFF 기준관련 항목 추가 					
					recPara.setField("YS_DN_WR_LOC", 	ydDnWrLoc );

					commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updN7YSL206CarMtlIns", logId, methodNm, " 상차 이송재료 등록 ");
					
					
// 2025.11.24 구내운송 상차완료
					carLdCmplYn = CarTSMv.carLdCmplYn(logId, recPara);
					
					//차량스케줄 야드설비작업매수, 중량, 야드차량진행상태, 야드상차개시일시, 야드상차완료일시 등 수정
					recPara = JDTORecordFactory.getInstance().create();
					
// 2025.10.16 상차 완료 막음 (L3 화면에서 상차완료 백업 처리)							
					if ("Y".equals(carLdCmplYn)) {
						recPara.setField("YD_CAR_PROG_STAT", "5"); //야드차량진행상태(상차완료)
					} else {
						recPara.setField("YD_CAR_PROG_STAT", "4"); //야드차량진행상태(상차개시)
					}
					
//					recPara.setField("YD_CAR_PROG_STAT", "4"); //야드차량진행상태(상차개시)
					
					recPara.setField("YD_WBOOK_ID" 		, ydWbookId ); 
					recPara.setField("YS_STK_COL_GP" 	, ydDnWrLoc.substring(0, 6) ); 
					recPara.setField("WR_DT" 			, currDt ); 
					recPara.setField("YD_CAR_SCH_ID" 	, ydCarSchId ); 
					recPara.setField("MODIFIER" , modifier     ); 
					

// 2025.09.09 TB_YS_CARSCH 테이블 YD_PNT_CD3(야드포인트코드3) = '0000' 하는 부분이 있어 Query 변경
//					commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CarSchLd", logId, methodNm, " 상차 차량스케줄 수정 ");
					commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updN7YSL006CarSchLd", logId, methodNm, " 상차 차량스케줄 수정 ");

					recPara = JDTORecordFactory.getInstance().create();
					recPara.setResultCode(logId);	//Log ID
					recPara.setResultMsg(methodNm);	//Log Method Name
					recPara.setField("YD_CAR_SCH_ID", ydCarSchId); 
					recPara.setField("YD_CRN_SCH_ID", ydCrnSchId); 
					recPara.setField("MODIFIER"     , "N7YSL206"     ); 	// 수정자
					

					if ("L".equals(ydCarUseGp)) {
// 2025.11.24 상차 완료 기준 조회 "Y" 이면 상차 완료 전송 (YSTSJ008)							
						if ("Y".equals(carLdCmplYn)) {
							
// 2025.10.29 항상 대형 옥외 가는 물량이 아니기 때문에 저장품제원 전송 막음							
//							// 빌렛옥외야드(L2)이송시 송신
//							recPara.setField("YD_INFO_SYNC_CD", "7" );
//							jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN8L002", recPara));
							
// 2025.10.15 상차 완료 막음 (L3 화면에서 상차완료 처리)
							jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSTSJ008", recPara));
						}	

						// 이송지시 상차 완료 UPDATE
						CarTSMv.updPbStlFrToMoveUp(recPara);
						
//TB_YS_PREPSCH(YS_준비스케줄), TB_YS_PREPMTL(YS_준비재료) 종료
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

			if ("TR".equals(ydUpWrLoc.substring(2, 4)) && !"TR".equals(ydDnWrLoc.substring(2, 4))) {
				//야드하차작업예약ID 차량하차스케줄 정보 조회
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID" 		, ydWbookId ); 
				
				jsChk = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL006CarSchUd", logId, methodNm, "하차 차량스케줄 조회 "); 
		    	
				if (jsChk.size() > 0) {
					jrChk = jsChk.getRecord(0);

					jrParam.setField("YD_CAR_SCH_ID", commUtils.trim(jrChk.getFieldString("YD_CAR_SCH_ID"))); //야드차량스케쥴ID(이력등록시에도 사용)
					ydCarSchId  = commUtils.trim(jrChk.getFieldString("YD_CAR_SCH_ID"));
					carUdCmplYn = commUtils.trim(jrChk.getFieldString("CAR_UD_CMPL_YN")); //차량하차완료여부
					
					//차량하차완료이면
					if ("Y".equals(carUdCmplYn)) {
						//하차 차량스케줄 야드설비작업상태, 야드차량진행상태, 야드상차작업예약ID, 착지개소코드 등 수정
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setResultCode(logId);	//Log ID
						recPara.setResultMsg(methodNm);	//Log Method Name
						recPara.setField("MODIFIER" , modifier     ); 
						recPara.setField("WR_DT" 			, currDt ); 
						recPara.setField("YD_CAR_SCH_ID" 	, ydCarSchId ); 
						
						commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CarSchUd", logId, methodNm, "하차 차량스케줄 수정  ");

						
						//구내운송 소재차량하차완료
						jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSTSJ010", recPara));
						
						
						
					}
				} else {
					//야드하차작업예약ID가 없는 경우 권상실적수신에서 작업예약 재료번호로 차량하차스케줄ID를 조회하여 작업예약ID 등록
					commUtils.printLog(logId, "권하실적 하차 차량스케줄이 없습니다.", "SL");
				}
			}

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
			jrParam2.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
			jrParam2.setField("YS_STK_COL_GP", "GE%"); 
			jrParam2.setField("YS_STK_BED_NO", "%"); 
			commDao.update(jrParam2, "com.inisteel.cim.ys.common.dao.YsCommDAO.clrUpDnWrkMtlN7", logId, methodNm, "적치단 작업Clear ");
	
///////////////////////////////////////////////////////////			
// 2025.12.04 설비 보급 이나 추출시 Seq 역순 등록
			String sApplyYnPI = commDao.ApplyYnPI(logId, methodNm, "APPNEW", "007", "*");
			if("Y".equals(sApplyYnPI)){

				szMsg = "TB_YS_RULE APPNEW 007 발랫정정야드 Seq 역순 등록 처리 ";
	      		commUtils.printLog(logId, szMsg, "");
				
	      		if("Y".equals(szREVERSE_YN))	
	      		{ 
					szMsg = "Seq 역순 등록";
		      		commUtils.printLog(logId, szMsg, "");
					
		    	    int intRtnVal	= 0 ;
		      		
					// 적치단(크레인 및 권하위치) 수정
		    	    intRtnVal = commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyrSeqReverse", logId, methodNm, "빌렛정정 크레인 권하실적 적치단 Seq 역순 수정 ");
	
					if(intRtnVal <= 0) {
						resMsg.setField("YD_L3_HD_RS_CD", "DN14"); 					// 야드L3처리결과코드
						resMsg.setField("YD_L3_MSG"     , "오류 : 권하 위치 역순 변경 오류 "); 	// 야드L3MESSAGE
						throw new Exception(ydL3Msg);
					}	      		
	
					szMsg = "빌렛정정 크레인 권하실적 적치단 Seq 역순 수정 수정 건수 [" + intRtnVal + "]";
		      		commUtils.printLog(logId, szMsg, "");
	      		} else {
					szMsg = " Seq 역순 변경 없음";
		      		commUtils.printLog(logId, szMsg, "");
	      		}
				
			}
///////////////////////////////////////////////////////////			
			
			
			
			sApplyYnPI = commDao.ApplyYnPI(logId, methodNm, "APPNEW", "003", "*");

			if("Y".equals(sApplyYnPI)){
// 2025.11.30 권하 실적 처리시 권하 위치YS_적치단(TB_YS_STKLYR) 에 재료정보가 없는 상태가 크레인 작업지시에서 생성되어 UPDATE 건수 없으면 오류 처리 체크 추가 
				szMsg = "TB_YS_RULE 003_권하실적 건수 체크 ";
	      		commUtils.printLog(logId, szMsg, "");
				
	    	    int intRtnVal	= 0 ;
	      		
				// 적치단(크레인 및 권하위치) 수정
	    	    intRtnVal = commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updN7YSL206StkLyr", logId, methodNm, "적치단(크레인 및 권하위치) 수정");

				szMsg = "적치단(크레인 및 권하위치) 수정 건수 [" + intRtnVal + "]";
	      		commUtils.printLog(logId, szMsg, "");

				
			}
			else {
				// 적치단(크레인 및 권하위치) 수정
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updN7YSL206StkLyr", logId, methodNm, "적치단(크레인 및 권하위치) 수정");
			}			
			
			//크레인작업재료 삭제
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CrnMtl", logId, methodNm, "크레인작업재료 삭제");
			
			//크레인스케쥴 권하실적 수정 및 삭제
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CrnSch", logId, methodNm, "크레인스케쥴 권하실적 수정 및 삭제");

			//작업예약완료 이면
			if ("Y".equals(wbCmplYn)) {
				//작업예약재료 삭제
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006WbMtlDel", logId, methodNm, "작업예약재료 삭제");
				//작업예약 수정 및 삭제
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006WbDel", logId, methodNm, "작업예약 수정 및 삭제");
			} else {
				//크레인작업재료번호로 작업예약재료 삭제 (작업예약재료에 작업이 완료된 재료는 DEL_YN='Y' 함으로써 스케줄 취소 후 재 작업시 작업대상에서 제외시킨다.) 
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
			
			//BILLET공통 수정 전에 야드에서 진도코드 변경하는 경우 진행관리로 YSPBJ002 전송
			// 1)이송이면서 여재구부이 1이면  진도코드를 'B', 2이면  진도코드를 'Y'로 변경
			// 2)주문재이면서 진도코드D 이면 진도코드를 B로 변경 
			jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
// 2025.08.20 진행관리 BILLET야드에의한진도변경 (권하처리) 부분 막음			
//			jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSPBJ002BySchId", jrParam));

			//BILLET공통 수정**
			// 1)이송이면서 여재구부이 1이면 'B', 2이면 'Y' 로 변경
			// 2)주문재이면서 진도코드D 이면 진도코드를 B로 변경 
// 2025.09.05 BILLET공통 수정 부분 막음			
// 2025.10.17 빌렛이나 번들 공통 야드저장위치 UPDATE			
			jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchToBilletComm", logId, methodNm, " 빌렛 공통 조회 "); 
			if (jsChk.size() > 0) {
				// 빌렛 이면
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006BilletComm", logId, methodNm, "빌렛 공통 수정");
			}
			else {
				// 빌렛 아니면
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006BundleComm", logId, methodNm, "번들 공통 수정");
			}
			
			//이송지시 수정 -- WC
// 2025.09.05 TB_PB_STLFRTOMOVE UPDATE 막음			
// 2025.10.25 권상 위치가 차량 이면			
//			if ("Y".equals(carUdCmplYn)) { // 하차 - 권하실적작업시 
			if ("TR".equals(ydUpWrLoc.substring(2, 4))) {
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updN7YSL206StlfrToMove", logId, methodNm, "이송지시 수정");	
			}
			
			//저장품 수정**
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updN7YSL206Stock", logId, methodNm, "저장품 수정");
			
			//작업이력 등록
			jrParam.setField("YD_CAR_SCH_ID"   , commUtils.trim(jrParam.getFieldString("YD_CAR_SCH_ID" ))); 
			jrParam.setField("YD_TCAR_SCH_ID"  , commUtils.trim(jrParam.getFieldString("YD_TCAR_SCH_ID" ))); 
			jrParam.setField("YD_CRN_SCH_ID"   , commUtils.trim(jrParam.getFieldString("YD_CRN_SCH_ID" ))); 
			
// 2025.12.12 차량 작업시 TB_YS_WRKBOOK 테이블에 차량 정보 UPDATE 개선
			sApplyYnPI = commDao.ApplyYnPI(logId, methodNm, "APPNEW", "017", "*");
			if("Y".equals(sApplyYnPI)){
				szMsg = "TB_YS_RULE 017 권상,권하실적 처리시 차량이면 TB_YS_WRKHIST 에 운송장비코드 UPDATE";
	      		commUtils.printLog(logId, szMsg, "");

	      		jrParam.setField("YD_CAR_SCH_ID"   , ydCarSchId); 
			}
			
			commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkHist", logId, methodNm, "작업이력 등록");
			
			/**********************************************************
			* 8. 권하지시위치 단과 실적 단이 다르면 저장품제원 전문 전송(YSNxL002)
			**********************************************************/
			if (chgDnWrLayer) {
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN7L202DnWr", jrParam));
			}
			
			resMsg.setField("YD_UP_WR_LOC", ydUpWrLoc); //야드권상실적위치
			resMsg.setField("YD_DN_WR_LOC", ydDnWrLoc); //야드권하실적위치

			//크레인작업실적응답 전송
			if (resYn) {
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
				jrRtn = commUtils.addSndData(jrRtn, cbtYsComm.getYSN7L204(resMsg));
			}
			
			/**********************************************************
			* 9. 전문 전송 (재료진도 업데이트 후에 다시 그 정보를 읽어서 보내줌, 그래서 이 위치에 놓음
			*     생산통제 빌렛입고실적(YSCUJ038)
			**********************************************************/
// 2025.08.20 생산통제 소형압연입고실적 부분 막음				
//			if ("TF".equals(ydUpWrLoc.substring(2, 4))||"TR".equals(ydUpWrLoc.substring(2, 4))) {
//				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSCUJ038", jrParam));
//			}

// 2025.12.10 권하 처리후 다음 작업지시 요청 부분 개선(EAI에서 권하 응답 보다 다음 작업지시가 먼저 전송 되는 현상 빌생 하여 개선)
			sApplyYnPI = commDao.ApplyYnPI(logId, methodNm, "APPNEW", "013", "*");
			if("Y".equals(sApplyYnPI)){
				szMsg = "TB_YS_RULE APPNEW 013 권하 처리후 다음 작업지시 요청";
	      		commUtils.printLog(logId, szMsg, "");
// 1안 내부 처리 YSYSJ001 사용
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setResultCode(logId);	//Log ID
				jrYdMsg.setResultMsg(methodNm);	//Log Method Name

				jrYdMsg.setField("JMS_TC_CD"         , "YSYSJ001"               ); //JMSTC코드
				jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
				jrYdMsg.setField("YD_EQP_ID"         , ydEqpId              	); //야드설비ID
				jrYdMsg.setField("YD_WRK_PROG_STAT"  , "W"                      ); //야드작업진행상태: [W:명령선택대기]
				jrYdMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId); //야드크레인스케쥴ID

				commUtils.printLog(logId, "JMS_TC_CD >>>>>>>>>> " + jrYdMsg.toString(), "");
				
				jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
				
// 2안  EJBConnector 사용
//		    	JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
//		    	jrYdMsg.setResultCode(logId);	//Log ID
//		    	jrYdMsg.setResultMsg(methodNm);	//Log Method Name	
//				
//		    	jrYdMsg.setField("JMS_TC_CD"       , "N7YSL204"	) ;	//크레인작업지시요구
//		    	jrYdMsg.setField("YD_EQP_ID"       , ydEqpId    );	//야드설비ID
//		    	jrYdMsg.setField("YD_WRK_PROG_STAT", "W"		);	//야드작업진행상태(권상작업지시)
//		    	
//	    		EJBConnector ejbConn = new EJBConnector("default", "CbtYsL2RcvSeEJB", this);
//	    		jrRtn = (JDTORecord)ejbConn.trx("rcvN7YSL204", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
				
	      		
			} else {
				/**********************************************************
				* 10. 크레인작업지시요구 전문 호출(NxYDL004)
				**********************************************************/
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setResultCode(logId);	//Log ID
				jrYdMsg.setResultMsg(methodNm);	//Log Method Name

				jrYdMsg.setField("JMS_TC_CD", msgId.substring(0, 2) + "YSL204"); //JMSTC코드
					
				jrYdMsg.setField("YD_EQP_ID"       , ydEqpId   ); //야드설비ID
				jrYdMsg.setField("YD_WRK_PROG_STAT", "4"       ); //야드작업진행상태(권하완료)
				jrYdMsg.setField("YD_SCH_CD"       , ydSchCd   ); //야드스케쥴코드
				jrYdMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId); //야드크레인스케쥴ID
				jrYdMsg.setField("MODIFIER"        , modifier  ); //수정자
				
				//크레인작업지시 전문을 추가
				jrRtn = commUtils.addSndData(jrRtn, this.rcvN7YSL204(jrYdMsg));
			}
			

			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			if (resYn) {
				try {
					
					//PIDEV_F : 정상SET후  ERROR 발생한 경우								
					if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
						resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
						resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
					}
					
					//크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "YsCommEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { cbtYsComm.getYSN7L204(resMsg) });
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인운전모드전환(N7YSL207)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvN7YSL207(JDTORecord rcvMsg) throws DAOException {
        String szMsg	= "";
        String methodNm = "크레인운전모드전환[CbtYsL2RcvSeEJB.rcvN7YSL207] < " + rcvMsg.getResultMsg();
        String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);	


		commUtils.printLog(logId, methodNm, "S+");
        
        szMsg = rcvMsg.toString();
		commUtils.printLog(logId, szMsg, "");
		
		
		
		try {

            /*
			7	YS_EQP_ID			야드설비ID			CHAR	6
			8	YS_EQP_WRK_MODE		야드설비작업Mode		CHAR	1
			9	YS_EQP_WRK_MODE2	야드설비작업Mode2	CHAR	1
            */

			// 수신 항목 값
			String msgId        	= commUtils.getMsgId(rcvMsg); 										// EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId      	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"          )); 	// 야드설비ID
			String ydEqpWrkMode 	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE"    )); 	// 야드설비작업Mode
			String ydEqpWrkMode2	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE2"   )); 	// 야드설비작업Mode2
			
			methodNm = msgId.substring(0, 2) + methodNm;

            szMsg = "\n\t YD_EQP_ID   		: " 	+ ydEqpId 
              	  + "\n\t YD_EQP_WRK_MODE   : " 	+ ydEqpWrkMode 
              	  + "\n\t YD_EQP_WRK_MODE2  : " 	+ ydEqpWrkMode2;

    		commUtils.printLog(logId, szMsg, "");
            
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				throw new Exception("오류:설비ID 없음");
			} else if ("".equals(ydEqpWrkMode)) {
				throw new Exception("야드설비작업Mode 없음");
			} else if ("".equals(ydEqpWrkMode2)) {
				throw new Exception("야드설비작업Mode2 없음");
			}

			/**********************************************************
			* 2. 설비테이블 업데이트
			**********************************************************/
			
			
			
			/**********************************************************
			* 3. 크레인작업실적응답(YSN7L204) 전문 생성(모드변경) 
			**********************************************************/

			// 전송 Data 생성
			JDTORecord resMsg = JDTORecordFactory.getInstance().create(); 	// 크레인작업실적응답 전문 생성용
			
			// 크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);									// Log ID
			resMsg.setResultMsg(methodNm);									// Log Method Name
			resMsg.setField("YD_EQP_ID"       , ydEqpId     	); 			// 야드설비ID
			resMsg.setField("YD_L2_WR_GP"     , "M"         	); 			// 야드L2실적구분(운전모드전환)
			resMsg.setField("YD_L3_HD_RS_CD"  , "0000"      	); 			// 야드L3처리결과코드
			resMsg.setField("YD_L3_MSG"       , ""				); 			// 야드L3MESSAGE
			
			JDTORecord jrRtn = null;	// 전문 Return
			jrRtn = commUtils.addSndData(jrRtn, cbtYsComm.getYSN7L204(resMsg));
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	}
	


	
	/**
	 *      [A] 오퍼레이션명 : 차량작업예정정보요구(N7YSL208)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvN7YSL208(JDTORecord rcvMsg) throws DAOException {

        String szMsg	= "";
        String methodNm = "차량작업예정정보요구[CbtYsL2RcvSeEJB.rcvN7YSL208] < " + rcvMsg.getResultMsg();
        String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);	


		commUtils.printLog(logId, methodNm, "S+");
        
        szMsg = rcvMsg.toString();
		commUtils.printLog(logId, szMsg, "");
		
		
		
		try {

            /*
			7	PT_LOAD_LOC			상차도 위치			CHAR	6
            */

			// 수신 항목 값
			String msgId        	= commUtils.getMsgId(rcvMsg); 										// EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sPtLoadLoc     	= commUtils.trim(rcvMsg.getFieldString("PT_LOAD_LOC"          )); 	// 상차도 위치
			
			methodNm = msgId.substring(0, 2) + methodNm;

            szMsg = "\n\t PT_LOAD_LOC   	: " 	+ sPtLoadLoc ;

    		commUtils.printLog(logId, szMsg, "");
            
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(sPtLoadLoc)) {
				throw new Exception("오류:상차도 위치 없음");
			}

			
			/**********************************************************
			* 2. 차량작업예정정보(YSN7L205) 전문 생성
			**********************************************************/

			// 전송 Data 생성
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			JDTORecord jrRtn = null;	// 전문 Return
			
			// 차량작업예정정보 전문 생성용
			jrParam.setResultCode(logId);									// Log ID
			jrParam.setResultMsg(methodNm);									// Log Method Name
			
			jrParam.setField("PT_LOAD_LOC"       , sPtLoadLoc     	); 		// 상차도 위치
			
			jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN7L205", jrParam));
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}

	}
	
	
	
	
	
	
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 대형압연 빌렛/봉강 출하상 추출요구(M3YSL201)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvM3YSL201(JDTORecord rcvMsg) throws DAOException {
        String szMsg	= "";
		String methodNm = "대형압연 빌렛/봉강 출하상 추출요구[CbtYsL2RcvSeEJBBean.rcvM3YSL201]] < " + rcvMsg.getResultMsg();
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);
		
		JDTORecord jrParam = JDTORecordFactory.getInstance().create();
//		JDTORecord inParam = JDTORecordFactory.getInstance().create();
		String sExcptMsg = "";
		String sIfData = "";
		String msgId = null;
//		JDTORecordSet jsTemp = null;
//		JDTORecord jrTemp = null;
//		String szStkStlNo = null;

		JDTORecord jrRtn = null;

		try {
			commUtils.printLog(logId, methodNm, "S+");

	        szMsg = rcvMsg.toString();
			commUtils.printLog(logId, szMsg, "");
			
            /*
			7	YD_EQP_ID			야드설비ID 			CHAR	6
			8	YD_STK_BED_STL_SH	야드적치Bed재료매수	NUMBER	2
			9	YD_EQP_WRK_SH 		야드설비작업매수		NUMBER	2
			10	STL_APPEAR_GP		재료외형구분			CHAR	1
			11	SSTL_NO1			특수강재료번호1		CHAR	12
			12	SSTL_NO2			특수강재료번호2		CHAR	12
			13	SSTL_NO3			특수강재료번호3		CHAR	12
			14	SSTL_NO4			특수강재료번호4		CHAR	12
			15	SSTL_NO5			특수강재료번호5		CHAR	12
			16	SSTL_NO6			특수강재료번호6		CHAR	12
			17	SSTL_NO7			특수강재료번호7		CHAR	12
			18	SSTL_NO8			특수강재료번호8		CHAR	12
			19	SSTL_NO9			특수강재료번호9		CHAR	12
			20	SSTL_NO10			특수강재료번호10		CHAR	12
			21	MV_CD				이송코드			CHAR	9
            */
			
			//수신 항목 값
			msgId         		 	= commUtils.getMsgId(rcvMsg); 
			String ydEqpId       	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"        )); 	// 야드설비ID
			String ysStkBedNo    	= "01"; 														// 특수강야드적치Bed번호(전문에서 베드번호 항목 삭제)
			String ydStkBedStlSh 	= commUtils.trim(rcvMsg.getFieldString("YD_STK_BED_STL_SH")); 	// 야드적치Bed재료매수
			String ydEqpWrkSh 		= commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_SH")); 		// 야드설비작업매수
			String stlAppearGp 		= commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP")); 		// 재료외형구분

			String L3Hmi         	= commUtils.nvl(rcvMsg.getFieldString("L3_HMI"),"N");         	// 백업화면 기동 여부

            szMsg = "\n\t YD_EQP_ID   		: " 	+ ydEqpId 
              	  + "\n\t YS_STK_BED_NO 	: " 	+ ysStkBedNo 
               	  + "\n\t YD_STK_BED_STL_SH : " 	+ ydStkBedStlSh 
               	  + "\n\t YD_EQP_WRK_SH 	: " 	+ ydEqpWrkSh 
               	  + "\n\t STL_APPEAR_GP 	: " 	+ stlAppearGp 
               	  + "\n\t L3_HMI     		: " 	+ L3Hmi;

      		commUtils.printLog(logId, szMsg, "");
			
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6 || !ydEqpId.startsWith("GE")) {
				sExcptMsg = "전문 이상! 설비ID[" + ydEqpId + "]";
			} else if ("".equals(ysStkBedNo)) {
				sExcptMsg = "전문 이상! 적치Bed번호 없음";
			} else if ("".equals(ydEqpWrkSh)) {
				sExcptMsg = "전문 이상! 야드설비작업매수 없음";
			}

			if(!"".equals(sExcptMsg)) {
				throw new Exception(sExcptMsg);
			}			

// *************** TB_YS_STOCK 테이블 INSERT/UPDATE (현재 BILLET 부분만 있음 봉강 추가 필요)
//          재료 외형 구분			
//			M - Bloom 	: Bloom 생산 ~ 대형압연 이전
//			T - Billet 	: 대형압연완료 ~ 소형압연 이전
//			L - 대형봉강 	: 대형압연완료 ~ 제품창고 입고 이전 (종합판정)
//			P - 각강(빌렛) 	: 각강생산(대형압연) ~ 빌렛정정완료 이전
//			Q - 각강(번들) 	: 빌렛정정완료 ~ 제품창고 입고 이전(종합판정)
//			S - 소형봉강 	: 소형압연완료 ~ 제품창고 입고 이전 (종합판정)
//			W - 선재 		: 선재생산(소형압연) ~ 제품창고 입고 이전 (종합판정)
//			N - 바인코일 	: BIC생산(소형압연) ~ 제품창고 입고 이전 (종합판정)
			

// 2025.12.08 출하상 ON/OFF
			String sApplyYnPI = commDao.ApplyYnPI(logId, methodNm, "APPNEW", "010", "*");
			String sOnOff = "N";
			if("Y".equals(sApplyYnPI)){
				
				szMsg = "TB_YS_RULE APPNEW 010 대형 압연 출하상 [" + ydEqpId +"] ON/OFF ";
	      		commUtils.printLog(logId, szMsg, "");
				
	      		sOnOff = CarTSMv.ApplyRuleItem(logId, methodNm, "GE0001", ydEqpId);
	      		if("N".equals(sOnOff)){

	      			szMsg = "TB_YS_RULE APPNEW 010 대형 압연 출하상 [" + ydEqpId +"] 추출 요구 OFF 추출요구 작업 하지 않음 ";
		      		commUtils.printLog(logId, szMsg, "");

					commUtils.printLog(logId, methodNm, "S-");
					
					return jrRtn;
	      			
	      		}

			}
			
			
			if(ydEqpId.startsWith("GEPC3") || ydEqpId.startsWith("GEPC4") || ydEqpId.startsWith("GEPC5")) {
// 2025.08.26 출하상 위치로는 빌렛, 봉강 구분이 되지 않기 때문에 
//  		  TB_PB_BILLETCOMM 에 있으면  M3YSL201Billet 아니면 M3YSL201Bundle Call
//			
				
				String stlNo 			= "";															// 재료번호
				stlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO"+1));

	            szMsg = "\n\t SSTL_NO   	: " 	+ stlNo;

        		commUtils.printLog(logId, szMsg, "");

				JDTORecord recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("BLT_NO" , stlNo     ); 
				
				JDTORecordSet jsChk = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getBilletComm", logId, methodNm, "BILLET 공통 조회");

				if (jsChk.size() > 0) {
					
					// 빌렛 출하상
					jrRtn =  M3YSL201Billet(rcvMsg);
					
				} else {
					
					// 번들 출하상
					jrRtn =  M3YSL201Bundle(rcvMsg);
					
				}
				
			} else {
				sExcptMsg = "전문 이상! 설비ID[" + ydEqpId + "]";
				throw new Exception(sExcptMsg);
			}

			// 전송할 Data가 있으면 전송 처리
//			if (jrRtn != null) {
//
//		        szMsg = jrRtn.toString();
//				commUtils.printLog(logId, "jrRtn = " + szMsg, "");
//				
//				jrRtn.setResultCode(logId);
//				jrRtn.setResultMsg(methodNm);
//
//				EJBConnector sndConn = new EJBConnector("default", "YsCommEJB", this);
//				sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn });
//			} 
			
			commUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
			
		} catch (DAOException e) {
			//if("".equals(sExcptMsg)) {
			//	sExcptMsg = e.getMessage();
			//}
			throw e;
		} catch (Exception e) {
			//if("".equals(sExcptMsg)) {
			//	sExcptMsg = e.getMessage();
			//}
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
	 *      [A] 오퍼레이션명 : 대형압연 빌렛 출하상 추출요구
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord M3YSL201Billet(JDTORecord rcvMsg) throws DAOException {
        String szMsg	= "";
		String methodNm = "대형압연 빌렛 출하상 추출요구[CbtYsL2RcvSeEJBBean.M3YSL201Billet]] < " + rcvMsg.getResultMsg();
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);
		
		JDTORecord jrParam = JDTORecordFactory.getInstance().create();
		JDTORecord inParam = JDTORecordFactory.getInstance().create();
		
		String sExcptMsg = "";
		String sIfData = "";
		String msgId = null;
		JDTORecordSet jsTemp = null;
		JDTORecord jrTemp = null;
		String szStkStlNo = null;

		JDTORecord jrRtn = null;
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

            /*
			7	YD_EQP_ID			야드설비ID 			CHAR	6
			8	YS_STK_BED_NO		특수강야드적치Bed번호	CHAR	2
			9	YD_STK_BED_STL_SH	야드적치Bed재료매수	NUMBER	2
			10	YD_EQP_WRK_SH 		야드설비작업매수		NUMBER	2
			11	SSTL_NO1			특수강재료번호1		CHAR	12
			12	SSTL_NO2			특수강재료번호2		CHAR	12
			13	SSTL_NO3			특수강재료번호3		CHAR	12
			14	SSTL_NO4			특수강재료번호4		CHAR	12
			15	SSTL_NO5			특수강재료번호5		CHAR	12
			16	SSTL_NO6			특수강재료번호6		CHAR	12
			17	SSTL_NO7			특수강재료번호7		CHAR	12
			18	SSTL_NO8			특수강재료번호8		CHAR	12
			19	SSTL_NO9			특수강재료번호9		CHAR	12
			20	SSTL_NO10			특수강재료번호10		CHAR	12
			21	MV_CD				이송코드			CHAR	9
            */

			//수신 항목 값
			msgId         			= commUtils.getMsgId(rcvMsg); 
			String ydEqpId       	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID")			); 	// 야드설비ID
			String ysStkBedNo    	= "01"; 														// 특수강야드적치Bed번호
			String ydStkBedStlSh 	= commUtils.trim(rcvMsg.getFieldString("YD_STK_BED_STL_SH")	); 	// 야드적치Bed재료매수
			String ydEqpWrkSh 		= commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_SH")		); 	// 야드설비작업매수
			String ydMvCd 			= commUtils.trim(rcvMsg.getFieldString("MV_CD")				); 	// 이송코드
			String modifier      	= commUtils.trim(rcvMsg.getFieldString("MODIFIER")			); 	// 수정자(Backup Only)
			String L3Hmi         	= commUtils.nvl(rcvMsg.getFieldString("L3_HMI"), "N"		);  // 백업화면 기동 여부
			
			String sstlNo 			= "";															// 재료번호
			String insYsStkBedNo	= "01";  														// 재료 적치 할 bed 
			String ydPrepSchId		= "";															// 야드준비스케쥴ID(차량상차작업예약ID)
			
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

            szMsg = "\n\t YD_EQP_ID   		: " 	+ ydEqpId 
              	  + "\n\t YS_STK_BED_NO     : " 	+ ysStkBedNo 
              	  + "\n\t YD_STK_BED_STL_SH : " 	+ ydStkBedStlSh
              	  + "\n\t YD_EQP_WRK_SH 	: " 	+ ydEqpWrkSh
              	  + "\n\t MV_CD 			: " 	+ ydMvCd;

    		commUtils.printLog(logId, szMsg, "");
			

    		int mtlSh = Integer.parseInt(ydEqpWrkSh); // 야드설비작업매수
			
			for (int ii = 1; ii <= mtlSh; ii++) {
				
				sIfData += commUtils.trim(rcvMsg.getFieldString("SSTL_NO"+ii));
				if(ii < mtlSh) {
					sIfData += ", ";
				}
				
			}

// 2025.11.26 적치열 매수 만큼만 트래킹 처리 
			if(8 < mtlSh) {
				mtlSh = 8;
			}
			    		
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6 || !ydEqpId.startsWith("GE")) {
				sExcptMsg = "전문 이상! 설비ID[" + ydEqpId + "]";
			} else if ("".equals(ysStkBedNo)) {
				sExcptMsg = "전문 이상! 적치Bed번호 없음";
			} else if ("".equals(ydEqpWrkSh)) {
				sExcptMsg = "전문 이상! 야드설비작업매수 없음";
			}

			if(!"".equals(sExcptMsg)) {
				throw new Exception(sExcptMsg);
			}			

			JDTORecordSet jsChk = JDTORecordFactory.getInstance().createRecordSet("Temp");

			if(L3Hmi.equals("N")) {   // 전문이 오는 경우
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("YS_STK_COL_GP"      , ydEqpId   ); //야드적치열구분
				

				// 화면에서 백업시 야드 적치위치 등록 된 후에 온다
				/**********************************************************
				* 2.1 재료번호  CHECK : 저장위치 등록여부
				**********************************************************/
				
				for (int ii = 1; ii <= mtlSh; ii++) {
					sstlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	// 재료번호
							
					if ("".equals(sstlNo)) {
						sExcptMsg = "전문 이상! 재료번호(SSTL_NO" + ii + ") 없음";
						throw new Exception(sExcptMsg);
					}
					jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setResultCode(logId);	//Log ID
					jrParam.setResultMsg(methodNm);	//Log Method Name
					jrParam.setField("YS_STK_COL_GP"      , ydEqpId   ); 	// 야드적치열구분
					jrParam.setField("SSTL_NO"			  , sstlNo    ); 	// 재료 번호
					jrParam.setField("YS_STK_BED_NO"      , "01"); 			// 야드적치Bed번호
					
					
					jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getSstlNoYardYN", logId, methodNm, "저장위치 등록여부");
		
					if (jsChk != null && jsChk.size() > 0) {
						
						jrTemp = jsChk.getRecord(0);
						
						sExcptMsg   = "이미 야드에 적치된 재료 "
							        + sstlNo	
									+ " ["
									+ commUtils.trim(jrTemp.getFieldString("YS_STK_COL_GP")) 
									+ "-" 
						            + commUtils.trim(jrTemp.getFieldString("YS_STK_BED_NO"))
						            + "]";

						String sApplyYnPI = commDao.ApplyYnPI(logId, methodNm, "APPNEW", "001", "*");
						
						if("Y".equals(sApplyYnPI)){
							szMsg = "TB_YS_RULE 001 이미 야드에 적치된 재료 적용여부";
				      		commUtils.printLog(logId, szMsg, "");
							
							return jrRtn;
							
						}
						else {
							throw new Exception(sExcptMsg);
						}
					}	
	
				}
			}
			
			/**********************************************************
			* 2.1  저장품 등록
			**********************************************************/
			
			if(L3Hmi.equals("N")) {
			

				/**********************************************************
				* 2.1 적재단, 저장품 등록
				**********************************************************/

				for (int ii = 1; ii <= mtlSh; ii++) {
					sstlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	//재료번호
							
					if ("".equals(sstlNo)) {
						sExcptMsg = "전문 이상! 재료번호(SSTL_NO" + ii + ") 없음";
						throw new Exception(sExcptMsg);
					}
					jrParam = JDTORecordFactory.getInstance().create();
					
					
					
					jrParam.setResultCode(logId);							// Log ID
					jrParam.setResultMsg(methodNm);							// Log Method Name
					jrParam.setField("YS_STK_COL_GP"      , ydEqpId   ); 	// 야드적치열구분
					jrParam.setField("SSTL_NO"			  , sstlNo    ); 	// 재료번호
					jrParam.setField("YS_STK_BED_NO"      , insYsStkBedNo); // 야드적치Bed번호
					jrParam.setField("YS_STK_LYR_NO"      , "01"); 			// 야드적치 단 -> 무조건 1단
					jrParam.setField("YS_STK_SEQ_NO", String.valueOf(ii)); 	// 야드적치SEQ
					jrParam.setField("YD_STK_LYR_MTL_STAT", "C"       ); 	// 야드적치단재료상태(적치중)
					jrParam.setField("MODIFIER"           , modifier  ); 	// 수정자
					
					// 재료번호 이전 저장위치 재료번호 CLEAR
					jrParam.setField("YD_STK_LYR_MTL_STAT", "E"       ); 	// 야드적치단재료상태(적치가능)
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyrStlNoClear", logId, methodNm, "야드 적치단 재료번호 CLEAR"); 

					
					
					jrParam.setField("YD_STK_LYR_MTL_STAT", "C"       ); 	// 야드적치단재료상태(적치중)
					
					
					
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyr", logId, methodNm, "야드 적치단 등록"); 
					
				 
				}
			
			} else {
				insYsStkBedNo = ysStkBedNo;
			}			

			jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER"           , modifier  ); //수정자			
			

			for (int ii = 1; ii <= mtlSh; ii++) {
				sstlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	//재료번호

				jrParam.setField("SSTL_NO"	, sstlNo    ); 
				jrParam.setField("CARRY_OUT", "Y"       ); 
				commDao.insert(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.insBtYdStock", logId, methodNm, "야드 저장품 등록"); 
				
				
				inParam = JDTORecordFactory.getInstance().create();
				inParam.setResultCode(logId);	//Log ID
				inParam.setResultMsg(methodNm);	//Log Method Name
				//추가, 이동
				inParam.setField("FNL_REG_PGM"			, "M3YSL201Billet" );
				inParam.setField("YD_GP"				, "G" );
				inParam.setField("YD_BAY_GP"			, ydEqpId.substring(1,2) );
				inParam.setField("YD_EQP_GP"			, ydEqpId.substring(2,4) );
				inParam.setField("YS_STK_COL_NO"		, ydEqpId.substring(4,6) );
				inParam.setField("YS_STK_BED_NO"		, commUtils.trim(ysStkBedNo) );
				inParam.setField("YS_STK_LYR_NO"		, "01" );
				inParam.setField("YS_STK_SEQ_NO"		, ""+ii );
				inParam.setField("YS_STR_LOC"			, ydEqpId 
						                                + commUtils.trim(ysStkBedNo)
						                                + "01"
						                                + ""+ii) ;
				inParam.setField("SSTL_NO"				, sstlNo  );
				
				/**********************************************************
				* 1.2공통 저장위치 Update (별도 Transaction 으로 처리)
				**********************************************************/
				EJBConnector tranConn = new EJBConnector("default", "BtYsL2RcvSeEJB", this);
				tranConn.trx("updBtCommYsStrLoc", new Class[] { JDTORecord.class }, new Object[] { inParam });
			}

			
			//STOCK 재료번호가 존재하지 않으면 작업예약을 만들지 않는다.
			for (int ii = 1; ii <= mtlSh; ii++) {
				
				sstlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	//재료번호
				
				jrParam.setField("SSTL_NO"	, sstlNo );
				
				//제품번호가 야드저장품에 존재하는지, 작업예약과 크레인스케줄에 잡혀있는 대상인지를 가져오고 BILLET공통에서 현재진도코드를 가져온다. 
				jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.getIsEnableStlNo", logId, methodNm, "제품번호 작업대상여부 조회");
				
				if (jsTemp != null && jsTemp.size() > 0) {
					
					jrTemp = jsTemp.getRecord(0);
	
					szStkStlNo		= commUtils.trim(jrTemp.getFieldString("SSTL_NO")); //Stock 에 존재하지 않으면 ""
			    } 			
				
				//저장품에 존재하는 제품번호인지 체크
				if("".equals(szStkStlNo)) {

					sExcptMsg = "전단실적 누락 " + sstlNo;
					//throw new Exception(sExcptMsg);
					break;
				}
			}
			
			
			if("".equals(sExcptMsg)){
				
				// 작업예약등록
				// CbtYsJspSeEJBBean  	>> updbtMvStkWrkBook 
//				String stlNos = commUtils.trim(gdReq.getParam("SSTL_NOS")); // 재료번호들
//				String ysStkColGp = commUtils.trim(gdReq.getParam("YS_STK_COL_GP")); // 야드적치열구분(6자리 이상)
//				String ydToLocGuide = commUtils.trim(gdReq.getParam("YD_TO_LOC_GUIDE")); // 야드To위치Guide
//				String ydWrkCrn = commUtils.trim(gdReq.getParam("YD_WRK_CRN")); // 야드작업크레인(작업자지정 크레인)
				GridData inGridData = new GridData();

				String stlNos = "";
				for (int ii = 1; ii <= mtlSh; ii++) {
// 2025.09.01 재료 번호로 블룹 번호 SELECT 후 신규 재료 이송 테이블 TO 위치 체크 추출 대상 TO 위치가 동일 하면 작업 예약 등록시 TO 위치 가이드에 넣고 다른게 있으면 TO 위치 가이드 XXXXXX  
					stlNos += commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	// 재료번호
					if(ii < mtlSh) {
						stlNos += ",";
					}
					
				}

// 2025.09.01 재료번호로 블룹 번호 SELECT 후 신규 재료 이송 테이블 TO 위치 체크 추출대상이 동일한 TO 위치이면 TO 위치 가이드 설정
// 			    테이블에 없으면 "" 
//			    하나 이상이면 "GEXXXXXX" 로 	 TO 위치 가이드 설정
				sstlNo = "";
				String ydRcptPlnStrLoc 		= "";		// TB_YS_LMILLPLNSTRLOC.YD_RCPT_PLN_STR_LOC(야드입고예정위치)
				String ydRcptPlnStrLocFirst = "";		// 첫번째 TB_YS_LMILLPLNSTRLOC.YD_RCPT_PLN_STR_LOC(야드입고예정위치)
				String ydRcptPlnStrLocCur 	= "";		// 현재  TB_YS_LMILLPLNSTRLOC.YD_RCPT_PLN_STR_LOC(야드입고예정위치)
				
				String sApplyYnPI = commDao.ApplyYnPI(logId, methodNm, "APPNEW", "004", "*");
				
			
				
				for (int ii = 1; ii <= mtlSh; ii++) {
// 2025.09.01 재료 번호로 블룹 번호 SELECT 후 신규 재료 이송 테이블 TO 위치 체크 추출 대상 TO 위치가 동일 하면 작업 예약 등록시 TO 위치 가이드에 넣고 다른게 있으면 TO 위치 가이드 GEXXXXXX  
					sstlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	// 재료번호

					jrParam.setField("SSTL_NO"	, sstlNo );
					
					//제품번호가 야드저장품에 존재하는지, 작업예약과 크레인스케줄에 잡혀있는 대상인지를 가져오고 BILLET공통에서 현재진도코드를 가져온다. 
					jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getydRcptPlnStrLoc", logId, methodNm, "빌렛 출하상 추출 야드입고예정위치 조회");
					// 첫번째 야드입고예정위치
					if(ii == 1 && jsTemp.size() > 0) {
						ydRcptPlnStrLocFirst 	= commUtils.trim(jsTemp.getRecord(0).getFieldString("YD_RCPT_PLN_STR_LOC"   ));
						ydRcptPlnStrLocCur 		= commUtils.trim(jsTemp.getRecord(0).getFieldString("YD_RCPT_PLN_STR_LOC"   ));
					}

					if (jsTemp == null || jsTemp.size() == 0) {
						// 빌렛 출하상 추출 야드입고예정위치  없음
						ydRcptPlnStrLocCur = "";
					}
					
					if(!ydRcptPlnStrLocFirst.equals(ydRcptPlnStrLocCur)) {
						// 첫번째 재료 이후 야드입고예정위치가 첫번째와 다르면
						// 빌렛 출하상 추출 야드입고예정위치  GEXXXXXX
						
						if( jsTemp.size() > 0) {
							szMsg = "봉강빌렛 출하상 추출 야드입고예정위치 재료번호 [" + sstlNo + "] 야드입고예정위치가 처음 [" 
								      + ydRcptPlnStrLocFirst + "] 현재 [" + commUtils.trim(jsTemp.getRecord(0).getFieldString("YD_RCPT_PLN_STR_LOC")) + "]가 다릅니다.";	
							commUtils.printLog(logId, szMsg, "");
						}
						else{
							szMsg = "봉강빌렛 출하상 추출 야드입고예정위치 재료번호 [" + sstlNo + "] 야드입고예정위치가 처음 [" 
								      + ydRcptPlnStrLocFirst + "] 과 다릅니다.";	
							commUtils.printLog(logId, szMsg, "");
						}
						
						if(!"Y".equals(sApplyYnPI)){
							ydRcptPlnStrLocFirst = "GEXXXXXX";
						}

						break;
						
						
						
					}
					
				}

				
// 2025.09.09 TO 위치 가이드가 개소코드 이면 이송지시 테이블 INSERT
				
				szMsg = "****** ydRcptPlnStrLocFirst : [" 	+ ydRcptPlnStrLocFirst + "]";
	      		commUtils.printLog(logId, szMsg, "");
				
// 개소 코드가 대형옥외 옥외야드(S3Y21, 철분말(BSY04)   	      		
				if(  "S3Y21".equals(ydRcptPlnStrLocFirst)
				  || "BSY04".equals(ydRcptPlnStrLocFirst)) {

					sstlNo = "";
					for (int ii = 1; ii <= mtlSh; ii++) {
						
						sstlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	// 재료번호
						
// 2025.09.11 재료번호 별로 이송지시 테이블 INSERT					
						
						jrParam = JDTORecordFactory.getInstance().create();

						jrParam.setResultCode(logId);							// Log ID
						jrParam.setResultMsg(methodNm);							// Log Method Name
						jrParam.setField("SSTL_NO"      		, sstlNo   				); 	// 재료번호
						jrParam.setField("USER_ID"      		, modifier				); 	// 등록자, 수정자
						jrParam.setField("SPOS_WLOC_CD"			, "S3S20"   			); 	// 대형옥내 남4문 - (빌렛/각강) 출하상, 봉강출하상은 무조건 남4문 이송출고	 
						jrParam.setField("ARR_WLOC_CD"      	, ydRcptPlnStrLocFirst	); 	// 착지 개소 코드 - TB_YS_LMILLPLNSTRLOC.YD_RCPT_PLN_STR_LOC(야드입고예정위치)
						jrParam.setField("REG_PGM"				, "YardSystem "			); 	// 등록프로그램 

						szMsg = "****** insPbStlFrToMove jrParam : " + jrParam.toString();
			      		commUtils.printLog(logId, szMsg, "");
						
						// 이송지시 테이블 INSERT
//						commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.insPbStlFrToMove", logId, methodNm, "이송지시 등록");
						insPbStlFrToMove(jrParam);
						
					}
					
				}

				
// 개소 코드가 대형옥외 옥외야드(S3Y21, 철분말(BSY04)   	      		
				if(  "S3Y21".equals(ydRcptPlnStrLocFirst)
				  || "BSY04".equals(ydRcptPlnStrLocFirst)) {
					
					
// 2025.12.05 해당 출하상 OFF 이면 출하상 추출요구 처리 하지 않음 로직 추가 필요 
					
					
					
// 2025.09.16 TB_YS_PREPSCH(YS_준비스케줄), TB_YS_PREPMTL(YS_준비재료) 등록 
					szMsg = "TB_YS_PREPSCH(YS_준비스케줄), TB_YS_PREPMTL(YS_준비재료) 등록";
		      		commUtils.printLog(logId, szMsg, "******");

					String 	szYD_SCH_CD			= "";
					String 	szYD_AIM_BAY_GP		= "";
					
					if("S3Y21".equals(ydRcptPlnStrLocFirst)) {
						// 이송 출고
						szYD_SCH_CD 		= "GETR41UA";
						szYD_AIM_BAY_GP		= "F";
			    	}else if("BSY04".equals(ydRcptPlnStrLocFirst)) {
						szYD_SCH_CD 		= "GETR41UA";
						szYD_AIM_BAY_GP		= "H";
					}
					else {
						szYD_SCH_CD 		= "GEPC01LA";
						szYD_AIM_BAY_GP		= "E";
					}

					jrParam = JDTORecordFactory.getInstance().create();
					
					jrParam.setResultCode(logId);											// Log ID
					jrParam.setResultMsg(methodNm);											// Log Method Name

					ydPrepSchId = commDao.getSeqId(logId, methodNm, "PrepSch"	);			// 야드준비스케쥴ID
					
					jrParam.setField("YD_PREP_SCH_ID", 		ydPrepSchId					); 	// 야드준비스케쥴ID
					jrParam.setField("YD_SCH_CD", 			szYD_SCH_CD					); 	// 스케줄코드
					jrParam.setField("YD_PREP_WK_ST", 		"L"							); 	// 야드준비작업상태
					jrParam.setField("YD_AIM_BAY_GP", 		szYD_AIM_BAY_GP				); 	// 목적동
					
					sstlNo = "";
					for (int ii = 1; ii <= mtlSh; ii++) {
						
						sstlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	// 재료번호

						//준비재료 등록
						jrParam.setField("SSTL_NO"			, sstlNo); 
						jrParam.setField("YD_GP"			, "G"); 
						jrParam.setField("YS_STK_COL_GP"	, ydEqpId); 
						jrParam.setField("YS_STK_BED_NO"	, "01"); 
						jrParam.setField("YS_STK_LYR_NO"	, "01"); 
						jrParam.setField("YS_STK_SEQ_NO"	, String.valueOf(ii)); 
						jrParam.setField("MODIFIER"			, modifier); 	// 등록자, 수정자
						
						commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insPrepMtl", logId, methodNm, "준비재료 등록");
						
					}
					
					commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insPrepSchAimBay", logId, methodNm, "준비스케줄 등록");
					
					szMsg = "야드준비스케쥴ID [" + ydPrepSchId + "] 스케줄코드 [" + szYD_SCH_CD + "] 등록";
		      		commUtils.printLog(logId, szMsg, "******");
					
					
				}
				
				
// 2025.09.10 야드To위치Guide 가 개소코드 이면  남4문 위치로 설정 
// 개소 코드가 대형옥외 옥외야드(S3Y21, 철분말(BSY04)   	      		
				if(  "S3Y21".equals(ydRcptPlnStrLocFirst)
				  || "BSY04".equals(ydRcptPlnStrLocFirst)) {
					// 대형 옥외 야드, 사외통합 야드  
					ydRcptPlnStrLoc = "GETR41";
					
				}
				else {
					
					ydRcptPlnStrLoc = ydRcptPlnStrLocFirst;
					
				}
				
				inGridData.addParam("SSTL_NOS", 		stlNos);			// 재료번호들
				inGridData.addParam("YS_STK_COL_GP", 	ydEqpId);			// 야드적치열구분(6자리 이상)
//				inGridData.addParam("YD_TO_LOC_GUIDE", 	ydMvCd);			// 야드To위치Guide
				inGridData.addParam("YD_TO_LOC_GUIDE", 	ydRcptPlnStrLoc);	// 야드To위치Guide
				inGridData.addParam("YD_WRK_CRN", 		"");				// 야드작업크레인(작업자지정 크레인)
				inGridData.addParam("userid", 			modifier);			// 수정자

// 2025.10.28 야드준비스케쥴ID(차량상차작업예약ID) 추가				
				inGridData.addParam("YD_PREP_SCH_ID", 	ydPrepSchId);		// 야드준비스케쥴ID(차량상차작업예약ID)
				
				szMsg = "\n\t SSTL_NO   		: " 	+ stlNos 
                	  + "\n\t YS_STK_COL_GP     : " 	+ ydEqpId 
                	  + "\n\t YD_TO_LOC_GUIDE   : " 	+ ydRcptPlnStrLoc;

	      		commUtils.printLog(logId, szMsg, "");

	      		
	      		
// 2025.12.05 해당 출하상 OFF 이면 출하상 추출요구 처리 하지 않음 로직 추가 필요 

	      		
// 작업 예약 등록				
				jrRtn = CbtYsJsp.updbtMvStkWrkBook(inGridData);
				
//				//예외처리 없을 경우만 호출한다.
//				/**********************************************************
//				* 3. 대형압연 빌렛/봉강 출하상 추출요구 처리
//				**********************************************************/
//				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
//
//				// 설비인출요구
//				jrYdMsg.setResultCode(logId);	//Log ID
//				jrYdMsg.setResultMsg(methodNm);	//Log Method Name
//				jrYdMsg.setField("JMS_TC_CD"         , "YSYSJ502"               ); // JMSTC코드
//				jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); // JMSTC생성일시
//				jrYdMsg.setField("YD_EQP_ID"         , ydEqpId                  ); // 야드설비ID
//				jrYdMsg.setField("YS_STK_BED_NO"     , insYsStkBedNo  			); // 야드적치Bed번호
//				jrYdMsg.setField("YS_STK_LYR_NO"     , "01"               		); // 야드적치단번호
//				jrYdMsg.setField("YD_SCH_ST_GP"      , "A"                      ); // 야드스케쥴기동구분(Auto)
//				jrYdMsg.setField("MODIFIER"        , modifier                 	); // 수정자
//				
//		        szMsg = jrYdMsg.toString();
//				commUtils.printLog(logId, szMsg, "");
//				
//				//전송할 전문에 추가
//				jrRtn = commUtils.addSndData(jrYdMsg);
				
				
			}

			commUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
			
		} catch (DAOException e) {
			//if("".equals(sExcptMsg)) {
			//	sExcptMsg = e.getMessage();
			//}
			throw e;
		} catch (Exception e) {
			//if("".equals(sExcptMsg)) {
			//	sExcptMsg = e.getMessage();
			//}
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
	 *      [A] 오퍼레이션명 : 대형압연 봉강 출하상 추출요구
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord M3YSL201Bundle(JDTORecord rcvMsg) throws DAOException {
        String szMsg	= "";
		String methodNm = "대형압연 봉강 출하상 추출요구[CbtYsL2RcvSeEJBBean.M3YSL201Bundle]] < " + rcvMsg.getResultMsg();
		String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);
		
		JDTORecord jrParam = JDTORecordFactory.getInstance().create();
		JDTORecord inParam = JDTORecordFactory.getInstance().create();
		
		String sExcptMsg = "";
		String sIfData = "";
		String msgId = null;
		JDTORecordSet jsTemp = null;
		JDTORecord jrTemp = null;
		String szStkStlNo = null;
		
		JDTORecord jrRtn = null;

		try {
			commUtils.printLog(logId, methodNm, "S+");

            /*
			7	YD_EQP_ID			야드설비ID 			CHAR	6
			8	YS_STK_BED_NO		특수강야드적치Bed번호	CHAR	2
			9	YD_STK_BED_STL_SH	야드적치Bed재료매수	NUMBER	2
			10	YD_EQP_WRK_SH 		야드설비작업매수		NUMBER	2
			11	SSTL_NO1			특수강재료번호1		CHAR	12
			12	SSTL_NO2			특수강재료번호2		CHAR	12
			13	SSTL_NO3			특수강재료번호3		CHAR	12
			14	SSTL_NO4			특수강재료번호4		CHAR	12
			15	SSTL_NO5			특수강재료번호5		CHAR	12
			16	SSTL_NO6			특수강재료번호6		CHAR	12
			17	SSTL_NO7			특수강재료번호7		CHAR	12
			18	SSTL_NO8			특수강재료번호8		CHAR	12
			19	SSTL_NO9			특수강재료번호9		CHAR	12
			20	SSTL_NO10			특수강재료번호10		CHAR	12
			21	MV_CD				이송코드			CHAR	9
            */

			// 수신 항목 값
			msgId         			= commUtils.getMsgId(rcvMsg); 
			String ydEqpId       	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"        )); 	// 야드설비ID
			String ysStkBedNo    	= "01"; 														// 특수강야드적치Bed번호
			String ydStkBedStlSh 	= commUtils.trim(rcvMsg.getFieldString("YD_STK_BED_STL_SH")); 	// 야드적치Bed재료매수
			String ydEqpWrkSh 		= commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_SH")); 		// 야드설비작업매수
			String ydMvCd 			= commUtils.trim(rcvMsg.getFieldString("MV_CD")); 				// 이송코드
			String modifier      	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"       )); 	// 수정자(Backup Only)
			String L3Hmi         	= commUtils.nvl(rcvMsg.getFieldString("L3_HMI"),"N");         	// 백업화면 기동 여부
			
			String sstlNo 			= "";															// 재료번호
			String insYsStkBedNo	= "01";  														// 재료 적치 할 bed 
			String ydPrepSchId		= "";															// 야드준비스케쥴ID(차량상차작업예약ID)

			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

            szMsg = "\n\t YD_EQP_ID   		: " 	+ ydEqpId 
              	  + "\n\t YS_STK_BED_NO     : " 	+ ysStkBedNo 
              	  + "\n\t YD_STK_BED_STL_SH : " 	+ ydStkBedStlSh
              	  + "\n\t YD_EQP_WRK_SH 	: " 	+ ydEqpWrkSh
              	  + "\n\t MV_CD 			: " 	+ ydMvCd;

    		commUtils.printLog(logId, szMsg, "");
			
			int mtlSh = Integer.parseInt(ydEqpWrkSh); // 야드설비작업매수
			
			for (int ii = 1; ii <= mtlSh; ii++) {
				
				sIfData += commUtils.trim(rcvMsg.getFieldString("SSTL_NO"+ii));
				if(ii < mtlSh) {
					sIfData += ", ";
				}
				
			}

// 2025.11.26 적치열 매수 만큼만 트래킹 처리 
			if(8 < mtlSh) {
				mtlSh = 8;
			}
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6 || !ydEqpId.startsWith("GE")) {
				sExcptMsg = "전문 이상! 설비ID[" + ydEqpId + "]";
			} else if ("".equals(ysStkBedNo)) {
				sExcptMsg = "전문 이상! 적치Bed번호 없음";
			} else if ("".equals(ydEqpWrkSh)) {
				sExcptMsg = "전문 이상! 야드설비작업매수 없음";
			}

			if(!"".equals(sExcptMsg)) {
				throw new Exception(sExcptMsg);
			}			

			JDTORecordSet jsChk = JDTORecordFactory.getInstance().createRecordSet("Temp");

			if(L3Hmi.equals("N")) {   // 전문이 오는 경우
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("YS_STK_COL_GP"      , ydEqpId   ); //야드적치열구분
				

				// 화면에서 백업시 야드 적치위치 등록 된 후에 온다
				/**********************************************************
				* 2.1 재료번호  CHECK : 저장위치 등록여부
				**********************************************************/
				
				for (int ii = 1; ii <= mtlSh; ii++) {
					sstlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	// 재료번호
							
					if ("".equals(sstlNo)) {
						sExcptMsg = "전문 이상! 재료번호(SSTL_NO" + ii + ") 없음";
						throw new Exception(sExcptMsg);
					}
					jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setResultCode(logId);	//Log ID
					jrParam.setResultMsg(methodNm);	//Log Method Name
					jrParam.setField("YS_STK_COL_GP"      , ydEqpId   ); 	// 야드적치열구분
					jrParam.setField("SSTL_NO"			  , sstlNo    ); 	// 재료 번호
					jrParam.setField("YS_STK_BED_NO"      , "01"); 			// 야드적치Bed번호
					
					
					jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getSstlNoYardYN", logId, methodNm, "저장위치 등록여부");
		
					if (jsChk != null && jsChk.size() > 0) {
						
						jrTemp = jsChk.getRecord(0);
						
						sExcptMsg   = "이미 야드에 적치된 재료 "
							        + sstlNo	
									+ " ["
									+ commUtils.trim(jrTemp.getFieldString("YS_STK_COL_GP")) 
									+ "-" 
						            + commUtils.trim(jrTemp.getFieldString("YS_STK_BED_NO"))
						            + "]";

						String sApplyYnPI = commDao.ApplyYnPI(logId, methodNm, "APPNEW", "001", "*");
						
						if("Y".equals(sApplyYnPI)){
							
							szMsg = "TB_YS_RULE 001 이미 야드에 적치된 재료 적용여부 ";
				      		commUtils.printLog(logId, szMsg, "");
							
							return jrRtn;
						}
						else {
							throw new Exception(sExcptMsg);
						}
					}	
	
				}
			}
			
			/**********************************************************
			* 2.1  저장품 등록
			**********************************************************/
			
			if(L3Hmi.equals("N")) {
			

				/**********************************************************
				* 2.1 적재단, 저장품 등록
				**********************************************************/

				for (int ii = 1; ii <= mtlSh; ii++) {
					sstlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	//재료번호
							
					if ("".equals(sstlNo)) {
						sExcptMsg = "전문 이상! 재료번호(SSTL_NO" + ii + ") 없음";
						throw new Exception(sExcptMsg);
					}
					jrParam = JDTORecordFactory.getInstance().create();
					
					jrParam.setResultCode(logId);										// Log ID
					jrParam.setResultMsg(methodNm);										// Log Method Name
					jrParam.setField("YS_STK_COL_GP"      	, ydEqpId   ); 				// 야드적치열구분
					jrParam.setField("SSTL_NO"			  	, sstlNo     ); 			// 재료번호
					jrParam.setField("YS_STK_BED_NO"      	, insYsStkBedNo); 			// 야드적치Bed번호
					jrParam.setField("YS_STK_LYR_NO"      	, "01"); 					// 야드적치 단 -> 무조건 1단
					jrParam.setField("YS_STK_SEQ_NO"		, String.valueOf(ii)); 		// 야드적치SEQ
					jrParam.setField("YD_STK_LYR_MTL_STAT"	, "C"       ); 				// 야드적치단재료상태(적치중)
					jrParam.setField("MODIFIER"           	, modifier  ); 				// 수정자
					
					// 재료번호 이전 저장위치 재료번호 CLEAR
					jrParam.setField("YD_STK_LYR_MTL_STAT", "E"       ); 	// 야드적치단재료상태(적치가능)
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyrStlNoClear", logId, methodNm, "야드 적치단 재료번호 CLEAR"); 

					
					
					jrParam.setField("YD_STK_LYR_MTL_STAT", "C"       ); 	// 야드적치단재료상태(적치중)
					
					
					
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyr", logId, methodNm, "야드 적치단 등록"); 
					
				 
				}
			
			} else {
				insYsStkBedNo = ysStkBedNo;
			}			

			jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER"           , modifier  ); //수정자			
			
			
			for (int ii = 1; ii <= mtlSh; ii++) {
				sstlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	//재료번호

				jrParam.setField("SSTL_NO"	, sstlNo     ); 
				jrParam.setField("CARRY_OUT", "Y"       ); 
				commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insBundleYdStock", logId, methodNm, "야드 저장품 등록"); 
				
				
				inParam = JDTORecordFactory.getInstance().create();
				inParam.setResultCode(logId);	//Log ID
				inParam.setResultMsg(methodNm);	//Log Method Name
				//추가, 이동
				inParam.setField("FNL_REG_PGM"			, "M3YSL201Bundle" );
				inParam.setField("YD_GP"				, "G" );
				inParam.setField("YD_BAY_GP"			, ydEqpId.substring(1,2) );
				inParam.setField("YD_EQP_GP"			, ydEqpId.substring(2,4) );
				inParam.setField("YS_STK_COL_NO"		, ydEqpId.substring(4,6) );
				inParam.setField("YS_STK_BED_NO"		, commUtils.trim(ysStkBedNo) );
				inParam.setField("YS_STK_LYR_NO"		, "01" );
				inParam.setField("YS_STK_SEQ_NO"		, ""+ii );
				inParam.setField("YS_STR_LOC"			, ydEqpId 
						                                + commUtils.trim(ysStkBedNo)
						                                + "01"
						                                + ""+ii) ;
				inParam.setField("SSTL_NO"				, sstlNo  );
				
				/**********************************************************
				* 1.2공통 저장위치 Update (별도 Transaction 으로 처리)
				**********************************************************/
				EJBConnector tranConn = new EJBConnector("default", "CbtYsL2RcvSeEJB", this);
				tranConn.trx("updBndlCommYsStrLoc", new Class[] { JDTORecord.class }, new Object[] { inParam });
			}


			//STOCK 재료번호가 존재하지 않으면 작업예약을 만들지 않는다.
			for (int ii = 1; ii <= mtlSh; ii++) {
				
				sstlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	//재료번호
				
				jrParam.setField("SSTL_NO"	, sstlNo );
				
				//제품번호가 야드저장품에 존재하는지, 작업예약과 크레인스케줄에 잡혀있는 대상인지를 가져오고 BILLET공통에서 현재진도코드를 가져온다. 
				jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.getIsEnableStlNo", logId, methodNm, "제품번호 작업대상여부 조회");
				
				if (jsTemp != null && jsTemp.size() > 0) {
					
					jrTemp = jsTemp.getRecord(0);
	
					szStkStlNo		= commUtils.trim(jrTemp.getFieldString("SSTL_NO")); //Stock 에 존재하지 않으면 ""
			    } 			
				
				//저장품에 존재하는 제품번호인지 체크
				if("".equals(szStkStlNo)) {

					sExcptMsg = "전단실적 누락 " + sstlNo;
					//throw new Exception(sExcptMsg);
					break;
				}
			}
			
			
			if("".equals(sExcptMsg)){
				
				// 작업예약등록
				// CbtYsJspSeEJBBean  	>> updbtMvStkWrkBook 
//				String stlNos = commUtils.trim(gdReq.getParam("SSTL_NOS")); // 재료번호들
//				String ysStkColGp = commUtils.trim(gdReq.getParam("YS_STK_COL_GP")); // 야드적치열구분(6자리 이상)
//				String ydToLocGuide = commUtils.trim(gdReq.getParam("YD_TO_LOC_GUIDE")); // 야드To위치Guide
//				String ydWrkCrn = commUtils.trim(gdReq.getParam("YD_WRK_CRN")); // 야드작업크레인(작업자지정 크레인)
				GridData inGridData = new GridData();

				String stlNos = "";
				for (int ii = 1; ii <= mtlSh; ii++) {
					
					stlNos += commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	// 재료번호
					if(ii < mtlSh) {
						stlNos += ",";
					}
					
				}

// 2025.09.01 재료번호로 블룹 번호 SELECT 후 신규 재료 이송 테이블 TO 위치 체크 추출대상이 동일한 TO 위치이면 TO 위치 가이드 설정
//			    테이블에 없으면 "" 
//			    하나 이상이면 "GEXXXXXX" 로 	 TO 위치 가이드 설정
				sstlNo = "";
				String ydRcptPlnStrLoc 		= "";		// TB_YS_LMILLPLNSTRLOC.YD_RCPT_PLN_STR_LOC(야드입고예정위치)
				String ydRcptPlnStrLocFirst = "";		// 첫번째 TB_YS_LMILLPLNSTRLOC.YD_RCPT_PLN_STR_LOC(야드입고예정위치)
				String ydRcptPlnStrLocCur 	= "";		// 현재  TB_YS_LMILLPLNSTRLOC.YD_RCPT_PLN_STR_LOC(야드입고예정위치)
				
				String sApplyYnPI = commDao.ApplyYnPI(logId, methodNm, "APPNEW", "004", "*");
				
				for (int ii = 1; ii <= mtlSh; ii++) {
// 2025.09.01 재료 번호로 블룹 번호 SELECT 후 신규 재료 이송 테이블 TO 위치 체크 추출 대상 TO 위치가 동일 하면 작업 예약 등록시 TO 위치 가이드에 넣고 다른게 있으면 TO 위치 가이드 GEXXXXXX  
					sstlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	// 재료번호

					jrParam.setField("SSTL_NO"	, sstlNo );
					
					//제품번호가 야드저장품에 존재하는지, 작업예약과 크레인스케줄에 잡혀있는 대상인지를 가져오고 BILLET공통에서 현재진도코드를 가져온다. 
					jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getydRcptPlnStrLoc", logId, methodNm, "봉강출하상 추출 야드입고예정위치 조회");
					// 첫번째 야드입고예정위치
					if(ii == 1 && jsTemp.size() > 0) {
						ydRcptPlnStrLocFirst 	= commUtils.trim(jsTemp.getRecord(0).getFieldString("YD_RCPT_PLN_STR_LOC"   ));
						ydRcptPlnStrLocCur 		= commUtils.trim(jsTemp.getRecord(0).getFieldString("YD_RCPT_PLN_STR_LOC"   ));
					}

					if (jsTemp == null || jsTemp.size() == 0) {
						// 빌렛 출하상 추출 야드입고예정위치  없음
						ydRcptPlnStrLocCur = "";
					}
					
					if(!ydRcptPlnStrLocFirst.equals(ydRcptPlnStrLocCur)) {
						// 첫번째 재료 이후 야드입고예정위치가 첫번째와 다르면
						// 빌렛 출하상 추출 야드입고예정위치  GEXXXXXX

						if( jsTemp.size() > 0) {
							szMsg = "봉강빌렛 출하상 추출 야드입고예정위치 재료번호 [" + sstlNo + "] 야드입고예정위치가 처음 [" 
								      + ydRcptPlnStrLocFirst + "] 현재 [" + commUtils.trim(jsTemp.getRecord(0).getFieldString("YD_RCPT_PLN_STR_LOC")) + "]가 다릅니다.";	
							commUtils.printLog(logId, szMsg, "");
						}
						else{
							szMsg = "봉강빌렛 출하상 추출 야드입고예정위치 재료번호 [" + sstlNo + "] 야드입고예정위치가 처음 [" 
								      + ydRcptPlnStrLocFirst + "] 과 다릅니다.";	
							commUtils.printLog(logId, szMsg, "");
						}
						
						if(!"Y".equals(sApplyYnPI)){
							ydRcptPlnStrLocFirst = "GEXXXXXX";
						}

						break;
						
					}
					
										
				}
					
				
// 2025.09.09 TO 위치 가이드가 개소코드 이면 이송지시 테이블 INSERT
				
				szMsg = "****** ydRcptPlnStrLocFirst : [" 	+ ydRcptPlnStrLocFirst + "]";
	      		commUtils.printLog(logId, szMsg, "");
				
				if(  "S3Y21".equals(ydRcptPlnStrLocFirst)
				  || "BSY04".equals(ydRcptPlnStrLocFirst)) {

					sstlNo = "";
					for (int ii = 1; ii <= mtlSh; ii++) {

						sstlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	// 재료번호
						
// 2025.09.11 재료번호 별로 이송지시 테이블 INSERT					
						
						jrParam = JDTORecordFactory.getInstance().create();

						jrParam.setResultCode(logId);							// Log ID
						jrParam.setResultMsg(methodNm);							// Log Method Name
						jrParam.setField("SSTL_NO"      		, sstlNo   				); 	// 재료번호
						jrParam.setField("USER_ID"      		, modifier				); 	// 등록자, 수정자
						jrParam.setField("SPOS_WLOC_CD"			, "S3S20"   			); 	// 대형옥내 남4문 - (빌렛/각강) 출하상, 봉강출하상은 무조건 남4문 이송출고	 
						jrParam.setField("ARR_WLOC_CD"      	, ydRcptPlnStrLocFirst	); 	// 착지 개소 코드 - TB_YS_LMILLPLNSTRLOC.YD_RCPT_PLN_STR_LOC(야드입고예정위치)
						jrParam.setField("REG_PGM"				, "YardSystem "			); 	// 등록프로그램 
						
						// 이송지시 테이블 INSERT
//						commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.insPbStlFrToMove", logId, methodNm, "이송지시 등록");
						insPbStlFrToMove(jrParam);
						
					}
					
				}

				if(  "S3Y21".equals(ydRcptPlnStrLocFirst)
				  || "BSY04".equals(ydRcptPlnStrLocFirst)) {

// 2025.09.16 TB_YS_PREPSCH(YS_준비스케줄), TB_YS_PREPMTL(YS_준비재료) 등록 
					szMsg = "TB_YS_PREPSCH(YS_준비스케줄), TB_YS_PREPMTL(YS_준비재료) 등록";
		      		commUtils.printLog(logId, szMsg, "******");

					String 	szYD_SCH_CD			= "";
					String 	szYD_AIM_BAY_GP		= "";
					
					if("S3Y21".equals(ydRcptPlnStrLocFirst)) {
						// 이송 출고
						szYD_SCH_CD 		= "GETR41UA";
						szYD_AIM_BAY_GP		= "F";
			    	}else if("BSY04".equals(ydRcptPlnStrLocFirst)) {
						szYD_SCH_CD 		= "GETR41UA";
						szYD_AIM_BAY_GP		= "H";
					}
					else {
						szYD_SCH_CD 		= "GEPC01LA";
						szYD_AIM_BAY_GP		= "E";
					}

					jrParam = JDTORecordFactory.getInstance().create();
					
					jrParam.setResultCode(logId);											// Log ID
					jrParam.setResultMsg(methodNm);											// Log Method Name

					ydPrepSchId = commDao.getSeqId(logId, methodNm, "PrepSch"	);			// 야드준비스케쥴ID
					
					jrParam.setField("YD_PREP_SCH_ID", 		ydPrepSchId					); 	// 야드준비스케쥴ID
					jrParam.setField("YD_SCH_CD", 			szYD_SCH_CD					); 	// 스케줄코드
					jrParam.setField("YD_PREP_WK_ST", 		"L"							); 	// 야드준비작업상태
					jrParam.setField("YD_AIM_BAY_GP", 		szYD_AIM_BAY_GP				); 	//목적동
					
					sstlNo = "";
					for (int ii = 1; ii <= mtlSh; ii++) {
						
						sstlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	// 재료번호

						//준비재료 등록
						jrParam.setField("SSTL_NO"			, sstlNo); 
						jrParam.setField("YD_GP"			, "G"); 
						jrParam.setField("YS_STK_COL_GP"	, ydEqpId); 
						jrParam.setField("YS_STK_BED_NO"	, "01"); 
						jrParam.setField("YS_STK_LYR_NO"	, "01"); 
						jrParam.setField("YS_STK_SEQ_NO"	, String.valueOf(ii)); 
						
						commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insPrepMtl", logId, methodNm, "준비재료 등록");
						
					}
					
					commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insPrepSchAimBay", logId, methodNm, "준비스케줄 등록");
					
					szMsg = "야드준비스케쥴ID [" + ydPrepSchId + "] 스케줄코드 [" + szYD_SCH_CD + "] 등록";
		      		commUtils.printLog(logId, szMsg, "******");
									
									
				}				
								
								
// 2025.09.10 야드To위치Guide 가 개소코드 이면  남4문 위치로 설정 
				if(  "S3Y21".equals(ydRcptPlnStrLocFirst)
				  || "BSY04".equals(ydRcptPlnStrLocFirst)) {
					// 대형 옥외 야드, 사외통합 야드  
					ydRcptPlnStrLoc = "GETR41";
					
				}
				else {
					ydRcptPlnStrLoc = ydRcptPlnStrLocFirst;
				}
				
				inGridData.addParam("SSTL_NOS", 		stlNos);			// 재료번호들
				inGridData.addParam("YS_STK_COL_GP", 	ydEqpId);			// 야드적치열구분(6자리 이상)
				inGridData.addParam("YD_TO_LOC_GUIDE", 	ydRcptPlnStrLoc);	// 야드To위치Guide
				inGridData.addParam("YD_WRK_CRN", 		"");				// 야드작업크레인(작업자지정 크레인)
				inGridData.addParam("userid", 			modifier);			// 수정자
				
// 2025.10.28 야드준비스케쥴ID(차량상차작업예약ID) 추가				
				inGridData.addParam("YD_PREP_SCH_ID", 	ydPrepSchId);		// 야드준비스케쥴ID(차량상차작업예약ID)
				
				szMsg = "\n\t SSTL_NO   		: " 	+ stlNos 
                	  + "\n\t YS_STK_COL_GP     : " 	+ ydEqpId 
                	  + "\n\t YD_TO_LOC_GUIDE   : " 	+ ydMvCd;

	      		commUtils.printLog(logId, szMsg, "");
				
				jrRtn = CbtYsJsp.updbtMvStkWrkBook(inGridData);
				
//				//예외처리 없을 경우만 호출한다.
//				/**********************************************************
//				* 3. 대형압연 빌렛/봉강 출하상 추출요구 처리
//				**********************************************************/
//				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
//
//				// 설비인출요구
//				jrYdMsg.setResultCode(logId);	//Log ID
//				jrYdMsg.setResultMsg(methodNm);	//Log Method Name
//				jrYdMsg.setField("JMS_TC_CD"         , "YSYSJ502"               ); // JMSTC코드
//				jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); // JMSTC생성일시
//				jrYdMsg.setField("YD_EQP_ID"         , ydEqpId                  ); // 야드설비ID
//				jrYdMsg.setField("YS_STK_BED_NO"     , insYsStkBedNo  			); // 야드적치Bed번호
//				jrYdMsg.setField("YS_STK_LYR_NO"     , "01"               		); // 야드적치단번호
//				jrYdMsg.setField("YD_SCH_ST_GP"      , "A"                      ); // 야드스케쥴기동구분(Auto)
//				jrYdMsg.setField("MODIFIER"        , modifier                 	); // 수정자
//				
//		        szMsg = jrYdMsg.toString();
//				commUtils.printLog(logId, szMsg, "");
//				
//				//전송할 전문에 추가
//				jrRtn = commUtils.addSndData(jrYdMsg);

				
				
			}

			commUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
			
		} catch (DAOException e) {
			//if("".equals(sExcptMsg)) {
			//	sExcptMsg = e.getMessage();
			//}
			throw e;
		} catch (Exception e) {
			//if("".equals(sExcptMsg)) {
			//	sExcptMsg = e.getMessage();
			//}
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
	 *      [A] 오퍼레이션명 : PRESS교정 추출요구(M4YSL211)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvM4YSL211(JDTORecord rcvMsg) throws DAOException {
		
        String szMsg	= "";
		String methodNm = "PRESS교정 추출요구[CbtYsL2RcvSeEJBBean.rcvM4YSL211] < " + rcvMsg.getResultMsg();
        String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);	

        JDTORecord jrParam = JDTORecordFactory.getInstance().create();
		JDTORecord inParam = JDTORecordFactory.getInstance().create();
		String sExcptMsg = "";
		String sIfData = "";
		String msgId = null;
		JDTORecordSet jsTemp = null;
		JDTORecord jrTemp = null;
		String szStkStlNo = null;

		JDTORecord jrRtn = null;
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

	        
	        szMsg = rcvMsg.toString();
			commUtils.printLog(logId, szMsg, "");
			
            /*
			7	YD_EQP_ID			야드설비ID 			CHAR	6
			8	YS_STK_BED_NO		특수강야드적치Bed번호	CHAR	2
			9	YD_STK_BED_STL_SH	야드적치Bed재료매수	NUMBER	2
			10	YD_EQP_WRK_SH 		야드설비작업매수		NUMBER	2
			11	SSTL_NO1			특수강재료번호1		CHAR	12
			12	SSTL_NO2			특수강재료번호2		CHAR	12
			13	SSTL_NO3			특수강재료번호3		CHAR	12
			14	SSTL_NO4			특수강재료번호4		CHAR	12
			15	SSTL_NO5			특수강재료번호5		CHAR	12
			16	SSTL_NO6			특수강재료번호6		CHAR	12
			17	SSTL_NO7			특수강재료번호7		CHAR	12
			18	SSTL_NO8			특수강재료번호8		CHAR	12
			19	SSTL_NO9			특수강재료번호9		CHAR	12
			20	SSTL_NO10			특수강재료번호10		CHAR	12
            */
			
			
			// 수신 항목 값
			msgId         			= commUtils.getMsgId(rcvMsg); 
			String ydEqpId       	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID")			); 	// 야드설비ID
			String ysStkBedNo    	= commUtils.trim(rcvMsg.getFieldString("YS_STK_BED_NO")		); 	// 야드적치Bed번호
			String ydStkBedStlSh 	= commUtils.trim(rcvMsg.getFieldString("YD_STK_BED_STL_SH")	); 	// 야드적치Bed재료매수
			String ydEqpWrkSh 		= commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_SH")		); 	// 야드설비작업매수
			String modifier      	= commUtils.trim(rcvMsg.getFieldString("MODIFIER")			); 	// 수정자(Backup Only)
			String L3Hmi         	= commUtils.nvl(rcvMsg.getFieldString("L3_HMI"),"N"			);  // 백업화면 기동 여부
			
			String stlNo 			= "";															// 재료번호
			String insYsStkBedNo	= "01";  														// 재료 적치 할 bed 

// 2025.12.08 프레스교정기추출대(GEPC01) - 예정저장위치 
			String ydToLocGuide = "";

// 2025.12.22 TO 위치 가이드
			String sApplyYnPI = commDao.ApplyYnPI(logId, methodNm, "APPNEW", "021", "*");
			if("Y".equals(sApplyYnPI)){
				
				szMsg = "TB_YS_RULE APPNEW 021 M4YSL211(PRESS교정 추출요구) TO 위치 가이드";
	      		commUtils.printLog(logId, szMsg, "");
				
	      		ydToLocGuide	= commUtils.trim(rcvMsg.getFieldString("TO_LOC_GUIDE")			); 	// TO위치가이드

				szMsg = "M4YSL211(PRESS교정 추출요구) TO 위치 가이드 [" + ydToLocGuide + "]";
	      		commUtils.printLog(logId, szMsg, "");
				
			}
			
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

            szMsg = "\n\t YD_EQP_ID   		: " 	+ ydEqpId 
              	  + "\n\t YS_STK_BED_NO     : " 	+ ysStkBedNo 
              	  + "\n\t YD_STK_BED_STL_SH : " 	+ ydStkBedStlSh
              	  + "\n\t YD_EQP_WRK_SH 	: " 	+ ydEqpWrkSh;

    		commUtils.printLog(logId, szMsg, "");
			
			
			int mtlSh = Integer.parseInt(ydEqpWrkSh); // 야드설비작업매수
			
			for (int ii = 1; ii <= mtlSh; ii++) {
				
				sIfData += commUtils.trim(rcvMsg.getFieldString("SSTL_NO"+ii));
				if(ii < mtlSh) {
					sIfData += ", ";
				}
				
			}
			
// 2025.11.26 적치열 매수 만큼만 트래킹 처리 
			if(8 < mtlSh) {
				mtlSh = 8;
			}
						
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6 || !ydEqpId.startsWith("GEPC03")) {
				sExcptMsg = "전문 이상! 설비ID(YD_EQP_ID) 이상 [" + ydEqpId + "]";
			} else if ("".equals(ysStkBedNo)) {
				sExcptMsg = "전문 이상! 적치Bed번호(YS_STK_BED_NO) 없음";
			} else if ("".equals(ydEqpWrkSh)) {
				sExcptMsg = "전문 이상! 야드설비작업매수(YD_EQP_WRK_SH) 없음";
			}

			if(!"".equals(sExcptMsg)) {
				throw new Exception(sExcptMsg);
			}
			

			JDTORecordSet jsChk = JDTORecordFactory.getInstance().createRecordSet("Temp");

			if(L3Hmi.equals("N")) {   // 전문이 오는 경우
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("YS_STK_COL_GP"      , ydEqpId   ); //야드적치열구분
				

				// 화면에서 백업시 야드 적치위치 등록 된 후에 온다
				/**********************************************************
				* 2.1 재료번호  CHECK : 저장위치 등록여부
				**********************************************************/
				
				for (int ii = 1; ii <= mtlSh; ii++) {
					stlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	// 재료번호
							
					if ("".equals(stlNo)) {
						sExcptMsg = "전문 이상! 재료번호(SSTL_NO" + ii + ") 없음";
						throw new Exception(sExcptMsg);
					}
					jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setResultCode(logId);	//Log ID
					jrParam.setResultMsg(methodNm);	//Log Method Name
					jrParam.setField("YS_STK_COL_GP"      , ydEqpId   ); 	// 야드적치열구분
					jrParam.setField("SSTL_NO"			  , stlNo     ); 	// 재료 번호
					jrParam.setField("YS_STK_BED_NO"      , "01"); 			// 야드적치Bed번호
					
					
					jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getSstlNoYardYN", logId, methodNm, "저장위치 등록여부");
		
					if (jsChk != null && jsChk.size() > 0) {
						
						jrTemp = jsChk.getRecord(0);
						
						sExcptMsg   = "이미 야드에 적치된 재료 "
							        + stlNo	
									+ " ["
									+ commUtils.trim(jrTemp.getFieldString("YS_STK_COL_GP")) 
									+ "-" 
						            + commUtils.trim(jrTemp.getFieldString("YS_STK_BED_NO"))
						            + "]";
						sApplyYnPI = commDao.ApplyYnPI(logId, methodNm, "APPNEW", "001", "*");
						
						if("Y".equals(sApplyYnPI)){
							
							szMsg = "TB_YS_RULE 001 이미 야드에 적치된 재료 적용여부 ";
				      		commUtils.printLog(logId, szMsg, "");
							
							return jrRtn;
							
						}
						else {
							throw new Exception(sExcptMsg);
						}
					}	
	
				}
			}
			
			
			/**********************************************************
			* 2.1  저장품 등록
			**********************************************************/
			jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER"           , modifier  ); //수정자			
			
			for (int ii = 1; ii <= mtlSh; ii++) {
				stlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	//재료번호

				jrParam.setField("SSTL_NO"	, stlNo     ); 
				jrParam.setField("CARRY_OUT", "Y"       ); 
				commDao.insert(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.insBtYdStock", logId, methodNm, "야드 저장품 등록"); 
				
				
				inParam = JDTORecordFactory.getInstance().create();
				inParam.setResultCode(logId);	//Log ID
				inParam.setResultMsg(methodNm);	//Log Method Name
				//추가, 이동
				inParam.setField("FNL_REG_PGM"			, "M4YSL211" );
				inParam.setField("YD_GP"				, "G" );
				inParam.setField("YD_BAY_GP"			, ydEqpId.substring(1,2) );
				inParam.setField("YD_EQP_GP"			, ydEqpId.substring(2,4) );
				inParam.setField("YS_STK_COL_NO"		, ydEqpId.substring(4,6) );
				inParam.setField("YS_STK_BED_NO"		, commUtils.trim(ysStkBedNo) );
				inParam.setField("YS_STK_LYR_NO"		, "01" );
				inParam.setField("YS_STK_SEQ_NO"		, ""+ii );
				inParam.setField("YS_STR_LOC"			, ydEqpId 
						                                + commUtils.trim(ysStkBedNo)
						                                + "01"
						                                + ""+ii) ;
				inParam.setField("SSTL_NO"				, stlNo  );
				
				/**********************************************************
				* 1.2공통 저장위치 Update (별도 Transaction 으로 처리)
				**********************************************************/
				EJBConnector tranConn = new EJBConnector("default", "BtYsL2RcvSeEJB", this);
				tranConn.trx("updBtCommYsStrLoc", new Class[] { JDTORecord.class }, new Object[] { inParam });
			}

			
			if(L3Hmi.equals("N")) {
			

				/**********************************************************
				* 2.1 적재단, 저장품 등록
				**********************************************************/

				for (int ii = 1; ii <= mtlSh; ii++) {
					stlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	//재료번호
							
					if ("".equals(stlNo)) {
						sExcptMsg = "전문 이상! 재료번호(SSTL_NO" + ii + ") 없음";
						throw new Exception(sExcptMsg);
					}
					jrParam = JDTORecordFactory.getInstance().create();
					
					
					
					jrParam.setResultCode(logId);							// Log ID
					jrParam.setResultMsg(methodNm);							// Log Method Name
					jrParam.setField("YS_STK_COL_GP"      , ydEqpId   ); 	// 야드적치열구분
					jrParam.setField("SSTL_NO"			  , stlNo     ); 	// 재료번호
					jrParam.setField("YS_STK_BED_NO"      , insYsStkBedNo); // 야드적치Bed번호
					jrParam.setField("YS_STK_LYR_NO"      , "01"); 			// 야드적치 단 -> 무조건 1단
					jrParam.setField("YS_STK_SEQ_NO", String.valueOf(ii)); 	// 야드적치SEQ
					jrParam.setField("YD_STK_LYR_MTL_STAT", "C"       ); 	// 야드적치단재료상태(적치중)
					jrParam.setField("MODIFIER"           , modifier  ); 	// 수정자
					
					// 재료번호 이전 저장위치 재료번호 CLEAR
					jrParam.setField("YD_STK_LYR_MTL_STAT", "E"       ); 	// 야드적치단재료상태(적치중)
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyrStlNoClear", logId, methodNm, "야드 적치단 재료번호 CLEAR"); 

					
					
					jrParam.setField("YD_STK_LYR_MTL_STAT", "C"       ); 	// 야드적치단재료상태(적치중)
					
					
					
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyr", logId, methodNm, "야드 적치단 등록"); 
					
				 
				}
			
			} else {
				insYsStkBedNo = ysStkBedNo;
			}			
			
			
			
			// STOCK 재료번호가 존재하지 않으면 작업예약을 만들지 않는다.
			for (int ii = 1; ii <= mtlSh; ii++) {
				
				stlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	//재료번호
				
				jrParam.setField("SSTL_NO"	, stlNo );
				
				//제품번호가 야드저장품에 존재하는지, 작업예약과 크레인스케줄에 잡혀있는 대상인지를 가져오고 BILLET공통에서 현재진도코드를 가져온다. 
				jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.getIsEnableStlNo", logId, methodNm, "재료번호 작업대상여부 조회");
				
				if (jsTemp != null && jsTemp.size() > 0) {
					
					jrTemp = jsTemp.getRecord(0);
	
					szStkStlNo		= commUtils.trim(jrTemp.getFieldString("SSTL_NO")); //Stock 에 존재하지 않으면 ""
			    } 			
				
				//저장품에 존재하는 제품번호인지 체크
				if("".equals(szStkStlNo)) {

					sExcptMsg = "전단실적 누락 " + stlNo;
					//throw new Exception(sExcptMsg);
					break;
				}
			}
			
			
			if("".equals(sExcptMsg)){
				
				// 작업예약등록
				// CbtYsJspSeEJBBean  	>> updbtMvStkWrkBook 
//				String stlNos = commUtils.trim(gdReq.getParam("SSTL_NOS")); // 재료번호들
//				String ysStkColGp = commUtils.trim(gdReq.getParam("YS_STK_COL_GP")); // 야드적치열구분(6자리 이상)
//				String ydToLocGuide = commUtils.trim(gdReq.getParam("YD_TO_LOC_GUIDE")); // 야드To위치Guide
//				String ydWrkCrn = commUtils.trim(gdReq.getParam("YD_WRK_CRN")); // 야드작업크레인(작업자지정 크레인)
				GridData inGridData = new GridData();

				String stlNos = "";
				for (int ii = 1; ii <= mtlSh; ii++) {
					
					stlNos += commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	// 재료번호
					if(ii < mtlSh) {
						stlNos += ",";
					}
					
				}
				
// 2025.12.08 프레스교정기추출대(GEPC01) - 예정저장위치 
				sApplyYnPI 	= commDao.ApplyYnPI(logId, methodNm, "APPNEW", "009", "*");
				if("Y".equals(sApplyYnPI) && "".equals(ydToLocGuide)){
					szMsg = "TB_YS_RULE 009 프레스교정기추출대(GEPC01) - 예정저장위치 ";
		      		commUtils.printLog(logId, szMsg, "");
					
// 2025.12.22 전문 TO위치가이드에 값이 없으면 TB_YS_RULE APPGI6 에 등록된 예정저장위치 사용
					szMsg = "전문  TO위치가이드에 값이 없음 ";
		      		commUtils.printLog(logId, szMsg, "");
					
// 2025.12.22 TB_YS_RULE APPGI6 프레스교정기추출대(GEPC01) - 예정저장위치 SELECT 부분 변경
					szMsg = "TB_YS_RULE APPNEW 009 프레스교정기추출대(GEPC01) - 예정저장위치 ";
		      		commUtils.printLog(logId, szMsg, "");
					
					ydToLocGuide = CarTSMv.ApplyRuleItem(logId, methodNm, "APPGI6", "*");

					szMsg = "TB_YS_RULE APPGI6 프레스교정기추출대(GEPC01) - 예정저장위치  [" + ydToLocGuide +"] ";
		      		commUtils.printLog(logId, szMsg, "");
					
//				    jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.selPrssPlnStrLoc", logId, methodNm, "프레스교정기추출대(GEPC01) - 예정저장위치");
//					if (jsChk.size() > 0) {
//			 			szMsg = "************ 프레스교정기추출대(GEPC01) - 예정저장위치";
//			       		commUtils.printLog(logId, szMsg, "******");
//						
//						// 빌렛 출하상 야드To위치Guide
//			       		ydToLocGuide = commUtils.trim(jsChk.getRecord(0).getFieldString("ITEM"   )); // 프레스교정기추출대(GEPC01) - 예정저장위치
//
//			 			szMsg = "************ 프레스교정기추출대(GEPC01) - 예정저장위치 [" + ydToLocGuide + "]";
//			       		commUtils.printLog(logId, szMsg, "******");
//					}					

				}
				
				inGridData.addParam("SSTL_NOS", 		stlNos);			// 재료번호들
				inGridData.addParam("YS_STK_COL_GP", 	ydEqpId);			// 야드적치열구분(6자리 이상)
				inGridData.addParam("YD_TO_LOC_GUIDE", 	ydToLocGuide);		// 야드To위치Guide
				inGridData.addParam("YD_WRK_CRN", 		"");				// 야드작업크레인(작업자지정 크레인)
				inGridData.addParam("userid", 			modifier);			// 수정자
				
				szMsg = "\n\t SSTL_NO   		: " 	+ stlNos 
                	  + "\n\t YS_STK_COL_GP     : " 	+ ydEqpId
          	  		  + "\n\t YD_TO_LOC_GUIDE   : " 	+ ydToLocGuide;

	      		commUtils.printLog(logId, szMsg, "");
				
				jrRtn = CbtYsJsp.updbtMvStkWrkBook(inGridData);
				
//				//예외처리 없을 경우만 호출한다.
//				/**********************************************************
//				* 3. 대형압연 빌렛/봉강 출하상 추출요구 처리
//				**********************************************************/
//				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
//
//				// 설비인출요구
//				jrYdMsg.setResultCode(logId);	//Log ID
//				jrYdMsg.setResultMsg(methodNm);	//Log Method Name
//				jrYdMsg.setField("JMS_TC_CD"         , "YSYSJ502"               ); // JMSTC코드
//				jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); // JMSTC생성일시
//				jrYdMsg.setField("YD_EQP_ID"         , ydEqpId                  ); // 야드설비ID
//				jrYdMsg.setField("YS_STK_BED_NO"     , insYsStkBedNo  			); // 야드적치Bed번호
//				jrYdMsg.setField("YS_STK_LYR_NO"     , "01"               		); // 야드적치단번호
//				jrYdMsg.setField("YD_SCH_ST_GP"      , "A"                      ); // 야드스케쥴기동구분(Auto)
//				jrYdMsg.setField("MODIFIER"        , modifier                 	); // 수정자
//				
//		        szMsg = jrYdMsg.toString();
//				commUtils.printLog(logId, szMsg, "");
//				
//				//전송할 전문에 추가
//				jrRtn = commUtils.addSndData(jrYdMsg);
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch (DAOException e) {
			//if("".equals(sExcptMsg)) {
			//	sExcptMsg = e.getMessage();
			//}
			throw e;
		} catch (Exception e) {
			//if("".equals(sExcptMsg)) {
			//	sExcptMsg = e.getMessage();
			//}
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
	 *      [A] 오퍼레이션명 : PRESS교정 Reject요구(M4YSL212)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvM4YSL212(JDTORecord rcvMsg) throws DAOException {
		
        String szMsg	= "";
		String methodNm = "PRESS교정 추출요구[CbtYsL2RcvSeEJBBean.rcvM4YSL212] < " + rcvMsg.getResultMsg();
        String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);	

        JDTORecord jrParam = JDTORecordFactory.getInstance().create();
		JDTORecord inParam = JDTORecordFactory.getInstance().create();
		String sExcptMsg = "";
		String sIfData = "";
		String msgId = null;
		JDTORecordSet jsTemp = null;
		JDTORecord jrTemp = null;
		String szStkStlNo = null;

		JDTORecord jrRtn = null;
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

	        
	        szMsg = rcvMsg.toString();
			commUtils.printLog(logId, szMsg, "");
			
            /*
			7	YD_EQP_ID			야드설비ID 			CHAR	6
			8	YS_STK_BED_NO		특수강야드적치Bed번호	CHAR	2
			9	YD_STK_BED_STL_SH	야드적치Bed재료매수	NUMBER	2
			10	YD_EQP_WRK_SH 		야드설비작업매수		NUMBER	2
			11	SSTL_NO1			특수강재료번호1		CHAR	12
			12	SSTL_NO2			특수강재료번호2		CHAR	12
			13	SSTL_NO3			특수강재료번호3		CHAR	12
			14	SSTL_NO4			특수강재료번호4		CHAR	12
			15	SSTL_NO5			특수강재료번호5		CHAR	12
			16	SSTL_NO6			특수강재료번호6		CHAR	12
			17	SSTL_NO7			특수강재료번호7		CHAR	12
			18	SSTL_NO8			특수강재료번호8		CHAR	12
			19	SSTL_NO9			특수강재료번호9		CHAR	12
			20	SSTL_NO10			특수강재료번호10		CHAR	12
            */
			
			
			// 수신 항목 값
			msgId         			= commUtils.getMsgId(rcvMsg); 
			String ydEqpId       	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"        	)); 	// 야드설비ID
			String ysStkBedNo    	= commUtils.trim(rcvMsg.getFieldString("YS_STK_BED_NO"    	)); 	// 야드적치Bed번호
			String ydStkBedStlSh 	= commUtils.trim(rcvMsg.getFieldString("YD_STK_BED_STL_SH"	)); 	// 야드적치Bed재료매수
			String ydEqpWrkSh 		= commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_SH"		)); 	// 야드설비작업매수
			String modifier      	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"       	)); 	// 수정자(Backup Only)
			String L3Hmi         	= commUtils.nvl(rcvMsg.getFieldString("L3_HMI"),"N");         		// 백업화면 기동 여부
			
			String stlNo 			= "";															// 재료번호
			String insYsStkBedNo	= "01";  														// 재료 적치 할 bed 

// 2025.12.22 PRESS교정 Reject요구(GETF02) - TO 위치 가이드 
			String ydToLocGuide = "";
			String sApplyYnPI = commDao.ApplyYnPI(logId, methodNm, "APPNEW", "022", "*");
			if("Y".equals(sApplyYnPI)){
				
				szMsg = "TB_YS_RULE APPNEW 022 M4YSL212(PRESS교정 Reject요구) TO 위치 가이드";
	      		commUtils.printLog(logId, szMsg, "");
				
	      		ydToLocGuide	= commUtils.trim(rcvMsg.getFieldString("TO_LOC_GUIDE")			); 	// TO위치가이드

				szMsg = "M4YSL212(PRESS교정 Reject요구) TO 위치 가이드 [" + ydToLocGuide + "]";
	      		commUtils.printLog(logId, szMsg, "");
				
			}
			
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

            szMsg = "\n\t YD_EQP_ID   		: " 	+ ydEqpId 
              	  + "\n\t YS_STK_BED_NO     : " 	+ ysStkBedNo 
              	  + "\n\t YD_STK_BED_STL_SH : " 	+ ydStkBedStlSh
        	  	  + "\n\t YD_EQP_WRK_SH 	: " 	+ ydEqpWrkSh;

    		commUtils.printLog(logId, szMsg, "");
			
			
			int mtlSh = Integer.parseInt(ydEqpWrkSh); // 야드설비작업매수
			
			for (int ii = 1; ii <= mtlSh; ii++) {
				
				sIfData += commUtils.trim(rcvMsg.getFieldString("SSTL_NO"+ii));
				if(ii < mtlSh) {
					sIfData += ", ";
				}
				
			}
			
// 2025.11.26 적치열 매수 만큼만 트래킹 처리 
			if(8 < mtlSh) {
				mtlSh = 8;
			}

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6 || !ydEqpId.startsWith("GE")) {
				sExcptMsg = "전문 이상! 설비ID(YD_EQP_ID) 이상 [" + ydEqpId + "]";
			} else if ("".equals(ysStkBedNo)) {
				sExcptMsg = "전문 이상! 적치Bed번호(YS_STK_BED_NO) 없음";
			} else if ("".equals(ydEqpWrkSh)) {
				sExcptMsg = "전문 이상! 야드설비작업매수(YD_EQP_WRK_SH) 없음";
			}

			if(!"".equals(sExcptMsg)) {
				throw new Exception(sExcptMsg);
			}
			

			JDTORecordSet jsChk = JDTORecordFactory.getInstance().createRecordSet("Temp");

			if(L3Hmi.equals("N")) {   // 전문이 오는 경우
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("YS_STK_COL_GP"      , ydEqpId   ); //야드적치열구분
				

				// 화면에서 백업시 야드 적치위치 등록 된 후에 온다
				/**********************************************************
				* 2.1 재료번호  CHECK : 저장위치 등록여부
				**********************************************************/
				
				for (int ii = 1; ii <= mtlSh; ii++) {
					stlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	// 재료번호
							
					if ("".equals(stlNo)) {
						sExcptMsg = "전문 이상! 재료번호(SSTL_NO" + ii + ") 없음";
						throw new Exception(sExcptMsg);
					}
					jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setResultCode(logId);	//Log ID
					jrParam.setResultMsg(methodNm);	//Log Method Name
					jrParam.setField("YS_STK_COL_GP"      , ydEqpId   ); 	// 야드적치열구분
					jrParam.setField("SSTL_NO"			  , stlNo     ); 	// 재료 번호
					jrParam.setField("YS_STK_BED_NO"      , "01"); 			// 야드적치Bed번호
					
					
					jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getSstlNoYardYN", logId, methodNm, "저장위치 등록여부");
		
					if (jsChk != null && jsChk.size() > 0) {
						
						jrTemp = jsChk.getRecord(0);
						
						sExcptMsg   = "이미 야드에 적치된 재료 "
							        + stlNo	
									+ " ["
									+ commUtils.trim(jrTemp.getFieldString("YS_STK_COL_GP")) 
									+ "-" 
						            + commUtils.trim(jrTemp.getFieldString("YS_STK_BED_NO"))
						            + "]";
						sApplyYnPI = commDao.ApplyYnPI(logId, methodNm, "APPNEW", "001", "*");
						
						if("Y".equals(sApplyYnPI)){
							
							szMsg = "TB_YS_RULE 001 이미 야드에 적치된 재료 적용여부 ";
				      		commUtils.printLog(logId, szMsg, "");
							
							return jrRtn;
							
						}
						else {
							throw new Exception(sExcptMsg);
						}
					}	
	
				}
			}
			
			
			/**********************************************************
			* 2.1  저장품 등록
			**********************************************************/
			jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER"           , modifier  ); //수정자			
			
			for (int ii = 1; ii <= mtlSh; ii++) {
				stlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	//재료번호

				jrParam.setField("SSTL_NO"	, stlNo     ); 
				jrParam.setField("CARRY_OUT", "Y"       ); 
				commDao.insert(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.insBtYdStock", logId, methodNm, "야드 저장품 등록"); 
				
				
				inParam = JDTORecordFactory.getInstance().create();
				inParam.setResultCode(logId);	//Log ID
				inParam.setResultMsg(methodNm);	//Log Method Name
				//추가, 이동
				inParam.setField("FNL_REG_PGM"			, "M4YSL212" );
				inParam.setField("YD_GP"				, "G" );
				inParam.setField("YD_BAY_GP"			, ydEqpId.substring(1,2) );
				inParam.setField("YD_EQP_GP"			, ydEqpId.substring(2,4) );
				inParam.setField("YS_STK_COL_NO"		, ydEqpId.substring(4,6) );
				inParam.setField("YS_STK_BED_NO"		, commUtils.trim(ysStkBedNo) );
				inParam.setField("YS_STK_LYR_NO"		, "01" );
				inParam.setField("YS_STK_SEQ_NO"		, ""+ii );
				inParam.setField("YS_STR_LOC"			, ydEqpId 
						                                + commUtils.trim(ysStkBedNo)
						                                + "01"
						                                + ""+ii) ;
				inParam.setField("SSTL_NO"				, stlNo  );
				
				/**********************************************************
				* 1.2공통 저장위치 Update (별도 Transaction 으로 처리)
				**********************************************************/
				EJBConnector tranConn = new EJBConnector("default", "BtYsL2RcvSeEJB", this);
				tranConn.trx("updBtCommYsStrLoc", new Class[] { JDTORecord.class }, new Object[] { inParam });
			}

			
			if(L3Hmi.equals("N")) {
			

				/**********************************************************
				* 2.1 적재단, 저장품 등록
				**********************************************************/

				for (int ii = 1; ii <= mtlSh; ii++) {
					stlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	//재료번호
							
					if ("".equals(stlNo)) {
						sExcptMsg = "전문 이상! 재료번호(SSTL_NO" + ii + ") 없음";
						throw new Exception(sExcptMsg);
					}
					jrParam = JDTORecordFactory.getInstance().create();
					
					
					
					jrParam.setResultCode(logId);							// Log ID
					jrParam.setResultMsg(methodNm);							// Log Method Name
					jrParam.setField("YS_STK_COL_GP"      , ydEqpId   ); 	// 야드적치열구분
					jrParam.setField("SSTL_NO"			  , stlNo     ); 	// 재료 번호
					jrParam.setField("YS_STK_BED_NO"      , insYsStkBedNo); // 야드적치Bed번호
					jrParam.setField("YS_STK_LYR_NO"      , "01"); 			// 야드적치 단 -> 무조건 1단
					jrParam.setField("YS_STK_SEQ_NO", String.valueOf(ii)); 	// 야드적치SEQ
					jrParam.setField("YD_STK_LYR_MTL_STAT", "C"       ); 	// 야드적치단재료상태(적치중)
					jrParam.setField("MODIFIER"           , modifier  ); 	// 수정자
					
					// 재료번호 이전 저장위치 재료번호 CLEAR
					jrParam.setField("YD_STK_LYR_MTL_STAT", "E"       ); 	// 야드적치단재료상태(적치중)
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyrStlNoClear", logId, methodNm, "야드 적치단 재료번호 CLEAR"); 

					
					
					jrParam.setField("YD_STK_LYR_MTL_STAT", "C"       ); 	// 야드적치단재료상태(적치중)
					
					
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyr", logId, methodNm, "야드 적치단 등록"); 
					
				 
				}
			
			} else {
				insYsStkBedNo = ysStkBedNo;
			}			
			
			
			
			// STOCK 재료번호가 존재하지 않으면 작업예약을 만들지 않는다.
			for (int ii = 1; ii <= mtlSh; ii++) {
				
				stlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	//재료번호
				
				jrParam.setField("SSTL_NO"	, stlNo );
				
				//제품번호가 야드저장품에 존재하는지, 작업예약과 크레인스케줄에 잡혀있는 대상인지를 가져오고 BILLET공통에서 현재진도코드를 가져온다. 
				jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.getIsEnableStlNo", logId, methodNm, "제품번호 작업대상여부 조회");
				
				if (jsTemp != null && jsTemp.size() > 0) {
					
					jrTemp = jsTemp.getRecord(0);
	
					szStkStlNo		= commUtils.trim(jrTemp.getFieldString("SSTL_NO")); //Stock 에 존재하지 않으면 ""
			    } 			
				
				//저장품에 존재하는 제품번호인지 체크
				if("".equals(szStkStlNo)) {

					sExcptMsg = "전단실적 누락 " + stlNo;
					//throw new Exception(sExcptMsg);
					break;
				}
			}
			
			if("".equals(sExcptMsg)){
				
				// 작업예약등록
				// CbtYsJspSeEJBBean  	>> updbtMvStkWrkBook 
//				String stlNos = commUtils.trim(gdReq.getParam("SSTL_NOS")); // 재료번호들
//				String ysStkColGp = commUtils.trim(gdReq.getParam("YS_STK_COL_GP")); // 야드적치열구분(6자리 이상)
//				String ydToLocGuide = commUtils.trim(gdReq.getParam("YD_TO_LOC_GUIDE")); // 야드To위치Guide
//				String ydWrkCrn = commUtils.trim(gdReq.getParam("YD_WRK_CRN")); // 야드작업크레인(작업자지정 크레인)
				GridData inGridData = new GridData();

				String stlNos = "";
				for (int ii = 1; ii <= mtlSh; ii++) {
					
					stlNos += commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	// 재료번호
					if(ii < mtlSh) {
						stlNos += ",";
					}
					
				}
				
				inGridData.addParam("SSTL_NOS", 		stlNos			);		// 재료번호들
				inGridData.addParam("YS_STK_COL_GP", 	ydEqpId			);		// 야드적치열구분(6자리 이상)
				inGridData.addParam("YD_TO_LOC_GUIDE", 	ydToLocGuide	);		// 야드To위치Guide
				inGridData.addParam("YD_WRK_CRN", 		""				);		// 야드작업크레인(작업자지정 크레인)
				inGridData.addParam("userid", 			modifier		);		// 수정자
				
				szMsg = "\n\t SSTL_NO   		: " 	+ stlNos 
                	  + "\n\t YS_STK_COL_GP     : " 	+ ydEqpId
    	  		      + "\n\t YD_TO_LOC_GUIDE   : " 	+ ydToLocGuide;

	      		commUtils.printLog(logId, szMsg, "");
				
				jrRtn = CbtYsJsp.updbtMvStkWrkBook(inGridData);
				
//				//예외처리 없을 경우만 호출한다.
//				/**********************************************************
//				* 3. 대형압연 빌렛/봉강 출하상 추출요구 처리
//				**********************************************************/
//				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
//
//				// 설비인출요구
//				jrYdMsg.setResultCode(logId);	//Log ID
//				jrYdMsg.setResultMsg(methodNm);	//Log Method Name
//				jrYdMsg.setField("JMS_TC_CD"         , "YSYSJ502"               ); // JMSTC코드
//				jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); // JMSTC생성일시
//				jrYdMsg.setField("YD_EQP_ID"         , ydEqpId                  ); // 야드설비ID
//				jrYdMsg.setField("YS_STK_BED_NO"     , insYsStkBedNo  			); // 야드적치Bed번호
//				jrYdMsg.setField("YS_STK_LYR_NO"     , "01"               		); // 야드적치단번호
//				jrYdMsg.setField("YD_SCH_ST_GP"      , "A"                      ); // 야드스케쥴기동구분(Auto)
//				jrYdMsg.setField("MODIFIER"        , modifier                 	); // 수정자
//				
//		        szMsg = jrYdMsg.toString();
//				commUtils.printLog(logId, szMsg, "");
//				
//				//전송할 전문에 추가
//				jrRtn = commUtils.addSndData(jrYdMsg);
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch (DAOException e) {
			//if("".equals(sExcptMsg)) {
			//	sExcptMsg = e.getMessage();
			//}
			throw e;
		} catch (Exception e) {
			//if("".equals(sExcptMsg)) {
			//	sExcptMsg = e.getMessage();
			//}
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
	 *      [A] 오퍼레이션명 : ShotBlast 장입대 Reject 요구(M4YSL213)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvM4YSL213(JDTORecord rcvMsg) throws DAOException {
		
        String szMsg	= "";
		String methodNm = "ShotBlast 장입대 Reject 요구[CbtYsL2RcvSeEJBBean.rcvM4YSL213] < " + rcvMsg.getResultMsg();
        String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);	

        JDTORecord jrParam = JDTORecordFactory.getInstance().create();
		JDTORecord inParam = JDTORecordFactory.getInstance().create();
		String sExcptMsg = "";
		String sIfData = "";
		String msgId = null;
		JDTORecordSet jsTemp = null;
		JDTORecord jrTemp = null;
		String szStkStlNo = null;

		JDTORecord jrRtn = null;
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

	        
	        szMsg = rcvMsg.toString();
			commUtils.printLog(logId, szMsg, "");
			
            /*
			7	YD_EQP_ID			야드설비ID 			CHAR	6
			8	YS_STK_BED_NO		특수강야드적치Bed번호	CHAR	2
			9	YD_STK_BED_STL_SH	야드적치Bed재료매수	NUMBER	2
			10	YD_EQP_WRK_SH 		야드설비작업매수		NUMBER	2
			11	SSTL_NO1			특수강재료번호1		CHAR	12
			12	SSTL_NO2			특수강재료번호2		CHAR	12
			13	SSTL_NO3			특수강재료번호3		CHAR	12
			14	SSTL_NO4			특수강재료번호4		CHAR	12
			15	SSTL_NO5			특수강재료번호5		CHAR	12
			16	SSTL_NO6			특수강재료번호6		CHAR	12
			17	SSTL_NO7			특수강재료번호7		CHAR	12
			18	SSTL_NO8			특수강재료번호8		CHAR	12
			19	SSTL_NO9			특수강재료번호9		CHAR	12
			20	SSTL_NO10			특수강재료번호10		CHAR	12
            */
			
			
			// 수신 항목 값
			msgId         			= commUtils.getMsgId(rcvMsg); 
			String ydEqpId       	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID")			);	// 야드설비ID
			String ysStkBedNo    	= commUtils.trim(rcvMsg.getFieldString("YS_STK_BED_NO")		);	// 야드적치Bed번호
			String ydStkBedStlSh 	= commUtils.trim(rcvMsg.getFieldString("YD_STK_BED_STL_SH")	);	// 야드적치Bed재료매수
			String ydEqpWrkSh 		= commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_SH")		);	// 야드설비작업매수
			String modifier      	= commUtils.trim(rcvMsg.getFieldString("MODIFIER")			);	// 수정자(Backup Only)
			String L3Hmi         	= commUtils.nvl(rcvMsg.getFieldString("L3_HMI"),"N"			);	// 백업화면 기동 여부
			
			String stlNo 			= "";															// 재료번호
			String insYsStkBedNo	= "01";  														// 재료 적치 할 bed 
			
// 2025.12.22 M4YSL213(ShotBlast 장입대 Reject 요구) - TO위치가이드 
			String ydToLocGuide = "";
			String sApplyYnPI = commDao.ApplyYnPI(logId, methodNm, "APPNEW", "023", "*");
			if("Y".equals(sApplyYnPI)){
				
				szMsg = "TB_YS_RULE APPNEW 023 M4YSL213(ShotBlast 장입대 Reject 요구) TO위치 가이드";
	      		commUtils.printLog(logId, szMsg, "");
				
	      		ydToLocGuide	= commUtils.trim(rcvMsg.getFieldString("TO_LOC_GUIDE")			); 	// TO위치가이드

				szMsg = "M4YSL213(ShotBlast 장입대 Reject 요구) TO 위치 가이드 [" + ydToLocGuide + "]";
	      		commUtils.printLog(logId, szMsg, "");
				
			}
			
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

            szMsg = "\n\t YD_EQP_ID   		: " 	+ ydEqpId 
              	  + "\n\t YS_STK_BED_NO     : " 	+ ysStkBedNo 
              	  + "\n\t YD_STK_BED_STL_SH : " 	+ ydStkBedStlSh
              	  + "\n\t YD_EQP_WRK_SH 	: " 	+ ydEqpWrkSh;

    		commUtils.printLog(logId, szMsg, "");
			

    		int mtlSh = Integer.parseInt(ydEqpWrkSh); // 야드설비작업매수
			
			for (int ii = 1; ii <= mtlSh; ii++) {
				
				sIfData += commUtils.trim(rcvMsg.getFieldString("SSTL_NO"+ii));
				if(ii < mtlSh) {
					sIfData += ", ";
				}
				
			}
			
// 2025.11.26 적치열 매수 만큼만 트래킹 처리 
			if(8 < mtlSh) {
				mtlSh = 8;
			}
						
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6 || !ydEqpId.startsWith("GE")) {
				sExcptMsg = "전문 이상! 설비ID(YD_EQP_ID) 이상 [" + ydEqpId + "]";
			} else if ("".equals(ysStkBedNo)) {
				sExcptMsg = "전문 이상! 적치Bed번호(YS_STK_BED_NO) 없음";
			} else if ("".equals(ydEqpWrkSh)) {
				sExcptMsg = "전문 이상! 야드설비작업매수(YD_EQP_WRK_SH) 없음";
			}

			if(!"".equals(sExcptMsg)) {
				throw new Exception(sExcptMsg);
			}
			

			JDTORecordSet jsChk = JDTORecordFactory.getInstance().createRecordSet("Temp");

			if(L3Hmi.equals("N")) {   // 전문이 오는 경우
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("YS_STK_COL_GP"      , ydEqpId   ); //야드적치열구분
				

				// 화면에서 백업시 야드 적치위치 등록 된 후에 온다
				/**********************************************************
				* 2.1 재료번호  CHECK : 저장위치 등록여부
				**********************************************************/
				
				for (int ii = 1; ii <= mtlSh; ii++) {
					stlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	// 재료번호
							
					if ("".equals(stlNo)) {
						sExcptMsg = "전문 이상! 재료번호(SSTL_NO" + ii + ") 없음";
						throw new Exception(sExcptMsg);
					}
					jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setResultCode(logId);	//Log ID
					jrParam.setResultMsg(methodNm);	//Log Method Name
					jrParam.setField("YS_STK_COL_GP"      , ydEqpId   ); 	// 야드적치열구분
					jrParam.setField("SSTL_NO"			  , stlNo     ); 	// 재료 번호
					jrParam.setField("YS_STK_BED_NO"      , "01"); 			// 야드적치Bed번호
					
					jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getSstlNoYardYN", logId, methodNm, "저장위치 등록여부");
		
					if (jsChk != null && jsChk.size() > 0) {
						
						jrTemp = jsChk.getRecord(0);
						
						sExcptMsg   = "이미 야드에 적치된 재료 "
							        + stlNo	
									+ " ["
									+ commUtils.trim(jrTemp.getFieldString("YS_STK_COL_GP")) 
									+ "-" 
						            + commUtils.trim(jrTemp.getFieldString("YS_STK_BED_NO"))
						            + "]";
						sApplyYnPI = commDao.ApplyYnPI(logId, methodNm, "APPNEW", "001", "*");
						
						if("Y".equals(sApplyYnPI)){
							
							szMsg = "TB_YS_RULE 001 이미 야드에 적치된 재료 적용여부 ";
				      		commUtils.printLog(logId, szMsg, "");
							
							return jrRtn;
							
						}
						else {
							throw new Exception(sExcptMsg);
						}
					}	
	
				}
			}
			
			
			/**********************************************************
			* 2.1  저장품 등록
			**********************************************************/
			jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER"           , modifier  ); //수정자			
			
			for (int ii = 1; ii <= mtlSh; ii++) {
				stlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	//재료번호

				jrParam.setField("SSTL_NO"	, stlNo     ); 
				jrParam.setField("CARRY_OUT", "Y"       ); 
				commDao.insert(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.insBtYdStock", logId, methodNm, "야드 저장품 등록"); 
				
				
				inParam = JDTORecordFactory.getInstance().create();
				inParam.setResultCode(logId);	//Log ID
				inParam.setResultMsg(methodNm);	//Log Method Name
				//추가, 이동
				inParam.setField("FNL_REG_PGM"			, "M4YSL213" );
				inParam.setField("YD_GP"				, "G" );
				inParam.setField("YD_BAY_GP"			, ydEqpId.substring(1,2) );
				inParam.setField("YD_EQP_GP"			, ydEqpId.substring(2,4) );
				inParam.setField("YS_STK_COL_NO"		, ydEqpId.substring(4,6) );
				inParam.setField("YS_STK_BED_NO"		, commUtils.trim(ysStkBedNo) );
				inParam.setField("YS_STK_LYR_NO"		, "01" );
				inParam.setField("YS_STK_SEQ_NO"		, ""+ii );
				inParam.setField("YS_STR_LOC"			, ydEqpId 
						                                + commUtils.trim(ysStkBedNo)
						                                + "01"
						                                + ""+ii) ;
				inParam.setField("SSTL_NO"				, stlNo  );
				
				/**********************************************************
				* 1.2공통 저장위치 Update (별도 Transaction 으로 처리)
				**********************************************************/
				EJBConnector tranConn = new EJBConnector("default", "BtYsL2RcvSeEJB", this);
				tranConn.trx("updBtCommYsStrLoc", new Class[] { JDTORecord.class }, new Object[] { inParam });
			}

			
			if(L3Hmi.equals("N")) {
			

				/**********************************************************
				* 2.1 적재단, 저장품 등록
				**********************************************************/

				for (int ii = 1; ii <= mtlSh; ii++) {
					stlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	//재료번호
							
					if ("".equals(stlNo)) {
						sExcptMsg = "전문 이상! 재료번호(SSTL_NO" + ii + ") 없음";
						throw new Exception(sExcptMsg);
					}
					jrParam = JDTORecordFactory.getInstance().create();
					
					
					
					jrParam.setResultCode(logId);							// Log ID
					jrParam.setResultMsg(methodNm);							// Log Method Name
					jrParam.setField("YS_STK_COL_GP"      , ydEqpId   ); 	// 야드적치열구분
					jrParam.setField("SSTL_NO"			  , stlNo     ); 	// 재료번호
					jrParam.setField("YS_STK_BED_NO"      , insYsStkBedNo); // 야드적치Bed번호
					jrParam.setField("YS_STK_LYR_NO"      , "01"); 			// 야드적치 단 -> 무조건 1단
					jrParam.setField("YS_STK_SEQ_NO", String.valueOf(ii)); 	// 야드적치SEQ
					jrParam.setField("YD_STK_LYR_MTL_STAT", "C"       ); 	// 야드적치단재료상태(적치중)
					jrParam.setField("MODIFIER"           , modifier  ); 	// 수정자
					
					// 재료번호 이전 저장위치 재료번호 CLEAR
					jrParam.setField("YD_STK_LYR_MTL_STAT", "E"       ); 	// 야드적치단재료상태(적치중)
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyrStlNoClear", logId, methodNm, "야드 적치단 재료번호 CLEAR"); 

					
					
					jrParam.setField("YD_STK_LYR_MTL_STAT", "C"       ); 	// 야드적치단재료상태(적치중)
					
					
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyr", logId, methodNm, "야드 적치단 등록"); 
					
				 
				}
			
			} else {
				insYsStkBedNo = ysStkBedNo;
			}			
			
			
			
			// STOCK 재료번호가 존재하지 않으면 작업예약을 만들지 않는다.
			for (int ii = 1; ii <= mtlSh; ii++) {
				
				stlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	//재료번호
				
				jrParam.setField("SSTL_NO"	, stlNo );
				
				//제품번호가 야드저장품에 존재하는지, 작업예약과 크레인스케줄에 잡혀있는 대상인지를 가져오고 BILLET공통에서 현재진도코드를 가져온다. 
				jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.getIsEnableStlNo", logId, methodNm, "제품번호 작업대상여부 조회");
				
				if (jsTemp != null && jsTemp.size() > 0) {
					
					jrTemp = jsTemp.getRecord(0);
	
					szStkStlNo		= commUtils.trim(jrTemp.getFieldString("SSTL_NO")); //Stock 에 존재하지 않으면 ""
			    } 			
				
				//저장품에 존재하는 제품번호인지 체크
				if("".equals(szStkStlNo)) {

					sExcptMsg = "전단실적 누락 " + stlNo;
					//throw new Exception(sExcptMsg);
					break;
				}
			}
			
			if("".equals(sExcptMsg)){
				
				// 작업예약등록
				// CbtYsJspSeEJBBean  	>> updbtMvStkWrkBook 
//				String stlNos = commUtils.trim(gdReq.getParam("SSTL_NOS")); // 재료번호들
//				String ysStkColGp = commUtils.trim(gdReq.getParam("YS_STK_COL_GP")); // 야드적치열구분(6자리 이상)
//				String ydToLocGuide = commUtils.trim(gdReq.getParam("YD_TO_LOC_GUIDE")); // 야드To위치Guide
//				String ydWrkCrn = commUtils.trim(gdReq.getParam("YD_WRK_CRN")); // 야드작업크레인(작업자지정 크레인)
				GridData inGridData = new GridData();

				String stlNos = "";
				for (int ii = 1; ii <= mtlSh; ii++) {
					
					stlNos += commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	// 재료번호
					if(ii < mtlSh) {
						stlNos += ",";
					}
					
				}
				
				inGridData.addParam("SSTL_NOS", 		stlNos			);	// 재료번호들
				inGridData.addParam("YS_STK_COL_GP", 	ydEqpId			);	// 야드적치열구분(6자리 이상)
				inGridData.addParam("YD_TO_LOC_GUIDE", 	ydToLocGuide	);	// 야드To위치Guide
				inGridData.addParam("YD_WRK_CRN", 		""				);	// 야드작업크레인(작업자지정 크레인)
				inGridData.addParam("userid", 			modifier		);	// 수정자
				
				szMsg = "\n\t SSTL_NO   		: " 	+ stlNos 
                	  + "\n\t YS_STK_COL_GP     : " 	+ ydEqpId
                	  + "\n\t YD_TO_LOC_GUIDE   : " 	+ ydToLocGuide;

	      		commUtils.printLog(logId, szMsg, "");
				
				jrRtn = CbtYsJsp.updbtMvStkWrkBook(inGridData);
				
//				//예외처리 없을 경우만 호출한다.
//				/**********************************************************
//				* 3. 대형압연 빌렛/봉강 출하상 추출요구 처리
//				**********************************************************/
//				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
//
//				// 설비인출요구
//				jrYdMsg.setResultCode(logId);	//Log ID
//				jrYdMsg.setResultMsg(methodNm);	//Log Method Name
//				jrYdMsg.setField("JMS_TC_CD"         , "YSYSJ502"               ); // JMSTC코드
//				jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); // JMSTC생성일시
//				jrYdMsg.setField("YD_EQP_ID"         , ydEqpId                  ); // 야드설비ID
//				jrYdMsg.setField("YS_STK_BED_NO"     , insYsStkBedNo  			); // 야드적치Bed번호
//				jrYdMsg.setField("YS_STK_LYR_NO"     , "01"               		); // 야드적치단번호
//				jrYdMsg.setField("YD_SCH_ST_GP"      , "A"                      ); // 야드스케쥴기동구분(Auto)
//				jrYdMsg.setField("MODIFIER"        , modifier                 	); // 수정자
//				
//		        szMsg = jrYdMsg.toString();
//				commUtils.printLog(logId, szMsg, "");
//				
//				//전송할 전문에 추가
//				jrRtn = commUtils.addSndData(jrYdMsg);
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch (DAOException e) {
			//if("".equals(sExcptMsg)) {
			//	sExcptMsg = e.getMessage();
			//}
			throw e;
		} catch (Exception e) {
			//if("".equals(sExcptMsg)) {
			//	sExcptMsg = e.getMessage();
			//}
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
	 *      [A] 오퍼레이션명 : 대형압연설비 Tracking 정보(M3YSL220)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvM3YSL220(JDTORecord rcvMsg) throws DAOException {
		
        String szMsg	= "";
		String methodNm = "대형압연설비 Tracking 정보[CbtYsL2RcvSeEJBBean.rcvM3YSL220] < " + rcvMsg.getResultMsg();
        String logId 	= commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);	

        JDTORecord jrParam 		= JDTORecordFactory.getInstance().create();
		String sExcptMsg 		= "";
		String sIfData 			= "";
		String msgId 			= null;
		JDTORecord jrTemp 		= null;
		JDTORecord recPara = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, methodNm, "S+");

	        
	        szMsg = rcvMsg.toString();
			commUtils.printLog(logId, szMsg, "");
			
            /*
			7	YD_EQP_ID			야드설비ID 			CHAR	6
			8	YD_STK_BED_STL_SH	야드적치Bed재료매수	NUMBER	2
			09	SSTL_NO1			특수강재료번호1		CHAR	12
			
			...
			
			64	SSTL_NO56			특수강재료번호56	CHAR	12
            */
			
			
			//수신 항목 값
			msgId         			= commUtils.getMsgId(rcvMsg); 
			String ydEqpId       	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"        )); 	// 야드설비ID
			String ysStkBedNo    	= "01"; 														// 야드적치Bed번호
			String ydStkBedStlSh 	= commUtils.trim(rcvMsg.getFieldString("YD_STK_BED_STL_SH")); 	// 야드적치Bed재료매수
			String modifier      	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"       )); 	// 수정자(Backup Only)
			String ysStkLyrNo    	= "01"; 														// 야드적치단번호
			
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;


            szMsg = "\n\t YD_EQP_ID   		: " 	+ ydEqpId 
              	  + "\n\t YD_STK_BED_STL_SH : " 	+ ydStkBedStlSh;

    		commUtils.printLog(logId, szMsg, "");
			
    		// sExcptMsg 메세지가 있으면 처리
			for (int ii = 0; ii < 56; ii++) {
				sIfData += commUtils.trim(rcvMsg.getFieldString("SSTL_NO"+(ii+1))) + " ";
				if((ii+1) < 7) {
					sIfData += ", ";
				}
			}			
			
						
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6 || !ydEqpId.startsWith("GE")) {
				sExcptMsg = "전문 이상! 설비ID(YD_EQP_ID) 이상 [" + ydEqpId + "]";
			} else if ("".equals(ydStkBedStlSh)) {
				sExcptMsg = "전문 이상! 적치Bed재료매수(YD_STK_BED_STL_SH) 없음";
			}

			if(!"".equals(sExcptMsg)) {
				throw new Exception(sExcptMsg);
			}

// 2025.10.13 			
//			String ysStkColGp    = ydEqpId.substring(0,4)+"01";
			String ysStkColGp    = ydEqpId;

			/**********************************************************
			* 2. 적치Bed 재료번호, 재료상태  및 저장품 Update
			**********************************************************/
			JDTORecord outParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);					// Log ID
			jrParam.setResultMsg(methodNm);					// Log Method Name
			int mtlSh = Integer.parseInt(ydStkBedStlSh); 	// 야드적치Bed재료매수
			String stlNo = "";								// 재료번호
		
// 2025.11.26 적치열 매수 만큼만 트래킹 처리 
			if(8 < mtlSh) {
				mtlSh = 8;
			}
			
			JDTORecordSet jsChk = JDTORecordFactory.getInstance().createRecordSet("Temp");	
			
			jrParam.setResultCode(logId);							// Log ID
			jrParam.setResultMsg(methodNm);							// Log Method Name
			jrParam.setField("YS_STK_COL_GP"      , ysStkColGp   ); // 야드적치열구분
			

			/**********************************************************
			* 2.1 재료번호  CHECK : 저장위치 등록여부
			**********************************************************/
// 2025.09.11 체크 막음 체크 없이 수신되는 대로 처리			
//			for (int ii = 1; ii <= mtlSh; ii++) {
//				stlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	// 재료번호
//						
//				if ("".equals(stlNo)) {
//					sExcptMsg = "전문 이상! 재료번호(SSTL_NO" + ii + ") 없음";
//					throw new Exception(sExcptMsg);
//				}
//				jrParam = JDTORecordFactory.getInstance().create();
//				jrParam.setResultCode(logId);							// Log ID
//				jrParam.setResultMsg(methodNm);							// Log Method Name
//				jrParam.setField("YS_STK_COL_GP"      , ysStkColGp   ); // 야드적치열구분
//				jrParam.setField("SSTL_NO"			  , stlNo     ); 
//				jrParam.setField("YS_STK_BED_NO"      , "01"); 			// 야드적치Bed번호
//				
//				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYswBookYNstlNo
//				SELECT YS_STK_COL_GP
//			         , YS_STK_BED_NO 
//			      FROM TB_YS_STKLYR
//		       	 WHERE SSTL_NO = :V_SSTL_NO
//			       AND YS_STK_COL_GP = :V_YS_STK_COL_GP 
//				   AND YS_STK_BED_NO >= :V_YS_STK_BED_NO 
//				*/ 
//				jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYswBookYNstlNo", logId, methodNm, "저장위치 등록여부");
//	
//				if (jsChk != null && jsChk.size() > 0) {
//					
//					jrTemp = jsChk.getRecord(0);
//					
//					sExcptMsg   = "이미 야드에 적치된 재료 "
//						        + stlNo	
//								+ " ["
//								+ commUtils.trim(jrTemp.getFieldString("YS_STK_COL_GP")) 
//								+ "-" 
//					            + commUtils.trim(jrTemp.getFieldString("YS_STK_BED_NO"))
//					            + "]";
//					throw new Exception(sExcptMsg);
//				}	
//
//			}

// 2025.10.22 설비 적치열 CLEAR
			jrParam.setField("MODIFIER"           	, modifier		); // 수정자							
			jrParam.setField("YD_STK_LYR_ACT_STAT", "E"       		); 	// 야드적치단활성상태
			jrParam.setField("YD_STK_LYR_MTL_STAT", "E"       		); 	// 야드적치단재료상태(적치가능)
			jrParam.setField("YS_STK_COL_GP"      	, ysStkColGp   	); // 야드적치열구분
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColGpClear", logId, methodNm, "설비 적치단 CLEAR"); 
			
			
			/**********************************************************
			* 2.2 적재단, 저장품 등록
			**********************************************************/
			
			for (int ii = 1; ii <= mtlSh; ii++) {
				stlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	// 재료번호
						
				if ("".equals(stlNo)) {
					sExcptMsg = "재료번호(SSTL_NO" + ii + ") 없음";
					throw new Exception(sExcptMsg);
				}
				
				jrParam.setField("YS_STK_COL_GP"      	, ysStkColGp   			); // 야드적치열구분
				jrParam.setField("SSTL_NO"       		, stlNo    				); // 재료번호
				jrParam.setField("YS_STK_BED_NO"		, ysStkBedNo 			); // 야드적치Bed번호
				jrParam.setField("YS_STK_LYR_NO"		, ysStkLyrNo  			); // 특수강야드적치단번호
				jrParam.setField("YS_STK_SEQ_NO"		, String.valueOf(ii)	); // 야드적치SEQ
				jrParam.setField("YD_STK_LYR_MTL_STAT"	, "C"       			); // 야드적치단재료상태(적치중)
				jrParam.setField("MODIFIER"           	, modifier  			); // 수정자							
				//CARRY_OUT 시점에 저장픔 YD_RCPT_DATE UPDATE
				jrParam.setField("CARRY_OUT"	    , "Y"       ); 

				// 재료번호 이전 저장위치 재료번호 CLEAR
				jrParam.setField("YD_STK_LYR_MTL_STAT", "E"       ); 	// 야드적치단재료상태(적치가능)
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyrStlNoClear", logId, methodNm, "야드 적치단 재료번호 CLEAR"); 
				
				jrParam.setField("YD_STK_LYR_MTL_STAT", "C"       ); 	// 야드적치단재료상태(적치중)
				
				
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyr", logId, methodNm, "야드 적치단 등록"); 
				
				recPara.setField("BLT_NO" , stlNo     ); 

				jsChk = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getBilletComm", logId, methodNm, "BILLET 공통 조회");

				if (jsChk.size() > 0) {
					
					// 빌렛 출하상
					commDao.insert(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.insBtYdStock", logId, methodNm, "BILLET 야드 저장품 등록"); 
					
				} else {
					
					// 번들 출하상
					commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insBundleYdStock", logId, methodNm, "BUNDLE 야드 저장품 등록"); 
					
				}
				
			}
			

			JDTORecord jrRtn = null;
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch (DAOException e) {
			//if("".equals(sExcptMsg)) {
			//	sExcptMsg = e.getMessage();
			//}
			throw e;
		} catch (Exception e) {
			//if("".equals(sExcptMsg)) {
			//	sExcptMsg = e.getMessage();
			//}
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
	 *      [A] 오퍼레이션명 : 밴드쏘이송재 추출요구(M4YSL214)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvM4YSL214(JDTORecord rcvMsg) throws DAOException {
		
        String szMsg	= "";
		String methodNm = "밴드쏘이송재 추출요구[CbtYsL2RcvSeEJBBean.rcvM4YSL214] < " + rcvMsg.getResultMsg();
        String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);	

        JDTORecord jrParam = JDTORecordFactory.getInstance().create();
		JDTORecord inParam = JDTORecordFactory.getInstance().create();
		String sExcptMsg = "";
		String sIfData = "";
		String msgId = null;
		JDTORecordSet jsTemp = null;
		JDTORecord jrTemp = null;
		String szStkStlNo = null;

		JDTORecord jrRtn = null;

		try {
			commUtils.printLog(logId, methodNm, "S+");

	        
	        szMsg = rcvMsg.toString();
			commUtils.printLog(logId, szMsg, "");
			
            /*
			7	YD_EQP_ID			야드설비ID 			CHAR	6
			8	YS_STK_BED_NO		특수강야드적치Bed번호	CHAR	2
			9	YD_STK_BED_STL_SH	야드적치Bed재료매수	NUMBER	2
			10	YD_EQP_WRK_SH 		야드설비작업매수		NUMBER	2
			11	SSTL_NO1			특수강재료번호1		CHAR	12
			12	SSTL_NO2			특수강재료번호2		CHAR	12
			13	SSTL_NO3			특수강재료번호3		CHAR	12
			14	SSTL_NO4			특수강재료번호4		CHAR	12
			15	SSTL_NO5			특수강재료번호5		CHAR	12
			16	SSTL_NO6			특수강재료번호6		CHAR	12
			17	SSTL_NO7			특수강재료번호7		CHAR	12
			18	SSTL_NO8			특수강재료번호8		CHAR	12
			19	SSTL_NO9			특수강재료번호9		CHAR	12
			20	SSTL_NO10			특수강재료번호10		CHAR	12
            */
			
			
			// 수신 항목 값
			msgId         			= commUtils.getMsgId(rcvMsg); 
			String ydEqpId       	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"        	)); 	// 야드설비ID
			String ysStkBedNo    	= commUtils.trim(rcvMsg.getFieldString("YS_STK_BED_NO"    	)); 	// 야드적치Bed번호
			String ydStkBedStlSh 	= commUtils.trim(rcvMsg.getFieldString("YD_STK_BED_STL_SH"	)); 	// 야드적치Bed재료매수
			String ydEqpWrkSh 		= commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_SH"		)); 	// 야드설비작업매수
			String modifier      	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"       	)); 	// 수정자(Backup Only)
			String L3Hmi         	= commUtils.nvl(rcvMsg.getFieldString("L3_HMI"),"N");         		// 백업화면 기동 여부
			
			String stlNo 			= "";															// 재료번호
			String insYsStkBedNo	= "01";  														// 재료 적치 할 bed 
			
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

            szMsg = "\n\t YD_EQP_ID   		: " 	+ ydEqpId 
              	  + "\n\t YS_STK_BED_NO     : " 	+ ysStkBedNo 
              	  + "\n\t YD_STK_BED_STL_SH : " 	+ ydStkBedStlSh
              	  + "\n\t YD_EQP_WRK_SH 	: " 	+ ydEqpWrkSh;

    		commUtils.printLog(logId, szMsg, "");
			

    		int mtlSh = Integer.parseInt(ydEqpWrkSh); // 야드설비작업매수
			
			for (int ii = 1; ii <= mtlSh; ii++) {
				
				sIfData += commUtils.trim(rcvMsg.getFieldString("SSTL_NO"+ii));
				if(ii < mtlSh) {
					sIfData += ", ";
				}
				
			}
			

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6 || !ydEqpId.startsWith("GEPC05")) {
				sExcptMsg = "전문 이상! 설비ID(YD_EQP_ID) 이상 [" + ydEqpId + "]";
			} else if ("".equals(ysStkBedNo)) {
				sExcptMsg = "전문 이상! 적치Bed번호(YS_STK_BED_NO) 없음";
			} else if ("".equals(ydEqpWrkSh)) {
				sExcptMsg = "전문 이상! 야드설비작업매수(YD_EQP_WRK_SH) 없음";
			}

			if(!"".equals(sExcptMsg)) {
				throw new Exception(sExcptMsg);
			}
			

			JDTORecordSet jsChk = JDTORecordFactory.getInstance().createRecordSet("Temp");

			if(L3Hmi.equals("N")) {   // 전문이 오는 경우
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("YS_STK_COL_GP"      , ydEqpId   ); //야드적치열구분
				

				// 화면에서 백업시 야드 적치위치 등록 된 후에 온다
				/**********************************************************
				* 2.1 재료번호  CHECK : 저장위치 등록여부
				**********************************************************/
				
				for (int ii = 1; ii <= mtlSh; ii++) {
					stlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	// 재료번호
							
					if ("".equals(stlNo)) {
						sExcptMsg = "전문 이상! 재료번호(SSTL_NO" + ii + ") 없음";
						throw new Exception(sExcptMsg);
					}
					jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setResultCode(logId);	//Log ID
					jrParam.setResultMsg(methodNm);	//Log Method Name
					jrParam.setField("YS_STK_COL_GP"      , ydEqpId   ); 	// 야드적치열구분
					jrParam.setField("SSTL_NO"			  , stlNo     ); 	// 재료 번호
					jrParam.setField("YS_STK_BED_NO"      , "01"); 			// 야드적치Bed번호
					
					jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getSstlNoYardYN", logId, methodNm, "저장위치 등록여부");
		
					if (jsChk != null && jsChk.size() > 0) {
						
						jrTemp = jsChk.getRecord(0);
						
						sExcptMsg   = "이미 야드에 적치된 재료 "
							        + stlNo	
									+ " ["
									+ commUtils.trim(jrTemp.getFieldString("YS_STK_COL_GP")) 
									+ "-" 
						            + commUtils.trim(jrTemp.getFieldString("YS_STK_BED_NO"))
						            + "]";

						String sApplyYnPI = commDao.ApplyYnPI(logId, methodNm, "APPNEW", "001", "*");
						
						if("Y".equals(sApplyYnPI)){
							
							szMsg = "TB_YS_RULE 001 이미 야드에 적치된 재료 적용여부 ";
				      		commUtils.printLog(logId, szMsg, "");
							
							return jrRtn;
							
						}
						else {
							throw new Exception(sExcptMsg);
						}
					}	
	
				}
			}
			
			
			/**********************************************************
			* 2.1  저장품 등록
			**********************************************************/
			jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER"           , modifier  ); //수정자			
			
			for (int ii = 1; ii <= mtlSh; ii++) {
				stlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	//재료번호

				jrParam.setField("SSTL_NO"	, stlNo     ); 
				jrParam.setField("CARRY_OUT", "Y"       ); 
				commDao.insert(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.insBtYdStock", logId, methodNm, "야드 저장품 등록"); 
				
				
				inParam = JDTORecordFactory.getInstance().create();
				inParam.setResultCode(logId);	//Log ID
				inParam.setResultMsg(methodNm);	//Log Method Name
				//추가, 이동
				inParam.setField("FNL_REG_PGM"			, "M4YSL214" );
				inParam.setField("YD_GP"				, "G" );
				inParam.setField("YD_BAY_GP"			, ydEqpId.substring(1,2) );
				inParam.setField("YD_EQP_GP"			, ydEqpId.substring(2,4) );
				inParam.setField("YS_STK_COL_NO"		, ydEqpId.substring(4,6) );
				inParam.setField("YS_STK_BED_NO"		, commUtils.trim(ysStkBedNo) );
				inParam.setField("YS_STK_LYR_NO"		, "01" );
				inParam.setField("YS_STK_SEQ_NO"		, ""+ii );
				inParam.setField("YS_STR_LOC"			, ydEqpId 
						                                + commUtils.trim(ysStkBedNo)
						                                + "01"
						                                + ""+ii) ;
				inParam.setField("SSTL_NO"				, stlNo  );
				
				/**********************************************************
				* 1.2공통 저장위치 Update (별도 Transaction 으로 처리)
				**********************************************************/
				EJBConnector tranConn = new EJBConnector("default", "BtYsL2RcvSeEJB", this);
				tranConn.trx("updBtCommYsStrLoc", new Class[] { JDTORecord.class }, new Object[] { inParam });
			}

			
			if(L3Hmi.equals("N")) {
			

				/**********************************************************
				* 2.1 적재단, 저장품 등록
				**********************************************************/

				for (int ii = 1; ii <= mtlSh; ii++) {
					stlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	//재료번호
							
					if ("".equals(stlNo)) {
						sExcptMsg = "전문 이상! 재료번호(SSTL_NO" + ii + ") 없음";
						throw new Exception(sExcptMsg);
					}
					jrParam = JDTORecordFactory.getInstance().create();
					
					
					
					jrParam.setResultCode(logId);							// Log ID
					jrParam.setResultMsg(methodNm);							// Log Method Name
					jrParam.setField("YS_STK_COL_GP"      , ydEqpId   ); 	// 야드적치열구분
					jrParam.setField("SSTL_NO"			  , stlNo     ); 	// 재료번호
					jrParam.setField("YS_STK_BED_NO"      , insYsStkBedNo); // 야드적치Bed번호
					jrParam.setField("YS_STK_LYR_NO"      , "01"); 			// 야드적치 단 -> 무조건 1단
					jrParam.setField("YS_STK_SEQ_NO", String.valueOf(ii)); 	// 야드적치SEQ
					jrParam.setField("YD_STK_LYR_MTL_STAT", "C"       ); 	// 야드적치단재료상태(적치중)
					jrParam.setField("MODIFIER"           , modifier  ); 	// 수정자
					
					// 재료번호 이전 저장위치 재료번호 CLEAR
					jrParam.setField("YD_STK_LYR_MTL_STAT", "E"       ); 	// 야드적치단재료상태(적치중)
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyrStlNoClear", logId, methodNm, "야드 적치단 재료번호 CLEAR"); 

					
					
					jrParam.setField("YD_STK_LYR_MTL_STAT", "C"       ); 	// 야드적치단재료상태(적치중)
					
					/* STKLYR 변경 - com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyr
					UPDATE TB_YS_STKLYR
					SET    SSTL_NO = :V_SSTL_NO
					        ,YD_STK_LYR_ACT_STAT = NVL(:V_YD_STK_LYR_ACT_STAT,YD_STK_LYR_ACT_STAT)
					        ,YD_STK_LYR_MTL_STAT = NVL(:V_YD_STK_LYR_MTL_STAT,YD_STK_LYR_MTL_STAT)
					        ,MODIFIER = :V_MODIFIER
					        ,MOD_DDTT = SYSDATE
					WHERE  YS_STK_COL_GP = :V_YS_STK_COL_GP
					AND    YS_STK_BED_NO = :V_YS_STK_BED_NO
					AND    YS_STK_LYR_NO = :V_YS_STK_LYR_NO
					AND    YS_STK_SEQ_NO = :V_YS_STK_SEQ_NO
					*/
// 2025.11.04 밴드쏘이송재 추출요구 차에 실을때 까지 시간이 많이 걸릴수도 있기 때문에 다음 추출요구시 
//			     앞에 추출요구 정보가 없어질수도 있어 적치단 저장품 등록 하지 않고 TB_YS_PREPSCH(YS_준비스케줄), TB_YS_PREPMTL(YS_준비재료)만 등록 
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyr", logId, methodNm, "야드 적치단 등록"); 
					
				 
				}
			
			} else {
				insYsStkBedNo = ysStkBedNo;
			}			
			

			
		    String szSPOS_WLOC_CD 		= "S3Y99"; 		// 대형옥내 남42문 - 밴드쏘이송재 추출요구는 무조건 남42문 이송출고	 
		    String szARR_WLOC_CD 		= "S3Y21"; 		// 착지 개소 코드 - 대형옥외 옥외야드(S3Y21)
			

 // 2025.11.04 이송지시(TB_PB_STLFRTOMOVE) 테이블 등록 
 			szMsg = "TB_PB_STLFRTOMOVE(이송지시) 등록 발지 [" + szSPOS_WLOC_CD + "] 착지 [" + szARR_WLOC_CD + "]";
       		commUtils.printLog(logId, szMsg, "******");
		    
		    stlNo = "";
			for (int ii = 1; ii <= mtlSh; ii++) {
				
				stlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	// 재료번호
				
//2025.11.04 재료번호 별로 이송지시 테이블 INSERT					
				
				jrParam = JDTORecordFactory.getInstance().create();

				jrParam.setResultCode(logId);									// Log ID
				jrParam.setResultMsg(methodNm);									// Log Method Name
				jrParam.setField("SSTL_NO"      		, stlNo   			); 	// 재료번호
				jrParam.setField("USER_ID"      		, modifier			); 	// 등록자, 수정자
//				jrParam.setField("SPOS_WLOC_CD"			, szSPOS_WLOC_CD   	); 	// 대형옥내 남42문	 
//				jrParam.setField("ARR_WLOC_CD"      	, szARR_WLOC_CD		); 	// 착지 개소 코드 - 대형옥외 옥외야드(S3Y21)
				jrParam.setField("SPOS_WLOC_CD"			, "S3Y99"   		); 	// 대형옥내 남42문	 
				jrParam.setField("ARR_WLOC_CD"      	, "S3Y21"			); 	// 착지 개소 코드 - 대형옥외 옥외야드(S3Y21)
				jrParam.setField("REG_PGM"				, "YardSystem "		); 	// 등록프로그램 

				szMsg = "****** insPbStlFrToMove jrParam : " + jrParam.toString();
	      		commUtils.printLog(logId, szMsg, "");
				
				// 이송지시 테이블 INSERT
				insPbStlFrToMove(jrParam);
				
			}
		  
// 2025.11.04 TB_YS_PREPSCH(YS_준비스케줄), TB_YS_PREPMTL(YS_준비재료) 등록 
			szMsg = "TB_YS_PREPSCH(YS_준비스케줄), TB_YS_PREPMTL(YS_준비재료) 등록";
      		commUtils.printLog(logId, szMsg, "******");

			String 	szYD_SCH_CD			= "GETR42UB";	// 빌렛정정 이송출고 (밴드쏘)
			String 	szYD_AIM_BAY_GP		= "F";			// 대형옥외 옥외야드
			
			jrParam = JDTORecordFactory.getInstance().create();
			
			jrParam.setResultCode(logId);											// Log ID
			jrParam.setResultMsg(methodNm);											// Log Method Name

			String	ydPrepSchId = commDao.getSeqId(logId, methodNm, "PrepSch"	);	// 야드준비스케쥴ID
			
			jrParam.setField("YD_PREP_SCH_ID", 		ydPrepSchId					); 	// 야드준비스케쥴ID
			jrParam.setField("YD_SCH_CD", 			szYD_SCH_CD					); 	// 스케줄코드
			jrParam.setField("YD_PREP_WK_ST", 		"L"							); 	// 야드준비작업상태
			jrParam.setField("YD_AIM_BAY_GP", 		szYD_AIM_BAY_GP				); 	// 목적동
			
			stlNo = "";
			for (int ii = 1; ii <= mtlSh; ii++) {
				
				stlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	// 재료번호

				//준비재료 등록
				jrParam.setField("SSTL_NO"			, stlNo); 
				jrParam.setField("YD_GP"			, "G"); 
				jrParam.setField("YS_STK_COL_GP"	, ydEqpId); 
				jrParam.setField("YS_STK_BED_NO"	, "01"); 
				jrParam.setField("YS_STK_LYR_NO"	, "01"); 
				jrParam.setField("YS_STK_SEQ_NO"	, String.valueOf(ii)); 
				jrParam.setField("MODIFIER"			, modifier); 	// 등록자, 수정자
				
				commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insPrepMtl", logId, methodNm, "준비재료 등록");
				
			}
			
			commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insPrepSchAimBay", logId, methodNm, "준비스케줄 등록");
			
			szMsg = "야드준비스케쥴ID [" + ydPrepSchId + "] 스케줄코드 [" + szYD_SCH_CD + "] 등록";
      		commUtils.printLog(logId, szMsg, "******");
		    
		    
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch (DAOException e) {
			//if("".equals(sExcptMsg)) {
			//	sExcptMsg = e.getMessage();
			//}
			throw e;
		} catch (Exception e) {
			//if("".equals(sExcptMsg)) {
			//	sExcptMsg = e.getMessage();
			//}
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
	 *      [A] 오퍼레이션명 : 빌렛정정출하상 추출요구(M4YSL215)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvM4YSL215(JDTORecord rcvMsg) throws DAOException {
		
        String szMsg	= "";
		String methodNm = "빌렛정정출하상 추출요구[CbtYsL2RcvSeEJBBean.rcvM4YSL215] < " + rcvMsg.getResultMsg();
        String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);	

        JDTORecord jrParam = JDTORecordFactory.getInstance().create();
		JDTORecord inParam = JDTORecordFactory.getInstance().create();
		String sExcptMsg = "";
		String sIfData = "";
		String msgId = null;
		JDTORecordSet jsTemp = null;
		JDTORecord jrTemp = null;
		String szStkStlNo = null;

		JDTORecord jrRtn = null;
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

	        
	        szMsg = rcvMsg.toString();
			commUtils.printLog(logId, szMsg, "");
			
            /*
			7	YD_EQP_ID			야드설비ID 			CHAR	6
			8	YS_STK_BED_NO		특수강야드적치Bed번호	CHAR	2
			9	YD_STK_BED_STL_SH	야드적치Bed재료매수	NUMBER	2
			10	YD_EQP_WRK_SH 		야드설비작업매수		NUMBER	2
			11	SSTL_NO1			특수강재료번호1		CHAR	12
			12	SSTL_NO2			특수강재료번호2		CHAR	12
			13	SSTL_NO3			특수강재료번호3		CHAR	12
			14	SSTL_NO4			특수강재료번호4		CHAR	12
			15	SSTL_NO5			특수강재료번호5		CHAR	12
			16	SSTL_NO6			특수강재료번호6		CHAR	12
			17	SSTL_NO7			특수강재료번호7		CHAR	12
			18	SSTL_NO8			특수강재료번호8		CHAR	12
			19	SSTL_NO9			특수강재료번호9		CHAR	12
			20	SSTL_NO10			특수강재료번호10		CHAR	12
            */
			
			
			// 수신 항목 값
			msgId         			= commUtils.getMsgId(rcvMsg); 
			String ydEqpId       	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"        	)); 	// 야드설비ID
			String ysStkBedNo    	= commUtils.trim(rcvMsg.getFieldString("YS_STK_BED_NO"    	)); 	// 야드적치Bed번호
			String ydStkBedStlSh 	= commUtils.trim(rcvMsg.getFieldString("YD_STK_BED_STL_SH"	)); 	// 야드적치Bed재료매수
			String ydEqpWrkSh 		= commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_SH"		)); 	// 야드설비작업매수
			String modifier      	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"       	)); 	// 수정자(Backup Only)
			String L3Hmi         	= commUtils.nvl(rcvMsg.getFieldString("L3_HMI"),"N");         	// 백업화면 기동 여부
			
			String stlNo 			= "";															// 재료번호
			String insYsStkBedNo	= "01";  														// 재료 적치 할 bed 
			
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

            szMsg = "\n\t YD_EQP_ID   		: " 	+ ydEqpId 
              	  + "\n\t YS_STK_BED_NO     : " 	+ ysStkBedNo 
              	  + "\n\t YD_STK_BED_STL_SH : " 	+ ydStkBedStlSh
        	  	  + "\n\t YD_EQP_WRK_SH 	: " 	+ ydEqpWrkSh;

    		commUtils.printLog(logId, szMsg, "");
			

    		int mtlSh = Integer.parseInt(ydEqpWrkSh); // 야드설비작업매수
			
			for (int ii = 1; ii <= mtlSh; ii++) {
				
				sIfData += commUtils.trim(rcvMsg.getFieldString("SSTL_NO"+ii));
				if(ii < mtlSh) {
					sIfData += ", ";
				}
				
			}
			
// 2025.11.26 적치열 매수 만큼만 트래킹 처리 
			if(8 < mtlSh) {
				mtlSh = 8;
			}
						
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6 || !ydEqpId.startsWith("GEPC0")) {
				sExcptMsg = "전문 이상! 설비ID(YD_EQP_ID) 이상 [" + ydEqpId + "]";
			} else if ("".equals(ysStkBedNo)) {
				sExcptMsg = "전문 이상! 적치Bed번호(YS_STK_BED_NO) 없음";
			} else if ("".equals(ydEqpWrkSh)) {
				sExcptMsg = "전문 이상! 야드설비작업매수(YD_EQP_WRK_SH) 없음";
			}

			if(!"".equals(sExcptMsg)) {
				throw new Exception(sExcptMsg);
			}
			

			JDTORecordSet jsChk = JDTORecordFactory.getInstance().createRecordSet("Temp");

			if(L3Hmi.equals("N")) {   // 전문이 오는 경우
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("YS_STK_COL_GP"      , ydEqpId   ); //야드적치열구분
				

				// 화면에서 백업시 야드 적치위치 등록 된 후에 온다
				/**********************************************************
				* 2.1 재료번호  CHECK : 저장위치 등록여부
				**********************************************************/
				
				for (int ii = 1; ii <= mtlSh; ii++) {
					stlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	// 재료번호
							
					if ("".equals(stlNo)) {
						sExcptMsg = "전문 이상! 재료번호(SSTL_NO" + ii + ") 없음";
						throw new Exception(sExcptMsg);
					}
					jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setResultCode(logId);	//Log ID
					jrParam.setResultMsg(methodNm);	//Log Method Name
					jrParam.setField("YS_STK_COL_GP"      , ydEqpId   ); 	// 야드적치열구분
					jrParam.setField("SSTL_NO"			  , stlNo     ); 	// 재료 번호
					jrParam.setField("YS_STK_BED_NO"      , "01"); 			// 야드적치Bed번호
					
					jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getSstlNoYardYN", logId, methodNm, "저장위치 등록여부");
		
					if (jsChk != null && jsChk.size() > 0) {
						
						jrTemp = jsChk.getRecord(0);
						
						sExcptMsg   = "이미 야드에 적치된 재료 "
							        + stlNo	
									+ " ["
									+ commUtils.trim(jrTemp.getFieldString("YS_STK_COL_GP")) 
									+ "-" 
						            + commUtils.trim(jrTemp.getFieldString("YS_STK_BED_NO"))
						            + "]";
						String sApplyYnPI = commDao.ApplyYnPI(logId, methodNm, "APPNEW", "001", "*");
						
						if("Y".equals(sApplyYnPI)){
							
							szMsg = "TB_YS_RULE 001 이미 야드에 적치된 재료 적용여부 ";
				      		commUtils.printLog(logId, szMsg, "");
							
							return jrRtn;
							
						}
						else {
							throw new Exception(sExcptMsg);
						}
					}	
	
				}
			}
			
			
			/**********************************************************
			* 2.1  저장품 등록
			**********************************************************/
			jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER"           , modifier  ); //수정자			
			
			for (int ii = 1; ii <= mtlSh; ii++) {
				stlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	//재료번호

				jrParam.setField("SSTL_NO"	, stlNo     ); 
				jrParam.setField("CARRY_OUT", "Y"       ); 
				commDao.insert(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.insBtYdStock", logId, methodNm, "야드 저장품 등록"); 
				
				
				inParam = JDTORecordFactory.getInstance().create();
				inParam.setResultCode(logId);	//Log ID
				inParam.setResultMsg(methodNm);	//Log Method Name
				//추가, 이동
				inParam.setField("FNL_REG_PGM"			, "M4YSL215" );
				inParam.setField("YD_GP"				, "G" );
				inParam.setField("YD_BAY_GP"			, ydEqpId.substring(1,2) );
				inParam.setField("YD_EQP_GP"			, ydEqpId.substring(2,4) );
				inParam.setField("YS_STK_COL_NO"		, ydEqpId.substring(4,6) );
				inParam.setField("YS_STK_BED_NO"		, commUtils.trim(ysStkBedNo) );
				inParam.setField("YS_STK_LYR_NO"		, "01" );
				inParam.setField("YS_STK_SEQ_NO"		, ""+ii );
				inParam.setField("YS_STR_LOC"			, ydEqpId 
						                                + commUtils.trim(ysStkBedNo)
						                                + "01"
						                                + ""+ii) ;
				inParam.setField("SSTL_NO"				, stlNo  );
				
				/**********************************************************
				* 1.2공통 저장위치 Update (별도 Transaction 으로 처리)
				**********************************************************/
				EJBConnector tranConn = new EJBConnector("default", "BtYsL2RcvSeEJB", this);
				tranConn.trx("updBtCommYsStrLoc", new Class[] { JDTORecord.class }, new Object[] { inParam });
			}

			
			if(L3Hmi.equals("N")) {
			

				/**********************************************************
				* 2.1 적재단, 저장품 등록
				**********************************************************/

				for (int ii = 1; ii <= mtlSh; ii++) {
					stlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	//재료번호
							
					if ("".equals(stlNo)) {
						sExcptMsg = "전문 이상! 재료번호(SSTL_NO" + ii + ") 없음";
						throw new Exception(sExcptMsg);
					}
					jrParam = JDTORecordFactory.getInstance().create();
					
					
					
					jrParam.setResultCode(logId);							// Log ID
					jrParam.setResultMsg(methodNm);							// Log Method Name
					jrParam.setField("YS_STK_COL_GP"      , ydEqpId   ); 	// 야드적치열구분
					jrParam.setField("SSTL_NO"			  , stlNo     ); 	// 재료번호
					jrParam.setField("YS_STK_BED_NO"      , insYsStkBedNo); // 야드적치Bed번호
					jrParam.setField("YS_STK_LYR_NO"      , "01"); 			// 야드적치 단 -> 무조건 1단
					jrParam.setField("YS_STK_SEQ_NO", String.valueOf(ii)); 	// 야드적치SEQ
					jrParam.setField("YD_STK_LYR_MTL_STAT", "C"       ); 	// 야드적치단재료상태(적치중)
					jrParam.setField("MODIFIER"           , modifier  ); 	// 수정자
					
					// 재료번호 이전 저장위치 재료번호 CLEAR
					jrParam.setField("YD_STK_LYR_MTL_STAT", "E"       ); 	// 야드적치단재료상태(적치중)
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyrStlNoClear", logId, methodNm, "야드 적치단 재료번호 CLEAR"); 

					
					
					jrParam.setField("YD_STK_LYR_MTL_STAT", "C"       ); 	// 야드적치단재료상태(적치중)
					
					
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyr", logId, methodNm, "야드 적치단 등록"); 
					
				 
				}
			
			} else {
				insYsStkBedNo = ysStkBedNo;
			}			
			
			
			
			// STOCK 재료번호가 존재하지 않으면 작업예약을 만들지 않는다.
			for (int ii = 1; ii <= mtlSh; ii++) {
				
				stlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	//재료번호
				
				jrParam.setField("SSTL_NO"	, stlNo );
				
				//제품번호가 야드저장품에 존재하는지, 작업예약과 크레인스케줄에 잡혀있는 대상인지를 가져오고 BILLET공통에서 현재진도코드를 가져온다. 
				jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.getIsEnableStlNo", logId, methodNm, "제품번호 작업대상여부 조회");
				
				if (jsTemp != null && jsTemp.size() > 0) {
					
					jrTemp = jsTemp.getRecord(0);
	
					szStkStlNo		= commUtils.trim(jrTemp.getFieldString("SSTL_NO")); //Stock 에 존재하지 않으면 ""
			    } 			
				
				//저장품에 존재하는 제품번호인지 체크
				if("".equals(szStkStlNo)) {

					sExcptMsg = "전단실적 누락 " + stlNo;
					//throw new Exception(sExcptMsg);
					break;
				}
			}
			
			if("".equals(sExcptMsg)){
				
				// 작업예약등록
				// CbtYsJspSeEJBBean  	>> updbtMvStkWrkBook 
//				String stlNos = commUtils.trim(gdReq.getParam("SSTL_NOS")); // 재료번호들
//				String ysStkColGp = commUtils.trim(gdReq.getParam("YS_STK_COL_GP")); // 야드적치열구분(6자리 이상)
//				String ydToLocGuide = commUtils.trim(gdReq.getParam("YD_TO_LOC_GUIDE")); // 야드To위치Guide
//				String ydWrkCrn = commUtils.trim(gdReq.getParam("YD_WRK_CRN")); // 야드작업크레인(작업자지정 크레인)
				GridData inGridData = new GridData();

				String stlNos = "";
				for (int ii = 1; ii <= mtlSh; ii++) {
					
					stlNos += commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	// 재료번호
					if(ii < mtlSh) {
						stlNos += ",";
					}
					
				}
				
//////////////////////////////////////////////////////////////////////////////////
// 빌렛 정정 출하상 To위치Guide 차량인 경우  START  				
// 2025.12.08 자리수 체크 필요
// 2025.12.08 substring 막음
				String sApplyYnPI = commDao.ApplyYnPI(logId, methodNm, "APPNEW", "011", "*");
				if("Y".equals(sApplyYnPI)){
					szMsg = "TB_YS_RULE 011 빌렛 정정 입측 보급 트래킹 제외 ";
		      		commUtils.printLog(logId, szMsg, "");

// 2025.11.06 GEPC01, GEPC02, GEPC04 출하상 추출요구시 작업예약에 TO위치가이드 전송(SELECT ITEM FROM TB_YS_RULE WHERE REPR_CD_GP = 'APPGI3')
// 2025.11.11 GEPC04 추출 요구 이면 야드To위치Guide GEXXXXXX				
					String ydToLocGuide = "GEXXXXXX"; // 야드To위치Guide
					if(  "GEPC01".equals(ydEqpId)  
					  || "GEPC02".equals(ydEqpId)
					  || "GEPC04".equals(ydEqpId)) {
						
						ydToLocGuide = CarTSMv.ApplyRuleItem(logId, methodNm, "APPGI3", ydEqpId);
	
			 			szMsg = "************ 빌렛 출하상 야드To위치Guide [" + ydToLocGuide + "]";
			       		commUtils.printLog(logId, szMsg, "******");
						
					}
				
					if ("GETR11".equals(ydToLocGuide)) {
// 2025.11.06 야드To위치Guide 차량 이면 예약 만들지 않고 준비 스케쥴 등록
	//					TB_YS_PREPSCH(YS_준비스케줄), TB_YS_PREPMTL(YS_준비재료)
	//					TB_PB_STLFRTOMOVE(이송 지시 테이블)
					    String szSPOS_WLOC_CD 		= "S3Y22"; 		// 대형옥내 남1문 - 빌렛출하상 추출요구는 무조건 남1문 이송출고	 
					    String szARR_WLOC_CD 		= ""; 			// 착지 개소 코드 - 대형옥외 옥외야드(S3Y21),사외통합(철분말)(BSY04)
					    
// 2025.12.07 이송 목적지(TB_YS_RULE 테이블의 REPR_CD_GP : APPGI5) 검색
					    szARR_WLOC_CD 	= CarTSMv.ApplyRuleItem(logId, methodNm, "APPGI5", ydEqpId);
	
			 			szMsg = "************ 빌렛정정출하상 이송목적지 [" + szARR_WLOC_CD + "]";
			       		commUtils.printLog(logId, szMsg, "******");
					    
			       		stlNo = "";
	
			 			szMsg = "TB_PB_STLFRTOMOVE(이송지시) 등록";
			       		commUtils.printLog(logId, szMsg, "******");
			       		
			       		
						for (int ii = 1; ii <= mtlSh; ii++) {
							
							stlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	//재료번호
							
//2025.11.06 재료번호 별로 이송지시 테이블 INSERT					
							
							jrParam = JDTORecordFactory.getInstance().create();
	
							jrParam.setResultCode(logId);									// Log ID
							jrParam.setResultMsg(methodNm);									// Log Method Name
							jrParam.setField("SSTL_NO"      		, stlNo   			); 	// 재료번호
							jrParam.setField("USER_ID"      		, modifier			); 	// 등록자, 수정자
							jrParam.setField("SPOS_WLOC_CD"			, szSPOS_WLOC_CD   	); 	// 대형옥내 남42문	 
							jrParam.setField("ARR_WLOC_CD"      	, szARR_WLOC_CD		); 	// 착지 개소 코드 - 대형옥외 옥외야드(S3Y21)
							jrParam.setField("REG_PGM"				, "YardSystem "		); 	// 등록프로그램 
	
							szMsg = "****** insPbStlFrToMove jrParam : " + jrParam.toString();
				      		commUtils.printLog(logId, szMsg, "");
							
							// 이송지시 테이블 INSERT
							insPbStlFrToMove(jrParam);
						}
			       		
// 2025.11.06 TB_YS_PREPSCH(YS_준비스케줄), TB_YS_PREPMTL(YS_준비재료) 등록 
						szMsg = "TB_YS_PREPSCH(YS_준비스케줄), TB_YS_PREPMTL(YS_준비재료) 등록";
			      		commUtils.printLog(logId, szMsg, "******");
	
						String 	szYD_SCH_CD			= "GETR11UB";	// 빌렛정정 남1문 이송출고
						String 	szYD_AIM_BAY_GP		= "F";			// 대형옥외 옥외야드
						
						jrParam = JDTORecordFactory.getInstance().create();
						
						jrParam.setResultCode(logId);											// Log ID
						jrParam.setResultMsg(methodNm);											// Log Method Name
	
						String	ydPrepSchId = commDao.getSeqId(logId, methodNm, "PrepSch"	);	// 야드준비스케쥴ID
						
						jrParam.setField("YD_PREP_SCH_ID", 		ydPrepSchId					); 	// 야드준비스케쥴ID
						jrParam.setField("YD_SCH_CD", 			szYD_SCH_CD					); 	// 스케줄코드
						jrParam.setField("YD_PREP_WK_ST", 		"L"							); 	// 야드준비작업상태
						jrParam.setField("YD_AIM_BAY_GP", 		szYD_AIM_BAY_GP				); 	// 목적동
						
						stlNo = "";
						for (int ii = 1; ii <= mtlSh; ii++) {
							
							stlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	// 재료번호
	
							//준비재료 등록
							jrParam.setField("SSTL_NO"			, stlNo); 
							jrParam.setField("YD_GP"			, "G"); 
							jrParam.setField("YS_STK_COL_GP"	, ydEqpId); 
							jrParam.setField("YS_STK_BED_NO"	, "01"); 
							jrParam.setField("YS_STK_LYR_NO"	, "01"); 
							jrParam.setField("YS_STK_SEQ_NO"	, String.valueOf(ii)); 
							jrParam.setField("MODIFIER"			, modifier); 	// 등록자, 수정자
							
							commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insPrepMtl", logId, methodNm, "준비재료 등록");
							
						}
						
						commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insPrepSchAimBay", logId, methodNm, "준비스케줄 등록");
						
						szMsg = "야드준비스케쥴ID [" + ydPrepSchId + "] 스케줄코드 [" + szYD_SCH_CD + "] 등록";
			      		commUtils.printLog(logId, szMsg, "******");
						
					}
					else {
						inGridData.addParam("SSTL_NOS", 		stlNos);			// 재료번호들
						inGridData.addParam("YS_STK_COL_GP", 	ydEqpId);			// 야드적치열구분(6자리 이상)
						inGridData.addParam("YD_TO_LOC_GUIDE", 	ydToLocGuide);		// 야드To위치Guide
						inGridData.addParam("YD_WRK_CRN", 		"");				// 야드작업크레인(작업자지정 크레인)
						inGridData.addParam("userid", 			modifier);			// 수정자
						
						szMsg = "\n\t SSTL_NO   		: " 	+ stlNos 
		                	  + "\n\t YS_STK_COL_GP     : " 	+ ydEqpId;
	
			      		commUtils.printLog(logId, szMsg, "");
						
						jrRtn = CbtYsJsp.updbtMvStkWrkBook(inGridData);
					}
//빌렛 정정 출하상 To위치Guide 차량인 경우  START  				
//////////////////////////////////////////////////////////////////////////////////
					
				} else {
					
					
// 2025.11.06 GEPC01, GEPC02 출하상 추출요구시 작업예약에 TO위치가이드 전송(SELECT ITEM FROM TB_YS_RULE WHERE REPR_CD_GP = 'APPGI3')
// 2025.11.11 GEPC04 추출 요구 이면 야드To위치Guide GEXXXXXX				
					String ydToLocGuide = "GEXXXXXX"; // 야드To위치Guide
					if(  "GEPC01".equals(ydEqpId.substring(0, 6))  
					  || "GEPC02".equals(ydEqpId.substring(0, 6))) {
						
						
						ydToLocGuide = CarTSMv.ApplyRuleItem(logId, methodNm, "APPGI3", ydEqpId);
						
			 			szMsg = "************ 빌렛 출하상 야드To위치Guide [" + ydToLocGuide + "]";
			       		commUtils.printLog(logId, szMsg, "******");
						
					}
					
					inGridData.addParam("SSTL_NOS", 		stlNos);			// 재료번호들
					inGridData.addParam("YS_STK_COL_GP", 	ydEqpId);			// 야드적치열구분(6자리 이상)
					inGridData.addParam("YD_TO_LOC_GUIDE", 	ydToLocGuide);		// 야드To위치Guide
					inGridData.addParam("YD_WRK_CRN", 		"");				// 야드작업크레인(작업자지정 크레인)
					inGridData.addParam("userid", 			modifier);			// 수정자
					
					szMsg = "\n\t SSTL_NO   		: " 	+ stlNos 
	                	  + "\n\t YS_STK_COL_GP     : " 	+ ydEqpId;

		      		commUtils.printLog(logId, szMsg, "");
					
					jrRtn = CbtYsJsp.updbtMvStkWrkBook(inGridData);
				}

				
				
//				//예외처리 없을 경우만 호출한다.
//				/**********************************************************
//				* 3. 대형압연 빌렛/봉강 출하상 추출요구 처리
//				**********************************************************/
//				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
//
//				// 설비인출요구
//				jrYdMsg.setResultCode(logId);	//Log ID
//				jrYdMsg.setResultMsg(methodNm);	//Log Method Name
//				jrYdMsg.setField("JMS_TC_CD"         , "YSYSJ502"               ); // JMSTC코드
//				jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); // JMSTC생성일시
//				jrYdMsg.setField("YD_EQP_ID"         , ydEqpId                  ); // 야드설비ID
//				jrYdMsg.setField("YS_STK_BED_NO"     , insYsStkBedNo  			); // 야드적치Bed번호
//				jrYdMsg.setField("YS_STK_LYR_NO"     , "01"               		); // 야드적치단번호
//				jrYdMsg.setField("YD_SCH_ST_GP"      , "A"                      ); // 야드스케쥴기동구분(Auto)
//				jrYdMsg.setField("MODIFIER"        , modifier                 	); // 수정자
//				
//		        szMsg = jrYdMsg.toString();
//				commUtils.printLog(logId, szMsg, "");
//				
//				//전송할 전문에 추가
//				jrRtn = commUtils.addSndData(jrYdMsg);
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch (DAOException e) {
			//if("".equals(sExcptMsg)) {
			//	sExcptMsg = e.getMessage();
			//}
			throw e;
		} catch (Exception e) {
			//if("".equals(sExcptMsg)) {
			//	sExcptMsg = e.getMessage();
			//}
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
	 *      [A] 오퍼레이션명 : 빌렛 정정 설비 Tracking 정보(M4YSL220)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvM4YSL220(JDTORecord rcvMsg) throws DAOException {
		
        String szMsg	= "";
		String methodNm = "빌렛 정정 설비 Tracking 정보[CbtYsL2RcvSeEJBBean.rcvM4YSL220] < " + rcvMsg.getResultMsg();
        String logId 	= commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);	

        JDTORecord jrParam 		= JDTORecordFactory.getInstance().create();
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
			7	YD_EQP_ID			야드설비ID 			CHAR	6
			8	YD_STK_BED_STL_SH	야드적치Bed재료매수	NUMBER	2
			09	SSTL_NO1			특수강재료번호1		CHAR	12
			
			...
			
			49	SSTL_NO40			특수강재료번호40	CHAR	12
            */
			
			
			//수신 항목 값
			msgId         			= commUtils.getMsgId(rcvMsg); 
			String ydEqpId       	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"        	)); 	// 야드설비ID
			String ysStkBedNo    	= commUtils.trim(rcvMsg.getFieldString("YS_STK_BED_NO"		)); 	// 야드적치Bed번호
			String ydStkBedStlSh 	= commUtils.trim(rcvMsg.getFieldString("YD_STK_BED_STL_SH"	)); 	// 야드적치Bed재료매수
			String modifier      	= commUtils.trim(rcvMsg.getFieldString("MODIFIER"       	)); 	// 수정자(Backup Only)
			String ysStkLyrNo    	= "01"; 															// 야드적치단번호
			
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;


            szMsg = "\n\t YD_EQP_ID   		: " 	+ ydEqpId 
            	  + "\n\t YS_STK_BED_NO 	: " 	+ ysStkBedNo
            	  + "\n\t YD_STK_BED_STL_SH : " 	+ ydStkBedStlSh;

    		commUtils.printLog(logId, szMsg, "");
			
    		// sExcptMsg 메세지가 있으면 처리
			for (int ii = 0; ii < 56; ii++) {
				sIfData += commUtils.trim(rcvMsg.getFieldString("SSTL_NO"+(ii+1))) + " ";
				if((ii+1) < 7) {
					sIfData += ", ";
				}
			}			
			
						
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6 || !ydEqpId.startsWith("GE")) {
				sExcptMsg = "전문 이상! 설비ID(YD_EQP_ID) 이상 [" + ydEqpId + "]";
			} else if ("".equals(ysStkBedNo)) {
				sExcptMsg = "전문 이상! 적치Bed번호(YS_STK_BED_NO) 없음";
			} else if ("".equals(ydStkBedStlSh)) {
				sExcptMsg = "전문 이상! 적치Bed재료매수(YD_STK_BED_STL_SH) 없음";
			}

			if(!"".equals(sExcptMsg)) {
				throw new Exception(sExcptMsg);
			}
			
//			String ysStkColGp    = ydEqpId.substring(0,4)+"01";
			String ysStkColGp    = ydEqpId;

			
// 2025.12.09 빌렛 정정 야드 보급대(설비 입측 GETF01, GEFT02, GETF04) 트래킹 정보 처리 제외
//            크레인 작업지시가 만들어진 상태에서 트래킹이 와서 크레인 작업 권하 위치 정보가 CLEAR 되는 현상 발생			
			String sApplyYnPI = commDao.ApplyYnPI(logId, methodNm, "APPNEW", "011", "*");
			if("Y".equals(sApplyYnPI)){
				
				szMsg = "TB_YS_RULE APPNEW 011 빌렛 정정 야드 보급대(설비 입측 [" + ydEqpId +"] 트래킹 정보 처리 제외 ";
	      		commUtils.printLog(logId, szMsg, "");
				
	      		if(  "GETF01".equals(ydEqpId.substring(0, 6))  
	  			  || "GETF02".equals(ydEqpId.substring(0, 6))
	  			  || "GETF04".equals(ydEqpId.substring(0, 6))) {

	      			szMsg = "TB_YS_RULE APPNEW 011 빌렛 정정 야드 보급대(설비 입측 [" + ydEqpId +"] 트래킹 정보 처리 제외 처리 ";
		      		commUtils.printLog(logId, szMsg, "");

					commUtils.printLog(logId, methodNm, "S-");
					
					return jrRtn;
	      			
	      		}

			}
			
			/**********************************************************
			* 2. 적치Bed 재료번호, 재료상태  및 저장품 Update
			**********************************************************/
			JDTORecord outParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);					// Log ID
			jrParam.setResultMsg(methodNm);					// Log Method Name
			int mtlSh = Integer.parseInt(ydStkBedStlSh); 	// 야드적치Bed재료매수
			String stlNo = "";								// 재료번호
		
			JDTORecordSet jsChk = JDTORecordFactory.getInstance().createRecordSet("Temp");	
			
			jrParam.setResultCode(logId);							// Log ID
			jrParam.setResultMsg(methodNm);							// Log Method Name

			
// 2025.10.22 설비 적치열 CLEAR
			jrParam.setField("MODIFIER"           	, modifier		); // 수정자							
			jrParam.setField("YD_STK_LYR_ACT_STAT", "E"       		); 	// 야드적치단활성상태
			jrParam.setField("YD_STK_LYR_MTL_STAT", "E"       		); 	// 야드적치단재료상태(적치가능)
			jrParam.setField("YS_STK_COL_GP"      , ysStkColGp   	); // 야드적치열구분
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColGpClear", logId, methodNm, "설비 적치단 CLEAR"); 

// 2025.11.26 적치열 매수 만큼만 트래킹 처리 
			if(8 < mtlSh) {
				mtlSh = 8;
			}
			
			/**********************************************************
			* 2.1 적재단, 저장품 등록
			**********************************************************/
			
			for (int ii = 1; ii <= mtlSh; ii++) {
				stlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	// 재료번호
						
				if ("".equals(stlNo)) {
					sExcptMsg = "재료번호(SSTL_NO" + ii + ") 없음";
					throw new Exception(sExcptMsg);
				}

				jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setResultCode(logId);							// Log ID
				jrParam.setResultMsg(methodNm);							// Log Method Name

				// 2025.10.01 설비 적치열 정보 CLEAR
				jrParam.setField("MODIFIER", 					modifier  		); 	// 수정자							
				jrParam.setField("SSTL_NO", 					stlNo   		); 	// 재료번호
				jrParam.setField("YD_STK_LYR_ACT_STAT"	, 		"E"				); 	// 야드적치단활성상태(적치 가능)
				jrParam.setField("YD_STK_LYR_MTL_STAT"	, 		"E"       		); 	// 야드적치단재료상태(적치가능)

				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyrStlNoClear", logId, methodNm, "저장위치 재료번호 CLEAR "); 
				
				
				jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setResultCode(logId);							// Log ID
				jrParam.setResultMsg(methodNm);							// Log Method Name

				jrParam.setField("YS_STK_COL_GP"      	, ysStkColGp   			); // 야드적치열구분
				jrParam.setField("MODIFIER"           	, modifier  			); // 수정자							
				jrParam.setField("SSTL_NO"       		, stlNo    				); // 재료번호
				jrParam.setField("YS_STK_BED_NO"		, ysStkBedNo 			); // 야드적치Bed번호
				jrParam.setField("YS_STK_LYR_NO"		, ysStkLyrNo  			); // 특수강야드적치단번호
				jrParam.setField("YS_STK_SEQ_NO"		, String.valueOf(ii)	); // 야드적치SEQ
				//CARRY_OUT 시점에 저장픔 YD_RCPT_DATE UPDATE
				jrParam.setField("CARRY_OUT"	    , "Y"       ); 


				jrParam.setField("YD_STK_LYR_MTL_STAT"	, "C"       			); // 야드적치단재료상태(적치중)
				
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyr", logId, methodNm, "야드 적치단 등록"); 
				
				commDao.insert(jrParam, "com.inisteel.cim.ys.bt.dao.BtYsDAO.insBtYdStock", logId, methodNm, "야드 저장품 등록"); 
			}
			
// 2025.10.01 보급대 매수 0이면 스케쥴 체크후 작업 예약 호출
			/**********************************************************
			* 2.2 트래킹 설비가 빌렛정정 보급대(GETF01), PRESS 보급대(GETF02) 이면 보급대에 재료 없으면 작업지시 체크후 작업예약 기동
			*   2.2.1 보급 크레인 작업 지시 있는지 체크 있으면 PASS
			*   2.2.1 보급 크레인 작업 예약 있는지 체크 있으면 기동 없으면 PASS
			**********************************************************/
			if(("GETF01".equals(ydEqpId) || "GETF02".equals(ydEqpId)) && mtlSh == 0) {

				String ydSchCd    = ydEqpId + "LB"; 		// 야드 크레인 스케쥴ID
				
	            szMsg = "보급 스케쥴 코드 : " + ydSchCd;
	      		commUtils.printLog(logId, szMsg, "");

	      		
				jrParam.setField("YD_SCH_CD"	, ydSchCd );
				
				// 빌렛정정 보급대(GETF01), PRESS 보급대(GETF02) YS_크레인스케줄 보급 크레인 작업 지시 조회 
				jsChk  = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchCnt", logId, methodNm, "보급 크레인 작업 지시 건수 조회");
				
				// 보급 크레인 작업 지시 없으면
				if (jsChk == null || jsChk.size() == 0) {
		            szMsg = "보급 스케쥴 코드 [" + ydSchCd + "] 작업 지시 없음.";
		      		commUtils.printLog(logId, szMsg, "");
		      		
					// 빌렛정정 보급대(GETF01), PRESS 보급대(GETF02) YS_작업예약 보급 크레인 작업 지시 조회 
					jsChk  = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnWBCnt", logId, methodNm, "보급 작업예약 건수 조회");
					if (jsChk.size() > 0) {

			            szMsg = "보급 스케쥴 코드 [" + ydSchCd + "] 보급 작업예약 있음.";
			      		commUtils.printLog(logId, szMsg, "");
						
						jsChk  = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnWB", logId, methodNm, "보급 작업예약 조회");
						
						String ydWbookId = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_WBOOK_ID"));
						ydEqpId = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_ID"));
						
						// 작업 예약 스케쥴 기동
						// 크레인스케줄 전문 - Log ID, Method, 수정자 Set
						JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
						
						jrYdMsg.setResultCode(logId);		// Log ID
						jrYdMsg.setResultMsg(methodNm);		// Log Method Name
						
						// 크레인스케줄 기동
						jrYdMsg.setField("JMS_TC_CD"		 , "YSYSJ502"					); 		// 크레인스케줄Main
						jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()  	); 		// JMSTC생성일시
						jrYdMsg.setField("YD_WBOOK_ID"       , ydWbookId 					); 		// 야드작업예약ID
						jrYdMsg.setField("YD_SCH_CD"         , ydSchCd   					); 		// 야드스케쥴코드
						jrYdMsg.setField("YD_EQP_ID"         , ydEqpId  					); 		// 야드설비ID
						
						//작업지시 등 전송할 전문이 있으면 받아서 전송
						jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
						
					} else {
			            szMsg = "보급 스케쥴 코드 [" + ydSchCd + "] 보급 작업예약 없음.";
			      		commUtils.printLog(logId, szMsg, "");
					}
		      		
				} else {
		            szMsg = "보급 스케쥴 코드 [" + ydSchCd + "] 작업 지시 있음.";
		      		commUtils.printLog(logId, szMsg, "");
				}
				
				
			}
			
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch (DAOException e) {
			//if("".equals(sExcptMsg)) {
			//	sExcptMsg = e.getMessage();
			//}
			throw e;
		} catch (Exception e) {
			//if("".equals(sExcptMsg)) {
			//	sExcptMsg = e.getMessage();
			//}
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
	 *      [A] 오퍼레이션명 : 블룸공통 UPDATE
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return void
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public void updBndlCommYsStrLoc(JDTORecord jrParam) throws DAOException {
		String methodNm = "번들공통 UPDATE[CbtYsL2RcvSeEJB.updBndlCommYsStrLoc] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			
			
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBndlCommYsStrLoc", logId, methodNm, "번들공통 UPDATE");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	
		  
	

	
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업가능응답(N7YSL209)
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN7YSL209(JDTORecord rcvMsg) throws DAOException {
        String szMsg	= "";
		String methodNm = "크레인작업가능응답[CbtYsL2RcvSeEJB.rcvN7YSL209] < " + rcvMsg.getResultMsg();
        String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);	
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
	        szMsg = rcvMsg.toString();
			commUtils.printLog(logId, szMsg, "");
			
			
			//수신 항목 값ydEqpId
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId     	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"));     		// 설비ID
			String ydWrkProgStat= commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT"));    // 야드작업진행상태
			String ydSchCd   	= commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"));    		// 야드스케쥴코드
			String ydCrnSchId  	= commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID")); 		// 야드크레인스케쥴ID
			String reqYn  		= commUtils.trim(rcvMsg.getFieldString("REQ_YN")); 				// 유무응답
			String ReqMsg  		= commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_REQ_MSG")); // 메시지
			String sModifier    = commUtils.trim(rcvMsg.getFieldString("MODIFIER"     )); 		// 수정자(Backup Only)
			String headMsgGp    = commUtils.trim(rcvMsg.getFieldString("MSG_GP"     )); 		// head msg구분
			 
			if ("".equals(sModifier)) { sModifier = msgId; }
			
			methodNm = msgId.substring(0, 2) + methodNm;			
			
            szMsg = "\n\t YD_EQP_ID   			: " 	+ ydEqpId 
            	  + "\n\t YD_WRK_PROG_STAT  	: " 	+ ydWrkProgStat 
            	  + "\n\t YD_SCH_CD         	: " 	+ ydSchCd 
            	  + "\n\t YD_CRN_SCH_ID     	: " 	+ ydCrnSchId 
            	  + "\n\t REQ_YN     			: " 	+ reqYn 
            	  + "\n\t YD_WRK_PROG_REQ_MSG   : " 	+ ReqMsg;

      		commUtils.printLog(logId, szMsg, "");
			
			
			String ydWbookId 		= "";
			String ydWrkProgStst	= "";	
			String ydUpWoLoc        = "";
			String ydDnWoLoc		= "";	
			String ydSchPrior       = ""; //스케줄 우선순위
			String autoYn           = "N";
			
			
				
			if (ydEqpId.length() < 6) {
				commUtils.printLog(logId, ydEqpId + "설비ID 이상.", "");
				throw new Exception("설비ID 이상 [" + ydEqpId + "]");
			}
			/**********************************************************
			* 1. 수신 항목 값 Check
			*   무인 크레인이 이면 
			**********************************************************/
			autoYn = "N";

			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, sModifier);
			
			jrParam.setField("YD_EQP_ID"         		, ydEqpId);  					
			jrParam.setField("YD_CRN_SCH_ID"      		, ydCrnSchId);  					
			jrParam.setField("YD_WRK_PROG_STAT"    		, ydWrkProgStat);  					
			jrParam.setField("YD_WRK_PROG_REQ_MSG"   	, ReqMsg);  					

			commUtils.printLog(logId, "크레인 CHECK : [" + autoYn + "]", "");	
			
			JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYsCbtCrnSch", logId, methodNm, "크레인스케줄 조회");
			if (jsCrnSch.size() == 0) {
				commUtils.printLog(logId, "크레인 스케쥴 번호가 없습니다..! [" + ydCrnSchId + "]", "");
				throw new Exception("크레인 스케쥴 번호가 없습니다..! [" + ydCrnSchId + "]");
			} else {
				ydCrnSchId 		= jsCrnSch.getRecord(0).getFieldString("YD_CRN_SCH_ID");   
				ydWbookId 		= jsCrnSch.getRecord(0).getFieldString("YD_WBOOK_ID");     
				ydSchCd 		= jsCrnSch.getRecord(0).getFieldString("YD_SCH_CD");       
				ydWrkProgStst	= jsCrnSch.getRecord(0).getFieldString("YD_WRK_PROG_STAT");
				ydUpWoLoc	    = jsCrnSch.getRecord(0).getFieldString("YS_UP_WO_LOC");    //권상위치
				ydDnWoLoc	    = jsCrnSch.getRecord(0).getFieldString("YS_DN_WO_LOC");    //권하위치
				ydSchPrior      = jsCrnSch.getRecord(0).getFieldString("YD_SCH_PRIOR");    //스케줄우선순위
			} 
			
			
			

			if ("Y".equals(reqYn)){
				/**********************************************************
				* 2.가능인 경우
				**********************************************************/						
				
				
				commUtils.printLog(logId, methodNm + "가능인 경우", "");
				
			    commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYsCrnSchProgStat", logId, methodNm,  "크레인 스케줄 작업진행상태 변경");
				
			
			} else if ("N".equals(reqYn)){
				/**********************************************************
				* 2.불가인 경우 (L2 작업 불가인 경우 L2에서는 크레인 작업지시 CLEAR 하기 때문에 크레인 작업지시 취소 불필요)
				*    - TB_YS_CRNSCH.YD_WRK_PROG_STAT = 'W'
				**********************************************************/						
				
				commUtils.printLog(logId, methodNm + "불가 일경우", "");

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
	 *      [A] 오퍼레이션명 : L2야드적치현황정보(N7YSL210)
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN7YSL210(JDTORecord rcvMsg) throws DAOException {

        String szMsg	= "";
		String methodNm = "L2야드적치현황정보[CbtYsL2RcvSeEJB.rcvN7YSL210] < " + rcvMsg.getResultMsg();
        String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);	
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
	        szMsg = rcvMsg.toString();
			commUtils.printLog(logId, szMsg, "");
			
			
			//수신 항목 값ydEqpId
			String msgId        = commUtils.getMsgId(rcvMsg); 									// EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sInfReqNo   	= commUtils.trim(rcvMsg.getFieldString("INF_REQ_NO"		));		// 정보요구번호
			String sT0talCnt    = commUtils.trim(rcvMsg.getFieldString("T0TAL_CNT"		));		// 야드적치현황정보송신TOTAL
			String sCurrCnt     = commUtils.trim(rcvMsg.getFieldString("CURR_CNT"		));		// 야드적치현황정보현재순번
			String sModifier    = "N7YSL210"; 													// 수정자
			
			
            szMsg = "\n\t INF_REQ_NO   	: " 	+ sInfReqNo 
            	  + "\n\t T0TAL_CNT  	: " 	+ sT0talCnt 
            	  + "\n\t CURR_CNT      : " 	+ sCurrCnt;

      		commUtils.printLog(logId, szMsg, "");
			
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
      		
			
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, sModifier);
			
			jrParam.setField("INF_REQ_NO"         		, sInfReqNo);		// 정보요구번호
			
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.getStrLocMtlInfoDmd", logId, methodNm, "정보요구번호 조회");
			if (jsChk == null || jsChk.size() == 0) {
	            szMsg = "수신된 정보요구번호가 없습니다..! [" + sInfReqNo + "]";
	            commUtils.printLog(logId, szMsg, "");
	            
				throw new Exception(szMsg);
			} 

			
			String sstlNo 	= "";
			String sLodLoc 	= "";
			
			// 최대 40 까지 위치,재료번호
			for (int ii = 1; ii <= 40; ii++) {
				
				sLodLoc = commUtils.trim(rcvMsg.getFieldString("LOD_LOC" + ii));	// 적재위치
				sstlNo 	= commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	// 재료번호
				
				jrParam.setField("LOD_LOC"	, 	sLodLoc );
				jrParam.setField("SSTL_NO_L2", 	sstlNo );
				
				
				if ("".equals(sLodLoc)) {
		            szMsg = "수신된 적재위치 없습니다..! 정보요구번호 [" + sInfReqNo + "] 재료번호 [" + sstlNo + "]";
		            commUtils.printLog(logId, szMsg, "");
		            
// 현재 이후 더이상 없으면 break 있으면 계속 loop
//		            continue;
		            break;
				}

//  L2재료정보 UPDATE
				jrParam.setField("INF_REQ_NO"         		, sInfReqNo);		// 정보요구번호
				
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.updStrLocMtlInfoCnfmRs", logId, methodNm, "L2야드적치현황정보 L2재료정보 등록"); 
				
				
			}
			
// T0TAL_CNT = CURR_CNT 집계 
			if(sT0talCnt.equals(sCurrCnt)){

// TB_YS_STRLOC_MTLINFOCNFMRS(저장위치재료정보확인결과) 테이블 일치/불일치 UPDATE
				jrParam.setField("INF_REQ_NO"         		, sInfReqNo);		// 정보요구번호
				
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
	 *      [A] 오퍼레이션명 : 이송지시 INSERT
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return void
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public void insPbStlFrToMove(JDTORecord jrParam) throws DAOException {
		String methodNm = "이송지시 INSERT[CbtYsL2RcvSeEJBBean.insPbStlFrToMove] < " + jrParam.getResultMsg();
        String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);	

		try {
			
			// 이송지시 테이블 INSERT
			commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.insPbStlFrToMove", logId, methodNm, "이송지시 등록");
			
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
		String methodNm = "저장위치별 재료정보 요구 INSERT[CbtYsL2RcvSeEJBBean.insStrLocMtlIn] < ";
        String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);	

        commUtils.printLog(logId, methodNm, "S+");
        
        szMsg = rcvMsg.toString();
		commUtils.printLog(logId, szMsg, "");

		try {
			
			String ysInfReqNo 	= commUtils.trim(rcvMsg.getFieldString("INF_REQ_NO"		)); // 정보요구번호
			String ysLodLoc  	= commUtils.trim(rcvMsg.getFieldString("LOD_LOC"        )); // 요청 적치열
			
            szMsg = "\n\t INF_REQ_NO   : " 	+ ysInfReqNo 
                  + "\n\t LOD_LOC      : " 	+ ysLodLoc;

      		commUtils.printLog(logId, szMsg, "");
			
			// 저장위치재료정보요구 테이블 INSERT
			commDao.insert(rcvMsg, "com.inisteel.cim.ys.common.dao.insStrLocMtlInfoDmd", logId, methodNm, "저장위치별 재료정보 요구 등록");
			
			// 저장위치재료정보확인결과 테이블 L3 정보 INSERT
			commDao.insert(rcvMsg, "com.inisteel.cim.ys.common.dao.insStrLocMtlInfoCnfmRs", logId, methodNm, "저장위치재료정보확인결과 등록");
			
			commUtils.printLog(logId, methodNm, "S-");

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
			
			jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN7L210", jrParam));

	        szMsg = "야드적치현황정보요구(YSN7L210) 전문 생성 ****** " + jrRtn.toString();
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
	 *      [A] 오퍼레이션명 : L2야드좌표정보(N7YSL211)
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN7YSL211(JDTORecord rcvMsg) throws DAOException {


        String szMsg	= "";
		String methodNm = "L2야드좌표정보[CbtYsL2RcvSeEJB.rcvN7YSL211] < " + rcvMsg.getResultMsg();
        String logId = commUtils.getLogId(YsConstant.YD_GP_G, YsConstant.YD_BAY_GP_E);	
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
	        szMsg = rcvMsg.toString();
			commUtils.printLog(logId, szMsg, "");
			
			
			//수신 항목 값ydEqpId
			String msgId     		= commUtils.getMsgId(rcvMsg); 										// EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String srYsStkColGp   	= commUtils.trim(rcvMsg.getFieldString("YS_STK_COL_GP"		));		// 적치열
			String srYsStkBedNo   	= commUtils.trim(rcvMsg.getFieldString("YS_STK_BED_NO"		));		// 베드
			String srYsStkLyrCls  	= commUtils.trim(rcvMsg.getFieldString("YS_STK_LYR_CLS"		));		// 단구분 (1 : 홀수단, 2 : 짝수단)
			String srYdXaxis		= commUtils.trim(rcvMsg.getFieldString("YD_XAXIS"    		)); 	// 야드X축
			String srYdYaxis   		= commUtils.trim(rcvMsg.getFieldString("YD_YAXIS"    		)); 	// 야드Y축
			String sModifier    	= "N7YSL211"; 														// 수정자
			
			
            szMsg = "\n\t YS_STK_COL_GP   	: " 	+ srYsStkColGp 
            	  + "\n\t YS_STK_BED_NO  	: " 	+ srYsStkBedNo 
            	  + "\n\t YS_STK_LYR_CLS    : " 	+ srYsStkLyrCls
            	  + "\n\t YD_XAXIS      	: " 	+ srYdXaxis
            	  + "\n\t YD_YAXIS      	: " 	+ srYdYaxis
            	  ;

      		commUtils.printLog(logId, szMsg, "");
			
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
      		
      		if (!srYsStkColGp.startsWith("GE")) {
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
	
}


